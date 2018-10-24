package com.jk51.model.role;

import org.w3c.dom.Text;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/3-03-03
 * 修改记录 :
 */
public class VipMember {
    private Integer   siteId;
    private Integer   memberId;
    private String    buyerNick;
    private Timestamp createTime;
    private Timestamp lastTime;
    private Integer  orderNum;
    private Integer  orderFee;
    private String  store_name;
    private  String  inviteCode;
    private  String  name; //用户姓名
    private  String  mobile; //用户手机
    private  Integer sex;    //用户性别
    private  String  idcard_number;  //用户身份证号码
    private  Date    bitthday;  //用户生日
    private  String  email;  //用户邮箱
    private  String  address;   //用户地址
    private  String  qq;  //用户qq
    private  String  membership_number;  //会员卡号
    private  String  country;   //国家
    private  String  province;   //省
    private  String  city;   //市
    private  String  area;  //区
    private  String  barcode; //会员条形码
    private  String  memo;   //备注
    private  String   tag;  //会员标签

    private  BigInteger integrate; //会员积分
    private  Integer status;//会员状态
    private  String adminName;

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
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

    public String getBuyerNick() {
        return buyerNick;
    }

    public void setBuyerNick(String buyerNick) {
        this.buyerNick = buyerNick;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getLastTime() {
        return lastTime;
    }

    public void setLastTime(Timestamp lastTime) {
        this.lastTime = lastTime;
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

    public String getStores_number() {
        return store_name;
    }

    public void setStores_number(String store_name) {
        this.store_name = store_name;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getIdcard_number() {
        return idcard_number;
    }

    public void setIdcard_number(String idcard_number) {
        this.idcard_number = idcard_number;
    }

    public Date getBitthday() {
        return bitthday;
    }

    public void setBitthday(Date bitthday) {
        this.bitthday = bitthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMembership_number() {
        return membership_number;
    }

    public void setMembership_number(String membership_number) {
        this.membership_number = membership_number;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public BigInteger getIntegrate() {
        return integrate;
    }

    public void setIntegrate(BigInteger integrate) {
        this.integrate = integrate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
