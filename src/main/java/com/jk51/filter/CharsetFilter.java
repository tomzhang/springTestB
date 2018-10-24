package com.jk51.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component("charsetFilter")
public class CharsetFilter implements Filter {
    private static final Logger log =  LoggerFactory.getLogger(CharsetFilter.class);
    public void destroy() {
        log.info("过滤器销毁");
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        //log.info("执行过滤操作");
        request.setCharacterEncoding("utf-8");
        chain.doFilter(request, response);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
    }
    @Override
    public void init(FilterConfig config) throws ServletException {
        log.info("过滤器初始化");
    }
}
