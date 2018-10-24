package com.jk51.modules.concession.controller;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.Goods;
import com.jk51.model.concession.ConcessionDesc2;
import com.jk51.model.order.BeforeCreateOrderReq;
import com.jk51.model.order.Member;
import com.jk51.model.order.response.DistributeResponse;
import com.jk51.modules.concession.service.ConcessionQueryService;
import com.jk51.modules.coupon.service.CouponDetailService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.order.service.DistributeOrderService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ztq on 2018/1/11
 * Description:
 */
@RequestMapping("test")
@RestController
public class TestController {
   /* private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DistributeOrderService distributeOrderService;
    @Autowired
    private CouponDetailService couponDetailService;
    @Autowired
    private ConcessionQueryService concessionQueryService;


    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private MemberMapper memberMapper;


    @PostMapping("/usableListForTest")
    public ReturnDto findUsableListForTest(@RequestBody BeforeCreateOrderReq req) throws Exception {
        String applyChannel = "103";
        req.setTradesSource(120);
        int siteId = req.getSiteId();

        // 查询商品价格
        setGoodsPriceForOrderGoods(req, siteId);

        List<Map<String, Integer>> goodsList = req.getOrderGoods().stream()
            .map(goods -> {
                Map<String, Integer> map = new HashMap<>();
                map.put("goodsId", goods.getGoodsId());
                map.put("num", goods.getGoodsNum());
                map.put("goodsPrice", goods.getGoodsPrice());
                return map;
            }).collect(Collectors.toList());

        // 通过手机号活动memberId
        Member member = setMemberInfoForReq(req, siteId);
        Integer memberId = member.getMemberId();
        String orderType = req.getOrderType();
        if ("1".equals(orderType))
            orderType = "200";
        else if ("2".equals(orderType))
            orderType = "100";
        else
            orderType = null;

        String storeId = req.getStoresId() + "";

        DistributeResponse response = distributeOrderService.beforeOrder(req, "2");

        int orderFee = response.getOrderRealPrice();
        int postFee = response.getOrderFreight();
        int areaId = response.getStore().getCityId();
        String goodsInfo = JSON.toJSONString(goodsList);

        ReturnDto returnDto = couponDetailService.usableCouponList(String.valueOf(siteId),
            String.valueOf(memberId),
            orderType,
            applyChannel,
            storeId,
            String.valueOf(orderFee),
            String.valueOf(postFee),
            goodsInfo,
            String.valueOf(areaId),
            response.getConcessionResult());

        return returnDto;
    }

    @PostMapping("beforeOrder")
    public ReturnDto beforeOrder(@RequestBody BeforeCreateOrderReq req) {
        int siteId = req.getSiteId();
        setGoodsPriceForOrderGoods(req, siteId);
        setMemberInfoForReq(req, siteId);
        req.setTradesSource(120);

        DistributeResponse response = distributeOrderService.beforeOrder(req, "2");

        return ReturnDto.buildSuccessReturnDto(response);
    }

    *//**
     * @param siteId
     * @param goodsId
     * @param status  1表示全部 2表示券可以使用/活动可以参加 3表示券不能使用/活动不能参加
     * @return
     *//*
    @GetMapping("findConcessionRuleByGoodsId")
    public ReturnDto findConcessionRuleByGoodsId(Integer siteId, Integer goodsId, Integer status) {
        if (siteId == null) return ReturnDto.buildFailedReturnDto("siteId不存在");
        if (goodsId == null) return ReturnDto.buildFailedReturnDto("goodsId不存在");

        try {
            List<ConcessionDesc2> concessionList = concessionQueryService.findConcessionRuleByGoodsId(siteId, goodsId, status);
            return ReturnDto.buildSuccessReturnDto(concessionList);
        } catch (Exception e) {
            logger.error("异常发生，{}", e);
            return ReturnDto.buildFailedReturnDto("异常发送");
        }
    }

    private Member setMemberInfoForReq(@RequestBody BeforeCreateOrderReq req, int siteId) {
        String mobile = req.getMobile();
        Member member = memberMapper.findMobileById(siteId, mobile);
        req.setUserId(member.getMemberId());
        req.setBuyerId(member.getBuyerId());
        return member;
    }

    private void setGoodsPriceForOrderGoods(@RequestBody BeforeCreateOrderReq req, int siteId) {
        req.getOrderGoods().forEach(goods -> {
            Goods goodsInfo = goodsMapper.getBySiteIdAndGoodsId(goods.getGoodsId(), siteId);
            goods.setGoodsPrice(goodsInfo.getShopPrice());
        });
    }*/
}
