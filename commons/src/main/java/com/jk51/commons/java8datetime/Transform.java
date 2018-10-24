package com.jk51.commons.java8datetime;

import java.time.*;
import java.util.Date;

/**
 * 该类用于LocalDateTime，LocalDate, LocalTime, Date之间的相互转换
 * <p>
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
public class Transform {
    public static final ZoneId zone = ZoneId.systemDefault();

    /**
     * java.util.Date 转换成 LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime uDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * java.util.Date 转换成 LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate uDateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalDate();
    }

    /**
     * java.util.Date 转换成 LocalTime
     *
     * @param date
     * @return
     */
    public static LocalTime uDateToLocalTime(Date date) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalTime();
    }

    /**
     * LocalDateTime 转换成 java.util.Date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToUDate(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * LocalDate转换成Date，转换成localDate的 00:00:00 这个时间点
     *
     * @param localDate
     * @return
     */
    public static Date localDateToUDate(LocalDate localDate) {
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * LocalTime转换成Date，转换的是代码运行时间点当天的localTime时间点
     *
     * @param localTime
     * @return
     */
    public static Date localTimeToUDate(LocalTime localTime) {
        LocalDate localDate = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }
}
