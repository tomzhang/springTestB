package com.jk51.modules.distribution.mapper;

import com.jk51.model.distribute.Reward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/9.
 */
@Mapper
public interface RewardMapper {

    List<Map<String,Object>> getRewardList (Map<String,Object> map);

    Integer receviedMoney (@Param("siteId") Integer siteId);

    Integer payMoney (@Param("siteId") Integer siteId);

    Integer queryMinMoney (@Param("siteId") Integer siteId);

    Integer updateMinMoney (@Param("siteId") Integer siteId,@Param("minMoney") Integer minMoney);

    Integer insertSelective(Reward reward);

    Integer updateOrderStatus(@Param("tradesId") Long tradesId, @Param("siteId") Integer siteId,
                               @Param("distributorId") Integer distributorId, @Param("orderStatus") Integer orderStatus);

    Reward selectRewardById(@Param("siteId") Integer siteId , @Param("distributorId") Integer distributorId, @Param("id")Integer id);

    Integer updateRewardStatus( @Param("siteId") Integer siteId, @Param("id")Integer id, @Param("rewardStatus") Integer rewardStatus);

    Reward selectRewardByOrderId(@Param("tradesId") Long tradeId,@Param("siteId") Integer siteId);

    Integer updateRewardRealPay(@Param("realPay") Integer realPay,@Param("siteId") Integer siteId,@Param("id") long id);
}
