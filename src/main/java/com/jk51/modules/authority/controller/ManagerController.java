package com.jk51.modules.authority.controller;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.role.ManagerHasRole;
import com.jk51.model.role.PermissionType;
import com.jk51.model.role.Role;
import com.jk51.modules.authority.service.ManagerService;
import com.jk51.modules.authority.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
@Controller
@RequestMapping("/manager")
public class ManagerController {

    private static final Logger log = LoggerFactory.getLogger(ManagerController.class);

    @Autowired
    private ManagerService managerService;
    @Autowired
    private RoleService roleService;

    /**
     * 包含List<ManagerHasRole>的map
     * 增加用户角色关系数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/addManagerHasRole")
    public String addRoleTOManager(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        try {
            String info = param.get("managerHasRoles").toString();
            log.info("增加用户角色信息" + info);
            List<ManagerHasRole> managerHasRoleList = JacksonUtils.json2list(info, ManagerHasRole.class);
            return managerService.addRoleToManager(managerHasRoleList);
        } catch (Exception e) {
            log.error("数据库操作失败" + e);
            return "faild";
        }
    }

    @ResponseBody
    @RequestMapping("/getRoleIds")
    public List<Integer> roles(Integer siteId, Integer adminId) {
        return managerService.selectManagerRole(siteId, adminId);
    }

    /**
     * 从管理员更改用户角色关系表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/updaterole")
    public String updateManagerRole(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        log.info("从管理员更改用户角色关系");
        try {
            return managerService.updateManagerRole(param);
        } catch (Exception e) {
            log.error("更改用户角色异常" + e);
            return "faild";
        }
    }

    /**
     * 删除用户角色关系数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/delrole")
    public String delManagerRole(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer id = null;//角色id
        Integer siteId = 0;
        Integer platform = null;
        Integer storeId = 0;
        Integer managerIds = null;
        if (!StringUtil.isEmpty(param.get("id"))) {
            id = Integer.valueOf(param.get("id").toString());
        }
        if (!StringUtil.isEmpty(param.get("siteId"))) {
            siteId = Integer.valueOf(param.get("siteId").toString());
        }
        if (!StringUtil.isEmpty(param.get("platform"))) {
            platform = Integer.valueOf(param.get("platform").toString());
        }
        if (!StringUtil.isEmpty(param.get("storeId"))) {
            storeId = Integer.valueOf(param.get("storeId").toString());
        }
        return managerService.delManagerRole(siteId, id, platform, storeId);
    }

    /**
     * 根据名称查询所有角色  name非必需 name为空时查询该管理员下所有角色
     *
     * @return
     */
    //@RequestParam("site_id") Integer siteId,Integer managerId ,Integer platform,@RequestParam(required = false,value = "name")String name
    @ResponseBody
    @RequestMapping("/selectrole")
    public Map<String, Object> selectRole(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        return managerService.selectManagerRole(param);
    }

    /**
     * 根据管理员id查找具有的角色
     *
     * @param //@RequestBody Map<String, Object> map
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getRole")
    public Map<String, List<Role>> findRoleByManager(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Integer siteId = 0;
        Integer managerId = null;
        Integer platform = null;
        Integer storeId = 0;
        if (!StringUtil.isEmpty(map.get("siteId"))) {
            siteId = Integer.valueOf(map.get("siteId").toString());
        }
        if (!StringUtil.isEmpty(map.get("managerId"))) {
            managerId = Integer.valueOf(map.get("managerId").toString());
        }
        if (!StringUtil.isEmpty(map.get("platform"))) {
            platform = Integer.valueOf(map.get("platform").toString());
        }
        if (!StringUtil.isEmpty(map.get("storeId"))) {
            storeId = Integer.valueOf(map.get("storeId").toString());
        }
        Map<String, List<Role>> roleMap = new HashMap<String, List<Role>>();
        List<Role> roleList = roleService.getRoleBymanagerHasRole(siteId, platform, storeId, managerId);
        roleMap.put("roleList", roleList);
        return roleMap;
    }

    /**
     * 根据管理员id查找具有的角色
     *
     * @param //@RequestBody Map<String, Object> map
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getRoleMap")
    public List<Role> getRoleMap(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Integer siteId = 0;
        Integer managerId = null;
        Integer platform = null;
        Integer storeId = 0;
        if (!StringUtil.isEmpty(map.get("siteId"))) {
            siteId = Integer.valueOf(map.get("siteId").toString());
        }
        if (!StringUtil.isEmpty(map.get("managerId"))) {
            managerId = Integer.valueOf(map.get("managerId").toString());
        }
        if (!StringUtil.isEmpty(map.get("platform"))) {
            platform = Integer.valueOf(map.get("platform").toString());
        }
        if (!StringUtil.isEmpty(map.get("storeId"))) {
            storeId = Integer.valueOf(map.get("storeId").toString());
        }
        List<Role> roleList = roleService.getRoleBymanagerHasRole(siteId, platform, storeId, managerId);
        return roleList;
    }

    @ResponseBody
    @RequestMapping("/adminpermission")
    public Map<String, Object> adminPermission(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        return managerService.permissions(param);
    }

    @ResponseBody
    @RequestMapping("/userpermission")
    public Map<String, Object> userPermission(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        return managerService.selectPermission(param);
    }

    @ResponseBody
    @RequestMapping("/userpermissions")
    public List<PermissionType> userPermissions(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        return managerService.selectPermissions(param);
    }

    @ResponseBody
    @RequestMapping("/addGroup")
    public Map<String, Object> addGroup(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        return managerService.addGroup(siteId);
    }

}
