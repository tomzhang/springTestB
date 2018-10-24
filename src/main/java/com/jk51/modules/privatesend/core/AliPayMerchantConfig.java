package com.jk51.modules.privatesend.core;


public class AliPayMerchantConfig {

    public static String URL="https://openapi.alipay.com/gateway.do";
    public static String CHARSET = "UTF-8";
    public static String FORMAT = "json";
    public static String SIGNTYPE = "RSA2";
    public static String SIGN_CHARSET = "GBK";

    private String alipay_flag;
    private String alipay_privatekey;
    private String alipay_publickey;
    private String alipay_appid;

    private String merchant_id;

    private String publickey;

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    public String getAlipay_flag() {
        return alipay_flag;
    }

    public void setAlipay_flag(String alipay_flag) {
        this.alipay_flag = alipay_flag;
    }

    public String getAlipay_privatekey() {
        return alipay_privatekey;
    }

    public void setAlipay_privatekey(String alipay_privatekey) {
        this.alipay_privatekey = alipay_privatekey;
    }

    public String getAlipay_publickey() {
        return alipay_publickey;
    }

    public void setAlipay_publickey(String alipay_publickey) {
        this.alipay_publickey = alipay_publickey;
    }

    public String getAlipay_appid() {
        return alipay_appid;
    }

    public void setAlipay_appid(String alipay_appid) {
        this.alipay_appid = alipay_appid;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }
}
