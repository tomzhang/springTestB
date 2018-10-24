package com.jk51.model.order;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-03-17
 * 修改记录:
 */
public class SCityHasStores {

    private Integer cityId;
    private String cityName;
    private List<SBStores> bStores;

    public SCityHasStores() {
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<SBStores> getbStores() {
        return bStores;
    }

    public void setbStores(List<SBStores> bStores) {
        this.bStores = bStores;
    }
}
