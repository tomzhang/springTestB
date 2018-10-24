package com.jk51.modules.goods.mapper;


import com.jk51.model.goods.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryMapper {

    List<Category> getList(Map m);

    List<Category> getListByPid(Map m);

    Category getByCateId(Map m);

    int insert(Category cate);

    int del(Map m);

    int update(Category cate);

    String getCodeIncrease(Map m);

    Category getByCateCode(Map m);

    int updateGoodsCate(Map m);

    Category getByCateName(Map m);

    String queryGoodsImgExtra(Map m);

    String querGoodsHashImg(Map m);

    int updateCategoryImg(Map m);

    int delCategoryImg(Map m);

    String getCategoryDefault(Map param);
}
