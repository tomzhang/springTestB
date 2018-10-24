package com.jk51.modules.distribution.mapper;

import com.jk51.modules.distribution.result.DistributorReward;
import com.jk51.modules.distribution.result.RefereeList;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 推荐人账户（奖励，提现，余额的总计）操作
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-05-07 13:42
 * 修改记录:
 */
public interface RefereeListMapper {


    List<RefereeList> selectRefereeList(@Param("siteId") Integer siteId, @Param("distributorId") Long distributorId);

    Integer insertRefereeList(RefereeList refereeList);

    Integer updateRefereeList(RefereeList refereeList);

    List<DistributorReward> selectRefereeLists(Map<String,Object> map);

    Long selectRefereeListCount(Map<String,Object> map);

    DistributorReward selectRefereeListTotal(Map<String,Object> map);

}
