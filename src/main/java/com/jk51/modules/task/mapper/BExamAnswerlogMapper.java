package com.jk51.modules.task.mapper;

import com.jk51.model.task.BExamAnswerlog;
import com.jk51.model.task.BExamAnswerlogExample;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BExamAnswerlogMapper {
    long countByExample(BExamAnswerlogExample example);

    int deleteByExample(BExamAnswerlogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BExamAnswerlog record);

    int insertSelective(BExamAnswerlog record);

    List<BExamAnswerlog> selectByExample(BExamAnswerlogExample example);

    BExamAnswerlog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BExamAnswerlog record, @Param("example") BExamAnswerlogExample example);

    int updateByExample(@Param("record") BExamAnswerlog record, @Param("example") BExamAnswerlogExample example);

    int updateByPrimaryKeySelective(BExamAnswerlog record);

    int updateByPrimaryKey(BExamAnswerlog record);

    //计划结束的
    List<Date> maxTime();

    //所有的
    List<Date> allMaxTime();
}
