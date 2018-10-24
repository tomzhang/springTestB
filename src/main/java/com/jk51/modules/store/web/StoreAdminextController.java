package com.jk51.modules.store.web;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.order.SPage;
import com.jk51.model.order.SStoreAdminext;
import com.jk51.modules.appInterface.util.ResultMap;
import com.jk51.modules.store.service.StoreAdminextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: zhangkuncheng
 * 创建日期: 2017-05-26
 * 修改记录:
 */
@Controller
@RequestMapping("/store")
public class StoreAdminextController {

    public static final Logger logger = LoggerFactory.getLogger(StoreAdminextController.class);

    @Autowired
    private StoreAdminextService storeAdminextService;

    @RequestMapping(value = "/clerk/sa/selectClerkList")
    @ResponseBody
    public Map<String,Object> selectClerkList(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        try {
            SStoreAdminext storeAdminext = JacksonUtils.json2pojo( param.get("storeAdminext")+"" , SStoreAdminext.class);
            SPage page = JacksonUtils.json2pojo( param.get("page")+"" , SPage.class);
            logger.info("---------JK-SERVICE--WEBpagexxxxxxxxxxxxxxx---------:{}", JacksonUtils.obj2json(page));
            result = storeAdminextService.selectClerkList(storeAdminext,page);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /*@RequestMapping(value = "/clerk/sa/selectClerkList")
    @ResponseBody
    public List<SStoreAdminext> selectClerkList(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        List<SStoreAdminext> list = new ArrayList<>();
        try {
            SStoreAdminext storeAdminext = JacksonUtils.json2pojo( param.get("storeAdminext")+"" , SStoreAdminext.class);
            SPage page = JacksonUtils.json2pojo( param.get("page")+"" , SPage.class);
            list = storeAdminextService.selectClerkList(storeAdminext,page);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    @RequestMapping(value = "/clerk/sa/selectClerkCount")
    @ResponseBody
    public SPage selectClerkCount(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        SPage sPage = null;
        try {
            SStoreAdminext storeAdminext = JacksonUtils.json2pojo( param.get("storeAdminext")+"" , SStoreAdminext.class);
            SPage page = JacksonUtils.json2pojo( param.get("page")+"" , SPage.class);
            sPage = storeAdminextService.selectClerkCount(storeAdminext,page);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sPage;
    }*/
    @RequestMapping(value = "/clerk/sa/selectClerkByInviteCode")
    @ResponseBody
    public SStoreAdminext selectClerkByInviteCode(HttpServletRequest request) {
        Map<String,Object> result = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        String inviteCode = String.valueOf(param.get("inviteCode"));

        SStoreAdminext sStoreAdminext = storeAdminextService.selectClerkByInviteCode(siteId,inviteCode);
        return sStoreAdminext;
    }

    @RequestMapping(value = "editAvatar")
    @ResponseBody
    public Map editAvatar(Integer siteId, Integer id, String avatar) {
        storeAdminextService.eidtAvatar(siteId, id, avatar);
        return ResultMap.successResult();
    }

    @RequestMapping(value = "queryItem")
    @ResponseBody
    public Map queryItem(Integer siteId, Integer id) {
        return storeAdminextService.queryItem(siteId, id);
    }

    @RequestMapping(value = "forgetPwd")
    @ResponseBody
    public Map forgetPwd(Integer siteId, String phone, String pwd, String code) {
        return storeAdminextService.forgetPwd(siteId, phone, pwd, code);
    }
}
