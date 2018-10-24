package com.jk51.modules.wechat.service;

import com.jk51.model.BMobileWechat;
import com.jk51.modules.appInterface.mapper.BMobileWechatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-31
 * 修改记录:
 */
@Service
public class BMobileService {
    @Autowired
    private BMobileWechatMapper bMobileWechatMapper;

    public List<String> findMobile(String userId, Integer siteId) {
        return bMobileWechatMapper.findMobile(userId, siteId);
    }

    public Map<String,Object> findBMobileWechat(String mobile, Integer siteId) {
        return bMobileWechatMapper.findBMobileWechat(mobile, siteId);
    }

    public Integer insert(Map<String, Object> map) {
        return bMobileWechatMapper.insert(map);
    }

    public Integer updateByPrimaryKey(Map<String, Object> map) {
        return bMobileWechatMapper.updateByPrimaryKey(map);
    }
}
