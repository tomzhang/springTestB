package com.jk51.model.coupon;

/**
 * Created by Administrator on 2017/8/17.
 */

/**
 * 根据活动Id查询目标商品下是否有赠品
 */
public class ErpParams {
    private Integer siteId; //门店ID

    private String couponNo;

    private Integer status;

    @Override
    public String toString() {
        return "ErpParams{" +
            "siteId=" + siteId +
            ", couponNo='" + couponNo + '\'' +
            ", status=" + status +
            '}';
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCouponNo() {

        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public Integer getSiteId() {

        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
