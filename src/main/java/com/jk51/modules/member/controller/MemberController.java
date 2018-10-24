package com.jk51.modules.member.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.message.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.login.BLogin;
import com.jk51.model.order.Member;
import com.jk51.model.role.VipMember;
import com.jk51.model.role.requestParams.VipMemberAddSelectParams;
import com.jk51.model.role.requestParams.VipMemberSelectParam;
import com.jk51.modules.member.service.BWxLoginService;
import com.jk51.modules.member.service.MemberService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/1-03-01
 * 修改记录 :
 */
@Controller
@RequestMapping("member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private BWxLoginService bWxLoginService;

    private Map<String, Object> result = new HashedMap();
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @ResponseBody
    @RequestMapping(value = "/vip_list", method = RequestMethod.POST)
    public Map<String, Object> MemberList(HttpServletRequest request) {
        Map<String, Object> map = new HashedMap();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        VipMemberSelectParam vipMemberSelectParam = new VipMemberSelectParam();
        Integer page = 1;
        Integer pageSize = 15;
        SimpleDateFormat utilDate = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = "";
        String endTime = "";

        if (param.get("page") != null && param.get("page") != "") {
            page = Integer.parseInt(param.get("page").toString());
        }
        if (param.get("pageSize") != null && param.get("pageSize") != "") {
            pageSize = Integer.parseInt(param.get("pageSize").toString());
        }
        if (param.get("phone") != null && param.get("phone") != "") {
            vipMemberSelectParam.setMobile((String) param.get("phone"));
        } else {
            param.put("phone", "");
        }
        try {
            if (param.get("start_time") != null && param.get("start_time") != "") {
                startTime = (String) param.get("start_time");
                vipMemberSelectParam.setStart_time(utilDate.parse(startTime));
            } else {
                param.put("start_time", "");
            }
            if (param.get("end_time") != null && param.get("end_time") != "") {
                endTime = (String) param.get("end_time");
                vipMemberSelectParam.setEnd_time(utilDate.parse(endTime));
            } else {
                param.put("end_time", "");
            }
        } catch (Exception e) {
            logger.error("日期转换异常", e);
        }
        if (param.get("store_name") != null) {
            vipMemberSelectParam.setStore_name((String) param.get("store_name"));
        }
        if (param.get("site_id") != null) {
            vipMemberSelectParam.setSite_id(Integer.parseInt(param.get("site_id").toString()));
        }

        PageHelper.startPage(page, pageSize);//开启分页
        PageInfo<VipMember> pageInfo = memberService.queryMemberList(page, pageSize, vipMemberSelectParam);
        List<VipMember> items = pageInfo.getList();

        //map.put("status", true);
        //map.put("result", pageInfo.getList());
        map.put("pageSize", pageSize);
        map.put("current", page);
        map.put("before", pageInfo.getPrePage());
        map.put("next", pageInfo.getNextPage());
        map.put("total_pages", pageInfo.getPages());
        map.put("total_items", pageInfo.getTotal());
        map.put("items", items);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/vip_blacklist", method = RequestMethod.POST)
    public Map<String, Object> MemberBlackList(HttpServletRequest request) {
        Map<String, Object> map = new HashedMap();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        VipMemberSelectParam vipMemberSelectParam = new VipMemberSelectParam();
        Integer page = 1;
        Integer pageSize = 15;
        SimpleDateFormat utilDate = new SimpleDateFormat("yyyy-MM-dd");

        String startTime = "";
        String endTime = "";

        if (param.get("page") != null && param.get("page") != "") {
            page = Integer.parseInt(param.get("page").toString());
        }
        if (param.get("pageSize") != null && param.get("pageSize") != "") {
            pageSize = Integer.parseInt(param.get("pageSize").toString());
        }
        if (param.get("phone") != null && param.get("phone") != "") {
            vipMemberSelectParam.setMobile((String) param.get("phone"));
        } else {
            param.put("phone", "");
        }
        try {
            if (param.get("start_time") != null && param.get("start_time") != "") {
                startTime = (String) param.get("start_time");
                vipMemberSelectParam.setStart_time(utilDate.parse(startTime));
            } else {
                param.put("start_time", "");
            }
            if (param.get("end_time") != null && param.get("end_time") != "") {
                endTime = (String) param.get("end_time");
                vipMemberSelectParam.setEnd_time(utilDate.parse(endTime));
            } else {
                param.put("end_time", "");
            }
        } catch (Exception e) {
            logger.error("日期转换异常", e);
        }
        if (param.get("store_name") != null) {
            vipMemberSelectParam.setStore_name((String) param.get("store_name"));
        }
        if (param.get("site_id") != null) {
            vipMemberSelectParam.setSite_id(Integer.parseInt(param.get("site_id").toString()));
        }

        PageHelper.startPage(page, pageSize);//开启分页
        PageInfo<VipMember> pageInfo = memberService.queryMemberBlackList(page, pageSize, vipMemberSelectParam);
        List<VipMember> items = pageInfo.getList();

        //map.put("status", true);
        //map.put("result", pageInfo.getList());
        map.put("pageSize", pageSize);
        map.put("current", page);
        map.put("before", pageInfo.getPrePage());
        map.put("next", pageInfo.getNextPage());
        map.put("total_pages", pageInfo.getPages());
        map.put("total_items", pageInfo.getTotal());
        map.put("items", items);
        return map;
    }



    @ResponseBody
    @RequestMapping(value = "/insertLoginLog", method = RequestMethod.POST)
    public Integer insertLoginLog(@RequestBody Map<String, Object> param) {
        BLogin bLogin = JacksonUtils.map2pojo(param, BLogin.class);
        return bWxLoginService.loginLog(bLogin);
    }

    @ResponseBody
    @RequestMapping(value = "/getMemberByOpenId", method = RequestMethod.POST)
    public Member getMemberByOpenId(int siteId, String openId) {
        return memberService.getMemberByOpenId(siteId, openId);
    }

    @RequestMapping(value = "getOpenIdByPhone", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, Object>> getOpenIdByPhone(Integer siteId, String phone) {
        return memberService.getOpenIdByPhone(siteId, phone);
    }
}
