package com.jk51.modules.pandian.job;

import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.model.PandianSortRemindHitRate;
import com.jk51.modules.pandian.dto.NotHasRemindCountRate;
import com.jk51.modules.pandian.dto.RemindHitCountRate;
import com.jk51.modules.pandian.mapper.InventorySortNotHasRemindMapper;
import com.jk51.modules.pandian.mapper.InventorySortRemindHitMapper;
import com.jk51.modules.pandian.mapper.PandianSortRemindHitRateMapper;
import com.jk51.modules.pandian.param.StatusParam;
import com.jk51.modules.pandian.service.PandianOrderRedisManager;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/5/10
 * 修改记录:
 */

@Component
@RunMsgWorker(queueName = "pandianSortTopic11")
public class PandianSortJob implements MessageWorker {


    public static final String MQ_TOPIC_PANDIAN_SORT = "pandianSortTopic11";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PandianOrderRedisManager redisManager;
    @Autowired
    private PandianSortRemindHitRateMapper pandianSortRemindHistRateMapper;
    @Autowired
    private InventorySortNotHasRemindMapper sortNotHasRemindMapper;
    @Autowired
    private InventorySortRemindHitMapper sortRemindHitMapper;

    @Override
    public void consume(Message message) throws Exception {

        try{

            String body = message.getMessageBody();
            logger.info("收到盘点排序消息：{}",body);

            StatusParam param = JacksonUtils.json2pojo(body,StatusParam.class);

            redisManager.mergeZset(param);
            statisticsPandianSortRemindHit(param);

        }catch (Exception e){

            logger.error("盘点消息处理异常：{}",ExceptionUtil.exceptionDetail(e));
        }

    }

    //统计盘点推荐正确率
    private void statisticsPandianSortRemindHit(StatusParam param){

       pandianSortRemindHistRateMapper.insert(getPandianSortRemindHistRate(param));
    }

    private PandianSortRemindHitRate getPandianSortRemindHistRate(StatusParam param){

        NotHasRemindCountRate notHasRemindCountRate =  sortNotHasRemindMapper.countRate(param);
        RemindHitCountRate remindHitCountRate = sortRemindHitMapper.countRate(param);

        PandianSortRemindHitRate result = new PandianSortRemindHitRate();
        result.setPandianNum(param.getPandian_num());
        result.setSiteId(param.getSiteId());
        result.setStoreId(param.getStoreId());
        result.setSameNum(remindHitCountRate.getSameNum());
        result.setNotSameNum(remindHitCountRate.getNotSameNum());
        result.setNotRemindNum(remindHitCountRate.getNotRemindNum());
        result.setNextChecked(notHasRemindCountRate.getNextChecked());
        result.setNextNotInInventory(notHasRemindCountRate.getNextNotInInventory());
        result.setNextNotInRedis(notHasRemindCountRate.getNextNotInRedis());
        result.setCurrentNotInRedis(notHasRemindCountRate.getCurrentNotInRedis());
        result.setHasRemind(notHasRemindCountRate.getHasRemind());

        return result;
    }
}
