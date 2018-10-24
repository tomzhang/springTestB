package com.jk51.modules.account.service;

import com.jk51.model.account.models.FinanceAuditLog;
import com.jk51.modules.account.mapper.FinanceAuditLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * filename :com.jk51.modules.account.goodsService.
 * author   :zw
 * date     :2017/2/24
 * Update   :
 */
@Service
public class FinanceAuditLogService {
    @Autowired
    private FinanceAuditLogMapper financeAuditLogMapper;

    public int addFinanceAuditLog(Integer operation_id ,String operation_name ,String finance_no ,String remark,Integer financeId){
        FinanceAuditLog financeAuditLog = new FinanceAuditLog();
        financeAuditLog.setOperation_id(operation_id);
        financeAuditLog.setOperation_name(operation_name);
        financeAuditLog.setFinance_no(finance_no);
        financeAuditLog.setRemark(remark);
        financeAuditLog.setFinance_id(financeId);
       return financeAuditLogMapper.addFinanceAuditLogList(financeAuditLog);
    }
}
