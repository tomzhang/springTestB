package com.jk51.modules.sms.smsConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-17
 * 修改记录:
 */
@Component
@ConfigurationProperties(prefix = "sms.ytx")
public class YtxSmsConfig {
    //请求地址
    private String ytx_sms_url;
    //请求端口
    private String ytx_sms_port;
    //account sid
    private String ytx_sms_sid;
    //account token
    private String ytx_sms_token;
    //应用 id  appid
    private String ytx_sms_appid;
    //过期时间
    private String ytx_sms_valid_min;
    //验证码模板id
    private String ytx_sms_tempid_regcode;
    //通知模板id
    private String ytx_sms_tempid_msg;
    //通知模板id
    private String ytx_sms_yu_appid;

    public void setYtx_sms_yu_appid(String ytx_sms_yu_appid) {
        this.ytx_sms_yu_appid = ytx_sms_yu_appid;
    }

    public String getYtx_sms_yu_appid() {
        return ytx_sms_yu_appid;
    }

    public String getYtx_sms_url() {
        return ytx_sms_url;
    }

    public void setYtx_sms_url(String ytx_sms_url) {
        this.ytx_sms_url = ytx_sms_url;
    }

    public String getYtx_sms_port() {
        return ytx_sms_port;
    }

    public void setYtx_sms_port(String ytx_sms_port) {
        this.ytx_sms_port = ytx_sms_port;
    }

    public String getYtx_sms_sid() {
        return ytx_sms_sid;
    }

    public void setYtx_sms_sid(String ytx_sms_sid) {
        this.ytx_sms_sid = ytx_sms_sid;
    }

    public String getYtx_sms_token() {
        return ytx_sms_token;
    }

    public void setYtx_sms_token(String ytx_sms_token) {
        this.ytx_sms_token = ytx_sms_token;
    }

    public String getYtx_sms_appid() {
        return ytx_sms_appid;
    }

    public void setYtx_sms_appid(String ytx_sms_appid) {
        this.ytx_sms_appid = ytx_sms_appid;
    }

    public String getYtx_sms_valid_min() {
        return ytx_sms_valid_min;
    }

    public void setYtx_sms_valid_min(String ytx_sms_valid_min) {
        this.ytx_sms_valid_min = ytx_sms_valid_min;
    }

    public String getYtx_sms_tempid_regcode() {
        return ytx_sms_tempid_regcode;
    }

    public void setYtx_sms_tempid_regcode(String ytx_sms_tempid_regcode) {
        this.ytx_sms_tempid_regcode = ytx_sms_tempid_regcode;
    }

    public String getYtx_sms_tempid_msg() {
        return ytx_sms_tempid_msg;
    }

    public void setYtx_sms_tempid_msg(String ytx_sms_tempid_msg) {
        this.ytx_sms_tempid_msg = ytx_sms_tempid_msg;
    }
}
