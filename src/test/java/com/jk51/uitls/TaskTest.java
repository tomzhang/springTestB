package com.jk51.uitls;

import com.github.pagehelper.PageInfo;
import com.jk51.Bootstrap;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.CouponRuleActivity;
import com.jk51.model.coupon.requestParams.OrderMessageParams;
import com.jk51.model.promotions.rule.ProCouponRuleView;
import com.jk51.modules.account.job.SchedulerTask;
import com.jk51.modules.coupon.job.CouponStausTask;
import com.jk51.modules.coupon.job.OldConvertNewCouponTask;
import com.jk51.modules.coupon.job.SendCouponTask;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.mapper.CouponRuleActivityMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.request.CouponGoods;
import com.jk51.modules.coupon.service.*;
import com.jk51.modules.promotions.request.ProRuleMaxParam;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class TaskTest {

    private static final Logger log = LoggerFactory.getLogger(TaskTest.class);

    @Autowired
    private SchedulerTask task;

    @Autowired
    private CouponActivityMapper couponActivityMapper;

    @Autowired
    private CouponActivityService service;
    @Autowired
    private CouponProcessService couponProcessService;

    @Autowired
    private CouponFilterService couponFilterService;

    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    private CouponRuleActivityMapper mapper;

    @Autowired
    private CouponRuleMapper ruleMapper;
    @Autowired
    private SendCouponTask sendCouponTask;
    @Autowired
    private CouponStausTask couponStausTask;
    @Autowired
    private OldConvertNewCouponTask oldConvertNewCouponTask;
    @Autowired
    private CouponSendService couponSendService;

    @Test
    public void taskOldConvert(){
        oldConvertNewCouponTask.run(100166);
    }

    @Test
    public void taskStatusTaskTest() {
        couponStausTask.updateActivityStatus();
        while (true){}
    }
    @Test
    public void testSendCouponDirect() {
        couponSendService.sendCouponDirect(100073,90);
        while (true){}
    }
    @Test
    public void taskSendTaskTest() {
        couponStausTask.updateStatusByTimeRule();
    }

    @Test
    public void taskSendCouponTest() {
        sendCouponTask.sendMembersDayCoupon();
    }


    @Test
    public void taskTest() {
        task.generateBillingJob();
    }

    @Test
    public void tasksTest() {
        task.autoAccount();
    }


    @Test
    public void updateBudgetDate() {
        task.getBudgetDate(1001731478676589300l);
    }

    public CouponActivity get(int siteid) {

        CouponActivity couponActivity = new CouponActivity();
        couponActivity.setSiteId(siteid);
        couponActivity.setSendObj(1);
        couponActivity.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        couponActivity.setStartTime(couponActivity.getCreateTime());
        couponActivity.setEndTime(Timestamp.valueOf(couponActivity.getCreateTime().toLocalDateTime().plusMonths(1)));
        couponActivity.setContent("测试活动");
        couponActivity.setTitle("标题");
        couponActivity.setSendConditionType(1);
        couponActivity.setSendCondition("100&*&non&*&100001:100002");
        couponActivity.setSendWay(2);
        couponActivity.setSendType(3);
        couponActivity.setStatus(0);
        return couponActivity;
    }

    public CouponRuleActivity getact(int siteid, int ruleid) {
        CouponRuleActivity activity = new CouponRuleActivity();
        activity.setRuleId(ruleid);
        activity.setSiteId(siteid);
        activity.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        activity.setNum(1);
        return activity;
    }

    @Test
    public void insertChildTest() {
        CouponActivity couponActivity = get(10002);
        List<CouponRuleActivity> ruleActivities = new ArrayList<>();
        ruleActivities.add(getact(10002, 1));
        ruleActivities.add(getact(10002, 2));

        service.createAcitve(couponActivity, ruleActivities);

    }

    @Test
    public void getRule() {
        List<CouponRuleActivity> rule = mapper.getRuleByActive(10003, 16);

        rule.forEach(couponRuleActivity -> System.out.println(couponRuleActivity.toString()));
    }

    @Test
    public void getRules() {
        List<CouponRule> rule = ruleMapper.findCouponRuleByActive(10003, 16);

        rule.forEach(couponRuleActivity -> System.out.println(couponRuleActivity.toString()));
    }
@Autowired
private CouponDetailService couponDetailService;

    @Test
    public void testUseCoupon() throws Exception{
        /**
         *  private boolean isFirstOrder;//是否首单
         private Integer orderType;//订单类型
         private Integer applyChannel;//适用渠道
         private Integer storeId;//门店id
         private Integer postFee;//邮费
         private Integer orderFee;//实付金额 商品金额-积分+邮费

         * map数据如：goodsId：1234，num:2,goodsPrice:9000

        private List<Map<String, Integer>> goodsInfo;
        private Integer areaId;//地址id，省的id
        private Integer couponId;//优惠券id
        private Integer userId;//用户id
        private Integer siteId;//商家id
         */
         OrderMessageParams orderMessageParams = new OrderMessageParams();
        orderMessageParams.setSiteId(100190);
        orderMessageParams.setUserId(15233896);
        orderMessageParams.setFirstOrder(couponSendService.isFirstOrder(100190,15233896));
        orderMessageParams.setOrderType(100);
        orderMessageParams.setApplyChannel(103);
        orderMessageParams.setStoreId(10);
        orderMessageParams.setOrderFee(201);
        orderMessageParams.setPostFee(100);
        List<Map<String, Integer>> list = new ArrayList<>();
        Map map = new HashMap();
        map.put("goodsId",145260);
        map.put("num",3);
        map.put("goodsPrice",500);

        Map map2 = new HashMap();
        map2.put("goodsId",145250);
        map2.put("num",1);
        map2.put("goodsPrice",100);

      Map map3 = new HashMap();
        map3.put("goodsId",46272);
        map3.put("num",4);
        map3.put("goodsPrice",1);
 /*
        Map map4 = new HashMap();
        map4.put("goodsId",13877);
        map4.put("num",1);
        map4.put("goodsPrice",100);*/
        list.add(map);
       list.add(map2);
       list.add(map3);
       /* list.add(map4);*/
        int a=list.stream().mapToInt(item->item.get("num")).sum();

        orderMessageParams.setGoodsInfo(list);
        orderMessageParams.setAreaId(430000);
        Map<String, Object>  map1 = couponDetailService.findUsableCoupon(orderMessageParams);
        System.out.println(map1);
    }

    @Test
    public void testUserList() throws Exception{
        List<Map<String, Object>> resultMap=couponDetailService.findUserCouponList( 100073,  15232676,  0);
        System.out.print(resultMap);
    }

    @Test
    public void discountMoney(){
        couponProcessService.discountMoney( 500, 1, 0, 33);
    }
    @Test
    public void testReids(){
        //172.20.12.14
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(0);
        jedisPoolConfig.setMaxWaitMillis(-1);
        jedisPoolConfig.setTestOnBorrow(true);

        JedisPool jedis = new JedisPool(jedisPoolConfig, "172.20.12.14", 6379, 10000);
        //Jedis jedis = new Jedis("172.20.10.249", 6379);
        Integer siteId = 100180;

       // String s = jedis.hget("coupon:register:" + siteId,"Q428368065563528");
        Jedis jedis1 = jedis.getResource();
        String s = jedis1.hget("coupon:register:" + siteId,"Q428368065563528");
        System.out.println("---------:"+s);
    }
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Test
    public void updateUseAmount(){
        couponRuleMapper.updateUseAmount(100030,1);
    }


    @Test
    public void CouponSendServiceTest(){
        couponSendService.sendCoupon(100073,99999999);
    }
@Autowired
private StringRedisTemplate stringRedisTemplate;
    @Test
    public void testsss(){
        stringRedisTemplate.opsForList().rightPush("lpList", "stringList");
        stringRedisTemplate.opsForList().rightPush("lpList", "222");
        for (int i =0 ;i<2 ; i++)
        System.out.println(stringRedisTemplate.opsForList().rightPop("lpList"));


    }


    @Test
    public void testMyCoupon(){
        List<Map<String, Object>> data;
        try {
            data=couponDetailService.findUserCouponList(Integer.parseInt("100073"), Integer.parseInt("15231826"), Integer.parseInt("2"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void updateCouponRuleTimer(){
        couponStausTask.updateStatusByTimeRuleForSpikeTicket();
    }
    @Test
    public void couponSenda(){
        try{
            boolean result=this.couponSendService.sendCouponByPay("1001901501743559805");
        }catch(Exception e){

        }

    }

    @Test
    public  void testRecoverCoupon(){
//        boolean result =this.couponDetailService.returnCoupon("1000731497868662965",1);
    }

    @Test
    public void testCoupon(){
        CouponGoods goods =new CouponGoods();
        goods.setRuleId("23");
        goods.setSiteId("100190");
        goods.setGoodsName("感冒");
        PageInfo<?> pageInfo =couponRuleService.queryCouponGoodsList(goods);
        log.info(""+pageInfo);
    }




    @Test
    public void testGoodsCoupon(){
    
        List<CouponActivity> dto =couponFilterService.getCanReceiveCoupon(100073,276+"",196353);
        log.info(""+dto);
    }

    @Autowired
    CouponActiveForMemberService couponActiveForMemberService;
@Autowired
CouponActivityService couponActivityService;
    @Test
    public void tsetGetMembers() throws  Exception{
/*
        List<Map<String, Integer>> list=couponSendService.getGoodsInfo("1000731500859682760");
        boolean result=couponActiveForMemberService.checkUserForCouponActive(1523188,403,100073);
        System.out.print(result);*/
        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(100190, 10);
        couponActivityService.canSendBySendRules(10,couponActivity,12);
    }
    @Autowired
    PromotionsRuleService promotionsRuleService;

    @Test
    public void testToupdateCouponruleStatus() throws Exception{
     /*   boolean result=couponRuleService.isOverdue("{\"validity_type\":1,\"draw_day\":null,\"how_day\":null,\"assign_type\":null,\"assign_rule\":null,\"startTime\":\"2017-08-01\",\"endTime\":\"2017-08-01\"}");
        System.out.print("result:"+result);*/
     boolean result=promotionsRuleService.checkproRuleTimeRule("{\"validity_type\":2,\"assign_rule\":\"1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31\",\"lastDayWork\":3}");
    }
    @Test
    public  void tastproRuleUsableForMax()throws  Exception{
        ProRuleMaxParam proRuleMaxParam=new ProRuleMaxParam();
        proRuleMaxParam.setSiteId("100073");
        proRuleMaxParam.setUserId("15232344");
        proRuleMaxParam.setOrderType("100");
        proRuleMaxParam.setApplyChannel("103");
        proRuleMaxParam.setStoreId("10");
        proRuleMaxParam.setOrderFee("201");
        proRuleMaxParam.setPostFee("100");
        List<Map<String, Integer>> list = new ArrayList<>();
        Map map = new HashMap();
        map.put("goodsId",145260);
        map.put("num",1);
        map.put("goodsPrice",300);

        Map map2 = new HashMap();
        map2.put("goodsId",145250);
        map2.put("num",1);
        map2.put("goodsPrice",300);

        Map map3 = new HashMap();
        map3.put("goodsId",46272);
        map3.put("num",4);
        map3.put("goodsPrice",1);
 /*
        Map map4 = new HashMap();
        map4.put("goodsId",13877);
        map4.put("num",1);
        map4.put("goodsPrice",100);*/
        list.add(map);
        list.add(map2);
        list.add(map3);
       /* list.add(map4);*/


        proRuleMaxParam.setGoodsInfo(JacksonUtils.obj2json(list));
        proRuleMaxParam.setAreaId("430000");
//        Map<String,Object> returnDto=promotionsRuleService.proRuleUsableForMax(proRuleMaxParam);
//        System.out.print(returnDto);
    }

    @Test
    public void testProRule() throws  Exception{
       /* ProCouponRuleView proCouponRuleView=promotionsRuleService.getProCouponRuleListForProRule(100073,12);
        System.out.print(proCouponRuleView);*/
    }

    @Test
    public void testPrice() throws  Exception{
        /*int discount_num = (int) Math.floor(4 /3);
        System.out.print(discount_num);*/
        couponSendService.sendCouponByOrder("1001901507780352250");
    }
}
