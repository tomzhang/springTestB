package com.jk51.model.treat;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 自动回复
 * 作者: hulan
 * 创建日期: 2017-03-06
 * 修改记录:
 */
public class AutoReply {
    private Integer siteId;
    private Integer id;
    private Integer replyType;
    private String ruleName;
    private String keywords;
    private String replyContent;
    private Integer delTag;
    private Timestamp createTime;
    private Timestamp updateTime;

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

    public Integer getReplyType() {
        return replyType;
    }

    public void setReplyType(Integer replyType) {
        this.replyType = replyType;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public Integer getDelTag() {
        return delTag;
    }

    public void setDelTag(Integer delTag) {
        this.delTag = delTag;
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

    @Override
    public String toString() {
        return "AutoReply{" +
                "siteId=" + siteId +
                ", id=" + id +
                ", replyType=" + replyType +
                ", ruleName='" + ruleName + '\'' +
                ", keywords='" + keywords + '\'' +
                ", replyContent='" + replyContent + '\'' +
                ", delTag=" + delTag +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
