package com.jk51.model.treat;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 结算日实体
 * 作者: gaojie
 * 创建日期: 2017-03-17
 * 修改记录:
 */
public class YbSettlementdayConfig {


    private int id;
    private int site_id;
    private int set_type;   //DEFAULT '2' COMMENT '设置类型（0按日结 1按周结 2按月结)',
    private int pay_day_value; //DEFAULT '3' COMMENT '付款日期在结算日期之后的天数',
    private String set_value;  //DEFAULT '' COMMENT '按周结的值(星期几);按月结的值(几号)按日接此处为0',
    private Timestamp thelast_time; //DEFAULT '0000-00-00 00:00:00' COMMENT '上次结算日期',
    private Timestamp create_time;
    private Timestamp update_time;
    private int finance_type;  //设置结算方式 0以结束状态结算  1以付款状态结算

    private Timestamp transfer_time;//迁移时间

    public int getFinance_type() {
        return finance_type;
    }

    public void setFinance_type(int finance_type) {
        this.finance_type = finance_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public int getSet_type() {
        return set_type;
    }

    public void setSet_type(int set_type) {
        this.set_type = set_type;
    }

    public int getPay_day_value() {
        return pay_day_value;
    }

    public void setPay_day_value(int pay_day_value) {
        this.pay_day_value = pay_day_value;
    }

    public String getSet_value() {
        return set_value;
    }

    public void setSet_value(String set_value) {
        this.set_value = set_value;
    }

    public Timestamp getThelast_time() {
        return thelast_time;
    }

    public void setThelast_time(Timestamp thelast_time) {
        this.thelast_time = thelast_time;
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

    public Timestamp getTransfer_time() {
        return transfer_time;
    }

    public void setTransfer_time(Timestamp transfer_time) {
        this.transfer_time = transfer_time;
    }
}
