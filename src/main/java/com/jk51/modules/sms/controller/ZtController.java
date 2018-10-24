package com.jk51.modules.sms.controller;

import com.jk51.modules.sms.service.CommonService;
import com.jk51.modules.sms.service.DjSmsService;
import com.jk51.modules.sms.service.ZtSmsService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-21
 * 修改记录:
 */
@Controller
@RequestMapping("/ztsms")
public class ZtController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZtController.class);
    @Autowired
    ZtSmsService ztSmsService;
    @Autowired
    private CommonService commonService;

    /**
     * @param produceNumber 产品ID
     * @param oldPhone      要发送信息的手机号码
     * @return 返回0，发送成功，返回-1，发送失败
     */

    @ResponseBody
    @RequestMapping("/tpl_service")
    public String SendMessage(Integer siteId, String produceNumber, String oldPhone, Integer type) {
        try {
            String[] phones = oldPhone.split(",");
            for (String phone : phones) {
                commonService.sendErrorMessage(siteId, produceNumber, phone, type);
            }
            return "0";
        } catch (Exception e) {
            LOGGER.debug("调用短信接口失败" + e.getMessage());
            return "-1";
        }
    }
}
