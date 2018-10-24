package com.jk51.modules.clerkvisit.service;

import com.jk51.Bootstrap;
import com.jk51.model.order.Trades;
import com.jk51.modules.clerkvisit.job.VisitSchedule;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/11/29.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class BClerkVisitServiceTest {
    @Autowired
    private BClerkVisitService bClerkVisitService;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private VisitSchedule visitSchedule;

    @Test
    public void test(){
        String  id="1001901511338971027";
        Trades trades=tradesMapper.getTradesByTradesId(Long.valueOf(id));
        bClerkVisitService.addVisitTask(trades);
    }



    @Test
    public void run(){
        //visitSchedule.visitSchedule();
    }
}
