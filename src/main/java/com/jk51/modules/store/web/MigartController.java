package com.jk51.modules.store.web;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.goods.library.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MigartController {

    private static final Logger logger = LoggerFactory.getLogger(MigartController.class);

    private String TREAT_COMMON_CLASS_APTH = "com.jk51.modules.persistence.mapper.";

    private String TREAT_COMMON_CLASS_MODEL_APTH = "com.jk51.model.order.";


    @PostMapping("migart")
    public Map treatHelper(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        logger.info("####treat####param:{}", JacksonUtils.mapToJson(param));
        Map resultMap = new HashMap();
        try {

            String methodName = param.get("methodName") + "";
            String classNameWrap = param.get("className") + "";
            String paramType = param.get("param_type") + "";
            Object[] argsv;
            if(paramType.equals("arr")){
                argsv = param2arr(JacksonUtils.json2map(param.get("argsv")+""));
            }else{
                String porot = param.get("poro_t") + "";
                String porov = param.get("poro_v") + "";
                argsv = new Object[1];
                argsv[0] = JacksonUtils.json2pojo(porov, Class.forName(TREAT_COMMON_CLASS_MODEL_APTH + porot));
            }

            String className = TREAT_COMMON_CLASS_APTH + classNameWrap;

            Map methodProperties = getMethodProperties(className, methodName);
            String returnType = methodProperties.get("returnType") + "";
            Method method = (Method) methodProperties.get("method");
            Class[] argsType = (Class[]) methodProperties.get("argsType");
            String beanName = classNameWrap.substring(0, 1).toLowerCase() + classNameWrap.substring(1, classNameWrap.length());
            Object resultWrap = ReflectionUtils.invokeMethod(method, SpringContextUtil.getApplicationContext().getBean(classNameWrap), argsv);
            resultMap.put("result", resultWrap);
            resultMap.put("status", "success");
        } catch (Exception e) {
            resultMap.put("status", "error");
            logger.info("####treat####Exception:{}", e);
        }
        return resultMap;
    }

    public Map getMethodProperties(String className, String methodName) {
        Map param = new HashMap();
        try {
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(Class.forName(className));
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
//                    String returnType = method.getReturnType().getName();
//                    Class[] parameterTypes = method.getParameterTypes();
//                    Class[] argsClassType = new Class[parameterTypes.length];
//                    for (int i = 0; i < parameterTypes.length; i++) {
//                        argsClassType[i] = Class.forName(parameterTypes[i].getName());
//                    }
//                    param.put("returnType", returnType);
//                    param.put("argsType", argsClassType);
                    param.put("method", method);
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return param;
    }

    public Object[] param2arr(Map param) {
        Object[] arr;
        try {
            Integer i = Integer.valueOf(param.get("key_length") + "");
            arr = new Object[i];
            for (int m = 0; m < i; m++) {
                if(StringUtil.isEmpty(param.get("key_" + m))){
                    //arr[m] = JacksonUtils.param.get("key_" + m);
                    continue;
                }
                arr[m] = param.get("key_" + m);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new Object[0];
        }
        return arr;
    }
}
