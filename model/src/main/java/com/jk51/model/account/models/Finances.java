package com.jk51.model.account.models;

import java.sql.Timestamp;
import java.util.Date;

/**
 * filename :com.jk51.model.account.models.
 * author   :zw
 * date     :2017/2/22
 * Update   :
 * 出账表
 */
public class Finances {
    private Integer id;
    private String finance_no;
    private Integer seller_id;
    private String seller_name;
    private Date settlement_start_date;
    private Date settlement_end_date;
    private Date pay_day;
    private Integer total_pay;
    private Integer finance_checking_total;
    private Integer platform_total;
    private Integer post_total;
    private Integer commission_total;
    private Integer need_pay;
    private Integer real_pay;
    private Integer invoice;
    private Integer invoice_value;
    private Integer status;
    private String remark;
    private Integer audit_status;
    private String audit_remark;
    private Integer refund_total;
    private Integer refund_checking_total;
    private Integer is_charge_off;
    private Timestamp create_time;
    private Timestamp update_time;
    private Date pay_date;
    private Date invoice_time;
    private String invoice_number;
    private String express_number;
    private String express_type;
    private Date change_130_time;
    private String operator_name;
    private String merchant_remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFinance_no() {
        return finance_no;
    }

    public void setFinance_no(String finance_no) {
        this.finance_no = finance_no;
    }

    public Integer getSeller_id() {
        return seller_id;
    }

    public Integer getPost_total() {
        return post_total;
    }

    public void setPost_total(Integer post_total) {
        this.post_total = post_total;
    }

    public void setSeller_id(Integer seller_id) {
        this.seller_id = seller_id;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public Date getSettlement_start_date() {
        return settlement_start_date;
    }

    public void setSettlement_start_date(Date settlement_start_date) {
        this.settlement_start_date = settlement_start_date;
    }

    public Date getSettlement_end_date() {
        return settlement_end_date;
    }

    public void setSettlement_end_date(Date settlement_end_date) {
        this.settlement_end_date = settlement_end_date;
    }

    public Date getPay_day() {
        return pay_day;
    }

    public void setPay_day(Date pay_day) {
        this.pay_day = pay_day;
    }

    public Integer getTotal_pay() {
        return total_pay;
    }

    public void setTotal_pay(Integer total_pay) {
        this.total_pay = total_pay;
    }

    public Integer getFinance_checking_total() {
        return finance_checking_total;
    }

    public void setFinance_checking_total(Integer finance_checking_total) {
        this.finance_checking_total = finance_checking_total;
    }

    public Integer getPlatform_total() {
        return platform_total;
    }

    public void setPlatform_total(Integer platform_total) {
        this.platform_total = platform_total;
    }

    public Integer getCommission_total() {
        return commission_total;
    }

    public void setCommission_total(Integer commission_total) {
        this.commission_total = commission_total;
    }

    public Integer getNeed_pay() {
        return need_pay;
    }

    public void setNeed_pay(Integer need_pay) {
        this.need_pay = need_pay;
    }

    public Integer getReal_pay() {
        return real_pay;
    }

    public void setReal_pay(Integer real_pay) {
        this.real_pay = real_pay;
    }

    public Integer getInvoice() {
        return invoice;
    }

    public void setInvoice(Integer invoice) {
        this.invoice = invoice;
    }

    public Integer getInvoice_value() {
        return invoice_value;
    }

    public void setInvoice_value(Integer invoice_value) {
        this.invoice_value = invoice_value;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getAudit_status() {
        return audit_status;
    }

    public void setAudit_status(Integer audit_status) {
        this.audit_status = audit_status;
    }

    public String getAudit_remark() {
        return audit_remark;
    }

    public void setAudit_remark(String audit_remark) {
        this.audit_remark = audit_remark;
    }

    public Integer getRefund_total() {
        return refund_total;
    }

    public void setRefund_total(Integer refund_total) {
        this.refund_total = refund_total;
    }

    public Integer getRefund_checking_total() {
        return refund_checking_total;
    }

    public void setRefund_checking_total(Integer refund_checking_total) {
        this.refund_checking_total = refund_checking_total;
    }

    public Integer getIs_charge_off() {
        return is_charge_off;
    }

    public void setIs_charge_off(Integer is_charge_off) {
        this.is_charge_off = is_charge_off;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public Date getPay_date() {
        return pay_date;
    }

    public void setPay_date(Date pay_date) {
        this.pay_date = pay_date;
    }

    public Date getInvoice_time() {
        return invoice_time;
    }

    public void setInvoice_time(Date invoice_time) {
        this.invoice_time = invoice_time;
    }

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getExpress_number() {
        return express_number;
    }

    public void setExpress_number(String express_number) {
        this.express_number = express_number;
    }

    public String getExpress_type() {
        return express_type;
    }

    public void setExpress_type(String express_type) {
        this.express_type = express_type;
    }

    public Date getChange_130_time() {
        return change_130_time;
    }

    public void setChange_130_time(Date change_130_time) {
        this.change_130_time = change_130_time;
    }

    public String getOperator_name() {
        return operator_name;
    }

    public void setOperator_name(String operator_name) {
        this.operator_name = operator_name;
    }

    public String getMerchant_remark() {
        return merchant_remark;
    }

    public void setMerchant_remark(String merchant_remark) {
        this.merchant_remark = merchant_remark;
    }
}
