package com.jk51.model.registration.models;

/**
 * Created by mqq on 2017/4/10.
 */
public class MyMingYiYuYueDetail {
    private String doctorName;       //医生名称
    private String contactPersonName;//就诊人名称
    private String getContactPersonPhone;//就诊人手机号
    private String price;//价格
    private String time1;//预约时间  年月日
    private String time2;//预约时间  排班开始时间段
    private String time3;//预约时间  排班结束时间段
    private String storesName;//门店名称
    private String storesaddress;//门店地址
    private String sickInfo;//病情描述
    private String diagnoseInfo;//初，复诊情况
    private Integer servceOrderId;//预约表主键
    private String servstatusInfo;//预约状态

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getDiagnoseInfo() {
        return diagnoseInfo;
    }

    public void setDiagnoseInfo(String diagnoseInfo) {
        this.diagnoseInfo = diagnoseInfo;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getGetContactPersonPhone() {
        return getContactPersonPhone;
    }

    public void setGetContactPersonPhone(String getContactPersonPhone) {
        this.getContactPersonPhone = getContactPersonPhone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSickInfo() {
        return sickInfo;
    }

    public void setSickInfo(String sickInfo) {
        this.sickInfo = sickInfo;
    }

    public String getStoresaddress() {
        return storesaddress;
    }

    public void setStoresaddress(String storesaddress) {
        this.storesaddress = storesaddress;
    }

    public String getStoresName() {
        return storesName;
    }

    public void setStoresName(String storesName) {
        this.storesName = storesName;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public String getTime3() {
        return time3;
    }

    public void setTime3(String time3) {
        this.time3 = time3;
    }

    public Integer getServceOrderId() {
        return servceOrderId;
    }

    public void setServceOrderId(Integer servceOrderId) {
        this.servceOrderId = servceOrderId;
    }

    public String getServstatusInfo() {
        return servstatusInfo;
    }

    public void setServstatusInfo(String servstatusInfo) {
        this.servstatusInfo = servstatusInfo;
    }
}
