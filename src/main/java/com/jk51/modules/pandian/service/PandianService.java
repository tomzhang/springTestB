package com.jk51.modules.pandian.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.jk51.commons.excel.ExcelOperator;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.redis.RedisUtil;
import com.jk51.configuration.AliOSSConfiguration;
import com.jk51.model.*;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.pandian.Response.PandianPlanDetail;
import com.jk51.modules.pandian.Response.PandianPlanInfo;
import com.jk51.modules.pandian.Response.PandianUploadStatus;
import com.jk51.modules.pandian.elasticsearch.repositories.InventoryRepository;
import com.jk51.modules.pandian.mapper.*;
import com.jk51.modules.pandian.param.PDMessage;
import com.jk51.modules.pandian.param.StatusParam;
import com.jk51.modules.pandian.param.StoreOrderStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PandianService {
    private static final Logger logger = LoggerFactory.getLogger(PandianService.class);
    public static final Integer TYPE_SITE = 0;//商户设置
    public static final Integer TYPE_STORE = 1;//门店设置
    private static final String[] FIELDS = new String[]{"门店编码*", "商品编码*", "商品名称*", "批号*", "规格*", "生产厂家*", "账面库存"};
    private static final int STORE_NUM_CELL = 0;
    private static final int GOODS_CODE_CELL = 1;
    private static final int DRUG_NAME_CELL = 2;
    private static final int BATCH_NUMBER_CELL = 3;
    private static final int SPECIFCATION_CELL = 4;
    private static final int GOODS_COMPANY_CELL = 5;
    private static final int INVENTORY_ACCOUNTING_CELL = 6;

    @Value("${report.temp_dir}")
    private String temp_dir;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BPandianPlanMapper bPandianPlanMapper;
    @Autowired
    private BPandianPlanLogMapper bPandianPlanLogMapper;
    @Autowired
    private BPandianPlanExecutorMapper bPandianPlanExecutorMapper;
    @Autowired
    private BPandianOrderMapper bPandianOrderMapper;
    @Autowired
    private BPandianFileMapper bPandianFileMapper;
    @Autowired
    private BInventoriesMapper bInventoriesMapper;
    @Autowired
    private BPandianOrderStatusMapper bPandianOrderStatusMapper;
    @Autowired
    private PandianErpDataSync pandianErpDataSync;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private PandianAsyncService pandianAsyncService;
    /**
     * 添加盘点计划
     */
    @Transactional
    public Map<String, Object> addPlan(BPandianPlan pandianPlan) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (pandianPlan.getPlanDay() == null || pandianPlan.getPlanHour() == null || pandianPlan.getPlanCheckType() == null
            || pandianPlan.getType() == null || pandianPlan.getSiteId() == null || pandianPlan.getPlanOperator() == null || pandianPlan.getPlanExecutor() == null) {
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        if (pandianPlan.getType() == PandianService.TYPE_STORE && pandianPlan.getStoreId() == null) {//门店设置
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

//        try {
        Map<String, Object> executorMap = (Map<String, Object>) JSON.parse(pandianPlan.getPlanExecutor());
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info("json解析异常 {}", pandianPlan.getPlanExecutor());
//        }
        pandianPlan.setPlanExecutor(null);

        int executorSize = 0;
        int executorSave = 0;
        List<BPandianPlanExecutor> pandianPlanExecutors = new ArrayList<>();
        List<Integer> storeIdList = new ArrayList<>();
        if (executorMap != null) {
            executorSize = executorMap.size();
            for (Map.Entry<String, Object> entry : executorMap.entrySet()) {
                BPandianPlanExecutor pandianPlanExecutor = new BPandianPlanExecutor();
                pandianPlanExecutor.setSiteId(pandianPlan.getSiteId());
//                pandianPlanExecutor.setPlanId(pandianPlan.getId());
                pandianPlanExecutor.setStoreId(Integer.parseInt(entry.getKey()));
                pandianPlanExecutor.setClerks(String.valueOf(entry.getValue()));
                pandianPlanExecutors.add(pandianPlanExecutor);
                storeIdList.add(Integer.parseInt(entry.getKey()));

                /*try {
                    if(entry.getValue() instanceof Integer){
                        Integer clerks = (Integer) entry.getValue();
                        System.out.println(clerks);
                        executorSave++;
                    }else if(entry.getValue() instanceof JSONArray){
                        List<Integer> clerks = (List<Integer>) entry.getValue();
                        System.out.println(clerks);
                        executorSave++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("map解析异常 {}", entry.getValue());
                }*/
            }
        }
        executorMap.clear();//空指针 json字符串解析异常

        if (storeIdList.size() == 0) {
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("siteId", pandianPlan.getSiteId());
        params.put("planCheckType", pandianPlan.getPlanCheckType());
        params.put("planDay", pandianPlan.getPlanDay());
        params.put("planHour", pandianPlan.getPlanHour());
        params.put("storeIdList", storeIdList);
        params.put("storeId", storeIdList.get(0));//全部门店
        List<Map<String, Object>> existPlan = bPandianPlanMapper.getExistPlans(params);
        if (existPlan != null && existPlan.size() != 0) {
            result.put("status", "ERROR");
            result.put("message", "存在雷同计划设置");
            result.put("existPlan", existPlan);
            return result;
        }
        pandianPlan.setPlanStop(pandianPlan.getPlanType()==1?1:null);
        bPandianPlanMapper.insertSelective(pandianPlan);
        executorSave = bPandianPlanExecutorMapper.insertByList(pandianPlanExecutors, pandianPlan.getId());
        if (pandianPlan.getPlanType() == 1) {
            createPandianOrder(pandianPlan, storeIdList);
        }

        try {
            insertPandianPlanLog(pandianPlan.getSiteId(), pandianPlan.getStoreId(), pandianPlan.getType(),
                pandianPlan.getId(), pandianPlan.getPlanOperator(), JSON.toJSONString(pandianPlan), "添加盘点计划," + " storeSize: " + executorSize + " save: " + executorSave);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("addPlan 日志异常 {}", e);
        }

        result.put("status", "OK");
//        result.put("executorMap", JSON.toJSONString(executorMap));
        return result;
    }

    /**
     * 停用/删除 盘点计划
     */
    public Map<String, Object> stopOrDelPlan(BPandianPlan pandianPlan) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (pandianPlan.getId() == null || pandianPlan.getType() == null || pandianPlan.getSiteId() == null || pandianPlan.getPlanOperator() == null) {
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        if (pandianPlan.getType() == PandianService.TYPE_STORE && pandianPlan.getStoreId() == null) {//门店设置
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        //停用之前检查

        bPandianPlanMapper.stopOrDelPlan(pandianPlan);

        try {
            List<String> descList = new ArrayList<>();
            if ("0".equals(String.valueOf(pandianPlan.getPlanStop()))) {
                descList.add("启用");
            } else if ("1".equals(String.valueOf(pandianPlan.getPlanStop()))) {
                descList.add("停用");
            }
            if ("0".equals(String.valueOf(pandianPlan.getPlanDelete()))) {
                descList.add("恢复");
            } else if ("1".equals(String.valueOf(pandianPlan.getPlanDelete()))) {
                descList.add("删除");
            }
            insertPandianPlanLog(pandianPlan.getSiteId(), pandianPlan.getStoreId(), pandianPlan.getType(),
                pandianPlan.getId(), pandianPlan.getPlanOperator(), JSON.toJSONString(pandianPlan), Joiner.on(",").join(descList));
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("stopOrDelPlan 日志异常 {}", e);
        }

        result.put("status", "OK");
        return result;
    }

    /**
     * 盘点计划详情
     */
    public PandianPlanDetail getPlanDetail(Integer siteId, Integer storeId, Integer id) {
        return bPandianPlanMapper.getPlanDetail(siteId, storeId, id);
    }

    public List<PandianPlanInfo> getPlanList(Integer siteId, Integer storeId, Integer source, Integer id,
                                             Integer planType, String startTime, String endTime,
                                             Integer pageNum, Integer pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        return bPandianPlanMapper.getPlanList(siteId, storeId, source, id, planType, startTime, endTime);
    }

    public String getPandianNum(Integer siteId, Integer storeId, Integer type) throws Exception {
        BPandianOrder order = bPandianOrderMapper.getLatestPandianOrder(siteId, storeId, type);
        StringBuilder pandianNum = new StringBuilder("PD");
        if (type == TYPE_STORE) {
            pandianNum.append(bPandianPlanMapper.getStoreNum(siteId, storeId));
        } else {
            pandianNum.append(siteId);
        }
        pandianNum.append(new SimpleDateFormat("yy").format(new Date()));
        if (order != null) {
            Integer currentNum = Integer.parseInt(order.getPandianNum().substring(order.getPandianNum().length() - 4));
            pandianNum.append(String.format("%04d", currentNum + 1));
        } else {
            pandianNum.append("0001");
        }
        return pandianNum.toString();
    }

    public void insertPandianPlanLog(Integer siteId, Integer storeId, Integer type, Integer planId, Integer operator, String newPlanPart, String description) throws Exception {
        BPandianPlanLog pandianPlanLog = new BPandianPlanLog();
        pandianPlanLog.setSiteId(siteId);
        pandianPlanLog.setStoreId(storeId);
        pandianPlanLog.setType(type);
        pandianPlanLog.setPlanId(planId);
        pandianPlanLog.setOperator(operator);
        pandianPlanLog.setNewPlan(newPlanPart);
        pandianPlanLog.setDescription(description);
        bPandianPlanLogMapper.insertSelective(pandianPlanLog);
    }

    public List<String> getPlanClerks(Integer siteId, Integer id, Integer storeId) {
        return bPandianPlanExecutorMapper.getClerkNameList(siteId, id, storeId);
    }

    public Map<String, Object> getPlanOrder(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params == null || params.get("id") == null || params.get("siteId") == null) {
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        Map<String, Object> order = bPandianOrderMapper.getPlanOrder(Integer.parseInt(String.valueOf(params.get("siteId"))), Integer.parseInt(String.valueOf(params.get("id"))));

        result.put("order", order);
        result.put("status", "OK");
        return result;
    }

    /*public Map<String, Object> addFile(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params == null || StringUtil.isEmpty(params.get("id")) || StringUtil.isEmpty(params.get("fileUrl")) || StringUtil.isEmpty(params.get("option")) ||
            StringUtil.isEmpty(params.get("type")) || StringUtil.isEmpty(params.get("siteId")) || StringUtil.isEmpty(params.get("planOperator")) ||
            StringUtil.isEmpty(params.get("num")) || StringUtil.isEmpty(params.get("pid"))) {
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        Integer type = Integer.parseInt(String.valueOf(params.get("type")));
        if (type == PandianService.TYPE_STORE && StringUtil.isEmpty(params.get("storeId"))) {//门店设置
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }
        Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
        Integer storeId = !StringUtil.isEmpty(params.get("storeId")) ? Integer.parseInt(String.valueOf(params.get("storeId"))) : null;
        Integer planOperator = Integer.parseInt(String.valueOf(params.get("planOperator")));
        Integer id = Integer.parseInt(String.valueOf(params.get("id")));
        String pandianNum = String.valueOf(params.get("num"));
        Integer planId = Integer.parseInt(String.valueOf(params.get("pid")));
        Integer option = Integer.parseInt(String.valueOf(params.get("option")));
        String fileUrl = String.valueOf(params.get("fileUrl"));

        Integer fileId = null;
        try {
            String desc = "";
            if (option == 0) {

                deleteByAdd(siteId, storeId, id);//storeId!=null删除门店商品
                desc = "清空并新增, ";
            } else if (option == 1) {
                desc = "覆盖新增, ";
            } else {
                result.put("status", "ERROR");
                result.put("message", "缺少必要参数");
                return result;
            }
            fileId = insertBPandinFile(siteId, storeId, id, option, fileUrl, params);
            params.put("fileId", fileId);
            insertPandianPlanLog(siteId, storeId, type, null, planOperator, JSON.toJSONString(params), "添加盘点文件addFile：" + desc + "fileId-" + String.valueOf(fileId));

            Integer i = bPandianOrderMapper.getUploadByStatus(siteId, id, null, storeId);
            if (i != null) {
                result.put("status", "ERROR");
                result.put("message", "文件上传中，稍后重试");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("addFile 日志异常 {}", e);
        }
        if (fileId == null) {
            result.put("status", "ERROR");
            result.put("message", "上传异常");
            return result;
        }

        long s = System.currentTimeMillis();
        Workbook workbook = parseWorkbook(fileUrl);
        if (workbook == null) {
            result.put("status", "ERROR");
            result.put("message", "文件解析异常");
            return result;
        }
        int numberSheet = workbook.getNumberOfSheets();
        Sheet activeSheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
        if (numberSheet <= 0 || activeSheet == null || activeSheet.getLastRowNum() < 1) {
            result.put("status", "ERROR");
            result.put("message", "空文件");
            return result;
        }
        Row titleRow = activeSheet.getRow(0);
        for (int i = 0; i < FIELDS.length; i++) {
//            System.out.println(titleRow.getCell(i).getStringCellValue());
            if (!FIELDS[i].equals(StringUtil.trimAll(titleRow.getCell(i).getStringCellValue()))) {
                result.put("status", "ERROR");
                result.put("message", "模板不匹配");
                return result;
            }
        }
        StringBuilder ext = new StringBuilder();
        ext.append("workbook:" + String.valueOf((System.currentTimeMillis() - s) / 1000f) + "(sheets:" + numberSheet + ")");

        Long w = System.currentTimeMillis();
        Map<String, Integer> storeMap = new HashMap<>();//门店num
        Map<String, Integer> canUpStoreMap = new HashMap<>();//门店num
        try {
            List<Map<String, Object>> storeList = bPandianPlanExecutorMapper.getStoreNumList(siteId, storeId, planId);
            if (storeList != null && storeList.size() != 0) {
                storeList.stream().forEach(m -> storeMap.put(String.valueOf(m.get("storeNum")), Integer.parseInt(String.valueOf(m.get("id")))));
            }
            List<Map<String, Object>> canUpStoreList = bPandianOrderStatusMapper.getCanUpStoreNumList(siteId, storeId, id);
            if (canUpStoreList != null && canUpStoreList.size() != 0) {
                canUpStoreList.stream().forEach(m -> canUpStoreMap.put(String.valueOf(m.get("storeNum")), Integer.parseInt(String.valueOf(m.get("id")))));
            }

            bPandianOrderStatusMapper.updateStatusByAdd(siteId, storeId, id, 1);//storeId!=null门店上传 //更新盘点单状态为上传中
        } catch (Exception e) {
            logger.info("addFile 修改状态 {}", e);
            result.put("status", "ERROR");
            result.put("message", "上传异常");
            return result;
        }
        ext.append(" query:" + String.valueOf((System.currentTimeMillis() - w) / 1000f) + "(storeMap:" + storeMap.size() + ")");


        int fieldsLength = FIELDS.length;
        int mustLength = fieldsLength - 1;//前len-1列必填项
        int rowCount = 0;
        Map<String, String> errorMap = new ConcurrentHashMap<>();//标记不符合行
        Map<String, String> goodsMap = new HashMap<>();//用来匹配Excel内重复项
        Set<String> storeNumSet = new HashSet<>();//用来查询门店编号
        Cell cell = null;
        for (Sheet sheet : workbook) {//省略空sheet
            rowCount += sheet.getLastRowNum();
            int sheetIndex = workbook.getSheetIndex(sheet.getSheetName());
            for (Row row : sheet) {//省略空行
                if (row.getRowNum() == 0) {//标题行
                    continue;
                }
                *//*if(row.getLastCellNum()!=fieldsLength-1 || row.getLastCellNum()!=fieldsLength){
                    errorMap.put(sheetIndex+"-"+row.getRowNum(), "数据项不匹配");
                    continue;
                }*//*
                boolean flag = true;
                for (int i = 0; i < mustLength; i++) {//必填项不能为空
                    //cell = row.getCell(i);
                    cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    try {
                        if (cell == null || StringUtil.isEmpty(getCellValueParse(cell).trim())) {
                            errorMap.put(sheetIndex + "-" + row.getRowNum(), "必填项不能为空");
                            flag = false;
                            break;
                        }
                    } catch (Exception e) {
                        errorMap.put(sheetIndex + "-" + row.getRowNum(), "数据项类型不匹配");
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    String good = getCellValueParse(row.getCell(STORE_NUM_CELL)).trim() + getCellValueParse(row.getCell(GOODS_CODE_CELL)).trim() + getCellValueParse(row.getCell(BATCH_NUMBER_CELL)).trim();
                    if (goodsMap.containsKey(good)) {
                        errorMap.put(sheetIndex + "-" + row.getRowNum(), "数据重复项");
                        errorMap.put(goodsMap.get(good), "数据重复项");
                    } else {
                        String storeNum = getCellValueParse(row.getCell(STORE_NUM_CELL)).trim();
                        Integer storeNumId = storeMap.get(storeNum);
                        if ((storeNumId == null) || (storeId != null && !storeId.equals(storeNumId))) {//门店ID
                            errorMap.put(sheetIndex + "-" + row.getRowNum(), "无法匹配的门店编号");
                        } else {
                            if(StringUtil.isEmpty(canUpStoreMap.get(storeNum))){
                                errorMap.put(sheetIndex + "-" + row.getRowNum(), "门店已上传");
                            }else {
                                goodsMap.put(good, sheetIndex + "-" + row.getRowNum());
                            }
                        }
                    }
                }
            }
        }
        goodsMap = null;
        ext.append(" sheetFor:" + String.valueOf((System.currentTimeMillis() - w) / 1000f) + "(rowCount:" + rowCount + ")");


        //DecimalFormat df = new DecimalFormat("#");
        final AtomicInteger total = new AtomicInteger(0);
        final AtomicInteger successTotal = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        for (Sheet sheet : workbook) {//省略空sheet
            int sheetIndex = workbook.getSheetIndex(sheet.getSheetName());
            for (Row row : sheet) {//省略空行
                if(row.getRowNum() != 0){
                    total.incrementAndGet();
                }
                if (row.getRowNum() == 0 || errorMap.containsKey(sheetIndex + "-" + row.getRowNum())) {//标题行，异常列
                    continue;
                }
                executorService.execute(() -> {
                    try {
                        String storeNum = getCellValueParse(row.getCell(STORE_NUM_CELL)).trim();
                        Integer storeNumId = storeMap.get(getCellValueParse(row.getCell(STORE_NUM_CELL)).trim());
                        String goodsCode = getCellValueParse(row.getCell(GOODS_CODE_CELL)).trim();
                        String batchNumber = getCellValueParse(row.getCell(BATCH_NUMBER_CELL)).trim();
                        String drugName = getCellValueParse(row.getCell(DRUG_NAME_CELL)).trim();
                        String specifCation = getCellValueParse(row.getCell(SPECIFCATION_CELL)).trim();
                        String goodsCompany = getCellValueParse(row.getCell(GOODS_COMPANY_CELL)).trim();
                        Double inventoryAccounting = null;
                        try {
                            if (row.getCell(INVENTORY_ACCOUNTING_CELL).getCellTypeEnum() == CellType.NUMERIC) {//不算日期new DecimalFormat("#").format(row.getCell(INVENTORY_ACCOUNTING_CELL).getNumericCellValue())
                                inventoryAccounting = row.getCell(INVENTORY_ACCOUNTING_CELL).getNumericCellValue();
                            } else if (row.getCell(INVENTORY_ACCOUNTING_CELL).getCellTypeEnum() == CellType.STRING) {
                                inventoryAccounting = Double.valueOf(row.getCell(INVENTORY_ACCOUNTING_CELL).getStringCellValue().trim());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        BInventories bInventories = new BInventories();
                        bInventories.setSiteId(siteId);
                        bInventories.setOrderId(id);
                        bInventories.setPandianNum(pandianNum);
                        bInventories.setPlanId(planId);
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
                           *//* if (bInventoriesMapper.updateByAdd(bInventories) <= 0) {
                                bInventoriesMapper.insertSelective(bInventories);
                            }*//*

                            int num = update(bInventories);
                            if(num<=0){
                                insert(bInventories);
                            }
                        }
                        successTotal.incrementAndGet();
                    } catch (Exception e) {
                        logger.debug(e.getMessage());
                        errorMap.put(sheetIndex + "-" + row.getRowNum(), "添加时异常");
                    }
                });
            }
        }
        executorService.shutdown();
        try {
            while (!executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS)) ;
        } catch (Exception e) {
            logger.debug(e.getMessage());
            //executorService.shutdownNow();
        }

        ext.append(" complete:" + String.valueOf((System.currentTimeMillis() - w) / 1000f));
        try {
            //更新盘点单状态为已上传
            bPandianOrderStatusMapper.updateStatusByAdd(siteId, storeId, id, 100);//storeId!=null门店上传
            bPandianFileMapper.updateStatusById(fileId, 1, ext.toString(), null);//日志
        } catch (Exception e) {
            //
        }

        result.put("fileId", fileId);
        result.put("errorMap", errorMap);
        result.put("total", total.get());
        result.put("successTotal", successTotal.get());
        result.put("errorTotal", errorMap.size());
        result.put("status", "OK");
        return result;
    }*/

    @Autowired
    private OSSClient ossClient;
    @Autowired
    private AliOSSConfiguration.AliOSSConfig aliOSSConfig;
    public static final String header_storenum = "门店编码*";
    public static final String header_goodscode = "商品编码*";
    public static final String header_goodsname = "商品名称*";
    public static final String header_batchnum = "批号*";
    public static final String header_spec = "规格*";
    public static final String header_company = "生产厂家*";
    public static final String header_inventory = "账面库存";
    public static final List<String> HEADERS = Arrays.asList(header_storenum,header_goodscode,header_goodsname,header_batchnum,header_spec,header_company,header_inventory);
    public static final List<String> MUST_HEADERS = Arrays.asList(header_storenum,header_goodscode,header_goodsname,header_batchnum,header_spec,header_company);
    public static final List<String> UNIQUE_HEADERS = Arrays.asList(header_storenum,header_goodscode,header_batchnum);
    public static final List<Integer> UPLOAD_STATUS = Arrays.asList(0,100);//待上传、待下发
    //有问题加分布式锁
    public Result addFile(String fileName, String fileURL, String fileKey, String pdNum, Integer siteId, Integer option, Integer storeId) {
        if (StringUtils.isBlank(fileURL) || StringUtils.isBlank(fileKey) || StringUtils.isBlank(pdNum)
            || siteId == null || !Arrays.asList(0,1).contains(option))
            return Result.fail("缺少必要参数");

        PandianUploadStatus pandianUploadStatus = bPandianPlanMapper.getStorePandianStatus(pdNum, siteId, storeId);

        if (pandianUploadStatus == null || CollectionUtils.isEmpty(pandianUploadStatus.getStores()))
            return Result.fail("查无对应盘点单号");

        Optional<StoreOrderStatus> storeOrderStatus;
        if (storeId != null) {
            if (pandianUploadStatus.getUploadType() == 1)
                return Result.fail("只允许总部上传");

            storeOrderStatus = pandianUploadStatus.getStores()
                .stream()
                .filter(o -> storeId.equals(o.getStoreId()))
                .findFirst();

            if (!storeOrderStatus.isPresent())
                return Result.fail("查无对应门店");
            if (!UPLOAD_STATUS.contains(storeOrderStatus.get().getStatus()))
                return Result.fail("不符合上传条件");
        }

        Map<String, StoreOrderStatus> storesMap = pandianUploadStatus.getStores()
            .stream()
            .collect(Collectors.toMap(StoreOrderStatus::getStoresNumber, Function.identity(), (key1, key2) -> key2));

        OSSObject ossObject = null;
        try {
            ossObject = ossClient.getObject(AliOSSConfiguration.AliOSSConfig.BUCKET_NAME, fileKey);
        } catch (OSSException e) {
            logger.error("addFile OSSException：{}，{}，{}，{}", pdNum, fileKey, ossClient.getEndpoint(), e.getMessage());
        } catch (ClientException e) {
            logger.error("addFile ClientException：{}，{}，{}，{}", pdNum, fileKey, ossClient.getEndpoint(), e.getMessage());
        } catch (Exception e) {
            logger.error("addFile Exception：{}，{}，{}，{}", pdNum, fileKey, ossClient.getEndpoint(), e.getMessage());
        }

        if (ossObject == null) return Result.fail("文件加载异常");

        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(ossObject.getObjectContent());
        } catch (IOException e) {
            logger.error("addFile workbook IOException：{}，{}，{}", pdNum, ossObject, e.getMessage());
        } catch (InvalidFormatException e) {
            logger.error("addFile workbook InvalidFormatException：{}，{}，{}", pdNum, ossObject, e.getMessage());
        } catch (Exception e) {
            logger.error("addFile workbook Exception：{}，{}，{}", pdNum, ossObject, e.getMessage());
        } finally {
            try {
                ossObject.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (workbook == null) return Result.fail("文件解析异常");

        long startTime = System.currentTimeMillis();
        Map<String, String> errorMap = new ConcurrentHashMap<>();//标记不符合行
        Map<String, String> goodsMap = new HashMap<>();//用来匹配Excel内重复项
        List<Integer> sheetIndexList = new ArrayList<>();//被处理sheet
        Set<String> storeNumSet = new HashSet<>();//被处理store
        Sheet sheet = null;
        StringBuilder good = null;
        StoreOrderStatus storeStatus = null;
        int totalLimit = 0;
        //遍历每一个sheet
        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
            sheet = workbook.getSheetAt(sheetIndex);
            if (sheet == null || sheet.getLastRowNum() < 1) {//跳过空sheet
                logger.info("sheet：index[{}]，name[{}]，rowNum[{}]；空sheet跳过continue。", sheetIndex, sheet.getSheetName(), sheet.getLastRowNum());
                continue;
            } else if (sheet.getRow(0) == null) {//跳过空标题行sheet
                logger.info("sheet：index[{}]，name[{}]，rowNum[{}]；空表头sheet跳过continue。", sheetIndex, sheet.getSheetName(), sheet.getLastRowNum());
                continue;
            } else {
                Iterator<Row> iterator = null;
                Row row = null;
                Cell cell = null;

                boolean flag = false;//跳过不匹配标题行sheet
                row = sheet.getRow(0);
                for (int i = 0; i < HEADERS.size(); i++) {
                    cell = row.getCell(i);
                    if (cell == null) {
                        flag = true;
                        break;
                    } else {
                        cell.setCellType(CellType.STRING);
                        if (!HEADERS.get(i).equals(StringUtils.trim(cell.getStringCellValue()))) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    logger.info("sheet：index[{}]，name[{}]，rowNum[{}]；未匹配表头sheet跳过continue。", sheetIndex, sheet.getSheetName(), sheet.getLastRowNum());
                    continue;
                } else {
                    iterator = sheet.rowIterator();//遍历检查每一行
                    while (iterator.hasNext()) {//省略空行
                        row = iterator.next();
                        if (row.getRowNum() == 0) //标题行
                            continue;

                        boolean mustHeader = true;//必填项不能为空
                        for (String header : MUST_HEADERS) {
                            cell = row.getCell(HEADERS.indexOf(header), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                            if (cell == null || StringUtils.isBlank(pandianAsyncService.getCellValueParse(cell).trim())) {
                                errorMap.put(sheetIndex + "-" + row.getRowNum(), "必填项不能为空");//未知单元格类型
                                mustHeader = false;
                                break;
                            }
                        }
                        if (mustHeader) {//检查重复项
                            good = new StringBuilder();
                            for (String cellKey : UNIQUE_HEADERS)
                                good.append(pandianAsyncService.getCellValueParse(row.getCell(HEADERS.indexOf(cellKey))).trim());

                            if (goodsMap.containsKey(good.toString())) {
                                errorMap.put(sheetIndex + "-" + row.getRowNum(), "数据重复项");
                                errorMap.put(goodsMap.get(good.toString()), "数据重复项");
                            } else {
                                String storeNum = pandianAsyncService.getCellValueParse(row.getCell(HEADERS.indexOf(header_storenum))).trim();
                                storeStatus = storesMap.get(storeNum);
                                if ((storeStatus == null) || (storeId != null && !storeId.equals(storeStatus.getStoreId()))) {//门店ID
                                    errorMap.put(sheetIndex + "-" + row.getRowNum(), "无法匹配的门店编号");
                                } else if (!UPLOAD_STATUS.contains(storeStatus.getStatus())) {
                                    errorMap.put(sheetIndex + "-" + row.getRowNum(), "门店盘点状态不允许上传");
                                } else {
                                    goodsMap.put(good.toString(), sheetIndex + "-" + row.getRowNum());
                                    storeNumSet.add(storeNum);
                                }
                            }
                        }
                        if (++totalLimit > 15000) {
                            return Result.fail("最多允许上传15000条数据");
                        }
                    }
                }
                sheetIndexList.add(sheetIndex);
            }
        }
        goodsMap = null;
        long parseTime = System.currentTimeMillis();

        if (CollectionUtils.isEmpty(sheetIndexList)) {
            try {
                bPandianFileMapper.insertSelective(new BPandianFile(){{
                    setSiteId(siteId);
                    setStoreId(storeId);
                    setType(option);
                    setOrderId(pandianUploadStatus.getId());
                    setPandianNum(pdNum);
                    setFileUrl(fileURL);
                    //setResUrl();
                    setFileName(fileName);
                    setExt(String.format("未被处理的文件；startTime：%s，parseTime：%s。", startTime, parseTime));
                }});
            } catch (Exception e) {
                logger.error("addFile fileLog：未被处理的文件[{}/{}]，{}，{}", pdNum, fileURL, e.getMessage(), e);
            }
            return Result.fail("未被处理的文件（表格数据不符合模板要求：空sheet、无表头、不匹配表头）");
        } else {
            List<Integer> storeIdList = null;
            try {
                if (CollectionUtils.isNotEmpty(storeNumSet)) {
                    storeIdList = storeNumSet.stream().map(num -> storesMap.get(num).getStoreId()).collect(Collectors.toList());

                    if (option == 0) deleteByStores(siteId, storeIdList, pandianUploadStatus.getId());

                    bPandianOrderStatusMapper.updateStatusByStoreIdList(siteId, pandianUploadStatus.getId(), storeIdList, 1);
                }
            } catch (Exception e) {
                logger.error("addFile OrderStatus：修改门店盘点单状态异常[{}/{}]，{}，{}，{}", pdNum, pandianUploadStatus.getId(), storeNumSet, e.getMessage(), e);
                return Result.fail("操作异常，稍后重试");
            }

            String pdfFlag = new StringBuilder("pdf")
                .append(siteId)
                .append(storeId == null ? 0 : storeId)
                .append(pdNum)
                .append(System.currentTimeMillis())
                .append(new Random().nextInt(1000))
                .toString();

            pandianAsyncService.asyncInsertExcel2DB(siteId, storeId, option, pdNum,
                fileKey, fileName, fileURL, pdfFlag, workbook, sheetIndexList, storesMap, pandianUploadStatus,
                storeNumSet, storeIdList, errorMap, startTime, parseTime, totalLimit);

            return Result.success(pdfFlag);
        }
    }

    public Result getFileRes(String pdfFlag) {
        if (StringUtils.isBlank(pdfFlag)) return Result.fail("缺少必要参数");
        String fileResult = null;
        Map resultMap = null;
        try {
            fileResult = stringRedisTemplate.opsForValue().get(pdfFlag);
            if (fileResult == null) fileResult = stringRedisTemplate.opsForValue().get(pdfFlag + "_row");
            if (fileResult != null) resultMap = JSONObject.parseObject(fileResult, Map.class);
            //stringRedisTemplate.delete(pdfFlag);
        } catch (Exception e) {
            logger.error("getFileRes ：{}，{}, {}, {}, {}", pdfFlag, fileResult, resultMap, e.getMessage(), e);
        }
        return Result.success(resultMap);
    }



    private void deleteByAdd(Integer siteId,Integer storeId,Integer orderId){

        inventoryRepository.deleteByAdd(siteId, storeId, orderId);

        pandianAsyncService.asyncDeleteByAdd2DB(siteId, storeId, orderId);
    }

    private void deleteByStores(Integer siteId, List<Integer> storeIds, Integer orderId) {
        inventoryRepository.deleteByStoreIdList(siteId, orderId, storeIds);
        //pandianAsyncService.asyncDeleteByStoreIdList(siteId, orderId, storeIds);
        bInventoriesMapper.deleteByStoreIdList(siteId, orderId, storeIds);
    }




    public Workbook parseWorkbook(String fileUrl) {
        Workbook workbook = null;
        try {
            URL url = new URL(fileUrl);
            URLConnection urlConnection = url.openConnection();
//            urlConnection.setConnectTimeout(1000 * 5);
//            urlConnection.setReadTimeout(1000 * 5);
//            int fileLength = urlConnection.getContentLength();
//            String filePathName = urlConnection.getURL().getFile();
//            String fileName = filePathName.substring(filePathName.lastIndexOf(File.separatorChar) + 1);
//            logger.info(filePathName + "-" + fileLength);

            try (
                InputStream inputStream = urlConnection.getInputStream();
                PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
            ) {
                workbook = WorkbookFactory.create(pushbackInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workbook;
    }


    public Integer insertBPandinFile(Integer siteId, Integer storeId, Integer orderId, Integer option, String fileUrl, Map<String, Object> params) throws Exception {
        BPandianFile bPandianFile = new BPandianFile();
        bPandianFile.setSiteId(siteId);
        bPandianFile.setStoreId(storeId);
        bPandianFile.setOrderId(orderId);
        bPandianFile.setType(option);
        bPandianFile.setFileUrl(fileUrl);
        bPandianFile.setPandianNum(String.valueOf(params.get("num")));
        bPandianFile.setFileName(String.valueOf(params.get("fileName")));
        bPandianFile.setFileSize(String.valueOf(params.get("fileSize")));
        bPandianFile.setPartTime(String.valueOf(params.get("partTime")));
        bPandianFile.setUpTime(String.valueOf(params.get("upTime")));
//            bPandianFile.setParseTime();
//            bPandianFile.setExt();
//            bPandianFile.setRes();
//            bPandianFile.setResUrl();
        bPandianFileMapper.insertSelective(bPandianFile);
        return bPandianFile.getId();
    }

    @Transactional
    public void createPandianOrder(BPandianPlan plan, List<Integer> storeIdList) throws Exception {
        if (storeIdList == null) {
            storeIdList = bPandianPlanExecutorMapper.getExecStoreIdList(plan.getSiteId(), plan.getId());
        }

        if (storeIdList != null && storeIdList.size() != 0) {
            BPandianOrder pandianOrder = new BPandianOrder();
            pandianOrder.setSiteId(plan.getSiteId());
            pandianOrder.setStoreId(plan.getStoreId());
            pandianOrder.setType(plan.getType());
            pandianOrder.setPlanId(plan.getId());
            pandianOrder.setPandianNum(getPandianNum(plan.getSiteId(), plan.getStoreId(), plan.getType()));
            pandianOrder.setIsUpSite(plan.getUploadType());
            bPandianOrderMapper.insertSelective(pandianOrder);

            List<BPandianOrderStatus> orderStatusList = storeIdList.stream().map(storeId -> {
                BPandianOrderStatus orderStatus = new BPandianOrderStatus();
                orderStatus.setSiteId(plan.getSiteId());
                orderStatus.setPlanId(plan.getId());
                orderStatus.setStoreId(storeId);
                orderStatus.setOrderId(pandianOrder.getId());
                orderStatus.setPandianNum(pandianOrder.getPandianNum());
                return orderStatus;
            }).collect(Collectors.toList());
            bPandianOrderStatusMapper.insertByList(orderStatusList);

            try {
                if(plan.getType() != PandianService.TYPE_STORE)
                    stringRedisTemplate.opsForList().leftPush("pdSite_" + pandianOrder.getSiteId(), pandianOrder.getPandianNum());
                storeIdList.stream().forEach(storeId -> stringRedisTemplate.opsForList().leftPush("pdStore_" + pandianOrder.getSiteId() + "_" + storeId, pandianOrder.getPandianNum()));
            } catch (Exception e) {
                logger.error("设置 pdSite/Store 异常 {} ", e);
            }

            pandianErpDataSync.sendMessageAsync(new PDMessage(){{
                setSiteId(pandianOrder.getSiteId());
                setType(pandianOrder.getType());
                setPlanId(pandianOrder.getPlanId());
                setOrderId(pandianOrder.getId());
                setOrderNum(pandianOrder.getPandianNum());
                setInsertType(0);
            }});
        }
    }

    public Map<String,Object> pdErpSync(Integer siteId, Integer orderId, List<Integer> storeIds) throws Exception {
        return pandianErpDataSync.pdErpSync(siteId, orderId, storeIds);
    }


    public Map<String, Object> updateFileStatus(BPandianFile bPandianFile) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (bPandianFile == null || bPandianFile.getId() == null) {
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }
        bPandianFileMapper.updateByPrimaryKeySelective(bPandianFile);
        result.put("status", "OK");
        return result;
    }

    public Map<String, Object> reportBef(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params == null || StringUtil.isEmpty(params.get("id")) || StringUtil.isEmpty(params.get("option")) || StringUtil.isEmpty(params.get("type"))
            || StringUtil.isEmpty(params.get("siteId")) || StringUtil.isEmpty(params.get("num"))) {
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }
        Integer type = Integer.parseInt(String.valueOf(params.get("type")));
        if (type == PandianService.TYPE_STORE && StringUtil.isEmpty(params.get("storeId"))) {//门店设置
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }
        List<Integer> storeIdList = null;
        try {
            if (!StringUtil.isEmpty(params.get("storeIds"))) {
                storeIdList = (List<Integer>) JSON.parse((String) params.get("storeIds"));
            }
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "参数类型异常");
            return result;
        }

        Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
        Integer storeId = !StringUtil.isEmpty(params.get("storeId")) ? Integer.parseInt(String.valueOf(params.get("storeId"))) : null;
        Integer id = Integer.parseInt(String.valueOf(params.get("id")));
        Integer option = Integer.parseInt(String.valueOf(params.get("option")));//0-all 1-query
        if (option.equals(1)) {
            //
        }

        Page<Map> page = PageHelper.startPage(1, 1);
        List<Map<String, Object>> bInventoriesList = bInventoriesMapper.getInventoriesList(siteId, storeIdList, id, null);
        page.toPageInfo().getList().clear();
        result.put("pageInfo", page.toPageInfo());
        result.put("status", "OK");
        return result;
    }

    public File exportReport(Map<String, Object> params) throws Exception {
        Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
        Integer storeId = !StringUtil.isEmpty(params.get("storeId")) ? Integer.parseInt(String.valueOf(params.get("storeId"))) : null;
        Integer id = Integer.parseInt(String.valueOf(params.get("id")));
        Integer option = Integer.parseInt(String.valueOf(params.get("option")));//0-all 1-query
        Map<String, Object> conditionMap = null;
        if (option.equals(1)) {
            conditionMap = new HashMap<>();
        }
        List<Integer> storeIdList = null;
        try {
            logger.info("storeIds---------"+params.get("storeIds"));
            if (!StringUtil.isEmpty(params.get("storeIds"))) {
                storeIdList =(List<Integer>)params.get("storeIds");
                //storeIdList = (List<Integer>) JSON.parse((String) params.get("storeIds"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        List<String> header = Arrays.asList("门店编号", "门店名称", "商品编号", "商品名称", "批号", "规格", "生产厂家",
            "账面库存", "实际盘点", "盈亏", "异常", "盘点人", "监盘人", "审批人", "操作时间", "顺序号");
        List<String> cols = Arrays.asList("store_num", "store_name", "goods_code", "drug_name", "batch_number", "specif_cation", "goods_company",
            "inventory_accounting", "actual_store", "difference", "quality", "check_person", "supervisor", "approver", "update_time", "score");

        File file = new File(temp_dir + "/report_" + System.currentTimeMillis() + ".xls");
        file.getParentFile().mkdirs();
        int pageNum = 0, pageSize = 2000, pages = 0;
        Page page = null;
        HSSFWorkbook hssfWorkbook = ExcelOperator.createWorkbook("data", header);
        do {
            boolean isCount = false;
            if (page == null) {
                isCount = true;
                page = PageHelper.startPage(++pageNum, pageSize, isCount);
            } else {
                //避免每次都发起Count查询
                PageHelper.startPage(++pageNum, pageSize, isCount);
            }
            List<Map<String, Object>> bInventoriesList = null;
            try {
                bInventoriesList = bInventoriesMapper.getInventoriesList(siteId, storeIdList, id, conditionMap);
                if (isCount) {
                    pages = page.getPages();
                }
            } catch (Exception e) {
                //System.out.println(e);
                logger.error(e.getMessage(), e);
            }
            if (bInventoriesList == null)
                bInventoriesList = new ArrayList<>();
            ExcelOperator.writeData(hssfWorkbook, cols, bInventoriesList, ExcelOperator.createContentAreaStyle(hssfWorkbook));
            FileOutputStream out = new FileOutputStream(file);
            hssfWorkbook.write(out);
            out.close();
        } while (pages > pageNum);
        return file;
    }

    public Map<String, Object> updateUpSite(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params == null || StringUtil.isEmpty(params.get("id")) || StringUtil.isEmpty(params.get("siteId")) || StringUtil.isEmpty(params.get("isUpSite"))) {
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
        Integer id = Integer.parseInt(String.valueOf(params.get("id")));
        Integer isUpSite = Integer.parseInt(String.valueOf(params.get("isUpSite")));//0-store 1-site
        Integer planOperator = Integer.parseInt(String.valueOf(params.get("planOperator")));

        if (isUpSite == 0 || isUpSite == 1) {
            Integer i = bPandianOrderMapper.getUploadByStatus(siteId, id, isUpSite ^ 1, null);
            if (i == null) {
                bPandianOrderMapper.updateIsUpSite(siteId, id, isUpSite);
            } else {
                result.put("status", "ERROR");
                result.put("message", "文件上传中，稍后重试");
                return result;
            }
        } else {
            result.put("status", "ERROR");
            result.put("message", "无效参数");
            return result;
        }

        try {
            insertPandianPlanLog(siteId, null, 0, null, planOperator, JSON.toJSONString(params), "上传设置");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("updateUpSite 日志异常 {}", e);
        }
        result.put("status", "OK");
        return result;
    }



    //获取盘点单的状态

    /**
     * @param s siteId,storeId,pandian_num
     * @return
     */
    public Map<String, Object> getPanDianOrderStatus(StatusParam s) {
        Map<String, Object> result = new HashMap<>();
        Integer status = bPandianOrderStatusMapper.getStatus(s);
        result.put("status", "OK");
        result.put("data", status);
        return result;
    }

    public Map<String,Object> getPDStoreList(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params == null || StringUtil.isEmpty(params.get("siteId"))) {
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }
        int pageNum = StringUtil.isEmpty(params.get("pageNum")) ? 1 : Integer.parseInt(String.valueOf(params.get("pageNum")));
        int pageSize = StringUtil.isEmpty(params.get("pageSize")) ? 15 : Integer.parseInt(String.valueOf(params.get("pageSize")));

        Page<Map> page = PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> storeList = bPandianPlanMapper.getPDStoreList(params);

        result.put("pageInfo", page.toPageInfo());
        result.put("storelist", page.toPageInfo());
        result.put("status", "OK");
        return result;
    }

    public Map<String,Object> getPDClerkList(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params == null || StringUtil.isEmpty(params.get("siteId"))) {
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }
        int pageNum = StringUtil.isEmpty(params.get("pageNum")) ? 1 : Integer.parseInt(String.valueOf(params.get("pageNum")));
        int pageSize = StringUtil.isEmpty(params.get("pageSize")) ? 15 : Integer.parseInt(String.valueOf(params.get("pageSize")));

        Page<Map> page = PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> clerkList = bPandianPlanMapper.getPDClerkList(params);

        result.put("pageInfo", page.toPageInfo());
        result.put("status", "OK");
        return result;
    }

    @Async
    public Future<Map<String, Object>> getPDRemindFuture(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params == null || StringUtil.isEmpty(params.get("type")) || StringUtil.isEmpty(params.get("siteId"))) {
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return new AsyncResult<Map<String, Object>>(result);
        }
        Integer type = Integer.parseInt(String.valueOf(params.get("type")));
        if (type == PandianService.TYPE_STORE && StringUtil.isEmpty(params.get("storeId"))) {
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return new AsyncResult<Map<String, Object>>(result);
        }

        List<String> pdNumList = null;
        StringBuilder key = new StringBuilder();
        if(type == PandianService.TYPE_STORE){
            key.append("pdStore_").append(String.valueOf(params.get("siteId"))).append("_").append(String.valueOf(params.get("storeId")));
        }else {
            key.append("pdSite_").append(String.valueOf(params.get("siteId")));
        }

        pdNumList = getListByKey(key.toString());

        result.put("pdNumList", pdNumList);
        result.put("status", "OK");
        return new AsyncResult<Map<String, Object>>(result);
    }

    public Map<String,Object> getPDRemind(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        Future<Map<String, Object>> future = getPDRemindFuture(params);
        try {
            result = future.get();
        } catch (InterruptedException e) {
            logger.debug("InterruptedException 异常");
            result.put("status", "ERROR");
        } catch (ExecutionException e) {
            logger.debug("ExecutionException 异常");
            result.put("status", "ERROR");
        }
        return result;
    }



    private List<String> getListByKey(String key) {
        List<String> list = new ArrayList<>();
        while (true) {
            String value = null;
            try {
                value = stringRedisTemplate.opsForList().rightPop(key);
            } catch (Exception e) {
                logger.error("getListByKey异常 {} ", e);
            }
            if (value == null) {
                break;
            } else {
                list.add(value);
            }
        }
        return list;
    }

    public Set<String> testInfo(Integer siteId, String pdNum) {
        String fileKey = bPandianFileMapper.getFileUrlByPDNum(siteId, pdNum);
        Set<String> result = new HashSet<>();
        if (StringUtils.isNotBlank(fileKey)) result = RedisUtil.setGetAllValue(fileKey);
        return result;
    }

    public Integer planOrderNum(Integer id) {
        return bPandianPlanMapper.planOrderNum(id);
    }

}

