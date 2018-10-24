package com.jk51.model.balance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by Administrator on 2018/4/26.
 */
public class Balance {

    @JsonProperty("siteId")
    private Integer siteId;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("balance")
    private Integer balance; //余额

    @JsonProperty("balanceWarning")
    private Integer balanceWarning; //余额预警值

    @JsonProperty("credit")
    private Integer credit;         //信用值

    @JsonProperty("isValid")
    private Integer isValid;        //是否禁用

    @JsonProperty("createTime")
    private Date createtime;        //创建时间

    @JsonProperty("updateTime")
    private Date updatetime;        //修改时间

    @JsonProperty("msgNum")
    private Integer msgNum;        //短信提醒次数

    @JsonProperty("msgTotalNum")
    private Integer msgTotalNum;   //短信总数

    @JsonProperty("msgFee")
    private Integer msgFee;        //短信费率

    @JsonProperty("msgSwitch")
    private Integer msgSwitch;        //短信开关

    @JsonProperty("isToll")
    private Integer isToll;        //操作是否收费

    @JsonProperty("dutyPhone")
    private String dutyPhone;     //51负责人电话

    @JsonProperty("sitePhone")
    private String sitePhone;     //商家负责人电话

    @JsonProperty("recordTime")
    private Date recordTime;     //商家负责人电话

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getBalanceWarning() {
        return balanceWarning;
    }

    public void setBalanceWarning(Integer balanceWarning) {
        this.balanceWarning = balanceWarning;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Integer getMsgFee() {
        return msgFee;
    }

    public void setMsgFee(Integer msgFee) {
        this.msgFee = msgFee;
    }

    public Integer getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(Integer msgNum) {
        this.msgNum = msgNum;
    }

    public Integer getMsgTotalNum() {
        return msgTotalNum;
    }

    public void setMsgTotalNum(Integer msgTotalNum) {
        this.msgTotalNum = msgTotalNum;
    }

    public String getDutyPhone() {
        return dutyPhone;
    }

    public void setDutyPhone(String dutyPhone) {
        this.dutyPhone = dutyPhone;
    }

    public Integer getMsgSwitch() {
        return msgSwitch;
    }

    public void setMsgSwitch(Integer msgSwitch) {
        this.msgSwitch = msgSwitch;
    }

    public Integer getIsToll() {
        return isToll;
    }

    public void setIsToll(Integer isToll) {
        this.isToll = isToll;
    }

    public String getSitePhone() {
        return sitePhone;
    }

    public void setSitePhone(String sitePhone) {
        this.sitePhone = sitePhone;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }
}
