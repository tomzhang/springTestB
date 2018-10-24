package com.jk51.commons.okhttp;


import com.github.pagehelper.util.StringUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class OkHttpUtil
{
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final Logger log = LoggerFactory.getLogger(OkHttpUtil.class);
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .build();

    public static String postJson(String url, String json) {
        if(!StringUtil.isEmpty(json)&&json.length()<200){
            log.info("调用第三方接口开始，URL：{},参数：{}",url,json);
        }
        String bodyStr="";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        Call call=null;
        try {
            call=client.newCall(request);
            response = call.execute();
            bodyStr= response.body().string();
        } catch (IOException e) {
            log.info("请求失败----------------url:{},参数：{}"+e,url,json);
        }finally {
            call.cancel();
        }
        if(!StringUtil.isEmpty(bodyStr)&&bodyStr.length()<200){
            log.info("调用第三方接口结束，url:{},json:{},返回结果：{}",url,json,bodyStr);
        }
        return bodyStr;
    }
    public static String postJson(String url, String json,Map headerMap) {
        log.info("调用第三方接口开始，URL：{},参数：{}",url,json);
        String bodyStr="";
        RequestBody body = RequestBody.create(JSON, json);
        Headers headers =Headers.of(headerMap);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(headers)
                .build();
        Response response = null;
        Call call=null;
        try {
            call=client.newCall(request);
            response = call.execute();
            bodyStr= response.body().string();
        } catch (IOException e) {
            log.info("请求失败----------------url:{},参数：{}"+e,url,json);
        }finally {
            call.cancel();
        }
        log.info("调用第三方接口结束，url:{},json:{},返回结果：{}",url,json,bodyStr);
        return bodyStr;
    }
    public static String postMap(String url, Map map,Map headerMap) {
        log.info("调用第三方接口开始，URL：{},参数：{}",url,map);
        String bodyStr="";
        Response response = null;
        Call call=null;
        try {
            FormBody.Builder builder=new FormBody.Builder();
            if(map!=null&&map.size()!=0){
                for (Object obj : map.keySet()) {
                    if(map.get(obj)==null){
                        //builder.add(obj+"",null);
                    }else {
                        builder.add(obj+"",map.get(obj).toString());
                    }
                }
            }
            RequestBody body = builder.build();
            Headers headers =Headers.of(headerMap);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .headers(headers)
                    .build();
            call=client.newCall(request);
            response = call.execute();
            bodyStr= response.body().string();
        } catch (IOException e) {
            log.info("请求失败----------------url:{},参数：{}"+e,url,map);
        }finally {
            call.cancel();
        }
        log.info("调用第三方接口结束，返回结果：{}",bodyStr);
        return bodyStr;
    }
    public static String postMap(String url, Map map) {
        log.info("调用第三方接口开始，URL：{},参数：{}",url,map);
        String bodyStr="";
        Response response = null;
        Call call=null;
        try {
            FormBody.Builder builder=new FormBody.Builder();
            if(map!=null&&map.size()!=0){
                for (Object obj : map.keySet()) {
                    if(map.get(obj)==null){
                        //builder.add(obj+"",null);
                    }else {
                        builder.add(obj+"",map.get(obj).toString());
                    }
                }
            }
            RequestBody body = builder.build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            call=client.newCall(request);
            response = call.execute();
            bodyStr= response.body().string();
        } catch (IOException e) {
            log.info("请求失败----------------url:{},参数：{}"+e,url,map);
        }finally {
            call.cancel();
        }
        log.info("调用第三方接口结束，返回结果：{}",bodyStr);
        return bodyStr;
    }
    public static String get(String url) {
        log.info("调用第三方接口开始，URL：{}",url);
        String bodyStr="";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        Call call=null;
        try {
            call=client.newCall(request);
            response = call.execute();
            bodyStr= response.body().string();
        } catch (IOException e) {
            log.info("请求失败----------------url:{}"+e,url);
        }finally {
            call.cancel();
        }
        log.info("调用第三方接口结束，返回结果：{}",bodyStr);
        return bodyStr;
    }
    /*public static String getTimeUlr(String url) {
        log.info("调用第三方接口开始，URL：{}",url);
        client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .build();
        String bodyStr="";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        Call call=null;
        try {
            call=client.newCall(request);
            response = call.execute();
            bodyStr= response.body().string();
        } catch (IOException e) {
            log.info("请求失败----------------url:{}"+e,url);
        }finally {
            call.cancel();
        }
        log.info("调用第三方接口结束，返回结果：{}",bodyStr);
        return bodyStr;
    }*/

    public static String getMap(String url, Map map) {
        log.info("调用第三方接口开始，URL：{},参数：{}",url,map);
        String bodyStr="";
        Response response = null;
        Call call=null;
        try {
            FormBody.Builder builder=new FormBody.Builder();
            if(map!=null&&map.size()!=0){
                int i=0;
                for (Object obj : map.keySet()) {
                    if(map.get(obj)==null){
                        //builder.add(obj+"",null);
                    }else {
                        if(i==0){
                            url+="?";
                        }else {
                            url+="&";
                        }
                        //builder.add(obj+"",map.get(obj).toString());
                        url=url+obj+"="+map.get(obj);
                        i++;
                    }
                }
            }
            RequestBody body = builder.build();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            call=client.newCall(request);
            response = call.execute();
            bodyStr= response.body().string();
        } catch (IOException e) {
            log.info("请求失败----------------url:{},参数：{}"+e,url,map);
        }finally {
            call.cancel();
        }
        log.info("调用第三方接口结束，返回结果：{}",bodyStr);
        return bodyStr;
    }
    public static String getList(String url,  List list) {
        log.info("调用第三方接口开始，URL：{},参数：{}",url,list);
        String bodyStr="";
        String json= list.toString();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .get().put(body)
                .build();
        Response response = null;
        Call call=null;
        try {
            call=client.newCall(request);
            response = call.execute();
            bodyStr= response.body().string();
        } catch (IOException e) {
            log.info("请求失败----------------url:{},参数：{}"+e,url,list);
        }finally {
            call.cancel();
        }
        log.info("调用第三方接口结束，返回结果：{}",bodyStr);
        return bodyStr;
    }

    public static File postFile(String url) {
        log.info("调用第三方接口开始，URL：{},",url);
        String bodyStr="";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        Call call=null;
        File file = new File("../report");
        try {
            call=client.newCall(request);
            response = call.execute();
            InputStream in = response.body().byteStream();

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[8096];
            int size = 0;
            while ((size = in.read(buf)) != -1)
                fos.write(buf, 0, size);
            fos.close();

        } catch (IOException e) {
            log.info("请求失败----------------url:{}"+e,url);
        }finally {
            call.cancel();
        }
        log.info("调用第三方接口结束，返回结果：{}",file);
        return file;
    }
    public static void download(String url) {
        log.info("调用第三方接口开始，URL：{},",url);
        String bodyStr="";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        Call call=null;
        File file = new File("../report");
        try {
            call=client.newCall(request);
            response = call.execute();
            InputStream in = response.body().byteStream();

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[8096];
            int size = 0;
            while ((size = in.read(buf)) != -1)
                fos.write(buf, 0, size);
            in.close();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            log.info("请求失败----------------url:{}"+e,url);
        }finally {
            call.cancel();
        }
        log.info("调用第三方接口结束，返回结果");
    }
    public static String multipartHttpPost(String url, MultipartFile file, String filename, String name, Map<String,Object> param) {
        log.info("调用第三方接口开始，URL：{},",url);
        String bodyStr="";
        Call call=null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            InputStream input= file.getInputStream();
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            RequestBody body=RequestBody.create(JSON,output.toByteArray());
            Request request = new Request.Builder()
                    .url(url).post(body)
                    .build();
            Response response = null;
            call=client.newCall(request);
            response = call.execute();
            bodyStr = response.body().string();


        } catch (IOException e) {
            log.info("请求失败----------------url:{}"+e,url);
        }finally {
            call.cancel();
        }
        log.info("调用第三方接口结束，返回结果"+bodyStr);
        return bodyStr;
    }
}

