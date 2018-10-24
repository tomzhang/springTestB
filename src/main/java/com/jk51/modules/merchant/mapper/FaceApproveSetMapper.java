package com.jk51.modules.merchant.mapper;

import com.jk51.model.approve.FaceApproveSet;
import org.apache.ibatis.annotations.Mapper;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/1/20
 * 修改记录:
 */
@Mapper
public interface FaceApproveSetMapper {
    
    FaceApproveSet getBySiteId(Integer siteId);

    Integer upd(FaceApproveSet faceApproveSet);

    Integer add(FaceApproveSet faceApproveSet);
}
