package com.jk51.commons.ccprest.result;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import org.apache.commons.lang.UnhandledException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-17
 * 修改记录:发送短信
 */


public class Sms {
    private static final Logger log = LoggerFactory.getLogger(Sms.class);
    //编码格式。发送编码格式统一用UTF-8
    private static String ENCODING = "UTF-8";

    /*
         * @return json格式字符串
         */
    public static int singleSend(String url, Map<String, String> params) {
        int result = -1;
        try {
            result = Integer.parseInt(String.valueOf(JacksonUtils.json2map(OkHttpUtil.postMap(url, params)).get("code")));//发送短信并返回结果
        } catch (Exception e) {
            log.info("信息发送失败{}", e);
        }
        return result;
    }

    public static String post(String url, Map<String, String> paramsMap) {
        return OkHttpUtil.postMap(url, paramsMap);
//        CloseableHttpClient client = HttpClients.createDefault();
//        String responseText = "";
//        CloseableHttpResponse response = null;
//        try {
//            HttpPost method = new HttpPost(url);
//            if (paramsMap != null) {
//                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
//                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
//                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
//                    paramList.add(pair);
//                }
//                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
//            }
//            response = client.execute(method);
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                responseText = EntityUtils.toString(entity, ENCODING);
//            }
//        } catch (Exception e) {
//            log.info("后台请求参数不正确{}", e);
//        } finally {
//            try {
//                response.close();
//            } catch (Exception e) {
//                log.info("后台返回参数请求失败{}", e);
//            }
//        }
//        log.info("responseText{}", responseText);
//        return responseText;
    }

    public static String httpRequest(String requestUrl, String requestMethod) {
        String result = "";
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            //http协议传输
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            //设置请求方式
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();
            //将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            //释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            result = buffer.toString();
        } catch (Exception e) {
            log.error("http调用接口参数不正确httpRequest", e);
        }
        return result;
    }
}
