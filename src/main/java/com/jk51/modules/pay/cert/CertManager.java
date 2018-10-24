package com.jk51.modules.pay.cert;

import com.jk51.modules.pay.service.uni.UniConfig;
import com.jk51.modules.pay.service.wx.WxAppConfig;
import com.jk51.modules.pay.service.wx.WxConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-24
 * 修改记录:
 */
@Component
public class CertManager {
    @Autowired
    WxConfig wxConfig;
    @Autowired
    UniConfig uniConfig;
    @Autowired
    WxAppConfig wxAppConfig;

    /**
     * 获取微信证书
     * @return
     */
    public InputStream getWxCert() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(wxConfig.getCert_path());
    }

    /**
     * 获取微信证书
     * @return
     */
    public InputStream getWxCertApp() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(wxAppConfig.getCert_path());
    }



    /**
     * 获取银联公钥文件
     * @return
     */
    public InputStream getUniPublicKey() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(uniConfig.getValidateCertPath());
    }

    /**
     * 获取银联私钥文件
     * @return
     */
    public InputStream getUniPrivateKey() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(uniConfig.getSignCertPath());
    }
    /**
     * 获取微信证书
     * @return
     */
    public InputStream getWxCert(String cert_path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(cert_path);
    }
}
