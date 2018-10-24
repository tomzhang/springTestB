package com.jk51.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-04-01
 * 修改记录:
 */
@Configuration
public class PathUrlConfig {
    @Value("${erp.jiuzhou}")
    private String jiuzhou;

    @Value("${erp.qianjin}")
    private String qianjin;

    @Value("${erp.tianrun}")
    private String tianrun;


    public String getJiuzhou() {
        return jiuzhou;
    }

    public void setJiuzhou(String jiuzhou) {
        this.jiuzhou = jiuzhou;
    }

    public String getQianjin() {
        return qianjin;
    }

    public void setQianjin(String qianjin) {
        this.qianjin = qianjin;
    }

    public String getTianrun() {
        return tianrun;
    }

    public void setTianrun(String tianrun) {
        this.tianrun = tianrun;
    }

}
