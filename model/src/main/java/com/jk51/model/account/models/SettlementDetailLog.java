package com.jk51.model.account.models;

import java.sql.Timestamp;

/**
 * filename :com.jk51.model.account.models.
 * author   :zw
 * date     :2017/2/24
 * Update   :
 * 对账明细操作记录表
 */
public class SettlementDetailLog {
    private Integer id;
    private Integer flow;
    private Integer operator_id;
    private String operator_name;
    private Timestamp create_time;
    private Long trades_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFlow() {
        return flow;
    }

    public void setFlow(Integer flow) {
        this.flow = flow;
    }

    public Integer getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(Integer operator_id) {
        this.operator_id = operator_id;
    }

    public String getOperator_name() {
        return operator_name;
    }

    public void setOperator_name(String operator_name) {
        this.operator_name = operator_name;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Long getTrades_id() {
        return trades_id;
    }

    public void setTrades_id(Long trades_id) {
        this.trades_id = trades_id;
    }
}
