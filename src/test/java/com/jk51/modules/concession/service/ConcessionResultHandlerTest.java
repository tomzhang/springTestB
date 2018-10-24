package com.jk51.modules.concession.service;

import com.alibaba.fastjson.JSON;
import com.jk51.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by ztq on 2018/3/12
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class ConcessionResultHandlerTest {

    @Autowired
    private ConcessionResultHandler concessionResultHandler;

    @Test
    public void getConcessionResultByTradesId2() {

    }
}
