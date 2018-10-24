package com.jk51.modules.task.domain.count;

import com.jk51.configuration.DatabaseConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class CountsTest {
    @Test
    public void getRangeTime() throws Exception {
//        Counts counts = new Counts();
        LocalDateTime startTime = LocalDateTime.of(2017, 11, 1, 0, 0, 0);
        LocalDateTime executeEnd = LocalDateTime.of(2017, 12, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.now();
        int[] dayList = IntStream.iterate(1, i -> i + 1).limit(31).toArray();
        CountRangeTime rangeTime = Counts.calcRangeTime(startTime, endTime, executeEnd, (byte) 30, dayList);

        assertEquals(rangeTime.getEndTime(), endTime);
    }

}
