package com.jk51.modules.balance.controller;

import com.jk51.model.balance.BaseFeeSet;
import com.jk51.modules.balance.service.BaseFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 基础模块
 * 作者: chen_pt
 * 创建日期: 2018/5/11
 * 修改记录:
 */
@Controller
@RequestMapping("/jk51b")
public class BaseFeeController {

    @Autowired
    private BaseFeeService baseFeeService;


    @RequestMapping("/getBaseFee")
    public @ResponseBody BaseFeeSet getBaseFee(Integer siteId, Integer id){

        return baseFeeService.getBaseFee(siteId, id);
    }

    @RequestMapping("/getBaseFeeLst")
    public @ResponseBody List<BaseFeeSet> getBaseFeeLst(Integer siteId){

        return baseFeeService.getBaseFeeLst(siteId);
    }

    @RequestMapping("/addBaseFee")
    public @ResponseBody Integer addBaseFee(BaseFeeSet record){

        return baseFeeService.addBaseFee(record);
    }

    @RequestMapping("/updBaseFee")
    public @ResponseBody Integer updBaseFee(BaseFeeSet record){

        return baseFeeService.updBaseFee(record);
    }

    @RequestMapping("/delBaseFee")
    public @ResponseBody Integer delBaseFee(Integer siteId,Integer id){

        return baseFeeService.delBaseFee(siteId,id);
    }

}
