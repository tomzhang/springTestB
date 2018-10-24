package com.jk51.model.goods;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yeah
 * 创建日期: 2017-02-24
 * 修改记录:
 */
// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class YbGoodsSyncDraft {
    private Integer id;

    private Integer site_id;

    private Byte sync_type;

    private Integer yb_goods_id;

    private Integer detail_tpl;

    private Integer brand_id;

    private String drug_name;

    private String specif_cation;

    private String approval_number;

    private Integer info_sync_status;

    private Integer img_sync_status;

    private Date create_time;

    private Date update_time;

    private Integer goods_status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Byte getSync_type() {
        return sync_type;
    }

    public void setSync_type(Byte sync_type) {
        this.sync_type = sync_type;
    }

    public Integer getYb_goods_id() {
        return yb_goods_id;
    }

    public void setYb_goods_id(Integer yb_goods_id) {
        this.yb_goods_id = yb_goods_id;
    }

    public Integer getDetail_tpl() {
        return detail_tpl;
    }

    public void setDetail_tpl(Integer detail_tpl) {
        this.detail_tpl = detail_tpl;
    }

    public Integer getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(Integer brand_id) {
        this.brand_id = brand_id;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getSpecif_cation() {
        return specif_cation;
    }

    public void setSpecif_cation(String specif_cation) {
        this.specif_cation = specif_cation;
    }

    public String getApproval_number() {
        return approval_number;
    }

    public void setApproval_number(String approval_number) {
        this.approval_number = approval_number;
    }

    public Integer getInfo_sync_status() {
        return info_sync_status;
    }

    public void setInfo_sync_status(Integer info_sync_status) {
        this.info_sync_status = info_sync_status;
    }

    public Integer getImg_sync_status() {
        return img_sync_status;
    }

    public void setImg_sync_status(Integer img_sync_status) {
        this.img_sync_status = img_sync_status;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getGoods_status() {
        return goods_status;
    }

    public void setGoods_status(Integer goods_status) {
        this.goods_status = goods_status;
    }
}
