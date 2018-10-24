package com.jk51.modules.account.job;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.account.models.MerchantConfig;
import com.jk51.model.order.Trades;
import com.jk51.modules.account.controller.PayDataImportController;
import com.jk51.modules.account.mapper.MerchantConfigMapper;
import com.jk51.modules.account.service.ChargeOffService;
import com.jk51.modules.account.service.SettlementDetailService;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;

@Component
public class SchedulerTask {

    @Autowired
    private MerchantConfigMapper mapper;

    @Autowired
    private TradesMapper tradesMapper;

    @Autowired
    private ChargeOffService chargeOffService;

    @Autowired
    private SettlementDetailService settlementDetailService;

    private static final Logger logger = LoggerFactory.getLogger(SchedulerTask.class);


    /**
     * 出账单
     */
    public void generateBillingJob(){

        /*Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant,zone);
        LocalDate today = localDateTime.toLocalDate();*/

        LocalDate today = LocalDate.now();
        List<MerchantConfig> mlist=mapper.getAll();
        /*List<MerchantConfig> mlist=new ArrayList<MerchantConfig>();//测试代码，要删
        mlist.add(mapper.get(siteid));*/

        List<MerchantConfig> weekList= mlist.stream().filter(m->m.getSet_type() == 1)
                .filter(m->m.getSet_value().equals(String.valueOf(today.getDayOfWeek().getValue()))).collect(Collectors.toList());
        List<MerchantConfig> monthList= mlist.stream().filter(m->m.getSet_type() == 2)
                .filter(m->m.getSet_value().equals(String.valueOf(today.getDayOfMonth()))).collect(Collectors.toList());
        List<MerchantConfig> dayList= mlist.stream().filter(m->m.getSet_type() == 0).collect(Collectors.toList());
        logger.info("Billing Job Start weekmerchant{} monthmerchant{}"
                ,weekList.stream().map(s->s.getSite_id()).collect(Collectors.toList())
                ,monthList.stream().map(s->s.getSite_id()).collect(Collectors.toList()));
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        try {
            if(!weekList.isEmpty()){
            ForkJoinTask<List<ReturnDto>> week=
                    forkJoinPool.submit(new ParallelProcessUtils<MerchantConfig>(
                            weekList, s->{return chargeOffService.classifiedAccount(Integer.valueOf(s.getSite_id()),"system");}));
                logger.info("run result: week[{}]"
                        ,week.get().stream().map(s->s.getMessage()).collect(Collectors.toList())
                );
            }
            if(!monthList.isEmpty()) {
                ForkJoinTask<List<ReturnDto>> month =
                        forkJoinPool.submit(new ParallelProcessUtils<MerchantConfig>(
                                monthList, s -> {
                            return chargeOffService.classifiedAccount(Integer.valueOf(s.getSite_id()),"system");
                        }));
                logger.info("run result: month[{}] "
                        ,month.get().stream().map(s->s.getMessage()).collect(Collectors.toList())
                );
            }
            if(!dayList.isEmpty()) {
                ForkJoinTask<List<ReturnDto>> day =
                        forkJoinPool.submit(new ParallelProcessUtils<MerchantConfig>(
                                dayList, s -> {
                            return chargeOffService.classifiedAccount(Integer.valueOf(s.getSite_id()),"system");
                        }));
                logger.info("run result: day[{}]"
                        ,day.get().stream().map(s->s.getMessage()).collect(Collectors.toList())
                );
            }
            logger.info("not fund need account num");

        } catch (Exception e) {
            logger.error("Run Billing Job Error:",e);
        }finally {
            forkJoinPool.shutdown();
        }

    }

    public void accountException(){
        try {
            settlementDetailService.AccountException();
            logger.info("开始添加结算异常订单数据");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void autoAccount(){
        try {
            settlementDetailService.batchAccountChecking(null);
            logger.info("run auto account result:start");
        } catch (Exception e) {
            logger.error("Run Billing Job Error:",e);
        }
    }


    public String getBudgetDate(long orderid){
        Trades trades=tradesMapper.getTradesByTradesId(orderid);
        MerchantConfig config=mapper.get(trades.getSiteId());
        LocalDate localDate=LocalDate.now();
        LocalDate budgetDate = null;
        if(config.getSet_type() == 1){
            budgetDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.of(Integer.valueOf(config.getSet_value()))));
        }else{
            if(localDate.getDayOfMonth() < Integer.valueOf(config.getSet_value())){
                budgetDate = localDate.withDayOfMonth(Integer.valueOf(config.getSet_value()));
            }else{
                budgetDate = localDate.withDayOfMonth(Integer.valueOf(config.getSet_value())).plusMonths(1);
            }
        }
        return budgetDate.toString();
    }



}
