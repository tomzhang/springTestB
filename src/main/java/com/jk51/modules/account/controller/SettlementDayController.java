package com.jk51.modules.account.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.account.models.SettlementdayConfig;
import com.jk51.model.account.requestParams.SettingSettlementDayParam;
import com.jk51.modules.account.service.SettlementDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * filename :com.jk51.modules.account.controller.
 * author   :zw
 * date     :2017/3/14
 * Update   :
 */
@RestController
@RequestMapping("settlementDay")
public class SettlementDayController {

    @Autowired
    private SettlementDayService settlementDayService;

    @ResponseBody
    @PostMapping(value = "/settingSettlementDay", consumes = "application/json")
    public ReturnDto settingSettlementDay(@RequestBody SettingSettlementDayParam settingSettlementDayParam) {
        return settlementDayService.settingSettlementDay(settingSettlementDayParam);
    }

    @ResponseBody
    @PostMapping(value = "/settingFianceType", consumes = "application/json")
    public ReturnDto settingFianceType(@RequestBody SettingSettlementDayParam settingSettlementDayParam) {
        return settlementDayService.settingFianceType(settingSettlementDayParam);
    }
}
