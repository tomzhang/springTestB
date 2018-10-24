package com.jk51.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-03-24
 * 修改记录:
 */
public class StoreDeployInfo {
    public String clerkName;
    public String clerkMobile;
    public String perStore;
    public String newStore;
    public String operator;
    public Date createTime;

    public String getClerkName() {
        return clerkName;
    }

    public void setClerkName(String clerkName) {
        this.clerkName = clerkName;
    }

    public String getClerkMobile() {
        return clerkMobile;
    }

    public void setClerkMobile(String clerkMobile) {
        this.clerkMobile = clerkMobile;
    }

    public String getPerStore() {
        return perStore;
    }

    public void setPerStore(String perStore) {
        this.perStore = perStore;
    }

    public String getNewStore() {
        return newStore;
    }

    public void setNewStore(String newStore) {
        this.newStore = newStore;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
