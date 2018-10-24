package com.jk51.modules.coupon.utils;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.jk51.commons.java8datetime.ParseAndFormat;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.UnknownTypeException;
import com.jk51.exception.UnsupportedDataTypeException;
import com.jk51.model.Stores;
import com.jk51.model.concession.ConcessionDecorator;
import com.jk51.model.concession.GiftMsg;
import com.jk51.model.concession.result.GiftResult;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.GoodsRule;
import com.jk51.model.coupon.requestParams.LimitRule;
import com.jk51.model.coupon.requestParams.TimeRule;
import com.jk51.model.order.Orders;
import com.jk51.modules.coupon.constants.CouponConstant;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.mapper.GoodsmMapper;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.promotions.utils.OrderDeductionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.jk51.modules.coupon.constants.CouponConstant.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Created by ztq on 2017/12/25
 * Description:
 */
@SuppressWarnings({"unchecked", "SuspiciousMethodCalls", "serial"})
public class CouponDecorator implements ConcessionDecorator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /* -- spring beans -- */
    private CouponSendService couponSendService;
    private StoresService storesService;
    private OrderService orderService;
    private OrderDeductionUtils orderDeductionUtils;
    private GoodsMapper goodsMapper;
    private GoodsmMapper goodsmMapper;

    /* -- Field -- */
    private final CouponDetail couponDetail;
    private final CouponRule couponRule;
    private final CouponActivity couponActivity;
    private final Param param;
    private final Result result = new Result();

    private boolean filter = false;

    /**
     * 不要随便添加构造方法，important!!!
     *
     * @param couponDetail
     * @param couponRule
     * @param couponActivity
     * @param param
     * @param sc
     */
    public CouponDecorator(CouponDetail couponDetail,
                           CouponRule couponRule,
                           CouponActivity couponActivity,
                           Param param,
                           ServletContext sc) {
        this.couponDetail = couponDetail;
        this.couponRule = couponRule;
        this.couponActivity = couponActivity;
        this.param = param;
        getBeans(sc);
    }


    @Override
    public boolean filter() {
        checkNotNull();
        // 通用过滤 除商品，规则，独立设置，使用限制以外的规则过滤
        if (!commonFilter()) {
            return false;
        }
        //商品过滤
        GoodsRule goodsRule = JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class);
        if (goodsRule == null) return false;
        if (!judgeGoods(goodsRule)) {
            setErrorMessage(FALL_SHORT_OF_GOODS);
            return false;
        }
        List<GoodsData> useCouponGoods = param.getGoodsDataList().stream().filter(GoodsData::getUseCoupon).collect(toList());

        if (!checkCouponRule(goodsRule, useCouponGoods)) return false;
        List<GoodsData> canUseCouponGoodsList = param.getGoodsDataList().stream().filter(GoodsData::getUseCoupon).collect(toList());
        this.filter = true;
        return canUseCouponGoodsList.size() > 0;
    }


    @Override
    public void concession() {
        checkNotNull();
        try {
            if (!this.filter) {
                throw new Exception("没有使用过滤方法进行操作");
            }
            if(!Objects.equals(couponDetail.getUserId(),param.getMemberId())){
                result.setErrorMsg(MEMBER_ERROR);
                throw new Exception("券所属人与使用者不符");
            }
            //GoodsRule goodsRule = JacksonUtils.json2pojo(couponRule.getGoodsRule(), GoodsRule.class);
            GoodsRule goodsRule = JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class);
            if (goodsRule == null) return;

            List<GoodsData> goodsData = param.getGoodsDataList().stream()
                .filter(GoodsData::getUseCoupon)
                .collect(toList());

            concessionCoupon(goodsRule, goodsData);

            if (result.getResultMark() == 1 && result.getCouponDiscount() == 0) {
                result.setErrorMsg(CALC_RESULT_ZERO);
                throw new Exception("优惠金额异常");
            }

            if (result.getResultMark() == 1 && result.getCouponDiscount() < 0) {
                throw new Exception("优惠金额异常");
            }
            goodsData = param.getGoodsDataList().stream()
                .filter(GoodsData::getUseCoupon)
                .collect(toList());

            if (goodsData.size() == 0) {
                this.result.setResultMark(3);
                setErrorMessage(ERROR_MESSAGE);
            }
            result.setGoodsData(goodsData);
        } catch (Exception e) {
            logger.error("优惠券计算异常:{}", e);
            throw new RuntimeException(e);
        }

    }


    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class Param {

        private Integer siteId;

        private Integer memberId;
        /**
         * 订单商品信息
         * key:goodsId value:商品id
         */
        private List<GoodsData> goodsDataList;
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
         * 用户与购买商品的门店的距离，0代表分配到总店，算不出距离。
         */
        private Integer distance;

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

        public List<GoodsData> getGoodsDataList() {
            return goodsDataList;
        }

        public void setGoodsDataList(List<GoodsData> goodsDataList) {
            this.goodsDataList = goodsDataList;
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

        public Integer getDistance() {
            return distance;
        }

        public void setDistance(Integer distance) {
            this.distance = distance;
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
         * 结果标记，1代表优惠商品金额，2表示赠送礼品, 3计算不出优惠
         */
        private Integer resultMark;

        /**
         * 过滤false原因或者是计算不出优惠的原因
         */
        private String errorMsg;

        /**
         * 优惠了多少金额
         */
        private Integer discount = 0;

        private Integer couponDiscount = 0;

        private List<GoodsData> goodsData;

        private GiftResult giftResult = new GiftResult();

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

        public Integer getCouponDiscount() {
            return couponDiscount;
        }

        public void setCouponDiscount(Integer couponDiscount) {
            this.couponDiscount = couponDiscount;
        }
    }

    /**
     * 原始数据，计算过程中不变
     */
    public static class GoodsData {

        private Integer goodsId;
        private Integer num;
        /**
         * 商店价格，单位为分
         */
        private Integer shopPrice;

        /**
         * 是否用券
         */
        private Boolean useCoupon;

        /**
         * 该类商品优惠了多少金额，以分为单位，如果该商品的useCoupon为true，则discount不能为null
         */
        private Integer discount = 0;

        private Integer couponDiscount = 0;

        public Integer getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(Integer goodsId) {
            this.goodsId = goodsId;
        }

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        public Integer getShopPrice() {
            return shopPrice;
        }

        public void setShopPrice(Integer shopPrice) {
            this.shopPrice = shopPrice;
        }

        public Boolean getUseCoupon() {
            return useCoupon;
        }

        public void setUseCoupon(Boolean useCoupon) {
            this.useCoupon = useCoupon;
        }

        public Integer getDiscount() {
            return discount;
        }

        public void setDiscount(Integer discount) {
            this.discount = discount;
        }

        public Integer getCouponDiscount() {
            return couponDiscount;
        }

        public void setCouponDiscount(Integer couponDiscount) {
            this.couponDiscount = couponDiscount;
        }
    }

    private void getBeans(ServletContext sc) {
        BeanFactory beanFactory = WebApplicationContextUtils.getWebApplicationContext(sc);
        couponSendService = beanFactory.getBean(CouponSendService.class);
        storesService = beanFactory.getBean(StoresService.class);
        orderService = beanFactory.getBean(OrderService.class);
        orderDeductionUtils = beanFactory.getBean(OrderDeductionUtils.class);
        goodsMapper = beanFactory.getBean(GoodsMapper.class);
        goodsmMapper = beanFactory.getBean(GoodsmMapper.class);
    }

    private Boolean checkCouponRule(GoodsRule goodsRule, List<GoodsData> useCouponGoods) {
        try {
            switch (couponRule.getCouponType()) {
                case CouponConstant.CASH_COUPON:
                    //现金券
                    checkReduceMoneyCoupon(goodsRule, useCouponGoods);
                    break;

                case CouponConstant.CASH_DISCOUNT_COUPON:
                    //打折券
                    checkDiscountRuleCoupon(goodsRule, useCouponGoods);
                    break;

                case CouponConstant.LIMIT_PRICE_COUPON:
                    //限价券
                    checkFixedPriceCoupon(goodsRule, useCouponGoods);
                    break;

                case CouponConstant.FREE_POSTAGE_COUPON:
                    //包邮券
                    break;

                case CouponConstant.GIFT_COUPON:
                    //满赠券
                    checkGiftRuleCoupon(goodsRule, useCouponGoods);
                    break;
            }
        } catch (Exception e) {
            logger.error("优惠券过滤因异常而失败,{}", e);
            return false;
        }
        return true;
    }


    /**
     * 检查参数是否为null
     *
     * @return
     */
    private void checkNotNull() {
        Preconditions.checkNotNull(couponDetail);
        Preconditions.checkNotNull(couponRule);
        Preconditions.checkNotNull(couponActivity);
        Preconditions.checkNotNull(param);
    }

    /**
     * 通用的过滤
     *
     * @return
     */
    private boolean commonFilter() {
        try {
            if(couponDetail.getStatus() == 0){
                throw new Exception("券已使用");
            }
            //使用优惠券与本人所拥有优惠券不符合
            if(!Objects.equals(couponDetail.getUserId(),param.getMemberId())){
                throw new Exception(MEMBER_ERROR);
            }
            //不允许针对订单的优惠券出现
            if (couponRule.getAimAt() == 0) {
                setErrorMessage(ERROR_MESSAGE);
                return false;
            }
            LimitRule limitRule = JSON.parseObject(couponRule.getLimitRule(), LimitRule.class);
            if (limitRule == null) {
                setErrorMessage(ERROR_MESSAGE);
                return false;
            }
            //校验时间
            if (!judgeLimitTime()) return false;
            //校验首单
            if (!judgeFirstOrder(limitRule)) return false;
            //检验订单类型
            if (!judgeOrderType(limitRule)) {
                setErrorMessage(NOT_APPLY_CHANNEL_ONLINE);
                return false;
            }
            //校验使用渠道
            if (!judgeApplyChannel(limitRule)) {
                setErrorMessage(NOT_APPLY_CHANNEL);
                return false;
            }
            //校验门店信息
            return judgeUseStore(limitRule);
        } catch (Exception e) {
            logger.error("异常发生:{}", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 计算优惠券入口
     *
     * @param goodsRule
     * @param goodsData
     * @throws UnsupportedDataTypeException
     */
    private void concessionCoupon(GoodsRule goodsRule, List<GoodsData> goodsData) throws Exception {
        if (goodsData.isEmpty()) {
            result.setResultMark(3);
            result.setErrorMsg(ERROR_MESSAGE);
        } else {
            switch (couponRule.getCouponType()) {
                // 现金券
                case CouponConstant.CASH_COUPON:
                    calculateCashCouponConcession(goodsRule, goodsData);
                    break;

                // 折扣券
                case CouponConstant.CASH_DISCOUNT_COUPON:
                    calculateDiscountCouponConcession(goodsRule, goodsData);
                    break;

                // 限价券
                case CouponConstant.LIMIT_PRICE_COUPON:
                    calculateLimitPriceCouponConcession(goodsRule, goodsData);
                    break;


                // 赠品券
                case CouponConstant.GIFT_COUPON:
                    calculateGiftCouponConcession(goodsRule, goodsData);
                    break;

                default:
                    throw new UnknownTypeException("优惠券类型不明");
            }
        }
    }


    //检查满赠券

    private void checkGiftRuleCoupon(GoodsRule goodsRule, List<GoodsData> useCouponGoods) throws Exception {
        switch (goodsRule.getGift_send_type()) {
            case 2:
                //买啥送啥
                checkGiftRuleType2(goodsRule, useCouponGoods);
                break;
            case 3:
                //买送任意
                checkGiftRuleType3(goodsRule, useCouponGoods);
                break;
            default:
                throw new UnsupportedDataTypeException("不支持的过滤类型");
        }
    }

    private void checkGiftRuleType2(GoodsRule goodsRule, List<GoodsData> useCouponGoods) throws UnsupportedDataTypeException {
        Integer calculateBase = goodsRule.getGift_calculate_base();
        List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
        List<GoodsRule.GiftStorage> gift_storage = goodsRule.getGift_storage();
        //准备阶梯数据
        Map<Integer, Map<String, Integer>> ladderMap = new HashMap<Integer, Map<String, Integer>>() {{
            rule.forEach(m -> put(m.get("ladder"), m));
        }};
        Map<Integer, GoodsRule.GiftStorage> giftStorage = new HashMap<Integer, GoodsRule.GiftStorage>() {{
            gift_storage.forEach(g -> put(g.getGiftId(), g));
        }};
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                if (goodsRule.getRule_type() == 1) {
                    //满件
                    Map<String, Integer> tempMap = ladderMap.get(1);
                    Integer meetNum = tempMap.get("meetNum");
                    Integer giftNum = 0;
                    for (GoodsData goods : useCouponGoods) {
                        if (goods.getNum() < meetNum) {
                            goods.setUseCoupon(false);
                            setErrorMessage(String.format(FALL_SHORT_OF_NUMBER_2_GOODS, meetNum));
                            continue;
                        }
                        GoodsRule.GiftStorage storage = giftStorage.get(goods.getGoodsId());
                        //没有赠品信息，过滤掉（买啥送啥）
                        //赠品库存不足，过滤掉
                        if (storage == null || storage.getSendNum() <= 0) {
                            goods.setUseCoupon(false);
                            continue;
                        }
                        if (storage.getSendNum() > 0) {
                            giftNum++;
                        }
                    }
                    ;
                    long count = useCouponGoods.stream().filter(GoodsData::getUseCoupon).count();
                    //本券轮询所有商品后没有一个赠品（赠品库存不足） 且必须有符合的商品
                    if (giftNum == 0) {
                        setErrorMessage(RANG_OF_STOCKS_OVERSTEP);
                    } else if (count > 0) {
                        //若有一个赠品能够赠送，则把错误信息抹除
                        setErrorMessage(null);
                    }
                } else if (goodsRule.getRule_type() == 2) {
                    //满元 已经被取消了
                    /*Map<String, Integer> tempMap = ladderMap.get(1);
                    Integer meetMoney = tempMap.get("meetMoney");
                    useCouponGoods.forEach(goods -> {
                        int item_price = goods.getShopPrice() * goods.getNum();
                        if (item_price < meetMoney) {
                            goods.setUseCoupon(false);
                        }
                        GoodsRule.GiftStorage storage = giftStorage.get(goods.getGoodsId());
                        //没有赠品信息，过滤掉（买啥送啥）
                        //赠品库存不足，过滤掉
                        if (storage == null || storage.getSendNum() <= 0)
                            goods.setUseCoupon(false);
                    });*/
                    useCouponGoods.forEach(gd -> gd.setUseCoupon(false));
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            case CALCULATE_BASE_COMBINATION:
            default:
                throw new UnsupportedDataTypeException("不支持的过滤类型");
        }

    }

    private void checkGiftRuleType3(GoodsRule goodsRule, List<GoodsData> useCouponGoods) throws UnsupportedDataTypeException {
        Integer calculateBase = goodsRule.getGift_calculate_base();
        List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
        List<GoodsRule.GiftStorage> gift_storage = goodsRule.getGift_storage();
        //准备阶梯数据
        Map<Integer, Map<String, Integer>> ladderMap = new HashMap<Integer, Map<String, Integer>>() {{
            rule.forEach(m -> put(m.get("ladder"), m));
        }};
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                boolean erase_error = false;
                if (goodsRule.getRule_type() == 1) {
                    //满件
                    Map<String, Integer> tempMap = ladderMap.get(1);
                    Integer meetNum = tempMap.get("meetNum");
                    for (GoodsData goods : useCouponGoods) {
                        if (goods.getNum() < meetNum) {
                            goods.setUseCoupon(false);
                            setErrorMessage(String.format(FALL_SHORT_OF_NUMBER_2_GOODS, meetNum));
                        } else {
                            erase_error = true;
                        }
                    }
                    ;
                    //抹除满足条件的错误信息
                    if (erase_error)
                        setErrorMessage(null);
                    checkGiftStock(useCouponGoods, gift_storage);

                } else if (goodsRule.getRule_type() == 2) {
                    /*//满元 已取消
                    Map<String, Integer> tempMap = ladderMap.get(1);
                    Integer meetMoney = tempMap.get("meetMoney");
                    useCouponGoods.forEach(goods -> {
                        int item_price = goods.getShopPrice() * goods.getNum();
                        if (item_price < meetMoney) {
                            goods.setUseCoupon(false);
                        }
                    });
                    checkGiftStock(useCouponGoods, gift_storage);*/
                    useCouponGoods.forEach(gd -> gd.setUseCoupon(false));
                }
                break;
            case CALCULATE_BASE_COMBINATION:
                if (goodsRule.getRule_type() == 1) {
                    Map<String, Integer> tempMap = ladderMap.get(1);
                    Integer meetNum = tempMap.get("meetNum");
                    int total_num = useCouponGoods.stream().mapToInt(GoodsData::getNum).sum();
                    if (total_num < meetNum) {
                        useCouponGoods.forEach(gd -> gd.setUseCoupon(false));
                        setErrorMessage(String.format(FALL_SHORT_OF_NUMBER_2_GOODS, meetNum));
                    }
                    checkGiftStock(useCouponGoods, gift_storage);
                } else if (goodsRule.getRule_type() == 2) {
                    Map<String, Integer> tempMap = ladderMap.get(1);
                    Integer meetMoney = tempMap.get("meetMoney");
                    int total_money = useCouponGoods.stream().mapToInt(gd -> gd.getShopPrice() * gd.getNum()).sum();
                    //购买价格比规则最低价格低
                    if (total_money < meetMoney) {
                        useCouponGoods.forEach(gd -> gd.setUseCoupon(false));
                        setErrorMessage(String.format(FALL_SHORT_OF_MONEY_2_GOODS_MONEY, meetMoney / 100.00f));
                    }
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new UnsupportedDataTypeException("不支持的过滤类型");
        }
    }

    private void checkGiftStock(List<GoodsData> useCouponGoods, List<GoodsRule.GiftStorage> gift_storage) {
        int total_stock = gift_storage.stream().mapToInt(GoodsRule.GiftStorage::getSendNum).sum();
        if (total_stock <= 0) {
            useCouponGoods.forEach(gd -> gd.setUseCoupon(false));
            setErrorMessage(RANG_OF_STOCKS_OVERSTEP);
        }
    }
    //检查打折券

    private void checkDiscountRuleCoupon(GoodsRule goodsRule, List<GoodsData> useCouponGoods) throws Exception {
        switch (goodsRule.getRule_type()) {
            case 4:
                //立折
                break;
            case 1:
                //满多少元打多少折
                checkReduceMoneyCouponType1AndCheckDiscountCouponType1(goodsRule, useCouponGoods);
                break;
            case 2:
                //满多少件打多少折
                checkDiscountCouponType2(goodsRule, useCouponGoods);
                break;
            case 5:
                //第二件打几折，最多优惠多少件
                checkDiscountCouponType5(goodsRule, useCouponGoods);
                break;
            case 6:
                //满减 距离
                if (couponDetail.getDistanceDiscount() == null || couponDetail.getDistanceDiscount() <= 0) {
                    throw new Exception("打折优惠券优惠出现异常");
                }
                break;
            default:
                throw new UnsupportedDataTypeException("不支持的过滤类型");
        }
    }

    private void checkDiscountCouponType5(GoodsRule goodsRule, List<GoodsData> useCouponGoods) throws UnsupportedDataTypeException {
        Integer calculateBase = getCalculateBase(goodsRule);
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                useCouponGoods.forEach(gs -> {
                    if (gs.getNum() < 2) {
                        //数量太少享受不了优惠券的优惠
                        gs.setUseCoupon(false);
                    }
                });
                if (checkedAllDisabled(useCouponGoods)) {
                    setErrorMessage(String.format(FALL_SHORT_OF_NUMBER_2_GOODS, 2));
                }
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new UnsupportedDataTypeException("不支持的过滤类型");
        }
    }

    private void checkDiscountCouponType2(GoodsRule goodsRule, List<GoodsData> useCouponGoods) throws UnsupportedDataTypeException {
        Integer calculateBase = getCalculateBase(goodsRule);
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                List<Map<String, Integer>> ladder = (List<Map<String, Integer>>) goodsRule.getRule();
                OptionalInt meet_num = ladder.stream().mapToInt(m -> m.get("meet_num")).min();
                int min_meet_num = meet_num.getAsInt();
                //默认单品
                useCouponGoods.forEach(gs -> {
                    if (gs.getNum() < min_meet_num) {
                        //单品不满足最低满件
                        gs.setUseCoupon(false);
                    }
                });
                if (checkedAllDisabled(useCouponGoods)) {
                    setErrorMessage(String.format(FALL_SHORT_OF_NUMBER_2_GOODS, min_meet_num));
                }
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new UnsupportedDataTypeException("不支持的过滤类型");
        }
    }

    private Boolean checkedAllDisabled(List<GoodsData> useCouponGoods) {
        List<GoodsData> collect = useCouponGoods.stream().filter(GoodsData::getUseCoupon).collect(toList());
        return collect == null || collect.size() == 0;
    }
    //检查满减券

    private void checkReduceMoneyCoupon(GoodsRule goodsRule, List<GoodsData> useCouponGoods) throws Exception {

        switch (goodsRule.getRule_type()) {
            case 4:
                break;
            case 1:
                //满多少元减多少 默认组合
                checkReduceMoneyCouponType1AndCheckDiscountCouponType1(goodsRule, useCouponGoods);
                break;
            case 0:
                //每满多少减多少 默认组合
                checkReduceMoneyCouponType0(goodsRule, useCouponGoods);
                break;
            case 6:
                //满减 距离
                if (couponDetail.getDistanceReduce() == null || couponDetail.getDistanceReduce() <= 0) {
                    throw new Exception("满减距离券优惠金额异常");
                }
                break;
            default:
                throw new UnsupportedDataTypeException("不支持的过滤类型");
        }
    }

    private void checkReduceMoneyCouponType0(GoodsRule goodsRule, List<GoodsData> useCouponGoods) throws UnsupportedDataTypeException {
        Integer calculateBase = getCalculateBase(goodsRule);
        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                //1.默认处理
                Map<String, Object> rule = (Map<String, Object>) goodsRule.getRule();
                Integer each_full_money = (Integer) rule.get("each_full_money");
                //商品原价-活动优惠价，得到总得商品优惠价
                int sum = useCouponGoods.stream().map(gs -> (gs.getShopPrice() * gs.getNum()) - gs.getDiscount()).mapToInt(Integer::intValue).sum();
                if (sum < each_full_money) {
                    //组合商品加起来价格低于优惠券最低使用门槛
                    useCouponGoods.forEach(gs -> gs.setUseCoupon(false));
                    setErrorMessage(String.format(FALL_SHORT_OF_MONEY_2_GOODS_MONEY, each_full_money / 100.00f));
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            case CALCULATE_BASE_SINGLE_MEET_ALL:
            default:
                throw new UnsupportedDataTypeException("不支持的过滤类型");
        }
    }

    private void checkReduceMoneyCouponType1AndCheckDiscountCouponType1(GoodsRule goodsRule, List<GoodsData> useCouponGoods) throws Exception {
        Integer calculateBase = getCalculateBase(goodsRule);
        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                //满多少元减多少 默认组合
                //按默认处理
                //1.是否符合最低的满减
                fullMoneyLadderRule(goodsRule, useCouponGoods);
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new UnsupportedDataTypeException("不支持的过滤类型");

        }
    }


    //检查限价券
    private void checkFixedPriceCoupon(GoodsRule goodsRule, List<GoodsData> useCouponGoods) throws Exception {

        //限价券只有商品限价没有分别商品限价
        switch (goodsRule.getRule_type()) {
            case 3:
                checkFixedPriceCouponType3(goodsRule, useCouponGoods);
                break;
            default:
                throw new UnknownTypeException("未知限价优惠券计算类型");
        }
    }

    private void checkFixedPriceCouponType3(GoodsRule goodsRule, List<GoodsData> useCouponGoods) throws UnsupportedDataTypeException {
        Integer calculateBase = getCalculateBase(goodsRule);
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ONE:
                Map<String, Object> map = (Map<String, Object>) goodsRule.getRule();
                Integer each_goods_max_buy_num = (Integer) map.get("each_goods_max_buy_num");
                Integer buy_num_max = (Integer) map.get("buy_num_max");
                checkBuyNumOverRange(useCouponGoods, each_goods_max_buy_num);
                if (checkedAllDisabled(useCouponGoods)) {
                    //都不具备使用券的条件
                    setErrorMessage(String.format(RANG_OF_BUY_MAX_NUMBER_OVERSTEP, buy_num_max));
                }
                break;
            case CALCULATE_BASE_COMBINATION:
            default:
                throw new UnsupportedDataTypeException("不支持的过滤类型");
        }
    }

    /**
     * @param useCouponGoods
     */
    private Map<Integer, Integer> checkBuyNumOverRange(List<GoodsData> useCouponGoods, Integer buy_num_max) {
        List<Integer> goods = useCouponGoods.stream().map(GoodsData::getGoodsId).collect(toList());
        String couponActivityId = this.couponDetail.getSource();
        Integer ruleId = this.couponRule.getRuleId();
        String orderId = this.couponDetail.getOrderId();
        List<Orders> orderList = this.orderService.findOrderByCouponDetail(param.siteId, param.memberId, couponActivityId, ruleId, orderId, goods);

        //对 历史订单的商品进行map转换 key:goodsId  value: 历史购买数量
        Map<Integer, Integer> orderMap = new HashMap<Integer, Integer>() {{
            orderList.forEach(orders -> {
                if (get(orders.getGoodsId()) == null) {
                    put(orders.getGoodsId(), orders.getGoodsNum());
                } else if (get(orders.getGoodsId()) != null) {
                    Integer num = get(orders.getGoodsId());
                    put(orders.getGoodsId(), num + orders.getGoodsNum());
                }
            });
        }};

        for (GoodsData gs : useCouponGoods) {
            Integer num = orderMap.get(gs.getGoodsId());
            if (num == null) {
                // 没有进行历史购买
                continue;
            }
            //针对活动最大购买数量
            if (buy_num_max <= num) {
                //超出购买最大限制
                setErrorMessage(String.format(RANG_OF_BUY_MAX_NUMBER_OVERSTEP, buy_num_max));
                gs.setUseCoupon(false);
            }
        }
        return orderMap;
    }


    //------------ Filter Rule --------------

    //满元阶梯
    private void fullMoneyLadderRule(GoodsRule goodsRule, List<GoodsData> useCouponGoods) {
        //List<Map> ladder = JSONArray.parseArray(goodsRule.getRule().toString(), Map.class);
        List<Map<String, Integer>> ladder = (List<Map<String, Integer>>) goodsRule.getRule();
        OptionalInt meet_money = ladder.stream().mapToInt(m -> m.get("meet_money")).min();
        int min_meet_money = meet_money.getAsInt();
        //商品原价减活动优惠 得到优惠后的价格
        int sum = useCouponGoods.stream().mapToInt(gs -> (gs.getShopPrice() * gs.getNum()) - gs.getDiscount()).sum();
        //如果组合商品价格加起来小于最低阶梯门槛
        if (sum < min_meet_money) {
            //没有资格使用该券
            //对所有的券进行遍历把useCoupon 改为false
            useCouponGoods.forEach(gs -> gs.setUseCoupon(false));
            setErrorMessage(String.format(FALL_SHORT_OF_MONEY_2_GOODS_MONEY, min_meet_money / 100.00f));
        }
    }
    //----------------------------

    //判断商品是否在规则内
    private boolean judgeGoods(GoodsRule goodsRule) {
        List<GoodsData> goodsDataList = param.getGoodsDataList();
        if (goodsRule.getType() == 0) {
            goodsDataList.forEach(goodsData -> goodsData.setUseCoupon(true));
            return true;
        }

        // 按类目，不处理
        if (goodsRule.getType() == 1) {
            goodsDataList.forEach(goodsData -> goodsData.setUseCoupon(false));
            setErrorMessage(ERROR_MESSAGE);
            return false;
        }

        Set<String> goodsIdsInParam = goodsDataList.stream()
            .map(gd -> gd.getGoodsId() + "")
            .collect(toSet());

        Set<String> goodsIdsInRule = new HashSet<>(Arrays.asList(goodsRule.getPromotion_goods().split(",")));

        goodsIdsInParam.retainAll(goodsIdsInRule);

        boolean flag = false;
        if (goodsRule.getType() == 2) {
            for (GoodsData goodsData : goodsDataList) {
                if (goodsIdsInParam.contains(goodsData.getGoodsId() + "")) {
                    goodsData.setUseCoupon(true);
                    flag = true;
                } else
                    goodsData.setUseCoupon(false);

            }
        }

        if (goodsRule.getType() == 3) {
            for (GoodsData goodsData : goodsDataList) {
                if (goodsIdsInParam.contains(goodsData.getGoodsId() + ""))
                    goodsData.setUseCoupon(false);
                else {
                    goodsData.setUseCoupon(true);
                    flag = true;
                }
            }
        }

        return flag;
    }
    //判断使用门店

    private Boolean judgeUseStore(LimitRule limitRule) {
        //代表总店，无法分配门店
        if (Integer.valueOf(0).equals(param.getStoreId()) && limitRule.getApply_store() != -1) {
            setErrorMessage(ERROR_MESSAGE);
            return false;
        }
        switch (limitRule.getApply_store()) {
            case -1:
                break; //全部门店
            case 1:
                //具体门店
                if ("200".equals(param.getOrderType())) {
                    //具体门店不允许送货上门 200 是指订单送货上门 所以该券无效
                    setErrorMessage(NOT_APPLY_CHANNEL_ONLINE);
                    return false;
                }
                if (!Arrays.asList(limitRule.getUse_stores().split(",")).contains(param.getStoreId().toString())) {
                    //没有包含使用的门店
                    setErrorMessage(NOT_APPLY_STORE);
                    return false;
                }
                break;
            case 2:
                //区域门店
                if ("200".equals(param.getOrderType())) {
                    //区域门店 不允许送货上门 200 是指订单送货上门 所以该券无效
                    setErrorMessage(NOT_APPLY_CHANNEL_ONLINE);
                    return false;
                }
                // addrCode case=2时实际是放的是城市id，limitRule.getUse_stores 也是城市id
                String addrCode = null;
                // 门店自提
                if ("100".equals(param.getOrderType())) {
                    Stores store = storesService.getStore(param.getStoreId(), param.getSiteId());
                    addrCode = store != null ? store.getCity_id() + "" : "";
                }
                String[] areasArr = limitRule.getUse_stores().split(",");
                List<String> areas = Arrays.asList(areasArr);
                if (!areas.contains(addrCode)) {
                    setErrorMessage(NOT_APPLY_STORE);
                    //选择的门店,不适合这张券的使用
                    return false;
                }
                break;
        }
        return true;
    }
    //判断订单类型

    private Boolean judgeOrderType(LimitRule limitRule) {
        //校验订单类型
        String[] split_orderType = limitRule.getOrder_type().split(",");
        List<String> orderTypes = Arrays.asList(split_orderType);
        return param.getOrderType() != null && orderTypes.contains(param.getOrderType());
    }
    //判断首单

    private Boolean judgeFirstOrder(LimitRule limitRule) {
        //校验首单
        switch (limitRule.getIs_first_order()) {
            case 0:
                break;
            case 1:
                //用户是首单true 否则是false;
                boolean firstOrder = couponSendService.isFirstOrder(param.siteId, param.memberId);
                if (!firstOrder) {
                    //不是首单
                    setErrorMessage(NOT_FIRST_ORDER);
                    return false;
                }
                break;
        }
        return true;
    }

    private boolean judgeApplyChannel(LimitRule limitRule) {
        return limitRule.getApply_channel().contains(this.param.applyChannel);
    }

    //判断时间
    public void setErrorMessage(String message) {
        if (StringUtil.isBlank(this.result.errorMsg)) {
            this.result.errorMsg = message;
        }
    }

    private Boolean judgeLimitTime() {
        //时间
        //TimeRule timeRule = JacksonUtils.json2pojo(couponRule.getTimeRule(), TimeRule.class);
        TimeRule timeRule = JSON.parseObject(couponRule.getTimeRule(), TimeRule.class);
        switch (timeRule.getValidity_type()) {
            // 绝对时间
            case TimeRule.VALIDITY_TYPE_ABSOLUTE_TIME:
//                LocalDateTime absolute = LocalDateTime.parse(timeRule.getEndTime(), ParseAndFormat.dateTimeFormatter_3);
                LocalDate absolute_date = LocalDate.parse(timeRule.getEndTime(), ParseAndFormat.dateTimeFormatter_3);
                LocalTime absolute_time = LocalTime.MAX;
                LocalDate start_date = LocalDate.parse(timeRule.getStartTime(), ParseAndFormat.dateTimeFormatter_3);
                LocalTime start_time = LocalTime.MIN;
                LocalDateTime start_datetime = LocalDateTime.of(start_date, start_time);
                LocalDateTime absolute = LocalDateTime.of(absolute_date, absolute_time);
                if (start_datetime.isAfter(param.getOrderTime())) {
                    //还未开始
                    setErrorMessage(NOT_STARTED);
                    return false;
                }
                if (absolute.isBefore(param.getOrderTime())) {
                    //结束了
                    setErrorMessage(ALREADY_ENDED);
                    return false;
                }

                break;
            // 秒杀时间
            case TimeRule.VALIDITY_TYPE_MONTH_SEPARATE:
                LocalDateTime second = LocalDateTime.parse(timeRule.getEndTime(), ParseAndFormat.longFormatter);
                LocalDateTime start_second = LocalDateTime.parse(timeRule.getStartTime(), ParseAndFormat.longFormatter);
                if (start_second.isAfter(param.getOrderTime())) {
                    //还未开始
                    setErrorMessage(NOT_STARTED);
                    return false;
                }
                if (second.isBefore(param.getOrderTime())) {
                    //结束了
                    setErrorMessage(ALREADY_ENDED);
                    return false;
                }
                break;
            case TimeRule.VALIDITY_TYPE_RELATIVE_TIME:
                LocalDateTime receiveTime = couponDetail.getCreateTime().toLocalDateTime();
                receiveTime = receiveTime.plusDays(timeRule.getHow_day().longValue());
                if (receiveTime.isBefore(param.getOrderTime())) {
                    //结束了
                    setErrorMessage(ALREADY_ENDED);
                    return false;
                }
                break;
        }
        return true;
    }


    //----------分割线-----------

    // 满赠--计算
    private void calculateGiftCouponConcession(GoodsRule goodsRule, List<GoodsData> goodsData) throws UnsupportedDataTypeException {
        switch (goodsRule.getGift_send_type()) {
            case 2:
                //买啥送啥
                calculateGiftCouponType2(goodsRule, goodsData);
                break;
            case 3:
                //多种赠品随便选
                calculateGiftCouponType3(goodsRule, goodsData);
                break;
            default:
                throw new UnknownTypeException("未知满赠优惠券计算类型");
        }
    }

    private void calculateGiftCouponType2(GoodsRule goodsRule, List<GoodsData> goodsData) throws UnsupportedDataTypeException {
        Integer calculateBase = goodsRule.getGift_calculate_base();
        List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
        List<GoodsRule.GiftStorage> gift_storage = goodsRule.getGift_storage();
        //准备阶梯数据
        Map<Integer, Map<String, Integer>> ladderMap = new HashMap<Integer, Map<String, Integer>>() {{
            rule.forEach(m -> put(m.get("ladder"), m));
        }};
        Map<Integer, GoodsRule.GiftStorage> giftStorage = new HashMap<Integer, GoodsRule.GiftStorage>() {{
            gift_storage.forEach(g -> put(g.getGiftId(), g));
        }};
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                Integer total_send_num = 0;
                if (goodsRule.getRule_type() == 1) {
                    //满件  买啥送啥只有满件了~
                    for (GoodsData gd : goodsData) {
                        Integer sendNum = 0;
                        //计算满件所赠送的赠品数量
                        sendNum = handlerGiftMeetNum(rule, ladderMap, gd.getNum(), sendNum);
                        total_send_num = calculateSingleGiftGoods(giftStorage, total_send_num, gd, sendNum);
                    }


                } /*else if (goodsRule.getRule_type() == 2) {
                    //满元   满元被去除
                    for (GoodsData gd : goodsData) {
                        Integer sendNum = 0;
                        int goodsMoney = gd.getShopPrice() * gd.getNum();
                        //计算满元所赠送的赠品数量
                        sendNum = handlerGiftMeetMoney(rule, ladderMap, goodsMoney, sendNum);
                        total_send_num = calculateSingleGiftGoods(giftStorage, total_send_num, gd, sendNum);
                    }

                }*/ else {
                    throw new UnsupportedDataTypeException("不支持的计算类型");
                }
                if (total_send_num <= 0) {
                    //一个要赠送的赠品都没有
                    goodsData.forEach(gd -> gd.setUseCoupon(false));
                    result.getGiftResult().getGiftList().clear();
                    break;
                }
                //设置全部赠品数量
                result.getGiftResult().setMaxSendNum(total_send_num);
                result.setResultMark(2);
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new UnsupportedDataTypeException("不支持的计算类型");
        }
    }

    @SuppressWarnings("unchecked")
    private void calculateGiftCouponType3(GoodsRule goodsRule, List<GoodsData> goodsData) throws UnsupportedDataTypeException {
        Integer calculateBase = goodsRule.getGift_calculate_base();
        List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
        List<GoodsRule.GiftStorage> gift_storage = goodsRule.getGift_storage();
        //准备阶梯数据
        Map<Integer, Map<String, Integer>> ladderMap = new HashMap<Integer, Map<String, Integer>>() {{
            rule.forEach(m -> put(m.get("ladder"), m));
        }};
        //规则赠品数量的最大量
        int sendNum = 0;
        Integer total_send_num = 0;
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:

                if (goodsRule.getRule_type() == 1) {
                    //满件
                    for (GoodsData gd : goodsData) {
                        //计算满件所赠送的赠品数量
                        sendNum += handlerGiftMeetNum(rule, ladderMap, gd.getNum(), sendNum);
                    }
                } /*else if (goodsRule.getRule_type() == 2) {
                    //满元  满元已经被取消了
                    for (GoodsData gd : goodsData) {
                        int goodsMoney = gd.getShopPrice() * gd.getNum();
                        //计算满元所赠送的赠品数量
                        sendNum += handlerGiftMeetMoney(rule, ladderMap, goodsMoney, sendNum);
                    }
                } */ else {
                    throw new UnsupportedDataTypeException("不支持的计算类型");
                }
                if (sendNum <= 0) {
                    result.setResultMark(3);
                    result.setErrorMsg(ERROR_MESSAGE);
                    goodsData.forEach(gd -> gd.setUseCoupon(false));
                }

                total_send_num = calculateGiftSingleSendAnyGift(gift_storage, goodsData, sendNum, total_send_num);
                if (total_send_num <= 0) {
                    //没有任何赠品可送
                    result.getGiftResult().getGiftList().clear();
                    result.setResultMark(3);
                    result.setErrorMsg(ERROR_MESSAGE);
                    goodsData.forEach(gd -> gd.setUseCoupon(false));
                }
                //假如遇到库存不足于赠送数量  totao_send_num 是把所有的赠品库存加起来的一个变量
                if (total_send_num < sendNum) {
                    sendNum = total_send_num;
                }
                //计算完成  买啥送任意赠品 单品的满赠券 只匹配第一个符合券的赠送规则
                result.getGiftResult().setMaxSendNum(sendNum);
                result.setResultMark(2);
                break;
            case CALCULATE_BASE_COMBINATION:
                //赠品 买啥送任意 组合
                if (goodsRule.getRule_type() == 1) {
                    //满件
                    //把所有商品的数量加在一起，去匹配满赠券的规则
                    int total_goods_num = goodsData.stream().filter(GoodsData::getUseCoupon).mapToInt(GoodsData::getNum).sum();
                    //解析满赠券赠送规则
                    sendNum = handlerGiftMeetNum(rule, ladderMap, total_goods_num, sendNum);
                    //赠送数量总计
                    total_send_num = 0;
                    total_send_num = calculateGiftSingleSendAnyGift(gift_storage, goodsData, sendNum, total_send_num);
                    if (total_send_num <= 0) {
                        //没有任何赠品可送
                        result.getGiftResult().getGiftList().clear();
                        goodsData.forEach(gd -> gd.setUseCoupon(false));
                        break;
                    }
                    //假如遇到库存不足于赠送数量  totao_send_num 是把所有的赠品库存加起来的一个变量
                    if (total_send_num < sendNum) {
                        sendNum = total_send_num;
                    }
                    result.getGiftResult().setMaxSendNum(sendNum);
                    result.setResultMark(2);
                    break;

                } else if (goodsRule.getRule_type() == 2) {
                    //满元
                    //把所有的商品数量乘商品单价，得到商品总价格，去匹配满赠符合的满赠数量
                    int total_goods_price = goodsData.stream().filter(GoodsData::getUseCoupon).mapToInt(gd -> (gd.getShopPrice() * gd.getNum()) - gd.getDiscount()).sum();
                    //计算满元所赠送的赠品数量
                    sendNum = handlerGiftMeetMoney(rule, ladderMap, total_goods_price, sendNum);
                    total_send_num = 0;
                    total_send_num = calculateGiftSingleSendAnyGift(gift_storage, goodsData, sendNum, total_send_num);
                    if (total_send_num <= 0) {
                        //没有任何赠品可送
                        result.getGiftResult().getGiftList().clear();
                        goodsData.forEach(gd -> gd.setUseCoupon(false));
                        result.setResultMark(3);
                        if (sendNum == 0) {
                            Map<String, Integer> firstLadder = ladderMap.get(1);
                            Integer meetMoney = firstLadder.get("meetMoney");
                            setErrorMessage(String.format(FALL_SHORT_OF_MONEY_2_GOODS_MONEY, meetMoney / 100.00f));
                        } else {
                            setErrorMessage(RANG_OF_STOCKS_OVERSTEP);
                        }
                        break;
                    }
                    //假如遇到库存不足于赠送数量  totao_send_num 是把所有的赠品库存加起来的一个变量
                    if (total_send_num < sendNum) {
                        sendNum = total_send_num;
                    }
                    //计算完成
                    result.getGiftResult().setMaxSendNum(sendNum);
                    result.setResultMark(2);
                    break;
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new UnsupportedDataTypeException("不支持的计算类型");
        }
    }

    /**
     * 抽取的满赠 送任意赠品 的计算通用方法
     *
     * @param gift_storage   赠品列表
     * @param gd             单品则是GoodsData 组合则是List<GoodsData>
     * @param sendNum        解析规则中，符合规则的赠送数量
     * @param total_send_num 赠送总量
     */
    private Integer calculateGiftSingleSendAnyGift(List<GoodsRule.GiftStorage> gift_storage, Object gd, Integer sendNum, Integer total_send_num) {
        if (sendNum <= 0) {
            //判断传入的gd 是GoodsData 还是List 因为单品和组合之分 组合是List 单品是GoodsData
            if (gd instanceof GoodsData) {
                ((GoodsData) gd).setUseCoupon(false);
            } else if (gd instanceof List) {
                ((List<GoodsData>) gd).forEach(goodsData -> goodsData.setUseCoupon(false));
            }

            return 0;
        }

        for (GoodsRule.GiftStorage giftStorage : gift_storage) {
            //取赠品信息
            GiftMsg giftResult = goodsMapper.getGiftResult(param.getSiteId(), giftStorage.getGiftId());
            if (giftResult == null) {
                if (gd instanceof GoodsData) {
                    ((GoodsData) gd).setUseCoupon(false);
                } else if (gd instanceof List) {
                    ((List<GoodsData>) gd).forEach(goodsData -> goodsData.setUseCoupon(false));
                }
                continue;
            }
            //单品  库存是否大于 这个商品的赠送数量
            if (giftStorage.getSendNum() <= sendNum) {
                // 库存数量 异常
                if (giftStorage.getSendNum() < 0) {
                    continue;
                }
                giftResult.setSendNum(giftStorage.getSendNum());
                total_send_num += giftStorage.getSendNum();
            } else {
                //库存量大于（满足）要赠送的数量
                giftResult.setSendNum(sendNum);
                total_send_num += sendNum;
            }
            Map<String, Object> imgMap = goodsmMapper.selectImgById(param.getSiteId(), giftStorage.getGiftId());
            if (imgMap != null) {
                giftResult.setHash(((String) imgMap.get("hash")));
            } else {
                giftResult.setHash("");
            }
            if (giftResult.getSendNum() == null) {
                continue;
            }
            //存放赠品信息
            result.getGiftResult().getGiftList().add(giftResult);
        }
        return total_send_num;
    }

    private Integer calculateSingleGiftGoods(Map<Integer, GoodsRule.GiftStorage> giftStorage, Integer total_send_num, GoodsData gd, Integer sendNum) {
        //没有查到要送的赠品数量
        if (sendNum <= 0) {
            gd.setUseCoupon(false);
            return total_send_num;
        }
        //判断库存
        GoodsRule.GiftStorage storage = giftStorage.get(gd.goodsId);
        GiftMsg giftResult = goodsMapper.getGiftResult(param.getSiteId(), storage.getGiftId());
        if (giftResult == null) {
            gd.setUseCoupon(false);
            return total_send_num;
        }
        //取赠品信息
        //单品  库存是否大于 这个商品的赠送数量
        if (storage.getSendNum() <= sendNum) {
            // 库存数量不足赠送数量时
            if (storage.getSendNum() < 1) {
                // 这个商品不能再送了，不能参加这个优惠券的优惠了
                gd.setUseCoupon(false);
                return total_send_num;
            }
            giftResult.setSendNum(storage.getSendNum());
            total_send_num += storage.getSendNum();
        } else {
            //库存量大于要赠送的数量
            giftResult.setSendNum(sendNum);
            total_send_num += sendNum;
        }

        //给赠品的map信息里添加赠品显示的图片
        Map<String, Object> imgMap = goodsmMapper.selectImgById(param.getSiteId(), storage.getGiftId());
        if (imgMap != null) {
            giftResult.setHash(((String) imgMap.get("hash")));
        } else {
            giftResult.setHash("");
        }
        if (giftResult.getSendNum() == null) {
            gd.setUseCoupon(false);
            return total_send_num;
        }
        //存放赠品信息
        result.getGiftResult().getGiftList().add(giftResult);

        return total_send_num;
    }

    //满赠 满元
    private Integer handlerGiftMeetMoney(List<Map<String, Integer>> rule, Map<Integer, Map<String, Integer>> ladderMap, Integer goodsMoney, Integer sendNum) {
        for (int i = 1; i <= rule.size(); i++) {
            Map<String, Integer> tempMap = ladderMap.get(i);
            Integer meetMoney = tempMap.get("meetMoney");
            if (goodsMoney >= meetMoney) {
                sendNum = tempMap.get("sendNum");
            } else {
                break;
            }
        }
        return sendNum;
    }

    //满赠 满件
    private Integer handlerGiftMeetNum(List<Map<String, Integer>> rule, Map<Integer, Map<String, Integer>> ladderMap, Integer goodsNum, Integer sendNum) {
        for (int i = 1; i <= rule.size(); i++) {
            Map<String, Integer> tempMap = ladderMap.get(i);
            Integer meetNum = tempMap.get("meetNum");
            //商品数量大于规则的件数，可以到下一阶梯再进行匹配
            if (goodsNum >= meetNum) {
                sendNum = tempMap.get("sendNum");
            } else {
                break;
            }
        }
        return sendNum;
    }


    /**
     * 限价
     *
     * @param goodsRule
     * @param goodsData
     * @throws UnsupportedDataTypeException
     */
    private void calculateLimitPriceCouponConcession(GoodsRule goodsRule, List<GoodsData> goodsData) throws Exception {
        switch (goodsRule.getRule_type()) {
            case 3:
                //限价计算
                calculateLimitPriceType3(goodsRule, goodsData);
                break;
            default:
                throw new UnknownTypeException("未知限价优惠券计算类型");
        }
    }

    /**
     * @param goodsRule
     * @param goodsData
     * @throws Exception
     */
    //限价 --计算
    private void calculateLimitPriceType3(GoodsRule goodsRule, List<GoodsData> goodsData) throws Exception {
        Integer calculateBase = getCalculateBase(goodsRule);
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ONE:
                Map<String, Integer> rule = (Map<String, Integer>) goodsRule.getRule();
                //限价活动单个订单上限
                Integer buy_num_max = rule.get("buy_num_max");
                //限价活动总计商品上限
                Integer each_goods_max_buy_num = rule.get("each_goods_max_buy_num");
                Integer fixedPrice = rule.get("each_goods_price");

                goodsData = goodsData.stream().filter(GoodsData::getUseCoupon).collect(toList());
                //将不符合的商品进行标记，待剔除（针对的是超过活动最大购买数量）改

                Map<Integer, Integer> orderMap = checkBuyNumOverRange(goodsData, each_goods_max_buy_num);
                //将标记的商品(不符合)进行剔除
                goodsData = goodsData.stream().filter(GoodsData::getUseCoupon).collect(toList());
                boolean is_not_calculate = false;
                //最大限价商品的那个优惠
                int max_limit_price = 0;
                //最大限价优惠的那个商品的id
                int goodsId = 0;
                for (GoodsData gd : goodsData) {
                    //历史购买数量
                    Integer history_buy_num = orderMap.get(gd.getGoodsId());
                    if (history_buy_num == null) {
                        //历史购买量为null 时为其初始化值，防止后续操作空指针
                        history_buy_num = 0;
                    }
                    if (history_buy_num >= each_goods_max_buy_num) {
                        //历史购买数量超过或等于最大活动购买量
                        gd.setUseCoupon(false);
                        continue;
                    }
                    //得到历史可购买余量  历史购买2件   最大购买量5  这里就是3
                    int can_buy_num = each_goods_max_buy_num - history_buy_num;
                    if (can_buy_num < 0) {
                        //不可能小于0  (一脸质疑)
                        gd.setUseCoupon(false);
                        continue;
                    }
                    //判断，符合历史可购买数量 是否满足于本次购买的数量，满足即把本次购买数量设置为能够购买的数量
                    //反之呢，就是这次购买量有点大，超过了历史可购买量，只能购买can_buy_num数量的商品
                    if (can_buy_num >= gd.getNum()) {
                        can_buy_num = gd.getNum();
                    }
                    //这里 基本获得了满足于历史条件的购买数量，下面判断，本次购买的最大限制数量
                    if (can_buy_num >= buy_num_max) {
                        //我们历史可购买余量也超出了本次最大购买量的话，就将可购买量设置成为本次最大购买量
                        can_buy_num = buy_num_max;
                    }
                    //现在的can_buy_num 是这个商品购买数量可参加限价的数量
                    //可购买量的限价优惠价格
                    int price = can_buy_num * fixedPrice;
                    //可购买量的原价
                    int cost = gd.getShopPrice() * can_buy_num;

                    if (price <= 0) {
                        //不能为负数
                        gd.setUseCoupon(false);
                        is_not_calculate = true;
                        continue;
                    }
                    if (cost <= price) {
                        //原价低于优惠后的价格
                        gd.setUseCoupon(false);
                        is_not_calculate = true;
                        break;
                    }
                    //本次优惠的价格
                    int each_limit_price_discount = cost - price;
                    if (each_limit_price_discount <= 0) {
                        //原价减限价 得道的优惠小于等于0
                        gd.setUseCoupon(false);
                        is_not_calculate = true;
                        break;
                    }
                    //判断 之前保存的最大优惠价格相比较，如果优惠幅度与本次的优惠价格一样
                    if (max_limit_price == each_limit_price_discount) {
                        //那么久比较两个的商品id，把最大的优惠价格更新为商品id最大的那个
                        if (goodsId < gd.getGoodsId()) {
                            //暂时确认为本次循环的商品作为享受优惠券的商品
                            goodsId = gd.getGoodsId();
                            max_limit_price = each_limit_price_discount;
                            gd.setUseCoupon(true);
//                            gd.setDiscount(gd.getDiscount() + each_limit_price_discount);
                            gd.setCouponDiscount(each_limit_price_discount);
                        }
                        continue;
                    }
                    // 之前保存的最大价格 比本次循环的优惠小 替换最大优惠价格
                    if (max_limit_price < each_limit_price_discount) {
                        //暂时确认为本次循环的商品作为享受优惠券的商品
                        goodsId = gd.getGoodsId();
                        max_limit_price = each_limit_price_discount;
                        gd.setUseCoupon(true);
//                        gd.setDiscount(gd.getDiscount()+each_limit_price_discount);
                        gd.setCouponDiscount(each_limit_price_discount);
                    }
                }
                if (max_limit_price == 0) {
                    result.setErrorMsg(CALC_RESULT_ZERO);
                    throw new Exception("价格计算异常");
                }
                if (max_limit_price < 0 || is_not_calculate) {
                    //价格计算异常
                    goodsData.forEach(gd -> gd.setUseCoupon(false));
                    throw new Exception("价格计算异常");
                }
                //限价券只能用一次，所以要将其他的优惠小的商品全部置为false
                //将最大优惠商品id给一个final字段 用于lambda表达式内判断
                final int goodsId_f = goodsId;
                goodsData.stream().filter(gd -> gd.getGoodsId() != goodsId_f).forEach(gd -> gd.setUseCoupon(false));
                result.setResultMark(1);
//                result.setDiscount(result.getDiscount() + max_limit_price);
                result.setCouponDiscount(max_limit_price);
                break;
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                //暂无
            case CALCULATE_BASE_COMBINATION:
                //暂无，走异常
            default:
                throw new UnsupportedDataTypeException("不支持的计算类型");
        }
    }


    /**
     * 计算现金券优惠
     *
     * @param goodsRule 参数couponRule已解析出来的GoodsRule
     * @param goodsData
     */
    private void calculateCashCouponConcession(@Nonnull GoodsRule goodsRule, @Nonnull List<GoodsData> goodsData) throws UnsupportedDataTypeException {
        switch (goodsRule.getRule_type()) {
            case 0: // 每满元减
                calculateCashCouponType0(goodsRule, goodsData);
                break;

            case 1: // 满元减
                calculateCashCouponType1(goodsRule, goodsData);
                break;

            case 4: // 立减多少元
                calculateCashCouponType4(goodsRule, goodsData);
                break;

            case 6: // 按距离满减
                calculateCashCouponType6(goodsRule, goodsData);
                break;

            default:
                throw new UnknownTypeException("未知现金优惠券计算类型");
        }
    }

    //现金 每满元减 计算
    private void calculateCashCouponType0(GoodsRule goodsRule, List<GoodsData> goodsData) throws UnsupportedDataTypeException {
        Integer calculateBase = getCalculateBase(goodsRule);

        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                Integer totalGoodsPrice = countTotalGoodsPrice(goodsData);
                Map<String, Integer> rule = (Map) goodsRule.getRule();
                Integer eachFullMoney = rule.get("each_full_money");
                Integer reducePrice = rule.get("reduce_price");
                Integer maxReduce = rule.get("max_reduce");

                Integer calculateNum = totalGoodsPrice / eachFullMoney;

                int reduceMoney = reducePrice * calculateNum;
                if (maxReduce == 0) {
                    if (reduceMoney > totalGoodsPrice)
                        reduceMoney = totalGoodsPrice;
                } else {
                    if (reduceMoney > maxReduce)
                        reduceMoney = maxReduce;

                    if (reduceMoney > totalGoodsPrice)
                        reduceMoney = totalGoodsPrice;
                }

                setResult(goodsData, reduceMoney, totalGoodsPrice);

                break;

            case CALCULATE_BASE_SINGLE_MEET_ALL:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new UnsupportedDataTypeException("不支持的计算类型");
        }
    }

    //现金 满元减 计算
    private void calculateCashCouponType1(@Nonnull GoodsRule goodsRule, @Nonnull List<GoodsData> goodsData) throws UnsupportedDataTypeException {
        Integer calculateBase = getCalculateBase(goodsRule);

        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                Integer totalGoodsPrice = countTotalGoodsPrice(goodsData);
                List<Map<String, Integer>> rule = (List) goodsRule.getRule();

                Map<Integer, Map<String, Integer>> tempMap = new HashMap<>();
                for (Map<String, Integer> map : rule) {
                    tempMap.put(map.get("ladder"), map);
                }

                int reduceMoney = 0;
                for (int i = 0; i < rule.size(); i++) {
                    Integer meetMoney = tempMap.get(i + 1).get("meet_money");
                    if (totalGoodsPrice >= meetMoney)
                        reduceMoney = tempMap.get(i + 1).get("reduce_price");
                    else
                        break;
                }

                if (reduceMoney == 0) {
                    goodsData.forEach(gd -> gd.setUseCoupon(false));
                    break;
                }

                setResult(goodsData, reduceMoney, totalGoodsPrice);

                break;

            case CALCULATE_BASE_SINGLE_MEET_ONE:
            case CALCULATE_BASE_SINGLE_MEET_ALL:
            default:
                throw new UnsupportedDataTypeException("不支持的计算类型");
        }
    }

    //现金 距离 计算
    private void calculateCashCouponType6(GoodsRule goodsRule, List<GoodsData> goodsData) {
        int reduceMoney;

        reduceMoney = couponDetail.getDistanceReduce();

        if (reduceMoney == 0) {
            goodsData.forEach(gd -> gd.setUseCoupon(false));
        }

        Integer totalGoodsPrice = countTotalGoodsPrice(goodsData);

        setResult(goodsData, reduceMoney, totalGoodsPrice);
    }

    //现金 立减 计算
    private void calculateCashCouponType4(@Nonnull GoodsRule goodsRule, @Nonnull List<GoodsData> goodsData) {
        int directMoney = Integer.parseInt(((Map) goodsRule.getRule()).get("direct_money").toString());

        Integer totalPrice = countTotalGoodsPrice(goodsData);

        if (directMoney > totalPrice)
            directMoney = totalPrice;

        setResult(goodsData, directMoney, totalPrice);
    }


    /**
     * 计算打折优惠
     *
     * @param goodsRule
     * @param goodsData
     */
    private void calculateDiscountCouponConcession(@Nonnull GoodsRule goodsRule, List<GoodsData> goodsData) throws Exception {
        // 打折
        switch (goodsRule.getRule_type()) {
            case 4:
                //立折
                calculateDiscountType4(goodsRule, goodsData);
                break;
            case 1:
                //满元折
                calculateDiscountType1(goodsRule, goodsData);
                break;
            case 2:
                //满件折
                calculateDiscountType2(goodsRule, goodsData);
                break;
            case 5:
                //第二件折
                calculateDiscountType5(goodsRule, goodsData);
                break;
            case 6:
                //距离打折
                calculateDiscountType6(goodsRule, goodsData);
                break;
        }
    }

    //打折--距离 计算
    private void calculateDiscountType6(GoodsRule goodsRule, List<GoodsData> goodsData) throws Exception {
        int discount;
        discount = couponDetail.getDistanceDiscount();
        if (discount == 0) {
            throw new Exception("打折距离券折扣信息出错");
        }
        //商品总价格（原）
        Integer totalGoodsPrice = countTotalGoodsPrice(goodsData);

        //优惠的钱(分)
        Integer discountMoney = orderDeductionUtils.discountMoney(totalGoodsPrice, goodsRule.getIs_ml(), goodsRule.getIs_round(), discount);

        //把总优惠平均到每个商品
        averageDiscount(goodsData, discountMoney, totalGoodsPrice);
        result.setResultMark(1);
//        result.setDiscount(result.getDiscount() + discountMoney);
        result.setCouponDiscount(discountMoney);
    }

    //打折--第二件折 计算
    private void calculateDiscountType5(GoodsRule goodsRule, List<GoodsData> goodsData) throws Exception {
        Integer calculateBase = getCalculateBase(goodsRule);
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                Map<String, Integer> rule = (Map<String, Integer>) goodsRule.getRule();
                Integer discount = rule.get("discount");
                Integer max_buy_num = rule.get("max_buy_num");
                for (GoodsData gs : goodsData) {
                    int discount_num = gs.getNum();
                    if (gs.getNum() >= 2) {
                        discount_num = discount_num / 2;
                        if (discount_num > max_buy_num && max_buy_num != 0) {
                            discount_num = max_buy_num;
                        }
                    } else {
                        gs.setUseCoupon(false);
                        continue;
                    }
                    if (discount_num == gs.getNum()) {
                        //数量没变过，没有使用打折
                        gs.setUseCoupon(false);
                        continue;
                    }
                    Integer discountMoney = orderDeductionUtils.discountMoney((gs.getShopPrice() * discount_num) - ((gs.getDiscount() / gs.getNum()) * discount_num), goodsRule.getIs_ml(), goodsRule.getIs_round(), discount);
//                    gs.setDiscount(discountMoney);
                    gs.setCouponDiscount(discountMoney);
                }
                int sum_reduce_money = goodsData.stream().filter(GoodsData::getUseCoupon).mapToInt(GoodsData::getCouponDiscount).sum();
                if (sum_reduce_money == 0) {
                    result.setErrorMsg(CALC_RESULT_ZERO);
                    throw new Exception("第二件打折计算异常");
                }
                if (sum_reduce_money < 0) {
                    throw new Exception("第二件打折计算异常");
                }
                result.setResultMark(1);
//                result.setDiscount(result.getDiscount() + sum_reduce_money);
                result.setCouponDiscount(sum_reduce_money);
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new UnsupportedDataTypeException("不支持的计算类型");
        }

    }

    //打折--满件折 计算
    private void calculateDiscountType2(GoodsRule goodsRule, List<GoodsData> goodsData) throws Exception {
        Integer calculateBase = getCalculateBase(goodsRule);
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                //强转规则
                List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
                // 转换 优惠券规则数据结构
                Map<Integer, Map<String, Integer>> ladderMap = new HashMap<Integer, Map<String, Integer>>() {{
                    for (Map<String, Integer> map : rule) {
                        put(map.get("ladder"), map);
                    }
                }};
                for (GoodsData gd : goodsData) {
                    int item_goods_price = (gd.getShopPrice() * gd.getNum()) - gd.getDiscount();
                    //判断商品全部价格符合哪个ladder优惠的档位
                    int reduce_discount = 0;
                    for (int i = 1; i <= rule.size(); i++) {
                        Map<String, Integer> ladder = ladderMap.get(i);
                        Integer meet_num = ladder.get("meet_num");
                        if (gd.getNum() >= meet_num) {
                            reduce_discount = ladder.get("discount");
                        } else {
                            break;
                        }
                    }
                    if (reduce_discount == 0) {
                        //没有找到符合的优惠打折，折扣
                        gd.setUseCoupon(false);
                        continue;
                    }
                    Integer discountMoney = orderDeductionUtils.discountMoney(item_goods_price, goodsRule.getIs_ml(), goodsRule.getIs_round(), reduce_discount);
//                    gd.setDiscount(discountMoney);
                    gd.setCouponDiscount(discountMoney);
                }
                int total_reduce_money = goodsData.stream().filter(GoodsData::getUseCoupon).map(GoodsData::getCouponDiscount).mapToInt(Integer::intValue).sum();
                if (total_reduce_money == 0) {
                    throw new Exception("满减折优惠券计算异常");
                    //按理来说，不存在的
                }
//                result.setDiscount(total_reduce_money);
                result.setCouponDiscount(total_reduce_money);
                result.setResultMark(1);
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new UnsupportedDataTypeException("不支持的计算类型");
        }
    }

    //打折--满元折 计算
    private void calculateDiscountType1(GoodsRule goodsRule, List<GoodsData> goodsData) throws Exception {
        Integer calculateBase = getCalculateBase(goodsRule);
        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                //强转 优惠券规则
                List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
                // 计算全部的商品价格
                Integer totalMoney = countTotalGoodsPrice(goodsData);
                // 转换 优惠券规则数据结构
                Map<Integer, Map<String, Integer>> ladderMap = new HashMap<Integer, Map<String, Integer>>() {{
                    for (Map<String, Integer> map : rule) {
                        put(map.get("ladder"), map);
                    }
                }};
                //判断商品全部价格符合哪个ladder优惠的档位
                int reduce_discount = 0;
                for (int i = 1; i <= rule.size(); i++) {
                    Map<String, Integer> ladder = ladderMap.get(i);
                    Integer meet_money = ladder.get("meet_money");
                    if (totalMoney >= meet_money) {
                        reduce_discount = ladder.get("discount");
                    } else {
                        break;
                    }
                }
                if (reduce_discount == 0) {
                    result.setErrorMsg(CALC_RESULT_ZERO);
                    throw new Exception("满元折优惠金额异常");
                }
                //计算折扣后的价格
                Integer reduce_money = orderDeductionUtils.discountMoney(totalMoney, goodsRule.getIs_ml(), goodsRule.getIs_round(), reduce_discount);
                averageDiscount(goodsData, reduce_money, totalMoney);
                result.setResultMark(1);
//                result.setDiscount(result.getDiscount() + reduce_money);
                result.setCouponDiscount(reduce_money);
                break;

            case CALCULATE_BASE_SINGLE_MEET_ONE:
            case CALCULATE_BASE_SINGLE_MEET_ALL:
            default:
                throw new UnsupportedDataTypeException("不支持的计算类型");
        }


    }

    //打折--立折 计算
    private void calculateDiscountType4(GoodsRule goodsRule, List<GoodsData> goodsData) throws Exception {
        Integer calculateBase = getCalculateBase(goodsRule);
        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                //打折的优惠不会超过原价
                Map<String, Object> rule = (Map<String, Object>) goodsRule.getRule();
                int direct_discount = Integer.parseInt(rule.get("direct_discount").toString());
                //最大优惠价格
                int max_reduce = Integer.parseInt(rule.get("max_reduce").toString());
                int total_discount_money = 0;
                int total_price = 0;
                for (GoodsData gs : goodsData) {
                    int item_price = (gs.getShopPrice() * gs.getNum()) - gs.getDiscount();
                    //循环遍历所有商品，对商品进行打折价格计算
                    Integer discountMoney = orderDeductionUtils.discountMoney(item_price, goodsRule.getIs_ml(), goodsRule.getIs_round(), direct_discount);
//                    gs.setDiscount(gs.getDiscount() + discountMoney);
                    gs.setCouponDiscount(discountMoney);
                    total_discount_money += discountMoney;
                    //顺便把商品总价给循环计算了
                    total_price += item_price;
                }
                if (total_discount_money == 0) {
                    //计算半天，怎么会连一毛钱都没有给我加呢
                    result.setErrorMsg(CALC_RESULT_ZERO);
                    throw new Exception("立折优惠价格计算异常");
                }
                //打折优惠 超过最大优惠
                if (total_discount_money > max_reduce && max_reduce != 0) {
                    //超过封顶价格
                    total_discount_money = max_reduce;
                    averageDiscount(goodsData, total_discount_money, total_price);
                }
//                result.setDiscount(result.getDiscount() + total_discount_money);
                result.setCouponDiscount(total_discount_money);
                result.setResultMark(1);
                break;
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            case CALCULATE_BASE_SINGLE_MEET_ALL:
            default:
                throw new UnsupportedDataTypeException("不支持的计算类型");
        }

    }

    //-----------计算工具方法

    //设置计算结果
    private void setResult(@Nonnull List<GoodsData> goodsData, int totalDiscount, int totalGoodsPrice) {
        result.setResultMark(1);
//        result.setDiscount(result.getDiscount() + totalDiscount);
        result.setCouponDiscount(totalDiscount);
        averageDiscount(goodsData, totalDiscount, totalGoodsPrice);
    }

    //获取默认的单品或者组合计算方式
    private Integer getCalculateBase(@Nonnull GoodsRule goodsRule) {
        return Optional.ofNullable(goodsRule.getGift_calculate_base())
            .orElseGet(() -> DEFAULT_CALCULATE_BASE.get(couponRule.getCouponType()).get(goodsRule.getRule_type()));
    }

    /**
     * 计算商品总金额
     *
     * @param goodsData
     * @return
     */
    private int countTotalGoodsPrice(@Nonnull List<GoodsData> goodsData) {
        return goodsData.stream()
            .map(gd -> (gd.getShopPrice() * gd.getNum()) - gd.getDiscount())
            .reduce(0, Integer::sum);
    }

    /**
     * 根据商品种类平分优惠金额，依据是该商品金额占优惠商品总价的比例
     *
     * @param goodsData
     * @param totalDiscount   减的价格
     * @param totalGoodsPrice 总价格  活动优惠后的
     */
    @SuppressWarnings("Duplicates")
    private void averageDiscount(@Nonnull List<GoodsData> goodsData, int totalDiscount, int totalGoodsPrice) {
        int countDiscount = 0;
        for (int i = 0; i < goodsData.size(); i++) {
            GoodsData goods = goodsData.get(i);
            if (i != goodsData.size() - 1) {
                int goodsPrice = (goods.getShopPrice() * goods.getNum()) - goods.getDiscount();
                int directMoneyPerGoods = (totalDiscount * goodsPrice) / totalGoodsPrice;
//                goods.setDiscount(goods.getDiscount() + directMoneyPerGoods); //goods.getDiscount 原活动减的价格  directMoneyPerGoods 是优惠券平分后才优惠价格
                goods.setCouponDiscount(directMoneyPerGoods);
                countDiscount += directMoneyPerGoods;
            } else {
                //最后一个商品的优惠信息  活动之前的优惠+ 最后减出来的优惠
                goods.setCouponDiscount(totalDiscount - countDiscount);
            }
        }
    }

    /* -- setter & getter -- */

    public CouponDetail getCouponDetail() {
        return couponDetail;
    }

    public CouponRule getCouponRule() {
        return couponRule;
    }

    public CouponActivity getCouponActivity() {
        return couponActivity;
    }

    public Param getParam() {
        return param;
    }

    public Result getResult() {
        return result;
    }
}
