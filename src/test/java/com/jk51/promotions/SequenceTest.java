package com.jk51.promotions;

import com.jk51.Bootstrap;
import com.jk51.model.promotions.sequence.SequenceParam;
import com.jk51.model.promotions.sequence.SequenceResult;
import com.jk51.model.promotions.sequence.wechat.WechatSequenceResult;
import com.jk51.modules.promotions.sequence.app.AppSequenceHandlerImp;
import com.jk51.modules.promotions.sequence.app.AppSequenceImpl;
import com.jk51.modules.promotions.sequence.wechat.WechatSequenceHandlerImpl;
import com.jk51.modules.promotions.sequence.wechat.WechatSequenceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletContext;
import java.util.Set;

/**
 * Created by javen73 on 2018/5/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class SequenceTest {
    @Autowired
    private ServletContext sc;

    SequenceParam param = new SequenceParam(100190,15234298,1,10,60);

    @Test
    public void test1()throws Exception{
        long timeMillis = System.currentTimeMillis();
        AppSequenceImpl appSequence = new AppSequenceImpl(sc,param);
        appSequence.collection();
        appSequence.processGoods();
        appSequence.processSequence(new AppSequenceHandlerImp());
        appSequence.processTags();
        SequenceResult sequenceResult = appSequence.result.get();
        System.out.println(System.currentTimeMillis()-timeMillis);
        System.out.println(sequenceResult);
        while (true){}
    }


    @Test
    public void test2() throws Exception{
        WechatSequenceImpl ws = new WechatSequenceImpl(sc,param);
        ws.collection();
        ws.processGoods();
        ws.processSequence(new WechatSequenceHandlerImpl());
        ws.processTags();
        WechatSequenceResult sequenceResult =(WechatSequenceResult) ws.result.get();
        while(true){}
    }
}
