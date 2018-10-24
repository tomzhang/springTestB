package com.jk51.modules.account.service;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.interceptor.LoginInterceptor;
import com.jk51.model.account.models.SettlementdayConfig;
import com.jk51.model.account.requestParams.SettingSettlementDayParam;
import com.jk51.modules.account.mapper.SettlementdayConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * filename :com.jk51.modules.account.goodsService.
 * author   :zw
 * date     :2017/3/14
 * Update   :
 */
@Service
public class SettlementDayService {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    @Autowired
    private SettlementdayConfigMapper settlementdayConfigMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReturnDto settingSettlementDay(SettingSettlementDayParam settingSettlementDayParam) {

        Integer id = judgeConfig(settingSettlementDayParam);
        if (id > 0) {
            try {
                settingSettlementDayParam.setId(id);
                settlementdayConfigMapper.updateSettlementdayConfig(processBin(settingSettlementDayParam));
            } catch (Exception e) {
                logger.error("站点id：" + settingSettlementDayParam.getSite_id() + "修改结算日失败，原因：" + e);
                return ReturnDto.buildFailedReturnDto("站点id：" + settingSettlementDayParam.getSite_id() + "修改结算日失败，原因：" + e);
            }
        } else {
            try {
                settlementdayConfigMapper.addSettlementdayConfig(processBin(settingSettlementDayParam));
            } catch (Exception e) {
                logger.error("站点id：" + settingSettlementDayParam.getSite_id() + "创建结算日失败，原因：" + e);
                return ReturnDto.buildFailedReturnDto("站点id：" + settingSettlementDayParam.getSite_id() + "创建结算日失败，原因：" + e);
            }
        }

        return ReturnDto.buildSuccessReturnDto("settlement day create or update success");
    }

    private int judgeConfig(SettingSettlementDayParam settingSettlementDayParam) {
        SettlementdayConfig settlementdayConfig = settlementdayConfigMapper.selectSettlementDay(settingSettlementDayParam.getSite_id());
        if (null == settlementdayConfig) {
            return 0;
        }
        return settlementdayConfig.getId();
    }

    private SettlementdayConfig processBin(SettingSettlementDayParam settingSettlementDayParam) {
        SettlementdayConfig settlementdayConfig = new SettlementdayConfig();
        if (settingSettlementDayParam.getSite_id() > 0) {
            settlementdayConfig.setId(settingSettlementDayParam.getId());
        }
        settlementdayConfig.setSite_id(settingSettlementDayParam.getSite_id());
        settlementdayConfig.setSet_type(settingSettlementDayParam.getSet_type());
        settlementdayConfig.setSet_value(settingSettlementDayParam.getSet_value());
        return settlementdayConfig;
    }
//     更新结算方式 0以结束状态结算；1以付款状态结算
    public ReturnDto settingFianceType(SettingSettlementDayParam settingSettlementDayParam) {
         int  result = settlementdayConfigMapper.updateFianceTypeConfig(settingSettlementDayParam);
         if (result==1){
           return ReturnDto.buildSuccessReturnDto("保存成功！");
         }
         return ReturnDto.buildFailedReturnDto("保存失败！");
    }
}
