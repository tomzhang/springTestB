package com.jk51.modules.pay.controller;

import com.jk51.commons.CommonConstant;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.commons.xml.XmlUtils;
import com.jk51.model.order.Trades;
import com.jk51.model.pay.PayInterfaceLog;
import com.jk51.modules.discount.service.DiscountService;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.pay.constants.PayConstant;
import com.jk51.modules.pay.service.PayInterfaceLogService;
import com.jk51.modules.pay.service.PayService;
import com.jk51.modules.pay.service.ali.request.AliRequestParam;
import com.jk51.modules.pay.service.merchant.PayServiceMerchant;
import com.jk51.modules.pay.service.merchant.WxConfigMerchant;
import com.jk51.modules.pay.service.merchant.WxPayApiMerchant;
import com.jk51.modules.pay.service.wx.WxConfigfw;
import com.jk51.modules.trades.service.TradesService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@RestController
@RequestMapping("/site/wx")
public class WxPaySiteController {

    private static final Logger log = LoggerFactory.getLogger(WxPaySiteController.class);

    @Autowired
    PayInterfaceLogService payInterfaceLogService;
    @Autowired
    PayService payService;
    @Autowired
    private DiscountService discountService;
    @Autowired
    PayServiceMerchant payServiceMerchant;

    @Autowired
    WxPayApiMerchant wxPayApiMerchant;
    @Autowired
    private OrderService orderService;
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
    /**
     * 微信回调(线下微信支付)
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
        } catch (Exception e) {
            log.error("微信支付回调xml解析错误 {}, {}", xml, e);
            return SYSTEM_FAIL_XML;
        }
        String out_trade_no = Objects.toString(map.get("out_trade_no"), "");
        out_trade_no = out_trade_no.split("_")[0];
        long tradesId = NumberUtils.toLong(out_trade_no);
        if (tradesId == 0) {
            return SYSTEM_FAIL_XML;
        }
        Integer siteId=Integer.parseInt(out_trade_no.substring(0,6));
        if (!wxPayApiMerchant.checkSign(map,siteId)) {
            log.warn("微信支付回调签名验证失败 {}", xml);
            return SIGN_FAIL_XML;
        }
        //执行业务操作
        try {
            int totalFee = NumberUtils.toInt(Objects.toString(map.get("total_fee")));
            /*Map<String, Object> maptrades=new HashedMap();
            maptrades.put("trades_id",tradesId);
            maptrades.put("real_pay",totalFee);
            maptrades.put("is_payment",1);
            maptrades.put("pay_style","wx");
            maptrades.put("pay_time",new Date());
            maptrades.put("pay_number",map.get("transaction_id"));
            maptrades.put("site_id",siteId);*/
            String pay_number=map.get("transaction_id")+"";
            Map<String, Object> trades = discountService.updateTradesLine("wx",tradesId,siteId,pay_number);
            if("-1".equals(trades.get("status"))) {
                log.warn("微信支付回调签名支付金额失败 {} {}", tradesId, totalFee);
                return SYSTEM_FAIL_XML;
            }

            return SUCCESS_XML;
        } catch (Exception e) {
            log.error("支付成功，更改订单状态失败，订单号:" + out_trade_no, e);
        }

        return SYSTEM_FAIL_XML;
    }


    @RequestMapping("/createjsapiordersite")
    public ReturnDto createjsapiordersite(String code, String trades_id,int siteId,String mobile) {
        if(StringUtil.isEmpty(code))
            return ReturnDto.buildFailedReturnDto("code不能为空");
        if(StringUtil.isEmpty(trades_id))
            return ReturnDto.buildFailedReturnDto("trades_id不能为空");
        if(StringUtil.isEmpty(siteId))
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        try {
            long tradesId=Long.parseLong(trades_id);
            Map<String, Object> tradesMap =new HashedMap();
            tradesMap.put("trades_id",trades_id);
            Map<String, Object> trades = discountService.getTradesLine(tradesMap);
            if(trades == null) {
                return ReturnDto.buildFailedReturnDto("订单不存在");
            }
            //trades_id += DateUtils.formatDate("_yyddHHmmss");
//            WxPublicConfig wxPublicConfig = wxPublicConfigService.findConfigBySiteId(Integer.parseInt(site_id));
            //String openid = payService.getOpenid(trades.getSiteId(), wxConfig.getAppid(), wxConfig.getAPPSECRET(), code);
            String tradesName=orderService.findTradesName(Integer.parseInt(trades.get("site_id")+""),tradesId)+"-线下";
            Map<String,String> payMap =payServiceMerchant.wxCreateJSAPIOrder(Integer.parseInt(trades.get("site_id")+"") ,trades_id, tradesId,Integer.parseInt(trades.get("real_pay")+""),tradesName,code,mobile);
            String prepay_id = payMap.get("prepay_id");
            Map<String, String> map = new HashMap<>();
            map.put("appId", payMap.get("appId"));
            map.put("timeStamp", Long.toString(System.currentTimeMillis()));
            map.put("nonceStr", StringUtil.getRandomChar(32));
            map.put("package", "prepay_id=" + prepay_id);
            map.put("signType", "MD5");
            wxPayApiMerchant.sign(map,siteId);
            return ReturnDto.buildSuccessReturnDto(map);
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return ReturnDto.buildSystemErrorReturnDto();
    }
    @RequestMapping("/getconfigsite")
    public ReturnDto getconfigsite(String url,int site_id) {
        if(StringUtil.isEmpty(site_id)) {
            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        }
        Map map = new HashMap();
        map.put("noncestr", StringUtil.getRandomChar(32));
        map.put("timestamp", System.currentTimeMillis());
        map.put("url", url);
        try {
            WxConfigMerchant wxConfigMerchant=wxPayApiMerchant.toConfig(site_id);
            String jsapi_ticket = payServiceMerchant.getJSAPITicket(wxConfigMerchant);
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
            map.put("appId", wxConfigMerchant.getAppid());
            return ReturnDto.buildSuccessReturnDto(map);
        } catch (Exception e) {
            log.error("Exception", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }
    @RequestMapping("/getappid")
    public ReturnDto getAppid(Integer site_id) {
        if(StringUtil.isEmpty(site_id)) {
            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        }
        Map map = new HashMap();
        try {
            WxConfigMerchant wxConfigMerchant=wxPayApiMerchant.toConfig(site_id);
            map.put("appid",wxConfigMerchant.getAppid());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        map.put("timestamp", System.currentTimeMillis());
        return ReturnDto.buildSuccessReturnDto(map);
    }
}
