package com.jk51.modules.member.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.SVipMember;
import com.jk51.model.order.SBMember;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.model.orders.SYbMember;
import com.jk51.modules.integral.service.IntegralService;
import com.jk51.modules.member.request.MemberDto;
import com.jk51.modules.member.service.VIPMemberService;
import com.jk51.modules.offline.service.OfflineMemberService;
import com.jk51.modules.store.service.BMemberService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-25
 * 修改记录:
 */
@Controller
@RequestMapping("member")
public class VIPMemberController {

    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(VIPMemberController.class);
    @Autowired
    private VIPMemberService vipMemberService;
    @Autowired
    private BMemberService storeMemberService;
    @Autowired
    private IntegralService integralService;
    @Autowired
    private OfflineMemberService offlineMemberService;

    @ResponseBody
    @PostMapping("queryMemberListBP")
    public PageInfo getMemberList2(HttpServletRequest request, Integer page, Integer pageSize) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        PageHelper.startPage(page, pageSize);
        List<SVipMember> vipMemberList = vipMemberService.getMemberList2(parameterMap);
        PageInfo pageInfo = new PageInfo<>(vipMemberList);
        return pageInfo;
    }

    @ResponseBody
    @PostMapping("queryMemberList")
    public PageInfo getMemberList3(HttpServletRequest request, Integer page, Integer pageSize) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        long total = vipMemberService.getMemberList2Count(parameterMap);
        com.github.pagehelper.Page pageBean = new com.github.pagehelper.Page(page, pageSize);//Page(int pageNum, int pageSize, boolean count, Boolean reasonable)
        pageBean.setReasonable(true);
        pageBean.setTotal(total);
        parameterMap.put("start", pageBean.getStartRow());
        parameterMap.put("pageSize", pageBean.getPageSize());
        List<SVipMember> vipMemberList = vipMemberService.getMemberList2(parameterMap);
        PageInfo pageInfo = new PageInfo<>(pageBean);
        pageInfo.setList(vipMemberList);

        return pageInfo;
    }


    /**
     * 查询会员列表
     */
    @RequestMapping(value = "getAllMembersForActivity", method = RequestMethod.POST)
    @ResponseBody
    private ReturnDto getAllMembersForActivity(@RequestBody MemberDto memberDto) {

        if (StringUtils.isBlank(memberDto.getSiteId()))
            return ReturnDto.buildFailedReturnDto("siteId为空");
        PageInfo<?> pageInfo;
        try {

            pageInfo = vipMemberService.queryMemberList(memberDto);
        } catch (Exception e) {
            logger.info("查询优惠券商品报错:");
            return ReturnDto.buildFailedReturnDto("系统错误，请稍后再试");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }


    @ResponseBody
    @PostMapping("queryMemberBlackList")
    public PageInfo queryMemberBlackList(HttpServletRequest request, Integer page, Integer pageSize) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        PageHelper.startPage(page, pageSize);
        List<SVipMember> vipMemberList = vipMemberService.getMemberBlackList(parameterMap);
        PageInfo pageInfo = new PageInfo<>(vipMemberList);
        return pageInfo;
    }

    /**
     * 门店名称
     */
    @ResponseBody
    @PostMapping("getStoreName")
    public Map<String, Object> getStoreName(Integer siteId) {
        Map<String, Object> map = new HashMap<>();
        map.put("storeNameList", vipMemberService.getsStoreName(siteId));
        return map;
    }

    /**
     * 门店名称查id
     */
    @ResponseBody
    @PostMapping("getStoreIdByName")
    public Integer getStoreIdByName(Integer siteId, String storeName) {
        return vipMemberService.getStoreIdByName(siteId, storeName);
    }

    /**
     * 按id查找会员
     */
    @ResponseBody
    @PostMapping("getMemberById")
    public ReturnDto getMemberById(Integer siteId, Integer memberId) {
        return vipMemberService.getMemberById(siteId, memberId);
    }

    /**
     * 新增会员
     */
    @ResponseBody
    @PostMapping("/addMemberIntegral")
    public Integer addMemberIntegral(HttpServletRequest request) {
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        SBMember member = null;
        SBMemberInfo memberInfo = null;
        try {
            member = JacksonUtils.json2pojo(objectMap.get("member") + "", SBMember.class);
            memberInfo = JacksonUtils.json2pojo(objectMap.get("memberInfo") + "", SBMemberInfo.class);
        } catch (Exception e) {
            return 0;
        }
        Long integral = member.getIntegrate();

        member.setIntegrate(null);
        memberInfo.setIntegrate(null);

        Integer status = vipMemberService.addMember(member, memberInfo);

        //积分操作
        try {
            if (integral == null) {
                integral = 0l;
            }
            if (status == 1 && integral >= 0l) {

                member = storeMemberService.selectByPhoneNum(member.getMobile(), member.getSite_id());

                Map<String, Object> param = new HashMap<String, Object>();

                param.put("type", true);
                param.put("value", integral);
                param.put("buyerId", member.getBuyer_id());
                param.put("siteId", member.getSite_id());


                Map<String, Object> map = integralService.storeUpdateIntegral(param);

                if (map.get("status") == null || !"success".equals(map.get("status"))) {
                    logger.error("siteId:" + member.getSite_id() + "buyerId:" + member.getBuyer_id() + "，integral:" + integral + "添加积分失败");
                }

            }
            offlineMemberService.getuser(member.getSite_id(), member.getMobile(), null);
        } catch (Exception e) {
            logger.error("siteId:" + member.getSite_id() + "buyerId:" + member.getBuyer_id() + "，integral:" + integral + "添加积分失败", e);
        }
        return status;
    }

    /**
     * 添加ybmember
     *
     * @param member
     * @return
     */
    @ResponseBody
    @PostMapping("addYbMember")
    public Integer addYbMember(SYbMember member) {
        return storeMemberService.insertSelective(member);
    }

    /**
     * 移出黑名单
     *
     * @param siteId
     * @param memberId
     */
    @ResponseBody
    @PostMapping("removeBlackMember")
    public void removeBlackMember(Integer siteId, Integer memberId) {

        vipMemberService.removeBlackMember(siteId, memberId);
        Integer buyerId = vipMemberService.selectMemberByMobile(siteId, memberId);
        Integer memberInfoId = buyerId;
        vipMemberService.removeBlackMemberInfo(siteId, memberInfoId);
    }

    /**
     * 更新member信息
     *
     * @return
     */
    @ResponseBody
    @PostMapping("updateMemberByMemberId")
    public Integer updateMemberByMemberId(HttpServletRequest request) {
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        SBMember member = null;
        SBMemberInfo memberInfo = null;
        try {
            member = JacksonUtils.json2pojo(objectMap.get("member") + "", SBMember.class);
            memberInfo = JacksonUtils.json2pojo(objectMap.get("memberInfo") + "", SBMemberInfo.class);
        } catch (Exception e) {
            return 0;
        }
        memberInfo.setMember_id(member.getBuyer_id());
        member.setBuyer_id(null);
        if (member.getBan_status() == 0) {
            memberInfo.setStatus(0);
        } else if (member.getBan_status() == -1) {
            memberInfo.setStatus(10);
        } else {
            memberInfo.setStatus(20);
        }
        storeMemberService.updateMemberByMemberIds1(member, memberInfo.getMember_id());

        return storeMemberService.updateMemberInfoByMemberId1(memberInfo);
    }

    @ResponseBody
    @PostMapping("selectAll")
    public List<Map<String, Object>> selectAll() {
        return storeMemberService.selectAll();
    }

    @ResponseBody
    @PostMapping("getStoresIdByStoreNumber")
    public Integer getStoresIdByStoreNumber(Integer siteId, String storeNumber) {
        return vipMemberService.getStoresIdByStoreNumber(siteId, storeNumber);
    }

    @ResponseBody
    @PostMapping("getStoreIdByClerk")
    public Map<String, Object> getStoreIdByClerk(Integer siteId, String inviteCode) {
        return vipMemberService.getStoreIdByClerk(siteId, inviteCode);
    }

    @GetMapping("getMemberInfoByIds")
    @ResponseBody
    public ReturnDto getMemgerInfoByIds(Integer siteId, String ids) {
        try {
            List<Map<String, Object>> membersInfo = vipMemberService.getMemgerInfoByIds(siteId, ids);
            return ReturnDto.buildSuccessReturnDto(membersInfo);
        } catch (Exception e) {
            logger.error("exception, {}", e);
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }

}
