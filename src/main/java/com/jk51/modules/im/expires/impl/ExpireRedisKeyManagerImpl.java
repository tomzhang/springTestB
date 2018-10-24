package com.jk51.modules.im.expires.impl;

import com.jk51.modules.im.expires.ExpireRedisKeyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/8/28
 * 修改记录:
 */
@Service
public class ExpireRedisKeyManagerImpl implements ExpireRedisKeyManager {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public static int SIX_MINUTE = 6 * 60;

    @Override
    public boolean exits(String key) {
         redisTemplate.hasKey(key);
         return redisTemplate.hasKey(key);
    }

    @Override
    public void addKey(String key, Map<String,String> map) {
        redisTemplate.opsForHash().putAll(key,map);
        redisTemplate.expire(key,SIX_MINUTE,TimeUnit.SECONDS);
    }

    @Override
    public void delete(String str) {
        redisTemplate.delete(str);
    }

    @Override
    public String getValue(String key,String hashKey) {
        return (String) redisTemplate.opsForHash().get(key,hashKey);
    }
}
