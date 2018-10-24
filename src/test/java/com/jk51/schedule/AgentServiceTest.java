package com.jk51.schedule;

import com.jk51.Bootstrap;
import com.jk51.modules.schedule.AgentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-02-22
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class AgentServiceTest {

    public static final Logger logger = LoggerFactory.getLogger(AgentServiceTest.class);

    @Autowired
    private AgentService service;

    @Test
    public void invoke(){
        Map<String,Object> parameter =  new HashMap<>();
        service.perform(1,parameter);
    }
}
