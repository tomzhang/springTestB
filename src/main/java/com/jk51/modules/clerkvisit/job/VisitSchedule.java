package com.jk51.modules.clerkvisit.job;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.clerkvisit.BClerkVisit;
import com.jk51.model.clerkvisit.BVisitDeploy;
import com.jk51.model.order.Member;
import com.jk51.model.order.Trades;
import com.jk51.modules.appInterface.service.AppMemberService;
import com.jk51.modules.clerkvisit.mapper.BClerkVisitMapper;
import com.jk51.modules.clerkvisit.mapper.BVisitDeployMapper;
import com.jk51.modules.clerkvisit.service.BClerkVisitService;
import com.jk51.modules.clerkvisit.util.AvgTask;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.mq.mns.CloudQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by Administrator on 2017/12/5.
 */
@Component
public class VisitSchedule {
    public static final Logger logger = LoggerFactory.getLogger(VisitSchedule.class);
    //执行定时任务,每天早上8点59分
    @Autowired
    private BClerkVisitMapper bClerkVisitMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private BVisitDeployMapper bVisitDeployMapper;
    @Autowired
    private AppMemberService appMemberService;
    @Autowired
    private BClerkVisitService bClerkVisitService;
    private boolean flag = false;//是否分配标识


    //分派回访任务
    public void taskPush() {
        logger.info("任务派发开始");
        List<BClerkVisit> vistList = bClerkVisitMapper.queryTodayWaitForVisit();
        if(vistList.size() >0){
            logger.info("本次回访任务数{}",vistList.size());
            bClerkVisitService.taskPush(vistList);
        }

    }


    public void sysAssigned(){
        List<BClerkVisit> vistList = bClerkVisitMapper.queryTodayWaitForVisit();
        //分配结果
        Map<Integer, List<BClerkVisit>> resultMap = new HashMap<>();
        //门店店员分组
        Map<Integer, List<StoreAdminExt>> storesMap = new HashMap<>();
        if (vistList.size() > 0) {
            vistList.stream().forEach(bClerkVisit -> {
                Trades trades = tradesMapper.getClosestTrades(bClerkVisit);
                if (Objects.nonNull(trades)) {
                    //根据订单信息查询该商户下所有已启用员工
                    List<StoreAdminExt> adminList = storeAdminExtMapper.selectAllByUsed(trades.getSiteId(), bClerkVisit.getStoreId());
                    //优先级集合
                    List<Integer> priorityList = getPriority(trades);
//                      //具体商户待分配任务
//                      List<BClerkVisit> storeVisitList=vistList.stream().filter(storeBClerkVisit -> storeBClerkVisit.getSiteId().equals(trades.getSiteId())).collect(Collectors.toList());
                    if (!storesMap.containsKey(bClerkVisit.getStoreId())) {
                        storesMap.put(bClerkVisit.getStoreId(), adminList);
                    }
                    //按优先级分配
                    if (priorityList.size() > 0) {
                        priorityList.stream().forEach(storeUserId -> {
                            adminList.stream().forEach(adminExt -> {
                                if (adminExt.getStoreadmin_id() == storeUserId) {
                                    if (resultMap.containsKey(storeUserId)) {
                                        List<BClerkVisit> list = resultMap.get(storeUserId);
                                        bClerkVisit.setStoreAdminId(storeUserId);
                                        bClerkVisit.setAdminName(adminExt.getName());
                                        list.add(bClerkVisit);
                                        resultMap.put(storeUserId, list);
                                        //分配成功
                                        flag = true;
                                    } else {
                                        bClerkVisit.setStoreAdminId(storeUserId);
                                        bClerkVisit.setAdminName(adminExt.getName());
                                        List<BClerkVisit> list = new ArrayList<>();
                                        list.add(bClerkVisit);
                                        resultMap.put(storeUserId, list);
                                        flag = true;
                                    }
                                }
                            });
                        });
                        //随机分配
                        if (flag == false) {
                            StoreAdminExt adminExt = adminList.get(new Random().nextInt(adminList.size()));
                            if (resultMap.containsKey(adminExt.getStoreadmin_id())) {
                                List<BClerkVisit> bClerkVisits = resultMap.get(adminExt.getStoreadmin_id());
                                bClerkVisit.setStoreAdminId(adminExt.getStoreadmin_id());
                                bClerkVisit.setAdminName(adminExt.getName());
                                bClerkVisits.add(bClerkVisit);
                                resultMap.put(adminExt.getStoreadmin_id(), bClerkVisits);
                            } else {
                                List<BClerkVisit> listb = new ArrayList<>();
                                bClerkVisit.setStoreAdminId(adminExt.getStoreadmin_id());
                                bClerkVisit.setAdminName(adminExt.getName());
                                listb.add(bClerkVisit);
                                resultMap.put(adminExt.getStoreadmin_id(), listb);
                            }
                        }
                    } else {//随机分配
                        StoreAdminExt adminExt = adminList.get(new Random().nextInt(adminList.size()));
                        if (resultMap.containsKey(adminExt.getStoreadmin_id())) {
                            List<BClerkVisit> list = resultMap.get(adminExt.getStoreadmin_id());
                            bClerkVisit.setStoreAdminId(adminExt.getStoreadmin_id());
                            bClerkVisit.setAdminName(adminExt.getName());
                            list.add(bClerkVisit);
                            resultMap.put(adminExt.getStoreadmin_id(), list);
                        } else {
                            List<BClerkVisit> list = new ArrayList<>();
                            bClerkVisit.setStoreAdminId(adminExt.getStoreadmin_id());
                            bClerkVisit.setAdminName(adminExt.getName());
                            list.add(bClerkVisit);
                            resultMap.put(adminExt.getStoreadmin_id(), list);
                        }
                    }
                    flag = false;
                } else {
                    //未找到相关订单
                    logger.info("未找到相关订单");
                }
            });
        }
        assignedTasksAvg(resultMap, storesMap);
    }

    //平均分配
    public void assignedTasksAvg(Map<Integer, List<BClerkVisit>> visitTasks, Map<Integer, List<StoreAdminExt>> storesMap) {
        List<BClerkVisit> visitTask = new ArrayList<>();
        List<BClerkVisit> result = new ArrayList<>();
        //根据门店分组派发任务
        for (Map.Entry<Integer, List<StoreAdminExt>> stores : storesMap.entrySet()) {
            int count = 0;//该门店下总任务
            for (StoreAdminExt adminExt : stores.getValue()) {
                if(Objects.nonNull(visitTasks.get(adminExt.getStoreadmin_id()))){
                    count+=visitTasks.get(adminExt.getStoreadmin_id()).size();
                }
            }
            int adminSize = stores.getValue().size();//待分配人员总数
            String[][] rev_task = new String[adminSize][3];//记录应分配任务数

            for (int i = 0; i < stores.getValue().size(); i++) {
                StoreAdminExt adminExt = stores.getValue().get(i);
                rev_task[i][0] = String.valueOf(adminExt.getStoreadmin_id());
                rev_task[i][1] = String.valueOf(0);
            }
            //平均分配后每个人应分配的任务数
            AvgTask.taskAllocation(count, adminSize, rev_task);
            for (int i = 0; i < rev_task.length; i++) {
                Integer userId = Integer.valueOf(rev_task[i][0]);//店员编号
                Integer taskCount = Integer.valueOf(rev_task[i][1]);//应分配任务数
                if(taskCount > 0){
                    int hasTasks=0;
                    if(Objects.nonNull(visitTasks.get(userId))){
                        hasTasks= visitTasks.get(userId).size();
                    }
                    Integer replenishNum = taskCount -hasTasks ;
                    //需要移出,(如果需要再进行优先级排序,暂时不做)
                    if (replenishNum < 0) {//获取需要重新分配的任务
                        List<BClerkVisit> list = visitTasks.get(userId).subList(taskCount, hasTasks);
                        list.stream().forEach(bClerkVisit -> {
                            bClerkVisit.setStoreAdminId(0);
                            bClerkVisit.setAdminName(null);
                        });
                        visitTask.addAll(list);
                        result.addAll(visitTasks.get(userId).subList(0, taskCount));
                    }else{
                        if(Objects.nonNull(visitTasks.get(userId))){
                            result.addAll(visitTasks.get(userId));
                        }
                    }
                }
            }
            //再分配
            for (int i = 0; i < rev_task.length; i++) {
                Integer userId = Integer.valueOf(rev_task[i][0]);//店员编号
                Optional<StoreAdminExt> adminExt = stores.getValue().stream().filter(adminExt1 -> userId.equals(adminExt1.getStoreadmin_id())).findFirst();
                String userName = adminExt.get().getName();
                Integer taskCount = Integer.valueOf(rev_task[i][1]);//应分配任务数
                int hasTasks=0;
                if(Objects.nonNull(visitTasks.get(userId))){
                    hasTasks= visitTasks.get(userId).size();
                }
                Integer replenishNum = taskCount -hasTasks ;
                if(replenishNum > 0){
                    if (visitTask.size() > 0) {
                        for (int j = 0; j < replenishNum; j++) {
                            for (BClerkVisit bClerkVisit : visitTask) {
                                if(bClerkVisit.getStoreAdminId().equals(0)){
                                    //在待分配集合中标记为已分配
                                    bClerkVisit.setStoreAdminId(userId);
                                    bClerkVisit.setAdminName(userName);
                                    //加入到结果集
                                    result.add(bClerkVisit);
                                    break;
                                }
                            }
                        }
                    }
                }

            }
            visitTask.clear();//清空待下个门店分配
        }
        sendTask(result);

    }

    @Transactional(rollbackFor = Exception.class)
    public void sendTask(List<BClerkVisit> list){
        if(list.size() > 0){
            list.stream().forEach(bClerkVisit -> {
                bClerkVisit.setStatus(Byte.valueOf("20"));
                //修改回访状态
                bClerkVisitMapper.updateByPrimaryKeySelective(bClerkVisit);
               //------------------------新增回访调配记录start----------------------------//
                BVisitDeploy bVisitDeploy=new BVisitDeploy();
                bVisitDeploy.setSiteId(bClerkVisit.getSiteId());
                bVisitDeploy.setClerkVisitId(bClerkVisit.getId());
                bVisitDeploy.setPreAdminId(0);
                bVisitDeploy.setPreAdminName("0");
                bVisitDeploy.setAdminId(bClerkVisit.getStoreAdminId());
                bVisitDeploy.setAdminName(bClerkVisit.getAdminName());
                bVisitDeploy.setPreStoreId(0);
                bVisitDeploy.setStoreId(0);
                bVisitDeploy.setOperatorName("0");
                bVisitDeploy.setOperatorId(0);
                bVisitDeployMapper.insertSelective(bVisitDeploy);
                String taskName="回访任务";
                //推送消息给店员
                try {
//                    appMemberService.notifyVisitMessage(1,bClerkVisit.getSiteId(),bClerkVisit.getStoreAdminId(),bClerkVisit.getStoreId(), PushType.TASK_VISIT,taskName);
                }catch (Exception e){
                    logger.error("回访任务通知发送失败:{},失败原因{}",bClerkVisit,e);
                }
            });
        }
    }


    public List<Integer> getPriority(Trades trades) {
        List<Integer> list = new ArrayList<>();
        //促销员
        if(trades.getStoreUserId() != null && trades.getStoreUserId() != 0){
            list.add(trades.getStoreUserId());
        }
        //最近一次咨询店员
        String simservice=bClerkVisitMapper.getLatelyBimService(trades.getSiteId(),trades.getBuyerId());
        if(Objects.nonNull(simservice)){
            list.add(Integer.valueOf(simservice));
        }
        //最近一次回访店员
        int visit=bClerkVisitMapper.getLatelyBClerkVisit(trades.getSiteId(),trades.getBuyerId());
        if(Objects.nonNull(visit)){
            list.add(visit);
        }
        //邀请注册的店员
        Member member = memberMapper.getMember(trades.getSiteId(), trades.getBuyerId());
        if(member != null && member.getRegisterClerks() != BigInteger.ZERO){
            list.add(Integer.valueOf(member.getRegisterClerks().toString()));
        }
        return list;
    }

    /**
     * 修改回访任务状态
     */
    public void changeClerkVisitStatus() {
        logger.debug("开始修改回访任务的状态");
        //查询并修改过期的回访任务状态
        long start = System.currentTimeMillis();
        int result = bClerkVisitMapper.updateReturnTaskStatus();
        long end = System.currentTimeMillis();
        logger.debug("修改回访任务成功 更新数据条数: {} 更新用时毫秒数: {}",result,end-start);
    }
}
