package com.jk51.modules.task.domain.reward;

import com.jk51.modules.task.domain.RewardRule;

import java.util.Objects;

import static com.jk51.modules.task.domain.reward.RewardResult.DEFAULT_RESULT;

public class LadderMather implements RewardCalcStrategy {
    private Integer totalCountValue;
    private RewardRule.Detail[] ladders;

    public LadderMather(Integer totalCountValue, RewardRule.Detail[] ladders) {
        this.totalCountValue = totalCountValue;
        this.ladders = ladders;
    }

    @Override
    public RewardResult calc() {
        if (Objects.isNull(ladders) || ladders.length == 0) {
            return DEFAULT_RESULT;
        }

        int lastReward = 0;
        int targetLevel = 0;
        for (RewardRule.Detail ladder : ladders) {
            if (totalCountValue >= ladder.getCondition()) {
                lastReward = ladder.getReward();
                targetLevel++;
            }
        }

        return new RewardResult(lastReward, targetLevel);
    }
}
