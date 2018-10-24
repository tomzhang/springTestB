package com.jk51.modules.task.mapper;

import com.jk51.model.JKHashMap;
import com.jk51.model.task.BTaskreward;
import com.jk51.model.task.BTaskrewardExample;
import java.util.List;

import com.jk51.modules.task.domain.dto.RewardRankQueryDTO;
import org.apache.ibatis.annotations.Param;
import java.util.Map;

import com.jk51.modules.task.domain.FollowTask;
import org.springframework.web.bind.annotation.PostMapping;

public interface BTaskrewardMapper {
    long countByExample(BTaskrewardExample example);

    int deleteByExample(BTaskrewardExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BTaskreward record);

    int insertSelective(BTaskreward record);

    List<BTaskreward> selectByExample(BTaskrewardExample example);

    BTaskreward selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BTaskreward record, @Param("example") BTaskrewardExample example);

    int updateByExample(@Param("record") BTaskreward record, @Param("example") BTaskrewardExample example);

    int updateByPrimaryKeySelective(BTaskreward record);

    int updateByPrimaryKey(BTaskreward record);

    List<FollowTask> taskAdminFollow(Map<String, Object> param);

    Map<String,Integer> queryTaskRewardByJoinId(Map<String,Object> param);

    List<Map<String,Object>>queryTaskRewardList(Map<String,Object> param);

    List<FollowTask> queryRewardFollow(Map<String, Object> param);

    /**
     * 添加一条奖励记录 如果记录已经存在更新奖励值和统计值
     * 奖励值覆盖 统计值在原有基础上增加
     * @param record
     * @return
     */
    int addRewardOnDuplicateUpdate(BTaskreward record);

    /**
     * 更新一条奖励记录 增加count_value
     * @param record
     * @return
     */
    int updateRewardIncre(BTaskreward record);

    //计划结束的健康豆和
    Integer beansReward(@Param("siteId") Integer siteId,@Param("storeAdminId") Integer storeAdminId);

    /**
     * 任务排名
     * @param param
     * @return
     */
    List<JKHashMap> selectSortList(RewardRankQueryDTO param);

    /**
     * 个人排名
     * @param param
     * @return
     */
    JKHashMap selectPersonalSort(RewardRankQueryDTO param);

    /**
     * 第多少名
     * @param executeId
     * @param countValue
     * @return
     */
    int selectRank(@Param("executeId") Integer executeId, @Param("countValue") Integer countValue);

    List<JKHashMap> selectNoneReward(@Param("joinType") int joinType, @Param("selectedIds") int[] selectedIds);
}
