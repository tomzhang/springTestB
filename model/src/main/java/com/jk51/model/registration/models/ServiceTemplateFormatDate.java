package com.jk51.model.registration.models;

import java.util.Date;

/**
 * Created by admin on 2017/4/11.
 */
public class ServiceTemplateFormatDate{
    private Integer id;


    private Integer siteId;


    private Integer storeId;


    private String templateNo;


    private String startTime;


    private String endTime;


    private Integer accountSource;


    private Integer isDel;


    private Date createTime;


    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getSiteId() {
        return siteId;
    }


    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getStoreId() {
        return storeId;
    }


    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }


    public String getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(String templateNo) {
        this.templateNo = templateNo;
    }


    public String getStartTime() {
        return startTime;
    }


    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }


    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getAccountSource() {
        return accountSource;
    }

    public void setAccountSource(Integer accountSource) {
        this.accountSource = accountSource;
    }


    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
