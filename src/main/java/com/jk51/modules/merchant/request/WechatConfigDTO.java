package com.jk51.modules.merchant.request;

public class WechatConfigDTO {
    private Boolean hasErpPrice;
    private String title;
    private Integer siteId;
    private String integralName;
    private String pcLogurl;
    private String baiduScript;
    private String merchantName;

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public void setBaiduScript(String baiduScript) {
        this.baiduScript = baiduScript;
    }

    public String getBaiduScript() {
        return baiduScript;
    }

    public String getPcLogurl() {
        return pcLogurl;
    }

    public void setPcLogurl(String pcLogurl) {
        this.pcLogurl = pcLogurl;
    }

    public Boolean getHasErpPrice() {
        return hasErpPrice;
    }

    public void setHasErpPrice(Boolean hasErpPrice) {
        this.hasErpPrice = hasErpPrice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getIntegralName() {
        return integralName;
    }

    @Override
    public String toString() {
        return "WechatConfigDTO{" +
            "hasErpPrice=" + hasErpPrice +
            ", title='" + title + '\'' +
            ", siteId=" + siteId +
            ", integralName='" + integralName + '\'' +
            ", pcLogurl='" + pcLogurl + '\'' +
            ", baiduScript='" + baiduScript + '\'' +
            ", merchantName='" + merchantName + '\'' +
            '}';
    }

    public void setIntegralName(String integralName) {
        this.integralName = integralName;
    }

}
