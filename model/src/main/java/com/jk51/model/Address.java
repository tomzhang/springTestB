package com.jk51.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;

// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 驼峰命名
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Address {
    /**
     * 买家地址库
     */
    private Integer addressId;

    protected Integer siteId;

    /**
     * 买家唯一id,对应yb_member.id
     */
    private Integer buyerId;

    /**
     * 地区代码(省/市)
     */
    private Integer provinceCode;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String country;

    /**
     * 详细地址,不需要重复填写省/市/区
     */
    private String addr;

    /**
     * 地区邮政编码
     */
    private String zipCode;

    /**
     * 电话号码
     */
    //private String phone;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 收货人姓名
     */
    private String name;

    /**
     * 删除标签 0=正常 1=删除
     */
    //private Integer delTag;

    /**
     * 是否为默认地址: 0 （不是），1(是)
     */
    private Integer isDefault;

    //private Date createTime;

    //private Date updateTime;

    private String addrLng;
    private String addrLat;
    //private String addrDetail;

    public String getAddrLng() {
        return addrLng;
    }

    public void setAddrLng(String addrLng) {
        this.addrLng = addrLng;
    }

    public String getAddrLat() {
        return addrLat;
    }

    public void setAddrLat(String addrLat) {
        this.addrLat = addrLat;
    }

    /*public String getAddrDetail() {
        return addrDetail;
    }

    public void setAddrDetail(String addrDetail) {
        this.addrDetail = addrDetail;
    }*/

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /*public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }*/

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

    /*public Integer getDelTag() {
        return delTag;
    }

    public void setDelTag(Integer delTag) {
        this.delTag = delTag;
    }*/

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    /*public Date getCreateTime() {
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
    }*/
}
