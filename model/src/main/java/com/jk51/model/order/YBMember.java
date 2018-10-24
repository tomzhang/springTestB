package com.jk51.model.order;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:总部后台的匿名用户信息
 * 作者: baixiongfei
 * 创建日期: 2017/2/28
 * 修改记录:
 */
public class YBMember {

    private int	memberId;
    private String buyerNick;
    private String mobile;
    private String passwd;
    private Timestamp lastLogin;
    private String name;
    private int	sex;
    private String idcardNumber;
    private int	reginSource;
    private int	area;
    private String bUsersarr;
    private String lastIpaddr;
    private Date birthday;
    private int	registerStores;
    private Timestamp createTime;
    private Timestamp updateTime;
    private int	integrate;
    private int	isActivated;

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
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

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getIdcardNumber() {
        return idcardNumber;
    }

    public void setIdcardNumber(String idcardNumber) {
        this.idcardNumber = idcardNumber;
    }

    public int getReginSource() {
        return reginSource;
    }

    public void setReginSource(int reginSource) {
        this.reginSource = reginSource;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getBUsersarr() {
        return bUsersarr;
    }

    public void setBUsersarr(String bUsersarr) {
        this.bUsersarr = bUsersarr;
    }

    public String getLastIpaddr() {
        return lastIpaddr;
    }

    public void setLastIpaddr(String lastIpaddr) {
        this.lastIpaddr = lastIpaddr;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getRegisterStores() {
        return registerStores;
    }

    public void setRegisterStores(int registerStores) {
        this.registerStores = registerStores;
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

    public int getIntegrate() {
        return integrate;
    }

    public void setIntegrate(int integrate) {
        this.integrate = integrate;
    }

    public int getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(int isActivated) {
        this.isActivated = isActivated;
    }

    @Override
    public String toString() {
        return "YBMember{" +
                "memberId=" + memberId +
                ", buyerNick='" + buyerNick + '\'' +
                ", mobile='" + mobile + '\'' +
                ", passwd='" + passwd + '\'' +
                ", lastLogin=" + lastLogin +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", idcardNumber='" + idcardNumber + '\'' +
                ", reginSource=" + reginSource +
                ", area=" + area +
                ", bUsersarr='" + bUsersarr + '\'' +
                ", lastIpaddr='" + lastIpaddr + '\'' +
                ", birthday=" + birthday +
                ", registerStores=" + registerStores +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", integrate=" + integrate +
                ", isActivated=" + isActivated +
                '}';
    }
}
