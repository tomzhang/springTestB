package com.jk51.model.account.requestParams;

import com.jk51.model.order.Page;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * filename :com.jk51.model.account.requestParams.
 * author   :zw
 * date     :2017/3/11
 * Update   :
 */
public class AccountParams extends Page {
    private String  select_type; //搜索类别 by_order:根据订单 by_store:根据门店 by_user 根据店员
    private Integer site_id;
    private String sellerName;
    private Integer sellerId;
    private String payType;
    private Integer accountStatus;//到账状态
    private Integer checkStatus;//对账状态
    private Integer refund_checking_status;//退款对账状态
    private String tradesId;//订单id
    private String payNumber;//流水号
    private String dealStartTime; //交易结束时间范围
    private String dealEndTime;
    private String paymentStartTime;//付款时间范围
    private String paymentEndTime;
    private String settlementStatus; //结算
    private String refundStatus;//退款状态
    private String orderStartTime;//订单下单时间范围
    private String orderEndTime;
    private String tradesStore;//订单门店
    private String storeUserId; //订单来源店员
    private String storeAdminCode;//店员邀请码
    private String storeAdminName;//店员姓名
    private String merchantName;
    private String source_name;
    private String arayacak_name;
    private String send_name;
    private String register_name;
    private String financeId;
    private String totalAward;
    private String real_refund_money;
    private String financeNo;
    private String payTimeStart;
    private String payTimeEnd;
    private String refundAmountEnd;
    private  String payAmountStart;
    private  String exportType;
    private String serviceOrder;  // 是否是服务商订单

    public String getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(String serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getPayAmountEnd() {
        return payAmountEnd;
    }

    public void setPayAmountEnd(String payAmountEnd) {
        this.payAmountEnd = payAmountEnd;
    }

    private  String payAmountEnd;

    public String getPayAmountStart() {
        return payAmountStart;
    }

    public void setPayAmountStart(String payAmountStart) {
        this.payAmountStart = payAmountStart;
    }

    public String getPayTimeEnd() {
        return payTimeEnd;
    }

    public void setPayTimeEnd(String payTimeEnd) {
        this.payTimeEnd = payTimeEnd;
    }

    public String getRefundAmountEnd() {
        return refundAmountEnd;
    }

    public void setRefundAmountEnd(String refundAmountEnd) {
        this.refundAmountEnd = refundAmountEnd;
    }

    public String getRefundCheckingStatus() {
        return refundCheckingStatus;
    }

    public void setRefundCheckingStatus(String refundCheckingStatus) {
        this.refundCheckingStatus = refundCheckingStatus;
    }

    public String getRefundAmountStart() {
        return refundAmountStart;
    }

    public void setRefundAmountStart(String refundAmountStart) {
        this.refundAmountStart = refundAmountStart;
    }

    public String getPayCheckingStatus() {
        return payCheckingStatus;
    }

    public void setPayCheckingStatus(String payCheckingStatus) {
        this.payCheckingStatus = payCheckingStatus;
    }

    public String getAccountCheckingStatus() {
        return accountCheckingStatus;
    }

    public void setAccountCheckingStatus(String accountCheckingStatus) {
        this.accountCheckingStatus = accountCheckingStatus;
    }

    private String refundCheckingStatus;
    private String refundAmountStart;
    private String payCheckingStatus;
    private String accountCheckingStatus;

    public String getPayTimeStart() {
        return payTimeStart;
    }

    public void setPayTimeStart(String payTimeStart) {
        this.payTimeStart = payTimeStart;
    }

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle;
    }

    private String payStyle;



    public String getFinanceNo() {
        return financeNo;
    }

    public void setFinanceNo(String financeNo) {
        this.financeNo = financeNo;
    }

    public String getReal_refund_money() {
        return real_refund_money;
    }

    public void setReal_refund_money(String real_refund_money) {
        this.real_refund_money = real_refund_money;
    }

    public String getTotalAward() {
        return totalAward;
    }

    public void setTotalAward(String totalAward) {
        this.totalAward = totalAward;
    }

    public String getFinanceId() {
        return financeId;
    }

    public void setFinanceId(String financeId) {
        this.financeId = financeId;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public String getArayacak_name() {
        return arayacak_name;
    }

    public void setArayacak_name(String arayacak_name) {
        this.arayacak_name = arayacak_name;
    }

    public String getSend_name() {
        return send_name;
    }

    public void setSend_name(String send_name) {
        this.send_name = send_name;
    }

    public String getRegister_name() {
        return register_name;
    }

    public void setRegister_name(String register_name) {
        this.register_name = register_name;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getSelect_type() {
        return select_type;
    }

    public void setSelect_type(String select_type) {
        this.select_type = select_type;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getRefund_checking_status() {
        return refund_checking_status;
    }

    public void setRefund_checking_status(Integer refund_checking_status) {
        this.refund_checking_status = refund_checking_status;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Integer getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(Integer accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Integer getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Integer checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

    public String getPayNumber() {
        return payNumber;
    }

    public void setPayNumber(String payNumber) {
        this.payNumber = payNumber;
    }



    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }



    public String getTradesStore() {
        return tradesStore;
    }

    public void setTradesStore(String tradesStore) {
        this.tradesStore = tradesStore;
    }

    public String getStoreUserId() {
        return storeUserId;
    }

    public void setStoreUserId(String storeUserId) {
        this.storeUserId = storeUserId;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getDealStartTime() {
        return dealStartTime;
    }

    public void setDealStartTime(String dealStartTime) {
        this.dealStartTime = dealStartTime;
    }

    public String getDealEndTime() {
        return dealEndTime;

    }

    public void setDealEndTime(String dealEndTime) {
        this.dealEndTime = dealEndTime;
    }

    public String getPaymentStartTime() {
        return paymentStartTime;
    }

    public void setPaymentStartTime(String paymentStartTime) {
        this.paymentStartTime = paymentStartTime;
    }

    public String getPaymentEndTime() {
        return paymentEndTime;
    }

    public void setPaymentEndTime(String paymentEndTime) {
        this.paymentEndTime = paymentEndTime;
    }

    public String getOrderStartTime() {
        return orderStartTime;
    }

    public void setOrderStartTime(String orderStartTime) {
        this.orderStartTime = orderStartTime;
    }

    public String getOrderEndTime() {
        return orderEndTime;
    }

    public void setOrderEndTime(String orderEndTime) {
        this.orderEndTime = orderEndTime;
    }

    public String getStoreAdminCode() {
        return storeAdminCode;
    }

    public void setStoreAdminCode(String storeAdminCode) {
        this.storeAdminCode = storeAdminCode;
    }

    public String getStoreAdminName() {
        return storeAdminName;
    }

    public void setStoreAdminName(String storeAdminName) {
        this.storeAdminName = storeAdminName;
    }
}
