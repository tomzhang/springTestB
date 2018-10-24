package com.jk51.modules.promotions.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.BStores;
import com.jk51.model.Stores;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.CouponRuleActivity;
import com.jk51.model.coupon.requestParams.ActiveStatus;
import com.jk51.model.coupon.requestParams.SignMembers;
import com.jk51.model.exception.ParameterErrorException;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.order.Member;
import com.jk51.model.promotions.*;
import com.jk51.model.promotions.activity.ProActivityBomb;
import com.jk51.model.promotions.activity.VisitParams;
import com.jk51.model.promotions.activity.VisitTask;
import com.jk51.model.promotions.rule.ProCouponRuleView;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.coupon.controller.CouponActivityController;
import com.jk51.modules.coupon.mapper.*;
import com.jk51.modules.coupon.request.OwnCouponParam;
import com.jk51.modules.coupon.service.CouponDetailService;
import com.jk51.modules.coupon.service.CouponFilterService;
import com.jk51.modules.coupon.service.ParsingCouponRuleService;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.persistence.mapper.MemberLabelMapper;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.mapper.PromotionsDetailMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.request.ProActivityDto;
import com.jk51.modules.promotions.request.ProActivityDtoForPage;
import com.jk51.modules.promotions.request.PromotionsAndCouponCountParam;
import com.jk51.modules.promotions.service.PromotionsActivityService;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 活动发放相关的接口
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
@RestController
@RequestMapping("/promotions/activity")
public class PromotionsActivityController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PromotionsActivityService service;
    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;
    @Autowired
    private CouponActivityMapper couponActivityMapper;
    @Autowired
    private CouponActivityReissureMapper couponActivityReissureMapper;

    @Autowired
    private CouponRuleActivityMapper couponRuleActivityMapper;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;
    @Autowired
    private YbMerchantMapper ybMerchantMapper;
    @Autowired
    private MemberLabelMapper memberLabelMapper;
    @Autowired
    private PromotionsRuleMapper promotionsRuleMapper;
    @Autowired
    private PromotionsActivityService promotionsActivityService;
    @Autowired
    private PromotionsRuleService promotionsRuleService;
    @Autowired
    private PromotionsDetailMapper promotionsDetailMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private BStoresMapper bStoresMapper;

    @Autowired
    private PromotionsFilterService promotionsFilterService;
    @Autowired
    private CouponFilterService couponFilterService;

    /* -- 创建活动发放 开始 -- */
    @PostMapping("create")
    public ReturnDto createPromotionsActivity(@RequestBody PromotionsActivity promotionsActivity) {
        logger.info("创建活动开始");
        return service.create(promotionsActivity);
    }

    /**
     * 创建可发布的活动和草稿状态的发放活动
     *
     * @param ruleAndActivity 活动和发放活动
     * @return
     */
    @PostMapping("createReleaseRuleAndDraftActivity")
    public ReturnDto createReleaseRuleAndDraftActivity(@RequestBody RuleAndActivityParams ruleAndActivity) {
        try {
            service.createReleaseRuleAndDraftActivity(ruleAndActivity);
            return ReturnDto.buildSuccessReturnDto();
        } catch (ParameterErrorException e) {
            logger.error("参数异常，{}", e);
            return ReturnDto.buildFailedReturnDto("参数异常");
        } catch (Exception e) {
            logger.error("异常，{}", e);
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }
    /* -- 创建活动发放 结束 -- */

    /* -- 更新活动发放 开始 -- */
    @PostMapping("update")
    public ReturnDto updatePromotionsActivity(@RequestBody PromotionsActivity promotionsActivity) {
        logger.info("更新活动开始");
        return service.update(promotionsActivity);
    }

    @PostMapping("editReleaseRuleAndDraftActivity")
    public ReturnDto editReleaseRuleAndDraftActivity(@RequestBody RuleAndActivityParams ruleAndActivity) {
        try {
            ruleAndActivity.getPromotionsRule().setSiteId(ruleAndActivity.getSiteId());
            ruleAndActivity.getPromotionsActivity().setSiteId(ruleAndActivity.getSiteId());
            service.editReleaseRuleAndDraftActivity(ruleAndActivity);
            return ReturnDto.buildSuccessReturnDto();
        } catch (Exception e) {
            logger.error("异常，{}", e);
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }

    /**
     * 修改状态，多用于手动操作修改状态
     *
     * @return
     */
    @PostMapping("changeStatus/{siteId}/{activityId}/{status}")
    public ReturnDto changeStatus(@PathVariable("siteId") Integer siteId,
                                  @PathVariable("activityId") Integer activityId,
                                  @PathVariable("status") Integer status) {

        return service.changeStatus(siteId, activityId, status);
    }
    /* -- 更新活动发放 结束 -- */


    /* -- 查询活动发放 开始 -- */

    @GetMapping("getPromotionsRuleAndActivityByActivityId")
    public ReturnDto getPromotionsRuleAndActivityByActivityId(Integer siteId, Integer activityId) {

        try {
            PromotionsActivity activity = promotionsActivityService.getPromotionsActivityById(siteId, activityId);
            PromotionsRule rule = promotionsRuleService.getPromotionsRuleById(siteId, activity.getPromotionsId());
            ProCouponRuleView promotionsRuleView = promotionsRuleService.promotionsRuleForType(rule.getPromotionsType(), rule.getPromotionsRule());
            activity.setPromotionsRule(rule);

            boolean isRelease = promotionsActivityService.checkRuleIsReleaseAtLeastOnce(siteId, activity.getPromotionsId());
            Map<String, Object> map = new HashMap<String, Object>() {{
                put("promotionsActivity", activity);
                put("isRelease", isRelease);
                put("promotionsRuleView", promotionsRuleView);
            }};
            if (rule.getUseStore().equals("2")) {
                List<String> cityIds = Arrays.asList(rule.getUseArea().split(","));
                List<Stores> list = bStoresMapper.getStoreByCityAndSiteId(cityIds, rule.getSiteId());
                String stores = JacksonUtils.obj2json(list);
                map.put("storeList", stores);
            } else if (rule.getUseStore().equals("1")) {
                List<String> storeIds = Arrays.asList(rule.getUseArea().split(","));
                List<Stores> list = bStoresMapper.getStoreByStoreIds(storeIds, rule.getSiteId());
                String stores = JacksonUtils.obj2json(list);
                map.put("storeList", stores);
            }
            return ReturnDto.buildSuccessReturnDto(map);
        } catch (Exception e) {
            logger.error("异常抛出, {}", e);
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }

    @RequestMapping(name = "pc活动列表页", value = "proActivityList")
    @ResponseBody
    public ReturnDto proActivityList(@RequestBody ProActivityDtoForPage proActivityDtoForPage) {
        PageInfo<?> pageInfo = null;
        if (null == proActivityDtoForPage.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        pageInfo = service.proActivityList(proActivityDtoForPage);
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    @RequestMapping(name = "pc活动列表页", value = "proActivityListNew")
    @ResponseBody
    public ReturnDto proActivityListNew(@RequestBody ProActivityDtoForPage proActivityDtoForPage) {
        PageInfo<?> pageInfo = null;
        if (null == proActivityDtoForPage.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        pageInfo = service.proActivityListNew(proActivityDtoForPage);
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    @RequestMapping(name = "查询会员手机号", value = "getMemberInfo")
    @ResponseBody
    public ReturnDto getMemberMoblie(Integer siteId, String memberIds) {

        String[] memberIdArr = memberIds.split(",");
        List<String> list = new ArrayList<String>(Arrays.asList(memberIdArr));
        String mobilesByMemberIds = memberMapper.findMobilesByMemberIds(siteId, list);
        return ReturnDto.buildSuccessReturnDto(mobilesByMemberIds);
    }

    @RequestMapping(name = "pc发放优惠券列表页", value = "couponActivityList")
    @ResponseBody
    public ReturnDto couponActivityList(@RequestBody ProActivityDtoForPage proActivityDtoForPage) {
        PageInfo<?> pageInfo = null;
        if (null == proActivityDtoForPage.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        pageInfo = service.couponActivityList(proActivityDtoForPage);
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    @RequestMapping(name = "pc发放优惠活动列表页", value = "promotionsActivityList")
    @ResponseBody
    public ReturnDto promotionsActivityList(@RequestBody ProActivityDtoForPage proActivityDtoForPage) {
        PageInfo<?> pageInfo = null;
        if (null == proActivityDtoForPage.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        pageInfo = service.promotionsActivityList(proActivityDtoForPage);
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    @RequestMapping(name = "pc活动详情", value = "proActivityDetail")
    @ResponseBody
    public ReturnDto proActivityDetail(ProActivityDto proActivityDto) {
        CouponActivity couponActivity;
        List<CouponRule> list = new ArrayList<>();
        List<CouponRuleActivity> cra;

        PromotionsActivity promotionsActivity = null;

        if (null == proActivityDto.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        if (null == proActivityDto.getId())
            return ReturnDto.buildFailedReturnDto("id不能为空");
        if (null == proActivityDto.getProActivityType())
            return ReturnDto.buildFailedReturnDto("proActivityType不能为空");

        try {

            if (proActivityDto.getProActivityType() == 1) {
                couponActivity = couponActivityMapper.getCouponActivity(proActivityDto.getSiteId(), proActivityDto.getId());
                if (couponActivity == null) {
                    return ReturnDto.buildFailedReturnDto("发放优惠券详情为空");
                }

                couponActivity.setMap(couponActivityReissureMapper.queryAllReissureActivityList(proActivityDto.getSiteId(), proActivityDto.getId()));

                cra = couponRuleActivityMapper.getRuleByActiveWithRule(proActivityDto.getSiteId(), proActivityDto.getId());
                List<Integer> ruleIds = cra.stream().map(CouponRuleActivity::getRuleId).collect(Collectors.toList());
                List<Map<String, Object>> combineList = couponDetailMapper.combineCouponActivityDetail(proActivityDto.getSiteId(), ruleIds, proActivityDto.getId().toString());
                int isReissUre = 0;
                for (CouponRuleActivity item : cra) {
                    CouponRule couponRule = item.getCouponRule();
//                    CouponRule couponRule = couponRuleMapper.findMemberNumById(item.getRuleId(), proActivityDto.getSiteId(), proActivityDto.getId().toString());
                    /*couponRule.setSendMemberNum(couponDetailMapper.findSendMemberAmount(proActivityDto.getSiteId(), proActivityDto.getId().toString(), item.getRuleId()));
                    couponRule.setMemberNum(couponDetailMapper.findUsedMemberAmount(proActivityDto.getSiteId(), proActivityDto.getId().toString(), item.getRuleId()));
                    couponRule.setSendNum(couponDetailMapper.findSendAmount(proActivityDto.getSiteId(), proActivityDto.getId(), item.getRuleId()));
                    couponRule.setUseAmount(couponDetailMapper.findUsedAmount(proActivityDto.getSiteId(), proActivityDto.getId(), item.getRuleId()));*/
//                    Map<String, Object> combineMap = couponDetailMapper.combineCouponActivityDetail(proActivityDto.getSiteId(), item.getRuleId(), proActivityDto.getId().toString());
                    CouponActivityController.eachSetCouponRule(combineList, couponRule);
                    couponRule.setCouponView(parsingCouponRuleService.accountCoupon(couponRule.getAimAt(),
                        couponRule.getCouponType(), couponRule.getOrderRule(), couponRule.getGoodsRule()));
                    couponRule.setRuleId(item.getRuleId());

                    if (couponRule.getStatus() == 0) {
                        isReissUre = 1;
                    }

                    list.add(couponRule);
                }

                couponActivity.setIsReissUre(isReissUre);

                YbMerchant ybMerchant = ybMerchantMapper.getMerchant(proActivityDto.getSiteId().toString());
                couponActivity.setYbMerchant(ybMerchant);
                couponActivity.setCouponRules(list);

                if (couponActivity.getSendObj() == 2) {
                    SignMembers members = JSON.parseObject(couponActivity.getSignMermbers(), SignMembers.class);
                    if (members.getType() == 1)
                        couponActivity.setMemberLabels(memberLabelMapper.getLabelAllForCouponActive(proActivityDto.getSiteId(), members.getPromotion_members().split(",")));
                }

                return ReturnDto.buildSuccessReturnDto(couponActivity);
            }

            if (proActivityDto.getProActivityType() == 2) {
                promotionsActivity = promotionsActivityMapper.getPromotionsActivityDetail(proActivityDto.getSiteId(), proActivityDto.getId());
                if (null != promotionsActivity.getUseObject() && !"".equals(promotionsActivity.getUseObject())) {
                    String members = JSONObject.parseObject(promotionsActivity.getUseObject()).get("promotion_members").toString();
                    String[] aaa = members.split(",");
                    List<String> bbb = Arrays.asList(aaa);
                    String mobiles = memberMapper.findMobilesByMemberIds(promotionsActivity.getSiteId(), bbb);
                    promotionsActivity.setMemberPhones(mobiles);
                }
                List<String> labelList = new ArrayList<String>();
                if (promotionsActivity.getUseObject() != "") {
                    SignMembers signMembers = JSON.parseObject(promotionsActivity.getUseObject(), SignMembers.class);
                    if (signMembers.getType() == 1) {
                        promotionsActivity.setMemberLabels(memberLabelMapper.getLabelAllForCouponActive(proActivityDto.getSiteId(),
                            JSON.parseObject(promotionsActivity.getUseObject(), SignMembers.class).getPromotion_members().split(",")));
                    }
                    if (signMembers.getType() == 3) {
                        String[] split = signMembers.getPromotion_members().split(",");
                        for (String str : split) {
                            labelList.add(str);
                        }
                        promotionsActivity.setLabelList(labelList);
                    }
                }
                PromotionsRule pr = promotionsRuleMapper.getPromotionsRuleByIdAndSiteId(promotionsActivity.getSiteId(), promotionsActivity.getPromotionsId());
                promotionsActivity.setPromotionsRule(pr);
                return ReturnDto.buildSuccessReturnDto(promotionsActivity);
            }

        } catch (Exception e) {
            logger.info("活动详情查询异常" + e);
            return ReturnDto.buildFailedReturnDto("活动详情查询异常");
        }

        return null;
    }


    @RequestMapping(name = "活动中心首页", value = "/centerOfProActivity")
    @ResponseBody
    public ReturnDto centerOfProActivity(@RequestBody ProActivityDto proActivityDto) {
        if (null == proActivityDto.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");

        List<Map<String, Object>> list = promotionsActivityService.centerOfProActivityList(proActivityDto);
        return ReturnDto.buildSuccessReturnDto(list);
    }
    //----------------------------------------------------------------------------------------------start
    @Autowired
    private CouponDetailService couponDetailService;
    @RequestMapping(name = "个人中心活动和优惠券统计", value = "/centerOfProActivityAndCouponCount")
    @ResponseBody
    public ReturnDto centerOfProActivityAndCouponCount(@RequestBody PromotionsAndCouponCountParam promotionsAndCouponCountParam) {

        if (null == promotionsAndCouponCountParam.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        List<Map<String, Object>> data = null;
        //log.info("个人中心查询有无可以领取的优惠券可是:ownCouponParam:" + ParameterUtil.ObjectConvertJson(ownCouponParam));
            if (null == promotionsAndCouponCountParam.getUserId())
                return ReturnDto.buildFailedReturnDto("userId不能为空");
        //优惠券统计
        OwnCouponParam ownCouponParam = new OwnCouponParam();
        ownCouponParam.setSiteId(promotionsAndCouponCountParam.getSiteId());
        ownCouponParam.setMemberId(promotionsAndCouponCountParam.getMemberId());
        ownCouponParam.setContentType(promotionsAndCouponCountParam.getContentType());
        data = couponDetailService.centerOfOwnCoupon(ownCouponParam);
        //活动统计
        ProActivityDto proActivityDto = new ProActivityDto();
        proActivityDto.setSiteId(promotionsAndCouponCountParam.getSiteId());
        List<Map<String, Object>> list = promotionsActivityService.centerOfProActivityList(proActivityDto);

        //log.info("领券中心领券列表查寻返回结果:data:" + ParameterUtil.ObjectConvertJson(data));
        Map<String, Object> result = new HashMap<>();
        result.put("promotionsCount", CollectionUtils.isEmpty(list) ? 0 : list.size());
        result.put("couponCount", CollectionUtils.isEmpty(data) ? 0 : data.size());

        return ReturnDto.buildSuccessReturnDto(result);
    }
    //----------------------------------------------------------------------------------------------end
    @RequestMapping(name = "个人中心活动中心", value = "/centerOfProActivityCount")
    @ResponseBody
    public ReturnDto centerOfProActivityCount(@RequestBody ProActivityDto proActivityDto) {
        if (null == proActivityDto.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");

        List<Map<String, Object>> list = promotionsActivityService.centerOfProActivityList(proActivityDto);
        return ReturnDto.buildSuccessReturnDto(CollectionUtils.isEmpty(list) ? 0 : list.size());
    }

    @GetMapping("queryUseNumAndSendNumForActivity")
    @ResponseBody
    public ReturnDto queryUseNumAndSendNumForActivity(String ids, Integer siteId) {
        if (ids == null || ids.length() == 0) {
            return ReturnDto.buildFailedReturnDto("id不能为空");
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
            List<Map<String, Integer>> result = promotionsDetailMapper.queryUseNumAndSendNumForActivity(siteId, idList);
            return ReturnDto.buildSuccessReturnDto(result);
        } catch (Exception e) {
            logger.error("数据错误导致的查询失败");
            return ReturnDto.buildFailedReturnDto("数据错误导致的查询失败");
        }
    }


    @RequestMapping(name = "活动详情", value = "detailOfProActivity")
    @ResponseBody
    public ReturnDto detailOfProActivity(@RequestBody ProActivityDto proActivityDto) {
        if (null == proActivityDto.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");

        if (null == proActivityDto.getId())
            return ReturnDto.buildFailedReturnDto("id不能为空");

        if (null == proActivityDto.getMemberId())
            return ReturnDto.buildFailedReturnDto("尚未登录");
        Map<String, Object> promotionsActivityDetail = promotionsActivityService.getPromotionsActivityMap(proActivityDto);

        if (promotionsActivityDetail != null && null != promotionsActivityDetail.get("id"))
            return ReturnDto.buildSuccessReturnDto(promotionsActivityDetail);
        else
            return ReturnDto.buildFailedReturnDto("数据异常");

    }
    /* -- 查询活动发放 结束 -- */

    /**
     * 活动待发布修改为发布中
     * 根据开始时间做判断
     * 开始时间如果为null 则状态改为发布中（已开始）
     * 开始时间不为null 根据开始时间判断 是否已开始或者未开始 已开始状态为0 未开始状态为11
     *
     * @param siteId
     * @param activityId
     * @return
     */
    @RequestMapping(value = "/sendActivity/{siteId}/{activityId}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto updateActivity(@PathVariable("siteId") Integer siteId, @PathVariable("activityId") Integer activityId) {
        try {
            //获取活动实体
            PromotionsActivity pa = promotionsActivityMapper.getPromotionsActivityDetail(siteId, activityId);

            //获取开始时间
            Timestamp startTime = Timestamp.valueOf(pa.getStartTime());

            if (startTime == null) {
                promotionsActivityMapper.updateStatusByIdAndSiteId(activityId, siteId, 0);

            } else {
                Timestamp now = DateUtils.getNowTimestamp();
                //startTime-now 正数说明startTime大 未开始 反之startTime小已开始

                long result = DateUtils.getTimeDifference(startTime, now);
                if (result > 0) {
                    //未开始
                    promotionsActivityMapper.updateStatusByIdAndSiteId(activityId, siteId, 11);

                } else {
                    //已开始
                    promotionsActivityMapper.updateStatusByIdAndSiteId(activityId, siteId, 0);
                }
            }
        } catch (RuntimeException e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }

        return ReturnDto.buildSuccessReturnDto("发布成功");
    }

    /**
     * 作废活动
     *
     * @param activeStatus
     * @return
     */
    @RequestMapping(value = "/updateActiveStatus", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ReturnDto updateActiveStatus(@RequestBody ActiveStatus activeStatus) {
        if (null == activeStatus.getSiteId()) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        if (null == activeStatus.getActiveId()) {
            return ReturnDto.buildFailedReturnDto("活动信息不能为空" +
                "");
        }
        return service.updateActiveStatus(activeStatus);
    }

    /**
     * 获取活动选取的自定义会员信息
     */
    @RequestMapping(value = "/getMembersForCouponActive/{activityId}/{siteId}")
    @ResponseBody
    public ReturnDto getMambersForCouponactive(@PathVariable("activityId") Integer activityId, @PathVariable("siteId") Integer siteId) {

        if (null == activityId)
            return ReturnDto.buildFailedReturnDto("activityId不能为空");

        if (null == siteId)
            return ReturnDto.buildFailedReturnDto("siteId不能为空");

        return service.getAllMembersForActivity(activityId, siteId);

    }

    @RequestMapping("getForcePopupCounts/{siteId}")
    public ReturnDto getForcePopupCounts(@PathVariable("siteId") Integer siteId) {
        return service.getForcePopupCounts(siteId);
    }

    /**
     * 微信活动弹框
     */
    @RequestMapping(value = "/proActivityBomb", name = "微信弹框数据获取")
    @ResponseBody
    public ReturnDto getproActivityBomb(@RequestBody ProActivityBomb proActivityBomb) {
        if (null == proActivityBomb.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        if (null == proActivityBomb.getType())
            return ReturnDto.buildFailedReturnDto("type不能为空");
        return service.getproActivityBomb(proActivityBomb);
    }

    @RequestMapping(value = "/getProCouponTagsByGoods", name = "通过商品获取商品的活动优惠标签")
    @ResponseBody
    public ReturnDto getProCouponTagsByGoods(EasyToSeeParam easyToSeeParam) {
        if (null == easyToSeeParam.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        if (null == easyToSeeParam.getGoodsIds())
            return ReturnDto.buildFailedReturnDto("goodsId不能为空");
        if (null == easyToSeeParam.getSearchType())
            return ReturnDto.buildFailedReturnDto("searchType不能为空");
        Integer siteId = easyToSeeParam.getSiteId();
        String goodsId = easyToSeeParam.getGoodsIds();
        Integer userId = easyToSeeParam.getUserId();
        Integer searchType = easyToSeeParam.getSearchType();
        String mobilePhone = easyToSeeParam.getMobile();
        Integer buyerId = null;
        Map<String, Object> result = new HashMap();
        try {
            if (userId == null && mobilePhone != null) {
                Member mobileById = memberMapper.findMobileById(siteId, mobilePhone.toString());
                if (mobileById != null) {
                    userId = mobileById.getMemberId();
                    buyerId = mobileById.getBuyerId();
                }
            }
            result.put("isNeedAuth", false);
            List<EasyToSee> easyToSees = promotionsFilterService.filterActivityGroupGoodsIds(easyToSeeParam);
            if (userId == null && mobilePhone == null) {
                couponFilterService.addCoupons(siteId, easyToSees, goodsId, result);
            } else {
                couponFilterService.addCoupons4Easy2See(siteId, goodsId, userId, easyToSees);
            }
            promotionsFilterService.GroupEasy2See(siteId, easyToSees, userId, result);
            if (searchType == 1) {
                int tagsNum = EasyToSeeConstants.GOODS_DETAIL;
                // 0商品搜索，1 商品详情
                promotionsFilterService.resolveEasy2SeeTransTags(easyToSees, buyerId, searchType, tagsNum);
            } else if (searchType == 0) {
                int tagsNum = EasyToSeeConstants.SEARCH_GOODS;
                // 0商品搜索，1 商品详情
                promotionsFilterService.resolveEasy2SeeTransTags(easyToSees, buyerId, searchType, tagsNum);
            }
            result.put("goods", easyToSees);
        } catch (Exception e) {
            logger.error("解析活动优惠券标签失败:{}", e);
            ReturnDto.buildFailedReturnDto("解析活动优惠券标签失败");
        }
        return ReturnDto.buildSuccessReturnDto(result);
    }


    /**
     * 回访--同时包含回访会员和回访商品，处于未开始或进行中状态的活动
     *
     * @return
     */
    @RequestMapping("infoForVisit")
    @ResponseBody
    public ReturnDto infoForVisit(@RequestBody VisitParams visitParams) {
        if (StringUtil.isEmpty(visitParams.getGoodIds())) return ReturnDto.buildFailedReturnDto("goodsId为空");
        if (StringUtil.isEmpty(visitParams.getUserIds())) return ReturnDto.buildFailedReturnDto("userId为空");
        try {
            logger.info("回访任务查询活动信息");
            Map<String, Object> stringObjectMap = promotionsActivityService.
                searchContainGoodsIdAndUserIdPromotions(visitParams.getGoodIds(), visitParams.getUserIds(), visitParams.getSiteId());
            if (stringObjectMap != null && !stringObjectMap.isEmpty())
                return ReturnDto.buildSuccessReturnDto(stringObjectMap);
        } catch (Exception e) {
            logger.error("查询活动信息异常:",e);
        }
        return null;
    }


}
