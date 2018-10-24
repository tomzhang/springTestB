package com.jk51.model;

import com.jk51.model.order.Store;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-03-17
 * 修改记录:
 */
public class CityHasStores {

    private Integer cityId;
    private String cityName;
    private List<Store> bStores;

    public CityHasStores() {
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

    public List<Store> getbStores() {
        return bStores;
    }

    public void setbStores(List<Store> bStores) {
        this.bStores = bStores;
    }
}
