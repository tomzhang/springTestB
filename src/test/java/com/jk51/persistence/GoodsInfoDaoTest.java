package com.jk51.persistence;

import com.github.pagehelper.PageHelper;
import com.jk51.Bootstrap;
import com.jk51.model.Goods;
import com.jk51.modules.goods.mapper.GoodsMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class GoodsInfoDaoTest {

    @Autowired
    private GoodsMapper goodsMapper;

    @Test
    public void gettest(){
        Goods goods=goodsMapper.get("50");
        System.out.println(goods.getDrugName());
        goodsMapper.get("50");
        Assert.assertEquals(goods.getGoodsId().toString(),"50");
    }

    @Test
    public void getListtest(){
        PageHelper.startPage(1,20);
        List<Goods> list= goodsMapper.getList();
        Assert.assertEquals(list.size(),20);
    }

    @Test
    public void updateTest(){
        Goods goods=goodsMapper.get("50");
        goodsMapper.update("50",goods.getDrugName()+"@");
        System.out.println(goodsMapper.get("50").getDrugName());
    }

}
