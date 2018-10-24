package com.jk51.modules.offline.service;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.erpDataConfig.DataSourceConfig_ceshi;
import com.jk51.erpDataConfig.jiuzhou.DataSourceConfig_JiuZhou;
import com.jk51.model.erp.PanDianHead;
import com.jk51.model.erp.PanDianItem;
import com.jk51.model.erp.StoreOrderNum;
import com.jk51.modules.pandian.job.PandianErpInsertMessage;
import com.jk51.modules.pandian.mapper.BInventoriesMapper;
import com.jk51.mq.mns.CloudQueueFactory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:盘点的erp对接
 * 作者: dumingliang
 * 创建日期: 2018-05-08
 * 修改记录:
 */
@Service
public class OfflineCheckService {

    private static final Logger logger = LoggerFactory.getLogger(OfflineCheckService.class);

    public static final List<Integer> pdErpSiteIdList = Arrays.asList(100166);

    @Autowired
    private ErpToolsService erpToolsService;

    @Autowired
    private BInventoriesMapper inventoriesMapper;

    @Resource
    private DataSourceConfig_JiuZhou dataSourceConfig_jiuZhou;
    @Resource
    private DataSourceConfig_ceshi dataSourceConfig_ceshi;
    //查询最新任务号
    private static final String sq1_selectBillid = "SELECT a.billid,a.unit_no,a.third_no,a.add_time FROM app_storemovtake_head a JOIN (SELECT unit_no,MAX(add_time) add_time FROM app_storemovtake_head WHERE status=0 GROUP BY unit_no) b ON a.unit_no=b.unit_no AND a.add_time=b.add_time";
    //查询单门店最新任务号
    private static final String sql_selectByUid = "SELECT a.billid,a.unit_no,a.third_no,a.add_time FROM app_storemovtake_head a JOIN (SELECT unit_no,MAX(add_time) add_time FROM app_storemovtake_head WHERE status=0 and unit_no=? GROUP BY unit_no) b ON a.unit_no=b.unit_no AND a.add_time=b.add_time";
    //更新盘点单号
    private static final String sql_updateThirdNo = "update app_storemovtake_head set third_no=? where billid=?";
    //查询此次需要盘点的商品总数
    private static final String sql_getCount = "select count(*) from app_storemovtake_item where billid in(?)";
    //查询最新的盘点表数据（根据任务号查询）
    private static final String sq1server_checkInfo = "SELECT billid,itemid,mname,spec,productor,goodsno,barcode,batchid,isnull(batchno,'无') batchno,ackquty from app_storemovtake_item where billid=? and goodsno is not null and goodsno !='' and mname is not NULL and mname !=''";

    private static final String mysql_checkInfo = "SELECT billid,itemid,mname,spec,productor,goodsno,barcode,batchid,ifnull(batchno,'无') batchno,ackquty from app_storemovtake_item where billid=? and goodsno is not null and goodsno !='' and mname is not NULL and mname !=''";
    //更新盘点状态
    private static final String sq1_updateStatus = "update app_storemovtake_head set status=1 where billid=? and third_no=?";
    //根据盘点单号查询任务号
    private static final String sql_selectByPandianNum = "select billid,unit_no from app_storemovtake_head where status=0 AND third_no=? and unit_no=?";
    //更新实际盘点数
    private static final String sql_updateActqty = "update app_storemovtake_item set actquty=? where billid=? and itemid=? and goodsno=?";
    //查询盘点单未更改盘点实际数量
    private static final String sql_ActqtyIsNull = "select itemid from app_storemovtake_item WHERE actquty is NULL AND billid=?";

    private JdbcTemplate getJdbcTemplateBysiteId(Integer siteId) {
        try {
            if (siteId == 100166) {
                return dataSourceConfig_jiuZhou.getJiuZhouJDBCTemplate();
            } else if (siteId == 100190) {
                return dataSourceConfig_ceshi.getCeshiJDBCTemplate();
            }
        } catch (Exception e) {
            logger.info("获取jdbc连接方式出错,siteId{},问题:{}.", siteId, e.getMessage());
            erpToolsService.sendError(siteId, "盘点接口对接,【getJdbcTemplateBysiteId】", "问题：" + e.getMessage(),
                e.toString(), 4);
        }
        return null;
    }

    //盘点单生成时读取九洲任务号,并更新外部盘点单号
    public PanDianHead getPanDianStatusFromErp(Integer siteId, String pandianNO) {
        logger.info("getPanDianStatusFromErp，siteId {}， pandianNO {} ", siteId, pandianNO);
        PanDianHead panDianHead = new PanDianHead();
        panDianHead.setSiteId(siteId);
        panDianHead.setPanDianNum(pandianNO);
        try {
            if (pdErpSiteIdList.contains(siteId)) {
                //查询最新的盘点任务号
                List<StoreOrderNum> storeOrderNumList = new ArrayList<>();
                List<StoreOrderNum> result = getJdbcTemplateBysiteId(siteId).query(sq1_selectBillid, new BeanPropertyRowMapper<>(StoreOrderNum.class));
                if (!CollectionUtils.isEmpty(result)) {
                    for (StoreOrderNum orderNum : result) {
                        int month = orderNum.getAdd_time().getMonth() + 1;
                        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
                        if (month == currentMonth) {//不是当前月数据不读
                            storeOrderNumList.add(orderNum);
                        }
                    }
                }
                panDianHead.setStoreOrderNums(storeOrderNumList);
                logger.info("getPanDianStatusFromErp，siteId:{},pandianNum:{},返回值:{},", siteId, pandianNO, panDianHead.toString());
            }
        } catch (Exception e) {
            logger.info("method:{},siteId:{},reason:{}", "getPanDianStatusFromErp", siteId, e.getMessage());
            erpToolsService.sendError(siteId, "盘点接口对接,【getPanDianStatusFromErp】", "问题：" + e.getMessage(), e.toString(),
                4);
        }
        return panDianHead;
    }

    //获取最新的盘点单的商品数据
    public List<PanDianItem> getPanDianDetail(Integer siteId, String billid, String pandianNum) {
        List<PanDianItem> panDianItemList = new ArrayList<PanDianItem>();
        //根据任务号查询商品数据信息（门店）
        try {
            if (pdErpSiteIdList.contains(siteId)) {
                if (siteId == 100166) {
                    panDianItemList = getJdbcTemplateBysiteId(siteId).query(sq1server_checkInfo, new Object[]{billid}, new BeanPropertyRowMapper<>(PanDianItem.class));
                } else if (siteId == 100190) {
                    panDianItemList = getJdbcTemplateBysiteId(siteId).query(mysql_checkInfo, new Object[]{billid}, new BeanPropertyRowMapper<>(PanDianItem.class));
                }
                getJdbcTemplateBysiteId(siteId).update(sql_updateThirdNo, new Object[]{pandianNum, billid});
            }
        } catch (Exception e) {
            logger.info("获取最新盘点单商品数据：商户：{},任务号：{},问题：{}.", siteId, billid, e.getMessage());
            erpToolsService.sendError(siteId, "盘点接口对接,【getPanDianDetail】", "问题：" + e.getMessage(),
                e.toString(), 4);
        }
        return panDianItemList;
    }

    //查询某一个门店的最新盘点单商品数据
    public List<PanDianItem> getDetailByunitNO(Integer siteId, String panDianNum, String uid) {
        List<PanDianItem> panDianItemList = new ArrayList<>();
        try {
            if (pdErpSiteIdList.contains(siteId)) {
                StoreOrderNum storeOrderNum = getJdbcTemplateBysiteId(siteId).queryForObject(sql_selectByUid, new Object[]{uid, uid}, new BeanPropertyRowMapper<>(StoreOrderNum.class));
                getJdbcTemplateBysiteId(siteId).update(sql_updateThirdNo, new Object[]{panDianNum});
                panDianItemList = getPanDianDetail(siteId, storeOrderNum.getBillid(), panDianNum);
            }
        } catch (Exception e) {
            logger.info("获取最新盘点单商品数据：商户：{},问题：{}.", siteId, e.getMessage());
        }
        return panDianItemList;
    }

    //查询某一门店是否存在最新任务号
    public StoreOrderNum getBillidByUnitNO(Integer siteId, String panDianNum, String uid) {
        StoreOrderNum storeOrderNum = new StoreOrderNum();
        try {
            if (pdErpSiteIdList.contains(siteId)) {
                storeOrderNum = getJdbcTemplateBysiteId(siteId).queryForObject(sql_selectByUid, new Object[]{uid}, new BeanPropertyRowMapper<>(StoreOrderNum.class));
            }
        } catch (Exception e) {
            logger.info("查询某一门店是否存在最新任务号：商户：{},问题：{}.", siteId, e.getMessage());
        }
        return storeOrderNum;
    }

    /**
     * 盘点完成将实际库存数更新到线下erp
     *
     * @param siteId
     * @param pandianNum
     */
    public void updateOfflinetqty(Integer siteId, String pandianNum, String uid, List<Integer> itemIds) {
        try {
            if (pdErpSiteIdList.contains(siteId)) {
                //查询盘点号和任务号对应
                List<StoreOrderNum> storeOrderNums = getJdbcTemplateBysiteId(siteId).query(sql_selectByPandianNum,
                    new Object[]{pandianNum, uid}, new BeanPropertyRowMapper<>(StoreOrderNum.class));
                for (StoreOrderNum storeOrderNum : storeOrderNums) {
                    //查询本地盘点数据
                    List<Map<String, Object>> mapList = inventoriesMapper.selectInventoriesListToErp(siteId, storeOrderNum.getUnit_no(), pandianNum, itemIds);
                    Long startTime = System.currentTimeMillis();
                    int count = 300;
                    int nums = (int) Math.ceil(Double.valueOf(String.valueOf(mapList.size())) / Double.valueOf(String.valueOf(count)));
                    Map<Integer, List<Map<String, Object>>> map = new HashMap<>();
                    if (nums == 1) {
                        map.put(1, mapList);
                    } else {
                        for (int i = 1; i <= nums; i++) {
                            if (i == nums) {
                                List<Map<String, Object>> mapList1 = mapList.subList((i - 1) * count, mapList.size());
                                map.put(i, mapList1);
                            } else {
                                List<Map<String, Object>> mapList1 = mapList.subList((i - 1) * count, i * count);
                                map.put(i, mapList1);
                            }
                        }
                    }
                    logger.info("商户{},盘点单号：{},门店编码{},数据分组数据：{}", siteId, pandianNum, uid, map.toString());
                    updateActkty(siteId, map, storeOrderNum, pandianNum);
                    List<Integer> panDianItemIdList = getJdbcTemplateBysiteId(siteId).queryForList(sql_ActqtyIsNull, new Object[]{storeOrderNum.getBillid()}, Integer.class);
                    if (CollectionUtils.isEmpty(panDianItemIdList)) {
                        //更新这个盘点单，任务号的状态为盘点确认
                        getJdbcTemplateBysiteId(siteId).update(sq1_updateStatus, new PreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps) throws SQLException {
                                ps.setString(1, storeOrderNum.getBillid());//任务号
                                ps.setString(2, pandianNum);//盘点单号
                            }
                        });
                    }
                    Long endTime = System.currentTimeMillis();
                    logger.info("商户siteId:{},盘点单号:{},任务号:{},门店编码:{},更新数量:{},时间:{}",
                        siteId, pandianNum, storeOrderNum.getBillid(), uid, mapList.size(), endTime - startTime);
                }
            }
        } catch (Exception e) {
            logger.info("更新erp盘点数据出错[updateOfflinetqty]，siteId:{},盘点单号:{},reason:{}", siteId, pandianNum, e);
            erpToolsService.sendError(siteId, "盘点接口对接，【updateOfflinetqty】", "问题：" + e.getMessage(),
                e.toString(), 4);
        }
    }

    /**
     * @param siteId
     * @param pandianNum
     */
    public void updateOfflinetqtyReload(Integer siteId, String pandianNum, String uid) {
        try {
            if (pdErpSiteIdList.contains(siteId)) {
                //查询盘点号和任务号对应
                List<StoreOrderNum> storeOrderNums = getJdbcTemplateBysiteId(siteId).query(sql_selectByPandianNum,
                    new Object[]{pandianNum, uid}, new BeanPropertyRowMapper<>(StoreOrderNum.class));
                for (StoreOrderNum storeOrderNum : storeOrderNums) {
                    List<Integer> panDianItemIdList = getJdbcTemplateBysiteId(siteId).queryForList(sql_ActqtyIsNull, new Object[]{storeOrderNum.getBillid()}, Integer.class);
                    if (!CollectionUtils.isEmpty(panDianItemIdList)) {
                        updateOfflinetqty(siteId, pandianNum, uid, panDianItemIdList);
                    }
                }
            }
        } catch (Exception e) {
            logger.info("更新erp盘点数据出错[updateOfflinetqty]，siteId:{},盘点单号:{},reason:{}", siteId, pandianNum, e);
            erpToolsService.sendError(siteId, "盘点接口对接，【updateOfflinetqty】", "问题：" + e.getMessage(),
                e.toString(), 4);
        }
    }

    public void sendMq(Integer siteId, String pandianNum, String uid) {
        Map<String, Object> erpInsertObj = new HashMap<>();
        erpInsertObj.put("siteId", siteId);
        erpInsertObj.put("uid", uid);
        erpInsertObj.put("pandianNum", pandianNum);
        String queueName = PandianErpInsertMessage.QUEUE_NAME;
        CloudQueue queue = CloudQueueFactory.create(queueName);
        Message message = new Message();
        message.setMessageBody(JSON.toJSONBytes(erpInsertObj));
        try {
            queue.putMessage(message);
            logger.info(" 加入消息队列成功! queueName:{} messageBody:{},messageId:{}", queueName,
                message.getMessageBodyAsString(),
                message.getMessageId());
        } catch (Exception e) {
            logger.debug("发送到消息队列失败 body:{} error:{}", message.getMessageBodyAsString(),
                e.getMessage());
        }
    }

    private void updateActkty(Integer siteId, Map<Integer, List<Map<String, Object>>> actktyMap, StoreOrderNum storeOrderNum, String pandianNum) {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 10, 10L, TimeUnit.SECONDS, workQueue);
        actktyMap.keySet().forEach((Integer key) -> {
            int n = Integer.valueOf(key);
            Future<?> future = executor.submit(() -> {
                updateActktyByUidTemp(siteId, actktyMap.get(n), storeOrderNum.getBillid(), pandianNum);
            });
        });
        executor.shutdown();
        try {
            while (!executor.awaitTermination(1000L, TimeUnit.MILLISECONDS)) ;
        } catch (Exception e) {
            logger.debug("九洲实际盘点数更新失败" + e.getMessage());
            erpToolsService.sendError(siteId, "盘点接口对接，【updateOfflinetqty】", "问题：" + e.getMessage(),
                e.toString(), 4);
        }
    }

    public void updateActktyByUidTemp(Integer siteId, List<Map<String, Object>> maplist, String billid, String pandianNum) {
        StringBuffer baseSQL = new StringBuffer("UPDATE app_storemovtake_item SET actquty = CASE ");
        if (!CollectionUtils.isEmpty(maplist)) {
            for (Map<String, Object> objectMap : maplist) {
                baseSQL.append("WHEN " +
                    "itemid = '" + Integer.parseInt(objectMap.get("erp_data_seq").toString()) + "' " + "AND " +
                    "goodsno ='" + objectMap.get("goods_code").toString() + "' THEN " +
                    "'" + objectMap.get("actual_store").toString() + "' ");
            }
            baseSQL.append("else actquty end where billid='" + billid + "'");
        }
        logger.info("商户{},盘点单号：{},任务号{}, sql语句：{},", siteId, pandianNum, billid, baseSQL);
        for (int i = 3; i > 0; i--) {
            try {
                if (getJdbcTemplateBysiteId(siteId).update(baseSQL.toString()) > 0) {
                    break;
                }
            } catch (Exception e) {
                logger.info("商户{},盘点单号：{}, 语句序号：{}，reason:{},",
                    siteId, pandianNum, billid, maplist.get(0).get("erp_data_seq"), e.getMessage());
            }
        }
    }

    public List<Integer> getUnsyncCheck(Integer siteId, String pandianNum, String uid) {
        try {
            if (pdErpSiteIdList.contains(siteId)) {//查询盘点号和任务号对应
                List<StoreOrderNum> storeOrderNums = getJdbcTemplateBysiteId(siteId).query(sql_selectByPandianNum,
                    new Object[]{pandianNum, uid}, new BeanPropertyRowMapper<>(StoreOrderNum.class));
                if (!CollectionUtils.isEmpty(storeOrderNums)) {
                    return getJdbcTemplateBysiteId(siteId).queryForList(sql_ActqtyIsNull, new Object[]{storeOrderNums.get(0).getBillid()}, Integer.class);
                }
            }
        } catch (Exception e) {
            logger.info("查询未erp盘点数据出错[updateOfflinetqty]，siteId:{},盘点单号:{},reason:{}", siteId, pandianNum, e);
            erpToolsService.sendError(siteId, "盘点接口对接，【updateOfflinetqty】", "问题：" + e.getMessage(),
                e.toString(), 4);
        }
        return null;
    }
}
