package com.jk51.model.balance;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/5/11
 * 修改记录:
 */
public class BaseFeeSet {

    private Integer id;
    private Integer siteId;
    private String name;
    private String scene;
    private String deliveryType;
    private String payType;
    private Integer feeRule;//费用规则（0-按订单实付金额，不含运费；1-按订单实付金额，含运费）
    private Integer feeType;
    private Float feeRate;
    private String refuseRule;//退单规则
    private String mark;
    private Integer isDel;
    private Date createTime;
    private Date updateTime;

    private Integer refuseType;//拒单类型

    public Integer getRefuseType() {
        return refuseType;
    }

    public void setRefuseType(Integer refuseType) {
        this.refuseType = refuseType;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Integer getFeeRule() {
        return feeRule;
    }

    public void setFeeRule(Integer feeRule) {
        this.feeRule = feeRule;
    }

    public Integer getFeeType() {
        return feeType;
    }

    public void setFeeType(Integer feeType) {
        this.feeType = feeType;
    }

    public Float getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(Float feeRate) {
        this.feeRate = feeRate;
    }

    public String getRefuseRule() {
        return refuseRule;
    }

    public void setRefuseRule(String refuseRule) {
        this.refuseRule = refuseRule;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
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

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }
}
