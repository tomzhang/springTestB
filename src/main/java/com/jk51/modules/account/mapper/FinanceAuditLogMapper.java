package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.FinanceAuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * filename :com.jk51.modules.account.mapper.
 * author   :zw
 * date     :2017/2/24
 * Update   :
 */
@Mapper
public interface FinanceAuditLogMapper {
    int addFinanceAuditLogList(@Param(value = "financeAuditLog") FinanceAuditLog financeAuditLog);
}
