package com.jk51.modules.merchant.controller;

import com.jk51.model.approve.FaceApproveSet;
import com.jk51.modules.merchant.service.FaceApproveSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:  人脸识别后台设置
 * 作者: chen_pt
 * 创建日期: 2018/1/20
 * 修改记录:
 */
@Controller
@RequestMapping("/faceApprove/set")
public class FaceApproveSetController {

    @Autowired private FaceApproveSetService faceApproveSetService;


    @RequestMapping("/getBySiteId")
    public @ResponseBody FaceApproveSet getBySiteId(Integer siteId){
        return faceApproveSetService.getBySiteId(siteId);
    }

    @RequestMapping("/upd")
    public @ResponseBody Integer upd(FaceApproveSet faceApproveSet){
        return faceApproveSetService.upd(faceApproveSet);
    }

    @RequestMapping("/add")
    public @ResponseBody Integer add(FaceApproveSet faceApproveSet){
        return faceApproveSetService.add(faceApproveSet);
    }
}
