package com.jk51.modules.treat.controller;

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
public class TreatIntranceController {

    private static final Logger logger = LoggerFactory.getLogger(TreatIntranceController.class);

    private String TREAT_COMMON_CLASS_PATH = "com.jk51.modules.treat.mapper.";

    private String TREAT_COMMON_SERVICE_PATH = "com.jk51.modules.treat.service.";

    private String TREAT_COMMON_CLASS_MODEL_PATH = "com.jk51.model.treat.";


    @PostMapping("treat")
    public Map treatHelper(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        logger.info("####treat####param:{}", JacksonUtils.mapToJson(param));
        Map resultMap = new HashMap();
        try {

            String methodName = param.get("methodName") + "";
            String classNameWrap = param.get("className") + "";
            String paramType = param.get("param_type") + "";
            Object[] argsv;
            if (paramType.equals("poro")) {
                String porot = param.get("poro_t") + "";
                String porov = param.get("poro_v") + "";
                argsv = new Object[1];
                String poroPrefix = StringUtil.isEmpty(param.get("classLocal")) ? TREAT_COMMON_CLASS_MODEL_PATH + porot : porot;
                argsv[0] = JacksonUtils.json2pojo(porov, Class.forName(poroPrefix));
            } else {
                argsv = param2arr(JacksonUtils.json2map(param.get("argsv") + ""));
            }

            String className = handleClassLocation(param);

            Map methodProperties = getMethodProperties(className, methodName);
            //String returnType = methodProperties.get("returnType") + "";
            Method method = (Method) methodProperties.get("method");
            //Class[] argsType = (Class[]) methodProperties.get("argsType");

            classNameWrap = StringUtil.isEmpty(param.get("classLocal")) ? classNameWrap : classNameWrap.substring(classNameWrap.lastIndexOf(".") + 1);
            String beanName = classNameWrap.substring(0, 1).toLowerCase() + classNameWrap.substring(1, classNameWrap.length());
            Object bean = SpringContextUtil.getApplicationContext().getBean(beanName);
            bean = StringUtil.isEmpty(bean) ? SpringContextUtil.getApplicationContext().getBean(classNameWrap) : bean;

            Object resultWrap = ReflectionUtils.invokeMethod(method, bean, argsv);
            resultMap.put("result", resultWrap);
            resultMap.put("status", "success");
        } catch (Exception e) {
            resultMap.put("status", "error");
            logger.info("####treat####Exception:{}", e);
        }

        return resultMap;
    }

    public String handleClassLocation(Map param) {
        if (StringUtil.isEmpty(param.get("classLocal"))) {
            String classNameWrap = param.get("className") + "";
            if (classNameWrap.endsWith("Service")) {
                return TREAT_COMMON_SERVICE_PATH + classNameWrap;
            } else {
                return TREAT_COMMON_CLASS_PATH + classNameWrap;
            }
        }
        return param.get("className") + "";
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

            if (param == null) {
                return null;
            }
            if(StringUtil.isEmpty(param.get("key_length"))){
                arr = new Object[1];
                arr[0] = param;
                return arr;
            }

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
