package com.jk51.modules.es.utils;

import com.jk51.Bootstrap;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by admin on 2018/6/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class QueryUtilsTest {


    @Test
    public void queryList() {
//        qb.should(QueryBuilders.queryStringQuery("keyword_py:"+PinYinUtil.getPinYin(key,"")));
    }
}
