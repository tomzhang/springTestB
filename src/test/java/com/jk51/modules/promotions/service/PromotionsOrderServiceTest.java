package com.jk51.modules.promotions.service;

import com.alibaba.fastjson.JSON;
import com.jk51.model.concession.ConcessionDesc;
import com.jk51.model.concession.GiftMsg;
import com.jk51.model.concession.result.GiftResult;
import com.jk51.model.order.response.OrderResponse;
import com.jk51.modules.BaseUnitTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztq on 2018/1/17
 * Description:
 */
public class PromotionsOrderServiceTest extends BaseUnitTest {
    @Autowired
    private PromotionsOrderService promotionsOrderService;

    @Test
    public void verifyGiftGoodsStockWithCoupon() {
        Integer couponDetailId = 6;
        List<GiftResult> giftResultList = buildGiftResultForCoupon(couponDetailId);
        Integer siteId = 100190;
        String promotionsActivityIds = "";

        OrderResponse orderResponse = promotionsOrderService.verifyGiftGoodsStock(giftResultList, siteId, promotionsActivityIds, couponDetailId);
        System.out.println(JSON.toJSONString(orderResponse));
    }

    @Test
    public void verifyGiftGoodsStockWithPromotions() {
        List<GiftResult> giftResultList = buildGiftResultForPromotions();
        Integer siteId = 100190;
        Integer couponDetailId = null;
        String promotionsActivityIds = "8,9";

        OrderResponse orderResponse = promotionsOrderService.verifyGiftGoodsStock(giftResultList, siteId, promotionsActivityIds, couponDetailId);
        System.out.println(JSON.toJSONString(orderResponse));
    }

    private List<GiftResult> buildGiftResultForPromotions() {
        List<GiftResult> list = new ArrayList<>();

        GiftResult giftResult1 = new GiftResult();
        ConcessionDesc concessionDesc = new ConcessionDesc();
        concessionDesc.setConcessionType(2);
        concessionDesc.setPromotionsActivityId(8);
        giftResult1.setConcessionDesc(concessionDesc);

        List<GiftMsg> giftMsgs = new ArrayList<>();
        GiftMsg giftMsg1 = new GiftMsg() {{
            setGoodsId(1096);
            setSendNum(1);
        }};
        giftMsgs.add(giftMsg1);
        giftResult1.setGiftList(giftMsgs);

        list.add(giftResult1);

        GiftResult giftResult2 = new GiftResult();
        ConcessionDesc concessionDesc2 = new ConcessionDesc();
        concessionDesc2.setConcessionType(2);
        concessionDesc2.setPromotionsActivityId(9);
        giftResult2.setConcessionDesc(concessionDesc2);

        List<GiftMsg> giftMsgs2 = new ArrayList<>();
        GiftMsg giftMsg2 = new GiftMsg() {{
            setGoodsId(1848663);
            setSendNum(2);
        }};
        giftMsgs2.add(giftMsg2);
        giftResult2.setGiftList(giftMsgs2);

        list.add(giftResult2);

        return list;
    }


    private List<GiftResult> buildGiftResultForCoupon(Integer couponDetailId) {
        List<GiftResult> list = new ArrayList<>();

        GiftResult giftResult = new GiftResult();
        ConcessionDesc concessionDesc = new ConcessionDesc();
        concessionDesc.setConcessionType(1);
        concessionDesc.setCouponDetailId(couponDetailId);
        giftResult.setConcessionDesc(concessionDesc);

        List<GiftMsg> giftMsgs = new ArrayList<>();
        GiftMsg giftMsg1 = new GiftMsg() {{
            setGoodsId(1848648);
            setSendNum(1);
        }};

        giftMsgs.add(giftMsg1);

        giftResult.setGiftList(giftMsgs);
        list.add(giftResult);

        return list;
    }
}
