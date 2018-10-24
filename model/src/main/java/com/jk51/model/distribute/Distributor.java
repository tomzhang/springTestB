package com.jk51.model.distribute;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 分销商基本信息实体
 * @author yeah
 */
public class Distributor {
    private Integer id;

    private Integer uid;

    private String userName;

    private String realName;

    private String wechatName;

    private String alipayName;

    private Byte applyType;

    private Byte isRoot;

    private byte level;

    private Byte status;

    private String note;

    private Integer owner;
    
    private Date createTime;
    
    private Date updateTime;

    private Byte isOpen;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName == null ? null : realName.trim();
    }

    public String getWechatName() {
        return wechatName;
    }

    public void setWechatName(String wechatName) {
        this.wechatName = wechatName == null ? null : wechatName.trim();
    }

    public String getAlipayName() {
        return alipayName;
    }

    public void setAlipayName(String alipayName) {
        this.alipayName = alipayName == null ? null : alipayName.trim();
    }

    public Byte getApplyType() {
        return applyType;
    }

    public void setApplyType(Byte applyType) {
        this.applyType = applyType;
    }

    public Byte getIsRoot() {
        return isRoot;
    }

    public void setIsRoot(Byte isRoot) {
        this.isRoot = isRoot;
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
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

    public Byte getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Byte isOpen) {
        this.isOpen = isOpen;
    }

    @Override
    public String toString() {
        return "Distributor{" +
                "id=" + id +
                ", uid=" + uid +
                ", userName='" + userName + '\'' +
                ", realName='" + realName + '\'' +
                ", wechatName='" + wechatName + '\'' +
                ", alipayName='" + alipayName + '\'' +
                ", applyType=" + applyType +
                ", isRoot=" + isRoot +
                ", level=" + level +
                ", status=" + status +
                ", note='" + note + '\'' +
                ", owner=" + owner +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isOpen=" + isOpen +
                '}';
    }
}