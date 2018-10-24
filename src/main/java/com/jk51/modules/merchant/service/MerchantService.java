package com.jk51.modules.merchant.service;

import com.github.pagehelper.PageHelper;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.SManager;
import com.jk51.model.order.SMerchant;
import com.jk51.model.treat.MerchantExtTreat;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.persistence.mapper.SManagerMapper;
import com.jk51.modules.persistence.mapper.SMerchantMapper;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import com.jk51.modules.treat.mapper.MerchantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-06-02
 * 修改记录:
 */
@Service
public class MerchantService {
    @Autowired
    private SMerchantMapper sMerchantMapper;
    @Autowired
    private SManagerMapper sManagerMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private YbMerchantMapper ybMerchantMapper;

    public List<SMerchant> getMerchants(Integer siteId, String username, String password) {
        return sMerchantMapper.getMerchants(siteId, username, password);
    }

    public List<SManager> getUsername(Integer siteId, String username, String password) {
        return sManagerMapper.getUserName(siteId, username, password);
    }
    public Integer merchantUpdatePassword(Integer siteId, Integer userId, String password) {
        return sMerchantMapper.updatePassword(siteId, userId, password);
    }
    public Integer managerUpdatePassword(Integer siteId, Integer userId, String password) {
        return sManagerMapper.updatePassword(siteId, userId, password);
    }

    public Map<String,Object> getMerchantBySiteId(int siteId) {
        return merchantMapper.selectInfoByMerchantId(siteId);
    }

    public String queryMerchantName(Integer siteId) {
        return sMerchantMapper.queryMerchantName(siteId);
    }

    public int editDefaulto2o(String siteId, String metaVal){
        if(StringUtil.isEmpty(ybMerchantMapper.queryDefaulto2o(siteId))){
            return ybMerchantMapper.insertDefaulto2o(siteId, metaVal);
        }else {
            return ybMerchantMapper.updateDefaulto2o(siteId, metaVal);
        }
    }

    public List<Map<String, Object>> getWXStoreQRUrl(Integer siteId, String storeName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return merchantMapper.getWXStoreQRUrl(siteId, storeName);
    }
}
