package com.jk51.modules.im.service.wechatUtil;

import com.jk51.commons.http.HttpClient;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.merchant.MerchantExt;
import com.jk51.modules.merchant.mapper.MerchantExtMapper;
import com.jk51.modules.persistence.mapper.SBMemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-09-04
 * 修改记录:
 */
@Service
public class WechatUtil {


    private Logger logger = LoggerFactory.getLogger(WechatUtil.class);

    @Autowired
    private SBMemberMapper sbMemberMapper;
    @Autowired
    private MerchantExtMapper merchantExtMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    private static final String TENCENT_ACCESS_TOKEN="tencent_access_token";
    /**
     * 从b_member表中获取openid
     * */
    public String getOpenId(Integer siteId,Integer buyerId){

       return sbMemberMapper.getOpenId(siteId,buyerId);
    }



    /**
     * 获取access_token
     * */
    public String getAccessToken(Integer siteId){
        String result = getFromRedisAccessToken(siteId);
//        String result = "";
        if(!StringUtil.isEmpty(result)){
            return result;
        }
        MerchantExt merchantExt = merchantExtMapper.selectByMerchantId(siteId);
        try {
            result = HttpClient.doHttpGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+merchantExt.getWx_appid()+"&secret="+merchantExt.getWx_secret());
            Map<String,Object> accessToken = JacksonUtils.json2map(result);

            if(!StringUtil.isEmpty(accessToken.get("access_token"))){
                Integer timeout=Integer.parseInt(accessToken.get("expires_in")+"");
                setToRedisAccessToken(siteId, accessToken.get("access_token").toString(),timeout);
                return accessToken.get("access_token").toString();
            }
        } catch (Exception e) {

            logger.error("获取AccessToken失败。siteId:{},报错信息：{},result：{}",siteId,e,result);
        }
        return "";
    }
    public String getFromRedisAccessToken(Integer siteId) {
        return stringRedisTemplate.opsForValue().get(TENCENT_ACCESS_TOKEN + "_" + siteId);
    }
    public void setToRedisAccessToken(Integer siteId, String accessToken,long timeout) {
        stringRedisTemplate.opsForValue().set(TENCENT_ACCESS_TOKEN + "_" + siteId, accessToken, timeout, TimeUnit.SECONDS);
    }
    /**
     * 获取wechatInfo
     * */
    public WechatInfo getWechatInfo(Integer siteId, Integer buyerId){


        String result = "";
        try {
            result =  HttpClient.doHttpGet("https://api.weixin.qq.com/cgi-bin/user/info?access_token="+getAccessToken(siteId)+"&openid="+getOpenId(siteId,buyerId)+"&lang=zh_CN");

            WechatInfo wechatInfo = JacksonUtils.json2pojo(result,WechatInfo.class);

            if(!StringUtil.isEmpty(wechatInfo)){
                return wechatInfo;
            }
        } catch (Exception e) {

            logger.error("获取wechatInfo失败。siteId:{}，buyerId：{},报错信息：{},result：{}",siteId,buyerId,e,result);
        }


        return null;

    }


}
