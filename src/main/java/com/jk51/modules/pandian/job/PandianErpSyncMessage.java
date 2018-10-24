package com.jk51.modules.pandian.job;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jk51.communal.redis.RedisUtil;
import com.jk51.model.BPandianFile;
import com.jk51.model.Inventories;
import com.jk51.model.erp.PanDianHead;
import com.jk51.model.erp.PanDianItem;
import com.jk51.modules.marketing.service.MarketingService;
import com.jk51.modules.offline.service.OfflineCheckService;
import com.jk51.modules.pandian.elasticsearch.service.InventoriesESMenager;
import com.jk51.modules.pandian.mapper.BInventoriesMapper;
import com.jk51.modules.pandian.mapper.BPandianFileMapper;
import com.jk51.modules.pandian.mapper.BPandianOrderStatusMapper;
import com.jk51.modules.pandian.param.PDInsertMessage;
import com.jk51.modules.pandian.param.PDMessage;
import com.jk51.modules.pandian.param.StoreOrderStatus;
import com.jk51.mq.mns.CloudQueueFactory;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RunMsgWorker(queueName = PandianErpSyncMessage.QUEUE_NAME)
public class PandianErpSyncMessage implements MessageWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(PandianErpSyncMessage.class);

    public static final String QUEUE_NAME = "PandianErpSyncQueueForEs";
    public static final String DIRECTORY_NAME = "pd-message";
    public static final long EXPIRE_TIMEOUT = 7L;
    public static final TimeUnit EXPIRE_TIMEUNIT = TimeUnit.DAYS;

    private static final ExecutorService executorService;
    static {
        executorService = Executors.newFixedThreadPool(10, new ThreadFactoryBuilder()
            .setNameFormat("PD-exec-%d")
            .setDaemon(true)
            .build());
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MarketingService marketingService;
    @Autowired
    private BPandianOrderStatusMapper bPandianOrderStatusMapper;
    @Autowired
    private OfflineCheckService offlineCheckService;
    @Autowired
    private BInventoriesMapper bInventoriesMapper;
    @Autowired
    private BPandianFileMapper bPandianFileMapper;
    @Autowired
    private InventoriesESMenager inventoriesESMenager;


    @Override
    public void consume(Message message) throws Exception {
        MessageBody messageBody = null;
        try {
            messageBody = JSONObject.parseObject(message.getMessageBody(), MessageBody.class);
            Objects.requireNonNull(messageBody, "无效的messageBody。");
        } catch (Exception e) {
            LOGGER.error("消息 {} 错误的消息内容 {} {} ", message, e.getMessage(), e);
            return;
        }

        JSONObject data = (JSONObject) messageBody.getData();
        switch (messageBody.getType()) {
            case PDMessage.MESSAGE_TYPE :
                sendPDMessage(data.toJavaObject(PDMessage.class));
                break;
            case PDInsertMessage.MESSAGE_TYPE :
                CloudQueue cloudQueue = getCloudQueue(QUEUE_NAME);
                message.setReceiptHandle(cloudQueue.changeMessageVisibilityTimeout(message.getReceiptHandle(), 60 * 5));
                processPDInsertMessage(data.toJavaObject(PDInsertMessage.class));
                break;
            default:
                LOGGER.error("未被匹配的消息 {} ", message);
        }

    }


    public void sendPDMessage(PDMessage pdMessage) throws Exception {
        String flag = pdMessage.getSiteId() + "-" + pdMessage.getType() + "-" + pdMessage.getOrderNum();
        if (pdMessage.getInsertType() != 0) flag += "-" + System.currentTimeMillis();
        //File directory = getDirectoryFile(DIRECTORY_NAME + "/" + flag);
        String countKey = DIRECTORY_NAME + "-" + flag;

        List<StoreOrderStatus> storeStatusList = bPandianOrderStatusMapper.getPDAllStoreStatus(pdMessage.getSiteId(), pdMessage.getPlanId(), pdMessage.getOrderId());
        if (CollectionUtils.isNotEmpty(pdMessage.getStoreIdList()) && CollectionUtils.isNotEmpty(storeStatusList))
            storeStatusList = storeStatusList.stream().filter(o -> pdMessage.getStoreIdList().contains(o.getStoreId())).collect(Collectors.toList());

        PanDianHead panDianHead = null;
        try {
            LOGGER.info("sendPDMessage-getPanDianStatusFromErp，siteId {}， pandianNO {} ", pdMessage.getSiteId(), pdMessage.getOrderNum());
            panDianHead = offlineCheckService.getPanDianStatusFromErp(pdMessage.getSiteId(), pdMessage.getOrderNum());
        } catch (Exception e) {
            LOGGER.error("PanDianStatusFromErp异常 {} ", e);
            throw e;
        }

        bPandianFileMapper.insertSelective(new BPandianFile(){{
            setSiteId(pdMessage.getSiteId());
            setStoreId(0);
            setType(pdMessage.getType());
            setOrderId(pdMessage.getOrderId());
            setPandianNum(pdMessage.getOrderNum());
            setFileUrl(countKey);
        }});

        //File file = new File(directory, pdMessage.getOrderNum() + "-" + "ErpInfo.txt");
        Map<String, Object> logMap = new LinkedHashMap<>();
        logMap.put("pdMessage", pdMessage);
        logMap.put("orderErpInfo", panDianHead);
        if (CollectionUtils.isEmpty(storeStatusList)) {
            logMap.put("pdError", "无门店盘点单");
        } else if (panDianHead == null || CollectionUtils.isEmpty(panDianHead.getStoreOrderNums())) {
            logMap.put("pdError", "无ERP门店盘点单");
        } else {
            List<Integer> storeIdList = new ArrayList<>();
            List<PDInsertMessage> pdInsertMessageList = new ArrayList<>();
            Map<String, String> erpStoreOrderNumMap = panDianHead.getStoreOrderNums().stream().collect(Collectors.toMap(o -> o.getUnit_no(), o -> o.getBillid(), (v1, v2) -> v2));
            Map<String, String> errorMap = new HashMap<>();
            for (StoreOrderStatus storeOrderStatus : storeStatusList) {
                if (storeOrderStatus.getStatus() == null) {
                    errorMap.put(storeOrderStatus.getStoresNumber(), "门店无盘点单");
                } else if (storeOrderStatus.getStatus()!=0 && storeOrderStatus.getStatus()!=100) {
                    errorMap.put(storeOrderStatus.getStoresNumber(), "门店数据禁止上传");
                } else if (!erpStoreOrderNumMap.containsKey(storeOrderStatus.getStoresNumber())) {
                    errorMap.put(storeOrderStatus.getStoresNumber(), "门店无ERP数据");
                } else {
                    PDInsertMessage pdInsertMessage = new PDInsertMessage(){{
                        setSiteId(pdMessage.getSiteId());
                        setPlanId(pdMessage.getPlanId());
                        setOrderId(pdMessage.getOrderId());
                        setStoreId(storeOrderStatus.getStoreId());
                        setStoreNum(storeOrderStatus.getStoresNumber());
                        setOrderNum(pdMessage.getOrderNum());
                        setErpStoreTaskNum(erpStoreOrderNumMap.get(storeOrderStatus.getStoresNumber()));
                        setDirectoryPath(countKey);
                    }};
                     /*try {
                            incr(countKey);
                        } catch (Exception e) {
                            LOGGER.error("incr异常 {} ", e);
                        }*/
                    storeIdList.add(storeOrderStatus.getStoreId());
                    pdInsertMessageList.add(pdInsertMessage);
                }
            }
            if (CollectionUtils.isNotEmpty(storeIdList)) {
                bPandianOrderStatusMapper.updateStatusByStoreIdList(pdMessage.getSiteId(), pdMessage.getOrderId(), storeIdList, 1);
                pdInsertMessageList.stream().forEach(o -> {//按门店进行数据同步，防止消息消费太快
                    try {
                        sendMessage(PDInsertMessage.MESSAGE_TYPE, o);
                    } catch (Exception e) {
                        LOGGER.error("门店导入通知异常 {} ", e);
                        errorMap.put(o.getStoreNum(), "门店导入通知异常");
                        marketingService.addLog(pdMessage.getSiteId(), 96, 0, "MessageProduce", JSONObject.toJSONString(o), "盘点门店导入通知异常");
                    }
                });
            }
            logMap.put("errorMap", errorMap);
        }

        String result = null;
        try {
            result = JSONObject.toJSONString(logMap);
            //FileUtils.writeStringToFile(file, result);
            RedisUtil.setAdd(countKey, result);
            RedisUtil.setExpire(countKey, EXPIRE_TIMEOUT, EXPIRE_TIMEUNIT);
        } catch (Exception e) {
            LOGGER.error("PDMessage {} 写入文件异常 {} ", result, e);
        }
    }


    public void processPDInsertMessage(PDInsertMessage insertMessage) throws Exception {
        //File directory = getDirectoryFile(insertMessage.getDirectoryPath());
        StoreOrderStatus storeOrderStatus = bPandianOrderStatusMapper.getPDStoreStatus(insertMessage.getSiteId(), insertMessage.getPlanId(), insertMessage.getOrderId(), insertMessage.getStoreId());

        //File file = new File(directory, insertMessage.getStoreId() + "-" + insertMessage.getStoreNum() + "-" + "insertInfo.txt");
        Map<String, Object> logMap = new LinkedHashMap<>();
        logMap.put("insertMessage", insertMessage);
        if (storeOrderStatus == null) {
            logMap.put(storeOrderStatus.getStoresNumber(), "门店无盘点单");
        } else if (storeOrderStatus.getStatus() != 1) {
            logMap.put(storeOrderStatus.getStoresNumber(), "门店数据禁止上传");
        } else {
            List<PanDianItem> goodsList = null;
            try {
                goodsList = offlineCheckService.getPanDianDetail(insertMessage.getSiteId(), insertMessage.getErpStoreTaskNum(),insertMessage.getOrderNum());
            } catch (Exception e) {
                LOGGER.error("getPanDianDetail异常 {}", e);
                throw e;
            }
            if (CollectionUtils.isNotEmpty(goodsList)) {
                List<Inventories> inventoriesList = goodsList.stream()
                    .map(o -> new Inventories(){{
                        setSite_id(insertMessage.getSiteId());
                        setOrder_id(insertMessage.getOrderId());
                        setPandian_num(insertMessage.getOrderNum());
                        setPlan_id(insertMessage.getPlanId());
                        setStore_id(insertMessage.getStoreId());
                        setStore_num(insertMessage.getStoreNum());
                        setGoods_code(o.getGoodsno());
                        setBatch_number(o.getBatchno());
                        setDrug_name(o.getMname());
                        setSpecif_cation(o.getSpec());
                        setGoods_company(o.getProductor());
                        setInventory_accounting(o.getAckquty());
                        setErpDataSeq(o.getItemid());
                        setCreate_time(new Date());
                        setUpdate_time(new Date());
                    }})
//                    .limit(1)
                    .collect(Collectors.toList());
                goodsList = null;
                List<List<Inventories>> partList = Lists.partition(inventoriesList, 2000);
                inventoriesList = null;
                try {
                    //System.gc();
                    AtomicInteger count = new AtomicInteger(0);
                    List<CompletableFuture<Map<String, Object>>> completableFutures =  partList.stream()
                        .map(o -> {
                            int index = count.incrementAndGet();
                            return CompletableFuture.supplyAsync(() -> {
                                Map<String, Object> errorMap = new HashMap<>();
                                try {
                                    inventoriesESMenager.insertInventory(o);
                                } catch (Exception e) {
                                    LOGGER.error("InventoriesInsertByList异常 {} ", e);
                                    errorMap.put(Integer.toString(index), "添加异常first：" + o.get(0).getGoods_code() + " - " + o.get(0).getErpDataSeq());
                                }
                                return errorMap;
                            }, executorService);
                        })
                        .collect(Collectors.toList());
                    partList = null;
                    Map<String, Object> errorMap = completableFutures.stream().map(CompletableFuture::join).collect(HashMap::new, HashMap::putAll, HashMap::putAll);
                    logMap.put("errorMap", errorMap);
                } catch (Exception e) {
                    LOGGER.error("completableFutures 异常 {}", e);
                }
            } else {
                logMap.put("insertError", "门店erp无数据");
            }
            try {
                bPandianOrderStatusMapper.updateBillidStatusByStoreId(insertMessage.getSiteId(), insertMessage.getOrderId(), insertMessage.getStoreId(), 100, insertMessage.getErpStoreTaskNum());
            } catch (Exception e) {
                LOGGER.error("门店状态更新异常 {} ", e);
                logMap.put("insertError", "门店状态更新异常");
            }
        }

        String result = null;
        try {
            result = JSONObject.toJSONString(logMap);
            //FileUtils.writeStringToFile(file, result);
            RedisUtil.setAdd(insertMessage.getDirectoryPath(), result);
            RedisUtil.setExpire(insertMessage.getDirectoryPath(), EXPIRE_TIMEOUT, EXPIRE_TIMEUNIT);
        } catch (Exception e) {
            LOGGER.error("PDInsertMessage {} 写入文件异常 {} ", result, e);
        }
    }









    private long incr(String key) throws Exception {
        return stringRedisTemplate.opsForValue().increment(key, 1);
    }

    private long decr(String key) throws Exception {
        return stringRedisTemplate.opsForValue().increment(key, -1);
    }

    private File getDirectoryFile(String directoryName) {
        directoryName = System.getProperty("java.io.tmpdir") + "/" + directoryName;
        File directory = new File(directoryName);
        if (!directory.exists()) directory.mkdirs();
        return directory;
    }


    @Autowired
    CloudAccount account;
    /**
     * 在系统设计上保证您队列中的所有消息至少被消费一次,建议您加强应用服务的容错性，以便多次处理同一消息时不会造成错误或不一致。
     * 如果消息超过不可见时间仍未完成消费，则需要发送不可见时间段的延长请求（ChangeVisibilityTimeout）；否则消息将会在不可见时间段结束后重新被其他消费者取出。
     * MNS中的消息保留期限是可配置的，您可以设置 MessageRetentionPeriod 为1分钟到15天之间的任何值。默认值为4天；一旦达到消息保留期限，您的消息会被自动删除。
     * CloudQueue cloudQueue = getCloudQueue(QUEUE_NAME);
     * cloudQueue.changeMessageVisibility(message.getReceiptHandle(), 60 * 30);//这个时候ReceiptHandle值以改变，用messag.getReceiptHandle去删除该消息会报错。
     */
    private CloudQueue getCloudQueue(String name) {
        String queueName = CloudQueueFactory.create(name).getAttributes().getQueueName();
        return account.getMNSClient().getQueueRef(queueName);
    }


    public void sendMessage(int messageType, Object data) throws Exception {
        sendMessage(new PandianErpSyncMessage.MessageBody() {{
            setType(messageType);
            setData(data);
        }});
    }

    public void sendMessage(MessageBody messageBody) throws Exception {
        if (messageBody.getType() == null || messageBody.getData() == null) throw new RuntimeException("MessageBody - NullPointerException");
        CloudQueue queue = CloudQueueFactory.create(QUEUE_NAME);
        queue.putMessage(new Message(JSONObject.toJSONString(messageBody)));
    }

    public static class MessageBody {
        private Integer type;

        private Object data;

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "MessageBody{" +
                "type=" + type +
                ", data=" + data +
                '}';
        }
    }

    public static class Constant {
        public static final int PD_MESSAGE = 0;
        public static final int PDINSERT_MESSAGE = 1;
    }
}
