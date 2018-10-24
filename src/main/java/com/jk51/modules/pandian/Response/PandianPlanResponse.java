package com.jk51.modules.pandian.Response;


import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-01
 * 修改记录:
 */
public class PandianPlanResponse {

    private String description;
    private String type;
    private String pandian_num;
    private Integer planType;   //盘点单创建时的类型
    private Long waitNum;
    private Long repeatNum;
    private Integer clerckNum;
    private Date createTime;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
    }

    public Integer getPlanType() {
        return planType;
    }

    public void setPlanType(Integer planType) {
        this.planType = planType;
    }

    public Long getWaitNum() {
        return waitNum;
    }

    public void setWaitNum(Long waitNum) {
        this.waitNum = waitNum;
    }

    public Long getRepeatNum() {
        return repeatNum;
    }

    public void setRepeatNum(Long repeatNum) {
        this.repeatNum = repeatNum;
    }

    public Integer getClerckNum() {
        return clerckNum;
    }

    public void setClerckNum(Integer clerckNum) {
        this.clerckNum = clerckNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
