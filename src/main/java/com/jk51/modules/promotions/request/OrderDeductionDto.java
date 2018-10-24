package com.jk51.modules.promotions.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.promotions.request.
 * author   :zw
 * date     :2017/8/11
 * Update   :
 * 活动筛选和抵扣参数
 */
public class OrderDeductionDto {
    private boolean isFirstOrder;//是否首单
    private Integer orderType;//订单类型
    private Integer applyChannel;//适用渠道
    private Integer storeId;//门店id
    private Integer postFee;//邮费
    private Integer orderFee;//实付金额 商品金额-积分+邮费
    /**
     * map数据如：goodsId：1234，num:2,goodsPrice:9000
     */
    private List<Map<String, Integer>> goodsInfo;
    private Integer areaId;//地址id，省的id
    private Integer userId;//用户id
    private Map promotionsIdsMap;//活动规则id多个规则
    private Integer siteId;//商家id

    public OrderDeductionDto(){
         super();
    }

    public  OrderDeductionDto(ProRuleMessageParam proRuleMessageParam,List<Map<String,Object>> paramMap){
        this.isFirstOrder=proRuleMessageParam.isFirstOrder();
        this.orderType=proRuleMessageParam.getOrderType();
        this.applyChannel=proRuleMessageParam.getApplyChannel();
        this.storeId=proRuleMessageParam.getStoreId();
        this.postFee=proRuleMessageParam.getPostFee();
        this.orderFee=proRuleMessageParam.getOrderFee();
        this.goodsInfo=proRuleMessageParam.getGoodsInfo();
        this.areaId=proRuleMessageParam.getAreaId();
        this.userId=proRuleMessageParam.getUserId();
        this.siteId=proRuleMessageParam.getSiteId();
        Map<Object,Object> map=new HashMap<Object,Object>();
        for(int i=0;i<paramMap.size();i++){
            map.put(i+1,paramMap.get(i).get("proActivityId"));
        }
        this.promotionsIdsMap=map;
        this.siteId=proRuleMessageParam.getSiteId();
    }

    public boolean isFirstOrder() {
        return isFirstOrder;
    }

    public void setFirstOrder(boolean firstOrder) {
        isFirstOrder = firstOrder;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getApplyChannel() {
        return applyChannel;
    }

    public void setApplyChannel(Integer applyChannel) {
        this.applyChannel = applyChannel;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getPostFee() {
        return postFee;
    }

    public void setPostFee(Integer postFee) {
        this.postFee = postFee;
    }

    public Integer getOrderFee() {
        return orderFee;
    }

    public void setOrderFee(Integer orderFee) {
        this.orderFee = orderFee;
    }

    public List<Map<String, Integer>> getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(List<Map<String, Integer>> goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Map getPromotionsIdsMap() {
        return promotionsIdsMap;
    }

    public void setPromotionsIdsMap(Map promotionsIdsMap) {
        this.promotionsIdsMap = promotionsIdsMap;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public String toString() {
        return "OrderMessageParams{" +
                "isFirstOrder=" + isFirstOrder +
                ", orderType=" + orderType +
                ", applyChannel=" + applyChannel +
                ", storeId=" + storeId +
                ", postFee=" + postFee +
                ", orderFee=" + orderFee +
                ", goodsInfo=" + goodsInfo +
                ", areaId=" + areaId +
                ", userId=" + userId +
                ", promotionsIdsMap=" + promotionsIdsMap +
                ", siteId=" + siteId +
                '}';
    }
}
