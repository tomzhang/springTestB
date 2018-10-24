package com.jk51.modules.grouppurchase;

import com.alibaba.fastjson.JSON;
import com.jk51.Bootstrap;
import com.jk51.model.grouppurchase.GroupPurchase;
import com.jk51.modules.grouppurchase.mapper.GroupPurChaseMapper;
import com.jk51.modules.grouppurchase.request.GroupPurchaseForGoods;
import com.jk51.modules.grouppurchase.service.GroupPurChaseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/20.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class GroupPurchaseTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private GroupPurChaseMapper groupPurChaseMapper;
    @Autowired
    private GroupPurChaseService groupPurChaseService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void insert () throws Exception {
        GroupPurchase groupPurchase = new GroupPurchase();
        groupPurchase.setSiteId(100190);
        groupPurchase.setCreateTime(LocalDateTime.now());
        groupPurchase.setUpdateTime(LocalDateTime.now());
        groupPurchase.setProActivityId(2);
        groupPurchase.setTradesId("32432432432");
        groupPurchase.setMemberId(1562787);
        groupPurChaseMapper.create(groupPurchase);
    }

    @Test
    public void update () throws Exception {
        groupPurChaseService.updateGroupPurchaseStatus("1001901512611546580", 100190);
    }

    @Test
    public void getDataForGroupPurchase () throws Exception {
        GroupPurchaseForGoods groupPurchaseForGoods = new GroupPurchaseForGoods();
        groupPurchaseForGoods.setSiteId(100190);
        groupPurchaseForGoods.setGetGroupPurchaseForActivityId(21);
        groupPurchaseForGoods.setGroupPurchaseId(25);
        groupPurchaseForGoods.setGoodsId(1849846);
        groupPurchaseForGoods.setMemberId(15234337);
        Map<String, Object> resultMap = groupPurChaseService.getGroupPurchaseDataForGoods(groupPurchaseForGoods);
        System.out.println(JSON.toJSONString(resultMap));
    }

    @Test
    public void insertForStatusTask () {
        GroupPurchase groupPurchase = new GroupPurchase();
        LocalDateTime now = LocalDateTime.now();

        groupPurchase.setSiteId(100190);
        groupPurchase.setCreateTime(now);
        groupPurchase.setUpdateTime(now);
        groupPurchase.setProActivityId(148);
        groupPurchase.setGoodsId(176);
        groupPurchase.setMemberId(15232745);
    }

    @Test
    public void testJson () {
        String stringJson = "";
        GroupPurchase groupPurchase = JSON.parseObject(stringJson, GroupPurchase.class);
        logger.info("..........", groupPurchase);
    }

}
