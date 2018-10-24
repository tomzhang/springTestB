package com.jk51.order;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.jk51.Bootstrap;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.grouppurchase.GroupPurchase;
import com.jk51.model.order.*;
import com.jk51.model.order.response.DistributeResponse;
import com.jk51.modules.order.service.DistributeOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: baixiongfei
 * 创建日期: 2017/3/6
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class DistributeOrderServiceTest {


    @Autowired
    private DistributeOrderService distributeOrderService;

    /**
     * 用户下单之前查询送货上门的运费信息及订单的价格信息
     */
    /*@Test
    public void testBeforeOrder(){

        List<OrderGoods> orderGoodss = new ArrayList<OrderGoods>();
        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setGoodsId(173);
        orderGoods.setGoodsNum(3);
        orderGoodss.add(orderGoods);

        BeforeCreateOrderReq beforeCreateOrderReq = new BeforeCreateOrderReq();
        beforeCreateOrderReq.setSiteId(100090);
        beforeCreateOrderReq.setBuyerId(36234);
        beforeCreateOrderReq.setAddrCode("131081");
        beforeCreateOrderReq.setMobile("138171075008");
        beforeCreateOrderReq.setStoresId(323143);
        beforeCreateOrderReq.setOrderGoods(orderGoodss);
        beforeCreateOrderReq.setCouponId(232);
        beforeCreateOrderReq.setIntegralUse(10);
        beforeCreateOrderReq.setOrderType("1");

        DistributeResponse respnose = distributeOrderService.beforeOrder(beforeCreateOrderReq,"2");
        try {
            ReturnDto dto = new ReturnDto();
            dto.setCode("0000");
            dto.setMessage("success");
            dto.setValue(JacksonUtils.obj2jsonIgnoreNull(respnose));
            System.out.println(JacksonUtils.obj2json(beforeCreateOrderReq));
            System.out.println(JacksonUtils.obj2json(dto));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


   /* @Test
    public void testBeforeQuery(){
       BeforeCreateOrderReq bq = new BeforeCreateOrderReq();
       bq.setSiteId(100190);
       bq.setStoresId(0);
       bq.setUserId(15234298);
       bq.setBuyerId(769269);
       bq.setMobile("17600101016");
       bq.setIntegralUse(0);
       bq.setOrderGoods(new ArrayList<OrderGoods>(){{
           OrderGoods orderGoods = new OrderGoods();
           orderGoods.setGoodsId(209);
           orderGoods.setGoodsNum(1);
           orderGoods.setGoodsPrice(1400);
           add(orderGoods);
       }});
       bq.setGroupPurchaseJson("{\"proActivityId\":890,\"goodsId\":209}");
        GroupPurchase gp =new GroupPurchase();
        gp.setProActivityId(890);
        gp.setGoodsId(209);
       bq.setGroupPurchase(gp);
        distributeOrderService.beforeOrder(bq,"2");
    }*/


    @Test
    public void testBeforeQuery2(){
      //普通活动+优惠券
        BeforeCreateOrderReq bq = new BeforeCreateOrderReq();
        bq.setSiteId(100190);
        bq.setStoresId(1899);
        bq.setBuyerId(769269);
        bq.setUserId(15234298);
        bq.setMobile("17600101016");
        bq.setIntegralUse(0);
        bq.setCouponId(2647586);
        bq.setOrderType("2");
        bq.setOrderGoods(new ArrayList<OrderGoods>(){{
            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setGoodsId(213);
            orderGoods.setGoodsNum(1);
            orderGoods.setGoodsPrice(2200);
            add(orderGoods);
        }});
        DistributeResponse distributeResponse = distributeOrderService.beforeOrder(bq, "2");
        System.out.println(distributeResponse);
    }

}


