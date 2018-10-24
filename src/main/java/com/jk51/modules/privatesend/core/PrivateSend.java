package com.jk51.modules.privatesend.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.esn.mapper.GoodsEsMapper;
import com.jk51.modules.im.service.wechatUtil.WechatUtil;
import com.jk51.modules.offline.service.ErpToolsService;
import com.jk51.modules.privatesend.util.TemplateIdConstant;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class PrivateSend {

    private static final Logger logger = LoggerFactory.getLogger(PrivateSend.class);

    @Autowired
    private WechatUtil wechatUtil;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MerchantExtTreatMapper merchantExtTreatMapper;

    @Autowired
    GoodsEsMapper goodsEsMapper;
    @Autowired
    ErpToolsService erpToolsService;

    private static final String WX_TEMPLATE_ALL="wx_template_all_";
    private String COLOR_RED = "#FF0000";
    private String COLOR_BLUE = "#191970";

    /**
     * 创建拼单成功提醒
     * <p>
     * 参数说明：
     * {{first}}
     * 商品：{{keyword1}}
     * 拼单成员：{{keyword2}}
     * 发货时间：{{keyword3}}
     * {{remark}}
     * <p>
     * 内容示例：
     * 恭喜您与xx拼单成功，点击去看看他还买了什么~
     * 商品：桂圆红枣枸杞茶
     * 拼单成员：胖大星、海绵宝宝、章鱼哥
     * 发货时间：2017.8.15
     * 赶快点击进入拼单页面
     *
     * @param siteId
     * @param userOpenid b_member.open_id
     * @param url        模板跳转链接
     * @param first
     * @param remark
     * @param keyword1
     * @param keyword2
     * @param keyword3
     * @return
     */
    public Map togetherOrderCreateSuccess(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5) {
        String param = templateParam(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.TOGETHER_ORDER_CREATE_SUCCESS), url, first, remark, true, keyword1, keyword2, keyword3, keyword4, keyword5);
        return templateRequest(siteId, param);
    }

    /**
     * 参加拼单成功提醒
     */
    public Map togetherOrderJoinSuccess(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5) {
        String param = " {\n" +
            "           \"touser\":\""+userOpenid+"\",\n" +
            "           \"template_id\":\""+getWxTemplateIdFromAPI(siteId,TemplateIdConstant.TOGETHER_ORDER_JOIN_SUCCESS)+"\",\n" +
            "           \"url\":\""+url+"\",  \n" +

            "\"data\":{\n" +
            "                   \"first\": {\n" +
            "                       \"value\":\""+first+"\"" +
            "                   },\n" +
            "                   \"keyword1\":{\n" +
            "                       \"value\":\""+keyword1+"\"" +
            "                   },\n" +
            "                   \"keyword2\": {\n" +
            "                       \"value\":\""+keyword2+"\"" +
            "                   },\n" +
            "                   \"keyword3\": {\n" +
            "                       \"value\":\""+keyword3+"\"" +
            "                   },\n" +
            "                   \"keyword4\": {\n" +
            "                       \"value\":\""+keyword4+"\",\n" +
            "                       \"color\":\"" + COLOR_RED + "\"\n" +
            "                   },\n" +
            "                   \"keyword5\": {\n" +
            "                       \"value\":\""+keyword5+"\",\n" +
            "                       \"color\":\"" + COLOR_RED + "\"\n" +
            "                   },\n" +
            "                   \"remark\":{\n" +
            "                       \"value\":\""+remark+"!\" " +
            "                   }\n" +
            "           }" +
            "       }";
        return templateRequest(siteId, param);
    }

    /**
     * 拼单成功通知
     */
    public Map togetherOrderCreateSuccessNotice(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3) {
        String param = templateParamRemarkBodyColor2(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.TOGETHER_ORDER_CREATE_SUCCESS_NOTICE), url, first, remark, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 拼单失败通知
     *
     * {{first}}
     * 订单编号：{{keyword1}}
     * 商品信息：{{keyword2}}
     * {{remark}}
     */
    public Map togetherOrderCreateFailNotice(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2) {
        String param = templateParamRemarkBodyColor2(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.TOGETHER_ORDER_CREATE_FAIL_NOTICE), url, first, remark, keyword1, keyword2);
        return templateRequest(siteId, param);
    }

    /**
     * 拼单失败通知(退款)
     * {{first}}
     * 拼单商品：{{keyword1}}
     * 商品金额：{{keyword2}}
     * 退款金额：{{keyword3}}
     * {{remark}}
     */
    public Map togetherOrderCreateFailNoticeRefund(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3) {
        String param = templateParamRemarkBodyColor2(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.TOGETHER_ORDER_CREATE_FAIL_NOTICE_REFUND), url, first, remark, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 拼单人数不足提醒
     */
    public Map togetherOrderPeopleLack(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3) {
        String param = templateParam(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.TOGETHER_ORDER_PEOPLE_LACK), url, first, remark, false, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 订单取消通知
     */
    public Map togetherOrderCancel(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3, String keyword4) {
        String param = templateParamRemarkBodyColor(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.TOGETHER_ORDER_CANCEL), url, first, remark, keyword1, keyword2, keyword3, keyword4);
        return templateRequest(siteId, param);
    }

    /**
     * 未支付超时通知
     */
    public Map togetherOrderUnPay(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5) {
        String param = " {\n" +
            "           \"touser\":\""+userOpenid+"\",\n" +
            "           \"template_id\":\""+getWxTemplateIdFromAPI(siteId,TemplateIdConstant.TOGETHER_ORDER_UNPAY_OVERTIME)+"\",\n" +
            "           \"url\":\""+url+"\",  \n" +

            "\"data\":{\n" +
            "                   \"first\": {\n" +
            "                       \"value\":\""+first+"\",\n" +
            "                       \"color\":\"#e02e05\"\n" +
            "                   },\n" +
            "                   \"orderID\":{\n" +
            "                       \"value\":\""+keyword1+"\"" +
            "                   },\n" +
            "                   \"commodityTitle\": {\n" +
            "                       \"value\":\""+keyword2+"\"" +
            "                   },\n" +
            "                   \"orderTime\": {\n" +
            "                       \"value\":\""+keyword3+"\"" +
            "                   },\n" +
            "                   \"orderPrice\": {\n" +
            "                       \"value\":\""+keyword4+"\"" +
            "                   },\n" +
            "                   \"orderStatus\": {\n" +
            "                       \"value\":\""+keyword5+"\"" +
            "                   },\n" +
            "                   \"remark\":{\n" +
            "                       \"value\":\""+remark+"!\" " +
            "                   }\n" +
            "           }" +
            "       }";
        return templateRequest(siteId, param);
    }

    /**
     * 商品发货通知（拼团）
     */
    public Map togetherOrderSendNotice(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3, String keyword4) {
        String param = templateParamRemarkBodyColor(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.TOGETHER_ORDER_SEND_NOTICE), url, first, remark, keyword1, keyword2, keyword3, keyword4);
        return templateRequest(siteId, param);
    }

    /**
     * 订单待付款提醒
     */
    public Map orderToPayNotice(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2) {
        String param = templateParam(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.ORDER_TO_PAY_NOTICE), url, first, remark, false, keyword1, keyword2);
        return templateRequest(siteId, param);
    }

    /**
     * 订单提货通知（门店自提通知）
     */
    public Map orderStoreToTake(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3, String keyword4) {
        String param = templateParam(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.ORDER_STORE_TO_TAKE), url, first, remark, false, keyword1, keyword2, keyword3, keyword4);
        return templateRequest(siteId, param);
    }

    /**
     * 订单发货通知
     */
    public Map orderSendNotice(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3, String keyword4, String keyword5) {
        String param = templateParam(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.ORDER_SEND_NOTICE), url, first, remark, false, keyword1, keyword2, keyword3, keyword4, keyword5);
        return templateRequest(siteId, param);
    }

    /**
     * 订单签收
     */
    public Map orderSign(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3) {
        String param = templateParam(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.ORDER_SIGN), url, first, remark, false, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 退款成功
     */
    public Map orderRefundSuccess(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2) {
        String param = templateParam(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.ORDER_REFUND_SUCCESS), url, first, remark, false, keyword1, keyword2);
        return templateRequest(siteId, param);
    }

    /**
     * 退款失败
     */
    public Map orderRefundFail(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3) {
        String param = templateParam(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.ORDER_REFUND_FAIL), url, first, remark, false, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 交易成功
     */
    public Map orderIsSuccess(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2) {
        String param = templateParam(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.ORDER_IS_SUCCESS), url, first, remark, false, keyword1, keyword2);
        return templateRequest(siteId, param);
    }

    /**
     * 心电检测结果通知
     */
    public Map ecgResultMessage(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3, String keyword4) {
        String param = templateParamRemarkBodyColor3(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.ECG_RESULT_MESSAGE), url, first, remark, keyword1, keyword2,keyword3,keyword4);
        return templateRequest(siteId, param);
    }

    /**
     * 设备连接成功提醒
     */
    public Map ecgSuccessMessage(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2) {
        String param = templateParamRemarkBodyColor3(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.ECG_SUCCESS_MESSAGE), url, first, remark, keyword1, keyword2);
        return templateRequest(siteId, param);
    }

    /**
     * 开通成功通知
     */
    public Map couponIsSuccess(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3) {
        String param = templateParam(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.COUPON_IS_SUCCESS), url, first, remark, false, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 服务到期提醒
     */
    public Map couponIsExpire(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3) {
        String param = templateParam(userOpenid, getWxTemplateIdFromAPI(siteId,TemplateIdConstant.COUPON_IS_EXPIRE), url, first, remark, false, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    public Map templateRequest(Integer siteId, String param) {
        try {

            if(param.indexOf("__getWxTemplateIdFromAPI_templateId_fail__") != -1){
                logger.info("模板消息未启用");
                return getFailedMap(new RuntimeException(siteId + "模板消息未启用"));
            }

            logger.info(param);
            String jsonResult = OkHttpUtil.postJson(getTencentTokenUrl(TemplateIdConstant.URL, siteId), param);
            logger.info(jsonResult+"-------------------------------------------");
            Map result = JacksonUtils.json2map(jsonResult);
            String prefix;
            if("ok".equals(result.get("errmsg"))){
                prefix= "success";
                logger.info(jsonResult);
            }else {
                prefix="error";
                logger.error("模板消息发送失败"+jsonResult);
                erpToolsService.sendMailTo51JK("模板消息发送失败","商户站点：" + siteId +",消息内容："+param+ ",详细报错信息：" + jsonResult);
            }
            goodsEsMapper.insertLog(siteId + "", "-------template------" + prefix + "------" + param, jsonResult);
            return JacksonUtils.json2map(jsonResult);
        } catch (Exception e) {
            logger.error("微信公众号发送消息失败, {}", e);
            e.printStackTrace();
            return getFailedMap(e);
        }
    }

    public Map getFailedMap(Exception e) {
        Map result = new HashMap();
        result.put("errmsg", "failed");
        result.put("description", e);
        return result;
    }

    //first为红色，remark为黑色,body为黑色,第四行第五行可选择是否为红色
    public String templateParam(String userOpenid, String templateId, String url, String first, String remark, boolean lastTwoRedFlag, String... strings) {
        StringBuffer str = new StringBuffer();

        remark = getFormatString(remark);
        first = getFormatString(first);

        str.append(" {\n" +
            "           \"touser\":\"" + userOpenid + "\",\n" +
            "           \"template_id\":\"" + templateId + "\",\n" +
            "           \"url\":\"" + url + "\",  \n" +

            "\"data\":{\n" +
            "                   \"first\": {\n" +
            "                       \"value\":\"" + first + "\",\n" +
            "                       \"color\":\"" + COLOR_RED + "\"\n" +
            "                   },\n");

        for (int i = 0; i < strings.length; i++) {
            int prefix = i + 1;
            String value = getFormatString(strings[i]);
            str.append("                   \"keyword" + prefix + "\":{" + "\"value\":\"" + value + "\"");
            if (lastTwoRedFlag && (i == 4 || i == 3)) {
                str.append(",\"color\":\"" + COLOR_RED + "\"");
            }
            str.append("},\n");
        }
        str.append("                   \"remark\":{\n" +
            "                       \"value\":\"" + remark + "\" " +
            "                   }\n" +
            "           }" +
            "       }");
        return str.toString();

    }

    //first为红色，remark为红色,body为蓝色
    public String templateParamRemarkBodyColor(String userOpenid, String templateId, String url, String first, String remark, String... strings) {
        StringBuffer str = new StringBuffer();

        remark = getFormatString(remark);
        first = getFormatString(first);

        str.append(" {\n" +
            "           \"touser\":\"" + userOpenid + "\",\n" +
            "           \"template_id\":\"" + templateId + "\",\n" +
            "           \"url\":\"" + url + "\",  \n" +

            "\"data\":{\n" +
            "                   \"first\": {\n" +
            "                       \"value\":\"" + first + "\",\n" +
            "                       \"color\":\"" + COLOR_RED + "\"\n" +
            "                   },\n");

        for (int i = 0; i < strings.length; i++) {
            int prefix = i + 1;
            String value = getFormatString(strings[i]);
            str.append("                   \"keyword" + prefix + "\":{" + "\"value\":\"" + value + "\"");
            str.append(",\"color\":\"" + COLOR_BLUE + "\"");
            str.append("},\n");
        }
        str.append("                   \"remark\":{\n" +
            "                       \"value\":\"" + remark + "\" ,\n" +
            "                       \"color\":\"" + COLOR_RED + "\"\n" +
            "                   }\n" +
            "           }" +
            "       }");
        return str.toString();

    }

    //first为黑色，remark为红色,body为蓝色
    public String templateParamRemarkBodyColor2(String userOpenid, String templateId, String url, String first, String remark, String... strings) {
        StringBuffer str = new StringBuffer();

        remark = getFormatString(remark);
        first = getFormatString(first);

        str.append(" {\n" +
            "           \"touser\":\"" + userOpenid + "\",\n" +
            "           \"template_id\":\"" + templateId + "\",\n" +
            "           \"url\":\"" + url + "\",  \n" +

            "\"data\":{\n" +
            "                   \"first\": {\n" +
            "                       \"value\":\"" + first + "\"" +
            "                   },\n");

        for (int i = 0; i < strings.length; i++) {
            int prefix = i + 1;
            String value = getFormatString(strings[i]);
            str.append("                   \"keyword" + prefix + "\":{" + "\"value\":\"" + value + "\"");
            str.append(",\"color\":\"" + COLOR_BLUE + "\"");
            str.append("},\n");
        }
        str.append("                   \"remark\":{\n" +
            "                       \"value\":\"" + remark + "\" ,\n" +
            "                       \"color\":\"" + COLOR_RED + "\"\n" +
            "                   }\n" +
            "           }" +
            "       }");
        return str.toString();

    }

    //first为红色，remark为蓝色,body为黑色
    public String templateParamRemarkBodyColor3(String userOpenid, String templateId, String url, String first, String remark, String... strings) {
        StringBuffer str = new StringBuffer();

        remark = getFormatString(remark);
        first = getFormatString(first);

        str.append(" {\n" +
            "           \"touser\":\"" + userOpenid + "\",\n" +
            "           \"template_id\":\"" + templateId + "\",\n" +
            "           \"url\":\"" + url + "\",  \n" +

            "\"data\":{\n" +
            "                   \"first\": {\n" +
            "                       \"value\":\"" + first + "\",\n" +
            "                       \"color\":\"" + COLOR_RED + "\"\n" +
            "                   },\n");

        for (int i = 0; i < strings.length; i++) {
            int prefix = i + 1;
//            String value = getFormatString(strings[i]); 去除空格验证
            str.append("                   \"keyword" + prefix + "\":{" + "\"value\":\"" + strings[i] + "\"");
            str.append("},\n");
        }
        str.append("                   \"remark\":{\n" +
            "                       \"value\":\"" + remark + "\" ,\n" +
            "                       \"color\":\"" + COLOR_BLUE + "\"\n" +
            "                   }\n" +
            "           }" +
            "       }");
        return str.toString();

    }

    public String getFormatString(String value){
        return StringUtil.isNotEmpty(value) ? value.replaceAll("\\s", "").replaceAll("NULL", "") : "";
    }

    private String getTencentTokenUrl(String url, Integer siteId) {
        String accessToken= wechatUtil.getAccessToken(siteId);
        url += accessToken;
       /* String fromRedisAccessToken = getFromRedisAccessToken(siteId);

        if (StringUtil.isEmpty(fromRedisAccessToken)) {
            String accessToken= wechatUtil.getAccessToken(siteId);
            url += accessToken;
            setToRedisAccessToken(siteId,accessToken);
        } else {
            url += fromRedisAccessToken;
        }*/
        logger.info("url-------------------------"+url);
        return url;
    }

    public String getWxTemplateIdFromAPI(Integer siteId, String name) {
        try {

            if(merchantExtTreatMapper.selectByMerchantId(siteId).getWx_template_flag() != 1){
                return "__getWxTemplateIdFromAPI_templateId_fail__";
            }

            JSONArray jsonArray;
            String templateIdFromRedis = stringRedisTemplate.opsForValue().get(WX_TEMPLATE_ALL + siteId);
            if (StringUtil.isNotEmpty(templateIdFromRedis)) {
                jsonArray = JSON.parseArray(templateIdFromRedis);
            } else {
                String jsonResult = OkHttpUtil.get(getTencentTokenUrl(TemplateIdConstant.URL_TEMPLATE_ALL, siteId));
                logger.info(jsonResult+"获取模板返回结果。。--------------------");
                JSONObject jsonObject = (JSONObject) JSON.parse(jsonResult);
                jsonArray = jsonObject.getJSONArray("template_list");
                stringRedisTemplate.opsForValue().set(WX_TEMPLATE_ALL + siteId, jsonArray.toJSONString(), 3, TimeUnit.MINUTES);//缓存template_list
            }

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject template = jsonArray.getJSONObject(i);
                String templateId = template.getString("template_id");
                String title = template.getString("title");
                String content = template.getString("content");
                if (name.equals(TemplateIdConstant.TOGETHER_ORDER_CREATE_FAIL_NOTICE)) {//拼单失败通知由于名称一样，特殊处理
                    if (name.equals(title) && content.split("：").length == 3) {
                        logger.info("拼单失败通知获取template_id:" + templateId);
                        return templateId;
                    }
                } else if (name.equals(TemplateIdConstant.TOGETHER_ORDER_CREATE_FAIL_NOTICE_REFUND)) {//拼单失败通知由于名称一样，特殊处理
                    if (TemplateIdConstant.TOGETHER_ORDER_CREATE_FAIL_NOTICE.equals(title) && content.split("：").length == 4) {//
                        logger.info("拼单失败通知（退款）获取template_id:" + templateId);
                        return templateId;
                    }
                } else if (name.equals(title)) {
                    logger.info("获取template_id:" + templateId);
                    return templateId;
                }
            }
            return name;
        } catch (Exception e) {
            logger.error("获得模板ID失败, {}", e);
            e.printStackTrace();
            goodsEsMapper.insertLog(siteId + "", "-------template------获得模板ID失败------", e.toString());
            return name;
        }
    }
}
