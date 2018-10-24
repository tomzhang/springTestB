package com.jk51.modules.marketing.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.marketing.request.MarketingMemberParm;
import com.jk51.modules.marketing.service.MarketingMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 中奖记录
 * 作者: chen_pt
 * 创建日期: 2018/3/19
 * 修改记录:
 */
@Controller
@RequestMapping("marketing/member")
public class MarketingMemberController {

    @Autowired
    private MarketingMemberService marketingMemberService;


    /**
     * 获取商户中奖记录
     * @param parms
     * @return
     */
    @RequestMapping("/getLst")
    public @ResponseBody PageInfo getLst(MarketingMemberParm parms){
        PageHelper.startPage(parms.getPageNum(),parms.getPageSize());
        if(StringUtil.isNotEmpty(parms.getType_info()) && parms.getType_info().endsWith("积分")){
            parms.setType_info(parms.getType_info().replace("积分",""));
        }
        List<Map> mapLst = marketingMemberService.getLst(parms);
        return new PageInfo<>(mapLst);
    }

    /**
     * 状态标记
     * @param siteId
     * @param id 记录Id
     * @param status
     * @return
     */
    @RequestMapping("/changeStatus")
    public @ResponseBody Integer changeStatus(Integer siteId, Integer id, Integer status,String remark){
        return marketingMemberService.changeStatus(siteId, id, status,remark);
    }



}
