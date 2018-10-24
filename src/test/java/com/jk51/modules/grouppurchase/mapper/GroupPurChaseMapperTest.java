package com.jk51.modules.grouppurchase.mapper;

import com.alibaba.fastjson.JSON;
import com.jk51.Bootstrap;
import com.jk51.model.grouppurchase.GroupPurchase;
import com.jk51.modules.grouppurchase.request.GroupPurchaseParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

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
public class GroupPurChaseMapperTest {
    @Autowired
    private GroupPurChaseMapper groupPurChaseMapper;

    @Test
    public void getGroupPurchaseList() throws Exception {
        GroupPurchaseParam groupPurchaseParam = new GroupPurchaseParam() {{
            setStatus(1);
        }};
        List<GroupPurchase> groupPurchaseList = groupPurChaseMapper.getGroupPurchaseList(groupPurchaseParam);
        System.out.println(groupPurchaseList);
        System.out.println(JSON.toJSONString(groupPurchaseList));
    }

    @Test
    public void getGroupPurchaseListForTask() {
    }

    @Test
    public void findByTradesId() {
        GroupPurchase groupPurchase = groupPurChaseMapper.findByTradesId(100190, "1001901491039469204");
        System.out.println(JSON.toJSONString(groupPurchase));
    }
}
