package com.jk51.model.order;

import com.jk51.model.concession.result.GiftResult;
import com.jk51.model.concession.result.Result;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:门店直购订单模型
 * 作者: baixiongfei
 * 创建日期: 2017/2/24
 * 修改记录:
 */
public class StoreDirect {

    private int siteId;//商家ID

    private String sotreId;//门店编号

    private String mobile;//用户手机号码

    private List<OrderGoods> orderGoods;//商品信息

    private String storeUserId;//帮助用户下单的门店店员ID

    private int integral;//积分

    private String receiverAddress;//收货地址

    private String receiverName;//收货人姓名

    private String receiverPhone;//收货人电话(座机)

    private String receiverMobile;//收货人手机

    private String receiverZip;//收货地址的邮编

    private String invoice;//是否开发票1：开，2：不开

    private String invoiceTitle;//发票抬头

    private Integer tradesSource;//订单来源: 110 (网站)，120（微信），130（app）, 140（店员帮用户下单），9999（其它）

    private Integer distributorId;

    /* -- 优惠券/活动 相关参数 -- */

    private String userCouponId; // 使用的优惠券ID

    private List<GiftResult> giftGoods; // 赠品信息

    private String promActivityIdArr; // 优惠活动ids

    private Result concessionResult; // 预下单计算结果，用于下单是做校验


    /* -- setter & getter -- */

    public List<GiftResult> getGiftGoods() {
        return giftGoods;
    }

    public void setGiftGoods(List<GiftResult> giftGoods) {
        this.giftGoods = giftGoods;
    }

    public String getPromActivityIdArr() {
        return promActivityIdArr;
    }

    public void setPromActivityIdArr(String promActivityIdArr) {
        this.promActivityIdArr = promActivityIdArr;
    }

    public Result getConcessionResult() {
        return concessionResult;
    }

    public void setConcessionResult(Result concessionResult) {
        this.concessionResult = concessionResult;
    }

    public Integer getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public Integer getTradesSource() {
        return tradesSource;
    }

    public void setTradesSource(Integer tradesSource) {
        this.tradesSource = tradesSource;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getSotreId() {
        return sotreId;
    }

    public void setSotreId(String sotreId) {
        this.sotreId = sotreId;
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

    public String getStoreUserId() {
        return storeUserId;
    }

    public void setStoreUserId(String storeUserId) {
        this.storeUserId = storeUserId;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverZip() {
        return receiverZip;
    }

    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip;
    }

    public String getInvoice() {
        return invoice == null ? "1" : invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(String userCouponId) {
        this.userCouponId = userCouponId;
    }

    @Override
    public String toString() {
        return "StoreDirect{" +
            "siteId=" + siteId +
            ", sotreId='" + sotreId + '\'' +
            ", mobile='" + mobile + '\'' +
            ", orderGoods=" + orderGoods +
            ", storeUserId='" + storeUserId + '\'' +
            ", integral=" + integral +
            ", receiverAddress='" + receiverAddress + '\'' +
            ", receiverName='" + receiverName + '\'' +
            ", receiverPhone='" + receiverPhone + '\'' +
            ", receiverMobile='" + receiverMobile + '\'' +
            ", receiverZip='" + receiverZip + '\'' +
            ", invoice='" + invoice + '\'' +
            ", invoiceTitle='" + invoiceTitle + '\'' +
            ", userCouponId='" + userCouponId + '\'' +
            ", tradesSource='" + tradesSource + '\'' +
            '}';
    }
}
