package com.jk51.modules.store.web;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.MerchantClerkInfo;
import com.jk51.model.Stores;
import com.jk51.model.order.SBStores;
import com.jk51.model.order.Store;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.store.service.BStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-24
 * 修改记录:
 */
@Controller
@RequestMapping("/store")
public class StoreController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BStoreService bStoreService;

    @Autowired
    private BStoresMapper bStoresMapper;

    /**
     * 新增门店的同时赋予门店一个超级管理员，
     *
     * @return
     */
    @RequestMapping(value = "/insertStoreInfo")
    @ResponseBody
    public String insertStoreInfo(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        return bStoreService.insertStoreInfo(params);

    }

    /**
     * 更新门店信息
     *
     * @return
     */
    @RequestMapping(value = "/updateStore")
    @ResponseBody
    public String updateStore(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        return bStoreService.updateStore(params);
    }


    /**
     * 展示商户所有门店信息
     *
     * @return
     */
    @RequestMapping(value = "/selectAllStores")
    @ResponseBody
    public List<Store> selectAllStores(Integer site_id, String storeName, String storeNumber) {
        return bStoreService.selectAllStore(site_id, storeName, storeNumber);
    }
    /**
     * 商户所有门店信息（全部包括禁用）
     *
     * @return
     */
    @RequestMapping(value = "/selectStoreAll")
    @ResponseBody
    public List<Store> selectStoreAll(Integer site_id) {
        return bStoreService.selectStoreAll(site_id);
    }

    @RequestMapping("selectStoreInfoByCityIds")
    @ResponseBody
    public ReturnDto selectStoreInfoByCityIds(Integer siteId, String cityIds) {
        try {
            return ReturnDto.buildSuccessReturnDto(bStoreService.selectStoreInfoByCityIds(siteId, cityIds));
        } catch (Exception e) {
            logger.error("error, {}", e);
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }

    /**
     * 根据storeIds拿到所有门店信息
     * @param siteId
     * @param storeIds
     * @return
     */
    @RequestMapping("selectStoreInfoByStoreIds")
    @ResponseBody
    public ReturnDto selectStoreInfoByStoreIds(Integer siteId, String storeIds) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            List<String> storeList = Arrays.asList(storeIds.split(","));
            List<Stores> list = bStoresMapper.getStoreByStoreIds(storeList, siteId);
            String stores = JacksonUtils.obj2json(list);
            map.put("storeList",stores);
            map.put("storeNum",list.size());
            return ReturnDto.buildSuccessReturnDto(map);
        } catch (Exception e) {
            logger.error("error, {}", e);
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }


    /**
     * 更改门店状态
     */
    @RequestMapping(value = "/updateStoreStatusByStoreIds")
    @ResponseBody
    public String updateStoreStatusByStoreIds(Integer siteId, Integer type, String ids) {
        return bStoreService.updateStoreStatusByStoreIds(siteId, type, ids);

    }


    /**
     * 条件分页查询门店信息
     *
     * @return
     */
    @RequestMapping(value = "/selectByFuzzy")
    @ResponseBody
    public PageInfo selectByFuzzy(Integer siteId, String name, String storeNumber, Integer type, Integer storesStatus, Integer isQjd, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Store> merchantClerkInfos = bStoreService.selectByFuzzy(siteId, name, storeNumber, type, storesStatus, isQjd, null);
        PageInfo pageInfo = new PageInfo<>(merchantClerkInfos);
        return pageInfo;
    }

    /**
     * 条件查询门店信息
     *
     * @return
     */
    @RequestMapping(value = "/selectByFuzzyNoPage")
    @ResponseBody
    public List<Store> selectByFuzzyNoPage(Integer siteId, String name, String storeNumber, Integer type, Integer storesStatus, Integer isQjd, String service_support) {
        List<Store> merchantClerkInfos = bStoreService.selectByFuzzy(siteId, name, storeNumber, type, storesStatus, isQjd, service_support);
        return merchantClerkInfos;
    }


    /**
     * 根据门店主键查门店信息
     *
     * @param site_id 商户站点
     * @param id      门店id
     * @return
     */
    @RequestMapping(value = "/selectStoreByStoreKey")
    @ResponseBody
    public Store selectStoreByStoreKey(Integer site_id, Integer id) {
        return bStoreService.selectStoreByStoreKey(site_id, id);
    }


    /**
     * 更改门店自主定价权限
     *
     * @return
     */
    @RequestMapping(value = "/updateOwnPricingType")
    @ResponseBody
    public String updateOwnPricingType(Integer OwnPricingType, Integer siteId, String ids) {
        return bStoreService.updateOwnPricingType(OwnPricingType, siteId, ids);
    }


    /**
     * @return
     */
    @RequestMapping(value = "/updatePermission")
    @ResponseBody
    public Integer updatePermission(String type, String ids, Integer siteId, String metaType) {
        return bStoreService.updatePermission(type, ids, siteId, metaType);
    }

    /**
     * 会员注册邀请码更改设置
     */
    @RequestMapping(value = "/updateShowInviteCode")
    @ResponseBody
    public Integer updateShowInviteCode(Integer compuInviteCode, Integer winxinInviteCode, Integer siteId) {
        return bStoreService.updateShowInviteCode(winxinInviteCode, compuInviteCode, siteId);
    }

    /**
     * 门店自动分单设置
     *
     * @param orderAssignType 分单模式
     * @param merchantId      商家id
     * @param storesIds       总仓门店ids
     * @param asssids         分单门店ids
     * @param time            分单事件
     * @param storetype       分单类型
     * @return
     */
    @RequestMapping(value = "/setOrderAssignType")
    @ResponseBody
    public Integer setOrderAssignType(Integer orderAssignType, Integer merchantId, String storesIds, String asssids, String time, Integer storetype, String storesIdslin) {
        return bStoreService.setOrderAssignType(orderAssignType, merchantId, storesIds, asssids, time, storetype,storesIdslin);
    }


    @RequestMapping(value = "/findStoreBycity")
    @ResponseBody
    public List<Store> findStoreBycity(Integer siteId, String city) {
        return bStoreService.findStoreBycity(siteId, city);
    }

    @RequestMapping(value = "/selectBySiteIdAndCityAndStoreName")
    @ResponseBody
    public List<SBStores> selectBySiteIdAndCityAndStoreName(Integer siteId, String city, String storeName) {
        return bStoreService.selectBySiteIdAndCityAndStoreName(siteId, city, storeName);
    }

    //获取商家门店最大id
    @RequestMapping(value = "/getStoreId")
    @ResponseBody
    public Integer getStoreId(Integer siteId) {
        return bStoreService.getStoreId(siteId);
    }

    //获取商家所有门店ids
    @RequestMapping(value = "/allstoreids")
    @ResponseBody
    public String allstoreids(Integer siteId) {
        return bStoreService.allstoreids(siteId);
    }

    @RequestMapping(value = "/selectByPremaryKey")
    @ResponseBody
    public Map<String, Object> selectByPremaryKey(Integer merchantId) {
        return bStoreService.selectByPremaryKey(merchantId);
    }

    /**
     * 更改商家发票管理
     *
     * @param merchant_id
     * @param invoice_is
     * @return
     */
    @RequestMapping(value = "/updateInvoice")
    @ResponseBody
    public Integer updateInvoice(Integer merchant_id, Integer invoice_is) {
        return bStoreService.updateInvoice(merchant_id, invoice_is);
    }

    @RequestMapping(value = "/editSeller")
    @ResponseBody
    public void editSeller(HttpServletRequest request) {
        Map map = ParameterUtil.getParameterMap(request);
        bStoreService.editSeller(map);
    }

    @RequestMapping(value = "/querySeller")
    @ResponseBody
    public Map querySeller(Map param) {
        return bStoreService.querySeller(param);
    }

    /**
     * 更改店员聊天开关权限
     *
     * @param siteId
     * @param meta_val
     * @return
     */
    @RequestMapping(value = "/updateClerkChatType")
    @ResponseBody
    public Integer updateClerkChatType(Integer siteId, String meta_val, String storeIds) {
        return bStoreService.updateClerkChatType(siteId, meta_val, storeIds);
    }

    /**
     * 获取该商家下所有店员
     *
     * @param siteId
     * @param storename
     * @param mobile
     * @param clerkname
     * @return
     */
    @RequestMapping(value = "/selectPageAllClerk")
    @ResponseBody
    public PageInfo selectPageAllClerk(Integer siteId, String storename, String mobile, String clerkname, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<MerchantClerkInfo> merchantClerkInfos = bStoreService.selectAllClerk(siteId, storename, mobile, clerkname);
        PageInfo pageInfo = new PageInfo<>(merchantClerkInfos);
        return pageInfo;
    }

    /**
     * 获取该商家下所有店员
     *
     * @param siteId
     * @param storename
     * @param mobile
     * @param clerkname
     * @return
     */
    @RequestMapping(value = "/selectAllClerk")
    @ResponseBody
    public List<MerchantClerkInfo> selectAllClerk(Integer siteId, String storename, String mobile, String clerkname) {
        List<MerchantClerkInfo> merchantClerkInfos = bStoreService.selectAllClerk(siteId, storename, mobile, clerkname);
        return merchantClerkInfos;
    }

    /**
     * 获取该商家下所有店员
     *
     * @param siteId
     * @param mobile
     * @param name
     * @param ivcode
     * @param storeId
     * @param status
     * @return
     */
    @RequestMapping(value = "/selectPagesAllClerk")
    @ResponseBody
    public PageInfo selectPagesAllClerk(Integer siteId, String mobile, String name, String ivcode, Integer storeId, Integer status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<MerchantClerkInfo> merchantClerkInfos = bStoreService.selectAllClerk(siteId, mobile, name, ivcode, storeId, status);
        PageInfo pageInfo = new PageInfo<>(merchantClerkInfos);
        return pageInfo;
    }

    /**
     * 根据id查询相应的门店信息
     *
     * @param siteId
     * @param ids
     * @return
     */
    @GetMapping("/selectByids")
    @ResponseBody
    public ReturnDto selectByids(Integer siteId, String ids, Integer storesStatus) {
        return bStoreService.selectByids(siteId, ids, storesStatus);
    }

    @RequestMapping("/selectByStoreId")
    @ResponseBody
    public ReturnDto selectByStoreId(Integer siteId, Integer storeId, Integer storesStatus) {
        return bStoreService.selectByStoreId(siteId, storeId, storesStatus);
    }

    /**
     * 更改订单改价权限
     *
     * @return
     */
    @RequestMapping(value = "/updatUpPricingType")
    @ResponseBody
    public String updateUpPricingType(Integer UpPricingType, Integer siteId, String ids) {
        return bStoreService.updateUpricingType(UpPricingType, siteId, ids);
    }
}
