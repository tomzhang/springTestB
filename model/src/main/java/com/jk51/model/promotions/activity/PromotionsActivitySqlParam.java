package com.jk51.model.promotions.activity;

import java.util.List;

/**
 * Created by ztq on 2018/2/3
 * Description:PromotionsActivity Sql查询参数类
 */
public class PromotionsActivitySqlParam {

    private Integer siteId;

    private List<Integer> statusList;

    private Integer promotionsActivityId;


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

    public Integer getPromotionsActivityId() {
        return promotionsActivityId;
    }

    public void setPromotionsActivityId(Integer promotionsActivityId) {
        this.promotionsActivityId = promotionsActivityId;
    }
}
