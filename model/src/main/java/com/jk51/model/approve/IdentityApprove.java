package com.jk51.model.approve;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 身份认证实体类
 * 作者: chen_pt
 * 创建日期: 2018/1/19
 * 修改记录:
 */
public class IdentityApprove {

    private Integer id;
    private Integer siteId;
    private Integer memberId;
    private String name;
    private String idcardNumber;//身份证号
    private String frontImg;    //正面照
    private String reverseImg;  //反面照
    private Integer status;     //审核状态（0审核中  1已通过  2未通过）
    private String reason;      //审核说明
    private Date createTime;
    private Date updateTime;
    private Integer type;       //0智能审核   1人工审核

    private String mobile;
    private String avatar;
    private Integer reMemberId;//用户表主键

    public Integer getReMemberId() {
        return reMemberId;
    }

    public void setReMemberId(Integer reMemberId) {
        this.reMemberId = reMemberId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
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

    public String getFrontImg() {
        return frontImg;
    }

    public void setFrontImg(String frontImg) {
        this.frontImg = frontImg;
    }

    public String getReverseImg() {
        return reverseImg;
    }

    public void setReverseImg(String reverseImg) {
        this.reverseImg = reverseImg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
}
