package com.jk51.modules.pc.controller;

import com.jk51.model.pc.HomePage;
import com.jk51.modules.pc.service.HomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: pc首页商品配置
 * 作者: chen_pt
 * 创建日期: 2017/11/16
 * 修改记录:
 */
@Controller
@RequestMapping("homePage")
public class HomePageController {

    @Autowired
    private HomePageService homePageService;

    @RequestMapping("add")
    public @ResponseBody Integer add(HomePage homePage){
        return homePageService.add(homePage);
    }

    @RequestMapping("upd")
    public @ResponseBody Integer upd(HomePage homePage){
        return homePageService.upd(homePage);
    }

    @RequestMapping("getLst")
    public @ResponseBody List<HomePage> getLst(Integer siteId,Integer bfrom){
        return homePageService.getLst(siteId, bfrom);
    }

}
