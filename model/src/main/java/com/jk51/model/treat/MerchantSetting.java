package com.jk51.model.treat;

import java.util.Date;

public class MerchantSetting {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column yb_merchant_settings.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column yb_merchant_settings.merchant_id
     *
     * @mbg.generated
     */
    private Integer merchantId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column yb_merchant_settings.type
     *
     * @mbg.generated
     */
    private String type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column yb_merchant_settings.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column yb_merchant_settings.update_time
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column yb_merchant_settings.content
     *
     * @mbg.generated
     */
    private String content;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column yb_merchant_settings.id
     *
     * @return the value of yb_merchant_settings.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column yb_merchant_settings.id
     *
     * @param id the value for yb_merchant_settings.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column yb_merchant_settings.merchant_id
     *
     * @return the value of yb_merchant_settings.merchant_id
     *
     * @mbg.generated
     */
    public Integer getMerchantId() {
        return merchantId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column yb_merchant_settings.merchant_id
     *
     * @param merchantId the value for yb_merchant_settings.merchant_id
     *
     * @mbg.generated
     */
    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column yb_merchant_settings.type
     *
     * @return the value of yb_merchant_settings.type
     *
     * @mbg.generated
     */
    public String getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column yb_merchant_settings.type
     *
     * @param type the value for yb_merchant_settings.type
     *
     * @mbg.generated
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column yb_merchant_settings.create_time
     *
     * @return the value of yb_merchant_settings.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column yb_merchant_settings.create_time
     *
     * @param createTime the value for yb_merchant_settings.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column yb_merchant_settings.update_time
     *
     * @return the value of yb_merchant_settings.update_time
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column yb_merchant_settings.update_time
     *
     * @param updateTime the value for yb_merchant_settings.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column yb_merchant_settings.content
     *
     * @return the value of yb_merchant_settings.content
     *
     * @mbg.generated
     */
    public String getContent() {
        return content;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column yb_merchant_settings.content
     *
     * @param content the value for yb_merchant_settings.content
     *
     * @mbg.generated
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}