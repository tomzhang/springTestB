package com.jk51.model.concession;

/**
 * Created by ztq on 2018/1/9
 * Description: ConcessionDesc 二版
 */
public class ConcessionDesc2 {
    /**
     * 11表示优惠券（CouponRuleID）
     * 12表示优惠券（CouponDetailID）
     * 21表示活动（promotionsRuleID）
     * 22表示活动（PromotionsActivityID）
     * 23表示活动（PromotionsDetailID）
     */
    private Integer concessionType;

    /**
     * 根据{@link ConcessionDesc2#concessionType} 确定是哪种类型的id
     */
    private Integer id;

    /**
     * 标题，根据实际情况获取
     */
    private String title;

    /**
     * 规则展示名称
     */
    private String ruleView;


    /* -- setter & getter -- */

    public Integer getConcessionType() {
        return concessionType;
    }

    public void setConcessionType(Integer concessionType) {
        this.concessionType = concessionType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRuleView() {
        return ruleView;
    }

    public void setRuleView(String ruleView) {
        this.ruleView = ruleView;
    }
}
