package com.jk51.service;

import com.jk51.Bootstrap;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.account.models.AccountCommissionRate;
import com.jk51.model.account.models.PayPlatform;
import com.jk51.model.account.requestParams.AccountParams;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.requestParams.CouponView;
import com.jk51.model.coupon.requestParams.TimeRule;
import com.jk51.model.promotions.rule.ProCouponRuleView;
import com.jk51.modules.account.mapper.AccountCommissionRateMapper;
import com.jk51.modules.account.mapper.PayPlatformMapper;
import com.jk51.modules.account.mapper.SettlementDetailAndTradesMapper;
import com.jk51.modules.account.service.SettlementDetailAndTradesService;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.service.CouponRuleService;
import com.jk51.modules.coupon.service.ParsingCouponRuleService;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import org.apache.ibatis.annotations.Arg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * filename :com.jk51.goodsService.
 * author   :zw
 * date     :2017/3/16
 * Update   :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class AccountCommissionServiceTest {
    @Autowired
    private AccountCommissionRateMapper accountCommissionRateMapper;
    @Autowired
    private PayPlatformMapper payPlatformMapper;
    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private PromotionsRuleService promotionsRuleService;

    @Test
    public void teset123(){
       String  orderId="1000301494229875014";
        CouponDetail couponDetail = couponDetailMapper.findCouponDetailByOrderId(orderId);
    }


    @Test
    public void testAddCommission() throws Exception{
      /*  AccountCommissionRate accountCommissionRate = new AccountCommissionRate();
        accountCommissionRate.setSite_id(1004);
        accountCommissionRate.setDirect_purchase_rate(0.03);
        accountCommissionRate.setDistributor_rate(0.04);
        accountCommissionRate.setShipping_fee_rate(0.05);*/
        //accountCommissionRateMapper.addAccount(accountCommissionRate);
      /*  PayPlatform payPlatform = new PayPlatform();
        payPlatform.setSite_id(10002);
        payPlatform.setPay_type("wx");
        payPlatform.setProcedure_fee(0.01);*/
     //   payPlatformMapper.getPayPlatformById(10002,"wx");
        TimeRule timeRule = new TimeRule();
        timeRule.setValidity_type(3);
        timeRule.setAssign_type(1);
        timeRule.setAssign_rule("1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31,");
        String str = JacksonUtils.obj2json(timeRule);
        System.out.println("--------"+str);
    }
    @Test
    public void testRule(){
       CouponView couponView =  parsingCouponRuleService.accountCoupon(2000, 100190);
       String promotionsRule="{\"goodsIdsType\":\"1\",\"goodsIds\":\"259498,259497,226613\",\"isMl\":2,\"isRound\":0,\"ruleType\":\"1\",\"rules\":[{\"direct_discount\":85,\"goods_money_limit\":0}]}";
        ProCouponRuleView aa=promotionsRuleService.promotionsRuleForType(20,promotionsRule);
        System.out.println("---------"+aa.getProruleDetail());
        System.out.println("---------"+couponView.getRuleDetail());
        System.out.println("---------"+couponView.getIsAllType());
        System.out.println("---------"+couponView.getMaxDiscount());
        System.out.println("---------"+couponView.getMaxMoney());
    }
    @Autowired
    SettlementDetailAndTradesMapper settlementDetailAndTradesMapper;
    @Test
    public void testStore(){
        AccountParams accountParams = new AccountParams();
        settlementDetailAndTradesMapper.getClerkSettlementListByObj(accountParams).forEach(p->{System.out.println(p+"---");});
    }

    @Autowired
    private SettlementDetailAndTradesService settlementDetailAndTradesService;

    @Test
    public void testExcel(){
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("payType","cash");
//        settlementDetailAndTradesService.queryGetSettlementListByObjExport(param);
    }

}
