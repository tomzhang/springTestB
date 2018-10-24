package com.jk51.model.account.models;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/13-03-13
 * 修改记录 :
 */
public class FinancesStatistic {
    private Integer id;//结算统计表（出账表)
    private Integer sellerId;//商家id
    private String sellerName;//商家名称
    private String settlementStartDate;//结算开始时间
    private String settlementEndDate;//结算结束时间
    private String payDay;//结算日（出账日）

    private String actualDate;//预计财务付款日
    private String payStyle;//支付方式
    private Integer totalPay;//支付金额
    private Integer totalPayA;//汇总金额
    private Integer commissionTotal;//交易佣金
    private Integer financeCheckingTotal;//对账
    private Integer refundTotal;//买家退款金额
    private Integer refundCheckingTotal;//对账
    private String auditStatus;//审核状态
    private Integer platformTotal;//代收手续费
    private Integer platform_total;
    private String status;//结算状态
    private String financeNo;//账单编号

    private String payDate;
    private String invoiceTime;
    private String invoiceNumber;
    private String expressNumber;
    private String expressType;
    private String operatorName;
    private String remark;
    private String changeTime;
    private Integer postTotal;
    private Integer totalPays;
    private String  statuss;
    private String payDays;

    public Integer getTotalPayA() {
        return totalPayA;
    }

    public void setTotalPayA(Integer totalPayA) {
        this.totalPayA = totalPayA;
    }

    public Integer getPlatform_total() {
        return platform_total;
    }

    public void setPlatform_total(Integer platform_total) {
        this.platform_total = platform_total;
    }

    public String getPayDays() {
        return payDays;
    }

    public void setPayDays(String payDays) {
        this.payDays = payDays;
    }

    public String getStatuss() {
        return statuss;
    }

    public void setStatuss(String statuss) {
        this.statuss = statuss;
    }

    public Integer getTotalPays() {
        return totalPays;
    }

    public void setTotalPays(Integer totalPays) {
        this.totalPays = totalPays;
    }

    public Integer getPostTotal() {
        return postTotal;
    }

    public void setPostTotal(Integer postTotal) {
        this.postTotal = postTotal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSettlementStartDate() {
        return settlementStartDate;
    }

    public void setSettlementStartDate(String settlementStartDate) {
        this.settlementStartDate = settlementStartDate;
    }

    public String getSettlementEndDate() {
        return settlementEndDate;
    }

    public void setSettlementEndDate(String settlementEndDate) {
        this.settlementEndDate = settlementEndDate;
    }

    public String getPayDay() {
        return payDay;
    }

    public void setPayDay(String payDay) {
        this.payDay = payDay;
    }

    public String getActualDate() {
        return actualDate;
    }

    public void setActualDate(String actualDate) {
        this.actualDate = actualDate;
    }

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle;
    }

    public Integer getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(Integer totalPay) {
        this.totalPay = totalPay;
    }

    public Integer getCommissionTotal() {
        return commissionTotal;
    }

    public void setCommissionTotal(Integer commissionTotal) {
        this.commissionTotal = commissionTotal;
    }

    public Integer getFinanceCheckingTotal() {
        return financeCheckingTotal;
    }

    public void setFinanceCheckingTotal(Integer financeCheckingTotal) {
        this.financeCheckingTotal = financeCheckingTotal;
    }

    public Integer getRefundTotal() {
        return refundTotal;
    }

    public void setRefundTotal(Integer refundTotal) {
        this.refundTotal = refundTotal;
    }

    public Integer getRefundCheckingTotal() {
        return refundCheckingTotal;
    }

    public void setRefundCheckingTotal(Integer refundCheckingTotal) {
        this.refundCheckingTotal = refundCheckingTotal;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Integer getPlatformTotal() {
        return platformTotal;
    }

    public void setPlatformTotal(Integer platformTotal) {
        this.platformTotal = platformTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFinanceNo() {
        return financeNo;
    }

    public void setFinanceNo(String financeNo) {
        this.financeNo = financeNo;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getInvoiceTime() {
        return invoiceTime;
    }

    public void setInvoiceTime(String invoiceTime) {
        this.invoiceTime = invoiceTime;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getExpressNumber() {
        return expressNumber;
    }

    public void setExpressNumber(String expressNumber) {
        this.expressNumber = expressNumber;
    }

    public String getExpressType() {
        return expressType;
    }

    public void setExpressType(String expressType) {
        this.expressType = expressType;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }
}
