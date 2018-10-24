package com.jk51.modules.pc.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.model.pc.Feedback;
import com.jk51.model.pc.Notice;
import com.jk51.modules.pc.service.FeedbackService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:  反馈回复
 * 作者: chen_pt
 * 创建日期: 2017/11/15
 * 修改记录:
 */
@Controller
@RequestMapping("feedback")
public class FeedbackController {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(FeedbackController.class);

    @Autowired private FeedbackService feedbackService;

    @RequestMapping("add")
    public @ResponseBody Integer add(Feedback feedback){
        return feedbackService.add(feedback);
    }

    @RequestMapping("getLst")
    public @ResponseBody
    PageInfo<Feedback> getLst(Integer siteId, Integer buyerId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Feedback> list = feedbackService.getLst(siteId, buyerId);
        return new PageInfo<Feedback>(list);
    }

    @RequestMapping("reply")
    public @ResponseBody Integer reply(String reply, Integer siteId, Integer id){
        return feedbackService.reply(reply, siteId, id);
    }

    @RequestMapping("getLstByUserId")
    public @ResponseBody List<Feedback> getLstByUserId(Integer siteId, Integer memberId){
        return feedbackService.getLstByUserId(siteId, memberId);
    }

}
