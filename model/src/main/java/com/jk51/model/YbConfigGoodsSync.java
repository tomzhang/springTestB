package com.jk51.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;
import java.util.Date;

// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 驼峰命名
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class YbConfigGoodsSync implements Serializable {
    /**
     * 自增ID
     */
    private Integer id;

    /**
     * 商品录入模板标识：10（药品类模板），20（其他类模板），30（器械类模板），40（保健品模板），50（2.0版本废弃），60（化妆品模板），70 (中药材模板) ，80 (消毒类模板)
     */
    private Integer detailTpl;

    /**
     * 是否允许新增 0=拒绝新增 1=允许新增
     */
    private Boolean allowAdd;

    /**
     * 是否允许更新 0=禁止更新 1=允许更新
     */
    private Boolean allowUpdate;

    /**
     * 允许更新的字段主键 多个用逗号隔开如:approval_number,specif_cation
     */
    private String fieldsPk;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否更新字段列表多个用逗号隔开如:approval_number,specif_cation,goods_company,drug_category
     */
    private String fields;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDetailTpl() {
        return detailTpl;
    }

    public void setDetailTpl(Integer detailTpl) {
        this.detailTpl = detailTpl;
    }

    public Boolean getAllowAdd() {
        return allowAdd;
    }

    public void setAllowAdd(Boolean allowAdd) {
        this.allowAdd = allowAdd;
    }

    public Boolean getAllowUpdate() {
        return allowUpdate;
    }

    public void setAllowUpdate(Boolean allowUpdate) {
        this.allowUpdate = allowUpdate;
    }

    public String getFieldsPk() {
        return fieldsPk;
    }

    public void setFieldsPk(String fieldsPk) {
        this.fieldsPk = fieldsPk;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
}