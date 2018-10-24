package com.jk51.modules.task.mapper;

import com.jk51.model.task.BTaskcount;
import com.jk51.model.task.BTaskcountExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import java.util.Map;


public interface BTaskcountMapper {
    long countByExample(BTaskcountExample example);

    int deleteByExample(BTaskcountExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BTaskcount record);

    int insertSelective(BTaskcount record);

    List<BTaskcount> selectByExample(BTaskcountExample example);

    BTaskcount selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BTaskcount record, @Param("example") BTaskcountExample example);

    int updateByExample(@Param("record") BTaskcount record, @Param("example") BTaskcountExample example);

    int updateByPrimaryKeySelective(BTaskcount record);

    int updateByPrimaryKey(BTaskcount record);

    int sumByExample(BTaskcountExample example);

    int queryTaskCountByExecuteId(Map<String,Object> param);

    List<Map<String,Object>> queryTaskListByJoinId(Map<String,Object>param);

    int selectSiteIdByJoinType(@Param("joinType") Byte joinType, @Param("joinId") Integer joinId);
}
