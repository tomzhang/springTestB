package com.jk51.model;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/7/2
 * 修改记录:
 */
public class PandianSortRemindHitRate {

    private Integer id;
    private String pandianNum;
    private Integer siteId;
    private Integer storeId;
    private Integer sameNum;
    private Integer notSameNum;
    private Integer notRemindNum;
    private Integer nextChecked;
    private Integer nextNotInInventory;
    private Integer nextNotInRedis;
    private Integer currentNotInRedis;
    private Integer hasRemind;
    private Date createTime;
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPandianNum() {
        return pandianNum;
    }

    public void setPandianNum(String pandianNum) {
        this.pandianNum = pandianNum;
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


    public Integer getSameNum() {
        return sameNum;
    }

    public void setSameNum(Integer sameNum) {
        this.sameNum = sameNum;
    }

    public Integer getNotSameNum() {
        return notSameNum;
    }

    public void setNotSameNum(Integer notSameNum) {
        this.notSameNum = notSameNum;
    }

    public Integer getNotRemindNum() {
        return notRemindNum;
    }

    public void setNotRemindNum(Integer notRemindNum) {
        this.notRemindNum = notRemindNum;
    }

    public Integer getNextChecked() {
        return nextChecked;
    }

    public void setNextChecked(Integer nextChecked) {
        this.nextChecked = nextChecked;
    }

    public Integer getNextNotInInventory() {
        return nextNotInInventory;
    }

    public void setNextNotInInventory(Integer nextNotInInventory) {
        this.nextNotInInventory = nextNotInInventory;
    }

    public Integer getNextNotInRedis() {
        return nextNotInRedis;
    }

    public void setNextNotInRedis(Integer nextNotInRedis) {
        this.nextNotInRedis = nextNotInRedis;
    }

    public Integer getCurrentNotInRedis() {
        return currentNotInRedis;
    }

    public void setCurrentNotInRedis(Integer currentNotInRedis) {
        this.currentNotInRedis = currentNotInRedis;
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

    public Integer getHasRemind() {
        return hasRemind;
    }

    public void setHasRemind(Integer hasRemind) {
        this.hasRemind = hasRemind;
    }
}
