package com.jk51.modules.coupon.constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.jk51.modules.promotions.constants.PromotionsConstant.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@SuppressWarnings("serial")
public class CouponConstant {
    public static final Integer IS_SELECT_TRUE = 0;// 正选
    public static final Integer IS_SELECT_FALSE = 1;// 反选

    public static final String PRODUCT_LIMIT_TYPE_KEY = "limit_type";//商品限制类型key
    public static final String PRODUCT_LIMIT_TYPE_VALUE_KEY = "limit_value";//商品限制值key
    public static final int PRODUCT_LIMIT_TYPE_ALL = 1; //商品限制类型
    public static final int PRODUCT_LIMIT_TYPE_CATEGORY = 2; //商品类目
    public static final int PRODUCT_LIMIT_TYPE_GOODS = 3; //商品

    public static final int SEND_OBJ_ALL = 1;//优惠券发放对象
    public static final int SEND_TYPE_REGIST = 1;//注册后发放
    public static final int SEND_TYPE_DIRECT = 2;//直接发
    public static final int SEND_TYPE_RANGE_TIME = 3;//固定时间发放
    public static final int SEND_TYPE_AFTER_PAY = 4;//付款后发放
    public static final int SEND_TYPE_FIRST_PAY = 5;//首单发放
    public static final int SEND_CONDITION_TYPE_1 = 1;//满足多少元
    public static final int SEND_CONDITION_TYPE_2 = 2;//满足多少件
    public static final int SEND_WAY_ACCOUNT = 1;//直接发送到顾客账户
    public static final int SEND_WAY_WAIT = 2;//需用户领取
    public static final int SEND_WAY_RED_BAG = 3;//红包
    public static final int SEND_WAY_STROE = 4;//门店
    public static final int SEND_WAY_CLERK = 5;//店员
    public static final int STATUS_NORMAL = 0;//正常状态
    public static final int STATUS_STOP = 1;//正常状态

    public static final int COUPON_NOT_COPY = 0; //优惠券不可复制
    public static final int COUPON_COPY = 1; //优惠券可复制
    public static final int COUPON_NOT_SHARE = 0; //优惠券不可分享
    public static final int COUPON_SHARE = 1; //优惠券可分享
    public static final int COUPON_IS_USE = 0;//优惠券已使用
    public static final int COUPON_IS_NOT_USE = 1;//优惠券待使用

    public static final int AIM_AT_ORDER = 0; //优惠券规则正对订单
    public static final int AIM_AT_GOODS = 1; //优惠券规则正对商品

    public static final int ORDER_DEDUCT = 0;// 0订单直接减去多少
    public static final int ORDER_EACH_FULL = 1; //1每满多少减/折多少
    public static final int ORDER_FULL_MONEY = 2; //2满多少元减/折多少
    public static final int ORDER_FULL_NUM = 3;//3满多少件减/折多少
    public static final int ORDER_FULL_POST = 4;//4满了多少包邮

    public static final int GOODS_EACH_FULL = 0; //0每满多少减/折多少
    public static final int GOODS_FULL_MONEY = 1; //1满多少元减/折多少
    public static final int GOODS_FULL_NUM = 2;//2满多少件减/折多少
    public static final int GOODS_LIMIT_PRICE = 3;//3每个商品限价多少元
    public static final int GOODS_PRICE = 4;//4商品立减多少元
    public static final int GOODS_SECOND_DISCOUNT = 5;//5第几件多少折
    public static final int GOODS_DISTANCE = 6; //6满多少米减/折多少元

    public static final int CASH_COUPON = 100; //现金券
    public static final int CASH_DISCOUNT_COUPON = 200; //折扣券
    public static final int LIMIT_PRICE_COUPON = 300; //限价券
    public static final int FREE_POSTAGE_COUPON = 400; //包邮券
    public static final int GIFT_COUPON = 500;//满赠券

    /* -- 优惠券规则状态常量 -- */
    public static final int COUPON_RULE_STATUS_RELEASE = 0; // 0可发放
    public static final int COUPON_RULE_STATUS_NO_INVENTORY = 3; // 3已发完
    public static final int COUPON_RULE_STATUS_STOP = 2; // 2手动停发
    public static final int COUPON_RULE_STATUS_OVERDUE = 1; // 1已过期
    public static final int COUPON_RULE_STATUS_INVALID = 4; // 4手动作废
    public static final int COUPON_RULE_STATUS_DRAFT = 10; // 10待发放

    /* -- 时间规则状态 -- */
    public static final int TIME_RULE_STATUS_ABSOLUTE = 1; // 1绝对时间
    public static final int TIME_RULE_STATUS_RELATIVE = 2; // 2绝对时间
    public static final int TIME_RULE_STATUS_SECKILL = 4; // 4秒杀时间

    /* -- 活动状态常量 -- */
    public static final int ACTIVITY_STATUS_RELEASE = 0; // 0发布中
    public static final int ACTIVITY_STATUS_RELEASE_REGULAR = 1;// 1定时发布(未到达发布时间，定时任务使用)
    public static final int ACTIVITY_STATUS_OVERDUE = 2;// 2过期结束(根据活动定义的时间结束)
    public static final int ACTIVITY_STATUS_NO_INVENTORY = 3;// 3已发完结束(该活动下的所有优惠券已经发完)
    public static final int ACTIVITY_STATUS_STOP = 4;// 4手动结束(手动停止发布优惠券活动)
    public static final int ACTIVITY_STATUS_DRAFT = 10;// 10待发布(活动可编辑，草稿状态)

    /* -- 优惠券活动发放对象常量 -- */
    public static final int ACTIVITY_ALL_MEMBER = 1; // 全部会员
    public static final int ACTIVITY_SPECIFIED_MEMBER = 2; // 指定标签组会员
    public static final int ACTIVITY_SPECIFIED_MEMBER_LABEL = 4; // 指定标签库会员
    public static final int ACTIVITY_CUSTOM_MEMBER = 3; // 自定义会员

    /* -- 优惠券发放种类常量 -- */
    public static final int ACTIVITY_SEND_ONLY_ONE = 1;
    public static final int ACTIVITY_SEND_RANDOM_ONE = 2;
    public static final int ACTIVITY_SEND_ALL = 3;
    public static final int ACTIVITY_TURN_TABLE = 4; //转盘优惠券


    /* -- 计算方式单品还是组合 -- */
    public static final int CALCULATE_BASE_SINGLE_MEET_ALL = 1; // 单品，计算所有符合单品规则的优惠叠加
    public static final int CALCULATE_BASE_COMBINATION = 2; // 组合
    public static final int CALCULATE_BASE_SINGLE_MEET_ONE = 3; // 单品，只计算一次

    /**
     * 单品还是组合的默认配置
     */
    public static final Map<Integer, Map<Integer, Integer>> DEFAULT_CALCULATE_BASE = Collections.unmodifiableMap(new HashMap<Integer, Map<Integer, Integer>>() {{
        // 满赠券不配置

        // 现金券
        put(CASH_COUPON, Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
            put(1, CALCULATE_BASE_COMBINATION); // 满元减 组合计算
            put(2, CALCULATE_BASE_SINGLE_MEET_ONE); // 立减 单品计算
            put(0, CALCULATE_BASE_COMBINATION); // 每满元减 组合计算
        }}));

        // 折扣券
        put(CASH_DISCOUNT_COUPON, Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
            put(4, CALCULATE_BASE_COMBINATION); //立折 单品
            put(1, CALCULATE_BASE_COMBINATION); //满元折 组合计算
            put(2, CALCULATE_BASE_SINGLE_MEET_ALL); //满件折 单品
            put(5, CALCULATE_BASE_SINGLE_MEET_ALL); //第二件折  单品
        }}));

        // 限价券
        put(LIMIT_PRICE_COUPON, Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
            put(3, CALCULATE_BASE_SINGLE_MEET_ONE); //限价 单品
        }}));

        // 包邮券

    }});

    /* -- 是否是独立优惠券 -- */
    public static final int INDEPENDENT = 1; // 该优惠券独立，即使用了该优惠券就不允许参加任何活动
    public static final int DEPENDENT = 2; // 该优惠券不独立，即使用了该优惠券也可以参加其他活动

    /**
     * 定义不同类型活动的独立属性的默认状态
     */
    public static final Map<Integer, Integer> DEFAULT_INDEPENDENT = Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
        put(CASH_COUPON, DEPENDENT); // 现金券 不独立
        put(CASH_DISCOUNT_COUPON, DEPENDENT); // 折扣券 不独立
        put(LIMIT_PRICE_COUPON, INDEPENDENT); // 限价券 独立
        put(GIFT_COUPON, DEPENDENT); // 满赠券 不独立
    }});

    /**
     * 优惠券类型对应的活动类型
     */
    public static final Map<Integer, Integer> COUPON_TYPE_TO_PROMOTIONS_TYPE = Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
        put(CASH_COUPON, PROMOTIONS_RULE_TYPE_MONEY_OFF); // 现金券 不独立
        put(CASH_DISCOUNT_COUPON, PROMOTIONS_RULE_TYPE_DISCOUNT); // 折扣券 不独立
        put(LIMIT_PRICE_COUPON, PROMOTIONS_RULE_TYPE_LIMIT_PRICE); // 限价券 独立
        put(GIFT_COUPON, PROMOTIONS_RULE_TYPE_GIFT); // 满赠券 独立
    }});

    public static final Map<Integer,String> COUPON_STATUS_MAP = new HashMap<Integer,String>(){{
        put(0,"已使用");
        put(1,"可使用");
        put(2,"已过期");
    }};
//    100现金券 200打折券 300限价券 400包邮券 500满赠券
    public static final Map<Integer,String> COUPON_TYPE = new HashMap<Integer,String>(){{
        put(CASH_COUPON,"现金券");
        put(CASH_DISCOUNT_COUPON,"打折券");
        put(LIMIT_PRICE_COUPON,"限价券");
        put(FREE_POSTAGE_COUPON,"包邮券");
        put(GIFT_COUPON,"满赠券");
    }};
    //        订单类型 100自提订单 200送货上门 300门店直购 400预购订单 多选用逗号隔开
    public static final Map<String,String> COUPON_ORDER_TYPE = new HashMap<String,String>(){{
        put("100","自提订单");
        put("200","送货上门");
        put("300","门店直购（店员APP内下单）");
        put("400","预购订单");
    }};
    public static final Map<String,String> COUPON_CHANNEL = new HashMap<String,String>(){{
        put("101,103","线上使用");
        put("105","线下使用");
        put("101,103,105","线上线下均可使用");
    }};

    public static final Map<Integer,String> COUPON_FIRST_ORDER = new HashMap<Integer,String>(){{
       put(0,"全部订单");
       put(1,"仅首单");
    }};

    public static final Map<Integer,String> COUPON_TYPE_MAP_STRING = Collections.unmodifiableMap(new HashMap<Integer,String>(){{
        put(CASH_COUPON,"现金券");
        put(CASH_DISCOUNT_COUPON,"折扣券");
        put(LIMIT_PRICE_COUPON,"限价券");
        put(FREE_POSTAGE_COUPON,"包邮券");
        put(GIFT_COUPON,"满赠券");
    }});
    /**
     * 可用优惠过滤提示字段
     */
    public static final String FALL_SHORT_OF_GOODS = "无指定商品，不可用";
    public static final String FALL_SHORT_OF_NUMBER_2_GOODS = "指定商品未满%d件，不可用";
    public static final String FALL_SHORT_OF_MONEY_2_GOODS_MONEY = "指定商品金额未满%.2f元，不可用";
    public static final String RANG_OF_BUY_MAX_NUMBER_OVERSTEP = "已超过限购%d件数量，不能使用";
    public static final String RANG_OF_STOCKS_OVERSTEP = "赠品已送完，暂不能使用";
    public static final String NOT_STARTED = "优惠活动还未开始，不可用";
    public static final String ALREADY_ENDED = "优惠活动已结束，不可用";
    public static final String NOT_FIRST_ORDER = "仅限首单使用";
    public static final String NOT_APPLY_CHANNEL_ONLINE = "仅限送货上门订单使用/仅限门店自提订单使用";
    public static final String NOT_APPLY_CHANNEL = "仅限线上使用";
    public static final String NOT_APPLY_STORE = "仅限指定门店使用";
    public static final String ERROR_MESSAGE = "未满足使用条件";
    public static final String CALC_RESULT_ZERO = "计算出优惠金额为零，无法使用该券";
    public static final String FAIL_BECAUSE_PROMOTIONS = "该券不能与活动叠加使用";
    public static final String MEMBER_ERROR = "账户下没有查到该券";


    /**
     * 生成线下优惠券对应号码
     *
     * @return
     */
    public synchronized static String getCouponDownNo() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String before = df.format(new Date());
        String after = "";
        Random random = new Random();
        String[] array = {"0001", "0002", "0003", "0004", "0005", "0006", "0007", "0008", "0009",
            "0010", "0011", "0012", "0013", "0014", "0015", "0016", "0017", "0018",
            "0019", "0020", "0021", "0022", "0023", "0024", "0025", "0026", "0027",
            "0028", "0029", "0030", "0031", "0032", "0033", "0034", "0035", "0036"};
        int a1[] = new int[7];
        for (int i = 0; i < a1.length; i++) {
            //生成一个介于0到9的数字
            a1[i] = random.nextInt(8) + 1;

            for (int j = 1; j < i; j++) {
                while (a1[i] == a1[j]) {//如果重复，退回去重新生成随机数
                    if (i == 0) {
                        break;
                    }
                    i--;
                }
            }
        }
        for (int i = 0; i < a1.length; i++) {
            after += a1[i];
        }
        return before + array[random.nextInt(array.length)] + after;
    }


    public synchronized static String getCouponDownNum() {
        String str1 = String.format(" %O4d", 1);


        return null;
    }

}
