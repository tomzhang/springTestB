package com.jk51.modules.concession;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.ParamErrorException;
import com.jk51.exception.UnknownTypeException;
import com.jk51.model.concession.ConcessionCalculate;
import com.jk51.model.concession.ConcessionDesc;
import com.jk51.model.concession.GiftMsg;
import com.jk51.model.concession.result.*;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.GiftRule;
import com.jk51.modules.concession.constants.ConcessionConstant;
import com.jk51.modules.concession.service.ConcessionResultHandler;
import com.jk51.modules.coupon.constants.CouponConstant;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.utils.CouponDecorator;
import com.jk51.modules.grouppurchase.request.GroupPurchaseForBeforeOrder;
import com.jk51.modules.promotions.service.PromotionsActivityService;
import com.jk51.modules.promotions.utils.PromotionsDecorator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.jk51.modules.promotions.constants.PromotionsConstant.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Created by ztq on 2017/12/25
 * Description: 优惠计算规则实现
 */
public class ConcessionCalculateBaseImpl implements ConcessionCalculate {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /* -- spring beans -- */
    private CouponDetailMapper couponDetailMapper;
    private CouponRuleMapper couponRuleMapper;
    private CouponActivityMapper couponActivityMapper;
    private PromotionsActivityService promotionsActivityService;
    private ConcessionResultHandler concessionResultHandler;


    /* -- private Field -- */

    private Param param;

    /**
     * 可以参加的活动列表
     */
    private List<PromotionsActivity> promotionsActivities;

    /**
     * 计算过程中生成的结果
     */
    private Result result = new Result();

    /**
     * 可以参加活动的商品id集合
     */
    private Set<Integer> goodsIdsCanUsePromotions = new HashSet<>();

    /**
     * 活动计算顺序
     */
    private final Integer[] promotionsTypesOrder = new Integer[]{
        PROMOTIONS_RULE_TYPE_GROUP_BOOKING,
        PROMOTIONS_RULE_TYPE_LIMIT_PRICE,
        PROMOTIONS_RULE_TYPE_DISCOUNT,
        PROMOTIONS_RULE_TYPE_MONEY_OFF,
        PROMOTIONS_RULE_TYPE_GIFT,
        PROMOTIONS_RULE_TYPE_FREE_POST,
    };


    /**
     * 不要随便添加构造方法，important!!!
     *
     * @param param
     * @param promotionsActivities
     */
    public ConcessionCalculateBaseImpl(Param param, List<PromotionsActivity> promotionsActivities) {
        this.param = param;
        this.promotionsActivities = promotionsActivities;
        getSpringBeans(checkNotNull(param.getServletContext()));
    }


    /* -- Method -- */
    @Override
    public Optional<Result> calculateInAllRule() {
        // 参数check
        if (!checkParam()) {
            return Optional.empty();
        }

        // 设置商品参数
        setGoodsInfoForResultAndOthers();

        // 团购活动彻底独立计算
        //如果拼团，则底下的的优惠券和活动都不会进行计算
        boolean groupPurchaseFlag = calculateGroupPurchasePromotions();

        //计算优惠之前，赋值优惠券和优惠劵规则
        if (param.getCouponDetailId()!=null) {
            CouponDetail couponDetail = checkNotNull(couponDetailMapper.getCouponDetailByCouponId(param.getSiteId(), checkNotNull(param.getCouponDetailId())));
            CouponRule couponRule = checkNotNull(couponRuleMapper.getByCouponId(couponDetail.getId(), param.getSiteId()));
            this.param.setCouponRule(couponRule);
            this.param.setCouponDetail(couponDetail);
        }
        //限价劵先算优惠券后获得
        if ((this.param.getCouponRule()!=null&&this.param.getCouponRule().getCouponType()==300)&&!groupPurchaseFlag) {
            calculateCoupon();
        }
        // 如果参与团购活动，则不再对订单做其他非团购活动的计算
        if (!groupPurchaseFlag) {
            calculatePromotions();
        }

        // 如果参与团购活动，则不再计算优惠券
        if ((this.param.getCouponRule()!=null&&this.param.getCouponRule().getCouponType()!=300)&&!groupPurchaseFlag) {
            calculateCoupon();
        }



        setRuleViewForGiftResults();
        setToMoneyRules();
        sortGiftResultBySendNum();

        return Optional.ofNullable(this.result);
    }



    /* -- InnerClass -- */

    @SuppressWarnings({"WeakerAccess", "unused"})
    public static class Param {

        private Integer siteId;

        private Integer memberId;

        /**
         * 订单商品信息
         */
        private List<GoodsData> goodsDataList;


        /* -- 顾客使用优惠券参数相关 -- */

        private boolean hasUseCoupon;

        private Integer couponDetailId;

        private CouponRule couponRule = null;

        private CouponDetail couponDetail= null;


        /* -- 顾客下单拼团参数相关 -- */

        private GroupPurchaseForBeforeOrder groupPurchaseForBeforeOrder;


        /* -- 下单时间 -- */

        private LocalDateTime orderTime = LocalDateTime.now();


        /* -- 下单渠道 -- */

        /**
         * 100门店后台 101门店助手 102Pc站 103微信商城 104支付宝 105线下
         */
        private String applyChannel;


        /* -- 下单方式和地区参数 -- */

        /**
         * 100自提订单 200送货上门 300门店直购
         */
        private String orderType;

        /**
         * 收货人市区ID
         */
        private String receiverCityCode;

        /**
         * 当id为0时，表示被分配到总店
         */
        private Integer storeId;

        /**
         * 订单运费
         */
        private Integer freight = 0;


        /* -- 业务无关参数 -- */

        private ServletContext servletContext;


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

        public boolean isHasUseCoupon() {
            return hasUseCoupon;
        }

        public void setHasUseCoupon(boolean hasUseCoupon) {
            this.hasUseCoupon = hasUseCoupon;
        }

        public Integer getCouponDetailId() {
            return couponDetailId;
        }

        public void setCouponDetailId(Integer couponDetailId) {
            this.couponDetailId = couponDetailId;
        }

        public CouponRule getCouponRule() {
            return couponRule;
        }

        public void setCouponRule(CouponRule couponRule) {
            this.couponRule = couponRule;
        }

        public void setCouponDetail(CouponDetail couponDetail) {
            this.couponDetail = couponDetail;
        }

        public CouponDetail getCouponDetail() {
            return couponDetail;
        }

        public GroupPurchaseForBeforeOrder getGroupPurchaseForBeforeOrder() {
            return groupPurchaseForBeforeOrder;
        }

        public void setGroupPurchaseForBeforeOrder(GroupPurchaseForBeforeOrder groupPurchaseForBeforeOrder) {
            this.groupPurchaseForBeforeOrder = groupPurchaseForBeforeOrder;
        }

        public LocalDateTime getOrderTime() {
            return orderTime;
        }

        public void setOrderTime(LocalDateTime orderTime) {
            this.orderTime = orderTime;
        }

        public String getApplyChannel() {
            return applyChannel;
        }

        public void setApplyChannel(String applyChannel) {
            this.applyChannel = applyChannel;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public String getReceiverCityCode() {
            return receiverCityCode;
        }

        public void setReceiverCityCode(String receiverCityCode) {
            this.receiverCityCode = receiverCityCode;
        }

        public Integer getStoreId() {
            return storeId;
        }

        public void setStoreId(Integer storeId) {
            this.storeId = storeId;
        }

        public Integer getFreight() {
            return freight;
        }

        public void setFreight(Integer freight) {
            this.freight = freight;
        }

        public ServletContext getServletContext() {
            return servletContext;
        }

        public void setServletContext(ServletContext servletContext) {
            this.servletContext = servletContext;
        }
    }

    private static class GiftMsgComparator implements Comparator<GiftMsg> {

        @Override
        public int compare(GiftMsg o1, GiftMsg o2) {
            return Integer.compare(o2.getSendNum(), o1.getSendNum());
        }
    }

    /* -- private method -- */

    /**
     * 通过spring的BeanFactory手动活动bean对象
     *
     * @param sc
     */
    private void getSpringBeans(ServletContext sc) {
        BeanFactory beanFactory = WebApplicationContextUtils.getWebApplicationContext(sc);

        // mapper
        couponDetailMapper = beanFactory.getBean(CouponDetailMapper.class);
        couponRuleMapper = beanFactory.getBean(CouponRuleMapper.class);
        couponActivityMapper = beanFactory.getBean(CouponActivityMapper.class);

        // service
        promotionsActivityService = beanFactory.getBean(PromotionsActivityService.class);
        concessionResultHandler = beanFactory.getBean(ConcessionResultHandler.class);
    }


    /**
     * 参数是否正确的判断
     *
     * @return true 参数正确，足够用于后续优惠的筛选和计算
     * false 参数错误，不够用于后续优惠的筛选和计算
     */
    @SuppressWarnings("RedundantIfStatement")
    private boolean checkParam() {
        if (param.getSiteId() == null) return false;

        if (param.getMemberId() == null) return false;

        if (param.getOrderType() == null) return false;

        if (StringUtils.isBlank(param.getApplyChannel()) || "0".equals(param.getApplyChannel())) return false;

        // 送货上门 无收货人市区信息
        if ("200".equals(param.getOrderType()) && param.getReceiverCityCode() == null)
            return false;

        // 找不到券信息
        if (param.hasUseCoupon && param.getCouponDetailId() == null)
            return false;

        return true;
    }


    /**
     * 通过参数的商品信息{@link Param#goodsDataList}设置结果的商品信息{@link Result#goodsConcession}和其他{@link ConcessionCalculateBaseImpl#goodsIdsCanUsePromotions}
     */
    private void setGoodsInfoForResultAndOthers() {
        Map<Integer, GoodsDataForResult> map = new HashMap<>();

        param.getGoodsDataList().stream()
            .filter(gd -> gd.getNum() * gd.getShopPrice() > 0)
            .forEach(gd -> {
                GoodsDataForResult goodsData = new GoodsDataForResult();
                goodsData.setGoodsId(gd.getGoodsId());
                goodsData.setNum(gd.getNum());
                goodsData.setShopPrice(gd.getShopPrice());

                map.put(gd.getGoodsId(), goodsData);
                goodsIdsCanUsePromotions.add(gd.getGoodsId());
            });

        result.setGoodsConcession(map);
    }


    private boolean calculateGroupPurchasePromotions() {
        boolean groupPurchaseFlag = false;

        GroupPurchaseForBeforeOrder groupPurchaseForBeforeOrder = param.getGroupPurchaseForBeforeOrder();
        if (groupPurchaseForBeforeOrder != null) {
            groupPurchaseFlag = true;

            if (checkForGroupPurchase(groupPurchaseForBeforeOrder)) {
                Optional<PromotionsDecorator> optional = buildPromotionsDecoratorForGroupPurchase();

                if (optional.isPresent()) {
                    PromotionsDecorator promotionsDecorator = optional.get();

                    if (promotionsDecorator.filter()) {
                        promotionsDecorator.concession();
                        PromotionsDecorator.Result result = promotionsDecorator.getResult();
                        buildGroupPurchaseResult(result, groupPurchaseForBeforeOrder);
                    } else {
                        String errorMsg = promotionsDecorator.getResult().getErrorMsg();
                        if (StringUtils.isNotBlank(errorMsg))
                            throw new RuntimeException("不符合规则:" + errorMsg);
                        else
                            throw new RuntimeException("不符合参加团购的规则");
                    }
                } else
                    throw new RuntimeException("参数无法构建");
            } else
                throw new ParamErrorException();
        }

        return groupPurchaseFlag;
    }


    /**
     * 商品id一致性校验，包括参数中的商品id，剩余需要计算的商品id，团购活动参数的商品id
     *
     * @param groupPurchaseForBeforeOrder
     * @return
     */
    private boolean checkForGroupPurchase(GroupPurchaseForBeforeOrder groupPurchaseForBeforeOrder) {
        try {
            List<GoodsData> goodsDataList = param.getGoodsDataList();
            if (goodsDataList.size() == 1 && goodsIdsCanUsePromotions.size() == 1) {
                GoodsData goodsData = goodsDataList.get(0);
                Integer goodsId = goodsData.getGoodsId();

                return goodsId.equals(groupPurchaseForBeforeOrder.getGoodsId()) && goodsIdsCanUsePromotions.contains(goodsId);
            } else
                return false;
        } catch (Exception e) {
            logger.error("发生异常，{}", e);
            return false;
        }
    }


    /**
     * 构建团购活动用参数
     *
     * @return
     */
    private Optional<PromotionsDecorator> buildPromotionsDecoratorForGroupPurchase() {
        Optional<PromotionsDecorator> promotionsDecoratorOptional = buildParamToPromotionsForFilter();

        if (promotionsDecoratorOptional.isPresent()) {
            PromotionsDecorator promotionsDecorator = promotionsDecoratorOptional.get();
            GroupPurchaseForBeforeOrder groupPurchaseForBeforeOrder = param.getGroupPurchaseForBeforeOrder();
            Integer promotionsActivityId = groupPurchaseForBeforeOrder.getPromotionsActivityId();
            Optional<PromotionsActivity> optional = promotionsActivityService.getActivePromotionsActivityWithPromotionsRule(param.getSiteId(), promotionsActivityId);

            if (optional.isPresent()) {
                PromotionsActivity promotionsActivity = optional.get();
                promotionsDecorator.setPromotionsActivity(promotionsActivity);
                promotionsDecorator.setPromotionsRule(promotionsActivity.getPromotionsRule());
            } else {
                return Optional.empty();
            }

            return Optional.of(promotionsDecorator);
        } else {
            return Optional.empty();
        }
    }


    /**
     * 构建promotionsDecorator用于过滤，这次构建不加入promotionsRule和promotionsActivity参数
     *
     * @return
     */
    @SuppressWarnings("ConstantConditions")
    private Optional<PromotionsDecorator> buildParamToPromotionsForFilter() {
        Integer siteId = checkNotNull(param.getSiteId());

        PromotionsDecorator.Param promotionsParam = new PromotionsDecorator.Param();
        promotionsParam.setSiteId(siteId);
        promotionsParam.setMemberId(param.getMemberId());

        if (!setGoodsDataForPromotionsParam(promotionsParam))
            return Optional.empty();

        promotionsParam.setFreight(param.getFreight());
        promotionsParam.setOrderTime(param.getOrderTime());
        promotionsParam.setOrderType(param.getOrderType());
        promotionsParam.setStoreId(param.getStoreId());
        promotionsParam.setReceiverCityCode(param.getReceiverCityCode());
        promotionsParam.setApplyChannel(param.getApplyChannel());

        return Optional.of(new PromotionsDecorator(null, null, promotionsParam, param.getServletContext()));
    }


    private boolean setGoodsDataForPromotionsParam(PromotionsDecorator.Param promotionsParam) {
        List<PromotionsDecorator.GoodsData> goodsDataList = param.getGoodsDataList().stream()
            .filter(gd -> goodsIdsCanUsePromotions.contains(gd.getGoodsId()))
            .filter(gd -> gd.getShopPrice() * gd.getNum() > 0)
            .map(this::getGoodsData)
            .collect(toList());

        promotionsParam.setGoodsData(goodsDataList);

        return goodsDataList.size() != 0;
    }


    /**
     * 把团购活动计算结果计入总输出结果
     *
     * @param result
     * @param groupPurchaseForBeforeOrder
     */
    private void buildGroupPurchaseResult(PromotionsDecorator.Result result, GroupPurchaseForBeforeOrder groupPurchaseForBeforeOrder) {
        if (result.getResultMark() == 3)  // 参加团购活动失败，直接报错
            throw new RuntimeException("团购活动计算失败");
        else {
            this.result.setUsePromotions(true);
            this.result.setPromotionsDiscount(result.getDiscount());
            this.result.setEfficientPromotionsActivityId(StringUtil.addIdSeparateWithComma(this.result.getEfficientPromotionsActivityId(),
                groupPurchaseForBeforeOrder.getPromotionsActivityId().toString()));

            Integer goodsId = groupPurchaseForBeforeOrder.getGoodsId();
            Optional<GoodsDataForResult> optional = Optional.ofNullable(this.result.getGoodsConcession().get(goodsId));

            if (optional.isPresent()) {
                GoodsDataForResult goodsDataForResult = optional.get();
                goodsDataForResult.setDiscount(result.getDiscount());

                Map<Integer, PromotionsResult> promotionsRemark = goodsDataForResult.getPromotionsRemark();
                PromotionsResult promotionsResult = promotionsRemark.get(PROMOTIONS_RULE_TYPE_GROUP_BOOKING);

                if (promotionsResult == null) {
                    promotionsResult = new PromotionsResult();
                    promotionsResult.setPromotionsRuleType(PROMOTIONS_RULE_TYPE_GROUP_BOOKING);
                    promotionsResult.setPromotionsActivityId(groupPurchaseForBeforeOrder.getPromotionsActivityId());
                    promotionsResult.setConcessionType(1);
                    promotionsResult.setDiscount(result.getDiscount());

                    promotionsRemark.put(PROMOTIONS_RULE_TYPE_GROUP_BOOKING, promotionsResult);
                } else
                    throw new RuntimeException("一个订单只会参加一个团购活动");
            } else
                throw new RuntimeException("所有商品在计算前都已经录入结果，不可能查询不到");

        }
    }


    private void calculateCoupon() {
        // 优惠券计算
        CouponDecorator.Result couponResult = null;
        if (param.hasUseCoupon) {
            // 是则根据参数组建优惠券计算参数
            CouponDecorator couponDecorator = buildParamToCoupon();
            // 筛选，计算，根据结果组建活动用参数
            if (couponDecorator.filter()) {
                couponDecorator.concession();
                couponResult = couponDecorator.getResult();
            }
        }

        // 优惠券结果处理，同时也是活动计算的开始
        gatherDataFromCouponResult(couponResult);
    }


    private CouponDecorator buildParamToCoupon() {
        Integer siteId = checkNotNull(param.getSiteId());
        //CouponDetail couponDetail = checkNotNull(couponDetailMapper.getCouponDetailByCouponId(siteId, checkNotNull(param.getCouponDetailId())));
        //CouponRule couponRule = checkNotNull(couponRuleMapper.getByCouponId(couponDetail.getId(), siteId));
        CouponActivity couponActivity = checkNotNull(couponActivityMapper.getCouponActivity(siteId, Integer.parseInt(this.param.getCouponDetail().getSource())));
        //this.param.setCouponRule(couponRule);

        CouponDecorator.Param couponParam = new CouponDecorator.Param();
        couponParam.setSiteId(siteId);
        couponParam.setMemberId(param.getMemberId());
        setGoodsDataListForCouponParam(couponParam);
        couponParam.setFreight(checkNotNull(param.getFreight()));
        couponParam.setOrderTime(param.getOrderTime());
        couponParam.setOrderType(checkNotNull(param.getOrderType()));
        couponParam.setStoreId(param.getStoreId());
        couponParam.setReceiverCityCode(param.getReceiverCityCode());
        couponParam.setApplyChannel(param.getApplyChannel());

        return new CouponDecorator(this.param.getCouponDetail(), this.param.getCouponRule(), couponActivity, couponParam, param.getServletContext());
    }


    private void setGoodsDataListForCouponParam(CouponDecorator.Param couponParam) {
        List<CouponDecorator.GoodsData> goodsDataList = param.getGoodsDataList().stream()
            .filter(goodsData -> goodsData.getShopPrice() * goodsData.getNum() > 0)
             //查询参加活动的商品，是否可以使用优惠券
            /*.filter(goodsData -> this.result.getGoodsConcession().get(goodsData.getGoodsId()).isCanUseCoupon())
                .filter(goodsData -> { // 参加活动的商品不能使用限价劵
                    Map<Integer, GoodsDataForResult> goodsConcession=this.result.getGoodsConcession();
                    Map<Integer, PromotionsResult> promotionsRemark = goodsConcession.get(goodsData.getGoodsId()).getPromotionsRemark();
                    Optional<CouponRule> optionalCouponRule = Optional.ofNullable(this.param.getCouponRule());
                    if (promotionsRemark.size()!=0 && optionalCouponRule.get().getCouponType()==300) {
                        return false;
                    } else {
                        return true;
                    }
                })*/
            .map(goodsData -> {
                CouponDecorator.GoodsData data = new CouponDecorator.GoodsData();
                data.setGoodsId(checkNotNull(goodsData.getGoodsId()));
                data.setNum(checkNotNull(goodsData.getNum()));
                data.setShopPrice(checkNotNull(goodsData.getShopPrice()));
                data.setUseCoupon(false);
                data.setDiscount(this.result.getGoodsConcession().get(goodsData.getGoodsId()).getDiscount());
                return data;
            }).collect(toList());

        couponParam.setGoodsDataList(goodsDataList);
    }


    /**
     * 把优惠券计算结果转换到{@link ConcessionCalculateBaseImpl#result}
     *
     * @param result
     */
    private void gatherDataFromCouponResult(CouponDecorator.Result result) {
        // 处理结果
        boolean useCoupon = false;
        if (result != null) {
            useCoupon = checkCouponResult(result);
        }

        if (useCoupon) {
            switch (result.getResultMark()) {
                case 1: // 优惠商品金额
                    this.result.setUseCoupon(true);
                    this.result.setCouponConcessionRemark(1);
                    this.result.setCouponDiscount(result.getCouponDiscount()+result.getDiscount());
                    this.result.setCouponDetailId(param.getCouponDetailId());

                    setCouponGoodsResultToTotalResult(result);

                    break;

                case 2: // 满赠
                    this.result.setUseCoupon(true);
                    this.result.setCouponConcessionRemark(2);
                    this.result.setCouponDetailId(param.getCouponDetailId());
                    this.result.setCouponDiscount(result.getDiscount());

                    setCouponGoodsResultToTotalResult(result);

                    GiftResult giftResult = result.getGiftResult();
                    setCouponDescForGiftResult(giftResult);
                    List<GiftResult> giftResultList = this.result.getGiftResults();
                    giftResultList.add(giftResult);

                    break;

                default:
                    throw new UnknownTypeException();
            }

            removeGoodsAndPromotionsByCoupon(result);

        } else { // 没有使用优惠券
            this.result.setUseCoupon(false);
        }
    }

    /**
     * 校验该商品是否使用了优惠券和优惠券计算是否正确
     *
     * @param result
     * @return true 表示使用了优惠券并且计算正确，其他都是false
     */
    private boolean checkCouponResult(CouponDecorator.Result result) {
        switch (result.getResultMark()) {
            case 1:
                List<CouponDecorator.GoodsData> goodsDataList = result.getGoodsData().stream()
                    .filter(CouponDecorator.GoodsData::getUseCoupon)
                    .collect(toList());

                if (goodsDataList.size() == 0)
                    return false;

                Integer totalDiscount = goodsDataList.stream()
                    .map(CouponDecorator.GoodsData::getCouponDiscount)
                    .reduce(0, Integer::sum);

                // if (result.getDiscount().equals(totalDiscount)) {
                if (totalDiscount >= result.getCouponDiscount()) {
                        result.setGoodsData(goodsDataList);
                    return true;
                } else
                    return false;

            case 2:
                long count = result.getGoodsData().stream()
                    .filter(CouponDecorator.GoodsData::getUseCoupon)
                    .count();

                if (Long.valueOf(0L).equals(count))
                    return false;

                GiftResult giftResult = result.getGiftResult();
                return checkGiftRule(giftResult);

            case 3:
                return false;

            default:
                throw new UnknownTypeException();
        }
    }


    private boolean checkGiftRule(GiftResult giftResult) {
        List<GiftMsg> giftList = giftResult.getGiftList();

        if (giftList.size() == 0)
            return false;

        if (giftResult.getMaxSendNum() == 0)
            return false;

        Integer totalSendNum = giftList.stream()
            .map(GiftMsg::getSendNum)
            .reduce(0, Integer::sum);

        return totalSendNum >= giftResult.getMaxSendNum();
    }


    private void setCouponGoodsResultToTotalResult(CouponDecorator.Result result) {
        result.getGoodsData().stream()
            .filter(CouponDecorator.GoodsData::getUseCoupon)
            .forEach(goodsData -> {
                Optional<GoodsDataForResult> optional = Optional.ofNullable(this.result.getGoodsConcession().get(goodsData.getGoodsId()));

                if (optional.isPresent()) {
                    GoodsDataForResult data = optional.get();
                    data.setUseCoupon(goodsData.getUseCoupon());
                    data.setCouponDiscount(goodsData.getCouponDiscount());

                    // 设置该商品总计参加了多少优惠
                    data.setDiscount(goodsData.getDiscount() + goodsData.getCouponDiscount());
                } else
                    throw new RuntimeException("所有商品在计算前都已经录入结果，不可能查询不到");
            });
    }


    private void setCouponDescForGiftResult(GiftResult giftResult) {
        ConcessionDesc concessionDesc = new ConcessionDesc();
        concessionDesc.setConcessionType(ConcessionConstant.COUPON);
        concessionDesc.setCouponDetailId(this.param.getCouponDetailId());
        giftResult.setConcessionDesc(concessionDesc);
    }


    /**
     * 从待计算的商品{@link ConcessionCalculateBaseImpl#goodsIdsCanUsePromotions}中移除参加过独立优惠券的商品
     *
     * @param result
     */
    private void removeGoodsAndPromotionsByCoupon(CouponDecorator.Result result) {
        // 根据独立优惠券移除待计算的商品
        CouponRule couponRule = checkNotNull(couponRuleMapper.getByCouponId(param.getCouponDetailId(), param.getSiteId()));
        Integer isIndependentCoupon = CouponConstant.DEFAULT_INDEPENDENT.get(couponRule.getCouponType());

        if (isIndependentCoupon == CouponConstant.INDEPENDENT) {
            result.getGoodsData().stream()
                .filter(CouponDecorator.GoodsData::getUseCoupon)
                .forEach(goodsData -> goodsIdsCanUsePromotions.remove(goodsData.getGoodsId()));
        }

        /*this.promotionsActivities = this.promotionsActivities.stream()
            .filter(promotionsActivity -> Optional.ofNullable(promotionsActivity.getCanUseCoupon())
                .orElse(DEFAULT_TO_USE_COUPON.get(promotionsActivity.getPromotionsRule().getPromotionsType()))
                .equals(ALLOW_TO_USE_COUPON))
            .collect(toList());*/
    }


    private void calculatePromotions() {
        // 筛选可参加的活动（拼团全部过滤）
        if (!filterPromotionsActivities()) {
            return;
        }

        // 对活动根据独立与否，以及类型进行分组
        Map<Integer, Map<Integer, List<PromotionsActivity>>> groupPromotionsActivities = groupPromotionsActivity();

        // 独立活动计算
        if (goodsIdsCanUsePromotions.size() != 0) { // 仍然还有商品可以参与计算
            Map<Integer, List<PromotionsActivity>> independentPromotionsActivities = groupPromotionsActivities.get(INDEPENDENT);
            if (independentPromotionsActivities != null) {
                for (Integer promotionsRuleType : promotionsTypesOrder) {
                    // 根据类型分配活动，计算该订单能参加的最优的同类型的活动（商品不拆数量）
                    List<PromotionsActivity> promotionsActivities = independentPromotionsActivities.get(promotionsRuleType);
                    if (promotionsActivities != null) {
                        calculateIndependentAllForOneType(promotionsActivities, promotionsRuleType);
                    }
                }
            }
        }

        // 非独立活动计算
        if (goodsIdsCanUsePromotions.size() != 0) { // 仍然还有商品可以参与计算
            Map<Integer, List<PromotionsActivity>> dependentPromotionsActivities = groupPromotionsActivities.get(DEPENDENT);
            if (dependentPromotionsActivities != null) {
                for (Integer promotionsRuleType : promotionsTypesOrder) {
                    // 根据类型分配活动，计算该订单能参加的最优的同类型的活动（商品不拆数量）
                    List<PromotionsActivity> promotionsActivities = dependentPromotionsActivities.get(promotionsRuleType);
                    if (promotionsActivities != null) {
                        calculateDependentAllForOneType(promotionsActivities, promotionsRuleType);
                    }
                }
            }
        }

    }


    /**
     * 第一层根据是否是独立活动，
     *
     * @return
     */
    private Map<Integer, Map<Integer, List<PromotionsActivity>>> groupPromotionsActivity() {
        return promotionsActivities.stream()
            .collect(groupingBy(
                promotionsActivity -> Optional.ofNullable(promotionsActivity.getIsIndependent())
                    .orElseGet(() -> DEFAULT_INDEPENDENT.get(promotionsActivity.getPromotionsRule().getPromotionsType())),
                groupingBy(promotionsActivity -> promotionsActivity.getPromotionsRule().getPromotionsType())
            ));
    }


    /**
     * 根据商品和其他参数过滤活动，其中，比较特殊的是，过滤所有拼团活动
     *
     * @return true表示可以有商品和活动存活，可以继续计算，反之为false
     */
    private boolean filterPromotionsActivities() {
        if (goodsIdsCanUsePromotions.size() != 0) {
            Optional<PromotionsDecorator> optional = buildParamToPromotionsForFilter();

            if (optional.isPresent()) {
                PromotionsDecorator promotionsDecorator = optional.get();
                Iterator<PromotionsActivity> it = promotionsActivities.iterator();

                while (it.hasNext()) {
                    promotionsDecorator.getParam().getGoodsData().forEach(
                        goodsData -> goodsData.setUsePromotions(false)
                    );

                    PromotionsActivity promotionsActivity = it.next();

                    // 移除团购活动
                    if (promotionsActivity.getPromotionsRule().getPromotionsType().equals(PROMOTIONS_RULE_TYPE_GROUP_BOOKING)) {
                        it.remove();
                        continue;
                    }

                    // 改变活动参数，过滤
                    promotionsDecorator.setPromotionsActivity(promotionsActivity);
                    promotionsDecorator.setPromotionsRule(promotionsActivity.getPromotionsRule());
                    if (!promotionsDecorator.filter()) {
                        it.remove();
                    } else {
                        System.out.println(1111);
                    }
                }

                return promotionsActivities.size() != 0;
            } else {
                return false;
            }
        } else
            return false;
    }


    /**
     * 根据可计算的商品和同类型独立活动计算最优
     *
     * @param promotionsActivityList
     * @param promotionsRuleType
     */
    private void calculateIndependentAllForOneType(List<PromotionsActivity> promotionsActivityList, Integer promotionsRuleType) {
        // 循环直至无活动可以参加
        Set<PromotionsActivity> promotionsActivitySet = new HashSet<>(promotionsActivityList);
        List<PromotionsActivity> giftPromotionsActivityList = new ArrayList<>();

        // 赠品计算，组合为先
        buildActivityListForGiftPromotions(promotionsActivityList, promotionsRuleType, giftPromotionsActivityList);

        // 单一类型活动计算
        if (goodsIdsCanUsePromotions.size() != 0) {
            while ((!promotionsRuleType.equals(PROMOTIONS_RULE_TYPE_GIFT) && promotionsActivitySet.size() != 0)
                || (promotionsRuleType.equals(PROMOTIONS_RULE_TYPE_GIFT) && giftPromotionsActivityList.size() != 0)) {
                Iterator<PromotionsActivity> it1 = promotionsActivitySet.iterator();
                Iterator<PromotionsActivity> it2 = giftPromotionsActivityList.iterator();

                // 邮费只优惠一次
                if (promotionsRuleType.equals(PROMOTIONS_RULE_TYPE_FREE_POST) && result.getPostageDiscount() > 0) {
                    break;
                }

                // 一类型求一次最优解
                switch (promotionsRuleType) { // 团购活动独立计算
                    case PROMOTIONS_RULE_TYPE_DISCOUNT:
                    case PROMOTIONS_RULE_TYPE_FREE_POST:
                    case PROMOTIONS_RULE_TYPE_MONEY_OFF:
                    case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                        calculateMaxInOneTypeAndIndependentPromotions(promotionsActivitySet, it1, promotionsRuleType);
                        break;

                    case PROMOTIONS_RULE_TYPE_GIFT:
                        calculateExistInOneTypeAndIndependentGiftPromotions(giftPromotionsActivityList, it2);
                        break;

                    default:
                        throw new UnknownTypeException();
                }
            }
        }
    }


    private void buildActivityListForGiftPromotions(List<PromotionsActivity> promotionsActivityList, Integer promotionsRuleType, List<PromotionsActivity> giftPromotionsActivityList) {
        if (promotionsRuleType.equals(PROMOTIONS_RULE_TYPE_GIFT)) {
            Map<Integer, List<PromotionsActivity>> collect = promotionsActivityList.stream()
                .filter(promotionsActivity -> promotionsActivity.getPromotionsRule().getPromotionsType().equals(PROMOTIONS_RULE_TYPE_GIFT))
                .collect(groupingBy(promotionsActivity -> {
                    GiftRule giftRule = JSON.parseObject(promotionsActivity.getPromotionsRule().getPromotionsRule(), GiftRule.class);
                    return giftRule.getCalculateBase();
                }));

            // 赠品活动组合优先计算
            List<PromotionsActivity> list = collect.get(2);
            if (list != null && list.size() != 0)
                giftPromotionsActivityList.addAll(list);

            list = collect.get(1);
            if (list != null && list.size() != 0)
                giftPromotionsActivityList.addAll(list);
        }
    }


    /**
     * 计算金额最优的独立活动，计算过程中，移除无法计算的活动和已经确定计算的商品
     *
     * @param promotionsActivitySet
     * @param it
     * @param promotionsRuleType
     */
    private void calculateMaxInOneTypeAndIndependentPromotions(Set<PromotionsActivity> promotionsActivitySet, Iterator<PromotionsActivity> it, Integer promotionsRuleType) {
        PromotionsDecorator maxDiscountPromotionsDecorator = calculateMaxDiscountForSameTypePromotions(it);

        if (maxDiscountPromotionsDecorator != null) {
            handleIndependentPromotionsResult(maxDiscountPromotionsDecorator, promotionsActivitySet, promotionsRuleType);
        }
    }


    private PromotionsDecorator calculateMaxDiscountForSameTypePromotions(Iterator<PromotionsActivity> it) {
        int maxDiscount = 0;
        PromotionsDecorator maxDiscountPromotionsDecorator = null;

        while (it.hasNext()) {
            PromotionsActivity promotionsActivity = it.next();
            Optional<PromotionsDecorator> optional = buildPromotionsDecorator(promotionsActivity);

            if (!optional.isPresent()) {
                it.remove();
                break;
            }

            PromotionsDecorator promotionsDecorator = optional.get();

            // 过滤
            boolean activityPromotions = false;
            if (promotionsDecorator.filterWithRule()) {
                // 计算
                promotionsDecorator.concession();
                PromotionsDecorator.Result result = promotionsDecorator.getResult();

                if (isCalculateRight(result, promotionsDecorator.getPromotionsRule())) {
                    activityPromotions = true;

                    // 求最优
                    if (result.getDiscount() > maxDiscount) {
                        maxDiscountPromotionsDecorator = promotionsDecorator;
                        maxDiscount = result.getDiscount();
                    }
                }
            }

            // 确定不能参加的活动应该移除
            if (!activityPromotions)
                it.remove();
        }

        return maxDiscountPromotionsDecorator;
    }


    /**
     * 构建参数用于独立活动计算，考虑同类型优惠券
     *
     * @param promotionsActivity
     * @return
     */
    private Optional<PromotionsDecorator> buildPromotionsDecorator(PromotionsActivity promotionsActivity) {
        Optional<PromotionsDecorator> optional = buildParamToPromotionsForFilter();

        if (optional.isPresent()) {
            PromotionsDecorator promotionsDecorator = optional.get();

            PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
            promotionsDecorator.setPromotionsActivity(promotionsActivity);
            promotionsDecorator.setPromotionsRule(promotionsRule);

            if (!resetGoodsDataForPromotionsParam(promotionsDecorator, promotionsRule.getPromotionsType()))
                return Optional.empty();

            // 免邮活动参数重设
            if (Integer.valueOf(PROMOTIONS_RULE_TYPE_FREE_POST).equals(promotionsRule.getPromotionsType())) {
                if (!resetFreightForFreePostagePromotionsParam(promotionsDecorator))
                    return Optional.empty();
            }

            return Optional.of(promotionsDecorator);
        } else
            return Optional.empty();
    }


    /**
     * 考虑同类型优惠券的情况下重新设置商品
     *
     * @param promotionsDecorator
     * @param promotionsType
     * @return
     */
    private boolean resetGoodsDataForPromotionsParam(PromotionsDecorator promotionsDecorator, Integer promotionsType) {
        // 如果使用优惠券
        if (this.result.isUseCoupon()) {
            List<PromotionsDecorator.GoodsData> goodsDataList = promotionsDecorator.getParam().getGoodsData().stream()
                .filter(goodsData -> goodsData.getShopPrice() * goodsData.getNum() - goodsData.getDiscount() > 0)
                .filter(goodsData -> goodsIdsCanUsePromotions.contains(goodsData.getGoodsId()))
                /*.filter(goodsData -> { // 同类型优惠券过滤
                    GoodsDataForResult goodsDataForResult = this.result.getGoodsConcession().get(goodsData.getGoodsId());
                    Optional<CouponRule> optionalCouponRule = Optional.ofNullable(this.param.getCouponRule());

                    if (goodsDataForResult.isUseCoupon() && optionalCouponRule.isPresent()) {
                        // 优惠券类型与活动类型互转
                        CouponRule couponRule = optionalCouponRule.get();
                        Integer promotionsTypeTemp = CouponConstant.COUPON_TYPE_TO_PROMOTIONS_TYPE.get(couponRule.getCouponType());

                        return promotionsTypeTemp == null || !promotionsDecorator.getPromotionsRule().getPromotionsType().equals(promotionsTypeTemp);
                    } else {
                        return true;
                    }
                })*/.collect(toList());

            promotionsDecorator.getParam().setGoodsData(goodsDataList);

            if (goodsDataList.size() == 0)
                return false;
        }

        // 如果活动已经有该类型的计算结果
        List<PromotionsDecorator.GoodsData> goodsDataList = promotionsDecorator.getParam().getGoodsData().stream()
            .filter(goodsData -> {
                GoodsDataForResult goodsDataForResult = this.result.getGoodsConcession().get(goodsData.getGoodsId());
                return goodsDataForResult == null || goodsDataForResult.getPromotionsRemark().get(promotionsType) == null;
            }).collect(toList());

        promotionsDecorator.getParam().setGoodsData(goodsDataList);

        return goodsDataList.size() != 0;
    }


    private boolean isCalculateRight(PromotionsDecorator.Result result, PromotionsRule promotionsRule) {
        boolean flag = result.getResultMark() == 1 && result.getDiscount() > 0 && result.getGoodsData().size() > 0;

        if (flag) {
            switch (promotionsRule.getPromotionsType()) {
                case PROMOTIONS_RULE_TYPE_DISCOUNT:
                case PROMOTIONS_RULE_TYPE_MONEY_OFF:
                case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                case PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                    Integer totalDiscount = result.getGoodsData().stream()
                        .filter(PromotionsDecorator.GoodsData::getUsePromotions)
                        .map(PromotionsDecorator.GoodsData::getDiscount)
                        .reduce(0, Integer::sum);

                    flag = totalDiscount.equals(result.getDiscount());
                    break;

                default:
                    // doNothing
            }
        }

        return flag;
    }


    /**
     * 处理活动运算结果，剔除已参加过的活动，以及被独立活动参加的商品
     *
     * @param maxDiscountPromotionsDecorator 已经计算过，并且计算成功的
     * @param promotionsActivitySet
     * @param promotionsRuleType
     */
    private void handleIndependentPromotionsResult(PromotionsDecorator maxDiscountPromotionsDecorator,
                                                   Set<PromotionsActivity> promotionsActivitySet,
                                                   Integer promotionsRuleType) {
        // 移除已经计算过的活动
        PromotionsActivity promotionsActivity = maxDiscountPromotionsDecorator.getPromotionsActivity();
        PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
        promotionsActivitySet.remove(promotionsActivity);

        // 根据计算结果剔除被独立活动计算过的商品以及加入结果集
        PromotionsDecorator.Result result = maxDiscountPromotionsDecorator.getResult();

        // 统计活动计算优惠结果
        this.result.setUsePromotions(true);
        this.result.setPromotionsDiscount(this.result.getPromotionsDiscount() + result.getDiscount());
        this.result.setEfficientPromotionsActivityId(StringUtil.addIdSeparateWithComma(this.result.getEfficientPromotionsActivityId(),
            promotionsActivity.getId().toString()));

        result.getGoodsData().stream()
            .filter(PromotionsDecorator.GoodsData::getUsePromotions)
            .forEach(goodsData -> {
                // 移除已经被独立活动计算过的商品
                Integer goodsId = goodsData.getGoodsId();
                goodsIdsCanUsePromotions.remove(goodsId);

                // 设置计算结果到结果集
                setToResult(promotionsActivity, promotionsRule, goodsData);
            });

        if (promotionsRuleType.equals(PROMOTIONS_RULE_TYPE_FREE_POST)) {
            if (checkFreePostagePromotionsResult(result)) {
                //将邮费优惠 放到postageDiscount中，同时将原来把邮费计算进去的discount给减掉
                this.result.setPostageDiscount(result.getDiscount());
                this.result.setPromotionsDiscount(this.result.getPromotionsDiscount() - result.getDiscount());
            }
        }
    }

    private void setToResult(PromotionsActivity promotionsActivity, PromotionsRule promotionsRule, PromotionsDecorator.GoodsData goodsData) {
        // 获取该商品的活动优惠信息
        Optional<GoodsDataForResult> optional = Optional.ofNullable(this.result.getGoodsConcession().get(goodsData.getGoodsId()));

        if (optional.isPresent()) {
            GoodsDataForResult data = optional.get();
            data.setDiscount(data.getDiscount() + goodsData.getDiscount());

            if (data.isCanUseCoupon()) {
                boolean canUseCoupon = Optional.ofNullable(promotionsActivity.getCanUseCoupon())
                    .orElse(DEFAULT_TO_USE_COUPON.get(promotionsActivity.getPromotionsRule().getPromotionsType()))
                    .equals(ALLOW_TO_USE_COUPON);
                data.setCanUseCoupon(canUseCoupon);
            }

            PromotionsResult promotionsResult = new PromotionsResult();
            promotionsResult.setConcessionType(1);
            promotionsResult.setDiscount(goodsData.getDiscount());
            promotionsResult.setPromotionsActivityId(promotionsActivity.getId());
            promotionsResult.setPromotionsRuleType(promotionsRule.getPromotionsType());

            Map<Integer, PromotionsResult> promotionsRemark = data.getPromotionsRemark();
            PromotionsResult temp_1 = promotionsRemark.get(promotionsRule.getPromotionsType());

            if (temp_1 == null)
                promotionsRemark.put(promotionsRule.getPromotionsType(), promotionsResult);
            else
                throw new RuntimeException("单商品参与的活动优惠应该只有一个，因为求最优活动优惠不拆数量");
        } else
            throw new RuntimeException("所有商品在计算前都已经录入结果，不可能查询不到");
    }

    /**
     * 检验免邮活动计算结果
     *
     * @param result
     * @return
     */
    private boolean checkFreePostagePromotionsResult(PromotionsDecorator.Result result) {
        boolean flag = false;

        if (result.getResultMark().equals(1) && result.getDiscount() > 0) {
            Integer totalGoodsDiscount = result.getGoodsData().stream()
                .filter(PromotionsDecorator.GoodsData::getUsePromotions)
                .map(PromotionsDecorator.GoodsData::getDiscount)
                .reduce(0, Integer::sum);

            if (totalGoodsDiscount.equals(0))
                flag = true;
            else
                throw new RuntimeException("免邮活动，商品优惠应该为0");
        }

        return flag;
    }


    /**
     * 计算独立满赠活动
     *
     * @param giftPromotionsActivityList
     * @param it
     */
    private void calculateExistInOneTypeAndIndependentGiftPromotions(List<PromotionsActivity> giftPromotionsActivityList, Iterator<PromotionsActivity> it) {
        PromotionsDecorator workGiftPromotionsDecorator = getActiveGiftPromotionsDecorator(it);

        // 处理结果和参数
        if (workGiftPromotionsDecorator != null) {
            handleIndependentGiftPromotionsResult(workGiftPromotionsDecorator, giftPromotionsActivityList);
        }
    }


    @Nullable
    private PromotionsDecorator getActiveGiftPromotionsDecorator(Iterator<PromotionsActivity> it) {
        PromotionsDecorator workGiftPromotionsDecorator = null;

        while (it.hasNext()) {

            // 构建PromotionsDecorator，不考虑复用对象
            PromotionsActivity promotionsActivity = it.next();
            Optional<PromotionsDecorator> optional = buildPromotionsDecorator(promotionsActivity);

            if (!optional.isPresent()) {
                it.remove();
                continue;
            }

            PromotionsDecorator promotionsDecorator = optional.get();

            // 过滤
            if (promotionsDecorator.filterWithRule()) {
                promotionsDecorator.concession();
                PromotionsDecorator.Result result = promotionsDecorator.getResult();

                if (result.getResultMark() == 2 && result.getGiftResult() != null && checkGiftPromotionsResult(result)) {
                    workGiftPromotionsDecorator = promotionsDecorator;
                    break;
                }
            }

            it.remove();
        }

        return workGiftPromotionsDecorator;
    }


    private boolean checkGiftPromotionsResult(PromotionsDecorator.Result result) {
        long count = result.getGoodsData().stream()
            .filter(PromotionsDecorator.GoodsData::getUsePromotions)
            .count();

        if (Long.valueOf(0L).equals(count))
            return false;

        GiftResult giftResult = result.getGiftResult();

        return checkGiftRule(giftResult);
    }


    /**
     * 处理活动运算结果，剔除已参加过的活动，以及被独立活动参加的商品
     *
     * @param workGiftPromotionsDecorator
     * @param giftPromotionsActivityList
     */
    private void handleIndependentGiftPromotionsResult(PromotionsDecorator workGiftPromotionsDecorator, List<PromotionsActivity> giftPromotionsActivityList) {
        // 移除已经计算过的活动
        PromotionsActivity promotionsActivity = workGiftPromotionsDecorator.getPromotionsActivity();
        PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
        giftPromotionsActivityList.remove(promotionsActivity);

        // 根据计算结果剔除被独立活动计算过的商品以及加入结果集
        PromotionsDecorator.Result result = workGiftPromotionsDecorator.getResult();

        // 统计活动计算优惠结果
        this.result.setUsePromotions(true);
        this.result.setEfficientPromotionsActivityId(StringUtil.addIdSeparateWithComma(this.result.getEfficientPromotionsActivityId(),
            promotionsActivity.getId().toString()));

        result.getGoodsData().stream()
            .filter(PromotionsDecorator.GoodsData::getUsePromotions)
            .forEach(goodsData -> {
                // 移除已经被独立活动计算过的商品
                Integer goodsId = goodsData.getGoodsId();
                goodsIdsCanUsePromotions.remove(goodsId);

                // 设置计算结果到结果集
                setToGiftResult(promotionsActivity, promotionsRule, goodsData);
            });

        GiftResult giftResult = result.getGiftResult();
        setPromotionsDescForGiftRule(promotionsActivity, giftResult);
        this.result.getGiftResults().add(giftResult);
    }


    private void setToGiftResult(PromotionsActivity promotionsActivity, PromotionsRule promotionsRule, PromotionsDecorator.GoodsData goodsData) {
        // 获取该商品的活动优惠信息
        Optional<GoodsDataForResult> optional = Optional.ofNullable(this.result.getGoodsConcession().get(goodsData.getGoodsId()));

        if (optional.isPresent()) {
            GoodsDataForResult data = optional.get();

            if (data.isCanUseCoupon()) {
                boolean canUseCoupon = Optional.ofNullable(promotionsActivity.getCanUseCoupon())
                    .orElse(DEFAULT_TO_USE_COUPON.get(promotionsActivity.getPromotionsRule().getPromotionsType()))
                    .equals(ALLOW_TO_USE_COUPON);
                data.setCanUseCoupon(canUseCoupon);
            }

            PromotionsResult promotionsResult = new PromotionsResult();
            promotionsResult.setConcessionType(1);
            promotionsResult.setDiscount(0);
            promotionsResult.setPromotionsActivityId(promotionsActivity.getId());
            promotionsResult.setPromotionsRuleType(promotionsRule.getPromotionsType());

            Map<Integer, PromotionsResult> promotionsRemark = data.getPromotionsRemark();
            PromotionsResult temp_1 = promotionsRemark.get(promotionsRule.getPromotionsType());

            if (temp_1 == null)
                promotionsRemark.put(promotionsRule.getPromotionsType(), promotionsResult);
            else
                throw new RuntimeException("单商品参与的活动优惠应该只有一个，因为求最优活动优惠不拆数量");

        } else
            throw new RuntimeException("所有商品在计算前都已经录入结果，不可能查询不到");
    }


    private void setPromotionsDescForGiftRule(PromotionsActivity promotionsActivity, GiftResult giftResult) {
        ConcessionDesc concessionDesc = new ConcessionDesc();
        concessionDesc.setConcessionType(ConcessionConstant.PROMOTIONS);
        concessionDesc.setPromotionsActivityId(promotionsActivity.getId());
        giftResult.setConcessionDesc(concessionDesc);
    }


    /**
     * 计算金额最优的非独立活动，计算过程中，移除无法计算的活动
     *
     * @param promotionsActivitySet
     * @param it
     * @param promotionsRuleType
     */
    private void calculateMaxInOneTypeAndDependentPromotions(Set<PromotionsActivity> promotionsActivitySet, Iterator<PromotionsActivity> it, Integer promotionsRuleType) {
        //同类型中（如打折活动），最优的活动
        PromotionsDecorator maxDiscountPromotionsDecorator = calculateMaxDiscountForSameTypePromotions(it);
        //对最优的活动进行优惠处理
        if (maxDiscountPromotionsDecorator != null) {
            handleDependentPromotionsResult(maxDiscountPromotionsDecorator, promotionsActivitySet, promotionsRuleType);
        }
    }


    /**
     * 处理活动运算结果，剔除已参加过的活动
     *
     * @param maxDiscountPromotionsDecorator
     * @param promotionsActivitySet
     * @param promotionsRuleType
     */
    private void handleDependentPromotionsResult(PromotionsDecorator maxDiscountPromotionsDecorator, Set<PromotionsActivity> promotionsActivitySet, Integer promotionsRuleType) {
        // 移除已经计算过的活动
        PromotionsActivity promotionsActivity = maxDiscountPromotionsDecorator.getPromotionsActivity();
        PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
        promotionsActivitySet.remove(promotionsActivity);

        // 根据计算结果剔除被独立活动计算过的商品以及加入结果集
        PromotionsDecorator.Result result = maxDiscountPromotionsDecorator.getResult();

        // 统计活动计算优惠结果
        this.result.setUsePromotions(true);

        this.result.setPromotionsDiscount(this.result.getPromotionsDiscount() + result.getDiscount());
        this.result.setEfficientPromotionsActivityId(StringUtil.addIdSeparateWithComma(this.result.getEfficientPromotionsActivityId(),
            promotionsActivity.getId().toString()));

        result.getGoodsData().stream()
            .filter(PromotionsDecorator.GoodsData::getUsePromotions)
            .forEach(goodsData -> {
                // 设置计算结果到结果集
                setToResult(promotionsActivity, promotionsRule, goodsData);
            });

        if (promotionsRuleType.equals(PROMOTIONS_RULE_TYPE_FREE_POST)) {
            if (checkFreePostagePromotionsResult(result)) {
                //将邮费优惠 放到postageDiscount中，同时将原来把邮费计算进去的discount给减掉
                this.result.setPostageDiscount(result.getDiscount());
                this.result.setPromotionsDiscount(this.result.getPromotionsDiscount() - result.getDiscount());
            }
        }
    }


    private void calculateExistInOneTypeAndDependentGiftPromotions(List<PromotionsActivity> giftPromotionsActivityList, Iterator<PromotionsActivity> it) {
        PromotionsDecorator workGiftPromotionsDecorator = getActiveGiftPromotionsDecorator(it);

        // 处理结果和参数
        if (workGiftPromotionsDecorator != null) {
            handleDependentGiftPromotionsResult(workGiftPromotionsDecorator, giftPromotionsActivityList);
        }
    }


    private void handleDependentGiftPromotionsResult(PromotionsDecorator workGiftPromotionsDecorator, List<PromotionsActivity> giftPromotionsActivityList) {
        // 移除已经计算过的活动
        PromotionsActivity promotionsActivity = workGiftPromotionsDecorator.getPromotionsActivity();
        PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
        giftPromotionsActivityList.remove(promotionsActivity);

        // 根据计算结果剔除被独立活动计算过的商品以及加入结果集
        PromotionsDecorator.Result result = workGiftPromotionsDecorator.getResult();

        // 统计活动计算优惠结果
        this.result.setUsePromotions(true);
        this.result.setEfficientPromotionsActivityId(StringUtil.addIdSeparateWithComma(this.result.getEfficientPromotionsActivityId(),
            promotionsActivity.getId().toString()));

        result.getGoodsData().stream()
            .filter(PromotionsDecorator.GoodsData::getUsePromotions)
            .forEach(goodsData -> {
                // 设置计算结果到结果集
                setToGiftResult(promotionsActivity, promotionsRule, goodsData);
            });

        GiftResult giftResult = result.getGiftResult();
        setPromotionsDescForGiftRule(promotionsActivity, giftResult);
        this.result.getGiftResults().add(giftResult);
    }


    /**
     * 根据可计算的商品和同类型非独立活动计算最优，遵循商品不拆数量原则
     *
     * @param promotionsActivityList
     * @param promotionsRuleType
     */
    private void calculateDependentAllForOneType(List<PromotionsActivity> promotionsActivityList, Integer promotionsRuleType) {
        // 循环直至无活动可以参加
        Set<PromotionsActivity> promotionsActivitySet = new HashSet<>(promotionsActivityList);
        List<PromotionsActivity> giftPromotionsActivityList = new ArrayList<>();

        // 赠品计算，组合为先
        buildActivityListForGiftPromotions(promotionsActivityList, promotionsRuleType, giftPromotionsActivityList);

        if (goodsIdsCanUsePromotions.size() != 0) { // 非独立活动计算不再剔除商品，所以只判断一次
            while ((!promotionsRuleType.equals(PROMOTIONS_RULE_TYPE_GIFT) && promotionsActivitySet.size() != 0)
                || (promotionsRuleType.equals(PROMOTIONS_RULE_TYPE_GIFT) && giftPromotionsActivityList.size() != 0)) {
                Iterator<PromotionsActivity> it = promotionsActivitySet.iterator();
                Iterator<PromotionsActivity> it2 = giftPromotionsActivityList.iterator();

                // 邮费只优惠一次
                if (promotionsRuleType.equals(PROMOTIONS_RULE_TYPE_FREE_POST) && result.getPostageDiscount() > 0) {
                    break;
                }

                switch (promotionsRuleType) {
                    case PROMOTIONS_RULE_TYPE_DISCOUNT:
                    case PROMOTIONS_RULE_TYPE_FREE_POST:
                    case PROMOTIONS_RULE_TYPE_MONEY_OFF:
                    case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                        calculateMaxInOneTypeAndDependentPromotions(promotionsActivitySet, it, promotionsRuleType);

                        break;

                    case PROMOTIONS_RULE_TYPE_GIFT:
                        calculateExistInOneTypeAndDependentGiftPromotions(giftPromotionsActivityList, it2);
                        break;

                    default: // 团购活动，限价活动不存在非独立
                        throw new UnknownTypeException();
                }
            }
        }
    }


    /**
     * 为免邮活动重设邮费参数
     *
     * @param promotionsDecorator
     * @return
     */
    private boolean resetFreightForFreePostagePromotionsParam(PromotionsDecorator promotionsDecorator) {
        Collection<GoodsDataForResult> values = this.result.getGoodsConcession().values();
        Integer totalFreePostage = values.stream()
            .map(goodsDataForResult -> {
                PromotionsResult promotionsResult = goodsDataForResult.getPromotionsRemark().get(PROMOTIONS_RULE_TYPE_FREE_POST);
                if (promotionsResult == null || promotionsResult.getDiscount() == null)
                    return 0;
                else
                    return promotionsResult.getDiscount();
            }).reduce(0, Integer::sum);

        int leftFreight = this.param.getFreight() - totalFreePostage;
        if (leftFreight < 0)
            throw new RuntimeException();
        else if (leftFreight == 0)
            return false;
        else {
            promotionsDecorator.getParam().setFreight(leftFreight);
            return true;
        }
    }


    private PromotionsDecorator.GoodsData getGoodsData(GoodsData goodsData) {
        PromotionsDecorator.GoodsData gd = new PromotionsDecorator.GoodsData();

        gd.setGoodsId(goodsData.getGoodsId());
        gd.setNum(goodsData.getNum());
        gd.setShopPrice(goodsData.getShopPrice());
        gd.setUsePromotions(false);
        gd.setDiscount(this.result.getGoodsConcession().get(goodsData.getGoodsId()).getDiscount());

        return gd;
    }


    private void setRuleViewForGiftResults() {
        concessionResultHandler.setRuleViewForGiftResult(this.param.getSiteId(), this.result.getGiftResults());
    }

    private void setToMoneyRules() {
        Map<Integer, GoodsDataForResult> goodsConcession = this.result.getGoodsConcession();
        Map<Integer, MoneyResultForPromotions> map = new HashMap<>();

        Map<Integer, PromotionsActivity> activityMap = promotionsActivities.stream()
            .collect(Collectors.toMap(PromotionsActivity::getId, promotionsActivity -> promotionsActivity));

        for (Map.Entry<Integer, GoodsDataForResult> e1 : goodsConcession.entrySet()) {
            GoodsDataForResult goodsDataForResult = e1.getValue();

            Map<Integer, PromotionsResult> promotionsRemark = goodsDataForResult.getPromotionsRemark();

            for (Map.Entry<Integer, PromotionsResult> e2 : promotionsRemark.entrySet()) {
                PromotionsResult promotionsResult = e2.getValue();

                if (Integer.valueOf(1).equals(promotionsResult.getConcessionType())) {
                    Integer promotionsActivityId = promotionsResult.getPromotionsActivityId();
                    MoneyResultForPromotions value = map.get(promotionsActivityId);

                    if (value == null) {
                        PromotionsActivity promotionsActivity = activityMap.get(promotionsActivityId);
                        PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
                        value = new MoneyResultForPromotions();
                        value.setPromotionsActivityId(promotionsActivityId);
                        value.setDiscount(promotionsResult.getDiscount());
                        value.setPromotionsName(promotionsActivity.getTitle());
                        value.setPromotionsRuleId(promotionsRule.getId());
                        value.setPromotionsType(promotionsRule.getPromotionsType());
                    } else {
                        value.setDiscount(value.getDiscount() + promotionsResult.getDiscount());
                    }

                    map.put(promotionsActivityId, value);
                }
            }
        }

        if (map.size() != 0) {
            List<MoneyResultForPromotions> moneyResultForPromotionsList = new ArrayList<>(map.values()).stream()
                .sorted(Comparator.comparingInt(MoneyResultForPromotions::getPromotionsActivityId))
                .collect(toList());

            this.result.setMoneyResultForPromotionsList(moneyResultForPromotionsList);
        }
    }

    private void sortGiftResultBySendNum() {
        for (GiftResult giftResult : this.result.getGiftResults()) {
            giftResult.getGiftList().sort(new GiftMsgComparator());
        }
    }


    /* -- method for test, not allowed to be used except test -- */

    /**
     * 该方法仅用于测试，不可调用
     *
     * @return
     */
    @SuppressWarnings("ConstantConditions")
    @Deprecated
    public static Map<Integer, Map<Integer, List<PromotionsActivity>>>
    methodForTest1(List<PromotionsActivity> promotionsActivities) {
        ConcessionCalculateBaseImpl concessionCalculateBase = new ConcessionCalculateBaseImpl(null, promotionsActivities);
        return concessionCalculateBase.groupPromotionsActivity();
    }

    /* -- setter and getter -- */
    // 不提供任何setter&getter
}
