package com.jk51.modules.task.mapper;

import com.jk51.model.JKHashMap;
import com.jk51.model.task.BTaskExecute;
import com.jk51.model.task.BTaskExecuteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import java.util.Map;

public interface BTaskExecuteMapper {
    long countByExample(BTaskExecuteExample example);

    int deleteByExample(BTaskExecuteExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BTaskExecute record);

    int insertSelective(BTaskExecute record);

    List<BTaskExecute> selectByExample(BTaskExecuteExample example);

    BTaskExecute selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BTaskExecute record, @Param("example") BTaskExecuteExample example);

    int updateByExample(@Param("record") BTaskExecute record, @Param("example") BTaskExecuteExample example);

    int updateByPrimaryKeySelective(BTaskExecute record);

    int updateByPrimaryKey(BTaskExecute record);

//    int insertList(List<BTaskExecute> record);

    List<JKHashMap<String,Object>> selectNotifyMsg();

    List<JKHashMap<String,Object>> queryNotifyMsgByPlanIds(String[] plainIds);

    int insertList(@Param("record") List<BTaskExecute> record);

    List<Map<String,Object>> selectTaskListByJoinId(Map<String,Object>param);

    int queryTaskCountByJoinId(Map<String,Object> param);

    Map<String,Object> queryCompleteInfo(@Param(value = "executeId")int excuteId);

    Integer queryExecutePrimaryKey (Map<String, Object> map);

    Integer isComplete (@Param("planId") Integer planId);

    int updateCountValue(@Param("id")Integer executeId);
}
