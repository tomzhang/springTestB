package com.jk51.model.treat;


import java.util.Date;

public class WxAutoReplyOnOff {
    private Integer id;
    private Integer siteId;
    private Date createTime;
    private Date updateTime;
    private Integer onOff;

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

    public Integer getOnOff() {
        return onOff;
    }

    public void setOnOff(Integer onOff) {
        this.onOff = onOff;
    }
}
