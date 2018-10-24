package com.jk51.modules.abutmentInterface.service;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.SBMember;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.modules.persistence.mapper.SBMemberMapper;
import com.jk51.modules.privatesend.core.PrivateSend;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.service.TradesService;
import com.jk51.modules.wechat.service.StoreMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName HeartServeService
 * @Description 心服务
 * @Date 2018-08-16 17:24
 */
@Service
public class HeartServeService {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartServeService.class);

    @Autowired
    MemberMapper memberMapper;

    @Autowired
    SBMemberMapper sbMemberMapper;

    @Autowired
    private StoreMemberService storeMemberService;

    @Autowired
    private PrivateSend privateSend;

    @Autowired
    private TradesService tradesService;

    public Map<String, Object> queryMemInfo(Map<String, Object> parameterMap) {
        return  memberMapper.getMemberInfoByOpenId(parameterMap);
    }

    public Map<String,Object> queryIsMember(String openId, Integer siteId) {
        return memberMapper.queryMemInfo(openId,siteId);
    }

    @Transactional
    public ReturnDto sendUserInfos(Map<String, Object> parameterMap) {
        String userPhone = String.valueOf(parameterMap.get("userPhone"));
        Integer siteId = Integer.valueOf(parameterMap.get("siteId").toString());
        SBMember member = sbMemberMapper.selectByPhoneNum(userPhone, siteId);
        int insertA = 0;
        int insertB = 0;
        if (member == null) {
            SBMember member1 = new SBMember();
            SBMemberInfo memberInfo1 = new SBMemberInfo();
            member1.setMobile(userPhone);
            member1.setSite_id(siteId);
            //用户来源: 110 (网站) ; 120（微信）; 130（app）; 140 (后台手工录入) ; 9999（其它）,
            member1.setMem_source(120);
            member1.setName(String.valueOf(parameterMap.get("userName")));
            memberInfo1.setSite_id(siteId);
            try {
                insertA = storeMemberService.addMember(member1, memberInfo1);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("HeartServeService : {}",e.getMessage());
            }
        }else {
            insertA = 1;
        }
        //拼接ID规则    用户编号(buyer_id)+设备编号(equipmentNumber)+商家编号(site_id)+门店编号(store_id)+openId
        //查询记录
        Map<String,Object> map1 = memberMapper.queryBySiteIdAndPhone(siteId, userPhone);
        Object buyerId = map1.get("buyerId");
        String equipmentNumber = String.valueOf(parameterMap.get("equipmentNumber"));
        Object storeId = map1.get("storeId");
//        Object openId = map1.get("openId");
        Object openId = parameterMap.get("openId");
        String uniqueId = buyerId + "#" + equipmentNumber + "#" + siteId + "#" + storeId + "#" +openId;
        parameterMap.put("uniqueUserId51jk", uniqueId);
        //查询是否已经有记录
        Map<String,Object> isHavaLog = memberMapper.queryUserInfo(parameterMap);
        if (Objects.isNull(isHavaLog)) {
            //插入新表作为记录
            insertB = memberMapper.inserUserInfoLog(parameterMap);
        }else {
            insertB = 1;
        }
        //调用接口传送用户注册数据
        String url = "https://www.zhiyunxinkang.com/51jk/user/registration";

        Map<String,Object> parameter = new HashMap<String,Object>();
        parameter.put("accessKeyId",parameterMap.get("accessKeyId"));//跃技医疗事先提供给51jk
        parameter.put("accessKeySecret",parameterMap.get("accessKeySecret"));//跃技医疗事先提供给51jk
        parameter.put("uniqueUserId51jk",uniqueId);
        parameter.put("name",parameterMap.get("userName"));
        parameter.put("cellPhone",parameterMap.get("userPhone"));
        int sex = Integer.parseInt(parameterMap.get("sex").toString());
        String gender;
        if (0 == sex) {
            gender = "女";
        }else if (1 == sex) {
            gender = "男";
        }else {
            gender = "保密";
        }
        parameter.put("gender",gender);
        parameter.put("age",parameterMap.get("age"));
        parameter.put("bloodPressureHigh",parameterMap.get("bloodPressureHigh"));
        parameter.put("bloodPressureLow",parameterMap.get("bloodPressureLow"));
        parameter.put("bloodGlu",parameterMap.get("bloodGlucose"));
        parameter.put("height",parameterMap.get("height"));
        parameter.put("weight",parameterMap.get("weight"));
        parameter.put("personalIntroduction",parameterMap.get("conditionSum"));
//        parameter.put("deviceId",parameterMap.get("deviceId"));//二维码中包含该信息
        parameter.put("macAddress",parameterMap.get("deviceId"));//二维码中包含该信息
        ReturnDto returnDto = null;
        try {
            String result = HttpClient.doHttpPost(url, JSON.toJSONString(parameter));
            LOGGER.debug("sendUserInfos 提交用户信息到跃技医疗 {}", result);
            if (StringUtil.isNotEmpty(result)) {
                Map map = JSON.parseObject(result, Map.class);
                String status = String.valueOf(map.get("status"));
                if ("success".equals(status)) {
                    if (1 == insertA && 1 == insertB) {
//                        returnDto = ReturnDto.buildSuccessReturnDto("提交信息成功!");
                        LOGGER.info("sendUserInfos 提交信息成功");
                        //查看是否连接成功
                        /*String checkCon = "https://www.zhiyunxinkang.com/51jk/device/connection/status";
                        Map<String, Object> params = new HashedMap();
                        params.put("accessKeyId", parameterMap.get("accessKeyId"));
                        params.put("accessKeySecret", parameterMap.get("accessKeySecret"));
                        params.put("deviceBind51jkId", map.get("deviceBind51jkId"));
                        String s = HttpClient.doHttpPost(checkCon, JSON.toJSONString(params));
                        if (StringUtil.isNotEmpty(s)) {
                            Map map2 = JSON.parseObject(s, Map.class);
                            if ("success".equals(map2.get("status"))) {
                                if ("C".equals(map2.get("connectionStatus"))) {
                                    LOGGER.info("sendUserInfos 提交信息成功, 设备已连接!");
                                    return ReturnDto.buildSuccessReturnDto("提交信息成功, 设备已连接!");
                                }else {
                                    return ReturnDto.buildFailedReturnDto("提交信息成功, 但设备未连接!");
                                }
                            }else {
                                return ReturnDto.buildFailedReturnDto("提交信息成功, 但设备未连接!");
                            }
                        }else {
                            return ReturnDto.buildFailedReturnDto("提交信息成功, 但设备未连接!");
                        }*/
                        Map<String, Object> params = new HashMap<String,Object>();
                        params.put("accessKeyId", parameterMap.get("accessKeyId"));
                        params.put("accessKeySecret", parameterMap.get("accessKeySecret"));
                        params.put("deviceBind51jkId", map.get("deviceBind51jkId"));
                        returnDto = ReturnDto.buildSuccessReturnDto(params);
                    }else {
                        LOGGER.error("sendUserInfos 提交信息成功, 保存到数据库记录失败!");
                        returnDto = ReturnDto.buildFailedReturnDto("提交信息成功, 保存到数据库记录失败!");
                    }
                }else {
                    returnDto = ReturnDto.buildFailedReturnDto("提交信息失败, 101!");
                }
            }else {
                returnDto = ReturnDto.buildFailedReturnDto("提交信息失败, 101!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            returnDto = ReturnDto.buildFailedReturnDto("提交信息失败, 101!"+e.getMessage());
        }

        return returnDto;
    }

    //保存数据调用模版
    public String saveAndCall(Map<String, Object> parameterMap) {
        int i = memberMapper.saveCheckLog(parameterMap);
        if (1 == i) {
            //调用模版
            int templateId = Integer.parseInt(parameterMap.get("templateId").toString());
            String[] split = parameterMap.get("uniqueUserId51jk").toString().split("#");
            String siteId = split[2];
            String openId = split[4];
            //TODO 将设备号和recordId拼接传递
            String shopwxUrl = tradesService.getShopwxUrl(Integer.valueOf(siteId));
//            String url = "http://"+siteId+".weixin-pre.51jk.wang/my/ecgDetectionDetail?equipmentNumber="+ split[1] + "&recordId=" + parameterMap.get("recordId");    //跳转页面
            String url = shopwxUrl+"/my/ecgDetectionDetail?equipmentNumber="+ split[1] + "&recordId=" + parameterMap.get("recordId");    //跳转页面
            String remark = "点击查看报告详情";
            Map map = null;
//            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-ddHH:mm");
//            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (1 == templateId) {//正常
                LOGGER.debug("HeartService saveAndCall  1 开始调用微信检测结果模版发送................................");
                String normalRemark = "欢迎您再次使用。点击查看报告详情。";
                //Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3, String keyword4
                //用户编号(buyer_id)+设备编号(equipmentNumber)+商家编号(site_id)+门店编号(store_id)+openId
                String first = "您好, 您刚刚检测了心电, 结果如下";
                String keyword1 = parameterMap.get("heartRate") + "次/分钟";//心率
                String keyword2 = parameterMap.get("recordTime").toString();//诊断时间
//                Date parse = sdf1.parse(keyword2);
//                String format = sdf2.format(parse);
                String keyword3 = parameterMap.get("testResults").toString();//诊断结果
                String keyword4 = parameterMap.get("conclusion").toString();//诊断结论
                map = privateSend.ecgResultMessage(Integer.valueOf(siteId), openId, url, first, normalRemark, keyword1, keyword2, keyword3, keyword4);//调用模版
            }else if (2 == templateId) {//异常1
                LOGGER.debug("HeartService saveAndCall  2 开始调用微信检测结果模版发送................................");
                String first = "您好, 您刚刚检测了心电, 结果如下";
                String keyword1 = parameterMap.get("heartRate") + "次/分钟";
                String keyword2 = parameterMap.get("recordTime").toString();
//                Date parse = sdf1.parse(keyword2);
//                String format = sdf2.format(parse);
                String keyword3 = parameterMap.get("testResults").toString();
                String keyword4 = parameterMap.get("conclusion").toString();
                map = privateSend.ecgResultMessage(Integer.valueOf(siteId), openId, url, first, remark, keyword1, keyword2, keyword3, keyword4);
            }else if (3 == templateId) {//异常2
                LOGGER.debug("HeartService saveAndCall  3 开始调用微信检测结果模版发送................................");
                String first = "您好, 您于"+parameterMap.get("recordTime").toString()+"检测了心电";
                String keyword1 = "";
                String keyword2 = "";
                String keyword3 = "";
                String keyword4 = "专家正在审读心电图，将在12小时内给出结果。您可能接到我们来自021-64390010的电话，请您保持电话畅通。";
                map = privateSend.ecgResultMessage(Integer.valueOf(siteId), openId, url, first, "", keyword1, keyword2, keyword3, keyword4);
            }else {//干扰过大
                LOGGER.debug("HeartService saveAndCall  4 开始调用微信检测结果模版发送................................");
                String first = "您好, 您刚刚检测了心电, 结果如下";
                String keyword1 = "--";
                String keyword2 = parameterMap.get("recordTime").toString();
//                Date parse = sdf1.parse(keyword2);
//                String format = sdf2.format(parse);
                String keyword3 = parameterMap.get("testResults").toString();
                String keyword4 = parameterMap.get("conclusion").toString();
                map = privateSend.ecgResultMessage(Integer.valueOf(siteId), openId, url, first, remark, keyword1, keyword2, keyword3, keyword4);
            }
            if ("ok".equals(map.get("errmsg"))) {
                Map<String,Object> map1 = new HashMap<String,Object>();
                map1.put("status", "success");
                map1.put("error", false);
                map1.put("errorMessage", "调用模版成功!");
                return JSON.toJSONString(map1);
            }else {
                Map<String,Object> map2 = new HashMap<String,Object>();
                map2.put("status", "failed");
                map2.put("error", true);
                map2.put("errorMessage", "调用模版失败!");
                return JSON.toJSONString(map2);
            }

        }else {
            Map<String,Object> map3 = new HashMap<String,Object>();
            map3.put("status", "failed");
            map3.put("error", true);
            map3.put("errorMessage", "存储记录失败!");
            return JSON.toJSONString(map3);
        }
    }

    public Map<String, Object> queryEquipmentInfos(Map<String, Object> equipmentNumber) {
        return  memberMapper.queryEquipment(equipmentNumber);
    }

    public ReturnDto queryUserRegistionInfo(Map<String, Object> parameterMap) {
        Map<String,Object> map = memberMapper.queryUserInfo(parameterMap);
        if (Objects.isNull(map)) {
            return ReturnDto.buildFailedReturnDto("注册信息为空!");
        }else {
            return ReturnDto.buildSuccessReturnDto(map);
        }
    }
}
