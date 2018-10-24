package com.jk51.modules.im.service;

import com.alibaba.fastjson.JSON;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.IQueryResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.communal.redis.RedisUtil;
import com.jk51.model.BMessage;
import com.jk51.model.BMessageReceived;
import com.jk51.model.BMessageSetting;
import com.jk51.modules.appInterface.mapper.BMessageMapper;
import com.jk51.modules.appInterface.mapper.BMessageReceivedMapper;
import com.jk51.modules.appInterface.mapper.BMessageSenderMapper;
import com.jk51.modules.appInterface.mapper.BMessageSettingMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jk51.modules.im.util.PushConstant.*;

@Service
public class PushServeService {
    private static Logger logger = LoggerFactory.getLogger(PushServeService.class);
    @Resource
    @Qualifier("storeHelpPush")
    private PushServe storeHelpPush;
    @Resource
    @Qualifier("storeXiaoWuPush")
    private PushServe storeXiaoWuPush;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BMessageMapper bMessageMapper;
    @Autowired
    private BMessageReceivedMapper bMessageReceivedMapper;
    @Autowired
    private BMessageSettingMapper bMessageSettingMapper;
    @Autowired
    private BMessageSenderMapper bMessageSenderMapper;

    /**
     * 单用户推送消息 ------ 严格来说是 推给某个设备
     * 个推使用clientid来标识每个独立的用户，每一台终端上每一个app拥有一个独立的clientid。
     *
     *若两个账号在同一个设备登录过，有可能两个账号对应同一个clientID，客户端应根据拿到的具体信息 判断显示与否：
     *      新订单提醒，发货提醒。。。
     */
    public void pushMessageToSingle(InitialMessage initialMessage) throws Exception {
        String clientId = getPushClientId(initialMessage.getSiteId(), initialMessage.getStoreAdminId());//获取推送ID
        if(clientId == null){
            return;
        }

        BMessage bMessage = getProcessPushMessage(initialMessage);//根据设置生成推送信息格式
        bMessage.setPushIdList(JSON.toJSONString(Arrays.asList(initialMessage.getStoreAdminId())));//为了客户端好统一判断clientID对应关系

        send(clientId, initialMessage, bMessage, false);

        try {
            bMessageMapper.insertSelective(bMessage);//记录该消息
            BMessageReceived bMessageReceived = new BMessageReceived();
            bMessageReceived.setMessageType(bMessage.getMessageType());
            bMessageReceived.setbMessageId(bMessage.getId());
            bMessageReceived.setSiteId(bMessage.getSiteId());
            bMessageReceived.setStoreId(bMessage.getStoreId());
            bMessageReceived.setStoreAdminId(bMessage.getStoreAdminId());
            bMessageReceivedMapper.insertSelective(bMessageReceived);
        } catch (Exception e) {
            logger.error("pushMessageToSingle 保存异常 {}，{}", e.getMessage(), e);
        }
    }

    public void send(String clientId, InitialMessage initialMessage, BMessage bMessage, boolean force) {
        int count = 0;
        try {
            sendSingle(clientId, storeHelpPush, initialMessage, bMessage, force);
        } catch (Exception e) {
            logger.error("storeHelpPush sendSingle异常 appid：{}，{}，{}", storeHelpPush.getAppId(), e.getMessage(), e);
            count++;
        }
        try {
            sendSingle(clientId, storeXiaoWuPush, initialMessage, bMessage, force);
        } catch (Exception e) {
            logger.error("storeXiaoWuPush sendSingle异常 appid：{}，{}，{}", storeXiaoWuPush.getAppId(), e.getMessage(), e);
            count++;
        }
        if (count == 2) throw new RuntimeException("pushMessageToSingle 异常");
    }

    public void sendSingle(String clientId, PushServe pushServe, InitialMessage initialMessage, BMessage bMessage, boolean force) {
        TransmissionTemplate template = getTransmissionTemplateAPNS(bMessage.getNotificationTitle(),
            bMessage.getNotificationText(),
            JSON.toJSONString(bMessage),
            pushServe.getAppId(),
            pushServe.getAppKey(),
            initialMessage.getBadgeNum(),
            bMessage.getSound());//设置透传模板
        SingleMessage message = getSingleMessage(template, bMessage.getOffLine(), bMessage.getWifi());//设置单推消息的消息体

        boolean flag = true;
        try {
            if(getClientIdIsOnline(initialMessage.getSiteId(), initialMessage.getStoreAdminId(), null) == null){//cid 登录时存，，这里以mysql中的为标准
                flag = false;
            }
        } catch (Exception e) {
            logger.error("getClientIdIsOnline 报错,siteId:{},id:{}报错信息：{}",initialMessage.getSiteId(),initialMessage.getStoreAdminId(),ExceptionUtil.exceptionDetail(e));
        }

        if(flag || force) {//登录才推送
            Target target = new Target();
            target.setAppId(pushServe.getAppId());
            target.setClientId(clientId);
            //target.setAlias(Alias);
            IPushResult ret = null;
            try {
                ret = pushServe.pushMessageToSingle(message, target);
            } catch (RequestException e) {
                logger.error("pushMessageToSingle 失败，appid：{}，clientId：{}，失败信息:{}",
                    target.getAppId(), target.getClientId(), ExceptionUtil.exceptionDetail(e));
                ret = pushServe.pushMessageToSingle(message, target, e.getRequestId());
            }
            if (ret != null) {
                bMessage.setStatus(1);
                logger.info("pushMessageToSingle 成功，appid：{}，clientId：{}，返回信息:{}",
                    target.getAppId(), target.getClientId(), ret.getResponse().toString());
            } else {
                logger.error("pushMessageToSingle 服务器响应异常");
            }
        }
    }





    /**
     * 对指定用户列表推送消息
     */
    public void pushMessageToList(InitialMessage initialMessage, List<String> storeAdminIds) throws Exception {
        boolean save = true;
        int count = 0;
        try {
            if (sendList(storeHelpPush, initialMessage, storeAdminIds, save) == 1)
                save = false;
        } catch (Exception e) {
            logger.error("storeHelpPush sendList异常 appid：{}，{}，{}", storeHelpPush.getAppId(), e.getMessage(), e);
            count++;
        }
        try {
            sendList(storeXiaoWuPush, initialMessage, storeAdminIds, save);
        } catch (Exception e) {
            logger.error("storeXiaoWuPush sendList异常 appid：{}，{}，{}", storeXiaoWuPush.getAppId(), e.getMessage(), e);
            count++;
        }
        if (count == 2) throw new RuntimeException("pushMessageToList 异常");
    }


    public int sendList(PushServe pushServe, InitialMessage initialMessage, List<String> storeAdminIds, boolean save) {
        int result = -1;
        Map<String, Object> targetListMap = getTargetListMap(initialMessage, storeAdminIds, pushServe.getAppId());//获取推送目标列表
        if(targetListMap == null){
            return result;
        }

        List<Target> targets = (List<Target>) targetListMap.get("targets");
        List<String> storeAdminIdList = (List<String>) targetListMap.get("storeAdminIds");

        BMessage bMessage = getProcessPushMessage(initialMessage);//根据设置生成推送信息格式
        bMessage.setPushIdList(JSON.toJSONString(storeAdminIdList));

        TransmissionTemplate template = getTransmissionTemplateAPNS(bMessage.getNotificationTitle(),
            bMessage.getNotificationText(),
            JSON.toJSONString(bMessage),
            pushServe.getAppId(),
            pushServe.getAppKey(),
            initialMessage.getBadgeNum(),
            bMessage.getSound());//设置透传模板
        ListMessage message = getListMessage(template, bMessage.getOffLine(), bMessage.getWifi());//设置列表推送消息的消息体

        String taskId = pushServe.getContentId(message);// taskId用于在推送时去查找对应的message
        IPushResult ret = null;//发送消息
        if(targets.size() != 0){
            try {
                ret = pushServe.pushMessageToList(taskId, targets);
            } catch (RequestException e) {
                logger.error("sendList 异常，appid：{}，taskId：{}，{}", pushServe.getAppId(), taskId, e);
                throw e;
            }
            if (ret != null) {
                bMessage.setStatus(1);
                logger.info("sendList 成功，appid：{}，taskId：{}，推送结果：{}", pushServe.getAppId(), taskId, ret.getResponse());
            } else {
                logger.error("个推服务响应异常");
            }
            result = 1;
        }
        logger.info("targets ：{}，storeAdminIdList：{}",targets,storeAdminIdList);

        if(save) {
            try {
                bMessageMapper.insertSelective(bMessage);//记录该消息
                List<BMessageReceived> messageReceivedList = new ArrayList<>();
                for(String id : storeAdminIdList){
                    BMessageReceived bMessageReceived = new BMessageReceived();
                    bMessageReceived.setMessageType(bMessage.getMessageType());
                    bMessageReceived.setbMessageId(bMessage.getId());
                    bMessageReceived.setSiteId(bMessage.getSiteId());
                    bMessageReceived.setStoreId(bMessage.getStoreId());
                    bMessageReceived.setStoreAdminId(Integer.parseInt(id));
                    messageReceivedList.add(bMessageReceived);
                }
                bMessageReceivedMapper.insertByList(messageReceivedList);
            } catch (Exception e) {
                logger.error("sendList 保存异常 {}，{}", e.getMessage(), e);
            }
        }

        return result;
    }


    /**
     *登录通知其它client退出
     */
    public void pushMessageToSingleClientId(InitialMessage initialMessage, String clientId) throws Exception {
        if(StringUtil.isEmpty(clientId)){
            return;
        }

        BMessage bMessage = getProcessPushMessage(initialMessage);//根据设置生成推送信息格式
        bMessage.setPushIdList(JSON.toJSONString(Arrays.asList(initialMessage.getStoreAdminId())));//为了客户端好统一判断clientID对应关系

        send(clientId, initialMessage, bMessage, true);

        try {
            bMessageMapper.insertSelective(bMessage);//记录该消息
            if(bMessage.getStatus() == 1){
                BMessageReceived bMessageReceived = new BMessageReceived();
                bMessageReceived.setMessageType(bMessage.getMessageType());
                bMessageReceived.setbMessageId(bMessage.getId());
                bMessageReceived.setSiteId(bMessage.getSiteId());
                bMessageReceived.setStoreId(bMessage.getStoreId());
                bMessageReceived.setStoreAdminId(bMessage.getStoreAdminId());
                bMessageReceivedMapper.insertSelective(bMessageReceived);
            }
        } catch (Exception e) {
            logger.error("pushMessageToSingleClientId 保存异常 {}，{}", e.getMessage(), e);
        }
    }


    /**
     *查看用户 是否在线登录app
     * @return
     */
    public Set<String> getClientIdIsOnline(String siteId, String storeAdminId, String clientId) throws Exception{
        //Set<String> clientIdKeys = stringRedisTemplate.keys("notifyId_" + siteId + "_" + storeAdminId + "_" + "*");
        //Set<String> clientIdKeys = RedisUtil.scan("notifyId_" + siteId + "_" + storeAdminId + "_" + "*");
        Set<String> clientIdKeys = RedisUtil.setGetAllValue("notifyId_" + siteId + "_" + storeAdminId);
        if(clientIdKeys==null || clientIdKeys.size()==0){
            clientIdKeys = null;
        }
        return clientIdKeys;
    }


    /**
     * 对消息进行处理
     */
    public BMessage getProcessPushMessage(InitialMessage initialMessage) {
        if(StringUtil.isEmpty(initialMessage.getMessageMapJSON())){
            Map<String, Object> map = new HashMap<>();
            initialMessage.setMessageMapJSON(JSON.toJSONString(map));
        }

        Map<String, Object> messageMap = com.alibaba.fastjson.JSON.parseObject(initialMessage.getMessageMapJSON(), HashMap.class);

        BMessageSetting pushSetting = getPushSetting(initialMessage.getSiteId(), initialMessage.getMessageType());
        BMessage bMessage = getPushMessage(initialMessage.getSiteId(), initialMessage.getStoreId(), initialMessage.getStoreAdminId(), pushSetting, initialMessage.getMessageMapJSON());//根据设置生成推送信息格式

        if("".equals(bMessage.getMessageType())){//数据库未配置类型
            if(initialMessage.getOffLine()!=null && initialMessage.getOffLine()==0){
                bMessage.setOffLine(0);
            }
            if(initialMessage.getWifi()!=null && initialMessage.getWifi()==1){
                bMessage.setWifi(1);
            }
        }

        if(PushType.NOTIFY_OTHER_CLIENT_QUIT.getValue().equals(initialMessage.getMessageType())){//通知该用户的其他客户端下线
//            bMessage = processClientQuit(bMessage, messageMap);
        }else if(PushType.ORDER_NEWORDER.getValue().equals(initialMessage.getMessageType())){//新订单提醒
            bMessage = processNewOrder(bMessage, messageMap);
        }else if(PushType.ORDER_NOTIFYSHIPMENT.getValue().equals(initialMessage.getMessageType())){//通知发货
            bMessage = processNotifyShipment(bMessage, messageMap);
        }else if(PushType.NOTIFY_SEND_COUPON.getValue().equals(initialMessage.getMessageType())){//派券提醒
            bMessage = processNotifySendCoupon(bMessage, messageMap);
        }else if(PushType.TASK_NEWTASK.getValue().equals(initialMessage.getMessageType())) {//新任务
            bMessage = processNotifyNewTask(bMessage, messageMap);
        }else if(PushType.TASK_PUNISHMENTTASK.getValue().equals(initialMessage.getMessageType())) {//惩罚提醒
            bMessage = processNotifyPubnishment(bMessage, messageMap);
        }else if(PushType.TASK_FINISH.getValue().equals(initialMessage.getMessageType())) {//完成任务
            bMessage = processNotifyFinishTask(bMessage, messageMap);
        }else if(PushType.TASK_NEWORDERTASK.getValue().equals(initialMessage.getMessageType())) {//新订单任务
            bMessage = processNotifyNewOrderTask(bMessage, messageMap);
        }else if(PushType.TASK_NEWREGISTERTASK.getValue().equals(initialMessage.getMessageType())) {//新注册任务
            bMessage = processNotifyNewRegisterTask(bMessage, messageMap);
        } else if (PushType.TASK_NEWEXAM.getValue().equals(initialMessage.getMessageType())) {
            bMessage = processNotifyNewExam(bMessage, messageMap);
        }else if(PushType.TASK_VISIT.getValue().equals(initialMessage.getMessageType())){
            bMessage=processNotifyVisitTask(bMessage,messageMap);
        } else if (PushType.NOTIFY_PAY_SUCCESS.getValue().equals(initialMessage.getMessageType())) {
            bMessage=processPaySuccess(bMessage,messageMap);
        } else if (PushType.NOTIFY_PAY_FAIL.getValue().equals(initialMessage.getMessageType())) {
            bMessage=processPayFail(bMessage,messageMap);
        }

        return bMessage;
    }

    public BMessage processPaySuccess(BMessage bMessage, Map<String, Object> messageMap){
        String msg=bMessage.getMessageSummary().replaceAll("xxx", String.valueOf(messageMap.get("money")));
        double coupon_fee=(Double.parseDouble(String.valueOf(messageMap.get("coupon_fee"))))/10/10;
        String pay_style=String.valueOf(messageMap.get("pay_style"));
        if(!StringUtil.isEmpty(coupon_fee)&&coupon_fee>0){
            msg=msg.replaceAll("#","优惠"+coupon_fee+"元,");
        }else {
            msg=msg.replaceAll("#","");
        }
        msg=msg.replaceAll("@","wx".equals(pay_style)?"微信":"支付宝");
        bMessage.setMessageSummary(msg);
        bMessage.setNotificationText(msg);
        return bMessage;
    }

    public BMessage processPayFail(BMessage bMessage, Map<String, Object> messageMap){
        bMessage.setMessageSummary(bMessage.getMessageSummary().replaceAll("xxx", String.valueOf(messageMap.get("money"))));
        bMessage.setNotificationText(bMessage.getNotificationText().replaceAll("xxx", String.valueOf(messageMap.get("money"))));
        return bMessage;
    }

    /**
     * 新订单提醒
     */
    public BMessage processNewOrder(BMessage bMessage, Map<String, Object> messageMap){
//        Trades trades = JSON.parseObject((String) messageMap.get("trades"), Trades.class);
        bMessage.setMessageSummary(bMessage.getMessageSummary().replaceAll("xxx", String.valueOf(messageMap.get("recevierMobile"))));//xxx下了新订单，请及时处理！
        bMessage.setNotificationText(bMessage.getNotificationText().replaceAll("xxx", String.valueOf(messageMap.get("recevierMobile"))));//通知：xxx下了新订单，请及时处理！
        return bMessage;
    }

    /**
     * 通知发货
     */
    public BMessage processNotifyShipment(BMessage bMessage, Map<String, Object> messageMap){
        bMessage.setMessageTitle(bMessage.getMessageTitle().replaceAll("xxx", String.valueOf(messageMap.get("remainingTime"))));//xxx分钟后有订单需要发货
        bMessage.setMessageSummary(bMessage.getMessageSummary().replaceAll("xxx", String.valueOf(messageMap.get("stockupId"))));//取货号 xxx 的订单需要发货！
        bMessage.setMessageContent(bMessage.getMessageContent().replaceAll("xxx", String.valueOf(messageMap.get("remainingTime"))));//以下订单xxx分钟后需要发货
        bMessage.setNotificationTitle(bMessage.getNotificationTitle().replaceAll("xxx", String.valueOf(messageMap.get("remainingTime"))));//xxx分钟后有订单需要发货
        bMessage.setNotificationText(bMessage.getNotificationText().replaceAll("xxx", String.valueOf(messageMap.get("stockupId"))));//取货号 xxx 的订单需要发货！
        return bMessage;
    }

    /**
     * 派券提醒
     */
    public BMessage processNotifySendCoupon(BMessage bMessage, Map<String, Object> messageMap){
        bMessage.setMessageSummary(bMessage.getMessageSummary().replaceAll("xxx", String.valueOf(messageMap.get("couponNum"))));//您收到xxx张新的优惠券
        bMessage.setNotificationText(bMessage.getNotificationText().replaceAll("xxx", String.valueOf(messageMap.get("couponNum"))));//您收到xxx张新的优惠券
        return bMessage;
    }

    //新任务
    public BMessage processNotifyNewTask(BMessage bMessage, Map<String, Object> messageMap){
        bMessage.setMessageSummary(bMessage.getMessageSummary().replaceAll("新任务", String.valueOf(messageMap.get("taskName"))));
        bMessage.setNotificationText(bMessage.getNotificationText().replaceAll("新任务", String.valueOf(messageMap.get("taskName"))));
        return bMessage;
    }
    //惩罚提醒
    public BMessage processNotifyPubnishment(BMessage bMessage, Map<String, Object> messageMap){
        bMessage.setMessageSummary(bMessage.getMessageSummary().replaceAll("惩罚任务", String.valueOf(messageMap.get("taskName"))));
        bMessage.setNotificationText(bMessage.getNotificationText().replaceAll("惩罚任务", String.valueOf(messageMap.get("taskName"))));
        return bMessage;
    }
    //完成任务
    public BMessage processNotifyFinishTask(BMessage bMessage, Map<String, Object> messageMap){
        bMessage.setMessageSummary(bMessage.getMessageSummary().replaceAll("完成任务", String.valueOf(messageMap.get("taskName"))));
        bMessage.setNotificationText(bMessage.getNotificationText().replaceAll("完成任务", String.valueOf(messageMap.get("taskName"))));
        return bMessage;
    }

    //新订单任务
    public BMessage processNotifyNewOrderTask(BMessage bMessage, Map<String, Object> messageMap){
        bMessage.setMessageSummary(bMessage.getMessageSummary().replaceAll("新订单任务", String.valueOf(messageMap.get("taskName"))));
        bMessage.setNotificationText(bMessage.getNotificationText().replaceAll("新订单任务", String.valueOf(messageMap.get("taskName"))));
        return bMessage;
    }

    //新注册任务
    public BMessage processNotifyNewRegisterTask(BMessage bMessage, Map<String, Object> messageMap){
        bMessage.setMessageSummary(bMessage.getMessageSummary().replaceAll("新注册任务", String.valueOf(messageMap.get("taskName"))));
        bMessage.setNotificationText(bMessage.getNotificationText().replaceAll("新注册任务", String.valueOf(messageMap.get("taskName"))));
        return bMessage;
    }

    public BMessage processNotifyNewExam(BMessage bMessage, Map<String, Object> messageMap){
        bMessage.setMessageSummary(bMessage.getMessageSummary().replaceAll("新答题任务", String.valueOf(messageMap.get("taskName"))));
        bMessage.setNotificationText(bMessage.getNotificationText().replaceAll("新答题任务", String.valueOf(messageMap.get("taskName"))));
        return bMessage;
    }

    public BMessage processNotifyVisitTask(BMessage bMessage, Map<String, Object> messageMap){
        bMessage.setMessageSummary(bMessage.getMessageSummary().replaceAll("新回访任务", String.valueOf(messageMap.get("taskName"))));
        bMessage.setNotificationText(bMessage.getNotificationText() + String.valueOf(messageMap.get("taskNums")));
        return bMessage;
    }















    /**
     * 根据店员ID 获取 推送clientID
     * @param siteId
     * @param storeAdminId
     * @return
     */
    public String getPushClientId(String siteId, String storeAdminId) {
        String clientId = null;
        try {
//            clientId = stringRedisTemplate.opsForValue().get("notifyId_" + siteId + "_" + storeAdminId);
//            if(clientId == null){//店员 未登录APP 推送
                clientId = bMessageSenderMapper.getPushClientId(siteId, storeAdminId);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return clientId;
    }

    /**
     *批量推送获取 目标列表
     */
    public Map<String, Object> getTargetListMap(InitialMessage initialMessage, List<String> storeAdminIds, String appId){

        logger.info("批量推送获取参数：initialMessage:{},storeAdminIds:{},appId:{}",initialMessage,storeAdminIds,appId);
        Map<String, Object> targetListMap = null;
        if(storeAdminIds!=null){
            if(initialMessage.getSiteId()!=null){
                targetListMap = getPushTargetListByStoreAdminIds(initialMessage.getSiteId(), appId, storeAdminIds);
            }else {
                return targetListMap;
            }
        }else if(initialMessage.getSiteId()!=null && initialMessage.getStoreId()!=null && initialMessage.getStoreAdminId()!=null){
            targetListMap = getPushTargetListByStoreAdminIds(initialMessage.getSiteId(), appId, Arrays.asList(initialMessage.getStoreAdminId()));
        }else if(initialMessage.getSiteId()!=null && initialMessage.getStoreId()!=null){
            targetListMap = getPushTargetListByStoreId(initialMessage.getSiteId(), appId, initialMessage.getStoreId());
        }else if(initialMessage.getSiteId()!=null){
            //
        }else {
            //
        }
        return targetListMap;
    }

    /**
     *获取推送目标 targets - storeAdminIds
     */
    public Map<String, Object> getPushTargetListByStoreAdminIds(String siteId, String appId, List<String> storeAdminIds) {
        Map<String, Object> targetListMap = null;

        List<Target> targets = new ArrayList();
        List<String> storeAdminIdList = new ArrayList();
        for(String id : storeAdminIds){
            String clientId = getPushClientId(siteId, id);//获取推送ID，mysql
            if(clientId != null){
                boolean flag = true;
                try {
                    if(getClientIdIsOnline(siteId, id, null) == null){//cid 登录时存，，这里以mysql中的为标准
                        flag = false;
                    }
                } catch (Exception e) {
                    logger.error("getClientIdIsOnline 报错,siteId:{},id:{}报错信息：{}",siteId,id,ExceptionUtil.exceptionDetail(e));
                }
                if(flag){
                    Target target = new Target();
                    target.setAppId(appId);
                    target.setClientId(clientId);
                    targets.add(target);
                }
                storeAdminIdList.add(id);
            }
        }
        if(storeAdminIdList.size()!=0){
            targetListMap = new HashMap<>();
            targetListMap.put("targets", targets);
            targetListMap.put("storeAdminIds", storeAdminIdList);
        }
        return targetListMap;
    }

    /**
     *获取推送目标List
     */
    public Map<String, Object> getPushTargetListByStoreId(String siteId, String appId, String storeId) {
        Map<String, Object> targetListMap = null;

        List<Target> targets = new ArrayList();
        List<String> storeAdminIdList = new ArrayList();
        try {
            List<Map<String, Object>> clientIds = bMessageSenderMapper.getPushClientIdList(siteId, storeId);
            if(clientIds!=null && clientIds.size()!=0){
                for(Map<String, Object> map : clientIds){
                    boolean flag = true;
                    try {
                        if(getClientIdIsOnline(siteId, String.valueOf(map.get("store_admin_id")), null) == null){//cid 登录时存，，这里以mysql中的为标准
                            flag = false;
                        }
                    } catch (Exception e) {
                        logger.error("getClientIdIsOnline 报错,siteId:{},id:{}报错信息：{}",siteId,String.valueOf(map.get("store_admin_id")),ExceptionUtil.exceptionDetail(e));
                    }
                    if(flag){
                        Target target = new Target();
                        target.setAppId(appId);
                        target.setClientId(String.valueOf(map.get("client_id")));
                        targets.add(target);
                    }
                    storeAdminIdList.add(String.valueOf(map.get("store_admin_id")));
                }
            }
        } catch (Exception e) {
            logger.error("获取推送目标List 报错：报错信息{}",ExceptionUtil.exceptionDetail(e));
        }

        if(storeAdminIdList.size()!=0){
            targetListMap = new HashMap<>();
            targetListMap.put("targets", targets);
            targetListMap.put("storeAdminIds", storeAdminIdList);
        }
        return targetListMap;
    }


    /**
     * 设置单推消息体
     * @param template
     * @return
     */
    public SingleMessage getSingleMessage(TransmissionTemplate template, Integer offLine, Integer wifi) {//单推消息的消息体
        SingleMessage message = new SingleMessage();
        message.setData(template);//推送消息消息内容
        message.setOffline((offLine!=null&&offLine==0)?false:true);//消息离线是否存储
        message.setOfflineExpireTime(24 * 3600 * 1000);// 离线有效时间，单位为毫秒，可选
        message.setPushNetWorkType((wifi!=null&&wifi==1)?1:0);// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        return message;
    }

    /**
     *设置列表推送消息体
     */
    public ListMessage getListMessage(TransmissionTemplate template, Integer offLine, Integer wifi) {//单推消息的消息体
        ListMessage message = new ListMessage();
        message.setData(template);//推送消息消息内容
        message.setOffline((offLine!=null&&offLine==0)?false:true);//消息离线是否存储
        message.setOfflineExpireTime(24 * 3600 * 1000);// 离线有效时间，单位为毫秒，可选
        message.setPushNetWorkType((wifi!=null&&wifi==1)?1:0);// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        return message;
    }

    /**
     * 设置推送信息模板
     * @param siteId
     * @param messageType
     * @return
     */
    public BMessageSetting getPushSetting(String siteId, String messageType) {
        siteId = PushType.settingId;
        BMessageSetting pushSetting = null;
        try {
           /* String pushSettingStr = null;//修改数据库要更新的。。。
            try {
                pushSettingStr = stringRedisTemplate.opsForValue().get("pushSetting_" + siteId + "_" + messageType);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            }*/
//            if(pushSettingStr == null){
                pushSetting = bMessageSettingMapper.getPushSetting(siteId, messageType);
//                if(pushSetting != null){
//                    pushSettingStr = com.alibaba.fastjson.JSON.toJSONString(pushSetting);
//                    stringRedisTemplate.opsForValue().set("pushSetting_" + siteId + "_" + messageType, pushSettingStr);
//                }
            /*}else {
                //pushSettingStr.replaceAll("\"","");
                pushSetting = com.alibaba.fastjson.JSON.parseObject(pushSettingStr, BMessageSetting.class);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        if(pushSetting == null){
            pushSetting = new BMessageSetting();
            pushSetting.setMessageType("");//未指定类型测试
            pushSetting.setMessageTitle("新消息：" + siteId + "_" + messageType);
            pushSetting.setMessageIcon(null);
            pushSetting.setMessageSummary("你有新的消息，请及时处理！");
            pushSetting.setMessageContent(null);
            pushSetting.setMessageWhereabouts(null);
            pushSetting.setNotificationTitle("新消息");
            pushSetting.setNotificationText("你有新的消息，请及时处理！");
            pushSetting.setNotificationLogo(null);
            pushSetting.setNotificationLogoUrl(null);
            pushSetting.setNotificationRing(1);
            pushSetting.setNotificationVibrate(1);
            pushSetting.setNotificationClearable(1);
            pushSetting.setMandatoryReminder(0);
            pushSetting.setOffLine(1);
            pushSetting.setWifi(0);
            pushSetting.setSound("default");
        }
        return pushSetting;
    }

    /**
     * 生成推送信息
     * @param siteId
     * @param storeId
     * @param storeAdminId
     * @param pushSetting
     * @param messageJSON
     * @return
     */
    public BMessage getPushMessage(String siteId, String storeId, String storeAdminId, BMessageSetting pushSetting, String messageJSON) {
        BMessage pushMessage = new BMessage();
        pushMessage.setSiteId(Integer.parseInt(siteId));
        pushMessage.setStoreId(Integer.parseInt(storeId));
        pushMessage.setStoreAdminId(!StringUtil.isEmpty(storeAdminId)?Integer.parseInt(storeAdminId):null);
        pushMessage.setMessageType(pushSetting.getMessageType());
        pushMessage.setMessageTitle(pushSetting.getMessageTitle());
        pushMessage.setMessageIcon(pushSetting.getMessageIcon());
        pushMessage.setMessageSummary(pushSetting.getMessageSummary());
        pushMessage.setMessageContent(pushSetting.getMessageContent());
        pushMessage.setMessageWhereabouts(pushSetting.getMessageWhereabouts());
        pushMessage.setNotificationTitle(pushSetting.getNotificationTitle());
        pushMessage.setNotificationText(pushSetting.getNotificationText());
        pushMessage.setNotificationLogo(pushSetting.getNotificationLogo());
        pushMessage.setNotificationLogoUrl(pushSetting.getNotificationLogoUrl());
        pushMessage.setNotificationRing(pushSetting.getNotificationRing());
        pushMessage.setNotificationVibrate(pushSetting.getNotificationVibrate());
        pushMessage.setNotificationClearable(pushSetting.getNotificationClearable());
        pushMessage.setMandatoryReminder(pushSetting.getMandatoryReminder());
        pushMessage.setExt(messageJSON);
        pushMessage.setOffLine(pushSetting.getOffLine());
        pushMessage.setWifi(pushSetting.getWifi());
        pushMessage.setSound(!StringUtil.isEmpty(pushSetting.getSound())?pushSetting.getSound():"default");
        return pushMessage;
    }

    /**
     * 设置透传模板
     * @param content
     * @return
     */
    public TransmissionTemplate getTransmissionTemplateAPNS(String notifyTitle, String notifyText, String content, String appId, String appKey, String badgeNum, String sound){//ios、android 统一透传模板
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);//设定接收的应用
        template.setAppkey(appKey);
        template.setTransmissionType(2);//收到消息是否立即启动应用，1为立即启动，2则广播等待客户端自启动
        template.setTransmissionContent(content);//透传内容，不支持转义字符

        //JSONObject jsonObj = JSON.parseObject(content);
        setAPNPayload(template, notifyTitle, notifyText, badgeNum, sound);//设置ios离线通知

        /*try {
            template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");//收到消息的展示时间
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return template;
    }

    /**
     * ios 离线通知 apns
     * @param template
     * @param content
     */
    public void setAPNPayload(TransmissionTemplate template, String title, String content, String badgeNum, String sound) {// ios APNs通知参数：setAutoBadge、setAlertMsg、setSound(不设置无声)必须至少设置一项
        APNPayload apnPayload = new APNPayload();
        apnPayload.setAutoBadge(!StringUtil.isEmpty(badgeNum)?badgeNum:"+1");//apnPayload.setAutoBadge("+1");//设置角标，还可以实现显示数字的自动增减，如"+1"、"-1"、"1"等
        apnPayload.setContentAvailable(1);//推送直接带有透传数据
        apnPayload.setSound(!StringUtil.isEmpty(sound)?sound:"default");//通知铃声文件名，无声设置为"com.gexin.ios.silence"
        apnPayload.setAlertMsg(getDictionaryAlertMsg(title, content));//字典模式使用APNPayload.DictionaryAlertMsg
//        apnPayload.setAlertMsg(new APNPayload.SimpleAlertMsg(content));//通知消息体 -----没有通知标题
//        apnPayload.setCategory();//在客户端通知栏触发特定的action和button显示
//        apnPayload.addCustomMsg();//增加自定义的数据,Key-Value形式

//        // 添加多媒体资源
//        apnPayload.addMultiMedia(new MultiMedia().setResType(MultiMedia.MediaType.video)
//                .setResUrl("http://ol5mrj259.bkt.clouddn.com/test2.mp4")
//                .setOnlyWifi(true));

        template.setAPNInfo(apnPayload);//iOS推送使用该字段
    }

    private APNPayload.AlertMsg getDictionaryAlertMsg(String title, String content) {
        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
        alertMsg.setBody(content);//通知文本消息字符串
//        alertMsg.setActionLocKey("ActionLockey");//(用于多语言支持）指定执行按钮所使用的Localizable.strings
//        alertMsg.setLocKey("LocKey");//(用于多语言支持）指定Localizable.strings文件中相应的key
//        alertMsg.addLocArg("loc-args");//如果loc-key中使用的占位符，则在loc-args中指定各参数
//        alertMsg.setLaunchImage("launch-image");//指定启动界面图片名
        // iOS8.2以上版本支持
        alertMsg.setTitle(title);//通知标题
//        alertMsg.setTitleLocKey("TitleLocKey");//(用于多语言支持）对于标题指定执行按钮所使用的Localizable.strings,仅支持iOS8.2以上版本
//        alertMsg.addTitleLocArg("TitleLocArg");//对于标题, 如果loc-key中使用的占位符，则在loc-args中指定各参数,仅支持iOS8.2以上版本
        return alertMsg;
    }

    /**
     * 消息首页 获取最新消息记录
     */
    public Map<String,Object> getLastMessages(String siteId, String storeId, String storeAdminId) throws Exception{
        Map<String,Object> result = new HashMap<>();
        List<Map<String, Object>> messageList = bMessageReceivedMapper.getLastMessages(siteId, storeId, storeAdminId);
        List<Map<String, Object>> noReadMessageNum = bMessageReceivedMapper.getNoReadMessageNum(siteId, storeId, storeAdminId);
        result.put("messageList", messageList);
        result.put("noReadNum", noReadMessageNum);
        return result;
    }

    /**
     *获取店员指定类型消息列表
     */
    public Map<String,Object> getMessageList(String siteId, String storeId, String storeAdminId, String messageType, int pageNum, int pageSize) {
        Map<String,Object> result = new HashMap<>();
        Page<Map> page = PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> messageList = bMessageReceivedMapper.getMessageList(siteId, storeId, storeAdminId, messageType);
        result.put("pageInfo", page.toPageInfo());
        return result;
    }

    /**
     *删除店员指定类型消息列表
     */
    public void delMessageList(String siteId, String storeId, String storeAdminId, String messageType) throws Exception {
        bMessageReceivedMapper.delMessageList(siteId, storeId, storeAdminId, messageType);
    }

    /**
     *设置消息为已读
     */
    public Map<String,Object> readMessage(String siteId, String id) {
        Map<String,Object> result = new HashMap<>();
        int i = bMessageReceivedMapper.updateReadMessage(siteId, id);
        if(i > 0){
            result.put("setRead", "OK");
        }
        return result;
    }

    /**
     *店员某类型消息 全部设置为已读
     */
    public Map<String,Object> readMessageAll(String siteId, String storeId, String storeAdminId, String messageType) {
        Map<String,Object> result = new HashMap<>();
        int i = bMessageReceivedMapper.readMessageAll(siteId, storeId, storeAdminId, messageType);
        if(i > 0){
            result.put("setRead", "OK");
        }
        return result;
    }

    /**
     *删除消息
     */
    public Map<String,Object> delMessage(String siteId, String id) {
        Map<String,Object> result = new HashMap<>();
        int i = bMessageReceivedMapper.deleteByPrimaryKey(siteId, id);
        if(i > 0){
            result.put("del", "OK");
        }
        return result;
    }

    public Map<String,Object> copy(String siteId, String storeAdminId, Integer force) {
        Map<String,Object> result = new HashMap<>();
        long s = System.currentTimeMillis();
        Set<String> clientIdKeys = null;
        int num = 0;
        try {
            if (force != null && force == 1) {
                clientIdKeys = stringRedisTemplate.keys("notifyId_" + "*");
            } else if (StringUtils.isNotBlank(siteId) && StringUtils.isNotBlank(storeAdminId)){
                clientIdKeys = stringRedisTemplate.keys("notifyId_" + siteId + "_" + storeAdminId + "_" + "*");
            } else {
                result.put("msg", "none");
            }
        } catch (Exception e) {
            logger.error("keys* {}", e);
            result.put("err", e.getMessage());
        }
        if (CollectionUtils.isNotEmpty(clientIdKeys)) {
            num = clientIdKeys.size();
            Map<String, List<String>> keyMapping = clientIdKeys.stream().filter(o -> o.split("_").length == 4).collect(() -> new HashMap<String, List<String>>(),
                (m, o) -> {
                    String k = o.substring(0, o.lastIndexOf("_"));
                    String v = o.substring(o.lastIndexOf("_") + 1);
                    List<String> value = m.getOrDefault(k, Lists.newArrayList());
                    value.add(v);
                    m.put(k, value);
                },
                HashMap::putAll);
            if (MapUtils.isNotEmpty(keyMapping)) {
                AtomicInteger errNum = new AtomicInteger(0);
                AtomicInteger compNum = new AtomicInteger(0);
                for (Map.Entry<String, List<String>> entry : keyMapping.entrySet()) {
                    if (CollectionUtils.isNotEmpty(entry.getValue())) {
                        try {
                            RedisUtil.setAdd(entry.getKey(), entry.getValue().toArray(new String[0]));
//                            RedisUtil.setRemove(entry.getKey(), entry.getValue().toArray(new String[0]));
                            result.put("compNum", compNum.incrementAndGet());
                        } catch (Exception e) {
                            logger.error("RedisUtil {} {} {} ", entry.getKey(), entry.getValue(), e);
                            result.put("err2", e.getMessage());
                            result.put("errNum", errNum.incrementAndGet());
                        }
                    }
                }
            }
        }
        result.put("num", num);
        result.put("time", (System.currentTimeMillis() - s)/1000f);
        return result;
    }

    public Map<String,Object> delTest(String siteId, String storeAdminId, Integer force) {
        Map<String,Object> result = new HashMap<>();
        long s = System.currentTimeMillis();
        Set<String> clientIdKeys = null;
        int num = 0;
        try {
            if (force != null && force == 1) {
                clientIdKeys = stringRedisTemplate.keys("notifyId_" + "*");
            } else if (StringUtils.isNotBlank(siteId) && StringUtils.isNotBlank(storeAdminId)){
                clientIdKeys = stringRedisTemplate.keys("notifyId_" + siteId + "_" + storeAdminId);
            } else {
                result.put("msg", "none");
            }
        } catch (Exception e) {
            logger.error("keys* {}", e);
            result.put("err", e.getMessage());
        }
        if (CollectionUtils.isNotEmpty(clientIdKeys)) {
            num = clientIdKeys.size();
            try {
                clientIdKeys.stream()
                    .filter(o -> o.split("_").length == 3)
                    .filter(o -> stringRedisTemplate.type(o).equals(DataType.STRING))
                    .forEach(o -> stringRedisTemplate.delete(o));
            } catch (Exception e) {
                logger.error("clientIdKeys {}", clientIdKeys);
                result.put("err2", e.getMessage());
            }
        }
        result.put("num", num);
        result.put("time", (System.currentTimeMillis() - s)/1000f);
        return result;
    }

    public Map<String,Object> crTest(String key, Integer cr) {
        Map<String,Object> result = new HashMap<>();
        long s = System.currentTimeMillis();
        Long value = null;
        if (cr == 0) {
            value = stringRedisTemplate.opsForValue().increment(key, 1);
        } else if (cr == 1) {
            value = stringRedisTemplate.opsForValue().increment(key, -1);
        } else {
            value = Long.valueOf(stringRedisTemplate.opsForValue().get(key));
        }
        result.put("value", value);
        result.put("time", (System.currentTimeMillis() - s)/1000f);
        return result;
    }
}
