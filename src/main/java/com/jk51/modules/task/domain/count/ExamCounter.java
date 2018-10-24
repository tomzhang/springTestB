package com.jk51.modules.task.domain.count;

import com.alibaba.fastjson.JSONObject;
import com.jk51.model.task.BTask;
import com.jk51.model.task.BTaskExecute;
import com.jk51.model.task.BTaskplan;
import com.jk51.modules.goods.library.SpringContextUtil;
import com.jk51.modules.task.domain.RewardRule;
import com.jk51.modules.task.domain.TaskPlanHelper;
import com.jk51.modules.task.domain.reward.RewardResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * 试卷答题统计
 */
public class ExamCounter implements Countable {
    private Integer success;
    private Integer storeAdminId;

    @Autowired
    private TaskPlanHelper taskPlanHelper;

    public ExamCounter(Integer success, Integer storeAdminId) {
        this.success = success;
        this.storeAdminId = storeAdminId;

        taskPlanHelper = new TaskPlanHelper();
        SpringContextUtil.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public Map<Integer, Long> count(BTask bTask, BTaskplan bTaskplan, BTaskExecute bTaskExecute) {
//        RewardRule rewardRule = JSONObject.parseObject(bTask.getRewardDetail(), RewardRule.class);
//        RewardResult rewardResult = taskPlanHelper.calcReward(success, rewardRule);
        int joinId = taskPlanHelper.getRealRewardId(bTask.getSiteId(), storeAdminId, bTask.getObject());

        Map<Integer, Long> map = new HashMap<>();
        map.put(joinId, (long)success);

        return map;
    }
}
