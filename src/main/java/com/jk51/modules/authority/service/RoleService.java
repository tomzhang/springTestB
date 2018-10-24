package com.jk51.modules.authority.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.SStoreAdminext;
import com.jk51.model.role.*;
import com.jk51.modules.authority.mapper.ManagerHasRoleMapper;
import com.jk51.modules.authority.mapper.RoleHasPermissionMapper;
import com.jk51.modules.authority.mapper.RoleMapper;
import com.jk51.modules.persistence.mapper.SStoreAdminextMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-22
 * 修改记录:角色服务
 */
@Service
public class RoleService {
    @Autowired
    private ManagerService managerService;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleHasPermissionMapper roleHasPermissionMapper;
    @Autowired
    private ManagerHasRoleMapper managerHasRoleMapper;
    @Autowired
    private SStoreAdminextMapper storeAdminextMapper;

    /**
     * 增加角色
     *
     * @param role
     * @return
     */
    @Transactional
    public int addRole(Role role) {
        return roleMapper.insertSelective(role);
    }

    /**
     * 更新角色
     *
     * @param role
     * @return
     */
    @Transactional
    public int updateRole(Role role) {
        return roleMapper.updateByPrimaryKeySelective(role);
    }
//
//    public Role find(RoleKey key) {
//        return roleMapper.selectByPrimaryKey(key);
//    }


    /**
     * 删除角色（角色用户表，角色权限表，角色表）
     *
     * @return
     */
    @Transactional
    public int delRoleByPrimaryKey(RoleKey roleKey, Integer platform, Integer storeId) {
        roleHasPermissionMapper.delByRoleKey(roleKey);
        managerHasRoleMapper.deleteByRoleKey(roleKey.getSiteId(), roleKey.getId(), platform, storeId);
        return roleMapper.deleteByPrimaryKey(roleKey);
    }

    /**
     * 角色中的权限更改
     */
    @Transactional
    public String updateUserHasPermissionsByPrimaryKey(RoleKey roleKey, List<RoleHasPermission> roleHasPermissions) {
        String result = "success";
        roleHasPermissionMapper.delByRoleKey(roleKey);
        if (roleHasPermissions.size() > 0) {
            result = addRoleHasPermissions(roleHasPermissions);
        }
        return result;
    }

    /**
     * 根据管理员id获取角色列表
     *
     * @param siteId
     * @param managerId
     * @return
     */
    public List<Role> getRoleBymanagerHasRole(Integer siteId, Integer platform, Integer storeId, Integer managerId) {
        return roleMapper.getRoleBymanagerHasRole(siteId, platform, storeId, managerId);
    }

    /**
     * 获得角色列表并绑定用户角色关系,得到managerid
     *
     * @param id       角色id
     * @param siteId   商家站点id
     * @param platform
     * @param storeId
     * @param name
     * @return
     */
    public List<Role> getRole(Integer id, Integer siteId, Integer platform, Integer storeId, String name) {
        List<Role> roleList = roleMapper.getRole(id, siteId, platform, storeId, name);
        for (Role role : roleList) {
            List<ManagerHasRole> managerHasRoleList = managerHasRoleMapper.selectByRoleKey(role.getSiteId(), role.getId(), platform, storeId);
            List<ManagerKey> managerKeyList = new ArrayList<>();
            for (ManagerHasRole managerHasRole : managerHasRoleList) {
                managerKeyList.add(new ManagerKey(managerHasRole.getManager_id(), siteId));
            }
            role.setManagerKeyList(managerKeyList);
        }
        return roleList;
    }

    /**
     * 增加角色权限关系
     *
     * @param roleHasPermissionList
     * @return
     */
    @Transactional
    public String addRoleHasPermissions(List<RoleHasPermission> roleHasPermissionList) {
        int i = 0;
        for (RoleHasPermission roleHasPermission : roleHasPermissionList) {
            i += roleHasPermissionMapper.insertSelective(roleHasPermission);
        }
        if (i == roleHasPermissionList.size()) {
            return "success";
        } else {
            return "faild";
        }
    }

    /**
     * 从角色角度更新角色用户表
     *
     * @param params
     * @return
     */
    @Transactional
    public String updatemanagerHasRolefromRole(Map<String, Object> params) {
        Integer roleId = null;//角色id
        Integer siteId = 0;
        Integer platform = null;
        Integer storeId = 0;
        List<Integer> managerIds = new ArrayList<>();
        if (!StringUtil.isEmpty(params.get("roleId"))) {
            roleId = Integer.parseInt(params.get("roleId").toString());
        }
        if (!StringUtil.isEmpty(params.get("siteId"))) {
            siteId = Integer.parseInt(params.get("siteId").toString());
        }
        if (!StringUtil.isEmpty(params.get("platform"))) {
            platform = Integer.parseInt(params.get("platform").toString());
        }
        if (!StringUtil.isEmpty(params.get("storeId"))) {
            storeId = Integer.parseInt(params.get("storeId").toString());
        }
        if (!StringUtil.isEmpty(params.get("managerIds"))) {
            String[] strings = params.get("managerIds").toString().split(",");
            for (String s : strings) {
                managerIds.add(Integer.parseInt(s.toString()));
            }
        }
        managerHasRoleMapper.deleteByRoleKey(siteId, roleId, platform, storeId);//删除原管理员对应的角色关系
        List<ManagerHasRole> managerHasRoleList = new ArrayList<>();
        if (managerIds.size() > 0) {
            for (int i = 0; i < managerIds.size(); i++) {
                ManagerHasRole managerHasRole = new ManagerHasRole();
                managerHasRole.setManager_id(managerIds.get(i));
                managerHasRole.setPlatform(platform);
                managerHasRole.setStore_id(storeId);
                managerHasRole.setRole_id(roleId);
                managerHasRole.setSite_id(siteId);
                managerHasRoleList.add(managerHasRole);
            }
            return managerService.addRoleToManager(managerHasRoleList);
        }
        return "success";
    }

    public List<SStoreAdminext> selectBySiteIdAndStoreAdminId(Integer siteId, Integer id) {
        return storeAdminextMapper.selectBySiteIdAndStoreAdminId(siteId, id);
    }

    public List<Role> selectRoleNum(Integer id, Integer siteId, Integer platform, Integer storeId, String name) {
        return roleMapper.getRole(id, siteId, platform, storeId, name);
    }

    public int addRoleDefault(Role role){
        return roleMapper.addDefaultRole(role);
    }

}
