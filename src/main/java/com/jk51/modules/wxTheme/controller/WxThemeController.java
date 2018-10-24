package com.jk51.modules.wxTheme.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.theme.WxTheme;
import com.jk51.model.theme.WxThemeParm;
import com.jk51.modules.wxTheme.service.WxThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/8/7
 * 修改记录:
 */
@Controller
@RequestMapping("theme")
public class WxThemeController {

    @Autowired private WxThemeService wxThemeService;


    @RequestMapping("list")
    public @ResponseBody PageInfo<WxTheme> getLstBySiteId(WxThemeParm parm) {
        PageHelper.startPage(parm.getPageNum(),parm.getPageSize());//开启分页

        List<WxTheme> lst = wxThemeService.getLstBySiteId(parm);
        PageInfo<WxTheme> pageInfo = new PageInfo<>(lst);
        return pageInfo;
    }
    @RequestMapping("list2")
    public @ResponseBody List<WxTheme> getLstBySiteId2(WxThemeParm parm) {

        List<WxTheme> lst = wxThemeService.getLstBySiteId(parm);
        return lst;
    }

    @RequestMapping("getWechatUrl")
    public @ResponseBody String getWechatUrl(Integer siteId, Integer type) {

        return wxThemeService.getWechatUrl(siteId);
    }

    @RequestMapping("add")
    public @ResponseBody Integer addTheme(HttpServletRequest request){
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        WxTheme theme = new WxTheme();
        try {
            theme = JacksonUtils.json2pojo(objectMap.get("meta").toString(), WxTheme.class);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return wxThemeService.add(theme);
    }

    @RequestMapping("addAndDraft")
    public @ResponseBody Integer addAndDraft(HttpServletRequest request){
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        WxTheme theme = new WxTheme();
        try {
            theme = JacksonUtils.json2pojo(objectMap.get("meta").toString(), WxTheme.class);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return wxThemeService.addAndDraft(theme);
    }

    @RequestMapping("upd")
    public @ResponseBody Integer updTheme(HttpServletRequest request){
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        WxTheme theme = new WxTheme();
        try {
            theme = JacksonUtils.json2pojo(objectMap.get("meta").toString(), WxTheme.class);
        } catch (Exception e) {
            return 0;
        }
        return wxThemeService.update(theme);
    }

    @RequestMapping("del")
    public @ResponseBody Integer delTheme(HttpServletRequest request){
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        WxTheme theme = new WxTheme();
        Integer siteId = Integer.parseInt(objectMap.get("siteId").toString());
        Integer themeId = Integer.parseInt(objectMap.get("themeId").toString());
        theme.setSiteId(siteId);
        theme.setThemeId(themeId);
        return wxThemeService.del(theme);
    }

    @RequestMapping("getWxTheme")
    public @ResponseBody WxTheme getWxTheme(HttpServletRequest request){
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        WxTheme theme = new WxTheme();
        Integer siteId = Integer.parseInt(objectMap.get("siteId").toString());
        String title = objectMap.get("title").toString();
        theme.setSiteId(siteId);
        theme.setTitle(title);
        return wxThemeService.getWxTheme(theme);
    }


}
