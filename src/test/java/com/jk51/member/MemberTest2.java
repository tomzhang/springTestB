package com.jk51.member;

import com.jk51.Bootstrap;
import com.jk51.model.order.Member;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/7-03-07
 * 修改记录 :
 */
public class MemberTest2 {
    @Autowired
    MemberMapper memberMapper;
    @Test
    public void updateMember(){
        Member m=new Member();
        m.setSiteId(100003);
        memberMapper.updateVipMember(m);

    }


}
