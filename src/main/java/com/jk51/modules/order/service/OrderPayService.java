package com.jk51.modules.order.service;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.ChOrderRemind;
import com.jk51.model.account.models.PayPlatform;
import com.jk51.model.order.BMember;
import com.jk51.model.order.Trades;
import com.jk51.modules.account.mapper.PayPlatformMapper;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.modules.balance.service.BaseFeeService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.im.mapper.ChOrderRemindMapper;
import com.jk51.modules.im.service.GeTuiPush;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.integral.service.IntegerRuleService;
import com.jk51.modules.integral.service.IntegralService;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.smallTicket.service.SmallTicketService;
import com.jk51.modules.trades.consumer.TradesWebcall;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.modules.trades.service.TradesService;
import com.jk51.mq.mns.CloudQueueFactory;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:分单service
 * 作者: baixiongfei
 * 创建日期: 2017/2/16
 * 修改记录:
 */
@Service
public class OrderPayService {
    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private IntegralService integralService;


    @Autowired
    private TradesService tradesService;
    @Autowired
    private CouponSendService couponSendService;
    @Autowired
    private ChOrderRemindMapper chOrderRemindMapper;
    @Autowired
    private GeTuiPush geTuiPush;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private StoreAdminMapper storeAdminMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private SmallTicketService smallTicketService;
    @Autowired
    private BaseFeeService baseFeeService;
    public static final Logger logger = LoggerFactory.getLogger(OrderPayService.class);


    /**
     * 对应 tradesService.paySuccessCallback()
     *
     * @param trades
     * @throws Exception
     */
    public void paySuccessCallback2(Trades trades) throws Exception {
        logger.info("首单付款发放----pay paySuccessCallback2----------------");

        //            schedulerTask.getBudgetDate(trades.getTradesId());
        /*try {
            updateOrderPayCommission(trades.getSiteId(), trades.getTradesId(), trades.getRealPay(), trades.getPayStyle());
        } catch (Exception e) {
            logger.error("记录每笔订单的支付佣金，支付完成之后需要调用该方法把支付佣金记录到b_trades表中失败，错误：" + e);
        }*/
        try {
            //推送付款成功订单或者退款成功订单给erp
//            tradesService.pushTradesToJZ(trades.getSiteId(), trades.getTradesId());
        } catch (Exception e) {
            logger.error("推送付款成功订单或者退款成功订单给erp错误：" + e);
        }



        //修改会员信息
        BMember bShopMember = ordersMapper.getMemberByBuyerId(trades.getSiteId(), trades.getBuyerId());
        if(!StringUtil.isEmpty(bShopMember)){
            bShopMember.setOrderFee(bShopMember.getOrderFee() + trades.getRealPay());
            bShopMember.setOrderNum(bShopMember.getOrderNum() + 1);
            ordersMapper.updateOrderMember(bShopMember);
            //---------生成小票--------start
            smallTicketService.callBack(bShopMember, trades.getTradesId());
            //---------生成小票--------end
        }
        /*try {
            storeOrderRemind(trades.getTradesId());//已付款订单且指定门店 进行订单提醒，微信端下单
        } catch (Exception e) {
            logger.error("错误：" + e);
            e.printStackTrace();
        }*/

        /* 直购订单不送优惠券本期处理 zw*/
        /*try {
            if (trades.getPostStyle().intValue() != CommonConstant.POST_STYLE_DIRECT_PURCHASE) {
                couponSendService.sendCouponByPay(String.valueOf(trades.getTradesId()));

            }
        } catch (Exception e) {
            logger.error("赠送优惠券失败，错误：" + e);
            e.printStackTrace();
        }*/
        //支付后电话提醒，加入消息队列
        //sendMqMessage(trades.getTradesId());

    }
    public void sendMqMessage(Long tradesId,Integer time,Integer isTel) {
        logger.info("电话提醒开始{}", tradesId);
        Map<String, Object> map = new HashedMap();
        map.put("tradesId", tradesId);
        map.put("isTel", isTel);
        String queueName = TradesWebcall.topicName;
        CloudQueue queue = CloudQueueFactory.create(queueName);
        Message message = new Message();
        message.setMessageBody(JacksonUtils.mapToJson(map).getBytes());
        if(!StringUtil.isEmpty(time)){
            message.setDelaySeconds(time);//设置消息延时，单位是秒
        }
        try {
            Message result = queue.putMessage(message);
            logger.info("tradesId{} 加入消息队列成功! queueName:{} messageBody:{},messageId:{}", tradesId, queueName, message.getMessageBodyAsString(),message.getMessageId());
        }catch (Exception e) {
            logger.debug("发送到消息队列失败 body:{} error:{}", message.getMessageBodyAsString(), e.getMessage());
        }

    }
    /**
     * 记录每笔订单的支付佣金，支付完成之后需要调用该方法把支付佣金记录到b_trades表中(20170727产品定义去掉最低交易佣金为0.01元)
     *
     * @param siteId         商家ID
     * @param tradesId       订单交易ID
     * @param orderRealPrice 订单的实际支付金额
     * @param payType        支付的类型(yb_payplatform);ali (支付宝) ，wx (微信)， bil(快钱)， unionPay(银联)， health_insurance（医保），cash（现金）',
     * @return true：成功，false：失败
     */
    public boolean updateOrderPayCommission(int siteId, long tradesId, int orderRealPrice, String payType, int isServiceOrder,Trades trades) {

        int payCommissionInt = balanceService.getOrderPayCommission(siteId, orderRealPrice, payType);
        //如果支付佣金小于1分钱，则兜底为1分钱
        /*if (payCommissionInt < 1) {
            payCommissionInt = 1;
        }*/
        logger.info(tradesId+"------------------------------回调佣金" + payCommissionInt);
        //服务商订单
        if(isServiceOrder==1){
            String scene=trades.getTradesSource()+"";
            String deliveryType=trades.getPostStyle()+"";
            String payTypenew=trades.getTradeTypePayLine()+"";
            Map baseFeeMap=baseFeeService.getBaseFeeByCode(siteId,  scene,  deliveryType,  payTypenew);
            int result = Integer.parseInt( baseFeeMap.get("result")+"");
            if(result!=0){
                double baseFee = Double.parseDouble(baseFeeMap.get("feeRate")+"");
                int feeRule = Integer.parseInt( baseFeeMap.get("feeRule")+"");
                double payCommission = (double) orderRealPrice * (baseFee / 100);
                //费用规则（0-按订单实付金额，不含运费；1-按订单实付金额，含运费）
                if(feeRule==0){
                    payCommission = (double) (trades.getRealPay()-trades.getPostFee()) * (baseFee / 100);
                }
                //根据小数点后一位四舍五入
                BigDecimal bd = new BigDecimal(payCommission);
                //四舍五入后返回支付佣金(单位：分)
                int payCommissionIntnew = bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                //修改余额操作日志
                balanceService.insertBalanceDetail(siteId,2,payCommissionIntnew,"订单佣金",tradesId,null,null);
            }

        }
        //更新支付佣金到b_trades表
        int flag = tradesMapper.updatePayCommission(siteId, tradesId, payCommissionInt);
        if (flag == 1) {
            return true;
        }
        return false;
    }

    /**
     * 记录每笔订单的支付佣金，支付完成之后需要调用该方法把支付佣金记录到b_trades表中(20170727产品定义去掉最低交易佣金为0.01元)
     *
     * @param siteId         商家ID
     * @param tradesId       订单交易ID
     * @param orderRealPrice 订单的实际支付金额
     * @param payType        支付的类型(yb_payplatform);ali (支付宝) ，wx (微信)， bil(快钱)， unionPay(银联)， health_insurance（医保），cash（现金）',
     * @return true：成功，false：失败
     */
    public boolean updateOrderCashPayCommission(int siteId, long tradesId, int orderRealPrice, String payType,Trades trades) {

        //int payCommissionInt = balanceService.getOrderPayCommission(siteId,  orderRealPrice, payType);
        String scenes=trades.getTradesSource()+"";
        String deliveryType=trades.getPostStyle()+"";
        String payTypenew=trades.getTradeTypePayLine()+"";
        Map baseFeeMap=baseFeeService.getBaseFeeByCode(siteId,  scenes,  deliveryType,  payTypenew);
        int result = Integer.parseInt( baseFeeMap.get("result")+"");
        if(result!=0){
            double baseFee = Double.parseDouble(baseFeeMap.get("feeRate")+"");
            int feeRule = Integer.parseInt(baseFeeMap.get("feeRule")+"");
            double payCommission = (double) orderRealPrice * (baseFee / 100);
            if(feeRule==1){
                payCommission = (double) (trades.getRealPay()-trades.getPostFee()) * (baseFee / 100);
            }
            //根据小数点后一位四舍五入
            BigDecimal bd = new BigDecimal(payCommission);
            //四舍五入后返回支付佣金(单位：分)
            int payCommissionIntnew = bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            //修改余额操作日志
            balanceService.insertBalanceDetail(siteId,2,payCommissionIntnew,"订单佣金",tradesId,null,null);
        }
        return false;
    }
    //买家付款成功后，要推送 新订单提醒 到门店助手APP 该订单对应门店下所有店员 ；
    // 若该已付款订单，并没有被指定某个门店，在商家后台指定门店时 再推送
    // {"site_id":"1001","order_id":"1000011448018893293","assign_store_id":"49","source_store_id":"120","self_take_store_id":"49","self_take_code":"1127205324","status":"120"}
    @Async
    public void storeOrderRemind(Long tradesId) {
        try {
            Trades trades = tradesMapper.getTradesByTradesId(tradesId);
            if (trades != null && trades.getAssignedStores() != null && trades.getTradesSource().intValue() == 120) {//微信端下单
                List<String> storeAdminIds = storeAdminMapper.getStoreAdminIdsByStore(trades.getSiteId(), trades.getAssignedStores());
                if (storeAdminIds != null && storeAdminIds.size() != 0) {
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("tradesId", trades.getTradesId());
                    messageMap.put("siteId", trades.getSiteId());
                    messageMap.put("storeId", trades.getAssignedStores());
                    String messageStr = JSON.toJSONString(messageMap);
                    geTuiPush.noticeOtherAppOrderRemind(storeAdminIds, messageStr, "orderRemind");//个推的clientId根据设备信息生成，短期内某一设备标识一个clientId,不能区分店员，过设置storeId让app区分

                    Map<String, Object> extraMap = new HashMap<>();
                    extraMap.put("site_id", trades.getSiteId());
                    extraMap.put("order_id", trades.getTradesId());
                    extraMap.put("assign_store_id", trades.getAssignedStores());
                    extraMap.put("source_store_id", trades.getTradesStore());
                    extraMap.put("self_take_store_id", trades.getSelfTakenStore());
                    extraMap.put("self_take_code", trades.getSelfTakenCode());
                    extraMap.put("status", trades.getTradesStatus());
                    String extraStr = JSON.toJSONString(extraMap);

                    List<ChOrderRemind> chOrderReminds = chOrderRemindMapper.getChOrderRemindByOrderId(trades.getTradesId());
                    if (chOrderReminds != null && chOrderReminds.size() != 0) {//商家指派门店
                        chOrderRemindMapper.updateByOrderId(trades.getTradesId(), trades.getPostStyle(), extraStr);
                    } else {
                        ChOrderRemind chOrderRemind = new ChOrderRemind();
                        chOrderRemind.setOrderId(String.valueOf(trades.getTradesId()));
                        chOrderRemind.setPostStyle(trades.getPostStyle());
                        chOrderRemind.setExtra(extraStr);
                        chOrderRemindMapper.insertSelective(chOrderRemind);
                    }
                }
            }

            //网站后台 订单提醒 :: 指派的门店,对应 b_stores.id，当assigned_stores为0时就是说明是总部发的货, 默认 null
            //这里 原来网站后台 1分钟轮询一次
            if (trades != null) {
                if (trades.getAssignedStores() != null && trades.getAssignedStores() != 0) {
                    stringRedisTemplate.opsForValue().set("orderRemind_Store_" + trades.getSiteId() + trades.getAssignedStores(), String.valueOf(trades.getTradesId()));
                    if (System.currentTimeMillis() - trades.getPayTime().getTime() < 60000) {//若付款超过一分钟，且已指定门店，不对商家提醒订单
                        stringRedisTemplate.opsForValue().set("orderRemind_Site_" + trades.getSiteId(), String.valueOf(trades.getTradesId()));
                    }
                } else {
                    stringRedisTemplate.opsForValue().set("orderRemind_Site_" + trades.getSiteId(), String.valueOf(trades.getTradesId()));
                }
                logger.info("设置商家订单提醒记录：orderRemind_Site_" + trades.getSiteId() + "：" + stringRedisTemplate.opsForValue().get("orderRemind_Site_" + trades.getSiteId()));
                logger.info("设置门店订单提醒记录：orderRemind_Store_" + trades.getSiteId() + trades.getAssignedStores() + "：" + stringRedisTemplate.opsForValue().get("orderRemind_Store_" + trades.getSiteId() + trades.getAssignedStores()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("订单[{}]，订单提醒记录失败{}", tradesId, e);
        }
    }


}
