package com.jk51.model.clerkvisit;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BVisitDesc implements Serializable {
    private Integer id;

    private Integer siteId;

    /**
     * 回访ID
     */
    private Integer visitId;

    /**
     * 店员ID
     */
    private Integer storeAdminId;

    /**
     * 店员姓名
     */
    private String adminName;

    /**
     * 店员门店ID
     */
    private Integer storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 会员ID
     */
    private Integer buyerId;

    /**
     * 店员发券数
     */
    private Integer sendCouponNum;

    /**
     * 短信发送情况  10 未发  20 已发
     */
    private Byte smsStatus;

    /**
     * 会员是否打开活动页面 10 未发送 20 已发送顾客未打开 30 顾客已浏览
     */
    private Byte pageStatus;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

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

    public Integer getVisitId() {
        return visitId;
    }

    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
    }

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
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
        this.storeName = storeName;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getSendCouponNum() {
        return sendCouponNum;
    }

    public void setSendCouponNum(Integer sendCouponNum) {
        this.sendCouponNum = sendCouponNum;
    }

    public Byte getSmsStatus() {
        return smsStatus;
    }

    public void setSmsStatus(Byte smsStatus) {
        this.smsStatus = smsStatus;
    }

    public Byte getPageStatus() {
        return pageStatus;
    }

    public void setPageStatus(Byte pageStatus) {
        this.pageStatus = pageStatus;
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
}
