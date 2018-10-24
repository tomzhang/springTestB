package com.jk51.modules.sms.service;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.cloopen.rest.sdk.CCPRestSDK;
import com.jk51.commons.ccprest.result.BaseResult;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.http.HttpClientManager;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.modules.sms.smsConfig.QiMoor;
import com.jk51.modules.sms.smsConfig.QiMoorConfig;
import com.jk51.modules.sms.smsConfig.YtxSmsConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 备用
 * 作者: chen
 * 创建日期: 2017-02-22
 * 修改记录:
 */
@Service
public class Sms7MoorService {

    private static final Logger log = LoggerFactory.getLogger(Sms7MoorService.class);

    @Autowired
    private QiMoorConfig _7moorconfig;

    private QiMoor _7moor;
    @Autowired
    private YtxSmsConfig ytx;
    @Autowired
    private BalanceService balanceService;
    /**
     * 发送短信请求
     * @param num
     * @param var
     * @return
     */
    public BaseResult _7moorSentSms(String num,String ... var){
        _7moor = new QiMoor();

        String url = String.format(_7moorconfig.getUrl_sms(),_7moorconfig.getAccountID(),_7moor.getSid(_7moorconfig.getAccountID(),_7moorconfig.getApisecret()));


        try {
            CloseableHttpResponse result = HttpClientManager.httpPostRequest(url,_7moor.getParam(_7moorconfig.getPassword(),num,"",var),
                    _7moor.getHeaderMap(_7moorconfig.getAccountID()));
            String resultStr = result.getEntity().toString();
            return BaseResult.success();
        } catch (IOException e) {
            log.error("http请求失败"+e);
            return BaseResult.failed();
        }
    }

    /**
     * 获取短信模板请求
     * @return
     */
    public  BaseResult _7moorgetSmsTemplate(){

        _7moor = new QiMoor();

        String tempUrl = _7moorconfig.getUrl_temp();
        String accountid = _7moorconfig.getAccountID();
        String apisec = _7moorconfig.getApisecret();
        String sid = _7moor.getSid(accountid,apisec);


//        String url = String.format(_7moorconfig.getUrl_temp(),_7moorconfig.getAccountID(),_7moor.getSid(_7moorconfig.getAccountID(),_7moorconfig.getApiSecret()));

        String url = String.format(tempUrl,accountid,sid);
        try {
            CloseableHttpResponse result = HttpClientManager.httpPostRequest(url,_7moor.getParam(_7moorconfig.getPassword()),_7moor.getHeaderMap(accountid));
            String resultStr = EntityUtils.toString(result.getEntity());
            return BaseResult.success();
        } catch (IOException e) {
            log.error("url请求失败"+e);
            return BaseResult.failed();
        }
    }


    /**
     * 语音验证码
     * @param exten
     * @param vcode
     * @return
     */
    public BaseResult _7MoorWebCall(String exten,String vcode){

        //_7moor = new QiMoor();
        String webCallUrl = "http://115.29.14.183:3000/OpenService?action=voiceVerifyStandard&account=%s&exten=%s&verifyCode=%s";

        String url = String.format(webCallUrl,_7moorconfig.getAccountID(),exten,vcode);
        log.info(url);

        try {
            String resStr = HttpClient.doHttpGet(url);
           /* CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response =null;
            HttpGet method = new HttpGet(url);
            response=client.execute(method);
            String resStr = HttpClientManager.getResponseString(response,null);*/
            log.info(resStr);
            JSONObject objects = JSONArray.parseObject(resStr);
            if("000000".equals(objects.get("statusCode")))
                return BaseResult.success();
            else return BaseResult.failed();
        } catch (Exception e) {
            log.error("url请求错误" + e);
            return BaseResult.failed();
        }
    }

    public Map<String,Object>  webcall(String exten,long tradesId){
        log.info("webcall tradesId:{},exten:{}",tradesId,exten);
        String baseUrl = "http://121.43.153.58/command?Action=Webcall&Account=%s&WebCallType=asynchronous&CallBackUrl=%s&PBX=%s&Timeout=60&ServiceNo=02126125865&Exten=%s&ActionID="+tradesId;
        String url = _7moorconfig.getUrl_web_call();
        String accoun = _7moorconfig.getAccountID();
        String pbx = _7moorconfig.getPBX();
        String serviceNo = _7moorconfig.getServiceNo();
        String realUrl = String.format(baseUrl,accoun,url,pbx,exten);
        Integer code = 0;
        String msg = "";
        Map<String,Object> resutl = new HashMap<>();
        try {
            String str = HttpClient.doHttpGet(realUrl);
            log.info("webcall resutl:{}",str);
//            String str = HttpClientManager.getResponseString(HttpClientManager.httpGetRequest(realUrl),"utf-8");
           /* Map<String,Object> map = JacksonUtils.json2map(str);
            if("5".equals(map.get("Message"))){
                log.debug("检查余额"+map.get("Message"));
                msg=map.get("Message").toString();
                resutl.put("code",code);
                resutl.put("msg",msg);
                return resutl;
            }else if("4".equals(map.get("Message"))){
                msg=map.get("Message").toString();
                msg=map.get("Message").toString();
                resutl.put("code",code);
                resutl.put("msg",msg);
                return resutl;
            }else{
                code=2;
                msg="请求失败";
                msg=map.get("Message").toString();
                resutl.put("code",code);
                resutl.put("msg",msg);
                return resutl;
            }*/
        } catch (IOException e) {
            /*code=5;
            msg="请求失败"+e;
            resutl.put("code",code);
            resutl.put("msg",msg);*/
            log.info("IOException:{}",e);
        } catch (Exception e) {
            /*code=5;
            msg="请求失败"+e;
            resutl.put("code",code);
            resutl.put("msg",msg);*/
            log.info("Exception:{}",e);
        }
        return resutl;
    }


//    public String webcall(String exten) {

//        String baseUrl = "http://121.43.153.58/command?Action=Webcall&Account=%s&PBX=%s&Timeout=20&ServiceNo=02126125865&Exten=%s";
////        String url = _7moorconfig.getUrl_webCall();
//        String account = _7moorconfig.getAccountID();
//        String pbx = _7moorconfig.getPBX();
//        String serviceNo = _7moorconfig.getServiceNo();
//        log.info(serviceNo);
//        String realUrl = String.format(baseUrl,account,pbx,exten);
//        log.info(realUrl);
//
////        try {
////            String str = HttpClientManager.getResponseString(HttpClientManager.httpGetRequest(realUrl),"utf-8");
////            Map<String,Object> map = JacksonUtils.json2map(str);
////            if("5".equals(map.get("Message"))){
////                log.debug("检查余额");
////            }
////            return "ok";
////        } catch (IOException e) {
////            log.error("请求失败"+e);
////        } catch (Exception e) {
////            log.error("解析失败"+e);
////        }
////        return "faild";
//
//        new Thread(new Runnable(){
//
//
//            @Override
//            public void run() {
//                log.info("线程已启动");
//                call();
//            }
//
//            private void call() {
//                int i = 1;
//                while (true) {
//                    log.info("到这里call()");
//                    if(i==6) return;
//                    try {
//                    log.info("到这里call()try");
//                        if(i%2!=0)
//                            Thread.sleep(30000);
//                        else Thread.sleep(120000);
//                        i++;
//                        if(i==6) return;
//                        String resultStr = HttpClientManager.getResponseString(HttpClientManager.httpGetRequest(realUrl), "utf-8");
//                    log.info("到这里call()map");
//                        Map<String, Object> result = JacksonUtils.json2map(resultStr);
//                        if("true".equalsIgnoreCase((String) result.get("Success"))){
//                            if("4".equalsIgnoreCase((String) result.get("Message"))){
//                                return ;
//                            }else if("5".equalsIgnoreCase((String) result.get("Message"))){
//                                log.error("请检查余额");
//                                return ;
//                            }else{
//
//                            }
//                        }
//                    } catch(IOException e){
//                        e.printStackTrace();
//                        log.error("请求失败"+e);
//                    } catch(Exception e){
//                        e.printStackTrace();
//                        log.error("解析失败"+e);
//                    }
//                }
//            }
//        }).start();
//        return "";
//    }
    /**
     * 语音验证码(new)云通信
     * @param exten
     * @param vcode
     * @return
     */
    public BaseResult _7MoorWebCallNew(String exten,String vcode, String siteId){
        HashMap<String, Object> result = null;
        CCPRestSDK restAPI = new CCPRestSDK();
        //restAPI.init("app.cloopen.com", "8883");
        restAPI.init(ytx.getYtx_sms_url(), ytx.getYtx_sms_port());
        // 初始化服务器地址和端口，生产环境配置成app.cloopen.com，端口是8883.
        restAPI.setAccount(ytx.getYtx_sms_sid(), ytx.getYtx_sms_token());
        // 初始化主账号名称和主账号令牌，登陆云通讯网站后，可在"控制台-应用"中看到开发者主账号ACCOUNT SID和
        //主账号令牌AUTH TOKEN。
        restAPI.setAppId(ytx.getYtx_sms_yu_appid());
        // 初始化应用ID，如果是在沙盒环境开发，
        //请配置"控制台-应用-测试DEMO"中的APPID。如切换到生产环境，请使用自己创建应用的APPID
        result = restAPI.voiceVerify(vcode, exten,_7moorconfig.getServiceNo(),"3","", "zh", "","","","");
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet= data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                log.info(key +" = "+object);
            }
            Map<String, Object> log = new HashMap<>();
            log.put("phone", exten);
            log.put("msg", "语音验证码");
            log.put("channel", "ZT");
            //修改余额操作日志
            balanceService.insertBalanceDetail(Integer.parseInt(siteId),4,0,JacksonUtils.mapToJson(log),null,140,null);
        }else{
            //异常返回输出错误码和错误信息
            log.info("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
        return BaseResult.success();
    }
    /**
     * 订单提醒(new)云通信
     * @param exten
     * @return
     */
    public Map<String, Object> landingCall(String exten,Integer siteId){
        Map<String, Object> result = null;

        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init(ytx.getYtx_sms_url(), ytx.getYtx_sms_port());// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount(ytx.getYtx_sms_sid(), ytx.getYtx_sms_token());// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId(ytx.getYtx_sms_yu_appid());// 初始化应用ID
        //type=1，则播放默认语音文件,0是自定义语音文件
        result = restAPI.landingCall(exten, "order.wav", "您有新订单，请及时处理！", "01086210439", "3", "", "", "1", "20", "5", "9", "0");

        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                log.info(key +" = "+object);
            }
            //修改余额操作日志
            Map<String, Object> log = new HashMap<>();
            log.put("phone", exten);
            log.put("msg", "订单提醒【电话提醒】");
            log.put("channel", "ZT");
            balanceService.insertBalanceDetail(siteId,4,0,JacksonUtils.mapToJson(log),null,820,null);
        }else{
            //异常返回输出错误码和错误信息
            log.info("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
        return result;
    }
}
