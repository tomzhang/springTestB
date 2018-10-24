package com.jk51.modules.trades.service;

import com.jk51.commons.CommonConstant;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.Refund;
import com.jk51.model.order.Trades;
import com.jk51.modules.pay.constants.PayConstant;
import com.jk51.modules.pay.service.PayService;
import com.jk51.modules.trades.mapper.RefundMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.trades.goodsService.
 * author   :zw
 * date     :2017/2/20
 * Update   :
 */
@Service
public class RefundService {
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private TradesService tradesService;
    @Autowired
    private PayService payService;
    private static final Logger logger = LoggerFactory.getLogger(RefundService.class);
    /**
     * 获取退款记录表中的为退款记录
     *
     * @return
     */
    public List<Refund> getRefundListByAccountCheckingStatus() {
        return refundMapper.getRefundListByAccountCheckingStatus(null);
    }

    public int updateAccountStatus(Integer id) {
        return refundMapper.updateAccountStatus(id);
    }

    /**
     * 校验订单是否能退款
     * 判断每个商家的实付总金额(未出账且付款的所有订单)与每一个退款订单金额对比,若总实付金额 < 退款金额且结算方式为付款方式则不能退款,否则退款正常
     * @param trades
     * @param realRefundMoney
     * @return
     */
    public ReturnDto validateOrderRefund(Trades trades,Integer realRefundMoney) {
        if(Arrays.asList("wx","ali").contains(trades.getPayStyle())) {
            Trades trades1 = refundMapper.getTradesRealPayTotal(trades.getSiteId());
            if (new BigDecimal(trades1.getRealPay()).intValue() < realRefundMoney.intValue() && trades1.getFinance_type() == 1) {
                return ReturnDto.buildFailedReturnDto("您的账户余额不足，暂无法退款。");
            } else {
                return ReturnDto.buildSuccessReturnDto();
            }
        } else {
            return ReturnDto.buildSuccessReturnDto();
        }
    }

    /**
     * 系统自动退款
     * @param tradeId
     * @param is_integral
     * @param is_coupon
     * @param realRefundMoney 默认全额退款
     * @return
     */
    public ReturnDto zdApplyRefund(String tradeId,int is_integral, int is_coupon,Integer realRefundMoney,Integer operatorType) {
        try {
            Refund refund = new Refund();
            if (!StringUtil.isEmpty(tradeId)) {
                refund.setTradeId(tradeId);
                Trades trades = tradesService.getTradesByTradesId(Long.parseLong(refund.getTradeId()));
                refund.setSiteId(trades.getSiteId());
                if(realRefundMoney==0){
                    refund.setRealRefundMoney(trades.getRealPay());
                }else {
                    refund.setRealRefundMoney(realRefundMoney);
                }
                if (trades == null) {
                    logger.info("未查到与订单号：[{}]相关退款信息", refund.getTradeId());
                    return ReturnDto.buildFailedReturnDto(String.format("未查到与订单号：[%s]相关退款信息", refund.getTradeId()));
                } else {
                    //校验订单是否能退款
                    ReturnDto  returnDto=validateOrderRefund(trades,trades.getRealPay());
                    if(!"OK".equals(returnDto.getStatus())){
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
                                Map<String,String> map = payService.aliRefund(trades.getSiteId(), Long.parseLong(refund.getTradeId()), System.currentTimeMillis(), refund.getRealRefundMoney(),trades.getIsServiceOrder());
                                refundNo=map.get("refund_id");
                                flag = true;
                                msg=map.get("err_code_des");
                            } else if (trades.getPayStyle().equals(PayConstant.PAY_STYLE_CASH) || trades.getPayStyle().equals(PayConstant.PAY_STYLE_HEALTH_INSURANCE)) {
                                refundNo = payService.cashRefund(Long.parseLong(refund.getTradeId()));
                                flag = true;
                            }else if(trades.getPayStyle().equals("integral")){
                                refundNo="0";
                                flag=true;
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
                            refund.setOperatorType(operatorType);

                            int oldTradesStatus = trades.getTradesStatus();
                            trades.setSettlementStatus(CommonConstant.SETTLEMENT_STATUS_MAY);  //更新为可结算状态
                            trades.setTradesStatus(CommonConstant.TRADES_REFUND_SUCCESS);   //更新为已退款
                            trades.setIsRefund(CommonConstant.IS_REFUND_FOUR);
                            tradesService.merchantRefund(trades, oldTradesStatus, CommonConstant.SOURCE_BUSINESS_MERCHANT_REFUND, refund, is_coupon, is_integral);

                           /* Refund refundNew=refundMapper.getByTradesId(refund.getSiteId(),refund.getTradeId());
                            if(StringUtil.isEmpty(refundNew)){
                                refundNew=refund;
                                refundNew.setOperatorType(300);
                                refundMapper.addRefund(refundNew);
                            }*/
                            logger.info("订单号：[{}]，退款成功", refund);
                            tradesService.pushTradesToJZ(trades.getSiteId(), trades.getTradesId());
                            //退款成功提醒
                            tradesService.sendorderRefundSuccess(trades,refund.getRealRefundMoney());
                            return ReturnDto.buildSuccessReturnDto("退款成功。");
                        }
                    }
                }
            }
            logger.info("退款失败，请输入正确的参数，"+ tradeId);
            return ReturnDto.buildFailedReturnDto("退款失败，请输入正确的参数");

        } catch (Exception e) {
            logger.error("商家发起退款失败{} ，{}", tradeId, e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }
}
