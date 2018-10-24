package com.jk51.modules.clerkvisit.service;

import com.jk51.model.clerkvisit.BClerkVisit;
import com.jk51.model.clerkvisit.BVisitTrade;
import com.jk51.model.order.Orders;
import com.jk51.model.order.Trades;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.modules.clerkvisit.mapper.BClerkVisitMapper;
import com.jk51.modules.clerkvisit.mapper.BVisitTradeMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.promotions.service.PromotionsActivityService;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/12/25.
 */
@Service
public class BVisitTradeService {
    public static final Logger logger = LoggerFactory.getLogger(BVisitTradeService.class);
    @Autowired
    private BVisitTradeMapper bVisitTradeMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private BClerkVisitMapper bClerkVisitMapper;
    @Autowired
    PromotionsActivityService promotionsActivityService;


    public int getCountForActivityTrades(Map<String,Object> param){
        return bVisitTradeMapper.queryCountForActivityTrades(param);
    }

    public void insertSelective(BVisitTrade bVisitTrade){
        long tradesId=bVisitTrade.getTradesId();
        Trades trades = tradesMapper.getTradesByTradesId(tradesId);
        if(Objects.isNull(trades) || Objects.isNull(trades.getBuyerId()) || Objects.isNull(trades.getStoreUserId()) || Objects.isNull(trades.getTradesStore()) ){

        }else{
            //对应任务
            List<BClerkVisit> bClerkVisitList =bClerkVisitMapper.queryByTrades(trades);
            if(bClerkVisitList.size() > 0 ){
                List<Orders> orders = this.ordersMapper.getOrdersByTradesId(tradesId);
                //获取该订单中所有goodsId
                String goodsIds=orders.stream().map(orders1 -> orders1.getGoodsId().toString()).collect(Collectors.joining(","));
                String [] gids=goodsIds.split(",");
                bClerkVisitList.stream().forEach(bClerkVisit -> {
                    String[]  activityId=bClerkVisit.getActivityIds().split(",");
                    Set<String> set = new HashSet(Arrays.asList(activityId));
                    for(String str : set){
                        //进行中的活动
                        List<PromotionsActivity> releasePomotionsActivity = promotionsActivityService.findAllReleasePromotionsActivityForBuyer(bClerkVisit.getSiteId(), Integer.valueOf(str),2);
                        if(releasePomotionsActivity.size() > 0){
                            PromotionsActivity promotionsActivity =releasePomotionsActivity.get(0);
                            PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
                            Map<String, String> map = PromotionsRuleService.getGoodsIds(promotionsRule);
                            if(Objects.nonNull(map.get("goodsIds"))){
                                String goodsIds1=map.get("goodsIds");
                                String [] ids=goodsIds1.split(",");
                                if(stringArrayCompare(ids,gids) && Objects.nonNull(bClerkVisit.getId()) && Objects.nonNull(bClerkVisit.getSiteId())){
                                    bVisitTrade.setVisitId(bClerkVisit.getId());
                                    bVisitTrade.setSiteId(bClerkVisit.getSiteId());
                                    bVisitTradeMapper.insertSelective(bVisitTrade);
                                    break;
                                }
                            }

                        }
                    }
                });

            }
        }
    }

    public  boolean stringArrayCompare(String[] b, String[] c) {
        boolean flag = false;
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (c[i].equals(b[j])) {
                    flag = true;
                    break;
                } else {
                    flag = false;
                }
            }
        }
        return flag;
    }

}
