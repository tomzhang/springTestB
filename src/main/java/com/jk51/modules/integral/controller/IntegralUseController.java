package com.jk51.modules.integral.controller;


import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.integral.service.IntegralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
@ResponseBody
@RequestMapping("integralUse")
public class IntegralUseController {

    private static final Logger logger = LoggerFactory.getLogger(IntegralService.class);

    @Autowired
    private IntegralService service;

    /**
     * 注册送积分
     *
     * @param request
     * @return
     */
    @PostMapping("register")
    public JSONObject addIntegralWithRegister(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        return service.integralAddForRegister(param);
    }

    /**
     * 签到及送积分
     *
     * @param request
     * @return
     */
    @PostMapping("checkin")
    public JSONObject addIntegralWithCheckin(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);

        String checkParam = mapKeyHelper(param, "siteId", "buyerId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }

        //签到并增加积分
        return service.integralAddForChicken(param, true);
    }

    /**
     * 查询签到送积分数据
     *
     * @param request
     * @return
     */
    @PostMapping("queryCheckinRule")
    public JSONObject queryCheckinRule(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        param.put("useCase", CommonConstant.USE_CASE_CHECKIN);
        String checkParam = mapKeyHelper(param, "siteId", "buyerId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        logger.info("查询当日签到可获得积分数、连续签到规则接口，参数为：{}", JacksonUtils.mapToJson(param));
        JSONObject result = new JSONObject();
        Map today = service.checkinToday(param);//判断当天是否已签到
        if (StringUtil.isEmpty(today)) {
            result.put("msg", CommonConstant.MSG_CHECKIN_TOMORROW);
            result.put("code", "1002");
        }else{
            result.put("code", "1003");
            //result.putAll(goodsService.checkinTomorrowVal(status, param, today));
        }
        result.putAll(service.integralAddForChicken(param, false));

        param.put("useCase", CommonConstant.USE_CASE_CHECKIN);
        Map ruleMap = service.query(param);//是否开启签到送积分
        if (StringUtil.isEmpty(ruleMap) || (ruleMap != null && ruleMap.get("status").equals(CommonConstant.STATUS_SETTING))) {
            result.put("value", 0);
        }
        result.remove("status");
        return result;
    }

    /**
     * 查询明日签到可获得积分数
     *
     * @param request
     * @return
     */
    @PostMapping("queryTomorrow")
    public JSONObject queryTomorrow(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        param.put("useCase", CommonConstant.USE_CASE_CHECKIN);
        String checkParam = mapKeyHelper(param, "siteId", "buyerId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        logger.info("查询明日签到可获得积分数接口，参数为：{}", JacksonUtils.mapToJson(param));
        Map ruleMap = service.query(param);//是否开启签到送积分
        if (StringUtil.isEmpty(ruleMap) || (ruleMap != null && ruleMap.get("status").equals(CommonConstant.STATUS_SETTING))) {
            return resultHelper(false, CommonConstant.MSG_STATUS_CHECKIN_T);
        }
        Map exist = service.checkinToday(param);//判断当天是否已签到
        if (StringUtil.isEmpty(exist)) {
            return resultHelper(false, CommonConstant.MSG_CHECKIN_TOMORROW);
        }
        return service.checkinTomorrowVal(ruleMap, param, exist);
    }


    /**
     * 购物送积分
     *
     * @param request
     * @return
     */
    @PostMapping("buy")
    public JSONObject addIntegralWithBuy(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        return service.integralAddForBuy(param);
    }

    /**
     * 积分抵现金
     *
     * @param request
     * @return
     */
    @PostMapping("diff")
    public JSONObject integralDiff(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        return service.integralDiff(param);
    }

    /**
     * 根据订单金额查询可抵用积分数
     *
     * @param request
     * @return
     */
    @PostMapping("maxDiff")
    public JSONObject integralMaxDiff(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        return service.integralMaxDiff(param);
    }

    /**
     * 计算用户输入的积分数的可抵扣钱数
     *
     * @param request
     * @return
     */
    @PostMapping("calcMoney")
    public JSONObject integralDiffCalcMoney(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        return service.integralCalcMoney(param);
    }

    public String mapKeyHelper(Map map, String... args) {
        for (String param : args) {
            if (!map.containsKey(param)) return param;
        }
        return "";
    }

    public JSONObject resultHelper(boolean flag, String str) {
        JSONObject result = new JSONObject();
        result.put("status", flag ? "success" : "error");
        result.put("msg", str);
        return result;
    }

    public JSONObject resultHelper(boolean flag) {
        JSONObject result = new JSONObject();
        result.put("status", flag ? "success" : "error");
        return result;
    }

}
