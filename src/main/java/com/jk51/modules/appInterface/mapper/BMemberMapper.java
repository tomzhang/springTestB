package com.jk51.modules.appInterface.mapper;

import com.jk51.model.order.BMember;
import com.jk51.modules.im.service.iMRecode.response.Member;
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
public interface BMemberMapper {

    List<Map<String, Object>> findRegisterInfo(@Param("member_id") int member_id, @Param("siteId") int siteId);

    List<BMember> getMemberList(int userId);

    BMember selectByPhoneNum(@Param("mobile") String mobile, @Param("site_id") Integer siteId);

    //门店后台：会员列表
    List<Map<String, Object>> getMemberMapList(Map map);

    //门店后台：会员列表
    Integer getMemberCount(Map map);

    /**
     * 门店后台：添加会员
     * 返回member_id
     *
     * @param member
     * @return
     */
    Integer insertSelective(BMember member);

    Integer selectByPhoneNumCount(@Param("mobile") String mobile, @Param("site_id") Integer siteId, @Param("register_stores") Integer storeId);

    BMember getMember(@Param("member_id") Integer memberId, @Param("site_id") Integer siteId, @Param("register_stores") Integer storeId);

    BMember getMemberBySiteIdAndMemberId(@Param("member_id") Integer memberId, @Param("site_id") Integer siteId);

    Map<String,Object> getMemberByBuyerId(@Param("buyerId") Integer buyerId, @Param("siteId") Integer siteId);

    Integer updateMemberByMemberId(BMember member);

    List<Map<String, Object>> selectAll();

    void updateLastLoginTime(@Param("siteId") Integer siteId, @Param("memberId") Integer memberId);

    List<Map<String, Object>> getOfflineInfo(@Param("siteId") Integer siteId, @Param("mobile") String mobile);

    void updateIdNum(@Param("siteId") Integer siteId, @Param("mobile") String mobile, @Param("certif_no") String certif_no, @Param("age") Integer age);

    List<Map<String, Object>> getCardNo(@Param("siteId") Integer siteId, @Param("mobile") String mobile);

    BMember findMemberBySiteIdAndMobile(@Param("siteId") int siteId, @Param("mobile") String mobile);

    Map<String, Object> selectMemberMapByPhoneNum(@Param("siteId") String siteId, @Param("mobile") String mobile);

    int updateFirstErp(@Param("siteId") int siteId, @Param("mobile") String mobile);

    int updateMember(Map map);

    int updateOfflineIntegral(Map map);

    List<Member> findBySiteIdAndMobile(@Param("site_id") int site_id, @Param("mobile") String mobile);

    List<Map<String, Object>> selectMemberBySiteIdAndMobile(@Param("siteId") Integer siteId, @Param("mobile") String mobile);

    Map<String, Object> selectMemberInfoToWX(@Param("siteId") int siteId, @Param("mobile") String mobile);

    //根据openId查询是否已注册
    Map<String,Object> queryIsMember(Map<String, Object> parameterMap);

    String queryMemberCardId(@Param("siteId") Integer siteId);
}
