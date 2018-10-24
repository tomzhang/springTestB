package com.jk51.modules.member.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.Member;
import com.jk51.model.order.YBMember;
import com.jk51.model.role.VipMember;
import com.jk51.model.role.requestParams.VipMemberSelectParam;
import com.jk51.modules.member.mapper.MemberInfoMapper;
import com.jk51.modules.member.mapper.VipMemberMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
;
import java.util.List;
import java.util.Map;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     : 会员列表
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/1-03-01
 * 修改记录 :
 */
@Service
public class MemberService {
    @Autowired
    private VipMemberMapper vipMemberMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private MemberInfoMapper memberInfoMapper;
    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    public PageInfo<VipMember> queryMemberList(Integer page, Integer pageSize, VipMemberSelectParam vipMemberSelectParam) {

        PageHelper.startPage(page, pageSize);//开启分页
        List<VipMember> list = this.vipMemberMapper.getMemberList(vipMemberSelectParam);
        return new PageInfo<>(list);
    }

    public PageInfo<VipMember> queryMemberBlackList(Integer page, Integer pageSize, VipMemberSelectParam vipMemberSelectParam) {

        PageHelper.startPage(page, pageSize);//开启分页
        List<VipMember> list = this.vipMemberMapper.getMemberBlackList(vipMemberSelectParam);
        return new PageInfo<>(list);
    }












    public Member selectById(Integer siteId, Integer buyerId) {
        return memberMapper.getMember(siteId, buyerId);
    }

    public Integer insertMember(Member member) {
        return memberMapper.saveMemberInfo(member);
    }

    public Integer insertYBMember(YBMember member) {
        return memberMapper.insertYbUser(member);
    }

    public Member getMemberByOpenId(int siteId, String openId) {
        return memberMapper.getMemberByOpenId(siteId, openId);
    }

    public List<Map<String, Object>> getOpenIdByPhone(int siteId, String phone) {
        List<String> phoneList = StringUtil.toList(phone, ",");
        return memberInfoMapper.getOpenIdByPhone(siteId, phoneList);
    }
}
