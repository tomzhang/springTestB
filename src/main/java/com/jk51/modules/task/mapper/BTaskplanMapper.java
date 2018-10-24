package com.jk51.modules.task.mapper;

import com.jk51.model.task.BTaskplan;
import com.jk51.model.task.BTaskplanExample;
import com.jk51.modules.es.entity.GoodsInfosAdminReq;
import com.jk51.modules.task.domain.TaskPlanChangeStatus;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BTaskplanMapper {
    long countByExample(BTaskplanExample example);

    int deleteByExample(BTaskplanExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BTaskplan record);

    int insertSelective(BTaskplan record);

    List<BTaskplan> selectByExampleWithBLOBs(BTaskplanExample example);

    List<BTaskplan> selectByExample(BTaskplanExample example);

    BTaskplan selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BTaskplan record, @Param("example") BTaskplanExample example);

    int updateByExampleWithBLOBs(@Param("record") BTaskplan record, @Param("example") BTaskplanExample example);

    int updateByExample(@Param("record") BTaskplan record, @Param("example") BTaskplanExample example);

    int updateByPrimaryKeySelective(BTaskplan record);

    int updateByPrimaryKeyWithBLOBs(BTaskplan record);

    int updateByPrimaryKey(BTaskplan record);

    Map<String,Object> getDetails(Integer id);

    List<Integer> getAllStart();

    Integer taskIsExist(@Param("id") Integer id);


    Integer insertTaskPlan(Map<String, Object> param);

    /**
     * 查询即将修改状态的计划
     * @return
     */
    List<TaskPlanChangeStatus> selectWillChangeStatusList();
    List<Map<String,Object>> getTaskPlan(HashMap queryMap);

    List<Integer> queryPlanIdsByJoin(Map<String, Object> param);








    List<BTaskplan> queryPlanListById(Map<String,Object>param);

    String queryTaskIdsById(@Param("id") Integer id);

    Map<String, Object> queryTaskPlanTime(@Param("id") Integer id);

    List<String> queryTaskPlanForGoodsId(GoodsInfosAdminReq goodsInfosAdminReq);

}
