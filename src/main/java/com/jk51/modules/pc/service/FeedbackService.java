package com.jk51.modules.pc.service;

import com.jk51.model.pc.Feedback;
import com.jk51.modules.pc.mapper.FeedbackMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/11/15
 * 修改记录:
 */
@Service
public class FeedbackService {
    private static Logger logger = LoggerFactory.getLogger(FeedbackService.class);

    @Autowired private FeedbackMapper feedbackMapper;


    public Integer add(Feedback feedback) {
        return feedbackMapper.add(feedback);
    }

    public List<Feedback> getLst(Integer siteId, Integer buyerId) {
        return feedbackMapper.getLst(siteId, buyerId);
    }

    public Integer reply(String reply, Integer siteId, Integer id) {
        return feedbackMapper.reply(reply, siteId, id);
    }

    public List<Feedback> getLstByUserId(Integer siteId, Integer memberId) {
        return feedbackMapper.getLstByUserId(siteId, memberId);
    }
}
