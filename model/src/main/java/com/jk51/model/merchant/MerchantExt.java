package com.jk51.model.merchant;

import java.util.Date;

public class MerchantExt {
    private Integer id;

    private Integer merchant_id;

    private String client_id;

    private String client_secret;

    private String company_qualurl;

    private String tax_certificate;

    private String organization_codecert;

    private String gsp_approve;

    private String drug_cert;

    private String medical_cert;

    private String medicalequi_cert;

    private String fdcirculation_cert;

    private String inter_drug_inform_cert;

    private String inter_drug_trad_cert;

    private String legal_person;

    private String recipe_frontend_setting;

    private String recipe_backend_setting;

    private String remark;

    private Integer wei_show_invite_code;

    private Integer compu_show_invite_code;

    private String trade_cert_number;

    private String style_list;

    private String active_style;

    private Integer icp_position;

    private Integer order_assign_type;

    private Integer order_pc_alert;

    private Integer order_voice_alert;

    private Integer order_phone_lert;

    private String order_remind_phones;

    private Byte balance_sheet_date;

    private String shopwx_serverurl;

    private String shop_ip;

    private String store_url;

    private String wx_sub_mch_id;

    private Integer allow_refund;

    private Date create_time;

    private Date update_time;

    private Integer system_backup;

    private String wx_appid;

    private String wx_secret;

    private String alipay_account;
    private String wx_original_id;
    private String order_lert;

    public String getOrder_lert() {
        return order_lert;
    }

    public void setOrder_lert(String order_lert) {
        this.order_lert = order_lert;
    }

    public String getWx_appid() {
        return wx_appid;
    }

    public void setWx_appid(String wx_appid) {
        this.wx_appid = wx_appid;
    }

    public String getWx_secret() {
        return wx_secret;
    }

    public void setWx_secret(String wx_secret) {
        this.wx_secret = wx_secret;
    }

    public String getAlipay_account() {
        return alipay_account;
    }

    public void setAlipay_account(String alipay_account) {
        this.alipay_account = alipay_account;
    }

    public String getWx_original_id() {
        return wx_original_id;
    }

    public void setWx_original_id(String wx_original_id) {
        this.wx_original_id = wx_original_id;
    }

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

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id == null ? null : client_id.trim();
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret == null ? null : client_secret.trim();
    }

    public String getCompany_qualurl() {
        return company_qualurl;
    }

    public void setCompany_qualurl(String company_qualurl) {
        this.company_qualurl = company_qualurl == null ? null : company_qualurl.trim();
    }

    public String getTax_certificate() {
        return tax_certificate;
    }

    public void setTax_certificate(String tax_certificate) {
        this.tax_certificate = tax_certificate == null ? null : tax_certificate.trim();
    }

    public String getOrganization_codecert() {
        return organization_codecert;
    }

    public void setOrganization_codecert(String organization_codecert) {
        this.organization_codecert = organization_codecert == null ? null : organization_codecert.trim();
    }

    public String getGsp_approve() {
        return gsp_approve;
    }

    public void setGsp_approve(String gsp_approve) {
        this.gsp_approve = gsp_approve == null ? null : gsp_approve.trim();
    }

    public String getDrug_cert() {
        return drug_cert;
    }

    public void setDrug_cert(String drug_cert) {
        this.drug_cert = drug_cert == null ? null : drug_cert.trim();
    }

    public String getMedical_cert() {
        return medical_cert;
    }

    public void setMedical_cert(String medical_cert) {
        this.medical_cert = medical_cert == null ? null : medical_cert.trim();
    }

    public String getMedicalequi_cert() {
        return medicalequi_cert;
    }

    public void setMedicalequi_cert(String medicalequi_cert) {
        this.medicalequi_cert = medicalequi_cert == null ? null : medicalequi_cert.trim();
    }

    public String getFdcirculation_cert() {
        return fdcirculation_cert;
    }

    public void setFdcirculation_cert(String fdcirculation_cert) {
        this.fdcirculation_cert = fdcirculation_cert == null ? null : fdcirculation_cert.trim();
    }

    public String getInter_drug_inform_cert() {
        return inter_drug_inform_cert;
    }

    public void setInter_drug_inform_cert(String inter_drug_inform_cert) {
        this.inter_drug_inform_cert = inter_drug_inform_cert == null ? null : inter_drug_inform_cert.trim();
    }

    public String getInter_drug_trad_cert() {
        return inter_drug_trad_cert;
    }

    public void setInter_drug_trad_cert(String inter_drug_trad_cert) {
        this.inter_drug_trad_cert = inter_drug_trad_cert == null ? null : inter_drug_trad_cert.trim();
    }

    public String getLegal_person() {
        return legal_person;
    }

    public void setLegal_person(String legal_person) {
        this.legal_person = legal_person == null ? null : legal_person.trim();
    }

    public String getRecipe_frontend_setting() {
        return recipe_frontend_setting;
    }

    public void setRecipe_frontend_setting(String recipe_frontend_setting) {
        this.recipe_frontend_setting = recipe_frontend_setting == null ? null : recipe_frontend_setting.trim();
    }

    public String getRecipe_backend_setting() {
        return recipe_backend_setting;
    }

    public void setRecipe_backend_setting(String recipe_backend_setting) {
        this.recipe_backend_setting = recipe_backend_setting == null ? null : recipe_backend_setting.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getWei_show_invite_code() {
        return wei_show_invite_code;
    }

    public void setWei_show_invite_code(Integer wei_show_invite_code) {
        this.wei_show_invite_code = wei_show_invite_code;
    }

    public Integer getCompu_show_invite_code() {
        return compu_show_invite_code;
    }

    public void setCompu_show_invite_code(Integer compu_show_invite_code) {
        this.compu_show_invite_code = compu_show_invite_code;
    }

    public String getTrade_cert_number() {
        return trade_cert_number;
    }

    public void setTrade_cert_number(String trade_cert_number) {
        this.trade_cert_number = trade_cert_number == null ? null : trade_cert_number.trim();
    }

    public String getStyle_list() {
        return style_list;
    }

    public void setStyle_list(String style_list) {
        this.style_list = style_list == null ? null : style_list.trim();
    }

    public String getActive_style() {
        return active_style;
    }

    public void setActive_style(String active_style) {
        this.active_style = active_style == null ? null : active_style.trim();
    }

    public Integer getIcp_position() {
        return icp_position;
    }

    public void setIcp_position(Integer icp_position) {
        this.icp_position = icp_position;
    }

    public Integer getOrder_assign_type() {
        return order_assign_type;
    }

    public void setOrder_assign_type(Integer order_assign_type) {
        this.order_assign_type = order_assign_type;
    }

    public Integer getOrder_pc_alert() {
        return order_pc_alert;
    }

    public void setOrder_pc_alert(Integer order_pc_alert) {
        this.order_pc_alert = order_pc_alert;
    }

    public Integer getOrder_voice_alert() {
        return order_voice_alert;
    }

    public void setOrder_voice_alert(Integer order_voice_alert) {
        this.order_voice_alert = order_voice_alert;
    }

    public Integer getOrder_phone_lert() {
        return order_phone_lert;
    }

    public void setOrder_phone_lert(Integer order_phone_lert) {
        this.order_phone_lert = order_phone_lert;
    }

    public String getOrder_remind_phones() {
        return order_remind_phones;
    }

    public void setOrder_remind_phones(String order_remind_phones) {
        this.order_remind_phones = order_remind_phones == null ? null : order_remind_phones.trim();
    }

    public Byte getBalance_sheet_date() {
        return balance_sheet_date;
    }

    public void setBalance_sheet_date(Byte balance_sheet_date) {
        this.balance_sheet_date = balance_sheet_date;
    }

    public String getShopwx_serverurl() {
        return shopwx_serverurl;
    }

    public void setShopwx_serverurl(String shopwx_serverurl) {
        this.shopwx_serverurl = shopwx_serverurl == null ? null : shopwx_serverurl.trim();
    }

    public String getShop_ip() {
        return shop_ip;
    }

    public void setShop_ip(String shop_ip) {
        this.shop_ip = shop_ip == null ? null : shop_ip.trim();
    }

    public String getStore_url() {
        return store_url;
    }

    public void setStore_url(String store_url) {
        this.store_url = store_url == null ? null : store_url.trim();
    }

    public String getWx_sub_mch_id() {
        return wx_sub_mch_id;
    }

    public void setWx_sub_mch_id(String wx_sub_mch_id) {
        this.wx_sub_mch_id = wx_sub_mch_id == null ? null : wx_sub_mch_id.trim();
    }

    public Integer getAllow_refund() {
        return allow_refund;
    }

    public void setAllow_refund(Integer allow_refund) {
        this.allow_refund = allow_refund;
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

    public Integer getSystem_backup() {
        return system_backup;
    }

    public void setSystem_backup(Integer system_backup) {
        this.system_backup = system_backup;
    }

    public String companyQualUrl() {
        return "MerchantExt{" +
            "company_qualurl='" + company_qualurl + '\'' +
            ", tax_certificate='" + tax_certificate + '\'' +
            ", organization_codecert='" + organization_codecert + '\'' +
            ", gsp_approve='" + gsp_approve + '\'' +
            ", drug_cert='" + drug_cert + '\'' +
            ", medical_cert='" + medical_cert + '\'' +
            ", medicalequi_cert='" + medicalequi_cert + '\'' +
            ", fdcirculation_cert='" + fdcirculation_cert + '\'' +
            ", inter_drug_inform_cert='" + inter_drug_inform_cert + '\'' +
            ", inter_drug_trad_cert='" + inter_drug_trad_cert + '\'' +
            '}';
    }
}
