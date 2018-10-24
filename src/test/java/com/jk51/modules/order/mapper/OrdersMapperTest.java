package com.jk51.modules.order.mapper;

import com.alibaba.fastjson.JSON;
import com.jk51.model.order.Orders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ztq on 2018/3/3
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class OrdersMapperTest {
    @Autowired
    private OrdersMapper ordersMapper;

    @Test
    public void queryOrdersUsePromotions() {
        List<Orders> ordersList = null;//ordersMapper.queryOrdersUsePromotions(100190, 10, 10);
        Map<Integer, Integer> map = ordersList.stream()
            .collect(Collectors.toMap(Orders::getGoodsId, Orders::getGoodsNum, (i, j) -> i + j));
        System.out.println(JSON.toJSONString(ordersList));
        System.out.println(map);
    }
}
