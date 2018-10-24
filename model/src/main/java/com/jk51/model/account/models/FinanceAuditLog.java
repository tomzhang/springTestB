package com.jk51.model.account.models;

import java.sql.Timestamp;

/**
 * filename :com.jk51.model.account.models.
 * author   :zw
 * date     :2017/2/24
 * Update   :
 * 账单表操作日志记录
 */
public class FinanceAuditLog {
    private Integer id;
    private Integer operation_id;
    private String operation_name;
    private String finance_no;
    private int finance_id;
    private Timestamp create_time;
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOperation_id() {
        return operation_id;
    }

    public void setOperation_id(Integer operation_id) {
        this.operation_id = operation_id;
    }

    public String getOperation_name() {
        return operation_name;
    }

    public void setOperation_name(String operation_name) {
        this.operation_name = operation_name;
    }

    public String getFinance_no() {
        return finance_no;
    }

    public void setFinance_no(String finance_no) {
        this.finance_no = finance_no;
    }

    public int getFinance_id() {
        return finance_id;
    }

    public void setFinance_id(int finance_id) {
        this.finance_id = finance_id;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

