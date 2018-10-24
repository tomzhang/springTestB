package com.jk51.model;

import java.sql.Timestamp;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/8-03-08
 * 修改记录 :
 */
public class SVipMember {
    private Integer siteId;
    private Integer memberId;
    private Timestamp createTime;
    private Timestamp lastTime;
    private Integer orderNum;
    private Integer orderFee;
    private String inviteCode;
    private Integer source;
    private String name; //用户姓名
    private String mobile; //用户手机
    private String integrate; //会员积分
    private String storeName;
    private String adminName;

    //成功订单和金额有改动，为了不冲突，新建了变量
    private Integer orderNum1;
    private Integer orderFee1;

    private String avatar;
    private String register_stores;

    public String getRegister_stores() {
        return register_stores;
    }

    public void setRegister_stores(String register_stores) {
        this.register_stores = register_stores;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getIntegrate() {
        return integrate;
    }

    public void setIntegrate(String integrate) {
        this.integrate = integrate;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Integer getOrderNum1() {
        return orderNum1;
    }

    public void setOrderNum1(Integer orderNum1) {
        this.orderNum1 = orderNum1;
    }

    public Integer getOrderFee1() {
        return orderFee1;
    }

    public void setOrderFee1(Integer orderFee1) {
        this.orderFee1 = orderFee1;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "VipMember{" +
            "siteId=" + siteId +
            ", memberId=" + memberId +
            ", createTime=" + createTime +
            ", lastTime=" + lastTime +
            ", orderNum=" + orderNum +
            ", orderFee=" + orderFee +
            ", inviteCode='" + inviteCode + '\'' +
            ", name='" + name + '\'' +
            ", mobile='" + mobile + '\'' +
            ", integrate='" + integrate + '\'' +
            ", storeName='" + storeName + '\'' +
            ", adminName='" + adminName + '\'' +
            ", orderNum1='" + orderNum1 + '\'' +
            ", orderFee1='" + orderFee1 + '\'' +
            '}';
    }
}
