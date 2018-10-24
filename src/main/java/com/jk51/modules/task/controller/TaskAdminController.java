package com.jk51.modules.task.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.model.Message;
import com.jk51.model.task.*;
import com.jk51.modules.es.entity.ReturnDto;
import com.jk51.modules.task.domain.BTaskPlanStatus;
import com.jk51.modules.task.domain.count.AbstractCounter;
import com.jk51.modules.task.domain.count.CounterFactory;
import com.jk51.modules.task.domain.count.Counts;
import com.jk51.modules.task.job.TaskQueueWorker;
import com.jk51.modules.task.mapper.*;
import com.jk51.modules.task.service.TaskPlanService;
import com.jk51.modules.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/task/admin")
public class TaskAdminController {

    @Autowired
    BTaskExecuteMapper bTaskExecuteMapper;

    @Autowired
    BTaskMapper bTaskMapper;

    @Autowired
    BTaskplanMapper bTaskplanMapper;

    @Autowired
    TaskService taskService;

    @Autowired
    TQuotaMapper tQuotaMapper;

    @Autowired
    BTaskcountMapper bTaskcountMapper;

    @Autowired
    TaskQueueWorker taskQueueWorker;

    @Autowired
    TaskPlanService taskPlanService;

    @RequestMapping("/execute/{id:\\d+}")
    public ReturnDto execute(@PathVariable("id") int id) {
        BTaskExecute bTaskExecute = bTaskExecuteMapper.selectByPrimaryKey(id);
        BTask bTask = bTaskMapper.selectByPrimaryKey(bTaskExecute.getTaskId());
        BTaskplan bTaskplan = bTaskplanMapper.selectByPrimaryKey(bTaskExecute.getPlanId());

        // 统计类型
        List<Integer> typeIdList = Arrays.stream(bTask.getTypeIds().split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList());
        List<TCounttype> tCounttypes = taskService.findCountTypeList(typeIdList);

        TQuota tQuota = tQuotaMapper.selectByPrimaryKey(bTask.getTargetId());
        AbstractCounter counter = CounterFactory.create(tQuota.getType());
        Counts counts = new Counts(bTaskplan, bTask, bTaskExecute);
        counts.count(counter, tCounttypes);

        return ReturnDto.buildSuccessReturnDto();
    }

    @RequestMapping("/consume")
    public ReturnDto consume(@RequestBody Integer[] ids) {
        Arrays.stream(ids).forEach(id -> {
            Message message = new Message();
            BTaskcount bTaskcount = bTaskcountMapper.selectByPrimaryKey(id);
            message.setMessageBody(JSONObject.toJSONString(bTaskcount));
            try {
                taskQueueWorker.consume(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return ReturnDto.buildSuccessReturnDto();
    }

    @GetMapping("/changeStatus/{id:\\d+}")
    public ReturnDto changeStatus(@PathVariable("id") int id) {
        taskPlanService.changeStatus(new int[]{id}, BTaskPlanStatus.STARTING.getValue());

        return ReturnDto.buildSuccessReturnDto();
    }
}
