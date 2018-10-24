package com.jk51.modules.privatesend.service;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.exception.BusinessLogicException;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.privatesend.core.AliPayMerchantConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AliPayOpenPublicService {

    private static final Logger logger = LoggerFactory.getLogger(AliPayOpenPublicService.class);
    @Autowired
    private YbMerchantMapper ybMerchantMapper;

    public AliPayMerchantConfig getAliPayMerchantConfig(String siteId) throws BusinessLogicException {
        Map result = ybMerchantMapper.queryAliPayInfo(siteId);
        AliPayMerchantConfig aliPayMerchantConfig = JacksonUtils.map2pojo(result, AliPayMerchantConfig.class);
        if ("0".equals(aliPayMerchantConfig.getAlipay_flag())) {
            throw new BusinessLogicException("未开通支付宝生活号");
        }
        return aliPayMerchantConfig;
    }

    public AlipayClient getAliPayClient(AliPayMerchantConfig aliPayMerchantConfig) {
        return new DefaultAlipayClient(AliPayMerchantConfig.URL, aliPayMerchantConfig.getAlipay_appid(), aliPayMerchantConfig.getAlipay_privatekey(), AliPayMerchantConfig.FORMAT, AliPayMerchantConfig.CHARSET, aliPayMerchantConfig.getAlipay_publickey(), AliPayMerchantConfig.SIGNTYPE);
    }

}
