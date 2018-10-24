package com.jk51.login;

import com.jk51.Bootstrap;
import com.jk51.model.BManager;
import com.jk51.model.order.Member;
import com.jk51.modules.authority.mapper.ManagerMapper;
import com.jk51.modules.login.service.LoginService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/2/28-02-28
 * 修改记录 :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class LoginTest {

    @Autowired
    private LoginService loginService;
    @Autowired
    private ManagerMapper managerMapper;

    @Test
    public void memberService() {

    /*    loginService.memberRegister(1003,"1234","18906186808","1234",110,"" +
                "12121");*/
        BManager loginManager=  managerMapper.findUserAndPasswordById(1003,"xiaomama","69c5fcebaa65b560eaf06c3fbeb481ae44b8d618");
        System.out.println("=============="+managerMapper);
        //loginService.login(1003,"123","111123");
        System.out.println("==========="+ loginService.login(1003,"xiaomama","69c5fcebaa65b560eaf06c3fbeb481ae44b8d618"));
    }
}
