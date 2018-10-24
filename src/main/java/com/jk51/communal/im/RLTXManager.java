package com.jk51.communal.im;

import com.jk51.commons.encode.BASE64Decoder;
import com.jk51.commons.encode.Base64Coder;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.im.util.RLMessageParameter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;


/**
 * Created by gaojie on 2017/2/14.
 */
@Service
public class RLTXManager {

    private static final Logger logger = LoggerFactory.getLogger(RLTXManager.class);
  /*  private String accountSid = "8a216da855e425d60155e89123920433";
    private static final String authToken = "6d15b298163a4f35ba404ddfebe421dd";
    private static final String baseUrl = "https://yaodianbangshodcsvip1imapp.cloopen.net:8883/2013-12-26";*/

    private String accountSid = "8aaf070865796a570165947abe8113de";
    private static final String authToken = "f1dc25c410e04502b99788e2571cc524";
    private static final String baseUrl = "https://app.cloopen.com:8883/2013-12-26";
    private String sig;
    private String timeStamp = "";
    private static final String NOTICE_MESSAGE = "您有一条新消息";


    public RLTXManager(){
    }

    public void setTiemstamp(){
        String format = "yyyyMMddHHmmss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        this.timeStamp =  sdf.format(new Date());
    }

    public void setSig(){
        setTiemstamp();
        StringBuilder builder = new StringBuilder();
        builder.append(accountSid);
        builder.append(authToken);
        builder.append(timeStamp);
        try {
            this.sig = encoderByMd5(builder.toString());
        } catch (Exception e) {
            logger.error("sig MD5加密报错{}",e);
        }
    }


    /**
     *推送信息
     *
     * */
    @Async
    public Future<Map<String,Object>> pushMsg(Map<String,Object> param ){

       setSig();
       Map<String,Object> resultMap = new HashMap<String,Object>();
       String url = baseUrl+"/Accounts/"+accountSid+"/IM/PushMsg?sig="+sig;

       Map<String,String> headerMap = getHeaderMap();
       CloseableHttpResponse response = null;
       String result = "";

       try {
           result =  OkHttpUtil.postJson(url,JacksonUtils.mapToJson(param),headerMap);

           resultMap =  JacksonUtils.json2map(result);

            logger.info("=======推送聊天消息======= param:{} -receiver:{}- res:{}\n",param,JacksonUtils.obj2json(param.get("receiver")),resultMap);
       } catch (Exception e) {
           logger.error("推送信息报错:{}",e);
       }

       return new AsyncResult(resultMap);

   }



   public String getAuthorization(){
       StringBuilder builder = new StringBuilder();
       builder.append(accountSid);
       builder.append(":");
       builder.append(timeStamp);
       String result = "";
       try {
           result =  base64Encoder(builder.toString());
       } catch (UnsupportedEncodingException e) {
           logger.error("base64加密报错{}",e);
       }
       return result;
   }


    public String base64Encoder(String src) throws UnsupportedEncodingException
    {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(src.getBytes("utf-8"));
    }

    public String base64Decoder(String dest)
            throws NoSuchAlgorithmException, IOException
    {
        BASE64Decoder decoder = new BASE64Decoder();
        return new String(decoder.decodeBuffer(dest), "utf-8");
    }

    /**利用MD5进行加密
     * @param str  待加密的字符串
     * @return  加密后的字符串
     * @throws NoSuchAlgorithmException  没有这种产生消息摘要的算法
     * @throws UnsupportedEncodingException
     */
    public String encoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException{

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = md.digest(str.getBytes("utf-8"));
        return byte2HexStr(b);
    }

    private String byte2HexStr(byte[] b)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            String s = Integer.toHexString(b[i] & 0xFF);
            if (s.length() == 1)
                sb.append("0");

            sb.append(s.toUpperCase());
        }
        return sb.toString();
    }

    public Map<String,String> getHeaderMap() {
        Map<String,String> headerMap = new HashMap<String, String>();
        headerMap.put("Accept","application/json");
        headerMap.put("Content-Type","application/json;charset=utf-8");
        headerMap.put("Authorization",getAuthorization());
        return headerMap;
    }

    public Map<String,Object> getParam(RLMessageParameter param){

        String[] receiverArray = StringUtil.split(param.getReceiver(),",");
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("pushType",param.getPush_type());
        paramMap.put("appId",param.getAppId());
        paramMap.put("sender",param.getSender());
        paramMap.put("receiver",receiverArray);
        paramMap.put("msgType",1);
        paramMap.put("msgContent",param.getMsgContent());
        paramMap.put("msgDomain","yuntongxun");
        paramMap.put("msgFileUrl","");
        paramMap.put("msgFileName","");
        paramMap.put("extOpts",getextOpts());

        return paramMap;
    }

    //添加APNS提示信息
    private String getextOpts(){

        Map<String,String> apsMap = new HashMap<String,String>();
        apsMap.put("apsalert",NOTICE_MESSAGE);

        String extOpts = JacksonUtils.mapToJson(apsMap);

        try {
            return Base64Coder.encode(extOpts);
        } catch (UnsupportedEncodingException e) {
            logger.error("base64 extOpts 异常，报错信息：{}，参数：{}",e,extOpts);
        }

        return null;
    }



    public Map<String,Object> getParam(String reveiver,String sender,String msg,String appId){

        String[] receiverArray ={reveiver};
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("pushType",1);
        paramMap.put("appId",appId);
        paramMap.put("sender",sender);
        paramMap.put("receiver",receiverArray);
        paramMap.put("msgType",1);
        paramMap.put("msgContent",msg);
        paramMap.put("msgDomain","");
        paramMap.put("msgFileName","");
        paramMap.put("msgFileUrl","");

        return paramMap;
    }


    public void getUserState(){

    }
}
