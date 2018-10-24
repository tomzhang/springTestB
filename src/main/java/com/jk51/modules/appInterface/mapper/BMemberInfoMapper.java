package com.jk51.modules.appInterface.mapper;


import com.jk51.model.order.SBMemberInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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
public interface BMemberInfoMapper {

    Integer findCountRegistNum(@Param("invitationCode") String invitationCode, @Param("time") String time, @Param("siteId") int siteId);

    List<SBMemberInfo> findMemberInfoList(@Param("invitationCode") String invitation_code, @Param("date") String date, @Param("siteId") int siteId);

    List<Date> getRegisteDate(@Param("invitationCode") String invitation_code, @Param("month") String month, @Param("siteId") int siteId);


    /**
     * 门店后台：添加会员Info
     * 返回主键
     *
     * @param memberInfo
     * @return
     */
    Integer insert(SBMemberInfo memberInfo);

    /**
     * 门店后台：添加会员Info
     * 返回主键
     *
     * @param memberInfo
     * @return
     */
    Integer insertSelective(SBMemberInfo memberInfo);

    SBMemberInfo getMemberInfo(@Param("member_id") Integer memberId, @Param("site_id") Integer siteId);

    Integer updateMemberInfoByMemberId(SBMemberInfo memberInfo);

    Integer updateMemberInfo(Map map);

    List<Map<String, Object>> appselectmembernum(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("storeUserId") Integer storeUserId, @Param("start") String start, @Param("end") String end);

    List<Map<String, Object>> appselectmembernum2(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("storeUserId") Integer storeUserId, @Param("start") String start, @Param("end") String end);

    /**
     * erp对接查询线上会员注册信息
     *
     * @param siteId
     * @param mobile
     * @return
     */
    Map<String, Object> erpGetMemberInfo(@Param("siteId") Integer siteId, @Param("mobile") String mobile, @Param("invite_code") String invite_code);

    Integer updateCardNo(@Param("siteId") Integer siteId, @Param("mobile") String mobile, @Param("cardNo") String cardNo, @Param("buyerId") Integer buyerId);

    //查询erp会员对应的注册门店信息
    Map<String, Object> selectRegistMemInfo(@Param("siteId") Integer siteId, @Param("mobile") String mobile);
}
