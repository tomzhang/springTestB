package com.jk51.modules.appInterface.util;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Javen on 2017/6/22.
 */
public class StatisticsDate {
    public static Logger logger4j = org.apache.log4j.Logger.getLogger(StatisticsDate.class);

    public static List<Map<String,Date>> processDate(Date date,int days){
         List<Map<String,Date>> result=new ArrayList<Map<String, Date>>() ;
       for (int i=0;i<days;i++) {
           Map<String, Date> map = new HashMap<String, Date>();
           Calendar start = java.util.Calendar.getInstance();
           start.setTime(date);
           start.set(Calendar.HOUR_OF_DAY, 0);
           start.set(Calendar.MINUTE, 0);
           start.set(Calendar.SECOND, 0);
           start.set(Calendar.DATE, start.get(Calendar.DATE) - i);
           map.put("start", start.getTime());
           Calendar end = java.util.Calendar.getInstance();
           end.setTime(date);
           end.set(Calendar.HOUR_OF_DAY, 23);
           end.set(Calendar.MINUTE, 59);
           end.set(Calendar.SECOND, 59);
           end.set(Calendar.DATE, end.get(Calendar.DATE) - i);
           map.put("end", end.getTime());
           System.out.println("start = [" + start.getTime().toLocaleString() + "], end = [" + end.getTime().toLocaleString() + "]");
           result.add(map);
       }
       return result;
    }

    public static Date processString2Date(String strdate){
        Date date=new Date();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            date=dateFormat.parse(strdate);
        }catch (Exception e){
            //时间转换异常
            date=new Date();
            logger4j.error("StatisticsDate UtilsClass->DateCaseException ");
        }
        return date;
    }
    public static String processDate2String(Date date){
        String sdate="";
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            sdate=dateFormat.format(date);
        }catch (Exception e){
            //时间转换异常
            sdate="";
            logger4j.error("StatisticsDate UtilsClass->DateCaseException ");
        }
        return sdate;
    }


}
