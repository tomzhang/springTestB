package com.jk51.modules.promotions.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.jk51.exception.UnknownTypeException;
import com.jk51.model.concession.ConcessionDecorator;
import com.jk51.model.concession.GiftMsg;
import com.jk51.model.concession.result.GiftResult;
import com.jk51.model.order.Orders;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.*;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.mapper.GoodsmMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.promotions.constants.PromotionsConstant;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jk51.modules.coupon.constants.CouponConstant.CALCULATE_BASE_SINGLE_MEET_ONE;
import static com.jk51.modules.promotions.constants.PromotionsConstant.*;
import static java.util.stream.Collectors.toList;

/**
 * Created by ztq on 2017/12/25
 * Description:
 */
@SuppressWarnings("Duplicates")
public class PromotionsDecorator implements ConcessionDecorator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /* -- spring beans -- */
    private GoodsMapper goodsMapper;
    private GoodsmMapper goodsmMapper;
    private OrdersMapper ordersMapper;
    private PromotionsRuleService promotionsRuleService;
    private PromotionsConditionChecker promotionsConditionChecker;
    private OrderDeductionUtils orderDeductionUtils;

    /* -- Filed -- */
    private PromotionsRule promotionsRule;
    private PromotionsActivity promotionsActivity;
    private final Param param;
    private final Result result = new Result();

    /**
     * 不要随便添加构造方法，important!!!
     *
     * @param promotionsRule
     * @param promotionsActivity
     * @param param
     * @param sc
     */
    public PromotionsDecorator(PromotionsRule promotionsRule, PromotionsActivity promotionsActivity, Param param, ServletContext sc) {
        this.promotionsRule = promotionsRule;
        this.promotionsActivity = promotionsActivity;
        this.param = param;
        getBeans(sc);
    }

    private void getBeans(ServletContext sc) {
        BeanFactory beanFactory = WebApplicationContextUtils.getWebApplicationContext(sc);
        promotionsConditionChecker = beanFactory.getBean(PromotionsConditionChecker.class);
        goodsMapper = beanFactory.getBean(GoodsMapper.class);
        goodsmMapper = beanFactory.getBean(GoodsmMapper.class);
        ordersMapper = beanFactory.getBean(OrdersMapper.class);
        promotionsRuleService = beanFactory.getBean(PromotionsRuleService.class);
        orderDeductionUtils = beanFactory.getBean(OrderDeductionUtils.class);
    }

    @Override
    public boolean filter() {
        checkNotNull();
        // 通用过滤 除商品，规则，独立设置，使用限制以外的规则过滤
        if (!commonFilter()) {
            return false;
        }
        // 根据类型过滤
        return filterWithRule();
    }

    @Override
    public void concession() {
        checkNotNull();
        //一组商品，一种活动
        //过滤出那些商品参加了这个活动，然后计算优惠
        //过滤原则：（限价独立）-》 打折-》满减-》满赠-》包邮
        //计算优惠需要注意是单品还是组合
        //需要每个商品的优惠金额  按照比例算出
        try {
            if (promotionsRule == null) return;
            List<PromotionsDecorator.GoodsData> goodsData = param.getGoodsData();
            if (goodsData.isEmpty()) {
                result.setResultMark(3);
            } else {
                switch (promotionsRule.getPromotionsType()) {
                    // 满赠活动
                    case PromotionsConstant.PROMOTIONS_RULE_TYPE_GIFT:
                        calculateGoodWhetherItMeetsGiftPromotions(promotionsRule, goodsData);
                        break;

                    // 打折活动
                    case PromotionsConstant.PROMOTIONS_RULE_TYPE_DISCOUNT:
                        calculateGoodWhetherItMeetsDiscountPromotions(promotionsRule, goodsData);
                        break;

                    // 包邮活动
                    case PromotionsConstant.PROMOTIONS_RULE_TYPE_FREE_POST:
                        calculateGoodWhetherItMeetsPostPromotions(promotionsRule, goodsData, param);
                        break;

                    // 满减活动
                    case PromotionsConstant.PROMOTIONS_RULE_TYPE_MONEY_OFF:
                        calculateGoodWhetherItMeetsFullReducePromotions(promotionsRule, goodsData);
                        break;

                    // 限价活动
                    case PromotionsConstant.PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                        calculateGoodWhetherItMeetsLimitedPricePromotions(promotionsRule, goodsData,param.getMemberId());
                        break;

                    // 拼团活动
                    case PromotionsConstant.PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                        calculateGoodWhetherItMeetsGroupBookingPromotions(promotionsRule, goodsData);
                        break;
                    default:
                        throw new UnknownTypeException("活动类型不明");
                }
            }


            if (goodsData.size() == 0)
                result.setResultMark(3);

            result.setGoodsData(goodsData);
        } catch (Exception e) {
            logger.error("活动计算出现异常,{}", e);
        }

    }

    /**
     * 通用的过滤
     *
     * @return
     */
    public boolean commonFilter() {
        //4是否首单的过滤
        if (promotionsRule.getIsFirstOrder() == 1) {
            if (!promotionsConditionChecker.checkFirstOrder(param)) {
                this.result.setErrorMsg(IS_NOT_FIRST_ORDER);
                return false;
            }
        }
        //1有效期的过滤
        if (!promotionsConditionChecker.checkPeriodOfValidity(this)) {
            this.result.setErrorMsg(IS_NOT_IN_VALIDITY);
            return false;
        }

        //3订单类型的过滤
        if (!promotionsConditionChecker.checkOrderType(param, this)) {
            this.result.setErrorMsg(NONSUPPORT_ORDER_TYPE);
            return false;
        }

        //2适用门店的过滤
        if (!promotionsConditionChecker.checkStore(param, this)) {
            this.result.setErrorMsg(NOT_IN_STORE);
            return false;
        }

        //参与对象的过滤
        if (!promotionsConditionChecker.checkJoinObject(this, param)) {
            this.result.setErrorMsg(NOT_IN_JOIN_OBJECT);
            return false;
        }
        return true;
    }

    public boolean filterWithRule() {
        return promotionsConditionChecker.checkPromotionsAvailableOrNot(this, param);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class Param {

        /* -- Fields -- */
        private Integer siteId;
        private Integer memberId;

        /**
         * 订单商品信息
         */
        private List<GoodsData> goodsData;

        /**
         * 订单运费
         */
        private Integer freight;

        /**
         * 下单时间
         */
        private LocalDateTime orderTime;

        /**
         * 100自提订单 200送货上门 300门店直购
         */
        private String orderType;

        /**
         * 门店ID，当storeId为0时，代表无法分配门店，分配到总店手动分配
         */
        private Integer storeId;

        /**
         * 收货人市区id
         */
        private String receiverCityCode;

        /**
         * 100门店后台 101门店助手 102Pc站 103微信商城 104支付宝 105线下
         */
        private String applyChannel;


        /* -- setter and getter -- */

        public Integer getSiteId() {
            return siteId;
        }

        public void setSiteId(Integer siteId) {
            this.siteId = siteId;
        }

        public Integer getMemberId() {
            return memberId;
        }

        public void setMemberId(Integer memberId) {
            this.memberId = memberId;
        }

        public List<GoodsData> getGoodsData() {
            return goodsData;
        }

        public void setGoodsData(List<GoodsData> goodsData) {
            this.goodsData = goodsData;
        }

        public Integer getFreight() {
            return freight;
        }

        public void setFreight(Integer freight) {
            this.freight = freight;
        }

        public LocalDateTime getOrderTime() {
            return orderTime;
        }

        public void setOrderTime(LocalDateTime orderTime) {
            this.orderTime = orderTime;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public Integer getStoreId() {
            return storeId;
        }

        public void setStoreId(Integer storeId) {
            this.storeId = storeId;
        }

        public String getReceiverCityCode() {
            return receiverCityCode;
        }

        public void setReceiverCityCode(String receiverCityCode) {
            this.receiverCityCode = receiverCityCode;
        }

        public String getApplyChannel() {
            return applyChannel;
        }

        public void setApplyChannel(String applyChannel) {
            this.applyChannel = applyChannel;
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public class Result {
        /**
         * 结果标记，1代表优惠金额，2表示赠送礼品, 3计算不出优惠
         */
        private Integer resultMark = 3;

        /**
         * 过滤false原因或者是计算不出优惠的原因
         */
        private String errorMsg;

        /**
         * 优惠了多少金额
         */
        private Integer discount = 0;

        /**
         * 商品信息
         */
        private List<GoodsData> goodsData;

        private GiftResult giftResult = new GiftResult();


        /* -- setter & getter -- */

        public Integer getResultMark() {
            return resultMark;
        }

        public void setResultMark(Integer resultMark) {
            this.resultMark = resultMark;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public Integer getDiscount() {
            return discount;
        }

        public void setDiscount(Integer discount) {
            this.discount = discount;
        }

        public List<GoodsData> getGoodsData() {
            return goodsData;
        }

        public void setGoodsData(List<GoodsData> goodsData) {
            this.goodsData = goodsData;
        }

        public GiftResult getGiftResult() {
            return giftResult;
        }

        public void setGiftResult(GiftResult giftResult) {
            this.giftResult = giftResult;
        }
    }

    /**
     * 原始数据
     */
    public static class GoodsData {
        private Integer goodsId;
        private Integer num;

        /**
         * 商店价格，单位为分
         */
        private Integer shopPrice;

        /**
         * 该类商品优惠了多少金额，以分为单位
         */
        private Integer discount = 0;

        private Boolean usePromotions;

        public Boolean getUsePromotions() {
            return usePromotions;
        }

        public void setUsePromotions(Boolean usePromotions) {
            this.usePromotions = usePromotions;
        }

        public Integer getDiscount() {

            return discount;
        }

        public void setDiscount(Integer discount) {
            this.discount = discount;
        }

        public Integer getShopPrice() {

            return shopPrice;
        }

        public void setShopPrice(Integer shopPrice) {
            this.shopPrice = shopPrice;
        }

        public Integer getNum() {

            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        public Integer getGoodsId() {

            return goodsId;
        }

        public void setGoodsId(Integer goodsId) {
            this.goodsId = goodsId;
        }
    }

    //计算满赠活动优惠情况
    private void calculateGoodWhetherItMeetsGiftPromotions(PromotionsRule promotionsRule, List<GoodsData> goodsData) {

        /*
          计算满赠活动的原则：
          1 区分单品和组合的方式
          单品就是活动指定的某一种商品满件或者满元，组合就是活动指定的多种商品相加满件或者满元
          2 满赠活动计算时 计算满元时需要商品原价减去已优惠金额discount,因为满赠活动计算优先级较低 可能存在
          商品已经经历过满折或者满减活动的计算，这时不可直接拿商品原价计算
          3 满赠活动如果存在并满足，设置ResultMark为2  表示是送赠品
          4 满赠活动现在需要赠品信息(具体送几件)
          5 送的赠品需要和库存判断，取较小的那个
         */

        try {
            GiftRule giftRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GiftRule.class);
            switch (giftRule.getSendType()) {
                //买啥送啥
                case 2:
                    calculateBuySomeOneSendSomeOnePromotionsConcession(promotionsRule, giftRule, goodsData);
                    break;
                //多种赠品随便送
                case 3:
                    calculateRandomSendPromotionsConcession(promotionsRule, giftRule, goodsData);
                    break;
                default:
                    throw new RuntimeException("未知的sendType类型");
            }
        } catch (Exception e) {
            throw new RuntimeException("满赠活动计算异常");
        }

    }

    //计算打折活动优惠情况
    private void calculateGoodWhetherItMeetsDiscountPromotions(PromotionsRule promotionsRule, List<GoodsData> goodsData) {
        /*
          打折活动计算原则
          1 区分单品还是组合 如果字段为空则为默认值
          2 这里的商品已经排除了独立活动的计算，而且打折活动计算时最优先级
          3 打折类型 1直接打折/分别打折  2满多少元打折  3 满多少件打折   4 第几件打折
         */
        try {
            DiscountRule discountRule = JSON.parseObject(promotionsRule.getPromotionsRule(), DiscountRule.class);
            switch (discountRule.getRuleType()) {
                //统一打折
                case 1:
                    calculateForDirectDiscount(promotionsRule, discountRule, goodsData);
                    break;
                //分别打折
                case 5:
                    calculateForDirectDiscount2(promotionsRule, discountRule, goodsData);
                    break;
                //满元打折
                case 2:
                    calculateSatisfyPriceDiscount(promotionsRule, discountRule, goodsData);
                    break;
                //满件打折
                case 3:
                    calculateSatisfyPieceDiscount(promotionsRule, discountRule, goodsData);
                    break;
                //第二件打折
                case 4:
                    calculateTheSecondOneDiscount(promotionsRule, discountRule, goodsData);
                    break;
                default:
                    throw new RuntimeException("无法解析的ruleType类型");
            }
        } catch (Exception e) {
            throw new UnknownTypeException();
        }
    }

    //计算包邮活动优惠情况
    private void calculateGoodWhetherItMeetsPostPromotions(PromotionsRule promotionsRule, List<GoodsData> goodsData, PromotionsDecorator.Param param) {
        /*
          包邮活动计算原则
          1 包邮活动优先级别最低
          2 包邮活动不仅仅要考虑是否在指定商品范围内，还需要考虑是否在包邮范围内
          3 包邮的优惠金额最后计算
          4 包邮活动需要判断freight 字段的值  如果不为0 商品goodData中的discount设置为0，result中的discount设置包邮优惠
            如果为0，则设置该商品usePromotions为false  并且设置resultMark的值为3（无优惠）
         */
        try {
            FreePostageRule freePostageRule = JSON.parseObject(promotionsRule.getPromotionsRule(), FreePostageRule.class);
            List<String> strings = Arrays.asList(freePostageRule.getGoodsIds().split(","));
            //前端传递过来的城市code
            String receiverCityCode = param.getReceiverCityCode();
            String areaIds = freePostageRule.getAreaIds();
            Integer reducePostageLimit = freePostageRule.getReducePostageLimit();
            for (GoodsData g : goodsData) {
                //判断商品是否在指定范围之内(包括是否在包邮区域内)
                switch (freePostageRule.getGoodsIdsType()) {
                    //全部商品参加并且在指定包邮区域内
                    case 0:
                        //AreaIdsType 1 指定包邮地区 2指定不包邮地区
                        freeIsInTheArea(freePostageRule, areaIds, receiverCityCode, g, reducePostageLimit);
                        break;
                    //指定商品参加
                    case 1:
                        if (strings.contains(g.getGoodsId().toString())) {
                            freeIsInTheArea(freePostageRule, areaIds, receiverCityCode, g, reducePostageLimit);
                        }
                        break;
                    //指定商品不参加
                    case 2:
                        if (!strings.contains(g.getGoodsId().toString())) {
                            freeIsInTheArea(freePostageRule, areaIds, receiverCityCode, g, reducePostageLimit);
                        }
                        break;
                    default:
                        throw new UnknownTypeException();

                }
            }
        } catch (Exception e) {
            throw new UnknownTypeException();
        }

    }

    //计算满减活动优惠情况
    private void calculateGoodWhetherItMeetsFullReducePromotions(PromotionsRule promotionsRule, List<GoodsData> goodsData) {
        /*
          满减活动计算原则
          1 满减活动优先级在满折活动之下
          2 满减活动要区分单品还是组合
          3 同级满减活动取优惠最少的那个活动
          4 ruleType 1 表示立减多少元  2 表示每满多少减多少元 3表示满多少元减多少元
          5 每满多少元减多少元有封顶设定
         */
        try {
            ReduceMoneyRule reduceMoneyRule = JSON.parseObject(promotionsRule.getPromotionsRule(), ReduceMoneyRule.class);
            switch (reduceMoneyRule.getRuleType()) {
                //立减活动
                case 1:
                    calculateCashPromotionsConcession(promotionsRule, reduceMoneyRule, goodsData);
                    break;

                //每满多少减多少活动（可设置封顶）
                case 2:
                    calculateMeetEveryTimeSatisfyPromotionsConcession(promotionsRule, reduceMoneyRule, goodsData);
                    break;

                //满多少减多少活动
                case 3:
                    calculateMeetSatisfyPromotionsConcession(promotionsRule, reduceMoneyRule, goodsData);
                    break;
                default:
                    throw new UnknownTypeException("未知RuleType类型");
            }
        } catch (Exception e) {
            throw new UnknownTypeException("满减活动类型解析异常");
        }
    }

    //计算限价活动优惠情况
    private void calculateGoodWhetherItMeetsLimitedPricePromotions(PromotionsRule promotionsRule, List<GoodsData> goodsData,Integer memberId) {

        /*
          限价活动计算原则：
          1 限价目前只有单品 calculateBase为null的就是老数据 默认为单品
          2 限价活动是独立活动 优先级别较高
          3 限价活动在商品满足指定范围内的情况下，需要考虑它是否超过单件商品件数限制和总商品限购限制
            如果超过限制，则不计算优惠（其实会在过滤的时候直接PASS掉）
          4 限价活动的统一限价和分别限价是同一种限价类型
          5 分别限价的时候，需要区分不同商品之间的不同限价价格
          6 限价活动默认单品
         */
        try {
            //转化为限价活动实体类
            FixedPriceRule fixedPriceRule = JSON.parseObject(promotionsRule.getPromotionsRule(), FixedPriceRule.class);
            switch (fixedPriceRule.getRuleType()) {
                //统一限价
                case 1:
                    calculateLimitPricePromotions(promotionsRule, fixedPriceRule, goodsData,memberId);
                    break;
                //分别限价
                case 2:
                    calculateLimitPricePromotions2(promotionsRule, fixedPriceRule, goodsData,memberId);
                    break;
                default:
                    throw new UnknownTypeException("未知限价活动优惠类型");
            }


        } catch (Exception e) {
            throw new UnknownTypeException();
        }

    }

    //计算拼团活动优惠情况
    private void calculateGoodWhetherItMeetsGroupBookingPromotions(PromotionsRule promotionsRule, List<GoodsData> goodsData) {
        /*
          拼团活动计算方式比较特殊，最后考虑
          1 拼团商品目前无法是组合
          2 拼团活动可以统一或者分别设置商品的具体价格或者对于目标商品的具体折扣
          3 拼团活动抹零的逻辑可以调用之前的接口
         */
        try {
            GroupBookingRule groupBookingRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GroupBookingRule.class);
            switch (groupBookingRule.getRuleType()) {
                //统一设置拼团金额
                case 1:
                    calculateGroupBookingUnifySum(promotionsRule, groupBookingRule, goodsData);
                    break;
                //分别设置拼团金额
                case 2:
                    calculateGroupBookingRespectiveSum(promotionsRule, groupBookingRule, goodsData);
                    break;
                //统一设置拼团折扣
                case 3:
                    calculateGroupBookingUnifyDiscount(promotionsRule, groupBookingRule, goodsData);
                    break;
                //分别设置拼团折扣
                case 4:
                    calculateGroupBookingRespectiveDiscount(promotionsRule, groupBookingRule, goodsData);
                    break;
                default:
                    throw new RuntimeException("未知的ruleType类型");
            }

        } catch (Exception e) {
            throw new UnknownTypeException();
        }
    }

    /**
     * 根据商品种类平分优惠金额，依据是该商品金额占优惠商品总价的比例
     *
     * @param goodsData
     * @param totalDiscount
     */
    private void averageDiscount(@Nonnull List<PromotionsDecorator.GoodsData> goodsData, int totalDiscount, int totalGoodsPrice) {
        int countDiscount = 0;
        for (int i = 0; i < goodsData.size(); i++) {
            PromotionsDecorator.GoodsData goods = goodsData.get(i);
            if (i != goodsData.size() - 1) {
                int goodsPrice = goods.getShopPrice() * goods.getNum() - goods.getDiscount();
                int directMoneyPerGoods = (totalDiscount * goodsPrice) / totalGoodsPrice;
                goods.setDiscount(directMoneyPerGoods);
                countDiscount += directMoneyPerGoods;
            } else {
                goods.setDiscount(totalDiscount - countDiscount);
            }
        }
    }

    //设置返回值
    private void setResult(@Nonnull List<PromotionsDecorator.GoodsData> goodsData, int totalDiscount, int totalGoodsPrice) {
        result.setResultMark(1);
        result.setDiscount(totalDiscount);
        averageDiscount(goodsData, totalDiscount, totalGoodsPrice);
    }

    //计算满足活动的商品订单总价格
    private int countTotalGoodsPrice(@Nonnull List<PromotionsDecorator.GoodsData> goodsData) {
        return goodsData.stream()
            .map(gd -> (gd.getShopPrice() * gd.getNum()) - gd.getDiscount())
            .reduce(0, Integer::sum);
    }

    //计算直接打折活动（统一打折）
    private void calculateForDirectDiscount(@Nonnull PromotionsRule promotionsRule, @Nonnull DiscountRule discountRule, @Nonnull List<GoodsData> goodsDataList) {
        int calculateBase = promotionsRuleService.getCalculateBase(discountRule, promotionsRule.getPromotionsType());
        //折扣数额
        Integer discount = discountRule.getRules().get(0).get("direct_discount");
        //折扣优惠上限
        Integer discountUpperLimit = discountRule.getRules().get(0).get("goods_money_limit");
        //是否抹零数据
        Integer isMl = discountRule.getIsMl();
        Integer isRound = discountRule.getIsRound();
        //指定商品ID范围
        List<String> strings = Arrays.asList(discountRule.getGoodsIds().split(","));

        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                PromotionsConditionChecker promotionsConditionChecker = new PromotionsConditionChecker();
                promotionsConditionChecker.classifyInTheRange(goodsDataList, discountRule.getGoodsIdsType(), strings);
                List<GoodsData> satisfyGoodsData = goodsDataList.stream().filter(GoodsData::getUsePromotions).collect(toList());
                satisfyGoodsData.forEach(gs -> {
                    //循环遍历所有商品，对商品进行打折价格计算
                    Integer discountMoney = orderDeductionUtils.discountMoney(gs.getShopPrice(), isMl, isRound, discount);
                    discountMoney *= gs.getNum();
                    //打折优惠 超过最大优惠
                    if (discountMoney > discountUpperLimit && discountUpperLimit != 0) {
                        //超过封顶价格
                        gs.setDiscount(discountUpperLimit);
                    } else {
                        gs.setDiscount(discountMoney);
                    }
                });
                int sum_discount = satisfyGoodsData.stream().filter(GoodsData::getUsePromotions).mapToInt(GoodsData::getDiscount).sum();
                result.setDiscount(sum_discount);
                result.setResultMark(1);
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("暂时无法计算的优惠类型");
        }
    }

    //计算直接打折活动（分别打折）
    private void calculateForDirectDiscount2(@Nonnull PromotionsRule promotionsRule, @Nonnull DiscountRule discountRule, @Nonnull List<GoodsData> goodsDataList) {
        int calculateBase = promotionsRuleService.getCalculateBase(discountRule, promotionsRule.getPromotionsType());
        //分别打折数据list
        List<Map<String, Integer>> ruleslist = discountRule.getRules();
        //是否抹零数据
        Integer isMl = discountRule.getIsMl();
        Integer isRound = discountRule.getIsRound();
        //指定商品ID范围
        List<String> strings = Arrays.asList(discountRule.getGoodsIds().split(","));

        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                PromotionsConditionChecker promotionsConditionChecker = new PromotionsConditionChecker();
                promotionsConditionChecker.classifyInTheRange(goodsDataList, discountRule.getGoodsIdsType(), strings);
                List<GoodsData> satisfyGoodsList = goodsDataList.stream().filter(GoodsData::getUsePromotions).collect(toList());
                satisfyGoodsList.forEach(gs -> {
                    for (Map m : ruleslist) {
                        if (m.get("goodsId").toString().equals(gs.getGoodsId().toString())) {
                            Integer discountUpperLimit = Integer.parseInt(m.get("max_reduce").toString());
                            Integer discount = Integer.parseInt(m.get("discount").toString());
                            Integer discountMoney = orderDeductionUtils.discountMoney(gs.getShopPrice(), isMl, isRound, discount);
                            discountMoney *= gs.getNum();
                            //打折优惠 超过最大优惠
                            if (discountMoney > discountUpperLimit && discountUpperLimit > 0) {
                                //超过封顶价格
                                gs.setDiscount(discountUpperLimit);
                            } else {
                                gs.setDiscount(discountMoney);
                            }
                        }
                    }
                });
                int sum_discount = satisfyGoodsList.stream().filter(GoodsData::getUsePromotions).mapToInt(GoodsData::getDiscount).sum();
                result.setDiscount(sum_discount);
                result.setResultMark(1);
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("暂时无法计算的优惠类型");
        }

    }

    //计算满元打折活动
    private void calculateSatisfyPriceDiscount(@Nonnull PromotionsRule promotionsRule, @Nonnull DiscountRule discountRule, @Nonnull List<GoodsData> goodsDataList) {
        int calculateBase = promotionsRuleService.getCalculateBase(discountRule, promotionsRule.getPromotionsType());
        //阶梯满折规则集合
        List<Map<String, Integer>> rulesList = discountRule.getRules();
        //符合满元折扣最低限制
        Integer satisfyLowerLimit = rulesList.get(0).get("meet_money");
        //抹零设置
        Integer isMl = discountRule.getIsMl();
        Integer isRound = discountRule.getIsRound();
        //指定商品范围ID;
        List<String> strings = Arrays.asList(discountRule.getGoodsIds().split(","));
        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                Integer discount = 0;
                PromotionsConditionChecker promotionsConditionChecker = new PromotionsConditionChecker();
                promotionsConditionChecker.classifyInTheRange(goodsDataList, discountRule.getGoodsIdsType(), strings);
                List<GoodsData> maybeSatisfyGoodsList = goodsDataList.stream().filter(GoodsData::getUsePromotions).collect(toList());
                Integer priceSum = maybeSatisfyGoodsList.stream().mapToInt(g -> (g.getShopPrice() * g.getNum() - g.getDiscount())).sum();
                if (priceSum >= satisfyLowerLimit) {
                    discount = handlerDiscountMeetMoney(rulesList, priceSum, discount);
                }
                if (discount == 0) {
                    maybeSatisfyGoodsList.forEach(gd -> gd.setUsePromotions(false));
                    result.setResultMark(3);
                    break;
                }
                Integer reduce_money = orderDeductionUtils.discountMoney(priceSum, isMl, isRound, discount);
                averageDiscount(maybeSatisfyGoodsList, reduce_money, priceSum);
                result.setResultMark(1);
                result.setDiscount(reduce_money);
                break;
            case CALCULATE_BASE_SINGLE_MEET_ALL:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("暂时不支持的计算类型");
        }
    }

    //计算满件打折活动
    private void calculateSatisfyPieceDiscount(@Nonnull PromotionsRule promotionsRule, @Nonnull DiscountRule discountRule, @Nonnull List<GoodsData> goodsDataList) {
        int calculateBase = promotionsRuleService.getCalculateBase(discountRule, promotionsRule.getPromotionsType());
        //阶梯满折规则集合
        List<Map<String, Integer>> rulesList = discountRule.getRules();
        //符合满件折扣最低限制
        Integer satisfyLowerLimit = rulesList.get(0).get("meet_num");
        //抹零设置
        Integer isMl = discountRule.getIsMl();
        Integer isRound = discountRule.getIsRound();
        //指定商品范围ID;
        List<String> strings = Arrays.asList(discountRule.getGoodsIds().split(","));
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                Integer  discount = 0;
                Integer totalDiscount = 0;
                PromotionsConditionChecker promotionsConditionChecker = new PromotionsConditionChecker();
                promotionsConditionChecker.classifyInTheRange(goodsDataList, discountRule.getGoodsIdsType(), strings);
                List<GoodsData> maybeSatisfyGoodsList = goodsDataList.stream().filter(GoodsData::getUsePromotions).collect(toList());
                for (GoodsData ms : maybeSatisfyGoodsList) {
                    Integer num = ms.getNum();
                    if (num >= satisfyLowerLimit) {
                        discount = handlerDiscountMeetNum(rulesList, num, discount);
                        Integer reduce_money = orderDeductionUtils.discountMoney((ms.getShopPrice()*num)-ms.getDiscount(), isMl, isRound, discount);
                        ms.setDiscount(reduce_money);
                        totalDiscount += reduce_money;
                    }
                }
               /* pieceSum = maybeSatisfyGoodsList.stream().mapToInt(GoodsData::getNum).sum();
                priceSum = maybeSatisfyGoodsList.stream().mapToInt(g -> (g.getShopPrice() - g.getDiscount()) * g.getNum()).sum();
                if (pieceSum >= satisfyLowerLimit) {
                    discount = handlerDiscountMeetNum(rulesList, pieceSum, discount);
                }*/
                if (totalDiscount == 0) {
                    maybeSatisfyGoodsList.forEach(gd -> gd.setUsePromotions(false));
                    result.setResultMark(3);
                    break;
                }
                //averageDiscount(maybeSatisfyGoodsList, reduce_money, pieceSum);
                result.setResultMark(1);
                result.setDiscount(totalDiscount);
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("暂时不支持的计算类型");
        }

    }

    //计算第二件打折活动
    private void calculateTheSecondOneDiscount(@Nonnull PromotionsRule promotionsRule, @Nonnull DiscountRule discountRule, @Nonnull List<GoodsData> goodsDataList) {
        int calculateBase = promotionsRuleService.getCalculateBase(discountRule, promotionsRule.getPromotionsType());
        //阶梯满折规则集合
        List<Map<String, Integer>> rulesList = discountRule.getRules();
        //第二件优惠件数上线
        Integer promotionsUpperLimit = rulesList.get(0).get("goods_amount_limit");
        //第二件折扣
        Integer discount = rulesList.get(0).get("discount");
        //抹零设置
        Integer isMl = discountRule.getIsMl();
        Integer isRound = discountRule.getIsRound();
        //指定商品范围ID;
        List<String> strings = Arrays.asList(discountRule.getGoodsIds().split(","));
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                Integer totalPrice = 0;
                Integer totalDiscount = 0;
                PromotionsConditionChecker promotionsConditionChecker = new PromotionsConditionChecker();
                promotionsConditionChecker.classifyInTheRange(goodsDataList, discountRule.getGoodsIdsType(), strings);
                List<GoodsData> maybeSatisfyGoodsList = goodsDataList.stream().filter(GoodsData::getUsePromotions).collect(toList());
                List<GoodsData> SatisfyGoodsList = new ArrayList<>();
                for (GoodsData gd : maybeSatisfyGoodsList) {
                    if (gd.getNum() < 2) {
                        gd.setUsePromotions(false);
                        continue;
                    } else {
                        Integer multiple = gd.getNum() / 2;
                        if (promotionsUpperLimit > 0) {
                            multiple = multiple >= promotionsUpperLimit ? promotionsUpperLimit : multiple;
                        }
                        Integer discountMoney = orderDeductionUtils.discountMoney(gd.getShopPrice() * multiple, isMl, isRound, discount);
                        totalPrice += gd.getShopPrice() * gd.getNum() - gd.getDiscount();
                        gd.setUsePromotions(true);
                        gd.setDiscount(discountMoney);
                        totalDiscount += discountMoney;
                        SatisfyGoodsList.add(gd);
                    }
                }
                //averageDiscount(SatisfyGoodsList, totalDiscount, totalPrice);
                result.setGoodsData(SatisfyGoodsList);
                result.setResultMark(1);
                result.setDiscount(totalDiscount);
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("暂时不支持的计算类型");
        }

    }

    //计算限价活动（统一限价）
    private void calculateLimitPricePromotions(@Nonnull PromotionsRule promotionsRule, @Nonnull FixedPriceRule fixedPriceRule, @Nonnull List<GoodsData> goodsDataList,Integer memberId) {
        //获取默认计算方式
        int calculateBase = promotionsRuleService.getCalculateBase(fixedPriceRule, promotionsRule.getPromotionsType());
        //限价活动指定商品ID范围
        List<String> strings = Arrays.asList(fixedPriceRule.getGoodsIds().split(","));
        //限定价格
        Integer fixedPrice = fixedPriceRule.getFixedPrice();

        //限价活动单个订单上限
        Integer buyNumEachOrder = fixedPriceRule.getBuyNumEachOrder();


        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                //参加限价活动的商品总件数

                Integer totalDiscount = 0;

                //先过滤掉不在指定范围的商品
                PromotionsConditionChecker promotionsConditionChecker = new PromotionsConditionChecker();
                promotionsConditionChecker.classifyInTheRange(goodsDataList, fixedPriceRule.getGoodsIdsType(), strings);

                //再过滤掉指定范围但是购买数量超过了单件限购数量的商品
                List<GoodsData> maybeSatisfyList = goodsDataList.stream()
                    .filter(GoodsData::getUsePromotions)
                    .sorted(Comparator.comparingInt(goodsData -> -(goodsData.getShopPrice() - goodsData.getDiscount() / goodsData.getNum()))) // 按照单价高->低排序，考虑已优惠的金额
                    .collect(toList());

                for (GoodsData goodsData : maybeSatisfyList) {
                    //已经参与该活动的数量
                    List<Orders> ordersList = ordersMapper.queryOrdersUsePromotions(promotionsRule.getSiteId(), promotionsRule.getId(), promotionsActivity.getId(),memberId,goodsData.getGoodsId());
                    Integer count = ordersList.stream()
                        .mapToInt(Orders::getGoodsNum)
                        .sum();

                    //限价活动总计商品上限
                    Integer storage = fixedPriceRule.getTotal() - count;
                    goodsData.setUsePromotions(false);
                    int canLimitNum = IntStream.of(buyNumEachOrder, storage, goodsData.getNum())
                        .min()
                        .orElseThrow(RuntimeException::new);

                    if (canLimitNum <= 0)
                        continue;

                    Integer discount;
                    if (goodsData.getNum() > canLimitNum) {
                        Integer perDiscountByGoodsNum = goodsData.getDiscount() / goodsData.getNum();
                        discount = (goodsData.getShopPrice() - perDiscountByGoodsNum - fixedPrice) * canLimitNum;
                    } else {
                        canLimitNum = goodsData.getNum();
                        discount = (goodsData.getShopPrice() - fixedPrice) * canLimitNum - goodsData.getDiscount();
                    }

                    if (discount <= 0)
                        continue;
                    totalDiscount += discount;
                    goodsData.setUsePromotions(true);
                    goodsData.setDiscount(discount);
                }
                result.setGoodsData(maybeSatisfyList);
                result.setDiscount(totalDiscount);
                result.setResultMark(1);

                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
                throw new RuntimeException("暂不支持的计算类型");

            default:
                throw new UnknownTypeException();
        }

    }

    //计算限价活动(分别限价)
    private void calculateLimitPricePromotions2(@Nonnull PromotionsRule promotionsRule, @Nonnull FixedPriceRule fixedPriceRule, @Nonnull List<GoodsData> goodsDataList,Integer memberId) {
        //获取默认计算方式
        int calculateBase = promotionsRuleService.getCalculateBase(fixedPriceRule, promotionsRule.getPromotionsType());
        //商品限价数据集合
        List<Map<String, Integer>> rulesList = fixedPriceRule.getRules();

        //限价活动单个商品上限
        Integer buyNumEachOrder = fixedPriceRule.getBuyNumEachOrder();

        List<String> goodsIdList = rulesList.stream()
            .map(map -> map.get("goodsId").toString())
            .collect(toList());

        Map<String, String> fixedPriceByGoodsId = rulesList.stream()
            .collect(Collectors.toMap(map -> map.get("goodsId").toString(), map -> map.get("fixedPrice").toString()));

        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                //参加限价活动的商品总件数
                Integer totalDiscount = 0;

                PromotionsConditionChecker promotionsConditionChecker = new PromotionsConditionChecker();
                promotionsConditionChecker.classifyInTheRange(goodsDataList, fixedPriceRule.getGoodsIdsType(), goodsIdList);
                List<GoodsData> maybeSatisfyList = goodsDataList.stream()
                    .filter(GoodsData::getUsePromotions)
                    .sorted(Comparator.comparingInt(goodsData -> -(goodsData.getShopPrice() - goodsData.getDiscount() / goodsData.getNum()))) // 按照单价高->低排序，考虑已优惠的金额
                    .collect(toList());


                for (GoodsData goodsData : maybeSatisfyList) {
                    //已经参与该活动的数量
                    List<Orders> ordersList = ordersMapper.queryOrdersUsePromotions(promotionsRule.getSiteId(), promotionsRule.getId(), promotionsActivity.getId(),memberId,goodsData.getGoodsId());
                    Integer count = ordersList.stream()
                        .mapToInt(Orders::getGoodsNum)
                        .sum();

                    //限价活动总计商品上限
                    Integer storage = fixedPriceRule.getTotal() - count;
                    goodsData.setUsePromotions(false);

                    int canLimitNum = IntStream.of(buyNumEachOrder, storage, goodsData.getNum())
                        .min()
                        .orElseThrow(RuntimeException::new);

                    if (canLimitNum <= 0)
                        continue;

                    Integer fixedPrice = Integer.valueOf(fixedPriceByGoodsId.get(goodsData.getGoodsId().toString()));

                    Integer discount;
                    if (goodsData.getNum() > canLimitNum) {
                        Integer perDiscountByGoodsNum = goodsData.getDiscount() / goodsData.getNum();
                        discount = (goodsData.getShopPrice() - perDiscountByGoodsNum - fixedPrice) * canLimitNum;
                    } else {
                        canLimitNum = goodsData.getNum();
                        discount = (goodsData.getShopPrice() - fixedPrice) * canLimitNum - goodsData.getDiscount();
                    }

                    if (discount <= 0) {
                        continue;
                    }

                    totalDiscount += discount;
                    goodsData.setUsePromotions(true);
                    goodsData.setDiscount(discount);
                }

                result.setGoodsData(maybeSatisfyList);
                result.setDiscount(totalDiscount);
                result.setResultMark(1);
                break;

            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
                throw new RuntimeException("暂不支持的计算类型");

            default:
                throw new UnknownTypeException();
        }

    }

    //立减活动计算方法
    private void calculateCashPromotionsConcession(@Nonnull PromotionsRule promotionsRule, @Nonnull ReduceMoneyRule reduceMoneyRule, @Nonnull List<GoodsData> goodsData) {
        int calculateBase = promotionsRuleService.getCalculateBase(reduceMoneyRule, promotionsRule.getPromotionsType());
        List<String> strings = Arrays.asList(reduceMoneyRule.getGoodsIds().split(","));
        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                //满减活动立减多少元
                Integer reduceMoney = reduceMoneyRule.getRules().get(0).getReduceMoney();
                PromotionsConditionChecker promotionsConditionChecker = new PromotionsConditionChecker();
                promotionsConditionChecker.classifyInTheRange(goodsData, reduceMoneyRule.getGoodsIdsType(), strings);
                List<GoodsData> satisfyGoodsData = goodsData.stream().filter(GoodsData::getUsePromotions).collect(toList());
                setResult(satisfyGoodsData, reduceMoney, countTotalGoodsPrice(satisfyGoodsData));
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
                throw new UnknownTypeException("立减活动暂不支持的计算类型");
        }

    }

    //阶梯活动计算方法
    public void calculateMeetSatisfyPromotionsConcession(@Nonnull PromotionsRule promotionsRule, @Nonnull ReduceMoneyRule reduceMoneyRule, @Nonnull List<GoodsData> goodsData) {
        //阶梯满减活动
        int calculateBase = promotionsRuleService.getCalculateBase(reduceMoneyRule, promotionsRule.getPromotionsType());
        //满减最低限度
        Integer meetMoney = reduceMoneyRule.getRules().get(0).getMeetMoney();
        //阶梯满减规则集合
        List<ReduceMoneyRule.InnerRule> riList = reduceMoneyRule.getRules();
        //具体满减多少金额
        Integer reduceMoney = 0;
        List<String> strings = Arrays.asList(reduceMoneyRule.getGoodsIds().split(","));
        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                PromotionsConditionChecker promotionsConditionChecker = new PromotionsConditionChecker();
                promotionsConditionChecker.classifyInTheRange(goodsData, reduceMoneyRule.getGoodsIdsType(), strings);
                List<GoodsData> satisfyGoodsData = goodsData.stream().filter(GoodsData::getUsePromotions).collect(toList());
                Integer totalGoodsPrice = countTotalGoodsPrice(satisfyGoodsData);
                if (totalGoodsPrice >= meetMoney) {
                    for (ReduceMoneyRule.InnerRule ri : riList) {
                        if (totalGoodsPrice >= ri.getMeetMoney()) {
                            reduceMoney = ri.getReduceMoney();
                        }
                    }
                    setResult(satisfyGoodsData, reduceMoney, totalGoodsPrice);
                } else {
                    satisfyGoodsData.forEach(ss -> ss.setUsePromotions(false));
                    result.setResultMark(3);
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
                throw new UnknownTypeException("阶梯满减活动暂不支持的计算类型");
        }
    }

    //每满减计算方法
    private void calculateMeetEveryTimeSatisfyPromotionsConcession(@Nonnull PromotionsRule promotionsRule, @Nonnull ReduceMoneyRule reduceMoneyRule, @Nonnull List<GoodsData> goodsData) {
        //每满多少减去多少活动
        int calculateBase = promotionsRuleService.getCalculateBase(reduceMoneyRule, promotionsRule.getPromotionsType());
        //参加活动之后满足活动优惠限度
        Integer meetMoney = reduceMoneyRule.getRules().get(0).getMeetMoney();
        //满足活动后的满减金额
        Integer reduceMoney = reduceMoneyRule.getRules().get(0).getReduceMoney();
        //活动的封顶金额
        int cap = reduceMoneyRule.getRules().get(0).getCap();
        List<String> strings = Arrays.asList(reduceMoneyRule.getGoodsIds().split(","));

        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                PromotionsConditionChecker promotionsConditionChecker = new PromotionsConditionChecker();
                promotionsConditionChecker.classifyInTheRange(goodsData, reduceMoneyRule.getGoodsIdsType(), strings);
                List<GoodsData> satisfyGoodsData = goodsData.stream().filter(GoodsData::getUsePromotions).collect(toList());
                Integer totalGoodsPrice = countTotalGoodsPrice(satisfyGoodsData);
                if (totalGoodsPrice >= meetMoney) {
                    Integer multiple = totalGoodsPrice / meetMoney;
                    Integer totalDisCount = 0;
                    if (cap > 0) {
                        totalDisCount = reduceMoney * multiple >= cap ? cap : reduceMoney * multiple;
                    } else {
                        totalDisCount = reduceMoney * multiple;
                    }
                    setResult(satisfyGoodsData, totalDisCount, totalGoodsPrice);
                } else {
                    satisfyGoodsData.forEach(ss -> ss.setUsePromotions(false));
                    result.setResultMark(3);
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
                throw new UnknownTypeException("每满-减活动暂不支持的计算类型");
        }
    }

    //赠品买啥送啥计算方法
    private void calculateBuySomeOneSendSomeOnePromotionsConcession(@Nonnull PromotionsRule promotionsRule, @Nonnull GiftRule giftRule, @Nonnull List<GoodsData> goodsData) {

        Integer calculateBase = giftRule.getCalculateBase();
        //满赠活动满件最小满足数量
        Integer meetNum = giftRule.getRuleConditions().get(0).getMeetNum();
        //满赠活动满元最小满足数量
        Integer meetMoney = giftRule.getRuleConditions().get(0).getMeetMoney();
        //满赠活动阶梯满赠集合
        List<GiftRule.RuleCondition> grList = giftRule.getRuleConditions();
        //获取指定赠送的赠品集合
        List<GiftRule.sendGifts> sendGifts = giftRule.getSendGifts();
        //满赠活动该最大赠送数量
        //id集合
        List<String> strings = Arrays.asList(giftRule.getGoodsIds().split(","));
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                Integer maxSendNum = 0;
                //买同种之送同种（满件）-》叠加计算
                if (giftRule.getRuleType() == 1) {
                    goodsData.forEach(g -> {
                        if (!strings.contains(g.getGoodsId().toString()) || g.getNum() < meetNum) {
                            g.setUsePromotions(false);
                        } else {
                            g.setUsePromotions(true);
                        }
                    });
                    //参加并满足满赠活动的商品集合
                    List<GoodsData> satisfyGoodsData = goodsData.stream().filter(GoodsData::getUsePromotions).collect(toList());
                    Integer temp = 0;
                    for (GoodsData g : satisfyGoodsData) {
                        if (g.getNum() >= meetNum) {
                            temp = handlerGiftMeetNum(grList, g.getNum(), temp);
                            maxSendNum += maxSenNumCalculate(maxSendNum, sendGifts, temp, g);
                        }
                    }
                    if (maxSendNum <= 0) {
                        //一个要赠送的赠品都没有
                        goodsData.forEach(gd -> gd.setUsePromotions(false));
                        result.getGiftResult().getGiftList().clear();
                        break;
                    }
                    result.getGiftResult().setMaxSendNum(maxSendNum);
                    result.setResultMark(2);
                } else if (giftRule.getRuleType() == 2) {
                    //买同种送同种（满元）-》组合计算，只计算一次
                    Integer maxSendNum1 = 0;
                    //组合总价格
                    Integer combinationSum = 0;
                    Integer sendNum = 0;
                    if (giftRule.getRuleType() == 2) {
                        goodsData.forEach(g -> {
                            if (!strings.contains(g.getGoodsId().toString())) {
                                g.setUsePromotions(false);
                            }
                        });
                        List<GoodsData> combinationMaybeSatisfyGoodsData = goodsData.stream().filter(g -> g.getUsePromotions() == true).collect(toList());
                        for (GoodsData ggg : combinationMaybeSatisfyGoodsData) {
                            combinationSum += (ggg.getShopPrice() * ggg.getNum() - ggg.getDiscount());
                        }
                        if (combinationSum > meetMoney) {
                            for (GiftRule.RuleCondition gr : grList) {
                                if (combinationSum >= gr.getMeetMoney()) {
                                    sendNum = handlerGiftMeetMoney(grList, combinationSum, sendNum);
                                }
                            }
                            maxSendNum1 = calculateGiftSingleSendAnyGift(sendGifts, sendNum);
                            if (maxSendNum1 <= 0) {
                                //一个要赠送的赠品都没有
                                goodsData.forEach(gd -> gd.setUsePromotions(false));
                                result.getGiftResult().getGiftList().clear();
                                break;
                            }
                            result.getGiftResult().setMaxSendNum(maxSendNum1);
                            result.setResultMark(2);
                        } else {
                            combinationMaybeSatisfyGoodsData.forEach(cm -> cm.setUsePromotions(false));
                        }
                    }
                } else {
                    throw new RuntimeException("无法解析的ruleType类型");
                }
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("暂不支持的计算类型");
        }
    }

    //赠品多种商品随便送计算方法
    private void calculateRandomSendPromotionsConcession(@Nonnull PromotionsRule promotionsRule, @Nonnull GiftRule giftRule, @Nonnull List<GoodsData> goodsData) {
        Integer calculateBase = giftRule.getCalculateBase();
        //满赠活动最小满足数量
        Integer meetNum = giftRule.getRuleConditions().get(0).getMeetNum();
        //满赠活动最小满足数量
        Integer meetMoney = giftRule.getRuleConditions().get(0).getMeetMoney();
        //满赠活动阶梯满赠集合
        List<GiftRule.RuleCondition> grList = giftRule.getRuleConditions();
        //获取指定赠送的赠品集合
        List<GiftRule.sendGifts> sendGifts = giftRule.getSendGifts();
        //id集合
        List<String> strings = Arrays.asList(giftRule.getGoodsIds().split(","));
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                Integer maxSendNum = 0;
                Integer sendNum = 0;
                goodsData.forEach(g -> {
                    if (!strings.contains(g.getGoodsId().toString()) || g.getNum() < meetNum) {
                        g.setUsePromotions(false);
                    }
                });
                List<GoodsData> satisfyGoodsData = goodsData.stream().filter(g -> g.getUsePromotions() == true).collect(toList());

                //满赠活动之满件
                if (giftRule.getRuleType() == 1) {
                    for (GoodsData gd : satisfyGoodsData) {
                        //计算满件所赠送的赠品数量
                        sendNum += handlerGiftMeetNum(grList, gd.getNum(), sendNum);
                    }
                    maxSendNum = calculateGiftSingleSendAnyGift(sendGifts, sendNum);
                    if (sendNum <= 0) {
                        result.setResultMark(3);
                        goodsData.forEach(gd -> gd.setUsePromotions(false));
                    }
                    if (maxSendNum > 0) {
                        result.getGiftResult().setMaxSendNum(maxSendNum);
                        result.setResultMark(2);
                    } else {
                        result.getGiftResult().setMaxSendNum(maxSendNum);
                        result.setResultMark(3);
                        satisfyGoodsData.forEach(sfg -> sfg.setUsePromotions(false));
                    }
                } else {
                    throw new RuntimeException("不支持的计算类型");
                }

                break;
            case CALCULATE_BASE_COMBINATION:
                Integer maxSendNum2 = 0;
                Integer sendNum1 = 0;
                goodsData.forEach(g -> {
                    if (!strings.contains(g.getGoodsId().toString())) {
                        g.setUsePromotions(false);
                    }
                });
                //满赠活动之满件
                if (giftRule.getRuleType() == 1) {
                    List<GoodsData> mabeySatisfyGoodsData = goodsData.stream().filter(g -> g.getUsePromotions() == true).collect(toList());
                    Integer totalPiece = mabeySatisfyGoodsData.stream().mapToInt(m -> m.getNum()).sum();
                    if (totalPiece >= meetNum) {
                        sendNum1 = handlerGiftMeetNum(grList, totalPiece, sendNum1);
                        maxSendNum2 = calculateGiftSingleSendAnyGift(sendGifts, sendNum1);
                        if (maxSendNum2 <= 0) {
                            //一个要赠送的赠品都没有
                            goodsData.forEach(gd -> gd.setUsePromotions(false));
                            result.getGiftResult().getGiftList().clear();
                            break;
                        }
                        result.getGiftResult().setMaxSendNum(maxSendNum2);
                        result.setResultMark(2);
                    } else {
                        mabeySatisfyGoodsData.forEach(m -> m.setUsePromotions(false));
                    }
                } else if (giftRule.getRuleType() == 2) {
                    Integer totalPrice = 0;
                    List<GoodsData> mabeySatisfyGoodsData = goodsData.stream().filter(g -> g.getUsePromotions() == true).collect(toList());
                    for (GoodsData ggg : mabeySatisfyGoodsData) {
                        totalPrice += (ggg.getShopPrice()) * ggg.getNum() - ggg.getDiscount();
                    }
                    if (totalPrice >= meetMoney) {
                        sendNum1 = handlerGiftMeetMoney(grList, totalPrice, sendNum1);
                        maxSendNum2 = calculateGiftSingleSendAnyGift(sendGifts, sendNum1);
                        if (maxSendNum2 <= 0) {
                            //一个要赠送的赠品都没有
                            goodsData.forEach(gd -> gd.setUsePromotions(false));
                            result.getGiftResult().getGiftList().clear();
                            break;
                        }
                        result.getGiftResult().setMaxSendNum(maxSendNum2);
                        result.setResultMark(2);
                    } else {
                        mabeySatisfyGoodsData.forEach(m -> m.setUsePromotions(false));
                    }
                } else {
                    throw new RuntimeException("无法解析的ruleType类型");
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("暂不支持的计算类型");
        }

    }

    /**
     * @param sendGifts
     * @param sendNum
     * @return
     */
    private Integer calculateGiftSingleSendAnyGift(List<GiftRule.sendGifts> sendGifts, Integer sendNum) {
        Integer maxSendNum = 0;
        for (GiftRule.sendGifts gs : sendGifts) {
            //该单件赠品大于应送数量
            GiftMsg giftMsg = goodsMapper.getGiftResult(param.getSiteId(), gs.getGiftId());
            Map<String, Object> imgMap = goodsmMapper.selectImgById(param.getSiteId(), gs.getGiftId());

            if (imgMap != null) {
                giftMsg.setHash(((String) imgMap.get("hash")));
            } else {
                giftMsg.setHash("");
            }
            if (gs.getSendNum() >= sendNum) {
                giftMsg.setSendNum(sendNum);
            } else {
                giftMsg.setSendNum(gs.getSendNum());
            }
            maxSendNum += giftMsg.getSendNum();
            result.getGiftResult().getGiftList().add(giftMsg);
        }
        maxSendNum = maxSendNum > sendNum ? sendNum : maxSendNum;
        return maxSendNum;
    }

    //满赠-》计算阶梯满赠种不同商品数量可以赠送的总量（每个满赠商品sendNum需要和库存对比，由此得出maxSendNum）
    private Integer maxSenNumCalculate(Integer maxSendNum, List<GiftRule.sendGifts> gsList, Integer sendNum, GoodsData g) {
        for (GiftRule.sendGifts gs : gsList) {
            if (gs.getGiftId().toString().equals(g.getGoodsId().toString())) {
                GiftMsg giftMsg = goodsMapper.getGiftResult(param.getSiteId(), gs.getGiftId());
                Integer inventory = gs.getSendNum() >= sendNum ? sendNum : gs.getSendNum();
                if (inventory != 0) {
                    Map<String, Object> stringObjectMap = goodsmMapper.selectImgById(param.getSiteId(), giftMsg.getGoodsId());
                    if (stringObjectMap != null) {
                        if (stringObjectMap.get("hash") == null) {
                            giftMsg.setHash("");
                        } else {
                            giftMsg.setHash(stringObjectMap.get("hash").toString());
                        }
                    }
                    giftMsg.setSendNum(inventory);
                    if (!result.getGiftResult().getGiftList().contains(giftMsg)) {
                        result.getGiftResult().getGiftList().add(giftMsg);
                    }
                    maxSendNum = maxSendNum + sendNum >= inventory ? inventory : sendNum;
                } else {
                    g.setUsePromotions(false);
                    return maxSendNum;
                }
            }
        }
        return maxSendNum;

    }

    //满赠 -》 计算阶梯满赠种不同商品数量该送多少件商品（不考虑库存）
    private Integer handlerGiftMeetNum(List<GiftRule.RuleCondition> rule, Integer goodsNum, Integer sendNum) {
        for (GiftRule.RuleCondition gr : rule) {
            Integer meetNum = gr.getMeetNum();
            //商品数量大于规则的件数，可以到下一阶梯再进行匹配
            if (goodsNum >= meetNum) {
                sendNum = gr.getSendNum();
            } else {
                break;
            }
        }
        return sendNum;
    }

    //满赠 -》 计算阶梯满赠种不同商品价格该送多少件商品（不考虑库存）
    private Integer handlerGiftMeetMoney(List<GiftRule.RuleCondition> rule, Integer totalPrice, Integer sendNum) {
        for (GiftRule.RuleCondition gr : rule) {
            Integer meetMoney = gr.getMeetMoney();
            //商品数量大于规则的件数，可以到下一阶梯再进行匹配
            if (totalPrice >= meetMoney) {
                sendNum = gr.getSendNum();
            } else {
                break;
            }
        }
        return sendNum;
    }

    //满折 -》 计算阶梯满折种不同商品总价该打多少折
    private Integer handlerDiscountMeetMoney(List<Map<String, Integer>> rule, Integer totalPrice, Integer discount) {
        for (Map m : rule) {
            Integer meetMoney = Integer.parseInt(m.get("meet_money").toString());
            //商品数量大于规则的件数，可以到下一阶梯再进行匹配
            if (totalPrice >= meetMoney) {
                discount = Integer.parseInt(m.get("discount").toString());
            } else {
                break;
            }
        }
        return discount;
    }

    //满折 -》 计算阶梯满折种不同商品数量该打多少折
    private Integer handlerDiscountMeetNum(List<Map<String, Integer>> rule, Integer totalPiece, Integer discount) {
        for (Map m : rule) {
            Integer meetNum = Integer.parseInt(m.get("meet_num").toString());
            //商品数量大于规则的件数，可以到下一阶梯再进行匹配
            if (totalPiece >= meetNum) {
                discount = Integer.parseInt(m.get("discount").toString());
            } else {
                break;
            }
        }
        return discount;
    }

    //包邮 特殊情况设置数值
    private void setBadResult(Result result, GoodsData g) {
        g.setUsePromotions(false);
        g.setDiscount(0);
        result.setResultMark(3);
        result.setDiscount(0);
    }

    //包邮 成功下设置数值
    private void setOkResult(Result result, GoodsData g, Integer reducePostageLimit) {
        Integer discount = param.getFreight();
        if (reducePostageLimit > 0) {
            discount = discount >= reducePostageLimit ? reducePostageLimit : discount;
        }
        g.setUsePromotions(true);
        g.setDiscount(0);
        result.setResultMark(1);
        result.setDiscount(discount);
    }

    //拼团活动统一团购价（全部商品参加，指定商品参加，指定商品不参加）
    private void calculateGroupBookingUnifySum(@Nonnull PromotionsRule promotionsRule, @Nonnull GroupBookingRule groupBookingRule, @Nonnull List<GoodsData> goodsData) {
        int calculateBase = promotionsRuleService.getCalculateBase(groupBookingRule, promotionsRule.getPromotionsType());
        List<String> strings = Arrays.asList(groupBookingRule.getGoodsIds().split(","));
        //拼团规则
        List<Map<String, Integer>> rules = groupBookingRule.getRules();
        //拼团统一设置的价格
        Integer groupPrice = rules.get(0).get("groupPrice");
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                if (groupBookingRule.getGoodsIdsType() == 0) {
                    for (GoodsData g : goodsData) {
                        groupBookingSetValue(goodsData, groupPrice, g, result);
                    }
                } else if (groupBookingRule.getGoodsIdsType() == 1) {
                    List<GoodsData> maybeSatisfyGoodsList = goodsData.stream().filter(g -> strings.contains(g.getGoodsId().toString())).collect(toList());
                    maybeSatisfyGoodsList.forEach(msg -> groupBookingSetValue(maybeSatisfyGoodsList, groupPrice, msg, result));
                } else if (groupBookingRule.getGoodsIdsType() == 2) {
                    List<GoodsData> maybeSatisfyGoodsList = goodsData.stream().filter(g -> !(strings.contains(g.getGoodsId().toString()))).collect(toList());
                    maybeSatisfyGoodsList.forEach(msg -> groupBookingSetValue(maybeSatisfyGoodsList, groupPrice, msg, result));
                } else {
                    throw new RuntimeException("无法解析的goodsIdsType");
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            case CALCULATE_BASE_COMBINATION:
            default:
                throw new RuntimeException("无法解析的计算类型");
        }
    }

    //拼团活动分别团购价（指定商品参加）
    private void calculateGroupBookingRespectiveSum(@Nonnull PromotionsRule promotionsRule, @Nonnull GroupBookingRule groupBookingRule, @Nonnull List<GoodsData> goodsData) {
        int calculateBase = promotionsRuleService.getCalculateBase(groupBookingRule, promotionsRule.getPromotionsType());
        //拼团规则
        List<Map<String, Integer>> rules = groupBookingRule.getRules();
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                if (groupBookingRule.getGoodsIdsType() == 1) {
                    rules.forEach(r -> {
                        for (GoodsData gd : goodsData) {
                            if ((r.get("goodsId").toString()).equals(gd.getGoodsId().toString())) {
                                Integer groupPrice = Integer.parseInt(r.get("groupPrice").toString());
                                groupBookingSetValue(goodsData, groupPrice, gd, result);
                            }
                        }
                    });
                } else {
                    throw new RuntimeException("无法解析的goodsIdsType");
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            case CALCULATE_BASE_COMBINATION:
            default:
                throw new RuntimeException("无法解析的计算类型");
        }
    }

    //拼团活动统一折扣（全部商品参加，指定商品参加，指定商品不参加）
    private void calculateGroupBookingUnifyDiscount(@Nonnull PromotionsRule promotionsRule, @Nonnull GroupBookingRule groupBookingRule, @Nonnull List<GoodsData> goodsData) {
        int calculateBase = promotionsRuleService.getCalculateBase(groupBookingRule, promotionsRule.getPromotionsType());
        List<String> strings = Arrays.asList(groupBookingRule.getGoodsIds().split(","));
        //拼团规则
        List<Map<String, Integer>> rules = groupBookingRule.getRules();
        Integer isMl = groupBookingRule.getIsMl();
        Integer isRound = groupBookingRule.getIsRound();
        Integer maxReduce = rules.get(0).get("maxReduce");
        Integer groupDiscount = rules.get(0).get("groupDiscount");
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                if (groupBookingRule.getGoodsIdsType() == 0) {
                    for (GoodsData g : goodsData) {
                        groupBookingSetValue2(goodsData, groupDiscount, g, result, isMl, isRound, maxReduce);
                    }
                } else if (groupBookingRule.getGoodsIdsType() == 1) {
                    List<GoodsData> maybeSatisfyGoodsList = goodsData.stream().filter(g -> strings.contains(g.getGoodsId().toString())).collect(toList());
                    maybeSatisfyGoodsList.forEach(msg -> groupBookingSetValue2(maybeSatisfyGoodsList, groupDiscount, msg, result, isMl, isRound, maxReduce));
                } else if (groupBookingRule.getGoodsIdsType() == 2) {
                    List<GoodsData> maybeSatisfyGoodsList = goodsData.stream().filter(g -> !(strings.contains(g.getGoodsId().toString()))).collect(toList());
                    maybeSatisfyGoodsList.forEach(msg -> groupBookingSetValue2(maybeSatisfyGoodsList, groupDiscount, msg, result, isMl, isRound, maxReduce));
                } else {
                    throw new RuntimeException("无法解析的goodsIdsType");
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            case CALCULATE_BASE_COMBINATION:
            default:
                throw new RuntimeException("无法解析的计算类型");
        }
    }

    //拼团活动分别折扣（指定商品参加）
    private void calculateGroupBookingRespectiveDiscount(@Nonnull PromotionsRule promotionsRule, @Nonnull GroupBookingRule groupBookingRule, @Nonnull List<GoodsData> goodsData) {
        int calculateBase = promotionsRuleService.getCalculateBase(groupBookingRule, promotionsRule.getPromotionsType());
        //拼团规则
        List<Map<String, Integer>> rules = groupBookingRule.getRules();
        Integer isMl = groupBookingRule.getIsMl();
        Integer isRound = groupBookingRule.getIsRound();
        Integer maxReduce = rules.get(0).get("maxReduce");
        Map<String, String> groupDiscountById = rules.stream()
            .collect(Collectors.toMap(map -> map.get("goodsId").toString(), map -> map.get("groupDiscount").toString()));
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                if (groupBookingRule.getGoodsIdsType() == 1) {
                    for (GoodsData gd : goodsData) {
                        groupBookingSetValue2(goodsData, Integer.parseInt(groupDiscountById.get(gd.getGoodsId().toString())), gd, result, isMl, isRound, maxReduce);
                    }
                } else {
                    throw new RuntimeException("无法解析的goodsIdsType");
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            case CALCULATE_BASE_COMBINATION:
            default:
                throw new RuntimeException("无法解析的计算类型");
        }
    }

    private void groupBookingSetValue(List<GoodsData> goodsData, Integer groupPrice, GoodsData g, Result result) {
        Integer discount = (g.getShopPrice() - g.getDiscount() - groupPrice);
        g.setDiscount(discount);
        g.setUsePromotions(true);
        result.setResultMark(1);
        result.setDiscount(discount * g.getNum());
        result.setGoodsData(goodsData);
    }

    private void groupBookingSetValue2(List<GoodsData> goodsData, Integer groupDiscount, GoodsData g, Result result, Integer isMl, Integer isRound, Integer maxReduce) {
        Integer discountMoney = orderDeductionUtils.discountMoney(g.getShopPrice(), isMl, isRound, groupDiscount);
        g.setDiscount(discountMoney >= maxReduce ? maxReduce : discountMoney);
        g.setUsePromotions(true);
        result.setResultMark(1);
        if (maxReduce > 0) {
            result.setDiscount((discountMoney >= maxReduce ? maxReduce : discountMoney) * g.getNum());
        } else {
            result.setDiscount(discountMoney);
        }
        result.setGoodsData(goodsData);
    }

    /**
     * 检查参数是否为null
     *
     * @return
     */
    private void checkNotNull() {
        Preconditions.checkNotNull(promotionsRule);
        Preconditions.checkNotNull(promotionsActivity);
        Preconditions.checkNotNull(param);
    }

    /* -- setter and getter -- */
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

    public Param getParam() {
        return param;
    }

    public Result getResult() {
        return result;
    }

    public void freeIsInTheArea(FreePostageRule freePostageRule, String areaIds, String receiverCityCode, GoodsData g, Integer reducePostageLimit) {
        if (freePostageRule.getAreaIdsType() == 1) {
            if (areaIds.contains(receiverCityCode)) {
                if (param.getFreight() == 0) {
                    setBadResult(result, g);
                } else if (param.getFreight() != 0) {
                    setOkResult(result, g, reducePostageLimit);
                }
            }
        } else if (freePostageRule.getAreaIdsType() == 2) {
            if (!areaIds.contains(receiverCityCode)) {
                if (param.getFreight() == 0) {
                    setBadResult(result, g);
                } else if (param.getFreight() != 0) {
                    setOkResult(result, g, reducePostageLimit);
                }
            }
        } else {
            throw new RuntimeException("无法解析的包邮设置");
        }
    }

}
