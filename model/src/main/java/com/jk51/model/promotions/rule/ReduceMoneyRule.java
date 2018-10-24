package com.jk51.model.promotions.rule;

import java.util.List;

/**
 * 满减规则
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/9/12                                <br/>
 * 修改记录:                                         <br/>
 */
public class ReduceMoneyRule {
    /**
     * 计算是单品计算还是组合计算：1代表单品计算一次符合规则，2代表组合，3代表单品计算所有符合规则叠加优惠，null表示默认
     */
    private Integer calculateBase;

    /**
     * 0全部商品参加 1指定商品参加 2指定商品不参加
     */
    private Integer goodsIdsType;

    /**
     * 商品id,用逗号分隔，全部商品用all表示
     */
    private String goodsIds;

    /**
     * 1 代表 立减多少元（即无规则减少订单价格）
     * 2 代表 每满多少元减多少元，可设置封顶
     * 3 代表 满多少元减多少元（多少级阶梯可自定义）
     */
    private Integer ruleType;

    private List<InnerRule> rules;

    public static class InnerRule {
        /**
         * 优惠金额点
         */
        private Integer meetMoney;

        /**
         * 优惠的金额
         */
        private Integer reduceMoney;

        /**
         * 封顶，0代表不封顶
         */
        private int cap;

        /**
         * 阶梯
         */
        private Integer ladder;

        public InnerRule() {
        }

        public InnerRule(Integer meetMoney, Integer reduceMoney, int cap, Integer ladder) {
            this.meetMoney = meetMoney;
            this.reduceMoney = reduceMoney;
            this.cap = cap;
            this.ladder = ladder;
        }

        public Integer getMeetMoney() {
            return meetMoney;
        }

        public void setMeetMoney(Integer meetMoney) {
            this.meetMoney = meetMoney;
        }

        public Integer getReduceMoney() {
            return reduceMoney;
        }

        public void setReduceMoney(Integer reduceMoney) {
            this.reduceMoney = reduceMoney;
        }

        public int getCap() {
            return cap;
        }

        public void setCap(int cap) {
            this.cap = cap;
        }

        public Integer getLadder() {
            return ladder;
        }

        public void setLadder(Integer ladder) {
            this.ladder = ladder;
        }

        @Override
        public String toString() {
            return "InnerRule{" +
                "meetMoney=" + meetMoney +
                ", reduceMoney=" + reduceMoney +
                ", cap=" + cap +
                ", ladder=" + ladder +
                '}';
        }
    }

    public Integer getGoodsIdsType() {
        return goodsIdsType;
    }

    public void setGoodsIdsType(Integer goodsIdsType) {
        this.goodsIdsType = goodsIdsType;
    }

    public String getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public List<InnerRule> getRules() {
        return rules;
    }

    public void setRules(List<InnerRule> rules) {
        this.rules = rules;
    }

    public Integer getCalculateBase() {
        return calculateBase;
    }

    public void setCalculateBase(Integer calculateBase) {
        this.calculateBase = calculateBase;
    }

    @Override
    public String toString() {
        return "ReduceMoneyRule{" +
            "calculateBase=" + calculateBase +
            ", goodsIdsType=" + goodsIdsType +
            ", goodsIds='" + goodsIds + '\'' +
            ", ruleType=" + ruleType +
            ", rules=" + rules +
            '}';
    }
}
