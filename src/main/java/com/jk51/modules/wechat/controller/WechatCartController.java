package com.jk51.modules.wechat.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.merchant.MerchantExt;
import com.jk51.model.order.SBMember;
import com.jk51.modules.appInterface.util.ResultMap;
import com.jk51.modules.merchant.service.MerchantExtService;
import com.jk51.modules.wechat.service.WechatCartService;
import com.jk51.modules.wechat.service.WechatMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Controller
@RequestMapping("/wechat")
public class WechatCartController {
    private Logger log = LoggerFactory.getLogger(WechatCartController.class);
    @Autowired
    private WechatCartService wechatCartService;
    @ResponseBody
    @RequestMapping("/updateWxCart")
    public Map<String,Object> updateWxCart(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        log.info("记录购物车数据updateWxCart:{}",param);
        return wechatCartService.updateWxCart(param);
    }
    @ResponseBody
    @RequestMapping("/delWxCart")
    public Map<String,Object> delWxCart(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        log.info("记录购物车数据delWxCart:{}",param);
        return wechatCartService.delWxCart(param);
    }
    @ResponseBody
    @RequestMapping("/getWxCart")
    public Map<String,Object> getWxCart(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        log.info("记录购物车数据getWxCart:{}",param);
        return wechatCartService.getWxCart(param);
    }

}
