package com.jk51.modules.pay.service.uni;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-21
 * 修改记录:
 */
@Component
@ConfigurationProperties(prefix = "pay.unipay")
public class UniConfig {
    private String merId;
    private String frontUrl;
    private String backUrl;
    /** 前台请求URL. */
    private String frontRequestUrl;
    /** 后台请求URL. */
    private String backRequestUrl;
    /** 签名证书路径. */
    private String signCertPath;
    /** 签名证书密码. */
    private String signCertPwd;
    /** 签名证书类型. */
    private String signCertType;
    /** 验证签名公钥证书目录. */
    private String validateCertDir;

    private String validateCertPath;

    public String getValidateCertPath() {
        return validateCertPath;
    }

    public void setValidateCertPath(String validateCertPath) {
        this.validateCertPath = validateCertPath;
    }

    public String getValidateCertDir() {
        return validateCertDir;
    }

    public void setValidateCertDir(String validateCertDir) {
        this.validateCertDir = validateCertDir;
    }

    public String getSignCertPath() {
        return signCertPath;
    }

    public void setSignCertPath(String signCertPath) {
        this.signCertPath = signCertPath;
    }

    public String getSignCertPwd() {
        return signCertPwd;
    }

    public void setSignCertPwd(String signCertPwd) {
        this.signCertPwd = signCertPwd;
    }

    public String getSignCertType() {
        return signCertType;
    }

    public void setSignCertType(String signCertType) {
        this.signCertType = signCertType;
    }

    public String getFrontRequestUrl() {
        return frontRequestUrl;
    }

    public void setFrontRequestUrl(String frontRequestUrl) {
        this.frontRequestUrl = frontRequestUrl;
    }

    public String getBackRequestUrl() {
        return backRequestUrl;
    }

    public void setBackRequestUrl(String backRequestUrl) {
        this.backRequestUrl = backRequestUrl;
    }

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }
}
