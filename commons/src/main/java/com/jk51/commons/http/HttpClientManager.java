package com.jk51.commons.http;


import com.jk51.commons.string.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 * {@link HttpClient}
 *
 * @deprecated
 */
public class HttpClientManager {
    private static final Logger log = LoggerFactory.getLogger(HttpClientManager.class);

    private static PoolingHttpClientConnectionManager connMgrPool;

    private static void init() {
        if (connMgrPool == null) {
            connMgrPool = new PoolingHttpClientConnectionManager();
            connMgrPool.setMaxTotal(200);// 连接池最大连接数
            connMgrPool.setDefaultMaxPerRoute(50);// 每个路由最大连接数，默认是2
        }
    }

    /**
     * 通过连接池获取HTTPclient
     *
     * @return
     *
     * @deprecated
     */
    private static CloseableHttpClient getHttpClient() {
        init();
        return HttpClients.custom().setConnectionManager(connMgrPool).build();
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private static CloseableHttpClient getHttpsClient(InputStream is, String password) throws IOException {
        SSLContext sslcontext = null;
        try {
            if(is == null) {
                sslcontext = SSLContexts.custom()
                        .loadTrustMaterial(new TrustStrategy() {
                            @Override
                            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                return true;
                            }
                        })
                        .build();
            } else {
                KeyStore keyStore  = KeyStore.getInstance("PKCS12");
                keyStore.load(is, password.toCharArray());
                sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, password.toCharArray())
                        .build();
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "TLSv1" },
                null,
                new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
        return httpclient;
    }

    /**
     * 处理https post请求
     * @param url
     * @param map
     * @param charset
     * @return
     */
    public static String doHttpsPost(String url, Map<String,String> map, String charset, InputStream is, String password) throws IOException{
        if(StringUtil.isEmpty(charset))
            charset = "UTF-8";
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
        }
        if(list.size() > 0){
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
            httpPost.setEntity(entity);
        }
        return getResponseString(getHttpsResult(httpPost,is,password),charset);
    }

    public static String doHttpsPost(String url, String str, String charset, InputStream is, String password) throws IOException{
        if(StringUtil.isEmpty(charset))
            charset = "UTF-8";
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(str, charset);
        httpPost.setEntity(stringEntity);
        return getResponseString(getHttpsResult(httpPost,is,password),charset);
    }

    /**
     * Http GET请求，不带任何参数
     * @param url
     * @return
     */
    public static CloseableHttpResponse httpGetRequest(String url) throws IOException {
        HttpGet httpGet =  new HttpGet(url);
        return getResult(httpGet);
    }

    /**
     * Http GET请求，携带请求参数
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     */
    public static CloseableHttpResponse httpGetRequest(String url,Map<String, Object> params) throws URISyntaxException, IOException {
        URIBuilder uri = new URIBuilder();
        uri.setPath(url);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        uri.setParameters(pairs);
        HttpGet httpGet = new HttpGet(uri.build());
        return getResult(httpGet);
    }

    /**
     * Http GET请求，携带头信息和参数
     * @param url
     * @param headers
     * @param params
     * @return
     * @throws URISyntaxException
     */
    public static CloseableHttpResponse httpGetRequest(String url, Map<String, Object> headers, Map<String, Object> params) throws URISyntaxException, IOException {
        URIBuilder uri = new URIBuilder();
        uri.setPath(url);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        uri.setParameters(pairs);
        HttpGet httpGet = new HttpGet(uri.build());
        for(Map.Entry<String, Object> param : headers.entrySet()){
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return getResult(httpGet);
    }

    /**
     * Http POST请求，不携带消息体
     * @param url
     * @return
     */
    public static CloseableHttpResponse httpPostRequest(String url) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        return getResult(httpPost);
    }

    /**
     * Http POST请求，字符串参数
     * @param url
     * @param str
     * @return
     */
    public static String httpPostRequest(String url, String str) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(str, "UTF-8");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = getResult(httpPost);
        String result = getResponseString(response,"UTF-8");
        return result;
    }
    /**
     * Http POST请求，字符串参数
     * @param url
     * @param str
     * @return
     */
    public static String httpPostRequestUtf8(String url, String str) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
        StringEntity stringEntity = new StringEntity(str, "UTF-8");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = getResult(httpPost);
        String result = getResponseString(response,"UTF-8");
        return result;
    }
    /**
     * 获取返回字符串
     * @param response
     * @return
     * @throws IOException
     */
    public static String getResponseString(CloseableHttpResponse response, String charset) throws IOException {
        if(StringUtil.isEmpty(charset))
            charset = "UTF-8";
        String result = null;
        try {
            // 得到返回对象
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 获取返回结果
                result = EntityUtils.toString(entity,charset);
            }
        } catch (IOException e) {
            log.error("IOException", e);
            throw e;
        } finally {
            // 关闭到客户端的连接
            try {
                if(response != null){
                    response.close();
                }
            } catch (IOException e) {
                log.error("IOException", e);
                throw e;
            }
        }
        return result;
    }


    /**
     * Http POST请求，携带请求消息体
     * @param url
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static CloseableHttpResponse httpPostRequest(String url,Map<String, Object> params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        String jsonValue = covertParams2JSON(params);
        StringEntity stringEntity = new StringEntity(jsonValue, "UTF-8");
        httpPost.setEntity(stringEntity);
        httpPost.setHeader("Content-Type", "application/json");
        return getResult(httpPost);
    }

    /**
     * Http POST请求，携带请求消息体,请求头参数
     * @param url
     * @param params
     * @param headerMap
     * @return
     * @throws UnsupportedEncodingException
     */
    public static CloseableHttpResponse httpPostRequest(String url,Map<String, Object> params,Map<String,String> headerMap) throws IOException {
        HttpPost httpPost = new HttpPost(url);

        //添加请求头
        if(headerMap !=null && headerMap.entrySet().size()>0){
            for(Map.Entry<String, String> entry : headerMap.entrySet()){
                httpPost.addHeader(entry.getKey(),entry.getValue());
            }
        }
        String jsonValue = covertParams2JSON(params);
        StringEntity stringEntity = new StringEntity(jsonValue, "UTF-8");
        httpPost.setEntity(stringEntity);
        return getResult(httpPost);
    }

    /**
     * Http POST请求，携带消息头和请求消息体
     * @param url
     * @param contentType
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static CloseableHttpResponse httpPostRequest(String url,String contentType,Map<String, Object> params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        String jsonValue = covertParams2JSON(params);
        StringEntity stringEntity = new StringEntity(jsonValue, "UTF-8");
        stringEntity.setContentType(contentType);
        httpPost.setEntity(stringEntity);
        return getResult(httpPost);
    }
    /**
     *
     * @param url
     * @param params  参数
     * @return
     */
    public static String  httpPostRequeset(String url,List params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpEntity httpEntity = new UrlEncodedFormEntity(params, "UTF-8");
        httpPost.setEntity(httpEntity);
        CloseableHttpResponse response = client.execute(httpPost);
        String str = EntityUtils.toString(response.getEntity());
        return str;
    }

    /**
     * 执行HTTP请求
     * @param request
     * @return
     */
    private static CloseableHttpResponse getResult(HttpRequestBase request) throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        return executeRequest(request, httpClient);

    }
    /**
     * 执行HTTPS请求
     * @param request
     * @return
     */
    private static CloseableHttpResponse getHttpsResult(HttpRequestBase request, InputStream is, String password) throws IOException {
        CloseableHttpClient httpClient = getHttpsClient(is, password);
        return executeRequest(request, httpClient);


    }

    private static CloseableHttpResponse executeRequest(HttpRequestBase request, CloseableHttpClient httpClient) throws IOException {
        /**
         * setConnectTimeout：设置连接超时时间，单位毫秒。
         * setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
         * setSocketTimeout：请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
         */
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(15000).setSocketTimeout(10000).build();
        request.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            log.info("HttpResponse code:{}", statusCode);
            return response;
        }catch (IOException e) {
            log.error("IOException:", e);
            throw e;
        }
    }

    /**
     * 转换请求参数
     * @param params
     * @return
     */
    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params){
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for(Map.Entry<String, Object> param : params.entrySet()){
            pairs.add(new BasicNameValuePair(param.getKey(), (String) param.getValue()));
        }
        return pairs;
    }

    /**
     * 转换请求参数
     * @param params
     * @return
     */
    private static String covertParams2JSON(Map<String, Object> params){
        ObjectMapper om = new ObjectMapper();
        try {
            return om.writeValueAsString(params);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
