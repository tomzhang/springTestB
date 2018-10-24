package com.jk51.modules.promotions.service;

import com.alibaba.fastjson.JSON;
import com.jk51.Bootstrap;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.coupon.requestParams.SignMembers;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.activity.ShowRule;
import com.jk51.model.promotions.activity.VisitTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/9                                <br/>
 * 修改记录:                                         <br/>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class PromotionsActivityServiceTest {
    @Autowired
    private PromotionsActivityService service;

    @Test
    public void create() throws Exception {
        PromotionsActivity activity = new PromotionsActivity();

        activity.setTitle("活动发放入口a");
        activity.setSiteId(100073);
        activity.setStatus(0);
        activity.setPromotionsId(1);
        activity.setStartTime(LocalDateTime.now());
        activity.setEndTime(activity.getStartTime());
        activity.setIntro("活动发放介绍");
        activity.setPosterPic("活动海报图片url");
        activity.setTemplatePic("活动模版图片url");

        activity.setUseObject(JacksonUtils.obj2json(getSignMembers()));

        activity.setShowRule(JacksonUtils.obj2json(getShowRule()));

        ReturnDto returnDto = service.create(activity);
        System.out.println(returnDto);
    }

    private ShowRule getShowRule() {
        ShowRule showRule = new ShowRule();

        showRule.setIsShow(1);
        showRule.setForcePopup(0);
        showRule.setPopupAtHomePage(0);
        showRule.setPopupWhenLogin(1);

        return showRule;
    }

    private SignMembers getSignMembers() {
        SignMembers signMembers = new SignMembers();

        signMembers.setType(2);

        return signMembers;
    }



    @Test
    public void getPromotionsActivityWithPromotionsRule() {
        Optional<PromotionsActivity> optional = service.getActivePromotionsActivityWithPromotionsRule(100190, 374);
        if (optional.isPresent())
            System.out.println(JSON.toJSONString(optional));
    }

    /*@Test
    public void VisitInterFaceTest(){
        List<VisitTask> visitTasks = service.searchContainGoodsIdAndUserIdPromotions("1849244,1849312", "15234487", 100190);
        System.out.println("测试成功");
    }*/
}
