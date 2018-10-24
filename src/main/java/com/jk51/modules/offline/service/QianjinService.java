package com.jk51.modules.offline.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.configuration.PathUrlConfig;
import com.jk51.modules.appInterface.mapper.BMemberInfoMapper;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 千金erp对接，要继承ERPOfflineAbstractService，实现service调度
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-07 9:37
 * 修改记录:
 */
@Service
public class QianjinService {

    private final static Logger LOGGER = LoggerFactory.getLogger(QianjinService.class);


    @Autowired
    private PathUrlConfig pathUrlConfig;

    @Autowired
    private BMemberMapper memberMapper;

    @Autowired
    private BMemberInfoMapper memberInfoMapper;

    @Autowired
    private MerchantERPMapper merchantERPMapper;


/*    *//**
     * 获取库存信息
     * {请求千金示例
     * url:http://218.75.221.60:8003/servlet/wscitystockservlet?GOODSNO=90301104,90301105&UID=2,4}
     *
     * @param GOODSNO 商品编码 多个用","分隔
     * @param UID     门店编号 多个用","分隔
     * @return
     *//*
    public Map<String, Object> getStock(String GOODSNO, String UID) {

        String url = "/wscitystockservlet?GOODSNO=" + GOODSNO + "&UID=" + UID;

        LOGGER.info("##库存信息##请求地址:[{}],商品编码:[{}],门店编号:[{}]", GOODSNO, UID);

        return httpGet(url);
    }*/

    /**
     * 获取会员信息
     * {请求千金示例
     * url:http://218.75.221.60:8003/servlet/wscitymemberservlet?mobile=13588302754}
     *
     * @param siteId
     * @param mobile
     * @return
     *//*
    @Transactional
    public Map<String, Object> getUser(Integer siteId, String mobile) {

        Integer code = 1;
        String msg = "";

        String url = "/wscitymemberservlet?mobile=" + mobile;
//        Member member = memberMapper.selectMember(siteId,mobile);

        Map member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(siteId), mobile);

        LOGGER.info("##获取会员信息##请求地址是:[{}],手机号:[{}]", url, mobile);

        Map<String, Object> result = httpGet(url);

        //判断是否正常获取数据
        //如果该用户信息不存在 返回数据
        if (!StringUtil.isEmpty(result.get("code")) && !"-1".equals(result.get("code").toString())) {
            if ("0".equals(result.get("code").toString())) {
                return result;
            }
        } else {
            return result;
        }

        Map map = (Map) ((List) result.get("info")).get(0);

        //更新b_member 表信息
        Map memberMap = new HashMap();

        memberMap.put("name", map.get("name"));

        if (StringUtil.isNotBlank((String) map.get("sex"))) {
            Integer sex = map.get("sex").equals("男") ? 1 : 0;
            memberMap.put("sex", sex);
        } else {
            memberMap.put("sex", 3);
        }

        memberMap.put("idcard_number", map.get("certif_no"));
        memberMap.put("email", map.get("email"));
        memberMap.put("site_id", siteId);
        memberMap.put("mobile", mobile);

        int i = memberMapper.updateMember(memberMap);

        if (i != 0) {//更新b_member_info表信息
            Map memberInfoMap = new HashMap();
            memberInfoMap.put("address", map.get("address"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = dateFormat.parse(map.get("birthday").toString());
                memberInfoMap.put("birthday", date);
            } catch (Exception e) {
                memberInfoMap.put("birthday", "1970-01-01");
            }
            memberInfoMap.put("card_no", map.get("card_no"));
            memberInfoMap.put("site_id", siteId);
            memberInfoMap.put("member_id", member.get("buyer_id"));
            int j = memberInfoMapper.updateMemberInfo(memberInfoMap);
            if (j != 0) {
                msg = "success";
            } else {
                code = -1;
                msg = "同步失败";
            }
        } else {
            code = -1;
            msg = "同步失败";
        }
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }*/

    /**
     * 添加会员
     * {请求千金示例
     * url : http://218.75.221.60:
     * 8003/servlet/wscitymemberinsertservlet?mobile=13588302754&busno=101031}
     *
     * @param param
     * @return
     *//*
    public Map<String, Object> addUser(Map<String, Object> param) {
        Map<String, Object> memberInfo = memberInfoMapper.erpGetMemberInfo(100180, param.get("mobile_no").toString(), null);
        String busno = "101218";
        if (!StringUtil.isEmpty(memberInfo.get("uid"))) {
            busno = memberInfo.get("uid").toString();
        }
        String url = "/wscitymemberinsertservlet?mobile=" + param.get("mobile_no") + "&busno=" + busno;
        LOGGER.info("添加会员--mobile:[{}],busno:[{}]", param.get("mobile_no"), busno);
        Map<String, Object> result = httpGet(url);
        if (!StringUtil.isEmpty(result.get("code")) && !"-1".equals(result.get("code").toString())) {
            if ("1".equals(result.get("code").toString())) {
                //插入会员信息
                return updateUser(param);
            }
        } else {
            return result;
        }

        return result;
    }*/

   /* *//**
     * 会员信息更新
     * <p>
     * {请求千金示例
     * url : http://218.75.221.60:8003/servlet/wscitymemberupdateservlet?mobile=13588302754&cardholder=张三&sex=男&birthday=1990-04-01}
     * <p>
     * map.put("mobile","18812345678");
     * map.put("name","ccc");
     * map.put("sex","男");
     * Dataformat df = new DateFormat("yyyy-MM-dd");
     * map.put("birthday","1987-10-13");
     * map.put("address","浙江省杭州市西湖区南山路");
     *
     * @param param
     * @return
     *//*
    public Map<String, Object> updateUser(Map<String, Object> param) {
        LOGGER.info(param.toString());

        String url = "/wscitymemberupdateservlet?mobile=" + param.get("mobile_no") + "&cardholder=" + param.get("name") + "&sex=" + param.get("sex") + "&birthday=" + param.get("birthday");
        Map<String, Object> result = httpGet(url);
        //修改先下信息操作成功，将信息保存到线上
        if (result.get("code") != null && "1".equals(result.get("code").toString())) {
            return getUser((Integer) param.get("siteId"), param.get("mobile_no").toString());//调用获取会员方法
        }
//        Map<String,Object> result =  httpGet(url);
        return result;
    }*/


   /* *//**
     * 千金数据请求
     *
     * @return
     *//*
    private Map<String, Object> httpGet(String url) {
        //http://218.75.221.60:8003/servlet
        String msg = "";
        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(100180)));
        if (StringUtil.isEmpty(merchantErpInfo)) {
            msg = "erp请求异常";
        }
        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
        try {
            String result = httpGetRequesetResult(baseUrl + url);
            LOGGER.info("#####" + result + "#####");
            return JacksonUtils.json2map(result);
        } catch (IOException e) {
            LOGGER.error("erp请求异常" + e);
            e.printStackTrace();
            msg = "erp请求异常" + e;
        } catch (Exception e) {
            LOGGER.error("数据解析异常" + e);
            e.printStackTrace();
            msg = "数据解析异常" + e;
        }
        Map<String, Object> res = new HashMap<>();
        res.put("code", -1);
        res.put("status", msg);
        return res;
    }*/

    /**
     * 返回结果处理
     *
     * @param url
     * @return
     */
    private String httpGetRequesetResult(String url) throws IOException {

       /* CloseableHttpResponse response = HttpClientManager.httpGetRequest(url);

        String result = null;
        try {
            // 得到返回对象
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 获取返回结果
                result = EntityUtils.toString(entity,"UTF-8");
            }
        } catch (IOException e) {
            LOGGER.error("IOException->URL:" + url, e);
        } finally {
            // 关闭到客户端的连接
            try {
                if(response != null){
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error("IOException关闭异常->URL:" + url, e);
            }
        }*/
        return OkHttpUtil.get(url);
    }

}
