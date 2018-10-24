package com.jk51.modules.merchant.service;

import com.jk51.model.approve.FaceApproveSet;
import com.jk51.modules.merchant.mapper.FaceApproveSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/1/20
 * 修改记录:
 */
@Service
public class FaceApproveSetService {

    @Autowired private FaceApproveSetMapper faceApproveSetMapper;


    public FaceApproveSet getBySiteId(Integer siteId) {
        return faceApproveSetMapper.getBySiteId(siteId);
    }

    public Integer upd(FaceApproveSet faceApproveSet) {
        return faceApproveSetMapper.upd(faceApproveSet);
    }

    public Integer add(FaceApproveSet faceApproveSet) {
        FaceApproveSet set = getBySiteId(faceApproveSet.getSiteId());
        if(set!=null){
            return -1;
        }
        return faceApproveSetMapper.add(faceApproveSet);
    }
}
