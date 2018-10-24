package com.jk51.modules.offline.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.BStores;
import com.jk51.model.Goods;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.goods.library.BatchResult;
import com.jk51.modules.goods.library.GoodsImportProperty;
import com.jk51.modules.goods.library.GoodsImportXlsHelper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.request.BatchImportDto;
import com.jk51.modules.offline.mapper.BExcelTimeMapper;
import com.jk51.modules.offline.mapper.BStoresStorageMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-09-27
 * 修改记录:
 */
@Service
public class GoodsERPServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsERPServices.class);

    @Autowired
    private BStoresStorageMapper bStoresStorageMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private BExcelTimeMapper bExcelTimeMapper;
    @Autowired
    private BStoresMapper bStoresMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @param batchImportDto detailTpl：110;option（导入类型）：add:新增型；update:覆盖型
     * @return
     */
    public BatchResult importStorageFromEX(BatchImportDto batchImportDto) throws Exception {
        if (batchImportDto.getOption().equals("update")) {//覆盖型导入
            bStoresStorageMapper.updateStatus(batchImportDto.getSiteId());
        }
        BatchResult batchResult = new BatchResult();
        GoodsImportXlsHelper goodsImportXlsHelper = new GoodsImportXlsHelper();
        //获取数据
        goodsImportXlsHelper.open(batchImportDto.getFileurl());
        //检查xls数据是否符合规范
        goodsImportXlsHelper.checkFileFromStorage(batchImportDto.getDetailTpl(), batchImportDto.getOption());
        Map<String, Object> goodsMap = new HashMap<>();
        if (StringUtil.isEmpty(stringRedisTemplate.opsForValue().get(batchImportDto.getSiteId() + "_erpStorage_" + "good"))) {
            List<Map<String, Object>> goodsList = goodsMapper.selectGoodsListBysiteId(batchImportDto.getSiteId(), 1);
            for (Map<String, Object> good : goodsList) {
                goodsMap.put(GoodsImportProperty.safeToString(good.get("goods_code").toString()),
                    good.get("drug_name") + "_" + good.get("drug_category") + "_" + good.get("specif_cation") + "_");
            }
            stringRedisTemplate.opsForValue().set(batchImportDto.getSiteId() + "_erpStorage_" + "good", JacksonUtils.mapToJson(goodsMap), 1, TimeUnit.HOURS);
        } else {
            try {
                goodsMap = JacksonUtils.json2map(stringRedisTemplate.opsForValue().get(batchImportDto.getSiteId() + "_erpStorage_" + "good"));
            } catch (Exception e) {
                List<Map<String, Object>> goodsList = goodsMapper.selectGoodsListBysiteId(batchImportDto.getSiteId(), 1);
                for (Map<String, Object> good : goodsList) {
                    goodsMap.put(GoodsImportProperty.safeToString(good.get("goods_code").toString()),
                        good.get("drug_name") + "_" + good.get("drug_category") + "_" + good.get("specif_cation") + "_");
                }
            }
        }
        Map<String, Object> storeMap = new HashMap<>();//库中已保存门店数据
        if (StringUtil.isEmpty(stringRedisTemplate.opsForValue().get(batchImportDto.getSiteId() + "_erpStorage_" + "stores"))) {
            List<BStores> storesList = bStoresMapper.selectAllStoreByStatus(batchImportDto.getSiteId(), 1);
            for (BStores store : storesList) {
                storeMap.put(store.getStoresNumber(), store.getName());
            }
            stringRedisTemplate.opsForValue().set(batchImportDto.getSiteId() + "_erpStorage_" + "stores", JacksonUtils.mapToJson(storeMap), 1, TimeUnit.HOURS);
        } else {
            try {
                storeMap = JacksonUtils.json2map(stringRedisTemplate.opsForValue().get(batchImportDto.getSiteId() + "_erpStorage_" + "stores"));
            } catch (Exception e) {
                List<BStores> storesList = bStoresMapper.selectAllStoreByStatus(batchImportDto.getSiteId(), 1);
                for (BStores store : storesList) {
                    storeMap.put(store.getStoresNumber(), store.getName());
                }
            }
        }
        Map<String, Object> hasStorageMap = new HashMap<>();
        List<Map> hasStorageList = bStoresStorageMapper.selectStorageBySiteId(batchImportDto.getSiteId(), null, null, null, null, 1);
        for (Map<String, Object> hashStorage : hasStorageList) {//将数据进行处理便于匹配
            hasStorageMap.put(hashStorage.get("stores_number") + "_" + hashStorage.get("goods_code"), hashStorage.get("id"));
        }
        final AtomicInteger ai = new AtomicInteger();
        final AtomicInteger _ai = new AtomicInteger();
        final AtomicInteger marryFail_ai = new AtomicInteger();
        final AtomicInteger repeatMarry_ai = new AtomicInteger();
        Map<String, Object> goodsCodeCache = new ConcurrentHashMap();
        Long startTimeMillis = System.currentTimeMillis();
        String nowDate = DateUtils.formatDate(new Date(startTimeMillis), "YYYY-MM-dd HH:mm:ss");
        List errorList = new ArrayList();
        List<Sheet> sheetList = goodsImportXlsHelper.getSheetList();
        for (Sheet activeSheet : sheetList) {
            List<Map<String, String>> backStorageMapList = new ArrayList<>();//备份数据组
            List<Map<String, String>> insertGoodsList = new ArrayList<>();
            List<Map<String, String>> updateGoodsList = new ArrayList<>();
            Iterator<Row> it = activeSheet.rowIterator();
            while (it.hasNext()) {
                Row row = it.next();
                if (row.getRowNum() == 0) {
                    continue;
                }
                if (isBlankRow(row)) {
                    continue;
                }
                Map errorMap = new HashMap();
                try {
                    Map<String, String> goodsData = handleRow(row, goodsCodeCache, batchImportDto.getDetailTpl());
                    String stores_number = goodsData.get("stores_number").toString();
                    String goods_code = String.valueOf(goodsData.get("goods_code"));
                    String[] goodsInfo = goodsMap.get(goods_code).toString().split("_");
                    if (StringUtil.isEmpty(goodsData)) {
                        continue;
                    } else if (hasStorageMap.containsKey(stores_number + "_" + goods_code)) {//该对象已存在于数据库中，更新数据
                        goodsData.put("site_id", String.valueOf(batchImportDto.getSiteId()));
                        goodsData.put("type", batchImportDto.getOption());
                        goodsData.put("sid", (hasStorageMap.get(stores_number + "_" + goods_code)).toString());
                        updateGoodsList.add(goodsData);
                        goodsData.put("type", batchImportDto.getOption());
                        goodsData.put("stores_name",
                            GoodsImportProperty.safeToString(storeMap.get(stores_number)));
                        goodsData.put("goods_name", GoodsImportProperty.safeToString(goodsInfo[0]));//商品名称
                        goodsData.put("goods_category",
                            GoodsImportProperty.safeToString(GoodsImportProperty.drugCateToName.get(goodsInfo[1])));//类别
                        goodsData.put("specif_cation", GoodsImportProperty.safeToString(goodsInfo[2]));//规格
                        goodsData.put("create_time", nowDate);
                        backStorageMapList.add(goodsData);
                        ai.incrementAndGet();
                        continue;
                    } else {//新增数据
                        goodsData.put("site_id", String.valueOf(batchImportDto.getSiteId()));
                        goodsData.put("stores_name",
                            GoodsImportProperty.safeToString(storeMap.get(stores_number)));
                        goodsData.put("goods_name", GoodsImportProperty.safeToString(goodsInfo[0]));//商品名称
                        goodsData.put("goods_category",
                            GoodsImportProperty.safeToString(GoodsImportProperty.drugCateToName.get(goodsInfo[1])));//类别
                        goodsData.put("specif_cation", GoodsImportProperty.safeToString(goodsInfo[2]));//规格
                        goodsData.put("create_time", nowDate);
                        goodsData.put("type", batchImportDto.getOption());
                        insertGoodsList.add(goodsData);
                        backStorageMapList.add(goodsData);
                        ai.incrementAndGet();
                        continue;
                    }
                } catch (RuntimeException e) {
                    LOGGER.debug(e.getMessage());
                    errorMap.put("sheetname", activeSheet.getSheetName());
                    errorMap.put("rownum", String.valueOf(row.getRowNum()));
                    errorMap.put("reason", e.getMessage());
                } catch (Exception e) {
                    LOGGER.debug(e.getMessage());
                    errorMap.put("sheetname", activeSheet.getSheetName());
                    errorMap.put("rownum", String.valueOf(row.getRowNum()));
                    errorMap.put("reason", "未知的错误原因");
                }
                if (!errorMap.isEmpty()) {
                    errorList.add(errorMap);
                }
            }
            if (insertGoodsList.size() > 0) {
                LOGGER.info("需要添加库存的数据:{}", insertGoodsList.toString());
                insertStorageList(insertGoodsList);
            }
            if (updateGoodsList.size() > 0) {
                LOGGER.info("需要更新库存的数据:{}", updateGoodsList.toString());
                updateStorageList(updateGoodsList);
            }
            if (backStorageMapList.size() > 0) {
//                LOGGER.info("需要备份库存的数据:{}", backStorageMapList.toString());
                backupStorage2(batchImportDto.getSiteId(), batchImportDto.getOption(), backStorageMapList);
            }
            batchResult.setErrorList(errorList);
            batchResult.setSuccessNum(ai.get());
            batchResult.setFailNum(batchResult.getErrorList().size() - _ai.get());
            batchResult.setMarrySuccessNum(ai.get() - marryFail_ai.get());
            batchResult.setMarryFailNum(marryFail_ai.get());
            batchResult.setRepeatMarryNum(repeatMarry_ai.get());
        }
        Long endTimeMillis = System.currentTimeMillis();
        LOGGER.info("处理{}条记录用时{}", batchResult.getSuccessNum() + batchResult.getFailNum(), endTimeMillis - startTimeMillis);
        bExcelTimeMapper.insert(batchImportDto.getSiteId(), batchImportDto.getFilesize(), String.valueOf(batchResult.getSuccessNum() + batchResult.getFailNum()),
            String.valueOf(endTimeMillis - startTimeMillis), "库存导入");
        return batchResult;
    }

    /**
     * erp价格导入
     *
     * @param batchImportDto detailTpl：120;option（导入类型）：add/update
     * @return
     */
    public BatchResult importPriceFromEX(BatchImportDto batchImportDto) throws Exception {
        BatchResult batchResult = new BatchResult();
        GoodsImportXlsHelper goodsImportXlsHelper = new GoodsImportXlsHelper();
        //获取数据
        goodsImportXlsHelper.open(batchImportDto.getFileurl());
        //检查xls数据是否符合规范
        goodsImportXlsHelper.checkFileFromStorage(batchImportDto.getDetailTpl(), batchImportDto.getOption());

        final AtomicInteger ai = new AtomicInteger();
        final AtomicInteger _ai = new AtomicInteger();
        final AtomicInteger marryFail_ai = new AtomicInteger();
        final AtomicInteger repeatMarry_ai = new AtomicInteger();

        Long startTimeMillis = System.currentTimeMillis();
        List errorList = new ArrayList();
        List<Sheet> sheetList = goodsImportXlsHelper.getSheetList();
        for (Sheet activeSheet : sheetList) {
            Iterator<Row> it = activeSheet.rowIterator();
            while (it.hasNext()) {
                Row row = it.next();
                if (row.getRowNum() == 0) {
                    continue;
                }
                if (isBlankRow(row)) {
                    continue;
                }
//                executor.execute(() -> {
                Map errorMap = new HashMap();
                try {
                    Map<String, String> goodsData = handleRow(row, null, batchImportDto.getDetailTpl());
                    if (StringUtil.isEmpty(goodsData))
                        continue;
                    updateErpPrice(batchImportDto, goodsData);
                    ai.incrementAndGet();
                } catch (RuntimeException e) {
                    LOGGER.debug(e.getMessage());
                    errorMap.put("sheetname", activeSheet.getSheetName());
                    errorMap.put("rownum", String.valueOf(row.getRowNum()));
                    errorMap.put("reason", e.getMessage());
                } catch (Exception e) {
                    LOGGER.debug(e.getMessage());
                    errorMap.put("sheetname", activeSheet.getSheetName());
                    errorMap.put("rownum", String.valueOf(row.getRowNum()));
                    errorMap.put("reason", "未知的错误原因");
                }
                if (!errorMap.isEmpty()) {
                    errorList.add(errorMap);
                }
            }
        }
        batchResult.setErrorList(errorList);
        Long endTimeMillis = System.currentTimeMillis();
        batchResult.setSuccessNum(ai.get());
        batchResult.setFailNum(batchResult.getErrorList().size() - _ai.get());
        batchResult.setMarrySuccessNum(ai.get() - marryFail_ai.get());
        batchResult.setMarryFailNum(marryFail_ai.get());
        batchResult.setRepeatMarryNum(repeatMarry_ai.get());
        LOGGER.info("处理{}条记录用时{}", batchResult.getSuccessNum() + batchResult.getFailNum(), endTimeMillis - startTimeMillis);
        bExcelTimeMapper.insert(batchImportDto.getSiteId(), batchImportDto.getFilesize(), String.valueOf(batchResult.getSuccessNum() + batchResult.getFailNum()),
            String.valueOf(endTimeMillis - startTimeMillis), "erp价格导入");
        return batchResult;
    }

    //将每条记录转换成对象
    private Map<String, String> handleRow(Row row, Map<String, Object> storageCache, int detailTpl) throws Exception {
        Map<String, String> goodsInfo = GoodsImportProperty.conv2GoodsInfo(row, detailTpl);//每条记录保存成map对象
        //检查必填字段
        if (detailTpl == 110) {
            GoodsImportProperty.storage_checkRequire(goodsInfo);
        } else if (detailTpl == 120) {
            GoodsImportProperty.erp_price_checkRequire(goodsInfo);
        }
        return goodsInfo;
    }

    public PageInfo<Map> selectStorageInfo(Integer siteId, String goods_code, String goods_name, String stores_name, String stores_number,
                                           Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Map> goodsStorages = bStoresStorageMapper.selectStorageBySiteId(siteId, goods_name, goods_code, stores_number, stores_name, 1);
        PageInfo page = new PageInfo<>(goodsStorages);
        return page;
    }

    public List<Map<String, Object>> loadstorageStores(Integer siteId) {
        return bStoresStorageMapper.loadstorageStores(siteId, 1);
    }

    public void updateErpStorage(Integer siteId, Map<String, String> goodsData, Long startTimeMillis) throws ParseException {
        Map<String, Object> goods_old = bStoresStorageMapper.selectByUidAndCode(siteId, goodsData.get("stores_number").toString(),
            goodsData.get("goods_code").toString());
        if (StringUtil.isEmpty(goods_old)) {
            bStoresStorageMapper.insertERPStorage(goodsData);
        } else {
            Long result = startTimeMillis - DateUtils.convert(goods_old.get("create_time").toString(), "yyyy-MM-dd HH:mm:ss").getTime();
            if (result > 86400000) {
                //时间超过了24小时，备份该条数据并并更新数据
                bStoresStorageMapper.insertStorageCopy(goods_old);
                bStoresStorageMapper.updateERPStorage(goodsData);
            } else {
                //24小时内该对象保存最后一次的值
                bStoresStorageMapper.updateERPStorage(goodsData);
            }
        }
    }

    public void updateErpPrice(BatchImportDto batchImportDto, Map<String, String> params) throws Exception {
        Goods goods = goodsMapper.queryByGoodsCode(params.get("goods_code").toString(), String.valueOf(batchImportDto.getSiteId()));
        if (StringUtil.isEmpty(goods)) {
            throw new RuntimeException("商品编码对应的商品不存在");
        } else {
            try {
                Map<String, Object> erpMap = new HashMap<>();
                erpMap.put("siteId", batchImportDto.getSiteId());
                erpMap.put("erp_price", params.get("erp_price"));
                erpMap.put("goods_code", params.get("goods_code"));
                goodsMapper.updateErpPrice(erpMap);
            } catch (Exception e) {
                throw new RuntimeException("更改商品erp价格异常");
            }

        }
    }

  /*  *//**
     * 每次用户保存完库存后对数据进行一次备份，备份数据类型字段:siteId+年份+四位数字
     *//*
    @Async
    public void backupStorage(Integer siteId, String option, String nowDate) {
        String storageType = null;//备份时数据序列号
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);//获取当年年份
        String max_number = bStoresStorageMapper.selectBackMAX(siteId, option);//获取最大序列号
        if (StringUtil.isEmpty(max_number)) {
            storageType = String.valueOf(siteId) + String.valueOf(year) + "0001";
        } else if (option.equals("add")) {
            storageType = max_number;
        } else if (option.equals("update")) {
            storageType = String.valueOf(siteId) + String.valueOf(year) + String.format("%04d",
                Integer.parseInt(max_number.substring(max_number.length() - 4, max_number.length())) + 1);
        } else {
            storageType = String.valueOf(siteId) + String.valueOf(year) + "0001";
        }
        List<Map<String, Object>> storageInsertList = bStoresStorageMapper.selectStorageList(siteId, option, nowDate);
        int num_list = storageInsertList.size() / 2000 + 1;
        for (int i = 0; i < num_list; i++) {
            Map<String, Object> stringObjectMap = new HashMap<>();
            List<Map<String, Object>> storaList = new ArrayList<>();
            for (int j = 0; j < 2000; j++) {
                if (storageInsertList.size() > j + i * 2000) {
                    storaList.add(storageInsertList.get(j + i * 2000));
                    continue;
                } else {
                    break;
                }
            }
            stringObjectMap.put("storageType", storageType);
            stringObjectMap.put("storageList", storaList);
            bStoresStorageMapper.backUpStorage(stringObjectMap);
        }
    }*/

    /**
     * 每次用户保存完库存后对数据进行一次备份，备份数据类型字段:siteId+年份+四位数字
     */
    @Async
    public void backupStorage2(Integer siteId, String option, List<Map<String, String>> storageList) {
        String storageType = null;//备份时数据序列号
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);//获取当年年份
        String max_number = bStoresStorageMapper.selectBackMAX(siteId, option);//获取最大序列号
        if (StringUtil.isEmpty(max_number)) {
            storageType = String.valueOf(siteId) + String.valueOf(year) + "0001";
        } else if (option.equals("add")) {
            storageType = max_number;
        } else if (option.equals("update")) {
            storageType = String.valueOf(siteId) + String.valueOf(year) + String.format("%04d",
                Integer.parseInt(max_number.substring(max_number.length() - 4, max_number.length())) + 1);
        } else {
            storageType = String.valueOf(siteId) + String.valueOf(year) + "0001";
        }
        int num_list = storageList.size() / 2000 + 1;
        for (int i = 0; i < num_list; i++) {
            Map<String, Object> stringObjectMap = new HashMap<>();
            List<Map<String, String>> storaList = new ArrayList<>();
            for (int j = 0; j < 2000; j++) {
                if (storageList.size() > j + i * 2000) {
                    storaList.add(storageList.get(j + i * 2000));
                    continue;
                } else {
                    break;
                }
            }
            stringObjectMap.put("storageType", storageType);
            stringObjectMap.put("storageList", storaList);
            bStoresStorageMapper.backUpStorage2(stringObjectMap);
        }
    }

    public static boolean isBlankRow(Row row) {
        if (row == null) return true;
        boolean result = true;
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i, Row.RETURN_BLANK_AS_NULL);
            String value = "";
            if (cell != null) {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        value = cell.getStringCellValue();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        value = String.valueOf((int) cell.getNumericCellValue());
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        value = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                        value = String.valueOf(cell.getCellFormula());
                        break;
                    default:
                        break;
                }

                if (!value.trim().equals("")) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    @Async
    public void insertStorageList(List<Map<String, String>> storageList) {
        int num = storageList.size() / 1000 + 1;//可细分为若干个小数组
        for (int i = 0; i < num; i++) {
            List<Map<String, String>> storaList = new ArrayList<>();
            for (int j = 0; j < 1000; j++) {
                if (storageList.size() > j + i * 1000) {
                    storaList.add(storageList.get(j + i * 1000));
                    continue;
                } else {
                    break;
                }
            }
            bStoresStorageMapper.insertStorageList(storaList);
        }
    }

    @Async
    public void updateStorageList(List<Map<String, String>> storageList) {
        int num = storageList.size() / 1000 + 1;//可细分为若干个小数组
        for (int i = 0; i < num; i++) {
            List<Map<String, String>> storaList = new ArrayList<>();
            for (int j = 0; j < 1000; j++) {
                if (storageList.size() > j + i * 1000) {
                    storaList.add(storageList.get(j + i * 1000));
                    continue;
                } else {
                    break;
                }
            }
//            bStoresStorageMapper.updateStorageByList(storaList);
            bStoresStorageMapper.batchupdateStorageByListId(storaList);
        }
    }

    //通过接口插入库存信息
    public Map<String, Object> application_insertStorage(Integer siteId, List<Map> storageList) {
        LOGGER.info("method:[/offline/application_insertStorage],站点:{},传递参数:{}", siteId, storageList.toString());
        final AtomicInteger marryFail_ai = new AtomicInteger();
        final AtomicInteger zeroFail_ai = new AtomicInteger();
        Map storageMap = new HashMap();//保存此次要存入的数据
        Map<String, Object> result = new HashMap<>();
        Long startTimeMillis = System.currentTimeMillis();
        String nowDate = DateUtils.formatDate(new Date(startTimeMillis), "YYYY-MM-dd HH:mm:ss");
        List<Map<String, String>> insertStorageList = new ArrayList<>();
        List<Map<String, String>> updateStorageList = new ArrayList<>();
        Map<String, Object> goodsMap = new HashMap<>();
        if (StringUtil.isEmpty(stringRedisTemplate.opsForValue().get(siteId + "_erpStorage_" + "good"))) {
            List<Map<String, Object>> goodsList = goodsMapper.selectGoodsListBysiteId(siteId, 1);
            for (Map<String, Object> good : goodsList) {
                goodsMap.put(GoodsImportProperty.safeToString(good.get("goods_code").toString()),
                    good.get("drug_name") + "_" + good.get("drug_category") + "_" + good.get("specif_cation") + "_");
            }
            stringRedisTemplate.opsForValue().set(siteId + "_erpStorage_" + "good", JacksonUtils.mapToJson(goodsMap), 1, TimeUnit.HOURS);
        } else {
            try {
                goodsMap = JacksonUtils.json2map(stringRedisTemplate.opsForValue().get(siteId + "_erpStorage_" + "good"));
            } catch (Exception e) {
                List<Map<String, Object>> goodsList = goodsMapper.selectGoodsListBysiteId(siteId, 1);
                for (Map<String, Object> good : goodsList) {
                    goodsMap.put(GoodsImportProperty.safeToString(good.get("goods_code").toString()),
                        good.get("drug_name") + "_" + good.get("drug_category") + "_" + good.get("specif_cation") + "_");
                }
            }
        }
        Map<String, Object> storeMap = new HashMap<>();//库中已保存门店数据
        if (StringUtil.isEmpty(stringRedisTemplate.opsForValue().get(siteId + "_erpStorage_" + "stores"))) {
            List<BStores> storesList = bStoresMapper.selectAllStoreByStatus(siteId, 1);
            for (BStores store : storesList) {
                storeMap.put(store.getStoresNumber(), store.getName());
            }
            stringRedisTemplate.opsForValue().set(siteId + "_erpStorage_" + "stores", JacksonUtils.mapToJson(storeMap), 1, TimeUnit.HOURS);
        } else {
            try {
                storeMap = JacksonUtils.json2map(stringRedisTemplate.opsForValue().get(siteId + "_erpStorage_" + "stores"));
            } catch (Exception e) {
                List<BStores> storesList = bStoresMapper.selectAllStoreByStatus(siteId, 1);
                for (BStores store : storesList) {
                    storeMap.put(store.getStoresNumber(), store.getName());
                }
            }
        }
        Map hasStorageMap = new HashMap();//库中已保存数据
        List<Map> hasStorageList = bStoresStorageMapper.selectStorageBySiteId(siteId, null, null, null, null, 1);
        for (Map<String, Object> hashStorage : hasStorageList) {//将数据进行处理便于匹配
            hasStorageMap.put(hashStorage.get("stores_number") + "_" + hashStorage.get("goods_code"), hashStorage.get("id"));
        }
        for (Map<String, Object> goodStorage : storageList) {
            if (Integer.parseInt(String.valueOf(goodStorage.get("kcqty"))) < 0) {
                zeroFail_ai.incrementAndGet();
                continue;
            }
            Map<String, String> storage = new HashMap<>();
            String currentStorage = goodStorage.get("UID") + "_" + goodStorage.get("GOODSNO");
            if (storageMap.containsKey(currentStorage)) {
//                LOGGER.info(goodStorage.get("UID") + "_" + goodStorage.get("GOODSNO") + "此批插入已存在");
                continue;
            } else {
                storageMap.put(currentStorage, "");
                if (hasStorageMap.containsKey(currentStorage)) {//该对象已存在于数据库中，更新数据
                    storage.put("site_id", GoodsImportProperty.safeToString(siteId));
                    storage.put("in_stock", GoodsImportProperty.safeToString(goodStorage.get("kcqty")));
                    storage.put("stores_number", GoodsImportProperty.safeToString(goodStorage.get("UID")));
                    storage.put("goods_code", GoodsImportProperty.safeToString(goodStorage.get("GOODSNO")));
                    storage.put("type", "update");
                    storage.put("sid", GoodsImportProperty.safeToString(hasStorageMap.get(goodStorage.get("UID") + "_" + goodStorage.get("GOODSNO"))));
                    updateStorageList.add(storage);
                    continue;
                } else {//该对象不存在于数据库中
                    if (storeMap.containsKey(goodStorage.get("UID").toString()) && goodsMap.containsKey(goodStorage.get("GOODSNO").toString())) {
                        String[] goodsInfo = goodsMap.get(goodStorage.get("GOODSNO")).toString().split("_");
                        storage.put("stores_number", GoodsImportProperty.safeToString(goodStorage.get("UID")));
                        storage.put("stores_name", GoodsImportProperty.safeToString(storeMap.get(goodStorage.get("UID"))));
                        storage.put("site_id", GoodsImportProperty.safeToString(siteId));
                        storage.put("goods_code", GoodsImportProperty.safeToString(goodStorage.get("GOODSNO")));
                        storage.put("goods_name", GoodsImportProperty.safeToString(goodsInfo[0]));//商品名称
                        storage.put("goods_category",
                            GoodsImportProperty.safeToString(GoodsImportProperty.drugCateToName.get(goodsInfo[1])));//类别
                        storage.put("goods_batch_number", "");
                        storage.put("specif_cation", GoodsImportProperty.safeToString(goodsInfo[2]));//规格
                        storage.put("in_stock", GoodsImportProperty.safeToString(goodStorage.get("kcqty")));
                        storage.put("create_time", nowDate);
                        storage.put("type", "add");
                        if ((!StringUtil.isEmpty(storage.get("stores_number"))) && (!StringUtil.isEmpty(storage.get("goods_code")))) {
                            insertStorageList.add(storage);
                        }
                    } else {
//                        LOGGER.info("erp接口导入库存数据，站点:{}，数据{}不存在线上库",
//                            siteId, goodStorage.get("UID") + "_" + goodStorage.get("GOODSNO"));
                        marryFail_ai.incrementAndGet();
                        continue;
                    }
                }
            }
        }
        Long MiddleTimeMillis = System.currentTimeMillis();
        LOGGER.info("此次后台处理数据处理完毕,时间为{}，需要处理的总数量:{},需要新增库存的数量:{}," +
                "需要更新的库存数量:{},未匹配到的数量:{},库存为0的商品数量:{}。",
            MiddleTimeMillis - startTimeMillis, storageList.size(), insertStorageList.size(),
            updateStorageList.size(), marryFail_ai.get(), zeroFail_ai.get());
        if (insertStorageList.size() > 0) {
            LOGGER.info("需要添加库存的数据:{}", insertStorageList.toString());
            insertStorageList(insertStorageList);
        }
        if (updateStorageList.size() > 0) {
            LOGGER.info("需要更新库存的数据:{}", updateStorageList.toString());
            updateStorageList(updateStorageList);
        }
        Long endTimeMillis = System.currentTimeMillis();
        LOGGER.info("此次插入数据处理完毕,时间为{}", endTimeMillis - MiddleTimeMillis);
        result.put("code", 200);
        result.put("msg", "库存信息处理成功");
        return result;
    }

    /**
     * 根据分类获取商品信息
     *
     * @param params
     * @return
     */
    public ReturnDto getGoodsListByType(Map<String, Object> params) {
        if (!params.containsKey("siteId")) {
            return ReturnDto.buildFailedReturnDto("商户站点不能为空");
        } else {//查询二级分类下所有的商品
            List<Map<String, Object>> goodsList = goodsMapper.selectGoodsByCateType(Integer.parseInt(params.get("siteId").toString()),
                params.get("type").toString());
            goodsList.stream().parallel().forEach(map -> {
                if (map.containsKey("hash") && (!StringUtil.isEmpty(map.get("hash")))) {
                    String goodsImg = map.get("hash").toString();
                    String imageType = map.get("imageType").toString().equals("30") ? "gif" : map.get("imageType").toString().equals("20") ? "png" : "jpg";
                    map.put("drugUrl", "https://jkosshash.oss-cn-shanghai.aliyuncs.com/" + goodsImg + "." + imageType);
                } else {
                    LOGGER.info("siteId:{},goodsId:{}：没有图片", params.get("siteId"), map.get("goodsId"));
                    map.put("drugUrl", "");
                }
                map.remove("imageType");
                map.remove("hash");
            });
            return ReturnDto.buildSuccessReturnDto(goodsList);
        }
    }
}
