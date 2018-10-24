package com.jk51.modules.account.controller;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.account.service.AccountPayCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author: chen
 * @Description:
 * @Date: created in 2018/9/27
 * @Modified By:
 */
@Controller
@RequestMapping("/account/")
public class AccountPayCheckController {
    private static final Logger logger =  LoggerFactory.getLogger(AccountPayCheckController.class);

    @Autowired
    private AccountPayCheckService payCheckService;

    @RequestMapping("check")
    public @ResponseBody PageInfo getPayCheck(HttpServletRequest request){

        Map<String, Object> parms = ParameterUtil.getParameterMap(request);

        return payCheckService.getPayCheck(parms);
    }

}
