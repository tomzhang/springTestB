package com.jk51.modules.pay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.*;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.order.Trades;
import com.jk51.model.pay.PayInterfaceLog;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.pay.constants.PayConstant;
import com.jk51.modules.pay.exception.PayException;
import com.jk51.modules.pay.service.PayInterfaceLogService;
import com.jk51.modules.pay.service.PayLogsService;
import com.jk51.modules.pay.service.PayService;
import com.jk51.modules.pay.service.PayfwService;
import com.jk51.modules.pay.service.ali.AliConfig;
import com.jk51.modules.pay.service.ali.AliConfigfw;
import com.jk51.modules.pay.service.ali.AliPayApi;
import com.jk51.modules.pay.service.ali.AliPayApifw;
import com.jk51.modules.pay.service.ali.request.AliRequestParam;
import com.jk51.modules.trades.service.TradesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Controller
@RequestMapping("/pay/ali")
public class AliPayController {

    private static final Logger log = LoggerFactory.getLogger(AliPayController.class);
    @Autowired
    AliConfig aliConfig;
    @Autowired
    AliConfigfw aliConfigfw;
    @Autowired
    AliPayApi aliPayApi;
    @Autowired
    AliPayApifw aliPayApifw;
    @Autowired
    PayfwService payfwService;
    @Autowired
    PayService payService;
    @Autowired
    TradesService tradesService;
    @Autowired
    PayInterfaceLogService payInterfaceLogService;
    @Autowired
    PayLogsService payLogsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BalanceService balanceService;
    @RequestMapping(value = "/callback")
    @ResponseBody
    public String aliPayCallback(HttpServletRequest request) {
        Map map = ParameterUtil.getParameterMap(request);
        log.info("支付宝回调参数\n{}", JacksonUtils.mapToJson(map));
        long startMillis= System.currentTimeMillis();
        String signType = (String)map.get("sign_type");
        try {
            if(AlipaySignature.rsaCheckV1(map, aliConfig.getAli_public_key(), "utf-8",signType)||AlipaySignature.rsaCheckV1(map, aliConfigfw.getAli_public_key(), "utf-8",signType)) {
                String trade_status = (String) map.get("trade_status");
                if(!"TRADE_SUCCESS".equals(trade_status)) {
                    return "success";
                }
                String out_trade_no = (String) map.get("out_trade_no");
                String seller_id = (String) map.get("seller_id");
                String app_id = (String) map.get("app_id");
                String body = (String) map.get("body");
                Trades trades = tradesService.getTradesByTradesId(Long.parseLong(out_trade_no));
                String receipt_amount = (String)map.get("receipt_amount");
                Integer totalFee = new Float(Float.parseFloat(receipt_amount) * 100).intValue();
                if(trades != null && totalFee.equals(trades.getRealPay())
                        && aliConfig.getApp_id().equals(app_id)
                        //&& aliConfig.getSeller_id().equals(seller_email)
                        ) {
                    try {
                        //回调成功后取消支付宝和微信生成的订单
                        AliRequestParam param = new AliRequestParam();
                        param.setOut_trade_no(trades.getTradesId().toString());
                        if(trades.getIsServiceOrder()==0){
                            aliPayApi.close(param);
                            payService.wxClose(trades.getSiteId(), Objects.toString(map.get("out_trade_no"), "")+ DateUtils.formatDate("_yyddHHmmss"));
                        }else {
                            aliPayApifw.close(param);
                            payfwService.wxClose(trades.getSiteId(), Objects.toString(map.get("out_trade_no"), ""));
                        }

                        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
                        payInterfaceLog.setTradesId(Long.parseLong(out_trade_no));
                        payInterfaceLog.setTransactionId((String) map.get("trade_no"));
                        payInterfaceLog.setPayResult(JacksonUtils.mapToJson(map));
                        payInterfaceLog.setExeResult(PayConstant.PAY_INTERFACE_EXE_SUCCESS);
                        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_ALI);
                        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_ALI_CB);
                        payInterfaceLog.setSiteId(trades.getSiteId());
                        payInterfaceLog.setTradesFee(trades.getRealPay());
                        payInterfaceLogService.insert(payInterfaceLog);
                        payLogsService.updateByTrades(trades.getTradesId(),(String) map.get("trade_no"), PayConstant.PAY_STYLE_ALI_QRCODE, PayConstant.PAY_STATUS_SUCCESS, new Date(), JacksonUtils.mapToJson(map), aliConfig.getSeller_id(), (String) map.get("buyer_logon_id"));
                        trades.setPayNumber((String) map.get("trade_no"));
                        trades.setPayStyle(PayConstant.PAY_STYLE_ALI);
                        trades.setCashierId(trades.getStoreUserId());
                        if(!StringUtil.isEmpty(body)){
                            try {
                                trades.setTradeTypePayLine(Integer.parseInt(body));
                            }catch (Exception e) {
                                trades.setTradeTypePayLine(210);
                                log.info("支付宝回调body Exception\n{}", JacksonUtils.mapToJson(map));
                            }
                        }else {
                            trades.setTradeTypePayLine(210);
                        }
                        if(!tradesService.paySuccessCallback(trades)) {
                            log.info("订单号为：{},无法执行付款操作", trades.getTradesId());
                        }
                    } catch (BusinessLogicException e) {
                        log.info(e.getMessage(), e);
                        return "fail";
                    } catch (PayException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    return "fail";
                }
            } else {
                log.info("支付宝回调验签失败");
                return "fail";
            }
        } catch (AlipayApiException e) {
            log.info("AlipayApiException:{}", e);
            return "error";
        }
        log.info("支付宝支付支付回调耗时: {}", System.currentTimeMillis() - startMillis);
        return "success";
    }
    @RequestMapping(value = "/callbackfw")
    @ResponseBody
    public String aliPayCallbackfw(HttpServletRequest request) {
        Map map = ParameterUtil.getParameterMap(request);
        log.info("支付宝回调参数\n{}", JacksonUtils.mapToJson(map));
        long startMillis= System.currentTimeMillis();
        String signType = (String)map.get("sign_type");
        String out_trade_no = (String) map.get("out_trade_no");
        try {
            aliConfig=aliPayApifw.toConfig(out_trade_no.substring(0,6));
            if(AlipaySignature.rsaCheckV1(map, aliConfig.getAli_public_key(), "utf-8",signType)) {
                String trade_status = (String) map.get("trade_status");
                if(!"TRADE_SUCCESS".equals(trade_status)) {
                    return "success";
                }

                String seller_id = (String) map.get("seller_id");
                String app_id = (String) map.get("app_id");
                String body = (String) map.get("body");
                Trades trades = tradesService.getTradesByTradesId(Long.parseLong(out_trade_no));
                String receipt_amount = (String)map.get("receipt_amount");
                Integer totalFee = new Float(Float.parseFloat(receipt_amount) * 100).intValue();
                if(trades != null && totalFee.equals(trades.getRealPay())
                        && aliConfig.getApp_id().equals(app_id)
                    //&& aliConfig.getSeller_id().equals(seller_email)
                        ) {
                    try {
                        //回调成功后取消支付宝和微信生成的订单
                        AliRequestParam param = new AliRequestParam();
                        param.setOut_trade_no(trades.getTradesId().toString());
                        if(trades.getIsServiceOrder()==0){
                            aliPayApi.close(param);
                            payService.wxClose(trades.getSiteId(), Objects.toString(map.get("out_trade_no"), "")+ DateUtils.formatDate("_yyddHHmmss"));
                        }else {
                            aliPayApifw.close(param);
                            payfwService.wxClose(trades.getSiteId(), Objects.toString(map.get("out_trade_no"), ""));
                        }

                        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
                        payInterfaceLog.setTradesId(Long.parseLong(out_trade_no));
                        payInterfaceLog.setTransactionId((String) map.get("trade_no"));
                        payInterfaceLog.setPayResult(JacksonUtils.mapToJson(map));
                        payInterfaceLog.setExeResult(PayConstant.PAY_INTERFACE_EXE_SUCCESS);
                        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_ALI);
                        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_ALI_CB);
                        payInterfaceLog.setSiteId(trades.getSiteId());
                        payInterfaceLog.setTradesFee(trades.getRealPay());
                        payInterfaceLogService.insert(payInterfaceLog);
                        payLogsService.updateByTrades(trades.getTradesId(),(String) map.get("trade_no"), PayConstant.PAY_STYLE_ALI_QRCODE, PayConstant.PAY_STATUS_SUCCESS, new Date(), JacksonUtils.mapToJson(map), aliConfig.getSeller_id(), (String) map.get("buyer_logon_id"));
                        trades.setPayNumber((String) map.get("trade_no"));
                        trades.setPayStyle(PayConstant.PAY_STYLE_ALI);
                        trades.setCashierId(trades.getStoreUserId());
                        if(!StringUtil.isEmpty(body)){
                            try {
                                trades.setTradeTypePayLine(Integer.parseInt(body));
                            }catch (Exception e) {
                                trades.setTradeTypePayLine(210);
                                log.info("支付宝回调body Exception\n{}", JacksonUtils.mapToJson(map));
                            }
                        }else {
                            trades.setTradeTypePayLine(210);
                        }
                        if(!tradesService.paySuccessCallback(trades)) {
                            log.info("订单号为：{},无法执行付款操作", trades.getTradesId());
                        }
                    } catch (BusinessLogicException e) {
                        log.info(e.getMessage(), e);
                        return "fail";
                    } catch (PayException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    return "fail";
                }
            } else {
                log.info("支付宝回调验签失败");
                return "fail";
            }
        } catch (AlipayApiException e) {
            log.info("AlipayApiException:{}", e);
            return "error";
        } catch (IllegalAccessException e) {
            log.info("IllegalAccessException:{}", e);
            return "error";
        }
        log.info("支付宝支付支付回调耗时: {}", System.currentTimeMillis() - startMillis);
        return "success";
    }
    @RequestMapping(value = "/balanceCallback")
    @ResponseBody
    public String balanceCallback(HttpServletRequest request) {
        Map map = ParameterUtil.getParameterMap(request);
        log.info("支付宝回调参数\n{}", JacksonUtils.mapToJson(map));
        long startMillis= System.currentTimeMillis();
        String signType = (String)map.get("sign_type");
        try {
            if(AlipaySignature.rsaCheckV1(map, aliConfig.getAli_public_key(), "utf-8",signType)||AlipaySignature.rsaCheckV1(map, aliConfigfw.getAli_public_key(), "utf-8",signType)) {
                String trade_status = (String) map.get("trade_status");
                if(!"TRADE_SUCCESS".equals(trade_status)) {
                    return "success";
                }
                String out_trade_no = (String) map.get("out_trade_no");
                String seller_id = (String) map.get("seller_id");
                String app_id = (String) map.get("app_id");
                String body = (String) map.get("body");
                String receipt_amount = (String)map.get("receipt_amount");
                Integer totalFee = new Float(Float.parseFloat(receipt_amount) * 100).intValue();
                String thirdSerialNum=map.get("trade_no").toString();
                Integer siteId=Integer.parseInt(out_trade_no.substring(0,6));
                PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
                payInterfaceLog.setTradesId(Long.parseLong(out_trade_no));
                payInterfaceLog.setTransactionId(thirdSerialNum);
                payInterfaceLog.setPayResult(JacksonUtils.mapToJson(map));
                payInterfaceLog.setExeResult(PayConstant.PAY_INTERFACE_EXE_SUCCESS);
                payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_ALI);
                payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_ALI_CB);
                payInterfaceLog.setSiteId(siteId);
                payInterfaceLog.setTradesFee(totalFee);
                payInterfaceLogService.insert(payInterfaceLog);
                if(balanceService.updBalanceCallback(siteId,out_trade_no,8, thirdSerialNum)!=1) {
                    log.error("充值流水号为：{},无法执行付款操作", out_trade_no);
                }
            } else {
                log.info("支付宝回调验签失败");
                return "fail";
            }
        } catch (AlipayApiException e) {
            log.info("AlipayApiException:{}", e);
            return "error";
        }
        log.info("支付宝支付支付回调耗时: {}", System.currentTimeMillis() - startMillis);
        return "success";
    }
    @RequestMapping("/createorder")
    @ResponseBody
    public ReturnDto createOrder(String site_id, String trades_id) {
        /*if(StringUtil.isEmpty(site_id))
            return ReturnDto.buildFailedReturnDto("site_id不能为空");*/
        if(StringUtil.isEmpty(trades_id)||"null".equals(trades_id))
            return ReturnDto.buildFailedReturnDto("trades_id不能为空");
        Trades trades = tradesService.getTradesByTradesId(Long.parseLong(trades_id));
        if(trades == null) {
            return ReturnDto.buildFailedReturnDto("订单不存在");
        }
        if(trades.getIsPayment() == 1) {
            return ReturnDto.buildFailedReturnDto("订单已经支付");
        }
        try {
            String tradesName=orderService.findTradesName(trades.getSiteId(),trades.getTradesId());
            String fw=balanceService.boolIsProvider(trades.getSiteId());
            String url ="";
            if("NO".equals(fw)||trades.getIsServiceOrder()==0){
                url=payService.aliPreCreate(trades.getSiteId(),trades.getTradesId(),trades.getRealPay(),tradesName);
            }else {
                url = payfwService.aliPreCreate(trades.getSiteId(),trades.getTradesId(),trades.getRealPay(),tradesName);
            }
            if(StringUtil.isEmpty(url))
                return ReturnDto.buildFailedReturnDto("生成订单失败");
            Map<String, Object> data = new HashMap<String,Object>();
            data.put("code_url",url);
            data.put("trades_id",trades.getTradesId().toString());
            data.put("order_price", new DecimalFormat("######0.00").format(trades.getRealPay()/100f));
            return ReturnDto.buildSuccessReturnDto(data);
            //return ReturnDto.buildSuccessReturnDto(url);
        } catch (Exception e) {
            log.info("支付宝支付异常-PayException:{}", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    @RequestMapping("/create")
    @ResponseBody
    public String create(String logonid) {
        AliRequestParam aliRequestParam = new AliRequestParam();
        String out_trade_no = StringUtil.getRandomStr(16);
        log.info("订单号：" + out_trade_no);
        aliRequestParam.setOut_trade_no(out_trade_no);
        aliRequestParam.setBuyer_logon_id(logonid);
        aliRequestParam.setSubject("我是大老鼠");
        aliRequestParam.setTotal_amount(0.01f);
        AlipayTradeCreateResponse response = null;
        try {
            String fw=balanceService.boolIsProvider(Integer.parseInt(out_trade_no.substring(0,6)));
            if("NO".equals(fw)){
                response = aliPayApi.create(aliRequestParam);
            }else {
                response = aliPayApifw.create(aliRequestParam);
            }

            log.info("订单生成结果\n" + JacksonUtils.obj2json(response));
            return "orderID---->"+response.getOutTradeNo() + "  TradeNo----->" + response.getTradeNo();
        } catch (Exception e) {
            log.info("支付宝支付异常-PayException:{}", e);
            return "error";
        }
    }

    @RequestMapping("/refund")
    @ResponseBody
    public String refund(String orderid) {
        AliRequestParam aliRequestParam = new AliRequestParam();
        aliRequestParam.setOut_trade_no(orderid);
        aliRequestParam.setRefund_amount(0.01f);
        try {
            AlipayTradeRefundResponse response;
            String fw=balanceService.boolIsProvider(Integer.parseInt(orderid.substring(0,6)));
            if("NO".equals(fw)){
                response = aliPayApi.refund(aliRequestParam);
            }else {
                response = aliPayApifw.refund(aliRequestParam);
            }
            if(response.isSuccess()) {
                log.info("退款成功结果\n" + JacksonUtils.obj2json(response));
                return "success";
            }
            else {
                log.info("失败原因：" + response.getSubMsg());
                return "fail";
            }
        } catch (Exception e) {
            log.info("支付宝支付异常-PayException:{}", e);
            return "error";
        }
    }

    @RequestMapping("/cancel")
    @ResponseBody
    public String cancel(String orderid) {
        AliRequestParam aliRequestParam = new AliRequestParam();
        aliRequestParam.setOut_trade_no(orderid);
        try {
            AlipayTradeCancelResponse response;
            String fw=balanceService.boolIsProvider(Integer.parseInt(orderid.substring(0,6)));
            if("NO".equals(fw)){
                response = aliPayApi.cancel(aliRequestParam);
            }else {
                response = aliPayApifw.cancel(aliRequestParam);
            }
            if(response.isSuccess()) {
                log.info("订单取消结果\n" + JacksonUtils.obj2json(response));
                return "success";
            }
            else {
                log.info("失败原因：" + response.getSubMsg());
                return "fail";
            }
        } catch (Exception e) {
            log.info("支付宝支付异常-PayException:{}", e);
            return "error";
        }
    }

    @RequestMapping("/close")
    @ResponseBody
    public String close(String orderid) {
        AliRequestParam aliRequestParam = new AliRequestParam();
        aliRequestParam.setOut_trade_no(orderid);
        try {

            AlipayTradeCloseResponse response;
            String fw=balanceService.boolIsProvider(Integer.parseInt(orderid.substring(0,6)));
            if("NO".equals(fw)){
                response = aliPayApi.close(aliRequestParam);
            }else {
                response = aliPayApifw.close(aliRequestParam);
            }
            if(response.isSuccess()) {
                log.info("订单关闭结果\n" + JacksonUtils.obj2json(response));
                return "success";
            }
            else {
                log.info("失败原因：" + response.getSubMsg());
                return "fail";
            }
        } catch (Exception e) {
            log.error("PayException", e);
            return "error";
        }
    }

    @RequestMapping("/pay")
    @ResponseBody
    public ReturnDto pay(Integer site_id, String auth_code, String trades_id) {
        if(StringUtil.isEmpty(site_id))
            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        if(StringUtil.isEmpty(trades_id))
            return ReturnDto.buildFailedReturnDto("trades_id不能为空");
        if(StringUtil.isEmpty(auth_code))
            return ReturnDto.buildFailedReturnDto("auth_code不能为空");
        Trades trades = tradesService.getTradesByTradesId(Long.parseLong(trades_id));
        if(trades == null) {
            return ReturnDto.buildFailedReturnDto("订单不存在");
        }
        try {
            String tradesName=orderService.findTradesName(trades.getSiteId(),trades.getTradesId());
            String result = "";
            String fw=balanceService.boolIsProvider(Integer.parseInt(trades_id.substring(0,6)));
            if("NO".equals(fw)||trades.getIsServiceOrder()==0){
                result = payService.aliPay(site_id,trades.getTradesId(),auth_code,tradesName,trades.getRealPay());
            }else {
                result = payfwService.aliPay(site_id,trades.getTradesId(),auth_code,tradesName,trades.getRealPay());
            }
            if(result != null)
            {
               /*trades.setPayNumber(result);
                trades.setPayStyle(PayConstant.PAY_STYLE_ALI);
                trades.setCashierId(trades.getStoreUserId());
                tradesService.paySuccessCallback(trades);*/
                return ReturnDto.buildSuccessReturnDto("支付成功");
            }
            else {
                return ReturnDto.buildFailedReturnDto("支付失败");
            }
        } catch (Exception e) {
            log.error("PayException", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }


    @RequestMapping("/appPay")
    @ResponseBody
    public ReturnDto appPay(Integer site_id, String trades_id) {
        if(StringUtil.isEmpty(site_id))
            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        if(StringUtil.isEmpty(trades_id))
            return ReturnDto.buildFailedReturnDto("trades_id不能为空");
        Trades trades = tradesService.getTradesByTradesId(Long.parseLong(trades_id));
        if(trades == null) {
            return ReturnDto.buildFailedReturnDto("订单不存在");
        }
        try {
            String result = payService.aliAppPay(site_id,trades.getTradesId(),trades.getRealPay());
            if(result != null)
            {
                return ReturnDto.buildSuccessReturnDto(result);
            }
            else {
                return ReturnDto.buildFailedReturnDto("支付失败");
            }
        } catch (Exception e) {
            log.error("PayException", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }



    @RequestMapping("/query")
    @ResponseBody
    public String query(String orderid) {
        AliRequestParam aliRequestParam = new AliRequestParam();
        aliRequestParam.setOut_trade_no(orderid);
        try {
            AlipayTradeQueryResponse response = payService.aliquery(aliRequestParam);
            if(response.isSuccess()) {
                log.info("订单查询结果\n" + JacksonUtils.obj2json(response));
                return "success,trade_no:" + response.getTradeNo();
            } else {
                log.info("失败原因：" + response.getSubMsg());
                return "fail";
            }
        } catch (Exception e) {
            log.error("PayException", e);
            return "error";
        }
    }

    @RequestMapping("/queryrefund")
    @ResponseBody
    public String queryRefund(String orderid, String out_request_no) {
        AliRequestParam aliRequestParam = new AliRequestParam();
        aliRequestParam.setOut_trade_no(orderid);
        aliRequestParam.setOut_request_no(out_request_no);
        try {
            AlipayTradeFastpayRefundQueryResponse response = aliPayApi.queryRefund(aliRequestParam);
            if(response.isSuccess()) {
                log.info("订单查询结果\n" + JacksonUtils.obj2json(response));
                return "success";
            } else {
                log.info("失败原因：" + response.getSubMsg());
                return "fail";
            }
        } catch (Exception e) {
            log.error("PayException", e);
            return "error";
        }
    }
    /**
     * 支付宝查询对账单下载地址
     * @param siteId
     * @param bill_date 账单时间：日账单格式为yyyy-MM-dd，月账单格式为yyyy-MM。
     * @return
     */
    @RequestMapping(value = "/downloadurlQuery",method = RequestMethod.POST)
    @ResponseBody
    public String downloadurlQuery(Integer siteId,String bill_date) {

        try {
            return aliPayApifw.downloadurlQuery(siteId,bill_date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
