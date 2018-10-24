package com.jk51.interceptor;

import com.jk51.modules.offline.mapper.BMethodTimeMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import java.util.Arrays;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-09-07
 * 修改记录:
 */
@Aspect
@Configuration
public class TimeInterceptor {

    private static Logger logger = LoggerFactory.getLogger(TimeInterceptor.class);
    @Autowired
    private BMethodTimeMapper methodTimeMapper;

    /**
     * 定义拦截规则：拦截offline.service包下面的所有类中，有@TimeRequired注解的方法。
     */
    @Pointcut("execution (* com.jk51.modules.offline.service.*.*(..))&& @annotation(com.jk51.annotation.TimeRequired)")
    public void TimeInterceptor() {
        logger.info("加载erp请求控制拦截器[{}]", this.getClass().getName());
    }

    /**
     * 统计方法执行耗时Around环绕通知
     *
     * @param joinPoint
     * @return
     */
    @Around("TimeInterceptor()")
    public Object timeAround(ProceedingJoinPoint joinPoint) {
        Object obj = null;
        Object[] args = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();
        try {
            obj = joinPoint.proceed(args);
        } catch (Throwable e) {
            logger.error("统计某方法执行耗时环绕通知出错", e);
        }
        long endTime = System.currentTimeMillis();
        this.printExecTime(startTime, endTime, joinPoint, obj);
        return obj;
    }

    @Async
    private void printExecTime(long startTime, long endTime, ProceedingJoinPoint joinPoint, Object object) {
        // 获取执行的方法名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        List<String> argsParams = Arrays.asList(signature.getParameterNames());
        List<Object> argsValues = Arrays.asList(joinPoint.getArgs());
        long diffTime = endTime - startTime;
        logger.warn("-----" + methodName + " 方法,请求参数名称:{" + argsParams.toString() + "},请求参数值:{" + argsValues.toString() + "}.执行耗时：" + diffTime + " ms");
        try {
            methodTimeMapper.insertMethodStaticsTime(Integer.parseInt(argsValues.get(0).toString()), methodName, argsParams.toString(), argsValues.toString(), object.toString(), diffTime);
        } catch (Exception e) {
            logger.info("统计信息插入失败");
        }
    }

}
