package com.jk51.modules.store.web;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.*;
import com.jk51.model.role.Role;
import com.jk51.modules.authority.service.RoleService;
import com.jk51.modules.store.service.MerchantUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: zhangkuncheng
 * 创建日期: 2017-05-27
 * 修改记录:
 */
@Controller
@RequestMapping("/manager")
public class MerchantUserController {

    @Autowired
    private MerchantUserService merchantUserService;
    @Autowired
    private RoleService roleService;
    private static final Logger log = LoggerFactory.getLogger(MerchantUserController.class);

    /**
     * 查询门店后台各订单量
     *
     * @param
     * @return
     */

    @RequestMapping(value = "/mu/insertManager")
    @ResponseBody
    public SManager insertManager(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        SManager sManager = null;
        try {
            sManager = JacksonUtils.json2pojo(JacksonUtils.mapToJson(param), SManager.class);
            log.info("---------------------------查询用户siteId---JKservice---------------------------------{}", JacksonUtils.mapToJson(param));
        } catch (Exception e) {
            e.printStackTrace();
        }
        SManager sManager1 = merchantUserService.insertManager(sManager);
        try {
            log.info("-----新增管理员信息-----" + JacksonUtils.obj2json(sManager1));
        } catch (Exception e) {
            log.info("-----新增管理员信息转换异常-----" + e);
        }
        return sManager1;
    }

    @RequestMapping(value = "/mu/updateManagerTo")
    @ResponseBody
    public Integer updateManagerTo(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        log.info("操作对象为+" + param);
        SManager sManager = new SManager();
        try {
            sManager = JacksonUtils.json2pojo(JacksonUtils.mapToJson(param), SManager.class);
            log.info("---------------------------查询修改用户siteId---JKservice------------------" + JacksonUtils.mapToJson(param));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return merchantUserService.updateManagerTo(sManager);
    }

    @RequestMapping(value = "/mu/selectAll")
    @ResponseBody
    public List<SManager> selectAll(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer isActive = null;
        if (!StringUtil.isEmpty(param.get("isActive"))) {
            isActive = Integer.parseInt(String.valueOf(param.get("isActive")));
        }
        String username = null;
        if (!StringUtil.isEmpty(param.get("username"))) {
            username = String.valueOf(param.get("username"));
        }
        String realname = null;
        if (!StringUtil.isEmpty(param.get("realname"))) {
            realname = String.valueOf(param.get("realname"));
        }
        List<SManager> ss = merchantUserService.selectAll(siteId, username, realname, isActive);
        return ss;
    }

    @RequestMapping(value = "/mu/selectAllPage")
    @ResponseBody
    public PageInfo selectAllPage(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer pageNum = Integer.parseInt(String.valueOf(param.get("pageNum")));
        Integer pageSize = Integer.parseInt(String.valueOf(param.get("pageSize")));
        Integer isActive = null;
        if ("null".equals(String.valueOf(param.get("isActive")))) {
            isActive = null;
        } else {
            isActive = Integer.parseInt(String.valueOf(param.get("isActive")));
        }
        String username = null;
        String realname = null;
        if ("null".equals(param.get("username"))) {
            username = null;
        } else {
            username = String.valueOf(param.get("username"));
        }
        if ("null".equals(param.get("realname"))) {
            realname = null;
        } else {
            realname = String.valueOf(param.get("realname"));
        }
        PageHelper.startPage(pageNum, pageSize);
        List<SManager> mList = merchantUserService.selectAll(siteId, username, realname, isActive);

        for (SManager manager : mList) {
            List<SRole> rList = new ArrayList<>();
            List<Role> roleList = roleService.getRoleBymanagerHasRole(manager.getSiteId(), null, null, manager.getId());
            manager.setRoleList(roleList);
        }
        PageInfo pageInfo = new PageInfo<>(mList);
        return pageInfo;
    }

    @RequestMapping(value = "/mu/selectBySelective")
    @ResponseBody
    public SManager selectBySelective(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));
        String username = null;
        if ("null".equals(String.valueOf(param.get("username")))) {
            username = null;
        } else {
            username = String.valueOf(param.get("username"));
        }
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        return merchantUserService.selectBySelective(id, username, siteId);
    }

    @RequestMapping(value = "/mu/selectByUsername")
    @ResponseBody
    public List<SManager> selectByUsername(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer id = null;
        String username = null;
        if ("null".equals(String.valueOf(param.get("username")))) {
            username = null;
        } else {
            username = String.valueOf(param.get("username"));
        }
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        return merchantUserService.selectByUsername(id, username, siteId);
    }

    @RequestMapping(value = "/mu/getUserNamebyPrimaryKey")
    @ResponseBody
    public String getUserNamebyPrimaryKey(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));
        Integer site_id = Integer.parseInt(String.valueOf(param.get("site_id")));
        return merchantUserService.getUserNamebyPrimaryKey(id, site_id);
    }

    @RequestMapping(value = "/mu/getMerchants")
    @ResponseBody
    public List<SMerchant> getMerchants(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        String username = String.valueOf(param.get("username"));
        String password = String.valueOf(param.get("password"));
        List<SMerchant> list = merchantUserService.getMerchants(siteId, username, password);
        return list;
    }

    @RequestMapping(value = "/mu/getUserName")
    @ResponseBody
    public List<SManager> getUserName(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        String username = String.valueOf(param.get("username"));
        String password = String.valueOf(param.get("password"));
        List<SManager> list = merchantUserService.getUserName(siteId, username, password);
        return list;
    }

    @RequestMapping(value = "/mu/selectStatus")
    @ResponseBody
    public int selectStatus(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer seteId = Integer.parseInt(String.valueOf(param.get("seteId")));
        int i = merchantUserService.selectStatus(seteId);
        return i;
    }

    @RequestMapping(value = "/mu/updateLoginCount")
    @ResponseBody
    public void updateLoginCount(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        String username = String.valueOf(param.get("username"));
        String password = String.valueOf(param.get("password"));
        merchantUserService.updateLoginCount(siteId, username, password);
    }

    @RequestMapping(value = "/mu/insertSelective")
    @ResponseBody
    public Integer insertSelective(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        SBGoodsPrebook sbGoodsPrebook = null;
        try {
            sbGoodsPrebook = JacksonUtils.json2pojo(JacksonUtils.mapToJson(param), SBGoodsPrebook.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return merchantUserService.insertSelective(sbGoodsPrebook);
    }









   /* @RequestMapping(value = "/mu/getUserName")
    @ResponseBody
    public Map<String,Object> getUserName(HttpServletRequest request) {
        Map<String,Object> result = new HashMap<String,Object>();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        String username = String.valueOf(param.get("username"));
        String password = String.valueOf(param.get("password"));
        String IP = String.valueOf(param.get("IP"));
        //HttpServletRequest req = param.get("request");
        String str = merchantUserService.getUserName(siteId,username,password,request,IP);
        //String string = str.substring(1,str.length()-1);
        result.put("str",str);
        return result;
    }*/


}
