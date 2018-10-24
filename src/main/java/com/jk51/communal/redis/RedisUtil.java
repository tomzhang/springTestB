package com.jk51.communal.redis;

import com.jk51.commons.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/4/22
 * 修改记录:
 */
@Service
public class RedisUtil {

    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    private static RedisTemplate redisTemplate;

    public static Set<String> setGetAllValue(String key) {

        return redisTemplate.opsForSet().members(key);
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate redisTemplate){
        RedisUtil.redisTemplate = redisTemplate;
    }

    /**
     * @param pattern
     * @return Set<String>
     *     根据pattern返回Reids 中匹配的键集合
     * */
    public static Set<String> scan(String pattern){

        return redisTemplate.keys(pattern);
      /* if(StringUtil.isEmpty(pattern)){
           throw new IllegalArgumentException("pattern 不能为空");
       }

        RedisConnection connection = stringRedisTemplate.getConnectionFactory().getConnection();

        Set<String> result = new HashSet();
        ScanOptions scanOptions = ScanOptions.scanOptions()
            .match(pattern)
            .build();

        boolean done = false;

        while (!done){

            Cursor c = connection.scan(scanOptions);
            while (c.hasNext()){
                result.add(new String((byte[]) c.next()));
            }
            done = true;
        }

        return  result;*/
    }




    public static void setAdd(String key,String value){

        Long num =  redisTemplate.opsForSet().add(key,value);

        logger.info("redis setAdd,key:{},value:{},num:{}",key,value,num);
    }

    public static void setAdd(String key,String... value){
        Long num =  redisTemplate.opsForSet().add(key,value);
        logger.info("redis setAdd,key:{},value:{},num:{}",key,value,num);
    }

    public static void setRemove(String key,String ... value){
        Long num =  redisTemplate.opsForSet().remove(key,value);
        logger.info("redis setRemove,key:{},value:{},num:{}",key,value,num);
    }

    public static void setExpire(String key, final long timeout, final TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

}
