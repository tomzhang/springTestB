package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SGroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SGroupMemberMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SGroupMember record);

    int insertSelective(SGroupMember record);

    SGroupMember selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SGroupMember record);

    int updateByPrimaryKey(SGroupMember record);

    int insertList(@Param("list") List<SGroupMember> groupMembers);

    int updateList(@Param("site_id") Integer siteId, @Param("store_admin_id") Integer storeAdminId);

    SGroupMember selectByClerkId(@Param("store_admin_id") Integer id, @Param("site_id") Integer siteId);

    List<Integer> selectGroupIds(@Param("siteId") Integer siteId, @Param("adminId") Integer storeadminId);
}
