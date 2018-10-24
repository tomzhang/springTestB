package com.jk51.modules.authority.mapper;

import com.jk51.model.role.ManagerHasRole;
import com.jk51.model.role.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ManagerHasRoleMapper {

    int deleteByRoleKey(@Param("site_id") Integer siteId, @Param("role_id") Integer id, @Param("platform") Integer platform, @Param("store_id") Integer storeId);

    int insertSelective(ManagerHasRole record);

    ManagerHasRole selectByPrimaryKey(Integer id, Integer siteId);

    List<ManagerHasRole> selectByRoleKey(@Param("site_id") Integer siteId, @Param("role_id") Integer roleId, @Param("platform") Integer platform, @Param("store_id") Integer store_id);

    int deleteByManagerKey(@Param("site_id") Integer siteId, @Param("platform") Integer platform, @Param("store_id") Integer store_id, @Param("manager_id") Integer managerId);

    List<Role> selectByName(@Param("site_id") Integer siteId, @Param("manager_id") Integer managerId, @Param("platform") Integer platform, @Param("name") String name);

    List<Integer> selectRoleIds(@Param("siteId") Integer siteId, @Param("adminId") Integer adminId);

    List<Map<String,Object>> selectStoreNameAndStoreIdByManagerId(@Param("siteId") Integer siteId,@Param("userId") String userId);
}