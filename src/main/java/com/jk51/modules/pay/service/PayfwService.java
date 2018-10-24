package com.jk51.modules.pay.service;

import com.alipay.api.response.*;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.commons.xml.XmlUtils;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.order.Trades;
import com.jk51.model.pay.PayInterfaceLog;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.pay.constants.PayConstant;
import com.jk51.modules.pay.exception.PayException;
import com.jk51.modules.pay.mapper.PayInterfaceLogMapper;
import com.jk51.modules.pay.service.ali.AliConfig;
import com.jk51.modules.pay.service.ali.AliPayApifw;
import com.jk51.modules.pay.service.ali.request.AliRequestParam;
import com.jk51.modules.pay.service.wx.*;
import com.jk51.modules.pay.service.wx.request.WxRedpackRequestParam;
import com.jk51.modules.pay.service.wx.request.WxRequestParam;
import com.jk51.modules.pay.service.wx.request.WxXcxConfig;
import com.jk51.modules.trades.service.TradesService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Service
public class PayfwService {

    private static final Logger log = LoggerFactory.getLogger(PayfwService.class);
    @Autowired
    WxConfig wxConfigold;
    @Autowired
    WxConfigfw wxConfigfw;
    @Autowired
    WxAppConfig wxAppConfig;

    @Autowired
    WxConfig51jk wxConfig51jk;
    @Autowired
    AliConfig aliConfig;
    @Autowired
    AliPayApifw aliPayApi;
    @Autowired
    WxPayApifw wxPayApi;
    @Autowired
    PayInterfaceLogService payInterfaceLogService;
    @Autowired
    PayLogsService payLogsService;
    @Autowired
    PayInterfaceLogMapper payInterfaceLogMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    OrderService orderService;

    @Autowired
    TradesService tradesService;
    @Autowired
    WxXcxConfig wxXcxConfig;

    public boolean noMoneyPay(Trades trades) throws BusinessLogicException{
        if(trades == null || trades.getTradesId() == null)
            return false;
        trades = tradesService.getTradesByTradesId(trades.getTradesId());
        if(trades == null)
            return false;
        payLogsService.addByTrades(trades.getTradesId(),PayConstant.PAY_STYLE_CASH,PayConstant.PAY_STATUS_FAIL);
        payLogsService.updateByTrades(trades.getTradesId(), null,PayConstant.PAY_STYLE_CASH,PayConstant.PAY_STATUS_SUCCESS,new Date(),null, null, null);
        if (!StringUtils.equals(trades.getPayStyle(), CommonConstant.TRADES_PAY_TYPE_INTEGRAL)) {
            trades.setPayStyle(PayConstant.PAY_STYLE_CASH);
        }
        return tradesService.paySuccessCallback(trades);
    }

    /**
     * 现金支付
     * @param tradesId
     * @param cashPaymentPay
     * @param medicalInsuranceCardPay
     * @param lineBreaksPay
     * @param cashReceiptNote
     * @return
     */
    public boolean cashPay(Long tradesId, Integer cashPaymentPay, Integer medicalInsuranceCardPay, Integer lineBreaksPay, String cashReceiptNote) {

        try {
            //防止客户已经支付(查询之前的订单是否微信已经支付)
            String oldtradesId=stringRedisTemplate.opsForValue().get(tradesId+"_wxOrder");
            log.info("--------------------------------------oldtradesId"+oldtradesId);

            //防止客户已经支付(查询之前的订单是否微信没有支付直接取消)
            if(!StringUtil.isEmpty(oldtradesId)){
                log.info("--------------------------------------quxiao"+oldtradesId);
                //this.wxReverse(siteId, oldtradesId, null);
                this.wxClose(Integer.parseInt(oldtradesId.substring(0,5)), oldtradesId);
            }

            String alioldtradesId=stringRedisTemplate.opsForValue().get(tradesId+"_aliOrder");
            log.info("--------------------------------------alioldtradesId"+alioldtradesId);
            AliRequestParam param = new AliRequestParam();
            if(!StringUtil.isEmpty(alioldtradesId)){
                log.info("--------------------------------------quxiao"+alioldtradesId);
                param.setOut_trade_no(alioldtradesId);
                aliPayApi.close(param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        payLogsService.addByTrades(tradesId,medicalInsuranceCardPay>0?PayConstant.PAY_STYLE_HEALTH_INSURANCE:PayConstant.PAY_STYLE_CASH,PayConstant.PAY_STATUS_FAIL);
        int result = orderService.updateTradesExt(tradesId.toString(),cashPaymentPay,medicalInsuranceCardPay,lineBreaksPay,cashReceiptNote);
        if(result > 0) {
            payLogsService.updateByTrades(tradesId,null,
                    medicalInsuranceCardPay>0?PayConstant.PAY_STYLE_HEALTH_INSURANCE:PayConstant.PAY_STYLE_CASH,
                    PayConstant.PAY_STATUS_SUCCESS,new Date(),null,null,null);
            return true;
        }
        return false;
    }

    public String cashRefund(Long tradesId) {
        payLogsService.updateByTrades(tradesId, PayConstant.PAY_STATUS_REFUND);
        return "0";
    }

    /**
     * 支付宝预下单
     * @param tradesId
     * @param tradesFee
     * @param tradesName
     * @return
     * @throws Exception
     */
    public String aliPreCreate(Integer siteId, Long tradesId, int tradesFee, String tradesName) throws PayException {
        payLogsService.addByTrades(tradesId,PayConstant.PAY_STYLE_ALI_QRCODE,PayConstant.PAY_STATUS_FAIL);
        AliRequestParam aliRequestParam = new AliRequestParam();
        aliRequestParam.setOut_trade_no(Long.toString(tradesId));
        aliRequestParam.setTotal_amount(tradesFee/100f);
        aliRequestParam.setSubject(tradesName);
        aliRequestParam.setBody("210");
        try {
            //防止客户已经支付(查询之前的订单是否微信已经支付)
            String oldtradesId=stringRedisTemplate.opsForValue().get(tradesId+"_wxOrder");
            log.info("--------------------------------------oldtradesId"+oldtradesId);
            if(!StringUtil.isEmpty(oldtradesId)){
                Map oldqueryResult = this.wxQueryOrder(siteId, oldtradesId, null);
                if(oldqueryResult != null && "SUCCESS".equals(oldqueryResult.get("trade_state"))
                        && tradesFee == Integer.parseInt((String) oldqueryResult.get("total_fee"))) {
                    log.info("--------------------------------------yizhifu"+oldtradesId);
                    return null;//微信支付成功
                }
            }
            //防止客户已经支付(查询之前的订单是否微信没有支付直接取消)
            if(!StringUtil.isEmpty(oldtradesId)){
                log.info("--------------------------------------quxiao"+oldtradesId);
                //this.wxReverse(siteId, oldtradesId, null);
                Map rema=this.wxClose(siteId, oldtradesId);;
                boolean flag=(boolean)rema.get("flag");
                if(!flag){
                    //return "支付失败！";
                    return null;
                }
            }

            String alioldtradesId=stringRedisTemplate.opsForValue().get(tradesId+"_aliOrder");
            log.info("--------------------------------------alioldtradesId"+alioldtradesId);
            AliRequestParam param = new AliRequestParam();
            if(!StringUtil.isEmpty(alioldtradesId)){
                param.setOut_trade_no(alioldtradesId);
                AlipayTradeQueryResponse res = aliPayApi.query(param);
                if (res.isSuccess()&&res.getTradeStatus().equals("TRADE_SUCCESS")
                        && tradesFee==(new BigDecimal(res.getTotalAmount()).multiply(new BigDecimal(100)).intValue())) {
                    return null;
                }
            }
            /*if(!StringUtil.isEmpty(alioldtradesId)){
                log.info("--------------------------------------quxiao"+alioldtradesId);
                param.setOut_trade_no(alioldtradesId);
                aliPayApi.close(param);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        stringRedisTemplate.opsForValue().set(tradesId+"_aliOrder", String.valueOf(tradesId));

        AlipayTradePrecreateResponse response = null;
        String result = null;
        try {
            response = aliPayApi.precreate(aliRequestParam);
            result = JacksonUtils.obj2json(response);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLogService.insertAliPreCreate(tradesId,tradesFee,siteId,result,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("支付宝预下单失败");
        }
        String qrCode = "";
        if(response.isSuccess()) {
            qrCode = response.getQrCode();
            payInterfaceLogService.insertAliPreCreate(tradesId,tradesFee,siteId,result,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
        } else {
            payInterfaceLogService.insertAliPreCreate(tradesId,tradesFee,siteId,result,PayConstant.PAY_INTERFACE_EXE_FAIL);
        }
        return qrCode;
    }

    /**
     * 支付宝订单查询
     * @param tradesId
     * @param tradesNo
     * @return
     * @throws Exception
     */
    public AlipayTradeQueryResponse aliQuery(Integer siteId, Long tradesId, String tradesNo) throws PayException {
        AliRequestParam aliRequestParam = new AliRequestParam();
        if(tradesId !=null)
            aliRequestParam.setOut_trade_no(Long.toString(tradesId));
        aliRequestParam.setTrade_no(tradesNo);
        AlipayTradeQueryResponse response = null;
        String json = null;
        try {
            response = aliPayApi.query(aliRequestParam);
            json = JacksonUtils.obj2json(response);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLogService.insertAliQuery(tradesId,siteId,null,json,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("支付宝订单查询失败");
        }
        if(response.isSuccess()) {
            payInterfaceLogService.insertAliQuery(tradesId,siteId,response.getTradeNo(),json,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
        } else {
            payInterfaceLogService.insertAliQuery(tradesId,siteId,null,json,PayConstant.PAY_INTERFACE_EXE_FAIL);
        }
        return response;
    }

    /**
     * 支付宝支付
     * @param tradesId
     * @param authCode
     * @param subject
     * @param tradesFee
     * @return
     * @throws Exception
     */
    public String aliPay(Integer siteId, Long tradesId, String authCode, String subject, int tradesFee) throws PayException {
        if(tradesFee <= 0)
            throw new PayException("订单金额不能小于1");
        payLogsService.addByTrades(tradesId,PayConstant.PAY_STYLE_ALI_PAY,PayConstant.PAY_STATUS_FAIL);
        AliRequestParam aliRequestParam = new AliRequestParam();
        aliRequestParam.setOut_trade_no(Long.toString(tradesId));
        aliRequestParam.setAuth_code(authCode);
        aliRequestParam.setSubject(subject);
        aliRequestParam.setTotal_amount(tradesFee/100f);
        aliRequestParam.setScene("bar_code");
        aliRequestParam.setBody("210");
        AlipayTradePayResponse response = null;
        String result = null;
        try {
            response = aliPayApi.pay(aliRequestParam);
            result = JacksonUtils.obj2json(response);
            if(response.isSuccess()) {
                payLogsService.updateByTrades(tradesId,PayConstant.PAY_STYLE_ALI_PAY,response.getTradeNo(),PayConstant.PAY_STATUS_SUCCESS,new Date(),result,aliConfig.getSeller_id(), response.getBuyerPayAmount());
                payInterfaceLogService.insertAliPay(tradesId,tradesFee,siteId,response.getTradeNo(),result,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
                return response.getTradeNo();
            } else {
                log.info("支付宝支付失败:{}", response.getSubMsg());
                payInterfaceLogService.insertAliPay(tradesId,tradesFee,siteId,null,result,PayConstant.PAY_INTERFACE_EXE_FAIL);
                payLogsService.updateByTrades(tradesId, PayConstant.PAY_STATUS_PAY_ERROR);
                return null;
            }
        } catch (Exception e) {
            log.error("Exception", e);
            payLogsService.updateByTrades(tradesId, PayConstant.PAY_STATUS_PAY_ERROR);
            payInterfaceLogService.insertAliPay(tradesId,tradesFee,siteId,null,result,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("支付宝支付失败！");
        }

    }



    /**
     * 支付宝App支付
     * @param tradesId
     * @param tradesFee
     * @return
     * @throws Exception
     */
    public String aliAppPay(Integer siteId, Long tradesId, int tradesFee) throws PayException {
        if(tradesFee <= 0)
            throw new PayException("订单金额不能小于1");
        payLogsService.addByTrades(tradesId,PayConstant.PAY_STYLE_ALI_PAY,PayConstant.PAY_STATUS_FAIL);
        AlipayTradeAppPayResponse response = null;
        String result="";
        try {
            response = aliPayApi.alipayTradeAppPay(String.valueOf(tradesId));
            result = JacksonUtils.obj2json(response);
            if(response.isSuccess()) {
                payLogsService.updateByTrades(tradesId,PayConstant.PAY_STYLE_ALI_APP,response.getTradeNo(),PayConstant.PAY_STATUS_SUCCESS,new Date(),result,aliConfig.getSeller_id(), response.getTotalAmount());
                payInterfaceLogService.insertAliPay(tradesId,tradesFee,siteId,response.getTradeNo(),result,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
                return response.getBody();
            } else {
                log.info("支付宝App支付失败:{}", response.getSubMsg());
                payInterfaceLogService.insertAliPay(tradesId,tradesFee,siteId,null,result,PayConstant.PAY_INTERFACE_EXE_FAIL);
                payLogsService.updateByTrades(tradesId, PayConstant.PAY_STATUS_PAY_ERROR);
                return null;
            }
        } catch (Exception e) {
            log.error("Exception", e);
            payLogsService.updateByTrades(tradesId, PayConstant.PAY_STATUS_PAY_ERROR);
            payInterfaceLogService.insertAliPay(tradesId,tradesFee,siteId,null,result,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("支付宝App支付失败！");
        }

    }

    public String aliRefund(Integer siteId, Long tradesId, Long refundNo, int refundFee) throws Exception {

        AliRequestParam aliRequestParam = new AliRequestParam();
        aliRequestParam.setOut_trade_no(Long.toString(tradesId));
        aliRequestParam.setOut_request_no(Long.toString(refundNo));
        aliRequestParam.setRefund_amount(refundFee/100f);
        AlipayTradeRefundResponse response = null;
        String result = null;
        try {
            response = aliPayApi.refund(aliRequestParam);
            result = JacksonUtils.obj2json(response);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLogService.insertAliRefund(tradesId,refundNo,refundFee,siteId,null,result,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("支付宝退款失败");
        }
        if(response.isSuccess()) {
            payInterfaceLogService.insertAliRefund(tradesId,refundNo,refundFee,siteId,response.getTradeNo(),result,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
            payLogsService.updateByTrades(tradesId, PayConstant.PAY_STATUS_REFUND);
            return response.getTradeNo();
        } else {
            payInterfaceLogService.insertAliRefund(tradesId,refundNo,refundFee,siteId,null,result,PayConstant.PAY_INTERFACE_EXE_FAIL);
            payLogsService.updateByTrades(tradesId, PayConstant.PAY_STATUS_REFUND_ERROR);
            return null;
        }
    }

    /**
     * 微信预生成订单:扫码支付
     * @param tradesId:解决重复问题：tradesId=订单号_随机字符串
     * @param tradesFee
     * @param tradesName
     * @return 默认返回支付的url
     * @throws PayException
     * @throws IOException
     */
    public String wxCreateNativeOrder(Integer siteId, String tradesId, int tradesFee, String tradesName) throws PayException {
        WxRequestParam wxRequestParam = new WxRequestParam();
        //防止客户已经支付(查询之前的订单是否微信已经支付)
        String oldtradesId=stringRedisTemplate.opsForValue().get(tradesId.split("_")[0]+"_wxOrder");
        log.info("--------------------------------------oldtradesId"+oldtradesId);
        if(!StringUtil.isEmpty(oldtradesId)){
            Map oldqueryResult = this.wxQueryOrder(siteId, oldtradesId, null);
            if(oldqueryResult != null && "SUCCESS".equals(oldqueryResult.get("trade_state"))
                    && tradesFee == Integer.parseInt((String) oldqueryResult.get("total_fee"))) {
                log.info("--------------------------------------yizhifu"+oldtradesId);
                return null;
                //return (String)oldqueryResult.get("transaction_id");
            }
        }

        try {
            String alioldtradesId=stringRedisTemplate.opsForValue().get(tradesId.split("_")[0]+"_aliOrder");
            log.info("--------------------------------------alioldtradesId"+alioldtradesId);
            AliRequestParam param = new AliRequestParam();
            if(!StringUtil.isEmpty(alioldtradesId)){
                param.setOut_trade_no(alioldtradesId);
                AlipayTradeQueryResponse res = aliPayApi.query(param);
                if (res.isSuccess()&&res.getTradeStatus().equals("TRADE_SUCCESS")
                        && tradesFee==(new BigDecimal(res.getTotalAmount()).multiply(new BigDecimal(100)).intValue())) {
                    //return res.getTradeNo();
                    return null;
                }
            }
            /*if(!StringUtil.isEmpty(alioldtradesId)){
                log.info("--------------------------------------quxiao"+alioldtradesId);
                param.setOut_trade_no(alioldtradesId);
                aliPayApi.close(param);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        WxConfig wxConfig=wxPayApi.toConfig(tradesId);
        stringRedisTemplate.opsForValue().set(tradesId.split("_")[0]+"_wxOrder",tradesId);
        payLogsService.addByTrades(Long.parseLong(tradesId.split("_")[0]),PayConstant.PAY_STYLE_WX_NATIVE,PayConstant.PAY_STATUS_FAIL);
        wxRequestParam.setAppid(wxConfigfw.getAppid());
        wxRequestParam.setSub_appid(wxConfig.getAppid());
        wxRequestParam.setOut_trade_no(tradesId.toString());
        wxRequestParam.setTotal_fee(tradesFee);
        wxRequestParam.setBody(tradesName);
        wxRequestParam.setTrade_type("NATIVE");
        wxRequestParam.setProduct_id(StringUtil.getRandomChar(32));
        Map map = null;
        try {
            //防止客户已经支付(查询之前的订单是否微信没有支付直接取消)
            if(!StringUtil.isEmpty(oldtradesId)){
                log.info("--------------------------------------quxiao"+oldtradesId);
                //this.wxReverse(siteId, oldtradesId, null);
                Map rema=this.wxClose(siteId, oldtradesId);
                boolean flag=(boolean)rema.get("flag");
                if(!flag){
                    //return "支付失败！";
                    return null;
                }

            }
            String xml =wxPayApi.unifiedOrder(wxRequestParam);
            map = XmlUtils.xml2map(xml);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code")))
                payInterfaceLogService.insertWxCreateNativeOrder(Long.parseLong(tradesId.split("_")[0]),tradesFee,siteId,xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS,tradesId);
            else
            payInterfaceLogService.insertWxCreateNativeOrder(Long.parseLong(tradesId.split("_")[0]),tradesFee,siteId,xml,PayConstant.PAY_INTERFACE_EXE_FAIL,tradesId);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLogService.insertWxCreateNativeOrder(Long.parseLong(tradesId.split("_")[0]),tradesFee,siteId,null,PayConstant.PAY_INTERFACE_EXE_ERROR,tradesId);
            throw new PayException("微信预生成订单失败");
        }
        if(map == null)
            return null;
        return (String) map.get("code_url");
    }

    /**
     * 微信支付预下单：公众号支付
     * @param tradesId
     * @param tradesFee
     * @param tradesName
     * @param openid
     * @return
     * @throws PayException
     * @throws IOException
     */
    public String wxCreateJSAPIOrder(Integer siteId,String trades_id, Long tradesId, int tradesFee, String tradesName, String openid) throws PayException {
        //防止客户已经支付(查询之前的订单是否微信已经支付)
        /*String oldtradesId=stringRedisTemplate.opsForValue().get(tradesId+"_wxOrder");
        log.info("wxCreateJSAPIOrder--------------------------------------oldtradesId"+oldtradesId);
        if(!StringUtil.isEmpty(oldtradesId)){
            this.wxClose(siteId,oldtradesId);
        }*/
        stringRedisTemplate.opsForValue().set(tradesId+"_wxOrder",trades_id);
        WxConfig wxConfig=wxPayApi.toConfig(tradesId+"");
        WxRequestParam wxRequestParam = new WxRequestParam();
//        WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(siteId);
//        if(wxPublicConfig == null)
//            throw new PayException("siteId找不到适合的公众号");
        payLogsService.addByTrades(tradesId,PayConstant.PAY_STYLE_WX_JSAPI,PayConstant.PAY_STATUS_FAIL);
        wxRequestParam.setAppid(wxConfigfw.getAppid());
        wxRequestParam.setSub_appid(wxConfig.getAppid());
        wxRequestParam.setOut_trade_no(trades_id);
        wxRequestParam.setTotal_fee(tradesFee);
        wxRequestParam.setBody(tradesName);
        wxRequestParam.setTrade_type("JSAPI");
        wxRequestParam.setOpenid(openid);
        Map map = null;
        try {
            String xml = wxPayApi.unifiedOrder(wxRequestParam);
            map = XmlUtils.xml2map(xml);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code")))
                payInterfaceLogService.insertWxCreateJSAPIOrder(tradesId,tradesFee,siteId,xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
            else
                payInterfaceLogService.insertWxCreateJSAPIOrder(tradesId,tradesFee,siteId,xml,PayConstant.PAY_INTERFACE_EXE_FAIL);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLogService.insertWxCreateJSAPIOrder(tradesId,tradesFee,siteId,null,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("微信预生成订单失败");
        }
        if(map == null)
            return "";
        return (String) map.get("prepay_id");
    }


    /**
     * 微信支付预下单：APP支付
     * @param tradesId
     * @param tradesFee
     * @param tradesName
     * @return
     * @throws PayException
     * @throws IOException
     */
    public String wxCreateAPPOrder(Integer siteId, Long tradesId, int tradesFee, String tradesName, HttpServletRequest request) throws PayException {
        WxRequestParam wxRequestParam = new WxRequestParam();
        payLogsService.addByTrades(tradesId,PayConstant.PAY_STYLE_WX_APP,PayConstant.PAY_STATUS_FAIL);
        //根据商户获取支付配置
        //getWxAppConfigBySiteId(siteId);
        WxConfig wxConfig=wxPayApi.toConfigSiteId(siteId);
        wxRequestParam.setAppid(wxConfig.getAppid());
        wxRequestParam.setOut_trade_no(tradesId.toString());
        wxRequestParam.setTotal_fee(tradesFee);
        wxRequestParam.setBody(tradesName);
        wxRequestParam.setTrade_type("APP");
        wxRequestParam.setSpbill_create_ip(getIpAddress(request));//获取用户ip地址
        Map map = null;
        try {
            String xml = wxPayApi.unifiedAppOrder(wxRequestParam);
            map = XmlUtils.xml2map(xml);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code")))
                payInterfaceLogService.insertWxCreateAPPOrder(tradesId,tradesFee,siteId,xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
            else
                payInterfaceLogService.insertWxCreateAPPOrder(tradesId,tradesFee,siteId,xml,PayConstant.PAY_INTERFACE_EXE_FAIL);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLogService.insertWxCreateAPPOrder(tradesId,tradesFee,siteId,null,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("微信预生成订单失败");
        }
        if(map == null)
            return "";
        return (String) map.get("prepay_id");
    }

    /**
     * 获取客户端真实IP地址
     * @param request
     * @return
     */
    public  String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 微信刷卡支付
     * @param tradesId
     * @param tradesFee
     * @param tradesName
     * @param authCode
     * @return
     */
    public String wxPay(Integer siteId, String tradesId, int tradesFee, String tradesName, String authCode) throws InterruptedException, PayException {
        WxRequestParam wxRequestParam = new WxRequestParam();
//        WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(siteId);
//        if(wxPublicConfig == null)
//            throw new PayException("siteId找不到适合的公众号");
        //防止客户已经支付(查询之前的订单是否微信已经支付)
        /*String oldtradesId=stringRedisTemplate.opsForValue().get(tradesId.split("_")[0]+"_wxOrder");
        log.info("--------------------------------------oldtradesId"+oldtradesId);
        if(!StringUtil.isEmpty(oldtradesId)){
            Map oldqueryResult = this.wxQueryOrder(siteId, oldtradesId, null);
            if(oldqueryResult != null && "SUCCESS".equals(oldqueryResult.get("trade_state"))
                    && tradesFee == Integer.parseInt((String) oldqueryResult.get("total_fee"))) {
                log.info("--------------------------------------yizhifu"+oldtradesId);
                return (String)oldqueryResult.get("transaction_id");
            }
        }*/
        WxConfig wxConfig=wxPayApi.toConfig(tradesId+"");
        stringRedisTemplate.opsForValue().set(tradesId.split("_")[0]+"_wxOrder",tradesId);
        payLogsService.addByTrades(Long.parseLong(tradesId.split("_")[0]),PayConstant.PAY_STYLE_WX_MICROPAY,PayConstant.PAY_STATUS_FAIL);
        wxRequestParam.setAppid(wxConfigfw.getAppid());
        wxRequestParam.setSub_appid(wxConfig.getAppid());
        wxRequestParam.setOut_trade_no(tradesId);
        wxRequestParam.setTotal_fee(tradesFee);
        wxRequestParam.setBody(tradesName);
        wxRequestParam.setAuth_code(authCode);
        Map map = null;
        String xml = null;
        try {
           /* //防止客户已经支付(查询之前的订单是否微信没有支付直接取消)
            if(!StringUtil.isEmpty(oldtradesId)){
                log.info("--------------------------------------quxiao"+oldtradesId);
               // this.wxReverse(siteId, oldtradesId, null);
                this.wxClose(siteId, oldtradesId);
            }*/

            xml = wxPayApi.microPay(wxRequestParam);
            map = XmlUtils.xml2map(xml);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code"))) {
                payInterfaceLogService.insertWxPay(Long.parseLong(tradesId.split("_")[0]),tradesFee,siteId,(String)map.get("transaction_id"),xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS,tradesId);
                payLogsService.updateByTrades(Long.parseLong(tradesId.split("_")[0]),(String)map.get("transaction_id"), PayConstant.PAY_STYLE_WX_MICROPAY, PayConstant.PAY_STATUS_SUCCESS, new Date(),
                        xml, wxConfig.getMch_id(), (String) map.get("openid"));

                return (String)map.get("transaction_id");
            } else if("USERPAYING".equals(map.get("err_code"))) {
                Thread.sleep(5000);
                for (int i = 0; i < 3; ++i) {
                    Map queryResult = this.wxQueryOrder(siteId, tradesId, null);
                    if(queryResult != null && "SUCCESS".equals(queryResult.get("trade_state"))
                            && tradesFee == Integer.parseInt((String) queryResult.get("total_fee"))) {
                        payInterfaceLogService.insertWxPay(Long.parseLong(tradesId.split("_")[0]),tradesFee,siteId,(String)queryResult.get("transaction_id"),xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS,tradesId);
                        payLogsService.updateByTrades(Long.parseLong(tradesId.split("_")[0]),(String)map.get("transaction_id"), PayConstant.PAY_STYLE_WX_MICROPAY, PayConstant.PAY_STATUS_SUCCESS, new Date(),
                                xml, wxConfig.getMch_id(), (String) map.get("openid"));
                        return (String)map.get("transaction_id");
                    } else if(queryResult != null && ("USERPAYING").equals(queryResult.get("trade_state"))) {
                        Thread.sleep(10000);
                    } else {
                        Thread.sleep(5000);
                    }
                }
            }
            payInterfaceLogService.insertWxPay(Long.parseLong(tradesId.split("_")[0]),tradesFee,siteId,map==null?null:(String)map.get("transaction_id"),xml,PayConstant.PAY_INTERFACE_EXE_FAIL,tradesId);
            this.wxReverse(siteId, tradesId, null);
            payLogsService.updateByTrades(Long.parseLong(tradesId.split("_")[0]), PayConstant.PAY_STATUS_PAY_ERROR);
            return null;
        } catch (Exception e) {
            log.error("Exception", e);
            payLogsService.updateByTrades(Long.parseLong(tradesId.split("_")[0]), PayConstant.PAY_STATUS_PAY_ERROR);
            payInterfaceLogService.insertWxPay(Long.parseLong(tradesId.split("_")[0]),tradesFee,siteId,map==null?null:(String)map.get("transaction_id"),xml,PayConstant.PAY_INTERFACE_EXE_ERROR,tradesId);
            throw new PayException("微信支付失败");
        }

    }

    /**
     * 微信订单查询
     * @param tradesId
     * @param transactionId
     * @return
     */
    public Map wxQueryOrder(Integer siteId, String tradesId, String transactionId) throws PayException {
        Map map =  wxQueryOrderOld(siteId,tradesId,transactionId);
        if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("trade_state"))) {
            return map;
        }
        List<PayInterfaceLog> list= payInterfaceLogMapper.findByTradesIdquery(tradesId.split("_")[0]);
        for (PayInterfaceLog payInterfaceLog: list) {
            map=  wxQueryOrderOld(siteId,payInterfaceLog.getTradesIdTime(),transactionId);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("trade_state"))) {
                return map;
            }
        }
        return map;
    }
    /**
     * 微信订单查询
     * @param tradesId
     * @param transactionId
     * @return
     */
    public Map wxQueryOrderOld(Integer siteId, String tradesId, String transactionId) throws PayException {
        WxRequestParam wxRequestParam = new WxRequestParam();
//        WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(siteId);
//        if(wxPublicConfig == null)
//            throw new PayException("siteId找不到适合的公众号");
        WxConfig wxConfig=wxPayApi.toConfig(tradesId);
        wxRequestParam.setAppid(wxConfigfw.getAppid());
        wxRequestParam.setSub_appid(wxConfig.getAppid());
        wxRequestParam.setOut_trade_no(tradesId);
        wxRequestParam.setTransaction_id(transactionId);
        Map map = null;
        try {
            String xml = wxPayApi.orderQuery(wxRequestParam);
            map = XmlUtils.xml2map(xml);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("trade_state"))) {
                payInterfaceLogService.insertWxQueryOrder(Long.parseLong(tradesId.split("_")[0]),siteId,(String)map.get("transaction_id"),xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
                Trades trades = tradesService.getTradesByTradesId(Long.parseLong(tradesId.split("_")[0]));
                trades.setPayNumber((String)map.get("transaction_id"));
                trades.setPayStyle(PayConstant.PAY_STYLE_WX);
                trades.setCashierId(trades.getStoreUserId());
                Integer tradeTypePayLine=100;
                if (Objects.equals("NATIVE", map.get("trade_type"))) {
                    tradeTypePayLine=110;
                } else if (Objects.equals("JSAPI", map.get("trade_type"))) {
                    tradeTypePayLine=100;
                }else if (Objects.equals("APP",map.get("trade_type"))){
                    tradeTypePayLine=130;
                }else if (Objects.equals("MICROPAY",map.get("trade_type"))){
                    tradeTypePayLine=120;
                }
                trades.setTradeTypePayLine(tradeTypePayLine);
                if(!tradesService.paySuccessCallback(trades)) {
                    log.error("订单号为：{},无法执行付款操作", trades.getTradesId());
                }
                return map;
            } else{
                payInterfaceLogService.insertWxQueryOrder(Long.parseLong(tradesId.split("_")[0]),siteId,null,xml,PayConstant.PAY_INTERFACE_EXE_FAIL);
            }

        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLogService.insertWxQueryOrder(Long.parseLong(tradesId.split("_")[0]),siteId,(String)map.get("transaction_id"),null,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("微信订单查询失败");
        }
        return map;
    }
    /**
     * 微信安全退款
     * @param siteId
     * @param tradesId
     * @param tradesFee
     * @param refundFee
     * @param outRefundNo
     * @return
     * @throws PayException
     */
    public String wxRefund(Integer siteId, long tradesId, int tradesFee, int refundFee, long outRefundNo) throws  PayException{
        PayInterfaceLog payInterfaceLog = payInterfaceLogService.findByTradesId(Long.toString(tradesId),PayConstant.PAY_STYLE_WX, PayConstant.PAY_INTERFACE_WX_RF,siteId);
        if(payInterfaceLog != null)
            return payInterfaceLog.getRefundId();
        payInterfaceLog = payInterfaceLogService.findByTradesId(Long.toString(tradesId),PayConstant.PAY_STYLE_WX_NATIVE, PayConstant.PAY_INTERFACE_WX_CB,siteId);
        if(payInterfaceLog == null)
            payInterfaceLog = payInterfaceLogService.findByTradesId(Long.toString(tradesId),PayConstant.PAY_STYLE_WX_JSAPI, PayConstant.PAY_INTERFACE_WX_CB,siteId);
        if(payInterfaceLog == null)
            payInterfaceLog = payInterfaceLogService.findByTradesId(Long.toString(tradesId),PayConstant.PAY_STYLE_WX_MICROPAY, PayConstant.PAY_INTERFACE_WX_PAY,siteId);
        if(payInterfaceLog == null)
            payInterfaceLog = payInterfaceLogService.findByTradesId(Long.toString(tradesId),PayConstant.PAY_STYLE_WX_APP, PayConstant.PAY_INTERFACE_WX_CB,siteId);
        if(payInterfaceLog == null)
            payInterfaceLog = payInterfaceLogService.findByTradesId(Long.toString(tradesId),PayConstant.PAY_INTERFACE_WX_CB, PayConstant.PAY_INTERFACE_WX_CB,siteId);

        if(payInterfaceLog == null) {
//            throw new PayException("找不到退款订单");
            // 可能是51健康账号的
            return this.wxRefund(siteId, tradesId, "", tradesFee, refundFee, outRefundNo);
        }
        if ((StringUtil.isNotEmpty(payInterfaceLog.getPayStyle()) && payInterfaceLog.getPayStyle().contains("wx_app"))) {
            return this.wxAppRefund(siteId, payInterfaceLog.getTradesId(), payInterfaceLog.getTransactionId(),tradesFee,refundFee,outRefundNo);
        }else{
            return this.wxRefund(siteId, payInterfaceLog.getTradesId(), payInterfaceLog.getTransactionId(),tradesFee,refundFee,outRefundNo);
        }

    }

    /**
     * 微信退款
     * @param tradesId
     * @param transactionId
     * @param tradesFee
     * @param refundFee
     * @param outRefundNo
     * @return
     */
    public String wxRefund(Integer siteId, Long tradesId, String transactionId, int tradesFee, int refundFee, long outRefundNo) throws PayException {
        WxRequestParam wxRequestParam = new WxRequestParam();
//        WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(siteId);
//        if(wxPublicConfig == null)
//            throw new PayException("siteId找不到适合的公众号");
        WxConfig wxConfig=wxPayApi.toConfig(wxRequestParam.getOut_trade_no());
        wxRequestParam.setAppid(wxConfigfw.getAppid());
        wxRequestParam.setSub_appid(wxConfig.getAppid());
        wxRequestParam.setOut_trade_no(tradesId.toString());
        wxRequestParam.setTransaction_id(transactionId);
        wxRequestParam.setTotal_fee(tradesFee);
        wxRequestParam.setRefund_fee(refundFee);
        wxRequestParam.setOut_refund_no(Long.toString(outRefundNo));
        Map map = null;
        try {
            String xml;
            // region 临时处理多个账号的退款问题 这个判断可以在8月份删除
            if (is51jkTrades(tradesId, transactionId)) {
                wxRequestParam.setAppid(wxConfig51jk.getAppid());
                xml = wxPayApi.refund51jk(wxRequestParam);
            } else {
                xml = wxPayApi.refund(wxRequestParam);
            }
            // endregion
//            String xml = wxPayApi.refund(wxRequestParam);
            map = XmlUtils.xml2map(xml);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code"))) {
                payInterfaceLogService.insertWxRefund(tradesId,tradesFee,outRefundNo,refundFee,siteId,transactionId,(String) map.get("refund_id"),xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
                String refund_fee = (String)map.get("refund_fee");
                if(refundFee == Integer.parseInt(refund_fee)) {
                    payLogsService.updateByTrades(tradesId, PayConstant.PAY_STATUS_REFUND);
                    return (String) map.get("refund_id");
                }
            } else {
                payInterfaceLogService.insertWxRefund(tradesId,tradesFee,outRefundNo,refundFee,siteId,transactionId,null,xml,PayConstant.PAY_INTERFACE_EXE_FAIL);
            }
            payLogsService.updateByTrades(tradesId, PayConstant.PAY_STATUS_REFUND_ERROR);
            return null;
        } catch (Exception e) {

            log.error("Exception", e);
            payInterfaceLogService.insertWxRefund(tradesId,tradesFee,outRefundNo,refundFee,siteId,transactionId,null,null,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("微信退款失败");
        }
    }



    /**
     * App微信退款
     * @param tradesId
     * @param transactionId
     * @param tradesFee
     * @param refundFee
     * @param outRefundNo
     * @return
     */
    public String wxAppRefund(Integer siteId, Long tradesId, String transactionId, int tradesFee, int refundFee, long outRefundNo) throws PayException {
        WxRequestParam wxRequestParam = new WxRequestParam();
        wxRequestParam.setAppid(wxAppConfig.getAppid());
        if(StringUtil.isEmpty(transactionId) && tradesId != null) {
            wxRequestParam.setOut_trade_no(tradesId.toString());
        } else {
            wxRequestParam.setTransaction_id(transactionId);
        }
        wxRequestParam.setTotal_fee(tradesFee);
        wxRequestParam.setRefund_fee(refundFee);
        wxRequestParam.setOut_refund_no(Long.toString(outRefundNo));
        Map map = null;
        try {
            String xml;
            xml = wxPayApi.refundApp(wxRequestParam);
            map = XmlUtils.xml2map(xml);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code"))) {
                payInterfaceLogService.insertWxRefund(tradesId,tradesFee,outRefundNo,refundFee,siteId,transactionId,(String) map.get("refund_id"),xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
                String refund_fee = (String)map.get("refund_fee");
                if(refundFee == Integer.parseInt(refund_fee)) {
                    payLogsService.updateByTrades(tradesId, PayConstant.PAY_STATUS_REFUND);
                    return (String) map.get("refund_id");
                }
            } else {
                payInterfaceLogService.insertWxRefund(tradesId,tradesFee,outRefundNo,refundFee,siteId,transactionId,null,xml,PayConstant.PAY_INTERFACE_EXE_FAIL);
            }
            payLogsService.updateByTrades(tradesId, PayConstant.PAY_STATUS_REFUND_ERROR);
            return null;
        } catch (Exception e) {

            log.error("Exception", e);
            payInterfaceLogService.insertWxRefund(tradesId,tradesFee,outRefundNo,refundFee,siteId,transactionId,null,null,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("微信退款失败");
        }
    }

    public boolean is51jkTrades(Long tradesId, String transactionId) {
        WxRequestParam orderQueryParam = new WxRequestParam();
        WxConfig wxConfig=wxPayApi.toConfig(tradesId+"");
        orderQueryParam.setAppid(wxConfig.getAppid());
        if (StringUtils.isNotEmpty(transactionId)) {
            orderQueryParam.setTransaction_id(transactionId);
        } else {
            orderQueryParam.setOut_trade_no(String.valueOf(tradesId));
        }
        String xml;
        try {
            xml = wxPayApi.orderQuery(orderQueryParam);
            Map map = XmlUtils.xml2map(xml);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("trade_state"))) {
                return false;
            }
        } catch (PayException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 微信退款查询
     * @param outRefundNo
     * @return
     */
    public Map wxRefundQuery(Integer siteId, long outRefundNo) throws PayException {

        WxRequestParam wxRequestParam = new WxRequestParam();
//        WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(siteId);
//        if(wxPublicConfig == null)
//            throw new PayException("siteId找不到适合的公众号");
        WxConfig wxConfig=wxPayApi.toConfigSiteId(siteId);
        wxRequestParam.setAppid(wxConfigfw.getAppid());
        wxRequestParam.setSub_appid(wxConfig.getAppid());
        wxRequestParam.setOut_refund_no(Long.toString(outRefundNo));
        Map map = null;
        try {
            String xml = wxPayApi.refundQuery(wxRequestParam);
            map = XmlUtils.xml2map(xml);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code")))
                payInterfaceLogService.insertWxRefundQuery(outRefundNo,siteId,xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
            else
                payInterfaceLogService.insertWxRefundQuery(outRefundNo,siteId,xml,PayConstant.PAY_INTERFACE_EXE_FAIL);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLogService.insertWxRefundQuery(outRefundNo,siteId,null,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("微信退款订单查询失败");
        }
        return map;
    }

    /**
     * 微信撤销订单
     * @param tradesId
     * @param transactionId
     * @return
     */
    public Map wxReverse(Integer siteId, String tradesId, String transactionId) throws PayException {

        WxRequestParam wxRequestParam = new WxRequestParam();
//        WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(siteId);
//        if(wxPublicConfig == null)
//            throw new PayException("siteId找不到适合的公众号");
        WxConfig wxConfig=wxPayApi.toConfigSiteId(siteId);
        wxRequestParam.setAppid(wxConfigfw.getAppid());
        wxRequestParam.setSub_appid(wxConfig.getAppid());
        if(tradesId != null)
            wxRequestParam.setOut_trade_no(tradesId);
        wxRequestParam.setTransaction_id(transactionId);
        Map map = null;
        try {
            String xml = wxPayApi.reverse(wxRequestParam);
            map = XmlUtils.xml2map(xml);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code")))
                payInterfaceLogService.insertWxReverse(Long.parseLong(tradesId.split("_")[0]), transactionId, siteId, xml, PayConstant.PAY_INTERFACE_EXE_SUCCESS);
            else
                payInterfaceLogService.insertWxReverse(Long.parseLong(tradesId.split("_")[0]), transactionId, siteId, xml, PayConstant.PAY_INTERFACE_EXE_FAIL);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLogService.insertWxReverse(Long.parseLong(tradesId.split("_")[0]), transactionId, siteId, null, PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("微信订单撤销失败");
        }
        return map;
    }

    /**
     * 微信关闭订单
     * @param tradesId
     * @return
     */
    public Map wxClose(Integer siteId, String tradesId) throws PayException {
        WxConfig wxConfig=wxPayApi.toConfigSiteId(siteId);
        WxRequestParam wxRequestParam = new WxRequestParam();
//        WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(siteId);
//        if(wxPublicConfig == null)
//            throw new PayException("siteId找不到适合的公众号");
        List<PayInterfaceLog> list= payInterfaceLogMapper.findByTradesIdALL(tradesId.split("_")[0]);
        boolean flag=true;
        Map map = new HashedMap();
        for (PayInterfaceLog payInterfaceLog: list) {
            if(!StringUtil.isEmpty(payInterfaceLog.getTradesIdTime())){
                wxRequestParam.setAppid(wxConfigfw.getAppid());
                wxRequestParam.setSub_appid(wxConfig.getAppid());
                wxRequestParam.setOut_trade_no(payInterfaceLog.getTradesIdTime());

                try {
                    String xml = wxPayApi.closeOrder(wxRequestParam);
                    map = XmlUtils.xml2map(xml);
                    if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code"))){
                        payInterfaceLogMapper.updateByTradesId(payInterfaceLog.getId());
                        payInterfaceLogService.insertWxClose(Long.parseLong(tradesId.split("_")[0]),siteId,xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
                    }else{
                        payInterfaceLogService.insertWxClose(Long.parseLong(tradesId.split("_")[0]),siteId,xml,PayConstant.PAY_INTERFACE_EXE_FAIL);
                        flag=false;
                    }
                } catch (Exception e) {
                    log.error("Exception", e);
                    payInterfaceLogService.insertWxClose(Long.parseLong(tradesId.split("_")[0]),siteId,null,PayConstant.PAY_INTERFACE_EXE_ERROR);
                    throw new PayException("微信订单关闭失败");
                }
            }

        }
        map.put("flag",flag);
        return map;
    }
    /**
     * 微信关闭订单
     * @param tradesId
     * @return
     */
    public Map wxCloseNew(Integer siteId, String tradesId) throws PayException {
        WxConfig wxConfig=wxPayApi.toConfigSiteId(siteId);
        WxRequestParam wxRequestParam = new WxRequestParam();
//        WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(siteId);
//        if(wxPublicConfig == null)
//            throw new PayException("siteId找不到适合的公众号");
        List<PayInterfaceLog> list=new ArrayList<>();
        PayInterfaceLog payInterface=new PayInterfaceLog();
        payInterface.setTradesIdTime(tradesId.split("_")[0]);
        list.add(payInterface);
        list.addAll(payInterfaceLogMapper.findByTradesIdALL(tradesId.split("_")[0]));
        boolean flag=true;
        Map map = new HashedMap();
        for (PayInterfaceLog payInterfaceLog: list) {
            if(!StringUtil.isEmpty(payInterfaceLog.getTradesIdTime())){
                wxRequestParam.setAppid(wxConfigfw.getAppid());
                wxRequestParam.setSub_appid(wxConfig.getAppid());
                wxRequestParam.setOut_trade_no(payInterfaceLog.getTradesIdTime());

                try {
                    String xml = wxPayApi.closeOrder(wxRequestParam);
                    map = XmlUtils.xml2map(xml);
                    if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code"))){
                        payInterfaceLogMapper.updateByTradesId(payInterfaceLog.getId());
                        payInterfaceLogService.insertWxClose(Long.parseLong(tradesId.split("_")[0]),siteId,xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
                    }else{
                        payInterfaceLogService.insertWxClose(Long.parseLong(tradesId.split("_")[0]),siteId,xml,PayConstant.PAY_INTERFACE_EXE_FAIL);
                        flag=false;
                    }
                } catch (Exception e) {
                    log.error("Exception", e);
                    payInterfaceLogService.insertWxClose(Long.parseLong(tradesId.split("_")[0]),siteId,null,PayConstant.PAY_INTERFACE_EXE_ERROR);
                    throw new PayException("微信订单关闭失败");
                }
            }

        }
        map.put("flag",flag);
        return map;
    }



    @Cacheable(value = "weixinjsapi",keyGenerator = "defaultKeyGenerator")
    public String getOpenid(Integer siteId, String appid, String appsecret, String code) throws PayException {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_GOP);
        payInterfaceLog.setSiteId(siteId);
        String json = null;
        Map map = null;
        try {
            json = wxPayApi.getOpenId(appid,appsecret,code);
            map = JacksonUtils.json2map(json);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLog.setExeResult(PayConstant.PAY_INTERFACE_EXE_ERROR);
            payInterfaceLogService.insert(payInterfaceLog);
            throw new PayException("微信获取openid失败");
        }
        if (checkFail(payInterfaceLog, json, map)) return "";
        return (String) map.get("openid");
    }
    public String getOpenidXCX(Integer siteId, String appid, String appsecret, String code) throws PayException {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_GOP);
        payInterfaceLog.setSiteId(siteId);
        String json = null;
        Map map = null;
        try {
            json = wxPayApi.getOpenIdXCX(appid,appsecret,code);
            map = JacksonUtils.json2map(json);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLog.setExeResult(PayConstant.PAY_INTERFACE_EXE_ERROR);
            payInterfaceLogService.insert(payInterfaceLog);
            throw new PayException("微信获取openid失败");
        }
        if (checkFail(payInterfaceLog, json, map)) return "";
        return (String) map.get("openid");
    }


    @Cacheable(value = "weixinjsapi",keyGenerator = "defaultKeyGenerator")
    public String getAccessToken(String appid, String appsecret) throws PayException {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_GAT);
        payInterfaceLog.setSiteId(0);
        String json = null;
        Map map = null;
        try {
            json = wxPayApi.getAccessToken(appid,appsecret);
            map = JacksonUtils.json2map(json);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLog.setExeResult(PayConstant.PAY_INTERFACE_EXE_ERROR);
            payInterfaceLogService.insert(payInterfaceLog);
            throw new PayException("微信获取access_token失败");
        }
        if (checkFail(payInterfaceLog, json, map)) return "";
        String access_token = (String) map.get("access_token");
        return access_token;
    }

    @Cacheable(value = "weixinjsapi",keyGenerator = "defaultKeyGenerator")
    public String getJSAPITicket(String accessToken) throws PayException {
        PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
        payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX);
        payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_GTK);
        payInterfaceLog.setSiteId(0);
        String json = null;
        Map map = null;
        try {
            json = wxPayApi.getJSAPITicket(accessToken);
            map = JacksonUtils.json2map(json);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLog.setExeResult(PayConstant.PAY_INTERFACE_EXE_ERROR);
            payInterfaceLogService.insert(payInterfaceLog);
            throw new PayException("微信获取ticket失败");
        }
        if (checkFail(payInterfaceLog, json, map)) return "";
        String jsapi_ticket = (String)map.get("ticket");
        return jsapi_ticket;
    }

    private boolean checkFail(PayInterfaceLog payInterfaceLog, String json, Map map) {
        payInterfaceLog.setPayResult(json);
        if(map == null) {
            payInterfaceLog.setExeResult(PayConstant.PAY_INTERFACE_EXE_FAIL);
            payInterfaceLogService.insert(payInterfaceLog);
            return true;
        } else {
            payInterfaceLog.setExeResult(PayConstant.PAY_INTERFACE_EXE_SUCCESS);
            payInterfaceLogService.insert(payInterfaceLog);
        }
        return false;
    }

    @Cacheable(value = "weixinjsapi",keyGenerator = "defaultKeyGenerator")
    public String getJSAPITicket(String appid, String appsecret) throws PayException {
        String access_token = getAccessToken(appid, appsecret);
        return getJSAPITicket(access_token);
    }

    /**
     * 微信支付预下单：小程序支付
     * @param tradesId
     * @param tradesFee
     * @param tradesName
     * @param openid
     * @return
     * @throws PayException
     * @throws IOException
     */
    public String wxCreateXCXAPIOrder(Integer siteId, Long tradesId, int tradesFee, String tradesName, String openid) throws PayException {
        WxRequestParam wxRequestParam = new WxRequestParam();
        payLogsService.addByTrades(tradesId,PayConstant.PAY_STYLE_WX_JSAPI,PayConstant.PAY_STATUS_FAIL);
        //微信分配的小程序ID
        wxRequestParam.setAppid(wxXcxConfig.getXcxappid());
        wxRequestParam.setOut_trade_no(tradesId.toString());
        wxRequestParam.setTotal_fee(tradesFee);
        wxRequestParam.setBody(tradesName);
        wxRequestParam.setTrade_type("JSAPI");
        wxRequestParam.setOpenid(openid);
        Map map = null;
        try {
            String xml = wxPayApi.unifiedOrderXCX(wxRequestParam);
            log.info("wxCreateXCXAPIOrder-------xml:{}",xml);
            map = XmlUtils.xml2map(xml);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code")))
                payInterfaceLogService.insertWxCreateJSAPIOrder(tradesId,tradesFee,siteId,xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
            else
                payInterfaceLogService.insertWxCreateJSAPIOrder(tradesId,tradesFee,siteId,xml,PayConstant.PAY_INTERFACE_EXE_FAIL);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLogService.insertWxCreateJSAPIOrder(tradesId,tradesFee,siteId,null,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("微信预生成订单失败");
        }
        if(map == null)
            return "";
        return (String) map.get("prepay_id");
    }

    /**
     * 微信支付预下单：H5支付
     * @param tradesId
     * @param tradesFee
     * @param tradesName
     * @return
     * @throws PayException
     * @throws IOException
     */
    public String wxCreateH5Order(Integer siteId,String trades_id, Long tradesId, int tradesFee, String tradesName, String spbill_create_ip) throws PayException {
        //防止客户已经支付(查询之前的订单是否微信已经支付)
        /*String oldtradesId=stringRedisTemplate.opsForValue().get(tradesId+"_wxOrder");
        log.info("wxCreateJSAPIOrder--------------------------------------oldtradesId"+oldtradesId);
        if(!StringUtil.isEmpty(oldtradesId)){
            this.wxClose(siteId,oldtradesId);
        }*/
        WxConfig wxConfig=wxPayApi.toConfigSiteId(siteId);
        stringRedisTemplate.opsForValue().set(tradesId+"_wxOrder",trades_id);

        WxRequestParam wxRequestParam = new WxRequestParam();
//        WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(siteId);
//        if(wxPublicConfig == null)
//            throw new PayException("siteId找不到适合的公众号");
        payLogsService.addByTrades(tradesId,PayConstant.PAY_STYLE_WX_JSAPI,PayConstant.PAY_STATUS_FAIL);
        wxRequestParam.setAppid(wxConfigfw.getAppid());
        wxRequestParam.setSub_appid(wxConfig.getAppid());
        wxRequestParam.setOut_trade_no(trades_id);
        wxRequestParam.setTotal_fee(tradesFee);
        wxRequestParam.setBody(tradesName);
        wxRequestParam.setTrade_type("MWEB");
        wxRequestParam.setScene_info("{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"http://www.51jk.com/\",\"wap_name\": \"51健康\"}}");
        wxRequestParam.setSpbill_create_ip(spbill_create_ip);
        Map map = null;
        try {
            String xml = wxPayApi.unifiedOrder(wxRequestParam);
            map = XmlUtils.xml2map(xml);
            if("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code")))
                payInterfaceLogService.insertWxCreateJSAPIOrder(tradesId,tradesFee,siteId,xml,PayConstant.PAY_INTERFACE_EXE_SUCCESS);
            else
                payInterfaceLogService.insertWxCreateJSAPIOrder(tradesId,tradesFee,siteId,xml,PayConstant.PAY_INTERFACE_EXE_FAIL);
        } catch (Exception e) {
            log.error("Exception", e);
            payInterfaceLogService.insertWxCreateJSAPIOrder(tradesId,tradesFee,siteId,null,PayConstant.PAY_INTERFACE_EXE_ERROR);
            throw new PayException("微信预生成订单失败");
        }
        if(map == null)
            return "";
        return (String) map.get("mweb_url");
    }
    public Map sendredpack(String tradesId, int tradesFee, String tradesName, String re_openid) throws InterruptedException, PayException {
        WxRedpackRequestParam wxRequestParam = new WxRedpackRequestParam();
        WxConfig wxConfig=wxPayApi.toConfig(tradesId);
        wxRequestParam.setWxappid(wxConfigfw.getAppid());
        wxRequestParam.setMsgappid(wxConfig.getAppid());
        wxRequestParam.setMch_id(wxConfigfw.getMch_id());
        wxRequestParam.setSub_mch_id(wxConfig.getMch_id());
        wxRequestParam.setConsume_mch_id(wxConfig.getMch_id());
        wxRequestParam.setTotal_num(1);
        wxRequestParam.setRe_openid(re_openid);
        wxRequestParam.setMch_billno(tradesId);
        wxRequestParam.setTotal_amount(tradesFee);
        wxRequestParam.setSend_name(tradesName);
        wxRequestParam.setRemark("百万红包，快来抢！");
        wxRequestParam.setAct_name("凭小票，领红包！");
        wxRequestParam.setClient_ip(wxConfigfw.getSpbill_create_ip());
        wxRequestParam.setWishing("感谢您参与，祝您身体健康！");
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        Map map = null;
        String xml = null;
        try {
            xml = wxPayApi.sendredpack(wxRequestParam);
            map = XmlUtils.xml2map(xml);
        } catch (Exception e) {
            throw new PayException("微信发红包失败");
        }
        return map;
    }
}
