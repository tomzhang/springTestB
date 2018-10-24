package com.jk51.modules.member.request;

import com.jk51.model.order.Page;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/1/27
 * 修改记录:
 */
public class IdentityApproveParm extends Page {
    private Integer siteId;
    private String mobile;
    private String name;
    private String idcardNumber;
    private Integer type;//0只能审核   1人工审核  2手动调整
    private Integer status;//-1 未认证 0审核中  1已审核  2审核失败
    private String begainTime;//审核开始时间
    private String endTime;//审核结束时间

    private String bFrom;//判断从哪个页面跳进来的

    public String getbFrom() {
        return bFrom;
    }

    public void setbFrom(String bFrom) {
        this.bFrom = bFrom;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getIdcardNumber() {
        return idcardNumber;
    }

    public void setIdcardNumber(String idcardNumber) {
        this.idcardNumber = idcardNumber;
    }
}
