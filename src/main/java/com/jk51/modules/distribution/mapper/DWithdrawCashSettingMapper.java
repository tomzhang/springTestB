package com.jk51.modules.distribution.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-19 14:50
 * 修改记录:
 */
@Mapper
public interface DWithdrawCashSettingMapper {

    /**
     * 查询最小提现金额
     * @param siteId
     * @return
     */
    Integer selectWithdrawMinMoneyBySiteId(@Param("siteId")Integer siteId);

    /**
     * 修改最小提现金额
     * @param siteId
     * @param minMoney
     * @return
     */
    Integer updateWithdrawMinMoneyBySiteId(@Param("siteId")Integer siteId,@Param("minMoney")Integer minMoney);

}
