package com.jk51.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-02-22
 * 修改记录:
 */
@Service
public class TestSchedule {

    public static final Logger logger = LoggerFactory.getLogger(TestSchedule.class);

    public static final int timeout = 5;


    public void run() throws InterruptedException {
        logger.info("开始执行业务逻辑");
        TimeUnit.SECONDS.sleep(timeout);
        logger.info("业务逻辑执行完成");
    }
}
