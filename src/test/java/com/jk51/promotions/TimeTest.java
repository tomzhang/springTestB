package com.jk51.promotions;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by Administrator on 2018/5/12.
 */
public class TimeTest {

    @Test
    public void test1(){
        LocalDateTime end = LocalDateTime.of(2018, 5, 24, 14, 59);
        LocalDateTime now = LocalDateTime.now();
        long between = ChronoUnit.HOURS.between(end, now);
        long between1 = ChronoUnit.DAYS.between(end, now);
        long between2 = ChronoUnit.MINUTES.between( now,end);
        System.out.println(between);
        System.out.println(between1);
        System.out.println(between2);

    }

    @Test
    public void test2(){
        LocalDateTime end = LocalDateTime.of(2018, 5, 24, 14, 59);
        LocalDateTime now = LocalDateTime.now();
        String s = resolveTime(end, now);
        System.out.println(s);
    }

    public String resolveTime(LocalDateTime end,LocalDateTime now){
        long hours = ChronoUnit.HOURS.between(now, end);
        if(hours < 24 && hours!=0){
            return hours+"小时后结束";
        }else if(hours>= 24){
            long days = ChronoUnit.DAYS.between(now, end);
            return days+"天后结束";
        }
        if(hours == 0){
            long minutes = ChronoUnit.MINUTES.between(now, end);
            return minutes + "分钟后结束";
        }
        return null;
    }
}
