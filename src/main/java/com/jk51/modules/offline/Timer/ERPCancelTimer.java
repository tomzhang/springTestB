package com.jk51.modules.offline.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.jk51.modules.offline.Timer.ERPPriceSyncTimer.ErpPriceSiteIdList;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-07-04
 * 修改记录:
 */
@Component
public class ERPCancelTimer {

    private static final Logger log = LoggerFactory.getLogger(ERPCancelTimer.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 0 1 * * ?")
    public void execute() {
        for (Integer siteId : ErpPriceSiteIdList) {
            log.info("erp多价格统计次数归零{}", siteId);
            stringRedisTemplate.opsForValue().set(siteId + "_erpPriceNums", "0");
        }
    }
}
