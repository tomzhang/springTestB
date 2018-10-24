package com.jk51.model.account.requestParams;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jk51.model.order.Page;

import java.util.Date;

/**
 * filename :com.jk51.model.account.requestParams.
 * author   :zw
 * date     :2017/2/17
 * Update   :
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassifiedAccountParam extends Page{

    private String financeNo;
    private Integer totalPay;//支付金额
    private String sellerName;//商家名称
    private Integer sellerId;//商家编号
    private String payStyle;//支付方式
    private Integer invoice;//发票状态
    private Integer status;//结算状态
    private Integer auditStatus;//审核状态

    private Date sActualDate;//实际支付日开始时间
    private Date eActualDate;//实际支付日开结束时间
    private Date settlementDate;//结算操作日
    private Date sPayment;//财务支付日开始时间
    private Date ePayment;//财务支付日结束时间
    private Date sOutDate;//财务支付日结束时间
    private Date eOutDate;//财务支付日结束时间

    private String findType;//查询方：merchant（商户后台），51jk（51后台），

    public String getFindType() {
        return findType;
    }

    public void setFindType(String findType) {
        this.findType = findType;
    }

    public Date getsOutDate() {
        return sOutDate;
    }

    public void setsOutDate(Date sOutDate) {
        this.sOutDate = sOutDate;
    }

    public Date geteOutDate() {
        return eOutDate;
    }

    public void seteOutDate(Date eOutDate) {
        this.eOutDate = eOutDate;
    }

    private Double sPay;//开始支付金额
    private Double ePay;//结束支付金额



    public String getFinanceNo() {
        return financeNo;
    }

    public void setFinanceNo(String financeNo) {
        this.financeNo = financeNo;
    }

    public Integer getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(Integer totalPay) {
        this.totalPay = totalPay;
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

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle;
    }

    public Integer getInvoice() {
        return invoice;
    }

    public void setInvoice(Integer invoice) {
        this.invoice = invoice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Date getsActualDate() {
        return sActualDate;
    }

    public void setsActualDate(Date sActualDate) {
        this.sActualDate = sActualDate;
    }

    public Date geteActualDate() {
        return eActualDate;
    }

    public void seteActualDate(Date eActualDate) {
        this.eActualDate = eActualDate;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(Date settlementDate) {
        this.settlementDate = settlementDate;
    }

    public Date getsPayment() {
        return sPayment;
    }

    public void setsPayment(Date sPayment) {
        this.sPayment = sPayment;
    }

    public Date getePayment() {
        return ePayment;
    }

    public void setePayment(Date ePayment) {
        this.ePayment = ePayment;
    }

    public Double getsPay() {
        return sPay;
    }

    public void setsPay(Double sPay) {
        this.sPay = sPay;
    }

    public Double getePay() {
        return ePay;
    }

    public void setePay(Double ePay) {
        this.ePay = ePay;
    }
}
