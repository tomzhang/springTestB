package com.jk51.modules.pay.service.merchant;

import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.http.HttpClientManager;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.balance.Balance;
import com.jk51.modules.balance.mapper.BalanceMapper;
import com.jk51.modules.pay.cert.CertManager;
import com.jk51.modules.pay.constants.PayConstant;
import com.jk51.modules.pay.exception.PayException;
import com.jk51.modules.pay.service.wx.WxConfigfw;
import com.jk51.modules.pay.service.wx.request.WxRequestParam;
import com.jk51.modules.persistence.mapper.DiscountMapper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.*;

/**
 * 商家的微信支付
 */
@Component
public class WxPayApiMerchant {
    private static final Logger log = LoggerFactory.getLogger(WxPayApiMerchant.class);


    @Autowired
    private DiscountMapper discountMapper;
    @Autowired
    CertManager certManager;
    @Autowired
    WxConfigfw wxConfigfw;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BalanceMapper balanceMapper;
    /**
     * 微信统一下单(服务商模式)
     * @param wxRequestParam
     * @return
     */
    public Map<String,String> unifiedOrder(WxRequestParam wxRequestParam,String code,String mobile,Integer siteId) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        //检测必填参数
        if (StringUtil.isEmpty(wxRequestParam.getOut_trade_no())) {
            throw new PayException("缺少统一支付接口必填参数out_trade_no！");
        } else if (StringUtil.isEmpty(wxRequestParam.getBody())) {
            throw new PayException("缺少统一支付接口必填参数body！");
        } else if (StringUtil.isEmpty(wxRequestParam.getTotal_fee())) {
            throw new PayException("缺少统一支付接口必填参数total_fee！");
        } else if (StringUtil.isEmpty(wxRequestParam.getTrade_type())) {
            throw new PayException("缺少统一支付接口必填参数trade_type！");
        }
        WxConfigMerchant wxConfig=this.toConfig(Integer.parseInt(wxRequestParam.getMch_id()));
        //wxRequestParam.setAppid(wxConfig.getAppid());
        if(StringUtil.isEmpty(wxRequestParam.getSub_openid())){
            try {
                String json = getOpenId(wxConfig.getAppid(),wxConfig.getAPPSECRET(),code);
                Map map = JacksonUtils.json2map(json);
                wxRequestParam.setSub_openid(map.get("openid").toString());
                //wxRequestParam.setOpenid(map.get("openid").toString());
                //sbMemberMapper.updateIdopenId(siteId, mobile,map.get("openid").toString());
                stringRedisTemplate.opsForValue().set(code + "_" + siteId, map.get("openid").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //关联参数
        if ("JSAPI".equals(wxRequestParam.getTrade_type()) && (StringUtil.isEmpty(wxRequestParam.getOpenid())&&StringUtil.isEmpty(wxRequestParam.getSub_openid()))) {
            throw new PayException("统一支付接口中，缺少必填参数openid！trade_type为JSAPI时，openid为必填参数！");
        }
        if ("NATIVE".equals(wxRequestParam.getTrade_type()) && StringUtil.isEmpty(wxRequestParam.getProduct_id())) {
            throw new PayException("统一支付接口中，缺少必填参数product_id！trade_type为JSAPI时，product_id为必填参数！");
        }

        log.info("异步通知url未设置，则使用配置文件中的url");
        //异步通知url未设置，则使用配置文件中的url
        if (StringUtil.isEmpty(wxRequestParam.getNotify_url())) {
            log.info("异步通知url未设置，则使用配置文件中的url"+wxConfig.getNotify_url());
            if(wxRequestParam.getBody().indexOf("-线下")>-1){
                wxRequestParam.setNotify_url(wxConfig.getNotify_url());
            }else {
                wxRequestParam.setNotify_url(wxConfigfw.getNotify_url());
            }
        }

        wxRequestParam.setAppid(wxConfigfw.getAppid());
        wxRequestParam.setSub_appid(wxConfig.getAppid());
        wxRequestParam.setSub_mch_id(wxConfig.getMch_id());//子商户号
        wxRequestParam.setMch_id(wxConfigfw.getMch_id());//商户号
        if(StringUtil.isEmpty(wxRequestParam.getSpbill_create_ip()))
            wxRequestParam.setSpbill_create_ip(wxConfigfw.getSpbill_create_ip());
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));

        sign(wxRequestParam, wxConfigfw.getKey());
        String xml = toXml(wxRequestParam);
        Map<String,String> payMap =new HashedMap();
        payMap.put("prepay_id",OkHttpUtil.postJson(PayConstant.WX_UNIFIED_ORDER_URL, xml));
        payMap.put("appId",wxConfigfw.getAppid());
        return payMap;
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
        WxConfigMerchant wxConfig=this.toConfig(Integer.parseInt(wxRequestParam.getMch_id()));
        wxRequestParam.setMch_id(wxConfig.getMch_id());//商户号
        wxRequestParam.setAppid(wxConfig.getAppid());
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam, wxConfig.getKey());
        String xml = toXml(wxRequestParam);
        return OkHttpUtil.postJson(PayConstant.WX_ORDER_QUERY_URL, xml);
                //OkHttpUtil.postJson(PayConstant.WX_ORDER_QUERY_URL, xml);
    }

    /**
     * 订单退款
     * @param wxRequestParam
     * @return
     */
    public String refund(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException, Exception {
        checkWxRequestParam(wxRequestParam);
        WxConfigMerchant wxConfig=this.toConfig(Integer.parseInt(wxRequestParam.getMch_id()));
        if(StringUtil.isEmpty(wxRequestParam.getOp_user_id())) {
            wxRequestParam.setOp_user_id(wxConfig.getMch_id());
        }
        wxRequestParam.setAppid(wxConfig.getAppid());
        wxRequestParam.setMch_id(wxConfig.getMch_id());//商户号
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam, wxConfig.getKey());
        String xml = toXml(wxRequestParam);
        return executeCertOpertation(PayConstant.WX_REFUND_URL, xml,wxConfig.getMch_id());
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
        WxConfigMerchant wxConfig=this.toConfig(Integer.parseInt(wxRequestParam.getMch_id()));
        wxRequestParam.setMch_id(wxConfig.getMch_id());//商户号
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam, wxConfig.getKey());
        String xml = toXml(wxRequestParam);
        return OkHttpUtil.postJson(PayConstant.WX_REPORT_URL, xml);
    }



    public String reverse(WxRequestParam wxRequestParam) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException, Exception {
        if(StringUtil.isEmpty(wxRequestParam.getAppid())) {
            throw new PayException("撤销订单API接口中，缺少必填参数appid！");
        } else if(StringUtil.isEmpty(wxRequestParam.getOut_trade_no()) && StringUtil.isEmpty(wxRequestParam.getTransaction_id())) {
            throw new PayException("撤销订单API接口中，参数out_trade_no和transaction_id必须填写一个！");
        }
        WxConfigMerchant wxConfig=this.toConfig(Integer.parseInt(wxRequestParam.getMch_id()));
        wxRequestParam.setMch_id(wxConfig.getMch_id());//商户号
        wxRequestParam.setAppid(wxConfig.getAppid());
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));
        sign(wxRequestParam, wxConfig.getKey());
        String xml = toXml(wxRequestParam);
        return executeCertOpertation(PayConstant.WX_REVERSE_URL, xml,wxConfig.getMch_id());
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
    private String executeCertOpertation(String url, String xml,String mch_id) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {
        return HttpClientManager.doHttpsPost(url,xml,null,certManager.getWxCert(), mch_id);
    }

    public String getOpenId(String appid,String appscret, String code) throws Exception{
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

    /**
     * 微信签名
     * @param
     */
    /*private void sign(WxRequestParam wxRequestParam) throws IllegalAccessException, NoSuchAlgorithmException{
        WxConfigMerchant wxConfig=this.toConfig(Integer.parseInt(wxRequestParam.getMch_id()));
        sign(wxRequestParam, wxConfig.getKey());
    }*/
    public void sign(Map<String, String> map,Integer siteId) throws NoSuchAlgorithmException {

        Set keySet = map.keySet();
        Object[] objs = keySet.toArray();
        Arrays.sort(objs);
        StringBuffer tempStr = new StringBuffer();
        for (int i = 0; i < objs.length; ++i) {
            tempStr.append(objs[i] + "=" + map.get(objs[i])  + "&");
        }
        Balance balance = balanceMapper.getBalanceBySiteIdForBool(siteId);
        if (StringUtil.isEmpty(balance)){
            WxConfigMerchant wxConfig= null;
            try {
                wxConfig = this.toConfig(siteId);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            tempStr.append("key=" + wxConfig.getAPPSECRET());
        }else {
            tempStr.append("key=" + wxConfigfw.getKey());
        }

        map.put("paySign", EncryptUtils.encryptToMD5(tempStr.toString()));
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
    public WxConfigMerchant toConfig(Integer siteId) throws IllegalAccessException{
        if(siteId == null){
            return null;
        }
        Map<String,Object> params=new HashedMap();
        params.put("siteId",siteId);
        Map<String,Object> payMap = discountMapper.getPayAliWx(params);
        if(payMap==null)return null;
        WxConfigMerchant wxConfig=new WxConfigMerchant(payMap.get("wx_app_id").toString(), payMap.get("wx_key").toString(), payMap.get("wx_mch_id").toString(), payMap.get("wx_notify_url").toString(), payMap.get("wx_spbill_create_ip").toString(), payMap.get("wx_appsecret").toString(), payMap.get("wx_cert_path").toString(), "");
        return wxConfig;
    }
    public boolean checkSign(Map map,Integer siteId) {
        WxConfigMerchant wxConfig= null;
        try {
            wxConfig = this.toConfig(siteId);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Set<String> set = map.keySet();
        Object[] strs = set.toArray();
        Arrays.sort(strs);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; ++i) {
            if(!"sign".equals(strs[i])) {
                sb.append(strs[i] + "=" + map.get(strs[i]) + "&");
            }
        }
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
    /**
     * 微信统一下单(普通模式)
     * @param wxRequestParam
     * @return
     */
    public Map<String,String> unifiedOrderP(WxRequestParam wxRequestParam,String code,String mobile,Integer siteId) throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        //检测必填参数
        if (StringUtil.isEmpty(wxRequestParam.getOut_trade_no())) {
            throw new PayException("缺少统一支付接口必填参数out_trade_no！");
        } else if (StringUtil.isEmpty(wxRequestParam.getBody())) {
            throw new PayException("缺少统一支付接口必填参数body！");
        } else if (StringUtil.isEmpty(wxRequestParam.getTotal_fee())) {
            throw new PayException("缺少统一支付接口必填参数total_fee！");
        } else if (StringUtil.isEmpty(wxRequestParam.getTrade_type())) {
            throw new PayException("缺少统一支付接口必填参数trade_type！");
        }
        WxConfigMerchant wxConfig=this.toConfig(Integer.parseInt(wxRequestParam.getMch_id()));
        //wxRequestParam.setAppid(wxConfig.getAppid());
        if(StringUtil.isEmpty(wxRequestParam.getSub_openid())){
            try {
                String json = getOpenId(wxConfig.getAppid(),wxConfig.getKey(),code);
                Map map = JacksonUtils.json2map(json);
                wxRequestParam.setOpenid(map.get("openid").toString());
                //wxRequestParam.setOpenid(map.get("openid").toString());
                //sbMemberMapper.updateIdopenId(siteId, mobile,map.get("openid").toString());
                stringRedisTemplate.opsForValue().set(code + "_" + siteId, map.get("openid").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            wxRequestParam.setOpenid(wxRequestParam.getSub_openid());
            wxRequestParam.setSub_openid(null);
        }
        //关联参数
        if ("JSAPI".equals(wxRequestParam.getTrade_type()) && (StringUtil.isEmpty(wxRequestParam.getOpenid())&&StringUtil.isEmpty(wxRequestParam.getSub_openid()))) {
            throw new PayException("统一支付接口中，缺少必填参数openid！trade_type为JSAPI时，openid为必填参数！");
        }
        if ("NATIVE".equals(wxRequestParam.getTrade_type()) && StringUtil.isEmpty(wxRequestParam.getProduct_id())) {
            throw new PayException("统一支付接口中，缺少必填参数product_id！trade_type为JSAPI时，product_id为必填参数！");
        }

        log.info("异步通知url未设置，则使用配置文件中的url");
        //异步通知url未设置，则使用配置文件中的url
        if (StringUtil.isEmpty(wxRequestParam.getNotify_url())) {
            log.info("异步通知url未设置，则使用配置文件中的url"+wxConfig.getNotify_url());
            wxRequestParam.setNotify_url(wxConfig.getNotify_url());
        }

        wxRequestParam.setAppid(wxConfig.getAppid());
        wxRequestParam.setMch_id(wxConfig.getMch_id());//商户号
        if(StringUtil.isEmpty(wxRequestParam.getSpbill_create_ip()))
            wxRequestParam.setSpbill_create_ip(wxConfigfw.getSpbill_create_ip());
        wxRequestParam.setNonce_str(StringUtil.getRandomStr(32));

        sign(wxRequestParam, wxConfig.getAPPSECRET());
        String xml = toXml(wxRequestParam);
        Map<String,String> payMap =new HashedMap();
        payMap.put("prepay_id",OkHttpUtil.postJson(PayConstant.WX_UNIFIED_ORDER_URL, xml));
        payMap.put("appId",wxConfig.getAppid());
        return payMap;
    }
}
