package com.jk51.modules.sms.smsConfig;

/**
*    短信模板的必要参数
*  smsEnum     短信枚举
 *  siteId
 *  phone
 *  serviceType
 *  smsType
 *  args      模板动态参数数组
 **/
public class SmsParams {
    private SmsEnum smsEnum;
    private Integer siteId;
    private String phone;
    private Integer serviceType;
    private Integer smsType;
    private String[] args;

    public SmsParams(Integer siteId, String phone, Integer serviceType, Integer smsType,SmsEnum smsEnum,String[] args) {
        this.siteId = siteId;
        this.phone = phone;
        this.serviceType = serviceType;
        this.smsType = smsType;
        this.smsEnum = smsEnum;
        this.args = args;
    }

    public SmsParams() {
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public Integer getSmsType() {
        return smsType;
    }

    public void setSmsType(Integer smsType) {
        this.smsType = smsType;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public SmsEnum getSmsEnum() {
        return smsEnum;
    }

    public void setSmsEnum(SmsEnum smsEnum) {
        this.smsEnum = smsEnum;
    }
}
