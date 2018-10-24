package com.jk51.modules.appInterface.util;

import com.jk51.commons.ccprest.result.BaseResult;
import com.jk51.commons.ccprest.result.BaseResultEnum;
import com.jk51.modules.sms.service.CommonService;
import com.jk51.modules.sms.service.Sms7MoorService;
import com.jk51.modules.sms.service.YpSmsService;
import com.jk51.modules.sms.service.ZtSmsService;
import com.jk51.modules.sms.smsConfig.SmsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-05-25
 * 修改记录:
 */
@Service
public class SendSMS {

    @Autowired
    private YpSmsService ypService;
    @Autowired
    private ZtSmsService ztSmsService;
    @Autowired
    private Sms7MoorService _7moorSmsService;
    @Autowired
    private CommonService commonService;


    /**
     * 发送短信验证码
     *
     * @param phone
     * @param code
     */
    public Integer sendSMS(Integer siteId, String phone, String code, Integer type) {
     /*   String word = "【51健康】51健康提示，亲爱的用户，验证码是" + code + "。如非本人操作，请忽略本短信。";
        String status = ztSmsService.SendMessage(siteId, word, phone, type, type);
        if (!"0".equals(status)) {
            Integer result = ypService.SendMessage(siteId, 1402527, phone, code, type);
            if (result != 0) {
                return -1;
            }
        }
        return 0;*/
        return Integer.parseInt(commonService.SendMessage(
            commonService.transformParam(siteId, phone,  type, type,SmsEnum.VERIFY_CODE,code,"51健康")));
    }

    /**
     * 发送语音验证码
     *
     * @
     */
    public Map<String, Object> webcall(String phone, String msg) {

        BaseResult baseResult = _7moorSmsService._7MoorWebCall(phone, msg);

        if (baseResult.getCode() == BaseResultEnum.SUCCESS.getCode()) {
            return ReturnMap.buildSuccessReturnMap();
        } else {
            return ReturnMap.buildFailReturnMap("发送语音验证码失败");
        }
    }
}
