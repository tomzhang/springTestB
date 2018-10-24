package com.jk51.modules.task.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.JKHashMap;
import com.jk51.model.task.BTask;
import com.jk51.model.task.BTaskExecute;
import com.jk51.model.task.BTaskplan;
import com.jk51.modules.task.domain.TaskPlanHelper;
import com.jk51.modules.task.domain.dto.RewardRankQueryDTO;
import com.jk51.modules.task.mapper.BTaskExecuteMapper;
import com.jk51.modules.task.mapper.BTaskMapper;
import com.jk51.modules.task.mapper.BTaskplanMapper;
import com.jk51.modules.task.mapper.BTaskrewardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author
 */
@Service
public class RewardRankService {
    private static final String COUNT_VALUE_KEY = "count_value";
    private static final String SORTED_KEY = "sorted";
    private static final String PLAN_ID_KEY = "plan_id";
    private static final String TASK_ID_KEY = "task_id";
    private static final String JOIN_ID_KEY = "join_id";
    private static final int DEFAULT_TOP = 3;

    @Autowired
    BTaskrewardMapper bTaskrewardMapper;

    @Autowired
    BTaskExecuteMapper bTaskExecuteMapper;

    @Autowired
    BTaskplanMapper bTaskplanMapper;

    @Autowired
    BTaskMapper bTaskMapper;

    @Autowired
    TaskPlanHelper taskPlanHelper;

    /**
     * 排名 缺少数据时使用默认的补齐
     * @param param
     * @return
     */
    public Map<String,Object> sortList(RewardRankQueryDTO param) {
        if (Objects.isNull(param.getTop())) {
            param.setTop(DEFAULT_TOP);
        }

        Map<String,Object> result = new HashMap<>(2);

        try {
            List<JKHashMap> softList = bTaskrewardMapper.selectSortList(param);
            int planId, taskId;
            if (softList.size() == 0) {
                // 没有任何排名数据 获取task_id、plan_id
                BTaskExecute bTaskExecute = bTaskExecuteMapper.selectByPrimaryKey(param.getExecuteId());
                planId = bTaskExecute.getPlanId();
                taskId = bTaskExecute.getTaskId();
            } else {
                JKHashMap first = softList.get(0);
                planId = first.getInt(PLAN_ID_KEY);
                taskId = first.getInt(TASK_ID_KEY);
            }
            BTaskplan bTaskplan = bTaskplanMapper.selectByPrimaryKey(planId);
            BTask bTask = bTaskMapper.selectByPrimaryKey(taskId);

            param.setJoinType(bTask.getObject());
            List<Integer> joinIds = StringUtil.split(bTaskplan.getJoinIds(), ",", Integer::valueOf);
            joinIds = taskPlanHelper.getRewardIds(bTaskplan.getSiteId(), bTask.getObject(), bTaskplan.getJoinType(), joinIds);

            final int personalId = bTask.getObject() == 10 ? param.getStoreId() : param.getJoinId();

            JKHashMap personal = findPersonal(softList, personalId, param);
            // 排名中是否包含当前用户
            boolean isNotPersonal = Objects.isNull(personal);
            int top = param.getTop();
            int fill = top - softList.size();
            int extra = isNotPersonal ? (fill <= top ? 0 : 1) : 0;
            fill += extra;

            if (fill > 0) {
                int[] existsIds = softList.stream().mapToInt(item -> item.getInt("join_id")).sorted().toArray();
                int[] selectedIds = new int[fill];
                int idx = 0;
                if (isNotPersonal && joinIds.contains(personalId)) {
                    selectedIds[idx++] = personalId;
                    int index = joinIds.indexOf(personalId);
                    if (index != -1) {
                        joinIds.remove(index);
                    }
                }
                for (Integer joinId : joinIds) {
                    int basic = joinId;
                    if (Arrays.binarySearch(existsIds, basic) == -1) {
                        selectedIds[idx++] = basic;
                        if (idx == fill) {
                            break;
                        }
                    }
                }
                // 补齐数量
                List<JKHashMap> noneRewardList = selectNoneReward(taskId, planId, param.getJoinType(), selectedIds);
                for (JKHashMap item : noneRewardList) {
                    if (isNotPersonal && item.getInt(JOIN_ID_KEY) == personalId) {
                        personal = item;
                        if (extra == 1) {
                            personal.put(SORTED_KEY, top + 1);
                        } else {
                            softList.add(item);
                        }
                    } else {
                        softList.add(item);
                    }
                }
            }

            for (int i = 0, size = softList.size(); i < size; i++) {
                softList.get(i).put(SORTED_KEY, i + 1);
            }

            result.put("items", softList);
            result.put("current", personal);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询没有奖励的数据
     * @param taskId
     * @param planId
     * @param joinType
     * @param selectedIds
     * @return
     */
    private List<JKHashMap> selectNoneReward(Integer taskId, Integer planId, Byte joinType, int[] selectedIds) {
        BTask bTask = bTaskMapper.selectByPrimaryKey(taskId);
        Byte rewardType = bTask.getRewardType();

        List<JKHashMap> noneRewardList = bTaskrewardMapper.selectNoneReward(joinType, selectedIds);
        for (JKHashMap item : noneRewardList) {
            setDefault(item, joinType, rewardType, planId);
        }

        return noneRewardList;
    }

    /**
     * 设置默认值
     * @param item
     * @param joinType
     * @param rewardType
     * @param planId
     */
    private void setDefault(JKHashMap item, Byte joinType, Byte rewardType, Integer planId) {
        item.put("reward", 0);
        item.put("join_type", joinType);
        item.put("reward", 0);
        item.put("reward_type", rewardType);
        item.put("complete", 0);
        item.put("plan_id", planId);
    }

    /**
     * 获取计划发放对象
     * @param planId
     * @return
     */
    private List<Integer> getPlanJoinIds(Integer planId) {
        BTaskplan bTaskplan = bTaskplanMapper.selectByPrimaryKey(planId);
        return StringUtil.split(bTaskplan.getJoinIds(), ",", Integer::valueOf);
    }

    private JKHashMap findPersonal(List<JKHashMap> softList, int personalId, RewardRankQueryDTO param) {
        for (JKHashMap item : softList) {
            if (item.getInt(JOIN_ID_KEY) == personalId) {
                return item;
            }
        }

        JKHashMap personal = bTaskrewardMapper.selectPersonalSort(param);
        if (Objects.nonNull(personal)) {
            int rank = bTaskrewardMapper.selectRank(param.getExecuteId(), personal.getInt(COUNT_VALUE_KEY));
            personal.put(SORTED_KEY, rank);
        }

        return personal;
    }
}
