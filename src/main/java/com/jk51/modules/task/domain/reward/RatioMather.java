package com.jk51.modules.task.domain.reward;

import com.jk51.modules.task.domain.RewardRule;

import java.util.Optional;

import static com.jk51.modules.task.domain.reward.RewardResult.DEFAULT_RESULT;

public class RatioMather implements RewardCalcStrategy {
    private Integer totalCountValue;
    private RewardRule.Detail detail;

    public RatioMather(Integer totalCountValue, RewardRule.Detail detail) {
        this.totalCountValue = totalCountValue;
        this.detail = detail;
    }

    @Override
    public RewardResult calc() {
        if (detail.getCondition() == 0) {
            return DEFAULT_RESULT;
        }

        int multipleNum = totalCountValue / detail.getCondition();
        int topLimit = Optional.ofNullable(detail.getTopLimit()).orElse(0);
        int rValue = multipleNum * detail.getReward();
        // 有上限 超过上限直接奖励上限值
        if (topLimit > 0) {
            rValue = rValue > topLimit ? topLimit : rValue;
        }

        return new RewardResult(rValue, multipleNum);
    }
}
