package com.jk51.modules.merchant.controller;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.merchant.Ask;
import com.jk51.modules.merchant.service.AskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/19.
 */
@Controller
@RequestMapping("/ask")
public class AskController {

    @Autowired
    private AskService askService;
    public static final Logger log = LoggerFactory.getLogger(AskController.class);

    /**
     * 添加问答
     * @param request
     * @return
     */
    @RequestMapping(value = "/insertAsk")
    @ResponseBody
    public Map<String,Object> insertAsk(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Ask ask = null;
        try {
            ask = JacksonUtils.json2pojo(JacksonUtils.mapToJson(param), Ask.class);
        } catch (Exception e) {
            log.error("添加问答失败，类型转换异常：{}",e);
        }
        Map<String,Object> map = askService.insertAsk(ask);
        return map;
    }

    /**
     * 获取全部问答
     * @param request
     * @return
     */
    @RequestMapping(value = "/getAskAll")
    @ResponseBody
    public Map<String,Object> getAskAll(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        return askService.getAskAll(siteId);
    }

    /**
     * 根据Id查询问答
     * @param request
     * @return
     */
    @RequestMapping(value = "/getAskById")
    @ResponseBody
    public Map<String,Object> getAskById(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));
        return askService.getAskById(siteId,id);
    }

    /**
     * 修改问答
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateAsk")
    @ResponseBody
    public Map<String,Object> updateAsk(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Ask ask = null;
        try {
            ask = JacksonUtils.json2pojo(JacksonUtils.mapToJson(param), Ask.class);
        } catch (Exception e) {
            log.error("修改问答失败，类型转换异常:{}",e);
        }
        return askService.updateAsk(ask);
    }

    /**
     * 删除问答
     * @param request
     * @return
     */
    @RequestMapping(value = "/deleteAsk")
    @ResponseBody
    public Map<String,Object> deleteAsk(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));
        return askService.deleteAsk(siteId,id);
    }

    /**
     * 根据标签名称模糊查询
     * @param request
     * @return
     */
    @RequestMapping(value = "/getAskByName")
    @ResponseBody
    public Map<String,Object> getAskByName(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        String dimName = String.valueOf(param.get("dimName"));
        return askService.getAskByName(siteId,dimName);
    }

}
