package com.jk51.modules.userScenarios.util;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:新人管理器
 * 作者: ChengShanyunduo
 * 创建日期: 2017-06-07
 * 修改记录:
 */
public class MyX509TrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
