package com.jk51.model;

import java.sql.Timestamp;

/**
 * 门店关联表
 * Created by Administrator on 2018/6/29.
 */
public class GoodsStoreRelation {
    private Integer id;
    private Integer siteId;
    private Integer goodsId;
    private String storeIds;
    private Timestamp createTime;
    private Timestamp updateTime;

    @Override
    public String toString() {
        return "GoodsStoreRelation{" +
            "id=" + id +
            ", siteId=" + siteId +
            ", goodsId=" + goodsId +
            ", storeIds='" + storeIds + '\'' +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            '}';
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

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getStoreIds() {
        return storeIds;
    }

    public void setStoreIds(String storeIds) {
        this.storeIds = storeIds;
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


}
