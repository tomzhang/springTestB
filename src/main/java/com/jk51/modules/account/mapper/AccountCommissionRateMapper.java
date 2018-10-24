package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.AccountCommissionRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 查询商家设置的交易佣金比例
 * 作者: baixiongfei
 * 创建日期: 2017/3/16
 * 修改记录:
 */
@Mapper
public interface AccountCommissionRateMapper {

    public AccountCommissionRate getCommissionRatById(@Param("siteId") Integer siteId);
    public int addAccount(@Param(value = "accountCommissionRate") AccountCommissionRate accountCommissionRate);
    public int updateAccount(@Param(value = "accountCommissionRate") AccountCommissionRate accountCommissionRate);
}
