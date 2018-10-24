package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 门店信息
 * 作者: baixiongfei
 * 创建日期: 2017/2/16
 * 修改记录:
 */
public class Store {

    private int siteId;//品牌ID

    private int id;

    private String storesNumber;//门店编号

    private String name;//门店名称

    private int isQjd;//是否旗舰店(1 是 0 否)

    private int type;//门店类型(直营店 1 加盟店2 )

    private int cityId;//门店所属城市

    private int regionId;//门店所属区域

    private String address;//门店地址

    private String baiduLat;//百度坐标，纬度 | 百度地图暂时不用，目前就用高德地图

    private String baiduLng;//百度坐标，经度

    private String gaodeLat;//高德坐标，纬度

    private String gaodeLng;//高德坐标，经度

    private int mapFlag;//坐标是否标注: 1 是 0 否

    private String storeImgs;//门店图片

    private String tel;//门店电话

    private String businessTime;//门店值班时间

    private String feature;//门店特色

    private String summary;//门店简介

    private String qrCodeImg;//二维码id

    private int qrCodeType;//二维码是否带logo (1 是 0 否)

    private int storesStatus;//是否有效(0=禁用 1=启用)

    private int isDel;//门店软删除（0：表示软删除，默认为1）

    private String province;//省份

    private String city;//市

    private String serviceSupport;//支持服务 150=送货上门 160=门店自提,多种服务请用逗号连接

    private String selfTokenTime;//自提时间

    private String deliveryTime;//送货时间

    private String remindMobile;//提醒手机

    private String order_lert;//提醒设置

    private String country;//区

    private int ownPricingType;//自主定价(默认0,1允许)

    private int ownPromotionType;//自主创建优惠活动(默认0,1允许)

    private Integer upPricingType;//自主改价(默认0,1允许)

    private Timestamp createTime;//创建时间

    private Timestamp updateTime;//更新时间

    private String admin;

    private String pwd;

    private Integer storeAdminId;

    private String deviceNum;//设备号

    private String deviceImgUrl;//设备号图片

    private Integer deviceFlag;//判断该商户是否使用设备号

    public String getOrder_lert() {
        return order_lert;
    }

    public void setOrder_lert(String order_lert) {
        this.order_lert = order_lert;
    }

    public Integer getUpPricingType() {
        return upPricingType;
    }

    public void setUpPricingType(Integer upPricingType) {
        this.upPricingType = upPricingType;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStoresNumber() {
        return storesNumber;
    }

    public void setStoresNumber(String storesNumber) {
        this.storesNumber = storesNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsQjd() {
        return isQjd;
    }

    public void setIsQjd(int isQjd) {
        this.isQjd = isQjd;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBaiduLat() {
        return baiduLat;
    }

    public void setBaiduLat(String baiduLat) {
        this.baiduLat = baiduLat;
    }

    public String getBaiduLng() {
        return baiduLng;
    }

    public void setBaiduLng(String baiduLng) {
        this.baiduLng = baiduLng;
    }

    public String getGaodeLat() {
        return gaodeLat;
    }

    public void setGaodeLat(String gaodeLat) {
        this.gaodeLat = gaodeLat;
    }

    public String getGaodeLng() {
        return gaodeLng;
    }

    public void setGaodeLng(String gaodeLng) {
        this.gaodeLng = gaodeLng;
    }

    public int getMapFlag() {
        return mapFlag;
    }

    public void setMapFlag(int mapFlag) {
        this.mapFlag = mapFlag;
    }

    public String getStoreImgs() {
        return storeImgs;
    }

    public void setStoreImgs(String storeImgs) {
        this.storeImgs = storeImgs;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getBusinessTime() {
        return businessTime;
    }

    public void setBusinessTime(String businessTime) {
        this.businessTime = businessTime;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getQrCodeImg() {
        return qrCodeImg;
    }

    public void setQrCodeImg(String qrCodeImg) {
        this.qrCodeImg = qrCodeImg;
    }

    public int getQrCodeType() {
        return qrCodeType;
    }

    public void setQrCodeType(int qrCodeType) {
        this.qrCodeType = qrCodeType;
    }

    public int getStoresStatus() {
        return storesStatus;
    }

    public void setStoresStatus(int storesStatus) {
        this.storesStatus = storesStatus;
    }

    public int getIsDel() {
        return isDel;
    }

    public void setIsDel(int isDel) {
        this.isDel = isDel;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getServiceSupport() {
        return serviceSupport;
    }

    public void setServiceSupport(String serviceSupport) {
        this.serviceSupport = serviceSupport;
    }

    public String getSelfTokenTime() {
        return selfTokenTime;
    }

    public void setSelfTokenTime(String selfTokenTime) {
        this.selfTokenTime = selfTokenTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getRemindMobile() {
        return remindMobile;
    }

    public void setRemindMobile(String remindMobile) {
        this.remindMobile = remindMobile;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getOwnPricingType() {
        return ownPricingType;
    }

    public void setOwnPricingType(int ownPricingType) {
        this.ownPricingType = ownPricingType;
    }

    public int getOwnPromotionType() {
        return ownPromotionType;
    }

    public void setOwnPromotionType(int ownPromotionType) {
        this.ownPromotionType = ownPromotionType;
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

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public String getDeviceImgUrl() {
        return deviceImgUrl;
    }

    public void setDeviceImgUrl(String deviceImgUrl) {
        this.deviceImgUrl = deviceImgUrl;
    }

    public Integer getDeviceFlag() {
        return deviceFlag;
    }

    public void setDeviceFlag(Integer deviceFlag) {
        this.deviceFlag = deviceFlag;
    }

    @Override
    public String toString() {
        return "Store{" +
            "siteId=" + siteId +
            ", id=" + id +
            ", storesNumber='" + storesNumber + '\'' +
            ", name='" + name + '\'' +
            ", isQjd=" + isQjd +
            ", type=" + type +
            ", cityId=" + cityId +
            ", regionId=" + regionId +
            ", address='" + address + '\'' +
            ", baiduLat='" + baiduLat + '\'' +
            ", baiduLng='" + baiduLng + '\'' +
            ", gaodeLat='" + gaodeLat + '\'' +
            ", gaodeLng='" + gaodeLng + '\'' +
            ", mapFlag=" + mapFlag +
            ", storeImgs='" + storeImgs + '\'' +
            ", tel='" + tel + '\'' +
            ", businessTime='" + businessTime + '\'' +
            ", feature='" + feature + '\'' +
            ", summary='" + summary + '\'' +
            ", qrCodeImg='" + qrCodeImg + '\'' +
            ", qrCodeType=" + qrCodeType +
            ", storesStatus=" + storesStatus +
            ", isDel=" + isDel +
            ", province='" + province + '\'' +
            ", city='" + city + '\'' +
            ", serviceSupport='" + serviceSupport + '\'' +
            ", selfTokenTime='" + selfTokenTime + '\'' +
            ", deliveryDime='" + deliveryTime + '\'' +
            ", remindMobile='" + remindMobile + '\'' +
            ", country='" + country + '\'' +
            ", ownPricingType=" + ownPricingType +
            ", ownPromotionType=" + ownPromotionType +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            '}';
    }
}
