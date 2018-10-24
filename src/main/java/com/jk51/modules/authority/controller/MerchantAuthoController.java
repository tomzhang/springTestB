package com.jk51.modules.authority.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.authority.service.MerchantAuthoService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家授权
 *
 * @auhter zy
 * @create 2017-08-04 14:21
 */
@Controller
@RequestMapping("/Authorize")
public class MerchantAuthoController {

    public static final Logger LOGGER = LoggerFactory.getLogger(MerchantAuthoController.class);

    @Autowired
    private MerchantAuthoService merchantAuthoService;

    @RequestMapping(value = "/merchantList",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> selectMerchantList(HttpServletRequest request) {
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
        Map<String,Object> resultMap = new HashedMap();
        int page = (param.get("page") == null || "".equals(param.get("page")))?1:Integer.valueOf(param.get("page").toString());
        int pageSize = (param.get("pageSize") == null || "".equals(param.get("pageSize")))?15:Integer.valueOf(param.get("pageSize").toString());
        PageHelper.startPage(page,pageSize);
        List<Map<String,Object>> merchantList = merchantAuthoService.selectMerchantList(param);
        if(merchantList != null || merchantList.size() != 0) {
            PageInfo<?> pageInfo = new PageInfo<>(merchantList);

            resultMap.put("page",pageInfo.getPageNum());
            resultMap.put("pageSize",pageInfo.getPageSize());
            resultMap.put("totalPages",pageInfo.getPages());
            resultMap.put("total",pageInfo.getTotal());
            resultMap.put("items",merchantList);
            resultMap.put("result","success");
        }else{
            resultMap.put("result","fail");
            LOGGER.debug("没有查询到商家列表!");
        }
        return resultMap;
    }


    //插入授权码
    @RequestMapping(value="/insertAuthoLog",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> insertAuthoLog(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        if(StringUtil.isEmpty(parameterMap.get("siteId"))) {
            Map<String,Object> res = new HashMap<String, Object>();
            res.put("result", "siteId不能为空!");
            return res;
        }
        Map<String,Object> map  = merchantAuthoService.insertAuthoLog(parameterMap);
        return map;
    }

    //根据siteId和密码查询授权码记录
    @RequestMapping(value = "/getAuthoLog",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getMerchants(HttpServletRequest request) {
        Map<String, Object> result = null;
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String password = String.valueOf(param.get("password"));
        result = merchantAuthoService.getAuthoLog(password);
        return result;
    }

    //根据siteId和门店名称查询门店列表
    @RequestMapping(value = "/getStoreList",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getStoresList(Integer siteId,String storeName) {
        if(StringUtil.isEmpty(siteId)) {
            List<Map<String,Object>> resultList = new ArrayList<>();
            Map<String,Object> reusltMap = new HashedMap();
            reusltMap.put("result","siteId不能为空!");
            return resultList;
        }
        List<Map<String,Object>> resuList = merchantAuthoService.getStoresListBySiteId(siteId,storeName);
        if(resuList == null || resuList.size() == 0) {
            List<Map<String,Object>> resultList = new ArrayList<>();
            Map<String,Object> reusltMap = new HashedMap();
            reusltMap.put("result","查询门店列表有误!");
            return resultList;
        }else{
            return resuList;
        }

    }


}
