package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:商家账号信息
 * 作者: baixiongfei
 * 创建日期: 2017/3/1
 * 修改记录:
 */
public class YBAccount {
    private int	accountId;
    private int	sellerId;
    private String	accountName;
    private int	payplatformId;
    private String	account;
    private String	paymentName;
    private Double	proportion;
    private Timestamp createTime;
    private Timestamp	updateTime;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getPayplatformId() {
        return payplatformId;
    }

    public void setPayplatformId(int payplatformId) {
        this.payplatformId = payplatformId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public Double getProportion() {
        return proportion;
    }

    public void setProportion(Double proportion) {
        this.proportion = proportion;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
