package com.jk51.modules.merchant.utils;

import com.jk51.commons.sms.ServiceType;
import com.jk51.commons.sms.SysType;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.treat.MerchantExtTreat;
import com.jk51.modules.merchant.job.message.SendSuccessMailToMerchantDto;
import com.jk51.modules.sms.service.CommonService;
import com.jk51.modules.sms.smsConfig.SmsEnum;
import com.jk51.modules.sms.smsConfig.SmsParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：
 * 作者: XC
 * 创建日期: 2018-10-18 13:46
 * 修改记录:
 **/
@Component
public class MessageForApply {
    @Autowired
    private CommonService commonService;

    public static final String CONTACTS = "冯经理";
    public static final String CONTACT_NUMBER = "021-65798989转826";
    public static final String APPLY_FOR_LINK = "http://www.51jk.com/apply.html";
    public void sendFailMessageToMerchant(YbMerchant merchant,String reason){
        String[] args = new String[]{merchant.getLegal_name(),reason,APPLY_FOR_LINK,CONTACTS,CONTACT_NUMBER};
        SmsParams smsParams = new SmsParams(merchant.getMerchant_id(),merchant.getLegal_mobile(),ServiceType.TO_BACKSTAGE,SysType.APPLY_FAIL,SmsEnum.APPLY_FAIL,args);
        commonService.SendMessage(smsParams);
    }
    public void sendSuccessMessageToMerchant(YbMerchant merchant, MerchantExtTreat merchantExt,String pwd){
        String[] args = new String[]{merchant.getLegal_name(),merchant.getShop_url(),pwd,merchantExt.getStore_url(),merchant.getShopwx_url(),CONTACTS,CONTACT_NUMBER};
        SmsParams smsParams = new SmsParams(merchant.getMerchant_id(),merchant.getLegal_mobile(),ServiceType.TO_BACKSTAGE,SysType.APPLY_SUCCESS,SmsEnum.APPLY_SUCCESS,args);
        commonService.SendMessage(smsParams);
    }
}
