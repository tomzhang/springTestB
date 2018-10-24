package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.SettlementdayConfig;
import com.jk51.model.account.requestParams.SettingSettlementDayParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * filename :com.jk51.modules.account.mapper.
 * author   :zw
 * date     :2017/3/14
 * Update   :
 */
@Mapper
public interface SettlementdayConfigMapper {
    int addSettlementdayConfig(@Param(value = "settlementdayConfig") SettlementdayConfig settlementdayConfig);
    SettlementdayConfig selectSettlementDay(Integer site_id);
    int updateSettlementdayConfig(@Param(value = "settlementdayConfig") SettlementdayConfig settlementdayConfig);
    int updateConfig(@Param(value = "settlementdayConfig") SettlementdayConfig settlementdayConfig);

    int updateFianceTypeConfig(SettingSettlementDayParam settingSettlementDayParam);
}
