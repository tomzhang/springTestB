package com.jk51.modules.official.controller;


import com.github.pagehelper.PageInfo;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.model.official.ApplyUser;
import com.jk51.modules.distribution.result.Resultful;
import com.jk51.modules.official.mapper.ApplyUserMapper;
import com.jk51.modules.official.service.ApplyUserService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
@RequestMapping("/official")
public class ApplyUserController {

    @Autowired
    private ApplyUserService applyUserService;
    @Autowired
    private ApplyUserMapper applyUserMapper;

    private Logger logger = LoggerFactory.getLogger(ApplyUserController.class);

    @ResponseBody
    @RequestMapping(value = "/sendCode", method = {RequestMethod.GET, RequestMethod.POST})
    public Resultful sendCode(String uphone,String code){
       return applyUserService.sendCode(uphone,code);
    }

    @ResponseBody
    @RequestMapping(value = "/sendRegistCode", method = {RequestMethod.GET, RequestMethod.POST})
    public Resultful sendRegistCode(String uphone,String code){
        return applyUserService.sendRegistCode(uphone,code);
    }

    /**
     * 51官网密码登陆
     */
    @ResponseBody
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public Resultful login(String uphone, String upwd) {
        return applyUserService.login(uphone,upwd);
    }

    /**
     * 验证码登录
     */
    @ResponseBody
    @RequestMapping(value = "/loginByCode", method = {RequestMethod.GET, RequestMethod.POST})
    public Resultful loginByCode(String uphone) {
        return applyUserService.loginByCode(uphone);
    }

    /**
     * 注册
     */
    @ResponseBody
    @RequestMapping(value = "/registUser", method = {RequestMethod.GET, RequestMethod.POST})
    public Resultful registUser(ApplyUser applyUser){
        return applyUserService.registUser(applyUser);
    }

    @ResponseBody
    @PostMapping(value = "/queryUser")
    public Resultful queryUser(HttpServletRequest request,
                          @RequestParam(value = "page", defaultValue = "1", required = true) Integer page,
                          @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize){
        Resultful result = new Resultful();
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        try {
        PageInfo<?> pageInfo = this.applyUserService.queryUser(params,page,pageSize);
        Map<String, Object> data = new HashedMap();
        data.put("current", pageInfo.getPageNum());
        data.put("before", pageInfo.getPrePage());
        data.put("next", pageInfo.getNextPage());
        data.put("total_pages", pageInfo.getPages());
        data.put("total_items", pageInfo.getTotal());
        data.put("items", pageInfo.getList());
        result.setCode(Resultful.SUCCESS);
        result.setData(data);
        }catch (Exception e){
            logger.error("获取官网申请账号列表失败,错误是" + e);
            result.setCode(Resultful.FAILED);
            result.setMsg("获取列表失败");
        }
        return result;
    }

}
