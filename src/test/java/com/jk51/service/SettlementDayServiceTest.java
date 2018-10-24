package com.jk51.service;

import com.jk51.Bootstrap;
import com.jk51.model.account.models.SettlementDetail;
import com.jk51.model.account.models.SettlementdayConfig;
import com.jk51.modules.account.mapper.SettlementDetailMapper;
import com.jk51.modules.account.mapper.SettlementdayConfigMapper;
import com.jk51.modules.account.service.SettlementDetailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * filename :com.jk51.goodsService.
 * author   :zw
 * date     :2017/3/15
 * Update   :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class SettlementDayServiceTest {
    private static final Logger log = LoggerFactory.getLogger(PayServiceTest.class);
    @Autowired
    private SettlementdayConfigMapper settlementdayConfigMapper;
    @Test
    public void addSettlementDay() throws Exception{
        SettlementdayConfig settlementdayConfig = new SettlementdayConfig();
        settlementdayConfig.setSite_id(1001102);
        settlementdayConfig.setSet_type(1);
        settlementdayConfig.setSet_value("15");
        int i= settlementdayConfigMapper.addSettlementdayConfig(settlementdayConfig);
    }
    @Test
    public void selectSettlementDay(){
        SettlementdayConfig settlementdayConfig = new SettlementdayConfig();
        settlementdayConfig.setSite_id(1001102);
        settlementdayConfig.setId(206);
        settlementdayConfig.setSet_type(2);
        settlementdayConfig.setSet_value("7");
        int i= settlementdayConfigMapper.updateSettlementdayConfig(settlementdayConfig);
    }
    @Autowired
    private SettlementDetailMapper settlementDetailMapper;
    @Test
    public void testSettlementDetail(){
        SettlementDetail settlementDetail = new SettlementDetail();
        settlementDetail.setTrades_id(1111111111111l);
        settlementDetail.setPay_style("wx");
        settlementDetail.setPay_number("1111111111111");
        settlementDetail.setAccount_checking_status(1);
        settlementDetail.setBusiness_types("222222222");
        settlementDetail.setRefund_fee(1);
        settlementDetail.setRefund_checking_status(1);
        settlementDetail.setPlatform_payment_amount(1);
        settlementDetail.setPlatform_fashionable_amount(1);
        settlementDetail.setPlatform_service_fee(1);
        settlementDetail.setPlatform_refund_fee(1);
        settlementDetail.setAccount_status("222");
        settlementDetailMapper.addSettlementDetail(settlementDetail);
    }

}
