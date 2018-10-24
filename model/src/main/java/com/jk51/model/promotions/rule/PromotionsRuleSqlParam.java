package com.jk51.model.promotions.rule;

import java.util.List;

/**
 * Created by ztq on 2018/2/3
 * Description:活动规则sql参数
 */
public class PromotionsRuleSqlParam {

    private Integer siteId;

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
