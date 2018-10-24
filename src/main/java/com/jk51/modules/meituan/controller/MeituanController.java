package com.jk51.modules.meituan.controller;

import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.meituan.constants.CancelOrderReasonId;
import com.jk51.modules.meituan.constants.MtConfig;
import com.jk51.modules.meituan.constants.RequestConstant;
import com.jk51.modules.meituan.request.CancelOrderRequest;
import com.jk51.modules.meituan.request.CreateOrderByShopRequest;
import com.jk51.modules.meituan.request.MockOrderRequest;
import com.jk51.modules.meituan.service.MtService;
import com.jk51.modules.meituan.sign.SignHelper;
import com.jk51.modules.meituan.util.DateUtil;
import com.jk51.modules.meituan.util.ParamBuilder;
import com.jk51.modules.meituan.vo.OpenApiGood;
import com.jk51.modules.meituan.vo.OpenApiGoods;
import com.jk51.modules.trades.service.TradesDeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

@Controller()
@RequestMapping("mt")
public class MeituanController {


    private static final Logger logger = LoggerFactory.getLogger(MeituanController.class);
    // TEST_DELIVERY_ID，配送唯一标识，实际实现时需要替换为自己的delivery_id
    private static Long TEST_DELIVERY_ID = Long.valueOf(1520836428);

    // TEST_ORDER_ID 订单号，实际实现时需要替换为自己的订单id
    private static String TEST_ORDER_ID = String.valueOf(1520836428);


    @RequestMapping("createOrder")
    @ResponseBody
    public Map createOrder(){
        String appkey = MtConfig.APP_KEY;
        String secret = MtConfig.SECRET;

        CreateOrderByShopRequest request = buildMockRequest(appkey);

        Map<String, String> params = ParamBuilder.convertToMap(request);


        try {
            String sign = SignHelper.generateSign(params, secret);
            params.put("sign", sign);
            String res = OkHttpUtil.postMap(RequestConstant.ORDER_CREATE_BY_SHOP, params);
            logger.info(String.format("reponse data: %s", res));
        } catch (Exception e) {
            logger.error("error", e);
        }

        return null;
    }

    @RequestMapping("cancelOrder")
    @ResponseBody
    public void cancelOrder(){
        String appkey = MtConfig.APP_KEY;
        String secret = MtConfig.SECRET;

        CancelOrderRequest request = buildMockRequestCancel(appkey);

        Map<String, String> params = ParamBuilder.convertToMap(request);


        try {
            String sign = SignHelper.generateSign(params, secret);
            params.put("sign", sign);
            String res = OkHttpUtil.postMap(RequestConstant.ORDER_CANCEL, params);
            logger.info(String.format("reponse data: %s", res));
        } catch (Exception e) {
            logger.error("error", e);
        }
    }

    @RequestMapping("statusTest")
    @ResponseBody
    public void statusTest(){
        Map<String, String> params = buildMockRequestStatus();
        try {
            String res = OkHttpUtil.postMap(RequestConstant.MOCK_ORDER_PICKUP, params);
            logger.info(String.format("reponse data: %s", res));
        } catch (Exception e) {

        }
    }

    private CancelOrderRequest buildMockRequestCancel(String appkey) {
        CancelOrderRequest request = new CancelOrderRequest();
        request.setAppkey(appkey);
        request.setTimestamp(DateUtil.unixTime());
        request.setVersion("1.0");
        request.setDeliveryId(TEST_DELIVERY_ID);
        request.setMtPeisongId("1520838981088706");
        request.setCancelOrderReasonId(CancelOrderReasonId.PARTNER_REASON);
        request.setCancelReason("测试取消");
        return request;
    }

    private CreateOrderByShopRequest buildMockRequest(String appkey) {
        CreateOrderByShopRequest request = new CreateOrderByShopRequest();
        request.setAppkey(appkey);
        request.setTimestamp(DateUtil.unixTime());
        request.setVersion("1.0");

        // 设置订单号及配送服务标识
        request.setDeliveryId(TEST_DELIVERY_ID);
        request.setOrderId(TEST_ORDER_ID);

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
        request.setShopId("test_0001");

        // 设置取货人信息，请根据测试门店地址 在测试发单时合理设置送货地址
        request.setReceiverName("测试收货人");
        request.setReceiverAddress("测试收货人地址");
        request.setReceiverPhone("18523657373");
        request.setReceiverLng(97235458);
        request.setReceiverLat(31065074);

        // 设置预计送达时间为1小时以后
        request.setExpectedDeliveryTime(DateUtil.unixTime() + 3600L);

        // 设置门店流水号，门店流水号为一天中单个门店的订单序号，方便骑手线下到门店取货
        request.setPoiSeq("1");

        // 设置商品重量，单位为kg
        request.setGoodsWeight(new BigDecimal(3));

        // 设置商品详情
        OpenApiGoods openApiGoods = new OpenApiGoods();

        OpenApiGood openApiGood1 = new OpenApiGood();
        // 商品数量
        openApiGood1.setGoodCount(1);
        // 商品名称
        openApiGood1.setGoodName("进口红提");
        // 商品单价
        openApiGood1.setGoodPrice(new BigDecimal("20.5"));
        // 商品数量单位
        openApiGood1.setGoodUnit("盒");

        OpenApiGood openApiGood2 = new OpenApiGood();
        // 商品数量
        openApiGood2.setGoodCount(1);
        // 商品名称
        openApiGood2.setGoodName("进口芒果");
        // 商品单价
        openApiGood2.setGoodPrice(new BigDecimal("40.5"));
        // 商品数量单位
        openApiGood2.setGoodUnit("盒");

        openApiGoods.setGoods(Arrays.asList(openApiGood1, openApiGood2));

        request.setGoodsDetail(openApiGoods);

        // 设置备注信息
        request.setNote("小哥麻烦要一下发票");
        return request;
    }

    private Map<String, String> buildMockRequestStatus(){
        String appkey = MtConfig.APP_KEY;
        String secret = MtConfig.SECRET;

        MockOrderRequest request = new MockOrderRequest();
        request.setAppkey(appkey);
        request.setTimestamp(DateUtil.unixTime());
        request.setVersion("1.0");
        request.setDeliveryId(TEST_DELIVERY_ID);
        request.setMtPeisongId("1520838981088706");

        Map<String, String> params = ParamBuilder.convertToMap(request);
        try{
            String sign = SignHelper.generateSign(params, secret);
            params.put("sign", sign);
        }catch (Exception e){

        }
        return params;
    }

    @Autowired
    private MtService mtService;
    @Autowired
    private StoresMapper storesMapper;
    @RequestMapping("a")
    public void a(){
//        tradesDeliveryService.calcPrice();
        int i = storesMapper.queryEleStatus("100190", "1");
        logger.error("====================="+i);
    }

    @RequestMapping("test")
    @ResponseBody
    public Map createOrderFmt(long tradesId, int storeId){
        return mtService.createOrderFmt(tradesId, storeId);
    }
}
