package com.jk51.model.coupon.requestParams;

/**
 * Created by Administrator on 2017/7/31.
 */
public class ReissureActivityParams {
    private Integer siteId;

    private Integer userID; //登录用户ID

    private String userName; //登录用户名

    private String vipMembers; //会员ID

    private Integer activeId;  // 活动ID

    private Integer ruleId;  //规则ID

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getActiveId() {
        return activeId;
    }

    public void setActiveId(Integer activeId) {
        this.activeId = activeId;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVipMembers() {
        return vipMembers;
    }

    public void setVipMembers(String vipMembers) {
        this.vipMembers = vipMembers;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }
}
