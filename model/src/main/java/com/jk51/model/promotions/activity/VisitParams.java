package com.jk51.model.promotions.activity;

/**
 * Created by Administrator on 2018/3/15.
 */
public class VisitParams {

    private  String goodIds;

    private  String userIds;

    private  Integer siteId;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public String getGoodIds() {

        return goodIds;
    }

    public void setGoodIds(String goodIds) {
        this.goodIds = goodIds;
    }

    @Override
    public String toString() {
        return "VisitParams{" +
            "goodIds='" + goodIds + '\'' +
            ", userIds='" + userIds + '\'' +
            '}';
    }
}
