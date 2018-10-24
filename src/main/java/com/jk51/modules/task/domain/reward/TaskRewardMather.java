package com.jk51.modules.task.domain.reward;

public class TaskRewardMather {
    private RewardCalcStrategy rewardCalcStrategy;

    public TaskRewardMather(RewardCalcStrategy rewardCalcStrategy) {
        this.rewardCalcStrategy = rewardCalcStrategy;
    }

    public RewardResult calc() {
        return rewardCalcStrategy.calc();
    }
}
