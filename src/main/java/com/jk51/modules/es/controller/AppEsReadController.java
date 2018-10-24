package com.jk51.modules.es.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.BSearchLog;
import com.jk51.modules.es.entity.*;
import com.jk51.modules.es.service.AppEsService;
import com.jk51.modules.es.service.EsService;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName AppEsReadController
 * @Description APP ES搜索接口
 * @Date 2018-06-07 14:28
 */
@Controller
@RequestMapping("/app_esInterface")
public class AppEsReadController {

    public static final Logger LOGGER = LoggerFactory.getLogger(AppEsReadController.class);

    @Autowired
    AppEsService appEsService;

    @Autowired
    private EsService gService;

    @RequestMapping(value = "/appGetGoodsList",method = RequestMethod.POST)
    @ResponseBody
    public AppGoodsInfosResp getGoodsList(@RequestBody GoodsInfosAdminReq gInfosReq) {
        LOGGER.info("****appGetGoodsList request:{}", gInfosReq.toString());
        String dbname = gInfosReq.getDbname().split("_")[2];
        try {
                return appEsService.getGoodsListByCondition(gInfosReq,dbname);
        } catch (Exception e) {
            LOGGER.info("****appGetGoodsList getMessage:{}", e.getMessage());
        }
        return AppGoodsInfosResp.buildSystemErrorResp();
    }


    @RequestMapping("/suggest")
    @ResponseBody
    public ReturnDto suggest(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        try {
//            return gService.querySuggest(param.get("key") + "", param.get("siteId") + "");
            return appEsService.querySuggest(param.get("key") + "", param.get("siteId") + "");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("****suggest method error:{}", e.getMessage());
        }
        return ReturnDto.buildSystemErrorReturnDto();
    }
}
