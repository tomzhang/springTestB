package com.jk51.model.promotions.rule;

import java.util.List;
import java.util.Map;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/9/12                                <br/>
 * 修改记录:                                         <br/>
 */
public class FixedPriceRule {
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
     * 限价多少元
     */
    private Integer fixedPrice;

    /**
     * 每件商品每次最多可购买的件数
     */
    private Integer buyNumEachOrder;

    /**
     * 可优惠的商品总数库存
     * 库存字段还是根据数据库查询会更容易且正确
     */
    @Deprecated
    private Integer storage;

    /**
     * 可优惠的商品历史总量
     */
    private Integer total;

    /**
     * 1 代表 所有商品限定一个价格(如果ruleType为null,视作为1来判断) 从字段fixedPrice读取限定价
     * 2 代表 每个商品都有自己的限定价格 从字段
     */
    private Integer ruleType;

    /**
     * <h4>如果ruleType字段等于1时,rules为null或空list</h4>
     * <h4>如果ruleType字段等于2时,rules里面的map所放的字段名和含义</h4>
     * <ol>
     * <li>goodsId 商品ID</li>
     * <li>fixedPrice 商品对应限定价格</li>
     * </ol>
     */
    private List<Map<String, Integer>> rules;

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

    public Integer getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(Integer fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public Integer getBuyNumEachOrder() {
        return buyNumEachOrder;
    }

    public void setBuyNumEachOrder(Integer buyNumEachOrder) {
        this.buyNumEachOrder = buyNumEachOrder;
    }

    public Integer getStorage() {
        return storage;
    }

    public void setStorage(Integer storage) {
        this.storage = storage;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
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
        return "FixedPriceRule{" +
            "calculateBase=" + calculateBase +
            ", goodsIdsType=" + goodsIdsType +
            ", goodsIds='" + goodsIds + '\'' +
            ", fixedPrice=" + fixedPrice +
            ", buyNumEachOrder=" + buyNumEachOrder +
            ", storage=" + storage +
            ", total=" + total +
            ", ruleType=" + ruleType +
            ", rules=" + rules +
            '}';
    }
}
