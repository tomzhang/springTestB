package com.jk51.modules.authority.mapper;

import com.jk51.model.role.Permission;
import com.jk51.model.role.RoleKey;
import com.jk51.model.role.StorePermission;
import com.jk51.model.role.SysPermissionInit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PermissionMapper {

    int insertSelective(Permission record);

    List<Permission> selectByPrimaryKey(@Param("platform") Integer platform, @Param("typeId") Integer typeId);

    int updateByPrimaryKeySelective(Permission record);

    List<Permission> getPermissionsByRole(RoleKey roleKey);

    List<Permission> selectByPlatform(@Param("platform") Integer platform);

    int delPermission(@Param("id") Integer id);

    Permission selectById(@Param("id") Integer id);

    List<Permission> selectPermisssionAll();

    List<SysPermissionInit> findSysPermissionInit();

    List<StorePermission> selectPermissionByTypeName(@Param("platform") Integer platform, @Param("typeNames") List<String> typeName);

    List<Integer> selectPermissionUnchecked(@Param("platform") Integer platform, @Param("desc") String desc,
                                            @Param("permissionId") List<String> permissionId, @Param("status") Integer status);

    //查询门店权限
    List<Map<String, Object>> selectStoresPermissions(@Param("platform") Integer platform, @Param("desc") String desc,
                                                      @Param("permissionId") List<String> permissionId, @Param("status") Integer status);

}
