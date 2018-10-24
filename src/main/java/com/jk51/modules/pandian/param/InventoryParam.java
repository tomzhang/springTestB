package com.jk51.modules.pandian.param;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-10-27
 * 修改记录:
 */
public class InventoryParam {

    private String drug_name;
    private String pandian_num;
    private String goods_code;
    private String bar_code;
    private String batch_number;
    private Integer siteId;
    private Integer storeId;

    private String mobile_type;
    private Date click_scaner_date;
    private Date send_request_date;

    private Date get_request_time;
    private Date response_time;
    private Set<String> goodsCodes = new HashSet();

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
    }

    public String getGoods_code() {
        return goods_code;
    }

    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
    }

    public String getBar_code() {
        return bar_code;
    }

    public void setBar_code(String bar_code) {
        this.bar_code = bar_code;
    }

    public String getBatch_number() {
        return batch_number;
    }

    public void setBatch_number(String batch_number) {
        this.batch_number = batch_number;
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

    public String getMobile_type() {
        return mobile_type;
    }

    public void setMobile_type(String mobile_type) {
        this.mobile_type = mobile_type;
    }

    public Date getGet_request_time() {
        return get_request_time;
    }

    public void setGet_request_time(Date get_request_time) {
        this.get_request_time = get_request_time;
    }

    public Date getResponse_time() {
        return response_time;
    }

    public void setResponse_time(Date response_time) {
        this.response_time = response_time;
    }

    public Date getClick_scaner_date() {
        return click_scaner_date;
    }

    public void setClick_scaner_date(Date click_scaner_date) {
        this.click_scaner_date = click_scaner_date;
    }

    public Date getSend_request_date() {
        return send_request_date;
    }

    public void setSend_request_date(Date send_request_date) {
        this.send_request_date = send_request_date;
    }

    public Set<String> getGoodsCodes() {
        return goodsCodes;
    }

    public void setGoodsCodes(Set<String> goodsCodes) {
        this.goodsCodes = goodsCodes;
    }
}
