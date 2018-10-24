package com.jk51.commons.random;

import java.util.Random;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 按业务规则生成16位不可重复的优惠券编码
 * 作者: chenpeng
 * 创建日期: 2017/3/2
 * 修改记录:
 */
public class CouponCodeUtil {

    /**
     * 随机数位数
     */
    private static int LENGTH = 12;
    /**
     * 优惠券编号，初始位数4位，后期依次递增
     */
    private static int COUPON_NUMBER = 4;

    /**
     * 生成优惠券编码
     *
     * @param couponId 优惠券id
     * @return
     */
    public static String getCouponCode(String couponId) {

        String couponCodeTwo;
        String couponCodeOne;

        if (couponId.length() >= COUPON_NUMBER) {
            couponCodeOne = getCouponCodeOne(LENGTH + COUPON_NUMBER - couponId.length());
            couponCodeTwo = couponId;
        } else {
            couponCodeOne = getCouponCodeOne(LENGTH);
            couponCodeTwo = getCouponCodeTwo(Integer.parseInt(couponId), COUPON_NUMBER);
        }

        return couponCodeOne + couponCodeTwo;
    }

    /**
     * 生成优惠券的第一部分编码
     *
     * @return
     */
    private static String getCouponCodeOne(int len) {
        StringBuffer buffer = new StringBuffer("0123456789");
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        int range = buffer.length();
        for (int i = 0; i < len; i++) {
            sb.append(buffer.charAt(random.nextInt(range)));
        }
        return sb.toString();
    }

    /**
     * 获取优惠券的第二部分编码
     *
     * @param num       商户编号
     * @param fixdlenth 生成数字的长度
     * @return
     */
    private static String getCouponCodeTwo(long num, int fixdlenth) {
        StringBuffer sb = new StringBuffer();
        String strNum = String.valueOf(num);
        if (fixdlenth - strNum.length() >= 0) {
            sb.append(generateZeroString(fixdlenth - strNum.length()));
        } else {
            throw new RuntimeException("将数字" + num + "转化为长度为" + fixdlenth + "的字符串发生异常！");
        }
        sb.append(strNum);
        return sb.toString();
    }

    /**
     * 生成一个定长的纯0字符串
     *
     * @param length 字符串长度
     * @return 纯0字符串
     */
    private static String generateZeroString(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append('0');
        }
        return sb.toString();
    }



}
