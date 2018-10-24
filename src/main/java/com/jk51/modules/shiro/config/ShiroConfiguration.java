package com.jk51.modules.shiro.config;

import com.jk51.modules.shiro.MShiroFilterFactoryBean;
import com.jk51.modules.shiro.MyShiroRealm;
import com.jk51.modules.shiro.ShiroCacheManager;
import com.jk51.session.ShiroSessionDao;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-23
 * 修改记录:
 */
@Configuration
public class ShiroConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ShiroConfiguration.class);


    @Bean(name = "myShiroRealm")
    public MyShiroRealm getShiroRealm() {
        MyShiroRealm realm = new MyShiroRealm();
        realm.setCacheManager(getCacheManage());
        return realm;
    }

    @Bean(name = "cacheShiroManager")
    public CacheManager getCacheManage() {

        EhCacheManager ehCacheManager =  new EhCacheManager();
//        ehCacheManager.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return ehCacheManager;
    }


    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    @Bean
    public CookieRememberMeManager getRememberManager(){
        CookieRememberMeManager meManager = new CookieRememberMeManager();
        meManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        meManager.setCookie(getRememberMeCookie());
        return meManager;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(MyShiroRealm myShiroRealm) {
//        DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
//        dwsm.setRealm(myShiroRealm);
////      <!-- 用户授权/认证信息Cache, 采用EhCache 缓存 -->
//        dwsm.setCacheManager(cacheManager);


        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setCacheManager(getCacheManage());
        securityManager.setSessionManager(getSessionManage());
        securityManager.setRememberMeManager(getRememberManager());
        securityManager.setRealm(getShiroRealm());
        return securityManager;

    }

    @Bean(name = "sessionValidationScheduler")
    public ExecutorServiceSessionValidationScheduler getExecutorServiceSessionValidationScheduler() {
        ExecutorServiceSessionValidationScheduler scheduler = new ExecutorServiceSessionValidationScheduler();
        scheduler.setInterval(900000);
        return scheduler;
    }

    @Bean(name = "sessionIdCookie")
    public SimpleCookie getSessionIdCookie() {
        SimpleCookie cookie = new SimpleCookie("sid");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        return cookie;
    }

    @Bean(name = "rememberMeCookie")
    public SimpleCookie getRememberMeCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(2592000);
        return simpleCookie;
    }

    @Bean(name = "sessionDao")
    public ShiroSessionDao getShiroSessionDao(){
        return new ShiroSessionDao();
    }

    @Bean(name = "sessionManager")
    public DefaultWebSessionManager getSessionManage() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(1800000);
        sessionManager.setSessionValidationScheduler(getExecutorServiceSessionValidationScheduler());
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(getSessionIdCookie());
        ShiroSessionDao shiroSessionDao = getShiroSessionDao();
//        shiroSessionDao.setCacheManager(getCacheManage());
//        EnterpriseCacheSessionDAO cacheSessionDAO = new EnterpriseCacheSessionDAO();
//        cacheSessionDAO.setCacheManager(getCacheManage());
        sessionManager.setSessionDAO(shiroSessionDao);
        // -----可以添加session 创建、删除的监听器

        return sessionManager;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(securityManager);
        return aasa;
    }

    private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean/*, Objects stuService, Objects scoreDao*/){
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // authc：
        filterChainDefinitionMap.put("/user", "authc");// 测试
        // anon：它对应的过滤器里面是空的,什么都没做
//        log.info("######从数据库读取权限规则，加载到shiroFilter中######");
        filterChainDefinitionMap.put("/user/edit/**", "authc,perms[user:edit]");// 测试

        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/**", "anon");//anon

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    }

    /**
     * @return
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager/*, Objects Service, Objects score*/) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new MShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 登录成功后要跳转的连接
        shiroFilterFactoryBean.setSuccessUrl("/user");
        //未通过认跳转的页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");

        loadShiroFilterChain(shiroFilterFactoryBean/*, Service, score*/);
        return shiroFilterFactoryBean;
    }

//    @Bean(name = "shiroDialect")
//    public ShiroDialect shiroDialect(){
//        return new ShiroDialect();
//    }



}
