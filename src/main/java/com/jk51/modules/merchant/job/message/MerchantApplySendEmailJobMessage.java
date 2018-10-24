package com.jk51.modules.merchant.job.message;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：
 * 作者: XC
 * 创建日期: 2018-09-30 16:38
 * 修改记录:
 **/
public class MerchantApplySendEmailJobMessage {
    private SendSuccessMailToMerchantDto ssmmd;
    private SendFailedMailToMerchantDto sfmmd;
    private SendMailTo51Dto smd;

    public SendSuccessMailToMerchantDto getSsmmd() {
        return ssmmd;
    }

    public void setSsmmd(SendSuccessMailToMerchantDto ssmmd) {
        this.ssmmd = ssmmd;
    }

    public SendFailedMailToMerchantDto getSfmmd() {
        return sfmmd;
    }

    public void setSfmmd(SendFailedMailToMerchantDto sfmmd) {
        this.sfmmd = sfmmd;
    }

    public SendMailTo51Dto getSmd() {
        return smd;
    }

    public void setSmd(SendMailTo51Dto smd) {
        this.smd = smd;
    }
}
