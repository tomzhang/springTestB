package com.jk51.order;

import com.jk51.Bootstrap;
import com.jk51.model.order.HomeDeliveryAndStoresInvite;
import com.jk51.model.order.OrderGoods;
import com.jk51.model.order.StoreDirect;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.pay.exception.PayException;
import com.jk51.modules.pay.service.PayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

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
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    PayService payService;
    /**
     * 测试直购订单
     */
    @Test
    public void testCreateStoreDirectOrders(){
        List<OrderGoods> orderGoodss = new ArrayList<OrderGoods>();
        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setGoodsId(8);
        orderGoods.setGoodsNum(1);
        orderGoodss.add(orderGoods);
        StoreDirect storeDirect = new StoreDirect();
        storeDirect.setSiteId(100090);
//        storeDirect.setOrderGoods(orderGoodss);
        storeDirect.setIntegral(0);
        storeDirect.setInvoice("1");
        storeDirect.setInvoiceTitle("上海伍壹健康科技有限公司");
        storeDirect.setMobile("13817101866");
        storeDirect.setReceiverAddress("虹口区虹关路368号建邦大厦15楼");
        storeDirect.setReceiverMobile("13817101865");
        storeDirect.setReceiverName("会淹死的鱼");
        storeDirect.setReceiverPhone("02199999999");
        storeDirect.setSotreId("0001");
        storeDirect.setUserCouponId("0");

        System.out.println(orderService.createStoreDirectOrders(storeDirect));
    }

    /**
     * 测试送货上门订单和门店自提订单
     */
    @Test
    public void testCreateOrders(){
        HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite = new HomeDeliveryAndStoresInvite();
        List<OrderGoods> orderGoodss = new ArrayList<OrderGoods>();
        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setGoodsId(1);
        orderGoods.setGoodsNum(1);
        orderGoodss.add(orderGoods);
        homeDeliveryAndStoresInvite.setSiteId(100030);
        homeDeliveryAndStoresInvite.setOrderType("2");//订单类型,1:送货上门订单,2：门店自提订单
        homeDeliveryAndStoresInvite.setOrderGoods(orderGoodss);
        homeDeliveryAndStoresInvite.setReceiverMobile("13817109876");
        homeDeliveryAndStoresInvite.setReceiverPhone("02188888888");
        homeDeliveryAndStoresInvite.setReceiverName("会淹死的鱼");
        homeDeliveryAndStoresInvite.setBuyerMessage("跟我快点发货哦，亲");
//        homeDeliveryAndStoresInvite.setClerkInvitationCode(12120);
        homeDeliveryAndStoresInvite.setIntegralUse(0);
        homeDeliveryAndStoresInvite.setInvoiceTitle("上海伍壹健康科技有限公司");
        homeDeliveryAndStoresInvite.setLat("125.21");
        homeDeliveryAndStoresInvite.setLng("30.25");
        homeDeliveryAndStoresInvite.setMobile("13817109876");
        homeDeliveryAndStoresInvite.setFlag(1);
        //homeDeliveryAndStoresInvite.setPlatformType(110);
        homeDeliveryAndStoresInvite.setReceiverAddress("虹口区虹关路368号建邦大厦15楼");
        homeDeliveryAndStoresInvite.setReceiverCityCode("31100");
        homeDeliveryAndStoresInvite.setReceiverProvinceCode("43110");
        //homeDeliveryAndStoresInvite.setPostStyle("150");//配送方式：110(卖家包邮),120(平邮),130(快递),140(EMS),150(送货上门),160(门店自提)，170(门店直销)，180(货到付款),9999(其它)
        //homeDeliveryAndStoresInvite.setSelfTakenStore();//自提门店ID，当订单类型为2：门店自提订单时，改值必传
        homeDeliveryAndStoresInvite.setStoreUserId(5423);//门店促销员ID
        homeDeliveryAndStoresInvite.setTradesInvoice(1);//是否需要开发票
        homeDeliveryAndStoresInvite.setTradesSource(130);//订单来源: 110 (网站)，120（微信），130（app）, 140（店员帮用户下单），9999（其它）
        homeDeliveryAndStoresInvite.setTradesStore("323143");//订单来源门店
        //homeDeliveryAndStoresInvite.setUserCouponId(232);//使用的优惠券ID

        System.out.println(orderService.createOrders(homeDeliveryAndStoresInvite));
    }

    @Test
    public void wxCreateNativeOrder(){
        try {
            payService.wxCreateNativeOrder(100190, "100190101010101",1,"ztst");
        } catch (PayException e) {
            e.printStackTrace();
        }
    }
}
