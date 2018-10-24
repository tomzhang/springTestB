package com.jk51.modules.marketing.request;

import java.util.Date;

public class Winner {
    private String mobile;

    private String name;

    private Integer type;

    private String tName;

    private Integer typeId;

    private String typeInfo;

    private Integer tag;

    private Date createTime;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeInfo() {
        return typeInfo;
    }

    public void setTypeInfo(String typeInfo) {
        this.typeInfo = typeInfo;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Winner{" +
            "mobile='" + mobile + '\'' +
            ", name='" + name + '\'' +
            ", type=" + type +
            ", tName='" + tName + '\'' +
            ", typeId=" + typeId +
            ", typeInfo=" + typeInfo +
            ", tag=" + tag +
            ", createTime=" + createTime +
            '}';
    }
}
