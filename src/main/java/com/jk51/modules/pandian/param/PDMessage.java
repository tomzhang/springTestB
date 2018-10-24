package com.jk51.modules.pandian.param;

import com.jk51.modules.pandian.job.PandianErpSyncMessage;

import java.util.List;

public class PDMessage {
    public static final int MESSAGE_TYPE = PandianErpSyncMessage.Constant.PD_MESSAGE;

    private Integer siteId;

    private Integer type;//0-总部计划；1-门店计划

    private Integer planId;

    private Integer orderId;

    private String orderNum;

    private int insertType;//0-自动erp导入；1-人工erp导入；

    private List<Integer> storeIdList;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public int getInsertType() {
        return insertType;
    }

    public void setInsertType(int insertType) {
        this.insertType = insertType;
    }

    public List<Integer> getStoreIdList() {
        return storeIdList;
    }

    public void setStoreIdList(List<Integer> storeIdList) {
        this.storeIdList = storeIdList;
    }

    @Override
    public String toString() {
        return "PDMessage{" +
            "siteId=" + siteId +
            ", type=" + type +
            ", planId=" + planId +
            ", orderId=" + orderId +
            ", orderNum='" + orderNum + '\'' +
            ", insertType=" + insertType +
            ", storeIdList=" + storeIdList +
            '}';
    }
}
