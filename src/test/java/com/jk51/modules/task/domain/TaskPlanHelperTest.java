package com.jk51.modules.task.domain;

import com.jk51.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class TaskPlanHelperTest {
    @Autowired
    TaskPlanHelper taskPlanHelper;

    @Test
    public void getRewardIds() throws Exception {
        List<Integer> rewardIds = taskPlanHelper.getRewardIds(130, 231);
        assertTrue(rewardIds.contains(1179));
        assertTrue(rewardIds.contains(1177));
    }

    @Test
    public void stringToList() throws Exception {
    }

}