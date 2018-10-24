package com.jk51.modules.pay.service;

import com.jk51.model.pay.PayInterfaceLog;
import com.jk51.modules.pay.constants.PayConstant;
import com.jk51.modules.pay.mapper.PayInterfaceLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-27
 * 修改记录:
 */
@Service
public class PayInterfaceLogService {
    @Autowired
    PayInterfaceLogMapper payInterfaceLogMapper;

    public void insert(PayInterfaceLog payInterfaceLog) {
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

    public PayInterfaceLog findByTradesId(String trades_id, String pay_style, String pay_interface, Integer siteId) {
        List<PayInterfaceLog> payInterfaceLogs = payInterfaceLogMapper.findByTradesId(trades_id,pay_style,pay_interface, siteId);
        if(payInterfaceLogs != null && !payInterfaceLogs.isEmpty())
            return payInterfaceLogs.get(0);
        return null;
    }

    /**
     * 支付宝预下单接口日志
     * @param tradesId
     * @param tradesFee
     * @param siteId
     * @param payResult
     * @param exeResult
     */
    public void insertAliPreCreate(Long tradesId, Integer tradesFee, Integer siteId, String payResult, Byte exeResult) {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setTradesId(tradesId);
        payInterfaceLog.setTradesFee(tradesFee);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_ALI);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_ALI_PO);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

    /**
     * 支付宝订单查询
     * @param tradesId
     * @param siteId
     * @param payResult
     * @param exeResult
     */
    public void insertAliQuery(Long tradesId, Integer siteId,String transactionId, String payResult, Byte exeResult) {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setTradesId(tradesId);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_ALI);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_ALI_QR);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setTransactionId(transactionId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

    /**
     * 支付宝支付接口
     * @param tradesId
     * @param tradesFee
     * @param siteId
     * @param payResult
     * @param exeResult
     */
    public void insertAliPay(Long tradesId, Integer tradesFee, Integer siteId,String transactionId,String payResult, Byte exeResult) {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setTradesId(tradesId);
        payInterfaceLog.setTradesFee(tradesFee);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_ALI);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_ALI_PAY);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setTransactionId(transactionId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

    /**
     * 支付宝退款接口
     * @param tradesId
     * @param refundNo
     * @param refundFee
     * @param siteId
     * @param refundId
     * @param payResult
     * @param exeResult
     */
    public void insertAliRefund(Long tradesId, Long refundNo, int refundFee, Integer siteId,String refundId,String payResult, Byte exeResult) {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setTradesId(tradesId);
        payInterfaceLog.setRefundNo(refundNo);
        payInterfaceLog.setRefundFee(refundFee);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_ALI);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_ALI_RF);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setRefundId(refundId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

    /**
     * 微信扫码预生成订单接口
     * @param tradesId
     * @param tradesFee
     * @param siteId
     * @param payResult
     * @param exeResult
     */
    public void insertWxCreateNativeOrder(Long tradesId, Integer tradesFee, Integer siteId, String payResult, Byte exeResult,String tradesIdTime) {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setTradesId(tradesId);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX_NATIVE);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_PO);
        payInterfaceLog.setTradesFee(tradesFee);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLog.setTradesIdTime(tradesIdTime);
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

    /**
     * 微信公众号支付接口
     * @param tradesId
     * @param tradesFee
     * @param siteId
     * @param payResult
     * @param exeResult
     */
    public void insertWxCreateJSAPIOrder(Long tradesId, Integer tradesFee, Integer siteId, String payResult, Byte exeResult) {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setTradesId(tradesId);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX_JSAPI);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_PO);
        payInterfaceLog.setTradesFee(tradesFee);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

    /**
     * 微信App支付接口
     * @param tradesId
     * @param tradesFee
     * @param siteId
     * @param payResult
     * @param exeResult
     */
    public void insertWxCreateAPPOrder(Long tradesId,Integer tradesFee,Integer siteId,String payResult,Byte exeResult){
        PayInterfaceLog payInterfaceLog=new PayInterfaceLog();
        payInterfaceLog.setTradesId(tradesId);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX_APP);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_PO);
        payInterfaceLog.setTradesFee(tradesFee);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLogMapper.insert(payInterfaceLog);

    }

    /**
     * 微信支付
     * @param tradesId
     * @param tradesFee
     * @param siteId
     * @param transactionId
     * @param payResult
     * @param exeResult
     */
    public void insertWxPay(Long tradesId, Integer tradesFee, Integer siteId,String transactionId, String payResult, Byte exeResult,String tradesIdTime) {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setTradesId(tradesId);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX_MICROPAY);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_PAY);
        payInterfaceLog.setTradesFee(tradesFee);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setTransactionId(transactionId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLog.setTradesIdTime(tradesIdTime);
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

    /**
     * 微信订单查询
     * @param tradesId
     * @param siteId
     * @param transactionId
     * @param payResult
     * @param exeResult
     */
    public void insertWxQueryOrder(Long tradesId, Integer siteId,String transactionId, String payResult, Byte exeResult) {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setTradesId(tradesId);
        payInterfaceLog.setTransactionId(transactionId);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_QR);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setTransactionId(transactionId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

    /**
     * 微信退款接口
     * @param tradesId
     * @param tradesFee
     * @param outRefundNo
     * @param refundFee
     * @param siteId
     * @param transactionId
     * @param payResult
     * @param exeResult
     */
    public void insertWxRefund(Long tradesId, Integer tradesFee, Long outRefundNo, Integer refundFee, Integer siteId,String transactionId, String refundId, String payResult, Byte exeResult) {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setTradesId(tradesId);
        payInterfaceLog.setTransactionId(transactionId);
        payInterfaceLog.setRefundNo(outRefundNo);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_RF);
        payInterfaceLog.setTradesFee(tradesFee);
        payInterfaceLog.setRefundFee(refundFee);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setRefundId(refundId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

    /**
     * 微信退款查询
     * @param outRefundNo
     * @param siteId
     * @param payResult
     * @param exeResult
     */
    public void insertWxRefundQuery( Long outRefundNo, Integer siteId, String payResult, Byte exeResult) {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setRefundNo(outRefundNo);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_RFOQR);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

    /**
     * 微信订单撤销
     * @param tradesId
     * @param transactionId
     * @param siteId
     * @param payResult
     * @param exeResult
     */
    public void insertWxReverse(Long tradesId, String transactionId, Integer siteId, String payResult, Byte exeResult) {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setTradesId(tradesId);
        payInterfaceLog.setTransactionId(transactionId);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_RV);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

    public void insertWxClose(Long tradesId, Integer siteId, String payResult, Byte exeResult) {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setTradesId(tradesId);
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_CLS);
        payInterfaceLog.setSiteId(siteId);
        payInterfaceLog.setPayResult(payResult);
        payInterfaceLog.setExeResult(exeResult);
        payInterfaceLogMapper.insert(payInterfaceLog);
    }

}
