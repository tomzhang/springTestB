package com.jk51.mq;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.jk51.annotation.MsgConsumer;
import com.jk51.mq.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件名:com.jk51.mq.DemoConsumer
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-02-10
 * 修改记录:
 */
@Component
@MsgConsumer(topicName = "OrderTopic",tagName = "AAA")
public class DemoConsumer implements Consumer {

    private static Logger logger = LoggerFactory.getLogger(DemoConsumer.class);

    @Override
    public void consume(List<MessageExt> msgs) {
        for(MessageExt ext :msgs){
            logger.info(ext.getMsgId()+","+new String(ext.getBody()));
        }
    }
}
