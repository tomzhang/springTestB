package com.jk51.modules.marketing.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.jk51.model.*;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.modules.coupon.service.CouponActivityService;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.marketing.mapper.*;
import com.jk51.modules.marketing.request.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MarketingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketingService.class);

    @Autowired
    private BMarketingPlanMapper bMarketingPlanMapper;
    @Autowired
    private BMarketingPlanExtMapper bMarketingPlanExtMapper;
    @Autowired
    private BMarketingLogMapper bMarketingLogMapper;
    @Autowired
    private BMarketingMemberMapper bMarketingMemberMapper;
    @Autowired
    private BMarketingMemberExtMapper bMarketingMemberExtMapper;
    @Autowired
    private MessageProduce messageProduce;
    @Autowired
    private CouponActivityService couponActivityService;


    /**
     * 创建一个活动
     * @param bMarketingPlan
     * @return
     * @throws Exception
     */
    public Result createPrimary(BMarketingPlan bMarketingPlan) throws Exception {
        Result result = createBefore(bMarketingPlan);
        if (result.getStatus() == Result.FAIL_STATUS) return result;
        bMarketingPlanMapper.insertSelective(bMarketingPlan);
        addLog(bMarketingPlan.getSiteId(), bMarketingPlan.getOperatorType(), bMarketingPlan.getOperatorId(), "createPrimary", JSON.toJSONString(bMarketingPlan), "新增活动(活动ID)：" + bMarketingPlan.getId());
        return Result.success(bMarketingPlan);
    }

    private Result createBefore(BMarketingPlan bMarketingPlan) {
        if (bMarketingPlan == null || bMarketingPlan.getSiteId() == null || bMarketingPlan.getDefaultNum() == null || bMarketingPlan.getStartTime() == null || bMarketingPlan.getEndTime() == null
            || StringUtils.isBlank(bMarketingPlan.getName()) || StringUtils.isBlank(bMarketingPlan.getDescription())) {
            return Result.fail("参数异常。");
        }

        if (bMarketingPlan.getStartTime().compareTo(bMarketingPlan.getEndTime()) >= 0)
            return Result.fail("开始时间应小于结束时间。");

        /*List<BMarketingPlan> existPlanList = null;
        try {
            existPlanList = bMarketingPlanMapper.getExistPlanList(bMarketingPlan.getSiteId(), bMarketingPlan.getStartTime(), bMarketingPlan.getEndTime(), null);
        } catch (Exception e) {
            LOGGER.error("营销计划查询异常", e);
            return Result.fail("营销计划查询异常。");
        }

        if (CollectionUtils.isNotEmpty(existPlanList))
            return Result.fail("活动时间冲突。", existPlanList);*/

        return Result.success();
    }





    /**
     * 更新一个活动
     * @param bMarketingPlan
     * @return
     * @throws Exception
     */
    public Result editPrimary(BMarketingPlan bMarketingPlan) throws Exception {
        Result result = editBefore(bMarketingPlan);
        if (result.getStatus() == Result.FAIL_STATUS) return result;
        bMarketingPlanMapper.updateByPrimaryKeySelective(bMarketingPlan);
        addLog(bMarketingPlan.getSiteId(), bMarketingPlan.getOperatorType(), bMarketingPlan.getOperatorId(), "editPrimary", JSON.toJSONString(bMarketingPlan), "更新活动(活动ID)：" + bMarketingPlan.getId());
        return Result.success();
    }

    private Result editBefore(BMarketingPlan bMarketingPlan) throws Exception {
        if (bMarketingPlan == null || bMarketingPlan.getSiteId() == null || bMarketingPlan.getId() == null) return Result.fail("参数异常。");
        if (detail(bMarketingPlan.getSiteId(), bMarketingPlan.getId()) == null) return Result.fail("查无活动计划。");

        List<BMarketingPlan> existPlanList = null;
        if (bMarketingPlan.getStartTime() == null || bMarketingPlan.getEndTime() == null) {
            bMarketingPlan.setStartTime(null);
            bMarketingPlan.setEndTime(null);
        } else if (bMarketingPlan.getStartTime().compareTo(bMarketingPlan.getEndTime()) >= 0) {
            return Result.fail("开始时间应小于结束时间。");
        } /*else {
            existPlanList = bMarketingPlanMapper.getExistPlanList(bMarketingPlan.getSiteId(), bMarketingPlan.getStartTime(), bMarketingPlan.getEndTime(), bMarketingPlan.getId());
        }

        if (CollectionUtils.isNotEmpty(existPlanList))
            return Result.fail("活动时间冲突。", existPlanList);
*/
        return Result.success();
    }




    /**
     *  删除一个活动
     * @param bMarketingPlan
     * @return
     */
    @Transactional
    public Result delPrimary(BMarketingPlan bMarketingPlan) throws Exception {
        if (bMarketingPlan == null || bMarketingPlan.getSiteId() == null || bMarketingPlan.getId() == null) return Result.fail("参数异常。");
        if (detail(bMarketingPlan.getSiteId(), bMarketingPlan.getId()) == null) return Result.fail("查无活动计划。");

        bMarketingPlanMapper.deleteById(bMarketingPlan.getSiteId(), bMarketingPlan.getId());
        bMarketingPlanExtMapper.deleteByPlanId(bMarketingPlan.getSiteId(), bMarketingPlan.getId());

        addLog(bMarketingPlan.getSiteId(), bMarketingPlan.getOperatorType(), bMarketingPlan.getOperatorId(), "delPrimary", JSON.toJSONString(bMarketingPlan), "删除活动(活动ID)：" + bMarketingPlan.getId());
        return Result.success();
    }







    /**
     * 查看一个活动
     * @param siteId
     * @param id
     * @return
     * @throws Exception
     */
    public BMarketingPlan detail(Integer siteId, Integer id) throws Exception {
        BMarketingPlan bMarketingPlan = bMarketingPlanMapper.selectById(siteId, id);
        prizesNumSum(bMarketingPlan);
        return bMarketingPlan;
    }

    /**
     *  查询活动列表
     * @param bMarketingPlan
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public List<BMarketingPlan> list(BMarketingPlan bMarketingPlan, Integer pageNum, Integer pageSize) throws Exception {
        Page page = PageHelper.startPage(pageNum, pageSize);
        List<BMarketingPlan> planList = bMarketingPlanMapper.getPlanList(bMarketingPlan, pageNum, pageSize);
        if (CollectionUtils.isNotEmpty(planList)) planList.stream().forEach(this::prizesNumSum);
        return planList;
    }

    private void prizesNumSum(BMarketingPlan bMarketingPlan) {
        if (bMarketingPlan != null) {
            bMarketingPlan.setWinnersSum(bMarketingPlanMapper.getWinnersSum(bMarketingPlan.getSiteId(), bMarketingPlan.getId()));
            bMarketingPlan.setServerTime(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            if (CollectionUtils.isEmpty(bMarketingPlan.getPrizesSettingList())) {
                bMarketingPlan.setPrizesSettingList(null);
            } else {
                Integer ceilingSum = 0;
                int receiveSum = 0;
                for (BMarketingPlan.PrizesSetting prizes : bMarketingPlan.getPrizesSettingList()) {
                    if (prizes.getType() != PrizesType.NONE.getVal()) {
                        if (ceilingSum != null && prizes.getCeiling() != null) {
                            ceilingSum += prizes.getCeiling();
                        } else {
                            ceilingSum = null;
                        }
                        if (prizes.getReceive() != null) receiveSum += prizes.getReceive();
                    }
                }
                bMarketingPlan.setCeilingSum(ceilingSum);
                bMarketingPlan.setReceiveSum(receiveSum);
            }
        }
    }




    /**
     * 给一个活动配置奖品
     * @param list
     * @param siteId
     * @param planId
     * @param operatorType
     * @param operatorId
     * @return
     * @throws Exception
     */
    @Transactional
    public Result setting(List<PlanExtListParam.MarketingPlanExt> list, Integer siteId, Integer planId, Integer operatorType, Integer operatorId) throws Exception {
        if (CollectionUtils.isEmpty(list)) return Result.fail("奖品设置不能为空。");

        BMarketingPlan plan = detail(siteId, planId);//活动计划
        if (plan == null) return Result.fail("查无活动计划。");

        Set<Integer> ruleIdsSet = list.stream().filter(e -> e.getType() == PrizesType.COUPON.getVal()).map(e -> e.getTypeId()).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(ruleIdsSet)) {
            if (CollectionUtils.isNotEmpty(plan.getPrizesSettingList())) {
                Set<Integer> originRuleIdsSet = plan.getPrizesSettingList().stream().filter(e -> e.getType() == PrizesType.COUPON.getVal()).map(e -> e.getTypeId()).collect(Collectors.toSet());
                ruleIdsSet = ruleIdsSet.stream().filter(e -> !originRuleIdsSet.contains(e)).collect(Collectors.toSet());
            }
            String ruleIds = Joiner.on(",").skipNulls().join(ruleIdsSet);
            settingCoupon(siteId, ruleIds, plan.getName(), plan.getCouponActivityId(), planId, null);
        }

        bMarketingPlanExtMapper.insertByList(list, siteId, planId);
        if (plan.getStatus().compareTo(PlanStatus.CREATE_PLAN.getVal()) == 0) bMarketingPlanMapper.updateStatusById(siteId, planId, PlanStatus.SETTING_PRIZES.getVal());

        addLog(siteId, operatorType, operatorId, "setting", JSON.toJSONString(list), "设置奖品(活动ID)：" + planId);
        return Result.success();
    }

    @Transactional
    public Result editChances(List<PlanExtListParam.MarketingPlanExt> list, Integer siteId, Integer planId, Integer operatorType, Integer operatorId) throws Exception {
        if (CollectionUtils.isEmpty(list) || siteId == null || planId == null/* || !list.stream().allMatch(o -> o.getId() != null)*/)
            return Result.fail("缺少必要参数。");

        BMarketingPlan plan = detail(siteId, planId);//活动计划
        if (plan == null) return Result.fail("查无活动计划。");

        list = list.stream().filter(o -> o.getId() != null).collect(Collectors.toList());
        bMarketingPlanExtMapper.updateByList(list, siteId, planId);

        addLog(siteId, operatorType, operatorId, "editChances", JSON.toJSONString(list), "批量更新奖品(活动ID)：" + planId);
        return Result.success();
    }


    /**
     *  更新一个奖品
     * @param planExt
     * @param operatorType
     * @param operatorId
     * @return
     * @throws Exception
     */
    public Result editSetting(BMarketingPlanExt planExt, Integer operatorType, Integer operatorId) throws Exception {
        if (planExt == null || planExt.getSiteId() == null || planExt.getId() == null || planExt.getMarketingPlanId() == null) return Result.fail("缺少必要参数。");

        BMarketingPlan plan = detail(planExt.getSiteId(), planExt.getMarketingPlanId());//活动计划
        if (plan == null) return Result.fail("查无活动计划。");

        BMarketingPlan.PrizesSetting prizes = plan.getPrizesSettingList().stream().filter(p -> p.getId().equals(planExt.getId())).findFirst().get();
        if (planExt.getType() == null && planExt.getTypeId() == null && planExt.getTypeInfo() == null) {
            planExt.setReceive(null);
        } else if (planExt.getType() != null && (planExt.getTypeId() != null || planExt.getTypeInfo() != null)) {
            if (planExt.getType() == PrizesType.COUPON.getVal()) {
                if (planExt.getTypeId() == null) {
                    return Result.fail("缺少必要参数。");
                } else {
                    if (PrizesType.COUPON.getVal() == prizes.getType()) {
                        delCouponId(plan, prizes.getTypeId());
                    }
                    Set<Integer> originRuleIdsSet = plan.getPrizesSettingList().stream().filter(e -> e.getType() == PrizesType.COUPON.getVal()).map(e -> e.getTypeId()).collect(Collectors.toSet());
                    if (CollectionUtils.isEmpty(originRuleIdsSet) || !originRuleIdsSet.contains(planExt.getTypeId()))
                        settingCoupon(planExt.getSiteId(), planExt.getTypeId() + "", plan.getName(), plan.getCouponActivityId(), plan.getId(), null);
                }
            }
            planExt.setReceive(0);
        } else {
            return Result.fail("无法处理的请求。");
        }

        bMarketingPlanExtMapper.updateByPrimaryKeySelective(planExt);

        addLog(planExt.getSiteId(), operatorType, operatorId, "editSetting", JSON.toJSONString(planExt), "更新奖品(奖品ID)：" + planExt.getId());
        return Result.success();
    }

    private void settingCoupon(Integer siteId, String ruleIds, String actName, Integer actId, Integer planId, String originTypeIds) throws Exception {
        if (StringUtils.isNotBlank(ruleIds)) {
            if (actId == null) {
                CouponActivity couponActivity = couponActivityService.createActivityForTurntable(siteId, actName, ruleIds, null, null);
                bMarketingPlanMapper.updateCouponActivityId(siteId, planId, couponActivity.getId());
            } else {
                /*Boolean flag = couponActivityService.updateActivityForTurnTable(siteId, actId, ruleIds);
                if (!flag) throw new RuntimeException("updateActivityForTurnTable - 异常");*/
                if (StringUtils.isNotBlank(originTypeIds)) couponActivityService.updateRemoveActivity(siteId, actId, originTypeIds);
                couponActivityService.updateAddActivity(siteId, actId, ruleIds);
            }
        }
    }


    /**
     *  删除一个奖品
     * @param planExt
     * @param operatorType
     * @param operatorId
     * @return
     */
    public Result delSetting(BMarketingPlanExt planExt, Integer operatorType, Integer operatorId) throws Exception {
        if (planExt == null || planExt.getSiteId() == null || planExt.getId() == null || planExt.getMarketingPlanId() == null) return Result.fail("缺少必要参数。");

        BMarketingPlan plan = detail(planExt.getSiteId(), planExt.getMarketingPlanId());//活动计划
        if (plan == null) return Result.fail("查无活动计划。");

        BMarketingPlan.PrizesSetting prizes = plan.getPrizesSettingList().stream().filter(p -> p.getId().equals(planExt.getId())).findFirst().get();
        if (plan.getPrizesSettingList().size() <= 3) return Result.fail("剩余奖品项数不应少于3。");
        if (PrizesType.NONE.getVal() == prizes.getType()) {
            List<BMarketingPlan.PrizesSetting> nonePrizes = plan.getPrizesSettingList().stream().filter(p -> p.getType() == PrizesType.NONE.getVal()).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(nonePrizes) || nonePrizes.size() <= 1) return Result.fail("应至少设置一个谢谢惠顾类型。");
        } else if (PrizesType.COUPON.getVal() == prizes.getType()) {
            delCouponId(plan, prizes.getTypeId());
        }

        bMarketingPlanExtMapper.deleteById(planExt.getSiteId(), planExt.getId());
        addLog(planExt.getSiteId(), operatorType, operatorId, "delSetting", JSON.toJSONString(planExt), "删除奖品(奖品ID)：" + planExt.getId());
        return Result.success();
    }

    private void delCouponId(BMarketingPlan plan, Integer typeId) throws Exception {
        List<Integer> originRuleIdsList = plan.getPrizesSettingList().stream()
            .filter(e -> e.getType() == PrizesType.COUPON.getVal())
            .filter(e -> e.getTypeId().equals(typeId)).map(e -> e.getTypeId()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(originRuleIdsList) && originRuleIdsList.size() <= 1)
            couponActivityService.updateRemoveActivity(plan.getSiteId(), plan.getCouponActivityId(), typeId + "");
    }


    /**
     *  更新一个计划状态
     * @param bMarketingPlan
     * @return
     * @throws Exception
     */
    public Result startOrStop(BMarketingPlan bMarketingPlan) throws Exception {
        if (bMarketingPlan == null || bMarketingPlan.getSiteId() == null || bMarketingPlan.getId() == null || bMarketingPlan.getStop() == null) return Result.fail("参数异常。");
        if (detail(bMarketingPlan.getSiteId(), bMarketingPlan.getId()) == null) return Result.fail("查无活动计划。");

        bMarketingPlanMapper.startOrStop(bMarketingPlan.getSiteId(), bMarketingPlan.getId(), bMarketingPlan.getStop());

        addLog(bMarketingPlan.getSiteId(), bMarketingPlan.getOperatorType(), bMarketingPlan.getOperatorId(), "startOrStop", JSON.toJSONString(bMarketingPlan), "更新活动启停状态(活动ID)：" + bMarketingPlan.getId());
        return Result.success();
    }





    /**
     *  更新一个计划类型
     * @param bMarketingPlan
     * @return
     * @throws Exception
     */
    @Transactional
    public Result editType(BMarketingPlan bMarketingPlan) throws Exception {
        if (bMarketingPlan == null || bMarketingPlan.getSiteId() == null || bMarketingPlan.getId() == null || bMarketingPlan.getType() == null) return Result.fail("参数异常。");
        BMarketingPlan plan = detail(bMarketingPlan.getSiteId(), bMarketingPlan.getId());
        if (plan == null) return Result.fail("查无活动计划。");

        bMarketingPlanMapper.updateTypeById(bMarketingPlan.getSiteId(), bMarketingPlan.getId(), bMarketingPlan.getType(), bMarketingPlan.getStyle());
        if (plan.getType() == null /*&& plan.getStatus().compareTo(PlanStatus.SETTING_PRIZES.getVal()) == 0*/) bMarketingPlanMapper.updateStatusById(bMarketingPlan.getSiteId(), bMarketingPlan.getId(), PlanStatus.SETTING_TYPE.getVal());

        addLog(bMarketingPlan.getSiteId(), bMarketingPlan.getOperatorType(), bMarketingPlan.getOperatorId(), "editType", JSON.toJSONString(bMarketingPlan), "更新活动类型(活动ID)：" + bMarketingPlan.getId());
        return Result.success();
    }


    /**
     *  完成发布一个计划
     * @param bMarketingPlan
     * @return
     * @throws Exception
     */
    public Result settingComplete(BMarketingPlan bMarketingPlan) throws Exception {
        if (bMarketingPlan == null || bMarketingPlan.getSiteId() == null || bMarketingPlan.getId() == null) return Result.fail("参数异常。");
        BMarketingPlan plan = detail(bMarketingPlan.getSiteId(), bMarketingPlan.getId());
        if (plan == null) return Result.fail("查无活动计划。");
        if (plan.getStatus().compareTo(PlanStatus.SETTING_TYPE.getVal()) == 0)
            bMarketingPlanMapper.updateStatusById(bMarketingPlan.getSiteId(), bMarketingPlan.getId(), PlanStatus.COMPLETE.getVal());

        addLog(bMarketingPlan.getSiteId(), bMarketingPlan.getOperatorType(), bMarketingPlan.getOperatorId(), "settingComplete", JSON.toJSONString(bMarketingPlan), "完成发布活动(活动ID)：" + bMarketingPlan.getId());
        return Result.success();
    }





    /**
     * 抽取一个活动的一个奖品
     * @param randomParams
     * @return
     */
    @Transactional
    public synchronized Result prizesRandom(RandomParams randomParams) throws Exception {
        if (randomParams == null || randomParams.getSiteId() == null || randomParams.getPlanId() == null || randomParams.getBuyerId() == null)
            return Result.fail("参数异常。");

        BMarketingPlan plan = detail(randomParams.getSiteId(), randomParams.getPlanId());//活动计划
        BMarketingMember memberPrizes = memberPrizesDetail(randomParams.getSiteId(), randomParams.getPlanId(), randomParams.getBuyerId());//会员参与情况

        Result result = checkMemberPrizes(plan, memberPrizes);
        if (result.getStatus() == Result.FAIL_STATUS) return result;

        BMarketingPlan.PrizesSetting prizes = selectPrizes(plan.getPrizesSettingList());
        prizes = checkPrizes(prizes, randomParams, plan, memberPrizes);

        if (prizes.getType() != PrizesType.NONE.getVal()) {
            int r = bMarketingPlanExtMapper.updateReceiveByPrizesId(prizes.getId(), randomParams.getSiteId(), prizes.getReceive());
            if (r == 0) throw new RuntimeException("updateReceiveByPrizesId - 异常");
        }

        Integer tag = null;
        if (memberPrizes == null) {
            memberPrizes = new BMarketingMember(){{
                setSiteId(randomParams.getSiteId());
                setMarketingPlanId(randomParams.getPlanId());
                setMemberId(randomParams.getBuyerId());
                setDrawNum(1);
            }};
            bMarketingMemberMapper.insertSelective(memberPrizes);
            tag = 1;
        } else {
            int num = bMarketingMemberMapper.updateDrawNum(memberPrizes.getSiteId(), memberPrizes.getId(), memberPrizes.getDrawNum());
            if (num == 0) throw new RuntimeException("updateCeilingDraw - 异常");
            tag = memberPrizes.getDrawNum() + 1;
        }

        BMarketingMemberExt ext = new BMarketingMemberExt();
        ext.setSiteId(randomParams.getSiteId());
        ext.setMarketingMemberId(memberPrizes.getId());
        ext.setType(prizes.getType());
        ext.setTypeId(prizes.getTypeId());
        ext.setTypeInfo(prizes.getTypeInfo());
        ext.setTag(tag);
//        ext.setStatus(sendStatus);
        bMarketingMemberExtMapper.insertSelective(ext);

        prizesSort(plan.getPrizesSettingList());
        int index = Arrays.binarySearch(plan.getPrizesSettingList().toArray(new BMarketingPlan.PrizesSetting[0]), prizes, (o1, o2) -> o1.getId().compareTo(o2.getId()));
        Map<String, Object> respMap = new HashMap<>();
        if (prizes.getType() != PrizesType.NONE.getVal()) {
            sendPrizesMessage(prizes, randomParams, ext.getId());
            respMap.put("type", 1);
            respMap.put("index", index);
            respMap.put("info", new StringBuilder("获得 ")
                .append(prizes.getTypeInfo())
                .append(" ")
                .append(prizes.getType() == PrizesType.COUPON.getVal() ? "优惠券" : (prizes.getType() == PrizesType.SCORE.getVal() ? "积分" : ""))
                .toString());
        } else {
            respMap.put("type", 0);
            respMap.put("index", index);
            respMap.put("info", "谢谢惠顾，下次努力哦！");
        }
        prizes.setChances(null);
        prizes.setCeiling(null);
        prizes.setReceive(null);
        respMap.put("prizes", prizes);

        return Result.success(respMap);
    }

    public Result statusDetail(RandomParams randomParams) throws Exception {
        if (randomParams == null || randomParams.getSiteId() == null || randomParams.getPlanId() == null || randomParams.getBuyerId() == null)
            return Result.fail("参数异常。");

        BMarketingPlan plan = detail(randomParams.getSiteId(), randomParams.getPlanId());//活动计划
        BMarketingMember memberPrizes = memberPrizesDetail(randomParams.getSiteId(), randomParams.getPlanId(), randomParams.getBuyerId());//会员参与情况
        List<Winner> winnersLog = bMarketingPlanMapper.getWinnersByPlanId(randomParams.getSiteId(), randomParams.getPlanId());

        Result result = checkMemberPrizes(plan, memberPrizes);

        prizesSort(plan.getPrizesSettingList());
        Map<String, Object> respMap = new HashMap<>();
        respMap.put("planId", plan.getId());
        respMap.put("name", plan.getName());
        respMap.put("desc", plan.getDescription());
        respMap.put("type", plan.getType());
        respMap.put("style", plan.getStyle());
        respMap.put("status", plan.getStatus());
        respMap.put("stop", plan.getStop());
        respMap.put("del", plan.getDel());
        respMap.put("sTime", plan.getStartTime());
        respMap.put("eTime", plan.getEndTime());
        respMap.put("rTime", plan.getServerTime());
        respMap.put("prizes", plan.getPrizesSettingList() == null ? null : plan.getPrizesSettingList().stream().map(prizes -> {
            prizes.setChances(null);
            prizes.setCeiling(null);
            prizes.setReceive(null);
            String info = prizes.getTypeInfo() + " ";
            if (prizes.getType() == PrizesType.NONE.getVal()) {
                info = PrizesType.NONE.getDesc();
            } else if (prizes.getType() == PrizesType.COUPON.getVal()) {
                info += PrizesType.COUPON.getDesc();
            } else if (prizes.getType() == PrizesType.SCORE.getVal()) {
                info += PrizesType.SCORE.getDesc();
            }
            prizes.setInfo(info);
            return prizes;
        }).collect(Collectors.toList()));
        respMap.put("ceiling", memberDrawCeiling(plan, memberPrizes));
        respMap.put("drawNum", memberPrizes == null ? 0 : memberPrizes.getDrawNum());
        respMap.put("winners", CollectionUtils.isEmpty(winnersLog) ? null : winnersLog.stream().map(winners -> new StringBuilder("恭喜 ")
            .append(winners.getMobile())
            .append(" 获得了 ")
            .append(winners.getTypeInfo())
            .append(" ")
            .append(winners.gettName())
            .toString()).collect(Collectors.toList()));
        respMap.put("alert", result.getStatus() == Result.FAIL_STATUS ? result.getMsg() : null);

        return Result.success(respMap);
    }

    private void prizesSort(List<BMarketingPlan.PrizesSetting> list) {
        if (CollectionUtils.isNotEmpty(list)) list.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
    }

    private boolean sendPrizesMessage(BMarketingPlan.PrizesSetting prizes, RandomParams randomParams, Integer marketingMemberExtId) {
        boolean result = true;
        MessageProduce.MessageBody messageBody = null;
        try {
            messageBody = new MessageProduce.MessageBody() {{
                setType(PrizesMessage.MESSAGE_TYPE);
                setData(new PrizesMessage(){{
                    setSiteId(randomParams.getSiteId());
                    setPlanId(randomParams.getPlanId());
                    setMobile(randomParams.getMobile());
                    setBuyerId(randomParams.getBuyerId());
                    setPrizesId(prizes.getId());
                    setType(prizes.getType());
                    setTypeId(prizes.getTypeId());
                    setTypeInfo(prizes.getTypeInfo());
                    setMarketingMemberExtId(marketingMemberExtId);
                }});
            }};
            messageProduce.sendMessage(messageBody);
        } catch (Exception e) {
            LOGGER.error("sendPrizesMessage 异常 {}", e);
            result = false;
        }
        addLog(randomParams.getSiteId(), 98, randomParams.getBuyerId(), "sendPrizesMessage",
            JSON.toJSONString(messageBody), "发送获奖消息(活动ID)：" + randomParams.getPlanId() + " - " + result);
        return result;
    }

    public BMarketingMember memberPrizesDetail(Integer siteId, Integer planId, Integer buyerId) throws Exception {
        return bMarketingMemberMapper.selectByBuyerId(siteId, planId, buyerId);
    }

    public Result checkMemberPrizes(BMarketingPlan plan, BMarketingMember memberPrizes) {
        Result result = checkMarketingPlan(plan);
        if (result.getStatus() == Result.FAIL_STATUS) return result;
        if (plan.getDefaultNum() != null && (plan.getDefaultNum() == 0 || (memberPrizes != null && memberDrawCeiling(plan, memberPrizes).compareTo(memberPrizes.getDrawNum()) <= 0)))
            return Result.fail("当前抽奖机会已使用完。");
        return Result.success();
    }

    public Result checkMarketingPlan(BMarketingPlan plan) {
        if (plan == null || plan.getDel() == true) return Result.fail("查无活动计划。");
        if (plan.getStartTime() == null || plan.getEndTime() == null) return Result.fail("活动时间异常。");
        if (plan.getStop() == true) return Result.fail("当前抽奖活动已暂停，请留意官方信息。");
        if (plan.getStatus() != PlanStatus.COMPLETE.getVal()) return Result.fail("活动未完成发布。");

        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isBefore(LocalDateTime.ofInstant(plan.getStartTime().toInstant(), ZoneId.systemDefault()))) return Result.fail("当前抽奖活动暂未开始，敬请期待。");
        if (currentTime.isAfter(LocalDateTime.ofInstant(plan.getEndTime().toInstant(), ZoneId.systemDefault()))) return Result.fail("当前抽奖活动已结束，下次赶早。");

        return Result.success();
    }

    private BMarketingPlan.PrizesSetting checkPrizes(BMarketingPlan.PrizesSetting prizes, RandomParams randomParams, BMarketingPlan plan, BMarketingMember memberPrizes) {
        BMarketingPlan.PrizesSetting origin = prizes;
        if (prizes == null || (prizes.getCeiling() != null && prizes.getReceive().compareTo(prizes.getCeiling()) >= 0))
            prizes = plan.getPrizesSettingList().stream().filter(p -> p.getType() == PrizesType.NONE.getVal()).findFirst().get();

        addLog(randomParams.getSiteId(), 99, randomParams.getBuyerId(), "checkPrizes",
            "original：" + JSON.toJSONString(origin) + "，final：" + JSON.toJSONString(prizes),"检查奖品(活动ID)：" + randomParams.getPlanId());
        return prizes;
    }

    private Integer memberDrawCeiling(BMarketingPlan plan, BMarketingMember marketingMember) {
        if (plan.getDefaultNum() != null && marketingMember != null)
            return plan.getDefaultNum() + marketingMember.getAddNum();
        return plan.getDefaultNum();
    }

    private BMarketingPlan.PrizesSetting selectPrizes(List<BMarketingPlan.PrizesSetting> prizesList) {
        BMarketingPlan.PrizesSetting result = null;
        if (prizesList == null) return result;
        LOGGER.info("prizesList：{}", prizesList);

        prizesList.stream().forEach(prizes -> prizes.setChances(new BigDecimal(Double.toString(prizes.getChances())).divide(new BigDecimal("100"), 5, BigDecimal.ROUND_HALF_DOWN).floatValue()));
        prizesList.sort((o1, o2) -> o1.getChances() < o2.getChances() ? -1 : (o1.getChances() == o2.getChances() ? 0 : 1));

        double random = Math.random();
        BigDecimal sum = new BigDecimal(0.0);
        for (BMarketingPlan.PrizesSetting prizes : prizesList) {
            sum = sum.add(new BigDecimal(Double.toString(prizes.getChances())));
            if (random < sum.doubleValue()) {
                result = prizes;
                break;
            }
        }
        LOGGER.info("prizesList：{}，random：{}，result：{}", prizesList, random, result);

        return result;
    }










    public void addLog(Integer siteId, Integer operatorType, Integer operatorId, String processMethod, String params, String description) {
        try {
            bMarketingLogMapper.insertSelective(new BMarketingLog(){{
                setSiteId(siteId);
                setOperatorType(operatorType);
                setOperator(operatorId);
                setProcessMethod(processMethod);
                setParams(params);
                setDescription(description);
            }});
        } catch (Exception e) {
            LOGGER.error("添加营销日志异常", e);
        }
    }

}
