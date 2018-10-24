package com.jk51.modules.account.service;

import com.jk51.model.account.models.SettlementDetailLog;
import com.jk51.modules.account.mapper.SettlementDetailLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * filename :com.jk51.modules.account.goodsService.
 * author   :zw
 * date     :2017/2/24
 * Update   :
 */
@Service
public class SettlementDetailLogService {
    @Autowired
    private SettlementDetailLogMapper settlementDetailLogMapper;

    /**
     * 保存对账明细操作日志
     * @param flow
     * @param trades_id
     */
    public void addSettlementDetailLog(Integer flow,long trades_id){
        SettlementDetailLog settlementDetailLog = new SettlementDetailLog();
        settlementDetailLog.setFlow(flow);
        settlementDetailLog.setOperator_id(0);// 之后扩展存操作用户
        settlementDetailLog.setOperator_name("system");
        settlementDetailLog.setTrades_id(trades_id);
        settlementDetailLogMapper.addSettlementDetailLog(settlementDetailLog);
    }
}
