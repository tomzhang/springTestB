package com.jk51.service;

import com.github.pagehelper.PageInfo;
import com.jk51.Bootstrap;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.account.models.SettlementDetailAndTrades;
import com.jk51.model.account.requestParams.AccountParams;
import com.jk51.modules.account.controller.AccountController;
import com.jk51.modules.account.mapper.SettlementDetailAndTradesMapper;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.wechat.service.WechatMemberService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.goodsService.
 * author   :zw
 * date     :2017/3/11
 * Update   :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class AccountServiceTest {
    @Autowired
    private SettlementDetailAndTradesMapper settlementDetailAndTradesMapper;

    @Autowired
    private AccountController accountController;
    @Test
    public void getList(){
        AccountParams accountParams = new AccountParams();

        //accountParams.setSellerId(10001611);
     // Map<String,Object> list=  accountController.accountDetail(accountParams, 1, 15);
      /*  list.forEach(p->{
            System.out.println("------"+p.getTrades_id());
        });*/
      //  System.out.println("------------"+list.get("result"));

      /* List<SettlementDetailAndTrades> list1 = settlementDetailAndTradesMapper.getSettlementListByObj(accountParams);
        list1.forEach(p->{System.out.println("--------------"+p.getTrades_id());});*/

    }
    @Autowired
    private WechatMemberService wechatMemberService;

    @Test
    public void testRegister(){
//        wechatMemberService.loginByUnameAndVcode(100073,"12111111111","");
    }
    @Autowired
    private CouponSendService couponSendService;
    @Test
    public void testSendCouponDirect(){
       // couponSendService.sendClerk(100073,159);
    }
}
