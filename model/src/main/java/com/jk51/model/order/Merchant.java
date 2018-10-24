package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:商家信息(连锁)
 * 作者: baixiongfei
 * 创建日期: 2017/2/20
 * 修改记录:
 */
public class Merchant {

    private int	id;
    private int	merchantId;
    private String	merchantName;
    private String	sellerNick;
    private String	sellerPwd;
    private String	companyName;
    private String	legalName;
    private String	shopTitle;
    private String	shopUrl;
    private String	shopLogurl;
    private int	shopArea;
    private String	shopAddress;
    private String	servicePhone;
    private String	serviceMobile;
    private String	companyEmail;
    private String	shortMessageSign;
    private String	shopQq;
    private String	shopWeixin;
    private String	shopwxUrl;
    private String	shopDesc;
    private int	invoiceIs;
    private int	roleId;
    private String	companyQualurl;
    private Timestamp lastLogin;
    private String	lastIpaddr;
    private int	isFrozen;
    private String	frozenResion;
    private String	siteRecord;
    private int	integralProportion;
    private String	qrcodeTips;
    private long legalMobile;
    private String	payeeName;
    private String	shopWatermark;
    private int	siteStatus;
    private int	wxSiteStatus;
    private String	shopBack1;
    private Timestamp createTime;
    private Timestamp updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getSellerNick() {
        return sellerNick;
    }

    public void setSellerNick(String sellerNick) {
        this.sellerNick = sellerNick;
    }

    public String getSellerPwd() {
        return sellerPwd;
    }

    public void setSellerPwd(String sellerPwd) {
        this.sellerPwd = sellerPwd;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getShopTitle() {
        return shopTitle;
    }

    public void setShopTitle(String shopTitle) {
        this.shopTitle = shopTitle;
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    public String getShopLogurl() {
        return shopLogurl;
    }

    public void setShopLogurl(String shopLogurl) {
        this.shopLogurl = shopLogurl;
    }

    public int getShopArea() {
        return shopArea;
    }

    public void setShopArea(int shopArea) {
        this.shopArea = shopArea;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }

    public String getServiceMobile() {
        return serviceMobile;
    }

    public void setServiceMobile(String serviceMobile) {
        this.serviceMobile = serviceMobile;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getShortMessageSign() {
        return shortMessageSign;
    }

    public void setShortMessageSign(String shortMessageSign) {
        this.shortMessageSign = shortMessageSign;
    }

    public String getShopQq() {
        return shopQq;
    }

    public void setShopQq(String shopQq) {
        this.shopQq = shopQq;
    }

    public String getShopWeixin() {
        return shopWeixin;
    }

    public void setShopWeixin(String shopWeixin) {
        this.shopWeixin = shopWeixin;
    }

    public String getShopwxUrl() {
        return shopwxUrl;
    }

    public void setShopwxUrl(String shopwxUrl) {
        this.shopwxUrl = shopwxUrl;
    }

    public String getShopDesc() {
        return shopDesc;
    }

    public void setShopDesc(String shopDesc) {
        this.shopDesc = shopDesc;
    }

    public int getInvoiceIs() {
        return invoiceIs;
    }

    public void setInvoiceIs(int invoiceIs) {
        this.invoiceIs = invoiceIs;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getCompanyQualurl() {
        return companyQualurl;
    }

    public void setCompanyQualurl(String companyQualurl) {
        this.companyQualurl = companyQualurl;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastIpaddr() {
        return lastIpaddr;
    }

    public void setLastIpaddr(String lastIpaddr) {
        this.lastIpaddr = lastIpaddr;
    }

    public int getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(int isFrozen) {
        this.isFrozen = isFrozen;
    }

    public String getFrozenResion() {
        return frozenResion;
    }

    public void setFrozenResion(String frozenResion) {
        this.frozenResion = frozenResion;
    }

    public String getSiteRecord() {
        return siteRecord;
    }

    public void setSiteRecord(String siteRecord) {
        this.siteRecord = siteRecord;
    }

    public int getIntegralProportion() {
        return integralProportion;
    }

    public void setIntegralProportion(int integralProportion) {
        this.integralProportion = integralProportion;
    }

    public String getQrcodeTips() {
        return qrcodeTips;
    }

    public void setQrcodeTips(String qrcodeTips) {
        this.qrcodeTips = qrcodeTips;
    }
    
    public long getLegalMobile() {
        return legalMobile;
    }

    public void setLegalMobile(long legalMobile) {
        this.legalMobile = legalMobile;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getShopWatermark() {
        return shopWatermark;
    }

    public void setShopWatermark(String shopWatermark) {
        this.shopWatermark = shopWatermark;
    }

    public int getSiteStatus() {
        return siteStatus;
    }

    public void setSiteStatus(int siteStatus) {
        this.siteStatus = siteStatus;
    }

    public int getWxSiteStatus() {
        return wxSiteStatus;
    }

    public void setWxSiteStatus(int wxSiteStatus) {
        this.wxSiteStatus = wxSiteStatus;
    }

    public String getShopBack1() {
        return shopBack1;
    }

    public void setShopBack1(String shopBack1) {
        this.shopBack1 = shopBack1;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Merchant{" +
                "id=" + id +
                ", merchantId=" + merchantId +
                ", merchantName='" + merchantName + '\'' +
                ", sellerNick='" + sellerNick + '\'' +
                ", sellerPwd='" + sellerPwd + '\'' +
                ", companyName='" + companyName + '\'' +
                ", legalName='" + legalName + '\'' +
                ", shopTitle='" + shopTitle + '\'' +
                ", shopUrl='" + shopUrl + '\'' +
                ", shopLogurl='" + shopLogurl + '\'' +
                ", shopArea=" + shopArea +
                ", shopAddress='" + shopAddress + '\'' +
                ", servicePhone='" + servicePhone + '\'' +
                ", serviceMobile='" + serviceMobile + '\'' +
                ", companyEmail='" + companyEmail + '\'' +
                ", shortMessageSign='" + shortMessageSign + '\'' +
                ", shopQq='" + shopQq + '\'' +
                ", shopWeixin='" + shopWeixin + '\'' +
                ", shopwxUrl='" + shopwxUrl + '\'' +
                ", shopDesc='" + shopDesc + '\'' +
                ", invoiceIs=" + invoiceIs +
                ", roleId=" + roleId +
                ", companyQualurl='" + companyQualurl + '\'' +
                ", lastLogin=" + lastLogin +
                ", lastIpaddr='" + lastIpaddr + '\'' +
                ", isFrozen=" + isFrozen +
                ", frozenResion='" + frozenResion + '\'' +
                ", siteRecord='" + siteRecord + '\'' +
                ", integralProportion=" + integralProportion +
                ", qrcodeTips='" + qrcodeTips + '\'' +
                ", legalMobile=" + legalMobile +
                ", payeeName='" + payeeName + '\'' +
                ", shopWatermark='" + shopWatermark + '\'' +
                ", siteStatus=" + siteStatus +
                ", wxSiteStatus=" + wxSiteStatus +
                ", shopBack1='" + shopBack1 + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
