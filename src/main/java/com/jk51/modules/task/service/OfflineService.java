package com.jk51.modules.task.service;

import com.alibaba.fastjson.JSONObject;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.JKHashMap;
import com.jk51.model.task.BTask;
import com.jk51.model.task.BTaskExecute;
import com.jk51.model.task.BTaskplan;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.task.domain.RewardRule;
import com.jk51.modules.task.domain.TaskPlanHelper;
import com.jk51.modules.task.domain.count.Counts;
import com.jk51.modules.task.domain.count.ExamCounter;
import com.jk51.modules.task.domain.reward.RewardResult;
import com.jk51.modules.task.mapper.BTaskExecuteMapper;
import com.jk51.modules.task.mapper.BTaskMapper;
import com.jk51.modules.task.mapper.BTaskplanMapper;
import com.jk51.modules.task.mapper.TaskPlanCountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/4/16.
 */
@Service
public class OfflineService {
    private static final Logger logger= LoggerFactory.getLogger(OfflineService.class);
    @Autowired
    private BTaskExecuteMapper bTaskExecuteMapper;
    @Autowired
    BTaskMapper bTaskMapper;
    @Autowired
    BTaskplanMapper bTaskplanMapper;
    @Autowired
    TaskPlanService taskPlanService;
    @Autowired
    StoreAdminMapper storeAdminMapper;
    @Autowired
    TaskPlanCountMapper taskPlanCountMapper;

    public JKHashMap<String,Object> commitOffline(Map<String,Object> param) throws BusinessLogicException {
        Objects.requireNonNull(param, "提交信息不能为空");
        int executeId = Integer.valueOf(param.get("executeId").toString());
        int storeAdminId = Integer.valueOf(param.get("storeAdminId").toString());

        BTaskExecute bTaskExecute = bTaskExecuteMapper.selectByPrimaryKey(executeId);
        if (Objects.isNull(bTaskExecute)) {
            throw new BusinessLogicException("任务不存在");
        }

        BTask bTask = bTaskMapper.selectByPrimaryKey(bTaskExecute.getTaskId());
        BTaskplan bTaskplan = bTaskplanMapper.selectByPrimaryKey(bTaskExecute.getPlanId());

        if (!(bTaskplan.getActiveType()==10)) {
            int[] dayList = Arrays.stream(bTaskplan.getDayNum().split(",")).mapToInt(i -> Integer.parseInt(i)).toArray();
            if (!taskPlanService.isActiveDay(bTask.getTimeType(), dayList, LocalDateTime.now())) {
                throw new BusinessLogicException("今天不是任务有效日期，不能提交");
            }
        }

        int siteId = Integer.valueOf(param.get("siteId").toString());
        String nums=param.get("commitNum").toString();
        Double cnm=Double.parseDouble(nums);
        Integer commitNum=(new   Double(cnm)).intValue();
        //根据统计类型计算单位
        if(bTask.getTargetId() == 3 || bTask.getTargetId() == 4 || bTask.getTargetId() == 5 || bTask.getTargetId() == 6){

            commitNum =(new   Double(cnm*100)).intValue();
        }
        // 店员信息
        JKHashMap<String, Object> storeAdminInfo = storeAdminMapper.selectSimpleInfo(siteId, storeAdminId);
        Objects.requireNonNull(storeAdminInfo, "店员不存在");

        // 奖励统计
        Counts counts = new Counts(bTaskplan, bTask, bTaskExecute);
        ExamCounter examCounter = new ExamCounter(commitNum, storeAdminId);
        counts.count(examCounter);

        TaskPlanHelper taskPlanHelper = new TaskPlanHelper();
        RewardRule rewardRule = JSONObject.parseObject(bTask.getRewardDetail(), RewardRule.class);
        RewardResult rewardResult = taskPlanHelper.calcReward(commitNum, rewardRule);
        JKHashMap<String, Object> result = new JKHashMap();
        // 奖励多少
        result.put("rewardValue", rewardResult.getReward());
        // 奖励目标级别
        result.put("targetLevel", rewardResult.getTargetLevel());
        // 奖励什么
        result.put("rewardType", bTask.getRewardType());

        return result;
    }
}
