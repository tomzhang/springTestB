package com.jk51.cache;

import com.jk51.Bootstrap;
import com.jk51.model.Goods;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 * 文件名:com.jk51.goodsService.
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-01-16
 * 修改记录:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class CacheServiceTest {

    @Autowired
    private CacheService cacheService;

    @Test
    public void integrateTest() throws Exception {
        Goods goods = cacheService.queryGoods("50");
        goods.setDrugName("打死都没卡上免费");
        cacheService.updateGoods(goods);
        //休眠十秒，看看缓存的数据
        TimeUnit.SECONDS.sleep(10);
        cacheService.delGoods(goods.getGoodsId().toString());
    }



}
