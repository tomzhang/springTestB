package com.jk51.modules.coupon.service;

import org.springframework.stereotype.Service;

/**
 * filename :com.jk51.modules.coupon.service.
 * author   :zw
 * date     :2017/5/22
 * Update   :
 * 优惠券编码加密解密
 */
@Service
public class CouponNoEncodingService {
    private static final long OR_NOT_VALUE = 123123123122123L;

    private String reverse(String s) {
        char[] array = s.toCharArray();
        String reverse = "";
        for (int i = array.length - 1; i >= 0; i--)
            reverse += array[i];
        return reverse;
    }


    public String encryptionCouponNo(String couponNo) {
        String coupon = reverse(couponNo + "1");
        Long aLong = Long.valueOf(coupon);
        return String.valueOf(aLong ^ OR_NOT_VALUE);
    }


    public String decryptionCouponNo(String couponNo) {
        long l = Long.parseLong(couponNo) ^ OR_NOT_VALUE;
        String coupon = reverse(l + "");
        return coupon.substring(0, coupon.length() - 1);
    }

}
