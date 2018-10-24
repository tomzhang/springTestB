package com.jk51.modules.coupon.utils;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.coupon.constants.CouponConstant;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.coupon.utils.
 * author   :zw
 * date     :2017/6/1
 * Update   :
 */
@Service
public class CouponProcessUtils {

    public List<Map<String, String>> String2List(Object sJson) {
        List<Map<String, String>> mapList = new ArrayList<>();
        try {

            JSONArray jsonArray = new JSONArray(sJson.toString());
            int iSize = jsonArray.length();
            for (int i = 0; i < iSize; i++) {
                Map<String, String> stringMap = new HashMap<>();
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                if (jsonObj.has("meet_num") && jsonObj.has("discount")) {
                    stringMap.put("meet_num", Obj2String(jsonObj.get("meet_num")));
                    stringMap.put("discount", Obj2String(jsonObj.get("discount")));
                    stringMap.put("ladder", Obj2String(jsonObj.get("ladder")));
                } else if (jsonObj.has("meet_num") && jsonObj.has("reduce_price")) {
                    stringMap.put("meet_money", Obj2String(jsonObj.get("meet_money")));
                    stringMap.put("reduce_price", Obj2String(jsonObj.get("reduce_price")));
                    stringMap.put("ladder", Obj2String(jsonObj.get("ladder")));
                } else if (jsonObj.has("meet_money") && jsonObj.has("discount")) {
                    stringMap.put("meet_money", Obj2String(jsonObj.get("meet_money")));
                    stringMap.put("discount", Obj2String(jsonObj.get("discount")));
                    stringMap.put("ladder", Obj2String(jsonObj.get("ladder")));
                } else if (jsonObj.has("meet_money") && jsonObj.has("reduce_price")) {
                    stringMap.put("meet_money", Obj2String(jsonObj.get("meet_money")));
                    stringMap.put("reduce_price", Obj2String(jsonObj.get("reduce_price")));
                    stringMap.put("ladder", Obj2String(jsonObj.get("ladder")));
                }else if(jsonObj.has("distance_meter") && jsonObj.has("reduce_price")){
                    stringMap.put("distance_meter", Obj2String(jsonObj.get("distance_meter")));
                    stringMap.put("reduce_price", Obj2String(jsonObj.get("reduce_price")));
                    stringMap.put("ladder", Obj2String(jsonObj.get("ladder")));
                }else if(jsonObj.has("distance_meter") && jsonObj.has("discount")){
                    stringMap.put("distance_meter", Obj2String(jsonObj.get("distance_meter")));
                    stringMap.put("discount", Obj2String(jsonObj.get("discount")));
                    stringMap.put("ladder", Obj2String(jsonObj.get("ladder")));
                }else if(jsonObj.has("meetNum") && jsonObj.has("sendNum")){
                    stringMap.put("meetNum", Obj2String(jsonObj.get("meetNum")));
                    stringMap.put("sendNum", Obj2String(jsonObj.get("sendNum")));
                    stringMap.put("ladder", Obj2String(jsonObj.get("ladder")));
                }else if(jsonObj.has("meetMoney") && jsonObj.has("sendNum")){
                    stringMap.put("meetMoney", Obj2String(jsonObj.get("meetMoney")));
                    stringMap.put("sendNum", Obj2String(jsonObj.get("sendNum")));
                    stringMap.put("ladder", Obj2String(jsonObj.get("ladder")));
                }
                mapList.add(stringMap);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return mapList;
    }

    private String Obj2String(Object obj) {
        String str = obj == null ? "0" : obj.toString();
        return str;
    }

    public Map<String, Object> String2Map(Object sJson) {
        Map<String, Object> stringMap = new HashMap<>();
        try {
            stringMap = JacksonUtils.json2map(sJson.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringMap;
    }

    public Integer convertInt(String str) {
        if (StringUtils.isBlank(str)) {
            return 0;
        } else {
            return Integer.parseInt(str);
        }
    }
    public Integer convertInt(Object str) {
        if (str == null) {
            return 0;
        } else {
            return Integer.parseInt(str.toString());
        }
    }

}
