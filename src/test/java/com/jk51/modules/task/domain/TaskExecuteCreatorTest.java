package com.jk51.modules.task.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.jk51.configuration.DatabaseConfiguration;
import com.jk51.model.task.BTask;
import com.jk51.model.task.BTaskplan;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DatabaseConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class TaskExecuteCreatorTest {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void create() throws Exception {
        Map map = jdbcTemplate.queryForMap("SELECT * FROM b_taskplan WHERE id = ?", 123);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        BTaskplan bTaskplan = objectMapper.convertValue(map, BTaskplan.class);
        map = jdbcTemplate.queryForMap("SELECT * FROM b_task WHERE id = ?", bTaskplan.getTaskIds().split(",")[0]);
        BTask bTask = objectMapper.convertValue(map, BTask.class);

        // 日日
        byte timeType = 10;
        byte activeType = 10;
        bTaskplan.setActiveType(activeType);
        bTaskplan.setStartTime(new Date("2017/10/01"));
        bTaskplan.setEndTime(new Date("2017/10/10 23:59:59"));
        bTask.setTimeType(timeType);

        assertEquals(10, new TaskExecuteCreator(bTaskplan, bTask).create().size());

        // 日周
        timeType = 10;
        activeType = 20;
        bTaskplan.setActiveType(activeType);
        bTaskplan.setStartTime(new Date("2017/10/01"));
        bTaskplan.setEndTime(new Date("2017/10/10 23:59:59"));
        bTaskplan.setDayNum("1,2,3");
        bTask.setTimeType(timeType);

        assertEquals(5, new TaskExecuteCreator(bTaskplan, bTask).create().size());

        // 日月
        timeType = 10;
        activeType = 30;
        bTaskplan.setActiveType(activeType);
        bTaskplan.setStartTime(new Date("2017/10/01"));
        bTaskplan.setEndTime(new Date("2017/10/31 23:59:59"));
        bTaskplan.setDayNum("1,2,3");
        bTask.setTimeType(timeType);

        assertEquals(3, new TaskExecuteCreator(bTaskplan, bTask).create().size());
        bTaskplan.setDayNum("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31");
        bTask.setTimeType(timeType);

        assertEquals(31, new TaskExecuteCreator(bTaskplan, bTask).create().size());

        // 周日
        timeType = 20;
        activeType = 10;
        bTaskplan.setActiveType(activeType);
        bTaskplan.setStartTime(new Date("2017/10/01"));
        bTaskplan.setEndTime(new Date("2017/10/10 23:59:59"));
        bTaskplan.setDayNum("1,2,3");
        bTask.setTimeType(timeType);

        assertEquals(1, new TaskExecuteCreator(bTaskplan, bTask).create().size());

        // 周周
        timeType = 20;
        activeType = 20;
        bTaskplan.setActiveType(activeType);
        bTaskplan.setStartTime(new Date("2017/10/01"));
        bTaskplan.setEndTime(new Date("2017/10/10 23:59:59"));
        bTaskplan.setDayNum("1,2,3");
        bTask.setTimeType(timeType);

        assertEquals(1, new TaskExecuteCreator(bTaskplan, bTask).create().size());

        // 周月
        timeType = 20;
        activeType = 30;
        bTaskplan.setActiveType(activeType);
        bTaskplan.setStartTime(new Date("2017/10/01"));
        bTaskplan.setEndTime(new Date("2017/10/31 23:59:59"));
        bTaskplan.setDayNum("1,2,3");
        bTask.setTimeType(timeType);

        assertEquals(4, new TaskExecuteCreator(bTaskplan, bTask).create().size());

        // 月日
        timeType = 30;
        activeType = 10;
        bTaskplan.setActiveType(activeType);
        bTaskplan.setStartTime(new Date("2017/10/01"));
        bTaskplan.setEndTime(new Date("2017/10/20 23:59:59"));
        bTaskplan.setDayNum("1,2,3");
        bTask.setTimeType(timeType);

        assertEquals(0, new TaskExecuteCreator(bTaskplan, bTask).create().size());

        // 月日
        timeType = 30;
        activeType = 10;
        bTaskplan.setActiveType(activeType);
        bTaskplan.setStartTime(new Date("2017/10/01"));
        bTaskplan.setEndTime(new Date("2017/10/31 23:59:59"));
        bTaskplan.setDayNum("1,2,3");
        bTask.setTimeType(timeType);

        assertEquals(1, new TaskExecuteCreator(bTaskplan, bTask).create().size());

        // 月日
        timeType = 30;
        activeType = 10;
        bTaskplan.setActiveType(activeType);
        bTaskplan.setStartTime(new Date("2017/10/01"));
        bTaskplan.setEndTime(new Date("2017/11/01 23:59:59"));
        bTaskplan.setDayNum("1,2,3");
        bTask.setTimeType(timeType);

        assertEquals(1, new TaskExecuteCreator(bTaskplan, bTask).create().size());

        // 月日
        timeType = 30;
        activeType = 10;
        bTaskplan.setActiveType(activeType);
        bTaskplan.setStartTime(new Date("2017/10/01"));
        bTaskplan.setEndTime(new Date("2017/11/30 23:59:59"));
        bTaskplan.setDayNum("1,2,3");
        bTask.setTimeType(timeType);

        assertEquals(2, new TaskExecuteCreator(bTaskplan, bTask).create().size());

        // 月周
        timeType = 30;
        activeType = 10;
        bTaskplan.setActiveType(activeType);
        bTaskplan.setStartTime(new Date("2017/10/01"));
        bTaskplan.setEndTime(new Date("2017/10/31 23:59:59"));
        bTaskplan.setDayNum("1,2,3");
        bTask.setTimeType(timeType);

        assertEquals(1, new TaskExecuteCreator(bTaskplan, bTask).create().size());

        // 月月
        timeType = 30;
        activeType = 10;
        bTaskplan.setActiveType(activeType);
        bTaskplan.setStartTime(new Date("2017/10/01"));
        bTaskplan.setEndTime(new Date("2017/10/31 23:59:59"));
        bTaskplan.setDayNum("1,2,3");
        bTask.setTimeType(timeType);

        assertEquals(1, new TaskExecuteCreator(bTaskplan, bTask).create().size());
    }

}
