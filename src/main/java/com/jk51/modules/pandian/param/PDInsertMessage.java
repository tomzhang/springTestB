package com.jk51.modules.pandian.param;

import com.jk51.modules.pandian.job.PandianErpSyncMessage;

public class PDInsertMessage {
    public static final int MESSAGE_TYPE = PandianErpSyncMessage.Constant.PDINSERT_MESSAGE;

    private Integer siteId;

    private Integer planId;

    private Integer orderId;

    private Integer storeId;

    private String storeNum;

    private String orderNum;

    private String erpStoreTaskNum;

    private String directoryPath;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(String storeNum) {
        this.storeNum = storeNum;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getErpStoreTaskNum() {
        return erpStoreTaskNum;
    }

    public void setErpStoreTaskNum(String erpStoreTaskNum) {
        this.erpStoreTaskNum = erpStoreTaskNum;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String toString() {
        return "PDInsertMessage{" +
            "siteId=" + siteId +
            ", planId=" + planId +
            ", orderId=" + orderId +
            ", storeId=" + storeId +
            ", storeNum='" + storeNum + '\'' +
            ", orderNum='" + orderNum + '\'' +
            ", erpStoreTaskNum='" + erpStoreTaskNum + '\'' +
            ", directoryPath='" + directoryPath + '\'' +
            '}';
    }
}
