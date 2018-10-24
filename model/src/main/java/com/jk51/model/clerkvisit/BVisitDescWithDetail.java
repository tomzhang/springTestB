package com.jk51.model.clerkvisit;

import java.util.Date;

/**
 * Created by Administrator on 2017/12/26.
 */
public class BVisitDescWithDetail {


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

    private String adminMobile;

    private String buyerMobile;

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

    /**
     * 回访反馈
     */
    private Integer telResult;

    private Integer goodsNum;

    private String tradesIds;

    /**
     * 回访状态
     */
    private Integer status;

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

    public Integer getTelResult() {
        return telResult;
    }

    public void setTelResult(Integer telResult) {
        this.telResult = telResult;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getTradesIds() {
        return tradesIds;
    }

    public void setTradesIds(String tradesIds) {
        this.tradesIds = tradesIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAdminMobile() {
        return adminMobile;
    }

    public void setAdminMobile(String adminMobile) {
        this.adminMobile = adminMobile;
    }

    public String getBuyerMobile() {
        return buyerMobile;
    }

    public void setBuyerMobile(String buyerMobile) {
        this.buyerMobile = buyerMobile;
    }
}
