package com.jk51.modules.schedule;

import com.alibaba.fastjson.JSONObject;
import com.jk51.model.schedule.ScheduleExecutionLog;
import com.jk51.modules.schedule.service.ScheduleExecutionLogService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-02-21
 * 修改记录:
 */
@RestController
@RequestMapping("/schedule")
public class AgentController {

    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    private ScheduleExecutionLogService logService;


    @RequestMapping(value = "/agent_service",method = RequestMethod.POST)
    public String perform(@RequestParam Integer scheduleId,
                          @RequestParam(required = false) String paramJSON){
        logger.debug("接受到来自任务调度平台的任务调度请求,参数为:{}",paramJSON);
        HashMap<String,Object> invokeParams = null;
        try{
            if(!StringUtils.isEmpty(paramJSON)){
                invokeParams = JSONObject.parseObject(paramJSON,HashMap.class);
            }
        }catch (Exception ex){
            String logMsg = "解析调用参数发生异常："+ ExceptionUtils.getRootCauseMessage(ex);
            logger.error(logMsg,ex);
            writeLog(scheduleId,logMsg);
            return "FAIL";
        }
        agentService.perform(scheduleId,invokeParams);
        logger.debug("任务请求已异步调用，返回调度平台.");
        return "SUCCESS";
    }

    private void writeLog(Integer scheduleId,String logMsg){
        ScheduleExecutionLog log = new ScheduleExecutionLog();
        log.setScheduleId(scheduleId);
        log.setStatus(ExecuteResultStatus.FAIL.getValue());
        log.setLog(logMsg);
        log.setStartedAt(new Timestamp(Instant.now().toEpochMilli()));
        logService.insert(log);
    }
}
