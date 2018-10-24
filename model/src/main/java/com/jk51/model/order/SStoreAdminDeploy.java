package com.jk51.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class SStoreAdminDeploy {
	
	private Integer id;

    private Integer site_id;

    private Integer store_admin_id;

    private Integer pre_store_id;

    private Integer new_store_id;

    private Integer operator_id;

    private Date create_time;

    private String merchant_user;

    public String getMerchant_user() {
        return merchant_user;
    }

    public void setMerchant_user(String merchant_user) {
        this.merchant_user = merchant_user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getStore_admin_id() {
        return store_admin_id;
    }

    public void setStore_admin_id(Integer store_admin_id) {
        this.store_admin_id = store_admin_id;
    }

    public Integer getPre_store_id() {
        return pre_store_id;
    }

    public void setPre_store_id(Integer pre_store_id) {
        this.pre_store_id = pre_store_id;
    }

    public Integer getNew_store_id() {
        return new_store_id;
    }

    public void setNew_store_id(Integer new_store_id) {
        this.new_store_id = new_store_id;
    }

    public Integer getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(Integer operator_id) {
        this.operator_id = operator_id;
    }

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "StoreAdminDeploy{" +
                "id=" + id +
                ", site_id=" + site_id +
                ", store_admin_id=" + store_admin_id +
                ", pre_store_id=" + pre_store_id +
                ", new_store_id=" + new_store_id +
                ", operator_id=" + operator_id +
                ", create_time=" + create_time +
                '}';
    }
}
