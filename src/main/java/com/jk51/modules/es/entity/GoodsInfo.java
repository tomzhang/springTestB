package com.jk51.modules.es.entity;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class GoodsInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -960369532690750831L;

	private Long goods_id;
	
	private String adverse_reactioins;

	private Long browse_number;

	private int cost_price;

	private String forpeople_desc;

	private String goods_action;

	private String goods_batch_no;

	private String goods_code;

	private String goods_contd;

	private String goods_deposit;

	private String goods_desc;

	private String goods_description;

	private String goods_indications;

	private String goods_note;

	private String goods_usage;

	private String main_ingredient;

	private String product_date;

	private String qualification;

	private int wx_purchase_way;

	private String category_path;

	private String keywords;

	private String drug_name;

	private String com_name;

	private String specif_cation;

	@JsonProperty("barnd_id")
	private Long brand_id;

	private Long drug_category;

	private Long goods_property;

	private Long goods_use;

	private Long goods_forts;

	private String goods_forpeople;

	private Long user_cateid;

	private String goods_title;

	private String goods_tagsid;

	private Long market_price;

	private Long shop_price;

	private Long discount_price;

	private Long in_stock;

	private Long goods_weight;

	private Long control_num;

	private Long goods_status;

	private Long freight_payer;

	private String list_time;

	private String delist_time;

	private Long postage_id;

	private String create_time;

	private String update_time;

	private Long detail_tpl;

	private int purchase_way;

	private int is_medicare;

	private String medicare_code;

	private Long medicare_top_price;

	private String bar_code;
	
	private int bar_code_status;

	private String mnemonic_code;

	private String goods_use_method;

	private String approval_number;

	private String goods_company;

	private Long goods_validity;

	private Long trans_mumber;

	private Long shopping_number;

	private Long goodsextd_id;

	@JsonProperty("barnd_name")
	private String brand_name;
	
	@JsonProperty("barnd_desc")
	private String brand_desc;

	private Long parent_id;

	private String cate_name;

	private Long cate_ishow;

	private Long cate_code;

	private String goods_url;

	private String def_url;
	
	private String goods_pinyin;
	
	private String goods_shouzimu;

	private String cateCode;
	
	private String isDistribute;

    private Integer goods_num;  //销量

    /*商品促销标签字段*/
    //b_coupon_rule
    private String coupon_type100;  //现金券

    private String coupon_type200;  //打折券

    private String coupon_type300;  //限价券

    //b_promotions_rule

    private String promotions_type10;   //满赠活动

    private String promotions_type20;   //打折活动

    private String promotions_type30;   //包邮活动
	
	public String getCateCode() {
		return cateCode;
	}

	public void setCateCode(String cateCode) {
		this.cateCode = cateCode;
	}

	public String getIsDistribute() {
		return isDistribute;
	}

	public void setIsDistribute(String isDistribute) {
		this.isDistribute = isDistribute;
	}

	public Long getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}

	public String getAdverse_reactioins() {
		return adverse_reactioins;
	}

	public void setAdverse_reactioins(String adverse_reactioins) {
		this.adverse_reactioins = adverse_reactioins;
	}

	public Long getBrowse_number() {
		return browse_number;
	}

	public void setBrowse_number(Long browse_number) {
		this.browse_number = browse_number;
	}

	public int getCost_price() {
		return cost_price;
	}

	public void setCost_price(int cost_price) {
		this.cost_price = cost_price;
	}

	public int getBar_code_status() {
		return bar_code_status;
	}

	public void setBar_code_status(int bar_code_status) {
		this.bar_code_status = bar_code_status;
	}

	public String getForpeople_desc() {
		return forpeople_desc;
	}

	public void setForpeople_desc(String forpeople_desc) {
		this.forpeople_desc = forpeople_desc;
	}

	public String getGoods_action() {
		return goods_action;
	}

	public void setGoods_action(String goods_action) {
		this.goods_action = goods_action;
	}

	public String getGoods_batch_no() {
		return goods_batch_no;
	}

	public void setGoods_batch_no(String goods_batch_no) {
		this.goods_batch_no = goods_batch_no;
	}

	public String getGoods_code() {
		return goods_code;
	}

	public void setGoods_code(String goods_code) {
		this.goods_code = goods_code;
	}

	public String getGoods_contd() {
		return goods_contd;
	}

	public void setGoods_contd(String goods_contd) {
		this.goods_contd = goods_contd;
	}

	public String getGoods_deposit() {
		return goods_deposit;
	}

	public void setGoods_deposit(String goods_deposit) {
		this.goods_deposit = goods_deposit;
	}

	public String getGoods_desc() {
		return goods_desc;
	}

	public void setGoods_desc(String goods_desc) {
		this.goods_desc = goods_desc;
	}

	public String getGoods_description() {
		return goods_description;
	}

	public void setGoods_description(String goods_description) {
		this.goods_description = goods_description;
	}

	public String getGoods_indications() {
		return goods_indications;
	}

	public void setGoods_indications(String goods_indications) {
		this.goods_indications = goods_indications;
	}

	public String getGoods_note() {
		return goods_note;
	}

	public void setGoods_note(String goods_note) {
		this.goods_note = goods_note;
	}

	public String getGoods_usage() {
		return goods_usage;
	}

	public void setGoods_usage(String goods_usage) {
		this.goods_usage = goods_usage;
	}

	public String getMain_ingredient() {
		return main_ingredient;
	}

	public void setMain_ingredient(String main_ingredient) {
		this.main_ingredient = main_ingredient;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public int getWx_purchase_way() {
		return wx_purchase_way;
	}

	public void setWx_purchase_way(int wx_purchase_way) {
		this.wx_purchase_way = wx_purchase_way;
	}

	public String getCategory_path() {
		return category_path;
	}

	public void setCategory_path(String category_path) {
		this.category_path = category_path;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
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

	public Long getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(Long brand_id) {
		this.brand_id = brand_id;
	}

	public Long getDrug_category() {
		return drug_category;
	}

	public void setDrug_category(Long drug_category) {
		this.drug_category = drug_category;
	}

	public Long getGoods_property() {
		return goods_property;
	}

	public void setGoods_property(Long goods_property) {
		this.goods_property = goods_property;
	}

	public Long getGoods_use() {
		return goods_use;
	}

	public void setGoods_use(Long goods_use) {
		this.goods_use = goods_use;
	}

	public Long getGoods_forts() {
		return goods_forts;
	}

	public void setGoods_forts(Long goods_forts) {
		this.goods_forts = goods_forts;
	}

	public String getGoods_forpeople() {
		return goods_forpeople;
	}

	public void setGoods_forpeople(String goods_forpeople) {
		this.goods_forpeople = goods_forpeople;
	}

	public Long getUser_cateid() {
		return Long.valueOf(this.cateCode == null || "".equals(this.cateCode) ? "0" : this.cateCode);
	}

	public void setUser_cateid(Long user_cateid) {
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

	public Long getMarket_price() {
		return market_price;
	}

	public String getProduct_date() {
		return product_date;
	}

	public void setProduct_date(String product_date) {
		this.product_date = product_date;
	}

	public String getList_time() {
		return list_time;
	}

	public void setList_time(String list_time) {
		this.list_time = list_time;
	}

	public String getDelist_time() {
		return delist_time;
	}

	public void setDelist_time(String delist_time) {
		this.delist_time = delist_time;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public void setMarket_price(Long market_price) {
		this.market_price = market_price;
	}

	public Long getShop_price() {
		return shop_price;
	}

	public void setShop_price(Long shop_price) {
		this.shop_price = shop_price;
	}

	public Long getDiscount_price() {
		return discount_price;
	}

	public void setDiscount_price(Long discount_price) {
		this.discount_price = discount_price;
	}

	public Long getIn_stock() {
		return in_stock;
	}

	public void setIn_stock(Long in_stock) {
		this.in_stock = in_stock;
	}

	public Long getGoods_weight() {
		return goods_weight;
	}

	public void setGoods_weight(Long goods_weight) {
		this.goods_weight = goods_weight;
	}

	public Long getControl_num() {
		return control_num;
	}

	public void setControl_num(Long control_num) {
		this.control_num = control_num;
	}

	public Long getGoods_status() {
		return goods_status;
	}

	public void setGoods_status(Long goods_status) {
		this.goods_status = goods_status;
	}

	public Long getFreight_payer() {
		return freight_payer;
	}

	public void setFreight_payer(Long freight_payer) {
		this.freight_payer = freight_payer;
	}

	public Long getPostage_id() {
		return postage_id;
	}

	public void setPostage_id(Long postage_id) {
		this.postage_id = postage_id;
	}

	public Long getDetail_tpl() {
		return detail_tpl;
	}

	public void setDetail_tpl(Long detail_tpl) {
		this.detail_tpl = detail_tpl;
	}

	public int getPurchase_way() {
		return purchase_way;
	}

	public void setPurchase_way(int purchase_way) {
		this.purchase_way = purchase_way;
	}

	public int getIs_medicare() {
		return is_medicare;
	}

	public void setIs_medicare(int is_medicare) {
		this.is_medicare = is_medicare;
	}

	public String getMedicare_code() {
		return medicare_code;
	}

	public void setMedicare_code(String medicare_code) {
		this.medicare_code = medicare_code;
	}

	public Long getMedicare_top_price() {
		return medicare_top_price;
	}

	public void setMedicare_top_price(Long medicare_top_price) {
		this.medicare_top_price = medicare_top_price;
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

	public String getGoods_use_method() {
		return goods_use_method;
	}

	public void setGoods_use_method(String goods_use_method) {
		this.goods_use_method = goods_use_method;
	}

	public String getApproval_number() {
		return approval_number;
	}

	public void setApproval_number(String approval_number) {
		this.approval_number = approval_number;
	}

	public String getGoods_company() {
		return goods_company;
	}

	public void setGoods_company(String goods_company) {
		this.goods_company = goods_company;
	}

	public Long getGoods_validity() {
		return goods_validity;
	}

	public void setGoods_validity(Long goods_validity) {
		this.goods_validity = goods_validity;
	}

	public Long getTrans_mumber() {
		return trans_mumber;
	}

	public void setTrans_mumber(Long trans_mumber) {
		this.trans_mumber = trans_mumber;
	}

	public Long getShopping_number() {
		return shopping_number;
	}

	public void setShopping_number(Long shopping_number) {
		this.shopping_number = shopping_number;
	}

	public Long getGoodsextd_id() {
		return goodsextd_id;
	}

	public void setGoodsextd_id(Long goodsextd_id) {
		this.goodsextd_id = goodsextd_id;
	}

	public String getBrand_name() {
		return brand_name;
	}

	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}

	public String getBrand_desc() {
		return brand_desc;
	}

	public void setBrand_desc(String brand_desc) {
		this.brand_desc = brand_desc;
	}

	public Long getParent_id() {
		return parent_id;
	}

	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}

	public String getCate_name() {
		return cate_name;
	}

	public void setCate_name(String cate_name) {
		this.cate_name = cate_name;
	}

	public Long getCate_ishow() {
		return cate_ishow;
	}

	public void setCate_ishow(Long cate_ishow) {
		this.cate_ishow = cate_ishow;
	}

	public Long getCate_code() {
		return cate_code;
	}

	public void setCate_code(Long cate_code) {
		this.cate_code = cate_code;
	}

	public String getGoods_url() {
		return goods_url;
	}

	public void setGoods_url(String goods_url) {
		this.goods_url = goods_url;
	}

	public String getDef_url() {
		return def_url;
	}

	public void setDef_url(String def_url) {
		this.def_url = def_url;
	}

	public String getGoods_pinyin() {
		return goods_pinyin;
	}

	public void setGoods_pinyin(String goods_pinyin) {
		this.goods_pinyin = goods_pinyin;
	}

	public String getGoods_shouzimu() {
		return goods_shouzimu;
	}

	public void setGoods_shouzimu(String goods_shouzimu) {
		this.goods_shouzimu = goods_shouzimu;
	}
	
}
