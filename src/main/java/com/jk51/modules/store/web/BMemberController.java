package com.jk51.modules.store.web;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.SVipMember;
import com.jk51.model.order.MemberListParam;
import com.jk51.model.order.SBMember;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.model.orders.SYbMember;
import com.jk51.modules.member.service.VIPMemberService;
import com.jk51.modules.store.service.BMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: zhangkuncheng
 * 创建日期: 2017-05-17
 * 修改记录:
 */
@Controller
@RequestMapping("/store")
public class BMemberController {

    @Autowired
    private BMemberService bMemberService;
    @Autowired
    private VIPMemberService vipMemberService;

    private Logger logger = LoggerFactory.getLogger(BMemberController.class);
    /**
     * 获取会员列表
     * @param
     * @return
     */
    @RequestMapping(value = "/bm/getMemberMapListBP")
    @ResponseBody
    public Map<String,Object> getMemberMapList(@RequestBody MemberListParam memberParam){
        Map<String,Object> result = new HashMap<String,Object>();
       // Map<String,Object> param = ParameterUtil.getParameterMap(request);

        try {
            /*String str = JacksonUtils.mapToJson(param);
            MemberListParam memberParam = JacksonUtils.json2pojo(str,MemberListParam.class);*/
            logger.error("获取会员列表请求参数:{}",memberParam);
            PageInfo<Map<String, Object>> pageInfo = bMemberService.getMemberMapList(memberParam);
            result.put("mList",pageInfo.getList());
            result.put("total_items",pageInfo.getTotal());
            result.put("pageCount",pageInfo.getPages());
            result.put("current", memberParam.getPageIndex());
            result.put("before", pageInfo.getPrePage());
            result.put("next", pageInfo.getNextPage());
            result.put("pageSize", memberParam.getPageSize());
            result.put("status","OK");
        } catch (Exception e) {
            logger.error("获取会员列表失败，报错信息:{}",e);
        }

        return result;
    }

    @RequestMapping(value = "/bm/getMemberMapList")
    @ResponseBody
    public Map<String,Object> getMemberMapList2(@RequestBody MemberListParam memberParam){
        Map<String,Object> result = new HashMap<String,Object>();
        try {
            logger.error("获取会员列表请求参数:{}",memberParam);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            //分页数据处理
            if(StringUtil.isEmpty(memberParam.getPageSize())){
                memberParam.setPageSize(0);
            }
            if(StringUtil.isEmpty(memberParam.getPageIndex())){
                memberParam.setPageIndex(0);
            }
            Integer page_size = memberParam.getPageSize() > 0 ? memberParam.getPageSize() : 15;
            Integer pageIndex = memberParam.getPageIndex() > 0 ? memberParam.getPageIndex() : 1;

            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("site_id", memberParam.getSiteId());
            parameterMap.put("store_id", memberParam.getStoreId());
            parameterMap.put("mobile", !StringUtil.isEmpty(memberParam.getMobile())?memberParam.getMobile():null);
            parameterMap.put("invite_code", !StringUtil.isEmpty(memberParam.getInviteCode())?memberParam.getInviteCode():null);
            parameterMap.put("name", !StringUtil.isEmpty(memberParam.getName())?memberParam.getName():null);
            parameterMap.put("text", memberParam.getText());

            //查询条件日期处理
            Date date_start = null;
            Date date_end = null;
            if (StringUtil.isNotBlank(memberParam.getDateStart())) {
                date_start = dateFormat.parse(memberParam.getDateStart().trim());
                parameterMap.put("start_time", date_start);
            } else {
                parameterMap.put("start_time", null);
            }

            if (StringUtil.isNotBlank(memberParam.getDateEnd())) {
                date_end = dateFormat.parse(memberParam.getDateEnd().trim());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date_end);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                parameterMap.put("end_time", calendar.getTime());
            } else {
                parameterMap.put("end_time", null);
            }

            long total = vipMemberService.getMemberList2Count(parameterMap);

            com.github.pagehelper.Page pageBean = new com.github.pagehelper.Page(pageIndex, page_size);//Page(int pageNum, int pageSize, boolean count, Boolean reasonable)
            pageBean.setReasonable(true);
            pageBean.setTotal(total);
            parameterMap.put("start", pageBean.getStartRow());
            parameterMap.put("pageSize", pageBean.getPageSize());

            List<SVipMember> vipMemberList = vipMemberService.getMemberList2(parameterMap);
            PageInfo pageInfo = new PageInfo<>(pageBean);
            pageInfo.setList(vipMemberList);

            result.put("mList",pageInfo.getList());
            result.put("total_items",pageInfo.getTotal());
            result.put("pageCount",pageInfo.getPages());
            result.put("current", memberParam.getPageIndex());
            result.put("before", pageInfo.getPrePage());
            result.put("next", pageInfo.getNextPage());
            result.put("pageSize", memberParam.getPageSize());
            result.put("status","OK");
        } catch (Exception e) {
            logger.error("获取会员列表失败，报错信息:{}",e);
        }
        return result;
    }

    @RequestMapping(value = "/bm/getMemberMapListCount")
    @ResponseBody
    public Map<String,Object> getMemberMapListCount(@RequestBody MemberListParam memberParam){
        Map<String,Object> result = new HashMap<String,Object>();
        try {
            logger.error("获取会员列表请求参数:{}",memberParam);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("site_id", memberParam.getSiteId());
            parameterMap.put("store_id", memberParam.getStoreId());

            parameterMap.put("mobile", !StringUtil.isEmpty(memberParam.getMobile())?memberParam.getMobile():null);
            parameterMap.put("name", !StringUtil.isEmpty(memberParam.getName())?memberParam.getName():null);
            parameterMap.put("text", memberParam.getText());

            //查询条件日期处理
            Date date_start = null;
            Date date_end = null;
            if (StringUtil.isNotBlank(memberParam.getDateStart())) {
                date_start = dateFormat.parse(memberParam.getDateStart().trim());
                parameterMap.put("start_time", date_start);
            } else {
                parameterMap.put("start_time", null);
            }
            if (StringUtil.isNotBlank(memberParam.getDateEnd())) {
                date_end = dateFormat.parse(memberParam.getDateEnd().trim());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date_end);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                parameterMap.put("end_time", calendar.getTime());
            } else {
                parameterMap.put("end_time", null);
            }
            long storeMemberCount = vipMemberService.getMemberList2Count(parameterMap);//门店会员数

            parameterMap.put("invite_code", !StringUtil.isEmpty(memberParam.getInviteCode())?memberParam.getInviteCode():null);
            parameterMap.remove("store_id");
            long selfMemberCount = vipMemberService.getMemberList2Count(parameterMap);//我的会员数

            result.put("storeMemberCount", storeMemberCount);
            result.put("selfMemberCount", selfMemberCount);
            result.put("status","OK");
        } catch (Exception e) {
            logger.error("获取会员列表失败，报错信息:{}",e);
        }
        return result;
    }


    //获取会员数量
    @RequestMapping(value = "/bm/getMemberCount")
    @ResponseBody
    public Integer getMemberCount(HttpServletRequest request) throws Exception {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String str = JacksonUtils.mapToJson(param);
        MemberListParam memberParam = JacksonUtils.json2pojo(str,MemberListParam.class);

        return bMemberService.getMemberCount(memberParam);
    }

    //添加会员送积分---根据手机号查询会员
    @RequestMapping(value = "/bm/selectByPhoneNum")
    @ResponseBody
    public SBMember selectByPhoneNum(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
       /* SBMember bMember = null;
        try {
            String str = JacksonUtils.mapToJson(param);
            bMember = JacksonUtils.json2pojo(str,SBMember.class);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        String mobile = param.get("mobile")+"";
        Integer siteId = Integer.parseInt(param.get("siteId")+"");
        return bMemberService.selectByPhoneNum(mobile,siteId);
    }
    //------------------------------------------------------------
    //添加会员
    @RequestMapping(value = "/bm/selectCountByMobile")
    @ResponseBody
    public int selectCountByMobile(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String mobile = String.valueOf(param.get("mobile"));
        return bMemberService.selectCountByMobile(mobile);
    }

    @RequestMapping(value = "/bm/selectByMobile")
    @ResponseBody
    public SYbMember selectByMobile(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String mobile = String.valueOf(param.get("mobile"));
        return bMemberService.selectByMobile(mobile);
    }

    @RequestMapping(value = "/bm/updateByPrimaryKeySelective")
    @ResponseBody
    public void updateByPrimaryKeySelective(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        SYbMember sYbMember = null;
        try {
            String str = JacksonUtils.mapToJson(param);
            sYbMember = JacksonUtils.json2pojo(str,SYbMember.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bMemberService.updateByPrimaryKeySelective(sYbMember);
    }

    @RequestMapping(value = "/bm/insertSelective")
    @ResponseBody
    public int insertSelective(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        SYbMember sYbMember = null;
        try {
            String str = JacksonUtils.mapToJson(param);
            sYbMember =JacksonUtils.json2pojo(str,SYbMember.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bMemberService.insertSelective(sYbMember);
    }
    //添加会员信息
    @RequestMapping(value = "/bm/addMember")
    @ResponseBody
    public Integer addMember(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        try {
            SBMember member = JacksonUtils.json2pojo(param.get("member")+"", SBMember.class);
            SBMemberInfo sBMemberInfo = JacksonUtils.json2pojo(param.get("memberInfo")+"", SBMemberInfo.class);
            return bMemberService.addMember(member,sBMemberInfo);
        } catch (Exception e) {
            logger.error("会员添加失败：{}",e);
            return null;
        }
    }
    /*@RequestMapping(value = "/bm/addMember")
    @ResponseBody
    public Integer addMember(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        SBMember bMember = null;
        try {
            String str = JacksonUtils.mapToJson(param);
            bMember = JacksonUtils.json2pojo(str,SBMember.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bMemberService.addMember(bMember);
    }*/
    //添加会员扩展信息
    @RequestMapping(value = "/bm/addMemberInfo")
    @ResponseBody
    public Integer addMemberInfo(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        SBMemberInfo sBMemberInfo = null;
        try {
            String str = JacksonUtils.mapToJson(param);
            sBMemberInfo = JacksonUtils.json2pojo(str, SBMemberInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Integer i = bMemberService.addMemberInfo(sBMemberInfo);
        return i;
    }
    //添加ybmember
    @RequestMapping(value = "/bm/addYbMember")
    @ResponseBody
    public Integer addYbMember(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        SYbMember sYbMember = null;
        try {
            String str = JacksonUtils.mapToJson(param);
            sYbMember = JacksonUtils.json2pojo(str, SYbMember.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bMemberService.addYbMember(sYbMember);
    }
    //校验手机号
    //String moblie,Integer siteId,Integer storeId
    @RequestMapping(value = "/bm/checkMobile")
    @ResponseBody
    public Integer checkMobile(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String moblie = param.get("moblie").toString();
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Object storeIdObj= param.get("storeId");
        Integer storeId=null;
        if(!StringUtil.isEmpty(storeId)){
            storeId=Integer.parseInt(storeIdObj.toString());
        }
        return bMemberService.checkMobile(moblie,siteId,storeId);
    }
    //获取member
    @RequestMapping(value = "/bm/getMembers")
    @ResponseBody
    public SBMember getMembers(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer memberId = Integer.parseInt(String.valueOf(param.get("memberId")));
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        SBMember sBMember = bMemberService.getMembers(memberId,siteId,storeId);
        return sBMember;
    }
    @RequestMapping(value = "/bm/getMemberInfo")
    @ResponseBody
    public SBMemberInfo getMemberInfo(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer buyer_id = Integer.parseInt(String.valueOf(param.get("buyer_id")));
        SBMemberInfo sBMemberInfo = bMemberService.getMemberInfo(buyer_id,siteId);
        if (null == sBMemberInfo){
            return null;
        }else {
            return sBMemberInfo;
        }
    }
    @RequestMapping(value = "/bm/getMemberInfoTwo")
    @ResponseBody
    public SBMemberInfo getMemberInfoTwo(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));

        if (null == param.get("buyer_id")){
            Integer buyer_id = null;
            return bMemberService.getMemberInfoTwo(buyer_id,siteId);
        }else {
            Integer buyer_id = Integer.parseInt(String.valueOf(param.get("buyer_id")));
            return bMemberService.getMemberInfoTwo(buyer_id,siteId);
        }
    }

    //更新member信息
    @RequestMapping(value = "/bm/updateMemberByMemberIds")
    @ResponseBody
    public void updateMemberByMemberIds(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        SBMember sBMember = null;
        try {
            String str = JacksonUtils.mapToJson(param);
            sBMember = JacksonUtils.json2pojo(str,SBMember.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bMemberService.updateMemberByMemberIds2(sBMember);
    }

    //更新member信息
    @RequestMapping(value = "/bm/updateMemberInfoByMemberId")
    @ResponseBody
    public Integer updateMemberInfoByMemberId(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        SBMemberInfo sBMemberInfo = null;
        try {
            String str = JacksonUtils.mapToJson(param);
            sBMemberInfo = JacksonUtils.json2pojo(str,SBMemberInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bMemberService.updateMemberInfoByMemberId2(sBMemberInfo);
    }

    @RequestMapping(value = "/bm/selectAll")
    @ResponseBody
    public Map<String,Object> selectAll(HttpServletRequest request) {
        Map<String,Object> result = new HashMap<String,Object>();
        List<Map<String,Object>> list = null;
        try {
            list = bMemberService.selectAll();
            result.put("mList",list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



}
