package com.jk51.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:商家O2O运费规则，超过最大距离时，则使用快递
 * 作者: baixiongfei
 * 创建日期: 2017/2/21
 * 修改记录:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class O2ORule {

    private String distance;//距离，单位：公里

    private String fix_price;//运费，单位：分

    private String free_scope;//订单金额在该金额范围内时免运费

    private String index;//新加下标

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFix_price() {
        return fix_price;
    }

    public void setFix_price(String fix_price) {
        this.fix_price = fix_price;
    }

    public String getFree_scope() {
        return free_scope;
    }

    public void setFree_scope(String free_scope) {
        this.free_scope = free_scope;
    }

    @Override
    public String toString() {
        return "O2ORule{" +
                "distance='" + distance + '\'' +
                ", fix_price='" + fix_price + '\'' +
                ", free_scope='" + free_scope + '\'' +
                '}';
    }
}
