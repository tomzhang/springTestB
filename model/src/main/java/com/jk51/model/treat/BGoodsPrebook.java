package com.jk51.model.treat;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 预约订单JavaBean
 * 作者: baixiongfei
 * 创建日期: 2017/3/11
 * 修改记录:
 */
public class BGoodsPrebook {

    private int	siteId;
    private int	prebookId;
    private String prebookPhone;
    private int	prebookGoodsId;
    private String prebookGoodsName;
    private int	prebookGoodsNum;
    private int	prebookClerkId;
    private String prebookClerk;
    private String prebookTrades;
    private Timestamp prebookAcceptTime;
    private Timestamp prebookTradesTime;
    private int	prebookState;
    private Timestamp createTime;
    private Timestamp updateTime;

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getPrebookId() {
        return prebookId;
    }

    public void setPrebookId(int prebookId) {
        this.prebookId = prebookId;
    }

    public String getPrebookPhone() {
        return prebookPhone;
    }

    public void setPrebookPhone(String prebookPhone) {
        this.prebookPhone = prebookPhone;
    }

    public int getPrebookGoodsId() {
        return prebookGoodsId;
    }

    public void setPrebookGoodsId(int prebookGoodsId) {
        this.prebookGoodsId = prebookGoodsId;
    }

    public String getPrebookGoodsName() {
        return prebookGoodsName;
    }

    public void setPrebookGoodsName(String prebookGoodsName) {
        this.prebookGoodsName = prebookGoodsName;
    }

    public int getPrebookGoodsNum() {
        return prebookGoodsNum;
    }

    public void setPrebookGoodsNum(int prebookGoodsNum) {
        this.prebookGoodsNum = prebookGoodsNum;
    }

    public int getPrebookClerkId() {
        return prebookClerkId;
    }

    public void setPrebookClerkId(int prebookClerkId) {
        this.prebookClerkId = prebookClerkId;
    }

    public String getPrebookClerk() {
        return prebookClerk;
    }

    public void setPrebookClerk(String prebookClerk) {
        this.prebookClerk = prebookClerk;
    }

    public String getPrebookTrades() {
        return prebookTrades;
    }

    public void setPrebookTrades(String prebookTrades) {
        this.prebookTrades = prebookTrades;
    }

    public Timestamp getPrebookAcceptTime() {
        return prebookAcceptTime;
    }

    public void setPrebookAcceptTime(Timestamp prebookAcceptTime) {
        this.prebookAcceptTime = prebookAcceptTime;
    }

    public Timestamp getPrebookTradesTime() {
        return prebookTradesTime;
    }

    public void setPrebookTradesTime(Timestamp prebookTradesTime) {
        this.prebookTradesTime = prebookTradesTime;
    }

    public int getPrebookState() {
        return prebookState;
    }

    public void setPrebookState(int prebookState) {
        this.prebookState = prebookState;
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
        return "BGoodsPrebook{" +
            "siteId=" + siteId +
            ", prebookId=" + prebookId +
            ", prebookPhone='" + prebookPhone + '\'' +
            ", prebookGoodsId=" + prebookGoodsId +
            ", prebookGoodsName='" + prebookGoodsName + '\'' +
            ", prebookGoodsNum=" + prebookGoodsNum +
            ", prebookClerkId=" + prebookClerkId +
            ", prebookClerk='" + prebookClerk + '\'' +
            ", prebookTrades='" + prebookTrades + '\'' +
            ", prebookAcceptTime=" + prebookAcceptTime +
            ", prebookTradesTime=" + prebookTradesTime +
            ", prebookState=" + prebookState +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            '}';
    }
}
