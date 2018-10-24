package com.jk51.model.promotions.rule;

import java.util.List;
import java.util.Map;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/11/17                                <br/>
 * 修改记录:                                         <br/>
 */
public class GroupBookingRule {
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
     * 1 代表 统一设置拼团价（金额）
     * 2 代表 根据商品分别设置拼团价（金额）
     * 3 代表 统一设置拼团价（折扣）
     * 4 代表 根据商品分别设置拼团价（折扣）
     */
    private Integer ruleType;

    /**
     * <h4>如果ruleType字段等于1时,rules里面的map所放的字段名和含义</h4>
     * <ol>
     * <li>groupPrice 商品统一拼团价</li>
     * <li>groupMemberNum 成团人数</li>
     * <li>goodsLimitNum 商品限购数量</li>
     * </ol>
     * <h4>如果ruleType字段等于2时,rules里面的map所放的字段名和含义</h4>
     * <ol>
     * <li>goodsId 商品id</li>
     * <li>groupPrice 商品对应的拼团价</li>
     * <li>groupMemberNum 商品对应的成团人数</li>
     * <li>goodsLimitNum 商品对应的限购数量</li>
     * </ol>
     * <h4>如果ruleType字段等于3时,rules里面的map所放的字段名和含义</h4>
     * <ol>
     * <li>groupDiscount 商品统一拼团折扣</li>
     * <li>groupMemberNum 成团人数</li>
     * <li>goodsLimitNum 商品限购数量</li>
     * <li>maxReduce 最多优惠几元 0 代表无限制</li>
     * </ol>
     * <h4>如果ruleType字段等于4时,rules里面的map所放的字段名和含义</h4>
     * <ol>
     * <li>goodsId 商品id</li>
     * <li>groupDiscount 商品对应的拼团折扣</li>
     * <li>groupMemberNum 商品对应的成团人数</li>
     * <li>goodsLimitNum 商品对应的限购数量</li>
     * <li>maxReduce 商品对应的最多优惠几元 0 代表无限制</li>
     * </ol>
     */
    private List<Map<String, Integer>> rules;

    /**
     * 单位：小时
     * 开团后，团队至满团员的有效期，过期团未满团员则自动取消团，付款原路返回，
     */
    private Integer groupLiveTime;

    /**
     * 0不抹零 1按角抹零 2按分抹零
     */
    private Integer isMl;

    /**
     * 0四舍五入 1直接抹去 2无意义（当isMl=0的时候使用）
     */
    private Integer isRound;

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

    public List<Map<String, Integer>> getRules() {
        return rules;
    }

    public void setRules(List<Map<String, Integer>> rules) {
        this.rules = rules;
    }

    public Integer getGroupLiveTime() {
        return groupLiveTime;
    }

    public void setGroupLiveTime(Integer groupLiveTime) {
        this.groupLiveTime = groupLiveTime;
    }

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

    public Integer getCalculateBase() {
        return calculateBase;
    }

    public void setCalculateBase(Integer calculateBase) {
        this.calculateBase = calculateBase;
    }

    @Override
    public String toString() {
        return "GroupBookingRule{" +
            "calculateBase=" + calculateBase +
            ", goodsIdsType=" + goodsIdsType +
            ", goodsIds='" + goodsIds + '\'' +
            ", ruleType=" + ruleType +
            ", rules=" + rules +
            ", groupLiveTime=" + groupLiveTime +
            ", isMl=" + isMl +
            ", isRound=" + isRound +
            '}';
    }
}
