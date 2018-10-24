package com.jk51.modules.member.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.model.approve.IdentityApprove;
import com.jk51.modules.distribution.result.Page;
import com.jk51.modules.member.request.IdentityApproveParm;
import com.jk51.modules.member.service.IdentityApproveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 身份认证
 * 作者: chen_pt
 * 创建日期: 2018/1/19
 * 修改记录:
 */
@Controller
@RequestMapping("/identity/approve")
public class IdentityApproveController {

    @Autowired
    private IdentityApproveService identityApproveService;


    @RequestMapping("/add")
    public @ResponseBody Integer add(IdentityApprove identityApprove){
        Integer x = identityApproveService.add(identityApprove);
        if(x==1){
            return identityApprove.getId();
        }
        return -1;
    }

    @RequestMapping("/upd")
    public @ResponseBody Integer upd(IdentityApprove identityApprove){

        return identityApproveService.upd(identityApprove);
    }

    @RequestMapping("/getByMemberIdAndSiteId")
    public @ResponseBody IdentityApprove getByMemberIdAndSiteId(Integer siteId, Integer memberId){

        return identityApproveService.getByMemberIdAndSiteId(siteId, memberId);
    }

    @RequestMapping("/getLstBySiteId")
    public @ResponseBody PageInfo<IdentityApprove> getLstBySiteId(IdentityApproveParm parms){
        PageHelper.startPage(parms.getPageNum(),parms.getPageSize());
        List<IdentityApprove> lst = identityApproveService.getLstBySiteId(parms);
        return new PageInfo<>(lst);
    }

    /**
     * 实名认证：审核
     * @param identityApprove
     * @return
     */
    @RequestMapping("/audit")
    public @ResponseBody Integer audit(IdentityApprove identityApprove){

        return identityApproveService.audit(identityApprove);
    }

    /**
     * 解析身份证照片
     * @param img
     * @return
     */
    @RequestMapping("/parseIdcardImg")
    public @ResponseBody Map<String, Object> parseIdcardImg(String img){

        return identityApproveService.parseIdcardImg(img);
    }

    /**
     * 获取人工 智能 手动 审核的数量
     * @param siteId
     * @return
     */
    @RequestMapping("/getNumByType")
    public @ResponseBody List<Map> getNumByType(Integer siteId){

        return identityApproveService.getNumByType(siteId);
    }

    /**
     * 获取已通过和未通过的数量
     * @param siteId
     * @return
     */
    @RequestMapping("/getNumByStatus")
    public @ResponseBody List<Map> getNumByStatus(Integer siteId){

        return identityApproveService.getNumByStatus(siteId);
    }


}
