package com.jk51.model.coupon.requestParams;

import com.jk51.model.coupon.CouponRule;
import org.apache.catalina.LifecycleState;

import java.util.List;

/**
 * Created by ztq on 2018/1/25
 * Description: 用来进行sql条件查询
 */
public class CouponDetailSqlParam {


    /* -- Field -- */

    private Integer siteId;

    /**
     * 优惠券状态 0:已使用 1:待使用
     * 例如：
     * 订单取消，优惠券状态会由 0->1，此时的orderId无效
     */
    private Integer status;

    private Integer memberId;

    private String tradesId;

    /**
     * 注释见{@link CouponRule#status}
     */
    private List<Integer> couponRuleStatusList;


    /* -- getter & setter -- */

    public List<Integer> getCouponRuleStatusList() {
        return couponRuleStatusList;
    }

    public void setCouponRuleStatusList(List<Integer> couponRuleStatusList) {
        this.couponRuleStatusList = couponRuleStatusList;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }
}
