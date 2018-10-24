package com.jk51.model.order.response;

import com.jk51.model.concession.result.GiftResult;
import com.jk51.model.concession.result.MoneyResultForPromotions;
import com.jk51.model.concession.result.Result;
import com.jk51.model.order.GoodsInfo;
import com.jk51.model.order.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:前台查询分单价格，以及下单时返回分单的最佳门店，以及商品最终的价格信息
 * 作者: baixiongfei
 * 创建日期: 2017/2/23
 * 修改记录:
 */
@SuppressWarnings("unused")
public class DistributeResponse {

    //最优门店，用于分单,用于送货上门订单
    private Store store;
    private int minDistance;

    //订单的最初价格
    private int orderOriginalPrice;

    //订单的运费
    private int orderFreight;
    //o2o超过4-6公里向客户收的运费
    private int freightCommission;

    //订单的运费
    private Integer isO2O;

    //积分抵扣金额
    private int integralDeductionPrice;

    //用户存量积分
    private int integral;

    //最终的订单金额
    private int orderRealPrice;

    //分销商品的总价格
    private int distributePrice;

    //分销商品的提示信息 json格式
    private String distributeTip;
    //分销商品折扣金额
    private Integer distributeDiscountPrice;

    // 需要多少积分抵扣
    private Integer needIntegral;

    /* -- 优惠券，活动相关参数 -- */
    /**
     * 是否使用了优惠券计算出了优惠
     */
    private boolean useCoupon = false;

    /**
     * 优惠金额，包括优惠券优惠金额和活动优惠金额
     */
    private int concessionDeductionPrice = 0;

    /**
     * 优惠券优惠金额
     */
    private int couponDeductionPrice = 0;

    /**
     * 活动优惠金额
     */
    private int proRuleDeductionPrice = 0;

    /**
     * 生效的活动ID，用逗号分隔
     */
    private String efficientPromotionsActivityId = "";

    /**
     * 赠品列表相关信息（包括赠品券赠品和活动赠品）
     */
    private List<GiftResult> giftResultList = new ArrayList<>();

    /**
     * 客户端以选择的赠品
     */
    private List<GiftResult> selectedGiftResultList = new ArrayList<>();

    /**
     * 优惠金钱的活动的信息集合
     */
    private List<MoneyResultForPromotions> moneyResultForPromotionsList = new ArrayList<>();

    /**
     * 预下单计算的优惠结果，包括优惠券和活动
     */
    private Result concessionResult;

    /**
     * 优惠总金额（商品总价格+运费-支付金额）
     */
    private int couponALLPrice = 0;

    /* -- 优惠券，活动相关参数 END -- */

    /**
     * 计算订单价格时的商品信息
     */
    private List<GoodsInfo> goodsInfoInfos;

    /**
     * 获取商家设置支持订单类型
     */
    private Map<String, Object> settingDisMap;
    /**
     * 是否是分销订单
     */
    private boolean distributeGoods;

    private Integer postageDiscount;

    /* -- setter & getter -- */

    public void setPostageDiscount(Integer postageDiscount) {
        this.postageDiscount = postageDiscount;
    }

    public Integer getPostageDiscount() {
        return postageDiscount;
    }

    public void setDistributeGoods(boolean distributeGoods) {
        this.distributeGoods = distributeGoods;
    }
    public boolean getDistributeGoods(){
        return this.distributeGoods;
    }

    public void setSettingDisMap(Map<String, Object> settingDisMap) {
        this.settingDisMap = settingDisMap;
    }

    public Map<String, Object> getSettingDisMap() {
        return settingDisMap;
    }

    public List<GoodsInfo> getGoodsInfoInfos() {
        return goodsInfoInfos;
    }

    public void setGoodsInfoInfos(List<GoodsInfo> goodsInfoInfos) {
        this.goodsInfoInfos = goodsInfoInfos;
    }

    public List<GiftResult> getSelectedGiftResultList() {
        return selectedGiftResultList;
    }

    public void setSelectedGiftResultList(List<GiftResult> selectedGiftResultList) {
        this.selectedGiftResultList = selectedGiftResultList;
    }

    public List<MoneyResultForPromotions> getMoneyResultForPromotionsList() {
        return moneyResultForPromotionsList;
    }

    public void setMoneyResultForPromotionsList(List<MoneyResultForPromotions> moneyResultForPromotionsList) {
        this.moneyResultForPromotionsList = moneyResultForPromotionsList;
    }

    public void setCouponALLPrice(int couponALLPrice) {
        this.couponALLPrice = couponALLPrice;
    }

    public int getCouponALLPrice() {
        return couponALLPrice;
    }
    /* 超级优惠功能迭代无用的字段
    //是否有满赠活动
    private String isHaveGiftProRuleActivity;

    //满赠活动对应的优惠活动主键
    private Integer giftpromotionsRuleId;

    //满赠活动对应的活动主键
    private Integer giftpromotionsActivityId;

    private Map<String, Object> giftRuleMsg;

    private List<UsePromotionsParams> proRuleList;

    private Map discountDetail;//优惠活动，优惠券详情*/

    /* -- setter & getter -- */

    public String getEfficientPromotionsActivityId() {
        return efficientPromotionsActivityId;
    }

    public void setEfficientPromotionsActivityId(String efficientPromotionsActivityId) {
        this.efficientPromotionsActivityId = efficientPromotionsActivityId;
    }

    public int getConcessionDeductionPrice() {
        return concessionDeductionPrice;
    }

    public void setConcessionDeductionPrice(int concessionDeductionPrice) {
        this.concessionDeductionPrice = concessionDeductionPrice;
    }

    public boolean isUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(boolean useCoupon) {
        this.useCoupon = useCoupon;
    }

    public List<GiftResult> getGiftResultList() {
        return giftResultList;
    }

    public void setGiftResultList(List<GiftResult> giftResultList) {
        this.giftResultList = giftResultList;
    }

    public Result getConcessionResult() {
        return concessionResult;
    }

    public void setConcessionResult(Result concessionResult) {
        this.concessionResult = concessionResult;
    }

    public void setFreightCommission(int freightCommission) {
        this.freightCommission = freightCommission;
    }

    public int getFreightCommission() {
        return freightCommission;
    }

    //是否走O2O
    public void setIsO2O(Integer isO2O) {
        this.isO2O = isO2O;
    }

    public Integer getIsO2O() {
        return isO2O;
    }

    public Integer getDistributeDiscountPrice() {
        return distributeDiscountPrice;
    }

    public void setDistributeDiscountPrice(Integer distributeDiscountPrice) {
        this.distributeDiscountPrice = distributeDiscountPrice;
    }

    public String getDistributeTip() {
        return distributeTip;
    }

    public void setDistributeTip(String distributeTip) {
        this.distributeTip = distributeTip;
    }

    public int getDistributePrice() {
        return distributePrice;
    }

    public void setDistributePrice(int distributePrice) {
        this.distributePrice = distributePrice;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public int getOrderOriginalPrice() {
        return orderOriginalPrice;
    }

    public void setOrderOriginalPrice(int orderOriginalPrice) {
        this.orderOriginalPrice = orderOriginalPrice;
    }

    public int getOrderFreight() {
        return orderFreight;
    }

    public void setOrderFreight(int orderFreight) {
        this.orderFreight = orderFreight;
    }

    public int getIntegralDeductionPrice() {
        return integralDeductionPrice;
    }

    public void setIntegralDeductionPrice(int integralDeductionPrice) {
        this.integralDeductionPrice = integralDeductionPrice;
    }

    public int getCouponDeductionPrice() {
        return couponDeductionPrice;
    }

    public void setCouponDeductionPrice(int couponDeductionPrice) {
        this.couponDeductionPrice = couponDeductionPrice;
    }

    public int getOrderRealPrice() {
        return orderRealPrice;
    }

    public void setOrderRealPrice(int orderRealPrice) {
        this.orderRealPrice = orderRealPrice;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public Integer getNeedIntegral() {
        return needIntegral;
    }

    public void setNeedIntegral(Integer needIntegral) {
        this.needIntegral = needIntegral;
    }

    public void setProRuleDeductionPrice(int proRuleDeductionPrice) {
        this.proRuleDeductionPrice = proRuleDeductionPrice;
    }

    public int getProRuleDeductionPrice() {
        return proRuleDeductionPrice;
    }

    public void setMinDistance(int minDistance) {
        this.minDistance = minDistance;
    }

    public int getMinDistance() {
        return minDistance;
    }

    @Override
    public String toString() {
        return "DistributeResponse{" +
            "store=" + store +
            ", minDistance=" + minDistance +
            ", orderOriginalPrice=" + orderOriginalPrice +
            ", orderFreight=" + orderFreight +
            ", freightCommission=" + freightCommission +
            ", isO2O=" + isO2O +
            ", integralDeductionPrice=" + integralDeductionPrice +
            ", integral=" + integral +
            ", orderRealPrice=" + orderRealPrice +
            ", distributePrice=" + distributePrice +
            ", distributeTip='" + distributeTip + '\'' +
            ", distributeDiscountPrice=" + distributeDiscountPrice +
            ", needIntegral=" + needIntegral +
            ", useCoupon=" + useCoupon +
            ", concessionDeductionPrice=" + concessionDeductionPrice +
            ", couponDeductionPrice=" + couponDeductionPrice +
            ", proRuleDeductionPrice=" + proRuleDeductionPrice +
            ", giftResultList=" + giftResultList +
            ", concessionResult=" + concessionResult +
                ", couponALLPrice=" + couponALLPrice +
            '}';
    }
}
