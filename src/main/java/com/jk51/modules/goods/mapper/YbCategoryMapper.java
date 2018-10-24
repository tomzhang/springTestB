package com.jk51.modules.goods.mapper;

import com.jk51.model.goods.Category;
import com.jk51.model.goods.YbCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-28
 * 修改记录:
 */
@Mapper
public interface YbCategoryMapper {
    int insert(YbCategory category);

    int deleteByCate_id(String cate_id);

    int updateCateName(@Param("cate_id") String cate_id,@Param("cate_name") String cate_name);

    List<YbCategory> getAllGoodsClassify();

    List<YbCategory> getParentCategory();

    List<YbCategory> getChildren(Integer cateId);

    YbCategory queryCatByCateId(Integer cateId);

    YbCategory queryCatByCateCode(Long cateCode);

    List<YbCategory> getGoodsClassifyByParentId(String parent_id);

    int updateByprimaryKey(YbCategory category);

    Map join51jkByCode(@Param("cateCode") long cateCode, @Param("siteId") int siteId);

    Integer getCodeId (@Param("cateCode") String cateCode);

    String selectCateNameById(@Param("id") Integer id);

}
