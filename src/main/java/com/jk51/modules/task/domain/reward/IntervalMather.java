package com.jk51.modules.task.domain.reward;

import com.jk51.modules.task.domain.RewardRule;

import java.util.Objects;

/**
 * 区间奖励计算
 */
public class IntervalMather implements RewardCalcStrategy {
    private Integer totalCountValue;
    private RewardRule.Detail[] intervals;

    public IntervalMather(Integer totalCountValue, RewardRule.Detail[] intervals) {
        this.totalCountValue = totalCountValue;
        this.intervals = intervals;
    }

    @Override
    public RewardResult calc() {
        // 总和
        int sum = 0;
        int targetLevel = 0;
        int lastMax = 0;
        for (RewardRule.Detail interval : intervals) {
            if (Objects.isNull(interval)) {
                continue;
            }


            // 最小值是上一个区间的最大值 忽略最小值
            int ladderValue = totalCountValue - interval.getIntervalMin() + (interval.getIntervalMin() == lastMax ? 0 : 1);


            if (totalCountValue > interval.getIntervalMax() && interval.getIntervalMax() != -1) {
                ladderValue -= totalCountValue - interval.getIntervalMax();
            }

            sum += calcLadderReward(ladderValue, interval);
            lastMax = interval.getIntervalMax();
            // 奖励目标级别
            targetLevel++;
            if (totalCountValue <= interval.getIntervalMax()) {
                break;
            }
        }

        // 没奖励就没有目标等级
        if (sum == 0) {
            targetLevel = 0;
        }

        return new RewardResult(sum, targetLevel);
    }

    /**
     * 计算区间奖励
     * @return
     */
    public int calcLadderReward(int ladderValue, RewardRule.Detail interval) {
        int ladderReward = ladderValue / interval.getCondition() * interval.getReward();
        if (interval.getTopLimit() != null && interval.getTopLimit() != 0 && ladderReward > interval.getTopLimit()) {
            // 超过上限
            ladderReward = interval.getTopLimit();
        }

        return ladderReward;
    }
}
