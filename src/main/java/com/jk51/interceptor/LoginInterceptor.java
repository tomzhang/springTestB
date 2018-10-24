package com.jk51.interceptor;

import com.jk51.annotation.LoginRequired;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件名:com.jk51.interceptor.LoginInterceptor
 * 描述: 登录拦截，AOP示例
 * 作者: wangzhengfei
 * 创建日期: 2017-01-16
 * 修改记录:
 */
@Aspect
@Component
public class LoginInterceptor {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    /**
     * 是否启用拦截
     */
    @Value("${login.interceptorEnable}")
    private boolean interceptorEnable;

    /**
     * 没有登录重定向的路径
     */
    @Value("${login.unloginRedirectPath}")
    private String unloginRedirectPath;

    @Value("${login.redirectType}")
    private String redirectType;

    /**
     * Redis缓存模板
     */
    @Autowired
    private StringRedisTemplate template;



    /**
     * 定义拦截规则：拦截com.jk51.web.controller包下面的所有类中，有@LoginRequired注解的方法。
     */
    @Pointcut("execution(* com.jk51.**.**.controller..*(..)) && @annotation(com.jk51.annotation.LoginRequired)")
    public void LoginInterceptor() {
        logger.info("加载登录控制拦截器[{}]",this.getClass().getName());
    }


    public static final Map<String,Boolean> cache = new ConcurrentHashMap<String,Boolean>();

    /**
     * 环绕逻辑控制
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("LoginInterceptor()")
    public Object Interceptor(ProceedingJoinPoint point) throws Throwable {
        //如果禁用则直接跳过登录检查逻辑
        if(!interceptorEnable){
            return point.proceed();
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod(); //获取被拦截的方法
        if(logger.isDebugEnabled()){
            logger.debug("拦截到方法名称:{}.{}",method.getDeclaringClass().getName(),method.getName());
        }
        //如果不需要拦截
        if(!resolve(method)){
            return point.proceed();
        }
        HttpServletRequest request = HttpRequestThreadScope.getRequest();
        HttpServletResponse response = HttpRequestThreadScope.getResponse();
        //如果不是http请求
        if(request == null || response == null) return point.proceed();
        //如果已经登录
        if(loginCheck(request)){
            return point.proceed();
        }
        logger.info("current session["+request.getSession().getId()+"] has not login.");
        //此处只处理了页面请求，没有处理Ajax请求的返回
        RedirectType rt = RedirectType.of(redirectType);
        if(rt == RedirectType.FORWARD){
            request.getRequestDispatcher(unloginRedirectPath).forward(request,response);
        }else if(rt == RedirectType.REDIRECT){
            response.sendRedirect(unloginRedirectPath);
        }else{
            logger.error("配置错误，为空或不能识别的跳转方式:{}",redirectType);
            response.sendRedirect("/error");
        }
        return null;
    }

    /**
     * 解析并缓存
     * @param method
     * @return
     */
    public boolean resolve(Method method){
        //统一使用method.toString()作为缓存的key
        String key = getKey(method);
        if(cache.containsKey(key)){
            return cache.get(key).booleanValue();
        }
        Annotation[] annotations = method.getDeclaredAnnotations();
        Boolean required = getRequiredValue(annotations);
        if(required == null){
            annotations = method.getDeclaringClass().getDeclaredAnnotations();
            required = getRequiredValue(annotations);
        }
        //如果没有LoginRequired注解或者非必须登录则直接跳过
        if(required == null || !required.booleanValue()){
            cache.put(key,false);
            return false;
        }else{
            cache.put(key,true);
            return true;
        }
    }

    /**
     * 获取方法缓存的key
     * @param method
     * @return
     */
    public String getKey(Method method){
        //统一使用method.toString()作为缓存的key
        return method.toString();
    }

    /**
     * 判断是否含有LoginRequired注解RequiredValue
     * @param annotations 某个方法或类上的注解集合
     * @return 如果没有加此注解范围Null，否则返回相应的RequiredValue
     */
    private Boolean getRequiredValue(Annotation[] annotations){
        for(Annotation annotation : annotations){
            if(annotation.annotationType().getName().equals("com.jk51.annotation.LoginRequired")){
                LoginRequired required = (LoginRequired)annotation;
                return required.required();
            }
        }
        return null;
    }


    /**
     * 登录检查
     * @param request
     * @return
     */
    private boolean loginCheck(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return session.getAttribute(session.getId()) != null;
    }

}
