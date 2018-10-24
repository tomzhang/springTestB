package com.jk51.model.order;

import java.util.Date;
import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 会员信息扩展表
 * 作者: baixiongfei
 * 创建日期: 2017/2/28
 * 修改记录:
 */
public class BMemberInfo {
    private int	siteId;
    private int	id;
    private int	memberId;
    private Date birthday;
    private int	concerned;
    private Timestamp concernedTime;
    private int	integrate;
    private int	integrateUsed;
    private int	checkinNum;
    private int	checkinSum;
    private Timestamp	checkinLasttime;
    private String	weixinCode;
    private String	qq;
    private String	membershipNumber;
    private String	barcode;
    private int	country;
    private int	province;
    private int	city;
    private int	area;
    private String	address;
    private String tag;
    private int	age;
    private String	yibaoCard;
    private int	status;
    private int	storeId;
    private String	avatar;
    private String	inviteCode;
    private Timestamp createTime;
    private Timestamp updateTime;

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getConcerned() {
        return concerned;
    }

    public void setConcerned(int concerned) {
        this.concerned = concerned;
    }

    public Timestamp getConcernedTime() {
        return concernedTime;
    }

    public void setConcernedTime(Timestamp concernedTime) {
        this.concernedTime = concernedTime;
    }

    public int getIntegrate() {
        return integrate;
    }

    public void setIntegrate(int integrate) {
        this.integrate = integrate;
    }

    public int getIntegrateUsed() {
        return integrateUsed;
    }

    public void setIntegrateUsed(int integrateUsed) {
        this.integrateUsed = integrateUsed;
    }

    public int getCheckinNum() {
        return checkinNum;
    }

    public void setCheckinNum(int checkinNum) {
        this.checkinNum = checkinNum;
    }

    public int getCheckinSum() {
        return checkinSum;
    }

    public void setCheckinSum(int checkinSum) {
        this.checkinSum = checkinSum;
    }

    public Timestamp getCheckinLasttime() {
        return checkinLasttime;
    }

    public void setCheckinLasttime(Timestamp checkinLasttime) {
        this.checkinLasttime = checkinLasttime;
    }

    public String getWeixinCode() {
        return weixinCode;
    }

    public void setWeixinCode(String weixinCode) {
        this.weixinCode = weixinCode;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public void setMembershipNumber(String membershipNumber) {
        this.membershipNumber = membershipNumber;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getYibaoCard() {
        return yibaoCard;
    }

    public void setYibaoCard(String yibaoCard) {
        this.yibaoCard = yibaoCard;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
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
        return "BMemberInfo{" +
                "siteId=" + siteId +
                ", id=" + id +
                ", memberId=" + memberId +
                ", birthday=" + birthday +
                ", concerned=" + concerned +
                ", concernedTime=" + concernedTime +
                ", integrate=" + integrate +
                ", integrateUsed=" + integrateUsed +
                ", checkinNum=" + checkinNum +
                ", checkinSum=" + checkinSum +
                ", checkinLasttime=" + checkinLasttime +
                ", weixinCode='" + weixinCode + '\'' +
                ", qq='" + qq + '\'' +
                ", membershipNumber='" + membershipNumber + '\'' +
                ", barcode='" + barcode + '\'' +
                ", country=" + country +
                ", province=" + province +
                ", city=" + city +
                ", area=" + area +
                ", address='" + address + '\'' +
                ", tag='" + tag + '\'' +
                ", age=" + age +
                ", yibaoCard='" + yibaoCard + '\'' +
                ", status=" + status +
                ", storeId=" + storeId +
                ", avatar='" + avatar + '\'' +
                ", inviteCode='" + inviteCode + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

}
