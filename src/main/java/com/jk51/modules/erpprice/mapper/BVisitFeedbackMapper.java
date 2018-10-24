package com.jk51.modules.erpprice.mapper;

import com.jk51.model.erpprice.BVisitFeedback;
import com.jk51.model.erpprice.BVisitFeedbackExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BVisitFeedbackMapper {
    long countByExample(BVisitFeedbackExample example);

    int deleteByExample(BVisitFeedbackExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BVisitFeedback record);

    int insertSelective(BVisitFeedback record);

    List<BVisitFeedback> selectByExample(BVisitFeedbackExample example);

    BVisitFeedback selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BVisitFeedback record, @Param("example") BVisitFeedbackExample example);

    int updateByExample(@Param("record") BVisitFeedback record, @Param("example") BVisitFeedbackExample example);

    int updateByPrimaryKeySelective(BVisitFeedback record);

    int updateByPrimaryKey(BVisitFeedback record);
}