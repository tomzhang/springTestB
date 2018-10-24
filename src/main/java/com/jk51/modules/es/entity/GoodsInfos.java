package com.jk51.modules.es.entity;

import com.jk51.modules.promotions.request.LabelParam;

import java.io.Serializable;
import java.util.List;

public class GoodsInfos implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7540417995482066539L;

	private int goods_id;

	private String goods_code;
	
	private String goods_title;

	private String goods_company;

	private String drug_name;

	private String com_name;

	private int market_price;

	private int shop_price;

	private int in_stock;

	private String specif_cation;
	
	private int goods_status;

	private String update_time;

	private String bar_code;

	private String approval_number;

	private String brand_name;

	private int cate_code;

	private String def_url;
	
	private int purchase_way;
	
	private String wx_purchase_way;
	
	private String user_cateid;

    /*商品促销标签字段*/
    //b_coupon_rule //b_promotions_rule
//    private LabelParam coupon_types;  //现金券,打折券,限价券 //满赠活动,打折活动,包邮活动

    private Object coupon_types;  //现金券,打折券,限价券 //满赠活动,打折活动,包邮活动

    private Long drug_category;    //药品分类

    private Long goods_num; //销量

    private Boolean isNeedAuth; //优惠标签是否需要登录显示更多
    private Integer promotions_price; //特殊显示活动价格
    private Integer promotions_type; //特殊显示活动类型
    private Object tags_titles;

    /**
     * APP搜索中 discount_price为空则取goods_price, 都为空则取shop_price
     */
    private Integer goods_price;

    private int control_num; //限购数量 0不限购

    private int detail_tpl; //模版类型

    private  int app_goods_status;//单个商品状态

    private int is_medicare; //是否医保

    public int getIs_medicare() {
        return is_medicare;
    }

    public void setIs_medicare(int is_medicare) {
        this.is_medicare = is_medicare;
    }

    public int getApp_goods_status() {
        return app_goods_status;
    }

    public void setApp_goods_status(int app_goods_status) {
        this.app_goods_status = app_goods_status;
    }

    public int getControl_num() {
        return control_num;
    }

    public void setControl_num(int control_num) {
        this.control_num = control_num;
    }

    public int getDetail_tpl() {
        return detail_tpl;
    }

    public void setDetail_tpl(int detail_tpl) {
        this.detail_tpl = detail_tpl;
    }

    public Integer getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(Integer goods_price) {
        this.goods_price = goods_price;
    }

    public Integer getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(Integer discount_price) {
        this.discount_price = discount_price;
    }

    private Integer discount_price;

    public Object getCoupon_types() {
        return coupon_types;
    }

    public void setCoupon_types(Object coupon_types) {
        this.coupon_types = coupon_types;
    }

    public Object getTags_titles() {
        return tags_titles;
    }

    public void setTags_titles(Object tags_titles) {
        this.tags_titles = tags_titles;
    }

    public Long getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(Long goods_num) {
        this.goods_num = goods_num;
    }

    public Long getDrug_category() {
        return drug_category;
    }

    public void setDrug_category(Long drug_category) {
        this.drug_category = drug_category;
    }

	public String getUser_cateid() {
		return user_cateid;
	}

	public void setUser_cateid(String user_cateid) {
		this.user_cateid = user_cateid;
	}

	public int getPurchase_way() {
		return purchase_way;
	}

	public void setPurchase_way(int purchase_way) {
		this.purchase_way = purchase_way;
	}

	public String getWx_purchase_way() {
		return wx_purchase_way;
	}

	public void setWx_purchase_way(String wx_purchase_way) {
		this.wx_purchase_way = wx_purchase_way;
	}

	public int getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_code() {
		return goods_code;
	}

	public void setGoods_code(String goods_code) {
		this.goods_code = goods_code;
	}

	public String getGoods_title() {
		return goods_title;
	}

	public void setGoods_title(String goods_title) {
		this.goods_title = goods_title;
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

	public int getMarket_price() {
		return market_price;
	}

	public void setMarket_price(int market_price) {
		this.market_price = market_price;
	}

	public int getShop_price() {
		return shop_price;
	}

	public void setShop_price(int shop_price) {
		this.shop_price = shop_price;
	}

	public int getIn_stock() {
		return in_stock;
	}

	public void setIn_stock(int in_stock) {
		this.in_stock = in_stock;
	}

	public String getSpecif_cation() {
		return specif_cation;
	}

	public void setSpecif_cation(String specif_cation) {
		this.specif_cation = specif_cation;
	}

	public int getGoods_status() {
		return goods_status;
	}

	public void setGoods_status(int goods_status) {
		this.goods_status = goods_status;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getBar_code() {
		return bar_code;
	}

	public void setBar_code(String bar_code) {
		this.bar_code = bar_code;
	}

	public String getApproval_number() {
		return approval_number;
	}

	public void setApproval_number(String approval_number) {
		this.approval_number = approval_number;
	}

	public String getBrand_name() {
		return brand_name;
	}

	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}

	public int getCate_code() {
		return cate_code;
	}

	public void setCate_code(int cate_code) {
		this.cate_code = cate_code;
	}

	public String getDef_url() {
		return def_url;
	}

	public void setDef_url(String def_url) {
		this.def_url = def_url;
	}

	public String getGoods_company() {
		return goods_company;
	}

	public void setGoods_company(String goods_company) {
		this.goods_company = goods_company;
	}

    public Boolean getIsNeedAuth() {
        return isNeedAuth;
    }

    public void setNeedAuth(Boolean needAuth) {
        isNeedAuth = needAuth;
    }

    public Integer getPromotions_price() {
        return promotions_price;
    }

    public void setPromotions_price(Integer promotions_price) {
        this.promotions_price = promotions_price;
    }

    public Integer getPromotions_type() {
        return promotions_type;
    }

    public void setPromotions_type(Integer promotions_type) {
        this.promotions_type = promotions_type;
    }
}
