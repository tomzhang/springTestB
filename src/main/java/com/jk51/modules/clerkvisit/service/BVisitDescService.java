package com.jk51.modules.clerkvisit.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.clerkvisit.BVisitDescWithDetail;
import com.jk51.model.order.Member;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.modules.clerkvisit.mapper.BVisitDescMapper;
import com.jk51.modules.clerkvisit.mapper.BVisitTradeMapper;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.merchant.mapper.ClerkReturnVisitMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.service.PromotionsActivityService;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Administrator on 2017/12/26.
 */
@Service
public class BVisitDescService {
    public static final Logger logger = LoggerFactory.getLogger(BVisitDescService.class);

    @Autowired
    private BVisitDescMapper bVisitDescMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private BVisitTradeMapper bVisitTradeMapper;

//    @Autowired
//    private CouponActivityMapper couponActivityMapper;
    @Autowired
    PromotionsActivityMapper promotionsActivityMapper;
    @Autowired
    ClerkReturnVisitMapper clerkReturnVisitMapper;
    @Autowired
    CouponDetailMapper couponDetailMapper;
    @Autowired
    PromotionsActivityService promotionsActivityService;
    @Autowired
    MemberMapper memberMapper;

    public List<BVisitDescWithDetail> queryVisitDetailList(Map<String,Object> param){
        return bVisitDescMapper.queryVisitDetailList(param);
    }

    public Map<String, Object> checkVisitResult(Map<String, Object> parameterMap) {
        //店员回访数据
        Map<String, Object> resltuMap=bVisitDescMapper.checkVisitResult(parameterMap);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Object buyerId=resltuMap.get("buyer_id");
        Object siteId=resltuMap.get("site_id");
        Object managerId=resltuMap.get("store_admin_id");
        Object storeId =resltuMap.get("store_id");
        //店员在App中下单
        List<Map<String, Object>> tradeIdList=bVisitTradeMapper.selectByVisitIdAndSiteId(parameterMap.get("id").toString(),parameterMap.get("siteId").toString());

        for(int x=0;x<tradeIdList.size();x++){
            List<String> namelist=new ArrayList<>();
            List<String> numberlist=new ArrayList<>();
            Long tradesId=Long.valueOf(tradeIdList.get(x).get("trades_id").toString());
            List<Map<String,Object>> ordersList = ordersMapper.getOrdersListByTradesIdAndSiteId(parameterMap.get("siteId").toString(),tradesId);
            for(int y=0;y<ordersList.size();y++){
                namelist.add(ordersList.get(y).get("goods_title").toString());
                numberlist.add(ordersList.get(y).get("goods_num").toString());
            }
            tradeIdList.get(x).put("namelist",namelist);
            tradeIdList.get(x).put("numberlist",numberlist);
        }
        String[]  activityId=parameterMap.get("activityIds").toString().split(",");
        //去重
        Set<String> set = new HashSet(Arrays.asList(activityId));
        int usedNum=0;//以核销券
        int brandNewNum=0;//未核销
        //活动期间顾客消费信息
        List<Map<String,Object>> ordersList=new ArrayList<>();


        for (int i = 0; i < activityId.length; i++) {
            set.add(activityId[i]);
        }
        for(String str : set){
            List<PromotionsActivity> releasePomotionsActivity = promotionsActivityService.findAllReleasePromotionsActivityForBuyer(Integer.valueOf(parameterMap.get("siteId").toString()), Integer.valueOf(str),1);
            Member member=memberMapper.getMember(Integer.valueOf(siteId.toString()),Integer.valueOf(buyerId.toString()));
                PromotionsActivity promotionsActivity =releasePomotionsActivity.get(0);
                parameterMap.put("startTime",promotionsActivity.getStartTime());
                parameterMap.put("endTime",promotionsActivity.getEndTime());
                parameterMap.put("memberId",member.getMemberId());
                parameterMap.put("buyerId",buyerId);
                parameterMap.put("storeAdminId",managerId);
                parameterMap.put("storeId",storeId);
                Map<String, Object> map=couponDetailMapper.getCouponToBuyer(parameterMap);
                if(Objects.nonNull(map)){
                    Integer used=Integer.valueOf(map.get("used").toString());
                    Integer brandNew=Integer.valueOf(map.get("brandNew").toString());
                    usedNum+=used;
                    brandNewNum+=brandNew;
                }
                //参与商品
                PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
                Map<String, String> goodsIds = PromotionsRuleService.getGoodsIds(promotionsRule);
                if(StringUtil.isNotEmpty(goodsIds.get("goodsIds"))){
                    String goodsIds1=goodsIds.get("goodsIds");
                    String [] ids=goodsIds1.split(",");
                    parameterMap.put("goodsIds",ids);
                    parameterMap.put("buyerId",resltuMap.get("buyer_id"));
                    List<Map<String, Object>> activityIdList=ordersMapper.getOrdersListByActivity(parameterMap);
                    for(int t=0;t<activityIdList.size();t++){
                        activityIdList.get(t).put("startTime",promotionsActivity.getStartTime().format(formatter));
                        activityIdList.get(t).put("endTime",promotionsActivity.getEndTime().format(formatter));
                    }
                    ordersList.addAll(activityIdList);
                }
            }
        resltuMap.put("tradeIdList",tradeIdList);
        resltuMap.put("activityTradeList",ordersList);
        resltuMap.put("usedNum",usedNum);
        resltuMap.put("brandNewNum",brandNewNum);
        return resltuMap;
    }


    public int updateByVisitId(String siteId,String visitId, String storeAdminId) {
       return bVisitDescMapper.updateSmsStatusByVisitId(siteId,visitId,storeAdminId);
    }
}
