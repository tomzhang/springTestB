package com.jk51.modules.pandian.service;

import com.alibaba.fastjson.JSON;
import com.jk51.model.BPandianOrder;
import com.jk51.model.BPandianPlan;
import com.jk51.modules.pandian.mapper.BPandianOrderMapper;
import com.jk51.modules.pandian.mapper.BPandianPlanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class PandianSchedule {
    private static final Logger logger = LoggerFactory.getLogger(PandianSchedule.class);
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BPandianPlanMapper bPandianPlanMapper;
    @Autowired
    private PandianService pandianService;

    /*
        商家后台
            盘点单：PD100229170001
            100229   商家编号
            17  代表年份
            0001   代表单据序列号
            按照此规格编写的，年按照年份变更，序列号按照单据递增，0002,0003依次递增
        盈亏单同理，只是把PD换成YK。
        门店后台
            盘点单：PD10001170001
            10001   门店编号
            17  代表年份
            0001   代表单据序列号
     */
//    @Scheduled(fixedDelay = 1000 * 10 * 1)
    public void createPandianOrder() {
        List<BPandianPlan> planList = bPandianPlanMapper.getUnCreateOrderPlanList();
        if(planList != null && planList.size() != 0){
            for(BPandianPlan plan : planList){
                try {
                    pandianService.createPandianOrder(plan, null);
                } catch (Exception e) {
                    logger.error("createPandianOrder 异常 {}", e);
                }
            }
        }
    }

}
