package com.jk51.modules.task.mapper;

import com.jk51.model.task.BTaskConsumeLog;
import com.jk51.model.task.BTaskConsumeLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BTaskConsumeLogMapper {
    long countByExample(BTaskConsumeLogExample example);

    int deleteByExample(BTaskConsumeLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BTaskConsumeLog record);

    int insertSelective(BTaskConsumeLog record);

    List<BTaskConsumeLog> selectByExample(BTaskConsumeLogExample example);

    BTaskConsumeLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BTaskConsumeLog record, @Param("example") BTaskConsumeLogExample example);

    int updateByExample(@Param("record") BTaskConsumeLog record, @Param("example") BTaskConsumeLogExample example);

    int updateByPrimaryKeySelective(BTaskConsumeLog record);

    int updateByPrimaryKey(BTaskConsumeLog record);
}