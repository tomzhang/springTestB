package com.jk51.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 文件名:com.jk51.interceptor.WebMVCConfigurerAdapter
 * 描述: 自定义web请求拦截策略配置
 * 作者: wangzhengfei
 * 创建日期: 2017-02-03
 * 修改记录:
 */
@Configuration
public class WebMVCConfigurerAdapter extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestWrapInterceptorAdapter());
    }

}
