package com.jk51.account;

import com.jk51.Bootstrap;
import com.jk51.model.account.requestParams.ClassifiedAccountParam;
import com.jk51.modules.account.controller.ClassifiedStatisticController;
import com.jk51.modules.account.mapper.ClassifiedStatisticMapper;
import com.jk51.modules.account.service.SettlementDetailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/13-03-13
 * 修改记录 :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class ClassifiedStatisticTest {

    @Autowired
    private ClassifiedStatisticMapper classifiedStatisticMapper;
    @Autowired
    private ClassifiedStatisticController classifiedStatisticController;

    @Autowired
    private SettlementDetailService settlementDetailService;

    @Test
    public void getList(){
     ClassifiedAccountParam classifiedAccountParam=new ClassifiedAccountParam();
        classifiedAccountParam.setFinanceNo("10001611201703030222832");
      /*     List<FinancesStatistic>list=classifiedStatisticMapper.getClassifiedList(classifiedAccountParam);
        list.parallelStream().forEach(p->{
            System.out.println("+++++++++++++++++++++++++++"+classifiedAccountParam.getFinance_no());
        });*/


        /*Map<String,Object> result= classifiedStatisticController.queryClassifiedStatistic(classifiedAccountParam,1,29);
        System.out.println(result.toString());*/
    }

    @Test
    public void test(){
        settlementDetailService.batchAccountChecking(null);
    }


}
