package com.jk51.model.distribute;

import com.jk51.model.order.Page;

import java.util.Date;

/**
 * Created by guosheng on 2017/4/19.
 */
public class QueryGoodsDistribute  {
    private String userCateid; //商品类目
    private String goodsCode;
    private String drugName;
    private String specifCation;
    private String goodsTitle;
    private Integer minPrice;
    private Integer maxPrice;
    private String name;
    private Integer shopPrice;
    private Integer costPrice;
    private Integer siteId;
    private  Integer templateid;
    private Integer goodsStatus;

    private String  templateName;
    private String reward;
    private String discount;

    private Long id;

    private String hash;

    private Short width;

    private Short height;

    private Integer size;

    private Integer type;

    private String host_id;

    private Boolean flag;

    private Integer goods_id;

    private Boolean is_default;

    private Date create_time;

    private Integer pageNum;

    private Integer pageSize;

    private Integer has_erp;

    public Integer getHas_erp() {
        return has_erp;
    }

    public void setHas_erp(Integer has_erp) {
        this.has_erp = has_erp;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getSpecifCation() {
        return specifCation;
    }

    public void setSpecifCation(String specifCation) {
        this.specifCation = specifCation;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Short getWidth() {
        return width;
    }

    public void setWidth(Short width) {
        this.width = width;
    }

    public Short getHeight() {
        return height;
    }

    public void setHeight(Short height) {
        this.height = height;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getHost_id() {
        return host_id;
    }

    public void setHost_id(String host_id) {
        this.host_id = host_id;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Integer getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(Integer goods_id) {
        this.goods_id = goods_id;
    }

    public Boolean getIs_default() {
        return is_default;
    }

    public void setIs_default(Boolean is_default) {
        this.is_default = is_default;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getUserCateid() {
        return userCateid;
    }

    public void setUserCateid(String userCateid) {
        this.userCateid = userCateid;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(Integer shopPrice) {
        this.shopPrice = shopPrice;
    }

    public Integer getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Integer costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getTemplateid() {
        return templateid;
    }

    public void setTemplateid(Integer templateid) {
        this.templateid = templateid;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }


    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(Integer goodsStatus) {
        this.goodsStatus = goodsStatus;
    }
}
