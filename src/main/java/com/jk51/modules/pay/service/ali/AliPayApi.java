package com.jk51.modules.pay.service.ali;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.Trades;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.pay.constants.PayConstant;
import com.jk51.modules.pay.exception.PayException;
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
public class AliPayApi {
    private static final Logger log = LoggerFactory.getLogger(AliPayApi.class);

    @Autowired
    AliConfig aliConfig;

    AlipayClient alipayClient;

    @Autowired
    private TradesService tradesService;
    @Autowired
    private DiscountMapper discountMapper;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private OrderService orderService;
    /**
     * 生成扫码订单
     * @param aliRequestParam
     * @return
     */
    public AlipayTradePrecreateResponse precreate(AliRequestParam aliRequestParam) throws Exception {
        if(StringUtil.isEmpty(aliRequestParam.getOut_trade_no())) {
            throw new PayException("缺少统一支付支付接口必填参数out_trade_no! ");
        } else if(StringUtil.isEmpty(aliRequestParam.getTotal_amount())) {
            throw new PayException("缺少统一支付支付接口必填参数total_amount! ");
        } else if(StringUtil.isEmpty(aliRequestParam.getSubject())) {
            throw new PayException("缺少统一支付支付接口必填参数subject! ");
        }

        //AliConfig aliConfig = toConfig(aliRequestParam.getOut_trade_no());
        alipayClient = new DefaultAlipayClient(PayConstant.ALI_REQUEST_URL,
                aliConfig.getApp_id(),aliConfig.getPrivate_key(),"json",
                "utf-8",aliConfig.getAli_public_key(),"RSA2");
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl(aliConfig.getNotify_url());
        request.setBizContent(JacksonUtils.obj2jsonIgnoreNull(aliRequestParam));
        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        return response;
    }

    /**
     * 订单查询
     * @param aliRequestParam
     * @return
     * @throws Exception
     */
    public AlipayTradeQueryResponse query(AliRequestParam aliRequestParam) throws Exception  {
        if(StringUtil.isEmpty(aliRequestParam.getOut_trade_no()) && StringUtil.isEmpty(aliRequestParam.getTrade_no())) {
            throw new PayException("订单查询接口中，out_trade_no、trade_no至少填一个！ ");
        }
        Trades trades = tradesService.getTradesByTradesId(Long.parseLong(aliRequestParam.getOut_trade_no()));
        AliConfig aliConfig = toConfig(aliRequestParam.getOut_trade_no());
        alipayClient = new DefaultAlipayClient(PayConstant.ALI_REQUEST_URL,
                aliConfig.getApp_id(),aliConfig.getPrivate_key(),"json",
                "utf-8",aliConfig.getAli_public_key(),"RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent(JacksonUtils.obj2jsonIgnoreNull(aliRequestParam));
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if (response.isSuccess()&&response.getTradeStatus().equals("TRADE_SUCCESS")) {

            trades.setPayNumber(response.getTradeNo());
            trades.setPayStyle(PayConstant.PAY_STYLE_ALI);
            trades.setCashierId(trades.getStoreUserId());
            String body=response.getBody()+"";
            if(!StringUtil.isEmpty(body)){
                try {
                    trades.setTradeTypePayLine(Integer.parseInt(body));
                }catch (Exception e) {
                    trades.setTradeTypePayLine(210);
                }
            }else {
                trades.setTradeTypePayLine(210);
            }
            if(!tradesService.paySuccessCallback(trades)) {
                log.error("订单号为：{},无法执行付款操作", trades.getTradesId());
            }
        }
        return response;
    }

    /**
     * 退款
     * @param aliRequestParam
     * @return
     * @throws Exception
     */
    public AlipayTradeRefundResponse refund(AliRequestParam aliRequestParam) throws Exception {
        if(StringUtil.isEmpty(aliRequestParam.getOut_trade_no()) && StringUtil.isEmpty(aliRequestParam.getTrade_no())) {
            throw new PayException("订单退款接口中，out_trade_no、trade_no至少填一个！ ");
        } else if(StringUtil.isEmpty(aliRequestParam.getRefund_amount())) {
            throw new PayException("缺少订单退款接口必须参数refund_amount！ ");
        }
        AliConfig aliConfig = toConfig(aliRequestParam.getOut_trade_no());
        alipayClient = new DefaultAlipayClient(PayConstant.ALI_REQUEST_URL,
                aliConfig.getApp_id(),aliConfig.getPrivate_key(),"json",
                "utf-8",aliConfig.getAli_public_key(),"RSA2");
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent(JacksonUtils.obj2jsonIgnoreNull(aliRequestParam));
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        return response;
    }

    /**
     * 刷卡支付
     * @param aliRequestParam
     * @return
     * @throws Exception
     */
    public AlipayTradePayResponse pay(AliRequestParam aliRequestParam) throws Exception{
        if(StringUtil.isEmpty(aliRequestParam.getOut_trade_no())) {
            throw new PayException("缺少统一支付支付接口必填参数out_trade_no! ");
        } else if(StringUtil.isEmpty(aliRequestParam.getScene())) {
            throw new PayException("缺少统一支付支付接口必填参数scene! ");
        } else if(StringUtil.isEmpty(aliRequestParam.getAuth_code())) {
            throw new PayException("缺少统一支付支付接口必填参数auth_code! ");
        } else if(StringUtil.isEmpty(aliRequestParam.getSubject())) {
            throw new PayException("缺少统一支付支付接口必填参数subject! ");
        }
        AliConfig aliConfig = toConfig(aliRequestParam.getOut_trade_no());
        alipayClient = new DefaultAlipayClient(PayConstant.ALI_REQUEST_URL,
                aliConfig.getApp_id(),aliConfig.getPrivate_key(),"json",
                "utf-8",aliConfig.getAli_public_key(),"RSA2");
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        request.setNotifyUrl(aliConfig.getNotify_url());
        request.setBizContent(JacksonUtils.obj2jsonIgnoreNull(aliRequestParam));
        AlipayTradePayResponse response = alipayClient.execute(request);
        return response;
    }


    /**
     * 支付交易返回失败或支付系统超时，调用该接口撤销交易。
     * @param aliRequestParam
     * @return
     * @throws Exception
     */
    public AlipayTradeCancelResponse cancel(AliRequestParam aliRequestParam) throws Exception{
        if(StringUtil.isEmpty(aliRequestParam.getOut_trade_no()) && StringUtil.isEmpty(aliRequestParam.getTrade_no())) {
            throw new PayException("订单取消接口中，out_trade_no、trade_no至少填一个！ ");
        }
        AliConfig aliConfig = toConfig(aliRequestParam.getOut_trade_no());
        alipayClient = new DefaultAlipayClient(PayConstant.ALI_REQUEST_URL,
                aliConfig.getApp_id(),aliConfig.getPrivate_key(),"json",
                "utf-8",aliConfig.getAli_public_key(),"RSA2");
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        request.setBizContent(JacksonUtils.obj2jsonIgnoreNull(aliRequestParam));
        AlipayTradeCancelResponse response = alipayClient.execute(request);
        return response;
    }

    /**
     * 创建下单
     * @param aliRequestParam
     * @return
     * @throws Exception
     */
    public AlipayTradeCreateResponse create(AliRequestParam aliRequestParam) throws Exception {
        if(StringUtil.isEmpty(aliRequestParam.getOut_trade_no())) {
            throw new PayException("缺少统一支付支付接口必填参数out_trade_no! ");
        } else if(StringUtil.isEmpty(aliRequestParam.getTotal_amount())) {
            throw new PayException("缺少统一支付支付接口必填参数total_amount! ");
        } else if(StringUtil.isEmpty(aliRequestParam.getSubject())) {
            throw new PayException("缺少统一支付支付接口必填参数subject! ");
        } else if(StringUtil.isEmpty(aliRequestParam.getBuyer_id()) && StringUtil.isEmpty(aliRequestParam.getBuyer_logon_id())) {
            throw new PayException("统一支付支付接口中，buyer_id、buyer_logon_id至少填一个！ ");
        }
        AliConfig aliConfig = toConfig(aliRequestParam.getOut_trade_no());
        alipayClient = new DefaultAlipayClient(PayConstant.ALI_REQUEST_URL,
                aliConfig.getApp_id(),aliConfig.getPrivate_key(),"json",
                "utf-8",aliConfig.getAli_public_key(),"RSA2");
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
        request.setBizContent(JacksonUtils.obj2jsonIgnoreNull(aliRequestParam));
        AlipayTradeCreateResponse response = alipayClient.execute(request);
        return response;
    }

    /**
     * 查询退款订单
     * @param aliRequestParam
     * @return
     * @throws Exception
     */
    public AlipayTradeFastpayRefundQueryResponse queryRefund(AliRequestParam aliRequestParam) throws Exception  {
        if(StringUtil.isEmpty(aliRequestParam.getOut_trade_no()) && StringUtil.isEmpty(aliRequestParam.getTrade_no())) {
            throw new PayException("退款订单查询接口中，out_trade_no、trade_no至少填一个！ ");
        } else if(StringUtil.isEmpty(aliRequestParam.getOut_request_no())) {
            throw new PayException("缺少退款订单查询接口必填参数out_request_no! ");
        }
        AliConfig aliConfig = toConfig(aliRequestParam.getOut_trade_no());
        alipayClient = new DefaultAlipayClient(PayConstant.ALI_REQUEST_URL,
                aliConfig.getApp_id(),aliConfig.getPrivate_key(),"json",
                "utf-8",aliConfig.getAli_public_key(),"RSA2");
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        request.setBizContent(JacksonUtils.obj2jsonIgnoreNull(aliRequestParam));
        AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
        return response;
    }

    /**
     * 关闭订单
     * @param aliRequestParam
     * @return
     * @throws Exception
     */
    public AlipayTradeCloseResponse close(AliRequestParam aliRequestParam) throws Exception {
        if(StringUtil.isEmpty(aliRequestParam.getOut_trade_no()) && StringUtil.isEmpty(aliRequestParam.getTrade_no())) {
            throw new PayException("订单关闭接口中，out_trade_no、trade_no至少填一个！ ");
        }
        AliConfig aliConfig = toConfig(aliRequestParam.getOut_trade_no());
        alipayClient = new DefaultAlipayClient(PayConstant.ALI_REQUEST_URL,
                aliConfig.getApp_id(),aliConfig.getPrivate_key(),"json",
                "utf-8",aliConfig.getAli_public_key(),"RSA2");
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        request.setBizContent(JacksonUtils.obj2jsonIgnoreNull(aliRequestParam));
        AlipayTradeCloseResponse response = alipayClient.execute(request);
        return response;
    }

    /**
     * 分单
     * @param aliRequestParam
     * @return
     * @throws Exception
     */
    public AlipayTradeOrderSettleResponse settle(AliRequestParam aliRequestParam) throws Exception {
        if(StringUtil.isEmpty(aliRequestParam.getOut_trade_no()) && StringUtil.isEmpty(aliRequestParam.getTrade_no())) {
            throw new PayException("订单分单接口中，out_trade_no、trade_no至少填一个！ ");
        } else if(aliRequestParam.getRoyalty_parameters() == null || aliRequestParam.getRoyalty_parameters().size() == 0) {
            throw new PayException("缺少退款订单分单接口必填参数royalty_parameters! ");
        }
        AliConfig aliConfig = toConfig(aliRequestParam.getOut_trade_no());
        alipayClient = new DefaultAlipayClient(PayConstant.ALI_REQUEST_URL,
                aliConfig.getApp_id(),aliConfig.getPrivate_key(),"json",
                "utf-8",aliConfig.getAli_public_key(),"RSA2");
        AlipayTradeOrderSettleRequest request = new AlipayTradeOrderSettleRequest();
        request.setBizContent(JacksonUtils.obj2jsonIgnoreNull(aliRequestParam));
        AlipayTradeOrderSettleResponse response = alipayClient.execute(request);
        return response;
    }

    /**
     * 支付宝网站支付
     *
     * @param tradeId
     * @return
     */
    public Map alipayTradeWapPay(String tradeId, String reUrl,String product_code){
        Map result = new HashedMap();
        try {
            Trades t = tradesService.getTradesByTradesId(Long.valueOf(tradeId));
            // 商户订单号，商户网站订单系统中唯一订单号，必填
            String out_trade_no = tradeId;
            // 订单名称，必填
            String subject = orderService.findTradesName(t.getSiteId(),t.getTradesId());
            // 付款金额，必填
            double total=t.getRealPay();
            String total_amount = total/100+"";

            // 商品描述，可空
            String body = "200";
            // 超时时间 可空
            String timeout_express = "2m";
            // 销售产品码 必填
            //String product_code = "QUICK_WAP_PAY";

            AliConfig aliConfig = toConfig(tradeId);
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
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 支付宝App支付
     * @param tradeId
     * @return
     */
    public AlipayTradeAppPayResponse alipayTradeAppPay(String tradeId){
        Map result = new HashedMap();
        try {
            Trades t = tradesService.getTradesByTradesId(Long.valueOf(tradeId));
            // 商户订单号，商户网站订单系统中唯一订单号，必填
            String out_trade_no = tradeId;
            // 订单名称，必填
            String subject = "51JK";
            // 付款金额，必填
            double total=t.getRealPay();
            String total_amount = total/100+"";
            // 超时时间 可空
            String timeout_express = "2m";
            // 销售产品码 必填
            String product_code = "QUICK_MSECURITY_PAY";
            AliConfig aliConfig = toConfig(tradeId);
            AlipayClient client = new DefaultAlipayClient(aliConfig.getUrl(), aliConfig.getApp_id(), aliConfig.getPrivate_key(), aliConfig.getFormat(), aliConfig.getCharset(), aliConfig.getAli_public_key(), aliConfig.getSign_type());
            AlipayTradeAppPayRequest request=new AlipayTradeAppPayRequest();
            AlipayTradeAppPayModel model=new AlipayTradeAppPayModel();
            model.setOutTradeNo(out_trade_no);
            model.setSubject(subject);
            model.setTotalAmount(total_amount);
            model.setTimeoutExpress(timeout_express);
            model.setProductCode(product_code);
            model.setBody("220");
            request.setBizModel(model);
            request.setNotifyUrl(aliConfig.getNotify_url());
            AlipayTradeAppPayResponse response = client.sdkExecute(request);
            return  response;
        } catch(Exception e){
            e.printStackTrace();
            result.put("status", "error");
            result.put("msg", e.getMessage());
        }
        return null;
    }

    public AliConfig toConfig(String tradeId) throws IllegalAccessException{

        if(tradeId == null){
            return null;
        }
        Integer siteId=Integer.parseInt(tradeId.substring(0,6));
        Map<String,Object> params=new HashedMap();
        params.put("siteId",siteId);
        Map<String,Object> payMap = discountMapper.getPayAliWx(params);
        //查看该商家是否是服务商商户
        String fw=balanceService.boolIsProvider(siteId);
        if("NO".equals(fw)||payMap==null)return aliConfig;
        AliConfig aliConfigz=new AliConfig(
                payMap.get("ali_app_id").toString(), payMap.get("ali_seller_id").toString(),
                payMap.get("ali_private_key").toString(), payMap.get("public_key").toString(),
                aliConfig.getNotify_url(), payMap.get("ali_public_key").toString(),
                payMap.get("ali_log_path").toString(),  payMap.get("ali_return_url").toString());
        return aliConfigz;
    }
}
