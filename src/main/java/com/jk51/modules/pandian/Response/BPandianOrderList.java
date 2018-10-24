package com.jk51.modules.pandian.Response;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/5/16
 * 修改记录:
 */
public class BPandianOrderList {

    private Integer id;
    private Integer siteId;
    private Integer storeId;
    private Integer type;
    private String pandianNum;
    private Integer planId;
    private Date createTime;
    private Date updateTime;
    private Integer isUpSite;
    private String storeName;
    private String storesNumber;
    private Integer status;  //盘点状态
    private Integer planCheckType;
    private String billid;

    private BPandianOrderListCount count;

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

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPandianNum() {
        return pandianNum;
    }

    public void setPandianNum(String pandianNum) {
        this.pandianNum = pandianNum;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
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

    public Integer getIsUpSite() {
        return isUpSite;
    }

    public void setIsUpSite(Integer isUpSite) {
        this.isUpSite = isUpSite;
    }

    public BPandianOrderListCount getCount() {
        return count;
    }

    public void setCount(BPandianOrderListCount count) {
        this.count = count;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoresNumber() {
        return storesNumber;
    }

    public void setStoresNumber(String storesNumber) {
        this.storesNumber = storesNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPlanCheckType() {
        return planCheckType;
    }

    public void setPlanCheckType(Integer planCheckType) {
        this.planCheckType = planCheckType;
    }

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid;
    }
}
