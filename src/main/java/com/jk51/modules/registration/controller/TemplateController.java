package com.jk51.modules.registration.controller;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.registration.models.ServceTemplate;
import com.jk51.model.registration.models.ServceUseDetail;
import com.jk51.model.registration.requestParams.MineClassesParams;
import com.jk51.model.registration.requestParams.ServceOrderTemplateParams;
import com.jk51.model.registration.requestParams.TemplateParams;
import com.jk51.modules.registration.mapper.ServceUseDetailMapper;
import com.jk51.modules.registration.request.TemplateRequestParam;
import com.jk51.modules.registration.service.ServceUseDetailServce;
import com.jk51.modules.registration.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * filename :com.jk51.modules.registration.controller.
 * author   :zw
 * date     :2017/4/7
 * Update   :
 */

@RestController
@RequestMapping("template")
public class TemplateController {
    private static  final Logger logger= LoggerFactory.getLogger(ServiceOrderController.class);

    @Autowired
    private TemplateService templateService;
    @Autowired
    private ServceUseDetailMapper servceUseDetailMapper;
    @Autowired
    private ServceUseDetailServce servceUseDetailServce;

    /**
     * 创建排班模版
     *
     * @param templateParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/createTemplate")
    public ReturnDto createCouponRule(@RequestBody TemplateParams templateParams) {
        return templateService.createTemplate(templateParams);
    }

    /**
     * 更新排班模版
     *
     * @param templateParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/updateTemplate")
    public ReturnDto updateCouponRule(@RequestBody TemplateParams templateParams) {
            return templateService.updateTemplate(templateParams);
    }

    /**
     * @param servceUseDetail
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteServUserDetail")
    public ReturnDto deleteServUserDetail(@RequestBody ServceUseDetail servceUseDetail) {
        if(null==servceUseDetail.getServUserIds()){
            return ReturnDto.buildFailedReturnDto("servUserIds不能为空");
        }
        if(null==servceUseDetail.getSiteId()){
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        servceUseDetail.setStatus(1);
      return servceUseDetailServce.updateServceUser(servceUseDetail);
    }

    /**
     * 医生排班
     *
     * @param mineClassesParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/mineClasses")
    public ReturnDto mineClasses(@RequestBody MineClassesParams mineClassesParams) {
        return templateService.createMineClasses(mineClassesParams);
    }

    @ResponseBody
    @GetMapping(value = "/selectMineClasses/{siteId}/{goodsId}")
    public ReturnDto getCouponByActive(@PathVariable("siteId") Integer siteId, @PathVariable("goodsId") Integer goodsId) {
        if (siteId == null) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }
        if (goodsId == null) {
            return ReturnDto.buildFailedReturnDto("goodsId不能为空");
        }
        return templateService.selectMineClasses(siteId, goodsId);
    }

    /**
     * 根据storeID查询模板
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/queryTemplate")
    public ReturnDto queryTemplate(HttpServletRequest request) {
        Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
        Integer storeId = Integer.parseInt(paramMap.get("storeId").toString());
        Map<String, List<ServceTemplate>> resualtMap = new HashMap();
        try {
            resualtMap = templateService.queryTemplateByStoreId(storeId);
            return ReturnDto.buildSuccessReturnDto(resualtMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param templateRequestParam
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryAllTemplateByStore")
    public ReturnDto queryAllTemplateByPage(@RequestBody TemplateRequestParam templateRequestParam) {
        if(null==templateRequestParam.getStoreId()){
            return ReturnDto.buildFailedReturnDto("storeId不能为空");
        }
        PageInfo<?> pageInfo=null;
        try {
            pageInfo = this.templateService.queryAllTemplateByStore(templateRequestParam);
        } catch (Exception e) {
            logger.error("获取模本列表失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询模板列表出错");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     * 根据storeID查询模板
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/queryTemplateNo")
    public ReturnDto queryTemplateNo(HttpServletRequest request) {
        Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
        Integer storeId = Integer.parseInt(paramMap.get("storeId").toString());
        String templateNo = paramMap.get("templateNo").toString();
        Map<String, List<ServceTemplate>> resualtMap = new HashMap();
        try {
            resualtMap = templateService.queryTemplateNo(storeId, templateNo);
            return ReturnDto.buildSuccessReturnDto(resualtMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @ResponseBody
    @RequestMapping(value = "/queryAllTemplateByGoods")
    public ReturnDto queryAllTemplateByGoods(ServceOrderTemplateParams params) {

        if (null == params.getSiteId()) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }
        if (null == params.getGoodsId()) {
            return ReturnDto.buildFailedReturnDto("goodsId不能为空");
        }
    /*    if(null==params.getStoreId()){
            return ReturnDto.buildFailedReturnDto("storeId不能为空");
        }*/
        if (null == params.getTime()) {
            return ReturnDto.buildFailedReturnDto("time不能为空");
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("siteId", params.getSiteId());
        paramMap.put("goodsId", params.getGoodsId());
        //paramMap.put("storeId",params.getStoreId());
        paramMap.put("startTime", getFirstDayTheMonth(params.getTime()));
        paramMap.put("endTime", getLastDayTheMonth(params.getTime()));
        List<Map<String, Object>> resultList = servceUseDetailMapper.selectAllMineClassesByGoods(paramMap);
      


        return ReturnDto.buildSuccessReturnDto(resultList);

    }

    private String getFirstDayTheMonth(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day_first = null;
        try {
            Date date = sdf.parse(time);
            GregorianCalendar gcFirst = (GregorianCalendar) Calendar.getInstance();
            gcFirst.setTime(date);
            gcFirst.set(Calendar.DAY_OF_MONTH, 1);
            day_first = sdf.format(gcFirst.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day_first;
    }

    public String getLastDayTheMonth(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day_last = null;
        try {
            Date date = sdf.parse(time);
            GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
            gcLast.setTime(date);
            gcLast.set(Calendar.DAY_OF_MONTH, gcLast.getActualMaximum(Calendar.DAY_OF_MONTH));
            day_last = sdf.format(gcLast.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day_last;
    }

}
