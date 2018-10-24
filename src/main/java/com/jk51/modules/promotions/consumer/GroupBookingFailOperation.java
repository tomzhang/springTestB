package com.jk51.modules.promotions.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.model.Message;
import com.google.common.base.Throwables;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.Goods;
import com.jk51.model.order.Trades;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.grouppurchase.mapper.GroupPurChaseMapper;
import com.jk51.modules.grouppurchase.service.GroupPurChaseService;
import com.jk51.modules.privatesend.core.AliPrivateSend;
import com.jk51.modules.privatesend.core.PrivateSend;
import com.jk51.modules.promotions.constants.GroupBookingConstant;
import com.jk51.modules.trades.controller.TradesController;
import com.jk51.modules.trades.service.RefundService;
import com.jk51.modules.trades.service.TradesService;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 团购活动时效到期，人数未满，开团失败处理
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/11/23                                <br/>
 * 修改记录:                                         <br/>
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
@RunMsgWorker(queueName = "GroupBookingFailOperation1")
public class GroupBookingFailOperation implements MessageWorker {
    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String ALREADY_REFUND = "用户已退款";
    private static final String UNPAY = "用户未付款，关闭订单";

    @Autowired
    private TradesService tradesService;
    @Autowired
    private GroupPurChaseMapper groupPurChaseMapper;
    @Autowired
    private PrivateSend privateSend;
    @Autowired
    private AliPrivateSend aliPrivateSend;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GroupPurChaseService groupPurChaseService;
    @Autowired
    private RefundService refundService;

    /**
     * 团购活动时效到期，人数未满，开团失败处理
     *
     * @param message 消费的消息
     */
    @Override
    public void consume(Message message) {
        String messageBodyAsString = message.getMessageBodyAsString();
        logger.info("团购活动团队时效已到，开团失败，开始处理!{}", messageBodyAsString);
        try {
            JSONObject jsonObject = JSON.parseObject(messageBodyAsString);
            String tradesId = (String) jsonObject.get("tradesId");
            Integer groupPurchaseId = (Integer) jsonObject.get("groupPurchaseId");
            Integer siteId = (Integer) jsonObject.get("siteId");

            // 退款
            Trades trades = tradesService.getTradesByTradesId(Long.parseLong(tradesId));
            ReturnDto refundResult = refund(trades);

            if (!"000".equals(refundResult.getCode())) {
                logger.info("团购活动开团失败 -> 退款，订单编号{}的退款失败", tradesId);
                throw new RuntimeException("退款失败");
            }

            // 退款成功，修改groupPurchase状态
            groupPurChaseMapper.updateStatusByIdAndSiteId(groupPurchaseId, siteId, 4);

            sendFailToGroupMsgToWeChatCustomer(jsonObject, siteId, trades, refundResult, groupPurchaseId);
        } catch (Exception e) {
            logger.error("\'团购活动开团失败处理\'失败，异常信息：{}", messageBodyAsString, e);
        }
    }

    /**
     * 拼图时间已到，人数未满，拼团失败通知客户
     *
     * @param jsonObject      内涵key groupPurchaseId, siteId, tradesId, openId, goodsId 具体见{@link GroupPurChaseService#batchSendGroupBookingFailOperationMQMsg(java.util.List, Integer)}
     * @param siteId
     * @param trades
     * @param refundResult
     * @param groupPurchaseId
     */
    public void sendFailToGroupMsgToWeChatCustomer(JSONObject jsonObject, Integer siteId, Trades trades, ReturnDto refundResult, Integer groupPurchaseId) {
        String openId = (String) jsonObject.get("openId");
        String aliUserId = (String) jsonObject.get("aliUserId");
        if (StringUtils.isBlank(openId) && StringUtils.isBlank(aliUserId))
            return;

        Integer goodsId = (Integer) jsonObject.get("goodsId");
        Goods goods = goodsMapper.getBySiteIdAndGoodsId(goodsId, siteId);
        String realPay_formatted = String.format("%.2f", trades.getRealPay() / 100f);
        // 等待微信前端定义
        String url = tradesService.getOrderDUrl(siteId, trades.getPostStyle(), trades.getTradesId());

        // 拼单失败通知（退款）
        if (refundResult.getValue().equals(ALREADY_REFUND)) {
            // doNothing
        } else if (refundResult.getValue().equals(UNPAY)) {
            groupPurChaseService.sendGroupPurchaseCancelMsgToWechatCustomer(siteId, openId,aliUserId, trades, goods, groupPurchaseId);
        } else {
            privateSend.togetherOrderCreateFailNoticeRefund(siteId, openId, url,
                GroupBookingConstant.GROUP_FAIL_AND_REFUND_FIRST,
                GroupBookingConstant.GROUP_FAIL_AND_REFUND_REMARK,
                goods.getDrugName(),
                realPay_formatted,
                realPay_formatted);
            aliPrivateSend.togetherOrderCreateFailNoticeRefund(siteId, aliUserId, url,
                GroupBookingConstant.GROUP_FAIL_AND_REFUND_FIRST,
                GroupBookingConstant.GROUP_FAIL_AND_REFUND_REMARK,
                goods.getDrugName(),
                realPay_formatted,
                realPay_formatted);
        }
    }

    /**
     * 退款或关闭未付款订单
     *
     * @param trades 订单
     * @return code 000 代表成功， 其他失败
     * @throws Exception 异常
     */
    private ReturnDto refund(Trades trades) throws Exception {
        ReturnDto refundResult;

        if (trades.getIsPayment() == 1 && trades.getTradesStatus() != 900) { // 用户已付款，未退款
            logger.info("用户已付款，未退款，退款开始, {}", trades);
            refundResult = refundService.zdApplyRefund(trades.getTradesId().toString(), 1, 1, trades.getRealPay(), 300);

        } else if (trades.getIsPayment() == 1 && trades.getTradesStatus() == 900) { // 用户已退款
            logger.info(ALREADY_REFUND);
            refundResult = ReturnDto.buildSuccessReturnDto(ALREADY_REFUND);

        } else if (trades.getTradesStatus() != 170) { // 用户未付款，关闭订单
            logger.info(UNPAY);
            Integer tradesStatus = trades.getTradesStatus();
            trades.setTradesStatus(170);
            tradesService.closeTrades(trades, tradesStatus, "拼团时效到期，未付款");
            refundResult = ReturnDto.buildSuccessReturnDto(UNPAY);
        } else {
            if (trades.getIsPayment() == 0) {
                refundResult = ReturnDto.buildSuccessReturnDto(UNPAY);
            } else {
                refundResult = ReturnDto.buildSuccessReturnDto(ALREADY_REFUND);
            }
        }

        return refundResult;
    }
}
