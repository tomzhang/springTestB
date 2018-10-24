package com.jk51.model.order.response;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: baixiongfei
 * 创建日期: 2017/3/17
 * 修改记录:
 */
public class RefundQueryReq {

    private Integer siteId;//商户ID

    private Integer storeId;//门店ID
    //订单号
    private String tradeId;

    private String buyerId;//用户唯一编号

    //退款类型 100:用户发起退款，200：商户发起退款
    private Integer refundType;

    //退款状态 100=申请退款  110=拒绝退款 120=退款成功
    private Integer refundStatus;

    //退款单生成开始时间,格式：yyyy-MM-dd
    private String createTimeStart;

    //退款单生成结束时间,格式：yyyy-MM-dd
    private String createTimeEnd;

    private Integer pageNum = 1;//当前第几页，默认为第一页

    private Integer pageSize = 10;//默认每页多少条数据，默认每页10条

    private String refundSerialNo;  //退款流水号

    private String sellerName;  //商家名称

    private String mobile;  //会员手机号

    private String reason;//退款原因

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

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

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Integer getRefundType() {
        return refundType;
    }

    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public Integer getPageNum() {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        if (pageSize == null || pageNum <= 0) {
            pageSize = 10;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getRefundSerialNo() {
        return refundSerialNo;
    }

    public void setRefundSerialNo(String refundSerialNo) {
        this.refundSerialNo = refundSerialNo;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "RefundQueryReq{" +
            "siteId=" + siteId +
            ", storeId=" + storeId +
            ", tradeId='" + tradeId + '\'' +
            ", buyerId='" + buyerId + '\'' +
            ", refundType=" + refundType +
            ", refundStatus=" + refundStatus +
            ", createTimeStart='" + createTimeStart + '\'' +
            ", createTimeEnd='" + createTimeEnd + '\'' +
            ", pageNum=" + pageNum +
            ", pageSize=" + pageSize +
            ", refundSerialNo='" + refundSerialNo + '\'' +
            ", sellerName='" + sellerName + '\'' +
            ", mobile='" + mobile + '\'' +
            '}';
    }
}
