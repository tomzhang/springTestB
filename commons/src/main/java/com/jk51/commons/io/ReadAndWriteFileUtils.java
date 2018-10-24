package com.jk51.commons.io;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator on 2017/1/14.
 */
public class ReadAndWriteFileUtils {


    private static String DEFAULT_ENCODING = "UTF-8";

    /**
     * 文本文件的读
     *
     * @param filePath
     */
    public static String readTextFile(String filePath) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader in = null;
        in = new BufferedReader(new FileReader(filePath));
        String str;
        try {
            while ((str = in.readLine()) != null) {
                sb.append(str + "\n");
            }

        } finally {
            in.close();
        }
        return sb.toString();
    }

    /**
     * 文本文件的写入
     *
     * @param filePath
     * @param append
     * @param text
     */
    public static void writeTextFile(String filePath, boolean append, String text) throws Exception {
        if (text == null) {
            return;
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(filePath, append));
        try {
            out.write(text);
        } finally {
            out.close();
        }


    }

    /**
     * 二进制文件的读
     *
     * @param filePath
     * @return
     */
    public static byte[] readBinaryFile(String filePath) throws Exception {
        byte[] data = null;

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath));
        try {
            data = new byte[in.available()];
            in.read(data);
        } finally {
            in.close();
        }
        return data;
    }

    /**
     * 二进制文件的写入
     *
     * @param filePath
     * @param data
     */
    public static void writeBinaryFile(String filePath, byte[] data) throws Exception {
        if (data == null) {
            return;
        }
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));
        try {
            out.write(data);
        } finally {
            out.close();
        }

    }

    /**
     * 加载属性文件并转为map
     */
    public static Map<String, String> loadPropsToMap(String propsPath, String encoding) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        Properties props = loadProps(propsPath, encoding);
        for (String key : props.stringPropertyNames()) {
            map.put(key, props.getProperty(key));
        }
        return map;
    }

    /**
     * 加载属性文件
     *
     * @param propsPath
     * @param encoding
     * @return
     */
    private static Properties loadProps(String propsPath, String encoding) throws Exception {
        Properties props = null;
        InputStream in = null;

        try {
            if (StringUtils.isBlank(propsPath)) {
                throw new IllegalArgumentException();
            }
            String suffix = ".properties";
            if (propsPath.lastIndexOf(suffix) == -1) {
                propsPath += suffix;
            }
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(propsPath);
            if (in != null) {
                props = new Properties();
                if (StringUtils.isBlank(encoding)) {
                    props.load(new InputStreamReader(in, DEFAULT_ENCODING));
                } else {
                    props.load(new InputStreamReader(in, encoding));
                }
            }
            return props;
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
