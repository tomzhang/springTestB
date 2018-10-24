package com.jk51.promotions;

import com.fasterxml.jackson.databind.node.IntNode;
import com.jk51.Bootstrap;
import com.jk51.modules.promotions.request.OrderCombinationsParam;
import com.jk51.modules.promotions.service.OrderCombinationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.promotions.
 * author   :zw
 * date     :2017/11/2
 * Update   :
 */
/*@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")*/
public class OrderCombinationServiceTest {
    @Autowired
    private OrderCombinationService orderCombinationService;
    @Test
    public void test(){
        List<OrderCombinationsParam> ocpl = new ArrayList<>();
        OrderCombinationsParam orderCombinationsParam1 = new OrderCombinationsParam();
        orderCombinationsParam1.setGoodsId(1);
        ocpl.add(orderCombinationsParam1);
        OrderCombinationsParam orderCombinationsParam2 = new OrderCombinationsParam();
        orderCombinationsParam2.setGoodsId(2);
        ocpl.add(orderCombinationsParam2);
        OrderCombinationsParam orderCombinationsParam3 = new OrderCombinationsParam();
        orderCombinationsParam3.setGoodsId(3);
        ocpl.add(orderCombinationsParam3);
        OrderCombinationsParam orderCombinationsParam4 = new OrderCombinationsParam();
        orderCombinationsParam4.setGoodsId(4);
        ocpl.add(orderCombinationsParam4);
        OrderCombinationsParam orderCombinationsParam5 = new OrderCombinationsParam();
        orderCombinationsParam5.setGoodsId(5);
        ocpl.add(orderCombinationsParam5);
        orderCombinationService.divideIntoGroupsByDiscount(ocpl);
    }
@Test
    public void test1 (){
        Map<Integer,String> map = new HashMap();
        ArrayList arrayList = new ArrayList();
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        arrayList.add(4);
        arrayList.add(5);
        ArrayList arrayList1 = new ArrayList();
        String startGoodsId = "";
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList1.addAll(dg((Integer) arrayList.get(i), i, arrayList));
        }
        Integer i = 0;
    }
    public ArrayList dg(Integer goodsId,Integer z, ArrayList arrayList){
        ArrayList arrayList1 = new ArrayList();
        String startGoodsId = "";
        for (int i = z; i < arrayList.size(); i++) {
            startGoodsId += arrayList.get(i)+",";
            for (int j = i+1; j < arrayList.size(); j++) {
                arrayList1.add(startGoodsId+arrayList.get(j));
            }
        }
        return arrayList1;
    }
}
