package com.jk51.modules.pay.service.wx;

import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.http.HttpClientManager;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.modules.pay.cert.CertManager;
import com.jk51.modules.pay.constants.PayConstant;
import com.jk51.modules.pay.exception.PayException;
import com.jk51.modules.pay.service.wx.request.WxRedpackRequestParam;
import com.jk51.modules.pay.service.wx.request.WxRequestParam;
import com.jk51.modules.pay.service.wx.request.WxXcxConfig;
import com.jk51.modules.persistence.mapper.DiscountMapper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Component
public class WxPayApifw {
    private static final Logger log = LoggerFactory.getLogger(WxPayApifw.class);

    @Autowired
    WxConfig wxConfigold;
    @Autowired
    WxConfigfw wxConfigfw;
    @Autowired
    CertManager certManager;

    @Autowired
    WxConfig51jk wxConfig51jk;
    @Autowired
    WxAppConfig wxAppConfig;
    @Autowired
    WxXcxConfig wxXcxConfig;
    @Autowired
    private DiscountMapper discountMapper;
    @Autowired
    private BalanceService balanceService;
    /**
     * 微信统一下单
     * @param wxRequestParam
     * @return
     */
    public String unifiedOrder(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        //检测必填参数
        if (StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("缺少统一支付接口必填参数appid！");
        } else if (StringUtil.isEmpty(wxRequestParam.getOut_trade_no())) {
            throw new PayException("缺少统一支付接口必填参数out_trade_no！");
        } else if (StringUtil.isEmpty(wxRequestParam.getBody())) {
            throw new PayException("缺少统一支付接口必填参数body！");
        } else if (StringUtil.isEmpty(wxRequestParam.getTotal_fee())) {
            throw new PayException("缺少统一支付接口必填参数total_fee！");
        } else if (StringUtil.isEmpty(wxRequestParam.getTrade_type())) {
            throw new PayException("缺少统一支付接口必填参数trade_type！");
        }
        //关联参数
        if ("JSAPI".equals(wxRequestParam.getTrade_type()) && StringUtil.isEmpty(wxRequestParam.getOpenid())) {
            throw new PayException("统一支付接口中，缺少必填参数openid！trade_type为JSAPI时，openid为必填参数！");
        }
        if ("NATIVE".equals(wxRequestParam.getTrade_type()) && StringUtil.isEmpty(wxRequestParam.getProduct_id())) {
            throw new PayException("统一支付接口中，缺少必填参数product_id！trade_type为JSAPI时，product_id为必填参数！");
        }
        log.info("异步通知url未设置，则使用配置文件中的url");
        //根据订单获取商户的支付账户
        WxConfig wxConfig=this.toConfig(wxRequestParam.getOut_trade_no());
        //异步通知url未设置，则使用配置文件中的url
        if (StringUtil.isEmpty(wxRequestParam.getNotify_url())) {
            log.info("异步通知url未设置，则使用配置文件中的url"+wxConfig.getNotify_url());
            wxRequestParam.setNotify_url(wxConfigfw.getNotify_url());
        }

        wxRequestParam.setSub_mch_id(wxConfig.getMch_id());//子商户号
        wxRequestParam.setMch_id(wxConfigfw.getMch_id());//商户号
        if(StringUtil.isEmpty(wxRequestParam.getSpbill_create_ip()))
            wxRequestParam.setSpbill_create_ip(wxConfigfw.getSpbill_create_ip());
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        return OkHttpUtil.postJson(PayConstant.WX_UNIFIED_ORDER_URL, xml);
    }
    /**
     * 微信统一下单（小程序）
     * @param wxRequestParam
     * @return
     */
    public String unifiedOrderXCX(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        //检测必填参数
        if (StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("缺少统一支付接口必填参数appid！");
        } else if (StringUtil.isEmpty(wxRequestParam.getOut_trade_no())) {
            throw new PayException("缺少统一支付接口必填参数out_trade_no！");
        } else if (StringUtil.isEmpty(wxRequestParam.getBody())) {
            throw new PayException("缺少统一支付接口必填参数body！");
        } else if (StringUtil.isEmpty(wxRequestParam.getTotal_fee())) {
            throw new PayException("缺少统一支付接口必填参数total_fee！");
        } else if (StringUtil.isEmpty(wxRequestParam.getTrade_type())) {
            throw new PayException("缺少统一支付接口必填参数trade_type！");
        }
        //关联参数
        if ("JSAPI".equals(wxRequestParam.getTrade_type()) && StringUtil.isEmpty(wxRequestParam.getOpenid())) {
            throw new PayException("统一支付接口中，缺少必填参数openid！trade_type为JSAPI时，openid为必填参数！");
        }
        if ("NATIVE".equals(wxRequestParam.getTrade_type()) && StringUtil.isEmpty(wxRequestParam.getProduct_id())) {
            throw new PayException("统一支付接口中，缺少必填参数product_id！trade_type为JSAPI时，product_id为必填参数！");
        }
        log.info("异步通知url未设置，则使用配置文件中的url");
        //异步通知url未设置，则使用配置文件中的url
        if (StringUtil.isEmpty(wxRequestParam.getNotify_url())) {
            log.info("异步通知url未设置，则使用配置文件中的url"+wxXcxConfig.getNotify_url());
            wxRequestParam.setNotify_url(wxXcxConfig.getNotify_url());
        }

        wxRequestParam.setMch_id(wxXcxConfig.getMch_id());//商户号
        if(StringUtil.isEmpty(wxRequestParam.getSpbill_create_ip()))
            wxRequestParam.setSpbill_create_ip("172.20.10.92");
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam, wxXcxConfig.getKey());
        String xml = toXml(wxRequestParam);
        return OkHttpUtil.postJson(PayConstant.WX_UNIFIED_ORDER_URL, xml);
    }

    /**
     * App下单
     * @param wxRequestParam
     * @return
     */
    public String unifiedAppOrder(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        //检测必填参数
        if (StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("缺少统一支付接口必填参数appid！");
        } else if (StringUtil.isEmpty(wxRequestParam.getOut_trade_no())) {
            throw new PayException("缺少统一支付接口必填参数 out_trade_no！");
        } else if (StringUtil.isEmpty(wxRequestParam.getBody())) {
            throw new PayException("缺少统一支付接口必填参数 body！");
        } else if (StringUtil.isEmpty(wxRequestParam.getTotal_fee())) {
            throw new PayException("缺少统一支付接口必填参数total_fee！");
        } else if (StringUtil.isEmpty(wxRequestParam.getTrade_type())) {
            throw new PayException("缺少统一支付接口必填参数trade_type！");
        }
        log.info("异步通知url未设置，则使用配置文件中的url");
        //异步通知url未设置，则使用配置文件中的url
        if (StringUtil.isEmpty(wxRequestParam.getNotify_url())) {
            log.info("异步通知url未设置，则使用配置文件中的url"+wxAppConfig.getNotify_url());
            wxRequestParam.setNotify_url(wxAppConfig.getNotify_url());
        }

        wxRequestParam.setMch_id(wxAppConfig.getMch_id());//商户号
        if(StringUtil.isEmpty(wxRequestParam.getSpbill_create_ip()))
            wxRequestParam.setSpbill_create_ip(wxRequestParam.getSpbill_create_ip());
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        appSign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        return OkHttpUtil.postJson(PayConstant.WX_UNIFIED_ORDER_URL, xml);
    }




    /**
     * 刷卡支付
     * @param wxRequestParam
     * @return
     * @throws PayException
     * @throws IOException
     * @throws IllegalAccessException
     * @throws NoSuchAlgorithmException
     */
    public String microPay(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        if (StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("缺少统一支付接口必填参数appid！");
        } else if (StringUtil.isEmpty(wxRequestParam.getOut_trade_no())) {
            throw new PayException("缺少统一支付接口必填参数out_trade_no！");
        } else if (StringUtil.isEmpty(wxRequestParam.getBody())) {
            throw new PayException("缺少统一支付接口必填参数body！");
        } else if (StringUtil.isEmpty(wxRequestParam.getTotal_fee())) {
            throw new PayException("缺少统一支付接口必填参数total_fee！");
        } else if (StringUtil.isEmpty(wxRequestParam.getAuth_code())) {
            throw new PayException("缺少统一支付接口必填参数auth_code！");
        }
        //根据订单获取商户的支付账户
        WxConfig wxConfig=this.toConfig(wxRequestParam.getOut_trade_no());
        wxRequestParam.setMch_id(wxConfigfw.getMch_id());//商户号
        wxRequestParam.setSub_mch_id(wxConfig.getMch_id());//子商户公众账号ID
        if(StringUtil.isEmpty(wxRequestParam.getSpbill_create_ip()))
            wxRequestParam.setSpbill_create_ip(wxConfigfw.getSpbill_create_ip());
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        return OkHttpUtil.postJson(PayConstant.WX_MICRO_PAY_URL, xml);
    }



    /**
     * 查询订单
     * @param wxRequestParam
     * @return
     */
    public String orderQuery(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        if(StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("订单查询接口中，缺少必填参数appid！");
        } else if(StringUtil.isEmpty(wxRequestParam.getOut_trade_no()) && StringUtil.isEmpty(wxRequestParam.getTransaction_id())) {
            throw new PayException("订单查询接口中，out_trade_no、transaction_id至少填一个！");
        }
        //根据订单获取商户的支付账户
        WxConfig wxConfig=this.toConfig(wxRequestParam.getOut_trade_no());
        wxRequestParam.setMch_id(wxConfigfw.getMch_id());//商户号
        wxRequestParam.setSub_mch_id(wxConfig.getMch_id());//子商户公众账号ID
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        return OkHttpUtil.postJson(PayConstant.WX_ORDER_QUERY_URL, xml);
        //OkHttpUtil.postJson(PayConstant.WX_ORDER_QUERY_URL, xml);
    }

    /**
     * 关闭订单
     * @param wxRequestParam
     * @return
     */
    public String closeOrder(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        if(StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("订单关闭接口中，缺少必填参数appid！");
        } else if(StringUtil.isEmpty(wxRequestParam.getOut_trade_no())) {
            throw new PayException("订单关闭接口中，out_trade_no必填！");
        }
        //根据订单获取商户的支付账户
        WxConfig wxConfig=this.toConfig(wxRequestParam.getOut_trade_no());
        wxRequestParam.setMch_id(wxConfigfw.getMch_id());//商户号
        wxRequestParam.setSub_mch_id(wxConfig.getMch_id());//子商户公众账号ID
        wxRequestParam.setAppid(wxConfigfw.getAppid());
        wxRequestParam.setSub_appid(wxConfig.getAppid());
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        wxRequestParam.setSign(null);
        sign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        return OkHttpUtil.postJson(PayConstant.WX_CLOSE_ORDER_URL, xml);
    }

    /**
     * 订单退款
     * @param wxRequestParam
     * @return
     */
    public String refund(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException, Exception {
        checkWxRequestParam(wxRequestParam);
        //根据订单获取商户的支付账户
        WxConfig wxConfig=this.toConfig(wxRequestParam.getOut_trade_no());
        if(StringUtil.isEmpty(wxRequestParam.getOp_user_id())) {
            wxRequestParam.setOp_user_id(wxConfigfw.getMch_id());
        }
        wxRequestParam.setMch_id(wxConfigfw.getMch_id());//商户号
        wxRequestParam.setSub_mch_id(wxConfig.getMch_id());//子商户公众账号ID
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        wxRequestParam.setOut_trade_no(null);
        sign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        return HttpClientManager.doHttpsPost(PayConstant.WX_REFUND_URL,xml,null,certManager.getWxCert(wxConfigfw.getCert_path()), wxConfigfw.getMch_id());
        //return executeCertOpertation(PayConstant.WX_REFUND_URL, xml);
    }

    /**
     * @param wxRequestParam
     * @return
     */
    public String refundApp(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException, Exception {
        checkWxRequestParam(wxRequestParam);
        if(StringUtil.isEmpty(wxRequestParam.getOp_user_id())) {
            wxRequestParam.setOp_user_id(wxAppConfig.getMch_id());
        }
        wxRequestParam.setMch_id(wxAppConfig.getMch_id());//商户号
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        wxRequestParam.setAppid(wxAppConfig.getAppid());
        appSign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        return executeCertAppOpertation(PayConstant.WX_REFUND_URL, xml);
    }

    public String refund51jk(WxRequestParam wxRequestParam) throws Exception {
        checkWxRequestParam(wxRequestParam);
        if(StringUtil.isEmpty(wxRequestParam.getOp_user_id())) {
            wxRequestParam.setOp_user_id(wxConfig51jk.getMch_id());
        }
        wxRequestParam.setMch_id(wxConfig51jk.getMch_id());//商户号
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        wxRequestParam.setNonce_str("uV7tyQju5eqUMzV7NhNww5PncM6N9zhB");
        //log.info("退款资金来源仅针对老资金流商户使用setRefund_account");
        //wxRequestParam.setRefund_account("REFUND_SOURCE_RECHARGE_FUNDS");
        sign(wxRequestParam, wxConfig51jk.getKey());
        String xml = toXml(wxRequestParam);
        log.info("xml退款-----"+xml);
        return HttpClientManager.doHttpsPost(PayConstant.WX_REFUND_URL,xml,null,certManager.getWxCert(), wxConfig51jk.getMch_id());
        //return executeCertOpertation51jk(PayConstant.WX_REFUND_URL, xml);
    }

    public void checkWxRequestParam(WxRequestParam wxRequestParam) throws PayException {
        if(StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("退款申请接口中，缺少必填参数appid！");
        } else if(StringUtil.isEmpty(wxRequestParam.getOut_trade_no()) && StringUtil.isEmpty(wxRequestParam.getTransaction_id())) {
            throw new PayException("退款申请接口中，out_trade_no、transaction_id至少填一个！");
        } else if(StringUtil.isEmpty(wxRequestParam.getOut_refund_no())) {
            throw new PayException("退款申请接口中，缺少必填参数out_refund_no！");
        } else if(StringUtil.isEmpty(wxRequestParam.getTotal_fee())) {
            throw new PayException("退款申请接口中，缺少必填参数total_fee！");
        } else if(StringUtil.isEmpty(wxRequestParam.getRefund_fee())) {
            throw new PayException("退款申请接口中，缺少必填参数refund_fee！");
        }
    }

    private String executeCertOpertation51jk(String url, String xml) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(wxConfig51jk.getCert_path());
        return HttpClientManager.doHttpsPost(url,xml,null, resourceAsStream, wxConfig51jk.getMch_id());
    }

    /**
     * 查询退款
     * @param wxRequestParam
     * @return
     */
    public String refundQuery(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        if(StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("退款查询接口中，缺少必填参数appid！");
        } else if(StringUtil.isEmpty(wxRequestParam.getOut_trade_no()) && StringUtil.isEmpty(wxRequestParam.getTransaction_id())
                && StringUtil.isEmpty(wxRequestParam.getOut_refund_no()) && StringUtil.isEmpty(wxRequestParam.getRefund_id())) {
            throw new PayException("退款查询接口中，out_refund_no、out_trade_no、transaction_id、refund_id四个参数必填一个！");
        }
        //根据订单获取商户的支付账户
        WxConfig wxConfig=this.toConfig(wxRequestParam.getOut_trade_no());
        wxRequestParam.setMch_id(wxConfigfw.getMch_id());//商户号
        wxRequestParam.setSub_mch_id(wxConfig.getMch_id());//子商户公众账号ID
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        return OkHttpUtil.postJson(PayConstant.WX_REFUND_QUERY_URL, xml);
    }

    public String downloadBill(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        if(StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("对账单接口中，缺少必填参数appid！");
        } else if(StringUtil.isEmpty(wxRequestParam.getBill_date())) {
            throw new PayException("对账单接口中，缺少必填参数bill_date！");
        }
        //根据订单获取商户的支付账户
        WxConfig wxConfig=this.toConfig(wxRequestParam.getOut_trade_no());
        wxRequestParam.setMch_id(wxConfigfw.getMch_id());//商户号
        wxRequestParam.setSub_mch_id(wxConfig.getMch_id());//子商户公众账号ID
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        return OkHttpUtil.postJson(PayConstant.WX_DOWNLOAD_BILL_URL, xml);
    }

    public String report(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        if(StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("缺少必填参数appid！");
        } else if(StringUtil.isEmpty(wxRequestParam.getInterface_url())) {
            throw new PayException("接口URL，缺少必填参数interface_url！");
        } else if(StringUtil.isEmpty(wxRequestParam.getReturn_code())) {
            throw new PayException("返回状态码，缺少必填参数return_code！");
        } else if(StringUtil.isEmpty(wxRequestParam.getResult_code())) {
            throw new PayException("业务结果，缺少必填参数result_code！");
        } else if(StringUtil.isEmpty(wxRequestParam.getUser_ip())) {
            throw new PayException("访问接口IP，缺少必填参数user_ip！");
        } else if(StringUtil.isEmpty(wxRequestParam.getExecute_time())) {
            throw new PayException("接口耗时，缺少必填参数execute_time！");
        }
        //根据订单获取商户的支付账户
        WxConfig wxConfig=this.toConfig(wxRequestParam.getOut_trade_no());
        wxRequestParam.setMch_id(wxConfigfw.getMch_id());//商户号
        wxRequestParam.setSub_mch_id(wxConfig.getMch_id());//子商户公众账号ID
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        return OkHttpUtil.postJson(PayConstant.WX_REPORT_URL, xml);
    }

    public String shortUrl(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        if(StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("缺少必填参数appid！");
        } else if(StringUtil.isEmpty(wxRequestParam.getShorturl())) {
            throw new PayException("需要转换的URL，签名用原串，传输需URL encode！");
        }
        //根据订单获取商户的支付账户
        WxConfig wxConfig=this.toConfig(wxRequestParam.getOut_trade_no());
        wxRequestParam.setMch_id(wxConfigfw.getMch_id());//商户号
        wxRequestParam.setSub_mch_id(wxConfig.getMch_id());//子商户公众账号ID
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        return OkHttpUtil.postJson(PayConstant.WX_SHORT_URL_URL, xml);
    }

    public String reverse(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException, Exception {
        if(StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("撤销订单API接口中，缺少必填参数appid！");
        } else if(StringUtil.isEmpty(wxRequestParam.getOut_trade_no()) && StringUtil.isEmpty(wxRequestParam.getTransaction_id())) {
            throw new PayException("撤销订单API接口中，参数out_trade_no和transaction_id必须填写一个！");
        }
        //根据订单获取商户的支付账户
        WxConfig wxConfig=this.toConfig(wxRequestParam.getOut_trade_no());
        wxRequestParam.setMch_id(wxConfigfw.getMch_id());//商户号
        wxRequestParam.setSub_mch_id(wxConfig.getMch_id());//子商户公众账号ID
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        return HttpClientManager.doHttpsPost(PayConstant.WX_REVERSE_URL,xml,null,certManager.getWxCert(wxConfig.getCert_path()), wxConfig.getMch_id());
        //return executeCertOpertation(PayConstant.WX_REFUND_URL, xml);
        //return executeCertOpertation(PayConstant.WX_REVERSE_URL, xml);
    }

    /**
     * 添加证书操作
     * @param url
     * @param xml
     * @return
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException
     */
    /*private String executeCertOpertation(String url, String xml) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {
        return HttpClientManager.doHttpsPost(url,xml,null,certManager.getWxCert(), wxConfig.getMch_id());
    }*/

    private String executeCertAppOpertation(String url, String xml) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {
        return HttpClientManager.doHttpsPost(url,xml,null,certManager.getWxCertApp(), wxAppConfig.getMch_id());
    }

    public String getOpenId(String appid, String appscret, String code) throws Exception{
        if(StringUtil.isEmpty(appid)) {
            throw new PayException("appid不能为空！");
        } else if(StringUtil.isEmpty(appscret)) {
            throw new PayException("appscret不能为空！");
        }
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid="+ appid +"&secret="+ appscret +"&code="+ code +"&grant_type=authorization_code";

        return HttpClient.doHttpGet(url);
//        CloseableHttpResponse response = HttpClientManager.httpGetRequest(url);
//        HttpEntity entity = response.getEntity();
//        if (entity != null) {
//             获取返回结果
//            return EntityUtils.toString(entity);
//        }
//        return null;
    }

    /**
     * 小程序获取OpenId
     * @param appid
     * @param appscret
     * @param code
     * @return
     * @throws Exception
     */
    public String getOpenIdXCX(String appid, String appscret, String code) throws Exception{
        if(StringUtil.isEmpty(appid)) {
            throw new PayException("appid不能为空！");
        } else if(StringUtil.isEmpty(appscret)) {
            throw new PayException("appscret不能为空！");
        }
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+ appid +"&secret="+ appscret +"&js_code="+ code +"&grant_type=authorization_code";

        return HttpClient.doHttpGet(url);
//        CloseableHttpResponse response = HttpClientManager.httpGetRequest(url);
//        HttpEntity entity = response.getEntity();
//        if (entity != null) {
//             获取返回结果
//            return EntityUtils.toString(entity);
//        }
//        return null;
    }
    public String getAccessToken(String appid, String appscret) throws Exception {
        if(StringUtil.isEmpty(appid)) {
            throw new PayException("appid不能为空！");
        } else if(StringUtil.isEmpty(appscret)) {
            throw new PayException("appscret不能为空！");
        }
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+ appid +"&secret=" + appscret;

        return HttpClient.doHttpGet(url);
//        CloseableHttpResponse response = HttpClientManager.httpGetRequest(url);
//        HttpEntity entity = response.getEntity();
//        if (entity != null) {
        // 获取返回结果
//            return EntityUtils.toString(entity);
//        }
//        return null;
    }

    public String getJSAPITicket(String access_token) throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+ access_token +"&type=jsapi";

        return HttpClient.doHttpGet(url);
//        CloseableHttpResponse response = HttpClientManager.httpGetRequest(url);
//        HttpEntity entity = response.getEntity();
//        if (entity != null) {
        // 获取返回结果
//            return EntityUtils.toString(entity);
//        }
//        return null;
    }


    private void appSign(WxRequestParam wxRequestParam) throws IllegalAccessException, NoSuchAlgorithmException{
        sign(wxRequestParam, wxAppConfig.getKey());
    }


    /**
     * 微信签名
     * @param wxRequestParam
     */
    private void sign(WxRequestParam wxRequestParam) throws IllegalAccessException, NoSuchAlgorithmException{
        //根据订单获取商户的支付账户
        //WxConfig wxConfig=this.toConfig(wxRequestParam.getOut_trade_no());
        sign(wxRequestParam, wxConfigfw.getKey());
    }
    /**
     * 微信红包前面签名
     * @param wxRequestParam
     */
    private void sign(WxRedpackRequestParam wxRequestParam) throws IllegalAccessException, NoSuchAlgorithmException{
        //根据订单获取商户的支付账户
        //WxConfig wxConfig=this.toConfig(wxRequestParam.getOut_trade_no());
        sign(wxRequestParam, wxConfigfw.getKey());
    }
    private void sign(WxRequestParam wxRequestParam, String wxKey) throws IllegalAccessException, NoSuchAlgorithmException {
        if(wxRequestParam == null){
            return;
        }

        Field[] declaredFields = wxRequestParam.getClass().getDeclaredFields();
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> fields = new ArrayList<String>();
        try {
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if(field.get(wxRequestParam) != null) {
                    fields.add(field.getName());
                    map.put(field.getName(), field.get(wxRequestParam));
                }
            }
            Collections.sort(fields);
            StringBuffer tempStr = new StringBuffer();
            for (String fieldName : fields) {
                tempStr.append(fieldName + "=" + map.get(fieldName) + "&");
            }
            tempStr.append("key=" + wxKey);
            String sign = EncryptUtils.encryptToMD5(tempStr.toString());
            sign = sign.toUpperCase();
            wxRequestParam.setSign(sign);
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException", e);
            throw e;
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException", e);
            throw e;
        }
    }
    private void sign(WxRedpackRequestParam wxRequestParam, String wxKey) throws IllegalAccessException, NoSuchAlgorithmException {
        if(wxRequestParam == null){
            return;
        }

        Field[] declaredFields = wxRequestParam.getClass().getDeclaredFields();
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> fields = new ArrayList<String>();
        try {
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if(field.get(wxRequestParam) != null) {
                    fields.add(field.getName());
                    map.put(field.getName(), field.get(wxRequestParam));
                }
            }
            Collections.sort(fields);
            StringBuffer tempStr = new StringBuffer();
            for (String fieldName : fields) {
                tempStr.append(fieldName + "=" + map.get(fieldName) + "&");
            }
            tempStr.append("key=" + wxKey);
            String sign = EncryptUtils.encryptToMD5(tempStr.toString());
            sign = sign.toUpperCase();
            wxRequestParam.setSign(sign);
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException", e);
            throw e;
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException", e);
            throw e;
        }
    }
    public void signXCX(Map<String, String> map) throws NoSuchAlgorithmException {
        Set keySet = map.keySet();
        Object[] objs = keySet.toArray();
        Arrays.sort(objs);
        StringBuffer tempStr = new StringBuffer();
        for (int i = 0; i < objs.length; ++i) {
            tempStr.append(objs[i] + "=" + map.get(objs[i])  + "&");
        }
        tempStr.append("key=" + wxXcxConfig.getKey());
        map.put("paySign", EncryptUtils.encryptToMD5(tempStr.toString()));
    }
    public void sign(Map<String, String> map,String key) throws NoSuchAlgorithmException {
        Set keySet = map.keySet();
        Object[] objs = keySet.toArray();
        Arrays.sort(objs);
        StringBuffer tempStr = new StringBuffer();
        for (int i = 0; i < objs.length; ++i) {
            tempStr.append(objs[i] + "=" + map.get(objs[i])  + "&");
        }
        tempStr.append("key=" + key);
        map.put("paySign", EncryptUtils.encryptToMD5(tempStr.toString()));
    }

    public void appSign(Map<String, String> map) throws NoSuchAlgorithmException {
        Set keySet = map.keySet();
        Object[] objs = keySet.toArray();
        Arrays.sort(objs);
        StringBuffer tempStr = new StringBuffer();
        for (int i = 0; i < objs.length; ++i) {
            tempStr.append(objs[i] + "=" + map.get(objs[i])  + "&");
        }
        tempStr.append("key=" + wxAppConfig.getKey());
        map.put("sign", EncryptUtils.encryptToMD5(tempStr.toString()));
    }

    public boolean checkSign(Map map,String out_trade_no) {
        Set<String> set = map.keySet();
        Object[] strs = set.toArray();
        Arrays.sort(strs);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; ++i) {
            if(!"sign".equals(strs[i])) {
                sb.append(strs[i] + "=" + map.get(strs[i]) + "&");
            }
        }
        //根据订单获取商户的支付账户
        WxConfig wxConfig=this.toConfig(out_trade_no);
        sb.append("key=" + wxConfig.getKey());
        try {
            String sign = EncryptUtils.encryptToMD5(sb.toString()).toUpperCase();
//            if(map.get("sign").equals(sign))
            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String toXml(WxRequestParam wxRequestParam) throws IllegalAccessException{
        if(wxRequestParam == null)
            return null;
        Field[] declaredFields = wxRequestParam.getClass().getDeclaredFields();
        StringBuffer xmlStr = new StringBuffer("<xml>");
        try {

            for (Field field : declaredFields) {
                field.setAccessible(true);
                if(field.get(wxRequestParam) != null)
                    xmlStr.append("<"+ field.getName() +"><![CDATA[").append(field.get(wxRequestParam)).append("]]></"+ field.getName() +">");
            }
            xmlStr.append("</xml>");
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException", e);
        }
        return xmlStr.toString();
    }
    private String toXml(WxRedpackRequestParam wxRequestParam) throws IllegalAccessException{
        if(wxRequestParam == null)
            return null;
        Field[] declaredFields = wxRequestParam.getClass().getDeclaredFields();
        StringBuffer xmlStr = new StringBuffer("<xml>");
        try {

            for (Field field : declaredFields) {
                field.setAccessible(true);
                if(field.get(wxRequestParam) != null)
                    xmlStr.append("<"+ field.getName() +"><![CDATA[").append(field.get(wxRequestParam)).append("]]></"+ field.getName() +">");
            }
            xmlStr.append("</xml>");
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException", e);
        }
        return xmlStr.toString();
    }
    public WxConfig toConfig(String out_trade_no){
        if(StringUtil.isEmpty(out_trade_no)){
            return null;
        }
        Integer siteId=Integer.parseInt(out_trade_no.substring(0,6));
        return toConfigSiteId( siteId);
    }
    public WxConfig toConfigSiteId(Integer siteId){
        if(StringUtil.isEmpty(siteId)){
            return null;
        }
        Map<String,Object> params=new HashedMap();
        params.put("siteId",siteId);
        Map<String,Object> payMap = discountMapper.getPayAliWx(params);
        //查看该商家是否是服务商商户
        String fw=balanceService.boolIsProvider(siteId);
        if("NO".equals(fw)||payMap==null)return wxConfigold;
        WxConfig wxConfig=new WxConfig(payMap.get("wx_app_id").toString(), payMap.get("wx_appsecret").toString(), payMap.get("wx_mch_id").toString(), wxConfigold.getNotify_url(), payMap.get("wx_spbill_create_ip").toString(), payMap.get("wx_key").toString(), payMap.get("wx_cert_path").toString(), "");
        return wxConfig;
    }
    /**
     * 发放普通红包
     * @param wxRequestParam
     * @return
     * @throws PayException
     * @throws IOException
     * @throws IllegalAccessException
     * @throws NoSuchAlgorithmException
     */
    public String sendredpack(WxRedpackRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {

        sign(wxRequestParam);
        String xml = toXml(wxRequestParam);
        //return OkHttpUtil.postJson(PayConstant.WX_SENDEEDPACK_URL, xml);
        return HttpClientManager.doHttpsPost(PayConstant.WX_SENDEEDPACK_URL,xml,null,certManager.getWxCert(wxConfigfw.getCert_path()), wxConfigfw.getMch_id());
    }
}
