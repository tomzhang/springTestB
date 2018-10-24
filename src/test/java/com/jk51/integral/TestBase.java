package com.jk51.integral;

import com.alibaba.fastjson.JSONObject;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-06-22 15:46
 * 修改记录:
 */
public class TestBase {

    public static void main(String[] args) {

        String str = "{\"value\":\"99999\",\"max_num\":\"\",\"add_value\":\"\"}";

        JSONObject jsonObject = JSONObject.parseObject(str);

        Integer i = 10;

        Integer max = jsonObject.getInteger("max_num");

        System.out.println(i % max);

    }

}
