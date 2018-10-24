package com.jk51.model.merchant;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 会员卡设置
 * 作者: chen_pt
 * 创建日期: 2017/11/2
 * 修改记录:
 */
public class MemberCardSet {

    private Integer siteId;

    private Integer id;

    private String bgImg;

    private String title;

    private String mkWords;

    private Date createTime;

    private Date updateTime;


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

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMkWords() {
        return mkWords;
    }

    public void setMkWords(String mkWords) {
        this.mkWords = mkWords;
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
