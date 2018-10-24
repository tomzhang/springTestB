package com.jk51.modules.merchant.controller;

import com.jk51.commons.date.StatisticsDate;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.merchant.service.DataProfileService;
import com.jk51.modules.merchant.service.TimerInsertService;
import com.jk51.modules.merchant.service.TransAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 统计数据定时添加数据
 */
@Controller
@RequestMapping("/merchant/dataProfile")
public class TimerInsertController {

    @Autowired
    private TimerInsertService timerInsertService;
    @Autowired
    private TransAccountService transAccountService;
    @Autowired
    private DataProfileService dataProfileService;
    private static final Logger log = LoggerFactory.getLogger(TimerInsertController.class);

    @RequestMapping(value = "/dataInsert")
    @ResponseBody
    public void dataInsert(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        try {
            //timerInsertService.dataInsert(param);
        } catch (Exception e) {
            log.info("插入失败:{}", e);
        }
    }

    //全部初始化
    @GetMapping(value = "/dataInit")
    public void dataInit() {
        try {
            transAccountService.initStaticsRecord();
        } catch (Exception e) {
            log.info("插入失败:{}", e);
        }
    }

    //添加指定日期数据
    @ResponseBody
    @PostMapping(value = "/dataInitForTime")
    public void dataInitForTime(String time,Integer i) {
        try {
            //执行
            dataInitForTimePublic(time,i);
        } catch (Exception e) {
            log.info("插入失败:{}", e);
        }
    }

    //添加指定时间段数据
    @ResponseBody
    @PostMapping(value = "/dataInitForTimeQuantum")
    public void dataInitForTimeQuantum(String time,Integer days,Integer i) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(time);
            List<Map<String, Date>> dates = StatisticsDate.processDate(date, days);
            Collections.reverse(dates);
            for (Map<String, Date> map : dates){
                Date sTime = map.get("start");
                String str = sdf.format(sTime);
                //执行
                dataInitForTimePublic(str,i);
                //System.out.println("-------------------"+str);
            }
        } catch (Exception e) {
            log.info("插入失败:{}", e);
        }
    }

    //按参数执行
    public void dataInitForTimePublic(String time,Integer i) {
        try {
            if ( 1001 == i){
                timerInsertService.dataInsert(time);//数据概况
            }else if (1002 == i){
                timerInsertService.dataInsertFlow(time);//流量分析
                System.out.println("-------------------"+time);
            }else if(1003 == i){
                timerInsertService.dataInsertTransaction(time);//交易分析
            }
        } catch (Exception e) {
            log.info("插入失败:{}", e);
        }
    }

    /**
     * 修改停留时间 & 区域名称
     * @param start
     * @param end
     * @param i
     */
    @ResponseBody
    @PostMapping(value = "/updateDate4DBE")
    public void updateDate4DBE(Integer start,Integer end,Integer i){
        try{
            if (1001 == i){
                dataProfileService.ip2Name4DB(start,end);//修改数据表中的IP名称
            }else if (1002 == i){
                dataProfileService.updateTime4DB(start,end);//修改数据表中的停留时间
            }
        }catch (Exception e){
            log.info("修改异常:{}",e);
        }
    }

    /**
     * 修改数据状态
     * @param time
     */
    @ResponseBody
    @PostMapping(value = "/updateStruts")
    public void updateStruts(String time){
        dataProfileService.updateStruts(time);
    }
}
