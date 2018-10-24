package com.jk51.modules.integral.timingtask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;

import com.jk51.modules.goods.mapper.BIntegralGoodsMapper;
import com.jk51.modules.integral.model.IntegralGoodsTask;
import com.jk51.modules.integral.timingtask.thread.IntegralGoodsTimingOpenThread;
import com.jk51.modules.integral.timingtask.util.ThreadPools;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.partitioningBy;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-05-27 17:33
 * 修改记录:
 */
//@Component
//@Configurable
//@EnableScheduling
@Service
public class IntegralGoodsTimingService {

    private final static Logger LOGGER = LoggerFactory.getLogger(IntegralGoodsTimingService.class);

    //开启任务类型
    private final static Integer OPEN_TYPE = 0;

    //关闭任务类型
    private final static Integer CLOSE_TYPE = 1;

    @Autowired
    BIntegralGoodsMapper integralGoodsMapper;

//    //每1分钟执行一次
//    @Scheduled(cron = "${quartz.integralGoodsTaskTime}")
//    public void reportCurrentByCron(){
//
//        DateFormat dateFormat = dateFormat();
//
//        try {
//
//            LOGGER.info("定时任务开始开启>>>>>>>>>>" + dateFormat.format(new Date()));
//
//            this.process();
//
//            LOGGER.info("定时任务线程成功开启完毕>>>>>>>>>>" + dateFormat.format(new Date()));
//
//        } catch (Exception e) {
//            LOGGER.error("定时任务异常了>>>>>>>>>>" + dateFormat.format(new Date()),e);
//        }
//
//    }
//
//    private SimpleDateFormat dateFormat(){
//        return new SimpleDateFormat ("yyyy-MM-dd hh:mm:dd");
//    }

    //入口方法
    public void process(){
        createTaskThreadPool(this.getOpenTaskList(),OPEN_TYPE);
        createTaskThreadPool(this.getCloseTaskList(),CLOSE_TYPE);
    }

    //创建任务线程，加入线程池
    public void createTaskThreadPool(List<IntegralGoodsTask> taskList,int type){

        if(taskList != null && taskList.size() > 0){

            ExecutorService openPool =  ThreadPools.getThreadPool(this, taskList.size());

            taskList.forEach(task -> {
                try {
                    openPool.execute(this.getThread(task,type));
                } catch (Exception e) {
                    LOGGER.error("定时开启任务线程池异常>>>>>>>>>>type:" + type,e);
                }
            });

        }

    }

    // 获取一个线程
    public Thread getThread(IntegralGoodsTask task,int type){
        return new IntegralGoodsTimingOpenThread(task,type,integralGoodsMapper);
    }

    //获取下一分钟前需要开启的任务
    public List<IntegralGoodsTask> getOpenTaskList() {

        List<IntegralGoodsTask> integralGoodsTaskListOpen = integralGoodsMapper.listIntegralGoodsTimingOpenTask();

        integralGoodsTaskListOpen = getTaskList(integralGoodsTaskListOpen);

        return integralGoodsTaskListOpen;
    }

    //获取下一分钟前需要关闭的任务
    public List<IntegralGoodsTask> getCloseTaskList() {

        List<IntegralGoodsTask> integralGoodsTaskListClose = integralGoodsMapper.listIntegralGoodsTimingCloseTask();

        integralGoodsTaskListClose = getTaskList(integralGoodsTaskListClose);

        return integralGoodsTaskListClose;
    }

    //重组任务，将待执行的和立即执行的任务分开，并将立即执行的任务合并
    public List<IntegralGoodsTask> getTaskList( List<IntegralGoodsTask> integralGoodsTaskList){

        //重组任务
        Map<Boolean,List<IntegralGoodsTask>> integralGoodsTaskMap = integralGoodsTaskList.stream().collect(partitioningBy(integralGoodsTask ->  integralGoodsTask.getTimeInterval() > 0));

        List<IntegralGoodsTask> taskIntervalGreaterThanZeroList = integralGoodsTaskMap.get(true);

        List<IntegralGoodsTask> taskIntervalLessThanZeroList = integralGoodsTaskMap.get(false);

        //将立即执行的任务合并
        StringBuffer goodsIntervalLessThanZeroIds  = new StringBuffer();

        if(taskIntervalLessThanZeroList != null && taskIntervalLessThanZeroList.size() > 0){

            taskIntervalLessThanZeroList.forEach(integralGoodsTask -> {

                if(StringUtils.isNotBlank(integralGoodsTask.getGoodsIds())){

                    goodsIntervalLessThanZeroIds.append("," + integralGoodsTask.getGoodsIds());

                }

            });

            //重组任务List
            if(goodsIntervalLessThanZeroIds.length()> 0){

                IntegralGoodsTask integralGoodsTask = new IntegralGoodsTask();

                integralGoodsTask.setGoodsIds(goodsIntervalLessThanZeroIds.toString());

                integralGoodsTask.setTimeInterval(0);

                //开启的积分任务List不能为空
                if(taskIntervalGreaterThanZeroList == null){

                    taskIntervalGreaterThanZeroList = new LinkedList<IntegralGoodsTask>();

                }

                taskIntervalGreaterThanZeroList.add(integralGoodsTask);

            }
        }

        return taskIntervalGreaterThanZeroList;

    }

}
