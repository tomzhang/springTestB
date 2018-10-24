package com.jk51.modules.wechat.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.merchant.MerchantExt;
import com.jk51.model.order.SBMember;
import com.jk51.modules.appInterface.util.ResultMap;
import com.jk51.modules.merchant.service.MerchantExtService;
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
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-31
 * 修改记录:
 */
@Controller
@RequestMapping("/wechat/member")
public class WechatMemberController {
    private Logger log = LoggerFactory.getLogger(WechatMemberController.class);
    @Autowired
    private WechatMemberService wechatMemberService;
    @Autowired
    private MerchantExtService merchantExtService;

    @PostMapping("updateIdNum")
    public void updateIdNum(Integer siteId, String mobile_no, String certif_no, Integer age) {
        wechatMemberService.updateIdNum(siteId, mobile_no, certif_no, age);
    }

    @ResponseBody
    @PostMapping("getOfflineInfo")
    public Map getOfflineInfo(Integer siteId, String mobile_no) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("infoList", wechatMemberService.getOfflineInfo(siteId, mobile_no));
        return resultMap;
    }

    @ResponseBody
    @PostMapping("getCardNo")
    public Map getCardNo(Integer siteId, String mobile) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("infoList", wechatMemberService.getCardNo(siteId, mobile));
        return resultMap;
    }

    @ResponseBody
    @PostMapping("selectByPhoneNum")
    public SBMember selectByPhoneNum(String mobile, Integer siteId) {
        return wechatMemberService.selectByPhoneNum(mobile, siteId);
    }

    @ResponseBody
    @PostMapping("loginByUnameAndPwd")
    public Map<String, Object> loginByUnameAndPwd(String username, String password, Integer siteId) {
        try {
            return wechatMemberService.loginByUnameAndPwd(username, password, siteId);
        } catch (Exception e) {
            log.error("密码加密异常");
            return ResultMap.errorResult("系统忙，请稍后重试");
        }
    }

    @ResponseBody
    @PostMapping("loginByUnameAndVcode")
    public Map<String, Object> loginByUnameAndVcode(String phoneNum, Integer siteId, String inviteCode, String passwd,Integer mem_source, String secondToken) {
        return wechatMemberService.loginByUnameAndVcode(siteId, phoneNum, inviteCode, passwd,mem_source,secondToken);
    }

    @ResponseBody
    @RequestMapping("/memberRegistration")
    public Map<String, Object> memberRegistration(HttpServletRequest request) {
        String siteId = request.getParameter("siteId");
        String mobile = request.getParameter("mobile");

        Map<String, Object> result = new HashMap<>();
        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(mobile)){
            result.put("status", "ERROR");
            result.put("message", "缺少必填参数");
            return result;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        params.put("mobile", mobile);
        try {
            result = wechatMemberService.loginByUnameAndVcode2(params);
            result.put("status", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "ERROR");
            result.put("message", "通讯异常，请稍后重试");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("queryMemberInfoByPhoneNum")
    public Map<String, Object> queryMemberInfoByPhoneNum(String phone, Integer siteId) {
        return wechatMemberService.queryMemberInfoByPhoneNum(phone, siteId);
    }

    @ResponseBody
    @PostMapping("getCheckin")
    public Map<String, Object> getCheckin(Integer memberId, Integer siteId) {
        return wechatMemberService.getCheckin(siteId, memberId);
    }

    @ResponseBody
    @PostMapping("setCheckin")
    public Integer setCheckin(Integer memberId, Integer siteId, Integer checkinNum) {
        return wechatMemberService.setCheckin(siteId, memberId, checkinNum);
    }

    @ResponseBody
    @PostMapping("getMerchantExt")
    public MerchantExt getMerchantExt(Integer merchantId) {
        return merchantExtService.findByMerchantId(merchantId);
    }

    @ResponseBody
    @PostMapping("checkMember")
    public Map checkMember(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        return wechatMemberService.checkMember(params);
    }

    @ResponseBody
    @PostMapping("updatePassword")
    public Integer updatePassword(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        return wechatMemberService.updatePassword(params);
    }

    @ResponseBody
    @PostMapping("updatePasswordByMobile")
    public Integer updatePasswordByMobile(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        return wechatMemberService.updatePasswordByMobile(params);
    }

    @ResponseBody
    @PostMapping("setUserPassword")
    public Integer setUserPassword(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        return wechatMemberService.setUserPassword(params);
    }

    @ResponseBody
    @PostMapping("recordLoginLog")
    public Map recordLoginLog(String siteId, String mobile, String buyerId, String inviteCode, String ip) {
        return wechatMemberService.recoreLoginLog(siteId, mobile, buyerId, inviteCode, ip);
    }

    @ResponseBody
    @PostMapping("updUserInfo")
    public Integer updUserInfo(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        return wechatMemberService.updUserInfo(params);
    }

    @ResponseBody
    @PostMapping("getUserInfo")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        return wechatMemberService.getUserInfo(params);
    }


    @ResponseBody
    @PostMapping("getBMember")
    public SBMember getBMember(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        return wechatMemberService.getBMember(params);
    }

    @ResponseBody
    @PostMapping("recordLoginLog2")
    public Map recordLoginLog2(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return wechatMemberService.recordLoginLog2(params);
    }

    @ResponseBody
    @PostMapping("getTokenByMemberId")
    public Map getTokenByMemberId(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return wechatMemberService.getTokenByMemberId(params);
    }


}
