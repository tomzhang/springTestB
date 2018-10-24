package com.jk51.modules.pay.service.wx.request;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WxRequestParam {
    //公众账号ID
    private String appid;
    //商户号
    private String mch_id;
    //设备号
    private String device_info;
    //随机字符串
    private String nonce_str;
    //签名
    private String sign;
    //签名类型
    private String sign_type;
    //商品描述
    private String body;
    //商品详情
    private String detail;
    //附加数据
    private String attach;
    //商户订单号
    private String out_trade_no;
    //标价币种 默认人民币：CNY
    private String fee_type;
    //标价金额 单位为分
    private Integer total_fee;
    //终端IP
    private String spbill_create_ip;
    //交易起始时间 格式为yyyyMMddHHmmss
    private String time_start;
    //交易结束时间 格式为yyyyMMddHHmmss
    private String time_expire;
    //商品标记
    private String goods_tag;
    //通知地址
    private String notify_url;
    //交易类型 JSAPI，NATIVE，APP
    private String trade_type;
    //商品ID
    private String product_id;
    //指定支付方式
    private String limit_pay;
    //用户标识
    private String openid;

    //子商户号
    private String sub_mch_id;

    //子商户公众账号ID
    private String sub_appid;

    //用户子标识
    private String sub_openid;
    /**
     * 退款请求参数
     */

    //微信订单号
    private String transaction_id;
    //商户退款单号
    private String out_refund_no;
    //退款金额
    private Integer refund_fee;
    //货币种类
    private String refund_fee_type;
    //操作员
    private String op_user_id;
    //微信退款单号
    private String refund_id;
    //对账单日期 格式：20140603
    private String 	bill_date;
    //账单类型 ALL，返回当日所有订单信息，默认值
    //COUNT，返回当日成功支付的订单
    //REFUND，返回当日退款订单
    private String bill_type;

    /**
     * 微信上报
     */
    //接口URL
    private String interface_url;
    //接口耗时
    private Integer execute_time;
    //返回状态码
    private String return_code;
    //业务结果
    private String result_code;
    //访问接口IP
    private String user_ip;

    //URL链接
    private String shorturl;
    //扫码支付授权码，设备读取用户微信中的条码或者二维码信息
    private String auth_code;
    //退款资金来源仅针对老资金流商户使用
    //REFUND_SOURCE_UNSETTLED_FUNDS---未结算资金退款（默认使用未结算资金退款）
    //REFUND_SOURCE_RECHARGE_FUNDS---可用余额退款
    private String refund_account;
    //场景信息//IOS移动应用
    //{"h5_info": {"type":"IOS","app_name": "王者荣耀","bundle_id": "com.tencent.wzryIOS"}}

    //安卓移动应用
    //{"h5_info": {"type":"Android","app_name": "王者荣耀","package_name": "com.tencent.tmgp.sgame"}}

    //WAP网站应用
    //{"h5_info": {"type":"Wap","wap_url": "https://pay.qq.com","wap_name": "腾讯充值"}}
    private String scene_info;

    public String getSub_mch_id() {
        return sub_mch_id;
    }

    public void setSub_mch_id(String sub_mch_id) {
        this.sub_mch_id = sub_mch_id;
    }

    public String getSub_appid() {
        return sub_appid;
    }

    public void setSub_appid(String sub_appid) {
        this.sub_appid = sub_appid;
    }

    public String getSub_openid() {
        return sub_openid;
    }

    public void setSub_openid(String sub_openid) {
        this.sub_openid = sub_openid;
    }

    public void setScene_info(String scene_info) {
        this.scene_info = scene_info;
    }

    public String getScene_info() {
        return scene_info;
    }

    public void setRefund_account(String refund_account) {
        this.refund_account = refund_account;
    }

    public String getRefund_account() {
        return refund_account;
    }

    public String getAuth_code() {
        return auth_code;
    }

    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }

    public String getShorturl() {
        return shorturl;
    }

    public void setShorturl(String shorturl) {
        this.shorturl = shorturl;
    }

    public String getInterface_url() {
        return interface_url;
    }

    public void setInterface_url(String interface_url) {
        this.interface_url = interface_url;
    }

    public int getExecute_time() {
        return execute_time;
    }

    public void setExecute_time(int execute_time) {
        this.execute_time = execute_time;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getUser_ip() {
        return user_ip;
    }

    public void setUser_ip(String user_ip) {
        this.user_ip = user_ip;
    }

    public String getBill_date() {
        return bill_date;
    }

    public void setBill_date(String bill_date) {
        this.bill_date = bill_date;
    }

    public String getBill_type() {
        return bill_type;
    }

    public void setBill_type(String bill_type) {
        this.bill_type = bill_type;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_refund_no() {
        return out_refund_no;
    }

    public void setOut_refund_no(String out_refund_no) {
        this.out_refund_no = out_refund_no;
    }

    public int getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(int refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getRefund_fee_type() {
        return refund_fee_type;
    }

    public void setRefund_fee_type(String refund_fee_type) {
        this.refund_fee_type = refund_fee_type;
    }

    public String getOp_user_id() {
        return op_user_id;
    }

    public void setOp_user_id(String op_user_id) {
        this.op_user_id = op_user_id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public int getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(int total_fee) {
        this.total_fee = total_fee;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(String time_expire) {
        this.time_expire = time_expire;
    }

    public String getGoods_tag() {
        return goods_tag;
    }

    public void setGoods_tag(String goods_tag) {
        this.goods_tag = goods_tag;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getLimit_pay() {
        return limit_pay;
    }

    public void setLimit_pay(String limit_pay) {
        this.limit_pay = limit_pay;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
