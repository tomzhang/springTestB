package com.jk51.modules.grouppurchase.request;

/**
 * Created by mqq on 2017/11/21.
 */
public class GroupPurchaseForProActivityParam {
    private Integer siteId;

    private Integer proActivityId;

    public Integer getSiteId () {
        return siteId;
    }

    public void setSiteId (Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getProActivityId () {
        return proActivityId;
    }

    public void setProActivityId (Integer proActivityId) {
        this.proActivityId = proActivityId;
    }
}
