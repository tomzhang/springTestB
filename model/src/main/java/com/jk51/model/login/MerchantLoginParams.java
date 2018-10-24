package com.jk51.model.login;

/**
 * filename :com.jk51.model.login.RequestParams.
 * author   :zhangduoduo
 * date     :2017/3/9
 * Update   :
 */
public class MerchantLoginParams {
    private Integer siteId;
    private String userName;
    private String password;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

