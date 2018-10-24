package com.jk51.modules.esn.controller;

import com.jk51.Bootstrap;
import com.jk51.commons.http.HttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * Created by admin on 2017/9/6.
 */
//更新es索引
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class EsWriteControllerTest {

    @Test
    public void updateindex() {
        try {
            String s = HttpClient.doHttpPost("http://172.20.10.29:8768/es/batchGoods/1_2_100190");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true);
    }
}
