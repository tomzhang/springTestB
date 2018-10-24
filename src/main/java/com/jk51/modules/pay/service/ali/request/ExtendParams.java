package com.jk51.modules.pay.service.ali.request;


public class ExtendParams {
    private String hb_fq_num;
    private String hb_fq_seller_percent;
    private String sys_service_provider_id;

    public ExtendParams() {
    }

    public String getHb_fq_num() {
        return hb_fq_num;
    }

    public void setHb_fq_num(String hb_fq_num) {
        this.hb_fq_num = hb_fq_num;
    }

    public String getHb_fq_seller_percent() {
        return hb_fq_seller_percent;
    }

    public void setHb_fq_seller_percent(String hb_fq_seller_percent) {
        this.hb_fq_seller_percent = hb_fq_seller_percent;
    }

    public String getSys_service_provider_id() {
        return sys_service_provider_id;
    }

    public void setSys_service_provider_id(String sys_service_provider_id) {
        this.sys_service_provider_id = sys_service_provider_id;
    }
}
