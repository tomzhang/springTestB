package com.jk51.modules.promotions.request;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.BeforeCreateOrderReq;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mqq on 2017/8/11.
 */
public class ProRuleMessageParam {
    private static final Logger logger = LoggerFactory.getLogger(ProRuleMessageParam.class);
    private Integer siteId;
    private Integer userId;
    private Integer storeId;
    private Integer orderType;
    private Integer applyChannel;
    private Integer postFee;
    private Integer areaId;
    private Integer receiverProvinceCode;
    private Integer receiverCityCode;
    private Integer orderFee;
    private List<Map<String, Integer>> goodsInfo;
    private boolean isFirstOrder;
    private Integer couponId;
    private Integer couponPrice;
    private Integer totalPrice;

    public ProRuleMessageParam () {
        super();
    }

    public ProRuleMessageParam (BeforeCreateOrderReq beforeCreateOrderReq, int orderFreight, int couponDeductionPrice) {
        try {
            this.siteId = beforeCreateOrderReq.getSiteId();
            this.userId = beforeCreateOrderReq.getUserId();
            this.orderType = StringUtil.equals(beforeCreateOrderReq.getOrderType(), "1") ? 200 : 100;
            this.applyChannel = 103;
            this.couponPrice = couponPrice;

            if (StringUtils.isNotEmpty(beforeCreateOrderReq.getReceiverProvinceCode())) {
                this.areaId = Integer.parseInt(beforeCreateOrderReq.getReceiverProvinceCode());
                this.receiverProvinceCode = Integer.parseInt(beforeCreateOrderReq.getReceiverProvinceCode());
            }
            if (StringUtils.isNotEmpty(beforeCreateOrderReq.getReceiverCityCode()))
                this.receiverCityCode = Integer.parseInt(beforeCreateOrderReq.getReceiverCityCode());

            this.postFee = orderFreight;

            this.storeId = beforeCreateOrderReq.getStoresId();

            this.couponId = beforeCreateOrderReq.getCouponId();


            List goodList = beforeCreateOrderReq.getOrderGoods().stream().map(goods -> {
                Map temp = new HashMap();
                temp.put("goodsId", goods.getGoodsId());
                temp.put("num", goods.getGoodsNum());
                temp.put("goodsPrice", goods.getGoodsPrice());
                temp.put("origingoodsPrice", goods.getGoodsPrice());
                return temp;
            }).collect(Collectors.toList());
            this.goodsInfo = goodList;

            this.couponPrice = couponDeductionPrice;

            this.totalPrice = beforeCreateOrderReq.getOrderGoods().stream().mapToInt(goodsMap -> goodsMap.getGoodsNum() * goodsMap.getGoodsPrice()).sum();
        } catch (Exception e) {
            logger.info("优惠规则解析错误:" + e);
            return;
        }
    }

    public ProRuleMessageParam (ProRuleMaxParam proRuleMaxParam) {
        try {
            this.siteId = Integer.parseInt(proRuleMaxParam.getSiteId());
            this.userId = Integer.parseInt(proRuleMaxParam.getUserId());
            this.orderType = Integer.parseInt(proRuleMaxParam.getOrderType());
            this.applyChannel = Integer.parseInt(proRuleMaxParam.getApplyChannel());
            this.orderFee = Integer.parseInt(proRuleMaxParam.getOrderFee());


            if (StringUtils.isNotEmpty(proRuleMaxParam.getAreaId()))
                this.areaId = Integer.parseInt(proRuleMaxParam.getAreaId());
            if (StringUtils.isNotEmpty(proRuleMaxParam.getReceiverProvinceCode()))
                this.receiverProvinceCode = Integer.parseInt(proRuleMaxParam.getReceiverProvinceCode());
            if (StringUtils.isNotEmpty(proRuleMaxParam.getReceiverCityCode()))
                this.receiverCityCode = Integer.parseInt(proRuleMaxParam.getReceiverCityCode());
            if (StringUtils.isNotEmpty(proRuleMaxParam.getPostFee()))
                this.postFee = Integer.parseInt(proRuleMaxParam.getPostFee());
            else
                this.postFee = 0;

            if (StringUtils.isNotEmpty(proRuleMaxParam.getStoreId()))
                this.storeId = Integer.parseInt(proRuleMaxParam.getStoreId());

            if (StringUtils.isNotEmpty(proRuleMaxParam.getCouponId()))
                this.couponId = Integer.parseInt(proRuleMaxParam.getCouponId());


            List<LinkedHashMap<String, Object>> list = JacksonUtils.json2listMap(proRuleMaxParam.getGoodsInfo());

            Iterator<LinkedHashMap<String, Object>> it = list.iterator();
            List<Map<String, Integer>> goodsList = new ArrayList<>();
            while (it.hasNext()) {
                Map<String, Integer> goodsMap = new HashMap<String, Integer>();
                LinkedHashMap<String, Object> map = it.next();
                Iterator<Map.Entry<String, Object>> itMap = map.entrySet().iterator();
                while (itMap.hasNext()) {
                    Map.Entry<String, Object> entry = itMap.next();
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Integer)
                        goodsMap.put(key, (Integer) value);
                    else
                        goodsMap.put(key, (int) Math.rint(Float.parseFloat(value.toString())));
                }
                if (null != goodsMap.get("goodsPrice"))
                    goodsMap.put("originGoodsPrice", goodsMap.get("goodsPrice"));
                goodsList.add(goodsMap);
            }
            this.goodsInfo = goodsList;
        } catch (Exception e) {
            logger.info("优惠规则解析错误:" + e);
            return;
        }
    }

    public Integer getUserId () {
        return userId;
    }

    public void setUserId (Integer userId) {
        this.userId = userId;
    }

    public Integer getSiteId () {
        return siteId;
    }

    public void setSiteId (Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getApplyChannel () {
        return applyChannel;
    }

    public void setApplyChannel (Integer applyChannel) {
        this.applyChannel = applyChannel;
    }

    public Integer getAreaId () {
        return areaId;
    }

    public void setAreaId (Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getOrderFee () {
        return orderFee;
    }

    public void setOrderFee (Integer orderFee) {
        this.orderFee = orderFee;
    }

    public Integer getOrderType () {
        return orderType;
    }

    public void setOrderType (Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getPostFee () {
        return postFee;
    }

    public void setPostFee (Integer postFee) {
        this.postFee = postFee;
    }

    public Integer getStoreId () {
        return storeId;
    }

    public void setStoreId (Integer storeId) {
        this.storeId = storeId;
    }

    public List<Map<String, Integer>> getGoodsInfo () {
        return goodsInfo;
    }

    public void setGoodsInfo (List<Map<String, Integer>> goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public boolean isFirstOrder () {
        return isFirstOrder;
    }

    public void setFirstOrder (boolean firstOrder) {
        isFirstOrder = firstOrder;
    }

    public Integer getReceiverCityCode () {
        return receiverCityCode;
    }

    public void setReceiverCityCode (Integer receiverCityCode) {
        this.receiverCityCode = receiverCityCode;
    }

    public Integer getReceiverProvinceCode () {
        return receiverProvinceCode;
    }

    public void setReceiverProvinceCode (Integer receiverProvinceCode) {
        this.receiverProvinceCode = receiverProvinceCode;
    }

    public Integer getCouponId () {
        return couponId;
    }

    public void setCouponId (Integer couponId) {
        this.couponId = couponId;
    }

    public Integer getCouponPrice () {
        return couponPrice;
    }

    public void setCouponPrice (Integer couponPrice) {
        this.couponPrice = couponPrice;
    }

    public Integer getTotalPrice () {
        return totalPrice;
    }

    public void setTotalPrice (Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
}
