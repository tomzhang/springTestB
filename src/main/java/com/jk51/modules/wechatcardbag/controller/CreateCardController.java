package com.jk51.modules.wechatcardbag.controller;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.concession.ConcessionCalculateBaseImpl;
import com.jk51.modules.im.service.wechatUtil.WechatUtil;
import com.jk51.modules.pay.service.merchant.WxConfigMerchant;
import com.jk51.modules.pay.service.merchant.WxPayApiMerchant;
import com.jk51.modules.wechatcardbag.service.CreateCardService;
import com.jk51.modules.wechatcardbag.service.WechatCardBagConfig;
import net.sf.jpam.PamException;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName CreateCardController
 * @Description 创建微信卡券
 * @Date 2018-04-28 16:25
 */
@Controller
@RequestMapping("/wechat/card")
public class CreateCardController {

    public static final Logger LOGGER = LoggerFactory.getLogger(CreateCardController.class);

    public static final String BASEURL = "https://api.weixin.qq.com";


    @Autowired
    private WechatUtil wechatUtil;

    @Autowired
    private WechatCardBagConfig wechatCardBagConfig;

    @Autowired
    WxPayApiMerchant wxPayApiMerchant;

    @Autowired
    CreateCardService createCardService;

    /**
     * 上传门店图片, 卡券logo
     * @param request
     * @return
     */
    @PostMapping("/uploadLogo")
    @ResponseBody
    public ReturnDto uploadLogo(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        //获取access_token
        String accessToken = wechatUtil.getAccessToken(Integer.valueOf(siteId.toString()));
        MultipartFile buffer = (MultipartFile)parameterMap.get("buffer");
        Map<String,Object> map = new HashedMap();
        map.put("access_token",accessToken);
        map.put("buffer",buffer);
        ReturnDto returnDto = null;
        try {
            String result = HttpClient.doHttpPost(BASEURL + wechatCardBagConfig.UPLOADLOGO, JSON.toJSONString(map));
            Map map1 = JSON.parseObject(result, Map.class);
            returnDto = ReturnDto.buildSuccessReturnDto(map1);
        } catch (IOException e) {
            e.printStackTrace();
            returnDto = ReturnDto.buildFailedReturnDto("上传logo失败"+e);
        }
        return returnDto;
    }


    /**
     * 根据access_token查询会员卡ID
     * 查询商家会员卡
     * 查询状态为审核通过的和在公众平台发放的
     * @param request
     * @return
     */
    @PostMapping("/queryCardId")
    @ResponseBody
    public ReturnDto queryCardIdByAccessToken(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        String accessToken = wechatUtil.getAccessToken(Integer.valueOf(siteId.toString()));

        Map<String,Object> map = new HashedMap();
        map.put("offset",0);
        map.put("count",1);
        map.put("status_list",Arrays.asList("CARD_STATUS_VERIFY_OK", "CARD_STATUS_DISPATCH"));
        ReturnDto returnDto = null;
        try {
            String result = HttpClient.doHttpPost(BASEURL + wechatCardBagConfig.QUERYCARDID+accessToken,JSON.toJSONString(map));
            Map map1 = JSON.parseObject(result, Map.class);
            Object errcode = map1.get("errcode");
            String cardId = "";
            if(0 == Integer.parseInt(errcode.toString())) {
                List card_id_list = (List) map1.get("card_id_list");
                if(Objects.nonNull(card_id_list) && card_id_list.size() != 0) {
                    cardId = card_id_list.get(0).toString();
                }
            }
            returnDto = ReturnDto.buildSuccessReturnDto(cardId);
        } catch (IOException e) {
            e.printStackTrace();
            returnDto = ReturnDto.buildFailedReturnDto("查询商户card_id失败"+e.toString());
        }
        return returnDto;
    }


    /**
     * 根据openid和card_id查询用户卡包
     * @param request
     * @return
     */
    @PostMapping("/queryUserCard")
    @ResponseBody
    public ReturnDto queryUserCard(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        String accessToken = wechatUtil.getAccessToken(Integer.valueOf(siteId.toString()));
        Map<String,Object> json = new HashedMap();
        json.put("openid",parameterMap.get("openid"));
        json.put("card_id",parameterMap.get("card_id"));
        ReturnDto returnDto = null;
        try {
            String s = HttpClient.doHttpPost(BASEURL + WechatCardBagConfig.QUERYUSERCARD+accessToken,JSON.toJSONString(json));
            Map map = JSON.parseObject(s, Map.class);
            Object errcode = map.get("errcode");
            if(0 == Integer.parseInt(errcode.toString())) {
//                Map<String,Object> userCard = new HashedMap();
                List card_list = (List) map.get("card_list");
                if(Objects.isNull(card_list) || card_list.size() == 0) {
                    map.put("isHas","NO");
                    map.put("memCard","");
                    returnDto = ReturnDto.buildSuccessReturnDto(map);
                }else {
                    Map map1 = (Map) card_list.get(0);
                    Object code = map1.get("code");
                    map.put("isHas","YES");
                    map.put("memCard",code);
                    returnDto = ReturnDto.buildSuccessReturnDto(map);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            returnDto = ReturnDto.buildFailedReturnDto("查询用户会员卡记录异常"+e.toString());
        }
        return returnDto;
    }


    /**
     * 获取会员卡扩展和签名
     * @param request
     * @return
     */
    @PostMapping("/getExtAnd")
    @ResponseBody
    public ReturnDto getCardExtAngSign(HttpServletRequest request) {
        //签名 api_ticket   timestamp
        //扩展 timestamp当前时间  signature签名
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.valueOf(parameterMap.get("siteId").toString());
        String accessToken = wechatUtil.getAccessToken(Integer.valueOf(siteId.toString()));
        String timestamp = System.currentTimeMillis() + "";
        String result = "";
        ReturnDto returnDto = null;
        //获取api_ticket
        try {
            String apiTicket = HttpClient.doHttpGet(BASEURL + wechatCardBagConfig.QUERYAPITICKET + accessToken);
            /*{
                errcode: 0
                errmsg: "ok"
                ticket: "IpK_1T69hDhZkLQTlwsAX67I4_XSqmhW6YBnhUvZ7XoQmACaFWMjaZr_L7y8YJq3BId78Q1GxQL2tv8ApnsKfA"
                expires_in: 7200
            }*/
            String ticket = "";
            Map map = JSON.parseObject(apiTicket, Map.class);
            if (Integer.parseInt(map.get("errcode").toString()) == 0) {
                ticket = String.valueOf(map.get("ticket"));
            }
            //生成签名
            String data = "api_ticket="+ticket+"&timestamp="+timestamp;
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            String signature = String.valueOf(sha1.digest(data.getBytes()));
            result = "{\"timestamp\":\"" + timestamp + "\",\"signature\":\"" + signature + "\"}";
            returnDto = ReturnDto.buildSuccessReturnDto(result);
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
            returnDto = ReturnDto.buildFailedReturnDto(result);
        }
        return returnDto;
    }

    @RequestMapping("/getCardConfig")
    @ResponseBody
    public ReturnDto getconfigsite(Integer site_id,String openId, String cardId) {
        if(StringUtil.isEmpty(site_id)) {
            return ReturnDto.buildFailedReturnDto("site_id不能为空");
        }
        if(StringUtil.isEmpty(openId)) {
            return ReturnDto.buildFailedReturnDto("openId不能为空");
        }
        if(StringUtil.isEmpty(cardId)) {
            return ReturnDto.buildFailedReturnDto("cardId不能为空");
        }
        Map map = new HashMap();
        String randomChar = StringUtil.getRandomChar(32);
        map.put("nonce_str", randomChar);//随机字符串
        String ltime = (System.currentTimeMillis() / 1000)+"";
        map.put("timestamp", ltime);//当前时间毫秒数
//        map.put("openid",openId);
        map.put("card_id",cardId);
        try {
            WxConfigMerchant wxConfig=wxPayApiMerchant.toConfig(site_id);
            if(wxConfig == null) {return ReturnDto.buildFailedReturnDto("找不到此商家config");}
            /*String jsapi_ticket = payServiceMerchant.getJSAPITicket(site_id);
            map.put("jsapi_ticket", jsapi_ticket);*/
            String accessToken = wechatUtil.getAccessToken(site_id);
            //TODO api_ticket调用次数有限 需存到缓存中
            String jsapi_ticket = HttpClient.doHttpGet(BASEURL + wechatCardBagConfig.QUERYAPITICKET + accessToken);
            /*{
                errcode: 0
                errmsg: "ok"
                ticket: "IpK_1T69hDhZkLQTlwsAX67I4_XSqmhW6YBnhUvZ7XoQmACaFWMjaZr_L7y8YJq3BId78Q1GxQL2tv8ApnsKfA"
                expires_in: 7200
            }*/
            String api_ticket = "";
            Map ticketMap = JSON.parseObject(jsapi_ticket, Map.class);
            if (Integer.parseInt(ticketMap.get("errcode").toString()) == 0) {
                api_ticket = String.valueOf(ticketMap.get("ticket"));
            }
            map.put("api_ticket", api_ticket);

            /*排序
            List<String> ss = new List<String>() { api_ticket, l, nonce_str, card_id };
            var list=ss.OrderBy(x=>x,StringComparer.Ordinal).ToArray();
            var orderstring=string.Join("",list);

            string _signature = SHA1Helper.HmacSha1(orderstring);
            this.txt_qm.Text = _signature;
            this.txt_px.Text = orderstring;*/

            /*List<Map.Entry<String,String>> list = new ArrayList<Map.Entry<String,String>>(map.entrySet());

            Collections.sort(list,new Comparator<Map.Entry<String,String>>() {
                //升序排序
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });*/

            /*Set keySet = map.keySet();
            Object[] objs = keySet.toArray();
            Arrays.sort(objs);
            StringBuffer tempStr = new StringBuffer();
            for (int i = 0; i < objs.length; ++i) {
                tempStr.append(objs[i] + "=" + map.get(objs[i]));
                if(i != objs.length - 1) {
                    tempStr.append("&");
                }
            }*/

            Object[] objects = map.values().toArray();
            Arrays.sort(objects);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < objects.length; i++) {
                sb.append(objects[i]);
            }

            /*StringBuffer sb = new StringBuffer();
            for(int i = 0; i < list.size(); i++) {
                Map.Entry<String, String> stringStringEntry = list.get(i);
                String key = stringStringEntry.getKey();
                String value = stringStringEntry.getValue();
                sb.append(key+"="+value);
                if(i != list.size() - 1) {
                    sb.append("&");
                }
            }*/

            String signature = EncryptUtils.encryptToSHA(sb.toString()).toLowerCase();
            LOGGER.debug("map: "+map);
            LOGGER.debug("sort: "+sb.toString());
            LOGGER.debug("signature: "+signature);
            map.put("signature", signature);
            map.put("appId", wxConfig.getAppid());
//            String ext = "{\"timestamp\":\"" + ltime + "\",\"nonce_str\":\"" + randomChar + "\",\"api_ticket\":\"" + api_ticket + "\",\"card_id\":\"" + cardId + "\"}";
            Map<String,String> cardExt = new HashedMap();
            cardExt.put("nonce_str",randomChar);
            cardExt.put("api_ticket",api_ticket);
            cardExt.put("card_id",cardId);
            cardExt.put("timestamp",ltime);
            LOGGER.debug("cardExt:  "+JSON.toJSONString(cardExt));
            map.put("ext",JSON.toJSONString(cardExt));
            return ReturnDto.buildSuccessReturnDto(map);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    @PostMapping("/updateMerchantCard")
    @ResponseBody
    public ReturnDto updateMerchantCard(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        Object cardId = parameterMap.get("cardId");
        Object url = parameterMap.get("url");
        String accessToken = wechatUtil.getAccessToken(Integer.valueOf(siteId.toString()));
        /*{"card": {"member_card": {"wx_activate": true, "wx_activate_after_submit" : true,//是否设置跳转型一键激活
                    "wx_activate_after_submit_url" : "https://qq.com"
                //用户提交信息后跳转的网页 }
            }
        }*/
       StringBuffer sb = new StringBuffer();
       sb.append("{\"card_id\": \""+cardId+"\",\"member_card\": {\"wx_activate\": true, \"wx_activate_after_submit\" : true,\"wx_activate_after_submit_url\" :");
       sb.append("\""+url+"\"");
       sb.append("}}");
       ReturnDto returnDto = null;
        try {
            String result = HttpClient.doHttpPost(BASEURL + wechatCardBagConfig.UPDATEMERCHANTCARD + accessToken, sb.toString());
            Map map = JSON.parseObject(result, Map.class);
            if ("ok".equals(map.get("errmsg"))) {
                returnDto = ReturnDto.buildSuccessReturnDto("更新会员卡成功!");
            } else {
                returnDto = ReturnDto.buildSuccessReturnDto("更新会员卡失败!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            returnDto = ReturnDto.buildFailedReturnDto("更新会员卡失败!"+e.toString());
        }
        return  returnDto;
    }

    /**
     * 查询用户手机号
     * @param request
     * @return
     */
    @PostMapping("/queryUserMobileP")
    @ResponseBody
    public ReturnDto queryUserMpByCode(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Map<String,Object> queryMp = new HashedMap();
        Object card_id = parameterMap.get("card_id");
        Object siteId = parameterMap.get("siteId");
        String accessToken = wechatUtil.getAccessToken(Integer.valueOf(siteId.toString()));
        parameterMap.remove("siteId");
        parameterMap.remove("wechatToken");
        parameterMap.remove("erpStoreId");
        parameterMap.remove("erpAreaCode");
        ReturnDto returnDto = null;
        try {
            String result = HttpClient.doHttpPost(BASEURL + wechatCardBagConfig.QUERYCARDCODE + accessToken, JSON.toJSONString(parameterMap));
            Map map = JSON.parseObject(result, Map.class);
            if ("ok".equals(map.get("errmsg"))) {
                List card_list = (List) map.get("card_list");
                Map map1 = (Map) card_list.get(0);
                String code = String.valueOf(map1.get("code"));
                queryMp.put("card_id",card_id);
                queryMp.put("code",code);
                String cardInfo = HttpClient.doHttpPost(BASEURL + wechatCardBagConfig.QUERYCARDINFO + accessToken, JSON.toJSONString(queryMp));
                Map map2 = JSON.parseObject(cardInfo, Map.class);
                if ("ok".equals(map2.get("errmsg"))) {
                    //TODO 跳转url之后才能拿到activity_ticket
                    //TODO通过接口激活之后可以拿到手机号
                    Object user_info1 = map2.get("user_info");
                    if (StringUtil.isEmpty(user_info1)) {
                        returnDto  = ReturnDto.buildFailedReturnDto("没有查询到会员信息!");
                    }else {
                        Map user_info = (Map)user_info1;
                        Object common_field_list1 = user_info.get("common_field_list");
                        if (StringUtil.isEmpty(common_field_list1)) {
                            returnDto  = ReturnDto.buildFailedReturnDto("没有查询到会员手机号!");
                        }else {
                            List common_field_list = (List)common_field_list1;
                            StringBuffer sb = new StringBuffer();
                            common_field_list.stream().forEach(fields -> {
                                Map fields1 = (Map) fields;
//                                if (strings.contains(name)) {
//                                    map.put("",value);
//                                }
                                if ("USER_FORM_INFO_FLAG_MOBILE".equals(fields1.get("name"))) {
                                    sb.append(String.valueOf(fields1.get("value")));
                                }
                            });
                            String phone = sb.toString();
                            if (StringUtil.isNotEmpty(phone)) {
                                returnDto = ReturnDto.buildSuccessReturnDto(phone);
                            }else {
                                returnDto  = ReturnDto.buildFailedReturnDto("没有查询到会员手机号!");
                            }
                        }
                    }

                }else {
                    returnDto = ReturnDto.buildFailedReturnDto("查询会员手机号失败!");
                }

            }else {
                returnDto = ReturnDto.buildFailedReturnDto("查询会员手机号失败!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  returnDto;
    }


    /**
     * 查询用户会员卡号
     * @param request
     * @return
     */
    @PostMapping("/queryUsersCardId")
    @ResponseBody
    @SuppressWarnings("all")
    public ReturnDto queryUsersCode(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        String accessToken = wechatUtil.getAccessToken(Integer.valueOf(siteId.toString()));
        Map<String,Object> json = new HashedMap();
        json.put("openid",parameterMap.get("openid"));
        json.put("card_id",parameterMap.get("card_id"));
        ReturnDto returnDto = null;
        try {
            String s = HttpClient.doHttpPost(BASEURL + WechatCardBagConfig.QUERYUSERCARD+accessToken,JSON.toJSONString(json));
            Map map = JSON.parseObject(s, Map.class);
            Object errcode = map.get("errcode");
            if(0 == Integer.parseInt(errcode.toString())) {
                List card_list = (List) map.get("card_list");
                Map map1 = (Map) card_list.get(0);
                Object code = map1.get("code");
                returnDto = ReturnDto.buildSuccessReturnDto(code);
                return returnDto;
            }

        } catch (IOException e) {
            e.printStackTrace();
            returnDto = ReturnDto.buildFailedReturnDto("查询用户会员卡code异常"+e.toString());
        }
        return returnDto;
    }


    /**
     * 通过接口激活会员卡
     * @param request
     * @return
     */
    @PostMapping("/activateMemberCard")
    @ResponseBody
    public ReturnDto activateMemberCard(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        String accessToken = wechatUtil.getAccessToken(Integer.valueOf(siteId.toString()));
        Object code = parameterMap.get("code");
        /*{
            "init_bonus": 100,//初始积分
            "init_bonus_record":"旧积分同步", //积分同步说明
            "init_balance": 200,//初始余额
            "membership_number": "706563212025",//会员卡编号可与code等值
            "code": "706563212025",
            "card_id": "p4ayHwSKzTMs1pfBF77rqmi2ZK94"
        }*/
        parameterMap.put("membership_number",code);
        parameterMap.remove("siteId");
        parameterMap.remove("wechatToken");
        parameterMap.remove("erpStoreId");
        parameterMap.remove("erpAreaCode");
        ReturnDto returnDto = null;
        try {
            String result = HttpClient.doHttpPost(BASEURL + wechatCardBagConfig.ACTIVATEMEMBERCARD + accessToken, JSON.toJSONString(parameterMap));
            Map map = JSON.parseObject(result, Map.class);
            if ("ok".equals(map.get("errmsg"))){
                returnDto = ReturnDto.buildSuccessReturnDto("激活会员卡成功!");
            }else {
                returnDto = ReturnDto.buildFailedReturnDto("激活会员卡失败!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            returnDto = ReturnDto.buildFailedReturnDto("激活会员卡失败!"+e.getMessage());
        }
        return  returnDto;
    }


    /***
     * 查询会员卡组件接口
     * @param request
     * @return
     */
    @PostMapping("/queryCardM")
    @ResponseBody
    public ReturnDto queryCardModule(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        String accessToken = wechatUtil.getAccessToken(Integer.valueOf(siteId.toString()));
        parameterMap.remove("siteId");
        parameterMap.remove("wechatToken");
        parameterMap.remove("erpStoreId");
        parameterMap.remove("erpAreaCode");
        parameterMap.put("outer_str","登录接口");
        ReturnDto returnDto = null;
        try {
            String result = HttpClient.doHttpPost(BASEURL + wechatCardBagConfig.QUERYCARDMODULE + accessToken, JSON.toJSONString(parameterMap));
            Map map = JSON.parseObject(result, Map.class);
            if ("ok".equals(map.get("errmsg"))) {
                Object url = map.get("url");
                returnDto = ReturnDto.buildSuccessReturnDto(url);
            }else {
                returnDto = ReturnDto.buildFailedReturnDto("查询卡券组件失败!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            returnDto = ReturnDto.buildFailedReturnDto("查询卡券组件失败!"+e.getMessage());
        }
        return  returnDto;
    }

    /**
     * 查询会员卡是否激活
     * @param request
     * @return
     */
    @PostMapping("/queryCardIsActivate")
    @ResponseBody
    public ReturnDto isActivate(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        String accessToken = wechatUtil.getAccessToken(Integer.valueOf(siteId.toString()));
        parameterMap.remove("siteId");
        parameterMap.remove("wechatToken");
        parameterMap.remove("erpStoreId");
        parameterMap.remove("erpAreaCode");
        ReturnDto returnDto = null;
        try {
            String result = HttpClient.doHttpPost(BASEURL + wechatCardBagConfig.QUERYCARDINFO + accessToken, JSON.toJSONString(parameterMap));

            Map map2 = JSON.parseObject(result, Map.class);
            if ("ok".equals(map2.get("errmsg"))) {
                Object user_info = map2.get("user_info");
                /*List common_field_list = (List) user_info.get("common_field_list");
                Map map3 = (Map) common_field_list.get(0);
                String phone = String.valueOf(map3.get("value"));
                if (StringUtil.isNotEmpty(phone)) {
                    returnDto = ReturnDto.buildSuccessReturnDto(phone);
                }else {
                    returnDto  = ReturnDto.buildFailedReturnDto("没有查询到会员手机号!");
                }*/
                if (StringUtil.isEmpty(user_info)) {
                    returnDto = ReturnDto.buildSuccessReturnDto("NO");
                }else {
                    returnDto = ReturnDto.buildSuccessReturnDto("YES");
                }
            }else {
                returnDto = ReturnDto.buildFailedReturnDto("查询会员手机号失败!");
            }

        } catch (IOException e) {
            e.printStackTrace();
            returnDto = ReturnDto.buildFailedReturnDto("查询会员卡详情失败!"+e.getMessage());
        }
        return returnDto;
    }


    /**
     * 根据openId查询是否是会员
     * @param request
     * @return
     */
    @PostMapping("/queryIsMemberByOpenid")
    @ResponseBody
    public ReturnDto queryIsMem(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        ReturnDto returnDto = createCardService.queryIsHaveLog(parameterMap);
        return returnDto;
    }


    /**
     *  数据库查询商家会员卡号
     * @param request
     * @return
     */
    @PostMapping("/queryMemberCardId")
    @ResponseBody
    public ReturnDto queryMemberCardId(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        String cardId = createCardService.queryMemberCardId(Integer.valueOf(String.valueOf(siteId)));
        if (StringUtil.isEmpty(cardId)) {
            return ReturnDto.buildFailedReturnDto("商家无会员卡!");
        }else {
            return ReturnDto.buildSuccessReturnDto(cardId);
        }
    }


    /**
     * 查询用户基本信息
     * @param request
     * @return
     */
    @PostMapping("/queryUserInfo")
    @ResponseBody
    public ReturnDto queryUserInfoByCode(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Map<String,Object> queryMp = new HashedMap();
        Object card_id = parameterMap.get("card_id");
        Object siteId = parameterMap.get("siteId");
        Object card_code = parameterMap.get("card_code");
        String accessToken = wechatUtil.getAccessToken(Integer.valueOf(siteId.toString()));
//        String[] keys = new String[]{"USER_FORM_INFO_FLAG_NAME","USER_FORM_INFO_FLAG_SEX","USER_FORM_INFO_FLAG_BIRTHDAY","USER_FORM_INFO_FLAG_MOBILE","USER_FORM_INFO_FLAG_EMAIL","USER_FORM_INFO_FLAG_DETAIL_LOCATION"};
//        List<String> list  = new ArrayList<>();
//        List<String> strings = Arrays.asList(keys);
        ReturnDto returnDto = null;
        try {
                queryMp.put("card_id",card_id);
                queryMp.put("code",card_code);
                String cardInfo = HttpClient.doHttpPost(BASEURL + wechatCardBagConfig.QUERYCARDINFO + accessToken, JSON.toJSONString(queryMp));
                Map map2 = JSON.parseObject(cardInfo, Map.class);
                if ("ok".equals(map2.get("errmsg"))) {
                    //TODO 跳转url之后才能拿到activity_ticket
                    //TODO通过接口激活之后可以拿到手机号
                    Object user_info1 = map2.get("user_info");
                    if (StringUtil.isEmpty(user_info1)) {
                        returnDto  = ReturnDto.buildFailedReturnDto("没有查询到会员信息!");
                    }else {
                        Map user_info = (Map)user_info1;
                        Object common_field_list1 = user_info.get("common_field_list");
                        if (StringUtil.isEmpty(common_field_list1)) {
                            returnDto  = ReturnDto.buildFailedReturnDto("没有查询到会员信息!");
                        }else {
                            List common_field_list = (List)common_field_list1;
                            Map<String,Object> map = new HashedMap();
                            common_field_list.stream().forEach(fields -> {
                                Map fields1 = (Map) fields;
//                                if (strings.contains(name)) {
//                                    map.put("",value);
//                                }
                                map.put(String.valueOf(fields1.get("name")),fields1.get("value"));

                            });
                            returnDto = ReturnDto.buildSuccessReturnDto(map);


                            /*Map map3 = (Map) common_field_list.get(0);
                            String phone = String.valueOf(map3.get("value"));
                            if (StringUtil.isNotEmpty(phone)) {
                                returnDto = ReturnDto.buildSuccessReturnDto(phone);
                            }else {
                                returnDto  = ReturnDto.buildFailedReturnDto("没有查询到会员手机号!");
                            }*/
                        }
                    }

                }else {
                    returnDto = ReturnDto.buildFailedReturnDto("查询会员信息失败!");
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  returnDto;
    }

}
