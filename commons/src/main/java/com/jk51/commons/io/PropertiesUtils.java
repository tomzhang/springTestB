package com.jk51.commons.io;

import org.apache.commons.lang.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件名:com.jk51.commons.io.PropertiesUtils
 * 描述: 属性文件读写工具类
 * 作者: wangzhengfei
 * 创建日期: 2017-01-17
 * 修改记录:
 */
public class PropertiesUtils {

    /**
     * 属性文件缓存，key为文件路径，value为文件键值对集合
     */
    private static final Map<String, Properties> cache = new ConcurrentHashMap<String, Properties>();


    /**
     * 加载/读取属性文件，不提供缓存
     *
     * @param path 属性文件路径
     * @return
     * @throws IOException
     */
    public static Properties load(String path) throws IOException {
        Properties props = new Properties();
        if (StringUtils.isEmpty(path)) {
            throw new IOException("文件路径不能为空.");
        }

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        try {
            props.load(is);
        } finally {
            if (is != null) is.close();
        }
        return props;
    }

    /**
     * 加载并且缓存
     *
     * @param path 属性文件路径
     * @throws IOException
     */
    public static Properties loadAndCache(String path) throws IOException {
        Properties props = load(path);
        cache.put(path, props);
        return (Properties)props.clone();
    }

    /**
     * 重新加载
     *
     * @param path 属性文件路径
     * @throws IOException
     */
    public static void reload(String path) throws IOException {
        loadAndCache(path);
    }

    /**
     * 获取value
     *
     * @param path 属性文件路径
     * @param key  key
     * @return 值字符串
     * @throws IOException
     */
    public static String getValue(String path, String key) throws IOException {
        if (cache.containsKey(path)) {
            return (String) cache.get(path).get(key);
        }
        Properties props = load(path);

        return (String) props.get(key);
    }

    /**
     * 存储文件内容
     *
     * @param path  属性文件路径
     * @param key   需要设置的key
     * @param value 值字符串
     * @throws IOException
     */
    public static void setValue(String path, String key, String value) throws IOException {
        Properties props = cache.get(path);
        if (props == null) {
            props = load(path);
        }
        String filePath = Thread.currentThread().getContextClassLoader().getResource(path).getPath();
        OutputStream os = new FileOutputStream(filePath);
        try {
            props.setProperty(key,value);
            props.store(os, "Program auto write...");
        } finally {
            os.close();
        }
    }

    /**
     * 批量写入属性文件
     *
     * @param path 属性文件路径
     * @param kv kv集合
     * @throws IOException
     */
    public static void batchSetValue(String path, Map<String, String> kv) throws IOException {
        if(kv == null || kv.isEmpty()) return;
        Properties props = cache.get(path);
        if (props == null) {
            props = load(path);
        }
        String filePath = Thread.currentThread().getContextClassLoader().getResource(path).getPath();
        OutputStream os = new FileOutputStream(filePath);
        try {
            Iterator<Map.Entry<String,String>> iterator = kv.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,String> entry = iterator.next();
                props.setProperty(entry.getKey(),entry.getValue());
            }
            props.store(os, "Program batch auto write...");
        } finally {
            os.close();
        }

    }


    /**
     * 移除属性文件中的kv
     * @param path 属性文件路径
     * @param keys 待移除的key集合
     * @throws IOException
     */
    public static void remove(String path,String... keys) throws IOException {
        if(keys == null || keys.length == 0) return;
        Properties props = cache.get(path);
        if (props == null) {
            props = load(path);
        }
        String filePath = Thread.currentThread().getContextClassLoader().getResource(path).getPath();
        OutputStream os = new FileOutputStream(filePath);
        try {
            for(String key : keys){
                props.remove(key);
            }
            props.store(os, "program auto write...");
        } finally {
            os.close();
        }
    }

    /**
     * 清除缓存
     * @param path 缓存文件路径
     */
    public static void clearCache(String path){
        if(cache.get(path) != null){
            cache.get(path).clear();
        }
    }

    /**
     * 清除缓存+属性文件文件内容
     * @param path 缓存文件路径
     */
    public static void clearAll(String path) throws IOException {
        if(cache.get(path) != null){
            cache.get(path).clear();
        }
        String filePath = Thread.currentThread().getContextClassLoader().getResource(path).getPath();
        OutputStream os = new FileOutputStream(filePath);
        os.write(new byte[]{});
        os.close();
    }

}
