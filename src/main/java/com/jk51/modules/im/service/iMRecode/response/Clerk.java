package com.jk51.modules.im.service.iMRecode.response;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-06-23
 * 修改记录:
 */
public class Clerk {


    private Integer storeAdminId;

    //由店员名称加上店员手机号码
    private String name;

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
