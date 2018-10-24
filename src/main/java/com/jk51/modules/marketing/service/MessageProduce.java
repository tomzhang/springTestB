package com.jk51.modules.marketing.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.BMarketingPlan;
import com.jk51.modules.coupon.service.CouponActivityService;
import com.jk51.modules.integral.service.IntegerRuleService;
import com.jk51.modules.marketing.mapper.BMarketingMemberExtMapper;
import com.jk51.modules.marketing.mapper.BMarketingPlanMapper;
import com.jk51.modules.marketing.request.PrizesMessage;
import com.jk51.modules.marketing.request.PrizesType;
import com.jk51.mq.mns.CloudQueueFactory;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RunMsgWorker(queueName = MessageProduce.QUEUE_NAME)
public class MessageProduce implements MessageWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProduce.class);

    public static final String QUEUE_NAME = "MessageProduceQueue";


    @Autowired
    private BMarketingMemberExtMapper bMarketingMemberExtMapper;
    @Autowired
    private MarketingService marketingService;
    @Autowired
    private IntegerRuleService integerRuleService;
    @Autowired
    private CouponActivityService couponActivityService;
    @Autowired
    private BMarketingPlanMapper bMarketingPlanMapper;


    @Override
    public void consume(Message message) throws Exception {
        MessageBody messageBody = null;
        try {
            messageBody = JSONObject.parseObject(message.getMessageBody(), MessageBody.class);
            Objects.requireNonNull(messageBody, "无效的messageBody。");
        } catch (Exception e) {
            LOGGER.error("消息 {} 错误的消息内容 {} {} ", message, e.getMessage(), e);
            return;
        }

        JSONObject data = (JSONObject) messageBody.getData();
        switch (messageBody.getType()) {
            case PrizesMessage.MESSAGE_TYPE :
                sendPrizes(data.toJavaObject(PrizesMessage.class));
                break;
            default:
                LOGGER.error("未被匹配的消息 {} ", message);
        }

    }


    private void sendPrizes(PrizesMessage prizesMessage) throws Exception {
        BMarketingPlan plan = bMarketingPlanMapper.selectAllById(prizesMessage.getSiteId(), prizesMessage.getPlanId());
        if (plan == null) return;

        Integer sendStatus = 300;//100-未发放  200-已发放  300-发放失败
        String errorMsg = null;
        if (prizesMessage.getType() == PrizesType.COUPON.getVal()) {
            ReturnDto result = couponActivityService.sendCouponForTurnTable(prizesMessage.getSiteId(), plan.getCouponActivityId(), prizesMessage.getBuyerId(), prizesMessage.getTypeId());
            if ("OK".equals(result.getStatus())) {
                sendStatus = 200;
            } else {
                errorMsg = result.getMessage();
            }
        } else if (prizesMessage.getType() == PrizesType.SCORE.getVal()) {
            String result = integerRuleService.getIntegralByGame(new HashMap() {{
                put("siteId", prizesMessage.getSiteId());
                put("buyerId", prizesMessage.getBuyerId());
                put("addIntegral", prizesMessage.getTypeInfo());
            }});
            if ("送积分成功".equals(result)) sendStatus = 200;
        }

        marketingService.addLog(prizesMessage.getSiteId(), 97, prizesMessage.getBuyerId(), "MessageProduce",
            JSONObject.toJSONString(prizesMessage), "系统发放奖品(活动ID)：" + prizesMessage.getPlanId() + " - " + sendStatus + " - " + errorMsg);

        try {
            bMarketingMemberExtMapper.changeStatus(prizesMessage.getSiteId(), prizesMessage.getMarketingMemberExtId(), sendStatus, "系统发放");
        } catch (Exception e) {
            LOGGER.error("奖品系统发放异常 {} ", e);
        }
    }




    public void sendMessage(MessageBody messageBody) throws Exception {
        if (messageBody.getType() == null || messageBody.getData() == null) throw new RuntimeException("MessageBody - NullPointerException");
        CloudQueue queue = CloudQueueFactory.create(QUEUE_NAME);
        queue.putMessage(new Message(JSONObject.toJSONString(messageBody)));
    }

    public static class MessageBody {
        private Integer type;

        private Object data;

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "MessageBody{" +
                "type=" + type +
                ", data=" + data +
                '}';
        }
    }

    public static class Constant {
        public static final int PRIZES_MESSAGE = 0;
    }
}
