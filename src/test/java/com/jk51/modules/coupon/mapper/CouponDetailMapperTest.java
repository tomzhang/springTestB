package com.jk51.modules.coupon.mapper;

import com.jk51.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/21                                <br/>
 * 修改记录:                                         <br/>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class CouponDetailMapperTest {
    @Autowired
    private CouponDetailMapper couponDetailMapper;

    @Test
    public void useAmountBySiteIdAndRuleId() throws Exception {
        List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(10,11,12,13,14,15,16));

        try {
            //System.out.println(couponDetailMapper.useAmountBySiteIdAndRuleId(100190, list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void findSendAmount() {
        Integer sendAmount = couponDetailMapper.findSendAmount(100190, null, 30);
        System.out.println(sendAmount);
    }

}
