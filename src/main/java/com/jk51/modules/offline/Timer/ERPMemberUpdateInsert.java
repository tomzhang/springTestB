package com.jk51.modules.offline.Timer;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.mq.mns.CloudQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/19.
 */
@Async
@Component
public class ERPMemberUpdateInsert implements ApplicationListener<ERPMemberEvent> {

    private static final Logger log = LoggerFactory.getLogger(ERPMemberEvent.class);

    @Override
    public void onApplicationEvent(ERPMemberEvent event) {
        Map messageMap = new HashMap();
        messageMap.put("messageType", event.getString());
        CloudQueue queue = CloudQueueFactory.create(ERPMemberSynce.ERPMember);
        Message message = new Message();
        message.setMessageBody(JSON.toJSONString(messageMap));
        try {
            Message result = queue.putMessage(message);
            log.info("成功加入队列：" + ERPMemberSynce.ERPMember);
        } catch (Exception e) {
            log.info("插入失败:{}",e);
        }
    }
}
