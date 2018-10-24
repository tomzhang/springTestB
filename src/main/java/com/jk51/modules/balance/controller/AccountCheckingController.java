package com.jk51.modules.balance.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.balance.service.AccountCheckingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/9/21.
 */
@Controller
@RequestMapping("/checking")
public class AccountCheckingController {
    private static final Logger log = LoggerFactory.getLogger(AccountCheckingService.class);
    @Autowired
    private AccountCheckingService accountCheckingService;

    @RequestMapping(value="/getMerchantTradesChecking")
    @ResponseBody
    public Map<String,Object> getMerchantTradesChecking(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return accountCheckingService.getMerchantTradesChecking(param);
    }

    /**
     * 商户后台：批量导入资金文件
     * @param request
     * @return
     */
    @RequestMapping(value="/addCheckingData")
    @ResponseBody
    public Map<String, Object> addCheckingData(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        return accountCheckingService.addCheckingData(param);
    }

    @RequestMapping(value="/getMerchantFunds")
    @ResponseBody
    public Map<String,Object> getMerchantFunds(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return accountCheckingService.getMerchantFunds(param);
    }

    @RequestMapping(value="/getStoreFunds")
    @ResponseBody
    public Map<String,Object> getStoreFunds(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return accountCheckingService.getStoreFunds(param);
    }

    @RequestMapping(value="/timeingMerchantFunds")
    @ResponseBody
    public void timeingMerchantFunds(HttpServletRequest request) {
        accountCheckingService.timeingMerchantFunds();
    }

    @RequestMapping(value="/timeingStoreFunds")
    @ResponseBody
    public void timeingStoreFunds(HttpServletRequest request) {
        accountCheckingService.timeingStoreFunds();
    }

}
