package com.jk51.modules.im.netease;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/8/31
 * 修改记录:
 */
public class CheckSumBuilder {

    private static Logger logger = LoggerFactory.getLogger(CheckSumBuilder.class);

    public static String getCheckSum(String appSecret, String nonce, String curTime){
        return encode("sha1", appSecret + nonce + curTime);
    }

    public static String getMD5(String requestBody){
        return encode("md5", requestBody);
    }

    private static String encode(String algorithm,String value){

        if(value == null){
            return null;
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(value.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFormattedText(byte[] bytes){
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
}
