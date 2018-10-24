package com.jk51.model.coupon.requestParams;

/**
 * filename :com.jk51.model.coupon.requestParams.
 * author   :zw
 * date     :2017/3/6
 * Update   :
 */
public class BasisParams {
    private Integer siteId;
    private Integer ruleId;
    private String ruleName;
    private String markedWords;
    private Integer couponType;
    private Integer amount;
    private Integer dayNum;
    private TimeRule timeRule;
    private LimitRule limitRule;
    private String limitState;
    private String limitRemark;
    private Integer aimAt;
/*    private Timestamp startTime;
    private Timestamp endTime;*/
    private OrderRule orderRule;
    private AreaRule areaRule;
    private GoodsRule goodsRule;

    private Integer status;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getMarkedWords() {
        return markedWords;
    }

    public void setMarkedWords(String markedWords) {
        this.markedWords = markedWords;
    }

    public Integer getCouponType() {
        return couponType;
    }

    public void setCouponType(Integer couponType) {
        this.couponType = couponType;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getDayNum() {
        return dayNum;
    }

    public void setDayNum(Integer dayNum) {
        this.dayNum = dayNum;
    }

    public TimeRule getTimeRule() {
        return timeRule;
    }

    public void setTimeRule(TimeRule timeRule) {
        this.timeRule = timeRule;
    }

    public LimitRule getLimitRule() {
        return limitRule;
    }

    public void setLimitRule(LimitRule limitRule) {
        this.limitRule = limitRule;
    }

    public String getLimitState() {
        return limitState;
    }

    public void setLimitState(String limitState) {
        this.limitState = limitState;
    }

    public String getLimitRemark() {
        return limitRemark;
    }

    public void setLimitRemark(String limitRemark) {
        this.limitRemark = limitRemark;
    }

    public Integer getAimAt() {
        return aimAt;
    }

    public void setAimAt(Integer aimAt) {
        this.aimAt = aimAt;
    }

    public OrderRule getOrderRule() {
        return orderRule;
    }

    public void setOrderRule(OrderRule orderRule) {
        this.orderRule = orderRule;
    }

    public AreaRule getAreaRule() {
        return areaRule;
    }

    public void setAreaRule(AreaRule areaRule) {
        this.areaRule = areaRule;
    }

    public GoodsRule getGoodsRule() {
        return goodsRule;
    }

    public void setGoodsRule(GoodsRule goodsRule) {
        this.goodsRule = goodsRule;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BasisParams{" +
                "siteId=" + siteId +
                ", ruleId=" + ruleId +
                ", ruleName='" + ruleName + '\'' +
                ", markedWords='" + markedWords + '\'' +
                ", couponType=" + couponType +
                ", amount=" + amount +
                ", dayNum=" + dayNum +
                ", timeRule=" + timeRule +
                ", limitRule=" + limitRule +
                ", limitState='" + limitState + '\'' +
                ", limitRemark='" + limitRemark + '\'' +
                ", aimAt=" + aimAt +
                ", orderRule=" + orderRule +
                ", areaRule=" + areaRule +
                ", goodsRule=" + goodsRule +
                ", status=" + status +
                '}';
    }

//所有的rule规则
     /*

      //******************************************
        所有递归的改了种方式
        满件 满元 折扣
         ruleMap.put("meet_num", "1"); ---ruleMap.put("meet_money", "1");//满多少
         ruleMap.put("discount", "99");//折多少
         ruleMap.put("ladder", "1");// 阶梯
         ruleMap.put("meet_num", "2");
         ruleMap.put("discount", "88");
         ruleMap.put("ladder", "2");
         .
         .
         .
         满件 满元 直减
         ruleMap.put("meet_money", "1"); -----ruleMap.put("meet_num", "1")//满多少
         ruleMap.put("reduce_price", "5");//减多少
         ruleMap.put("ladder", "1");// 阶梯
         ruleMap.put("meet_money", "2");
         ruleMap.put("reduce_price", "10");
         ruleMap.put("ladder", "2");

        goods Rule_type 4 现金券和折扣券 每满多少减/折多少  新增
        ruleMap.put("direct_money", "500"); //商品直接减去多少
        //
        ruleMap.put("direct_discount", "88"); //商品直接折扣多少
        ruleMap.put("max_reduce", "5000"); //如果为0表示不封顶 否则最多减多少

        goods Rule_type 5 折扣券 第二件半价  新增
        ruleMap.put("how_piece", "2"); //表示第几件
        ruleMap.put("discount", "50"); //多少折 半价表示50
        ruleMap.put("max_buy_num", "5"); //如果为0表示不封顶 否则最多减多少

        goods Rule_type 0 只有现金券 每满多少减

        ruleMap.put("each_full_money", "1");
        ruleMap.put("reduce_price", "1000");
        ruleMap.put("max_reduce", "1000"); //如果为0表示不封顶 否则最多减多少
         //**************goods Rule_type 1 现金券  满金额
        ruleMap.put("meet_money_first", "1000");
        ruleMap.put("reduce_price_first", "100");
        ruleMap.put("meet_money_second", "2000");
        ruleMap.put("reduce_price_second", "300");
        ruleMap.put("meet_money_third", "3000");
        ruleMap.put("reduce_price_third", "500");
        ruleMap.put("meet_money_fourth", "4000");
        ruleMap.put("reduce_price_fourth", "1000");
        ruleMap.put("meet_money_fifth", "5000");
        ruleMap.put("reduce_price_fifth", "1500");
         //**************goods Rule_type 1 折扣券 满金额
       ruleMap.put("meet_money_first", "1000");
        ruleMap.put("discount_first", "99");
        ruleMap.put("meet_money_second", "2000");
        ruleMap.put("discount_second", "97");
        ruleMap.put("meet_money_third", "3000");
        ruleMap.put("discount_third", "95");
        ruleMap.put("meet_money_fourth", "4000");
        ruleMap.put("discount_fourth", "92");
        ruleMap.put("meet_money_fifth", "5000");
        ruleMap.put("discount_fifth", "90");
        //**************goods Rule_type 2  现金券 满件
        ruleMap.put("meet_num_first", "1");
        ruleMap.put("reduce_price_first", "100");
        ruleMap.put("meet_num_second", "2");
        ruleMap.put("reduce_price_second", "300");
        ruleMap.put("meet_num_third", "3");
        ruleMap.put("reduce_price_third", "500");
        ruleMap.put("meet_num_fourth", "4");
        ruleMap.put("reduce_price_fourth", "1000");
        ruleMap.put("meet_num_fifth", "5");
        ruleMap.put("reduce_price_fifth", "1500");
         //**************goods Rule_type 2 折扣券 满件
        ruleMap.put("meet_num_first", "1");
        ruleMap.put("discount_first", "99");
        ruleMap.put("meet_num_second", "2");
        ruleMap.put("discount_second", "97");
        ruleMap.put("meet_num_third", "3");
        ruleMap.put("discount_third", "95");
        ruleMap.put("meet_num_fourth", "4");
        ruleMap.put("discount_fourth", "92");
        ruleMap.put("meet_num_fifth", "5");
        ruleMap.put("discount_fifth", "90");
        //********* goods Rule_type 3 限价券
        ruleMap.put("each_goods_price", "500");
        ruleMap.put("buy_num_max", "5");
        ruleMap.put("each_goods_max_buy_num", "15");

        //******* order Rule_type 0 现金券
        ruleMap.put("direct_money", "500"); //订单直接减去多少
         //******* order Rule_type 0 折扣券
        ruleMap.put("discount_money", "88"); //订单直接折多少
        //******* order Rule_type 1 现金券;
        ruleMap.put("each_full_money", "10000");
        ruleMap.put("reduce_price", "500");
        ruleMap.put("max_reduce", "5000"); //如果为0表示不封顶 否则最多减多少
        //******* order Rule_type 2 现金券 满元
         ruleMap.put("meet_money_first", "1000");
        ruleMap.put("reduce_price_first", "100");
        ruleMap.put("meet_money_second", "2000");
        ruleMap.put("reduce_price_second", "300");
        ruleMap.put("meet_money_third", "3000");
        ruleMap.put("reduce_price_third", "500");
        ruleMap.put("meet_money_fourth", "4000");
        ruleMap.put("reduce_price_fourth", "1000");
        ruleMap.put("meet_money_fifth", "5000");
        ruleMap.put("reduce_price_fifth", "1500");
     //**************order Rule_type 2 折扣券 满元
        ruleMap.put("meet_num_first", "1");
        ruleMap.put("discount_first", "99");
        ruleMap.put("meet_num_second", "2");
        ruleMap.put("discount_second", "97");
        ruleMap.put("meet_num_third", "3");
        ruleMap.put("discount_third", "95");
        ruleMap.put("meet_num_fourth", "4");
        ruleMap.put("discount_fourth", "92");
        ruleMap.put("meet_num_fifth", "5");
        ruleMap.put("discount_fifth", "90");
        //**************order Rule_type 3  现金券 满件
        ruleMap.put("meet_num_first", "1");
        ruleMap.put("reduce_price_first", "100");
        ruleMap.put("meet_num_second", "2");
        ruleMap.put("reduce_price_second", "300");
        ruleMap.put("meet_num_third", "3");
        ruleMap.put("reduce_price_third", "500");
        ruleMap.put("meet_num_fourth", "4");
        ruleMap.put("reduce_price_fourth", "1000");
        ruleMap.put("meet_num_fifth", "5");
        ruleMap.put("reduce_price_fifth", "1500");
         //**************order Rule_type 3 折扣券 满件
        ruleMap.put("meet_num_first", "1");
        ruleMap.put("discount_first", "99");
        ruleMap.put("meet_num_second", "2");
        ruleMap.put("discount_second", "97");
        ruleMap.put("meet_num_third", "3");
        ruleMap.put("discount_third", "95");
        ruleMap.put("meet_num_fourth", "4");
        ruleMap.put("discount_fourth", "92");
        ruleMap.put("meet_num_fifth", "5");
        ruleMap.put("discount_fifth", "90");
        //*************order Rule_type 4 包邮券 只有订单
        ruleMap.put("order_full_money", "1000");
        ruleMap.put("order_full_money_post_max", "500");//为空表示没有设置上现
         ruleMap.put("order_full_num", "2");
        ruleMap.put("order_full_num_post_max", "500");//为空表示没有设置上现
        */

}
