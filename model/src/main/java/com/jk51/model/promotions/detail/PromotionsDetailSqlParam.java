package com.jk51.model.promotions.detail;

/**
 * Created by ztq on 2018/1/27
 * Description: 用来进行sql条件查询
 */
public class PromotionsDetailSqlParam {

    /* -- Field -- */
    private Integer siteId;

    private Long tradesId;

    /**
     * 状态0:已使用 1:以退款
     */
    private Integer status;

    /* -- setter & getter -- */

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Long getTradesId() {
        return tradesId;
    }

    public void setTradesId(Long tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
