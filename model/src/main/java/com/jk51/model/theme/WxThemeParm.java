package com.jk51.model.theme;

import com.jk51.model.order.Page;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/9/15
 * 修改记录:
 */
public class WxThemeParm extends Page {

    private Integer type;   // 1: 表示只查询已发布的数据 ，其他表示全部查询
    private String title;   // 标题
    private Integer siteId; // 商家Id

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
