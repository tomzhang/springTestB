package com.jk51.modules.marketing.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jk51.model.order.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/3/19
 * 修改记录:
 */
public class MarketingMemberParm extends Page {

    private Integer siteId;
    private String mobile;
    private String name;
    private Integer type;
    private String begainTime;
    private String endTime;
    private Integer status;
    private String type_info;


    public String getType_info() {
        return type_info;
    }

    public void setType_info(String type_info) {
        this.type_info = type_info;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBegainTime() {
        return begainTime;
    }

    public void setBegainTime(String begainTime) {
        this.begainTime = begainTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
