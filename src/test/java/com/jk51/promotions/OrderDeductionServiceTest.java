package com.jk51.promotions;

import com.jk51.Bootstrap;
import com.jk51.model.coupon.requestParams.CouponFilterParams;
import com.jk51.modules.promotions.request.OrderDeductionDto;
import com.jk51.modules.promotions.service.OrderDeductionService;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import org.apache.commons.collections.map.HashedMap;
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
 * date     :2017/8/14
 * Update   :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class OrderDeductionServiceTest {
    @Autowired
    private OrderDeductionService orderDeductionService;
    @Autowired
    private PromotionsFilterService promotionsFilterService;
    @Autowired
    private PromotionsRuleService promotionsRuleService;

    @Test
    public void testCountDeduction () throws Exception {
        OrderDeductionDto orderDeductionDto = new OrderDeductionDto();
        Map<String, Integer> map = new HashedMap();
        map.put("goodsId", 489);
        map.put("num", 1);
        map.put("goodsPrice", 1);
        Map<String, Integer> map1 = new HashedMap();
        map1.put("goodsId", 45331);
        map1.put("num", 1);
        map1.put("goodsPrice", 1990);
        List<Map<String, Integer>> list = new ArrayList<>();
        list.add(map);
        list.add(map1);
        Map<String, Integer> map3 = new HashedMap();
        map3.put("0", 12);
        map3.put("1", 13);
        map3.put("2", 14);
        orderDeductionDto.setPromotionsIdsMap(map3);
        orderDeductionDto.setSiteId(100073);
        orderDeductionDto.setPostFee(0);
        orderDeductionDto.setOrderFee(1);
        orderDeductionDto.setGoodsInfo(list);
        // List<UsePromotionsParams> usePromotionsParamss = orderDeductionService.countDeduction(orderDeductionDto);
        // System.out.println("zwresult-------" + JSON.parse(usePromotionsParamss.toString()));
    }

    @Test
    public void testFilterByGoodsId () {
        CouponFilterParams couponFilterParams = new CouponFilterParams();
        couponFilterParams.setSiteId(100190);
        couponFilterParams.setUserId(15232723);
        couponFilterParams.setGoodsId(605 + "");
        promotionsFilterService.filterByGoodsId(couponFilterParams);
    }

    @Test
    public void testSplitOrder () {
        List<Map<String, Integer>> goodsList = new ArrayList<Map<String, Integer>>();
        Map map1 = new HashMap<String, Integer>();
        map1.put("goodsId", 11111);
        map1.put("goodsPrice", 12);
        map1.put("goodsNum", 1);
        goodsList.add(map1);

        Map map2 = new HashMap<String, Integer>();
        map2.put("goodsId", 2222);
        map2.put("goodsPrice", 22);
        map2.put("goodsNum", 2);
        goodsList.add(map2);

        Map map3 = new HashMap<String, Integer>();
        map3.put("goodsId", 3333);
        map3.put("goodsPrice", 33);
        map3.put("goodsNum", 3);
        goodsList.add(map3);

        Map map4 = new HashMap<String, Integer>();
        map4.put("goodsId", 4444);
        map4.put("goodsPrice", 44);
        map4.put("goodsNum", 4);
        goodsList.add(map4);

        Map map5 = new HashMap<String, Integer>();
        map5.put("goodsId", 5555);
        map5.put("goodsPrice", 55);
        map5.put("goodsNum", 5);
        goodsList.add(map5);

//        List<Map<String, List<Map<String, Integer>>>> result = promotionsRuleService.goodsListInfoGroup(goodsList);
        int a=1;

    }
}
