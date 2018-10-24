package com.jk51.modules.userScenarios.service;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.SClerkDetail;
import com.jk51.model.order.Trades;
import com.jk51.model.qrcode.BConcern;
import com.jk51.model.qrcode.WeiXinQRCode;
import com.jk51.model.qrcode.WeiXinResult;
import com.jk51.modules.im.service.wechatUtil.WechatUtil;
import com.jk51.modules.persistence.mapper.SStoreAdminMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.modules.userScenarios.mapper.QrcodeMapper;
import com.jk51.modules.userScenarios.util.HttpRequestUtil;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-06-06
 * 修改记录:
 */

@Service
public class QrcodeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QrcodeService.class);

    // 临时二维码
    private final static String QR_SCENE = "QR_SCENE";
    // 永久二维码
    private final static String QR_LIMIT_SCENE = "QR_LIMIT_SCENE";
    // 永久二维码(字符串)
    private final static String QR_LIMIT_STR_SCENE = "QR_LIMIT_STR_SCENE";
    // 创建二维码
    private String create_ticket_path = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
    // 通过ticket换取二维码
    private String showqrcode_path = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private WechatUtil wechatUtil;

    @Autowired
    private QrcodeMapper qrcodeMapper;

    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private SStoreAdminMapper storeAdminMapper;

    @Value("${report.temp_dir}")
    private String temp_dir;

    @Transactional
    public Map<String, Object> createQrcode(Map<String, Object> param){
        Map<String, Object> result = new HashMap();

        Integer siteId = Integer.parseInt(param.get("siteId").toString());
        String code = param.get("sceneStr").toString();
        Integer type = Integer.parseInt(param.get("type").toString());
        String sceneStr = "";

        //创建二维码
        Map<String, Object> wxConfig = queryAppidAndSecret(siteId);
        if (null == wxConfig){
            result.put("error", "appid,secret无效，请联系管理员");
            return result;
        }
        //生成accessToken。每个accessToken有2个小时时效，每天能申请2000次
        String accessToken = null;
        accessToken = wechatUtil.getAccessToken(siteId);
        if (null == accessToken){
            result.put("error", "qrCode error");
            return result;
        }

        //生成永久ticket。一个ticket对应一个二维码，只能申请100000个永久二维码
        if(1 == type){
            sceneStr = "admin_" + code;
        }else if (2 == type){
            sceneStr = "store_" + code;
        }else if (3 == type){
            sceneStr = "merchant_" + code;
        }else if (4 == type){
            sceneStr = "device_" + code;
        }
        WeiXinQRCode ticket = createForeverStrTicket(accessToken, sceneStr);
        if (null == ticket.getTicket()){
            //stringRedisTemplate.delete(siteId+"accessToken");
            accessToken = wechatUtil.getAccessToken(siteId);
            ticket = createForeverStrTicket(accessToken, sceneStr);
        }

        //显示地址
        String url = "";
        try {
            url = showQrcode(ticket.getTicket());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info(url);

        //下载地址
        String downloadUrl = "" ;
        downloadUrl = ticket.getUrl();

        result.put("sceneStr", sceneStr);
        result.put("ticket", ticket);
        result.put("url", url);
        result.put("downloadUrl", downloadUrl);

        //更新数据库二维码字段
        if(1 == type){
            if (insertQrcodeIntoStoreadmin(siteId,code,url) != 1) {
                LOGGER.error("二维码更新失败");
                result.put("error","二维码更新失败");
            }
        }else if (2 == type){
            //存门店
        }else if (3 == type){
            //存商户
        }

        return result;
    }

    public String getAccessToken(Integer siteId, String appid, String appsecret) {

        String redisAccessToken = stringRedisTemplate.opsForValue().get(siteId+"accessToken");
        LOGGER.info("redisAccessToken1:"+redisAccessToken);
        //redisAccessToken = null;

        if(redisAccessToken==null || redisAccessToken.equals("")){
            String result = wechatUtil.getAccessToken(siteId);
            LOGGER.info("redisAccessToken1 result:"+result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (null != jsonObject) {
                try {
                    result = jsonObject.getString("access_token");
                    LOGGER.info("redisAccessToken1 result2:"+result);
                } catch (JSONException e) {
                    LOGGER.info("获取token失败 errcode:"+e);
                }
            }
            if(!StringUtil.isEmpty(result)){
                stringRedisTemplate.opsForValue().set(siteId+"accessToken", result, 120, TimeUnit.MINUTES);
            }

            return result;
        }else{
            return redisAccessToken;
        }

    }

    /**
     * 创建永久二维码(数字)
     * @param accessToken
     * @param sceneId 场景Id
     * @return
     */
    public String createForeverTicket(String accessToken, int sceneId) {

        TreeMap<String,String> params = new TreeMap<String,String>();
        params.put("access_token", accessToken);
        //output data
        Map<String,Integer> intMap = new HashMap<String,Integer>();
        intMap.put("scene_id",sceneId);
        Map<String,Map<String,Integer>> mapMap = new HashMap<String,Map<String,Integer>>();
        mapMap.put("scene", intMap);
        //
        Map<String,Object> paramsMap = new HashMap<String,Object>();
        paramsMap.put("action_name", QR_LIMIT_SCENE);
        paramsMap.put("action_info", mapMap);
        String data = JSONObject.toJSONString(paramsMap);
        data =  HttpRequestUtil.HttpsDefaultExecute(HttpRequestUtil.POST_METHOD,create_ticket_path,params,data);
        WeiXinQRCode wxQRCode = null;
        try {
            wxQRCode = new Gson().fromJson(data, WeiXinQRCode.class);
        } catch (JsonSyntaxException e) {
            wxQRCode = null;
            e.printStackTrace();
        }
        return wxQRCode==null?null:wxQRCode.getTicket();
    }

    /**
     * 创建永久二维码(字符串)
     *
     * @param accessToken
     * @param sceneStr 场景str
     * @return
     */
    public WeiXinQRCode createForeverStrTicket(String accessToken, String sceneStr){
        TreeMap<String,String> params = new TreeMap<String,String>();
        params.put("access_token", accessToken);
        //output data
        Map<String,String> intMap = new HashMap<String,String>();
        intMap.put("scene_str",sceneStr);
        Map<String,Map<String,String>> mapMap = new HashMap<String,Map<String,String>>();
        mapMap.put("scene", intMap);

        Map<String,Object> paramsMap = new HashMap<String,Object>();
        paramsMap.put("action_name", QR_LIMIT_STR_SCENE);
        paramsMap.put("action_info", mapMap);
        LOGGER.info(accessToken+"创建永久二维码paramsMap:"+paramsMap);
        String data = new Gson().toJson(paramsMap);
        LOGGER.info("创建永久二维码1:"+data);
                data =  HttpRequestUtil.HttpsDefaultExecute(HttpRequestUtil.POST_METHOD,create_ticket_path,params,data);
        LOGGER.info("创建永久二维码2:"+data);
        WeiXinQRCode wxQRCode = null;
        try {
            wxQRCode = new Gson().fromJson(data, WeiXinQRCode.class);
        } catch (JsonSyntaxException e) {
            wxQRCode = null;
        }
        return wxQRCode;
    }

    /**
     * 获取二维码ticket后，通过ticket换取二维码图片展示
     * @param ticket
     * @return
     */
    public String showQrcode(String ticket){
        return showqrcode_path.replace("TICKET", HttpRequestUtil.urlEncode(ticket,HttpRequestUtil.DEFAULT_CHARSET));
    }

    /**
     * 获取二维码ticket后，通过ticket换取二维码图片
     * @param ticket
     * @param savePath  保存的路径,例如 F:\\test\test.jpg
     * @return		Result.success = true 表示下载图片下载成功
     */
    public WeiXinResult downloadQrcode(String ticket,String savePath) throws Exception{
        TreeMap<String,String> params = new TreeMap<String,String>();
        params.put("ticket", ticket);
        WeiXinResult result = HttpRequestUtil.downMeaterMetod(params,"GET",showqrcode_path,savePath);
        return result;
    }

    /**
     * 查询appid和secret
     * @param siteId
     * @return
     */
    public Map<String, Object> queryAppidAndSecret(Integer siteId){
        return qrcodeMapper.queryAppidAndSecret(siteId);
    }

    /**
     * 更新店员的二维码
     * @param siteId
     * @param clerkCode
     * @param url
     * @return
     */
    public Integer insertQrcodeIntoStoreadmin(Integer siteId, String clerkCode, String url){
        return qrcodeMapper.insertQrcodeIntoStoreadmin(siteId, clerkCode, url);
    }

    /**
     * 扫描二维码保留场景值或记录到表中
     * @param param
     * @return
     */
    public Map<String, Object> scanCodeConcern(Map<String, Object> param){
        Map<String, Object> result = new HashMap();
        Integer siteId = Integer.parseInt(param.get("siteId").toString());
        //String toUserName = param.get("ToUserName").toString();   //开发者微信号
        String openId = param.get("FromUserName").toString();  //发送方账号 openId
        Long createTime = Long.parseLong(param.get("CreateTime").toString()); //消息创建时间
        String eventKey = "";
        Integer type = 0;
        Integer typePk = 0;
        BConcern concern = new BConcern();

        Integer recordCount = qrcodeMapper.isExist(siteId, openId, createTime);
        if (recordCount == 0){
            if(param.containsKey("EventKey")){
                //EventKey存入redis
                eventKey = param.get("EventKey").toString();
                eventKey = eventKey.replace("qrscene_","");

                if(eventKey.contains("admin")){
                    String code = eventKey.replace("admin_", "");
                    typePk = qrcodeMapper.queryStroeAdminId(siteId, code);
                    typePk = typePk!=null ? typePk : 0;
                    type = 1;
                }else if (eventKey.contains("store")) {
                    String stroreId = eventKey.replace("store_", "");
                    //typePk = qrcodeMapper.queryStroeId(siteId, code);
                    typePk = Integer.valueOf(stroreId);
                    type = 2;
                }else if (eventKey.contains("merchant")){
                    typePk = siteId;
                    type = 3;
                }
            }
            //封装实体类。插入表
            concern.setSiteId(siteId);
            concern.setTypePk(typePk);
            concern.setType(type);
            concern.setOpenId(openId);
            concern.setSceneStr(eventKey);
            concern.setCreateTime(createTime);
            Integer count = qrcodeMapper.insertConcern(concern);
            if(count != 1){
                result.put("status", "false");
                result.put("code", "insert concern false");
            }else{
                result.put("status","success");
            }

            result.put("status","success");
        }


        return result;
    }



    public Map<String, Object> adminQrcodeInfo (Map<String, Object> param){
        Integer siteId = Integer.parseInt(param.get("siteId").toString());
        Integer id = Integer.parseInt(param.get("id").toString());
        Map<String, Object> adminInfo = qrcodeMapper.queryAdminInfo(siteId, id);
//        if (adminInfo.get("qrcode_url").equals("")){
//            Map<String, Object> falseMap = new HashMap();
//            falseMap.put("state","false");
//            return falseMap;
//        }
//        String qrcodeUrl = adminInfo.get("qrcode_url").toString();
//        String name = adminInfo.get("name").toString();
//        String code = adminInfo.get("clerk").toString();
//
//        String ticket = qrcodeUrl.substring(qrcodeUrl.indexOf("ticket=")+7,qrcodeUrl.length());
//        //String senceStr = "admin_" + invitedCode;
//        try {
//            downloadQrcode(ticket,temp_dir + "/" +name+"_"+code+".jpg");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return adminInfo;


    }

    public Map<String, Object> createAllQrcode() throws Exception{
        ConcurrentHashMap<String, Object> createAllResult = new ConcurrentHashMap<>();
        List<Integer> merchantList = qrcodeMapper.merchantList();
        merchantList.parallelStream().forEach(merchant -> {
            List<Map<String, Object>> storeAdminId = qrcodeMapper.allStoreAdminId(merchant);
            if(!StringUtil.isEmpty(storeAdminId)){
                storeAdminId.parallelStream().forEach(admin -> {
                    Integer id = Integer.parseInt(admin.get("storeadmin_id").toString());
                    if(admin.containsKey("clerk_invitation_code") && admin.get("clerk_invitation_code").toString()!="" && admin.get("clerk_invitation_code").toString()!=null) {
                        String code = admin.get("clerk_invitation_code").toString();
                        Map<String, Object> param = new HashMap();
                        param.put("siteId", merchant);
                        param.put("sceneStr", code);
                        param.put("type", 1);
                        Map<String, Object> result = createQrcode(param);
                        if (result.containsKey("error")) {
                            String error = result.get("error").toString();
                            LOGGER.error("商户{}下{}店员生成二维码失败,原因为：{}", merchant, id, error);
                            createAllResult.put("errorAdmin", merchant + "_" + id);
                            //createAllResult.put("adminId", id);
                            createAllResult.put("state", "error");
                        }
                    }
                });
            }
        });
//        for (Integer merchant:merchantList) {
//            List<Map<String, Object>> storeAdminId = qrcodeMapper.allStoreAdminId(merchant);
//            for (Map<String, Object> admin : storeAdminId){
//                Integer id = Integer.parseInt(admin.get("storeadmin_id").toString());
//                String code = admin.get("clerk_invitation_code").toString();
//                Map<String, Object> param = new HashMap();
//                param.put("siteId", merchant);
//                param.put("sceneStr", code);
//                param.put("type", 1);
//                Map<String, Object> result = createQrcode(param);
//                if (result.containsKey("error")){
//                    String error = result.get("error").toString();
//                    LOGGER.error("商户{}下{}店员生成二维码失败,原因为：{}",merchant,id,error);
//                    createAllResult.put("errorAdmin", merchant+"_"+id);
//                    //createAllResult.put("adminId", id);
//                    createAllResult.put("state", "error");
//                }
//            }
//        }
        if(createAllResult.containsKey("state")){
            return createAllResult;
        }else {
            createAllResult.put("state", "success");
        }
        return createAllResult;
    }

    public Map<String, Object> createMerchantQrcode(Integer siteId){
        ConcurrentHashMap<String, Object> createAllResult = new ConcurrentHashMap<>();
        List<Map<String, Object>> storeAdminId = qrcodeMapper.allStoreAdminId(siteId);
        if(!StringUtil.isEmpty(storeAdminId)){
            storeAdminId.parallelStream().forEach(admin -> {
                Integer id = Integer.parseInt(admin.get("storeadmin_id").toString());
                if(admin.containsKey("clerk_invitation_code") && admin.get("clerk_invitation_code").toString()!="" && admin.get("clerk_invitation_code").toString()!=null) {
                    String code = admin.get("clerk_invitation_code").toString();
                    Map<String, Object> param = new HashMap();
                    param.put("siteId", siteId);
                    param.put("sceneStr", code);
                    param.put("type", 1);
                    Map<String, Object> result = createQrcode(param);
                    if (result.containsKey("error")) {
                        LOGGER.error("商户{}下{}店员生成二维码失败,原因为：{}", siteId, id, result);
                        createAllResult.put("errorAdmin", siteId + "_" + id);
                        //createAllResult.put("adminId", id);
                        createAllResult.put("state", "error");
                    }
                }
            });
        }
        if(createAllResult.containsKey("state")){
            return createAllResult;
        }else {
            createAllResult.put("state", "success");
        }
        return createAllResult;
    }

    public Map<String, Object> createStroeAdminQrcode(Integer siteId, Integer storeAdminId){
        ConcurrentHashMap<String, Object> createAllResult = new ConcurrentHashMap<>();
        Map<String, Object> admin = qrcodeMapper.queryAdminInfo(siteId, storeAdminId);
        if(admin.containsKey("clerk_invitation_code") && admin.get("clerk_invitation_code").toString()!="" && admin.get("clerk_invitation_code").toString()!=null) {
            String code = admin.get("clerk_invitation_code").toString();
            Map<String, Object> param = new HashMap();
            param.put("siteId", siteId);
            param.put("sceneStr", code);
            param.put("type", 1); 
            Map<String, Object> result = createQrcode(param);
            if (result.containsKey("error")) {
                String error = result.get("error").toString();
                LOGGER.error("商户{}下{}店员生成二维码失败,原因为：{}", siteId, storeAdminId, error);
                createAllResult.put("errorAdmin", siteId + "_" + storeAdminId);
                //createAllResult.put("adminId", id);
                createAllResult.put("state", "error");
            }
        }
        if(createAllResult.containsKey("state")){
            return createAllResult;
        }else {
            createAllResult.put("state", "success");
        }
        return createAllResult;
    }

    public Integer insertOpenid(String openid, Integer siteId, Integer buyerId){
        String queryOpenid = qrcodeMapper.queryOpenid(siteId, buyerId);
        if (queryOpenid == null || !queryOpenid.equals(openid)) {
            Integer status = qrcodeMapper.insertOpenid(siteId, buyerId, openid);
            return status;
        }else {
            return -1;
        }

    }
    public Integer insertAliQrcode(Integer siteId,Integer storeadminId,String url){
        return qrcodeMapper.insertAliQrcode( siteId, storeadminId, url);
    }

    public Integer cancelConcern(Integer siteId,String open_id){
        return qrcodeMapper.cancelConcern( siteId, open_id);
    }
    public Map queryConcernStatus(Map<String, Object> param){
        String open_id = String.valueOf(param.get("openid"));
        Integer siteId = Integer.parseInt(param.get("siteId").toString());
        Map map=qrcodeMapper.queryConcernStatus( siteId, open_id);
        if(StringUtil.isEmpty(map)||"0".equals(map.get("concern_status").toString())){
            map=new HashedMap();
            if(param.containsKey("tradesId")){
                long tradesId = Long.parseLong(param.get("tradesId").toString());
                Trades trades = tradesMapper.getTradesByTradesId(tradesId);
                if(!StringUtil.isEmpty(trades)&&StringUtil.isEmpty(trades.getStockupUserId())){
                    SClerkDetail store=storeAdminMapper.selectClerkDetail(siteId, trades.getStockupUserId());
                    map.put("url",store.getQrcode_url());
                }
            }
            map.put("Isscan","N");
        }else {
            map=new HashedMap();
            map.put("Isscan","Y");
        }

        return map;
    }
}
