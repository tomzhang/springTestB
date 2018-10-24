package com.jk51.registration;


import com.jk51.Bootstrap;
import com.jk51.model.registration.models.ContactPerson;
import com.jk51.modules.registration.mapper.ContactPersonMapper;
import com.jk51.modules.registration.mapper.ServceOrderMapper;
import com.jk51.modules.registration.service.ConactPersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by Administrator on 2017/4/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class ContactPersonDaoTest {
    @Autowired
    private ConactPersonService conactPersonservice;

    @Autowired
    private ContactPersonMapper contactPersonMapper;

    @Autowired
    ServceOrderMapper servceOrderMapper;

    @Test
    public void queryContactPersonTest(){
        ContactPerson personParam=new ContactPerson();

        personParam.setMemberId(22);

   // contactPersonMapper.selectByPrimaryKey(2,100030);
        servceOrderMapper.selectByPrimaryKey(3,100086);

    }
}
