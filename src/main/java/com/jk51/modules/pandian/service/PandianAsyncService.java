package com.jk51.modules.pandian.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.configuration.AliOSSConfiguration;
import com.jk51.model.BInventories;
import com.jk51.model.BInventoryLog;
import com.jk51.model.BPandianFile;
import com.jk51.model.Inventories;
import com.jk51.modules.pandian.Response.PandianUploadStatus;
import com.jk51.modules.pandian.dto.PandianSortRedisDto;
import com.jk51.modules.pandian.elasticsearch.repositories.InventoryRepository;
import com.jk51.modules.pandian.mapper.*;
import com.jk51.modules.pandian.param.StoreAdminConfirmParam;
import com.jk51.modules.pandian.param.StoreOrderStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/7/3
 * 修改记录:
 */
@Service
public class PandianAsyncService {

    private Logger logger = LoggerFactory.getLogger(PandianAsyncService.class);

    @Autowired
    private BInventoriesMapper bInventoriesMapper;
    @Autowired
    private BInventoryLogMapper bInventoryLogMapper;
    @Autowired
    private InventoriesMapper inventoriesMapper;
    @Autowired
    private InventoryRepository inventoryRepository;

    @Async
    public void asyncDeleteByStoreIdList(Integer siteId,Integer orderId,List<Integer> storeIds){

        bInventoriesMapper.deleteByStoreIdList(siteId, orderId, storeIds);
    }

    @Async
    public void asyncDeleteByAdd2DB(Integer siteId,Integer storeId,Integer orderId){
        bInventoriesMapper.deleteByAdd(siteId, storeId, orderId);
    }

    @Async
    public void insert2DB(BInventories i){
        bInventoriesMapper.insertSelective(i);
    }

    @Async
    public void update2DB(BInventories bInventories){
        bInventoriesMapper.updateByAdd(bInventories);
    }


    @Async
    public void asyncUpdateInventoryLog(PandianSortRedisDto dto, Double score) {

        List<BInventoryLog> logs = bInventoryLogMapper.findLog(dto.getStoreId(),dto.getPandianNum(),dto.getCurrentGoodsCode());

        if(StringUtil.isEmpty(logs)){
            return;
        }

        for(BInventoryLog log:logs){
            log.setScore(score.toString());
            bInventoryLogMapper.updateLog(log);
        }

    }

    @Async
    public void asyncUpdateInventory(PandianSortRedisDto dto, Double score) {

         inventoriesMapper.updateScore(dto.getPandianNum(),dto.getStoreId(),dto.getCurrentGoodsCode(),score);
         inventoryRepository.updateScore(dto.getPandianNum(),dto.getStoreId(),dto.getCurrentGoodsCode(),score);
    }

    @Async
    public void asyncInsertInventoryLog(BInventoryLog log){

        try{
            bInventoryLogMapper.insert(log);
        }catch (Exception e){
            logger.error("盘点确认日志记录异常：报错信息：{}",ExceptionUtil.exceptionDetail(e));
        }

    }

    @Async
    public void asyncRestInventoryonfirm(Integer storeId, String pandian_num){

        try{
            inventoriesMapper.restInventoryonfirm(storeId,pandian_num);
        }catch (Exception e){
            logger.error("盘点复盘异步更新mysql报错：storeId:{}, pandian_num:{},报错信息：{}",ExceptionUtil.exceptionDetail(e));
        }

    }

    @Async
    public void insert2DB(Inventories inventories){

        inventoriesMapper.insertInventory(inventories);
    }

    @Async
    public void update2DB(Inventories inventories){

        inventoriesMapper.updateInventoryByEsId(inventories);
    }

    @Async
    public void storeAdminConfirm2DB(StoreAdminConfirmParam param) {

        inventoriesMapper.storeAdminConfirm(param);
    }

    @Async
    public void asyncInsert2DB(List<Inventories> list){
        bInventoriesMapper.insertByList(list);
    }












    @Autowired
    private OSSClient ossClient;
    @Autowired
    private AliOSSConfiguration.AliOSSConfig aliOSSConfig;
    @Autowired
    BPandianOrderStatusMapper bPandianOrderStatusMapper;
    @Autowired
    BPandianFileMapper bPandianFileMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Async
    public void asyncInsertExcel2DB(Integer siteId, Integer storeId, Integer option, String pdNum,
                                    String fileKey, String fileName, String fileURL, String pdfFlag,
                                    Workbook workbook, List<Integer> sheetIndexList, Map<String, StoreOrderStatus> storesMap,
                                    PandianUploadStatus pandianUploadStatus, Set<String> storeNumSet, List<Integer> storeIdList,
                                    Map<String, String> errorMap, long startTime, long parseTime, int totalLimit) {
        final String pdfFlagRow = pdfFlag + "_row";
        final AtomicInteger total = new AtomicInteger(0);
        final AtomicInteger successTotal = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(30, new ThreadFactoryBuilder()
            .setNameFormat("Async-Request-PD-%d")
            .setDaemon(true)
            .build());
        Sheet sheet = null;
        for (Integer index : sheetIndexList) {
            sheet = workbook.getSheetAt(index);
            for (Row row : sheet) {
                if (row.getRowNum() != 0) total.incrementAndGet();
                if (row.getRowNum() == 0 || errorMap.containsKey(index + "-" + row.getRowNum()))
                    continue;
                executorService.execute(() -> {
                    try {
                        String storeNum = getCellValueParse(row.getCell(PandianService.HEADERS.indexOf(PandianService.header_storenum))).trim();
                        Integer storeNumId = storesMap.get(storeNum).getStoreId();
                        String goodsCode = getCellValueParse(row.getCell(PandianService.HEADERS.indexOf(PandianService.header_goodscode))).trim();
                        String batchNumber = getCellValueParse(row.getCell(PandianService.HEADERS.indexOf(PandianService.header_batchnum))).trim();
                        String drugName = getCellValueParse(row.getCell(PandianService.HEADERS.indexOf(PandianService.header_goodsname))).trim();
                        String specifCation = getCellValueParse(row.getCell(PandianService.HEADERS.indexOf(PandianService.header_spec))).trim();
                        String goodsCompany = getCellValueParse(row.getCell(PandianService.HEADERS.indexOf(PandianService.header_company))).trim();
                        Double inventoryAccounting = null;
                        try {
                            if (CellType.NUMERIC.equals(row.getCell(PandianService.HEADERS.indexOf(PandianService.header_inventory)).getCellTypeEnum())) {//不算日期new DecimalFormat("#").format(row.getCell(INVENTORY_ACCOUNTING_CELL).getNumericCellValue())
                                inventoryAccounting = row.getCell(PandianService.HEADERS.indexOf(PandianService.header_inventory)).getNumericCellValue();
                            } else if (CellType.STRING.equals(row.getCell(PandianService.HEADERS.indexOf(PandianService.header_inventory)).getCellTypeEnum())) {
                                inventoryAccounting = Double.valueOf(row.getCell(PandianService.HEADERS.indexOf(PandianService.header_inventory)).getStringCellValue().trim());
                            }
                        } catch (Exception e) {
                            logger.error("addFile inventoryAccounting：获取账面库存异常 {}，{}，{}，{}，{} {}", pdNum, index, row.getRowNum(), goodsCode, e.getMessage(), e);
                        }
                        BInventories bInventories = new BInventories();
                        bInventories.setSiteId(siteId);
                        bInventories.setOrderId(pandianUploadStatus.getId());
                        bInventories.setPandianNum(pdNum);
                        bInventories.setPlanId(pandianUploadStatus.getPlanId());
                        bInventories.setStoreId(storeNumId);
                        bInventories.setStoreNum(storeNum);
                        bInventories.setGoodsCode(goodsCode);
                        bInventories.setBatchNumber(batchNumber);
                        bInventories.setDrugName(drugName);
                        bInventories.setSpecifCation(specifCation);
                        bInventories.setGoodsCompany(goodsCompany);
                        bInventories.setInventoryAccounting(inventoryAccounting);
                        if (option == 0) {//直接新增
                            insert(bInventories);
                            //bInventoriesMapper.insertSelective(bInventories);
                        } else {//覆盖新增
                           /* if (bInventoriesMapper.updateByAdd(bInventories) <= 0) {
                                bInventoriesMapper.insertSelective(bInventories);
                            }*/
                            int num = update(bInventories);
                            if(num<=0){
                                insert(bInventories);
                            }
                        }
                        successTotal.incrementAndGet();
                    } catch (Exception e) {
                        logger.error("addFile bInventories：数据插入异常 {}，{}，{}，{} {}", pdNum, index, row.getRowNum(), e.getMessage(), e);
                        errorMap.put(index + "-" + row.getRowNum(), "添加时异常");
                    }
                    try {
                        Map<String, Object> rowMap = new HashMap<>();
                        rowMap.put("type", 2);
                        rowMap.put("total", totalLimit);
                        rowMap.put("count", successTotal.get() + errorMap.size());
                        stringRedisTemplate.opsForValue().set(pdfFlagRow, JSONObject.toJSONString(rowMap), 5, TimeUnit.HOURS);
                    } catch (Exception e) {
                        logger.error("addFile pdfFlag_row：{}，{}，{}", pdNum, e.getMessage(), e);
                    }
                });
            }
        }
        executorService.shutdown();
        try {
            while (!executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS)) ;
        } catch (Exception e) {
            logger.error("addFile executorService：线程中断异常 {}，{}，{}", pdNum, e.getMessage(), e);
            //executorService.shutdownNow();
        }

        long insertTime = System.currentTimeMillis();
        try {
            if (CollectionUtils.isNotEmpty(storeNumSet))
                bPandianOrderStatusMapper.updateStatusByStoreIdList(siteId, pandianUploadStatus.getId(), storeIdList, 100);
        } catch (Exception e) {
            logger.error("addFile OrderStatus finally：修改门店盘点单状态异常[{}/{}]，{}，{}，{}", pdNum, pandianUploadStatus.getId(), storeNumSet, e.getMessage(), e);
        }

        String resultFileURL = null;
        try {
            if (MapUtils.isNotEmpty(errorMap)) {
                Font font = workbook.createFont();
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                font.setColor(Font.COLOR_RED);
                Row row = null;
                Cell cell = null;
                for (Map.Entry<String, String> entry : errorMap.entrySet()) {
                    sheet = workbook.getSheetAt(Integer.parseInt(entry.getKey().split("-")[0]));
                    row = sheet.getRow(Integer.parseInt(entry.getKey().split("-")[1]));
                    cell = row.createCell(PandianService.HEADERS.size() + 1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(entry.getValue());
                }

                Workbook workbookCopy = workbook;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbookCopy.write(outputStream);
                String key = AliOSSConfiguration.randomBucketKey(fileKey.substring(fileKey.lastIndexOf(".") + 1));
                try {
                    PutObjectRequest putObjectRequest = new PutObjectRequest(aliOSSConfig.BUCKET_NAME, key, new ByteArrayInputStream(outputStream.toByteArray()));
                    PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
                    resultFileURL = "http://" + aliOSSConfig.BUCKET_NAME + "." + aliOSSConfig.getImgUrl() + "/" + key;
                } catch (OSSException e) {
                    logger.error("addFile OSSException {} {}", e.getMessage(), e);
                } catch (ClientException e) {
                    logger.error("addFile ClientException {} {}", e.getMessage(), e);
                }
                workbook.close();
            } else {
                //resultFileURL = fileURL;
            }
        } catch (Exception e) {
            logger.error("addFile upload resultFile 异常 {} {}", e.getMessage(), e);
            resultFileURL = "error";
        }
        long uploadTime = System.currentTimeMillis();

        Map<String, Object> result = new HashMap();
        result.put("type", 1);
        result.put("total", total.get());
        result.put("successTotal", successTotal.get());
        result.put("errorTotal", errorMap.size());
        result.put("pdNum", pdNum);
        result.put("pdfFlag", pdfFlag);
        result.put("fileName", fileName);
        result.put("resUrl", resultFileURL);
        storeNumSet = null;
        storeIdList = null;
        String resultURL = resultFileURL;
        try {
            bPandianFileMapper.insertSelective(new BPandianFile(){{
                setSiteId(siteId);
                setStoreId(storeId);
                setType(option);
                setOrderId(pandianUploadStatus.getId());
                setPandianNum(pdNum);
                setFileUrl(fileURL);
                setResUrl(resultURL);
                setFileName(fileName);
                setExt(String.format("文件处理完成：%s；startTime：%s，parseTime：%s，insertTime：%s，uploadTime：%s。", result, startTime, parseTime, insertTime, uploadTime));
            }});
        } catch (Exception e) {
            logger.error("addFile fileLog：文件处理完成[{}/{}]，{}，{}", pdNum, resultURL, e.getMessage(), e);
        }

        try {
            stringRedisTemplate.opsForValue().set(pdfFlag, JSONObject.toJSONString(result), 5, TimeUnit.HOURS);
        } catch (Exception e) {
            logger.error("addFile pdfFlag：文件处理完成[{}/{}]，{}，{}", pdNum, resultURL, e.getMessage(), e);
        }
    }


    public String getCellValueParse(Cell cell) {
        String cellValue = "";
        try {
            switch (cell.getCellTypeEnum()) {
                case STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    cellValue = String.valueOf((int) cell.getNumericCellValue());
                    break;
                case BOOLEAN:
                    cellValue = cell.getBooleanCellValue() ? "1" : "0";
                    break;
                case BLANK:
                    cellValue = "";
                    break;
                default:
                    cellValue = cell.getStringCellValue();
                    break;
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return cellValue;
    }

    private void insert(BInventories i){

        Inventories inventory = typeConvert(i);
        String esId = inventoryRepository.index(inventory);

        i.setEsId(Integer.parseInt(esId));
        //pandianAsyncService.insert2DB(i);
        bInventoriesMapper.insertSelective(i);
    }

    public int update(BInventories i){

        Inventories inventory = typeConvert(i);
        int result = inventoryRepository.updateInvenroty(inventory);

        //pandianAsyncService.update2DB(i);
        bInventoriesMapper.updateByAdd(i);

        return result;
    }

    private Inventories typeConvert(BInventories i){

        Inventories result = new Inventories();
        result.setSite_id(i.getSiteId());
        result.setOrder_id(i.getOrderId());
        result.setPandian_num(i.getPandianNum());
        result.setPlan_id(i.getPlanId());
        result.setStore_id(i.getStoreId());
        result.setStore_num(i.getStoreNum());
        result.setGoods_code(i.getGoodsCode());
        result.setBatch_number(i.getBatchNumber());
        result.setDrug_name(i.getDrugName());
        result.setSpecif_cation(i.getSpecifCation());
        result.setGoods_company(i.getGoodsCompany());
        result.setInventory_accounting(new BigDecimal(i.getInventoryAccounting()).setScale(4,RoundingMode.HALF_UP).doubleValue());


        return result;

    }

}
