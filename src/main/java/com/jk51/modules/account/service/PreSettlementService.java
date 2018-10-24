package com.jk51.modules.account.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.account.requestParams.AccountParams;
import com.jk51.model.account.requestParams.PreSettlementParam;
import com.jk51.model.order.Refund;
import com.jk51.modules.account.controller.PreSettlementController;
import com.jk51.modules.account.mapper.SettlementDetailAndTradesMapper;
import com.jk51.modules.trades.mapper.RefundMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 预结算列表
 * 作者: chen_pt
 * 创建日期: 2017/7/28
 * 修改记录:
 */
@Service
public class PreSettlementService {

    private static final Logger logger = LoggerFactory.getLogger(PreSettlementController.class);

    @Autowired private RefundMapper refundMapper;
    @Autowired private SettlementDetailAndTradesMapper settlementDetailAndTradesMapper;




    public List<Map<String, Object>> getPreSettlementLst(PreSettlementParam parm) {

        List<Map<String, Object>> list = settlementDetailAndTradesMapper.getPreSettlementLst(parm);
        if(list.size()>0){
            for (Map<String, Object> m : list) {

                Object pay_style=m.get("pay_style");
                //查询退款表退款金额
                Refund refund=refundMapper.getRefundByTradeId(Integer.parseInt(m.get("site_id").toString()),m.get("trades_id").toString());
                //判断支付类型，线下类型没有退款金额
                if (null==pay_style||"cash".equals(pay_style.toString())||"health_insurance".equals(pay_style.toString())){
                    m.put("real_refund_money",0);
                }else {
                    m.put("real_refund_money",refund==null?0:refund.getRealRefundMoney());
                }
                m.put("trades_id",m.get("trades_id").toString());

                //处理实际出账日 实际付款日
                if(m.get("pay_day")==null || m.get("pay_day").equals("")){
                    m.put("pay_day","---");
                }
                if(m.get("pay_date")==null || m.get("pay_date").equals("")){
                    m.put("pay_date","---");
                }


                //处理合同出账日，合同付款日
                String pactBillDay = "---";//合同出账日
                String pactPayDay = "---";//合同付款日

                String setType = m.get("set_type").toString();
                if(setType!=null && setType!=""){
                    String createTimeStr = m.get("create_times").toString();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date createTimes = null;
                    try {
                        createTimes = format.parse(createTimeStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar calendarTime = Calendar.getInstance();//下单时间
                    calendarTime.setTime(createTimes);

                    String day = m.get("set_value").toString();//周结时：周几   月结时：每月几号
                    int year = calendarTime.get(Calendar.YEAR);
                    int month = calendarTime.get(Calendar.MONTH)+1;
                    int day2 = Integer.parseInt(day);

                    if(Integer.parseInt(setType)==0){//日结
                        pactBillDay = year + "-" + month + "-" + day;
                        pactPayDay = year + "-" + month + "-" + day;
                    }else if(Integer.parseInt(setType)==1){//周结
                        pactBillDay = getWeekDateByDate(createTimeStr,day2);
                        pactPayDay = getDateAfterNDays(pactBillDay,3);
                    }else if(Integer.parseInt(setType)==2){//月结

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month-1, day2);
                        Long t1 = calendarTime.getTimeInMillis();//下单时间
                        Long t2 = calendar.getTimeInMillis();//本月账单时间
                        if(t1<t2){
                            pactBillDay = year + "-" + month + "-" + day;
                        }else{
                            if(month==12){
                                pactBillDay = (year+1) + "-" + 1 + "-" + day;
                            }else {
                                pactBillDay = year + "-" + (month+1) + "-" + day;
                            }
                        }
                        pactPayDay = getDateAfterNDays(pactBillDay,3);
                    }
                }
                m.put("pactBillDay",pactBillDay);
                m.put("pactPayDay",pactPayDay);
            }

        }

        return list;
    }


    /**
     * 导出报表
     * @param params
     * @return
     */
    public List<Map<String, Object>> getPreSettlementLstExport(Map<String, Object> params) {
        PreSettlementParam preSettlementParam = new PreSettlementParam();
        try {
            preSettlementParam = JacksonUtils.map2pojo(params, PreSettlementParam.class);
        } catch (Exception e) {
            logger.error("导出财务预算" + e);
        }
        List<Map<String, Object>> preSettlementLst = settlementDetailAndTradesMapper.getPreSettlementLst(preSettlementParam);
        return getYingshouXiaoji(preSettlementLst);
    }

    private List<Map<String, Object>> getYingshouXiaoji(List<Map<String, Object>> paramMap) {
        if (null != paramMap && paramMap.size() > 0) {
            for (Map<String, Object> m : paramMap) {
                BigDecimal yingshouxiaoji = null;
                DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                dfs.setDecimalSeparator('.');
                dfs.setGroupingSeparator(',');
                dfs.setMonetaryDecimalSeparator('.');
                DecimalFormat df = new DecimalFormat("###,###.##", dfs);
                String lastRealPay = "---";
                Object trades_id = m.get("trades_id");
                Object daishoujine = m.get("real_pays");
                Number parse = 0;
                try {
                    parse = df.parse(daishoujine.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Object daituijine = m.get("real_refund_money");
                Object daishoushouxufei = m.get("shoufei");
                Object jiaoyiyongjin = m.get("jiaoyiyongjin");
                Object daishoupeisongfei = m.get("daishoupeisongfei");
                if (null != daishoujine && StringUtils.equalsIgnoreCase((daishoujine == null ? 0 : daishoujine).toString(), "---")) {
                    lastRealPay = "---";
                }
                if (null != daituijine && !StringUtils.equalsIgnoreCase((daituijine == null ? 0 : daituijine).toString(), "---")) {
                    yingshouxiaoji = new BigDecimal(parse.toString()).subtract(new BigDecimal((daituijine == null ? 0 : daituijine).toString()));
                    lastRealPay = yingshouxiaoji.toString();
                }

                if (null != daishoushouxufei && !StringUtils.equalsIgnoreCase((daishoushouxufei == null ? 0 : daishoujine).toString(), "---")
                        && !StringUtils.equalsIgnoreCase(lastRealPay.toString(), "---")) {
                    yingshouxiaoji = new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal((daishoushouxufei == null ? 0 : daishoushouxufei).toString()));
                    lastRealPay = yingshouxiaoji.toString();
                }

                if (null != jiaoyiyongjin && !StringUtils.equalsIgnoreCase((jiaoyiyongjin == null ? 0 : jiaoyiyongjin).toString(), "---")
                        && !StringUtils.equalsIgnoreCase(lastRealPay.toString(), "---")) {
                    yingshouxiaoji = new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal((jiaoyiyongjin == null ? 0 : jiaoyiyongjin).toString()));
                    lastRealPay = yingshouxiaoji.toString();
                }

                if (null != daishoupeisongfei && !StringUtils.equalsIgnoreCase((daishoupeisongfei == null ? 0 : daishoupeisongfei).toString(), "---")
                        && !StringUtils.equalsIgnoreCase(lastRealPay.toString(), "---")) {
                    yingshouxiaoji = new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal((daishoupeisongfei == null ? 0 : daishoupeisongfei).toString()));
                    lastRealPay = yingshouxiaoji.toString();
                }
                m.put("trades_id", trades_id + "");
                m.put("lastrealpay", lastRealPay + "");


                //处理实际出账日 实际付款日
                if(m.get("pay_day")==null || m.get("pay_day").equals("")){
                    m.put("pay_day","---");
                }
                if(m.get("pay_date")==null || m.get("pay_date").equals("")){
                    m.put("pay_date","---");
                }


                //处理合同出账日，合同付款日
                String pactBillDay = "---";//合同出账日
                String pactPayDay = "---";//合同付款日

                String setType = m.get("set_type").toString();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                if(setType!=null && setType!=""){
                    String createTimeStr = m.get("create_times").toString();
                    Date createTimes = null;
                    try {
                        createTimes = format.parse(createTimeStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar calendarTime = Calendar.getInstance();//下单时间
                    calendarTime.setTime(createTimes);

                    String day = m.get("set_value").toString();//周结时：周几   月结时：每月几号
                    int year = calendarTime.get(Calendar.YEAR);
                    int month = calendarTime.get(Calendar.MONTH)+1;
                    int day2 = Integer.parseInt(day);

                    if(Integer.parseInt(setType)==0){//日结
                        pactBillDay = year + "-" + month + "-" + day;
                        pactPayDay = year + "-" + month + "-" + day;
                    }else if(Integer.parseInt(setType)==1){//周结
                        pactBillDay = getWeekDateByDate(createTimeStr,day2);
                        pactPayDay = getDateAfterNDays(pactBillDay,3);
                    }else if(Integer.parseInt(setType)==2){//月结

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month-1, day2);
                        Long t1 = calendarTime.getTimeInMillis();//下单时间
                        Long t2 = calendar.getTimeInMillis();//本月账单时间
                        if(t1<t2){
                            pactBillDay = year + "-" + month + "-" + day;
                        }else{
                            if(month==12){
                                pactBillDay = (year+1) + "-" + 1 + "-" + day;
                            }else{
                                pactBillDay = year + "-" + (month+1) + "-" + day;
                            }
                        }
                        pactPayDay = getDateAfterNDays(pactBillDay,3);
                    }
                }
                m.put("pactbillday",pactBillDay);
                m.put("pactpayday",pactPayDay);

                Object financeNo = m.get("financeNos");
                if(financeNo==null){
                    m.put("financeno","---");
                }

                m.put("financeno",financeNo.toString());
            }
        }
        return paramMap;
    }







    /**
     * 获取给定日期N天后的日期
     * @param dateTime   给定的日期
     * @param days       往后推算的天数  N
     * @return
     */
    public static String getDateAfterNDays(String dateTime, int days) {
        Calendar calendar = Calendar.getInstance();
        String[] dateTimeArray = dateTime.split("-");
        int year = Integer.parseInt(dateTimeArray[0]);
        int month = Integer.parseInt(dateTimeArray[1]);
        int day = Integer.parseInt(dateTimeArray[2]);
        calendar.set(year, month - 1, day);
        long time = calendar.getTimeInMillis();// 给定时间与1970 年 1 月 1 日的00:00:00.000的差，以毫秒显示
        calendar.setTimeInMillis(time + days * 1000 * 60 * 60 * 24);// 用给定的 long值设置此Calendar的当前时间值
        return calendar.get(Calendar.YEAR)// 计算后的时间——年
                + "-" + (calendar.get(Calendar.MONTH) + 1)// 计算后的时间——月
                + "-" + calendar.get(Calendar.DAY_OF_MONTH)// 计算后的时间——日
                ;
    }

    /**
     * 根据给定日期获取其是周几
     * @param date
     * @return
     */
    public static int getWeek(Date date){
        int[] weeks = {7,1,2,3,4,5,6};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index<0){
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 根据已知的一个固定日期-->获取给定周几的日期
     * @param date   //给定日期
     * @param day    //周几
     */
    public static String getWeekDateByDate(String date, int day){
        String dDate = "---";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date2 = format.parse(date);
            int weekDay = getWeek(date2);
            System.out.println("给定日期是周:"+weekDay);
            //计算两数之间的差
            int dValue = day-weekDay;
            dDate = getDateAfterNDays(format.format(date2),dValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dDate;
    }

}
