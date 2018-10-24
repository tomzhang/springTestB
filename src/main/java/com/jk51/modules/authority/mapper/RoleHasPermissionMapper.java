package com.jk51.modules.authority.mapper;

import com.jk51.model.role.RoleHasPermission;
import com.jk51.model.role.RoleKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleHasPermissionMapper {
    int delByRoleKey(RoleKey roleKey);

    int insertSelective(RoleHasPermission record);

    RoleHasPermission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoleHasPermission record);

}