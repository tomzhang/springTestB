package com.jk51.modules.task.domain.count;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.task.*;
import com.jk51.modules.goods.library.SpringContextUtil;
import com.jk51.modules.task.domain.TaskPlanHelper;
import com.jk51.modules.task.mapper.BTaskBlobMapper;
import com.jk51.modules.task.mapper.BTaskExecuteMapper;
import com.jk51.modules.task.mapper.BTaskcountMapper;
import com.jk51.modules.task.mapper.TaskPlanCountMapper;
import com.jk51.modules.task.service.TaskPlanService;
import com.jk51.mq.mns.CloudQueueFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import static com.jk51.model.task.BTaskplan.ActiveType.EVERY_DAY;

public class Counts {
    public static final String QUEUE_NAME = "taskPlanQueue-taskplan";

    private static final Logger logger = LoggerFactory.getLogger(Counts.class);

    private CountRangeTime rangeTime;

    protected BTaskplan bTaskplan;

    protected BTask task;

    protected BTaskExecute bTaskExecute;

    @Autowired
    protected RedisTemplate<String, Long> redisTemplate;

    @Autowired
    protected TaskPlanService taskPlanService;

    @Autowired
    protected BTaskcountMapper bTaskcountMapper;

    @Autowired
    BTaskBlobMapper bTaskBlobMapper;

    @Autowired
    TaskPlanCountMapper taskPlanCountMapper;

    @Autowired
    BTaskExecuteMapper bTaskExecuteMapper;

    public Counts(BTaskplan bTaskplan, BTask task, BTaskExecute bTaskExecute) {
        this.bTaskplan = bTaskplan;
        this.task = task;
        this.bTaskExecute = bTaskExecute;
        SpringContextUtil.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);
    }

    public boolean count(AbstractCounter countable, List<TCounttype> tCounttypeList) {
        rangeTime = getRangeTime();
        countable.withBTask(task).withBTaskExecute(bTaskExecute).withBTaskplan(bTaskplan);
        Map<Integer, Long> rewardCountMap = countable.count(tCounttypeList, rangeTime);

        return batchAddCountRecord(rewardCountMap, rangeTime);
    }

    public boolean count(Countable countable) {
        Map<Integer, Long> rewardCountMap = countable.count(task, bTaskplan, bTaskExecute);
        Integer joinId = rewardCountMap.keySet().iterator().next();
        Long countValue = rewardCountMap.get(joinId);

        // 统计值不走b_taskcount获取可以直接走奖励计算
        return addUniqueCountRecord(joinId, task.getId(), countValue.intValue());
    }

    /**
     * 获取统计数据开始时间和结束时间段
     * @return
     */
    public CountRangeTime getRangeTime() {
        int[] dayList = Arrays.stream(bTaskplan.getDayNum().split(","))
            .filter(StringUtil::isNotEmpty)
            .mapToInt(i -> Integer.parseInt(i))
            .toArray();
        LocalDateTime executeEnd = LocalDateTime.ofInstant(bTaskExecute.getEndDay().toInstant(), ZoneOffset.systemDefault());
        LocalDateTime startTime = getStartTime();

        CountRangeTime rangeTime = calcRangeTime(startTime, LocalDateTime.now(), executeEnd, bTaskplan.getActiveType(), dayList);
        logger.info("任务执行计划 {} 统计时间段 {} - {}", bTaskExecute.getId(), rangeTime.getStartTime(), rangeTime.getEndTime());

        return rangeTime;
    }

    public static CountRangeTime calcRangeTime(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime executeEnd, Byte activeType, int[] dayList) {
        CountRangeTime countRangeTime = new CountRangeTime();
        countRangeTime.setStartTime(startTime);

        if (endTime.compareTo(executeEnd) > 0) {
            // 当前时间大于任务执行计划的结束日期 统计到结束时间点
            endTime = executeEnd;
        }

        int diffDays = (int)(endTime.toLocalDate().toEpochDay() - startTime.toLocalDate().toEpochDay());
        if (diffDays > 0 && !Objects.equals(activeType, EVERY_DAY.getValue())) {
            // 统计的开始时间和结束时间不是同一天且计划有效日期类型不是每日
            boolean flag;
            LocalDateTime tempTime = startTime;
            // 从开始时间到执行计划开始计算出连续时间日的最后一天
            do {
                tempTime = tempTime.plusDays(1L);
                if (tempTime.compareTo(executeEnd) >= 0) {
                    flag = false;
                } else {
                    flag = TaskPlanHelper.isActiveDay(activeType, dayList, tempTime);
                }
                if (!flag) {
                    // 不是有效期 设置统计结束时间为该日期的0时0分0秒
                    endTime = tempTime.withHour(0).withMinute(0).withSecond(0);
                }
            } while (flag && tempTime.compareTo(endTime) < 0);
        }
        countRangeTime.setEndTime(endTime);

        return countRangeTime;
    }

    /**
     * 获取时间范围的开始时间
     * @return
     */
    public LocalDateTime getStartTime() {
        LocalDateTime startTime = taskPlanService.getStartTime(bTaskExecute.getId());
        if (Objects.isNull(startTime)) {
            // 返回任务执行计划开始时间
            startTime = LocalDateTime.ofInstant(bTaskExecute.getStartDay().toInstant(), ZoneOffset.systemDefault());

            byte activeType = bTaskplan.getActiveType();
            if (task.getTimeType() != 10 && activeType != 10) {
                // 任务和计划都不是每天
                int[] dayList = {};
                if (StringUtils.isNotEmpty(bTaskplan.getDayNum())) {
                    Arrays.stream(bTaskplan.getDayNum().split(",")).mapToInt(i -> Integer.parseInt(i)).toArray();
                }
                // 防止数据异常导致的死循环
                int i = 0;
                while (!taskPlanService.isActiveDay(activeType, dayList, startTime) && i < 31) {
                    i++;
                    startTime = startTime.plusDays(1L);
                }
            }
        }

        return startTime;
    }

    public BTaskcount createBTaskcount(Integer joinId, Integer taskId, Integer countValue) {
        BTaskcount bTaskcount = new BTaskcount();

        bTaskcount.setJoinType(task.getObject());
        bTaskcount.setJoinId(joinId);
        bTaskcount.setPlanId(bTaskplan.getId());
        bTaskcount.setTaskId(taskId);
        bTaskcount.setExecuteId(bTaskExecute.getId());
        bTaskcount.setCountValue(countValue);
        if (bTaskplan.getSiteId() != 0) {
            bTaskcount.setSiteId(bTaskplan.getSiteId());
        } else {
            int siteId = bTaskcountMapper.selectSiteIdByJoinType(task.getObject(), joinId);
            bTaskcount.setSiteId(siteId);
        }

        return bTaskcount;
    }

    public BTaskcount createBTaskcount(Integer joinId, Integer taskId, Integer countValue, CountRangeTime countRangeTime) {
        ZoneId zoneId = ZoneOffset.systemDefault();
        Date countStart = Date.from(countRangeTime.getStartTime().atZone(zoneId).toInstant());
        Date countEnd = Date.from(countRangeTime.getEndTime().atZone(zoneId).toInstant());

        BTaskcount bTaskcount = createBTaskcount(joinId, taskId, countValue);
        bTaskcount.setCountStart(countStart);
        bTaskcount.setCountEnd(countEnd);

        return bTaskcount;
    }

    /**
     * 检查结束时间
     * 如果结束就将计划改为完成
     * @param countEndTime
     * @param executeEndTime
     */
    public boolean checkTimeEndIfEndOnComplete(LocalDateTime countEndTime, LocalDateTime executeEndTime) {
        if (countEndTime.compareTo(executeEndTime) > -1) {
            return true;
        }

        return false;
    }

    /**
     * 批量增加奖励记录
     * @param rewardCountMap
     * @param countRangeTime
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddCountRecord(final Map<Integer, Long> rewardCountMap, CountRangeTime countRangeTime) {
        Objects.requireNonNull(rewardCountMap, "统计数据不能为空");
        Objects.requireNonNull(countRangeTime, "统计时间不能为空");

        ZoneId zoneId = ZoneOffset.systemDefault();
        long timeMillis = countRangeTime.getEndTime().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
        LocalDateTime executeEndTime = LocalDateTime.ofInstant(bTaskExecute.getEndDay().toInstant(), zoneId);

        // 检查任务执行计划是否统计结束
        boolean taskExecuteIsComplete = checkTimeEndIfEndOnComplete(countRangeTime.getEndTime(), executeEndTime);
        if (taskExecuteIsComplete) {
            logger.info("更新执行计划完成 {} {} {}", bTaskExecute.getId(), countRangeTime, executeEndTime);
            // 统计的结束时间大于或者等级任务执行计划的结束时间 改执行计划已执行完毕
            taskPlanService.updateExecuteComplete(bTaskExecute.getId());
            // 取出所有奖励对象
            List<Integer> rewardIds = taskPlanService.getRewardIds(task.getSiteId(), task.getObject(), bTaskplan.getJoinType(), bTaskplan.getJoinIds());
            rewardIds.forEach(id -> {
                if (!rewardCountMap.containsKey(id)) {
                    rewardCountMap.put(id, 0L);
                }
            });
        }

        List<Message> messages = new ArrayList<>(16);
        CloudQueue queue = CloudQueueFactory.create(QUEUE_NAME);
        for (Map.Entry<Integer, Long> item : rewardCountMap.entrySet()) {
            if (item.getValue() == 0 && taskExecuteIsComplete == false) {
                // 任务未完成不记录0值
                continue;
            }
            BTaskcount bTaskcount = createBTaskcount(item.getKey(), task.getId(), item.getValue().intValue(), countRangeTime);
            // 批量写入可能存在消息发送成功然后被消费的时候数据还没写入数据库 可以考虑延时消息 这里不增加复杂性了
            bTaskcountMapper.insertSelective(bTaskcount);
            String messageBody = JSONObject.toJSONString(bTaskcount);
            messages.add(new Message(messageBody));
            if (messages.size() % 16 == 0) {
                // 批量发送消息 批量消息一次最多16条 https://help.aliyun.com/document_detail/35135.html?spm=5176.doc27437.6.710.rNtaog
                queue.batchPutMessage(messages);
                messages.clear();
            }
        }

        // 发送剩余的
        if (messages.size() > 0) {
            queue.batchPutMessage(messages);
            messages.clear();
        }

        return taskExecuteIsComplete || updateStartTime(bTaskExecute.getId(), timeMillis);
    }

    public boolean addCountRecord(Integer joinId, Integer taskId, Integer countValue, CountRangeTime countRangeTime) {
        ZoneId zoneId = ZoneOffset.systemDefault();
        long timeMillis = countRangeTime.getEndTime().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
        boolean result;
        LocalDateTime executeEndTime = LocalDateTime.ofInstant(bTaskExecute.getEndDay().toInstant(), zoneId);
        if (checkTimeEndIfEndOnComplete(countRangeTime.getEndTime(), executeEndTime)) {
            // 统计的结束时间大于或者等级任务执行计划的结束时间 改执行计划已执行完毕
            taskPlanService.updateExecuteComplete(bTaskExecute.getId());
        }
        if (countValue == 0) {
            // 0值直接pass不记录
            result = true;
        } else {
            BTaskcount bTaskcount = createBTaskcount(joinId, taskId, countValue, countRangeTime);
            result = bTaskcountMapper.insertSelective(bTaskcount) != 0;
            // 发送消息进行奖励计算和任务通知
            CloudQueue queue = CloudQueueFactory.create(QUEUE_NAME);
            queue.putMessage(new Message(JSONObject.toJSONString(bTaskcount)));
        }

        if (result) {
            return updateStartTime(bTaskExecute.getId(), timeMillis);
        }

        return false;
    }

    /**
     * 任务可以多次答题 只算最后一次
     * @param joinId
     * @param taskId
     * @param countValue
     * @return
     */
    public boolean addUniqueCountRecord(Integer joinId, Integer taskId, Integer countValue) {
        BTaskcountExample example = new BTaskcountExample();
        example.createCriteria()
            .andExecuteIdEqualTo(bTaskExecute.getId())
            .andJoinTypeEqualTo(task.getObject())
            .andJoinIdEqualTo(joinId);
        List<BTaskcount> bTaskcounts = bTaskcountMapper.selectByExample(example);

        BTaskcount bTaskcount;
        if (CollectionUtils.isEmpty(bTaskcounts)) {
            bTaskcount = createBTaskcount(joinId, taskId, countValue);
            bTaskcount.setCountStart(new Date());
            bTaskcount.setCountEnd(new Date());

            bTaskcountMapper.insertSelective(bTaskcount);
        } else {
            bTaskcount = bTaskcounts.get(0);
            bTaskcount.setCountValue(countValue);

            bTaskcountMapper.updateByPrimaryKeySelective(bTaskcount);
        }

        // 发送消息进行奖励计算和任务通知
        CloudQueue queue = CloudQueueFactory.create(QUEUE_NAME);
        queue.putMessage(new Message(JSONObject.toJSONString(bTaskcount)));

        return true;
    }

    /**
     * 更新开始时间
     * @param executeId
     * @param timeMillis
     * @return
     */
    public boolean updateStartTime(int executeId, long timeMillis) {
        return taskPlanService.updateStartTime(executeId, timeMillis);
    }

}
