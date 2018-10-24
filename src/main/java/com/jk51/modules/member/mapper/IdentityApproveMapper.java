package com.jk51.modules.member.mapper;

import com.jk51.model.approve.IdentityApprove;
import com.jk51.modules.member.request.IdentityApproveParm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/1/19
 * 修改记录:
 */
@Mapper
public interface IdentityApproveMapper {

    Integer add(IdentityApprove identityApprove);

    Integer upd(IdentityApprove identityApprove);

    Integer audit(IdentityApprove identityApprove);

    IdentityApprove getByMemberIdAndSiteId(@Param("siteId") Integer siteId, @Param("memberId") Integer memberId);

    List<IdentityApprove> getLstBySiteId(IdentityApproveParm parms);

    List<Map> getNumByType(Integer siteId);

    List<Map> getNumByStatus(Integer siteId);
}
