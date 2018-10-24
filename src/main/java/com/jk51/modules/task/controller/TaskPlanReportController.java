package com.jk51.modules.task.controller;

import com.jk51.modules.task.service.TaskPlanReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-08-29 14:08
 * 修改记录:
 */
@RequestMapping("/taskPlan")
@Controller
public class TaskPlanReportController {

    private final static Logger LOGGER = LoggerFactory.getLogger(TaskPlanReportController.class);

    @Autowired
    TaskPlanReportService reportService;

    @RequestMapping("/getTaskPlanListReport")
    @ResponseBody
    public Map<String,Object> getTaskPlanListReport(@RequestParam Map<String,Object> params, Integer planId,Integer siteId, HttpServletRequest request){
        try {
            return reportService.getTaskPlanListReportData(planId,siteId);
        } catch (Exception e) {
            LOGGER.error("导出任务计划失败",e);
            return null;
        }
    }


}
