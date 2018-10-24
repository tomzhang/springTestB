package com.jk51.modules.index.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.Target;
import com.jk51.modules.index.service.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/13.
 */
@Controller
@ResponseBody
@RequestMapping("/target")
public class TargetController {

    @Autowired
    private TargetService targetService;

    /**
     *根据主键指标ID，获取当前指标
     * @param request
     * @return
     */
    @RequestMapping("/getTarget")
    public Map<String,Object> getTargetById(HttpServletRequest request){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        Target target = targetService.getTargetById(request.getParameter("targetId"));
        resultMap.put("target", target);
        return resultMap;
    }

    /**
     *根据商家ID和一层权重ID，获取当前一层权重下指标列表
     * @param request
     * @return
     */
    @RequestMapping("/getTargetList")
    public Map<String,Object> getTargetByOwnerAndFirstWeigthId(HttpServletRequest request){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        List<Target> targetList = targetService.getTargetByOwnerAndFirstWeigthId(request.getParameter("firstWeigthId"), request.getParameter("owner"));
        resultMap.put("targetList", targetList);
        return resultMap;
    }

    /**
     *根据指标ID，更新指标表
     * @param request
     * @return
     */
    @RequestMapping("/updateTarget")
    public Map<String,Object> updateTarget(HttpServletRequest request){
        Map<String,Object> paramsMap = ParameterUtil.getParameterMap(request);
        return targetService.updateTarget(paramsMap);
    }

    /**
     *添加指标
     * @param request
     * @return
     */
    @RequestMapping("/addTarget")
    public Map<String,Object> addTarget(HttpServletRequest request){
        Map<String,Object> paramsMap = ParameterUtil.getParameterMap(request);
        return targetService.insertTarget(paramsMap);
    }



}
