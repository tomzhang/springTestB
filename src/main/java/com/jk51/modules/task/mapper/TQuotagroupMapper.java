package com.jk51.modules.task.mapper;

import com.jk51.model.task.TQuotagroup;
import com.jk51.model.task.TQuotagroupExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TQuotagroupMapper {
    long countByExample(TQuotagroupExample example);

    int deleteByExample(TQuotagroupExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TQuotagroup record);

    int insertSelective(TQuotagroup record);

    List<TQuotagroup> selectByExample(TQuotagroupExample example);

    TQuotagroup selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TQuotagroup record, @Param("example") TQuotagroupExample example);

    int updateByExample(@Param("record") TQuotagroup record, @Param("example") TQuotagroupExample example);

    int updateByPrimaryKeySelective(TQuotagroup record);

    int updateByPrimaryKey(TQuotagroup record);
}