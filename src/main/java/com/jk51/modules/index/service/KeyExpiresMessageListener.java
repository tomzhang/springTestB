package com.jk51.modules.index.service;


import com.jk51.commons.string.StringUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.modules.im.service.IMExpireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-24
 * 修改记录:
 */
public class KeyExpiresMessageListener implements MessageListener {

    @Autowired
    public IMExpireService imExpireService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static final Logger logger = LoggerFactory.getLogger(KeyExpiresMessageListener.class);

    private RedisTemplate<String,String> redisTemplate;

    public KeyExpiresMessageListener() {
    }

    public void setRedisTemplate( RedisTemplate<String,String> redisTemplate){
        this.redisTemplate = redisTemplate;
    }
    @Override
    public void onMessage(Message message, byte[] pattern) {
        byte[] body = message.getBody();

        String key = new String(body);
        String value = stringRedisTemplate.opsForList().rightPop(key+"-list");
        stringRedisTemplate.delete(key+"-list");

        if(value==null){
            return;
        }

        //聊天中咨询与一键呼叫超时处理
           if(isIMKey(value)){

            try{
                imExpireService.handleEvent(value);
            }catch (Exception e){
                logger.error("聊天过期事件处理异常,redisKey:{},value:{},报错信息{}",key,value,ExceptionUtil.exceptionDetail(e));
            }

        }
    }



    //判断过期键是否为IM过期键,判断是否以"{\\\"title\\\":"开始，并且能解析成RedisKey实例
    public boolean isIMKey(String redisKey){

        if(redisKey.contains("title")&&imExpireService.redisKeyParse(redisKey)!=null){
            return true;
        }else{
            return false;
        }

    }


}
