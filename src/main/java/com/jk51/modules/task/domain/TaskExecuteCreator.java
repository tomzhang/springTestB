package com.jk51.modules.task.domain;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.task.BTask;
import com.jk51.model.task.BTaskExecute;
import com.jk51.model.task.BTaskplan;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static java.time.DayOfWeek.MONDAY;

/**
 * 创建任务执行计划
 */
//@Slf4j
public class TaskExecuteCreator {
    public static final byte DAY = 10;
    public static final byte WEEK = 20;
    public static final byte MONTH = 30;
    private static final Logger log = LoggerFactory.getLogger(TaskExecuteCreator.class);

    private static final ZoneId ZONE_ID = ZoneOffset.systemDefault();

    private BTaskplan bTaskplan;
    private BTask bTask;
    private int[] dayNums = {};

    public TaskExecuteCreator(BTaskplan bTaskplan, BTask bTask) {
        this.bTaskplan = bTaskplan;
        this.bTask = bTask;

        if (StringUtil.isNotEmpty(bTaskplan.getDayNum())) {
            dayNums = Arrays.stream(bTaskplan.getDayNum().split(",")).mapToInt(Integer::parseInt).toArray();
        }
    }

    public List<BTaskExecute> create() {
        List<BTaskExecute> bTaskExecutes = new LinkedList<>();
        CursorDateTime cursorDateTime = new CursorDateTime();
        LocalDate startDay;

        while (cursorDateTime.hasNext()) {
            startDay = cursorDateTime.toLocalDate();

            if (skipDate(cursorDateTime.value)) {
                cursorDateTime.increment();
                continue;
            }

            BTaskExecute bTaskExecute = new BTaskExecute();
            bTaskExecute.setPlanId(bTaskplan.getId());
            bTaskExecute.setTaskId(bTask.getId());
            bTaskExecute.setStartDay(Date.from(startDay.atStartOfDay(ZONE_ID).toInstant()));
            cursorDateTime.increment();
            bTaskExecute.setEndDay(Date.from(cursorDateTime.toLocalDate().atStartOfDay(ZONE_ID).toInstant()));

            log.info("生成执行计划 timeType: {} activeType: {} 计划id: {} 任务id:{} {}",
                bTask.getTimeType(),
                bTaskplan.getActiveType(),
                bTaskplan.getId(),
                bTask.getId(),
                bTaskExecute);
            bTaskExecutes.add(bTaskExecute);
        }

        return bTaskExecutes;
    }

    /**
     * 是否跳过指定日期
     * @param startTime
     * @return
     */
    public boolean skipDate(LocalDateTime startTime) {
        LocalDate startDay = startTime.toLocalDate();
        byte activeType = bTaskplan.getActiveType();
        byte timeType = bTask.getTimeType();

        if (activeType == DAY) {
            return false;
        }

        if (dayNums.length == 0) {
            // 自然周和自然月的任务计划有效天没选
            return true;
        }

        if (timeType == DAY) {
            // 任务是自然日 计划时月/周
            if (!TaskPlanHelper.isActiveDay(activeType, dayNums, startDay.atStartOfDay())) {
                // 当前不是一个有效日期 不创建任务执行计划
                return true;
            }
        } else if (activeType == MONTH && timeType == WEEK) {
            // 计划是月 任务是周 从一周开始到一周结束的时间只有有一天在月选择天数里就创建
            int[] days = betweenDaysOfMonth(startDay, startTime.plusMonths(1L).toLocalDate());
            boolean flag = false;
            for (int day : days) {
                if (ArrayUtils.contains(dayNums, day)) {
                    flag = true;
                    break;
                }
            }

            return !flag;
        }

        return false;
    }

    /**
     * 获取两个时间之间的日期
     * @param startDate
     * @param endDate
     * @return
     */
    public int[] betweenDaysOfMonth(LocalDate startDate, LocalDate endDate) {
        int[] days = new int[(int)(endDate.toEpochDay() - startDate.toEpochDay())];
        int i = 0;
        while (startDate.compareTo(endDate) < 0) {
            days[i++] = startDate.getDayOfMonth();
            startDate = startDate.plusDays(1L);
        }

        return days;
    }

    private class CursorDateTime {
        private LocalDateTime value;
        LocalDateTime endTime = LocalDateTime.ofInstant(bTaskplan.getEndTime().toInstant(), ZONE_ID);

        public CursorDateTime() {
            value = LocalDateTime.ofInstant(bTaskplan.getStartTime().toInstant(), ZONE_ID);

            if (bTask.getTimeType() == WEEK && value.getDayOfWeek() != MONDAY) {
                // 开始日期不是周一 将开始时间调整为下周一
                value = value.with(TemporalAdjusters.next(MONDAY));
            } else if (bTask.getTimeType() == MONTH && value.getDayOfMonth() != 1) {
                // 开始日期不是当月的第一天 将开始时间调整为下个月第一天
                value = value.with(TemporalAdjusters.firstDayOfNextMonth());
            }
        }

        public void increment() {
            // 不需要时分秒
            value = nextDateTime();
        }

        public LocalDateTime nextDateTime() {
            // 不需要时分秒
            if (bTask.getTimeType() == 10) {
                return value.plusDays(1L);
            } else if (bTask.getTimeType() == 20) {
                return value.plusWeeks(1L);
            } else if (bTask.getTimeType() == 30) {
                return value.plusMonths(1L);
            }

            throw new IllegalStateException("无效的时间类型");
        }

        public boolean hasNext() {
            return value.compareTo(endTime) <= 0 && nextDateTime().plusSeconds(-1L).compareTo(endTime) <= 0;
        }

        public LocalDate toLocalDate() {
            return value.toLocalDate();
        }
    }
}
