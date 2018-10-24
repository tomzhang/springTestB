package com.jk51.modules.pc.controller;

import com.jk51.model.pc.Help;
import com.jk51.modules.pc.service.HelpService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 帮助中心
 * 作者: chen_pt
 * 创建日期: 2017/11/15
 * 修改记录:
 */
@Controller
@RequestMapping("help")
public class HelpController {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(HelpController.class);

    @Autowired private HelpService helpService;

    @RequestMapping("add")
    public @ResponseBody Integer add(Help help){
        return helpService.add(help);
    }

    @RequestMapping("upd")
    public @ResponseBody Integer upd(Help help){
        return helpService.upd(help);
    }

    @RequestMapping("getLst")
    public @ResponseBody List<Help> getLst(Integer siteId){

        List<Help> firLst = helpService.getLst(siteId);//一级帮助中心
        if(firLst.size()>0){
            for (Help help : firLst) {
                List<Help> secLst = helpService.getLstByFirTitle(siteId,help.getFirTitle());//二级帮助中心
                help.setSecHelpLst(secLst);
            }
        }

        return firLst;
    }

    @RequestMapping("getBySecTitle")
    public @ResponseBody Help getBySecTitle(Integer siteId, String firTitle, String secTitle){
        return helpService.getBySecTitle(siteId, firTitle, secTitle);
    }

}
