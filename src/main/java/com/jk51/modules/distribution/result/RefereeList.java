package com.jk51.modules.distribution.result;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 推荐人账户（奖励，提现，余额的总计）
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-05-07 13:58
 * 修改记录:
 */
public class RefereeList {

    private Long refereeId; // int(10) NOT NULL,
    private String refereeName;// varchar(20) NOT NULL COMMENT '推荐人',
    private Long totalIncomeAmount;// int(15) DEFAULT NULL COMMENT '收入总金额',
    private Long totalExpenditure;// int(15) DEFAULT NULL COMMENT '支出总金额',
    private Long accountBalance;// int(15) DEFAULT NULL COMMENT '账户余额',

    public RefereeList() {
    }

    public Long getRefereeId() {
        return refereeId;
    }

    public void setRefereeId(Long refereeId) {
        this.refereeId = refereeId;
    }

    public String getRefereeName() {
        return refereeName;
    }

    public void setRefereeName(String refereeName) {
        this.refereeName = refereeName;
    }

    public Long getTotalIncomeAmount() {
        return totalIncomeAmount;
    }

    public void setTotalIncomeAmount(Long totalIncomeAmount) {
        this.totalIncomeAmount = totalIncomeAmount;
    }

    public Long getTotalExpenditure() {
        return totalExpenditure;
    }

    public void setTotalExpenditure(Long totalExpenditure) {
        this.totalExpenditure = totalExpenditure;
    }

    public Long getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Long accountBalance) {
        this.accountBalance = accountBalance;
    }
}
