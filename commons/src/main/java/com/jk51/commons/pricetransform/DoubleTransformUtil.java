package com.jk51.commons.pricetransform;

import com.jk51.commons.string.StringUtil;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 浮点数转换工具类
 *
 * @auhter zy
 * @create 2018-02-27 16:46
 */
public class DoubleTransformUtil {

    /**
     * string类型的浮点数
     * @param str1
     * @param num2  倍数
     * @return
     */
    public static BigDecimal multiplystr(String str1, int num2) {
        if (StringUtil.isNotEmpty(str1)) {

//            BigDecimal bd = BigDecimal.valueOf(Double.parseDouble(str1));
            BigDecimal bd = new  BigDecimal(str1);
            return bd.multiply(new BigDecimal(num2));
        }else {
            return null;
        }

    }

    /**
     * double类型形参
     * @param dou1
     * @param num2 倍数
     * @return
     */
    public static BigDecimal multiplyDou(Double dou1, int num2) {
        if (null != dou1) {
            return new BigDecimal(dou1).multiply(new BigDecimal(num2));
        }else {
            return null;
        }

    }


}
