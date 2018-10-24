package com.jk51.modules.promotions.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.*;
import com.jk51.modules.BaseUnitTest;
import com.jk51.utils.json.JSONUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

import static org.junit.Assert.*;


/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/9                                <br/>
 * 修改记录:                                         <br/>
 */
public class PromotionsRuleServiceTest extends BaseUnitTest {

    @Autowired
    private PromotionsRuleService promotionsRuleService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void changeGiftNums() throws Exception {
        String sql = "select * from b_promotions_rule WHERE promotions_type = 10 limit 1";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql);

        assertNotEquals(null, map);
        assertNotEquals(0, map.size());

        GiftRule giftRule = JacksonUtils.json2pojo(map.get("promotions_rule").toString(), GiftRule.class);
        List<GiftRule.sendGifts> sendGifts = giftRule.getSendGifts();
        Integer giftId = sendGifts.get(0).getGiftId();
//        promotionsRuleService.changeGiftNums((int) map.get("site_id"), (int) map.get("id"), giftId, 1);
        Map<Integer, Integer> map_ = new HashMap() {{
            put(giftId, 1);
        }};
        promotionsRuleService.changeGiftNums((int) map.get("site_id"), (int) map.get("id"), map_);
    }

    @Test
    public void changeUseAmount() {
        String sql = "select * from b_promotions_rule limit 1";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql);
        assertNotEquals(null, map);
        assertNotEquals(0, map.size());

        promotionsRuleService.changeSendAmount((int) map.get("site_id"), (int) map.get("id"), 11);
    }

    @Test
    public void changeSendAmount() {
        String sql = "select * from b_promotions_rule limit 1";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql);
        assertNotEquals(null, map);
        assertNotEquals(0, map.size());

        promotionsRuleService.changeUseAmount((int) map.get("site_id"), (int) map.get("id"), 11);
    }

    @Test
    public void autoChangeStatus() throws Exception {
        String sql = "select * from b_promotions_rule";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        assertNotEquals(0, list.size());
        list.stream()
                .forEach(map -> {
                    int id = Integer.parseInt(map.get("id").toString());
                    int siteId = Integer.parseInt(map.get("site_id").toString());
                    assertTrue(promotionsRuleService.autoChangeStatus(siteId, id));
                });
    }

    @Test
    public void testCreate() throws Exception {
        PromotionsRule giftRule = createPromotionsRule(10);
        ReturnDto returnDto = promotionsRuleService.create(giftRule);
        assertEquals(ReturnDto.buildSuccessReturnDto().toString(), returnDto.toString());

        PromotionsRule discountRule = createPromotionsRule(20);
        ReturnDto returnDto1 = promotionsRuleService.create(discountRule);
        assertEquals(ReturnDto.buildSuccessReturnDto().toString(), returnDto1.toString());

        PromotionsRule freePostageRule = createPromotionsRule(30);
        ReturnDto returnDto2 = promotionsRuleService.create(freePostageRule);
        assertEquals(ReturnDto.buildSuccessReturnDto().toString(), returnDto2.toString());

        PromotionsRule reduceMoneyRule = createPromotionsRule(40);
        ReturnDto returnDto3 = promotionsRuleService.create(reduceMoneyRule);
        assertEquals(ReturnDto.buildSuccessReturnDto().toString(), returnDto3.toString());

        PromotionsRule fixedPriceRule = createPromotionsRule(50);
        ReturnDto returnDto4 = promotionsRuleService.create(fixedPriceRule);
        assertEquals(ReturnDto.buildSuccessReturnDto().toString(), returnDto4.toString());
    }

    @Test
    public void testSomething() throws Exception {
        String sql = "select * from b_promotions_rule limit 1";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql);

        String json = JSON.toJSONString(map);
        Object obj = JSON.parse(json);

        JSONUtils.convert(obj);
        System.out.println(JSON.toJSONString(obj));
        PromotionsRule promotionsRule = JacksonUtils.json2pojo(JSON.toJSONString(obj), PromotionsRule.class);
        System.out.println(promotionsRule);
    }

    private PromotionsRule createPromotionsRule(Integer promotionsType) throws Exception {
        PromotionsRule rule = new PromotionsRule();

        rule.setPromotionsName("onlyForUnitTest_autoCreate");
        rule.setPromotionsType(promotionsType);
        switch (promotionsType.intValue()) {
            case 10:
                rule.setLabel("满赠活动");
                rule.setPromotionsRule(JacksonUtils.obj2json(getGiftRule()));
                break;
            case 20:
                rule.setLabel("打折活动");
                rule.setPromotionsRule(JacksonUtils.obj2json(getDiscountRule()));
                break;
            case 30:
                rule.setLabel("包邮活动");
                rule.setPromotionsRule(JacksonUtils.obj2json(getFreePostageRule()));
                break;
            case 40:
                rule.setLabel("满减活动");
                rule.setPromotionsRule(JacksonUtils.obj2json(getReduceMoneyRule()));
                break;
            case 50:
                rule.setLabel("限价活动");
                rule.setPromotionsRule(JacksonUtils.obj2json(getFixedPriceRule()));
                break;
            default:
                throw new RuntimeException("不支持的活动类型, " + promotionsType);
        }
        rule.setTotal(-1);
        rule.setAmount(-1);
        rule.setTimeRule(JacksonUtils.obj2json(getTimeRule()));
        rule.setIsFirstOrder(0);
        rule.setUseStore("-1");
        rule.setOrderType("100,200");
        rule.setLimitState("优惠说明");
        rule.setLimitRemark("商家备注");
        rule.setSiteId(100190);

        return rule;
    }

    private GiftRule getGiftRule() {
        GiftRule giftRule = new GiftRule();
        giftRule.setCalculateBase(1);
        giftRule.setGoodsIds("184531,184696");
        giftRule.setRuleType(2);
        GiftRule.RuleCondition condition = GiftRule.RuleCondition.createMeetMoneyCondition(1000, 1, 1);
        giftRule.setRuleConditions(Arrays.asList(condition));
        giftRule.setSendType(2);
        giftRule.setSendGifts(Arrays.asList(new GiftRule.sendGifts(1,11)));
        return giftRule;
    }

    private DiscountRule getDiscountRule() {
        DiscountRule discountRule = new DiscountRule();

        discountRule.setGoodsIdsType(0);
        discountRule.setGoodsIds("all");
        discountRule.setIsMl(1);
        discountRule.setIsRound(1);
        discountRule.setRuleType(1);
        discountRule.setRules(Arrays.asList(new HashMap() {{
            put("direct_discount", 10);
            put("goods_money_limit", 200);
        }}));

        return discountRule;
    }

    private FreePostageRule getFreePostageRule() {
        FreePostageRule freePostageRule = new FreePostageRule();

        freePostageRule.setGoodsIdsType(0);
        freePostageRule.setGoodsIds("all");
        freePostageRule.setMeetMoney(100);
        freePostageRule.setReducePostageLimit(100);
        freePostageRule.setAreaIdsType(1);
        freePostageRule.setAreaIds("110100,500100");

        return freePostageRule;
    }

    private ReduceMoneyRule getReduceMoneyRule() {
        ReduceMoneyRule reduceMoneyRule = new ReduceMoneyRule();

        reduceMoneyRule.setGoodsIdsType(0);
        reduceMoneyRule.setGoodsIds("all");
        reduceMoneyRule.setRuleType(1);
        reduceMoneyRule.setRules(Arrays.asList(new ReduceMoneyRule.InnerRule(0, 11, 0, 1)));

        return reduceMoneyRule;
    }

    private FixedPriceRule getFixedPriceRule() {
        FixedPriceRule fixedPriceRule = new FixedPriceRule();

        fixedPriceRule.setGoodsIdsType(0);
        fixedPriceRule.setGoodsIds("all");
        fixedPriceRule.setBuyNumEachOrder(2);
        fixedPriceRule.setFixedPrice(1);
        fixedPriceRule.setStorage(100);
        fixedPriceRule.setTotal(fixedPriceRule.getStorage());

        return fixedPriceRule;
    }

    private TimeRuleForPromotionsRule getTimeRule() {
        TimeRuleForPromotionsRule timeRule = new TimeRuleForPromotionsRule();
        timeRule.setValidity_type(2);
        timeRule.setAssign_rule("1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31");
        timeRule.setLastDayWork(0);

        return timeRule;
    }
}
