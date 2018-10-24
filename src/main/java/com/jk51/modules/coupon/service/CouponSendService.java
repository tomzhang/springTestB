package com.jk51.modules.coupon.service;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.StoreAdmin;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.coupon.*;
import com.jk51.model.coupon.requestParams.LimitRule;
import com.jk51.model.coupon.requestParams.ReissureActivity;
import com.jk51.model.coupon.requestParams.TimeRule;
import com.jk51.model.order.Member;
import com.jk51.model.order.Orders;
import com.jk51.model.order.Trades;
import com.jk51.model.promotions.rule.TimeRuleForPromotionsRule;
import com.jk51.modules.coupon.constants.CouponConstant;
import com.jk51.modules.coupon.mapper.*;
import com.jk51.modules.im.event.DelayedMessageProduce;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.jk51.modules.coupon.constants.CouponConstant.*;
import static java.util.stream.Collectors.toList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chenpeng
 * 创建日期: 2017/3/8
 * 修改记录:
 */
@Service
public class CouponSendService {

    private static final Logger log = LoggerFactory.getLogger(CouponSendService.class);

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private CouponActivityMapper couponActivityMapper;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private CouponRuleActivityMapper couponRuleActivityMapper;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private CouponClerkMapper couponClerkMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private StoreAdminMapper storeAdminMapper;
    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    private StringRedisTemplate srt;
    @Autowired
    private CouponDetailMapper detailMapper;
    @Autowired
    private CouponActivityProcessService couponActivityProcessService;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CouponDetailService couponDetailService;
    @Autowired
    private CouponActivityService couponActivityService;
    @Autowired
    private CouponActiveForMemberService couponActiveForMemberService;
    @Autowired
    private CouponActivityReissureMapper  couponActivityReissureMapper;

    @Autowired
    private DelayedMessageProduce delayedMessageProduce;

    @Autowired
    private StoresMapper storesMapper;

    /**
     * 优惠券发放,注册后发放
     *
     * @param siteId 商家id
     * @param userId 会员id
     * @return
     */
    public boolean sendCoupon(Integer siteId, Integer userId) {
        log.info("优惠券下单修改已使用数量。。。。。。。");

        try {
            List<CouponActivity> cr = couponActivityMapper.getCouponActivityList(siteId);
            for (CouponActivity item : cr) {
                if (item.getSendType() == null) {
                    continue;
                }
                if(Objects.nonNull(item.getTimeRule())){
                    if(!checkCouponActivityTimeRule(item.getTimeRule())){
                        log.info("当前时间不能发放优惠券。。。。。。");
                        continue;
                    }
                }
                if (item.getSendType().equals(CouponConstant.SEND_TYPE_REGIST)) {
                    List<CouponRuleActivity> cras = couponRuleActivityMapper.getRuleByActive(siteId, item.getId());

                    Integer seed = Calendar.getInstance().get(Calendar.MILLISECOND);
                    List<Integer> ruleIds = cras.stream()
                            .map(CouponRuleActivity::getRuleId)
                            .filter(ruleId -> couponActivityService.canSendBySendRules(ruleId, item, seed))
                            .collect(toList());

                    List<CouponRule> couponRules = getCouponRules(ruleIds, siteId);
                    List<CouponDetail> details = couponActivityProcessService.getCoupon(couponRules, userId,
                            String.valueOf(item.getId()), null);

                    details.stream()
                            .filter(Objects::nonNull)
                            .filter(detail -> syncCouponAmount(detail.getRuleId(), siteId))
                            .forEach(detail -> {
                                couponDetailMapper.insertCouponDetail(detail);
                                couponRuleService.updateRuleStatus(siteId, detail.getRuleId(), 0,
                                        null, null, null, null);
                                couponActivityProcessService.updateCouponCommon(siteId, detail.getRuleId(),
                                        item.getId(), 0, null, null);
                                couponActivityService.checkStatus(item.getId(), item.getSiteId());
                            });
                }
            }
        } catch (Exception e) {
            log.error("优惠券发放,注册后发放失败:{}",e);
            return false;
        }

        return true;
    }


    public boolean checkCouponActivityTimeRule(String couponActivityTimeRuleForJson){
        try {
            CouponActivityTimeRuleForJson activityTimeRule = JSON.parseObject(couponActivityTimeRuleForJson,CouponActivityTimeRuleForJson.class);
            switch (activityTimeRule.getValidity_type()){
                case 2:
                    //按照月份的日期

                    //当前月份最后一天
                    Calendar cale = Calendar.getInstance();
                    cale.set(Calendar.MONTH, LocalDateTime.now().getMonthValue());
                    cale.set(Calendar.DAY_OF_MONTH, 0);
                    Integer lastday = cale.get(Calendar.DAY_OF_MONTH);

                    String[] days = activityTimeRule.getAssign_rule().split(",");
                    Set<String> set = new HashSet<String>(Arrays.asList(days));
                    String dayOfMonth = LocalDateTime.now().getDayOfMonth() + "";

                    if (!(set.contains(dayOfMonth) || checklastDayWork(couponActivityTimeRuleForJson, lastday)))
                        return false;
                    break;
                case 3:
                    //按照星期
                    String[] assign_rule_weeks = activityTimeRule.getAssign_rule().split(",");
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
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private boolean checklastDayWork(String activityTimeRuleForJson, int lastday) {
        CouponActivityTimeRuleForJson activityTimeRule = JSON.parseObject(activityTimeRuleForJson, CouponActivityTimeRuleForJson.class);
        if (null == activityTimeRule.getLastDayWork() || activityTimeRule.getLastDayWork() == 0)
            return false;
        else if ((lastday - LocalDateTime.now().getDayOfMonth()) < activityTimeRule.getLastDayWork())
            return true;
        else
            return false;
    }

    /**
     * 优惠券发放，下完订单后调用
     *
     * @param tradeId 订单tradeId
     * @return
     */
    public boolean sendCouponByOrder(String tradeId) throws BusinessLogicException {
        Trades trades = getTradesDetial(Long.parseLong(tradeId));//根据订单id查询订单详情
       /* if (trades.getPostStyle().intValue() == CommonConstant.POST_STYLE_DIRECT_PURCHASE) {
            return false;
        }*/
        List<Map<String, Integer>> goodsInfo = getGoodsInfo(tradeId);//封装商品信息
        Integer siteId = trades.getSiteId();

        Member member = memberMapper.getMember(siteId, trades.getBuyerId());//buyer_id转换member_id
        Integer userId = member.getMemberId();
        // 去查下改订单有没有使用优惠券
        Map<String, Object> isNull = couponDetailMapper.findCouponByOrderId(tradeId);
        if (isNull != null) {
            CouponRule couponRule = couponRuleMapper.findCouponRuleById(Integer.parseInt(isNull.get("rule_id").toString()), siteId);

            if (couponRule != null) {
                // 此处更新rule表订单价格跟商品总数及使用次数
                int total = 0;
                total = goodsInfo.stream().mapToInt(map -> map.get("num")).sum();
                log.info("站点:" + siteId + " ruleId为:" + isNull.get("rule_id").toString() + " 订单价格为:" + trades.getRealPay() + "分，商品数量为:" + total);
                //改数量 优惠券规则表
                couponRuleService.updateRuleStatus(siteId, Integer.parseInt(isNull.get("rule_id").toString()), null,
                        couponRule.getUseAmount() + 1,
                        couponRule.getOrderPrice() + trades.getRealPay(),
                        couponRule.getGoodsNum() + total, null);
                if (StringUtils.isNotBlank((String) isNull.get("source"))) {
                    //改数量 活动
                    couponActivityProcessService.updateCouponCommon(siteId, Integer.parseInt(isNull.get("rule_id").toString()),
                            Integer.parseInt(isNull.get("source").toString()), null, 0, null);
                }
                // 此处先判断使用的是不是会员券(5,15,25)，如果是再补发一张
                TimeRule timeRule = JSON.parseObject(couponRule.getTimeRule(), TimeRule.class);
                if (timeRule.getValidity_type() == 3) {
                    String id = couponRuleService.getCouponDownDetailNum(Integer.parseInt(isNull.get("rule_id").toString()), siteId);
                    //计算距离券
                    CouponDetail detail = couponActivityProcessService.findDistanceResult(couponRule,userId,
                        CouponDetail.build(siteId, isNull.get("source").toString(),
                            Integer.parseInt(isNull.get("rule_id").toString()),
                            id, userId, tradeId)
                        );

                    if (syncCouponAmount(Integer.parseInt(isNull.get("rule_id").toString()), siteId)) {
                        if(detail!=null){
                            couponDetailMapper.insertCouponDetail(detail);
                            couponRuleService.updateRuleStatus(siteId, detail.getRuleId(), 0, null, null, null, null);
                        }
                    }
                }
            }
        }

         sendCouponByOrder(siteId, trades.getBuyerId(), goodsInfo, userId, tradeId, trades.getRealPay());
        return true;
    }

    /**
     * 优惠券发放，付款后调用
     *
     * @param tradeId 订单tradeId
     * @return
     */
    public boolean sendCouponByPay(String tradeId) throws BusinessLogicException {

        log.info("首单付款发放----pay sendCouponByPay----------------{}",tradeId);
        Trades trades = getTradesDetial(Long.parseLong(tradeId));//根据订单id查询订单详情
       /* if (trades.getPostStyle().intValue() == CommonConstant.POST_STYLE_DIRECT_PURCHASE) {
            return false;
        }*/
        List<Map<String, Integer>> goodsInfo = getGoodsInfo(tradeId);//封装商品信息
        Integer siteId = trades.getSiteId();
        Member member = memberMapper.getMember(siteId, trades.getBuyerId());//buyer_id转换member_id
        Integer userId = member.getMemberId();
        sendCouponByOrder(siteId, trades.getBuyerId(), goodsInfo, userId, tradeId, trades.getRealPay());
        return true;
    }


    /**
     * @param siteId
     * @param buyerId
     * @param goodsInfo
     * @param userId
     * @param tradeId
     * @param realPay
     */
    private void sendCouponByOrder(Integer siteId, Integer buyerId, List<Map<String, Integer>> goodsInfo, Integer userId,
                                   String tradeId, Integer realPay) {
        boolean isFirstOrder = orderService.checkUserFirstOrderByPayment(siteId, buyerId);//是否首单
        log.info("首单付款发放----err false----------------{}",isFirstOrder);
        List<Integer> ruleIds = new ArrayList<>();
        try {
            //首单
            List<CouponActivity> cr = couponActivityMapper.getCouponActivityList(siteId);
            for (CouponActivity item : cr) {
                if(Objects.nonNull(item.getTimeRule())){
                    if(!checkCouponActivityTimeRule(item.getTimeRule())){
                        log.info("当前时间不能发放优惠券。。。。。。");
                        continue;
                    }
                }
                Integer sendType = -1;
                if (item.getSendType() != null) {
                    sendType = item.getSendType();
                }
                if (isFirstOrder) {

                    log.info("首单付款发放----pay CouponActivity----------------{}",cr.toString());
                    // 首单发放并且是下单时候发放 zw
                    if (sendType.equals(CouponConstant.SEND_TYPE_FIRST_PAY) && checkSendCondition(item, goodsInfo, realPay)) {

                        List<CouponRuleActivity> cras = couponRuleActivityMapper.getRuleByActive(siteId, item.getId());

                        Integer seed = Calendar.getInstance().get(Calendar.MILLISECOND);
                        ruleIds = cras.stream()
                                .map(CouponRuleActivity::getRuleId)
                                .filter(ruleId -> couponActivityService.canSendBySendRules(ruleId, item, seed))
                                .collect(toList());

                        List<CouponRule> couponRules = getCouponRules(ruleIds, siteId);
                        List<CouponDetail> details = couponActivityProcessService.getCoupon(couponRules, String.valueOf(item.getId()), userId, tradeId);
                        directSendCoupon(details, siteId, item.getId());
                    }
                }

                // 只要付款后就发放，含首单 zw update by ztq
                if (sendType.equals(CouponConstant.SEND_TYPE_AFTER_PAY)) {
                    if (checkSendCondition(item, goodsInfo, realPay)) {
                        int sendWay = item.getSendWay();

                        List<CouponRuleActivity> cras = couponRuleActivityMapper.getRuleByActive(siteId, item.getId());

                        Integer seed = Calendar.getInstance().get(Calendar.MILLISECOND);
                        ruleIds = cras.stream()
                                .map(CouponRuleActivity::getRuleId)
                                .filter(ruleId -> couponActivityService.canSendBySendRules(ruleId, item, seed))
                                .collect(toList());

                        List<CouponRule> couponRules = getCouponRules(ruleIds, siteId);
                        List<CouponDetail> details = couponActivityProcessService.getCoupon(couponRules, userId, String.valueOf(item.getId()), null, tradeId);
                        switch (sendWay) {
                            case CouponConstant.SEND_WAY_ACCOUNT://直接发送账户
                                directSendCoupon(details, siteId, item.getId());
                                break;
                            case CouponConstant.SEND_WAY_WAIT://需用户领取
                                directSendCoupon(details, siteId, item.getId());
                                srt.opsForValue().set("payment" + String.valueOf(siteId) + String.valueOf(userId) + String.valueOf(item.getId()), String.valueOf(item.getId()));
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("站点:{}进入优惠券直接发放线程", siteId);
            e.printStackTrace();
        }

    }

    private void directSendCoupon(List<CouponDetail> details, Integer siteId, Integer activityId) {
        //批量插入
        details.removeAll(Collections.singleton(null));
        log.info("站点:进入优惠券直接发放线程共{}条数据",details.size());
        details = details.stream().filter(detail -> {
            int exist = couponDetailMapper.findCouponDetailBySiteIdAndSendOrder(siteId, detail.getSendOrderId(), detail.getRuleId(), detail.getSource());
            return exist < 1;
        }).collect(toList());
        couponDetailMapper.insertCouponDetailBatch(details);
        details.stream()
                .filter(detail -> syncCouponAmount(detail.getRuleId(), siteId))
                .forEach(detail -> {

                    couponRuleService.updateRuleStatus(siteId, detail.getRuleId(), 0, null,
                            null, null, null);

                    couponActivityProcessService.updateCouponCommon(siteId, detail.getRuleId(), activityId, 0,
                            null, null);

                });

        boolean success = couponActivityService.checkStatus(activityId, siteId);

        if (success)
            return;
        else
            throw new RuntimeException("更新状态失败");
    }

    /**
     * 直接发放优惠券
     *
     * @param siteId
     * @param activityId
     * @return
     */
    public boolean sendCouponDirect(Integer siteId, Integer activityId) {

        long startTime = System.currentTimeMillis();

        log.info("站点" + siteId + "活动id" + activityId + "：进入优惠券直接发放线程");

        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(siteId, activityId);
        if (couponActivity == null) {
            log.error("站点:" + siteId + "未找到直接发放优惠券");
            return false;
        }

        log.info("活动发放类型" + couponActivity.getSendType());
        if (couponActivity.getSendType() == null) {
            log.error("站点:" + siteId + "未找到发放优惠券类型");
            return false;
        }

        if (!couponActivity.getSendType().equals(CouponConstant.SEND_TYPE_DIRECT)) {
            log.error("站点:" + siteId + "发放优惠券类型不是直接发放");
            return false;
        }

        // 根据活动状态判断是否可以发放
        if (couponActivity.getStatus() != 0) {
            log.error("站点" + siteId + "活动id" + activityId + "：优惠券活动状态不是发布中，不可发放优惠券");
            return false;
        }


        log.info("站点:" + siteId + "开始发放优惠券");
        switch (couponActivity.getSendWay()) {
            case CouponConstant.SEND_WAY_ACCOUNT: // 直接发送至会员中心
                sendMember(couponActivity);
                break;
            case CouponConstant.SEND_WAY_WAIT://需用户领取
                break;
            case CouponConstant.SEND_WAY_RED_BAG://红包,微信下单页面
                break;
            case CouponConstant.SEND_WAY_STROE://门店，进门店后台领券中心领取
                break;
            case CouponConstant.SEND_WAY_CLERK://店员
                sendClerk(couponActivity);
                break;
        }

        //  更新activity状态
        CouponActivity a = couponActivityMapper.getCouponActivity(siteId, activityId);
        Integer sendStatus = 0;
        if (null != a.getSendStatus()) {
            sendStatus = a.getSendStatus() + 1;
        }

        //利用session未关闭设置参数 -- 持久态 --不一定成功
        a.setSendStatus(sendStatus);
        //重新更新一遍
        couponActivityMapper.updateSendStatus(a);

        // 更新活动和规则状态，此处代码冗余，但不会有逻辑问题
        boolean success = couponActivityService.checkStatus(activityId, siteId);
        if (success) {
            log.debug("activity status is updated");
        } else {
            log.error("activity status fail");
        }

        long endTime = System.currentTimeMillis();
        log.info("站点" + siteId + "活动id" + activityId + "发放结束，耗时:" + (endTime - startTime));
        return true;
    }

    /**
     * 划分会员数量的尺度
     */
    private final static int MEMBER_SLICE_NUM = 1000;

    /**
     * 发送给会员
     *
     * @param couponActivity
     */
    private void sendMember(CouponActivity couponActivity) {
        List<Member> members;
        List<CouponRule> couponRules;
        // sendNumTag 指的是优惠券活动的发放种类类型, pageSize则是会员分页基数
        int sendNumTag = ACTIVITY_SEND_ALL;
        Integer pageSize = MEMBER_SLICE_NUM;

        // 获取sendNumTag
        try {
            if (couponActivity.getSendRules() != null) {
                CouponActivityRulesForJson sendRules = JacksonUtils.json2pojo(couponActivity.getSendRules(), CouponActivityRulesForJson.class);
                sendNumTag = sendRules.getSendNumTag();
            } else {
                sendNumTag = ACTIVITY_SEND_ALL;
            }
        } catch (Exception e) {
            log.error("解析sendRules出错, ", e);
            sendNumTag = ACTIVITY_SEND_ALL;
        }

        // 根据活动发放对象分全部会员和指定会员
        switch (couponActivity.getSendObj()) {
            case ACTIVITY_SPECIFIED_MEMBER:
            case ACTIVITY_SPECIFIED_MEMBER_LABEL:
                // 查询全部的指定会员
                members = couponActiveForMemberService.queryAllMemberForCouponActive(couponActivity);
                // 查询活动下满足会员数量的优惠券规则，并且把数据的优惠券数量和状态更新，即预先取出优惠券
                couponRules = getCouponRules(couponActivity, members.size(), sendNumTag);
                // 发放优惠券给会员
                binCouponDetail(couponActivity, couponRules, members, sendNumTag);
                break;
            case ACTIVITY_CUSTOM_MEMBER:
                // 查询全部的自定义会员
                members = couponActiveForMemberService.queryAllMemberForCouponActive(couponActivity);
                // 查询活动下满足会员数量的优惠券规则，并且把数据的优惠券数量和状态更新，即预先取出优惠券
                couponRules = getCouponRules(couponActivity, members.size(), sendNumTag);
                // 发放优惠券给会员
                binCouponDetail(couponActivity, couponRules, members, sendNumTag);
                break;
            case ACTIVITY_ALL_MEMBER:
                if(sendNumTag == ACTIVITY_TURN_TABLE){
                    //转盘 转盘不能发放
                    break;
                }
                int i = 0;

                do { // 每次循环从数据库中取出1000个会员
                    members = memberMapper.findAllMemberByPage(couponActivity.getSiteId(), pageSize, i * pageSize);
                    // 查询活动下满足会员数量的优惠券规则，并且把数据的优惠券数量和状态更新，即预先取出优惠券
                    couponRules = getCouponRules(couponActivity, members.size(), sendNumTag);
                    // 发放优惠券给会员
                    binCouponDetail(couponActivity, couponRules, members, sendNumTag);
                    // 查询活动下是否还有优惠券可发
                    boolean isOver = activityIsOver(couponActivity.getId(), couponActivity.getSiteId());
                    if (isOver)
                        break;
                    i++;
                }
                while (members.size() == 1000);
                break;
            default:
                throw new RuntimeException("不应该出现的错误");
        }
    }

    /**
     * 查询活动下是否还有优惠券可发
     *
     * @param activityId
     * @param siteId
     * @return
     */
    private boolean activityIsOver(Integer activityId, Integer siteId) {
        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(siteId, activityId);
        return couponActivity.getStatus() != 0;
    }

    public CouponRule getCouponRulesForTurnTable(CouponActivity couponActivity,Integer sendRule) {
        List<CouponRuleActivity> ruleByActive = couponRuleActivityMapper.getRuleByActive(couponActivity.getSiteId(), couponActivity.getId());
        List<CouponRule> couponRules = ruleByActive.stream()
            .map(couponRuleActivity -> couponRuleMapper.findCouponRuleInActionById(couponRuleActivity.getRuleId(), couponRuleActivity.getSiteId()))
            .filter(Objects::nonNull)
            .collect(toList());
        List<CouponRule> result = new ArrayList<>();
        if (couponRules.size() == 0) return null;
        couponRules.stream()
            .filter(couponRule -> Objects.equals(couponRule.getRuleId(),sendRule))
            .filter(couponRule -> couponRule.getAmount() != 0)
            .forEach(couponRule -> beforeBuildCouponDetail(1, result, couponRule, couponActivity.getId()));
        if(result.size()==1){
            return result.get(0);
        }
        return null;
    }
    /**
     * 查询活动下满足会员数量的优惠券规则，并且把数据的优惠券数量和状态更新，即预先取出优惠券
     *
     * @param couponActivity
     * @param size           要发优惠券的会员数量
     * @param sendNumTag
     * @return
     */
    private List<CouponRule> getCouponRules(CouponActivity couponActivity, int size, int sendNumTag) {
        List<CouponRuleActivity> ruleByActive = couponRuleActivityMapper.getRuleByActive(couponActivity.getSiteId(), couponActivity.getId());
        List<CouponRule> couponRules = ruleByActive.stream()
                .map(couponRuleActivity -> couponRuleMapper.findCouponRuleInActionById(couponRuleActivity.getRuleId(), couponRuleActivity.getSiteId()))
                .filter(couponRule -> couponRule != null)
                .collect(toList());

        List<CouponRule> result = new ArrayList<>();
        if (couponRules.size() == 0) return result;
        switch (sendNumTag) {
            case ACTIVITY_SEND_RANDOM_ONE:
                Random random = new Random();
                int totalAmount = 0;

                if (size <= MEMBER_SLICE_NUM / 10) { // 小于划分数量，可以随机一种即可
                    do {
                        totalAmount = prepareCouponRules(size, couponRules, result, random, totalAmount, couponActivity.getId());
                        if (totalAmount == -1) break;
                    }
                    while (totalAmount < size);
                } else { // 大于划分数量，需要随机多种
                    do {
                        totalAmount = prepareCouponRules(MEMBER_SLICE_NUM / 10, couponRules, result, random, totalAmount, couponActivity.getId());
                        if (totalAmount == -1) break;
                    }
                    while (totalAmount < size);
                }
                break;
            case ACTIVITY_SEND_ONLY_ONE:
            case ACTIVITY_SEND_ALL:
            default:
                couponRules.stream()
                        .filter(couponRule -> couponRule.getAmount() != 0) // 这里的过滤是为了防止couponRule的status的延时变更
                        .forEach(couponRule -> beforeBuildCouponDetail(size, result, couponRule, couponActivity.getId()));
                break;
        }
        return result;
    }

    private int prepareCouponRules(int size, List<CouponRule> couponRules, List<CouponRule> result, Random random, int totalAmount, int activityId) {
        int randomIndex = random.nextInt(couponRules.size());
        CouponRule couponRule = couponRules.get(randomIndex);

        boolean hasResidue = beforeBuildCouponDetail(size, result, couponRule, activityId);
        if (!hasResidue) {
            couponRules.remove(randomIndex);
        }
        if (couponRules.size() == 0)
            return -1;

        totalAmount += result.get(result.size() - 1).getAmount();
        return totalAmount;
    }

    /**
     * 这里的构建CouponRule时的size不是优惠券规则真实的amount字段，而是用来说明该优惠券有几张优惠券会用来发放
     *
     * @param size
     * @param result
     * @param couponRule
     * @param activityId
     * @return
     */
    private boolean beforeBuildCouponDetail(int size, List<CouponRule> result, CouponRule couponRule, int activityId) {
        CouponRule temp;
        boolean hasResidue = true;
        //如果该券规则的数量不足时不处理
        if (couponRule.getAmount() == 0) {
            return false;
        } else if (couponRule.getAmount() == -1) {
            //券规则数量不限  此处size 为会员数量
            temp = new CouponRule(couponRule.getSiteId(), couponRule.getRuleId(), size);
        } else if (couponRule.getAmount() > size) {
            temp = new CouponRule(couponRule.getSiteId(), couponRule.getRuleId(), size);
            couponActivityService.updateAfterSendRule(couponRule,couponRule.getSiteId(), activityId, couponRule.getRuleId(), size);
            couponRule.setAmount(couponRule.getAmount() - size);
        } else {
            temp = new CouponRule(couponRule.getSiteId(), couponRule.getRuleId(), couponRule.getAmount());
            couponActivityService.updateAfterSendRule(couponRule,couponRule.getSiteId(), activityId, couponRule.getRuleId(), couponRule.getAmount());
            couponRule.setAmount(0);
            hasResidue = false;
        }
        result.add(temp);
        return hasResidue;
    }

    /**
     * 发放优惠券给会员
     *
     * @param couponActivity
     * @param couponRules
     * @param members
     * @param sendNumTag
     */

    private void binCouponDetail(CouponActivity couponActivity, List<CouponRule> couponRules, List<Member> members, int sendNumTag) {
        List<CouponDetail> batchInsert = new ArrayList<>();

        int totalAmount = 0;

        switch (sendNumTag) {
            case ACTIVITY_SEND_ONLY_ONE:
            case ACTIVITY_SEND_ALL:
                for (CouponRule couponRule : couponRules) {
                    int amount = couponRule.getAmount();
                    if (amount == -1) {
                        totalAmount = Integer.MAX_VALUE;
                        break;
                    } else {
                        totalAmount = totalAmount > amount ? totalAmount : amount;
                    }
                }
                break;
            case ACTIVITY_SEND_RANDOM_ONE:
                for (CouponRule couponRule : couponRules) {
                    int amount = couponRule.getAmount();
                    if (amount == -1) {
                        totalAmount = Integer.MAX_VALUE;
                        break;
                    } else {
                        totalAmount += amount;
                    }
                }
                break;
        }

        members.stream()
                .limit(totalAmount)
                .forEach(member -> {
                            if (sendNumTag == ACTIVITY_SEND_RANDOM_ONE) {
                                CouponRule couponRule = couponRules.get(0);
                                CouponDetail details = couponActivityProcessService.getCoupon(
                                        couponRule, member.getMemberId(), String.valueOf(couponActivity.getId()), null);
                                batchInsert.add(details);
                                couponRule.setAmount(couponRule.getAmount() - 1);

                                if (couponRule.getAmount() == 0)
                                    couponRules.remove(0);
                            } else {
                                couponRules.stream()
                                        .forEach(couponRule -> {
                                            if (couponRule.getAmount() != 0) {
                                                CouponDetail details = couponActivityProcessService.getCoupon(
                                                        couponRule, member.getMemberId(), String.valueOf(couponActivity.getId()), null);
                                                batchInsert.add(details);
                                                couponRule.setAmount(couponRule.getAmount() - 1);
                                            }
                                        });
                            }
                        }
                );
        batchInsert.removeAll(Collections.singleton(null));
        log.error("-----共{}条数据",batchInsert.size());
        couponDetailMapper.insertCouponDetailBatch(batchInsert);
    }


    /**
     * 直接派发店员
     *
     * @param couponActivity
     * @return
     */
    public boolean sendClerk(CouponActivity couponActivity) {
        List<String> storeList = new ArrayList();//门店
        List<StoreAdmin> clerks = new ArrayList();//店员
        if ("-1".equals(couponActivity.getSendWayValue())) {//全部门店
            clerks = storeAdminMapper.selectAllStoreAdmin(couponActivity.getSiteId());
            storeList=storesMapper.selectAllStoresBySiteId(couponActivity.getSiteId());
        } else { //指定门店
            List<String> storesId = Arrays.asList(couponActivity.getSendWayValue().split(","));
            for (String storeId : storesId) {//遍历所有指定门店获取门店下的店员
                clerks.addAll(storeAdminMapper.selectStoreAdminByStoreId(couponActivity.getSiteId(), Integer.parseInt(storeId)));
            }
            storeList=storesId;

        }
        log.info("发放优惠券给店员的店员数量：" + clerks.size());

        boolean ifg = true;
        try {
            if (clerks != null) {
                List<CouponRuleActivity> ruleByActive = couponRuleActivityMapper.getRuleByActive(couponActivity.getSiteId(),
                        couponActivity.getId());
                for (CouponRuleActivity couponRuleActivity : ruleByActive) {
                    List<CouponClerk> couponClerkList = clerks.stream().map(StoreAdmin::getId)
                            .map(id -> binCouponClerk(couponRuleActivity, couponActivity, id))
                            .collect(toList());
                    log.info("----------couponClerkList----------->" + couponClerkList.toString());
                    couponClerkMapper.addCouponClerkList(couponClerkList);
                }

                //派券提醒
                log.info("开始派发门店优惠券,门店id===>{}",storeList.toString());

                storeList.stream().forEach(item->{
                    notifySendCoupons(couponActivity.getSiteId(),Integer.parseInt(item),ruleByActive.size());
                });
            }
        } catch (Exception e) {
            ifg = false;
            e.printStackTrace();
        }


        // 更新activity状态
        CouponActivity couponActivity1 = couponActivityMapper.getCouponActivity(couponActivity.getSiteId(), couponActivity.getId());
        if (ifg) {
            couponActivity1.setSendStatus(1); // 发放成功
        } else {
            couponActivity1.setSendStatus(2);  // 发放失败
        }

        couponActivityMapper.updateSendStatus(couponActivity1);
        // 冗余代码，其实优惠券数量没有变动，即没有发优惠券，那么该代码其实是冗余的，但是考虑到时间的话，放这里也没错
        couponActivityService.checkStatus(couponActivity.getSiteId(), couponActivity.getId());
        log.debug("activity status is updated");
        return true;
    }

    /**
     * 派券提醒
     * @param siteId
     * @param storeId
     * @return
     * @throws Exception
     */
    public String notifySendCoupons(Integer siteId,Integer storeId,Integer couponNum){
        Map messageMap = new HashMap();
        try {
            messageMap.put("messageType", PushType.NOTIFY_SEND_COUPON.getValue());
            messageMap.put("siteId", String.valueOf(siteId));
            messageMap.put("storeId", String.valueOf(storeId));
            messageMap.put("couponNum", couponNum);
            delayedMessageProduce.delayedMessageProduce(messageMap, String.valueOf(siteId), PushType.NOTIFY_SEND_COUPON.getValue(), null, null);
        }catch (Exception e){
            log.error("发送派券提醒异常,错误信息===>",e);
        }
        return "";
    }


    /**
     * 组装bin
     *
     * @param couponRuleActivity
     * @param couponActivity
     * @param storeId
     * @return
     */
    private CouponClerk binCouponClerk(CouponRuleActivity couponRuleActivity, CouponActivity couponActivity, Integer storeId) {
        CouponClerk couponClerk = new CouponClerk();
        CouponRule couponRule = couponRuleMapper.findCouponRuleById(couponRuleActivity.getRuleId(), couponActivity.getSiteId());
        couponClerk.setRuleId(couponRuleActivity.getRuleId());
        couponClerk.setSiteId(couponActivity.getSiteId());
        couponClerk.setManagerId(storeId.toString());
        couponClerk.setUseCouponNum(couponRule.getAmount());
        couponClerk.setActiveId(couponActivity.getId());
        return couponClerk;
    }

    /**
     * 校验发送条件
     *
     * @param couponActivity
     * @param goodsInfo      [{goodsId：1234，num:2}]
     * @param payAmount      订单价格(单位为分)
     * @return true/false
     */
    public boolean checkSendCondition(CouponActivity couponActivity, List<Map<String, Integer>> goodsInfo, Integer payAmount) {

        List<String> join = new ArrayList<>();
        List<String> nonJoin = new ArrayList<>();
        int total = 0;
        List<String> goodsIds = new ArrayList<>();
        for (Map<String, Integer> item : goodsInfo) {
            total += item.get("num");
            goodsIds.add(String.valueOf(item.get("goodsId")));
        }

        //满足多少元
        if (couponActivity.getSendConditionType() == 1) {
            if (couponActivity.getSendConditionProducts().equals("all")) { // 全部商品
                if (payAmount >= couponActivity.getSendConditionValue()) {
                    return true;
                }

            } else if (couponActivity.getSendConditionProducts().contains("non&&")) { // 指定商品不参加
                String[] nonJoin_ = couponActivity.getSendConditionProducts().replace("non&&", "").split(":");
                nonJoin.addAll(Arrays.asList(nonJoin_));
                nonJoin.retainAll(goodsIds);
                if (nonJoin.size() == 0) { // 所有订单内的商品都不在指定商品范围内，且满足下面的条件，才返回true
                    if (payAmount >= couponActivity.getSendConditionValue()) {
                        return true;
                    }
                }

            } else {
                String[] join_ = couponActivity.getSendConditionProducts().split(":"); // 指定商品参加
                join.addAll(Arrays.asList(join_));
                if (join.containsAll(goodsIds)) { // 所有订单的商品都在指定商品范围内，且满足下面的条件，才返回true
                    if (payAmount >= couponActivity.getSendConditionValue()) {
                        return true;
                    }
                }
            }
        }

        //满足多少件
        else if (couponActivity.getSendConditionType() == 2) {
            if (couponActivity.getSendConditionProducts().equals("all")) { // 全部商品
                if (total >= couponActivity.getSendConditionValue()) {
                    return true;
                }

            } else if (couponActivity.getSendConditionProducts().contains("non&&")) { // 指定商品不参加
                String[] nonJoin_ = couponActivity.getSendConditionProducts().replace("non&&", "").split(":");
                nonJoin.addAll(Arrays.asList(nonJoin_));
                nonJoin.retainAll(goodsIds);

                if (nonJoin.size() == 0) {
                    if (total >= couponActivity.getSendConditionValue()) {
                        return true;
                    }
                }

            } else {
                String[] join_ = couponActivity.getSendConditionProducts().split(":"); // 指定商品参加
                join.addAll(Arrays.asList(join_));
                if (join.containsAll(goodsIds)) {
                    if (total >= couponActivity.getSendConditionValue()) {
                        return true;
                    }
                }
            }
        }

        // 满元且满件
        else if (couponActivity.getSendConditionType() == 3) {
            String[] limits = couponActivity.getSendCondition().split("&&")[0].split(",");
            int moneyStart = Integer.parseInt(limits[0]);
            int numStart = Integer.parseInt(limits[1]);

            if (couponActivity.getSendConditionProducts().equals("all")) { // 全部商品
                if (total >= numStart && payAmount >= moneyStart) {
                    return true;
                }

            } else if (couponActivity.getSendConditionProducts().contains("non&&")) { // 指定商品不参加
                String[] nonJoin_ = couponActivity.getSendConditionProducts().replace("non&&", "").split(":");
                nonJoin.addAll(Arrays.asList(nonJoin_));
                nonJoin.retainAll(goodsIds);
                if (nonJoin.size() == 0) {
                    if (total >= numStart && payAmount >= moneyStart) {
                        return true;
                    }
                }

            } else { // 指定参加商品
                String[] join_ = couponActivity.getSendConditionProducts().split(":"); // 指定参加商品
                join.addAll(Arrays.asList(join_));
                if (join.containsAll(goodsIds)) {
                    if (total >= numStart && payAmount >= moneyStart) {
                        return true;
                    }
                }
            }
        }

        // 不满元也不满件
        else if (couponActivity.getSendConditionType() == 4) {
            return true;
        }
        log.info("首单付款发放----err false----------------");
        return false;
    }

    public List<Map<String, Integer>> getGoodsInfo(String tradesId) throws BusinessLogicException {

        List<Map<String, Integer>> goodsInfo = new ArrayList<>();

        Trades trades = getTradesDetial(Long.parseLong(tradesId));
        if (trades != null) {
            List<Orders> orders = trades.getOrdersList();

            Map<String, Integer> goodsMap = new HashMap<>();
            orders.stream().forEach(order -> {
                goodsMap.put("goodsId", order.getGoodsId());
                goodsMap.put("num", order.getGoodsNum());
            });
            goodsInfo.add(goodsMap);
        }
        return goodsInfo;
    }


    private LimitRule checkLimitRule(String limitRuleStr) {
        LimitRule limitRule = JSON.parseObject(limitRuleStr, LimitRule.class);
        return limitRule;
    }

    private List<CouponRule> getCouponRules(List<Integer> ruleIds, Integer siteId) {
        return ruleIds.stream()
                .map(ruleId -> {
                    CouponRule couponRule = new CouponRule();
                    couponRule.setSiteId(siteId);
                    couponRule.setRuleId(ruleId);
                    return couponRule;
                }).collect(toList());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean syncCouponAmount(Integer ruleId, Integer siteId) {

        CouponRule couponRule = couponRuleMapper.findCouponRuleById(ruleId, siteId);
        if (couponRule != null) {
            if (couponRule.getAmount() == -1) {
                return true;
            }
            if (couponRule.getAmount() > 0) {
                couponRule.setAmount(couponRule.getAmount() - 1);
                couponRule.setRuleId(ruleId);
                couponRule.setSiteId(siteId);
                couponRuleMapper.updateAmountByRuleId(couponRule);
                return true;
            }
        }

        return false;
    }

    public boolean isFirstOrder(Integer siteId, Integer userId) {
        Member member = memberMapper.getMemberByMemberId(siteId, userId);
        return orderService.checkUserFirstOrder(siteId, member.getBuyerId());
    }

    /**
     * 查询订单详情
     *
     * @param tradesId
     * @return
     * @throws BusinessLogicException
     */
    public Trades getTradesDetial(Long tradesId) throws BusinessLogicException {
        Trades trades = tradesMapper.getTradesDetails(tradesId);
        if (trades != null) {
            //获取店员邀请码
            String clerkInvitationCode = "";
            if (trades.getStoreUserId() != null) {
                List<StoreAdminExt> storeAdminExt = storeAdminExtMapper.getStoreAdminExtBySiteIdAndStoreAdminId(trades.getSiteId(), trades.getStoreUserId());
                if (storeAdminExt != null && storeAdminExt.size() != 0) {
                    String clerkInvitationCodeStr = storeAdminExt.get(0).getClerk_invitation_code();
                    if (!StringUtil.isEmpty(clerkInvitationCodeStr)) {
                        int index = clerkInvitationCodeStr.indexOf("_");
                        if (index != -1) {
                            clerkInvitationCode = clerkInvitationCodeStr.substring(index + 1, clerkInvitationCodeStr.length());
                        }
                    }
                }
            }
            trades.setClerkInvitationCode(clerkInvitationCode);

            List<Orders> ordersList = ordersMapper.getOrdersByTradesId(trades.getTradesId());
            if (ordersList != null && ordersList.size() > 0) {
                trades.setOrdersList(ordersList);
                trades.setMap(couponDetailService.findOrderCoupon(String.valueOf(tradesId)));  //优惠券

            } else {
                log.info("未查到与订单号：[{}]相关商品信息", tradesId);
                throw new BusinessLogicException("未查到与订单号：[{}]相关商品信息" + tradesId + "]");
            }
        } else {
            log.info("未查到与订单号：[{}]相关信息", tradesId);
            throw new BusinessLogicException("未查到与订单号：[{}]相关商品信息" + tradesId + "]");
        }
        return trades;
    }

    protected   void insertReissureActivity(List<Member> memberList, CouponActivity couponActivity, ReissureActivity reissureActivity){
        //发放券的种类,send_all 是补发活动下所有的券
        int sendNumTag = ACTIVITY_SEND_ALL;

        try {
            if (couponActivity.getSendRules() != null) {
                if(StringUtils.isNotEmpty(couponActivity.getSendRules())){
                    CouponActivityRulesForJson sendRules = JacksonUtils.json2pojo(couponActivity.getSendRules(), CouponActivityRulesForJson.class);
                    if(sendRules.getSendNumTag()>0)
                    sendNumTag = sendRules.getSendNumTag();
                }

            } else {
                sendNumTag = ACTIVITY_SEND_ALL;
            }
        } catch (Exception e) {
            sendNumTag = ACTIVITY_SEND_ALL;
        }
        List<CouponRule> couponRules = getCouponRules(couponActivity, memberList.size(), sendNumTag);
        binReissureActivityCouponDetail(couponActivity,couponRules,memberList,sendNumTag,reissureActivity);
    }

    /**
     *
     * @param couponActivity 活动
     * @param couponRules 优惠券预创建
     * @param members 会员
     * @param sendNumTag 方法种类，随机发，全部发等
     * @param reissureActivity 补发操作日志
     */
    private void binReissureActivityCouponDetail(CouponActivity couponActivity, List<CouponRule> couponRules, List<Member> members, int sendNumTag,ReissureActivity reissureActivity) {
        List<CouponDetail> batchInsert = new ArrayList<>();

        int totalAmount = 0;

        switch (sendNumTag) {
            case ACTIVITY_SEND_ONLY_ONE:
            case ACTIVITY_TURN_TABLE:
            case ACTIVITY_SEND_ALL:
                for (CouponRule couponRule : couponRules) {
                    int amount = couponRule.getAmount();
                    if (amount == -1) {
                        totalAmount = Integer.MAX_VALUE;
                        break;
                    } else {
                        totalAmount = totalAmount > amount ? totalAmount : amount;
                    }
                }
                break;
            case ACTIVITY_SEND_RANDOM_ONE:
                for (CouponRule couponRule : couponRules) {
                    int amount = couponRule.getAmount();
                    if (amount == -1) {
                        totalAmount = Integer.MAX_VALUE;
                        break;
                    } else {
                        totalAmount += amount;
                    }
                }
                break;
        }

        members.stream()
                .limit(totalAmount)
                .forEach(member -> {
                            if (sendNumTag == ACTIVITY_SEND_RANDOM_ONE) {
                                CouponRule couponRule = couponRules.get(0);
                                CouponDetail details = couponActivityProcessService.getCoupon(
                                        couponRule, member.getMemberId(), String.valueOf(couponActivity.getId()), null);
                                details.setType(1);
                                batchInsert.add(details);
                                couponRule.setAmount(couponRule.getAmount() - 1);

                                if (couponRule.getAmount() == 0)
                                    couponRules.remove(0);
                            } else {
                                couponRules.stream()
                                        .forEach(couponRule -> {
                                            if (couponRule.getAmount() != 0) {
                                                CouponDetail details = couponActivityProcessService.getCoupon(
                                                        couponRule, member.getMemberId(), String.valueOf(couponActivity.getId()), null);
                                                details.setType(1);
                                                batchInsert.add(details);
                                                couponRule.setAmount(couponRule.getAmount() - 1);
                                            }
                                        });
                            }
                        }
                );
        if(members.size()>totalAmount)
            reissureActivity.setSuccessNum(totalAmount);
        else
            reissureActivity.setSuccessNum(members.size());

        couponActivityReissureMapper.updateCouponActivityReissureForSuccessNum(reissureActivity);
        batchInsert.removeAll(Collections.singleton(null));
        couponDetailMapper.insertCouponDetailBatchForReissure(batchInsert);
    }

}
