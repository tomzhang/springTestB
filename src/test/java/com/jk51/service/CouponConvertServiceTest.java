package com.jk51.service;

import com.jk51.commons.json.JacksonUtils;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

/**
 * filename :com.jk51.goodsService.
 * author   :zw
 * date     :2017/4/19
 * Update   :
 */
public class CouponConvertServiceTest {

    @Test
    public void convertMap(){
        String str = "{\"1006\":\"健胃消食片/为诚\"}";
        try {
            Map map = JacksonUtils.json2map(str);
            Object ss = map.keySet();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test(){
        String str  = String.valueOf(new Date());
    }
}
