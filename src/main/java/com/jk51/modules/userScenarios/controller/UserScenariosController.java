package com.jk51.modules.userScenarios.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.pay.WxPublicConfig;
import com.jk51.modules.userScenarios.service.QrcodeService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.collections.map.HashedMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-06-07
 * 修改记录:
 */
@Controller
@RequestMapping("/wechat")
public class UserScenariosController {

    private static final Logger logger = LoggerFactory.getLogger(QrcodeService.class);

    @Autowired
    QrcodeService qrcodeService;



    /**
     * 生成带参数的永久二维码
     * @param request:
     *               sceneStr 场景值，店员是邀请码
     *               siteId 商户id
     *               type 1店员 2门店 3商户
     *
     * @param response
     * @return
     */
    @RequestMapping("/qrcode")
    @ResponseBody
    public Map<String, Object> Code(HttpServletRequest request, HttpServletResponse response){

        //参数
        Map<String, Object> param = ParameterUtil.getParameterMap(request);

        //存放返回值
        Map<String, Object> result = qrcodeService.createQrcode(param);



        return result;
    }

    /**
     * 扫描二维码记录表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/scan")
    @ResponseBody
    public Map<String, Object> scanCode(HttpServletRequest request, HttpServletResponse response){
        //参数
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Map<String, Object> result = qrcodeService.scanCodeConcern(param);
        return result;
    }

    @RequestMapping("/saveQrcode")
    @ResponseBody
    public Map<String, Object> adminQrcodeInfo (HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> param = ParameterUtil.getParameterMap(request);

        Map<String, Object> adminInfo = qrcodeService.adminQrcodeInfo(param);

        return adminInfo;
    }

    @RequestMapping("/createAllQrcode")
    @ResponseBody
    public Map<String, Object> createAllQrcode (){
        Map<String, Object> result = new HashMap();
        try {
            result = qrcodeService.createAllQrcode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping("/createMerchantQrcode")
    @ResponseBody
    public Map<String, Object> createMerchantQrcode (HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(param.get("siteId").toString());
        Map<String, Object> result = new HashMap();
        try {
            result = qrcodeService.createMerchantQrcode(siteId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping("/createStroeAdminQrcode")
    @ResponseBody
    public Map<String, Object> createStroeAdminQrcode (HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(param.get("siteId").toString());
        Integer storeAdminId = Integer.parseInt(param.get("storeAdminId").toString());
        Map<String, Object> result = new HashMap();
        try {
            result = qrcodeService.createStroeAdminQrcode(siteId, storeAdminId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping("/insertOpenid")
    @ResponseBody
    public Map<String, Object> insertOpenid(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Map<String, Object> result = new HashMap();
        String openid = String.valueOf(map.get("openid"));
        Integer siteId = Integer.parseInt(map.get("siteId").toString());
        Integer buyerId = Integer.parseInt(map.get("buyerId").toString());
        Integer status = qrcodeService.insertOpenid(openid, siteId, buyerId);
        result.put("status", status);
        return result;
    }
    @RequestMapping("/insertAliQrcode")
    @ResponseBody
    public Map<String, Object> insertAliQrcode(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Map<String, Object> result = new HashMap();
        String url = String.valueOf(map.get("url"));
        Integer siteId = Integer.parseInt(map.get("siteId").toString());
        Integer storeadminId = Integer.parseInt(map.get("storeadminId").toString());
        logger.info("cs-------siteId:{}--url:{}-----clerkCode:{}---",siteId,url,storeadminId);
        Integer status = qrcodeService.insertAliQrcode( siteId, storeadminId, url);
        result.put("status", status);
        return result;
    }
    /**
     * 取消关注
     * @param request
     * @return
     */
    @RequestMapping("/cancelConcern")
    @ResponseBody
    public Map<String, Object> cancelConcern(HttpServletRequest request){
        //参数
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String open_id = String.valueOf(param.get("open_id"));
        Integer siteId = Integer.parseInt(param.get("siteId").toString());
        Map<String, Object> result = new HashMap();
        Integer status = qrcodeService.cancelConcern(siteId,open_id);
        result.put("status", status);
        return result;
    }
    /**
     * 取消关注
     * @param request
     * @return
     */
    @RequestMapping("/queryConcernStatus")
    @ResponseBody
    public Map<String, Object> queryConcernStatus(HttpServletRequest request){
        //参数
        Map<String, Object> param = ParameterUtil.getParameterMap(request);


        Map<String, Object> result =qrcodeService.queryConcernStatus(param);
        return result;
    }
}
