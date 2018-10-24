package com.jk51.model.distribute;

import java.util.Date;

public class DistributorExt {
    private Integer id;

    private Integer did;

    private Integer uid;

    private Integer parentId;

    private String mobile;

    private String userName;

    private Integer account;

    private Integer frozenAccount;

    private Integer storeAccount;

    private Integer rewardAccount;

    private Byte status;

    private String note;

    private String invitationCode;

    private Boolean isDel;

    private Date createTime;

    private Date updateTime;

    private Integer chargeAccount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDid() {
        return did;
    }

    public void setDid(Integer did) {
        this.did = did;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public Integer getAccount() {
        return account;
    }

    public void setAccount(Integer account) {
        this.account = account;
    }

    public Integer getFrozenAccount() {
        return frozenAccount;
    }

    public void setFrozenAccount(Integer frozenAccount) {
        this.frozenAccount = frozenAccount;
    }

    public Integer getStoreAccount() {
        return storeAccount;
    }

    public void setStoreAccount(Integer storeAccount) {
        this.storeAccount = storeAccount;
    }

    public Integer getRewardAccount() {
        return rewardAccount;
    }

    public void setRewardAccount(Integer rewardAccount) {
        this.rewardAccount = rewardAccount;
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

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode == null ? null : invitationCode.trim();
    }

    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
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

    public Integer getChargeAccount() {
        return chargeAccount;
    }

    public void setChargeAccount(Integer chargeAccount) {
        this.chargeAccount = chargeAccount;
    }
}