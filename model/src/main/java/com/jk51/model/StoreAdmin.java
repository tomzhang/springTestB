package com.jk51.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by gaojie on 2017/2/15.
 * 店员表
 *
 */
public class StoreAdmin implements Serializable {


    //商家ID
    private int site_id;
    //自增id
    private int id;
    //门店ID
    private int store_id;

    //店员手机号码
    private String user_name;
    private String user_pwd;
    private int user_type;
    private int is_del;
    private int status;
    private Timestamp last_login_time;
    private int login_count;
    private Timestamp create_time;
    private Timestamp update_time;
    private String mobile;
    private String receiver;

    //除亲密度指标外其他指标值总分
    private double countIndex;

    //亲密度-历史数据指标分
    private double history_index;

    //亲密度-店员下单量指标分
    private double order_index;

    //所有的指标分
    private double allIndex;

    //关系密切性 一级权重值
    private double cloose_weight_value;

    //店员下单量二级权重值
    private double order_Num_weight_value;

    //与用户的历史数据二级权重值
    private double intersection_weight_value;

    public double getCloose_weight_value() {
        return cloose_weight_value;
    }

    public void setCloose_weight_value(double cloose_weight_value) {
        this.cloose_weight_value = cloose_weight_value;
    }

    public double getOrder_Num_weight_value() {
        return order_Num_weight_value;
    }

    public void setOrder_Num_weight_value(double order_Num_weight_value) {
        this.order_Num_weight_value = order_Num_weight_value;
    }

    public double getIntersection_weight_value() {
        return intersection_weight_value;
    }

    public void setIntersection_weight_value(double intersection_weight_value) {
        this.intersection_weight_value = intersection_weight_value;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_pwd() {
        return user_pwd;
    }

    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public int getIs_del() {
        return is_del;
    }

    public void setIs_del(int is_del) {
        this.is_del = is_del;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(Timestamp last_login_time) {
        this.last_login_time = last_login_time;
    }

    public int getLogin_count() {
        return login_count;
    }

    public void setLogin_count(int login_count) {
        this.login_count = login_count;
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

    public double getCountIndex() {
        return countIndex;
    }

    public void setCountIndex(double countIndex) {
        this.countIndex = countIndex;
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

    public double getAllIndex() {
        return allIndex;
    }

    public void setAllIndex(double allIndex) {
        this.allIndex = allIndex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
