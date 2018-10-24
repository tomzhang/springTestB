package com.jk51.modules.account.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.account.requestParams.DealTimeParam;
import com.jk51.modules.account.service.SettingDealTimeService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * filename :com.jk51.modules.account.controller.
 * author   :zw
 * date     :2017/3/17
 * Update   :
 * 设置交易时间
 */
@RestController
@RequestMapping("settingDealTime")
public class SettingDealTime {
    @Autowired
    private SettingDealTimeService settingDealTimeService;

    @ResponseBody
    @RequestMapping(value = "commissionSetting", consumes = "application/json")
    public ReturnDto commissionSetting(@RequestBody DealTimeParam dealTimeParam) {
        return  settingDealTimeService.settingDealTime(dealTimeParam);
    }




}
