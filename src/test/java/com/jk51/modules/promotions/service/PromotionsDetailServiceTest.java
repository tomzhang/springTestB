package com.jk51.modules.promotions.service;

import com.jk51.Bootstrap;
import com.jk51.model.promotions.PromotionsDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/9                                <br/>
 * 修改记录:                                         <br/>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class PromotionsDetailServiceTest {
    @Autowired
    private PromotionsDetailService service;

    @Test
    public void insert () throws Exception {
        PromotionsDetail promotionsDetail = new PromotionsDetail();
        promotionsDetail.setActivityId(1);
        promotionsDetail.setCreateTime(LocalDateTime.now());
        promotionsDetail.setOrderId("瞎写的");
        promotionsDetail.setPromotionsNo("瞎写的");
        promotionsDetail.setRefundTime(promotionsDetail.getCreateTime());
        promotionsDetail.setRuleId(1);
        promotionsDetail.setSiteId(100073);
        promotionsDetail.setStatus(0);
        promotionsDetail.setUpdateTime(promotionsDetail.getCreateTime());
        service.insert(promotionsDetail);
    }

    @Test
    public void test () {
        List<String> list1 = new ArrayList<String>();
        list1.add("1111");
        list1.add("2222");
        list1.add("3333");
        list1.add("4444");

        List<String> list2 = new ArrayList<String>();
        list2.add("1111");
        list2.add("2222");
        list2.add("5555");

        List<String> list3 = new ArrayList<String>();
        list3.add("1111");
        list3.add("2222");

        List<String> list4 = new ArrayList<String>();
        list4.add("5555");
        list4.add("6666");

        boolean b1 = Collections.disjoint(list1, list2);
        boolean b2 = Collections.disjoint(list2, list1);
        boolean b3 = list1.containsAll(list2);
        boolean b4 = list2.containsAll(list1);

        boolean b5 = Collections.disjoint(list1, list3);
        boolean b6 = Collections.disjoint(list3, list1);
        boolean b7 = list1.containsAll(list3);
        boolean b8 = list3.containsAll(list1);

        boolean b9 = Collections.disjoint(list1, list4);
        boolean b10 = Collections.disjoint(list4, list1);
        boolean b11 = list1.containsAll(list4);
        boolean b12 = list4.containsAll(list1);
    }

}
