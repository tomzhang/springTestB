package com.jk51.model.order;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:  对应数据库b_member
 * 作者: hulan
 * 创建日期: 2017-02-20
 * 修改记录:
 */
public class Member {

    private Integer siteId;
    private Integer memberId;
    private Integer buyerId;
    private String buyerNick;
    private String mobile;
    private String passwd;
    private Integer sex;
    private String email;
    private String idcardNumber;
    private Integer orderNum;
    private Integer orderFee;
    private String memo;
    private Integer registerStores;
    private BigInteger registerClerks;
    private String name;
    private String lastIpaddr;
    private BigInteger integrate;
    private BigInteger totalGetIntegrate;
    private BigInteger totalConsumeIntegrate;
    private Integer memSource;
    private Integer isActivated;
    private Integer banStatus;
    private Timestamp lastTime;
    private Timestamp createTime;
    private Timestamp updateTime;

    private Long offlineIntegral;

    private Boolean firstErp;

    private String registerTime;

    private String storName;
    private String openId;
    private String aliUserId;

    public String getAliUserId() {
        return aliUserId;
    }

    public void setAliUserId(String aliUserId) {
        this.aliUserId = aliUserId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOpenId() {
        return openId;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getStorName() {
        return storName;
    }

    public void setStorName(String storName) {
        this.storName = storName;
    }

    public Long getOfflineIntegral() {
        return offlineIntegral;
    }

    public void setOfflineIntegral(Long offlineIntegral) {
        this.offlineIntegral = offlineIntegral;
    }

    public Boolean getFirstErp() {
        return firstErp;
    }

    public void setFirstErp(Boolean firstErp) {
        this.firstErp = firstErp;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerNick() {
        return buyerNick;
    }

    public void setBuyerNick(String buyerNick) {
        this.buyerNick = buyerNick;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdcardNumber() {
        return idcardNumber;
    }

    public void setIdcardNumber(String idcardNumber) {
        this.idcardNumber = idcardNumber;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getOrderFee() {
        return orderFee;
    }

    public void setOrderFee(Integer orderFee) {
        this.orderFee = orderFee;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getRegisterStores() {
        return registerStores;
    }

    public void setRegisterStores(Integer registerStores) {
        this.registerStores = registerStores;
    }

    public BigInteger getRegisterClerks() {
        return registerClerks;
    }

    public void setRegisterClerks(BigInteger registerClerks) {
        this.registerClerks = registerClerks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastIpaddr() {
        return lastIpaddr;
    }

    public void setLastIpaddr(String lastIpaddr) {
        this.lastIpaddr = lastIpaddr;
    }

    public BigInteger getIntegrate() {
        return integrate;
    }

    public void setIntegrate(BigInteger integrate) {
        this.integrate = integrate;
    }

    public BigInteger getTotalGetIntegrate() {
        return totalGetIntegrate;
    }

    public void setTotalGetIntegrate(BigInteger totalGetIntegrate) {
        this.totalGetIntegrate = totalGetIntegrate;
    }

    public BigInteger getTotalConsumeIntegrate() {
        return totalConsumeIntegrate;
    }

    public void setTotalConsumeIntegrate(BigInteger totalConsumeIntegrate) {
        this.totalConsumeIntegrate = totalConsumeIntegrate;
    }

    public Integer getMemSource() {
        return memSource;
    }

    public void setMemSource(Integer memSource) {
        this.memSource = memSource;
    }

    public Integer getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(Integer isActivated) {
        this.isActivated = isActivated;
    }

    public Integer getBanStatus() {
        return banStatus;
    }

    public void setBanStatus(Integer banStatus) {
        this.banStatus = banStatus;
    }

    public Timestamp getLastTime() {
        return lastTime;
    }

    public void setLastTime(Timestamp lastTime) {
        this.lastTime = lastTime;
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
        return "Member{" +
            "siteId=" + siteId +
            ", memberId=" + memberId +
            ", buyerId=" + buyerId +
            ", buyerNick='" + buyerNick + '\'' +
            ", mobile='" + mobile + '\'' +
            ", passwd='" + passwd + '\'' +
            ", sex=" + sex +
            ", email='" + email + '\'' +
            ", idcardNumber='" + idcardNumber + '\'' +
            ", orderNum=" + orderNum +
            ", orderFee=" + orderFee +
            ", memo='" + memo + '\'' +
            ", registerStores=" + registerStores +
            ", registerClerks=" + registerClerks +
            ", name='" + name + '\'' +
            ", lastIpaddr='" + lastIpaddr + '\'' +
            ", integrate=" + integrate +
            ", totalGetIntegrate=" + totalGetIntegrate +
            ", totalConsumeIntegrate=" + totalConsumeIntegrate +
            ", memSource=" + memSource +
            ", isActivated=" + isActivated +
            ", banStatus=" + banStatus +
            ", storName=" + storName +
            ", registerTime=" + registerTime +
            ", lastTime=" + lastTime +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", openId=" + openId +
            '}';
    }
}
