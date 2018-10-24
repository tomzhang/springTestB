package com.jk51.modules.goods.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.goods.service.YbGoodsCategoryLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/1.
 */
@Controller
@RequestMapping("/goods")
public class YbGoodsCategoryLabelController {
    @Autowired
    private YbGoodsCategoryLabelService ybGoodsCategoryLabelService;

    /**
     * 查询所有的疾病标签
     * @return
     */
    @RequestMapping(value="/getDiseaseAllLabel")
    @ResponseBody
    public Map<String,Object> getDiseaseAllLabel() {
        return ybGoodsCategoryLabelService.getDiseaseAllLabel();
    }

    /**
     * 查询所有的功效标签
     * @return
     */
    @RequestMapping(value="/getEffectAllLabel")
    @ResponseBody
    public Map<String,Object> getEffectAllLabel() {
        return ybGoodsCategoryLabelService.getEffectAllLabel();
    }

    /**
     * 根据商品ID查询商品的疾病标签与功效标签
     * @return
     */
    @RequestMapping(value="/getEffectAndDiseaseLabelById")
    @ResponseBody
    public Map<String,Object> getEffectAndDiseaseLabelById(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return ybGoodsCategoryLabelService.getEffectAndDiseaseLabelById(params);
    }

    /**
     * 根据商品ID修改商品的疾病标签与功效标签
     * @return
     */
    @RequestMapping(value="/updateEffectAndDiseaseLabelById")
    @ResponseBody
    public Map<String,Object> updateEffectAndDiseaseLabelById(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return ybGoodsCategoryLabelService.updateEffectAndDiseaseLabelById(params);
    }

}
