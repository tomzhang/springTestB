package com.jk51.modules.merchant.controller;

import com.jk51.model.merchant.BusinessCategory;
import com.jk51.modules.merchant.mapper.BusinessCategoryMapper;
import com.jk51.modules.merchant.service.BusinessCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：经营类目
 * 作者: XC
 * 创建日期: 2018-09-27 17:54
 * 修改记录:
 **/
@RestController
@RequestMapping("/business_category")
public class BusinessCategoryController {
    @Autowired
    private BusinessCategoryService businessCategoryService;
    @Autowired
    private BusinessCategoryMapper businessCategoryMapper;

    @GetMapping("/getById/{id}")
    public BusinessCategory getById(@PathVariable Integer id){
        BusinessCategory businessCategory = businessCategoryMapper.getById(id);
        if (businessCategory==null){
            businessCategory = new BusinessCategory();
        }
        return businessCategory;
    }

    @GetMapping("/getByParentId/{parent_id}")
    public List<BusinessCategory> getByParentId(@PathVariable Integer parent_id){
        return businessCategoryMapper.getByParentId(parent_id);
    }
}
