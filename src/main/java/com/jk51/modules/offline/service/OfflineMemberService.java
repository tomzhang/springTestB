package com.jk51.modules.offline.service;

import com.jk51.annotation.TimeRequired;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.commons.xml.XmlUtils;
import com.jk51.modules.appInterface.mapper.BMemberInfoMapper;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import com.jk51.modules.offline.mapper.BErpOrderLogMapper;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-11-02
 * 修改记录:线下会员信息对接
 */
@Service
public class OfflineMemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineMemberService.class);

    @Autowired
    private MerchantERPMapper merchantERPMapper;
    @Autowired
    private ErpToolsService erpToolsService;
    @Autowired
    private BMemberMapper memberMapper;
    @Autowired
    private BMemberInfoMapper memberInfoMapper;
    @Autowired
    private GuangJiOfflineService guangJiOfflineService;
    @Autowired
    private BaoDaoOfflineService baoDaoOfflineService;
    @Autowired
    private DeKangOfflineService deKangOfflineService;
    @Autowired
    private BErpOrderLogMapper erpOrderLogMapper;

    //查询erp会员接口
    public Map<String, Object> getuser(Integer siteId, String mobile, String invite_code) {
        LOGGER.info("erp获取会员信息，商家id{},会员手机号码:{},店员邀请码:{}.", siteId, mobile, invite_code);
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(siteId)));
        Map member = memberMapper.selectMemberInfoToWX(siteId, mobile);
        if (StringUtil.isEmpty(merchantErpInfo)) {
            result.put("code", 400);
            result.put("msg", "刚商户未对接erp");
            return result;
        }
        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
        Integer status = Integer.parseInt(merchantErpInfo.get("status").toString());//erp开关状态 0 关闭  1 正常
        Integer memberFlag = Integer.parseInt(merchantErpInfo.get("member").toString());//模块开关 0 关闭  1 正常
        if (status == 1 && memberFlag == 1) {
            Map<String, Object> response = new HashMap<>();
            try {
                if (siteId == 100180) {//千金商户
                    response = qianjin_getUser(siteId, baseUrl, mobile, member.get("buyerId"), member.get("uid"));
                } else if (siteId == 100166) {//九洲,前端显示会员卡号，不是手机号码
                    response = jiuzhou_getUser(siteId, baseUrl, mobile, invite_code, member.get("buyerId"), member.get("uid"));
                } else if (siteId == 100190) {//天润
                    response = tianrun_getUser(siteId, baseUrl, mobile, member.get("buyerId"), member.get("uid"));
                } else if (siteId == 100204) {//广济，比较特殊，提供的是数据库权限
                    response = guangJiOfflineService.getUser(siteId, mobile, member);
                } else if (siteId == 100173) {//中联
                    response = zhonglian_getUser(siteId, baseUrl, mobile, member);
                } else if (siteId == 100030) {//宝岛
                    response = baoDaoOfflineService.getUser(siteId, mobile, member);
                } else if (siteId == 100213 || siteId == 100239 || siteId == 100271) {//济生,天伦，内蒙国大
                    response = haidian_getUser(siteId, baseUrl, mobile, member);
                } else if (siteId == 100272) {//成都聚仁堂
                    response = jurentang_getUser(siteId, baseUrl, mobile, member);
                } else if (siteId == 100268) {//德仁堂
                    response = derentang_getUser(siteId, baseUrl, mobile, invite_code, member.get("buyerId"), member.get("uid"));
                }
            /*else if (siteId == 100215) {//德康
                response = deKangOfflineService.getUser(siteId, mobile, invite_code, member.get("buyer_id"));
            }*/
                else {
                    LOGGER.info("会员通道未打通");
                    result.put("code", 404);
                    result.put("msg", "会员通道未打通");
                    return result;
                }
            } catch (Exception e) {
                LOGGER.info("erp会员信息获取有误" + e.getMessage());
                response.put("code", 404);
            }
            if (Integer.parseInt(response.get("code").toString()) == 200) {
                LOGGER.info("获取会员信息接口成功");
                result.put("code", 200);
                return result;
            } else if (Integer.parseInt(response.get("code").toString()) == 400) {
                LOGGER.info("调取会员信息接口成功，但是该会员不存在，也没有自动创建成功");
                result.put("code", 400);
                return result;
            } else {
                LOGGER.info("调取会员信息接口失败,siteId:{},mobile:{}", siteId, mobile);
                result.put("code", 404);
                result.put("msg", "调取会员信息接口失败");
                return result;
            }
        } else {//erp对接通道关闭
            LOGGER.info("erp对接通道关闭");
            result.put("code", 404);
            result.put("msg", "erp对接通道关闭");
            return result;
        }
    }

    /**
     * 获取千金会员信息,返回用户的个人信息
     *
     * @param siteId
     * @param mobile
     * @return
     */
//    @TimeRequired
    public Map<String, Object> qianjin_getUser(Integer siteId, String baseUrl, String mobile, Object buyerId, Object uid) {
        Map<String, Object> responseParams = new HashMap<>();
        Map<String, Object> resultMem = new HashMap<>();//接收erp接口信息
        Map<String, Object> memberMap = new HashMap<>();
        Map<String, Object> memberInfoMap = new HashMap<>();
        String url = baseUrl + "/wscitymemberservlet?mobile=" + mobile;
        LOGGER.info("千金商户获取会员信息,请求地址是:[{}],手机号:[{}],店员邀请码[{}]", url, mobile);
        resultMem = erpToolsService.requestErp(url);
        erpOrderLogMapper.insertSelectErpLog(siteId, url, mobile, JacksonUtils.mapToJson(resultMem));
        if (!StringUtil.isEmpty(resultMem.get("code")) && !"-1".equals(resultMem.get("code").toString())) { //判断是否正常获取数据,如果该用户信息不存在 返回数据
            if ("0".equals(resultMem.get("code").toString())) {//该会员不存在或者参数不正确
                Map<String, Object> addResult = qianjin_addUser(baseUrl, siteId, mobile, uid);//调用新增接口
                LOGGER.info("千金新增会员接口返回值:{}", addResult.toString());
                resultMem = erpToolsService.requestErp(url);//重新调用查询接口
            }
        } else {//访问千金erp会员接口失败
            responseParams.put("code", 404);
            responseParams.put("msg", "接口调用失败");
            return responseParams;
        }
        Map erpMember = (Map) ((List) resultMem.get("info")).get(0);  //成功获取线下会员信息
        memberMap.put("name", erpMember.get("name"));//获取会员姓名
        if (StringUtil.isNotBlank((String) erpMember.get("sex"))) {
            Integer sex = erpMember.get("sex").equals("男") ? 1 : 0;
            memberMap.put("sex", sex);
        } else {
            memberMap.put("sex", 3);
        }
        memberMap.put("idcard_number", erpMember.get("certif_no"));
        memberMap.put("email", erpMember.get("email"));
        memberMap.put("site_id", siteId);
        memberMap.put("mobile", mobile);
        memberInfoMap.put("address", erpMember.get("address"));
        try {
            memberInfoMap.put("birthday", DateUtils.convert(erpMember.get("birthday").toString(), "yyyy-MM-dd"));
        } catch (Exception e) {
            memberInfoMap.put("birthday", "1970-01-01");
        }
        memberInfoMap.put("card_no", erpMember.get("card_no"));//获取会员卡号
        memberInfoMap.put("site_id", siteId);
        memberInfoMap.put("member_id", buyerId);
        updateMemberInfo(memberMap, memberInfoMap);
        memberMapper.updateFirstErp(siteId, mobile);//信息修改成功，对接成功
        responseParams.put("code", 200);
        responseParams.put("msg", "erp会员信息对接成功");
        return responseParams;
    }

    /**
     * 获取九洲会员信息
     *
     * @param siteId
     * @param mobile
     * @return
     */
//    @TimeRequired
    public Map<String, Object> jiuzhou_getUser(Integer siteId, String baseUrl, String mobile, String invite_code, Object buyerId, Object uid) {
        Map<String, Object> responseParams = new HashMap<>();
        Map<String, Object> memberMap = new HashMap<>();
        Map<String, Object> memberInfoMap = new HashMap<>();
        String url = baseUrl + "/user/get?mobile=" + mobile + "&invite_code=" + invite_code + "";
        LOGGER.info("九洲商户获取会员信息,请求地址是:[{}],手机号:[{}],店员邀请码[{}]", url, mobile, invite_code);
        Map<String, Object> resultMem = erpToolsService.requestErp(url);//接收erp接口信息
        erpOrderLogMapper.insertSelectErpLog(siteId, url, mobile, JacksonUtils.mapToJson(resultMem));
        if (!StringUtil.isEmpty(resultMem.get("code")) && (Integer) resultMem.get("code") == -1) {//九洲用户信息获取失败
            responseParams.put("code", 404);
            responseParams.put("msg", "接口调用失败");
            return responseParams;
        }
        Map erpMember = (Map) ((List) resultMem.get("info")).get(0);
        memberMap.put("name", erpMember.containsKey("name") ? erpMember.get("name") : null);//获取会员姓名
        memberMap.put("sex", erpMember.containsKey("sex") ? (erpMember.get("sex").equals("男") ? 1 : (erpMember.get("sex").equals("女") ? 0 : 3)) : 3);
        memberMap.put("email", erpMember.containsKey("email") ? erpMember.get("email") : null);
        memberMap.put("site_id", siteId);
        memberMap.put("mobile", mobile);
        memberInfoMap.put("address", erpMember.containsKey("address") ? erpMember.get("address").toString() : "");
        try {
            memberInfoMap.put("birthday", DateUtils.convert(erpMember.get("birthday").toString(), "yyyy-MM-dd"));
        } catch (Exception e) {
            LOGGER.info("九洲用户手机号码:{},生日{}解析异常原因:{}", mobile, erpMember.get("birthday"), e.getMessage());
            memberInfoMap.put("birthday", "1970-01-01");
        }
        memberInfoMap.put("card_no", erpMember.containsKey("card_no") ? erpMember.get("card_no") : null);
        memberInfoMap.put("site_id", siteId);
        memberInfoMap.put("member_id", buyerId);
        updateMemberInfo(memberMap, memberInfoMap);
        memberMapper.updateFirstErp(siteId, mobile);
        responseParams.put("code", 200);
        return responseParams;
    }

    /**
     * 获取天润会员信息
     *
     * @param siteId
     * @param mobile
     * @return
     */
//    @TimeRequired
    public Map<String, Object> tianrun_getUser(Integer siteId, String baseUrl, String mobile, Object buyerId, Object uid) {
        Map<String, Object> responseParams = new HashMap<>();
        Map<String, Object> memberMap = new HashMap<>();
        Map<String, Object> memberInfoMap = new HashMap<>();
        String url = baseUrl + "/getinfo/" + mobile + "/0";
        LOGGER.info("天润商户获取会员信息,请求地址是:[{}],手机号:[{}],门店编码[{}]", url, mobile, uid);
        Map<String, Object> resultMem = erpToolsService.requestErp(url);//接收erp接口信息
        erpOrderLogMapper.insertSelectErpLog(siteId, url, mobile, JacksonUtils.mapToJson(resultMem));
        if (!StringUtil.isEmpty(resultMem.get("code")) && (Integer) resultMem.get("code") == -1) {//是否正常获取数据
            responseParams.put("code", 404);
            responseParams.put("msg", "接口调用失败");
            return responseParams;
        }
        Map erpMember = (Map) ((List) resultMem.get("info")).get(0);
        memberMap.put("name", erpMember.containsKey("huiyname") ? erpMember.get("huiyname") : null);//获取会员姓名
        memberMap.put("sex", erpMember.containsKey("sex") ? (erpMember.get("sex").equals("男") ? 1 : (erpMember.get("sex").equals("女") ? 0 : 3)) : 3);
        memberMap.put("email", erpMember.containsKey("email") ? erpMember.get("email") : null);
        memberMap.put("site_id", siteId);
        memberMap.put("mobile", mobile);
        memberInfoMap.put("address", erpMember.containsKey("address") ? erpMember.get("address").toString() : "");
        try {
            memberInfoMap.put("birthday", DateUtils.convert(erpMember.get("csrq").toString(), "yyyy-MM-dd"));
        } catch (Exception e) {
            LOGGER.info("天润用户手机号码:{},生日{}解析异常原因:{}", mobile, erpMember.get("csrq"), e.getMessage());
            memberInfoMap.put("birthday", "1970-01-01");
        }
        memberInfoMap.put("card_no", erpMember.containsKey("huiybh") ? erpMember.get("huiybh") : null);
        memberInfoMap.put("site_id", siteId);
        memberInfoMap.put("member_id", buyerId);
        updateMemberInfo(memberMap, memberInfoMap);
        memberMapper.updateFirstErp(siteId, mobile);
        responseParams.put("code", 200);
        return responseParams;
    }

    /**
     * 中联商户获取线下会员信息
     *
     * @param siteId
     * @param baseUrl
     * @param mobile
     * @param getMemberInfo
     * @return
     */
//    @TimeRequired
    public Map<String, Object> zhonglian_getUser(Integer siteId, String baseUrl, String mobile, Map<String, Object> getMemberInfo) {
        Map<String, Object> erpMem = new HashMap<>();
        Map<String, Object> responseParams = new HashMap<>();
        String uid = "";
        if (!StringUtil.isEmpty(getMemberInfo.get("uid"))) {
            uid = getMemberInfo.get("uid").toString();
        }
        String url = baseUrl + "/getquery?mobile=" + mobile + "&placepointid=" + uid;
        LOGGER.info("中联获取会员信息请求地址是:[{}],手机号:[{}]对应的门店编码[{}]", url, mobile, uid);
        try {
            String reresult = OkHttpUtil.get(url);
            erpMem = JacksonUtils.json2map(XmlUtils.xml2map(reresult).get("return").toString());
            erpOrderLogMapper.insertSelectErpLog(siteId, url, mobile, JacksonUtils.mapToJson(erpMem));
            LOGGER.info("中联获取会员信息请求地址是:[{}],返回值：[{}]", url, erpMem);
            if (!erpMem.get("code").toString().equals("0")) {
                responseParams.put("code", 404);
                responseParams.put("msg", "获取失败");
                return responseParams;
            }
        } catch (Exception e) {
            LOGGER.info("中联获取会员信息接口解析异常" + e.getMessage());//类型转换异常则没有查询到数据,接口调用失败
            responseParams.put("code", 405);
            responseParams.put("msg", "获取失败");
            return responseParams;
        }
        Map<String, Object> erpMember = (Map) ((List) erpMem.get("info")).get(0);
        //更新b_member 表信息
        Map memberMap = new HashMap();
        memberMap.put("name", erpMember.containsKey("name") ? erpMember.get("name") : null);//获取会员姓名
        memberMap.put("sex", erpMember.containsKey("sex") ? (erpMember.get("sex").equals("男") ? 1 : (erpMember.get("sex").equals("女") ? 0 : 3)) : 3);
        memberMap.put("site_id", siteId);
        memberMap.put("mobile", mobile);
        Map memberInfoMap = new HashMap();
        memberInfoMap.put("address", erpMember.containsKey("address") ? erpMember.get("address").toString() : "");
        try {
            memberInfoMap.put("birthday", DateUtils.convert(erpMember.get("birthday").toString(), "yyyy-MM-dd"));
        } catch (Exception e) {
            memberInfoMap.put("birthday", "1970-01-01");
        }
        memberInfoMap.put("card_no", erpMember.containsKey("card_no") ? erpMember.get("card_no") : null);
        memberInfoMap.put("site_id", siteId);
        memberInfoMap.put("member_id", getMemberInfo.get("buyerId"));
        Integer updatecode = updateMemberInfo(memberMap, memberInfoMap);
        memberMapper.updateFirstErp(siteId, mobile);
        responseParams.put("code", 200);
        return responseParams;
    }

    /**
     * 济生堂,天伦，国大商户获取线下会员信息
     * <p>
     * 海典系统
     */
//    @TimeRequired
    public Map<String, Object> haidian_getUser(Integer siteId, String baseUrl, String mobile, Map<String, Object> getMemberInfo) {
        Map<String, Object> erpMem = new HashMap<>();
        Map<String, Object> responseParams = new HashMap<>();
        String uid = "";
        String memberName = "";
        if (!StringUtil.isEmpty(getMemberInfo.get("uid"))) {
            uid = getMemberInfo.get("uid").toString();
        }
        if (!StringUtil.isEmpty(getMemberInfo.get("memberName"))) {
            memberName = getMemberInfo.get("memberName").toString();
        }
        String url = baseUrl + "/user/get";
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("mobile", mobile);
        LOGGER.info("erp{}获取会员信息请求地址是:{},手机号:{},对应的门店编码{}", siteId, url, mobile, uid);
        try {
            erpMem = erpToolsService.requestHeaderPar(url, requestParams);
            erpOrderLogMapper.insertSelectErpLog(siteId, url, mobile, JacksonUtils.mapToJson(erpMem));
            LOGGER.info("erp{}获取会员信息请求地址是:{},返回值：{}", siteId, url, erpMem);
            if (!erpMem.containsKey("code")) {
                responseParams.put("code", 400);
                responseParams.put("msg", "获取失败");
                return responseParams;
            } else if (erpMem.get("code").toString().equals("-1")) {
                LOGGER.info("erp{}该会员信息不存在", siteId);
                erpMem = handian_addUser(baseUrl, siteId, mobile, uid, memberName);//新增会员信息
                if (!erpMem.containsKey("code") || !erpMem.get("code").toString().equals("0")) {
                    responseParams.put("code", 405);
                    responseParams.put("msg", "新增会员失败");
                    return responseParams;
                }
            }
        } catch (Exception e) {
            LOGGER.info("erp{}获取会员信息接口解析异常{}", siteId, e.getMessage());//类型转换异常则没有查询到数据,接口调用失败
            responseParams.put("code", 405);
            responseParams.put("msg", "获取失败");
            return responseParams;
        }
        Map<String, Object> erpMember = (Map) ((List) erpMem.get("info")).get(0);
        //更新b_member 表信息
        Map memberMap = new HashMap();
        memberMap.put("name", erpMember.containsKey("name") ? erpMember.get("name") : null);//获取会员姓名
        memberMap.put("sex", erpMember.containsKey("sex") ?
            (erpMember.get("sex").toString().trim().equals("男") ?
                1 : (erpMember.get("sex").toString().trim().equals("女") ? 0 : 3)) : 3);
        memberMap.put("site_id", siteId);
        memberMap.put("mobile", mobile);
        Map memberInfoMap = new HashMap();
        memberInfoMap.put("address", erpMember.containsKey("address") ?
            (StringUtil.isEmpty(erpMember.get("address")) ? "" : erpMember.get("address").toString()) : "");
        try {
            memberInfoMap.put("birthday", DateUtils.convert(erpMember.get("birthday").toString(), "yyyy-MM-dd"));
        } catch (Exception e) {
            memberInfoMap.put("birthday", "1970-01-01");
        }
        memberInfoMap.put("card_no", erpMember.containsKey("memcardno") ? erpMember.get("memcardno") : null);
        memberInfoMap.put("site_id", siteId);
        memberInfoMap.put("member_id", getMemberInfo.get("buyerId"));
        updateMemberInfo(memberMap, memberInfoMap);
        memberMapper.updateFirstErp(siteId, mobile);
        responseParams.put("code", 200);
        return responseParams;
    }

    /**
     * 聚仁堂商户获取线下会员信息
     * <p>
     * 海典系统
     *
     * @param siteId
     * @param baseUrl
     * @param mobile
     * @return
     */
//    @TimeRequired
    public Map<String, Object> jurentang_getUser(Integer siteId, String baseUrl, String mobile, Map<String, Object> getMemberInfo) {
        Map<String, Object> erpMem = new HashMap<>();
        Map<String, Object> responseParams = new HashMap<>();
        String uid = "";
        if (!StringUtil.isEmpty(getMemberInfo.get("uid"))) {
            uid = getMemberInfo.get("uid").toString();
        }
        String url = baseUrl + "/MemCards";
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("mobile", mobile);
        requestParams.put("uid", uid);
        LOGGER.info("erp{}获取会员信息请求地址是:{},手机号:{},对应的门店编码{}",
            siteId, url, mobile, uid);
        try {
            erpMem = erpToolsService.requestHeaderPar(url, requestParams);
            erpOrderLogMapper.insertSelectErpLog(siteId, url, mobile, JacksonUtils.mapToJson(erpMem));
            LOGGER.info("erp{}获取会员信息请求地址是:{},返回值：{}", siteId, url, erpMem);
            if (!erpMem.containsKey("code")) {
                responseParams.put("code", 400);
                responseParams.put("msg", "获取失败");
                return responseParams;
            }
        } catch (Exception e) {
            LOGGER.info("erp{}获取会员信息接口解析异常{}", siteId, e.getMessage());//类型转换异常则没有查询到数据,接口调用失败
            responseParams.put("code", 405);
            responseParams.put("msg", "获取失败");
            return responseParams;
        }
        Map<String, Object> erpMember = (Map) ((List) erpMem.get("info")).get(0);
        //更新b_member 表信息
        Map memberMap = new HashMap();
        memberMap.put("name", erpMember.containsKey("Name") ? erpMember.get("Name") : null);//获取会员姓名
        memberMap.put("sex", erpMember.containsKey("sex") ?
            (erpMember.get("sex").toString().trim().equals("男") ?
                1 : (erpMember.get("sex").toString().trim().equals("女") ? 0 : 3)) : 3);
        memberMap.put("site_id", siteId);
        memberMap.put("idcard_number", erpMember.containsKey("certif_no") ? erpMember.get("certif_no") : null);//身份证号
        memberMap.put("mobile", mobile);
        Map memberInfoMap = new HashMap();
        memberInfoMap.put("address", erpMember.containsKey("address") ?
            (StringUtil.isEmpty(erpMember.get("address")) ? "" : erpMember.get("address").toString()) : "");
        try {
            memberInfoMap.put("birthday", DateUtils.convert(erpMember.get("birthday").toString(), "yyyy-MM-dd"));
        } catch (Exception e) {
            memberInfoMap.put("birthday", "1970-01-01");
        }
        memberInfoMap.put("card_no", erpMember.containsKey("Card_no") ? erpMember.get("Card_no") : null);
        memberInfoMap.put("site_id", siteId);
        memberInfoMap.put("member_id", getMemberInfo.get("buyerId"));
        updateMemberInfo(memberMap, memberInfoMap);
        memberMapper.updateFirstErp(siteId, mobile);
        responseParams.put("code", 200);
        return responseParams;
    }

    /**
     * 德仁堂商户获取线下会员信息
     * <p>
     * 海典系统
     *
     * @param siteId
     * @param baseUrl
     * @param mobile
     * @param buyerId
     * @return
     */
//    @TimeRequired
    public Map<String, Object> derentang_getUser(Integer siteId, String baseUrl, String mobile, String invite_code, Object buyerId, Object uid) {
        Map<String, Object> erpMem = new HashMap<>();
        Map<String, Object> responseParams = new HashMap<>();
        String url = baseUrl + "user/get";
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("mobile", mobile);
        requestParams.put("uid", uid);
        LOGGER.info("erp{}获取会员信息请求地址是:{},手机号:{},店员邀请码{},对应的门店编码{}",
            siteId, url, mobile, invite_code, uid);
        try {
            erpMem = JacksonUtils.json2map(OkHttpUtil.postJson(url, JacksonUtils.mapToJson(requestParams)));
            erpOrderLogMapper.insertSelectErpLog(siteId, url, mobile, JacksonUtils.mapToJson(erpMem));
            LOGGER.info("erp{}获取会员信息请求地址是:{},返回值：{}", siteId, url, erpMem);
            if ((!erpMem.containsKey("code")) || !("1".equals(erpMem.get("code").toString()))) {
                responseParams.put("code", 400);
                responseParams.put("msg", "获取失败");
                return responseParams;
            }
        } catch (Exception e) {
            LOGGER.info("erp{}获取会员信息接口解析异常{}", siteId, e.getMessage());//类型转换异常则没有查询到数据,接口调用失败
            responseParams.put("code", 405);
            responseParams.put("msg", "获取失败");
            return responseParams;
        }
        Map<String, Object> erpMember = (Map) ((List) erpMem.get("info")).get(0);
        //更新b_member 表信息
        Map memberMap = new HashMap();
        memberMap.put("name", erpMember.containsKey("name") ? erpMember.get("name") : null);//获取会员姓名
        memberMap.put("sex", erpMember.containsKey("sex") ?
            (erpMember.get("sex").toString().trim().equals("男") ?
                1 : (erpMember.get("sex").toString().trim().equals("女") ? 0 : 3)) : 3);
        memberMap.put("site_id", siteId);
        memberMap.put("idcard_number", erpMember.containsKey("certif_no") ? erpMember.get("certif_no") : null);//身份证号
        memberMap.put("mobile", mobile);
        Map memberInfoMap = new HashMap();
        memberInfoMap.put("address", erpMember.containsKey("address") ?
            (StringUtil.isEmpty(erpMember.get("address")) ? "" : erpMember.get("address").toString()) : "");
        try {
            memberInfoMap.put("birthday", DateUtils.convert(erpMember.get("birthday").toString(), "yyyy-MM-dd"));
        } catch (Exception e) {
            memberInfoMap.put("birthday", "1970-01-01");
        }
        memberInfoMap.put("card_no", erpMember.containsKey("card_no") ? erpMember.get("card_no") : null);
        memberInfoMap.put("site_id", siteId);
        memberInfoMap.put("member_id", buyerId);
        updateMemberInfo(memberMap, memberInfoMap);
        memberMapper.updateFirstErp(siteId, mobile);
        responseParams.put("code", 200);
        return responseParams;
    }
    /**
     * 会员如果不存在，通知商户增加会员
     *
     * @param
     * @return
     */
/*    public Map<String, Object> addMember(Integer siteId, String mobile) {
        LOGGER.info("添加会员--siteId:[{}],mobile:[{}],busno:[{}]", siteId, mobile);
        Map<String, Object> responseParams = new HashMap<>();
        Map<String, Object> erpMap = merchantERPMapper.selectMerchantERPInfo(siteId);
        if (StringUtil.isEmpty(erpMap)) {
            responseParams.put("code", 400);
            responseParams.put("msg", "该商户不存在,添加会员信息失败");
            return responseParams;
        }
        String baseUrl = erpMap.get("erpUrl").toString();//erp请求地址
        Integer status = Integer.parseInt(erpMap.get("status").toString());//erp开关状态
        Integer memberFlag = Integer.parseInt(erpMap.get("member").toString());//模块开关 0 关闭  1 正常
        if (status == 1 && memberFlag == 1) {
            if (siteId == 100180) {//目前手动绑定会员信息的商户只有千金一家
                return qianjin_addUser(baseUrl, siteId, mobile);
            } else {
                LOGGER.info("会员通道未打通");
                responseParams.put("code", 400);
                responseParams.put("msg", "会员通道未打通");
                return responseParams;
            }
        } else {
            LOGGER.info("erp对接通道关闭");
            responseParams.put("code", 400);
            responseParams.put("msg", "erp对接通道关闭");
            return responseParams;
        }
    }*/

    /**
     * 添加千金会员信息
     *
     * @return
     */
    public Map<String, Object> qianjin_addUser(String baseUrl, Integer siteId, String mobile, Object uid) {
        Map<String, Object> responseParams = new HashMap<>();
        String busno = "101218";
        if (!StringUtil.isEmpty(uid)) {
            busno = uid.toString();
        }
        LOGGER.info("千金添加会员mobile:[{}],busno:[{}]", mobile, busno);
        String url = baseUrl + "/wscitymemberinsertservlet?mobile=" + mobile + "&busno=" + busno;
        Map<String, Object> result = erpToolsService.requestErp(url);
        if (!StringUtil.isEmpty(result.get("code")) && !"-1".equals(result.get("code").toString())) {
            if ("1".equals(result.get("code").toString())) {//千金会员添加成功
                responseParams.put("code", 200);
                responseParams.put("msg", "千金会员添加成功");
                return responseParams;
            } else {
                responseParams.put("code", 400);
                responseParams.put("msg", "千金会员添加失败");
                return responseParams;
            }
        } else {
            responseParams.put("code", 400);
            responseParams.put("msg", "千金会员添加接口调用失败");
            return responseParams;
        }
    }

    /**
     * 添加济生会员信息
     *
     * @return
     */
    public Map<String, Object> handian_addUser(String baseUrl, Integer siteId, String mobile, String uid, String memberName) {
        Map<String, Object> responseParams = new HashMap<>();
        try {
            String url = baseUrl + "/user/add";
            LOGGER.info("erp{}添加会员请求地址:{},mobile:{},uid:{}", siteId, url, mobile, uid);
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("mobile", mobile);
            requestParams.put("name", memberName);
            requestParams.put("sex", null);
            requestParams.put("birthday", null);
            requestParams.put("address", null);
            requestParams.put("store_number", uid);
            Map<String, Object> result = erpToolsService.requestHeaderPar(url, requestParams);
            if (result.containsKey("code")) {
                if ("0".equals(result.get("code").toString())) {//千金会员添加成功
                    return result;
                } else {
                    responseParams.put("code", 500);
                    responseParams.put("msg", "会员添加失败或该手机号码对应会员已存在。");
                    return responseParams;
                }
            } else {
                responseParams.put("code", 400);
                responseParams.put("msg", "会员添加接口调用失败");
                return responseParams;
            }
        } catch (Exception e) {
            LOGGER.info("sitId:{},会员新增接口调用失败。原因:{}。", siteId, e.getMessage());
            responseParams.put("code", 400);
            responseParams.put("msg", "会员添加接口调用失败");
            return responseParams;
        }
    }

    /**
     * 更新会员信息
     *
     * @return
     */
    public Map<String, Object> updateMemberinfo(Map<String, Object> requestParams) {
        Integer siteId = Integer.parseInt(requestParams.get("siteId").toString());
        String mobile = requestParams.get("mobile").toString();
        String name = StringUtil.isEmpty(requestParams.get("name")) ? "" : requestParams.get("name").toString();
        String sex = StringUtil.isEmpty(requestParams.get("sex")) ? "" : Integer.parseInt(requestParams.get("sex").toString()) == 3 ? "" : Integer.parseInt(requestParams.get("sex").toString()) == 1 ? "男" : "女";
        String birthday = StringUtil.isEmpty(requestParams.get("birthday")) ? "" : requestParams.get("birthday").toString();
        String address = StringUtil.isEmpty(requestParams.get("address")) ? "" : requestParams.get("address").toString();
        LOGGER.info("erp修改会员信息，siteId:{},mobile:{},name:{},sex:{},birthday:{},address:{}",
            siteId, mobile, name, sex, birthday, address);
        Map<String, Object> responseParams = new HashMap<>();
        Map<String, Object> erpMap = merchantERPMapper.selectMerchantERPInfo(siteId);
        if (StringUtil.isEmpty(erpMap)) {
            responseParams.put("code", 400);
            responseParams.put("msg", "该商户不存在");
            return responseParams;
        }
        String baseUrl = erpMap.get("erpUrl").toString();//erp请求地址
        Integer status = Integer.parseInt(erpMap.get("status").toString());//erp开关状态 0 关闭  1 正常
        Integer memberFlag = Integer.parseInt(erpMap.get("member").toString());//模块开关 0 关闭  1 正常
        if (status == 1 && memberFlag == 1) {
            if (siteId == 100180) {
                return qianjin_updateUser(siteId, baseUrl, mobile, name, sex, birthday);
            } else if (siteId == 100166) {
                return jiuzhou_updateUser(siteId, baseUrl, mobile, name, sex, birthday, address);
            } else if (siteId == 100190) {
                return tianrun_updateUser(siteId, baseUrl, mobile, name, sex, birthday, address);
            } else if (siteId == 100204) {
                return guangJiOfflineService.updateUser(siteId, mobile, name, sex, birthday, address);
            } else if (siteId == 100173) {
                return zhonglian_updateUser(siteId, baseUrl, mobile, name, sex, birthday, address);
            } else if (siteId == 100030) {
                return baoDaoOfflineService.updateUser(siteId, mobile, name, sex, birthday, address);
            } else if (siteId == 100213 || siteId == 100239 || siteId == 100271) {
                return handian_updateUser(siteId, baseUrl, mobile, name, sex, birthday, address);
            } else if (siteId == 100272) {
                return jurentang_updateUser(siteId, baseUrl, mobile, name, sex, birthday, address);
            } else if (siteId == 100268) {
                return derentang_updateUser(siteId, baseUrl, mobile, name, sex, birthday, address);
            }
           /* else if (siteId == 100215) {
                return deKangOfflineService.updateUser(siteId, mobile, name, sex, birthday, address);
            }*/
            else {
                LOGGER.info("会员通道未打通.");
                responseParams.put("code", 400);
                responseParams.put("msg", "会员通道未打通");
                return responseParams;
            }
        } else {
            LOGGER.info("erp对接通道关闭");
            responseParams.put("code", 400);
            responseParams.put("msg", "erp对接通道关闭");
            return responseParams;
        }
    }

    //    @TimeRequired
    public Map<String, Object> qianjin_updateUser(Integer siteId, String baseUrl, String mobile, String name, String sex, String birthday) {
        Map<String, Object> responseParams = new HashMap<>();
        String url = baseUrl + "/wscitymemberupdateservlet?mobile=" + mobile + "&cardholder=" + name + "&sex=" + sex + "&birthday=" + birthday + "";
        LOGGER.info("千金用户更改会员信息,请求地址:{},手机号码:{},姓名:{},性别:{},生日:{}.", url, mobile, name, sex, birthday);
        Map<String, Object> erpMap = erpToolsService.requestErp(url);//修改先下信息操作成功
        if (!StringUtil.isEmpty(erpMap) && "1".equals(erpMap.get("code").toString())) {
            responseParams.put("code", 200);
            responseParams.put("msg", "千金线下会员信息修改成功");
            return responseParams;
        } else {
            responseParams.put("code", 400);
            responseParams.put("msg", "会员信息修改失败");
            return responseParams;
        }
    }

    //    @TimeRequired
    public Map<String, Object> jiuzhou_updateUser(Integer siteId, String baseUrl, String mobile, String name, String sex, String birthday, String address) {
        Map<String, Object> resquest = new HashMap<>();
        resquest.put("mobile_no", mobile);
        resquest.put("name", name);
        resquest.put("sex", sex);
        resquest.put("birthday", birthday);
        resquest.put("address", address);
        Map<String, Object> responseParams = new HashMap<>();
        String url = baseUrl + "/user/update";
        Map<String, Object> result = erpToolsService.requestErp(url, resquest);
        if (!StringUtil.isEmpty(result) && (Integer) result.get("code") == 0) {//修改先下信息操作成功
            responseParams.put("code", 200);
            responseParams.put("msg", "九洲线下会员信息修改成功");
            return responseParams;
        } else {
            responseParams.put("code", 400);
            responseParams.put("msg", "会员信息修改失败");
            return responseParams;
        }
    }

    //    @TimeRequired
    public Map<String, Object> tianrun_updateUser(Integer siteId, String baseUrl, String mobile, String name, String sex, String birthday, String address) {
        if (StringUtil.isEmpty(sex)) {
            sex = "null";
        }
        if (StringUtil.isEmpty(birthday)) {
            birthday = "null";
        }
        if (StringUtil.isEmpty(address) || address.equals("|")) {
            address = "null";
        }
        Map<String, Object> responseParams = new HashMap<>();
        try {
            address = address.toString().replace("|", "%7C");
            LOGGER.info("处理后地址地址为" + address);
        } catch (Exception e) {
            LOGGER.info("处理地址中的特殊字符失误" + e);
        }
        String url = baseUrl + "/updateinfo/" + mobile + "/" + name + "/" + sex + "/" + birthday + "/" + address + "";
        Map<String, Object> result = erpToolsService.requestErp(url);
        if (!StringUtil.isEmpty(result) && (Integer) result.get("code") == 0) {//修改先下信息操作成功，将信息保存到线上
            responseParams.put("code", 200);
            responseParams.put("msg", "天润线下会员信息修改成功");
            return responseParams;
        } else {
            responseParams.put("code", 400);
            responseParams.put("msg", "会员信息修改失败");
            return responseParams;
        }
    }

    //    @TimeRequired
    public Map<String, Object> zhonglian_updateUser(Integer siteId, String baseUrl, String mobile, String name, String sex, String birthday, String address) {
        Map<String, Object> responseParams = new HashMap<>();
        String url = baseUrl + "/updateInsider?mobile=" + mobile + "&name=" + name + "&sex=" + sex + "&birthday=" + birthday + "&address=" + address + "";
        LOGGER.info("中联更新会员信息接口,请求地址是:[{}],手机号:[{}],姓名:[{}],性别:[{}],生日:[{}],住址:[{}],", url, mobile);
        Map<String, Object> erp_params = new HashMap<>();
        try {
            String reresult = OkHttpUtil.get(url);
            erp_params = JacksonUtils.json2map(XmlUtils.xml2map(reresult).get("return").toString());
            if (Integer.parseInt(erp_params.get("code").toString()) == 0) {//修改先下信息操作成功，将信息保存到线上
                responseParams.put("code", 200);
                responseParams.put("msg", "中联线下会员信息修改成功");
                return responseParams;
            } else {
                responseParams.put("code", 400);
                responseParams.put("msg", "更新异常，稍后重试！");
                return responseParams;
            }
        } catch (Exception e) {
            LOGGER.info("解析异常" + e.getMessage());//类型转换异常则没有查询到数据,接口调用失败
            responseParams.put("code", 404);
            responseParams.put("msg", "更新异常，稍后重试！");
            return responseParams;
        }
    }

    //    @TimeRequired
    public Map<String, Object> handian_updateUser(Integer siteId, String baseUrl, String mobile, String name, String sex, String birthday, String address) {
        Map<String, Object> responseParams = new HashMap<>();
        Map<String, Object> requestParams = new HashedMap();
        requestParams.put("mobile", mobile);
        requestParams.put("name", name);
        requestParams.put("sex", sex);
        requestParams.put("birthday", birthday);
        requestParams.put("address", address);
        String url = baseUrl + "/user/update";
        LOGGER.info("erp{},更新会员信息接口,请求地址是:{},手机号:{},姓名:{},性别:{},生日:{},住址:{}.",
            siteId, url, mobile, name, sex, birthday, address);
        Map<String, Object> erp_params = new HashMap<>();
        try {
            erp_params = erpToolsService.requestHeaderPar(url, requestParams);
            if (erp_params.containsKey("code") && erp_params.get("code").equals("0")) {//修改先下信息操作成功，将信息保存到线上
                responseParams.put("code", 200);
                responseParams.put("msg", "海典线下会员信息修改成功");
                return responseParams;
            } else {
                responseParams.put("code", 400);
                responseParams.put("msg", "更新异常，稍后重试！");
                return responseParams;
            }
        } catch (Exception e) {
            LOGGER.info("erp{}添加会员解析异常{}", siteId, e.getMessage());//类型转换异常则没有查询到数据,接口调用失败
            responseParams.put("code", 404);
            responseParams.put("msg", "更新异常，稍后重试！");
            return responseParams;
        }
    }

    //成都聚仁堂修改会员信息
//    @TimeRequired
    public Map<String, Object> jurentang_updateUser(Integer siteId, String baseUrl, String mobile, String name, String sex, String birthday, String address) {
        Map<String, Object> responseParams = new HashMap<>();
        Map<String, Object> requestParams = new HashedMap();
        requestParams.put("mobile", mobile);
        requestParams.put("name", name);
        requestParams.put("sex", sex);
        requestParams.put("birthday", birthday);
        requestParams.put("address", address);
        String url = baseUrl + "/MemCards/update";
        LOGGER.info("erp{},更新会员信息接口,请求地址是:{},手机号:{},姓名:{},性别:{},生日:{},住址:{}.",
            siteId, url, mobile, name, sex, birthday, address);
        Map<String, Object> erp_params = new HashMap<>();
        try {
            erp_params = erpToolsService.requestHeaderPar(url, requestParams);
            if (erp_params.containsKey("code") && erp_params.get("code").equals("0")) {//修改先下信息操作成功，将信息保存到线上
                responseParams.put("code", 200);
                responseParams.put("msg", "聚仁堂线下会员信息修改成功");
                return responseParams;
            } else {
                responseParams.put("code", 400);
                responseParams.put("msg", "更新异常，稍后重试！");
                return responseParams;
            }
        } catch (Exception e) {
            LOGGER.info("erp{}添加会员解析异常{}", siteId, e.getMessage());//类型转换异常则没有查询到数据,接口调用失败
            responseParams.put("code", 404);
            responseParams.put("msg", "更新异常，稍后重试！");
            return responseParams;
        }
    }

    //成都德仁堂修改会员信息
//    @TimeRequired
    public Map<String, Object> derentang_updateUser(Integer siteId, String baseUrl, String mobile, String
        name, String sex, String birthday, String address) {
        Map<String, Object> responseParams = new HashMap<>();
        Map<String, Object> requestParams = new HashedMap();
        requestParams.put("Mobile", mobile);
        requestParams.put("name", name);
        requestParams.put("sex", sex);
        requestParams.put("birthday", birthday);
        requestParams.put("address", address);
        String url = baseUrl + "user/update";
        LOGGER.info("erp{},更新会员信息接口,请求地址是:{},手机号:{},姓名:{},性别:{},生日:{},住址:{}.",
            siteId, url, mobile, name, sex, birthday, address);
        Map<String, Object> erp_params = new HashMap<>();
        try {
            erp_params = JacksonUtils.json2map(OkHttpUtil.postJson(url, JacksonUtils.mapToJson(requestParams)));
            if (erp_params.containsKey("code") && erp_params.get("code").equals("1")) {//修改先下信息操作成功，将信息保存到线上
                responseParams.put("code", 200);
                responseParams.put("msg", "德仁堂线下会员信息修改成功");
                return responseParams;
            } else {
                responseParams.put("code", 400);
                responseParams.put("msg", "更新异常，稍后重试！");
                return responseParams;
            }
        } catch (Exception e) {
            LOGGER.info("erp{}添加会员解析异常{}", siteId, e.getMessage());//类型转换异常则没有查询到数据,接口调用失败
            responseParams.put("code", 404);
            responseParams.put("msg", "更新异常，稍后重试！");
            return responseParams;
        }
    }

    /**
     * 更新线上会员信息
     *
     * @param memberMap     要更新的会员主表信息
     * @param memberInfoMap 要更新的会员扩展表信息
     * @return
     */
    @Transactional
    public Integer updateMemberInfo(Map<String, Object> memberMap, Map<String, Object> memberInfoMap) {
        int i = memberMapper.updateMember(memberMap);
        int j = memberInfoMapper.updateMemberInfo(memberInfoMap);
        if (i != 0 && j != 0) {
            return 1;
        } else {
            return -1;
        }
    }
}
