package com.jk51.modules.clerkvisit.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.clerkvisit.BVisitTrade;
import com.jk51.modules.clerkvisit.service.BVisitTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/25.
 */
@Controller
@RequestMapping("/visitTrades")
public class BVisitTradeController {
    @Autowired
    private BVisitTradeService bVisitTradeService;

    /**
     * 统计活动时间内回访顾客购买回访商品的订单量(只统计药店帮手)
     * @param request
     * param:siteId
     * param:activityId 活动编号
     * param:startTime  活动开始时间
     * param:endTime    活动结束时间
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTradesCount")
    public ReturnDto getTradesCount(HttpServletRequest request){
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        int count =bVisitTradeService.getCountForActivityTrades(param);
        return ReturnDto.buildSuccessReturnDto(count);
    }

    @ResponseBody
    @RequestMapping("/insertSelective")
    public ReturnDto insertSelective(BVisitTrade bVisitTrade){
        try {
            bVisitTradeService.insertSelective(bVisitTrade);
            return ReturnDto.buildSuccessReturnDto("创建成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnDto.buildFailedReturnDto("创建失败");
    }

}
