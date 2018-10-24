package com.jk51.model.account.requestParams;

import java.sql.Timestamp;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     : 退款列表请求参数
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/14-03-14
 * 修改记录 :
 */
public class RefundParams  {
    private Integer siteId;
    private String merchantName;//商家名称
    private String tradeId;//订单号
    private String refundSerialNo;//退款流水号
    private Integer tradeStatus;//订单状态
    private String  reason;//退货原因
    private Timestamp refundTime;//退款时间

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getRefundSerialNo() {
        return refundSerialNo;
    }

    public void setRefundSerialNo(String refundSerialNo) {
        this.refundSerialNo = refundSerialNo;
    }

    public Integer getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(Integer tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Timestamp refundTime) {
        this.refundTime = refundTime;
    }
}
