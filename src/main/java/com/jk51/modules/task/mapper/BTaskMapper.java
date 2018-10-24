package com.jk51.modules.task.mapper;

import com.jk51.model.task.BTask;
import com.jk51.model.task.BTaskExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BTaskMapper {
    long countByExample(BTaskExample example);

    int deleteByExample(BTaskExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BTask record);

    int insertSelective(BTask record);

    List<BTask> selectByExample(BTaskExample example);

    BTask selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BTask record, @Param("example") BTaskExample example);

    int updateByExample(@Param("record") BTask record, @Param("example") BTaskExample example);

    int updateByPrimaryKeySelective(BTask record);

    int updateByPrimaryKey(BTask record);

    Map<String, Object> taskDetail(@Param("id") Integer id);

    List<Map<String, Object>> queryTaskList(Map<String, Object> map);

    int updateByPrimaryKeyNotOverflowLimit(@Param("taskId") Integer taskId, @Param("rewardValue") int rewardValue);

    List<Map<String, Object>> queryTaskListByIds(Map<String, Object> map);

    Map<String,Object> queryTaskInfoByExecuteId(Map<String, Object> map);

    List<Map<String,Object>> queryMyRewards(Map map);

    BTask findById(Integer id);

    List<Map<String,Object>> queryTypeRewardTotal(BTaskExample example);

    Map<String,Object> queryAdminHead(@Param("siteId") Integer siteId,@Param("joinId") Integer joinId);

    List<Integer> checkExaminationInTask();
}
