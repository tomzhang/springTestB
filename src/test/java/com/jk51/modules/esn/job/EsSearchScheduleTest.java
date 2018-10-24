package com.jk51.modules.esn.job;

import com.jk51.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by admin on 2017/9/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class EsSearchScheduleTest {

    @Autowired
    private EsSearchSchedule esSearchSchedule;

    @Test
    public void autoUpdateESIndex() {
        esSearchSchedule.autoUpdateESIndex();
        while (true);
    }

}
