package com.jk51.commons.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-22
 * 修改记录:
 */
public class MD5Utils {

    /*
 * 加密算法
 * 获取结果大写
 */
    public static String encode(String text){

        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(text.getBytes());
            StringBuilder sb =new StringBuilder();
            for(byte b:result){
                int number = b&0xff;
                String hex = Integer.toHexString(number);
                if(hex.length() == 1){
                    sb.append("0"+hex);
                }else{
                    sb.append(hex);
                }
            }
            //若要小写  去掉toUpperCase() 即可
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }

        return "" ;
    }

    /**
     * 密码加密
     * @param pwd
     * @return
     */
    public static String getPassword(String pwd){
        //加密动作，获取加密的对象
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            //对数据进行加密,加密的动作已经完成
            byte[] bs = digest.digest(pwd.getBytes());

            //对加密后的结果，进行优化，将加密后的结果，先转换成正数，然后，转换成16进制格式
            String password = "";
            for (byte b : bs) {
                //转换成正数
                //b类型byte   进行 & 255 int类型数据，自动类型提升
                //b：1111 1001
                //b转换成int类型之后：0000 0000 0000 0000 0000 0000 1111 1001，最高位变成0之后转换成正数
                int temp = b & 255;
                //转换成16进制格式
                String hexString = Integer.toHexString(temp);
                if(temp >=0 && temp < 16){
                    password = password +"0"+ hexString;
                }else{
                    password = password + hexString;
                }
                //工具类加密的结果：90150983cd24fb0d6963f7d28e17f72
                //工具类加密的结果：900150983cd24fb0d6963f7d28e17f72
                //mysql加密结果： 900150983cd24fb0d6963f7d28e17f72
            }

            return password;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }



}
