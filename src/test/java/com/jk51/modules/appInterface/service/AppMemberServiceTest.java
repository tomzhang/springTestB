package com.jk51.modules.appInterface.service;

import com.jk51.Bootstrap;
import com.jk51.modules.im.service.PushType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by admin on 2017/10/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class AppMemberServiceTest {

    @Autowired
    private AppMemberService appMemberService;

    @Test
    public void notifyTaskMessage() {
        try {
            appMemberService.notifyTaskMessage(100190,100882,null, PushType.TASK_NEWEXAM,"测试4");
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true);
    }

}
