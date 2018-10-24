package com.jk51.modules.shiro.controller;

import com.jk51.model.role.Manager;
import com.jk51.modules.authority.mapper.ManagerMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-23
 * 修改记录:
 */
@Controller
public class ShrioController {

    private static final Logger logger = LoggerFactory.getLogger(ShrioController.class);

    @Autowired
    private ManagerMapper managerMapper;

    @RequestMapping(value="/login",method = RequestMethod.GET)
    public String loginForm(ModelMap modelMap){
        modelMap.addAttribute("manager",new Manager());
        return "login";
    }

    @RequestMapping(value = "/login" ,method = RequestMethod.POST)
    public String login(@Valid Manager manager, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()) return "login";

        String managerName = manager.getUsername();
        UsernamePasswordToken token = new UsernamePasswordToken(manager.getUsername(),manager.getPassword());
        Subject currentUser = SecurityUtils.getSubject();

        try{
            logger.info("对用户"+managerName+"进行验证..");
            currentUser.login(token);
            logger.info("验证通过");
        }catch(UnknownAccountException uae){
            logger.error("未知账户"+uae);
            redirectAttributes.addFlashAttribute("message","位置账户");
        }catch (IncorrectCredentialsException ice){
            logger.error("错误的凭证"+ice);
            redirectAttributes.addFlashAttribute("message","密码错误");
        }catch (LockedAccountException lae){
            logger.error("账户已锁定"+lae);
            redirectAttributes.addFlashAttribute("message","账户已锁定");
        }catch (ExcessiveAttemptsException eae){
            logger.error("错误次数过多"+eae);
            redirectAttributes.addFlashAttribute("message","错误次数过多");
        }catch (AuthenticationException ae){
            logger.error("验证未通过"+ae);
            redirectAttributes.addFlashAttribute("message","用户名或密码不正确");
        }

        if(currentUser.isAuthenticated()){
            logger.info("用户通过认证");
            return "redirect:fx";
        }else{
            token.clear();
            return "redirect:/login";
        }

    }

    @RequestMapping(value="/logout",method=RequestMethod.GET)
    public String logout(RedirectAttributes redirectAttributes ){
        //使用权限管理工具进行用户的退出，跳出登录，给出提示信息
        SecurityUtils.getSubject().logout();
        redirectAttributes.addFlashAttribute("message", "您已安全退出");
        return "redirect:/login";
    }

    @RequestMapping("/403")
    public String unauthorizedRole(){
        logger.info("------没有权限-------");
        return "403";
    }

    @RequestMapping("/user")
    public String getUserList(Map<String, Object> model){
//        model.put("userList", userDao.getList());
        return "user";
    }

    @RequestMapping("/user/edit/{userid}")
    public String getUserList(@PathVariable int userid){
        logger.info("------进入用户信息修改-------");
        return "user_edit";
    }

}
