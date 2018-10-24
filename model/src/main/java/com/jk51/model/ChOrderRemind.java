package com.jk51.model;

import java.sql.Timestamp;

public class ChOrderRemind {
    private Integer id;

    private String orderId;

    private Integer postStyle;

    private Integer pharmacistId;

    private Integer isPushed;

    private Integer isReaded;

    private String extra;

    private Timestamp deletedAt;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public Integer getPostStyle() {
        return postStyle;
    }

    public void setPostStyle(Integer postStyle) {
        this.postStyle = postStyle;
    }

    public Integer getPharmacistId() {
        return pharmacistId;
    }

    public void setPharmacistId(Integer pharmacistId) {
        this.pharmacistId = pharmacistId;
    }

    public Integer getIsPushed() {
        return isPushed;
    }

    public void setIsPushed(Integer isPushed) {
        this.isPushed = isPushed;
    }

    public Integer getIsReaded() {
        return isReaded;
    }

    public void setIsReaded(Integer isReaded) {
        this.isReaded = isReaded;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra == null ? null : extra.trim();
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}