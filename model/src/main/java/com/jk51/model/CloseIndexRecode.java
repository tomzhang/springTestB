package com.jk51.model;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-21
 * 修改记录:
 */
public class CloseIndexRecode {

    private int id;
    private int site_id;
    private int storeadmin_id;
    private String customer_user_name;
    private double history_index;
    private double order_index;
    private Timestamp create_time;

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

    public int getStoreadmin_id() {
        return storeadmin_id;
    }

    public void setStoreadmin_id(int storeadmin_id) {
        this.storeadmin_id = storeadmin_id;
    }

    public String getCustomer_user_name() {
        return customer_user_name;
    }

    public void setCustomer_user_name(String customer_user_name) {
        this.customer_user_name = customer_user_name;
    }

    public double getHistory_index() {
        return history_index;
    }

    public void setHistory_index(double history_index) {
        this.history_index = history_index;
    }

    public double getOrder_index() {
        return order_index;
    }

    public void setOrder_index(double order_index) {
        this.order_index = order_index;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }
}
