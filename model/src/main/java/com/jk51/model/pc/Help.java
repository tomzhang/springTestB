package com.jk51.model.pc;

import java.util.Date;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/11/15
 * 修改记录:
 */
public class Help {

    private Integer id;
    private Integer siteId;
    private String firTitle;   //一级名称
    private String secTitle;   //二级名称
    private String content;//规则
    private Integer isShow;//0显示  1不显示
    private Date createTime;
    private Date updateTime;


    private List<Help> secHelpLst; //二级帮助列表


    public List<Help> getSecHelpLst() {
        return secHelpLst;
    }

    public void setSecHelpLst(List<Help> secHelpLst) {
        this.secHelpLst = secHelpLst;
    }

    public String getFirTitle() {
        return firTitle;
    }

    public void setFirTitle(String firTitle) {
        this.firTitle = firTitle;
    }

    public String getSecTitle() {
        return secTitle;
    }

    public void setSecTitle(String secTitle) {
        this.secTitle = secTitle;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
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
