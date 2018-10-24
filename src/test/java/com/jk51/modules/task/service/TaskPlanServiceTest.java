package com.jk51.modules.task.service;

import com.jk51.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class TaskPlanServiceTest {
    @Autowired
    TaskPlanService taskPlanService;

    @Test
    public void selectUncompleted() throws Exception {
//        taskPlanService.selectUncompleted(1);
    }

    @Test
    public void updateExecuteComplete() {
        boolean b = taskPlanService.updateExecuteComplete(1586);
        System.out.print(b);
        while(true);
    }

}
