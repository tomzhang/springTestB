package com.jk51.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * 文件名:com.jk51.async.LogAggregateTask
 * 描述: 日志聚合任务，异步方法开发示例，调用与普通方法调用无差别
 * 作者: wangzhengfei
 * 创建日期: 2017-01-16
 * 修改记录:
 */
@Component
public class LogAggregateTask {

    private static final Logger logger = LoggerFactory.getLogger(LogAggregateTask.class);

    @Async
    public void aggregateErrorLog() throws InterruptedException {
        logger.info("这是一个模拟异步任务的请求.");
        Thread.sleep(5000l);
        logger.info("异步任务[错误日志汇总]处理完成.");
    }

    @Async
    public Future<Integer> asyncTaskWithFuture()  throws InterruptedException {
        logger.info("这是一个模拟异步任务的请求.");
        int total = 0;
        for(int i=0;i<1000;i++){
            total++;
            Thread.sleep(5l);
        }
        logger.info("异步任务[错误日志汇总]处理完成.");
        return new AsyncResult<Integer>(total);
    }
}
