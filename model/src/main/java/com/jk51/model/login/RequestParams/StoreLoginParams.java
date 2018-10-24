package com.jk51.model.login.RequestParams;

/**
 * filename :com.jk51.model.login.RequestParams.
 * author   :zw
 * date     :2017/3/15
 * Update   :
 */
public class StoreLoginParams {
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
