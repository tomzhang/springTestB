package com.jk51.model.goods;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

//import org.hibernate.validator.constraints.Length;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yeah
 * 创建日期: 2017-02-20
 * 修改记录:
 */
public class YbGoodsGrid implements Serializable {
    //    @NotNull
    String drug_name;
    //    @Length(min=4, max=8)
    String approval_number;
    Integer id;
    String specif_cation;
    String specif_code;
    String brand_name;
    Date start_date;
    Date end_date;
    String goods_company;
    String cate_id;
    Boolean has_image;
    Boolean udateimg_status;
    Boolean hasExtdFields;
    Integer detail_tpl;
    Integer goods_status;
    Boolean update_status;
    String renewable;

    //商品状态(上架1、下架2、所有-1)
    private Integer status;

    List cats;

    Integer siteId;


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getApproval_number() {
        return approval_number;
    }

    public void setApproval_number(String approval_number) {
        this.approval_number = approval_number;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSpecif_cation() {
        return specif_cation;
    }

    public void setSpecif_cation(String specif_cation) {
        this.specif_cation = specif_cation;
    }

    public String getSpecif_code() {
        return specif_code;
    }

    public void setSpecif_code(String specif_code) {
        this.specif_code = specif_code;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public String getGoods_company() {
        return goods_company;
    }

    public void setGoods_company(String goods_company) {
        this.goods_company = goods_company;
    }

    public String getCate_id() {
        return cate_id;
    }

    public void setCate_id(String cate_id) {
        this.cate_id = cate_id;
    }

    public Boolean getHas_image() {
        return has_image;
    }

    public void setHas_image(Boolean has_image) {
        this.has_image = has_image;
    }

    public Boolean getUdateimg_status() {
        return udateimg_status;
    }

    public void setUdateimg_status(Boolean udateimg_status) {
        this.udateimg_status = udateimg_status;
    }

    public Integer getDetail_tpl() {
        return detail_tpl;
    }

    public void setDetail_tpl(Integer detail_tpl) {
        this.detail_tpl = detail_tpl;
    }

    public Integer getGoods_status() {
        return goods_status;
    }

    public void setGoods_status(Integer goods_status) {
        this.goods_status = goods_status;
    }

    public Boolean getUpdate_status() {
        return update_status;
    }

    public void setUpdate_status(Boolean update_status) {
        this.update_status = update_status;
    }

    public Boolean getHasExtdFields() {
        return hasExtdFields;
    }

    public void setHasExtdFields(Boolean hasExtdFields) {
        this.hasExtdFields = hasExtdFields;
    }

    public String getRenewable() {
        return renewable;
    }

    public void setRenewable(String renewable) {
        this.renewable = renewable;
    }

    public List getCats() {
        return cats;
    }

    public void setCats(List cats) {
        this.cats = cats;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public String toString() {
        return "YbGoodsGrid{" +
                "drug_name='" + drug_name + '\'' +
                ", approval_number='" + approval_number + '\'' +
                ", id=" + id +
                ", specif_cation='" + specif_cation + '\'' +
                ", specif_code='" + specif_code + '\'' +
                ", brand_name='" + brand_name + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", goods_company='" + goods_company + '\'' +
                ", cate_id='" + cate_id + '\'' +
                ", has_image=" + has_image +
                ", udateimg_status=" + udateimg_status +
                ", hasExtdFields=" + hasExtdFields +
                ", detail_tpl=" + detail_tpl +
                ", goods_status=" + goods_status +
                ", update_status=" + update_status +
                ", renewable='" + renewable + '\'' +
                ", status=" + status +
                ", cats=" + cats +
                '}';
    }
}
