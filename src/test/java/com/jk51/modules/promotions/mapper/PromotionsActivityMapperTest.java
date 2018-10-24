package com.jk51.modules.promotions.mapper;

import com.alibaba.fastjson.JSON;
import com.jk51.Bootstrap;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.activity.PromotionsActivitySqlParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/11/22                                <br/>
 * 修改记录:                                         <br/>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class PromotionsActivityMapperTest {
    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;

    @Test
    public void getSomeInfoForTask() throws Exception {
        List<Map<String, Object>> list = promotionsActivityMapper.getSomeInfoForTask(new HashSet<Integer>() {{
            add(115);
            add(116);
            add(117);
            add(118);
            add(119);
            add(120);
        }});

        System.out.println(list);
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void findByParamWithRuleIn() {
        PromotionsActivitySqlParam param = new PromotionsActivitySqlParam();
        param.setSiteId(100190);
        List<Integer> statusList = new ArrayList<>(Arrays.asList(0, 2));
        param.setStatusList(statusList);
        List<PromotionsActivity> activityList = promotionsActivityMapper.findByParamWithRuleIn(param);

        System.out.println(JSON.toJSONString(activityList));
    }
}
