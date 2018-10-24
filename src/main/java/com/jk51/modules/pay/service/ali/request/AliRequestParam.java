package com.jk51.modules.pay.service.ali.request;


import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
public class AliRequestParam {
    /*统一收单线下交易预创建请求参数*/
    //商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
    private String out_trade_no;
    //订单总金额
    private Float total_amount;
    //订单标题
    private String subject;
    //支付宝交易号
    private String trade_no;
    //需要退款的金额
    private Float refund_amount;
    //支付场景 条码支付，取值：bar_code 声波支付，取值：wave_code
    private String scene;
    //支付授权码
    private String auth_code;
    //结算请求流水号
    private String out_request_no;
    //分账明细信息
    private List<AliTransRequestParam> royalty_parameters;
    //买家支付宝账号
    private String buyer_logon_id;
    //买家的支付宝唯一用户号
    private String buyer_id;
    //区分订单支付类型
    private String body;
    private ExtendParams extend_params;

    public void setExtend_params(ExtendParams extend_params) {
        this.extend_params = extend_params;
    }

    public ExtendParams getExtend_params() {
        return extend_params;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getBuyer_logon_id() {
        return buyer_logon_id;
    }

    public void setBuyer_logon_id(String buyer_logon_id) {
        this.buyer_logon_id = buyer_logon_id;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public Float getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Float total_amount) {
        this.total_amount = total_amount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public Float getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(Float refund_amount) {
        this.refund_amount = refund_amount;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getAuth_code() {
        return auth_code;
    }

    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }

    public String getOut_request_no() {
        return out_request_no;
    }

    public void setOut_request_no(String out_request_no) {
        this.out_request_no = out_request_no;
    }

    public List<AliTransRequestParam> getRoyalty_parameters() {
        return royalty_parameters;
    }

    public void setRoyalty_parameters(List<AliTransRequestParam> royalty_parameters) {
        this.royalty_parameters = royalty_parameters;
    }
}
