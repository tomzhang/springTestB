package com.jk51.modules.faceplusplus.service;

import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtil {

    public static String getImageStrFromUrl(String imgURL) {
        byte[] data = null;
        try {
            URL url = new URL(imgURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream inStream = conn.getInputStream();
            data = readInputStream(inStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        BASE64Encoder encoder = new BASE64Encoder();
//        return encoder.encode(data);
        return Base64Util.encode(data);
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    public static String getImageStr(String imgPath) {

        byte[] data = null;
        try {
            InputStream in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

}
