package com.jk51.commons.des;

import javax.crypto.SecretKey;
import java.time.Instant;

/**
 * Created by Administrator on 2017/1/22.
 */
public class EncryptUtilsTest {
    public static void main(String[] args) {
        long start = Instant.now().toEpochMilli();
        String info = "123456";
        try {
            //
            System.out.println("MD5加密：" + EncryptUtils.encryptToMD5(info));  //消耗时间  0.007 sec
            System.out.println("SHA加密：" + EncryptUtils.encryptToSHA(info));   //消耗时间  0.008 sec

            //生成一个DES算法的密钥
            SecretKey key = EncryptUtils.createSecretDESKey(); //36CD68796DB421168885DFA1D7D5FF01F93053B73F50A4DA988C634F6782930C92258471E977C700B104AD3203E7973D
            String str = EncryptUtils.encryptToDES(key, info);
            System.out.println("DES加密后为:" + str); // 耗时  0.377 sec
            //使用这个密钥解密
            System.out.println("DES解密后为:" + EncryptUtils.decryptByDES(key, str));  //耗时 0.38

            //生成一个AES算法的密匙
            SecretKey key1 = EncryptUtils.createSecretAESKey();
            String str1 = EncryptUtils.encryptToAES(key1, info); //耗时 0.382 sec
            System.out.println("AES加密后为:" + str1);
            System.out.println("AES解密后为:" + EncryptUtils.decryptByAES(key1, str1));  //耗时 0.404 sec

            //  指定key进行加解密

            ////   AES
            String AESKey = EncryptUtils.getAESKey(EncryptUtils.encryptToSHA(info));
            System.out.println(AESKey);
            // 生成一个AES算法的密匙
            String strc = EncryptUtils.encryptToAES(AESKey, info);  //耗时 0.197 sec
            System.out.println("AES加密后为:" + strc);
            // 使用这个密匙解密
            String strd = EncryptUtils.decryptByAES(AESKey, strc);  //耗时 0.201 sec
            System.out.println("AES解密后为：" + strd);

            ////   DES
            String DESKey = EncryptUtils.getDESKey(EncryptUtils.encryptToSHA(info));
            System.out.println(DESKey);
            String str11 = EncryptUtils.encryptToDES(DESKey, info);  //耗时 0.198 sec
            System.out.println("DES加密后为:" + str11);
            // 使用这个密匙解密
            String str12 = EncryptUtils.decryptByDES(DESKey, str11);  // 耗时 0.207 sec
            System.out.println("DES解密后为：" + str12);

        } catch (Exception e) {
            e.printStackTrace();
        }


        long end = Instant.now().toEpochMilli();
        System.out.println("消耗时间：" + (end - start) / 1000f + " sec.");

    }
}
