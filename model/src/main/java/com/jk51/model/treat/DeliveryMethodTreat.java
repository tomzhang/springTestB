package com.jk51.model.treat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangcheng
 * 创建日期: 2017-3-4.
 */
public class DeliveryMethodTreat {
    @JsonProperty("siteId")
    private int siteId;
    @JsonProperty("devlId")
    private int devlId;
    @JsonProperty("devlIds")
    private List<Integer> devlIds;
    @JsonProperty("postStyleId")
    private int postStyleId;
    @JsonProperty("deliveryName")
    private String deliveryName;
    @JsonProperty("valuation")
    private int valuation;
    @JsonProperty("firstWeight")
    private int firstWeight;
    @JsonProperty("addWeight")
    private int addWeight;
    @JsonProperty("defFirstprice")
    private int defFirstprice;
    @JsonProperty("defAddprice")
    private int defAddprice;
    @JsonProperty("appointFirstweight")
    private String appointFirstweight;
    @JsonProperty("appointAddweight")
    private String appointAddweight;
    @JsonProperty("appointFirstprice")
    private String appointFirstprice;
    @JsonProperty("appointAddprice")
    private String appointAddprice;
    @JsonProperty("appointArea")
    private String appointArea;
    @JsonProperty("devlDesc")
    private String devlDesc;
    @JsonProperty("devDesc")
    private String devDesc;
    @JsonProperty("isActivation")
    private int isActivation;
    @JsonProperty("defaultFlag")
    private int defaultFlag;
    @JsonProperty("updateTime")
    private Timestamp updateTime;

    public List<Integer> getDevlIds() {
        return devlIds;
    }

    public void setDevlIds(List<Integer> devlIds) {
        this.devlIds = devlIds;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getDevlId() {
        return devlId;
    }

    public void setDevlId(int devlId) {
        this.devlId = devlId;
    }

    public int getPostStyleId() {
        return postStyleId;
    }

    public void setPostStyleId(int postStyleId) {
        this.postStyleId = postStyleId;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public int getValuation() {
        return valuation;
    }

    public void setValuation(int valuation) {
        this.valuation = valuation;
    }

    public int getFirstWeight() {
        return firstWeight;
    }

    public void setFirstWeight(int firstWeight) {
        this.firstWeight = firstWeight;
    }

    public int getAddWeight() {
        return addWeight;
    }

    public void setAddWeight(int addWeight) {
        this.addWeight = addWeight;
    }

    public int getDefFirstprice() {
        return defFirstprice;
    }

    public void setDefFirstprice(int defFirstprice) {
        this.defFirstprice = defFirstprice;
    }

    public int getDefAddprice() {
        return defAddprice;
    }

    public void setDefAddprice(int defAddprice) {
        this.defAddprice = defAddprice;
    }

    public String getAppointFirstweight() {
        return appointFirstweight;
    }

    public void setAppointFirstweight(String appointFirstweight) {
        this.appointFirstweight = appointFirstweight;
    }

    public String getAppointAddweight() {
        return appointAddweight;
    }

    public void setAppointAddweight(String appointAddweight) {
        this.appointAddweight = appointAddweight;
    }

    public String getAppointFirstprice() {
        return appointFirstprice;
    }

    public void setAppointFirstprice(String appointFirstprice) {
        this.appointFirstprice = appointFirstprice;
    }

    public String getAppointAddprice() {
        return appointAddprice;
    }

    public void setAppointAddprice(String appointAddprice) {
        this.appointAddprice = appointAddprice;
    }

    public String getAppointArea() {
        return appointArea;
    }

    public void setAppointArea(String appointArea) {
        this.appointArea = appointArea;
    }

    public String getDevlDesc() {
        return devlDesc;
    }

    public void setDevlDesc(String devlDesc) {
        this.devlDesc = devlDesc;
    }

    public String getDevDesc() {
        return devDesc;
    }

    public void setDevDesc(String devDesc) {
        this.devDesc = devDesc;
    }

    public int getIsActivation() {
        return isActivation;
    }

    public void setIsActivation(int isActivation) {
        this.isActivation = isActivation;
    }

    public int getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(int defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
