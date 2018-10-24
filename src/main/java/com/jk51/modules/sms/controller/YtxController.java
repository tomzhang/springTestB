package com.jk51.modules.sms.controller;

import com.jk51.commons.ccprest.result.BaseResult;
import com.jk51.modules.sms.service.YtxSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-17
 记录:
 */
@Controller
@RequestMapping("/yxtsms")
public class YtxController {

    @Autowired
    private YtxSmsService ytxSmsServicel;

    @ResponseBody
    @RequestMapping("/regcode")
    public BaseResult sendRegCode(String tel, String regCode ,String verifyName){
        return ytxSmsServicel.sendRegCodeByYtx(tel,regCode,verifyName);
    }

    @ResponseBody
    @RequestMapping("/message")
    public BaseResult sendMsg(String tel ,String verifyName){
        return ytxSmsServicel.sendMsgByYtx(tel,verifyName);
    }
}
