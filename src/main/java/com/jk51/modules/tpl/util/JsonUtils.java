package com.jk51.modules.tpl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * json处理工具类
 */
public class JsonUtils {
    private static JsonUtils instance = new JsonUtils();
    private JsonUtils(){}
    public static JsonUtils getInstance(){
        return instance;
    }
    private ObjectMapper mapper = new ObjectMapper();

    public String objectToJson(Object object) throws IOException {
        return mapper.writeValueAsString(object);
    }

    public <T> T readValue(String jsonStr, Class<T> valueType) throws IOException {
        return mapper.readValue(jsonStr, valueType);
    }

    public static String stripEnd(String str, String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }

        if (stripChars == null) {
            while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                end--;
            }
        }
        return str.substring(0, end);
    }
}
