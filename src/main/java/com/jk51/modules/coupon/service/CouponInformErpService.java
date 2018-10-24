package com.jk51.modules.coupon.service;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.CouponErpParams;
import com.jk51.model.coupon.requestParams.LimitRule;
import com.jk51.model.coupon.requestParams.TimeRule;
import com.jk51.model.order.Member;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.offline.service.CouponErpService;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.mq.mns.CloudQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/9/22.
 */
@Service
public class CouponInformErpService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private CouponRuleMapper couponRuleMapper;

    @Autowired
    private CouponDetailMapper couponDetailMapper;

    @Autowired
    private CouponNoEncodingService couponNoEncodingService;

    public static final Logger logger = LoggerFactory.getLogger(CouponInformErpService.class);

    /**
     * 用户领券
     *
     * @param details 优惠券详情集合
     */
    public void ifContainCrashCouponThenSendQueueMessage(List<CouponDetail> details) {
        sendMsgBin(details, "用户通过渠道领取此券,0");
    }

    /**
     * 用户退款/取消订单
     *
     * @param scene 情景 0取消订单退券 1退款成功退券
     * @param orderId 订单id
     */
    public void ifContainCrashCouponThenSendQueueMessage(String orderId,Integer scene) {
        CouponDetail couponDetail = couponDetailMapper.findCouponDetailByOrderId(orderId);
        ArrayList list = new ArrayList();
        list.add(couponDetail);
        switch (scene){
            case 0: {
                sendMsgBin(list, "用户取消订单归还此券,1");
                break;
            }
            case 1: {
                sendMsgBin(list, "用户申请退款并且商家同意归还此券,1");
                break;
            }

        }

    }

    /**
     * 用户使用
     *
     * @param couponDetail 优惠券详情
     */
    public void ifContainCrashCouponThenSendQueueMessage(CouponDetail couponDetail) {
        ArrayList list = new ArrayList();
        list.add(couponDetail);
        sendMsgBin(list, "用户使用了此券,-1");
    }


    public Integer ErpUpdateByCouponNo(Integer siteId, String couponNo, Integer status) {
        if (couponNo != null && !"".equals(couponNo)) {
            String[] couponNoStrArr = couponNo.split(",");
            String[] decryptionCouponNoArr = new String[couponNoStrArr.length];
            for (int i = 0; i < couponNoStrArr.length; i++) {
                decryptionCouponNoArr[i] = couponNoEncodingService.decryptionCouponNo(couponNoStrArr[i]);
            }
            logger.info("更新优惠券状态,站点:{},解析后的券码:{},状态:{}.", siteId, decryptionCouponNoArr.toString(), status);
            return couponDetailMapper.updateStatusByCouponNo(siteId, decryptionCouponNoArr, status);
        }
        return 0;
    }

    private void sendMsgBin(List<CouponDetail> details, String status) {
        try {
            details.stream()
                .filter(couponDetail -> couponDetail.getRuleId() != 0)
                .forEach(couponDetail -> {
                    CouponRule couponRule = couponRuleMapper.findCouponRuleById(couponDetail.getRuleId(),
                        couponDetail.getSiteId());
                    //如果活动中包含线下的现金券,则发送消息到消息队列
                    if (couponRule != null && couponRule.getCouponType() == 100 &&
                        !"101,103".equals(getApplyChannel(couponRule.getLimitRule()))) {
                        CouponErpParams couponErpParams = new CouponErpParams();
                        couponErpParams.setSiteId(couponRule.getSiteId());
                        couponErpParams.setRuleId(couponRule.getRuleId());
                        couponErpParams.setCouponNo(couponNoEncodingService.encryptionCouponNo(couponDetail.getCouponNo()));
                        Member member = memberMapper.getMemberByMemberId(couponRule.getSiteId(), couponDetail.getUserId());
                        couponErpParams.setMobile(member == null ? "0" : member.getMobile());
                        timeRule(couponRule.getTimeRule(), couponErpParams);
                        couponErpParams.setStatus(status);
                        sendMq(couponErpParams);
                    }
                });
        } catch (Exception e) {
            logger.error("通知erp优惠券相关信息错误，异常：{}", e);
            e.printStackTrace();
        }
    }

    private void timeRule(String timeRuleStr, CouponErpParams couponErpParams) {
        TimeRule timeRule = JSON.parseObject(timeRuleStr, TimeRule.class);
        //如果是相对时间
        if (timeRule.getValidity_type() == 2) {
            CouponDetail couponDetail = couponDetailMapper.findCouponDetailByCouponNo(couponNoEncodingService.
                decryptionCouponNo(couponErpParams.getCouponNo()), couponErpParams.getSiteId());
            couponErpParams.setStartTime(couponDetail.getCreateTime().toString().replaceAll(".\\d+$", ""));
            couponErpParams.setEndTime(getSpecifiedDayAfter(couponDetail.getCreateTime().toString()));
        } else {
            couponErpParams.setStartTime(timeRule.getStartTime());
            couponErpParams.setEndTime(timeRule.getEndTime());
        }
    }

    private String getApplyChannel(String limitRuleStr) {
        LimitRule limitRule = JSON.parseObject(limitRuleStr, LimitRule.class);
        return limitRule.getApply_channel();
    }

    private void sendMq(CouponErpParams couponErpParams) {

        String queueName = CouponErpService.topicName;
        CloudQueue queue = CloudQueueFactory.create(queueName);
        Message message = new Message();
        message.setMessageBody(JSON.toJSONBytes(couponErpParams));
        try {
            queue.putMessage(message);
            logger.info(" 加入消息队列成功! queueName:{} messageBody:{},messageId:{}", queueName,
                message.getMessageBodyAsString(),
                message.getMessageId());
        } catch (Exception e) {
            logger.debug("发送到消息队列失败 body:{} error:{}", message.getMessageBodyAsString(),
                e.getMessage());
        }
    }

    public static String getSpecifiedDayAfter(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(specifiedDay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);

        String dayAfter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
        return dayAfter;
    }

}
