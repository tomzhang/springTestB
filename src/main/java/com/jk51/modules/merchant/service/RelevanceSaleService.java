package com.jk51.modules.merchant.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.excel.ExcelOperator;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.modules.es.entity.GoodsInfos;
import com.jk51.modules.exportreport.service.ExportClassifyFromdb;
import com.jk51.modules.merchant.mapper.MemberCardSetMapper;
import com.jk51.modules.smallTicket.mapper.SmallTicketMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName RelevanceSaleService
 * @Description 关联销售
 * @Date 2018-09-15 13:54
 */
@Service
public class RelevanceSaleService {


    public  static final Logger LOGGER = LoggerFactory.getLogger(RelevanceSaleService.class);

    @Autowired
    private MemberCardSetMapper memberCardSetMapper;

    @Autowired
    private SmallTicketMapper smallTicketMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${report.temp_dir}")
    private String temp_dir;

    /**
     * 关联分类中 relevance_classify 存储的数据结构
     *   {
     *     "relevance1" : {"cateCode" : "", "cateName" : ""},
     *     "relevance2" : {"cateCode" : "", "cateName" : ""},
     *     "relevance3" : {"cateCode" : "", "cateName" : ""},
     *     "relevance4" : {"cateCode" : "", "cateName" : ""}
     *    }
     */
    public ReturnDto queryClassifyList(Map<String, Object> parameterMap, Integer pageNum, Integer pageSize) {
        Map<String, Object> map = new HashedMap();
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>>  classifyList = memberCardSetMapper.queryCategoryList(parameterMap);
        if (Objects.isNull(classifyList) || classifyList.size() <= 0) {
            return ReturnDto.buildFailedReturnDto("没有查询分类列表!");
        }
        PageInfo<Map<String, Object>> allList = new PageInfo<>(classifyList);
        map.put("currentPage", allList.getPageNum());
        map.put("pages", allList.getPages());
        map.put("pageSize", allList.getPageSize());
        map.put("totalRecord", allList.getTotal());
        //处理集合中的关联信息
        /*classifyList.stream().forEach(relevancejson -> {
            Map map1 = JSON.parseObject(String.valueOf(relevancejson.get("relevanceClassify")), Map.class);
            map1.put("relevance1", JSON.parseObject(map1.get("relevance1").toString(), Map.class));
            map1.put("relevance2", JSON.parseObject(map1.get("relevance2").toString(), Map.class));
            map1.put("relevance3", JSON.parseObject(map1.get("relevance3").toString(), Map.class));
            map1.put("relevance4", JSON.parseObject(map1.get("relevance4").toString(), Map.class));
            relevancejson.put("relevanceClassify", map1);
        });*/
        map.put("classifyList", classifyList);
        return ReturnDto.buildSuccessReturnDto(map);
    }

    //查询分类记录
    public Map<String, Object> queryClassifyLog(String siteId, String cateCode) {
        return memberCardSetMapper.queryCategoryLog(siteId, cateCode);
    }

    //新增关联分类记录
    public int insertClassifyLog(Map<String, Object> parameterMap) {
        return memberCardSetMapper.insertClassifyLog(parameterMap);
    }

    //更新关联分类记录
    public int updateClassifyLog(Map<String, Object> parameterMap) {
        return memberCardSetMapper.updateClassifyLog(parameterMap);
    }


    //导入关联分类
    @SuppressWarnings("all")
    public Map<String, Object> batchInsertOrUpdate(Integer siteId, List<Map<String, Object>> relevanceSale) {
        LOGGER.info("导入关联分类方法记时开始--------" + LocalDateTime.now());

        List<String> existIdsList = null;
        //记录需要插入的分类编号集合
        List<String> insertIdsList = null;
        //记录记录需要更新的分类编号集合
        List<String> updateIdsList = null;
        //上传文件中分类编号集合
        List<String> uploadIdsList = null;
        //保存处理完之后的新数据集合
        List<Map<String,Object>> newRelevanceList = new ArrayList<>();
        //记录错误信息集合
        List<Map<String, Object>> errorResultList = new ArrayList<Map<String, Object>>();
        //记录成功数据集合
        List<Map<String,Object>> successResultList = new ArrayList<>();


        //查询已存在的关联分类记录
        List<Map<String,Object>> classifyList = memberCardSetMapper.queryAlreadyExist(siteId);
        if (classifyList.size() > 0) {
            //过滤出已存在分类编号集合
            existIdsList = classifyList.stream().map(m -> {return m.get("cateCode");})
                .map(String::valueOf)
                .collect(toList());
            //过滤出上传文件中的分类编号集合
            uploadIdsList = relevanceSale.stream().map(m -> {return m.get("cateCode");}).map(String::valueOf).collect(toList());
            //获取需要新增的分类编号集合
            uploadIdsList.removeAll(existIdsList);
            insertIdsList = uploadIdsList;
            //更新的分类编码集合
            updateIdsList = classifyList.stream().map(m -> {return m.get("cateCode");}).map(String::valueOf).collect(toList());
        }else {//全部新增
            //过滤出上传文件中的分类编号集合
            uploadIdsList = relevanceSale.stream().map(m -> {return m.get("cateCode");}).map(String::valueOf).collect(toList());
            insertIdsList = uploadIdsList;
        }
        //查询所有三级分类集合
        List<Map<String,Object>> classisfyList = smallTicketMapper.queryGoodsClassify(siteId.toString());
        //创建一个有cate_name组成的集合
        List<String> cateNameList = classisfyList.stream().map(map -> {return map.get("cate_name");}).map(String::valueOf).map(String::trim).collect(toList());



        //处理上传文件集合中每个关联分类
        relevanceSale.stream().forEach(relevance -> {
            Map<String,Object> map = new HashedMap();
            //cateCode, cateName, relevance1, relevance2, relevance3, relevance4, relevanceReson
            Object cateCode = relevance.get("cateCode");
            String cateName = String.valueOf(relevance.get("cateName"));
            String relevance1 = (StringUtil.isNotEmpty(String.valueOf(relevance.get("relevance1")))? String.valueOf(relevance.get("relevance1")) : "").trim();
            String relevance2 = (StringUtil.isNotEmpty(String.valueOf(relevance.get("relevance2")))? String.valueOf(relevance.get("relevance2")) : "").trim();
            String relevance3 = (StringUtil.isNotEmpty(String.valueOf(relevance.get("relevance3")))? String.valueOf(relevance.get("relevance3")) : "").trim();
            String relevance4 = (StringUtil.isNotEmpty(String.valueOf(relevance.get("relevance4")))? String.valueOf(relevance.get("relevance4")) : "").trim();
            String relevanceReson = StringUtil.isNotEmpty(String.valueOf(relevance.get("relevanceReson")))? String.valueOf(relevance.get("relevanceReson")) : "";
            if ((!"null".equals(relevance1) && StringUtil.isNotEmpty(relevance1) && !cateNameList.contains(relevance1)) || (!"null".equals(relevance2) && StringUtil.isNotEmpty(relevance2) && !cateNameList.contains(relevance2)) ||
            (!"null".equals(relevance3) && StringUtil.isNotEmpty(relevance3) && !cateNameList.contains(relevance3)) || (!"null".equals(relevance4) && StringUtil.isNotEmpty(relevance4) && !cateNameList.contains(relevance4))) {
                //将该记录添加到错误信息集合
                errorResultList.add(relevance);
            }else {
                successResultList.add(relevance);
            }
            StringBuffer sb = new StringBuffer();
            int flag = 0;
            sb.append("{");
            sb.append("\"relevance1\" : {\"cateCode\" : \"");
            if (cateNameList.contains(relevance1)) {
                /*String catec = classisfyList.stream().map(clas -> {
                    if (relevance1.equals(clas.get("cate_name"))) {
                        return clas.get("cate_code");
                    } else {
                        return "";
                    }
                }).toString();*/
                Map<String, Object> map1 = classisfyList.stream().filter(clas -> {
                    return relevance1.equals(clas.get("cate_name").toString().trim());
                }).findFirst().get();
                if (Objects.nonNull(map1)) {
                    sb.append(map1.get("cate_code")+ "\"");
                    sb.append(", \"cateName\" : \"");
                    sb.append(relevance1 + "\"");
                }else {
                    sb.append("\"");
                    sb.append(", \"cateName\" : ");
                    sb.append("\"\"");
                }
                sb.append("},");
            }else {
                sb.append("\"");
                sb.append(", \"cateName\" : ");
                sb.append("\"\"");
                sb.append("},");
                flag++;
            }


            sb.append("\"relevance2\" : {\"cateCode\" : \"");
            if (cateNameList.contains(relevance2)) {
                /*String catec = classisfyList.stream().map(clas -> {
                    if (relevance2.equals(clas.get("cate_name"))) {
                        return clas.get("cate_code");
                    } else {
                        return "";
                    }
                }).toString();*/
                Map<String, Object> map1 = classisfyList.stream().filter(clas -> {
                    return relevance2.equals(clas.get("cate_name").toString().trim());
                }).findFirst().get();
                if (Objects.nonNull(map1)) {
                    sb.append(map1.get("cate_code") + "\"");
                    sb.append(", \"cateName\" : \"");
                    sb.append(relevance2 + "\"");
                }else {
                    sb.append("\"");
                    sb.append(", \"cateName\" : ");
                    sb.append("\"\"");
                }
                sb.append("},");
            }else {
                sb.append("\"");
                sb.append(", \"cateName\" : ");
                sb.append("\"\"");
                sb.append("},");
                flag++;
            }



            sb.append("\"relevance3\" : {\"cateCode\" : \"");
            if (cateNameList.contains(relevance3)) {
                /*String catec = classisfyList.stream().map(clas -> {
                    if (relevance3.equals(clas.get("cate_name"))) {
                        return clas.get("cate_code");
                    } else {
                        return "";
                    }
                }).toString();*/
                Map<String, Object> map1 = classisfyList.stream().filter(clas -> {
                    return relevance3.equals(clas.get("cate_name").toString().trim());
                }).findFirst().get();
                if (Objects.nonNull(map1)) {
                    sb.append(map1.get("cate_code") + "\"");
                    sb.append(", \"cateName\" : \"");
                    sb.append(relevance3 + "\"");
                }else {
                    sb.append("\"");
                    sb.append(", \"cateName\" : ");
                    sb.append("\"\"");
                }
                sb.append("},");
            }else {
                sb.append("\"");
                sb.append(", \"cateName\" : \"");
                sb.append("" + "\"");
                sb.append("},");
                flag++;
            }


            sb.append("\"relevance4\" : {\"cateCode\" : \"");
            if (cateNameList.contains(relevance4)) {
                /*String catec = classisfyList.stream().map(clas -> {
                    if (relevance4.equals(clas.get("cate_name"))) {
                        return clas.get("cate_code");
                    } else {
                        return "";
                    }
                }).toString();*/
                Map<String, Object> map1 = classisfyList.stream().filter(clas -> {
                    return relevance4.equals(clas.get("cate_name").toString().trim());
                }).findFirst().get();
                if (Objects.nonNull(map1)) {
                    sb.append(map1.get("cate_code") + "\"");
                    sb.append(", \"cateName\" : \"");
                    sb.append(relevance4 + "\"");
                }else {
                    sb.append("\"");
                    sb.append(", \"cateName\" : ");
                    sb.append("\"\"");
                }
                sb.append("}}");
            }else {
                sb.append("\"");
                sb.append(", \"cateName\" : \"");
                sb.append("" + "\"");
                sb.append("}}");
                flag++;
            }

            // cateCode, cateName, relevanceList,relevanceReson
            /**
             relevanceList: * {
             "relevance1" : {"cateCode" : "", "cateName" : ""},
             "relevance2" : {"cateCode" : "", "cateName" : ""},
             "relevance3" : {"cateCode" : "", "cateName" : ""},
             "relevance4" : {"cateCode" : "", "cateName" : ""}
             }
             */
            map.put("cateCode",cateCode);
            map.put("cateName",cateName);
            if (4 == flag) {
                map.put("relevanceList","");
            }else {
                map.put("relevanceList",sb.toString());
            }

            if (StringUtil.isNotEmpty(relevanceReson) && !"null".equals(relevanceReson)) {
                map.put("relevanceReson",relevanceReson);
            }else {
                map.put("relevanceReson","");
            }
            map.put("isPause", 1);//未暂停
            map.put("isDelete", 0);//未删除
            /*if (cateNameList.contains(relevance1) || cateNameList.contains(relevance2) || cateNameList.contains(relevance3) || cateNameList.contains(relevance4)) {
                map.put("isRelevance", 1);
            }else {
                map.put("isRelevance", 0);
            }*/
            if (StringUtil.isNotEmpty(String.valueOf(map.get("relevanceList")))) {
                map.put("isRelevance", 1);
            }else {
                map.put("isRelevance", 0);
            }
            map.put("siteId", siteId);
            newRelevanceList.add(map);
        });




        Map<String,Object> map = new HashedMap();
//        map.put("newRelevanceList", newRelevanceList);
        //插入
        int resultIn = 0;
        List<Map<String,Object>> newRele2 = null;
        if (Objects.nonNull(insertIdsList) && insertIdsList.size() > 0) {
            //过滤出包含有新增编号的集合
            List<String> newRele = insertIdsList;
            //过滤符合插入条件的集合
            newRele2 = newRelevanceList.stream().filter(newRe -> {
                if (newRele.contains(newRe.get("cateCode"))) {
                    return true;
                }else {
                    return false;
                }
            }).collect(toList());

            map.put("newRelevanceList", newRele2);


//            map.put("insertIdsList", insertIdsList);
            resultIn = memberCardSetMapper.addClassifyList(map);//批量插入
        }
        int resultUp = 0;
        List<Map<String,Object>> newRele3 = null;
        if (Objects.nonNull(updateIdsList) && updateIdsList.size() > 0) {
//            map.put("uodateIdsList", uodateIdsList);
            //过滤
            List<String> updateIdsList2 = updateIdsList;
            newRele3 = newRelevanceList.stream().filter(newRe -> {
                if (updateIdsList2.contains(newRe.get("cateCode"))) {
                    return true;
                }else {
                    return false;
                }
            }).collect(toList());
            map.put("newRelevanceList", newRele3);

            resultUp = memberCardSetMapper.updateClassifyList(map);//批量更新
        }
        //判断是否有还未更新的数据
        if (resultIn > 0) {
            newRelevanceList.removeAll(newRele2);
        }
        if (resultUp > 0) {
            newRelevanceList.removeAll(newRele3);
        }
        //区分全部成功及部分成功
        if (errorResultList.size() == 0 && newRelevanceList.size() == 0) {
            return null;
        }else {
            //部分成功, 将成功及失败的数据存储的redis, 以供下载
//            stringRedisTemplate.opsForValue().set("UserStoresDistance_" + s , "");
//            redisTemplate.opsForValue().set(accessTokenKey, accessToken.getAccess_token(), 2 * 60, TimeUnit.MINUTES);

            long l = System.currentTimeMillis();
            String succKey = "success"+l;
            String errkey = "error"+l;

            //void set(K key, V value, long timeout, TimeUnit unit);
            stringRedisTemplate.opsForValue().set(succKey, JSON.toJSONString(successResultList), 10, TimeUnit.MINUTES);
            stringRedisTemplate.opsForValue().set(errkey, JSON.toJSONString(errorResultList), 10, TimeUnit.MINUTES);
            Map<String,Object> dataResult = new HashedMap(){{put("successkey",succKey);put("errorkey",errkey);}};
            return dataResult;
        }
        /*if (newRelevanceList.size() > 0) {

            return newRelevanceList;
        }else {
            return null;
        }*/
    }

    //清除
    public Integer clearClassifyLog(Map<String, Object> parameterMap) {
        return  memberCardSetMapper.clearRelevanceLog(parameterMap);
    }

    public List<Map<String,Object>> queryCateNameByLike(Map<String, Object> parameterMap) {
        return memberCardSetMapper.selectCateName(parameterMap);
    }

    public Map<String, Object> queryCategoryByName(Map<String, Object> parameterMap) {
        return memberCardSetMapper.selectClassifyByName(parameterMap);
    }

    //暂停使用
    public Integer pauseOrStart(Map<String, Object> parameterMap) {
        return memberCardSetMapper.pauseOrStart(parameterMap);
    }


    /**
     * 从Redis获取数据库导出商品分类
     * @param exportParams
     * @return
     * @throws BusinessLogicException
     * @throws IOException
     */
    public File getReportFromRedis(ExportClassifyFromdb exportParams) throws BusinessLogicException, IOException {
        List<String> header = exportParams.getHeader();
        if(CollectionUtils.isEmpty(exportParams.getCols())){
            exportParams.setCols(header);
        }

        //将Map转成对象
        Map<String, Object> params = exportParams.getParams();
//        String siteId = params.get("siteId").toString();
        String redisKey = params.get("redisKey").toString();


        File file = new File(temp_dir + "/report_" + System.currentTimeMillis() + ".xls");
        file.getParentFile().mkdirs();
        HSSFWorkbook hssfWorkbook = ExcelOperator.createWorkbook("data", header);//设置表头内容

//        List<GoodsInfos> goodsInfoss = null;

        //从数据库查询分类
//        List<Map<String,Object>> classifies = smallTicketMapper.queryGoodsClassify(siteId);

        List<Map<String,Object>> list = null;
        String s = stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtil.isNotEmpty(s)) {
            list = JSON.parseObject(s, List.class);
        }


        HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
        //如果表格的第一位为空，则表示没有设置
        if(sheet.getRow(0) == null || sheet.getRow(0).getCell(0) == null){
            throw new IOException("未设置表头.");
        }
        //表格的风格取表头的风格
        HSSFCellStyle contentAreaStyle = ExcelOperator.createContentAreaStyle(hssfWorkbook);
        List<String> cols = exportParams.getCols();

        list.stream().forEach(classify -> {
            HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            for (int i = 0 ; i < cols.size(); i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(contentAreaStyle);
                HSSFRichTextString text = null;

                String s1 = cols.get(i);
                if ("分类编码".equals(s1)) {
                    if(StringUtil.isEmpty(classify.get("cateCode"))){
                        text = new HSSFRichTextString(StringUtils.EMPTY);
                    }else{
                        text = new HSSFRichTextString(classify.get("cateCode").toString());
                    }
                }else if ("三级分类".equals(s1)) {
                    if(StringUtil.isEmpty(classify.get("cateName"))){
                        text = new HSSFRichTextString(StringUtils.EMPTY);
                    }else{
                        text = new HSSFRichTextString(classify.get("cateName").toString());
                    }
                }else if ("关联分类1".equals(s1)) {
                    if(StringUtil.isEmpty(classify.get("relevance1"))){
                        text = new HSSFRichTextString(StringUtils.EMPTY);
                    }else{
                        text = new HSSFRichTextString(classify.get("relevance1").toString());
                    }
                }else if ("关联分类2".equals(s1)) {
                    if(StringUtil.isEmpty(classify.get("relevance2"))){
                        text = new HSSFRichTextString(StringUtils.EMPTY);
                    }else{
                        text = new HSSFRichTextString(classify.get("relevance2").toString());
                    }
                }else if ("关联分类3".equals(s1)) {
                    if(StringUtil.isEmpty(classify.get("relevance3"))){
                        text = new HSSFRichTextString(StringUtils.EMPTY);
                    }else{
                        text = new HSSFRichTextString(classify.get("relevance3").toString());
                    }
                }else if ("关联分类4".equals(s1)) {
                    if(StringUtil.isEmpty(classify.get("relevance4"))){
                        text = new HSSFRichTextString(StringUtils.EMPTY);
                    }else{
                        text = new HSSFRichTextString(classify.get("relevance4").toString());
                    }
                }else if ("关联理由".equals(s1)) {
                    if(StringUtil.isEmpty(classify.get("relevanceReson"))){
                        text = new HSSFRichTextString(StringUtils.EMPTY);
                    }else{
                        text = new HSSFRichTextString(classify.get("relevanceReson").toString());
                    }
                }
                cell.setCellValue(text);
            }

        });

        FileOutputStream out = new FileOutputStream(file);
        hssfWorkbook.write(out);
        out.close();
//        } while (pages > currentPage);
        return file;
    }
}
