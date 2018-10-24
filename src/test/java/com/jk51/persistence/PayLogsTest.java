package com.jk51.persistence;

import com.github.pagehelper.PageInfo;
import com.jk51.Bootstrap;
import com.jk51.model.account.models.ClassifiedStatistic;
import com.jk51.model.account.models.PayDataImport;
import com.jk51.model.account.requestParams.ClassifiedAccountParam;
import com.jk51.model.account.requestParams.PayDataImportParams;
import com.jk51.modules.account.mapper.ClassifiedStatisticMapper;
import com.jk51.modules.account.mapper.PayDataImportMapper;
import com.jk51.modules.account.service.PayDataImportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class PayLogsTest {
    @Autowired
    private PayDataImportMapper payDataImportMapper;

    @Autowired
    private ClassifiedStatisticMapper classifiedStatisticMapper;

    @Autowired
    private PayDataImportService payDataImportService;
   /* @Test
    public void  getPayLog(){
        System.out.println("=====================");
        PayDataImportParams payDataImport=new PayDataImportParams();
        payDataImport.setTrades_id("1000011442303314952");
       List<PayDataImport> list=payDataImportMapper.getPayLogList(payDataImport);
       // PageInfo list=   payDataImportService.queryMemberList(payDataImport);
     System.out.println(list);
    }*/


    @Test
    public void getPayLog() {
        System.out.println("=====================");
        ClassifiedAccountParam classifiedAccountParam = new ClassifiedAccountParam();
        classifiedAccountParam.setFinanceNo("");
        //List<PayDataImport> list=payDataImportMapper.getPayLogList(payDataImport);
        //   List<ClassifiedStatistic> list = classifiedStatisticMapper.getClassifiedList(classifiedAccountParam);
      /*  list.forEach(p->{
            System.out.println("+=======================+"+p.getId());
        });

        System.out.println(list);
    }*/
    }
}