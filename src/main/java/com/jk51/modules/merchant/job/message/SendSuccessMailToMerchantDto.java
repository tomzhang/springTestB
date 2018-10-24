package com.jk51.modules.merchant.job.message;

import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.treat.MerchantExtTreat;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：
 * 作者: XC
 * 创建日期: 2018-09-30 16:41
 * 修改记录:
 **/
public class SendSuccessMailToMerchantDto {
    private YbMerchant merchant;
    private MerchantExtTreat merchantExt;
    private String seller_pwd;
    private String accepter;

    public YbMerchant getMerchant() {
        return merchant;
    }

    public void setMerchant(YbMerchant merchant) {
        this.merchant = merchant;
    }

    public String getSeller_pwd() {
        return seller_pwd;
    }

    public void setSeller_pwd(String seller_pwd) {
        this.seller_pwd = seller_pwd;
    }

    public String getAccepter() {
        return accepter;
    }

    public void setAccepter(String accepter) {
        this.accepter = accepter;
    }

    public MerchantExtTreat getMerchantExt() {
        return merchantExt;
    }

    public void setMerchantExt(MerchantExtTreat merchantExt) {
        this.merchantExt = merchantExt;
    }
}
