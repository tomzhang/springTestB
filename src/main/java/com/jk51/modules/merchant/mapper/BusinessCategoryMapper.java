package com.jk51.modules.merchant.mapper;

import com.jk51.model.merchant.BusinessCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BusinessCategoryMapper {
    BusinessCategory getById(@Param("id") Integer id);

    List<BusinessCategory> getList();

    List<BusinessCategory> getByParentId(@Param("parent_id")Integer parent_id);
}
