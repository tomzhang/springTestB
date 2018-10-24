package com.jk51.modules.promotions.job;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.java8datetime.ParseAndFormat;
import com.jk51.model.Goods;
import com.jk51.model.grouppurchase.GroupPurchase;
import com.jk51.model.order.Trades;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.GroupBookingRule;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.grouppurchase.mapper.GroupPurChaseMapper;
import com.jk51.modules.grouppurchase.service.GroupPurChaseService;
import com.jk51.modules.privatesend.core.AliPrivateSend;
import com.jk51.modules.privatesend.core.PrivateSend;
import com.jk51.modules.promotions.constants.GroupBookingConstant;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import com.jk51.modules.trades.service.TradesService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/28                                <br/>
 * 修改记录:                                         <br/>
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public class StatusTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PromotionsRuleMapper promotionsRuleMapper;
    @Autowired
    private PromotionsRuleService promotionsRuleService;
    @Autowired
    private GroupPurChaseMapper groupPurChaseMapper;
    @Autowired
    private GroupPurChaseService groupPurChaseService;
    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;
    @Autowired
    private PrivateSend privateSend;
    @Autowired
    private AliPrivateSend aliPrivateSend;
    @Autowired
    private TradesService tradesService;
    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 定时任务，不要改动stream为parallelStream
     */
    public void checkPromotionsRuleStatus() {
        promotionsRuleMapper.getPromotionsRuleToJob()
            .forEach(this::checkPromotionsRuleStatusAsync);
    }

    @Async
    public void checkPromotionsRuleStatusAsync(PromotionsRule promotionsRule){
        promotionsRuleService.autoChangeStatus(promotionsRule.getSiteId(), promotionsRule.getId());
    }

    /**
     * 扫描开团中的信息
     * 1.查询出拼团失败的信息，送入消息队列处理
     * 2.查询需要做出拼团人数不足提醒的团员，发出通知
     */
    public void groupPurchaseStatus() {
        autoUpdateGroupPurchaseStatus(null);
    }

    public void autoUpdateGroupPurchaseStatus(@Nullable Integer headGroupPurchaseId) {
        // 现在 这个时间点放这里是为了保证每个定时任务之间，现在这个时间点间距尽量一致，减少代码运行时间的影响
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime afterNow = now.plus(Duration.ofSeconds(GroupBookingConstant.REMIND_SCHEDULE_TIME_INTERVAL / 2));
        LocalDateTime beforeNow = now.minus(Duration.ofSeconds(GroupBookingConstant.REMIND_SCHEDULE_TIME_INTERVAL / 2));

        // 只查询团长的团信息，团员的后续处理
        List<GroupPurchase> groupPurchaseList = groupPurChaseMapper.getGroupPurchaseListForTask(headGroupPurchaseId);
        Set<Integer> promotionsActivityIds = groupPurchaseList.stream()
            .map(GroupPurchase::getProActivityId)
            .collect(toSet());
        if (groupPurchaseList.size() == 0 || promotionsActivityIds.size() == 0) return;

        // 只根据promotionsActivityId，而没有同时使用siteId和promotionsActivityId去查询活动规则
        // 虽然有些浪费空间，但可以确实地加快数据库查询效率
        Map<String, Map<String, Object>> tempMap = new HashMap<>();
        promotionsActivityMapper.getSomeInfoForTask(promotionsActivityIds)
            .stream()
            .filter(map -> "60".equals(map.get("promotions_type").toString()))
            .forEach(map -> tempMap.put(map.get("site_id").toString().concat(map.get("id").toString()), map));

        // 数据处理
        for (GroupPurchase groupPurchase : groupPurchaseList) {
            try {
                LocalDateTime groupStartTime = groupPurchase.getGroupbeginTime();

                String key = groupPurchase.getSiteId().toString().concat(groupPurchase.getProActivityId().toString());
                Map<String, Object> ruleData = tempMap.get(key);
                GroupBookingRule groupBookingRule = JSON.parseObject(ruleData.get("promotions_rule").toString(), GroupBookingRule.class);
                Duration groupLiveTime = Duration.ofHours(groupBookingRule.getGroupLiveTime());

                // 判断拼单是否失败
                LocalDateTime groupEndTime = groupStartTime.plus(groupLiveTime);
                if (groupEndTime.isBefore(now)) {
                    // 发出结束团的通知
                    List<GroupPurchase> groupPurchases = groupPurChaseMapper.findGroupPurchasesByParentId(groupPurchase.getId(), groupPurchase.getSiteId());
                    groupPurChaseService.batchSendGroupBookingFailOperationMQMsg(groupPurchases, groupPurchase.getId());
                    continue;
                }

                // 判断是否需要发拼单人数不足提醒
                LocalDateTime remindTime = calculateRemindTime(groupStartTime, groupBookingRule.getGroupLiveTime());
                if (remindTime.isAfter(beforeNow) && remindTime.isBefore(afterNow)) {
                    int goodsId = groupPurchase.getGoodsId();
                    Goods goods = goodsMapper.getBySiteIdAndGoodsId(goodsId, groupPurchase.getSiteId());

                    // 发出提醒通知
                    List<GroupPurchase> groupPurchases = groupPurChaseMapper.findGroupPurchasesByParentId(groupPurchase.getId(), groupPurchase.getSiteId());
                    sendRemindMsgAboutGroupToWeChatCustomer(groupPurchases, groupPurchase, groupBookingRule, now, goods);
                    // 发出未支付超时通知
                    sendUnpaidMsgAboutGroupToWeChatCustomer(groupPurchases, goods);
                }
            } catch (Exception e) {
                logger.error("发生异常, {}", e);
            }
        }

    }

    @Async
    private void sendUnpaidMsgAboutGroupToWeChatCustomer(List<GroupPurchase> groupPurchase, Goods goods) {
        groupPurchase.stream()
            .filter(gp -> gp.getStatus() == 0)
            .forEach(gp -> {
                String openId = gp.getOpenId();
                String aliUserId = gp.getAliUserId();
                if (StringUtils.isBlank(openId) && StringUtils.isBlank(aliUserId)) return;

                Long tradesId = Long.parseLong(gp.getTradesId());
                Trades trades = tradesService.getTradesByTradesId(tradesId);

                String url = tradesService.getOrderDUrl(gp.getSiteId(), trades.getPostStyle(), tradesId);
                String keyword2 = goods.getDrugName();
                String keyword3 = trades.getCreateTime().toLocalDateTime().format(ParseAndFormat.dateTimeFormatter_2);
                String keyword4 = String.format("%.2f元", trades.getRealPay() / 100f);

                privateSend.togetherOrderUnPay(gp.getSiteId(), openId, url,
                    GroupBookingConstant.GROUP_NOPAY_REMIND_FIRST,
                    GroupBookingConstant.GROUP_NOPAY_REMIND_REMARK,
                    tradesId.toString(), keyword2, keyword3, keyword4,
                    GroupBookingConstant.GROUP_NOPAY_REMIND_KEYWORD5);
                aliPrivateSend.togetherOrderUnPay(gp.getSiteId(), aliUserId, url,
                    GroupBookingConstant.GROUP_NOPAY_REMIND_FIRST,
                    GroupBookingConstant.GROUP_NOPAY_REMIND_REMARK,
                    tradesId.toString(), keyword2, keyword3, keyword4,
                    GroupBookingConstant.GROUP_NOPAY_REMIND_KEYWORD5);
            });
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Async
    private void sendRemindMsgAboutGroupToWeChatCustomer(List<GroupPurchase> groupPurchases,
                                                         GroupPurchase parentGroupPurchase,
                                                         GroupBookingRule groupBookingRule,
                                                         LocalDateTime now, Goods goods) {
        // 计算剩余时间
        LocalDateTime groupBeginTime = parentGroupPurchase.getGroupbeginTime();
        LocalDateTime groupEndTime = groupBeginTime.plus(Duration.ofHours(groupBookingRule.getGroupLiveTime()));
        Duration duration = Duration.ofMinutes(ChronoUnit.MINUTES.between(groupEndTime, now)).abs();

        String remainderTime;
        if (duration.toHours() != 0) {
            remainderTime = String.format("%d小时%d分钟", duration.toHours(), duration.minus(Duration.ofHours(duration.toHours())).toMinutes());
        } else {
            remainderTime = String.format("%d分钟", duration.toMinutes());
        }

        int memberNum = groupPurChaseService.getAbsentMemberNumBeforeSuccess(groupPurchases, parentGroupPurchase.getGoodsId(), groupBookingRule);

        groupPurchases.forEach(groupPurchase -> {
            String openId = groupPurchase.getOpenId();
            String aliUserId = groupPurchase.getAliUserId();
            if (StringUtils.isBlank(openId) && StringUtils.isBlank(aliUserId)) return;

            Trades trades = tradesService.getTradesByTradesId(Long.parseLong(groupPurchase.getTradesId()));
            Integer siteId = groupPurchase.getSiteId();
            String url = tradesService.getOrderDUrl(siteId, trades.getPostStyle(), trades.getTradesId());

            String first = String.format(GroupBookingConstant.GROUP_REMIND_FIRST, remainderTime, memberNum);
            String remark = GroupBookingConstant.GROUP_REMIND_REMARK;
            String keyword1 = goods.getDrugName();
            String keyword2 = remainderTime;
            String keyword3 = memberNum + "人";

            privateSend.togetherOrderPeopleLack(siteId, openId, url, first, remark, keyword1, keyword2, keyword3);
            aliPrivateSend.togetherOrderPeopleLack(siteId, aliUserId, url, first, remark, keyword1, keyword2, keyword3);
        });
    }

    /**
     * @param groupStartTime 开团时间
     * @param groupLiveTime  单位：小时， 团有效时间
     * @return
     */
    private LocalDateTime calculateRemindTime(LocalDateTime groupStartTime, Integer groupLiveTime) {
        Duration durationToRemind = Duration.ofMillis(groupLiveTime * 60 * 60 * 100 * (100 - GroupBookingConstant.REMIND_DURATION_BEFORE_FAIL) / 100);
        return groupStartTime.plus(durationToRemind);
    }
}
