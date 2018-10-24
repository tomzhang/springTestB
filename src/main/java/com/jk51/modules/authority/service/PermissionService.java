package com.jk51.modules.authority.service;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.role.*;
import com.jk51.modules.authority.mapper.BStoresPermissionMapper;
import com.jk51.modules.authority.mapper.PermissionMapper;
import com.jk51.modules.authority.mapper.PermissionTypeMapper;
import com.jk51.modules.integral.mapper.IntegrallogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-23
 * 修改记录:
 */
@Service
public class PermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private PermissionTypeMapper permissionTypeMapper;
    @Autowired
    private BStoresPermissionMapper bStoresPermissionMapper;

    /**
     * 增加权限
     *
     * @param permission
     * @return
     */
    @Transactional
    public int insertPermission(Permission permission) {
        return permissionMapper.insertSelective(permission);
    }

    /**
     * 根据平台查找权限,权限显示
     * key:"permissionType" value:permissionType
     * key:"permissions" value:permissions
     *
     * @param platform 平台代号
     * @return
     */
    public Map<String, Object> selectPermission(Integer platform) {
        Map<String, Object> permissionMap = new HashMap<>();
        List<PermissionType> permissionTypes = permissionTypeMapper.selectByPlatform(platform);
        permissionMap.put("permissionType", permissionTypes);//保存该平台权限类型
        permissionMap.put("permissions", permissionMapper.selectByPlatform(platform));//保存该平台所有权限
        return permissionMap;
    }

    public List<Permission> getPermissionsByRole(RoleKey roleKey) {
        return permissionMapper.getPermissionsByRole(roleKey);
    }

    /**
     * @param permissionType
     * @return 200 success    500：faild
     */
    public String insertPermissionType(PermissionType permissionType) {
        if (permissionType == null)
            return "500";
        permissionType.setIsDel(0);
        int i = permissionTypeMapper.insertSelective(permissionType);

        if (i != 0)
            return "200";
        else
            return "500";
    }

    ;

    /**
     * 伪删除
     *
     * @param typeId
     * @return
     */
    public String delType(Integer typeId) {
        int i = permissionTypeMapper.deletePermissionType(typeId);
        if (i != 0) return "200";
        return "500";
    }


    /**
     * 根据平台查询模块
     *
     * @param platform
     * @return
     */
    public List<PermissionType> selectTypes(String name, Integer platform, Integer id) {
        List<PermissionType> types = permissionTypeMapper.selectTypes(name, platform, id);
        if (types != null)
            return types;
        throw new RuntimeException("为查询到数据库");
    }

    /**
     * 根据平台查询模块
     *
     * @param platform
     * @return
     */
    public List<Map> selectTypesPermissions(String name, Integer platform, Integer id) {
        List<Map> permissions = permissionTypeMapper.selectTypesPermissions(name, platform, id);
        if (permissions != null)
            return permissions;
        throw new RuntimeException("为查询到数据库");
    }

    /**
     * 根据平台查询模块
     *
     * @param platform
     * @return
     */
    public List<PermissionType> selectPermissionTypes(String name, Integer platform, Integer id) {
        List<PermissionType> permissions = permissionTypeMapper.selectTypes(name, platform, id);
        if (permissions != null)
            return permissions;
        throw new RuntimeException("为查询到数据库");
    }

    public String updateTypes(PermissionType permissionType) {
        if (permissionType == null)
            return "500";
        int i = permissionTypeMapper.updatePermissionType(permissionType);
        if (i != 0)
            return "200";
        return "500";
    }

    /**
     * permission add
     *
     * @param permission
     * @return
     */
    public String addPermission(Permission permission) {
        if (permission == null)
            return "500";

        permission.setIsDel(0);
        int i = permissionMapper.insertSelective(permission);//返回值为插入的主键id
        if (i != 0)
            return "200";
        return "500";
    }

    /**
     * 权限编辑
     *
     * @param permission
     * @return
     */
    public String updatePermission(Permission permission) {
        if (permission == null || permission.getId() == null)
            return "500";
        int i = permissionMapper.updateByPrimaryKeySelective(permission);
        if (i != 0)
            return "200";
        return "500";
    }

    public String delPermission(Integer id) {
        int i = permissionMapper.delPermission(id);
        if (i != 0) return "200";
        return "500";
    }

    public List<TypeModel> selectTypeInfo(Integer platform) {
        if (platform == null)
            return permissionTypeMapper.selectTypeInfo(110);
        if (platform == 0)
            return permissionTypeMapper.selectTypeInfo(null);
        return permissionTypeMapper.selectTypeInfo(platform);
    }

    public Permission findAPermission(Integer id) {
        return permissionMapper.selectById(id);
    }

    public PermissionType selectType(Integer id) {
        return permissionTypeMapper.selectById(id);
    }


    public List<Permission> selectPermisssionAll() {
        return permissionMapper.selectPermisssionAll();
    }

    public List<SysPermissionInit> findSysPermissionInit() {
        return permissionMapper.findSysPermissionInit();
    }

    /**
     * 查询门店的盘点权限
     *
     * @return
     */
    public Map<String, Object> selectStoreCheckPermission(Integer siteId, String desc, Integer status) {
        try {
            Map<String, Object> result = bStoresPermissionMapper.selectPermission(siteId, desc, status);
            LOGGER.info("查询商户后台门店的盘点权限站点:{},搜索条件desc:{},结果:{}.", siteId, desc, result);
            return result;
        } catch (Exception e) {
            LOGGER.info("查询商户后台门店的盘点权限站点:{},搜索条件desc:{},异常:{}.", siteId, desc, e);
        }
        return null;
    }

    /**
     * 查询商户或者门店下某一模块的未被选中的权限信息
     *
     * @param siteId
     * @param desc   模块名称，模糊查询
     * @param status
     * @return
     */
    public Map<String, Object> selectUnselectPermission(Integer siteId, Integer storeId, Integer platform, String desc, Integer status) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> result = selectStoreCheckPermission(siteId, desc, status);
        if (StringUtil.isEmpty(result)) {
            response.put("code", -1);
            return response;//该商户未设置此模块权限
        }
        if (StringUtil.toList(result.get("store_id").toString(), ",").contains(String.valueOf(storeId))) {
            List<Integer> unPermissionIds = permissionMapper.selectPermissionUnchecked(platform, desc,
                StringUtil.toList(result.get("permission_id").toString(), ","), 0);
            response.put("code", 0);
            response.put("info", unPermissionIds);
            return response;
        } else {
            response.put("code", -1);
            return response;//该商户未设置此模块权限
        }
    }


    /**
     * 查询
     */
    public List<StorePermission> selectPermissionByTypeName(Integer platform, String typeName) {
        List<String> selectedTypeName = new ArrayList<>();
        String[] typeNames = typeName.split(",");
        if (typeNames.length > 0) {
            for (String n : typeNames) {
                selectedTypeName.add(n);
            }
        }
        return permissionMapper.selectPermissionByTypeName(platform, selectedTypeName);
    }

    /**
     * update门店盘点权限，一条记录表示一个门店的一个权限
     */
    public String updatepandianStoresPermissions(Integer siteId, String storeIds, String permissionIds, String desc) {
        Integer resultType = 0;
        Map<String, Object> storePermission = bStoresPermissionMapper.selectPermission(siteId, desc, 1);
        if (StringUtil.isEmpty(storePermission)) {//若不存在该权限
            resultType = bStoresPermissionMapper.insertPermission(siteId, storeIds, permissionIds, desc, 1, 0);
        } else {//若存在该权限
            resultType = bStoresPermissionMapper.updateStorePermission(siteId, Integer.parseInt(storePermission.get("id").toString()), storeIds, permissionIds, desc, 1);
        }
        if (resultType > 0) {
            return "200";
        } else {
            return "500";
        }
    }

    /**
     * 获取门店盘点权限，调用此接口说明该门店拥有盘点权限
     *
     * @param siteId
     * @param desc
     * @param status
     * @return key:action;value:desc;
     */
    public ReturnDto selectStorePermissions(Integer siteId, String desc, Integer status) {
        Map<String, Object> storePermissions = bStoresPermissionMapper.selectPermission(siteId, desc, status);//store_id,permission_id,
        if (!StringUtil.isEmpty(storePermissions)) {//将门店权限以key:values形式显示出来
            List<Map<String, Object>> result = permissionMapper.selectStoresPermissions(130, desc,
                StringUtil.toList(storePermissions.get("permission_id").toString(), ","), 0);
            return ReturnDto.buildSuccessReturnDto(result);
        } else {
            LOGGER.info("该商户没有门店盘点权限");
            return ReturnDto.buildFailedReturnDto("该商户没有门店盘点权限");
        }
    }

}
