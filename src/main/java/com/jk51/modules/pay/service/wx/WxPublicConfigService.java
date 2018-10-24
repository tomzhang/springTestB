package com.jk51.modules.pay.service.wx;

import com.jk51.model.pay.WxPublicConfig;
import com.jk51.modules.pay.mapper.WxPublicConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-10
 * 修改记录:
 */
@Service
public class WxPublicConfigService {
    @Autowired
    WxPublicConfigMapper wxPublicConfigMapper;

    public WxPublicConfig findConfigBySiteId(Integer siteId) {
        return wxPublicConfigMapper.findConfigBySiteId(siteId);
    }
}
