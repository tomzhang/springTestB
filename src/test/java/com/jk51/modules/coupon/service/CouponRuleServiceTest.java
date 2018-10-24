package com.jk51.modules.coupon.service;

import com.alibaba.fastjson.JSON;
import com.jk51.Bootstrap;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.BasisParams;
import com.jk51.model.coupon.requestParams.GoodsRule;
import com.jk51.model.coupon.requestParams.LimitRule;
import com.jk51.model.coupon.requestParams.TimeRule;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/7/17                                 <br/>
 * 修改记录:                                         <br/>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class CouponRuleServiceTest {

    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CouponRuleMapper couponRuleMapper;

    @Test
    public void checkStatus() throws Exception {
        System.out.println("-------------------------------------------" + couponRuleService.checkStatus(100073, 171));
    }

    @Test
    public void test1() {
        CouponRule couponRule = couponRuleMapper.findCouponRuleById(2517, 100190);
        GoodsRule goodsRule = JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class);
        int direct_money = Integer.parseInt(((Map) goodsRule.getRule()).get("direct_money").toString());
        System.out.println(direct_money);
    }

    @Test
    public void createCouponRule() throws Exception {
        BasisParams basisParams = new BasisParams();

        basisParams.setRuleName("unitTest");
        basisParams.setMarkedWords("123");
        basisParams.setSiteId(100190);
        basisParams.setCouponType(500);
        basisParams.setAimAt(1);

        switch (basisParams.getCouponType()) {
            case 500:
                basisParams.setGoodsRule(generateGiftCoupon());
                break;
            default:
                throw new RuntimeException("没有对应的goodRule异常");
        }

        basisParams.setAmount(1);
        basisParams.setTimeRule(gengerateTimeRule());
        basisParams.setLimitRule(generateLimitRule());
        basisParams.setLimitState("aaa");
        basisParams.setLimitRemark("bbb");

        ReturnDto returnDto = couponRuleService.createCouponRule(basisParams);
        assertEquals(ReturnDto.buildSuccessReturnDto("coupon rule create success").toString(), returnDto.toString());
    }

    @Test
    public void changeGiftNums() throws Exception {
        String sql = "SELECT * FROM b_coupon_rule WHERE coupon_type = 500 limit 1";
        Map<String, Object> couponRule = jdbcTemplate.queryForMap(sql);
        assertEquals(500, couponRule.get("coupon_type"));

        Object goods_rule = couponRule.get("goods_rule");
        GoodsRule goodsRule = JSON.parseObject(goods_rule.toString(), GoodsRule.class);
        Map<Integer, Integer> map = new HashMap<>();
        goodsRule.getGift_storage().stream()
                .forEach(giftStorage -> map.put(giftStorage.getGiftId(), 1));


        couponRuleService.changeGiftNums((int) couponRule.get("site_id"), (int) couponRule.get("rule_id"), map);
    }

    private GoodsRule generateGiftCoupon() {
        GoodsRule goodsRule = new GoodsRule();

        goodsRule.setGift_calculate_base(1);
        goodsRule.setType(2);
        goodsRule.setPromotion_goods("11,546,178,334");
        goodsRule.setRule_type(1);

        List<Map<String, Integer>> ruleConditions = new ArrayList<>();
        ruleConditions.add(new HashMap() {{
            put("meetNum", 1);
            put("sendNum", 1);
            put("ladder", 1);
        }});
        goodsRule.setRule(ruleConditions);

        goodsRule.setGift_send_type(3);

        List<GoodsRule.GiftStorage> giftStorage = new ArrayList<>();
        giftStorage.add(new GoodsRule.GiftStorage(184830, 1, 1));

        goodsRule.setGift_storage(giftStorage);

        return goodsRule;
    }

    private TimeRule gengerateTimeRule() {
        TimeRule timeRule = new TimeRule();
        timeRule.setValidity_type(2);
        timeRule.setDraw_day(0);
        timeRule.setHow_day(1);

        return timeRule;
    }

    private LimitRule generateLimitRule() {
        LimitRule limitRule = new LimitRule();
        limitRule.setIs_first_order(0);
        limitRule.setOrder_type("100,200");
        limitRule.setApply_channel("101,103");
        limitRule.setApply_store(-1);
        limitRule.setIs_share(0);
        limitRule.setCan_get_num(0);

        return limitRule;
    }
}
