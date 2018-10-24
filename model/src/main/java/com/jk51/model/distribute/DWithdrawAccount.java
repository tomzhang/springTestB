package com.jk51.model.distribute;

import java.io.Serializable;
import java.util.Date;

/**
 * @author
 */
public class DWithdrawAccount implements Serializable {
    /**
     * 提现账号表
     */
    
    private Integer id;

    /**
     * 分销商id
     */
    private Integer distributorId;

    /**
     * 药店总部
     */
    private Integer owner;

    /**
     * 开户人姓名
     */
    private String name;

    /**
     * 提现帐号
     */
    private String account;

    /**
     * 账号类型: 100:ali (支付宝) ，200:wx (微信)，300:银联
     */
    private String type;

    /**
     * 添加时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    
    private Date updateTime;

    /**
     * 开户行名称
     */
    private String bandName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }
}