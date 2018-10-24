package com.jk51.modules.wechat.service;

import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.integral.service.IntegralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 积分
 */

@Service
public class WechatIntegralService {

    private static final Logger logger = LoggerFactory.getLogger(WechatIntegralService.class);


    @Autowired
    private IntegralService integralService;

    /**
     * 注册送积分
     * 必须参数，siteId, buyerId, buyerNick
     *
     * @param param
     * @return
     */
    public Map integral4Regist(Map param) {
        JSONObject result = integralService.integralAddForRegister(param);
        if (!StringUtil.isEmpty(result) && "success".equals(result.get("status"))) {
            logger.info("成功调用###注册送积分###接口");
            return result;
        }
        logger.error("调用###注册送积分###接口失败：{}", JSONObject.toJSON(result));
        return result;
    }


}
