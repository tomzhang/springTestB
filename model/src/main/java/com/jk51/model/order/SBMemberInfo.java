package com.jk51.model.order;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-09
 * 修改记录:
 */
public class SBMemberInfo {

    private Integer id;
    private Integer site_id;
    private Integer member_id;
    private Date birthday;
    private Integer concerned;
    private Timestamp concerned_time;
    private Integer integrate;
    private Integer integrate_used;
    private Integer checkin_num;
    private Integer checkin_sum;
    private Timestamp checkin_lasttime;
    private String weixin_code;
    private String qq;
    private String membership_number;
    private String barcode;
    private Integer country;
    private Integer province;
    private Integer city;
    private Integer area;
    private String address;
    private String tag;
    private Integer age;
    private String yibao_card;
    private Integer status;
    private Integer store_id;
    private String avatar;
    private String invite_code;
    private Timestamp create_time;
    private Timestamp update_time;
    private String storeAdminext_id;

    private Integer is_approve_avatar;//0头像未认证   1头像已认证
    private String secondToken;

    public void setSecondToken(String secondToken) {
        this.secondToken = secondToken;
    }

    public String getSecondToken() {
        return secondToken;
    }

    public SBMemberInfo() {
    }

    public Integer getIs_approve_avatar() {
        return is_approve_avatar;
    }

    public void setIs_approve_avatar(Integer is_approve_avatar) {
        this.is_approve_avatar = is_approve_avatar;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getMember_id() {
        return member_id;
    }

    public void setMember_id(Integer member_id) {
        this.member_id = member_id;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getConcerned() {
        return concerned;
    }

    public void setConcerned(Integer concerned) {
        this.concerned = concerned;
    }

    public Timestamp getConcerned_time() {
        return concerned_time;
    }

    public void setConcerned_time(Timestamp concerned_time) {
        this.concerned_time = concerned_time;
    }

    public Integer getIntegrate() {
        return integrate;
    }

    public void setIntegrate(Integer integrate) {
        this.integrate = integrate;
    }

    public Integer getIntegrate_used() {
        return integrate_used;
    }

    public void setIntegrate_used(Integer integrate_used) {
        this.integrate_used = integrate_used;
    }

    public Integer getCheckin_num() {
        return checkin_num;
    }

    public void setCheckin_num(Integer checkin_num) {
        this.checkin_num = checkin_num;
    }

    public Integer getCheckin_sum() {
        return checkin_sum;
    }

    public void setCheckin_sum(Integer checkin_sum) {
        this.checkin_sum = checkin_sum;
    }

    public Timestamp getCheckin_lasttime() {
        return checkin_lasttime;
    }

    public void setCheckin_lasttime(Timestamp checkin_lasttime) {
        this.checkin_lasttime = checkin_lasttime;
    }

    public String getWeixin_code() {
        return weixin_code;
    }

    public void setWeixin_code(String weixin_code) {
        this.weixin_code = weixin_code;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getYibao_card() {
        return yibao_card;
    }

    public void setYibao_card(String yibao_card) {
        this.yibao_card = yibao_card;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getInvite_code() {
        return invite_code;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public String getStoreAdminext_id() {
        return storeAdminext_id;
    }

    public void setStoreAdminext_id(String storeAdminext_id) {
        this.storeAdminext_id = storeAdminext_id;
    }
}
