package com.jk51.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jk51.Bootstrap;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.coupon.requestParams.*;
import com.jk51.model.order.Trades;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponNoEncodingService;
import com.jk51.modules.coupon.service.CouponProcessService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.trades.service.TradesService;
import org.apache.commons.collections.map.HashedMap;
import org.json.JSONArray;
import org.json.JSONObject;
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


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class CouponServiceTest {
    @Autowired
    private CouponProcessService couponProcessService;

    @Test
    public void discountMoney(){
        Integer integer = couponProcessService.discountMoney(1000, 2, 1, 50);
        System.out.println(integer);
    }

    @Test
    public void jsonCreate() {
        GoodsRule goodsRule = new GoodsRule();
        goodsRule.setType(2);
        goodsRule.setPromotion_goods("1180,1177,1176,1158,1006");
        goodsRule.setRule_type(2);
        Map<String, String> ruleMap = new HashMap<>();
        ruleMap.put("each_full", "100");
        ruleMap.put("deduct", "5");
        ruleMap.put("max", "50");


        ruleMap.put("meet_price_1", "3900");
        ruleMap.put("discount_price_1", "500");
        ruleMap.put("meet_price_2", "6900");
        ruleMap.put("discount_price_2", "1000");
        ruleMap.put("meet_price_3", "9900");
        ruleMap.put("discount_price_3", "1500");

        ruleMap.put("meet_num_1", "1");
        ruleMap.put("discount_price_1", "500");
        ruleMap.put("meet_num_2", "3");
        ruleMap.put("discount_price_2", "1500");
        ruleMap.put("meet_num_3", "5");
        ruleMap.put("discount_price_3", "3000");

        goodsRule.setRule(JacksonUtils.mapToJson(ruleMap));
        ObjectMapper jsonStu = new ObjectMapper();
        try {
            String str = jsonStu.writeValueAsString(goodsRule);
            System.out.println("-------" + str);
        } catch (Exception e) {

        }
    }

   /* @Test
    public void insertCouponRule() {
        BasisParams basisParams = new BasisParams();
        basisParams.setSiteId(100004);
        basisParams.setRuleName("包邮券");
        basisParams.setMarkedWords("订单满多少元或者多少件包邮 有上限");
        basisParams.setCouponType(400);
        basisParams.setAmount(10);
     //   basisParams.setTimeRule(createTimeRule());
      //  basisParams.setLimitRule(createLimitRule());
        basisParams.setLimitState("没啥好说的");
        basisParams.setLimitRemark("也没啥好说的");
        basisParams.setAimAt(0);
        //basisParams.setStartTime(LocalDate.now());
        //basisParams.setEndTime();
     //   basisParams.setOrderRule(createOrderRule());
        //basisParams.setAreaRule(createAreaRule());
        //basisParams.setGoodsRule(createGoodsRule());
        couponRuleService.createCouponRule(basisParams);
    }*/

    public TimeRule createTimeRule() {
        TimeRule timeRule = new TimeRule();
        timeRule.setValidity_type(2);
        timeRule.setDraw_day(3);
        timeRule.setHow_day(3);
        return timeRule;
    }

    public LimitRule createLimitRule() {
        LimitRule limitRule = new LimitRule();
        limitRule.setIs_first_order(0);
        limitRule.setRegister_auto_send(0);
//        limitRule.setOrder_type(200);
        //  limitRule.setApply_channel(103);
        limitRule.setApply_store(1);
        limitRule.setIs_share(0);
        return limitRule;
    }

    public OrderRule createOrderRule() {
        OrderRule orderRule = new OrderRule();
        orderRule.setRule_type(4);
        Map<String, String> ruleMap = new HashMap<>();
        ruleMap.put("order_full_money", "1000");
        ruleMap.put("order_full_money_post_max", "500");//为空表示没有设置上现
        ruleMap.put("order_full_num", "2");
        ruleMap.put("order_full_num_post_max", "500");//为空表示没有设置上现
        orderRule.setRule(JacksonUtils.mapToJson(ruleMap));
        return orderRule;
    }

    public AreaRule createAreaRule() {
        AreaRule areaRule = new AreaRule();
        areaRule.setPost_area(0);
        Map<String, String> ruleMap = new HashMap<>();
        ruleMap.put("province_ids", "130730,130705");
        areaRule.setRule(ruleMap);
        return areaRule;
    }

    public GoodsRule createGoodsRule() {
        GoodsRule goodsRule = new GoodsRule();
        goodsRule.setIs_ml(0);
        goodsRule.setGoods_num_max(5);
        goodsRule.setType(2);
        goodsRule.setPromotion_goods("1,2,3,4");
        goodsRule.setRule_type(3);
        Map<String, String> ruleMap = new HashMap<>();

        ruleMap.put("each_goods_price", "500");
        ruleMap.put("buy_num_max", "5");
        ruleMap.put("each_goods_max_buy_num", "15");
        goodsRule.setRule(JacksonUtils.mapToJson(ruleMap));
        return goodsRule;
    }

    @Test
    public void testCoupon() {
        Map<String, Integer> map = new HashedMap();
        map.put("goodsId", 489);
        map.put("num", 1);
        map.put("goodsPrice", 1);
     /*   Map<String, Integer> map1 = new HashedMap();
        map1.put("goodsId", 45331);
        map1.put("num", 1);
        map1.put("goodsPrice", 1990);*/
        List<Map<String, Integer>> list = new ArrayList<>();
        list.add(map);
        //list.add(map1);
        OrderMessageParams orderMessageParams = new OrderMessageParams();
        orderMessageParams.setCouponId(1925);
        orderMessageParams.setSiteId(100190);
        orderMessageParams.setPostFee(0);
        orderMessageParams.setOrderFee(1);
        orderMessageParams.setGoodsInfo(list);
        ReturnDto returnDto = couponProcessService.accountCoupon(orderMessageParams);
        System.out.println("----------" + returnDto.getValue().toString());
    }
    @Autowired
    CouponRuleMapper couponRuleMapper;

    @Test
    public void testUpdateStatus() {
        couponRuleMapper.revampCouponRuleStatus(1, 10002, 2);
    }

   @Autowired
   private CouponSendService couponSendService;
    @Test
    public void testSendByOrder() throws Exception {
        String tradeId = "1001661494416840868";
        //couponSendService.sendCoupon(tradeId);
    }
    @Autowired
    private TradesService tradesService;
    @Test
    public void testpaySuccessCallback() throws Exception{
        String tradeId = "1001661494419930360";
        Trades trades = tradesService.getTradesByTradesId(Long.parseLong(tradeId));
        tradesService.paySuccessCallback(trades);
    }
    @Autowired
    private CouponNoEncodingService couponNoEncodingService;
    @Test
    public void testEncryptionCouponNo(){
        //112130761986754
       // 111120851034818
        //System.out.println(couponNoEncodingService.encryptionCouponNo("1001660032001"));
        System.out.println(couponNoEncodingService.encryptionCouponNo("2300000665"));
    }
    @Test
    public void testMain(){
        String sJson = "[{'gwcxxid':'1','spsl':'2'},{'gwcxxid':'1','spsl':'2'},{'gwcxxid':'3','spsl':'4'}]";
        try {
            JSONArray jsonArray = new JSONArray(sJson);
            int iSize = jsonArray.length();
            System.out.println("Size:" + iSize);
            for (int i = 0; i < iSize; i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                System.out.println("[" + i + "]gwcxxid=" + jsonObj.get("gwcxxid"));
                System.out.println("[" + i + "]spsl=" + jsonObj.get("spsl"));
                System.out.println();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testSendCoupon() throws Exception{
        couponSendService.sendCouponByOrder("1001901520928152786");
    }
}

