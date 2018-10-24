package com.jk51.modules.offline.service;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.commons.xml.XmlUtils;
import com.jk51.modules.appInterface.mapper.BMemberInfoMapper;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-09-21
 * 修改记录:
 */
@Service
public class ZhongLianService {

    private static final Logger logger = LoggerFactory.getLogger(ZhongLianService.class);

    private static final String BASE_URL = "http://58.60.4.60:8015/axis2/services/InsiderService/";

    @Autowired
    private BMemberMapper memberMapper;

    @Autowired
    private BMemberInfoMapper memberInfoMapper;
    @Autowired
    private ErpToolsService erpToolsService;

    /**
     * 获取会员信息，登陆后同步信息+个人中心获取会员信息
     *
     * @param mobile
     * @return
     */
    public Map<String, Object> getUser(Integer siteId, String mobile, String invite_code) {
        Map<String, Object> erpMem = new HashMap<>();
        Map<String, Object> responseParams = new HashMap<>();
        Map<String, Object> memToWx = new HashMap<>();
        Map member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(siteId), mobile);
        Map<String, Object> getMemberInfo = memberInfoMapper.erpGetMemberInfo(siteId, mobile, null);
        String uid = "";
        if (!StringUtil.isEmpty(getMemberInfo.get("uid"))) {
            uid = getMemberInfo.get("uid").toString();
        }
        String url = "getquery?mobile=" + mobile + "&placepointid=" + uid;
        logger.info("##中联获取会员信息##请求地址是:[{}],手机号:[{}],店员邀请码[{}],对应的门店编码", url, mobile, invite_code, uid);
        try {
            String reresult = OkHttpUtil.get(BASE_URL + url);
            erpMem = JacksonUtils.json2map(XmlUtils.xml2map(reresult).get("return").toString());
            logger.info("##中联获取会员信息##请求地址是:[{}],返回值：[{}]", url, erpMem);
            if (!erpMem.get("code").toString().equals("0")) {
                responseParams.put("code", 2);
                responseParams.put("msg", "获取失败");
                return responseParams;
            }
        } catch (Exception e) {
            logger.info("解析异常" + e);//类型转换异常则没有查询到数据,接口调用失败
            responseParams.put("code", 2);
            responseParams.put("msg", "获取失败");
            return responseParams;
        }
        Map<String, Object> erp_params = (Map) ((List) erpMem.get("info")).get(0);
        //更新b_member 表信息
        Map memberMap = new HashMap();
        memberMap.put("name", erp_params.get("name"));
        memToWx.put("name", memberMap.get("name"));
        if (!erp_params.get("sex").equals("") && erp_params.get("sex") != null) {
            Integer sex = erp_params.get("sex").equals("男") ? 1 : 0;
            memberMap.put("sex", sex);
        } else {
            memberMap.put("sex", 3);
        }
        memToWx.put("sex", erp_params.get("sex"));
        memberMap.put("site_id", siteId);
        memberMap.put("mobile", mobile);
        memToWx.put("mobile", memberMap.get("mobile"));
        int i = memberMapper.updateMember(memberMap);
        try {
            memToWx.put("birthday", DateUtils.convert(erp_params.get("birthday").toString(), "yyyy-MM-dd"));
        } catch (Exception e) {
            logger.info("生日更新失败");
        }
        if (i != 0) {//更新b_member_info表信息
            Map memberInfoMap = new HashMap();
            Map<String, Object> areaIds = erpToolsService.getareaIds(erp_params.get("address").toString());
            memberInfoMap.put("province", areaIds.get("province"));
            memberInfoMap.put("city", areaIds.get("city"));
            memberInfoMap.put("area", areaIds.get("area"));
            memberInfoMap.put("address", areaIds.get("address"));
            memToWx.put("address", erp_params.get("address"));
            try {
                memberInfoMap.put("birthday", DateUtils.convert(erp_params.get("birthday").toString(), "yyyy-MM-dd"));
            } catch (Exception e) {
                memberInfoMap.put("birthday", "1970-01-01");
            }
            memToWx.put("birthday", memberInfoMap.get("birthday"));
            memberInfoMap.put("card_no", erp_params.get("card_no"));
            memberInfoMap.put("site_id", siteId);
            memberInfoMap.put("member_id", member.get("buyer_id"));
            memToWx.put("card_no", mobile);
            int j = memberInfoMapper.updateMemberInfo(memberInfoMap);
            if (j != 0) {
                memberMapper.updateFirstErp(siteId, mobile);
                responseParams.put("code", 0);
                responseParams.put("info", memToWx);
                responseParams.put("msg", "会员信息同步成功");
            } else {
                responseParams.put("code", 2);
                responseParams.put("info", memToWx);
                responseParams.put("msg", "会员信息更新失败");
            }
        } else {//51后台没有会员信息
            responseParams.put("code", -1);
            responseParams.put("info", memToWx);
            responseParams.put("msg", "会员信息更新失败");
        }
        return responseParams;
    }

    /**
     * 获取会员线下积分总数
     *
     * @param siteId
     * @param mobile
     * @return
     */
    public Map<String, Object> getIntegrate(Integer siteId, String mobile) {
        String url = "queryScore?mobile=" + mobile;
        logger.info("##中联获取会员积分##请求地址是:[{}],手机号:[{}],", BASE_URL + url, mobile);
        Map<String, Object> erp_params = new HashMap<>();
        Map<String, Object> responseParams = new HashMap<>();
        BigInteger olIntegral = new BigInteger("0");
        try {
            String reresult = OkHttpUtil.get(BASE_URL + url);
            erp_params = JacksonUtils.json2map(XmlUtils.xml2map(reresult).get("return").toString());
            if (erp_params.get("code").equals("0")) {
                olIntegral = new BigInteger((String) ((Map) ((List) erp_params.get("info")).get(0)).get("gold_score"));
            }
        } catch (Exception e) {
            logger.info("解析异常" + e);//类型转换异常则没有查询到数据,接口调用失败
            responseParams.put("code", 0);
            responseParams.put("msg", "积分同步失败");
            return responseParams;
        }
        logger.info("offlineScore:{}", olIntegral);
        //更新线下积分
        Map map = new HashMap();
        map.put("olIntegral", olIntegral);
        map.put("gold_score", olIntegral);
        map.put("siteId", siteId);
        map.put("mobile", mobile);
        List list = new ArrayList();
        list.add(map);
        int i = memberMapper.updateOfflineIntegral(map);
        if (i != 0) {
            responseParams.put("code", 0);
            responseParams.put("msg", "更新成功");
            responseParams.put("info", list);
        } else {
            responseParams.put("code", 0);
            responseParams.put("msg", "积分同步失败");
            responseParams.put("info", list);
        }
        return responseParams;
    }

    /**
     * 会员信息更新
     * map.put("mobile_no","18812345678");
     * map.put("name","ccc");
     * map.put("sex","男");
     * Dataformat df = new DateFormat("yyyy-MM-dd");
     * map.put("birthday","1987-10-13");
     * map.put("address","浙江省杭州市西湖区南山路");
     */
    public Map<String, Object> updateUser(Integer siteId, String mobile, String name, String sex, String birthday, String address) {
        Map<String, Object> responseParams = new HashMap<>();
        String url = "updateInsider?mobile=" + mobile + "&name=" + name + "&sex=" + sex + "&birthday=" + birthday + "&address=" + address + "";
        logger.info("##中联更新会员信息接口##请求地址是:[{}],手机号:[{}],姓名:[{}],性别:[{}],生日:[{}],住址:[{}],", BASE_URL + url, mobile);
        Map<String, Object> erp_params = new HashMap<>();
        try {
            String reresult = OkHttpUtil.get(BASE_URL + url);
            erp_params = JacksonUtils.json2map(XmlUtils.xml2map(reresult).get("return").toString());
            if (Integer.parseInt(erp_params.get("code").toString()) == 0) {//修改先下信息操作成功，将信息保存到线上
                return getUser(siteId, mobile, null);//调用获取会员方法
            }
        } catch (Exception e) {
            logger.info("解析异常" + e);//类型转换异常则没有查询到数据,接口调用失败
            responseParams.put("code", -1);
            responseParams.put("msg", "更新异常，稍后重试！");
            return responseParams;
        }
        return erp_params;
    }

}


