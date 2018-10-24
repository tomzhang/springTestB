package com.jk51.modules.offline.service;

import com.alibaba.fastjson.JSONArray;
import com.jk51.commons.json.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-07-03
 * 修改记录:
 */
@Service
public class TimeService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取缓存中多价格一共有多少条数据
     *
     * @param siteId
     * @return
     */
    public int getErpPriceList(Integer siteId) {
        int counts = 0;
        try {
            Long size = stringRedisTemplate.opsForList().size(siteId + "_erpStorePrice");
            for (int i = 0; i < size; i++) {
                counts += JSONArray.parseArray(JacksonUtils.obj2json(
                    JacksonUtils.json2map(stringRedisTemplate.opsForList().index(siteId + "_erpStorePrice", i)).get("goodlist")), Map.class).size();
            }
            return counts;
        } catch (Exception e) {

        }
        return counts;
    }
}
