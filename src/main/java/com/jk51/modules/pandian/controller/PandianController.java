package com.jk51.modules.pandian.controller;

import com.github.pagehelper.Page;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.BPandianFile;
import com.jk51.model.BPandianPlan;
import com.jk51.model.erp.StoreOrderNum;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.offline.service.OfflineCheckService;
import com.jk51.modules.pandian.Response.PandianPlanInfo;
import com.jk51.modules.pandian.param.StatusParam;
import com.jk51.modules.pandian.service.PandianService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RequestMapping("/pandian")
@RestController
public class PandianController {
    private static final Logger logger = LoggerFactory.getLogger(PandianController.class);
    @Autowired
    private PandianService pandianService;
    @Autowired
    private OfflineCheckService offlineCheckService;

    @RequestMapping("/addPlan")
    public Map<String, Object> addPlan(@RequestBody BPandianPlan pandianPlan) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = pandianService.addPlan(pandianPlan);
        } catch (Exception e) {
            logger.error("addPlan 异常 {}", e);
            result.put("status", "ERROR");
            result.put("message", "盘点计划添加异常！");
        }
        return result;
    }

    @RequestMapping("/stopOrDelPlan")
    public Map<String, Object> stopOrDelPlan(BPandianPlan pandianPlan) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = pandianService.stopOrDelPlan(pandianPlan);
        } catch (Exception e) {
            logger.error("stopOrDelPlan 异常 {}", e);
            result.put("status", "ERROR");
            result.put("message", "盘点计划停用异常！");
        }
        return result;
    }

    @RequestMapping("/getPlanDetail")
    public Result getPlanDetail(Integer siteId, Integer storeId, Integer id) {
        Result result;
        if (siteId == null || id == null) {
            result = Result.fail("缺少必要参数");
        } else {
            try {
                Object planList = pandianService.getPlanDetail(siteId, storeId, id);
                result = Result.success(planList);
            } catch (Exception e) {
                logger.error("getPlanDetail 异常 {}", e);
                result = Result.fail("查询异常");
            }
        }
        return result;
    }

    @RequestMapping("/getPlanList")
    public Result getPlanList(Integer siteId, Integer storeId, Integer source, Integer id,
                              Integer planType, String startTime, String endTime,
                              @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                              @RequestParam(required = false, defaultValue = "15") Integer pageSize) {
        Result result;
        if (siteId == null) {
            result = Result.fail("缺少必要参数");
        } else {
            if (StringUtils.isNotBlank(startTime))  startTime += " 00:00:00";
            if (StringUtils.isNotBlank(endTime)) endTime += " 23:59:59";
            try {
                List<PandianPlanInfo> planList = pandianService.getPlanList(siteId, storeId, source, id, planType, startTime, endTime, pageNum, pageSize);
                result = Result.success(planList instanceof Page ? ((Page) planList).toPageInfo() : planList);
            } catch (Exception e) {
                logger.error("getPlanList 异常 {}", e);
                result = Result.fail("查询异常");
            }
        }
        return result;
    }

    @RequestMapping("/getPlanClerks")
    public Result planClerks(Integer siteId, Integer id, Integer storeId) {
        Result result;
        if (siteId == null || id == null || storeId == null) {
            result = Result.fail("缺少必要参数。");
        } else {
            try {
                List<String> clerks = pandianService.getPlanClerks(siteId, id, storeId);
                result = Result.success(clerks);
            } catch (Exception e) {
                logger.error("planClerks {} {}", e.getMessage(), e);
                result = Result.fail("查询服务异常。");
            }
        }
        return result;
    }

    /*@RequestMapping("/addFile")
    public Map<String, Object> addFile(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = pandianService.addFile(params);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", "上传异常！");
        }
        return result;
    }*/
    @RequestMapping("/addFile")
    public Callable<Result> addFile(String fileName, String fileURL, String fileKey, String pdNum, Integer siteId, Integer option, Integer storeId) {
        return () -> pandianService.addFile(fileName, fileURL, fileKey, pdNum, siteId, option, storeId);
    }

    @RequestMapping("/getFileRes")
    public Callable<Result> getFileRes(String pdfFlag) {
        return () -> pandianService.getFileRes(pdfFlag);
    }

    @RequestMapping("/updateFileStatus")
    public Map<String, Object> updateFileStatus(BPandianFile bPandianFile) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = pandianService.updateFileStatus(bPandianFile);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", "更新异常！");
        }
        return result;
    }

    @RequestMapping("/reportBef")
    public Map<String, Object> reportBef(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Map<String, Object> params = ParameterUtil.getParameterMap(request);
            result = pandianService.reportBef(params);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", "下载异常！");
        }
        return result;
    }

    @RequestMapping("/exportReport")
    public void exportReport(@RequestBody Map<String, Object> params, HttpServletResponse response) throws IOException {
        File file = null;
        RandomAccessFile raf = null;
        OutputStream responseOS = response.getOutputStream();
        try {
            responseOS = response.getOutputStream();
            file = pandianService.exportReport(params);
            response.addHeader("Content-Length", "" + file.length());
            response.setHeader("content-disposition", "attachment;filename=" + new String(file.getName().getBytes("UTF-8"), "ISO-8859-1"));
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
        } finally {
            if (raf != null)
                raf.close();
            if (file != null)
                file.delete();
            if (responseOS != null)
                responseOS.close();
        }
    }

    @RequestMapping("/updateUpSite")
    public Map<String, Object> updateUpSite(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Map<String, Object> params = ParameterUtil.getParameterMap(request);
            result = pandianService.updateUpSite(params);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", "上传设置异常");
        }
        return result;
    }

    @RequestMapping("getPanDianOrderStatus")
    public Map<String, Object> getPanDianOrderStatus(@RequestBody StatusParam statusParam) {
        return pandianService.getPanDianOrderStatus(statusParam);
    }

    @RequestMapping("/getPDStoreList")
    public Map<String, Object> getPDStoreList(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> params = ParameterUtil.getParameterMap(request);
            result = pandianService.getPDStoreList(params);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", e.toString());
        }
        return result;
    }

    @RequestMapping("/getPDClerkList")
    public Map<String, Object> getPDClerkList(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> params = ParameterUtil.getParameterMap(request);
            result = pandianService.getPDClerkList(params);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", e.toString());
        }
        return result;
    }

    @RequestMapping("/getPDRemind")
    public Map<String, Object> getPDRemind(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> params = ParameterUtil.getParameterMap(request);
            result = pandianService.getPDRemind(params);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", e.toString());
        }
        return result;
    }

    @RequestMapping("/erpData_get")
    public Map<String, Object> erpData_get(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> params = ParameterUtil.getParameterMap(request);
            Integer siteId = Integer.parseInt(params.get("siteId").toString());
            String pandianNum = params.get("pandianNum").toString();
            String unitNO = params.get("unitNO").toString();
            StoreOrderNum storeOrderNum = offlineCheckService.getBillidByUnitNO(siteId, pandianNum, unitNO);
            if (StringUtil.isEmpty(storeOrderNum.getBillid())) {
                result.put("code", "100");
                result.put("msg", "ERP未找到对应的盘点作业单，请在ERP操作后再来点击ERP库存读取。");
                return result;
            } else {
                result.put("code", "200");
                result.put("msg", storeOrderNum.getBillid());
                return result;
            }
        } catch (Exception e) {
            logger.info("method:/pandian/erpData_get,reson:{}", e.getMessage());
            result.put("code", "400");
            result.put("msg", "ERP未找到对应的盘点作业单，请在ERP操作后再来点击ERP库存读取。");
            return result;
        }
    }

    @RequestMapping("/pdErpSync")
    public Map<String, Object> pdErpSync(Integer siteId, Integer orderId, String storeIds) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Integer> storeId = Arrays.stream(storeIds.split(",")).map(id -> Integer.parseInt(id)).collect(Collectors.toList());
            result = pandianService.pdErpSync(siteId, orderId, storeId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", e.toString());
        }
        return result;
    }

    @RequestMapping("/testInfo")
    public Set<String> testInfo(Integer siteId, String pdNum) {
        Set<String> result = new HashSet<>();
        try {
            result = pandianService.testInfo(siteId, pdNum);
        } catch (Exception e) {
            result.add("error");
        }
        return result;
    }


    @RequestMapping("/planOrderNum")
    public Result planOrderNum(Integer id) {
        Result result;
        if (id == null) {
            result = Result.fail("缺少必要参数");
        } else {
            try {
                Integer total = pandianService.planOrderNum(id);
                result = Result.success(total);
            } catch (Exception e) {
                logger.error("planOrderNum 异常 {}", e);
                result = Result.fail("查询异常");
            }
        }
        return result;
    }
}
