package com.jk51.model.treat;

import java.sql.Timestamp;

/**
 * Created by wangcheng on 2017/3/4.
 */
public class DeliveryTemplate {
    private int id;
    private String code;
    private String name;
    private int siteId;
    private String regMailNo;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String checked;
    private int isActivation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsActivation() {
        return isActivation;
    }

    public void setIsActivation(int isActivation) {
        this.isActivation = isActivation;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getRegMailNo() {
        return regMailNo;
    }

    public void setRegMailNo(String regMailNo) {
        this.regMailNo = regMailNo;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }
}
