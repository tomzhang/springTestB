package com.jk51.modules.coupon.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.encode.Base64Coder;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.UnknownTypeException;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.Stores;
import com.jk51.model.concession.ConcessionDesc;
import com.jk51.model.concession.result.GiftResult;
import com.jk51.model.concession.result.GoodsDataForResult;
import com.jk51.model.concession.result.Result;
import com.jk51.model.coupon.*;
import com.jk51.model.coupon.requestParams.*;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.order.BMember;
import com.jk51.model.order.BeforeCreateOrderReq;
import com.jk51.model.order.Member;
import com.jk51.model.order.Trades;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.concession.constants.ConcessionConstant;
import com.jk51.modules.concession.service.ConcessionResultHandler;
import com.jk51.modules.coupon.constants.CouponConstant;
import com.jk51.modules.coupon.mapper.BCouponDetailExtraLogMapper;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.request.OwnCouponParam;
import com.jk51.modules.coupon.utils.CouponDecorator;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.jk51.commons.base.Preconditions.checkNotBlank;
import static com.jk51.commons.base.Preconditions.passPredicateOrThrow;
import static com.jk51.modules.coupon.constants.CouponConstant.FAIL_BECAUSE_PROMOTIONS;
import static java.util.stream.Collectors.toList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chenpeng
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@SuppressWarnings("serial")
@Service
public class CouponDetailService {
    private static final Logger log = LoggerFactory.getLogger(CouponDetailService.class);

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private CouponInformErpService couponInformErpService;
    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;
    @Autowired
    private CouponProcessService couponProcessService;
    @Autowired
    private CouponNoEncodingService couponNoEncodingService;
    @Autowired
    private ConcessionResultHandler concessionResultHandler;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private BStoresMapper bStoresMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private CouponActivityMapper couponActivityMapper;
    @Autowired
    private CouponActivityMapper mapper;
    @Autowired
    private YbMerchantMapper ybMerchantMapper;
    @Autowired
    private BMemberMapper bMemberMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private BCouponDetailExtraLogMapper bCouponDetailExtraLogMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;


    public List<Map<String, Object>> centerOfOwnCoupon(OwnCouponParam ownCouponParam) {
        List<Map<String, Object>> result = couponDetailMapper.centerOfOwnCoupon(ownCouponParam);
        List<String> activityId = result.stream().map(m -> m.get("activityId").toString()).distinct().collect(toList());
        List<Map<String, Object>> getCouponNum = null;
        if(Objects.nonNull(activityId)&&activityId.size()>0){
            getCouponNum= couponDetailMapper.findGetCouponNum(ownCouponParam.getSiteId(), activityId,ownCouponParam.getMemberId());
        }

        if (result.isEmpty())
            return null;

        result = result.stream().filter(map -> checkOwnCoupon(map,
            ownCouponParam.getSiteId(),
            Integer.parseInt(map.get("activityId").toString()),
            Integer.parseInt(map.get("rule_id").toString()),
            ownCouponParam.getUserId()))
            .filter(map -> checkEffectiveTimeRuleForOwnCoupon((String) map.get("time_rule"), (Date) map.get("create_time")))
            .collect(toList());
        List<Map<String, Object>> finalGetCouponNum = getCouponNum;
        result = result
            .stream()
            .filter(map->
            filterCouponNum(finalGetCouponNum, map))
            .collect(toList());

        result.forEach(resultMap -> {
            String timeRule = (String) resultMap.get("time_rule");
            resultMap.put("effectiveTime", getEffectiveTimeForGoodsDetail(timeRule, (Date) resultMap.get("create_time")));
            resultMap.put("effectiveTimeType", getEffictiveTimeType(timeRule));
            String order_rule = resultMap.get("order_rule") == null ? null : resultMap.get("order_rule").toString();
            String goods_rule = resultMap.get("goods_rule") == null ? null : resultMap.get("goods_rule").toString();
            resultMap.put("couponView", parsingCouponRuleService.accountCoupon((int) resultMap.get("aim_at"), (int) resultMap.get("coupon_type")
                , order_rule, goods_rule));
        });
        return result;
    }

    private Boolean filterCouponNum(List<Map<String, Object>> getCouponNum, Map<String, Object> map) {
        List<Integer> ruleIds = getCouponNum.stream().map(m -> Integer.parseInt(m.get("rule_id").toString())).collect(toList());
        int activity_id = Integer.parseInt(map.get("activityId").toString());
        int ruleId = Integer.parseInt(map.get("rule_id").toString());
        int sendLimit = Integer.parseInt(map.get("sendLimit").toString());
        if(!ruleIds.contains(ruleId))
            return true;
        for (Map<String, Object> m : getCouponNum) {
            Integer source = Integer.parseInt(m.get("source").toString());
            Integer rule_id = Integer.parseInt(m.get("rule_id").toString());
            if(Objects.equals(activity_id,source)&&Objects.equals(ruleId,rule_id)){
                int num = Integer.parseInt(m.get("num").toString());
                if(sendLimit>num)
                    return true;
                else
                    return false;
            }
        }
        return false;
    }

    public Map<String, Object> centerOfOwnCouponDetail(OwnCouponParam ownCouponParam) {
        Map<String, Object> resultMap = couponDetailMapper.centerOfOwnCouponDetail(ownCouponParam);
        String order_rule = resultMap.get("order_rule") == null ? null : resultMap.get("order_rule").toString();
        String goods_rule = resultMap.get("goods_rule") == null ? null : resultMap.get("goods_rule").toString();
        resultMap.put("couponView", parsingCouponRuleService.accountCoupon((int) resultMap.get("aim_at"), (int) resultMap.get("coupon_type")
            , order_rule, goods_rule));
        YbMerchant ybMerchant = ybMerchantMapper.getMerchant(ownCouponParam.getSiteId().toString());
        resultMap.put("ybMerchant", ParameterUtil.ObjectConvertJson(ybMerchant));
        try {
            String limitRule = (String) resultMap.get("limit_rule");
            LimitRule limitRule1 = JSON.parseObject(limitRule, LimitRule.class);
            if (limitRule1.getApply_store() == -1)
                resultMap.put("useScope", -1);
            else
                resultMap.put("useScope", 1);

            if (limitRule1.getApply_store() == 1) {
                resultMap.put("storeIds", limitRule1.getUse_stores());
            } else if (limitRule1.getApply_store() == 2) {
                String citys = limitRule1.getUse_stores();
                if ("".equals(citys)) {
                    resultMap.put("storeIds", "");
                } else {
                    List<String> cityIds = Arrays.asList(citys.split(","));
                    List<Integer> stores = bStoresMapper.findStoreIdByCityAndSiteId(ownCouponParam.getSiteId(), cityIds);
                    String storesIds = stores.stream().map(i -> i.toString()).collect(Collectors.joining(","));
                    //将最后一个逗号去掉
                    storesIds = storesIds.substring(0, storesIds.length() - 1);
                    resultMap.put("storeIds", storesIds);
                }
            } else {
                resultMap.put("storeIds", "");
            }

            if (StringUtil.equalsIgnoreCase(limitRule1.getApply_channel(), "101,103"))
                resultMap.put("isLine", 1);
            else if (StringUtil.equalsIgnoreCase(limitRule1.getApply_channel(), "105"))
                resultMap.put("isLine", 2);
            else if (StringUtil.equalsIgnoreCase(limitRule1.getApply_channel(), "101,103,105"))
                resultMap.put("isLine", 3);
            else if (StringUtil.equalsIgnoreCase(limitRule1.getApply_channel(), "100,101,102,103,104,105"))
                resultMap.put("isLine", 1);
            else if (StringUtil.equalsIgnoreCase(limitRule1.getApply_channel(), "100,101,102,103,104,105"))
                resultMap.put("isLine", 1);


        } catch (Exception e) {
            log.info("优惠券详情接口解析异常" + e);
            return resultMap;
        }

        return resultMap;
    }

    private boolean checkOwnCoupon(Map<String, Object> canReciveLimit, Integer siteId, Integer activityId, Integer ruleId, Integer userId) {
        int resultCount = couponDetailMapper.findOwnCouponCount(siteId, activityId, ruleId, userId);
        Integer limit = canReciveLimit.get("sendLimit") != null ? Integer.parseInt(canReciveLimit.get("sendLimit").toString()) : 1;
        canReciveLimit.put("canReciveLimit", limit);
        if (resultCount >= limit)
            return false;
        else {
            canReciveLimit.put("canReciveLimit", limit - resultCount);
            return true;
        }
    }

    public List<Map<String, Object>> findUserCouponList(Integer siteId, Integer userId, Integer status) throws ParseException {

        Map<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        params.put("userId", userId);

        if (status == 0) {//待使用
            log.info("查询用户待使用优惠券");
            params.put("useStatus", "1");
            params.put("nStatus", "1");
            List<Map<String, Object>> data = couponDetailMapper.findCouponList(params);
            log.info("查询用户待使用优惠券结果没有过滤时间:" + data);
            List<Map<String, Object>> filterData = new ArrayList<>();

            for (Map<String, Object> item : data) {
                try {
                    log.info("-mqq-----item------------------:{}", ParameterUtil.ObjectConvertJson(item));
                    String timeRule = (String) item.get("time_rule");
                    if (checkEffectiveTimeRule(timeRule, (Date) item.get("create_time"))) {
                        log.info("-mqq-----item--22222222299999999999----------------");
                        // 加密优惠券编码 mqq
                        item.put("coupon_no", String.valueOf(item.get("coupon_no")).indexOf("Q") != -1 ? String.valueOf(
                            item.get("coupon_no")) : couponNoEncodingService.encryptionCouponNo(String.valueOf(item.get("coupon_no"))));
                        log.info("-mqq-----item--1212123123123123123----------------");
                        item.put("effectiveTime", getEffectiveTime(timeRule, (Date) item.get("create_time")));
                        item.put("effectiveTimeType", getEffictiveTimeType(timeRule));
                        String order_rule = item.get("order_rule") == null ? null : item.get("order_rule").toString();
                        String goods_rule = item.get("goods_rule") == null ? null : item.get("goods_rule").toString();
                        log.info("-mqq-----------------------:" + (int) item.get("aim_at"));

                        item.put("couponView", parsingCouponRuleService.accountCoupon((int) item.get("aim_at"), (int) item.get("coupon_type")
                            , order_rule, goods_rule));
                        boolean is_replace = isReplaceDistanceResult4MaxDiscount(item);
                        if (is_replace) {
                            filterData.add(item);
                        }
                    }
                } catch (Exception e) {
                    log.info("查询用户可用优惠券异常:" + e);
                    e.printStackTrace();
                    return null;
                }
            }
            log.info("查询用户待使用优惠券结果:" + data);
            return filterData;
        } else if (status == 1) {//已使用
            log.info("查询用户已使用优惠券");
            params.put("useStatus", "0");
            List<Map<String, Object>> data = couponDetailMapper.findCouponList(params);
            data.stream().forEach(item -> {
                String order_rule = item.get("order_rule") == null ? null : item.get("order_rule").toString();
                String goods_rule = item.get("goods_rule") == null ? null : item.get("goods_rule").toString();
                item.put("couponView", parsingCouponRuleService.accountCoupon((int) item.get("aim_at")
                    , (int) item.get("coupon_type"), order_rule, goods_rule));
            });
            log.info("查询用户已使用优惠券结果:" + data);
            return data;
        } else if (status == 2) {//已过期
            log.info("查询用户已过期优惠券");
            params.put("status", "1");
            List<Map<String, Object>> data = couponDetailMapper.findCouponList(params);

            List<Map<String, Object>> filterData = new ArrayList<>();
            data.stream().forEach(item -> {
                String timeRule = (String) item.get("time_rule");
                if (!checkEffectiveTimeRule(timeRule, (Date) item.get("create_time"))) {
                    item.put("couponView", parsingCouponRuleService.accountCoupon((int) item.get("aim_at"), (int) item.get("coupon_type"), item.get("order_rule").toString(), item.get("goods_rule").toString()));
                    filterData.add(item);
                }
            });
            /*for (Map<String, Object> item : data) {
                String timeRule = (String) item.get("time_rule");
                if (!checkEffectiveTimeRule(timeRule, (Date) item.get("create_time"))) {
                    item.put("couponView", parsingCouponRuleService.accountCoupon((int) item.get("aim_at"), (int) item.get("coupon_type"), item.get("order_rule").toString(), item.get("goods_rule").toString()));
                    filterData.add(item);
                }
            }*/
            log.info("查询用户已过期优惠券结果:" + data);
            return filterData;
        } else {
            return null;
        }

    }

    /**
     * 是否将距离结果替换成最大的显示结果
     *
     * @param item
     * @return true 不是距离券或距离券结果已经替换，false 距离券计算结果有问题
     */
    private boolean isReplaceDistanceResult4MaxDiscount(Map<String, Object> item) {
        try {
            //老数据中优惠券分按订单和按商品 如果是按订单直接返回true
            if (null == item.get("aim_at"))
                return false;

            if (((int) (item.get("aim_at")) == 0))
                return true;

            Integer coupon_type = (Integer) item.get("coupon_type");
            GoodsRule goods_rule = JacksonUtils.json2pojo(item.get("goods_rule").toString(), GoodsRule.class);
            if (goods_rule.getRule_type() != 6 && (coupon_type == 100 || coupon_type == 200 || coupon_type == 300 || coupon_type == 500)) {
                return true;
            }

            if (item.get("distance_reduce") != null || item.get("distance_discount") != null) {
                Integer distance_reduce = item.get("distance_reduce") == null ? null : Integer.parseInt(item.get("distance_reduce").toString());
                Integer distance_discount = item.get("distance_discount") == null ? null : Integer.parseInt(item.get("distance_discount").toString());
                CouponView couponView = (CouponView) item.get("couponView");
                if (distance_reduce != null) {
                    couponView.setMaxMoney(distance_reduce);
                } else if (distance_discount != null) {
                    couponView.setMaxDiscount(distance_discount);
                }
                return true;
            }
        } catch (Exception e) {
            log.error("距离券替换显示结果异常:{}", e);
            return false;
        }

        return false;
    }

    /**
     * 获取可用优惠券列表
     *
     * @param siteId
     * @param userId
     * @param orderType
     * @param applyChannel
     * @param storeId
     * @param orderFee
     * @param postFee
     * @param goodsInfo
     * @param areaId
     * @param concessionResult
     * @return
     * @throws Exception
     */
    public ReturnDto usableCouponList(String siteId, String userId, String orderType, String applyChannel, String storeId,
                                      String orderFee, String postFee, String goodsInfo, String areaId, Result concessionResult) throws Exception {
        log.info("获取可用优惠券列表---开始执行---参数:goodsInfo{}",goodsInfo);
        log.info("获取可用优惠券列表---开始执行---参数:concessionResult{}",JSON.toJSONString(concessionResult));
        /* -- new one -- */

        if (StringUtils.isBlank(storeId)) storeId = "0";

        Optional<List<CouponDetail>> optional = findUsableCouponDetails(Integer.parseInt(siteId), Integer.parseInt(checkNotBlank(userId)));
        if (!optional.isPresent()) {
            return ReturnDto.buildSuccessReturnDto(emptyUsableCoupon());
        }

        List<CouponDecorator.GoodsData> goodsDataList;

        boolean calculateByConcessionResult = false;
        // 预下单优惠计算结果
        if (concessionResult != null) {
            calculateByConcessionResult = true;
            Map<Integer, GoodsDataForResult> goodsConcession = concessionResult.getGoodsConcession();
            log.info("goodsInfo转换之前goodsInfo:{},goodsConcession:{}",goodsInfo,JSON.toJSONString(goodsConcession));
            goodsDataList = JSON.parseArray(goodsInfo).stream()
                .map(obj -> getGoodsData((Map<String, Object>) obj))
                .filter(goodsData -> {
                    GoodsDataForResult goodsDataForResult = goodsConcession.get(goodsData.getGoodsId());
                    //对预下单计算的结果中取出活动的优惠价格。将活动的价格带入优惠券的计算过程
                    if (Objects.nonNull(goodsDataForResult)){
//                        goodsData.setDiscount(goodsDataForResult.getDiscount()); old code
                        //discount为优惠券和活动的总优惠，如果选择券后，discount会把优惠券的优惠加上，然后这部分的处理
                        //是把discount所加入的券的金额给减掉
                        int discount = goodsDataForResult.getDiscount() - goodsDataForResult.getCouponDiscount();
                        if(discount<0){
                            goodsData.setDiscount(0);
                        }else {
                            goodsData.setDiscount(discount);
                        }
                    }
                    return goodsDataForResult == null || goodsDataForResult.isCanUseCoupon();
                })
                .collect(toList());
        } else {
            goodsDataList = JSON.parseArray(goodsInfo).stream()
                .map(obj -> getGoodsData((Map<String, Object>) obj))
                .collect(toList());
        }
        log.info("goodsInfo转GoodsDate后的列表:{}",JSON.toJSONString(goodsDataList));
        List<CouponDetail> couponDetails = optional.get();

/*
        if (goodsDataList.size() == 0) {
            return ReturnDto.buildSuccessReturnDto(emptyUsableCoupon(couponDetails));
        }
*/
        log.info("获取可用优惠券列表---准备查询优惠券是否可用的详情{CouponDetailService:391}---goodsDateList:{}---size:{}",JSON.toJSONString(goodsDataList),goodsDataList.size());
        // 优惠券是否可用详情
        List<Map<String, Object>> couponUsableDetail;

        couponUsableDetail =
            filterCoupon(siteId, userId, orderType, applyChannel, storeId, orderFee, postFee, goodsDataList, areaId, couponDetails);

        if (calculateByConcessionResult) {
            // 计算可用优惠券使用的商品信息不考虑独立活动的影响
            List<CouponDecorator.GoodsData> goodsDataListForCompare = JSON.parseArray(goodsInfo).stream()
                .map(obj -> getGoodsData((Map<String, Object>) obj))
                .collect(toList());

            if (goodsDataList.size() != goodsDataListForCompare.size()) {
                log.info("没有使用活动下的优惠券可用详情:{}---goodsDateListForCompare.size:{}",JSON.toJSONString(goodsDataListForCompare),goodsDataListForCompare.size());
                // 没有使用活动下的优惠券可用详情
                List<Map<String, Object>> couponUsableDetailForCompare =
                    filterCoupon(siteId, userId, orderType, applyChannel, storeId, orderFee, postFee, goodsDataListForCompare, areaId, couponDetails);

                couponUsableDetail = compareCouponUsableDetail(couponUsableDetail, couponUsableDetailForCompare);
            }
        }
        log.info("couponUsableDetail结果过滤之前:{}---size:{}",JSON.toJSONString(couponUsableDetail),couponUsableDetail.size());
        couponUsableDetail = couponUsableDetail.stream()
            .sorted(Comparator.comparingInt(map -> -Integer.parseInt(map.get("usable").toString())))
            .collect(toList());

        List<Map<String, Object>> usable = couponUsableDetail.stream()
            .filter(map -> "1".equals(map.get("usable").toString()))
            .collect(toList());
        log.info("couponUsableDetail结果过滤之后:{}---size:{}",JSON.toJSONString(couponUsableDetail),couponUsableDetail.size());
        Map<String, Object> data = new HashMap<>();
        data.put("totalcuponNum", couponDetails.size());
        data.put("usable_num", usable.size());
        data.put("unusable_num", couponUsableDetail.size() - usable.size());
        data.put("data", couponUsableDetail);
        data.put("couponList", usable);
        return ReturnDto.buildSuccessReturnDto(data);


        /* -- old one -- */
        /* todo 超级优惠上线 删除
        OrderMessageParams orderMessageParams = new OrderMessageParams();

        if (!StringUtil.isEmpty(areaId)) {
            orderMessageParams.setAreaId(Integer.parseInt(areaId));
        }
        orderMessageParams.setSiteId(Integer.parseInt(siteId));
        orderMessageParams.setUserId(Integer.parseInt(userId));
        orderMessageParams.setFirstOrder(couponSendService.isFirstOrder(Integer.parseInt(siteId), Integer.parseInt(userId)));
        orderMessageParams.setOrderType(Integer.parseInt(orderType));
        orderMessageParams.setApplyChannel(Integer.parseInt(applyChannel));
        orderMessageParams.setBeforeCreateOrderReq(beforeCreateOrderReq);

        if (!StringUtils.isBlank(postFee)) {
            orderMessageParams.setPostFee(Integer.parseInt(postFee));
        } else {
            orderMessageParams.setPostFee(0);
        }
        if (!StringUtil.isEmpty(storeId))
            orderMessageParams.setStoreId(Integer.parseInt(storeId));
        orderMessageParams.setOrderFee(Integer.parseInt(orderFee));
        try {
            List<LinkedHashMap<String, Object>> list = JacksonUtils.json2listMap(goodsInfo);
            Iterator<LinkedHashMap<String, Object>> it = list.iterator();
            List<Map<String, Integer>> goodsList = new ArrayList<>();
            while (it.hasNext()) {
                Map<String, Integer> goodsMap = new HashMap<>();
                LinkedHashMap<String, Object> map = it.next();
                Iterator<Map.Entry<String, Object>> itMap = map.entrySet().iterator();
                while (itMap.hasNext()) {
                    Map.Entry<String, Object> entry = itMap.next();
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Integer)
                        goodsMap.put(key, (Integer) value);
                    else
                        // 这块的异常是由于商品价格出现异常数据，导致转换异常，所以加了个取整 zw
                        goodsMap.put(key, (int) Math.rint(Float.parseFloat(value.toString())));
                }
                goodsList.add(goodsMap);
            }
            orderMessageParams.setGoodsInfo(goodsList);
        } catch (Exception e) {
            log.error("goodsInfo数据格式错误", e);
            return ReturnDto.buildFailedReturnDto("goodsInfo数据格式错误");
        }
        Map<String, Object> data = findUsableCoupon(orderMessageParams);
        System.out.println(JSON.toJSONString(ReturnDto.buildSuccessReturnDto(data)));
        return ReturnDto.buildSuccessReturnDto(data);
        */
    }

    private CouponDecorator.GoodsData getGoodsData(Map<String, Object> obj) {
        CouponDecorator.GoodsData goodsData = new CouponDecorator.GoodsData();
        Map<String, Object> map = obj;
        goodsData.setGoodsId(Integer.parseInt(map.get("goodsId").toString()));
        goodsData.setNum(Integer.parseInt(map.get("num").toString()));
        goodsData.setShopPrice(Integer.parseInt(map.get("goodsPrice").toString()));
        log.info("优惠券列表过滤---goodsInfo转换GoodsDate对象:{}",JSON.toJSONString(goodsData));
        return goodsData;
    }

    /**
     * @param couponUsableDetail           考虑独立活动下的优惠券可用详情
     * @param couponUsableDetailForCompare 没有考虑活动下的优惠券可用详情
     * @return
     */
    private List<Map<String, Object>> compareCouponUsableDetail(List<Map<String, Object>> couponUsableDetail, List<Map<String, Object>> couponUsableDetailForCompare) {
        List<Integer> ids = couponUsableDetail.stream()
            .filter(map -> "1".equals(map.get("usable").toString()))
            .map(map -> ((Integer) ((Map<String, Object>) map.get("couponUseDetail")).get("couponId")))
            .collect(toList());

        List<Map<String, Object>> result = couponUsableDetailForCompare.stream()
            .filter(map -> "0".equals(map.get("usable").toString()))
            .collect(toList());

        for (Map<String, Object> map : couponUsableDetailForCompare) {

            if ("1".equals(map.get("usable").toString())) {
                Integer couponDetailId = (Integer) ((Map<String, Object>) map.get("couponUseDetail")).get("couponId");

                if (!ids.contains(couponDetailId)) {
                    map.put("usable", 0);
                    map.put("errorMsg", FAIL_BECAUSE_PROMOTIONS);
                }

                result.add(map);
            }

        }

        return result;
    }

    private List<Map<String, Object>> filterCoupon(String siteId, String userId, String orderType, String applyChannel, String storeId, String orderFee, String postFee, List<CouponDecorator.GoodsData> goodsInfo, String areaId, List<CouponDetail> couponDetails) {
        List<Map<String, Object>> couponUsableDetail = new ArrayList<>();
        for (CouponDetail couponDetail : couponDetails) {
            Map<String, Object> map = new HashMap<>();

            Optional<CouponDecorator> optional = buildIncompleteCouponDecorator(siteId, userId,
                orderType, applyChannel, storeId, postFee, goodsInfo, areaId, couponDetail);
            if (optional.isPresent()) {
                CouponDecorator couponDecorator = optional.get();
                couponDecorator.getParam().getGoodsDataList().forEach(gd -> {
                    gd.setCouponDiscount(0);
                });
                CouponDecorator.Result couponConcessionResult = concessionByCouponDecorator(couponDecorator);
                gatherResultToMap(map, couponDetail, couponConcessionResult, Integer.valueOf(orderFee));

                if (!CouponConstant.ALREADY_ENDED.equals(map.get("errorMsg")))
                    couponUsableDetail.add(map);
            } else
                break;
        }
        log.info("完成过滤(CouponDetailService#filterCoupon):{}",JSON.toJSONString(couponUsableDetail));
        return couponUsableDetail;
    }

    private boolean usableCoupon(CouponDecorator.Result result) {
        if (StringUtils.isNotBlank(result.getErrorMsg()))
            return false;

        if (result.getResultMark() == null) {
            throw new RuntimeException();
        } else {
            if (result.getResultMark() == 3 && StringUtils.isBlank(result.getErrorMsg()))
                throw new RuntimeException("但返回结果为3时，应该给出计算失败理由");

            if (result.getResultMark() == 1 || result.getResultMark() == 2)
                return true;

            throw new UnknownTypeException();
        }
    }

    private void gatherResultToMap(Map<String, Object> map, CouponDetail couponDetail, CouponDecorator.Result couponConcessionResult, Integer orderFee) {
        CouponRule couponRule = couponDetail.getCouponRule();
        convertPojoToMap(couponDetail, map, couponRule);

        Map<String, Object> couponUseDetail = new HashMap<>();
        CouponView couponView = parsingCouponRuleService.accountCoupon(couponRule.getAimAt(), couponRule.getCouponType(),
            couponRule.getOrderRule(), couponRule.getGoodsRule());

        if (usableCoupon(couponConcessionResult)) {
            map.put("usable", 1);
            switch (couponConcessionResult.getResultMark()) {
                case 1:
                    couponUseDetail.put("accountAmount", passPredicateOrThrow(i -> i >= 0, orderFee - couponConcessionResult.getCouponDiscount()));
                    couponUseDetail.put("couponId", couponDetail.getId());
                    couponUseDetail.put("couponMoney", couponConcessionResult.getCouponDiscount());

                    break;

                case 2:
                    GiftResult giftResult = couponConcessionResult.getGiftResult();
                    setConcessionDescForGiftResult(couponDetail, giftResult);

                    couponUseDetail.put("couponId", couponDetail.getId());
                    couponUseDetail.put("giftList", giftResult.getGiftList());
                    couponUseDetail.put("maxSenNum", giftResult.getMaxSendNum());
                    couponUseDetail.put("couponRuleView", couponView);

                    break;

                default:
                    throw new UnknownTypeException();
            }
        } else {
            map.put("usable", 0);
            map.put("errorMsg", checkNotBlank(couponConcessionResult.getErrorMsg()));
        }

        String timeRule = couponRule.getTimeRule();
        map.put("effectiveTime", getEffectiveTime(timeRule, couponDetail.getCreateTime()));
        map.put("effectiveTimeType", getEffictiveTimeType(timeRule));
        map.put("couponView", couponView);
        map.put("couponUseDetail", couponUseDetail);
    }

    private void setConcessionDescForGiftResult(CouponDetail couponDetail, GiftResult giftResult) {
        ConcessionDesc concessionDesc = new ConcessionDesc();
        concessionDesc.setConcessionType(ConcessionConstant.COUPON);
        concessionDesc.setCouponDetailId(couponDetail.getId());
        giftResult.setConcessionDesc(concessionDesc);
        concessionResultHandler.setRuleViewForGiftResult(couponDetail.getSiteId(), giftResult);
    }

    private void convertPojoToMap(CouponDetail couponDetail, Map<String, Object> map, CouponRule couponRule) {
        map.put("rule_name", couponRule.getRuleName());
        map.put("time_rule", couponRule.getTimeRule());
        map.put("limit_rule", couponRule.getLimitRule());
        map.put("order_rule", couponRule.getOrderRule());
        map.put("area_rule", couponRule.getAreaRule());
        map.put("goods_rule", couponRule.getGoodsRule());
        map.put("marked_words", couponRule.getMarkedWords());
        map.put("coupon_type", couponRule.getCouponType());
        map.put("limit_state", couponRule.getLimitState());
        map.put("limit_remark", couponRule.getLimitRemark());
        map.put("aim_at", couponRule.getAimAt());
        map.put("status", couponRule.getStatus());
        map.put("id", couponDetail.getId());
        map.put("rule_id", couponDetail.getRuleId());
        map.put("coupon_no", couponDetail.getCouponNo());
        map.put("create_time", couponDetail.getCreateTime());
        map.put("distance_reduce", couponDetail.getDistanceReduce());
        map.put("distance_discount", couponDetail.getDistanceDiscount());
    }

    private CouponDecorator.Result concessionByCouponDecorator(CouponDecorator couponDecorator) {
        CouponDecorator.Result result;
        if (couponDecorator.filter()) {
            try {
                couponDecorator.concession();
                result = couponDecorator.getResult();
            } catch (Exception e) {
                log.error("异常发送, {}", e);
                result = couponDecorator.getResult();
                if (result == null)
                    throw new NullPointerException();
                else {
                    if (StringUtils.isBlank(result.getErrorMsg()))
                        throw new RuntimeException(e);
                }
            }
        } else {
            result = couponDecorator.getResult();
        }

        return result;
    }

    private Optional<CouponDecorator> buildIncompleteCouponDecorator(String siteId, String userId, String orderType, String applyChannel, String storeId, String postFee, List<CouponDecorator.GoodsData> goodsInfo, String areaId, CouponDetail couponDetail) {
        CouponDecorator.Param param = new CouponDecorator.Param();
        param.setOrderTime(LocalDateTime.now());
        param.setSiteId(Integer.parseInt(siteId));
        param.setMemberId(Integer.parseInt(userId));
        param.setOrderType(orderType);
        param.setApplyChannel(applyChannel);
        param.setStoreId(Integer.parseInt(storeId));

        if (StringUtils.isBlank(postFee))
            param.setFreight(0);
        else
            param.setFreight(Integer.parseInt(postFee));

        if (goodsInfo.size() == 0) {
            return Optional.empty();
        } else {
            param.setGoodsDataList(goodsInfo);
        }

        param.setReceiverCityCode(areaId);

        CouponDecorator couponDecorator = new CouponDecorator(couponDetail, couponDetail.getCouponRule(), couponDetail.getCouponActivity(), param, servletContext);

        return Optional.of(couponDecorator);
    }

    public Map<String, Object> getCouponDetailByScanQr(Integer siteId, Integer storeId, Integer managerId, String couponNo) throws Exception {
        Boolean writeOffCoupons = true;
        //返回结果Map
        Map<String, Object> couponDetailMap = new HashMap<String, Object>();
        String coupon_no = couponNoEncodingService.decryptionCouponNo(couponNo);
        CouponDetail couponDetail = couponDetailMapper.findCouponDetailByCouponNo(coupon_no, siteId);
        if(Objects.isNull(couponDetail)){
            return null;
        }
        CouponRule couponRule = couponRuleMapper.findCouponRuleById(couponDetail.getRuleId(), siteId);
        Map<String, Boolean> fieldStatus = new HashMap<String, Boolean>() {{
            put("couponStatus", true);
            put("storeStatus", true);
            put("channelStatus", true);
            put("firstOrderStatus", true);
        }};

        Map<String, Object> checkTimeParam = new HashMap<String, Object>();
        checkTimeParam.put("time_rule", couponRule.getTimeRule());
        checkTimeParam.put("create_time", couponDetail.getCreateTime());
        boolean checkTimeRuleOverdue = couponRuleService.checkTimeRuleOverdue(checkTimeParam);
        int status = couponDetail.getStatus();
        //优惠券为待使用，并且已过期
        if (status == 1 && !checkTimeRuleOverdue) {
            status = 2;
            fieldStatus.put("couponStatus", false);
        }
        if (status == 0) {
            fieldStatus.put("couponStatus", false);
        }

        LimitRule limitRule = JSON.parseObject(couponRule.getLimitRule(), LimitRule.class);
        List<String> orderTypeList = Arrays.asList(limitRule.getOrder_type().split(","));
        String orderType = orderTypeList.stream().map(CouponConstant.COUPON_ORDER_TYPE::get).collect(Collectors.joining(","));

        String applyStoreDetail = getApplyStoreDetail(siteId, storeId, limitRule);
        fieldStatus.put("storeStatus",!"本门店不可用".equals(applyStoreDetail));
        String apply_channel = CouponConstant.COUPON_CHANNEL.get(limitRule.getApply_channel());
        if (StringUtil.isBlank(apply_channel)) {
            apply_channel = getChannelSecondPlan(limitRule);
        }
        if ("线上使用".equals(apply_channel)) {
            fieldStatus.put("channelStatus", false);
        }
        String firstOrder = CouponConstant.COUPON_FIRST_ORDER.get(limitRule.getIs_first_order());
        //券为首单，但同时为线下可用，则不进行首单校验，，，券如果为线上使用，那么进入该判断，进行首单判断 加入标志
        if (limitRule.getIs_first_order() == 1 && !fieldStatus.get("channelStatus")) {
            BMember bMember = bMemberMapper.getMemberBySiteIdAndMemberId(couponDetail.getUserId(), siteId);
            boolean firstOrderFlag = orderService.checkUserFirstOrderByPayment(siteId, bMember.getBuyerId());
            fieldStatus.put("firstOrderStatus", firstOrderFlag);
        }
        couponDetailMap.put("ruleName", couponRule.getRuleName());
        couponDetailMap.put("status", CouponConstant.COUPON_STATUS_MAP.get(status));
        couponDetailMap.put("couponType", CouponConstant.COUPON_TYPE.get(couponRule.getCouponType()));
        couponDetailMap.put("effectiveTime", getEffectiveTimeForCouponDetail(couponRule.getTimeRule()));
        couponDetailMap.put("couponView", parsingCouponRuleService.accountCoupon(couponRule.getAimAt(),
            couponRule.getCouponType(), couponRule.getOrderRule(), couponRule.getGoodsRule()));
        couponDetailMap.put("orderType", orderType);
        couponDetailMap.put("applyStore", applyStoreDetail);
        couponDetailMap.put("applyChannel", apply_channel);
        couponDetailMap.put("firstOrder", firstOrder);
        couponDetailMap.put("remark", couponRule.getLimitRemark());
        couponDetailMap.put("fieldStatusMap", fieldStatus);
        //是否能够核销，检查每项的状态，有一些为false则不能核销
        for (String fieldKey : fieldStatus.keySet()) {
            if (writeOffCoupons) {
                Boolean aBoolean = fieldStatus.get(fieldKey);
                if (!aBoolean) {
                    writeOffCoupons = false;
                }
            } else {
                //如果核销有一项不符合，就不用循环了
                break;
            }

        }
        couponDetailMap.put("writeOffCoupons", writeOffCoupons);
        System.out.println(writeOffCoupons);
        return couponDetailMap;
    }

    private String getChannelSecondPlan(LimitRule limitRule) throws Exception {
        List<String> channelList = Arrays.asList(limitRule.getApply_channel().split(","));
        if (channelList == null || channelList.size() < 1) {
            throw new Exception("适用渠道数据异常");
        }
        if (channelList.contains("105")) {
            if (channelList.size() > 1) {
                return "线上线下均可使用";
            } else {
                return "线下使用";
            }
        } else {
            return "线上使用";
        }
    }

    private String getApplyStoreDetail(int siteId, int storeId, LimitRule limitRule) throws Exception {
        if(limitRule.getApply_store() == -1){
           return "全部门店";
        }else {
            List<String> stores = new ArrayList<>();
            if (limitRule.getApply_store() == 1) {
                //具体门店
                stores = Arrays.asList(limitRule.getUse_stores().split(","));
            } else if (limitRule.getApply_store() == 2) {
                //区域门店
                List<String> cityIds = Arrays.asList(limitRule.getUse_stores().split(","));
                stores = bStoresMapper.findStoreIdByCityAndSiteId(siteId,cityIds).stream().map(String::valueOf).collect(toList());
            } else {
                throw new Exception("未知门店类型");
            }
            if (stores.contains(String.valueOf(storeId))) {
                return "本门店可用";
            } else {
                return "本门店不可用";
            }
        }
    }

    public ReturnDto writeOffCoupons(Integer siteId, Integer managerId, String couponNo) throws Exception {

        String coupon_no = couponNoEncodingService.decryptionCouponNo(couponNo.toString());
        CouponDetail couponDetail = couponDetailMapper.findCouponDetailByCouponNo(coupon_no, siteId);
        CouponRule couponRule = couponRuleMapper.findCouponRuleById(couponDetail.getRuleId(), siteId);
        StoreAdminExt storeAdminExtById = storeAdminExtMapper.getStoreAdminExtById(siteId.toString(), managerId.toString());
        LimitRule limitRule = JSON.parseObject(couponRule.getLimitRule(), LimitRule.class);
        if (couponDetail.getStatus() == 0)
            return ReturnDto.buildFailedReturnDto("该券已使用");
        Map<String, Object> checkTimeParam = new HashMap<String, Object>();
        checkTimeParam.put("time_rule", couponRule.getTimeRule());
        checkTimeParam.put("create_time", couponDetail.getCreateTime());
        boolean checkTimeRuleOverdue = couponRuleService.checkTimeRuleOverdue(checkTimeParam);
        if (!checkTimeRuleOverdue) {
            return ReturnDto.buildFailedReturnDto("该券已过期");
        }
        String applyStoreDetail =
        getApplyStoreDetail(siteId,storeAdminExtById.getStore_id(),limitRule);
        if("本门店不可用".equals(applyStoreDetail))
            return ReturnDto.buildFailedReturnDto("本门店不可用");

        String channelSecondPlan = getChannelSecondPlan(limitRule);
        if ("线上使用".equals(channelSecondPlan)) {
            return ReturnDto.buildFailedReturnDto("仅限线上使用");
        }
        /*if (limitRule.getIs_first_order() == 1) {
            BMember bMember = bMemberMapper.getMemberBySiteIdAndMemberId(couponDetail.getUserId(), siteId);
            boolean firstOrderFlag = orderService.checkUserFirstOrderByPayment(siteId, bMember.getBuyerId());
            if (!firstOrderFlag) {
                return ReturnDto.buildFailedReturnDto("用户已不是首单");
            }
        }*/
        String[] couponArr = {coupon_no};
        int use_status = 0;
        Integer row = couponDetailMapper.updateStatusByCouponNo(siteId, couponArr, use_status);
        if (row == 0) {
            return ReturnDto.buildFailedReturnDto("核销优惠券失败");
        }
        BMember bMember = bMemberMapper.getMemberBySiteIdAndMemberId(couponDetail.getUserId(), siteId);
        //创建日志记录表
        BCouponDetailExtraLog log = new BCouponDetailExtraLog(siteId, coupon_no, couponDetail.getStatus(), use_status,
            couponDetail.getUserId(), bMember.getMobile(), managerId, storeAdminExtById.getStore_id(), new Date());
        //插入
        bCouponDetailExtraLogMapper.insertSelective(log);
        return ReturnDto.buildSuccessReturnDto("核销成功");

    }

    public static class GoodsData {
        private Integer goodsId;

        private Integer num;

        /**
         * 商店价格，单位为分
         */
        private Integer goodsPrice;

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

        public Integer getGoodsPrice() {
            return goodsPrice;
        }

        public void setGoodsPrice(Integer goodsPrice) {
            this.goodsPrice = goodsPrice;
        }
    }


    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")

    private Map<String, Object> emptyUsableCoupon() {
        return emptyUsableCoupon(null);
    }

    private Map<String, Object> emptyUsableCoupon(List<CouponDetail> couponDetails) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        List<Map<String, Object>> coupons = new ArrayList<>();

        map.put("unusable_num", couponDetails == null ? 0 : couponDetails.size());
        map.put("totalcuponNum", couponDetails == null ? 0 : couponDetails.size());
        map.put("usable_num", 0);
        map.put("data", data);
        map.put("couponList", coupons);
        return map;
    }

    /**
     * 查询未使用的，没有过期或者手动作废的优惠券
     *
     * @param siteId
     * @param userId
     * @return
     */
    private Optional<List<CouponDetail>> findUsableCouponDetails(int siteId, Integer userId) {
        CouponDetailSqlParam param = new CouponDetailSqlParam();
        param.setSiteId(siteId);
        param.setStatus(1);
        param.setMemberId(userId);
        param.setCouponRuleStatusList(Arrays.asList(0, 3));
        List<CouponDetail> couponDetailList = couponDetailMapper.findByParamWithRuleIn(param).stream()
            .filter(this::findCouponDetail)
            .filter(couponDetail -> checkEffectiveTimeRule(couponDetail.getCouponRule().getTimeRule(), couponDetail.getCreateTime()))
            .collect(toList());

        if (couponDetailList.size() == 0)
            return Optional.empty();
        else
            return Optional.of(couponDetailList);
    }


    /**
     * 过滤已过期或者是手动作废的优惠券，同时查询 CouponRule 和 CouponActivity
     *
     * @param couponDetail
     * @return
     */
    @SuppressWarnings("RedundantIfStatement")
    private boolean findCouponDetail(CouponDetail couponDetail) {
        Integer siteId = couponDetail.getSiteId();
        CouponRule couponRule = couponRuleMapper.findCouponRuleById(couponDetail.getRuleId(), siteId);
        couponDetail.setCouponRule(couponRule);

        if (couponRule == null || couponRule.getStatus() == 1 || couponRule.getStatus() == 4) {
            return false;
        }

        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(siteId, Integer.parseInt(couponDetail.getSource()));
        couponDetail.setCouponActivity(couponActivity);

        if (couponActivity == null)
            return false;

        return true;
    }

    /**
     * 根据订单获取优惠券是否可用列表
     *
     * @param orderMessageParams
     * @return
     */
    public Map<String, Object> findUsableCoupon(OrderMessageParams orderMessageParams) {
        Map<String, Object> params = new HashMap<>();
        params.put("siteId", orderMessageParams.getSiteId());
        params.put("userId", orderMessageParams.getUserId());
        params.put("useStatus", "1");
        params.put("status", 0);

        List<Map<String, Object>> data = couponDetailMapper.findCouponList(params);
        data = data.stream()
            .filter(map -> couponRuleService.checkTimeRuleType2(map))
            .collect(toList());

        List<Map<String, Object>> coupons = null;

        //因为满证券数据结构和之前的有区别 对满证券做单独过滤处理
        //因预下单要显示错误的原因 且错误分优先级  所以下面过滤执行的方法顺序 如需改动 请多加注意
        coupons = data.stream()
            .filter(map -> couponRuleService.checkCouponsForGift(map, orderMessageParams)).collect(toList());

        coupons = coupons.stream().filter(map -> checkAimAt(map, orderMessageParams)).collect(toList());

        //解析时间规则过滤
        coupons = coupons.stream()
            .filter(map -> couponRuleService.checkTimeRule(map))
            .collect(toList());

        //解析优惠券限制类
        coupons = coupons.stream()
            .filter(map -> couponRuleService.checkLimitRule(map, orderMessageParams))
            .collect(toList());


        for (Map<String, Object> map : data) {
            String timeRule = (String) map.get("time_rule");
            map.put("effectiveTime", getEffectiveTime(timeRule, (Date) map.get("create_time")));
            map.put("effectiveTimeType", getEffictiveTimeType(timeRule));

            String order_rule = map.get("order_rule") == null ? null : map.get("order_rule").toString();
            String goods_rule = map.get("goods_rule") == null ? null : map.get("goods_rule").toString();
            map.put("couponView", parsingCouponRuleService.accountCoupon((int) map.get("aim_at"), (int) map.get("coupon_type")
                , order_rule, goods_rule));
            //将距离券的最终优惠信息替换到显示字段上
            isReplaceDistanceResult4MaxDiscount(map);
            map.put("usable", 0);//不可以用
            for (Map<String, Object> map1 : coupons) {
                if (map.get("id").equals(map1.get("id"))) {
                    map.put("usable", 1);//可以用
                    orderMessageParams.setCouponId((Integer) map.get("id"));
                    ReturnDto returnDto = couponProcessService.accountCoupon(orderMessageParams);
                    map.put("couponUseDetail", returnDto.getValue());
                }
            }
        }

        Collections.sort(data, new MapComparatorDesc());

        Map<String, Object> map = new HashMap<>();
        map.put("unusable_num", data.size() - coupons.size());
        map.put("totalcuponNum", data.size());
        map.put("usable_num", coupons.size());
        map.put("data", data);
        map.put("couponList", coupons);
        return map;
    }

    public ReturnDto findCouponListByStoreAdmin(Map<String, Object> parameterMap) throws Exception {
        Integer siteId = Integer.parseInt((String) parameterMap.get("siteId"));
        Integer storeId = Integer.parseInt((String) parameterMap.get("storeId"));
        Integer managerId = Integer.parseInt((String) parameterMap.get("managerId"));
        Integer activityId = Integer.parseInt((String) parameterMap.get("activityId"));
        Integer ruleId = Integer.parseInt((String) parameterMap.get("ruleId"));
        Integer pageNum = Integer.parseInt((String) parameterMap.get("pageNum"));
        Integer pageSize = Integer.parseInt((String) parameterMap.get("pageSize"));
        String regex = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern pattern = Pattern.compile(regex);
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> couponListByStoreAdmin = couponDetailMapper.findCouponListByStoreAdmin(siteId, activityId, ruleId, storeId, managerId);
        PageInfo<Map<String, Object>> infos = new PageInfo<>(couponListByStoreAdmin);
        List<Map<String, Object>> list = infos.getList();
        for (Map<String, Object> temp : list) {
            String mobile = temp.get("mobile").toString();
            String overlay = org.apache.commons.lang3.StringUtils.overlay(mobile, "****", 3, 7);
            temp.put("mobile", overlay);
            Object nick = temp.get("nick");
            if (nick == null || "".equals(nick.toString().trim())) {
                continue;
            }
            Matcher matcher = pattern.matcher(nick.toString());
            if (matcher.matches()) {
                //是Base64
                temp.put("nick", Base64Coder.decode(nick.toString()));
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("pageNum", infos.getPageNum());
        result.put("pageSize", infos.getPageSize());
        result.put("pages", infos.getPages());
        result.put("total", infos.getTotal());
        result.put("data", list);
        return ReturnDto.buildSuccessReturnDto(result);
    }

    static class MapComparatorDesc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Integer v1 = Integer.valueOf(m1.get("usable").toString());
            Integer v2 = Integer.valueOf(m2.get("usable").toString());
            if (v2 != null) {
                return v2.compareTo(v1);
            }
            return 0;
        }

    }


    private boolean checkAimAt(Map<String, Object> map, OrderMessageParams orderMessageParams) {
        Integer aimAt = (Integer) map.get("aim_at");
        if (aimAt.equals(1)) {
            return couponRuleService.checkGoodsRule(map, orderMessageParams);
        } else if (aimAt.equals(0)) {
            return couponRuleService.checkOrderRule(map, orderMessageParams);
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean checkShareAmount(Integer siteId, Integer ruleId, Integer managerId) {

        CouponDetail couponDetail = couponDetailMapper.getCouponDetail(siteId, managerId, ruleId);
        if (couponDetail != null) {
            CouponRule couponRule = couponRuleMapper.findCouponRuleById(ruleId, siteId);
            if (couponRule.getAmount() == -1) {
                return true;
            }
            if (couponRule.getAmount() > 0) {
                couponRule.setAmount(couponRule.getAmount() - 1);
                couponRuleMapper.updateAmountByRuleId(couponRule);
                couponDetailMapper.updateCouponDetail(siteId, managerId, ruleId);
                return true;
            }
        }

        return false;
    }
    /**
     * 核销券-券详情时间解析
     *
     * @param timeRuleJson
     * @return
     * @throws Exception
     */
    public static String getEffectiveTimeForCouponDetail(String timeRuleJson) throws Exception {
        String result = null;
        TimeRule timeRule = JSON.parseObject(timeRuleJson, TimeRule.class);
        switch (timeRule.getValidity_type()) {
            case 1:
                //绝对时间
                result = timeRule.getStartTime() + " 至 " + timeRule.getEndTime();
                break;
            case 2:
                //相对时间
                Integer day = timeRule.getHow_day();
                result = "领取后" + day + "天内使用";
                break;
            case 4:
                //秒杀时间
                result = timeRule.getStartTime() + " 至 " + timeRule.getEndTime();
                break;
            default:
                throw new Exception("时间解析错误");
        }
        return result;
    }

    /**
     * 计算有效时间 商品详情单独使用
     *
     * @param timeRuleJson
     * @param createDate
     * @return
     */
    public static String getEffectiveTimeForGoodsDetail(String timeRuleJson, Date createDate) {

        String result = null;

        try {
            TimeRule timeRule = JSON.parseObject(timeRuleJson, TimeRule.class);
            switch (timeRule.getValidity_type()) {
                case 1:
                    //绝对时间
                    LocalDateTime endTime = LocalDate.parse(timeRule.getEndTime(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(0, 0, 0);

                    result = getTimeDifference(LocalDateTime.now(), endTime.plusDays(1));
                    break;
                case 2:
                    //相对时间
                    Integer day = timeRule.getHow_day();
                    result = "领取后" + day + "天";
                    break;
                case 3:
                    if (timeRule.getAssign_type().equals(1)) {
                        String assign_rule = timeRule.getAssign_rule();

                        result = "每月" + assign_rule + "使用";
                    } else if (timeRule.getAssign_type().equals(2)) {
                        String assign_rule = timeRule.getAssign_rule();
                        result = "每周" + assign_rule + "使用";
                    }
                    break;
                case 4:
                    SimpleDateFormat formt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date strtTimeag = formt.parse(timeRule.getStartTime());
                    Date endTimeag = formt.parse(timeRule.getEndTime());
                    if (new Date().before(strtTimeag)) {
                        result = getTimeDifferenceCouponSecond(new Date(), strtTimeag, timeRuleJson);
                        break;
                    } else if (new Date().after(strtTimeag) && new Date().before(endTimeag)) {
                        result = getTimeDifferenceCouponSecond(new Date(), endTimeag, timeRuleJson);
                        break;
                    }
            }
        } catch (Exception e) {
            return null;
        }

        return result;
    }


    /**
     * 计算有效时间
     *
     * @param timeRuleJson
     * @param createDate
     * @return
     */
    public static String getEffectiveTime(String timeRuleJson, Date createDate) {

        String result = null;

        try {
            TimeRule timeRule = JSON.parseObject(timeRuleJson, TimeRule.class);
            switch (timeRule.getValidity_type()) {
                case 1:
                    //绝对时间
                    LocalDateTime endTime = LocalDate.parse(timeRule.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atTime(0, 0, 0);
                    result = getTimeDifference(LocalDateTime.now(), endTime.plusDays(1));
                    break;
                case 2:
                    //相对时间
                    Date startDate = DateUtils.getBeforeOrAfterDate(createDate, timeRule.getDraw_day());
                    Date endDate = DateUtils.getBeforeOrAfterDate(startDate, timeRule.getHow_day());
                    result = getTimeDifference(new Date(), endDate);
                    break;
                case 3:
                    if (timeRule.getAssign_type().equals(1)) {
                        String assign_rule = timeRule.getAssign_rule();

                        result = "每月" + assign_rule + "使用";
                    } else if (timeRule.getAssign_type().equals(2)) {
                        String assign_rule = timeRule.getAssign_rule();
                        result = "每周" + assign_rule + "使用";
                    }
                    break;
                case 4:
                    SimpleDateFormat formt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date strtTimeag = formt.parse(timeRule.getStartTime());
                    Date endTimeag = formt.parse(timeRule.getEndTime());
                    if (new Date().before(strtTimeag)) {
                        result = getTimeDifferenceCouponSecond(new Date(), strtTimeag, timeRuleJson);
                        break;
                    } else if (new Date().after(strtTimeag) && new Date().before(endTimeag)) {
                        result = getTimeDifferenceCouponSecond(new Date(), endTimeag, timeRuleJson);
                        break;
                    }
            }
        } catch (Exception e) {
            return null;
        }

        return result;
    }


    //解析时间类型1普通时间即倒计时时间2显示会员券时间
    public static int getEffictiveTimeType(String timeRuleJson) {
        TimeRule timeRule = JSON.parseObject(timeRuleJson, TimeRule.class);
        if (timeRule.getValidity_type() == 1 || timeRule.getValidity_type() == 2) {
            return 1;
        } else if (timeRule.getValidity_type() == 4) {
            SimpleDateFormat formt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date strtTimeag = formt.parse(timeRule.getStartTime());
                Date endTimeag = formt.parse(timeRule.getEndTime());
                if (new Date().before(strtTimeag)) {
                    return 3;
                } else if (new Date().after(strtTimeag) && new Date().before(endTimeag)) {
                    return 1;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 1;
        } else {
            return 2;
        }
    }

    private static String getTimeDifference(LocalDateTime from, LocalDateTime to) {
        Timestamp t1 = Timestamp.valueOf(from);
        Timestamp t2 = Timestamp.valueOf(to);
        long differ = t2.getTime() - t1.getTime();
        long day = 1000 * 60 * 60 * 24;
        if (differ > day) {
            int days = (int) (differ / day);
            return String.valueOf(days) + "天";
        } else {
            int hours = (int) (differ / 3600000);
            int minutes = (int) ((differ / 1000 - hours * 3600) / 60);
            int second = (int) (differ / 1000 - hours * 3600 - minutes * 60);
            return "" + hours + "小时" + minutes + "分" + second + "秒";
        }
    }

    private static String getTimeDifference(Date from, Date to) {
        long differ = to.getTime() - from.getTime();
        long day = 1000 * 60 * 60 * 24;
        if (differ > day) {
            int days = (int) (differ / day);
            return String.valueOf(days) + "天";
        } else {
            int hours = (int) (differ / 3600000);
            int minutes = (int) ((differ / 1000 - hours * 3600) / 60);
            int second = (int) (differ / 1000 - hours * 3600 - minutes * 60);
            return "" + hours + "小时" + minutes + "分" + second + "秒";
        }
    }

    private static String getTimeDifferenceCouponSecond(Date from, Date to, String timeJson) {
        int type = getEffictiveTimeType(timeJson);
        long differ = to.getTime() - from.getTime();

        int hours = (int) (differ / 3600000);
        int minutes = (int) ((differ / 1000 - hours * 3600) / 60);
        int second = (int) (differ / 1000 - hours * 3600 - minutes * 60);
        if (type == 1) {
            return "" + hours + "时" + minutes + "分" + second + "秒";
        } else if (type == 3) {
            if (hours >= 24) {
                return "" + (int) (hours / 24) + "天" + (hours - 24 * (int) (hours / 24)) + "时";
            } else if (hours < 24 && hours >= 1) {
                return "" + hours + "时" + minutes + "分";
            } else if (hours < 1 && minutes > 1) {
                return "" + minutes + "分" + second + "秒";
            } else if (hours < 1 && minutes < 1) {
                return "" + second + "秒";
            }
        }
        return "" + hours + "小时" + minutes + "分" + second + "秒";

    }


    /**
     * 退还优惠券 zw
     *
     * @param scene   情景 0取消订单退券 1退款成功退券
     * @param orderId 订单id
     * @param type    1退还0不退还
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean returnCoupon(String orderId, Integer type, Integer scene) {

        Trades trades = tradesMapper.getTradesByTradesId(Long.parseLong(orderId));
        CouponDetail couponDetail = couponDetailMapper.findCouponDetailByOrderId(orderId);
        if (null == trades) {
            log.error("找不到此订单");
            return false;
        }
        /* if (trades.getIsRefund() != 120 || trades.get) { //只有退款成功的时候才能退
            log.error("此订单没有退款成功，不能退还优惠券");
            return false;
        }*/
      /*  if (null == couponDetail) {
            log.error("找不到此优惠券");
            return false;
        }*/
        switch (type) {
            case 1:
                log.info("退还优惠券,");
                // 退还优惠券，将状态改为待使用
                if (null != couponDetail) {
                    couponDetailMapper.updateCouponToReturn(orderId);
                    // rule表使用数量减一 b
                    couponRuleMapper.updateUseAmount(couponDetail.getSiteId(), couponDetail.getRuleId());
                    couponInformErpService.ifContainCrashCouponThenSendQueueMessage(orderId, scene);
                }
                break;
            case 0:
                break;
        }

        // 不管商家退不退优惠券，都要将送出去的优惠券回收
        List<CouponDetail> details = couponDetailMapper.findCouponDetailBySendOrderId(orderId);
        log.info("送出去的优惠券回收,");
        for (CouponDetail item : details) {
            if (item.getStatus() == 1) {
                try {
                    couponDetailMapper.updateCouponToRecovery(orderId);
                    couponRuleMapper.updateUseAmountByRuleId(item.getSiteId(), item.getRuleId());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        return true;
    }

    public Map<String, Object> findOrderCoupon(String orderId) {
        Map<String, Object> result = new HashMap<>();

        if (StringUtils.isBlank(orderId))
            throw new NullPointerException("orderId 不能为空");

        Map<String, Object> orderUse = couponDetailMapper.findCouponByOrderId(orderId);
        if (null != orderUse) {
            if (couponRuleService.checkTimeRule(orderUse)) {
                orderUse.put("effcetiveStatus", 0);//有效
            } else {
                orderUse.put("effcetiveStatus", 1);//无效
            }
            result.put("orderUse", orderUse);
            result.put("promotion", couponDetailMapper.findCouponBySendOrderId(orderId));
        }

        return result;
    }

    public CouponDetailView findWxCouponDetailById(Integer siteId, Integer id) {

        CouponDetailView couponDetailView = new CouponDetailView();

        Map<String, Object> item = couponDetailMapper.findWxCouponDetailById(siteId, id);

        Map<String, Object> urlMap = mapper.findWxUrlBySiteId(siteId);
        String url = urlMap.get("shopwx_url").toString() + "/new/sendCoupons?siteId=" + siteId;


        if (item != null) {
            LimitRule limitRule = JSON.parseObject(item.get("limit_rule").toString(), LimitRule.class);
            String order_rule = item.get("order_rule") == null ? null : item.get("order_rule").toString();
            String goods_rule = item.get("goods_rule") == null ? null : item.get("goods_rule").toString();
            Integer distance_reduce = item.get("distance_reduce") == null ? null : Integer.parseInt(item.get("distance_reduce").toString());
            Integer distance_discount = item.get("distance_discount") == null ? null : Integer.parseInt(item.get("distance_discount").toString());
            couponDetailView.setDistanceReduce(distance_reduce);
            couponDetailView.setDistanceDiscount(distance_discount);
            couponDetailView.setCouponView(parsingCouponRuleService.accountCoupon((int) item.get("aim_at")
                , (int) item.get("coupon_type"), order_rule, goods_rule));
            try {
                couponDetailView.setCouponNo(String.valueOf(item.get("coupon_no")).indexOf("Q") != -1 ?
                    String.valueOf(item.get("coupon_no")) : couponNoEncodingService.encryptionCouponNo
                    (String.valueOf(item.get("coupon_no"))));
            } catch (Exception e) {
                couponDetailView.setCouponNo(String.valueOf(item.get("coupon_no")));
                e.printStackTrace();
            }
            couponDetailView.setCouponId(Integer.parseInt(item.get("id").toString()));
            couponDetailView.setCouponType(Integer.parseInt(item.get("coupon_type").toString()));
            String[] ids = limitRule.getApply_channel().split(",");
            if (ids.length == 1 && "105".equals(ids[0])) {
                couponDetailView.setIsLine(2);
            } else if (ids.length > 1 && Arrays.asList(ids).contains(String.valueOf("105"))) {
                couponDetailView.setIsLine(3);
            } else {
                couponDetailView.setIsLine(1);
            }
            if (limitRule.getApply_store() == 1) {
                couponDetailView.setStoreIds(limitRule.getUse_stores());
            } else if (limitRule.getApply_store() == 2) {
                String citys = limitRule.getUse_stores();
                if ("".equals(citys)) {
                    couponDetailView.setStoreIds("");
                } else {
                    List<String> cityIds = Arrays.asList(citys.split(","));
                    List<Integer> stores = bStoresMapper.findStoreIdByCityAndSiteId(siteId, cityIds);
                    String storesIds = stores.stream().map(i -> i.toString()).collect(Collectors.joining(","));
                    couponDetailView.setStoreIds(storesIds);
                }
            } else {
                couponDetailView.setStoreIds("");
            }
            couponDetailView.setOrderType(limitRule.getOrder_type());
            couponDetailView.setUseScope(limitRule.getApply_store());
            couponDetailView.setIsShare(limitRule.getIs_share());
            Integer source = StringUtils.isBlank((String) item.get("source")) ? 0 : Integer.parseInt(item.get("source").toString());
            couponDetailView.setActiveId(source);
            couponDetailView.setRuleId((int) item.get("rule_id"));
            couponDetailView.setSiteId(siteId);
            couponDetailView.setUrl(url + "&activityId=" + item.get("source").toString()); // zw 备注下这里的ruleId为activityId 之前的遗留
            CouponActivity couponActivity = couponActivityMapper.getCouponActivity(siteId, couponDetailView.getActiveId());
            if (null == couponActivity) {
                // 旧站的老数据转换需要在处理
            } else {

                couponDetailView.setTitle(couponActivity.getTitle());
                couponDetailView.setContent(couponActivity.getContent());
                couponDetailView.setImage(couponActivity.getImage());
            }
        }
        couponDetailView.setUseStatus(Integer.parseInt(item.get("useStatus").toString()));
        isReplaceDistanceResult4CouponView(couponDetailView);

        return couponDetailView;
    }

    private void isReplaceDistanceResult4CouponView(CouponDetailView couponDetailView) {
        if (couponDetailView.getDistanceDiscount() != null || couponDetailView.getDistanceReduce() != null) {
            if (couponDetailView.getDistanceReduce() != null) {
                couponDetailView.getCouponView().setMaxMoney(couponDetailView.getDistanceReduce());
            } else if (couponDetailView.getDistanceDiscount() != null) {
                couponDetailView.getCouponView().setMaxDiscount(couponDetailView.getDistanceDiscount());
            }
        }
    }

    /**
     * 检查优惠券时间
     *
     * @param timeRuleJson
     * @param createDate
     * @return
     */
    public boolean checkEffectiveTimeRule(String timeRuleJson, Date createDate) {
        log.info("-0-----------mqq1111111111---timeRuleJson{}", timeRuleJson);
        try {
            TimeRule timeRule = JSON.parseObject(timeRuleJson, TimeRule.class);
            log.info("-0-----------mqq-333333333333--timeRule{}", ParameterUtil.ObjectConvertJson(timeRule));
            switch (timeRule.getValidity_type()) {

                case 1:
                    //绝对时间
                    LocalDateTime endTime = LocalDate.parse(timeRule.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atTime(0, 0, 0);
                    if (!endTime.isAfter(LocalDateTime.now().minusDays(1))) {
                        log.info("-0-----------mqq-666666666666--timeRule");
                        return false;
                    }
                    break;
                case 2:
                    //相对时间
                    Date endDate = DateUtils.getBeforeOrAfterDate(createDate, timeRule.getHow_day() + timeRule.getDraw_day());
                    if (!endDate.after(new Date())) {
                        log.info("-0-----------mqq-777777777--timeRule");
                        return false;
                    }
                    break;
                case 3:
                    break;
                case 4:
                    SimpleDateFormat formt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date strtTimeag = formt.parse(timeRule.getStartTime());

                    Date endTimeag = formt.parse(timeRule.getEndTime());

                    if (endTimeag.before(new Date())) {
                        log.info("-0-----------mqq-888888888888--timeRule");
                        return false;
                    }
                    break;


            }
        } catch (Exception e) {
            log.info("判断优惠券时间解析异常:" + e);
        }
        return true;
    }


    /**
     * 领券检查优惠券时间判断是否过期
     *
     * @param timeRuleJson
     * @param createDate
     * @return
     */
    public boolean checkEffectiveTimeRuleForOwnCoupon(String timeRuleJson, Date createDate) {
        try {
            TimeRule timeRule = com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON.parseObject(timeRuleJson, TimeRule.class);
            switch (timeRule.getValidity_type()) {
                case 1:
                    //绝对时间
                    LocalDateTime endTime = LocalDate.parse(timeRule.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atTime(0, 0, 0);
                    if (!endTime.isAfter(LocalDateTime.now().minusDays(1))) {
                        return false;
                    }
                    break;
                case 2:
                    //相对时间
                    break;
                case 3:
                    break;
                case 4:
                    SimpleDateFormat formt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date strtTimeag = formt.parse(timeRule.getStartTime());

                    Date endTimeag = formt.parse(timeRule.getEndTime());

                    if (endTimeag.before(new Date()))
                        return false;
                    break;


            }
        } catch (Exception e) {

        }
        return true;
    }

    public Set<Integer> goodsCoupon(List<Map<String, Object>> list) {
        Set<Integer> goodsId = new HashSet<>();
        for (Map<String, Object> map : list) {
            if (Integer.parseInt(map.get("aim_at").toString()) == 1) {
                String goodsRuleParams = map.get("goods_rule") == null ? null : map.get("goods_rule").toString();
                GoodsRule goodsRule = JSON.parseObject(goodsRuleParams, GoodsRule.class);
                if (goodsRule.getType() == 2) {
                    String[] ids = goodsRule.getPromotion_goods().split(",");
                    for (int i = 0; i < ids.length; i++) {
                        goodsId.add(Integer.parseInt(ids[i]));
                    }
                }
            }
        }
        return goodsId;
    }

    public Integer couponUseUnuse(int siteId, int ruleId, int status, Map<String, Object> params) {
        int use = couponDetailMapper.findCouponDetailStatusBySiteIdAndRuleId(siteId, ruleId, params, status);
        return use;
    }

    public List<CouponDetail> getCouponDetailsByTradesId(Integer siteId, @Nonnull Long tradesId) {
        CouponDetailSqlParam couponDetailSqlParam = new CouponDetailSqlParam();
        couponDetailSqlParam.setSiteId(siteId);
        couponDetailSqlParam.setTradesId(tradesId.toString());
        couponDetailSqlParam.setStatus(0);
        List<CouponDetail> couponDetailList = couponDetailMapper.findByParam(couponDetailSqlParam);
        if (couponDetailList.size() != 0 && couponDetailList.size() != 1) // 数据有误
            throw new RuntimeException("数据库数据有误");

        return couponDetailList;
    }
}



