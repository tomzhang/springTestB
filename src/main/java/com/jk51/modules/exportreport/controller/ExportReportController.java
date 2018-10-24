package com.jk51.modules.exportreport.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.excel.ExcelOperator;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.coupon.service.CouponRuleService;
import com.jk51.modules.esn.service.GoodsEsService;
import com.jk51.modules.exportreport.service.ExportClassifyFromdb;
import com.jk51.modules.exportreport.service.ExportParams;
import com.jk51.modules.exportreport.service.ExportParamsForES;
import com.jk51.modules.exportreport.service.ExportReportService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.smallTicket.mapper.SmallTicketMapper;
import com.jk51.modules.smallTicket.service.SmallTicketService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-16
 * 修改记录:
 */
@SuppressWarnings("Duplicates")
@Controller
public class ExportReportController {
    private static final Logger logger = LoggerFactory.getLogger(ExportReportController.class);
    @Autowired
    ExportReportService exportReportService;

    @Autowired
    private CouponRuleService couponRuleService;

    //-----增加，导入商品上下架-----start
    @Value("${report.temp_dir}")
    private String temp_dir;
    @RequestMapping("/merchant/leadIntoUpperImport")
    @ResponseBody
    public ReturnDto leadIntoUpperAndLowerImport(HttpServletRequest request) throws IOException {
        logger.info("导入商品上下架开始！");
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        if (Objects.isNull(parameterMap))
            return ReturnDto.buildFailedReturnDto("传入参数不能为空！");
        Object siteIdObj = parameterMap.get("siteId");
        if (Objects.isNull(siteIdObj))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        String goodsInfoStr = (String) parameterMap.get("goodsInfoList");
        if (StringUtils.isBlank(goodsInfoStr))
            return ReturnDto.buildFailedReturnDto("导入表格数据不能为空！");
        List<Map<String, Object>> errorGoodsInfo = exportReportService.getErrorGoodsInfo(Integer.parseInt(siteIdObj.toString()), goodsInfoStr);
        Object successGoodsIdMapObj = errorGoodsInfo.get(errorGoodsInfo.size() - 1).get("successGoodsIdMap");
        if (Objects.nonNull(successGoodsIdMapObj)){
            List<String> goodsIdList = (List<String>)successGoodsIdMapObj;
            exportReportService.updateGoodsIndex2(String.valueOf(siteIdObj), goodsIdList);
        }
        return ReturnDto.buildSuccessReturnDto(errorGoodsInfo);
    }
    @RequestMapping("/exportReport")
    @ResponseBody
    public void exportReport(@RequestBody ExportParams exportParams, HttpServletResponse response) throws IOException{
        File file = null;
        RandomAccessFile raf = null;
        OutputStream responseOS = response.getOutputStream();
        try {
            responseOS = response.getOutputStream();
            file = exportReportService.getReport(exportParams);
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
            logger.error(e.getMessage(), e);
        }  finally {
            if(raf != null)
                raf.close();
            if(file != null)
                file.delete();
            if(responseOS != null)
                responseOS.close();
        }

    }

    /**
     * 从ES导出商品列表
     * @param exportParams
     * @param response
     * @throws IOException
     */
    @RequestMapping("/exportFromES")
    @ResponseBody
    public void exportReportFromES(@RequestBody ExportParamsForES exportParams, HttpServletResponse response) throws IOException{
        File file = null;
        RandomAccessFile raf = null;
        OutputStream responseOS = response.getOutputStream();
        try {
            responseOS = response.getOutputStream();
            file = exportReportService.getReportFromES(exportParams);


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
            logger.error(e.getMessage(), e);
        }  finally {
            if(raf != null)
                raf.close();
            if(file != null)
                file.delete();
            if(responseOS != null)
                responseOS.close();
        }

    }


    @RequestMapping("getCouponTableList")
    @ResponseBody
    public Object getCouponTableList(HttpServletRequest request){
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        Map map = new HashMap();
        try {
            map = (Map<String,Object>) couponRuleService.findCouponListTable(params);
        } catch (Exception e) {
            logger.error("查询优惠券使用情况列表失败:{}",e);
            map.put("code",-1);
            map.put("total",0);
        }
        return map;
    }
    //    exportCouponDetailList
    @RequestMapping("/exportCouponDetailList")
    @ResponseBody
    public void exportCouponDetailList(@RequestBody Map<String,Object> params, HttpServletResponse response) throws IOException{
        File file = null;
        RandomAccessFile raf = null;
        OutputStream responseOS = response.getOutputStream();
        try {
            responseOS = response.getOutputStream();
            file = exportReportService.exportCouponDetailList(params);
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
            logger.error("导出优惠券使用情况列表失败{}", e);
        }  finally {
            if(raf != null)
                raf.close();
            if(file != null)
                file.delete();
            if(responseOS != null)
                responseOS.close();
        }
    }


    /**
     * 从ES中导出商品数据
     * @param exportParams
     * @param response
     * @throws IOException
     */
    @RequestMapping("/exportReportFromES")
    @ResponseBody
    public void exportReportFromES(@RequestBody ExportParams exportParams, HttpServletResponse response) throws IOException{
        File file = null;
        RandomAccessFile raf = null;
        OutputStream responseOS = response.getOutputStream();
        try {
            responseOS = response.getOutputStream();
            file = exportReportService.getReport(exportParams);
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
            logger.error(e.getMessage(), e);
        }  finally {
            if(raf != null)
                raf.close();
            if(file != null)
                file.delete();
            if(responseOS != null)
                responseOS.close();
        }

    }


    /**
     * 查询数据库导出商品列表
     * @param exportParams
     * @param response
     * @throws IOException
     */
    @RequestMapping("/exportFromdb")
    @ResponseBody
    public void exportReportFromdb(@RequestBody ExportClassifyFromdb exportParams, HttpServletResponse response) throws IOException{
        File file = null;
        RandomAccessFile raf = null;
        OutputStream responseOS = response.getOutputStream();
        try {
            responseOS = response.getOutputStream();
            file = exportReportService.getReportFromdb(exportParams);


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
            logger.error(e.getMessage(), e);
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
