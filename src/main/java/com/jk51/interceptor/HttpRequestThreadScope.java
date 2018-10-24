package com.jk51.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件名:com.jk51.interceptor.ThreadScope
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-02-03
 * 修改记录:
 */
public class HttpRequestThreadScope {

    private final static ThreadLocal<Map<String,Object>> threadLocal = new ThreadLocal<Map<String,Object>>();

    public static void set(HttpServletRequest request, HttpServletResponse response){
        if(threadLocal.get() == null){
            Map<String,Object> map = new HashMap<String,Object>();
            threadLocal.set(map);
        }
        threadLocal.get().put("request",request);
        threadLocal.get().put("response",response);
    }

    public static void setRequest(HttpServletRequest request){
        if(threadLocal.get() == null){
            Map<String,Object> map = new HashMap<String,Object>();
            threadLocal.set(map);
        }
        threadLocal.get().put("request",request);
    }

    public static void setResponse(HttpServletResponse response){
        if(threadLocal.get() == null){
            Map<String,Object> map = new HashMap<String,Object>();
            threadLocal.set(map);
        }
        threadLocal.get().put("response",response);
    }

    public static HttpServletRequest getRequest(){
        if(threadLocal.get() == null) return null;
        return (HttpServletRequest)threadLocal.get().get("request");
    }

    public static HttpServletResponse getResponse(){
        if(threadLocal.get() == null) return null;
        return (HttpServletResponse)threadLocal.get().get("response");
    }


    public static void remove(){
        threadLocal.remove();
    }

}
