package com.jk51.modules.coupon.utils;

import com.jk51.Bootstrap;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponDetailService;
import com.jk51.modules.coupon.service.CouponRuleService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.service.GoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletContext;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by ztq on 2017/12/26
 * Description:
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class CouponDecoratorTest {
    @Autowired
    private ServletContext sc;

    @Autowired
    private CouponDetailService couponDetailService;

    @Autowired
    private CouponDetailMapper couponDetailMapper;

    @Autowired
    private CouponRuleService CouponRuleService;
    @Autowired
    private CouponRuleMapper couponRuleMapper;

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private CouponActivityMapper couponActivityMapper;

   /* @Test
    public void test() {
        CouponDecorator couponDecorator = new CouponDecorator(null, null, null, null, sc);
        CouponSendService couponSendService = couponDecorator.getCouponSendService();
        System.out.println(couponSendService);
    }*/

    /*@Test
    public void testFilter(){
         //满赠买啥送任意  组合  代码基础流程通过
        CouponDetail couponDetail = couponDetailMapper.getCouponDetail(100190, 0, 2531);
        CouponRule couponRule = couponRuleMapper.findCouponRuleById(2531, 100190);
        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(100190, 1443);

        CouponDecorator.Param param = new CouponDecorator.Param();
        param.setSiteId(100190);
        param.setStoreId(100);
        param.setGoodsDataList(new ArrayList<CouponDecorator.GoodsData>(){{
            CouponDecorator.GoodsData goodsData = new CouponDecorator.GoodsData();
            goodsData.setGoodsId(900);
            goodsData.setShopPrice(1000);
            goodsData.setNum(5);
            add(goodsData);
            CouponDecorator.GoodsData gd1 = new CouponDecorator.GoodsData();
            gd1.setGoodsId(1634);
            gd1.setShopPrice(2000);
            gd1.setNum(3);
            add(gd1);
        }});
        param.setMemberId(15234298);
        param.setOrderTime(LocalDateTime.now());
        param.setOrderType("100");
        param.setReceiverCityCode("0");
        param.setDistance(100);
        CouponDecorator couponDecorator = new CouponDecorator(couponDetail,couponRule,couponActivity,param,sc);
        System.out.println(couponDecorator.filter());
        couponDecorator.concession();
        System.out.println("库存----");
        System.out.println(couponDecorator.getResult().getGiftResult().getMaxSendNum());
        System.out.println(couponDecorator.getResult().getGiftResult().getGiftList());
    }*/

    @Test
    public void testFilter1(){

        //立减 券，初测通过
        Integer siteId= 100190;
        Integer ruleId = 2541;
        Integer managerId = 0;
        Integer activityId = 1449;
        Integer memberId = 15234298;
        CouponDecorator couponDecorator = getCouponDecorator(siteId, ruleId, managerId, activityId, memberId);
        printResult(couponDecorator);

    }

    private void printResult(CouponDecorator couponDecorator) {
        System.out.println(couponDecorator.filter());
        couponDecorator.concession();
        System.out.println("赠品库存----");
        System.out.println(couponDecorator.getResult().getGiftResult().getMaxSendNum());
        System.out.println(couponDecorator.getResult().getGiftResult().getGiftList());
        System.out.println("优惠----");
        System.out.println(couponDecorator.getResult().getDiscount());
        System.out.println("优惠类型 1代表优惠商品金额，2表示赠送礼品, 3计算不出优惠");
        System.out.println(couponDecorator.getResult().getResultMark());
        couponDecorator.getResult().getGoodsData().forEach(gd->{
            System.out.println("商品id:"+gd.getGoodsId()+"  商品总价原价:"+gd.getShopPrice()*gd.getNum()+"  商品优惠的价格:"+gd.getDiscount());
        });
    }

    private CouponDecorator getCouponDecorator(Integer siteId, Integer ruleId, Integer managerId, Integer activityId, Integer memberId) {
        CouponDetail couponDetail = couponDetailMapper.getCouponDetail(siteId, managerId, ruleId);
        CouponRule couponRule = couponRuleMapper.findCouponRuleById(ruleId, siteId);
        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(siteId, activityId);

        CouponDecorator.Param param = new CouponDecorator.Param();
        param.setSiteId(siteId);
        param.setStoreId(100);
        param.setGoodsDataList(new ArrayList<CouponDecorator.GoodsData>(){{
            CouponDecorator.GoodsData goodsData = new CouponDecorator.GoodsData();
            goodsData.setGoodsId(361);
            goodsData.setShopPrice(1000);
            goodsData.setNum(5);
            add(goodsData);
            CouponDecorator.GoodsData gd1 = new CouponDecorator.GoodsData();
            gd1.setGoodsId(1634);
            gd1.setShopPrice(2000);
            gd1.setNum(3);
            add(gd1);
        }});
        param.setMemberId(memberId);
        param.setOrderTime(LocalDateTime.now());
        param.setOrderType("100");
        param.setReceiverCityCode("0");
        param.setDistance(100);
        return new CouponDecorator(couponDetail,couponRule,couponActivity,param,sc);
    }


}
