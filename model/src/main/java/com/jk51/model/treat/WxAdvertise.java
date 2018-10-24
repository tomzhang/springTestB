package com.jk51.model.treat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WxAdvertise {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_wx_ads.ads_id
     *
     * @mbg.generated
     */
    @JsonProperty("adsId")
    private Integer adsId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_wx_ads.site_id
     *
     * @mbg.generated
     */
    @JsonProperty("siteId")
    private Integer siteId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_wx_ads.type
     *
     * @mbg.generated
     */
    @JsonProperty("type")
    private Integer type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_wx_ads.title
     *
     * @mbg.generated
     */
    @JsonProperty("title")
    private String title;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_wx_ads.links
     *
     * @mbg.generated
     */
    @JsonProperty("links")
    private String links;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_wx_ads.ads_img
     *
     * @mbg.generated
     */
    @JsonProperty("adsImg")
    private String adsImg;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_wx_ads.icon
     *
     * @mbg.generated
     */
    @JsonProperty("icon")
    private String icon;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_wx_ads.list_order
     *
     * @mbg.generated
     */
    @JsonProperty("listOrder")
    private Integer listOrder;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_wx_ads.status
     *
     * @mbg.generated
     */
    @JsonProperty("status")
    private Integer status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_wx_ads.create_time
     *
     * @mbg.generated
     */
    @JsonProperty("createTime")
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_wx_ads.update_time
     *
     * @mbg.generated
     */
    @JsonProperty("updateTime")
    private Date updateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_wx_ads.ads_id
     *
     * @return the value of b_wx_ads.ads_id
     *
     * @mbg.generated
     */
    public Integer getAdsId() {
        return adsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_wx_ads.ads_id
     *
     * @param adsId the value for b_wx_ads.ads_id
     *
     * @mbg.generated
     */
    public void setAdsId(Integer adsId) {
        this.adsId = adsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_wx_ads.site_id
     *
     * @return the value of b_wx_ads.site_id
     *
     * @mbg.generated
     */
    public Integer getSiteId() {
        return siteId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_wx_ads.site_id
     *
     * @param siteId the value for b_wx_ads.site_id
     *
     * @mbg.generated
     */
    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_wx_ads.type
     *
     * @return the value of b_wx_ads.type
     *
     * @mbg.generated
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_wx_ads.type
     *
     * @param type the value for b_wx_ads.type
     *
     * @mbg.generated
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_wx_ads.title
     *
     * @return the value of b_wx_ads.title
     *
     * @mbg.generated
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_wx_ads.title
     *
     * @param title the value for b_wx_ads.title
     *
     * @mbg.generated
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_wx_ads.links
     *
     * @return the value of b_wx_ads.links
     *
     * @mbg.generated
     */
    public String getLinks() {
        return links;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_wx_ads.links
     *
     * @param links the value for b_wx_ads.links
     *
     * @mbg.generated
     */
    public void setLinks(String links) {
        this.links = links == null ? null : links.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_wx_ads.ads_img
     *
     * @return the value of b_wx_ads.ads_img
     *
     * @mbg.generated
     */
    public String getAdsImg() {
        return adsImg;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_wx_ads.ads_img
     *
     * @param adsImg the value for b_wx_ads.ads_img
     *
     * @mbg.generated
     */
    public void setAdsImg(String adsImg) {
        this.adsImg = adsImg == null ? null : adsImg.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_wx_ads.icon
     *
     * @return the value of b_wx_ads.icon
     *
     * @mbg.generated
     */
    public String getIcon() {
        return icon;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_wx_ads.icon
     *
     * @param icon the value for b_wx_ads.icon
     *
     * @mbg.generated
     */
    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_wx_ads.list_order
     *
     * @return the value of b_wx_ads.list_order
     *
     * @mbg.generated
     */
    public Integer getListOrder() {
        return listOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_wx_ads.list_order
     *
     * @param listOrder the value for b_wx_ads.list_order
     *
     * @mbg.generated
     */
    public void setListOrder(Integer listOrder) {
        this.listOrder = listOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_wx_ads.status
     *
     * @return the value of b_wx_ads.status
     *
     * @mbg.generated
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_wx_ads.status
     *
     * @param status the value for b_wx_ads.status
     *
     * @mbg.generated
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_wx_ads.create_time
     *
     * @return the value of b_wx_ads.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_wx_ads.create_time
     *
     * @param createTime the value for b_wx_ads.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_wx_ads.update_time
     *
     * @return the value of b_wx_ads.update_time
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_wx_ads.update_time
     *
     * @param updateTime the value for b_wx_ads.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}