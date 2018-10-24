package com.jk51.modules.appInterface.service;


import com.jk51.commons.string.StringUtil;
import com.jk51.modules.appInterface.mapper.BMobileWechatMapper;
import com.jk51.modules.appInterface.util.AuthToken;
import com.jk51.modules.im.mapper.ChAnswerRelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-07
 * 修改记录:
 */
@Service
@ConfigurationProperties(prefix="RLIM")
public class ChatService {

    @Autowired
    private UsersService usersService;
    @Autowired
    private ChAnswerRelationMapper chAnswerRelationMapper;
    @Autowired
    private BMobileWechatMapper bMobileWechatMapper;
    private String appid;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }





    //获取联系人,根据APP的容联Id查询chAnswerRelation表中的用户容联ID
    public Map<String,Object> getanswerrelation(String sender) {


        Map<String,Object> result = new HashMap<String,Object>();
        List<Map<String,String>> receiver = new ArrayList<Map<String,String>>();
        Map<String,Object> results = new HashMap<String,Object>();

        List<String> userOpenIdList = chAnswerRelationMapper.findUserOpenId(sender);
        if(!StringUtil.isEmpty(userOpenIdList)){

           for(String str:userOpenIdList){
               Map<String,String> map = new HashMap<>();
               map.put("openId",str);
               map.put("memberId",str);
               receiver.add(map);
           }

        }

        results.put("statusCode",0);
        results.put("receiver",receiver);
        result.put("results",results);
        result.put("status","OK");

        return result;
    }

    //修改昵称
    public Map<String,Object> editremark(String sender, String receiver, String remark) {

        Map<String,Object> result = new HashMap<String,Object>();
        if(StringUtil.isEmpty(sender)|| StringUtil.isEmpty(receiver)|| StringUtil.isEmpty(remark)){
            result.put("status","ERROR");
            result.put("errorMessage","参数为空");
            return result;
        }
        if(remark.length()>10){
            result.put("status","ERROR");
            result.put("errorMessage","昵称长度大于10");
            return result;
        }

        int num = chAnswerRelationMapper.updateRemark(sender,receiver,remark);
        if(num>=1){
            result.put("status","OK");
        }else{
            result.put("status","ERROR");
            result.put("errorMessage","更新昵称失败");
        }

        return result;
    }

    //获取昵称
    public Map<String,Object> getuserremark(String sender, String receiver) {

        Map<String,Object> result = new HashMap<String,Object>();
        Map<String,Object> results = new HashMap<>();

        List<String> chAnswerRelationList = chAnswerRelationMapper.getRemark(sender,receiver);
        if(StringUtil.isEmpty(chAnswerRelationList)||chAnswerRelationList.size()==0){
            result.put("status","ERROR");
            result.put("errorMessage","查询昵称失败");
            return result;
        }
        result.put("status","OK");
        results.put("remark",chAnswerRelationList.get(0));
        results.put("statusCode",0);
        result.put("results",results);


        return result;
    }

    //获取下单手机号
    public Map<String,Object> gettelbyopenidoruid(String sender, String accessToken) {

        Map<String,Object> result = new HashMap<String,Object>();

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if(StringUtil.isEmpty(authToken)){
            result.put("status","ERROR");
            result.put("errorMessage","accessToken解析失败！");
            return result;
        }

        if(StringUtil.isEmpty(sender)){
            result.put("status","ERROR");
            result.put("errorMessage","sender为空");
            return result;
        }

        String userId = usersService.getYbmemberIdByToken(sender);

        List<String> mobiles = bMobileWechatMapper.findMobile(userId,authToken.getSiteId());
        if(StringUtil.isEmpty(mobiles)||mobiles.size()==0){
            result.put("status","ERROR");
            result.put("errorMessage","查询手机号失败");
            return result;
        }

        Map<String,Object> results = new HashMap<String,Object>();
        results.put("mobile",mobiles.get(0));
        result.put("status","OK");
        result.put("results",results);
        return result;
    }


}
