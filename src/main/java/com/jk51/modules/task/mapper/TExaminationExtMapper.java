package com.jk51.modules.task.mapper;

import com.jk51.model.task.TExaminationExt;
import com.jk51.model.task.TExaminationExtExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TExaminationExtMapper {
    long countByExample(TExaminationExtExample example);

    int deleteByExample(TExaminationExtExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TExaminationExt record);

    int insertSelective(TExaminationExt record);

    List<TExaminationExt> selectByExampleWithBLOBs(TExaminationExtExample example);

    List<TExaminationExt> selectByExample(TExaminationExtExample example);

    TExaminationExt selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TExaminationExt record, @Param("example") TExaminationExtExample example);

    int updateByExampleWithBLOBs(@Param("record") TExaminationExt record, @Param("example") TExaminationExtExample example);

    int updateByExample(@Param("record") TExaminationExt record, @Param("example") TExaminationExtExample example);

    int updateByPrimaryKeySelective(TExaminationExt record);

    int updateByPrimaryKeyWithBLOBs(TExaminationExt record);

    int updateByPrimaryKey(TExaminationExt record);

    TExaminationExt selectByExamId(@Param("examId") Integer examId);
}
