package com.jk51.model.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SGoods implements Serializable {

    private Integer goods_id;
    private Integer drug_category;
    private Integer goods_property;
    private Integer goods_forts;
    private Integer goods_validity;
    private Integer in_stock;
    private Integer goods_weight;
    private Integer control_num;
    private Integer goods_status;
    private Integer freight_payer;
    private Integer postage_id;
    private Integer detail_tpl;
    private Integer purchase_way;
    private Integer yb_goods_id;
    private Boolean is_medicare;
    private Double market_price;
    private Double shop_price;
    private Double cost_price;
    private Double discount_price;
    private Double medicare_top_price;
    private String approval_number;
    private String drug_name;
    private String com_name;
    private String specif_cation;
    private String goods_company;
    private String barnd_id;
    private String goods_use;
    private String goods_forpeople;
    private String user_cateid;
    private String goods_title;
    private String goods_tagsid;
    private String medicare_code;
    private String bar_code;
    private String mnemonic_code;
    private String goods_code;
    private Timestamp list_time;
    private Timestamp delist_time;
    private Timestamp create_time;
    private Timestamp update_time;

    public Integer getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(Integer goods_id) {
        this.goods_id = goods_id;
    }

    public Integer getDrug_category() {
        return drug_category;
    }

    public void setDrug_category(Integer drug_category) {
        this.drug_category = drug_category;
    }

    public Integer getGoods_property() {
        return goods_property;
    }

    public void setGoods_property(Integer goods_property) {
        this.goods_property = goods_property;
    }

    public Integer getGoods_forts() {
        return goods_forts;
    }

    public void setGoods_forts(Integer goods_forts) {
        this.goods_forts = goods_forts;
    }

    public Integer getGoods_validity() {
        return goods_validity;
    }

    public void setGoods_validity(Integer goods_validity) {
        this.goods_validity = goods_validity;
    }

    public Integer getIn_stock() {
        return in_stock;
    }

    public void setIn_stock(Integer in_stock) {
        this.in_stock = in_stock;
    }

    public Integer getGoods_weight() {
        return goods_weight;
    }

    public void setGoods_weight(Integer goods_weight) {
        this.goods_weight = goods_weight;
    }

    public Integer getControl_num() {
        return control_num;
    }

    public void setControl_num(Integer control_num) {
        this.control_num = control_num;
    }

    public Integer getGoods_status() {
        return goods_status;
    }

    public void setGoods_status(Integer goods_status) {
        this.goods_status = goods_status;
    }

    public Integer getFreight_payer() {
        return freight_payer;
    }

    public void setFreight_payer(Integer freight_payer) {
        this.freight_payer = freight_payer;
    }

    public Integer getPostage_id() {
        return postage_id;
    }

    public void setPostage_id(Integer postage_id) {
        this.postage_id = postage_id;
    }

    public Integer getDetail_tpl() {
        return detail_tpl;
    }

    public void setDetail_tpl(Integer detail_tpl) {
        this.detail_tpl = detail_tpl;
    }

    public Integer getPurchase_way() {
        return purchase_way;
    }

    public void setPurchase_way(Integer purchase_way) {
        this.purchase_way = purchase_way;
    }

    public Integer getYb_goods_id() {
        return yb_goods_id;
    }

    public void setYb_goods_id(Integer yb_goods_id) {
        this.yb_goods_id = yb_goods_id;
    }

    public Boolean getIs_medicare() {
        return is_medicare;
    }

    public void setIs_medicare(Boolean is_medicare) {
        this.is_medicare = is_medicare;
    }

    public Double getMarket_price() {
        return market_price;
    }

    public void setMarket_price(Double market_price) {
        this.market_price = market_price;
    }

    public Double getShop_price() {
        return shop_price;
    }

    public void setShop_price(Double shop_price) {
        this.shop_price = shop_price;
    }

    public Double getCost_price() {
        return cost_price;
    }

    public void setCost_price(Double cost_price) {
        this.cost_price = cost_price;
    }

    public Double getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(Double discount_price) {
        this.discount_price = discount_price;
    }

    public Double getMedicare_top_price() {
        return medicare_top_price;
    }

    public void setMedicare_top_price(Double medicare_top_price) {
        this.medicare_top_price = medicare_top_price;
    }

    public String getApproval_number() {
        return approval_number;
    }

    public void setApproval_number(String approval_number) {
        this.approval_number = approval_number;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getCom_name() {
        return com_name;
    }

    public void setCom_name(String com_name) {
        this.com_name = com_name;
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

    public String getBarnd_id() {
        return barnd_id;
    }

    public void setBarnd_id(String barnd_id) {
        this.barnd_id = barnd_id;
    }

    public String getGoods_use() {
        return goods_use;
    }

    public void setGoods_use(String goods_use) {
        this.goods_use = goods_use;
    }

    public String getGoods_forpeople() {
        return goods_forpeople;
    }

    public void setGoods_forpeople(String goods_forpeople) {
        this.goods_forpeople = goods_forpeople;
    }

    public String getUser_cateid() {
        return user_cateid;
    }

    public void setUser_cateid(String user_cateid) {
        this.user_cateid = user_cateid;
    }

    public String getGoods_title() {
        return goods_title;
    }

    public void setGoods_title(String goods_title) {
        this.goods_title = goods_title;
    }

    public String getGoods_tagsid() {
        return goods_tagsid;
    }

    public void setGoods_tagsid(String goods_tagsid) {
        this.goods_tagsid = goods_tagsid;
    }

    public String getMedicare_code() {
        return medicare_code;
    }

    public void setMedicare_code(String medicare_code) {
        this.medicare_code = medicare_code;
    }

    public String getBar_code() {
        return bar_code;
    }

    public void setBar_code(String bar_code) {
        this.bar_code = bar_code;
    }

    public String getMnemonic_code() {
        return mnemonic_code;
    }

    public void setMnemonic_code(String mnemonic_code) {
        this.mnemonic_code = mnemonic_code;
    }

    public String getGoods_code() {
        return goods_code;
    }

    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
    }

    public Timestamp getList_time() {
        return list_time;
    }

    public void setList_time(Timestamp list_time) {
        this.list_time = list_time;
    }

    public Timestamp getDelist_time() {
        return delist_time;
    }

    public void setDelist_time(Timestamp delist_time) {
        this.delist_time = delist_time;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }
}
