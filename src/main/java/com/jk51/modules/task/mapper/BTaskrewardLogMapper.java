package com.jk51.modules.task.mapper;

import com.jk51.model.task.BTaskrewardLog;
import com.jk51.model.task.BTaskrewardLogExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BTaskrewardLogMapper {
    long countByExample(BTaskrewardLogExample example);

    int deleteByExample(BTaskrewardLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BTaskrewardLog record);

    int insertSelective(BTaskrewardLog record);

    List<BTaskrewardLog> selectByExample(BTaskrewardLogExample example);

    BTaskrewardLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BTaskrewardLog record, @Param("example") BTaskrewardLogExample example);

    int updateByExample(@Param("record") BTaskrewardLog record, @Param("example") BTaskrewardLogExample example);

    int updateByPrimaryKeySelective(BTaskrewardLog record);

    int updateByPrimaryKey(BTaskrewardLog record);
}