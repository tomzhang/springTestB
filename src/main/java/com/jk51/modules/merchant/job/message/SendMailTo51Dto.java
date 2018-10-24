package com.jk51.modules.merchant.job.message;

import com.jk51.model.merchant.MerchantApplyDto;

import java.util.List;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：
 * 作者: XC
 * 创建日期: 2018-09-30 16:52
 * 修改记录:
 **/
public class SendMailTo51Dto {
    private String merchant_name;
    private List<MerchantApplyDto> merchantApplyDtos;

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public List<MerchantApplyDto> getMerchantApplyDtos() {
        return merchantApplyDtos;
    }

    public void setMerchantApplyDtos(List<MerchantApplyDto> merchantApplyDtos) {
        this.merchantApplyDtos = merchantApplyDtos;
    }
}
