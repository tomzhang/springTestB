package com.jk51.modules.im.netease;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.im.netease.config.Config;
import com.jk51.modules.im.netease.param.SendMsgParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.jk51.modules.im.netease.Constant.NETEASE;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/1
 * 修改记录:
 */
@Service
public class Util {

    @Autowired
    private Config config;

    @Autowired
    private StringRedisTemplate redisTemplate;


    public Map<String,String> getHeaderMap(){
        Map<String,String> result = new HashMap<>();
        result.put("AppKey",config.getAppkey());
        result.put("Nonce",config.getNonce());
        String curTime = getCurTime();
        result.put("CurTime", curTime);
        result.put("CheckSum",CheckSumBuilder.getCheckSum(config.getAppSecret(),config.getNonce(),curTime));
        return result;
    }

    private String getCurTime(){
        return String.valueOf((new Date()).getTime() / 1000L);
    }

    public boolean neteaseActive(){

        boolean result = false;
        String im = redisTemplate.opsForValue().get("im");
        if(NETEASE.equals(im)){
            result = true;
        }

        return result;
    }

    public SendMsgParam getSendMsgParam(String from,String to,String msgContent){

        SendMsgParam result = new SendMsgParam();
        result.setFrom(from);
        result.setTo(to);

        Map<String,String> body = new HashMap<>();
        body.put("msg",msgContent);
        result.setBody(JacksonUtils.mapToJson(body));

        return result;
    }




}
