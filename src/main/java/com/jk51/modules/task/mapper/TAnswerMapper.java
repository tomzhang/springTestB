package com.jk51.modules.task.mapper;

import com.jk51.model.task.TAnswer;
import com.jk51.model.task.TAnswerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TAnswerMapper {
    long countByExample(TAnswerExample example);

    int deleteByExample(TAnswerExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TAnswer record);

    int insertSelective(TAnswer record);

    List<TAnswer> selectByExample(TAnswerExample example);

    TAnswer selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TAnswer record, @Param("example") TAnswerExample example);

    int updateByExample(@Param("record") TAnswer record, @Param("example") TAnswerExample example);

    int updateByPrimaryKeySelective(TAnswer record);

    int updateByPrimaryKey(TAnswer record);

    int batchInsert(@Param("answers") List<TAnswer> answers);
}
