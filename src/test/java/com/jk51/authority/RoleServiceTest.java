package com.jk51.authority;

import com.jk51.Bootstrap;
import com.jk51.commons.http.HttpClientManager;
import com.jk51.model.role.Permission;
import com.jk51.model.role.PermissionType;
import com.jk51.model.role.Role;
import com.jk51.model.role.TypeModel;
import com.jk51.modules.authority.mapper.PermissionTypeMapper;
import com.jk51.modules.authority.service.ManagerService;
import com.jk51.modules.authority.service.PermissionService;
import com.jk51.modules.authority.service.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-22
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class RoleServiceTest {
    @Autowired
    private RoleService roleService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionTypeMapper permissionTypeMapper;


//    @Test
//    public void addRole() {
//        Role role = new Role();
//        role.setId(1);
//        role.setName("超级管理员");
//        role.setRole_desc("可以管理所有的XXXX");
//        role.setPermissions("++++++++++所有权限+++++++++++");
//        role.setPlatform(110);
//        role.setIs_active(1);
//        role.setCreate_time(new Timestamp(System.currentTimeMillis()));
//        role.setUpdate_time(new Timestamp(System.currentTimeMillis()));
//        System.out.println("执行结果返回值：+++++" + roleService.addRole(role));
//    }
//
//    @Test
//    public void delRole() {
//        RoleKey roleid = new RoleKey();
//        roleid.setId(1);
//        roleid.setSite_id(100006);
//        System.out.println("执行结果返回值：+++++" + roleService.delRole(roleid));
//    }

    @Test
    public void updateRole() {
        Role b_role = new Role();
        b_role.setSiteId(111111);
        b_role.setId(1);
        b_role.setName("超级管理员");
        b_role.setRoleDesc("可以管理所有的XXXX");
        b_role.setPermissions("++++++++++所有权限+++++++++++");
        b_role.setPlatform(110);
        b_role.setIsActive(1);
        b_role.setUpdatetime(new Timestamp(System.currentTimeMillis()));
        System.out.println("执行结果返回值：+++++" + roleService.updateRole(b_role));
    }

//    @Test
//    public void getRoleKeyByManagerKey() {
//        ManagerKey managerKey=new ManagerKey();
//        managerKey.setId(1);
//        managerKey.setSite_id(100002);
//      RoleKey roleKey= managerService.getRoleKeyByManagerKey(managerKey);
//        System.out.println(roleKey.getId()+"++++"+roleKey.getSite_id());
//    }
//    @Test
//    public void getPermissionsByRoleKey(){
//        RoleKey roleKey=new RoleKey();
//        roleKey.setId(201);
//        roleKey.setSite_id(111111);
//       Map<String,String> map= roleService.getPermissionsByRoleKey(roleKey);
//        for(Map.Entry entry:map.entrySet()){
//            System.out.println(entry.getKey()+"="+entry.getValue());
//        }
//    }

    /*@Test
    public void testRoleSelectByName(){
        List<Role> list = roleService.selectRoleByName("店长");
        System.out.println(list.size());
        for (Role role:
             list) {
            System.out.println(role);
        }
    }*/
    @Test
    public void getRoleByManagerHasRole() {
     //   List<Role> list = roleService.getRoleBymanagerHasRole(100178, 7);
//        for (Role role : list) {
//            System.out.println(role.getName());
//        }
    }

    public void getPermissionsBysiteId() {
//        Map<String, List<Permission>> map = permissionService.selectPermission(110);
    }

    @Test
    public void getrole() {
        List<Role> roleList = roleService.getRole(null, null, 110, null, null);
        for (Role role : roleList) {
            System.out.println(role.getName());
        }
    }


    @Test
    public void testSelectTypes(){
        List<PermissionType> types = permissionTypeMapper.selectTypes(null,130,null);
        for (PermissionType type:types
             ) {
            System.out.println(type.getName());
            for (Permission permission:type.getPermissions()
                 ) {
                System.out.println("======"+permission.getAction()+"====="+permission.getName());
            }
        }
    }

    @Test
    public void testInsertType(){

        PermissionType permissionType = new PermissionType();
        permissionType.setName("测试");
        permissionType.setPermissionDesc("测试");
        permissionType.setPlatform("110");

        permissionService.insertPermissionType(permissionType);
    }

    @Test
    public void testHttpInsert() throws Exception {
        PermissionType permissionType = new PermissionType();
        permissionType.setName("测试12");
        permissionType.setPermissionDesc("测试12");
        permissionType.setPlatform("120");
//"127.0.0.1:8764/permission/addpsermissiontype"
//        String jsonStr = JacksonUtils.obj2json(permissionType);
        Map<String,Object> param = new HashMap<>();
        param.put("permissionType",permissionType);
        //String result = HttpClientManager.getResponseString(OkHttpUtil.postJson("/permission/addpsermissiontype","application/json;charset=utf-8",param),"utf-8");
      //  System.out.println(result);
    }

    @Test
    public void testTypeInfo(){
        Integer platform = 110;
        List<TypeModel> list = permissionTypeMapper.selectTypeInfo(platform);
        for (TypeModel tm:list
             ) {
            System.out.println(tm.getId()+":"+tm.getName());
        }
    }

    @Test
    public void testgetType(){
//        PermissionType permissionType = permissionService.selectType(12);
        Permission permission = permissionService.findAPermission(12);
        System.out.println(permission);
    }


}
