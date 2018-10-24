package com.jk51.modules.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.BeanInitializationException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-23
 * 修改记录:
 */
public class MShiroFilterFactoryBean extends ShiroFilterFactoryBean {

    //需要忽略的请求
    private Set<String> ignoreExt;

    public MShiroFilterFactoryBean(){
        super();
        ignoreExt = new HashSet<>();
        ignoreExt.add(".jpg");
        ignoreExt.add(".png");
        ignoreExt.add(".gif");
        ignoreExt.add(".bmp");
        ignoreExt.add(".js");
        ignoreExt.add(".css");
    }

    @Override
    protected AbstractShiroFilter createInstance() throws Exception {

        SecurityManager securityManager = getSecurityManager();
        if(null == securityManager)
        {
            String msg = "";
            throw new BeanInitializationException(msg);
        }
        if(!(securityManager instanceof WebSecurityManager)){
            String msg ="";
            throw new BeanInitializationException(msg);
        }

        FilterChainManager manager = createFilterChainManager();
        PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
        chainResolver.setFilterChainManager(manager);
        return new MSpringShiroFilter((WebSecurityManager) securityManager, chainResolver);
    }


    private final class MSpringShiroFilter extends AbstractShiroFilter {

        protected MSpringShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
            super();
            if (webSecurityManager == null) {
                throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
            }
            setSecurityManager(webSecurityManager);
            if (resolver != null) {
                setFilterChainResolver(resolver);
            }
        }

        @Override
        protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse,
                                        FilterChain chain) throws ServletException, IOException {
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            String str = request.getRequestURI().toLowerCase();
            boolean flag = true;
            int idx = 0;
            if(( idx = str.indexOf(".")) > 0){
                str = str.substring(idx);
                if(ignoreExt.contains(str.toLowerCase()))
                    flag = false;
            }
            if(flag){
                super.doFilterInternal(servletRequest, servletResponse, chain);
            }else{
                chain.doFilter(servletRequest, servletResponse);
            }
        }

    }

}
