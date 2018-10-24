package com.jk51.commons.bar;

import java.time.Instant;

/**
 * Created by Administrator on 2017/1/22.
 */
public class BarCodeUtilsTest {
    public static void main(String[] args) {
       /*String decodeContent = BarCodeUtils.decode(imgPath);
        System.out.println("解码内容如下：" + decodeContent);
        System.out.println("finished zxing EAN-13 decode.");*/
        long start = Instant.now().toEpochMilli();
        String msg1 = "774561324813";


        String msg2 = "8845613248";
        try {
            //生成
            //BarCodeUtils.generateFile_EAN13(msg1);   //消耗0.601 sec - 0.652 sec
            BarCodeUtils.generateFile_Code39(msg2);    //消耗0.566 - 0.6  sec

            //解析
            //String result = BarCodeUtils.decode("E:\\1484630916139.png");  //EAN13 :0.196 sec   Code39 :
            long end = Instant.now().toEpochMilli();
            //System.out.println("解析结果：" + result);
            System.out.println("生成条形码消耗时间：" + (end - start) / 1000f + " sec.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
