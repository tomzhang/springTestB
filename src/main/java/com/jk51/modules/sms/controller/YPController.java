package com.jk51.modules.sms.controller;

import com.jk51.commons.ccprest.result.BaseResult;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.sms.SysType;
import com.jk51.modules.appInterface.service.AppMemberService;
import com.jk51.modules.clerkvisit.service.BVisitDescService;
import com.jk51.modules.merchant.service.ClerkReturnVisitService;
import com.jk51.modules.merchant.service.MerchantService;
import com.jk51.modules.sms.service.CommonService;
import com.jk51.modules.sms.service.Sms7MoorService;
import com.jk51.modules.sms.service.YpSmsService;
import com.jk51.modules.sms.service.ZtSmsService;
import com.jk51.modules.sms.smsConfig.SmsEnum;
import com.jk51.modules.sms.smsConfig.SmsParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-17
 * 修改记录:
 */
@Controller
@RequestMapping("/sms")
public class YPController {
    @Autowired
    YpSmsService ypService;
    @Autowired
    ZtSmsService ztSmsService;
    @Autowired
    CommonService commonService;
    @Autowired
    MerchantService merchantService;
    @Autowired
    private Sms7MoorService _7moorSmsService;
    @Autowired
    BVisitDescService bVisitDescService;
    @Autowired
    ClerkReturnVisitService clerkReturnVisitService;
    @Autowired
    AppMemberService appMemberService;

    /*
    不同的业务请求调用的业务语句不同
    填写要替换的词组
    返回值为0，代表发送成功，返回值不为0，代表发送失败。
     */
    @ResponseBody
    @RequestMapping("/tpl_service")
    public int SendMessage(String phone, String code, Integer siteId, Integer type, Integer smsType) {
        String merchantName  = merchantService.queryMerchantName(siteId);
        return Integer.parseInt(commonService.SendMessage(
            commonService.transformParam(siteId, phone, type, smsType,SmsEnum.VERIFY_CODE,code,merchantName)));
    }

    /**
     * 后台商务admin短信验证
     * @param phone
     * @param code
     * @param siteId
     * @param type
     * @param smsType
     * @return
     */
    @ResponseBody
    @RequestMapping("/tpl_code")
    public int SendSMS(String phone, String code, Integer siteId, Integer type, Integer smsType) {
        String merchantName  = merchantService.queryMerchantName(siteId);
        return Integer.parseInt(commonService.SendMessage(
            commonService.transformParam(siteId, phone, type, smsType,SmsEnum.VERIFY_CODE,code,merchantName)));
    }

    @ResponseBody
    @RequestMapping("/tpl_doctor")
    public int SendDoctor(String phone, String doctor, Integer siteId, Integer type, Integer smsType) {
        return Integer.parseInt(commonService.SendMessage(commonService.transformParam(
            siteId,phone,  type, smsType,SmsEnum.DOCTOR_SMS,phone,doctor)));
    }

    /**
     * 三级分销短信
     *
     * @param phone
     * @param code
     * @param name
     * @param url
     * @return
     */
    @ResponseBody
    @RequestMapping("/tpl_distri")
    public Map<String, Object> sendVali(Integer siteId, String phone, String code, String name, String url) {
        Map<String, Object> map = new HashMap<>();
        String result = commonService.SendMessage(commonService.transformParam(siteId, phone,null,SysType.INVITATION_CODE,
            SmsEnum.THREE_DISTRIBUTION_SMS, code, name, url));
        map.put("merchantName", result);
        return map;
    }

    /**
     * 语音验证码
     *
     * @param phone
     * @param code
     * @param siteId
     * @return
     */
    @ResponseBody
    @RequestMapping("/_7MoorWebCallNew")
    public Map<String, Object> _7MoorWebCallNew(String phone, String code, String siteId) {
        Map<String, Object> map = new HashMap<>();
        BaseResult result = _7moorSmsService._7MoorWebCallNew(phone, code, siteId);
        map.put("result", result);
        return map;
    }

    /**
     * 回访中发送短信
     *
     * @param request
     * @return
     */
    @PostMapping("/visitMessage")
    @ResponseBody
    public Map<String, Object> sendVisitMessage(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String phone = param.get("phone").toString();
        String time = "";
        if (Objects.nonNull(param.get("time"))) {
            time = param.get("time").toString();
        }
        String siteId = param.get("siteId").toString();
        Map<String, Object> merchantMap = merchantService.getMerchantBySiteId(Integer.valueOf(siteId));
        String merchantName = merchantMap.get("merchant_name").toString();
        String title = "";
        if (Objects.nonNull(param.get("title"))) {
            title = param.get("title").toString();
        }
        String goodsId = param.get("goodsId").toString();
        String storeAdminId = param.get("storeAdminId").toString();
        String visitId = param.get("id").toString();
        String activityId = "";
        if (Objects.nonNull(param.get("activityId"))) {
            activityId = param.get("activityId").toString();
        }

        String texturl = "http://" + siteId + appMemberService.getCouponUrl() + "single/product?visitId=" +
            visitId + "&storeAdminId=" + storeAdminId + "&goodsId=" + goodsId;

        String result = commonService.SendMessage(commonService.transformParam(Integer.parseInt(siteId), phone,null,SysType.MARKETING_MESSAGES,
            SmsEnum.ACTIVITY_SMS, merchantName, time, title, texturl));
        if (result.contains("0")) {
            bVisitDescService.updateByVisitId(siteId, visitId, storeAdminId);
            if (Objects.nonNull(param.get("bvsId"))) {
                clerkReturnVisitService.updateSmsnum(siteId, param.get("bvsId").toString());
            }

        }
        map.put("result", result);
        return map;
    }
}
