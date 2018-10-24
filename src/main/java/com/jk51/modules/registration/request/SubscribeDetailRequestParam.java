package com.jk51.modules.registration.request;

import com.jk51.model.order.Page;

/**
 * Created by mqq on 2017/4/13.
 */
public class SubscribeDetailRequestParam extends Page {

    private String doctorName;//医生名称

    private Integer siteId;//商户主键

    private Integer storeId;//门店主键

    private String userCateid;//分类

    private Integer status;//状态


    private String startTime;//开始时间

    private String endTime;//结束时间

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getUserCateid() {
        return userCateid;
    }

    public void setUserCateid(String userCateid) {
        this.userCateid = userCateid;
    }


}
