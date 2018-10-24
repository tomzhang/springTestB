package com.jk51.commons.des;

import com.jk51.commons.CommonConstant;
import com.jk51.commons.SCommonConstant;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密算法
 * Created by hulan on 2017/1/17.
 * 常用的加密解密可以分为：信息摘要算法:MD5,SHA(也就是单向加密理论上无法解密)、
 * 对称加密算法 ：DES,3DES,AES、非对称加密算法:RSA,DSA
 */
public class SEncryptUtils {
    /**
     * MD5加密
     *
     * @param info
     * @return
     */
    public static String encryptToMD5(String info) throws NoSuchAlgorithmException {
        byte[] digest = null;

        //得到一个MD5的消息摘要
        MessageDigest md = MessageDigest.getInstance(CommonConstant.MD5);
        //添加要进行计算摘要的信息
        md.update(info.getBytes());
        //得到该摘要
        digest = md.digest();

        return byte2hex(digest);  //将摘要转为字符串
    }

    /**
     * SHA加密
     *
     * @param info
     * @return
     */
    public static String encryptToSHA(String info) throws NoSuchAlgorithmException {
        byte[] digest = null;
        MessageDigest md = MessageDigest.getInstance(SCommonConstant.SHA_1);
        md.update(info.getBytes());
        digest = md.digest();
        return byte2hex(digest);
    }

    /**
     * 根据一定的算法得到相应的key
     *
     * @param algorithm
     * @param src
     * @return
     */
    public static String getKey(String algorithm, String src) {
        if (algorithm.equals(CommonConstant.AES)) {
            return src.substring(0, 16);
        } else if (algorithm.equals(CommonConstant.DES)) {
            return src.substring(0, 8);
        } else {
            return null;
        }
    }

    /**
     * 得到AES加密的key
     *
     * @param src
     * @return
     */
    public static String getAESKey(String src) {
        return getKey(CommonConstant.AES, src);
    }

    /**
     * 得到DES加密的key
     *
     * @param src
     * @return
     */
    public static String getDESKey(String src) {
        return getKey(CommonConstant.DES, src);
    }

    /**
     * 创建密钥
     *
     * @param algorithm
     * @return
     */
    public static SecretKey createSecretKey(String algorithm) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator;
        SecretKey secretKey = null;

        // 返回生成指定算法的秘密密钥的 KeyGenerator 对象
        keyGenerator = KeyGenerator.getInstance(algorithm);
        // 生成一个密钥
        secretKey = keyGenerator.generateKey();

        return secretKey;
    }

    /**
     * 创建一个AES的密钥
     *
     * @return
     */
    public static SecretKey createSecretAESKey() throws NoSuchAlgorithmException {
        return createSecretKey(CommonConstant.AES);
    }

    /**
     * 创建一个DES密钥
     *
     * @return
     */
    public static SecretKey createSecretDESKey() throws NoSuchAlgorithmException {
        return createSecretKey(CommonConstant.DES);
    }

    /**
     * 根据相应的加密算法、密钥、原文件进行加密，返回加密后的文件
     *
     * @param algorithm 加密算法:DES,AES
     * @param key
     * @param info
     * @return
     */
    public static String encrypt(String algorithm, SecretKey key, String info) throws Exception {//
        // 定义要生成的密文
        byte[] cipherByte = null;
        // 得到加密/解密器
        Cipher c1 = Cipher.getInstance(algorithm);
        // 用指定的密钥和模式初始化Cipher对象
        // 参数:(ENCRYPT_MODE, DECRYPT_MODE, WRAP_MODE,UNWRAP_MODE)
        c1.init(Cipher.ENCRYPT_MODE, key);
        // 对要加密的内容进行编码处理,
        cipherByte = c1.doFinal(info.getBytes());

        // 返回密文的十六进制形式
        return byte2hex(cipherByte);
    }

    /**
     * 根据相应的解密算法、密钥和需要解密的文本进行解密，返回解密后的文本内容
     *
     * @param algorithm
     * @param key
     * @param sInfo
     * @return
     */
    public static String decrypt(String algorithm, SecretKey key, String sInfo) throws Exception {
        byte[] cipherByte = null;

        // 得到加密/解密器
        Cipher c1 = Cipher.getInstance(algorithm);
        // 用指定的密钥和模式初始化Cipher对象
        c1.init(Cipher.DECRYPT_MODE, key);
        // 对要解密的内容进行编码处理
        cipherByte = c1.doFinal(hex2byte(sInfo));

        return new String(cipherByte);
    }

    /**
     * 根据相应的解密算法、指定的密钥和需要解密的文本进行加密，返回解密后的文本内容
     *
     * @param algorithm
     * @param sSrc
     * @param sKey
     * @return
     * @throws Exception
     */
    public static String decrypt(String algorithm, String sSrc, String sKey) throws Exception {
        try {
            //判断key是否为空
            if (sKey == null) {
                throw new Exception("Key为空");
            }
            // 判断采用AES加解密方式的Key是否为16位
            if (algorithm.equals(CommonConstant.AES) && sKey.length() != 16) {
                throw new Exception("Key长度不是16位");
            }
            // 判断采用DES加解密方式的Key是否为8位
            if (algorithm.equals(CommonConstant.DES) && sKey.length() != 8) {
                throw new Exception("Key长度不是8位");
            }
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = hex2byte(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据相应的加密算法、指定的密钥、源文件进行加密，返回加密后的文件
     *
     * @param algorithm 加密算法 （DES、AES）
     * @param sSrc
     * @param sKey      这个key可以由用户自己指定 注意AES的长度为16位,DES的长度为8位
     * @return
     * @throws Exception
     */
    public static String encrypt(String algorithm, String sSrc, String sKey) throws Exception {
        // 判断Key是否正确
        if (sKey == null) {
            throw new Exception("Key为空null");
        }
        // 判断采用AES加解密方式的Key是否为16位
        if (algorithm.equals(CommonConstant.AES) && sKey.length() != 16) {
            throw new Exception("Key长度不是16位");
        }
        // 判断采用DES加解密方式的Key是否为8位
        if (algorithm.equals(CommonConstant.DES) && sKey.length() != 8) {
            throw new Exception("Key长度不是8位");
        }
        byte[] raw = sKey.getBytes("ASCII");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());
        return byte2hex(encrypted);
    }

    /**
     * 采用DES随机生成的密钥进行加密
     *
     * @param key
     * @param info
     * @return
     */
    public static String encryptToDES(SecretKey key, String info) throws Exception {
        return encrypt(CommonConstant.DES, key, info);
    }

    /**
     * 采用DES指定密钥的方式进行加密
     *
     * @param key
     * @param info
     * @return
     * @throws Exception
     */
    public static String encryptToDES(String key, String info) throws Exception {
        return encrypt(CommonConstant.DES, info, key);
    }

    /**
     * 采用DES随机生成密钥的方式进行解密，密钥需要与加密的生成的密钥一样
     *
     * @param key
     * @param sInfo
     * @return
     */
    public static String decryptByDES(SecretKey key, String sInfo) throws Exception {
        return decrypt(CommonConstant.DES, key, sInfo);
    }

    /**
     * 采用DES用户指定密钥的方式进行解密，密钥需要与加密时指定的密钥一样
     *
     * @param key
     * @param sInfo
     * @return
     * @throws Exception
     */
    public static String decryptByDES(String key, String sInfo) throws Exception {
        return decrypt(CommonConstant.DES, sInfo, key);
    }

    /**
     * 采用AES随机生成的密钥进行加密
     *
     * @param key
     * @param info
     * @return
     */
    public static String encryptToAES(SecretKey key, String info) throws Exception {
        return encrypt(CommonConstant.AES, key, info);
    }

    /**
     * 采用AES指定密钥的方式进行加密
     *
     * @param key
     * @param info
     * @return
     * @throws Exception
     */
    public static String encryptToAES(String key, String info) throws Exception {
        return encrypt(CommonConstant.AES, info, key);
    }

    /**
     * 采用AES随机生成密钥的方式进行解密，密钥需要与加密的生成的密钥一样
     *
     * @param key
     * @param sInfo
     * @return
     */
    public static String decryptByAES(SecretKey key, String sInfo) throws Exception {
        return decrypt(CommonConstant.AES, key, sInfo);
    }

    /**
     * 采用AES用户指定密钥的方式进行解密，密钥需要与加密时指定的密钥一样
     *
     * @param key
     * @param sInfo
     * @return
     * @throws Exception
     */
    public static String decryptByAES(String key, String sInfo) throws Exception {
        return decrypt(CommonConstant.AES, sInfo, key);
    }

    /**
     * 十六进制字符串转化为二进制
     *
     * @param strhex
     * @return
     */
    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
        }
        return b;
    }

    /**
     * 二进制转化为16进制字符串
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int i = 0; i < b.length; i++) {
            stmp = Integer.toHexString(b[i] & 0XFF);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        //return hs.toUpperCase();
        return hs;
    }
    /**
     * SHA加密不转大写
     *
     * @param info
     * @return
     */
    public static String encryptToSHA1(String info) throws NoSuchAlgorithmException {
        byte[] digest = null;
        MessageDigest md = MessageDigest.getInstance(SCommonConstant.SHA_1);
        md.update(info.getBytes());
        digest = md.digest();
        return byte1hex(digest);
    }
    /**
     * 二进制转化为16进制字符串(不转大写)
     *
     * @param b
     * @return
     */
    public static String byte1hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int i = 0; i < b.length; i++) {
            stmp = Integer.toHexString(b[i] & 0XFF);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }
}
