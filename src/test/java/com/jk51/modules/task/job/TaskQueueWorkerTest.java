package com.jk51.modules.task.job;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.model.Message;
import com.jk51.Bootstrap;
import com.jk51.model.task.BTaskcount;
import com.jk51.modules.task.mapper.BTaskcountMapper;
import com.jk51.modules.task.mapper.BTaskrewardMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.stream.Stream;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class TaskQueueWorkerTest {
    @Autowired
    TaskQueueWorker taskQueueWorker;

    @Autowired
    BTaskcountMapper bTaskcountMapper;

    @Autowired
    BTaskrewardMapper bTaskrewardMapper;

    @Test
    public void consume() throws Exception {
        Stream.of(22317).forEach(id -> {
            Message message = new Message();
            BTaskcount bTaskcount = bTaskcountMapper.selectByPrimaryKey(id);
            message.setMessageBody(JSONObject.toJSONString(bTaskcount));
            try {
                taskQueueWorker.consume(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
