package com.jk51.modules.authority.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.role.Permission;
import com.jk51.model.role.PermissionType;
import com.jk51.model.role.SysPermissionInit;
import com.jk51.model.role.TypeModel;
import com.jk51.modules.authority.service.PermissionService;
import com.jk51.modules.store.service.BStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-03-02
 * 修改记录:
 */
@Controller
@ResponseBody
@RequestMapping("/permission")
public class PermissionController {

    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private BStoreService bStoreService;

    @RequestMapping("/addPermission")
    public String addPermission(@RequestParam(required = true) Permission permission) {
        int i = permissionService.insertPermission(permission);
        if (i != 0) {
            return "success";
        }
        return "faild";
    }

    /**
     * 获取该平台所有权限
     *
     * @return
     */
    @RequestMapping("/selectPermission")
    public String selectPermission(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Integer platform = Integer.valueOf(map.get("platform").toString());
        String str = JacksonUtils.mapToJson(permissionService.selectPermission(platform));
        String result = "";
        try {
            result = new String(str.getBytes("utf-8"), "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            logger.error("根据平台权限获取失败" + e);
        }
        return result;
    }


    @RequestMapping("/addtype")
    public String addPermissionType(@RequestBody PermissionType permissionType) {
        logger.info(permissionType.toString());
        return permissionService.insertPermissionType(permissionType);
    }

    /**
     * 删除一条模块信息
     *
     * @param typeId
     * @return
     */
    @RequestMapping("/deltype/{typeid}")
    public String delType(@PathVariable("typeid") Integer typeId) {
        return permissionService.delType(typeId);
    }

    @RequestMapping("/updatetype")
    public String updateType(@RequestBody PermissionType permissionType) {
        return permissionService.updateTypes(permissionType);
    }

    /**
     * 根据权限名称、平台信息、模块id查询
     *
     * @return
     */
    @RequestMapping("/typelist")
    public List<PermissionType> selectTypes(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String name = (String) param.get("permissionName");
        Integer platform = (Integer) param.get("platform");
        Integer id = (Integer) param.get("id");

        try {
            return permissionService.selectTypes(name, platform, id);
        } catch (Exception e) {
            logger.info("查询异常" + e);
            return null;
        }
    }

    /**
     * 根据权限名称、平台信息、模块id查询
     *
     * @return
     */
    @RequestMapping("/selectTypesPermissions")
    public List<Map> selectTypesPermissions(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String name = param.get("permissionName").toString();
        Integer platform = Integer.parseInt(param.get("platform").toString());
        Integer id = null;
        if (!StringUtil.isEmpty(param.get("id"))) {
            id = Integer.parseInt(param.get("id").toString());
        }

        try {
            return permissionService.selectTypesPermissions(name, platform, id);
        } catch (Exception e) {
            logger.info("查询异常" + e);
            return null;
        }
    }

    /**
     * 根据模块名称、平台信息、模块id查询
     *
     * @return
     */
    @RequestMapping("/selectTPermissionTypes")
    public List<PermissionType> selectTPermissionTypes(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String name = null;
        if (!StringUtil.isEmpty(param.get("permissionName"))) {
            name = param.get("permissionName").toString();
        }
        Integer platform = Integer.parseInt(param.get("platform").toString());
        Integer id = null;
        if (!StringUtil.isEmpty(param.get("id"))) {
            id = Integer.parseInt(param.get("id").toString());
        }
        try {
            return permissionService.selectPermissionTypes(name, platform, id);
        } catch (Exception e) {
            logger.info("查询异常" + e);
            return null;
        }
    }

    @RequestMapping("/addpermission")
    public String addpermission(@RequestBody Permission permission) {
        return permissionService.addPermission(permission);
    }

    @RequestMapping("/updatepermission")
    public String updatePermission(@RequestBody Permission permission) {
        return permissionService.updatePermission(permission);
    }

    @RequestMapping("/delpermission/{id}")
    public String delPermission(@PathVariable("id") Integer id) {
        return permissionService.delPermission(id);
    }

    @RequestMapping("/typeinfo/{platform}")
    public List<TypeModel> typeInfo(@PathVariable("platform") Integer platform) {
        return permissionService.selectTypeInfo(platform);
    }

    @RequestMapping("/permission/{id}")
    public Permission selectPermisssion(@PathVariable("id") Integer id) {
        return permissionService.findAPermission(id);
    }

    @RequestMapping("/type/{id}")
    public PermissionType selectType(@PathVariable("id") Integer id) {
        return permissionService.selectType(id);
    }

    @RequestMapping("/selectPermisssionAll")
    public List<Permission> selectPermisssionAll() {
        return permissionService.selectPermisssionAll();
    }

    @RequestMapping("/findSysPermissionInit")
    public List<SysPermissionInit> findSysPermissionInit() {
        return permissionService.findSysPermissionInit();
    }

    @RequestMapping("/selectCheckStoresPermission")
    @ResponseBody
    public Map<String, Object> selectCheckStoresPermission(Integer siteId, String desc, Integer status) {
        return permissionService.selectStoreCheckPermission(siteId, desc, status);
    }

    /**
     * 根据名称查询某些摸块的权限信息
     *
     * @param platform
     * @param name     多个用逗号隔开
     * @return
     */
    @RequestMapping("/selectPermissionByTypeName")
    public Map<String, Object> selectPermissionByTypeName(@RequestParam("platform") Integer platform, @RequestParam("name") String name) {
        logger.info("查询盘点权限：platform:{},name:{}.", platform, name);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("info", permissionService.selectPermissionByTypeName(platform, name));
        return resultMap;
    }

    @RequestMapping("updatepandianStoresPermissions")
    public String updatepandianStoresPermissions(Integer siteId, String storeIds, String permissionIds, Integer pandianStoreType, String desc) {
        if (pandianStoreType == 0) {
            storeIds = bStoreService.selectStoreIds(siteId, storeIds);
        }
        logger.info("更新门店的盘点权限信息siteId:{},门店:{},权限信息:{},描述:{}.", siteId, storeIds, permissionIds, desc);
        return permissionService.updatepandianStoresPermissions(siteId, storeIds, permissionIds, desc);
    }

    /**
     * 获取未被选中的门店ids
     *
     * @param siteId
     * @param storeIds
     * @return
     */
    @RequestMapping("selectUnselectStoreIds")
    public String selectUnselectStoreIds(Integer siteId, String storeIds) {
        return bStoreService.selectStoreIds(siteId, storeIds);
    }

    @RequestMapping("selectUnselectPermission")
    public Map<String, Object> selectUnselectPermission(Integer siteId, Integer storeId, Integer platform, String desc, Integer status) {
        return permissionService.selectUnselectPermission(siteId, storeId, platform, desc, status);
    }

    //获取门店的盘点权限（Map对象）
    @RequestMapping("selectStoresCheckPermissions")
    public ReturnDto selectStoresPermissions(Integer siteId, String desc, Integer status) {
        return permissionService.selectStorePermissions(siteId, desc, status);
    }

}
