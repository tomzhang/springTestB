package com.jk51.modules.pandian.param;

import javax.validation.constraints.NotNull;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-01
 * 修改记录:
 */
public class RepeatInventoryParam {

    @NotNull(message = "pandian_num不能为空")
    private String pandian_num;
    @NotNull(message = "storeAdminId不能为空")
    private Integer storeAdminId;

    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
    }

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }
}
