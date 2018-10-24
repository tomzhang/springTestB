package com.jk51.modules.pay.service.ali.request;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
public class AliTransRequestParam {
    //分账支出方账户
    private String trans_out;
    //分账收入方账户
    private String trans_in;
    //分账的金额
    private float amount;
    //分账信息中分账百分比
    private float amount_percenta;

    public String getTrans_out() {
        return trans_out;
    }

    public void setTrans_out(String trans_out) {
        this.trans_out = trans_out;
    }

    public String getTrans_in() {
        return trans_in;
    }

    public void setTrans_in(String trans_in) {
        this.trans_in = trans_in;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getAmount_percenta() {
        return amount_percenta;
    }

    public void setAmount_percenta(float amount_percenta) {
        this.amount_percenta = amount_percenta;
    }
}
