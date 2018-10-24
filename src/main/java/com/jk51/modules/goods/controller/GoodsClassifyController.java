package com.jk51.modules.goods.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.goods.service.GoodsClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:商品分类
 * 作者: gaojie
 * 创建日期: 2017-02-28
 * 修改记录:
 */
@RequestMapping("ybcat")
@Controller
@ResponseBody
public class GoodsClassifyController {

    @Autowired
    private GoodsClassifyService goodsClassifyService;

    /**
     * 添加商品分类
     * param：
     * parent_id 父类id，一级分类时为0
     * cate_name  分类名称
     * if_distributor  旧项目有传，暂时不知道作用
     */
    @RequestMapping("/addGoodsClassify")
    public ReturnDto addGoodsClassify(HttpServletRequest request) {

        String parent_id = request.getParameter("parent_id");
        if (StringUtil.isEmpty(parent_id)) {
            parent_id = "0";
        }

        String cate_name = request.getParameter("cate_name");
        if (StringUtil.isEmpty(cate_name)) {
            return ReturnDto.buildStatusERRO("cate_name为空");
        }


        return goodsClassifyService.addGoodsClassify(parent_id, cate_name);

    }

    /**
     * 删除商品分类，如果商品分类对应有产品，则需要把对应的商品移到其他的商品分类上才能删除
     * param：
     * cate_id 父类id，一级分类时为0
     */
    @RequestMapping("/categoryDelete")
    public Map<String, Object> categoryDelete(HttpServletRequest request) {

        String cate_id = request.getParameter("cate_id");
        return goodsClassifyService.category_delete(cate_id);
    }


    /**
     * 批量修改商品分類名稱
     */
    @RequestMapping("/productCategoryBatchEdit")
    public Map<String, Object> productCategoryBatchEdit(HttpServletRequest request) {

        String category_list = request.getParameter("category_list");
        return goodsClassifyService.category_batchEdit(category_list);

    }

    /**
     * 查詢所有的商品分類
     */
    @RequestMapping("/getAllGoodsClassify")
    @ResponseBody
    public Map<String, Object> getAllGoodsClassify(HttpServletRequest request) {
        Map<String, Object> map = goodsClassifyService.getAllGoodsClassify();
        return map;
    }

    /**
     * 根据parent_id查询商品分类
     * */
    @RequestMapping("/getGoodsClassifyByParentId")
    public Map<String, Object> getGoodsClassifyByParentId(HttpServletRequest request) {


        String parent_id = request.getParameter("parent_id");
        return goodsClassifyService.getGoodsClassifyByParentId(parent_id);
    }

}
