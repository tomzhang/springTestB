package com.jk51.modules.task.job;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.task.*;
import com.jk51.modules.appInterface.service.AppMemberService;
import com.jk51.modules.task.domain.BTaskPlanStatus;
import com.jk51.modules.task.domain.TaskPlanChangeStatus;
import com.jk51.modules.task.domain.count.AbstractCounter;
import com.jk51.modules.task.domain.count.CounterFactory;
import com.jk51.modules.task.domain.count.Counts;
import com.jk51.modules.task.mapper.BTaskplanMapper;
import com.jk51.modules.task.mapper.TQuotaMapper;
import com.jk51.modules.task.service.TaskPlanService;
import com.jk51.modules.task.service.TaskService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.time.*;
import java.util.*;

@Component
public class TaskSchedule{


    public static final Logger logger = LoggerFactory.getLogger(TaskSchedule.class);

    private static String REDIS_LOCK_KEY_PREFIX = "shop:task:lock:";

    @Autowired
    BTaskplanMapper bTaskplanMapper;

    @Autowired
    TaskService taskService;

    @Autowired
    TQuotaMapper tQuotaMapper;

    @Autowired
    TaskPlanService taskPlanService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    AppMemberService appMemberService;

    /**
     * 任务数据统计
     */
    @Async
    public void schedule() {
        Date now = Date.from(LocalDate.now().atStartOfDay(ZoneOffset.systemDefault()).toInstant());
        List<BTaskExecute> bTaskExecutes = taskPlanService.selectUncompleted(now);
        if (CollectionUtils.isNotEmpty(bTaskExecutes)) {
            logger.info("开始处理任务执行计划 总共{}个", bTaskExecutes.size());
            long t1 = System.nanoTime();
            bTaskExecutes.parallelStream().unordered().forEach(this::handleTaskExecute);
//            ExecutorService executor = Executors.newCachedThreadPool();
//            for (BTaskExecute bTaskExecute : bTaskExecutes) {
//                executor.execute(() -> this.handleTaskExecute(bTaskExecute));
//            }
//            executor.shutdown();
//            try {
//                while (!executor.awaitTermination(1, TimeUnit.SECONDS));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            long t2 = System.nanoTime();
            logger.info("执行任务执行计划总共耗时 {}纳秒, 平均耗时 {}纳秒", t2 - t1, (t2 - t1) / bTaskExecutes.size());
        }
    }

    /**
     * 自动更改任务计划状态计划任务 1分钟1次
     */
    public void autoChangeStatus() {
        List<TaskPlanChangeStatus> willChangeStatusList = bTaskplanMapper.selectWillChangeStatusList();
        logger.info("需要修改的计划数据 {}", willChangeStatusList);

        if (CollectionUtils.isEmpty(willChangeStatusList)) {
            return;
        }

        Timer timer = new Timer();

        willChangeStatusList.forEach(changeStatus -> {
            int delay = changeStatus.getSecond() * 1000;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (StringUtils.isEmpty(changeStatus.getIds())) {
                        return;
                    }
                    List<Integer> ids = StringUtil.split(changeStatus.getIds(), ",", Integer::valueOf);
                    byte status = changeStatus.getStatus();
                    if (status == BTaskPlanStatus.NONSTART.getValue()) {
                        taskPlanService.changeStatus(ids, BTaskPlanStatus.STARTING.getValue());
                        // 修改为开始
                    } else if (status == BTaskPlanStatus.STARTING.getValue()) {
                        // 修改为结束
                        taskPlanService.changeStatus(ids, BTaskPlanStatus.STOP.getValue());
                    }
                }
            }, delay);
        });
    }




    /**
     * 定时发布任务消息
     * 每天0时
     */
    public void autoNotifyMes() {
        taskPlanService.autoNotifyMess(null);
    }


    /**
     * 处理任务执行计划
     * @param bTaskExecute
     */
    @Async
    public void handleTaskExecute(BTaskExecute bTaskExecute) {
        BTaskplan bTaskplan = bTaskplanMapper.selectByPrimaryKey(bTaskExecute.getPlanId());
        if (Objects.isNull(bTaskplan)) {
            logger.info("无效的任务执行计划 根据计划id{}查找不到记录", bTaskExecute.getPlanId());
            return;
        }

        BTask bTask = taskService.selectByPrimaryKey(bTaskExecute.getTaskId());
        if (Objects.isNull(bTask)) {
            logger.info("无效的任务执行计划 根据任务id{}查找不到记录", bTaskExecute.getTaskId());
            return;
        }

        countTaskExecute(bTaskExecute, bTaskplan, bTask);
    }
    @Async
    public void countTaskExecute(BTaskExecute bTaskExecute, BTaskplan bTaskplan, BTask bTask) {
        logger.info("处理任务执行计划 {}", bTaskExecute.getId());
        String lockKey = REDIS_LOCK_KEY_PREFIX + bTaskExecute.getId();
        RedisConnection connection = null;
        final Charset charsetName = Charset.forName("utf-8");
        final byte[] lockKeyBytes = lockKey.getBytes(charsetName);

        try {
            // 设置最长运行时间为1小时
            long seconds = 60 * 60;
            connection = redisTemplate.getConnectionFactory().getConnection();
            boolean isGetDistributedLock = (boolean)redisTemplate.execute((RedisCallback<Boolean>) client -> {
                byte[] timestamp = String.valueOf(Instant.now().toEpochMilli()).getBytes(charsetName);
                boolean isLock = client.setNX(lockKeyBytes, timestamp);
                if (!isLock) {
                    // key已经存在 查看key有没有设置过期时间
                    long ttl = client.ttl(lockKeyBytes);
                    if (ttl == -1L) {
                        // 设置过期时间
                        client.expire(lockKeyBytes, seconds);
                    }
                } else {
                    client.expire(lockKeyBytes, seconds);
                }

                return isLock;
            });

            if (!isGetDistributedLock) {
                logger.info("任务执行{} 当前还有任务在进行 {}", bTaskExecute.getId(), bTaskplan);
                return;
            }

            int[] dayList = {};
            if (StringUtils.isNotEmpty(bTaskplan.getDayNum())) {
                Arrays.stream(bTaskplan.getDayNum().split(",")).mapToInt(i -> Integer.parseInt(i)).toArray();
            }
            LocalDateTime now = LocalDateTime.now();
            if (!taskPlanService.isActiveDay(bTaskplan.getActiveType(), dayList, now)) {
                // 今天不是一个有效的统计日期
                LocalDateTime startTime = taskPlanService.getStartTime(bTaskExecute.getId());
                if (Objects.nonNull(startTime) && Period.between(startTime.toLocalDate(), now.toLocalDate()).isZero()) {
                    // 上次统计到了今天
                    logger.info("当前不是有效日期 {} {}", bTask, bTaskplan);
                    return;
                }
            }
            // 统计类型
            List<Integer> typeIdList = StringUtil.split(bTask.getTypeIds(), ",", Integer::valueOf);
            List<TCounttype> tCounttypes = taskService.findCountTypeList(typeIdList);
            // 任务指标
            Integer targetId = bTask.getTargetId();
            TQuota tQuota = tQuotaMapper.selectByPrimaryKey(targetId);

            if (Objects.isNull(tQuota) || StringUtils.isEmpty(bTaskplan.getJoinIds())) {
                logger.info("任务指标为空或者计划对象为空 {} {}", tQuota, bTaskplan.getJoinIds());
                return;
            }

            Byte type = tQuota.getType();
            AbstractCounter counter = CounterFactory.create(type);
            Counts counts = new Counts(bTaskplan, bTask, bTaskExecute);
            counts.count(counter, tCounttypes);
        } catch (Exception e) {
            logger.info("{} 统计出错{} {}", bTaskplan.getId(), e.getMessage(), e);
        } finally {
            if (connection != null) {
                // 将锁删除
                if (connection.del(lockKeyBytes) == 1) {
                    logger.info("任务执行{} 分布式锁释放成功", bTaskplan.getId());
                } else {
                    logger.error("任务执行{} 分布式锁释放失败 {} 等待自动删除", bTaskplan.getId(), lockKey);
                }
                // 释放链接
                RedisConnectionUtils.releaseConnection(connection, redisTemplate.getConnectionFactory());
            }
        }

    }
}
