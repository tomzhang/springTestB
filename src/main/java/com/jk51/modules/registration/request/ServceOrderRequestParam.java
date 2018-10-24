package com.jk51.modules.registration.request;

import com.jk51.model.order.Page;

import java.util.Date;

/**
 * Created by mqq on 2017/4/12.
 */
public class ServceOrderRequestParam  extends Page{
    private Integer servceOrdId;//预约主键信息
    private Integer siteId;//商户主键
    private String tradeId;//订单号

    private String userCateid;//分类

    private String selectType;//搜索类别

    private String selectParam;//传参内容

    private Integer Status;//状态

    private String startServiceDate;//预约开始时间

    private String endServiceDate;//预约结束时间

    private String startOrderTradeDate;//下单开始时间

    private String endOrderTradeDate;//下单结束时间


    public Integer getServceOrdId() {
        return servceOrdId;
    }

    public void setServceOrdId(Integer servceOrdId) {
        this.servceOrdId = servceOrdId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getUserCateid() {
        return userCateid;
    }

    public void setUserCateid(String userCateid) {
        this.userCateid = userCateid;
    }

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    public String getSelectParam() {
        return selectParam;
    }

    public void setSelectParam(String selectParam) {
        this.selectParam = selectParam;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getStartServiceDate() {
        return startServiceDate;
    }

    public void setStartServiceDate(String startServiceDate) {
        this.startServiceDate = startServiceDate;
    }

    public String getEndServiceDate() {
        return endServiceDate;
    }

    public void setEndServiceDate(String endServiceDate) {
        this.endServiceDate = endServiceDate;
    }

    public String getStartOrderTradeDate() {
        return startOrderTradeDate;
    }

    public void setStartOrderTradeDate(String startOrderTradeDate) {
        this.startOrderTradeDate = startOrderTradeDate;
    }

    public String getEndOrderTradeDate() {
        return endOrderTradeDate;
    }

    public void setEndOrderTradeDate(String endOrderTradeDate) {
        this.endOrderTradeDate = endOrderTradeDate;
    }
}
