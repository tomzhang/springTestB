package com.jk51.modules.task.service;

import com.jk51.Bootstrap;
import com.jk51.modules.task.domain.BTaskPlanStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class TaskServiceTest {
    @Autowired
    TaskService taskService;

    @Test
    public void checkTest(){
        int[] ids = {1};
        Map<String, Object> result = taskService.changeStatus(ids, BTaskPlanStatus.DELETE.getValue());
        System.out.println(result);
    }
}