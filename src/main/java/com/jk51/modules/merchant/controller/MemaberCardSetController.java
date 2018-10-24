package com.jk51.modules.merchant.controller;

import com.jk51.model.merchant.MemberCardSet;
import com.jk51.modules.merchant.service.MemberCardSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/11/2
 * 修改记录:
 */
@Controller
@RequestMapping("cardSet")
public class MemaberCardSetController {

    @Autowired private MemberCardSetService cardSetService;


    @RequestMapping("/getBySiteId")
    public @ResponseBody MemberCardSet getBySiteId(Integer siteId){
        return cardSetService.getBySiteId(siteId);
    }

    @RequestMapping("/upd")
    public @ResponseBody Integer updCardSet(MemberCardSet cardSet){
        return cardSetService.updCardSet(cardSet);
    }

    @RequestMapping("/add")
    public @ResponseBody Integer addCardSet(MemberCardSet cardSet){
        return cardSetService.addCardSet(cardSet);
    }

}
