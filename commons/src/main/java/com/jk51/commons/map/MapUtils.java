package com.jk51.commons.map;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chen on 2017/2/16.
 */
public class MapUtils {

    /**
     * 发送远端请求  返回字符串
     * @param path
     * @return
     * @throws IOException
     */
    public static String sentURL(String path) throws IOException {
        InputStream is=null;
            URL url=new URL(path);
            is= url.openStream();
            String result =  readAll(new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8"))));
        is.close();
        return result;
    }

    /**
     * 读取文件流
     * @param rd
     * @return
     * @throws IOException
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


    /**
     * 输入俩点之间的坐标
     * 计算俩点之间直线距离,单位为m
     */
    public static double getDistance(Double longitude1, Double latitude1,Double longitude2, Double latitude2 ) {
        double lat1 = (Math.PI / 180) * latitude1;
        double lat2 = (Math.PI / 180) * latitude2;
        double lon1 = (Math.PI / 180) * longitude1;
        double lon2 = (Math.PI / 180) * longitude2;
        //地球半径
        double R = 6371;
        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;
        return d * 1000;
    }

    /**
     * 将javaBean对象转化成map对象
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if(obj == null)
            return null;

        Map<String, Object> map = new HashMap<String, Object>();

        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            Object value = getter!=null ? getter.invoke(obj) : null;
            map.put(key, value);
        }

        return map;
    }

}
