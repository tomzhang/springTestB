package com.jk51.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static org.springframework.data.elasticsearch.annotations.FieldType.String;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-10-18
 * 修改记录:
 */
@Document(indexName = "inventories",type ="inventory" )
public class Inventories implements Serializable {

    private static final long serialVersionUID = -7540417995482066539L;

    @Id
    private Integer id;
    private Integer plan_id;

    @Field(type = String,index = FieldIndex.not_analyzed )
    private String pandian_num;
    private Integer store_id;
    //private String good_type; //药品类型

    @Field(type = String,index = FieldIndex.not_analyzed )
    private String goods_code; //'商品编号，b_goods.goods_code'
    private String drug_name; //商品名称

    private String drug_name_py; //商品名称
    //private String approval_number; //批准文号
    private String specif_cation;   //药品规格
    private String goods_company;  //'生产厂家'
    private Double inventory_accounting;  //账目库存
    private Double actual_store; //实际库存
    private Integer inventory_checker; //盘点人storeAdminId
    private String checkerName;  //盘点人名字

    @Field(
        type = FieldType.Date,
        index = FieldIndex.not_analyzed
    )
    private Date create_time;

    @Field(
        type = FieldType.Date,
        index = FieldIndex.not_analyzed
    )
    private Date update_time;
    private String batch_number;
    private String quality;
    private Integer site_id;
    private String hash;
    private Integer inventory_confirm = 0; //盘点确认
    private Integer plan_stock_show;
    private Integer order_id = 0;
    private Integer isDel = 0;


    @Field(type = String,index = FieldIndex.not_analyzed )
    private String store_num; //门店编号
    private Integer modify = 0;
    private Integer repeat = 0;

    private Integer esId;
    private Integer erpDataSeq;
    private BigDecimal score;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(Integer plan_id) {
        this.plan_id = plan_id;
    }

    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public String getGoods_code() {
        return goods_code;
    }

    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
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

    public String getGoods_company() {
        return goods_company;
    }

    public void setGoods_company(String goods_company) {
        this.goods_company = goods_company;
    }

    public Double getInventory_accounting() {
        return inventory_accounting;
    }

    public void setInventory_accounting(Double inventory_accounting) {
        this.inventory_accounting = inventory_accounting;
    }

    public Double getActual_store() {
        return actual_store;
    }

    public void setActual_store(Double actual_store) {
        this.actual_store = actual_store;
    }

    public Integer getInventory_checker() {
        return inventory_checker;
    }

    public void setInventory_checker(Integer inventory_checker) {
        this.inventory_checker = inventory_checker;
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

    public String getBatch_number() {
        return batch_number;
    }

    public void setBatch_number(String batch_number) {
        this.batch_number = batch_number;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public String getCheckerName() {
        return checkerName;
    }

    public void setCheckerName(String checkerName) {
        this.checkerName = checkerName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getInventory_confirm() {
        return inventory_confirm;
    }

    public void setInventory_confirm(Integer inventory_confirm) {
        this.inventory_confirm = inventory_confirm;
    }

    public Integer getPlan_stock_show() {
        return plan_stock_show;
    }

    public void setPlan_stock_show(Integer plan_stock_show) {
        this.plan_stock_show = plan_stock_show;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public String getStore_num() {
        return store_num;
    }

    public void setStore_num(String store_num) {
        this.store_num = store_num;
    }

    public Integer getModify() {
        return modify;
    }

    public void setModify(Integer modify) {
        this.modify = modify;
    }

    public java.lang.Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(java.lang.Integer isDel) {
        this.isDel = isDel;
    }

    public Integer getEsId() {
        return esId;
    }

    public void setEsId(Integer esId) {
        this.esId = esId;
    }


    public Integer getErpDataSeq() {
        return erpDataSeq;
    }

    public void setErpDataSeq(Integer erpDataSeq) {
        this.erpDataSeq = erpDataSeq;
    }

    public java.lang.String getDrug_name_py() {
        return drug_name_py;
    }

    public void setDrug_name_py(java.lang.String drug_name_py) {
        this.drug_name_py = drug_name_py;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Integer getRepeat() {
        return repeat;
    }

    public void setRepeat(Integer repeat) {
        this.repeat = repeat;
    }

    @Override

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inventories that = (Inventories) o;

        if (plan_id != null ? !plan_id.equals(that.plan_id) : that.plan_id != null) return false;
        if (store_id != null ? !store_id.equals(that.store_id) : that.store_id != null) return false;
        if (goods_code != null ? !goods_code.equals(that.goods_code) : that.goods_code != null) return false;
        if (drug_name != null ? !drug_name.equals(that.drug_name) : that.drug_name != null) return false;
        return site_id != null ? site_id.equals(that.site_id) : that.site_id == null;
    }

    @Override
    public int hashCode() {
        int result = plan_id != null ? plan_id.hashCode() : 0;
        result = 31 * result + (store_id != null ? store_id.hashCode() : 0);
        result = 31 * result + (goods_code != null ? goods_code.hashCode() : 0);
        result = 31 * result + (drug_name != null ? drug_name.hashCode() : 0);
        result = 31 * result + (site_id != null ? site_id.hashCode() : 0);
        return result;
    }
}
