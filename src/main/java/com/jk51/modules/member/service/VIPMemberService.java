package com.jk51.modules.member.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.SVipMember;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.goods.PageData;
import com.jk51.model.order.SBMember;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.model.orders.SYbMember;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.offline.service.ErpToolsService;
import com.jk51.modules.member.mapper.VipMemberMapper;
import com.jk51.modules.member.request.MemberDto;
import com.jk51.modules.offline.service.OfflineMemberService;
import com.jk51.modules.store.service.BMemberService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-25
 * 修改记录:
 */
@Service
public class VIPMemberService {
    @Autowired
    private ErpToolsService erpToolsService;
    @Autowired
    private VipMemberMapper vipMemberMapper;
    @Autowired
    private BMemberService storeMemberService;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    OfflineMemberService offlineMemberService;


    public List<SVipMember> getMemberList2(Map<String, Object> map) {
        // return vipMemberMapper.getMemberList2(map);
        //在查看页面事调用erp进行数据的同步操作
      /*  List<SVipMember> vipMemberList = vipMemberMapper.getMemberList3(map);
        for (SVipMember vipmember : vipMemberList) {
            offlineMemberService.getuser(vipmember.getSiteId(), vipmember.getMobile(), vipmember.getInviteCode());
        }*/
        return vipMemberMapper.getMemberList3(map);
    }


    public long getMemberList2Count(Map<String, Object> map) {
        return vipMemberMapper.getMemberList3Count(map);
    }


    public List<SVipMember> getMemberBlackList(Map<String, Object> map) {
        return vipMemberMapper.getMemberBlackList2(map);
    }

    //获取会员信息
    public PageInfo<?> queryMemberList(MemberDto memberDto) {
        PageHelper.startPage(memberDto.getPageNum(), memberDto.getPageSize());

        List<PageData> list = vipMemberMapper.queryAllMembers(memberDto);
        return new PageInfo<>(list);
    }


    public List<Map<String, Object>> getsStoreName(Integer siteId) {
        return vipMemberMapper.getStoreName(siteId);
    }


    public Integer getStoreIdByName(Integer siteId, String storeName) {
        return vipMemberMapper.getStoreIdByName(storeName, siteId);
    }


    public Map<String, Object> getStoreIdByClerk(Integer siteId, String inviteCode) {
        return vipMemberMapper.getStoreIdByClerk(siteId, inviteCode);
    }


    public ReturnDto getMemberById(Integer siteId, Integer memberId) {
        Map<String, Object> vipMember = vipMemberMapper.getMemberById(siteId, memberId);
        if (StringUtil.isEmpty(vipMember)) {
            return ReturnDto.buildFailedReturnDto("该会员信息不存在。");
        }
        if (StringUtil.isEmpty(vipMember.get("address"))) {
            return ReturnDto.buildSuccessReturnDto(vipMember);
        } else {
            Map<String, Object> areaIds = erpToolsService.getareaIds(vipMember.get("address").toString());
            if (StringUtil.isEmpty(areaIds.get("area"))) {
                return ReturnDto.buildSuccessReturnDto(vipMember);
            } else {
                vipMember.put("province", areaIds.get("province"));
                vipMember.put("city", areaIds.get("city"));
                vipMember.put("area", areaIds.get("area"));
                vipMember.put("address", areaIds.get("address"));
            }
        }
        return ReturnDto.buildSuccessReturnDto(vipMember);
    }


    public Integer getStoresIdByStoreNumber(Integer siteId, String storeNumber) {
        return vipMemberMapper.getStoresIdByStoreNumber(siteId, storeNumber);
    }

    @Transactional
    public void removeBlackMember(Integer siteId, Integer memberId) {
        vipMemberMapper.removeBlackMember(siteId, memberId);
    }


    public Integer selectMemberByMobile(Integer siteId, Integer memberId) {
        return vipMemberMapper.selectMemberByMobile(siteId, memberId);
    }

    @Transactional
    public void removeBlackMemberInfo(Integer siteId, Integer memberId) {
        vipMemberMapper.removeBlackMemberInfo(siteId, memberId);
    }

    @Transactional
    public Integer addMember(SBMember member, SBMemberInfo memberInfo) {

        //判断是否ybMember含有这个手机号
        if (storeMemberService.selectCountByMobile(member.getMobile()) > 0) {

            SYbMember ybMember = storeMemberService.selectByMobile(member.getMobile());

            //判断要添加会员的商户是否存在
            if (memberInfo.getSite_id() == null) {
                return 3;
            }
            //判断商户组是否存在
            else if (StringUtil.isBlank(ybMember.getbUsersarr())) {

                //更新Usersarr
                SYbMember newYbMember = new SYbMember();
                newYbMember.setMemberId(ybMember.getMemberId());
                newYbMember.setbUsersarr(member.getSite_id() + "");

                storeMemberService.updateByPrimaryKeySelective(ybMember);

            }
            //判断是否已经含有这个商家
            else if (!ybMember.getbUsersarr().contains(member.getSite_id() + "")) {

                //更新Usersarr
                SYbMember newYbMember = new SYbMember();
                newYbMember.setMemberId(ybMember.getMemberId());
                newYbMember.setbUsersarr(ybMember.getbUsersarr() + ";" + member.getSite_id());

                storeMemberService.updateByPrimaryKeySelective(newYbMember);

            } else {
                //return 4;
            }

            member.setBuyer_id(ybMember.getMemberId());
            memberInfo.setMember_id(ybMember.getMemberId());

        } else {

            //封装ybmember，准备插入
            SYbMember ybMember = new SYbMember();

            ybMember.setRegisterStores(member.getRegister_stores());
            ybMember.setMobile(member.getMobile());
            ybMember.setBirthday(memberInfo.getBirthday());
            ybMember.setName(member.getName());
            ybMember.setSex(member.getSex());
            ybMember.setIsActivated(0);
            ybMember.setIdcardNumber(member.getIdcard_number());
            ybMember.setArea(memberInfo.getArea() != null ? memberInfo.getArea()
                : memberInfo.getCity() != null ? memberInfo.getCity()
                : memberInfo.getArea() != null ? memberInfo.getArea() : 0);
            ybMember.setReginSource(member.getMem_source());
            //ybMember.setIntegrate(member.getIntegrate());
            ybMember.setbUsersarr(member.getSite_id() + "");
            ybMember.setIntegrate(member.getIntegrate());


            //插入 ybMember
            Integer yb_int = storeMemberService.insertSelective(ybMember);

            //增加缺少信息
            member.setTotal_get_integrate(member.getIntegrate());
            if (member.getBan_status() != null) {
                if (member.getBan_status() == 0) {
                    memberInfo.setStatus(0);
                } else if (member.getBan_status() == -1) {
                    memberInfo.setStatus(10);
                } else {
                    memberInfo.setStatus(20);
                }
            }

            //获取memberId
            member.setBuyer_id(ybMember.getMemberId());
            memberInfo.setMember_id(ybMember.getMemberId());

        }

        // /插入会员信息
        member.setMem_source(140);
        member.setIs_activated(0);

        //校验info会员扩展信息表是否存在
        //店员邀请码 storeAdminext_id
        if (!StringUtil.isEmpty(memberInfo.getStoreAdminext_id())) {
            try {
                StoreAdminExt storeAdminExt = storeAdminExtMapper.selectByPrimaryKey(Integer.parseInt(memberInfo.getStoreAdminext_id()), member.getSite_id());
                if (storeAdminExt != null && !StringUtil.isEmpty(storeAdminExt.getClerk_invitation_code())) {
                    memberInfo.setInvite_code(storeAdminExt.getClerk_invitation_code());
                    member.setRegister_clerks((long) storeAdminExt.getId());
                    member.setRegister_stores(storeAdminExt.getStore_id());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        Integer i = storeMemberService.addMember(member);
        SBMemberInfo bMemberInfo = storeMemberService.getMemberInfo(member.getBuyer_id(), member.getSite_id());
        if (bMemberInfo == null) {

            //插入会员扩展信息
            return storeMemberService.addMemberInfo(memberInfo);
        } else {
            //会员扩展信息已经存在不再录入
            return i;
        }
    }

    public List<Map<String, Object>> getMemgerInfoByIds(Integer siteId, String ids) {
        List<String> idsList = Arrays.asList(ids.split(","));
        return memberMapper.queryMembersInfoByMemberIds(siteId, idsList);
    }
}
