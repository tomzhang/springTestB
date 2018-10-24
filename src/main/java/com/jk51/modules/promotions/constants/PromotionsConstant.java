package com.jk51.modules.promotions.constants;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 该类作为所有活动的常量类，和promotions相关的常量都放在这里，请勤加注释！
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                 <br/>
 * 修改记录:                                         <br/>
 */
@SuppressWarnings({"Duplicates", "serial"})
public class PromotionsConstant {
    /* -- 活动规则状态 promotionsRule.status -- */
    public static final int PROMOTIONS_RULE_STATUS_RELEASE = 0; // 可发放
    public static final int PROMOTIONS_RULE_STATUS_OVERDUE = 1; // 已过期
    public static final int PROMOTIONS_RULE_STATUS_STOP = 2; // 手动停发
    public static final int PROMOTIONS_RULE_STATUS_NO_INVENTORY = 3; // 已发完
    public static final int PROMOTIONS_RULE_STATUS_DRAFT = 10; // 待发放

    /* -- 活动发放状态 promotionsActivity.status -- */
    public static final int PROMOTIONS_ACTIVITY_STATUS_HAS_REALEASE = 0; // 发布中(已开始)
    public static final int PROMOTIONS_ACTIVITY_STATUS_OVERDUE = 2; // 过期结束
    public static final int PROMOTIONS_ACTIVITY_STATUS_NO_INVENTORY = 3; // 发完结束
    public static final int PROMOTIONS_ACTIVITY_STATUS_STOP = 4; // 手动结束
    public static final int PROMOTIONS_ACTIVITY_STATUS_DRAFT = 10; // 待发布
    public static final int PROMOTIONS_ACTIVITY_STATUS_WILL_REALEASE = 11; // 发布中(未开始)

    /* -- 活动规则类型 promotionsRule.promotions_type -- */
    public static final int PROMOTIONS_RULE_TYPE_GIFT = 10; // 10满赠活动
    public static final int PROMOTIONS_RULE_TYPE_DISCOUNT = 20; // 20打折活动
    public static final int PROMOTIONS_RULE_TYPE_FREE_POST = 30; // 30包邮活动
    public static final int PROMOTIONS_RULE_TYPE_MONEY_OFF = 40; // 40满减活动
    public static final int PROMOTIONS_RULE_TYPE_LIMIT_PRICE = 50; // 50限价活动
    public static final int PROMOTIONS_RULE_TYPE_GROUP_BOOKING = 60; // 60拼团

    /* -- 指定商品类型 DiscountRule.goodsIdsType -- */
    public static final int DISCOUNT_RULE_GOODS_TYPE_ALL = 0; // 0全部商品参加
    public static final int DISCOUNT_RULE_GOODS_TYPE_JOIN = 1; //  1指定商品参加
    public static final int DISCOUNT_RULE_GOODS_TYPE_SIT_OUT = 2; //  2指定商品不参加

    /* -- 指定会员类型 DiscountRule.goodsIdsType -- */
    public static final int DISCOUNT_RULE_USER_TYPE_ALL = 0; // 0全部商品参加
    public static final int DISCOUNT_RULE_USER_TYPE_JOIN = 1; //  1指定商品参加
    public static final int DISCOUNT_RULE_USER_TYPE_SIT_OUT = 2; //  2指定商品不参加

    /* -- 指定会员 --*/
    public static final int MEMBER_TYPE_ALL = 0; //全部会员
    public static final int MEMBER_TYPE_TAGS_GROUP = 1; //标签组会员
    public static final int MEMBER_TYPE_SOME = 2; //指定会员
    public static final int MEMBER_TYPE_TAGS = 3; //指定标签会员

    /* -- 是否允许使用优惠券 -- */
    public static final int ALLOW_TO_USE_COUPON = 1; // 允许
    public static final int FORBID_TO_USE_COUPON = 2; // 不允许

    public static final Map<Integer, Integer> DEFAULT_TO_USE_COUPON = Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
        put(PROMOTIONS_RULE_TYPE_GIFT, ALLOW_TO_USE_COUPON); // 满赠 允许使用非同类非独立优惠券
        put(PROMOTIONS_RULE_TYPE_DISCOUNT, ALLOW_TO_USE_COUPON); // 满折 允许使用非同类非独立优惠券
        put(PROMOTIONS_RULE_TYPE_FREE_POST, ALLOW_TO_USE_COUPON); // 包邮 允许使用非同类非独立优惠券
        put(PROMOTIONS_RULE_TYPE_MONEY_OFF, ALLOW_TO_USE_COUPON); // 满减 允许使用非同类非独立优惠券
        put(PROMOTIONS_RULE_TYPE_LIMIT_PRICE, FORBID_TO_USE_COUPON); // 限价 不允许使用非同类非独立优惠券
        put(PROMOTIONS_RULE_TYPE_GROUP_BOOKING, FORBID_TO_USE_COUPON); // 拼团 不允许使用非同类非独立优惠券
    }});

    /* -- 是否是独立活动 -- */
    public static final int INDEPENDENT = 1; // 该活动独立，即不允许参加其他任何活动
    public static final int DEPENDENT = 2; // 该活动不独立，即允许参加非同类型的其他活动

    /* 是否已开始未开始 */
    public static final int READY = 11; //未开始
    public static final int ALREADY = 0; //已开始

    /**
     * 定义不同类型活动的独立属性的默认状态
     */
    public static final Map<Integer, Integer> DEFAULT_INDEPENDENT = Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
        put(PROMOTIONS_RULE_TYPE_GIFT, DEPENDENT); // 满赠 不独立
        put(PROMOTIONS_RULE_TYPE_DISCOUNT, DEPENDENT); // 满折 不独立
        put(PROMOTIONS_RULE_TYPE_FREE_POST, DEPENDENT); // 包邮 不独立
        put(PROMOTIONS_RULE_TYPE_MONEY_OFF, DEPENDENT); // 满减 不独立
        put(PROMOTIONS_RULE_TYPE_LIMIT_PRICE, INDEPENDENT); // 限价 独立
        put(PROMOTIONS_RULE_TYPE_GROUP_BOOKING, INDEPENDENT); // 拼团 独立
    }});

    /* -- 计算方式单品还是组合 -- */
    public static final int CALCULATE_BASE_SINGLE_MEET_ALL = 1; // 单品，计算所有符合单品规则的优惠叠加
    public static final int CALCULATE_BASE_COMBINATION = 2; // 组合
    public static final int CALCULATE_BASE_SINGLE_MEET_ONE = 3; // 单品，只计算一次

    /*== 错误提示（团购活动时）==*/
    public static final String IS_NOT_FIRST_ORDER = "非首单订单";
    public static final String IS_NOT_IN_VALIDITY = "不在活动有效期内";
    public static final String NONSUPPORT_ORDER_TYPE = "不支持的订单类型";
    public static final String NOT_IN_STORE = "不在指定门店或门店不存在";
    public static final String NOT_IN_JOIN_OBJECT = "不在指定参与对象范围内";
    public static final String NOT_IN_RANGE = "该商品不在活动指定范围内";

    public static final Map<Integer,List<Integer>> CALCULATE_SEQUENCE = Collections.unmodifiableMap(new HashMap<Integer,List<Integer>>(){{
        put(PROMOTIONS_RULE_TYPE_LIMIT_PRICE,new ArrayList<Integer>(){{
            add(PROMOTIONS_RULE_TYPE_GROUP_BOOKING);
        }});
        put(PROMOTIONS_RULE_TYPE_DISCOUNT, new ArrayList<Integer>(){{
            add(PROMOTIONS_RULE_TYPE_GROUP_BOOKING);
            add(PROMOTIONS_RULE_TYPE_LIMIT_PRICE);
        }});
        put(PROMOTIONS_RULE_TYPE_MONEY_OFF, new ArrayList<Integer>(){{
            add(PROMOTIONS_RULE_TYPE_GROUP_BOOKING);
            add(PROMOTIONS_RULE_TYPE_LIMIT_PRICE);
            add(PROMOTIONS_RULE_TYPE_DISCOUNT);
        }});
        put(PROMOTIONS_RULE_TYPE_GIFT, new ArrayList<Integer>(){{
            add(PROMOTIONS_RULE_TYPE_GROUP_BOOKING);
            add(PROMOTIONS_RULE_TYPE_LIMIT_PRICE);
            add(PROMOTIONS_RULE_TYPE_DISCOUNT);
            add(PROMOTIONS_RULE_TYPE_MONEY_OFF);
        }});
    }});

    public static final Map<Integer, List<Integer>> PRIORITY = Collections.unmodifiableMap(new HashMap<Integer, List<Integer>>(){{
        put(0,new ArrayList<Integer>(){{
            add(PROMOTIONS_RULE_TYPE_GROUP_BOOKING);
        }});
        put(1,new ArrayList<Integer>(){{
            add(PROMOTIONS_RULE_TYPE_GROUP_BOOKING);
            add(PROMOTIONS_RULE_TYPE_LIMIT_PRICE);
        }});
    }});
    /**
     * 单品还是组合的默认配置
     */
    public static final Map<Integer, Map<Integer, Integer>> DEFAULT_CALCULATE_BASE = Collections.unmodifiableMap(new HashMap<Integer, Map<Integer, Integer>>() {{
        // 赠品不配置

        // 打折活动配置
        put(PROMOTIONS_RULE_TYPE_DISCOUNT, Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
            put(1, CALCULATE_BASE_SINGLE_MEET_ALL); // 统一打折 单品计算
            put(5, CALCULATE_BASE_SINGLE_MEET_ALL);
            put(4, CALCULATE_BASE_SINGLE_MEET_ALL);
            put(3, CALCULATE_BASE_SINGLE_MEET_ALL);
            put(2, CALCULATE_BASE_COMBINATION);
            // ...
        }}));
        // 包邮不配置

        // 满减活动
        put(PROMOTIONS_RULE_TYPE_MONEY_OFF, Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
            put(1, CALCULATE_BASE_COMBINATION);
            put(2, CALCULATE_BASE_COMBINATION);
            put(3, CALCULATE_BASE_COMBINATION);
            // ...
        }}));

        // 限价活动
        put(PROMOTIONS_RULE_TYPE_LIMIT_PRICE, Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
            put(1, CALCULATE_BASE_SINGLE_MEET_ALL);
            put(2, CALCULATE_BASE_SINGLE_MEET_ALL);
            // ...
        }}));

        // 拼团
        put(PROMOTIONS_RULE_TYPE_GROUP_BOOKING, Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
            put(1, CALCULATE_BASE_SINGLE_MEET_ALL);
            put(2, CALCULATE_BASE_SINGLE_MEET_ALL);
            put(3, CALCULATE_BASE_SINGLE_MEET_ALL);
            put(4, CALCULATE_BASE_SINGLE_MEET_ALL);
            // ...
        }}));
    }});

    public static final DateTimeFormatter longFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

}
