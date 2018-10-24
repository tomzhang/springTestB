package com.jk51.model.order;

import com.jk51.model.concession.result.GiftResult;
import com.jk51.model.concession.result.Result;
import com.jk51.model.grouppurchase.GroupPurchase;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 与页面对接：用户在把商品添加到购物车之后，
 * 点击去结算查询订单价格、送货上门的运费等信息时需要传入的参数
 * 作者: baixiongfei
 * 创建日期: 2017/3/8
 * 修改记录:
 */
@SuppressWarnings("unused")
public class BeforeCreateOrderReq {

    private int siteId;

    private int storesId;

    private int buyerId;

    private int userId;

    private String mobile;

    private int integralUse;//用户使用的积分

    private String orderType;//预下单的订单类型，1：送货上门订单，2：门店自提订单，3：药房直购订单

    private String addrCode;//地址编码目前计算运费只精确到省和直辖市，送货上门订单计算运费时使用
    private String address;//收货人地址

    private String receiverProvinceCode;//收货人所在省或者直辖市的编码

    private String receiverCityCode;//收货人城市编码

    private String receiverAddress;//收货人地址

    private List<OrderGoods> orderGoods;

    private Integer distributorId;//分销商id

    // 使用积分兑换
    private Boolean exchange;

    private Integer tradesSource;//订单来源: 110 (网站)，120（微信），130（app）, 140（店员帮用户下单），9999（其它）, 160（支付宝）

    private Integer erpStoreId;

    private Integer erpAreaCode;

    private Map activityMap;

    private boolean isDistributeGoods = false;

    /* -- 优惠券，活动相关参数 -- */
    private Integer couponId;//优惠券ID

    private String groupPurchaseJson;//拼团活动信息json串

    private GroupPurchase groupPurchase;

    private String promActivityIdArr;//活动ID

    /**
     * 顾客选择的赠品
     */
    private String giftGoodsJson;

    /**
     * 顾客选择的赠品
     */
    private List<GiftResult> giftGoods;

    /**
     * 预下单计算的优惠结果，包括优惠券和活动
     */
    private Result concessionResult;

    private String concessionResultJSON;

    /* -- 优惠券，活动相关参数 END -- */


    /* -- setter & getter -- */

    public String getGiftGoodsJson() {
        return giftGoodsJson;
    }

    public void setGiftGoodsJson(String giftGoodsJson) {
        this.giftGoodsJson = giftGoodsJson;
    }

    public String getConcessionResultJSON() {
        return concessionResultJSON;
    }

    public void setConcessionResultJSON(String concessionResultJSON) {
        this.concessionResultJSON = concessionResultJSON;
    }

    public boolean isDistributeGoods() {
        return isDistributeGoods;
    }

    public void setDistributeGoods(boolean distributeGoods) {
        isDistributeGoods = distributeGoods;
    }

    public List<GiftResult> getGiftGoods() {
        return giftGoods;
    }

    public void setGiftGoods(List<GiftResult> giftGoods) {
        this.giftGoods = giftGoods;
    }

    public Result getConcessionResult() {
        return concessionResult;
    }

    public void setConcessionResult(Result concessionResult) {
        this.concessionResult = concessionResult;
    }

    public void setActivityMap(Map activityMap) {
        this.activityMap = activityMap;
    }

    public Map getActivityMap() {
        return activityMap;
    }

    public void setTradesSource(Integer tradesSource) {
        this.tradesSource = tradesSource;
    }

    public Integer getTradesSource() {
        return tradesSource;
    }

    public Integer getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public String getReceiverProvinceCode() {
        return receiverProvinceCode;
    }

    public void setReceiverProvinceCode(String receiverProvinceCode) {
        this.receiverProvinceCode = receiverProvinceCode;
    }

    public String getReceiverCityCode() {
        return receiverCityCode;
    }

    public void setReceiverCityCode(String receiverCityCode) {
        this.receiverCityCode = receiverCityCode;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getStoresId() {
        return storesId;
    }

    public void setStoresId(int storesId) {
        this.storesId = storesId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<OrderGoods> getOrderGoods() {
        return orderGoods;
    }

    public void setOrderGoods(List<OrderGoods> orderGoods) {
        this.orderGoods = orderGoods;
    }

    public int getIntegralUse() {
        return integralUse;
    }

    public void setIntegralUse(int integralUse) {
        this.integralUse = integralUse;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public String getPromActivityIdArr() {
        return promActivityIdArr;
    }

    public void setPromActivityIdArr(String promActivityIdArr) {
        this.promActivityIdArr = promActivityIdArr;
    }

    public String getAddrCode() {
        return addrCode;
    }

    public void setAddrCode(String addrCode) {
        this.addrCode = addrCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getExchange() {
        return exchange;
    }

    public void setExchange(Boolean exchange) {
        this.exchange = exchange;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getErpStoreId() {
        return erpStoreId;
    }

    public void setErpStoreId(Integer erpStoreId) {
        this.erpStoreId = erpStoreId;
    }

    public Integer getErpAreaCode() {
        return erpAreaCode;
    }

    public void setErpAreaCode(Integer erpAreaCode) {
        this.erpAreaCode = erpAreaCode;
    }

    public GroupPurchase getGroupPurchase() {
        return groupPurchase;
    }

    public void setGroupPurchase(GroupPurchase groupPurchase) {
        this.groupPurchase = groupPurchase;
    }

    public String getGroupPurchaseJson() {
        return groupPurchaseJson;
    }

    public void setGroupPurchaseJson(String groupPurchaseJson) {
        this.groupPurchaseJson = groupPurchaseJson;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
