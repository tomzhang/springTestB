package com.jk51.modules.offline.Timer;

import com.alibaba.fastjson.JSONArray;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.erpprice.service.ErpPriceImportService;
import com.jk51.modules.erpprice.service.SyncService;
import com.jk51.modules.offline.service.OfflineCheckService;
import com.jk51.modules.offline.service.TimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-06-15
 * 修改记录:
 */
@Component
public class ERPPriceSyncTimer {

    private static final Logger logger = LoggerFactory.getLogger(OfflineCheckService.class);

    public static final List<Integer> ErpPriceSiteIdList = Arrays.asList(100213, 100271, 100262);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    SyncService syncService;
    @Autowired
    private ErpPriceImportService erpPriceImportService;
    @Autowired
    private TimeService timeService;

    @Scheduled(fixedDelay = 600000)
    public void execute() {
        for (Integer siteId : ErpPriceSiteIdList) {
            String values = stringRedisTemplate.opsForList().rightPop(siteId + "_erpStorePrice");
            if (StringUtil.isEmpty(values)) {
                logger.info("{}缓存中没有值", siteId + "_erpStorePrice");
                stringRedisTemplate.opsForValue().getAndSet(siteId + "_erpPriceListCounts", String.valueOf(0));
            } else {
                try {
                    Map<String, Object> params = JacksonUtils.json2map(values);
                    logger.info("{}缓存中值：{}", siteId + "_erpStorePrice", params);
                    erpPriceImportService.storeSyncPrice(params);
                    List goodsList = JSONArray.parseArray(JacksonUtils.obj2json(params.get("goodlist")), Map.class);
                    Integer counts = getErpPriceList(siteId);
                    stringRedisTemplate.opsForValue().getAndSet(siteId + "_erpPriceListCounts", String.valueOf(counts));
                    Set<String> gcodes = new HashSet<>();
                    for (Object object : goodsList) {
                        String gCode = ((Map<String, Object>) object).get("goods_code").toString();
                        gcodes.add(gCode);
                    }
                    syncService.syncStorePriceFromApp(Integer.parseInt(params.get("siteId").toString()), new ArrayList<>(gcodes));
                } catch (Exception e) {
                    logger.info("多价格数据" + e);
                }
            }
        }

    }

    /**
     * 获取缓存中多价格一共有多少条数据
     *
     * @param siteId
     * @return
     */
    public int getErpPriceList(Integer siteId) {
        int counts = 0;
        try {
            Long size = stringRedisTemplate.opsForList().size(siteId + "_erpStorePrice");
            for (int i = 0; i < size; i++) {
                counts += JSONArray.parseArray(JacksonUtils.obj2json(
                    JacksonUtils.json2map(stringRedisTemplate.opsForList().index(siteId + "_erpStorePrice", i)).get("goodlist")), Map.class).size();
            }
            return counts;
        } catch (Exception e) {

        }
        return counts;
    }
}
