package com.jk51.model.merchant;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：签约合同
 * 作者: XC
 * 创建日期: 2018-09-29 13:33
 * 修改记录:
 **/
public class SignContract {
    private Integer id;
    private String contract_contents;
    private Integer commission;
    private Integer sms_fees;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContract_contents() {
        return contract_contents;
    }

    public void setContract_contents(String contract_contents) {
        this.contract_contents = contract_contents;
    }

    public Integer getCommission() {
        return commission;
    }

    public void setCommission(Integer commission) {
        this.commission = commission;
    }

    public Integer getSms_fees() {
        return sms_fees;
    }

    public void setSms_fees(Integer sms_fees) {
        this.sms_fees = sms_fees;
    }
}
