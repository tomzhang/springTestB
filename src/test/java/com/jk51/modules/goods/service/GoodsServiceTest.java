package com.jk51.modules.goods.service;

import com.jk51.Bootstrap;
import com.jk51.modules.goods.request.GoodsData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2017/8/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class GoodsServiceTest {
    @Autowired
    private GoodsService goodsService;

    @Test
    public void insertOne() throws Exception {
        for(int i = 0;i<20;i++){
            GoodsData goodsData = new GoodsData();
            goodsData.setGoodsTitle("哇哇哇"+i);
            goodsData.setDrugName("礼品商品"+i);
            goodsData.setDetailTpl(150);
            goodsData.setMarketPrice(0);
            goodsData.setShopPrice(0);
            goodsData.setSpecifCation("6x6");
            goodsData.setSiteId(100190);
            goodsService.insertOne(goodsData);
        }
    }

}