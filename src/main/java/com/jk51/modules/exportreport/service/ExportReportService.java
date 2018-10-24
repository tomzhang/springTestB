package com.jk51.modules.exportreport.service;

import com.alibaba.druid.sql.visitor.functions.If;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jk51.commons.excel.ExcelOperator;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.modules.coupon.service.CouponRuleService;
import com.jk51.modules.es.entity.GoodsInfos;
import com.jk51.modules.es.entity.GoodsInfosAdminReq;
import com.jk51.modules.es.entity.GoodsInfosResp;
import com.jk51.modules.es.service.EsService;
import com.jk51.modules.esn.service.GoodsEsService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.promotions.service.PromotionsDetailService;
import com.jk51.modules.smallTicket.mapper.SmallTicketMapper;
import okhttp3.Headers;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-16
 * 修改记录:
 */
@Service
public class ExportReportService implements ApplicationContextAware{
    private static final Logger logger = LoggerFactory.getLogger(ExportReportService.class);

    @Value("${report.temp_dir}")
    private String temp_dir;

    private ApplicationContext context;

    @Autowired
    private CouponRuleService couponRuleService;

    @Autowired
    private PromotionsDetailService service;

    @Autowired
    private EsService esService;
    //----------批量导入上下架-----------start
    @Autowired
    private SmallTicketMapper smallTicketMapper;
    //批量处理导入商品信息 -----start
    public List<Map<String, Object>> getErrorGoodsInfo(Integer siteId, String goodsInfoStr) {
        System.out.println("导入上下架处理方法记时开始--------" + LocalDateTime.now());
        logger.info("导入上下架处理方法记时开始--------" + LocalDateTime.now());
        List<HashMap> goodsInfoList = JSON.parseArray(goodsInfoStr, HashMap.class);
        //记录更新成功的goodsIds
        List<String> goodsIdsList = new ArrayList<>();
        //记录表中不存在的商品编码信息
        List<String> codeErrorList = null;
        //记录错误信息集合
        List<Map<String, Object>> errorGoodsList = new ArrayList<Map<String, Object>>();
        //1.批量处理  即先过滤商品编码为空的数据，并将该数据保存起来
        List<HashMap> goodsList = goodsInfoList.stream().filter(m -> {
            Object goodsCodeObj = m.get("goodsCode");
            if (Objects.nonNull(goodsCodeObj)){
                //若appGoodsStatus 和goodsStatus均为空，过滤
                Object appGoodsStatusObj = m.get("appGoodsStatus");
                Object goodsStatusObj = m.get("goodsStatus");
                if ((Objects.isNull(appGoodsStatusObj) || org.apache.commons.lang3.StringUtils.isBlank(appGoodsStatusObj.toString())) &&
                    (Objects.isNull(goodsStatusObj) || org.apache.commons.lang3.StringUtils.isBlank(goodsStatusObj.toString()))){
                    Map<String, Object> errorGoodsMap = new HashMap<>();
                    errorGoodsMap.put("goodsCode", goodsCodeObj == null ? null : goodsCodeObj);
                    errorGoodsMap.put("appGoodsStatus", appGoodsStatusObj == null ? null : appGoodsStatusObj);
                    errorGoodsMap.put("goodsStatus", goodsStatusObj == null ? null : goodsStatusObj);
                    errorGoodsMap.put("remark", "线下是否上/下架（app）、线上商城是否上/下架 两列数据均为空！故，该商品上/下架失败！");
                    errorGoodsList.add(errorGoodsMap);
                    return false;
                }
                return true;
            } else {
                Map<String, Object> errorGoodsMap = new HashMap<>();
                Object appGoodsStatusObj = m.get("appGoodsStatus");
                Object goodsStatusObj = m.get("goodsStatus");
                errorGoodsMap.put("goodsCode", goodsCodeObj == null ? null : goodsCodeObj);
                errorGoodsMap.put("appGoodsStatus", appGoodsStatusObj == null ? null : appGoodsStatusObj);
                errorGoodsMap.put("goodsStatus", goodsStatusObj == null ? null : goodsStatusObj);
                errorGoodsMap.put("remark", "该商品编码为空！故，该商品上/下架失败！");
                errorGoodsList.add(errorGoodsMap);
                return false;
            }
        }).collect(toList());
        //2.处理剩下商品编码不为空的数据                     /*并判断appGoodsStatus 和 goodsStatus是否为空，或是否=“上架”或“下架”*/
        List<String> goodsCodeList = goodsList.stream().map(m -> {return m.get("goodsCode");})
            .map(String::valueOf)
            .collect(toList());
        //2.1 根据商品编码不为空的数据，查询出对应的商品信息集合
        List<HashMap> goodsResultList = smallTicketMapper.getGoodsInfoListBySiteIdAndCode(siteId, goodsCodeList);


        //2.2 若查询出的商品信息集合size的大小 == 根据商品编码查询时商品编码的个数，则没有不存在的商品信息；否则，记录下商品编码不存在的信息；得到最终的商品信息集合
        if (goodsCodeList.size() != goodsResultList.size()){
            //取出商品编码不存在的商品，并进行错误信息记录
            List<String> codeList = goodsResultList.stream().map(m -> {return m.get("goodsCode");}).map(String::valueOf).collect(toList());
            codeErrorList = goodsCodeList;
            codeErrorList.removeAll(codeList);
        }
        //2.3 将表中不存在的商品编码进行错误信息记录
        if (Objects.nonNull(codeErrorList))
            codeErrorList.forEach(goodsCode -> {
                goodsList.forEach(m ->{
                    String goodsCodeStr = (String)m.get("goodsCode");
                    if (goodsCode.equals(goodsCodeStr)){
                        Object appGoodsStatusObj = m.get("appGoodsStatus");
                        Object goodsStatusObj = m.get("goodsStatus");
                        Map<String, Object> map = new HashMap<>();
                        map.put("goodsCode", goodsCodeStr);
                        map.put("appGoodsStatus", appGoodsStatusObj == null ? null : appGoodsStatusObj);
                        map.put("goodsStatus", goodsStatusObj == null ? null : goodsStatusObj);
                        map.put("remark", "该商品编码不存在！");
                        errorGoodsList.add(map);
                    }
                });
            });
        //2.4 再次去重，将Excel中，错误的商品编码（即表中不存在的商品编码）去除，即将goodsList 集合和 errorGoodsList集合中商品编码相同的去除，错误信息以在上面代码中记录
        List<String> goodsCodeStrList = errorGoodsList.stream().map(m -> {
            return m.get("goodsCode");
        }).map(String::valueOf).collect(toList());
        List<HashMap> resultGoodsList = goodsList.stream().filter(m -> {
            String goodsCode = (String) m.get("goodsCode");
            return !goodsCodeStrList.contains(goodsCode);
        }).collect(toList());
        //3.过滤表格中，appGoodsStatus 为空 或 appGoodsStatus != “上架”或“下架”的数据，并记录该信息；否则，根据最终的商品信息集合继续如下判断
        List<Map> appResultList = resultGoodsList.stream().map(m -> getAppGoodsMap(m, errorGoodsList)).filter(m -> {
            return !Objects.isNull(m);
        }).collect(toList());
        //3.0 对appResultList结果进行分组：即上架组、下架组
        Map<String, List<Map>> appGoodsStatusMap = appResultList.stream().collect(Collectors.groupingBy(m -> m.get("appGoodsStatus").toString()));
        //3.0.1 下架组直接进行更新
        List<Map> appLowerList = appGoodsStatusMap.get("下架");
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(appLowerList)){
            /*List<String> appLowerCodeList = appLowerList.stream().map(map -> {
                return map.get("goodsCode");
            }).map(String::valueOf).collect(toList());*/
            List<String> appLowerCodeList = getAppLowerGoodsCode(appLowerList, goodsResultList, errorGoodsList);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(appLowerCodeList)){
                Integer flag = smallTicketMapper.updateAppLowerGoodsBySiteIdAndCode(siteId, appLowerCodeList);
                System.out.println(flag + "app下架");
                if (0 < flag){
                    //获取下架成功的商品id
                    getSuccessGoodsIds(goodsResultList, appLowerCodeList, goodsIdsList);
                }
            }
        }
        //3.0.2 上架组要进行下列判断后，再进行更新
        List<Map> appUpperGoodsList = appGoodsStatusMap.get("上架");
        //3.1 //APP上架要求：商品编码 商品名称 商品标题 规格 生成厂家  现价（shop price），若满足该条件，则进行更新操作；否则记录对应的错误信息
        //app上下架，导入Excel中正常数据和查出的数据进行对比，若条件满足，则进行更新，若不满足，则记录错误信息
        List<Map<String, Object>> appSuccessGoodsInfoList = new ArrayList<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(appUpperGoodsList)){
            handleAppGoodsInfo(errorGoodsList, goodsResultList, appUpperGoodsList, appSuccessGoodsInfoList);
            //若appSuccessGoodsInfoList不为空，则上架组可进行更新操作
            if (Objects.nonNull(appSuccessGoodsInfoList)){
                /*List<String> appUpperCodeList = appSuccessGoodsInfoList.stream().map(map -> {
                    return map.get("goodsCode");
                }).map(String::valueOf).collect(toList());*/
                List<String> appUpperCodeList = getAppUpperGoodsCode(appSuccessGoodsInfoList, goodsResultList, errorGoodsList);
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(appUpperCodeList)){
                    Integer flag = smallTicketMapper.updateAppUpperGoodsBySiteIdAndCode(siteId, appUpperCodeList);
                    System.out.println(flag + "app上架");
                    if (0 < flag){
                        //获取上架成功的商品id
                        getSuccessGoodsIds(goodsResultList, appUpperCodeList, goodsIdsList);
                    }
                }
            }
        }
        //4.过滤表格中，goodsStatus 为空 或 goodsStatus !=“上架”或“下架”的数据，并记录该信息；否则，根据最终的商品信息集合继续如下判断
        //4.0 重复步骤 3 的步骤，进行如下操作
        List<Map> resultList = resultGoodsList.stream().map(m -> getGoodsMap(m, errorGoodsList)).filter(m -> {
            return !Objects.isNull(m);
        }).collect(toList());
        Map<String, List<Map>> goodsStatusMap = resultList.stream().collect(Collectors.groupingBy(m -> m.get("goodsStatus").toString()));
        List<Map> lowerList = goodsStatusMap.get("下架");
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(lowerList)){
            /*List<String> lowerCodeList = lowerList.stream().map(map -> {
                return map.get("goodsCode");
            }).map(String::valueOf).collect(toList());*/
            List<String> lowerCodeList = getLowerGoodsCode(lowerList, goodsResultList, errorGoodsList);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(lowerCodeList)){
                Integer flag = smallTicketMapper.updateLowerGoodsBySiteIdAndCode(siteId, lowerCodeList);
                System.out.println(flag + "商城下架");
                if (0 < flag){
                    //获取下架成功的商品id
                    getSuccessGoodsIds(goodsResultList, lowerCodeList, goodsIdsList);
                }
            }
        }
        List<Map> upperGoodsList = goodsStatusMap.get("上架");
        List<Map<String, Object>> successGoodsInfoList = new ArrayList<>();
        //4.1 //微信上架要求：商品编码 商品名称 商品标题 规格 生成厂家  现价（shop price） 主图
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(upperGoodsList)){
            handleGoodsInfo(errorGoodsList, goodsResultList, upperGoodsList, successGoodsInfoList);
            if (Objects.nonNull(successGoodsInfoList)){
                /*List<String> upperCodeList = successGoodsInfoList.stream().map(map -> {
                    return map.get("goodsCode");
                }).map(String::valueOf).collect(toList());*/
                List<String> upperCodeList = getUpperGoodsCode(successGoodsInfoList, goodsResultList, errorGoodsList);
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(upperCodeList)){
                    Integer flag = smallTicketMapper.updateUpperGoodsBySiteIdAndCode(siteId, upperCodeList);
                    System.out.println(flag + "商城上架");
                    if (0 < flag){
                        //获取上架成功的商品id
                        getSuccessGoodsIds(goodsResultList, upperCodeList, goodsIdsList);
                    }
                }
            }
        }
        System.out.println("导入上下架处理方法记时处理中--------" + LocalDateTime.now());
        logger.info("导入上下架处理方法记时处理中--------" + LocalDateTime.now());
        if (org.apache.commons.collections.CollectionUtils.isEmpty(errorGoodsList)){
            Map<String, Object> countMap = new HashMap<>();
            countMap.put("errorCount", 0);
            countMap.put("successCount", goodsInfoList.size());
            countMap.put("successGoodsIdMap", goodsIdsList);
            /*if (org.apache.commons.collections.CollectionUtils.isNotEmpty(goodsIdsList)){
                countMap.put("indexFlag", 1);
            *//*    updateGoodsIndex(siteId);
            }else {*//*
                updateGoodsIndex2(Integer.toString(siteId), goodsIdsList);
            }*/
            errorGoodsList.add(countMap);
            return errorGoodsList;
        }
        //如果商品编码相同，则进行商品编码相同的错误合并
        List<Map<String, Object>> mapList = mergeErrorInfo(errorGoodsList);
        Map<String, Object> countMap = new HashMap<>();
        Integer errorCount = mapList.size();
        countMap.put("errorCount", errorCount);
        Integer successCount = goodsInfoList.size() - errorCount;
        countMap.put("successCount", successCount);
        countMap.put("successGoodsIdMap", goodsIdsList);
        //goodsIdsList不为null且size>200,
        /*if (org.apache.commons.collections.CollectionUtils.isNotEmpty(goodsIdsList)){
            countMap.put("indexFlag", 1);
        *//*    updateGoodsIndex(siteId);
        }else {*//*
            updateGoodsIndex2(Integer.toString(siteId), goodsIdsList);
        }*/
        mapList.add(countMap);
        System.out.println("导入上下架处理方法记时结束--------" + LocalDateTime.now());
        logger.info("导入上下架处理方法记时结束--------" + LocalDateTime.now());
        return mapList;
    }
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsEsService goodsEsService;
    //更新全部商品的索引
    @Async
    public void updateGoodsIndex(Integer siteId){
        //更新商品索引操作 batchUpdateGoods
        Integer hasErpPrice = goodsMapper.getHasErpPriceOfMerchantExtBySiteId(siteId);
        if (Objects.nonNull(hasErpPrice)){
            goodsEsService.batchUpdateGoods(String.valueOf(siteId), String.valueOf(hasErpPrice));
            goodsEsService.updateSuggestByBrandId("b_shop_" + siteId);
        }
    }
    //更新指定商品的索引
    @Async
    public void updateGoodsIndex2(String siteId, List<String> goodsIdsList){
        try {
            String hasErpPrice = smallTicketMapper.getByMerchantId(Integer.parseInt(siteId));
            for (String goodsId :goodsIdsList) {
                goodsEsService.updateGoodsInxALlIndices("b_shop_" + siteId, goodsId, hasErpPrice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Object getCode(Map<String, Object> map){
        return map.get("goodsCode");
    }
    //如果商品编码相同，则进行商品编码相同的错误合并
    private List<Map<String, Object>> mergeErrorInfo(List<Map<String, Object>> errorGoodsList){
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Map<String, Object>> goodsCodeList = errorGoodsList.stream().filter(eg -> {
            Object goodsCodeObj = eg.get("goodsCode");
            if (Objects.isNull(goodsCodeObj) || org.apache.commons.lang3.StringUtils.isBlank(goodsCodeObj.toString())){
                resultList.add(eg);
                return false;
            }
            return true;
        }).collect(toList());
        Map<Object, List<Map<String, Object>>> codeMap = goodsCodeList.stream().collect(Collectors.groupingBy(ExportReportService::getCode));
        if (Objects.nonNull(codeMap) && codeMap.size() > 0){
            codeMap.entrySet().stream().forEach(m ->{
                List<Map<String, Object>> value = m.getValue();
                if (1 < value.size()){
                    String remark = "";
                    Object goodsCodeObj = null;
                    Object appGoodsStatusObj = null;
                    Object goodsStatusObj = null;
                    String remark1 = (String) value.get(0).get("remark");
                    for (Map<String, Object> map :value) {
                        String remark2 =(String) map.get("remark");
                        if (remark1.equals(remark2)){
                            remark = remark1;
                        }else if (!remark1.equals(remark2) && remark.contains(remark2)){
                            remark = remark;
                        }else {
                            remark += (String) map.get("remark");
                        }
                        goodsCodeObj = map.get("goodsCode");
                        appGoodsStatusObj = map.get("appGoodsStatus");
                        goodsStatusObj = map.get("goodsStatus");
                    }
                    Map<String, Object> goodsMap = new HashMap<>();
                    goodsMap.put("goodsCode", goodsCodeObj);
                    goodsMap.put("appGoodsStatus", appGoodsStatusObj);
                    goodsMap.put("goodsStatus", goodsStatusObj);
                    goodsMap.put("remark", remark);
                    resultList.add(goodsMap);
                }else {
                    resultList.addAll(value);
                }
            });
        }
        return resultList;
    }
    //存储更新成功的商品id
    private void getSuccessGoodsIds(List<HashMap> goodsResultList, List<String> codeList, List<String> goodsIdsList) {
        codeList.stream().forEach(app -> {
            goodsResultList.stream().forEach(g -> {
                Object goodsIdObj = g.get("goodsId");
                Object goodsCodeObj = g.get("goodsCode");
                if (app.equals(goodsCodeObj.toString())){
                    if (!goodsIdsList.contains(goodsIdObj.toString()))
                        goodsIdsList.add(goodsIdObj.toString());
                }
            });
        });
    }
    //excel中，app商品上架状态，判断该商品是否已经被上架，若已上架，则记录错误信息；否则，则更新该商品的上架状态(因上下架appSuccessGoodsInfoList集合的泛型有差异，合起写报错，故写两个方法进行是否已上下架判断)
    private List<String> getAppUpperGoodsCode(List<Map<String, Object>> appSuccessGoodsInfoList, List<HashMap> goodsResultList, List<Map<String, Object>> errorGoodsList) {
        List<String> appGoodsCodeList = new ArrayList<>();
        appSuccessGoodsInfoList.stream().forEach(app ->{
            Object appGoodsCodeObj = app.get("goodsCode");
            Object appGoodsStatusObj = app.get("appGoodsStatus");
            goodsResultList.stream().forEach(g -> {
                Object aGoodsCodeObj = g.get("goodsCode");
                Object aGoodsStatusObj = g.get("appGoodsStatus");
                if (appGoodsCodeObj.toString().equals(aGoodsCodeObj.toString())){
                    if (Objects.nonNull(aGoodsStatusObj) && org.apache.commons.lang3.StringUtils.isNotBlank(aGoodsStatusObj.toString())){
                        if (1 == Integer.parseInt(aGoodsStatusObj.toString())){//两者状态若相等，则该商品已被上下架，故，不可再次上下架，记录错误信息
                            Object goodsStatusObj = app.get("goodsStatus");
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodsCode", appGoodsCodeObj);
                            map.put("goodsStatus", goodsStatusObj);
                            map.put("appGoodsStatus", appGoodsStatusObj);
                            map.put("remark", "app，该商品已经上架，不可再次上架！");
                            errorGoodsList.add(map);
                        }else if (3 == Integer.parseInt(aGoodsStatusObj.toString())){
                            Object goodsStatusObj = app.get("goodsStatus");
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodsCode", appGoodsCodeObj);
                            map.put("goodsStatus", goodsStatusObj);
                            map.put("appGoodsStatus", appGoodsStatusObj);
                            map.put("remark", "该商品违规，app，不可上架！");
                            errorGoodsList.add(map);
                        }else if (4 == Integer.parseInt(aGoodsStatusObj.toString())){
                            Object goodsStatusObj = app.get("goodsStatus");
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodsCode", appGoodsCodeObj);
                            map.put("goodsStatus", goodsStatusObj);
                            map.put("appGoodsStatus", appGoodsStatusObj);
                            map.put("remark", "该商品已删除，app，不可上架！");
                            errorGoodsList.add(map);
                        }else {//两者状态若不相等，则该商品没有被上下架，故，可上下架
                            appGoodsCodeList.add(appGoodsCodeObj.toString().trim());
                        }
                    }
                }
            });
        });
        return appGoodsCodeList;
    }
    //excel中，app商品下架状态，判断该商品是否已经被下架，若已下架，则记录错误信息；否则，则更新该商品的下架状态(因上下架appSuccessGoodsInfoList集合的泛型有差异，合起写报错，故写两个方法进行是否已上下架判断)
    private List<String> getAppLowerGoodsCode(List<Map> appSuccessGoodsInfoList, List<HashMap> goodsResultList, List<Map<String, Object>> errorGoodsList) {
        List<String> appGoodsCodeList = new ArrayList<>();
        appSuccessGoodsInfoList.stream().forEach(app ->{
            Object appGoodsCodeObj = app.get("goodsCode");
            Object appGoodsStatusObj = app.get("appGoodsStatus");
            goodsResultList.stream().forEach(g -> {
                Object aGoodsCodeObj = g.get("goodsCode");
                Object aGoodsStatusObj = g.get("appGoodsStatus");
                if (appGoodsCodeObj.toString().equals(aGoodsCodeObj.toString())){
                    if (Objects.nonNull(aGoodsStatusObj) && org.apache.commons.lang3.StringUtils.isNotBlank(aGoodsStatusObj.toString())){
                        if (2 == Integer.parseInt(aGoodsStatusObj.toString())){//两者状态若相等，则该商品已被上下架，故，不可再次上下架，记录错误信息
                            Object goodsStatusObj = app.get("goodsStatus");
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodsCode", appGoodsCodeObj);
                            map.put("goodsStatus", goodsStatusObj);
                            map.put("appGoodsStatus", appGoodsStatusObj);
                            map.put("remark", "app，该商品已经下架，不可再次下架！");
                            errorGoodsList.add(map);
                        }else if (3 == Integer.parseInt(aGoodsStatusObj.toString())){
                            Object goodsStatusObj = app.get("goodsStatus");
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodsCode", appGoodsCodeObj);
                            map.put("goodsStatus", goodsStatusObj);
                            map.put("appGoodsStatus", appGoodsStatusObj);
                            map.put("remark", "该商品违规，app，不可下架！");
                            errorGoodsList.add(map);
                        }else if (4 == Integer.parseInt(aGoodsStatusObj.toString())){
                            Object goodsStatusObj = app.get("goodsStatus");
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodsCode", appGoodsCodeObj);
                            map.put("goodsStatus", goodsStatusObj);
                            map.put("appGoodsStatus", appGoodsStatusObj);
                            map.put("remark", "该商品已删除，app，不可下架！");
                            errorGoodsList.add(map);
                        }else {//两者状态若不相等，则该商品没有被上下架，故，可上下架
                            appGoodsCodeList.add(appGoodsCodeObj.toString().trim());
                        }
                    }
                }
            });
        });
        return appGoodsCodeList;
    }
    //微信上架要求：商品编码 商品名称 商品标题 规格 生成厂家  现价（shop price） 主图
    private void handleGoodsInfo(List<Map<String, Object>> errorGoodsList, List<HashMap> goodsResultList,
                                 List<Map> appUpperGoodsList, List<Map<String, Object>> successGoodsInfoList) {
        appUpperGoodsList.stream().forEach(app ->{
            goodsResultList.stream().forEach(g -> {
                handleUpperGoodsInfo(app, g, errorGoodsList, successGoodsInfoList);
            });
        });
    }
    private void handleUpperGoodsInfo(Map app, HashMap g, List<Map<String, Object>> errorGoodsList, List<Map<String, Object>> successGoodsInfoList){
        String appGoodsCode = (String)app.get("goodsCode");
        String gCode = (String)g.get("goodsCode");
        if (appGoodsCode.equals(gCode)){
            Object goodsNameObj = g.get("goodsName");//商品名称
            Object goodsTitleObj = g.get("goodsTitle");//商品标题
            Object specifCationObj = g.get("specifCation");//规格
            Object goodsCompanyObj = g.get("goodsCompany");//生成厂家
            Object shopPriceObj = g.get("shopPrice");//现价
            Object imageObj = g.get("image");//主图 image
            Object flagObj = g.get("flag");//主图是否有效
            Boolean goodsNameFlag = judgeGoodsInfo(goodsNameObj);
            if (!goodsNameFlag){
                Map<String, Object> map = new HashMap<>();
                Object appGoodsStatus = app.get("appGoodsStatus");
                Object goodsStatus = app.get("goodsStatus");
                map.put("goodsCode", appGoodsCode);
                map.put("appGoodsStatus", appGoodsStatus);
                map.put("goodsStatus", goodsStatus);
                map.put("remark", "该商品名称为空，不可上架！");
                errorGoodsList.add(map);
                return;
            }
            Boolean goodsTitleFlag = judgeGoodsInfo(goodsTitleObj);
            if (!goodsTitleFlag){
                Map<String, Object> map = new HashMap<>();
                Object appGoodsStatus = app.get("appGoodsStatus");
                Object goodsStatus = app.get("goodsStatus");
                map.put("goodsCode", appGoodsCode);
                map.put("appGoodsStatus", appGoodsStatus);
                map.put("goodsStatus", goodsStatus);
                map.put("remark", "该商品标题为空，不可上架！");
                errorGoodsList.add(map);
                return;
            }
            Boolean specifCationFlag = judgeGoodsInfo(specifCationObj);
            if (!specifCationFlag){
                Map<String, Object> map = new HashMap<>();
                Object appGoodsStatus = app.get("appGoodsStatus");
                Object goodsStatus = app.get("goodsStatus");
                map.put("goodsCode", appGoodsCode);
                map.put("appGoodsStatus", appGoodsStatus);
                map.put("goodsStatus", goodsStatus);
                map.put("remark", "该商品规格为空，不可上架！");
                errorGoodsList.add(map);
                return;
            }
            Boolean goodsCompanyFlag = judgeGoodsInfo(goodsCompanyObj);
            if (!goodsCompanyFlag){
                Map<String, Object> map = new HashMap<>();
                Object appGoodsStatus = app.get("appGoodsStatus");
                Object goodsStatus = app.get("goodsStatus");
                map.put("goodsCode", appGoodsCode);
                map.put("appGoodsStatus", appGoodsStatus);
                map.put("goodsStatus", goodsStatus);
                map.put("remark", "该商品生产厂家为空，不可上架！");
                errorGoodsList.add(map);
                return;
            }
            Boolean goodsPriceFlag = judgeGoodsPrice(shopPriceObj);
            if (!goodsPriceFlag){
                Map<String, Object> map = new HashMap<>();
                Object appGoodsStatus = app.get("appGoodsStatus");
                Object goodsStatus = app.get("goodsStatus");
                map.put("goodsCode", appGoodsCode);
                map.put("appGoodsStatus", appGoodsStatus);
                map.put("goodsStatus", goodsStatus);
                map.put("remark", "该商品现价为空，不可上架！");
                errorGoodsList.add(map);
                return;
            }
            Boolean imageFlag = judgeGoodsInfo(imageObj);
            Boolean flag = judgeGoodsInfo2(flagObj);
            if (!imageFlag || !flag){
                Map<String, Object> map = new HashMap<>();
                Object appGoodsStatus = app.get("appGoodsStatus");
                Object goodsStatus = app.get("goodsStatus");
                map.put("goodsCode", appGoodsCode);
                map.put("appGoodsStatus", appGoodsStatus);
                map.put("goodsStatus", goodsStatus);
                map.put("remark", "线上商城，该商品主图为空，不可上架！");
                errorGoodsList.add(map);
                return;
            }
            successGoodsInfoList.add(app);
        }
    }
    //APP上架要求：商品编码 商品名称 商品标题 规格 生成厂家  现价（shop price），若满足该条件，则进行更新操作；否则记录对应的错误信息
    //app上下架，导入Excel中正常数据和查出的数据进行对比，若条件满足，则进行更新，若不满足，则记录错误信息
    private void handleAppGoodsInfo(List<Map<String, Object>> errorGoodsList, List<HashMap> goodsResultList,
                                    List<Map> appUpperGoodsList, List<Map<String, Object>> appSuccessGoodsInfoList) {
        appUpperGoodsList.stream().forEach(app ->{
            goodsResultList.stream().forEach(g -> {
                handleAppUpperGoodsInfo(app, g, errorGoodsList, appSuccessGoodsInfoList);
            });
        });
    }
    //APP上架要求：商品编码 商品名称 商品标题 规格 生成厂家  现价（shop price），若满足该条件，则进行更新操作；否则记录对应的错误信息
    //app上下架，导入Excel中正常数据和查出的数据进行对比，若条件满足，则进行更新，若不满足，则记录错误信息
    private void handleAppUpperGoodsInfo(Map app, HashMap g, List<Map<String, Object>> errorGoodsList, List<Map<String, Object>> appSuccessGoodsInfoList){
        String appGoodsCode = (String)app.get("goodsCode");
        String gCode = (String)g.get("goodsCode");
        if (appGoodsCode.equals(gCode)){
            Object goodsNameObj = g.get("goodsName");//商品名称
            Object goodsTitleObj = g.get("goodsTitle");//商品标题
            Object specifCationObj = g.get("specifCation");//规格
            Object goodsCompanyObj = g.get("goodsCompany");//生成厂家
            Object shopPriceObj = g.get("shopPrice");//现价
            Boolean goodsNameFlag = judgeGoodsInfo(goodsNameObj);
            if (!goodsNameFlag){
                Map<String, Object> map = new HashMap<>();
                Object appGoodsStatus = app.get("appGoodsStatus");
                Object goodsStatus = app.get("goodsStatus");
                map.put("goodsCode", appGoodsCode);
                map.put("appGoodsStatus", appGoodsStatus);
                map.put("goodsStatus", goodsStatus);
                map.put("remark", "该商品名称为空，不可上架！");
                errorGoodsList.add(map);
                return;
            }
            Boolean goodsTitleFlag = judgeGoodsInfo(goodsTitleObj);
            if (!goodsTitleFlag){
                Map<String, Object> map = new HashMap<>();
                Object appGoodsStatus = app.get("appGoodsStatus");
                Object goodsStatus = app.get("goodsStatus");
                map.put("goodsCode", appGoodsCode);
                map.put("appGoodsStatus", appGoodsStatus);
                map.put("goodsStatus", goodsStatus);
                map.put("remark", "该商品标题为空，不可上架！");
                errorGoodsList.add(map);
                return;
            }
            Boolean specifCationFlag = judgeGoodsInfo(specifCationObj);
            if (!specifCationFlag){
                Map<String, Object> map = new HashMap<>();
                Object appGoodsStatus = app.get("appGoodsStatus");
                Object goodsStatus = app.get("goodsStatus");
                map.put("goodsCode", appGoodsCode);
                map.put("appGoodsStatus", appGoodsStatus);
                map.put("goodsStatus", goodsStatus);
                map.put("remark", "该商品规格为空，不可上架！");
                errorGoodsList.add(map);
                return;
            }
            Boolean goodsCompanyFlag = judgeGoodsInfo(goodsCompanyObj);
            if (!goodsCompanyFlag){
                Map<String, Object> map = new HashMap<>();
                Object appGoodsStatus = app.get("appGoodsStatus");
                Object goodsStatus = app.get("goodsStatus");
                map.put("goodsCode", appGoodsCode);
                map.put("appGoodsStatus", appGoodsStatus);
                map.put("goodsStatus", goodsStatus);
                map.put("remark", "该商品生产厂家为空，不可上架！");
                errorGoodsList.add(map);
                return;
            }
            Boolean goodsPriceFlag = judgeGoodsPrice(shopPriceObj);
            if (!goodsPriceFlag){
                Map<String, Object> map = new HashMap<>();
                Object appGoodsStatus = app.get("appGoodsStatus");
                Object goodsStatus = app.get("goodsStatus");
                map.put("goodsCode", appGoodsCode);
                map.put("appGoodsStatus", appGoodsStatus);
                map.put("goodsStatus", goodsStatus);
                map.put("remark", "该商品现价为空，不可上架！");
                errorGoodsList.add(map);
                return;
            }
            appSuccessGoodsInfoList.add(app);
        }
    }
    private Boolean judgeGoodsPrice(Object goodsPrice) {
        if (Objects.nonNull(goodsPrice))
            return true;
        return false;
    }
    private Boolean judgeGoodsInfo(Object goodsInfo) {
        if (Objects.nonNull(goodsInfo) && org.apache.commons.lang3.StringUtils.isNotBlank(goodsInfo.toString()))
            return true;
        return false;
    }
    private Boolean judgeGoodsInfo2(Object goodsInfo) {
        if (Objects.nonNull(goodsInfo) && 0 == Integer.parseInt(goodsInfo.toString()))
            return true;
        return false;
    }
    private  Map<String, Object> getGoodsMap(HashMap map, List<Map<String, Object>> errorGoodsList){
        Object goodsCodeObj = map.get("goodsCode");
        Object goodsStatusObj = map.get("goodsStatus");
        if (Objects.isNull(goodsStatusObj)){
            Object appGoodsStatusObj = map.get("appGoodsStatus");
            Map<String, Object> m = new HashMap<>();
            m.put("goodsCode", goodsCodeObj);
            m.put("appGoodsStatus", appGoodsStatusObj == null ? null : appGoodsStatusObj);
            m.put("goodsStatus", goodsStatusObj == null ? null : goodsStatusObj);
            m.put("remark", "线上商城是否上/下架，该列记录对应数据为空！故，该商品上/下架失败！");
            errorGoodsList.add(m);
            return null;
        }else if("上架".equals(goodsStatusObj.toString())){
            return map;
        }else if("下架".equals(goodsStatusObj.toString())){
            return map;
        }else {
            Object appGoodsStatusObj = map.get("appGoodsStatus");
            Map<String, Object> m = new HashMap<>();
            m.put("goodsCode", goodsCodeObj);
            m.put("appGoodsStatus", appGoodsStatusObj == null ? null : appGoodsStatusObj);
            m.put("goodsStatus", goodsStatusObj == null ? null : goodsStatusObj);
            m.put("remark", "线上商城是否上/下架，该列数据非上架或下架字样！故，该商品上/下架失败！");
            errorGoodsList.add(m);
            return null;
        }
    }
    private  Map<String, Object> getAppGoodsMap(HashMap map, List<Map<String, Object>> errorGoodsList){
        Object goodsCodeObj = map.get("goodsCode");
        Object appGoodsStatusObj = map.get("appGoodsStatus");
        if (Objects.isNull(appGoodsStatusObj)){
            Object goodsStatusObj = map.get("goodsStatus");
            Map<String, Object> m = new HashMap<>();
            m.put("goodsCode", goodsCodeObj);
            m.put("appGoodsStatus", appGoodsStatusObj == null ? null : appGoodsStatusObj);
            m.put("goodsStatus", goodsStatusObj == null ? null : goodsStatusObj);
            m.put("remark", "线下是否上/下架（app），该列记录对应数据为空！故，该商品上/下架失败！");
            errorGoodsList.add(m);
            return null;
        }else if("上架".equals(appGoodsStatusObj.toString())){
            return map;
        }else if("下架".equals(appGoodsStatusObj.toString())){
            return map;
        }else {
            Object goodsStatusObj = map.get("goodsStatus");
            Map<String, Object> m = new HashMap<>();
            m.put("goodsCode", goodsCodeObj);
            m.put("appGoodsStatus", appGoodsStatusObj == null ? null : appGoodsStatusObj);
            m.put("goodsStatus", goodsStatusObj == null ? null : goodsStatusObj);
            m.put("remark", "线下是否上/下架（app），该列数据非上架或下架字样！故，该商品上/下架失败！");
            errorGoodsList.add(m);
            return null;
        }
    }
    //excel中，线上商城，商品下架状态，判断该商品是否已经被下架，若已下架，则记录错误信息；否则，则更新该商品的下架状态(因上下架successGoodsInfoList集合的泛型有差异，合起写报错，故写两个方法进行是否已上下架判断)
    private List<String> getLowerGoodsCode(List<Map> successGoodsInfoList, List<HashMap> goodsResultList, List<Map<String, Object>> errorGoodsList) {
        List<String> goodsCodeList = new ArrayList<>();
        successGoodsInfoList.stream().forEach(goods ->{
            Object goodsCodeObj1 = goods.get("goodsCode");
            Object goodsStatusObj1 = goods.get("goodsStatus");
            goodsResultList.stream().forEach(g -> {
                Object GoodsCodeObj2 = g.get("goodsCode");
                Object goodsStatusObj2 = g.get("goodsStatus");
                if (goodsCodeObj1.toString().equals(GoodsCodeObj2.toString())){
                    if (Objects.nonNull(goodsStatusObj2) && org.apache.commons.lang3.StringUtils.isNotBlank(goodsStatusObj2.toString())){
                        if (2 == Integer.parseInt(goodsStatusObj2.toString())){//两者状态若相等，则该商品已被上下架，故，不可再次上下架，记录错误信息
                            Object appGoodsStatusObj = goods.get("appGoodsStatus");
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodsCode", goodsCodeObj1);
                            map.put("goodsStatus", goodsStatusObj1);
                            map.put("appGoodsStatus", appGoodsStatusObj);
                            map.put("remark", "线上商城，该商品已经下架，不可再次下架！");
                            errorGoodsList.add(map);
                        }else if(3 == Integer.parseInt(goodsStatusObj2.toString())){//两者状态若相等，则该商品已被上下架，故，不可再次上下架，记录错误信息
                            Object appGoodsStatusObj = goods.get("appGoodsStatus");
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodsCode", goodsCodeObj1);
                            map.put("goodsStatus", goodsStatusObj1);
                            map.put("appGoodsStatus", appGoodsStatusObj);
                            map.put("remark", "该商品违规，线上商城，不可下架！");
                            errorGoodsList.add(map);
                        }else if(4 == Integer.parseInt(goodsStatusObj2.toString())){//两者状态若相等，则该商品已被上下架，故，不可再次上下架，记录错误信息
                            Object appGoodsStatusObj = goods.get("appGoodsStatus");
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodsCode", goodsCodeObj1);
                            map.put("goodsStatus", goodsStatusObj1);
                            map.put("appGoodsStatus", appGoodsStatusObj);
                            map.put("remark", "该商品已删除，线上商城，不可下架！");
                            errorGoodsList.add(map);
                        }else {//两者状态若不相等，则该商品没有被上下架，故，可上下架
                            goodsCodeList.add(goodsCodeObj1.toString().trim());
                        }
                    }
                }
            });
        });
        return goodsCodeList;
    }
    //excel中，线上商城，商品上架状态，判断该商品是否已经被上架，若已上架，则记录错误信息；否则，则更新该商品的上架状态(因上下架successGoodsInfoList集合的泛型有差异，合起写报错，故写两个方法进行是否已上下架判断)
    private List<String> getUpperGoodsCode(List<Map<String, Object>> successGoodsInfoList, List<HashMap> goodsResultList, List<Map<String, Object>> errorGoodsList) {
        List<String> goodsCodeList = new ArrayList<>();
        successGoodsInfoList.stream().forEach(goods ->{
            Object goodsCodeObj1 = goods.get("goodsCode");
            Object goodsStatusObj1 = goods.get("goodsStatus");
            goodsResultList.stream().forEach(g -> {
                Object GoodsCodeObj2 = g.get("goodsCode");
                Object goodsStatusObj2 = g.get("goodsStatus");
                if (goodsCodeObj1.toString().equals(GoodsCodeObj2.toString())){
                    if (Objects.nonNull(goodsStatusObj2) && org.apache.commons.lang3.StringUtils.isNotBlank(goodsStatusObj2.toString())){
                        if (1 == Integer.parseInt(goodsStatusObj2.toString())){//两者状态若相等，则该商品已被上下架，故，不可再次上下架，记录错误信息
                            Object appGoodsStatusObj = goods.get("appGoodsStatus");
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodsCode", goodsCodeObj1);
                            map.put("goodsStatus", goodsStatusObj1);
                            map.put("appGoodsStatus", appGoodsStatusObj);
                            map.put("remark", "线上商城,该商品已经上架，不可再次上架！");
                            errorGoodsList.add(map);
                        }else if(3 == Integer.parseInt(goodsStatusObj2.toString())){//两者状态若相等，则该商品已被上下架，故，不可再次上下架，记录错误信息
                            Object appGoodsStatusObj = goods.get("appGoodsStatus");
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodsCode", goodsCodeObj1);
                            map.put("goodsStatus", goodsStatusObj1);
                            map.put("appGoodsStatus", appGoodsStatusObj);
                            map.put("remark", "该商品违规，线上商城，不可上架！");
                            errorGoodsList.add(map);
                        }else if(4 == Integer.parseInt(goodsStatusObj2.toString())){//两者状态若相等，则该商品已被上下架，故，不可再次上下架，记录错误信息
                            Object appGoodsStatusObj = goods.get("appGoodsStatus");
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodsCode", goodsCodeObj1);
                            map.put("goodsStatus", goodsStatusObj1);
                            map.put("appGoodsStatus", appGoodsStatusObj);
                            map.put("remark", "该商品已删除，线上商城，不可上架！");
                            errorGoodsList.add(map);
                        }else {//两者状态若不相等，则该商品没有被上下架，故，可上下架
                            goodsCodeList.add(goodsCodeObj1.toString().trim());
                        }
                    }
                }
            });
        });
        return goodsCodeList;
    }
    //----------批量导入上下架-----------end

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public File getReport(ExportParams exportParams) throws BusinessLogicException, IOException {
        String beanName = exportParams.getBeanName();
        String methodName = exportParams.getMethodName();
        List<String> header = exportParams.getHeader();
        if(CollectionUtils.isEmpty(exportParams.getCols())){
            exportParams.setCols(header);
        }

        //查询条件的时间加上时、分、秒
        String orderTimeStart = (String) exportParams.getParams().get("orderTimeStart");
        String orderTimeEnd = (String) exportParams.getParams().get("orderTimeEnd");
        if(!StringUtil.isEmpty(orderTimeStart)){
            orderTimeStart =  orderTimeStart + " 00:00:00";
            exportParams.getParams().put("orderTimeStart",orderTimeStart);
        }
        if(!StringUtil.isEmpty(orderTimeStart)){
            orderTimeEnd =  orderTimeEnd + " 23:59:59";
            exportParams.getParams().put("orderTimeEnd",orderTimeEnd);
        }

        //参数中将tradesStaus转换为list,并添加到参数中
        String tradesStatus = (String) exportParams.getParams().get("tradesStatus");
        if ( tradesStatus != null) {
            List list = Arrays.asList(tradesStatus.split(","));
            exportParams.getParams().put("list",list);
        }

        Object target = context.getBean(beanName);
        if(target == null){
            throw new BusinessLogicException("根据Bean名称"+beanName+"未找到相关的Bean实例，任务调度失败");
        }
        Method method = getTargetMethod(target,methodName);
        if(method == null){
            throw new BusinessLogicException("未找到匹配的方法,class["+target.getClass().getName()
                    +"],method[{"+methodName+"}],是否含有参数[java.util.Map]，请检查配置.");
        }
        File file = new File(temp_dir + "/report_" + System.currentTimeMillis() + ".xls");
        file.getParentFile().mkdirs();
        int pageNum = 0,pageSize = 2000, pages = 0;
        Page page = null;
        HSSFWorkbook hssfWorkbook = ExcelOperator.createWorkbook("data", header);
        do {
            boolean isCount = false;
            if(page == null){
                isCount = true;
                page = PageHelper.startPage(++pageNum, pageSize,isCount);
            }else{
                //避免每次都发起Count查询
                PageHelper.startPage(++pageNum, pageSize,isCount);
            }
            List<Map<String, Object>> tradesList = null;

            try {
                tradesList = (List<Map<String, Object>>) ReflectionUtils.invokeMethod(method,target,exportParams.getParams());
                if(isCount){
                    pages = page.getPages();
                }
            } catch (Exception e) {
                if(e instanceof  ClassCastException)
                    throw new BusinessLogicException("匹配的方法返回参数错误,class["+target.getClass().getName()
                            +"],method[{"+methodName+"}],参数是否是[List<Map<String, Object>>]，请检查配置.");
                e.printStackTrace();
            }
            if(tradesList == null)
                tradesList = new ArrayList<>();
            ExcelOperator.writeData(hssfWorkbook, exportParams.getCols(), tradesList, ExcelOperator.createContentAreaStyle(hssfWorkbook));
            FileOutputStream out = new FileOutputStream(file);
            hssfWorkbook.write(out);
            out.close();
        } while (pages > pageNum);
        return file;
    }

    /**
     * 从ES中导出商品数据
     * @param exportParams
     * @return
     * @throws BusinessLogicException
     * @throws IOException
     */
    public File getReportFromES(ExportParamsForES exportParams) throws BusinessLogicException, IOException {
        List<String> header = exportParams.getHeader();
        if(CollectionUtils.isEmpty(exportParams.getCols())){
            exportParams.setCols(header);
        }

        //将Map转成对象
        Map<String, Object> params = exportParams.getParams();
        String siteId = params.get("siteId").toString();
        params.remove("siteId");
        String s = JSON.toJSONString(params);
        GoodsInfosAdminReq goodsInfosAdminReq = JSON.parseObject(s, GoodsInfosAdminReq.class);
        goodsInfosAdminReq.setDbname("b_shop_" + siteId);

        File file = new File(temp_dir + "/report_" + System.currentTimeMillis() + ".xls");
        file.getParentFile().mkdirs();
        int currentPage = 0, pageNum = 2000, pages = 0, total = 0;
        goodsInfosAdminReq.setPageNum(pageNum);
        HSSFWorkbook hssfWorkbook = ExcelOperator.createWorkbook("data", header);//设置表头内容
        do {
            goodsInfosAdminReq.setCurrentPage(++currentPage);
            /*//判断当前也是否是最后一页
            if (currentPage == pages) {
                if (total > pageNum) {
                    goodsInfosAdminReq.setPageNum(total - (currentPage - 1) * pageNum);
                } else {
                    goodsInfosAdminReq.setPageNum(pageNum);
                }
            } else {
                goodsInfosAdminReq.setPageNum(pageNum);
            }*/
            List<GoodsInfos> goodsInfoss = null;
            try {
                GoodsInfosResp goodsListByAdmin = esService.getGoodsListByAdmin(goodsInfosAdminReq);
                if (pages == 0) {
                    if ("success".equals(goodsListByAdmin.getMessage())) {
                        total = (int)goodsListByAdmin.getTotal();
                        //根据总条数计算总页数
                        if (goodsListByAdmin.getTotal() % pageNum > 0) {
                            pages = (int)goodsListByAdmin.getTotal() / pageNum + 1;
                        }else {
                            pages = (int)goodsListByAdmin.getTotal() / pageNum;
                        }
                    }
                }
                if (Objects.nonNull(goodsListByAdmin.getgInfos())) {
                    goodsInfoss = goodsListByAdmin.getgInfos();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(goodsInfoss == null)
                goodsInfoss = new ArrayList<>();

            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            //如果表格的第一位为空，则表示没有设置
            if(sheet.getRow(0) == null || sheet.getRow(0).getCell(0) == null){
                throw new IOException("未设置表头.");
            }
            //表格的风格取表头的风格
            HSSFCellStyle contentAreaStyle = ExcelOperator.createContentAreaStyle(hssfWorkbook);
            List<String> cols = exportParams.getCols();
            goodsInfoss.stream().forEach(goodsInfos -> {
                HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
                for (int i = 0 ; i < cols.size(); i++) {
                    HSSFCell cell = row.createCell(i);
                    cell.setCellStyle(contentAreaStyle);
                    HSSFRichTextString text = null;

                    String s1 = cols.get(i);
                    if ("商品编码".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getGoods_code())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            text = new HSSFRichTextString(goodsInfos.getGoods_code());
                        }
                    }else if ("批准文号".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getApproval_number())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            text = new HSSFRichTextString(goodsInfos.getApproval_number());
                        }
                    }else if ("商品条形码".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getBar_code())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            text = new HSSFRichTextString(goodsInfos.getBar_code());
                        }
                    }else if ("商品名".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getDrug_name())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            text = new HSSFRichTextString(goodsInfos.getDrug_name());
                        }
                    }else if ("通用名".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getCom_name())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            text = new HSSFRichTextString(goodsInfos.getCom_name());
                        }
                    }else if ("药品类别".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getDrug_category())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            Long drug_category = goodsInfos.getDrug_category();
                            if (110 == drug_category) {
                                text = new HSSFRichTextString("甲类非处方药");
                            }else if (120 == drug_category) {
                                text = new HSSFRichTextString("乙类非处方药");
                            }else if (130 == drug_category) {
                                text = new HSSFRichTextString("处方药");
                            }else if (140 == drug_category) {
                                text = new HSSFRichTextString("双轨药");
                            }else if (150 == drug_category) {
                                text = new HSSFRichTextString("非方剂");
                            }else if (160 == drug_category) {
                                text = new HSSFRichTextString("方剂");
                            }else if (170 == drug_category) {
                                text = new HSSFRichTextString("一类");
                            }else if (180 == drug_category) {
                                text = new HSSFRichTextString("二类");
                            }else if (190 == drug_category) {
                                text = new HSSFRichTextString("三类");
                            }else {
                                text = new HSSFRichTextString("其他");
                            }
                        }
                    }else if ("规格".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getSpecif_cation())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            text = new HSSFRichTextString(goodsInfos.getSpecif_cation());
                        }
                    }else if ("生产企业".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getGoods_company())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            text = new HSSFRichTextString(goodsInfos.getGoods_company());
                        }
                    }else if ("商品标题".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getGoods_title())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            text = new HSSFRichTextString(goodsInfos.getGoods_title());
                        }
                    }else if ("现价".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getShop_price())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            BigDecimal bd = new BigDecimal(goodsInfos.getShop_price());
                            bd = bd.setScale(2, RoundingMode.HALF_UP);
                            //现价除以100保留两位小数
                            BigDecimal divide = new BigDecimal(goodsInfos.getShop_price()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                            text = new HSSFRichTextString(divide.toString());
                        }
                    }else if ("库存".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getIn_stock())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            text = new HSSFRichTextString(String.valueOf(goodsInfos.getIn_stock()));
                        }
                    }else if ("限购数量".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getControl_num())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            text = new HSSFRichTextString(String.valueOf(goodsInfos.getControl_num()));
                        }
                    }else if ("购买方式".equals(s1)) {
                        /*CONCAT((CASE bg.purchase_way
                            WHEN 110 THEN '电脑端:显示立即购买，购物车'
                        WHEN 120 THEN '电脑端:显示手机扫码购买'
                        WHEN 130 THEN '电脑端:显示立即购买，购物车，手机扫码购买'
                        WHEN 140 THEN '电脑端:显示该商品仅供展示'
                        ELSE '电脑端:显示立即购买，购物车'
                        END ) ,
                        (CASE bg.wx_purchase_way
                        WHEN 110 THEN ' 移动端:显示立即购买，购物车'
                        WHEN 120 THEN ' 移动端:显示该商品仅供展示'
                        WHEN 130 THEN ' 移动端:显示预约购买'
                        ELSE ' 移动端:显示立即购买，购物车'
                        END )) as 购买方式*/

                        StringBuffer sb = new StringBuffer();
                        if (StringUtil.isNotEmpty(String.valueOf(goodsInfos.getPurchase_way()))) {
                            int purchase_way = goodsInfos.getPurchase_way();
                            if (purchase_way == 110) {
                                sb.append("电脑端:显示立即购买，购物车 , ");
                            }else if (purchase_way == 120) {
                                sb.append("电脑端:显示手机扫码购买 , ");
                            }else if (purchase_way == 130) {
                                sb.append("电脑端:显示立即购买，购物车，手机扫码购买 , ");
                            }else if (purchase_way == 140) {
                                sb.append("电脑端:显示该商品仅供展示 , ");
                            }else {
                                sb.append("电脑端:显示立即购买，购物车 , ");
                            }
                        }else {
                            sb.append("电脑端:显示立即购买，购物车 , ");
                        }

                        if (StringUtil.isNotEmpty(goodsInfos.getWx_purchase_way())) {
                            String wx_purchase_way = goodsInfos.getWx_purchase_way();
                            if ("110".equals(wx_purchase_way)) {
                                sb.append("移动端:显示立即购买，购物车");
                            }else if ("120".equals(wx_purchase_way)) {
                                sb.append("移动端:显示该商品仅供展示");
                            }else if ("130".equals(wx_purchase_way)) {
                                sb.append("移动端:显示预约购买");
                            }else {
                                sb.append("移动端:显示立即购买，购物车");
                            }
                        }else {
                            sb.append("移动端:显示立即购买，购物车");
                        }

                            text = new HSSFRichTextString(sb.toString());
                    }

                    cell.setCellValue(text);
                }

            });



            FileOutputStream out = new FileOutputStream(file);
            hssfWorkbook.write(out);
            out.close();
        } while (pages > currentPage);
        return file;
    }




    private Method getTargetMethod(Object target, String methodName)
            throws BusinessLogicException{
        try{
            Method[] methods = target.getClass().getDeclaredMethods();
            for(Method method : methods){
                //如果参数个数为1并且是Map类型的参数，就认为是目标方法
                if(method.getName().equals(methodName)  && method.getParameterCount() == 1 &&
                        method.getParameterTypes()[0].getName().equals(Map.class.getName())){
                    return method;
                }
            }
            return null;
        }catch (Exception ex){
            throw new BusinessLogicException("查找目标方法异常,class["+target.getClass().getName()
                    +"],method[{"+methodName+"}],是否含有参数java.util.Map，请检查配置.",ex);
        }
    }
    public File exportCouponDetailList(Map<String, Object> params)throws Exception {
        File file = new File(temp_dir + "/report_" + System.currentTimeMillis() + ".xls");
        file.getParentFile().mkdirs();
        List<String> header= (List<String>) params.get("headers");
        List<String> cols= (List<String>) params.get("cols");
        HSSFWorkbook hssfWorkbook = ExcelOperator.createWorkbook("data", header);
        Integer type=params.get("type")==null?1:Integer.parseInt(params.get("type").toString());
        List<Map<String, Object>> tradesList = null;
        if(type==1) {
            tradesList = (List<Map<String, Object>>) couponRuleService.findCouponListTable(params);
        }else if(type==2){
            tradesList = (List<Map<String, Object>>) service.findCouponListTable(params);
        }
        if(tradesList == null)
            tradesList = new ArrayList<>();
        tradesList.stream().forEach(temp->{
            Object o = temp.containsKey("clerk_name") ? null : temp.put("clerk_name","--");
            o = temp.containsKey("clerk_mobile") ? null : temp.put("clerk_mobile","--");
            o = temp.containsKey("promotion_code") ? null : temp.put("promotion_code","--");
            o = temp.containsKey("store_name") ? null : temp.put("store_name","--");
            o = temp.containsKey("receiver_mobile") ? null : temp.put("receiver_mobile","--");
            o = temp.containsKey("use_time") ? null : temp.put("use_time","--");
            o = temp.containsKey("trades_id") ? null : temp.put("trades_id","--");
            Integer status = Integer.parseInt(temp.get("status").toString());
            if(status==0&&type==1){
                temp.put("status","已使用");
            }else if(status==1&&type==1){
                temp.put("status","待使用");
            }else if(status==0&&type==2){
                temp.put("status","已使用");
            }else if(status==1&&type==2){
                temp.put("status","已退款");
            }else if(status==2&&type==2){
                temp.put("status","订单取消");
            } else {
                temp.put("status","--");
            }
        });
        ExcelOperator.writeData(hssfWorkbook, cols, tradesList, ExcelOperator.createContentAreaStyle(hssfWorkbook));
        FileOutputStream out = new FileOutputStream(file);
        hssfWorkbook.write(out);
        out.close();
        return file;
    }

    /**
     * 从ES中导出数据
     * @param exportParams
     * @return
     * @throws BusinessLogicException
     * @throws IOException
     */
    @SuppressWarnings("all")
    public File getReportFromES(ExportParams exportParams) throws BusinessLogicException, IOException {
        String beanName = exportParams.getBeanName();
        String methodName = exportParams.getMethodName();
        List<String> header = exportParams.getHeader();
        if(CollectionUtils.isEmpty(exportParams.getCols())){
            exportParams.setCols(header);
        }

        //查询条件的时间加上时、分、秒
        String orderTimeStart = (String) exportParams.getParams().get("orderTimeStart");
        String orderTimeEnd = (String) exportParams.getParams().get("orderTimeEnd");
        if(!StringUtil.isEmpty(orderTimeStart)){
            orderTimeStart =  orderTimeStart + " 00:00:00";
            exportParams.getParams().put("orderTimeStart",orderTimeStart);
        }
        if(!StringUtil.isEmpty(orderTimeStart)){
            orderTimeEnd =  orderTimeEnd + " 23:59:59";
            exportParams.getParams().put("orderTimeEnd",orderTimeEnd);
        }

        //参数中将tradesStaus转换为list,并添加到参数中
        String tradesStatus = (String) exportParams.getParams().get("tradesStatus");
        if ( tradesStatus != null) {
            List list = Arrays.asList(tradesStatus.split(","));
            exportParams.getParams().put("list",list);
        }

        Object target = context.getBean(beanName);
        if(target == null){
            throw new BusinessLogicException("根据Bean名称"+beanName+"未找到相关的Bean实例，任务调度失败");
        }
        Method method = getTargetMethod(target,methodName);
        if(method == null){
            throw new BusinessLogicException("未找到匹配的方法,class["+target.getClass().getName()
                +"],method[{"+methodName+"}],是否含有参数[java.util.Map]，请检查配置.");
        }
        File file = new File(temp_dir + "/report_" + System.currentTimeMillis() + ".xls");
        file.getParentFile().mkdirs();
        int pageNum = 0,pageSize = 2000, pages = 0;
        Page page = null;
        HSSFWorkbook hssfWorkbook = ExcelOperator.createWorkbook("data", header);
        do {
            boolean isCount = false;
            if(page == null){
                isCount = true;
                page = PageHelper.startPage(++pageNum, pageSize,isCount);
            }else{
                //避免每次都发起Count查询
                PageHelper.startPage(++pageNum, pageSize,isCount);
            }
            List<Map<String, Object>> tradesList = null;

            try {
                tradesList = (List<Map<String, Object>>) ReflectionUtils.invokeMethod(method,target,exportParams.getParams());
                if(isCount){
                    pages = page.getPages();
                }
            } catch (Exception e) {
                if(e instanceof  ClassCastException)
                    throw new BusinessLogicException("匹配的方法返回参数错误,class["+target.getClass().getName()
                        +"],method[{"+methodName+"}],参数是否是[List<Map<String, Object>>]，请检查配置.");
                e.printStackTrace();
            }
            if(tradesList == null)
                tradesList = new ArrayList<>();
            ExcelOperator.writeData(hssfWorkbook, exportParams.getCols(), tradesList, ExcelOperator.createContentAreaStyle(hssfWorkbook));
            FileOutputStream out = new FileOutputStream(file);
            hssfWorkbook.write(out);
            out.close();
        } while (pages > pageNum);
        return file;
    }




    /**
     * 查询数据库导出商品分类
     * @param exportParams
     * @return
     * @throws BusinessLogicException
     * @throws IOException
     */
    public File getReportFromdb(ExportClassifyFromdb exportParams) throws BusinessLogicException, IOException {
        List<String> header = exportParams.getHeader();
        if(CollectionUtils.isEmpty(exportParams.getCols())){
            exportParams.setCols(header);
        }

        //将Map转成对象
        Map<String, Object> params = exportParams.getParams();
        String siteId = params.get("siteId").toString();


//        params.remove("siteId");
//        String s = JSON.toJSONString(params);
//        GoodsInfosAdminReq goodsInfosAdminReq = JSON.parseObject(s, GoodsInfosAdminReq.class);
//        goodsInfosAdminReq.setDbname("b_shop_" + siteId);

        File file = new File(temp_dir + "/report_" + System.currentTimeMillis() + ".xls");
        file.getParentFile().mkdirs();
//        int currentPage = 0, pageNum = 2000, pages = 0, total = 0;
//        goodsInfosAdminReq.setPageNum(pageNum);
        HSSFWorkbook hssfWorkbook = ExcelOperator.createWorkbook("data", header);//设置表头内容
//        do {
//            goodsInfosAdminReq.setCurrentPage(++currentPage);
            /*//判断当前也是否是最后一页
            if (currentPage == pages) {
                if (total > pageNum) {
                    goodsInfosAdminReq.setPageNum(total - (currentPage - 1) * pageNum);
                } else {
                    goodsInfosAdminReq.setPageNum(pageNum);
                }
            } else {
                goodsInfosAdminReq.setPageNum(pageNum);
            }*/
            List<GoodsInfos> goodsInfoss = null;
            try {
//                GoodsInfosResp goodsListByAdmin = esService.getGoodsListByAdmin(goodsInfosAdminReq);  从ES查询商品列表
                /*if (pages == 0) {
                    if ("success".equals(goodsListByAdmin.getMessage())) {
                        total = (int)goodsListByAdmin.getTotal();
                        //根据总条数计算总页数
                        if (goodsListByAdmin.getTotal() % pageNum > 0) {
                            pages = (int)goodsListByAdmin.getTotal() / pageNum + 1;
                        }else {
                            pages = (int)goodsListByAdmin.getTotal() / pageNum;
                        }
                    }
                }
                if (Objects.nonNull(goodsListByAdmin.getgInfos())) {
                    goodsInfoss = goodsListByAdmin.getgInfos();

                }*/

            } catch (Exception e) {
                e.printStackTrace();
            }

            //从数据库查询分类
            List<Map<String,Object>> classifies = smallTicketMapper.queryGoodsClassify(siteId);


//            if(goodsInfoss == null)
//                goodsInfoss = new ArrayList<>();

            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            //如果表格的第一位为空，则表示没有设置
            if(sheet.getRow(0) == null || sheet.getRow(0).getCell(0) == null){
                throw new IOException("未设置表头.");
            }
            //表格的风格取表头的风格
            HSSFCellStyle contentAreaStyle = ExcelOperator.createContentAreaStyle(hssfWorkbook);
            List<String> cols = exportParams.getCols();

            classifies.stream().forEach(classify -> {
                HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
                /*for (int  i = 0; i < cols.size(); i++) {
                    HSSFCell cell = row.createCell(i);
                    cell.setCellStyle(contentAreaStyle);
                    HSSFRichTextString text = null;
                }*/
                HSSFCell cell1 = row.createCell(0);//第一列分类编号
                cell1.setCellStyle(contentAreaStyle);
                HSSFRichTextString text1 = null;
                if (StringUtil.isEmpty(classify.get("cate_code"))) {
                    text1 = new HSSFRichTextString(StringUtils.EMPTY);
                } else {
                    text1 = new HSSFRichTextString(classify.get("cate_code").toString());
                }
                cell1.setCellValue(text1);

                HSSFCell cell2 = row.createCell(1);//第二列分类名称
                cell2.setCellStyle(contentAreaStyle);
                HSSFRichTextString text2 = null;
                if (StringUtil.isEmpty(classify.get("cate_name"))) {
                    text2 = new HSSFRichTextString(StringUtils.EMPTY);
                } else {
                    text2 = new HSSFRichTextString(classify.get("cate_name").toString());
                }
                cell2.setCellValue(text2);
            });


            /*goodsInfoss.stream().forEach(goodsInfos -> {
                HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
                for (int i = 0 ; i < cols.size(); i++) {
                    HSSFCell cell = row.createCell(i);
                    cell.setCellStyle(contentAreaStyle);
                    HSSFRichTextString text = null;

                    String s1 = cols.get(i);



                    if ("商品编码".equals(s1)) {
                        if(StringUtil.isEmpty(goodsInfos.getGoods_code())){
                            text = new HSSFRichTextString(StringUtils.EMPTY);
                        }else{
                            text = new HSSFRichTextString(goodsInfos.getGoods_code());
                        }
                    }else if ("购买方式".equals(s1)) {
                        *//*CONCAT((CASE bg.purchase_way
                            WHEN 110 THEN '电脑端:显示立即购买，购物车'
                        WHEN 120 THEN '电脑端:显示手机扫码购买'
                        WHEN 130 THEN '电脑端:显示立即购买，购物车，手机扫码购买'
                        WHEN 140 THEN '电脑端:显示该商品仅供展示'
                        ELSE '电脑端:显示立即购买，购物车'
                        END ) ,
                        (CASE bg.wx_purchase_way
                        WHEN 110 THEN ' 移动端:显示立即购买，购物车'
                        WHEN 120 THEN ' 移动端:显示该商品仅供展示'
                        WHEN 130 THEN ' 移动端:显示预约购买'
                        ELSE ' 移动端:显示立即购买，购物车'
                        END )) as 购买方式*//**//*

                        StringBuffer sb = new StringBuffer();
                        if (StringUtil.isNotEmpty(String.valueOf(goodsInfos.getPurchase_way()))) {
                            int purchase_way = goodsInfos.getPurchase_way();
                            if (purchase_way == 110) {
                                sb.append("电脑端:显示立即购买，购物车 , ");
                            }else if (purchase_way == 120) {
                                sb.append("电脑端:显示手机扫码购买 , ");
                            }else if (purchase_way == 130) {
                                sb.append("电脑端:显示立即购买，购物车，手机扫码购买 , ");
                            }else if (purchase_way == 140) {
                                sb.append("电脑端:显示该商品仅供展示 , ");
                            }else {
                                sb.append("电脑端:显示立即购买，购物车 , ");
                            }
                        }else {
                            sb.append("电脑端:显示立即购买，购物车 , ");
                        }

                        if (StringUtil.isNotEmpty(goodsInfos.getWx_purchase_way())) {
                            String wx_purchase_way = goodsInfos.getWx_purchase_way();
                            if ("110".equals(wx_purchase_way)) {
                                sb.append("移动端:显示立即购买，购物车");
                            }else if ("120".equals(wx_purchase_way)) {
                                sb.append("移动端:显示该商品仅供展示");
                            }else if ("130".equals(wx_purchase_way)) {
                                sb.append("移动端:显示预约购买");
                            }else {
                                sb.append("移动端:显示立即购买，购物车");
                            }
                        }else {
                            sb.append("移动端:显示立即购买，购物车");
                        }*//*

                        text = new HSSFRichTextString(sb.toString());
                    }

                    cell.setCellValue(text);
                }

            });*/



            FileOutputStream out = new FileOutputStream(file);
            hssfWorkbook.write(out);
            out.close();
//        } while (pages > currentPage);
        return file;
    }
}
