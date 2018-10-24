package com.jk51.modules.merchant.utils;

import com.jk51.communal.exceptionUtil.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：
 * 作者: XC
 * 创建日期: 2018-09-29 14:51
 * 修改记录:
 **/
public class ThreadUtil implements Runnable{
    private Logger logger = LoggerFactory.getLogger(ThreadUtil.class);

    private Class<?> clazz;
    private String methodName;
    private Object[] parameters;

    public ThreadUtil(Class<?> clazz, String methodName, Object[] parameters) {
        this.clazz = clazz;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    @Override
    public void run() {
        Class<?>[] parametersType = null;
        try {
            parametersType = getParametersType(clazz, methodName);
            Method method = clazz.getMethod(methodName,parametersType );
            method.invoke(method, parameters);
        } catch (Exception e) {
            logger.error("调用ThreadUtil类失败，参数clazz:{},methodName:{},parametersType:{},parameters:{},错误信息：{}",
                clazz, methodName, parametersType, parameters, ExceptionUtil.exceptionDetail(e));
        }
    }

   public void stratSendEmailThread(){
       Thread thread = new Thread(new ThreadUtil(clazz,methodName,parameters));
       thread.start();
   }

   public Class<?>[] getParametersType(Class<?> clazz,String methodName) throws Exception{
        Method[] methods = clazz.getMethods();
        Class[] paramTypes = null;
        for (int i = 0; i< methods.length; i++) {
            //和传入方法名匹配
            if(methodName.equals(methods[i].getName())){
                Class[] params = methods[i].getParameterTypes();
                paramTypes = new Class[params.length] ;
                for (int j = 0; j < params.length; j++) {
                    paramTypes[j] = Class.forName(params[j].getName());
                }
                break;
            }
        }
       return paramTypes;
   }
}
