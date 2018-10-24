package com.jk51.modules.pay.service.merchant;

import com.alipay.api.response.*;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.commons.xml.XmlUtils;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.balance.Balance;
import com.jk51.model.order.SBMember;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.model.order.Trades;
import com.jk51.model.pay.PayInterfaceLog;
import com.jk51.modules.balance.mapper.BalanceMapper;
import com.jk51.modules.im.service.wechatUtil.WechatUtil;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.pay.constants.PayConstant;
import com.jk51.modules.pay.exception.PayException;
import com.jk51.modules.pay.mapper.PayInterfaceLogMapper;
import com.jk51.modules.pay.service.PayInterfaceLogService;
import com.jk51.modules.pay.service.PayLogsService;
import com.jk51.modules.pay.service.ali.AliConfig;
import com.jk51.modules.pay.service.ali.AliPayApi;
import com.jk51.modules.pay.service.ali.request.AliRequestParam;
import com.jk51.modules.pay.service.wx.*;
import com.jk51.modules.pay.service.wx.request.WxRequestParam;
import com.jk51.modules.pay.service.wx.request.WxXcxConfig;
import com.jk51.modules.persistence.mapper.SBMemberMapper;
import com.jk51.modules.store.service.BMemberService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class PayServiceMerchant {

    private static final Logger log = LoggerFactory.getLogger(PayServiceMerchant.class);
    /*@Autowired
    WxConfigMerchant wxConfig;
    @Autowired
    AliConfigMerchant aliConfig;*/
    @Autowired
    WxConfigfw wxConfigfw;
    @Autowired
    WxPayApiMerchant wxPayApi;
    @Autowired
    AliPayApiMerchant aliPayApi;
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
    private WechatUtil wechatUtil;
    @Autowired
    private BalanceMapper balanceMapper;
    /**
     * 微信支付预下单：公众号支付
     * @param tradesId
     * @param tradesFee
     * @param tradesName
     * @param code
     * @return
     * @throws PayException
     * @throws IOException
     */
    public Map<String,String> wxCreateJSAPIOrder(Integer siteId,String trades_id, Long tradesId, int tradesFee, String tradesName, String code,String mobile) throws PayException {
        stringRedisTemplate.opsForValue().set(tradesId+"_wxOrder",trades_id);

        WxRequestParam wxRequestParam = new WxRequestParam();
        payLogsService.addByTrades(tradesId,PayConstant.PAY_STYLE_WX_JSAPI,PayConstant.PAY_STATUS_FAIL);

        wxRequestParam.setOut_trade_no(trades_id);
        wxRequestParam.setTotal_fee(tradesFee);
        wxRequestParam.setBody(tradesName);
        wxRequestParam.setTrade_type("JSAPI");
        String open_id=code;
                //stringRedisTemplate.opsForValue().get(code + "_" + siteId);
        if(!StringUtil.isEmpty(open_id)){
            wxRequestParam.setSub_openid(open_id);
        }
        /*SBMember member = sbMemberMapper.selectByPhoneNum(mobile, siteId);
        if(!StringUtil.isEmpty(member)){
            wxRequestParam.setOpenid(member.getOpen_id());
        }*/
        Map map = null;
        Map<String,String> payMap=null;
        try {
            wxRequestParam.setMch_id(siteId+"");
            Balance balance = balanceMapper.getBalanceBySiteIdForBool(siteId);
            if (StringUtil.isEmpty(balance)){
                payMap =wxPayApi.unifiedOrderP(wxRequestParam,code,mobile,siteId);
            }else {
                payMap =wxPayApi.unifiedOrder(wxRequestParam,code,mobile,siteId);
            }

            String xml = payMap.get("prepay_id");
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
        if(map == null||StringUtil.isEmpty(map.get("prepay_id")))
            return null;
        payMap.put("prepay_id",map.get("prepay_id").toString());
        return payMap;
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
    @Cacheable(value = "weixinjsapi",keyGenerator = "defaultKeyGenerator")
    public String getJSAPITicket(int siteId) throws PayException {
        String access_token =wechatUtil.getAccessToken(siteId);
//getAccessToken(appid, appsecret);
        return getJSAPITicket(access_token);
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

    public String getJSAPITicketfw() throws PayException {
        String access_token =getAccessToken(wxConfigfw.getAppid(), wxConfigfw.getAPPSECRET());
        return getJSAPITicket(access_token);
    }
    public String getJSAPITicket(WxConfigMerchant wxConfig) throws PayException {
        String access_token =getAccessToken(wxConfig.getAppid(), wxConfig.getKey());
        return getJSAPITicket(access_token);
    }
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
}
