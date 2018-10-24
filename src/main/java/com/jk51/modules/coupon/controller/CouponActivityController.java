package com.jk51.modules.coupon.controller;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponActivityRulesForJson;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.CouponRuleActivity;
import com.jk51.model.coupon.requestParams.*;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.order.Trades;
import com.jk51.model.promotions.rule.SelectGiftByGoodsIdParms;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.coupon.mapper.*;
import com.jk51.modules.coupon.service.CouponActivityService;
import com.jk51.modules.coupon.service.CouponRuleService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.coupon.service.ParsingCouponRuleService;
import com.jk51.modules.goods.service.GoodsService;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.persistence.mapper.MemberLabelMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.service.TradesService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: think
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@RestController
@RequestMapping("coupon")
public class CouponActivityController {

    private static final Logger logger = LoggerFactory.getLogger(CouponActivityController.class);

    @Autowired
    private CouponActivityMapper couponActivityMapper;
    @Autowired
    private CouponRuleActivityMapper couponRuleActivityMapper;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;
    @Autowired
    private TradesService tradesService;
    @Autowired
    private CouponSendService couponSendService;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private CouponActivityService couponActivityService;
    @Autowired
    private YbMerchantMapper ybMerchantMapper;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BStoresMapper bStoresMapper;

    @Autowired
    MemberLabelMapper memberLabelMapper;
    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    MemberMapper memberMapper;

    @Autowired
    CouponActivityReissureMapper couponActivityReissureMapper;

    @Autowired
    private GoodsService goodsService;

    /**
     * 创建优惠券活动
     *
     * @param couponActiveParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/createCouponActivity")
    public ReturnDto createActive(@RequestBody CouponActiveParams couponActiveParams) {
        try {
            CouponActivity activity = couponActivityService.buildCouponActivity(couponActiveParams);
            activity.valid();

            CouponActivityRulesForJson rulesForJson = JacksonUtils.json2pojo(activity.getSendRules(), CouponActivityRulesForJson.class);
            List<CouponRuleActivity> ruleParams = new ArrayList<>();

            setRuleParams(activity, rulesForJson, ruleParams);

            couponActivityService.createAcitve(activity, ruleParams);

        } catch (Exception e) {
            logger.error("createActive error parameter:[{}] exception:[{}] ", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }

        return ReturnDto.buildSuccessReturnDto("创建成功");
    }

    /**
     * 创建优惠券活动
     *
     * @param couponActiveParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/createReleasedCouponActive")
    public ReturnDto createReleasedCouponActive(@RequestBody CouponActiveParams couponActiveParams) {
        try {
            CouponActivity activity = couponActivityService.buildCouponActivity(couponActiveParams);
            activity.valid();

            CouponActivityRulesForJson rulesForJson = JacksonUtils.json2pojo(activity.getSendRules(), CouponActivityRulesForJson.class);
            List<CouponRuleActivity> ruleParams = new ArrayList<>();

            setRuleParams(activity, rulesForJson, ruleParams);

            couponActivityService.createReleasedAcitve(activity, ruleParams);

            int releaseRuleNum = couponRuleMapper.countCouponRuleByActive(couponActiveParams.getSiteId(), activity.getId());
            if (releaseRuleNum == 0) {
                return ReturnDto.buildFailedReturnDto("该活动下没有可以发放的优惠券");
            }

            //获取开始时间
            Timestamp startTime = activity.getStartTime();

            if (startTime == null) {
                couponActivityMapper.updateStatusByTime(couponActiveParams.getSiteId(), activity.getId(), 0);
                couponActivityService.sendCoupon(activity);

            } else {
                Timestamp now = DateUtils.getNowTimestamp();
                //startTime-now 正数说明startTime大 未开始 反之startTime小已开始

                long result = DateUtils.getTimeDifference(startTime, now);
                if (result > 0) {
                    //未开始
                    couponActivityMapper.updateStatusByTime(couponActiveParams.getSiteId(), activity.getId(), 11);

                } else {
                    //已开始
                    couponActivityMapper.updateStatusByTime(couponActiveParams.getSiteId(), activity.getId(), 0);
                    couponActivityService.sendCoupon(activity);
                }
            }
        } catch (Exception e) {
            logger.error("createActive error parameter:[{}] exception:[{}] ", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }

        return ReturnDto.buildSuccessReturnDto("创建成功");
    }

    /**
     * 领券中心列表，
     */
    /*@ResponseBody
    @GetMapping(value = "/getCoupon/{activeid}/{userid}/{siteid}")
    public ReturnDto getCouponByActive(@PathVariable("activeid") String activeid, @PathVariable("userid") String userid,
                                       @PathVariable("siteid") Integer siteid) {

        if (!StringUtils.isNotBlank(userid))
            return ReturnDto.buildFailedReturnDto("userid 为空");
        if (activeid == null || "null".equals(activeid))
            return ReturnDto.buildFailedReturnDto("activeid 为空");
        if (siteid == null)
            return ReturnDto.buildFailedReturnDto("siteid 为空");

        String[] ids = activeid.split(",");
        boolean ifg = false;
        for (int i = 0; i < ids.length; i++) {
            Integer activiteId = Integer.parseInt(ids[i]);

            // 判断用户是否领取
            List<CouponDetail> couponDetails =
                    couponDetailMapper.findIsReceive(siteid, Integer.parseInt(userid), activiteId);

            if (couponDetails.isEmpty()) {
                try {

                    Integer counter = couponActivityService.sendCouponByActive(activiteId, siteid, 0, userid, 0, 0);
                    if (counter == 0) {
                        // return ReturnDto.buildFailedReturnDto("您已经领取过该优惠券");
                    } else {
                        ifg = true;
                    }
                    //   return ReturnDto.buildSuccessReturnDto("获取优惠券成功");
                } catch (RuntimeException e) {
                    return ReturnDto.buildFailedReturnDto(e.getMessage());
                } catch (Exception e) {
                    logger.error("getCouponByActive error exception:[{}] ", e);
                    return ReturnDto.buildSystemErrorReturnDto();
                }
            }
        }

        if (ifg) {
            return ReturnDto.buildSuccessReturnDto("获取优惠券成功");

        } else {
            return ReturnDto.buildFailedReturnDto("您已经领取过该优惠券");
        }

    }*/

    /**
     * 分享优惠券
     *
     * @param ruleid
     * @param userid
     * @param siteid
     * @param managerid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/shareCoupon/{ruleid}/{userid}/{siteid}/{managerid}")
    public ReturnDto shareCoupon(@PathVariable("ruleid") Integer ruleid, @PathVariable("userid") String userid
        , @PathVariable("siteid") Integer siteid, @PathVariable("managerid") String managerid) {

        if (!StringUtils.isNotBlank(userid))
            return ReturnDto.buildFailedReturnDto("userid 为空");
        if (!StringUtils.isNotBlank(managerid))
            return ReturnDto.buildFailedReturnDto("managerid 为空");
        if (ruleid == null)
            return ReturnDto.buildFailedReturnDto("ruleid 为空");
        if (siteid == null)
            return ReturnDto.buildFailedReturnDto("siteid 为空");

        try {
            couponActivityService.shareCoupon(ruleid, siteid, userid, managerid);
        } catch (RuntimeException e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        } catch (Exception e) {
            logger.error("shareCoupon error exception:[{}] ", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }

        return ReturnDto.buildSuccessReturnDto("分享成功");
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

        return couponActivityService.getAllMembersForActivity(activityId, siteId);

    }

    /**
     * 查找活动列表
     *
     * @param couponActivity
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/activityList", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto findActivityList(CouponActivity couponActivity,
                                      @RequestParam(value = "page", defaultValue = "1") Integer page,
                                      @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize) throws Exception {

        if (StringUtil.isEmpty(couponActivity.getSiteId()))
            return ReturnDto.buildFailedReturnDto("siteId为空");

        Map<String, Object> CouponActivityPageInfoMap = couponActivityService.findCouponActivityList(page, pageSize, couponActivity);

        return ReturnDto.buildSuccessReturnDto(CouponActivityPageInfoMap);
    }

    @GetMapping("queryUsedNumAndUnusedNumForActivityList")
    @ResponseBody
    public ReturnDto queryUsedNumAndUnusedNumForActivityList(String ids, Integer siteId) {
        if (ids == null || ids.length() == 0) {
            return ReturnDto.buildFailedReturnDto("id不能为空");
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
            List<Map<String, Integer>> result = couponDetailMapper.useAmountBySiteIdAndRuleIdForActivityList(siteId, idList);
            return ReturnDto.buildSuccessReturnDto(result);
        } catch (Exception e) {
            logger.error("数据错误导致的查询失败");
            return ReturnDto.buildFailedReturnDto("数据错误导致的查询失败");
        }
    }

    /**
     * 获取活动详情
     *
     * @param siteId
     * @param activityId
     * @return
     */
    @RequestMapping(value = "/activityDetail/{siteId}/{activityId}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto findActivityDetail(@PathVariable("siteId") Integer siteId, @PathVariable("activityId") Integer activityId) {
        CouponActivity couponActivity;
        List<CouponRule> list = new ArrayList<>();
        List<CouponRuleActivity> cra;

        if (siteId == null)
            return ReturnDto.buildFailedReturnDto("siteId 为空");
        if (activityId == null)
            return ReturnDto.buildFailedReturnDto("activityId 为空");

        try {
            couponActivity = couponActivityMapper.getCouponActivity(siteId, activityId);
            if (couponActivity == null) {
                return ReturnDto.buildFailedReturnDto("发放优惠券详情为空");
            }

            couponActivity.setMap(couponActivityReissureMapper.queryAllReissureActivityList(siteId, activityId));

            cra = couponRuleActivityMapper.getRuleByActiveWithRule(siteId, activityId);
            List<Integer> ruleIds = cra.stream().map(CouponRuleActivity::getRuleId).collect(Collectors.toList());
            List<Map<String, Object>> combineList = couponDetailMapper.combineCouponActivityDetail(siteId, ruleIds, activityId.toString());
            int isReissUre = 0;
            for (CouponRuleActivity item : cra) {
                CouponRule couponRule = item.getCouponRule();
                if(Objects.isNull(couponRule))
                    continue;
//                CouponRule couponRule = couponRuleMapper.findMemberNumById(item.getRuleId(), siteId, activityId.toString());
                /*couponRule.setSendMemberNum(couponDetailMapper.findSendMemberAmount(siteId, activityId.toString(), item.getRuleId()));
                couponRule.setMemberNum(couponDetailMapper.findUsedMemberAmount(siteId, activityId.toString(), item.getRuleId()));
                couponRule.setSendNum(couponDetailMapper.findSendAmount(siteId, activityId, item.getRuleId()));
                couponRule.setUseAmount(couponDetailMapper.findUsedAmount(siteId, activityId, item.getRuleId()));*/
                eachSetCouponRule(combineList, couponRule);
                couponRule.setCouponView(parsingCouponRuleService.accountCoupon(couponRule.getAimAt(),
                    couponRule.getCouponType(), couponRule.getOrderRule(), couponRule.getGoodsRule()));
                couponRule.setRuleId(item.getRuleId());

                LimitRule limitRule1 = com.alibaba.fastjson.JSON.parseObject(couponRule.getLimitRule(), LimitRule.class);
                if (null != limitRule1.getApply_store() && limitRule1.getApply_store() == -1)
                    couponRule.setUseScope(-1);
                else
                    couponRule.setUseScope(1);
                if (limitRule1.getApply_store() == 2) {
                    String citys = limitRule1.getUse_stores();
                    if(citys==null){
                        limitRule1.setUse_stores("");
                    }else if (!"".equals(citys)) {
                        List<String> cityIds = Arrays.asList(citys.split(","));
                        List<Integer> stores = bStoresMapper.findStoreIdByCityAndSiteId(siteId, cityIds);
                        String storesIds = stores.stream().map(i -> i.toString()).collect(Collectors.joining(","));
                        limitRule1.setUse_stores(storesIds);
                    }
                }
                if (StringUtil.equalsIgnoreCase(limitRule1.getApply_channel(), "101,103"))
                    couponRule.setIsLine(1);
                else if (StringUtil.equalsIgnoreCase(limitRule1.getApply_channel(), "105"))
                    couponRule.setIsLine(2);
                else if (StringUtil.equalsIgnoreCase(limitRule1.getApply_channel(), "101,103,105"))
                    couponRule.setIsLine(3);

                if (null != limitRule1.getIs_share() && limitRule1.getIs_share() == 0)
                    couponRule.setIsShare(0);
                else if (limitRule1.getIs_share() == 1)
                    couponRule.setIsShare(1);


                if (couponRule.getStatus() == 0) {
                    isReissUre = 1;
                }
                couponRule.setLimitRule(com.alibaba.fastjson.JSON.toJSONString(limitRule1));
                list.add(couponRule);
            }

            couponActivity.setIsReissUre(isReissUre);

            YbMerchant ybMerchant = ybMerchantMapper.getMerchant(siteId.toString());
            couponActivity.setYbMerchant(ybMerchant);
            couponActivity.setCouponRules(list);

            if (couponActivity.getSendObj() == 2) {
                SignMembers members = JSON.parseObject(couponActivity.getSignMermbers(), SignMembers.class);
                if (members.getType() == 1)
                    couponActivity.setMemberLabels(memberLabelMapper.getLabelAllForCouponActive(siteId, members.getPromotion_members().split(",")));
            } else if (couponActivity.getSendObj() == 3) {
                SignMembers members = JSON.parseObject(couponActivity.getSignMermbers(), SignMembers.class);
                if (members != null && members.getType() == 2) {
                    String[] arr = members.getPromotion_members().split(",");
                    List<String> arrlist = Arrays.asList(arr);
                    String mobiles = memberMapper.findMobilesByMemberIds(couponActivity.getSiteId(), arrlist);
                    members.setPromotion_members(mobiles);
                    couponActivity.setSignMermbers(JSON.toJSONString(members));
                }
            } else if (couponActivity.getSendObj() == 4) {
                SignMembers members = JSON.parseObject(couponActivity.getSignMermbers(), SignMembers.class);
                String[] labelName = members.getPromotion_members().split(",");
                List<String> labelsList = new ArrayList<String>();
                if (members.getType() == 3) {
                    labelsList.addAll(Arrays.asList(labelName));
                    couponActivity.setLabelList(labelsList);
                }
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
        return ReturnDto.buildSuccessReturnDto(couponActivity);
    }

    public static void eachSetCouponRule(List<Map<String, Object>> combineList, CouponRule couponRule) {
        for (Map<String, Object> combineMap : combineList) {
            int rule_id = Integer.parseInt(combineMap.get("rule_id").toString());
            if(couponRule.getRuleId().equals(rule_id)){
                couponRule.setSendMemberNum(Integer.parseInt(combineMap.get("findSendMemberAmount").toString()));
                couponRule.setMemberNum(Integer.parseInt(combineMap.get("findUsedMemberAmount").toString()));
                couponRule.setSendNum(Integer.parseInt(combineMap.get("findSendAmount").toString()));
                couponRule.setUseAmount(Integer.parseInt(combineMap.get("findUsedAmount").toString()));
                break;
            }
        }
    }

    /**
     * 更新活动的结束时间，即延长活动的结束时间
     *
     * @param siteId
     * @param activityId
     * @param date
     * @return
     */
    @RequestMapping(value = "/updateActivity/{siteId}/{activityId}/{date}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto updateActivity(@PathVariable("siteId") Integer siteId, @PathVariable("activityId") Integer activityId,
                                    @PathVariable("date") Date date) {

        try {
            couponActivityMapper.updateCouponActivity(date, siteId, activityId);

            long newActivityTime = date.getTime() / 1000 + 24 * 3600;
            if (newActivityTime > new Date().getTime() / 1000) {
                couponActivityMapper.updateCouponActivityStatus(siteId, activityId);
            }
        } catch (RuntimeException e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }

        return ReturnDto.buildSuccessReturnDto("编辑发放详情成功");
    }

    /**
     * 获取活动详情
     *
     * @param siteId
     * @param activityId
     * @return
     */
    @RequestMapping(value = "/couponCenter/{siteId}/{activityId}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto couponCenter(@PathVariable("siteId") Integer siteId, @PathVariable("activityId") Integer activityId) {

        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(siteId, activityId);

        if (null == couponActivity)
            return ReturnDto.buildFailedReturnDto("没有该条活动信息");

        return ReturnDto.buildSuccessReturnDto(couponActivity);
    }

    @RequestMapping("/getSendCouponDetailCount")
    @ResponseBody

    public ReturnDto getSendCouponDetailCount(Integer siteId, Integer ruleId) {

        try {

            return ReturnDto.buildSuccessReturnDto(couponRuleService.getSendCouponDetailCount(siteId, ruleId));
        } catch (Exception e) {

            logger.error("导出优惠券详情查询失败", e);
            return ReturnDto.buildFailedReturnDto("导出优惠券详情查询失败");
        }
    }

    /**
     * 获取用户领取的可用的优惠券
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/couponCenter", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto couponCenter(HttpServletRequest request) {
        return getUsableCoupon(request);
    }

    /**
     * 获取用户领取的可用的优惠券
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getActiveWithCoupon", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto getActiveWithCoupon(HttpServletRequest request) {
        return getUsableCoupon(request);
    }


    /**
     * 微信下完订单后发送红包
     *
     * @param request
     * @return
     * @throws BusinessLogicException
     */
    @RequestMapping(value = "/red", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto reveiveRed(HttpServletRequest request) throws BusinessLogicException {
        /* -- 基础判断 开始 by ztq -- */
        String siteId = request.getParameter("siteId");
        String tradesId = request.getParameter("tradesId");

        if (StringUtils.isBlank(siteId))
            return ReturnDto.buildFailedReturnDto("siteid 为空");
        if (StringUtils.isBlank(tradesId))
            return ReturnDto.buildFailedReturnDto("tradesId 为空");

        /* -- 查看是否存在发布中(已开始)的活动 by ztq -- */

        List<CouponActivity> couponActivityList = couponActivityMapper.getCouponActivityList(Integer.parseInt(siteId));
        if (couponActivityList.isEmpty())
            return ReturnDto.buildFailedReturnDto("该商家下没有红包优惠券");

        /* -- 查找可以参加的活动 开始 by ztq -- */
        List<CouponActivity> filterList = new ArrayList<>();
        Trades trades = tradesService.getTradesDetial(Long.parseLong(tradesId));
        if (trades != null) {
            List<Map<String, Integer>> goodsInfo = couponSendService.getGoodsInfo(tradesId);
            // 此处判断订单符不符合发送红包的条件

            filterList = couponActivityList.stream()
                .filter(item -> item.getSendWay() == 3)
                .filter(item -> couponSendService.checkSendCondition(item, goodsInfo, trades.getRealPay()))
                .collect(Collectors.toList());
        }

        if (filterList.isEmpty())
            return ReturnDto.buildFailedReturnDto("该订单没有符合发送红包条件");

        //查看这个购买者的成功购买的所有订单
        List<Trades> tradesList = tradesService.selectByBuyerId(Integer.parseInt(siteId), trades.getBuyerId());

        //判断是否为首单
        Boolean flag = tradesList.stream()
            .filter(trades1 -> !trades.getTradesId().equals(trades1.getTradesId()))
            .allMatch(trades1 -> trades1.getCreateTime().getTime() > trades.getCreateTime().getTime());

        if (flag) {
            for (CouponActivity activity : filterList) {
                if (activity.getSendType() == 5) {
                    filterList.set(0, activity);
                    break;
                }
            }
        } else {
            filterList = filterList.stream().filter(map -> map.getSendType() != 5).collect(Collectors.toList());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("siteId", filterList.get(0).getSiteId());
        map.put("id", filterList.get(0).getId());
        map.put("image", filterList.get(0).getImage());
        map.put("content", filterList.get(0).getContent());
        map.put("title", filterList.get(0).getTitle());

        return ReturnDto.buildSuccessReturnDto(map);
    }

    /**
     * 获取可以领取的活动优惠券<br/>
     * 范围(type)：1，门店后台用 2，微信用 3，门店助手用
     *
     * @param request
     * @return
     */
    private ReturnDto getUsableCoupon(HttpServletRequest request) {
        String type = request.getParameter("type");
        String siteId = request.getParameter("siteId");
        String managerId = request.getParameter("managerId");
        String storeId = request.getParameter("storeId");
        String page = request.getParameter("page");
        String pageSize = request.getParameter("pageSize");
        String userId = request.getParameter("userId");

        if (!StringUtils.isNotBlank(siteId))
            return ReturnDto.buildFailedReturnDto("siteId 为空");

        try {
            return couponActivityService.judgeType(type, siteId, managerId, storeId, page, pageSize, userId);
        } catch (Exception e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }

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
            CouponActivity ca = couponActivityMapper.getCouponActivity(siteId, activityId);

            int releaseRuleNum = couponRuleMapper.countCouponRuleByActive(siteId, activityId);
            if (releaseRuleNum == 0) {
                return ReturnDto.buildFailedReturnDto("该活动下没有可以发放的优惠券");
            }

            //获取开始时间
            Timestamp startTime = ca.getStartTime();

            if (startTime == null) {
                couponActivityMapper.updateStatusByTime(siteId, activityId, 0);
                couponActivityService.sendCoupon(ca);

            } else {
                Timestamp now = DateUtils.getNowTimestamp();
                //startTime-now 正数说明startTime大 未开始 反之startTime小已开始

                long result = DateUtils.getTimeDifference(startTime, now);
                if (result > 0) {
                    //未开始
                    couponActivityMapper.updateStatusByTime(siteId, activityId, 11);

                } else {
                    //已开始
                    couponActivityMapper.updateStatusByTime(siteId, activityId, 0);
                    couponActivityService.sendCoupon(ca);
                }
            }
        } catch (RuntimeException e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }

        return ReturnDto.buildSuccessReturnDto("发布成功");
    }

    /**
     * 编辑优惠券活动
     *
     * @param couponActiveParams
     * @return
     */
    @PostMapping("/updateActivity")
    @ResponseBody
    public ReturnDto updateActivity(@RequestBody CouponActiveParams couponActiveParams) {
        try {

            CouponActivity couponActivity = couponActivityService.buildCouponActivity(couponActiveParams);
            couponActivity.valid();

            CouponActivityRulesForJson rulesForJson = JacksonUtils.json2pojo(couponActivity.getSendRules(), CouponActivityRulesForJson.class);
            List<CouponRuleActivity> ruleParams = new ArrayList<>();

            setRuleParams(couponActivity, rulesForJson, ruleParams);

            couponActivityService.updateActive(couponActivity, ruleParams);
        } catch (Exception e) {
            logger.info("更新操作失败，原因是：{}", e);
            return ReturnDto.buildFailedReturnDto("编辑失败，原因是: " + e.getMessage());
        }

        return ReturnDto.buildSuccessReturnDto("编辑成功");
    }

    private void setRuleParams(@RequestBody CouponActivity couponActivity, CouponActivityRulesForJson rulesForJson, List<CouponRuleActivity> ruleParams) {
        rulesForJson.getRules().stream()
            .forEach(map -> {
                int ruleId = Integer.parseInt(map.get("ruleId").toString());
                int amount = Integer.parseInt(map.get("amount").toString());
                CouponRuleActivity cra = new CouponRuleActivity(couponActivity.getSiteId(), ruleId,
                    couponActivity.getId(), amount);
                ruleParams.add(cra);
            });
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
        return couponActivityService.updateActiveStatus(activeStatus);
    }

    /**
     * 补发优惠券
     *
     * @param reissureActivityParams
     * @return
     */
    @RequestMapping(value = "/toReissureActivity", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ReturnDto toReissureActivity(@RequestBody ReissureActivityParams reissureActivityParams) {
        if (null == reissureActivityParams.getSiteId()) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        if (null == reissureActivityParams.getActiveId()) {
            return ReturnDto.buildFailedReturnDto("活动信息不能为空" +
                "");
        }

        if (null == reissureActivityParams.getUserID()) {
            return ReturnDto.buildFailedReturnDto("操作人信息不能为空" +
                "");
        }
        couponActivityService.createReissureActivity(reissureActivityParams);

        return ReturnDto.buildSuccessReturnDto("补发成功，补发结果请查看补发记录");
    }


    /**
     * 根据满赠活动ID查询赠品
     *
     * @param selectGiftByGoodsIdParms
     * @return
     */
    @RequestMapping(value = "/selectGiftByGoodsIdParms")
    @ResponseBody
    public ReturnDto selectGiftByGoodsIdParms(@RequestBody SelectGiftByGoodsIdParms selectGiftByGoodsIdParms) {
        if (selectGiftByGoodsIdParms.getSiteId() == null) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }
        if (selectGiftByGoodsIdParms.getId() == null) {
            return ReturnDto.buildFailedReturnDto("活动Id不能为空");
        }
        if (StringUtils.isEmpty(selectGiftByGoodsIdParms.getGoodsInfo()))
            return ReturnDto.buildFailedReturnDto("商品信息goodsInfo不能为空");
        Map<String, Object> giftReturns = goodsService.selectGiftByGoodsIdParms(selectGiftByGoodsIdParms);
        return ReturnDto.buildSuccessReturnDto(giftReturns);
    }


}
