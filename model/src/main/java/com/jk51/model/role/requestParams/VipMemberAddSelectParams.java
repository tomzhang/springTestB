package com.jk51.model.role.requestParams;

import java.math.BigInteger;
import java.util.Date;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/6-03-06
 * 修改记录 :
 */
public class VipMemberAddSelectParams {

    private Integer memberId;
    private Integer site_id;
    private  String  name; //用户姓名
    private  String  mobile; //用户手机
    private  Integer sex;    //用户性别
    private  String  idcard_number;  //用户身份证号码
    private  Date    birthday;  //用户生日
    private  String  email;  //用户邮箱
    private  String  address;   //用户地址
    private  String  qq;  //用户qq
    private  String  membership_number;  //会员卡号
    private  Integer  country;   //国家
    private  Integer  province;   //省
    private  Integer  city;   //市
    private  Integer  area;  //区
    private  String  barcode; //会员条形码
    private  String  memo;   //备注
    private  String    tag;  //会员标签
    private  BigInteger integrate; //会员积分
    private  Integer status;//会员状态

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
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
