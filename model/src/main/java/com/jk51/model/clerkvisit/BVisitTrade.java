package com.jk51.model.clerkvisit;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BVisitTrade implements Serializable {
    private Integer id;

    private Integer siteId;

    /**
     * 回访ID
     */
    private Integer visitId;

    private Date createTime;

    private Date updateTime;

    /**
     * 订单ID
     */
    private Long tradesId;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getVisitId() {
        return visitId;
    }

    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getTradesId() {
        return tradesId;
    }

    public void setTradesId(Long tradesId) {
        this.tradesId = tradesId;
    }
}
