package com.jk51.modules.coupon.utils;

import com.jk51.Bootstrap;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsDetail;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponDetailService;
import com.jk51.modules.coupon.service.CouponRuleService;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.mapper.PromotionsDetailMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.request.ProActivityDto;
import com.jk51.modules.promotions.utils.PromotionsDecorator;
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
 * Created by Dragonfly-captain on 2100/10/11
 * Description:
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class PromotionsDecoratorTest {

    @Autowired
    private ServletContext sc;
    @Autowired
    private PromotionsDetailMapper promotionsDetailMapper;
    @Autowired
    private PromotionsRuleMapper promotionsRuleMapper;
    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;

   /* @Test
    public void test() {
        CouponDecorator couponDecorator = new CouponDecorator(null, null, null, null, sc);
        CouponSendService couponSendService = couponDecorator.getCouponSendService();
        System.out.println(couponSendService);
    }*/

    @Test
    public void testFilter(){
        PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleByIdAndSiteId(100190, 872);
        ProActivityDto proActivityDto =new ProActivityDto();
        proActivityDto.setSiteId(100190);
        proActivityDto.setId(906);
        PromotionsActivity promotionsActivity = promotionsActivityMapper.getPromotionsActivity(proActivityDto);
        PromotionsDecorator.Param param = new PromotionsDecorator.Param();
        param.setSiteId(100190);
        param.setStoreId(100);
        param.setGoodsData(new ArrayList<PromotionsDecorator.GoodsData>(){{
            PromotionsDecorator.GoodsData goodsData = new PromotionsDecorator.GoodsData();
            goodsData.setGoodsId(560);
            goodsData.setShopPrice(1000);
            goodsData.setNum(5);
            add(goodsData);
            PromotionsDecorator.GoodsData gd1 = new PromotionsDecorator.GoodsData();
            gd1.setGoodsId(203);
            gd1.setShopPrice(2000);
            gd1.setNum(3);
            add(gd1);
            PromotionsDecorator.GoodsData gd2 = new PromotionsDecorator.GoodsData();
            gd2.setGoodsId(490);
            gd2.setShopPrice(2000);
            gd2.setNum(3);
            add(gd2);
        }});
        param.setMemberId(15234301);
        param.setOrderTime(LocalDateTime.now());
        param.setOrderType("100");
        param.setReceiverCityCode("0");
        PromotionsDecorator promotionsDecorator = new PromotionsDecorator(promotionsRule,promotionsActivity,param,sc);
        System.out.println(promotionsDecorator.filter());
        promotionsDecorator.concession();
        System.out.println("库存----");
        System.out.println(promotionsDecorator.getResult().getGiftResult().getMaxSendNum());
        System.out.println(promotionsDecorator.getResult().getGiftResult().getGiftList());
    }

  /*  @Test
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
*/

}
