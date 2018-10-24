package com.jk51.model.promotions;

/**
 * 活动规则和发放活动规则, 只用于做参数，不是实体类
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/10/30                                <br/>
 * 修改记录:                                         <br/>
 */
public class RuleAndActivityParams {
    private PromotionsRule promotionsRule;
    private PromotionsActivity promotionsActivity;
    private Integer siteId;

    public PromotionsRule getPromotionsRule() {
        return promotionsRule;
    }

    public void setPromotionsRule(PromotionsRule promotionsRule) {
        this.promotionsRule = promotionsRule;
    }

    public PromotionsActivity getPromotionsActivity() {
        return promotionsActivity;
    }

    public void setPromotionsActivity(PromotionsActivity promotionsActivity) {
        this.promotionsActivity = promotionsActivity;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public String toString() {
        return "RuleAndActivityParams{" +
            "promotionsRule=" + promotionsRule +
            ", promotionsActivity=" + promotionsActivity +
            ", siteId=" + siteId +
            '}';
    }
}
