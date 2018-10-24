package com.jk51.modules.appInterface.controller;

import com.jk51.commons.date.DateUtils;
import com.jk51.modules.appInterface.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 统计请求
 * 作者: gaojie
 * 创建日期: 2017-03-09
 * 修改记录:
 */

@Controller
@ResponseBody
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 日销售统计
     */
    @RequestMapping(value = "/todaystatistics")
    @ResponseBody
    public Map<String, Object> todaystatistics(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        return statisticsService.todaystatistics(accessToken);
    }

    /**
     * 日销售统计 /todaystatistics v2
     */
    @RequestMapping(value = "/daystatistics")
    @ResponseBody
    public Map<String, Object> daystatistics(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        return statisticsService.todaystatistics2(accessToken);
    }


    /**
     * 所有注册统计
     */
    @RequestMapping(value = "/registercount")
    public Map<String, Object> registercount(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");

        //time为null时查询所有时间的注册统计
        return statisticsService.registercount(accessToken, null);
    }

    /**
     * 月注册统计
     * 给定时间天2017-03
     * 返回每天的注册数量
     */
    @RequestMapping(value = "/registermonth")
    public Map<String, Object> registermonth(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        String month = (String) body.get("month");
        return statisticsService.registermonth(accessToken, month);
    }

    /**
     * 给定时间的注册列表
     * 给定时间天2017-03-10
     */
    @RequestMapping(value = "/registerdayList")
    public Map<String, Object> registerdaylist(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        String date = (String) body.get("date");
        return statisticsService.registerdaylist(accessToken, date);
    }


    /**
     * 销售统计
     */
    @RequestMapping(value = "/tradescount")
    public Map<String, Object> tradescount(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        return statisticsService.tradescount(accessToken);
    }

    /**
     * 门店 销售统计   tradescount v2
     */
    @RequestMapping(value = "/tradescount2")
    public Map<String, Object> tradescount2(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        return statisticsService.tradescount2(accessToken);
    }

    /**
     * 店员 销售统计   tradescount v2
     */
    @RequestMapping(value = "/tradescountclerk2")
    public Map<String, Object> tradescountclerk2(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        return statisticsService.tradescountclerk2(accessToken);
    }


    /**
     * 给定月份每天的销售量
     */
    @RequestMapping(value = "/tradesmonth")
    public Map<String, Object> tradesmonth(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        String month = (String) body.get("month");
        return statisticsService.tradesmonth(accessToken, month);
    }

    /**
     * 门店  给定月份每天的销售量  tradesmonth  v2
     */
    @RequestMapping(value = "/tradesmonth2")
    public Map<String, Object> tradesmonth2(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        String month = (String) body.get("month");
        return statisticsService.tradesmonth2(accessToken, month);
    }

    /**
     * 店员  给定月份每天的销售量  tradesmonth  v2
     */
    @RequestMapping(value = "/tradesmonthclerk2")
    public Map<String, Object> tradesmonthclerk2(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        String month = (String) body.get("month");
        return statisticsService.tradesmonthclerk2(accessToken, month);
    }


    /**
     * 给定每天的销售列表
     */
    @RequestMapping(value = "/tradesdaylist")
    public Map<String, Object> tradesdaylist(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        String date = (String) body.get("date");
        return statisticsService.tradesdaylist(accessToken, date);
    }

    /**
     * 给定每天的销售列表  门店
     */
    @RequestMapping(value = "/tradesdaylist2")
    public Map<String, Object> tradesdaylist2(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        String date = (String) body.get("date");
        return statisticsService.tradesdaylist2(accessToken, date);
    }

    /**
     * 给定每天的销售列表   店员
     */
    @RequestMapping(value = "/tradesdaylistclerk2")
    public Map<String, Object> tradesdaylistclerk2(@RequestBody Map<String, Object> body) {

        String accessToken = (String) body.get("AuthToken");
        String date = (String) body.get("date");
        return statisticsService.tradesdaylistclerk2(accessToken, date);
    }

    /**
     * 当前时间订单总额
     *
     * @param siteId
     * @param storeId
     * @param storeUserId
     * @return
     */
    @RequestMapping("appqueryTrades")
    public Map<String, Object> appqueryTrades(Integer siteId, Integer storeId, Integer storeUserId) {
        Date date = new Date();
        Map<String, Object> responseResult = new HashMap<>();
        String nowDate = DateUtils.formatDate(date, "yyyy-MM-dd");
        List<Map<String, Object>> current = statisticsService.appselectTradespay(siteId, storeId, storeUserId, nowDate);
        responseResult.put("result", current);
        responseResult.put("maxValue", statisticsService.selectMaxValue(current));
        return responseResult;
    }

    /**
     * 当前时间订单数量
     *
     * @param siteId
     * @param storeId
     * @param storeUserId
     * @return
     */
    @RequestMapping("appselectTradenum")
    public Map<String, Object> appselectTradenum(Integer siteId, Integer storeId, Integer storeUserId) {
        Date date = new Date();
        Map<String, Object> responseResult = new HashMap<>();
        String nowDate = DateUtils.formatDate(date, "yyyy-MM-dd");
        List<Map<String, Object>> current = statisticsService.appselectTradenum(siteId, storeId, storeUserId, nowDate);
        responseResult.put("result", current);
        responseResult.put("maxValue", statisticsService.selectMaxValue(current));
        return responseResult;
    }

    /**
     * 当前时间会员数量
     *
     * @param siteId
     * @param storeId
     * @param storeUserId
     * @return
     */
    @RequestMapping("appselectmembernum")
    public Map<String, Object> appselectmembernum(Integer siteId, Integer storeId, Integer storeUserId) {
        Date date = new Date();
        Map<String, Object> responseResult = new HashMap<>();
        String nowDate = DateUtils.formatDate(date, "yyyy-MM-dd");
        List<Map<String, Object>> current = statisticsService.appselectmembernum(siteId, storeId, storeUserId, nowDate);
        responseResult.put("result", current);
        responseResult.put("maxValue", statisticsService.selectMaxValue(current));
        return responseResult;
    }

}
