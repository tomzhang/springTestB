package com.jk51.model.account.models;


import java.util.Date;
import java.sql.Timestamp;

/**
 * filename :com.jk51.model.account.models.
 * author   :zw
 * date     :2017/2/17
 * Update   :
 * 分类汇总model
 */
public class ClassifiedStatistic {
    private Integer id;
    private String finance_no;
    private Integer seller_id;
    private String seller_name;
    private String pay_style;
    private Date settlement_start_date;
    private Date settlement_end_date;
    private Integer total_pay;
    private Integer finance_checking_total;
    private Integer platform_total;
    private Integer commission_total;
    private Integer refund_total;
    private Integer refund_checking_total;
    private Integer post_total;
    private Timestamp create_time;
    private Timestamp update_time;

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

    public void setSeller_id(Integer seller_id) {
        this.seller_id = seller_id;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getPay_style() {
        return pay_style;
    }

    public void setPay_style(String pay_style) {
        this.pay_style = pay_style;
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

    public Integer getPost_total() {
        return post_total;
    }

    public void setPost_total(Integer post_total) {
        this.post_total = post_total;
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
}
