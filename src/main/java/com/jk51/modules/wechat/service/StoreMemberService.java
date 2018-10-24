package com.jk51.modules.wechat.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.BMemberInfo;
import com.jk51.model.order.SBMember;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.model.orders.SYbMember;
import com.jk51.modules.persistence.mapper.SBMemberInfoMapper;
import com.jk51.modules.persistence.mapper.SBMemberMapper;
import com.jk51.modules.persistence.mapper.SYbMemberMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.treat.mapper.YbManagerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-31
 * 修改记录:
 */
@Service
public class StoreMemberService {

    @Autowired
    private SYbMemberMapper sYbMemberMapper;
    @Autowired
    private SBMemberMapper sbMemberMapper;
    @Autowired
    private SBMemberInfoMapper sbMemberInfoMapper;

    /**
     * 添加会员
     *
     * @param member
     * @param memberInfo
     * @return
     */
    @Transactional
    public Integer addMember(SBMember member, SBMemberInfo memberInfo) throws Exception {

        //判断是否ybMember含有这个手机号
        if (sYbMemberMapper.selectCountByMobile(member.getMobile()) > 0) {
            //if(ybMemberMapper.selectCountByMobile(member.getMobile())>0){

            SYbMember ybMember = sYbMemberMapper.selectByMobile(member.getMobile());
            //YbMember ybMember = ybMemberMapper.selectByMobile(member.getMobile());

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

                sYbMemberMapper.updateByPrimaryKeySelective(newYbMember);
                //ybMemberMapper.updateByPrimaryKeySelective(newYbMember);

            }
            //判断是否已经含有这个商家
            else if (!ybMember.getbUsersarr().contains(member.getSite_id() + "")) {

                //更新Usersarr
                SYbMember newYbMember = new SYbMember();
                newYbMember.setMemberId(ybMember.getMemberId());
                newYbMember.setbUsersarr(ybMember.getbUsersarr() + ";" + member.getSite_id());

                sYbMemberMapper.updateByPrimaryKeySelective(newYbMember);
                //ybMemberMapper.updateByPrimaryKeySelective(newYbMember);

            } else {
//                return 4;
            }

            member.setBuyer_id(ybMember.getMemberId());
            memberInfo.setMember_id(ybMember.getMemberId());

        } else {

            //封装ybmember，准备插入
            SYbMember ybMember = new SYbMember();

            ybMember.setMobile(member.getMobile());
            ybMember.setBirthday(memberInfo.getBirthday());
            ybMember.setName(member.getName());
            ybMember.setSex(member.getSex());
            ybMember.setIdcardNumber(member.getIdcard_number());
            ybMember.setArea(memberInfo.getArea() != null ? memberInfo.getArea()
                    : memberInfo.getCity() != null ? memberInfo.getCity()
                    : memberInfo.getArea() != null ? memberInfo.getArea() : 0);
            ybMember.setReginSource(member.getMem_source());
            ybMember.setRegisterStores(member.getRegister_stores());
            ybMember.setbUsersarr(member.getSite_id() + "");

            //插入 ybMember
            Integer ybInt = sYbMemberMapper.insertSelective(ybMember);
            //Integer ybInt = ybMemberMapper.insertSelective(ybMember);
            SYbMember ybMemberSel = sYbMemberMapper.selectByMobile(member.getMobile());
            //获取memberId
            member.setBuyer_id(ybMemberSel.getMemberId());
            memberInfo.setMember_id(ybMemberSel.getMemberId());

        }
        // /插入会员信息
        int i = sbMemberMapper.insertSelective(member);

        //校验info会员扩展信息表是否存在
        SBMemberInfo bMemberInfo = sbMemberInfoMapper.getMemberInfo(member.getBuyer_id(), member.getSite_id());
        //BMemberInfo bMemberInfo = bMemberInfoMapper.getMemberInfo(member.getBuyer_id(),member.getSite_id());

        if (bMemberInfo == null) {
            //插入会员扩展信息
            return sbMemberInfoMapper.insertSelective(memberInfo);
        } else {
            //会员扩展信息已经存在不再录入
            return i;
        }

    }
}
