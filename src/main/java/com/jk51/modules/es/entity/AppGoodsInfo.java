package com.jk51.modules.es.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName AppGoodsInfo
 * @Description 商品详情实体
 * @Date 2018-06-25 9:31
 */
public class AppGoodsInfo implements Serializable {


    private static final long serialVersionUID = 6210958186802140800L;


    private int goods_id;

    private String goods_code;

    private String goods_title;

    private String goods_company;

    private String drug_name;

    private String com_name;

    private int market_price;

    private int shop_price;

    private int in_stock; //库存

    private String specif_cation;

    private int goods_status;

    //private String updateTime;

    private String bar_code;

    private String approval_number;

    private String brand_name;

    private int cate_code;

    private String def_url;

    //private int purchaseWay;

    //private String wxPurchaseWay;

    private String user_cateid;

    /*商品促销标签字段*/
    //b_coupon_rule //b_promotions_rule
//    private LabelParam coupon_types;  //现金券,打折券,限价券 //满赠活动,打折活动,包邮活动

    private Object coupon_types;  //现金券,打折券,限价券 //满赠活动,打折活动,包邮活动

    private Long drug_category;    //药品分类

    private Long goods_num; //销量

    private int store_id;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
    //private Boolean isNeedAuth; //优惠标签是否需要登录显示更多
    //private Integer promotionsPrice; //特殊显示活动价格
    //private Integer promotionsType; //特殊显示活动类型
//    private Object tags_titles;

    /**
     * APP搜索中 discount_price为空则取goods_price, 都为空则取shop_price
     */
    private Integer goods_price;

    private Integer discount_price;

    private Integer have_task_plan; //是否有任务进行中 0：无,1：有

    private Integer shopping_record;//是否有购物记录 0：无,1：有

    private Integer gross_profit;//毛利

    private Integer is_main_push;//是否主推

    private String relevanceClassify;//关联分类json串

    private String relevanceReson;//关联分类原因

    public String getRelevanceReson() {
        return relevanceReson;
    }

    public void setRelevanceReson(String relevanceReson) {
        this.relevanceReson = relevanceReson;
    }

    public String getRelevanceClassify() {
        return relevanceClassify;
    }

    public void setRelevanceClassify(String relevanceClassify) {
        this.relevanceClassify = relevanceClassify;
    }

    public Integer getGross_profit() {
        return gross_profit;
    }

    public void setGross_profit(Integer gross_profit) {
        this.gross_profit = gross_profit;
    }

    public Integer getIs_main_push() {
        return is_main_push;
    }

    public void setIs_main_push(Integer is_main_push) {
        this.is_main_push = is_main_push;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    private List<String> tag;

    private int app_goods_status;//单个商品状态

    public int getApp_goods_status() {
        return app_goods_status;
    }

    private int is_medicare; //是否医保 默认0 1=非医保, 2=甲类医保, 3=乙类医保

    public int getIn_stock() {
        return in_stock;
    }

    public void setIn_stock(int in_stock) {
        this.in_stock = in_stock;
    }

    public int getIs_medicare() {
        return is_medicare;
    }

    public void setIs_medicare(int is_medicare) {
        this.is_medicare = is_medicare;
    }

    public void setApp_goods_status(int app_goods_status) {
        this.app_goods_status = app_goods_status;
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

    public String getGoods_company() {
        return goods_company;
    }

    public void setGoods_company(String goods_company) {
        this.goods_company = goods_company;
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

    public String getUser_cateid() {
        return user_cateid;
    }

    public void setUser_cateid(String user_cateid) {
        this.user_cateid = user_cateid;
    }

    public Object getCoupon_types() {
        return coupon_types;
    }

    public void setCoupon_types(Object coupon_types) {
        this.coupon_types = coupon_types;
    }

    public Long getDrug_category() {
        return drug_category;
    }

    public void setDrug_category(Long drug_category) {
        this.drug_category = drug_category;
    }

    public Long getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(Long goods_num) {
        this.goods_num = goods_num;
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

    public Integer getHave_task_plan() {
        return have_task_plan;
    }

    public void setHave_task_plan(Integer have_task_plan) {
        this.have_task_plan = have_task_plan;
    }

    public Integer getShopping_record() {
        return shopping_record;
    }

    public void setShopping_record(Integer shopping_record) {
        this.shopping_record = shopping_record;
    }

    @Override
    public String toString() {
        return "AppGoodsInfo{" +
            "goods_id=" + goods_id +
            ", goods_code='" + goods_code + '\'' +
            ", goods_title='" + goods_title + '\'' +
            ", goods_company='" + goods_company + '\'' +
            ", drug_name='" + drug_name + '\'' +
            ", com_name='" + com_name + '\'' +
            ", market_price=" + market_price +
            ", shop_price=" + shop_price +
            ", in_stock=" + in_stock +
            ", specif_cation='" + specif_cation + '\'' +
            ", goods_status=" + goods_status +
            ", bar_code='" + bar_code + '\'' +
            ", approval_number='" + approval_number + '\'' +
            ", brand_name='" + brand_name + '\'' +
            ", cate_code=" + cate_code +
            ", def_url='" + def_url + '\'' +
            ", user_cateid='" + user_cateid + '\'' +
            ", coupon_types=" + coupon_types +
            ", drug_category=" + drug_category +
            ", goods_num=" + goods_num +
            ", store_id=" + store_id +
            ", goods_price=" + goods_price +
            ", discount_price=" + discount_price +
            ", have_task_plan=" + have_task_plan +
            ", is_medicare=" + is_medicare +
            ", shopping_record=" + shopping_record +
            ", tag=" + tag +
            ", app_goods_status=" + app_goods_status +
            ", is_medicare=" + is_medicare +
            ", gross_profit=" + gross_profit +
            ", is_main_push=" + is_main_push +
            ", relevanceClassify=" + relevanceClassify +
            ", relevanceReson=" + relevanceReson +
            '}';
    }
}
