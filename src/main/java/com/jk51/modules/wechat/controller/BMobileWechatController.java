package com.jk51.modules.wechat.controller;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.BMobileWechat;
import com.jk51.modules.wechat.service.BMobileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang1
 * 创建日期: 2017-05-31
 * 修改记录:
 */
@Controller
@RequestMapping("wechat/bMobileWechat")
public class BMobileWechatController {
    @Autowired
    private BMobileService bMobileService;

    @ResponseBody
    @PostMapping("findMobile")
    public List<String> findMobile(String userId, Integer siteId) {
        return bMobileService.findMobile(userId, siteId);
    }

    @ResponseBody
    @PostMapping("findBMobileWechat")
    public Map<String,Object> findBMobileWechat(@RequestBody String json) {
        Map<String, Object> params = null;
        try {
            params = JacksonUtils.json2map(json);
            String mobile=params.get("mobile").toString();
            Integer siteId=Integer.parseInt(params.get("siteId").toString());
            return bMobileService.findBMobileWechat(mobile, siteId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @ResponseBody
    @PostMapping("insert")
    public  Map<String, Object> insert(@RequestBody String json) {
        Map<String, Object> params = null;
        try {
            params = JacksonUtils.json2map(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bMobileService.insert(params);
        return params;
    }
    @ResponseBody
    @PostMapping("updateByPrimaryKey")
    public  Map<String, Object> updateByPrimaryKey(@RequestBody String json) {
        Map<String, Object> params = null;
        try {
            params = JacksonUtils.json2map(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(StringUtil.isEmpty(params.get("mobile"))||"null".equals(params.get("mobile"))){
            return params;
        }
        bMobileService.updateByPrimaryKey(params);
        return params;
    }
}
