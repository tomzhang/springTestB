package com.jk51.modules.authority.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.Group;
import com.jk51.model.role.*;
import com.jk51.modules.authority.mapper.ManagerHasRoleMapper;
import com.jk51.modules.authority.mapper.ManagerMapper;
import com.jk51.modules.authority.mapper.PermissionMapper;
import com.jk51.modules.authority.mapper.RoleMapper;
import com.jk51.modules.treat.mapper.GroupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.DocFlavor;
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
public class ManagerService {
    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);
    @Autowired
    private ManagerMapper managerMapper;

    @Autowired
    private ManagerHasRoleMapper managerHasRoleMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private GroupMapper groupMapper;

    //增加管理员
    @Transactional
    public int insert(Manager manager) {
        return managerMapper.insertSelective(manager);
    }

    @Transactional
    public int del(Integer id, Integer siteId) {
        return managerMapper.deleteByPrimaryKey(id, siteId);
    }

    @Transactional
    public int update(Manager manager) {
        return managerMapper.updateByPrimaryKeySelective(manager);
    }


    /**
     * 从角色出发增加管理员
     */

    public String insertRoleToManager(List<ManagerHasRole> managerHasRoles) {
        int i = 0;
        for (ManagerHasRole managerhasrole : managerHasRoles) {
            i += managerHasRoleMapper.insertSelective(managerhasrole);
        }
        if (i != 0) {
            return "success";
        } else {
            return "faild";
        }
    }


    /**
     * 为管理员添加角色
     *
     * @param managerHasRoles
     * @return
     */
    @Transactional
    public String addRoleToManager(List<ManagerHasRole> managerHasRoles) {
        int i = 0;
        for (ManagerHasRole managerhasrole : managerHasRoles) {
            i += managerHasRoleMapper.insertSelective(managerhasrole);
        }
        if (i != 0) {
            return "success";
        } else {
            return "faild";
        }
    }

    /**
     * 从管理员更改管理员角色关系表
     *
     * @param params
     * @return
     */
    @Transactional
    public String updateManagerRole(Map<String, Object> params) {
        Integer siteId = 0;
        Integer platform = null;
        Integer storeId = 0;
        Integer managerId = null;
        List<Integer> roleIds = new ArrayList<>();
        if (!StringUtil.isEmpty(params.get("siteId"))) {
            siteId = Integer.parseInt(params.get("siteId").toString());
        }
        if (!StringUtil.isEmpty(params.get("platform"))) {
            platform = Integer.parseInt(params.get("platform").toString());
        }
        if (!StringUtil.isEmpty(params.get("storeId"))) {
            storeId = Integer.parseInt(params.get("storeId").toString());
        }
        if (!StringUtil.isEmpty(params.get("managerId"))) {
            managerId = Integer.parseInt(params.get("managerId").toString());
        }
        if (!StringUtil.isEmpty(params.get("roleIds"))) {
            String[] s = params.get("roleIds").toString().split(",");
            for (String u : s) {
                roleIds.add(Integer.parseInt(u));
            }
        }
        Integer j = managerHasRoleMapper.deleteByManagerKey(siteId, platform, storeId, managerId);//删除原管理员对应的角色关系
        List<ManagerHasRole> managerHasRoleList = new ArrayList<>();
        for (int i = 0; i < roleIds.size(); i++) {
            ManagerHasRole managerHasRole = new ManagerHasRole();
            managerHasRole.setManager_id(managerId);
            managerHasRole.setPlatform(platform);
            managerHasRole.setSite_id(siteId);
            managerHasRole.setRole_id(roleIds.get(i));
            managerHasRole.setStore_id(storeId);
            managerHasRoleList.add(managerHasRole);
        }
        return this.insertRoleToManager(managerHasRoleList);
    }

    /**
     * 查询一个用户拥有的角色(可通过名字查找)
     *
     * @return
     */
    @Transactional
    public Map<String, Object> selectManagerRole(Map<String, Object> params) {
        Integer siteId = 0;
        Integer platform = null;
        Integer storeId = 0;
        Integer managerId = null;
        String name = null;
        if (!StringUtil.isEmpty(params.get("siteId"))) {
            siteId = Integer.parseInt(params.get("siteId").toString());
        }
        if (!StringUtil.isEmpty(params.get("platform"))) {
            platform = Integer.parseInt(params.get("platform").toString());
        }
        if (!StringUtil.isEmpty(params.get("storeId"))) {
            storeId = Integer.parseInt(params.get("storeId").toString());
        }
        if (!StringUtil.isEmpty(params.get("storeId"))) {
            managerId = Integer.parseInt(params.get("managerId").toString());
        }
        if (!StringUtil.isEmpty(params.get("name"))) {
            name = params.get("name").toString();
        }
        List<Role> roles = managerHasRoleMapper.selectByName(siteId, managerId, platform, name);
        Map<String, Object> map = new HashMap<>();
        for (Role role : roles) {
            map.put(role.getName(), role.getId());
        }
        return map;
    }

    /**
     * 删除用户角色关系表
     *
     * @param siteId
     * @param id
     * @return
     */
    @Transactional
    public String delManagerRole(Integer siteId, Integer id, Integer platform, Integer storeId) {
        int i = managerHasRoleMapper.deleteByRoleKey(siteId, id, platform, storeId);
        if (i != 0) {
            return "success";
        }
        return "faild";
    }

    /**
     * 获取所有permission的信息并封装成map
     *
     * @param param
     * @return
     */

    public Map<String, Object> permissions(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        List<Permission> permissions = permissionMapper.selectByPlatform(Integer.parseInt(param.get("platform").toString()));
        for (Permission permission : permissions) {
            result.put(permission.getAction(), permission.getName());
        }
        return result;
    }

    /**
     * 获取权限列表
     *
     * @param param
     * @return
     */
    public Map<String, Object> selectPermission(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();

        List<Permission> permissions = roleMapper.selectPermissions(Integer.parseInt(param.get("siteId").toString()),
                Integer.parseInt(param.get("managerId").toString()), Integer.parseInt(param.get("platform").toString()));
        for (Permission permission : permissions
                ) {
            result.put(permission.getAction(), permission.getName());
        }
        return result;
    }

    /**
     * 获取权限列表
     *
     * @param param
     * @return
     */
    public List<PermissionType> selectPermissions(Map<String, Object> param) {

        return roleMapper.selectPermissionTypeList(Integer.parseInt(param.get("siteId").toString()),
                Integer.parseInt(param.get("managerId").toString()), Integer.parseInt(param.get("platform").toString()));

    }

    @Transactional
    public List<Integer> selectManagerRole(Integer siteId, Integer adminId) {
        List<Integer> ids = managerHasRoleMapper.selectRoleIds(siteId, adminId);
        return ids;
    }

    @Transactional
    public Map<String, Object> addGroup(Integer siteId) {

        Map<String, Object> addgroup = new HashMap<>();
        Integer code = 0;
        String msg = "";

        List<Group> currentGroup = groupMapper.selectBySiteId(siteId);
        if (currentGroup == null || currentGroup.size() == 0) {
            List<Group> groups = groupMapper.selectBySiteId(100001);
            try {
                int k = groupMapper.insertList(groups, siteId);
                msg = "添加服务组成功";
            } catch (Exception e) {
                logger.info("插入商户时添加服务组失败");
                code = -1;
                msg = "添加服务组失败，请联系管理员";
            }
        } else {
            code = 1;
            msg = "已存在服务组信息，请勿重复添加";
        }
        addgroup.put("code", code);
        addgroup.put("msg", msg);
        return addgroup;
    }
}
