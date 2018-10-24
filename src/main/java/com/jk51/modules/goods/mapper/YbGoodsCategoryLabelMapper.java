package com.jk51.modules.goods.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/1.
 */
@Mapper
public interface YbGoodsCategoryLabelMapper {

    //查询所有的疾病标签
    List<Map<String,Object>> getDiseaseAllLabel();

    //查询所有的功效标签
    List<String> getEffectAllLabel();

    //根据商品ID查询商品的疾病标签与功效标签
    Map<String,Object> getEffectAndDiseaseLabelById(Map<String, Object> params);

    //根据商品ID查询商品的疾病标签与功效标签
    Integer updateEffectAndDiseaseLabelById(Map<String, Object> params);
}
