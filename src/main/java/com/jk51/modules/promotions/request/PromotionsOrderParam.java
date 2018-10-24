package com.jk51.modules.promotions.request;

import java.util.List;

/**
 * filename :com.jk51.modules.promotions.request.
 * author   :zw
 * date     :2017/8/17
 * Update   :
 */
public class PromotionsOrderParam {
    private Integer siteId;
    private String orderId;
    private Integer userId;
    private List<PromotionsOrderRule> promotionsOrders;
    public String getOrderId() {
        return orderId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<PromotionsOrderRule> getPromotionsOrders() {
        return promotionsOrders;
    }

    public void setPromotionsOrders(List<PromotionsOrderRule> promotionsOrders) {
        this.promotionsOrders = promotionsOrders;
    }
}
