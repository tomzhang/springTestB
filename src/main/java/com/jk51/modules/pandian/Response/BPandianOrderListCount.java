package com.jk51.modules.pandian.Response;

import java.math.BigDecimal;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/5/16
 * 修改记录:
 */
public class BPandianOrderListCount {

    private BigDecimal inventoryTotal;  //账目库存数
    private BigDecimal actualStoreTotal; //实际库存
    private BigDecimal ProfitAndLossNum;   //盈亏数
    private String profitAndLossStatus; //盈亏状态

    public BigDecimal getInventoryTotal() {
        return inventoryTotal;
    }

    public void setInventoryTotal(BigDecimal inventoryTotal) {
        this.inventoryTotal = inventoryTotal;
    }

    public BigDecimal getActualStoreTotal() {
        return actualStoreTotal;
    }

    public void setActualStoreTotal(BigDecimal actualStoreTotal) {
        this.actualStoreTotal = actualStoreTotal;
    }

    public BigDecimal getProfitAndLossNum() {
        return ProfitAndLossNum;
    }

    public void setProfitAndLossNum(BigDecimal profitAndLossNum) {
        ProfitAndLossNum = profitAndLossNum;
    }

    public String getProfitAndLossStatus() {
        return profitAndLossStatus;
    }

    public void setProfitAndLossStatus(String profitAndLossStatus) {
        this.profitAndLossStatus = profitAndLossStatus;
    }
}
