package com.jk51.modules.promotions.utils;

import com.alibaba.fastjson.JSON;
import com.jk51.Bootstrap;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.modules.promotions.service.PromotionsActivityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by ztq on 2017/12/28
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class ConcessionCalculateBaseImplTest {
    @Autowired
    private PromotionsActivityService promotionsActivityService;


}
