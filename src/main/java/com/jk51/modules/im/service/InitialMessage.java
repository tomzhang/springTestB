package com.jk51.modules.im.service;

public class InitialMessage {
    private String siteId;
    private String storeId;
    private String storeAdminId;
    private String messageMapJSON;
    private String messageType;
    private Integer offLine;
    private Integer wifi;
    private String badgeNum;

    /**
     * 推给一个店员：id, id, id
     * 推给一个门店：id, id, null
     * 推给一个商家：id, null, null
     * @param siteId
     * @param storeId
     * @param storeAdminId
     */
    public InitialMessage(String siteId, String storeId, String storeAdminId) {
        this.siteId = siteId;
        this.storeId = storeId;
        this.storeAdminId = storeAdminId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(String storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public String getMessageMapJSON() {
        return messageMapJSON;
    }

    public void setMessageMapJSON(String messageMapJSON) {
        this.messageMapJSON = messageMapJSON;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Integer getOffLine() {
        return offLine;
    }

    public void setOffLine(Integer offLine) {
        this.offLine = offLine;
    }

    public Integer getWifi() {
        return wifi;
    }

    public void setWifi(Integer wifi) {
        this.wifi = wifi;
    }

    public String getBadgeNum() {
        return badgeNum;
    }

    public void setBadgeNum(String badgeNum) {
        this.badgeNum = badgeNum;
    }

    @Override
    public String toString() {
        return "InitialMessage{" +
                "siteId='" + siteId + '\'' +
                ", storeId='" + storeId + '\'' +
                ", storeAdminId='" + storeAdminId + '\'' +
                ", messageMapJSON='" + messageMapJSON + '\'' +
                ", messageType='" + messageType + '\'' +
                ", offLine=" + offLine +
                ", wifi=" + wifi +
                ", badgeNum='" + badgeNum + '\'' +
                '}';
    }
}
