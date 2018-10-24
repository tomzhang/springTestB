package com.jk51.modules.appInterface.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.SVipMember;
import com.jk51.modules.appInterface.service.Orders2Service;
import com.jk51.modules.distribution.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yanglile
 * 创建日期: 2017-06-15
 * 修改记录:
 */
@RequestMapping("/orders2")
@RestController
public class Orders2Controller {
    private static final Logger logger = LoggerFactory.getLogger(Orders2Controller.class);
    @Autowired
    private Orders2Service orders2Service;

    /**
     * APP工作首页
     * @param request
     * @return
     */
    @RequestMapping("/workerIndex")
    public Map<String, Object> workerIndex(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        if(StringUtil.isEmpty(params.get("siteId"))){
            result.put("status","ERROR");
            result.put("message","缺少必要参数！");
            return result;
        }

        try {
            result = orders2Service.workerIndex(params);
            result.put("status","OK");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status","ERROR");
            result.put("message","工作首页异常！");
        }
        return result;
    }

    /**
     *手机号查询历史购买商品

     */
    @RequestMapping("/selectGoodsByPhone")
    public Map<String, Object> selectGoodsByPhone(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String mobile = request.getParameter("mobile");
        String storeAdminId = request.getParameter("storeAdminId");
        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId) || StringUtil.isEmpty(mobile)){
            result.put("status","ERROR");
            result.put("message","缺少必要参数！");
            return result;
        }

        try {
            result = orders2Service.selectGoodsByPhone(siteId, storeId, mobile, storeAdminId);
            result.put("status","OK");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status","ERROR");
            result.put("message","获取会员历史商品异常！");
        }
        return result;
    }

    /**
     * 查询订单 备货编号
     * @param request
     * @return
     */
    @RequestMapping("/selectStockupId")
    public Map<String, Object> selectStockupId(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        String siteId = request.getParameter("siteId");
        String tradesId = request.getParameter("tradesId");
        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(tradesId)){
            result.put("status","ERROR");
            result.put("message","缺少必要参数！");
            return result;
        }

        try {
            List ids = new ArrayList<>();ids.add(tradesId);
            Map<String,Object> params = new HashMap<>();//用以查询备货编码
            params.put("siteId", siteId);
            params.put("ids", ids);
            result = orders2Service.selectStockupId(params);
            result.put("status","OK");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status","ERROR");
            result.put("message","获取备货编号异常！");
        }
        return result;
    }

    @RequestMapping("/serveCustomersCount")
    public Map<String, Object> serveCustomersCount(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String storeAdminId = request.getParameter("storeAdminId");
        String storeAdminExtId = request.getParameter("storeAdminExtId");
        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeAdminId) || StringUtil.isEmpty(storeAdminExtId)){
            result.put("status","ERROR");
            result.put("message","缺少必要参数！");
            return result;
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("siteId", siteId);
            //params.put("storeId", storeId);
            params.put("storeAdminId", storeAdminId);
            params.put("storeAdminExtId", storeAdminExtId);
            result = orders2Service.serveCustomersCount(params);
            result.put("status","OK");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status","ERROR");
            result.put("message","获取会员服务顾客异常！");
        }
        return result;
    }

    /**
     * 店员最近服务会员列表
     * @param request
     * @return
     */
    @RequestMapping("/serveCustomers")
    public Map<String, Object> serveCustomers(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String storeAdminId = request.getParameter("storeAdminId");
        String storeAdminExtId = request.getParameter("storeAdminExtId");
        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeAdminId) || StringUtil.isEmpty(storeAdminExtId)){
            result.put("status","ERROR");
            result.put("message","缺少必要参数！");
            return result;
        }

        Integer pageNum = !StringUtil.isEmpty(request.getParameter("pageNum"))?Integer.parseInt(request.getParameter("pageNum")):1;
        Integer pageSize = !StringUtil.isEmpty(request.getParameter("pageSize"))?Integer.parseInt(request.getParameter("pageSize")):15;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("siteId", siteId);
            //params.put("storeId", storeId);
            params.put("storeAdminId", storeAdminId);
            params.put("storeAdminExtId", storeAdminExtId);
            Page<SVipMember> page = PageHelper.startPage(pageNum, pageSize);
            List<SVipMember> vipMemberList = orders2Service.serveCustomers(params);

            result.put("mList", vipMemberList);
            result.put("total_items", page.toPageInfo().getTotal());
            result.put("pageCount", page.toPageInfo().getPages());
            result.put("current", page.toPageInfo().getPageNum());
            result.put("before", page.toPageInfo().getPrePage());
            result.put("next", page.toPageInfo().getNextPage());
            result.put("pageSize", page.toPageInfo().getPageSize());
            result.put("status","OK");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status","ERROR");
            result.put("message","获取会员服务顾客异常！");
        }
        return result;
    }

    @RequestMapping("/getStoreAdminExt")
    public Map<String, Object> getStoreAdminInfo(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String storeAdminId = request.getParameter("storeAdminId");
        String storeAdminExtId = request.getParameter("storeAdminExtId");

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("siteId", siteId);
            params.put("storeAdminExtId", storeAdminExtId);
            Map<String, Object> storeAdminExt = orders2Service.getStoreAdminExt(params);
            result.putAll(storeAdminExt);
            result.put("status","OK");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status","ERROR");
            result.put("message","获取店员信息异常！");
        }
        return result;
    }

    @RequestMapping("/myDeliveryOrderNum")
    public Map<String, Object> myDeliveryOrderNum(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String storeShippingClerkId = request.getParameter("storeShippingClerkId");

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("siteId", siteId);
            params.put("storeId", storeId);
            params.put("storeShippingClerkId", storeShippingClerkId);
            long total = orders2Service.myDeliveryOrderNum(params);
            result.put("total", total);
            result.put("status","OK");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status","ERROR");
            result.put("message","店员送货订单数量查询异常！");
        }
        return result;
    }

    /**
     * 根据预约单号查询预约单商品信息
     * @param request
     * @return
     */
    @PostMapping("/getGoodsByPreNumber")
    @SuppressWarnings("all")
    public Map<String,Object> getGoodsByPreNumber(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String preNumber = request.getParameter("preNumber");
        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(preNumber)){
            result.put("status","ERROR");
            result.put("message","缺少必要参数！");
            return result;
        }

        try {
            result = orders2Service.getPreGoodsByNumber(siteId, storeId, preNumber);
            result.put("status","OK");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status","ERROR");
            result.put("message","获取预约单商品信息异常！");
        }
        return result;
    }

    @RequestMapping("/offlineQRCode")
    public Result offlineQRCode(Integer siteId, Integer storeId, Integer storeAdminId, String tradesId) {
        return orders2Service.offlineQRCode(siteId, storeId, storeAdminId, tradesId);
    }

}
