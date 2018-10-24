package com.jk51.modules.index.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.StoreAdmin;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-31
 * 修改记录:
 */
@Service
public class StoreAdminService {

    @Autowired
    private StoreAdminMapper storeAdminMapper;

    //@Cacheable(value="storeAdmin",keyGenerator = "defaultKeyGenerator")
    public List<StoreAdmin> findStoreAdminBySiteId(int site_id) {

       return storeAdminMapper.findStoreAdminBySiteId(site_id);
    }
}
