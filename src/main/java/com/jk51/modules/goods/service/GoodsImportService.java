package com.jk51.modules.goods.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.goods.BGoodsSynchroLog;
import com.jk51.model.goods.Category;
import com.jk51.model.goods.PageData;
import com.jk51.model.goods.YbImagesAttr;
import com.jk51.modules.goods.library.BatchResult;
import com.jk51.modules.goods.library.GoodsImportProperty;
import com.jk51.modules.goods.library.GoodsImportXlsHelper;
import com.jk51.modules.goods.library.GoodsStatusConv;
import com.jk51.modules.goods.mapper.*;
import com.jk51.modules.goods.request.BatchImportDto;
import com.jk51.modules.goods.request.GoodsData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.partitioningBy;

@Service
public class GoodsImportService {
    private static final Logger logger = LoggerFactory.getLogger(GoodsImportService.class);

    @Autowired
    private YbGoodsMapper ybGoodsMapper;

    @Autowired
    private GoodsmMapper goodsmMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private YbCategoryMapper ybCategoryMapper;

    // 这里最多会存MAX_NUM条商品数据
    private Map<String, PageData> __ybGoodsInfoCache = new ConcurrentHashMap();
    private Map<String, Integer> __barndIdCache = new ConcurrentHashMap();
    private BatchImportDto batchImportDto;

    private final static Map<String,String> uploadStatus = new HashMap(){{
            put("1","上传成功");
            put("2","匹配成功");
            put("3","重复匹配");
            put("4","匹配失败");
            put("5","上传失败");
        }
    };

    public BatchResult batchImportTask(BatchImportDto batchImportDto) throws Exception {
        this.batchImportDto = batchImportDto;
        BatchResult batchResult = new BatchResult();
        GoodsImportXlsHelper gixHelper = new GoodsImportXlsHelper();
        gixHelper.open(batchImportDto.getFileurl());
        gixHelper.checkFile(batchImportDto.getDetailTpl(), batchImportDto.getOption());

        final AtomicInteger ai = new AtomicInteger();

        Sheet activeSheet = gixHelper.getActiveSheet();
        Long startTimeMillis = System.currentTimeMillis();
        List errorList = gixHelper.mapSkipTitle(activeSheet, row -> {
            Map errorMap = new HashMap();
            try {
                GoodsData goodsData = handleRow(row);
                if (goodsData != null
                        && goodsData.getGoodsId() != null
                        && goodsData.getGoodsId() > 0
                        && goodsData.getGoodsStatus() != null
                        && goodsData.getGoodsStatus() == GoodsStatusConv.STATUS_SOFTDEL) {
                    // 回收站的商品处理
                    goodsService.delisting(goodsData.getGoodsId(), batchImportDto.getSiteId(),null);
                } else {
                    // 这里可以考虑将添加改成添加到一个list之后一次性处理  更新就只能一个一个的更新了
                    // 字段校验
                    if (goodsData.getGoodsId() != null && goodsData.getGoodsId() > 0) {
                        saveImportData(goodsData, true, batchImportDto.getUse51());
                    } else {
                        saveImportData(goodsData, false, batchImportDto.getUse51());
                    }
                }
                ai.incrementAndGet();
                return null;
            } catch (RuntimeException e) {
                logger.debug(e.toString());
                errorMap.put("rownum", String.valueOf(row.getRowNum()));
                errorMap.put("reason", e.getMessage());
            } catch (Exception e) {
                logger.debug(e.getMessage());
                errorMap.put("rownum", String.valueOf(row.getRowNum()));
                errorMap.put("reason", "未知的错误原因");
            }

            return errorMap;
        }, true).filter(map -> map != null).collect(Collectors.toList());
        batchResult.setErrorList(errorList);
        Long endTimeMillis = System.currentTimeMillis();
        batchResult.setSuccessNum(ai.get());
        batchResult.setFailNum(batchResult.getErrorList().size());
        logger.info("处理{}条记录用时{}", batchResult.getSuccessNum() + batchResult.getFailNum(), endTimeMillis - startTimeMillis);

        return batchResult;
    }

    private GoodsData handleRow(Row row) throws Exception {
        Map<String, String> goodsInfo = GoodsImportProperty.conv2GoodsInfo(row, batchImportDto.getDetailTpl());
        goodsInfo.put("detail_tpl", String.valueOf(batchImportDto.getDetailTpl()));
        goodsInfo.put("site_id", String.valueOf(batchImportDto.getSiteId()));
        boolean isAdd = StringUtil.equalsIgnoreCase("add", batchImportDto.getOption());
        // 检查必填字段
        GoodsImportProperty.checkRequire(isAdd, goodsInfo);

        if (batchImportDto.getUse51() && isAdd) {
            String cacheKey = String.format("%s#%s#%s",
                    goodsInfo.get("approval_number"),
                    goodsInfo.get("specif_cation"),
                    String.valueOf(batchImportDto.getDetailTpl()));
            PageData ybGoodsInfo;
            if (__ybGoodsInfoCache.containsKey(cacheKey)) {
                ybGoodsInfo = __ybGoodsInfoCache.get(cacheKey);
            } else {
                // 查询yb_goods数据
                ybGoodsInfo = ybGoodsMapper.queryYbGoodByPzwh(
                        goodsInfo.get("approval_number"),
                        String.valueOf(batchImportDto.getDetailTpl()));
                if (ybGoodsInfo != null) {
                    __ybGoodsInfoCache.put(cacheKey, ybGoodsInfo);
                }
            }

            if (ybGoodsInfo != null && StringUtil.equals("2", String.valueOf(ybGoodsInfo.get("goods_status")))) {
                ybGoodsInfo.put(cacheKey, ybGoodsInfo);
                Map<String, String> ybGoodsInfoMap = JacksonUtils.getInstance().convertValue(ybGoodsInfo, Map.class);
                goodsInfo.put("yb_goods_id", String.valueOf(ybGoodsInfo.get("goods_id")));
                // 处理分类
                String cateidKey = "user_cate_id";
                if (StringUtil.isEmpty(goodsInfo.get(cateidKey)) && StringUtil.isNumeric(ybGoodsInfoMap.get("user_cateid"))) {
                    Map<String, String> categoryMap = ybCategoryMapper.join51jkByCode(Long.parseLong(ybGoodsInfoMap.get("user_cateid")), batchImportDto.getSiteId());
                    if (categoryMap != null) {
                        goodsInfo.put(cateidKey, categoryMap.get("cate_code"));
                    }
                }

                goodsInfo = GoodsImportProperty.merge(goodsInfo, ybGoodsInfoMap);
            }
        }
        if (isAdd) {
            // 查找商品
            Map selectParam = new HashMap();
            selectParam.put("approvalNumber", goodsInfo.get("approval_number"));
            int recy = Optional.ofNullable(batchImportDto.getRecy()).orElse(0);
            String goodsStatus = recy == 0 ? "1,2" : "1,2,4";
            selectParam.put("goodsStatus", goodsStatus);
            selectParam.put("siteId", batchImportDto.getSiteId());
            Map<String, String> goods = goodsMapper.getGoodsD(selectParam);

            if (goods != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                // 忽略未知字段
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                // 禁用注解
                objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);

                GoodsData goodsData = objectMapper.convertValue(goods, GoodsData.class);
                if (goodsData.getGoodsStatus() != null && goodsData.getGoodsStatus() == GoodsStatusConv.STATUS_SOFTDEL) {
                    if (recy != 0) {
                        // 更新回收站 当成商品更新处理
                        goodsInfo.put("goods_id", goods.get("goodsId"));
                        // 字段处理
                        emptyStrToNull(goodsInfo);
                    }
                    // 数据库中已经有记录了 查看回收站设置 如果回收站设置是还原则更新状态
                    if (recy == 1) {
                        goodsInfo.put("goods_status", String.valueOf(GoodsStatusConv.STATUS_DELISTING));
                    }
                } else {
                    throw new RuntimeException("批准文号已存在");
                }

            } else {
                // 导入的状态是下架
                goodsInfo.put("goods_status", String.valueOf(GoodsStatusConv.STATUS_DELISTING));
                // region 数据库非空又没有默认值的字段处理
                if (StringUtil.isEmpty(goodsInfo.get("market_price"))) {
                    goodsInfo.put("market_price", "0");
                }
                if (StringUtil.isEmpty(goodsInfo.get("shop_price"))) {
                    goodsInfo.put("shop_price", "0");
                }
                // endregion
            }
        } else {
            // 商品更新
            Map<String, String> oldGoodsInfo = goodsMapper.queryGoodsWithGoodsCode(goodsInfo.get("goods_code"), batchImportDto.getSiteId());
            if (oldGoodsInfo == null) {
                throw new RuntimeException("根据商品编码没有找到对应的记录");
            }
            goodsInfo.put("goods_id", oldGoodsInfo.get("goods_id"));
            // 获取批次
            Cell batchNoCell = row.getCell(row.getLastCellNum() - 1);
            Integer batchNoStr = GoodsImportXlsHelper.getCellValueParse(batchNoCell, Integer.class);
            if (batchNoStr != null && batchNoStr != 0) {
                goodsInfo.put("goods_batch_no", String.valueOf(batchNoStr));
            }
            /*
             * 将所有空字符串的字段设置为null mapper里面写的是字段名!=null
             * <if test="approvalNumber != null">
             *   `approval_number` = #{approvalNumber},
             * </if>
             */
            emptyStrToNull(goodsInfo);
        }
        GoodsData goodsData = JacksonUtils.map2pojo(goodsInfo, GoodsData.class);
        // 商品编码和条形码唯一
        if (StringUtil.isNotEmpty(goodsData.getGoodsCode()) || StringUtil.isNotEmpty(goodsData.getBarCode())) {
            if (goodsData.getGoodsId() != null && goodsData.getGoodsId() > 0) {
                //20180914
                // 更新是根据商品编码的 这里只需要判断条形码是不是唯一的
                /*if (StringUtil.isNotEmpty(goodsData.getBarCode())) {
                    int total = goodsMapper.countBarCodeNotGoodsCode(goodsData.getGoodsCode(), goodsData.getBarCode(), batchImportDto.getSiteId());
                    if (total > 0) {
                        throw new RuntimeException("商品条形码已存在");
                    }
                }*/
            } else {
                Map<String, Integer> uniqueMap = goodsMapper.queryGoodsCodeAndBarCodeCount(goodsData.getGoodsCode(), null, batchImportDto.getSiteId());
                //int barCodeTotal = Integer.parseInt(String.valueOf(uniqueMap.get("barCodeTotal")));
                int goodsCodeTotal = Integer.parseInt(String.valueOf(uniqueMap.get("goodsCodeTotal")));
                /*if (barCodeTotal > 0) {
                    throw new RuntimeException("商品条形码已存在");
                }*/
                if (goodsCodeTotal > 0) {
                    throw new RuntimeException("商品编码已存在");
                }
            }
        }
        // 处理分类
        bindCategory(goodsData,isAdd);
        // 处理品牌
        goodsData.setBarndId(getBarndId(goodsInfo.get("barnd_name")));

        return goodsData;
    }

    public BatchResult batchImportTaskNew(BatchImportDto batchImportDto) throws Exception {
        Long startTimeMillis = System.currentTimeMillis();
        this.batchImportDto = batchImportDto;
        BatchResult batchResult = new BatchResult();
        GoodsImportXlsHelper gixHelper = new GoodsImportXlsHelper();
        gixHelper.open(batchImportDto.getFileurl());
        gixHelper.checkFile(batchImportDto.getDetailTpl(), batchImportDto.getOption());

        final AtomicInteger ai = new AtomicInteger();
        final AtomicInteger _ai = new AtomicInteger();
        final AtomicInteger marryFail_ai = new AtomicInteger();
        final AtomicInteger repeatMarry_ai = new AtomicInteger();

        Sheet activeSheet = gixHelper.getActiveSheet();


        //单个表格重复匹配，用成员变量了
        Map<String, PageData> ybGoodsInfoCache = new ConcurrentHashMap();
        Map<String, Object>  goodsCodeCache = new ConcurrentHashMap();

        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue(1000);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 30, 10L, TimeUnit.SECONDS, queue);

        Iterator<Row> it = activeSheet.rowIterator();

        List errorList = new ArrayList();

        while (it.hasNext()){
            Row row = it.next();
            if(row.getRowNum() == 0){
                continue;
            }
            executor.execute(() -> {
                Map errorMap = new HashMap();
                try {
                    Map<String,Object> goodsMap= handleRowNew(row,ybGoodsInfoCache,goodsCodeCache).get();

                    GoodsData  goodsData = (GoodsData)goodsMap.get("goodsData");

                    int goodId;

                    if (goodsData.getGoodsId() != null && goodsData.getGoodsId() > 0) {
                        goodId = saveImportData(goodsData, true, batchImportDto.getUse51()).get();
                    } else {
                        goodId = saveImportData(goodsData, false, batchImportDto.getUse51()).get();
                    }

                    ai.incrementAndGet();

                    if(goodsMap.get("reasonMap") != null){
                        errorMap = (Map)goodsMap.get("reasonMap");
                        if("匹配失败".equals(errorMap.get("reason").toString())){
                            marryFail_ai.incrementAndGet();
                            try {
                                if(goodId > 0){
                                    BGoodsSynchroLog goodsSynchroLog = new BGoodsSynchroLog();
                                    goodsSynchroLog.setSite_id(goodsData.getSiteId());
                                    goodsSynchroLog.setGoods_id(goodsData.getGoodsId());
                                    goodsSynchroLog.setOperate_type(3);
                                    goodsSynchroLog.setOperate_status(2);
                                    goodsSynchroLog.setSynchro_type(1);
                                    goodsSynchroLog.setSynchro_status(1);

                                    this.insertGoodsSynchro(goodsSynchroLog);
                                    //goodsMapper.insertGoodsSynchro(goodsSynchroLog);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if("重复匹配".equals(errorMap.get("reason").toString())){
                            repeatMarry_ai.incrementAndGet();
                        }
                        errorMap.put("rownum", String.valueOf(row.getRowNum()));
                        _ai.incrementAndGet();
                    }
                } catch (RuntimeException e) {
                    logger.debug(e.toString());
                    errorMap.put("rownum", String.valueOf(row.getRowNum()));
                    errorMap.put("reason", e.getMessage());
                } catch (Exception e) {
                    logger.debug(e.getMessage());
                    errorMap.put("rownum", String.valueOf(row.getRowNum()));
                    errorMap.put("reason", "未知的错误原因");
                }

                if(!errorMap.isEmpty()){
                    errorList.add(errorMap);
                }

            });
            int threadSize = queue.size();
            logger.info("线程队列大小为-->"+threadSize);
        }

        executor.shutdown();
        while (!executor.awaitTermination(1000L, TimeUnit.MILLISECONDS));

        batchResult.setErrorList(errorList);
        batchResult.setSuccessNum(ai.get());
        batchResult.setFailNum(batchResult.getErrorList().size()-_ai.get());
        batchResult.setMarrySuccessNum(ai.get()-marryFail_ai.get());
        batchResult.setMarryFailNum(marryFail_ai.get());
        batchResult.setRepeatMarryNum(repeatMarry_ai.get());
        Long endTimeMillis = System.currentTimeMillis();
        logger.info("处理{}条记录用时：{}秒", batchResult.getSuccessNum() + batchResult.getFailNum(), (endTimeMillis - startTimeMillis)/1000);
        return batchResult;
    }
    @Async
    private Future<Integer> insertGoodsSynchro (BGoodsSynchroLog bGoodsSynchroLog){
        int i=goodsMapper.insertGoodsSynchro(bGoodsSynchroLog);
        return new AsyncResult<>(i);
    }
//    public BatchResult batchImportTaskNew(BatchImportDto batchImportDto) throws Exception {
//        this.batchImportDto = batchImportDto;
//        BatchResult batchResult = new BatchResult();
//        GoodsImportXlsHelper gixHelper = new GoodsImportXlsHelper();
//        gixHelper.open(batchImportDto.getFileurl());
//        gixHelper.checkFile(batchImportDto.getDetailTpl(), batchImportDto.getOption());
//
//        final AtomicInteger ai = new AtomicInteger();
//        final AtomicInteger _ai = new AtomicInteger();
//        final AtomicInteger marryFail_ai = new AtomicInteger();
//        final AtomicInteger repeatMarry_ai = new AtomicInteger();
//
//
//        Sheet activeSheet = gixHelper.getActiveSheet();
//        Long startTimeMillis = System.currentTimeMillis();
//
//        //单个表格重复匹配，用成员变量了
//        Map<String, PageData> ybGoodsInfoCache = new ConcurrentHashMap();
//        Map<String, Object>  goodsCodeCache = new ConcurrentHashMap();
//
//        List errorList = gixHelper.mapSkipTitle(activeSheet, row -> {
//            Map errorMap = new HashMap();
//            try {
//                Map<String,Object> goodsMap= handleRowNew(row,ybGoodsInfoCache,goodsCodeCache);
////                if (goodsData != null
////                        && goodsData.getGoodsId() != null
////                        && goodsData.getGoodsId() > 0
////                        && goodsData.getGoodsStatus() != null
////                        && goodsData.getGoodsStatus() == GoodsStatusConv.STATUS_SOFTDEL) {
////                    // 回收站的商品处理
////                    goodsService.delisting(goodsData.getGoodsId(), batchImportDto.getSiteId());
////                } else {
//                    // 这里可以考虑将添加改成添加到一个list之后一次性处理  更新就只能一个一个的更新了
//                    // 字段校验
//
//                    GoodsData  goodsData = (GoodsData)goodsMap.get("goodsData");
//
//                    int goodId;
//
//                    if (goodsData.getGoodsId() != null && goodsData.getGoodsId() > 0) {
//                        goodId = saveImportData(goodsData, true, batchImportDto.getUse51());
//                    } else {
//                        goodId = saveImportData(goodsData, false, batchImportDto.getUse51());
//                    }
////                }
//                ai.incrementAndGet();
//
//                if(goodsMap.get("reasonMap") != null){
//                    errorMap = (Map)goodsMap.get("reasonMap");
//                    if("匹配失败".equals(errorMap.get("reason").toString())){
//                        marryFail_ai.incrementAndGet();
//                        try {
//                            if(goodId > 0){
//                                BGoodsSynchroLog goodsSynchroLog = new BGoodsSynchroLog();
//                                goodsSynchroLog.setSite_id(goodsData.getSiteId());
//                                goodsSynchroLog.setGoods_id(goodsData.getGoodsId());
//                                goodsSynchroLog.setOperate_type(3);
//                                goodsSynchroLog.setOperate_status(2);
//                                goodsSynchroLog.setSynchro_type(1);
//                                goodsSynchroLog.setSynchro_status(1);
//
//                                goodsMapper.insertGoodsSynchro(goodsSynchroLog);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }else if("重复匹配".equals(errorMap.get("reason").toString())){
//                        repeatMarry_ai.incrementAndGet();
//                    }
//                    errorMap.put("rownum", String.valueOf(row.getRowNum()));
//                    _ai.incrementAndGet();
//                }else {
//                    return null;
//                }
//            } catch (RuntimeException e) {
//                logger.debug(e.toString());
//                errorMap.put("rownum", String.valueOf(row.getRowNum()));
//                errorMap.put("reason", e.getMessage());
//            } catch (Exception e) {
//                logger.debug(e.getMessage());
//                errorMap.put("rownum", String.valueOf(row.getRowNum()));
//                errorMap.put("reason", "未知的错误原因");
//            }
//            return errorMap;
//        }, true).filter(map -> map != null).collect(Collectors.toList());
//        batchResult.setErrorList(errorList);
//        Long endTimeMillis = System.currentTimeMillis();
//        batchResult.setSuccessNum(ai.get());
//        batchResult.setFailNum(batchResult.getErrorList().size()-_ai.get());
//        batchResult.setMarrySuccessNum(ai.get()-marryFail_ai.get());
//        batchResult.setMarryFailNum(marryFail_ai.get());
//        batchResult.setRepeatMarryNum(repeatMarry_ai.get());
//        logger.info("处理{}条记录用时{}", batchResult.getSuccessNum() + batchResult.getFailNum(), endTimeMillis - startTimeMillis);
//
//        return batchResult;
//    }

    @Async
    public Future<Map<String,Object>> handleRowNew(Row row,Map<String, PageData> ybGoodsInfoCache,Map<String, Object>  goodsCodeCache) throws Exception {

        Map<String, String> goodsInfo = GoodsImportProperty.conv2GoodsInfo(row, batchImportDto.getDetailTpl());

        if(goodsCodeCache.containsKey(goodsInfo.get("goods_code"))){
            throw new RuntimeException("该商品编码已经存在");
        }

        goodsCodeCache.put(goodsInfo.get("goods_code"),"");

        boolean isAdd = StringUtil.equalsIgnoreCase("add", batchImportDto.getOption());

        // 检查必填字段
        GoodsImportProperty.checkRequireNew(isAdd,batchImportDto.getDetailTpl() + "",goodsInfo);

        goodsInfo.put("detail_tpl", String.valueOf(batchImportDto.getDetailTpl()));
        goodsInfo.put("site_id", String.valueOf(batchImportDto.getSiteId()));

        Map<String,String> reasonMap = null;

        if (isAdd) {
            // 查找商家库商品（按批文号）
            Map selectParam = new HashMap();
            selectParam.put("goods_code", goodsInfo.get("goods_code"));
            String goodsStatus = "1,2";
            selectParam.put("goodsStatus", goodsStatus);
            selectParam.put("siteId", batchImportDto.getSiteId());
            List<Map<String, String>> goodsList = goodsMapper.getGoodsDList(selectParam);

            if(goodsList != null && goodsList.size() > 0){
                throw new RuntimeException("该商品编码已经存在");
            }

            if (batchImportDto.getUse51()) {

                String cacheKey = String.format("%s#%s#%s",
                        goodsInfo.get("bar_code"),
                        goodsInfo.get("approval_number"),
                        String.valueOf(batchImportDto.getDetailTpl()));

                PageData ybGoodsInfo;

                if(StringUtil.isEmpty(goodsInfo.get("bar_code")) && StringUtil.isEmpty(goodsInfo.get("approval_number"))){
                    //不会匹配到
                    ybGoodsInfo = null;
                }else if (ybGoodsInfoCache.containsKey(cacheKey)) {
                    ybGoodsInfo = ybGoodsInfoCache.get(cacheKey);
                    reasonMap = new HashMap<>();
                    reasonMap.put("reason", "重复匹配");
                }else {
                    // 查询yb_goods数据
                    final String barCode = goodsInfo.get("bar_code");
                    String approvalNumber = goodsInfo.get("approval_number");

                    //一次性查出来，减少查询次数，将图片数也查出来
                    List<PageData> pageDataListForBarCodeORApprovalNumber = ybGoodsMapper
                            .queryYbGoodByBarCodeORApprovalNumber(barCode, approvalNumber, String.valueOf(batchImportDto.getDetailTpl()));

                    //是否可以和51jk库商品匹配到
                    if(pageDataListForBarCodeORApprovalNumber == null || pageDataListForBarCodeORApprovalNumber.size() == 0){
                        //未匹配到
                        ybGoodsInfo = null;
                    }else if (pageDataListForBarCodeORApprovalNumber.size() == 1 && pageDataListForBarCodeORApprovalNumber.get(0).get("goods_id") == null ){
                        //未匹配到
                        ybGoodsInfo = null;
                    }else{
                        ybGoodsInfo = getPageData(pageDataListForBarCodeORApprovalNumber,
                                (truePageDataList,falsePageDataList) ->{
                                    //判断主流程是否满足
                                    if(truePageDataList == null || truePageDataList.size() == 0){
                                        return falsePageDataList;
                                    }else{
                                        return truePageDataList;
                                    }
                                },
                                (pageData -> pageData.getString("bar_code").equals(barCode)),//含有条形码和批文号的分组条件
                                (pageData -> pageData.getInt("updateStatus") == 2),//是否锁定的分组条件
                                (pageData -> pageData.getInt("imgCount") > 0 )//是否含有
                        );
                    }
                }

                if (ybGoodsInfo != null) {
                    ybGoodsInfoCache.put(cacheKey, ybGoodsInfo);
                }else {
                    reasonMap = new HashMap<>();
                    reasonMap.put("reason", "匹配失败");
                }

                //产品状态 1(未审核 不允许商家使用), 2(已审核 允许商家使用), 3(软删除), 10=初审
                String goods_status_examine = "2";

                if (ybGoodsInfo != null && StringUtil.equals(goods_status_examine, String.valueOf(ybGoodsInfo.get("goods_status")))) {
                    ybGoodsInfo.put(cacheKey, ybGoodsInfo);
                    Map<String, String> ybGoodsInfoMap = JacksonUtils.getInstance().convertValue(ybGoodsInfo, Map.class);
                    goodsInfo.put("yb_goods_id", String.valueOf(ybGoodsInfo.get("goods_id")));
                    // 处理分类
                    String cateidKey = "user_cate_id";
                    if (StringUtil.isEmpty(goodsInfo.get(cateidKey)) && StringUtil.isNumeric(ybGoodsInfoMap.get("user_cateid"))) {
                        Map<String, String> categoryMap = ybCategoryMapper.join51jkByCode(Long.parseLong(ybGoodsInfoMap.get("user_cateid")), batchImportDto.getSiteId());
                        if (categoryMap != null) {
                            goodsInfo.put(cateidKey, categoryMap.get("cate_code"));
                        }
                    }

                    goodsInfo = GoodsImportProperty.merge(goodsInfo, ybGoodsInfoMap);
                }
            }

            if (StringUtil.isEmpty(goodsInfo.get("market_price"))) {
                goodsInfo.put("market_price", "0");
            }

        } else {
            throw new RuntimeException("该商品更新状态不正确");
        }

        GoodsData goodsData = JacksonUtils.map2pojo(goodsInfo, GoodsData.class);

        // 处理分类
        bindCategory(goodsData,isAdd);
        // 处理品牌
        goodsData.setBarndId(getBarndId(goodsInfo.get("barnd_name")));

        Map<String,Object> resultMap = new HashMap<String,Object>();

        resultMap.put("goodsData",goodsData);
        resultMap.put("reasonMap",reasonMap);
        return new AsyncResult<>(resultMap);
        //return resultMap;
    }

    private PageData getPageData(List<PageData> pageDataList, BiFunction<List<PageData>,List<PageData>,List<PageData>> pageDataFunction, Predicate<PageData>... pageDataPredicate){

        for ( AtomicInteger i = new AtomicInteger(0) ; pageDataPredicate.length - 1 > i.get(); i.incrementAndGet()){
            //分区
            Map<Boolean,List<PageData>> pageDataListMap = pageDataList.parallelStream()
                    .sorted((pageData1,pageData2) -> (((Date)pageData1.get("update_time")).before((Date)pageData2.get("update_time")))? 1 : -1)
                    .collect(partitioningBy(pageDataPredicate[i.get()]));

            List<PageData> truePageDataList = pageDataListMap.get(true);
            List<PageData> falsePageDataList = pageDataListMap.get(false);

            pageDataList =  pageDataFunction.apply(truePageDataList,falsePageDataList);

            if(pageDataList.size() == 1){
                return pageDataList.get(0);
            }
        }

        return null;
    }

//    private PageData getPageData(List<PageData> pageDataListForBarCodeORApprovalNumber,String barCode){
//        PageData ybGoodsInfo = null;
//
//        //分区 按更新时间排序
//                Map<Boolean,List<PageData>> pageDataListMap = pageDataListForBarCodeORApprovalNumber.parallelStream()
//                        .sorted((pageData1,pageData2) -> (((Date)pageData1.get("update_time")).before((Date)pageData2.get("update_time")))? 1 : -1)
//                        .collect(partitioningBy(pageData -> pageData.getString("bar_code").equals(barCode)));
//
//                //含有条形码的51jk库商品
//                List<PageData> barCodePageDataList = pageDataListMap.get(true);
//                //含有批文号的51jk库商品
//                List<PageData> approvalNumberPageDataList = pageDataListMap.get(false);
//
//                //条形码优先判断
//                if(barCodePageDataList == null || barCodePageDataList.size() == 0){
//                    //无条形码的51jk库商品
//                    if(approvalNumberPageDataList != null){
//                        //批文号判断
//                        if(approvalNumberPageDataList.size() == 1){
//                            //批文号唯一的51jk库商品
//                            ybGoodsInfo = approvalNumberPageDataList.get(0);
//                        }else {
//                            //包含批文号的有个多个51jk库商品进行是否锁定状态判断
//                            ybGoodsInfo = getPageDataByUpdateStatus(approvalNumberPageDataList);
//                        }
//                    }
//                }else{
//                    //有条形码的51jk库商品
//                    if(barCodePageDataList.size() == 1){
//                        //条形码唯一的51jk库商品
//                        ybGoodsInfo = barCodePageDataList.get(0);
//                    }else {
//                        //包含条形码的有多个51jk库商品进行是否锁定状态判断
//                        ybGoodsInfo = getPageDataByUpdateStatus(barCodePageDataList);
//                    }
//                }
//                return ybGoodsInfo;
//
//    }

//    /**
//     * 匹配到多个商品,是否锁定子条件判断
//     * @param pageDataList
//     * @return
//     */
//    private PageData getPageDataByUpdateStatus(List<PageData> pageDataList){
//
//        //商品更新状态 0(锁定 不接受商家更新),1(可更新 接受商家更新)
//        int updateStatus = 0;
//        //分区 按照锁定状态
//        Map<Boolean,List<PageData>> pageDataListMap = pageDataList.parallelStream()
//                .collect(partitioningBy(pageData -> pageData.getInt("updateStatus") == updateStatus));
//        //锁定的51jk库商品
//        List<PageData> lockingPageDataList = pageDataListMap.get(true);
//        //未锁定的51jk库商品
//        List<PageData> unLockingPageDataList = pageDataListMap.get(false);
//
//        //锁定状态判断
//        if(lockingPageDataList == null || lockingPageDataList.size() == 0 ){
//            //多个未锁定的51jk库商品进行是否包含图片判断
//            return getPageDataByImg(unLockingPageDataList);
//        }else {
//            //锁定商品判断
//            if(lockingPageDataList.size() == 1){
//                //锁定商品唯一
//                return lockingPageDataList.get(0);
//            }else {
//                //多个未锁定的51jk库商品进行是否包含图片判断
//                return getPageDataByImg(lockingPageDataList);
//            }
//        }
//    }
//    /**
//     * 匹配到多个商品,是否含有图片子条件判断
//     * @param pageDataList
//     * @return
//     */
//    private PageData getPageDataByImg(List<PageData> pageDataList){
//
//        //分区 按照是否含有图片
//        Map<Boolean,List<PageData>> pageDataListMap = pageDataList.parallelStream()
//                .collect(partitioningBy(pageData -> pageData.getInt("imgCount")>0));
//
//        //含有图片
//        List<PageData> containImgPageDataList = pageDataListMap.get(true);
//        //不含有图片
//        List<PageData> unContainImgPageDataList = pageDataListMap.get(false);
//
//        if(containImgPageDataList == null || containImgPageDataList.size() == 0){
//            //含有图片的取最近更新的（包括唯一）
//            return unContainImgPageDataList.get(0);
//        }else {
//            //不含有图片的取最近更新的（包括唯一）
//            return containImgPageDataList.get(0);
//        }
//
//    }

    public void bindCategory(GoodsData goodsData,boolean isAdd) {
        if (!StringUtil.isNumber(goodsData.getUserCateid())) {
            Map param = new HashMap();
            param.put("siteId", batchImportDto.getSiteId());
            param.put("cateName", goodsData.getUserCateid());
            Category category = categoryMapper.getByCateName(param);
            if (category != null) {
                goodsData.setUserCateid(category.getCateCode());
            }else {
                if(isAdd){
                    //没有匹配到分类,默认放商户下第一条
                    String cateCodeDef = categoryMapper.getCategoryDefault(param);
                    goodsData.setUserCateid(cateCodeDef);
                }else {
                    if (goodsData.getUserCateid() != null) {
                        goodsData.setUserCateid(null);
                    }
                }
            }
        }
    }

    /**
     * 根据品牌名称获取品牌id
     * @param barnd_name
     * @return
     */
    public int getBarndId(String barnd_name) {
        int barndId = 0;
        if (StringUtil.isNotEmpty(barnd_name)) {
            String cacheKey = barnd_name;
            if (__barndIdCache.containsKey(cacheKey)) {
                barndId = __barndIdCache.get(cacheKey);
            } else {
                barndId = goodsService.getBarndId(cacheKey, batchImportDto.getSiteId());
                if (barndId > 0) {
                    __barndIdCache.put(cacheKey, barndId);
                }
            }
        }

        return barndId;
    }

    public void emptyStrToNull(Map<String, String> map) {
        map.entrySet().parallelStream().forEach(entry -> {
            if (entry.getValue() == null || "".equals(entry.getValue())) {
                entry.setValue(null);
            }
        });
    }

    @Async
    private Future<Integer> saveImportData(GoodsData goodsData, boolean isUpdate, boolean use51) {
        boolean hasSuccess;
        try {
            if (isUpdate) {
                hasSuccess = goodsService.updateOne(goodsData);
            } else {
                int goodsId = goodsService.insertOne(goodsData);
                hasSuccess = goodsId > 0;
                goodsData.setGoodsId(goodsId);
            }
            // 保存到数据库 勾选了使用51健康数据 并且51健康数据有对应的记录
            if (hasSuccess && use51 && Optional.ofNullable(goodsData.getYbGoodsId()).orElse(0) != 0) {
                // 处理图片
                try {
                    YbImagesAttr ybImagesAttr = ybGoodsMapper.queryImgLimitOne(goodsData.getYbGoodsId());
                    if (ybImagesAttr != null) {
                        copyImg(batchImportDto.getSiteId(), goodsData.getGoodsId(), goodsData.getYbGoodsId());
                    }
                } catch (Exception e) {
                    logger.debug(e.getMessage());
                }
            }
            return new AsyncResult<>(goodsData.getGoodsId());
            //return goodsData.getGoodsId();

        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new RuntimeException("保存失败");
        }
    }

    @Async
    void copyImg(int siteId, int goodsId, int ybGoodsId) {
        goodsmMapper.copy51jkImgByYbGoodsId(siteId, goodsId, ybGoodsId);
    }
}
