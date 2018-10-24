package com.jk51.index;

import com.jk51.Bootstrap;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.FirstWeight;
import com.jk51.model.SendRaceAnswerRecode;
import com.jk51.model.packageEntity.StoreAdminIndex;
import com.jk51.model.packageEntity.TargetIndexValue;
import com.jk51.modules.im.controller.IMController;
import com.jk51.modules.im.mapper.BIMServiceMapper;
import com.jk51.modules.im.mapper.SendRaceAnswerRecodeMapper;
import com.jk51.modules.im.service.IMService;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.index.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-20
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class IndexTest {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Test
    public void testStoreAdminIndexList() throws Exception {
        String  ss  =  stringRedisTemplate.opsForValue().get("zq6BNf_shortUrl");
        String aa =    ss.toString();

        /*Map<>
        iMService.saveImService();
*/
/*
        Map<String,String> param = new HashMap<>();
        param.put("msg_type","4");*/

/*
        IMController controller = new IMController();
        boolean b = controller.checkRelation("","wechat_614024");
        String aa = "11";*/
/*
        List<FirstWeight> list =  firstWeightService.getFirstWeightByOwner("123123");
        for(FirstWeight firstWeight:list){
            String w = firstWeight.getWeight_name();
        }*/
      /*  countIndexService.countIndex();*/
     /*   URL url = new URL("http://172.20.10.184:8762/users/test");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.connect();

        Map<String,List<String>> fields = conn.getHeaderFields();
        String mp = conn.getHeaderField("Set-Cookie");

        String sessionId = "";
        if(!StringUtil.isEmpty(mp)){
            sessionId = mp.substring(mp.indexOf("="),mp.indexOf(";"));
        }*/

        /*String cookieVal = "";
        String key = null;
        //取cookie
        for(int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++){
            if(key.equalsIgnoreCase("Set-cookie")){
                cookieVal = conn.getHeaderField(i);
                cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                sessionId = sessionId + cookieVal + ";";
            }
        }*/

      /*  System.out.println("**JSESSIONID*********************");
        System.out.println(sessionId);
        System.out.println("*********************************");

        conn.disconnect();*/
        //List<String> dayList = getDayListOfMonth(2017,3,0);
        //SendRaceAnswerRecode recode = new  SendRaceAnswerRecode();
       /* recode.setAppid("12354");
        recode.setMsg("123443");
        recode.setReceiver("1231241");
        recode.setSender("1231241");

        int id = sendRaceAnswerRecodeMapper.insertSelective(recode);
        int num = recode.getId();
      stringRedisTemplate.opsForValue().set("ca","111");*/
       /* stringRedisTemplate.opsForValue().set("ca","111:1");
        //stringRedisTemplate.expire("aaa",5, TimeUnit.SECONDS);
      // assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
       // stringRedisTemplate.opsForValue().set("call,123456,18816904369,12306,value","test");
       String aaa =  stringRedisTemplate.opsForValue().get("ca");
        String aaa1 =  stringRedisTemplate.opsForValue().get("ca");*/
        //targetService.getAllTarget();
        //firstWeightService.getAllFirstWeight();

        /*long times = System.currentTimeMillis();
       List<StoreAdminIndex> storeAdminIndexList = storeAdminMapper.getAllStoreAdminIndexList();
       countIndexService.countIndex();
       long times2 = (System.currentTimeMillis() - times)/1000/60;
       long times3 = times2+1;*/

        //assertEquals(true,storeAdminIndexList.size()>0);
       // assertNotNull("指标为空",targetService.getTargetBySiteId(100002));
        //StoreAdminIndex index = storeAdminIndexList.get(0);
        //index.setTargetList(targetService.getTargetBySiteId(100002));
        //countTitleIndexService.countTitleIndexOne(index);
        //countSatisfactionIndexService.countSatisfactionIndexOne(index);
        //countHistoryAnswerSpeedIndexService.countHistoryAnswerSpeedIndexServiceOne(index);
        //countBusyIndexService.CountBusyIndexOnde(index);
        //countStoreAttributesIndexService.countStoreAttributesFlagshipstoreIndexOne(index);
        //以店铺的算法计算指标分数
       //countServiceTotalIndexService.countServiceTotalIndex(storeAdminIndexList);
        //countStoreAttributesIndexService.countStoreAttributesOrderquantityIndex(storeAdminIndexList);

        //保存亲密度指标
        //countCloseIndexService.countCloseIndex(storeAdminIndexList);

        //countIndexService.countIndex();
       /* long times = System.currentTimeMillis();
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("sender","18615153756");
        param.put("site_id","100043");

        assertNotNull( "手机号字符串为空",screeningClerkService.getClerkId(param));
        long times2 = (System.currentTimeMillis() - times)/1000;
        long times3 = times2+1;*/
      /*  TargetIndexValue value = new TargetIndexValue();
        value.setHistoryAnswerSpeedIndex(5);
        value.setBusyIndex(5.5);
        value.setIntersectionIndex(5);
        value.setOrderNumIndex(5);
        value.setSatisfactionIndex(5);
        value.setServiceTotalIndex(5);
        value.setStoreAttributesClerkQuantityIndex(5);
        value.setStoreAttributesFlagshipstoreIndex(5);
        value.setTitleIndex(5);
        value.setStoreAttributesOrderquantityIndex(5);


        String jsonStr = JacksonUtils.obj2json(value);

        TargetIndexValue value2 =  indexInitService.targetRecodeJsonToBean(jsonStr);*/
    }

}
