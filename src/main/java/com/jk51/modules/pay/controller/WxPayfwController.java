package com.jk51.modules.pay.controller;

import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.Trades;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.pay.service.PayInterfaceLogService;
import com.jk51.modules.pay.service.PayService;
import com.jk51.modules.pay.service.merchant.PayServiceMerchant;
import com.jk51.modules.pay.service.merchant.WxPayApiMerchant;
import com.jk51.modules.pay.service.wx.WxConfigfw;
import com.jk51.modules.trades.service.TradesService;
import org.apache.commons.collections.map.HashedMap;
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
@RequestMapping("/fw/wx")
public class WxPayfwController {

    private static final Logger log = LoggerFactory.getLogger(WxPayfwController.class);

    @Autowired
    PayInterfaceLogService payInterfaceLogService;
    @Autowired
    PayService payService;
    @Autowired
    TradesService tradesService;
    @Autowired
    PayServiceMerchant payServiceMerchant;
    @Autowired
    WxPayApiMerchant wxPayApiMerchant;
    @Autowired
    WxConfigfw wxConfigfw;
    @Autowired
    private OrderService orderService;
    @RequestMapping("/createjsapiorder")
    public ReturnDto createjsapiordersite(String code, String trades_id,int site_id,String mobile) {
        if(StringUtil.isEmpty(code))
            return ReturnDto.buildFailedReturnDto("code不能为空");
        if(StringUtil.isEmpty(trades_id))
            return ReturnDto.buildFailedReturnDto("trades_id不能为空");
        if(StringUtil.isEmpty(site_id))
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        try {
            long tradesId=Long.parseLong(trades_id);
            Map<String, Object> tradesMap =new HashedMap();
            tradesMap.put("trades_id",trades_id);
            Trades trades = tradesService.getTradesByTradesId(tradesId);
            if(trades == null) {
                return ReturnDto.buildFailedReturnDto("订单不存在");
            }
            if(trades.getIsUpPrice()!=-1){
                trades_id += "_"+trades.getIsUpPrice();
            }
            String tradesName=orderService.findTradesName(trades.getSiteId(),trades.getTradesId());
            Map<String,String> payMap =payServiceMerchant.wxCreateJSAPIOrder(trades.getSiteId() ,trades_id, tradesId,trades.getRealPay(),tradesName,code,mobile);
            String prepay_id = payMap.get("prepay_id");
            Map<String, String> map = new HashMap<>();
            map.put("appId", payMap.get("appId"));
            map.put("timeStamp", Long.toString(System.currentTimeMillis()));
            map.put("nonceStr", StringUtil.getRandomChar(32));
            map.put("package", "prepay_id=" + prepay_id);
            map.put("signType", "MD5");
            wxPayApiMerchant.sign(map,site_id);
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
            String jsapi_ticket = payServiceMerchant.getJSAPITicketfw();
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
            map.put("appId", wxConfigfw.getAppid());
            return ReturnDto.buildSuccessReturnDto(map);
        } catch (Exception e) {
            log.error("Exception", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }
}
