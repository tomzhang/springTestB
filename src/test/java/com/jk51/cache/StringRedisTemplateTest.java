package com.jk51.cache;

import com.jk51.Bootstrap;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * 文件名:com.jk51.cache.StringRedisTemplateTest
 * 描述: 本示例旨在提供RedisTemplate提供的ops对Redis操作的示例
 * 作者: wangzhengfei
 * 创建日期: 2017-01-22
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class StringRedisTemplateTest {

    private static final Logger logger = LoggerFactory.getLogger(StringRedisTemplateTest.class);

    @Autowired
    private StringRedisTemplate template;

    private String key;

    @Test
    public void testStringCRUD() throws Exception {
        key = "testStringCRUD";
        String value = Instant.now().toString();
        ValueOperations<String,String> ops = template.opsForValue();
        ops.set(key,value);
        assertEquals(value,ops.get(key));
        ops.set(key,value,1, TimeUnit.SECONDS);
        TimeUnit.MILLISECONDS.sleep(1100L);
        assertEquals(null,ops.get(key));
        ops.set(key,value,1,TimeUnit.SECONDS);
        assertEquals(value,ops.get(key));
        template.delete(key);
        assertEquals(null,ops.get(key));
        ops.set(key,value,1, TimeUnit.SECONDS);
    }

    @Test
    public void testHashCRUD() throws Exception{
        key = "testHashCRUD";
        String hashKey01 = "Key01";
        String value01 = Instant.now().toString();
        String hashKey02 = "Key02";
        String value02 = Instant.now().toString();
        HashOperations<String, String, String> ops =  template.opsForHash();
        ops.put(key,hashKey01,value01);
        assertEquals(1,ops.entries(key).values().size());
        assertEquals(value01,ops.get(key,hashKey01));
        ops.put(key,hashKey02,value02);
        assertEquals(value02,ops.get(key,hashKey02));
        ops.put(key,hashKey02,value01);
        assertEquals(value01,ops.get(key,hashKey02));
        ops.put(key,hashKey01,value02);
        assertEquals(value02,ops.get(key,hashKey01));
        assertEquals(2,ops.entries(key).values().size());
        ops.delete(key,hashKey01);
        assertEquals(1,ops.entries(key).values().size());
        template.delete(key);
        //即使缓存不存在调用到HashMap属性也不会报错
        assertEquals(0,ops.entries(key).values().size());
    }

    @Test
    public void testListCRUD(){
        int expectLen = 10;
        key = "testListCRUD";
        for(int i=0;i<expectLen;i++){
            template.opsForList().leftPush(key,"listValue"+i);
        }
        assertEquals(expectLen,template.opsForList().range(key,0,9).size());
        assertEquals("listValue0",template.opsForList().rightPop(key));
        //参考 https://redis.io/commands/lrem
        template.opsForList().remove(key,0,"listValue8");
        assertEquals(expectLen-2,template.opsForList().range(key,0,9).size());
        assertEquals("listValue7",template.opsForList().index(key,1));
        assertEquals("listValue4",template.opsForList().index(key,-4));
    }

    @Test
    public void testSetCRUD(){
        int expectLen = 100;
        key = "testSetCRUD";
        SetOperations<String,String> ops = template.opsForSet();
        for(int i=0;i<expectLen;i++){
            ops.add(key,"set"+i);
        }
        ops.remove(key,"set75","set0");
        assertEquals(expectLen-2,ops.members(key).size());
    }

    @Test
    public void testZSetCRUD(){
        int expectLen = 100;
        key = "testSortedSetCRUD";
        ZSetOperations<String,String> ops = template.opsForZSet();
        for(int i=0;i<expectLen;i++){
            ops.add(key,"set"+i,0);
        }
        ops.remove(key,"set75");
        assertEquals(expectLen-1,ops.count(key,0,0).intValue());
    }

    @After
    public void clear(){
        template.delete(key);
    }
}
