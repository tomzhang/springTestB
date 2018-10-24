package com.jk51.modules.shiro;

import com.alibaba.fastjson.JSONObject;
import com.jk51.modules.goods.library.SpringContextUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.Set;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-03-11
 * 修改记录:
 */


public class ShiroCache implements Cache {



    @Override
    public Object get(Object key) throws CacheException {
        ApplicationContext context = SpringContextUtil.getApplicationContext();
        StringRedisTemplate template = context.getBean(StringRedisTemplate.class);
        return template.opsForValue().get(key);
    }

    @Override
    public Object put(Object key, Object value) throws CacheException {
        ApplicationContext context = SpringContextUtil.getApplicationContext();
        StringRedisTemplate template = context.getBean(StringRedisTemplate.class);
        if(!(value instanceof String) && value != null){
            value = JSONObject.toJSONString(value);
        }
        template.opsForValue().set((String)key,(String)value);
        return value;
    }

    @Override
    public Object remove(Object key) throws CacheException {
        return null;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set keys() {
        return null;
    }

    @Override
    public Collection values() {
        return null;
    }
}
