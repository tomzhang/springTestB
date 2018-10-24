package com.jk51.modules.coupon.service;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.coupon.*;
import com.jk51.model.coupon.mqParams.SendCouponMq;
import com.jk51.model.coupon.requestParams.*;
import com.jk51.model.order.Member;
import com.jk51.modules.coupon.event.CouponEvent;
import com.jk51.modules.coupon.mapper.*;
import com.jk51.modules.goods.library.SpringContextUtil;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.jk51.modules.coupon.constants.CouponConstant.*;
import static java.util.stream.Collectors.toList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: think
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@Service
public class CouponActivityService {

    @Autowired
    private CouponInformErpService couponInformErpService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private CouponDetailService detailService;
    @Autowired
    private CouponActivityMapper mapper;
    @Autowired
    private CouponRuleActivityMapper activityMapper;
    @Autowired
    private CouponRuleMapper ruleMapper;
    @Autowired
    private CouponDetailMapper detailMapper;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;
    @Autowired
    private CouponClerkMapper couponClerkMapper;
    @Autowired
    private CouponSendService couponSendService;
    @Autowired
    private CouponRuleActivityMapper couponRuleActivityMapper;
    @Autowired
    private CouponActivityMapper couponActivityMapper;
    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    private StringRedisTemplate srt;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private CouponActivityProcessService couponActivityProcessService;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private CouponActivityReissureMapper couponActivityReissureMapper;
    @Autowired
    private CouponNoEncodingService couponNoEncodingService;

    private static final Logger logger = LoggerFactory.getLogger(CouponActivityService.class);

    @Transactional
    public void updateAddActive(CouponActivity couponActivity, List<CouponRuleActivity> ruleList,Boolean isDel) {
        Integer delete = 0;
        if(isDel){
            //清理券和发放关系
            delete = activityMapper.deleteBySiteIdAndActiveId(couponActivity.getSiteId(), couponActivity.getId());
        }
        int update = couponActivityMapper.update(couponActivity);
        int insert = activityMapper.insertByBatch(ruleList);

        if (update != 1 || insert == 0 ) {
            throw new RuntimeException("活动更新失败");
        }
        if(isDel && delete ==0){
            throw new RuntimeException("活动更新失败");
        }
    }

    /**
     * 更新，从发放活动中添加一个券，首先判断活动是否是互动营销类sendNumtag == 4 的，其次在判断是不是手动结束掉的
     * 若是手动结束掉的 则代表活动中之前删除时仅剩一张券，但是没有删除，把活动置为了手动结束来代表活动没有券发放
     * 若再添加券，则把活动置为正常状态，并清除之前的所有关系，来添加新的状态
     * 若活动是正常仅添加券，则会将之前的券进行合并后，添加
     * @param siteId
     * @param activityId
     * @param ruleId
     * @return
     * @throws Exception
     */
    public ResultDto updateAddActivity(Integer siteId, Integer activityId, String ruleId) throws Exception {
        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(siteId, activityId);
        if (couponActivity == null) {
            return ResultDto.buildResultDto(false,"活动不存在");
        }
        CouponActivityRulesForJson couponActivityRulesForJson = JSON.parseObject(couponActivity.getSendRules(), CouponActivityRulesForJson.class);
        int sendNumTag = couponActivityRulesForJson.getSendNumTag();
        if (sendNumTag != 4 && Objects.isNull(couponActivityRulesForJson.getRules()))
            return ResultDto.buildResultDto(false,"发放规则不符合");
        //判断活动时间
        if(Objects.nonNull(couponActivity.getEndTime())){
            LocalDateTime end = couponActivity.getEndTime().toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            //超时关闭的活动
            if(end.isBefore(now)){
                return  ResultDto.buildResultDto(false,"该活动已过期");
            }
        }
        //把以前发放和券关系清除标记
        Boolean isDel = false;
        if(couponActivity.getStatus() == 4){
            //将活动状态更为正常
            couponActivity.setStatus(0);
            isDel = true;
        }
        List<Map<String, Object>> maps = buildActivitySendRule(ruleId, siteId);
        maps.forEach(map->map.put("activeId",couponActivity.getId()));
        //新要添加的发放和券的关系表
        List<CouponRuleActivity> couponRuleActivityList = getCouponRuleActivities(siteId, maps);
        couponRuleActivityList.forEach(cra -> {
            cra.setActiveId(activityId);
        });
        //如果仅仅是添加,不需要把以前的清除 那么需要把以前的和现在添加的信息合并
        if(!isDel)
            maps.addAll(couponActivityRulesForJson.getRules());
        couponActivityRulesForJson.setRules(maps);
        couponActivity.setSendRules(JSON.toJSONString(couponActivityRulesForJson));
        updateAddActive(couponActivity,couponRuleActivityList,isDel);
        return ResultDto.buildResultDto(true);
    }


    /**
     * 活动删除一张券
     */

    @Transactional
    public void updateRemoveActive(CouponActivity couponActivity, String ruleId) {
        int update = couponActivityMapper.update(couponActivity);
        int delete = activityMapper.deleteBySiteIdAndActiveIdAndRuleId(couponActivity.getSiteId(), couponActivity.getId(), ruleId);
        if (update != 1 || delete == 0) {
            throw new RuntimeException("活动更新失败");
        }
    }


    /**
     * 更新，从活动中删除一个优惠券,若是优惠券仅为最后一个券，则活动被置为手动结束
     * 若活动中存在2张及以上，则会对指定的一张进行移除
     * @param siteId
     * @param activityId
     * @param ruleId
     * @return
     * @throws Exception
     */
    public ResultDto updateRemoveActivity(Integer siteId, Integer activityId, String ruleId) throws Exception {
        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(siteId, activityId);
        if (couponActivity == null) {
            return ResultDto.buildResultDto(false,"活动不存在");
        }
        CouponActivityRulesForJson couponActivityRulesForJson = JSON.parseObject(couponActivity.getSendRules(), CouponActivityRulesForJson.class);
        int sendNumTag = couponActivityRulesForJson.getSendNumTag();
        if (sendNumTag != 4 && Objects.isNull(couponActivityRulesForJson.getRules()))
            return ResultDto.buildResultDto(false,"发放规则不符合");
        List<Map<String, Object>> sendRule = couponActivityRulesForJson.getRules()
            .stream()
            .filter(map -> !Objects.equals(map.get("ruleId"), ruleId))
            .collect(toList());
        couponActivityRulesForJson.setRules(sendRule);
        couponActivity.setSendRules(JSON.toJSONString(couponActivityRulesForJson));
        if(sendRule.size()==0){
            //最后一张券在发布中，但要被移除，则将活动暂时置为手动结束
            //待下次再添加券的时候，检测到活动是手动结束，则清空券和发放的关系，将新的关系建立
            couponActivity.setStatus(4);
            int update = couponActivityMapper.update(couponActivity);
            if(update==1)
                return ResultDto.buildResultDto(true);
            throw new Exception("更新活动失败:"+JSON.toJSONString(couponActivity));
        }
        updateRemoveActive(couponActivity,ruleId);
        return ResultDto.buildResultDto(true);
    }


    /**
     * 编辑大转盘
     */
    public Boolean updateActivityForTurnTable(Integer siteId, Integer activityId, String ruleIds) {
        try {
            CouponActivity couponActivity = couponActivityMapper.getCouponActivity(siteId, activityId);
            if (couponActivity == null) {
                return false;
            }
            List<Map<String, Object>> maps = buildActivitySendRule(ruleIds, siteId);
            List<CouponRuleActivity> couponRuleActivityList = getCouponRuleActivities(siteId, maps);
            couponRuleActivityList.forEach(cra -> {
                cra.setActiveId(activityId);
            });
            updateActive(couponActivity, couponRuleActivityList);
            return true;
        } catch (Exception e) {
            logger.error("修改大转盘活动失败：{}", e);
        }
        return false;
    }


    /**
     * 大转盘生产活动专用
     */
    /**
     * 创建大转盘活动后，此活动处于待发布状态，可以对大转盘进行优惠券编辑修改 {@link CouponActivityService#updateActivityForTurnTable}
     * 大转盘发放券 {@link CouponActivityService#sendCouponForTurnTable}
     *
     * @param siteId  站点
     * @param title   优惠活动名称
     * @param ruleIds 大转盘包含的券 ruleId 一个或多个id，多个以,分隔
     * @param start   大转盘开始时间，可以为null
     * @param end     大转盘结束时间，可以为null
     * @throws Exception
     */
    public CouponActivity createActivityForTurntable(Integer siteId, String title, String ruleIds, LocalDateTime start, LocalDateTime end) throws Exception {
        CouponActiveParams activeParams = new CouponActiveParams();
        activeParams.setSiteId(siteId);
        activeParams.setId(null);
        activeParams.setTitle(title);
        activeParams.setSendObj(1);
        activeParams.setSendType(2);
        activeParams.setSendWay(1);
        activeParams.setSendLimit(1);

        CouponActivityRulesForJson sendRule = new CouponActivityRulesForJson();
        List<Map<String, Object>> maps = buildActivitySendRule(ruleIds, siteId);
        sendRule.setRules(maps);
        sendRule.setSendNumTag(4);
        String sendRuleString = JSON.toJSONString(sendRule);
        activeParams.setSendRules(sendRuleString);
        CouponActivity activity = buildCouponActivity(activeParams);
        if (!Objects.isNull(start) && !Objects.isNull(end)) {
            activity.setStartTime(Timestamp.valueOf(start));
            activity.setEndTime(Timestamp.valueOf(end));
        }
        activity.valid();
        List<CouponRuleActivity> ruleActivities = getCouponRuleActivities(siteId, maps);
        createReleasedAcitve(activity, ruleActivities);
        return activity;
    }

    private List<CouponRuleActivity> getCouponRuleActivities(Integer siteId, List<Map<String, Object>> maps) {
        return maps.stream().map(map -> {
            int ruleId = Integer.parseInt(map.get("ruleId").toString());
            int amount = Integer.parseInt(map.get("amount").toString());
            return new CouponRuleActivity(siteId, ruleId,
                null, amount);
        }).collect(toList());
    }

    //构建发放优惠券的发放券
    //        {"rules":[{"ruleId":"2832","amount":-1,"name":"全场通用立减100","contentType":"100","contentMsg":"商品总价立减100.0元。"}],"sendNumTag":"1"}
    public List<Map<String, Object>> buildActivitySendRule(String ruleIds, Integer siteId) {
        List<String> ids = Arrays.asList(ruleIds.split(","));
        List<CouponRule> couponRuleIds = couponRuleMapper.findCouponRuleIds(ids, siteId);
        return couponRuleIds.stream().map(rule -> {
            Map<String, Object> result = new HashMap<String, Object>();
            CouponView couponView = parsingCouponRuleService.accountCoupon(rule.getRuleId(), siteId);
            result.put("ruleId", rule.getRuleId().toString());//放String类型
            result.put("amount", rule.getAmount());
            result.put("name", rule.getRuleName());
            result.put("contentType", String.valueOf(rule.getCouponType()));
            result.put("contentMsg", couponView.getRuleDetail());
            return result;
        }).collect(toList());
    }

    /**
     * 创建优惠券活动和活动与优惠券的中间表
     *
     * @param activity
     * @param ruleList
     */
    @Transactional
    public void createAcitve(CouponActivity activity, List<CouponRuleActivity> ruleList) {
        //新增默认状态为待发放
        activity.setStatus(10);

        mapper.insert(activity);

        try {
            Optional<Integer> activityId = Optional.of(activity.getId());
            ruleList.forEach(s -> s.setActiveId(activityId.get()));

            activityMapper.insertByBatch(ruleList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建优惠券活动和活动与优惠券的中间表
     *
     * @param activity
     * @param ruleList
     */
    @Transactional
    public void createReleasedAcitve(CouponActivity activity, List<CouponRuleActivity> ruleList) {
        //新增默认状态为待可发放
        activity.setStatus(0);

        mapper.insert(activity);

        try {
            Optional<Integer> activityId = Optional.of(activity.getId());
            ruleList.forEach(s -> s.setActiveId(activityId.get()));

            activityMapper.insertByBatch(ruleList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CouponActivity buildCouponActivity(CouponActiveParams couponActiveParams) throws Exception {
        CouponActivity couponActivity = new CouponActivity();
        couponActivity.setSiteId(couponActiveParams.getSiteId());
        couponActivity.setId(couponActiveParams.getId());
        couponActivity.setTitle(couponActiveParams.getTitle());
        couponActivity.setContent(couponActiveParams.getContent());
        couponActivity.setImage(couponActiveParams.getImage());
        couponActivity.setSendObj(couponActiveParams.getSendObj());
        couponActivity.setSendType(couponActiveParams.getSendType());
        couponActivity.setSendConditionType(couponActiveParams.getSendConditionType());
        couponActivity.setSendCondition(couponActiveParams.getSendCondition());
        couponActivity.setSendWay(couponActiveParams.getSendWay());
        couponActivity.setSendLimit(couponActiveParams.getSendLimit());
        couponActivity.setStartTime(couponActiveParams.getStartTime());
        couponActivity.setEndTime(couponActiveParams.getEndTime());
        couponActivity.setCreateTime(couponActiveParams.getCreateTime());
        couponActivity.setUpdateTime(couponActiveParams.getUpdateTime());
        couponActivity.setSendWayValue(couponActiveParams.getSendWayValue());
        couponActivity.setSendWayValue(couponActiveParams.getSendWayValue());
        couponActivity.setStatus(couponActiveParams.getStatus());
        couponActivity.setCreatedTotal(couponActiveParams.getCreatedTotal());
        couponActivity.setUsedTotal(couponActiveParams.getUsedTotal());
        couponActivity.setSendStatus(couponActiveParams.getSendStatus());
        couponActivity.setSignMermbers(JacksonUtils.obj2json(couponActiveParams.getSignMermbers()));
        couponActivity.setSendRules(couponActiveParams.getSendRules());
        couponActivity.setTimeRule(couponActiveParams.getTimeRule());

        return couponActivity;

    }


    /**
     * 编辑活动
     *
     * @param couponActivity
     * @param ruleList
     */
    @Transactional
    public void updateActive(CouponActivity couponActivity, List<CouponRuleActivity> ruleList) {
        int update = couponActivityMapper.update(couponActivity);
        int delete = activityMapper.deleteBySiteIdAndActiveId(couponActivity.getSiteId(), couponActivity.getId());
        int insert = activityMapper.insertByBatch(ruleList);

        if (update != 1 || delete == 0 || insert == 0) {
            throw new RuntimeException("活动更新失败");
        }
    }

    /**
     * 待定
     *
     * @param type      1:门店后台用 2:微信用 3:门店助手用
     * @param siteId    商家id
     * @param managerId 店员id
     * @param storeId   门店id
     * @param page      页码
     * @param pageSize  页面尺寸
     * @param userId    用户id
     * @return
     * @throws Exception
     */
    public ReturnDto judgeType(String type, String siteId, String managerId, String storeId, String page, String pageSize,
                               String userId) throws Exception {
        List<CouponActivity> cas_;
        Map<String, Object> result = new HashMap<>();

        if ("1".equals(type)) {
            if (!StringUtils.isNotBlank(storeId)) {
                return ReturnDto.buildFailedReturnDto("storeId 为空");
            }
            if (!StringUtils.isNotBlank(page)) {
                page = "1";
            }
            if (!StringUtils.isNotBlank(pageSize)) {
                pageSize = "10";
            }

            List<CouponActivity> cas = couponCenter(Integer.parseInt(siteId), null, Integer.parseInt(managerId), Integer.parseInt(storeId), type);
            cas_ = page(Integer.parseInt(page), Integer.parseInt(pageSize), cas);
            result.put("page", Integer.parseInt(page));
            result.put("pageSize", Integer.parseInt(pageSize));
            result.put("total", cas.size());
            result.put("list", cas_);
            return ReturnDto.buildSuccessReturnDto(result);
        } else if ("2".equals(type)) {
            if (StringUtils.isBlank(userId)) {
                return ReturnDto.buildFailedReturnDto("userId 为空");
            }
            cas_ = couponCenter(Integer.parseInt(siteId),

                Integer.parseInt(userId), Integer.parseInt(managerId), null, type);
            if (isGray(cas_)) {
                return ReturnDto.buildSuccessReturnDto(cas_);
            } else {
                return ReturnDto.buildSuccessReturnDto(new ArrayList<CouponActivity>());
            }

        } else if ("3".equals(type)) {
            if (!StringUtils.isNotBlank(managerId)) {
                return ReturnDto.buildFailedReturnDto("managerId 为空");
            }
            return ReturnDto.buildSuccessReturnDto(couponCenter(Integer.parseInt(siteId), managerId));
        } else {

        }
        return ReturnDto.buildFailedReturnDto("");
    }


    /**
     * 定时任务，查看是否有未正常发放完成的活动，如果有执行发送
     */
    @Transactional
    public void checkSendCoupon() {

        List<CouponActivity> allCouponActivity = couponActivityMapper.getAllCouponActivity();
        allCouponActivity.stream().forEach(a -> {
            sendCoupon(a);
        });


    }

    /**
     * 开启新线程发送优惠券
     *
     * @param activity
     */

    public void
    sendCoupon(CouponActivity activity) {


        Thread sendCouponDirect = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            couponSendService.sendCouponDirect(activity.getSiteId(), activity.getId());
        });



    /*    Thread sendClerk = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            couponSendService.sendClerk(activity.getSiteId(), activity.getId());
        });*/

        // sendClerk.start();
        sendCouponDirect.start();
        //sendClerk.destroy();
        //sendCouponDirect.destroy();
//
//        logger.debug("sendClerk.isAlive : [{}]",sendClerk.isAlive());
//        logger.debug("sendCouponDirect.isAlive : [{}]",sendCouponDirect.isAlive());
//        while(sendClerk.isAlive() == false && sendCouponDirect.isAlive() == false){
//
//            //更新activity状态
//            CouponActivity a = couponActivityMapper.getCouponActivity(activity.getSiteId(), activity.getId());
//            Integer sendStatus = 0;
//            if(null != a.getSendStatus()){
//                sendStatus += 1;
//            }
//            //利用session未关闭设置参数 -- 持久态 --不一定成功
//            a.setSendStatus(sendStatus);
//
//            //重新更新一遍
//            couponActivityMapper.updateSendStatus(a);
//            logger.debug("activity status is updated");
//        }

    }


    public void sendCouponByMQ(CouponActivity activity) {
        SendCouponMq sendCouponMq = new SendCouponMq();
        sendCouponMq.setSiteId(activity.getSiteId());
        sendCouponMq.setActivityId(activity.getId());
        sendCouponMq.setType(activity.getSendType());
        sendCouponMq.setSendWay(activity.getSendWay());
        CouponEvent couponEvent = new CouponEvent(activity, sendCouponMq);
        SpringContextUtil.getApplicationContext().publishEvent(couponEvent);

    }

    @Transactional
    public Integer sendCouponByActive(Integer activeId, Integer siteId, Integer ruleId, String userId, Integer storeId, Integer managerId,CouponActivity couponActivity) {
        String sendRules = couponActivity.getSendRules();
        CouponActivityRulesForJson couponActivityRulesForJson = JSON.parseObject(sendRules, CouponActivityRulesForJson.class);

        List<CouponRule> couponRuleList = ruleMapper.findCouponRuleByActive(siteId, activeId);
        Map<Integer, CouponRule> rules = couponRuleList.stream()
            .collect(Collectors.toMap(CouponRule::getRuleId, (r) -> r));
        List<CouponRuleActivity> activityList = null;

        if (couponActivity.getSendWay() == 2 && couponActivityRulesForJson.getSendNumTag() == 2) {
            Integer limit = couponActivity.getSendLimit() == null ? 1 : couponActivity.getSendLimit();
            Integer getTheNum = detailMapper.findIsReceiveCount(siteId, Integer.parseInt(userId), activeId);
            if (getTheNum >= limit)
                return 0;
            else {
                activityList = activityMapper.getRuleByActive(siteId, activeId);
                int random = new Random().nextInt(activityList.size());
                CouponRuleActivity couponRuleActivity = activityList.get(random);
                activityList = new ArrayList<>();
                activityList.add(couponRuleActivity);
            }

        } else {
            activityList = activityMapper.getRuleByActive(siteId, activeId).stream()
                .filter(couponRuleActivity -> {
                    Integer sendLimit = couponActivity.getSendLimit() == null ? 1 : couponActivity.getSendLimit();
                    //过滤掉数量不合适的
                    return checkIsGet(couponRuleActivity, userId, sendLimit);
                }).collect(toList());
        }
        List<CouponDetail> details = new ArrayList<>();

        Integer seed = Calendar.getInstance().get(Calendar.MILLISECOND);
        activityList.stream()
            //.filter(c -> checkCanRecieve(c, userId)) // 判断是否可分享领取 本期暂时不判断
            .filter(c -> {
                if (ruleId > 0) { //领单张券的时候不需要判断活动类型,
                    return true;
                }
                if (couponActivity.getSendWay() == 2 && couponActivityRulesForJson.getSendNumTag() == 2)
                    return true;
                else
                    return canSendBySendRules(c.getRuleId(), couponActivity, seed);
            })
            .forEach(cra -> {
                CouponRule rule = rules.get(cra.getRuleId());
                if (null != rule && (ruleId == 0 || rule.getRuleId().equals(ruleId))) { //领单张券入口
                    switch (rule.getAmount()) {
                        case -1:
                            details.add(
                                couponActivityProcessService.findDistanceResult(rule, Integer.valueOf(userId),
                                    CouponDetail.build(cra, Integer.valueOf(userId), activeId.toString(), managerId + "",
                                        couponRuleService.getCouponDownDetailNum(cra.getRuleId(), cra.getSiteId()), storeId)));

                            // 更新发放数量
                            couponRuleService.updateRuleStatus(cra.getSiteId(), cra.getRuleId(),
                                0, null, null, null, 0);

                            couponActivityProcessService.updateCouponCommon(cra.getSiteId(), cra.getRuleId(),
                                cra.getActiveId(), 0, null, 0);
                            break;
                        default:
                            if (rule.getAmount() > 0) {
                                rule.setAmount(rule.getAmount() - 1);

                                details.add(
                                    couponActivityProcessService.findDistanceResult(rule, Integer.valueOf(userId),
                                        CouponDetail.build(cra, Integer.valueOf(userId), activeId.toString(), managerId + "",
                                            couponRuleService.getCouponDownDetailNum(cra.getRuleId(), cra.getSiteId()), storeId)));

                                // 更新发放数量
                                couponRuleService.updateRuleStatus(cra.getSiteId(), cra.getRuleId(),
                                    0, null, null, null, 0);

                                couponActivityProcessService.updateCouponCommon(cra.getSiteId(),
                                    cra.getRuleId(), cra.getActiveId(), 0, null, 0);
                            }
                            break;
                    }
                }
            });
        details.removeAll(Collections.singleton(null));
        if (details.isEmpty()) {
            return 0;
        }

        detailMapper.insertCouponDetailBatch(details);
        couponInformErpService.ifContainCrashCouponThenSendQueueMessage(details);
        rules.values().stream().forEach(cr -> ruleMapper.updateAmountByRuleId(cr));


        // 更改活动和优惠券规则状态
        boolean success = checkStatus(activeId, siteId);
        if (success)
            return details.size();
        else
            throw new RuntimeException("改变状态失败"); // 为了回滚事物而抛出异常
    }

    /**
     * 检查用户是否已经领取
     *
     * @param cra
     * @param userId
     * @return
     */
    private boolean checkIsGet(CouponRuleActivity cra, String userId, Integer sendLimit) {


        Integer aLong = couponDetailMapper.countCouponByUserRuleAndActivity(userId, cra.getRuleId(), cra.getActiveId(),
            cra.getSiteId());
        logger.info("---------------------可领取数量：{}-----已领取数量:{}", sendLimit, aLong);
        if (sendLimit == -1 || aLong.intValue() < sendLimit.intValue()) //不限制或者领取数量小于可领取数量
            return true;
        else
            return false;
    }


    /**
     * 判断是否可领取
     *
     * @param c
     * @param userId
     * @return
     */
    private boolean checkCanRecieve(CouponRuleActivity c, String userId) {
        CouponRule couponRule = ruleMapper.findCouponRuleById(c.getRuleId(), c.getSiteId());
        Map<String, Object> map = null;
        try {
            map = JacksonUtils.json2map(couponRule.getLimitRule());
        } catch (Exception e) {
            e.printStackTrace();
        }
      /*  Integer is_share = (Integer) map.get("is_share");
        if (is_share.equals(0)) {
            return false;
        } else if (is_share.equals(1)) {*/
        Integer canGetNum = (Integer) map.get("can_get_num") == null || (Integer) map.get("can_get_num") == 0 ? 1 : (Integer) map.get("can_get_num");

        Integer num = couponDetailMapper.countCouponByUserRuleAndActivity(userId, c.getRuleId(),
            c.getActiveId(), c.getSiteId());
        return canGetNum.intValue() > num.intValue();
        // } else {
        //  return false;
        //   }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void shareCoupon(Integer ruleId, Integer siteId, String userId, String managerId) {
        CouponRule cr = ruleMapper.findCouponRuleById(ruleId, siteId);
        Optional.ofNullable(cr).orElseThrow(() -> new RuntimeException("没有该优惠券"));

        if (detailService.checkShareAmount(siteId, ruleId, Integer.valueOf(managerId))) {
            CouponDetail detail = couponActivityProcessService.findDistanceResult(cr, Integer.valueOf(userId),
                CouponDetail.build(cr, Integer.valueOf(userId), "share", managerId,
                    couponRuleService.getCouponDownDetailNum(ruleId, siteId)));
            if (detail != null)
                detailMapper.insertCouponDetail(detail);
        }

        couponRuleService.checkRuleAndActivityStatusByRuleId(siteId, ruleId);
    }


    /**
     * 优惠券列表，门店优惠券和微商城列表
     *
     * @param siteId
     * @param userId
     * @param storeId
     * @param type
     * @return
     */
    public List<CouponActivity> couponCenter(Integer siteId, Integer userId, Integer managerId, Integer storeId, String type) {

        List<CouponActivity> cas;
        List<Map<String, Object>> maps;
        int sendNum;

        Map<String, Object> urlMap = mapper.findWxUrlBySiteId(siteId);

        String url = urlMap.get("shopwx_url").toString() + "/new/sendCoupons?siteId=";

        if ("1".equals(type)) {//门店后台
            cas = mapper.getCouponActivityList(siteId);
            List<CouponActivity> cas3 = cas.stream().filter(couponActivity ->

                couponActivity.getSendWay() == 4).filter(couponActivity -> isExist(couponActivity.getSendWayValue()
                , storeId)).collect(toList());

            for (CouponActivity item : cas3) {

                item.setUrl(url + item.getSiteId() + "&activityId=" + item.getId() + "&managerId=" + managerId + "&storeId=" + storeId);
                maps = activityMapper.findCouponActivityAndRule(item.getSiteId(), item.getId());

                maps.stream().forEach(a -> a.put("couponView", parsingCouponRuleService.accountCoupon((int) a.get("aim_at"),
                    (int) a.get("coupon_type"), a.get("order_rule").toString(), a.get("goods_rule").toString())));
                sendNum = maps.stream().mapToInt(a -> (int) a.get("send_num")).sum();
                item.setMap(maps);
                item.setSendNum(sendNum);
            }

            return cas3;
        } else if ("2".equals(type)) {//微信用
            cas = mapper.getCouponActivityList(siteId);
            List<CouponActivity> cas3 = cas.stream().
                filter(couponActivity -> couponActivity.getSendWay() == 2).
                filter(couponActivity -> couponActivity.getSendType() != 1).
//                    filter(couponActivity -> userIdIsExist(couponActivity.getSiteId(), userId, couponActivity.getId())).
    filter(couponActivity -> filterOderFee(couponActivity.getSiteId(), userId, couponActivity)).
                    filter(couponActivity -> returnRuleNum(couponActivity.getSiteId(), couponActivity)).
//                    filter(couponActivity -> userIdIsPayment(couponActivity.getSiteId(), userId, couponActivity.getId())).
    collect(toList());

            for (CouponActivity item : cas3) {
                maps = activityMapper.findCouponActivityAndRule(item.getSiteId(), item.getId());
                maps.stream().forEach(a -> a.put("couponView", parsingCouponRuleService.accountCoupon((int) a.get("aim_at")
                    , (int) a.get("coupon_type"), a.get("order_rule").toString(), a.get("goods_rule").toString())));
                item.setMap(maps);
            }

            return cas3;
        } else {
            return null;
        }
    }

    /**
     * 判断活动下优惠券数量是否为空，
     *
     * @param siteid
     * @param couponActivity
     * @return
     */
    private boolean returnRuleNum(Integer siteid, CouponActivity couponActivity) {
        List<CouponRule> couponRuleList = couponRuleMapper.findCouponRuleByActive(siteid, couponActivity.getId());
        for (CouponRule couponRule : couponRuleList) {
            if (couponRule.getAmount() > 0 || couponRule.getAmount() == -1) {
                return true;
            }
        }
        return false;
    }

    public boolean isGray(List<CouponActivity> list) {

        List<Integer> amount = new ArrayList<>();
        Integer sum = 0;

        for (CouponActivity item : list) {
            item.getMap().stream().forEach(a -> amount.add(Integer.parseInt(a.get("amount").toString())));
        }

        for (int i = 0; i < amount.size(); i++) {
            sum += amount.get(i);
        }


        if (amount.contains(-1)) {
            return true;
        } else if (sum > 0) {
            return true;
        }

        return false;

    }

    public List<Map<String, Object>> couponCenter(Integer siteId, String managerId) {
        List<Map<String, Object>> map = couponClerkMapper.findManagerCoupon(siteId, managerId);
        if (map.isEmpty()) {
            return null;
        }
        map.stream().forEach(a -> {
            String order_rule = a.get("order_rule") == null ? "" : a.get("order_rule").toString();
            String goods_rule = a.get("goods_rule") == null ? "" : a.get("goods_rule").toString();
            a.put("couponView", parsingCouponRuleService.accountCoupon((int) a.get("aim_at"), (int) a.get("coupon_type"),
                order_rule, goods_rule));

        });
        return map;
    }

    public List<Map<String, Object>> getClerkUsableCoupons(Integer siteId, String managerId) {
        List<Map<String, Object>> map = couponClerkMapper.getClerkUsableCoupons(siteId, managerId);
        if (map.isEmpty()) {
            return null;
        }
        map.stream().forEach(a -> {
            String order_rule = a.get("order_rule") == null ? "" : a.get("order_rule").toString();
            String goods_rule = a.get("goods_rule") == null ? "" : a.get("goods_rule").toString();
            a.put("couponView", parsingCouponRuleService.accountCoupon((int) a.get("aim_at"), (int) a.get("coupon_type"),
                order_rule, goods_rule));

        });
        return map;
    }

    /**
     * 获取活动优惠券
     *
     * @param siteId
     * @param managerId
     * @return
     */
    public Map<String, Object> getActiveWithCoupon(Integer siteId, String managerId) {

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> active = null;

        List<Map<String, Object>> map = couponClerkMapper.findManagerCoupon(siteId, managerId);
        if (map.isEmpty()) {
            return null;
        }
        map.stream().forEach(a -> a.put("couponView", parsingCouponRuleService.accountCoupon((int) a.get("aim_at"),
            (int) a.get("coupon_type"), a.get("order_rule").toString(), a.get("goods_rule").toString())));

        if (map != null && map.size() > 0) {
            active = mapper.selectActiveById((Integer) map.get(0).get("active_id"));
        }

        result.put("active", active);
        result.put("coupons", map);
        return result;
    }

    //判断门店是否存在字符串里
    private boolean isExist(String sendWayValue, Integer id) {

        if ("all".equals(sendWayValue)) {
            return true;
        }

        String[] ids = sendWayValue.split(",");
        return Arrays.asList(ids).contains(String.valueOf(id));
    }

    //在此实现内存分页
    public List<CouponActivity> page(int pageNo, int pageSize, List<CouponActivity> list) throws Exception {
        List<CouponActivity> result = new ArrayList<>();
        if (list != null && list.size() > 0) {
            int allCount = list.size();
            int pageCount = (allCount + pageSize - 1) / pageSize;
            if (pageNo >= pageCount) {
                pageNo = pageCount;
            }
            int start = (pageNo - 1) * pageSize;
            int end = pageNo * pageSize;
            if (end >= allCount) {
                end = allCount;
            }
            for (int i = start; i < end; i++) {
                result.add(list.get(i));
            }
        }
        return (result.size() > 0) ? result : null;
    }


    /**
     * 过滤掉小于活动创建之前付款的订单
     *
     * @param siteid
     * @param userid
     * @param couponActivity
     * @return
     */
    public boolean filterOderFee(Integer siteid, Integer userid, CouponActivity couponActivity) {
        //如果优惠券领取是需要付款后才能的，查看是否有支付的订单
        if (couponActivity.getSendType() == 4) {
            int count = tradesMapper.selectTradesByActivityCreateTime(couponActivity.getCreateTime(), userid, siteid);
            if (count > 0) {
                return true;
            }
            return false;
        }
        return true;
    }

    //判断用户是否领取，返回true/false
    public boolean userIdIsExist(Integer siteid, Integer userid, Integer activiteId) {

        List<CouponDetail> couponDetails =
            couponDetailMapper.findIsReceive(siteid, userid, activiteId);
        List<CouponRuleActivity> activityShouldHave = couponRuleActivityMapper.getRuleByActive(siteid, activiteId);
        Map<String, Integer> count = new HashMap<>();
        for (CouponRuleActivity c : activityShouldHave) {
            count.put("i" + c.getRuleId(), 0);
        }

        for (CouponDetail c : couponDetails) {
            count.put("i" + c.getRuleId(), count.get("i" + c.getRuleId()) + 1);
        }

        Integer max = count.values().stream().mapToInt(x -> x).summaryStatistics().getMax();
        Integer min = count.values().stream().mapToInt(x -> x).summaryStatistics().getMin();

//        List<CouponDetail> couponDetails =
//                detailMapper.findIsReceive(siteid, userid, activiteId);
        if (max <= 1 && min == 0) {
            return true;
        }
        return false;
    }

    //判断用户是否付完款，返回true/false
    public boolean userIdIsPayment(Integer siteid, Integer userid, Integer activiteId) {
        String isNull = srt.opsForValue().get("payment" + siteid + userid + activiteId);
        if (StringUtils.isNotBlank(isNull)) {
            return true;
        }
        return false;
    }

    public void updateTimeByStatus(Integer siteId, Integer id) {
        CouponActivity couponActivity = mapper.getCouponActivity(siteId, id);
        if (couponActivity.getStartTime() != null && couponActivity.getEndTime() != null) {
            Long now = System.currentTimeMillis();
            Long startTime = couponActivity.getStartTime().getTime();
            Long endTime = couponActivity.getEndTime().getTime() + 24 * 60 * 60 * 1000;
            if (now < startTime) {
                couponActivityMapper.updateStatusByTime(couponActivity.getSiteId(), couponActivity.getId(), 1);
            } else if (now > startTime && now < endTime) {
                couponActivityMapper.updateStatusByTime(couponActivity.getSiteId(), couponActivity.getId(), 0);
            } else if (now > endTime) {
                couponActivityMapper.updateStatusByTime(couponActivity.getSiteId(), couponActivity.getId(), 2);
            }
        }
    }

    public void updateCouponActivityByUse(Integer siteId, Integer id,
                                          Integer createdTotal, Integer usedTotal) {
        CouponActivity couponActivity = new CouponActivity();
        couponActivity.setId(id);
        couponActivity.setSiteId(siteId);
        couponActivity.setCreatedTotal(createdTotal);
        couponActivity.setUsedTotal(usedTotal);
        couponActivityMapper.updateCouponActivityByUse(couponActivity);
    }


    /**
     * 分页查询优惠券活动
     *
     * @param page
     * @param pageSize
     * @param couponActivity
     * @return
     */
    public Map<String, Object> findCouponActivityList(Integer page, Integer pageSize, CouponActivity couponActivity) {
        PageHelper.startPage(page, pageSize);

        List<CouponActivity> list = couponActivityMapper.findCouponActivityList(couponActivity);
        if (list.size() != 0) {
            List<Integer> ids = list.stream().map(CouponActivity::getId).collect(toList());
            Map<String, Map<String, Object>> couponDetailStatus
                = couponDetailMapper.findCouponDetailStatusBySiteIdAndActivityIds(couponActivity.getSiteId(), ids);

            list.stream().forEach(activity -> {
                Map<String, Object> map = couponDetailStatus.get(activity.getId().toString());
                if (map != null) {
                    activity.setCreatedTotal(((Long) map.get("createNum")).intValue());
                    activity.setUsedTotal(((Long) map.get("createNum")).intValue() - ((BigDecimal) map.get("unUsedNum")).intValue());
                } else {
                    activity.setCreatedTotal(0);
                    activity.setUsedTotal(0);
                }
            });
        }

        PageInfo<?> pageInfo = new PageInfo<>(list);

        Map<String, Object> map = new HashMap<>();
        map.put("items", pageInfo.getList());
        map.put("page", pageInfo.getPageNum());
        map.put("pageSize", pageInfo.getPageSize());
        map.put("total", pageInfo.getTotal());
        return map;
    }
/*    public boolean updateStatus(Integer ruleId) {
        List<CouponRuleActivity> list1 = couponRuleActivityMapper.selectActiveIdByRuleId(ruleId);
        for (CouponRuleActivity couponRuleActivity:list1){
            if (couponRuleActivityMapper.selectActiveIdByRuleId(couponRuleActivity.getActiveId()).size() <= 1){
                if (couponRuleActivityMapper.getRuleByActive(couponRuleActivity.getSiteId(),couponRuleActivity.getRuleId()).size()<=1){
                    couponActivityMapper.updateStatus(couponRuleActivity.getSiteId(),couponRuleActivity.getActiveId());
                }
            }
        }
        return true;
    }*/

    // 判断活动下的优惠券规则 如果都过期或者撤销那么活动状态变更为终止
    private boolean judgeRuleStatus(List<CouponRule> couponRuleList) {
        if (!couponRuleList.isEmpty()) {
            for (CouponRule couponRule : couponRuleList) {
                if (couponRule.getStatus() == 0) {
                    return false;
                }
            }
            return true;
        }
        return false;

    }

    /**
     * 判断活动参加限制次数
     * @param couponActivity
     * @return
     */
    public Integer selectActivityLimitTimes(CouponActivity couponActivity) {
        if (couponActivity != null) {
            if (couponActivity.getSendLimit() == null)
                return 1;
            else
                return couponActivity.getSendLimit();
        }
        return null;
    }

    public Integer countActivityParticipateTimes(Integer memberId, Integer siteId, Integer activityId) {
        return couponDetailMapper.countActivityTimes(memberId, siteId, activityId).intValue();
    }

    public ReturnDto updateActiveStatus(ActiveStatus activeStatus) {


        CouponActivity activity = couponActivityMapper.getCouponActivity(activeStatus.getSiteId(), activeStatus.getActiveId());


        if (null == activity) {
            return ReturnDto.buildFailedReturnDto("没有查到相关活动信息");
        }

        if (activeStatus.getPreStatus() != activity.getStatus()) {
            return ReturnDto.buildFailedReturnDto("该活动状态已修改，请刷新后确认");
        }

        CouponActivity updateParam = new CouponActivity();
        updateParam.setSiteId(activeStatus.getSiteId());
        updateParam.setId(activeStatus.getActiveId());
        updateParam.setStatus(activeStatus.getToUpdateStatus());

        int result = couponActivityMapper.updateActiveForStopStatus(updateParam);


        if (result == 1) {
            return ReturnDto.buildSuccessReturnDto("修改活动成功");
        } else {
            return ReturnDto.buildFailedReturnDto("修改活动失败");
        }
    }

    /**
     * 检查活动的状态，如果不符合，则变更
     *
     * @param activityId
     * @param siteId
     * @return true 状态符合或者变更成功，false 状态不符合且无法变更
     */
    @Transactional
    public boolean checkStatus(Integer activityId, Integer siteId) {
        CouponActivity couponActivity = mapper.getCouponActivity(siteId, activityId);
        if (couponActivity == null) return true;

        List<CouponRuleActivity> ruleWithActive = activityMapper.getRuleByActive(siteId, activityId);

        Boolean reduce = ruleWithActive.stream().reduce(true,
            (aBoolean, couponRuleActivity) -> couponRuleService.checkStatus(couponRuleActivity.getSiteId(), couponRuleActivity.getRuleId()),
            (aBoolean, aBoolean2) -> aBoolean && aBoolean2);

        if (reduce == null)
            return false;

        // 可以发放的优惠券数量
        int releaseRuleNum = couponRuleMapper.countCouponRuleByActive(couponActivity.getSiteId(), couponActivity.getId());

        if (reduce) {
            return checkStatus(couponActivity, releaseRuleNum);
        } else {
            return false;
        }
    }

    private boolean checkStatus(CouponActivity couponActivity, int releaseRuleNum) {
        switch (couponActivity.getStatus()) {
            case ACTIVITY_STATUS_RELEASE_REGULAR:
                return checkStatusByTimeAndAmout(couponActivity, releaseRuleNum, false);
            case ACTIVITY_STATUS_RELEASE:
            case ACTIVITY_STATUS_OVERDUE:
            case ACTIVITY_STATUS_NO_INVENTORY:
                return checkStatusByTimeAndAmout(couponActivity, releaseRuleNum, true);
            case ACTIVITY_STATUS_STOP:
            case ACTIVITY_STATUS_DRAFT:
                return true;
            default:
                return false;
        }
    }

    /**
     * 根据数量和时间判断状态，如果不符合预期，则修改<br/>
     * ps:该方法只会在发布中，已发完结束，过期结束这3个状态之间扭转
     *
     * @param couponActivity
     * @param releaseRuleNum
     * @param isRelease      是发放还是定时发放
     * @return true:符合预期，或修改成功， false:不符合预期且修改失败
     */
    private boolean checkStatusByTimeAndAmout(CouponActivity couponActivity, int releaseRuleNum, boolean isRelease) {
        Timestamp end = couponActivity.getEndTime();
        Date now = new Date();

        if (end != null && now.after(end)) { // 活动已过期
            return ifNotThenChangeForStatus(couponActivity, ACTIVITY_STATUS_OVERDUE);
        } else if (releaseRuleNum == 0) { // 活动已发完结束
            return ifNotThenChangeForStatus(couponActivity, ACTIVITY_STATUS_NO_INVENTORY);
        } else { // 发布中
            if (isRelease)
                return ifNotThenChangeForStatus(couponActivity, ACTIVITY_STATUS_RELEASE);
            else
                return ifNotThenChangeForStatus(couponActivity, ACTIVITY_STATUS_RELEASE_REGULAR);
        }
    }

    /**
     * 如果活动状态和参数相同，则返回true，否则尝试修改，修改成功返回true，修改失败返回false
     *
     * @param couponActivity
     * @param status
     * @return
     */
    private boolean ifNotThenChangeForStatus(CouponActivity couponActivity, int status) {
        if (couponActivity.getStatus() == status) {
            return true;
        } else {
            int i = mapper.updateStatusByTime(couponActivity.getSiteId(), couponActivity.getId(), status);
            return i == 1;
        }
    }

    /**
     * 只根据优惠券活动的sendRules判断couponRule是否可以发布，注意:此时的 couponactivity 的 sendRules 里面的数据必须是可以发布的优惠券
     *
     * @param ruleId
     * @param couponActivity
     * @param seed           随机用的seed，保证一次循环中取的ruleId固定
     * @return
     */
    public boolean canSendBySendRules(Integer ruleId, CouponActivity couponActivity, Integer seed) {
        CouponActivityRulesForJson sendRules;
        try {
            String sendRules_ = couponActivity.getSendRules();
            if (sendRules_ != null) {
                sendRules = JSON.parseObject(sendRules_, CouponActivityRulesForJson.class);
                logger.info("canSendBySendRules:优惠券领取校验{}", sendRules);
            } else
                return true;
        } catch (Exception e) {
            logger.error("不可能出现的错误1参数, ruleId{}, couponActivity{}, seed{}", ruleId, couponActivity, seed);
            logger.error("不可能出现的错误1, 解析couponactivity的sendRules错误: ", e);
            return false;
        }

        List<Map<String, Object>> rules = sendRules.getRules();
        switch (sendRules.getSendNumTag()) {
            case 1: // 只发一张优惠券
                return (rules.size() == 1 && rules.get(0).get("ruleId").equals(ruleId.toString()));
            case 2: // 选择多张优惠券，随机发放其中一张
                List<Integer> ids = canBesendIdList(couponActivity);
                Integer ruleId_ = sendWhichOne(ids, seed);
                return ruleId_.equals(ruleId);
            case 0:
                logger.error("不可能出现的错误2参数, ruleId={}, couponActivity={}, seed={}", ruleId, couponActivity, seed);
                logger.error("不可能出现的错误2, sendNumTag==0, sendRules={}", sendRules);
            case 3: // 同时发放多张优惠券
                return rules.stream()
                    .map(map -> map.get("ruleId").equals(ruleId.toString()))
                    .reduce(false, ((aBoolean, aBoolean2) -> aBoolean || aBoolean2));

        }

        return true; // 返回true 是因为旧版活动没有sendRules字段
    }

    /**
     * 查找活动下可以发放的优惠券规则ids
     *
     * @param couponActivity
     * @return
     */
    private List<Integer> canBesendIdList(CouponActivity couponActivity) {
        List<Map<String, Object>> list = couponRuleActivityMapper.findCouponActivityAndRule(couponActivity.getSiteId(), couponActivity.getId());
        return list.stream()
            .filter(map -> "0".equals(map.get("status").toString()))
            .map(map -> Integer.parseInt(map.get("rule_id").toString()))
            .collect(toList());
    }

    private Integer sendWhichOne(List<Integer> ids, Integer seed) {
        Random random = new Random(seed);
        return ids.get(random.nextInt(ids.size()));
    }

    /**
     * 当发放优惠券时，更新相应的数量，
     * 不更新b_coupon_activity_coupon以及b_coupon_activity的相应字段，不更新b_coupon_rule的receive_num
     * 因为实际统计已经不用这些字段，而是统计b_coupon_detail的数量来代替
     *
     * @param siteId
     * @param activityId
     * @param ruleId
     * @param sendAmount
     * @return
     */
    @Transactional
    public boolean updateAfterSendRule(CouponRule couponRule,int siteId, int activityId, int ruleId, int sendAmount) {
        // 优惠券原始数据
//        CouponRule couponRule = couponRuleMapper.findCouponRuleById(ruleId, siteId);
        if (couponRule.getAmount() == -1) {
        } else {
            if (couponRule.getAmount() < sendAmount)
                return false;
            else {
                couponRule.setAmount(couponRule.getAmount() - sendAmount);
                couponRuleMapper.updateAmountByRuleId(couponRule);
            }
        }

        checkStatus(activityId, siteId);

        return true;
    }

    public ReturnDto getAllMembersForActivity(Integer activityId, Integer siteId) {
        CouponActivity couponActivity = mapper.getCouponActivity(siteId, activityId);
        if (couponActivity == null)
            return ReturnDto.buildFailedReturnDto("没查到对应的活动信息");
        if (couponActivity.getSendObj() != 3)
            return ReturnDto.buildFailedReturnDto("活动类型错误，请稍后再试");
        SignMembers members = JSON.parseObject(couponActivity.getSignMermbers(), SignMembers.class);
        List<Member> memberList = memberMapper.queryMemberListForCouponActive(siteId, new HashSet<String>(Arrays.asList(members.getPromotion_members().split(","))));

        return ReturnDto.buildSuccessReturnDto(memberList);

    }

    /**
     * @param reissureActivityParams
     */
    @Transactional
    public void createReissureActivity(ReissureActivityParams reissureActivityParams) {
        ReissureActivity reissureActivity = new ReissureActivity(reissureActivityParams);
        //补发券记录
        couponActivityReissureMapper.addCouponActivityReissure(reissureActivity);
        //执行补发券
        sendReissureActivity(reissureActivity, reissureActivityParams);
    }

    public void sendReissureActivity(ReissureActivity reissureActivity, ReissureActivityParams reissureActivityParams) {
        Thread sendCouponDirect = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tosendReissureActivity(reissureActivity, reissureActivityParams);
        });
        sendCouponDirect.start();
    }

    public void tosendReissureActivity(ReissureActivity reissureActivity, ReissureActivityParams reissureActivityParams) {
        //获取活动
        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(reissureActivity.getSiteId(),
            reissureActivity.getActivityId());
        if (couponActivity == null)
            return;
        //获得所有会员
        List<Member> memberList = memberMapper.queryMemberListForCouponActive(reissureActivity.getSiteId(),
            new HashSet<String>(Arrays.asList(reissureActivityParams.getVipMembers().split(","))));

        //对会员进行筛选  如果活动下的优惠券用户已经获得了，则把改用户筛选出去不参与补发
        memberList = memberList.stream().filter(member -> !checkCouponDetail(member.getMemberId(), couponActivity.getId(), couponActivity.getSiteId())).collect(toList());

        if (memberList.isEmpty())
            return;
        couponSendService.insertReissureActivity(memberList, couponActivity, reissureActivity);


    }

    public boolean checkCouponDetail(Integer memberId, Integer activiId, Integer siteId) {
        //获取coupondetail表中的优惠券，得到用户所在活动下所拥有的券的数量
        Long size = couponDetailMapper.findConponDetailListForMemberAndActiveNum(memberId, activiId, siteId);
        if (size > 0)
            return true;
        else
            return false;
    }

    /**
     * 大转盘发放优惠券
     *
     * @param siteId   站点
     * @param activity 活动
     * @param buyerId  会员表中的buyerId
     * @param sendRule 发放的优惠券id
     * @return
     */
    public ReturnDto sendCouponForTurnTable(Integer siteId, Integer activity, Integer buyerId, Integer sendRule) {
        CouponActivity couponActivity = couponActivityMapper.getCouponActivityInIssued(siteId, activity);
        if (Objects.isNull(couponActivity))
            return ReturnDto.buildFailedReturnDto("活动未查询到或活动状态无效");
        Member member = memberMapper.getMember(siteId, buyerId);
        if (Objects.isNull(member))
            return ReturnDto.buildFailedReturnDto("会员未查询到");
        //预发放，实际已减去券数量，但未生成券实例
        CouponRule couponRule = couponSendService.getCouponRulesForTurnTable(couponActivity, sendRule);
        //基本这里的amount 一定会是1 除了0 不可能是其他数
        if (couponRule.getAmount() != 0) {
            CouponDetail details = couponActivityProcessService.getCoupon(
                couponRule, member.getMemberId(), String.valueOf(couponActivity.getId()), null);
            //其实是补发动作，但转盘发券不视为补发
            details.setType(0);
            int detail = couponDetailMapper.insertCouponDetail(details);
            if (detail == 1) {
                logger.info("转盘优惠券成功发放一张优惠券");
                return ReturnDto.buildSuccessReturnDtoByMsg("优惠券已发放");
            }
            logger.info("发放失败,发放数量:{},发放优惠券:{},发放规则:{},发放活动:{}", detail, details, couponRule, couponActivity);
            return ReturnDto.buildFailedReturnDto("发放失败");
        }
        logger.info("发放失败,发放规则:{},发放活动:{}", couponRule, couponActivity);
        return ReturnDto.buildFailedReturnDto("发放失败");
    }
}
