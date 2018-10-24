package com.jk51.storage;

import com.jk51.Bootstrap;
import com.jk51.model.order.GoodsInfo;
import com.jk51.model.order.Store;
import com.jk51.model.order.Trades;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.offline.service.BaoDaoOfflineService;
import com.jk51.modules.offline.service.OfflineOrderService;
import com.jk51.modules.offline.service.StorageService;
import com.jk51.modules.order.service.DistributeOrderService;
import com.jk51.modules.trades.service.TradesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-12-09
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class storageERP {

    private static final Logger logger = LoggerFactory.getLogger(storageERP.class);
    @Autowired
    private BaoDaoOfflineService baoDaoOfflineService;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private StorageService storageService;
    @Autowired
    private OfflineOrderService offlineOrderService;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private DistributeOrderService orderService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TradesService tradesService;

    @Test
    public void getStoreByCityId() {
        try {
         /*   String goodno = "601-03-015,301-01-193,400-00-055";
            List<Map<String, Object>> storeList = storesMapper.getStoreIdBySiteId(100030);
            for (Map<String, Object> store : storeList) {
                Map<String, Object> result = baoDaoOfflineService.getStorageList(goodno, store.get("storeNumber").toString());
                logger.info("结果是" + result);
            }*/
            Map<String, Object> result = baoDaoOfflineService.getStorageList("70101035", "001");
            logger.info("结果是" + result);
        } catch (Exception e) {
            logger.info("宝岛报错了" + e);
        }

    }

    @Test
    public void getStoreByCityId_100213() {
        try {
            String goodno = "";
          /*  Map<String, Object> result = storageService.getStorageBysiteId("100190", "04500602,03601601", "80005");*/
//            Map<String, Object> result = storageService.getStorageBysiteId("100271", "14001900,60071589", "78");
//            Map<String, Object> result = storageService.getStorageBysiteId("100173", "5587,", "10957,3054,3017,");
            Map<String, Object> result = storageService.getStorageBysiteId("100030", "70101035,", "001");
            logger.info("result{}", result.toString());
        } catch (Exception e) {
            logger.info("e:{}", e.getMessage());
        }

    }

    @Test
    public void pushOrderErp() {
        try {
            offlineOrderService.pushOrder_haidian(100262, 1001901532401295579l, "http://122.228.181.194:7980/");
        } catch (Exception e) {
            logger.info("100262:" + e);
        }
    }

    private static final List<String> goodCodeList = Arrays.asList("70101035");

    @Test
    public void storageERPTest() {
        try {
            Integer siteId = 100030;
//            List<Store> storeList = storesMapper.selectAllStoreByStatus(siteId, 1);
            String storeNums = "002,008,001,022,003,010,007,009,015,005,010002,029,030";
//            String storeNums = "1019,1027,1011,1023,1033,1034,1024,2097";
            List<Store> storeList = new ArrayList<>();
            for (String u : storeNums.split(",")) {
                Store store = new Store();
                store.setStoresNumber(u);
                storeList.add(store);
            }
            List<GoodsInfo> goodsInfoList = new ArrayList<>();

//            List<String> goodCodeList = goodsMapper.getGoodCodeBySiteId(siteId, 1).subList(0, 2);
            for (int i = 0; i < goodCodeList.size(); i++) {
                GoodsInfo goodsInfo = new GoodsInfo();
                goodsInfo.setGoodsCode(goodCodeList.get(i));
                goodsInfo.setControlNum(1);
                goodsInfoList.add(goodsInfo);
            }
          /*  Long startTime = System.currentTimeMillis();
            List<Store> list = storageService.getStoresHasStorageList(siteId, storeList, goodsInfoList);
            Long endTime = System.currentTimeMillis();
            StringBuffer uidA = new StringBuffer("");
            list.stream().forEach(store -> {
                uidA.append(store.getStoresNumber() + ",");
            });
            logger.info("优化后匹配耗时:{}符合条件的门店{}", endTime - startTime, uidA);*/
            Long startTime1 = System.currentTimeMillis();
            List<Store> list1 = orderService.getBestStorageStoreFromERP(siteId, storeList, goodsInfoList);
            Long endTime1 = System.currentTimeMillis();
            StringBuffer uidB = new StringBuffer("");
            list1.stream().forEach(store -> {
                uidB.append(store.getStoresNumber() + ",");
            });
            logger.info("优化前匹配耗时:{}符合条件的门店{}", endTime1 - startTime1, uidB);
        } catch (Exception e) {
            logger.info("出问题啦{}", e);
        }
    }

    @Test
    public void pinganOrder() {
        try {
            Trades trades = tradesService.getTradesDetial(1001901539315480543l);
            offlineOrderService.pushOrder_pingAN(100190, trades,0);
        } catch (Exception e) {
            logger.info("平安订单推送有问题{}；", e);
        }
    }
}
