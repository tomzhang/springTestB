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
public class WxRedpackRequestParam {
    //(发放普通红包)商户订单号（每个订单号必须唯一。取值范围：0~9，a~z，A~Z）接口根据商户订单号支持重入，如出现超时可再调用。
    private String mch_billno;
    //(发放普通红包)公众账号appid
    private String wxappid;
    //(发放普通红包)触达用户appid
    private String msgappid;
    //(发放普通红包)红包发送者名称
    private String send_name;
    //随机字符串
    private String nonce_str;
    //签名
    private String sign;
    //商户号
    private String mch_id;
    //子商户号
    private String sub_mch_id;
    //用户openid
    private String re_openid;
    //付款金额
    private int total_amount;
    //红包发放总人数
    private int total_num;
    //红包祝福语
    private String wishing;
    //Ip地址
    private String client_ip;
    //活动名称
    private String act_name;
    //备注
    private String remark;
    //场景id
    private String scene_id;
    //活动信息
    private String risk_info;
    //扣钱方mchid
    private String consume_mch_id;


    public int getTotal_num() {
        return total_num;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }

    public String getWishing() {
        return wishing;
    }

    public void setWishing(String wishing) {
        this.wishing = wishing;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public String getAct_name() {
        return act_name;
    }

    public void setAct_name(String act_name) {
        this.act_name = act_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getScene_id() {
        return scene_id;
    }

    public void setScene_id(String scene_id) {
        this.scene_id = scene_id;
    }

    public String getRisk_info() {
        return risk_info;
    }

    public void setRisk_info(String risk_info) {
        this.risk_info = risk_info;
    }

    public String getConsume_mch_id() {
        return consume_mch_id;
    }

    public void setConsume_mch_id(String consume_mch_id) {
        this.consume_mch_id = consume_mch_id;
    }


    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setRe_openid(String re_openid) {
        this.re_openid = re_openid;
    }

    public String getRe_openid() {
        return re_openid;
    }

    public void setSend_name(String send_name) {
        this.send_name = send_name;
    }

    public String getSend_name() {
        return send_name;
    }

    public void setMsgappid(String msgappid) {
        this.msgappid = msgappid;
    }

    public String getMsgappid() {
        return msgappid;
    }

    public void setWxappid(String wxappid) {
        this.wxappid = wxappid;
    }

    public String getWxappid() {
        return wxappid;
    }

    public void setMch_billno(String mch_billno) {
        this.mch_billno = mch_billno;
    }

    public String getMch_billno() {
        return mch_billno;
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
    public String getSub_mch_id() {
        return sub_mch_id;
    }

    public void setSub_mch_id(String sub_mch_id) {
        this.sub_mch_id = sub_mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getMch_id() {
        return mch_id;
    }
}
