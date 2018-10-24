package com.jk51.modules.tpl.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.http.HttpClientManager;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.sms.SysType;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.BLogisticsOrder;
import com.jk51.model.Stores;
import com.jk51.model.account.models.AccountCommissionRate;
import com.jk51.model.map.Coordinate;
import com.jk51.model.order.Stockup;
import com.jk51.model.order.Trades;
import com.jk51.model.order.TradesExt;
import com.jk51.model.treat.MerchantTreat;
import com.jk51.modules.account.mapper.AccountCommissionRateMapper;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.map.service.MapService;
import com.jk51.modules.sms.service.CommonService;
import com.jk51.modules.sms.service.ZtSmsService;
import com.jk51.modules.sms.smsConfig.SmsEnum;
import com.jk51.modules.tpl.config.ImdadaConstant;
import com.jk51.modules.tpl.mapper.BLogisticsOrderMapper;
import com.jk51.modules.trades.mapper.StockupMapper;
import com.jk51.modules.trades.mapper.TradesExtMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.modules.trades.service.TradesDeliveryService;
import com.jk51.modules.trades.service.TradesService;
import com.jk51.modules.treat.mapper.MerchantMapper;
import org.apache.commons.collections.map.HashedMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ImdadaService {
    private static final Logger logger = LoggerFactory.getLogger(ImdadaService.class);

    @Value("${imdada.app_secret}")
    private String appSecret;

    @Value("${imdada.app_key}")
    private String appKey;

    @Value("${imdada.api_domain}")
    private String apiDomain;

    @Value("${imdada.allow_site}")
    private String allowSite;

    @Value("${imdada.notify_url}")
    private String notifyUrl;

    @Value("${imdada.source_id}")
    private String sourceId;

    @Autowired
    private StockupMapper stockupMapper;
    //json 序列化使用,本示例使用jackson
    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private BLogisticsOrderMapper bLogisticsOrderMapper;
    @Autowired
    TradesService tradesService;
    @Autowired
    private StoresService storesService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MapService mapService;
    @Autowired
    private ZtSmsService ztSmsService;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private CommonService commonService;

    /**
     * 获取城市code
     *
     * @return
     */
    public String getAPICityCode(Map requestMap, String cityName) {

        //根据需求按照文档构造请求参数
        Map<String, Object> apiParam = handleAPIParam(requestMap, "");
        String cityCode = "";
        //发送请求
        String response;
        String redisKey = "imdada_api_cityCode";
        try {
            response = stringRedisTemplate.opsForValue().get(redisKey);
            if (StringUtil.isEmpty(response)) {
                response = HttpClientManager.httpPostRequestUtf8(handleAPIURl(ImdadaConstant.ORDER_QUERY_CITYCODE_URL), toJson(apiParam));
                stringRedisTemplate.opsForValue().set(redisKey, response);
                stringRedisTemplate.expire(redisKey, 3, TimeUnit.MINUTES);
            }
            response = HttpClientManager.httpPostRequestUtf8(handleAPIURl(ImdadaConstant.ORDER_QUERY_CITYCODE_URL), toJson(apiParam));
            JSONObject respStr = (JSONObject) JSON.parse(response);
            if (respStr.getString("status").equals("success")) {
                JSONArray array = respStr.getJSONArray("result");
                for (int i = 0; i < array.size(); i++) {
                    if (cityName.indexOf(array.getJSONObject(i).getString("cityName")) != -1) {
                        return array.getJSONObject(i).getString("cityCode");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityCode;
    }

    /**
     * 处理城市名称
     *
     * @return
     */
    public String getAPICityName(Map requestMap, String cityName) {
        Map<String, Object> apiParam = handleAPIParam(requestMap, "");
        String response;
        String redisKey = "imdada_api_cityName";
        try {
            response = stringRedisTemplate.opsForValue().get(redisKey);
            if (StringUtil.isEmpty(response)) {
                response = OkHttpUtil.postJson(handleAPIURl(ImdadaConstant.ORDER_QUERY_CITYCODE_URL), toJson(apiParam));
                //HttpClientManager.httpPostRequestUtf8(handleAPIURl(ImdadaConstant.ORDER_QUERY_CITYCODE_URL), toJson(apiParam));
                stringRedisTemplate.opsForValue().set(redisKey, response);
                stringRedisTemplate.expire(redisKey, 3, TimeUnit.MINUTES);
            }
            JSONObject respStr = (JSONObject) JSON.parse(response);
            if (respStr.getString("status").equals("success")) {
                JSONArray array = respStr.getJSONArray("result");
                for (int i = 0; i < array.size(); i++) {
                    if (cityName.indexOf(array.getJSONObject(i).getString("cityName")) != -1) {
                        return array.getJSONObject(i).getString("cityName");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityName;
    }

    /**
     * 创建订单
     */
    @Transactional
    public Map<String, Object> createOrder(Map param) {
        MerchantTreat merchant = merchantMapper.getMerchant((param.get("tradesId") + "").substring(0, 6));
        if (!StringUtil.isEmpty(merchant.getImdada_flag()) && merchant.getImdada_flag() == 1) {
        } else {
            return resultHelper4mt(false, param.get("msg") + "");
        }

        param.put("sourceId", sourceId);
        String checkParam = mapKeyHelper(param, "tradesId", "storeId", "sourceId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper4mt(false, "参数 " + checkParam + " 不能为空！");
        }
        Trades trades = tradesService.getTradesByTradesId(Long.parseLong(param.get("tradesId") + ""));
        if (StringUtil.isEmpty(trades)) {
            return resultHelper4mt(false, "该订单不存在");
        }
//        if(allowSite.indexOf(trades.getSiteId() + "") == -1){
//            return resultHelper4mt(false, "该商户暂不支持达达物流");
//        }
        Stores stores = storesService.getStore(getMapInt(param, "storeId"), trades.getSiteId());
        if (StringUtil.isEmpty(stores)) {
            return resultHelper4mt(false, "该门店不存在");
        }
        String originShopId = handleOriginStoreId(stores, getMapString(param, "sourceId"));
        if (StringUtil.isEmpty(originShopId)) {
            return resultHelper4mt(false, "关联达达物流门店信息失败");
        }
        stores.setOrigin_shop_id(originShopId);

        boolean emptyFlag = strCheck(trades.getRecevierName(), trades.getReceiverAddress(), trades.getLat(), trades.getLng());
        if (!emptyFlag) {
            return resultHelper4mt(false, "参数错误");
        }

        Map result = new HashMap<>();
        param.put("origin_id", trades.getTradesId());
        String paramBody = toJson(handleAddOrderParam(trades, stores, param));
        //根据需求按照文档构造请求参数
        Map<String, Object> paramMap = handleAPIParam(param, paramBody);

        //发送请求
        String response;
        try {
            logger.info("####达达物流创建订单接口####tradesId{}", param.get("tradesId"));
            logger.info("####达达物流创建订单接口####参数为:{}", JacksonUtils.mapToJson(paramMap));
            response = HttpClientManager.httpPostRequestUtf8(handleAPIURl(ImdadaConstant.ORDER_ADD_URL), toJson(paramMap));
            logger.info("####达达物流创建订单接口####返回结果为:{}", response);
            result = JacksonUtils.json2map(response);
            if (result.get("status").equals("success")) {
                JSONObject item = JSON.parseObject(response);
                int fee = item.getJSONObject("result").getIntValue("fee");
                saveOrder2db(trades, stores, fee);
                return resultHelper4mt(true, "发货成功");
            } else {

                BLogisticsOrder bLogisticsOrder = bLogisticsOrderMapper.queryByOrderNumber(trades.getTradesId());
                if (!StringUtil.isEmpty(bLogisticsOrder) && bLogisticsOrder.getLogisticsId() == 1) {
                    failHandle(trades.getTradesId());
                }
                return resultHelper4mt(false, "发货失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("达达物流接口，创建订单异常！");
            return resultHelper4mt(false, "发货失败");
        }
    }

    public String handleOriginStoreId(Stores stores, String sourceId) {
        if (StringUtil.isEmpty(stores.getOrigin_shop_id())) {
            String originStoreIdId = stores.getSite_id() + "" + stores.getId();
            //String originStoreIdId = "11047059";//
            stores.setOrigin_shop_id(originStoreIdId);
            storesService.updateOriginStoreId(stores);
            Map param = new HashMap();
            param.put("siteId", stores.getSite_id());
            param.put("storeId", stores.getId());
            param.put("sourceId", sourceId);
            if (getMapString(addStore(param), "status").equals("success")) {
                return originStoreIdId;
            }
            return "";
        }
        return stores.getOrigin_shop_id();
    }

    public void saveOrder2db(Trades trades, Stores stores, int fee) {
        BLogisticsOrder bLogisticsOrder = new BLogisticsOrder();

        BLogisticsOrder exist = bLogisticsOrderMapper.selectByTradesId(String.valueOf(trades.getTradesId()));

        bLogisticsOrder.setStatus((byte) 0);
        bLogisticsOrder.setOrderNumber(trades.getTradesId());
        bLogisticsOrder.setLogisticsName("达达物流");
        bLogisticsOrder.setLogisticsId(2);
        bLogisticsOrder.setProvince(stores.getProvince());
        bLogisticsOrder.setCity(stores.getCity());
        bLogisticsOrder.setOrderTime(trades.getCreateTime());
        bLogisticsOrder.setSiteId(trades.getSiteId());
        bLogisticsOrder.setTotalFee(fee);
        bLogisticsOrder.setStoreId(trades.getTradesStore());
        bLogisticsOrder.setStoreName(stores.getName());

        if (StringUtil.isEmpty(exist)) {
            bLogisticsOrderMapper.insertSelective(bLogisticsOrder);
        } else {
            bLogisticsOrder.setId(exist.getId());
            bLogisticsOrderMapper.updateByPrimaryKey(bLogisticsOrder);
        }
    }

    private Map<String, Object> handleAddOrderParam(Trades trades, Stores store, Map param) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 15);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("origin_id", trades.getTradesId());
        paramMap.put("city_code", getAPICityCode(param, store.getCity()));
        paramMap.put("cargo_price", trades.getTotalFee() / 100);
        paramMap.put("is_prepay", 0);
        paramMap.put("expected_fetch_time", calendar.getTime().getTime() / 1000);
        paramMap.put("receiver_name", trades.getRecevierName());
        paramMap.put("receiver_address", trades.getReceiverAddress());
        paramMap.put("receiver_phone", trades.getRecevierMobile());
        paramMap.put("callback", notifyUrl);
        paramMap.put("shop_no", store.getOrigin_shop_id());

        paramMap.put("receiver_lat", trades.getLat());
        paramMap.put("receiver_lng", trades.getLng());

        Stockup stockup = stockupMapper.findByTradesId2(trades.getSiteId(), trades.getTradesId());
        paramMap.put("pickup_locker_code", StringUtil.isEmpty(stockup.getStockupId()) ? null : stockup.getStockupId());

        if (StringUtil.isEmpty(trades.getLng()) || trades.getLng() == 0.0 || trades.getLng() == 0) {
            Coordinate coordinate = mapService.geoCoordinate(trades.getReceiverAddress().replaceAll("\\s*", ""));
            paramMap.put("receiver_lat", coordinate.getLat());
            paramMap.put("receiver_lng", coordinate.getLng());
        }
        return paramMap;
    }

    /**
     * 查询订单状态
     *
     * @param param
     * @return
     */
    public Map<String, Object> queryOrder(Map param) {

        String checkParam = mapKeyHelper(param, "orderId", "sourceId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }

        Map result = new HashMap<>();
        param.put("order_id", getMapString(param, "orderId"));
        String paramBody = toJson(param);
        //根据需求按照文档构造请求参数
        Map<String, Object> paramMap = handleAPIParam(param, paramBody);

        //发送请求
        String response;
        try {
            logger.info("####达达物流查询订单状态接口####参数为:{}", JacksonUtils.mapToJson(paramMap));
            response = HttpClientManager.httpPostRequestUtf8(handleAPIURl(ImdadaConstant.ORDER_QUERY_STATUS_URL), toJson(paramMap));
            logger.info("####达达物流查询订单状态接口####返回结果:{}", response);
            result = JacksonUtils.json2map(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Object> addStore(Map param) {
        String checkParam = mapKeyHelper(param, "siteId", "storeId", "sourceId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        Stores stores = storesService.getStore(getMapInt(param, "storeId"), getMapInt(param, "siteId"));
        if (StringUtil.isEmpty(stores)) {
            return resultHelper(false, "该门店不存在");
        }
        Map result = new HashMap<>();
        String paramBody = toJson(handleAddStoreParam(param, stores));
        Map<String, Object> paramMap = handleAPIParam(param, paramBody);
        String response;
        try {
            logger.info("达达物流添加门店接口" + param.get("siteId") + param.get("storeId"));
            logger.info("####达达物流添加门店接口####参数为:{}", JacksonUtils.mapToJson(paramMap));
            response = HttpClientManager.httpPostRequestUtf8(handleAPIURl(ImdadaConstant.STORE_ADD), toJson(paramMap));
            logger.info("####达达物流添加门店接口####返回结果:{}", response);
            result = JacksonUtils.json2map(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List handleAddStoreParam(Map param, Stores stores) {
        List list = new ArrayList();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("station_name", stores.getName());
        paramMap.put("business", 5);//业务类型
        paramMap.put("city_name", getAPICityName(param, stores.getCity()));
        paramMap.put("area_name", stores.getCountry());
        paramMap.put("station_address", stores.getAddress());
        paramMap.put("lng", stores.getGaode_lng());
        paramMap.put("lat", stores.getGaode_lat());
        paramMap.put("contact_name", stores.getTel());
        paramMap.put("phone", stores.getTel());
        paramMap.put("origin_shop_id", stores.getOrigin_shop_id());
        list.add(paramMap);
        return list;
    }

    public Map<String, Object> updateStore(Map param) {
        String checkParam = mapKeyHelper(param, "siteId", "storeId", "sourceId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        Stores stores = storesService.getStore(getMapInt(param, "storeId"), getMapInt(param, "siteId"));
        if (StringUtil.isEmpty(stores)) {
            return resultHelper(false, "该门店不存在");
        }
        Map result = new HashMap<>();
        String paramBody = toJson(handleUpdateStoreParam(param, stores));
        Map<String, Object> paramMap = handleAPIParam(param, paramBody);
        String response;
        try {
            logger.info("####达达物流修改门店接口####参数为:{}", JacksonUtils.mapToJson(paramMap));
            response = HttpClientManager.httpPostRequestUtf8(handleAPIURl(ImdadaConstant.STORE_UPDATE), toJson(paramMap));
            logger.info("####达达物流修改门店接口####返回结果:{}", response);
            result = JacksonUtils.json2map(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Map handleUpdateStoreParam(Map param, Stores stores) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("station_name", stores.getName());
        paramMap.put("business", 5);//业务类型
        paramMap.put("city_name", getAPICityName(param, stores.getCity()));
        paramMap.put("area_name", stores.getCountry());
        paramMap.put("station_address", stores.getAddress());
        paramMap.put("lng", stores.getGaode_lng());
        paramMap.put("lat", stores.getGaode_lat());
        paramMap.put("contact_name", stores.getTel());
        paramMap.put("phone", stores.getTel());
        paramMap.put("origin_shop_id", stores.getOrigin_shop_id());
        return paramMap;
    }

    public Map<String, Object> queryStore(Map param) {
        Map result = new HashMap<>();
        String paramBody = toJson(param);
        Map<String, Object> paramMap = handleAPIParam(param, paramBody);
        String response;
        try {
            logger.info("####达达物流查询门店详情接口####参数为:{}", JacksonUtils.mapToJson(paramMap));
            response = HttpClientManager.httpPostRequestUtf8(handleAPIURl(ImdadaConstant.STORE_DETAIL), toJson(paramMap));
            logger.info("####达达物流查询门店详情接口####返回结果:{}", response);
            result = JacksonUtils.json2map(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void cb(Map param) {
        logger.info("####达达物流回调####");
        logger.info("####达达物流回调####");
        logger.info("####达达物流回调####");
        logger.info("####达达物流回调接口####参数####{}", JacksonUtils.mapToJson(param));
        //{"order_status":"1","cancel_reason":"","update_time":"1499990371","cancel_from":"0","signature":"b6c438e248d65f0d992a463f43a52b2a","dm_id":"0","order_id":"1001661499967901035","client_id":"267247621508723"}
        try {
            BLogisticsOrder item = new BLogisticsOrder();
            Long tradesId = Long.valueOf(getMapString(param, "order_id"));
            logger.info("####达达物流回调####tradesId" + tradesId);
            BLogisticsOrder exist = bLogisticsOrderMapper.queryByOrderNumber(tradesId);
            String orderId = String.valueOf(exist.getOrderNumber());
            int status = getMapInt(param, "order_status");
            int i = 0;
            //达达： 订单状态(待接单＝1 待取货＝2 配送中＝3 已完成＝4 已取消＝5 已过期＝7 指派单=8 可参考文末的状态说明）
            //数据库： 配送的状态 0：已通知 | 接收成功  1：已接单 | 系统已接单 2：已接单 | 已分配到骑手 3：以接单 | 骑手已到店 4：已取单 | 配送中 5：已送达 | 已送达  6：已取消 | 已取消 7：拒绝接单 | 异常
            switch (status) {
                case 1:
                    i = 0;
                    break;
                case 2:
                    i = 1;
                    try {
                        Trades t = tradesService.getTradesByTradesId(Long.valueOf(orderId));
                        logger.info("####执行物流更新状态####");
                        //tradesService.updateConfirmStatus(t, t.getTradesStatus(), CommonConstant.SOURCE_BUSINESS_SHIPPED);
                        tradesService.updateConfirmStatusExtra(t, t.getTradesStatus(), CommonConstant.SOURCE_BUSINESS_SHIPPED);
                        tradesService.sendorderSendNotice(t, "蜂鸟配送", orderId);//订单发货通知
                    } catch (BusinessLogicException e) {
                        logger.info("####执行物流更新状态失败####");
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    i = 4;
                    break;
                case 4:
                    i = 5;
                    //20170905确认送达才收服务费（地主确认）
                    Trades t = tradesService.getTradesByTradesId(Long.valueOf(orderId + ""));
                    updateO2OFreight(t.getSiteId(), t.getTradesId(), t.getRealPay(), "imdada");
                    //订单签收消息
                    tradesService.sendOrderSign(t);
                    break;
                case 5:

                    //失败
                    failHandle(tradesId);

                    i = 6;
                    break;
                case 7:

                    //失败
                    failHandle(tradesId);

                    i = 7;
                    break;
                case 8:
                    i = 7;
                    break;
            }


            if (StringUtil.isEmpty(exist)) {
                logger.info("####回调失败####");
                return;
            }

            exist.setStatus((byte) i);
            exist.setDiliveryman(getMapString(param, "dm_name"));
            String mobile = getMapString(param, "dm_mobile");
            if (!StringUtil.isEmpty((mobile))) {
                exist.setDistributionPhone(Long.valueOf(mobile));
            }

            Map<String, Object> log = new HashedMap();
            log.put("siteId", exist.getSiteId());
            log.put("orderNumber", exist.getOrderNumber());
            log.put("waybillNumber", exist.getWaybillNumber());
            log.put("status", i);
            log.put("description", param.get("cancel_reason"));
            bLogisticsOrderMapper.insertLog(log);
            bLogisticsOrderMapper.updateByPrimaryKey(exist);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> cancelOrder(Map param) {
        String checkParam = mapKeyHelper(param, "orderId", "sourceId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        Map result = new HashMap<>();
        String paramBody = toJson(handleCancelOrderParam(param));
        Map<String, Object> paramMap = handleAPIParam(param, paramBody);
        String response;
        try {
            logger.info("####达达物流取消订单接口####参数为:{}", JacksonUtils.mapToJson(paramMap));
            response = HttpClientManager.httpPostRequestUtf8(handleAPIURl(ImdadaConstant.ORDER_CANCEL), toJson(paramMap));
            logger.info("####达达物流取消订单接口####返回结果:{}", response);
            result = JacksonUtils.json2map(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Map handleCancelOrderParam(Map param) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("order_id", getMapString(param, "orderId"));
        //[{"reason":"没有达达接单","id":1},{"reason":"达达没来取货","id":2},{"reason":"达达态度太差","id":3},
        // {"reason":"顾客取消订单","id":4},{"reason":"订单填写错误","id":5},{"reason":"其他","id":10000}]

        Object reasonCodeItem = param.get("reasonCode");
        Object reasonValueItem = param.get("reasonValue");
        Integer reasonCode = StringUtil.isEmpty(reasonCodeItem) ? 4 : Integer.valueOf(reasonCodeItem.toString());
        String reasonValue = StringUtil.isEmpty(reasonValueItem) ? "" : reasonValueItem.toString();

        paramMap.put("cancel_reason_id", reasonCode);
        paramMap.put("cancel_reason", reasonValue);
        return paramMap;
    }

    public Map<String, Object> tmp(Map param) {
        Map result = new HashMap<>();
        String paramBody = toJson(param);
        Map<String, Object> paramMap = handleAPIParam(param, paramBody);
        String response;
        try {
            logger.info("####达达物流接口####参数为:{}", JacksonUtils.mapToJson(paramMap));
            response = HttpClientManager.httpPostRequestUtf8(handleAPIURl(param.get("url").toString()), toJson(paramMap));
            logger.info("####达达物流接口####返回结果:{}", response);
            result = JacksonUtils.json2map(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 达达物流调用失败后处理
     *
     * @param tradesId
     * @return
     */
    @Transactional
    public Map<Integer, String> failHandle(Long tradesId) {
        logger.info("####达达物流调用失败后处理####订单号####{}", tradesId);
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
            String word = "发货失败，订单号： " + tradesId + " ，收货人： " + userName + " 的订单发货失败，请及时处理。";
            logger.info("####达达物流调用失败后处理####发送短信####{}", word);
            if (!commonService.SendMessage(commonService.transformParam(trades.getSiteId(), sellerMobile, null, SysType.NEW_ORDER_FOR_MERCHANT,
                SmsEnum.LOGISTICS_SMS,tradesId.toString(),userName)).equals("0")) {
                logger.info("####信息发送失败####");
            }
            msgMap.put(0, "发送成功");
        }
        return msgMap;
    }

    public Map addStoreForce(String siteId, String storeId, String sourceId) {
        Stores stores = storesService.getStore(Integer.valueOf(storeId), Integer.valueOf(siteId));
        Map param = new HashMap();
        param.put("siteId", stores.getSite_id());
        param.put("storeId", storeId);
        param.put("sourceId", sourceId);

        Map addResult = addStore(param);
        if ("success".equals(addResult.get("status"))) {//添加门店成功后修改origin_shop_id
            String originStoreIdId = stores.getSite_id() + "" + stores.getId();
            stores.setOrigin_shop_id(originStoreIdId);
            storesService.updateOriginStoreId(stores);
        } else {
            logger.error("达达物流添加门店失败：" + siteId + stores.getId() + "," + JacksonUtils.mapToJson(addResult));
        }
        return addResult;
    }

    /**
     * 生成签名
     * 签名生成的通用步骤如下：
     * 第一步：将参与签名的参数按照键值(key)进行排序。
     * 第二步：将排序过后的参数进行key value字符串拼接。
     * 第三步：将拼接后的字符串首尾加上app_secret秘钥，合成签名字符串。
     * 第四步：对签名字符串进行MD5加密，生成32位的字符串。
     * 第五步：将签名生成的32位字符串转换为大写。
     *
     * @param requestMap
     * @return
     */
    public String getSign(Map<String, Object> requestMap) {
        //请求参数键值升序排序
        Collection<String> keySet = requestMap.keySet();
        List<String> list = new ArrayList<>(keySet);
        Collections.sort(list);

        //拼参数字符串。
        StringBuffer signStr = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            signStr.append(key + requestMap.get(key));
        }


        //MD5签名并校验
        String sign = encrypt(appSecret + signStr.toString() + appSecret);
        return sign.toUpperCase();
    }

    //根据业务需求按照文档构造请求参数
    private Map<String, Object> handleAPIParam(Map param, String paramBody) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("body", StringUtil.isEmpty(paramBody) ? "" : paramBody);  // 注意body是json字符串
        paramMap.put("format", "json");
        paramMap.put("timestamp", System.currentTimeMillis());
        paramMap.put("app_key", appKey);
        paramMap.put("v", "1.0");
        paramMap.put("source_id", getMapInt(param, "sourceId"));//

        //签名
        String sign = getSign(paramMap);
        paramMap.put("signature", sign);
        return paramMap;
    }

    /*
     * MD5 加密
     */
    public static String encrypt(String inbuf) {
        String s = null;
        char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(inbuf.getBytes("UTF-8"));
            byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
                // >>> 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;

    }

    // json 序列化
    public static <T> String toJson(T src) {
        try {
            if (null != mapper) {
                if (src instanceof String) {
                    return (String) src;
                } else {
                    return mapper.writeValueAsString(src);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String handleAPIURl(String url) {
        return apiDomain + url;
    }

    public String getMapString(Map param, String key) {
        return param.containsKey(key) ? param.get(key) + "" : "";
    }

    public int getMapInt(Map param, String key) {
        return (param.containsKey(key) && !StringUtil.isEmpty(param.get(key))) ? Integer.valueOf(param.get(key) + "") : 0;
    }

    public String mapKeyHelper(Map map, String... args) {
        for (String param : args) {
            if (!map.containsKey(param)) return param;
        }
        return "";
    }

    public boolean strCheck(Object... args) {
        for (Object arg : args) {
            if (StringUtil.isEmpty(arg)) {
                logger.info("达达物流接口，参数校验，{}为空", arg);
                return false;
            }
        }
        return true;
    }

    public Map resultHelper4mt(boolean flag, String str) {
        Map result = new HashedMap();
        result.put("addMsg", flag ? "true" : "false");
        result.put("error", str);
        return result;
    }

    public JSONObject resultHelper(boolean flag, String str) {
        JSONObject result = new JSONObject();
        result.put("status", flag ? "success" : "fail");
        result.put("error", str);
        return result;
    }

    public MapService getMapService() {
        return mapService;
    }

    @Autowired
    private AccountCommissionRateMapper accountCommissionRateMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private TradesDeliveryService tradesDeliveryService;
    @Autowired
    private TradesExtMapper tradesExtMapper;

    /**
     * 更新订单的O2O配送费用，如果订单选择了第三方配送的话(目前蜂鸟配送一家)，调用第三方配送服务接口返回成功之后调用(20170727产品定义去掉最低交易佣金为0.01元)
     *
     * @param siteId         商家ID
     * @param tradesId       订单交易ID
     * @param orderRealPrice 订单
     * @return true:成功，false:失败
     */
    public boolean updateO2OFreight(int siteId, long tradesId, int orderRealPrice, String deliveryType) {
        try {
            Trades trades = tradesMapper.getTradesByTradesId(tradesId);
            orderRealPrice = orderRealPrice - trades.getPostFee();
            //查询商家的交易佣金比例
            AccountCommissionRate acr = accountCommissionRateMapper.getCommissionRatById(siteId);
            //O2O配送费佣金比例
            double O2OCommissionRate = 0.00;
            if (acr != null) {
                O2OCommissionRate = acr.getShipping_fee_rate();
            } else {
                //如果查询不到，则兜底为0.01
                O2OCommissionRate = 0.05;
            }
            //配送费佣金(单位：分)=订单实际支付金额*配送费佣金比例
            double O2OCommission = (double) orderRealPrice * (O2OCommissionRate / 100);
            //根据小数点后一位四舍五入
            BigDecimal bd = new BigDecimal(O2OCommission);
            //四舍五入后返回支付佣金(单位：分)
            int O2OCommissionInt = bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            //如果支付佣金小于1分钱，则兜底为1分钱
            /*if (O2OCommissionInt < 1) {
                O2OCommissionInt = 1;
            }*/
            //如果支付佣金小于0分钱，则兜底为0分钱
            if (O2OCommissionInt < 0) {
                O2OCommissionInt = 0;
            }
            //o2o超过4-6公里向客户收的运费
            int freightCommission = trades.getFreightCommission();
            logger.info("freightCommission---------{}", freightCommission);
            O2OCommissionInt = O2OCommissionInt + freightCommission;
            //服务商订单
            if (trades.getIsServiceOrder() == 1) {
                //修改余额操作日志
                int O2OCommissionIntnew = 800;
                if ("ele".equals(deliveryType)) {
                    O2OCommissionIntnew = tradesDeliveryService.getBasicPrice("ele", trades.getReceiverCity());
                } else if ("mt".equals(deliveryType)) {
                    TradesExt tradesExt = tradesExtMapper.getByTradesId(trades.getTradesId());
                    O2OCommissionIntnew = tradesDeliveryService.calcMtPrice(trades.getReceiverCity(), tradesExt.getDistance() / 1000);
                }
                balanceService.insertBalanceDetail(siteId, 5, O2OCommissionIntnew, "订单运费", tradesId, null,null);
            }
            //更新b_trades表中的O2O配送费
            tradesMapper.updateO2OCommission(siteId, tradesId, O2OCommissionInt);
        } catch (Exception e) {
            logger.info("updateO2OFreight Exception:{}", e);
        }
        return false;
    }

    public boolean ce() {
        String str = "1000731496922110116";
        Trades t = tradesService.getTradesByTradesId(Long.valueOf(str));
        //return updateO2OFreight(t.getSiteId(), t.getTradesId(), t.getRealPay());
        return false;

    }

    public boolean lingF(long tradesId) {
        try {

            Trades trades = tradesMapper.getTradesByTradesId(tradesId);

            tradesService.updateConfirmStatusExtra(trades, trades.getTradesStatus(), CommonConstant.SOURCE_BUSINESS_SHIPPED);

            int orderRealPrice = trades.getRealPay() - trades.getPostFee();
            int siteId = trades.getSiteId();
            //查询商家的交易佣金比例
            AccountCommissionRate acr = accountCommissionRateMapper.getCommissionRatById(siteId);
            //O2O配送费佣金比例
            double O2OCommissionRate = 0.00;
            if (acr != null) {
                O2OCommissionRate = acr.getShipping_fee_rate();
            } else {
                //如果查询不到，则兜底为0.01
                O2OCommissionRate = 0.05;
            }
            //配送费佣金(单位：分)=订单实际支付金额*配送费佣金比例
            double O2OCommission = (double) orderRealPrice * (O2OCommissionRate / 100);
            //根据小数点后一位四舍五入
            BigDecimal bd = new BigDecimal(O2OCommission);
            //四舍五入后返回支付佣金(单位：分)
            int O2OCommissionInt = bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            //如果支付佣金小于1分钱，则兜底为1分钱
            /*if (O2OCommissionInt < 1) {
                O2OCommissionInt = 1;
            }*/
            //如果支付佣金小于0分钱，则兜底为0分钱
            if (O2OCommissionInt < 0) {
                O2OCommissionInt = 0;
            }
            //o2o超过4-6公里向客户收的运费
            int freightCommission = trades.getFreightCommission();
            logger.info("freightCommission---------{}", freightCommission);
            O2OCommissionInt = O2OCommissionInt + freightCommission;
            //更新b_trades表中的O2O配送费
            tradesMapper.updateO2OCommission(siteId, tradesId, O2OCommissionInt);
        } catch (Exception e) {
            logger.info("updateO2OFreight Exception:{}", e);
        }
        return false;
    }
}
