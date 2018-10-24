package com.jk51.model.order;

import java.io.Serializable;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-03-16 19:54
 * 修改记录:
 */
public class MemberListParam implements Serializable {

    private Integer pageIndex;
    private Integer pageSize;
    private String mobile;
    private String inviteCode;
    private String dateStart;
    private String dateEnd;
    private Integer siteId;
    private Integer storeId;
    private String name;//会员名
    private String text;//..

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MemberListParam() {

    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "MemberListParam{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", mobile='" + mobile + '\'' +
                ", inviteCode='" + inviteCode + '\'' +
                ", dateStart='" + dateStart + '\'' +
                ", dateEnd='" + dateEnd + '\'' +
                ", siteId=" + siteId +
                ", storeId=" + storeId +
                '}';
    }
}
