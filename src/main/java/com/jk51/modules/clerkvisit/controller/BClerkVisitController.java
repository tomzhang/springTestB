package com.jk51.modules.clerkvisit.controller;

import com.github.binarywang.java.emoji.EmojiConverter;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.clerkvisit.BClerkVisit;
import com.jk51.modules.appInterface.service.AppClerkVisitService;
import com.jk51.modules.clerkvisit.service.BClerkVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 * Created by Administrator on 2017/12/7.
 */
@Controller
@RequestMapping("/visit")
public class BClerkVisitController {
    @Autowired
    private BClerkVisitService bClerkVisitService;
    @Autowired
    private AppClerkVisitService appClerkVisitService;
    private static EmojiConverter emojiConverter = EmojiConverter.getInstance();

    @RequestMapping("/updateVisit")
    @ResponseBody
    public ReturnDto updateVisit(BClerkVisit bClerkVisit){
        Map<String,Object> param=new HashMap();
        param.put("siteId",bClerkVisit.getSiteId());
        param.put("visitId",bClerkVisit.getId());
        Map<String,Object> feedBack=bClerkVisitService.queryFeedBack(param);
        if(Objects.nonNull(feedBack)){
            //实际回访时间是否大于应回访时间-延时回访,status:50
            Date nowDate=new Date();
            LocalDate visitDate = ZonedDateTime.ofInstant( bClerkVisit.getVisitTime().toInstant(), ZoneId.systemDefault()).toLocalDate();
            LocalDate realVisitDate = ZonedDateTime.ofInstant(nowDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
            int res= realVisitDate.compareTo(visitDate);
            if(res > 0 ){
                bClerkVisit.setStatus(Byte.valueOf("50"));
            }

            int result = bClerkVisitService.updateVisitById(bClerkVisit);
            if(result >0){
                return ReturnDto.buildSuccessReturnDto("修改成功");
            }else {
                return ReturnDto.buildFailedReturnDto("修改失败");
            }
        }else{
            return ReturnDto.buildFailedReturnDto("暂未反馈，无法标记");
        }

    }

    /**
     * 回访反馈
     * @param request
     * @return
     */
    @RequestMapping("/feedBack")
    @ResponseBody
    public ReturnDto feedBack(HttpServletRequest request){
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
        if(Objects.nonNull(param.get("content"))){
            param.put("content",emojiConverter.toAlias(param.get("content").toString()));
        }
        Map<String,Object> feedBack=bClerkVisitService.queryFeedBack(param);
        boolean flag=false;
        if(Objects.nonNull(feedBack)){
            int result=bClerkVisitService.updateFeedBack(param);
            if(result > 0) flag =true;
        }else{
            int result = bClerkVisitService.insertFeedBack(param);
            if(result > 0) flag =true;
        }
        if(flag){
            return ReturnDto.buildSuccessReturnDto("反馈成功");
        }else {
            return ReturnDto.buildFailedReturnDto("反馈失败");
        }
    }

    @RequestMapping("/queryFeedBack")
    @ResponseBody
    public ReturnDto queryFeedBack(HttpServletRequest request){
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
        if(Objects.nonNull(bClerkVisitService.queryFeedBack(param))){
            return ReturnDto.buildSuccessReturnDto(bClerkVisitService.queryFeedBack(param));
        }else{
            return ReturnDto.buildFailedReturnDto("无反馈记录");
        }

    }

    @RequestMapping("/visitLog")
    @ResponseBody
    public ReturnDto visitLog(HttpServletRequest request){
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
        List<Map<String,Object>> list= appClerkVisitService.queryReturnVisitLog(Integer.valueOf(param.get("siteId").toString()),Integer.valueOf(param.get("buyerId").toString()));
        if(list.size() > 0){
            return ReturnDto.buildSuccessReturnDto(list);
        }else {
            return ReturnDto.buildFailedReturnDto("未查询到相关记录");
        }

    }

    @RequestMapping("/transpondVisit")
    @ResponseBody
    public ReturnDto transpondVisit(HttpServletRequest request){
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        boolean flag=bClerkVisitService.transpondVisit(param);
        if(flag){
            return ReturnDto.buildSuccessReturnDto("任务转出成功");
        }else{
            return ReturnDto.buildFailedReturnDto("任务转出失败");
        }
    }

    @RequestMapping("/storeAdminVistList")
    @ResponseBody
    public ReturnDto getStoreAdminVistList(Integer siteId,Integer storeId){
        return ReturnDto.buildSuccessReturnDto(bClerkVisitService.getStoreAdminVistList(siteId,storeId));
    }

    @RequestMapping("/queryVisitInfos")
    @ResponseBody
    public ReturnDto queryVisitInfos(Integer bvsId,Integer siteId){
        return ReturnDto.buildSuccessReturnDto(bClerkVisitService.queryVisitInfos(bvsId,siteId));
    }

}
