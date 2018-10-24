package com.jk51.service;

import com.jk51.Bootstrap;
import com.jk51.model.account.models.Finances;
import com.jk51.model.account.models.SettlementDetailAndTrades;
import com.jk51.modules.account.mapper.FinancesMapper;
import com.jk51.modules.account.mapper.MerchantConfigMapper;
import com.jk51.modules.account.mapper.SettlementDetailAndTradesMapper;
import com.jk51.modules.account.service.ChargeOffService;
import com.jk51.modules.index.mapper.StoresMapper;
import org.apache.tools.ant.taskdefs.Java;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

/**
 * filename :com.jk51.goodsService.
 * author   :zw
 * date     :2017/2/22
 * Update   :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class ChargeOffServiceTest {
    @Autowired
    private ChargeOffService chargeOffService;
    @Autowired
    private FinancesMapper financesMapper;
    @Autowired
    private MerchantConfigMapper merchantConfigMapper;
    @Autowired
    private SettlementDetailAndTradesMapper settlementDetailAndTradesMapper;
    @Test
    public void testClassifiedAccount(){
        System.out.println("11111111111111111111111111");
        chargeOffService.classifiedAccount(100205,"");
    }
    @Test
    public void getStartDay(){
        String s= "0.01";
        Double i = Double.parseDouble(s)*100;
        int c =(new   Double(i)).intValue();
        System.out.println(c+"---------");
     /*   int is_charge_off = 0;//是否可以出账
        List<SettlementDetailAndTrades> sdatList = settlementDetailAndTradesMapper.getSettlementListByTradesId(10001611);
        sdatList.stream()
                .filter(pdi -> pdi.getAccount_checking_status() != 1 ||
                (pdi.getRefund_checking_status() != 1 && pdi.getRefund_fee() > 0)).forEach(pdi->{
            System.out.println("------------>"+pdi.getTrades_id());
        });
        if (!sdatList.isEmpty() && null != sdatList) {
            is_charge_off = 1;
        }
        System.out.println("===========>"+is_charge_off);*/

       /* Date start_time = new java.util.Date();
        Date end_time = new java.util.Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        String str=sdf.format(start_time);
        SimpleDateFormat sdf1=new SimpleDateFormat("MMdd");
        String str1=sdf1.format(end_time);*/
      //  new java.sql.Date().toLocalDate().getMonth();

       // str1 = str1.replace("2017","");
       // System.out.println("=========>"+str1);

       // int random_number=(int)(Math.random()*900)+100;
       // System.out.println("1000611"+str+str1+random_number);
        //Date date = new Date();
        //Timestamp nousedate = new Timestamp(date.getTime());
    /*    Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();*/
     /*   LocalDate localDate = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        java.util.Date date = Date.from(instant);
        System.out.println(date);*/
      //  storesMapper.getStore(100016);
 /*       System.out.println(financesMapper.getStartDay(10001611).getSettlement_end_date());*/
    }

    @Test
    public void testFindFinances(){
        java.util.Date end_time = convertDate(LocalDate.now().plusDays(-1));
        Finances finances = financesMapper.getStartDayAndEndDay(100043, end_time, end_time);
        if(finances == null){
            System.out.println("------");
        }else {
            System.out.println(finances + "----------");
        }
    }
    private Date convertDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant start = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(start);
    }
}
