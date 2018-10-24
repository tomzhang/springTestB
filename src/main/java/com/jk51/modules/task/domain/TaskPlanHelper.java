package com.jk51.modules.task.domain;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.task.BTask;
import com.jk51.model.task.BTaskExecute;
import com.jk51.model.task.BTaskplan;
import com.jk51.modules.task.domain.reward.*;
import com.jk51.modules.task.mapper.TaskPlanCountMapper;
import com.jk51.modules.task.service.TaskPlanService;
import com.jk51.modules.task.service.TaskService;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.jk51.model.task.BTaskplan.ActiveType.EVERY_DAY;
import static com.jk51.model.task.BTaskplan.ActiveType.EVERY_MONTH;
import static com.jk51.model.task.BTaskplan.ActiveType.EVERY_WEEK;
import static com.jk51.modules.task.domain.reward.RewardResult.DEFAULT_RESULT;

@Component
public class TaskPlanHelper {
    @Autowired
    TaskService taskService;

    @Autowired
    TaskPlanService taskPlanService;

    @Autowired
    TaskPlanCountMapper taskPlanCountMapper;

    /**
     * 获取任务奖励对象id列表
     * @return
     */
    public List<Integer> getRewardIds(Integer planId, Integer taskId) {
        Objects.requireNonNull(planId, "计划id不能为空");
        Objects.requireNonNull(taskId, "任务id不能为空");

        BTask bTask = taskService.selectByPrimaryKey(taskId);
        BTaskplan bTaskplan = taskPlanService.selectByPrimaryKey(planId);

        return getRewardIds(bTask.getSiteId(), bTask.getObject(), bTaskplan.getJoinType(), bTaskplan.getJoinIds());
    }

    public List<Integer> getRewardIds(Integer siteId, Byte rewardObject, Byte joinType, String joinIds) {
        List<Integer> joinIdList = stringToList(joinIds);
        Objects.requireNonNull(joinIds, "计划发放对象不能为空");

        return getRewardIds(siteId, rewardObject, joinType, joinIdList);
    }

    /**
     * 获取任务奖励对象id列表
     * @param siteId 商户编号
     * @param rewardObject 任务奖励对象
     * @param joinType 计划参与类型
     * @param joinIds 计划参与类型id
     * @return
     */
    public List<Integer> getRewardIds(Integer siteId, Byte rewardObject, Byte joinType, List<Integer> joinIds) {
        Objects.requireNonNull(siteId, "商户id不能为空");
        Objects.requireNonNull(rewardObject, "奖励对象不能为空");
        Objects.requireNonNull(joinType, "计划发放对象类型不能为空");
        Objects.requireNonNull(joinIds, "计划发放对象不能为空");

        if (Objects.equals(rewardObject, joinType)) {
            return joinIds;
        }

        if(joinType == 20){
            return taskPlanCountMapper.selectStoreIdByAdminList(siteId,joinIds);
        }

        if (rewardObject == 10) {
            // 门店下所有店员
            return taskPlanCountMapper.selectAdminIdByStoreList(siteId, joinIds);
        }

        // 店员所属门店
        return taskPlanCountMapper.selectAdminIdByStoreList(siteId, joinIds);
    }

    /**
     * 获取实际奖励id
     * @param storeAdminId 门店店员id
     * @param rewardObject 任务奖励对象类型
     * @return
     */
    public int getRealRewardId(Integer siteId, Integer storeAdminId, Byte rewardObject) {
        if (rewardObject == 10) {
            // 给门店
            return taskPlanCountMapper.selectStoreIdByAdminId(siteId, storeAdminId);
        }

        return storeAdminId;
    }

    public static List<Integer> stringToList(String s) {
        return StringUtil.split(s, ",", Integer::parseInt);
    }

    /**
     * 计算奖励
     * @param totalCountValue
     * @param rewardRule
     * @return
     */
    public RewardResult calcReward(int totalCountValue, RewardRule rewardRule) {
        RewardResult rewardResult;
        if (rewardRule.getType() == 10) {
            // 按比例
            rewardResult = new TaskRewardMather(new RatioMather(totalCountValue, rewardRule.getDetail())).calc();
        } else if (rewardRule.getType() == 20) {
            // 阶梯
            rewardResult = new TaskRewardMather(new LadderMather(totalCountValue, rewardRule.getLadders())).calc();
        } else {
            // 区间比例
            rewardResult = new TaskRewardMather(new IntervalMather(totalCountValue, rewardRule.getIntervals())).calc();
        }

        return rewardResult;
    }

    public static boolean isActiveDay(byte activeType, int[] dayList, LocalDateTime dateTime) {
        if (activeType == EVERY_DAY.getValue() || dayList.length == 0) {
            // 每天 或者 周月没有选择具体日期
            return true;
        }

        int dayOfType;
        if (activeType == EVERY_WEEK.getValue()) {
            // 每周
            dayOfType = dateTime.getDayOfWeek().getValue();
            if (!ArrayUtils.contains(dayList, dayOfType)) {
                return false;
            }

            return true;
        } else if (activeType == EVERY_MONTH.getValue()) {
            dayOfType = dateTime.getDayOfMonth();
            if (!ArrayUtils.contains(dayList, dayOfType)) {
                return false;
            }

            return true;
        }

        return false;
    }
}
