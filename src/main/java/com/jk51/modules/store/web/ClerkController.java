package com.jk51.modules.store.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.Group;
import com.jk51.model.order.*;
import com.jk51.modules.merchant.service.ClerkReturnVisitService;
import com.jk51.modules.store.service.ClerkService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: zhangkuncheng
 * 创建日期: 2017-05-19
 * 修改记录:
 */
@Controller
@RequestMapping("/store")
public class ClerkController {

    public static final Logger log = LoggerFactory.getLogger(ClerkController.class);

    @Autowired
    private ClerkService ClerkService;
    @Autowired
    ClerkReturnVisitService clerkReturnVisitService;

    /**
     * 查询所有店员(分页)
     */
    @RequestMapping(value = "/clerk/cc/pageseleteSelectiv")
    @ResponseBody
    public PageInfo pageseleteSelectiv(HttpServletRequest request,Integer pageNum,Integer pageSize) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId =  Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        String mobile = String.valueOf(param.get("mobile"));
        if ("null".equals(mobile)){
            mobile = null;
        }
        Date start = null;
        Date end = null;
        try {
            start = JacksonUtils.json2pojo(param.get("start")+"",Date.class);
            end = JacksonUtils.json2pojo(param.get("end")+"",Date.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PageHelper.startPage(pageNum,pageSize);
        List<SClerkDetail> mList = ClerkService.seleteSelectiv(siteId,storeId,mobile,start,end);
        PageInfo pageInfo = new PageInfo<>(mList);
        return pageInfo;
    }
    /**
     * 查询所有店员
     */
    @RequestMapping(value = "/clerk/cc/seleteSelectiv")
    @ResponseBody
    public Map<String, Object> seleteSelectiv(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId =  Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        String mobile = String.valueOf(param.get("mobile"));
        if ("null".equals(mobile)){
            mobile = null;
        }
        Date start = null;
        Date end = null;
        try {
            start = JacksonUtils.json2pojo(param.get("start")+"",Date.class);
            end = JacksonUtils.json2pojo(param.get("end")+"",Date.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<SClerkDetail> mList = ClerkService.seleteSelectiv(siteId,storeId,mobile,start,end);
        result.put("mList",mList);
        return result;
    }
    /**
     * 查看角色的所有用户+该平台的所有用户
     */
    @RequestMapping(value = "/clerk/cc/seleteSelective")
    @ResponseBody
    public List<SStoreAdminext> seleteSelective(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId =  Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        String mobile = String.valueOf(param.get("mobile"));
        if ("null".equals(mobile)){
            mobile = null;
        }
        Date start = null;
        Date end = null;
        try {
            start = JacksonUtils.json2pojo(param.get("start")+"",Date.class);
            end = JacksonUtils.json2pojo(param.get("end")+"",Date.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<SStoreAdminext> mList = ClerkService.seleteSelective(siteId,storeId,mobile,start,end);
        return mList;
    }
    /**
     * 添加店员中
     */
    @RequestMapping(value = "/clerk/cc/getStoreName")
    @ResponseBody
    public SBStores getStoreName(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId =  Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));

        SBStores sBStores = ClerkService.getStoreName(siteId,storeId);
        return sBStores;
    }
    @RequestMapping(value = "/clerk/cc/selectGroups")
    @ResponseBody
    public Map<String, Object> selectGroups(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId =  Integer.parseInt(String.valueOf(param.get("siteId")));

        List<Group> mList = ClerkService.selectGroups(siteId);
        result.put("mList",mList);
        return result;
    }
    @RequestMapping(value = "/clerk/cc/selectByMobile")
    @ResponseBody
    public SStoreAdmin selectByMobile(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String mobile = String.valueOf(param.get("mobile"));
        SStoreAdmin obj = ClerkService.selectByMobile(mobile);
        return obj;
    }
    @RequestMapping(value = "/clerk/cc/insertSelective")
    @ResponseBody
    public int insertSelective(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String str = JacksonUtils.mapToJson(param);
        SStoreAdmin storeAdmin = null;
        try {
            storeAdmin = JacksonUtils.json2pojo(str,SStoreAdmin.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ClerkService.insertSelective(storeAdmin);
    }
    @RequestMapping(value = "/clerk/cc/insertSelectiveTwo")
    @ResponseBody
    public int insertSelectiveTwo(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String str = JacksonUtils.mapToJson(param);
        SStoreAdminext storeAdminext = null;
        try {
            storeAdminext = JacksonUtils.json2pojo(str,SStoreAdminext.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ClerkService.insertSelectiveTwo(storeAdminext);
    }
    @RequestMapping(value = "/clerk/cc/insertSelectiveThree")
    @ResponseBody
    public int insertSelectiveThree(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String str = JacksonUtils.mapToJson(param);
        SChUser chUser = null;
        try {
            chUser = JacksonUtils.json2pojo(str,SChUser.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ClerkService.insertSelectiveThree(chUser);
    }
    @RequestMapping(value = "/clerk/cc/insertSelectiveFore")
    @ResponseBody
    public int insertSelectiveFore(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String str = JacksonUtils.mapToJson(param);
        SChPharmacist chPharmacist = null;
        try {
            chPharmacist = JacksonUtils.json2pojo(str,SChPharmacist.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ClerkService.insertSelectiveFore(chPharmacist);
    }
    @RequestMapping(value = "/clerk/cc/selectInviteCodeMax")
    @ResponseBody
    public Map<String,Object> selectInviteCodeMax(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer site_id = Integer.parseInt(String.valueOf(param.get("site_id")));
        List<String> list = ClerkService.selectInviteCodeMax(site_id);
        result.put("list",list);
        return result;
    }
    @RequestMapping(value = "/clerk/cc/insertListTo")
    @ResponseBody
    public void insertListTo(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        List<SGroupMember> list = (List<SGroupMember>)param.get("list");
        ClerkService.insertListTo(list);
    }
    @RequestMapping(value = "/clerk/cc/selectByMobileCh")
    @ResponseBody
    public SChUser selectByMobileCh(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String mobile = String.valueOf(param.get("mobile"));
        return ClerkService.selectByMobileCh(mobile);
    }
    @RequestMapping(value = "/clerk/cc/selectByMobileExt")
    @ResponseBody
    public SStoreAdminext selectByMobileExt(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String mobile = String.valueOf(param.get("mobile"));
        return ClerkService.selectByMobileExt(mobile);
    }
    @RequestMapping(value = "/clerk/cc/selectByPrimeryKey")
    @ResponseBody
    public Map<String, Object> selectByPrimeryKey(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeadminId = Integer.parseInt(String.valueOf(param.get("storeadminId")));
        return ClerkService.selectByPrimeryKey(siteId,storeadminId);
    }
    //添加
    @RequestMapping(value = "/clerk/cc/insert")
    @ResponseBody
    public Map<String,Object> insert(HttpServletRequest request) {
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);

        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));

        String str = null;
        try {
            SStoreAdmin storeAdmin = JacksonUtils.json2pojo(param.get("storeAdminstrMap")+"", SStoreAdmin.class);
            SStoreAdminext storeAdminext = JacksonUtils.json2pojo( param.get("storeAdminextstrMap")+"" , SStoreAdminext.class);

            JSONArray arr1 = JSON.parseArray(param.get("roleIds")+"");
            JSONArray arr2 = JSON.parseArray(param.get("groupIds")+"");
            List<Integer> roleIds = new ArrayList<>();
            for (int i = 0;i < arr1.size();i++){
                roleIds.add((Integer) arr1.get(i));
            }
            List<Integer> groupIds = new ArrayList<>();
            if (null != arr2){
                for (int i = 0;i < arr2.size();i++){
                    roleIds.add((Integer) arr2.get(i));
                }
            }else {
                groupIds = null;
            }
            //System.out.print(roleIds+"   ----"+groupIds);
            log.info("===SERVICE-clerk日志===:{},{},{},{}",JacksonUtils.obj2json(storeAdmin).toString(),JacksonUtils.obj2json(storeAdminext).toString(),roleIds,groupIds);
            str = ClerkService.insert(storeAdmin,storeAdminext,roleIds,groupIds,siteId,storeId);
           // map = JacksonUtils.json2map(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("str",str);

        return map;
    }

    //修改
    @RequestMapping(value = "/clerk/cc/update")
    @ResponseBody
    public String update(HttpServletRequest request) {

        Map<String,Object> param = ParameterUtil.getParameterMap(request);

        String str = null;
        try {
            SStoreAdmin storeAdmin = JacksonUtils.json2pojo(param.get("storeAdminstrMap")+"", SStoreAdmin.class);
            SStoreAdminext storeAdminext = JacksonUtils.json2pojo( param.get("storeAdminextstrMap")+"" , SStoreAdminext.class);

            JSONArray arr1 = JSON.parseArray(param.get("roleIds")+"");
            JSONArray arr2 = JSON.parseArray(param.get("groupIds")+"");
            List<Integer> roleIds = new ArrayList<>();
            for (int i = 0;i < arr1.size();i++){
                roleIds.add((Integer) arr1.get(i));
            }
            List<Integer> groupIds = new ArrayList<>();
            if (null != arr2){
                for (int i = 0;i < arr2.size();i++){
                    roleIds.add((Integer) arr2.get(i));
                }
            }else {
                groupIds = null;
            }
            //System.out.print(roleIds+"   ----"+groupIds);
            str = ClerkService.update(storeAdmin,storeAdminext,roleIds,groupIds);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }
    //店员详情
    @RequestMapping(value = "/clerk/cc/selectStores")
    @ResponseBody
    public List<SBStores> selectStores(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        List<SBStores> list = null;
        String s = String.valueOf(param.get("storeId"));
        if (!"null".equals(String.valueOf(param.get("storeId")))){
            Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
            return ClerkService.selectStores(siteId,storeId);
        }else {
            return ClerkService.selectStores(siteId,null);
        }
    }
    @RequestMapping(value = "/clerk/cc/selectGroupIdsTo")
    @ResponseBody
    public Map<String,Object> selectGroupIdsTo(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeadminId = Integer.parseInt(String.valueOf(param.get("storeadminId")));
        List<Integer> list = ClerkService.selectGroupIdsTo(siteId,storeadminId);
        result.put("list",list);
        return result;
    }
    @RequestMapping(value = "/clerk/cc/selectClerkDetail")
    @ResponseBody
    public SClerkDetail selectClerkDetail(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));

        SClerkDetail sClerkDetail = ClerkService.selectClerkDetail(siteId,id);
        return sClerkDetail;
    }

    //门店调配
    @RequestMapping(value = "/clerk/cc/storeDeploy")
    @ResponseBody
    public Map<String,Object> storeDeploy(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeAmidId = Integer.parseInt(String.valueOf(param.get("storeAmidId")));
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        Integer newStoreId = Integer.parseInt(String.valueOf(param.get("newStoreId")));
        Integer operatorId = Integer.parseInt(String.valueOf(param.get("operatorId")));

        String merchantUser = param.get("merchantUser") == null ? "" : String.valueOf(param.get("merchantUser"));
        Map<String,Object> map = ClerkService.storeDeploy(siteId,storeAmidId,storeId,operatorId,newStoreId, merchantUser);
        return map;
    }

    @RequestMapping(value = "/clerk/cc/checkMateCode")
    @ResponseBody
    public String checkMateCode(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        String metaCode = String.valueOf(param.get("metaCode"));
        String str = ClerkService.checkMateCode(siteId,storeId,metaCode);
        return str;
    }
    @RequestMapping(value = "/clerk/cc/storeDeployInfo")
    @ResponseBody
    public List<StoreDeployInfo> storeDeployInfo(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeId = null;
        if (param.containsKey("storeId")&& !StringUtil.isEmpty(String.valueOf(param.get("storeId")))){
            storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        }
        String clerkName = String.valueOf(param.get("clerkName"));
        if ("null".equals(clerkName)){
            clerkName = null;
        }
        String mobile = String.valueOf(param.get("mobile"));
        if ("null".equals(mobile)){
            mobile = null;
        }
        List<StoreDeployInfo> list = ClerkService.storeDeployInfo(siteId,storeId,clerkName,mobile);
        return list;
    }
    //权限列表
    @RequestMapping(value = "/clerk/cc/getClerkInfo")
    @ResponseBody
    public List<SClerkInfo> getClerkInfo(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        String username = String.valueOf(param.get("username"));
        String realName = String.valueOf(param.get("realName"));
        Integer pageNum = Integer.parseInt(String.valueOf(param.get("pageNum")));
        Integer pageSize = Integer.parseInt(String.valueOf(param.get("pageSize")));
        String str = String.valueOf(param.get("active"));

        if (!"null".equals(String.valueOf(param.get("active")))){
            Integer active = Integer.parseInt(String.valueOf(param.get("active")));
            return ClerkService.getClerkInfo(siteId,storeId,username,realName,active,pageNum,pageSize);
        }else {
            List<SClerkInfo> list = ClerkService.getClerkInfo(siteId,storeId,username,realName,null,pageNum,pageSize);
            return list;
        }

    }
    @RequestMapping(value = "/clerk/cc/insertMeta")
    @ResponseBody
    public String insertMeta(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        String authCode = String.valueOf(param.get("authCode"));
        return ClerkService.insertMeta(siteId,storeId,authCode);
    }
    //删除店员
    @RequestMapping(value = "/clerk/cc/delClerk")
    @ResponseBody
    public String delClerk(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));

        return ClerkService.delClerk(siteId,id);
    }
    @RequestMapping(value = "/clerk/cc/delete")
    @ResponseBody
    public String delete(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));
        String str = null;
        try {
            str = ClerkService.delete(siteId,id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
    @RequestMapping(value = "/clerk/cc/seleByName")
    @ResponseBody
    public SStoreAdmin seleByName(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        String username = String.valueOf(param.get("username"));
        String password = String.valueOf(param.get("password"));
        SStoreAdmin storeAdmin = ClerkService.seleByName(siteId, username, password);
        return storeAdmin;
    }
    @RequestMapping(value = "/clerk/cc/selectAdminByUserTypeOrStoreId")
    @ResponseBody
    public SStoreAdmin selectAdminByUserTypeOrStoreId(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        String storeId1 = String.valueOf(param.get("storeId"));
        Integer storeId = null;
        if(storeId1 != null && storeId1 != "" && storeId1 != "null") {
            storeId = Integer.parseInt(storeId1);
        }
        SStoreAdmin storeAdmin = ClerkService.selectAdminByUserTypeOrStoreId(siteId,storeId);
        return storeAdmin;
    }
    @RequestMapping(value = "/clerk/cc/changePwd")
    @ResponseBody
    public String changePwd(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer site_id = Integer.parseInt(String.valueOf(param.get("site_id")));
        Integer storeadmin_id = Integer.parseInt(String.valueOf(param.get("storeadmin_id")));
        String oldPwd = String.valueOf(param.get("oldPwd"));
        String newPwd = String.valueOf(param.get("newPwd"));
        String str = ClerkService.changePwd(site_id, storeadmin_id, oldPwd,newPwd);
        return str;
    }

    //根据商户的编号查询到商户的微信域名并生成二维码图片地址
    @RequestMapping(value = "/clerk/cc/selectShopWxUrlBySiteId")
    @ResponseBody
    public String selectShopWxUrlBySiteId(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        String str = ClerkService.selectShopWxUrlBySiteId(siteId);
        return str;
    }
    @RequestMapping(value = "/clerk/cc/updateLoginCount")
    @ResponseBody
    public Integer updateLoginCount(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer site_id = Integer.parseInt(String.valueOf(param.get("site_id")));
        String username = String.valueOf(param.get("username"));
        String userPwd = String.valueOf(param.get("userPwd"));
        Integer i = ClerkService.updateLoginCount(site_id,username,userPwd);
        return i;
    }

    @RequestMapping(value = "/clerk/cc/selectStatue")
    @ResponseBody
    public Map<String,Integer> selectStatue(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));
        return ClerkService.selectStatue(siteId,id);
    }
    @RequestMapping(value = "/clerk/cc/updateClerkDel")
    @ResponseBody
    public int updateClerkDel(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        return ClerkService.updateClerkDel(siteId,storeId,id);
    }
    /*@RequestMapping(value = "/clerk/cc/getStores")
    @ResponseBody
    public List<SBStores> getStores(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        String name = String.valueOf(param.get("name"));
        return ClerkService.getStores(siteId,name);
    }*/

    /**
     * 查询店员回访列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/clerk/visit/list",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto selectClerkVisitList(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        String siteId = String.valueOf(parameterMap.get("siteId"));
        String storeId = String.valueOf(parameterMap.get("storeId"));
        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId)) {
            return ReturnDto.buildFailedReturnDto("商户ID和门店ID不能为空!");
        }
        int page = (parameterMap.get("page") == null || "".equals(parameterMap.get("page")))?1:Integer.valueOf(parameterMap.get("page").toString());
        int pageSize = (parameterMap.get("pageSize") == null || "".equals(parameterMap.get("pageSize")))?15:Integer.valueOf(parameterMap.get("pageSize").toString());
        //开启分页
        PageHelper.startPage(page,pageSize);
        List<Map<String,Object>> visitList = ClerkService.selectClerkVisitList(parameterMap);
        if(visitList.size() == 0 || Objects.isNull(visitList)) {
            return ReturnDto.buildFailedReturnDto("没有查询到回访列表记录!");
        }else {
            visitList.stream().forEach(vlist -> {
                //获取调配前后门店
                    String preStoreId = String.valueOf(vlist.get("preStoreId"));
                    String nStoreId = String.valueOf(vlist.get("bvdStoreId"));
                    if(!preStoreId.equals("0") && !preStoreId.equals("null") && !nStoreId.equals("null")) {
                        if(storeId.equals(nStoreId)) {  //转入
                            vlist.put("deployStatus","转入");
                        }else { //转出
                            vlist.put("deployStatus","转出");
                        }
                    }else {
                        vlist.put("deployStatus","");
                    }
            });
            PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(visitList);
            Map<String,Object> map = new HashedMap();
            map.put("page",mapPageInfo.getPageNum());
            map.put("pageSize",mapPageInfo.getPageSize());
            map.put("items",visitList);
            map.put("totalPages",mapPageInfo.getPages());   //总页数
            map.put("total",mapPageInfo.getTotal());     //总记录数
            return ReturnDto.buildSuccessReturnDto(map);
        }

    }

    /**
     * 批量修改回访状态
     * @param request
     * @return
     */
    @RequestMapping(value = "/clerkVisit/status",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto changeClerkVisitStatus(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        String siteId = String.valueOf(parameterMap.get("siteId"));
        String storeId = String.valueOf(parameterMap.get("storeId"));
        String ids = String.valueOf(parameterMap.get("idsList"));
        String activityIds = String.valueOf(parameterMap.get("activityIds"));
        String[] activityList = activityIds.split(",");
        String[] idsList = ids.split(",");
        parameterMap.put("idsList",idsList);
        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId)) {
            return ReturnDto.buildFailedReturnDto("商户ID和门店ID不能为空!");
        }
        int result = ClerkService.changeClerkStatus(parameterMap);
        parameterMap.put("activityList",activityList);
        clerkReturnVisitService.changeVisitStastatistics(parameterMap);
        if(result > 0) {
            return ReturnDto.buildSuccessReturnDto(result);
        }else {
            return ReturnDto.buildFailedReturnDto("修改回访状态失败!");
        }
    }

    /**
     * 获取门店店员列表
     * @return
     */
    @RequestMapping(value = "/clerk/list",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto getClerksList(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        String siteId = String.valueOf(parameterMap.get("siteId"));
        String storeId = String.valueOf(parameterMap.get("storeId"));
        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId)) {
            return ReturnDto.buildFailedReturnDto("商户和门店ID不能为空!");
        }
        List<Map<String,Object>> clerkList = ClerkService.getClerkList(parameterMap);
        Map<String,Object> map = new HashedMap();
        map.put("items",clerkList);
        map.put("total",clerkList.size());
        return ReturnDto.buildSuccessReturnDto(map);
    }

    /**
     * 门店后台回访任务批量调配
     * @param request
     * @return
     */
    @RequestMapping(value = "/changeClerk",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto changgeClerk(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        String siteId = String.valueOf(parameterMap.get("siteId"));
        String storeId = String.valueOf(parameterMap.get("storeId"));
        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId)) {
            return ReturnDto.buildFailedReturnDto("商户和门店ID不能为空!");
        }
        String[] clerkInfos = String.valueOf(parameterMap.get("clerkInfo")).split(",");
        String clerkId = clerkInfos[0];//店员ID
        String clerkName = clerkInfos[1];//店员姓名
        String storeName = clerkInfos[2];//门店名称
        String[] userIdss = String.valueOf(parameterMap.get("userIds")).split(",");
        parameterMap.put("userIds",userIdss);
        parameterMap.put("clerkId",clerkId);
        parameterMap.put("clerkName",clerkName);
        parameterMap.put("storeName",storeName);
        Boolean result = ClerkService.changeClerk(parameterMap);
        if(result) {
            return ReturnDto.buildSuccessReturnDto(result);
        }else {
            return ReturnDto.buildFailedReturnDto("调配失败!");
        }

    }

    @RequestMapping(value = "/consumerList",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto consumerList(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        int page = (parameterMap.get("page") == null || "".equals(parameterMap.get("page")))?1:Integer.valueOf(parameterMap.get("page").toString());
        int pageSize = (parameterMap.get("pageSize") == null || "".equals(parameterMap.get("pageSize")))?15:Integer.valueOf(parameterMap.get("pageSize").toString());
        //开启分页
        PageHelper.startPage(page,pageSize);
        List<Map<String,Object>> result = ClerkService.getConsumerList(parameterMap);
        PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(result);
        Map<String,Object> map = new HashedMap();
        map.put("page",mapPageInfo.getPageNum());
        map.put("pageSize",mapPageInfo.getPageSize());
        map.put("items",result);
        map.put("totalPages",mapPageInfo.getPages());   //总页数
        map.put("total",mapPageInfo.getTotal());     //总记录数
        if(result.size()!=0){
            return ReturnDto.buildSuccessReturnDto(map);
        }else{
            return ReturnDto.buildFailedReturnDto("没有消费记录!");
        }


    }
}
