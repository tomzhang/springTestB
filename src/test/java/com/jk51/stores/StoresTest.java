package com.jk51.stores;

import com.jk51.Bootstrap;
import com.jk51.model.Stores;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.index.service.StoresService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-27
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class StoresTest {
    @Autowired
    private StoresService storesService;

    @Test
    public void insertStores() {
        Stores stores = new Stores();
        stores.setSite_id(100179);
        stores.setName("qinmindayaofang");
        System.out.println(storesService.insertStores(stores)
        );
    }

    @Test
    public void updateStoresinfo() {
        Stores stores = new Stores();
        stores.setId(367);
        stores.setSite_id(100179);
        stores.setName("亲民大药房");
        System.out.println(storesService.updateMyStores(stores));
    }

    @Autowired
    CouponDetailMapper couponDetailMapper;
    @Autowired
    CouponRuleMapper couponRuleMapper;
    @Test
    public void tset(){
        CouponDetail couponDetail = couponDetailMapper.getCouponDetailByCouponId(100166,1);
        if(null != couponDetail){
            couponRuleMapper.updateUseAmountByRuleId(couponDetail.getSiteId(), couponDetail.getRuleId());
        }
    }

}
