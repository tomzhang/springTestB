package com.jk51.modules.im.event;

import com.jk51.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by admin on 2017/9/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class DelayedMessageProduceTest {

    @Autowired
    private DelayedMessageProduce delayedMessageProduce;

    @Test
    public void delayedMessageProduce() throws Exception {
        Map messageMap = new HashMap();
        messageMap.put("messageType", "task_finishTask");
        messageMap.put("siteId", "100190");
        messageMap.put("storeAdminId", 100882);
        messageMap.put("storeId", 1010);
        messageMap.put("taskName","张三;李四;二麻子111111");
        LocalDateTime localDateTime = LocalDateTime.now().withHour(14);
        localDateTime = localDateTime.plusMinutes(3);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Map messageMap = new HashMap();
                messageMap.put("messageType", "task_finishTask");
                messageMap.put("siteId", "100190");
                messageMap.put("storeAdminId", 100868);
                messageMap.put("storeId", 1010);
                messageMap.put("taskName","张三;李四;二麻子");
                LocalDateTime localDateTime = LocalDateTime.now().withHour(14);
                localDateTime = localDateTime.plusMinutes(3);
                ZoneId zone = ZoneId.systemDefault();//系统默认时区
                Instant instant = localDateTime.atZone(zone).toInstant();
                Date date = Date.from(instant); //开始时间
                try {
                    delayedMessageProduce.delayedMessageProduce(messageMap, "100190", "task_finishTask", null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },30*1000,60*1000);
        while (true);

    }


    @Test
    public void delayMessageTest() throws Exception {
        Map messageMap = new HashMap();
        messageMap.put("messageType", "task_finishTask");
        messageMap.put("siteId", "100190");
        messageMap.put("storeAdminId", 100868);
        messageMap.put("storeId", 1010);
        messageMap.put("taskName","张三;李四;二麻子");
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.withHour(10).plusMinutes(5);
        ZoneId zone = ZoneId.systemDefault();//系统默认时区
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant); //开始时间
        delayedMessageProduce.delayedMessageProduce(messageMap, "100190", "task_finishTask", date, null);
        while (true);
    }
}
