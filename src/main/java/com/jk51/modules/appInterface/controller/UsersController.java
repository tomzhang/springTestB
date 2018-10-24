package com.jk51.modules.appInterface.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.model.AppH5Version;
import com.jk51.modules.appInterface.service.UsersService;
import com.jk51.modules.appInterface.util.LoginINfo;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.im.netease.service.GetTuiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:app登录、验证码等请求处理
 * 作者: gaojie
 * 创建日期: 2017-03-01
 * 修改记录:
 */
@Controller
@ResponseBody
@RequestMapping("/users")
public class UsersController {

    private Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UsersService usersService;
    @Autowired
    private GetTuiService getTuiService;

    /**
     *修改密码
     *
     * */
    @RequestMapping(value="/resetpwd",method = RequestMethod.POST)
    public  Map<String,Object> resetpwd(@RequestBody Map<String,Object> body){

        String phone = (String) body.get("phone");
        String password = (String) body.get("password");
        return  usersService.resetpwd(phone,password);

    }


    /**
     *app登录
     * userToken
     * password 以前的加密方式 SHA1
     *
     * loginType   1:账户和密码登录   2:验证码登录(用户密码修改是)，验证码存储在password 属性中
     *
     *
     *
     * */

    @RequestMapping(value="/token")
    public Map<String,Object> userToken(@RequestBody LoginINfo loginInfo,HttpServletRequest request){

        return usersService.userToken(loginInfo);
    }

    @RequestMapping(value="/token2")
    public Map<String,Object> userToken2(@RequestBody LoginINfo loginInfo,HttpServletRequest request){
        return usersService.userToken2(loginInfo);
    }

    @RequestMapping(value="/updateClinetId")
    public Map<String,Object> updateClinetId(HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String storeAdminId = request.getParameter("storeAdminId");
        String deviceToken =  request.getParameter("deviceToken");
        String clientId =  request.getParameter("clientId");
        String isLogin =  request.getParameter("isLogin");

        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId) || StringUtil.isEmpty(storeAdminId) || StringUtil.isEmpty(clientId)){
            result.put("status","ERROR");
            result.put("message","缺少必要参数！");
            return result;
        }

        try {
            result = usersService.updateClinetId(siteId, storeId, storeAdminId, clientId, isLogin, deviceToken);
            result.put("status","OK");
        } catch (Exception e) {

            logger.error("updateClinetId异常：报错信息：{}",ExceptionUtil.exceptionDetail(e));
            result.put("status","ERROR");
            result.put("message","updateClinetId异常");
        }
        return result;
    }

    /**
     *检查店员
     * param site_id
     * param store_user_id
     *
     * */

    @RequestMapping(value="/check")
    public Map<String,Object> checkUser(@RequestBody Map<String,Object> body){

       String accessToken = (String) body.get("AuthToken");
        return usersService.checkUser(accessToken);
    }

    /**
     *获取用户中心
     * */
    @RequestMapping(value="/centers")
    public Map<String,Object> getUsersCenters(@RequestBody Map<String,Object> body){

        String accessToken = (String) body.get("AuthToken");
        return usersService.getUsersCenters(accessToken);
    }

    /**
     *app 设置在线
     * */
    @RequestMapping(value="/online")
    public Map<String,Object> setOnline(@RequestBody Map<String,Object> body){
        String accessToken = (String) body.get("AuthToken");
        return usersService.setOnline(accessToken);
    }

    /**
     *app 设置离线线
     * */
    @RequestMapping(value="/offline")
    public Map<String,Object> setOffline(@RequestBody Map<String,Object> body){
        String accessToken = (String) body.get("AuthToken");
        return usersService.setOffline(accessToken);
    }


    /**
     *获取联系人信息
     * */
    @RequestMapping(value="/friends")
    public Map<String,Object> getFriendsInfo(@RequestBody Map<String,Object> body,HttpServletRequest request){

        String member_ids = (String) body.get("member_ids");
        String authToken = (String) body.get("authToken");
        return usersService.getFriendsInfo(member_ids,authToken);
    }



    /**
     *设置接收系统消息
     *
     * */
    @RequestMapping(value="/remind")
    public Map<String,Object> remind(@RequestBody Map<String,Object> body){

        String accessToken = (String) body.get("AuthToken");
        return usersService.remind(accessToken);
    }

    /**
     *设置接收系统消息
     *
     * */
    @RequestMapping(value="/unremind")
    public Map<String,Object> unremind(@RequestBody Map<String,Object> body){

        String accessToken = (String) body.get("AuthToken");
        return usersService.unremind(accessToken);
    }

    /**
     * 监测版本查询接口
     * 返回update时间最大的版本
     * */
    @RequestMapping(value="versions")
    public Map<String,Object> checkVersios(){

        return usersService.getNewestAppVersios(0);
    }
    /**
     * 监测版本查询接口(newAPP)
     * */
    @RequestMapping(value="checkNewVersios")
    public Map<String,Object> checkNewVersios(){

        return usersService.getNewestAppVersios(1);
    }

   /* @RequestMapping(value="getTui")
    public ReturnDto geTui(){
        geTuiPush.noticeOtherAppQuit(100330);
        return ReturnDto.buildSuccessReturnDto();
    }*/


    /**
     *APP退出登录
     */
    @RequestMapping("/deldevicetoken")
    public Map<String, Object> deldevicetoken(@RequestBody Map<String,Object> body){
        Map<String, Object> result = new HashMap<>();
        String siteId = String.valueOf(body.get("siteId"));
        String storeId = String.valueOf(body.get("storeId"));
        String storeAdminId = String.valueOf(body.get("storeAdminId"));
        String clientId = String.valueOf(body.get("clientId"));
        try {
            result = usersService.deldevicetoken(siteId, storeId, storeAdminId, clientId);
        } catch (Exception e) {
            logger.error("deldevicetoken异常：body:{},报错信息：{}",body,ExceptionUtil.exceptionDetail(e));
            result.put("status", "ERROR");
        }
        return result;
    }

    /**
     * app接受参数日志
     * @param request
     * @return
     */
    @RequestMapping(value="/insertPhoneLog")
    @ResponseBody
    public Map<String,Object> insertPhoneLog(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return usersService.insertPhoneLog(params);
    }

    @GetMapping(value="/appH5Version")
    @ResponseBody
    public Result getAppH5NewestVersion(){
        return usersService.getAppH5NewestVersion();
    }

    @PostMapping(value="/appH5Version")
    @ResponseBody
    public Result addAppH5NewestVersion(@RequestBody AppH5Version param){
        return usersService.addAppH5NewestVersion(param);
    }

    @GetMapping("handOverIm")
    public Result handOverIm(){

       return getTuiService.noticeAllAppOffline();
    }
}
