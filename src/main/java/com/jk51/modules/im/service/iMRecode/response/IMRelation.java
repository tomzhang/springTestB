package com.jk51.modules.im.service.iMRecode.response;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-06-23
 * 修改记录:
 */
public class IMRelation {


    private List<Clerk> clerkList;
    private List<Member> memberList;

    public List<Clerk> getClerkList() {
        return clerkList;
    }

    public void setClerkList(List<Clerk> clerkList) {
        this.clerkList = clerkList;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }
}
