package com.jk51.persistence;

import com.jk51.Bootstrap;
import com.jk51.model.account.models.MerchantConfig;
import com.jk51.modules.account.mapper.MerchantConfigMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class MerchantConfigMapperTest {

    @Autowired
    private MerchantConfigMapper mapper;

    @Test
    public void getAll(){

        List<MerchantConfig> list=mapper.getAll();

        list.parallelStream().forEach(merchantConfig -> System.out.println(merchantConfig.getSite_id()));

    }

    @Test
    public void get(){

    }

}
