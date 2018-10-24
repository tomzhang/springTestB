package com.jk51.modules.discount.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.StoreAdmin;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.modules.esn.mapper.GoodsEsMapper;
import com.jk51.modules.im.service.InitialMessage;
import com.jk51.modules.im.service.PushServeService;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.pay.service.PayfwService;
import com.jk51.modules.pay.service.merchant.WxConfigMerchant;
import com.jk51.modules.pay.service.merchant.WxPayApiMerchant;
import com.jk51.modules.persistence.mapper.DiscountMapper;
import com.jk51.modules.privatesend.core.AliPrivateSend;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2018/4/3.
 */
@Service
public class DiscountService {
    @Autowired
    private DiscountMapper discountMapper;
    @Autowired
    private PushServeService pushServeService;
    @Autowired
    private YbMerchantMapper ybMerchantMapper;
    @Autowired
    private WxPayApiMerchant wxPayApiMerchant;
    @Autowired
    private StoreAdminMapper storeAdminMapper;
    @Autowired
    private PayfwService payService;
    @Autowired
    private AliPrivateSend aliPrivateSend;
    @Autowired
    private GoodsEsMapper goodsEsMapper;
    private static final Logger log = LoggerFactory.getLogger(DiscountService.class);
    /**
     * 阿里微信支付模块
     */
    public Map<String, Object> getPayAliWx(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Map<String,Object> payMap = discountMapper.getPayAliWx(params);
            map.put("payMap", payMap);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 阿里微信支付模块：添加订单来源
     */
    public Map<String, Object> insertTradesLine(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer site_id=Integer.parseInt(params.get("site_id")+"");
            String tradesId = site_id + String.valueOf(System.currentTimeMillis());
            params.put("trades_id",tradesId);
            double real_pay = Double.parseDouble(params.get("real_pay")+"")*10*10;
            double coupon_fee = Double.parseDouble(params.get("coupon_fee")+"")*10*10;
            double total_fee = Double.parseDouble(params.get("total_fee")+"")*10*10;
            if(real_pay<0){
                real_pay=0;
            }
            params.put("real_pay",(int)real_pay);
            params.put("coupon_fee",(int)coupon_fee);
            params.put("total_fee",(int)total_fee);

            //判断手机号是否为新注册
            /*if (!StringUtil.isEmpty(params.get("user_phone"))){
                Integer i = discountMapper.boolPhone(params);
                if (i == 0){
                    params.put("is_register",0);
                }else {
                    params.put("is_register",1);
                }
            }*/

            //判断手机号是否为空
            if (StringUtil.isEmpty(params.get("user_phone")) || "null".equals(String.valueOf(params.get("user_phone")))){
                params.put("user_phone","");
            }
            if(!StringUtil.isEmpty(params.get("store_user_id"))&&!"0".equals(params.get("store_user_id"))){
                Integer store_user_id=Integer.parseInt(params.get("store_user_id")+"");
                List<StoreAdmin> list= storeAdminMapper.getStoreAdminList(store_user_id+"", site_id+"");
                if(list!=null&&list.size()!=0){
                    params.put("trades_store",list.get(0).getStore_id());
                }
            }
            Integer i = discountMapper.insertTradesLine(params);
            if (i == 1){
                YbMerchant m = ybMerchantMapper.selectBySiteId(site_id);
                WxConfigMerchant wxConfig= null;
                try {
                    wxConfig = wxPayApiMerchant.toConfig(site_id);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


                map.put("appid",wxConfig.getAppid());
                map.put("timestamp", System.currentTimeMillis());

                if(real_pay==0){
                    long tradesIdlong = NumberUtils.toLong(tradesId);
                    updateTradesLine("wx",tradesIdlong,site_id,"");
                }
                map.put("msg", "添加成功");
                map.put("status", 0);
                map.put("tradesId", tradesId);
                String shopwx_url = m.getShopwx_url().replaceAll("，", ",").split(",")[0].indexOf("http") > -1 ? m.getShopwx_url().replaceAll("，", ",").split(",")[0] : "http://" + m.getShopwx_url().replaceAll("，", ",").split(",")[0];
                map.put("shopwx_url", shopwx_url);
            }else {
                map.put("msg", "添加失败");
                map.put("status", -1);
            }
            return map;
        } catch (Exception e) {
            log.info("添加异常:{}", e);
            map.put("msg", "添加失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 支付成功回调修改
     * 阿里微信支付模块：修改订单来源
     */
    @Transactional
    public Map<String, Object> updateTradesLine(String pay_style,long tradesId,Integer siteId,String pay_number) {

        Map<String, Object> map = new HashMap<>();
        try {
            Map<String, Object> maptrades=new HashedMap();
            maptrades.put("trades_id",tradesId);
            maptrades.put("is_payment",1);
            maptrades.put("pay_style",pay_style);
            maptrades.put("pay_time",new Date());
            maptrades.put("pay_number",pay_number);
            maptrades.put("site_id",siteId);
            Integer i = discountMapper.updateTradesLine(maptrades);
            if (i == 1){
                //提醒APP
                toAppMsg(tradesId);
                //修改:修改指定会员  所有未使用红包金额  为  已使用红包
                Map<String, Object> tradesMap =new HashedMap();
                tradesMap.put("trades_id",tradesId);
                Map<String, Object> tradesRmap = getTradesLine(tradesMap);
                Map<String, Object> paramsmember=new HashedMap();
                if(!StringUtil.isEmpty(tradesRmap.get("buyer_id"))){
                    Map<String,Object> buyerIdMap = new HashMap<>();
                    buyerIdMap.put("siteId",siteId);
                    buyerIdMap.put("buyer_id",Integer.parseInt(String.valueOf(tradesRmap.get("buyer_id"))));
                    Integer memberId = discountMapper.getMemberId(buyerIdMap);//根据buyer_id查询member_id
                    paramsmember.put("memberId",memberId);
                }
                paramsmember.put("siteId",tradesRmap.get("site_id"));
                updateRedpacketType(paramsmember);//retMap 只是为了检查查看
                map.put("msg", "修改成功");
                map.put("status", 0);
            }else {
                map.put("msg", "修改失败");
                map.put("status", -1);
            }
            return map;
        } catch (Exception e) {
            log.info("修改异常:{}", e);
            map.put("msg", "修改失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询打折记录
     */
    public Map<String, Object> getDiscountRuleLine(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            //根据订单ID查询该订单是否已经领了红包
            Map<String,Object> boolMoney = discountMapper.boolMoneyByTradesId(params);
            if (!StringUtil.isEmpty(boolMoney) && boolMoney.containsKey("redpacket_pay")){
                Integer randomNum = Integer.parseInt(String.valueOf(boolMoney.get("redpacket_pay")));
                map.put("randomNum", (double)randomNum/100);
                map.put("status", 0);
                map.put("msg", "查询成功");
                map.put("ruleMap", null);
                return map;
            }

            double total_feeT=Double.parseDouble(params.get("total_fee")+"")*10*10;
            int orderFee= (int) total_feeT;
            Map<String,Object> ruleMap = discountMapper.getDiscountRuleLine(params);
            if(ruleMap!=null){
                map.put("randomNum", 0);
                String rule_val= (String) ruleMap.get("rule_val");
                Integer discount_type= (Integer) ruleMap.get("discount_type");
                //后台设置总金额
                Integer total_fee= Integer.parseInt(ruleMap.get("total_fee")+"") ;
                //后台设置每日总金额
                Integer day_total_fee= Integer.parseInt(ruleMap.get("day_total_fee")+"") ;
                //所有线下订单总金额
                Integer old_total_fee=discountMapper.getTradesLineSum(params);

                SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd");
                params.put("create_time", data.format(new Date()) + " 00:00:00");
                params.put("create_time_end", data.format(new Date()) + " 23:59:59");
                //所有线下订单今日总金额
                int old_total_fee_day=discountMapper.getTradesLineSum(params);

                if(old_total_fee<total_fee&&old_total_fee_day<day_total_fee){
                    List<String> ruleList= JacksonUtils.json2listMap2(rule_val);
                    for (String ruleStr:ruleList){
                        Map<String, Object> rule=JacksonUtils.json2map(ruleStr);
                        Integer man_start= Integer.parseInt( rule.get("man_start")+"") ;
                        Integer man_end= Integer.parseInt( rule.get("man_end")+"") ;
                        Integer jian_start= Integer.parseInt( rule.get("jian_start")+"") ;
                        Integer jian_end= Integer.parseInt( rule.get("jian_end")+"") ;
                        Integer point= Integer.parseInt( rule.get("point")+"") ;
                        if(orderFee>=man_start&&orderFee<man_end){
                            params.put("real_pay", man_start);
                            params.put("real_pay_end", man_end);
                            params.put("create_time_end", null);
                            Integer old_point_fee=discountMapper.getTradesLineSum(params);
                            if((long)old_point_fee < (long)point*total_fee){
                                double randomNumint=getRandom(jian_start,jian_end);
                                if((old_total_fee+randomNumint)<total_fee&&(old_total_fee_day+randomNumint)<day_total_fee){
                                    double randomNum=randomNumint/100;
                                    map.put("randomNum", randomNum);
                                    //添加:获取红包记录
                                    params.put("redpacket_pay",randomNumint);
                                }

//                                Integer memberId = discountMapper.getMemberId(params);//根据buyer_id查询member_id
//                                params.put("memberId",memberId);
                                Map<String, Object> retMap = insertRedpacketLine(params);//retMap 只是为了检查查看
                                break;
                            }
                        }
                    }
                }
                if (map.containsKey("randomNum")){
                    double money = Double.parseDouble(String.valueOf(map.get("randomNum")));
                    if (money > 0){
                        Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
                        //查询会员的ali_user_id  ， 商户名称
                        String ali_user_id = discountMapper.getAliUserId(params);
                        String mechantName = discountMapper.getMechantName(siteId);
                        aliPrivateSend.redBacketSuccess(siteId,ali_user_id,"","恭喜您，中奖了","请稍后到微商城查看",money+"元","恭喜发财，大吉大利",mechantName);
                    }
                }
                map.put("status", 0);
                map.put("msg", "查询成功");
            }else {
                map.put("msg", "查询失败");
                map.put("status", -1);
            }
//            map.put("ruleMap", ruleMap);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询打折记录
     */
    public Map<String, Object> getDiscountRuleLineBySiteId(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Map<String,Object> ruleMap = discountMapper.getDiscountRuleLineBySiteId(params);
            //获取优惠累计金额
//            if (!StringUtil.isEmpty(ruleMap)){
////                String startTime = String.valueOf(ruleMap.get("discount_start_time"));
////                String endTime = String.valueOf(ruleMap.get("discount_end_time"));
////                params.put("starttime",startTime);
////                params.put("endtime",endTime);
//                Integer total_money = discountMapper.getTotalMoneyByActivite(params);//获取总优惠累计金额
//                ruleMap.put("total_money",total_money);
//
//                //获取当天时间
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                String starttime_day = sdf.format(new Date()) + " 00:00:00";
//                String endtime_day = sdf.format(new Date()) + " 23:59:59";
//                params.put("starttime",starttime_day);
//                params.put("endtime",endtime_day);
//                Integer total_money_day = discountMapper.getTotalMoneyByActivite(params);//获取当天总优惠累计金额
//                ruleMap.put("total_money_day",total_money_day);
//            }
            if (!StringUtil.isEmpty(ruleMap) && !StringUtil.isEmpty(ruleMap.get("discount_desc"))){
                Map<String,Object> disMap = JacksonUtils.json2map(String.valueOf(ruleMap.get("discount_desc")));
                if (!StringUtil.isEmpty(disMap) && !StringUtil.isEmpty(disMap.get("pc_desc"))){
                    String discount_desc = String.valueOf(disMap.get("pc_desc"));
                    ruleMap.put("discount_desc",discount_desc);
                }else {
                    ruleMap.put("discount_desc","");
                }
            }


            map.put("ruleMap", ruleMap);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 修改打折记录
     */
    public Map<String, Object> updateDiscountRuleLine(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Map<String,Object> ruleMap = discountMapper.getDiscountRuleLineBySiteId(params);
            Integer i = 0;
            if (!StringUtil.isEmpty(ruleMap)){
                i = discountMapper.updateDiscountRuleLine(params);
            }else {
                i = discountMapper.insertDiscountRuleLine(params);
            }
            map.put("ruleMap", ruleMap);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }


    /**
     * 查询线下订单
     */
    public Map<String, Object> getTradesLine(Map<String, Object> params) {
        return discountMapper.getTradesLine(params);
    }
    public static int getRandom(int min, int max){
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;

    }

    /**
     * 获取红包记录
     * siteId  商户ID
     * memberId  会员ID
     * redpacket_pay  红包金额
     * @param params
     * @return
     */
    public Map<String, Object> insertRedpacketLine(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer i = discountMapper.insertRedpacketLine(params);
            if (i == 1){
                map.put("msg", "添加成功");
                map.put("status", 0);
            }else {
                map.put("msg", "添加失败");
                map.put("status", -1);
            }
            return map;
        } catch (Exception e) {
            log.info("添加异常:{}", e);
            map.put("msg", "添加失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 获取指定会员所有未使用红包金额
     * siteId  商户ID
     * memberId  会员ID
     * @param params
     * @return
     */
    public Map<String, Object> getRedpacketTotalMoneyByUnused(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer i = discountMapper.getRedpacketTotalMoneyByUnused(params);
            //获取优惠规则
            Map<String, Object> discountRuleLine = discountMapper.getDiscountRuleLine(params);
            if (!StringUtil.isEmpty(discountRuleLine) && !StringUtil.isEmpty(discountRuleLine.get("discount_desc"))){
                String desc = String.valueOf(discountRuleLine.get("discount_desc"));
                Map<String, Object> descMap = JacksonUtils.json2map(desc);
                List<String> descList = (List<String>)descMap.get("wx_desc");
                String descStr = descList.get(0);
                if (!StringUtil.isEmpty(descStr)){
                    map.put("desc", desc);
                    map.put("type", 0);
                }else {
                    map.put("desc", "");
                    map.put("type", -1);
                }
            }else {
                map.put("desc", "");
                map.put("type", -1);
            }
            map.put("total_money", ((double)i)/100);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            map.put("desc", "");
            map.put("type", -1);
            return map;
        }
    }

    /**
     * 修改指定会员  所有未使用红包金额  为  已使用红包
     * siteId  商户ID
     * memberId  会员ID
     * @param params
     * @return
     */
    public Map<String, Object> updateRedpacketType(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer i = discountMapper.updateRedpacketType(params);
            map.put("msg", "修改成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("修改异常:{}", e);
            map.put("msg", "修改失败");
            map.put("status", -1);
            return map;
        }
    }
    public void toAppMsg(long trades_id) throws Exception{
        Map<String, Object> tradesMap =new HashedMap();
        tradesMap.put("trades_id",trades_id);
        Map<String, Object> trades = getTradesLine(tradesMap);
        String trades_store="0".equals(trades.get("trades_store")+"")?null:trades.get("trades_store")+"";
        String store_user_id="0".equals(trades.get("store_user_id")+"")?null:trades.get("store_user_id")+"";
        String siteId=trades.get("site_id")+"";
        double real_pay=(Double.parseDouble(trades.get("real_pay")+""))/10/10;
        double coupon_fee=(Double.parseDouble(trades.get("coupon_fee")+""))/10/10;
        String pay_style=trades.get("pay_style")+"";
        pushServeService.pushMessageToList(new InitialMessage(siteId, trades_store, store_user_id) {{
            setMessageType(PushType.NOTIFY_PAY_SUCCESS.getValue());
            setMessageMapJSON(JSON.toJSONString(new HashMap() {{
                put("money", real_pay);
                put("coupon_fee", coupon_fee);
                put("pay_style", pay_style);
            }}));
        }}, null);
        /*if(!StringUtil.isEmpty(store_user_id)&&"0".equals(store_user_id))
            pushServeService.pushMessageToList(new InitialMessage(siteId, trades_store, store_user_id) {{
                setMessageType(PushType.NOTIFY_PAY_FAIL.getValue());
                setMessageMapJSON(JSON.toJSONString(new HashMap() {{
                    put("money", real_pay);
                }}));
            }}, null);
        else if(!StringUtil.isEmpty(trades_store)&&"0".equals(trades_store)){

        }*/
    }


    /**
     * 获取支付优惠订单列表
     * siteId  商户ID
     * memberId  会员ID
     * @param params
     * @return
     */
    public Map<String, Object> getDiscountOrderList(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {

            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> orderList = discountMapper.getDiscountOrderList(params);
            if (orderList.size() > 0){
                for (Map<String,Object> orderMap : orderList){
                    if (StringUtil.isEmpty(orderMap.get("user_phone"))){
                        orderMap.put("user_phone","游客");
                    }
                    orderMap.put("trades_id",orderMap.get("trades_id").toString());
                }
            }
            PageInfo<Map<String, Object>> allLabel = new PageInfo<>(orderList);

            //获取优惠累计金额
            Integer total_money = discountMapper.getTotalMoneyByActivite(params);//获取总优惠累计金额
            map.put("total_money",total_money);

            //获取当天时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String starttime_day = sdf.format(new Date()) + " 00:00:00";
            String endtime_day = sdf.format(new Date()) + " 23:59:59";
            params.put("starttime",starttime_day);
            params.put("endtime",endtime_day);
            Integer total_money_day = discountMapper.getTotalMoneyByActivite(params);//获取当天总优惠累计金额
            map.put("total_money_day",total_money_day);

            map.put("orderList", allLabel);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 线下支付设置查询
     * siteId  商户ID
     * @param params
     * @return
     */
    public Map<String, Object> getOfflineBySiteId(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Map<String,Object> offlineMap = discountMapper.getOfflineBySiteId(params);

            map.put("offlineMap", offlineMap);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 修改/添加线下支付设置
     * @param params
     * @return
     */
    public Map<String, Object> editOfflineBySiteId(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            //判断数据是否有重复
            Map<String,Object> resultMap = boolDataIsRepetition(params);
            if (!StringUtil.isEmpty(resultMap)){
                return resultMap;
            }
            //存在，修改；不存在，添加
            Integer i = discountMapper.boolOfflineBySiteId(params);
            Integer j = 0;
            if (i > 0){
                j = discountMapper.editOfflineBySiteId(params);//修改
                if (j == 1){
                    //修改yb_merchant_ext中的数据
                    if (!StringUtil.isEmpty(params.get("wx_app_id")) || !StringUtil.isEmpty(params.get("wx_key"))){
                        discountMapper.updateMechantExt(params);
                    }
                    //修改yb_merchant中的数据
                    if (!StringUtil.isEmpty(params.get("ali_app_id")) || !StringUtil.isEmpty(params.get("ali_private_key"))
                        || !StringUtil.isEmpty(params.get("ali_public_key")) || !StringUtil.isEmpty(params.get("public_key"))){
                        discountMapper.updateMechant(params);
                    }
                }


            }else {
                j = discountMapper.insertOfflineBySiteId(params);//添加
            }
            if (j == 1){
                map.put("msg", "编辑成功");
                map.put("status", 0);
            }else {
                map.put("msg", "编辑失败");
                map.put("status", -1);
            }
            goodsEsMapper.insertLog(params.get("siteId").toString(), System.currentTimeMillis()+":"+JacksonUtils.obj2json(params), "修改支付配置结果："+JacksonUtils.obj2json(map));
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "编辑失败");
            map.put("status", -1);
            try {
                goodsEsMapper.insertLog(params.get("siteId").toString(), System.currentTimeMillis()+":"+JacksonUtils.obj2json(params), "修改支付配置更新异常："+e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return map;
        }
    }

    public Map<String,Object> boolDataIsRepetition(Map<String, Object> params){
        Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
        if (!StringUtil.isEmpty(params)){
            for (String key : params.keySet()){
                String value = String.valueOf(params.get(key));
                if (!StringUtil.isEmpty(value)){
                    String msg = "";
                    if ("wx_app_id".equals(key)){
                        msg = "公众号APPID";
                    }else if ("wx_mch_id".equals(key)){
                        msg = "微信支付商户号";
                    }else if ("wx_key".equals(key)){
                        msg = "公众号开发者密钥";
                    }else if ("wx_appsecret".equals(key)){
                        msg = "微信支付密钥";
                    }else if ("ali_app_id".equals(key)){
                        msg = "支付宝应用APPID";
                    }else if ("ali_seller_id".equals(key)){
                        msg = "支付宝账号";
                    }else if ("ali_private_key".equals(key)){
                        msg = "支付宝私有密钥（工具生成）";
                    }else if ("ali_public_key".equals(key)){
                        msg = "支付宝公有密钥（工具生成）";
                    }else if ("public_key".equals(key)){
                        msg = "支付宝公有密钥（支付宝生成）";
                    }else if ("ali_app_auth_token".equals(key)){
                        msg = "商家授权token";
                    }
                    Map<String,Object> rMap = discountMapper.getBoolDataIsRepetition(siteId,value);
                    if (!StringUtil.isEmpty(rMap)){
                        Integer i = Integer.parseInt(String.valueOf(rMap.get("num")));
                        String merchant_name = String.valueOf(rMap.get("merchant_name"));
                        String merchant_id = String.valueOf(rMap.get("site_id"));

                        Map<String,Object> map = new HashMap<>();
                        if (i > 0){
                            map.put("msg", msg + "  与商户："+merchant_name+"（"+merchant_id+"）"+" 有重复，请检查");
                            map.put("status", -1);
                            return map;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 添加小票索要记录
     * @param params
     * @return
     *
     * siteId
     * phone    手机号
     * remark   备注
     */
    public Map<String, Object> insertTicketBlag(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer i = discountMapper.insertTicketBlag(params);
            if (i == 1){
                map.put("msg", "添加成功");
                map.put("status", 0);
            }else {
                map.put("msg", "添加失败");
                map.put("status", -1);
            }
            return map;
        } catch (Exception e) {
            log.info("添加异常:{}", e);
            map.put("msg", "添加失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询小票索要记录
     * @param params
     * @return
     *
     * siteId
     * handle_status    状态
     */
    public Map<String, Object> getTicketBlag(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum, pageSize);//开启分页
            List<Map<String, Object>> ticketBlagMap = discountMapper.getTicketBlag(params);
            PageInfo<Map<String, Object>> allList = new PageInfo<>(ticketBlagMap);

            map.put("allList", allList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询指定会员获取了多少红包及红包总金额
     * @param params
     * @return
     *
     * siteId
     * memberId
     */
    public Map<String, Object> getRedPacketAndAllTotalMoney(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer i = discountMapper.getRedpacketTotalMoneyByUnused(params);//总金额
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("pageNum")));
            Integer pageSize = 15;
            PageHelper.startPage(pageNum, pageSize);//开启分页
            List<Map<String,Object>> redMap = discountMapper.getRedPacket(params);
            PageInfo<Map<String, Object>> allList = new PageInfo<>(redMap);

            map.put("redMap", allList.getList());
            map.put("pages", allList.getPages());
            map.put("totalMoney", ((double)i)/100);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 余额提现
     * @param params
     * @return
     *
     * siteId
     * memberId
     * oppen_id
     * total_money
     */
    public Map<String, Object> getTotalMoneyByBalance(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            //用户的trades_id(随机生成)
            String siteId = String.valueOf(params.get("siteId"));
            String trades_id = siteId + getCustomNum();
            Integer total_money = Integer.parseInt(String.valueOf(params.get("total_money")));
            String oppen_id = discountMapper.getOpenId(params);
            String merchantName = discountMapper.getMechantName(Integer.parseInt(siteId));
            Map redMap = payService.sendredpack(trades_id, total_money*10*10, merchantName, oppen_id);
            int status = 0;
            if (!StringUtil.isEmpty(redMap)){
                String result_code = String.valueOf(redMap.get("result_code"));
                if ("SUCCESS".equals(result_code)){
                    status++;
                    //提现成功，将该用户下所有的未使用红包改成已使用
                    discountMapper.updateRedpacketType(params);
                }
            }
            //记录日志
            String log = JacksonUtils.mapToJson(redMap);
            String member_id = String.valueOf(params.get("memberId"));

            if (status == 0){
                map.put("msg", "提现失败");
                map.put("status", -1);
                discountMapper.insertRedpacketLog(siteId,member_id,trades_id,total_money*10*10,oppen_id,log,1);
            }else {
                map.put("msg", "提现成功");
                map.put("status", 0);
                discountMapper.insertRedpacketLog(siteId,member_id,trades_id,total_money*10*10,oppen_id,log,0);
            }
            return map;
        } catch (Exception e) {
            log.info("提现失败:{}", e);
            map.put("msg", "提现异常");
            map.put("status", -1);
            return map;
        }
    }

    //获取当前时间的毫秒值
    public String getCustomNum(){
        long l = System.currentTimeMillis();
        return String.valueOf(l);
    }

    /**
     * 是否可抽红包
     * @param params
     * @return
     *
     * siteId
     * tradesId
     */
    public Map<String, Object> boolGetRedBao(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer i = discountMapper.boolGetRedBao(params);
            if (i == 1){
                //根据tradesId判断订单是否抽过红包
                Integer j = discountMapper.getHongbaoByTradesId(params);
                if (j==0){i=1;}else {i=0;}
            }
            map.put("status", i);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("status", 0);
            return map;
        }
    }

    /**
     *
     * @param params
     * @return
     *
     * siteId
     */
    public Map<String, Object> updateStatusById(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer i = discountMapper.updateStatusById(params);
            map.put("status", i);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("status", 0);
            return map;
        }
    }

    /**
     * 是否可抽红包
     * @param params
     * @return
     *
     * siteId
     */
    public Map<String, Object> getDiscountExtractList(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String,Object>> extractListMap = discountMapper.getDiscountExtractList(params);
            PageInfo<Map<String, Object>> allList = new PageInfo<>(extractListMap);
            map.put("allList", allList);
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 微信：商户反馈
     * @param params
     * @return
     *
     * siteId
     * phone
     */
    public Map<String, Object> getMerchantBack(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer i = discountMapper.getMerchantBack(params);
            if (i > 0){
                i = -1;
            }
            map.put("status", i);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("status", -1);
            return map;
        }
    }


    public Map<String,Object> getDeviceNumMap(Map<String, Object> params) {
        return discountMapper.getDeviceNumMap(params);
    }
}
