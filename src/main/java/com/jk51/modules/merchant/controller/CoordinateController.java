package com.jk51.modules.merchant.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.merchant.service.CoordinateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/27.
 */
@Controller
@RequestMapping("/coordinate")
public class CoordinateController {

    @Autowired
    private CoordinateService coordinateService;

    /**
     * 添加坐标
     * @param request
     * @return
     */
    @RequestMapping(value="/insertCoordinate")
    @ResponseBody
    public Map<String,Object> insertCoordinate(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return coordinateService.insertCoordinate(params);
    }

    /**
     * 查询所有坐标
     * @param request
     * @return
     */
    @RequestMapping(value="/selectCoordinate")
    @ResponseBody
    public Map<String,Object> selectCoordinate(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return coordinateService.selectCoordinate(params);
    }

    /**
     * 查根据ID询坐标
     * @param request
     * @return
     */
    @RequestMapping(value="/selectCoordinateById")
    @ResponseBody
    public Map<String,Object> selectCoordinateById(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return coordinateService.selectCoordinateById(params);
    }

    /**
     * 修改坐标
     * @param request
     * @return
     */
    @RequestMapping(value="/updateCoordinate")
    @ResponseBody
    public Map<String,Object> updateCoordinate(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return coordinateService.updateCoordinate(params);
    }

    /**
     * 删除坐标
     * @param request
     * @return
     */
    @RequestMapping(value="/deleteCoordinate")
    @ResponseBody
    public Map<String,Object> deleteCoordinate(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return coordinateService.deleteCoordinate(params);
    }

    /**
     * 查询指定范围内的会员数(6公里内)
     * @param request
     * @return
     */
    @RequestMapping(value="/selectMemberForKilometerAll")
    @ResponseBody
    public Map<String,Object> selectMemberForKilometerAll(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return coordinateService.selectMemberForKilometerAll(params);
    }

    /**
     * 查询指定范围内的会员数(按距离)
     * @param request
     * @return
     */
    @RequestMapping(value="/selectMemberForKilometer")
    @ResponseBody
    public Map<String,Object> selectMemberForKilometer(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return coordinateService.selectMemberForKilometer(params);
    }

    /**
     * 查询指定范围内的会员数(按距离)
     * @param request
     * @return
     */
    @RequestMapping(value="/selectStoreForKilometer")
    @ResponseBody
    public Map<String,Object> selectStoreForKilometer(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return coordinateService.selectStoreForKilometer(params);
    }


}
