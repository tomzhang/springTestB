package com.jk51.model.balance;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/5/11
 * 修改记录:
 */
public class SmsFeeRule {

    private Integer id;
    private Integer siteId;
    private Integer fee;
    private Integer smlNum;
    private Integer bigNum;
    private Integer isDel;
    private Date createTime;
    private Date updateTime;

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

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public Integer getSmlNum() {
        return smlNum;
    }

    public void setSmlNum(Integer smlNum) {
        this.smlNum = smlNum;
    }

    public Integer getBigNum() {
        return bigNum;
    }

    public void setBigNum(Integer bigNum) {
        this.bigNum = bigNum;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
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
