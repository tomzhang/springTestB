package com.jk51.modules.sms.service;

import com.jk51.commons.sms.SmsTemplate;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.merchant.service.MerchantService;
import com.jk51.modules.offline.mapper.SMSTplMapper;
import com.jk51.modules.sms.exception.IllegalParameterException;
import com.jk51.modules.sms.smsConfig.SmsEnum;
import com.jk51.modules.sms.smsConfig.SmsParams;
import com.jk51.modules.sms.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-04-18
 * 修改记录:
 * 100登录验证码  110找回密码验证码 120店员app登录验证码 130店员app找回密码验证码 140语音验证码
 * 200商家发货后提醒顾客按照提货码去提货
 * 300商家发货后提醒顾客提货地址，门店等信息（新版提货码）
 * 400三级分销业务中，邀请分销商加盟时的邀请码
 * 500店员在app端下单成功后，短信提醒顾客有新订单
 * 600店员在app端下单成功后，短信通知顾客付款，短链接跳转网页支付
 * 700店员回访顾客时，发送给顾客的营销短信
 * 800有新订单时提醒【商家总部】，包括自提，送货上门，退款
 * 810有新订单时提醒【门店店员】，包括自提，送货上门，退款
 * 820电话提醒，有新订单时提醒【门店店员】，包括自提，送货上门，退款
 * 900当账户余额小于预警值时，提醒商家充值
 */
@Service
public class CommonService {
    @Autowired
    YpSmsService ypSmsService;
    @Autowired
    ZtSmsService ztSmsService;
    @Autowired
    MerchantService merchantService;
    @Autowired
    private DjSmsService djSmsService;
    @Autowired
    private SMSTplMapper smsTplMapper;

//    /**
//     * 发送验证码短信
//     *
//     * @param siteId
//     * @param phone
//     * @param code
//     * @return
//     */
//    public Integer sendValiCode(Integer siteId, String phone, String code, Integer type, Integer smsType) {
//       /* String merchantName = merchantService.queryMerchantName(siteId);
//        String word = "";
//        if (null != merchantName) {
//            word = "验证码" + code + "，" + merchantName + "提示：如非本人操作，请忽略本短信。";
//        } else {
//            word = "验证码" + code + "，" + "提示：如非本人操作，请忽略本短信。";
//        }
//        String status = djSmsService.SendMessage(siteId, word, phone, type, smsType);
//        if (!"0".equals(status)) {
//            Integer result = ypSmsService.SendMessage(siteId, 1402527, phone, code, smsType);
//            if (result != 0) {
//                return -1;
//            }
//        }
//        return Integer.parseInt(status);*/
//
//        String merchantName = merchantService.queryMerchantName(siteId);
//        SmsParams smsParams = transformParam(siteId, phone, type, smsType, code, merchantName);
//        return commonSMS(SmsEnum.SENDVALICODE,smsParams);
//    }
        /**
         * 发送手机订单短信
         * 空字段传"",不能传null
         * type 商家总部800  门店店员810
         */
//    public String sendOrderSMS(Integer siteId, String mobile, String ordertype, String order, String storename, Integer type) {
//        /*String word = "您有新的" + ordertype + "订单" + order + "，请" + storename + "尽快处理。";
//        String result = "-1";
//        if (!StringUtil.isEmpty(ordertype)) {
//            result = djSmsService.SendMessage(siteId, word, mobile, type, type);
//            if (!result.equals("0")) {
//                result = ypSmsService.SendOrderTemplate2(siteId, 1954942, mobile, ordertype, order, storename, type).toString();
//            }
//        }
//        return result;*/
//        SmsParams smsParams = transformParam(siteId, mobile, type, type, ordertype, order,storename);
//        return commonSMS(SmsEnum.SENDORDERSMS,smsParams).toString();
//    }

//    public int sendDoctor(String phone, String doctor, Integer siteId, Integer type, Integer smsType) {
//        /*String word = "恭喜您" + phone + "已成功预约" + doctor + " ，到店后请出示本通知短信进行签到，请注意医生的排班信息以免延误。";
//        String status = djSmsService.SendMessage(siteId, word, phone, type, smsType);
//        if ("0".equals(status)) {
//            return 0;
//        } else {
//            return ypSmsService.sendDoctor(siteId, 1766850, phone, doctor);
//        }*/
//        SmsParams smsParams = transformParam(siteId, phone, type, smsType, phone, doctor);
//        return commonSMS(SmsEnum.SENDDOCTOR,smsParams);
//    }

/*    *//**
     * 发送手机提货码短信(toMember)
     * type 200
     *//*
    public String getGoodSMS(Integer siteId, String mobile, String SelfTakenCode, String time, String sellphone, String shortMessageSign, String storeAddress, Integer type) {
        if (StringUtil.isEmpty(mobile) || StringUtil.isEmpty(SelfTakenCode) || StringUtil.isEmpty(sellphone) || StringUtil.isEmpty(shortMessageSign) || StringUtil.isEmpty(storeAddress)) {
            return "-1";
        }

        String result = "-1";
//        String message = "51健康提示，提货码：" + SelfTakenCode + "，请于" + time + "前提货，门店联系电话" + sellphone + "。";
        String message = shortMessageSign + "提醒您到" + storeAddress + "提货，电话：" + sellphone + "，提货码：" + SelfTakenCode + "。";
        result = ztSmsService.SendMessage(siteId, message, mobile, type, type);
        if (!result.equals("0")) {
            result = ypSmsService.SendGetGoods2(siteId, 1916440, mobile, shortMessageSign, storeAddress, sellphone, SelfTakenCode, type).toString();
        }
        return result;
    }*/

//    /**
//     * 三级分销短信
//     * type 400
//     */
//    public String sendValiSMS(Integer siteId, String mobile, String code, String name, String url, Integer type) {
//        /*String result = "-1";
//        String word = "【51健康】我悄悄告诉你一个验证码" + code + "，你就可以加入" + name + "了，戳下载" + url + "";
//        result = ypSmsService.sendValiSMS(siteId, 1597204, mobile, code, name, url, type).toString();
//        return result;*/
//
//        SmsParams smsParams = transformParam(siteId, mobile, type, type, code, name,url);
//        return commonSMS(SmsEnum.SENDVALISMS,smsParams).toString();
//    }

//    /**
//     * 发送手机订单短信(App)
//     * 空字段传"",不能传null
//     * type 500
//     */
//    public String sendOrderSMSNew(Integer siteId, String mobile, String order, String storemobile, Integer type) {
////        String result = "-1";
////        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
////        String word = "您已成功下单。订单号：" + order + "；下单时间：" + format.format(new Date()) + "；详情请咨询门店：" + storemobile;
////        result = djSmsService.SendMessage(siteId, word, mobile, type, type);
////      /*  if (!result.equals("0")) {
////            result = ypSmsService.SendOrderTemplate2(1954942, mobile, ordertype, order, storename).toString();
////        }*/
////        return result;
//        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
//        SmsParams smsParams = transformParam(siteId, mobile, type, type, order, format.format(new Date()),storemobile);
//        return commonSMS(SmsEnum.SENDORDERSMSNEW,smsParams).toString();
//    }

//    /**
//     * App短信发送收款页面地址给顾客
//     *
//     * @param mobile         顾客手机号码
//     * @param merchantName   商户名称
//     * @param storeName      门店名称
//     * @param storeAdminName 店员姓名
//     * @param money          需付金额
//     * @param url            连接地址
//     * @param type           600
//     * @return
//     */
//    public String sendOrderAddress(Integer siteId, String mobile, String merchantName, String storeName, String storeAdminName, String money, String url, Integer type) {
//        /*String word = "【51健康】" + merchantName + "" + storeName + "" + storeAdminName + "店员发来了一个待付款订单，需付金额：" + money + "元。点击链接完成付款:" + url + "";
//        String result = "-1";
//        if (!StringUtil.isEmpty(mobile) && !StringUtil.isEmpty(merchantName) && !StringUtil.isEmpty(storeName) && !StringUtil.isEmpty(storeAdminName)
//            && !StringUtil.isEmpty(money) && !StringUtil.isEmpty(url)) {
//            result = ypSmsService.sendAppAddress(siteId, 2132278, mobile, merchantName, storeName, storeAdminName, money, url, type);
//        }
//        return result;*/
//        SmsParams smsParams = transformParam(siteId, mobile, type, type, merchantName, storeName,storeAdminName,money,url);
//        return commonSMS(SmsEnum.SENDORDERADDRESS,smsParams).toString();
//    }

//    /**
//     * 发送优惠活动短信短信
//     * 空字段传"",不能传null
//     * type 700
//     */
//    public String activitySMS(Integer siteId, String mobile, String merchantName, String time, String title, String texturl, Integer type) {
////        result = ztSmsService.SendMessage(word, mobile);
//        /*String result = "-1";
//        String word = "尊敬的用户" + merchantName + "微商城" + time + "" + title + "会员福利，点击查看详情:" + texturl + "。回T退订";
//        result = ypSmsService.activitySMS(siteId, 2143198, mobile, merchantName, time, title, texturl, type).toString();
//        return result;*/
//        SmsParams smsParams = transformParam(siteId, mobile, type, type, merchantName, merchantName,time,title,texturl);
//        return commonSMS(SmsEnum.ACTIVITYSMS,smsParams).toString();
//    }

//    /**
//     * 提货码新版
//     * type 300
//     */
//    public String ladingCode2(Integer siteId, String sendphone, String head, String storeName, String address, String storephone, String incode, String url, Integer type) {
//       /* String word = "" + head + "提醒您到" + storeName + "提货；地址：" + address + "；电话：" + storephone + "；提货码：" + incode + "点击查看条形码" + url + "";
//        String result = djSmsService.SendMessage(siteId, word, sendphone, type, type);
//        if (!result.equals("0")) {
//            result = ypSmsService.ladingCode2(siteId, sendphone, head, storeName, address, storephone, incode, url, 2176432, type).toString();
//        }
//        return result;*/
//
//        SmsParams smsParams = transformParam(siteId, sendphone, type, type, head, storeName,address,storephone,incode,url);
//        return commonSMS(SmsEnum.LADINGCODE2,smsParams).toString();
//    }
//
//    /**
//     * 物流短信模板
//     * type:300, smsType:800
//     */
//    public String logisticsSMS(Integer siteId, String phone, Integer type, Integer smsType,Long tradesId,String userName) {
//        //发货失败，订单号：【tradesId】，收货人：【userName】的订单发货失败，请及时处理。
//        //return djSmsService.SendMessage(siteId, productNumber, phone, type, smsType);
//        SmsParams smsParams = transformParam(siteId, phone, type, type, tradesId.toString(),userName);
//        return commonSMS(SmsEnum.LOGISTICSSMS,smsParams).toString();
//    }

    /**
     * 异常短信模板1
     *
     * @param siteId
     * @param produceNumber
     * @param oldPhone
     * @param type
     * @return
     */
    public String sendErrorMessage(Integer siteId, String produceNumber, String oldPhone, Integer type) {
        //produceNumber:【environment】环境【services】异常短信，ip地址:【ipAddress】
        return djSmsService.SendMessage(siteId, produceNumber, oldPhone, type, type);
    }

//    /**
//     * 服务商模式短信模板:充值成功短信
//     *
//     * @param siteId
//     * @param
//     * @param oldPhone
//     * @param type
//     * @return
//     */
//    public String serviceSMS_charge(Integer siteId, String oldPhone, Integer type,SmsEnum smsEnum ,String... args) {
//        //提醒商户：【51健康】提醒：您已成功充值【money】 元
//        //提醒51后台：【51健康】商户：【merchantName】【siteId】已成功充值 【money】 元
//        //return djSmsService.SendMessage(siteId, produceNumber, oldPhone, type, type);
//        SmsParams smsParams = transformParam(siteId, oldPhone, type, type, args);
//        return commonSMS(smsEnum,smsParams).toString();
//    }
//
//    /**
//     * 服务商模式短信模板:信用用完提醒（停止商家服务）
//     *
//     * @param siteId
//     * @param
//     * @param oldPhone
//     * @param type
//     * @return
//     */
//    public String serviceSMS_closeService(Integer siteId, String oldPhone,Integer serviceType, Integer type,SmsEnum smsEnum ,String... args) {
//        //提醒商户：【51健康】提醒：您的信用值已用完，并且已停止用户下单，请及时处理
//        //提醒51后台：【51健康】商户：【merchantName】【siteId】信用值已用完，并且已停止用户下单，请及时处理
//        //return djSmsService.SendMessage(siteId, produceNumber, oldPhone, serviceType, type);
//        SmsParams smsParams = transformParam(siteId, oldPhone, type, type, args);
//        return commonSMS(smsEnum,smsParams).toString();
//    }
//
//    /**
//     * 服务商模式短信模板:低于预警值
//     *
//     * @param siteId
//     * @param
//     * @param oldPhone
//     * @param type
//     * @returns
//     */
//    public String serviceSMS_belowValue(Integer siteId, String oldPhone,Integer serviceType, Integer type,SmsEnum smsEnum ,String... args) {
//        //【51健康】提醒：您的余额已低于预警值，请及时充值，以免影响业务.
//
//        //return djSmsService.SendMessage(siteId, produceNumber, oldPhone, serviceType, type);
//        SmsParams smsParams = transformParam(siteId, oldPhone, type, type, args);
//        return commonSMS(smsEnum,smsParams).toString();
//    }
//
//    /**
//     * 服务商模式短信模板，商户，51后台：余额不足短信提醒
//     *
//     * @param siteId
//     * @param
//     * @param oldPhone
//     * @param serviceType //服务商状态
//     * @param smsType   //短信状态
//     * @return
//     */
//    public String serviceSMS_lackbalance(Integer siteId, String oldPhone,Integer serviceType, Integer smsType,SmsEnum smsEnum ,String... args) {
//        //提醒商户：【51健康】提醒：您的余额已不足【money】 元，请及时充值，以免影响业务
//        //提醒51后台：【51健康】商户：【merchantName】【siteId】的余额已不足【money】元，请提醒充值
//        //return djSmsService.SendMessage(siteId, produceNumber, oldPhone, serviceType, smsType);
//        SmsParams smsParams = transformParam(siteId, oldPhone, serviceType, smsType, args);
//        return commonSMS(smsEnum,smsParams).toString();
//    }

    /**
     * 通用短信 api
     * @param smsParams
     * @return
     */
    public String SendMessage(SmsParams smsParams){
        return commonSMS(smsParams).toString();
    }


    /**
     * 调用通用短信模板前的参数转换，siteId,phone,serviceType,sysType为必须参，args是模板中
     * 需要用到的参数，必须按照模板所需参数的顺序来传入。
     * @param siteId
     * @param phone
     * @param serviceType
     * @param sysType
     * @param args
     * @return
     */
    public SmsParams transformParam(Integer siteId, String phone,Integer serviceType, Integer sysType, SmsEnum smsEnum,String... args){
        if(args == null){
            throw new IllegalParameterException("非法参数：参数不能为null");
        }
        for(int i = 0; i<args.length;i++){
            if(args[i]==null){
                args[i] = "";
            }
        }
        SmsParams smsParams = new SmsParams(siteId,phone,serviceType,sysType,smsEnum,args);
        return smsParams;
    }
    /**
     * 通用短信模板需要传入短信模板枚举，以及SmsParams
     * @param smsParams
     * @return
     */
    private Integer commonSMS(SmsParams smsParams){
        int id = smsParams.getSmsEnum().getTpl_id();
        SmsTemplate smsTemplate = smsTplMapper.selectSmsTplById(id);
        String word = smsTemplate.getWord();
        int yp_tpl_id = smsTemplate.getYp_tpl_id();     //云片模板id
        //int dj_status = smsTemplate.getDj_status();    //点集接口开启状态 1为开启 0为关闭
        //int yp_status = smsTemplate.getYp_status();    //云片接口开启状态 1为开启 0为关闭

        String[] args = smsParams.getArgs();
        String status = "-1";
        if(isDjSend(word,smsTemplate.getDj_status())){
            status = djSend(word,args,smsParams);
        }
        if (idYpSend(status,yp_tpl_id,smsTemplate.getYp_status())) {
            return ypSend(yp_tpl_id,smsParams,word,smsTemplate);
        }
        return Integer.parseInt(status);
    }

    private boolean isDjSend(String word ,int dj_status){
        return word!=null && dj_status!=0;
    }
    private String djSend(String word,String[] args,SmsParams smsParams){
        int i1 = word.split("%s").length - 1;
        int length = args.length;
        if((word.split("%s").length-1)!=args.length){
            throw new IllegalParameterException("非法参数：参数不匹配");
        }
        for (int i=0; i<args.length; i++){
            if(args[i] == null){
                throw new IllegalParameterException("非法参数：参数不匹配");
            }
        }
        word = String.format(word,args);
        return djSmsService.SendMessage(smsParams.getSiteId(), word, smsParams.getPhone(), smsParams.getServiceType(), smsParams.getSmsType());
    }

    private boolean idYpSend(String status,int yp_tpl_id,int yp_status){
        return !Constant.SUCCESS.equals(status) && yp_tpl_id!=0 && yp_status!=0;
    }
    private Integer ypSend(int yp_tpl_id,SmsParams smsParams,String word,SmsTemplate smsTemplate){
        Integer result = ypSmsService.sendNormalMessage(yp_tpl_id,smsParams,word,smsTemplate.getParamName());
        if (result != 0) {
            result = -1;
        }
        return result;
    }
}
