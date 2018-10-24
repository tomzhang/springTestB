
package com.jk51.modules.clerkvisit.service;

import com.alibaba.fastjson.JSON;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.Stores;
import com.jk51.model.clerkvisit.BClerkVisit;
import com.jk51.model.clerkvisit.BVisitDeploy;
import com.jk51.model.clerkvisit.BVisitMessage;
import com.jk51.model.order.Member;
import com.jk51.model.order.Orders;
import com.jk51.model.order.Trades;
import com.jk51.modules.appInterface.service.AppMemberService;
import com.jk51.modules.clerkvisit.mapper.BClerkVisitMapper;
import com.jk51.modules.clerkvisit.mapper.BVisitDeployMapper;
import com.jk51.modules.clerkvisit.util.AvgTask;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/11/28.
 */

@Service
public class BClerkVisitService {

    public static final Logger logger = LoggerFactory.getLogger(BClerkVisitService.class);
    private final int VISIT_DAY = -183;//暂定半年内统计可回访商品购买数量 unit:day
    @Autowired
    private BClerkVisitMapper bClerkVisitMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private BVisitDeployMapper bVisitDeployMapper;
    @Autowired
    private AppMemberService appMemberService;



    public void addVisitTask(Trades trades) {
        if (Objects.nonNull(trades)) {
            //本次支付订单中的商品详细列表
            List<Orders> ordersList = ordersMapper.getOrdersByTradesId(trades.getTradesId());
            //获取会员信息
            Integer storeId = getStoreId(trades);
            Stores store = storesMapper.getStore(trades.getSiteId(), storeId);
            Member member = memberMapper.getMember(trades.getSiteId(), trades.getBuyerId());
            if (Objects.nonNull(ordersList) && ordersList.size() > 0) {
                for (Orders orders : ordersList) {
                    //查询是否为可回访商品
                    Map<String, Object> goods = goodsMapper.getGoodsForVisit(trades.getSiteId(), orders.getGoodsId());
                    //可回访
                    if (Objects.nonNull(goods)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("goodsId", Integer.valueOf(goods.get("goodsId").toString()));
                        param.put("siteId", goods.get("siteId"));
                        param.put("buyerId", trades.getBuyerId());
                        param.put("time", trades.getPayTime());
                        param.put("visitDay", VISIT_DAY);
                        //计算在指定时间内该可回访商品购买的数量 goodsNum > 1
                        int goodsNum = ordersMapper.getGoodsNumByTime(param);
                        //添加回访任务
                        if (goodsNum > 1) {
                            //计算回访时间
                            Date visitdate = getVisitTime(trades.getPayTime(), orders.getGoodsNum(), Integer.valueOf(goods.get("depleteTime").toString()));
                            BClerkVisit bClerkVisit = new BClerkVisit();
                            bClerkVisit.setSiteId(trades.getSiteId());
                            bClerkVisit.setBuyerId(trades.getBuyerId());
                            bClerkVisit.setBuyerName(member.getName());
                            bClerkVisit.setBuyerMobile(Long.valueOf(member.getMobile()));
                            bClerkVisit.setStoreId(storeId);//门店编号初始为0待分派
                            bClerkVisit.setStoreAdminId(0);//店员编号初始为0待分派
                            bClerkVisit.setStoreName(store.getName());
                            bClerkVisit.setDoneTime(getDoneTime(visitdate));
                            bClerkVisit.setGoodsTitle(goods.get("drugName").toString());
                            bClerkVisit.setVisitTime(visitdate);
                            try {
                                bClerkVisitMapper.insertSelective(bClerkVisit);
                                logger.info("回访任务已生成", bClerkVisit);
                            } catch (Exception e) {
                                logger.error("回访任务创建失败", e);
                            }

                        }

                    }
                }
            }
        }
    }

    public int updateVisitById(BClerkVisit bClerkVisit){
        return bClerkVisitMapper.updateByPrimaryKeySelective(bClerkVisit);
    }

    //任务转发
    @Transactional(rollbackFor = Exception.class)
    public boolean transpondVisit(Map<String,Object> param){
        boolean flag =false;
        BClerkVisit bClerkVisit=bClerkVisitMapper.selectByPrimaryKey(Integer.parseInt(param.get("id").toString()));
        BVisitDeploy bVisitDeploy=new BVisitDeploy();
        bVisitDeploy.setPreAdminName(bClerkVisit.getAdminName());
        bVisitDeploy.setPreAdminId(bClerkVisit.getStoreAdminId());
        bVisitDeploy.setClerkVisitId(bClerkVisit.getId());
        bVisitDeploy.setSiteId(bClerkVisit.getSiteId());
        bVisitDeploy.setAdminId(Integer.parseInt(param.get("adminId").toString()));
        bVisitDeploy.setAdminName(param.get("adminName").toString());
        bVisitDeploy.setPreStoreId(bClerkVisit.getStoreId());
        bVisitDeploy.setStoreId(bClerkVisit.getStoreId());
        bVisitDeploy.setOperatorId(bClerkVisit.getStoreAdminId());
        bVisitDeploy.setOperatorName(bClerkVisit.getAdminName());
        int insert = bVisitDeployMapper.insertSelective(bVisitDeploy);
        bClerkVisit.setAdminName(param.get("adminName").toString());
        bClerkVisit.setStoreAdminId(Integer.parseInt(param.get("adminId").toString()));
        int update = bClerkVisitMapper.updateByPrimaryKeySelective(bClerkVisit);
        if(insert ==1 && update ==1){
            flag=true;
        }
        //消息主题
        List<BVisitMessage> list=new ArrayList<>();
        BVisitMessage bVisitMessage=new BVisitMessage();
        bVisitMessage.setId(bClerkVisit.getId());
        bVisitMessage.setGid(bClerkVisit.getGoodsIds());
        bVisitMessage.setAid(bClerkVisit.getActivityIds());
        bVisitMessage.setBid(bClerkVisit.getBuyerId());
        bVisitMessage.setbName(bClerkVisit.getBuyerName());
        list.add(bVisitMessage);
        //任务通知
        String taskName="回访任务";
        try {
            appMemberService.notifyVisitMessage(list,bClerkVisit.getSiteId(),bClerkVisit.getStoreAdminId(),bClerkVisit.getStoreId(), PushType.TASK_VISIT,taskName);
        }catch (Exception e){
            logger.error("回访任务:{}通知发送失败,失败原因:{}",bClerkVisit,e);
        }
        return flag;
    }


    public List<Map<String,Object>> getStoreAdminVistList(Integer siteId,Integer storeId){
        return bClerkVisitMapper.getStoreAdminVistList(siteId,storeId);
    }

    public int insertFeedBack(Map<String,Object> param){
       return bClerkVisitMapper.insertFeedBack(param);
    }

    public int updateFeedBack(Map<String,Object> param){
        return bClerkVisitMapper.updateFeedBack(param);
    }

    public Map<String,Object> queryFeedBack (Map<String,Object> param){
        return bClerkVisitMapper.queryFeedBack(param);
    }


    public void taskPush(List<BClerkVisit> list){
        Map<Integer,List<BClerkVisit>> group=list.stream().collect(Collectors.groupingBy(bClerkVisit -> bClerkVisit.getStoreAdminId()));
        if(group.size() > 0){
            for (Integer storeAdminId : group.keySet()) {
               push(group,storeAdminId);
            }
            bClerkVisitMapper.updateVisitStatus(list,20);
        }
    }

    @Async
    public void push(Map<Integer,List<BClerkVisit>> group,Integer storeAdminId){
        BClerkVisit bClerkVisit=group.get(storeAdminId).get(0);
//        Integer size=group.get(storeAdminId).size();
        List<BVisitMessage> mlist=new ArrayList<>();
        group.get(storeAdminId).stream().forEach(bClerkVisit1 -> {
            BVisitMessage bVisitMessage=new BVisitMessage();
            bVisitMessage.setAid(bClerkVisit1.getActivityIds());
            bVisitMessage.setId(bClerkVisit1.getId());
            bVisitMessage.setBid(bClerkVisit1.getBuyerId());
            bVisitMessage.setGid(bClerkVisit1.getGoodsIds());
            bVisitMessage.setbName(bClerkVisit1.getBuyerName());
            mlist.add(bVisitMessage);
        });
        //任务通知
        String taskName="回访任务";
        try {
            appMemberService.notifyVisitMessage(mlist,bClerkVisit.getSiteId(),bClerkVisit.getStoreAdminId(),bClerkVisit.getStoreId(), PushType.TASK_VISIT,taskName);
            logger.info("回访任务通知成功:{}",bClerkVisit.getId());
        }catch (Exception e){
            logger.error("回访任务:{}通知发送失败,失败原因:{}",bClerkVisit,e);
        }
    }


    public Map<String,Object> queryVisitInfos(Integer bvsId,Integer siteId){
        Map<String,Object> resultMap=new HashMap<>();
        resultMap=bClerkVisitMapper.queryVisitInfos(bvsId,siteId);
        Object object = resultMap.get("activity_infos");
        Object goodsIds = resultMap.get("goodsIds");
        Object member_source_num = resultMap.get("member_source_num");
        if("0".equals(goodsIds.toString())){
            resultMap.put("visitGoodsNum","all");
        }else{
            List<String> idList = Arrays.asList(goodsIds.toString().split(","));
            resultMap.put("visitGoodsNum",idList.size());
        }
        if("0".equals(member_source_num.toString())){
            resultMap.put("member_source_num","0");
        }

        List<Map<String,Object>> activityInfos = JSON.parseObject(object.toString(), List.class);
        resultMap.put("activity_infos",activityInfos);

        return resultMap;
    }

    public Integer getStoreId(Trades trades) {
        Integer storeId = trades.getTradesStore();
        if (storeId == null || storeId == 0) {
            storeId = trades.getSelfTakenStore();
        }
        if (storeId == null || storeId == 0) {
            storeId = trades.getAssignedStores();
        }
        return storeId;
    }

    public Date getVisitTime(Date time, Integer goodsNum, Integer depleteTime) {
        Calendar visitTime = Calendar.getInstance();
        visitTime.setTime(time);
        Integer amount = goodsNum * depleteTime - 1;
        visitTime.add(Calendar.DAY_OF_YEAR, -amount);
        return visitTime.getTime();
    }

    public Date getDoneTime(Date time) {
        Calendar visitTime = Calendar.getInstance();
        visitTime.setTime(time);
        visitTime.add(Calendar.DAY_OF_YEAR, 1);
        return visitTime.getTime();
    }


}

