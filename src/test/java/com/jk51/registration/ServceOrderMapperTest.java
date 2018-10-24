package com.jk51.registration;


import com.jk51.Bootstrap;
import com.jk51.model.registration.models.ServceOrder;
import com.jk51.modules.registration.mapper.ServceOrderMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2017/4/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class ServceOrderMapperTest {
    @Autowired
    private ServceOrderMapper servceOrderMapper;


    @Test
    public void addContactPersonTest(){
        ServceOrder servceOrder = servceOrderMapper.selectByPrimaryKey(1, 100086);
        System.out.println(servceOrder.toString());
    }

}
