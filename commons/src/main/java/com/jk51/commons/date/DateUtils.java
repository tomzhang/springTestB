package com.jk51.commons.date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件名:com.jk51.commons.date.DateUtils
 * 描述: JDK1.8 时间使用工具类，不建议使用java.util.Date相关再操作日期
 * 作者: wangzhengfei
 * 创建日期: 2017-01-16
 * 修改记录:
 */
public final class DateUtils {

    /**
     * 日志记录器
     */
    public static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    /**
     * 获取默认格式的当前时间
     * @return
     */
    public static String getCurrentTime(){
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * 获取默认格式的当前日期，格式如:2017-01-16
     * @return 日期字串
     */
    public static String getToday(){
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 解析时间
     * @param source 日期字符串
     * @param format 日期格式
     * @return
     */
    public static Date parseDate(String source,String format)  {
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        Date date = null;
        try {
            date =  sdf.parse(source);
        } catch (ParseException e) {
            logger.error("解析时间异常，source：{}，format：{}，报错信息：{}",source,format,e);
        }

        return date;
    }

    /**
     * 日期格式化
     * @param source 日期字符串
     * @param format 日期格式
     * @return
     * @throws ParseException
     */
    public static Date parse(String source,String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(source);
    }

    /**
     * 日期格式化
     * @param date 日期
     * @param format 格式
     * @return 格式化后的字符串
     */
    public static String toString(Date date,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 获取当前时间SqlDate对象
     * @return
     */
    public static java.sql.Date getNowSqlDate(){
        return new java.sql.Date(Instant.now().toEpochMilli());
    }

    /**
     * 日期转换
     * @param date 日期字串
     * @param format 格式
     * @return
     */
    public static java.sql.Date convert(String date,String format) throws ParseException {
        Date d = parse(date,format);
        return new java.sql.Date(d.getTime());
    }

    /**
     * 日期转换
     * @param date
     * @return
     */
    public static java.sql.Date convert(Date date){
        return new java.sql.Date(date.getTime());
    }

    /**
     * 获得当前时间戳
     * @return
     */
    public static Timestamp getNowTimestamp(){
        return new Timestamp(Instant.now().toEpochMilli());
    }

    public static String formatDate(String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static String formatDate(Date date,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     *获取当前时间加或减指定天数的时间
     * @param num  num为正整数时为加指定天数，为负整数时为减指定天数
     * */
    public static Date getBeforeOrAfterDate(Date now,int num){

        Calendar calendar=Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH,num);
        return calendar.getTime();
    }


    public static String getBeforeOrAfterDateString(String dataStr,int num){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String dateStr = null;
        try {
            Date date = formatter.parse(dataStr);
            Date resultDate = getBeforeOrAfterDate(date,num);
            dateStr = formatter.format(resultDate);
        } catch (ParseException e) {
           logger.error("解析时间报错,dataStr:{},报错信息：{}",dataStr,e);
        }

        return dateStr;

    }

    /**
     * 计算两个日期的时间差
     * @param formatTime1
     * @param formatTime2
     * @return
     */
    public static long getTimeDifference(Timestamp formatTime1, Timestamp formatTime2) {
        SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
        long t1 = 0L;
        long t2 = 0L;
        try {
            t1 = timeformat.parse(getTimeStampNumberFormat(formatTime1)).getTime();
        } catch (ParseException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        try {
            t2 = timeformat.parse(getTimeStampNumberFormat(formatTime2)).getTime();
        } catch (ParseException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }

        return (t1 - t2)/1000;
       /* //因为t1-t2得到的是毫秒级,所以要初3600000得出小时.算天数或秒同理
        int hours=(int) ((t1 - t2)/3600000);
        int minutes=(int) (((t1 - t2)/1000-hours*3600)/60);
        int second=(int) ((t1 - t2)/1000-hours*3600-minutes*60);
        return ""+hours+"小时"+minutes+"分"+second+"秒";*/
    }
    /**
     * 格式化时间
     * Locale是设置语言敏感操作
     * @param formatTime
     * @return
     */
    public static String getTimeStampNumberFormat(Timestamp formatTime) {
        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", new Locale("zh", "cn"));
        return m_format.format(formatTime);
    }
    /*
    获取指定格式的时间参数yyyyMMddHHmmss
     */
    public static  String GetCurrentTimeByType(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(new Date());
    }

    public static  String getCurrentTimeByType(String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 获取默认格式的当前日期，格式如:2017-01
     * @return 日期字串
     */
    public static String getMonth(){
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 获取当前月每天的日期
     * @return
     */
    public static List getDayListOfMonth(int year , int month, int day) {
        List<String> fullDayList = new LinkedList<String>();
        if(day <= 0 ) day = 1;
        Calendar cal = Calendar.getInstance();// 获得当前日期对象
        cal.clear();// 清除信息
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);// 1月从0开始
        cal.set(Calendar.DAY_OF_MONTH, day);// 设置为1号,当前日期既为本月第一天
        int count = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int j = 0; j <= (count-1);) {
            if(sdf.format(cal.getTime()).equals(getLastDay(year, month)))
                break;
            cal.add(Calendar.DAY_OF_MONTH, j == 0 ? +0 : +1);
            j++;
            fullDayList.add(sdf.format(cal.getTime()));
        }
        return fullDayList;
    }

    /**
     * 获取当前月日期最大的一天
     */
    public static String getLastDay(int year,int month){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        return sdf.format(cal.getTime());
    }

    /**
     * 与现在的提起比较大小
     * @param  day  yyyy-MM-dd 00:00:00
     */
    public static int compareNow(String day){

        Date date = null;
        try {
            date =  parse(day+" 00:00:00","yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            logger.error("解析时间报错",e);
        }

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.setTime(date);
        c2.setTime(new Date());

        int result =  c1.compareTo(c2);

        return result;
    }


    /**
     * 判断时间格式
     * 2004-2-30 是无效的
     * 2003-2-29 是无效的
     * @param str
     * @return
     */
    public static boolean isValidDate(String str,String format) {

        DateFormat formatter = new SimpleDateFormat(format);
        try{
            Date date = (Date)formatter.parse(str);
            return str.equals(formatter.format(date));
        }catch(Exception e){
            return false;
        }
    }


    /**
     * 获取指定日期段字符串，日期list
     *
     * @param startStr 日期字符串 格式"yyyy-MM-dd"
     * @parm endStr 日期字符串 格式"yyyy-MM-dd"
     * */
    public static List<String> getContinuousDayStr(String startStr,String endStr){

        List<String> dayStr = new LinkedList<>();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startLocalDate = LocalDate.parse(startStr, df);
        LocalDate endDate = LocalDate.parse(endStr, df);

        do{
            dayStr.add(startLocalDate.format(df));
            startLocalDate = startLocalDate.plusDays(1);
        }while (startLocalDate.compareTo(endDate)<=0);

        return dayStr;

    }
    /**
           * 当前日期加上天数后的日期
           * @param num 为增加的天数
           * @return
           */
    public static String plusDay2(int num, Date d) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, num);// num为增加的天数，可以改变的
        d = ca.getTime();
        String enddate = format.format(d);
        return enddate;
    }
    /**
     * 当前日期加上天数后的日期
     * @param num 为增加的天数
     * @return
     */
    public static Date plusDayDate(int num, Date d) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, num);// num为增加的天数，可以改变的
        d = ca.getTime();
        return d;
    }
}
