package com.jk51.modules.grouppurchase.service;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.commons.date.DateFormatConstant;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.encode.Base64Coder;
import com.jk51.commons.java8datetime.ParseAndFormat;
import com.jk51.exception.UnknownTypeException;
import com.jk51.model.Goods;
import com.jk51.model.grouppurchase.GroupPurchase;
import com.jk51.model.order.*;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.activity.PromotionsActivitySqlParam;
import com.jk51.model.promotions.rule.GroupBookingRule;
import com.jk51.modules.coupon.service.CouponActiveForMemberService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.grouppurchase.mapper.GroupPurChaseMapper;
import com.jk51.modules.grouppurchase.request.GroupPurchaseForGoods;
import com.jk51.modules.grouppurchase.request.GroupPurchaseForProActivityParam;
import com.jk51.modules.grouppurchase.request.GroupPurchaseParam;
import com.jk51.modules.grouppurchase.response.GroupInfo;
import com.jk51.modules.grouppurchase.response.GroupPurchaseResponseForBeforeQuery;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.member.service.MemberService;
import com.jk51.modules.order.mapper.DistributeOrderMapper;
import com.jk51.modules.privatesend.core.AliPrivateSend;
import com.jk51.modules.privatesend.core.PrivateSend;
import com.jk51.modules.promotions.constants.GroupBookingConstant;
import com.jk51.modules.promotions.constants.PromotionsConstant;
import com.jk51.modules.promotions.job.StatusTask;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.mapper.PromotionsDetailMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.request.ProRuleMessageParam;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import com.jk51.modules.promotions.utils.OrderDeductionUtils;
import com.jk51.modules.trades.controller.TradesController;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.RefundMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.modules.trades.service.TradesService;
import com.jk51.mq.mns.CloudQueueFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.jk51.commons.function.FunctionUtils.getIfSuccess;
import static com.jk51.commons.java8datetime.ParseAndFormat.longFormatter;
import static com.jk51.modules.grouppurchase.constant.GroupPurchaseConstants.*;

/**
 * Created by mqq on 2017/11/20.
 */
@SuppressWarnings({"Duplicates", "SpringJavaInjectionPointsAutowiringInspection"})
@Service
public class GroupPurChaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private GroupPurChaseMapper groupPurChaseMapper;
    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;
    @Autowired
    private PromotionsRuleService promotionsRuleService;
    @Autowired
    private PromotionsRuleMapper promotionsRuleMapper;
    @Autowired
    private CouponActiveForMemberService couponActiveForMemberService;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private OrderDeductionUtils orderDeductionUtils;
    @Autowired
    private DistributeOrderMapper distributeOrderMapper;
    @Autowired
    TradesService tradesService;
    @Autowired
    private PrivateSend privateSend;
    @Autowired
    private AliPrivateSend aliPrivateSend;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private PromotionsDetailMapper promotionsDetailMapper;
    @Autowired
    private TradesController tradesController;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 支付成功修改 拼团参团数据状态
     *
     * @param tradesId
     * @param siteId
     */
    @Transactional
    public void updateGroupPurchaseStatus(String tradesId, Integer siteId) {
        GroupPurchaseParam groupPurchaseParam = new GroupPurchaseParam();
        groupPurchaseParam.setTradesId(tradesId);
        groupPurchaseParam.setSiteId(siteId);

        GroupPurchase theGroupPurchase = groupPurChaseMapper.getOneGroupPurchase(groupPurchaseParam);

        if (null == theGroupPurchase) return;

        //开团第一人的状态修改
        Goods goods = goodsMapper.getBySiteIdAndGoodsId(theGroupPurchase.getGoodsId(), siteId);
        if (goods == null) return;
        //获取设置的拼团人数
        int groupActivityCount = getAmountForProActivity(theGroupPurchase);
        if (theGroupPurchase.getParentId() == null) {
            if (theGroupPurchase.getStatus().equals(0)) {
                int a = groupPurChaseMapper.updateStatusByIdAndSiteId(theGroupPurchase.getId(), theGroupPurchase.getSiteId(), 1);
                if (a == 1) {
                    for (int i = 0; i < groupActivityCount-1; i++) {
                        stringRedisTemplate.opsForList().leftPush(theGroupPurchase.getSiteId()+"_"+theGroupPurchase.getId()+"_groupActivityCount",UUID.randomUUID().toString().replaceAll("-", "").substring(0,9));
                    }
                    // 创建拼单成功提醒
                    sendGroupPurchaseStartMsgToWechatCustomer(tradesId, siteId, theGroupPurchase, goods);
                }
                logger.info("拼团活动开团成功状态更新:" + "订单:" + theGroupPurchase.getTradesId() + ";拼团主键：" + theGroupPurchase.getId() + ";状态是否修改:" + a);
            } else
                return;
        }

        //参团用户状态修改
        if (theGroupPurchase.getParentId() != null) {
            String theGroupPurchaseId=stringRedisTemplate.opsForList().leftPop(theGroupPurchase.getSiteId()+"_"+theGroupPurchase.getParentId()+"_groupActivityCount");
            //synchronized (new Integer(1)) {
                if (!StringUtils.isEmpty(theGroupPurchaseId)){
                    // 参加拼单成功
                    int b = groupPurChaseMapper.updateStatusByIdAndSiteId(theGroupPurchase.getId(), theGroupPurchase.getSiteId(), 2);
                    if (b == 1) {
                        // 参加拼单成功微信提醒
                        sendGroupPurchaseJoinMsgToWechatCustomer(theGroupPurchase, goods);
                    }

                    logger.info("拼团活动参团成功状态更新:" + "订单:" + theGroupPurchase.getTradesId() + ";拼团主键：" + theGroupPurchase.getId() + ";状态是否修改:" + b);
                    groupPurchaseParam = new GroupPurchaseParam();
                    groupPurchaseParam.setSiteId(theGroupPurchase.getSiteId());
                    groupPurchaseParam.setId(theGroupPurchase.getParentId());
                    GroupPurchase parentGroupPurchase = groupPurChaseMapper.getMainForGroupPurchase(groupPurchaseParam);
                    if (null == parentGroupPurchase)
                        return;

                    int totalCount = groupPurChaseMapper.selectCountForGroupPurchase(groupPurchaseParam) + 1;
                    //Trades trades = tradesService.getTradesByTradesId(Long.parseLong(parentGroupPurchase.getTradesId()));

                    // 成功拼单人数大于等于 并且团长的退款状态不是退款成功
                    //if (totalCount >= groupActivityCount && !Integer.valueOf(120).equals(trades.getIsRefund())) {
                    if (totalCount >= groupActivityCount) {
                        groupPurchaseParam.setStatus(2);
                        int c = groupPurChaseMapper.updateMainGroupPurchaseStatus(groupPurchaseParam);
                        if (c == 1) {
                            try {
                                cancelTradesForNoPayGroupPurchase(groupPurchaseParam, theGroupPurchase.getSiteId(), goods);
                                // 拼单成功通知（向所有成功支付的人通知）
                                logger.info("参团成功发送推送消息开始-------------------------------------------------------");
                                sendGroupPurchaseSuccessMsgToAllGroupMember(parentGroupPurchase, goods);
                            } catch (Exception e) {
                                logger.info("参团成功发送推送消息出现异常");
                            }
                        }
                        logger.info("拼团活动拼团成功状态更新:" + "订单:" + theGroupPurchase.getTradesId() + ";拼团主键：" + theGroupPurchase.getId() + ";状态是否修改:" + c);
                    }
                }
            //}
        }
    }

    /**
     * 发送参加拼单成功提醒
     *
     * @param memberGroupPurchase 团员参加拼单
     * @param goods               商品
     */
    private void sendGroupPurchaseJoinMsgToWechatCustomer(GroupPurchase memberGroupPurchase, Goods goods) {
        try {
            String openId = memberGroupPurchase.getOpenId();
            String aliUserId = memberGroupPurchase.getAliUserId();
            if (StringUtils.isBlank(openId) && StringUtils.isBlank(aliUserId)) return;

            Integer siteId = memberGroupPurchase.getSiteId();

            long tradesId = Long.parseLong(memberGroupPurchase.getTradesId());
            Trades trades = tradesService.getTradesByTradesId(tradesId);

            // 获取订单详情跳转地址
            String url = tradesService.getOrderDUrl(siteId, trades.getPostStyle(), tradesId);

            // 获取拼单价
            String groupPurchasePrice = String.format("%.2f元", trades.getRealPay() / 100f);

            // 获取团员电话
            GroupPurchase parentGroupPurchase = groupPurChaseMapper.findInfo(siteId, memberGroupPurchase.getParentId());
            Member parentMember = memberMapper.getMemberByMemberId(siteId, parentGroupPurchase.getMemberId());
            String phone = parentMember != null ? StringUtils.overlay(parentMember.getMobile(), "****", 3, 7) : "";

            // 获取拼单剩余人数
            GroupBookingRule groupBookingRule = getGroupBookingRule(memberGroupPurchase);
            int absentMemberNumBeforeSuccess = getAbsentMemberNumBeforeSuccess(memberGroupPurchase.getSiteId(),
                memberGroupPurchase.getParentId(),
                memberGroupPurchase.getGoodsId(),
                groupBookingRule);
            String absentGroupMemberNum;
            if (absentMemberNumBeforeSuccess != 0)
                absentGroupMemberNum = String.format("还差%d人", absentMemberNumBeforeSuccess);
            else
                absentGroupMemberNum = "拼团已成功";

            // 获取截止时间
            String groupEndTimeStr = getGroupEndTime(memberGroupPurchase, groupBookingRule);

            privateSend.togetherOrderJoinSuccess(siteId, openId, url,
                String.format(GroupBookingConstant.GROUP_JOIN_SUCCESS_FIRST, phone),
                GroupBookingConstant.GROUP_JOIN_SUCCESS_REMARK,
                goods.getDrugName(), groupPurchasePrice, phone, absentGroupMemberNum, groupEndTimeStr);
            aliPrivateSend.togetherOrderJoinSuccess(siteId, aliUserId, url,
                String.format(GroupBookingConstant.GROUP_JOIN_SUCCESS_FIRST, phone),
                GroupBookingConstant.GROUP_JOIN_SUCCESS_REMARK,
                goods.getDrugName(), groupPurchasePrice, phone, absentGroupMemberNum, groupEndTimeStr);
        } catch (Exception e) {
            logger.error("异常发生, {}", e);
        }
    }

    /**
     * 发送拼单成功提醒
     *
     * @param parentGroupPurchase
     * @param goods
     */
    private void sendGroupPurchaseSuccessMsgToAllGroupMember(GroupPurchase parentGroupPurchase, Goods goods) {
        List<GroupPurchase> groupPurchases = groupPurChaseMapper
            .findGroupPurchasesByParentId(parentGroupPurchase.getId(), parentGroupPurchase.getSiteId())
            .stream()
            .filter(gp -> gp.getStatus() == 2) // 查找所有成功拼单的团员信息
            .collect(Collectors.toList());
        logger.info(parentGroupPurchase+"参团成功发送推送消息--groupPurchases{}-数量：{},",groupPurchases,groupPurchases.size());
        Integer siteId = parentGroupPurchase.getSiteId();

        List<String> memberIds = groupPurchases.stream()
            .map(GroupPurchase::getMemberId)
            .map(Object::toString)
            .collect(Collectors.toList());

        List<Member> members = memberMapper.findMembersByMemberIds(siteId, memberIds);
        List<Map<Integer, String>> phones = members.stream()
            .map(member -> new HashMap<Integer, String>() {{
                put(member.getMemberId(), StringUtils.overlay(member.getMobile(), "****", 3, 7));
            }}).collect(Collectors.toList());

        groupPurchases.stream()
            .forEach(groupPurchase -> {
                String openId = groupPurchase.getOpenId();
                String aliUserId = groupPurchase.getAliUserId();
                if (StringUtils.isBlank(openId) && StringUtils.isBlank(aliUserId)) return;

                long tradesId = Long.parseLong(groupPurchase.getTradesId());
                Trades trades = tradesService.getTradesByTradesId(tradesId);

                // 到这里如果还有未付款的，那说明哪里出现了bug
                // 检测是否退款
                if (Integer.valueOf(0).equals(trades.getIsRefund()) || Integer.valueOf(110).equals(trades.getIsRefund())) {
                    String url = tradesService.getOrderDUrl(siteId, trades.getPostStyle(), tradesId);

                    // 最多显示除自己外的10个团员的信息
                    String otherMemberPhone = phones.stream()
                        .filter(map -> map.get(groupPurchase.getMemberId()) == null)
                        .flatMap(map -> map.values().stream())
                        .distinct()
                        .limit(3)
                        .reduce("", (s, s2) -> s += "," + s2).substring(1);

                    if (phones.size() > 4)
                        otherMemberPhone += "等";
                    logger.info("推送消息，微信openId{},", openId);
                    privateSend.togetherOrderCreateSuccessNotice(siteId, openId, url,
                        GroupBookingConstant.GROUP_SUCCESS_FIRST,
                        GroupBookingConstant.GROUP_SUCCESS_REMARK,
                        goods.getDrugName(), otherMemberPhone,
                        GroupBookingConstant.GROUP_SUCCESS_KEYWORD_3);
                    logger.info("推送消息，支付宝aliUserId{},", aliUserId);
                    aliPrivateSend.togetherOrderCreateSuccessNotice(siteId, aliUserId, url,
                        GroupBookingConstant.GROUP_SUCCESS_FIRST,
                        GroupBookingConstant.GROUP_SUCCESS_REMARK,
                        goods.getDrugName(), otherMemberPhone,
                        GroupBookingConstant.GROUP_SUCCESS_KEYWORD_3);

                }
            });
    }

    /**
     * 发送创建拼单成功提醒
     *
     * @param tradesId
     * @param siteId
     * @param parentGroupPurchase 团长的团信息
     * @param goods
     */
    private void sendGroupPurchaseStartMsgToWechatCustomer(String tradesId, Integer siteId, GroupPurchase parentGroupPurchase, Goods goods) {
        try {
            String openId = parentGroupPurchase.getOpenId();
            String aliUserId = parentGroupPurchase.getAliUserId();
            long tradesIdTypeLong = Long.parseLong(tradesId);
            Trades trades = tradesService.getTradesByTradesId(tradesIdTypeLong);
            String url = tradesService.getOrderDUrl(siteId, trades.getPostStyle(), tradesIdTypeLong);

            String groupPurchasePrice = String.format("%.2f元", trades.getRealPay() / 100f);
            String phone = getPhoneByBuyerId(siteId, trades.getBuyerId());

            // 获取拼单剩余人数
            GroupBookingRule groupBookingRule = getGroupBookingRule(parentGroupPurchase);
            String absentGroupMemberNum = String.format("还差%d人",
                    getAbsentMemberNumBeforeSuccess(parentGroupPurchase.getSiteId(),
                            parentGroupPurchase.getId(),
                            parentGroupPurchase.getGoodsId(),
                            groupBookingRule));

            // 获取截止时间
            String groupEndTimeStr = getGroupEndTime(parentGroupPurchase, groupBookingRule);
            logger.info(parentGroupPurchase+"开团成功推送消息，微信openId{},", openId);
            // 发送开团通知
            if (!StringUtils.isBlank(openId)){
                privateSend.togetherOrderCreateSuccess(siteId, openId, url,
                        GroupBookingConstant.GROUP_START_SUCCESS_FIRST,
                        GroupBookingConstant.GROUP_START_SUCCESS_REMARK,
                        goods.getDrugName(), groupPurchasePrice, phone, absentGroupMemberNum, groupEndTimeStr);
            }
            if (! StringUtils.isBlank(aliUserId)){
                logger.info("开团成功推送消息，支付宝aliUserId{},", aliUserId);
                aliPrivateSend.togetherOrderCreateSuccess(siteId, aliUserId, url,
                        GroupBookingConstant.GROUP_START_SUCCESS_FIRST,
                        GroupBookingConstant.GROUP_START_SUCCESS_REMARK,
                        goods.getDrugName(), groupPurchasePrice, phone, absentGroupMemberNum, groupEndTimeStr);
            }

            logger.info("开团成功推送消息结束--------------------------------------------------------------");
        }catch (Exception e){
            logger.info("开团成功推送消息结束Exception"+e.getMessage());
        }

    }

    /**
     * 根据拼团信息获取拼团规则
     *
     * @param parentGroupPurchase
     * @return
     */
    private GroupBookingRule getGroupBookingRule(GroupPurchase parentGroupPurchase) {
        PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleBySiteIdAndActivityId(parentGroupPurchase.getSiteId(),
            parentGroupPurchase.getProActivityId());
        if (!Integer.valueOf(60).equals(promotionsRule.getPromotionsType())) {
            throw new RuntimeException("数据异常");
        }
        return JSON.parseObject(promotionsRule.getPromotionsRule(), GroupBookingRule.class);
    }

    /**
     * 获取截止时间
     *
     * @param groupPurchase
     * @param groupBookingRule
     * @return
     */
    private String getGroupEndTime(GroupPurchase groupPurchase, GroupBookingRule groupBookingRule) {
        LocalDateTime groupBeginTime = groupPurchase.getGroupbeginTime();
        Duration duration = Duration.ofHours(groupBookingRule.getGroupLiveTime());
        LocalDateTime groupEndTime = groupBeginTime.plus(duration);
        return groupEndTime.format(ParseAndFormat.dateTimeFormatter_1);
    }


    /**
     * 通过buyerId获取会员手机号
     * 结果为 135****4324
     *
     * @param siteId
     * @param buyerId
     * @return
     */
    private String getPhoneByBuyerId(Integer siteId, Integer buyerId) {
        Member member = memberService.selectById(siteId, buyerId);
        String phone = member != null ? StringUtils.overlay(member.getMobile(), "****", 3, 7) : "";
        return phone;
    }

    @Transactional
    void cancelTradesForNoPayGroupPurchase(GroupPurchaseParam groupPurchaseParam, Integer siteId, Goods goods) {
        List<GroupPurchase> childRenNoPayList = groupPurChaseMapper.childRenNoPayList(groupPurchaseParam);

        childRenNoPayList.stream()
            .filter(groupPurchase -> groupPurchase.getStatus() == 0)
            .forEach(childNoPay -> {
                groupPurChaseMapper.updateStatusByIdAndSiteId(childNoPay.getId(), siteId, 4);
                Trades trades = tradesService.getTradesByTradesId(Long.parseLong(childNoPay.getTradesId()));
                try {
                    tradesController.closeTrades(trades.getTradesId(), 2, 9999);
                    // 未付款拼单取消通知
                    if (childNoPay.getStatus() == 0 && StringUtils.isNotBlank(childNoPay.getOpenId())) {
                        sendGroupPurchaseCancelMsgToWechatCustomer(childNoPay.getSiteId(), childNoPay.getOpenId(),childNoPay.getAliUserId(), trades, goods, childNoPay.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("主动取消订单状态错误");
                }

            });

    }

    public void sendGroupPurchaseCancelMsgToWechatCustomer(Integer siteId, String openId, String aliUserId, Trades trades, Goods goods, Integer groupPurchaseId) {
        if (StringUtils.isBlank(openId) && StringUtils.isBlank(aliUserId)) return;

        String url = tradesService.getOrderDUrl(siteId, trades.getPostStyle(), trades.getTradesId());
        GroupPurchase groupPurchase = groupPurChaseMapper.findInfo(siteId, groupPurchaseId);
        Member member = memberMapper.getMemberByMemberId(siteId, groupPurchase.getMemberId());
        String memberPhone = StringUtils.overlay(member.getMobile(), "****", 3, 7);
        String payMoney = String.format("%.2f元", trades.getRealPay() / 100f);

        privateSend.togetherOrderCancel(siteId, openId, url,
            GroupBookingConstant.GROUP_CANCEL_FIRST,
            GroupBookingConstant.GROUP_CANCEL_REMARK,
            memberPhone, trades.getTradesId().toString(), goods.getDrugName(), payMoney);

        aliPrivateSend.togetherOrderCancel(siteId, aliUserId, url,
            GroupBookingConstant.GROUP_CANCEL_FIRST,
            GroupBookingConstant.GROUP_CANCEL_REMARK,
            memberPhone, trades.getTradesId().toString(), goods.getDrugName(), payMoney);
    }


    /**
     * 获取拼团数据
     *
     * @param groupPurchaseForGoods
     * @return
     */
    @Nullable
    public Map<String, Object> getGroupPurchaseDataForGoods(GroupPurchaseForGoods groupPurchaseForGoods) {
        String regex = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern pattern = Pattern.compile(regex);

        try {
            GroupPurchaseForProActivityParam groupPurchaseForProActivityParam;
            Map<String, Object> responseMap;
            //a 如果只传入一个商品id,去查询商品的拼团第一个活动
            if (null == groupPurchaseForGoods.getGroupPurchaseId() && null == groupPurchaseForGoods.getGetGroupPurchaseForActivityId()) {
                groupPurchaseForProActivityParam = new GroupPurchaseForProActivityParam();
                groupPurchaseForProActivityParam.setSiteId(groupPurchaseForGoods.getSiteId());
                List<Map<String, Object>> resultMap = promotionsActivityMapper.getGroupPurchaseProActivity(groupPurchaseForProActivityParam);
                resultMap = checkGroupPurchaseProActivity(resultMap, groupPurchaseForGoods);
                if (CollectionUtils.isEmpty(resultMap))
                    return null;
                responseMap = new HashMap<>();
                responseMap.put("isHaveGroupPurchase", 0);
                String promotion_rule = resultMap.get(0).get("promotions_rule").toString();
                GroupBookingRule groupBookingRule = JSON.parseObject(promotion_rule, GroupBookingRule.class);

                responseMap = resultMapForGroupPurchase(responseMap, groupPurchaseForGoods, groupBookingRule, resultMap.get(0));
                if (responseMap == null)
                    return null;
                responseMap.put("proActivityMap", resultMap.get(0));

                GroupPurchaseParam groupPurchaseParam = new GroupPurchaseParam();
                groupPurchaseParam.setSiteId(groupPurchaseForGoods.getSiteId());
                groupPurchaseParam.setGoodsId(groupPurchaseForGoods.getGoodsId());
                groupPurchaseParam.setProActivityId(Integer.parseInt(resultMap.get(0).get("proActivityId").toString()));

                responseMap.put("totalJoinNum", groupPurChaseMapper.selectCountForJoinTheGroupPurchase(groupPurchaseParam));
                groupPurchaseParam.setStatus(1);
                List<GroupPurchase> mainGroupPurchaseList = groupPurChaseMapper.mainGroupPurchaseList(groupPurchaseParam).stream()
                    .peek(groupPurchase -> {
                        String buyerNick = groupPurchase.getBuyerNick();

                        if (StringUtils.isNotBlank(buyerNick) && pattern.matcher(buyerNick).matches()) {
                            try {
                                groupPurchase.setBuyerNick(Base64Coder.decode(buyerNick));
                            } catch (UnsupportedEncodingException e) {
                                logger.error("异常发送，{}", e);
                            }
                        }
                    }).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(mainGroupPurchaseList)) {
                    mainGroupPurchaseList = getChildrenListFromParent(mainGroupPurchaseList, groupPurchaseParam, groupBookingRule);
                    mainGroupPurchaseList.sort(new GroupPurchaseComparatorDesc());
                    mainGroupPurchaseList = (mainGroupPurchaseList.size() >= 5) ? mainGroupPurchaseList.subList(0, 5) : mainGroupPurchaseList;
                    responseMap.put("mainGroupPurchaseList", (mainGroupPurchaseList.size() >= 5) ? mainGroupPurchaseList.subList(0, 5) : mainGroupPurchaseList);
                } else {
                    responseMap.put("mainGroupPurchaseList", null);
                }

                return responseMap;
            }

            //b 如果传入拼团信息，则是用户参团，或是从用户分享出去的链接进入的
            if (null != groupPurchaseForGoods.getGroupPurchaseId() && null != groupPurchaseForGoods.getGetGroupPurchaseForActivityId()) {
                groupPurchaseForProActivityParam = new GroupPurchaseForProActivityParam();
                groupPurchaseForProActivityParam.setSiteId(groupPurchaseForGoods.getSiteId());
                groupPurchaseForProActivityParam.setProActivityId(groupPurchaseForGoods.getGetGroupPurchaseForActivityId());
                Map<String, Object> oneGroupPro = promotionsActivityMapper.getGroupPurchaseOneProActivity(groupPurchaseForProActivityParam);
                //如果对应活动已过期直接返回null
                if (null == oneGroupPro || oneGroupPro.get("proActivityStatus") == null
                    || oneGroupPro.get("status") == null
                    || Integer.parseInt(oneGroupPro.get("proActivityStatus").toString()) != 0
                    || Integer.parseInt(oneGroupPro.get("status").toString()) != 0)
                    return null;

                List<Map<String, Object>> resultMap = new ArrayList<>();
                resultMap.add(oneGroupPro);
                resultMap = checkGroupPurchaseProActivity(resultMap, groupPurchaseForGoods);

                if (CollectionUtils.isEmpty(resultMap))
                    return null;

                responseMap = new HashMap<>();
                String promotion_rule = resultMap.get(0).get("promotions_rule").toString();
                GroupBookingRule groupBookingRule = JSON.parseObject(promotion_rule, GroupBookingRule.class);

                responseMap = resultMapForGroupPurchase(responseMap, groupPurchaseForGoods, groupBookingRule, resultMap.get(0));
                if (responseMap == null)
                    return null;
                responseMap.put("proActivityMap", resultMap.get(0));

                GroupPurchaseParam groupPurchaseParam = new GroupPurchaseParam();
                groupPurchaseParam.setSiteId(groupPurchaseForGoods.getSiteId());
                groupPurchaseParam.setGoodsId(groupPurchaseForGoods.getGoodsId());
                groupPurchaseParam.setProActivityId(Integer.parseInt(resultMap.get(0).get("proActivityId").toString()));
                groupPurchaseParam.setId(groupPurchaseForGoods.getGroupPurchaseId());

                responseMap.put("totalJoinNum", groupPurChaseMapper.selectCountForJoinTheGroupPurchase(groupPurchaseParam));
                List<GroupPurchase> mainGroupPurchaseList = groupPurChaseMapper.mainGroupPurchaseList(groupPurchaseParam)
                    .stream()
                    .peek(groupPurchase -> {
                        String buyerNick = groupPurchase.getBuyerNick();
                        if (StringUtils.isNotBlank(buyerNick) && pattern.matcher(buyerNick).matches()) {
                            try {
                                groupPurchase.setBuyerNick(Base64Coder.decode(buyerNick));
                            } catch (UnsupportedEncodingException e) {
                                logger.error("异常发送，{}", e);
                            }
                        }
                    }).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(mainGroupPurchaseList)) {
                    GroupPurchase mainGroupPurchase = mainGroupPurchaseList.get(0);


                    if (mainGroupPurchase.getStatus() == 0 || mainGroupPurchase.getStatus() == 3 || mainGroupPurchase.getStatus() == 4)
                        return null;

                    if (mainGroupPurchase.getStatus() == 1)
                        responseMap.put("isHaveGroupPurchase", 0);

                    if (mainGroupPurchase.getStatus() == 2)
                        responseMap.put("isHaveGroupPurchase", 2);

                    mainGroupPurchaseList = getChildrenListFromParent(mainGroupPurchaseList, groupPurchaseParam, groupBookingRule);
                    if (CollectionUtils.isNotEmpty(mainGroupPurchaseList)) {
                        mainGroupPurchaseList.sort(new GroupPurchaseComparatorDesc());
                        mainGroupPurchaseList = (mainGroupPurchaseList.size() >= 5) ? mainGroupPurchaseList.subList(0, 5) : mainGroupPurchaseList;
                    }
                }
                responseMap.put("mainGroupPurchaseList", mainGroupPurchaseList);
                return responseMap;
            }
        } catch (Exception e) {
            logger.error("商品获取拼团活动拼团活动数据出现异常，参数:{}, 异常:{}", groupPurchaseForGoods, e);
            return null;
        }

        return null;
    }

    /**
     * 发送主题为{@link GroupBookingConstant#MQ_TOPIC_NAME}的消息队列
     *
     * @param groupPurchases
     * @param parentGroupPurchaseId
     */
    public void batchSendGroupBookingFailOperationMQMsg(List<GroupPurchase> groupPurchases, Integer parentGroupPurchaseId) {
        logger.info("团购开团失败操作开始{}", groupPurchases);

        List<Message> messages = groupPurchases.stream()
            .map(groupPurchase -> {
                Integer siteId = groupPurchase.getSiteId();
                Member member = memberMapper.getMemberByMemberId(siteId, groupPurchase.getMemberId());

                Map<String, Object> map = new HashMap<>();
                map.put("groupPurchaseId", groupPurchase.getId());
                map.put("siteId", siteId);
                map.put("tradesId", groupPurchase.getTradesId());
                map.put("openId", member.getOpenId());
                map.put("aliUserId", member.getAliUserId());
                map.put("goodsId", groupPurchase.getGoodsId());
                Message message = new Message();
                message.setMessageBody(JSON.toJSONString(map).getBytes());
                return message;
            }).collect(Collectors.toList());

        CloudQueue queue = CloudQueueFactory.create(GroupBookingConstant.MQ_TOPIC_NAME);

        try {
            queue.batchPutMessage(messages);
            logger.info("parentGroupPurchaseId{} 加入消息队列成功! queueName:{}", parentGroupPurchaseId, GroupBookingConstant.MQ_TOPIC_NAME);
        } catch (Exception e) {
            logger.error("parentGroupPurchaseId{} 团队批发送到消息队列失败 error:{}", parentGroupPurchaseId, e.getMessage());
        }
    }

    private List<Map<String, Object>> checkGroupPurchaseProActivity(List<Map<String, Object>> paramList, GroupPurchaseForGoods groupPurchaseForGoods) {
        try {
            paramList = paramList.stream()
                .filter(map -> promotionsRuleService.checkproRuleTimeRule((String) map.get("time_rule")))
                .filter(map -> couponActiveForMemberService
                    .checkProActivity(groupPurchaseForGoods.getSiteId(), Integer.parseInt(map.get("proActivityId").toString()), groupPurchaseForGoods.getMemberId()))
                .collect(Collectors.toList());

            paramList = paramList.stream().filter(map -> checkGroupPurchaseProActivityUseRule(map, groupPurchaseForGoods)).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(paramList))
                return null;

            return paramList;

        } catch (Exception e) {
            logger.info("商品获取拼团活动校验过程出现异常");
            return null;
        }

    }

    private boolean checkGroupPurchaseProActivityUseRule(Map<String, Object> map, GroupPurchaseForGoods groupPurchaseForGoods) {
        try {
            String promotion_rule = map.get("promotions_rule").toString();
            GroupBookingRule groupBookingRule = JSON.parseObject(promotion_rule, GroupBookingRule.class);

            if (groupBookingRule.getGoodsIdsType() == 0)
                return true;

            else if (groupBookingRule.getGoodsIdsType() == 1)
                return new HashSet<>(Arrays.asList(groupBookingRule.getGoodsIds().split(","))).contains(groupPurchaseForGoods.getGoodsId().toString());

            else if (groupBookingRule.getGoodsIdsType() == 2)
                return !new HashSet<>(Arrays.asList(groupBookingRule.getGoodsIds().split(","))).contains(groupPurchaseForGoods.getGoodsId().toString());


            return false;

        } catch (Exception e) {
            logger.info("商品获取拼团活动校验过程出现异常");
            return false;
        }

    }

    private List<GroupPurchase> getChildrenListFromParent(List<GroupPurchase> mainGroupPurchaseList, GroupPurchaseParam groupPurchaseParam, GroupBookingRule groupBookingRule) {
        List<GroupPurchase> groupPurchasesList = new ArrayList<>();
        mainGroupPurchaseList.forEach(groupPurchase -> {
            Integer groupLiveTime = groupBookingRule.getGroupLiveTime();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime groupbeginTime = groupPurchase.getGroupbeginTime();
            LocalDateTime aftergroupLiveTime = groupbeginTime.minusHours(0 - groupLiveTime);
            Duration duration = Duration.between(now, aftergroupLiveTime);
            groupPurchase.setLastTime(duration.toMillis());
            groupPurchase.setAftergroupLiveTime(aftergroupLiveTime.toString().replace("T", " "));
            groupPurchaseParam.setStatus(2);
            groupPurchaseParam.setParentId(groupPurchase.getId());
            groupPurchaseParam.setId(null);
            List<GroupPurchase> childrenGroupPurchase = groupPurChaseMapper.childrenFrommainGroupPurchaseList(groupPurchaseParam);
            groupPurchase.setChildrenList(childrenGroupPurchase);
            if (groupPurchase.getStatus() == 2 || duration.toMillis() > 0)
                groupPurchasesList.add(groupPurchase);

        });

        return groupPurchasesList;
    }

    /**
     * 获取拼团中其他团员的订单信息
     *
     * @param siteId
     * @param tradesId
     * @return
     */
    public Optional<Pair<String, List<Map<String, String>>>> getOtherOrdersAndStatusInGroup(@Nonnull Integer siteId, @Nonnull Long tradesId) {
        try {
            GroupPurchase groupPurchase = groupPurChaseMapper.findByTradesId(siteId, tradesId.toString());
            if (groupPurchase == null) return Optional.empty();

            Integer parentId;
            if (groupPurchase.getParentId() == null) parentId = groupPurchase.getId();
            else parentId = groupPurchase.getParentId();

            List<GroupPurchase> groupPurchases = groupPurChaseMapper.findGroupPurchasesByParentId(parentId, siteId);
            // 订单号
            List<Map<String, String>> resultList = groupPurchases.stream()
//                .filter(gp -> !gp.getId().equals(groupPurchase.getId()))
                .map(this::gatherResult)
                .collect(Collectors.toList());

            Pair<String, List<Map<String, String>>> result = new MutablePair<>((groupPurchase.getParentId() == null ? groupPurchase.getId() : groupPurchase.getParentId()) + "", resultList);

            return Optional.of(result);
        } catch (Exception e) {
            logger.error("出现异常, {}", e);
            return Optional.empty();
        }

    }

    /**
     * 收集结果
     *
     * @param gp
     * @return
     */
    private Map<String, String> gatherResult(GroupPurchase gp) {
        Map<String, String> map = new HashMap<>();

        Long tradesIdLong = Long.parseLong(gp.getTradesId());
        map.put("tradesId", tradesIdLong.toString());

        // 下单时间
        Trades trades = tradesMapper.getTradesByTradesId(tradesIdLong);
        map.put("tradesOrderTime", DateUtils.formatDate(new Date(trades.getCreateTime().getTime()), DateFormatConstant.LONG_DATETIME_FORMAT));

        // 会员
        String memberType;
        if (gp.getParentId() == null) memberType = "团长";
        else memberType = "团员";
        Member member = memberMapper.getMemberByMemberId(gp.getSiteId(), gp.getMemberId());
        if (member != null) {
            String nickName = member.getBuyerNick();
            if (nickName == null) {
                nickName = StringUtils.overlay(member.getMobile(), "****", 3, 7);
            }
            map.put("member", nickName + "(" + memberType + ")");
        }

        // 状态
        map.put("status", gp.getStatus().toString());

        return map;
    }


    private Map<String, Object> resultMapForGroupPurchase(Map<String, Object> responseMap, GroupPurchaseForGoods groupPurchaseForGoods, GroupBookingRule groupBookingRule, Map<String, Object> groupActivity) {
        try {

            int hasbuyNum = promotionsDetailMapper.getUseBuyedGoodsNum(groupPurchaseForGoods.getSiteId(), groupPurchaseForGoods.getGoodsId(), groupPurchaseForGoods.getUserId(), Integer.parseInt(groupActivity.get("proActivityId").toString()), Integer.parseInt(groupActivity.get("id").toString()));

            Goods goods = goodsMapper.getBySiteIdAndGoodsId(groupPurchaseForGoods.getGoodsId(), groupPurchaseForGoods.getSiteId());
            responseMap.put("goodsInfo", goods);
            List<Map<String, Integer>> rules = groupBookingRule.getRules();
            Integer goodsPrice = goods.getShopPrice();
            switch (groupBookingRule.getRuleType()) {

                case 1:
                    responseMap.put("groupPrice", rules.get(0).get("groupPrice"));
                    responseMap.put("groupMemberNum", rules.get(0).get("groupMemberNum"));
                    responseMap.put("goodsLimitNum", (rules.get(0).get("goodsLimitNum") - hasbuyNum) < 0 ? 0 : (rules.get(0).get("goodsLimitNum") - hasbuyNum));
                    responseMap.put("reducePrice", goodsPrice - rules.get(0).get("groupPrice"));
                    break;
                case 2:
                    rules = groupBookingRule.getRules().stream().filter(stringIntegerMap -> stringIntegerMap.get("goodsId").equals(groupPurchaseForGoods.getGoodsId())).collect(Collectors.toList());
                    responseMap.put("groupPrice", rules.get(0).get("groupPrice"));
                    responseMap.put("groupMemberNum", rules.get(0).get("groupMemberNum"));
                    responseMap.put("goodsLimitNum", (rules.get(0).get("goodsLimitNum") - hasbuyNum) < 0 ? 0 : (rules.get(0).get("goodsLimitNum") - hasbuyNum));
                    responseMap.put("reducePrice", goodsPrice - rules.get(0).get("groupPrice"));
                    break;
                case 3:
                    Integer is_ml = groupBookingRule.getIsMl();
                    Integer is_round = groupBookingRule.getIsRound();

                    Integer discount = rules.get(0).get("groupDiscount");
                    Integer maxReduce = rules.get(0).get("maxReduce");
                    Integer reducePrice = orderDeductionUtils.discountMoney(goodsPrice, is_ml, is_round, discount);

                    if (maxReduce > 0 && maxReduce < reducePrice)
                        reducePrice = maxReduce;
                    responseMap.put("groupPrice", goodsPrice - reducePrice);
                    responseMap.put("groupMemberNum", rules.get(0).get("groupMemberNum"));
                    responseMap.put("goodsLimitNum", (rules.get(0).get("goodsLimitNum") - hasbuyNum) < 0 ? 0 : (rules.get(0).get("goodsLimitNum") - hasbuyNum));
                    responseMap.put("reducePrice", reducePrice);
                    break;
                case 4:
                    rules = groupBookingRule.getRules().stream().filter(stringIntegerMap -> stringIntegerMap.get("goodsId").equals(groupPurchaseForGoods.getGoodsId())).collect(Collectors.toList());
                    Integer is_mls = groupBookingRule.getIsMl();
                    Integer is_rounds = groupBookingRule.getIsRound();
                    Integer discounts = rules.get(0).get("groupDiscount");
                    Integer maxReduces = rules.get(0).get("maxReduce");
                    Integer reducePrices = orderDeductionUtils.discountMoney(goodsPrice, is_mls, is_rounds, discounts);

                    if (maxReduces > 0 && maxReduces < reducePrices)
                        reducePrices = maxReduces;
                    responseMap.put("groupPrice", goodsPrice - reducePrices);
                    responseMap.put("groupMemberNum", rules.get(0).get("groupMemberNum"));
                    responseMap.put("goodsLimitNum", (rules.get(0).get("goodsLimitNum") - hasbuyNum) < 0 ? 0 : (rules.get(0).get("goodsLimitNum") - hasbuyNum));
                    responseMap.put("reducePrice", reducePrices);

                    break;
            }
        } catch (Exception e) {
            logger.info("拼团活动数据出现异常:");
            return null;
        }
        return responseMap;
    }

    private int getAmountForProActivity(GroupPurchase groupPurchase) {
        GroupPurchaseForProActivityParam groupPurchaseForProActivityParam = new GroupPurchaseForProActivityParam();
        groupPurchaseForProActivityParam.setSiteId(groupPurchase.getSiteId());
        groupPurchaseForProActivityParam.setProActivityId(groupPurchase.getProActivityId());
        Map<String, Object> oneGroupPro = promotionsActivityMapper.getGroupPurchaseOneProActivity(groupPurchaseForProActivityParam);

        String promotion_rule = oneGroupPro.get("promotions_rule").toString();
        GroupBookingRule groupBookingRule = JSON.parseObject(promotion_rule, GroupBookingRule.class);
        List<Map<String, Integer>> rules = groupBookingRule.getRules();
        switch (groupBookingRule.getRuleType()) {

            case 1:
            case 3:
                return rules.get(0).get("groupMemberNum");
            case 2:
            case 4:
                rules = groupBookingRule.getRules().stream().filter(stringIntegerMap -> stringIntegerMap.get("goodsId").equals(groupPurchase.getGoodsId())).collect(Collectors.toList());
                return rules.get(0).get("groupMemberNum");
        }
        return 0;
    }


    /**
     * 预下单下单验证拼团信息
     *
     * @param groupPurchase
     * @param req
     * @return
     */
    public GroupPurchaseResponseForBeforeQuery getDataForGroupPurchaseResponseForBeforeQuery(GroupPurchase groupPurchase, BeforeCreateOrderReq req) {
        GroupPurchaseResponseForBeforeQuery resultGroupPurchase = new GroupPurchaseResponseForBeforeQuery();
        //先验证数据信息
        if (null == groupPurchase.getProActivityId() || null == groupPurchase.getGoodsId()) {
            resultGroupPurchase.setResultStatus(-1);
            return resultGroupPurchase;
        }
        GroupPurchaseForProActivityParam groupPurchaseForProActivityParam = new GroupPurchaseForProActivityParam();
        groupPurchaseForProActivityParam.setSiteId(req.getSiteId());
        groupPurchaseForProActivityParam.setProActivityId(groupPurchase.getProActivityId());
        Map<String, Object> oneGroupPro = promotionsActivityMapper.getGroupPurchaseOneProActivity(groupPurchaseForProActivityParam);

        //活动状态校验
        if (null == oneGroupPro || oneGroupPro.get("proActivityStatus") == null
            || oneGroupPro.get("status") == null
            || Integer.parseInt(oneGroupPro.get("proActivityStatus").toString()) != 0
            || Integer.parseInt(oneGroupPro.get("status").toString()) != 0) {
            resultGroupPurchase.setResultStatus(-1);
            return resultGroupPurchase;

        }
        GroupPurchaseForGoods groupPurchaseForGoods = new GroupPurchaseForGoods();
        groupPurchaseForGoods.setSiteId(req.getSiteId());
        groupPurchaseForGoods.setGoodsId(groupPurchase.getGoodsId());
        groupPurchaseForGoods.setMemberId(req.getUserId());

        ProRuleMessageParam proRuleMessageParam = new ProRuleMessageParam(req, 0, 0);
        List<Map<String, Object>> resultMap = new ArrayList<>();
        resultMap.add(oneGroupPro);


        resultMap = resultMap.stream()
            .filter(map -> promotionsRuleService.checkproRuleFirstOrderTypeStore(proRuleMessageParam, map))
            .collect(Collectors.toList());

        //首单门店信息等校验
        if (CollectionUtils.isEmpty(resultMap)) {
            resultGroupPurchase.setResultStatus(-1);
            return resultGroupPurchase;
        }


        //做开团的
        if (null == groupPurchase.getId() && null != groupPurchase.getProActivityId() && null != groupPurchase.getGoodsId()) {
            //a 开团
            List<Integer> goodIds = new ArrayList<>();
            goodIds.add(groupPurchase.getGoodsId());
            List<GoodsInfo> goodsInfoList = distributeOrderMapper.getGoodsInfoByGoodIds(proRuleMessageParam.getSiteId(), goodIds);
            goodsInfoList.forEach(goodsInfo -> {
                goodsInfo.setDiscountPrice(getGroupPuechasePrice(proRuleMessageParam.getSiteId(), groupPurchase.getProActivityId(), groupPurchase.getGoodsId()));
                goodsInfo.setErpPrice(-1);
            });
            resultGroupPurchase.setListGoodsInfo(goodsInfoList);
            return resultGroupPurchase;
        } else if (null != groupPurchase.getId() && null != groupPurchase.getProActivityId() && null != groupPurchase.getGoodsId()) {
            //b 拼团
            GroupPurchaseParam groupPurchaseParam = new GroupPurchaseParam();
            groupPurchaseParam.setSiteId(proRuleMessageParam.getSiteId());
            groupPurchaseParam.setGoodsId(groupPurchase.getGoodsId());
            groupPurchaseParam.setProActivityId(groupPurchase.getProActivityId());
            groupPurchaseParam.setId(groupPurchase.getId());
            GroupPurchase mainGroupPurchas = CollectionUtils.isEmpty(groupPurChaseMapper.mainGroupPurchaseList(groupPurchaseParam)) ? null : groupPurChaseMapper.mainGroupPurchaseList(groupPurchaseParam).get(0);

            //拼团校验拼团状态
            if (mainGroupPurchas == null || mainGroupPurchas.getStatus() != 1) {
                resultGroupPurchase.setResultStatus(-1);
                resultGroupPurchase.setGroupPurchasestatus(-1);
                return resultGroupPurchase;
            }

            List<Integer> goodIds = new ArrayList<>();
            goodIds.add(groupPurchase.getGoodsId());
            List<GoodsInfo> goodsInfoList = distributeOrderMapper.getGoodsInfoByGoodIds(proRuleMessageParam.getSiteId(), goodIds);
            goodsInfoList.forEach(goodsInfo -> {
                goodsInfo.setDiscountPrice(getGroupPuechasePrice(proRuleMessageParam.getSiteId(), groupPurchase.getProActivityId(), groupPurchase.getGoodsId()));
                goodsInfo.setErpPrice(-1);
            });
            resultGroupPurchase.setListGoodsInfo(goodsInfoList);
            return resultGroupPurchase;
        }

        return null;
    }


    /**
     * 获取开团成功所需的剩余人数，即该团还差几人就能开团
     *
     * @param parentGroupPurchase 团长的团信息
     * @return
     */
    public int getAbsentMemberNumBeforeSuccess(GroupPurchase parentGroupPurchase) {
        PromotionsRule promotionsRule = promotionsRuleMapper
            .getPromotionsRuleBySiteIdAndActivityId(parentGroupPurchase.getSiteId(), parentGroupPurchase.getProActivityId());

        if (Integer.valueOf(60).equals(promotionsRule.getPromotionsType())) {
            GroupBookingRule groupBookingRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GroupBookingRule.class);

            return getAbsentMemberNumBeforeSuccess(parentGroupPurchase.getSiteId(),
                parentGroupPurchase.getId(),
                parentGroupPurchase.getGoodsId(),
                groupBookingRule);
        } else
            throw new RuntimeException("数据异常");
    }

    /**
     * 获取开团成功所需的剩余人数，即该团还差几人就能开团
     *
     * @param siteId
     * @param groupPurchaseParentId 团长id
     * @param goodsId
     * @param groupBookingRule
     * @return
     */
    public int getAbsentMemberNumBeforeSuccess(int siteId, int groupPurchaseParentId, Integer goodsId, GroupBookingRule groupBookingRule) {
        List<GroupPurchase> groupPurchases = groupPurChaseMapper.findGroupPurchasesByParentId(groupPurchaseParentId, siteId);

        return getAbsentMemberNumBeforeSuccess(groupPurchases, goodsId, groupBookingRule);
    }

    /**
     * 获取开团成功所需的剩余人数，即该团还差几人就能开团
     *
     * @param groupPurchases
     * @param goodsId
     * @param groupBookingRule
     * @return
     */
    public int getAbsentMemberNumBeforeSuccess(List<GroupPurchase> groupPurchases, Integer goodsId, GroupBookingRule groupBookingRule) {
        int groupMemberNum;
        switch (groupBookingRule.getRuleType()) {
            case 1:
            case 3:
                groupMemberNum = groupBookingRule.getRules().get(0).get("groupMemberNum");
                break;

            case 2:
            case 4:
                Map<String, Integer> rule = groupBookingRule.getRules().stream()
                    .filter(map -> goodsId.equals(Integer.parseInt(map.get("goodsId").toString())))
                    .findFirst()
                    .orElse(new HashMap<>());
                groupMemberNum = Optional.ofNullable(rule.get("groupMemberNum"))
                    .orElseThrow(() -> new RuntimeException("data error"));
                break;

            default:
                throw new RuntimeException("unknown type");
        }

        long size = groupPurchases.stream()
            .filter(groupPurchase -> groupPurchase.getStatus().equals(1) || groupPurchase.getStatus().equals(2))
            .count();
        int absentMemberNum = groupMemberNum - ((int) size);

        if (absentMemberNum >= 0)
            return absentMemberNum;
        else
            throw new RuntimeException("不该小于等于0");
    }


    /**
     * @param tradesId
     * @return 1:表示拼团中
     * 2:表示拼团成功
     * 3:其他
     */
    public int checkGroupPurchaseTradeStatusForDeliverGoods(long tradesId) {
        try {
            logger.info("查询订单状态是否可以提货。。。。。。{}。。。tradesId:" + tradesId);

            Trades trades = tradesMapper.getTradesByTradesId(tradesId);
            if (null == trades || !trades.getIsPayment().equals(1)) {
                logger.info("查询订单状态是否可以提货,查询订单数据异常。。。。。。{}。。。trades:" + trades);
                return 3;
            }

            GroupPurchase groupPurchase = groupPurChaseMapper.getGroupPurchaseFromTradsesId(tradesId + "");

            if (null != groupPurchase && null == groupPurchase.getParentId()) {
                if (groupPurchase.getStatus().equals(1) || groupPurchase.getStatus().equals(2)) {
                    logger.info("对应订单为拼团团主订单。。。。。{}。。。。。。getGroupPurchaseByTrades" + groupPurchase);
                    return groupPurchase.getStatus();
                } else {
                    return 3;
                }
            } else if (null != groupPurchase && null != groupPurchase.getParentId()) {
                logger.info("对应订单为参团订单。。。。。{}。。。。。。getGroupPurchaseByTrades" + groupPurchase);

                GroupPurchaseParam groupPurchaseParam = new GroupPurchaseParam();
                groupPurchaseParam.setSiteId(groupPurchase.getSiteId());
                groupPurchaseParam.setId(groupPurchase.getParentId());
                GroupPurchase parentGroupPurchase = groupPurChaseMapper.getMainForGroupPurchase(groupPurchaseParam);
                if (null != parentGroupPurchase) {
                    if (parentGroupPurchase.getStatus().equals(1) || parentGroupPurchase.getStatus().equals(2))
                        return parentGroupPurchase.getStatus();
                    else
                        return 3;
                } else {
                    return 3;
                }
            } else
                return 3;
        } catch (Exception e) {
            logger.info(e + "查询订单状态是否可以提货接口出现异常。。。。。。{}。。。tradesId:" + tradesId);
            return 3;
        }
    }

    /**
     * 根据订单id查询团信息
     *
     * @param siteId
     * @param tradesId
     * @return
     */
    @Nonnull
    public GroupInfo queryGroupInfoByTradesId(@Nonnull Integer siteId, @Nonnull Long tradesId) {
        GroupInfo groupInfo = new GroupInfo();
        GroupPurchase groupPurchase = groupPurChaseMapper.findByTradesId(siteId, tradesId.toString());

        // 查询是否是团的订单
        if (!isGroup(groupInfo, groupPurchase)) return groupInfo;

        // 查询订单相关状态
        Trades trades = tradesMapper.getTradesByTradesId(tradesId);
        setTradesInfo(trades, groupInfo);

        // 查询团长信息
        setGroupHeadInfo(groupInfo, groupPurchase);

        List<GroupPurchase> groupPurchaseList = groupPurChaseMapper.findGroupPurchasesByParentId(groupInfo.getParentId(), siteId);
        GroupPurchase headGroupPurchase = groupPurchaseList.stream()
            .filter(gp -> gp.getParentId() == null)
            .findFirst()
            .orElseThrow(RuntimeException::new);

        // 查询是否拼团成功
        setGroupStatusAndMainStatus(groupInfo, headGroupPurchase, groupPurchase, trades);

        // 拼团中查询时间和人数
        if (Integer.valueOf(GROUP_ING).equals(groupInfo.getGroupStatus())) {

            // 这里的修改是因为测试数据库经常删除活动表数据，导致报错，线上如果这里报异常，应该注意
            GroupBookingRule groupBookingRule;
            try {
                PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleBySiteIdAndActivityId(siteId, headGroupPurchase.getProActivityId());
                groupBookingRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GroupBookingRule.class);
            } catch (Exception e) {
                logger.error("异常发送，{}", e);
                groupInfo.setGroup(false);
                return groupInfo;
            }

            LocalDateTime groupEndTime = headGroupPurchase.getGroupbeginTime().plus(Duration.ofHours(groupBookingRule.getGroupLiveTime()));

            /*LocalDateTime now = LocalDateTime.now();
            if (groupEndTime.isBefore(now)) {
                groupInfo.setGroupStatus(GROUP_FAIL);
                groupInfo.setMainStatus(MAIN_STATUS_FAIL_SYSTEM);
                return groupInfo;
            }*/

            groupInfo.setGroupEndDateTime(longFormatter.format(groupEndTime));

            // 查询人数
            Integer groupPersonNum;
            if (groupBookingRule.getRuleType().equals(1) || groupBookingRule.getRuleType().equals(3))
                groupPersonNum = groupBookingRule.getRules().get(0).get("groupMemberNum");
            else
                groupPersonNum = groupBookingRule.getRules().stream()
                    .filter(map -> map.get("goodsId").equals(headGroupPurchase.getGoodsId()))
                    .findFirst()
                    .orElseThrow(RuntimeException::new)
                    .get("groupMemberNum");

            Long count = groupPurchaseList.stream()
                .filter(gp -> gp.getStatus().equals(1) || gp.getStatus().equals(2))
                .count();

            groupInfo.setPersonNumToSuccess(checkNotNull(getIfSuccess(() -> groupPersonNum - count.intValue(), i -> i > 0)));
        }

        return groupInfo;
    }

    private void setTradesInfo(Trades trades, GroupInfo groupInfo) {
        groupInfo.setPay(trades.getIsPayment() == 1);
        groupInfo.setRefund(trades.getIsRefund() == 120);
    }

    private void setGroupStatusAndMainStatus(GroupInfo groupInfo, GroupPurchase headGroupPurchase, GroupPurchase groupPurchase, Trades trades) {
        switch (headGroupPurchase.getStatus()) {
            case 0:
            case 1:
                groupInfo.setGroupStatus(GROUP_ING);
                if (groupInfo.isRefund()) {
                    setMainStatusAfterRefund(groupInfo, headGroupPurchase, groupPurchase);
                } else {
                    if (groupInfo.isHead()) {
                        if (headGroupPurchase.getStatus() == 0) {
                            groupInfo.setMainStatus(MAIN_STATUS_OPEN_UNPAY);
                        } else {
                            groupInfo.setMainStatus(MAIN_STATUS_OPEN_PAY);
                        }
                    } else {
                        if (groupPurchase.getStatus() == 2) {
                            groupInfo.setMainStatus(MAIN_STATUS_JOIN_PAY);
                        } else {
                            groupInfo.setMainStatus(MAIN_STATUS_JOIN_UNPAY);
                        }
                    }
                }

                break;

            case 2:
                groupInfo.setGroupStatus(GROUP_SUCCESS);
                if (groupInfo.isRefund()) {
                    setMainStatusAfterRefund(groupInfo, headGroupPurchase, groupPurchase);
                } else
                    groupInfo.setMainStatus(MAIN_STATUS_SUCCESS);

                break;

            case 3:
            case 4:
                groupInfo.setGroupStatus(GROUP_FAIL);
                groupInfo.setMainStatus(MAIN_STATUS_FAIL_SYSTEM);
                break;

            default:
                throw new UnknownTypeException();
        }

        switch (trades.getTradesStatus()) {
            case 160:
                groupInfo.setMainStatus(MAIN_STATUS_FAIL_CUSTOMER);
                break;

            case 170:
                groupInfo.setMainStatus(MAIN_STATUS_FAIL_SYSTEM);
                break;

            case 180:
                groupInfo.setMainStatus(MAIN_STATUS_FAIL_MERCHANT);
                break;

            default:
                // doNothing
        }
    }

    private void setMainStatusAfterRefund(GroupInfo groupInfo, GroupPurchase headGroupPurchase, GroupPurchase groupPurchase) {
        Refund refund = refundMapper.getRefundByTradeId(headGroupPurchase.getSiteId(), groupPurchase.getTradesId());
        switch (refund.getOperatorType()) {
            case 100:
                groupInfo.setMainStatus(MAIN_STATUS_FAIL_CUSTOMER);
                break;

            case 200:
                groupInfo.setMainStatus(MAIN_STATUS_FAIL_MERCHANT);
                break;

            case 300:
                groupInfo.setMainStatus(MAIN_STATUS_FAIL_SYSTEM);
                break;

            default:
                throw new UnknownTypeException();
        }
    }

    private boolean isGroup(GroupInfo groupInfo, GroupPurchase groupPurchase) {
        if (groupPurchase == null) {
            groupInfo.setGroup(false);
            return false;
        }

        groupInfo.setGroup(true);
        return true;
    }

    private void setGroupHeadInfo(GroupInfo groupInfo, GroupPurchase groupPurchase) {
        groupInfo.setHead(groupPurchase.getParentId() == null);

        if (groupInfo.isHead())
            groupInfo.setParentId(groupPurchase.getId());
        else
            groupInfo.setParentId(groupPurchase.getParentId());
    }

    /**
     * @param siteId
     * @param promotionsActivityId
     * @return 当查询不到数据或异常的时候会出现 空 Optional 对象
     */
    public Optional<List<String>> getStoresByProActivityId(@Nonnull Integer siteId,
                                                           @Nonnull Integer promotionsActivityId) {

        PromotionsActivitySqlParam param = new PromotionsActivitySqlParam();
        param.setSiteId(siteId);
        param.setPromotionsActivityId(promotionsActivityId);
        List<PromotionsActivity> list = promotionsActivityMapper.findByParamWithRuleIn(param);

        if (list.size() == 1) {
            PromotionsActivity promotionsActivity = list.get(0);
            PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();

            if (Integer.valueOf(PromotionsConstant.PROMOTIONS_RULE_TYPE_GROUP_BOOKING).equals(promotionsRule.getPromotionsType())) {
                List<String> stores;
                switch (promotionsRule.getUseStore()) {
                    case "-1":
                        stores = storesMapper.selectAllStoreByStatus(siteId, 1).stream()
                            .filter(store -> Integer.valueOf(1).equals(store.getIsDel()))
                            .map(store -> Integer.valueOf(store.getId()).toString())
                            .collect(Collectors.toList());

                        break;

                    case "1":
                        stores = storesMapper.selectStoreByIds(siteId, promotionsRule.getUseArea(), 1).stream()
                            .filter(store -> Integer.valueOf(1).equals(store.getIsDel()))
                            .map(store -> Integer.valueOf(store.getId()).toString())
                            .collect(Collectors.toList());

                        break;

                    case "2":
                        stores = storesMapper.selectStoreByCityIds(siteId, promotionsRule.getUseArea(), 1).stream()
                            .filter(store -> Integer.valueOf(1).equals(store.getIsDel()))
                            .map(store -> Integer.valueOf(store.getId()).toString())
                            .collect(Collectors.toList());

                        break;

                    default:
                        return Optional.empty();
                }

                if (stores != null && stores.size() != 0) {
                    return Optional.of(stores);
                } else
                    return Optional.empty();
            } else
                return Optional.empty();
        } else
            return Optional.empty();
    }

    static class GroupPurchaseComparatorDesc implements Comparator<GroupPurchase> {
        @Override
        public int compare(GroupPurchase param1, GroupPurchase param2) {
            Integer v1 = CollectionUtils.isNotEmpty(param1.getChildrenList()) ? param1.getChildrenList().size() : 0;
            Integer v2 = CollectionUtils.isNotEmpty(param2.getChildrenList()) ? param2.getChildrenList().size() : 0;
            long lastTime1 = param1.getLastTime();
            long lastTime2 = param2.getLastTime();

            if (v1.intValue() != v2.intValue()) {
                return v2.compareTo(v1);
            } else {
                return Long.compare(lastTime1, lastTime2);
            }

        }

    }

    private Integer getGroupPuechasePrice(Integer siteId, Integer proActivityId, Integer goodsId) {
        GroupPurchaseForProActivityParam groupPurchaseForProActivityParam = new GroupPurchaseForProActivityParam();
        groupPurchaseForProActivityParam.setSiteId(siteId);
        groupPurchaseForProActivityParam.setProActivityId(proActivityId);
        Map<String, Object> oneGroupPro = promotionsActivityMapper.getGroupPurchaseOneProActivity(groupPurchaseForProActivityParam);
        Goods goods = goodsMapper.getBySiteIdAndGoodsId(goodsId, siteId);

        String promotion_rule = oneGroupPro.get("promotions_rule").toString();
        GroupBookingRule groupBookingRule = JSON.parseObject(promotion_rule, GroupBookingRule.class);
        List<Map<String, Integer>> rules = groupBookingRule.getRules();
        switch (groupBookingRule.getRuleType()) {

            case 1:
                return rules.get(0).get("groupPrice");
            case 2:
                rules = groupBookingRule.getRules().stream().filter(stringIntegerMap -> stringIntegerMap.get("goodsId").equals(goodsId)).collect(Collectors.toList());
                return rules.get(0).get("groupPrice");
            case 3:
                Integer is_ml = groupBookingRule.getIsMl();
                Integer is_round = groupBookingRule.getIsRound();

                Integer discount = rules.get(0).get("groupDiscount");
                Integer maxReduce = rules.get(0).get("maxReduce");
                Integer reducePrice = orderDeductionUtils.discountMoney(goods.getShopPrice(), is_ml, is_round, discount);

                if (maxReduce.intValue() > 0 && maxReduce.intValue() < reducePrice)
                    reducePrice = maxReduce;
                return goods.getShopPrice() - reducePrice;
            case 4:
                rules = groupBookingRule.getRules().stream().filter(stringIntegerMap -> stringIntegerMap.get("goodsId").equals(goodsId)).collect(Collectors.toList());
                Integer is_mls = groupBookingRule.getIsMl();
                Integer is_rounds = groupBookingRule.getIsRound();
                Integer discounts = rules.get(0).get("groupDiscount");
                Integer maxReduces = rules.get(0).get("maxReduce");
                Integer reducePrices = orderDeductionUtils.discountMoney(goods.getShopPrice(), is_mls, is_rounds, discounts);

                if (maxReduces.intValue() > 0 && maxReduces.intValue() < reducePrices)
                    reducePrices = maxReduces;
                return goods.getShopPrice() - reducePrices;
        }
        return null;
    }
}
