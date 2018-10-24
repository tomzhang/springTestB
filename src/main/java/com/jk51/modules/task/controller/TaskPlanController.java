package com.jk51.modules.task.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.JKHashMap;
import com.jk51.model.task.BTaskplan;
import com.jk51.modules.task.domain.BTaskPlanStatus;
import com.jk51.modules.task.domain.FollowTask;
import com.jk51.modules.task.service.TaskPlanService;
import net.sf.jsqlparser.statement.select.Join;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by guosheng on 2017/8/7.
 */
@RestController
@RequestMapping("/taskPlan")
public class TaskPlanController {
    public static final Logger logger = LoggerFactory.getLogger(TaskPlanController.class);

    @Autowired
    TaskPlanService taskPlanService;

    /**
     * 获取计划列表
     * @param
     * @return
     */
    @PostMapping("/getTaskPlan")
    public ReturnDto getTaskPlan(@RequestParam HashMap queryMap, int pageNum, int pageSize){


        ReturnDto response;
        try {
            List<Map<String,Object>> taskPlanList =taskPlanService.getTaskPlan(queryMap,pageNum,pageSize);
            response = ReturnDto.buildSuccessReturnDto(new PageInfo<>(taskPlanList));
        } catch (Exception e) {
            logger.error("获取任务计划列表失败!", e);
            response = ReturnDto.buildFailedReturnDto("获取任务计划列表失败:" + e);
        }
        return response;
    }

    /**
     * 任务计划详情
     * @param
     * @return
     */
    @PostMapping("/detailsQuery")
    public ReturnDto find(BTaskplan bTaskplan){
//        ValidationUtils.check(bindingResult);

        ReturnDto response;
        try {
            Map<String, Object> store =taskPlanService.getPlanDetails(bTaskplan);
            response = ReturnDto.buildSuccessReturnDto(store);
        } catch (Exception e) {
            logger.error("获取任务计划详情失败!", e);
            response = ReturnDto.buildFailedReturnDto("获取任务计划详情失败:" + e);
        }
        return response;
    }

    /**
     * 删除任务
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/delete")
    public ReturnDto delete (HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String ids = param.get("ids").toString();
        int[] id = Arrays.stream(ids.split(",")).mapToInt( taskId -> Integer.parseInt(taskId)).toArray();


        Map<String, Object> result = taskPlanService.changeStatus(id, BTaskPlanStatus.DELETE.getValue());

        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     * 结束任务
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/end")
    public ReturnDto end (HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String ids = param.get("ids").toString();
        int[] id = Arrays.stream(ids.split(",")).mapToInt( taskId -> Integer.parseInt(taskId)).toArray();

        //boolean complete = taskPlanService.endPlan(id);
            Map<String, Object> result = taskPlanService.endPlan(id);
            return ReturnDto.buildSuccessReturnDto(result);


    }

    /**
     * 发布任务计划
     * @param
     * @return
     */
//    @RequestMapping("/release")
    @RequestMapping(value="/release",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto release(BTaskplan taskplan) {

//        Map<String, Object> param = ParameterUtil.getParameterMap(request);

        if(taskplan.getJoinType() == 10 && "all".equals(taskplan.getJoinIds())){
            List<Integer> list =taskPlanService.queryStoreNumbers(taskplan.getSiteId());
            String ids= Joiner.on(",").join(list);
            taskplan.setJoinIds(ids);
        }
        if(taskplan.getJoinType() == 20 && "all".equals(taskplan.getJoinIds())){
            List<Integer> list=taskPlanService.queryStoreAdminIds(taskplan.getSiteId());
            String ids=Joiner.on(",").join(list);
            taskplan.setJoinIds(ids);
        }
        int count =taskPlanService.release(taskplan);
        if (0 != count){
            return ReturnDto.buildStatusOK();
        }

        return ReturnDto.buildFailedReturnDto("新增" + count + "条任务计划");
    }

    /**
     * 任务追踪标头
     * @param id
     * @return
     */
    @RequestMapping(value = "/taskIdsList")
    @ResponseBody
    public ReturnDto taskIdsList(Integer id){
        return ReturnDto.buildListOnEmptyFail(taskPlanService.getTaskIdsList(id));
    }

    /**
     * 任务追踪
     * @param request
     * @return
     */
    @RequestMapping(value = "/taskFollow", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto taskFollow(HttpServletRequest request,@RequestParam(required = true, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "15") int pageSize){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Map<String,Object> result=new HashMap<>();
        param=taskPlanService.taskParam(param);
        Map<String,Object> taskCount=taskPlanService.taskCount(param);
        PageHelper.startPage(page,pageSize);
        List<FollowTask> list=taskPlanService.taskFollow(param);
        PageInfo pageInfo=new PageInfo<>(list);
        result.put("taskCount",taskCount);
        result.put("page",pageInfo);
        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     * 任务导出
     * @param request
     * @return
     */
    @RequestMapping(value = "/taskFollowReport", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto taskFollow(HttpServletRequest request){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Map<String,Object> result=new HashMap<>();
        param=taskPlanService.taskParam(param);
        Map<String,Object> taskCount=taskPlanService.taskCount(param);
        List<FollowTask> list=taskPlanService.taskFollow(param);
        result.put("taskCount",taskCount);
        result.put("list",list);
        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     * 任务计划时间
     * @param id
     * @return
     */
    @RequestMapping(value = "/taskPlanTime")
    @ResponseBody
    public ReturnDto taskPlanTime(Integer id){
        return ReturnDto.buildSuccessReturnDto(taskPlanService.taskPlanTime(id));
    }

    @RequestMapping(value = "/executePlanTime")
    @ResponseBody
    public ReturnDto executePlanTime(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        return ReturnDto.buildListOnEmptyFail(taskPlanService.executeTime(param));
    }
}
