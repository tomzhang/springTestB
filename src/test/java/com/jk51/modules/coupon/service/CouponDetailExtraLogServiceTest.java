package com.jk51.modules.coupon.service;

import com.jk51.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2018/4/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class CouponDetailExtraLogServiceTest {
    @Autowired
    private CouponDetailExtraLogService couponDetailExtraLogService;
    @Test
    public void findDetailExtraLogList() throws Exception {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("adminMobile","18721626507");
        param.put("siteId",100190);
        param.put("page",1);
        param.put("pageSize",15);
        couponDetailExtraLogService.findDetailExtraLogList(param);
    }

}
