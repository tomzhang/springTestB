package com.jk51.modules.order.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.Address;
import com.jk51.model.map.Coordinate;
import com.jk51.model.order.*;
import com.jk51.model.order.response.DistributeResponse;
import com.jk51.model.treat.MerchantExtTreat;
import com.jk51.modules.logistics.service.AddressService;
import com.jk51.modules.map.service.MapService;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:分单service
 * 作者: baixiongfei
 * 创建日期: 2017/2/16
 * 修改记录:
 */
@Service
public class PaDistributeOrderService {
    public static final Logger logger = LoggerFactory.getLogger(PaDistributeOrderService.class);

    @Autowired
    private MapService mapService;
    @Autowired
    private AddressService addressService;

    @Autowired
    private DistributeOrderService distributeOrderService;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private MerchantExtTreatMapper merchantExtTreatMapper;

    /**
     * 分单，包括计算订单价格(预下单)
     *
     * @param req
     * @param useFlag 使用场景 1：下单，2：预下单
     * @return
     */
    public DistributeResponse beforeOrder(BeforeCreateOrderReq req, String useFlag) {
        long startMillis = System.currentTimeMillis();
        logger.info("======beforeOrder分单开始:BeforeCreateOrderReq{},useFlag:{},开始：{}", req.toString(), useFlag, startMillis);
        if ("2".equals(useFlag)) {
            //String province = ordersMapper.getAreaId(req.getReceiverProvinceCode());
            String city = ordersMapper.getAreaId(req.getReceiverCityCode());
            String country = ordersMapper.getAreaId(req.getAddrCode());
            String area = ordersMapper.getAreaId(req.getAddress());
            String address = req.getReceiverAddress();
            if (!StringUtil.isEmpty(req.getReceiverAddress()) && !StringUtil.isEmpty(city) && req.getReceiverAddress().indexOf(city) == -1) {
                address = (country + city).trim();
                if (!StringUtil.isEmpty(area)) {
                    address = (address + area).trim();
                }
                address = (address + req.getReceiverAddress()).trim();
            }
            req.setAddress(address);
            req.setAddrCode(req.getReceiverProvinceCode());
        }
        logger.info("======beforeOrder根据手机号查询会员的buyer_id:buyer_id{},间隔：{}", req.getBuyerId(), System.currentTimeMillis() - startMillis);
        //根据手机号查询会员的buyer_id
        if (req.getBuyerId() == 0 || StringUtil.isEmpty(req.getBuyerId())) {
            BMember memberInfo = ordersMapper.getMemberById(req.getSiteId(), req.getMobile());
            if (!StringUtil.isEmpty(memberInfo)) {
                req.setUserId(memberInfo.getMemberId());
                req.setBuyerId(memberInfo.getBuyerId());
            }
        }
        logger.info("======beforeOrder根据手机号查询会员的SiteId:{},间隔：{}", req.getSiteId(), System.currentTimeMillis() - startMillis);
        DistributeResponse response = new DistributeResponse();
        String storeId = "";
        //计算商品原始总价格
        MerchantExtTreat merchantExt = merchantExtTreatMapper.selectByMerchantId(req.getSiteId());
        if (!StringUtil.isEmpty(req.getTradesSource()) && req.getTradesSource().equals(130)) {
            storeId = Objects.toString(req.getStoresId(), "");
            if (1 == merchantExt.getHas_erp_price()) {
                req.setErpStoreId(req.getStoresId());
            }
        }
        //查询购物车里面的商品信息
        List<GoodsInfo> goodsInfoInfos = distributeOrderService.getGoodsInfos(req.getSiteId(), req.getOrderGoods(), storeId, req.getErpStoreId(), req.getErpAreaCode());


        logger.info("======beforeOrder门店信息buyer_id:buyer_id{},间隔：{}", req.getStoresId(), System.currentTimeMillis() - startMillis);
        //获取有商品的门店
        List<Store> stores = distributeOrderService.getStore(req.getSiteId(), goodsInfoInfos, req.getReceiverCityCode(), null);


        logger.info("======beforeOrder获取有商品的门店stores:{},间隔：{}", stores, System.currentTimeMillis() - startMillis);
        //计算价格之前获取最近门店
        Distribute distributezui = new Distribute();
        distributezui.setUserDeliveryAddr(req.getAddress());
        distributeOrderService.getGoodsTotalPrice(req, goodsInfoInfos, req.getOrderGoods(), response, merchantExt.getHas_erp_price());
        int goodsTotalPrice = distributeOrderService.getGoodsTotalPrice(req, goodsInfoInfos, req.getOrderGoods(), response, merchantExt.getHas_erp_price(), req.getTradesSource());
        logger.info("======beforeOrder计算商品原始总价格goodsTotalPrice:{},间隔：{}", goodsTotalPrice, System.currentTimeMillis() - startMillis);
        int distributePrice = response.getDistributePrice() - response.getDistributeDiscountPrice();
        logger.info("======beforeOrder计算商品折扣后实际总价格distributePrice:{},间隔：{}", distributePrice, System.currentTimeMillis() - startMillis);
        Integer distributeDiscountPrice = response.getDistributeDiscountPrice();
        logger.info("======beforeOrder计算商品原始总价格distributeDiscountPrice:{},间隔：{}", distributeDiscountPrice, System.currentTimeMillis() - startMillis);
        if (null == distributeDiscountPrice) {
            distributeDiscountPrice = 0;
        }
//        logger.info("goodsInfoInfos============" + Arrays.toString(goodsInfoInfos.toArray()));
        //运费,单位：分
        int freight = 0;

        //是否需要计算运费
        if ("1".equals(req.getOrderType())) {//送货上门订单需要计算运费
            Distribute distribute = new Distribute();
            distribute.setSiteId(req.getSiteId());
            distribute.setOrderGoods(req.getOrderGoods());
            //计算送货上门的运费信息
            if (StringUtil.isNotEmpty(req.getAddress())) {
                //distribute.setUserDeliveryProvinceCode(Integer.parseInt(req.getAddrCode()));
                distribute.setUserDeliveryAddr(req.getAddress());
                //用户运费信息
                response = distributeOrderService.selectHomeDeliveryOrderStore(stores, distribute, goodsTotalPrice, goodsInfoInfos, useFlag);
            } else {
                //查询用户的默认收货地址
                Address address = addressService.findDefault(req.getBuyerId(), req.getSiteId());
                if (null != address) {
                    distribute.setUserDeliveryProvinceCode(address.getProvinceCode());
                    //用户运费信息
                    response = distributeOrderService.selectHomeDeliveryOrderStore(stores, distribute, goodsTotalPrice, goodsInfoInfos, useFlag);
                }
            }
        } else {//门店自提订单运费为0
            Distribute distribute = new Distribute();
            //用户运费信息
            //response = selectHomeDeliveryOrderStore(stores, distribute, goodsTotalPrice, goodsInfoInfos, useFlag);
            for (Store store : stores) {
                if (req.getStoresId() == store.getId()) {
                    response.setStore(store);
                    try {
                        Coordinate coordinate = mapService.geoCoordinate(String.valueOf(req.getAddress()));
                        //用户到门店的距离,单位：米
                        int distance = Integer.parseInt(mapService.geoDistance(coordinate, new Coordinate(Double.parseDouble(store.getGaodeLng()), Double.parseDouble(store.getGaodeLat()))));
                        response.setMinDistance(distance);
                    } catch (Exception e) {
                        logger.info("获取距离distance异常：" + e);
                    }

                    break;
                }
            }
        }
        freight = 0;
        response.setOrderFreight(freight);
        logger.info("======beforeOrder计算运费freight:{},", freight);
        response.setDistributePrice(distributePrice);
        //查询用户的剩余积分
        response.setIntegral(distributeOrderService.integralMax(req.getSiteId(), goodsTotalPrice, String.valueOf(req.getBuyerId())));
        //订单原始金额
        response.setOrderOriginalPrice(goodsTotalPrice);
        logger.info("======beforeOrder订单原始金额goodsTotalPrice:{},间隔：{}", goodsTotalPrice, System.currentTimeMillis() - startMillis);
        int integralDeductionPrice = 0;
        response.setIntegralDeductionPrice(integralDeductionPrice);
        response.setNeedIntegral(0);

        Integer postageDiscount = 0;
        response.setPostageDiscount(postageDiscount);
        //是否是分销订单
        response.setDistributeGoods(req.isDistributeGoods());
        response.setProRuleDeductionPrice(response.getProRuleDeductionPrice() + postageDiscount);
        int orderRealPrice = goodsTotalPrice;

        //不能出现负数订单
        if (orderRealPrice < 0) {
            orderRealPrice = 0;
            /*
            todo 超级优惠上线后删除
            if (concessionDeductionPrice > integralDeductionPrice) {
                response.setConcessionDeductionPrice(goodsTotalPrice + freight - integralDeductionPrice);
            } else {
                response.setIntegralDeductionPrice(goodsTotalPrice + freight - concessionDeductionPrice);
            }*/
        }

        //总优惠价格
        response.setCouponALLPrice(goodsTotalPrice + (freight + postageDiscount) - orderRealPrice);
        //查询用户的可用积分（优惠券抵扣金额答应订单金额）
        //response.setIntegral(integralMax(req.getSiteId(), goodsTotalPrice- couponDeductionPrice, String.valueOf(req.getBuyerId())));

        /* 当优惠券金额小于0的时候抵扣金额为0 这里主要处理限价券的问题 zw */
        if (response.getConcessionDeductionPrice() < 0) {
            response.setConcessionDeductionPrice(0);
        }
        logger.info("======beforeOrder订单原始金额orderRealPrice:{},间隔：{}", orderRealPrice, System.currentTimeMillis() - startMillis);
        response.setOrderRealPrice(orderRealPrice);
        //-----------设置分销订单前台提示语--------------before---------yeah-------------
        if (0 != distributePrice) {
            // 使用分销商品总金额计算最近提升等级
            String distributeTip = distributeOrderService.getDistributeTip(req, distributePrice);
            response.setDistributeTip(distributeTip);
            response.setDistributeDiscountPrice(distributeDiscountPrice);
        }
        //-------------------------------------------end-------------yeah--------------
        logger.info("======beforeOrder分单结束response:{},间隔：{}", response.toString(), System.currentTimeMillis() - startMillis);
        response.setGoodsInfoInfos(goodsInfoInfos);
        return response;
    }


}
