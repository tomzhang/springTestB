package com.jk51.model.account.requestParams;

import com.jk51.model.order.Page;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chenpeng
 * 创建日期: 2017/3/15
 * 修改记录:
 */
public class QuerySettlementLog extends Page{

    private Integer sellerId;
    private String sellerName;
    private String executeStatus;
    private String financeNo;
    private Date sOutDate;
    private Date eOutDate;

    private Integer siteId;

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(String executeStatus) {
        this.executeStatus = executeStatus;
    }

    public String getFinanceNo() {
        return financeNo;
    }

    public void setFinanceNo(String financeNo) {
        this.financeNo = financeNo;
    }

    public Date getsOutDate() {
        return sOutDate;
    }

    public void setsOutDate(Date sOutDate) {
        this.sOutDate = sOutDate;
    }

    public Date geteOutDate() {
        return eOutDate;
    }

    public void seteOutDate(Date eOutDate) {
        this.eOutDate = eOutDate;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
