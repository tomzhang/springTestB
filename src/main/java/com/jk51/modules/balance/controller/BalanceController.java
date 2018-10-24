package com.jk51.modules.balance.controller;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.balance.Balance;
import com.jk51.model.balance.BalanceDetail;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.modules.pay.service.PayService;
import com.jk51.modules.trades.service.TradesDeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/26.
 */
@Controller
@RequestMapping("/balance")
public class BalanceController {
    public static final Logger log = LoggerFactory.getLogger(BalanceController.class);
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private PayService payService;

    /**
     * 根据ID查询商户下的余额配置信息
     * @param siteId
     * @return
     */
    @RequestMapping(value="/getBalanceBySiteId")
    @ResponseBody
    public Balance getBalanceBySiteId(Integer siteId) {
        return balanceService.getBalanceBySiteId(siteId);
    }

    @RequestMapping(value="/editBalanceBySiteId")
    @ResponseBody
    public ReturnDto editBalanceBySiteId(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        try {
            Balance balance = JacksonUtils.json2pojo(JacksonUtils.mapToJson(param), Balance.class);
            return balanceService.editBalanceBySiteId(balance);
        } catch (Exception e) {
            log.error("编辑余额失败，对象转换异常：{}",e);
            return ReturnDto.buildFailedReturnDto("修改失败");
        }

    }

    @RequestMapping(value="/upBalance")
    @ResponseBody
    public ReturnDto upBalance(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        try {
            BalanceDetail balanceDetail = JacksonUtils.json2pojo(JacksonUtils.mapToJson(param), BalanceDetail.class);
            Integer siteId = balanceDetail.getSiteId();
            Integer payChange = balanceDetail.getPayChange();
            String description = balanceDetail.getDescription();
            Integer i = balanceService.insertBalanceDetail(siteId,0,payChange,description,null,null,null);
            if (i == 1){
                return ReturnDto.buildSuccessReturnDto();
            }else {
                return ReturnDto.buildFailedReturnDto("修改失败");
            }
        } catch (Exception e) {
            log.error("编辑余额失败，对象转换异常：{}",e);
            return ReturnDto.buildFailedReturnDto("修改失败");
        }

    }



    @RequestMapping(value="/insertBalanceDetail")
    @ResponseBody
    public Integer insertBalanceDetail(HttpServletRequest request) {
        try {
            Integer siteId = 100190;
            Integer payChange = 50;
            Integer applyStatus = 0;
            Long tradesId = 2457123156465232124L;
            String s = balanceService.boolIsProvider(100190);
            if (s.equals("YES")){
                String s1 = balanceService.boolBalanceIsValid(100190, 10,"");
                if (s1.equals("YES")){
                    return balanceService.insertBalanceDetail(100190,4,10,null,null,900,null);
                }
            }
            return 0;
//            return balanceService.insertBalanceDetail(100190,5,10000,null,2457123156465232124L);
        } catch (Exception e) {
            log.error("编辑余额失败，对象转换异常：{}",e);
            return 0;
        }

    }

    @RequestMapping(value="/getBalanceDetail")
    @ResponseBody
    public Map<String,Object> getBalanceDetail(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getBalanceDetail(param);
    }

    @RequestMapping(value="/getBalanceDetailOrder")
    @ResponseBody
    public Map<String,Object> getBalanceDetailOrder(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getBalanceDetailOrder(param);
    }

    @RequestMapping(value="/boolBalanceIsValid")
    @ResponseBody
    public String boolBalanceIsValid(Integer siteId,Integer commission,String payType) {
        try {
            return balanceService.boolBalanceIsValid(siteId,commission, payType);
        } catch (Exception e) {
            log.error("编辑余额失败，对象转换异常：{}",e);
            return "Error";
        }

    }
    @RequestMapping(value="/boolIsProvider")
    @ResponseBody
    public String boolIsProvider(Integer siteId) {
        try {
            return balanceService.boolIsProvider(siteId);
        } catch (Exception e) {
            log.error("编辑余额失败，对象转换异常：{}",e);
            return "Error";
        }

    }

    @RequestMapping(value="/getWarning")
    @ResponseBody
    public Integer getWarning(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getWarning(param);
    }

    @RequestMapping(value="/getBalanceAccountBySiteId")
    @ResponseBody
    public Map<String,Object> getBalanceAccountBySiteId(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getBalanceAccountBySiteId(param);
    }

    @RequestMapping(value="/getAccountBalanceBySiteId")
    @ResponseBody
    public Map<String,Object> getAccountBalanceBySiteId(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getAccountBalanceBySiteId(param);
    }

    @RequestMapping(value="/getAccountBalance")
    @ResponseBody
    public Map<String,Object> getAccountBalance(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getAccountBalance(param);
    }

    @RequestMapping(value="/getAccountBalanceSiteDetail")
    @ResponseBody
    public Map<String,Object> getAccountBalanceSiteDetail(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getAccountBalanceSiteDetail(param);
    }

    @RequestMapping(value="/updateHqRemark")
    @ResponseBody
    public Map<String,Object> updateHqRemark(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.updateHqRemark(param);
    }


    @RequestMapping(value="/balanceTiming")
    @ResponseBody
    public void balanceTiming(HttpServletRequest request) {
        balanceService.balanceTiming();
    }


    @RequestMapping(value="/getBalanceSMSLst")
    public @ResponseBody Map<String,Object> getBalanceSMSLst(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getBalanceSMSLst(param);
    }

    @RequestMapping(value="/getUpBalanceById")
    public @ResponseBody Map<String,Object> getUpBalanceById(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getUpBalanceById(param);
    }

    @RequestMapping(value="/getBalanceAccountForSite")
    public @ResponseBody Map<String,Object> getBalanceAccountForSite(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getBalanceAccountForSite(param);
    }

    @RequestMapping(value="/getAccountInfo")
    public @ResponseBody Map<String,Object> getAccountInfo(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getAccountInfo(param);
    }

    @RequestMapping(value="/updateAccountInfo")
    public @ResponseBody Map<String,Object> updateAccountInfo(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.updateAccountInfo(param);
    }

    @RequestMapping(value="/getTradesBrokerage")
    public @ResponseBody Map<String,Object> getTradesBrokerage(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getTradesBrokerage(param);
    }

    @RequestMapping(value="/getOtherDetail")
    public @ResponseBody Map<String,Object> getOtherDetail(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getOtherDetail(param);
    }

    @RequestMapping(value="/getSettlementDetail")
    @ResponseBody
    public Object getSettlementDetail(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getSettlementDetail(param);
    }

    @RequestMapping(value="/oneDay")
    @ResponseBody
    public void oneDay(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String time = String.valueOf(param.get("time"));
        Integer siteId = null;
        if (!StringUtil.isEmpty(param.get("siteId"))){
            siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        }
        balanceService.oneDay(time,siteId);
    }

    @RequestMapping(value="/perfectionData")
    @ResponseBody
    public void perfectionData(HttpServletRequest request) {
        balanceService.perfectionData();
    }

    @RequestMapping(value="/duiZhang")
    @ResponseBody
    public void duiZhang(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String startTime = String.valueOf(param.get("startTime"));
        String endTime = String.valueOf(param.get("endTime"));
        Integer siteId = null;
        if (!StringUtil.isEmpty(param.get("siteId"))){
            siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        }
        balanceService.duiZhang(siteId,startTime,endTime);
    }

    @RequestMapping(value="/oneMonth")
    @ResponseBody
    /**
     * startTime ：结算月份的第一天
     * endTime  ：结算月份的最后一天
     * endTimeBefore    ：结算月份前一个月的最后一天
     * siteId
     */
    public void oneMonth(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String startTime = String.valueOf(param.get("startTime"));
        String endTime = String.valueOf(param.get("endTime"));
        String endTimeBefore = String.valueOf(param.get("endTimeBefore"));
        Integer siteId = null;
        if (!StringUtil.isEmpty(param.get("siteId"))){
            siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        }
        balanceService.oneMonthByTime(siteId,startTime,endTime,endTimeBefore);
    }

    @RequestMapping(value="/getBalanceAccountForSiteMoth")
    public @ResponseBody Map<String,Object> getBalanceAccountForSiteMoth(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getBalanceAccountForSiteMoth(param);
    }

    @RequestMapping(value="/updateAuditStatus")
    public @ResponseBody Map<String,Object> updateAuditStatus(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.updateAuditStatus(param);
    }

    @RequestMapping(value="/getBalanceAccountBySiteIdMonth")
    @ResponseBody
    public Map<String,Object> getBalanceAccountBySiteIdMonth(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.getBalanceAccountBySiteIdMonth(param);
    }

    @RequestMapping(value="/updateAuditStatusMonth")
    @ResponseBody
    public Map<String,Object> updateAuditStatusMonth(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        return balanceService.updateAuditStatusMonth(param);
    }

    /**
     * 初始化：纠正订单
     */
    @RequestMapping(value="/correctTrades")
    @ResponseBody
    public Integer correctTrades(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer applyStatus = Integer.parseInt(String.valueOf(param.get("applyStatus")));
        Integer payChangePay = Integer.parseInt(String.valueOf(param.get("payChangePay")));
        String description = String.valueOf(param.get("description"));
        Long tradesId = Long.parseLong(String.valueOf(param.get("tradesId")));
        return balanceService.insertBalanceDetail(siteId,applyStatus,payChangePay,description,tradesId,null,null);
    }
    @RequestMapping(value="/lvDistance")
    @ResponseBody
    public void lvDistance(HttpServletRequest request) {
        balanceService.lvDistance();
    }

    @Autowired
    TradesDeliveryService tradesDeliveryService;
    @RequestMapping(value="/getMoney")
    @ResponseBody
    public String getMoney(HttpServletRequest request) {
        int i = tradesDeliveryService.getBasicPrice("ele", "无锡市");
        int j = tradesDeliveryService.calcMtPrice("无锡市", 562 / 1000);
        return i + "----" + j;
    }

    @RequestMapping(value="/getYunfeiFee")
    @ResponseBody
    public Map<String,Object> getYunfeiFee(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Long tradesId = Long.parseLong(String.valueOf(param.get("tradesId")));
        Integer yunfeiFee = balanceService.getYunfeiFee(tradesId);
        return new HashMap(){{put("yunfeiFee",yunfeiFee);}};
    }

    @RequestMapping(value="/getAccountBalanceStatementDetail")
    @ResponseBody
    public Map<String,Object> getAccountBalanceStatementDetail(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        return balanceService.getAccountBalanceStatementDetail(id,siteId);
    }

    /**
     * 自助充值
     * 7微信  8支付宝
     * 金额
     * @return
     */
    @RequestMapping(value="/accountTopup")
    public @ResponseBody Map<String,Object> accountTopup(HttpServletRequest request){
        Map<String,Object> params = ParameterUtil.getParameterMap(request);

        return balanceService.accountTopup(params);
    }


    /**
     * 充值记录
     * @param siteId
     * @return
     */
    @RequestMapping(value="/getTopupRecord")
    public @ResponseBody PageInfo getTopupRecord(Integer siteId, Integer pageNum, Integer pageSize){
        return balanceService.getTopupRecord(siteId,pageNum,pageSize);
    }

    @RequestMapping(value="/getTopupRecordBySerialNum")
    public @ResponseBody Map getTopupRecordBySerialNum(Integer siteId, String serialNum){
        return balanceService.getTopupRecordBySerialNum(siteId,serialNum);
    }

}
