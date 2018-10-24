package com.jk51.modules.store.web;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.index.service.StoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-24
 * 修改记录:
 */
@Controller
@RequestMapping("/store")
public class AuthorityController {
    @Autowired
    private StoresService storesService;

    /**
     * 查找具有自主定价权限的门店ids
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/authority/selectByOwnPricingTypeAndSiteId")
    public Map<String, Object> selectByOwnPricingTypeAndSiteId(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectByOwnPricingTypeAndSiteId(siteId);
    }

    /**
     * 查找具有订单改价权限的门店ids
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/authority/selectByUpPricingTypeAndSiteId")
    public String selectByUpPricingTypeAndSiteId(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectByUpPricingTypeAndSiteId(siteId);
    }

    /**
     * 判断该门店是否具有自主定价权限
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/authority/isByOwnPricingTypeAndSiteIdAndStoreId")
    public Boolean isByOwnPricingTypeAndSiteIdAndStoreId(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        Integer id = Integer.parseInt(paramsMap.get("id").toString());
        return storesService.selectByOwnPricingTypeAndSiteIdAndStoreId(siteId, id);
    }

    /**
     * 查找允许创建优惠活动的所有门店ids
     */
    @ResponseBody
    @PostMapping("/authority/selectByOwnPromotionTypeAndSiteId")
    public String selectByOwnPromotionTypeAndSiteId(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectByOwnPromotionTypeAndSiteId(siteId);
    }

    /**
     * 判断该门店是否具有创建优惠活动的权限
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/authority/isByOwnPromotionTypeAndSiteIdAndStoreId")
    public Boolean isByOwnPromotionTypeAndSiteIdAndStoreId(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        Integer id = Integer.parseInt(paramsMap.get("id").toString());
        return storesService.isByOwnPromotionTypeAndSiteIdAndStoreId(siteId, id);
    }

    /**
     * 获取具有修改积分权限的门店ids
     * 1:禁止门店;2:所有门店;3:指定门店;
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/authority/selectidsByPermissionTypeFromMeta")
    public String selectidsByPermissionTypeFromMeta(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectidsByPermissionTypeFromMeta(siteId);
    }

    /**
     * 获取具有退款的门店ids
     * 1:禁止门店;2:所有门店;3:指定门店;
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/authority/selectIdsByStorereFundPermissionFromMeta")
    public String selectIdsByStorereFundPermissionFromMeta(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectIdsByStorereFundPermissionFromMeta(siteId);
    }

    /**
     * 获取具有具有总仓权限的门店ids
     *
     * @return
     * @metaType site_general_warehouse_config
     */
    @ResponseBody
    @PostMapping("/authority/selectidsBywarehouseFromMeta")
    public String selectidsBywarehouseFromMeta(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectidsBywarehouseFromMeta(siteId);
    }
    /**
     * 获取具有具有总仓权限的门店ids
     *
     * @return
     * @metaType site_general_warehouse_config
     */
    @ResponseBody
    @PostMapping("/authority/selectidsBywarehouseFromMetaLin")
    public String selectidsBywarehouseFromMetaLin(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectidsBywarehouseFromMetaLin(siteId);
    }
    /**
     * 获取具有接单权限的门店ids
     *
     * @return
     * @metaType auto_assign_type
     */
    @ResponseBody
    @PostMapping("/authority/selectassignFromMeta")
    public String selectassignFromMeta(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectassignFromMeta(siteId);
    }

    /**
     * selfsupport服务
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/authority/selectSelfSupportBySiteIdAndStoreId")
    public String selectSelfSupportBySiteIdAndStoreId(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        Integer Storeid = Integer.parseInt(paramsMap.get("storeId").toString());
        return storesService.selectSelfSupportBySiteIdAndStoreId(siteId, Storeid);
    }

    /**
     * 获取门店分单设置
     *
     * @return
     * @metaType auto_assign_type
     */
    @ResponseBody
    @PostMapping("/authority/selectassign")
    public Map selectassign(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectassign(siteId);
    }

    /**
     * 获取邀请码显示设置
     */
    @ResponseBody
    @PostMapping("/authority/selectinvitecode")
    public Map selectinvitecode(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectinvitecode(siteId);
    }


    /**
     * 获取具有退款设置
     * 1:禁止门店;2:所有门店;3:指定门店;
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/authority/selectIdsByStorereFundPermission")
    public Map selectIdsByStorereFundPermission(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectIdsByStorereFundPermission(siteId);
    }

    /**
     * 获取门店积分设置
     * 1:禁止门店;2:所有门店;3:指定门店;
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/authority/selectidsByPermissionType")
    public Map selectidsByPermissionType(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectidsByPermissionType(siteId);
    }

    /**
     * 判断该门店是否具有退款权限
     */
    @ResponseBody
    @PostMapping("/authority/selectRefundByprimaryId")
    public Boolean selectRefundByprimaryId(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        Integer storeId = Integer.parseInt(paramsMap.get("storeId").toString());
        return storesService.selectRefundByprimaryId(siteId, storeId);
    }

    //获取店员chat权限设置总开关
    @ResponseBody
    @PostMapping("/authority/selectClerkChatType")
    public String selectClerkChatType(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectClerkChatType(siteId);
    }

    /**
     * 查询具有控制聊天功能的店员ids
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/authority/selectClerkChat")
    public List<Integer> selectClerkChat(HttpServletRequest request) {
        Map<String, Object> paramsMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(paramsMap.get("siteId").toString());
        return storesService.selectClerkChat(siteId);
    }
}
