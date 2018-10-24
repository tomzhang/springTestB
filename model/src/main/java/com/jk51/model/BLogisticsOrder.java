package com.jk51.model;

import java.util.Date;

public class BLogisticsOrder {
    private String waybillNumber;

    private Long orderNumber;

    private Integer logisticsId;

    private String logisticsName;

    private String province;

    private String city;

    private Integer storeId;

    private String storeName;

    private Date orderTime;

    private Integer orderAmount;

    private Integer distributionDistance;

    private Integer totalFee;

    private Integer totalWeight;

    private Integer startingFare;

    private Integer surpassDistanceFare;

    private Integer overweightChargeFare;

    private Integer overtimeFare;

    private Integer chargebackFare;

    private String diliveryman;

    private Long distributionPhone;

    private Byte status;

    private String errorCode;

    private Date createTime;

    private Date updateTime;

    private Integer operatorId;

    private Long notifyMobile;
	
	   private Integer id;

    private Integer siteId;

    private String orderNo;//解决页面显示订单号不全问题

    private  Integer o2OFreight;//运费

    public Integer getO2OFreight() {
        return o2OFreight;
    }

    public void setO2OFreight(Integer o2OFreight) {
        this.o2OFreight = o2OFreight;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber == null ? null : waybillNumber.trim();
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(Integer logisticsId) {
        this.logisticsId = logisticsId;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName == null ? null : logisticsName.trim();
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

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName == null ? null : storeName.trim();
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Integer getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Integer orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Integer getDistributionDistance() {
        return distributionDistance;
    }

    public void setDistributionDistance(Integer distributionDistance) {
        this.distributionDistance = distributionDistance;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Integer totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Integer getStartingFare() {
        return startingFare;
    }

    public void setStartingFare(Integer startingFare) {
        this.startingFare = startingFare;
    }

    public Integer getSurpassDistanceFare() {
        return surpassDistanceFare;
    }

    public void setSurpassDistanceFare(Integer surpassDistanceFare) {
        this.surpassDistanceFare = surpassDistanceFare;
    }

    public Integer getOverweightChargeFare() {
        return overweightChargeFare;
    }

    public void setOverweightChargeFare(Integer overweightChargeFare) {
        this.overweightChargeFare = overweightChargeFare;
    }

    public Integer getOvertimeFare() {
        return overtimeFare;
    }

    public void setOvertimeFare(Integer overtimeFare) {
        this.overtimeFare = overtimeFare;
    }

    public Integer getChargebackFare() {
        return chargebackFare;
    }

    public void setChargebackFare(Integer chargebackFare) {
        this.chargebackFare = chargebackFare;
    }

    public String getDiliveryman() {
        return diliveryman;
    }

    public void setDiliveryman(String diliveryman) {
        this.diliveryman = diliveryman == null ? null : diliveryman.trim();
    }

    public Long getDistributionPhone() {
        return distributionPhone;
    }

    public void setDistributionPhone(Long distributionPhone) {
        this.distributionPhone = distributionPhone;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode == null ? null : errorCode.trim();
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

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Long getNotifyMobile() {
        return notifyMobile;
    }

    public void setNotifyMobile(Long notifyMobile) {
        this.notifyMobile = notifyMobile;
    }

}