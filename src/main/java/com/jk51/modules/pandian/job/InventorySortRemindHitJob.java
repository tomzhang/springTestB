package com.jk51.modules.pandian.job;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.modules.pandian.param.InventorySortRemindHitMassege;
import com.jk51.modules.pandian.service.PandianRemindHitService;
import com.jk51.mq.mns.CloudQueueFactory;
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
 * 创建日期: 2018/7/2
 * 修改记录:
 */

@Component
@RunMsgWorker(queueName = "remindHit11" ,num = 3)
public class InventorySortRemindHitJob implements MessageWorker {

    public static final String QUEUE_NAME = "remindHit11";
    private static Logger logger = LoggerFactory.getLogger(InventorySortRemindHitJob.class);

    @Autowired
    private PandianRemindHitService pandianRemindHitService;

    @Override
    public void consume(Message message) throws Exception {

        try{

            String messageBodyAsString = message.getMessageBodyAsString();
            InventorySortRemindHitMassege sortRemindHitMassege = JacksonUtils.json2pojo(messageBodyAsString,InventorySortRemindHitMassege.class);

            pandianRemindHitService.recodeInfo(sortRemindHitMassege);

        }catch (Exception e){

            logger.error("InventorySortRemindHitJob 消费异常： message:{}, 异常信息：{}",message,ExceptionUtil.exceptionDetail(e));
        }

    }

    public static void sendMessage(InventorySortRemindHitMassege massege){
        try {

            CloudQueue queue =  CloudQueueFactory.create(InventorySortRemindHitJob.QUEUE_NAME);
            Message message = new Message(JacksonUtils.obj2json(massege).getBytes());
            queue.putMessage(message);

        } catch (Exception e) {
            logger.error("发送盘点排序消息失败：statusParams：{},报错信息：{}",massege,ExceptionUtil.exceptionDetail(e));
        }
    }


}
