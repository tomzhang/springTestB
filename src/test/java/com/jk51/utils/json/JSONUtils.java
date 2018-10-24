package com.jk51.utils.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * 用来测试的jsonUtils
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/9/11                                <br/>
 * 修改记录:                                         <br/>
 */
public class JSONUtils {
    private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    /**
     * @param json  类型为{@link JSONObject}或者是{@link JSONArray}
     * @return
     */
    public static void convert(Object json) {
        if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            for (Object o : jsonArray) {
                convert(o);
            }
        } else if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            Set<String> keys = jsonObject.keySet();

            String[] array = new String[keys.size()];
            int n = 0;
            for(String key : keys) array[n++] = key;

            for (String key : array) {
                Object value = jsonObject.get(key);
                jsonObject.remove(key);
                convert(value);
                jsonObject.put(underlineToCamel(key), value);
            }
        }
    }

    private static String underlineToCamel(String key) {
        String[] split = key.split("_");
        String result = Arrays.stream(split)
                .filter(Objects::nonNull)
                .reduce("", (s, s2) -> {
                    if (s2.matches("^[a-z][a-z0-9]*$")) {
                        return s.concat(s2.substring(0, 1).toUpperCase() + s2.substring(1));
                    } else {
                        return s.concat(s2);
                    }
                });
        return result.substring(0, 1).toLowerCase() + result.substring(1);
    }
}
