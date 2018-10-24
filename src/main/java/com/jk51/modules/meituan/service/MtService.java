package com.jk51.modules.meituan.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.BLogisticsOrder;
import com.jk51.model.Stores;
import com.jk51.model.map.Coordinate;
import com.jk51.model.order.Orders;
import com.jk51.model.order.QueryOrdersReq;
import com.jk51.model.order.Stockup;
import com.jk51.model.order.Trades;
import com.jk51.modules.esn.mapper.GoodsEsMapper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.map.service.MapService;
import com.jk51.modules.meituan.constants.MtConfig;
import com.jk51.modules.meituan.constants.RequestConstant;
import com.jk51.modules.meituan.request.CheckRequest;
import com.jk51.modules.meituan.request.CreateOrderByShopRequest;
import com.jk51.modules.meituan.sign.SignHelper;
import com.jk51.modules.meituan.util.DateUtil;
import com.jk51.modules.meituan.util.ParamBuilder;
import com.jk51.modules.meituan.vo.OpenApiGood;
import com.jk51.modules.meituan.vo.OpenApiGoods;
import com.jk51.modules.tpl.mapper.BLogisticsOrderMapper;
import com.jk51.modules.tpl.service.ImdadaService;
import com.jk51.modules.trades.mapper.StockupMapper;
import com.jk51.modules.trades.mapper.TradesExtMapper;
import com.jk51.modules.trades.service.DeliveryDispatchConstanct;
import com.jk51.modules.trades.service.TradesDeliveryService;
import com.jk51.modules.trades.service.TradesService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MtService {

    private static final Logger logger = LoggerFactory.getLogger(MtService.class);

    @Value("${imdada.source_id}")
    private String sourceId;

    @Autowired
    TradesService tradesService;
    @Autowired
    private StoresService storesService;
    @Autowired
    private StockupMapper stockupMapper;
    @Autowired
    private MapService mapService;
    @Autowired
    private BLogisticsOrderMapper bLogisticsOrderMapper;
    @Autowired
    private ImdadaService imdadaService;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private TradesDeliveryService tradesDeliveryService;
    @Autowired
    private GoodsEsMapper goodsEsMapper;

    public Map createOrderFmt(long tradesId, int storeId){
        Trades trades = tradesService.getTradesByTradesId(tradesId);
        Stores stores = storesService.getStore(storeId, trades.getSiteId());
        return createOrder(trades, stores);
    }

    public Map createOrder(Trades trades, Stores stores) {
        Map result = new HashMap();
        //Trades trades = tradesService.getTradesByTradesId(tradesId);
        if (StringUtil.isEmpty(trades)) {
            result.put("error", "发货异常");
            return result;
        }
//        if(allowSite.indexOf(trades.getSiteId() + "") == -1){
//            return resultHelper(false, "该商户暂不支持达达物流");
//        }
        //Stores stores = storesService.getStore(storeId, trades.getSiteId());

        CreateOrderByShopRequest request = buildMockRequestCreate(trades, stores);
        logger.info("mt-create-order" + trades.getTradesId());
        Map<String, String> params = ParamBuilder.convertToMap(request);

        try {
            //"code":110,"message":"送件地址超出当前可配送范围"
            JSONObject res = sendRequest(RequestConstant.ORDER_CREATE_BY_SHOP, params);
            String mtId = res.getJSONObject("data").getString("mt_peisong_id");
            this.save2db(trades, stores, mtId);
            result.put("addMsg", "true");
            return result;
        } catch (Exception e) {
            logger.error("error", e);
            return tradesDeliveryService.deliveryHandler(trades.getTradesId(), DeliveryDispatchConstanct.FLAG_MT,"发美团物流失败！"+e.getMessage());
        }
    }

    public void save2db(Trades trades, Stores stores, String mtId) {
        BLogisticsOrder bLogisticsOrder = new BLogisticsOrder();

        BLogisticsOrder exist = bLogisticsOrderMapper.selectByTradesId(String.valueOf(trades.getTradesId()));

        bLogisticsOrder.setStatus((byte) 0);
        bLogisticsOrder.setOrderNumber(trades.getTradesId());
        bLogisticsOrder.setLogisticsName("美团配送");
        bLogisticsOrder.setLogisticsId(3);
        bLogisticsOrder.setProvince(stores.getProvince());
        bLogisticsOrder.setCity(stores.getCity());
        bLogisticsOrder.setOrderTime(trades.getCreateTime());
        bLogisticsOrder.setSiteId(trades.getSiteId());
        bLogisticsOrder.setStoreId(trades.getTradesStore());
        bLogisticsOrder.setStoreName(stores.getName());
        bLogisticsOrder.setWaybillNumber(mtId);

        if (StringUtil.isEmpty(exist)) {
            bLogisticsOrderMapper.insertSelective(bLogisticsOrder);
        } else {
            bLogisticsOrder.setId(exist.getId());
            bLogisticsOrderMapper.updateByPrimaryKey(bLogisticsOrder);
        }
    }

    public JSONObject sendRequest(String url, Map params) throws Exception {
        String secret = MtConfig.SECRET;
        String sign = SignHelper.generateSign(params, secret);
        params.put("sign", sign);
        logger.info("mtReq:{}", params);
        String res = OkHttpUtil.postMap(url, params);
        logger.info("mtResp:{}", res);
        if (JSON.parseObject(res).getInteger("code") != 0) {
            goodsEsMapper.insertLog("0",JacksonUtils.mapToJson(params)+","+res,"配送日志");
            throw new Exception(JSON.parseObject(res).getString("message"));
        }
        return JSON.parseObject(res);
    }

    private CreateOrderByShopRequest buildMockRequestCreate(Trades trades, Stores stores) {
        CreateOrderByShopRequest request = new CreateOrderByShopRequest();
        request.setAppkey(MtConfig.APP_KEY);
        request.setTimestamp(DateUtil.unixTime());
        request.setVersion("1.0");

        // 设置订单号及配送服务标识
        request.setDeliveryId(trades.getTradesId());
        request.setOrderId(trades.getTradesId().toString());
        request.setDeliveryServiceCode(4011);// 快速达:4011

        // 设置测试门店 id，测试门店的坐标地址为 97235456,31065079（高德坐标），配送范围3km
        //request.setShopId("test_0001");//todo
        request.setShopId(String.valueOf(stores.getSite_id()) + String.valueOf(stores.getId()));//b_stores.site_id+b_stores.id

        // 设置取货人信息，请根据测试门店地址 在测试发单时合理设置送货地址
        request.setReceiverName(trades.getRecevierName());
        request.setReceiverAddress(trades.getReceiverAddress());
        request.setReceiverPhone(trades.getRecevierMobile());
        request.setReceiverLng(doubleFormatter(trades.getLng()));
        request.setReceiverLat(doubleFormatter(trades.getLat()));

        if (StringUtil.isEmpty(trades.getLng()) || trades.getLng() == 0.0 || trades.getLng() == 0) {
            Coordinate coordinate = mapService.geoCoordinate(trades.getReceiverAddress().replaceAll("\\s*", ""));
            request.setReceiverLng(doubleFormatter(coordinate.getLat()));
            request.setReceiverLng(doubleFormatter(coordinate.getLng()));
        }

        // 设置预计送达时间为1小时以后
        request.setExpectedDeliveryTime(DateUtil.unixTime() + 3600L);

        // 设置门店流水号，门店流水号为一天中单个门店的订单序号，方便骑手线下到门店取货
        Stockup stockup = stockupMapper.findByTradesId2(trades.getSiteId(), trades.getTradesId());
        request.setPoiSeq(StringUtil.isEmpty(stockup.getStockupId()) ? null : stockup.getStockupId());

        QueryOrdersReq queryOrdersReq = new QueryOrdersReq();
        queryOrdersReq.setTradesId(Long.toString(trades.getTradesId()));
        List<Trades> tradesList = null;
        List<OpenApiGood> goodsList = new ArrayList<>();
        BigDecimal goodsWeight=new BigDecimal(0);
        try {
            tradesList = tradesService.getTrades(queryOrdersReq);
            if (trades != null) {
                Trades trade = tradesList.get(0);
                List<Orders> orderss = trade.getOrdersList();
                for (int i = 0; i < orderss.size(); ++i) {
                    Orders orders = orderss.get(i);
                    OpenApiGood openApiGood = new OpenApiGood();
                    openApiGood.setGoodName(orders.getGoodsTitle());
                    openApiGood.setGoodCount(orders.getGoodsNum());
                    openApiGood.setGoodPrice(new BigDecimal(orders.getGoodsPrice() / 100));
                    goodsList.add(openApiGood);
                    Map<String,Object> result = goodsMapper.queryGoodsDetailByGoodId(orders.getGoodsId(),orders.getSiteId());
                    if(!StringUtil.isEmpty(result)&&!StringUtil.isEmpty(result.get("goods_weight"))){
                        goodsWeight=goodsWeight.add(new BigDecimal(result.get("goods_weight")+"").subtract(new BigDecimal(orders.getGoodsNum())));
                    }else {
                        goodsWeight=new BigDecimal(0.01);
                    }

                }
            }
        } catch (Exception e) {
            logger.info("美团下单异常：{}",e);
        }


        // 设置商品详情

        OpenApiGoods openApiGoods = new OpenApiGoods();
        openApiGoods.setGoods(goodsList);
        request.setGoodsDetail(openApiGoods);

        request.setGoodsWeight(goodsWeight);
        // 设置备注信息
        request.setNote(trades.getBuyerMessage());
        return request;
    }


    public void cb(Map map) {

        logger.info("mt cb");
        try {
            String str = JacksonUtils.mapToJson(map);
            logger.info("mt cb param:{}", str);
            JSONObject param = JSON.parseObject(JacksonUtils.mapToJson(map));
            BLogisticsOrder item = new BLogisticsOrder();
            Long tradesId = Long.valueOf(param.getString("order_id"));
            logger.info("#### mt cb####tradesId" + tradesId);
            BLogisticsOrder exist = bLogisticsOrderMapper.queryByOrderNumber(tradesId);
            exist.setWaybillNumber(param.getString("mt_peisong_id"));
            String orderId = String.valueOf(exist.getOrderNumber());

//            0：待调度
//            20：已接单
//            30：已取货
//            50：已送达
//            99：已取消
            int status = param.getIntValue("status");
            int i = 0;
            switch (status) {
                case 0:
                    i = 0;
                    break;
                case 20:
                    i = 1;
                    try {
                        Trades t = tradesService.getTradesByTradesId(Long.valueOf(orderId));
                        tradesService.updateConfirmStatusExtra(t, t.getTradesStatus(), CommonConstant.SOURCE_BUSINESS_SHIPPED);
                        tradesService.sendorderSendNotice(t, "美团", orderId);//订单发货通知
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                    }
                    break;
                case 30:
                    i = 4;
                    break;
                case 50:
                    i = 5;
                    //20170905确认送达才收服务费（地主确认）
                    Trades t = tradesService.getTradesByTradesId(Long.valueOf(orderId + ""));
                    imdadaService.updateO2OFreight(t.getSiteId(), t.getTradesId(), t.getRealPay(),"mt");
                    //订单签收消息
                    tradesService.sendOrderSign(t);
                    break;
                case 99:
                    i = 6;
                    //失败
                    failHandle(tradesId);

                    tradesDeliveryService.deliveryHandler(tradesId, DeliveryDispatchConstanct.FLAG_MT,"配送美团物流失败");

                    break;
            }


            if (StringUtil.isEmpty(exist)) {
                logger.info("mt cb error");
                return;
            }

            exist.setStatus((byte) i);
            exist.setDiliveryman(param.getString("courier_name"));
            String mobile = param.getString("courier_phone");
            if (!StringUtil.isEmpty((mobile))) {
                exist.setDistributionPhone(Long.valueOf(mobile));
            }

            Map<String, Object> log = new HashedMap();
            log.put("siteId", exist.getSiteId());
            log.put("orderNumber", exist.getOrderNumber());
            log.put("waybillNumber", exist.getWaybillNumber());
            log.put("status", i);
            log.put("description", param.getString("cancel_reason"));
            bLogisticsOrderMapper.insertLog(log);
            bLogisticsOrderMapper.updateByPrimaryKey(exist);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void check(long tradesId) {
        Trades trades = tradesService.getTradesByTradesId(tradesId);
        CheckRequest request = buildMockRequestCheck(trades);
        Map<String, String> params = ParamBuilder.convertToMap(request);
        try {
            sendRequest(RequestConstant.ORDER_CHECK_DELIVERY_ABILITY, params);
        } catch (Exception e) {
            logger.error("error{}", e);
        }
    }

    private CheckRequest buildMockRequestCheck(Trades trades) {
        CheckRequest request = new CheckRequest();
        request.setAppkey(MtConfig.APP_KEY);
        request.setTimestamp(DateUtil.unixTime());
        request.setVersion("1.0");
        /**
         * 设置配送服务编码
         *
         * 光速达:4001
         * 快速达:4011
         * 及时达:4012
         * 集中送:4013
         * 当天达:4021
         */
        request.setDeliveryServiceCode(4011);
        // 设置测试门店 id，测试门店的坐标地址为 97235456,31065079（高德坐标），配送范围3km
        request.setShopId("test_0001");//todo
        // 收件人地址，最长不超过 512 个字符
        request.setReceiverAddress(trades.getReceiverAddress());
        //收件人经度（高德坐标），高德坐标 *（ 10 的六次方），如 116398419
        request.setReceiverLng(doubleFormatter(trades.getLng()));
        //收件人纬度（高德坐标），高德坐标 *（ 10 的六次方），如 39985005
        request.setReceiverLat(doubleFormatter(trades.getLat()));
        //预留字段，方便以后扩充校验规则，check_type = 1
        request.setCheckType(1);
        //模拟发单时间，时区为 GMT+8，当前距离 Epoch（1970年1月1日) 以秒计算的时间，即 unix-timestamp。
        request.setMockOrderTime(Long.parseLong(DateUtil.unixTime() + ""));

        return request;
    }

    @Transactional
    public Map<Integer, String> failHandle(Long tradesId) {
        logger.info("mt fail{}", tradesId);
        Trades trades = tradesService.getTradesByTradesId(tradesId);
        //达达失败把配送信息和订单不关联
        //达达失败并且b_logistics_order的logistics_id=2（达达）
        BLogisticsOrder bLogisticsOrder = bLogisticsOrderMapper.queryByOrderNumber(tradesId);


        bLogisticsOrder.setOrderNumber(-tradesId);
        bLogisticsOrderMapper.updateByWayBill(bLogisticsOrder);

        //失败后修改b_trades.O2O_freight
        bLogisticsOrderMapper.updateTradeso2o(tradesId);

        Map<Integer, String> msgMap = new HashMap<>();

        if (!StringUtil.isEmpty(trades)) {
            String userName = trades.getRecevierName();
            String sellerMobile = trades.getSellerMobile();
//            String word = "发货失败，订单号： " + tradesId + " ，收货人： " + userName + " 的订单发货失败，请及时处理。";
//            logger.info("####达达物流调用失败后处理####发送短信####{}", word);
//            if (!ztSmsService.SendMessage(trades.getSiteId(), word, sellerMobile).equals("0")) {
//                logger.info("####信息发送失败####");
//            }
            msgMap.put(0, "发送成功");
        }
        return msgMap;
    }

    public int doubleFormatter(double d) {
        String str = String.valueOf(d);
        if (str.indexOf(".") == -1) {
            return 1000000 * Integer.parseInt(str);
        }
        if (str.endsWith(".0")) {
            return 1000000 * Integer.parseInt(str.replace(".0", ""));
        }
        String suffix = str.substring(str.indexOf(".") + 1);
        int suffixLen = suffix.length();
        String result;
        if (suffixLen == 6) {
            result = str.replace(".", "");
        } else if (suffixLen > 6) {
            result = str.substring(0, str.indexOf(".")) + suffix.substring(0, 6);
        } else {
            result = str.substring(0, str.indexOf(".")) + suffix + zeroFormatter(6 - suffixLen);
        }
        return Integer.parseInt(result);
    }

    public String zeroFormatter(int len) {
        if (len <= 0) return "";
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < len; i++) {
            str.append("0");
        }
        return str.toString();
    }

    public String mapKeyHelper(Map map, String... args) {
        for (String param : args) {
            if (!map.containsKey(param)) return param;
        }
        return "";
    }

}
