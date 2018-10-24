package com.jk51.map;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.jk51.commons.json.JacksonUtils;

/**
 * Created by chen on 2017/2/16.
 */
public class Map {

    public static void main(String[] args){

        String json = "{\"status\":\"1\",\"info\":\"OK\",\"infocode\":\"10000\",\"results\":[{\"origin_id\":\"1\",\"dest_id\":\"1\",\"distance\":\"15133\",\"duration\":\"1800\"}]}";
        try {
            java.util.Map<String, Object> map = JacksonUtils.json2map(json);
            JSONObject jsonObject = new JSONObject(map);
            Object obj = jsonObject.get("origin_id");
            System.out.println(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
