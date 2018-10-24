package com.jk51.modules.checkin.controller;

import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.checkin.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 签到
 *
 *
 */

@Controller()
@RequestMapping("checkin")
public class CheckinController {

    @Autowired
    private CheckinService checkinService;

    @PostMapping("action")
    @ResponseBody
    public JSONObject checkin(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);

        String checkParam = mapKeyHelper(param, "siteId", "buyerId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        return checkinService.checkinAction(param);
    }

    public String mapKeyHelper(Map map, String... args) {
        for (String param : args) {
            if (!map.containsKey(param)) return param;
        }
        return "";
    }

    public JSONObject resultHelper(boolean flag, String str) {
        JSONObject result = new JSONObject();
        result.put("status", flag ? "success" : "fail");
        result.put("msg", str);
        return result;
    }

    public JSONObject resultHelper(boolean flag) {
        JSONObject result = new JSONObject();
        result.put("status", flag ? "success" : "fail");
        return result;
    }

}
