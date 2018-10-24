package com.jk51.model.promotions.rule;

import java.util.List;

/**
 * {@link com.jk51.model.promotions.PromotionsRule}满赠活动的字段promotionRule对应的实体类
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/10                                <br/>
 * 修改记录:                                         <br/>
 */
public class GiftRule {
    /**
     * 满赠活动中 "1.选择购买商品及组合方式"<br/>
     * 计算时按单品计算还是组合计算：1代表单品，2代表组合
     *
     * 1 代表 购买选定商品中的【某一种】商品
     * 举例：购买A,B,D三种商品中，只有买同一种商品满3件送1件。 <br/>
     * 2 代表 购买选定商品中【任何】商品(不限品种)
     * 举例：购买A,B,D三种商品中，不管哪几种商品只要满3件送1件。 <br/>
     */
    private Integer calculateBase;

    /**
     * 商品id，以逗号分隔，配合字段calculateBase使用
     */
    private String goodsIds;

    /**
     * 赠送条件
     * 1 代表 满几件送几件
     * 2 代表 满几元送几件
     */
    private Integer ruleType;

    /**
     * 赠送规则，配合字段 ruleType 使用
     */
    private List<RuleCondition> ruleConditions;

    /**
     * 选择赠送的商品<br/>
     * 1 代表 送任意一种商品(多种赠品中任意选一种) <br/>
     * 2 代表 送同种商品(买啥送啥) <br/>
     * 3 代表 送任何不同种商品(多种赠品中随便选几种) <br/>
     * 4 代表 送固定商品(多种赠品一起送) <br/>
     */
    private Integer sendType;

    /**
     * 逗号分隔的赠送商品id集合
     */
    private List<sendGifts> sendGifts;

    public Integer getCalculateBase () {
        return calculateBase;
    }

    public void setCalculateBase (Integer calculateBase) {
        this.calculateBase = calculateBase;
    }

    public String getGoodsIds () {
        return goodsIds;
    }

    public void setGoodsIds (String goodsIds) {
        this.goodsIds = goodsIds;
    }

    public Integer getRuleType () {
        return ruleType;
    }

    public void setRuleType (Integer ruleType) {
        this.ruleType = ruleType;
    }

    public List<RuleCondition> getRuleConditions () {
        return ruleConditions;
    }

    public void setRuleConditions (List<RuleCondition> ruleConditions) {
        this.ruleConditions = ruleConditions;
    }

    public Integer getSendType () {
        return sendType;
    }

    public void setSendType (Integer sendType) {
        this.sendType = sendType;
    }

    public List<GiftRule.sendGifts> getSendGifts () {
        return sendGifts;
    }

    public void setSendGifts (List<GiftRule.sendGifts> sendGifts) {
        this.sendGifts = sendGifts;
    }

    @Override
    public String toString () {
        return "GiftRule{" +
                "calculateBase=" + calculateBase +
                ", goodsIds='" + goodsIds + '\'' +
                ", ruleType=" + ruleType +
                ", ruleConditions=" + ruleConditions +
                ", sendType=" + sendType +
                ", sendGifts=" + sendGifts +
                '}';
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class RuleCondition {
        private Integer meetNum;
        private Integer meetMoney;
        private Integer sendNum;
        private Integer ladder;

        public static RuleCondition createMeetNumCondition (Integer meetNum, Integer sendNum, Integer ladder) {
            RuleCondition ruleCondition = new RuleCondition();
            ruleCondition.setMeetNum(meetNum);
            ruleCondition.setSendNum(sendNum);
            ruleCondition.setLadder(ladder);
            return ruleCondition;
        }

        public static RuleCondition createMeetMoneyCondition (Integer meetMoney, Integer sendNum, Integer ladder) {
            RuleCondition ruleCondition = new RuleCondition();
            ruleCondition.setMeetMoney(meetMoney);
            ruleCondition.setSendNum(sendNum);
            ruleCondition.setLadder(ladder);
            return ruleCondition;
        }

        public Integer getMeetNum () {
            return meetNum;
        }

        public void setMeetNum (Integer meetNum) {
            this.meetNum = meetNum;
        }

        public Integer getMeetMoney () {
            return meetMoney;
        }

        public void setMeetMoney (Integer meetMoney) {
            this.meetMoney = meetMoney;
        }

        public Integer getSendNum () {
            return sendNum;
        }

        public void setSendNum (Integer sendNum) {
            this.sendNum = sendNum;
        }

        public Integer getLadder () {
            return ladder;
        }

        public void setLadder (Integer ladder) {
            this.ladder = ladder;
        }

        @Override
        public String toString () {
            return "RuleCondition{" +
                    "meetNum=" + meetNum +
                    ", meetMoney=" + meetMoney +
                    ", sendNum=" + sendNum +
                    ", ladder=" + ladder +
                    '}';
        }
    }

    @SuppressWarnings("unused")
    public static class sendGifts {
        private Integer giftId;

        /**
         * 实际意义是库存剩余量
         */
        private Integer sendNum;

        /**
         * 库存历史总量，用于统计
         */
        private Integer total;

        public sendGifts () {
        }

        public sendGifts (Integer giftId, Integer sendNum) {
            this.giftId = giftId;
            this.sendNum = sendNum;
        }

        public Integer getGiftId () {
            return giftId;
        }

        public void setGiftId (Integer giftId) {
            this.giftId = giftId;
        }

        public Integer getSendNum () {
            return sendNum;
        }

        public void setSendNum (Integer sendNum) {
            this.sendNum = sendNum;
        }

        public Integer getTotal () {
            return total;
        }

        public void setTotal (Integer total) {
            this.total = total;
        }

        @Override
        public String toString () {
            return "sendGifts{" +
                    "giftId=" + giftId +
                    ", sendNum=" + sendNum +
                    ", total=" + total +
                    '}';
        }
    }
}
