package com.jk51.modules.account.service;


import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.account.models.ClassifiedStatistic;
import com.jk51.model.account.models.FinancesStatistic;
import com.jk51.model.account.models.SettlementDetailAndTrades;
import com.jk51.model.account.requestParams.AccountParams;
import com.jk51.model.account.requestParams.ClassifiedAccountParam;
import com.jk51.model.order.Refund;
import com.jk51.modules.account.mapper.ClassifiedStatisticMapper;
import com.jk51.modules.account.mapper.SettlementDetailAndTradesMapper;
import com.jk51.modules.trades.mapper.RefundMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Map;


/**
 * filename :com.jk51.modules.account.goodsService.
 * author   :zw
 * date     :2017/2/17
 * Update   :
 */
@Service
public class SettlementDetailAndTradesService {
    @Autowired
    private SettlementDetailAndTradesMapper settlementDetailAndTradesMapper;
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private ClassifiedStatisticMapper classifiedStatisticMapper;

    public List<SettlementDetailAndTrades> getSettlementListByTradesId(Integer seller_id, Timestamp startDate, Timestamp endDate){
        return settlementDetailAndTradesMapper.getSettlementListByTradesId(seller_id,startDate,endDate);
    }
    public Refund getRefundListByTradesId(String trade_id){
        return refundMapper.getRefundListByTradesId(trade_id);
    }

//    public List<Map<String,Object>> queryGetSettlementListByObjExport(Map<String,Object> params){
//        JSONObject json=new JSONObject(params);
//        AccountParams accountParams=JSONObject.parseObject(json.toString(),AccountParams.class);
//        return getYingshouXiaoji(settlementDetailAndTradesMapper.getSettlementListByObj(accountParams));
//    };


//    public List<Map<String, Object>> getStoreSettlementListByObjs(AccountParams accountParams) throws Exception {
//        return settlementDetailAndTradesMapper.getStoreSettlementListByObjs(JacksonUtils.json2map(JacksonUtils.obj2json(accountParams)));
//    }

    /*账单汇总导出注入bean*/
    public List<Map<String,Object>> getClassifiedList(Map<String,Object> params){
        JSONObject json=new JSONObject(params);
        ClassifiedAccountParam accountParams=JSONObject.parseObject(json.toString(),ClassifiedAccountParam.class);
        return xiaoji(classifiedStatisticMapper.getClassified(accountParams));
    };

    public List<Map<String,Object>> queryGetClerkSettlementListByObjExport(Map<String,Object> params){
        JSONObject json=new JSONObject(params);
        AccountParams accountParams=JSONObject.parseObject(json.toString(),AccountParams.class);
        return getYingshouXiaoji2(settlementDetailAndTradesMapper.getClerkSettlementListByObj(accountParams));
    };

//    public List<Map<String,Object>> getClerkSettlementListByObjs(AccountParams accountParams)throws Exception{
//        return settlementDetailAndTradesMapper.getClerkSettlementListByObjs(JacksonUtils.json2map(JacksonUtils.obj2json(accountParams)));
//    };
    private  List<Map<String,Object>> getYingshouXiaoji2( List<Map<String,Object>> paramMap){
        if (null != paramMap && paramMap.size() > 0) {
            paramMap.stream().forEach(l -> {
                Map<String, Object> m = (Map<String, Object>) l;
                BigDecimal yingshouxiaoji = null;
                DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                dfs.setDecimalSeparator('.');
                dfs.setGroupingSeparator(',');
                dfs.setMonetaryDecimalSeparator('.');
                DecimalFormat df = new DecimalFormat("###,###.##", dfs);
                String lastRealPay = "---";
                Object trades_id = m.get("trades_id");
                Object daishoujine = m.get("real_pay_intro");
                Object daituijine=m.get("tuikuanjine1");
                Object daishoushouxufei=m.get("platsplit1");
                Object jiaoyiyongjin=m.get("jiaoyiyongjin");
                Object daishoupeisongfei=m.get("daishoupeisongfei");
                Number parse = 0;
                Number parse1 = 0;
                Number parse2 = 0;
                Number parse3 = 0;
                Number parse4 = 0;
                try {
                    parse = df.parse(daishoujine.toString());
                    parse1 = df.parse(daituijine.toString());
                    parse2 = df.parse(daishoushouxufei.toString());
                    parse3 = df.parse(jiaoyiyongjin.toString());
                    parse4 = df.parse(daishoupeisongfei.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(null!=daishoujine&& StringUtils.equalsIgnoreCase((parse==null?0:parse).toString(),"---")){
                    lastRealPay="---";
                }
                if(null!=daituijine&&!StringUtils.equalsIgnoreCase((daituijine==null?0:daituijine).toString(),"---")){
                    yingshouxiaoji=new BigDecimal(parse.toString()).subtract(new BigDecimal((parse1==null?0:parse1).toString()));
                    lastRealPay=yingshouxiaoji.toString();
                }

                if(null!=daishoushouxufei&&!StringUtils.equalsIgnoreCase( (daishoushouxufei==null?0:daishoujine).toString(),"---")
                        &&!StringUtils.equalsIgnoreCase(lastRealPay.toString(),"---")){
                    yingshouxiaoji=new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal((parse2==null?0:daishoushouxufei).toString()));
                    lastRealPay=yingshouxiaoji.toString();
                }

                if(null!=jiaoyiyongjin&&!StringUtils.equalsIgnoreCase((jiaoyiyongjin==null?0:jiaoyiyongjin).toString(),"---")
                        &&!StringUtils.equalsIgnoreCase(lastRealPay.toString(),"---")){
                    yingshouxiaoji=new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal((parse3==null?0:jiaoyiyongjin).toString()));
                    lastRealPay=yingshouxiaoji.toString();
                }

                if(null!=daishoupeisongfei&&!StringUtils.equalsIgnoreCase((daishoupeisongfei==null?0:daishoupeisongfei).toString(),"---")
                        &&!StringUtils.equalsIgnoreCase(lastRealPay.toString(),"---")){
                    yingshouxiaoji=new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal((parse4==null?0:daishoupeisongfei).toString()));
                    lastRealPay=yingshouxiaoji.toString();
                }
                m.put("trades_id", trades_id + "");
                m.put("lastrealpay", lastRealPay + "");

            });
        }
        return paramMap;
    }


    private  List<Map<String,Object>> xiaoji( List<Map<String,Object>> paramMap){
        if(null!=paramMap&&paramMap.size()>0){
            paramMap.stream().forEach(l -> {
                Map<String, Object> m = (Map<String, Object>) l;
                BigDecimal yingshouxiaoji=null;
                DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                dfs.setDecimalSeparator('.');
                dfs.setGroupingSeparator(',');
                dfs.setMonetaryDecimalSeparator('.');
                DecimalFormat df = new DecimalFormat("###,###.##", dfs);
                String  lastRealPay="---";
                Object financeno = m.get("financeno");
                Object daishoujine=m.get("totalpay");
                Object daituijine=m.get("refundtotal");
                Object daishoushouxufei=m.get("platformtotal");
                Object jiaoyiyongjin=m.get("commissiontotal");
                Object daishoupeisongfei=m.get("posttotal");
                Number parse = 0;
                Number parse1 = 0;
                Number parse2 = 0;
                Number parse3 = 0;
                Number parse4 = 0;
                try {
                    parse = df.parse(daishoujine.toString());
                    parse1 = df.parse(daituijine.toString());
                    parse2 = df.parse(daishoushouxufei.toString());
                    parse3 = df.parse(jiaoyiyongjin.toString());
                    parse4 = df.parse(daishoupeisongfei.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(null!=daishoujine&& StringUtils.equalsIgnoreCase((daishoujine==null?0:daishoujine).toString(),"---")){
                    lastRealPay="---";
                }
                if(null!=daituijine&&!StringUtils.equalsIgnoreCase((daituijine==null?0:daituijine).toString(),"---")){
                    yingshouxiaoji=new BigDecimal(parse.toString()).subtract(new BigDecimal(parse1.toString()));
                    lastRealPay=yingshouxiaoji.toString();
                }

                if(null!=daishoushouxufei&&!StringUtils.equalsIgnoreCase( (daishoushouxufei==null?0:daishoujine).toString(),"---")
                        &&!StringUtils.equalsIgnoreCase(lastRealPay.toString(),"---")){
                    yingshouxiaoji=new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal(parse2.toString()));
                    lastRealPay=yingshouxiaoji.toString();
                }

                if(null!=jiaoyiyongjin&&!StringUtils.equalsIgnoreCase((jiaoyiyongjin==null?0:jiaoyiyongjin).toString(),"---")
                        &&!StringUtils.equalsIgnoreCase(lastRealPay.toString(),"---")){
                    yingshouxiaoji=new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal(parse3.toString()));
                    lastRealPay=yingshouxiaoji.toString();
                }

                if(null!=daishoupeisongfei&&!StringUtils.equalsIgnoreCase((daishoupeisongfei==null?0:daishoupeisongfei).toString(),"---")
                        &&!StringUtils.equalsIgnoreCase(lastRealPay.toString(),"---")){
                    yingshouxiaoji=new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal(parse4.toString()));
                    lastRealPay=yingshouxiaoji.toString();
                }
                m.put("financeno", financeno + "");
                m.put("lastrealpay", lastRealPay + "");

            });
        }
        return paramMap;
    }




}
