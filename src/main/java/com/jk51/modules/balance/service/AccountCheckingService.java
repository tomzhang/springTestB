package com.jk51.modules.balance.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.balance.mapper.AccountCheckingMapper;
import com.jk51.modules.merchant.service.DataProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Created by Administrator on 2018/9/21.
 */
@Service
public class AccountCheckingService {
    private static final Logger log = LoggerFactory.getLogger(AccountCheckingService.class);
    @Autowired
    private AccountCheckingMapper accountCheckingMapper;
    @Autowired
    private DataProfileService dataProfileService;

    /**
     * 总部订单对账
     * order_start_time 下单时间
     * order_end_time
     * pay_start_time   支付时间
     * pay_end_time
     * pay_style        支付方式
     * checking_stutas  对账状态
     * trades_id
     * @return
     */
    public Map<String,Object> getMerchantTradesChecking(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> merchantTradesCheckingList = accountCheckingMapper.getMerchantTradesChecking(params);
            PageInfo<Map<String, Object>> allList = new PageInfo<>(merchantTradesCheckingList);
            map.put("allList", allList);
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 总部订单对账
     * siteId
     * import_time  资金对账时间（必填）
     * total_pay    总金额（必填）
     * store_num     门店编号
     * store_name   门店名称
     * wx_pay       微信支付
     * ali_pay      支付宝支付
     * cash_pay     现金支付
     * health_insurance_pay 医保支付
     * @return
     */
    public Map<String,Object> addCheckingData(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            //查询数据是否存在(1.门店资金情况，2.总部资金情况)
            //门店资金情况:siteId，import_time，status=1，store_num
            //总部资金情况:siteId，import_time，status=0，store_num=''
//            Integer count = accountCheckingMapper.boolStoreData(params);//判断是否存在

            params.put("status",params.containsKey("erp_store_num")?1:0);//判断类型 1：门店  0：总部
            Integer cnt = accountCheckingMapper.updateFundsData(params);
            map.put("returnInt", cnt==1?0:1);
            map.put("status", cnt);
        }catch (Exception e){
            log.info("添加异常:{}", e);
            map.put("status", 0);
        }
        return map;
    }

    /**
     * 总部订单对账
     * start_time
     * end_time
     * checking_stutas  对账状态
     * @return
     */
    public Map<String,Object> getMerchantFunds(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            params.put("status",0);
            params = getTime(params);
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> merchantTradesCheckingList = accountCheckingMapper.getMerchantFunds(params);
            //判断状态
            merchantTradesCheckingList = exchangeData(merchantTradesCheckingList);
            PageInfo<Map<String, Object>> allList = new PageInfo<>(merchantTradesCheckingList);
            map.put("allList", allList);
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 总部订单对账(报表)
     * start_time
     * end_time
     * checking_stutas  对账状态
     * @return
     */
    public List<Map<String,Object>> getMerchantFundsReport(Map<String,Object> params){
        params.put("status",0);
        params = getTime(params);
        List<Map<String,Object>> merchantTradesCheckingList = accountCheckingMapper.getMerchantFunds(params);
        //判断状态
        merchantTradesCheckingList = exchangeData(merchantTradesCheckingList);
        return merchantTradesCheckingList;
    }

    /**
     * 门店订单对账
     * start_time
     * end_time
     * store_num
     * store_name
     * checking_stutas  对账状态
     * @return
     */
    public Map<String,Object> getStoreFunds(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try{
            params.put("status",1);
            params = getTime(params);
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> merchantTradesCheckingList = accountCheckingMapper.getMerchantFunds(params);
            //判断状态
            merchantTradesCheckingList = exchangeData(merchantTradesCheckingList);
            PageInfo<Map<String, Object>> allList = new PageInfo<>(merchantTradesCheckingList);
            map.put("allList", allList);
            map.put("status", 0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}", e);
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 门店订单对账(报表)
     * start_time
     * end_time
     * checking_stutas  对账状态
     * @return
     */
    public List<Map<String,Object>> getStoreFundsReport(Map<String,Object> params){
        params.put("status",1);
        params = getTime(params);
        List<Map<String,Object>> merchantTradesCheckingList = accountCheckingMapper.getMerchantFunds(params);
        //判断状态
        merchantTradesCheckingList = exchangeData(merchantTradesCheckingList);
        return merchantTradesCheckingList;
    }




    //处理时间
    public Map<String,Object> getTime(Map<String,Object> params){
        if (params.containsKey("start_time") && params.containsKey("end_time")){
            String start_time = String.valueOf(params.get("start_time")).substring(0,10).replaceAll("-","");
            String end_time = String.valueOf(params.get("end_time")).substring(0,10).replaceAll("-","");
            params.put("start_time",start_time);
            params.put("end_time",end_time);
        }
        return params;
    }

    //比对数据
    public List<Map<String,Object>> exchangeData(List<Map<String,Object>> list){
        if (!list.isEmpty()){
            for (Map<String,Object> merchantTradesCheckingMap : list){
                Integer checking_status = Integer.parseInt(String.valueOf(merchantTradesCheckingMap.get("checking_status")));
                if (checking_status == 1){
                    double wx_pay = Double.parseDouble(String.valueOf(merchantTradesCheckingMap.get("wx_pay")));
                    double ali_pay = Double.parseDouble(String.valueOf(merchantTradesCheckingMap.get("ali_pay")));
                    double cash_pay = Double.parseDouble(String.valueOf(merchantTradesCheckingMap.get("cash_pay")));
                    double health_insurance_pay = Double.parseDouble(String.valueOf(merchantTradesCheckingMap.get("health_insurance_pay")));
                    double total_pay = Double.parseDouble(String.valueOf(merchantTradesCheckingMap.get("total_pay")));
                    double erp_wx_pay = Double.parseDouble(String.valueOf(merchantTradesCheckingMap.get("erp_wx_pay")));
                    double erp_ali_pay = Double.parseDouble(String.valueOf(merchantTradesCheckingMap.get("erp_ali_pay")));
                    double erp_cash_pay = Double.parseDouble(String.valueOf(merchantTradesCheckingMap.get("erp_cash_pay")));
                    double erp_health_insurance_pay = Double.parseDouble(String.valueOf(merchantTradesCheckingMap.get("erp_health_insurance_pay")));
                    double erp_total_pay = Double.parseDouble(String.valueOf(merchantTradesCheckingMap.get("erp_total_pay")));
                    if (wx_pay == erp_wx_pay && ali_pay==erp_ali_pay && cash_pay==erp_cash_pay && health_insurance_pay==erp_health_insurance_pay && total_pay==erp_total_pay){
                        merchantTradesCheckingMap.put("checking_status","一致");
                    }else {
                        merchantTradesCheckingMap.put("checking_status","不一致");
                    }
                }else {
                    merchantTradesCheckingMap.put("checking_status","未对账");
                }
            }
        }
        return list;
    }

    //定时任务：添加资金对账数据（总部）
    public void timeingMerchantFunds(){
        Map<String,Object> map =  getYestodayTime();//获取昨天的时间
        List<Integer> siteIdAll = accountCheckingMapper.getSiteIdAll();
//        List<Integer> siteIdAll = new ArrayList(){{add(100190);}};
        for (Integer siteId : siteIdAll){
            map.put("siteId",siteId);
            Map<String,Object> merchantfundsMap = accountCheckingMapper.getMerchantFundsBySiteIdAndTime(map);//查询数据

            merchantfundsMap.put("siteId",siteId);
            merchantfundsMap.put("import_time",String.valueOf(map.get("import_time")));
            merchantfundsMap.put("standard_time",String.valueOf(map.get("standard_time")));
            merchantfundsMap.put("status",0);
            accountCheckingMapper.insertFundsBySiteId(merchantfundsMap);//添加数据
        }
    }

    //定时任务：添加资金对账数据（门店）
    public void timeingStoreFunds(){
        Map<String,Object> map =  getYestodayTime();//获取昨天的时间
        List<Integer> siteIdAll = accountCheckingMapper.getSiteIdAll();//所有商家
//        List<Integer> siteIdAll = new ArrayList(){{add(100190);}};
        for (Integer siteId : siteIdAll){
            List<Map<String,Object>> storeMapList = accountCheckingMapper.getStoreMaoList(siteId);
            storeMapList.add(0,new HashMap(){{    //处理门店id为0的总部门店（b_trades表中）
                put("id",0);
                put("stores_number","总部门店");
                put("name","总部门店");
            }});
            map.put("siteId",siteId);
            for (Map<String,Object> storeMap : storeMapList){
                Integer storeId = Integer.parseInt(String.valueOf(storeMap.get("id")));
                map.put("storeId",storeId);
                Map<String,Object> merchantfundsMap = accountCheckingMapper.getMerchantFundsBySiteIdAndTime(map);//查询数据

                merchantfundsMap.put("siteId",siteId);
                merchantfundsMap.put("import_time",String.valueOf(map.get("import_time")));
                merchantfundsMap.put("standard_time",String.valueOf(map.get("standard_time")));
                merchantfundsMap.put("store_num",String.valueOf(storeMap.get("stores_number")));
                merchantfundsMap.put("store_name",String.valueOf(storeMap.get("name")));
                merchantfundsMap.put("status",1);
                if (merchantfundsMap.containsKey("total_pay") && !StringUtil.isEmpty(merchantfundsMap.get("total_pay")) ){
                    accountCheckingMapper.insertFundsBySiteId(merchantfundsMap);//添加数据
                }
            }
        }
    }

    public Map<String,Object> getYestodayTime(){
        Map<String,Object> map = new HashMap<>();
        String nextDay = dataProfileService.getNextDay(new Date());//获取昨天的时间
        String import_time = nextDay.replaceAll("-","");
        String start_time = nextDay + " 00:00:00";
        String end_time = nextDay + " 23:59:59";
        map.put("start_time",start_time);
        map.put("end_time",end_time);
        map.put("import_time",import_time);
        map.put("standard_time",start_time);
        return map;
    }


}
