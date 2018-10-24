package com.jk51.model;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:门店信息表
 * 作者: yanglile
 * 创建日期: 2017-02-15
 * 修改记录:
 */
public class Stores {

    /**
     * 商家ID
     */
    private long site_id;

    /**
     * 门店ID
     */
    private long id;

    /**
     * 门店编号
     */
    private String stores_number;

    /**
     * 药店名称
     */
    private String name;

    /**
     * 旗舰店(1 是 0 否)
     */
    private int is_qjd;

    /**
     * 直营店 1 加盟店2
     */
    private int type;

    /**
     * 门店所属城市
     */
    private int city_id;

    /**
     * 门店所属区域
     */
    private int region_id;

    /**
     * 店地址
     */
    private String address;

    /**
     * 百度坐标 纬度
     */
    private String baidu_lat;

    /**
     * 百度坐标  经度
     */
    private String baidu_lng;

    /**
     * 高德地图 经度
     */
    private String gaode_lng;

    /**
     * 高德地图 纬度
     */
    private String gaode_lat;

    /**
     * 坐标是否标注: 1 是 0 否
     */
    private int map_flag;

    private String store_imgs;
    private String tel;
    private String business_time;
    private String feature;
    private String summary;
    private String qr_code_img;
    private int qr_code_type;
    private int stores_status;
    private int is_del;
    private String province;
    private String city;
    private String service_support;
    private String self_token_time;
    private String delivery_time;
    private String remind_mobile;
    private String order_lert;
    private String country;
    private int own_pricing_type;
    private int own_promotion_type;
    private Timestamp create_time;
    private Timestamp update_time;
    private String origin_shop_id;

    public String getOrder_lert() {
        return order_lert;
    }

    public void setOrder_lert(String order_lert) {
        this.order_lert = order_lert;
    }

    public String getOrigin_shop_id() {
        return origin_shop_id;
    }

    public void setOrigin_shop_id(String origin_shop_id) {
        this.origin_shop_id = origin_shop_id;
    }

    public long getSite_id() {
        return site_id;
    }

    public void setSite_id(long site_id) {
        this.site_id = site_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStores_number() {
        return stores_number;
    }

    public void setStores_number(String stores_number) {
        this.stores_number = stores_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIs_qjd() {
        return is_qjd;
    }

    public void setIs_qjd(int is_qjd) {
        this.is_qjd = is_qjd;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBaidu_lat() {
        return baidu_lat;
    }

    public void setBaidu_lat(String baidu_lat) {
        this.baidu_lat = baidu_lat;
    }

    public String getBaidu_lng() {
        return baidu_lng;
    }

    public void setBaidu_lng(String baidu_lng) {
        this.baidu_lng = baidu_lng;
    }

    public String getGaode_lng() {
        return gaode_lng;
    }

    public void setGaode_lng(String gaode_lng) {
        this.gaode_lng = gaode_lng;
    }

    public String getGaode_lat() {
        return gaode_lat;
    }

    public void setGaode_lat(String gaode_lat) {
        this.gaode_lat = gaode_lat;
    }

    public int getMap_flag() {
        return map_flag;
    }

    public void setMap_flag(int map_flag) {
        this.map_flag = map_flag;
    }

    public String getStore_imgs() {
        return store_imgs;
    }

    public void setStore_imgs(String store_imgs) {
        this.store_imgs = store_imgs;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getBusiness_time() {
        return business_time;
    }

    public void setBusiness_time(String business_time) {
        this.business_time = business_time;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getQr_code_img() {
        return qr_code_img;
    }

    public void setQr_code_img(String qr_code_img) {
        this.qr_code_img = qr_code_img;
    }

    public int getQr_code_type() {
        return qr_code_type;
    }

    public void setQr_code_type(int qr_code_type) {
        this.qr_code_type = qr_code_type;
    }

    public int getStores_status() {
        return stores_status;
    }

    public void setStores_status(int stores_status) {
        this.stores_status = stores_status;
    }

    public int getIs_del() {
        return is_del;
    }

    public void setIs_del(int is_del) {
        this.is_del = is_del;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getService_support() {
        return service_support;
    }

    public void setService_support(String service_support) {
        this.service_support = service_support;
    }

    public String getSelf_token_time() {
        return self_token_time;
    }

    public void setSelf_token_time(String self_token_time) {
        this.self_token_time = self_token_time;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public String getRemind_mobile() {
        return remind_mobile;
    }

    public void setRemind_mobile(String remind_mobile) {
        this.remind_mobile = remind_mobile;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getOwn_pricing_type() {
        return own_pricing_type;
    }

    public void setOwn_pricing_type(int own_pricing_type) {
        this.own_pricing_type = own_pricing_type;
    }

    public int getOwn_promotion_type() {
        return own_promotion_type;
    }

    public void setOwn_promotion_type(int own_promotion_type) {
        this.own_promotion_type = own_promotion_type;
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
