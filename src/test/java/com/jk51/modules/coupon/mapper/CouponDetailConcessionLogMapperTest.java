package com.jk51.modules.coupon.mapper;

import com.google.common.base.Preconditions;
import com.jk51.Bootstrap;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponDetailConcessionLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Created by ztq on 2018/3/15
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class CouponDetailConcessionLogMapperTest {
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private CouponDetailConcessionLogMapper mapper;

    @Test
    public void insertWithoutId() {
        CouponDetail couponDetail = couponDetailMapper.getCouponDetailByCouponId(100190, 626);
        LocalDateTime now = LocalDateTime.now();
        CouponDetailConcessionLog log = new CouponDetailConcessionLog(Preconditions.checkNotNull(couponDetail), now);
        mapper.insertWithoutId(log);
        CouponDetailConcessionLog queryLog = mapper.queryByTradesId(couponDetail.getSiteId(), Long.parseLong(couponDetail.getOrderId()));

        assertEquals(queryLog.getSiteId(), couponDetail.getSiteId());
        assertEquals(queryLog.getCouponDetailId(), couponDetail.getId());
        assertEquals(queryLog.getCouponNo(), couponDetail.getCouponNo());

        assertEquals(queryLog.getOrderId(), couponDetail.getOrderId());
        assertEquals(queryLog.getSource(), couponDetail.getSource());
        assertEquals(queryLog.getManagerId(), couponDetail.getManagerId());

        assertEquals(queryLog.getIsCopy(), couponDetail.getIsCopy());
        assertEquals(queryLog.getIsShare(), couponDetail.getIsShare());
        assertEquals(queryLog.getShareNum(), couponDetail.getShareNum());
        assertEquals(queryLog.getRuleId(), couponDetail.getRuleId());

        assertEquals(queryLog.getUserId(), couponDetail.getUserId());
        assertEquals(queryLog.getStoreId(), couponDetail.getStoreId());
        assertEquals(queryLog.getSendOrderId(), couponDetail.getSendOrderId());
        assertEquals(queryLog.getType(), couponDetail.getType());

        assertEquals(queryLog.getDistanceReduce(), couponDetail.getDistanceReduce());
        assertEquals(queryLog.getDistanceDiscount(), couponDetail.getDistanceDiscount());
        assertEquals(queryLog.getDiscountAmount(), couponDetail.getDiscountAmount());
    }
}
