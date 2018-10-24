package com.jk51.modules.offline.service;

import com.jk51.commons.http.HttpClientManager;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.configuration.PathUrlConfig;
import com.jk51.modules.appInterface.mapper.BMemberInfoMapper;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-03-31
 * 修改记录:
 */
@Service
public class JiuZOfflineService {
    //"http://122.225.202.20:18080/ExService/user/get"
    private static final Logger logger = LoggerFactory.getLogger(JiuZOfflineService.class);

    @Autowired
    private PathUrlConfig pathUrlConfig;

    @Autowired
    private BMemberMapper memberMapper;

    @Autowired
    private BMemberInfoMapper memberInfoMapper;
    @Autowired
    private ErpToolsService erpToolsService;

    @Autowired
    private MerchantERPMapper merchantERPMapper;

    /**
     * 数据查询
     *
     * @param list 参数列表
     * @return
     */
    private Map<String, Object> httpPost(String url, List list) {
        Map<String, Object> erpMap = merchantERPMapper.selectMerchantERPInfo(100166);
        if (!erpMap.containsKey("erpUrl")) {
            return null;
        }
        String baseUrl = erpMap.get("erpUrl").toString();//erp请求地址
        String msg = "";
        try {
            String result = HttpClientManager.httpPostRequeset(baseUrl + url, list);
            logger.info("###########" + baseUrl + url + "#############");
            logger.info("#####" + result + "#####");
            return JacksonUtils.json2map(result);
        } catch (IOException e) {
            logger.error("erp请求异常" + e);
            e.printStackTrace();
            msg = "erp请求异常" + e;
        } catch (Exception e) {
            logger.error("数据解析异常" + e);
            e.printStackTrace();
            msg = "数据解析异常" + e;
        }
        Map<String, Object> res = new HashMap<>();
        res.put("code", -1);
        res.put("status", msg);
        return res;
    }

 /*   *//**
     * 获取会员信息
     *
     * @param mobile
     * @return
     *//*
    @Transactional
    public Map<String, Object> getUser(Integer siteId, String mobile, String invite_code) {
        Integer code = 0;
        String msg = "";
        String url = "/user/get";
        Map member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(siteId), mobile);
        logger.info("##获取会员信息##请求地址是:[{}],手机号:[{}],店员邀请码[{}]", url, mobile, invite_code);
        List list = new ArrayList();
        list.add(new BasicNameValuePair("mobile", mobile));
        list.add(new BasicNameValuePair("invite_code", invite_code));
        Map<String, Object> result = httpPost(url, list);
        if ((Integer) result.get("code") == -1) {//判断是否正常获取数据
            return result;
        }
        Map map = (Map) ((List) result.get("info")).get(0);

        //更新b_member 表信息
        Map memberMap = new HashMap();
        memberMap.put("name", map.get("name"));
        if (!map.get("sex").equals("") && map.get("sex") != null) {
            Integer sex = map.get("sex").equals("男") ? 1 : 0;
            memberMap.put("sex", sex);
        } else {
            memberMap.put("sex", 3);
        }
        memberMap.put("email", map.get("email") == null ? "" : map.get("email"));
        memberMap.put("site_id", siteId);
        memberMap.put("mobile", mobile);
        int i = memberMapper.updateMember(memberMap);
        if (i != 0) {//更新b_member_info表信息
            Map memberInfoMap = new HashMap();
            Map<String, Object> areaIds = erpToolsService.getareaIds(map.get("address").toString());
            memberInfoMap.put("province", areaIds.get("province"));
            memberInfoMap.put("city", areaIds.get("city"));
            memberInfoMap.put("area", areaIds.get("area"));
            memberInfoMap.put("address", areaIds.get("address"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = sdf.parse(map.get("birthday").toString());
                memberInfoMap.put("birthday", date);
            } catch (ParseException e) {
                memberInfoMap.put("birthday", "1970-01-01");
                ((Map) ((List) result.get("info")).get(0)).put("birthday", "1970-01-01");
            }
            memberInfoMap.put("card_no", map.get("card_no"));
            memberInfoMap.put("site_id", siteId);
            memberInfoMap.put("member_id", member.get("buyer_id"));
            int j = memberInfoMapper.updateMemberInfo(memberInfoMap);
            if (j != 0) {
                memberMapper.updateFirstErp(siteId, mobile);
                return result;
            } else {
                code = 2;
                msg = "会员信息更新失败";
            }
        } else {//51后台没有会员信息
            code = -1;
            msg = "会员信息更新失败";
        }
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }*/

    /**
     * 会员信息更新
     * map.put("mobile_no","18812345678");
     * map.put("name","ccc");
     * map.put("sex","男");
     * Dataformat df = new DateFormat("yyyy-MM-dd");
     * map.put("birthday","1987-10-13");
     * map.put("address","浙江省杭州市西湖区南山路");
     *
     * @param param
     * @return
     */
/*    @Transactional
    public Map<String, Object> updateUser(Map<String, Object> param) {
        String url = "/user/update";
        List list = new ArrayList();
        for (String s : param.keySet()) {
            list.add(new BasicNameValuePair(s, param.get(s).toString()));
        }
        Map<String, Object> result = httpPost(url, list);
        if ((Integer) result.get("code") == 0) {//修改先下信息操作成功，将信息保存到线上
            return getUser(Integer.parseInt(param.get("siteId") + ""), param.get("mobile_no").toString(), null);//调用获取会员方法
        }
        return result;
    }*/

    /**
     * 获取金豆积分
     *
     * @param mobile_no
     * @return
     */
    @Transactional
    public Map<String, Object> queryScore(Integer siteId, String mobile_no) {
        Map<String, Object> response = new HashMap<>();

        Integer code = 1;
        String msg = "";
        if (siteId != 100166) {
            response.put("code", -1);
            return response;
        }
        String url = "/user/queryScore";
        logger.info("##领取积分##请求地址是:[{}],手机号:[{}]", url, mobile_no);
        List list = new ArrayList();
        list.add(new BasicNameValuePair("mobile_no", mobile_no));
        Map<String, Object> result = httpPost(url, list);
        if ((Integer) result.get("code") != 1)
            return result;//判断是否成功请求到数据(String)((Map)((List)result.get("info"))).get("gold_score")
        BigInteger olIntegral = new BigInteger((String) ((Map) ((List) result.get("info")).get(0)).get("gold_score"));
        logger.info("offlineScore:{}", olIntegral.toString());
        //更新线下积分
        Map map = new HashMap();
        map.put("olIntegral", olIntegral);
        map.put("siteId", siteId);
        map.put("mobile", mobile_no);

        int i = memberMapper.updateOfflineIntegral(map);
        if (i != 0) {
            msg = "积分同步成功";
        } else {
            code = 0;
            msg = "积分同步失败";
        }
        result.put("code", code);
        result.put("msg", msg);

        return result;
    }

    /**
     * 获取积分领取列表
     *
     * @param mobile_no
     * @return
     */
    public Map<String, Object> getGivenList(String mobile_no) {
        String url = "/user/getGivenList";
        logger.info("##积分领取列表##请求地址是:[{}],手机号:[{}]", url, mobile_no);
        List list = new ArrayList();
        list.add(new BasicNameValuePair("mobile_no", mobile_no));
        return httpPost(url, list);
    }

    /**
     * 会员领取金豆
     *
     * @param mobileNo
     * @param orderNo
     * @return
     */
    @Transactional
    public Map<String, Object> receive(Integer siteId, String mobileNo, String orderNo) {

        String msg = "";

        String url = "/user/receive";
        logger.info("##会员领取积分##请求地址是:[{}],手机号:[{}],订单编号:[{}]", url, mobileNo, orderNo);
        List list = new ArrayList();
        list.add(new BasicNameValuePair("mobile_no", mobileNo));
        list.add(new BasicNameValuePair("order_no", orderNo));
        Map<String, Object> result = httpPost(url, list);
        //获取失败  直接返回结果
        if (Integer.parseInt(result.get("code").toString()) != 0) return result;

        //获取成功，同步先下积分到线上调用积分查询方法
        result = queryScore(siteId, mobileNo);
        if ((Integer) result.get("code") == 0) {
            msg = "领取成功";
            result.put("msg", msg);
        }

        return result;
    }

    /**
     * 获取消费查询金豆列表
     *
     * @param mobileNo
     * @return
     */
    public Map<String, Object> getScoreList(String mobileNo) {
        String url = "/user/getScoreList";
        logger.info("##消费积分列表##请求地址是:[{}],手机号:[{}]", url, mobileNo);
        List list = new ArrayList();
        list.add(new BasicNameValuePair("mobile_no", mobileNo));
        return httpPost(url, list);
    }

/*    *//**
     * 获取库存信息
     *
     * @param GOODSNO 商品编码 多个用","分隔
     * @param UID     门店编号 多个用","分隔
     * @return
     *//*
    @Transactional
    public Map<String, Object> storage(String GOODSNO, String UID) {
        String url = "/orders/storages";
        logger.info("##库存信息##请求地址:[{}],商品编码:[{}],门店编号:[{}]", url, GOODSNO, UID);
        List list = new ArrayList();
        list.add(new BasicNameValuePair("GOODSNO", GOODSNO));
        list.add(new BasicNameValuePair("UNIT_NO", UID));
        Map<String, Object> result = httpPost(url, list);
        return result;
    }*/

/*    @Transactional
    public void pushOrders(Map<String, Object> params) {
        Map<String, Object> erpMap = merchantERPMapper.selectMerchantERPInfo(100166);
        if (!erpMap.containsKey("erpUrl")) {
            return;
        }
        String baseUrl = erpMap.get("erpUrl").toString();//erp请求地址
        String url = baseUrl + "/orders/pushOrder";
        logger.info("推送订单信息给九州" + params);
        try {
            logger.info("九州推送订单返回值======" + OkHttpUtil.postJson(url, JacksonUtils.mapToJson(params)));
        } catch (Exception e) {
            logger.info("订单推送失败" + e);
        }
    }*/
}

