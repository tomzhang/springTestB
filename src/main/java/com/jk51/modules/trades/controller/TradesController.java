package com.jk51.modules.trades.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.bar.BarCodeUtils;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.sms.SysType;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.GoodsParam;
import com.jk51.model.YbMeta;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.order.*;
import com.jk51.model.order.response.TradesResponse;
import com.jk51.model.promotions.activity.GroupPeopleNum;
import com.jk51.model.treat.MerchantExtTreat;
import com.jk51.modules.account.constants.AccountConstants;
import com.jk51.modules.coupon.service.CouponDetailService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.goods.service.GoodsService;
import com.jk51.modules.grouppurchase.service.GroupPurChaseService;
import com.jk51.modules.im.event.PaySuccessEvent;
import com.jk51.modules.integral.service.IntegerRuleService;
import com.jk51.modules.integral.service.IntegralService;
import com.jk51.modules.member.service.MemberService;
import com.jk51.modules.offline.service.ErpToolsService;
import com.jk51.modules.offline.service.OfflineOrderService;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.order.service.OrderPayService;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.pay.constants.PayConstant;
import com.jk51.modules.pay.exception.PayException;
import com.jk51.modules.pay.service.PayService;
import com.jk51.modules.pay.service.ali.AliPayApifw;
import com.jk51.modules.pay.service.ali.request.AliRequestParam;
import com.jk51.modules.promotions.service.PromotionsActivityService;
import com.jk51.modules.sms.service.CommonService;
import com.jk51.modules.sms.smsConfig.SmsEnum;
import com.jk51.modules.store.service.ClerkService;
import com.jk51.modules.trades.mapper.TradesUpdatePriceLogMapper;
import com.jk51.modules.trades.service.RefundService;
import com.jk51.modules.trades.service.StockupService;
import com.jk51.modules.trades.service.TradesConvertService;
import com.jk51.modules.trades.service.TradesService;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Controller
@RequestMapping("/trades")
public class TradesController {

    private static final Logger logger = LoggerFactory.getLogger(TradesController.class);

    @Autowired
    private TradesService tradesService;

    @Autowired
    private PayService payService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private StockupService stockupService;

    @Autowired
    private IntegralService integralService;

    @Autowired
    private CouponSendService couponSendService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TradesConvertService tradesConvertService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RefundService refundService;
    @Autowired
    private PromotionsActivityService promotionsActivityService;

    @Autowired
    private AliPayApifw aliPayApi;

    @Autowired
    private GroupPurChaseService groupPurChaseService;

    @Autowired
    private MerchantExtTreatMapper merchantExtTreatMapper;
    @Autowired
    private ClerkService clerkService;
    @Autowired
    private TradesUpdatePriceLogMapper tradesUpdatePriceLogMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CouponDetailService couponDetailService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private ErpToolsService erpToolsService;
    @Autowired
    private OfflineOrderService offlineOrderService;
    @Autowired
    private IntegerRuleService integerRuleService;

    /**
     * 送货上门
     *
     * @param tradesId       订单号
     * @param tradesStatus   订单状态
     * @param stockupStatus  备货状态
     * @param shippingStatus 发货状态
     * @return
     */
    @RequestMapping(value = "/dealDeliveryProcess", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto dealDeliveryProcess(@RequestParam Long tradesId, @RequestParam(required = false) Integer storeId,
                                         @RequestParam(required = false) Integer clerkId, @RequestParam Integer tradesStatus,
                                         @RequestParam(required = false, defaultValue = "110") Integer stockupStatus,
                                         @RequestParam(required = false, defaultValue = "110") Integer shippingStatus,
                                         @RequestParam(required = false, defaultValue = "") String storeAuthCode) {

        if (tradesId == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {
            Trades trades = tradesService.getTradesByTradesId(tradesId);
            if (trades == null) {
                logger.info("未查到与订单号：[{}]相关信息", tradesId);
                return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", tradesId));
            } else {
                if (tradesStatus == CommonConstant.WAIT_SELLER_SHIPPED && shippingStatus == CommonConstant.SHIPPED_WAIT_DELIVERY) {
                    trades.setTradesStatus(tradesStatus);
                    try {
                        if (stockupStatus == CommonConstant.STOCKUP_WAIT_READY) { //支付完成 将交易状态更新成 120（等待卖家发货）
                            trades.setIsPayment(CommonConstant.IS_PAYMENT_ONE);  //更改为已付款
                            trades.setSettlementStatus(CommonConstant.SETTLEMENT_STATUS_WAIT); //将资金结算状态改为待结算
                            tradesService.updateStatusToStock(trades, CommonConstant.WAIT_PAYMENT_BUYERS, CommonConstant.SOURCE_BUSINESS_WAIT_READY);
//                            schedulerTask.getBudgetDate(tradesId);
                            logger.info("订单号：[{}],您的订单已经成功受理，即将为您备货。", tradesId);
                            return ReturnDto.buildSuccessReturnDto(String.format("订单号：[%s],您的订单已经成功受理，即将为您备货。", tradesId));

                        } else {  // 待发货
                            trades.setStockupStatus(stockupStatus);
                            tradesService.updateStatusYiStock(trades, CommonConstant.WAIT_SELLER_SHIPPED, CommonConstant.SOURCE_BUSINESS_WAIT_READY);
                            stockupService.commitStockup(tradesId, trades.getSiteId(), storeId, clerkId);
                            logger.info("订单号：[{}],您的订单已经成功备货，即将为您发货", tradesId);
                            return ReturnDto.buildSuccessReturnDto(String.format("订单号：[%s],您的订单已经成功备货，即将发货。", tradesId));

                        }
                    } catch (Exception e) {
                        logger.error("订单号：[{}],System Error {}", tradesId, e);
                        return ReturnDto.buildSystemErrorReturnDto();
                    }
                }

                //送货上门  门店确认收货 用户确认收货
                if (tradesStatus == CommonConstant.USER_RECEIVED || tradesStatus == CommonConstant.STORE_RECEIVED) {
                    try {
                        if (trades.getTradesStatus() == CommonConstant.HAVE_SHIPPED && trades.getShippingStatus() == CommonConstant.SHIPPED) {
                            int old_trades_status = trades.getTradesStatus();
                            trades.setShippingStatus(CommonConstant.SHIPPED_RECEIVED);
                            trades.setTradesStatus(tradesStatus);
                            boolean flag = false;
                            if (tradesStatus == CommonConstant.USER_RECEIVED) {
                                tradesService.updateConfirmStatus(trades, old_trades_status, CommonConstant.SOURCE_BUSINESS_USER_RECEIVED);
                            } else {
                                if (storeId != null) {
                                    int result = tradesService.validationStoreAuthCode(trades.getSiteId(), EncryptUtils.encryptToSHA(EncryptUtils.encryptToSHA(storeAuthCode)), storeId);
                                    if (result > 0)
                                        tradesService.updateConfirmStatus(trades, old_trades_status, CommonConstant.SOURCE_BUSINESS_STORE_RECEIVED);
                                    else {
                                        logger.info("错误的授权码[{}],订单号[{}]。", storeAuthCode, tradesId);
                                        return ReturnDto.buildFailedReturnDto("请输入正确的授权码");
                                    }
                                } else {
                                    tradesService.updateConfirmStatus(trades, old_trades_status, CommonConstant.SOURCE_BUSINESS_STORE_RECEIVED);
                                }
                            }

                            logger.info("您的订单已经收货。");
                            return ReturnDto.buildSuccessReturnDto(String.format("订单号：[%s],您的订单已经收货。", tradesId));

                        }
                    } catch (Exception e) {
                        logger.info("订单号：[{}],确认收货失败", tradesId);
                        return ReturnDto.buildSystemErrorReturnDto();
                    }
                }
                logger.info("订单号：[{}],不满足相关业务逻辑", tradesId);
                return ReturnDto.buildFailedReturnDto("处理失败");
            }
        }
    }

    /**
     * 门店自提  更新为已自提 提货码提货
     * 订单来源: 110 (网站)，120（微信），130（app）, 140（门店后台），9999（其它）
     *
     * @param tradesId
     * @param
     * @param selfTakenCode
     * @param
     * @return
     */
    @RequestMapping(value = "/toStore", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto getToStore(@RequestParam Long tradesId, String selfTakenCode, @RequestParam String tradesSource) {
        if (tradesId == null || selfTakenCode == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {
            Trades trades = tradesService.getTradesByTradesId(tradesId);
            if (trades == null) {
                logger.info("未查到与订单号：[{}]相关信息", tradesId);
                return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", tradesId));
            } else {
                int oldTradesStatus = trades.getTradesStatus();

                try {

                    boolean flag = false;
                    if ("130".equals(tradesSource) && (oldTradesStatus == CommonConstant.WAIT_SELLER_SHIPPED || oldTradesStatus == CommonConstant.WAIT_QU_HUO)) {//APP门店助手
                        flag = true;
                    } else if (oldTradesStatus == CommonConstant.WAIT_SELLER_SHIPPED || oldTradesStatus == CommonConstant.WAIT_QU_HUO) {//门店后台
                        Trades td = tradesService.validationBarCode(trades.getSiteId(), selfTakenCode);
                        if (td != null) {
                            flag = true;
                        } else {
                            logger.info("订单号：[{}],提货码已失效", tradesId);
                            return ReturnDto.buildFailedReturnDto(String.format("订单号：[%s],提货码已失效", tradesId));
                        }
                    }

                    if (flag) {
                        trades.setTradesStatus(CommonConstant.HAVE_TAKE_GOODS);   //已自提
                        tradesService.updateYiZiTi(trades, oldTradesStatus, CommonConstant.SOURCE_BUSINESS_YI_QU);
                        logger.info("订单号：[{}],提货码正确，已提货。", tradesId);
                        return ReturnDto.buildSuccessReturnDto(String.format("订单号：[%s],提货码正确，已提货。", tradesId));
                    } else {
                        logger.info("订单号：[{}],自提失败", tradesId);
                        return ReturnDto.buildFailedReturnDto(String.format("订单号：[%s],自提失败", tradesId));
                    }

                } catch (Exception e) {
                    logger.error("订单号：[{}],System Error  {}", tradesId, e);
                    return ReturnDto.buildSystemErrorReturnDto();
                }
            }

        }

    }

    /**
     * 用户申请退款
     *
     * @param refund 退款记录
     * @return
     */
    @RequestMapping(value = "/applyRefund", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto applyRefund(Refund refund) {
        if (refund == null) {
            logger.info("参数错误");
            return ReturnDto.buildFailedReturnDto("参数错误，订单号");
        } else {
            try {
                Trades trades = tradesService.getTradesByTradesId(Long.parseLong(refund.getTradeId()));
                if (trades.getIsRefund() != CommonConstant.IS_REFUND_NO) {
                    logger.info("订单[{}]已经申请退款，请不要重复提交", refund.getTradeId());
                    return ReturnDto.buildFailedReturnDto(String.format("订单[%s]已经申请退款，请不要重复提交", refund.getTradeId()));
                }
                if (trades == null) {
                    logger.info("未查到与订单号：[{}]相关信息", refund.getTradeId());
                    return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", refund.getTradeId()));
                } else {
                    refund.setTradeStatus(trades.getTradesStatus());
                    refund.setMerchantId(trades.getSiteId());
                    refund.setMerchantName(trades.getSellerNick());
                    refund.setPayStyle(trades.getPayStyle());
                    //refund.setStoreId(trades.getTradesStore());
                    refund.setRealPay(trades.getRealPay());
                    refund.setFreight(trades.getPostFee());
                    refund.setStoreId(trades.getAssignedStores());
                    if (refund.getApplyRefundMoney() > trades.getRealPay()) {  //判断申请金额是否大于实际支付金额
                        logger.info("订单号：[{}]，退款金额错误", refund.getTradeId());
                        return ReturnDto.buildFailedReturnDto("请输入正确的退款金额");

                    } else {   //全额退款 ||  部分退款
                        refund.setStatus(CommonConstant.REFUND_WAIT);
                        trades.setIsRefund(CommonConstant.IS_REFUND_ONE);  // 更新为等待受理（退款中）
                        tradesService.applyRefund(refund, trades);
                        logger.info("订单号：[{}],您已申请退款，退款金额[{}]元。", refund.getTradeId(), refund.getApplyRefundMoney() / 100.0);
                        //申请退款短信
                        tradesService.sendApplyRefundMsg(trades);
                        return ReturnDto.buildSuccessReturnDto(String.format("订单号：[%s],您已申请退款，退款金额[%s]元。", refund.getTradeId(), refund.getApplyRefundMoney() / 100.0));
                    }
                }
            } catch (Exception e) {
                logger.error("申请退款失败，订单号[{}]，{}", refund.getTradeId(), e);
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }

    }

    /**
     * 门店后台发起退款
     *
     * @param refund
     * @return
     */
    @RequestMapping(value = "/storeApplyRefund", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto storeApplyRefund(Refund refund, @RequestParam(required = false, defaultValue = "1") int is_integral, @RequestParam(required = false, defaultValue = "0") int is_coupon) {
        try {
            if (refund != null) {
                Trades trades = tradesService.getTradesByTradesId(Long.parseLong(refund.getTradeId()));
                if (trades == null) {
                    logger.info("未查到与订单号：[{}]相关退款信息", refund.getTradeId());
                    return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关退款信息", refund.getTradeId()));
                } else {
                    //校验订单是否能退款
                    ReturnDto returnDto = refundService.validateOrderRefund(trades, trades.getRealPay());
                    if (!"OK".equals(returnDto.getStatus())) {
                        return returnDto;
                    }
                    if (refund != null) {
                        refund.setTradeStatus(trades.getTradesStatus());
                        refund.setMerchantId(trades.getSiteId());
                        refund.setMerchantName(trades.getSellerNick());
                        refund.setPayStyle(trades.getPayStyle());
                        refund.setStoreId(trades.getAssignedStores());
                        refund.setRealPay(trades.getRealPay());
                        refund.setFreight(trades.getPostFee());
                        /**
                         * 验证店长授权码
                         */
                        int result = tradesService.validationStoreAuthCode(refund.getSiteId(), EncryptUtils.encryptToSHA(EncryptUtils.encryptToSHA(refund.getStoreAuthCode())), refund.getStoreId());
                        if (result > 0) {
                            if (refund.getRealRefundMoney() > trades.getRealPay()) {
                                logger.info("订单号：[{}]，退款失败，请输入正确的金额。", trades.getTradesId());
                                return ReturnDto.buildFailedReturnDto(String.format("订单号：[%s]，退款失败，请输入正确的金额。", trades.getTradesId()));
                            } else {
                                String refundNo = null;
                                boolean flag = false;
                                String msg = null;

                                //判断支付方式
                                if (trades.getPayStyle().equals("wx")) {
                                    Map<String,String> map= payService.wxRefund(trades.getSiteId(), Long.parseLong(refund.getTradeId()), trades.getRealPay(), refund.getRealRefundMoney(), System.currentTimeMillis(),trades.getIsServiceOrder(),trades.getPayNumber());
                                    refundNo=map.get("refund_id");
                                    flag = true;
                                    msg=map.get("err_code_des");
                                } else if (trades.getPayStyle().equals("ali")) {
                                    Map<String,String> map = payService.aliRefund(trades.getSiteId(), Long.parseLong(refund.getTradeId()), System.currentTimeMillis(), refund.getRealRefundMoney(),trades.getIsServiceOrder());
                                    refundNo=map.get("refund_id");
                                    flag = true;
                                    msg=map.get("err_code_des");
                                } else if (trades.getPayStyle().equals(PayConstant.PAY_STYLE_CASH) || trades.getPayStyle().equals(PayConstant.PAY_STYLE_HEALTH_INSURANCE)) {
                                    refundNo = payService.cashRefund(Long.parseLong(refund.getTradeId()));
                                    flag = true;
                                } else if (trades.getPayStyle().equals("integral")) {
                                    refundNo = "0";
                                    flag = true;
                                } else {
                                    refundNo = null;
                                }

                                //如果退款编号为空
                                if (StringUtil.isEmpty(refundNo) && flag) {
                                    logger.info("退款编号[{}]为null,退款失败", refundNo);
                                    if(StringUtil.isEmpty(msg)){
                                        return ReturnDto.buildSystemErrorReturnDto();
                                    }else {
                                        return new ReturnDto("599", msg, null);
                                    }

                                }
                                refund.setStatus(CommonConstant.REFUND_SUCCESS);
                                refund.setRefundSerialNo(refundNo);
                                refund.setIsRefundCoupon(is_coupon);
                                refund.setIsRefundIntegral(is_integral);
                                int oldTradesStatus = trades.getTradesStatus();
                                trades.setSettlementStatus(CommonConstant.SETTLEMENT_STATUS_MAY);  //更新为可结算状态
                                trades.setTradesStatus(CommonConstant.TRADES_REFUND_SUCCESS);   //更新为已退款
                                trades.setIsRefund(CommonConstant.IS_REFUND_FOUR);
                                tradesService.merchantRefund(trades, oldTradesStatus, CommonConstant.SOURCE_BUSINESS_STORE_REFUND, refund, is_coupon, is_integral);
                                logger.info("订单号：[{}]，退款成功", refund.getTradeId());
                                tradesService.pushTradesToJZ(trades.getSiteId(), trades.getTradesId());
                                //退款成功提醒
                                tradesService.sendorderRefundSuccess(trades, refund.getRealRefundMoney());
                                return ReturnDto.buildSuccessReturnDto("退款成功。");

                            }
                        }
                    }
                    logger.info("退款失败，授权码错误，", refund);
                    return ReturnDto.buildFailedReturnDto("退款失败，授权码错误");
                }
            }
            logger.info("退款失败，请输入正确的参数，", refund);
            return ReturnDto.buildFailedReturnDto("退款失败，请输入正确的参数");

        } catch (Exception e) {
            logger.error("退款失败{} ，{}", refund, e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    /**
     * 商家发起退款
     *
     * @param refund
     * @return
     */
    /*@RequestMapping(value = "/merchantApplyRefund", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto merchantApplyRefund(Refund refund, @RequestParam(required = false, defaultValue = "1") int is_integral, @RequestParam(required = false, defaultValue = "0") int is_coupon) {
        try {
            if (refund != null&&!StringUtil.isEmpty(refund.getTradeId())) {
                refundService.zdApplyRefund(refund.getTradeId(),is_integral,is_coupon,refund.getRealRefundMoney(),200);
                return ReturnDto.buildSuccessReturnDto("退款成功。");
            }
            logger.info("退款失败，请输入正确的参数，", refund);
            return ReturnDto.buildFailedReturnDto("退款失败，请输入正确的参数");
        } catch (Exception e) {
            logger.error("商家发起退款失败{} ，{}", refund, e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }*/
    @RequestMapping(value = "/merchantApplyRefund", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto merchantApplyRefund(Refund refund, @RequestParam(required = false, defaultValue = "1") int is_integral, @RequestParam(required = false, defaultValue = "0") int is_coupon) {
        try {
            if (refund != null) {
                Trades trades = tradesService.getTradesByTradesId(Long.parseLong(refund.getTradeId()));
                if (trades == null) {
                    logger.info("未查到与订单号：[{}]相关退款信息", refund.getTradeId());
                    return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关退款信息", refund.getTradeId()));
                } else {
                    //校验订单是否能退款
                    ReturnDto returnDto = refundService.validateOrderRefund(trades, trades.getRealPay());
                    if (!"OK".equals(returnDto.getStatus())) {
                        return returnDto;
                    }
                    if (trades.getIsRefund() != 0) {
                        return ReturnDto.buildSuccessReturnDto("不能重复发起退款");
                    }
                    if (refund != null) {
                        refund.setTradeStatus(trades.getTradesStatus());
                        refund.setMerchantId(trades.getSiteId());
                        refund.setMerchantName(trades.getSellerNick());
                        refund.setPayStyle(trades.getPayStyle());
                        refund.setStoreId(trades.getAssignedStores());
                        refund.setRealPay(trades.getRealPay());
                        refund.setFreight(trades.getPostFee());

                        if (refund.getRealRefundMoney() > trades.getRealPay()) {
                            logger.info("订单号：[{}]，退款失败，请输入正确的金额。", trades.getTradesId());
                            return ReturnDto.buildFailedReturnDto(String.format("订单号：[%s]，退款失败，请输入正确的金额。", trades.getTradesId()));
                        } else {
                            String refundNo = null;
                            boolean flag = false;
                            String msg = null;
                            //判断支付方式
                            if (trades.getPayStyle().equals("wx")) {
                                Map<String,String> map = payService.wxRefund(trades.getSiteId(), Long.parseLong(refund.getTradeId()), trades.getRealPay(), refund.getRealRefundMoney(), System.currentTimeMillis(),trades.getIsServiceOrder(),trades.getPayNumber());
                                refundNo=map.get("refund_id");
                                flag = true;
                                msg=map.get("err_code_des");
                            } else if (trades.getPayStyle().equals("ali")) {
                                Map<String,String> map  = payService.aliRefund(trades.getSiteId(), Long.parseLong(refund.getTradeId()), System.currentTimeMillis(), refund.getRealRefundMoney(),trades.getIsServiceOrder());
                                refundNo=map.get("refund_id");
                                flag = true;
                                msg=map.get("err_code_des");
                            } else if (trades.getPayStyle().equals(PayConstant.PAY_STYLE_CASH) || trades.getPayStyle().equals(PayConstant.PAY_STYLE_HEALTH_INSURANCE)) {
                                refundNo = payService.cashRefund(Long.parseLong(refund.getTradeId()));
                                flag = true;
                            } else if (trades.getPayStyle().equals("integral")) {
                                refundNo = "0";
                                flag = true;
                            } else {
                                refundNo = null;
                            }

                            //如果退款编号为空
                            if (StringUtil.isEmpty(refundNo) && flag) {
                                logger.info("退款编号[{}]为null,退款失败", refundNo);
                                if(StringUtil.isEmpty(msg)){
                                    return ReturnDto.buildSystemErrorReturnDto();
                                }else {
                                    return new ReturnDto("599", msg, null);
                                }
                            }
                            refund.setStatus(CommonConstant.REFUND_SUCCESS);
                            refund.setRefundSerialNo(refundNo);
                            refund.setIsRefundCoupon(is_coupon);
                            refund.setIsRefundIntegral(is_integral);

                            int oldTradesStatus = trades.getTradesStatus();
                            trades.setSettlementStatus(CommonConstant.SETTLEMENT_STATUS_MAY);  //更新为可结算状态
                            trades.setTradesStatus(CommonConstant.TRADES_REFUND_SUCCESS);   //更新为已退款
                            trades.setIsRefund(CommonConstant.IS_REFUND_FOUR);
                            tradesService.merchantRefund(trades, oldTradesStatus, CommonConstant.SOURCE_BUSINESS_MERCHANT_REFUND, refund, is_coupon, is_integral);
                            logger.info("订单号：[{}]，退款成功", refund);
                            tradesService.pushTradesToJZ(trades.getSiteId(), trades.getTradesId());
                            //退款成功提醒
                            tradesService.sendorderRefundSuccess(trades,refund.getRealRefundMoney());
                            return ReturnDto.buildSuccessReturnDto("退款成功。");
                        }
                    }
                }
            }
            logger.info("退款失败，请输入正确的参数，", refund);
            return ReturnDto.buildFailedReturnDto("退款失败，请输入正确的参数");

        } catch (Exception e) {
            logger.error("商家发起退款失败{} ，{}", refund, e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    /**
     * 门店操作退款
     *
     * @param tradesId    tradesId
     * @param is_refund   是否同意退款
     * @param is_integral 是否同意退积分
     * @param is_coupon   是否同意退优惠券
     * @return
     */
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto refund(Integer siteId, String tradesId, Integer money, @RequestParam(required = false, defaultValue = "0") int is_refund, @RequestParam(required = false, defaultValue = "0") int is_integral, @RequestParam(required = false, defaultValue = "0") int is_coupon, String storeAuthCode, Integer storeId) {
        if (tradesId == null || siteId == null || money == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {

            if (is_refund == 1) {
                try {
                    Refund refund = tradesService.selectRefundInfo(siteId, tradesId);
                    if (refund != null && refund.getStatus() == CommonConstant.REFUND_WAIT) {
                        refund.setStatus(CommonConstant.REFUSED_REFUND);
                        tradesService.refusedRefund(tradesId, CommonConstant.REFUSED_REFUND);  //商家拒绝退款

                        //退款失败提醒
                        tradesService.orderRefundFail(siteId, tradesId, refund.getExplain());
                        logger.info("商家拒绝退款，订单号[{}]", tradesId);
                        return ReturnDto.buildSuccessReturnDto(String.format("商家拒绝退款，订单号：[%s]", tradesId));
                    } else {
                        logger.info("订单号：[{}]，无法更新退款流程", tradesId);
                        return ReturnDto.buildFailedReturnDto(String.format("订单号：[%s]，无法更新退款流程", tradesId));
                    }

                } catch (Exception e) {
                    logger.error("退款失败，订单号：[{}] {}", tradesId, e);
                    return ReturnDto.buildSystemErrorReturnDto();
                }

            } else {
                try {
                    Trades trades = tradesService.getTradesByTradesId(Long.parseLong(tradesId));
                    if (trades == null) {
                        logger.info("未查到与订单号：[{}] 相关信息", tradesId);
                        return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", tradesId));
                    } else {
                        /**
                         * 验证店长授权码
                         */
                        int result = tradesService.validationStoreAuthCode(siteId, EncryptUtils.encryptToSHA(EncryptUtils.encryptToSHA(storeAuthCode)), storeId);
                        if (result > 0) {
                            if (money > trades.getRealPay()) {
                                logger.info("订单号：[{}]，退款失败，请输入正确的金额。", tradesId);
                                return ReturnDto.buildFailedReturnDto(String.format("订单号：[%s]，退款失败，请输入正确的金额。", tradesId));
                            } else {
                                //开始退款
                                //调用退款接口
                                String refundNo = null;
                                boolean flag = false;
                                String msg = null;
                                //判断支付方式
                                if (trades.getPayStyle().equals("wx")) {
                                    Map<String,String> map = payService.wxRefund(trades.getSiteId(), Long.parseLong(tradesId), trades.getRealPay(), money, System.currentTimeMillis(),trades.getIsServiceOrder(),trades.getPayNumber());
                                    refundNo=map.get("refund_id");
                                    flag = true;
                                    msg=map.get("err_code_des");
                                } else if (trades.getPayStyle().equals("ali")) {
                                    Map<String,String> map = payService.aliRefund(trades.getSiteId(), Long.parseLong(tradesId), System.currentTimeMillis(), money,trades.getIsServiceOrder());
                                    refundNo=map.get("refund_id");
                                    flag = true;
                                    msg=map.get("err_code_des");
                                } else if (trades.getPayStyle().equals(PayConstant.PAY_STYLE_CASH) || trades.getPayStyle().equals(PayConstant.PAY_STYLE_HEALTH_INSURANCE)) {
                                    refundNo = payService.cashRefund(Long.parseLong(tradesId));
                                    flag = true;
                                } else if (trades.getPayStyle().equals("integral")) {
                                    refundNo = "0";
                                    flag = true;
                                } else {
                                    refundNo = null;
                                }

                                if (StringUtil.isEmpty(refundNo) && flag) {
                                    logger.info("退款编号[{}]为null,退款失败", refundNo);
                                    if(StringUtil.isEmpty(msg)){
                                        return ReturnDto.buildSystemErrorReturnDto();
                                    }else {
                                        return new ReturnDto("599", msg, null);
                                    }
                                }
                                int oldTradesStatus = trades.getTradesStatus();
                                trades.setSettlementStatus(CommonConstant.SETTLEMENT_STATUS_MAY);  //更新为可结算状态
                                trades.setTradesStatus(CommonConstant.TRADES_REFUND_SUCCESS);   //更新为已退款
                                trades.setIsRefund(CommonConstant.IS_REFUND_FOUR);
                                tradesService.updateRefundStatus(trades, oldTradesStatus, CommonConstant.SOURCE_BUSINESS_STORE_HAVE_REFUND, CommonConstant.REFUND_SUCCESS, siteId, is_coupon, is_integral, refundNo, money);
                                logger.info("订单号：[{}]，退款成功", tradesId);

                                try {
                                    tradesService.pushTradesToJZ(trades.getSiteId(), Long.parseLong(tradesId));
                                } catch (Exception e) {
                                    logger.error("推送付款成功订单或者退款成功订单给erp错误：" + e);
                                }
                                //退款成功提醒
                                tradesService.sendorderRefundSuccess(trades,money);
                                return ReturnDto.buildSuccessReturnDto("退款成功。");

                            }

                        } else {
                            logger.info("订单号：[{}]，请输入正确的授权码", tradesId);
                            return ReturnDto.buildFailedReturnDto(String.format("订单号：[%s]，请输入正确的授权码", tradesId));
                        }

                    }
                } catch (Exception e) {
                    logger.error("订单号：[{}],System Error  {}", tradesId, e);
                    return ReturnDto.buildSystemErrorReturnDto();
                }
            }
        }
    }

    /**
     * 商家是否同意退款
     *
     * @param tradesId    tradesId
     * @param is_refund   是否同意退款
     * @param is_integral 是否同意退积分
     * @param is_coupon   是否同意退优惠券
     * @return
     */
    @RequestMapping(value = "/merchantRefund", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto merchantRefund(Integer siteId, String tradesId, Integer money,
                                    @RequestParam(required = false, defaultValue = "0") int is_refund,
                                    @RequestParam(required = false, defaultValue = "0") int is_integral,
                                    @RequestParam(required = false, defaultValue = "0") int is_coupon) {
        if (tradesId == null || siteId == null || money == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {

            if (is_refund == 1) {
                try {
                    Refund refund = tradesService.selectRefundInfo(siteId, tradesId);
                    if (refund != null && refund.getStatus() == CommonConstant.REFUND_WAIT) {
                        refund.setStatus(CommonConstant.REFUSED_REFUND);
                        tradesService.refusedRefund(tradesId, CommonConstant.REFUSED_REFUND);  //商家拒绝退款

                        //退款失败提醒
                        tradesService.orderRefundFail(siteId, tradesId, refund.getExplain());
                        logger.info("商家拒绝退款，订单号[{}]", tradesId);
                        return ReturnDto.buildSuccessReturnDto(tradesId);
                    } else {
                        logger.info("订单号：[{}]，无法更新退款流程", tradesId);
                        return ReturnDto.buildFailedReturnDto(String.format("订单号：[%s]，无法更新退款流程", tradesId));
                    }

                } catch (Exception e) {
                    logger.error("退款失败，订单号：[{}] {}", tradesId, e);
                    return ReturnDto.buildSystemErrorReturnDto();
                }

            } else {
                try {
                    Trades trades = tradesService.getTradesByTradesId(Long.parseLong(tradesId));
                    if (trades == null) {
                        logger.info("未查到与订单号：[{}] 相关信息", tradesId);
                        return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", tradesId));
                    } else {
                        if (money > trades.getRealPay()) {
                            logger.info("订单号：[{}]，退款失败，请输入正确的金额。", tradesId);
                            return ReturnDto.buildFailedReturnDto(String.format("订单号：[%s]，退款失败，请输入正确的金额。", tradesId));
                        } else {
                            //开始退款
                            //调用退款接口
                            //调用退款接口
                            String refundNo = null;
                            boolean flag = false;
                            String msg = null;
                            //判断支付方式
                            if (trades.getPayStyle().equals("wx")) {
                                Map<String,String> map = payService.wxRefund(trades.getSiteId(), Long.parseLong(tradesId), trades.getRealPay(), money, System.currentTimeMillis(),trades.getIsServiceOrder(),trades.getPayNumber());
                                logger.info("退款-------------map:[{}]", map);
                                refundNo=map.get("refund_id");
                                flag = true;
                                msg=map.get("err_code_des");
                                if(StringUtil.isEmpty(msg)||"null".equals(msg)){
                                    msg=map.get("return_msg");
                                }
                            } else if (trades.getPayStyle().equals("ali")) {
                                Map<String,String> map = payService.aliRefund(trades.getSiteId(), Long.parseLong(tradesId), System.currentTimeMillis(), money,trades.getIsServiceOrder());
                                refundNo=map.get("refund_id");
                                flag = true;
                                msg=map.get("err_code_des");
                            } else if (trades.getPayStyle().equals(PayConstant.PAY_STYLE_CASH) || trades.getPayStyle().equals(PayConstant.PAY_STYLE_HEALTH_INSURANCE)) {
                                refundNo = payService.cashRefund(Long.parseLong(tradesId));
                                flag = true;
                            } else if (trades.getPayStyle().equals("integral")) {
                                refundNo = "0";
                                flag = true;
                            } else {
                                refundNo = null;
                            }

                            if (StringUtil.isEmpty(refundNo) && flag) {
                                logger.info((StringUtil.isEmpty(msg))+"退款编号[{}]为null,退款失败msg:"+msg, refundNo);
                                if(StringUtil.isEmpty(msg)){
                                    logger.info("111");
                                    return ReturnDto.buildSystemErrorReturnDto();
                                }else {
                                    logger.info("333");
                                    return new ReturnDto("599", msg, null);
                                }
                            }
                            int oldTradesStatus = trades.getTradesStatus();
                            trades.setSettlementStatus(CommonConstant.SETTLEMENT_STATUS_MAY);  //更新为可结算状态
                            trades.setTradesStatus(CommonConstant.TRADES_REFUND_SUCCESS);   //更新为已退款
                            trades.setIsRefund(CommonConstant.IS_REFUND_FOUR);
                            tradesService.updateRefundStatus(trades, oldTradesStatus, CommonConstant.SOURCE_BUSINESS_MERCHANT_HAVE_REFUND, CommonConstant.REFUND_SUCCESS, siteId, is_coupon, is_integral, refundNo, money);
                            logger.info("订单号：[{}]，退款成功", tradesId);
                            tradesService.pushTradesToJZ(trades.getSiteId(), trades.getTradesId());
                            //退款成功提醒
                            tradesService.sendorderRefundSuccess(trades,money);
                            return ReturnDto.buildSuccessReturnDto(tradesId);

                        }
                    }
                } catch (Exception e) {
                    logger.error("订单号：[{}],System Error  {}", tradesId, e);
                    return ReturnDto.buildSystemErrorReturnDto();
                }
            }
        }
    }

    @RequestMapping(value = "/getTradesExt", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto getTradesDetails(Long tradesId) {
        if (tradesId == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {
            try {
                Trades trades = tradesService.getTradesDetial(tradesId);
                if (trades != null) {
                    logger.info("查询订单详情成功", trades);
                    return ReturnDto.buildSuccessReturnDto(trades);
                } else {
                    logger.info("未查到与订单号：[{}] 相关退款信息", tradesId);
                    return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s] 相关信息", tradesId));
                }
            } catch (Exception e) {
                logger.error("订单号：[{}]，System Error", tradesId, e);
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }
    }

    /**
     * 直购
     *
     * @param trades_id
     * @param
     * @return
     */
    @RequestMapping(value = "/directPurchase", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto directPurchase(@RequestParam Long trades_id, Integer cashPaymentPay, Integer medicalInsuranceCardPay, Integer lineBreaksPay, String cashReceiptNote) {
        if (trades_id == null || cashPaymentPay == null || medicalInsuranceCardPay == null || lineBreaksPay == null || cashReceiptNote == null) {
            logger.info("订单号：[{}],参数错误", trades_id);
            return ReturnDto.buildFailedReturnDto(String.format("订单号：[%s],参数错误", trades_id));
        } else {
            Trades trades = tradesService.getTradesByTradesId(trades_id);
            trades.setTradeTypePayLine(medicalInsuranceCardPay > 0 ?310:300);
            if(lineBreaksPay>0){
                trades.setRealPay(trades.getRealPay()-lineBreaksPay);
            }
            if (trades == null) {
                logger.info("未查到与订单号：[{}]相关信息", trades_id);
                return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", trades_id));
            } else {
                int old_trades_status = trades.getTradesStatus();
                try {
                    if (old_trades_status != CommonConstant.WAIT_PAYMENT_BUYERS) {
                        logger.info("订单：[{}],无法处理的流程", trades_id);
                        return ReturnDto.buildFailedReturnDto(String.format("订单：[%s],取货失败。", trades_id));
                    } else {
                        if (trades.getPostStyle() == CommonConstant.POST_STYLE_DIRECT_PURCHASE) {
                            trades.setTradesStatus(CommonConstant.HAVE_TAKE_GOODS);
                            trades.setShippingStatus(CommonConstant.SHIPPED_RECEIVED);  //更新为确认收货
                            trades.setIsPayment(CommonConstant.IS_PAYMENT_ONE);  // 标记已付款
                            trades.setSettlementStatus(CommonConstant.SETTLEMENT_STATUS_WAIT);  //更新为待结算
                            //
                            boolean result = payService.cashPay(trades_id, cashPaymentPay, medicalInsuranceCardPay, lineBreaksPay, cashReceiptNote);   //22222
                            if (result) {
                                trades.setPayStyle(medicalInsuranceCardPay > 0 ? PayConstant.PAY_STYLE_HEALTH_INSURANCE : PayConstant.PAY_STYLE_CASH);
                                tradesService.updateDirectPurchaseStatus(trades, old_trades_status, CommonConstant.SOURCE_BUSINESS_TAKE_GOOD);
                                JSONObject jsonObject = integralService.integralAddForBuy(new HashMap() {{
                                    put("siteId", trades.getSiteId());
                                    put("buyerId", trades.getBuyerId());
                                    put("orderAmount", trades.getRealPay());
                                }});
                                tradesService.saveIntegral(trades, jsonObject);
                                integerRuleService.integralByOrderMulti(trades);
                                if (old_trades_status != CommonConstant.USER_NOT_PAY_CLOSE) {
                                    couponSendService.sendCouponByOrder(String.valueOf(trades.getTradesId()));
                                }
                                offlineOrderService.erpOrdersService(trades.getSiteId(),trades.getTradesId());
//                            schedulerTask.getBudgetDate(trades_id);  //haibo
                                logger.info("您的订单已完成取货,订单号：[{}]", trades_id);
                                //服务模式现金支付也扣订单佣金
                                if(trades.getIsServiceOrder()==1){
                                    orderPayService.updateOrderCashPayCommission(trades.getSiteId(), trades.getTradesId(), trades.getRealPay(), trades.getPayStyle(),trades);
                                }
                                return ReturnDto.buildSuccessReturnDto("您的订单已完成取货。");
                            } else {
                                logger.info("支付失败,订单号：[{}]", trades_id);
                                return ReturnDto.buildSuccessReturnDto("支付失败。");
                            }
                        } else {
                            trades.setPayStyle(medicalInsuranceCardPay > 0 ? PayConstant.PAY_STYLE_HEALTH_INSURANCE : PayConstant.PAY_STYLE_CASH);
                            orderService.updateTradesExt(trades_id.toString(),cashPaymentPay,medicalInsuranceCardPay,lineBreaksPay,cashReceiptNote);
                            if (tradesService.paySuccessCallback(trades)) {
                                return ReturnDto.buildSuccessReturnDto("支付成功。");
                            } else {
                                return ReturnDto.buildSuccessReturnDto("支付失败。");
                            }

                        }


                    }
                } catch (Exception e) {
                    logger.error("订单号：[{}],System Error  {}", trades_id, e);
                    return ReturnDto.buildSystemErrorReturnDto();
                }
            }
        }
    }

    /**
     * 取消订单
     *
     * @param tradesId
     * @param operator     1用户主动关闭 2代表商家关闭
     * @param closedReason 取消原因 订单关闭原因,: 110(操作太复杂了)，120(太贵了)，130(准备去门店购买)， 140（信息填写错误，重新下单），
     *                     150（活动不给力），（9999）其他原因  0（用户未填，就为空）
     */
    @RequestMapping(value = "/close", method = RequestMethod.POST)
    @ResponseBody
    public TradesResponse closeTrades(@RequestParam Long tradesId, Integer operator, @RequestParam(required = false, defaultValue = "0") int closedReason) {
        if (tradesId == null || operator == null || operator == null || operator == 0) {
            logger.info("订单号：[{}]取消订单失败,原因参数错误", tradesId);
            return TradesResponse.buildFailedReturnDto(String.format("订单号：[%s],取消订单失败,原因参数错误", tradesId));
        } else {
            try {
                int new_trades_status = operator == 1 ? CommonConstant.USER_NOT_PAY_CLOSE : CommonConstant.BUSINESSES_CLOSE_ORDER;
                Trades trades = tradesService.getTradesByTradesId(tradesId);
                if (trades == null) {
                    logger.info("未查到与订单号：[{}]相关信息", tradesId);
                    return TradesResponse.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", tradesId));
                } else {
                    int old_trades_status = trades.getTradesStatus();
                    /*if (trades.getTradesStatus() == CommonConstant.WAIT_PAYMENT_BUYERS ||
                        (trades.getTradesStatus() == CommonConstant.WAIT_SELLER_SHIPPED && trades.getPostStyle() == CommonConstant.POST_STYLE_EXTRACT) ||
                        (trades.getTradesStatus() == CommonConstant.WAIT_QU_HUO && trades.getPostStyle() == CommonConstant.POST_STYLE_EXTRACT)) {*/
                    if (trades.getTradesStatus() == CommonConstant.WAIT_PAYMENT_BUYERS ) {
                        Map map = payService.wxQueryOrderOld(Integer.parseInt(trades.getSiteId() + ""), trades.getTradesId() + "", null);
                        if (!StringUtil.isEmpty(map)){
                            if ("SUCCESS".equals(map.get("return_code")) && !"SUCCESS".equals(map.get("trade_state"))) {
                                payService.wxCloseNew(trades.getSiteId(), trades.getTradesId() + DateUtils.formatDate("_yyddHHmmss"));
                            } else if ("SUCCESS".equals(map.get("trade_state"))) {
                                logger.error("订单：[{}],取消订单失败，已经支付！微信支付:{}", tradesId, map);
                                return TradesResponse.buildSystemErrorReturnDto();
                            }
                        }
                        try{
                            AlipayTradeQueryResponse response = payService.aliQuery(Integer.parseInt(trades.getSiteId() + ""), trades.getTradesId(), null);
                            if (response.isSuccess() && !"TRADE_SUCCESS".equals(response.getTradeStatus())) {
                                AliRequestParam aliRequestParam = new AliRequestParam();
                                aliRequestParam.setOut_trade_no(trades.getTradesId() + "");
                                aliPayApi.close(aliRequestParam);
                            } else if ("TRADE_SUCCESS".equals(response.getTradeStatus())) {
                                AliRequestParam aliRequestParam = new AliRequestParam();
                                aliRequestParam.setOut_trade_no(trades.getTradesId() + "");
                                aliPayApi.close(aliRequestParam);
                                logger.error("订单：[{}],取消订单失败，已经支付！支付宝支付:{}", tradesId, aliRequestParam);
                                return TradesResponse.buildSystemErrorReturnDto();
                            }
                        } catch (Exception e) {
                            logger.error("支付宝取消异常  {}", e);

                        }
                        trades.setClosedResion(closedReason);
                        trades.setTradesStatus(new_trades_status);
                        String source_business = operator == 1 ? CommonConstant.SOURCE_BUSINESS_USER_CLOSE : CommonConstant.SOURCE_BUSINESS_MERCHANTS_CLOSE;
                        tradesService.closeTrades(trades, old_trades_status, source_business);

                        logger.info("订单：[{}],该订单已被取消。", tradesId);
                        return TradesResponse.buildSuccessReturnDto(String.format("订单：[%s],该订单已被取消", tradesId), null);

                    } else {
                        logger.info("订单：[{}]订单状态为：[{}],无法关闭订单", tradesId, old_trades_status);
                        return TradesResponse.buildFailedReturnDto(String.format("订单：[%s]订单状态为：[%s],无法关闭订单", tradesId, old_trades_status));
                    }
                }
            } catch (Exception e) {
                logger.error("订单：[{}],取消订单失败，System Error  {}", tradesId, e);
                return TradesResponse.buildSystemErrorReturnDto();
            }
        }
    }

    @RequestMapping("/cancelStockup")
    @ResponseBody
    public Map<String, Object> cancelStockup(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        String siteId = request.getParameter("siteId");
        String tradesId = request.getParameter("tradesId");
        if (StringUtil.isEmpty(tradesId)) {
            result.put("status", "ERROR");
            result.put("message", "缺少必填参数");
            return result;
        }

        try {
            tradesService.cancelStockup(siteId, tradesId);
            result.put("status", "OK");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", "取消备货异常！");
        }
        return result;
    }

    /**
     * 给前端页面展示的条形码
     *
     * @param msg
     * @return
     */
    @RequestMapping(value = "/bar/{msg}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto bar(@PathVariable("msg") String msg) {
        if (msg == null) {
            logger.info("参数错误");
            return ReturnDto.buildFailedReturnDto("原因参数错误");
        } else {
            try {
                String path = BarCodeUtils.generateFile_Code39(msg).getPath();
                logger.info("生成条形码成功,提货码[{}]", msg);
                return ReturnDto.buildSuccessReturnDto(path);
            } catch (Exception e) {
                logger.error("生成条形码失败,提货码[{}]", msg, e);
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }
    }

    /**
     * 手动生成提货码 门店自提才提货码，门店后台用
     *
     * @param tradesId
     * @return
     */
    @RequestMapping(value = "/manualGenerateBar", method = RequestMethod.POST)
    @ResponseBody
    public TradesResponse manualGenerateBar(@RequestParam Long tradesId) {  //手动生成条形码
        if (tradesId == null) {
            logger.info("订单号：[{}]取消订单失败,原因参数错误", tradesId);
            return TradesResponse.buildFailedReturnDto(String.format("订单号：[%s],取消订单失败,原因参数错误", tradesId));
        } else {
            try {
                Trades trades = tradesService.getTradesByTradesId(tradesId);
                if (trades != null) {

                    //获取门店联系电话
                    String storePhone = "";
                    String storeName = "";
                    String storeAddress = "";
                    String shortMessageSign = "";
                    Store store = null;
                    if (trades.getAssignedStores() != null) {
                        //storePhone = tradesService.getStorePhone(trades.getSiteId(), trades.getAssignedStores());
                        store = tradesService.selectStoreInfo(trades.getSiteId(), trades.getAssignedStores());
                        if (store != null) {
                            storePhone = store.getTel();
                            storeName = store.getName();
                            storeAddress = store.getAddress();
                        }
                    }
                    if (StringUtil.isEmpty(storePhone)) {
                        storePhone = trades.getSellerMobile();
                    }

                    YbMerchant ybMerchant = tradesService.selectMerchantfo(String.valueOf(trades.getSiteId()));
                    if (shortMessageSign != null) {
                        shortMessageSign = ybMerchant.getShort_message_sign();
                    }

//                    String barcode = tradesService.generateBarcode(trades.getSiteId());
//                    trades.setSelfTakenCode(barcode);
//                    int result = tradesService.updateBarcod(trades);  //更新提货码
//                    if (result > 0) {
                    String re = "-1";
                    trades = tradesService.getTradesByTradesId(tradesId);//重新查询
                    Object o = memberService.selectById(trades.getSiteId(), trades.getBuyerId()).getMobile();
                    if (!StringUtil.isEmpty(o)) {
                        String urlOld = tradesService.getOrderDUrlNewZT(trades.getSiteId(), tradesId);
                        String url=tradesService.getOldUrl(trades.getSiteId(),urlOld);
                        re = commonService.SendMessage(commonService.transformParam(trades.getSiteId(),o.toString(),null ,SysType.LADING_ADRESS,
                            SmsEnum.LADING_CODE_NEW,shortMessageSign, storeName, storeAddress,storePhone , trades.getSelfTakenCode(), url));
                                //commonService.getGoodSMS(trades.getSiteId(), o.toString(), trades.getSelfTakenCode(), TimeUtil.getTimes(trades.getSelfTakenCodeExpires()), storePhone, shortMessageSign, storeAddress);
                    }
                    if (!re.equals("0")) {
                        logger.info("订单号[{}]，发送短信失败");
                        return TradesResponse.buildFailedReturnDto(String.format("订单号[{%s}]，发送短信失败", tradesId));
                    }
                    logger.info("手动生成提货码成功，订单号：{}", tradesId);
                    return TradesResponse.buildSuccessReturnDto(String.format("手动生成提货码成功，订单号：[%s]", tradesId), trades.getSelfTakenCode());
//                    } else {
//                        logger.info("更新提货码失败，订单号：{}", tradesId);
//                        return TradesResponse.buildFailedReturnDto(String.format("更新提货码失败，订单号：[%s]", tradesId));
//                    }

                } else {
                    logger.info("未查到与订单号：[{}]相关信息", tradesId);
                    return TradesResponse.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", tradesId));
                }
            } catch (Exception e) {
                logger.error("订单号：{}，生成条形码失败,  {}", tradesId, e);
                return TradesResponse.buildSystemErrorReturnDto();
            }
        }
    }


    /**
     * 查看订单列表
     *
     * @param queryOrdersReq
     * @return
     */
    /*@RequestMapping(value = "/tradesInfo", method = RequestMethod.POST)
    @ResponseBody*/
    public ReturnDto selectTradesInfo(QueryOrdersReq queryOrdersReq) {
        if (queryOrdersReq == null) {
            logger.info("参数错误");
            return ReturnDto.buildFailedReturnDto("参数错误");
        } else {
            try {
                if (queryOrdersReq.getTradesStatus() != null) {
                    List list = Arrays.asList(queryOrdersReq.getTradesStatus().split(","));
                    queryOrdersReq.setList(list);
                }
                Page<Trades> page = PageHelper.startPage(queryOrdersReq.getPageNum(), queryOrdersReq.getPageSize());
                List<Trades> tradesList = tradesService.getTrades(queryOrdersReq);

                //默认交易成功后3天之内允许顾客退款
                int refundLimit = 3;
                if (!StringUtil.isEmpty(queryOrdersReq.getSiteId())) {
                    YbMeta ybMeta = tradesService.getYbMeta(Integer.parseInt(queryOrdersReq.getSiteId()), AccountConstants.META_KEY_FINISH);
                    if (ybMeta != null) {
                        refundLimit = Integer.parseInt(ybMeta.getMetaVal());
                    }
                }

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("tradesList", tradesList);
                map.put("page", page.toPageInfo());
                map.put("refundLimit", refundLimit);
                logger.info("查询订单列表成功");
                return ReturnDto.buildSuccessReturnDto(map);
            } catch (Exception e) {
                logger.error("查询订单列表失败 ", e);
                return ReturnDto.buildFailedReturnDto("查询订单列表失败");
            }
        }

    }


    /**
     * 查看订单列表
     *
     * @param queryOrdersReq
     * @return
     */
    @RequestMapping(value = "/tradesInfo", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto getTradesList(QueryOrdersReq queryOrdersReq) {
        if (queryOrdersReq == null) {// || StringUtil.isEmpty(queryOrdersReq.getSiteId())
            logger.info("参数错误");
            return ReturnDto.buildFailedReturnDto("参数错误");
        } else {
            try {
                if (queryOrdersReq.getTradesStatus() != null) {
                    List list = Arrays.asList(queryOrdersReq.getTradesStatus().split(","));
                    queryOrdersReq.setList(list);
                }
                //queryOrdersReq.setSiteId("100166");

                if (!StringUtil.isEmpty(queryOrdersReq.getOrderTimeStart())) {
                    queryOrdersReq.setOrderTimeStart(queryOrdersReq.getOrderTimeStart() + " 00:00:00");
                }
                if (!StringUtil.isEmpty(queryOrdersReq.getOrderTimeEnd())) {
                    queryOrdersReq.setOrderTimeEnd(queryOrdersReq.getOrderTimeEnd() + " 23:59:59");
                }


                /*Page<Trades> page = PageHelper.startPage(queryOrdersReq.getPageNum(), queryOrdersReq.getPageSize());
                List<Trades> tradesList = tradesService.getTradesList(queryOrdersReq);*/

//                PageHelper.clearPage();
                queryOrdersReq.setCount(true);
                logger.info(queryOrdersReq.toString());
                long total = tradesService.getTradesListCount(queryOrdersReq);
                com.github.pagehelper.Page pageBean = new com.github.pagehelper.Page(queryOrdersReq.getPageNum(), queryOrdersReq.getPageSize());//Page(int pageNum, int pageSize, boolean count, Boolean reasonable)
                pageBean.setReasonable(true);
                pageBean.setTotal(total);
                queryOrdersReq.setStartRow(pageBean.getStartRow());
                queryOrdersReq.setCount(false);
                List<Trades> tradesList = new ArrayList<Trades>();
                if(pageBean.getStartRow()==((queryOrdersReq.getPageNum()-1)*queryOrdersReq.getPageSize())){
                    tradesList = tradesService.getTradesList(queryOrdersReq);
                }
                for (Trades trades : tradesList) {
                    GroupPeopleNum groupPeopleNum = new GroupPeopleNum();
                    groupPeopleNum.setSiteId(trades.getSiteId());
                    groupPeopleNum.setTradesId(Long.valueOf(trades.getTradesId()));
                    if (trades.getIsUpPrice() != -1) {
                        List<TradesUpdatePriceLog> tradesUpdatePriceLogs = tradesUpdatePriceLogMapper.selectTradesUpProceLog(trades.getSiteId(), trades.getTradesId());
                        trades.setTradesUpdatePriceLogs(tradesUpdatePriceLogs);
                    }
                    Map<String, Object> groupSumPeopleAndPayingPeople = promotionsActivityService.getGroupSumPeopleAndPayingPeople(groupPeopleNum);
                    if (groupSumPeopleAndPayingPeople != null && groupSumPeopleAndPayingPeople.size() != 0) {
                        trades.setSum(Integer.parseInt(groupSumPeopleAndPayingPeople.get("sum").toString()));
                        trades.setPayingPeople(Integer.parseInt(groupSumPeopleAndPayingPeople.get("payingPeople").toString()));
                        trades.setGroupStatus(groupSumPeopleAndPayingPeople.get("groupStatus").toString());
                        trades.setBeginTime(groupSumPeopleAndPayingPeople.get("beginTime").toString().replace("T", " "));
                        if (!StringUtil.isEmpty(groupSumPeopleAndPayingPeople.get("aftergroupLiveTime"))) {
                            trades.setAftergroupLiveTime(groupSumPeopleAndPayingPeople.get("aftergroupLiveTime").toString().replace("T", " "));
                        }
                        if (!StringUtil.isEmpty(groupSumPeopleAndPayingPeople.get("liveTime"))) {
                            trades.setLiveTime(groupSumPeopleAndPayingPeople.get("liveTime").toString());
                        }
                        trades.setRole(Integer.parseInt(groupSumPeopleAndPayingPeople.get("role").toString()));
                        trades.setRemainPeople(Integer.parseInt(groupSumPeopleAndPayingPeople.get("remainPeople").toString()));
                        trades.setGoodsId(Integer.parseInt(groupSumPeopleAndPayingPeople.get("goodsId").toString()));
                        trades.setFightGroupId(Integer.parseInt(groupSumPeopleAndPayingPeople.get("fightGroupId").toString()));
                        if (!StringUtil.isEmpty(groupSumPeopleAndPayingPeople.get("proActivityId"))) {
                            trades.setProActivityId(Integer.parseInt(groupSumPeopleAndPayingPeople.get("proActivityId").toString()));
                        }
                        //未支付查询
                        if (!StringUtil.isEmpty(queryOrdersReq.getTradesId())&&queryOrdersReq.getTradesId().equals(trades.getTradesId()+"")) {
                            if (trades.getIsPayment() == 0 && trades.getTradesStatus() == 110) {
                                try {
                                    payService.wxQueryOrder(Integer.parseInt(trades.getSiteId() + ""), trades.getTradesId() + "", null);
                                } catch (PayException e) {
                                    logger.info("查询微信支付订单失败：" + e);
                                } catch (Exception e) {
                                    logger.info("查询支付订单失败：" + e);
                                }
                                try {
                                    AliRequestParam aliRequestParam = new AliRequestParam();
                                    aliRequestParam.setOut_trade_no(trades.getTradesId() + "");
                                    payService.aliquery(aliRequestParam);
                                } catch (Exception e) {
                                    logger.info("查询支付宝支付订单失败：" + e);
                                }
                            }
                        }
                    }
                    List<Orders> ordersList = ordersMapper.getOrdersListByTradesId(String.valueOf(trades.getSiteId()), trades.getTradesId());//获取订单下商品
                    if (ordersList != null && ordersList.size() > 0) {
                        trades.setOrdersList(ordersList);
                    } else {
                        logger.info("未查到与订单号：[{}]相关商品信息", queryOrdersReq.getTradesId());
                        throw new BusinessLogicException("未查到与订单号：[{}]相关商品信息" + queryOrdersReq.getTradesId() + "]");
                    }
                    //新增状态判断
                    tradesConvertService.convert(trades, queryOrdersReq.getPlatform(), queryOrdersReq.getType(),true);
                    //分销订单
                    if(null != trades.getDistributorId()  && trades.getDistributorId() > 0){
                        List<Map<String,String>>  list=tradesService.discountDistributor(trades);
                        trades.setDiscountList(list);
                    }
                    //积分兑换
                    if (Objects.nonNull(trades.getIntegralUsed()) && trades.getIntegralUsed() > 0 ) {
                        trades.setDiscountList(tradesService.integralDistributor(trades));
                    }
                    TradesExt tradesExt = tradesService.getTradesExtByTradesId(trades.getTradesId());
                    if(!StringUtil.isEmpty(tradesExt)&&!StringUtil.isEmpty(tradesExt.getLineBreaksPay())&&tradesExt.getLineBreaksPay()>0){
                        Map<String, String> map=new HashedMap();
                        map.put("tradesId",trades.getTradesId().toString());
                        map.put("concessionNo","无");
                        map.put("concessionType","线下优惠");
                        Double resule=Double.parseDouble(tradesExt.getLineBreaksPay()+"")/100;
                        map.put("concessionResult","-"+resule);
                        map.put("concessionView","现金或医保卡付款时线下优惠金额");
                        map.put("operateTime", trades.getPayTime().toString());
                        List discountList=StringUtil.isEmpty(trades.getDiscountList())?new ArrayList():trades.getDiscountList();
                        discountList.add(map);
                        trades.setDiscountList(discountList);
                    }
                    if(!StringUtil.isEmpty(trades.getIsUpPrice())&&trades.getIsUpPrice()!=-1){
                        Map<String, String> map=new HashedMap();
                        map.put("tradesId",trades.getTradesId().toString());
                        map.put("concessionNo","无");
                        map.put("concessionType","手工改价");
                        int lineBreaksPay=StringUtil.isEmpty(tradesExt.getLineBreaksPay())?0:tradesExt.getLineBreaksPay();
                        Double resule=-(Double.parseDouble((trades.getIsUpPrice()-trades.getRealPay()-lineBreaksPay)+"")/100);
                        map.put("concessionResult",resule+"");
                        map.put("concessionView","手工改价");
                        map.put("operateTime", trades.getUpdateTime().toString());
                        List discountList=StringUtil.isEmpty(trades.getDiscountList())?new ArrayList():trades.getDiscountList();
                        discountList.add(map);
                        trades.setDiscountList(discountList);
                    }

                    /*if (StringUtils.isNotBlank(queryOrdersReq.getTradesId())) {
                        trades.setMap(couponDetailService.findOrderCoupon(queryOrdersReq.getTradesId()));  //优惠券
                    }*/

                    //拼团信息
                    if (Integer.valueOf(50).equals(trades.getServceTpye())) {
                        Optional<Pair<String, List<Map<String, String>>>> groups = groupPurChaseService.getOtherOrdersAndStatusInGroup(trades.getSiteId(), trades.getTradesId());
                        if (groups.isPresent()) {
                            Pair<String, List<Map<String, String>>> pair = groups.get();
                            trades.setGroupOrders(pair);
                        }
                    }

                    //未支付查询
                    if (!StringUtil.isEmpty(queryOrdersReq.getTradesId())&&queryOrdersReq.getTradesId().equals(trades.getTradesId()+"")) {
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
                        logger.info("查询OrderCouponMap开始" );
                        Map<String, Object> OrderCouponMap=couponDetailService.findOrderCoupon(queryOrdersReq.getTradesId());
                        logger.info("查询OrderCouponMap：" +OrderCouponMap);

                        //下单店员门店
                        Store store = tradesService.selectStoreInfo(trades.getSiteId(), trades.getTradesStore());
                        OrderCouponMap.put("assignedStores",store);

                        //订单的佣金&运费
                        Map<String,Object> balanceMap = tradesService.getYonjinAndPost(trades.getTradesId());
                        OrderCouponMap.putAll(balanceMap);
                        //查询订单是否结算
                        Integer balanceCount = tradesService.getAccountResult(trades.getTradesId());
                        OrderCouponMap.put("balanceCount",balanceCount);
                        trades.setMap(OrderCouponMap);  //优惠券
                    }
                }
                PageInfo pageInfo = new PageInfo<>(pageBean);
                pageInfo.setList(tradesList);
                //默认交易成功后3天之内允许顾客退款
                int refundLimit = 3;
                if (tradesList != null && tradesList.size() != 0) {
                    try {
                        //tradesService.settingTradesList(tradesList);//设置订单附加信息
                        tradesService.settingTradesList2(tradesList);
                        if (!StringUtil.isEmpty(queryOrdersReq.getSiteId())) {
                            YbMeta ybMeta = tradesService.getYbMeta(Integer.parseInt(queryOrdersReq.getSiteId()), AccountConstants.META_KEY_FINISH);
                            if (ybMeta != null) {
                                refundLimit = Integer.parseInt(ybMeta.getMetaVal());
                            }
                        }
                    } catch (Exception e) {
                        logger.error("查询允许顾客退款天数设置失败 ", e);
                    }
                } else {
                    tradesList = null;
                }

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("tradesList", tradesList);
                map.put("page", pageInfo);
                map.put("refundLimit", refundLimit);
                YbMerchant ybMerchant = tradesService.selectMerchantfo(queryOrdersReq.getSiteId());
                if(!StringUtil.isEmpty(ybMerchant)){
                    map.put("servicePhone", ybMerchant.getService_phone());
                }
                logger.info("查询订单列表成功");
                return ReturnDto.buildSuccessReturnDto(map);
            } catch (Exception e) {
                logger.error("查询订单列表失败 ", e);
                return ReturnDto.buildFailedReturnDto("查询订单列表失败");
            }
        }

    }

    /**
     * 根据订单状态查询订单数量
     *
     * @param queryOrdersReq
     * @return
     */
    @RequestMapping(value = "tradesInfoCount", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto getTradesListCount(QueryOrdersReq queryOrdersReq) {
        if (queryOrdersReq == null) {
            logger.info("参数错误");
            return ReturnDto.buildFailedReturnDto("参数错误");
        } else {
            try {
                queryOrdersReq.setCount(true);
//                新订单状态订单状态如下（10种）：
//                1-待付款(110)
//                3-待备货(120+110)
//                4-待发货(120+120)
//                5-已发货（130）
//                6-待自提（200）
//                8-退款中（is_refund：100）
//                11-其他
                List<Long> list = new ArrayList<>();

                queryOrdersReq.setNewTradesStatus(null);
                list.add(tradesService.getTradesListCount(queryOrdersReq));

                int[] status={1,3,4,5,6,8};
                Arrays.stream(status).forEach(i->{
                    queryOrdersReq.setNewTradesStatus(i);
                    list.add(tradesService.getTradesListCount(queryOrdersReq));
                });

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("countList", list);
                return ReturnDto.buildSuccessReturnDto(map);
            } catch (Exception e) {
                logger.error("根据订单状态查询订单数量失败", e);
                return ReturnDto.buildFailedReturnDto("根据订单状态查询订单数量失败");
            }
        }

    }

    /**
     * 根据postStyle查询订单数量
     *
     * @param queryOrdersReq
     * @return
     */
    @RequestMapping(value = "tradesInfoPostStyleCount", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto getTradesListPostStyleCount(QueryOrdersReq queryOrdersReq) {
        if (queryOrdersReq == null) {
            logger.info("参数错误");
            return ReturnDto.buildFailedReturnDto("参数错误");
        } else {
            try {
                queryOrdersReq.setCount(true);
                List<Long> list = new ArrayList<>();

                queryOrdersReq.setPostStyle(null);
                list.add(tradesService.getTradesListCount(queryOrdersReq));

                String[] status={"150","160","170"};
                Arrays.stream(status).forEach(i->{
                    queryOrdersReq.setPostStyle(i);
                    list.add(tradesService.getTradesListCount(queryOrdersReq));
                });

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("countList", list);
                return ReturnDto.buildSuccessReturnDto(map);
            } catch (Exception e) {
                logger.error("根据postStyle查询订单数量失败", e);
                return ReturnDto.buildFailedReturnDto("根据postStyle查询订单数量失败");
            }
        }

    }

    /**
     * 查询最近新新订单
     *
     * @param time
     * @param queryOrdersReq
     * @return
     */
    @RequestMapping("/getLastTrades")
    @ResponseBody
    public ReturnDto getLastTrades(Integer time, QueryOrdersReq queryOrdersReq) {
        if (time == null) {
            return ReturnDto.buildFailedReturnDto("time不能为空");
        }
        if (queryOrdersReq == null || queryOrdersReq.getSiteId() == null) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        String tradesId = null;
        if (queryOrdersReq.getSiteId() != null && queryOrdersReq.getStoresId() == null) {
            //System.out.println("获取商家订单提醒记录：orderRemind_Site_"+queryOrdersReq.getSiteId()+"："+stringRedisTemplate.opsForValue().get("orderRemind_Site_"+queryOrdersReq.getSiteId()));
            tradesId = stringRedisTemplate.opsForValue().get("orderRemind_Site_" + queryOrdersReq.getSiteId());
        }
        if (queryOrdersReq.getSiteId() != null && queryOrdersReq.getStoresId() != null) {
            //System.out.println("获取门店订单提醒记录：orderRemind_Store_"+queryOrdersReq.getSiteId()+queryOrdersReq.getStoresId()+"："+stringRedisTemplate.opsForValue().get("orderRemind_Store_"+queryOrdersReq.getSiteId()+queryOrdersReq.getStoresId()));
            tradesId = stringRedisTemplate.opsForValue().get("orderRemind_Store_" + queryOrdersReq.getSiteId() + queryOrdersReq.getStoresId());
        }
        List<Trades> trades = null;
        if (tradesId != null) {
            trades = new ArrayList<>();
            Trades t = new Trades();
            t.setTradesId(Long.parseLong(tradesId.replaceAll("\"", "")));
            trades.add(t);
            if (queryOrdersReq.getSiteId() != null && queryOrdersReq.getStoresId() == null) {
                stringRedisTemplate.delete("orderRemind_Site_" + queryOrdersReq.getSiteId());
                //System.out.println("获取商家订单提醒记录后清除：orderRemind_Site_"+queryOrdersReq.getSiteId()+"："+stringRedisTemplate.opsForValue().get("orderRemind_Site_"+queryOrdersReq.getSiteId()));
            }
            if (queryOrdersReq.getSiteId() != null && queryOrdersReq.getStoresId() != null) {
                stringRedisTemplate.delete("orderRemind_Store_" + queryOrdersReq.getSiteId() + queryOrdersReq.getStoresId());
                //System.out.println("获取门店订单提醒记录后清除：orderRemind_Store_"+queryOrdersReq.getSiteId()+queryOrdersReq.getStoresId()+"："+stringRedisTemplate.opsForValue().get("orderRemind_Store_"+queryOrdersReq.getSiteId()+queryOrdersReq.getStoresId()));
            }
        }

        /*queryOrdersReq.setPayTimeStart(new Timestamp(System.currentTimeMillis() - time * 60000));
        queryOrdersReq.setIsPayment(1);
        List<Trades> trades = tradesService.getStoreTrades(queryOrdersReq);*/
        Map<String, Object> map = new HashMap<>();
        if (trades != null && trades.size() != 0) {
            map.put("code", 200);
            map.put("data", trades);
            return ReturnDto.buildSuccessReturnDto(map);
        } else {
            map.put("code", 400);
            return ReturnDto.buildSuccessReturnDto(map);
        }
    }

    /**
     * 查看退款详情
     *
     * @param tradesId
     * @return
     */
    @RequestMapping(value = "/selectRefundInfo", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto selectRefundInfo(Integer siteId, String tradesId) {
        if (siteId == null || tradesId == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {
            logger.info("tradesId：" + tradesId);
            try {
                Refund refund = tradesService.selectRefundInfo(siteId, tradesId);
                if (refund != null) {
                    logger.info("查询[{}]退款详情成功  {}", tradesId, refund);
                    return ReturnDto.buildSuccessReturnDto(refund);
                } else {
                    logger.info("查询[{}]退款详情失败", tradesId);
                    return ReturnDto.buildFailedReturnDto(String.format("未查到[%s]相关退款信息", tradesId));
                }
            } catch (Exception e) {
                logger.info("查询[{}]退款详情失败, System error {}", tradesId, e);
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }
    }

    /**
     * 查询单个订单详情
     *
     * @param tradesId
     * @return
     */
    @RequestMapping(value = "/selectTradesDetails", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto selectTradesDetails(Long tradesId) {
        if (tradesId == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {
            logger.info("tradesId：" + tradesId);
            try {
                Trades trades = tradesService.getTradesDetial(tradesId);
                if (trades != null) {
                    logger.info("查询[{}]订单详情成功 {}", tradesId, trades);
                    return ReturnDto.buildSuccessReturnDto(trades);
                } else {
                    logger.info("查询[{}]订单详情失败", tradesId);
                    return ReturnDto.buildFailedReturnDto(String.format("未查到[%s]相关订单信息", tradesId));
                }

            } catch (Exception e) {
                logger.info("查询[{}]订单详情失败, System error {}", tradesId, e);
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }
    }

    /**
     * 送货上门 批量备货
     *
     * @param tradesIds
     * @return
     */
    @RequestMapping(value = "/toDoorBatchStockup", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto toDoorBatchStockup(@RequestParam List<Long> tradesIds) {
        logger.info("批量备货,打印参数：" + tradesIds);
        try {
            if (tradesIds != null && tradesIds.size() > 0) {
                tradesService.toDoorBatchStockup(tradesIds);
                logger.info("送货上门，批量备货成功");
                return ReturnDto.buildSuccessReturnDto("送货上门，批量备货成功");
            } else {
                logger.info("送货上门，批量备货失败，参数错误");
                return ReturnDto.buildFailedReturnDto("送货上门，批量备货失败，参数错误");
            }

        } catch (Exception e) {
            logger.error("送货上门，批量备货异常", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    /**
     * 送货上门 批量发货
     *
     * @param tradesIds
     * @return
     */
    @RequestMapping(value = "/toDoorBatchShipping", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto toDoorBatchShipping(@RequestParam List<Long> tradesIds) {
        try {
            if (tradesIds != null && tradesIds.size() > 0) {
                tradesService.toDoorBatchShipping(tradesIds);
                logger.info("送货上门，批量发货成功");
                return ReturnDto.buildSuccessReturnDto("送货上门，批量发货成功");
            } else {
                logger.info("送货上门，批量发货失败，参数错误");
                return ReturnDto.buildFailedReturnDto("送货上门，批量发货失败，参数错误");
            }
        } catch (Exception e) {
            logger.error("送货上门，批量发货异常", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    /**
     * 送货上门  批量确认收货
     *
     * @param tradesIds
     * @return
     */
    @RequestMapping(value = "/toDoorBatchConfirmStockup", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto toDoorBatchConfirmStockup(@RequestBody List<Long> tradesIds) {
        try {
            if (tradesIds != null && tradesIds.size() > 0) {
                tradesService.toDoorBatchConfirmShipping(tradesIds);
                logger.info("送货上门，批量确认收货成功");
                return ReturnDto.buildSuccessReturnDto("送货上门，批量确认收货成功");
            } else {
                logger.info("送货上门，批量确认收货失败，参数错误");
                return ReturnDto.buildFailedReturnDto("送货上门，批量确认收货失败，参数错误");
            }

        } catch (Exception e) {
            logger.error("送货上门，批量确认收货异常", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    /**
     * 门店自提 批量备货
     *
     * @param tradesIds
     * @return
     */
    @RequestMapping(value = "/toStoreoorBatchStockup", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto toStoreoorBatchStockup(@RequestParam List<Long> tradesIds) {
        try {
            if (tradesIds != null && tradesIds.size() > 0) {
                tradesService.toStoreBatchStockup(tradesIds);
                logger.info("门店自提，批量备货成功");
                return ReturnDto.buildSuccessReturnDto("门店自提，批量备货成功");
            } else {
                logger.info("门店自提，批量备货失败，参数错误");
                return ReturnDto.buildFailedReturnDto("门店自提，批量备货失败，参数错误");
            }
        } catch (Exception e) {
            logger.error("门店自提，批量备货异常", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    /**
     * 验证提货码
     *
     * @param siteId
     * @param selfTakenCode
     * @return
     */
    @RequestMapping(value = "/validationSelfTakenCode", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto validationSelfTakenCode(Integer siteId, String selfTakenCode, Integer storeId, Integer clerkId) {

        tradesService.insertSelfTakenLog(siteId, storeId, clerkId, selfTakenCode);

        if (siteId == null || selfTakenCode == null) {
            logger.info("验证提货码,参数错误");
            return ReturnDto.buildFailedReturnDto("验证提货码,参数错误");
        } else {
            try {
                Trades trades = tradesService.validationBarCode(siteId, selfTakenCode);
                if (trades != null) {
                    Trades trade = tradesService.getTradesDetial(trades.getTradesId());

                    Member member = memberService.selectById(trade.getSiteId(), trade.getBuyerId());
                    if (member != null) {
                        trade.setReceiverPhone(member.getMobile());
                        trade.setRecevierName(member.getName());
                    }

                    MerchantExtTreat merchantExtTreat = merchantExtTreatMapper.selectByMerchantId(siteId);
//                    if(merchantExtTreat!=null && merchantExtTreat.getSelf_taken_flag() == 1 && trades.getSelfTakenStore() != storeId){//只能在下单店提货
//                        logger.info("该订单只能在下单店提货");
//                        return ReturnDto.buildFailedReturnDto("该订单只能在下单店提货");
//                    }

                    if (trades.getServceTpye() != null && trades.getServceTpye() == 50) {//如果是拼团订单
                        int i = groupPurChaseService.checkGroupPurchaseTradeStatusForDeliverGoods(trades.getTradesId());
                        if (i == 1) {
                            logger.info("该订单正在拼团中，暂时不能提货");
                            return ReturnDto.buildFailedReturnDto("该订单正在拼团中，暂时不能提货");
                        } else if (i == 3) {
                            logger.info("该订单拼团未成功，不能提货了");
                            return ReturnDto.buildFailedReturnDto("该订单拼团未成功，不能提货了");
                        }
                    }

                    trade.setSelf_taken_flag(merchantExtTreat.getSelf_taken_flag());
                    logger.info("提货码正确，{}", trade);
                    return ReturnDto.buildSuccessReturnDto(trade);
                } else {
                    logger.info("请输入正确的提货码");
                    return ReturnDto.buildFailedReturnDto("请输入正确的提货码");
                }
            } catch (Exception e) {
                logger.error("提货码错误[{}]", selfTakenCode);
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }
    }

    /**
     * 门店店长输入授权码提货
     *
     * @param siteId
     * @param tradesId
     * @param storeId
     * @param storeAuthCode
     * @return
     */
    @RequestMapping(value = "/storePickGood", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto storePickGood(Integer siteId, Long tradesId, Integer storeId, String storeAuthCode) {
        if (siteId == null || tradesId == null || storeId == null || storeAuthCode == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {
            try {
                int result = tradesService.validationStoreAuthCode(siteId, EncryptUtils.encryptToSHA(EncryptUtils.encryptToSHA(storeAuthCode)), storeId);
                if (result > 0) {
                    Trades trades = tradesService.getTradesByTradesId(tradesId);
                    if (trades != null) {
                        int oldTradesStatus = trades.getTradesStatus();
                        if (oldTradesStatus == CommonConstant.WAIT_QU_HUO) {
                            trades.setTradesStatus(CommonConstant.HAVE_TAKE_GOODS);
                            tradesService.updateYiZiTi(trades, oldTradesStatus, CommonConstant.SOURCE_BUSINESS_YI_QU);
                            logger.info("订单号：[{}],已提货。", tradesId);
                            return ReturnDto.buildSuccessReturnDto(String.format("订单号：[%s],已提货。", tradesId));
                        } else {
                            logger.info("订单号[{}]，无法处理的流程");
                            return ReturnDto.buildFailedReturnDto(String.format("订单号[%s]，无法处理的流程", tradesId));
                        }
                    } else {
                        logger.info("未查到与订单号：[{}]相关信息", tradesId);
                        return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", tradesId));
                    }

                } else {
                    logger.info("错误的授权码[{}],订单号[{}]。", storeAuthCode, tradesId);
                    return ReturnDto.buildFailedReturnDto("请输入正确的授权码");
                }
            } catch (Exception e) {
                logger.error("提货失败，订单号：[{}]", tradesId);
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }
    }

    /**
     * 验证提货码后 提货
     *
     * @param tradesId
     * @return
     */
    @RequestMapping(value = "/pickGood", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto pickGood(Long tradesId) {
        if (tradesId == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {
            try {
                Trades trades = tradesService.getTradesByTradesId(tradesId);
                if (trades != null) {
                    int oldTradesStatus = trades.getTradesStatus();
                    if (oldTradesStatus == CommonConstant.WAIT_SELLER_SHIPPED || oldTradesStatus == CommonConstant.WAIT_QU_HUO) {
                        trades.setTradesStatus(CommonConstant.HAVE_TAKE_GOODS);
                        tradesService.updateYiZiTi(trades, oldTradesStatus, CommonConstant.SOURCE_BUSINESS_YI_QU);
                        logger.info("订单号：[{}],已提货。", tradesId);
                        return ReturnDto.buildSuccessReturnDto(String.format("订单号：[%s],已提货。", tradesId));
                    } else {
                        logger.info("订单号[{}]，无法处理的流程");
                        return ReturnDto.buildFailedReturnDto(String.format("订单号[%s]，无法处理的流程", tradesId));
                    }


                } else {
                    logger.info("未查到与订单号：[{}]相关信息", tradesId);
                    return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", tradesId));
                }
            } catch (Exception e) {
                logger.error("提货失败，订单号：[{}]", tradesId);
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }
    }

    /**
     * 门店自提 逾期未到店取消订单
     *
     * @param tradesId
     * @return
     */
    @RequestMapping(value = "/toStoreCancel", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto toStoreCancel(Long tradesId) {
        if (tradesId == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {
            try {
                Trades trades = tradesService.getTradesByTradesId(tradesId);
                if (trades != null) {
                    int oldTradesStatus = trades.getTradesStatus();
                    if (oldTradesStatus == CommonConstant.WAIT_QU_HUO) {
                        trades.setTradesStatus(CommonConstant.SYSTEM_STORE_CANCEL);
                        tradesService.updateYiZiTi(trades, oldTradesStatus, CommonConstant.SOURCE_BUSINESS_YI_QU);
                        logger.info("订单号：[{}],已取消订单。", tradesId);
                        return ReturnDto.buildSuccessReturnDto("已取消订单！");
                    } else {
                        logger.info("订单号[{}]，无法处理的流程");
                        return ReturnDto.buildFailedReturnDto(String.format("订单号[{}]，无法处理的流程", tradesId));
                    }
                } else {
                    logger.info("未查到与订单号：[{}]相关信息", tradesId);
                    return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", tradesId));
                }
            } catch (Exception e) {
                logger.error("取消订单失败，订单号：[{}]", tradesId);
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }
    }

    /**
     * 门店自提备货
     *
     * @param tradesId
     * @param selfTakenCodeStart   提货开始时间
     * @param selfTakenCodeExpires 提货码截止时间
     * @return
     */
    @RequestMapping(value = "/toStoreStockup", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto toStoreStockup(Integer siteId, Integer storeId, Integer clerkId, Long tradesId, @RequestParam(required = false) Timestamp selfTakenCodeStart, @RequestParam(required = false) Timestamp selfTakenCodeExpires) {
        if (siteId == null || storeId == null || clerkId == null || tradesId == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {
            try {
                Trades trades = tradesService.getTradesByTradesId(tradesId);
                if (trades != null) {
                    int oldTradesStatus = trades.getTradesStatus();
                    if (oldTradesStatus == CommonConstant.WAIT_SELLER_SHIPPED && trades.getStockupStatus() == CommonConstant.STOCKUP_WAIT_READY) {
//                        String barcode = tradesService.generateBarcode(trades.getSiteId());
                        trades.setTradesStatus(CommonConstant.WAIT_QU_HUO);
//                        trades.setSelfTakenCode(barcode);
                        trades.setStockupStatus(CommonConstant.STOCKUP_REDAY);
                        if (selfTakenCodeStart != null) {
                            trades.setSelfTakenCodeStart(selfTakenCodeStart);
                        }
                        if (selfTakenCodeExpires != null) {
                            trades.setSelfTakenCodeExpires(selfTakenCodeExpires);
                        }
                        tradesService.updateWaitZiTi(trades, oldTradesStatus, CommonConstant.SOURCE_BUSINESS_WAIT_QU);
                        stockupService.commitStockup(tradesId, siteId, storeId, clerkId);

                        /**
                         * 发送短信通知用户可取货了
                         */
                        trades = tradesService.getTradesByTradesId(tradesId);  //重新查询

                        //获取门店联系电话
                        String storePhone = "";
                        String storeName = "";
                        String storeAddress = "";
                        String shortMessageSign = "";
                        Store store = null;
                        if (trades.getAssignedStores() != null) {
                            //storePhone = tradesService.getStorePhone(trades.getSiteId(), trades.getAssignedStores());
                            store = tradesService.selectStoreInfo(trades.getSiteId(), trades.getAssignedStores());
                            if (store != null) {
                                storePhone = store.getTel();
                                storeName = store.getName();
                                storeAddress = store.getAddress();
                            }
                        }
                        if (StringUtil.isEmpty(storePhone)) {
                            storePhone = trades.getSellerMobile();
                        }

                        YbMerchant ybMerchant = tradesService.selectMerchantfo(String.valueOf(trades.getSiteId()));
                        if (shortMessageSign != null) {
                            shortMessageSign = ybMerchant.getShort_message_sign();
                        }

                        String re = "-1";
                        Object o = memberService.selectById(trades.getSiteId(), trades.getBuyerId()).getMobile();
                        if (!StringUtil.isEmpty(o)) {
                            //re = commonService.getGoodSMS(trades.getSiteId(), o.toString(), trades.getSelfTakenCode(), TimeUtil.getTimes(trades.getSelfTakenCodeExpires()), storePhone, shortMessageSign, storeAddress);
                            String urlOld = tradesService.getOrderDUrlNewZT(trades.getSiteId(), tradesId);
                            String url=tradesService.getOldUrl(trades.getSiteId(),urlOld);
                            re = commonService.SendMessage(commonService.transformParam(trades.getSiteId(),o.toString(),null ,SysType.LADING_ADRESS,
                                SmsEnum.LADING_CODE_NEW,shortMessageSign, storeName, storeAddress,storePhone , trades.getSelfTakenCode(), url));
                        }
                        if (!re.equals("0")) {
                            logger.info("订单号[{}]，发送短信失败");
                            return ReturnDto.buildFailedReturnDto(String.format("订单号[{}]，发送短信失败", tradesId));
                        }

                        logger.info("订单号：[{}],您的订单已成功备货，请尽快到门店自提，如有问题可电话联系我们。", tradesId);
                        return ReturnDto.buildSuccessReturnDto(trades.getSelfTakenCode());
                    } else {
                        logger.info("订单号[{}]，无法处理的流程");
                        return ReturnDto.buildFailedReturnDto(String.format("订单号[{}]，无法处理的流程", tradesId));
                    }

                } else {
                    logger.info("未查到与订单号：[{}]相关信息", tradesId);
                    return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", tradesId));
                }
            } catch (Exception e) {
                logger.error("门店自提，备货失败，订单号：[{}]", tradesId);
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }
    }

    /**
     * 送货上门 确认发货
     *
     * @param tradesId                订单号
     * @param type                    配送类型
     * @param store_shipping_clerk_id 送货员ID
     * @param post_name               快递名称
     * @param post_number             快递单号
     * @return
     */
    @RequestMapping(value = "/toDoorShipping", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto toDoorShipping(HttpServletRequest request, @RequestParam(required = false, defaultValue = "0") int clerkId, Long tradesId, @RequestParam(required = false, defaultValue = "1") int type, @RequestParam(required = false, defaultValue = "0") int store_shipping_clerk_id, @RequestParam(required = false) String post_name, @RequestParam(required = false) String post_number) {
        if (tradesId == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {
            try {
                Trades trades = tradesService.getTradesByTradesId(tradesId);
                if (trades != null) {
                    int oldTradesStatus = trades.getTradesStatus();
                    if (oldTradesStatus == CommonConstant.WAIT_SELLER_SHIPPED && trades.getStockupStatus() == CommonConstant.STOCKUP_REDAY && trades.getShippingStatus() == CommonConstant.SHIPPED_WAIT_DELIVERY) {
                        trades.setTradesStatus(CommonConstant.HAVE_SHIPPED);
                        trades.setShippingStatus(CommonConstant.SHIPPED);
                        if (type == 1) {  //蜂鸟配送
                            /*if (!tradesService.notifyExpress(request, trades.getAssignedStores(), tradesId)) {
                                logger.info("通知蜂鸟失败{}", tradesId);
                                return ReturnDto.buildFailedReturnDto(String.format("通知蜂鸟失败", tradesId));
                            } else {
                                logger.info("通知蜂鸟成功，发货成功！{}", tradesId);

                                //tradesService.updateConfirmStatus(trades, oldTradesStatus, CommonConstant.SOURCE_BUSINESS_SHIPPED);

                            }*/
                            Map<String, Object> map = tradesService.notifyDelivery(trades.getAssignedStores(), tradesId);
                            if ("true".equals(map.get("addMsg"))) {
                                return ReturnDto.buildSuccessReturnDto("您的订单已经成功通知蜂鸟，请耐心等待！");
                            } else {
                                return ReturnDto.buildFailedReturnDto(String.format("通知物流配送失败，请安排店员送货，原因：" + map.get("error") + "", tradesId));
                            }

                        } else if (type == 2) {  //店员配送
                            tradesService.clerkToSend(trades, oldTradesStatus, store_shipping_clerk_id);
                            tradesService.sendorderSendNotice(trades, null, store_shipping_clerk_id + "");//订单发货通知
                        } else {  //物流配送
                            tradesService.logisticsToSend(trades, oldTradesStatus, post_name, post_number);
                            tradesService.sendorderSendNotice(trades, post_name, post_number);//订单发货通知
                        }


                        stockupService.commitShipping(tradesId, clerkId);//备货状态

                        logger.info("订单号：[{}],您的订单已经成功发货，请准备收货。", tradesId);
                        return ReturnDto.buildSuccessReturnDto("您的订单已经成功发货，请准备收货。");
                    } else {
                        logger.info("订单号[{}]，无法处理的流程");
                        return ReturnDto.buildFailedReturnDto(String.format("订单号[%s]，无法处理的流程", tradesId));
                    }
                } else {
                    logger.info("未查到与订单号：[{}]相关信息", tradesId);
                    return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", tradesId));
                }
            } catch (Exception e) {
                logger.error("送货上门，确认发货失败，订单号：[{}]", tradesId);
                e.printStackTrace();
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }
    }

    /**
     * 送货上门 确认发货(总部后台操作)
     *
     * @param tradesId    订单号
     * @param post_name   快递名称
     * @param post_number 快递单号
     * @return
     */
    @RequestMapping(value = "/merchantShipping", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto merchantShipping(Long tradesId, @RequestParam(required = false) String post_name, @RequestParam(required = false) String post_number) {
        if (tradesId == null) {
            logger.info("参数错误，订单号[{}]", tradesId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，订单号[%s]", tradesId));
        } else {
            try {
                Trades trades = tradesService.getTradesByTradesId(tradesId);
                if (trades != null) {
                    int oldTradesStatus = trades.getTradesStatus();
                    trades.setTradesStatus(CommonConstant.HAVE_SHIPPED);
                    trades.setShippingStatus(CommonConstant.SHIPPED);
                    //设置为0时，为总部发的货
                    trades.setAssignedStores(0);
                    Integer count = tradesService.updateAssignStoreAndPostStyle(trades);
                    if (null != count && count.equals(1)) {
                        //物流配送
                        tradesService.logisticsToSend(trades, oldTradesStatus, post_name, post_number);
                        //订单发货通知
                        tradesService.sendorderSendNotice(trades, post_name, post_number);
                        logger.info("订单号：[{}],您的订单已经成功发货，请准备收货。", tradesId);
                        return ReturnDto.buildSuccessReturnDto("您的订单已经成功发货，请准备收货。");
                    }
                    return ReturnDto.buildFailedReturnDto(String.format("订单[{}]:改为总部发货失败", tradesId));
                } else {
                    logger.info("未查到与订单号：[{}]相关信息", tradesId);
                    return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关信息", tradesId));
                }
            } catch (Exception e) {
                logger.error("送货上门，发货失败，订单号：[{}]", tradesId);
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }
    }


    /**
     * 更新订单的配送方式和服务门店
     *
     * @param trades
     * @param errors
     * @return
     */
    @RequestMapping("/assignStore")
    @ResponseBody
    public ReturnDto assignStore(@Valid Trades trades, String userName, Errors errors) {
        if (errors.hasErrors()) return ReturnDto.buildFailedReturnDto("request has errors:" + errors.getAllErrors());

        if (trades.getTradesId() < 1) {
            return ReturnDto.buildSystemErrorReturnDto();
        }

        if (null == trades.getAssignedStores()) {
            return ReturnDto.buildSystemErrorReturnDto();
        }

        if (null == trades.getPostStyle()) {
            return ReturnDto.buildSystemErrorReturnDto();
        }

        //日志（需要保存更新前的数据）
        Trades tradesBefore = tradesService.getTradesByTradesId(trades.getTradesId());
        Integer beforeStore = tradesBefore.getAssignedStores();
        Integer beforePostStyle = tradesBefore.getPostStyle();
        Integer assignedStores = trades.getAssignedStores();
        Integer postStyle = trades.getPostStyle();
        Integer count = tradesService.updateAssignStoreAndPostStyleWithRefund(trades);

        if (null != count && count.equals(1)) {
            try {

                //日志
                tradesService.insertTradesAssignLog(tradesBefore.getSiteId(), trades.getTradesId(), userName, beforeStore, beforePostStyle, assignedStores, postStyle);

//                orderPayService.storeOrderRemind(trades.getTradesId());//已付款订单且指定门店 进行订单提醒，微信端下单

                Trades tradesOrigin = tradesService.getTradesByTradesId(trades.getTradesId());
                tradesOrigin.setPostStyle(trades.getPostStyle());
                tradesOrigin.setAssignedStores(trades.getAssignedStores());
                applicationContext.publishEvent(new PaySuccessEvent(this, tradesOrigin));//订单提醒
            } catch (Exception e) {
                e.printStackTrace();
            }
            erpToolsService.erpOrdersService(trades.getSiteId(), trades.getTradesId());
            return ReturnDto.buildSuccessReturnDto("");
        }

        return ReturnDto.buildSystemErrorReturnDto();
    }

    /**
     * 更新商家备注
     *
     * @param trades
     * @param errors
     * @return
     */
    @RequestMapping("/merchantComment")
    @ResponseBody
    public ReturnDto merchantComment(@Valid Trades trades, Errors errors) {
        if (errors.hasErrors()) return ReturnDto.buildFailedReturnDto("request has errors:" + errors.getAllErrors());

        if (trades.getTradesId() < 1) {
            return ReturnDto.buildSystemErrorReturnDto();
        }

        if (null == trades.getSellerFlag()) {
            return ReturnDto.buildSystemErrorReturnDto();
        }


        Integer count = tradesService.updateSellerFlagAndMemo(trades);

        if (null != count && count.equals(1)) {
            return ReturnDto.buildSuccessReturnDto("");
        }

        return ReturnDto.buildSystemErrorReturnDto();
    }

    /**
     * 订单统计接口
     *
     * @param siteId
     * @param buyerId
     * @return
     */
    @RequestMapping(value = "/tradesStatistical", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto tradesStatistical(Integer siteId, Integer buyerId) {
        if (siteId == null || buyerId == null) {
            logger.info("参数错误，会员[{}]", buyerId);
            return ReturnDto.buildFailedReturnDto(String.format("参数错误，会员[%s]", buyerId));
        } else {
            try {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("siteId", siteId);
                map.put("buyerId", buyerId);
                Map<String, Object> result = tradesService.tradesCount(map);
                logger.error("查询订单统计成功,会员ID[{}]", buyerId);
                return ReturnDto.buildSuccessReturnDto(result);
            } catch (Exception e) {
                logger.error("查询订单统计失败,会员ID[{}] {}", buyerId, e);
                return ReturnDto.buildSystemErrorReturnDto();
            }
        }

    }

    /***
     * 零元订单支付
     * @param tradesId
     * @param cashierId
     * @return
     */
    @RequestMapping("/noMoneyPay")
    @ResponseBody
    public ReturnDto noMoneyPay(Long tradesId, Integer cashierId) {
        if (tradesId == null) {
            logger.info("参数错误，订单号不能为空！");
            return ReturnDto.buildFailedReturnDto("参数错误，订单号不能为空！");
        }
        Trades trades = tradesService.getTradesByTradesId(tradesId);
        if (trades == null) {
            logger.info("参数错误，订单号[{}]订单不存在", tradesId);
            return ReturnDto.buildFailedReturnDto("参数错误，订单不存在！");
        }
        if (trades.getRealPay() != null && trades.getRealPay() > 0) {
            logger.info("订单号[{}]实付金额大于0", tradesId);
            return ReturnDto.buildFailedReturnDto("订单实付金额大于0");
        }
        if (trades.getTradesStatus() != CommonConstant.WAIT_PAYMENT_BUYERS || trades.getIsPayment() != CommonConstant.IS_PAYMENT_ZERO) {
            logger.info("订单号[{}]订单不是待付款状态", tradesId);
            return ReturnDto.buildFailedReturnDto("订单状态错误，无法罚款");
        }
        trades.setCashierId(cashierId);
        try {
            if (payService.noMoneyPay(trades)) {
                JSONObject jsonObject = integralService.integralAddForBuy(new HashMap() {{
                    put("siteId", trades.getSiteId());
                    put("buyerId", trades.getBuyerId());
                    put("orderAmount", trades.getRealPay());
                }});
                tradesService.saveIntegral(trades, jsonObject);
                couponSendService.sendCouponByOrder(String.valueOf(trades.getTradesId()));
                return ReturnDto.buildSuccessReturnDto("付款成功");
            }
            return ReturnDto.buildFailedReturnDto("付款失败");
        } catch (BusinessLogicException e) {
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    /**
     * 通知收银员收款
     *
     * @param tradesId
     * @return
     */
    @RequestMapping("/notifyCashier")
    @ResponseBody
    public ReturnDto notifyCashier(long tradesId) {
        int result = tradesService.updateTradePayStyle(PayConstant.PAY_STYLE_CASH, tradesId);
        if (result > 0)
            return ReturnDto.buildSuccessReturnDto("通知成功");
        return ReturnDto.buildFailedReturnDto("通知失败");
    }

    /**
     * 是否是第一笔分销订单
     *
     * @param tradesId
     * @return
     */
    @RequestMapping(value = "/isFirstDistributorOrder", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto isFirstDistributorOrder(String tradesId) {
        Map<String, Object> map;
        try {
            map = tradesService.isFirstDistributorOrder(tradesId);
        } catch (Exception e) {
            logger.error("根据tradeid查询是否是第一笔订单失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("根据tradeid查询是否是第一笔订单失败");
        }

        return ReturnDto.buildSuccessReturnDto(map);
    }

    @PostMapping("getTradesListfromERP")
    @ResponseBody
    public Map<String, Object> getTradesListfromERP(HttpServletRequest request) {
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        Map<String, Object> stringObjectMap = new HashMap<>();
        String sTime = null;
        String eTime = null;
        Integer site_id = null;
        Integer status = null;
        Integer pageNum = Integer.parseInt(objectMap.get("pageNum").toString());
        Integer pageSize = Integer.parseInt(objectMap.get("pageSize").toString());
        if (!StringUtil.isEmpty(objectMap.get("sTime"))) {
            sTime = objectMap.get("sTime").toString();
            sTime = sTime + " 00:00:00";
        }
        if (!StringUtil.isEmpty(objectMap.get("eTime"))) {
            eTime = objectMap.get("eTime").toString();
            eTime = eTime + " 23:59:59";
        }
        if (!StringUtil.isEmpty(objectMap.get("type"))) {
            status = Integer.parseInt(objectMap.get("type").toString());
        }
        if (!StringUtil.isEmpty(objectMap.get("site_id"))) {
            site_id = Integer.parseInt(objectMap.get("site_id").toString());
        } else {
            logger.info("站点id没有获取到");
            stringObjectMap.put("code", -1);
            stringObjectMap.put("msg", "订单查询异常");
            return stringObjectMap;
        }
        return tradesService.getTradesListFormERP(site_id, sTime, eTime, status, pageNum, pageSize);
    }

    @PostMapping("getTradesListfromERP2")
    @ResponseBody
    public Map<String, Object> getTradesListfromERP2(HttpServletRequest request) {
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        Map<String, Object> stringObjectMap = new HashMap<>();
        String sTime = null;
        String eTime = null;
        Integer site_id = null;
        Integer status = null;
        Integer pageNum = Integer.parseInt(objectMap.get("pageNum").toString());
        Integer pageSize = Integer.parseInt(objectMap.get("pageSize").toString());
        if (!StringUtil.isEmpty(objectMap.get("sTime"))) {
            sTime = objectMap.get("sTime").toString();
            sTime = sTime + " 00:00:00";
        }
        if (!StringUtil.isEmpty(objectMap.get("eTime"))) {
            eTime = objectMap.get("eTime").toString();
            eTime = eTime + " 23:59:59";
        }
        if (!StringUtil.isEmpty(objectMap.get("type"))) {
            status = Integer.parseInt(objectMap.get("type").toString());
        }
        if (!StringUtil.isEmpty(objectMap.get("site_id"))) {
            site_id = Integer.parseInt(objectMap.get("site_id").toString());
        } else {
            logger.info("站点id没有获取到");
            stringObjectMap.put("code", -1);
            stringObjectMap.put("msg", "订单查询异常");
            return stringObjectMap;
        }
        return tradesService.getTradesListFormERP2(site_id, sTime, eTime, status, pageNum, pageSize);
    }
    /**
     * 订单支付成功或者退款成功调用接口
     * <p>
     * 目前只支持九州，其他自动屏蔽
     *
     * @param siteId
     * @param tradesId
     */
    @RequestMapping("pushOrders")
    public void pushOrders(Integer siteId, Long tradesId) {
        tradesService.pushTradesToJZ(siteId, tradesId);
    }

    /**
     * 检查购买商品
     *
     * @param requestMap
     * @return
     */
    @RequestMapping("/checkGoods")
    @ResponseBody
    public ReturnDto checkGoods(@RequestBody Map<String, String> requestMap) {
        try {
            String buyerGoodsStr = requestMap.get("buyerGoods");
            if (Objects.isNull(buyerGoodsStr)) {
                throw new BusinessLogicException("购买商品不能为空");
            }

            int siteId = NumberUtils.toInt(requestMap.get("siteId"));
            if (siteId == 0) {
                throw new BusinessLogicException("商家编号不能为空");
            }
            List<Map> buyerGoods = JacksonUtils.getInstance().readValue(buyerGoodsStr, new TypeReference<List<Map>>() {
            });
            tradesService.checkGoods(buyerGoods, siteId);
        } catch (BusinessLogicException e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }

        return ReturnDto.buildSuccessReturnDto(null);
    }

    @RequestMapping(name = "礼品商品接口", value = "/giftList")
    @ResponseBody
    public ReturnDto giftList(@RequestBody GoodsParam goodsParam, HttpServletRequest request) {
        PageInfo<?> pageInfo = null;
        if (null == goodsParam.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        pageInfo = goodsService.proActivityList(goodsParam);
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     * 查询修改订单日志
     */
    @RequestMapping("/selectTradesUpProceLog")
    @ResponseBody
    public ReturnDto selectTradesUpProceLog(HttpServletRequest request) {
        try {
            Map<String, Object> param = ParameterUtil.getParameterMap(request);
            return tradesService.selectTradesUpProceLog(param);
        } catch (Exception e) {
            logger.error("查询修改订单失败{}", e);
            return ReturnDto.buildSuccessReturnDto("查询修改订单失败");
        }

    }
    /**
     * App短信发送收款页面地址给顾客
     *
     * @return
     */
    @RequestMapping("/sendOrderAddressSMS")
    @ResponseBody
    public ReturnDto sendOrderAddressSMS(Long tradesId,String storeAdminId){
        String re = "-1";
        Trades trades = tradesService.getTradesByTradesId(tradesId);
        Object o = memberService.selectById(trades.getSiteId(), trades.getBuyerId()).getMobile();
        YbMerchant ybMerchant = tradesService.selectMerchantfo(String.valueOf(trades.getSiteId()));
        String shortMessageSign = "";
        if (shortMessageSign != null) {
            shortMessageSign = ybMerchant.getShort_message_sign();
        }
        //获取门店联系电话
        //String storePhone = "";
        String storeName = "总部";
        //String storeAddress = "";
        Store store = null;
        SStoreAdminext storeAdminext= clerkService.getStoreAdminExtById(trades.getSiteId()+"",storeAdminId);

        if (trades.getAssignedStores() != null&&storeAdminext!= null) {
            store = tradesService.selectStoreInfo(trades.getSiteId(), storeAdminext.getStore_id());
            if (store != null) {
                //storePhone = store.getTel();
                storeName = store.getName();
                //storeAddress = store.getAddress();
            }
        }
       /* if (StringUtil.isEmpty(storePhone)) {
            storePhone = trades.getSellerMobile();
        }*/
        if (!StringUtil.isEmpty(o)) {

            String urlOld = tradesService.getOrderDUrlNew(trades.getSiteId(), tradesId);
            String url=tradesService.getOldUrl(trades.getSiteId(),urlOld);
            double money=(double)trades.getRealPay()/100;
            re = commonService.SendMessage(commonService.transformParam(trades.getSiteId(), o.toString(),null,SysType.PAYMENT_ORDER,
                SmsEnum.SEND_ORDER_ADDRESS, shortMessageSign, storeName, storeAdminext.getName(),  money+"",  url));
            //commonService.getGoodSMS(trades.getSiteId(), o.toString(), trades.getSelfTakenCode(), TimeUtil.getTimes(trades.getSelfTakenCodeExpires()), storePhone, shortMessageSign, storeAddress);
        }
        return ReturnDto.buildSuccessReturnDto(re);
    }
    @RequestMapping("/getOrderAddressSMS")
    @ResponseBody
    public ReturnDto getOrderAddressSMS(Long tradesId,String storeAdminId){
        String re = "-1";
        Trades trades = tradesService.getTradesByTradesId(tradesId);
        Object o = memberService.selectById(trades.getSiteId(), trades.getBuyerId()).getMobile();
        YbMerchant ybMerchant = tradesService.selectMerchantfo(String.valueOf(trades.getSiteId()));
        String shortMessageSign = "";
        if (shortMessageSign != null) {
            shortMessageSign = ybMerchant.getShort_message_sign();
        }
        SStoreAdminext storeAdminext= clerkService.getStoreAdminExtById(trades.getSiteId()+"",storeAdminId);
        String storeName = "总部";
        if (trades.getAssignedStores() != null&&storeAdminext!= null) {
            Store store = tradesService.selectStoreInfo(trades.getSiteId(), storeAdminext.getStore_id());
            if (store != null) {
                storeName = store.getName();
            }
        }
        if (!StringUtil.isEmpty(o)) {
            String urlOld = tradesService.getOrderDUrlNew(trades.getSiteId(), tradesId);
            String url=tradesService.getOldUrl(trades.getSiteId(),urlOld);
            double money=(double)trades.getRealPay()/100;
            re = "【51健康】" + shortMessageSign + "" + storeName + "" + storeAdminext.getName() + "店员发来了一个待付款订单，需付金额：" + money + "元。点击链接完成付款:" + url + "";
        }
        return ReturnDto.buildSuccessReturnDto(re);
    }

    @RequestMapping("/newOrderToPayNotice")
    @ResponseBody
    public ReturnDto newOrderToPayNotice(int siteId, Long tradesId,String open_id,String tradesStr){
        tradesService.newOrderToPayNotice(siteId,  tradesId, open_id, tradesStr);
        return ReturnDto.buildSuccessReturnDto("ok");
    }
    @RequestMapping("/newOrderToPayNoticeAli")
    @ResponseBody
    public ReturnDto newOrderToPayNoticeAli(int siteId, Long tradesId,String open_id,String tradesStr){
        tradesService.newOrderToPayNoticeAli(siteId,  tradesId, open_id, tradesStr);
        return ReturnDto.buildSuccessReturnDto("ok");
    }
    @RequestMapping("/insertOrderPayLog")
    @ResponseBody
    public ReturnDto insertOrderPayLog(int site_id, String order_number,String mobile,String order_mobile){
        Map<String, Object> params=new HashedMap();
        params.put("order_number",Long.parseLong(order_number));
        params.put("site_id",site_id);
        params.put("mobile",mobile);
        params.put("order_mobile",order_mobile);
        tradesService.insertOrderPayLog(params);
        return ReturnDto.buildSuccessReturnDto("ok");
    }
}
