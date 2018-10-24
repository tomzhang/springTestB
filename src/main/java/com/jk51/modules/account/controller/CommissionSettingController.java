package com.jk51.modules.account.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.account.requestParams.CommissionParam;
import com.jk51.modules.account.service.CommissionSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * filename :com.jk51.modules.account.controller.
 * author   :zw
 * date     :2017/3/15
 * Update   :
 */
@RestController
@RequestMapping("commission")
public class CommissionSettingController {
    @Autowired
    private CommissionSettingService commissionSettingService;

    @ResponseBody
    @RequestMapping(value = "commissionSetting",consumes = "application/json")
    public ReturnDto settingCommission(@RequestBody CommissionParam commissionParam) {
        return commissionSettingService.settingCommission(commissionParam);
    }
}
