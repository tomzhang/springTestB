package com.jk51.modules.balance.service;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.sms.ServiceType;
import com.jk51.commons.sms.SysType;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.account.models.PayPlatform;
import com.jk51.model.balance.Balance;
import com.jk51.model.balance.BalanceAccount;
import com.jk51.model.balance.BalanceAccountMonth;
import com.jk51.model.balance.BalanceDetail;
import com.jk51.model.order.Refund;
import com.jk51.model.order.Trades;
import com.jk51.model.order.TradesExt;
import com.jk51.modules.account.mapper.PayPlatformMapper;
import com.jk51.modules.account.mapper.SettlementDetailMapper;
import com.jk51.modules.balance.mapper.BalanceMapper;
import com.jk51.modules.merchant.service.LabelService;
import com.jk51.modules.pay.exception.PayException;
import com.jk51.modules.pay.service.PayService;
import com.jk51.modules.pay.service.ali.AliPayApi;
import com.jk51.modules.pay.service.ali.request.AliRequestParam;
import com.jk51.modules.sms.service.CommonService;
import com.jk51.modules.sms.smsConfig.SmsEnum;
import com.jk51.modules.trades.mapper.RefundMapper;
import com.jk51.modules.trades.mapper.TradesExtMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.modules.trades.service.TradesConvertService;
import com.jk51.modules.trades.service.TradesDeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/4/26.
 */
@Service
public class BalanceService {
    private static final Logger log = LoggerFactory.getLogger(BalanceService.class);
    @Autowired
    private BalanceMapper balanceMapper;

    @Autowired
    private LabelService labelService;
    @Autowired
    private PayPlatformMapper payPlatformMapper;

    @Autowired
    SettlementDetailMapper settlementDetailMapper;

    @Autowired
    TradesMapper tradesMapper;

    @Autowired
    BaseFeeService baseFeeService;

    @Autowired
    TradesDeliveryService tradesDeliveryService;

    @Autowired
    TradesExtMapper tradesExtMapper;
    @Autowired
    private TradesConvertService tradesConvertService;

    @Autowired
    CommonService commonService;
    @Autowired
    private PayService payService;
    @Autowired
    private AliPayApi aliPayApi;


    /**
     * 根据ID查询商户下的余额配置信息
     * @param siteId
     * @return
     */
    public Balance getBalanceBySiteId(Integer siteId) {
        return balanceMapper.getBalanceBySiteId(siteId);
    }

    /**
     * 根据ID更新商户下的余额配置信息
     * @param balance
     * @return
     */
    public ReturnDto editBalanceBySiteId(Balance balance) {
        try {
            Integer siteId = balance.getSiteId();
            //根据siteId查询商家记录，有就修改，没有添加
            Integer j = balanceMapper.getBalanceCountBySiteId(siteId);
            Integer i = 0;
            if (j > 0){
                Date date = new Date();
                balance.setRecordTime(date);
                i = balanceMapper.updateBalanceBySiteId(balance);
            }else {
                i = balanceMapper.insertBalanceBySiteId(balance);
            }

            //返回
            if (i == 1){
                return ReturnDto.buildSuccessReturnDto();
            }else {
                return ReturnDto.buildFailedReturnDto("因找不到数据导致更新失败");
            }
        } catch (Exception e) {
            log.error("更新操作异常", e);
            return ReturnDto.buildFailedReturnDto("更新操作异常");
        }
    }

    /**
     * 查询商家账户余额
     * @param siteId
     * @return
     */
    public Integer getBalancePayForSiteId(Integer siteId){
        return balanceMapper.getBalancePayForSiteId(siteId);
    }

    /**
     * 修改商家账户余额
     * @param siteId
     * @param balancePay
     * @return
     */
    public Integer updateBalancePay(Integer siteId,Integer balancePay){
        try{
            Balance balance = new Balance();
            balance.setBalance(balancePay);
            balance.setSiteId(siteId);
            return balanceMapper.updateBalanceBySiteId(balance);
        }catch (Exception e){
            log.error("更新操作异常", e);
            return 0;
        }
    }

    /**
     * 添加余额操作日志
     * payChange  金额变动（以分为单位）
     * applyStatus  操作类型 0.线下充值（+）  1.退佣（+）  2.抽佣（-）  3.返点（+）  4.短信费用（-）  5.运费（-）  6.退运费（+） 7微信充值  8支付宝充值
     * description ：操作描述长度不要超过1000
     * tradesId  订单ID
     * msgStatus  短信类型
     * msgNum 短信条数
     * 订单ID
     * @return
     */
//    public static volatile Integer count ;
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Integer insertBalanceDetail(Integer siteId,Integer applyStatus,Integer payChangePay,String description,Long tradesId,Integer msgStatus,Integer msgNum){
        if (StringUtil.isEmpty(msgNum)){msgNum = 1;}
        try{
            Integer count = 0;
//            Balance balance = balanceMapper.getBalanceBySiteIdForBool(siteId);
            if (!StringUtil.isEmpty(payChangePay)){
                payChangePay = Math.abs(payChangePay);//保持金额为正数
            }
            Integer id = balanceMapper.getId(siteId);
            Balance balanceObj = balanceMapper.getBalanceBySiteId2(id);//获取余额对象
            if (StringUtil.isEmpty(balanceObj) || 1 == balanceObj.getIsValid()){//判断是否是服务商商户
                return 0;
            }

            if (applyStatus == 2 || applyStatus == 5){//避免订单佣金记录的重复添加
                count++;
                Integer k = balanceMapper.boolIExist(siteId,tradesId,applyStatus);
                if (count != 1 || k > 0){
                    return 0;
                }
            }
            if(applyStatus == 4){//判断短信是否重复插入
                List<Map<String,Object>> fiveMapList = balanceMapper.getFiveMap(siteId);//根据siteId查询最近的5条短信记录
                for (Map<String,Object> fiveMap : fiveMapList){
                    String desc = String.valueOf(fiveMap.get("description"));
                    if (description.equals(desc)){
                        return 0;
                    }
                }
            }
            BalanceDetail balanceDetail = new BalanceDetail();
            balanceDetail.setSiteId(siteId);
            int balancePay = balanceObj.getBalance();//获取主表余额
            balanceDetail.setDescription(description);

            //获取操作类型
            if (applyStatus == 0){//充值
                count++;
                balancePay = balancePay + payChangePay;
                balanceDetail.setBalanceStatus(0);
                balanceObj.setMsgNum(3);
                balanceDetail.setApplyStatus(0);
                count++;

            }else if (applyStatus == 1){//退款（退还佣金）
                balanceDetail.setTradesId(tradesId);
                count++;
                balancePay = balancePay + payChangePay;
                balanceDetail.setBalanceStatus(0);
                balanceDetail.setApplyStatus(1);
                count++;

            }else if (applyStatus == 2){//抽佣
                count++;
                balanceDetail.setTradesId(tradesId);
                balancePay = balancePay - payChangePay;
                balanceDetail.setBalanceStatus(1);
                balanceDetail.setApplyStatus(2);

            }else if (applyStatus == 3){//返点
                count++;
                balancePay = balancePay + payChangePay;
                balanceDetail.setBalanceStatus(0);
                balanceDetail.setApplyStatus(3);
                count++;

            }else if (applyStatus == 4){//短信费用
                count++;
                if (count > 1){
                    return 0;
                }
//                Balance balanceObj2 = balanceMapper.getBalanceBySiteId(siteId);//获取余额对象
                int balancePay2 = balanceObj.getBalance();//获取主表余额
                Integer msgTotalNum = balanceObj.getMsgTotalNum();//商户信息总数
                Integer charge = isCharge(siteId, msgStatus, msgTotalNum) * msgNum;//判断是否收费
                balancePay = balancePay2 - charge;
                payChangePay = charge;
                balanceDetail.setBalanceStatus(1);
                balanceDetail.setMsgStatus(msgStatus);
                msgTotalNum++;
                balanceObj.setMsgTotalNum(msgTotalNum);
                balanceDetail.setApplyStatus(4);

                //获取手机号和订单ID
                Map<String, Object> msgMap = getMsgMap(description);
                if (msgMap.containsKey("phone") && !StringUtil.isEmpty(msgMap.get("phone"))){
                    balanceDetail.setPhone(String.valueOf(msgMap.get("phone")));
                }
                if (msgMap.containsKey("trades_id") && !StringUtil.isEmpty(msgMap.get("trades_id"))){
                    tradesId = Long.parseLong(String.valueOf(msgMap.get("trades_id")));
                    balanceDetail.setTradesId(tradesId);
                }
                count++;

            }else if (applyStatus == 5){//运费
                balanceDetail.setTradesId(tradesId);
                count++;
                balancePay = balancePay - payChangePay;
                balanceDetail.setBalanceStatus(1);
                balanceDetail.setApplyStatus(5);

            }else if (applyStatus == 6){//退运费
                count++;
                balanceDetail.setTradesId(tradesId);
                balancePay = balancePay + payChangePay;
                balanceDetail.setBalanceStatus(0);
                balanceDetail.setApplyStatus(6);
                count++;

            }

            //获取订单支付时间
            if (!StringUtil.isEmpty(tradesId)){
                Date date = balanceMapper.getPayTime(siteId,tradesId);
                if (!StringUtil.isEmpty(date) && !date.equals("null")){
                    balanceDetail.setPayTime(date);
                }
            }

            balanceObj.setBalance(balancePay);
            balanceDetail.setBalance(balancePay);
            balanceDetail.setPayChange(payChangePay);
            balanceDetail.setSerialNum(getSerialNum(siteId));//流水号


            if (count == 2){
                Integer j = balanceMapper.updateBalanceBySiteId(balanceObj);//修改余额金额
                Integer i = balanceMapper.insertBalanceDetail(balanceDetail);//添加日志
                if (applyStatus == 2 && j == 1 && i == 1){//佣金记录添加成功，修改订单表中的结算状态
                    balanceMapper.updateTradesStatusBySuccess(tradesId);
                }
                if (j == 1 && applyStatus != 1 && applyStatus != 3 && applyStatus != 6){ //除充值外，收入不走短信接口
                    boolBalanceIsWarning(siteId,payChangePay,applyStatus);//调用短信接口
                }
                return 1;
            }
            return 0;
        }catch (Exception e){
            log.error("日志添加异常:{}", e);
            Map<String,Object> map = new HashMap<>();
            map.put("siteId",siteId);
            map.put("applyStatus",applyStatus);
            map.put("payChangePay",payChangePay);
            map.put("description",description);
            map.put("tradesId",tradesId);
            balanceMapper.insertBalnceLog(map);
            return 0;
        }
    }

    //处理短信
    public Map<String,Object> getMsgMap(String s){
        Map<String,Object> returnMap = new HashMap<>();
        try{
            if (s.contains(":")){
                Map<String, Object> msgMap = JacksonUtils.json2map(s);
                if (msgMap.containsKey("msg") && !StringUtil.isEmpty(msgMap.get("msg"))){
                    String msg = String.valueOf(msgMap.get("msg"));
                    Pattern p = Pattern.compile("\\d{19,}");//19指的是订单ID长度
                    Matcher m = p.matcher(msg);
                    while (m.find()) {
                        String y = m.group();
                        returnMap.put("trades_id",y);
                    }
                }
                if (msgMap.containsKey("phone") && !StringUtil.isEmpty(msgMap.get("phone"))){
                    String phone = String.valueOf(msgMap.get("phone"));
                    returnMap.put("phone",phone);
                }
            }
        }catch (Exception e){
            log.info("信息内容转换异常：{}",e);
        }
        return returnMap;
    }

    /**
     * 判断商户是否是服务商
     * @param siteId
     * @return
     */
    public String boolIsProvider(Integer siteId){
        try{
            Balance balance = balanceMapper.getBalanceBySiteIdForBool(siteId);
            if (!StringUtil.isEmpty(balance)){
                return "YES";
            }else {
                return "NO";
            }
        }catch (Exception e){
            log.error("判断商户余额是否还可以使用操作异常", e);
            return "NO";
        }
    }

    /**
     * 判断商户余额是否还可以使用？
     * @param siteId
     * @param commission  金额
     * @return
     */
    public String boolBalanceIsValid(Integer siteId,Integer commission,String payType){
        try{
            Balance balanceObj = balanceMapper.getBalanceBySiteIdForBool(siteId);//获取余额对象
            if(StringUtil.isEmpty(balanceObj)){
                log.info("没有配置服务商商户，默认可以支付");
                return "NO";
            }
            int balance = balanceObj.getBalance();//余额
            int credit = balanceObj.getCredit();
            commission=this.getOrderPayCommission(siteId, commission, payType);
            //是否还可以使用
            if (balance + credit > 0 && balance + credit > commission){
                return "YES";
            }else {
                return "NO";
            }
        }catch (Exception e){
            log.error("判断商户余额是否还可以使用操作异常", e);
            return "NO";
        }
    }

    /**
     * 判断是否要发信息提醒？
     * @param siteId
     * @param payChangePay 充值金额
     * @return
     */
    public void boolBalanceIsWarning(Integer siteId,Integer payChangePay,Integer applyStatus){
        try{
            Balance balance1MsgNum = new Balance();
            balance1MsgNum.setSiteId(siteId);
            Balance balanceObj = balanceMapper.getBalanceBySiteId(siteId);//获取余额对象
            int balanceWarning = balanceObj.getBalanceWarning();//预警值
            Double warningFirst = balanceWarning * 0.3;//预警值的30%
            Double warningSecond = balanceWarning * 0.1;//预警值的10%
            Integer balance = balanceObj.getBalance();//账户余额

            String weMobileStr = balanceObj.getDutyPhone();
            List<String> weMobileList = labelService.stringToList(weMobileStr);

            Integer msgNum = balanceObj.getMsgNum();

            Map<String,Object> mechant = balanceMapper.getMechantNameBySiteId(siteId);//商家信息
            String mechantName = String.valueOf(mechant.get("merchant_name"));
            String mechantMobile = String.valueOf(mechant.get("service_mobile"));

            String mechantMsg = "";
            String weMsg = "";
            payChangePay = payChangePay/100;
            if (applyStatus == 0){
                //mechantMsg = "【51健康】提醒：您已成功充值" + payChangePay/100 + "元";
                //weMsg = "【51健康】商户："+ mechantName+ "（" + siteId + "）" + "已成功充值" + payChangePay/100 + "元";
                commonService.SendMessage(commonService.transformParam(siteId,mechantMobile,null,SysType.PREWARNING_VALUE,
                    SmsEnum.SERVICE_CHARGE_SH,payChangePay.toString()));
                for (String weMobile : weMobileList){
                    commonService.SendMessage(commonService.transformParam(siteId,weMobile,null,SysType.PREWARNING_VALUE,
                        SmsEnum.SERVICE_CHARGE_HT,mechantName,siteId.toString(),payChangePay.toString()));
                }
            }
            else if (applyStatus == -1){
                //mechantMsg = "【51健康】提醒：您的信用值已用完，并且已停止用户下单，请及时处理";
                //weMsg = "【51健康】商户："+ mechantName + "（" + siteId + "）" + "信用值已用完，并且已停止用户下单，请及时处理";
                commonService.SendMessage(commonService.transformParam(siteId,mechantMobile,ServiceType.TO_MERCHANT,SysType.PREWARNING_VALUE,
                    SmsEnum.SERVICE_CLOSE_SH,""));
                for (String weMobile : weMobileList){
                    commonService.SendMessage(commonService.transformParam(siteId,weMobile,ServiceType.TO_BACKSTAGE,SysType.PREWARNING_VALUE,
                        SmsEnum.SERVICE_CLOSE_HT,mechantName,siteId.toString()));
                }

            }
            else if (applyStatus == -2){
                //mechantMsg = "【51健康】提醒：您的余额已低于预警值，请及时充值，以免影响业务";
                commonService.SendMessage(commonService.transformParam(siteId,mechantMobile,ServiceType.TO_MERCHANT,SysType.PREWARNING_VALUE,
                    SmsEnum.SERVICE_BELOW_VALUE,""));
            }
            else if (applyStatus == 2 || applyStatus == 4 || applyStatus == 5){
                int tag = 0;
                //余额为0
                if (balance <= 0 && msgNum > 0){
                    balance1MsgNum.setMsgNum(0);
                    Integer j = balanceMapper.updateBalanceBySiteId(balance1MsgNum);
                    //mechantMsg = "【51健康】提醒：您的账户余额不足 0 元，请及时充值，以免影响业务";
                    //weMsg = "【51健康】商户："+ mechantName + "（" + siteId + "）" +" 的余额不足 0 元，请提醒充值";

                    commonService.SendMessage( commonService.transformParam(siteId,mechantMobile,ServiceType.TO_MERCHANT,SysType.PREWARNING_VALUE,
                        SmsEnum.SERVICE_LACK_BALANCE_SH,"0"));
                    for (String weMobile : weMobileList){
                        commonService.SendMessage(commonService.transformParam(siteId,weMobile,ServiceType.TO_BACKSTAGE,SysType.PREWARNING_VALUE,
                            SmsEnum.SERVICE_LACK_BALANCE_HT,mechantName,siteId.toString(),"0"));
                    }
                    tag++;
                }

                //预警值的10%
                if (tag == 0 && balance <= warningSecond && msgNum > 1){
                    balance1MsgNum.setMsgNum(1);
                    Integer j = balanceMapper.updateBalanceBySiteId(balance1MsgNum);
                    //mechantMsg = "【51健康】提醒：您的余额已不足" + warningSecond*0.01 +"元，请及时充值，以免影响业务";
                    //weMsg = "【51健康】商户："+ mechantName + "（" + siteId + "）" +" 的余额已不足" + warningSecond*0.01 +"元，请提醒充值";
                    warningSecond = warningSecond*0.01;
                    commonService.SendMessage(commonService.transformParam(siteId,mechantMobile,ServiceType.TO_MERCHANT,SysType.PREWARNING_VALUE,
                        SmsEnum.SERVICE_LACK_BALANCE_SH,warningSecond.toString()));
                    for (String weMobile : weMobileList){
                        commonService.SendMessage(commonService.transformParam(siteId,weMobile,ServiceType.TO_BACKSTAGE,SysType.PREWARNING_VALUE,
                            SmsEnum.SERVICE_LACK_BALANCE_HT,mechantName,siteId.toString(),warningSecond.toString()));
                    }
                    tag++;
                }

                //预警值的30%
                if (tag == 0 && balance <= warningFirst && msgNum > 2){
                    balance1MsgNum.setMsgNum(2);
                    Integer j = balanceMapper.updateBalanceBySiteId(balance1MsgNum);
                    //mechantMsg = "【51健康】提醒：您的余额已不足" + warningFirst*0.01 +"元，请及时充值，以免影响业务";
                    //weMsg = "【51健康】商户："+ mechantName + "（" + siteId + "）" +" 的余额已不足" + warningFirst*0.01 +"元，请提醒充值";
                    warningFirst = warningFirst*0.01;
                    commonService.SendMessage(commonService.transformParam(siteId,mechantMobile,ServiceType.TO_MERCHANT,SysType.PREWARNING_VALUE,
                        SmsEnum.SERVICE_LACK_BALANCE_SH,warningFirst.toString()));
                    for (String weMobile : weMobileList){
                        commonService.SendMessage(commonService.transformParam(siteId,weMobile,ServiceType.TO_BACKSTAGE,SysType.PREWARNING_VALUE,
                            SmsEnum.SERVICE_LACK_BALANCE_HT,mechantName,siteId.toString(),warningFirst.toString()));
                    }
                }
            }
        }catch (Exception e){
            log.error("发送信息提醒操作异常", e);
        }
    }


    /**
     * 查询余额明细
     *  siteId
     *
     * @return
     */
    public Map<String,Object> getBalanceDetail(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> balanceList = balanceMapper.getBalanceDetail(params);
            if (balanceList.size() > 0){
                for (Map<String,Object> balaceMap : balanceList){
                    if (!StringUtil.isEmpty(balaceMap.get("tradesId"))){
                        balaceMap.put("tradesId",String.valueOf(balaceMap.get("tradesId")));
                    }
                    if (4 == Integer.parseInt(String.valueOf(balaceMap.get("applyStatus")))){
                        String description = String.valueOf(balaceMap.get("description"));
                        if (description.contains("phone")){
                            Map<String,Object> phoneMap = JacksonUtils.json2map(description);
                            if (!StringUtil.isEmpty(phoneMap.get("phone"))){
                                String phone = String.valueOf(phoneMap.get("phone"));
                                balaceMap.put("tradesId",phone);
                            }
                        }
                    }
                    balaceMap.put("balance",String.valueOf(balaceMap.get("balance")).replaceAll(",",""));
                    balaceMap.put("payChange",String.valueOf(balaceMap.get("payChange")).replaceAll(",",""));
                }
            }
            PageInfo<Map<String, Object>> allList = new PageInfo<>(balanceList);
            map.put("allList", allList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询余额明细
     *  siteId
     *
     * @return
     */
    public Integer getWarning(Map<String,Object> params){
        try{
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            Balance balanceObj = balanceMapper.getWarning(siteId);
            if (!StringUtil.isEmpty(balanceObj)){
                Integer balance = Integer.parseInt(String.valueOf(balanceObj.getBalance()));
                Integer balanceWarning = Integer.parseInt(String.valueOf(balanceObj.getBalanceWarning()));
                Integer credit = Integer.parseInt(String.valueOf(balanceObj.getCredit()));
                Integer isValid = Integer.parseInt(String.valueOf(balanceObj.getIsValid()));//禁用字段

                if (balance < balanceWarning && balance + credit > 0){
                    return 1;
                }else if(balance + credit == 0){
                    return 2;
                }else {

                    return 0;
                }
            }else {
                return 0;
            }
        }catch (Exception e){
            log.info("查询异常:{}", e);
            return 0;
        }
    }

    /**
     * 查询结算记录
     *  siteId
     *
     * @return buildSuccessReturnDto
     */
    public Map<String,Object> getBalanceAccountBySiteId(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> balanceAccounts = balanceMapper.getBalanceAccountBySiteId(params);
            balanceAccounts = getListMap(balanceAccounts);
            PageInfo<Map<String, Object>> allList = new PageInfo<>(balanceAccounts);
            map.put("allList", allList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 商户后台：查询账户余额
     *  siteId
     *
     * @return buildSuccessReturnDto
     */
    public Map<String,Object> getAccountBalanceBySiteId(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));

            Map<String, Object> yestodayTime = getYestodayTime(new Date(), -0);//获取昨天时间
            String startTime = String.valueOf(yestodayTime.get("startTime"));
            String endTime = String.valueOf(yestodayTime.get("endTime"));

            Map<String, Object> beforeYestodayTime = getYestodayTime(new Date(), -1);//获取前天最后一条数据的时间
            String startTimeBefore = String.valueOf(beforeYestodayTime.get("startTime"));
            String endTimeBefore = String.valueOf(beforeYestodayTime.get("endTime"));

            Map<String,Object> yestodayDate = balanceMapper.getTodayData(siteId, startTime, endTime,startTimeBefore,endTimeBefore);
            yestodayDate.put("invoiceMoney",String.valueOf(yestodayDate.get("invoiceMoney")).replaceAll(",",""));
            map.put("accountBalance", yestodayDate);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }


    /**
     * 51后台：查询账户余额
     */
    public Map<String,Object> getAccountBalance(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Map<String, Object> yestodayTime = getYestodayTime(new Date(), -0);//获取昨天时间
            String startTime = String.valueOf(yestodayTime.get("startTime"));
            String endTime = String.valueOf(yestodayTime.get("endTime"));
            params.putAll(yestodayTime);

            Map<String, Object> beforeYestodayTime = getYestodayTime(new Date(), -1);//获取前天最后一条数据的时间
            String startTimeBefore = String.valueOf(beforeYestodayTime.get("startTime"));
            String endTimeBefore = String.valueOf(beforeYestodayTime.get("endTime"));
            params.put("startTimeBefore",startTimeBefore);
            params.put("endTimeBefore",endTimeBefore);

            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> accountBalanceList = balanceMapper.getAccountBalance(params);
            if (accountBalanceList.size() > 0){
                for (Map<String,Object> accountBalance : accountBalanceList){
                    Double balance = Double.parseDouble(String.valueOf(accountBalance.get("balance")).replaceAll(",",""));
                    Double balance_warning = Double.parseDouble(String.valueOf(accountBalance.get("balance_warning")).replaceAll(",",""));
                    Integer is_valid = Integer.parseInt(String.valueOf(accountBalance.get("is_valid")));
                    Double credit = Double.parseDouble(String.valueOf(accountBalance.get("credit")).replaceAll(",",""));
                    if (balance > balance_warning ){
                        accountBalance.put("credit_status","正常");
                    }else if (balance <= balance_warning ){
                        accountBalance.put("credit_status","预警");
                    }else if (balance + credit < 0){
                        accountBalance.put("credit_status","暂停");
                    }
                    accountBalance.put("lastBalance",String.valueOf(accountBalance.get("lastBalance")).replaceAll(",",""));
                    accountBalance.put("upBalance",String.valueOf(accountBalance.get("upBalance")).replaceAll(",",""));
                    accountBalance.put("nowBalance",String.valueOf(accountBalance.get("nowBalance")).replaceAll(",",""));
                    accountBalance.put("balance_warning",String.valueOf(accountBalance.get("balance_warning")).replaceAll(",",""));
                    accountBalance.put("credit",String.valueOf(accountBalance.get("credit")).replaceAll(",",""));
                }
            }

            PageInfo<Map<String, Object>> allList = new PageInfo<>(accountBalanceList);

            map.put("allList", allList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 51后台：查询支付明细
     */
    public Map<String,Object> getAccountBalanceSiteDetail(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{

            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> accountBalanceList = balanceMapper.getAccountBalanceSiteDetail(params);
            if (accountBalanceList.size() > 0){
                for (Map<String,Object> accountBalanceMap :accountBalanceList){
                    if (!StringUtil.isEmpty(accountBalanceMap.get("tradesId"))){
                        accountBalanceMap.put("tradesId",accountBalanceMap.get("tradesId").toString());
                    }
                    if (4 == Integer.parseInt(String.valueOf(accountBalanceMap.get("applyStatus")))){
                        String description = String.valueOf(accountBalanceMap.get("description"));
                        if (description.contains("phone")){
                            Map<String,Object> phoneMap = JacksonUtils.json2map(description);
                            if (!StringUtil.isEmpty(phoneMap.get("phone"))){
                                String phone = String.valueOf(phoneMap.get("phone"));
                                accountBalanceMap.put("tradesId",phone);
                            }
                        }
                    }
                    accountBalanceMap.put("balance",String.valueOf(accountBalanceMap.get("balance")).replaceAll(",",""));
                }
            }

            PageInfo<Map<String, Object>> allList = new PageInfo<>(accountBalanceList);

            map.put("allList", allList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 修改备注
     */
    public Map<String,Object> updateHqRemark(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer i = balanceMapper.updateHqRemark(params);
            if (i == 1){
                map.put("status", 0);
            }else {
                map.put("status", -1);
            }
            return map;
        }catch (Exception e){
            log.info("修改异常:{}", e);
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 51后台：根据ID查询充值详情
     */
    public Map<String,Object> getUpBalanceById(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Map<String,Object> upMap = balanceMapper.getUpBalanceById(params);
            if (!StringUtil.isEmpty(upMap.get("apply_status"))){
                Integer apply_status = Integer.parseInt(String.valueOf(upMap.get("apply_status")));
                String description = String.valueOf(upMap.get("description"));
                if (apply_status == 4 || apply_status == -2){
                    if (description.contains("type") || description.contains("phone")  || description.contains("msg")){
                        Map<String,Object> descriptionMap = JacksonUtils.json2map(description);
                        if (!StringUtil.isEmpty(descriptionMap.get("type"))){
                            upMap.put("msg_status",String.valueOf(descriptionMap.get("type")));
                        }
                        if (!StringUtil.isEmpty(descriptionMap.get("phone"))){
                            upMap.put("mobile",String.valueOf(descriptionMap.get("phone")));
                        }
                        if (!StringUtil.isEmpty(descriptionMap.get("msg"))){
                            upMap.put("description",String.valueOf(descriptionMap.get("msg")));
                        }
                    }else {
                        upMap.put("description",description);
                        upMap.put("msg_status","820");
                    }
                }
            }
            map.put("upMap", upMap);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 51后台：商家账单
     */
    public Map<String,Object> getBalanceAccountForSite(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{

            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> accountBalanceList = balanceMapper.getBalanceAccountForSite(params);
            accountBalanceList = getListMap2(accountBalanceList);
            PageInfo<Map<String, Object>> allList = new PageInfo<>(accountBalanceList);
            map.put("allList", allList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 结算弹框
     */
    public Map<String,Object> getAccountInfo(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Map<String,Object> accountInfoMap = balanceMapper.getAccountInfo(params);
            if (!StringUtil.isEmpty(accountInfoMap) && !StringUtil.isEmpty(accountInfoMap.get("now_expend")) && !StringUtil.isEmpty(accountInfoMap.get("now_income"))){
                Integer now_expend = Integer.parseInt(String.valueOf(accountInfoMap.get("now_expend")));
                Integer now_income = Integer.parseInt(String.valueOf(accountInfoMap.get("now_income")));
                accountInfoMap.put("money",now_expend - now_income);
            }
            map.put("accountInfoMap", accountInfoMap);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 编辑结算弹框
     */
    @Transactional
    public Map<String,Object> updateAccountInfo(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer i = balanceMapper.updateAccountInfo(params);//结算
            if (i == 1){//将详情表中的数据进行结算状态修改
                String accountTime = String.valueOf(params.get("accountTime"));
                String[] arr = accountTime.split("/");
                params.put("startTime",arr[0] + " 00:00:00");
                params.put("endTime",arr[1] + " 23:59:59");
//                params.put("date",new Date());
                balanceMapper.updateBalanceDetialByTime(params);
            }
            map.put("msg", "修改成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("修改异常:{}", e);
            map.put("msg", "修改失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 51后台：交易佣金
     */
    @Transactional
    public Map<String,Object> getTradesBrokerage(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> tradesBrokerage = balanceMapper.getTradesBrokerage(params);
            PageInfo<Map<String, Object>> allList = new PageInfo<>(tradesBrokerage);
            map.put("allList", allList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 51后台：其他业务详情
     */
    @Transactional
    public Map<String,Object> getOtherDetail(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> accountBalanceList = balanceMapper.getOtherDetail(params);
            if (accountBalanceList.size() > 0){
                for (Map<String,Object> accountBalanceMap :accountBalanceList){
                    if (!StringUtil.isEmpty(accountBalanceMap.get("trades_id"))){
                        accountBalanceMap.put("trades_id",accountBalanceMap.get("trades_id").toString());
                    }
                    accountBalanceMap.put("pay_change",String.valueOf(accountBalanceMap.get("pay_change")).replaceAll(",",""));
                }
            }
            PageInfo<Map<String, Object>> allList = new PageInfo<>(accountBalanceList);
            map.put("allList", allList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 51后台：其他业务详情
     */
    @Transactional
    public Object getSettlementDetail(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            if (StringUtil.isEmpty(params.get("tradeId"))){
                return ReturnDto.buildFailedReturnDto("tradeId为空");
            }
            if (StringUtil.isEmpty(params.get("site_id"))){
                return ReturnDto.buildFailedReturnDto("siteId为空");
            }

            String tradesId = String.valueOf(params.get("tradeId"));
            Integer siteId = Integer.parseInt(String.valueOf(params.get("site_id")));
            Integer apply_status = Integer.parseInt(String.valueOf(params.get("apply_status")));
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> map2 = balanceMapper.findDetails(tradesId, siteId,apply_status);
            Object trades_id = map2.get("trades_id");
            map2.put("trades_id", trades_id + "");
            result.put("map", map2);
            Map<String, Object> map3 = settlementDetailMapper.findUserNameById(tradesId, siteId);
            Map<String,Object> storeMap = balanceMapper.getStoreName(siteId,tradesId);
            if (!StringUtil.isEmpty(storeMap)){
                if (!StringUtil.isEmpty(storeMap.get("trades_source"))){
                    Integer trades_source = Integer.parseInt(String.valueOf(storeMap.get("trades_source")));
                    if (trades_source == 120){//微信下单，下单门店取：会员填写的店员邀请码所在的门店
                        map3.put("xiadanmendian",String.valueOf(storeMap.get("invitationName")));
                    }
                    if (trades_source == 130 || trades_source == 140){//APP下单，下单门店取：下单店员所在的门店
                        map3.put("xiadanmendian",String.valueOf(storeMap.get("sourceName")));
                    }
                }
            }

            result.put("name", map3);
            return result;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 根据订单ID查询佣金
     */
    public Integer getMoneyByTradesId(Long tradesId){
        return balanceMapper.getMoneyByTradesId(tradesId);
    }

    /**
     * 51后台：月账单
     */
    public Map<String,Object> getBalanceAccountForSiteMoth(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> accountBalanceList = balanceMapper.getBalanceAccountForSiteMoth(params);
            accountBalanceList = getListMap2(accountBalanceList);
            PageInfo<Map<String, Object>> allList = new PageInfo<>(accountBalanceList);
            map.put("allList", allList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 51后台：月账单
     */
    public Map<String,Object> updateAuditStatus(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            balanceMapper.updateAuditStatus(params);
            map.put("msg", "修改成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 商户后台：月账单
     *  siteId
     *
     * @return buildSuccessReturnDto
     */
    public Map<String,Object> getBalanceAccountBySiteIdMonth(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> balanceAccounts = balanceMapper.getBalanceAccountBySiteIdMonth(params);
            balanceAccounts = getListMap(balanceAccounts);
            PageInfo<Map<String, Object>> allList = new PageInfo<>(balanceAccounts);
            map.put("allList", allList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 商户后台：月账单
     */
    public Map<String,Object> updateAuditStatusMonth(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            //如果已经审核通过，就不走修改
            Integer i = balanceMapper.getAuditStatusById(params);
            if (i != 2){
                balanceMapper.updateAuditStatusMonth(params);
            }
            map.put("msg", "修改成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }







    //抽方法
    public List<Map<String,Object>> getListMap(List<Map<String,Object>> balanceAccounts){
        if (balanceAccounts.size() > 0){
            for (Map<String,Object> accMap : balanceAccounts){
                accMap.put("lastBalance",String.valueOf(accMap.get("lastBalance")).replaceAll(",",""));
                accMap.put("nowBalance",String.valueOf(accMap.get("nowBalance")).replaceAll(",",""));
                accMap.put("upBalance",String.valueOf(accMap.get("upBalance")).replaceAll(",",""));
                accMap.put("nowIncome",String.valueOf(accMap.get("nowIncome")).replaceAll(",",""));
                accMap.put("nowExpend",String.valueOf(accMap.get("nowExpend")).replaceAll(",",""));
                accMap.put("invoiceMoney",String.valueOf(accMap.get("invoiceMoney")).replaceAll(",",""));
            }
        }
        return balanceAccounts;
    }

    //抽方法
    public List<Map<String,Object>> getListMap2(List<Map<String,Object>> accountBalanceList){
        if (accountBalanceList.size() > 0){
            for (Map<String,Object> accountInfoMap : accountBalanceList){
                if (!StringUtil.isEmpty(accountInfoMap) && !StringUtil.isEmpty(accountInfoMap.get("nowExpend")) && !StringUtil.isEmpty(accountInfoMap.get("nowIncome"))){
                    Double now_expend = Double.parseDouble(String.valueOf(accountInfoMap.get("nowExpend")).replaceAll(",",""));
                    Double now_income = Double.parseDouble(String.valueOf(accountInfoMap.get("nowIncome")).replaceAll(",",""));
                    BigDecimal b = new BigDecimal(now_expend - now_income);
                    double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    accountInfoMap.put("money",f1);
                }
                accountInfoMap.put("nowBalance",String.valueOf(accountInfoMap.get("nowBalance")).replaceAll(",",""));
                accountInfoMap.put("lastBalance",String.valueOf(accountInfoMap.get("lastBalance")).replaceAll(",",""));
                accountInfoMap.put("upBalance",String.valueOf(accountInfoMap.get("upBalance")).replaceAll(",",""));
                accountInfoMap.put("invoiceMoney",String.valueOf(accountInfoMap.get("invoiceMoney")).replaceAll(",",""));
            }
        }
        return accountBalanceList;
    }

    //短信：不收商家费用
//    public void sendMsgForFree(Integer siteId,List<String> weMobileList,String weMsg,String mechantMsg,String mechantMobile,Integer type){
//        for (String weMobile : weMobileList){
//            ztSmsService.SendMessage2(siteId,weMsg,weMobile,type);//给51后台发送
//        }
//        ztSmsService.SendMessage2(siteId,mechantMsg,mechantMobile,type);//给商家发送
//    }

    //短信：收商家费用
//    public void sendMsgForToll(Integer siteId,List<String> weMobileList,String weMsg,String mechantMsg,String mechantMobile,Integer type){
//        for (String weMobile : weMobileList){
//            ztSmsService.SendMessage2(siteId,weMsg,weMobile,type);//给51后台发送
//        }
//        ztSmsService.SendMessage(siteId,mechantMsg,mechantMobile,null,type);//给商家发送
//    }


    //生成流水号
    public String getSerialNum(Integer siteId){
        long ct = System.currentTimeMillis();
        String s = String.valueOf(ct);
        String str = s.substring(4,s.length());
        return siteId + str;
    }

    //定时任务：执行发送短信
    public void sendMsgForTiming(){
        //获取所有的商家记录
        List<Map<String,Object>> siteMap = balanceMapper.getAllBalanceObj();

        //调用短信接口
        if (siteMap.size() > 0){
            for (Map<String,Object> balanceMap : siteMap){
                Integer siteId = Integer.parseInt(String.valueOf(balanceMap.get("siteId")));
                Integer balance = Integer.parseInt(String.valueOf(balanceMap.get("balance")));
                Integer balanceWarning = Integer.parseInt(String.valueOf(balanceMap.get("balanceWarning")));
                if (balance < balanceWarning){
                    Map<String,Object> mechant = balanceMapper.getMechantNameBySiteId(siteId);//商家信息
                    String mechantMobile = String.valueOf(mechant.get("service_mobile"));
                    //String mechantMsg = "您的账户余额低于预警值，请注意额度变化，及时充值，以免影响正常业务！";
                    commonService.SendMessage(commonService.transformParam(siteId,mechantMobile,ServiceType.TO_MERCHANT,SysType.PREWARNING_VALUE,
                        SmsEnum.SERVICE_BELOW_VALUE,""));//给商家发送
                }
            }
        }
    }
    public int getOrderPayCommission(int siteId, int orderRealPrice, String payType) {
        //查询商家设置的支付方式的支付佣金比例
        PayPlatform payPlatform = payPlatformMapper.getPayPlatformById(siteId, payType);
        //支付佣金比例
        double payCommissionRate = 0.00;
        if (payPlatform != null) {
            payCommissionRate = payPlatform.getProcedure_fee();
        } else {
            //如果查询不到支付佣金比例，则兜底为0.01
            payCommissionRate = 1;
        }
        //支付佣金(单位：分)= 订单实际支付金额*支付佣金比例
        double payCommission = (double) orderRealPrice * (payCommissionRate / 100);
        //根据小数点后一位四舍五入
        BigDecimal bd = new BigDecimal(payCommission);
        //四舍五入后返回支付佣金(单位：分)
        int payCommissionInt = bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        //如果支付佣金小于1分钱，则兜底为1分钱
        /*if (payCommissionInt < 1) {
            payCommissionInt = 1;
        }*/
        return payCommissionInt;
    }

    //定时任务：执行昨天的余额结算
    public void balanceTiming(){
        //获取启用服务商模式的商户ID
        List<Integer> siteIdList = balanceMapper.getSiteIdList();
        Map<String, Object> yestodayTime = getYestodayTime(new Date(), -1);//获取昨天时间
        String startTime = String.valueOf(yestodayTime.get("startTime"));
        String endTime = String.valueOf(yestodayTime.get("endTime"));
        Date nextDate = (Date) yestodayTime.get("nextDay");

        Map<String, Object> beforeYestodayTime = getYestodayTime(new Date(), -2);//获取前天最后一条数据的时间
        String startTimeBefore = "2018-05-01 00:00:00";
        String endTimeBefore = String.valueOf(beforeYestodayTime.get("endTime"));

        timeMethod (siteIdList,startTime,endTime,nextDate,startTimeBefore,endTimeBefore);
    }

    //根据时间，结算指定某一天的
    public void oneDay(String time,Integer siteId){
        try{
            List<Integer> siteIdList;
            if (!StringUtil.isEmpty(siteId)){
                siteIdList = new ArrayList<>();
                siteIdList.add(siteId);
            }else {
                siteIdList = balanceMapper.getSiteIdList();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(time);

            String startTime = time + " 00:00:00";
            String endTime = time + " 23:59:59";
            Date nextDate = date;

            Map<String, Object> beforeYestodayTime = getYestodayTime(date, -1);//获取昨天最后一条数据的时间
            String startTimeBefore = "2018-05-01 00:00:00";
            String endTimeBefore = String.valueOf(beforeYestodayTime.get("endTime"));

            timeMethod (siteIdList,startTime,endTime,nextDate,startTimeBefore,endTimeBefore);
        }catch (Exception e){
            log.info("日期转换异常:{}", e);
        }

    }

    //抽方法
    public void timeMethod (List<Integer> siteIdList,String startTime,String endTime,Date nextDate,String startTimeBefore,String endTimeBefore){
        if (siteIdList.size() > 0){
            for (Integer siteId : siteIdList){
                BalanceAccount balanceAccount = balanceMapper.getYestodayDate(siteId,startTime,endTime,startTimeBefore,endTimeBefore);//查询昨天的结算对象
                if (StringUtil.isEmpty(balanceAccount)){
                    balanceAccount = new BalanceAccount();
                    balanceAccount.setLastBalance(0);
                    balanceAccount.setNowBalance(0);
                    balanceAccount.setUpBalance(0);
                    balanceAccount.setNowIncome(0);
                    balanceAccount.setNowExpend(0);
                    balanceAccount.setInvoiceMoney(0);
                    balanceAccount.setInvoiceStatus(0);
                }
                balanceAccount.setAccountTime(nextDate);
                balanceAccount.setSiteId(siteId);

                //插入数据
                balanceMapper.insertBalanceAccountBySiteId(balanceAccount);

                //将出账的数据状态改为已出账状态
                balanceMapper.updateOutbillStatus(siteId,startTime,endTime,nextDate);
            }
        }
    }

    //获取时间
    public Map<String,Object> getYestodayTime(Date date,Integer day){
        Map<String,Object> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nextDay = sdf.format(date);
        String startTime = nextDay + " 00:00:00";
        String endTime = nextDay + " 23:59:59";
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        map.put("nextDay",date);
        return map;
    }

    //判断短信是否收钱,收多少钱
    public Integer isCharge(Integer siteId,Integer status,Integer msgNum){
        //查询收费短信状态码
        String msgStatus = balanceMapper.getMsgStatus(siteId);
        List<String> msgList = labelService.stringToList(msgStatus);
        Balance balanceObj = balanceMapper.getBalanceBySiteId(siteId);//获取余额对象
        if (!StringUtil.isEmpty(balanceObj.getMsgSwitch()) && 0 == balanceObj.getMsgSwitch()){
            if (msgList.contains(status.toString())){//收费
                if (status == 140 || status == 820){//语音收费为一毛
                    return 10;
                }

                //查询短信规则
                List<Map<String,Object>> roleList = balanceMapper.getMsgRole(siteId);
                Integer fee = 0;
                Integer i = 0;
                for (Map<String,Object> msgRole : roleList){
                    Integer bigNum = Integer.parseInt(String.valueOf(msgRole.get("big_num")));
                    Integer smlNum = Integer.parseInt(String.valueOf(msgRole.get("sml_num")));
                    if (bigNum >= msgNum && smlNum <= msgNum){
                        fee = Integer.parseInt(String.valueOf(msgRole.get("fee")));
                        i++;
                        return fee;
                    }
                }
                if (i == 0){//如果所有的收费情况都不符合...每条短信收默认值
                    int postageFree = balanceObj.getMsgFee();//短信费率
                    fee = postageFree;
                }
                return fee;
            }else {
                return 0;
            }
        }else {
            return 0;
        }

    }

    public Map<String,Object> getBalanceSMSLst(Map<String, Object> params) {
        Map<String,Object> map = new HashMap<>();
        try{
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> accountBalanceList = balanceMapper.getBalanceSMSLst(params);
            Map map2 = new HashMap();
            for (Map<String,Object> record : accountBalanceList){
                if (!StringUtil.isEmpty(record.get("description"))){
                    if (record.get("description").toString().contains("description") || record.get("description").toString().contains("phone")){
                        map2 = JacksonUtils.json2map(record.get("description").toString());
                        record.put("phone",map2.get("phone"));
                    }else {
                        record.put("phone","");
                    }

                }
            }
            PageInfo<Map<String, Object>> allList = new PageInfo<>(accountBalanceList);
            map.put("allList", allList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }


    //初始化：根据信息内容，完善b_balance_detail表中的trades_id,phone,pay_time
    public void perfectionData(){
        Integer id = null;
        try{
            //获取启用服务商模式的商户ID
            List<Integer> siteIdList = balanceMapper.getSiteIdList();
            for (Integer siteId : siteIdList){
                //根据siteId查询每个商户的短信集合
                List<Map<String,Object>> msgMapList = balanceMapper.getMsgContent(siteId);
                if (msgMapList.size() > 0){
                    for (Map<String,Object> msgMap : msgMapList){
                        id = Integer.parseInt(String.valueOf(msgMap.get("id")));
                        Map<String,Object> map = new HashMap<>();
                        Map<String, Object> description = getMsgMap(String.valueOf(msgMap.get("description")));
                        if (description.containsKey("phone") && !StringUtil.isEmpty(description.get("phone"))){
                            String phone = String.valueOf(description.get("phone"));
                            map.put("phone",phone);
                        }
                        if (description.containsKey("trades_id") && !StringUtil.isEmpty(description.get("trades_id"))){
                            Long tradesId = Long.parseLong(String.valueOf(description.get("trades_id")));
                            Date date = balanceMapper.getPayTime(siteId,tradesId);
                            if (!StringUtil.isEmpty(date) && !date.equals("null")){
                                map.put("date",date);
                            }
                            map.put("trades_id",tradesId);
                        }
                        if (!StringUtil.isEmpty(map)){//根据ID修改对应参数
                            map.put("id",id);
                            balanceMapper.updateHomologousData(map);
                        }
                    }
                }
            }
        }catch (Exception e){
            System.out.println("----------------------------------"+id);
            log.info("查询异常:{}", e);
        }

    }

    @Autowired
    RefundMapper refundMapper;
    //初始化：对账
    public void duiZhang(Integer siteId1,String startTime,String endTime){
        try{
            List<Integer> siteIdList;
            if (!StringUtil.isEmpty(siteId1)){
                siteIdList = new ArrayList<>();
                siteIdList.add(siteId1);
            }else {
                siteIdList = balanceMapper.getSiteIdList();
            }
            for (Integer siteId : siteIdList){
                List<Trades> tradesList = tradesMapper.getTradesListBySiteId(siteId,startTime,endTime);
                if (tradesList.size() > 0){
                    for (Trades trades :tradesList){
                        Long tradesId = trades.getTradesId();
                        //添加佣金记录：根据订单ID查询b_balance_detail表中是否存在扣钱记录，不存在就添加
                        Integer yongjin = getDetialByTradesIdAndApplyStatus(tradesId,2);
                        Integer tradesSource =  trades.getTradesSource();

                        if (yongjin == 0 && tradesSource != 130 && tradesSource != 140){
                            updateOrderPayCommission(siteId, tradesId, trades.getRealPay(), String.valueOf(trades.getTradesSource()), trades.getIsServiceOrder(), trades,trades.getPayTime());
                        }

                        Integer boolYunfei = balanceMapper.boolYunfei(tradesId);
                        if (boolYunfei == 1){
                            //添加运费记录
                            Integer yunfei = getDetialByTradesIdAndApplyStatus(tradesId,5);
                            if (yunfei == 0){
                                Map<String,Object> logisticsMap = balanceMapper.getLogisticsNameByTradesId(tradesId);
                                if (logisticsMap.containsKey("logistics_name") && !StringUtil.isEmpty(logisticsMap.get("logistics_name"))){
                                    String logisticsName = String.valueOf(logisticsMap.get("logistics_name"));
                                    Date create_time = (Date) logisticsMap.get("create_time");
                                    String deliveryType = null;
                                    if ("美团配送".equals(logisticsName)){
                                        deliveryType = "mt";
                                    }
                                    if ("蜂鸟配送".equals(logisticsName)){
                                        deliveryType = "ele";
                                    }
                                    updateO2OFreight(siteId,tradesId,0,deliveryType,create_time);
                                }
                            }
                        }
                        if (trades.getIsRefund() == 120 && tradesSource != 130 && tradesSource != 140){
                            //添加退佣金记录
                            Integer tuiyongjin = getDetialByTradesIdAndApplyStatus(tradesId,1);
                            if (tuiyongjin == 0){
                                Refund refund = refundMapper.getRefundByTradesId(tradesId);
                                Date refundTime = refund.getRefundTime();//用来作为记录的创建时间
                                fwRefund(trades,refund.getRealRefundMoney(),refundTime);
                            }
                        }



                    }
                }
            }

        }catch (Exception e){
            log.info("查询异常:{}", e);
        }
    }

    public Integer getDetialByTradesIdAndApplyStatus(Long tradesId,Integer applyStatus){
        return balanceMapper.getBalanceDetailByTradesIdAndApplyStatus(tradesId,applyStatus);
    }

    //佣金
    public void updateOrderPayCommission(int siteId, long tradesId, int orderRealPrice, String payType, int isServiceOrder,Trades trades,Date createTime) {
        String scene=trades.getTradesSource()+"";
        String deliveryType=trades.getPostStyle()+"";
        if (scene.equals("110") || scene.equals("120") || scene.equals("150") || scene.equals("160")){
            if (deliveryType.equals("150") || deliveryType.equals("160")){
                String payTypenew="100";
                Map baseFeeMap=baseFeeService.getBaseFeeByCode(siteId,  scene,  deliveryType,  payTypenew);
                int result = Integer.parseInt( baseFeeMap.get("result")+"");
                if(result!=0){
                    double baseFee = Double.parseDouble(baseFeeMap.get("feeRate")+"");
                    int feeRule = Integer.parseInt( baseFeeMap.get("feeRule")+"");
                    double payCommission = (double) orderRealPrice * (baseFee / 100);
                    //费用规则（0-按订单实付金额，不含运费；1-按订单实付金额，含运费）
                    if(feeRule==0){
                        payCommission = (double) (trades.getRealPay()-trades.getPostFee()) * (baseFee / 100);
                    }
                    //根据小数点后一位四舍五入
                    BigDecimal bd = new BigDecimal(payCommission);
                    //四舍五入后返回支付佣金(单位：分)
                    int payCommissionIntnew = bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                    //修改余额操作日志
                    insertBalanceDetail2(siteId,2,payCommissionIntnew,"订单佣金",tradesId,null,createTime);
                }
            }
        }

    }

    //退佣金
    public void fwRefund(Trades trades, int money,Date createTime){
        String scenes=trades.getTradesSource()+"";
        String deliveryType=trades.getPostStyle()+"";
        if (scenes.equals("110") || scenes.equals("120") || scenes.equals("150") || scenes.equals("160")){
            if (deliveryType.equals("150") || deliveryType.equals("160")) {
                String payTypenew = "100";
                Map baseFeeMap=baseFeeService.getBaseFeeByCode(trades.getSiteId(),  scenes,  deliveryType,  payTypenew);
                int result = Integer.parseInt( baseFeeMap.get("result")+"");
                if(result!=0){
                    int refuseRule = Integer.parseInt(baseFeeMap.get("refuseRule")+"");
                    if(refuseRule==1||refuseRule==2){
                        Integer payCommissionold= getMoneyByTradesId(trades.getTradesId());
                        //double baseFee = Double.parseDouble(baseFeeMap.get("feeRate")+"");
                        double payCommission = payCommissionold;
                        if(refuseRule==2){
                            payCommission = (double) money/trades.getRealPay() * payCommissionold;
                        }

                        //根据小数点后一位四舍五入
                        BigDecimal bd = new BigDecimal(payCommission);
                        //四舍五入后返回支付佣金(单位：分)
                        int payCommissionIntnew = bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                        //int payCommissionIntnew = balanceService.getOrderPayCommission(trades.getSiteId(), refund.getRealRefundMoney(), trades.getPayStyle());
                        //退款，退余额 修改余额操作日志
                        insertBalanceDetail2(trades.getSiteId(),1,payCommissionIntnew,"订单退款",trades.getTradesId(),null,createTime);
                    }
                }
            }
        }

    }

    //运费
    public void updateO2OFreight(int siteId, long tradesId, int orderRealPrice,String deliveryType,Date creatTime) {
        try {
            Trades trades = tradesMapper.getTradesByTradesId(tradesId);
            //修改余额操作日志
            int O2OCommissionIntnew=800;
            if("ele".equals(deliveryType)){
                O2OCommissionIntnew = tradesDeliveryService.getBasicPrice("ele", trades.getReceiverCity());
            }else if("mt".equals(deliveryType)){
                TradesExt tradesExt = tradesExtMapper.getByTradesId(trades.getTradesId());
                O2OCommissionIntnew = tradesDeliveryService.calcMtPrice(trades.getReceiverCity(), tradesExt.getDistance() / 1000);
            }
            insertBalanceDetail2(siteId,5,O2OCommissionIntnew,"订单运费",tradesId,null,creatTime);
        } catch (Exception e) {
            log.info("updateO2OFreight Exception:{}", e);
        }
    }

    @Transactional
    public Integer insertBalanceDetail2(Integer siteId,Integer applyStatus,Integer payChangePay,String description,Long tradesId,Integer msgStatus,Date createTime){
        try{

            if (!StringUtil.isEmpty(payChangePay)){
                payChangePay = Math.abs(payChangePay);//保持金额为正数
            }
            Balance balanceObj = balanceMapper.getBalanceBySiteId(siteId);//获取余额对象
            if (StringUtil.isEmpty(balanceObj) || 1 == balanceObj.getIsValid()){//判断是否是服务商商户
                return 0;
            }

            if (applyStatus == 2 || applyStatus == 5 || applyStatus == 1){//避免订单佣金记录的重复添加
                Integer k = balanceMapper.boolIExist(siteId,tradesId,applyStatus);
                if ( k > 0){
                    return 0;
                }
            }
            BalanceDetail balanceDetail = new BalanceDetail();
            balanceDetail.setSiteId(siteId);
            int balancePay = balanceObj.getBalance();//获取主表余额
            balanceDetail.setDescription(description);

            //获取操作类型
            if (applyStatus == 1){//退款（退还佣金）
                balanceDetail.setTradesId(tradesId);
                balancePay = balancePay + payChangePay;
                balanceDetail.setBalanceStatus(0);

            }else if (applyStatus == 2){//抽佣
                balanceDetail.setTradesId(tradesId);
                balancePay = balancePay - payChangePay;
                balanceDetail.setBalanceStatus(1);

            }else if (applyStatus == 5){//运费
                balanceDetail.setTradesId(tradesId);
                balancePay = balancePay - payChangePay;
                balanceDetail.setBalanceStatus(1);

            }

            //获取订单支付时间
            if (!StringUtil.isEmpty(tradesId)){
                Date date = balanceMapper.getPayTime(siteId,tradesId);
                if (!StringUtil.isEmpty(date) && !date.equals("null")){
                    balanceDetail.setPayTime(date);
                }
            }

            balanceObj.setBalance(balancePay);
            balanceDetail.setBalance(balancePay);
            balanceDetail.setPayChange(payChangePay);
            balanceDetail.setApplyStatus(applyStatus);
            balanceDetail.setSerialNum(getSerialNum(siteId));//流水号
//            balanceDetail.setCreateTime(createTime);

            Integer i = balanceMapper.insertBalanceDetail(balanceDetail);//添加日志
            if (i == 1 ){
                Integer j = balanceMapper.updateBalanceBySiteId(balanceObj);//日志添加成功，修改余额金额
                return j;
            }else {
                return 0;
            }
        }catch (Exception e){
            System.out.println("------------------------------------"+tradesId);
            log.error("日志添加异常:{}", e);
            return 0;
        }
    }

    //捋运费
    public void lvDistance(){
        //获取启用服务商模式的商户ID
        List<Integer> siteIdList = balanceMapper.getSiteIdList();
//        List<Integer> siteIdList = new ArrayList<>();
//        siteIdList.add(100190);
        for (Integer siteId : siteIdList){
            List<Map<String,Object>> listMap = balanceMapper.getYunfeiMap(siteId);
            if (listMap.size() > 0){
                int money = 0;
                for (Map<String,Object> detailMap : listMap){
                    Integer apply_status = Integer.parseInt(String.valueOf(detailMap.get("apply_status")));
                    Long trades_id = Long.parseLong(String.valueOf(detailMap.get("trades_id")));
                    int pay_change = Integer.parseInt(String.valueOf(detailMap.get("pay_change")));
                    int balance = Integer.parseInt(String.valueOf(detailMap.get("balance")));
                    if (apply_status == 5){//运费
                        int yunfei = getYunfeiFee(trades_id);
                        if (yunfei != pay_change){
                            int yunfeiYu = pay_change - yunfei;//运费差
                            money = money + yunfeiYu;
                        }
                        pay_change = yunfei;
                    }
                    Integer id = Integer.parseInt(String.valueOf(detailMap.get("id")));
                    balanceMapper.updateBalancePayChange(pay_change,id,balance + money);
                }
            }
        }

    }

    //获取运费
    public Integer getYunfeiFee(long tradesId){
        try{
            Map<String,Object> logisticsMap = balanceMapper.getLogisticsNameByTradesId(tradesId);
            String logisticsName = String.valueOf(logisticsMap.get("logistics_name"));
            Date date = (Date)logisticsMap.get("create_time");
            String deliveryType = null;
            if ("美团配送".equals(logisticsName)){
                deliveryType = "mt";
            }
            if ("蜂鸟配送".equals(logisticsName)){
                deliveryType = "ele";
            }

            Trades trades = tradesMapper.getTradesByTradesId(tradesId);

            int O2OCommissionIntnew=800;
            if("ele".equals(deliveryType)){
                O2OCommissionIntnew = tradesDeliveryService.getBasicPrice("ele", trades.getReceiverCity());
            }else if("mt".equals(deliveryType)){
                TradesExt tradesExt = tradesExtMapper.getByTradesId(trades.getTradesId());
                O2OCommissionIntnew = calcMtPrice(trades.getReceiverCity(), tradesExt.getDistance() / 1000,date);
            }
            return O2OCommissionIntnew;
        } catch (Exception e) {
            log.info("updateO2OFreight Exception:{}", e);
            return null;
        }
    }

    /**
     *
     * @param city 城市
     * @param distance 距离(单位km)
     * @return
     */
    public int calcMtPrice(String city, double distance,Date date){
        int i1 = tradesDeliveryService.getBasicPrice("mt", city);
        int i2 = priceMarkupTime(date);
        int i3 = tradesDeliveryService.priceMarkupDistance(distance);
        int i4 = 0;//priceMarkupWeight(0.5);
        log.error("mtPrice[base:{},time:{},distance:{},weight:{}]", i1, i2, i3, i4);
        return i1 + i2 + i3 + i4;
    }

    public int priceMarkupTime(Date date) {

        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
            Date now = null;
            Date beginTime = null;
            Date endTime = null;
            now = df.parse(df.format(date));

            //now = df.parse("21:00");

            beginTime = df.parse("00:00");
            endTime = df.parse("05:59");
            Boolean flag1 = TradesDeliveryService.belongCalendar(now, beginTime, endTime);
            if (flag1) return 200;

            beginTime = df.parse("11:00");
            endTime = df.parse("13:59");
            Boolean flag2 = TradesDeliveryService.belongCalendar(now, beginTime, endTime);
            if (flag2) return 200;

            beginTime = df.parse("21:00");
            endTime = df.parse("24:00");
            Boolean flag3 = TradesDeliveryService.belongCalendar(now, beginTime, endTime);
            if (flag3) return 200;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }


    //获取上个月的第一天和最后一天
    public String getFirstAndLastDay(int month,int day){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取前一个月第一天
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MONTH, month);
        calendar1.set(Calendar.DAY_OF_MONTH,day);
        String firstDay = sdf.format(calendar1.getTime());
        return firstDay;
    }

    //定时任务：执行上个月的余额结算
    public void oneMonth(){
        //获取启用服务商模式的商户ID
        List<Integer> siteIdList = balanceMapper.getSiteIdList();

        //获取上个月的第一天和最后一天
        String startTime = getFirstAndLastDay(-1,1);
        String endTime = getFirstAndLastDay(0,0);

        //获取上两个月的最后一天时间
        String thirdDay = getFirstAndLastDay(-1,0);

        //结算时间
        String nextDate = startTime + "/" + endTime;
        monthMethod(siteIdList,startTime+" 00:00:00",endTime+" 23:59:59",nextDate,"2018-05-01 00:00:00",thirdDay+" 23:59:59");

    }

    /**
     * 根据时间，结算指定某一天的
     *
     * @param siteId
     * @param startTime 上个月第一天
     * @param endTime   上个月最后一天
     * @param endTimeBefore 上上个月最后一天
     */
    public void oneMonthByTime(Integer siteId,String startTime,String endTime,String endTimeBefore){
        try{
            List<Integer> siteIdList;
            if (!StringUtil.isEmpty(siteId)){
                siteIdList = new ArrayList<>();
                siteIdList.add(siteId);
            }else {
                siteIdList = balanceMapper.getSiteIdList();
            }
            //结算时间
            String nextDate = startTime + "/" + endTime;

            monthMethod(siteIdList,startTime+" 00:00:00",endTime+" 23:59:59",nextDate,"2018-05-01 00:00:00",endTimeBefore+" 23:59:59");
        }catch (Exception e){
            log.info("日期转换异常:{}", e);
        }

    }

    //抽方法：月结算
    public void monthMethod(List<Integer> siteIdList,String startTime,String endTime,String nextDate,String startTimeBefore,String endTimeBefore){
        if (siteIdList.size() > 0){
            for (Integer siteId : siteIdList){
                BalanceAccountMonth balanceAccountMonth = balanceMapper.getYestodayDateMonth(siteId,startTime,endTime,startTimeBefore,endTimeBefore);//查询昨天的结算对象
                if (StringUtil.isEmpty(balanceAccountMonth)){
                    balanceAccountMonth = new BalanceAccountMonth();
                    balanceAccountMonth.setLastBalance(0);
                    balanceAccountMonth.setNowBalance(0);
                    balanceAccountMonth.setUpBalance(0);
                    balanceAccountMonth.setNowIncome(0);
                    balanceAccountMonth.setNowExpend(0);
                    balanceAccountMonth.setInvoiceMoney(0);
                    balanceAccountMonth.setInvoiceStatus(0);
                }
                balanceAccountMonth.setAccountTime(nextDate);
                balanceAccountMonth.setSiteId(siteId);

                //插入数据
                balanceMapper.insertBalanceAccountMonthBySiteId(balanceAccountMonth);
            }
        }
    }




    public Map<String,Object> getBalanceDetailOrder(Map<String, Object> params) {
        Map<String,Object> map = new HashMap<>();
        try{
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("pageNum")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> balanceList = balanceMapper.getBalanceDetailOrder(params);

            PageInfo<Map<String, Object>> allList = new PageInfo<>(balanceList);
            Trades t = null;
            for(Map<String,Object> map1 : allList.getList()){
                Trades trades = new Trades();
                trades.setSiteId(Integer.parseInt(params.get("siteId").toString()));
                trades.setTradesId(Long.parseLong(map1.get("trades_id").toString()));
                trades.setTradesStatus(Integer.parseInt(map1.get("trades_status").toString()));
                trades.setPostStyle(Integer.parseInt(map1.get("post_style").toString()));
//                trades.setCreateTime((Timestamp) map1.get("create_time"));
                trades.setIsRefund(Integer.parseInt(map1.get("is_refund").toString()));
                trades.setStockupStatus(Integer.parseInt(map1.get("stockup_status").toString()));
                trades.setDealFinishStatus(Integer.parseInt(map1.get("deal_finish_status").toString()));
                t = tradesConvertService.convert(trades,2,0,false);
                map1.put("trades_status",t.getPageShow().get("tradesStatus").toString());
            }
            map.put("allList", allList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    public List<Map<String,Object>> getBalanceDetailOrderReport(Map<String, Object> params) {
        Map<String,Object> map = new HashMap<>();
        try{
            List<Map<String,Object>> balanceList = balanceMapper.getBalanceDetailOrder(params);
            if(balanceList.size()>0){
                double total = 0;
                Trades t = null;
                for(Map<String,Object> map1 :balanceList){
                    Trades trades = new Trades();
                    trades.setSiteId(Integer.parseInt(params.get("siteId").toString()));
                    trades.setTradesId(Long.parseLong(map1.get("trades_id").toString()));
                    trades.setTradesStatus(Integer.parseInt(map1.get("trades_status").toString()));
                    trades.setPostStyle(Integer.parseInt(map1.get("post_style").toString()));
                    trades.setIsRefund(Integer.parseInt(map1.get("is_refund").toString()));
                    trades.setStockupStatus(Integer.parseInt(map1.get("stockup_status").toString()));
                    trades.setDealFinishStatus(Integer.parseInt(map1.get("deal_finish_status").toString()));
                    t = tradesConvertService.convert(trades,2,0,false);

                    map1.put("trades_status",t.getPageShow().get("tradesStatus").toString());
                    map1.put("real_pay",reDouble(Double.parseDouble(map1.get("real_pay").toString())/100));
                    map1.put("refund_fee",reDouble(Double.parseDouble(map1.get("refund_fee").toString())/100));
                    map1.put("commission",Double.parseDouble(map1.get("commission").toString())/100==0.00?"0.00":-Double.parseDouble(map1.get("commission").toString())/100);
                    map1.put("return_commission",Double.parseDouble(map1.get("returnCommission").toString())/100==0.0?"0.00":"+"+Double.parseDouble(map1.get("returnCommission").toString())/100);
                    map1.put("freight",Double.parseDouble(map1.get("freight").toString())/100==0.00?"0.00":-Double.parseDouble(map1.get("freight").toString())/100);
                    map1.put("return_freight",Double.parseDouble(map1.get("returnFreight").toString())/100==0.0?"0.00":"+"+Double.parseDouble(map1.get("returnFreight").toString())/100);
                    total =  Double.parseDouble(map1.get("return_commission").toString()) + Double.parseDouble(map1.get("commission").toString())
                        + Double.parseDouble(map1.get("return_freight").toString()) + Double.parseDouble(map1.get("freight").toString());
                    map1.put("total",total==0.0?"0.00":total>0.0?"+"+reDouble(total):reDouble(total));
                }
            }
            return balanceList;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            return null;
        }
    }

    /**
     * 保留两位小数
     * @param d
     * @return
     */
    public String reDouble(double d){
        return String.format("%.2f", d);
    }

    public void insertBalanceLog(){
        Map<String,Object> map = new HashMap<>();
        map.put("siteId",100190);
        map.put("applyStatus",2);
        map.put("payChangePay",20);
        map.put("description","订单佣金");
        map.put("tradesId",1001901528257023793L);
        balanceMapper.insertBalnceLog(map);
    }

    /**
     * 定时任务，每晚11点，定时检查有没有漏掉的订单佣金记录
     */
    public void executeDingshiJianLouAccount(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String time = sdf.format(date);
        String startTime = time + " 00:00:00";
        String endTime = time + " 23:30:00";
        List<Map<String,Object>> mapList = balanceMapper.getDingshiLou(startTime,endTime);
        if (mapList.size() > 0){
            for (Map<String,Object> map : mapList){
                Integer siteId = Integer.parseInt(String.valueOf(map.get("site_id")));
                Integer payChangePay = Integer.parseInt(String.valueOf(map.get("realPay")));
                Long tradesId = Long.parseLong(String.valueOf(map.get("trades_id")));
                insertBalanceDetail(siteId,2,payChangePay,"订单佣金",tradesId,null,null);
            }
        }
    }


    /**
     * 账单详情
     * @param id
     * @param siteId
     * @return
     */
    public Map<String,Object> getAccountBalanceStatementDetail(Integer id, Integer siteId) {
        Map<String,Object> map = new HashMap<>();
        try{
            //账单数据
            Map<String,Object> billMap = balanceMapper.getBillMap(id,siteId);
            String account_time = String.valueOf(billMap.get("account_time"));
            if (StringUtil.isEmpty(account_time)){
                billMap.put("account_time","----");
            }else {
                billMap.put("account_time",account_time.replace("/"," 到 "));
                String accountMonth = account_time.substring(5,7);
                if ("0".equals(accountMonth.substring(0,1))){
                    accountMonth = accountMonth.substring(1,2);
                }
                billMap.put("account_month",accountMonth + "月账单");
            }

            //账户数据
            Map<String,Object> accountMap = balanceMapper.getAccountMap(siteId);

            map.put("billMap", billMap);
            map.put("accountMap", accountMap);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }
    /**
     * 自助充值
     * 7微信充值  8支付宝充值
     * @return
     */
    @Transactional
    public Map<String,Object> accountTopup(Map<String,Object> params) {
        Map<String,Object> re = new HashMap<>();
        try {
            //先生成一条充值记录,未结算状态，当支付接口成功时再修改为已结算
            Integer siteId = Integer.parseInt(params.get("siteId").toString());
            Float total = Float.parseFloat(params.get("total").toString());//充值金额（元）
            Integer type = Integer.parseInt(params.get("type").toString());//类型
            String description="";
            if(params.get("type")!=null && Integer.parseInt(params.get("type").toString())==7){
                description = "微信充值";
            }else if (params.get("type")!=null && Integer.parseInt(params.get("type").toString())==8){
                description = "支付宝充值";
            }else {
                re.put("status","error");
                return re;
            }
            Integer id = balanceMapper.getId(Integer.parseInt(params.get("siteId").toString()));
            Balance balanceObj = balanceMapper.getBalanceBySiteId2(id);//获取余额对象
            if (StringUtil.isEmpty(balanceObj)){//判断是否是服务商商户
                re.put("status","error");
                return re;
            }
            re.put("isValid",balanceObj.getIsValid());
            if(balanceObj.getIsValid()==1){
                re.put("status","ok");
                re.put("qrcode","qrcode");
                return re;
            }

            BalanceDetail balanceDetail = new BalanceDetail();
            balanceDetail.setSiteId(siteId);
            balanceDetail.setStoreadminId(Integer.parseInt(params.get("userId").toString()));
            balanceDetail.setBalanceStatus(0);//0收入
            balanceDetail.setAuditStatus(1);//0已审核  1未审核
            balanceDetail.setSerialNum(getSerialNum(siteId));//流水号
            balanceDetail.setDescription(description);
            balanceDetail.setApplyStatus(type);
            balanceDetail.setPayChange((int) (total*100));//单位分
            balanceDetail.setBalance(balanceObj.getBalance());//当支付成功时需更改为balance+payChange,同时需要修改主表余额
            Integer x = balanceMapper.insertBalanceDetail(balanceDetail);//添加日志
            log.info("预充值记录："+x);
            if(x==1){
                if(Integer.parseInt(params.get("type").toString())==7){
                    //微信支付
                    String result = payService.wxCreateNativeOrder(siteId,balanceDetail.getSerialNum(), (int) (total*100),"51jk充值");
                    log.info("微信充值二维码："+result);
                    re.put("qrcode",result);
                    re.put("serialNum",balanceDetail.getSerialNum());
                    return re;
                }else {
                    //支付宝支付
                    AliRequestParam aliRequestParam = new AliRequestParam();
                    aliRequestParam.setScene("51jk充值");
                    aliRequestParam.setSubject("51jk充值");
                    aliRequestParam.setTotal_amount(total);
                    aliRequestParam.setOut_trade_no(balanceDetail.getSerialNum());
                    AlipayTradePrecreateResponse response = aliPayApi.precreate(aliRequestParam);
                    log.info("支付宝充值二维码："+response.getQrCode());
                    re.put("qrcode",response.getQrCode());
                    re.put("serialNum",balanceDetail.getSerialNum());
                    return re;
                }
            }
            re.put("status","error");
            return re;
        }catch (PayException payException){
            log.error("获取微信充值二维码异常：{}",payException);
            re.put("status","error");
            throw new RuntimeException(payException.getMessage());
        } catch (Exception e) {
            log.error("获取支付宝充值二维码异常：{}",e);
            re.put("status","error");
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 支付成功后回调函数
     * @param siteId
     * @param serialNum      51支付流水号
     * @param type           充值类型  7微信  8支付宝
     * @param thirdSerialNum 第三方流水号
     * @return
     */
    @Transactional
    public Integer updBalanceCallback(Integer siteId,String serialNum,Integer type,String thirdSerialNum) {
        Integer id = balanceMapper.getId(siteId);
        Balance balanceObj = balanceMapper.getBalanceBySiteId2(id);//获取余额对象
        BalanceDetail balanceDetail = balanceMapper.getBalanceDetailBySerialnum(siteId,serialNum);
        balanceDetail.setBalance(balanceDetail.getPayChange()+balanceObj.getBalance());
        balanceDetail.setAuditStatus(0);
        balanceDetail.setThirdSerialNum(thirdSerialNum);
        Integer x = balanceMapper.updBalanceDetail(balanceDetail);
        log.info("第三方支付回调函数=="+type);
        if(x==1){
            balanceObj.setBalance(balanceDetail.getPayChange()+balanceObj.getBalance());
            x = balanceMapper.updateBalanceBySiteId(balanceObj);
        }

        return x;
    }

    public PageInfo getTopupRecord(Integer siteId,Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Map> lst = balanceMapper.getTopupRecord(siteId);
        return new PageInfo(lst);
    }

    public Map getTopupRecordBySerialNum(Integer siteId, String serialNum) {
        try {
            return JacksonUtils.json2map(JacksonUtils.obj2json(balanceMapper.getBalanceDetailBySerialnum(siteId,serialNum)));
        } catch (Exception e) {
            log.error("获取单个充值记录异常{}",e);
            e.printStackTrace();
            return null;
        }
    }
}
