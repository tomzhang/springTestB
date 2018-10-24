package com.jk51.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.Bootstrap;
import com.jk51.model.order.Member;
import com.jk51.model.role.VipMember;
import com.jk51.model.role.requestParams.VipMemberAddSelectParams;
import com.jk51.model.role.requestParams.VipMemberSelectParam;
import com.jk51.modules.member.controller.MemberController;
import com.jk51.modules.member.mapper.VipMemberMapper;
import com.jk51.modules.member.service.MemberService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/2-03-02
 * 修改记录 :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
@Transactional
@Rollback
public class MemberTest {
    @Autowired
    private VipMemberMapper memberMapper;
    @Autowired
    private MemberService memberService;

    @Test
    public void getListtest(){
        PageHelper.startPage(1,20);
        VipMemberSelectParam vipMemberSelectParam =new VipMemberSelectParam();
        vipMemberSelectParam.setSite_id(100002);
        System.out.println("==========11111111111========");
        /*page:
        pageSize:

        vipMemberSelectParam:*/
        int page=2;
        int pageSize=2;
        //vipMemberSelectParam.setMobile("18906186808");

        PageInfo<VipMember> list=  memberService.queryMemberList(page,pageSize,vipMemberSelectParam);
        System.out.println(list);

        System.out.println(list.getPageSize());
        System.out.println(list.getPageNum());
        System.out.println();
        System.out.println(list.getList());


 /*       List<VipMember> list= memberMapper.getMemberList(vipMemberSelectParam);
        if(list == null){
            System.out.println("---------1-----");
        }else{
            System.out.println("123445656666");
        }
        System.out.println("*******************"+list);
        list.parallelStream().forEach(c -> {
            System.out.println("==========22222222222======="+c.getSiteId());
            System.out.println("==========22222222222======="+c.getMemberId());*/
       /*     if (c.getSiteId() != null) {
                memberMapper.getMemberList(c);
            }
        });*/
    }


    @Autowired
    private MemberController memberController;
    @Test
    public void vipMemeberControllerTest(){
        VipMemberAddSelectParams vipMemberAddSelectParams=new VipMemberAddSelectParams();
        vipMemberAddSelectParams.setSite_id(100002);
        vipMemberAddSelectParams.setMemberId(1);
        System.out.println("------"+ObjectConvertJson(vipMemberAddSelectParams));
      /*  com.jk51.commons.message.ReturnDto message= memberController.queryMemberById(vipMemberAddSelectParams);
        System.out.println(  message);*/
    }
    private String ObjectConvertJson(Object rule) {
        ObjectMapper jsonStu = new ObjectMapper();
        String str = "";
        try {
            str = jsonStu.writeValueAsString(rule);
        } catch (Exception e) {
            //log.info("转换异常，错误信息：" + e);
        }
        return str;
    }



}
