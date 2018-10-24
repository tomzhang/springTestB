package com.jk51.modules.merchant.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.concession.ConcessionCalculateBaseImpl;
import com.jk51.modules.exportreport.service.ExportClassifyFromdb;
import com.jk51.modules.merchant.service.RelevanceSaleService;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.R;
import static javafx.scene.input.KeyCode.S;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName RelevanceSaleController
 * @Description 关联销售
 * @Date 2018-09-15 11:46
 */
@RequestMapping("/RelevanceSale")
@Controller
public class RelevanceSaleController {

    public static final Logger LOGGER = LoggerFactory.getLogger(RelevanceSaleController.class);

    @Autowired
    private RelevanceSaleService relevanceSaleService;


    /**
     * 分页搜索
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryRelevanceCategory",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryClassifyBySite(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        if (Objects.isNull(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空!");
        }
        Integer pageNum = 1;
        Integer pageSize = 10;
        String num = parameterMap.get("pageNum").toString();
        String size = parameterMap.get("pageSize").toString();
        if (StringUtil.isNotEmpty(num)) {
            pageNum = Integer.valueOf(num);
        }
        if (StringUtil.isNotEmpty(size)) {
            pageSize = Integer.valueOf(size);
        }
        ReturnDto returnDto = relevanceSaleService.queryClassifyList(parameterMap, pageNum, pageSize);
        return returnDto;
    }

    /**
     * 插入或更新
     * @return
     */
    @RequestMapping(value = "/insertOrUpdate",method = RequestMethod.POST)
    @ResponseBody
    public  ReturnDto insertOrUpdateLog(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        String siteId = parameterMap.get("siteId").toString();
        String cateCode = parameterMap.get("cateCode").toString();
        if (StringUtil.isEmpty(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空!");
        }
        if (StringUtil.isEmpty(cateCode)) {
            return ReturnDto.buildFailedReturnDto("cateCode不能为空!");
        }
        Map<String,Object> map = relevanceSaleService.queryClassifyLog(siteId, cateCode);
        int i = 0;
        //是否关联
        String relevanceList = parameterMap.get("relevanceList").toString();
        if (StringUtil.isNotEmpty(relevanceList)) {
            parameterMap.put("isRelevance", 1);//有关联
        }else {
            parameterMap.put("isRelevance", 0);//无关联
        }
        if (Objects.isNull(map)) { //插入
            //前端传过来即可
//            parameterMap.put("isPause", 1); //0暂停 1正常
            parameterMap.put("isDelete", 0); //0未删  1删除
            i = relevanceSaleService.insertClassifyLog(parameterMap);
        }else {//更新
            i = relevanceSaleService.updateClassifyLog(parameterMap);
        }
        if (i == 1) {
            return ReturnDto.buildSuccessReturnDto("操作成功!");
        }else {
            return ReturnDto.buildSuccessReturnDto("操作失败!");
        }
    }

    @RequestMapping(value = "/relevanceImportFile", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto importRelevanceFile(HttpServletRequest request) {
        LOGGER.info("导入关联销售开始！");
        //siteId   relevanceSale导入的数据集合 List<Map<String, Object>>
        //map结构为:   cateCode, cateName, relevance1, relevance2, relevance3, relevance4, relevanceReson
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        if (Objects.isNull(parameterMap))
            return ReturnDto.buildFailedReturnDto("传入参数不能为空！");
        Object siteIdObj = parameterMap.get("siteId");
        if (Objects.isNull(siteIdObj))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        Object relevanceSale = parameterMap.get("relevanceSale");
        if (Objects.isNull(relevanceSale))
            return ReturnDto.buildFailedReturnDto("导入表格数据不能为空！");
        List<Map<String, Object>> list = JSON.parseObject(String.valueOf(relevanceSale), List.class);

        Map<String,Object> problemData = relevanceSaleService.batchInsertOrUpdate(Integer.valueOf(siteIdObj.toString()), list);

        if (Objects.nonNull(problemData)) {
//            return ReturnDto.buildFailedReturnDto(JSON.toJSONString(problemData));
//            return ReturnDto.buildFailedReturnDto("共有"+problemData.size()+"失败!      "+JSON.toJSONString(problemData));
            return ReturnDto.buildSuccessReturnDto(problemData);
        }else {
            return ReturnDto.buildSuccessReturnDto("上传关联分类文件成功!");
        }

    }

    /**
     * 清除关联分类
     * @param request
     * @return
     */
    @RequestMapping(value = "/clearThisLog",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto clearRelevanceLog(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Integer result = relevanceSaleService.clearClassifyLog(parameterMap);
        if(1 == result) {
            return ReturnDto.buildSuccessReturnDto("清除数据成功!");
        }else {
            return ReturnDto.buildFailedReturnDto("清除数据失败!");
        }
    }

    /**
     * 模糊查询分类名
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryCateNameByLike", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryClassifyName(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        List<Map<String,Object>> nameList = relevanceSaleService.queryCateNameByLike(parameterMap);
        if (nameList.size() > 0) {
            return ReturnDto.buildSuccessReturnDto(nameList);
        }else {
            return ReturnDto.buildFailedReturnDto("没有查询到相关分类!");
        }
    }


    /**
     * 根据名称查询分类
     * @param request
     * @return
     */
    @RequestMapping(value = "/queeyCategoryByCateName",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryClassifyByCateName(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Map<String,Object> resultMap = relevanceSaleService.queryCategoryByName(parameterMap);
        if (Objects.isNull(resultMap)) {
            return ReturnDto.buildFailedReturnDto("输入的分类名称有误, 没有查询到对应分类!");
        }else {
            return  ReturnDto.buildSuccessReturnDto(resultMap);
        }
    }

    /**
     * 暂停及启用
     * @param request
     * @return
     */
    @RequestMapping(value = "/pauseOrStart",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto pauseUse(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Integer result = relevanceSaleService.pauseOrStart(parameterMap);
        if(1 == result) {
            return ReturnDto.buildSuccessReturnDto("操作成功!");
        }else {
            return ReturnDto.buildFailedReturnDto("操作失败!");
        }
    }


    /**
     * 从Redis下载数据
     * @param exportParams
     * @param response
     * @throws IOException
     */
    @RequestMapping("/exportFromRedis")
    @ResponseBody
    public void exportReportFromdb(@RequestBody ExportClassifyFromdb exportParams, HttpServletResponse response) throws IOException{
        File file = null;
        RandomAccessFile raf = null;
        OutputStream responseOS = response.getOutputStream();
        try {
            responseOS = response.getOutputStream();
            file = relevanceSaleService.getReportFromRedis(exportParams);


            response.addHeader("Content-Length", "" + file.length());
            response.setHeader("content-disposition",
                "attachment;filename=" + new String(file.getName().getBytes("UTF-8"), "ISO-8859-1"));
            raf = new RandomAccessFile(file, "rw");
            byte[] buffer = new byte[1024 * 1024];
            int avariable = -1;
            while ((avariable = raf.read(buffer)) > 0) {
                responseOS.write(buffer, 0, avariable);
            }
            buffer = null;
            responseOS.flush();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }  finally {
            if(raf != null)
                raf.close();
            if(file != null)
                file.delete();
            if(responseOS != null)
                responseOS.close();
        }

    }
}
