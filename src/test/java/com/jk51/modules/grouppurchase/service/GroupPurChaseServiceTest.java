package com.jk51.modules.grouppurchase.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.common.filter.impl.Op;
import com.jk51.Bootstrap;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.Goods;
import com.jk51.model.order.Store;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.grouppurchase.request.GroupPurchaseParam;
import com.jk51.modules.grouppurchase.response.GroupInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by ztq on 2017/12/12
 * Description:
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class GroupPurChaseServiceTest {
    @Autowired
    private GroupPurChaseService groupPurChaseService;
    @Autowired
    private GoodsMapper goodsMapper;

    @Test
    public void getOtherOrdersAndStatusInGroup() {
        Optional<Pair<String, List<Map<String, String>>>> optional = groupPurChaseService.getOtherOrdersAndStatusInGroup(100190, 1001901513049339326l);
        optional.ifPresent(System.out::println);
    }

    @Test
    public void cancelTradesForNoPayGroupPurchase() {
        Goods goods = goodsMapper.getBySiteIdAndGoodsId(1575, 100190);
        GroupPurchaseParam groupPurchaseParam = new GroupPurchaseParam();
        groupPurchaseParam.setSiteId(100190);
        groupPurchaseParam.setId(6);

        groupPurChaseService.cancelTradesForNoPayGroupPurchase(groupPurchaseParam, 100190, goods);
    }

    @Test
    public void queryGroupInfoByTradesId() {
        Long tradesId = 1001901520589384017l;
        GroupInfo groupInfo = groupPurChaseService.queryGroupInfoByTradesId(100190, tradesId);
        System.out.println(JSON.toJSONString(groupInfo));
    }

    @Test
    public void getStoresByProActivityId() {
        Optional<List<String>> optional = groupPurChaseService.getStoresByProActivityId(100190, 99);
        if (optional.isPresent()) {
            System.out.println(JSON.toJSONString(optional.get()));
        } else {
            System.out.println("空对象。。。。。。。。。。。。。。。");
        }
    }
}
