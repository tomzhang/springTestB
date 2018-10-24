package com.jk51.commons.date;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Javen on 2017/6/22.
 */
public class StatisticsDate {
    public static Logger logger4j = Logger.getLogger(StatisticsDate.class);
    public static final String YESTERDAYPROPORTION = "yesterdayProportion";
    public static final String TODAYVALUE = "todayValue";
    public static final String PROPORTION = "proportion";


    public static List<Map<String, Date>> processDate(Date date, int days) {
        List<Map<String, Date>> result = new ArrayList<Map<String, Date>>();
        for (int i = 0; i < days; i++) {
            Map<String, Date> map = new HashMap<String, Date>();
            Calendar start = Calendar.getInstance();
            start.setTime(date);
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.DATE, start.get(Calendar.DATE) - i);
            map.put("start", start.getTime());
            Calendar end = Calendar.getInstance();
            end.setTime(date);
            end.set(Calendar.HOUR_OF_DAY, 23);
            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 59);
            end.set(Calendar.DATE, end.get(Calendar.DATE) - i);
            map.put("end", end.getTime());
            result.add(map);
        }
        return result;
    }


    public static Map<String, Date> processOneDateByDay(Date date, int day) {
        Map<String, Date> map = new HashMap<String, Date>();
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.DATE, start.get(Calendar.DATE) - day);
        map.put("start", start.getTime());
        Calendar end = Calendar.getInstance();
        end.setTime(date);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.DATE, end.get(Calendar.DATE) - day);
        map.put("end", end.getTime());
        return map;
    }

    public static Map<String, Date> processOneDate(Date date) {
        Map<String, Date> map = new HashMap<String, Date>();
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        map.put("start", start.getTime());
        Calendar end = Calendar.getInstance();
        end.setTime(date);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        map.put("end", end.getTime());
        return map;
    }


    public static Date processString2Date(String strdate) {
        Date date = new Date();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            date = dateFormat.parse(strdate);
        } catch (Exception e) {
            //时间转换异常
            date = new Date();
            logger4j.error("StatisticsDate UtilsClass->DateCaseException ");
        }
        return date;
    }

    public static String processDate2String(Date date) {
        String sdate = "";
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            sdate = dateFormat.format(date);
        } catch (Exception e) {
            //时间转换异常
            sdate = "";
            logger4j.error("StatisticsDate UtilsClass->DateCaseException ");
        }
        return sdate;
    }

    public static void processYesterdayAndLastWeek(Date date, List<Map<String, Object>> rs, String count) {
        List<Map<String, Date>> dates_temp = StatisticsDate.processDate(date, 7);
        //取今天
        String today = StatisticsDate.processDate2String(dates_temp.get(0).get("start"));
        Long todayNum = 0L;
        //取昨天
        String yesterday = StatisticsDate.processDate2String(dates_temp.get(1).get("start"));
        Long yesterdayNum = 0L;
        //取上周同期
        String lastWeek = StatisticsDate.processDate2String(dates_temp.get(6).get("start"));
        Long lastNum = 0L;
        for (Map<String, Object> item_temp : rs) {
            if (item_temp.get("create_time").equals(lastWeek)) {
                if (item_temp.get(count) instanceof BigDecimal) {
                    lastNum = ((BigDecimal) item_temp.get(count)).longValue();
                } else {
                    lastNum = (Long) item_temp.get(count);
                }

                continue;
            }
            if (item_temp.get("create_time").equals(yesterday)) {
                if (item_temp.get(count) instanceof BigDecimal) {
                    yesterdayNum = ((BigDecimal) item_temp.get(count)).longValue();
                } else {
                    yesterdayNum = (Long) item_temp.get(count);
                }

                continue;
            }
            if (item_temp.get("create_time").equals(today)) {
                if (item_temp.get(count) instanceof BigDecimal) {
                    todayNum = ((BigDecimal) item_temp.get(count)).longValue();
                } else {
                    todayNum = (Long) item_temp.get(count);
                }

                continue;
            }
        }
        for (Map<String, Object> item_temp : rs) {
            if (item_temp.get("create_time").equals(today)) {
                if (todayNum != null && yesterdayNum != null) {
                    if (todayNum == 0 && yesterdayNum != 0) {
                        item_temp.put("yesterday", -100.00);
                        continue;
                    }
                    if (todayNum != 0 && yesterdayNum == 0) {
                        item_temp.put("yesterday", 100.00);
                        continue;
                    }
                    if (todayNum == 0 && yesterdayNum == 0) {
                        item_temp.put("yesterday", "-");
                        continue;
                    }
                    item_temp.put("yesterday", (((todayNum.doubleValue() / yesterdayNum.doubleValue()) - 1.00) * 100.00));
                }
                if (todayNum != null && lastNum != null) {
                    if (todayNum == 0 && lastNum != 0) {
                        item_temp.put("lastWeek", -100.00);
                        continue;
                    }
                    if (todayNum != 0 && lastNum == 0) {
                        item_temp.put("lastWeek", 100.00);
                        continue;
                    }
                    if (todayNum == 0 && lastNum == 0) {
                        item_temp.put("lastWeek", "-");
                        continue;
                    }
                    item_temp.put("lastWeek", ((todayNum.doubleValue() / lastNum.doubleValue() - 1) * 100.00));
                }
            }
        }
    }

    /**
     * 获取当天数据与前一天数据的比例，保留2位小数
     **/
    public static Float getProportion(Float tadayValue, Float beforeDayValue) {

        float proportion = 0f;
        if (tadayValue == 0 && beforeDayValue == 0) {
            proportion = 0f;
        } else if (tadayValue == 0) {
            proportion = -1f;
        } else if (beforeDayValue == 0) {
            proportion = 1f;
        } else {
            proportion = tadayValue / beforeDayValue - 1;
        }

        BigDecimal bigDecimal = new BigDecimal((double) proportion);
        bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);

        return bigDecimal.floatValue();
    }


    public static Map<String, Date> processYesterdayAndLastWeek2(Date date, int days) {
        Map<String, Date> result = new HashMap<String, Date>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - days);
        result.put("start", calendar.getTime());

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        result.put("end", calendar.getTime());
        return result;
    }

    /**
     * 获取first数据与second数据的比例，保留2位小数
     **/
    public static Float getProp(Float firstValue, Float secondValue) {

        float proportion = 0f;
        if (firstValue == 0 && secondValue == 0) {
            proportion = 0f;
        } else if (firstValue == 0) {
            proportion = 0f;
        } else if (secondValue == 0) {
            proportion = 1f;
        } else {
            proportion = firstValue / secondValue;
        }
        BigDecimal bigDecimal = new BigDecimal((double) proportion);
        bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.floatValue();
    }
}
