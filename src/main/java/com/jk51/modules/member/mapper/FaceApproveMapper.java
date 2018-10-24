package com.jk51.modules.member.mapper;

import com.jk51.model.approve.FaceApprove;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/1/29
 * 修改记录:
 */
@Mapper
public interface FaceApproveMapper {

    Integer add(FaceApprove faceApprove);

    List<FaceApprove> getBySiteIdAndMemberId(@Param("siteId") Integer siteId,@Param("memberId") Integer memberId,@Param("type") Integer type);
}
