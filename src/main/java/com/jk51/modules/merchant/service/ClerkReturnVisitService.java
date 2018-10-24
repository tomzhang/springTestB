package com.jk51.modules.merchant.service;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.Stores;
import com.jk51.model.clerkvisit.BClerkVisit;
import com.jk51.model.clerkvisit.BVisitMessage;
import com.jk51.model.merchant.MemberLabel;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.appInterface.service.AppMemberService;
import com.jk51.modules.clerkvisit.mapper.BVisitDescMapper;
import com.jk51.modules.clerkvisit.mapper.BVisitStatisticsMapper;
import com.jk51.modules.clerkvisit.mapper.BVisitTradeMapper;
import com.jk51.modules.clerkvisit.service.BClerkVisitService;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.merchant.mapper.ClerkReturnVisitMapper;
import com.jk51.modules.merchant.mapper.WebPageMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.persistence.mapper.BTradesMapper;
import com.jk51.modules.persistence.mapper.MemberLabelMapper;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 回访列表
 *
 * @auhter zy
 * @create 2017-12-12 15:16
 */
@Service
public class ClerkReturnVisitService {

    @Value("${coupon.triggerCouponUrl}")
    public String visitWechatUrl;
    public static final Logger logger = LoggerFactory.getLogger(ClerkReturnVisitService.class);
    @Autowired
    ClerkReturnVisitMapper clerkReturnVisitMapper;

    @Autowired
    MemberLabelMapper memberLabelMapper;

    @Autowired
    BStoresMapper bStoresMapper;
    @Autowired
    PromotionsActivityMapper promotionsActivityMapper;
    @Autowired
    BVisitTradeMapper bVisitTradeMapper;
    @Autowired
    BTradesMapper bTradesMapper;
//    @Autowired
//    BVisitDescMapper bVisitDescMapper;
    @Autowired
    CouponDetailMapper couponDetailMapper;
    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    BClerkVisitService bClerkVisitService;
    @Autowired
    BVisitStatisticsMapper bVisitStatisticsMapper;
    @Autowired
    WebPageMapper webPageMapper;
    @Autowired
    AppMemberService appMemberService;
    @Autowired
    BVisitDescMapper bVisitDescMapper;

    //获取回访列表
    public List<Map<String, Object>> getVisitList(Map<String, Object> map) {
        if(Objects.nonNull(map.get("visitTime2"))){
            map.put("visitTime2",map.get("visitTime2").toString()+" 23:59:59");
        }
        List<Map<String, Object>> resultList=clerkReturnVisitMapper.getVisitList(map);
        return resultList;
    }

    //修改回访状态
    public int changeVisitStatus(Map<String, Object> map) {
        return clerkReturnVisitMapper.changeVisitStatus(map);
    }

    //获取商户下所有店员
    public List<Map<String, Object>> getMerchantClerkList(Map<String, Object> map) {
        return clerkReturnVisitMapper.getMerchantClerkList(map);
    }

    //调配
    @Transactional
    @SuppressWarnings("all")
    public Boolean changeClerk(Map<String, Object> map) {
        //先根据列表ID查询,调配前店员信息
        List<Map<String, Object>> adminsInfo = clerkReturnVisitMapper.getadminInfo(map);

        Map<String, Object> log = new HashedMap();
        log.put("siteId", map.get("siteId"));
        log.put("clerkId", map.get("clerkId"));//当前店员ID
        log.put("clerkName", map.get("clerkName"));
        adminsInfo.stream().forEach(adm -> {
            log.put("cvId", adm.get("id"));//clerk_visit_id
            log.put("preClerkId", adm.get("storeAdminId"));//之前店员ID
            log.put("preClerkName", adm.get("adminName"));
            log.put("operId", map.get("operId"));
            log.put("operName", map.get("operName"));
            log.put("preStoreId", adm.get("storeId"));
            log.put("nStoreId", map.get("storeId"));//调配之后门店ID
            clerkReturnVisitMapper.addChangeClerkLog(log);
        });
        int i = clerkReturnVisitMapper.changeClerk(map);
        if (i > 0) {
            for(int x=0;x<adminsInfo.size();x++){
                //消息主题
                List<BVisitMessage> list=new ArrayList<>();
                BVisitMessage bVisitMessage=new BVisitMessage();
                bVisitMessage.setId(Integer.valueOf(adminsInfo.get(x).get("id").toString()));
                bVisitMessage.setGid(adminsInfo.get(x).get("goodIds").toString());
                bVisitMessage.setAid(adminsInfo.get(x).get("activityIds").toString());
                bVisitMessage.setBid(Integer.valueOf(adminsInfo.get(x).get("buyerId").toString()));
                bVisitMessage.setbName(adminsInfo.get(x).get("buyerName").toString());
                list.add(bVisitMessage);
                String taskName="回访任务";
                if(Integer.valueOf(adminsInfo.get(x).get("status").toString())==20){
                    try {
                        appMemberService.notifyVisitMessage(list,Integer.valueOf(map.get("siteId").toString()),Integer.valueOf(map.get("clerkId").toString()),Integer.valueOf(map.get("storeId").toString()), PushType.TASK_VISIT,taskName);
                    } catch (Exception e) {
                        logger.error("回访任务:{}通知发送失败,失败原因:{}",e);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    //门店列表
    public List<Map<String, Object>> getStoreList(Integer siteId) {
        return clerkReturnVisitMapper.getStoreList(siteId);
    }


    //标签对象集合
    public List<MemberLabel> queryMemberLabels(Integer siteId, String[] labelIds) {
        return memberLabelMapper.getLabelAllForCouponActive(siteId, labelIds);
    }

    //查询商品信息
    public List<Map<String, Object>> queryGoodsInfoById(Integer siteId, String[] goodsIds, int goodsIdsType) {
        return clerkReturnVisitMapper.queryGoodsInfoById(siteId, goodsIds,goodsIdsType);
    }

    public List<Map<String, Object>> queryStoreInfo(Integer siteId, String[] storeIds) {
        return clerkReturnVisitMapper.queryStoreInfo(siteId, storeIds);
    }

    //查询门店列表
    public List<Stores> queryStoreListByCityId(Integer siteId, String[] cityIds) {
        List<String> strings = Arrays.asList(cityIds);
        return bStoresMapper.getStoreByCityAndSiteId(strings, siteId);
    }

    public Integer queryCoincideCustomer(Integer siteId, String[] goodsIds, Integer days, Integer goodsType, String[] memberIds) {
        return clerkReturnVisitMapper.queryCoincideCustomer(siteId, goodsIds, days,goodsType,memberIds);
    }

    public Integer queryCoincideCustomerNum(Integer siteId, String[] goodsIds, Integer days, String[] memberIds) {
        return clerkReturnVisitMapper.queryCoincideCustomerNum(siteId, goodsIds, days,memberIds);
    }

    public Integer queryMemberNum(Integer siteId) {
        return clerkReturnVisitMapper.queryMemberNums(siteId);
    }

    public List<Map<String, Object>> getActivityList(Map<String, Object> parameterMap) {

           List<Map<String, Object>>  result=clerkReturnVisitMapper.getSumForActivity(parameterMap) ;
        return  result;

//        List<Map<String, Object>>  result2=clerkReturnVisitMapper.getSumForActivity2(parameterMap);
//        for (int x=0;x<result2.size();x++){
//            Map<String, Object> map=result2.get(x);
//
//                parameterMap.put("activityId",map.get("activity_id").toString());
//
//                Map<String,Object> mapList=bVisitDescMapper.queryVisitCount(parameterMap);
//            if(mapList!=null){
//                map.put("send_num",mapList.get("send_num"));
//                map.put("page_open_num",mapList.get("page_open_num"));
//                map.put("goods_num",mapList.get("goods_num"));
//                map.put("trade_num",mapList.get("trade_num"));
//                map.put("send_used_num",mapList.get("send_used_num"));
//            }
//        }
//        return  result2;



        //查询b_visit_statistics
//        List<Map<String, Object>>  result= clerkReturnVisitMapper.getActivityList(parameterMap);
//        for(int x=0;x<result.size();x++){
//            String activityId=result.get(x).get("activity_id").toString();
//            String siteId=result.get(x).get("site_id").toString();
//            //每一个活动的时间
//            String startTime=result.get(x).get("start_time").toString();
//            String endTime=result.get(x).get("end_time").toString();
//
//            //根据活动id去统计回访中店员下单数
//            int tradeNum=bTradesMapper.getStoreAdminTradeNum(siteId,activityId);
//            //根据活动去统计回访中店员发给顾客的券数以及核销卷数
////            List<Map<String,Object>> clerkViAS goods_numsitList=clerkReturnVisitMapper.getActicityWithActivityId(siteId,activityId);
//            int ticketNum=clerkReturnVisitMapper.getCouponNum(siteId,activityId,startTime,endTime);
//            List<Map<String,Object>>  couponList=clerkReturnVisitMapper.getCouponList(siteId,activityId,startTime,endTime);
//            int useTicketNum=0;
//            for(int y=0;y<couponList.size();y++){
//                if(Integer.valueOf(couponList.get(y).get("status").toString())==0){
//                    useTicketNum++;
//                }
//            }
//
//            //根据活动去统计打开页面数
//            int openPageNum;
//            openPageNum=webPageMapper.queryPageNumByStoreadminId(siteId,startTime,endTime);
//
//            //根据活动id去统计回访中购买商品数量
//            int goodsNum=0;
//            List<Map<String,Object>> tradeList=bTradesMapper.getStoreAdminTrade(siteId,activityId);
//            if(tradeList.size()!=0){
//                for(int i=0;i<tradeList.size();i++){
//                    Map<String,Object> map=tradeList.get(i);
//                    List<Map<String,Object>> ordersList = ordersMapper.getOrdersListByTradesIdAndSiteId(map.get("site_id").toString(),Long.valueOf(map.get("trades_id").toString()));
//                    for(int t=0;t<ordersList.size();t++){
//                        goodsNum+=Integer.valueOf(ordersList.get(t).get("goods_num").toString());
//                    }
//                }
//            }
//
//            result.get(x).put("trade_num",tradeNum);
//            result.get(x).put("send_num",ticketNum);
//            result.get(x).put("send_used_num",useTicketNum);
//            result.get(x).put("goods_num",goodsNum);
//            result.get(x).put("page_open_num",openPageNum);
//
//        }
//        return result;

    }

    public Integer queryAllGoodsNum(Integer siteId) {
        return clerkReturnVisitMapper.getAllGoodsNum(siteId);
    }

    public int queryLeftGoodsNum(Integer siteId, String[] goodsIds) {
        return clerkReturnVisitMapper.queryLeftGoodsNum(siteId,goodsIds);
    }

    public List<Map<String,Object>> queryPIGoodsList(Map<String,Object> map) {
        return clerkReturnVisitMapper.getPIgoodsList(map);
    }

    public List<Map<String,Object>> getStoresBySiteId(Integer siteId, String storeCode, String clerkName) {
        return clerkReturnVisitMapper.getStoreClerkList(siteId,storeCode,clerkName);
    }

    public List<Map<String,Object>> queryMemberInfoBySiteId(Integer siteId,String[] memberIds) {
        return clerkReturnVisitMapper.queryMemberInfo(siteId,memberIds);
    }

    public List<Map<String,Object>> queryAccordMember(Integer siteId, String[] goodsIds, Integer days, Integer goodsType, String[] memberIds) {
        return clerkReturnVisitMapper.queryAccordMember(siteId,goodsIds,days,goodsType,memberIds);
    }

    public List<BClerkVisit> queryVisitBySiteId(Integer siteId) {
        return clerkReturnVisitMapper.queryVisitLog(siteId);
    }

    public List<String> queryGoodsIds(Integer siteId, Integer buyerId, String[] split,Integer days) {
        return clerkReturnVisitMapper.queryGoodsId(siteId,buyerId,split,days);
    }

    //事务插入b_visit_statistics,b_clerk_visit
    @Transactional
    public ReturnDto updateOrInsert(List<Map<String, Object>> tasks, List<BClerkVisit> visitList,Map<String,Object> bvstatistics,int timeType) {
        List<BClerkVisit> sendMess = new ArrayList<>();
        int result = 0;
        //b_clerk_visit
        int f = clerkReturnVisitMapper.insertActivityTask(bvstatistics);
        int statisticsId=Integer.valueOf(bvstatistics.get("id").toString());
        newTask:for (Map<String,Object> map : tasks) {//新回访任务
            map.put("id","");
            map.put("bvsId",statisticsId);
            //顾客ID
            Object buyerId = map.get("buyerId");
            //活动ID
            Object activityId1 = map.get("activityIds");
            //商品ID
            Object goodsIds2 = map.get("goodsIds");
            if(StringUtils.isNotEmpty(map.get("buyerMobile").toString().trim()) && !Objects.equals(map.get("buyerMobile").toString().trim(),"undefined")) {
                BClerkVisit bClerkVisit = JSON.parseObject(JSON.toJSONString(map),BClerkVisit.class);
                if(Objects.nonNull(visitList) && visitList.size() > 0){
                    for (BClerkVisit b : visitList) {//数据库同一天待回访的记录
                        /**
                         合并规则：
                         1，相同回访时间，相同顾客的任务进行合并。
                         2，合并时回访店员和回访门店相同者，不做变化，店员和门店不做变化。
                         3，合并时回访店员和回访门店不同时，取离顾客近的门店。
                         4，同门店，不同店员合并时，取与顾客关系近的店员。
                         */
                        Integer buyerId1 = b.getBuyerId();
                        if(buyerId.equals(buyerId1)) {
                            //活动ID
                            String activityIds = b.getActivityIds();
                            Set<String> actSet = new HashSet<>(Arrays.asList(activityIds.split(",")));
                            String[] split = String.valueOf(activityId1).split(",");
                            actSet.addAll(Arrays.asList(split));
                            bClerkVisit.setActivityIds(String.join(",",actSet));
                            //goods_ids
                            String goodsIds1 = b.getGoodsIds();
                            Set<String> goodsIdSet = new HashSet<>(Arrays.asList(goodsIds1.split(",")));
                            String[] splitg = String.valueOf(goodsIds2).split(",");
                            goodsIdSet.addAll(Arrays.asList(splitg));
                            bClerkVisit.setGoodsIds(String.join(",",goodsIdSet));
                            bClerkVisit.setId(b.getId());
                            int a = clerkReturnVisitMapper.updataVisitLog(bClerkVisit);
                            int a1 = clerkReturnVisitMapper.updataVisitDesc(bClerkVisit);
                            sendMess.add(bClerkVisit);
                            result = result + a + a1;
                            continue  newTask;
                        }

                    }
                    //新增记录
                    int c = clerkReturnVisitMapper.insertNewTask(bClerkVisit);
                    int c1 = clerkReturnVisitMapper.insertNewLog(bClerkVisit);
                    sendMess.add(bClerkVisit);
                    result = result + c + c1;
                    continue  newTask;
                }else {//新增记录
                    int d = clerkReturnVisitMapper.insertNewTask(bClerkVisit);
                    int d1 = clerkReturnVisitMapper.insertNewLog(bClerkVisit);
                    sendMess.add(bClerkVisit);
                    result = result + d + d1;
                }
            }

        }
        result += f;
        if(result > 0) {
            if(1 == timeType) {//立即推送回访消息
//                List<BClerkVisit> list = new ArrayList<>();
                if (Objects.nonNull(sendMess) && sendMess.size() > 0) {
                    bClerkVisitService.taskPush(sendMess);
                }

            }
            return ReturnDto.buildSuccessReturnDto("新增回访任务成功!");
        }else {
            return ReturnDto.buildFailedReturnDto("新增回访任务失败!");
        }
    }


    public List<Map<String,Object>> getAllStoresBySiteId(Integer siteId) {
        return clerkReturnVisitMapper.getAllStores(siteId);
    }

    public List<String> queryGoodsIdsByType(Integer siteId, String[] gdsIds, Integer goodsType) {
        return clerkReturnVisitMapper.queryGoodsIdsByType(siteId,gdsIds,goodsType);
    }

    public int updateSmsnum(String siteId, String bvsId) {
        return bVisitStatisticsMapper.updateSmsnum(siteId,bvsId);
    }

    public List<Map<String,Object>> queryStoresByCityId(Integer siteId, String[] cityIds) {
        List<String> strings = Arrays.asList(cityIds);
        return bStoresMapper.getStoresByCityId(strings, siteId);
    }

    public int changeVisitStastatistics(Map<String, Object> map) {
        return bVisitStatisticsMapper.updateRealMemberNum(map);
    }

    public List<String> queryMemberInfoById(Integer siteId, String[] memberIds) {
        return memberLabelMapper.queryMemberInfoById(siteId,memberIds);
    }
}




