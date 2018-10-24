package com.jk51.modules.pandian.elasticsearch.service;

import com.jk51.model.Inventories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.jk51.modules.pandian.elasticsearch.util.Constant.ES_IVENTORY_ID_REDIS_KEY;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/6/1
 * 修改记录:
 */
@Service
public class InventoryBuilder {

    @Autowired
    private static StringRedisTemplate redisTemplate;

    @Autowired
    private void setRedisTemplate(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public static IndexQuery buildIndex(Inventories i){

        long id = redisTemplate.opsForValue().increment(ES_IVENTORY_ID_REDIS_KEY,1);
        i.setId(Integer.valueOf((int) id));
        i.setEsId(Integer.valueOf((int) id));
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setObject(i);
        return indexQuery;
    }
}
