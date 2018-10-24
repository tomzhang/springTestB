package com.jk51.modules.pandian.job;

import com.aliyun.mns.model.Message;
import com.jk51.annotation.MsgConsumer;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.pandian.dto.PandianTimeDto;
import com.jk51.modules.pandian.service.PandianTimeService;
import com.jk51.mq.mns.CloudQueueFactory;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:  同过消息队列异步保存盘点过程中的各个时间点
 * 作者: gaojie
 * 创建日期: 2018-03-30
 * 修改记录:
 */
@Component
@RunMsgWorker(queueName = "PandianTimeTopic")
public class PandianTimeRecoderJob implements MessageWorker {


    public static final String MQ_TOPIC_PANDIAN_TIME = "PandianTimeTopic";

    private Logger logger = LoggerFactory.getLogger(PandianTimeRecoderJob.class);

    @Autowired
    private PandianTimeService pandianTimeService;

    @Override
    public void consume(Message message) throws Exception {



       String bodyStr = message.getMessageBody();
       logger.debug("收到盘点时间记录消息，{}",bodyStr);

        try{

            PandianTimeDto timeDto = JacksonUtils.json2pojo(bodyStr,PandianTimeDto.class);
            pandianTimeService.insert(timeDto);

        }catch (Exception e){

            logger.info("盘点时间消息处理异常，{}",e.getMessage());
        }

    }
}
