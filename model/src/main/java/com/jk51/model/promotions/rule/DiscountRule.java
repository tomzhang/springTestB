package com.jk51.model.promotions.rule;

import java.util.List;
import java.util.Map;

/**
 * {@link com.jk51.model.promotions.PromotionsRule}打折活动的字段promotionRule对应的实体类
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/10                                <br/>
 * 修改记录:                                         <br/>
 */
public class DiscountRule {
    /**
     * 计算是单品计算还是组合计算：1代表单品计算一次符合规则，2代表组合，3代表单品计算所有符合规则叠加优惠，null表示默认
     */
    private Integer calculateBase;

    /**
     * 0不抹零 1按角抹零 2按分抹零
     */
    private Integer isMl;

    /**
     * 0四舍五入 1直接抹去
     */
    private Integer isRound;

    /**
     * 0全部商品参加 1指定商品参加 2指定商品不参加
     */
    private Integer goodsIdsType;

    /**
     * 商品id,用逗号分隔，全部商品用all表示
     */
    private String goodsIds;

    /**
     * 是否计算邮费 1不计算 2计算
     */
    private Integer isPost;

    /**
     * 1 代表 直接打折
     * 2 代表 满元打折
     * 3 代表 满件打折
     * 4 代表 第n件打折
     * 5 代表 指定商品分别打折
     */
    private Integer ruleType;

    /**
     * <h4>如果ruleType字段等于1时,rules里面的map所放的字段名和含义</h4>
     * <ol>
     *     <li>direct_discount 直接打几折 88代表88%</li>
     *     <li>goods_money_limit 商品总价封顶 0 表示不封顶</li>
     * </ol>
     * <h4>如果ruleType字段等于2时,rules里面的map所放的字段名和含义</h4>
     * <ol>
     *     <li>meet_money 满多少元</li>
     *     <li>discount 打多少折</li>
     *     <li>ladder 第几阶梯</li>
     * </ol>
     * <h4>如果ruleType字段等于3时,rules里面的map所放的字段名和含义</h4>
     * <ol>
     *     <li>meet_num 满多少件</li>
     *     <li>discount 打多少折</li>
     *     <li>ladder 第几阶梯</li>
     * </ol>
     * <h4>如果ruleType字段等于4时,rules里面的map所放的字段名和含义</h4>
     * <ol>
     *     <li>rate 每几件打折</li>
     *     <li>discount 打多少折</li>
     *     <li>goods_amount_limit 优惠商品不得超过几件 0 表示不限制</li>
     * </ol>
     * <h4>如果ruleType字段等于5时,rules里面的map所放的字段名和含义</h4>
     * <ol>
     *     <li>goodsId 商品ID</li>
     *     <li>discount 该商品打几折</li>
     *     <li>max_reduce 该商品最多优惠多少元</li>
     * </ol>
     */
    private List<Map<String, Integer>> rules;

    public Integer getIsMl() {
        return isMl;
    }

    public void setIsMl(Integer isMl) {
        this.isMl = isMl;
    }

    public Integer getIsRound() {
        return isRound;
    }

    public void setIsRound(Integer isRound) {
        this.isRound = isRound;
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

    public Integer getIsPost() {
        return isPost;
    }

    public void setIsPost(Integer isPost) {
        this.isPost = isPost;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public List<Map<String, Integer>> getRules() {
        return rules;
    }

    public void setRules(List<Map<String, Integer>> rules) {
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
        return "DiscountRule{" +
            "calculateBase=" + calculateBase +
            ", isMl=" + isMl +
            ", isRound=" + isRound +
            ", goodsIdsType=" + goodsIdsType +
            ", goodsIds='" + goodsIds + '\'' +
            ", isPost=" + isPost +
            ", ruleType=" + ruleType +
            ", rules=" + rules +
            '}';
    }
}
