package com.jk51.modules.merchant.service;

import com.jk51.commons.date.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:统计优化初始化服务
 * 作者: dumingliang
 * 创建日期: 2017-07-14
 * 修改记录:
 */
@Service
public class TransAccountService {
    private static final Logger logger = LoggerFactory.getLogger(TransAccountService.class);
    @Autowired
    private TimerInsertService timerInsertService;

    public void initStaticsRecord() {
        logger.info("初始化执行统计方法{}", "initStaticsRecord");
        Date date = new Date();
        String nowDate = DateUtils.formatDate(date, "yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        logger.info("本年中的第" + cal.get(Calendar.DAY_OF_YEAR) + "天");
        String startDate = DateUtils.formatDate(DateUtils.getBeforeOrAfterDate(date, -cal.get(Calendar.DAY_OF_YEAR)), "yyyy-MM-dd");
        List<String> dateList = DateUtils.getContinuousDayStr(startDate, nowDate);
        for (int i = 1; i <=dateList.size() - 2; i++) {
            logger.info("当前时间{}", dateList.get(i));
            try {
                //数据概况初始化
                //timerInsertService.dataInsert(dateList.get(i));

                //流量分析初始化
                //timerInsertService.dataInsertFlow(dateList.get(i));

                //交易分析
                //timerInsertService.dataInsertTransaction(dateList.get(i));

            } catch (Exception e) {
                logger.info("初始化执行方法失败{}", e);
            }

        }
    }

}
