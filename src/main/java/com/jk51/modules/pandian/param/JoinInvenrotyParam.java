package com.jk51.modules.pandian.param;

import javax.validation.constraints.NotNull;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/8/1
 * 修改记录:
 */
public class JoinInvenrotyParam {

    @NotNull(message="pandianNum 不能为空")
    private String pandianNum;
    @NotNull(message="siteId 不能为空")
    private Integer siteId;
    @NotNull(message="storeId 不能为空")
    private Integer storeId;

    public String getPandianNum() {
        return pandianNum;
    }

    public void setPandianNum(String pandianNum) {
        this.pandianNum = pandianNum;
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
}
