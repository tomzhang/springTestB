package com.jk51.model.coupon.rule;

import java.util.List;

/**
 * Created by ztq on 2018/2/3
 * Description: 专门用来查询优惠券规则的参数类
 */
public class CouponRuleSqlParam {

    private Integer siteId;

    /**
     * 状态列表
     */
    private List<Integer> statusList;


    /* -- setter & getter -- */

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }
}
