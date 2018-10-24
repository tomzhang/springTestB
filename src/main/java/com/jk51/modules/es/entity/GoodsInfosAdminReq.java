package com.jk51.modules.es.entity;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoodsInfosAdminReq {

	private static final Logger logger = LoggerFactory.getLogger(GoodsInfosAdminReq.class);

	private String dbname;

	private String goods_id;

	private String goods_name;

	private String goods_title;

	private String approval_number;

	private String goods_code;

	private String user_cateid;

	private Long cate_code;

	private int goods_img;

	private String goods_usage;

	private int goods_forts;

	private int goods_forpeople;

	private String goods_tagsid;

	private int goods_weight;

	private String brand_id;

	private String brand_name;

	private String minPrice;

	private String maxPrice;

	private int goods_status;//传值1、2、4， -1相当于1和2

	private int detail_tpl;

	private int wx_purchase_way;

	private String bar_code;

	private int drug_category;//110(甲类非处方药)，120(已类非处方药)，130(处方药)，140(双轨药)，150(非方剂)，160(方剂)，170(一类)，180(二类)，190(三类),-1相当于排除130(处方药)，140(双轨药)，

	private int goods_property;

	private int purchase_way;

	private int pageNum;

	private int currentPage;

	private String cateCode;

	private String isDistribute;

	private String goods_condition;    //0综合排序 1销量排序

    private String userId;

    private Integer storeId;

    private Integer storeAdminId;

    private Integer erpAreaCode;

    private Integer siteId;

	/**--------------------------------------------------------*/
	//------添加字段 mobile------start
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    //------添加字段 mobile------end

	private String barnd_name;  //品牌名称
	private String approval_vague; //批准文号
	private String drug_name; //商品名称
	private String has_image; //是否存在图片yes,no
	private String shop_price;  //商品价格区间0,999999
	private int bar_code_status=-1; //商品条形码状态;1:有条形码，0：无条形码，-1表示所有，忽略该字段
	private int pageLitems; //每页商品数量
	private int barnd_id;  //品牌id
	private int goods_use; //使用方法


	private int cate_id;  //分类id(暂时忽略)
	private String keyword; //关键词

	private String drug_name_and_approval_number; //商品名或批准文号
	private String order; //排序条件 update_time desc,goods_id DESC 销量desc

	private int get_facet;//是否带分组(暂时忽略)
	private String only_return_child_cate; //只查询子分类下的商品(暂时忽略)

	private String phone_num;

    private String forpeople_desc;  //适用人群

    private String main_ingredient; //主要成分

    private String goods_code_status; //有无商编 1 有 2 无

    private String user_cateid_status; //有无分类 1 有 2 无

    private int app_goods_status;//1 上架 2 下架 APP中根据此状态来查询

    private Integer is_medicare;//是否医保 默认0 1=非医保, 2=甲类医保, 3=乙类医保

    private int is_main_push;//主推 2不主推 1主推

    private int gross_profit;//0未知 1高毛利 2中毛利 3低毛利 4负毛利

    public int getIs_main_push() {
        return is_main_push;
    }

    public void setIs_main_push(int is_main_push) {
        this.is_main_push = is_main_push;
    }

    public int getGross_profit() {
        return gross_profit;
    }

    public void setGross_profit(int gross_profit) {
        this.gross_profit = gross_profit;
    }

    public void setIs_medicare(int is_medicare) {
        this.is_medicare = is_medicare;
    }

    private int merchant_goods_status;//商户后台搜索字段

    public int getMerchant_goods_status() {
        return merchant_goods_status;
    }

    public void setMerchant_goods_status(int merchant_goods_status) {
        this.merchant_goods_status = merchant_goods_status;
    }

    public int getApp_goods_status() {
        return app_goods_status;
    }

    public void setApp_goods_status(int app_goods_status) {
        this.app_goods_status = app_goods_status;
    }

    public String getGoods_code_status() {
        return goods_code_status;
    }

    public void setGoods_code_status(String goods_code_status) {
        this.goods_code_status = goods_code_status;
    }

    public String getUser_cateid_status() {
        return user_cateid_status;
    }

    public void setUser_cateid_status(String user_cateid_status) {
        this.user_cateid_status = user_cateid_status;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getErpAreaCode() {
        return erpAreaCode;
    }

    public void setErpAreaCode(Integer erpAreaCode) {
        this.erpAreaCode = erpAreaCode;
    }

    public String getForpeople_desc() {
        return forpeople_desc;
    }

    public void setForpeople_desc(String forpeople_desc) {
        this.forpeople_desc = forpeople_desc;
    }

    public String getMain_ingredient() {
        return main_ingredient;
    }

    public void setMain_ingredient(String main_ingredient) {
        this.main_ingredient = main_ingredient;
    }

	public String getPhone_num() {return phone_num;}

	public void setPhone_num(String phone_num) {this.phone_num = phone_num;}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

	public String getBarnd_name() {
		return barnd_name;
	}

	public void setBarnd_name(String barnd_name) {
		this.barnd_name = barnd_name;
		this.setBrand_name(barnd_name);
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getApproval_vague() {
		return approval_vague;
	}

	public void setApproval_vague(String approval_vague) {
		this.approval_vague = approval_vague;
		this.setApproval_number(approval_vague);
	}

	public String getDrug_name() {
		return drug_name;
	}

	public void setDrug_name(String drug_name) {
		this.drug_name = drug_name;
//		this.setGoods_name(drug_name);
	}

	public String getHas_image() {
		return has_image;
	}

	public void setHas_image(String has_image) {
		this.has_image = has_image;
		if(StringUtils.isNotBlank(has_image)){
			if("yes".equalsIgnoreCase(has_image)){
				this.setGoods_img(1);
			}else if("no".equalsIgnoreCase(has_image)){
				this.setGoods_img(2);
			}
		}
	}

	public String getShop_price() {
		return shop_price;
	}

	public void setShop_price(String shop_price) {
		this.shop_price = shop_price;
		if(StringUtils.isNotBlank(shop_price)){
			String[] shop_prices = shop_price.split(",");
			if(Integer.valueOf(shop_prices[0]) < Integer.valueOf(shop_prices[1])){
				this.setMinPrice(shop_prices[0]);
				this.setMaxPrice(shop_prices[1]);
			}else{
				this.setMinPrice(shop_prices[1]);
				this.setMaxPrice(shop_prices[0]);
			}
		}
	}

	public int getCate_id() {
		return cate_id;
	}

	public void setCate_id(int cate_id) {
		this.cate_id = cate_id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getBarnd_id() {
		return barnd_id;
	}

	public void setBarnd_id(int barnd_id) {
		this.barnd_id = barnd_id;
		this.setBrand_id(String.valueOf(barnd_id));
	}

	public int getGoods_use() {
		return goods_use;
	}

	public void setGoods_use(int goods_use) {
		this.goods_use = goods_use;
		this.setGoods_usage(String.valueOf(goods_use));
	}

	public String getDrug_name_and_approval_number() {
		return drug_name_and_approval_number;
	}

	public void setDrug_name_and_approval_number(String drug_name_and_approval_number) {
		this.drug_name_and_approval_number = drug_name_and_approval_number;
		this.setDrug_name(drug_name_and_approval_number);
		this.setApproval_number(drug_name_and_approval_number);
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public int getGet_facet() {
		return get_facet;
	}

	public void setGet_facet(int get_facet) {
		this.get_facet = get_facet;
	}

	public String getOnly_return_child_cate() {
		return only_return_child_cate;
	}

	public void setOnly_return_child_cate(String only_return_child_cate) {
		this.only_return_child_cate = only_return_child_cate;
	}

	public int getBar_code_status() {
		return bar_code_status;
	}

	public void setBar_code_status(int bar_code_status) {
		this.bar_code_status = bar_code_status;
	}

	public int getPageLitems() {
		return pageLitems;
	}

	public void setPageLitems(int pageLitems) {
		this.setPageNum(pageLitems);
	}

	public String getGoods_usage() {
		return goods_usage;
	}

	public void setGoods_usage(String goods_usage) {
		this.goods_usage = goods_usage;
	}

	public int getGoods_forts() {
		return goods_forts;
	}

	public void setGoods_forts(int goods_forts) {
		this.goods_forts = goods_forts;
	}

	public int getGoods_forpeople() {
		return goods_forpeople;
	}

	public void setGoods_forpeople(int goods_forpeople) {
		this.goods_forpeople = goods_forpeople;
	}

	public String getGoods_tagsid() {
		return goods_tagsid;
	}

	public void setGoods_tagsid(String goods_tagsid) {
		this.goods_tagsid = goods_tagsid;
	}

	public int getGoods_weight() {
		return goods_weight;
	}

	public void setGoods_weight(int goods_weight) {
		this.goods_weight = goods_weight;
	}

	public String getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(String brand_id) {
		this.brand_id = brand_id;
	}

	public String getUser_cateid() {
		return user_cateid;
	}

	public void setUser_cateid(String user_cateid) {
		this.user_cateid = user_cateid;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getGoods_title() {
		return goods_title;
	}

	public void setGoods_title(String goods_title) {
		this.goods_title = goods_title;
	}

	public String getApproval_number() {
		return approval_number;
	}

	public void setApproval_number(String approval_number) {
		this.approval_number = approval_number;
	}

	public String getGoods_code() {
		return goods_code;
	}

	public void setGoods_code(String goods_code) {
		this.goods_code = goods_code;
	}

	public Long getCate_code() {
		return cate_code;
	}

	public void setCate_code(Long cate_code) {
		this.cate_code = cate_code;
	}

	public int getGoods_img() {
		return goods_img;
	}

	public void setGoods_img(int goods_img) {
		this.goods_img = goods_img;
	}

	public String getBrand_name() {
		return brand_name;
	}

	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}

	public String getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(String minPrice) {
		this.minPrice = minPrice;
	}

	public String getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(String maxPrice) {
		this.maxPrice = maxPrice;
	}

	public int getGoods_status() {
		return goods_status;
	}

	public void setGoods_status(int goods_status) {
		this.goods_status = goods_status;
	}

	public int getDetail_tpl() {
		return detail_tpl;
	}

	public void setDetail_tpl(int detail_tpl) {
		this.detail_tpl = detail_tpl;
	}

	public int getWx_purchase_way() {
		return wx_purchase_way;
	}

	public void setWx_purchase_way(int wx_purchase_way) {
		this.wx_purchase_way = wx_purchase_way;
	}

	public String getBar_code() {
		return bar_code;
	}

	public void setBar_code(String bar_code) {
		this.bar_code = bar_code;
	}

	public int getDrug_category() {
		return drug_category;
	}

	public void setDrug_category(int drug_category) {
		this.drug_category = drug_category;
	}

	public int getGoods_property() {
		return goods_property;
	}

	public void setGoods_property(int goods_property) {
		this.goods_property = goods_property;
	}

	public int getPurchase_way() {
		return purchase_way;
	}

	public void setPurchase_way(int purchase_way) {
		this.purchase_way = purchase_way;
	}

	public int getPageNum() {
		return pageNum = pageNum <= 0 ? 10 : pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum <= 0 ? 10 : pageNum;
	}

	public int getCurrentPage() {
		return currentPage = currentPage <= 0 ? 1 : currentPage;
	}

	public String getGoods_condition() {
		return goods_condition;
	}

	public void setGoods_condition(String goods_condition) {
		this.goods_condition = goods_condition;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage <= 0 ? 1 : currentPage;
	}
	public boolean isDBFail(){

		if(StringUtils.isNotBlank(this.getDbname())){
			return false;
		}
		return true;
	}

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
	public String toString() {
		return "GoodsInfosAdminReq [dbname=" + dbname + ", goods_id=" + goods_id + ", goods_name=" + goods_name
				+ ", goods_title=" + goods_title + ", approval_number=" + approval_number + ", goods_code=" + goods_code
				+ ", user_cateid=" + user_cateid + ", cate_code=" + cate_code + ", goods_img=" + goods_img
				+ ", goods_usage=" + goods_usage + ", goods_forts=" + goods_forts + ", goods_forpeople="
				+ goods_forpeople + ", goods_tagsid=" + goods_tagsid + ", goods_weight=" + goods_weight + ", brand_id="
				+ brand_id + ", brand_name=" + brand_name + ", minPrice=" + minPrice + ", maxPrice=" + maxPrice
				+ ", goods_status=" + goods_status + ", detail_tpl=" + detail_tpl + ", wx_purchase_way="
				+ wx_purchase_way + ", bar_code=" + bar_code + ", drug_category=" + drug_category + ", goods_property="
				+ goods_property + ", purchase_way=" + purchase_way + ", pageNum=" + pageNum + ", currentPage="
				+ currentPage + ", barnd_name=" + barnd_name + ", approval_vague=" + approval_vague + ", drug_name="
				+ drug_name + ", has_image=" + has_image + ", shop_price=" + shop_price + ", bar_code_status="
				+ bar_code_status + ", pageLitems=" + pageLitems + ", barnd_id=" + barnd_id + ", goods_use=" + goods_use
				+ ", cate_id=" + cate_id + ", goods_code_status=" + goods_code_status + ", user_cateid_status=" + user_cateid_status + ", keyword=" + keyword + ", drug_name_and_approval_number="
				+ drug_name_and_approval_number + ", order=" + order + ", get_facet=" + get_facet
				+ ", only_return_child_cate=" + only_return_child_cate + ", goods_condition="+goods_condition+","+forpeople_desc+","+main_ingredient+", app_goods_status = " + app_goods_status+", merchant_goods_status = " + merchant_goods_status+", is_medicare = "
                + is_medicare+", is_main_Push=" + is_main_push + ", gross_profit=" + gross_profit + "]";
	}
}
