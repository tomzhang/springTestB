package com.jk51.modules.authority.mapper;

import com.jk51.model.role.Permission;
import com.jk51.model.role.PermissionType;
import com.jk51.model.role.Role;
import com.jk51.model.role.RoleKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {

    int insertSelective(Role record);

    Role selectByPrimaryKey(RoleKey key);

    int deleteByPrimaryKey(RoleKey key);

    int updateByPrimaryKeySelective(Role record);

    List<Role> getRoleBymanagerHasRole(@Param("site_id") Integer siteId, @Param("platform") Integer platform, @Param("store_id") Integer storeId, @Param("manager_id") Integer managerId);

    List<Role> getRole(@Param("id") Integer id, @Param("site_id") Integer siteId, @Param("platform") Integer platform, @Param("store_id") Integer storeId, @Param("name") String name);

    List<Permission> selectPermissions(@Param("site_id") Integer siteId, @Param("manager_id") Integer managerId, @Param("platform") Integer platform);

    List<PermissionType> selectPermissionTypeList(@Param("siteId") Integer siteId, @Param("managerId") Integer managerId,@Param("platform") int platform);

    int addDefaultRole(Role role);
}