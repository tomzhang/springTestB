package com.jk51.modules.tpl.service;

import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.BLogisticsOrder;
import com.jk51.model.Stores;
import com.jk51.model.map.Coordinate;
import com.jk51.model.order.Stockup;
import com.jk51.model.order.Trades;
import com.jk51.model.treat.MerchantTreat;
import com.jk51.modules.esn.mapper.GoodsEsMapper;
import com.jk51.modules.goods.library.ResultMap;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.map.service.MapService;
import com.jk51.modules.tpl.config.ElemeOpenConfig;
import com.jk51.modules.tpl.config.RequestConstant;
import com.jk51.modules.tpl.mapper.BLogisticsOrderMapper;
import com.jk51.modules.tpl.request.AddStoreRequest;
import com.jk51.modules.tpl.request.CancelOrderRequest;
import com.jk51.modules.tpl.request.ElemeCreateOrderRequest;
import com.jk51.modules.tpl.request.ElemeQueryOrderRequest;
import com.jk51.modules.tpl.sign.OpenSignHelper;
import com.jk51.modules.tpl.util.JsonUtils;
import com.jk51.modules.tpl.util.RandomUtils;
import com.jk51.modules.trades.mapper.StockupMapper;
import com.jk51.modules.trades.service.DeliveryDispatchConstanct;
import com.jk51.modules.trades.service.TradesDeliveryService;
import com.jk51.modules.trades.service.TradesService;
import com.jk51.modules.treat.mapper.MerchantMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class EleService {
    private static final Log logger = LogFactory.getLog(EleService.class);


    @Autowired
    private BLogisticsOrderMapper bLogisticsOrderMapper;

    @Autowired
    private ElemeOpenConfig elemeOpenConfig;

    @Autowired
    private ImdadaService imdadaService;

    @Autowired
    private TradesService tradesService;
    @Autowired
    private StockupMapper stockupMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private StoresService storesService;

    @Value("${imdada.source_id}")
    private String sourceId;

    @Value("${ele.api_url}")
    public String ELE_API_URL;

    @Autowired
    private MapService mapService;

    @Autowired
    private GoodsEsMapper goodsEsMapper;

    @Autowired
    private TradesDeliveryService tradesDeliveryService;


    //error_code说明(返回信息中包括error_code则当错误处理,后续可能会新增编码)
    public final static Map<String,String> errorCodeItem = new HashMap() {{
        put("SCHEDULE_ORDER_OUT_OF_TIME_ERROR", "预订单超时异常");
        put("CARRIER_OFFLINE_ERROR", "超出配送商营业时间");
        put("ORDER_OUT_OF_DISTANCE_ERROR", "订单超区");
        put("OVER_RANGE_MAX_DISTANCE_ERROR", "订单超出步行最大距离限制（4KM）");
        put("ORDER_OUT_OF_WEIGHT", "订单超重");
        put("ORDER_OUT_OF_LOAD_ERROR", "订单超出运力");
        put("NO_CARRIER_ERROR", "当前暂无骑手接单,请稍后重试");
        put("ORDER_OUT_OF_SERVICE", "运单超服务范围");
        put("ORDER_OUT_OF_TIME_ERROR", "订单超时");
        put("MERCHANT_INTERRUPT_DELIVERY_ERROR", "商户原因中断配送");
        put("MERCHANT_FOOD_ERROR", "商家出货问题");
        put("MERCHANT_CALL_LATE_ERROR", "呼叫配送晚");
        put("USER_NOT_ANSWER_ERROR", "用户不接电话");
        put("USER_RETURN_ORDER_ERROR", "用户退单");
        put("USER_ADDRESS_ERROR", "用户地址错误");
        put("DELIVERY_OUT_OF_SERVICE", "超出服务范围");
        put("SYSTEM_MARKED_ERROR", "系统自动标记异常--订单超过3小时未送达");
        put("CARRIER_REMARK_EXCEPTION_ERROR", "骑手标记异常");
        put("OTHER_ERROR", "其他");
        put("NORMAL_ORDER_OUT_OF_TIME_ERROR", "众包无骑手接单");
        put("NORMAL_ORDER_OUT_OF_TIME_ERROR", "众包无骑手接单");
        put("SYSTEM_ERROR", "系统错误");
        put("UNKNOW_ERROR", "未知错误");
        put("ORDER_REPETITION", "订单重复");
        put("CURRENT_STATUS_NOT_ALLOW_CANCEL", "当前订单不允许取消");
        put("USER_CANCEL", "用户发起取消");
        put("SYSTEM_CANCEL", "系统取消");
        put("MERCHANT_CANCEL", "商户发起取消");
        put("CARRIER_CANCEL", "配送商发起取消");
        put("DELIVERY_TIMEOUT", "配送超时,系统标记异常");
        put("REJECT_ORDER", "蜂鸟拒单");
        put("FOR_UPDATE_TIP", "加小费取消");
    }};

    public final static Map<String,String> errorMsgItem = new HashMap() {{
        put("transport_tel", "门店联系方式（只支持手机号,400开头电话以及座机号码）");
        put("chain_store_code", "门店编号");
        put("transport_name", "门店名称");
        put("transport_address", "门店地址");
        put("transport_longitude", "门店坐标经度");
        put("transport_latitude", "门店坐标纬度");
        put("order_total_amount", "订单总金额");
        put("order_actual_amount", "客户需要支付的金额");
        put("order_weight", "订单总重量（kg）");
        put("order_remark", "用户备注");
        put("is_invoiced", "是否需要发票");
        put("chain_store_code", "门店编号");
    }};

    /**
     *  获取Token
     * @return
     */
    public Map<String,Object> getToken(){
        String appId=elemeOpenConfig.getAppId();
        String secretKey=elemeOpenConfig.getSecretKey();
        Map<String,Object> result = new HashMap<String,Object>();
        String url = ELE_API_URL + RequestConstant.obtainToken;

        List<BasicNameValuePair> params = new ArrayList<>();
        String salt = String.valueOf(RandomUtils.getInstance().generateValue(1000, 10000));
        String sig = OpenSignHelper.generateSign(appId, salt, secretKey);

        // 请求token
        Map<String, Object> paramsmap= new HashMap<String,Object>();
        paramsmap.put("app_id", appId);
        paramsmap.put("salt", salt);
        paramsmap.put("signature", sig);
        String tokenRes = "";
        try {
           tokenRes = OkHttpUtil.getMap(url, paramsmap);
                   //HttpClient.doHttpGet(url, paramsmap);
           //tokenRes= MapUtils.sentURL(url+"?app_id="+appId+"&salt="+salt+"&signature="+sig);
           //tokenRes = HttpClientManager.getResponseString(HttpClientManager.httpGetRequest(url, paramsmap),null);
            result=JacksonUtils.json2map(tokenRes);
        } catch (Exception e) {
            logger.error("请求蜂鸟 token出现异常", e);
        }
        return result;
    }

    /**
     * 取消订单
     * @param map
     * @return
     */
    public Map<String,Object> cancelOrder(Map<String,String> map){
        String appId=elemeOpenConfig.getAppId();
        String secretKey=elemeOpenConfig.getSecretKey();
        Map<String,Object> result = new HashMap<String,Object>();
        String url = ELE_API_URL+ RequestConstant.orderCancel;
        String partner_order_code = map.get("partneOrderCode"); //推单时 订单号
        Map<String,Object> maptoten=this.getToken();
        String token;
        if("200".equals(maptoten.get("code"))){
            Map<String,Object> datas=(Map<String,Object>)maptoten.get("data");
            token=datas.get("access_token").toString();
        }else{
            return ResultMap.errorResult(maptoten.get("msg").toString());
        }

        String description =  map.get("reasonValue");//原因
        Object reasonCodeItem =  map.get("reasonCode");
        Integer reasonCode = (StringUtil.isEmpty(reasonCodeItem)||"0".equals(reasonCodeItem.toString())) ? 3 : Integer.valueOf(reasonCodeItem.toString());

        CancelOrderRequest.CancelOrderRequstData data = new CancelOrderRequest.CancelOrderRequstData();
        if(StringUtil.isNotEmpty(description)){
            data.setOrder_cancel_description(description);
        }
        data.setOrder_cancel_reason_code(2);

        //订单取消编码（0:其他, 1:联系不上商户, 2:商品已经售完, 3:用户申请取消, 4:运力告知不配送 让取消订单, 5:订单长时间未分配, 6:接单后骑手未取件）
        data.setOrder_cancel_code(reasonCode);

        data.setPartner_order_code(partner_order_code);
        data.setOrder_cancel_time(new Date().getTime());

        CancelOrderRequest cancelOrderRequest = new CancelOrderRequest();
        cancelOrderRequest.setData(data);

        int salt = RandomUtils.getInstance().generateValue(1000, 10000);

        Map<String, Object> sigStr = new LinkedHashMap<>();      // 注意添加的顺序, 应该如下面一样各个key值顺序一致
        sigStr.put("app_id", appId);
        sigStr.put("access_token", token);        // 需要使用前面请求生成的token
        try {
            sigStr.put("data", cancelOrderRequest.getData());
        } catch (IOException e) {
            logger.error("获取取消订单数据异常", e);
        }
        sigStr.put("salt", salt);

        // 生成签名
        String sig = OpenSignHelper.generateBusinessSign(sigStr);
        cancelOrderRequest.setSignature(sig);

        cancelOrderRequest.setApp_id(appId);
        cancelOrderRequest.setSalt(salt);

        String requestJson = null;
        try {
            requestJson = JsonUtils.getInstance().objectToJson(cancelOrderRequest);
        } catch (IOException e) {
            logger.error("拼接json出现异常", e);
        }
        //url = url + RequestConstant.orderCancel;
        try {
            String res = OkHttpUtil.postJson(url, requestJson);
            result.put("result",res);
            logger.info(String.format("取消蜂鸟订单返回结果: %s", res));
        } catch (Exception e) {
            logger.error("取消订单出现异常", e);
        }
        return result;
    }
   /* public boolean createOrder(HttpServletRequest request,Trades trades, Stores stores,ElemeCreateOrderRequest.ItemsJson[] items){
        Map<String,Object> map=MycreateOrder(request,trades,stores,items);
        logger.info("蜂鸟接口返回："+map.toString()+"------------"+("true".equals(map.get("addMsg"))));
        if("true".equals(map.get("addMsg"))){
           return true;
        }
        return false;
    }*/
    /**
     * 创建订单
     * @param trades
     * @param items
     * @return
     */
    public Map<String,Object> MycreateOrder(Trades trades, Stores stores,ElemeCreateOrderRequest.ItemsJson[] items){
        logger.info("蜂鸟创建订单--- "+trades.getTradesId());
        Map<String,Object> result = new HashMap<String,Object>();
        String appId=elemeOpenConfig.getAppId();
        String secretKey=elemeOpenConfig.getSecretKey();
        if(StringUtil.isEmpty(trades)){
//            return ResultMap.errorResult("订单不能为空");
            result.put("error",result.get("订单不能为空"));
            return result;
        }
        if(StringUtil.isEmpty(items)){
//            return ResultMap.errorResult("订单商品必填项");
            result.put("error",result.get("订单商品必填项"));
            return result;
        }
        /*if("100166".equals(trades.getSiteId())){
            //调用达达物流接口
            Map param = new HashMap();
            param.put("tradesId", trades.getTradesId());
            param.put("storeId", stores.getId());
            param.put("sourceId", sourceId);
            imdadaService.createOrder(param);
            result.put("addMsg","true");
            return result;
        }*/
        Map<String,Object> maptoten=this.getToken();
        String token;
        if("200".equals(maptoten.get("code"))){
            Map<String,Object> data=(Map<String,Object>)maptoten.get("data");
            token=data.get("access_token").toString();
        }else{
            //只在九洲大药房同时使用达达和蜂鸟，其他商户仅使用蜂鸟。(产品定义)
  //          MerchantTreat merchant = merchantMapper.getMerchant(trades.getSiteId()+"");
//            if(!StringUtil.isEmpty(merchant.getImdada_flag())&&merchant.getImdada_flag()==1){
//            //if(trades.getSiteId().equals(100166)||100166==trades.getSiteId()){
//                Map param = new HashMap();
//                param.put("tradesId", trades.getTradesId());
//                param.put("storeId", stores.getId());
//                param.put("sourceId", sourceId);
//                imdadaService.createOrder(param);
//                result.put("addMsg","true");
//            }

            return tradesDeliveryService.deliveryHandler(trades.getTradesId(), DeliveryDispatchConstanct.FLAG_ELE,"配送蜂鸟失败，获取access_token失败！");
        }
        if(StringUtil.isEmpty(stores.getGaode_lng())){
//            return ResultMap.errorResult("门店的经度必填项");
            result.put("error",result.get("门店的经度必填项"));
            return result;
        }
        if(StringUtil.isEmpty(stores.getGaode_lng())){
//            return ResultMap.errorResult("门店的经度必填项");
            result.put("error",result.get("门店的经度必填项"));
            return result;
        }
        if(StringUtil.isEmpty(trades.getLng())){
//            return ResultMap.errorResult("收货人经度必填项");
            result.put("error",result.get("收货人经度必填项"));
            return result;
        }

        if(StringUtil.isEmpty(trades.getLat())){
//            return ResultMap.errorResult("收货人纬度必填项");
            result.put("error",result.get("收货人纬度必填项"));
            return result;
        }
        if(StringUtil.isEmpty(stores.getAddress())){
//            return ResultMap.errorResult("门店的地址必填项");
            result.put("error",result.get("门店的地址必填项"));
            return result;
        }

        String transport_name = trades.getSellerName();
        if(StringUtil.isNotEmpty(stores.getName())){
            transport_name = stores.getName();
        }
        if(StringUtil.isEmpty(transport_name)){
//            return ResultMap.errorResult("门店名称必填项");
            result.put("error",result.get("门店名称必填项"));
            return result;
        }
        String transport_address = stores.getAddress();
        String transport_tel = stores.getTel();//取货点联系方式, 只支持手机号,400开头电话以及座机号码
        /*if(StringUtil.isNotEmpty(trades.getSellerPhone())){
            transport_tel=trades.getSellerPhone();
        }*/
        if(StringUtil.isEmpty(transport_tel)){
//            return ResultMap.errorResult("取货点联系方式必填项");
            result.put("error",result.get("取货点联系方式必填项"));
            return result;
        }

        if(StringUtil.isEmpty(trades.getRecevierName())){
//            return ResultMap.errorResult("收货人姓名必填项");
            result.put("error",result.get("收货人姓名必填项"));
            return result;
        }


        if(StringUtil.isEmpty(trades.getReceiverAddress())){
//            return ResultMap.errorResult("收货人地址必填项");
            result.put("error",result.get("收货人地址必填项"));
            return result;
        }
        String transport_longitude = stores.getGaode_lng();
        String transport_latitude = stores.getGaode_lat();//
        String transport_remark = trades.getBuyerMessage();//取货点备注

        String receiver_address = trades.getReceiverAddress();
        String receiver_name = trades.getRecevierName();
        String receiver_primary_phone = trades.getReceiverPhone();
        if(StringUtil.isNotEmpty(trades.getRecevierMobile())){
            receiver_primary_phone = trades.getRecevierMobile();
        }
        if(StringUtil.isEmpty(receiver_primary_phone)){
            //return ResultMap.errorResult("收货人联系方式必填项");
            result.put("error",result.get("收货人联系方式必填项"));
            return result;
        }
        double receiver_longitude = trades.getLng();
        double receiver_latitude = trades.getLat();
        if(StringUtil.isEmpty(trades.getLng()) || trades.getLng() == 0.0 || trades.getLng() == 0){
            Coordinate coordinate = mapService.geoCoordinate(trades.getReceiverAddress().replaceAll("\\s*",""));
            receiver_longitude = coordinate.getLng();
            receiver_latitude = coordinate.getLat();
        }
        String partner_remark =  trades.getSellerMemo();//商户备注信息

        String partner_order_code = String.valueOf(trades.getTradesId());

        String notify_url =elemeOpenConfig.getNotify_url();
                //request.getHeader("host")+"/tpl/notifyOrderStatus";
        //notify_url = "http://100030.weixin-test.51jk.com/ele/eletest";
        //notify_url = "http://service-dev.51jk.com/ele/notifyOrderStatus";
        String order_total_amount =(trades.getTotalFee()/100)+"";//订单总金额（不包含商家的任何活动以及折扣的金额）
        String order_actual_amount = "0";//客户需要支付的金额
        String order_weight = String.valueOf(items.length);//订单总重量（kg），营业类型选定为果蔬生鲜、商店超市、其他三类时必填，大于0kg并且小于等于6kg
        String order_remark = trades.getBuyerMessage();//用户备注

        int is_invoiced = 0;//是否需要发票, 0:不需要, 1:需要

        int is_agent_payment = 0;//是否需要ele代收 0:否 1:是
        //String require_payment_pay = map.get("requirePaymentPay");
        int goods_count = items.length;


        /**
         * 将参数构造成一个json
         */
        ElemeCreateOrderRequest.ElemeCreateRequestData data = new ElemeCreateOrderRequest.ElemeCreateRequestData();

        /**
         * transportInfo
         */
        ElemeCreateOrderRequest.TransportInfo  transportInfo = new ElemeCreateOrderRequest.TransportInfo();
        transportInfo.setTransport_name(transport_name);
        transportInfo.setTransport_address(transport_address);
        transportInfo.setTransport_tel(transport_tel);



        transportInfo.setTransport_longitude(new BigDecimal(transport_longitude));
        transportInfo.setTransport_latitude(new BigDecimal(transport_latitude));
        transportInfo.setTransport_remark(transport_remark);
        transportInfo.setPosition_source(3);//取货点经纬度来源, 1:腾讯地图, 2:百度地图, 3:高德地图

        /**
         * receiverInfo
         */
        ElemeCreateOrderRequest.ReceiverInfo receiverInfo = new ElemeCreateOrderRequest.ReceiverInfo();
        receiverInfo.setReceiver_address(receiver_address);
        receiverInfo.setReceiver_name(receiver_name);
        receiverInfo.setReceiver_primary_phone(receiver_primary_phone);
        receiverInfo.setReceiver_longitude(new BigDecimal(receiver_longitude));
        receiverInfo.setReceiver_latitude(new BigDecimal(receiver_latitude));
        receiverInfo.setPosition_source(3);//取货点经纬度来源, 1:腾讯地图, 2:百度地图, 3:高德地图



        data.setTransport_info(transportInfo);
        data.setReceiver_info(receiverInfo);
        data.setItems_json(items);

        data.setPartner_remark(partner_remark);
        data.setPartner_order_code(partner_order_code);
        data.setNotify_url(notify_url);

        /**
         * 1: 蜂鸟配送, 未向饿了么物流平台查询过站点的订单，支持两小时送达
         * 2: 定点次日达, 提前向饿了么物流平台查询过配送站点的订单，支持次日送达
         */
        data.setOrder_type(1);    // 订单类型
        data.setOrder_total_amount(new BigDecimal(order_total_amount));
        data.setOrder_actual_amount(new BigDecimal(order_actual_amount));
        data.setOrder_weight(new BigDecimal(order_weight));
        data.setOrder_remark(order_remark);
        data.setIs_invoiced(is_invoiced); // 是否需要发票0：不需要；1：需要
        //data.setInvoice(invoice);
        data.setOrder_payment_status(1);
        data.setOrder_payment_method(1);
        data.setIs_agent_payment(is_agent_payment); // 是否需要承运商代收货款 0：否 1：是
        //data.setRequire_payment_pay(new BigDecimal(require_payment_pay));
        data.setGoods_count(goods_count);
        data.setRequire_receive_time(LocalDateTime.now().plusHours(1).
                atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());   // 预计送达时间 要大于当前时间

        data.setChain_store_code(stores.getSite_id() + "" + stores.getId()); //门店编号
        data.setOrder_add_time(new Date().getTime());
        Stockup stockup = stockupMapper.findByTradesId2(trades.getSiteId(), trades.getTradesId());
        data.setSerial_number(StringUtil.isEmpty(stockup.getStockupId())?null:stockup.getStockupId());


        ElemeCreateOrderRequest elemeCreateOrderRequest  = new ElemeCreateOrderRequest();
        elemeCreateOrderRequest.setData(data);

        int salt = RandomUtils.getInstance().generateValue(1000, 10000);
        elemeCreateOrderRequest.setApp_id(appId);
        elemeCreateOrderRequest.setSalt(salt);

        /**
         * 生成签名
         */
        Map<String, Object> sigStr = new LinkedHashMap<>();      // 注意添加的顺序, 应该如下面一样各个key值顺序一致
        sigStr.put("app_id", appId);
        sigStr.put("access_token", token);        // 需要使用前面请求生成的token
        try {
            sigStr.put("data", elemeCreateOrderRequest.getData());
        } catch (IOException e) {
            logger.error("获取json出现异常", e);
        }
        sigStr.put("salt", salt);
        // 生成签名
        String sig = OpenSignHelper.generateBusinessSign(sigStr);
        elemeCreateOrderRequest.setSignature(sig);

        String requestJson = null;
        try {
            requestJson = JsonUtils.getInstance().objectToJson(elemeCreateOrderRequest);
        } catch (IOException e) {
            logger.error("拼接json出现异常", e);
        }
        logger.info(String.format("request json is %s", requestJson));

        String url = ELE_API_URL + RequestConstant.orderCreate;
        String res = "";
        try {
             res=OkHttpUtil.postJson(url, requestJson);
            logger.info("蜂鸟返回---"+res);
            result=JacksonUtils.json2map(res);
            /*if(!"200".equals(result.get("code"))){
                return ResultMap.errorResult(result.get("msg").toString());
            }*/
            BLogisticsOrder bLogisticsOrder=new BLogisticsOrder();

            if("200".equals(result.get("code"))){
                // 接收到200只表示已接收到请求,不等于已接单,只有接收到"系统已接单"的回调才表示接单成功
                logger.info("蜂鸟接收到请求");
                BLogisticsOrder bLogisticsOrderList=new BLogisticsOrder();
                bLogisticsOrder.setStatus((byte) 0);
                bLogisticsOrder.setOrderNumber(trades.getTradesId());
                bLogisticsOrder.setLogisticsName("蜂鸟配送");
                bLogisticsOrder.setLogisticsId(1);
                bLogisticsOrder.setProvince(stores.getProvince());
                bLogisticsOrder.setCity(stores.getCity());
                bLogisticsOrder.setSiteId(trades.getSiteId());
                bLogisticsOrder.setOrderTime(trades.getCreateTime());
                bLogisticsOrder.setTotalFee(trades.getTotalFee());
                bLogisticsOrder.setStoreId(trades.getTradesStore());
                //bLogisticsOrder.setOperatorId(Integer.parseInt( request.getSession().getAttribute("id").toString()));
               // bLogisticsOrder.setNotifyMobile( request.getSession().getAttribute("id").toString());
                bLogisticsOrder.setStoreName(stores.getName());

                //bLogisticsOrder.setWaybillNumber(data.getPartner_order_code());//运单号

                BLogisticsOrder exist = bLogisticsOrderMapper.selectByTradesId(String.valueOf(trades.getTradesId()));
                if(StringUtil.isEmpty(exist)){
                    bLogisticsOrderMapper.insertSelective(bLogisticsOrder);
                }else {
                    bLogisticsOrder.setId(exist.getId());
                    bLogisticsOrderMapper.updateByPrimaryKey(bLogisticsOrder);
                }
                logger.info("保存发货日志");
                result.put("addMsg","true");
            }else{//调用达达物流接口
//                //只在九洲大药房同时使用达达和蜂鸟，其他商户仅使用蜂鸟。(产品定义)
//                MerchantTreat merchant = merchantMapper.getMerchant(trades.getSiteId()+"");
//                if(!StringUtil.isEmpty(merchant.getImdada_flag())&&merchant.getImdada_flag()==1){
//                //if(trades.getSiteId().equals(100166)||100166==trades.getSiteId()){
//                    Map param = new HashMap();
//                    param.put("tradesId", trades.getTradesId());
//                    param.put("storeId", stores.getId());
//                    param.put("sourceId", sourceId);
//                    imdadaService.createOrder(param);
//                    result.put("addMsg","true");
//                }else {
//                    result.put("error",result.get("msg"));
//                    return result;
//                }
                String msg="";
                for (String key : errorMsgItem.keySet()) {
                    msg=result.get("msg").toString().replace(key,errorMsgItem.get(key));
                }
                return tradesDeliveryService.deliveryHandler(trades.getTradesId(), DeliveryDispatchConstanct.FLAG_ELE,msg);
            }
        } catch (Exception e) {
            logger.error("推送订单出现异常", e);
        }
        return result;
    }

    /**
     * 蜂鸟返回状态
     * @param item
     * @return
     */
    public void updatbLogisticsOrderOld(JSONObject item)  {
        String orderNumber = item.get("partner_order_code") + "";//商户自己的订单号
        Integer order_status= item.getInteger("order_status");
        String open_order_code = item.get("open_order_code") + "";//蜂鸟配送开放平台返回的订单号
        String carrier_driver_name=item.getString("carrier_driver_name");
        String carrier_driver_phone=item.getString("carrier_driver_phone");
        String description=item.getString("description");
        String cancel_reason=item.getString("cancel_reason");
        String error_code=item.getString("error_code");
        if(StringUtil.isEmpty(description)){
            description=errorCodeItem.get(error_code);
        }
        if(!StringUtil.isEmpty(orderNumber)){
            BLogisticsOrder bLogisticsOrder=bLogisticsOrderMapper.selectByTradesId(orderNumber);

            if(!StringUtil.isEmpty(bLogisticsOrder)){
                if(!StringUtil.isEmpty(carrier_driver_name)){
                    bLogisticsOrder.setDiliveryman(carrier_driver_name);
                }
                if(!StringUtil.isEmpty(carrier_driver_phone)){
                    bLogisticsOrder.setDistributionPhone(Long.valueOf(carrier_driver_phone));
                }
                if(!StringUtil.isEmpty(error_code)){
                    bLogisticsOrder.setErrorCode(error_code);
                }
                if(!StringUtil.isEmpty(description)){
                    bLogisticsOrder.setErrorCode(description);
                }

                bLogisticsOrder.setWaybillNumber(open_order_code);

                if(StringUtil.isEmpty(order_status)){
                    return;
                }
                Trades t = tradesService.getTradesByTradesId(Long.valueOf(orderNumber));
                 if(order_status.equals(1)){
                     //更新备货状态
                     bLogisticsOrder.setStatus(Byte.valueOf("1"));

                     logger.info("####执行物流更新状态####");
                     try {
                         //tradesService.updateConfirmStatus(t, t.getTradesStatus(), CommonConstant.SOURCE_BUSINESS_SHIPPED);
                         tradesService.updateConfirmStatusExtra(t, t.getTradesStatus(), CommonConstant.SOURCE_BUSINESS_SHIPPED);
                         tradesService.sendorderSendNotice(t, "蜂鸟配送", open_order_code);//订单发货通知
                     } catch (BusinessLogicException e) {
                         logger.info("####执行物流更新状态失败####");
                         e.printStackTrace();
                     }
                 }else if(order_status.equals(20)){
                     bLogisticsOrder.setStatus(Byte.valueOf("2"));
                 }else if(order_status.equals(80)){
                     bLogisticsOrder.setStatus(Byte.valueOf("3"));
                 }else if(order_status.equals(2)){
                     bLogisticsOrder.setStatus(Byte.valueOf("4"));
                 }else if(order_status.equals(3)){
                     imdadaService.updateO2OFreight(t.getSiteId(), t.getTradesId(), t.getRealPay(),"ele");
                     bLogisticsOrder.setStatus(Byte.valueOf("5"));
                     //订单签收消息
                     tradesService.sendOrderSign(t);

                     //如果蜂鸟没有推送已接单，在送达时修改订单状态
                     if (t.getTradesStatus().equals(CommonConstant.WAIT_SELLER_SHIPPED)) {
                         try {
                             logger.info("####蜂鸟没有推送已接单，送达时修改订单状态####");
                             tradesService.updateConfirmStatusExtra(t, t.getTradesStatus(), CommonConstant.SOURCE_BUSINESS_SHIPPED);
                         } catch (BusinessLogicException e) {
                             logger.info("####蜂鸟没有推送已接单，送达时修改订单状态异常####", e);
                         }
                     }

                 }else if(order_status.equals(4)){
                     bLogisticsOrder.setStatus(Byte.valueOf("6"));
                 }else if(order_status.equals(5)){
                     bLogisticsOrder.setStatus(Byte.valueOf("7"));
                     logger.info("蜂鸟配送SiteId---"+t.getSiteId());
                     //只在九洲大药房同时使用达达和蜂鸟，其他商户仅使用蜂鸟。(产品定义)
                     MerchantTreat merchant = merchantMapper.getMerchant(t.getSiteId()+"");
                     if(!StringUtil.isEmpty(merchant.getImdada_flag())&&merchant.getImdada_flag()==1){
                     //if(t.getSiteId().equals(100166)||100166==t.getSiteId()||"100166".equals(orderNumber.substring(0,5))){
                         logger.info("蜂鸟配送SiteId---进入达达"+t.getSiteId());
                         //调用达达接口
                         Map imParam = new HashMap();
                         imParam.put("tradesId", orderNumber);
                         imParam.put("storeId", bLogisticsOrderMapper.queryTrade(orderNumber));
                         imParam.put("sourceId", sourceId);
                         imdadaService.createOrder(imParam);
                         return;
                     }
                 }
                Map<String,Object> log=new HashedMap();
                log.put("siteId",bLogisticsOrder.getSiteId());
                log.put("orderNumber",bLogisticsOrder.getOrderNumber());
                log.put("waybillNumber",open_order_code);
                log.put("status",bLogisticsOrder.getStatus());
                log.put("description",description);
                bLogisticsOrderMapper.insertLog(log);
                bLogisticsOrderMapper.updateByPrimaryKey(bLogisticsOrder);
            }
        }
    }

    /**
     * 蜂鸟返回状态
     * @param item
     * @return
     */
    public void updatbLogisticsOrder(JSONObject item,int type)  {
        String orderNumber = item.get("partner_order_code") + "";//商户自己的订单号
        Integer order_status= item.getInteger("order_status");
        String open_order_code = item.get("open_order_code") + "";//蜂鸟配送开放平台返回的订单号
        String carrier_driver_name=item.getString("carrier_driver_name");
        String carrier_driver_phone=item.getString("carrier_driver_phone");
        String description=item.getString("description");
        String cancel_reason=item.getString("cancel_reason");
        String error_code=item.getString("error_code");
        if(StringUtil.isEmpty(description)){
            description=errorCodeItem.get(error_code);
        }
        if(!StringUtil.isEmpty(orderNumber)){
            BLogisticsOrder bLogisticsOrder=bLogisticsOrderMapper.selectByTradesId(orderNumber);

            if(!StringUtil.isEmpty(bLogisticsOrder)){
                if(!StringUtil.isEmpty(carrier_driver_name)){
                    bLogisticsOrder.setDiliveryman(carrier_driver_name);
                }
                if(!StringUtil.isEmpty(carrier_driver_phone)){
                    bLogisticsOrder.setDistributionPhone(Long.valueOf(carrier_driver_phone));
                }
                if(!StringUtil.isEmpty(error_code)){
                    bLogisticsOrder.setErrorCode(error_code);
                }
                if(!StringUtil.isEmpty(description)){
                    bLogisticsOrder.setErrorCode(description);
                }

                bLogisticsOrder.setWaybillNumber(open_order_code);

                if(StringUtil.isEmpty(order_status)){
                    return;
                }
                Trades t = tradesService.getTradesByTradesId(Long.valueOf(orderNumber));
                if(order_status.equals(1)){
                    //更新备货状态
                    bLogisticsOrder.setStatus(Byte.valueOf("1"));

                    logger.info("####执行物流更新状态####");
                    try {
                        //tradesService.updateConfirmStatus(t, t.getTradesStatus(), CommonConstant.SOURCE_BUSINESS_SHIPPED);
                        tradesService.updateConfirmStatusExtra(t, t.getTradesStatus(), CommonConstant.SOURCE_BUSINESS_SHIPPED);
                        tradesService.sendorderSendNotice(t, "蜂鸟配送", open_order_code);//订单发货通知
                    } catch (BusinessLogicException e) {
                        logger.info("####执行物流更新状态失败####");
                        e.printStackTrace();
                    }
                }else if(order_status.equals(20)){
                    bLogisticsOrder.setStatus(Byte.valueOf("2"));
                }else if(order_status.equals(80)){
                    bLogisticsOrder.setStatus(Byte.valueOf("3"));
                }else if(order_status.equals(2)){
                    bLogisticsOrder.setStatus(Byte.valueOf("4"));
                }else if(order_status.equals(3)){
                    imdadaService.updateO2OFreight(t.getSiteId(), t.getTradesId(), t.getRealPay(),"ele");
                    bLogisticsOrder.setStatus(Byte.valueOf("5"));
                    //订单签收消息
                    tradesService.sendOrderSign(t);

                    //如果蜂鸟没有推送已接单，在送达时修改订单状态
                    if (t.getTradesStatus().equals(CommonConstant.WAIT_SELLER_SHIPPED)) {
                        try {
                            logger.info("####蜂鸟没有推送已接单，送达时修改订单状态####");
                            tradesService.updateConfirmStatusExtra(t, t.getTradesStatus(), CommonConstant.SOURCE_BUSINESS_SHIPPED);
                        } catch (BusinessLogicException e) {
                            logger.info("####蜂鸟没有推送已接单，送达时修改订单状态异常####", e);
                        }
                    }

                }else if(order_status.equals(4)){
                    bLogisticsOrder.setStatus(Byte.valueOf("6"));
                }else if(order_status.equals(5)){
                    bLogisticsOrder.setStatus(Byte.valueOf("7"));
                }
                Map<String,Object> log=new HashedMap();
                log.put("siteId",bLogisticsOrder.getSiteId());
                log.put("orderNumber",bLogisticsOrder.getOrderNumber());
                log.put("waybillNumber",open_order_code);
                log.put("status",bLogisticsOrder.getStatus());
                log.put("description",description);
                bLogisticsOrderMapper.insertLog(log);
                bLogisticsOrderMapper.updateByPrimaryKey(bLogisticsOrder);
                if(order_status.equals(5)){
                    if(type==0){
                        tradesDeliveryService.deliveryHandler(t.getTradesId(), DeliveryDispatchConstanct.FLAG_ELE,description);
                    }
                }
            }
        }
    }

    public void createImOrder(String orderNumber, long storeId){
        Map imParam = new HashMap();
        imParam.put("tradesId", orderNumber);
        imParam.put("storeId", storeId);
        imParam.put("sourceId", sourceId);
        imdadaService.createOrder(imParam);
    }

    public  Map<String,Object> queryOrder(String partner_order_code)  {
        String appId=elemeOpenConfig.getAppId();
        String secretKey=elemeOpenConfig.getSecretKey();
        Map<String,Object> result = new HashMap<String,Object>();
        String url = ELE_API_URL;

        ElemeQueryOrderRequest request = new ElemeQueryOrderRequest();
        ElemeQueryOrderRequest.ElemeQueryRequestData data = new ElemeQueryOrderRequest.ElemeQueryRequestData();
        data.setPartner_order_code(partner_order_code);
        request.setData(data);

        Map<String,Object> maptoten=this.getToken();
        String token;
        if("200".equals(maptoten.get("code"))){
            Map<String,Object> datas=(Map<String,Object>)maptoten.get("data");
            token=datas.get("access_token").toString();
        }else{
            return ResultMap.errorResult(maptoten.get("msg").toString());
        }
        int salt = RandomUtils.getInstance().generateValue(1000, 10000);
        request.setApp_id(appId);
        request.setSalt(salt);
        try {
        /**
         * 生成签名
         */
        Map<String, Object> sigStr = new LinkedHashMap<>();      // 注意添加的顺序, 应该如下面一样各个key值顺序一致
        sigStr.put("app_id", appId);
        sigStr.put("access_token", token);        // 需要使用前面请求生成的token
        sigStr.put("data", request.getData());
        sigStr.put("salt", salt);
        // 生成签名
        String sig = OpenSignHelper.generateBusinessSign(sigStr);
        request.setSignature(sig);

        String requestJson = JsonUtils.getInstance().objectToJson(request);

        url = url + RequestConstant.orderQuery;

            result.put("result",OkHttpUtil.postJson(url, requestJson));

        } catch (Exception e) {
            logger.error("查询订单出现异常", e);
        }
        return result;
    }
    public  Map<String,Object> selectbLogisticsOrder(String orderNumber)  {
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("bLogisticsOrder",bLogisticsOrderMapper.selectByTradesId(orderNumber));
        result.put("bLogisticsOrderList",bLogisticsOrderMapper.selectByTradesIdLog(orderNumber));
        return result;
    }

    public Map<String, Object> canceld(Map<String, String> map) {
        String tradesId = map.get("orderId") + "";
        BLogisticsOrder item = bLogisticsOrderMapper.queryByOrderNumber(Long.valueOf(tradesId));
        Map result = new HashMap();
        if (StringUtil.isEmpty(item)) {
            result.put("status", "fail");
            return result;
        }
        try {
            if (item.getLogisticsId().intValue() == 1) {
                result = JacksonUtils.json2map(cancelOrder(map).get("result") + "");
                result.put("status", result.get("code").equals("200") ? "success" : "fail");
            } else if(item.getLogisticsId().intValue() == 2) {
                map.put("sourceId", sourceId);
                result = imdadaService.cancelOrder(map);
            }

            Map<String, Object> log = new HashedMap();
            log.put("siteId", item.getSiteId());
            log.put("orderNumber", "-" + item.getOrderNumber());
            log.put("waybillNumber", item.getWaybillNumber());
            log.put("description", result.get("msg") + "");
            log.put("status", 6);
            bLogisticsOrderMapper.insertLog(log);

        } catch (Exception e) {
            logger.info("insertLog 取消订单异常：{}", e);
            return result;
        }
        if ("success".equals(result.get("status"))) {
            try {
                bLogisticsOrderMapper.hideRecord(item.getSiteId(), item.getOrderNumber());
                bLogisticsOrderMapper.hideRecordLog(item.getSiteId(), item.getOrderNumber());
                Trades trades = tradesService.getTradesByTradesId(item.getOrderNumber());
                tradesService.cancelShipping(trades);
            } catch (BusinessLogicException e) {
                logger.info("cancelShipping 取消订单异常：{}", e);
                return result;
            }
        }
        return result;
    }

    public Map<String,Object> addStore(String siteId, String storeId){
        String appId=elemeOpenConfig.getAppId();
        Map<String,Object> result = new HashMap();
        String url = ELE_API_URL+ RequestConstant.addStore;
        Map<String,Object> maptoten=this.getToken();
        String token;
        if("200".equals(maptoten.get("code"))){
            Map<String,Object> datas=(Map<String,Object>)maptoten.get("data");
            token=datas.get("access_token").toString();
        }else{
            return ResultMap.errorResult(maptoten.get("msg").toString());
        }

        Stores stores = storesService.getStore(Integer.valueOf(storeId), Integer.valueOf(siteId));

        storesService.updateStoreEleStatus(stores);

        AddStoreRequest.AddStoreRequestData data = new AddStoreRequest.AddStoreRequestData();
//        data.setName(stores.getName());
        data.setAddress(stores.getAddress());
        //data.setContactPhone(stores.getTel());
        data.setLatitude(stores.getGaode_lat());
        data.setLongitude(stores.getGaode_lng());

        data.setChain_store_name(stores.getName());
        //data.setChain_store_code(stores.getStores_number());
        data.setChain_store_code(stores.getSite_id() + "" + stores.getId());
        data.setPosition_source(3);//坐标属性（0:未知, 1:腾讯地图, 2:百度地图, 3:高德地图）
        data.setService_code(1);//配送服务(1:蜂鸟配送, 2:蜂鸟优送)
        data.setContact_phone(stores.getTel());


        AddStoreRequest addStoreRequest = new AddStoreRequest();
        addStoreRequest.setData(data);

        int salt = RandomUtils.getInstance().generateValue(1000, 10000);

        Map<String, Object> sigStr = new LinkedHashMap<>();      // 注意添加的顺序, 应该如下面一样各个key值顺序一致
        sigStr.put("app_id", appId);
        sigStr.put("access_token", token);        // 需要使用前面请求生成的token
        try {
            sigStr.put("data", addStoreRequest.getData());
        } catch (Exception e) {
            logger.error("获取添加门店数据异常", e);
        }
        sigStr.put("salt", salt);

        // 生成签名
        String sig = OpenSignHelper.generateBusinessSign(sigStr);
        addStoreRequest.setSignature(sig);

        addStoreRequest.setApp_id(appId);
        addStoreRequest.setSalt(salt);

        String requestJson = null;
        try {
            requestJson = JsonUtils.getInstance().objectToJson(addStoreRequest);
        } catch (IOException e) {
            logger.error("拼接json出现异常", e);
        }

        try {
            logger.info("####蜂鸟物流，添加门店####门店编号："+stores.getStores_number());
            String res = OkHttpUtil.postJson(url, requestJson);
            result.put("result",res);
            logger.info("####蜂鸟物流，添加门店####返回结果："+res);
        } catch (Exception e) {
            logger.error("####蜂鸟物流，添加门店####异常信息：", e);
        }
        return result;
    }

    public int getMapInt(Map param, String key) {
        return (param.containsKey(key) && !StringUtil.isEmpty(param.get(key))) ? Integer.valueOf(param.get(key) + "") : 0;
    }

    public Map storeDeliveryHandle(String deliveryFlag, String siteId, String storeIds) {
        try {
            List<Map> resultList = new ArrayList<>();
            for (int i = 0; i < storeIds.split(",").length; i++) {
                String storeId = storeIds.split(",")[i];
                Map param = new HashMap();
                param.put("siteId", siteId);
                param.put("storeId", storeId);
                Map map;
                Stores stores = storesService.getStore(Integer.valueOf(storeId), Integer.valueOf(siteId));
                if ("1".equals(deliveryFlag)) {//蜂鸟
                    map = addStore(siteId, storeId);
                } else {
                    map = imdadaService.addStoreForce(siteId, storeId, sourceId);
                }
                resultList.add(map);
            }
            Map result = new HashedMap();
            result.put("opt", resultList);
            return ResultMap.successResult(result);
        } catch (Exception e) {
            return ResultMap.errorResult(e.getMessage());
        }
    }

    public Map updateDeliveryStatus(String siteId, String storeIds, String flag) {
        List<Map> resultList = new ArrayList<>();
        for (int n = 0; n < storeIds.split(",").length; n++) {
            String storeId = storeIds.split(",")[n];
            Map result = new HashMap();
            int i;
            if (StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId)) {
                result.put("status", "error");
                continue;
            }
            if ("update".equals(flag)) {
                 i = merchantMapper.updateStoreDelivery(siteId, storeId, 0);
                result.put("status", i > 0 ? "success" : "error");
                continue;
            }
            if (!StringUtil.isEmpty(merchantMapper.isExist(siteId, storeId))) {
                 i = merchantMapper.updateStoreDelivery(siteId, storeId, 1);
                result.put("status", i > 0 ? "success" : "error");
            }else {
                i =  merchantMapper.addStoreDelivery(siteId, storeId);
                result.put("status", i > 0 ? "success" : "error");
            }
            resultList.add(result);
        }
        Map map = new HashedMap();
        map.put("opt", resultList);
        return map;
    }

    public Map updateDeliveryMeituanFun(String siteId, String storeIds, String flag) {
        List<Map> resultList = new ArrayList<>();
        for (int n = 0; n < storeIds.split(",").length; n++) {
            String storeId = storeIds.split(",")[n];
            Map result = new HashMap();
            int i;
            if (StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId)) {
                result.put("status", "error");
                continue;
            }
            if (!StringUtil.isEmpty(merchantMapper.isExist(siteId, storeId))) {
                i = merchantMapper.updateDeliveryMeituanFun(siteId, storeId, Integer.parseInt(flag));
                result.put("status", i > 0 ? "success" : "error");
            }else {
                i =  merchantMapper.addStoreDeliveryMeituan(siteId, storeId);
                result.put("status", i > 0 ? "success" : "error");
            }
            resultList.add(result);
        }
        Map map = new HashedMap();
        map.put("opt", resultList);
        return map;
    }
    public Map updateDeliveryshiFun(String siteId, String storeIds, String flag) {
        List<Map> resultList = new ArrayList<>();
        for (int n = 0; n < storeIds.split(",").length; n++) {
            String storeId = storeIds.split(",")[n];
            Map result = new HashMap();
            int i;
            if (StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId)) {
                result.put("status", "error");
                continue;
            }
            if (!StringUtil.isEmpty(merchantMapper.isExist(siteId, storeId))) {
                i = merchantMapper.updateDeliveryShiFun(siteId, storeId, Integer.parseInt(flag));
                result.put("status", i > 0 ? "success" : "error");
            }else {
                i =  merchantMapper.addStoreDeliveryShi(siteId, storeId, Integer.parseInt(flag));
                result.put("status", i > 0 ? "success" : "error");
            }
            resultList.add(result);
        }
        Map map = new HashedMap();
        map.put("opt", resultList);
        return map;
    }
    public BLogisticsOrder selectBLogisticsOrderByTradesId(String tradesId){
        return bLogisticsOrderMapper.selectByTradesId(tradesId);
    }

    @Async
    public void autoQueryo2o(String tradesId) {
        logger.info("autoQueryo2o" + tradesId);
        Map map = queryOrder(tradesId);
        try {
            Map result = JacksonUtils.json2map(map.get("result") + "");
            logger.info("autoQueryo2oResult" + JacksonUtils.mapToJson(result));

            if (result !=null && result.get("code").equals("200")) {
                Map data = (Map) result.get("data");
                BLogisticsOrder bLogisticsOrder = bLogisticsOrderMapper.selectByTradesId(tradesId);
                if (StringUtil.isEmpty(bLogisticsOrder)) return;
                Integer dbStatus = Integer.valueOf(bLogisticsOrder.getStatus());

                Integer status = Integer.valueOf(data.get("order_status") + "");
                Integer statusold = Integer.valueOf(data.get("order_status") + "");
                status=fnStatus(status);
                logger.info(status+"----autoQueryo2oResult dbStatus---" + dbStatus);
                if (dbStatus == status) return;//如果状态一样不做处理
                logger.info(status+"2----autoQueryo2oResult dbStatus---" + dbStatus);
                //系统已接单1,已分配骑手20,骑手已到店80,配送中2,已送达3,系统拒单/配送异常5
                if (dbStatus != status && (statusold == 1 || statusold == 20 || statusold == 80 || statusold == 2 || statusold == 3|| statusold == 5)) {
                    logger.info(status+"3----autoQueryo2oResult dbStatus---" + dbStatus);
                    JSONObject item = new JSONObject();
                    item.put("order_status", data.get("order_status"));
                    item.put("partner_order_code", tradesId);
                    item.put("carrier_driver_name", data.get("carrier_driver_name"));
                    item.put("carrier_driver_phone", data.get("carrier_driver_phone"));
                    logger.info("autoQueryo2oExecute");

                    updatbLogisticsOrder(item,1);

                    List<Map> existList = bLogisticsOrderMapper.selectByTradesIdLog(tradesId);
                    List<Map> list = (List<Map>) data.get("event_log_details");
                    for(Map event : list){
                        Integer eventStatus = Integer.valueOf(event.get("order_status").toString());
                        eventStatus=fnStatus(eventStatus);
                        Long eventTime = Long.valueOf(event.get("occur_time").toString());
                        if (checkStatus(existList, eventStatus)) {//不存在
                            Map<String, Object> log = new HashedMap();
                            log.put("siteId", bLogisticsOrder.getSiteId());
                            log.put("orderNumber", bLogisticsOrder.getOrderNumber());
                            log.put("status", eventStatus);
                            log.put("description", "");
                            log.put("time", new Date(eventTime));
                            bLogisticsOrderMapper.insertLogWithTime(log);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkStatus(List<Map> list, Integer status) {
        for (Map map : list) {
            if (Integer.valueOf(map.get("status").toString()) == status) {
                return false;
            }
        }
        return true;
    }
    public int fnStatus(int status) {
        logger.info("--------------------------status:"+status);
        int statusnew=status;
        if(status==20){
            statusnew=2;
        }else if(status==80){
            statusnew=3;
        }else if(status==2){
            statusnew=4;
        }else if(status==3){
            statusnew=5;
        }else if(status==4){
            statusnew=6;
        }else if(status==5) {
            statusnew=7;
        }
        logger.info(statusnew+":statusnew--------------------------status:"+status);
        return statusnew;
    }

    //门店后台，商户后台查看物流信息时自动查询并同步物流平台订单数据
    public  Map<String,Object> selectbLogisticsOrderAndAutoQueryo2o(String orderNumber)  {
        Map<String,Object> result = new HashMap<>();
        result.put("bLogisticsOrder",bLogisticsOrderMapper.selectByTradesId(orderNumber));
        result.put("bLogisticsOrderList",bLogisticsOrderMapper.selectByTradesIdLog(orderNumber));

        autoQueryo2o(orderNumber);
        return result;
    }

    //定时执行
    public void timerAutoQueryo2o() {
        String log = "timerAutoQueryo2o" + DateUtils.formatDate(new Date(), "YYYY-MM-dd HH:mm:ss");
        logger.info(log);
        List<Map> list = bLogisticsOrderMapper.queryYesterdayBadLogisticOrder();
        goodsEsMapper.insertLog("0", log, list.size() + "");
        list.stream().forEach(map -> {
            try {
                autoQueryo2o(map.get("trades_id").toString());
            } catch (Exception e) {
                logger.error("timerAutoQueryo2o error");
                e.printStackTrace();
            }
        });
    }
    public void insertLog(String str) {
        goodsEsMapper.insertLog("0",str,"配送日志");
    }

}
