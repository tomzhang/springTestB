package com.jk51.modules.official.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.official.YbCarousel;
import com.jk51.model.official.YbNewstrends;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.official.service.YbOffcialService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 官网后台配置
 *
 * @auhter zy
 * @create 2017-11-07 13:51
 */
@Controller
@RequestMapping("/offcial")
public class YbOffcialController {


    @Autowired
    private YbOffcialService ybOffcialService;

    /**
     *  添加或修改轮播图
     * @param ybCarousel
     * @return
     */
    @RequestMapping(value = "/addOrUpdateCarousel",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto addOrUpdateCarousel(@RequestBody YbCarousel ybCarousel) {
//        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        //先根据project和序列查询
//        YbCarousel carousel = ybOffcialService.selectCarlouselRecord(parameterMap);
//        String carouselId = String.valueOf(parameterMap.get("id"));
        Integer id = ybCarousel.getId();
        int result = 0;
//        if(Objects.nonNull(carousel)) {//更新
//        if(StringUtil.isNotEmpty(carouselId) && !carouselId.equalsIgnoreCase("null")) {
        if(Objects.nonNull(id)) {
            result = ybOffcialService.updateCarouselRecord(ybCarousel);
            if(result == 0) return ReturnDto.buildFailedReturnDto("更新轮播图失败!");
        }else {
            result = ybOffcialService.insertCarouselRecord(ybCarousel);
            if(result == 0) return ReturnDto.buildFailedReturnDto("新增轮播图失败!");
        }
        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     * 删除轮播图
     * @param request
     * @return
     */
    @RequestMapping(value = "/deleteCarousel",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto deleteCarousel(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        //根据id删除
        String id = String.valueOf(parameterMap.get("id"));
        if(StringUtil.isEmpty(id)) {
            return ReturnDto.buildFailedReturnDto("删除失败,没有id");
        }
        Integer integer = Integer.valueOf(id);
        int result = ybOffcialService.deleteCarouselRecord(integer);
        if(result == 1) {
            return ReturnDto.buildSuccessReturnDto(result);
        }
        return ReturnDto.buildFailedReturnDto("删除轮播图失败!");
    }

    /**
     * 查询轮播图列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/selectCarousels",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto selectCarousels(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        //根据project查询
        String project = String.valueOf(parameterMap.get("project"));
        if(StringUtil.isNotEmpty(project) && !project.equalsIgnoreCase("null")) {
            Integer pro = Integer.valueOf(project);
            List<YbCarousel> carousels = ybOffcialService.selectCarlousels(pro);
            return ReturnDto.buildSuccessReturnDto(carousels);
        }else {
            return  ReturnDto.buildFailedReturnDto("project主题编号不能为空!");
        }

    }

    /**
     *  分页查询新闻列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/selectNewsList",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto selectNewsList(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        String newsHeadline = String.valueOf(parameterMap.get("title"));
        if(StringUtil.isNotEmpty(newsHeadline) && !newsHeadline.equalsIgnoreCase("null")) {
            parameterMap.put("title",newsHeadline);
        }
        Integer pageNum = Integer.valueOf(parameterMap.get("pageNum").toString());
        Integer pageSize = Integer.valueOf(parameterMap.get("pageSize").toString());
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        List<YbNewstrends> newstrends = ybOffcialService.selectNewsList(parameterMap);
        if (Objects.nonNull(newstrends) && newstrends.size() != 0) {
            PageInfo pageInfo = new PageInfo(newstrends);
            Map<String,Object> result = new HashedMap();
            result.put("pageNums",pageInfo.getPages());
            result.put("totals",pageInfo.getTotal());
            result.put("curPageSize",pageInfo.getPageSize());
            result.put("curPageNum",pageInfo.getPageNum());
            result.put("result",newstrends);
            return ReturnDto.buildSuccessReturnDto(result);
        }
        return ReturnDto.buildFailedReturnDto("没有查询到结果!");
    }

    /**
     * 删除新闻
     * @param request
     * @return
     */
    @RequestMapping(value = "/deleteNews",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto deleteNews(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        //根据id删除
        String id = String.valueOf(parameterMap.get("id"));
        if(StringUtil.isNotEmpty(id)) {
            Integer newsId = Integer.valueOf(id);
            Integer result = ybOffcialService.deleteNewsById(newsId);
            if(result.equals(1)) {
                return  ReturnDto.buildSuccessReturnDto(result);
            }else {
                return  ReturnDto.buildFailedReturnDto("新闻删除失败!");
            }

        }else {
            return ReturnDto.buildFailedReturnDto("缺少新闻编号!!");
        }
    }

    /**
     * 新增或修改新闻动态
     * @param ybNewstrends
     * @return
     */
    @RequestMapping(value = "/insertOrAlertNews",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto insertOrAlertNews(@RequestBody YbNewstrends ybNewstrends) {
//        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
//        String id = String.valueOf(parameterMap.get("id"));
        Integer id = ybNewstrends.getId();
        Integer result = 0;
        if(id != null) {
            //更新
            result = ybOffcialService.updateNews(ybNewstrends);
        }else {
            result = ybOffcialService.insertNews(ybNewstrends);
        }
        if(result.equals(1)) return ReturnDto.buildSuccessReturnDto(result);
        return ReturnDto.buildFailedReturnDto("修改新闻动态失败!");
    }



}
