package com.jk51.model.merchant;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class YbMerchant implements Serializable {
    private Integer id;

    private Integer merchant_id;

    private String merchant_name;

    private String seller_nick;

    private String seller_pwd;

    private String company_name;

    private String legal_name;

    private String shop_title;

    private String shop_url;

    private String shop_logurl;

    private Integer shop_area;

    private String shop_address;

    private String service_phone;

    private String service_mobile;

    private String company_email;

    private String short_message_sign;

    private String shop_qq;

    private String shop_weixin;

    private String shopwx_url;

    private String shop_desc;

    private Integer invoice_is;

    private Integer role_id;

    private String company_qualurl;

    private Date last_login;

    private String last_ipaddr;

    private Integer is_frozen;

    private String frozen_resion;

    private String site_record;

    private BigDecimal integral_proportion;

    private String qrcode_tips;

    private String legal_mobile;

    private String payee_name;

    private String shop_watermark;

    private Integer site_status;

    private Integer wx_site_status;

    private String shop_back1;

    private Date create_time;

    private Date update_time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(Integer merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name == null ? null : merchant_name.trim();
    }

    public String getSeller_nick() {
        return seller_nick;
    }

    public void setSeller_nick(String seller_nick) {
        this.seller_nick = seller_nick == null ? null : seller_nick.trim();
    }

    public String getSeller_pwd() {
        return seller_pwd;
    }

    public void setSeller_pwd(String seller_pwd) {
        this.seller_pwd = seller_pwd == null ? null : seller_pwd.trim();
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name == null ? null : company_name.trim();
    }

    public String getLegal_name() {
        return legal_name;
    }

    public void setLegal_name(String legal_name) {
        this.legal_name = legal_name == null ? null : legal_name.trim();
    }

    public String getShop_title() {
        return shop_title;
    }

    public void setShop_title(String shop_title) {
        this.shop_title = shop_title == null ? null : shop_title.trim();
    }

    public String getShop_url() {
        return shop_url;
    }

    public void setShop_url(String shop_url) {
        this.shop_url = shop_url == null ? null : shop_url.trim();
    }

    public String getShop_logurl() {
        return shop_logurl;
    }

    public void setShop_logurl(String shop_logurl) {
        this.shop_logurl = shop_logurl == null ? null : shop_logurl.trim();
    }

    public Integer getShop_area() {
        return shop_area;
    }

    public void setShop_area(Integer shop_area) {
        this.shop_area = shop_area;
    }

    public String getShop_address() {
        return shop_address;
    }

    public void setShop_address(String shop_address) {
        this.shop_address = shop_address == null ? null : shop_address.trim();
    }

    public String getService_phone() {
        return service_phone;
    }

    public void setService_phone(String service_phone) {
        this.service_phone = service_phone == null ? null : service_phone.trim();
    }

    public String getService_mobile() {
        return service_mobile;
    }

    public void setService_mobile(String service_mobile) {
        this.service_mobile = service_mobile == null ? null : service_mobile.trim();
    }

    public String getCompany_email() {
        return company_email;
    }

    public void setCompany_email(String company_email) {
        this.company_email = company_email == null ? null : company_email.trim();
    }

    public String getShort_message_sign() {
        return short_message_sign;
    }

    public void setShort_message_sign(String short_message_sign) {
        this.short_message_sign = short_message_sign == null ? null : short_message_sign.trim();
    }

    public String getShop_qq() {
        return shop_qq;
    }

    public void setShop_qq(String shop_qq) {
        this.shop_qq = shop_qq == null ? null : shop_qq.trim();
    }

    public String getShop_weixin() {
        return shop_weixin;
    }

    public void setShop_weixin(String shop_weixin) {
        this.shop_weixin = shop_weixin == null ? null : shop_weixin.trim();
    }

    public String getShopwx_url() {
        return shopwx_url;
    }

    public void setShopwx_url(String shopwx_url) {
        this.shopwx_url = shopwx_url == null ? null : shopwx_url.trim();
    }

    public String getShop_desc() {
        return shop_desc;
    }

    public void setShop_desc(String shop_desc) {
        this.shop_desc = shop_desc == null ? null : shop_desc.trim();
    }

    public Integer getInvoice_is() {
        return invoice_is;
    }

    public void setInvoice_is(Integer invoice_is) {
        this.invoice_is = invoice_is;
    }

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public String getCompany_qualurl() {
        return company_qualurl;
    }

    public void setCompany_qualurl(String company_qualurl) {
        this.company_qualurl = company_qualurl == null ? null : company_qualurl.trim();
    }

    public Date getLast_login() {
        return last_login;
    }

    public void setLast_login(Date last_login) {
        this.last_login = last_login;
    }

    public String getLast_ipaddr() {
        return last_ipaddr;
    }

    public void setLast_ipaddr(String last_ipaddr) {
        this.last_ipaddr = last_ipaddr == null ? null : last_ipaddr.trim();
    }

    public Integer getIs_frozen() {
        return is_frozen;
    }

    public void setIs_frozen(Integer is_frozen) {
        this.is_frozen = is_frozen;
    }

    public String getFrozen_resion() {
        return frozen_resion;
    }

    public void setFrozen_resion(String frozen_resion) {
        this.frozen_resion = frozen_resion == null ? null : frozen_resion.trim();
    }

    public String getSite_record() {
        return site_record;
    }

    public void setSite_record(String site_record) {
        this.site_record = site_record == null ? null : site_record.trim();
    }

    public BigDecimal getIntegral_proportion() {
        return integral_proportion;
    }

    public void setIntegral_proportion(BigDecimal integral_proportion) {
        this.integral_proportion = integral_proportion;
    }

    public String getQrcode_tips() {
        return qrcode_tips;
    }

    public void setQrcode_tips(String qrcode_tips) {
        this.qrcode_tips = qrcode_tips == null ? null : qrcode_tips.trim();
    }

    public String getLegal_mobile() {
        return legal_mobile;
    }

    public void setLegal_mobile(String legal_mobile) {
        this.legal_mobile = legal_mobile;
    }

    public String getPayee_name() {
        return payee_name;
    }

    public void setPayee_name(String payee_name) {
        this.payee_name = payee_name == null ? null : payee_name.trim();
    }

    public String getShop_watermark() {
        return shop_watermark;
    }

    public void setShop_watermark(String shop_watermark) {
        this.shop_watermark = shop_watermark == null ? null : shop_watermark.trim();
    }

    public Integer getSite_status() {
        return site_status;
    }

    public void setSite_status(Integer site_status) {
        this.site_status = site_status;
    }

    public Integer getWx_site_status() {
        return wx_site_status;
    }

    public void setWx_site_status(Integer wx_site_status) {
        this.wx_site_status = wx_site_status;
    }

    public String getShop_back1() {
        return shop_back1;
    }

    public void setShop_back1(String shop_back1) {
        this.shop_back1 = shop_back1 == null ? null : shop_back1.trim();
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}
