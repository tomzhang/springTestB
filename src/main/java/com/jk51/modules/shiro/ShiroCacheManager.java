package com.jk51.modules.shiro;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.ShiroException;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 自定义缓存管理器,用于集成redis缓存
 * 作者: wangzhengfei
 * 创建日期: 2017-03-11
 * 修改记录:
 */
@Component
public class ShiroCacheManager implements CacheManager, Initializable, Destroyable {

    private static final Logger logger = LoggerFactory.getLogger(ShiroCacheManager.class);

    @Autowired
    private StringRedisTemplate template;

    private static final String CACHE_PREFIX = "shiro_redis_cache:";

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        if(logger.isDebugEnabled()){
            logger.debug("shiro get redis cache key:[{}]",name);
        }
        String value = template.opsForValue().get(name);
        if(StringUtils.isEmpty(value)){
            return null;
        }
        String key = CACHE_PREFIX+value;
        return JSONObject.parseObject(key,ShiroCache.class);
    }

    @Override
    public void destroy() throws Exception {
        //原则上需删除所有redis缓存中的权限认证keys,集群环境下建议Do Nothing.
        logger.info("shiro cache manager destroy...");
    }

    @Override
    public void init() throws ShiroException {
        logger.info("shiro cache manager init...");
    }
}
