package com.jk51.modules.erpprice.service;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.erpprice.BGoodsErp;
import com.jk51.modules.erpprice.mapper.BGoodsErpMapper;
import com.jk51.modules.goods.library.BatchResult;
import com.jk51.modules.goods.library.GoodsImportProperty;
import com.jk51.modules.goods.library.GoodsImportXlsHelper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.request.BatchImportDto;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.merchant.mapper.MerchantExtMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron
 * 创建日期: 2017-10-27 16:28
 * 修改记录:
 */
@Service
public class ErpPriceImportService {

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
    SyncService syncService;

    public BatchResult updateErpPriceForStore(BatchImportDto batchImportDto) {

        Long startTimeMillis = System.currentTimeMillis();

        BatchResult batchResult = new BatchResult();

        GoodsImportXlsHelper goodsImportXlsHelper = new GoodsImportXlsHelper();

        //获取数据
        goodsImportXlsHelper.open(batchImportDto.getFileurl());
        //检查xls数据是否符合规范
        goodsImportXlsHelper.checkFileFromStorage(batchImportDto.getDetailTpl(), batchImportDto.getOption());

        Sheet sheet = goodsImportXlsHelper.getSheetList().get(0);

        //单个表格重复匹配，用成员变量了
        Map<String, Object> priceCodeCache = new ConcurrentHashMap();

        if (sheet.getLastRowNum() > 0) {

            //将rows解析成Map型数据
            List<Map<String, String>> rows = goodsImportXlsHelper.mapSkipTitle(sheet, row -> {
                try {
                    Map<String, String> rowMap = handleRow(row, batchImportDto);
                    rowMap.put("rowNum", row.getRowNum() + "");
                    return rowMap;
                } catch (Exception e) {
                    return null;
                }
            }, true)
                .filter(row -> row != null)
                .collect(ArrayList<Map<String, String>>::new,
                    ArrayList<Map<String, String>>::add,
                    ArrayList<Map<String, String>>::addAll);

            if (rows.size() > 2000) {
                batchResult.setFailNum(sheet.getLastRowNum());
                batchResult.setErrorList(new ArrayList<Map<String, String>>() {{
                    add(new HashMap<String, String>() {{
                        put("rowNum", "1");
                        put("reason", "2000");
                        put("sheetName", sheet.getSheetName());
                    }});
                }});
                return batchResult;
            }

            //提取goodsCodes
            Set<String> goodsCodes = list2Set(rows, row -> row.get("goods_code"));

            //创建code 2 id映射
            Map<String, Integer> goodCode2Id = new ConcurrentHashMap<>();
            List<Map<String, Object>> goodsDatas = goodsMapper.getGoodsInfoByGoodsCodes(batchImportDto.getSiteId(), goodsCodes);
            goodsDatas.forEach(goodsData -> goodCode2Id.put(goodsData.get("goodsCode").toString(), Integer.parseInt(goodsData.get("goodsId").toString())));

            Map<String, Integer> storeNumber2Id = new ConcurrentHashMap<>();
            List<Map<String, Object>> storeDatas = storesMapper.getStoreIdBySiteId(batchImportDto.getSiteId());
            storeDatas.forEach(storeData -> storeNumber2Id.put(storeData.get("storeNumber").toString(), Integer.parseInt(storeData.get("id").toString())));

            //创建erpPrice Id映射
            Map<String, Integer> erpPriceId = new ConcurrentHashMap<>();
            List<Map<String, Object>> currentPriceList = bGoodsErpMapper.selectErpPriceListBysiteId(batchImportDto.getSiteId());
            currentPriceList.forEach(storePriceData -> erpPriceId.put(storePriceData.get("gs").toString(), Integer.parseInt(storePriceData.get("id").toString())));
            List<Map<String, String>> errorList = new ArrayList<>();

            final AtomicInteger successNum = new AtomicInteger();
            final AtomicInteger failNum = new AtomicInteger();
            final AtomicInteger repeatMarryNum = new AtomicInteger();
            //插入数据
            List batchUpdateList = new ArrayList();
            List batchInsertList = new ArrayList();
            for (Map<String, String> currentMap : rows) {
                Map<String, String> errorMap = new HashMap<>();
                try {
                    if (storeNumber2Id.get(currentMap.get("store_number").toString().trim()) == null) {
                        failNum.incrementAndGet();
                        errorMap.put("rowNum", currentMap.get("rowNum"));
                        errorMap.put("reason", "门店编码不存在");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                        continue;
                    }
                    Integer storeId = storeNumber2Id.get(currentMap.get("store_number").toString().trim());
                    Integer goodsId = goodCode2Id.get(currentMap.get("goods_code").toString().trim());
                    String goodsCode = currentMap.get("goods_code").toString().trim();
                    Integer goodsPrice = Integer.valueOf((new BigDecimal(currentMap.get("price").toString().trim()).multiply(new BigDecimal(100)).intValue() + "").toString());
                    String storeNumber = currentMap.get("store_number").toString().trim();
                    //重复匹配
                    if (Optional.ofNullable(priceCodeCache.get(currentMap.get("goods_code").toString().trim() + "_" + currentMap.get("store_number").toString().trim())).isPresent()) {
                        repeatMarryNum.incrementAndGet();
                        errorMap.put("rowNum", currentMap.get("rowNum"));
                        errorMap.put("reason", "门店+商品价格重复导入");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                        continue;
                    } else if (StringUtil.isEmpty(goodsPrice)) {
                        failNum.incrementAndGet();
                        errorMap.put("rowNum", currentMap.get("rowNum"));
                        errorMap.put("reason", "商品价格不能为空");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                        continue;
                    } else if (StringUtil.isEmpty(goodsId)) {
                        failNum.incrementAndGet();
                        errorMap.put("rowNum", currentMap.get("rowNum"));
                        errorMap.put("reason", "商品ID不能为空");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                        continue;
                    } else if (StringUtil.isEmpty(goodsCode)) {
                        failNum.incrementAndGet();
                        errorMap.put("rowNum", currentMap.get("rowNum"));
                        errorMap.put("reason", "商品编码不能为空");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                        continue;
                    } else if (StringUtil.isEmpty(storeId)) {
                        failNum.incrementAndGet();
                        errorMap.put("rowNum", currentMap.get("rowNum"));
                        errorMap.put("reason", "门店编码不能为空");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                        continue;
                    } else if (goodsId == 0) {
                        failNum.incrementAndGet();
                        errorMap.put("rowNum", currentMap.get("rowNum"));
                        errorMap.put("reason", "商品id不能为0");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                        continue;
                    } else {
                        priceCodeCache.put(goodsCode + "_" + storeNumber, "");
                        if (erpPriceId.containsKey(goodsCode + "_" + storeNumber)) {//表中存在该数据，进行更新
                            Map<String, Object> erp = new HashedMap();
                            erp.put("id", erpPriceId.get(goodsCode + "_" + storeNumber));
                            erp.put("shop_price", goodsPrice);
                            batchUpdateList.add(erp);
                            successNum.incrementAndGet();
                        } else {//表中不存在这个数据，进行新增
                            BGoodsErp record = new BGoodsErp();
                            record.setPrice(goodsPrice);
                            record.setSiteId(batchImportDto.getSiteId());
                            record.setGoodsCode(goodsCode);
                            record.setGoodsId(goodCode2Id.get(goodsCode));
                            record.setStoreId(storeId);
                            record.setStoreNumber(storeNumber);
                            batchInsertList.add(record);
                            successNum.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Excel导入多价格erp价格异常：{}", e.getMessage());
                    failNum.incrementAndGet();
                    continue;
                }
            }
            if (batchInsertList.size() > 0) {
                bGoodsErpMapper.batchInsertErpPrice(batchInsertList);
            }
            if (batchUpdateList.size() > 0) {
                bGoodsErpMapper.batchUpdateErpPrice(batchUpdateList);
            }
            batchResult.setErrorList(errorList);
            batchResult.setSuccessNum(successNum.get());
            batchResult.setFailNum(failNum.get());
            batchResult.setRepeatMarryNum(repeatMarryNum.get());
            Long endTimeMillis = System.currentTimeMillis();
            LOGGER.info("处理{}条记录用时{}", batchResult.getSuccessNum() + batchResult.getFailNum(), endTimeMillis - startTimeMillis);
            return batchResult;
         /*   //提取goodsCodes
            Set<String> goodsCodes = list2Set(rows, row -> row.get("goods_code"));

            //创建code 2 id映射
            Map<String, Integer> goodCode2Id = new ConcurrentHashMap<>();
            List<Map<String, Object>> goodsDatas = goodsMapper.getGoodsInfoByGoodsCodes(batchImportDto.getSiteId(), goodsCodes);
            goodsDatas.forEach(goodsData -> goodCode2Id.put(goodsData.get("goodsCode").toString(), Integer.parseInt(goodsData.get("goodsId").toString())));

            Map<String, Integer> storeNumber2Id = new ConcurrentHashMap<>();
            List<Map<String, Object>> storeDatas = storesMapper.getStoreIdBySiteId(batchImportDto.getSiteId());
            storeDatas.forEach(storeData -> storeNumber2Id.put(storeData.get("storeNumber").toString(), Integer.parseInt(storeData.get("id").toString())));

            List<Map<String, String>> errorList = new ArrayList<>();

            final AtomicInteger successNum = new AtomicInteger();
            final AtomicInteger failNum = new AtomicInteger();
            final AtomicInteger repeatMarryNum = new AtomicInteger();

            //插入数据
            rows.forEach(row -> {
                Map<String, String> errorMap = new HashMap<>();
                try {
                    Objects.requireNonNull(row, "表格原始数据处理失败");

                    if (storeNumber2Id.get(row.get("store_number")) == null) {
                        failNum.incrementAndGet();
                        errorMap.put("rowNum", row.get("rowNum"));
                        errorMap.put("reason", "门店编码不存在");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                        return;
                    }
                    Integer siteId = batchImportDto.getSiteId();
                    Integer storeId = storeNumber2Id.get(row.get("store_number"));
                    Integer goodsId = goodCode2Id.get(row.get("goods_code"));
                    String goodsCode = row.get("goods_code").toString();
                    Integer goodsPrice = Integer.valueOf((new BigDecimal(row.get("price")).multiply(new BigDecimal(100)).intValue() + "").toString());
                    String storeNumber = row.get("store_number");
                    //重复匹配
                    if (Optional.ofNullable(priceCodeCache.get(row.get("goods_code") + "_" + row.get("store_number"))).isPresent()) {
                        repeatMarryNum.incrementAndGet();
                        errorMap.put("rowNum", row.get("rowNum"));
                        errorMap.put("reason", "商品编码重复");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                    } else if (StringUtil.isEmpty(goodsPrice)) {
                        failNum.incrementAndGet();
                        errorMap.put("rowNum", row.get("rowNum"));
                        errorMap.put("reason", "商品价格不能为空");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                    } else if (StringUtil.isEmpty(goodsCode)) {
                        failNum.incrementAndGet();
                        errorMap.put("rowNum", row.get("rowNum"));
                        errorMap.put("reason", "商品编码不能为空");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                    } else if (StringUtil.isEmpty(goodsId)) {
                        failNum.incrementAndGet();
                        errorMap.put("rowNum", row.get("rowNum"));
                        errorMap.put("reason", "商品ID不能为空");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                    } else if (goodsId == 0) {
                        failNum.incrementAndGet();
                        errorMap.put("rowNum", row.get("rowNum"));
                        errorMap.put("reason", "商品ID不能为0");
                        errorMap.put("sheetName", sheet.getSheetName());
                        errorList.add(errorMap);
                    } else {
                        if (!gcodes.contains(goodsCode)) {
                            gcodes.add(goodsCode);
                        }
                        priceCodeCache.put(goodsCode + "_" + storeNumber, "");
                        if (bGoodsErpMapper.updateERPPrice(siteId, storeId, goodsCode, goodsPrice, storeNumber) > 0) {
                            successNum.incrementAndGet();
                        } else {
                            BGoodsErp record = new BGoodsErp();
                            record.setPrice(goodsPrice);
                            record.setSiteId(siteId);
                            record.setGoodsCode(goodsCode);
                            record.setGoodsId(goodCode2Id.get(goodsCode));
                            record.setStoreId(storeId);
                            record.setStoreNumber(storeNumber);
                            if (bGoodsErpMapper.insert(record) > 0) {
                                successNum.incrementAndGet();
                            } else {
                                failNum.incrementAndGet();
                                errorMap.put("rowNum", row.get("rowNum"));
                                errorMap.put("reason", "插入失败");
                                errorMap.put("sheetName", sheet.getSheetName());
                                errorList.add(errorMap);
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.info("导入erp价格异常：{}", e);
                    failNum.incrementAndGet();
                    errorMap.put("rowNum", row.get("rowNum"));
                    errorMap.put("reason", "处理失败");
                    errorMap.put("sheetName", sheet.getSheetName());
                    errorList.add(errorMap);
                }
            });
            batchResult.setErrorList(errorList);
            batchResult.setSuccessNum(successNum.get());
            batchResult.setFailNum(failNum.get());
            batchResult.setRepeatMarryNum(repeatMarryNum.get());
            Long endTimeMillis = System.currentTimeMillis();
            LOGGER.info("处理{}条记录用时{}", batchResult.getSuccessNum() + batchResult.getFailNum(), endTimeMillis - startTimeMillis);
            return batchResult;*/
        }
        return null;
    }

    private <T, R> Set<R> list2Set(List<T> list, Function<T, R> fun) {
        return list.parallelStream().map(fun).collect(Collectors.toSet());
    }

    //将每条记录转换成对象
    private Map<String, String> handleRow(Row row, BatchImportDto batchImportDto) throws Exception {
        Map<String, String> erpPriceInfo = GoodsImportProperty.getErpPrice(row, batchImportDto.getDetailTpl());
//        erpPriceCheckRequire(erpPriceInfo);
        return erpPriceInfo;
    }

    public static void erpPriceCheckRequire(Map<String, String> erpPriceInfo) {
        if (StringUtil.isBlank(erpPriceInfo.get("goods_code"))) {
            throw new RuntimeException("请填写商品编码");
        }
        if (StringUtil.isBlank(erpPriceInfo.get("store_number"))) {
            throw new RuntimeException("请填写门店编码");
        }
        if (StringUtil.isBlank(erpPriceInfo.get("price")) || Double.parseDouble(erpPriceInfo.get("price").toString()) == 0.00d) {
            throw new RuntimeException("请填写商品价格");
        }
    }

    //同步商品门店价格
    public Map<String, Object> storeSyncPrice(Map<String, Object> params) {
        List goodsList = new ArrayList<>();
        Integer siteId = 0;
        Map<String, Object> objectMap = new HashMap<>();
        LOGGER.info("更新商品价格接口参数{}", params.toString());
        if (StringUtil.isEmpty(params.get("siteId"))) {
            LOGGER.info("Method{}，商家站点为空", "updateGoodsPrice");
            objectMap.put("code", -1);
            objectMap.put("msg", "通讯异常，请稍后重试");
        } else if (StringUtil.isEmpty(params.get("goodlist"))) {
            LOGGER.info("Method{},商品信息为空", "storeSyncPrice");
            objectMap.put("code", -1);
            objectMap.put("msg", "需要修改的商品信息不能为空");
        } else {
            siteId = Integer.parseInt(params.get("siteId").toString());
            try {
                goodsList = JSONArray.parseArray(JacksonUtils.obj2json(params.get("goodlist")), Map.class);
            } catch (Exception e) {
                LOGGER.info("类型转换异常{}", e);
                objectMap.put("code", -1);
                objectMap.put("msg", "商品信息异常");
                return objectMap;
            }
        }
        Long startTimeMillis = System.currentTimeMillis();

        //单个表格重复匹配，用成员变量了
        Map<String, Object> priceCodeCache = new ConcurrentHashMap();

        //提取goodsCodes
        Set<String> goodsCodes = list2Set(goodsList, map -> ((Map<String, Object>) map).get("goods_code").toString());

        //创建code 2 id映射
        Map<String, Integer> goodCode2Id = new ConcurrentHashMap<>();
        List<Map<String, Object>> goodsDatas = goodsMapper.getGoodsInfoByGoodsCodes(siteId, goodsCodes);
        goodsDatas.forEach(goodsData -> goodCode2Id.put(goodsData.get("goodsCode").toString(), Integer.parseInt(goodsData.get("goodsId").toString())));

        Map<String, Integer> storeNumber2Id = new ConcurrentHashMap<>();
        List<Map<String, Object>> storeDatas = storesMapper.getStoreIdBySiteId(siteId);
        storeDatas.forEach(storeData -> storeNumber2Id.put(storeData.get("storeNumber").toString(), Integer.parseInt(storeData.get("id").toString())));

        //创建erpPrice Id映射
        Map<String, Integer> erpPriceId = new ConcurrentHashMap<>();
//        List<Map<String, Object>> currentPriceList = bGoodsErpMapper.selectErpPriceList(params);
        List<Map<String, Object>> currentPriceList = bGoodsErpMapper.selectErpPriceListBysiteId(siteId);
        currentPriceList.forEach(storePriceData -> erpPriceId.put(storePriceData.get("gs").toString(), Integer.parseInt(storePriceData.get("id").toString())));
        final AtomicInteger successNum = new AtomicInteger();
        final AtomicInteger failNum = new AtomicInteger();
        final AtomicInteger repeatMarryNum = new AtomicInteger();
        //插入数据
        List batchUpdateList = new ArrayList();
        List batchInsertList = new ArrayList();
        for (Object row : goodsList) {
            Map<String, Object> currentMap = (Map<String, Object>) row;
            try {
                if (storeNumber2Id.get(currentMap.get("store_number").toString().trim()) == null) {
                    failNum.incrementAndGet();
                    continue;
                }
                Integer storeId = storeNumber2Id.get(currentMap.get("store_number").toString().trim());
                Integer goodsId = goodCode2Id.get(currentMap.get("goods_code").toString().trim());
                String goodsCode = currentMap.get("goods_code").toString().trim();
                Integer goodsPrice = Integer.valueOf((new BigDecimal(currentMap.get("shop_price").toString().trim()).multiply(new BigDecimal(100)).intValue() + "").toString());
                String storeNumber = currentMap.get("store_number").toString().trim();
                //重复匹配
                if (Optional.ofNullable(priceCodeCache.get(currentMap.get("goods_code").toString().trim() + "_" + currentMap.get("store_number").toString().trim())).isPresent()) {
                    repeatMarryNum.incrementAndGet();
                    continue;
                } else if (StringUtil.isEmpty(goodsPrice)) {
                    failNum.incrementAndGet();
                    continue;
                } else if (StringUtil.isEmpty(goodsId)) {
                    failNum.incrementAndGet();
                    continue;
                } else if (StringUtil.isEmpty(storeId)) {
                    failNum.incrementAndGet();
                    continue;
                } else if (goodsId == 0) {
                    failNum.incrementAndGet();
                    continue;
                } else {
                    priceCodeCache.put(goodsCode + "_" + storeNumber, "");
                    if (erpPriceId.containsKey(goodsCode + "_" + storeNumber)) {//表中存在该数据，进行更新
                        Map<String, Object> erp = new HashedMap();
                        erp.put("id", erpPriceId.get(goodsCode + "_" + storeNumber));
                        erp.put("shop_price", goodsPrice);
                        batchUpdateList.add(erp);
                        successNum.incrementAndGet();
                    } else {//表中不存在这个数据，进行新增
                        BGoodsErp record = new BGoodsErp();
                        record.setPrice(goodsPrice);
                        record.setSiteId(siteId);
                        record.setGoodsCode(goodsCode);
                        record.setGoodsId(goodCode2Id.get(goodsCode));
                        record.setStoreId(storeId);
                        record.setStoreNumber(storeNumber);
                        batchInsertList.add(record);
                        successNum.incrementAndGet();
                    }
                }
            } catch (Exception e) {
                LOGGER.error("接口导入多价格erp价格异常：{}", e.getMessage());
                failNum.incrementAndGet();
                continue;
            }
        }
        if (batchInsertList.size() > 0) {
            bGoodsErpMapper.batchInsertErpPrice(batchInsertList);
        }
        if (batchUpdateList.size() > 0) {
            bGoodsErpMapper.batchUpdateErpPrice(batchUpdateList);
        }
        Long endTimeMillis = System.currentTimeMillis();
        LOGGER.info("Erp接口传递多价格数据：siteId:{},传入{}条，处理{}条，重复数据{}条，" +
                "成功{}条，插入{}条，更新{}条，记录用时{}",
            siteId, goodsList.size(), successNum.get() + failNum.get(), repeatMarryNum.get(),
            successNum.get(), batchInsertList.size(), batchUpdateList.size(), endTimeMillis - startTimeMillis);
        objectMap.put("code", 0);
        objectMap.put("msg", "处理数据成功");
        return objectMap;
    }
}


