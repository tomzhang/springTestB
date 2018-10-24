package com.jk51.modules.schedule;

import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.schedule.ScheduleExecutionLog;
import com.jk51.model.schedule.ScheduleMeta;
import com.jk51.modules.schedule.service.ScheduleExecutionLogService;
import com.jk51.modules.schedule.service.ScheduleMetaService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 定时任务调用代理类，目前没有考虑缓存，如果对性能有要求可以将Method缓存起来.
 *      <p>目前没有考虑监控，可以通过status结构做起来</p>
 * 作者: wangzhengfei
 * 创建日期: 2017-02-21
 * 修改记录:
 */
@Service
public class AgentService {

    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);

    //private static final Map<Long,ScheduleMeta> status = new ConcurrentHashMap<>();

    @Autowired
    private ScheduleExecutionLogService logService;

    @Autowired
    private ScheduleMetaService metaService;

    @Autowired
    ApplicationContext context;

    /**
     * 执行任务目标类调用
     * @param scheduleId 任务ID
     * @param params 调用参数
     */
    @Async
    @Transactional(propagation = Propagation.NEVER)
    public void perform(Integer scheduleId,Map<String,Object> params){
        ScheduleMeta meta = metaService.queryOne(scheduleId);
        long startAt = Instant.now().toEpochMilli();
        String logMsg = null;
        ExecuteResultStatus status = ExecuteResultStatus.UNKNOWN;
        try{
            //先将任务的执行状态改为运行中
            metaService.updateStatus(scheduleId,ExecuteStatus.RUNNING.getValue());
            invoke(meta,params);//执行调用
            //没有异常抛出可认为执行成功
            status = ExecuteResultStatus.SUCCESS;
            logMsg = "Success";
            logger.info("任务[{},{}]调度成功.",meta.getId(),meta.getName());
        }catch (Exception ex){
            logMsg = ExceptionUtils.getRootCauseMessage(ex);
            logger.info("任务[{},{}]调度失败:{}",meta.getId(),meta.getName(),logMsg,ex);
            status = ExecuteResultStatus.FAIL;
        }finally {
            metaService.updateStatus(scheduleId,ExecuteStatus.FINISHED.getValue());
            writeLog(scheduleId,status.getValue(),startAt,logMsg);
        }
    }

    /**
     * 调用目标类方法
     * @param meta 任务编排元数据
     * @param params 调用参数
     * @throws BusinessLogicException 调用时可能引发的异常
     */
    private void invoke(ScheduleMeta meta,Map<String,Object> params) throws BusinessLogicException {
        try{
            Object target = context.getBean(meta.getBeanName());
            if(target == null){
                throw new BusinessLogicException("根据Bean名称"+meta.getBeanName()+"未找到相关的Bean实例，任务调度失败");
            }
            boolean hasParam = false;
            if(!StringUtil.isEmpty(meta.getParamJSON())){
                hasParam = true;
            }
            Method method = getTargetMethod(target,meta.getMethod(),hasParam);
            if(method == null){
                throw new BusinessLogicException("未找到匹配的方法,class["+target.getClass().getName()
                        +"],method[{"+meta.getMethod()+"}],是否含有参数["+hasParam+"]，请检查配置.");
            }
            if(hasParam){
                ReflectionUtils.invokeMethod(method,target,params);
            }else{
                ReflectionUtils.invokeMethod(method,target);
            }
            logger.debug("方法调用完成,类[{}],方法[{}].",target.getClass().getName(),method.getName());
        }catch (Exception ex){
            throw new BusinessLogicException(ex);
        }
    }


    /**
     * 查找目标方法
     * @param target 目标Bean
     * @param methodName 方法名称
     * @param hasParam 是否含有参数
     * @return 目标方法对象
     * @throws BusinessLogicException 解析时可能引发的异常
     */
    private Method getTargetMethod(Object target,String methodName,boolean hasParam)
            throws BusinessLogicException{
        try{
            if(hasParam){
                Method[] methods = target.getClass().getDeclaredMethods();
                for(Method method : methods){
                    //如果参数个数为1并且是Map类型的参数，就认为是目标方法
                    if(method.getParameterCount() == 1 &&
                            method.getParameterTypes()[0].getName().equals(Map.class.getName())){
                        return method;
                    }
                }
                return null;
            }else{
                //查找无参的目标方法
                return target.getClass().getMethod(methodName);
            }
        }catch (Exception ex){
            throw new BusinessLogicException("查找目标方法异常,class["+target.getClass().getName()
                    +"],method[{"+methodName+"}],是否含有参数["+hasParam+"]，请检查配置.",ex);
        }
    }

    /**
     * 写入数据库日志
     * @param scheduleId
     * @param status
     * @param startedAt
     * @param logMsg
     */
    private void writeLog(Integer scheduleId,Integer status,Long startedAt,String logMsg){
        ScheduleExecutionLog log = new ScheduleExecutionLog();
        log.setScheduleId(scheduleId);
        log.setStatus(status);
        log.setLog(logMsg);
        log.setStartedAt(new Timestamp(startedAt));
        logService.insert(log);
    }

}
