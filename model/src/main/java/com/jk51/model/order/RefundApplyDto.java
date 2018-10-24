package com.jk51.model.order;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: fengzhiming
 * 创建日期: 2017/3/29
 * 修改记录:
 */
public class RefundApplyDto {
    private Integer siteId;
    private Integer storeId;
    private Long tradeId;
    private Integer applyRefundMoney;//申请金额
    private Integer realRefundMoney; //实际退款金额
    private Integer refundCash;//现金退款部分
    private Integer refundHealthInsurance;//医保退款部分
    private Integer isRefundGoods;//是否需要退货 0 ：不需要 1： 需要
    private String refundExpressNo;//退款快递号
    private String reason;//退货原因
    private String explain;//退货说明
    private String voucher;//退货图片凭证
    private Integer operatorType;//操作者类型 100：用户 200：商户
    private int is_integral;//是否退积分 0:退 1：不退
    private int is_coupon;//是否退优惠券 0：退 1：不退
    private String storeAuthCode;//店长授权码
    private Integer is_refund;//是否同意退款

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
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

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public int getIs_integral() {
        return is_integral;
    }

    public void setIs_integral(int is_integral) {
        this.is_integral = is_integral;
    }

    public int getIs_coupon() {
        return is_coupon;
    }

    public void setIs_coupon(int is_coupon) {
        this.is_coupon = is_coupon;
    }

    public String getStoreAuthCode() {
        return storeAuthCode;
    }

    public void setStoreAuthCode(String storeAuthCode) {
        this.storeAuthCode = storeAuthCode;
    }

    public Integer getIs_refund() {
        return is_refund;
    }

    public void setIs_refund(Integer is_refund) {
        this.is_refund = is_refund;
    }

    public Integer getApplyRefundMoney() {
        return applyRefundMoney;
    }

    public void setApplyRefundMoney(Integer applyRefundMoney) {
        this.applyRefundMoney = applyRefundMoney;
    }

    @Override
    public String toString() {
        return "RefundApplyDto{" +
            "siteId=" + siteId +
            ", storeId='" + storeId +
            ", tradeId=" + tradeId +
            ", realRefundMoney=" + realRefundMoney +
            ", refundCash=" + refundCash +
            ", refundHealthInsurance=" + refundHealthInsurance +
            ", isRefundGoods=" + isRefundGoods +
            ", refundExpressNo='" + refundExpressNo + '\'' +
            ", reason='" + reason + '\'' +
            ", explain='" + explain + '\'' +
            ", voucher='" + voucher + '\'' +
            ", operatorType=" + operatorType +
            ", is_integral=" + is_integral +
            ", is_coupon=" + is_coupon +
            ", storeAuthCode='" + storeAuthCode + '\'' +
            ", is_refund=" + is_refund +
            ", applyRefundMoney=" + applyRefundMoney +
            '}';
    }


}
