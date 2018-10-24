package com.jk51.model.integral;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-03-08
 * 修改记录:
 */
public class IntegralLog {
    private Integer siteId;
    private Integer id;
    private Integer memberId;
    private String buyerNick;
    private String integralDesc;
    private BigInteger integralAdd;
    private BigInteger integralDiff;
    private BigInteger integralOverplus;
    private String mark;
    private Timestamp createTime;
    private Timestamp updateTime;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    private Integer type;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getBuyerNick() {
        return buyerNick;
    }

    public void setBuyerNick(String buyerNick) {
        this.buyerNick = buyerNick;
    }

    public String getIntegralDesc() {
        return integralDesc;
    }

    public void setIntegralDesc(String integralDesc) {
        this.integralDesc = integralDesc;
    }

    public BigInteger getIntegralAdd() {
        return integralAdd;
    }

    public void setIntegralAdd(BigInteger integralAdd) {
        this.integralAdd = integralAdd;
    }

    public BigInteger getIntegralDiff() {
        return integralDiff;
    }

    public void setIntegralDiff(BigInteger integralDiff) {
        this.integralDiff = integralDiff;
    }

    public BigInteger getIntegralOverplus() {
        return integralOverplus;
    }

    public void setIntegralOverplus(BigInteger integralOverplus) {
        this.integralOverplus = integralOverplus;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
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

    @Override
    public String toString() {
        return "IntegralLog{" +
                "siteId=" + siteId +
                ", id=" + id +
                ", memberId=" + memberId +
                ", buyerNick='" + buyerNick + '\'' +
                ", integralDesc='" + integralDesc + '\'' +
                ", integralAdd=" + integralAdd +
                ", integralDiff=" + integralDiff +
                ", integralOverplus=" + integralOverplus +
                ", mark='" + mark + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
