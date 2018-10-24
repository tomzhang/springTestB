package com.jk51.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件名:com.jk51.interceptor.RequestWrapInterceptorAdapter
 * 描述: 将web请求的request设置到threadLocal中，便于后续如登录校验等处理.
 * 作者: wangzhengfei
 * 创建日期: 2017-02-03
 * 修改记录:
 */
public class RequestWrapInterceptorAdapter extends HandlerInterceptorAdapter {

    //before the actual handler will be executed
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler)
            throws Exception {
        HttpRequestThreadScope.set(request,response);
        return true;
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        HttpRequestThreadScope.remove();
    }

}
