package com.jk51.modules.task.domain;

import com.jk51.modules.task.domain.validation.AllowValue;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * 任务奖励规则
 */
public class RewardRule {
    // 类型
    @NotNull(groups = {AddGroup.class, UpdateGroup.class}, message = "任务奖励计算方式不能为空")
    @AllowValue(value = {0, 10, 20, 30}, groups = {AddGroup.class, UpdateGroup.class}, message = "任务奖励计算方式只能是按比例和阶梯")
    private Byte type;

    // 每满多少，奖励多少（按比例）
    private Detail detail;

    //区间阶梯条件
    private Detail[] intervals;

    // 阶梯条件
    private Detail[] ladders;


    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public Detail[] getIntervals() {
        return intervals;
    }

    public void setIntervals(Detail[] intervals) {
        this.intervals = intervals;
    }

    public Detail[] getLadders() {
        return ladders;
    }

    public void setLadders(Detail[] ladders) {
        this.ladders = ladders;
    }

    @Override
    public String toString() {
        return "RewardRule{" +
                "type=" + type +
                ", detail=" + detail +
                ", intervals=" + Arrays.toString(intervals) +
                ", ladders=" + Arrays.toString(ladders) +
                '}';
    }

    public static class Detail {
        // 满多少
        private Integer condition;
        // 送多少
        private Integer reward;

        // 上限
        private Integer topLimit;

        //区间最小值
        private Integer intervalMin;

        //区间最大值
        private Integer intervalMax;


        public Integer getCondition() {
            return condition;
        }

        public void setCondition(Integer condition) {
            this.condition = condition;
        }

        public Integer getReward() {
            return reward;
        }

        public void setReward(Integer reward) {
            this.reward = reward;
        }

        public Integer getTopLimit() {
            return topLimit;
        }

        public void setTopLimit(Integer topLimit) {
            this.topLimit = topLimit;
        }

        public Integer getIntervalMin() {
            return intervalMin;
        }

        public void setIntervalMin(Integer intervalMin) {
            this.intervalMin = intervalMin;
        }

        public Integer getIntervalMax() {
            return intervalMax;
        }

        public void setIntervalMax(Integer intervalMax) {
            this.intervalMax = intervalMax;
        }

        @Override
        public String toString() {
            return "Detail{" +
                    "condition=" + condition +
                    ", reward=" + reward +
                    ", topLimit=" + topLimit +
                    ", intervalMin=" + intervalMin +
                    ", intervalMax=" + intervalMax +
                    '}';
        }
    }
}
