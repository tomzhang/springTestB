package com.jk51.modules.trades.mapper;

import com.jk51.model.order.Member;
import com.jk51.model.order.YBMember;
import com.jk51.model.packageEntity.StoreAdminCloseIndex;
import com.jk51.modules.appInterface.util.MemberInfo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-02-20
 * 修改记录:
 */
@Mapper
public interface MemberMapper {
    void updateIntegral(Member member);

    Member getMember(int siteId, int buyerId);

    List<StoreAdminCloseIndex> findStoreAdminCloseIndex(@Param("now") Date now, @Param("before") Date before);

    Member findUserAndPasswordByMembersId(Integer siteId, String mobile, String passwd);

    /**
     * 通过手机号查找会员
     *
     * @param siteId
     * @param mobile
     * @return
     */
    Member findMobileById(Integer siteId, String mobile);

    //查询所有会员用于发放优惠券
    List<Member> findAllMember(Integer siteId);

    List<Member> findMembersByMemberIds(@Param("siteId") Integer siteId, @Param("ids") List<String> ids);

    //查询所有会员用于发放优惠券
    List<Member> findAllMemberByPage(@Param("siteId") int siteId, @Param("pageNum") int pageNum, @Param(value = "pageNo") int pageNo);

    void updateVipMember(@Param(value = "vipMemberAddSelectParams") Member member);

    Member getMemberByMemberId(int siteId, int memberId);

    Member getMemberByOpenId(@Param("siteId") int siteId, @Param("openId") String openId);

    Integer saveMemberInfo(@Param("member") Member member);

    Integer insertYbUser(@Param("member") YBMember member);

    YBMember selectYbMemberByPhone(String phone);

    Member selectByMobileAndSiteId(@Param("mobile") String mobile, @Param("siteId") int siteId);

    Member selectByMemberIdAndSiteId(@Param("memberId") String memberId, @Param("siteId") int siteId);


    int del(@Param("id") Integer id);

    Member getMemberById(@Param("member_id") String id);

    int update(@Param("member") Member membert);


    Map checkMember(Map m);

    int updatePassword(Map m);

    int updatePasswordByMobile(Map m);

    //-----------------------以下是web中迁移过来的方法
    HashMap queryMemberInfoByPhoneNum(@Param("phone") String phone, @Param("site_id") Integer site_id);

    Integer updateMemberInfo(Map<String, Object> params);

    Integer updateMember(Map<String, Object> params);

    Integer queryMemberExist(@Param("phone") String phone, @Param("siteId") Integer siteId);

    Member queryMember(@Param("phone") String phone, @Param("siteId") Integer siteId);

    Map queryBStoreAdminExt(@Param("site_id") int site_id, @Param("store_user_id") int store_user_id);

    Integer saveMember(Map<String, Object> parameterMap);

    Integer saveMemberInfoMap(Map<String, Object> parameterMap);

    Map<String, Object> getIntegrateByBuyerId(@Param("buyerId") String buyerId, @Param("siteId") String siteId);

    Integer saveYbMember(Map<String, Object> parameterMap);

    List<HashMap> findBTriggerByTriggerCode(Integer site_id);

    Map queryGoodCatNameByCatId(@Param("siteId") int siteId, @Param("catId") String catId);

    Integer setCheckin(@Param("siteId") Integer siteId, @Param("memberId") Integer memberId, @Param("checkinNum") Integer checkinNum);

    Map<String, Object> getCheckin(@Param("siteId") Integer siteId, @Param("memberId") Integer memberId);

    //-----统计
    List<Map<String, Object>> selectMemberCountBydays(@Param("siteId") Integer siteId, @Param("start") String startTime, @Param("end") String endTime);

    List<Map<String, Object>> getPullNewCount(@Param("siteId") Integer siteId, @Param("nowDay") String nowDay);

    List<Member> queryMemberListForCouponActive(@Param("siteId") Integer siteId, @Param("memberlist") Set paramSet);

    //-----查询
    @MapKey("phone")
    Map<String, Map<String, Object>> queryForImportByPhoneAndSiteId(@Param("phoneList") List<String> phoneList, @Param("siteId") Integer siteId);

    String findMobilesByMemberIds(@Param("siteId") Integer siteId, @Param("ids") List<String> ids);

    List<Map<String, Object>> queryMembersInfoByMemberIds(@Param("siteId") Integer siteId, @Param("ids") List<String> ids);

    List<Map<String, Object>> selectMemberInfoList(Map<String, Object> params);

    int updateOpenId(@Param("member") Member membert);

    String findMobileByIdByTime(@Param("siteId") Integer siteId, @Param("mobile") String mobile);

    //查询APP会员详情
    Map<String,Object> queryMemberInfoByBuyerId(@Param("buyerId") Integer buyerId,@Param("siteId") Integer siteId);

    Map<String,Object> getMembersLngAndLat(@Param("siteId")Integer siteId, @Param("memberId")Integer memberId);

    Map<String,Object> calculateDistance(Map<String,Object> map);

    List<String> queryMemberId(@Param("siteId") Integer siteId);

    MemberInfo getMemberByMobile(@Param("siteId") Integer siteId, @Param("mobile") String mobile);
    //APP查询会员列表
    List<Map<String,Object>> queryCustomerByInfo(Map<String, Object> parameterMap);

    List<Map<String,Object>> queryCustomerByDrug(Map<String, Object> parameterMap);

    //根据标签查询会员列表
    List<Map<String,Object>> getCustomerByLabel(Map<String, Object> parameterMap);

    String getMobileByTradesId(@Param("siteId") Integer siteId, @Param("tradesId") String tradesId);

    //根据OpenId查询会员信息
    Map<String,Object> getMemberInfoByOpenId(Map<String, Object> parameterMap);

    Map<String,Object> queryMemInfo(@Param("openId") String openId, @Param("siteId") Integer siteId);

    //保存用户注册数据
    int inserUserInfoLog(Map<String, Object> parameterMap);

    //保存检查结果
    int saveCheckLog(Map<String, Object> parameterMap);

    Map<String,Object> queryBySiteIdAndPhone(@Param("siteId") Integer siteId, @Param("userPhone") String userPhone);

    //查询设备信息
    Map<String,Object> queryEquipment(Map<String, Object> equipmentNumber);

    Map<String,Object> queryUserInfo(Map<String, Object> parameterMap);
}


