package com.jk51.modules.coupon.mapper;

import com.alibaba.fastjson.JSON;
import com.jk51.Bootstrap;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.rule.CouponRuleSqlParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/10/30                                <br/>
 * 修改记录:                                         <br/>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class CouponRuleMapperTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CouponRuleMapper couponRuleMapper;

    @Test
    public void addCouponRule() throws Exception {

    }

    @Test
    public void addCouponRuleAndGetId() throws Exception {
        CouponRule couponRule = couponRuleMapper.findCouponRuleById(1875, 100190);
        System.out.println(couponRule);
        LocalDateTime now = LocalDateTime.now();
        couponRule.setRuleName(couponRule.getRuleName() + "_" + now.getMinute() + ":" + now.getSecond());
        couponRule.setStatus(0);
        couponRule.setRuleId(null);
        int id = couponRuleMapper.addCouponRuleAndGetId(couponRule);
        System.out.println("This is the key?, " + couponRule.getRuleId());
    }

    @Test
    public void findByParam() {
        CouponRuleSqlParam param = new CouponRuleSqlParam();
        List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(2, 3));
        param.setStatusList(list);

        List<CouponRule> byParam = couponRuleMapper.findByParam(param);
        System.out.println(JSON.toJSONString(byParam));
    }
}
