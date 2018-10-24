package com.jk51.modules.task.job;

import com.jk51.Bootstrap;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.task.*;
import com.jk51.modules.appInterface.service.AppMemberService;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.task.domain.BTaskPlanStatus;
import com.jk51.modules.task.domain.count.AbstractCounter;
import com.jk51.modules.task.domain.count.CounterFactory;
import com.jk51.modules.task.domain.count.Counts;
import com.jk51.modules.task.domain.count.ExamCounter;
import com.jk51.modules.task.mapper.*;
import com.jk51.modules.task.service.TaskPlanService;
import com.jk51.modules.task.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
@Transactional
@Rollback
public class TaskScheduleTest {
    @Autowired
    TaskSchedule taskSchedule;

    @Autowired
    TaskPlanCountMapper taskPlanCountMapper;

    @Autowired
    BTaskExecuteMapper bTaskExecuteMapper;

    @Autowired
    BTaskplanMapper bTaskplanMapper;

    @Autowired
    BTaskMapper bTaskMapper;

    @Autowired
    TaskService taskService;

    @Autowired
    TQuotaMapper tQuotaMapper;

    @Autowired
    AppMemberService appMemberService;

    @Autowired
    TaskPlanService taskPlanService;

    @Test
    public void schedule() throws Exception {
        taskSchedule.schedule();
    }

    @Test
    public void autoChangeStatus() {
        taskSchedule.autoChangeStatus();
    }

    @Test
    @Rollback(false)
    public void change() {
        taskPlanService.changeStatus(new int[]{51}, BTaskPlanStatus.STARTING.getValue());
    }

    @Test
    public void autoNotifyMes() throws Exception {
        taskSchedule.autoNotifyMes();
//        appMemberService.notifyTaskMessage(100190,null,1010, PushType.TASK_NEWTASK, "hhh");
    }

    @Test
    public void pushMesByPlanIds() {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                taskSchedule.pushMesByPlanIds("34,35,36");
//            }
//        },30*1000,60*1000);
//        while (true);
    }


    @Test
    public void handleSiteTaskPlan() throws Exception {
    }

    @Test
    public void handleTaskPlan() throws Exception {
    }

    @Test
    public void test1() {
        taskPlanCountMapper.selectBmemberBySiteIdAndCreateTime(100190, "mem_source=120", LocalDateTime.now().plusDays(-1L), LocalDateTime.now());
    }

    @Test
    @Rollback(false)
    public void testExecute() {
        BTaskExecute bTaskExecute = bTaskExecuteMapper.selectByPrimaryKey(10319);
        BTask bTask = bTaskMapper.selectByPrimaryKey(bTaskExecute.getTaskId());
        BTaskplan bTaskplan = bTaskplanMapper.selectByPrimaryKey(bTaskExecute.getPlanId());

        // 统计类型
        List<Integer> typeIdList = StringUtil.split(bTask.getTypeIds(), ",", Integer::parseInt);
        List<TCounttype> tCounttypes = taskService.findCountTypeList(typeIdList);

        TQuota tQuota = tQuotaMapper.selectByPrimaryKey(bTask.getTargetId());
        AbstractCounter counter = CounterFactory.create(tQuota.getType());
        Counts counts = new Counts(bTaskplan, bTask, bTaskExecute);
        counts.count(counter, tCounttypes);
    }

    @Test
    @Rollback(false)
    public void testExamCommit() {
        BTaskExecute bTaskExecute = bTaskExecuteMapper.selectByPrimaryKey(3013);
        BTask bTask = bTaskMapper.selectByPrimaryKey(bTaskExecute.getTaskId());
        BTaskplan bTaskplan = bTaskplanMapper.selectByPrimaryKey(bTaskExecute.getPlanId());
        Counts counts = new Counts(bTaskplan, bTask, bTaskExecute);
        ExamCounter examCounter = new ExamCounter(4, 101254);
        counts.count(examCounter);
    }
}
