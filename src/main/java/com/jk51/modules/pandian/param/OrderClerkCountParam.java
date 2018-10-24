package com.jk51.modules.pandian.param;

import javax.validation.constraints.NotNull;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/8/20
 * 修改记录:
 */
public class OrderClerkCountParam {


    @NotNull(message = "pandianNum不能为空!")
    private String pandianNum;
    @NotNull(message = "planId不能为空!")
    private Integer planId;

    private Integer siteId;
    private Integer storeId;
    private Integer storeAdminId;
    private Integer isConfirm ;


    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

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

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public Integer getIsConfirm() {
        return isConfirm;
    }

    public void setIsConfirm(Integer isConfirm) {
        this.isConfirm = isConfirm;
    }


}
