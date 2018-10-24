package com.jk51.account;

import com.jk51.Bootstrap;
import com.jk51.model.account.models.PayDataImport;
import com.jk51.model.account.models.SettlementDetailAndTrades;
import com.jk51.model.order.Refund;
import com.jk51.modules.account.mapper.PayDataImportMapper;
import com.jk51.modules.account.mapper.SettlementDetailMapper;
import com.jk51.modules.account.service.ChargeOffService;
import com.jk51.modules.account.service.SettlementDetailService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.apache.catalina.LifecycleState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试
 *
 * @auhter admin
 * @create 2017-05-19 14:34
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class TestTest {

    @Autowired
    private SettlementDetailService settlementDetailService;

    @Autowired
    private ChargeOffService chargeOffService;

    @Autowired
    private TradesMapper tradesMapper;

    @Autowired
    private CouponSendService couponSendService;

    private SettlementDetailMapper settlementDetailMapper;

    @Autowired
    private PayDataImportMapper payDataImportMapper;

    public static void main(String[] args)throws Exception{

        List<String> aaa=new ArrayList<>();
        aaa.add("1001661507712395033");
        aaa.add("1001661507810914441");
        aaa.add("1001661507808079334");

        List<String> bbb=new ArrayList<>();
        bbb.add("1001661507529936078");
        bbb.add("1001661506844266675");
        bbb.add("1001661507808079334");
        bbb.add("1001661507531265782");

        List<String> unionRefunds = aaa.stream().filter(r->{
            return bbb.stream().filter(t->t.equals(r)).count()<=0;
        }).collect(Collectors.toList());

        System.out.println(unionRefunds);


        /*double m=12345.1;//分
        System.out.println(new BigDecimal(m).setScale(0, BigDecimal.ROUND_HALF_UP));*/
        /*LocalDate today = LocalDate.now();
        int aa=today.getDayOfWeek().getValue();
        System.out.println(aa);*/
//        String string = "2016-10-24 21:59:06";
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        System.out.println(sdf.parse(string));
//            DateUtils.formatDate()
//        DateUtils.convert("2017-07-01 21:00:01","yyyy-MM-dd,HH:mm:ss");
//        System.out.println(Timestamp.valueOf(sdf.parse(string)+""));
//        String aaa="123,456,789,147";

    }

    @Test//对账
    public void test(){
        System.out.println("******************************开始对账******************************");
        settlementDetailService.AccountException();
        settlementDetailService.batchAccountChecking(null);
//        List<Trades> list = settlementDetailMapper.selectTradesByMigrate();
    }

    @Test//账单汇总
    public void testClassifiedAccount(){
        System.out.println("******************************账单汇总******************************");
        chargeOffService.classifiedAccount(100097,null,null);
    }

    @Test
    public void getFinanceNoByTrades(){
         String financeNo=tradesMapper.getFinanceNoByTradesId("1000971497269502139");
        System.out.println(financeNo);
    }

    @Test
    public void testNotifySendCoupons(){
        System.out.println("******************************测试派券提醒******************************");
        couponSendService.notifySendCoupons(100190,1010,3);
    }

    @Test//账单汇总
    public void testNewFinance(){
        System.out.println("******************************新结算系统******************************");
        chargeOffService.classifiedAccount(100043,"system");
    }
    @Test//账单汇总
    public void testCheckedMoney(){
        System.out.println("******************************新结算系统******************************");
        settlementDetailMapper.findCheckedMoney("aaaa");
    }

    @Test
    public void qaq(){
        /*List<PayDataImport> aa=payDataImportMapper.selectIncmeList("100043201801180124447");
        List<PayDataImport> bb=payDataImportMapper.selectSpendingList("100043201801180124447");*/
        /*aa.addAll(bb);
        Map<String,PayDataImport> cc=aa.stream().collect(Collectors.toMap(PayDataImport::getTrades_id,a->a,(k1,k2)->k2));
        Integer income_amount=cc.values().stream().mapToInt(l->Integer.valueOf(l.getIncome_amount())).sum();
        Integer spending_amount=cc.values().stream().mapToInt(l->Integer.valueOf(l.getSpending_amount())).sum();*/
        /*List<PayDataImport> unionRefunds = aa.stream().filter(r->{
            return bb.stream().filter(t->t.getTrades_id().equals(r.getTrades_id())).count()>1;
        }).collect(Collectors.toList());*/
//        System.out.println("付款："+income_amount+",spending_amount"+spending_amount);
    }

}
