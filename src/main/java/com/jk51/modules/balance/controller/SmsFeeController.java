package com.jk51.modules.balance.controller;

import com.jk51.model.balance.SmsFeeRule;
import com.jk51.model.balance.SmsFeeSet;
import com.jk51.modules.balance.service.SmsFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 短信模块
 * 作者: chen_pt
 * 创建日期: 2018/5/11
 * 修改记录:
 */
@Controller
@RequestMapping("/jk51b")
public class SmsFeeController {

    @Autowired
    private SmsFeeService smsFeeService;


    @RequestMapping("/getSmsFeeSet")
    public @ResponseBody SmsFeeSet getSmsFeeSet(Integer siteId){

        return smsFeeService.getSmsFeeSet(siteId);
    }

    @RequestMapping("/addSmsFeeSet")
    public @ResponseBody Integer addSmsFeeSet(SmsFeeSet record){

        return smsFeeService.addSmsFeeSet(record);
    }

    @RequestMapping("/updSmsFeeSet")
    public @ResponseBody Integer updSmsFeeSet(SmsFeeSet record){

        return smsFeeService.updSmsFeeSet(record);
    }




    @RequestMapping("/getSmsFeeRuleLst")
    public @ResponseBody List<SmsFeeRule> getSmsFeeRuleLst(Integer siteId){

        return smsFeeService.getSmsFeeRuleLst(siteId);
    }

    @RequestMapping("/addSmsFeeRule")
    public @ResponseBody Integer addSmsFeeRule(SmsFeeRule record){

        return smsFeeService.addSmsFeeRule(record);
    }

    @RequestMapping("/updSmsFeeRule")
    public @ResponseBody Integer updSmsFeeRule(SmsFeeRule record){

        return smsFeeService.updSmsFeeRule(record);
    }

    @RequestMapping("/delSmsFeeRule")
    public @ResponseBody Integer delSmsFeeRule(Integer siteId, Integer id){

        return smsFeeService.delSmsFeeRule(siteId,id);
    }

}
