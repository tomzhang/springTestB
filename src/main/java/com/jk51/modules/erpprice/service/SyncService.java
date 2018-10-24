package com.jk51.modules.erpprice.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.erpprice.ErpPriceSetting;
import com.jk51.model.goods.YbStoresGoodsPrice;
import com.jk51.modules.erpprice.mapper.BGoodsErpMapper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.merchant.mapper.MerchantExtMapper;
import com.jk51.modules.persistence.mapper.SYbStoresGoodsPriceMapper;
import com.jk51.modules.store.service.SGoodsPriceService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-03-08
 * 修改记录:
 */
@Component
public class SyncService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ErpPriceImportService.class);

    @Autowired
    BGoodsErpMapper bGoodsErpMapper;
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    StoresMapper storesMapper;
    @Autowired
    MerchantExtMapper merchantExtMapper;
    @Autowired
    private ErpPriceSetService erpPriceSetService;
    @Autowired
    SYbStoresGoodsPriceMapper storesGoodsPriceMapper;
    @Autowired
    SGoodsPriceService goodsPriceService;

    /**
     * 进行同步操作，根据多价格设置规则将门店价格配置到门店列表中
     *
     * @param siteId
     */
    @Async
    public void syncStorePrice(Integer siteId) {
        //1:读取当前站点是否存在价格设置
        Map<String, Object> erpPriceSetting = new HashMap<>();
        try {
            erpPriceSetting = erpPriceSetService.getSettingDetailBySiteId(siteId);
        } catch (Exception e) {
            LOGGER.info("将多价格同步到门店后台出错" + e.getMessage());
            return;
        }
        if (erpPriceSetting == null || StringUtil.isEmpty(erpPriceSetting.get("type")) || "-1".equals(erpPriceSetting.get("type").toString())) {
            LOGGER.info("多价格设置不存在，不同步");
            return;
        }
        if (StringUtil.isEmpty(erpPriceSetting.get("data"))) {
            LOGGER.info("将多价格同步设置规则为空");
            return;
        }
        Map<String, Object> setting = new HashMap<>();
        try {
            setting = JacksonUtils.json2map(JacksonUtils.obj2json(erpPriceSetting.get("data")));//规则转换
        } catch (Exception e) {
            LOGGER.info("规则转换异常");
            return;
        }
        Map<String, Object> goodMap = new HashMap<>();//已存在的商品价格
        List<Map<String, Object>> goodsList = goodsMapper.selectGoodsListBysiteId2(siteId, null);
        for (Map<String, Object> good : goodsList) {
            goodMap.put(siteId + "_" + good.get("goods_id").toString(), good.get("shop_price"));
        }
        //包括该门店价格已禁用的情况
        Map<String, Object> exitedStorePriceMap = new HashMap<>();//已存在的门店商品价格
        List<Map<String, String>> ybStoresGoodsPriceList = storesGoodsPriceMapper.findList(null, siteId, null, null);
        if (ybStoresGoodsPriceList.size() > 0) {
            for (Map<String, String> storesGoodsPrice : ybStoresGoodsPriceList) {
                exitedStorePriceMap.put(String.valueOf(storesGoodsPrice.get("store_id")) + "_" + String.valueOf(storesGoodsPrice.get("goods_id")),
                    String.valueOf(storesGoodsPrice.get("id")));
            }
        }
        List<YbStoresGoodsPrice> insertStorePriceList = new ArrayList<>();//需要新增的价格数据
        List<Map<String, Object>> updateStorePriceList = new ArrayList<>();//需要更改的价格数据
        //根据不同的设置同步不同的价格体系,只同步优先级第一的价格列表
        switch (erpPriceSetting.get("type").toString()) {
            case "10"://总部基础价
                try {
                    List<ErpPriceSetting> erpSettingPOs_10 = JacksonUtils.json2list(JacksonUtils.obj2json(setting.get("0")),
                        ErpPriceSetting.class);//只同步优先级最高的门店价格
                    if (CollectionUtils.isEmpty(erpSettingPOs_10)) {
                        break;
                    }
                    List<Map<String, Object>> erpPriceList = bGoodsErpMapper.erpPriceList(new HashMap<String, Object>() {
                        {
                            put("siteId", siteId);
                            put("storeId", erpSettingPOs_10.get(0).getStoreId());
                        }
                    });//获取当前门店数据并查询当前门店的商品价格
                    List<Map<String, Object>> storeMapList = storesMapper.selectStoreByAreaIds(siteId, null, null);
                    for (Map<String, Object> storeMap : storeMapList) {
                        for (Map<String, Object> erpPrice : erpPriceList) {//所有商品价格都更改为同一门店
                            if (!exitedStorePriceMap.containsKey(storeMap.get("id").toString() + "_" + erpPrice.get("goods_id").toString())) {//当前门店下是否包含该商品会员价格
                                YbStoresGoodsPrice ybStoresGoodsPricenew = new YbStoresGoodsPrice();
                                ybStoresGoodsPricenew.setGoodsId(Integer.parseInt(erpPrice.get("goods_id").toString()));
                                ybStoresGoodsPricenew.setSiteId(siteId);
                                ybStoresGoodsPricenew.setStoreId(Integer.parseInt(storeMap.get("id").toString()));
                                ybStoresGoodsPricenew.setGoodsPrice(Integer.parseInt(goodMap.get(siteId + "_" + erpPrice.get("goods_id").toString()).toString()));
                                ybStoresGoodsPricenew.setDiscountPrice(Integer.parseInt(erpPrice.get("price").toString()));
                                insertStorePriceList.add(ybStoresGoodsPricenew);
                            } else {
                                updateStorePriceList.add(new HashMap<String, Object>() {{
                                    put("goodsPrice", Integer.parseInt(erpPrice.get("price").toString()));
                                    put("id", exitedStorePriceMap.get(storeMap.get("id") + "_" + erpPrice.get("goods_id")));
                                }});
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.info("规则转换异常" + e.getMessage());
                }
                break;
            case "20"://市级价
                for (Map.Entry<String, Object> entry : setting.entrySet()) {//循环每个城市的定价规则进行修改
                    try {
                        Integer cityId = Integer.parseInt(entry.getKey().toString());//当前的市级areaId
                        List<ErpPriceSetting> erpSettingPOs_20 = JacksonUtils.json2list(JacksonUtils.obj2json(entry.getValue()),
                            ErpPriceSetting.class);//只同步优先级最高的门店价格
                        if (CollectionUtils.isEmpty(erpSettingPOs_20)) {
                            break;
                        }
                        List<Map<String, Object>> erpPriceList_20 = bGoodsErpMapper.erpPriceList(new HashMap<String, Object>() {
                            {
                                put("siteId", siteId);
                                put("storeId", erpSettingPOs_20.get(0).getStoreId());
                            }
                        });//获取当前门店数据并查询当前门店的商品价格
                        List<Map<String, Object>> storeMapList = storesMapper.selectStoreByAreaIds(siteId, cityId, null);
                        for (Map<String, Object> erpPrice : erpPriceList_20) {//所有商品价格都更改为同一门店
                            for (Map<String, Object> storeMap : storeMapList) {
                                if (!exitedStorePriceMap.containsKey(storeMap.get("id") + "_" + erpPrice.get("goods_id"))) {//当前门店下是否包含该商品会员价格
                                    YbStoresGoodsPrice ybStoresGoodsPricenew = new YbStoresGoodsPrice();
                                    ybStoresGoodsPricenew.setGoodsId(Integer.parseInt(erpPrice.get("goods_id").toString()));
                                    ybStoresGoodsPricenew.setSiteId(siteId);
                                    ybStoresGoodsPricenew.setStoreId(Integer.parseInt(storeMap.get("id").toString()));
                                    ybStoresGoodsPricenew.setGoodsPrice(Integer.parseInt(goodMap.get(siteId + "_" + erpPrice.get("goods_id").toString()).toString()));
                                    ybStoresGoodsPricenew.setDiscountPrice(Integer.parseInt(erpPrice.get("price").toString()));
                                    insertStorePriceList.add(ybStoresGoodsPricenew);
                                } else {
                                    updateStorePriceList.add(new HashMap<String, Object>() {{
                                        put("goodsPrice", Integer.parseInt(erpPrice.get("price").toString()));
                                        put("id", exitedStorePriceMap.get(storeMap.get("id") + "_" + erpPrice.get("goods_id")));
                                    }});
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.info("规则转换异常" + e.getMessage());
                    }
                }
                break;
            case "30"://区级价
                for (Map.Entry<String, Object> entry : setting.entrySet()) {//循环每个区的定价规则进行修改
                    try {
                        Integer regionId = Integer.parseInt(entry.getKey().toString());//当前的区级areaId
                        List<ErpPriceSetting> erpSettingPOs_30 = JacksonUtils.json2list(JacksonUtils.obj2json(entry.getValue()),
                            ErpPriceSetting.class);//只同步优先级最高的门店价格
                        if (CollectionUtils.isEmpty(erpSettingPOs_30)) {
                            break;
                        }
                        List<Map<String, Object>> erpPriceList_30 = bGoodsErpMapper.erpPriceList(new HashMap<String, Object>() {
                            {
                                put("siteId", siteId);
                                put("storeId", erpSettingPOs_30.get(0).getStoreId());
                            }
                        });//获取当前门店数据并查询当前门店的商品价格
                        List<Map<String, Object>> storeMapList = storesMapper.selectStoreByAreaIds(siteId, null, regionId);
                        for (Map<String, Object> erpPrice : erpPriceList_30) {//所有商品价格都更改为同一门店
                            for (Map<String, Object> storeMap : storeMapList) {
                                if (!exitedStorePriceMap.containsKey(storeMap.get("id") + "_" + erpPrice.get("goods_id"))) {//当前门店下是否包含该商品会员价格
                                    YbStoresGoodsPrice ybStoresGoodsPricenew = new YbStoresGoodsPrice();
                                    ybStoresGoodsPricenew.setGoodsId(Integer.parseInt(erpPrice.get("goods_id").toString()));
                                    ybStoresGoodsPricenew.setSiteId(siteId);
                                    ybStoresGoodsPricenew.setStoreId(Integer.parseInt(storeMap.get("id").toString()));
                                    ybStoresGoodsPricenew.setGoodsPrice(Integer.parseInt(goodMap.get(siteId + "_" + erpPrice.get("goods_id").toString()).toString()));
                                    ybStoresGoodsPricenew.setDiscountPrice(Integer.parseInt(erpPrice.get("price").toString()));
                                    insertStorePriceList.add(ybStoresGoodsPricenew);
                                } else {
                                    updateStorePriceList.add(new HashMap<String, Object>() {{
                                        put("goodsPrice", Integer.parseInt(erpPrice.get("price").toString()));
                                        put("id", exitedStorePriceMap.get(storeMap.get("id") + "_" + erpPrice.get("goods_id")));
                                    }});
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.info("规则转换异常" + e.getMessage());
                    }
                }
                break;
            case "40"://门店价
                List<Map<String, Object>> erpPriceList_40 = bGoodsErpMapper.erpPriceList(new HashMap<String, Object>() {
                    {
                        put("siteId", siteId);
                    }
                });//获取当前门店数据并查询当前门店的商品价格
                for (Map<String, Object> erpPrice : erpPriceList_40) {
                    if (!exitedStorePriceMap.containsKey(erpPrice.get("store_id") + "_" + erpPrice.get("goods_id"))) {//当前门店下是否包含该商品会员价格
                        YbStoresGoodsPrice ybStoresGoodsPricenew = new YbStoresGoodsPrice();
                        ybStoresGoodsPricenew.setGoodsId(Integer.parseInt(erpPrice.get("goods_id").toString()));
                        ybStoresGoodsPricenew.setSiteId(siteId);
                        ybStoresGoodsPricenew.setStoreId(Integer.parseInt(erpPrice.get("store_id").toString()));
                        ybStoresGoodsPricenew.setGoodsPrice(Integer.parseInt(goodMap.get(siteId + "_" + erpPrice.get("goods_id").toString()).toString()));
                        ybStoresGoodsPricenew.setDiscountPrice(Integer.parseInt(erpPrice.get("price").toString()));
                        insertStorePriceList.add(ybStoresGoodsPricenew);
                    } else {
                        updateStorePriceList.add(new HashMap<String, Object>() {{
                            put("goodsPrice", Integer.parseInt(erpPrice.get("price").toString()));
                            put("id", exitedStorePriceMap.get(erpPrice.get("store_id") + "_" + erpPrice.get("goods_id")));
                        }});
                    }
                }
                break;
            default://不同步价格
                break;
        }
        if (insertStorePriceList.size() > 0) {
            int num = insertStorePriceList.size() / 1000 + 1;//可细分为若干个小数组
            for (int i = 0; i < num; i++) {
                List<YbStoresGoodsPrice> storePriceList = new ArrayList<>();
                for (int j = 0; j < 1000; j++) {
                    if (insertStorePriceList.size() > j + i * 1000) {
                        storePriceList.add(insertStorePriceList.get(j + i * 1000));
                        continue;
                    } else {
                        break;
                    }
                }
                try {
                    storesGoodsPriceMapper.batchInsertYBPrice(storePriceList);
                } catch (Exception e) {
                    LOGGER.info("批量更新多价格数据错误。" + e.getMessage());
                }
            }
        }
        if (updateStorePriceList.size() > 0) {
            int num = updateStorePriceList.size() / 1000 + 1;//可细分为若干个小数组
            for (int i = 0; i < num; i++) {
                List<Map<String, Object>> storePriceList = new ArrayList<>();
                for (int j = 0; j < 1000; j++) {
                    if (updateStorePriceList.size() > j + i * 1000) {
                        storePriceList.add(updateStorePriceList.get(j + i * 1000));
                        continue;
                    } else {
                        break;
                    }
                }
                storesGoodsPriceMapper.batchupdateYBPrice(storePriceList);
            }
        }
    }

    /**
     * 进行同步操作，根据多价格设置规则将门店价格配置到门店列表中
     * 只同步需要同步的价格数据
     *
     * @param siteId
     */
    @Async
    public void syncStorePriceFromApp(Integer siteId, List<String> gCodes) {
        if (CollectionUtils.isEmpty(gCodes)) {
            return;
        }
        //1:读取当前站点是否存在价格设置
        Map<String, Object> erpPriceSetting = new HashMap<>();
        try {
            erpPriceSetting = erpPriceSetService.getSettingDetailBySiteId(siteId);
        } catch (Exception e) {
            LOGGER.info("将多价格同步到门店后台出错" + e.getMessage());
            return;
        }
        if (erpPriceSetting == null || StringUtil.isEmpty(erpPriceSetting.get("type")) || "-1".equals(erpPriceSetting.get("type").toString())) {
            LOGGER.info("多价格设置不存在，不同步");
            return;
        }
        if (StringUtil.isEmpty(erpPriceSetting.get("data"))) {
            LOGGER.info("将多价格同步设置规则为空");
            return;
        }
        Map<String, Object> setting = new HashMap<>();
        try {
            setting = JacksonUtils.json2map(JacksonUtils.obj2json(erpPriceSetting.get("data")));//规则转换
        } catch (Exception e) {
            LOGGER.info("规则转换异常");
            return;
        }
        Map<String, Object> goodMap = new HashMap<>();//已存在的商品价格
        List<Map<String, Object>> goodsList = goodsMapper.selectGoodsListBysiteId2(siteId, null);
        for (Map<String, Object> good : goodsList) {
            goodMap.put(siteId + "_" + good.get("goods_id").toString(), good.get("shop_price"));
        }
        //包括该门店价格已禁用的情况
        Map<String, Object> exitedStorePriceMap = new HashMap<>();//已存在的门店商品价格
        List<Map<String, String>> ybStoresGoodsPriceList = storesGoodsPriceMapper.findList(null, siteId, null, null);
        if (ybStoresGoodsPriceList.size() > 0) {
            for (Map<String, String> storesGoodsPrice : ybStoresGoodsPriceList) {
                exitedStorePriceMap.put(String.valueOf(storesGoodsPrice.get("store_id")) + "_" + String.valueOf(storesGoodsPrice.get("goods_id")),
                    String.valueOf(storesGoodsPrice.get("id")));
            }
        }
        List<YbStoresGoodsPrice> insertStorePriceList = new ArrayList<>();//需要新增的价格数据
        List<Map<String, Object>> updateStorePriceList = new ArrayList<>();//需要更改的价格数据
        //根据不同的设置同步不同的价格体系,只同步优先级第一的价格列表
        switch (erpPriceSetting.get("type").toString()) {
            case "10"://总部基础价
                try {
                    List<ErpPriceSetting> erpSettingPOs_10 = JacksonUtils.json2list(JacksonUtils.obj2json(setting.get("0")),
                        ErpPriceSetting.class);//只同步优先级最高的门店价格
                    if (CollectionUtils.isEmpty(erpSettingPOs_10)) {
                        break;
                    }
                    List<Map<String, Object>> erpPriceList = bGoodsErpMapper.erpPriceList2(new HashMap<String, Object>() {
                        {
                            put("siteId", siteId);
                            put("storeId", erpSettingPOs_10.get(0).getStoreId());
                            put("goodCodes", gCodes);
                        }
                    });//获取当前门店数据并查询当前门店的商品价格
                    List<Map<String, Object>> storeMapList = storesMapper.selectStoreByAreaIds(siteId, null, null);
                    for (Map<String, Object> storeMap : storeMapList) {
                        for (Map<String, Object> erpPrice : erpPriceList) {//所有商品价格都更改为同一门店
                            if (!exitedStorePriceMap.containsKey(storeMap.get("id").toString() + "_" + erpPrice.get("goods_id").toString())) {//当前门店下是否包含该商品会员价格
                                YbStoresGoodsPrice ybStoresGoodsPricenew = new YbStoresGoodsPrice();
                                ybStoresGoodsPricenew.setGoodsId(Integer.parseInt(erpPrice.get("goods_id").toString()));
                                ybStoresGoodsPricenew.setSiteId(siteId);
                                ybStoresGoodsPricenew.setStoreId(Integer.parseInt(storeMap.get("id").toString()));
                                ybStoresGoodsPricenew.setGoodsPrice(Integer.parseInt(goodMap.get(siteId + "_" + erpPrice.get("goods_id").toString()).toString()));
                                ybStoresGoodsPricenew.setDiscountPrice(Integer.parseInt(erpPrice.get("price").toString()));
                                insertStorePriceList.add(ybStoresGoodsPricenew);
                            } else {
                                updateStorePriceList.add(new HashMap<String, Object>() {{
                                    put("goodsPrice", Integer.parseInt(erpPrice.get("price").toString()));
                                    put("id", exitedStorePriceMap.get(storeMap.get("id") + "_" + erpPrice.get("goods_id")));
                                }});
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.info("规则转换异常" + e.getMessage());
                }
                break;
            case "20"://市级价
                for (Map.Entry<String, Object> entry : setting.entrySet()) {//循环每个城市的定价规则进行修改
                    try {
                        Integer cityId = Integer.parseInt(entry.getKey().toString());//当前的市级areaId
                        List<ErpPriceSetting> erpSettingPOs_20 = JacksonUtils.json2list(JacksonUtils.obj2json(entry.getValue()),
                            ErpPriceSetting.class);//只同步优先级最高的门店价格
                        if (CollectionUtils.isEmpty(erpSettingPOs_20)) {
                            break;
                        }
                        List<Map<String, Object>> erpPriceList_20 = bGoodsErpMapper.erpPriceList2(new HashMap<String, Object>() {
                            {
                                put("siteId", siteId);
                                put("storeId", erpSettingPOs_20.get(0).getStoreId());
                                put("goodCodes", gCodes);
                            }
                        });//获取当前门店数据并查询当前门店的商品价格
                        List<Map<String, Object>> storeMapList = storesMapper.selectStoreByAreaIds(siteId, cityId, null);
                        for (Map<String, Object> erpPrice : erpPriceList_20) {//所有商品价格都更改为同一门店
                            for (Map<String, Object> storeMap : storeMapList) {
                                if (!exitedStorePriceMap.containsKey(storeMap.get("id") + "_" + erpPrice.get("goods_id"))) {//当前门店下是否包含该商品会员价格
                                    YbStoresGoodsPrice ybStoresGoodsPricenew = new YbStoresGoodsPrice();
                                    ybStoresGoodsPricenew.setGoodsId(Integer.parseInt(erpPrice.get("goods_id").toString()));
                                    ybStoresGoodsPricenew.setSiteId(siteId);
                                    ybStoresGoodsPricenew.setStoreId(Integer.parseInt(storeMap.get("id").toString()));
                                    ybStoresGoodsPricenew.setGoodsPrice(Integer.parseInt(goodMap.get(siteId + "_" + erpPrice.get("goods_id").toString()).toString()));
                                    ybStoresGoodsPricenew.setDiscountPrice(Integer.parseInt(erpPrice.get("price").toString()));
                                    insertStorePriceList.add(ybStoresGoodsPricenew);
                                } else {
                                    updateStorePriceList.add(new HashMap<String, Object>() {{
                                        put("goodsPrice", Integer.parseInt(erpPrice.get("price").toString()));
                                        put("id", exitedStorePriceMap.get(storeMap.get("id") + "_" + erpPrice.get("goods_id")));
                                    }});
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.info("规则转换异常" + e.getMessage());
                    }
                }
                break;
            case "30"://区级价
                for (Map.Entry<String, Object> entry : setting.entrySet()) {//循环每个区的定价规则进行修改
                    try {
                        Integer regionId = Integer.parseInt(entry.getKey().toString());//当前的区级areaId
                        List<ErpPriceSetting> erpSettingPOs_30 = JacksonUtils.json2list(JacksonUtils.obj2json(entry.getValue()),
                            ErpPriceSetting.class);//只同步优先级最高的门店价格
                        if (CollectionUtils.isEmpty(erpSettingPOs_30)) {
                            break;
                        }
                        List<Map<String, Object>> erpPriceList_30 = bGoodsErpMapper.erpPriceList2(new HashMap<String, Object>() {
                            {
                                put("siteId", siteId);
                                put("storeId", erpSettingPOs_30.get(0).getStoreId());
                                put("goodCodes", gCodes);
                            }
                        });//获取当前门店数据并查询当前门店的商品价格
                        List<Map<String, Object>> storeMapList = storesMapper.selectStoreByAreaIds(siteId, null, regionId);
                        for (Map<String, Object> erpPrice : erpPriceList_30) {//所有商品价格都更改为同一门店
                            for (Map<String, Object> storeMap : storeMapList) {
                                if (!exitedStorePriceMap.containsKey(storeMap.get("id") + "_" + erpPrice.get("goods_id"))) {//当前门店下是否包含该商品会员价格
                                    YbStoresGoodsPrice ybStoresGoodsPricenew = new YbStoresGoodsPrice();
                                    ybStoresGoodsPricenew.setGoodsId(Integer.parseInt(erpPrice.get("goods_id").toString()));
                                    ybStoresGoodsPricenew.setSiteId(siteId);
                                    ybStoresGoodsPricenew.setStoreId(Integer.parseInt(storeMap.get("id").toString()));
                                    ybStoresGoodsPricenew.setGoodsPrice(Integer.parseInt(goodMap.get(siteId + "_" + erpPrice.get("goods_id").toString()).toString()));
                                    ybStoresGoodsPricenew.setDiscountPrice(Integer.parseInt(erpPrice.get("price").toString()));
                                    insertStorePriceList.add(ybStoresGoodsPricenew);
                                } else {
                                    updateStorePriceList.add(new HashMap<String, Object>() {{
                                        put("goodsPrice", Integer.parseInt(erpPrice.get("price").toString()));
                                        put("id", exitedStorePriceMap.get(storeMap.get("id") + "_" + erpPrice.get("goods_id")));
                                    }});
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.info("规则转换异常" + e.getMessage());
                    }
                }
                break;
            case "40"://门店价
                List<Map<String, Object>> erpPriceList_40 = bGoodsErpMapper.erpPriceList2(new HashMap<String, Object>() {
                    {
                        put("siteId", siteId);
                        put("goodCodes", gCodes);
                    }
                });//获取当前门店数据并查询当前门店的商品价格
                for (Map<String, Object> erpPrice : erpPriceList_40) {
                    if (!exitedStorePriceMap.containsKey(erpPrice.get("store_id") + "_" + erpPrice.get("goods_id"))) {//当前门店下是否包含该商品会员价格
                        YbStoresGoodsPrice ybStoresGoodsPricenew = new YbStoresGoodsPrice();
                        ybStoresGoodsPricenew.setGoodsId(Integer.parseInt(erpPrice.get("goods_id").toString()));
                        ybStoresGoodsPricenew.setSiteId(siteId);
                        ybStoresGoodsPricenew.setStoreId(Integer.parseInt(erpPrice.get("store_id").toString()));
                        ybStoresGoodsPricenew.setGoodsPrice(Integer.parseInt(goodMap.get(siteId + "_" + erpPrice.get("goods_id").toString()).toString()));
                        ybStoresGoodsPricenew.setDiscountPrice(Integer.parseInt(erpPrice.get("price").toString()));
                        insertStorePriceList.add(ybStoresGoodsPricenew);
                    } else {
                        updateStorePriceList.add(new HashMap<String, Object>() {{
                            put("goodsPrice", Integer.parseInt(erpPrice.get("price").toString()));
                            put("id", exitedStorePriceMap.get(erpPrice.get("store_id") + "_" + erpPrice.get("goods_id")));
                        }});
                    }
                }
                break;
            default://不同步价格
                break;
        }
        if (insertStorePriceList.size() > 0) {
            int num = insertStorePriceList.size() / 1000 + 1;//可细分为若干个小数组
            for (int i = 0; i < num; i++) {
                List<YbStoresGoodsPrice> storePriceList = new ArrayList<>();
                for (int j = 0; j < 1000; j++) {
                    if (insertStorePriceList.size() > j + i * 1000) {
                        storePriceList.add(insertStorePriceList.get(j + i * 1000));
                        continue;
                    } else {
                        break;
                    }
                }
                try {
                    storesGoodsPriceMapper.batchInsertYBPrice(storePriceList);
                } catch (Exception e) {
                    LOGGER.info("批量更新多价格数据错误。" + e.getMessage());
                }
            }
        }
        if (updateStorePriceList.size() > 0) {
            int num = updateStorePriceList.size() / 1000 + 1;//可细分为若干个小数组
            for (int i = 0; i < num; i++) {
                List<Map<String, Object>> storePriceList = new ArrayList<>();
                for (int j = 0; j < 1000; j++) {
                    if (updateStorePriceList.size() > j + i * 1000) {
                        storePriceList.add(updateStorePriceList.get(j + i * 1000));
                        continue;
                    } else {
                        break;
                    }
                }
                storesGoodsPriceMapper.batchupdateYBPrice(storePriceList);
            }
        }
    }
}
