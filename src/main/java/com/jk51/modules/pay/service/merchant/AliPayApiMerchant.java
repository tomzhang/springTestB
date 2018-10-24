package com.jk51.modules.pay.service.merchant;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.Trades;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.pay.constants.PayConstant;
import com.jk51.modules.pay.exception.PayException;
import com.jk51.modules.pay.service.ali.AliConfig;
import com.jk51.modules.pay.service.ali.request.AliRequestParam;
import com.jk51.modules.persistence.mapper.DiscountMapper;
import com.jk51.modules.trades.service.TradesService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Component
public class AliPayApiMerchant {
    private static final Logger log = LoggerFactory.getLogger(AliPayApiMerchant.class);


    ;
    @Autowired
    private DiscountMapper discountMapper;
    @Autowired
    private OrderService orderService;
    /**
     * 支付宝网站支付
     *
     * @param tradeId
     * @return
     */
    public Map alipayTradeWapPay(String tradeId, String reUrl,String product_code,Integer siteId){
        AliConfigMerchant aliConfig= null;
        Map result = new HashedMap();
        try {
            aliConfig = toConfig(siteId);
            Map<String, Object> tradesMap =new HashedMap();
            tradesMap.put("trades_id",tradeId);
            Map<String, Object> trades= discountMapper.getTradesLine(tradesMap);
            // 商户订单号，商户网站订单系统中唯一订单号，必填
            String out_trade_no = tradeId;
            // 订单名称，必填
            String subject =orderService.findTradesName(siteId,Long.parseLong(tradeId));
            // 付款金额，必填
            double total=Integer.parseInt(trades.get("real_pay")+"");
            String total_amount = total/100+"";

            // 商品描述，可空
            String body = "200";
            // 超时时间 可空
            String timeout_express = "2m";
            // 销售产品码 必填
            //String product_code = "QUICK_WAP_PAY";
            AlipayClient client = new DefaultAlipayClient(aliConfig.getUrl(), aliConfig.getApp_id(), aliConfig.getPrivate_key(), aliConfig.getFormat(), aliConfig.getCharset(), aliConfig.getAli_public_key(), aliConfig.getSign_type());
            AlipayTradeWapPayRequest alipay_request = new AlipayTradeWapPayRequest();

            AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
            model.setOutTradeNo(out_trade_no);
            model.setSubject(subject);
            model.setTotalAmount(total_amount);
            model.setBody(body);
            model.setTimeoutExpress(timeout_express);
            model.setProductCode(product_code);
            alipay_request.setBizModel(model);
            alipay_request.setNotifyUrl(aliConfig.getNotify_url());
            alipay_request.setReturnUrl(reUrl);
            result.put("status", "success");
            result.put("formBody", client.pageExecute(alipay_request).getBody());
        } catch(Exception e){
            e.printStackTrace();
            result.put("status", "error");
            result.put("msg", "该商家暂不支持支付宝支付！");
        }
        return result;
    }
    public AliConfigMerchant toConfig(Integer siteId) throws IllegalAccessException{
        if(siteId == null){
            return null;
        }
        Map<String,Object> params=new HashedMap();
        params.put("siteId",siteId);
        Map<String,Object> payMap = discountMapper.getPayAliWx(params);
        if(payMap==null)return null;
        //(String app_id, String seller_id,
        // String private_key, String public_key,
        // String notify_url, String ali_public_key,
        // String log_path, String return_url) {
        AliConfigMerchant aliConfig=new AliConfigMerchant(
                payMap.get("ali_app_id").toString(), payMap.get("ali_seller_id").toString(),
                payMap.get("ali_private_key").toString(), payMap.get("public_key").toString(),
                payMap.get("ali_notify_url").toString(), payMap.get("ali_public_key").toString(),
                payMap.get("ali_log_path").toString(),  payMap.get("ali_return_url").toString());
        return aliConfig;
    }

}
