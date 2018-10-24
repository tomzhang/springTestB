package com.jk51.modules.balance.timerbalance;

import com.jk51.modules.balance.service.AccountCheckingService;
import com.jk51.modules.balance.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 结算定时任务
 * Created by Administrator on 2018/5/30.
 */
@Component
public class BalanceTaskSchedule {
    @Autowired
    BalanceService balanceService;
    @Autowired
    AccountCheckingService accountCheckingService;

    /**
     * 每天凌晨定时执行数据结算
     */
    public void executeAccount(){
        balanceService.balanceTiming();
    }

    /**
     * 每天早上10点定时检查服务商余额状态
     */
    public void executeSendMsgForTiming(){
        balanceService.sendMsgForTiming();
    }

    /**
     * 每月1号凌晨1点执行月账单出账
     */
    public void executeOneMonthAccount(){
        balanceService.oneMonth();
    }

    /**
     * 每天晚上23：30执行，检查是否有订单佣金没有添加进记录
     */
    public void executeDingshiJianLouAccount(){
        balanceService.executeDingshiJianLouAccount();
    }

    /**
     * 每月1号凌晨1点执行：总部资金对账
     */
    public void executeTimeingMerchantFunds(){
        accountCheckingService.timeingMerchantFunds();
    }

    /**
     * 每月1号凌晨1点执行：门店资金对账
     */
    public void executeTimeingStoreFunds(){
        accountCheckingService.timeingStoreFunds();
    }


}
