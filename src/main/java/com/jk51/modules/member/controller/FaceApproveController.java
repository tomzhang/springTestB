package com.jk51.modules.member.controller;

import com.jk51.model.approve.FaceApprove;
import com.jk51.modules.member.service.FaceApproveService;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 人脸识别
 * 作者: chen_pt
 * 创建日期: 2018/1/19
 * 修改记录:
 */
@Controller
@RequestMapping("/face/approve")
public class FaceApproveController {


    @Autowired
    private FaceApproveService faceApproveService;



    @RequestMapping("/add")
    public @ResponseBody Integer add(FaceApprove faceApprove){
        return faceApproveService.add(faceApprove);
    }

    /**
     * 获取用户的前三个认证头像or第一个
     * @param siteId
     * @param memberId
     * @return
     */
    @RequestMapping("/getBySiteIdAndMemberId")
    public @ResponseBody List<FaceApprove> getBySiteIdAndMemberId(Integer siteId, Integer memberId, Integer type){
        return faceApproveService.getBySiteIdAndMemberId(siteId,memberId,type);
    }


    /**
     * 解析人脸
     * @param faceImg
     * @return
     */
    @RequestMapping("/parseFaceImg")
    public @ResponseBody Integer parseFaceImg(Integer siteId, Integer memberId, String faceImg){
        return faceApproveService.parseFaceImg(siteId,memberId,faceImg);
    }

    /**
     * 解析人脸 不存数据库
     * @param faceImg
     * @return
     */
    @RequestMapping("/parseFaceImg2")
    public @ResponseBody FaceApprove parseFaceImg2(Integer siteId, String faceImg){
        return faceApproveService.parseFaceImg2(siteId,faceImg);
    }

}
