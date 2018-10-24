package com.jk51.modules.store.web;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.goods.YbStoresGoodsPrice;
import com.jk51.modules.store.service.SGoodsPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: zhangkuncheng
 * 创建日期: 2017-03-22
 * 修改记录:
 */
@Controller
@RequestMapping("/store")
public class SGoodPriceController {

    @Autowired
    private SGoodsPriceService getGoodsList;

    /**
     * 价格列表
     */
    @RequestMapping(value = "/findByGoodsList")
    @ResponseBody
    public Map<String, Object> queryList(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        result = getGoodsList.findByGoodsList(param);
        return result;
    }


    /**
     * 修改门店价格
     */
    @RequestMapping(value = "/updateYBPrice")
    @ResponseBody
    public int updateYBPrice(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);

        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer goodsPrice = Integer.parseInt(String.valueOf(param.get("goodsPrice")));
        Integer goodsId = Integer.parseInt(String.valueOf(param.get("goodsId")));
        Integer erpPrice = null;
        if (param.get("erpPrice") != null) {
            erpPrice = Integer.parseInt(String.valueOf(param.get("erpPrice")));
        }
        return getGoodsList.updateYBPrice(storeId, siteId, goodsPrice, goodsId, erpPrice);
    }

    /**
     * 刷新价格
     */
    @RequestMapping(value = "/refreshYBTime")
    @ResponseBody
    public int refreshYBTime(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        return getGoodsList.refreshYBTime(param);
    }

    /**
     * 恢复门店价格
     */
    @RequestMapping(value = "/resumeStorePriceAction")
    @ResponseBody
    public int resumeStorePriceAction(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer type = Integer.parseInt(String.valueOf(param.get("type")));
        Integer goodsId = Integer.parseInt(String.valueOf(param.get("goodsId")));
        return getGoodsList.resumeStorePriceAction(storeId, siteId, type, goodsId);
    }

    /**
     * 若门店对该商品设置价格，取门店价格
     */
    @RequestMapping(value = "/getGoodsInfoPriceByStore")
    @ResponseBody
    public YbStoresGoodsPrice getGoodsInfoPriceByStore(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        Integer goodsId = Integer.parseInt(String.valueOf(param.get("goodsId")));
        return getGoodsList.getGoodsInfoPriceByStore(storeId, siteId, goodsId);
    }

    /**
     * 禁用或者启用门店价格
     */
    @RequestMapping(value = "/updateStorePriceFlag")
    @ResponseBody
    public ReturnDto updateStorePriceFlag(Integer siteId, Integer storeId, String goodsIds, Integer delFlag) {
        return getGoodsList.updateStorePriceFlag(siteId, storeId, goodsIds, delFlag);
    }
}
