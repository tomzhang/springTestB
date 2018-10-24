package com.jk51.commons.parameter;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/9.
 */
public class ParameterUtil {
    /**
     * 从request中获得参数Map，并返回可读的Map
     *
     * @param request
     * @return
     */
    public static Map<String,Object> getParameterMap(HttpServletRequest request) {
        // 参数Map
        Map properties = request.getParameterMap();
        // 返回值Map
        Map<String,Object> returnMap = new HashMap<String,Object>();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if(null == valueObj){
                value = "";
            }else if(valueObj instanceof String[]){
                String[] values = (String[])valueObj;
                for(int i=0;i<values.length;i++){
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }
    /**
     * 从request中获得参数Map，并返回可读的Map
     *
     * @param request
     * @return
     */
    public static Map<String,String> getParameterMapString(HttpServletRequest request) {
        // 参数Map
        Map properties = request.getParameterMap();
        // 返回值Map
        Map<String,String> returnMap = new HashMap<String,String>();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if(null == valueObj){
                value = "";
            }else if(valueObj instanceof String[]){
                String[] values = (String[])valueObj;
                for(int i=0;i<values.length;i++){
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }
    public static String ObjectConvertJson(Object rule) {
        ObjectMapper jsonStu = new ObjectMapper();
        String str = "";
        try {
            str = jsonStu.writeValueAsString(rule);
        } catch (Exception e) {
        }
        return str;
    }
}
