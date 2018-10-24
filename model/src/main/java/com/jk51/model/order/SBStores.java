package com.jk51.model.order;

import java.util.Date;

public class SBStores {

    private String storesNumber;

    private String name;

    private Boolean isQjd;

    private String type;

    private Integer cityId;

    private Integer regionId;

    private String address;

    private String baiduLat;

    private String baiduLng;

    private String gaodeLng;

    private String gaodeLat;

    private Integer mapFlag;

    private String tel;

    private String businessTime;

    private String feature;

    private String summary;

    private String qrCodeImg;

    private Boolean qrCodeType;

    private Boolean storesStatus;

    private Boolean isDel;

    private String province;

    private String city;

    private String serviceSupport;

    private String selfTokenTime;

    private String deliveryTime;

    private String remindMobile;

    private String country;

    private Byte ownPricingType;

    private Byte ownPromotionType;

    private Date createTime;

    private Date updateTime;

    private String storeImgs;

    private Integer id;

    private Integer siteId;

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    private String admin;

    private String pwd;

    private Integer storeAdminId;

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public Boolean getQjd() {
        return isQjd;
    }

    public void setQjd(Boolean qjd) {
        isQjd = qjd;
    }

    public Boolean getDel() {
        return isDel;
    }

    public void setDel(Boolean del) {
        isDel = del;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

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

    public String getStoresNumber() {
        return storesNumber;
    }

    public void setStoresNumber(String storesNumber) {
        this.storesNumber = storesNumber == null ? null : storesNumber.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Boolean getIsQjd() {
        return isQjd;
    }

    public void setIsQjd(Boolean isQjd) {
        this.isQjd = isQjd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getBaiduLat() {
        return baiduLat;
    }

    public void setBaiduLat(String baiduLat) {
        this.baiduLat = baiduLat == null ? null : baiduLat.trim();
    }

    public String getBaiduLng() {
        return baiduLng;
    }

    public void setBaiduLng(String baiduLng) {
        this.baiduLng = baiduLng == null ? null : baiduLng.trim();
    }

    public String getGaodeLng() {
        return gaodeLng;
    }

    public void setGaodeLng(String gaodeLng) {
        this.gaodeLng = gaodeLng == null ? null : gaodeLng.trim();
    }

    public String getGaodeLat() {
        return gaodeLat;
    }

    public void setGaodeLat(String gaodeLat) {
        this.gaodeLat = gaodeLat == null ? null : gaodeLat.trim();
    }

    public Integer getMapFlag() {
        return mapFlag;
    }

    public void setMapFlag(Integer mapFlag) {
        this.mapFlag = mapFlag;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
    }

    public String getBusinessTime() {
        return businessTime;
    }

    public void setBusinessTime(String businessTime) {
        this.businessTime = businessTime == null ? null : businessTime.trim();
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    public String getQrCodeImg() {
        return qrCodeImg;
    }

    public void setQrCodeImg(String qrCodeImg) {
        this.qrCodeImg = qrCodeImg == null ? null : qrCodeImg.trim();
    }

    public Boolean getQrCodeType() {
        return qrCodeType;
    }

    public void setQrCodeType(Boolean qrCodeType) {
        this.qrCodeType = qrCodeType;
    }

    public Boolean getStoresStatus() {
        return storesStatus;
    }

    public void setStoresStatus(Boolean storesStatus) {
        this.storesStatus = storesStatus;
    }

    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getServiceSupport() {
        return serviceSupport;
    }

    public void setServiceSupport(String serviceSupport) {
        this.serviceSupport = serviceSupport == null ? null : serviceSupport.trim();
    }

    public String getSelfTokenTime() {
        return selfTokenTime;
    }

    public void setSelfTokenTime(String selfTokenTime) {
        this.selfTokenTime = selfTokenTime == null ? null : selfTokenTime.trim();
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime == null ? null : deliveryTime.trim();
    }

    public String getRemindMobile() {
        return remindMobile;
    }

    public void setRemindMobile(String remindMobile) {
        this.remindMobile = remindMobile == null ? null : remindMobile.trim();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public Byte getOwnPricingType() {
        return ownPricingType;
    }

    public void setOwnPricingType(Byte ownPricingType) {
        this.ownPricingType = ownPricingType;
    }

    public Byte getOwnPromotionType() {
        return ownPromotionType;
    }

    public void setOwnPromotionType(Byte ownPromotionType) {
        this.ownPromotionType = ownPromotionType;
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

    public String getStoreImgs() {
        return storeImgs;
    }

    public void setStoreImgs(String storeImgs) {
        this.storeImgs = storeImgs == null ? null : storeImgs.trim();
    }
}
