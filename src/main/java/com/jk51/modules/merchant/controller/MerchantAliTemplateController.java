package com.jk51.modules.merchant.controller;

import com.jk51.modules.merchant.service.MerchantAliTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/6/20
 * 修改记录:
 */
@Controller
@RequestMapping("/merchant/template")
public class MerchantAliTemplateController {

    @Autowired
    private MerchantAliTemplateService merchantAliTemplateService;


    @RequestMapping("add")
    public @ResponseBody Integer add(Integer siteId, String templateName, String templateId){
        return merchantAliTemplateService.add(siteId, templateName,templateId);
    }

    @RequestMapping("upd")
    public @ResponseBody Integer upd(Integer siteId, Integer id, String templateId){
        return merchantAliTemplateService.upd(siteId, id,templateId);
    }

    @RequestMapping("getAliTemplateLst")
    public @ResponseBody List<Map> getAliTemplateLst(Integer siteId){
        return merchantAliTemplateService.getAliTemplateLst(siteId);
    }

//    @RequestMapping("getAliTemplateIdBySiteIdAndName")
//    public @ResponseBody String getAliTemplateIdBySiteIdAndName(Integer siteId,String templateName){
//        return merchantAliTemplateService.getAliTemplateIdBySiteIdAndName(siteId,templateName);
//    }

//    @RequestMapping("del")
//    public @ResponseBody Integer del(Integer siteId, Integer id, Integer isdel){
//        return merchantAliTemplateService.del(siteId, id,isdel);
//    }

}
