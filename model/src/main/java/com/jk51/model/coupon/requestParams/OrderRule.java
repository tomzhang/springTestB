package com.jk51.model.coupon.requestParams;

/**
 * 实际情况不会在创建根据订单的优惠券，所以不推荐使用
 * <br/><br/>
 * filename :com.jk51.model.coupon.requestParams. <br/>
 * author   :zw <br/>
 * date     :2017/3/6 <br/>
 * Update   : <br/>
 */
@Deprecated
public class OrderRule {
    private Integer is_ml; //0不抹零 1按元抹零 2按角抹零
    private Integer is_round;//0不处理 1四舍五入 2直接抹去
    private Integer order_money_max; //订单金额不能超过多少元 默认-1 不处理
    private Integer goods_num_max;//每件商品最多可以购买多少件  默认-1 不处理
    private Integer rule_type; // 0订单直接减/折多少 1每满多少减/折多少 2满多少元减/折多少 3满多少件减/折多少 4满了多少包邮
    private Object rule;

    public Integer getIs_ml() {
        return is_ml;
    }

    public void setIs_ml(Integer is_ml) {
        this.is_ml = is_ml;
    }

    public Integer getIs_round() {
        return is_round;
    }

    public void setIs_round(Integer is_round) {
        this.is_round = is_round;
    }

    public Integer getOrder_money_max() {
        return order_money_max;
    }

    public void setOrder_money_max(Integer order_money_max) {
        this.order_money_max = order_money_max;
    }

    public Integer getGoods_num_max() {
        return goods_num_max;
    }

    public void setGoods_num_max(Integer goods_num_max) {
        this.goods_num_max = goods_num_max;
    }

    public Integer getRule_type() {
        return rule_type;
    }

    public void setRule_type(Integer rule_type) {
        this.rule_type = rule_type;
    }

    public Object getRule() {
        return rule;
    }

    public void setRule(Object rule) {
        this.rule = rule;
    }
}
