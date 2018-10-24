package com.jk51.modules.task.domain.reward;

public class RewardResult {
    private final Integer reward;
    private final Integer targetLevel;

    public static final RewardResult DEFAULT_RESULT = new RewardResult(0, 0);

    public RewardResult(Integer reward, Integer targetLevel) {
        this.reward = reward;
        this.targetLevel = targetLevel;
    }

    public Integer getReward() {
        return reward;
    }

    public Integer getTargetLevel() {
        return targetLevel;
    }
}
