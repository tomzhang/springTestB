package com.jk51.modules.member.request;


import com.jk51.model.order.Page;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/26.
 */
public class MemberDto extends Page {

    private String siteId;
    private String mobilePhone;

    private String startTime;

    private String endTime;

    private Integer storeId;

    private Integer status;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }
}
