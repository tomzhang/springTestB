package com.jk51.modules.trades.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.random.RandomUtils;
import com.jk51.commons.sms.SysType;
import com.jk51.commons.string.ShortUrlUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.commons.time.TimeUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.BLogisticsOrder;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.Stores;
import com.jk51.model.YbMeta;
import com.jk51.model.coupon.requestParams.UpdateOrderPriceParams;
import com.jk51.model.distribute.*;
import com.jk51.model.merchant.MerchantExt;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.order.*;
import com.jk51.modules.account.service.SettingDealTimeService;
import com.jk51.modules.authority.mapper.ManagerMapper;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.modules.balance.service.BaseFeeService;
import com.jk51.modules.concession.service.ConcessionResultHandler;
import com.jk51.modules.coupon.service.CouponDetailService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.distribution.mapper.*;
import com.jk51.modules.es.entity.ReturnDto;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.grouppurchase.service.GroupPurChaseService;
import com.jk51.modules.im.event.PaySuccessEvent;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.integral.mapper.OffIntegralLogMapper;
import com.jk51.modules.integral.service.IntegerRuleService;
import com.jk51.modules.integral.service.IntegralLogService;
import com.jk51.modules.merchant.mapper.MerchantExtMapper;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.merchant.mapper.YbMetaMapper;
import com.jk51.modules.merchant.service.MerchantExtService;
import com.jk51.modules.offline.service.ErpToolsService;
import com.jk51.modules.offline.service.OfflineOrderService;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.order.service.OrderPayService;
import com.jk51.modules.pay.exception.PayException;
import com.jk51.modules.pay.service.PayService;
import com.jk51.modules.pay.service.ali.AliPayApi;
import com.jk51.modules.pay.service.ali.request.AliRequestParam;
import com.jk51.modules.privatesend.core.AliPrivateSend;
import com.jk51.modules.privatesend.core.PrivateSend;
import com.jk51.modules.promotions.service.PromotionsOrderService;
import com.jk51.modules.registration.mapper.ServceOrderMapper;
import com.jk51.modules.sms.service.CommonService;
import com.jk51.modules.sms.smsConfig.SmsEnum;
import com.jk51.modules.tpl.mapper.BLogisticsOrderMapper;
import com.jk51.modules.tpl.request.ElemeCreateOrderRequest;
import com.jk51.modules.tpl.service.EleService;
import com.jk51.modules.trades.consumer.TradeMsgType;
import com.jk51.modules.trades.consumer.TradesPaySuccess;
import com.jk51.modules.trades.event.factory.CreateHandler;
import com.jk51.modules.trades.mapper.*;
import com.jk51.modules.treat.mapper.YbManagerMapper;
import com.jk51.mq.mns.CloudQueueFactory;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Service
public class TradesService {
    private static final Logger logger = LoggerFactory.getLogger(TradesService.class);

    //@Value("${erp.service_url}")
    //private String erp_service_url;
    @Autowired
    MerchantExtMapper merchantExtMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private TradesLogMapper tradesLogMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private TradesExtMapper tradesExtMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;
    @Autowired
    private CommonService commonService;
    @Autowired
    private MerchantExtService merchantExtService;

    @Autowired
    private StoresService storesService;
    @Autowired
    private EleService eleService;

    @Autowired
    private CouponDetailService couponDetailService;

    @Autowired
    private IntegralLogService integralLogService;

    @Autowired
    private SettingDealTimeService settingDealTimeService;

    @Autowired
    private YbMetaMapper ybMetaMapper;


    @Autowired
    private StoresMapper storesMapper;

    @Autowired
    private BLogisticsOrderMapper bLogisticsOrderMapper;
    @Autowired
    private StockupMapper stockupMapper;
    @Autowired
    private ServceOrderMapper servceOrderMapper;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private OffIntegralLogMapper offIntegralLogMapper;
    @Autowired
    private RewardMapper rewardMapper;

    @Autowired
    private PrivateSend privateSend;

    @Autowired
    private AliPrivateSend aliPrivateSend;

    @Value("${trades.trades_end}")
    private Integer tradesEnd;

    @Autowired
    private CreateHandler createHandler;


    @Autowired
    private DistributorMapper distributorMapper;

    @Autowired
    private PayService payService;


    @Autowired
    AliPayApi aliPayApi;

    @Autowired
    IntegerRuleService integralRuleService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    private CouponSendService couponSendService;
    @Autowired
    private PromotionsOrderService promotionsOrderService;

    @Autowired
    RecruitMapper recruitMapper;

    @Autowired
    GoodsDistributeMapper goodsDistributeMapper;

    @Autowired
    RewardTemplateMapper rewardTemplateMapper;
    @Autowired
    private YbMerchantMapper ybMerchantMapper;
    @Autowired
    private YbManagerMapper ybManagerMapper;

    @Autowired
    private ManagerMapper managerMapper;

    @Autowired
    private ErpToolsService erpToolsService;

    @Autowired
    private TradesUpdatePriceLogMapper tradesUpdatePriceLogMapper;
    @Autowired
    private GroupPurChaseService groupPurChaseService;
    @Autowired
    private ConcessionResultHandler concessionResultHandler;
    @Autowired
    private TradesDeliveryService tradesDeliveryService;
    @Autowired
    private OfflineOrderService offlineOrderService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private BaseFeeService baseFeeService;

    /**
     * 支付付成功订单处理
     *
     * @param trades
     */
    public boolean paySuccessCallback(Trades trades) throws BusinessLogicException {
        logger.info("{} 更新付款订单状态发生消息", trades.getTradesId());
        String paySuccessCallbackId=stringRedisTemplate.opsForValue().get(trades.getTradesId()+ "_paySuccessCallback");
        if (isWaitPaying(trades)&&StringUtil.isEmpty(paySuccessCallbackId)) {
            stringRedisTemplate.opsForValue().set(trades.getTradesId()+ "_paySuccessCallback","1", 24, TimeUnit.DAYS);
            trades.setTradesStatus(CommonConstant.WAIT_SELLER_SHIPPED);
            trades.setIsPayment(CommonConstant.IS_PAYMENT_ONE);
            trades.setSettlementStatus(CommonConstant.SETTLEMENT_STATUS_WAIT);
            // 直购
            if (trades.getPostStyle() == CommonConstant.POST_STYLE_DIRECT_PURCHASE) {
                /*if (trades.getPayStyle().equals(PayConstant.PAY_STYLE_CASH) || trades.getPayStyle().equals(PayConstant.PAY_STYLE_HEALTH_INSURANCE)) {
                    trades.setPlatSplit(0);
                }*/
                if (StringUtil.isEmpty(trades.getTradeTypePayLine())) {
                    trades.setTradeTypePayLine(0);
                    logger.info("setTradeTypePayLine 异常不能为零：{} ", trades);
                }
                trades.setTradesStatus(CommonConstant.HAVE_TAKE_GOODS);
                this.updateDirectPurchaseStatus(trades, CommonConstant.WAIT_PAYMENT_BUYERS, CommonConstant.SOURCE_BUSINESS_TAKE_GOOD);
            } else if (trades.getPostStyle() == CommonConstant.POST_STYLE_EXTRACT) {//自提
                this.updatePayStatus(trades, CommonConstant.WAIT_PAYMENT_BUYERS, CommonConstant.SOURCE_BUSINESS_WAIT_READY);
            } else if (trades.getPostStyle() == CommonConstant.POST_STYLE_DOOR) {//送货上门
                this.updateStatusToStock(trades, CommonConstant.WAIT_PAYMENT_BUYERS, CommonConstant.SOURCE_BUSINESS_WAIT_READY);
            } else {
                logger.debug("未知订单");
                return false;
            }
            try {
                integralRuleService.integralByOrderMulti(trades);
            } catch (Exception e) {
                logger.debug("购物送积分{}", e);
            }
            try {
                orderPayService.updateOrderPayCommission(trades.getSiteId(), trades.getTradesId(), trades.getRealPay(), trades.getPayStyle(), trades.getIsServiceOrder(), trades);
            } catch (Exception e) {
                logger.debug("记录每笔订单的支付佣金，支付完成之后需要调用该方法把支付佣金记录到b_trades表中失败，错误：" + e);
            }
            try {
                applicationContext.publishEvent(new PaySuccessEvent(this, trades));//订单提醒
            } catch (Exception e) {
                logger.debug("推送消息异常{}", e);
            }

            try {
                logger.info("发放优惠券{}", trades.getTradesId().toString());
                couponSendService.sendCouponByPay(trades.getTradesId().toString());
            } catch (Exception e) {
                logger.debug("发放优惠券处理付款订单{}出错{}", trades.getTradesId(), e);
            }

            try {
                logger.info("拼团订单状态修改{}", trades.getTradesId().toString());
                groupPurChaseService.updateGroupPurchaseStatus(trades.getTradesId().toString(), trades.getSiteId());
            } catch (Exception e) {
                logger.debug("拼团订单状态处理出错{}", trades.getTradesId(), e);
            }

            try {
                offlineOrderService.erpOrdersService(trades.getSiteId(), trades.getTradesId());
                sendMqMessage(trades);  //放入消息队列
            } catch (Exception e) {
                logger.debug("修改会员信息放入消息队列处理付款订单{}出错{}", trades.getTradesId(), e);
            }

            try {
                logger.info("支付成功后短信提醒{}", trades.getTradesId());
                //支付成功后短信提醒
                sendPaySuccessMsg(trades);
            } catch (Exception e) {
                logger.debug("支付成功后短信提醒{}出错{}", trades.getTradesId(), e);
            }

            try {
                logger.info("支付后电话提醒开始{}", trades.getTradesId());
                //支付后电话提醒，加入消息队列
                orderPayService.sendMqMessage(trades.getTradesId(), null, 1);
            } catch (Exception e) {
                logger.debug("支付后电话提醒{}出错{}", trades.getTradesId(), e);
            }
            return true;
        } /*else {
            try {
                logger.info("{} 异常订单", trades.getTradesId());
                Map<String, Object> params = new HashMap<String, Object>() {{
                    put("trades_id", trades.getTradesId());
                    put("site_id", trades.getSiteId());
                    put("old_trades_status", trades.getTradesStatus());
                    put("pay_style", trades.getPayStyle());
                    put("pay_number", trades.getPayNumber());
                }};
                tradesMapper.insertExceptionLog(params);
            } catch (Exception e) {
                logger.error("支付异常记录{}出错{}", trades.getTradesId(), e);
            }
        }*/
        return false;
    }

    /**
     * 订单状态是否等待支付
     *
     * @param trades
     * @return true 状态为未支付
     */
    private boolean isWaitPaying(final Trades trades) {
        return trades.getTradesStatus() == CommonConstant.WAIT_PAYMENT_BUYERS;
    }

    public void sendMqMessage(Trades trades) {
        Map<String, Object> map = new HashedMap();
        map.put("tradesId", trades.getTradesId());
        map.put("type", TradeMsgType.TRADES_PAY_SUCCESS);
        String queueName = TradesPaySuccess.topicName;
        CloudQueue queue = CloudQueueFactory.create(queueName);
        Message message = new Message();
        message.setMessageBody(JacksonUtils.mapToJson(map).getBytes());
        try {
            Message result = queue.putMessage(message);
            logger.info("tradesId{} 加入消息队列成功! queueName:{} messageBody:{},messageId:{}", trades.getTradesId(), queueName, message.getMessageBodyAsString(), message.getMessageId());
        } catch (Exception e) {
            logger.debug("发送到消息队列失败 body:{} error:{}", message.getMessageBodyAsString(), e.getMessage());
        }

    }

    public void saveIntegral(Trades trades, JSONObject jsonObject) {
        logger.info("购物送积分：" + jsonObject.toString());
        if ("success".equals(jsonObject.getString("status")) && jsonObject.containsKey("integrate")) {
            tradesExtMapper.udpateIntegralAward(trades.getTradesId(), jsonObject.getInteger("integrate"));
        }
    }

    /**
     * 支付成功发送短信
     *
     * @param trades
     */
    public void sendPaySuccessMsg(Trades trades) {
        //APP下送货上门和门店自提订单给用户发送信息提醒
        logger.info("APP下送货上门和门店自提订单给用户发送信息提醒-TradesSource:{}，TradesSource:{}，PostStyle:{}，", trades.getTradesSource(), trades.getStoreUserId(), trades.getPostStyle());
        if ((trades.getTradesSource() != null && trades.getTradesSource() == 130 && trades.getStoreUserId() != null) && (trades.getPostStyle() == 150)) {
            List<StoreAdminExt> storeAdminExt = storeAdminExtMapper.getStoreAdminExtBySiteIdAndStoreAdminId(trades.getSiteId(), trades.getStoreUserId());
            Member member = memberMapper.getMember(trades.getSiteId(), trades.getBuyerId());
            logger.info("APP下送货上门,Mobile:{},storeAdminExt{}", member.getMobile(), storeAdminExt);
            if (!StringUtil.isEmpty(storeAdminExt) && storeAdminExt.size() == 1 && !StringUtil.isEmpty(member)) {
                Stores stores = storesService.getStore(storeAdminExt.get(0).getStore_id(), trades.getSiteId());
                logger.info("APP下送货上门,Tel:{},", stores.getTel());
                commonService.SendMessage(commonService.transformParam(trades.getSiteId(), member.getMobile(), null,SysType.NEW_ORDER_FOR_CUSTOMER,
                    SmsEnum.ORDER_SMS_APP,trades.getTradesId().toString(),new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new Date()), stores.getTel()));

            }
        }
        if ((trades.getTradesSource() != null && trades.getTradesSource() == 130 && trades.getStoreUserId() != null) && (trades.getPostStyle() == 160)) {
            //List<StoreAdminExt> storeAdminExt = storeAdminExtMapper.getStoreAdminExtBySiteIdAndStoreAdminId(trades.getSiteId(), trades.getStoreUserId());
            Member member = memberMapper.getMember(trades.getSiteId(), trades.getBuyerId());
            logger.info("APP下自提订单,Mobile:{}", member.getMobile());
            if (!StringUtil.isEmpty(member)) {
                Stores stores = storesService.getStore(trades.getSelfTakenStore(), trades.getSiteId());
                logger.info("APP下自提订单,Tel:{},", stores.getTel());
                //commonService.ORDER_SMSNew(trades.getSiteId(), member.getMobile(), trades.getTradesId().toString(), stores.getTel());
                YbMerchant ybMerchant = selectMerchantfo(String.valueOf(trades.getSiteId()));
                String urlOld = getOrderDUrlNewZT(trades.getSiteId(), trades.getTradesId());
                String url = getOldUrl(trades.getSiteId(), urlOld);
                commonService.SendMessage(commonService.transformParam(trades.getSiteId(), member.getMobile(),null,SysType.LADING_ADRESS,
                    SmsEnum.LADING_CODE_NEW, ybMerchant.getShort_message_sign(), stores.getName(), stores.getAddress(), stores.getTel(), trades.getSelfTakenCode(), url));

            }
        }

        logger.info("支付成功发送短信sendPaySuccessMsg----------------TradesId:{}", trades.getTradesId());
        MerchantExt merchantExt = merchantExtService.findByMerchantId(trades.getSiteId());
        String ordertype = trades.getPostStyle() == CommonConstant.POST_STYLE_DOOR ? "送货上门" : (CommonConstant.POST_STYLE_EXTRACT == trades.getPostStyle() ? "自提" : "");
        Integer storeId = 0;
        if (trades.getPostStyle() == 150) {
            storeId = trades.getAssignedStores();//送货上门门店；为0说明是总部发货
        } else if (trades.getPostStyle() == 160) {//自提门店
            storeId = trades.getSelfTakenStore();
        }
        String phone = merchantExt.getOrder_remind_phones();
        if (storeId != 0) {//订单分到了对应门店
            Stores stores = storesService.getStore(storeId, trades.getSiteId());
            if (merchantExt.getOrder_phone_lert() != null && merchantExt.getOrder_phone_lert() == 1
                && merchantExt.getOrder_remind_phones() != null
                && ((trades.getPostStyle() == 150 && merchantExt.getOrder_lert().indexOf("150") > -1) || (trades.getPostStyle() == 160 && merchantExt.getOrder_lert().indexOf("160") > -1))) {
                if(phone!=null && !"".equals(phone)){
                    String[] phones = merchantExt.getOrder_remind_phones().split(",");//商户提醒短信
                    for (int i = 0; i < phones.length; i++){
                        commonService.SendMessage(commonService.transformParam(trades.getSiteId(), phones[i],null,SysType.NEW_ORDER_FOR_MERCHANT,
                            SmsEnum.ORDER_SMS, ordertype, trades.getTradesId().toString(), stores.getName()));
                    }
                }
            }
            if (StringUtils.isNotEmpty(stores.getRemind_mobile())
                && ((trades.getPostStyle() == 150 && stores.getOrder_lert().indexOf("150") > -1) || (trades.getPostStyle() == 160 && stores.getOrder_lert().indexOf("160") > -1))) {
                String[] re_phones = stores.getRemind_mobile().split(",");//门店提醒短信
                for (int i = 0; i < re_phones.length; i++) {
                    commonService.SendMessage(commonService.transformParam(trades.getSiteId(), re_phones[i], null,SysType.NEW_ORDER_FOR_CLERK,
                        SmsEnum.ORDER_SMS, ordertype, trades.getTradesId().toString(), ""));
                }
            }
        } else {//订单分到了总部
            if (merchantExt.getOrder_phone_lert() != null && merchantExt.getOrder_phone_lert() == 1
                && merchantExt.getOrder_remind_phones() != null
                && ((trades.getPostStyle() == 150 && merchantExt.getOrder_lert().indexOf("150") > -1) || (trades.getPostStyle() == 160 && merchantExt.getOrder_lert().indexOf("160") > -1))) {
                if(phone!=null && !"".equals(phone)) {
                    String[] phones = merchantExt.getOrder_remind_phones().split(",");//商户提醒短信
                    for (int i = 0; i < phones.length; i++) {
                        commonService.SendMessage(commonService.transformParam(trades.getSiteId(), phones[i], null, SysType.NEW_ORDER_FOR_MERCHANT,
                            SmsEnum.ORDER_SMS, ordertype, trades.getTradesId().toString(), ""));
                    }
                }
            }
        }
    }

    /**
     * 微信商城用户申请退款
     *
     * @param trades
     */
    public void sendApplyRefundMsg(Trades trades) {
        logger.info("用户申请退款发送短信sendPaySuccessMsg----------------TradesId:{}", trades.getTradesId());
        MerchantExt merchantExt = merchantExtService.findByMerchantId(trades.getSiteId());
        String ordertype = "退款中";
        Integer storeId = 0;
        if (trades.getPostStyle() == 150) {
            storeId = trades.getAssignedStores();//送货上门门店；为0说明是总部发货
        } else if (trades.getPostStyle() == 160) {//自提门店
            storeId = trades.getSelfTakenStore();
        }
        if (storeId != 0) {//订单分到了对应门店
            Stores stores = storesService.getStore(storeId, trades.getSiteId());
            if (merchantExt.getOrder_phone_lert() != null && merchantExt.getOrder_phone_lert() == 1
                && merchantExt.getOrder_remind_phones() != null && merchantExt.getOrder_lert().indexOf("400") > -1) {
                String[] phones = merchantExt.getOrder_remind_phones().split(",");//商户提醒短信
                for (int i = 0; i < phones.length; i++) {
                    commonService.SendMessage(commonService.transformParam(trades.getSiteId(), phones[i], null, SysType.NEW_ORDER_FOR_MERCHANT,
                        SmsEnum.ORDER_SMS, ordertype, trades.getTradesId().toString(), stores.getName()));
                }
            } else {
                logger.info("订单号为:{},商户设置不发退款提醒短信", trades.getTradesId());
            }
            if (StringUtils.isNotEmpty(stores.getRemind_mobile()) && stores.getOrder_lert().indexOf("400") > -1) {
                String[] re_phones = stores.getRemind_mobile().split(",");//门店提醒短信
                for (int i = 0; i < re_phones.length; i++) {
                    commonService.SendMessage(commonService.transformParam(trades.getSiteId(), re_phones[i], null,SysType.NEW_ORDER_FOR_CLERK,
                        SmsEnum.ORDER_SMS, ordertype, trades.getTradesId().toString(), ""));
                }
            } else {
                logger.info("订单号为:{},门店设置不发退款提醒短信", trades.getTradesId());
            }
        } else {//订单分到了总部
            if (merchantExt.getOrder_phone_lert() != null && merchantExt.getOrder_phone_lert() == 1
                && merchantExt.getOrder_remind_phones() != null && merchantExt.getOrder_lert().indexOf("400") > -1) {
                String[] phones = merchantExt.getOrder_remind_phones().split(",");//商户提醒短信
                for (int i = 0; i < phones.length; i++) {
                    commonService.SendMessage(commonService.transformParam(trades.getSiteId(), phones[i],null,SysType.NEW_ORDER_FOR_MERCHANT,
                        SmsEnum.ORDER_SMS, ordertype, trades.getTradesId().toString(), ""));
                }
            } else {
                logger.info("订单号为:{},商户设置不发退款提醒短信", trades.getTradesId());
            }
        }
    }

    /**
     * 支付成功发送消息处理分销订单
     *
     * @param trades
     * @return
     */
    private Boolean sendDistributeMsg(Trades trades) {
        try {
            if (trades.getDistributorId() == null || trades.getDistributorId() == 0) {
                logger.info("{} 不是分销订单,或者是分销订单但没有推荐人且用户还未成为分销商", trades.getTradesId());
                return false;
            } else {
                Map<String, Object> msg = new HashMap<>();
                msg.put("tradesId", trades.getTradesId());
                msg.put("type", TradeMsgType.TRADES_PAY_SUCCESS);
                this.createHandler.sendMqMsg(msg);
                return true;
            }
        } catch (Exception e) {
            logger.error("支付成功发送处理分销订单的消息失败:" + e);
            return false;
        }
    }

    /**
     * 通知蜂鸟发货
     *
     * @param storeId
     * @param tradesId
     * @return
     */
    public Map<String, Object> notifyExpress(int storeId, Long tradesId) throws BusinessLogicException {

        //b_logistics_order表存在记录则不再通知物流
        BLogisticsOrder flag = eleService.selectBLogisticsOrderByTradesId(String.valueOf(tradesId));
        if (!StringUtil.isEmpty(flag)) {
            Map result = new HashMap();
            result.put("error", "你已通知过o2o物流");
            return result;
        }

        QueryOrdersReq queryOrdersReq = new QueryOrdersReq();
        queryOrdersReq.setTradesId(Long.toString(tradesId));
        List<Trades> trades = this.getTrades(queryOrdersReq);
        if (trades != null) {
            Trades trade = trades.get(0);
            Stores stores = storesService.getStore(storeId, trade.getSiteId());
            List<Orders> orderss = trade.getOrdersList();
            ElemeCreateOrderRequest.ItemsJson[] items = new ElemeCreateOrderRequest.ItemsJson[orderss.size()];
            for (int i = 0; i < orderss.size(); ++i) {
                Orders orders = orderss.get(i);
                ElemeCreateOrderRequest.ItemsJson item = new ElemeCreateOrderRequest.ItemsJson();
                item.setItem_name(orders.getGoodsTitle());
                item.setItem_quantity(orders.getGoodsNum());
                item.setItem_price(new BigDecimal(orders.getGoodsPrice() / 100));
                item.setItem_actual_price(new BigDecimal(orders.getGoodsPrice() / 100));
                item.setIs_agent_purchase(0);
                item.setIs_need_package(0);
                items[i] = item;
            }
            return eleService.MycreateOrder(trade, stores, items);
        }
        return null;
    }

    /**
     * 调度发货
     *
     * @param storeId
     * @param tradesId
     * @return
     */
    public Map<String, Object> notifyDelivery(int storeId, Long tradesId) throws BusinessLogicException {

        //b_logistics_order表存在记录则不再通知物流
        BLogisticsOrder flag = eleService.selectBLogisticsOrderByTradesId(String.valueOf(tradesId));
        if (!StringUtil.isEmpty(flag)) {
            Map result = new HashMap();
            result.put("error", "你已通知过o2o物流");
            return result;
        }

        QueryOrdersReq queryOrdersReq = new QueryOrdersReq();
        queryOrdersReq.setTradesId(Long.toString(tradesId));
        List<Trades> trades = this.getTrades(queryOrdersReq);
        if (trades != null) {
            Trades trade = trades.get(0);
            Stores stores = storesService.getStore(storeId, trade.getSiteId());
//            List<Orders> orderss = trade.getOrdersList();
//            ElemeCreateOrderRequest.ItemsJson[] items = new ElemeCreateOrderRequest.ItemsJson[orderss.size()];
//            for (int i = 0; i < orderss.size(); ++i) {
//                Orders orders = orderss.get(i);
//                ElemeCreateOrderRequest.ItemsJson item = new ElemeCreateOrderRequest.ItemsJson();
//                item.setItem_name(orders.getGoodsTitle());
//                item.setItem_quantity(orders.getGoodsNum());
//                item.setItem_price(new BigDecimal(orders.getGoodsPrice() / 100));
//                item.setItem_actual_price(new BigDecimal(orders.getGoodsPrice() / 100));
//                item.setIs_agent_purchase(0);
//                item.setIs_need_package(0);
//                items[i] = item;
//            }
            return tradesDeliveryService.DeliveryDispatch(trade, stores);
        }
        return null;
    }

    /**
     * 蜂鸟确认收货业务处理
     *
     * @param tradesId
     */
    @Transactional
    public void confirmReceived(Long tradesId) throws BusinessLogicException {
        try {
            List<Orders> orders = getStatus(tradesId, CommonConstant.HAVE_SHIPPED);
            Trades trades = getTradesByTradesId(tradesId);
            trades.setTradesStatus(CommonConstant.STORE_RECEIVED);
            if (orders != null && orders.size() > 0) {
                ordersMapper.updateOrdersStatus(orders, CommonConstant.STORE_RECEIVED);
                tradesMapper.updateConfirmStatus(trades);
                addTradesLogs(trades, CommonConstant.HAVE_SHIPPED, CommonConstant.SOURCE_BUSINESS_FENGNIAO);
                offlineOrderService.pushOrder_pingAN(trades.getSiteId(), trades, 1);
            } else {
                logger.info("订单[{}],蜂鸟确认收货失败，无法更新订单明细状态", trades.getTradesId());

            }
        } catch (Exception e) {
            logger.error("订单[{}]，蜂鸟确认收货失败{}", tradesId, e);
            throw new BusinessLogicException(e);
        }
    }


    public Trades getTradesByTradesId(Long tradesId) {
        return tradesMapper.getTradesByTradesId(tradesId);
    }

    /**
     * 用户未付款前 取消订单
     *
     * @param trades
     * @param oldTradesStatus
     * @param sourceBusiness  业务来源
     * @return
     */
    @Transactional
    public void closeTrades(Trades trades, int oldTradesStatus, String sourceBusiness) throws BusinessLogicException {
        logger.info("lll业务来源订单[{}],取消订单失败，无法更新订单明细状态:{},oldTradesStatus:{}", trades.getTradesId(), sourceBusiness, oldTradesStatus);
        try {
            List<Orders> orders = getStatus(trades.getTradesId(), oldTradesStatus);
            if (orders != null && orders.size() > 0) {
                tradesMapper.updateUseToCLoseTrades(trades);
                ordersMapper.updateOrdersStatus(orders, trades.getTradesStatus());
                TradesExt tradesExt = getTradesExtByTradesId(trades.getTradesId());
                //如果退积分
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("siteId", trades.getSiteId());
                map.put("memberId", trades.getBuyerId());
                map.put("isBack", 1);
                map.put("tradesId", trades.getTradesId());
                map.put("integralAdd", tradesExt.getIntegralUsed());
                map.put("integralDiff", tradesExt.getIntegralFinalAward());
                String result = integralLogService.integraUpdate(map);
                if ("faild".equals(result)) {
                    logger.info("积分退还失败，订单号[{}]", trades.getTradesId());
                }
                logger.info("zw去取消订单调用退优惠券接口，订单号[{}]", trades.getTradesId());
                //退优惠券
                Integer scene = 0;
                couponDetailService.returnCoupon(String.valueOf(trades.getTradesId()), 1, scene);
                //取消订单改优惠活动状态
                promotionsOrderService.refundPromotions(String.valueOf(trades.getTradesId()), 0);
                addTradesLogs(trades, oldTradesStatus, sourceBusiness);
                //当订单被取消时或订单发货前退款，且订单有赠送商品时，尝试恢复赠品
                concessionResultHandler.tryRestoreInventoryByTradesId(trades.getTradesId(), trades.getSiteId());
                logger.info("l结束去取消订单调用退优惠券接口，订单号[{}]", trades.getTradesId());
            } else {
                logger.info("订单[{}],取消订单失败，无法更新订单明细状态", trades.getTradesId());
                throw new BusinessLogicException("订单[" + trades.getTradesId() + "],取消订单失败，无法更新订单明细状态：" + sourceBusiness);
            }
        } catch (Exception e) {
            logger.error("订单[{}]，取消订单失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);

        }

    }

    /**
     * 更新直购订单状态
     *
     * @param trades
     * @param old_trades_status
     * @param source_business
     * @return
     */
    @Transactional(rollbackFor = BusinessLogicException.class)
    public void updateDirectPurchaseStatus(Trades trades, int old_trades_status, String source_business) throws BusinessLogicException {
        try {
            ordersMapper.updateStatusByTradesId(trades.getTradesId(), trades.getTradesStatus());
            tradesMapper.updateToDirectPurchaseStatus(trades);
            addTradesLogs(trades, old_trades_status, source_business);
        } catch (Exception e) {
            logger.error("订单[{}]，更新直购订单状态失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }
    }

    /**
     * 送货上门 更新已发货状态
     *
     * @param trades
     * @param old_trades_status
     * @param source_business
     * @return
     */
    @Transactional
    public void updateStatusToSend(Trades trades, int old_trades_status, String source_business) throws BusinessLogicException {
        try {
            List<Orders> orders = getStatus(trades.getTradesId(), old_trades_status);
            if (orders != null && orders.size() > 0) {
                ordersMapper.updateOrdersStatus(orders, trades.getTradesStatus());
                tradesMapper.updateStatusToSend(trades);
                addTradesLogs(trades, old_trades_status, source_business);

            } else {
                logger.info("订单[{}],送货上门 更新发货状态失败，无法更新订单明细状态", trades.getTradesId());
                throw new BusinessLogicException("订单[" + trades.getTradesId() + "],送货上门 更新发货状态失败，无法更新订单明细状态");
            }
        } catch (Exception e) {
            logger.error("订单[{}]，更新发货状态失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }
    }

    /**
     * 送货上门 更新到待备货 将资金结算状态改为待结算
     *
     * @param trades
     * @param old_trades_status
     * @param source_business
     * @return
     */
    @Transactional
    public void updateStatusToStock(Trades trades, int old_trades_status, String source_business) throws BusinessLogicException {
        try {
            ordersMapper.updateStatusByTradesId(trades.getTradesId(), trades.getTradesStatus());
            tradesMapper.updateStatusToStock(trades);
            addTradesLogs(trades, old_trades_status, source_business);
        } catch (Exception e) {
            logger.error("订单[{}]，送货上门更新到待备货失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }
    }

    /**
     * 送货上门 更新到已备货
     *
     * @param trades
     * @param old_trades_status
     * @param source_business
     */
    @Transactional
    public void updateStatusYiStock(Trades trades, int old_trades_status, String source_business) throws BusinessLogicException {
        try {
            List<Orders> orders = getStatus(trades.getTradesId(), old_trades_status);

            if (orders != null && orders.size() > 0) {
                ordersMapper.updateOrdersStatus(orders, trades.getTradesStatus());
                tradesMapper.updateStatusAlreadyStock(trades);
                addTradesLogs(trades, old_trades_status, source_business);


            } else {
                logger.info("订单[{}],送货上门更新到已备货失败，无法更新订单明细状态", trades.getTradesId());
                throw new BusinessLogicException("订单[" + trades.getTradesId() + "],送货上门更新到已备货失败，无法更新订单明细状态");
            }
        } catch (Exception e) {
            logger.error("订单[{}]，送货上门更新到已备货失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }

    }

    /**
     * 用户或门店点击确认收货
     *
     * @param trades
     * @return
     */
    @Transactional
    public void updateConfirmStatus(Trades trades, int old_trades_status, String source_business) throws BusinessLogicException {
        try {
            List<Orders> orders = getStatus(trades.getTradesId(), old_trades_status);
            trades.setTradesStatus(trades.getTradesStatus() != null ? trades.getTradesStatus() : CommonConstant.STORE_RECEIVED);
            if (orders != null && orders.size() > 0) {
                ordersMapper.updateOrdersStatus(orders, trades.getTradesStatus() != null ? trades.getTradesStatus() : CommonConstant.STORE_RECEIVED);
                tradesMapper.updateConfirmStatus(trades);
                addTradesLogs(trades, old_trades_status, source_business);
                //订单签收消息
                sendOrderSign(trades);
                offlineOrderService.pushOrder_pingAN(trades.getSiteId(), trades,1);
            } else {
                logger.info("订单[{}],用户或门店点击确认收货失败，无法更新订单明细状态", trades.getTradesId());
                throw new BusinessLogicException("订单[" + trades.getTradesId() + "],用户或门店点击确认收货失败，无法更新订单明细状态");
            }
        } catch (Exception e) {
            logger.error("订单[{}]，用户或门店点击确认收货失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }
    }

    /**
     * 用户或门店点击确认收货
     *
     * @param trades
     * @return
     */
    @Transactional
    public void updateConfirmStatusExtra(Trades trades, int old_trades_status, String source_business) throws BusinessLogicException {

        try {
            List<Orders> orders = getStatus(trades.getTradesId(), old_trades_status);
            if (orders != null && orders.size() > 0) {
                ordersMapper.updateOrdersStatus(orders, CommonConstant.HAVE_SHIPPED);
                trades.setTradesStatus(CommonConstant.HAVE_SHIPPED);
                trades.setShippingStatus(CommonConstant.WAIT_SELLER_SHIPPED);
                tradesMapper.o2oToSend(trades);
                addTradesLogs(trades, old_trades_status, CommonConstant.SOURCE_BUSINESS_SHIPPED);

            } else {
                logger.info("订单[{}],送货上门 更新发货状态失败，无法更新订单明细状态", trades.getTradesId());
                throw new BusinessLogicException("订单[" + trades.getTradesId() + "],确认发货失败，无法更新订单明细状态");
            }
        } catch (Exception e) {
            logger.error("订单[{}]，更新发货状态失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }
    }


    /**
     * 未收货 取消发货o2o
     */
    @Transactional
    public void cancelShipping(Trades trades) throws BusinessLogicException {
        List<Orders> orders = getStatus(trades.getTradesId(), CommonConstant.HAVE_SHIPPED);
        if (orders != null && orders.size() > 0) {
            ordersMapper.updateOrdersStatus(orders, CommonConstant.WAIT_SELLER_SHIPPED);
            trades.setTradesStatus(CommonConstant.WAIT_SELLER_SHIPPED);
            trades.setShippingStatus(CommonConstant.SHIPPED_WAIT_DELIVERY);
            tradesMapper.cancelShipping(trades);
            try {
                addTradesLogs(trades, CommonConstant.HAVE_SHIPPED, "取消发货");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("订单[{}],送货上门 取消发货失败，无法更新订单明细状态", trades.getTradesId());
            throw new BusinessLogicException("订单[" + trades.getTradesId() + "],取消发货失败，无法更新订单明细状态");
        }
    }

    /**
     * 门店自提 更新为已付款状态 并 生成随机数字串
     *
     * @param trades
     * @param old_trades_status
     * @param source_business
     */
    @Transactional(rollbackFor = BusinessLogicException.class)
    public void updatePayStatus(Trades trades, int old_trades_status, String source_business) throws BusinessLogicException {
        try {
            ordersMapper.updateStatusByTradesId(trades.getTradesId(), trades.getTradesStatus());
            String barcode = generateBarcode(trades.getSiteId());
            trades.setSelfTakenCode(barcode);
            tradesMapper.updateGetToStoreForPayStatus(trades);
            addTradesLogs(trades, old_trades_status, source_business);
        } catch (Exception e) {
            logger.error("订单[{}]，门店自提更新为已付款状态失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }

    }

    /**
     * 门店自提  更新为待自提
     *
     * @param trades
     * @param old_trades_status 原交易状态
     * @param source_business   业务来源
     */
    @Transactional
    public void updateWaitZiTi(Trades trades, int old_trades_status, String source_business) throws BusinessLogicException {
        try {
            List<Orders> orders = getStatus(trades.getTradesId(), old_trades_status);
            if (orders != null && orders.size() > 0) {
                ordersMapper.updateOrdersStatus(orders, trades.getTradesStatus());
                tradesMapper.updateWaitExtract(trades);
                addTradesLogs(trades, old_trades_status, source_business);
                try {
                    Map<String, Object> tradeMap = tradesMapper.getUserByTrade(trades.getTradesId());
                    if (!StringUtil.isEmpty(tradeMap.get("post_style"))) {
                        String url = getOrderDUrl(trades.getSiteId(), (int) tradeMap.get("post_style"), trades.getTradesId());
                        String goods_title = tradeMap.get("goods_title").toString().indexOf(",") == -1 ? tradeMap.get("goods_title").toString() : tradeMap.get("goods_title").toString().split(",")[0] + "等";
                        if(!StringUtil.isEmpty(tradeMap.get("open_id"))){
                            privateSend.orderStoreToTake(trades.getSiteId(), tradeMap.get("open_id").toString(), url,
                                    "您购买的商品" + goods_title + "配货完成，可以提货了。", "",
                                    tradeMap.get("trades_id").toString(), trades.getSelfTakenCode(), Double.parseDouble(tradeMap.get("real_pay").toString()) / 100 + "元", tradeMap.get("store_name").toString() + "," + tradeMap.get("store_address").toString() + ",门店电话：" + tradeMap.get("tel").toString());
                        }
                        if(!StringUtil.isEmpty(tradeMap.get("ali_user_id"))){
                            aliPrivateSend.orderStoreToTake(trades.getSiteId(), tradeMap.get("ali_user_id").toString(), url,
                                "您购买的商品" + goods_title + "配货完成，可以提货了。", "",
                                tradeMap.get("trades_id").toString(), trades.getSelfTakenCode(), Double.parseDouble(tradeMap.get("real_pay").toString()) / 100 + "元", tradeMap.get("store_name").toString() + "," + tradeMap.get("store_address").toString() + ",门店电话：" + tradeMap.get("tel").toString());
                        }
                    }
                } catch (Exception e) {
                    logger.error("发送消息提醒失败：订单提货通知（门店自提通知）{}", trades.getTradesId(), e);
                }

            } else {
                logger.info("订单[{}],门店自提更新为待自提，无法更新订单明细状态", trades.getTradesId());
                throw new BusinessLogicException("订单[" + trades.getTradesId() + "],门店自提更新为待自提，无法更新订单明细状态");
            }
        } catch (Exception e) {
            logger.error("订单[{}]，门店自提更新为待自提失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }

    }

    /**
     * 更新为已自提
     *
     * @param trades
     * @param old_trades_status
     * @param source_business
     */
    @Transactional
    public void updateYiZiTi(Trades trades, int old_trades_status, String source_business) throws BusinessLogicException {
        try {
            List<Orders> orders = getStatus(trades.getTradesId(), old_trades_status);
            if (orders != null && orders.size() > 0) {
                ordersMapper.updateOrdersStatus(orders, trades.getTradesStatus());
                tradesMapper.updateAlreadyExtract(trades);
                addTradesLogs(trades, old_trades_status, source_business);
                offlineOrderService.pushOrder_pingAN(trades.getSiteId(), trades, 1);
            } else {
                logger.info("订单[{}],更新为已自提失败，无法更新订单明细状态", trades.getTradesId());
                throw new BusinessLogicException("订单[" + trades.getTradesId() + "],更新为已自提失败，无法更新订单明细状态");
            }
        } catch (Exception e) {
            logger.error("订单[{}]，更新为已自提失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }

    }

    /**
     * 商家同意退款
     *
     * @param trades
     * @param old_trades_status
     * @param source_business
     */

    public void updateRefundStatus(Trades trades, int old_trades_status, String source_business, int status, int siteId, int is_coupon, int is_integral, String refundSerialNo, int money) throws BusinessLogicException {
        try {
            trades.setRefundFee(money);
            List<Orders> orders = getStatus(trades.getTradesId(), old_trades_status);
            if (orders != null && orders.size() > 0) {
                ordersMapper.updateOrdersStatus(orders, trades.getTradesStatus());
                tradesMapper.updateRefundStatus(trades);
                addTradesLogs(trades, old_trades_status, source_business);
                refundMapper.updateStatus(String.valueOf(trades.getTradesId()), status, refundSerialNo, is_coupon, is_integral, money);  //更新退款记录
                TradesExt tradesExt = getTradesExtByTradesId(trades.getTradesId());
                //如果退积分
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("siteId", siteId);
                map.put("memberId", trades.getBuyerId());
                map.put("isBack", is_integral);
                map.put("tradesId", trades.getTradesId());
                map.put("integralAdd", tradesExt.getIntegralUsed());
                map.put("integralDiff", tradesExt.getIntegralFinalAward());
                map.put("money", money);
                map.put("realPay", trades.getRealPay());
                String result = integralLogService.integraUpdate(map);

               /* //推送积分变动情况
                Member member = memberMapper.getMember(trades.getSiteId(), trades.getBuyerId());
                Map<String, Object> maps = new HashMap<>();
                maps.put("tradesId", trades.getTradesId());
                maps.put("mobile", member.getMobile());
                maps.put("type", 1);
                maps.put("sumScore", +tradesExt.getIntegralFinalAward());
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                maps.put("create_time", sd.format(new Date()));
                maps.put("desc", "积分兑换订单退款，订单号：" + trades.getTradesId());
                erpToolsService.integralChange(trades.getSiteId(), maps);//推送积分变化到商户后台
                if ("faild".equals(result)) {
                    logger.info("积分退还失败，订单号[{}]", trades.getTradesId());
                }*/
                //如果退还优惠券
                Integer scene = 1;
                boolean flag = couponDetailService.returnCoupon(String.valueOf(trades.getTradesId()), is_coupon, scene);

                promotionsOrderService.refundPromotions(String.valueOf(trades.getTradesId()), 1);
               /* if (!flag) {
                    logger.info("订单[{}],优惠券退还失败", trades.getTradesId());
                    throw new BusinessLogicException("订单[" + trades.getTradesId() + "],,优惠券退还失败");
                }*/
                if (CommonConstant.SHIPPED_WAIT_DELIVERY == trades.getShippingStatus()) {
                    //当订单被取消时或订单发货前退款，且订单有赠送商品时，尝试恢复赠品
                    concessionResultHandler.tryRestoreInventoryByTradesId(trades.getTradesId(), trades.getSiteId());
                }
                //服务商模式订单退款，退佣金
                this.fwRefund(trades, money);

            } else {
                logger.info("订单[{}],退款失败，无法更新订单明细状态", trades.getTradesId());
                throw new BusinessLogicException("订单[" + trades.getTradesId() + "],退款失败，无法更新订单明细状态");

            }
        } catch (Exception e) {
            logger.error("订单[{}]，退款失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }

    }

    /**
     * 商家发起退款
     *
     * @param trades
     * @param old_trades_status
     * @param source_business
     * @param refund
     * @param is_coupon         0 不退优惠券  1退优惠券
     * @param is_integral       1 退积分  0 不退积分
     */
    @Transactional
    public void merchantRefund(Trades trades, int old_trades_status, String source_business, Refund refund, int is_coupon, int is_integral) throws BusinessLogicException {
        try {
            trades.setRefundFee(refund.getRealRefundMoney());
            List<Orders> orders = getStatus(trades.getTradesId(), old_trades_status);
            if (orders != null && orders.size() > 0) {
                ordersMapper.updateOrdersStatus(orders, trades.getTradesStatus());
                tradesMapper.updateRefundStatus(trades);
                addTradesLogs(trades, old_trades_status, source_business);  //
                Refund rf = refundMapper.getRefundListByTradesId(String.valueOf(trades.getTradesId()));
                if (rf == null) {
                    refundMapper.addRefund(refund);  //更新退款记录
                } else {
                    refundMapper.updateStatus(String.valueOf(trades.getTradesId()), refund.getStatus(), refund.getRefundSerialNo(), refund.getIsRefundCoupon(), refund.getIsRefundIntegral(), refund.getRealRefundMoney());  //更新退款记录
                }
                TradesExt tradesExt = getTradesExtByTradesId(trades.getTradesId());
                //如果退积分
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("siteId", trades.getSiteId());
                map.put("memberId", trades.getBuyerId());
                map.put("isBack", is_integral);
                map.put("tradesId", trades.getTradesId());
                map.put("siteId", trades.getSiteId());
                map.put("memberId", trades.getBuyerId());
                map.put("isBack", is_integral);
                map.put("tradesId", trades.getTradesId());
                map.put("integralDiff", tradesExt.getIntegralFinalAward());
                map.put("money", refund.getRealRefundMoney());
                map.put("realPay", trades.getRealPay());
                Map<String, Object> maps = new HashMap<>();
                if (tradesExt.getIntegralUsed() != null && tradesExt.getIntegralUsed() > 0) { //如果该笔订单是积分兑换商品，进行处理
                    Integer all_integral = 0;//订单总共耗费了积分数
                    Integer on_integral = 0;//订单消耗的线上积分数
                    Integer off_integral = 0;//订单消耗的线下积分数
                    Map<String, Object> integralTrades = offIntegralLogMapper.selectTradeBytradeId(trades.getSiteId(), trades.getTradesId());
                    if (!StringUtil.isEmpty(integralTrades)) {//积分信息推送
                        all_integral = Integer.valueOf(integralTrades.get("total_consum_integral").toString());
                        on_integral = Integer.valueOf(integralTrades.get("online_consum_integral").toString());
                        off_integral = Integer.valueOf(integralTrades.get("offline_consum_integral").toString());
                        map.put("integralAdd", -on_integral);
                    } else {
                        on_integral = -Integer.valueOf(tradesExt.getIntegralUsed());
                        map.put("integralAdd", tradesExt.getIntegralUsed());
                    }
                    //推送积分变动情况
                    Member member = memberMapper.getMember(trades.getSiteId(), trades.getBuyerId());
                    maps.put("tradesId", trades.getTradesId());
                    maps.put("mobile", member.getMobile());
                    maps.put("type", 1);
                    maps.put("sumScore", +tradesExt.getIntegralFinalAward() - on_integral - off_integral);
                    maps.put("on_costScore", +tradesExt.getIntegralFinalAward() - on_integral);
                    maps.put("off_costScore", -off_integral);
                    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    maps.put("create_time", sd.format(new Date()));
                    maps.put("desc", "积分兑换订单退款，订单号：" + trades.getTradesId());
                } else {//该笔订单是微信商城订单
                    map.put("integralAdd", tradesExt.getIntegralUsed());
                    //推送积分变动情况
                    Member member = memberMapper.getMember(trades.getSiteId(), trades.getBuyerId());
                    maps.put("tradesId", trades.getTradesId());
                    maps.put("mobile", member.getMobile());
                    maps.put("type", 1);
                    maps.put("sumScore", tradesExt.getIntegralFinalAward());
                    maps.put("on_costScore", -tradesExt.getIntegralFinalAward());
                    maps.put("off_costScore", 0);
                    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    maps.put("create_time", sd.format(new Date()));
                    maps.put("desc", "购物送积分订单退款，订单号：" + trades.getTradesId());
                }
                String result = integralLogService.integraUpdate(map);
                if ("faild".equals(result)) {
                    logger.info("积分退还失败，订单号[{}]", trades.getTradesId());
                } else {
                    if (Integer.parseInt(maps.get("sumScore").toString()) != 0) {
                        logger.info("推送订单退款信息{}", maps.toString());
                        erpToolsService.integralChange(trades.getSiteId(), maps);//推送积分变化到商户后台
                    }
                }
                //退优惠券
                Integer scene = 1;
                couponDetailService.returnCoupon(String.valueOf(trades.getTradesId()), is_coupon, scene);
                promotionsOrderService.refundPromotions(String.valueOf(trades.getTradesId()), 1);
                if (CommonConstant.SHIPPED_WAIT_DELIVERY == trades.getShippingStatus()) {
                    //当订单被取消时或订单发货前退款，且订单有赠送商品时，尝试恢复赠品
                    concessionResultHandler.tryRestoreInventoryByTradesId(trades.getTradesId(), trades.getSiteId());
                }
                //服务商模式订单退款，退佣金
                this.fwRefund(trades, refund.getRealRefundMoney());

            } else {
                logger.info("订单[{}],退款失败，无法更新订单明细状态", trades.getTradesId());
                throw new BusinessLogicException("订单号：[{}],退款失败，无法更新订单明细状态[" + trades.getTradesId() + "]");
            }
        } catch (Exception e) {
            logger.error("订单[{}]，退款失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }
    }

    /**
     * 商家拒绝退款
     *
     * @param tradesId
     * @param status
     * @throws BusinessLogicException
     */
    @Transactional
    public void refusedRefund(String tradesId, int status) throws BusinessLogicException {
        try {
            tradesMapper.updateRefuseRefundStatus(Long.parseLong(tradesId), status);
            refundMapper.refusedStatus(tradesId, status);  ////商家拒绝退款

        } catch (Exception e) {
            logger.error("订单[{}]，退款失败{}", tradesId, e);
            throw new BusinessLogicException(e);
        }

    }

    /**
     * 查询交易拓展记录
     *
     * @param tradesId
     * @return
     */
    public TradesExt getTradesExtByTradesId(Long tradesId) {
        return tradesExtMapper.getByTradesId(tradesId);
    }

    /**
     * 查询会员记录
     *
     * @param siteId
     * @param buyerId
     * @return
     */
    public Member getMember(Integer siteId, Integer buyerId) {
        return memberMapper.getMember(siteId, buyerId);
    }

    /**
     * 部分退款
     *
     * @param trades
     * @param old_trades_status
     * @param source_business
     * @param refund
     */
   /* @Transactional
    public Refund partRefund(Trades trades, int old_trades_status, String source_business, Refund refund) {
        List<Orders> orders = getStatus(trades.getTradesId(), old_trades_status);
        ordersMapper.updateOrdersStatus(orders, trades.getTradesStatus());
        tradesMapper.updateRefundStatus(trades);
        addTradesLogs(trades, old_trades_status, source_business);
        if (refund != null) {
            refundMapper.updateStatus(refund);  //更新退款记录
        }
        return selectRefundInfo(trades.getTradesId());
    }*/

    /**
     * 申请退款
     *
     * @param refund
     */
    public void applyRefund(Refund refund, Trades trades) {
        refundMapper.addRefund(refund);
        tradesMapper.updateIsRefund(trades);
    }

    /**
     * 查询退款详情
     *
     * @param tradesId
     * @return
     */
    public Refund selectRefundInfo(int siteId, String tradesId) {
        Refund refund = refundMapper.getByTradesId(siteId, tradesId);
        if (refund != null) {
            Map<String, Object> map = couponDetailService.findOrderCoupon(tradesId);
            YbMerchant ybMerchant = selectMerchantfo(siteId + "");
            if (!StringUtil.isEmpty(ybMerchant)) {
                map.put("servicePhone", ybMerchant.getService_phone());
            }
            refund.setMap(map);
        }
        return refund;
    }

    /**
     * 验证提货码
     *
     * @param siteId
     * @param self_taken_code 提货码
     * @return
     */
    public Trades validationBarCode(int siteId, String self_taken_code) {
        return tradesMapper.selectBarCode(siteId, self_taken_code);
    }

    /**
     * 生成提货码验证
     */
    public Trades validationBarCode2(int siteId, String self_taken_code) {
        return tradesMapper.selectBarCode2(siteId, self_taken_code);
    }

    /**
     * 超时未付款 系统取消
     */
    @Transactional
    public void systemCanel() {
        List<Merchant> merchants = tradesMapper.getMerchantInfo();
        //超时未付款，默认3天
        int systemCanelDay = 3;
        for (Merchant m : merchants) {
            YbMeta meta = settingDealTimeService.getDealTime(m.getMerchantId(), CommonConstant.META_KEY_CLOSE);
            if (meta == null) {
                logger.info("超时未付款,系统取消，未设置时间,商家ID：[{}]", m.getMerchantId());
                systemCanelDay = 3;
            } else {
                String result = meta.getMetaVal();
                systemCanelDay = Integer.parseInt(result);


            }
             /*Map<String, Object> map = new HashMap<String, Object>();
                map.put("source_business", CommonConstant.SOURCE_BUSINESS_SYSTEM_CANEL);
                map.put("old_trades_status", CommonConstant.WAIT_PAYMENT_BUYERS);
                map.put("new_trades_status", CommonConstant.SYSTEM_RECEIVED);
                map.put("siteId", m.getMerchantId());
                map.put("systemCanel", systemCanelDay);
                map.put("tradesEnd", tradesEnd);*/
            //tradesLogMapper.batchInsertToSystemCanel(map);  //日志

            Map<String, Object> mapTrades = new HashMap<String, Object>();
            mapTrades.put("tradesStatus", CommonConstant.UNPAID_OVERTIME);
            mapTrades.put("settlementStatus", CommonConstant.SETTLEMENT_STATUS_MAY);
            mapTrades.put("systemCanel", systemCanelDay);
            mapTrades.put("siteId", m.getMerchantId());
            mapTrades.put("tradesEnd", tradesEnd);
            tradesMapper.updateTimeoutBySystemCanel(mapTrades);

            try {
                List<Map<String, Object>> tradesList = tradesMapper.selectTimeoutBySystemCanelNew(m.getMerchantId(), systemCanelDay - 1);
                for (Map<String, Object> tradeMap : tradesList) {
                    String has = stringRedisTemplate.opsForValue().get(tradeMap.get("trades_id") + "_orderToPayNotice");
                    if (!StringUtil.isEmpty(tradeMap.get("post_style")) && StringUtil.isEmpty(has)) {
                        String url = getOrderDUrl(m.getMerchantId(), (int) tradeMap.get("post_style"), Long.parseLong(tradeMap.get("trades_id").toString()));
                        String goods_title = tradeMap.get("goods_title").toString().indexOf(",") == -1 ? tradeMap.get("goods_title").toString() : tradeMap.get("goods_title").toString().split(",")[0] + "等";
                        if (!StringUtil.isEmpty(tradeMap.get("open_id"))) {
                           privateSend.orderToPayNotice(m.getMerchantId(), tradeMap.get("open_id").toString(), url,
                                    "您好，您有一笔订单还未支付，商品详情：" + goods_title + "，超过支付时间将被自动取消，赶快支付吧~", "点击进入订单详情完成支付",
                                    tradeMap.get("trades_id").toString(), Double.parseDouble(tradeMap.get("real_pay").toString()) / 100 + "元");
                            }
                        if (!StringUtil.isEmpty(tradeMap.get("ali_user_id")) ) {
                           aliPrivateSend.orderToPayNotice(m.getMerchantId(), tradeMap.get("ali_user_id").toString(), url,
                                    "您好，您有一笔订单还未支付，商品详情：" + goods_title + "，超过支付时间将被自动取消，赶快支付吧~", "点击进入订单详情完成支付",
                                    tradeMap.get("trades_id").toString(), Double.parseDouble(tradeMap.get("real_pay").toString()) / 100 + "元",TimeUtil.getTimes(tradeMap.get("create_time")));
                        }
                        stringRedisTemplate.opsForValue().set(tradeMap.get("trades_id") + "_orderToPayNotice", "1", 25, TimeUnit.HOURS);
                    }
                    //当订单被取消时或订单发货前退款，且订单有赠送商品时，尝试恢复赠品
                    concessionResultHandler.tryRestoreInventoryByTradesId((Long) tradeMap.get("trades_id"), m.getMerchantId());
                }
            } catch (Exception e) {
                logger.error("订单待付款提醒失败{}", e);
            }
        }

    }

/*    public void systemConfirm() {
        int queryGoodsIdByCond = tradesMapper.selectTimeoutBySystemCanel(systemCanelDay);
        int page = queryGoodsIdByCond % 1000 > 0 ? queryGoodsIdByCond / 1000 + 1 : queryGoodsIdByCond / 1000;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("source_business", CommonConstant.SOURCE_BUSINESS_SYSTEM_CANEL);
        map.put("old_trades_status", CommonConstant.WAIT_PAYMENT_BUYERS);
        map.put("new_trades_status", CommonConstant.SYSTEM_RECEIVED);
        map.put("systemConfirm", systemConfirmDay);
        for (int i = 1; i <= page; i++) {
            map.put("pageNum", i);
            tradesLogMapper.batchInsertToSystemConfirm(map);  //日志
        }
        Map<String, Object> mapTrades = new HashMap<String, Object>();
        mapTrades.put("trades_status", CommonConstant.SYSTEM_RECEIVED);
        mapTrades.put("settlement_status", CommonConstant.SETTLEMENT_STATUS_MAY);
        mapTrades.put("systemConfirm", systemConfirmDay);
        mapTrades.put("postStyle", CommonConstant.POST_STYLE_EXTRACT);
        tradesMapper.updateTimeoutBySystemCanel(mapTrades);

    }*/

    /**
     * 送货上门  系统确认收货
     */
    @Transactional
    public void systemDelivery() {
        List<Merchant> merchants = tradesMapper.getMerchantInfo();
        //送货上门  系统确认收货，默认7天
        int systemDeliveryDay = 7;
        for (Merchant m : merchants) {
            YbMeta meta = settingDealTimeService.getDealTime(m.getMerchantId(), CommonConstant.META_KEY_AFFIRM);
            if (meta == null) {
                logger.info("送货上门 ,系统确认收货，未设置时间,商家ID：[{}]", m.getMerchantId());
                systemDeliveryDay = 7;
            } else {
                String result = meta.getMetaVal();
                systemDeliveryDay = Integer.parseInt(result);


            }
           /* Map<String, Object> map = new HashMap<String, Object>();
                map.put("source_business", CommonConstant.SOURCE_BUSINESS_SYSTEM_DELIVERY);
                map.put("old_trades_status", CommonConstant.HAVE_SHIPPED);
                map.put("new_trades_status", CommonConstant.SYSTEM_RECEIVED);
                map.put("systemDelivery", systemDeliveryDay);
                map.put("siteId", m.getMerchantId());
                map.put("tradesEnd", tradesEnd);*/
            //tradesLogMapper.batchInsertToSystemDelivery(map);    //日志
            Map<String, Object> mapTrades = new HashMap<String, Object>();
            mapTrades.put("tradesStatus", CommonConstant.SYSTEM_RECEIVED);
            mapTrades.put("settlementStatus", CommonConstant.SETTLEMENT_STATUS_MAY);
            mapTrades.put("systemDelivery", systemDeliveryDay);
            mapTrades.put("siteId", m.getMerchantId());
            mapTrades.put("tradesEnd", tradesEnd);
            List<Trades> ts = tradesMapper.selectBySystemDelivery(mapTrades);
            List<String> tradesIds = new ArrayList<>();
            ts.stream().forEach(t -> {
                try {
                    List<Orders> ordersList = getStatus(t.getTradesId(), CommonConstant.HAVE_SHIPPED);

                    if (ordersList != null && ordersList.size() > 0) {
                        ordersMapper.updateOrdersStatus(ordersList, CommonConstant.SYSTEM_RECEIVED);
                        addTradesLogs(t, CommonConstant.SYSTEM_RECEIVED, CommonConstant.SOURCE_BUSINESS_SHIPPED);
                        offlineOrderService.pushOrder_pingAN(t.getSiteId(), t,1);
                    } else {
                        logger.info("订单[{}],送货上门 更新发货状态失败，无法更新订单明细状态", t.getTradesId());
                        throw new BusinessLogicException("订单[" + t.getTradesId() + "],确认发货失败，无法更新订单明细状态");
                    }

                    /*//20170905地主确认蜂鸟或者达达没有送达就不能确认系统收货！
                    BLogisticsOrder bLogisticsOrder=bLogisticsOrderMapper.selectByTradesId(t.getTradesId().toString());
                    if(!StringUtil.isEmpty(bLogisticsOrder)&&("1".equals(bLogisticsOrder.getStatus())||"2".equals(bLogisticsOrder.getStatus())
                            ||"3".equals(bLogisticsOrder.getStatus())||"4".equals(bLogisticsOrder.getStatus()))){
                        logger.info("订单[{}],送货上门 没有配送没有成功！不能系统确认收货", t.getTradesId());
                        throw new BusinessLogicException("订单[" + t.getTradesId() + "],送货上门 没有配送没有成功！不能系统确认收货");
                    }*/
                } catch (Exception e) {
                    logger.error("订单[{}]，更新发货状态失败{}", t.getTradesId(), e);
                }
                tradesIds.add(t.getTradesId().toString());
            });
            if (!tradesIds.isEmpty()) {
                mapTrades.put("tradesIds", tradesIds);
                tradesMapper.updateTimeoutBySystemDelivery(mapTrades);
            }
        }

    }


    /**
     * 交易结束
     */
    @Transactional
    public void tradesEnd() {
        List<Merchant> merchants = tradesMapper.getMerchantInfo();
        //交易结束 默认3天
        int metaKeyFinish = 3;
        for (Merchant m : merchants) {
            YbMeta meta = null;
            try {
                meta = settingDealTimeService.getDealTime(m.getMerchantId(), CommonConstant.META_KEY_FINISH);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (meta == null) {
                logger.info("交易结束，未设置时间,商家ID：[{}]", m.getMerchantId());
                metaKeyFinish = 3;
            } else {
                String result = meta.getMetaVal();
                metaKeyFinish = Integer.parseInt(result);
            }
            /* Map<String, Object> map = new HashMap<String, Object>();
                map.put("source_business", CommonConstant.SOURCE_BUSINESS_SYSTEM_DELIVERY);
                map.put("old_trades_status", CommonConstant.HAVE_SHIPPED);
                map.put("new_trades_status", CommonConstant.SYSTEM_RECEIVED);
                map.put("metaKeyFinish", metaKeyFinish);
                map.put("siteId", m.getMerchantId());
                map.put("tradesEnd", tradesEnd);*/
            //tradesLogMapper.batchInsertToSystemDelivery(map);    //日志
            Map<String, Object> mapTrades = new HashMap<String, Object>();
            mapTrades.put("settlementStatus", CommonConstant.SETTLEMENT_STATUS_MAY);
            mapTrades.put("metaKeyFinish", metaKeyFinish);
            mapTrades.put("siteId", m.getMerchantId());
            mapTrades.put("tradesEnd", tradesEnd);

//            boolean flag = true;
//            try {
//                Map<String, Object> map = distributorMapper.getDistributorBySiteId(m.getMerchantId());
//                if (map == null || map.size() == 0 || Integer.parseInt(String.valueOf(map.get("is_open"))) == 0)
//                    flag = false;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            if (flag) {
//                TradeEndPipe tradeEndPipe = TradeBuilder.buildEndPipe();
//                //-------查询交易结束的订单，发送消息处理分销业务---------------begin------------yeah-------------------
//                try {
//                    List<Trades> tradesWillEndList = this.tradesMapper.queryTradesEndList(mapTrades);
//                    if (tradesWillEndList.size() > 0) {
//                        tradeEndPipe.handler(tradesWillEndList);
//                    }
//                    /*if (CollectionUtils.isNotEmpty(tradesWillEndList)) {
//                        List<Long> ids = tradesWillEndList.stream().filter(trades -> trades.getDistributorId() != null && trades.getDistributorId() > 0)
//                                .map(trades -> trades.getTradesId()).collect(Collectors.toList());
//                        Map msg = new HashMap();
//                        msg.put("tradesIds", ids);
//                        msg.put("type", TradeMsgType.TRADES_FINISH);
//                        this.createHandler.sendMqMsg(msg);
//                    }*/
//                } catch (Exception e) {
//                    logger.error("订单结束发送结束订单号失败{},----mapTrades{}", e, mapTrades);
//                }
//                //-----------------------------------------------------end---------------yeah--------------
//            }

            try {
                tradesMapper.tradesEnd(mapTrades);
            } catch (Exception e) {
                logger.error("订单结束状态修改失败" + e);
            }
        }

    }

    /**
     * 判断 数据库b_orders 的 订单状态是否与b_trades的状态一致
     *
     * @param tradesId
     * @param old_trades_status
     * @return
     */
    public List<Orders> getStatus(Long tradesId, int old_trades_status) {
        boolean flag = true;
        List<Orders> orders = ordersMapper.getOrdersByTradesId(tradesId);
        if (orders != null && orders.size() > 0) {
            for (Orders o : orders) {
                logger.info("备货状态getOrdersStatus：{}---old_trades_status：{}----比较：o.getOrdersStatus() != old_trades_status{}", o.getOrdersStatus(), old_trades_status, o.getOrdersStatus() != old_trades_status);
                if (o.getOrdersStatus() != old_trades_status) {
                    flag = false;
                    break;
                }
            }
            if (flag && orders != null && orders.size() > 0) {
                return orders;
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    /**
     * 记录交易日志
     *
     * @param trades
     * @param old_trades_status
     * @param source_business   业务来源
     */
    public void addTradesLogs(Trades trades, int old_trades_status, String source_business) {
        try {
            //添加交易更新日志
            TradesLog tradesLog = new TradesLog();
            tradesLog.setTradesId(trades.getTradesId());
            tradesLog.setSellerId(trades.getSellerId());
            tradesLog.setBuyerId(trades.getBuyerId());
            tradesLog.setOldTradesStatus(old_trades_status);
            tradesLog.setNewTradesStatus(trades.getTradesStatus());
            tradesLog.setSourceBusiness(source_business);
            tradesLog.setShippingStatus(trades.getShippingStatus());
            tradesLog.setStockupStatus(trades.getStockupStatus());
            //添加
            tradesLogMapper.addTradesLog(tradesLog);
        } catch (Exception e) {
            logger.info("添加交易更新日志trades：{} source_business:{} --- Exception：{}", trades, source_business, e);
        }

    }

    /**
     * 获取订单未处理的对账数据 zw
     *
     * @return
     */
    public List<Trades> getTradesListByAccountCheckingStatus() {
        return tradesMapper.getTradesListByAccountCheckingStatus(null);
    }

    public int updateAccountStatus(Long tradeId) {
        return tradesMapper.updateAccountStatus(tradeId);
    }

    /**
     * 订单信息展示
     *
     * @param queryOrdersReq
     * @return
     */
    public List<Trades> getTrades(QueryOrdersReq queryOrdersReq) throws BusinessLogicException {
        System.out.println("pageNum：" + queryOrdersReq.getPageNum());
        List<Trades> tradesList = tradesMapper.getStoreTrades(queryOrdersReq);
        if (tradesList != null && tradesList.size() > 0) {
            for (Trades trades : tradesList) {
                /*String storeUserName = storeAdminExtMapper.getNameById(trades.getSiteId(), trades.getStoreUserId());//促销员
                String storeShippingName = storeAdminMapper.getNameById(trades.getSiteId(), trades.getStoreShippingClerkId());//送货员
                trades.setStoreUserName(storeUserName);
                trades.setStoreShippingName(storeShippingName);*/
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

                List<Orders> ordersList = ordersMapper.getOrdersListByTradesId(String.valueOf(trades.getSiteId()), trades.getTradesId());
                //List<Orders> ordersList = ordersMapper.getOrdersByTradesId(trades.getTradesId());
                if (ordersList != null && ordersList.size() > 0) {
                    /*for (Orders o : ordersList) {
                        String hash = goodsMapper.getImageHash(trades.getSiteId(), o.getGoodsId());
                        o.setHash(hash);
                    }*/
                    trades.setOrdersList(ordersList);
                    Store store = null;
                    if (null != trades.getAssignedStores()) {
                        store = tradesMapper.selectStoreInfo(trades.getSiteId(), trades.getAssignedStores());
                        trades.setStore(store);
                    }
                } else {
                    logger.info("未查到与订单号：[{}]相关商品信息", trades.getTradesId());
                    continue;
                    //throw new BusinessLogicException("未查到与订单号：[{}]相关商品信息" + trades.getTradesId() + "]");

                }
            }
            return tradesList;
        } else {
            return null;
        }
    }


    /**
     * 更新提货码（手动发送提货码）
     *
     * @param trades
     */
    public int updateBarcod(Trades trades) {
        return tradesMapper.updateBarcode(trades);
    }

    /**
     * 送货上门 批量备货
     *
     * @param tradesIds
     */
    public void toDoorBatchStockup(List<Long> tradesIds) throws BusinessLogicException {
        for (Long tradesId : tradesIds) {
            Trades trades = getTradesByTradesId(tradesId);
            if (trades != null) {
                if (trades.getTradesStatus() == CommonConstant.WAIT_SELLER_SHIPPED) {
                    trades.setStockupStatus(CommonConstant.STOCKUP_REDAY);  //备货
                    updateStatusYiStock(trades, CommonConstant.WAIT_SELLER_SHIPPED, CommonConstant.SOURCE_BUSINESS_WAIT_DELIVERY);
                }
            }
        }
    }

    /**
     * 送货上门 批量发货
     *
     * @param tradesIds
     */
    public void toDoorBatchShipping(List<Long> tradesIds) throws BusinessLogicException {
        for (Long tradesId : tradesIds) {
            Trades trades = getTradesByTradesId(tradesId);
            if (trades != null) {
                if (trades.getTradesStatus() == CommonConstant.WAIT_SELLER_SHIPPED && trades.getStockupStatus() == CommonConstant.STOCKUP_REDAY) {
                    trades.setTradesStatus(CommonConstant.HAVE_SHIPPED);
                    trades.setShippingStatus(CommonConstant.SHIPPED);  //发货
                    updateStatusToSend(trades, CommonConstant.WAIT_SELLER_SHIPPED, CommonConstant.SOURCE_BUSINESS_SHIPPED);
                }
            }
        }
    }

    /**
     * 送货上门  批量确认收货
     *
     * @param tradesIds
     * @throws BusinessLogicException
     */
    public void toDoorBatchConfirmShipping(List<Long> tradesIds) throws BusinessLogicException {
        for (Long tradesId : tradesIds) {
            Trades trades = getTradesByTradesId(tradesId);
            if (trades != null) {
                if (trades.getTradesStatus() == CommonConstant.HAVE_SHIPPED && trades.getShippingStatus() == CommonConstant.SHIPPED) {
                    trades.setTradesStatus(CommonConstant.STORE_RECEIVED);
                    trades.setShippingStatus(CommonConstant.SHIPPED_RECEIVED);  //发货
                    //updateStatusToSend(trades, CommonConstant.WAIT_SELLER_SHIPPED, CommonConstant.SOURCE_BUSINESS_SHIPPED);
                    updateConfirmStatus(trades, CommonConstant.HAVE_SHIPPED, CommonConstant.SOURCE_BUSINESS_STORE_RECEIVED);
                    try {
                        offlineOrderService.pushOrder_pingAN(trades.getSiteId(), trades, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 门店自提  批量备货
     *
     * @param tradesIds
     * @throws BusinessLogicException
     */
    public void toStoreBatchStockup(List<Long> tradesIds) throws BusinessLogicException {
        for (Long tradesId : tradesIds) {
            Trades trades = getTradesByTradesId(tradesId);
            if (trades != null) {
                if (trades.getTradesStatus() == CommonConstant.WAIT_SELLER_SHIPPED) {
                    trades.setStockupStatus(CommonConstant.STOCKUP_REDAY);  //备货
                    trades.setTradesStatus(CommonConstant.WAIT_QU_HUO);
//                    String barcode = generateBarcode(trades.getSiteId());
//                    trades.setSelfTakenCode(barcode);
                    updateWaitZiTi(trades, CommonConstant.WAIT_SELLER_SHIPPED, CommonConstant.SOURCE_BUSINESS_WAIT_DELIVERY);
                    trades = getTradesByTradesId(tradesId);
                    String re = "-1";
//                    re = commonService.getGoodSMS(trades.getRecevierMobile(), trades.getSelfTakenCode(), TimeUtil.getTimes(trades.getSelfTakenCodeExpires()), trades.getSellerMobile());
                    if (re.equals("0")) {
                        logger.info("手动生成提货码成功，发送短信成功，订单号：{}，提货码：{}", tradesId, trades.getSelfTakenCode());
                    } else {
                        logger.info("手动生成提货码成功，发送短信失败，订单号：{}，提货码：{}", tradesId, trades.getSelfTakenCode());
                    }
                }
            }
        }
    }


    public List<Trades> getStoreTrades(QueryOrdersReq queryOrdersReq) {
        return tradesMapper.getStoreTrades(queryOrdersReq);
    }


    public List<Map<String, Object>> getStoreTradesReport(QueryOrdersReq queryOrdersReq) throws Exception {
        return tradesMapper.getStoreTradesReport(JacksonUtils.json2map(JacksonUtils.obj2json(queryOrdersReq)));
    }

    public List<Map<String, Object>> getStoreTradesAndGoodsReport(Map<String, Object> queryParamsMap) throws Exception {
        Integer siteId = Integer.parseInt(String.valueOf(queryParamsMap.get("siteId")));
        LocalDateTime checkTime = LocalDateTime.parse("2018-04-25 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<Map<String, Object>> tradesList = tradesMapper.getStoreTradesReport(queryParamsMap);
        if (CollectionUtils.isNotEmpty(tradesList) && MapUtils.isNotEmpty(tradesList.get(0))) {
            long[] tradesIdArray = tradesList.stream().mapToLong(o -> (long) o.get("订单编号")).distinct().toArray();
            if (ArrayUtils.isNotEmpty(tradesIdArray)) {
                List<Map<String, Object>> goodsInfoList = tradesMapper.getGoodsInfoReport(siteId, tradesIdArray);//array太长的情况。。。
                if (CollectionUtils.isNotEmpty(goodsInfoList) && MapUtils.isNotEmpty(goodsInfoList.get(0))) {
                    Map<Long, List<Map<String, Object>>> goodsInfoMap = goodsInfoList.stream().collect(() -> new HashMap<Long, List<Map<String, Object>>>(),
                        (m, o) -> {
                            List<Map<String, Object>> value = m.getOrDefault(o.get("订单编号"), Lists.newArrayList());
                            value.add(o);
                            m.put((Long) o.get("订单编号"), value);
                        },
                        HashMap::putAll);
                    tradesList = tradesList.stream().flatMap(o -> {
                        List<Map<String, Object>> goodsInfos = goodsInfoMap.get((long) o.get("订单编号"));
                        if (CollectionUtils.isNotEmpty(goodsInfos) && MapUtils.isNotEmpty(goodsInfos.get(0))) {
                            BigDecimal discount = new BigDecimal(String.valueOf(o.get("优惠（元）"))).subtract(new BigDecimal(String.valueOf(o.get("postageDiscount"))));
                            //BigDecimal total = goodsInfos.stream().filter(g -> g.get("商品现价") != null).map(g -> new BigDecimal(String.valueOf(g.get("商品现价"))).multiply(new BigDecimal(String.valueOf(g.get("购买数量"))))).reduce(new BigDecimal("0.0"), (o1, o2) -> o1.add(o2));
                            BigDecimal total = new BigDecimal(String.valueOf(o.get("商品总价（元）")));
                            AtomicDouble sum = new AtomicDouble(0.0);
                            AtomicInteger count = new AtomicInteger(0);
                            int goodsSize = goodsInfos.size();
                            goodsInfos.forEach(goods -> {
                                goods.putAll(o);
                                count.incrementAndGet();
                                if (checkTime.isBefore(LocalDateTime.ofInstant(((Date) o.get("createTime")).toInstant(), ZoneId.systemDefault()))
                                    && goods.get("商品现价") != null && total.compareTo(discount) >= 0) {
                                    BigDecimal goodsPrice = new BigDecimal(String.valueOf(goods.get("商品现价")));
                                    BigDecimal discountPart = null;
                                    if (count.get() == goodsSize) {
                                        discountPart = discount.subtract(new BigDecimal(Double.toString(sum.get())));
                                    } else {
                                        discountPart = goodsPrice.multiply(discount).divide(total, 2, BigDecimal.ROUND_HALF_DOWN);
                                        sum.addAndGet(discountPart.doubleValue());
                                    }
                                    goods.put("优惠金额", discountPart.doubleValue());
                                    goods.put("商品折后价", goodsPrice.subtract(discountPart).doubleValue());
                                }
                            });
                        } else {
                            goodsInfos = Lists.newArrayList(o);
                        }
                        return goodsInfos.stream();
                    }).collect(Collectors.toList());
                }
            }
        }
        return tradesList;
    }

    public String generateBarcode(int siteId) {
        String barcode = RandomUtils.randomSet(CommonConstant.BARCODE_LENGTH);
        boolean flag = false;
        while (flag) {
            Trades t = validationBarCode2(siteId, barcode);
            if (t != null) {
                barcode = RandomUtils.randomSet(CommonConstant.BARCODE_LENGTH);
            } else {
                flag = true;
            }
        }
        return barcode;
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
            /*//获取店员邀请码
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
            trades.setClerkInvitationCode(clerkInvitationCode);*/
            if (trades.getIsUpPrice() != -1) {
                List<TradesUpdatePriceLog> tradesUpdatePriceLogs = tradesUpdatePriceLogMapper.selectTradesUpProceLog(trades.getSiteId(), trades.getTradesId());
                trades.setTradesUpdatePriceLogs(tradesUpdatePriceLogs);
            }
            List<Map<String, Object>> groupOrders = null;
            if (Integer.valueOf(50).equals(trades.getServceTpye())) {
                Optional<Pair<String, List<Map<String, String>>>> groups = groupPurChaseService.getOtherOrdersAndStatusInGroup(trades.getSiteId(), trades.getTradesId());
                if (groups.isPresent()) {
                    Pair<String, List<Map<String, String>>> pair = groups.get();
                    trades.setGroupOrders(pair);
                }
            }
            if (null != trades.getAssignedStores()) {
                Store store = tradesMapper.selectStoreInfo(trades.getSiteId(), trades.getAssignedStores());
                trades.setStore(store);
                if (store != null && store.getName() != null) {
                    trades.setStoreNames(store.getName());
                }
            }
//            List<Orders> ordersList = ordersMapper.getOrdersByTradesId(trades.getTradesId());
            List<Orders> ordersList = ordersMapper.getOrdersListByTradesId(String.valueOf(trades.getSiteId()), trades.getTradesId());//获取订单下商品

            if (ordersList != null && ordersList.size() > 0) {
                trades.setOrdersList(ordersList);
                trades.setMap(couponDetailService.findOrderCoupon(String.valueOf(tradesId)));  //优惠券
            } else {
                logger.info("未查到与订单号：[{}]相关商品信息", tradesId);
                throw new BusinessLogicException("未查到与订单号：[{}]相关商品信息" + tradesId + "]");
            }
            logger.info("查询discountList开始" + tradesId);
            //查询订单的优惠信息
            Optional<List<Map<String, String>>> optional = concessionResultHandler.getConcessionResultByTradesId(trades);
            if (!optional.isPresent()) {
                // 异常发生

            } else {
                logger.info("查询discountList开始1");
                List<Map<String, String>> discountList = optional.get();
                if (discountList.size() > 0) {
                    trades.setDiscountList(discountList);
                }
                logger.info("查询discountList：" + discountList);
            }

            //判断是否是微信支付，是否已支付，（如果微信没有支付就调用微信查看订单是否已经支付了）修改平台的支付方式
            if (trades.getIsPayment() == 0 && trades.getTradesStatus() == 110) {
                try {
                    Map map = payService.wxQueryOrder(Integer.parseInt(trades.getSiteId() + ""), trades.getTradesId() + "", null);
                } catch (PayException e) {
                    logger.info("查询微信支付订单失败：" + e);
                } catch (Exception e) {
                    logger.info("查询支付订单失败：" + e);
                    e.printStackTrace();
                }
                try {
                    AliRequestParam aliRequestParam = new AliRequestParam();
                    aliRequestParam.setOut_trade_no(trades.getTradesId() + "");
                    AlipayTradeQueryResponse response = payService.aliquery(aliRequestParam);
                } catch (Exception e) {
                    logger.info("查询支付宝支付订单失败：" + e);
                    e.printStackTrace();
                }
            }
        } else {
            logger.info("未查到与订单号：[{}]相关信息", tradesId);
            throw new BusinessLogicException("未查到与订单号：[{}]相关商品信息" + tradesId + "]");
        }
        return trades;
    }

    /**
     * 验证授权码
     *
     * @param siteId
     * @param storeAuthCode
     * @param storeId
     * @return
     */
    public int validationStoreAuthCode(int siteId, String storeAuthCode, int storeId) {
        return tradesMapper.selectMeta(siteId, storeAuthCode, storeId);
    }

    /**
     * 送货上门 确认发货 （店员送货）
     *
     * @param trades
     * @param old_trades_status
     * @param store_shipping_clerk_id
     * @throws BusinessLogicException
     */
    @Transactional
    public void clerkToSend(Trades trades, int old_trades_status, int store_shipping_clerk_id) throws BusinessLogicException {
        try {
            List<Orders> orders = getStatus(trades.getTradesId(), old_trades_status);
            if (orders != null && orders.size() > 0) {
                ordersMapper.updateOrdersStatus(orders, CommonConstant.HAVE_SHIPPED);
                tradesMapper.clerkToSend(trades.getTradesId(), store_shipping_clerk_id);
                addTradesLogs(trades, old_trades_status, CommonConstant.SOURCE_BUSINESS_SHIPPED);

            } else {
                logger.info("订单[{}],送货上门 更新发货状态失败，无法更新订单明细状态", trades.getTradesId());
                throw new BusinessLogicException("订单[" + trades.getTradesId() + "],确认发货失败，无法更新订单明细状态");
            }
        } catch (Exception e) {
            logger.error("订单[{}]，更新发货状态失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }
    }

    /**
     * 送货上门 确认发货（物流配送）
     *
     * @param trades
     * @param old_trades_status
     * @param post_name         快递名称
     * @param post_number       快递单号
     * @throws BusinessLogicException
     */
    @Transactional
    public void logisticsToSend(Trades trades, int old_trades_status, String post_name, String post_number) throws BusinessLogicException {
        try {
            List<Orders> orders = getStatus(trades.getTradesId(), old_trades_status);
            if (orders != null && orders.size() > 0) {
                ordersMapper.updateOrdersStatus(orders, CommonConstant.HAVE_SHIPPED);
                tradesMapper.logisticsToSend(trades.getTradesId(), post_name, post_number);
                addTradesLogs(trades, old_trades_status, CommonConstant.SOURCE_BUSINESS_SHIPPED);
            } else {
                logger.info("订单[{}],送货上门 更新发货状态失败，无法更新订单明细状态", trades.getTradesId());
                throw new BusinessLogicException("订单[" + trades.getTradesId() + "],确认发货失败，无法更新订单明细状态");
            }
        } catch (Exception e) {
            logger.error("订单[{}]，更新发货状态失败{}", trades.getTradesId(), e);
            throw new BusinessLogicException(e);
        }
    }

    /**
     * 更新订单的配送方式和服务门店
     *
     * @return
     */
    public Integer updateAssignStoreAndPostStyle(Trades trades) {
        return tradesMapper.updateAssignStoreAndPostStyle(trades);

    }

    /**
     * 更新订单的配送方式和服务门店（同时更新b_refund.store_id）
     *
     * @return
     */
    public Integer updateAssignStoreAndPostStyleWithRefund(Trades trades) {
        refundMapper.updateRefundStoreId(trades.getTradesId(), trades.getAssignedStores());
        return tradesMapper.updateAssignStoreAndPostStyle(trades);

    }

    /**
     * 更新商家标记和商家备注
     *
     * @return
     */
    public Integer updateSellerFlagAndMemo(Trades trades) {
        return tradesMapper.updateSellerFlagAndMemo(trades);
    }

    /**
     * 订单统计接口
     *
     * @param map
     * @return
     */
    public Map<String, Object> tradesCount(Map<String, Object> map) {
        map.put("tradesStatus", "120");
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("payment", tradesMapper.selectTradesCount(map));
        map.put("tradesStatus", "130");
        result.put("shipping", tradesMapper.selectTradesCount(map));
        map.put("tradesStatus", "200");
        result.put("lift", tradesMapper.selectTradesCount(map));
        map.put("tradesStatus", "210,220,230,240,800");
        result.put("complete", tradesMapper.selectTradesCount(map));

        QueryOrdersReq queryOrdersReq = new QueryOrdersReq();
        queryOrdersReq.setSiteId(map.get("siteId").toString());
        queryOrdersReq.setBuyerId(map.get("buyerId").toString());
        queryOrdersReq.setTradesSource("120");
        queryOrdersReq.setNewTradesStatus(1);
        result.put("notPayment", tradesMapper.getTradesListCount(queryOrdersReq));
        queryOrdersReq.setNewTradesStatus(15);
        result.put("notShipping", tradesMapper.getTradesListCount(queryOrdersReq));
        queryOrdersReq.setNewTradesStatus(12);
        result.put("notRank", tradesMapper.getTradesListCount(queryOrdersReq));
        queryOrdersReq.setNewTradesStatus(14);
        result.put("refund", tradesMapper.getTradesListCount(queryOrdersReq));
        return result;
    }

    public int updateTradePayStyle(String payStyle, long tradesId) {
        return tradesMapper.updateTradePayStyle(payStyle, tradesId);
    }

    public YbMeta getYbMeta(int siteId, String metaKey) {
        return ybMetaMapper.findFirst(siteId, metaKey);
    }

    public String getStorePhone(Integer siteId, Integer storeId) {
        return storesMapper.getStorePhone(siteId, storeId);
    }

    public Store selectStoreInfo(Integer siteId, Integer storeId) {
        return tradesMapper.selectStoreInfo(siteId, storeId);
    }

    public YbMerchant selectMerchantfo(String siteId) {
        return ybMerchantMapper.getMerchant(siteId);
    }

    public List<Trades> selectByBuyerId(Integer siteId, Integer buyerId) {
        return tradesMapper.selectByBuyerId(siteId, buyerId);
    }

    public long getTradesListCount(QueryOrdersReq queryOrdersReq) {
        return tradesMapper.getTradesListCount(queryOrdersReq);
    }

    public List<Trades> getTradesList(QueryOrdersReq queryOrdersReq) {
        return tradesMapper.getTradesList(queryOrdersReq);
    }

    public void settingTradesList2(List<Trades> tradesList) throws Exception {
        if (tradesList != null && tradesList.size() != 0) {
            Integer siteId = tradesList.get(0).getSiteId();

            Set<Integer> storeAdminIds = new HashSet<>();//用以查询店员名：促销员名 促销员邀请码  送货员名
            Set<Long> stockupTradesIds = new HashSet<>();//用以查询备货编码
            Set<Long> logisticsTradesIds = new HashSet<>();//用以查询物流信息
            Set<Long> refundTradesIds = new HashSet<>();//用以查询退款金额
            Set<Long> servceOrderTradesIds = new HashSet<>();//用以查询预约单信息
            Set<Integer> storeTradesIds = new HashSet<>();//用以查询门店信息
            Set<Long> ordersTradesIds = new HashSet<>();//用以查询订单信息
            for (Trades trades : tradesList) {

                if (trades.getStoreUserId() != null && trades.getStoreUserId().intValue() != 0) {//促销员名 邀请码
                    storeAdminIds.add(trades.getStoreUserId());
                }
                if (trades.getStoreShippingClerkId() != null && trades.getStoreShippingClerkId().intValue() != 0) {//送货员名
                    storeAdminIds.add(trades.getStoreShippingClerkId());
                }
                if (trades.getStockupStatus() != null && trades.getStockupStatus().intValue() == 120) {// 备货编码
                    stockupTradesIds.add(trades.getTradesId());
                }
                logisticsTradesIds.add(trades.getTradesId());//物流信息
                if (trades.getIsRefund() != null && trades.getIsRefund().intValue() == 120) {//退款金额
                    refundTradesIds.add(trades.getTradesId());
                }
                if (trades.getServceTpye() != null && trades.getServceTpye().intValue() == 100) {//服务类订单扩展表(预约表）  唯一对应一个 trades 记录
                    servceOrderTradesIds.add(trades.getTradesId());
                }
                if (null != trades.getAssignedStores()) {
                    storeTradesIds.add(trades.getAssignedStores());
                }
                ordersTradesIds.add(trades.getTradesId());

                /*List<Orders> ordersList = ordersMapper.getOrdersListByTradesId(String.valueOf(trades.getSiteId()), trades.getTradesId());//获取订单下商品
                if (ordersList != null && ordersList.size() > 0) {
                    trades.setOrdersList(ordersList);
                    *//*Store store = null;
                    if (null != trades.getAssignedStores()) {
                        store = tradesMapper.selectStoreInfo(trades.getSiteId(), trades.getAssignedStores());
                        trades.setStore(store);
                    }*//*
                } else {
                    logger.info("未查到与订单号：[{}]相关商品信息", trades.getTradesId());
                    continue;
                    //throw new BusinessLogicException("未查到与订单号：[{}]相关商品信息" + trades.getTradesId() + "]");
                }*/
            }

            //-------------------------------这里---------------------
            Map<String, Object> params = new HashMap<>();
            params.put("siteId", siteId);

            Map<String, List<Orders>> ordersMap = null;
            if (ordersTradesIds.size() != 0) {
                params.put("ids", ordersTradesIds);
                List<Orders> ordersMaps = ordersMapper.getOrdersInfoList(params);

                if (ordersMaps != null && ordersMaps.size() != 0) {
                    ordersMap = listOrdersToMap(ordersMaps);
                }
            }
            Map<String, Store> storeMap = null;
            if (storeTradesIds.size() != 0) {
                params.put("ids", storeTradesIds);
                List<Store> storeList = tradesMapper.selectStoreInfoList(params);

                if (storeList != null && storeList.size() != 0) {
                    storeMap = listToMap(storeList);
                }
            }
            Map<String, Object> storeAdminMap = null;
            if (storeAdminIds.size() != 0) {
                params.put("ids", storeAdminIds);//用以查询店员名
                List<Map<String, Object>> storeAdminMaps = storeAdminExtMapper.getStoreAdminInfoList(params);

                if (storeAdminMaps != null && storeAdminMaps.size() != 0) {
                    storeAdminMap = listMapToMap(storeAdminMaps, "site_id", "storeadmin_id");
                }
            }
            Map<String, Object> tradesStockupsMap = null;
            if (stockupTradesIds.size() != 0) {
                params.put("ids", stockupTradesIds);//用以查询备货编码
                List<Map<String, Object>> tradesStockups = stockupMapper.getTradesStockupInfoList(params);

                if (tradesStockups != null && tradesStockups.size() != 0) {
                    tradesStockupsMap = listMapToMap(tradesStockups, "site_id", "trades_id");
                }
            }
            Map<String, Object> tradesLogisticsMap = null;
            if (logisticsTradesIds.size() != 0) {
                params.put("ids", logisticsTradesIds);//用以查询物流信息
                List<Map<String, Object>> tradesLogistics = bLogisticsOrderMapper.getTradesLogisticsList(params);

                if (tradesLogistics != null && tradesLogistics.size() != 0) {
                    tradesLogisticsMap = listMapToMap(tradesLogistics, "site_id", "trades_id");
                }
            }
            Map<String, Object> tradesRefundsMap = null;
            if (refundTradesIds.size() != 0) {
                params.put("ids", refundTradesIds);//用以查询退款金额
                List<Map<String, Object>> tradesRefunds = refundMapper.getTradesRefundList(params);

                if (tradesRefunds != null && tradesRefunds.size() != 0) {
                    tradesRefundsMap = listMapToMap(tradesRefunds, "site_id", "trades_id");
                }
            }
            Map<String, Object> tradesServceOrdersMap = null;
            if (servceOrderTradesIds.size() != 0) {
                params.put("ids", servceOrderTradesIds);//用以查询预约单信息
                List<Map<String, Object>> tradesServceOrders = servceOrderMapper.getTradesServceOrdersList(params);//服务类订单扩展表(预约表）  唯一对应一个 trades 记录

                if (tradesServceOrders != null && tradesServceOrders.size() != 0) {
                    tradesServceOrdersMap = listMapToMap(tradesServceOrders, "site_id", "trades_id");
                }
            }

            for (Trades trades : tradesList) {

                if (ordersMap != null && ordersMap.size() != 0) {
                    List<Orders> orders = ordersMap.get(String.valueOf(trades.getSiteId()) + String.valueOf(trades.getTradesId()));
                    if (orders != null && orders.size() != 0) {
                        trades.setOrdersList(orders);
                    }
                }

                if (null != trades.getAssignedStores()) {
                    if (storeMap != null && storeMap.size() != 0) {
                        Store store = storeMap.get(String.valueOf(trades.getSiteId()) + String.valueOf(trades.getAssignedStores()));
                        if (store != null) {
                            trades.setStore(store);
                        }
                    }
                }

                if (trades.getStoreUserId() != null && trades.getStoreUserId().intValue() != 0) {//促销员名 邀请码
                    if (storeAdminMap != null && storeAdminMap.size() != 0) {
                        Map<String, Object> storeAdmin = (Map<String, Object>) storeAdminMap.get(String.valueOf(trades.getSiteId()) + String.valueOf(trades.getStoreUserId()));
                        if (storeAdmin != null && storeAdmin.size() != 0) {
                            trades.setStoreUserName(storeAdmin.get("name") != null ? (String) storeAdmin.get("name") : null);

                            String clerkInvitationCodeStr = storeAdmin.get("clerk_invitation_code") != null ? String.valueOf(storeAdmin.get("clerk_invitation_code")) : null;
                            String clerkInvitationCode = null;
                            if (!StringUtil.isEmpty(clerkInvitationCodeStr)) {
                                int index = clerkInvitationCodeStr.indexOf("_");
                                if (index != -1) {
                                    clerkInvitationCode = clerkInvitationCodeStr.substring(index + 1, clerkInvitationCodeStr.length());
                                }
                            }
                            trades.setClerkInvitationCode(clerkInvitationCode);
                        }
                    }
                }

                if (trades.getStoreShippingClerkId() != null && trades.getStoreShippingClerkId().intValue() != 0) {//送货员名
                    if (storeAdminMap != null && storeAdminMap.size() != 0) {
                        Map<String, Object> storeAdmin = (Map<String, Object>) storeAdminMap.get(String.valueOf(trades.getSiteId()) + String.valueOf(trades.getStoreShippingClerkId()));
                        if (storeAdmin != null && storeAdmin.size() != 0) {
                            trades.setStoreShippingName(storeAdmin.get("name") != null ? (String) storeAdmin.get("name") : null);
                        }
                    }
                }

                if (trades.getStockupStatus() != null && trades.getStockupStatus().intValue() == 120) {// 备货编码
                    if (tradesStockupsMap != null && tradesStockupsMap.size() != 0) {
                        Map<String, Object> stockupsMap = (Map<String, Object>) tradesStockupsMap.get(String.valueOf(trades.getSiteId()) + String.valueOf(trades.getTradesId()));
                        if (stockupsMap != null && stockupsMap.size() != 0) {
                            trades.setStockupId(stockupsMap.get("stockup_id") != null ? String.valueOf(stockupsMap.get("stockup_id")) : null);
                        }
                    }
                }

                if (tradesLogisticsMap != null && tradesLogisticsMap.size() != 0) {//物流信息 //对订单点击确认发货，并不更改订单状态 对应生成唯一一条物流记录，第三方回调时才更改为已发货。
                    Map<String, Object> logisticsMap = (Map<String, Object>) tradesLogisticsMap.get(String.valueOf(trades.getSiteId()) + String.valueOf(trades.getTradesId()));
                    if (logisticsMap != null && logisticsMap.size() != 0) {
                        trades.setLogisticsName(logisticsMap.get("logistics_name") != null ? String.valueOf(logisticsMap.get("logistics_name")) : null);
                        trades.setLogisticsStatus(logisticsMap.get("status") != null ? String.valueOf(logisticsMap.get("status")) : null);
                    }
                }

                if (trades.getIsRefund() != null && trades.getIsRefund().intValue() == 120) {//退款金额
                    if (tradesRefundsMap != null && tradesRefundsMap.size() != 0) {
                        Map<String, Object> refundsMap = (Map<String, Object>) tradesRefundsMap.get(String.valueOf(trades.getSiteId()) + String.valueOf(trades.getTradesId()));
                        if (refundsMap != null && refundsMap.size() != 0) {
                            trades.setRealRefundMoney(refundsMap.get("real_refund_money") != null ? Integer.valueOf(String.valueOf(refundsMap.get("real_refund_money"))) : null);
                        }
                    }
                }

                try {
                    if (trades.getServceTpye() != null && trades.getServceTpye().intValue() == 100) {//服务类订单扩展表(预约表）  唯一对应一个 trades 记录
                        if (tradesServceOrdersMap != null && tradesServceOrdersMap.size() != 0) {
                            Map<String, Object> servceOrdersMap = (Map<String, Object>) tradesServceOrdersMap.get(String.valueOf(trades.getSiteId()) + String.valueOf(trades.getTradesId()));
                            if (servceOrdersMap != null && servceOrdersMap.size() != 0) {
                                trades.setUseDetailId(servceOrdersMap.get("use_detail_id") != null ? (Integer) servceOrdersMap.get("use_detail_id") : null);
                                trades.setUseCount(servceOrdersMap.get("use_count") != null ? (Integer) servceOrdersMap.get("use_count") : null);
                                trades.setAmount(servceOrdersMap.get("amount") != null ? (Integer) servceOrdersMap.get("amount") : null);
                                trades.setServeStatus(servceOrdersMap.get("serve_status") != null ? (Integer) servceOrdersMap.get("serve_status") : null);
                                trades.setSchedulePersonId(servceOrdersMap.get("schedule_person_id") != null ? (Integer) servceOrdersMap.get("schedule_person_id") : null);
                                trades.setDiagnoseStatus(servceOrdersMap.get("diagnose_status") != null ? (Integer) servceOrdersMap.get("diagnose_status") : null);
                                trades.setDiseaseInfo(servceOrdersMap.get("disease_info") != null ? String.valueOf(servceOrdersMap.get("disease_info")) : null);
                            }
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }

            }

        }
    }

    /**
     * listMap：
     * key1 - v1
     * key2 - v2
     * key3 - v3
     * Map:
     * key1key2 - listMap
     *
     * @param listMap
     * @param key1
     * @param key2
     * @return
     */
    public Map<String, Object> listMapToMap(List<Map<String, Object>> listMap, String key1, String key2) {
        Map<String, Object> resultMap = null;
        if (listMap != null && listMap.size() != 0 && !StringUtil.isEmpty(key1) && !StringUtil.isEmpty(key2)) {
            resultMap = new HashMap<>();
            for (Map<String, Object> lm : listMap) {
                resultMap.put(String.valueOf(lm.get(key1)) + String.valueOf(lm.get(key2)), lm);
            }
        }
        return resultMap;
    }

    public Map<String, Store> listToMap(List<Store> list) {
        Map<String, Store> resultMap = null;
        if (list != null && list.size() != 0) {
            resultMap = new HashMap<>();
            for (Store store : list) {
                resultMap.put(String.valueOf(store.getSiteId()) + String.valueOf(store.getId()), store);
            }
        }
        return resultMap;
    }

    public Map<String, List<Orders>> listOrdersToMap(List<Orders> list) {
        Map<String, List<Orders>> resultMap = null;
        if (list != null && list.size() != 0) {
            resultMap = new HashMap<>();

            for (Orders orders : list) {
                List<Orders> ordersList = null;
                if (resultMap.get(String.valueOf(orders.getSiteId()) + String.valueOf(orders.getTradesId())) != null) {
                    ordersList = resultMap.get(String.valueOf(orders.getSiteId()) + String.valueOf(orders.getTradesId()));
                    ordersList.add(orders);
                } else {
                    ordersList = new ArrayList<>();
                    ordersList.add(orders);
                    resultMap.put(String.valueOf(orders.getSiteId()) + String.valueOf(orders.getTradesId()), ordersList);
                }
            }
        }
        return resultMap;
    }


    public Map<String, Object> isFirstDistributorOrder(String tradeId) {
        Map<String, Object> resultMap = new HashedMap();

        //Map<String, Object> result = tradesMapper.isFirstDistributorOrder(tradeId);
        Trades trades = tradesMapper.getTradesByTradesId(Long.parseLong(tradeId));
        resultMap.put("isFirstOrder", "false");
        if (trades == null) {
            return resultMap;
        } else {
            Distributor distributor = distributorMapper.selectByUid(trades.getBuyerId(), trades.getSiteId());

            if (distributor != null && distributor.getStatus() == 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssms");
                Timestamp ts = trades.getCreateTime();
                Date date = new Date();
                try {
                    date = ts;
                    long order = date.getTime();
                    long dis = distributor.getCreateTime().getTime();
                    long time = (dis - order) / 1000;
                    if (time > 0 && time < 10) {
                        resultMap.put("isFirstOrder", "true");
                        Integer level = checkDistributor(trades);
                        if (level > 0) {
                            resultMap.put("level", level);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return resultMap;
    }

    public Integer checkDistributor(Trades trades) {
        int distributeTotalPrice = 0;
        Double distributePrice = 0.0;
        Double discountRate = 1.0;
        Integer reachedLevel = 0;
        List<Orders> orders = this.ordersMapper.getOrdersByTradesId(trades.getTradesId());
        for (Orders order : orders) {
            Integer goodsPrice = order.getGoodsPrice();
            Integer goodsId = order.getGoodsId();
            Integer goodsNum = order.getGoodsNum();
            GoodsDistribute goodsDistribute = this.goodsDistributeMapper.selectByGoodsIdAndSiteId(trades.getSiteId(), goodsId);
            if (null != goodsDistribute) {
                //是分销商品
                distributePrice += goodsNum * goodsPrice * discountRate;
                distributeTotalPrice = new BigDecimal(distributePrice).setScale(0, RoundingMode.HALF_UP).intValue();
            }
        }
        List<Recruit> recruitListByOwner = this.recruitMapper.getRecruitListByOwner(String.valueOf(trades.getSiteId()));
        Recruit recruit = recruitListByOwner.get(0);
        String ruleStr = recruit.getRule();
        try {
            Map rule = JacksonUtils.json2map(ruleStr);
            if (null != rule) {
                if (distributeTotalPrice >= Integer.parseInt(rule.get("level1").toString())) {
                    reachedLevel = getReachedLevel(0, distributeTotalPrice, rule);
                }
            }
        } catch (Exception e) {
            logger.debug("=======fenxiao 处理等级错误{}", e);
        }
        return reachedLevel;
    }

    private Integer getReachedLevel(Integer currentLevel, long order_price, Map rule) {
        if (null != rule.get("level" + (currentLevel + 1)) && order_price >= (Integer) rule.get("level" + (currentLevel + 1))) {
            //存在比现在更高的等级,并且达到这一等级需要购买的金额
            return this.getReachedLevel(currentLevel + 1, order_price, rule);
        }
        return currentLevel;
    }

    /**
     * 条件查询订单
     *
     * @param site_id
     * @param sTime
     * @param eTime
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Map<String, Object> getTradesListFormERP(Integer site_id, String sTime, String eTime, Integer type, Integer pageNum, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<Map> tradesList = tradesMapper.selectTradesListfromERP(sTime, eTime, site_id, type);
            PageInfo tradePageList = new PageInfo<>(tradesList);
            for (Map tMap : tradesList) {
                if (!"1".equals(tMap.get("payment").toString())) {
                    continue;
                }
                tMap.put("orderList", ordersMapper.selectOrderListFromERP(site_id, Long.parseLong(tMap.get("tId").toString())));
                if (1 == Integer.parseInt(tMap.get("payment").toString()) && 120 == Integer.parseInt(tMap.get("refund").toString())) {
                    tMap.put("status", 0);
                } else {
                    tMap.put("status", 1);
                }
                tMap.put("discount", Integer.parseInt(tMap.get("totalFee").toString()) + Integer.parseInt(tMap.get("postFee").toString()) - Integer.parseInt(tMap.get("realPay").toString()));
                tMap.put("totalPay", Integer.parseInt(tMap.get("totalFee").toString()) + Integer.parseInt(tMap.get("postFee").toString()));
                tMap.remove("payment");
                tMap.remove("refund");
            }
            params.put("total", tradePageList.getTotal());
            params.put("tradesList", tradesList);
            params.put("code", 0);
            params.put("msg", "订单查询成功");
        } catch (Exception e) {
            params.put("code", -1);
            params.put("msg", "订单查询失败");
        }
        return params;
    }

    /**
     * 条件查询订单
     *
     * @param site_id
     * @param sTime
     * @param eTime
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Map<String, Object> getTradesListFormERP2(Integer site_id, String sTime, String eTime, Integer type, Integer pageNum, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<Map> tradesList = tradesMapper.selectTradesListfromERP2(sTime, eTime, site_id, type);
            PageInfo tradePageList = new PageInfo<>(tradesList);
            for (Map tMap : tradesList) {
                if (!"1".equals(tMap.get("payment").toString())) {
                    continue;
                }
                Trades trades=new Trades();
                trades.setSiteId(site_id);
                trades.setTradesId(Long.valueOf(tMap.get("tId").toString()));
                try {
                    Optional<List<Map<String, String>>> optional = concessionResultHandler.getConcessionResultByTradesId(trades);
                    List<String> couponNames = optional.get().stream().map(t -> t.get("ruleName").toString()).collect(Collectors.toList());
                    tMap.put("couponName", StringUtil.join(couponNames, ","));
                } catch (Exception e) {
                    tMap.put("couponName", null);
                }
                tMap.put("orderList", ordersMapper.selectOrderListFromERP(site_id, Long.parseLong(tMap.get("tId").toString())));
                if (1 == Integer.parseInt(tMap.get("payment").toString()) && 120 == Integer.parseInt(tMap.get("refund").toString())) {
                    tMap.put("status", 1);
                } else {
                    tMap.put("status", 0);
                }
                tMap.put("discount", Integer.parseInt(tMap.get("totalFee").toString()) + Integer.parseInt(tMap.get("postFee").toString()) - Integer.parseInt(tMap.get("realPay").toString()));
                tMap.put("totalPay", Integer.parseInt(tMap.get("totalFee").toString()) + Integer.parseInt(tMap.get("postFee").toString()));
                tMap.remove("payment");
                tMap.remove("refund");
            }
            params.put("total", tradePageList.getTotal());
            params.put("tradesList", tradesList);
            params.put("code", 0);
            params.put("msg", "订单查询成功");
        } catch (Exception e) {
            params.put("code", -1);
            params.put("msg", "订单查询失败");
        }
        return params;
    }

    /**
     * (微信商城订单付款成功跑这个接口)
     * 推送付款成功订单或者退款成功订单给九洲
     * 推送付款成功订单或者退款成功订单给天润
     *
     * @param siteId
     * @param tradesId
     */
    public void pushTradesToJZ(Integer siteId, Long tradesId) {
        erpToolsService.erpOrdersService(siteId, tradesId);

    }


    /**
     * 检查购买商品
     *
     * @param buyerGoods goodsId goodsNum
     * @param siteId
     * @throws BusinessLogicException
     */
    public void checkGoods(List<Map> buyerGoods, int siteId) throws BusinessLogicException {
        String[] fields = {"wx_purchase_way", "control_num", "goods_title"};
        int[] ids = buyerGoods.stream().mapToInt(v -> NumberUtils.toInt(String.valueOf(v.get("goodsId")))).toArray();
        Map idNumMap = buyerGoods.stream().collect(Collectors.toMap(v -> v.get("goodsId"), v -> v.get("goodsNum")));
        List<Map> goodsList = goodsMapper.findByIds(ids, siteId, fields);
        // 商品购买数量不能超过100 如果设置了移动端购买方式 只有值为110才能购买
        for (Map goods : goodsList) {
            int num = NumberUtils.toInt(String.valueOf(idNumMap.get(goods.get("control_num"))));
            if (num > 100) {
                throw new BusinessLogicException(goods.get("goods_title") + "购买数量不能超过100");
            }
            if (NumberUtils.toInt(String.valueOf(goods.get("wx_purchase_way"))) != 110) {
                throw new BusinessLogicException(goods.get("goods_title") + "禁止购买");
            }
        }
    }

    @Transactional
    public void cancelStockup(String siteId, String tradesId) throws Exception {
        int i = tradesMapper.cancelStockup(siteId, tradesId);
        if (i > 0) {
            ordersMapper.cancelStockup(siteId, tradesId);
        }
        stockupMapper.cancelStockup(siteId, tradesId);
//        stockupService.sendStockup(Long.parseLong(tradesId));
    }

    /**
     * 修改订单价格
     *
     * @param params
     * @return
     */
    @Transactional
    public ReturnDto upTradesPrice(@RequestBody UpdateOrderPriceParams params) throws Exception {
        if (params.getSiteId() == null)
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        Integer siteId = Integer.parseInt(params.getSiteId().toString());
        if (params.getUserName() == null || params.getUserId() == null)
            return ReturnDto.buildFailedReturnDto("没有登录信息");
        String userName = params.getUserName().toString();
        String userId = params.getUserId().toString();
        if (params.getPlatform() == null)
            params.setPlatform("0");
        Integer platform = Integer.parseInt(params.getPlatform().toString());
        if (params.getTradesId() == null)
            return ReturnDto.buildFailedReturnDto("订单ID不能为空");
        Long tradeId = Long.parseLong(params.getTradesId().toString());
        Object remark = params.getRemark();
        if (params.getPrice() == null)
            return ReturnDto.buildFailedReturnDto("订单价格不能为空");

        double v = Double.parseDouble(params.getPrice().toString()) * 100;
        BigDecimal b = new BigDecimal(v);
        Double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        int price = f1.intValue();
        Trades tradesByTradesId = tradesMapper.getTradesByTradesId(tradeId);
        Integer realPay = tradesByTradesId.getRealPay();
        int r = 0;
        if (tradesByTradesId.getIsUpPrice() == -1) {
            //订单未修改过
            r = tradesMapper.updateIsUpPrice(siteId, tradeId);
        }
        int i = tradesMapper.updateRealPay(siteId, tradeId, price);
        Map<String, Object> selectCommissionRate = tradesMapper.selectCommissionRate(siteId);
        double d1 = new DecimalFormat().parse(String.valueOf(price)).doubleValue();
        if (tradesByTradesId.getSettlementType() == 0) {
            double purchase_rate = (Double.valueOf(selectCommissionRate.get("direct_purchase_rate").toString()) * d1) / 100;
            BigDecimal zz = new BigDecimal(purchase_rate);
            double v1 = zz.setScale(0, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
            try {
                tradesMapper.updateTradesSplit((int) v1, tradeId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (tradesByTradesId.getSettlementType() == 1) {
            double purchase_rate = (Double.valueOf(selectCommissionRate.get("distributor_rate").toString()) * d1) / 100;
            BigDecimal zz = new BigDecimal(purchase_rate);
            double v1 = zz.setScale(0, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
            try {
                tradesMapper.updateTradesSplit((int) v1, tradeId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (i == 1) {
            saveUpPriceLog(tradesByTradesId, r, siteId, userName, userId, platform, tradeId, price, remark, params.getStoreId());
            Reward reward = rewardMapper.selectRewardByOrderId(tradeId, siteId);
            if (!StringUtil.isEmpty(reward) && reward.getReal_pay() != price) {
                rewardMapper.updateRewardRealPay(price, siteId, reward.getId());
            }
           /* try {
                payService.wxClose(siteId, String.valueOf(tradeId));//防止客户已经支付(查询之前的订单是否微信已经支付)
            } catch (PayException e) {
                e.printStackTrace();
            }
            try {
                AliRequestParam param = new AliRequestParam();
                param.setOut_trade_no(String.valueOf(tradeId));
                aliPayApi.close(param);
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            return ReturnDto.buildSuccessReturnDto(i);
        }
        return ReturnDto.buildFailedReturnDto("修改失败");
    }

    private void saveUpPriceLog(Trades tradesByTradesId, int r, Integer siteId, String userName, String userId, Integer platform, Long tradeId, Integer price, Object remark, String storeId) throws Exception {
        logger.info("保存修改订单价格:{},{},{},{},{},{},{},{},{},{}", tradesByTradesId, r, siteId, userName, userId, platform, tradeId, price, remark, storeId);
        TradesUpdatePriceLog log = new TradesUpdatePriceLog();
        if (r == 1) {
            //第一次修改 拿最新修改的原价
            Trades trades = tradesMapper.getTradesByTradesId(tradeId);
            log.setOriginalPrice(trades.getIsUpPrice());
        } else if (r == 0) {
            //第n次修改  直接拿原价
            log.setOriginalPrice(tradesByTradesId.getIsUpPrice());
        }
        log.setSiteId(siteId);
        if (platform > 120) {
            //平台是门店或者APP
            log.setStoreId(Integer.parseInt(storeId));
            Stores store = storesMapper.getStore(Integer.parseInt(storeId), siteId);
            if (store != null)
                log.setStoreName(store.getName());
        }
        log.setChangePriceBefore(tradesByTradesId.getRealPay());
        log.setChangePriceAfter(price);

        log.setOperationAccount(userName);
        String realName = "";
        Integer userid = Integer.parseInt(userId);
        log.setOperationId(userid);
        if (!"admin".equals(userName)) {
            if (platform == 110) {
                //商户平台登录,应该去查商户
                realName = ybManagerMapper.getUserNamebyPrimaryKey(userid);
            } else if (platform == 120 && !"".equals(userName)) {
                //商家平台登录，查询商家用户表
                realName = managerMapper.selectByNamePrimaryKey(userid, siteId);
            } else if (platform > 120) {
                //门店平台登录或APP登录，查询店员表
                realName = storeAdminExtMapper.getStoreAdminNameById(siteId.toString(), userid.toString());
            }
        } else {
            realName = userName;
        }
        if (realName == null)
            realName = "";
        log.setOperationUser(realName);
        log.setOperationPlatform(platform);
        log.setRemark(remark == null ? null : remark.toString());
        log.setTradesNo(tradeId.toString());
        log.setCreateTime(new Timestamp(System.currentTimeMillis()));
        tradesUpdatePriceLogMapper.insertTradesUpdatePriceLog(log);
    }

    public com.jk51.commons.dto.ReturnDto selectTradesUpProceLog(Map<String, Object> params) {
        if (params.get("siteId") == null)
            return com.jk51.commons.dto.ReturnDto.buildFailedReturnDto("siteId不能为空");
        Integer siteId = Integer.parseInt(params.get("siteId").toString());
        if (params.get("tradeId") == null)
            return com.jk51.commons.dto.ReturnDto.buildFailedReturnDto("订单ID不能为空");
        Long tradeId = Long.parseLong(params.get("tradeId").toString());
        List<TradesUpdatePriceLog> tradesUpdatePriceLogs = tradesUpdatePriceLogMapper.selectTradesUpProceLog(siteId, tradeId);
        return com.jk51.commons.dto.ReturnDto.buildSuccessReturnDto(tradesUpdatePriceLogs);
    }

    /**
     * 订单签收消息
     *
     * @param trades
     */
    public void sendOrderSign(Trades trades) {
        try {
            Map<String, Object> tradeMap = tradesMapper.getUserByTrade(trades.getTradesId());
            if (!StringUtil.isEmpty(tradeMap.get("post_style"))) {
                YbMerchant m = ybMerchantMapper.selectBySiteId(trades.getSiteId());
                String url = getOrderDUrl(trades.getSiteId(), (int) tradeMap.get("post_style"), trades.getTradesId());
                if (!StringUtil.isEmpty(tradeMap.get("open_id"))) {
                    privateSend.orderSign(trades.getSiteId(), tradeMap.get("open_id").toString(), url,
                            "您的商品已经签收啦，感谢您对" + m.getShop_weixin() + "的支持和信任。", "",
                            tradeMap.get("trades_id").toString(), trades.getRecevierName() + "," + trades.getRecevierMobile(), StringUtil.isEmpty(trades.getConfirmGoodsTime()) ? TimeUtil.getTimes(new Date()) : trades.getConfirmGoodsTime().toString());

                }
                 if(!StringUtil.isEmpty(tradeMap.get("ali_user_id"))){
                    aliPrivateSend.orderSign(trades.getSiteId(), tradeMap.get("ali_user_id").toString(), url,
                        "您的商品已经签收啦，感谢您对" + m.getShop_weixin() + "的支持和信任。", "",
                        tradeMap.get("trades_id").toString(), trades.getRecevierName() + "," + trades.getRecevierMobile(), StringUtil.isEmpty(trades.getConfirmGoodsTime()) ? TimeUtil.getTimes(new Date()) : trades.getConfirmGoodsTime().toString());
                }
            }
        } catch (Exception e) {
            logger.error("发送消息提醒失败：订单发货通知{}", trades.getTradesId(), e);
        }
    }

    /**
     * 退款成功
     *
     * @param trades
     */
    public void sendorderRefundSuccess(Trades trades, int realRefundMoney) {
        try {
            Map<String, Object> tradeMap = tradesMapper.getUserByTrade(trades.getTradesId());
            if (!StringUtil.isEmpty(tradeMap.get("open_id")) && !StringUtil.isEmpty(tradeMap.get("post_style"))) {
                String url = getOrderDUrl(trades.getSiteId(), (int) tradeMap.get("post_style"), trades.getTradesId());
                Double money = Double.parseDouble(realRefundMoney + "");
                privateSend.orderRefundSuccess(trades.getSiteId(), tradeMap.get("open_id").toString(), url,
                    "您申请的退款已经成功，一般1-7个工作日原路退回到您支付的账户中，超时未收到请及时联系我们。", "",
                    tradeMap.get("trades_id").toString(), (money / 100) + "元");
                if(!StringUtil.isEmpty(tradeMap.get("ali_user_id"))){
                    aliPrivateSend.orderRefundSuccess(trades.getSiteId(), tradeMap.get("ali_user_id").toString(), url,
                        "您申请的退款已经成功，一般1-7个工作日原路退回到您支付的账户中，超时未收到请及时联系我们。", "",
                        tradeMap.get("trades_id").toString(), (money / 100) + "元");
                }
            }
        } catch (Exception e) {
            logger.error("发送消息提醒失败：订单发货通知{}", trades.getTradesId(), e);
        }
    }

    /**
     * 退款失败
     */
    public void orderRefundFail(Integer siteId, String tradesId, String voucher) {
        try {
            Map<String, Object> tradeMap = tradesMapper.getUserByTrade(Long.parseLong(tradesId));
            if (!StringUtil.isEmpty(tradeMap.get("open_id")) && !StringUtil.isEmpty(tradeMap.get("post_style"))) {
                YbMerchant m = ybMerchantMapper.selectBySiteId(siteId);
                String url = getOrderDUrl(siteId, (int) tradeMap.get("post_style"), Long.parseLong(tradesId));
                privateSend.orderRefundFail(siteId, tradeMap.get("open_id").toString(), url,
                    "您申请的退款，因不符合退款条件，暂时无法处理，详情可以联系客服。", "",
                    tradeMap.get("trades_id").toString(), Double.parseDouble(tradeMap.get("real_pay").toString()) / 100 + "元", voucher);
                if(!StringUtil.isEmpty(tradeMap.get("ali_user_id"))){
                    aliPrivateSend.orderRefundFail(siteId, tradeMap.get("ali_user_id").toString(), url,
                        "您申请的退款，因不符合退款条件，暂时无法处理，详情可以联系客服。", "",
                        tradeMap.get("trades_id").toString(), Double.parseDouble(tradeMap.get("real_pay").toString()) / 100 + "元", voucher);
                }

            }
        } catch (Exception e) {
            logger.error("发送消息提醒失败：退款失败通知{}", tradesId, e);
        }
    }

    /**
     * 订单发货通知
     *
     * @param trades
     */
    public void sendorderSendNotice(Trades trades, String post_name, String post_number) {
        try {
            Map<String, Object> tradeMap = tradesMapper.getUserByTrade(trades.getTradesId());
            if (!StringUtil.isEmpty(tradeMap.get("post_style"))) {
                String url = getOrderDUrl(trades.getSiteId(), (int) tradeMap.get("post_style"), trades.getTradesId());
                if (StringUtil.isEmpty(post_name) && !StringUtil.isEmpty(post_number)) {
                    List<StoreAdminExt> storeAdminExt = storeAdminExtMapper.getStoreAdminExtBySiteIdAndStoreAdminId(trades.getSiteId(), Integer.parseInt(post_number));
                    if (!StringUtil.isEmpty(storeAdminExt) && storeAdminExt.size() == 1) {
                        post_number = storeAdminExt.get(0).getName() + "," + storeAdminExt.get(0).getMobile();
                    }
                }
                post_name = StringUtil.isEmpty(post_name) ? tradeMap.get("store_name").toString() : post_name;
                String goods_title = tradeMap.get("goods_title").toString().indexOf(",") == -1 ? tradeMap.get("goods_title").toString() : tradeMap.get("goods_title").toString().split(",")[0] + "等";
                if(!StringUtil.isEmpty(tradeMap.get("open_id"))){
                    privateSend.orderSendNotice(trades.getSiteId(), tradeMap.get("open_id").toString(), url,
                            "您购买的商品已经发货了，请及时签收哦。", "",
                            goods_title, tradeMap.get("trades_id").toString(), post_name, post_number, trades.getReceiverAddress());
                }
                if(!StringUtil.isEmpty(tradeMap.get("ali_user_id"))){
                    aliPrivateSend.orderSendNotice(trades.getSiteId(), tradeMap.get("ali_user_id").toString(), url,
                        "您购买的商品已经发货了，请及时签收哦。", "",
                        goods_title, tradeMap.get("trades_id").toString(), post_name, post_number, trades.getReceiverAddress());
                }
            }
        } catch (Exception e) {
            logger.error("发送消息提醒失败：订单发货通知{}", trades.getTradesId(), e);
        }
    }

    /**
     * 获取订单详情的URL微信端
     *
     * @param siteId     商家id
     * @param post_style 订单的类型//150(送货上门),160(门店自提)
     * @param tradesId   订单ID
     * @return
     */
    public String getOrderDUrl(Integer siteId, Integer post_style, Long tradesId) {
        String url = "";
        try {
            YbMerchant m = ybMerchantMapper.selectBySiteId(siteId);
            url = getShopwxUrl(siteId);
            url = url + "/newOrder/orderDetailSHSM?tradesId=" + tradesId;
            /*if (CommonConstant.POST_STYLE_DOOR == post_style) {//150(送货上门),160(门店自提)
                url = url + "/new/orderShsm?tradesId=" + tradesId;
            } else if (CommonConstant.POST_STYLE_EXTRACT == post_style) {
                url = url + "/new/orderMdzt?tradesId=" + tradesId;
            }*/
        } catch (Exception e) {
            logger.error("获取订单详情的URL微信端异常：{}", e);
        }
        return url;
    }

    /**
     * 获取订单详情的URL微信端（新【APP远程支付用】）
     *
     * @param siteId   商家id
     * @param tradesId 订单ID
     * @return
     */
    public String getOrderDUrlNew(Integer siteId, Long tradesId) {
        String url = "";
        try {
            YbMerchant m = ybMerchantMapper.selectBySiteId(siteId);
            url = getShopwxUrl(siteId);
            url = url + "/remotely/remotelyPay?tradesId=" + tradesId;
        } catch (Exception e) {
            logger.error("获取订单详情的URL微信端异常：{}", e);
        }
        return url;
    }

    /**
     * 获取订单详情的URL微信端（新【自提订单】）
     *
     * @param siteId   商家id
     * @param tradesId 订单ID
     * @return
     */
    public String getOrderDUrlNewZT(Integer siteId, Long tradesId) {
        String url = "";
        try {
            YbMerchant m = ybMerchantMapper.selectBySiteId(siteId);
            url = getShopwxUrl(siteId);
            url = url + "/newOrder/orderCommonDetail?tradesId=" + tradesId;
        } catch (Exception e) {
            logger.error("获取订单详情的URL微信端异常：{}", e);
        }
        return url;
    }

    public String getShopwxUrl(Integer siteId) {
        String url = "";
        try {
            YbMerchant m = ybMerchantMapper.selectBySiteId(siteId);
            url = m.getShopwx_url().replaceAll("，", ",").split(",")[0].indexOf("http") > -1 ? m.getShopwx_url().replaceAll("，", ",").split(",")[0] : "http://" + m.getShopwx_url().replaceAll("，", ",").split(",")[0];

        } catch (Exception e) {
            logger.error("获取URL微信端异常：{}", e);
        }
        return url;
    }

    public String getShopUrl(Integer siteId) {
        String url = "";
        try {
            YbMerchant m = ybMerchantMapper.selectBySiteId(siteId);
            url = m.getShop_url().replaceAll("，", ",").split(",")[0].indexOf("http") > -1 ? m.getShop_url().replaceAll("，", ",").split(",")[0] : "http://" + m.getShop_url().replaceAll("，", ",").split(",")[0];

        } catch (Exception e) {
            logger.error("获取URL微信端异常：{}", e);
        }
        return url;
    }

    public String getOldUrl(Integer siteId, String urlOld) {
        String url = "";
        try {
            String shortUrl = ShortUrlUtil.shortUrlOne(urlOld);
            url = getShopUrl(siteId) + "/" + shortUrl;
            stringRedisTemplate.opsForValue().set(shortUrl + "_shortUrl", urlOld);
        } catch (Exception e) {
            logger.error("获取URL微信端异常：{}", e);
        }
        return url;
    }

    public int insertTradesAssignLog(Integer siteId, Long tradesId, String userName, Integer beforeStore, Integer beforePostStyle, Integer assignedStores, Integer postStyle) {
        try {
            return tradesMapper.insertTradesAssignLog(siteId, tradesId, beforeStore, beforePostStyle, assignedStores, postStyle, userName);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int insertSelfTakenLog(Integer siteId, Integer storeId, Integer clerkId, String selfTakenCode) {
        return tradesMapper.insertSelfTakenLog(siteId, storeId, clerkId, selfTakenCode);
    }

    public List<Map<String, String>> discountDistributor(Trades trades) {
        List<Map<String, String>> discountList = new ArrayList<>();
        Distributor distributor = distributorMapper.selectByUid(trades.getBuyerId(), trades.getSiteId());
        if (Objects.nonNull(distributor) && distributor.getStatus() == 1) {
            trades.getOrdersList().stream().forEach(orders -> {
                Map<String, String> count = new HashMap<>();
                GoodsDistribute goodsDistribute = this.goodsDistributeMapper.selectByGoodsIdAndSiteId(orders.getSiteId(), orders.getGoodsId());//判断是否是分销商品
                RewardTemplate rewardTemplate = new RewardTemplate();//获取对应的奖励模板
                if (null != goodsDistribute && goodsDistribute.getDistributionTemplate() > 0) {
                    rewardTemplate = this.rewardTemplateMapper.selectByTemplateId(goodsDistribute.getDistributionTemplate());
                    if (Objects.nonNull(rewardTemplate)) {
                        String discount = rewardTemplate.getDiscount();
                        count.put("tradesId", trades.getTradesId().toString());
                        count.put("concessionNo", rewardTemplate.getId().toString());
                        count.put("concessionType", "推荐人优惠");

                        Map<String, Object> discounts = new TreeMap<>();
                        try {
                            discounts = JacksonUtils.json2map(rewardTemplate.getDiscount());
                            StringBuilder stringBuilder = new StringBuilder();
                            for (Map.Entry<String, Object> entry : discounts.entrySet()) {
                                String level = entry.getKey().substring(5, 6);
                                stringBuilder.append(level + "级:" + (Double.valueOf(entry.getValue().toString()) / 10) + "折" + "  ");
                            }
                            count.put("concessionView", stringBuilder.toString());
                            //过滤大于当前等级的消费折扣
                            Iterator<String> iter = discounts.keySet().iterator();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                if (Integer.valueOf(key.substring(5)) > distributor.getLevel()) {
                                    iter.remove();
                                    discounts.remove(key);
                                }
                            }
                            double max = 100.0;
                            System.out.println(discounts.values().stream().filter(x -> Integer.valueOf(x.toString()) > 0).count());
                            long totalLevel = discounts.values().stream().filter(x -> Integer.valueOf(x.toString()) > 0).count();
                            Stream maxLevel = discounts.values().stream().filter(x -> Integer.valueOf(x.toString()) > 0);
                            List tmpList = (List) maxLevel.collect(Collectors.toList());
                            if (tmpList.size() > 0) {
                                max = discounts.values().stream().filter(x -> Integer.valueOf(x.toString()) > 0).mapToDouble(x -> Integer.valueOf(x.toString())).summaryStatistics().getMin();
                            }

                            //折扣率
                            Double discountRate = 1.0; //初始化折扣为1：无折扣
                            if (discounts.containsKey("level" + distributor.getLevel()) && Integer.valueOf(discounts.get("level" + distributor.getLevel()).toString()) > 0) {
                                discountRate = Double.parseDouble(discounts.get("level" + distributor.getLevel()).toString()) * 0.01;//获得折扣率

                            } else if ((int) totalLevel < distributor.getLevel()) {
                                discountRate = max * 0.01;//获得折扣率
                            }
                            int distributeDiscountPrice = 0;
                            double price = orders.getGoodsNum() * (orders.getGoodsFinalPrice() == null ? 0 : orders.getGoodsFinalPrice() * (1 - discountRate));
                            distributeDiscountPrice = new BigDecimal(price).setScale(0, RoundingMode.HALF_UP).intValue();
                            count.put("concessionResult", String.valueOf(distributeDiscountPrice));
                            count.put("operateTime", trades.getCreateTime().toString());
                            discountList.add(count);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
        return discountList;
    }

    public List<Map<String, String>> integralDistributor(Trades trades) {
        List<Map<String, String>> discountList = new ArrayList<>();
        trades.getOrdersList().stream().forEach(orders -> {
            DecimalFormat df = new DecimalFormat("0.00");
            double price = orders.getGoodsFinalPrice() == null ? 0 : orders.getGoodsFinalPrice().doubleValue();
            String strPrice = df.format(price / 100);
            Map<String, String> count = new HashMap<>();
            count.put("tradesId", trades.getTradesId().toString());
            count.put("concessionNo", "无");
            count.put("concessionType", "积分");
            count.put("concessionResult", strPrice);
            count.put("concessionView", "积分兑换");
            count.put("operateTime", trades.getCreateTime().toString());
            discountList.add(count);
        });
        return discountList;
    }


    //是否首单的过滤
    public boolean checkFirstOrder(Integer siteId, Integer memberId) {
        if (Objects.isNull(memberMapper) || Objects.isNull(siteId) || Objects.isNull(memberId))
            return false;
        Member member = memberMapper.getMemberByMemberId(siteId, memberId);
        if (Objects.nonNull(member) && member.getBuyerId() != null) {
            //是否有下订单
            int count = tradesMapper.queryUserPromotionsFirstOrder(siteId, member.getBuyerId());
            //是否有退款记录
            int count1 = tradesMapper.queryUserPromotionsIsRefundOrder(siteId, member.getBuyerId());
            if (count < 1 && count1 == 0) {
                return true;
            }
        }
        return false;
    }

    public void newOrderToPayNotice(int siteId, Long tradesId, String open_id, String tradesStr) {
        String url = getShopwxUrl(siteId);
        //url = url + "/member/card?tradesId=" + tradesStr;
        String goods_title = "APP下单";
        Trades trades = getTradesByTradesId(tradesId);
        MerchantExt merchantExt = merchantExtMapper.selectByMerchantId(siteId);
        String newurl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + merchantExt.getWx_appid() + "&redirect_uri=" +URLEncoder.encode(url + "/member/card?tradesId="+tradesStr ) +  "&response_type=code&scope=snsapi_base&state=266#wechat_redirect";
        privateSend.orderToPayNotice(siteId, open_id, newurl,
            "您好，您有一笔订单还未支付，商品详情：" + goods_title + "，超过支付时间将被自动取消，赶快支付吧~", "点击进入订单详情完成支付",
            tradesId + "", Double.parseDouble(trades.getRealPay().toString()) / 100 + "元");
    }

    public void newOrderToPayNoticeAli(int siteId, Long tradesId, String open_id, String tradesStr) {
        String url = getShopwxUrl(siteId);
        //url = url + "/member/card?tradesId=" + tradesStr;
        String goods_title = "APP下单";
        Trades trades = getTradesByTradesId(tradesId);
        Map aliPayMerchantConfig = ybMerchantMapper.queryAliPayInfo(siteId + "");
        //MerchantExt merchantExt=merchantExtMapper.selectByMerchantId(siteId);
        String newurl = null;
        try {
            newurl = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=" + aliPayMerchantConfig.get("alipay_appid") + "&scope=auth_user&redirect_uri=" + URLEncoder.encode(url + "/member/card?tradesId=" + tradesStr, "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.info(e.getMessage());
        }
       aliPrivateSend.orderToPayNotice(siteId,open_id,newurl,"您好，您有一笔订单还未支付，商品详情：" + goods_title + "，超过支付时间将被自动取消，赶快支付吧~", "点击进入订单详情完成支付",
                tradesId+"", Double.parseDouble(trades.getRealPay().toString()) / 100 + "元",TimeUtil.getTimes(trades.getCreateTime()));
        /*aliPrivateSend.togetherOrderUnPay(siteId, open_id, newurl, "您好，您有一笔订单还未支付，商品详情：" + goods_title + "，超过支付时间将被自动取消，赶快支付吧~", "点击进入订单详情完成支付",
            tradesId + "", Double.parseDouble(trades.getRealPay().toString()) / 100 + "元", "keyword3", "keyword4", "keyword5");
*/    }

    public void insertOrderPayLog(Map<String, Object> params) {
        Integer sum = tradesMapper.selectOrderPayLog(params);
        if (StringUtil.isEmpty(sum) || sum == 0) {
            tradesMapper.insertOrderPayLog(params);
        }

    }

    /**
     * 服务商模式订单退款，退佣金
     *
     * @param trades
     * @param money
     * @throws BusinessLogicException
     */
    public void fwRefund(Trades trades, int money) {
        //服务商订单
        if (trades.getIsServiceOrder() == 1) {
            String scenes = trades.getTradesSource() + "";
            String deliveryType = trades.getPostStyle() + "";
            String payTypenew = trades.getTradeTypePayLine() + "";
            Map baseFeeMap = baseFeeService.getBaseFeeByCode(trades.getSiteId(), scenes, deliveryType, payTypenew);
            int result = Integer.parseInt(baseFeeMap.get("result") + "");
            if (result != 0) {
                int refuseRule = Integer.parseInt(baseFeeMap.get("refuseRule") + "");
                if (refuseRule == 1 || refuseRule == 2) {
                    Integer payCommissionold = balanceService.getMoneyByTradesId(trades.getTradesId());
                    //double baseFee = Double.parseDouble(baseFeeMap.get("feeRate")+"");
                    double payCommission = payCommissionold;
                    if (refuseRule == 2) {
                        payCommission = (double) money / trades.getRealPay() * payCommissionold;
                    }

                    //根据小数点后一位四舍五入
                    BigDecimal bd = new BigDecimal(payCommission);
                    //四舍五入后返回支付佣金(单位：分)
                    int payCommissionIntnew = bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                    //int payCommissionIntnew = balanceService.getOrderPayCommission(trades.getSiteId(), refund.getRealRefundMoney(), trades.getPayStyle());
                    //退款，退余额 修改余额操作日志
                    balanceService.insertBalanceDetail(trades.getSiteId(), 1, payCommissionIntnew, "订单退款", trades.getTradesId(), null,null);
                }
            }

        }
    }

    public Map<String,Object> getYonjinAndPost(Long tradesId) {
        return tradesMapper.getYonjinAndPost(tradesId);
    }

    public Integer getAccountResult(Long tradesId) {
        return tradesMapper.getAccountResult(tradesId);
    }
}
