package com.jk51.modules.pay.service.uni;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.http.HttpClientManager;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.pay.cert.CertManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-21
 * 修改记录:
 */
@Component
public class UniPayApi {
    private static final Logger log = LoggerFactory.getLogger(UniPayApi.class);
    /** 证书容器. */
    private static KeyStore keyStore = null;
    private static String DEFAULT_ENCODE = "UTF-8";
    private static Map<String, X509Certificate> certMap = new HashMap<String, X509Certificate>();
    @Autowired
    UniConfig uniConfig;
    @Autowired
    CertManager certManager;

    /**
     * 支付
     * @param orderId:商户订单号
     * @param txnAmt:交易金额，单位分，不要带小数点
     * @return
     */
    public String getFrontConsumeData(String orderId, String txnAmt) throws Exception {
        Map<String, String> data = new HashMap<String, String>();

        data.put("version", "5.0.0");
        data.put("encoding", DEFAULT_ENCODE);
        data.put("signMethod", "01");
        data.put("txnType", "01");
        data.put("txnSubType", "01");
        data.put("bizType", "000201");
        data.put("channelType", "07");
        data.put("merId", uniConfig.getMerId());
        data.put("accessType", "0");
        data.put("orderId",orderId);
        data.put("txnTime", DateUtils.formatDate("yyyyMMddHHmmss"));
        data.put("currencyCode", "156");
        data.put("txnAmt", txnAmt);
        data.put("frontUrl", uniConfig.getFrontUrl());
        data.put("backUrl", uniConfig.getBackUrl());
        sign(data);
        String requestFrontUrl = uniConfig.getFrontRequestUrl();
        String html = createAutoFormHtml(requestFrontUrl, data);
        return html;
    }

    /**
     * 取消订单
     * @param orderId 商户订单号
     * @param origQryId 原始交易流水号
     * @param txnAmt 交易金额，单位分，不要带小数点
     * @return
     * @throws Exception
     */
    public Map<String, String> consumeUndo(String orderId, String origQryId, String txnAmt) throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        data.put("version", "5.0.0");
        data.put("encoding", DEFAULT_ENCODE);
        data.put("signMethod", "01");
        data.put("txnType", "31");
        data.put("txnSubType", "00");
        data.put("bizType", "000201");
        data.put("channelType", "07");
        data.put("merId", uniConfig.getMerId());
        data.put("accessType", "0");
        data.put("orderId", orderId);
        data.put("txnTime", DateUtils.formatDate("yyyyMMddHHmmss"));
        data.put("txnAmt", txnAmt);
        data.put("currencyCode", "156");
        data.put("backUrl", uniConfig.getBackUrl());
        data.put("origQryId", origQryId);
        sign(data);
        return sendReq(data);
    }

    /**
     * 订单查询
     * @param orderId 商户订单号
     * @param txnTime 订单发送时间
     * @return
     * @throws Exception
     */
    public Map<String, String> query(String orderId, String txnTime) throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        data.put("version", "5.0.0");
        data.put("encoding", DEFAULT_ENCODE);
        data.put("signMethod", "01");
        data.put("txnType", "00");
        data.put("txnSubType", "00");
        data.put("bizType", "000201");
        data.put("merId", "777290058110048");
        data.put("accessType", "0");
        data.put("orderId", orderId);
        data.put("txnTime", txnTime);
        sign(data);
        return sendReq(data);
    }

    /**
     * 退款
     * @param origQryId 原始交易流水号
     * @param txnAmt 交易金额，单位分，不要带小数点
     * @return
     * @throws Exception
     */
    public Map<String, String> refund(String origQryId, String txnAmt) throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        data.put("version", "5.0.0");
        data.put("encoding", DEFAULT_ENCODE);
        data.put("signMethod", "01");
        data.put("txnType", "04");
        data.put("txnSubType", "00");
        data.put("bizType", "000201");
        data.put("channelType", "07");
        data.put("merId", "777290058110048");
        data.put("accessType", "0");
        data.put("orderId", DateUtils.formatDate("yyyyMMddHHmmss"));
        data.put("txnTime", DateUtils.formatDate("yyyyMMddHHmmss"));
        data.put("currencyCode", "156");
        data.put("txnAmt", txnAmt);
        data.put("backUrl", uniConfig.getBackUrl());
        data.put("origQryId", origQryId);
        sign(data);
        return sendReq(data);
    }

    /**
     * 后台发送请求
     * @param data 请求参数
     * @return
     */
    private Map<String, String> sendReq(Map<String, String> data) throws Exception {
        Map<String, Object> rspData = new HashMap();
        String resultString = HttpClientManager.doHttpsPost(uniConfig.getBackRequestUrl(), data, DEFAULT_ENCODE,null,null);
        if (null != resultString && !"".equals(resultString)) {
            String[] strs = resultString.split("&");
            for(int i = 0; i < strs.length; ++i) {
                int j = strs[i].indexOf("=");
                if(j != -1)
                    rspData.put(strs[i].substring(0,j), strs[i].substring(j+1,strs[i].length()));
            }
        }
        if(!rspData.isEmpty()) {
            Map<String, String> rspData1 = new HashMap<String, String>();
            Set<String> set = rspData.keySet();
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                String key = it.next();
                rspData1.put(key, rspData.get(key).toString());
            }
            if(validate(rspData1,DEFAULT_ENCODE)) {
                log.info("签名成功");
            } else {
                log.info("签名失败");
            }
            return rspData1;
        }
        return null;
    }

    /**
     * 参数签名，默认utf-8
     * @param data 签名参数
     * @throws Exception
     */
    private void sign(Map<String, String> data) throws Exception{
        data.put("certId", getSignCertId());
        String stringData = coverMap2String(data);
        byte[] byteSign = null;
        String stringSign = null;
        // 通过SHA1进行摘要并转16进制
        byte[] signDigest = SecureUtil.sha1X16(stringData, DEFAULT_ENCODE);
        byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(getSignCertPrivateKey(), signDigest));
        stringSign = new String(byteSign);
        // 设置签名域值
        data.put("signature", stringSign);
    }

    /**
     * 验签
     * @param rspData 参数
     * @param encoding 默认 UTF-8
     * @return
     */
    public boolean validate(Map<String, String> rspData, String encoding) throws Exception{
        if (StringUtil.isEmpty(encoding)) {
            encoding = DEFAULT_ENCODE;
        }
        String stringSign = rspData.get("signature");
        // 从返回报文中获取certId ，然后去证书静态Map中查询对应验签证书对象
        String certId = rspData.get("certId");
        // 将Map信息转换成key1=value1&key2=value2的形式
        String stringData = coverMap2String(rspData);
        // 验证签名需要用银联发给商户的公钥证书.
        return SecureUtil.validateSignBySoft(
                getValidateKey(certId), SecureUtil.base64Decode(stringSign
                        .getBytes(encoding)), SecureUtil.sha1X16(stringData,
                        encoding));
    }

    /**
     * 获取公钥
     * @param certId
     * @return
     * @throws IOException
     */
    private PublicKey getValidateKey(String certId) throws CertificateException,IOException,NoSuchProviderException {
        X509Certificate cf = null;
        if (certMap.containsKey(certId)) {
            // 存在certId对应的证书对象
            cf = certMap.get(certId);
            return cf.getPublicKey();
        } else {
            // 不存在则重新Load证书文件目录
            initValidateCertFromDir();
            if (certMap.containsKey(certId)) {
                // 存在certId对应的证书对象
                cf = certMap.get(certId);
                return cf.getPublicKey();
            } else {
                log.error("缺少certId=[" + certId + "]对应的验签证书.");
                return null;
            }
        }
    }

    /**
     * 生成签名字符串
     * @param data
     * @return
     */
    private String coverMap2String(Map<String, String> data) {
        TreeMap<String, String> tree = new TreeMap<String, String>();
        Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            if ("signature".equals(en.getKey().trim())) {
                continue;
            }
            tree.put(en.getKey(), en.getValue());
        }
        it = tree.entrySet().iterator();
        StringBuffer sf = new StringBuffer();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            sf.append(en.getKey() + "=" + en.getValue()
                    + "&");
        }
        return sf.substring(0, sf.length() - 1);
    }

    /**
     * 创建支付表单
     * @param requestFrontUrl 支付链接
     * @param data 支付参数
     * @return
     */
    private String createAutoFormHtml(String requestFrontUrl, Map<String, String> data) {
        StringBuffer sf = new StringBuffer();
        sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="+ DEFAULT_ENCODE +"\"/></head><body>");
        sf.append("<form id = \"pay_form\" action=\"" + requestFrontUrl
                + "\" method=\"post\">");
        if (null != data && 0 != data.size()) {
            Set<Map.Entry<String, String>> set = data.entrySet();
            Iterator<Map.Entry<String, String>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> ey = it.next();
                String key = ey.getKey();
                String value = ey.getValue();
                sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
                        + key + "\" value=\"" + value + "\"/>");
            }
        }
        sf.append("</form>");
        sf.append("</body>");
        sf.append("<script type=\"text/javascript\">");
        sf.append("document.all.pay_form.submit();");
        sf.append("</script>");
        sf.append("</html>");
        return sf.toString();
    }

    /**
     * 获取私钥id
     * @return
     * @throws IOException
     */
    private String getSignCertId() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        if(keyStore == null)
            keyStore = getKeyInfo(certManager.getUniPrivateKey(), uniConfig.getSignCertPwd(), uniConfig.getSignCertType());
        try {
            Enumeration<String> aliasenum = keyStore.aliases();
            String keyAlias = null;
            if (aliasenum.hasMoreElements()) {
                keyAlias = aliasenum.nextElement();
            }
            X509Certificate cert = (X509Certificate) keyStore
                    .getCertificate(keyAlias);
            return cert.getSerialNumber().toString();
        } catch (KeyStoreException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取私钥
     * @return
     * @throws IOException
     */
    private PrivateKey getSignCertPrivateKey() throws UnrecoverableKeyException,IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException{
        if(keyStore == null)
            keyStore = getKeyInfo(certManager.getUniPrivateKey(), uniConfig.getSignCertPwd(), uniConfig.getSignCertType());
        PrivateKey privateKey = null;
        try {
            Enumeration<String> aliasenum = keyStore.aliases();
            String keyAlias = null;
            if (aliasenum.hasMoreElements()) {
                keyAlias = aliasenum.nextElement();
            }
            privateKey = (PrivateKey) keyStore.getKey(keyAlias,
                    uniConfig.getSignCertPwd().toCharArray());
        } catch (UnrecoverableKeyException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return privateKey;
    }

    /**
     * 加载keyStore
     * @param is
     * @param keypwd
     * @param type
     * @return
     * @throws IOException
     */
    private KeyStore getKeyInfo(InputStream is, String keypwd, String type) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException{
        try {
            KeyStore ks = KeyStore.getInstance(type);
            char[] nPassword = null;
            nPassword = null == keypwd || "".equals(keypwd.trim()) ? null: keypwd.toCharArray();
            if (null != ks) {
                ks.load(is, nPassword);
            }
            return ks;
        } catch (KeyStoreException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (CertificateException e) {
            log.error(e.getMessage(), e);
            throw e;
        }finally {
            if(null != is) {
                is.close();
            }
        }
    }
    /**
     * 初始化公钥
     * @throws IOException
     */
    private void initValidateCertFromDir() throws CertificateException,IOException,NoSuchProviderException{
        certMap.clear();
        CertificateFactory cf = null;
        InputStream in = null;
        try {
            cf = CertificateFactory.getInstance("X.509", "BC");
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(uniConfig.getValidateCertPath());
            X509Certificate validateCert = (X509Certificate) cf.generateCertificate(in);
            certMap.put(validateCert.getSerialNumber().toString(),
                    validateCert);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("IOException", e);
                    throw e;
                }
            }
        }
    }

}
