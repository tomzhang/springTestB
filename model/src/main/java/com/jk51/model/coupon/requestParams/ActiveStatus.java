package com.jk51.model.coupon.requestParams;

/**
 * Created by Administrator on 2017/7/12.
 */
public class ActiveStatus {

    private Integer siteId;

    private Integer activeId;

    private Integer preStatus;

    private Integer toUpdateStatus;

    public Integer getSiteId() {
        return siteId;
    }

    public void setToUpdateStatus(Integer toUpdateStatus) {
        this.toUpdateStatus = toUpdateStatus;
    }

    public Integer getToUpdateStatus() {
        return toUpdateStatus;
    }

    public void setPreStatus(Integer preStatus) {
        this.preStatus = preStatus;
    }


    public Integer getPreStatus() {
        return preStatus;
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
}
