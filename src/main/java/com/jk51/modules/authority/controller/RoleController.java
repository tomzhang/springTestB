package com.jk51.modules.authority.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.SStoreAdminext;
import com.jk51.model.role.Permission;
import com.jk51.model.role.Role;
import com.jk51.model.role.RoleHasPermission;
import com.jk51.model.role.RoleKey;
import com.jk51.modules.authority.service.PermissionService;
import com.jk51.modules.authority.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-22
 * 修改记录:角色控制层
 * url：127.0.0.1：8764/role
 */
@Controller
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    /**
     * 增加角色
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addRole", method = RequestMethod.POST)
    public String addRole(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Integer siteId = 0;
        Integer platform = null;
        Integer storeId = 0;
        if (!StringUtil.isEmpty(map.get("siteId"))) {
            siteId = Integer.parseInt(map.get("siteId").toString());
        }
        if (!StringUtil.isEmpty(map.get("platform"))) {
            platform = Integer.parseInt(map.get("platform").toString());
        }
        if (!StringUtil.isEmpty(map.get("storeId"))) {
            storeId = Integer.parseInt(map.get("storeId").toString());
        }
        Role role = new Role();
        role.setSiteId(siteId);
        role.setPlatform(platform);
        role.setStoreId(storeId);
        role.setName(map.get("rolename").toString());
        role.setRoleDesc(map.get("roleDesc").toString());
        List<Role> roleList = roleService.selectRoleNum(null, siteId, platform, storeId, map.get("rolename").toString());
        if (roleList.size() > 0) {
            return "300";
        }
        int i = roleService.addRole(role);
        if (i != 0)
            return "success";
        else
            return "faild";
    }

    /**
     * 更改角色
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/edit")
    public String editRole(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Integer roleId = null;
        Integer siteId = 0;
        Integer platform = null;
        Integer storeId = 0;
        String rolename = null;
        String roleDesc = null;
        if (!StringUtil.isEmpty(map.get("roleId"))) {
            roleId = Integer.parseInt(map.get("roleId").toString());
        }
        if (!StringUtil.isEmpty(map.get("siteId"))) {
            siteId = Integer.parseInt(map.get("siteId").toString());
        }
        if (!StringUtil.isEmpty(map.get("platform"))) {
            platform = Integer.parseInt(map.get("platform").toString());
        }
        if (!StringUtil.isEmpty(map.get("storeId"))) {
            storeId = Integer.parseInt(map.get("storeId").toString());
        }
        if (!StringUtil.isEmpty(map.get("rolename"))) {
            rolename = map.get("rolename").toString();
        }
        if (!StringUtil.isEmpty(map.get("roleDesc"))) {
            roleDesc = map.get("roleDesc").toString();
        }
        Role role = new Role();
        role.setSiteId(siteId);
        role.setId(roleId);
        role.setPlatform(platform);
        role.setStoreId(storeId);
        role.setName(rolename);
        role.setRoleDesc(roleDesc);
        int i = roleService.updateRole(role);
        if (i != 0)
            return "success";
        else
            return "faild";
    }

    /**
     * 删除角色
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/delrole")
    public String delRoe(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Integer id = null;//角色id
        Integer siteId = 0;
        Integer platform = null;
        Integer storeId = 0;
        if (!StringUtil.isEmpty(map.get("id"))) {
            id = Integer.valueOf(map.get("id").toString());
        }
        if (!StringUtil.isEmpty(map.get("siteId"))) {
            siteId = Integer.valueOf(map.get("siteId").toString());
        }
        if (!StringUtil.isEmpty(map.get("platform"))) {
            platform = Integer.valueOf(map.get("platform").toString());
        }
        if (!StringUtil.isEmpty(map.get("storeId"))) {
            storeId = Integer.valueOf(map.get("storeId").toString());
        }
        RoleKey roleKey = new RoleKey();
        roleKey.setId(id);
        roleKey.setSiteId(siteId);
        int i = roleService.delRoleByPrimaryKey(roleKey, platform, storeId);
        if (i != 0) {
            return "success";
        } else {
            return "faild";
        }
    }


    /**
     * 模糊查询角色信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getRole")
    public List<Role> getRole(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Integer id = null;//角色id
        Integer siteId = 0;
        Integer platform = null;
        Integer storeId = 0;
        String name = null;
        if (!StringUtil.isEmpty(map.get("id"))) {
            id = Integer.valueOf(map.get("id").toString());
        }
        if (!StringUtil.isEmpty(map.get("siteId"))) {
            siteId = Integer.valueOf(map.get("siteId").toString());
        }
        if (!StringUtil.isEmpty(map.get("platform"))) {
            platform = Integer.valueOf(map.get("platform").toString());
        }
        if (!StringUtil.isEmpty(map.get("storeId"))) {
            storeId = Integer.valueOf(map.get("storeId").toString());
        }
        if (!StringUtil.isEmpty(map.get("name"))) {
            name = map.get("name").toString();
        }
        List<Role> roleList = roleService.getRole(id, siteId, platform, storeId, name);
        return roleList;
    }


    /**
     * 获取角色权限
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPermissionByRoleId")
    public List<Permission> getPermissionByRoleId(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Integer id = null;//角色id
        Integer siteId = 0;
        if (!StringUtil.isEmpty(map.get("roleId"))) {
            id = Integer.valueOf(map.get("roleId").toString());
        }
        if (!StringUtil.isEmpty(map.get("siteId"))) {
            siteId = Integer.valueOf(map.get("siteId").toString());
        }
        RoleKey roleKey = new RoleKey();
        roleKey.setId(id);
        roleKey.setSiteId(siteId);
        List<Permission> permissionList = new ArrayList<>();
        permissionList = permissionService.getPermissionsByRole(roleKey);
        return permissionList;
    }

    /**
     * 增加角色权限
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/addRoleHasPermissions")
    public String addRoleHasPermissions(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Integer id = null;//角色id
        Integer siteId = 0;
        if (!StringUtil.isEmpty(map.get("roleId"))) {
            id = Integer.valueOf(map.get("roleId").toString());
        }
        if (!StringUtil.isEmpty(map.get("siteId"))) {
            siteId = Integer.valueOf(map.get("siteId").toString());
        }
        RoleKey roleKey = new RoleKey();
        roleKey.setId(id);
        roleKey.setSiteId(siteId);
        Object o = map.get("permissionIds");
        List<RoleHasPermission> roleHasPermissionList = new ArrayList<>();
        if (!StringUtil.isEmpty(o)) {
            String[] strings = o.toString().split(",");
            for (String i : strings) {
                RoleHasPermission roleHasPermission = new RoleHasPermission();
                roleHasPermission.setSiteId(siteId);
                roleHasPermission.setRoleId(id);
                roleHasPermission.setPermissionId(Integer.parseInt(i));
                roleHasPermissionList.add(roleHasPermission);
            }
        }
        return roleService.updateUserHasPermissionsByPrimaryKey(roleKey, roleHasPermissionList);
    }

    /**
     * 更改用户角色关系表（从角色列表）
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/updatemanagerHasRolefromRole")
    public String updatemanagerHasRolefromRole(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        return roleService.updatemanagerHasRolefromRole(map);
    }

    /**
     * 根据姓名查找角色
     *
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping("/selectBySiteIdAndStoreAdminId")
    public List<SStoreAdminext> selectBySiteIdAndStoreAdminId(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));
        return roleService.selectBySiteIdAndStoreAdminId(siteId, id);
    }

}
