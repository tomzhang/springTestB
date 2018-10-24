package com.jk51.modules.member.mapper;

import com.jk51.model.SVipMember;
import com.jk51.model.goods.PageData;
import com.jk51.model.role.VipMember;
import com.jk51.model.role.requestParams.VipMemberAddSelectParams;
import com.jk51.model.role.requestParams.VipMemberSelectParam;
import com.jk51.modules.member.request.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/3-03-03
 * 修改记录 :
 */
@Mapper
public interface VipMemberMapper {
    List<VipMember> getMemberList(@Param(value = "vipMemberSelectParam") VipMemberSelectParam vipMemberSelectParam);

    VipMember  queryMemberById(@Param("vipMemberAddSelectParams")VipMemberAddSelectParams vipMemberAddSelectParams );

    List<VipMember> getMemberBlackList(@Param(value = "vipMemberSelectParam") VipMemberSelectParam vipMemberSelectParam);

    List<Map<String,Object>> getMemberReport (Map<String,Object> map);
    List<SVipMember> getMemberList2(Map<String,Object> map);

    List<SVipMember> getMemberBlackList2(Map<String,Object> map);

    List<Map<String,Object>> getStoreName(Integer siteId);

    Integer getStoreIdByName(@Param("storeName") String storeName,@Param("siteId") Integer siteId);

    void removeBlackMember(@Param("siteId") Integer siteId,@Param("memberId") Integer memberId);

    Map<String,Object> getMemberById(@Param("siteId") Integer siteId,@Param("memberId") Integer memberId);

    Integer selectMemberByMobile(@Param("siteId") Integer siteId,@Param("memberId") Integer memberId);

    void removeBlackMemberInfo(@Param("siteId") Integer siteId, @Param("memberId") Integer memberId);

    Integer getStoresIdByStoreNumber(@Param("siteId") Integer siteId, @Param("storesNumber") String storesNumber);

    String selectStoreByIvCode(@Param("siteId") Integer siteId,@Param("ivcode") String ivcode);

    Map<String, Object> getStoreIdByClerk(@Param("siteId") Integer siteId, @Param("inviteCode") String inviteCode);

    List<SVipMember> getMemberList3(Map<String, Object> map);
    long getMemberList3Count(Map<String, Object> map);

    long serveCustomersCount(Map<String, Object> params);
    List<SVipMember> serveCustomers(Map<String, Object> params);
    List<PageData> queryAllMembers(MemberDto dto);
}
