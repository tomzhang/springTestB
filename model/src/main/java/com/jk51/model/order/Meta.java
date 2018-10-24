package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:商家配送方式及配送费设置信息
 * 作者: baixiongfei
 * 创建日期: 2017/2/20
 * 修改记录:
 */
public class Meta {

    private int metaId;
    private int siteId;
    private String metaKey;
    private String MetaType;
    private String metaVal;
    private String metaDesc;
    private int metaStatus;
    private Timestamp createTime;
    private Timestamp updateTime;

    public int getMetaId() {
        return metaId;
    }

    public void setMetaId(int metaId) {
        this.metaId = metaId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

    public String getMetaType() {
        return MetaType;
    }

    public void setMetaType(String metaType) {
        MetaType = metaType;
    }

    public String getMetaVal() {
        return metaVal;
    }

    public void setMetaVal(String metaVal) {
        this.metaVal = metaVal;
    }

    public String getMetaDesc() {
        return metaDesc;
    }

    public void setMetaDesc(String metaDesc) {
        this.metaDesc = metaDesc;
    }

    public int getMetaStatus() {
        return metaStatus;
    }

    public void setMetaStatus(int metaStatus) {
        this.metaStatus = metaStatus;
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
        return "Meta{" +
                "metaId=" + metaId +
                ", siteId=" + siteId +
                ", metaKey='" + metaKey + '\'' +
                ", MetaType='" + MetaType + '\'' +
                ", metaVal='" + metaVal + '\'' +
                ", metaDesc='" + metaDesc + '\'' +
                ", metaStatus=" + metaStatus +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
