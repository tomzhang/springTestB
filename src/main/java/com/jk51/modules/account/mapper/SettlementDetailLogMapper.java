package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.SettlementDetailLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * filename :com.jk51.modules.account.mapper.
 * author   :zw
 * date     :2017/2/24
 * Update   :
 */
@Mapper
public interface SettlementDetailLogMapper {
    int addSettlementDetailLog (@Param(value = "settlementDetailLog") SettlementDetailLog settlementDetailLog);
}
