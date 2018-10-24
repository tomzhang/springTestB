package com.jk51.model.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 高德返回信息
 * 作者: chen_pt
 * 创建日期: 2017/10/11
 * 修改记录:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeoJson {

    private String status;  //0：请求失败；1：请求成功

    private String info;    //status为0时，info返回错误原因，否则返回“OK”

    List<Pois> pois;        //搜索POI信息列表


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Pois> getPois() {
        return pois;
    }

    public void setPois(List<Pois> pois) {
        this.pois = pois;
    }
}
