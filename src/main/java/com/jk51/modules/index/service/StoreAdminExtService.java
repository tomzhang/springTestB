package com.jk51.modules.index.service;

import com.jk51.model.StoreAdminExt;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-17
 * 修改记录:
 */
@Service
public class StoreAdminExtService {

    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;

    public List<StoreAdminExt> getStoreAdminExtBySiteIdAndStoreAdminId(int site_id, int storeadmin_id) {
        return storeAdminExtMapper.getStoreAdminExtBySiteIdAndStoreAdminId(site_id,storeadmin_id);
    }

    //查询店员的门店名称，店员名称，店员职称
    public List<Map<String,String>> getSoreAdminInfo(String site_id,String store_admin_id){
        return  storeAdminExtMapper.getStoreAdminInfo(site_id,store_admin_id);
    }
}
