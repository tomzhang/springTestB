package com.jk51.modules.distribution.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.distribution.service.DistributorExtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/27.
 */
@Controller
@RequestMapping("/distributionext")
public class DistributorExtController {
    @Autowired
    private DistributorExtService distributorExtService;

    @RequestMapping("/insertDistributorExt")
    public Map<String,Object> insertDistributorExt(HttpServletRequest request){
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String msg=distributorExtService.insert(param) >0 ?"success":"error";
        resultMap.put("msg",msg);
        return  resultMap;
    }


    @RequestMapping("/updateDistributorExt")
    public Map<String,Object> updateDistributorExt(HttpServletRequest request){
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String msg=distributorExtService.updateByExampleSelective(param) >0 ?"success":"error";
        resultMap.put("msg",msg);
        return resultMap;
    }
}
