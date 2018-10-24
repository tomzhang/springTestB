package com.jk51.modules.task.service;

import com.jk51.model.task.BTask;
import com.jk51.model.task.BTaskplan;
import com.jk51.model.task.TQuota;
import com.jk51.modules.task.domain.FollowTask;
import com.jk51.modules.task.domain.RewardRule;
import com.jk51.modules.task.mapper.BTaskMapper;
import com.jk51.modules.task.mapper.TQuotaMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-08-29 14:40
 * 修改记录:
 */
@Service
public class TaskPlanReportService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TaskPlanReportService.class);

    @Autowired
    TaskPlanService planService;
    @Autowired
    BTaskMapper taskMapper;
    @Autowired
    TQuotaMapper tQuotaMapper;

    private final static int[] targerIdsofMoney = {3,4,5,6};

    public Map<String,Object> getTaskPlanListReportData(Integer planId,final Integer siteId){

        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
        final DateFormat dateFormat2 = new SimpleDateFormat("yyyy年第M月");
        final Date date = new Date();

        NumberFormat num = NumberFormat.getPercentInstance();
        num.setMaximumFractionDigits(2);
        NumberFormat num1=NumberFormat.getNumberInstance() ;
        num1.setMaximumFractionDigits(2);

        BTaskplan bTaskplan = planService.findByid(planId);
        //要返回的计划数据
        Map<String,Object> taskPlan = new HashMap<String,Object>();
        taskPlan.put("id",planId);
        taskPlan.put("name",bTaskplan.getName());
        Integer[] taskIds = String2IntegerArray(bTaskplan.getTaskIds(),",");
        if(taskIds != null){
            try {
                //线程池
                ExecutorService taskPool = Executors.newCachedThreadPool();
                //要返回的任务数据
                List<Map<String,Object>> tasksData = new ArrayList<Map<String,Object>>();
                for (Integer taskId : taskIds){
                    //开启任务子线程
                    taskPool.execute(()->{
                        BTask bTask = taskMapper.findById(taskId);
                        TQuota tQuota = tQuotaMapper.selectByPrimaryKey(bTask.getTargetId());

                        Map<String,Object> params = new HashMap<String,Object>(){{
                            put("siteId",siteId);
                            //任务发放对象类型
                            put("object",bTask.getObject());
                            put("planId",planId);
                            put("taskId",taskId);
                        }};

                        String timeType = "";

                        //时间类型
                        if(bTask.getTimeType() == 10){
                            timeType = dateFormat.format(date);
                            params.put("timeType",timeType);
                        }else if(bTask.getTimeType() == 20){
                            Calendar cal = Calendar.getInstance();
                            cal.setFirstDayOfWeek(Calendar.MONDAY);
                            cal.setTime(date);
                            timeType = cal.get(Calendar.YEAR) + "年第" + cal.get(Calendar.WEEK_OF_YEAR) + "周";
                            params.put("timeType", timeType);
                        }else if(bTask.getTimeType() == 30){
                            timeType = dateFormat2.format(date);
                            params.put("timeType",timeType);
                        }

                        //原始数据
                        List<FollowTask> followTasks = planService.taskFollow(params);

                        LOGGER.info("Id: {} {}", params, followTasks);

                        //原始数据转报表数据
                        List<Map<String,Object>> taskDatas = null;
                        if(Optional.ofNullable(followTasks).isPresent() && followTasks.size() > 0){
                            taskDatas = followTasks.parallelStream().map(followTask -> {

                                Map<String,Object> taskData = new HashMap<String, Object>();
//                            //序号
//                            taskData.put("id",followTasks.indexOf(followTask));
                                //店员名
                                taskData.put("adminName",followTask.getAdminName());
                                //店员id
                                taskData.put("storeadminId",followTask.getStoreadminId());
                                //店员邀请码
                                taskData.put("clerkInvitationCode",followTask.getClerkInvitationCode());
                                //门店
                                taskData.put("storeName",followTask.getStoreName());
                                //门店id
                                taskData.put("storesNumber",followTask.getStoresNumber());
                                //店员数
                                taskData.put("adminCount",followTask.getAdminCount());
                                //完成值
                                if(Optional.ofNullable(followTask.getTargetId()).isPresent()&& Arrays.binarySearch(targerIdsofMoney,followTask.getTargetId()) > -1){
                                    if(Optional.ofNullable(followTask.getReward()).isPresent())taskData.put("countValue",num1.format(followTask.getCountValue()/100.0));
                                }else {
                                    taskData.put("countValue",followTask.getCountValue());
                                }
                                //奖励类型
                                taskData.put("rewardType",followTask.getRewardType());
                                //奖励
                                if(Optional.ofNullable(followTask.getRewardType()).isPresent() && followTask.getRewardType() == 10){
                                    if(Optional.ofNullable(followTask.getReward()).isPresent())taskData.put("reward",num1.format(followTask.getReward()/100.0));
                                }else {
                                    taskData.put("reward",followTask.getReward());
                                }

                                RewardRule rewardRule = followTask.getRewardRule();

                                //循环条件
                                if(rewardRule.getType() == 10){
                                    //一级目标
                                    Integer conditionOne = rewardRule.getDetail().getCondition();
                                    Integer outConditionOne= followTask.getCountValue() - conditionOne;
                                    if(Optional.ofNullable(followTask.getTargetId()).isPresent()&& Arrays.binarySearch(targerIdsofMoney,followTask.getTargetId()) > -1){
                                        taskData.put("conditionOne",num1.format(conditionOne/100.0));
                                        //超出一级目标
                                        taskData.put("outConditionOne",num1.format(outConditionOne/100.0));
                                        //一级目标完成率
                                        taskData.put("outConditionOneRate",num.format((followTask.getCountValue() * 1.0)/conditionOne));
                                    }else {
                                        taskData.put("conditionOne",conditionOne);
                                        taskData.put("outConditionOne",outConditionOne);
                                        taskData.put("outConditionOneRate",num.format((followTask.getCountValue() * 1.0)/conditionOne));
                                    }

                                }
                                //阶梯条件
                                if(rewardRule.getType() == 20){
                                    RewardRule.Detail[] ladders = rewardRule.getLadders();

                                    Integer conditionOne = ladders[0].getCondition();
                                    Integer outConditionOne = followTask.getCountValue() - conditionOne;

                                    if(Optional.ofNullable(followTask.getTargetId()).isPresent()&& Arrays.binarySearch(targerIdsofMoney,followTask.getTargetId()) > -1){
                                        taskData.put("conditionOne",num1.format(conditionOne/100.0));
                                        //超出一级目标
                                        taskData.put("outConditionOne",num1.format(outConditionOne/100.0));
                                        //一级目标完成率
                                        taskData.put("outConditionOneRate",num.format((followTask.getCountValue() * 1.0)/conditionOne));
                                        if(ladders.length > 1){
                                            //二级目标
                                            taskData.put("conditionTwo",num1.format(ladders[1].getCondition()/100.0));
                                            if(ladders.length > 2){
                                                //三级目标
                                                taskData.put("conditionThree",num1.format(ladders[2].getCondition()/100.0));
                                            }
                                        }
                                    }else {
                                        taskData.put("conditionOne",conditionOne);
                                        taskData.put("outConditionOne",outConditionOne);
                                        taskData.put("outConditionOneRate",num.format((followTask.getCountValue() * 1.0)/conditionOne));
                                        if(ladders.length > 1){
                                            //二级目标
                                            taskData.put("conditionTwo",ladders[1].getCondition());
                                            if(ladders.length > 2){
                                                //三级目标
                                                taskData.put("conditionThree",ladders[2].getCondition());
                                            }
                                        }
                                    }
                                }

                                //阶梯条件
                                if(rewardRule.getType() == 30){
                                    RewardRule.Detail[] intervals = rewardRule.getIntervals();

                                    Integer conditionOne = intervals[0].getIntervalMax();
                                    Integer outConditionOne = followTask.getCountValue() - conditionOne;

                                    if(Optional.ofNullable(followTask.getTargetId()).isPresent()&& Arrays.binarySearch(targerIdsofMoney,followTask.getTargetId()) > -1){
                                        taskData.put("conditionOne",conditionOne == -1 ? "无穷大" : num1.format(conditionOne/100.0));
                                        //超出一级目标
                                        taskData.put("outConditionOne",conditionOne == -1 ? 0 : num1.format(outConditionOne/100.0));
                                        //一级目标完成率
                                        taskData.put("outConditionOneRate",conditionOne == -1 ? 0 : num.format((followTask.getCountValue() * 1.0)/conditionOne));
                                        if(intervals.length > 1){
                                            //二级目标
                                            taskData.put("conditionTwo",intervals[1].getIntervalMax() == -1 ? "无穷大" : num1.format(intervals[1].getIntervalMax()/100.0));
                                            if(intervals.length > 2){
                                                //三级目标
                                                taskData.put("conditionThree",intervals[2].getIntervalMax() == -1 ? "无穷大" : num1.format(intervals[2].getIntervalMax()/100.0));
                                            }
                                        }
                                    }else {
                                        taskData.put("conditionOne",conditionOne == -1 ? "无穷大" : conditionOne);
                                        taskData.put("outConditionOne",conditionOne == -1 ? 0 : outConditionOne);
                                        taskData.put("outConditionOneRate",conditionOne == -1 ? 0 : num.format((followTask.getCountValue() * 1.0)/conditionOne));
                                        if(intervals.length > 1){
                                            //二级目标
                                            taskData.put("conditionTwo",intervals[1].getIntervalMax() == -1 ? "无穷大" : intervals[1].getIntervalMax());
                                            if(intervals.length > 2){
                                                //三级目标
                                                taskData.put("conditionThree",intervals[2].getIntervalMax() == -1 ? "无穷大" : intervals[2].getIntervalMax());
                                            }
                                        }
                                    }
                                }

                                return taskData;}).collect(toList());
                        }

                        //报表各个任务数据封装
                        Map<String,Object> task= new HashMap<String,Object>();
                        task.put("id",taskId);
                        task.put("taskDetailsdata",taskDatas);
                        task.put("name",bTask.getName());
                        task.put("timeType",timeType);
                        task.put("targetName",tQuota.getName());
                        task.put("exportTime",dateFormat1.format(date));
                        task.put("object",bTask.getObject());
                        task.put("rewardType",bTask.getRewardType());
                        tasksData.add(task);
                    });
                }
                taskPool.shutdown();
                while (!taskPool.awaitTermination(1000L, TimeUnit.MILLISECONDS));
                //set列表数据
                taskPlan.put("tasksData",tasksData);
            } catch (Exception e) {
                LOGGER.error("任务报表数据",e);
            }
            return taskPlan;
        }

        return null;
    }

//    public List<Map<String,Object>> getTaskPlanListReportData(String pids,final Integer siteId){
//
//        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        final DateFormat dateFormat2 = new SimpleDateFormat("yyyy年第M月");
//        final Date date = new Date();
//
//        try {
//            Integer[] planIds = String2IntegerArray(pids,",");
//            if(planIds != null){
//                //要返回的计划数据组
//                List<Map<String,Object>> taskPlansData = new ArrayList<Map<String,Object>>();
//                //主线程池
//                ExecutorService planPool = Executors.newFixedThreadPool(10);
//                //任务计划循环进行查询
//                for (Integer planId : planIds) {
//                    //开启计划子线程
//                    planPool.execute(() -> {
//                        BTaskplan bTaskplan = planService.findByid(planId);
//                        //要返回的计划数据
//                        Map<String,Object> taskPlan = new HashMap<String,Object>();
//                        taskPlan.put("id",planId);
//                        taskPlan.put("name",bTaskplan.getName());
//
//                        Integer[] taskIds = String2IntegerArray(bTaskplan.getTaskIds(),",");
//                        if(taskIds != null){
//                            try {
//                                //子线程池
//                                ExecutorService taskPool = Executors.newCachedThreadPool();
//                                //要返回的任务数据
//                                List<Map<String,Object>> tasksData = new ArrayList<Map<String,Object>>();
//                                for (Integer taskId : taskIds){
//                                    //开启任务子线程
//                                    taskPool.execute(()->{
//                                        BTask bTask = taskMapper.findById(taskId);
//                                        Map<String,Object> params = new HashMap<String,Object>(){{
//                                            put("siteId",siteId);
//                                            put("object",bTaskplan.getJoinType());
//                                            put("planId",planId);
//                                            put("taskId",taskId);
//                                        }};
//
//                                        if(bTask.getTimeType() == 10){
//                                            params.put("timeType",dateFormat.format(date));
//                                        }else if(bTask.getTimeType() == 20){
//                                            Calendar cal = Calendar.getInstance();
//                                            cal.setTime(date);
//                                            params.put("timeType", cal.get(Calendar.YEAR) + "年第" + cal.get(Calendar.WEEK_OF_YEAR) + "周");
//                                        }else if(bTask.getTimeType() == 30){
//                                            params.put("timeType",dateFormat2.format(date));
//                                        }
//
//                                        List<FollowTask> followTasks = planService.taskFollow(params);
//                                        Map<String,Object> task= new HashMap<String,Object>();
//                                        task.put("id",taskId);
//                                        task.put("taskDetailsdata",followTasks);
//                                        task.put("name",bTask.getName());
//                                        tasksData.add(task);
//                                    });
//                                }
//                                //set列表数据
//                                taskPlan.put("tasksData",tasksData);
//                                taskPool.shutdown();
//                                while (!taskPool.awaitTermination(1000L, TimeUnit.MILLISECONDS));
//                            } catch (Exception e) {
//                                LOGGER.error("任务报表数据",e);
//                            }
//                        }
//                        //set计划数据
//                        taskPlansData.add(taskPlan);
//                    });
//                }
//                planPool.shutdown();
//                while (!planPool.awaitTermination(1000L, TimeUnit.MILLISECONDS));
//                return taskPlansData;
//            }
//        } catch (Exception e) {
//            LOGGER.error("任务计划报表数据",e);
//        }
//        return null;
//    }


    public Integer[] String2IntegerArray(String str,String regex){
        if(StringUtils.isNotBlank(str)){
            try {
                String[] strs = str.split(regex);

                Integer[] nums = new Integer[strs.length];

                for (int i = 0; i < strs.length ; i++) {
                    nums[i] = Integer.valueOf(strs[i]);
                }
                return nums;
            } catch (Exception e) {
                return null;
            }
        }else {
            return null;
        }
    }

}

