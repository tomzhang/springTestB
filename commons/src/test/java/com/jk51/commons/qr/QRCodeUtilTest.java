package com.jk51.commons.qr;

import java.time.Instant;

/**
 * Created by Administrator on 2017/1/22.
 */
public class QRCodeUtilTest {
    public static void main(String[] args) throws Exception {
        long start = Instant.now().toEpochMilli();
        String text = "http://www.baidu.comsdfgdsafgsdfaewgfeagretgfe放假啊十分丰厚跟阿尔法hi暗访那时快夫妇IE否认南京市快递费了健康就好开奖号2";
        try {
            //不含Logo
            //QRCodeUtil.encode(text, null, "e:\\", true);  // 消耗时间  0.172 sec
            //含Logo，不指定二维码图片名
            // QRCodeUtil.encode(text, "E:\\11.jpg", "e:\\", true);   // 消耗时间  0.299 sec
            //含Logo，指定二维码图片名
            //QRCodeUtil.encode(text, "E:\\11.jpg", "e:\\", "userScenarios", true);  // 消耗时间 0.296 sec

            //解析二维码

            String resultStr = QRCodeUtil.decode("E:\\1484624539923.jpg");  //解码消耗 0.202 sec
            System.out.println(resultStr);
            long end = Instant.now().toEpochMilli();
            System.out.println("生成二维码消耗时间：" + (end - start) / 1000f + " sec.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
