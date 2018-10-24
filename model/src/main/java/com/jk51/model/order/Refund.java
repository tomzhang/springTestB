package com.jk51.model.order;

import java.sql.Timestamp;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:  退款记录
 * 作者: hulan
 * 创建日期: 2017-02-17
 * 修改记录:
 */
public class Refund extends  Page{
    private Integer siteId;
    private Integer id;
    private Integer operatorId;  //操作员ID
    private String operatorName;  //操作员名称
    private Integer merchantId;  //商家ID
    private String merchantName;  //商家名称
    private String payStyle;
    private Integer status;
    private Integer storeId;
    private String tradeId;
    private Integer tradeStatus;
    private Integer realPay;
    private Integer freight;  //运费 单位分
    private Integer applyRefundMoney;  //申请退款金额 单位分
    private String refundSerialNo;  //退款流水号
    private Integer realRefundMoney; //实际退款金额 单位分
    private Integer refundCash;  //现金部分退款
    private Integer refundHealthInsurance;  //医保部分退款
    private Integer isRefundGoods;  //是否需要退货
    private String refundExpressNo;  //退款快递号
    private String reason; //退货原因
    private String explain;  //退货说明
    private String voucher;
    private Timestamp deletedAt;
    private Timestamp refundTime;   //退款时间
    private Integer accountCheckingStatus;   //对账状态 0=待处理 1=已处理
    private Integer operatorType;   //操作者类型
    private Timestamp createTime;
    private Timestamp updateTime;
    private String storeAuthCode;
    private Integer integralUsed;  //使用的积分

    private Integer integralFinalAward;   //实送积分

    private Map<String,Object> map;  //优惠券
    private Integer isRefundIntegral;
    private Integer isRefundCoupon;
    private Integer buyerId;
    private String mobile;
    private Integer dealFinishStatus;

    public Integer getTradesStatuss() {
        return tradesStatuss;
    }

    public void setTradesStatuss(Integer tradesStatuss) {
        this.tradesStatuss = tradesStatuss;
    }

    private Integer tradesStatuss;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Integer getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(Integer tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Integer getRealPay() {
        return realPay;
    }

    public void setRealPay(Integer realPay) {
        this.realPay = realPay;
    }

    public Integer getFreight() {
        return freight;
    }

    public void setFreight(Integer freight) {
        this.freight = freight;
    }

    public Integer getApplyRefundMoney() {
        return applyRefundMoney;
    }

    public void setApplyRefundMoney(Integer applyRefundMoney) {
        this.applyRefundMoney = applyRefundMoney;
    }

    public String getRefundSerialNo() {
        return refundSerialNo;
    }

    public void setRefundSerialNo(String refundSerialNo) {
        this.refundSerialNo = refundSerialNo;
    }

    public Integer getRealRefundMoney() {
        return realRefundMoney;
    }

    public void setRealRefundMoney(Integer realRefundMoney) {
        this.realRefundMoney = realRefundMoney;
    }

    public Integer getRefundCash() {
        return refundCash;
    }

    public void setRefundCash(Integer refundCash) {
        this.refundCash = refundCash;
    }

    public Integer getRefundHealthInsurance() {
        return refundHealthInsurance;
    }

    public void setRefundHealthInsurance(Integer refundHealthInsurance) {
        this.refundHealthInsurance = refundHealthInsurance;
    }

    public Integer getIsRefundGoods() {
        return isRefundGoods;
    }

    public void setIsRefundGoods(Integer isRefundGoods) {
        this.isRefundGoods = isRefundGoods;
    }

    public String getRefundExpressNo() {
        return refundExpressNo;
    }

    public void setRefundExpressNo(String refundExpressNo) {
        this.refundExpressNo = refundExpressNo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Timestamp getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Timestamp refundTime) {
        this.refundTime = refundTime;
    }

    public Integer getAccountCheckingStatus() {
        return accountCheckingStatus;
    }

    public void setAccountCheckingStatus(Integer accountCheckingStatus) {
        this.accountCheckingStatus = accountCheckingStatus;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getStoreAuthCode() {
        return storeAuthCode;
    }

    public void setStoreAuthCode(String storeAuthCode) {
        this.storeAuthCode = storeAuthCode;
    }

    public Integer getIntegralUsed() {
        return integralUsed;
    }

    public void setIntegralUsed(Integer integralUsed) {
        this.integralUsed = integralUsed;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public Integer getIntegralFinalAward() {
        return integralFinalAward;
    }

    public void setIntegralFinalAward(Integer integralFinalAward) {
        this.integralFinalAward = integralFinalAward;
    }

    public Integer getIsRefundIntegral() {
        return isRefundIntegral;
    }

    public void setIsRefundIntegral(Integer isRefundIntegral) {
        this.isRefundIntegral = isRefundIntegral;
    }

    public Integer getIsRefundCoupon() {
        return isRefundCoupon;
    }

    public void setIsRefundCoupon(Integer isRefundCoupon) {
        this.isRefundCoupon = isRefundCoupon;
    }

    public Integer getDealFinishStatus() {
        return dealFinishStatus;
    }

    public void setDealFinishStatus(Integer dealFinishStatus) {
        this.dealFinishStatus = dealFinishStatus;
    }

    @Override
    public String toString() {
        return "Refund{" +
                "siteId=" + siteId +
                ", id=" + id +
                ", operatorId=" + operatorId +
                ", operatorName='" + operatorName + '\'' +
                ", merchantId=" + merchantId +
                ", merchantName='" + merchantName + '\'' +
                ", payStyle='" + payStyle + '\'' +
                ", status=" + status +
                ", storeId=" + storeId +
                ", tradeId='" + tradeId + '\'' +
                ", tradeStatus=" + tradeStatus +
                ", realPay=" + realPay +
                ", freight=" + freight +
                ", applyRefundMoney=" + applyRefundMoney +
                ", refundSerialNo='" + refundSerialNo + '\'' +
                ", realRefundMoney=" + realRefundMoney +
                ", refundCash=" + refundCash +
                ", refundHealthInsurance=" + refundHealthInsurance +
                ", isRefundGoods=" + isRefundGoods +
                ", refundExpressNo='" + refundExpressNo + '\'' +
                ", reason='" + reason + '\'' +
                ", explain='" + explain + '\'' +
                ", voucher='" + voucher + '\'' +
                ", deletedAt=" + deletedAt +
                ", refundTime=" + refundTime +
                ", accountCheckingStatus=" + accountCheckingStatus +
                ", operatorType=" + operatorType +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", storeAuthCode='" + storeAuthCode + '\'' +
                ", integralUsed=" + integralUsed +
                '}';
    }
}
