package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SBMemberInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-09
 * 修改记录:
 */
@Mapper
public interface SBMemberInfoMapper {

    Integer findCountRegistNum(@Param("invitationCode") String invitationCode, @Param("time") String time);

    List<SBMemberInfo> findMemberInfoList(@Param("invitationCode") String invitation_code, @Param("date") String date);

    List<Date> getRegisteDate(@Param("invitationCode") String invitation_code, @Param("month") String month);


    /**
     * 门店后台：添加会员Info
     * 返回主键
     * @param memberInfo
     * @return
     */
    Integer insert(SBMemberInfo memberInfo);

    /**
     * 门店后台：添加会员Info
     * 返回主键
     * @param memberInfo
     * @return
     */
    Integer insertSelective(SBMemberInfo memberInfo);

    SBMemberInfo getMemberInfo(@Param("member_id") Integer memberId, @Param("site_id") Integer siteId);

    Integer updateMemberInfoByMemberId(SBMemberInfo memberInfo);

    Integer updateMemberInfoByMemberId1(SBMemberInfo memberInfo);
    Integer updateMemberInfoByMemberId2(SBMemberInfo memberInfo);
    Integer updateInviteCodeByMemberId(SBMemberInfo memberInfo);
    Integer updateSecondTokenByMemberId(@Param("secondToken")String secondToken,@Param("memberId") Integer memberId, @Param("siteId") Integer siteId);

}
