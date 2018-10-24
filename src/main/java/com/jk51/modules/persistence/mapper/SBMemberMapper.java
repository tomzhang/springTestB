package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SBMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-09
 * 修改记录:
 */
@Mapper
public interface SBMemberMapper {

    List<SBMember> getMemberList(int userId);
    SBMember selectByPhoneNum(@Param("mobile") String mobile, @Param("site_id") Integer siteId);

    //门店后台：会员列表
    List<Map<String,Object>>getMemberMapList(Map map);
    //门店后台：会员列表
    Integer getMemberCount(Map map);

    /**
     * 门店后台：添加会员
     * 返回member_id
     * @param member
     * @return
     */
    Integer insert(SBMember member);

    /**
     * 门店后台：添加会员
     * 返回member_id
     * @param member
     * @return
     */
    Integer insertSelective(SBMember member);

    Integer selectByPhoneNumCount(@Param("mobile") String mobile, @Param("site_id") Integer siteId, @Param("register_stores") Integer storeId);

    SBMember getMember(@Param("member_id") Integer memberId, @Param("site_id") Integer siteId, @Param("register_stores") Integer storeId);

    Integer updateMemberByMemberId(SBMember member);

    Integer updateMemberByMemberId1(SBMember member);

    Integer updateMemberByMemberId2(SBMember member);

    List<Map<String,Object>> selectAll();

    void updateLastLoginTime(@Param("siteId") Integer siteId, @Param("memberId") Integer memberId);

   List<Map<String,Object>> getOfflineInfo(@Param("siteId") Integer siteId, @Param("mobile") String mobile);

    void updateIdNum(@Param("siteId") Integer siteId, @Param("mobile") String mobile, @Param("certif_no") String certif_no, @Param("age") Integer age);

    List<Map<String,Object>> getCardNo(@Param("siteId") Integer siteId, @Param("mobile") String mobile);

    SBMember findMemberBySiteIdAndMobile(@Param("siteId") int siteId, @Param("mobile") String mobile);


    String getOpenId(@Param("siteId") Integer siteId,@Param("buyerId")Integer buyerId);

    SBMember selectByOpenId(@Param("openId") String openId, @Param("siteId") Integer siteId);

    SBMember getMembersBByAliUserID(@Param("aliUserID") String aliUserID, @Param("siteId") Integer siteId);

    Integer getbMemberAll(@Param("siteId") Integer siteId);

    List<Map<String,Object>> getUserAndInfoAndCocernBySiteId(@Param("siteId") Integer siteId);


}
