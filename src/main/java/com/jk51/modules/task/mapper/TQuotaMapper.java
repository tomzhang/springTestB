package com.jk51.modules.task.mapper;

import com.jk51.model.task.TQuota;
import com.jk51.model.task.TQuotaExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TQuotaMapper {
    long countByExample(TQuotaExample example);

    int deleteByExample(TQuotaExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TQuota record);

    int insertSelective(TQuota record);

    List<TQuota> selectByExample(TQuotaExample example);

    TQuota selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TQuota record, @Param("example") TQuotaExample example);

    int updateByExample(@Param("record") TQuota record, @Param("example") TQuotaExample example);

    int updateByPrimaryKeySelective(TQuota record);

    int updateByPrimaryKey(TQuota record);

    List<TQuota> selectQuotaGroupByGroupId(Map<String,Object> params);
}
