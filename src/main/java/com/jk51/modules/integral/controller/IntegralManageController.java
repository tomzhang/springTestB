package com.jk51.modules.integral.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.integral.service.IntegralLogService;
import com.jk51.modules.integral.service.IntegralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 商户后台 积分管理
 */
@Controller
@ResponseBody
@RequestMapping("integral")
public class IntegralManageController {

    @Autowired
    private IntegralLogService integralLogService;

    @Autowired
    private IntegralService service;

    /**
     * 更新积分规则
     */
    @PostMapping("updateRule")
    public JSONObject integralUpdateRule(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        String checkParam = mapKeyHelper(param, "siteId", "id", "status", "value", "integralDesc");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        service.updateRule(param);
        return resultHelper(true);
    }

    /**
     * 会员积分列表
     */
    @PostMapping("member")
    public JSONObject memberPage(@RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                 @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize, HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        Page page = PageHelper.startPage(pageNum, pageSize);
        List<Map> result = service.queryMemberi(param);
        return resultHelper(result, page.toPageInfo());
    }

    /**
     * 积分设置列表
     */
    @PostMapping("rules")
    public JSONObject rules(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        List<Map> result = service.queryRules(param);
        return resultHelper(result);
    }

    /**
     * 设置积分比例
     */
    @PostMapping("setProportion")
    public JSONObject setProportion(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        service.setProportion(param);
        return resultHelper(true);
    }

    @PostMapping("getProportion")
    public JSONObject getProportion(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        BigDecimal d = service.queryIntegralProportion(param);
        return resultHelper(d);
    }

    /**
     * 强制修改积分
     */
    @PostMapping("updateForce")
    public JSONObject storeUpdateIntegral(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        String checkParam = mapKeyHelper(param, "siteId", "type", "value", "buyerId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        if (StringUtil.isEmpty(param.get("siteId"))) {
            return resultHelper(false, "参数 siteId 不能为空！");
        }
        Map overPlusMap = service.getOverPlus(param);
        if (StringUtil.isEmpty(overPlusMap)) {
            return resultHelper(false, CommonConstant.MSG_MEMBER_NOTEXISTS);
        }
        service.updateIntegralForce(param, overPlusMap);
        return resultHelper(true);
    }

    /**
     * 查询积分记录
     */
    @PostMapping("logQuery")
    public JSONObject logQuery(@RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize, HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        Page page = PageHelper.startPage(pageNum, pageSize);
        List<Map> result = service.logQuery(param);
        return resultHelper(result, page.toPageInfo());
    }

    @RequestMapping(value = "/logadd", produces = "text/json;charset=utf-8")
    public String logAdd(@RequestBody Map<String, Object> param) {
        return integralLogService.integraUpdate(param);
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

    public JSONObject resultHelper(Object data, Object page) {
        JSONObject result = new JSONObject();
        result.put("status", true);
        result.put("result", data);
        result.put("page", page);
        return result;
    }

    public JSONObject resultHelper(Object data) {
        JSONObject result = new JSONObject();
        result.put("status", true);
        result.put("result", data);
        return result;
    }

}
