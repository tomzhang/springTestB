package com.jk51.modules.tpl.web;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.tpl.service.EleService;
import com.jk51.modules.tpl.service.ImdadaService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:达达物流
 * 作者: duyj
 * 创建日期:
 * 修改记录:
 */
@Controller
@RequestMapping("im")
public class ImdadaController {
    private static final Log logger = LogFactory.getLog(ImdadaController.class);
    @Autowired
    private ImdadaService service;
    @Autowired
    private EleService eleService;
    @PostMapping("addOrder")
    @ResponseBody
    public Map<String,Object> addOrder(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        return service.createOrder(param);
    }

    @PostMapping("queryOrder")
    @ResponseBody
    public Map<String,Object> queryOrder(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        return service.queryOrder(param);
    }

    @PostMapping("addStore")
    @ResponseBody
    public Map<String,Object> addStore(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        return service.addStore(param);
    }

    @PostMapping("updateStore")
    @ResponseBody
    public Map<String,Object> updateStore(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        return service.updateStore(param);
    }

    @RequestMapping("queryStore")
    @ResponseBody
    public Map<String,Object> queryStore(HttpServletRequest request){
        //im/queryStore?origin_shop_id=73753-1706&sourceId=73753
        Map param = ParameterUtil.getParameterMap(request);
        return service.queryStore(param);
    }

    @RequestMapping("tmp")
    @ResponseBody
    public Map<String,Object> test(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        return service.tmp(param);
    }

    @RequestMapping("cb")
    @ResponseBody
    public void cb(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        eleService.insertLog("达达配送回调参数：" + JacksonUtils.mapToJson(param));
        service.cb(param);
    }

    @RequestMapping("cancelOrder")
    @ResponseBody
    public Map cancelOrder(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        return service.cancelOrder(param);
    }

    @RequestMapping("fail")
    @ResponseBody
    public Map fail(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        return service.failHandle(Long.valueOf(param.get("tradesId")+""));
    }
    @RequestMapping("ces")
    @ResponseBody
    public boolean ces(){
        return service.ce();
    }


    @RequestMapping("lingF")
    @ResponseBody
    public boolean lingF(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        return service.lingF(Long.valueOf(param.get("tradesId") + ""));
    }

}
