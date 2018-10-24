package com.jk51.modules.task.mapper;

import com.jk51.model.task.TCounttype;
import com.jk51.model.task.TCounttypeExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TCounttypeMapper {
    long countByExample(TCounttypeExample example);

    int deleteByExample(TCounttypeExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TCounttype record);

    int insertSelective(TCounttype record);

    List<TCounttype> selectByExample(TCounttypeExample example);

    String conactTypeName(@Param("ids") List<Integer> ids);

    TCounttype selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TCounttype record, @Param("example") TCounttypeExample example);

    int updateByExample(@Param("record") TCounttype record, @Param("example") TCounttypeExample example);

    int updateByPrimaryKeySelective(TCounttype record);

    int updateByPrimaryKey(TCounttype record);

    List<TCounttype> selectTCounttypeByByGroupId(Map<String,Object> params);

}