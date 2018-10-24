package com.jk51.registration;

import com.jk51.Bootstrap;
import com.jk51.model.registration.models.ServceTemplate;
import com.jk51.model.registration.models.ServceUseDetail;
import com.jk51.model.registration.requestParams.TemplateParams;
import com.jk51.model.registration.requestParams.TemplateSonParams;
import com.jk51.modules.registration.mapper.ServceUseDetailMapper;
import com.jk51.modules.registration.service.TemplateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.security.Timestamp;
import java.util.*;

/**
 * filename :com.jk51.registration.
 * author   :zw
 * date     :2017/4/7
 * Update   :
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class TemplateServiceTest {

    @Autowired
    private TemplateService templateService;
    @Autowired
    private ServceUseDetailMapper servceUseDetailMapper;

    @Test
    public void testCreateTemplate() {
        TemplateParams templateParams = new TemplateParams();
        templateParams.setSiteId(100030);
        templateParams.setStoreId(13000);
        List<TemplateSonParams> list = new ArrayList<>();
        TemplateSonParams templateSonParams = new TemplateSonParams();
        templateSonParams.setStartTime(System.currentTimeMillis());
        templateSonParams.setEndTime(System.currentTimeMillis());
        templateSonParams.setAccountSource(11);
        list.add(templateSonParams);
        templateParams.setTemplateRule(list);
        templateService.createTemplate(templateParams);
    }
    @Test
    public void testData(){
        long current=System.currentTimeMillis();//当前时间毫秒数
        long zero=current/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();
        System.out.println(new Date(zero));
    }
    @Test
    public void testMineClasses(){
        Date startTime = getTimesMonthStart();
        Date endTime = getTimesMonthEnd();
        servceUseDetailMapper.selectAllMineClassesByGoodsId(100030,13440,startTime,endTime);
    }

    // 获得本月第一天0点时间
    public static Date getTimesMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    // 获得本月最后一天24点时间
    public static Date getTimesMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }

    @Test
    public void testSelect(){
        /*Map<String, ServceTemplate> stringServceTemplateMap = servceUseDetailMapper.selectAllMineClassesByGoodsId(13000, goodsId,
                                                            startTime, endTime)*/
        //Map<String, ServceTemplate> stringServceTemplateMap = templateService.queryTemplateByStoreId(13000);
        System.out.println(1);
    }

}
