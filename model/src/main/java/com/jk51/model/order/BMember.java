package com.jk51.model.order;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:商家会员信息
 * 作者: baixiongfei
 * 创建日期: 2017/2/24
 * 修改记录:
 */
public class BMember {
    private int	siteId;
    private int	memberId;
    private int	buyerId;
    private String	buyerNick;
    private String	mobile;
    private String	passwd;
    private Integer	sex;
    private String	email;
    private String	idcardNumber;
    private int	orderNum;
    private int	orderFee;
    private String	memo;
    private int	registerStores;
    private String	registerClerks;
    private String	name;
    private String	openId;
    private String	lastIpaddr;
    private BigInteger integrate;
    private BigInteger totalGetIntegrate;
    private BigInteger totalConsumeIntegrate;
    private int	memSource;
    private int	isActivated;
    private int	banStatus;
    private Timestamp lastTime;
    private Timestamp createTime;
    private Timestamp updateTime;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
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

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public int getOrderFee() {
        return orderFee;
    }

    public void setOrderFee(int orderFee) {
        this.orderFee = orderFee;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getRegisterStores() {
        return registerStores;
    }

    public void setRegisterStores(int registerStores) {
        this.registerStores = registerStores;
    }

    public String getRegisterClerks() {
        return registerClerks;
    }

    public void setRegisterClerks(String registerClerks) {
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

    public int getMemSource() {
        return memSource;
    }

    public void setMemSource(int memSource) {
        this.memSource = memSource;
    }

    public int getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(int isActivated) {
        this.isActivated = isActivated;
    }

    public int getBanStatus() {
        return banStatus;
    }

    public void setBanStatus(int banStatus) {
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
        return "BMember{" +
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
            ", registerClerks='" + registerClerks + '\'' +
            ", name='" + name + '\'' +
            ", openId='" + openId + '\'' +
            ", lastIpaddr='" + lastIpaddr + '\'' +
            ", integrate=" + integrate +
            ", totalGetIntegrate=" + totalGetIntegrate +
            ", totalConsumeIntegrate=" + totalConsumeIntegrate +
            ", memSource=" + memSource +
            ", isActivated=" + isActivated +
            ", banStatus=" + banStatus +
            ", lastTime=" + lastTime +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            '}';
    }
}
