package com.jk51.modules.distribution.mapper;

import com.jk51.model.distribute.DdistributorMoneyRecord;
import com.jk51.modules.distribution.result.DistributorReward;
import com.jk51.modules.distribution.result.DistributorRewardDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-14 11:54
 * 修改记录:
 */
@Mapper
public interface DistributeMoneyRecordMapper {

    /**
     * 查询推荐人奖励分页列表
     * @param map
     * @return
     */
    List<DistributorReward> selectDistributorMoneyList(Map<String,Object> map);

    /**
     * 查询推荐人奖励总数
     * @param map
     * @return
     */
    Long selectDistributorMoneyListCount(Map<String,Object> map);

    /**
     * 查询商户下推荐人奖励总计
     * @param siteId
     * @return
     */
    DistributorReward selectDistributorMoneyTotal(@Param("siteId")Integer siteId);

    /**
     * 查询推荐人奖励详情分页列表（分页数据）
     * @param map
     * @return
     */
    List<DistributorRewardDetail> selectDistributorMoneyDetailList(Map<String,Object> map);

    /**
     * 查询推荐人奖励详情分页列表（未确认分页数据）
     * @param map
     * @return
     */
    List<DistributorRewardDetail> selectDistributorMoneyDetailUnconfirmed(Map<String,Object> map);

    /**
     * 查询推荐人奖励详情总数（记录总数）
     * @param map
     * @return
     */
    Long selectDistributorMoneyDetailListCount(Map<String,Object> map);

    /**
     * 查询推荐人奖励详情总计（奖励总数）
     * @param map
     * @return
     */
    Long selectDistributorMoneyDetailListTotal(Map<String,Object> map);

    /**
     * 查询推荐人奖励详情总计（未确认奖励总数）
     * @param map
     * @return
     */
    Long selectDistributorMoneyDetailListTotalUnconfirmed(Map<String,Object> map);

    Integer updateDistributorMoneyDetailById(@Param("id") Long id,@Param("siteId")Integer siteId,@Param("status") Integer status,@Param("remark") String remark,@Param("accountBalance") Long accountBalance);

    Long insertDistributorMoneyRecord(DdistributorMoneyRecord ddistributorMoneyRecord);

    List<Map<String,Object>> selectReferrerAccountTotal(@Param("siteId") Integer siteId , @Param("distributorId") Integer distributorId);

    DistributorRewardDetail selectDistributorRewardDetailById(@Param("id") Long id,@Param("siteId")Integer siteId);

    Integer selectRewardTotal(Map<String,Object> map);
    
    Integer getMyTotalReward(@Param("siteId")Integer siteId, @Param("distributorId")Integer distributorId, @Param("orderStatus")int orderStatus);

    Integer getWithdraw(@Param("siteId") Integer siteId, @Param("distributorId") Integer distributorId,@Param("status") Integer status);

    Map<String,Object> selectReferrerTotal(@Param("siteId") Integer siteId, @Param("distributorId") Integer distributorId,@Param("type") Integer type,@Param("status") Integer status);

}
