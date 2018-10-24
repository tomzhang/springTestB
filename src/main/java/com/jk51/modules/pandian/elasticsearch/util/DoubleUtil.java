package com.jk51.modules.pandian.elasticsearch.util;

import com.jk51.commons.string.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/6/13
 * 修改记录:
 */
public class DoubleUtil {


    public static Double round(Double value,int scale){

        if(value==null){
            return null;
        }
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }

        return new BigDecimal(value).setScale(scale,RoundingMode.HALF_UP).doubleValue();
    }
}
