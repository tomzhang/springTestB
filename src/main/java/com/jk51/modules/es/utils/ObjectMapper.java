package com.jk51.modules.es.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ObjectMapper {

    static {
        ConvertUtils.register(new DateConverter(null), Date.class);
    }

    public static <T> T ConvertObject(Class<T> type, Map<String,SearchHitField> fieldMap) throws Exception{
        Object obj = type.newInstance();
        for(String fieldName:fieldMap.keySet()){
            SearchHitField hitField = fieldMap.get(fieldName);
            BeanUtils.setProperty(obj,fieldName,hitField.getValue());
        }

        return (T)obj;
    }
    
    public static <T> T ConvertToObject(Class<T> type, Map<String, Object> fieldMap) throws Exception {
        Object obj = type.newInstance();
        for(String fieldName:fieldMap.keySet()){
    		Object hitField = fieldMap.get(fieldName);
            try {
				BeanUtils.setProperty(obj,fieldName,hitField);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
        }
        return (T)obj;
    }

    public static <T> ArrayList<T> ConvertObjectArray(Class<T> type, SearchResponse responseList) throws Exception{

        List list=new ArrayList<T>();
        for(SearchHit hit : responseList.getHits()){
            list.add(ObjectMapper.ConvertObject(type,hit.getFields()));
        }
        return (ArrayList<T>)list;
    }
    
    public static Object ConvertObjectFields(Object obj){
    	
    	if (obj instanceof Integer) {
			return obj == null ? 0 : obj;
		} else if(obj instanceof String && null != obj){
			return obj.toString().trim();
		} else{
			return obj == null ? "" : obj;
		}
    }
}
