package com.jk51.modules.promotions.request;

import com.jk51.model.order.Page;
import com.jk51.model.promotions.activity.ShowRule;

/**
 * Created by mqq on 2017/8/10.
 */
public class ProActivityDtoForPage extends Page {
    private Integer siteId;

    private String proActivityName;

    private Integer proActivitySendWay;

    private Integer status;

    private String startTime;

    private String endTime;

    private ShowRule showRule;

    private Integer pcShow;

    private  Integer promotionsRuleType;

    public Integer getPromotionsRuleType() {
        return promotionsRuleType;
    }

    public void setPromotionsRuleType(Integer promotionsRuleType) {
        this.promotionsRuleType = promotionsRuleType;
    }

    private Integer ruleId;

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public ShowRule getShowRule() {
        return showRule;
    }

    public void setShowRule(ShowRule showRule) {
        this.showRule = showRule;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getProActivitySendWay() {
        return proActivitySendWay;
    }

    public void setProActivitySendWay(Integer proActivitySendWay) {
        this.proActivitySendWay = proActivitySendWay;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getProActivityName() {
        return proActivityName;
    }

    public void setProActivityName(String proActivityName) {
        this.proActivityName = proActivityName;
    }

    public Integer getPcShow() {
        return pcShow;
    }

    public void setPcShow(Integer pcShow) {
        this.pcShow = pcShow;
    }
}
