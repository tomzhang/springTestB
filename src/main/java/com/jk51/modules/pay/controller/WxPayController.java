package com.jk51.modules.pay.controller;

import com.jk51.commons.CommonConstant;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.commons.xml.XmlUtils;
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
import com.jk51.modules.pay.service.ali.AliPayApi;
import com.jk51.modules.pay.service.ali.AliPayApifw;
import com.jk51.modules.pay.service.ali.request.AliRequestParam;
import com.jk51.modules.pay.service.wx.WxAppConfig;
import com.jk51.modules.pay.service.wx.WxConfig;
import com.jk51.modules.pay.service.wx.WxPayApi;
import com.jk51.modules.pay.service.wx.request.WxXcxConfig;
import com.jk51.modules.trades.service.TradesService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@RestController
@RequestMapping("/pay/wx")
public class WxPayController {

    private static final Logger log = LoggerFactory.getLogger(WxPayController.class);

    private static final String SYSTEM_FAIL_XML =  "<xml>\n" +
            "  <return_code><![CDATA[FAIL]]></return_code>\n" +
            "  <return_msg><![CDATA[SYSERR]]></return_msg>\n" +
            "</xml>";

    private static final String SIGN_FAIL_XML =  "<xml>\n" +
            "  <return_code><![CDATA[FAIL]]></return_code>\n" +
            "  <return_msg><![CDATA[签名失败]]></return_msg>\n" +
            "</xml>";

    private static final String SUCCESS_XML =  "<xml>\n" +
            "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
            "  <return_msg><![CDATA[OK]]></return_msg>\n" +
            "</xml>";

    @Autowired
    WxAppConfig wxAppConfig;
    @Autowired
    PayInterfaceLogService payInterfaceLogService;
    @Autowired
    PayService payService;
    @Autowired
    WxPayApi wxPayApi;
    @Autowired
    TradesService tradesService;
    @Autowired
    WxXcxConfig wxXcxConfig;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    PayLogsService payLogsService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    AliPayApi aliPayApi;
    @Autowired
    AliPayApifw aliPayApifw;
    @Autowired
    PayfwService payfwService;
    @Autowired
    private OrderService orderService;
    @Autowired
    WxConfig wxConfig;
    /**
     * 微信回调
     * @param xml
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/callback",method = RequestMethod.POST)
    @ResponseBody
    public String weixinPayCallback(@RequestBody String xml) {
        log.debug("微信支付回调 接收到xml {} ", xml);
        Map map;
        try {
            map = XmlUtils.xml2map(xml);
            Objects.requireNonNull(map);
        } catch (Exception e) {
            log.error("微信支付回调xml解析错误 {}, {}", xml, e);
            return SYSTEM_FAIL_XML;
        }
        String out_trade_no = Objects.toString(map.get("out_trade_no"), "");
        if (!wxPayApi.checkSign(map,out_trade_no)) {
            log.warn("微信支付回调签名验证失败 {}", xml);
            return SIGN_FAIL_XML;
        }


        String out_trade_no_rd = Objects.toString(map.get("out_trade_no"), "");
        String trade_type = Objects.toString(map.get("trade_type"), "");
        out_trade_no = out_trade_no.split("_")[0];
        long tradesId = NumberUtils.toLong(out_trade_no);
        if (tradesId == 0) {
            return SYSTEM_FAIL_XML;
        }

        //执行业务操作
        try {
            Trades trades = tradesService.getTradesByTradesId(tradesId);
            Objects.requireNonNull(trades, "订单不存在" + tradesId);
            int totalFee = NumberUtils.toInt(Objects.toString(map.get("total_fee")));
            Integer realPay = trades.getRealPay();
            Objects.requireNonNull(realPay, "订单实付款为空");
            if(realPay.intValue() != totalFee) {
                log.warn("微信支付回调签名支付金额失败 {} {} {}", tradesId, realPay, totalFee);
                return SYSTEM_FAIL_XML;
            }

            //回调成功后取消支付宝和微信生成的订单
            AliRequestParam param = new AliRequestParam();
            param.setOut_trade_no(trades.getTradesId().toString());
            if(trades.getIsServiceOrder()==0){
                aliPayApi.close(param);
                payService.wxClose(trades.getSiteId(), Objects.toString(map.get("out_trade_no"), ""));
            }else {
                aliPayApifw.close(param);
                payfwService.wxClose(trades.getSiteId(), Objects.toString(map.get("out_trade_no"), ""));
            }


            trades.setPayNumber((String) map.get("transaction_id"));
            log.info("微信支付回调 {} 支付方式 {}", tradesId, trades.getPayStyle());
            if (!StringUtils.equals(trades.getPayStyle(), CommonConstant.TRADES_PAY_TYPE_INTEGRAL)) {
                // 积分兑换需要支付运费 不修改成微信支付
                trades.setPayStyle(PayConstant.PAY_STYLE_WX);
            }
            trades.setCashierId(trades.getStoreUserId());
            Integer tradeTypePayLine=100;
            if (Objects.equals("NATIVE", trade_type)) {
                tradeTypePayLine=110;
            } else if (Objects.equals("JSAPI", trade_type)) {
                tradeTypePayLine=100;
            }else if (Objects.equals("APP",trade_type)){
                tradeTypePayLine=130;
            }
            trades.setTradeTypePayLine(tradeTypePayLine);
            // paySuccessCallback 里面更新订单状态
            if(!tradesService.paySuccessCallback(trades)) {
                log.error("订单号为：{},无法执行付款操作", trades.getTradesId());
            }
            stringRedisTemplate.opsForValue().set(out_trade_no+"_wxOrder",out_trade_no_rd);
            //根据订单获取商户的支付账户
            WxConfig wxConfig=wxPayApi.toConfig(out_trade_no);
            String mch_id=wxConfig.getMch_id();//商户号
            // region 支付日志相关
            PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
            if (Objects.equals("NATIVE", trade_type)) {
                payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX_NATIVE);
            } else if (Objects.equals("JSAPI", trade_type)) {
                payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX_JSAPI);
            }else if (Objects.equals("APP",trade_type)){
                payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX_APP);
                mch_id=wxAppConfig.getMch_id();
            } else {
                payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_WX);
            }


            payInterfaceLog.setTradesId(tradesId);
            payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_WX_CB);
            payInterfaceLog.setExeResult(PayConstant.PAY_INTERFACE_EXE_SUCCESS);
            payInterfaceLog.setTransactionId((String) map.get("transaction_id"));
            payInterfaceLog.setPayResult(xml);
            payInterfaceLog.setSiteId(trades.getSiteId());
            payInterfaceLogService.insert(payInterfaceLog);
            payLogsService.updateByTrades(trades.getTradesId(), (String) map.get("transaction_id"),
                    payInterfaceLog.getPayStyle(),
                    PayConstant.PAY_STATUS_SUCCESS,
                    new Date(),
                    xml,
                    mch_id,
                    (String) map.get("openid"));
            // endregion

            return SUCCESS_XML;
        } catch (Exception e) {
            log.error("支付成功，更改订单状态失败，订单号:" + out_trade_no, e);
        }

        return SYSTEM_FAIL_XML;
    }
    /**
     * 微信回调
     * @param xml
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/balanceCallback",method = RequestMethod.POST)
    @ResponseBody
    public String wxBalanceCallback(@RequestBody String xml) {
        log.debug("微信支付回调 接收到xml {} ", xml);
        Map map;
        try {
            map = XmlUtils.xml2map(xml);
            Objects.requireNonNull(map);
        } catch (Exception e) {
            log.error("微信支付回调xml解析错误 {}, {}", xml, e);
            return SYSTEM_FAIL_XML;
        }
        String out_trade_no = Objects.toString(map.get("out_trade_no"), "");
        if (!wxPayApi.checkSign(map,out_trade_no)) {
            log.warn("微信支付回调签名验证失败 {}", xml);
            return SIGN_FAIL_XML;
        }


        String out_trade_no_rd = Objects.toString(map.get("out_trade_no"), "");
        String trade_type = Objects.toString(map.get("trade_type"), "");
        out_trade_no = out_trade_no.split("_")[0];
        long tradesId = NumberUtils.toLong(out_trade_no);
        if (tradesId == 0) {
            return SYSTEM_FAIL_XML;
        }

        //执行业务操作
        try {
            String thirdSerialNum=map.get("transaction_id").toString();
            Integer siteId=Integer.parseInt(out_trade_no.substring(0,6));
            int totalFee = NumberUtils.toInt(Objects.toString(map.get("total_fee")));
            PayInterfaceLog payInterfaceLog = new PayInterfaceLog();
            payInterfaceLog.setTradesId(Long.parseLong(out_trade_no));
            payInterfaceLog.setTransactionId((String) map.get("trade_no"));
            payInterfaceLog.setPayResult(JacksonUtils.mapToJson(map));
            payInterfaceLog.setExeResult(PayConstant.PAY_INTERFACE_EXE_SUCCESS);
            payInterfaceLog.setPayStyle(PayConstant.PAY_STYLE_ALI);
            payInterfaceLog.setPayInterface(PayConstant.PAY_INTERFACE_ALI_CB);
            payInterfaceLog.setSiteId(siteId);
            payInterfaceLog.setTradesFee(totalFee);
            if(balanceService.updBalanceCallback(siteId,out_trade_no,7, thirdSerialNum)!=1) {
                log.error("充值流水号为：{},无法执行付款操作", out_trade_no);
            }
            return SUCCESS_XML;
        } catch (Exception e) {
            log.error("支付成功，更改充值流水状态失败，充值流水号:" + out_trade_no, e);
        }

        return SYSTEM_FAIL_XML;
    }
    /**
     * 获取授权字符串
     * @return
     */
//    @RequestMapping("/getmpveriry")
//    public ReturnDto getMpVerify(String site_id) {
//        if(StringUtil.isEmpty(site_id))
//            return ReturnDto.buildFailedReturnDto("site_id不能为空");
//        WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(Integer.parseInt(site_id));
//        return ReturnDto.buildSuccessReturnDto(wxPublicConfig.getMpVerify());
//    }


    /**
     * 生成扫码订单
     * @return
     */
    @RequestMapping("/createorder")
    public ReturnDto createOrder(HttpServletRequest request) {
        String trades_id = request.getParameter("trades_id");
        if(StringUtil.isEmpty(trades_id))
            return ReturnDto.buildFailedReturnDto("trades_id不能为空");
//        if(StringUtil.isEmpty(site_id))
//            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        Trades trades = tradesService.getTradesByTradesId(Long.parseLong(trades_id));
        if(trades == null) {
            return ReturnDto.buildFailedReturnDto("订单不存在");
        }
        if(trades.getIsPayment() == 1) {
            return ReturnDto.buildFailedReturnDto("订单已经支付");
        }
        try {
            String tradesName=orderService.findTradesName(trades.getSiteId(),trades.getTradesId());
            trades_id += DateUtils.formatDate("_yyddHHmmss");
            String fw=balanceService.boolIsProvider(trades.getSiteId());
            String code_url ="";
            if("NO".equals(fw)||trades.getIsServiceOrder()==0){
                code_url = payService.wxCreateNativeOrder(trades.getSiteId(), trades_id,trades.getRealPay(),tradesName);
            }else {
                code_url = payfwService.wxCreateNativeOrder(trades.getSiteId(), trades_id,trades.getRealPay(),tradesName);
            }
            if(StringUtil.isEmpty(code_url)){
                return ReturnDto.buildSystemErrorReturnDto();
            }
            Map<String, Object> data = new HashMap<String,Object>();
            data.put("code_url",code_url);
            data.put("trades_id",trades.getTradesId().toString());
            data.put("order_price", new DecimalFormat("######0.00").format(trades.getRealPay()/100f));
            return ReturnDto.buildSuccessReturnDto(data);
        } catch (Exception e) {
            log.error("Exception", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }
    @RequestMapping("/refund")
    public ReturnDto refund(String trades_id, String transactionId) {
        if(StringUtil.isEmpty(trades_id))
            return ReturnDto.buildFailedReturnDto("trades_id不能为空");
//        if(StringUtil.isEmpty(site_id))
//            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        try {

            Trades trades = tradesService.getTradesByTradesId(Long.parseLong(trades_id));
            if(trades == null) {
                return ReturnDto.buildFailedReturnDto("订单不存在");
            }
            Map result = payService.wxRefund(trades.getSiteId(),
                    Long.parseLong(trades_id),transactionId,trades.getRealPay(),trades.getRealPay(),System.currentTimeMillis(),trades.getIsServiceOrder());
            if(result != null)
                return ReturnDto.buildSuccessReturnDto("退款成功");
            return ReturnDto.buildFailedReturnDto("退款失败");
        }  catch (Exception e) {
            log.error(e.getMessage(), e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    @RequestMapping("/orderquery")
    public String orderquery(String site_id, String orderid) {
        try {
            Map map = payService.wxQueryOrder(Integer.parseInt(site_id), orderid, null);
            return "result:" + JacksonUtils.mapToJson(map);
        } catch (Exception e) {
            log.error("Exception", e);
            return "error";
        }
    }

    @RequestMapping("/refundquery")
    public String refundquery(String site_id, String out_refund_no) {
        try {
            Map map = payService.wxRefundQuery(Integer.parseInt(site_id), Long.parseLong(out_refund_no));
            return "result:" + JacksonUtils.mapToJson(map);
        } catch (Exception e) {
            log.error("Exception", e);
            return "error";
        }
    }

    @RequestMapping("/micropay")
    public ReturnDto micropay(String auth_code,String trades_id) {
        if(StringUtil.isEmpty(trades_id))
            return ReturnDto.buildFailedReturnDto("trades_id不能为空");
        if(StringUtil.isEmpty(auth_code))
            return ReturnDto.buildFailedReturnDto("auth_code不能为空");
//        if(StringUtil.isEmpty(site_id))
//            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        Trades trades = tradesService.getTradesByTradesId(Long.parseLong(trades_id));
        if(trades == null) {
            return ReturnDto.buildFailedReturnDto("订单不存在");
        }
        try {
            String tradesName=orderService.findTradesName(trades.getSiteId(),trades.getTradesId());
            String fw=balanceService.boolIsProvider(trades.getSiteId());
            String result ="";
            if("NO".equals(fw)||trades.getIsServiceOrder()==0){
                result = payService.wxPay(trades.getSiteId(), trades.getTradesId() + DateUtils.formatDate("_yyddHHmmss") ,trades.getRealPay(),tradesName,auth_code);
            }else {
                result = payfwService.wxPay(trades.getSiteId(), trades.getTradesId() + DateUtils.formatDate("_yyddHHmmss") ,trades.getRealPay(),tradesName,auth_code);
            }
            if(result != null) {
                trades.setPayNumber(result);
                trades.setPayStyle(PayConstant.PAY_STYLE_WX);
                trades.setCashierId(trades.getStoreUserId());
                trades.setTradeTypePayLine(120);
                tradesService.paySuccessCallback(trades);
                return ReturnDto.buildSuccessReturnDto("支付成功");
            }
            return ReturnDto.buildFailedReturnDto("支付失败");
        } catch (Exception e) {
            log.error("Exception", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    @RequestMapping("/closeorder")
    public String closeorder(String site_id, String orderid) {
        try {
            Map map = payService.wxClose(Integer.parseInt(site_id), orderid);
            return "result:" + JacksonUtils.mapToJson(map);
        } catch (Exception e) {
            log.error("Exception", e);
            return "error";
        }
    }

    @RequestMapping("/reverse")
    public String reverse(String site_id, String orderid, String transaction_id) {
        try {
            Map map = payService.wxReverse(Integer.parseInt(site_id), orderid, transaction_id);
            return "result:" + JacksonUtils.mapToJson(map);
        } catch (Exception e) {
            log.error("Exception", e);
            return "error";
        }
    }
    @RequestMapping("/tojsapipage")
    public ModelAndView tojsapipage(String site_id, String code,String trades_id) {
        log.info("jsapi获取的code：" + code);
        ModelAndView mav = new ModelAndView();
        mav.addObject("code",code);
        mav.addObject("trades_id", trades_id);
        mav.setViewName("views/pay/jsapidemo");
        return mav;
    }

    @RequestMapping("/createjsapiorder")
    public ReturnDto createjsapiorder(String code, String trades_id) {
        if(StringUtil.isEmpty(code))
            return ReturnDto.buildFailedReturnDto("code不能为空");
        if(StringUtil.isEmpty(trades_id))
            return ReturnDto.buildFailedReturnDto("trades_id不能为空");
//        if(StringUtil.isEmpty(site_id))
//            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        try {
            long tradesId=Long.parseLong(trades_id);
            Trades trades = tradesService.getTradesByTradesId(tradesId);
            if(trades == null) {
                return ReturnDto.buildFailedReturnDto("订单不存在");
            }
            if(trades.getIsUpPrice()!=-1){
                trades_id += "_"+trades.getIsUpPrice();
            }
            String tradesName=orderService.findTradesName(trades.getSiteId(),trades.getTradesId());
            //trades_id += DateUtils.formatDate("_yyddHHmmss");
//            WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(Integer.parseInt(site_id));
            //根据订单获取商户的支付账户
            //WxConfig wxConfig=wxPayApi.toConfig(trades_id);
            String openid = payService.getOpenid(trades.getSiteId(), wxConfig.getAppid(), wxConfig.getAPPSECRET(), code);
            String prepay_id = payService.wxCreateJSAPIOrder(trades.getSiteId(),trades_id, tradesId,trades.getRealPay(),tradesName,openid);
            Map<String, String> map = new HashMap<>();
            map.put("appId", wxConfig.getAppid());
            map.put("timeStamp", Long.toString(System.currentTimeMillis()));
            map.put("nonceStr", StringUtil.getRandomChar(32));
            map.put("package", "prepay_id=" + prepay_id);
            map.put("signType", "MD5");
            wxPayApi.sign(map,wxConfig.getKey());
            return ReturnDto.buildSuccessReturnDto(map);
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return ReturnDto.buildSystemErrorReturnDto();
    }


   /* @RequestMapping("/createapporder")
    public ReturnDto createapporder (String trades_id, HttpServletRequest request){
        if ((StringUtil.isEmpty(trades_id))) {
            return ReturnDto.buildFailedReturnDto("trades_id不能为空");
        }
        try {
            Trades trades = tradesService.getTradesByTradesId(Long.parseLong(trades_id));
            if(trades == null) {
                return ReturnDto.buildFailedReturnDto("订单不存在");
            }
            String tradesName=orderService.findTradesName(trades.getSiteId(),trades.getTradesId());
//            WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(Integer.parseInt(site_id));
            //String openid = payService.getOpenid(trades.getSiteId(), wxConfig.getAppid(), wxConfig.getAPPSECRET(), code);
            String prepay_id = payService.wxCreateAPPOrder(trades.getSiteId(), Long.parseLong(trades_id),trades.getRealPay(),tradesName,request);
            Map<String, String> map = new HashMap<>();
            map.put("appid", wxAppConfig.getAppid());//wxConfig.getAppid()
            long  currentTimeMillis =System.currentTimeMillis();
            long seconds =currentTimeMillis/1000L;
            String timeStamp=String.valueOf(seconds).substring(0,10);
            map.put("timestamp", timeStamp);
            map.put("noncestr", StringUtil.getRandomChar(32));
            map.put("prepayid",  prepay_id);
            map.put("package","Sign=WXPay");
            map.put("partnerid",wxAppConfig.getMch_id());
            wxPayApi.appSign(map);
            return ReturnDto.buildSuccessReturnDto(map);
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return ReturnDto.buildSystemErrorReturnDto();
    }*/
   @RequestMapping("/createapporder")
   public ReturnDto createapporder (String trades_id, HttpServletRequest request){
       if ((StringUtil.isEmpty(trades_id))) {
           return ReturnDto.buildFailedReturnDto("trades_id不能为空");
       }
       try {
           Trades trades = tradesService.getTradesByTradesId(Long.parseLong(trades_id));
           if(trades == null) {
               return ReturnDto.buildFailedReturnDto("订单不存在");
           }
           String tradesName=orderService.findTradesName(trades.getSiteId(),trades.getTradesId());
//            WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(Integer.parseInt(site_id));
           //String openid = payService.getOpenid(trades.getSiteId(), wxConfig.getAppid(), wxConfig.getAPPSECRET(), code);
           String fw=balanceService.boolIsProvider(trades.getSiteId());
           String prepay_id ="";
           if("NO".equals(fw)||trades.getIsServiceOrder()==0){
               prepay_id = payService.wxCreateAPPOrder(trades.getSiteId(), Long.parseLong(trades_id),trades.getRealPay(),tradesName,request);
           }else {
               prepay_id = payfwService.wxCreateAPPOrder(trades.getSiteId(), Long.parseLong(trades_id),trades.getRealPay(),tradesName,request);
           }
           WxConfig wxConfig=wxPayApi.toConfig(trades_id);
           Map<String, String> map = new HashMap<>();
           map.put("appid", wxConfig.getAppid());//wxConfig.getAppid()
           long  currentTimeMillis =System.currentTimeMillis();
           long seconds =currentTimeMillis/1000L;
           String timeStamp=String.valueOf(seconds).substring(0,10);
           map.put("timestamp", timeStamp);
           map.put("noncestr", StringUtil.getRandomChar(32));
           map.put("prepayid",  prepay_id);
           map.put("package","Sign=WXPay");
           map.put("partnerid",wxConfig.getMch_id());
           wxPayApi.appSign(map);
           return ReturnDto.buildSuccessReturnDto(map);
       } catch (Exception e) {
           log.error("Exception", e);
       }
       return ReturnDto.buildSystemErrorReturnDto();
   }
    @RequestMapping("/getconfig")
    public ReturnDto getconfig(String url,Integer site_id) {
        /*if(StringUtil.isEmpty(site_id)) {
            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        }*/
        Map map = new HashMap();
        map.put("noncestr", StringUtil.getRandomChar(32));
        map.put("timestamp", System.currentTimeMillis());
        map.put("url", url);
        try {
//            WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(Integer.parseInt(site_id));
//            if(wxPublicConfig == null)
//                return ReturnDto.buildFailedReturnDto("找不到此商家config");
            //根据订单获取商户的支付账户
            //WxConfig wxConfig=wxPayApi.toConfigSiteId(site_id);
            String jsapi_ticket = payService.getJSAPITicket(wxConfig.getAppid(),wxConfig.getAPPSECRET());
            map.put("jsapi_ticket", jsapi_ticket);
            Set keySet = map.keySet();
            Object[] objs = keySet.toArray();
            Arrays.sort(objs);
            StringBuffer tempStr = new StringBuffer();
            for (int i = 0; i < objs.length; ++i) {
                tempStr.append(objs[i] + "=" + map.get(objs[i]));
                if(i != objs.length - 1) {
                    tempStr.append("&");
                }
            }
            String signature = EncryptUtils.encryptToSHA(tempStr.toString()).toLowerCase();
            map.put("signature", signature);
            map.put("appId", wxConfig.getAppid());
            return ReturnDto.buildSuccessReturnDto(map);
        } catch (Exception e) {
            log.error("Exception", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    @RequestMapping("/getappid")
    public ReturnDto getAppid(Integer site_id) {
       /* if(StringUtil.isEmpty(site_id)) {
            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        }*/
        Map map = new HashMap();
//        WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(Integer.parseInt(site_id));
//        if(wxPublicConfig == null || StringUtil.isEmpty(wxPublicConfig.getAppid())) {
//            return ReturnDto.buildFailedReturnDto("找不到此商户appid");
//        }
        //根据订单获取商户的支付账户
        //WxConfig wxConfig=wxPayApi.toConfigSiteId(site_id);
        map.put("appid",wxConfig.getAppid());
        map.put("timestamp", System.currentTimeMillis());
        return ReturnDto.buildSuccessReturnDto(map);
    }

    @RequestMapping("/wxCreateXCXAPIOrder")
    public ReturnDto wxCreateXCXAPIOrder(String code, String trades_id) {
        if(StringUtil.isEmpty(code))
            return ReturnDto.buildFailedReturnDto("code不能为空");
        if(StringUtil.isEmpty(trades_id))
            return ReturnDto.buildFailedReturnDto("trades_id不能为空");
//        if(StringUtil.isEmpty(site_id))
//            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        try {
            Trades trades = tradesService.getTradesByTradesId(Long.parseLong(trades_id));
            if(trades == null) {
                return ReturnDto.buildFailedReturnDto("订单不存在");
            }
            String tradesName=orderService.findTradesName(trades.getSiteId(),trades.getTradesId());
//            WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(Integer.parseInt(site_id));
            String openid = payService.getOpenid(trades.getSiteId(), wxXcxConfig.getXcxappid(), wxXcxConfig.getXcxappsecret(), code);
            String prepay_id = payService.wxCreateXCXAPIOrder(trades.getSiteId(), Long.parseLong(trades_id),trades.getRealPay(),tradesName,openid);
            Map<String, String> map = new HashMap<>();
            map.put("appId", wxXcxConfig.getXcxappid());
            map.put("timeStamp", Long.toString(System.currentTimeMillis()));
            map.put("nonceStr", StringUtil.getRandomChar(32));
            map.put("package", "prepay_id=" + prepay_id);
            map.put("signType", "MD5");
            wxPayApi.signXCX(map);
            return ReturnDto.buildSuccessReturnDto(map);
        } catch (Exception e) {
            log.error("wxCreateXCXAPIOrder Exception", e);
        }
        return ReturnDto.buildSystemErrorReturnDto();
    }

    @RequestMapping("/wxCreateH5Order")
    public ReturnDto wxCreateH5Order(String ip, String trades_id) {
        if(StringUtil.isEmpty(ip))
            return ReturnDto.buildFailedReturnDto("code不能为空");
        if(StringUtil.isEmpty(trades_id))
            return ReturnDto.buildFailedReturnDto("trades_id不能为空");
//        if(StringUtil.isEmpty(site_id))
//            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        try {
            long tradesId=Long.parseLong(trades_id);
            Trades trades = tradesService.getTradesByTradesId(tradesId);
            if(trades == null) {
                return ReturnDto.buildFailedReturnDto("订单不存在");
            }
            if(trades.getIsUpPrice()!=-1){
                trades_id += "_"+trades.getIsUpPrice();
            }
            String tradesName=orderService.findTradesName(trades.getSiteId(),trades.getTradesId());
            //根据订单获取商户的支付账户
            WxConfig wxConfig=wxPayApi.toConfig(trades_id);
            //trades_id += DateUtils.formatDate("_yyddHHmmss");
            String mweb_url = "";
            String fw=balanceService.boolIsProvider(trades.getSiteId());
            if("NO".equals(fw)||trades.getIsServiceOrder()==0){
                mweb_url = payService.wxCreateH5Order(trades.getSiteId(),trades_id, tradesId,trades.getRealPay(),tradesName,ip);
            }else {
                mweb_url = payfwService.wxCreateH5Order(trades.getSiteId(),trades_id, tradesId,trades.getRealPay(),tradesName,ip);
            }
            Map<String, String> map = new HashMap<>();
            map.put("appId", wxConfig.getAppid());
            map.put("timeStamp", Long.toString(System.currentTimeMillis()));
            map.put("nonceStr", StringUtil.getRandomChar(32));
            map.put("mweb_url", mweb_url);
            map.put("signType", "MD5");
            wxPayApi.sign(map,wxConfig.getKey());
            return ReturnDto.buildSuccessReturnDto(map);
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return ReturnDto.buildSystemErrorReturnDto();
    }
    @RequestMapping(value = "/sendredpack",method = RequestMethod.POST)
    @ResponseBody
    public Map sendredpack(String tradesId, int tradesFee, String tradesName, String re_openid) {

        try {
            return payfwService.sendredpack(tradesId,tradesFee,tradesName,re_openid);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (PayException e) {
            e.printStackTrace();
        }
        return null;
    }
}
