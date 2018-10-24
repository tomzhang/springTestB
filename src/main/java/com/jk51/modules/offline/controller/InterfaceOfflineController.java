
package com.jk51.modules.offline.controller;


import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.GoodsInfo;
import com.jk51.model.order.Store;
import com.jk51.modules.offline.service.*;
import com.jk51.modules.offline.utils.ErpMerchantUtils;
import com.jk51.modules.order.service.DistributeOrderService;
import com.jk51.modules.store.service.BStoreService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by admin on 2017/2/8.
 */
@RestController
@ResponseBody
@RequestMapping("/offline")
public class InterfaceOfflineController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceOfflineController.class);
    @Autowired
    private OfflineMemberService offlineMemberService;
    @Autowired
    private OfflineIntegrateService offlineIntegrateService;
    @Autowired
    private OfflineOrderService offlineOrderService;
    @Autowired
    private OfflineCheckService offlineCheckService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private BStoreService storesService;
    @Autowired
    private GoodsERPServices goodsERPServices;
    @Autowired
    private DistributeOrderService distributeOrderService;


    /**
     * 查询会员信息接口
     *
     * @param request
     * @return
     */
    @RequestMapping("getMemberInfo")
    @ResponseBody
    public Map<String, Object> getMemberInfo(HttpServletRequest request) {
        Map requestParams = ParameterUtil.getParameterMap(request);
        Map<String, Object> responseParams = new HashMap<>();
        Object siteId = requestParams.get("siteId");
        Object mobile = requestParams.get("mobile");
        String inviteCode = StringUtil.isEmpty(requestParams.get("inviteCode")) ? "" : requestParams.get("inviteCode").toString();
        if (siteId == null || mobile == null) {
            responseParams.put("code", 500);
            responseParams.put("msg", "参数不完整，重新请求");
            return requestParams;
        }
        return offlineMemberService.getuser(Integer.parseInt(siteId.toString()), mobile.toString(), inviteCode);
    }

    /**
     * 修改会员信息接口
     * 缺少一个修改本地会员信息的接口
     *
     * @param request
     * @return
     */
    @RequestMapping("updateMemberInfo")
    @ResponseBody
    public Map<String, Object> updateMemberInfo(HttpServletRequest request) {
        Map requestParams = ParameterUtil.getParameterMap(request);
        Map<String, Object> responseParams = new HashMap<>();
        Object siteId = requestParams.get("siteId");
        Object mobile = requestParams.get("mobile");
        if (siteId == null || mobile == null || requestParams.containsKey("name") == false) {
            responseParams.put("code", 400);
            responseParams.put("msg", "参数不完整，重新请求");
            return responseParams;
        }
        return offlineMemberService.updateMemberinfo(requestParams);//修改线下会员信息
    }

    /**
     * 获取用户的线下总积分
     *
     * @param siteId
     * @param mobile
     * @return
     */
    @PostMapping("getTotalOffIntegral")
    public Map<String, Object> getIntegralOff(Integer siteId, String mobile) {
        return offlineIntegrateService.getOffTotalScore(siteId, mobile);
    }

    @PostMapping("testCallBackPanDian")
    public ReturnDto testCallBackPanDian(Integer siteId, String pandianNum, String unitNO) {
        try {
            LOGGER.info("手动推送盘点信息===testCallBackPanDian,siteId:{},pandianNum:{},unitNO:{}.", siteId, pandianNum, unitNO);
            offlineCheckService.updateOfflinetqtyReload(siteId, pandianNum, unitNO);
            return ReturnDto.buildSuccessReturnDto();
        } catch (Exception e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }

    @PostMapping("getUnsyncCheck")
    public Integer getUnsyncCheck(Integer siteId, String pandianNum, String unitNO) {
        try {
            List<Integer> uncheckList = offlineCheckService.getUnsyncCheck(siteId, pandianNum, unitNO);
            if (CollectionUtils.isEmpty(uncheckList)) {
                return 0;
            } else {
                return uncheckList.size();
            }
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 获取商品信息
     *
     * @param requestParams
     * @return
     */
    @PostMapping("getDrugsByTypes")
    public ReturnDto getDrugsByTypes(@RequestBody Map<String, Object> requestParams) {
        try {
            Integer siteId = Integer.parseInt(requestParams.get("siteId").toString());
            String type = requestParams.get("type").toString();//获取的商品类型
            LOGGER.info("查询的商品信息参数：siteId:{},type:{}.", siteId, type);
            return goodsERPServices.getGoodsListByType(requestParams);
        } catch (Exception e) {
            LOGGER.info("查询getDrugsByTypes:{}", e.getMessage());
        }
        return ReturnDto.buildFailedReturnDto("失败");
    }

    /**
     * 查询有库存的门店信息,仅限自提
     *
     * @param requestParams
     * @return
     */
    @PostMapping("getStorageStores")
    public ReturnDto getStorageStores(@RequestBody Map<String, Object> requestParams) {
        try {
            Integer siteId = Integer.parseInt(requestParams.get("siteId").toString());
            List<LinkedHashMap<String, Object>> drugsList = JacksonUtils.json2listMap(JacksonUtils.obj2json(requestParams.get("drugs")));//获取商品信息
            LOGGER.info("查询的商品信息参数：siteId:{},storeList:{}.", siteId, drugsList);
            List<GoodsInfo> goodsInfoList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(drugsList)) {
                for (LinkedHashMap<String, Object> goodsMap : drugsList) {
                    GoodsInfo goodsInfo = new GoodsInfo();
                    goodsInfo.setGoodsCode(goodsMap.get("goodsNo").toString());
                    goodsInfo.setControlNum(Integer.parseInt(goodsMap.get("drugNum").toString()));
                    goodsInfoList.add(goodsInfo);
                }
                List<Store> storeList = storesService.selectByFuzzy(siteId, null, null, null, 1, null, "160");
                List<Store> storesHasStorageList = distributeOrderService.getBestStorageStore(siteId, storeList, goodsInfoList);
                List<Map<String, Object>> requestStoreList = new ArrayList<>();
                for (Store store : storesHasStorageList) {
                    Map<String, Object> storeMap = new HashMap<>();
                    storeMap.put("id", store.getId());
                    storeMap.put("unitNo", store.getStoresNumber());
                    storeMap.put("tradeName", store.getName());
                    storeMap.put("address", store.getAddress());
                    storeMap.put("storeMobile", store.getTel());
                    storeMap.put("longitude", store.getGaodeLng());
                    storeMap.put("latitude", store.getGaodeLat());
                    requestStoreList.add(storeMap);
                }
                return ReturnDto.buildSuccessReturnDto(requestStoreList);
            }
        } catch (Exception e) {
            LOGGER.info("查询getStorageStores:{}", e.getMessage());
        }
        return ReturnDto.buildFailedReturnDto("失败");
    }

}
