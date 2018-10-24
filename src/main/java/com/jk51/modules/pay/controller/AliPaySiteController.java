package com.jk51.modules.pay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.pay.PayInterfaceLog;
import com.jk51.modules.discount.service.DiscountService;
import com.jk51.modules.pay.constants.PayConstant;
import com.jk51.modules.pay.service.PayInterfaceLogService;
import com.jk51.modules.pay.service.ali.request.AliRequestParam;
import com.jk51.modules.pay.service.merchant.AliConfigMerchant;
import com.jk51.modules.pay.service.merchant.AliPayApiMerchant;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Controller
@RequestMapping("/site/ali")
public class AliPaySiteController {

    private static final Logger log = LoggerFactory.getLogger(AliPaySiteController.class);
    @Autowired
    AliConfigMerchant aliConfig;

    @Autowired
    AliPayApiMerchant aliPayApi;
    @Autowired
    DiscountService discountService;
    @Autowired
    PayInterfaceLogService payInterfaceLogService;

    @RequestMapping(value = "/callback")
    @ResponseBody
    public String aliPayCallback(HttpServletRequest request) {
        Map map = ParameterUtil.getParameterMap(request);
        log.info("支付宝回调参数{}", JacksonUtils.mapToJson(map));
        long startMillis= System.currentTimeMillis();
        String signType = (String)map.get("sign_type");
        String out_trade_no = (String) map.get("out_trade_no");
        Integer siteId=Integer.parseInt(out_trade_no.substring(0,6));
        try {
            aliConfig=aliPayApi.toConfig(siteId);
            if(AlipaySignature.rsaCheckV1(map, aliConfig.getAli_public_key(), "utf-8",signType)) {
                String trade_status = (String) map.get("trade_status");
                if(!"TRADE_SUCCESS".equals(trade_status)) {
                    return "success";
                }

                String seller_email = (String) map.get("seller_email");
                String app_id = (String) map.get("app_id");
                Map<String, Object> tradesMap =new HashedMap();
                tradesMap.put("trades_id",out_trade_no);
                Map<String, Object> trades = discountService.getTradesLine(tradesMap);
                String receipt_amount = (String)map.get("receipt_amount");
                Integer totalFee = new Float(Float.parseFloat(receipt_amount) * 100).intValue();
                if(trades != null && totalFee.equals(Integer.parseInt(trades.get("real_pay")+""))
                        && aliConfig.getApp_id().equals(app_id)
                        //&& aliConfig.getSeller_id().equals(seller_email)
                        ) {
                    try {
                        //回调成功后取消支付宝和微信生成的订单
                        AliRequestParam param = new AliRequestParam();
                        param.setOut_trade_no(out_trade_no);

                        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
                        payInterfaceLog.setTradesId(Long.parseLong(out_trade_no));
                        payInterfaceLog.setTransactionId((String) map.get("trade_no"));
                        payInterfaceLog.setPayResult(JacksonUtils.mapToJson(map));
                        payInterfaceLog.setExeResult(PayConstant.PAY_INTERFACE_EXE_SUCCESS);
                        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_ALI);
                        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_ALI_CB);
                        payInterfaceLog.setSiteId(siteId);
                        payInterfaceLog.setTradesFee(Integer.parseInt(trades.get("real_pay")+""));
                        payInterfaceLogService.insert(payInterfaceLog);
                       /* Map<String, Object> maptrades=new HashedMap();
                        maptrades.put("trades_id",out_trade_no);
                        maptrades.put("real_pay",totalFee);
                        maptrades.put("is_payment",1);
                        maptrades.put("pay_style","ali");
                        maptrades.put("pay_time",new Date());
                        maptrades.put("pay_number",map.get("trade_no"));
                        maptrades.put("site_id",siteId);*/
                        String pay_number=map.get("trade_no")+"";
                        long tradesId = NumberUtils.toLong(out_trade_no);
                        Map<String, Object> tradesup = discountService.updateTradesLine("ali",tradesId,siteId,pay_number);
                        if("-1".equals(tradesup.get("status"))) {
                            log.info("订单号为：{},无法执行付款操作", out_trade_no);
                        }

                    }  catch (Exception e) {
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
            e.printStackTrace();
        }
        log.info("支付宝支付支付回调耗时: {}", System.currentTimeMillis() - startMillis);
        return "success";
    }
}
