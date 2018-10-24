package com.jk51.commons.random;

import java.util.Random;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:  生成随机数字串
 * 作者: hulan
 * 创建日期: 2017-02-16
 * 修改记录:
 */
public class RandomUtils {
    //生成唯一随机num位随机数字串
    public static String randomSet(int num) {
        Random rd = new Random();
        String n = "";
        int getNum;
        do {
            getNum = Math.abs(rd.nextInt()) % 10 + 48;//产生数字0-9的随机数
            char num1 = (char) getNum;
            String dn = Character.toString(num1);
            n += dn;
        } while (n.length() < num);
        return n + "";
    }
}
