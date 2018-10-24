package com.jk51.modules.task.controller;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.SimplePage;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.task.BTask;
import com.jk51.modules.index.service.StoreAdminService;
import com.jk51.modules.task.domain.AddGroup;
import com.jk51.modules.task.domain.BTaskStatus;
import com.jk51.util.ValidationUtils;
import com.jk51.modules.task.domain.UpdateGroup;
import com.jk51.modules.task.domain.dto.BTaskDTO;
import com.jk51.modules.task.domain.dto.QueryBTaskDTO;
import com.jk51.modules.task.domain.dto.RewardRankQueryDTO;
import com.jk51.modules.task.job.TaskSchedule;
import com.jk51.modules.task.service.RewardRankService;
import com.jk51.modules.task.service.TaskService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

@RestController
@RequestMapping("/task")
public class TaskController {
    public static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    TaskService taskService;

    @Autowired
    private StoreAdminService storeAdminService;

    @Autowired
    RewardRankService rewardRankService;

    @Autowired
    TaskSchedule taskSchedule;

    /**
     * 任务列表查询
     * @param queryCondition
     * @param bindingResult
     * @return
     */
    @PostMapping("/find")
    public ReturnDto find(@Valid @RequestBody QueryBTaskDTO queryCondition, BindingResult bindingResult) throws BusinessLogicException {
        ValidationUtils.check(bindingResult);
        List<BTask> tasks = taskService.find(queryCondition);

        return ReturnDto.buildListOnEmptyFail(tasks);
    }

    /**
     * 添加任务
     * @param bTaskDTO
     * @param bindingResult
     * @return
     * @throws BusinessLogicException
     */
    @PostMapping("/save")
    public ReturnDto save(@Validated(AddGroup.class) @RequestBody BTaskDTO bTaskDTO, BindingResult bindingResult) throws BusinessLogicException {
        ValidationUtils.check(bindingResult);
        try {
            if (taskService.add(bTaskDTO)) {
                return ReturnDto.buildSuccessReturnDto(null);
            }else {
                return ReturnDto.buildFailedReturnDto("新建失败->插入异常");
            }
        } catch (BusinessLogicException e) {
            logger.error("新建任务出错 {} {} {}", bTaskDTO, e.getMessage(), e);
            return ReturnDto.buildFailedReturnDto("新建任务失败->" + e.getMessage());
        }
    }

    /**
     * 修改任务
     * @param bTaskDTO
     * @param bindingResult
     * @return
     * @throws BusinessLogicException
     */
    @PostMapping("/update")
    public ReturnDto update(@Validated(UpdateGroup.class) @RequestBody BTaskDTO bTaskDTO, BindingResult bindingResult) throws BusinessLogicException {
        ValidationUtils.check(bindingResult);
        if (taskService.update(bTaskDTO)) {
            return ReturnDto.buildSuccessReturnDto("更新成功");
        }

        return ReturnDto.buildFailedReturnDto("更新失败");
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
        int[] id = Arrays.stream(ids.split(",")).mapToInt(taskId -> Integer.parseInt(taskId)).toArray();

        Map<String, Object> result = taskService.changeStatus(id, BTaskStatus.SOFT_DELETE.getValue());

        Integer count = Integer.parseInt(result.get("seccessCount").toString());
        if (id.length == count){
            return ReturnDto.buildSuccessReturnDto(result);
        }

        return new ReturnDto("101", "成功删除"+count+"个任务，任务"+result.get("falseIds")+"在任务列表中，无法删除", result);
    }

    @PostMapping("/detail")
    public ReturnDto detail(HttpServletRequest request){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer id = Integer.parseInt(param.get("id").toString());
        Map<String, Object> bTask = taskService.taskDetail(id);


        return ReturnDto.buildSuccessReturnDto(bTask);
    }

    @RequestMapping("/specify_good")
    @ResponseBody
    public ReturnDto getTaskSpectifyGoods(Integer taskId,Integer pageSize,Integer pageNum,Integer siteId){

        try {
            return ReturnDto.buildSuccessReturnDto(taskService.getTaskSpectifyGoods(taskId,pageSize,pageNum,siteId));
        } catch (Exception e) {
            return ReturnDto.buildFailedReturnDto("查询任务指定商品失败 -> " + e.getMessage());
        }
    }

    @PostMapping("/taskList")
    public ReturnDto taskList(HttpServletRequest request){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        List<Map<String, Object>> bTask = taskService.queryTaskList(param);
        PageInfo pageInfo = new PageInfo<>(bTask);
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    @RequestMapping("/getQuotaGroupAndCounttype")
    @ResponseBody
    public ReturnDto getQuotaGroupAndCounttype(HttpServletRequest request, @RequestParam Map<String,Object> params){
        try {
            return ReturnDto.buildSuccessReturnDto(taskService.getQuotaGroupAndCounttype(params));
        } catch (Exception e) {
            logger.error("查询指标分组失败",e);
            return ReturnDto.buildFailedReturnDto("查询指标分组失败" + e.getMessage());
        }
    }
    @PostMapping("clerkTaskCount")
    public ReturnDto personalTaskCount(HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);//siteId,clerkId

        param.put("joinType",20);
        Map map;
        map = getQueryDate("1");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startTime = (LocalDate)map.get("startTime");
        LocalDate endTime = (LocalDate)map.get("endTime");

        param.put("start", startTime.format(formatter));
        param.put("end",endTime.format(formatter));
        if(map.containsKey("month") && map.containsKey("week")){
            param.put("week",map.get("week"));
            param.put("month",map.get("month"));
        }
        result = taskService.queryCounter(param);
        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     * @param request
     * @return
     */
    @RequestMapping("/personalTaskList")
    public ReturnDto personalTaskList(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Map<String,Object> result = new HashMap<>();
        List<Map<String,Object>> list;

        // 获取跟店员相关的进行中计划的is
        List<Integer> planIds = taskService.queryPlanIdsByJoin(param);

        if (CollectionUtils.isEmpty(planIds)) {
            return ReturnDto.buildFailedReturnDto(null);
        }

        param.put("planIds",planIds);
        Map map;
        // 获取查询日期
        if (param.containsKey("dateType") && !"".equals(param.get("dateType"))) {
            map = getQueryDate(param.get("dateType").toString());
        } else {
            map = getQueryDate("0");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startTime = (LocalDate)map.get("startTime");
        LocalDate endTime = (LocalDate)map.get("endTime");

        param.put("start", startTime.format(formatter));
        param.put("end",endTime.format(formatter));
        if(map.containsKey("month") && map.containsKey("week")){
            param.put("week",map.get("week"));
            param.put("month",map.get("month"));
        }

        list = taskService.queryTaskListByJoinId(param);
        PageInfo<?> pageInfo = new SimplePage<>(list).getPageInfo();

        result.put("isHasNext",pageInfo.isHasNextPage());
        result.put("pageSize",pageInfo.getPageSize());
        result.put("page",pageInfo.getPageNum());

        list = list.stream().sorted((p1,p2)->Double.valueOf(p2.get("getReward").toString()).compareTo(Double.valueOf(p1.get("complete").toString()))).collect(Collectors.toList());
        list = list.stream().sorted((p1,p2)->Integer.valueOf(p1.get("complete").toString()).compareTo(Integer.valueOf(p2.get("complete").toString()))).collect(Collectors.toList());
        list = list.stream().sorted((p1,p2)-> p2.get("create_time").toString().compareTo(p2.get("create_time").toString())).collect(Collectors.toList());

        result.put("items",list);
        return ReturnDto.buildSuccessReturnDto(result);

    }

    private Map<String,Object> getQueryDate(String dateType){
        Map<String,Object> param = new HashMap<>();
        LocalDate now = LocalDate.now();

        switch (dateType){
            case "2":  //明天
                param.put("startTime",now.plusDays(1L));
                param.put("endTime", now.plusDays(1L));

                param.put("week",now.plusDays(1L).getDayOfWeek().getValue());
                param.put("month",now.plusDays(1L).getDayOfMonth());
                break;
            case "3":  //昨天
                param.put("endTime",now.plusDays(-1L));
                param.put("startTime",now.plusDays(-1L));

                param.put("week", now.plusDays(-1L).getDayOfWeek().getValue());
                param.put("month", now.plusDays(-1L).getDayOfMonth());
                break;

            case "4":  //本周
                param.put("startTime", now.with(ChronoField.DAY_OF_WEEK, 1));
                param.put("endTime", now.with(ChronoField.DAY_OF_WEEK, 7));
                break;
            case "5":  //下周
                LocalDate nextWeek = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                param.put("startTime", nextWeek);
                param.put("endTime", nextWeek.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
                break;
            case "6":  //上周
                LocalDate preWeek = now.plusWeeks(-1L);
                param.put("startTime", preWeek.with(ChronoField.DAY_OF_WEEK, 1));
                param.put("endTime", preWeek.with(ChronoField.DAY_OF_WEEK, 7));
                break;

            case "7":  //本月
                param.put("startTime", now.with(TemporalAdjusters.firstDayOfMonth()));
                param.put("endTime", now.with(TemporalAdjusters.lastDayOfMonth()));
                break;
            case "8":  //下月
                LocalDate nextMonthFirstDay = now.with(TemporalAdjusters.firstDayOfNextMonth());
                param.put("startTime", nextMonthFirstDay);
                param.put("endTime", nextMonthFirstDay.plusMonths(1L).plusDays(-1L));
                break;
            case "9":  //上月
                param.put("startTime", now.with((temporal) -> temporal.plus(-1, MONTHS).with(DAY_OF_MONTH, 1)));
                param.put("endTime", now.with((temporal) -> temporal.with(DAY_OF_MONTH, 1).plus(-1, DAYS)));
                break;
            case "1":
                //当天
//                TemporalAdjusters.
                param.put("startTime",now);
                param.put("endTime",now);
                param.put("week", now.getDayOfWeek().getValue());
                param.put("month", now.getDayOfMonth());
                break;
            default:
                param.put("endTime",now);
                param.put("startTime", LocalDate.of(2017, 1, 1));
                break;
        }
        return param;
    }

    /**
     * 任务排行榜
     * @param param
     * <code>
        {
            "joinId":101299,
            "siteId":100190,
            "storeId":1177,
            "executeId": 6804
        }
     * </code>
     * @return
     */
    @RequestMapping("/sortList")
    public ReturnDto sortListed(@RequestBody RewardRankQueryDTO param) throws BusinessLogicException {
        Map<String,Object> result = rewardRankService.sortList(param);

        return ReturnDto.buildSuccessReturnDto(result);
    }

    //查询任务奖励
    @RequestMapping(value = "/queryMyRewards",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> queryMyRewards(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Map<String, Object> map = new HashMap();
        String siteId = String.valueOf(parameterMap.get("siteId"));
        Integer joinId = Integer.valueOf(String.valueOf(parameterMap.get("joinId")));
        if(siteId == null || "".equals(siteId) || "null".equals(siteId)) {
            map.put("status","fail");
            map.put("result","siteId不能为空!");
        } else {
            List<Map<String,Object>> resultList = taskService.queryMayRewards(parameterMap);
            //根据siteId和店员id查询店员头像
            Map<String,Object> head = taskService.queryAdminHead(Integer.valueOf(siteId),joinId);
            Integer money = 0;   //人名币
            Integer performance = 0; //绩效
            Integer beans = 0;
            if(resultList == null || resultList.size() == 0) {
                map.put("status","fail");
                map.put("result","没有对应的任务奖励!");
                map.put("avatar", Optional.ofNullable(head).map(t -> t.get("avatar")).orElse(""));
                map.put("money",money/100);
                map.put("performance",performance);
                map.put("beans",beans);
            }else {
                for(Map<String,Object> task : resultList) {
                    //rewardType    任务奖励 10 人民币 20 绩效
                    String rewardType = String.valueOf(task.get("rewardType"));
                    if("10".equals(rewardType)) {
                        Integer reward = Integer.valueOf(String.valueOf(task.get("reward")));
                        money += reward;
                        task.put("reward",reward/100);
                    }else if ("20".equals(rewardType)){
                        performance += Integer.valueOf(String.valueOf(task.get("reward")));
                    }else{
                        beans += Integer.valueOf(String.valueOf(task.get("reward")));
                    }
                }
                map.put("avatar",head.get("avatar"));
                map.put("money",money/100);
                map.put("performance",performance);
                map.put("beans", beans);
                map.put("status","success");
                map.put("result",resultList);
            }
        }

        return map;
    }

    //查询任务指定商品
    @RequestMapping(value = "/goodsList",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryGoodsList(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
//        Map<String, Object> map = new HashMap();
        ReturnDto response;
        try {
            List<Map<String,Object>> resultList = taskService.getTaskGoods(parameterMap);
            response = ReturnDto.buildSuccessReturnDto(resultList);
        } catch (Exception e) {
            logger.error("获取任务指定商品失败!", e);
            response = ReturnDto.buildFailedReturnDto("获取任务指定商品失败:" + e);
        }

        return response;

    }

}
