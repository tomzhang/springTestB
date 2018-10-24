package com.jk51.modules.offline.service;

import com.google.common.util.concurrent.AtomicDouble;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.erpDataConfig.DataSourceConfig_RuiSen;
import com.jk51.model.erp.TRGoods;
import com.jk51.model.order.Orders;
import com.jk51.model.order.Trades;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.integral.mapper.IntegrallogMapper;
import com.jk51.modules.member.mapper.MemberInfoMapper;
import com.jk51.modules.offline.mapper.BErpOrderLogMapper;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.modules.trades.service.TradesService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: erp订单同步服务
 * 作者: dumingliang
 * 创建日期: 2017-12-06
 * 修改记录:
 */
@Service
public class OfflineOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineOrderService.class);
    @Value("${erp.is_open}")
    private String is_Open;
    @Autowired
    private TradesService tradesService;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private IntegrallogMapper integrallogMapper;
    @Autowired
    private MerchantERPMapper merchantERPMapper;
    @Autowired
    private ErpToolsService erpToolsService;
    @Resource
    private DataSourceConfig_RuiSen dataSourceConfig_ruiSen;
    @Autowired
    private BErpOrderLogMapper erpOrderLogMapper;
    @Autowired
    private ErpMerchantSettingService staticsService;
    @Autowired
    private MemberInfoMapper memberInfoMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private OfflineMemberService offlineMemberService;

    /**
     * 订单推送
     *
     * @param siteId
     * @param tradesId
     */
    public void erpOrdersService(Integer siteId, Long tradesId) {
        LOGGER.info("OfflineOrderService===erpOrdersService,siteId{},tradesId{}", siteId, tradesId);
        if ("true".equals(is_Open)) {
            try {
                Map<String, Object> response = pushorders(siteId, tradesId);
                if (Integer.parseInt(response.get("code").toString()) == 0) {
                    staticsService.insertFaultStatics(siteId, "订单推送接口:[erpOrdersService]", 1,
                        200, response.get("msg").toString(), response.get("msg").toString(), String.valueOf(tradesId), 1);
                } else if (Integer.parseInt(response.get("code").toString()) == -2) {
                    staticsService.insertFaultStatics(siteId, "订单推送接口:[erpOrdersService]", 1,
                        201, response.get("msg").toString(), response.get("msg").toString(), String.valueOf(tradesId), 0);
                } else if (Integer.parseInt(response.get("code").toString()) == -3) {

                } else {
                    staticsService.insertFaultStatics(siteId, "订单推送接口:[erpOrdersService]", 1,
                        300, response.get("msg").toString(), response.get("msg").toString(), String.valueOf(tradesId), 0);
                }
            } catch (Exception e) { //发送失败，接口返回为null;
                LOGGER.info("ERP推送订单信息失败，siteId{},tradesId{},reason:{},", siteId, tradesId, e.getMessage());
                if (erpToolsService.judgeErpAppliBySiteId(siteId)) {
                    staticsService.insertFaultStatics(siteId, "订单推送接口:[erpOrdersService]", 1,
                        500, e.getMessage(), e.toString(), String.valueOf(tradesId), 0);
                } else {
                    staticsService.insertFaultStatics(siteId, "订单推送接口:[erpOrdersService]", 1,
                        400, e.getMessage(), e.toString(), String.valueOf(tradesId), 0);
                }
            }
        }
     /*   try {
            Trades trades = tradesService.getTradesDetial(tradesId);
            pushOrder_pingAN(siteId, trades, 0);
        } catch (Exception e) {
            LOGGER.info("仅限平安订单推送,e", e.getMessage());
        }*/
    }

    /**
     * 故障中心推送订单信息
     *
     * @param siteId
     * @param tradesId
     */
    public Integer erpOrdersService_faultCenter(Integer faultId, Integer siteId, Integer type, Long tradesId) {
        if ("true".equals(is_Open)) {
            try {
                Map<String, Object> response = pushorders(siteId, tradesId);
                if (Integer.parseInt(response.get("code").toString()) == 0) {
                    staticsService.updateFaultStaticsType(faultId, siteId, "订单推送接口:[erpOrdersService_faultCenter]", type,
                        200, null, null, 1);
                    return 1;
                } else if (Integer.parseInt(response.get("code").toString()) == -2) {
                    staticsService.insertFaultStatics(siteId, "订单推送接口:[erpOrdersService]", 1,
                        201, response.get("msg").toString(), response.get("msg").toString(), String.valueOf(tradesId), 0);
                } else if (Integer.parseInt(response.get("code").toString()) == -3) {

                } else {
                    staticsService.updateFaultStaticsType(faultId, siteId, "订单推送接口:[erpOrdersService_faultCenter]", type,
                        300, response.get("msg").toString() + "pushInfo:" + tradesId, response.get("msg").toString(), 0);
                }
            } catch (Exception e) { //发送失败
                LOGGER.info("ERP推送订单信息失败，siteId{},tradesId{},reason:{},", siteId, tradesId, e.getMessage());
                if (erpToolsService.judgeErpAppliBySiteId(siteId)) {
                    staticsService.updateFaultStaticsType(faultId, siteId, "订单推送接口:[erpOrdersService_faultCenter]", type,
                        500, e.getMessage() + "pushInfo:" + tradesId, e.toString(), 0);
                } else {
                    staticsService.updateFaultStaticsType(faultId, siteId, "订单推送接口:[erpOrdersService_faultCenter]", type,
                        400, e.getMessage() + "pushInfo:" + tradesId, e.toString(), 0);
                }
            }
        }
        return -1;
    }

    public Map<String, Object> pushorders(Integer siteId, Long tradesId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> result = merchantERPMapper.selectMerchantERPInfo(siteId);
        if (StringUtil.isEmpty(result)) {
            response.put("code", -3);
            response.put("msg", "该商户还未对接erp");
        } else if (Integer.parseInt(result.get("status").toString()) == 0) {
            response.put("code", -2);
            response.put("msg", "该商户erp接口通道全部关闭");
        } else if (Integer.parseInt(result.get("trades").toString()) == 0) {
            response.put("code", -3);
            response.put("msg", "该商户erp接口订单推送通道关闭或未打通订单接口");
        } else {
            String baseUrl = result.get("erpUrl").toString();
            Trades trades = tradesService.getTradesDetial(tradesId);
            offlineMemberService.getuser(siteId, trades.getMemberMobile(), "");
            if (siteId == 100166) {
                response = pushOrder_JiuZhou_New(siteId, tradesId, baseUrl);
            } else if (siteId == 100190) {//天润商户时推送订单
                response = pushOrder_TianRun(siteId, tradesId, baseUrl);
            } else if (siteId == 100213 || siteId == 100239 || siteId == 100271 || siteId == 100262) {//海典商户济生堂，天伦，内蒙国大,张和堂推送订单
                response = pushOrder_haidian(siteId, tradesId, baseUrl);
            } else if (siteId == 100238) {//瑞森推送订单
                response = pushOrder_ruisen(siteId, tradesId, baseUrl);
            } else if (siteId == 100268) {//德仁堂推送订单
                response = pushOrder_derentang(siteId, tradesId, baseUrl);
            }
        }
        return response;
    }

    //    @TimeRequired
    public Map<String, Object> pushOrder_JiuZhou_New(Integer siteId, long tradesId, String baseUrl) throws Exception {
        Map<String, Object> tradeObj = tradesMapper.selectTradesListfromERP_new(siteId, tradesId);
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        if (!"1".equals(tradeObj.get("payment").toString())) {
            return result;
        }
        List<Map> goodsInfos = ordersMapper.selectOrderListFromERP(siteId, Long.parseLong(tradeObj.get("tId").toString()));
        BigDecimal discount = new BigDecimal(tradeObj.get("totalFee").toString()).
            add(new BigDecimal(tradeObj.get("postFee").toString())).
            subtract(new BigDecimal(tradeObj.get("realPay").toString())).
            subtract(new BigDecimal(tradeObj.get("postageDiscount").toString()));//商品总优惠金额.去除运费优惠
        BigDecimal total = new BigDecimal(String.valueOf(tradeObj.get("totalFee")));
        AtomicDouble sum = new AtomicDouble(0.0);
        AtomicInteger count = new AtomicInteger(0);
        int goodsSize = goodsInfos.size();
        goodsInfos.forEach(goods -> {
            count.incrementAndGet();
            if (Integer.parseInt(goods.get("gprice").toString()) != 0 && total.compareTo(discount) >= 0) {
                BigDecimal goodsPrice = new BigDecimal(String.valueOf(goods.get("gprice")));
                BigDecimal discountPart = null;//商品优惠金额
                if (count.get() == goodsSize) {//最后一个商品价格
                    discountPart = (discount.subtract(new BigDecimal(Double.toString(sum.get()))))
                        .divide(new BigDecimal(String.valueOf(goods.get("gnum"))), 0, BigDecimal.ROUND_FLOOR);
                } else {
                    discountPart = goodsPrice.multiply(discount).divide(total, 0, BigDecimal.ROUND_FLOOR);
                    sum.addAndGet((discountPart.multiply(new BigDecimal(String.valueOf(goods.get("gnum"))))).doubleValue());
                }
                goods.put("gdiscount", discountPart);
                goods.put("gdisPrice", goodsPrice.subtract(discountPart));
            } else {
                goods.put("gdiscount", 0);
                goods.put("gdisPrice", 0);
            }
        });
        tradeObj.put("orderList", goodsInfos);
        if (1 == Integer.parseInt(tradeObj.get("payment").toString()) && 120 == Integer.parseInt(tradeObj.get("refund").toString())) {
            tradeObj.put("status", 0);//退款成功（和天润，济生堂，天伦规则不一样）
        } else {
            tradeObj.put("status", 1);//付款成功
        }
        tradeObj.put("totalPay", Integer.parseInt(tradeObj.get("totalFee").toString()) + Integer.parseInt(tradeObj.get("postFee").toString()));
        tradeObj.remove("payment");
        tradeObj.remove("refund");
        params.put("tradesList", tradeObj);
        params.put("code", StringUtil.isEmpty(tradeObj) ? -1 : 0);
        String url = baseUrl + "/orders/pushOrder";
        LOGGER.info("推送订单信息给九州" + params);
        LOGGER.info("九州推送订单返回值======" + OkHttpUtil.postJson(url, JacksonUtils.mapToJson(params)));
        erpOrderLogMapper.insertSelectErpLog(siteId, url, JacksonUtils.mapToJson(params), JacksonUtils.mapToJson(result));
        return result;
    }

    //    @TimeRequired
    public Map<String, Object> pushOrder_TianRun(Integer siteId, Long tradesId, String baseUrl) throws Exception {
        Trades trades = tradesService.getTradesDetial(tradesId);
        String mark = "购物送积分，订单号：" + tradesId;
        //查询订单送了多少积分，如果是积分兑换，则为null
        Map<String, Object> resultparams = integrallogMapper.getIntegralLogg(trades.getSiteId(), 40, mark);
        if (StringUtil.isEmpty(resultparams)) {
            trades.setIntegralFinalAward(0);
        } else if (!StringUtil.isEmpty(resultparams.get("integral_add"))) {
            trades.setIntegralFinalAward(Integer.parseInt(resultparams.get("integral_add").toString()));
        } else {
            trades.setIntegralFinalAward(0);
        }
        LOGGER.info("实时推送订单信息siteId{},trades{}", siteId, trades.toString());
        Map<String, Object> tradeObj = new HashMap<>();
        tradeObj.put("mobile", trades.getMemberMobile());//会员手机号
        tradeObj.put("djbh", trades.getTradesId());//订单编号
        tradeObj.put("sn", "1");//行号；默认为1
        tradeObj.put("createTime", trades.getCreateTime());//订单创建时间
        tradeObj.put("zx", "否");//是否执行；默认“是”
        tradeObj.put("lxr", trades.getRecevierName());//收件人姓名
        tradeObj.put("lxrdh", trades.getRecevierMobile());//收件人联系方式
        tradeObj.put("lxrdz", trades.getReceiverAddress());//收件人地址
        tradeObj.put("bz", trades.getBuyerMessage());//用户备注
        tradeObj.put("totalShl", "0");//一个订单中总数量
        tradeObj.put("postFee", trades.getPostFee());//运费
        tradeObj.put("realpay", trades.getRealPay());//实际支付金额
        tradeObj.put("totalYh", trades.getTotalFee() + trades.getPostFee() - trades.getRealPay());//优惠金额
        tradeObj.put("jifen", trades.getIntegralFinalAward());//订单赠送积分
        if (trades.getIsPayment() == 1 && trades.getIsRefund() != 120) {//当前订单付款成功订单传0，退款成功订单传1
            tradeObj.put("is_return", 0);
        } else if (trades.getIsRefund() == 120) {
            tradeObj.put("is_return", 1);
        } else {
            tradeObj.put("is_return", 1);
        }
        List<TRGoods> orderList = new ArrayList<>();
        for (int i = 0; i < trades.getOrdersList().size(); i++) {//订单内容赋值
            TRGoods trGoods = new TRGoods();
            trGoods.setSpbh(trades.getOrdersList().get(i).getGoodsCode());
            if (StringUtil.isEmpty(trades.getStore())) {
                trGoods.setJigid("0");
            } else {
                trGoods.setJigid(trades.getStore().getStoresNumber());
            }
            trGoods.setYhshj(String.valueOf(trades.getOrdersList().get(i).getGoodsPrice()));
            trGoods.setShl(String.valueOf(trades.getOrdersList().get(i).getGoodsNum()));
            if (trades.getOrdersList().get(i).getGoodsGifts() == 1) {
                trGoods.setFinalPrice(String.valueOf(0));
            } else {
                if (StringUtil.isEmpty(trades.getOrdersList().get(i).getGoodsFinalPrice())) {
                    trGoods.setFinalPrice(String.valueOf(trades.getOrdersList().get(i).getGoodsPrice()));
                } else {
                    trGoods.setFinalPrice(String.valueOf(trades.getOrdersList().get(i).getGoodsFinalPrice()));
                }
            }
            orderList.add(trGoods);
        }
        tradeObj.put("goodList", orderList);//商品列表
        LOGGER.info("天润订单推送，订单内容{}", tradeObj);
        String param = URLEncoder.encode(JacksonUtils.mapToJson(tradeObj), "UTF-8");
        String url = baseUrl + "/createorder/" + param;
        LOGGER.info("调取订单地址" + url);
        //code:0;正常
        Map<String, Object> result = JacksonUtils.json2map(OkHttpUtil.get(url));
        erpOrderLogMapper.insertSelectErpLog(siteId, baseUrl + "/createorder/", JacksonUtils.mapToJson(tradeObj), JacksonUtils.mapToJson(result));
        return result;
    }

    /**
     * 济生堂订单推送
     *
     * @param siteId
     * @param tradesId
     */
//    @TimeRequired
    public Map<String, Object> pushOrder_haidian(Integer siteId, Long tradesId, String baseUrl) throws Exception {
        Trades trades = tradesService.getTradesDetial(tradesId);
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("code", 0);
        Map<String, Object> tradesList = new HashedMap();
        tradesList.put("tId", tradesId);
        if (trades.getIsPayment() == 1 && trades.getIsRefund() != 120) {//当前订单付款成功订单传0，退款成功订单传1
            tradesList.put("status", 0);
        } else if (trades.getIsRefund() == 120) {
            tradesList.put("status", 1);
        } else {
            tradesList.put("status", 1);
        }
        tradesList.put("memberMobile", trades.getMemberMobile());//会员手机号码
        tradesList.put("poststyle", trades.getPostStyle());//购买方式，150：送货上门；160：门店自提；170：门店直购
        if (StringUtil.isEmpty(trades.getStore())) {
            tradesList.put("uid", 0);//门店编码
        } else {
            tradesList.put("uid", trades.getStore().getStoresNumber());//门店编码
        }
        tradesList.put("paystyle", trades.getPayStyle());//支付方式
        tradesList.put("mobile", trades.getRecevierMobile());
        tradesList.put("receiverName", trades.getRecevierName());
        tradesList.put("receiverAddress", trades.getReceiverAddress());
        tradesList.put("createTime", trades.getCreateTime());
        tradesList.put("totalFee", trades.getTotalFee());//商品金额
        tradesList.put("totalPay", trades.getTotalFee() + trades.getPostFee());//商品金额+运费
        tradesList.put("postFee", trades.getPostFee());//运费
        tradesList.put("discount", trades.getTotalFee() + trades.getPostFee() - trades.getRealPay());//优惠金额
        tradesList.put("realPay", trades.getRealPay());
        tradesList.put("employee_number", trades.getEmployee_number());
        // 增加优惠券信息ruleName,concessionNo,concessionType,operateTime,concessionView
        try {
            List<String> couponNames = trades.getDiscountList().stream().map(t -> t.get("ruleName").toString()).collect(Collectors.toList());
            tradesList.put("couponName", StringUtil.join(couponNames, ","));
        } catch (Exception e) {
            tradesList.put("couponName", null);
        }
        List<Map<String, Object>> orderList = new ArrayList<>();
        for (int i = 0; i < trades.getOrdersList().size(); i++) {//订单内容赋值
            Orders order = trades.getOrdersList().get(i);
            Map<String, Object> goods = new HashedMap();
            goods.put("id", i + 1);
            goods.put("gsc", order.getSpecifCation());
            goods.put("gcode", order.getGoodsCode());
            goods.put("gnum", order.getGoodsNum());
            if (order.getGoodsGifts() == 1) {
                goods.put("gprice", 0);
            } else {
                if (StringUtil.isEmpty(order.getGoodsFinalPrice())) {
                    goods.put("gprice", order.getGoodsPrice());
                } else {
                    goods.put("gprice", order.getGoodsFinalPrice());
                }
            }
            goods.put("isGift", order.getGoodsGifts());
            orderList.add(goods);
        }
        tradesList.put("orderList", orderList);//商品列表
        requestParams.put("tradesList", tradesList);
        String url = baseUrl + "/orders/push";
        LOGGER.info("海典商户推送订单信息{}，地址:{},订单信息:{}", siteId, url, requestParams.toString());
        Map<String, Object> result = JacksonUtils.json2map(OkHttpUtil.postJson(url, JacksonUtils.mapToJson(requestParams)));
        LOGGER.info("海典商户推送订单信息{}:订单号:{},返回信息:{}", siteId, tradesId, result.toString());
        if (siteId == 100271) {//国大的订单同时要推送给平安
            pushOrder_pingAN(siteId, trades, 0);
        }
        erpOrderLogMapper.insertSelectErpLog(siteId, url, JacksonUtils.mapToJson(requestParams), JacksonUtils.mapToJson(result));
        return result;
    }

    //    @TimeRequired
    private Map<String, Object> pushOrder_ruisen(Integer siteId, long tradesId, String baseUrl) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> tradeObj = tradesMapper.selectTradesListfromERP_new(siteId, tradesId);
        if (!"1".equals(tradeObj.get("payment").toString())) {
            result.put("code", -1);
            result.put("msg", "不是已付款订单");
            return result;
        }
        Integer status = 1;
        if (1 == Integer.parseInt(tradeObj.get("payment").toString()) && 120 == Integer.parseInt(tradeObj.get("refund").toString())) {
            status = 0; //退款成功（和天润，济生堂，天伦规则不一样）
        } else {
            status = 1;
        }
        List<Map> goodsInfos = ordersMapper.selectOrderListFromERP(siteId, Long.parseLong(tradeObj.get("tId").toString()));
        BigDecimal discount = new BigDecimal(tradeObj.get("totalFee").toString()).
            add(new BigDecimal(tradeObj.get("postFee").toString())).
            subtract(new BigDecimal(tradeObj.get("realPay").toString())).
            subtract(new BigDecimal(tradeObj.get("postageDiscount").toString()));//商品总优惠金额
        BigDecimal total = new BigDecimal(String.valueOf(tradeObj.get("totalFee")));
        AtomicDouble sum = new AtomicDouble(0.0);
        AtomicInteger count = new AtomicInteger(0);
        int goodsSize = goodsInfos.size();
        goodsInfos.forEach(goods -> {
            count.incrementAndGet();
            if (Integer.parseInt(goods.get("gprice").toString()) != 0 && total.compareTo(discount) >= 0) {
                BigDecimal goodsPrice = new BigDecimal(String.valueOf(goods.get("gprice")));
                BigDecimal discountPart = null;//商品优惠金额
                if (count.get() == goodsSize) {//最后一个商品价格
                    discountPart = (discount.subtract(new BigDecimal(Double.toString(sum.get()))))
                        .divide(new BigDecimal(String.valueOf(goods.get("gnum"))), 0, BigDecimal.ROUND_FLOOR);
                } else {
                    discountPart = goodsPrice.multiply(discount).divide(total, 0, BigDecimal.ROUND_FLOOR);
                    sum.addAndGet((discountPart.multiply(new BigDecimal(String.valueOf(goods.get("gnum"))))).doubleValue());
                }
                goods.put("gdiscount", discountPart);
                goods.put("gdisPrice", goodsPrice.subtract(discountPart));
            } else {
                goods.put("gdiscount", 0);
                goods.put("gdisPrice", 0);
            }
        });
        StringBuffer insertSQL = new StringBuffer("");
        for (Map good : goodsInfos) {
            Integer num = Integer.parseInt(good.get("gnum").toString());
            if (status == 0) {
                num = -num;
            }
            String insertsSQL = "insert into mdxsdd (djbh,xsdate,cinvcode,cinvstd,xsqty,xsdj,xsimoney,jsimoney,yhimoney," +
                "postFee,paystyle,receiverName,mobile,poststyle,receicerAddress,memberMobile,mdbh,bz) values(" +
                "'" + tradesId + "','" + tradeObj.get("createTime") + "','" + good.get("gcode") + "','" + good.get("gsc") + "'," + num + "," +
                new BigDecimal(good.get("gprice").toString()).multiply(new BigDecimal("0.01")) + "," +
                new BigDecimal(good.get("gprice").toString()).multiply(new BigDecimal("0.01")).multiply(new BigDecimal(num)) + "," +
                new BigDecimal(good.get("gdisPrice").toString()).multiply(new BigDecimal("0.01")).multiply(new BigDecimal(num)) + "," +
                (new BigDecimal(good.get("gprice").toString()).subtract(new BigDecimal(good.get("gdisPrice").toString()))).multiply(new BigDecimal("0.01")).multiply(new BigDecimal(num)) + "," +
                new BigDecimal(tradeObj.get("postFee").toString()).divide(new BigDecimal(goodsSize), 0, BigDecimal.ROUND_FLOOR).multiply(new BigDecimal("0.01")) + "," +
                "'" + tradeObj.get("paystyle") + "','" + tradeObj.get("receiverName") + "','" + tradeObj.get("mobile") + "'," + tradeObj.get("poststyle") + "," +
                "'" + tradeObj.get("receicerAddress") + "','" + tradeObj.get("memberMobile") + "','" + tradeObj.get("uid") + "','" + tradeObj.get("message") + "');";
            insertSQL.append(insertsSQL);
        }
        LOGGER.info("推送订单信息给瑞森" + insertSQL);
        int num = dataSourceConfig_ruiSen.getRuiSenJDBCTemplate().update(insertSQL.toString());
        if (num > 0) {
            result.put("code", 0);
        } else {
            result.put("code", -1);
        }
        return result;
    }

    /**
     * 德仁堂订单推送
     *
     * @param siteId
     * @param tradesId
     */
    public Map<String, Object> pushOrder_derentang(Integer siteId, Long tradesId, String baseUrl) throws Exception {
        Trades trades = tradesService.getTradesDetial(tradesId);
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("code", 0);
        Map<String, Object> tradesList = new HashedMap();
        tradesList.put("tId", tradesId);
        if (trades.getIsPayment() == 1 && trades.getIsRefund() != 120) {//当前订单付款成功订单传1，退款成功订单传0
            tradesList.put("status", 1);
        } else if (trades.getIsRefund() == 120) {
            tradesList.put("status", 0);
        } else {
            tradesList.put("status", 0);
        }
        tradesList.put("memberMobile", trades.getMemberMobile());//会员手机号码
        tradesList.put("poststyle", trades.getPostStyle());//购买方式，150：送货上门；160：门店自提；170：门店直购
        if (StringUtil.isEmpty(trades.getStore())) {
            tradesList.put("uid", 0);//门店编码
        } else {
            tradesList.put("uid", trades.getStore().getStoresNumber());//门店编码
        }
        tradesList.put("paystyle", trades.getPayStyle());//支付方式
        tradesList.put("mobile", trades.getRecevierMobile());
        tradesList.put("receiverName", trades.getRecevierName());
        tradesList.put("receiverAddress", trades.getReceiverAddress());
        tradesList.put("createTime", trades.getCreateTime());
        tradesList.put("totalFee", trades.getTotalFee());//商品金额
        tradesList.put("totalPay", trades.getTotalFee() + trades.getPostFee());//商品金额+运费
        tradesList.put("postFee", trades.getPostFee());//运费
        tradesList.put("discount", trades.getTotalFee() + trades.getPostFee() - trades.getRealPay());//优惠金额
        tradesList.put("realPay", trades.getRealPay());
        tradesList.put("employee_number", trades.getEmployee_number());
        // 增加优惠券信息ruleName,concessionNo,concessionType,operateTime,concessionView
        try {
            List<String> couponNames = trades.getDiscountList().stream().map(t -> t.get("ruleName").toString()).collect(Collectors.toList());
            tradesList.put("couponName", StringUtil.join(couponNames, ","));
        } catch (Exception e) {
            tradesList.put("couponName", null);
        }
        List<Map<String, Object>> orderList = new ArrayList<>();
        for (int i = 0; i < trades.getOrdersList().size(); i++) {//订单内容赋值
            Orders order = trades.getOrdersList().get(i);
            Map<String, Object> goods = new HashedMap();
            goods.put("id", i + 1);
            goods.put("gsc", order.getSpecifCation());
            goods.put("gcode", order.getGoodsCode());
            goods.put("gnum", order.getGoodsNum());
            if (order.getGoodsGifts() == 1) {
                goods.put("gprice", 0);
            } else {
                if (StringUtil.isEmpty(order.getGoodsFinalPrice())) {
                    goods.put("gprice", order.getGoodsPrice());
                } else {
                    goods.put("gprice", order.getGoodsFinalPrice());
                }
            }
            orderList.add(goods);
        }
        tradesList.put("orderList", orderList);//商品列表
        requestParams.put("tradesList", tradesList);
        String url = baseUrl + "/orders/pushOrder";
        LOGGER.info("德仁堂商户推送订单信息{}，地址:{},订单信息:{}", siteId, url, requestParams.toString());
        Map<String, Object> result = JacksonUtils.json2map(OkHttpUtil.postJson(url, JacksonUtils.mapToJson(requestParams)));
        LOGGER.info("德仁堂商户推送订单信息{}:订单号:{},返回信息:{}", siteId, tradesId, result.toString());
        erpOrderLogMapper.insertSelectErpLog(siteId, url, JacksonUtils.mapToJson(requestParams), JacksonUtils.mapToJson(result));
        return requestParams;
    }

    /**
     * 平安订单，保健品订单推送
     *
     * @param siteId
     */
    public void pushOrder_pingAN(Integer siteId, Trades trades, int status) throws Exception {
        try {
            Map<String, Object> pingAnERPMap = merchantERPMapper.selectMerchantERPInfo(999999);
            Map<String, Object> pinganOrder = ordersMapper.findPingAnOrderId(siteId, trades.getTradesId());
            Map<String, Object> tradesMap = new HashedMap();
            Integer tradesStatus = 0;
            if (status == 1) {
                tradesStatus = 1;
                trades = tradesService.getTradesDetial(trades.getTradesId());
            } else if (trades.getIsPayment() == 1 && trades.getIsRefund() != 120) {//当前订单付款成功订单传0，退款成功订单传-1
                tradesStatus = 0;
            } else if (trades.getIsRefund() == 120) {
                tradesStatus = -1;
            } else {
                tradesStatus = 0;
            }
            sendQCode(siteId, trades, pinganOrder, pingAnERPMap, tradesStatus);
            tradesMap.put("status", tradesStatus);
            Map<String, Object> memberInfoMap = memberInfoMapper.getMemberInfoByMobile(trades.getSiteId(), trades.getMemberMobile());
            if (!StringUtil.isEmpty(memberInfoMap) && (!memberInfoMap.containsKey("secondToken")) && StringUtil.isEmpty(memberInfoMap.get("secondToken"))) {
                return;//没有用户平安id
            }
            if (tradesStatus == 0) {
                if ((!memberInfoMap.containsKey("loginSource")) && (Integer.parseInt(memberInfoMap.get("loginSource").toString()) != 210)) {
                    return;//用户最后一次登陆来源不是平安
                }
            } else {
                List<Map<String, Object>> erpOrderList = erpOrderLogMapper.selectErpOrdersByTradesId(siteId, trades.getTradesId());
                if (CollectionUtils.isEmpty(erpOrderList)) {
                    return;//订单在付款成功时没有推送过，
                }
            }
            if ((!StringUtil.isEmpty(pinganOrder)) && pinganOrder.containsKey("orderNo") && (!StringUtil.isEmpty(pinganOrder.get("orderNo")))) {//是处方药订单
                return;//是处方药订单，不推
            }
            tradesMap.put("secondtoken", memberInfoMap.get("secondToken"));//平安会员id，手机号码可否？
            tradesMap.put("thirdPartyNo", pingAnERPMap.get("secretName"));//平安验证账户
            tradesMap.put("thirdPartyMM", pingAnERPMap.get("secretValue"));//平安验证密码
            tradesMap.put("thirdOrderId", trades.getTradesId());//51订单号
            tradesMap.put("dealer", trades.getSellerName());
            tradesMap.put("orderDate", trades.getCreateTime());
            tradesMap.put("postStyle", trades.getPostStyle());//购买方式，150：送货上门；160：门店自提；170：门店直购
            tradesMap.put("amount", new BigDecimal(trades.getRealPay().toString()).multiply(new BigDecimal("0.01")));
            tradesMap.put("url", tradesService.getShopwxUrl(trades.getSiteId()) + "/newOrder/orderCommonDetail?&tradesId=" + trades.getTradesId() + "");//订单详情页,使用商户的微信地址进行拼接
/*        tradesMap.put("memberMobile", trades.getMemberMobile());//会员手机号码
        tradesMap.put("paystyle", trades.getPayStyle());//支付方式*/
            List<Map<String, Object>> orderList = new ArrayList<>();
            for (int i = 0; i < trades.getOrdersList().size(); i++) {//订单内容赋值
                Orders order = trades.getOrdersList().get(i);
                Map<String, Object> goods = new HashedMap();
                goods.put("productId", order.getGoodsId());
                goods.put("goodsNo", order.getGoodsCode());
                goods.put("productName", order.getGoodsTitle());
                Map<String, Object> goodsMap = goodsMapper.getDefaultImagesAndCate(order.getSiteId(), order.getGoodsCode());
                if (goodsMap.containsKey("hash") && (!StringUtil.isEmpty(goodsMap.get("hash")))) {
                    String imageType = goodsMap.get("imageType").toString().equals("30") ? "gif" : goodsMap.get("imageType").toString().equals("20") ? "png" : "jpg";
                    goods.put("imageUrl", "https://jkosshash.oss-cn-shanghai.aliyuncs.com/" + goodsMap.get("hash") + "." + imageType);
                } else {
                    goods.put("imageUrl", "");
                }
                goods.put("productType", goodsMap.get("productType"));
                goods.put("cateName", goodsMap.get("cateName"));
                goods.put("originalAmount", goodsMap.get("drugOriginal"));
                goods.put("count", order.getGoodsNum());
                if (order.getGoodsGifts() == 1) {
                    goods.put("sellAmount", 0);//销售价
                } else {
                    if (StringUtil.isEmpty(order.getGoodsFinalPrice())) {
                        goods.put("sellAmount", new BigDecimal(order.getGoodsPrice().toString()).multiply(new BigDecimal("0.01")));//销售价
                    } else {
                        goods.put("sellAmount", new BigDecimal(order.getGoodsFinalPrice().toString()).multiply(new BigDecimal("0.01")));//销售价
                    }
                }
                orderList.add(goods);
            }
            tradesMap.put("products", orderList);//商品列表
            String url = pingAnERPMap.get("erpUrl").toString() + "healthProduct/saveHealthProduct";
            for (int i = 0; i < 3; i++) {
                try {
                    LOGGER.info("平安好医生保健品订单推送{}，地址:{},订单信息:{}", siteId, url, tradesMap.toString());
                    Map<String, Object> result = JacksonUtils.json2map(OkHttpUtil.postJson(url, JacksonUtils.mapToJson(tradesMap)));
                    LOGGER.info("平安好医生保健品订单推送{}:订单号:{},返回信息:{}", siteId, trades.getTradesId(), result.toString());
                    erpOrderLogMapper.insertSelectErpLog(siteId, url, JacksonUtils.mapToJson(tradesMap), JacksonUtils.mapToJson(result));
                    if (result.get("code").toString().equals("10000")) {
                        break;
                    }
                } catch (Exception e) {
                    LOGGER.info("平安订单推送有误循环{}，reason：{}", i, e.getMessage());
                    continue;
                }
            }
        } catch (Exception e) {
            LOGGER.info("平安订单推送有误{}", e.getMessage());
        }
    }

    /**
     * 平安订单，付款成功，处方药订单推送配送状态
     *
     * @param siteId
     */
    //付款成功，推送取货码
    public void sendQCode(Integer siteId, Trades trades, Map<String, Object> pinganOrderNo, Map<String, Object> pinganMap, Integer type) {
        if ((!StringUtil.isEmpty(pinganOrderNo)) && pinganOrderNo.containsKey("orderNo") && (!StringUtil.isEmpty(pinganOrderNo.get("orderNo")))) {//是处方药订单，
            Map<String, Object> requestParams = new HashMap<>();
            if (type == 1) {
                requestParams.put("step", 2);//备药步骤：1:备货,2:取货成功
            } else if (type == 0) {
                requestParams.put("step", 1);//备药步骤：1:备货,2:取货成功
            } else {
                return;
            }
            requestParams.put("orderNo", pinganOrderNo.get("orderNo"));
            requestParams.put("prescriptionNo", pinganOrderNo.get("prescriptionNo"));
            requestParams.put("qrCode", StringUtil.isEmpty(trades.getSelfTakenCode()) ? "" : EncryptUtils.base64EncodeToString(trades.getSelfTakenCode().getBytes()));//取货码
            requestParams.put("thirdPartyNo", pinganMap.get("secretName"));//平安验证账户
            requestParams.put("thirdPartyMM", pinganMap.get("secretValue"));//平安验证密码
            requestParams.put("status", 1);//备货完成
            requestParams.put("message", "");
            String url = pinganMap.get("erpUrl").toString() + "orderMessage/deliveryStatusPush";
            for (int i = 0; i < 3; i++) {
                try {
                    LOGGER.info("平安好医生配送推送{}，地址:{},配送信息:{}", siteId, url, requestParams.toString());
                    Map<String, Object> result = JacksonUtils.json2map(OkHttpUtil.postJson(url, JacksonUtils.mapToJson(requestParams)));
                    LOGGER.info("平安好医生{}:51订单号:{},返回信息:{}", siteId, trades.getTradesId(), result.toString());
                    erpOrderLogMapper.insertSelectErpLog(siteId, url, JacksonUtils.mapToJson(requestParams), JacksonUtils.mapToJson(result));
                    if (result.get("code").toString().equals("10000")) {
                        break;
                    }
                } catch (Exception e) {
                    LOGGER.info("平安好医生{}:51订单号:{},推送配送状态信息有误：{}", siteId, trades.getTradesId(), e.getMessage());
                }
            }
        } else {//非处方药不推送

        }
    }
}
