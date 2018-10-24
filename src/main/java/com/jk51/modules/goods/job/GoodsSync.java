package com.jk51.modules.goods.job;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.aliyun.mns.model.Message;
import com.jk51.annotation.MsgConsumer;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.goods.event.models.SyncQueue;
import com.jk51.modules.goods.service.SyncTaskService;
import com.jk51.mq.MsgConsumeException;
import com.jk51.mq.consumer.Consumer;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RunMsgWorker(queueName = "GoodsTopic")
public class GoodsSync implements MessageWorker{
    @Autowired
    SyncTaskService syncTaskService;

    @Autowired
    Environment env;
    private static Logger logger = LoggerFactory.getLogger(GoodsSync.class);

    public static final String MQTOPIC = "GoodsTopic";

    @Override
    public void consume(Message message) throws Exception {
        logger.info("收到新的消息");
        // 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
        // 处理消息
        String content = message.getMessageBodyAsString();
        logger.info("{} {} {} {}",message.getReceiptHandle(),content, message.getMessageId(), message.getMessageId());

        try {
            SyncQueue data = JacksonUtils.json2pojo(content, SyncQueue.class);
            handle(data);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            // 抛出一个异常消费失败
        }
    }

    private void handle(SyncQueue data) throws Exception {
        // 从数据库中查出记录
        Map<String, String> goodsInfo = findResult(data.getGoodsId(), data.getSiteId());
        if (goodsInfo != null) {
            syncTaskService.addSyncRecord(goodsInfo, data.getSiteId());
        }
    }

    /**
     * 查找商家商品数据
     * @param goodsId 商品id
     * @param siteId 商家id
     * @return
     */
    private Map<String, String> findResult(int goodsId, int siteId) {
        Map<String, Object> param = new HashMap();
        param.put("goodsId", goodsId);
        param.put("siteId", siteId);

        return syncTaskService.findGoods((param));
    }

    /**
     * 两个数据在允许更新字段上是否有不同
     * @param shopGoodsInfo 商家数据
     * @param sourceGoodsInfo ybzf数据
     * @param allowFields 允许更新字段
     * @return
     */
    public static boolean hasDiffDataOnAllowUpdateField(Map<String, ?> shopGoodsInfo, Map sourceGoodsInfo, String[] allowFields) {
        for (String field : allowFields) {
            try {
                String shopValue = (String)shopGoodsInfo.get(field);
                String ybzfValue = (String)sourceGoodsInfo.get(field);

                if (! StringUtil.equals(shopValue, ybzfValue)) {
                    logger.info("商品数据对比 {} {} {} {}", shopGoodsInfo.get("goodsId"), field, shopValue, ybzfValue);
                    // 有数据不同
                    return true;
                }
            } catch (Exception e) {
                //
            }
        }

        return false;
    }
}
//@MsgConsumer(
//    topicName = "GoodsTopic",
//    tagName = GoodsSync.YIB_GOODS_SYNC_TAG,
//    consumeType = MsgConsumer.ConsumeType.Orderly,
//    consumerGroup = "GoodsConsumerGroup"
//)
//public class GoodsSync implements Consumer {
//    public static final String YIB_GOODS_SYNC_TAG = "yib_goods_sync";
//    public static final String MQTOPIC = "GoodsTopic";
//
//    private static Logger logger = LoggerFactory.getLogger(GoodsSync.class);
//
//    @Autowired
//    SyncTaskService syncTaskService;
//
//    @Override
//    public void consume(List<MessageExt> msgs) throws MsgConsumeException {
//        logger.info("收到新的消息");
//        // 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
//        MessageExt msg = msgs.get(0);
//        // 处理消息
//        String content = new String(msg.getBody());
//        logger.info("{} {} {} {}", msg.getMsgId(), msg.getQueueOffset(), msg.getQueueId(), content);
//
//        try {
//            SyncQueue data = JacksonUtils.json2pojo(content, SyncQueue.class);
////              JSONObject data = (JSONObject) JSONObject.parse(content);
//            handle(data);
//        } catch (Exception e) {
//            logger.debug(e.getMessage());
//            // 抛出一个异常消费失败
//            throw new MsgConsumeException(e.getMessage());
//        }
//    }
//
//    private void handle(SyncQueue data) throws Exception {
//        // 从数据库中查出记录
//        Map<String, String> goodsInfo = findResult(data.getGoodsId(), data.getSiteId());
//        if (goodsInfo != null) {
//            syncTaskService.addSyncRecord(goodsInfo, data.getSiteId());
//        }
//    }
//
//    /**
//     * 查找商家商品数据
//     * @param goodsId 商品id
//     * @param siteId 商家id
//     * @return
//     */
//    private Map<String, String> findResult(int goodsId, int siteId) {
//        Map<String, Object> param = new HashMap();
//        param.put("goodsId", goodsId);
//        param.put("siteId", siteId);
//
//        return syncTaskService.findGoods((param));
//    }
//
//    /**
//     * 两个数据在允许更新字段上是否有不同
//     * @param shopGoodsInfo 商家数据
//     * @param sourceGoodsInfo ybzf数据
//     * @param allowFields 允许更新字段
//     * @return
//     */
//    public static boolean hasDiffDataOnAllowUpdateField(Map<String, ?> shopGoodsInfo, Map sourceGoodsInfo, String[] allowFields) {
//        for (String field : allowFields) {
//            try {
//                String shopValue = (String)shopGoodsInfo.get(field);
//                String ybzfValue = (String)sourceGoodsInfo.get(field);
//
//                if (! StringUtil.equals(shopValue, ybzfValue)) {
//                    logger.info("商品数据对比 {} {} {} {}", shopGoodsInfo.get("goodsId"), field, shopValue, ybzfValue);
//                    // 有数据不同
//                    return true;
//                }
//            } catch (Exception e) {
//                //
//            }
//        }
//
//        return false;
//    }
//}
