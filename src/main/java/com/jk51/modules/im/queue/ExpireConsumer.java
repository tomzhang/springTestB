package com.jk51.modules.im.queue;

import com.aliyun.mns.model.Message;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.modules.im.expires.ExpireRedisKeyManager;
import com.jk51.modules.im.service.IMExpireService;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.jk51.modules.im.util.ImExpireConstant.MSG_CONTENT;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/8/27
 * 修改记录:
 */
@Component
@RunMsgWorker(queueName = "pandianExpire31")
public class ExpireConsumer implements MessageWorker {

    private Logger logger = LoggerFactory.getLogger(ExpireConsumer.class);

    @Autowired
    private IMExpireService imExpireService;
    @Autowired
    private ExpireRedisKeyManager expireKey;

    @Override
    public void consume(Message message) throws Exception {

        String key = message.getMessageBody();
        logger.info("收到会员过期消息：key：{}",key);
        if(expireKey.exits(key)){

            String value =  expireKey.getValue(key,MSG_CONTENT);
            try{
                imExpireService.handleEvent(value);
            }catch (Exception e){
                logger.error("聊天过期事件处理异常,message:{},key:{},value:{},报错信息{}",message,key,value,ExceptionUtil.exceptionDetail(e));
            }

        }else{
            logger.info("不存在key：key：{}",key);
        }

        expireKey.delete(key);
    }
}
