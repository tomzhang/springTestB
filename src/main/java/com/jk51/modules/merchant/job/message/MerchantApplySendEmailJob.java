package com.jk51.modules.merchant.job.message;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.modules.merchant.utils.EmailTools;
import com.jk51.mq.mns.CloudQueueFactory;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：开站发送邮件
 * 作者: XC
 * 创建日期: 2018-09-30 16:06
 * 修改记录:
 **/

@Component
@RunMsgWorker(queueName = "sendOpenStationEmail1")
public class MerchantApplySendEmailJob implements MessageWorker {
    @Autowired
    private EmailTools emailTools;

    public static final String QUEUE_NAME = "sendOpenStationEmail1";
    private static Logger logger = LoggerFactory.getLogger(MerchantApplySendEmailJob.class);

    @Override
    public void consume(Message message) throws Exception {
        try{

            String messageBodyAsString = message.getMessageBodyAsString();
            MerchantApplySendEmailJobMessage masejMassege = JacksonUtils.json2pojo(messageBodyAsString,MerchantApplySendEmailJobMessage.class);
            if(masejMassege.getSmd()!=null){
                emailTools.sendMailTo51(masejMassege.getSmd());
            }
            if(masejMassege.getSsmmd()!=null){
                emailTools.sendSuccessMailToMerchant(masejMassege.getSsmmd());
            }
            if (masejMassege.getSfmmd()!=null){
                emailTools.sendFailMailToMerchant(masejMassege.getSfmmd());
            }

        }catch (Exception e){

            logger.error("MerchantApplySendEmailJob 消费异常： message:{}, 异常信息：{}",message,ExceptionUtil.exceptionDetail(e));
        }
    }

    public void sendMail(MerchantApplySendEmailJobMessage massege){
        try {

            CloudQueue queue =  CloudQueueFactory.create(MerchantApplySendEmailJob.QUEUE_NAME);
            Message message = new Message(JacksonUtils.obj2json(massege).getBytes());
            queue.putMessage(message);

        } catch (Exception e) {
            logger.error("发送开站邮件消息失败：statusParams：{},报错信息：{}",massege,ExceptionUtil.exceptionDetail(e));
        }
    }
}
