package com.jk51.modules.clerkvisit.job;

import com.jk51.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by admin on 2018/5/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class VisitScheduleTest {

    @Autowired
    private VisitSchedule visitSchedule;


    @Test
    public void test() {
//        VisitSchedule visitSchedule = new VisitSchedule();
//        visitSchedule.changeClerkVisitStatus();
        visitSchedule.taskPush();
    }
}
