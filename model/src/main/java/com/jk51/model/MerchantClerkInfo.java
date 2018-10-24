package com.jk51.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-03-02
 * 修改记录:
 */
public class MerchantClerkInfo {
    private Integer id;
    private String mobile;
    private String name;
    private String employeeNumber;
    private String ivocode;
    private Date createTime;
    private String clerkJob;
    private String storeName;
    private String memo;
    private Integer status;
    private Integer chat;
    private String storeId;
    private Integer storeadminid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChat() {
        return chat;
    }

    public void setChat(Integer chat) {
        this.chat = chat;
    }

    public String getIvocode() {
        try {
            return ivocode.split("_")[1];
        } catch (Exception e) {
            return ivocode;
        }
    }

    public void setIvocode(String ivocode) {
        this.ivocode = ivocode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getClerkJob() {
        return clerkJob;
    }

    public void setClerkJob(String clerkJob) {
        this.clerkJob = clerkJob;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getIsDel() {
        return status;
    }

    public void setIsDel(Integer isDel) {
        this.status = isDel;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "MerchantClerkInfo{" +
                "id=" + id +
                ", mobile='" + mobile + '\'' +
                ", name='" + name + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", ivocode='" + ivocode + '\'' +
                ", createTime=" + createTime +
                ", clerkJob='" + clerkJob + '\'' +
                ", storeName='" + storeName + '\'' +
                ", memo='" + memo + '\'' +
                ", status=" + status +
                ", chat=" + chat +
                ", storeId='" + storeId + '\'' +
                '}';
    }

    public Integer getStoreadminid() {
        return storeadminid;
    }

    public void setStoreadminid(Integer storeadminid) {
        this.storeadminid = storeadminid;
    }
}
