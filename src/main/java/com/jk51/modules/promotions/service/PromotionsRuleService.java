package com.jk51.modules.promotions.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.ParamErrorException;
import com.jk51.exception.UnknownTypeException;
import com.jk51.model.Stores;
import com.jk51.model.coupon.requestParams.LimitRule;
import com.jk51.model.exception.DBDataErrorException;
import com.jk51.model.goods.PageData;
import com.jk51.model.order.Member;
import com.jk51.model.order.response.UsePromotionsParams;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.*;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponActiveForMemberService;
import com.jk51.modules.coupon.service.CouponDetailService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.coupon.service.ParsingCouponRuleService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.service.GoodsService;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.mapper.PromotionsDetailMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.request.OrderDeductionDto;
import com.jk51.modules.promotions.request.ProActivityDto;
import com.jk51.modules.promotions.request.ProCouponRuleDto;
import com.jk51.modules.promotions.request.ProRuleMessageParam;
import com.jk51.modules.promotions.utils.AmountUtils;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.jk51.modules.promotions.constants.PromotionsConstant.*;
import static java.util.stream.Collectors.groupingBy;

/**
 * 活动规则相关的功能实现
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
@SuppressWarnings("Duplicates")
@Service
public class PromotionsRuleService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private PromotionsRuleMapper mapper;
    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;
    @Autowired
    private PromotionsActivityService promotionsActivityService;
    @Autowired
    private VerifyRuleService verifyRuleService;
    @Autowired
    private CouponSendService couponSendService;
    @Autowired
    private OrderDeductionService orderDeductionService;
    @Autowired
    private CouponActiveForMemberService couponActiveForMemberService;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private PromotionsDetailMapper promotionsDetailMapper;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private CouponDetailService couponDetailService;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;
    @Autowired
    private StoresService storesService;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsService goodsService;

    /**
     * 创建活动规则
     *
     * @param promotionsRule
     * @return
     */
    public ReturnDto create(PromotionsRule promotionsRule) {
        ReturnDto checkResult = isValidForCreate(promotionsRule);
        if (checkResult != null) {
            return checkResult;
        }

        promotionsRule.setCreateTime(LocalDateTime.now());
        promotionsRule.setUpdateTime(promotionsRule.getCreateTime());
        promotionsRule.setStatus(10);

        try {
            if (promotionsRule.getPromotionsType().equals(10)) {
                GiftRule giftRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GiftRule.class);
                giftRule.getSendGifts().stream()
                    .forEach(sendGifts -> sendGifts.setTotal(sendGifts.getSendNum()));
                promotionsRule.setPromotionsRule(JSON.toJSONString(giftRule));
            }

            int i = mapper.create(promotionsRule);
            if (i != 1)
                throw new RuntimeException();
        } catch (Exception e) {
            logger.error("活动规则放入数据库出错", e);
            return ReturnDto.buildFailedReturnDto("活动规则放入数据库出错");
        }
        return ReturnDto.buildSuccessReturnDto();
    }


    /**
     * 创建活动规则专用校验
     *
     * @param promotionsRule
     * @return
     */
    private ReturnDto isValidForCreate(PromotionsRule promotionsRule) {
        if (promotionsRule.getSiteId() == null) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }
        if (promotionsRule.getPromotionsType() == null) {
            return ReturnDto.buildFailedReturnDto("promotionsType不能为空");
        }
        if (promotionsRule.getAmount() == null || promotionsRule.getTotal() == null) {
            return ReturnDto.buildFailedReturnDto("数量不能为空");
        }
        if (promotionsRule.getLabel() == null) {
            return ReturnDto.buildFailedReturnDto("标签不能为空");
        }
        if (promotionsRule.getPromotionsRule() == null || promotionsRule.getPromotionsType() == null) {
            return ReturnDto.buildFailedReturnDto("规则不能为空");
        }
        if (promotionsRule.getIsFirstOrder() == null) {
            return ReturnDto.buildFailedReturnDto("是否首单不能为空");
        }

        return null;
    }


    /**
     * 自动改变规则状态，并更改相应的发放状态
     *
     * @param siteId
     * @param promotionsRuleId
     * @return
     */
    public boolean autoChangeStatus(Integer siteId, Integer promotionsRuleId) {
        List<PromotionsActivity> promotionsActivities = promotionsActivityMapper.getPromotionsActivitiesByRuleIdAndSiteId(siteId, promotionsRuleId);
        if (promotionsActivities.size() == 0) {
            return onlyChangeRuleStatus(siteId, promotionsRuleId);
        } else {
            return promotionsActivities.stream()
                .map(promotionsActivity -> promotionsActivityService.autoChangeStatus(siteId, promotionsActivity.getId()))
                .reduce(true, (aBoolean, aBoolean2) -> aBoolean && aBoolean2);
        }
    }

    /**
     * 根据数量，时间，自动判断并变更状态
     *
     * @param siteId
     * @param promotionsRuleId
     * @return true：状态可行；false：状态不可行
     */
    public boolean onlyChangeRuleStatus(Integer siteId, Integer promotionsRuleId) {
        PromotionsRule promotionsRule = mapper.getPromotionsRuleByIdAndSiteId(siteId, promotionsRuleId);

        switch (promotionsRule.getStatus()) {
            case PROMOTIONS_RULE_STATUS_DRAFT: // 该状态必须手动改变
            case PROMOTIONS_RULE_STATUS_STOP:
                return true;
            case PROMOTIONS_RULE_STATUS_RELEASE:
            case PROMOTIONS_RULE_STATUS_NO_INVENTORY:
            case PROMOTIONS_RULE_STATUS_OVERDUE:
                return autoChangeStatusByTimeAndAmount(promotionsRule);
        }

        return false;
    }

    /**
     * 根据时间和数量改变活动规则状态，在可发放，已发完结束，已过期三个状态之间流转
     *
     * @param promotionsRule
     * @return
     */
    private boolean autoChangeStatusByTimeAndAmount(PromotionsRule promotionsRule) {
        if (verifyRuleService.verifyTimeRule(promotionsRule)) {
            return ifNotThenChangeForStatus(promotionsRule, PROMOTIONS_RULE_STATUS_OVERDUE);
        } else {
            if (verifyRuleService.verifyAmount(promotionsRule)) {
                return ifNotThenChangeForStatus(promotionsRule, PROMOTIONS_RULE_STATUS_NO_INVENTORY);
            } else {
                return ifNotThenChangeForStatus(promotionsRule, PROMOTIONS_RULE_STATUS_RELEASE);
            }
        }
    }

    /**
     * 如果状态与校验值不符合就改变数据库内的状态
     *
     * @param promotionsRule
     * @param status
     * @return
     */
    private boolean ifNotThenChangeForStatus(PromotionsRule promotionsRule, int status) {
        if (promotionsRule.getStatus() == status) {
            return true;
        } else {
            int i = mapper.updateStatusByIdAndSiteId(promotionsRule.getId(), promotionsRule.getSiteId(), status);
            return i == 1;
        }
    }

    public PageInfo<?> promRuleList(ProCouponRuleDto param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<PageData> list = mapper.promRuleList(param);
        list.stream().forEach(map -> addPropertyForRuleType(map));
        return new PageInfo<>(list);
    }

    public PageInfo<?> couponRuleList(ProCouponRuleDto param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<PageData> list = mapper.couponRuleList(param);
        list.stream().forEach(map -> addPropertyForRuleType(map));
        return new PageInfo<>(list);
    }


    public Map<Object, Object> addPropertyForRuleType(Map<Object, Object> map) {
        if (StringUtils.equalsIgnoreCase(map.get("ruleType").toString(), "1")) {
            LimitRule limitRule = JSON.parseObject(map.get("limit_rule").toString(), LimitRule.class);
            if (null != limitRule.getApply_store() && limitRule.getApply_store() == -1)
                map.put("useScope", -1);
            else
                map.put("useScope", 1);
            ;

            if (StringUtil.equalsIgnoreCase(limitRule.getApply_channel(), "101,103"))
                map.put("isLine", 1);
            else if (StringUtil.equalsIgnoreCase(limitRule.getApply_channel(), "105"))
                map.put("isLine", 2);
            else if (StringUtil.equalsIgnoreCase(limitRule.getApply_channel(), "101,103,105"))
                map.put("isLine", 3);

            if (null != limitRule.getIs_share() && limitRule.getIs_share() == 0)
                map.put("isShare", 0);
            else if (limitRule.getIs_share() == 1)
                map.put("isShare", 1);
        }
        return map;
    }

    public ReturnDto stopRuleAndActivity(Integer siteId, Integer ruleId, Integer status) {
        try {
            int i = mapper.updateStatusByIdAndSiteId(ruleId, siteId, status);

            if (i == 1) {
                promotionsActivityMapper.getPromotionsActivitiesByRuleIdAndSiteId(siteId, ruleId)
                    .stream()
                    .forEach(promotionsActivity -> promotionsActivityMapper.updateStatusByIdAndSiteId(promotionsActivity.getId(), promotionsActivity.getSiteId(), PROMOTIONS_ACTIVITY_STATUS_STOP));
                return ReturnDto.buildSuccessReturnDto();
            } else
                return ReturnDto.buildFailedReturnDto("因找不到数据导致更新活动规则状态失败");
        } catch (Exception e) {
            logger.error("因找不到数据导致更新活动规则状态失败", e);
            return ReturnDto.buildFailedReturnDto("因更新操作失败导致更新活动规则状态失败");
        }

    }

    public ReturnDto changeStatus(Integer siteId, Integer ruleId, Integer status) {
        try {
            int i = mapper.updateStatusByIdAndSiteId(ruleId, siteId, status);
            if (i == 1)
                return ReturnDto.buildSuccessReturnDto();
            else
                return ReturnDto.buildFailedReturnDto("因找不到数据导致更新活动规则状态失败");
        } catch (Exception e) {
            logger.error("因找不到数据导致更新活动规则状态失败", e);
            return ReturnDto.buildFailedReturnDto("因更新操作失败导致更新活动规则状态失败");
        }
    }

    public ReturnDto edit(PromotionsRule promotionsRule) {
        ReturnDto checkResult = isValidForCreate(promotionsRule);
        if (checkResult != null) {
            return checkResult;
        }
        if (promotionsRule.getId() == null) {
            return ReturnDto.buildFailedReturnDto("id不能为空");
        }

        promotionsRule.setVersion(promotionsRule.getVersion() + 1);
        promotionsRule.setUpdateTime(LocalDateTime.now());

        try {
            int update = mapper.update(promotionsRule);
            if (update != 1)
                return ReturnDto.buildFailedReturnDto("数据库中没有这条数据");
            else
                return ReturnDto.buildSuccessReturnDto();
        } catch (Exception e) {
            logger.error("更新出错", e);
            return ReturnDto.buildFailedReturnDto("更新出错");
        }
    }

    public ReturnDto editPromotionsRuleOneField(Integer siteId, Integer ruleId, String field, String goods) {
        try {
            if (field.equals("goods")) {
                JSONObject jsonObject = JSON.parseObject(goods);
                Integer goodsIdsType = (Integer) jsonObject.get("goodsIdsType");
                String goodsIds = (String) jsonObject.get("goodsIds");
                if (goodsIdsType == null || goodsIds == null) {
                    return ReturnDto.buildFailedReturnDto("商品信息不足，无法修改");
                }

                PromotionsRule rule = mapper.getPromotionsRuleByIdAndSiteId(siteId, ruleId);

                Object promotionsRule = null;
                switch (rule.getPromotionsType()) {
                    case 10:
                        GiftRule giftRule = JSON.parseObject(rule.getPromotionsRule(), GiftRule.class);
                        giftRule.setGoodsIds(goodsIds);
                        promotionsRule = giftRule;
                        break;
                    case 20:
                        DiscountRule discountRule = JSON.parseObject(rule.getPromotionsRule(), DiscountRule.class);
                        discountRule.setGoodsIdsType(goodsIdsType);
                        discountRule.setGoodsIds(goodsIds);
                        promotionsRule = discountRule;
                        break;
                    case 30:
                        FreePostageRule freePostageRule = JSON.parseObject(rule.getPromotionsRule(), FreePostageRule.class);
                        freePostageRule.setGoodsIdsType(goodsIdsType);
                        freePostageRule.setGoodsIds(goodsIds);
                        promotionsRule = freePostageRule;
                        break;
                    case 40:
                        ReduceMoneyRule reduceMoneyRule = JSON.parseObject(rule.getPromotionsRule(), ReduceMoneyRule.class);
                        reduceMoneyRule.setGoodsIdsType(goodsIdsType);
                        reduceMoneyRule.setGoodsIds(goodsIds);
                        promotionsRule = reduceMoneyRule;
                        break;
                    case 50:
                        FixedPriceRule fixedPriceRule = JSON.parseObject(rule.getPromotionsRule(), FixedPriceRule.class);
                        fixedPriceRule.setGoodsIdsType(goodsIdsType);
                        fixedPriceRule.setGoodsIds(goodsIds);
                        promotionsRule = fixedPriceRule;
                        break;
                    case 60:
                        GroupBookingRule groupBookingRule = JSON.parseObject(rule.getPromotionsRule(), GroupBookingRule.class);
                        groupBookingRule.setGoodsIdsType(goodsIdsType);
                        groupBookingRule.setGoodsIds(goodsIds);
                        promotionsRule = groupBookingRule;
                        break;
                    default:
                        return ReturnDto.buildFailedReturnDto("不存在的类型");
                }

                mapper.updateOneFiled(siteId, ruleId, "promotions_rule", JSON.toJSONString(promotionsRule));
                return ReturnDto.buildSuccessReturnDto();
            } else {
                return ReturnDto.buildFailedReturnDto("field " + field + " 无法识别");
            }
        } catch (Exception e) {
            logger.error("系统出错, {}", e);
            return ReturnDto.buildFailedReturnDto("系统出错");
        }
    }

    /**
     * 改变赠品规则中的赠品数量
     *
     * @param siteId
     * @param promotionsRuleId
     * @param map              以giftId为key，改变量为value组装的map
     * @throws Exception
     */
    public void changeGiftNums(int siteId, int promotionsRuleId, Map<Integer, Integer> map) throws Exception {
        PromotionsRule rule = mapper.getPromotionsRuleByIdAndSiteId(siteId, promotionsRuleId);
        if (rule == null || rule.getPromotionsType() == null || !rule.getPromotionsType().equals(10)) {
            throw new RuntimeException("传入的promotionsRuleId有问题：" + promotionsRuleId);
        }

        changeGiftNums(rule, map);
    }

    /**
     * 重载方法，确保需要的数据的前提下使用，可以减少查询次数
     *
     * @param promotionsRule
     * @param map
     * @throws Exception
     */
    public void changeGiftNums(PromotionsRule promotionsRule, Map<Integer, Integer> map) {
        // 改变数量
        GiftRule giftRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GiftRule.class);
        List<GiftRule.sendGifts> sendGifts = giftRule.getSendGifts();
        for (GiftRule.sendGifts sendGift : sendGifts) {
            int flag = -1;
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                if (sendGift.getGiftId().equals(entry.getKey())) {
                    flag = entry.getKey();
                    int sendNum = sendGift.getSendNum() + entry.getValue();
                    if (sendNum < 0)
                        throw new ParamErrorException();

                    sendGift.setSendNum(sendNum);
                    break;
                }
            }
            if (flag != -1) {
                map.remove(flag);
            }
        }
        String json = JSON.toJSONString(giftRule);

        // 存入数据库
        mapper.updateOneFiled(promotionsRule.getSiteId(), promotionsRule.getId(), "promotions_rule", json);
    }

    /**
     * 改变使用数量
     *
     * @param siteId
     * @param promotionsRuleId
     * @param change           改变量，正数为增加，负数为减少
     */
    public void changeUseAmount(int siteId, int promotionsRuleId, int change) {
        PromotionsRule rule = mapper.getPromotionsRuleByIdAndSiteId(siteId, promotionsRuleId);
        if (rule == null) {
            throw new RuntimeException("传入的promotionsRuleId有问题：" + promotionsRuleId);
        }

        changeUseAmount(rule, change);
    }

    /**
     * 重载方法，确保需要的数据的前提下使用，可以减少查询次数
     *
     * @param promotionsRule
     * @param change         改变量，正数为增加，负数为减少
     */
    public void changeUseAmount(PromotionsRule promotionsRule, int change) {
        promotionsRule.setUseAmount(promotionsRule.getUseAmount() + change);

        mapper.updateOneFiled(promotionsRule.getSiteId(), promotionsRule.getId(), "use_amount", promotionsRule.getUseAmount());
    }

    /**
     * 改变生成数量
     *
     * @param siteId
     * @param promotionsRuleId
     * @param change           改变量，正数为增加，负数为减少
     */
    public void changeSendAmount(int siteId, int promotionsRuleId, int change) {
        PromotionsRule rule = mapper.getPromotionsRuleByIdAndSiteId(siteId, promotionsRuleId);
        if (rule == null) {
            throw new RuntimeException("传入的promotionsRuleId有问题：" + promotionsRuleId);
        }

        changeSendAmount(rule, change);
    }

    /**
     * 重载方法，确保需要的数据的前提下使用，可以减少查询次数
     *
     * @param promotionsRule
     * @param change         改变量，正数为增加，负数为减少
     */
    public void changeSendAmount(PromotionsRule promotionsRule, int change) {
        promotionsRule.setSendAmount(promotionsRule.getSendAmount() + change);

        mapper.updateOneFiled(promotionsRule.getSiteId(), promotionsRule.getId(), "send_amount", promotionsRule.getSendAmount());
    }


    /**
     * 订单 活动的 优惠
     *
     * @param proRuleMessageParam
     * @return
     */
    public Map<String, Object> proRuleUsableForMax(ProRuleMessageParam proRuleMessageParam) {
        List<Map<String, Object>> proRuleListForUsable = null;
        Map<String, Object> data = new HashMap<>();
        try {
            if (proRuleMessageParam == null)
                return null;
            proRuleMessageParam.setFirstOrder(couponSendService.isFirstOrder(proRuleMessageParam.getSiteId(), proRuleMessageParam.getUserId()));
            proRuleListForUsable = mapper.proRuleListForUsable(proRuleMessageParam.getSiteId());

            //判断活动是否对该用户可用时间规则校验
            proRuleListForUsable = proRuleListForUsable.stream()
                .filter(map -> checkproRuleTimeRule((String) map.get("time_rule")))
                .filter(map -> couponActiveForMemberService
                    .checkProActivity(proRuleMessageParam.getSiteId(), Integer.parseInt(map.get("proActivityId").toString()), proRuleMessageParam.getUserId()))
                .collect(Collectors.toList());

            //首单订单类型门店校验
            proRuleListForUsable = proRuleListForUsable.stream()
                .filter(map -> checkproRuleFirstOrderTypeStore(proRuleMessageParam, map))
                .collect(Collectors.toList());

            //满足使用规则校验
            proRuleListForUsable = proRuleListForUsable.stream()
                .filter(map -> checkProRuleForPromotionsRule(proRuleMessageParam, map))
                .collect(Collectors.toList());
            if (proRuleListForUsable.isEmpty())
                return null;
            else {
                OrderDeductionDto orderDeductionDto = new OrderDeductionDto(proRuleMessageParam, proRuleListForUsable);
                List<UsePromotionsParams> list = orderDeductionService.countDeduction(orderDeductionDto);
                data.put("proRuleList", list);
                data.put("proRuleDeductionPrice", list.stream().mapToInt(UsePromotionsParams::getDeductionMoney).sum());
                data.put("isHaveGiftProRuleActivity", list.stream().anyMatch(map -> map.getPromotionsType() == 10));
                if (Boolean.parseBoolean(data.get("isHaveGiftProRuleActivity").toString())) {
                    UsePromotionsParams usePromotionsParams = list.stream()
                        .filter(map -> map.getPromotionsType() == 10)
                        .findFirst()
                        .get();

                    SelectGiftByGoodsIdParms params = new SelectGiftByGoodsIdParms() {{
                        setSiteId(proRuleMessageParam.getSiteId());
                        setId(usePromotionsParams.getPromotionsId());
                        setGoodsInfo(JSON.toJSONString(proRuleMessageParam.getGoodsInfo()));
                    }};

                    Map<String, Object> giftRuleMap = goodsService.selectGiftByGoodsIdParms(params);
                    List<Map<String, Object>> giftList = (List) giftRuleMap.get("giftList");

                    if (null != giftRuleMap.get("giftList") && giftList.size() == 1) {
                        data.put("giftRuleMsg", goodsService.selectGiftByGoodsIdParms(params));
                    }
                    data.put("promotionsRuleId", usePromotionsParams.getPromotionsId());
                    data.put("promotionsActivityId", usePromotionsParams.getPromActivityId());
                }
                return data;
            }
        } catch (Exception e) {
            logger.info("活动解析异常:" + e);
            return null;
        }

    }

    public boolean checkproRuleTimeRule(String proRuleTimeRuleForJson) {


        try {
            TimeRuleForPromotionsRule timeRuleForPromotionsRule = JSON.parseObject(proRuleTimeRuleForJson, TimeRuleForPromotionsRule.class);
            switch (timeRuleForPromotionsRule.getValidity_type()) {
                case 1:
                    //绝对时间
                    String format = "yyyy-MM-dd hh:mm:ss";
                    Date startTime = DateUtils.parseDate(timeRuleForPromotionsRule.getStartTime(), format);
                    Date endTime = DateUtils.parseDate(timeRuleForPromotionsRule.getEndTime(), format);
                    if (!(startTime.before(new Date()) && endTime.after(new Date())))
                        return false;
                    break;
                case 2:
                    //按照月份的日期

                    //当前月份最后一天
                    Calendar cale = Calendar.getInstance();
                    cale.set(Calendar.MONTH, LocalDateTime.now().getMonthValue());
                    cale.set(Calendar.DAY_OF_MONTH, 0);
                    Integer lastday = cale.get(Calendar.DAY_OF_MONTH);

                    String[] days = timeRuleForPromotionsRule.getAssign_rule().split(",");
                    Set<String> set = new HashSet<String>(Arrays.asList(days));
                    String dayOfMonth = LocalDateTime.now().getDayOfMonth() + "";

                    if (!(set.contains(dayOfMonth) || checklastDayWork(proRuleTimeRuleForJson, lastday)))
                        return false;
                    break;
                case 3:
                    //按照星期
                    String[] assign_rule_weeks = timeRuleForPromotionsRule.getAssign_rule().split(",");
                    Set<String> set_week = new HashSet<String>(Arrays.asList(assign_rule_weeks));

                    String dayOfWeek = LocalDateTime.now().getDayOfWeek() + "";
                    if (dayOfWeek.equals("MONDAY"))
                        dayOfWeek = "1";
                    else if (dayOfWeek.equals("TUESDAY"))
                        dayOfWeek = "2";
                    else if (dayOfWeek.equals("WEDNESDAY"))
                        dayOfWeek = "3";
                    else if (dayOfWeek.equals("THURSDAY"))
                        dayOfWeek = "4";
                    else if (dayOfWeek.equals("FRIDAY"))
                        dayOfWeek = "5";
                    else if (dayOfWeek.equals("SATURDAY"))
                        dayOfWeek = "6";
                    else if (dayOfWeek.equals("SUNDAY"))
                        dayOfWeek = "7";
                    if (!(set_week.contains(dayOfWeek)))
                        return false;
                    break;

            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean checklastDayWork(String proRuleTimeRuleForJson, int lastday) {
        TimeRuleForPromotionsRule timeRuleForPromotionsRule = JSON.parseObject(proRuleTimeRuleForJson, TimeRuleForPromotionsRule.class);
        if (null == timeRuleForPromotionsRule.getLastDayWork() || timeRuleForPromotionsRule.getLastDayWork() == 0)
            return false;
        else if ((lastday - LocalDateTime.now().getDayOfMonth()) < timeRuleForPromotionsRule.getLastDayWork())
            return true;
        else
            return false;
    }

    public boolean checkproRuleFirstOrderTypeStore(ProRuleMessageParam proRuleMessageParam, Map<String, Object> paramMap) {
        if (proRuleMessageParam == null || paramMap == null)
            return false;

        //满赠活动和满赠券冲突 如果已经选中满赠券则不给满赠活动的信息
        if (null != proRuleMessageParam.getCouponId()) {
            Map<String, Object> couponDetail = couponDetailMapper.findWxCouponDetailById(proRuleMessageParam.getSiteId(), proRuleMessageParam.getCouponId());
            if (null != couponDetail
                && null != couponDetail.get("coupon_type")
                && StringUtils.equalsIgnoreCase("500", couponDetail.get("coupon_type").toString())
                && StringUtils.equalsIgnoreCase("10", paramMap.get("promotions_type").toString()))
                return false;
        }

        //限价活动和限价券冲突 如果已经选中限价券则不给限价活动的信息
        if (null != proRuleMessageParam.getCouponId()) {
            Map<String, Object> couponDetail = couponDetailMapper.findWxCouponDetailById(proRuleMessageParam.getSiteId(), proRuleMessageParam.getCouponId());
            if (null != couponDetail
                && null != couponDetail.get("coupon_type")
                && StringUtils.equalsIgnoreCase("300", couponDetail.get("coupon_type").toString())
                && StringUtils.equalsIgnoreCase("50", paramMap.get("promotions_type").toString()))
                return false;
        }

        //校验是否首单可用
        switch (Integer.parseInt(paramMap.get("is_first_order").toString())) {
            case 0:
                break;
            case 1:
                if (!proRuleMessageParam.isFirstOrder())
                    return false;
                break;
        }

        //如果是门店自提直接把送货上门的活动去掉
        if ((null == proRuleMessageParam.getOrderType()
            || proRuleMessageParam.getOrderType() == 100)
            && Integer.parseInt(paramMap.get("promotions_type").toString()) == 30)
            return false;

        if (proRuleMessageParam.getOrderType() == 200
            && null == proRuleMessageParam.getReceiverCityCode())
            return false;


        //校验订单类型
        String[] orderTytpes = ((String) paramMap.get("order_type")).split(",");
        Set<String> set = new HashSet<String>(Arrays.asList(orderTytpes));
        if (null == proRuleMessageParam.getOrderType())
            return false;
        else if (!set.contains(proRuleMessageParam.getOrderType().toString()))
            return false;


        //校验门店信息
        switch ((String) paramMap.get("use_store")) {
            case "-1":
                break;
            case "1":
                Set<String> useArea = new HashSet<>(Arrays.asList(paramMap.get("use_area").toString().split(",")));
                return useArea.contains(proRuleMessageParam.getStoreId().toString());
            case "2":
                String thrAreaId = null;
                if (proRuleMessageParam.getOrderType() == 200)
                    thrAreaId = proRuleMessageParam.getReceiverCityCode().toString();
                else if (proRuleMessageParam.getOrderType() == 100) {
                    Stores store = storesService.getStore(proRuleMessageParam.getStoreId(), proRuleMessageParam.getSiteId());
                    thrAreaId = store != null ? store.getCity_id() + "" : "";
                }

                String[] stores = ((String) paramMap.get("use_area")).split(",");
                Set<String> setStores = new HashSet<String>(Arrays.asList(stores));
                if (null == proRuleMessageParam.getStoreId() || !setStores.contains(thrAreaId))
                    return false;
        }
        return true;
    }


    //优惠活动使用规则校验总入口
    public boolean checkProRuleForPromotionsRule(ProRuleMessageParam proRuleMessageParam, Map<String, Object> paramMap) {
        switch (Integer.parseInt(paramMap.get("promotions_type").toString())) {
            case 10:
                return checkGiftRule(proRuleMessageParam, paramMap.get("promotions_rule").toString());
            case 20:
                return checkDiscountRule(proRuleMessageParam, paramMap.get("promotions_rule").toString());
            case 30:
                return checkFreePostageRule(proRuleMessageParam, paramMap.get("promotions_rule").toString());
            case 40:
                return checkReduceMoneyRule(proRuleMessageParam, paramMap.get("promotions_rule").toString());
            case 50:
                return checkControlMoneyRule(proRuleMessageParam, paramMap);
        }
        return false;
    }

    //校验满赠活动
    public boolean checkGiftRule(ProRuleMessageParam proRuleMessageParam, String giftRuleJson) {
        try {
            //**满赠的校验 按照原价进行计算(只有上层拆单过程 goodsPrice价格才会改变 注意商品参数中有个origingoodsPrice 即为goodsPrice)
            GiftRule giftRule = JSON.parseObject(giftRuleJson, GiftRule.class);
            switch (giftRule.getRuleType()) {
                case 1:
                    return checkContainNum(proRuleMessageParam, giftRuleJson);
                case 2:
                    return checkContainMoney(proRuleMessageParam, giftRuleJson);
            }
        } catch (Exception e) {
            logger.info("校验满赠活动异常:{}", e);
            return false;
        }

        return false;
    }

    //满赠活动满件
    private boolean checkContainNum(ProRuleMessageParam proRuleMessageParam, String giftRuleJson) {
        try {
            GiftRule giftRule = JSON.parseObject(giftRuleJson, GiftRule.class);
            List<Map<String, Integer>> goodsInfo = proRuleMessageParam.getGoodsInfo();
            //单种商品满足条件才可
            if (giftRule.getCalculateBase() == 1) {
                return goodsInfo.stream()
                    .anyMatch(map -> new HashSet<>(
                        Arrays.asList(giftRule.getGoodsIds().split(",")))
                        .contains(map.get("goodsId").toString())
                        && map.get("num") >= giftRule.getRuleConditions().get(0).getMeetNum());

                //多种商品组合满足条件即可
            } else if (giftRule.getCalculateBase() == 2) {
                int totalNum = goodsInfo.stream()
                    .filter(map -> new HashSet<>(
                        Arrays.asList(giftRule.getGoodsIds().split(",")))
                        .contains(map.get("goodsId").toString())).mapToInt(map -> map.get("num")).sum();
                if (totalNum >= giftRule.getRuleConditions().get(0).getMeetNum())
                    return true;
            }
        } catch (Exception e) {
            logger.error("满赠活动满件异常:" + e);
            return false;
        }

        return false;
    }

    //满赠活动满元减
    private boolean checkContainMoney(ProRuleMessageParam proRuleMessageParam, String giftRuleJson) {
        try {
            GiftRule giftRule = JSON.parseObject(giftRuleJson, GiftRule.class);
            List<Map<String, Integer>> goodsInfo = proRuleMessageParam.getGoodsInfo();

            //单种商品满足条件才可
            if (giftRule.getCalculateBase() == 1) {
                return goodsInfo.stream()
                    .anyMatch(map -> new HashSet<>(
                        Arrays.asList(giftRule.getGoodsIds().split(",")))
                        .contains(map.get("goodsId").toString())
                        && map.get("num") * map.get("origingoodsPrice") >= giftRule.getRuleConditions().get(0).getMeetMoney());

                //组合商品满足条件即可
            } else if (giftRule.getCalculateBase() == 2) {
                int totalMoney = goodsInfo.stream()
                    .filter(map -> new HashSet<>(
                        Arrays.asList(giftRule.getGoodsIds().split(",")))
                        .contains(map.get("goodsId").toString()))
                    .mapToInt(map -> map.get("num") * map.get("origingoodsPrice"))
                    .sum();

                if (totalMoney >= giftRule.getRuleConditions().get(0).getMeetMoney())
                    return true;
            }
        } catch (Exception e) {
            logger.error("满赠活动满元解析异常:{}", e);
            return false;
        }

        return false;
    }

    //打折活动校验
    public boolean checkDiscountRule(ProRuleMessageParam proRuleMessageParam, String discountRule) {
        try {
            //如果优惠券的减扣金额大于商品总价  就不在参与满减,满折，限价活动
            if (proRuleMessageParam.getTotalPrice() <= proRuleMessageParam.getCouponPrice())
                return false;
            DiscountRule discountRule1 = JSON.parseObject(discountRule, DiscountRule.class);
            switch (discountRule1.getRuleType()) {
                case 1:
                case 5:
                    return checkDirectDiscount(proRuleMessageParam, discountRule);
                case 2:
                    return checkDisCountMoney(proRuleMessageParam, discountRule);
                case 3:
                    return checkDisCountNum(proRuleMessageParam, discountRule);
                case 4:
                    return checkMoreGoodsDirectCount(proRuleMessageParam, discountRule);
            }
        } catch (Exception e) {
            logger.error("打折活动校验异常:{}", e);
            return false;
        }

        return false;
    }

    //打折活动直折
    private boolean checkDirectDiscount(ProRuleMessageParam proRuleMessageParam, String discountRule) {
        try {
            DiscountRule discountRuleparam = JSON.parseObject(discountRule, DiscountRule.class);
            List<Map<String, Integer>> goodsInfo = proRuleMessageParam.getGoodsInfo();
            if (discountRuleparam.getGoodsIdsType() == 0)
                return true;
            else if (discountRuleparam.getGoodsIdsType() == 1)
                return goodsInfo.stream().anyMatch(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSingleDisCountPro") && new HashSet<String>(Arrays.asList(discountRuleparam.getGoodsIds().split(",")))
                    .contains(map.get("goodsId").toString()));
            else if (discountRuleparam.getGoodsIdsType() == 2)
                return goodsInfo.stream().anyMatch(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSingleDisCountPro") && !new HashSet<String>(Arrays.asList(discountRuleparam.getGoodsIds().split(",")))
                    .contains(map.get("goodsId").toString()));

        } catch (Exception e) {
            logger.info("直折活动解析异常:" + e);
            return false;
        }
        return false;
    }

    //打折活动满件
    private boolean checkDisCountNum(ProRuleMessageParam proRuleMessageParam, String discountRule) {
        try {
            DiscountRule discountRuleparam = JSON.parseObject(discountRule, DiscountRule.class);
            List<Map<String, Integer>> goodsInfo = proRuleMessageParam.getGoodsInfo();
            if (discountRuleparam.getGoodsIdsType() == 0) {
                List<Map<String, Integer>> mapList = discountRuleparam.getRules();
                int totalNum = goodsInfo.stream().filter(stringIntegerMap -> null == stringIntegerMap.get("theGoodsContainsControlPrice") && null == stringIntegerMap.get("theGoodsContainsSingleDisCountPro")).mapToInt(map -> map.get("num")).sum();
                int num = mapList.get(0).get("meet_num");
                if (num <= totalNum)
                    return true;
            } else if (discountRuleparam.getGoodsIdsType() == 1) {
                List<Map<String, Integer>> mapList = discountRuleparam.getRules();
                int totalNum = goodsInfo.stream().filter(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSingleDisCountPro") && new HashSet<String>(Arrays.asList(discountRuleparam.getGoodsIds().split(",")))
                    .contains(map.get("goodsId").toString()))
                    .mapToInt(map -> map.get("num")).sum();
                int num = mapList.get(0).get("meet_num");
                if (num <= totalNum)
                    return true;
            } else if (discountRuleparam.getGoodsIdsType() == 2) {
                List<Map<String, Integer>> mapList = discountRuleparam.getRules();
                int totalNum = goodsInfo.stream().filter(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSingleDisCountPro") && !new HashSet<String>(Arrays.asList(discountRuleparam.getGoodsIds().split(",")))
                    .contains(map.get("goodsId").toString())).mapToInt(map -> map.get("num")).sum();
                int num = mapList.get(0).get("meet_num");
                if (num <= totalNum)
                    return true;
            }
        } catch (Exception e) {
            logger.info("满件打折活动解析异常:" + e);
            return false;
        }
        return false;
    }

    //打折活动满元
    private boolean checkDisCountMoney(ProRuleMessageParam proRuleMessageParam, String discountRule) {
        try {
            DiscountRule discountRuleparam = JSON.parseObject(discountRule, DiscountRule.class);
            List<Map<String, Integer>> goodsInfo = proRuleMessageParam.getGoodsInfo();
            if (discountRuleparam.getGoodsIdsType() == 0) {
                List<Map<String, Integer>> mapList = discountRuleparam.getRules();
                int totalMoney = goodsInfo.stream().filter(stringIntegerMap -> null == stringIntegerMap.get("theGoodsContainsControlPrice") && null == stringIntegerMap.get("theGoodsContainsSingleDisCountPro")).mapToInt(map -> map.get("num") * map.get("goodsPrice")).sum();
               /* if (discountRuleparam.getIsPost() == 2)
                    totalMoney += totalMonegy + proRuleMessageParam.getPostFee();*/
                int money = mapList.get(0).get("meet_money");
                if (money <= totalMoney)
                    return true;
            } else if (discountRuleparam.getGoodsIdsType() == 1) {
                List<Map<String, Integer>> mapList = discountRuleparam.getRules();
                int totalMoney = goodsInfo.stream().filter(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSingleDisCountPro") && new HashSet<String>(Arrays.asList(discountRuleparam.getGoodsIds().split(",")))
                    .contains(map.get("goodsId").toString())).mapToInt(map -> map.get("num") * map.get("goodsPrice")).sum();
               /* if (discountRuleparam.getIsPost() == 2)
                    totalMoney += totalMoney + proRuleMessageParam.getPostFee();*/
                int money = mapList.get(0).get("meet_money");
                if (money <= totalMoney)
                    return true;
            } else if (discountRuleparam.getGoodsIdsType() == 2) {
                List<Map<String, Integer>> mapList = discountRuleparam.getRules();
                int totalMoney = goodsInfo.stream().filter(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSingleDisCountPro") && !new HashSet<String>(Arrays.asList(discountRuleparam.getGoodsIds().split(",")))
                    .contains(map.get("goodsId").toString())).mapToInt(map -> map.get("num") * map.get("goodsPrice")).sum();
               /* if (discountRuleparam.getIsPost() == 2)
                    totalMoney += totalMoney + proRuleMessageParam.getPostFee();*/
                int money = mapList.get(0).get("meet_money");
                if (money <= totalMoney)
                    return true;
            }
        } catch (Exception e) {
            logger.info("满元打折活动解析异常:" + e);
            return false;
        }


        return false;
    }

    //打折活动直折
    private boolean checkMoreGoodsDirectCount(ProRuleMessageParam proRuleMessageParam, String discountRule) {
        try {
            List<Map<String, Integer>> goodsInfo = proRuleMessageParam.getGoodsInfo();
            DiscountRule discountRuleparam = JSON.parseObject(discountRule, DiscountRule.class);
            if (discountRuleparam.getGoodsIdsType() == 0)
                return goodsInfo.stream().anyMatch(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSingleDisCountPro") && map.get("num") >= discountRuleparam.getRules().get(0).get("rate"));
            else if (discountRuleparam.getGoodsIdsType() == 1) {
                return goodsInfo.stream().anyMatch(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSingleDisCountPro") && new HashSet<String>(Arrays.asList(discountRuleparam.getGoodsIds().split(",")))
                    .contains(map.get("goodsId").toString()) && map.get("num") >= discountRuleparam.getRules().get(0).get("rate"));
            } else if (discountRuleparam.getGoodsIdsType() == 2) {
                return goodsInfo.stream().anyMatch(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSingleDisCountPro") && !new HashSet<String>(Arrays.asList(discountRuleparam.getGoodsIds().split(",")))
                    .contains(map.get("goodsId").toString()) && map.get("num") >= discountRuleparam.getRules().get(0).get("rate"));
            }
        } catch (Exception e) {
            logger.info("半折活动解析异常:" + e);
            return false;
        }

        return false;
    }

    //包邮活动校验
    public boolean checkFreePostageRule(ProRuleMessageParam proRuleMessageParam, String freePostageRule) {
        try {
            FreePostageRule freePostageRuleParam = JSON.parseObject(freePostageRule, FreePostageRule.class);
            int totalOrderMoney = 0;
            if (freePostageRuleParam.getGoodsIdsType() == 0)
                totalOrderMoney = proRuleMessageParam.getGoodsInfo().stream().mapToInt(map -> map.get("num") * map.get("goodsPrice")).sum();
            if (freePostageRuleParam.getGoodsIdsType() == 1)
                totalOrderMoney = proRuleMessageParam.getGoodsInfo()
                    .stream()
                    .filter(map -> new HashSet<String>(Arrays.asList(freePostageRuleParam.getGoodsIds().split(",")))
                        .contains(map.get("goodsId").toString())).collect(Collectors.toList()).stream().mapToInt(map -> map.get("num") * map.get("origingoodsPrice")).sum();
            if (freePostageRuleParam.getGoodsIdsType() == 2)
                totalOrderMoney = proRuleMessageParam.getGoodsInfo()
                    .stream()
                    .filter(map -> !new HashSet<String>(Arrays.asList(freePostageRuleParam.getGoodsIds().split(",")))
                        .contains(map.get("goodsId").toString())).collect(Collectors.toList()).stream().mapToInt(map -> map.get("num") * map.get("origingoodsPrice")).sum();

            if (totalOrderMoney < freePostageRuleParam.getMeetMoney())
                return false;

            switch (freePostageRuleParam.getAreaIdsType()) {
                case 1:
                    return new HashSet<String>(Arrays.asList(freePostageRuleParam.getAreaIds().split(","))).contains(proRuleMessageParam.getReceiverCityCode().toString());
                case 2:
                    return !(new HashSet<String>(Arrays.asList(freePostageRuleParam.getAreaIds().split(","))).contains(proRuleMessageParam.getReceiverCityCode().toString()));

            }
        } catch (Exception e) {
            logger.info("包邮活动解析异常:" + e);
            return false;
        }

        return false;
    }

    //满减活动解析过滤
    public boolean checkReduceMoneyRule(ProRuleMessageParam proRuleMessageParam, String reduceMoneyRule) {
        try {
            //如果优惠券的减扣金额大于商品总价  就不在参与满减,满折，限价活动
            if (proRuleMessageParam.getTotalPrice() <= proRuleMessageParam.getCouponPrice())
                return false;
            ReduceMoneyRule reduceMoneyRuleParam = JSON.parseObject(reduceMoneyRule, ReduceMoneyRule.class);
            switch (reduceMoneyRuleParam.getRuleType()) {
                case 1:
                    return checkDirectReduceMoneyRule(proRuleMessageParam, reduceMoneyRuleParam);
                case 2:
                case 3:
                    return checkContainReduceMoneyRule(proRuleMessageParam, reduceMoneyRuleParam);
            }

        } catch (Exception e) {
            logger.info("满减解析异常:" + e);
            return false;
        }
        return false;
    }

    //满减活动直减过滤
    public boolean checkDirectReduceMoneyRule(ProRuleMessageParam proRuleMessageParam, ReduceMoneyRule reduceMoneyRuleParam) {
        try {
            if (reduceMoneyRuleParam.getGoodsIdsType() == 0)
                return proRuleMessageParam.getGoodsInfo().stream().anyMatch(map -> null == map.get("theGoodsContainsSinglereDuPro"));
            else if (reduceMoneyRuleParam.getGoodsIdsType() == 1)
                return proRuleMessageParam.getGoodsInfo().stream().anyMatch(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSinglereDuPro") && new HashSet<String>(Arrays.asList(reduceMoneyRuleParam.getGoodsIds().split(","))).contains(map.get("goodsId").toString()));
            else if (reduceMoneyRuleParam.getGoodsIdsType() == 2)
                return proRuleMessageParam.getGoodsInfo().stream().anyMatch(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSinglereDuPro") && !new HashSet<String>(Arrays.asList(reduceMoneyRuleParam.getGoodsIds().split(","))).contains(map.get("goodsId").toString()));
        } catch (Exception e) {
            logger.info("满减活动直减解析异常:" + e);
            return false;
        }
        return false;
    }

    //满减活动每满减，满减
    public boolean checkContainReduceMoneyRule(ProRuleMessageParam proRuleMessageParam, ReduceMoneyRule reduceMoneyRuleParam) {
        try {
            if (reduceMoneyRuleParam.getGoodsIdsType() == 0)
                return proRuleMessageParam.getGoodsInfo().stream().filter(map -> map.get("theGoodsContainsSinglereDuPro") == null).mapToInt(map -> map.get("goodsPrice") * map.get("num")).sum() >= reduceMoneyRuleParam.getRules().get(0).getMeetMoney();
            else if (reduceMoneyRuleParam.getGoodsIdsType() == 1)
                return proRuleMessageParam.getGoodsInfo().stream()
                    .filter(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSinglereDuPro") && new HashSet<String>(Arrays.asList(reduceMoneyRuleParam.getGoodsIds().split(","))).contains(map.get("goodsId").toString()))
                    .mapToInt(map -> map.get("goodsPrice") * map.get("num")).sum() >= reduceMoneyRuleParam.getRules().get(0).getMeetMoney();
            else if (reduceMoneyRuleParam.getGoodsIdsType() == 2)
                return proRuleMessageParam.getGoodsInfo().stream()
                    .filter(map -> null == map.get("theGoodsContainsControlPrice") && null == map.get("theGoodsContainsSinglereDuPro") && !new HashSet<String>(Arrays.asList(reduceMoneyRuleParam.getGoodsIds().split(","))).contains(map.get("goodsId").toString()))
                    .mapToInt(map -> map.get("goodsPrice") * map.get("num")).sum() >= reduceMoneyRuleParam.getRules().get(0).getMeetMoney();
        } catch (Exception e) {
            logger.info("满减活动直减解析异常:" + e);
            return false;
        }
        return false;
    }

    //限价活动解析过滤
    public boolean checkControlMoneyRule(ProRuleMessageParam proRuleMessageParam, Map<String, Object> paramMap) {
        try {
            //如果优惠券的减扣金额大于商品总价  就不在参与满减,满折，限价活动
            if (proRuleMessageParam.getTotalPrice() <= proRuleMessageParam.getCouponPrice())
                return false;
            String fixPriceRule = paramMap.get("promotions_rule").toString();
            Integer proActivityId = Integer.parseInt(paramMap.get("proActivityId").toString());
            Integer proRuleId = Integer.parseInt(paramMap.get("id").toString());
            FixedPriceRule fixedPriceRule = JSON.parseObject(fixPriceRule, FixedPriceRule.class);
            switch (fixedPriceRule.getGoodsIdsType()) {
                case 0:
                    return checkFixPriceNoGoodsControl(proRuleMessageParam, fixedPriceRule, proActivityId, proRuleId);
                case 1:
                case 2:
                    return checkFixPriceIsGoodsControl(proRuleMessageParam, fixedPriceRule, proActivityId, proRuleId);
            }
        } catch (Exception e) {
            logger.info("限价活动解析异常" + e);
            return false;
        }
        return false;
    }

    //限价活动指定商品
    public boolean checkFixPriceIsGoodsControl(ProRuleMessageParam proRuleMessageParam, FixedPriceRule fixedPriceRule, Integer proActivityId, Integer proRuleId) {
        try {
            Member member = memberMapper.getMemberByMemberId(proRuleMessageParam.getSiteId(), proRuleMessageParam.getUserId());
            if (fixedPriceRule.getGoodsIdsType() == 1)
                return proRuleMessageParam.getGoodsInfo().stream().filter(map -> new HashSet<String>(Arrays.asList(fixedPriceRule.getGoodsIds().split(","))).contains(map.get("goodsId").toString())).anyMatch(map -> map.get("num") <= fixedPriceRule.getBuyNumEachOrder()
                    && (map.get("num") + promotionsDetailMapper.getUseBuyedGoodsNum(proRuleMessageParam.getSiteId(), map.get("goodsId"), member.getBuyerId(), proActivityId, proRuleId))
                    <= fixedPriceRule.getTotal());
            else if (fixedPriceRule.getGoodsIdsType() == 2)
                return proRuleMessageParam.getGoodsInfo().stream().filter(map -> !new HashSet<String>(Arrays.asList(fixedPriceRule.getGoodsIds().split(","))).contains(map.get("goodsId").toString())).anyMatch(map -> map.get("num") <= fixedPriceRule.getBuyNumEachOrder()
                    && (map.get("num") + promotionsDetailMapper.getUseBuyedGoodsNum(proRuleMessageParam.getSiteId(), map.get("goodsId"), member.getBuyerId(), proActivityId, proRuleId))
                    <= fixedPriceRule.getTotal());
        } catch (Exception e) {
            logger.info("限价活动指定商品解析异常" + e);
            return false;
        }
        return false;
    }

    //限价活动任意商品
    public boolean checkFixPriceNoGoodsControl(ProRuleMessageParam proRuleMessageParam, FixedPriceRule fixedPriceRule, Integer proActivityId, Integer proRuleId) {
        try {
            Member member = memberMapper.getMemberByMemberId(proRuleMessageParam.getSiteId(), proRuleMessageParam.getUserId());
            return proRuleMessageParam.getGoodsInfo().stream().anyMatch(map -> map.get("num") <= fixedPriceRule.getBuyNumEachOrder()
                && (map.get("num") + promotionsDetailMapper.getUseBuyedGoodsNum(proRuleMessageParam.getSiteId(), map.get("goodsId"), member.getBuyerId(), proActivityId, proRuleId))
                <= fixedPriceRule.getTotal());
        } catch (Exception e) {
            logger.info("限价活动任意商品解析异常" + e);
            return false;
        }
    }

    public PageInfo<?> choosePromList(ProCouponRuleDto param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<PageData> list = mapper.choosePromList(param);
        return new PageInfo<>(list);

    }

    //优惠活动优惠详情读取总入口
    public ProCouponRuleView getProCouponRuleViewForProRule(Integer siteId, Integer proRuleId) {
        PromotionsRule promotionsRule = mapper.getPromotionsRuleByIdAndSiteId(siteId, proRuleId);
        return promotionsRuleForType(promotionsRule.getPromotionsType(), promotionsRule.getPromotionsRule());
    }

    public ProCouponRuleView promotionsRuleForType(Integer promotionsType, String proMotionsRule) {
        switch (promotionsType) {
            case 10:
                return GiftRuleForProCouponRuleView(proMotionsRule);
            case 20:
                return DiscountRuleForProCouponRuleView(proMotionsRule);
            case 30:
                return FreePostageRuleForProCouponRuleView(proMotionsRule);
            case 40:
                return ReDuceMoneyRuleForProCouponRuleView(proMotionsRule);
            case 50:
                return ConTrolMoneyRuleForProCouponRuleView(proMotionsRule);
            case 60:
                return GroupPurchaseForProCouponRuleView(proMotionsRule);
        }
        return null;
    }


    public ProCouponRuleView GroupPurchaseForProCouponRuleView(String proMotionsRule) {
        try {


            ProCouponRuleView proCouponRuleView = new ProCouponRuleView();
            proCouponRuleView.setIsAllType(1);
            GroupBookingRule groupBookingRule = JSON.parseObject(proMotionsRule, GroupBookingRule.class);

            String proruleDetail = "";
            String temp = "团购活动,";
            if (groupBookingRule.getRuleType().intValue() == 1) {
                proruleDetail = "统一拼团价" + AmountUtils.changeF2Y(groupBookingRule.getRules().get(0).get("groupPrice").toString()) + "元,成团人数" + groupBookingRule.getRules().get(0).get("groupMemberNum") + "人,限购" + groupBookingRule.getRules().get(0).get("goodsLimitNum") +
                    "件，开团后" + groupBookingRule.getGroupLiveTime() + "小时内有效,超过时间自动取消,付款原路返回";
            } else if (groupBookingRule.getRuleType().intValue() == 2) {
                proruleDetail = "拼团价格,成团人数,限购件数请参考相应商品,开团后" + groupBookingRule.getGroupLiveTime() + "小时内有效,超过时间自动取消,付款原路返回";
            } else if (groupBookingRule.getRuleType().intValue() == 3) {
                proruleDetail = "商品打" + AmountUtils.changeDiscount(groupBookingRule.getRules().get(0).get("groupDiscount").toString()) + "折,成员人数" + groupBookingRule.getRules().get(0).get("groupMemberNum") + "人,限购" + groupBookingRule.getRules().get(0).get("goodsLimitNum") + "件,开团后" + groupBookingRule.getGroupLiveTime() + "小时内有效,超过时间自动取消,付款原路返回";
            } else if (groupBookingRule.getRuleType().intValue() == 4) {
                proruleDetail = "商品打折,成团人数,限购件数请参考相应商品,开团后" + groupBookingRule.getGroupLiveTime() + "小时内有效,超过时间自动取消,付款原路返回";
            }
            proruleDetail = temp + proruleDetail;
            if(groupBookingRule.getRuleType()==3||groupBookingRule.getRuleType()==4) {
                proruleDetail += appendInfoForDiscountRule(groupBookingRule.getIsMl(), groupBookingRule.getIsRound());
            }
            proCouponRuleView.setProruleDetail(proruleDetail);

            return proCouponRuleView;
        } catch (Exception e) {
            logger.info("拼团活动解析详情异常" + e);
            return null;
        }
    }

    public ProCouponRuleView GiftRuleForProCouponRuleView(String proMotionsRule) {
        try {
            ProCouponRuleView proCouponRuleView = new ProCouponRuleView();
            proCouponRuleView.setIsAllType(1);
            GiftRule giftRule = JSON.parseObject(proMotionsRule, GiftRule.class);
            proCouponRuleView.setObj(giftRule);
            String proruleDetail = "";
            String temp = "购买指定商品";
            if (giftRule.getRuleType() == 1)
                proruleDetail = giftRule.getRuleConditions().stream().map(map -> "满" + map.getMeetNum() + "件送" + map.getSendNum() + "件").collect(Collectors.joining(","));
            else if (giftRule.getRuleType() == 2)
                proruleDetail = giftRule.getRuleConditions().stream().map(map -> "满" + AmountUtils.changeF2Y(map.getMeetMoney().toString()) + "元送" + map.getSendNum() + "件").collect(Collectors.joining(","));
            proruleDetail = temp + proruleDetail;
            proCouponRuleView.setMaxSendNum(Collections.max(giftRule.getRuleConditions().stream().map(map -> map.getSendNum()).collect(Collectors.toList())));
            proCouponRuleView.setProruleDetail(proruleDetail);
            return proCouponRuleView;
        } catch (Exception e) {
            logger.info("满赠优惠活动解析详情异常" + e);
            return null;
        }


    }

    public ProCouponRuleView DiscountRuleForProCouponRuleView(String proMotionsRule) {
        try {
            ProCouponRuleView proCouponRuleView = new ProCouponRuleView();
            DiscountRule discountRule = JSON.parseObject(proMotionsRule, DiscountRule.class);
            proCouponRuleView.setObj(discountRule);

            if (discountRule.getGoodsIdsType() == 0)
                proCouponRuleView.setIsAllType(0);
            else
                proCouponRuleView.setIsAllType(1);

            String proruleDetail = "";
            if (discountRule.getRuleType() == 1) {
                proruleDetail = discountRule.getRules().stream().map(map -> "商品总价打" + AmountUtils.changeDiscount(map.get("direct_discount").toString()) + "折").collect(Collectors.joining(","));
                if (null != discountRule.getRules().get(0).get("goods_money_limit") && Integer.parseInt(discountRule.getRules().get(0).get("goods_money_limit").toString()) > 0)
                    proruleDetail += ",最多优惠" + AmountUtils.changeF2Y(discountRule.getRules().get(0).get("goods_money_limit").toString()) + "元";

                proCouponRuleView.setMaxDiscount(Collections.min(discountRule.getRules().stream().map(map -> Double.parseDouble(AmountUtils.changeDiscount(map.get("direct_discount").toString()))).collect(Collectors.toList())));
            } else if (discountRule.getRuleType() == 2) {
                String temp = "商品总价";
                proruleDetail = discountRule.getRules().stream().map(map -> "满" + AmountUtils.changeF2Y(map.get("meet_money").toString()) + "元打" + AmountUtils.changeDiscount(map.get("discount").toString()) + "折").collect(Collectors.joining(";"));
                proruleDetail = temp + proruleDetail;
                proCouponRuleView.setMaxDiscount(Collections.min(discountRule.getRules().stream().map(map -> Double.parseDouble(AmountUtils.changeDiscount(map.get("discount").toString()))).collect(Collectors.toList())));
            } else if (discountRule.getRuleType() == 3) {
                proruleDetail = discountRule.getRules().stream().map(map -> "满" + map.get("meet_num") + "件，打" + AmountUtils.changeDiscount(map.get("discount").toString()) + "折").collect(Collectors.joining("；"));
                proCouponRuleView.setMaxDiscount(Collections.min(discountRule.getRules().stream().map(map -> Double.parseDouble(AmountUtils.changeDiscount(map.get("discount").toString()))).collect(Collectors.toList())));
            } else if (discountRule.getRuleType() == 4) {
                proruleDetail = discountRule.getRules().stream().map(map -> "第" + map.get("rate") + "件打" + AmountUtils.changeDiscount(map.get("discount").toString()) + "折").collect(Collectors.joining(","));
                if (null != discountRule.getRules().get(0).get("goods_amount_limit") && Integer.parseInt(discountRule.getRules().get(0).get("goods_amount_limit").toString()) > 0)
                    proruleDetail += "，最多优惠" + discountRule.getRules().get(0).get("goods_amount_limit") + "件";
            } else if (discountRule.getRuleType() == 5) {
                proruleDetail = "商品打折,请参考相应商品";
            }
            proruleDetail+=appendInfoForDiscountRule(discountRule.getIsMl(),discountRule.getIsRound());
            proCouponRuleView.setProruleDetail(proruleDetail);
            return proCouponRuleView;
        } catch (Exception e) {
            logger.info("打折优惠活动解析详情异常" + e);
            return null;
        }
    }

    public ProCouponRuleView FreePostageRuleForProCouponRuleView(String proMotionsRule) {
        try {
            String proruleDetail = "";
            FreePostageRule freePostageRule = JSON.parseObject(proMotionsRule, FreePostageRule.class);
            ProCouponRuleView proCouponRuleView = new ProCouponRuleView();
            proCouponRuleView.setObj(freePostageRule);
            String suffix = "(仅限指定地区)";
            String temp = "";
            if (freePostageRule.getGoodsIdsType() == 0) {
                proCouponRuleView.setIsAllType(0);
                temp = "全场";
            } else {
                proCouponRuleView.setIsAllType(1);
                temp = "指定商品";
            }
            proruleDetail += "满" + AmountUtils.changeF2Y(freePostageRule.getMeetMoney().toString()) + "元包邮";
            if (null != freePostageRule.getReducePostageLimit())
                proruleDetail += ",最多免" + AmountUtils.changeF2Y(freePostageRule.getReducePostageLimit().toString()) + "元运费";
            proruleDetail = temp + proruleDetail + suffix;
            proCouponRuleView.setProruleDetail(proruleDetail);
            return proCouponRuleView;
        } catch (Exception e) {
            logger.info("包邮优惠活动解析详情异常" + e);
            return null;
        }
    }

    public ProCouponRuleView ReDuceMoneyRuleForProCouponRuleView(String proMotionsRule) {
        try {
            String proruleDetail = "";
            ReduceMoneyRule reduceMoneyRuleParam = JSON.parseObject(proMotionsRule, ReduceMoneyRule.class);
            ProCouponRuleView proCouponRuleView = new ProCouponRuleView();
            proCouponRuleView.setObj(reduceMoneyRuleParam);
            if (reduceMoneyRuleParam.getGoodsIdsType() == 0)
                proCouponRuleView.setIsAllType(0);
            else
                proCouponRuleView.setIsAllType(1);

            if (reduceMoneyRuleParam.getRuleType() == 1) {
                proruleDetail = reduceMoneyRuleParam.getRules().stream().map(map -> "商品总价立减" + AmountUtils.changeF2Y(map.getReduceMoney().toString()) + "元").collect(Collectors.joining(","));
                proruleDetail += "。";
            } else if (reduceMoneyRuleParam.getRuleType() == 2) {
                proruleDetail = reduceMoneyRuleParam.getRules().stream().map(map -> "商品总价每满" + AmountUtils.changeF2Y(map.getMeetMoney().toString()) + "元,减" + AmountUtils.changeF2Y(map.getReduceMoney().toString()) + "元").collect(Collectors.joining(","));
                if (reduceMoneyRuleParam.getRules().get(0).getCap() > 0) {
                    proruleDetail += "，最多优惠" + AmountUtils.changeF2Y(reduceMoneyRuleParam.getRules().get(0).getCap() + "") + "元。";
                } else {
                    proruleDetail += "，上不封顶。";
                }
            } else if (reduceMoneyRuleParam.getRuleType() == 3) {
                if (reduceMoneyRuleParam.getRules() != null && reduceMoneyRuleParam.getRules().size() > 0) {
                    ReduceMoneyRule.InnerRule map = reduceMoneyRuleParam.getRules().get(0);
                    proruleDetail = "商品总价满" + AmountUtils.changeF2Y(map.getMeetMoney().toString()) + "元,减" + AmountUtils.changeF2Y(map.getReduceMoney().toString()) + "元";
                    if (reduceMoneyRuleParam.getRules().size() > 1) {
                        proruleDetail += "；";
                    }
                }
                proruleDetail += reduceMoneyRuleParam.getRules().stream().skip(1).map(map -> "满" + AmountUtils.changeF2Y(map.getMeetMoney().toString()) + "元,减" + AmountUtils.changeF2Y(map.getReduceMoney().toString()) + "元").collect(Collectors.joining("；"));
                proruleDetail += "。";

            }
            proCouponRuleView.setProruleDetail(proruleDetail);
            return proCouponRuleView;
        } catch (Exception e) {
            logger.info("满减优惠活动解析详情异常" + e);
            return null;
        }
    }

    public ProCouponRuleView ConTrolMoneyRuleForProCouponRuleView(String proMotionsRule) {
        try {
            String proruleDetail = "";
            FixedPriceRule fixedPriceRule = JSON.parseObject(proMotionsRule, FixedPriceRule.class);
            ProCouponRuleView proCouponRuleView = new ProCouponRuleView();
            proCouponRuleView.setObj(fixedPriceRule);
            if (fixedPriceRule.getGoodsIdsType() == 0)
                proCouponRuleView.setIsAllType(0);
            else
                proCouponRuleView.setIsAllType(1);

            if (fixedPriceRule.getRuleType() == 1 || null == fixedPriceRule.getRuleType())
                proruleDetail += "特价" + AmountUtils.changeF2Y(fixedPriceRule.getFixedPrice() + "") + "元，在有效期内每种商品每次最多可以买" + fixedPriceRule.getBuyNumEachOrder() + "件，累计可以买" + fixedPriceRule.getTotal() + "件";
            else if (fixedPriceRule.getRuleType() == 2)
                proruleDetail += "特价商品请参考相应商品信息,在有效期内每种商品每次最多可以买" + fixedPriceRule.getBuyNumEachOrder() + "件，累计可以买" + fixedPriceRule.getTotal() + "件";
            proCouponRuleView.setProruleDetail(proruleDetail);

            return proCouponRuleView;
        } catch (Exception e) {
            logger.info("包邮优惠活动解析详情异常" + e);
            return null;
        }
    }

    public Map<String, Object> prolongPromValidity(String promotionsId, Integer siteId, String ruleId, Integer days) {
        Map<String, Object> rs = new HashMap<String, Object>();
        try {
            Integer id = Integer.parseInt(ruleId.toString());
            Integer actId = Integer.parseInt(promotionsId.toString());
            ProActivityDto proActivityDto = new ProActivityDto();
            proActivityDto.setSiteId(siteId);
            proActivityDto.setId(actId);
            PromotionsActivity promotionsActivity = promotionsActivityMapper.getPromotionsActivity(proActivityDto);
            String endTime1 = String.valueOf(promotionsActivity.getEndTime());
            endTime1 = endTime1.replace("T", " ");
            PromotionsRule promotionsRule = mapper.getPromotionsRuleByIdAndSiteId(siteId, id);
            String json = promotionsRule.getTimeRule();
            Map<String, Object> map = JacksonUtils.json2map(json);
            String endTime = (String) map.get("endTime");
            Date date = DateUtils.parse(endTime, "yyyy-MM-dd HH:mm:ss");
            Calendar end = Calendar.getInstance();
            end.setTime(date);
            end.set(Calendar.DATE, end.get(Calendar.DATE) + days);
            String time = DateUtils.formatDate(end.getTime(), "yyyy-MM-dd HH:mm:ss");
            if (compare_date(endTime1, time) == -1 || compare_date(endTime1, time) == 0) {
                mapper.updateEndTimeBySiteIdAndActId(siteId, actId, time);
            }
            map.put("endTime", time);
            json = JacksonUtils.mapToJson(map);
            int i = mapper.updateEndTimeBySiteIdAndRuleId(siteId, id, json);
            if (i == 1) {
                rs.put("code", "000");
                rs.put("value", i);
            }
            return rs;
        } catch (Exception e) {
            logger.info("延长活动规则时间接口异常:{}" + e);
            rs.put("code", "-1");
            return rs;
        }
    }

    public PageInfo<?> findCouponActivity(ProCouponRuleDto proCouponRuleDto) {
        PageHelper.startPage(proCouponRuleDto.getPageNum(), proCouponRuleDto.getPageSize());
        List<Map<String, Object>> list = couponRuleMapper.findCouponActivity(proCouponRuleDto);
        list.stream().forEach(p -> {
            try {
                String order_rule = p.get("order_rule") == null ? null : p.get("order_rule").toString();
                String goods_rule = p.get("goods_rule") == null ? null : p.get("goods_rule").toString();
                p.put("couponView", parsingCouponRuleService.accountCoupon((int) p.get("aim_at"), (int) p.get("coupon_type")
                    , order_rule, goods_rule));
            } catch (Exception e) {
                logger.error("获取店员优惠券列表失败：", e);
            }
            String time_rule = couponDetailService.getEffectiveTimeForGoodsDetail(p.get("time_rule") + "",
                DateUtils.parseDate(p.get("create_time") + "", "yyyy-MM-dd hh:mm:dd"));
            if (time_rule == null || time_rule.startsWith("-")) {
                time_rule = "已过期";
            }
            p.put("time_rules", time_rule);

        });
        return new PageInfo<>(list);
    }

    public ReturnDto getReleasePromotionsNumAndGoodsNum(List<Map<String, Object>> params, Integer siteId) {
        // 查数据
        String[] fields = new String[]{"promotions_id", "promotions_type", "promotions_rule", "active_link"};
        List<Map<String, Object>> promotionsRules = promotionsActivityMapper.findAllReleasePromotions(siteId, fields);
        int goodsOnSaleNum = goodsMapper.countGoodsOnSale(siteId);

        try {
            // 数据同化，方便后续处理，把数据中的 promotions_rule 同化成map（key有：goodsIds，goodsIdsType，ruleType）
            List<Map<String, Object>> dataAfterAssimilate = assimilatePromotionsRules(promotionsRules);

            // 分组
            Map<String, Map<String, List<Map<String, Object>>>> groupedData = dataAfterAssimilate.stream()
                .collect(groupingBy((Map map) -> map.get("promotions_type").toString(),
                    groupingBy((Map map) -> ((Map) map.get("promotions_rule")).get("ruleType").toString())));

            // 分析数据，结果保存在参数params
            analyzeGroupedData(params, groupedData, goodsOnSaleNum);

            return ReturnDto.buildSuccessReturnDto(params);
        } catch (Exception e) {
            logger.error(e.getMessage() + ": {}", e);
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }

    @SuppressWarnings("Duplicates")
    private void analyzeGroupedData(List<Map<String, Object>> params, Map<String, Map<String, List<Map<String, Object>>>> groupedData, int goodsOnSaleNum) throws DBDataErrorException {
        for (int i = 0; i < params.size(); i++) {
            Map<String, Object> map = params.get(i);
            Integer promotionsType = Integer.parseInt(map.get("promotionsType").toString());
            Integer ruleType = Integer.parseInt(map.get("ruleType").toString());

            List<Map<String, Object>> list;
            Map<String, List<Map<String, Object>>> result_1 = groupedData.get(promotionsType.toString());
            if (result_1 != null)
                list = Optional.ofNullable(result_1.get(ruleType.toString())).orElseGet(ArrayList::new);
            else
                list = new ArrayList<>();

            Set<String> goodsInRule = new HashSet<>();
            Set<String> goodsNotInRule = new HashSet<>();
            boolean isAll = false;
            map.put("releaseRule", list.size());
            map.put("goodsInRule", 0);

            if (list.size() == 0) {
                continue;
            }

            for (int j = 0; j < list.size(); j++) {
                Map promotionsRuleMap = (Map) list.get(j).get("promotions_rule");

                if ("0".equals(promotionsRuleMap.get("goodsIdsType"))) {
                    isAll = true;
                    break;
                } else if ("1".equals(promotionsRuleMap.get("goodsIdsType"))) {
                    goodsInRule.addAll(Arrays.asList(promotionsRuleMap.get("goodsIds").toString().split(",")));
                } else if ("2".equals(promotionsRuleMap.get("goodsIdsType"))) {
                    goodsNotInRule.addAll(Arrays.asList(promotionsRuleMap.get("goodsIds").toString().split(",")));
                } else {
                    throw new DBDataErrorException();
                }
            }

            if (isAll) {
                map.put("goodsInRule", goodsOnSaleNum);
                continue;
            }

            if (goodsNotInRule.size() == 0) {
                goodsInRule = goodsInRule.stream()
                    .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
                    .collect(Collectors.toSet());

                map.put("goodsInRule", goodsInRule.size());
            } else {
                goodsNotInRule.removeAll(goodsInRule);
                int goodsInRuleNum = goodsInRule.size();
                int goodsInRuleNumByNot = goodsOnSaleNum - goodsNotInRule.size();
                map.put("goodsInRule", goodsInRuleNum >= goodsInRuleNumByNot ? goodsInRuleNum : goodsInRuleNumByNot);
            }
        }
    }

    /**
     * 把数据中的 promotions_rule 同化成map（key有：goodsIds， goodsIdsType，ruleType）
     *
     * @param promotionsRules
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> assimilatePromotionsRules(List<Map<String, Object>> promotionsRules) throws Exception {
        Map<String, Object> errorMap = new HashMap<String, Object>() {{
            put("isError", false);
            put("errorMsg", null);
        }};
        List<Map<String, Object>> result = promotionsRules.stream()
            .map(map -> {
                int promotionsType;
                if (map.get("promotions_type") == null) {
                    promotionsType = 99;
                    map.put("promotions_type", "99");
                } else {
                    promotionsType = Integer.parseInt(map.get("promotions_type").toString());
                }
                String promotionsRule = Optional.ofNullable(map.get("promotions_rule")).orElse("").toString();
                Map<String, Object> tempRuleMap = new HashMap<>();
                switch (promotionsType) {
                    case 10: // 10满赠活动
                        GiftRule giftRule = JSON.parseObject(promotionsRule, GiftRule.class);
                        tempRuleMap.put("goodsIdsType", "1");
                        tempRuleMap.put("goodsIds", giftRule.getGoodsIds());
                        tempRuleMap.put("ruleType", "1");
                        break;

                    case 20: // 20打折活动
                        DiscountRule discountRule = JSON.parseObject(promotionsRule, DiscountRule.class);
                        tempRuleMap.put("goodsIdsType", discountRule.getGoodsIdsType().toString());
                        tempRuleMap.put("goodsIds", discountRule.getGoodsIds());

                        String ruleType = discountRule.getRuleType().toString();
                        if ("1".equals(ruleType) || "5".equals(ruleType)) ruleType = "15";
                        else if ("3".equals(ruleType) || "4".equals(ruleType)) ruleType = "34";
                        else ruleType = "2";

                        tempRuleMap.put("ruleType", ruleType);
                        break;

                    case 30: // 30包邮活动
                        FreePostageRule freePostageRule = JSON.parseObject(promotionsRule, FreePostageRule.class);
                        tempRuleMap.put("goodsIdsType", freePostageRule.getGoodsIdsType().toString());
                        tempRuleMap.put("goodsIds", freePostageRule.getGoodsIds());
                        tempRuleMap.put("ruleType", "1");
                        break;

                    case 40: // 40满减活动
                        ReduceMoneyRule reduceMoneyRule = JSON.parseObject(promotionsRule, ReduceMoneyRule.class);
                        tempRuleMap.put("goodsIdsType", reduceMoneyRule.getGoodsIdsType().toString());
                        tempRuleMap.put("goodsIds", reduceMoneyRule.getGoodsIds());
                        tempRuleMap.put("ruleType", reduceMoneyRule.getRuleType().toString());
                        break;

                    case 50: // 50限价活动
                        FixedPriceRule fixedPriceRule = JSON.parseObject(promotionsRule, FixedPriceRule.class);
                        tempRuleMap.put("goodsIdsType", fixedPriceRule.getGoodsIdsType().toString());
                        tempRuleMap.put("goodsIds", fixedPriceRule.getGoodsIds());
                        tempRuleMap.put("ruleType", "1");
                        break;

                    case 60: // 60团购活动
                        GroupBookingRule groupBookingRule = JSON.parseObject(promotionsRule, GroupBookingRule.class);
                        tempRuleMap.put("goodsIdsType", groupBookingRule.getGoodsIdsType().toString());
                        tempRuleMap.put("goodsIds", groupBookingRule.getGoodsIds());
                        tempRuleMap.put("ruleType", "1");
                        break;

                    case 99: // 99 表示这个promotionsActivity是个外部链接的活动发放，没有链接任何的promotionsRule表数据
                        tempRuleMap.put("goodsIdsType", "1");
                        tempRuleMap.put("goodsIds", "");
                        tempRuleMap.put("ruleType", "99");
                        break;

                    default:
                        errorMap.put("isError", true);
                        errorMap.put("errorMsg", "数据库数据有误");
                }
                map.put("promotions_rule", tempRuleMap);

                return map;
            })
            .collect(Collectors.toList());

        if ((boolean) errorMap.get("isError")) {
            throw new Exception(errorMap.get("errorMsg").toString());
        }

        return result;
    }


    public PromotionsRule getPromotionsRuleById(Integer siteId, Integer promotionsId) {
        return mapper.getPromotionsRuleByIdAndSiteId(siteId, promotionsId);
    }

    public static int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2后");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取默认类型
     *
     * @return
     */
    public int getCalculateBase(Object promotionsRule, Integer promotionsRuleType) {
        switch (promotionsRuleType) {
            case PROMOTIONS_RULE_TYPE_GIFT:
            case PROMOTIONS_RULE_TYPE_FREE_POST:
                throw new UnsupportedOperationException();

            case PROMOTIONS_RULE_TYPE_DISCOUNT:
                DiscountRule discountRule = (DiscountRule) promotionsRule;
                return Optional.ofNullable(discountRule.getCalculateBase()).orElseGet(() -> DEFAULT_CALCULATE_BASE.get(PROMOTIONS_RULE_TYPE_DISCOUNT).get(discountRule.getRuleType()));

            case PROMOTIONS_RULE_TYPE_MONEY_OFF:
                ReduceMoneyRule reduceMoneyRule = (ReduceMoneyRule) promotionsRule;
                return Optional.ofNullable(reduceMoneyRule.getCalculateBase()).orElseGet(() -> DEFAULT_CALCULATE_BASE.get(PROMOTIONS_RULE_TYPE_MONEY_OFF).get(reduceMoneyRule.getRuleType()));

            case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                FixedPriceRule fixedPriceRule = (FixedPriceRule) promotionsRule;
                return Optional.ofNullable(fixedPriceRule.getCalculateBase()).orElseGet(() -> DEFAULT_CALCULATE_BASE.get(PROMOTIONS_RULE_TYPE_LIMIT_PRICE).get(fixedPriceRule.getRuleType()));

            case PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                GroupBookingRule groupBookingRule = (GroupBookingRule) promotionsRule;
                return Optional.ofNullable(groupBookingRule.getCalculateBase()).orElseGet(() -> DEFAULT_CALCULATE_BASE.get(PROMOTIONS_RULE_TYPE_GROUP_BOOKING).get(groupBookingRule.getRuleType()));

            default:
                throw new UnknownTypeException();
        }

    }


    /**
     * @param promotionsRule
     * @return
     *      goodsIdsType 0全部商品参加 1指定商品参加 2指定商品不参加
     *      goodsIds 商品id，用逗号分隔，all表示全部
     */
    public static Map<String, String> getGoodsIds(PromotionsRule promotionsRule) {
        Preconditions.checkNotNull(promotionsRule);
        Map<String, String> result = new HashMap<>();
        String rule = promotionsRule.getPromotionsRule();
        switch (promotionsRule.getPromotionsType()) {
            case PROMOTIONS_RULE_TYPE_GIFT:
                GiftRule giftRule = JSON.parseObject(rule, GiftRule.class);
                result.put("goodsIdsType", "1");
                result.put("goodsIds", giftRule.getGoodsIds());
                break;

            case PROMOTIONS_RULE_TYPE_DISCOUNT:
                DiscountRule discountRule = JSON.parseObject(rule, DiscountRule.class);
                result.put("goodsIdsType", discountRule.getGoodsIdsType().toString());
                result.put("goodsIds", discountRule.getGoodsIds());
                break;

            case PROMOTIONS_RULE_TYPE_FREE_POST:
                FreePostageRule freePostageRule = JSON.parseObject(rule, FreePostageRule.class);
                result.put("goodsIdsType", freePostageRule.getGoodsIdsType().toString());
                result.put("goodsIds", freePostageRule.getGoodsIds());
                break;

            case PROMOTIONS_RULE_TYPE_MONEY_OFF:
                ReduceMoneyRule reduceMoneyRule = JSON.parseObject(rule, ReduceMoneyRule.class);
                result.put("goodsIdsType", reduceMoneyRule.getGoodsIdsType().toString());
                result.put("goodsIds", reduceMoneyRule.getGoodsIds());
                break;

            case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                FixedPriceRule fixedPriceRule = JSON.parseObject(rule, FixedPriceRule.class);
                result.put("goodsIdsType", fixedPriceRule.getGoodsIdsType().toString());
                result.put("goodsIds", fixedPriceRule.getGoodsIds());
                break;

            case PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                GroupBookingRule groupBookingRule = JSON.parseObject(rule, GroupBookingRule.class);
                result.put("goodsIdsType", groupBookingRule.getGoodsIdsType().toString());
                result.put("goodsIds", groupBookingRule.getGoodsIds());
                break;

            default:
                throw new RuntimeException("unknown promotionsRule type");
        }

        return result;
    }


    public String appendInfoForDiscountRule(Integer isMl,Integer isRound){
        StringBuilder builder = new StringBuilder();
        builder.append(";商品总价");

        if (isMl.equals(0)) { // 不抹零
            builder.append("不抹零");
        } else if (isMl.equals(1)) { // 按角抹零
            if (isRound.equals(0)) {
                builder.append("四舍五入到角");
            } else if (isRound.equals(1)) {
                builder.append("角以后抹去");
            }
        } else if (isMl.equals(2)) { // 按分抹零
            if (isRound.equals(0)) {
                builder.append("四舍五入到分");
            } else if (isRound.equals(1)) {
                builder.append("分以后抹去");
            }
        } else {
            throw new RuntimeException("goodsRule.is_ml 不可能为 " + isMl);
        }

        builder.append("。");
        return builder.toString();
    }
}
