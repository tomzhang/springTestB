package com.jk51.modules.login.controller;

import com.jk51.commons.message.ReturnDto;
import com.jk51.model.login.MerchantLoginParams;
import com.jk51.modules.login.service.LoginService;
import com.jk51.modules.sms.service.YpSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     : 平台登陆，注册
 * 作者     : zhangduoduo
 * 创建日期 : 2017/2/27-02-27
 * 修改记录 :
 */
@RestController
@RequestMapping("login")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;
    @Autowired
    private YpSmsService ypSmsService;


    /**
     * 51和商家后台登陆
     *
     * @param
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/merchant",consumes = "application/json")
    public ReturnDto merchantLogin( @RequestBody MerchantLoginParams merchantLoginParams) {
        ReturnDto returnDto=null;
        try {
            returnDto = loginService.login(merchantLoginParams.getSiteId(), merchantLoginParams.getUserName(), merchantLoginParams.getPassword());
        } catch (Exception e) {
          return ReturnDto.buildFailedReturnDto("账号"+merchantLoginParams.getUserName()+"登录失败，原因:"+e);
        }
        return returnDto;
    }

    /**
     * 门店登陆
     * @param
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/store", method = {RequestMethod.GET, RequestMethod.POST})
    public ReturnDto storeLogin(Integer siteId,String username, String password) {
        ReturnDto message = loginService.storeLogin(siteId,username, password);
        return message;
    }



}
