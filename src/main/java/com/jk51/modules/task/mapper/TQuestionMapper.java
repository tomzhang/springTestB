package com.jk51.modules.task.mapper;

import com.jk51.model.task.TQuestion;
import com.jk51.model.task.TQuestionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TQuestionMapper {
    long countByExample(TQuestionExample example);

    int deleteByExample(TQuestionExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TQuestion record);

    int insertSelective(TQuestion record);

    List<TQuestion> selectByExample(TQuestionExample example);

    TQuestion selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TQuestion record, @Param("example") TQuestionExample example);

    int updateByExample(@Param("record") TQuestion record, @Param("example") TQuestionExample example);

    int updateByPrimaryKeySelective(TQuestion record);

    int updateByPrimaryKey(TQuestion record);
}