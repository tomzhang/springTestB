package com.jk51.modules.sms.controller;

import com.jk51.commons.ccprest.result.BaseResult;
import com.jk51.modules.sms.service.Sms7MoorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-22
 * 修改记录:
 */
@Controller
@RequestMapping("/qimoor")
public class Sms7MoorController {

    @Autowired
    private Sms7MoorService _7moorSmsService;

    @ResponseBody
    @RequestMapping("/sms")
    public BaseResult _7moorSmsSent(String num, String ... var) {
        return _7moorSmsService._7moorSentSms(num, var);
    }

    /**
     * 7moor语音验证码
     * @param exten
     * @param vcode
     * @return
     */
    @ResponseBody
    @RequestMapping("/webcall")
    public BaseResult _7moorWebCall(String exten ,String  vcode){
        return _7moorSmsService._7MoorWebCall(exten,vcode);
    }

    @ResponseBody
    @RequestMapping("/prompt")
    public Map<String,Object> voicePrompt(String exten){
        return _7moorSmsService.webcall(exten,0);
    }

}
