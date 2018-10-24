package com.jk51.modules.privatesend.core;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;
import com.alipay.api.request.AlipayOpenPublicMessageSingleSendRequest;
import com.alipay.api.response.AlipayOpenPublicMessageSingleSendResponse;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.modules.appInterface.util.ResultMap;
import com.jk51.modules.merchant.service.MerchantAliTemplateService;
import com.jk51.modules.offline.service.ErpToolsService;
import com.jk51.modules.privatesend.service.AliPayOpenPublicService;
import com.jk51.modules.privatesend.util.TemplateIdConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/6/19
 * 修改记录:
 */
@Service
public class AliPrivateSend {
    private static final Logger logger = LoggerFactory.getLogger(AliPrivateSend.class);
    private String COLOR_RED = "#FF0000";
    private String COLOR_BLUE = "#191970";
    private String COLOR_BLACK = "#000000";

    @Autowired
    private AliPayOpenPublicService aliPayOpenPublicService;
    @Autowired
    private MerchantAliTemplateService merchantAliTemplateService;
    @Autowired
    ErpToolsService erpToolsService;


//    /**
//     * 交易成功
//     */
//    public Map orderIsSuccesssss() {
//        String param = templateParam("2088702700652265", "48b342705b04419d8a7bf4998f87afd4", "http://m.baidu.com", "尊敬的陈先生", "您好您好", "小明", "123456");
//        return templateRequest(100190, param);
//    }
    /**
     * 创建拼单成功提醒 ok
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
        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.TOGETHER_ORDER_CREATE_SUCCESS), url, first, remark, true, keyword1, keyword2, keyword3, keyword4, keyword5);
        return templateRequest(siteId, param);
    }

    /**
     * 参加拼单成功提醒 ok
     */
    public Map togetherOrderJoinSuccess(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5) {
        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.TOGETHER_ORDER_JOIN_SUCCESS), url, first, remark,false, keyword1, keyword2, keyword3, keyword4, keyword5);
        return templateRequest(siteId, param);
    }

    /**
     * 拼单成功通知  ok
     */
    public Map togetherOrderCreateSuccessNotice(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3) {
        String param = templateParamRemarkBodyColor2(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.TOGETHER_ORDER_CREATE_SUCCESS_NOTICE), url, first, remark, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 拼单失败通知 ok
     *
     * {{first}}
     * 订单编号：{{keyword1}}
     * 商品信息：{{keyword2}}
     * 下单时间：{{keyword3}}
     * {{remark}}
     */
    public Map togetherOrderCreateFailNotice(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3) {
        String param = templateParamRemarkBodyColor2(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.TOGETHER_ORDER_CREATE_FAIL_NOTICE), url, first, remark, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 拼单失败通知(退款) ok
     * {{first}}
     * 拼单商品：{{keyword1}}
     * 商品金额：{{keyword2}}
     * 退款金额：{{keyword3}}
     * {{remark}}
     */
    public Map togetherOrderCreateFailNoticeRefund(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3) {
        String param = templateParamRemarkBodyColor2(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.TOGETHER_ORDER_CREATE_FAIL_NOTICE_REFUND), url, first, remark, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 拼单人数不足提醒 ok
     */
    public Map togetherOrderPeopleLack(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3) {
        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.TOGETHER_ORDER_PEOPLE_LACK), url, first, remark, false, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 订单取消通知 ok
     */
    public Map togetherOrderCancel(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3, String keyword4) {
        String param = templateParamRemarkBodyColor(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.TOGETHER_ORDER_CANCEL), url, first, remark, keyword1, keyword2, keyword3, keyword4);
        return templateRequest(siteId, param);
    }

    /**
     * 未支付超时通知 ok
     */
    public Map togetherOrderUnPay(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5) {
        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.TOGETHER_ORDER_UNPAY_OVERTIME), url, first, remark,false, keyword1, keyword2, keyword3, keyword4, keyword5);
        return templateRequest(siteId, param);
    }

    /**
     * 商品发货通知（拼团） ok
     */
    public Map togetherOrderSendNotice(Integer siteId, String userOpenid, String url, String first, String remark, String keyword1, String keyword2, String keyword3, String keyword4) {
        String param = templateParamRemarkBodyColor(userOpenid, getAliTemplateId(siteId, TemplateIdConstant.TOGETHER_ORDER_SEND_NOTICE), url, first, remark, keyword1, keyword2, keyword3, keyword4);
        return templateRequest(siteId, param);
    }

    /**
     * 订单待付款提醒 ok
     */
    public Map orderToPayNotice(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3) {
        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.ORDER_TO_PAY_NOTICE), url, first, remark, false, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 订单提货通知（门店自提通知）ok
     */
    public Map orderStoreToTake(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3, String keyword4) {
        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.ORDER_STORE_TO_TAKE), url, first, remark, false, keyword1, keyword2, keyword3, keyword4);
        return templateRequest(siteId, param);
    }

    /**
     * 订单发货通知  ok
     */
    public Map orderSendNotice(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3, String keyword4, String keyword5) {
        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.ORDER_SEND_NOTICE), url, first, remark, false, keyword1, keyword2, keyword3, keyword4, keyword5);
        return templateRequest(siteId, param);
    }

    /**
     * 订单签收  ok
     */
    public Map orderSign(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3) {
        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.ORDER_SIGN), url, first, remark, false, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 退款成功 ok
     */
    public Map orderRefundSuccess(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2) {
        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.ORDER_REFUND_SUCCESS), url, first, remark, false, keyword1, keyword2);
        return templateRequest(siteId, param);
    }

    /**
     * 退款失败  ok
     */
    public Map orderRefundFail(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3) {
        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.ORDER_REFUND_FAIL), url, first, remark, false, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 交易成功 ok
     */
    public Map orderIsSuccess(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3) {
//        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.ORDER_IS_SUCCESS), url, first, remark, false, keyword1, keyword2);
        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.ORDER_IS_SUCCESS), url, first, remark, false, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    /**
     * 抽中红包通知 ok
     */
    public Map redBacketSuccess(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2, String keyword3) {
        String param = templateParam(userOpenid, getAliTemplateId(siteId,TemplateIdConstant.RED_BACKET_SUCCESS), url, first, remark, false, keyword1, keyword2, keyword3);
        return templateRequest(siteId, param);
    }

    //first为红色，remark为黑色,body为黑色  userOpenid=2088702700652265
    public String templateParam(String userOpenid, String templateId, String url, String first, String remark, boolean lastTwoRedFlag, String... strings) {
        StringBuffer str = new StringBuffer();

        remark = getFormatString(remark);
        first = getFormatString(first);

        str.append("{\n" +
            "   \"template\":{\n" +
            "      \"context\":{\n");

            for (int i = 0; i < strings.length; i++) {
                int prefix = i + 1;
//                String value = getFormatString(strings[i]);
                String value = strings[i];
                str.append("                   \"keyword" + prefix + "\":{" + "\"value\":\"" + value + "\"");
                str.append(",\"color\":\"" + COLOR_BLACK + "\"");
                if (lastTwoRedFlag && (i == 4 || i == 3)) {
                    str.append(",\"color\":\"" + COLOR_RED + "\"");
                }
                str.append("},\n");
            }

            str.append("         \"remark\":{\n" +
                "            \"color\":\""+COLOR_BLACK+"\",\n" +
                "            \"value\":\" "+remark+" \"\n" +
                "         },\n" +
                "         \"head_color\":\""+COLOR_BLACK+"\",\n" +
                "         \"url\":\""+url+"\",\n" +
                "         \"action_name\":\"详情\",\n" +
                "         \"first\":{\n" +
                "            \"color\":\""+COLOR_RED+"\",\n" +
                "            \"value\":\""+first+"\"\n" +
                "         }\n" +
                "      },\n" +
                "      \"template_id\":\""+templateId+"\"\n" +
                "   },\n" +
                "   \"to_user_id\":\""+userOpenid+"\"\n" +
                "}");

        return str.toString();

    }

    //first为红色，remark为红色,body为蓝色
    public String templateParamRemarkBodyColor(String userOpenid, String templateId, String url, String first, String remark, String... strings) {
        StringBuffer str = new StringBuffer();

        remark = getFormatString(remark);
        first = getFormatString(first);

        str.append("{\n" +
            "   \"template\":{\n" +
            "      \"context\":{\n");

        for (int i = 0; i < strings.length; i++) {
            int prefix = i + 1;
            String value = getFormatString(strings[i]);
            str.append("                   \"keyword" + prefix + "\":{" + "\"value\":\"" + value + "\"");
            str.append(",\"color\":\"" + COLOR_BLUE + "\"");
            str.append("},\n");
        }

        str.append("         \"remark\":{\n" +
            "            \"color\":\""+COLOR_RED+"\",\n" +
            "            \"value\":\" "+remark+" \"\n" +
            "         },\n" +
            "         \"head_color\":\""+COLOR_BLACK+"\",\n" +
            "         \"url\":\""+url+"\",\n" +
            "         \"action_name\":\"详情\",\n" +
            "         \"first\":{\n" +
            "            \"color\":\""+COLOR_RED+"\",\n" +
            "            \"value\":\""+first+"\"\n" +
            "         }\n" +
            "      },\n" +
            "      \"template_id\":\""+templateId+"\"\n" +
            "   },\n" +
            "   \"to_user_id\":\""+userOpenid+"\"\n" +
            "}");
        return str.toString();

    }

    //first为黑色，remark为红色,body为蓝色
    public String templateParamRemarkBodyColor2(String userOpenid, String templateId, String url, String first, String remark, String... strings) {
        StringBuffer str = new StringBuffer();

        remark = getFormatString(remark);
        first = getFormatString(first);

        str.append("{\n" +
            "   \"template\":{\n" +
            "      \"context\":{\n");

        for (int i = 0; i < strings.length; i++) {
            int prefix = i + 1;
            String value = getFormatString(strings[i]);
            str.append("                   \"keyword" + prefix + "\":{" + "\"value\":\"" + value + "\"");
            str.append(",\"color\":\"" + COLOR_BLUE + "\"");
            str.append("},\n");
        }

        str.append("         \"remark\":{\n" +
            "            \"color\":\""+COLOR_RED+"\",\n" +
            "            \"value\":\" "+remark+" \"\n" +
            "         },\n" +
            "         \"head_color\":\""+COLOR_BLACK+"\",\n" +
            "         \"url\":\""+url+"\",\n" +
            "         \"action_name\":\"详情\",\n" +
            "         \"first\":{\n" +
            "            \"color\":\""+COLOR_BLACK+"\",\n" +
            "            \"value\":\""+first+"\"\n" +
            "         }\n" +
            "      },\n" +
            "      \"template_id\":\""+templateId+"\"\n" +
            "   },\n" +
            "   \"to_user_id\":\""+userOpenid+"\"\n" +
            "}");
        return str.toString();

    }

    public Map templateRequest(Integer siteId, String param){
        AlipayOpenPublicMessageSingleSendResponse response = null;
        try {
            if(param.indexOf("__getAliTemplateIdFromDb_fail__") != -1){
                logger.info("模板消息未启用");
                return ResultMap.errorResult("模板消息未启用");
            }

            AliPayMerchantConfig aliPayMerchantConfig = aliPayOpenPublicService.getAliPayMerchantConfig(siteId.toString());
            AlipayOpenPublicMessageSingleSendRequest aliPayRequest = new AlipayOpenPublicMessageSingleSendRequest();
            aliPayRequest.setBizContent(param);
            response = aliPayOpenPublicService.getAliPayClient(aliPayMerchantConfig).execute(aliPayRequest);
            if(response.isSuccess()){
                logger.error("模板消息发送成功"+response.getBody());
            }else {
                logger.error("模板消息发送失败"+response.getSubMsg());
                erpToolsService.sendMailTo51JK("模板消息发送失败","商户站点：" + siteId + ",详细报错信息：" + response.getSubMsg());
            }
        } catch (BusinessLogicException e) {
            logger.info("未开通支付宝生活号:{}"+e.getMessage());
            return ResultMap.errorResult(e.getMessage());
        } catch (AlipayApiException e) {
            logger.error("生活号发送消息模板失败,{}",e);
            return ResultMap.errorResult(e.getMessage());
        }
        return responseHandle(response);
    }

    public String getFormatString(String value){
        return StringUtil.isNotEmpty(value) ? value.replaceAll("\\s", "").replaceAll("NULL", "") : "";
    }

    public Map responseHandle(AlipayResponse response) {
        if (StringUtil.isEmpty(response)) {
            return ResultMap.errorResult(response + "");
        }
        if (response.isSuccess()) {
            Map result = new HashMap();
            result.put("result", response.getBody());
            return ResultMap.successResult(result);
        } else {
            return ResultMap.errorResult("调用失败:"+response.getSubMsg());
        }
    }

    public String getAliTemplateId(Integer siteId,String parm){
        String templateId = merchantAliTemplateService.getAliTemplateIdBySiteIdAndName(siteId,parm);
        logger.info("当前发送的ali模板Id:{}",templateId);
        if(StringUtil.isEmpty(templateId)){
            return "__getAliTemplateIdFromDb_fail__";
        }
        return templateId;
    }

}
