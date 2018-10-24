package com.jk51.modules.task.domain.reward;

import com.jk51.modules.task.domain.RewardRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class IntervalMatherTest {
    @Test
    public void calc() throws Exception {
        RewardRule.Detail[] intervals = {new RewardRule.Detail(), new RewardRule.Detail(), new RewardRule.Detail()};
        // 1-5 每2个奖励1 封顶3
        intervals[0].setIntervalMin(1);
        intervals[0].setIntervalMax(5);
        intervals[0].setCondition(2);
        intervals[0].setTopLimit(3);
        intervals[0].setReward(1);

        // 5-10 每2个奖励2 不封顶
        intervals[1].setIntervalMin(5);
        intervals[1].setIntervalMax(10);
        intervals[1].setCondition(2);
        intervals[1].setReward(2);

        // 10-15 每2个奖励3 不封顶
        intervals[2].setIntervalMin(10);
        intervals[2].setIntervalMax(15);
        intervals[2].setCondition(2);
        intervals[2].setReward(3);

        RewardResult reward;

        reward = new IntervalMather(1, intervals).calc();
        assertEquals(0, reward.getReward().intValue());

        reward = new IntervalMather(2, intervals).calc();
        assertEquals(1, reward.getReward().intValue());

        reward = new IntervalMather(3, intervals).calc();
        assertEquals(1, reward.getReward().intValue());

        reward = new IntervalMather(4, intervals).calc();
        assertEquals(2, reward.getReward().intValue());

        reward = new IntervalMather(5, intervals).calc();
        assertEquals(2, reward.getReward().intValue());

        reward = new IntervalMather(6, intervals).calc();
        assertEquals(2, reward.getReward().intValue());

        reward = new IntervalMather(7, intervals).calc();
        assertEquals(4, reward.getReward().intValue());

        reward = new IntervalMather(8, intervals).calc();
        assertEquals(4, reward.getReward().intValue());

        reward = new IntervalMather(9, intervals).calc();
        assertEquals(6, reward.getReward().intValue());

        reward = new IntervalMather(10, intervals).calc();
        assertEquals(6, reward.getReward().intValue());

        reward = new IntervalMather(11, intervals).calc();
        assertEquals(6, reward.getReward().intValue());

        reward = new IntervalMather(12, intervals).calc();
        assertEquals(9, reward.getReward().intValue());

        reward = new IntervalMather(13, intervals).calc();
        assertEquals(9, reward.getReward().intValue());

        reward = new IntervalMather(14, intervals).calc();
        assertEquals(12, reward.getReward().intValue());

        reward = new IntervalMather(15, intervals).calc();
        assertEquals(12, reward.getReward().intValue());
    }

}
