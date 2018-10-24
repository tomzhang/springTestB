package com.jk51.modules.merchant.job.message;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：
 * 作者: XC
 * 创建日期: 2018-09-30 16:48
 * 修改记录:
 **/
public class SendFailedMailToMerchantDto {
    private String legal_name;
    private String reason;
    private String accepter;

    public String getLegal_name() {
        return legal_name;
    }

    public void setLegal_name(String legal_name) {
        this.legal_name = legal_name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAccepter() {
        return accepter;
    }

    public void setAccepter(String accepter) {
        this.accepter = accepter;
    }
}
