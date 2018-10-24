package com.jk51.modules.appInterface.util;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:App accessToken组成
 * 作者: gaojie
 * 创建日期: 2017-03-06
 * 修改记录:
 */

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AuthToken implements Serializable {
    /**
    *  用户ID
    */
    public int userId;

    /**
     * 药师ID
     */
    public int pharmacistId;

    /**
     * 门店ID
     */
    public int storeId;

    /**
     * 店员扩展表ID
     */
    public int storeUserId;

    //店员ID
    public int storeAdminId;


    public String phone;

    public int siteId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPharmacistId() {
        return pharmacistId;
    }

    public void setPharmacistId(int pharmacistId) {
        this.pharmacistId = pharmacistId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getStoreUserId() {
        return storeUserId;
    }

    public void setStoreUserId(int storeUserId) {
        this.storeUserId = storeUserId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(int storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    @Override
    public String toString() {
        return "AuthToken{" +
            "userId=" + userId +
            ", pharmacistId=" + pharmacistId +
            ", storeId=" + storeId +
            ", storeUserId=" + storeUserId +
            ", phone='" + phone + '\'' +
            ", siteId=" + siteId +
            '}';
    }
}
