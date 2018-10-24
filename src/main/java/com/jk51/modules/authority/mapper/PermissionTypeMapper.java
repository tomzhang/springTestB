package com.jk51.modules.authority.mapper;

import com.jk51.model.role.PermissionType;
import com.jk51.model.role.TypeModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:权限类型
 * 作者: dumingliang
 * 创建日期: 2017-03-02
 * 修改记录:
 */
@Mapper
public interface PermissionTypeMapper {

    int insertSelective(PermissionType permissionType);

    List<PermissionType> selectByPlatform(@Param("platform") Integer platform);

    int updatePermissionType(PermissionType permissionType);

    int deletePermissionType(@Param("id") Integer id);

    /**
     * 查询包含权限在内的type 模块
     *
     * @param platform
     * @return
     */
    List<PermissionType> selectTypes(@Param("permission_name") String permissionName, @Param("platform") Integer platform, @Param("id") Integer id);

    List<Map> selectTypesPermissions(@Param("permission_name") String permissionName, @Param("platform") Integer platform, @Param("id") Integer id);

    List<TypeModel> selectTypeInfo(@Param("platform") Integer platform);

    PermissionType selectById(@Param("id") Integer id);

    List<Map<String,Object>> selectPermissionByModelName(@Param("permission_name") String permissionName, @Param("platform") Integer platform, @Param("id") Integer id);
}
