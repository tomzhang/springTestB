package com.jk51.modules.distribution.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.distribute.Recruit;
import com.jk51.modules.distribution.service.DistributionService;
import com.jk51.modules.distribution.service.RecruitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/9.
 */
@Controller
@ResponseBody
@RequestMapping("/recruit")
public class RecruitController {

    @Autowired
    private RecruitService recruitService;

    @Autowired
    private DistributionService distributionService;

    /**
     * 根据商家ID获取招募信息
     * @param
     * @return
     */
    @RequestMapping("/getrecruit")
    @ResponseBody
    public Map<String,Object> getrecruit(HttpServletRequest request){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        List<Recruit> recruitList =  recruitService.getRecruitListByOwner(String.valueOf(param.get("owner")));
        if(recruitList.size() >0){
            resultMap.put("recruit",recruitList.size()!=0?recruitList.get(0):recruitList);
            resultMap.put("msg","success");
        }else{
            resultMap.put("msg","error");
        }

        return resultMap;
    }

    /**
     *获取商家下分销商总数 status = 1
     * @param
     * @return
     */
    @RequestMapping("/getsumdistributor")
    public Map<String,Object> getsumdistributor(HttpServletRequest request){
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        int total =  recruitService.getDistributorTotal(String.valueOf(param.get("owner")), 1);
        resultMap.put("total", total);
        return resultMap;
    }

    /**
     *编辑招募
     * @return
     */
    @RequestMapping("/editRecruit")
    @ResponseBody
    public Map<String,Object> editRecruit(@RequestBody Map param){
       // Map<String,Object> paramsMap = ParameterUtil.getParameterMap(request);
        //Map<String,Object> resultMap = new HashMap<String,Object>();
        return recruitService.editRecruit(param);
    }

    /**
     *添加招募
     * @param request
     * @return
     */
    @RequestMapping("/addRecruit")
    public Map<String,Object> addRecruit(HttpServletRequest request){
        Map<String,Object> paramsMap = ParameterUtil.getParameterMap(request);
        return recruitService.editRecruit(paramsMap);
    }


}
