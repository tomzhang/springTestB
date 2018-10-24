package com.jk51.modules.order.service;

import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.map.Coordinate;
import com.jk51.model.order.*;
import com.jk51.model.order.response.DistributeResponse;
import com.jk51.model.order.response.OrderResponse;
import com.jk51.model.registration.models.ServceOrder;
import com.jk51.model.registration.models.ServceUseDetail;
import com.jk51.modules.account.mapper.AccountCommissionRateMapper;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.modules.distribution.mapper.DistributorMapper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.integral.mapper.IntegralGoodsMapper;
import com.jk51.modules.integral.service.IntegralService;
import com.jk51.modules.map.service.MapService;
import com.jk51.modules.merchant.mapper.MerchantExtMapper;
import com.jk51.modules.merchant.service.LabelService;
import com.jk51.modules.offline.service.ErpToolsService;
import com.jk51.modules.offline.service.OfflineIntegrateService;
import com.jk51.modules.order.mapper.DistributeOrderMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.pay.service.PayService;
import com.jk51.modules.promotions.service.PromotionsOrderService;
import com.jk51.modules.registration.mapper.ServceUseDetailMapper;
import com.jk51.modules.registration.service.ServceOrderService;
import com.jk51.modules.smallTicket.service.SmallTicketService;
import com.jk51.modules.sms.service.Sms7MoorService;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.modules.treat.mapper.MerchantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:订单下单service
 * 作者: baixiongfei
 * 创建日期: 2017/2/15
 * 修改记录:
 */
@Service
public class PaOrderService {

    @Autowired
    private BalanceService balanceService;


    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    MerchantExtMapper merchantExtMapper;
    @Autowired
    private DistributeOrderMapper distributeOrderMapper;

    @Autowired
    private AccountCommissionRateMapper accountCommissionRateMapper;
    @Autowired
    LabelService labelService;
    @Autowired
    private TradesMapper tradesMapper;

    @Autowired
    private OrderService orderService;
    @Autowired
    private PaDistributeOrderService distributeOrderService;
    @Autowired
    private DistributeOrderService distributeOrderServiceOld;
    @Autowired
    private MapService mapService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IntegralGoodsMapper integralGoodsMapper;

    @Autowired
    private ErpToolsService erpToolsService;


    @Autowired
    GoodsMapper goodsMapper;


    @Autowired
    private ServceOrderService servceOrderService;

    @Autowired
    private ServceUseDetailMapper servceUseDetailMapper;

    @Value("${erp.storage_service_url}")
    private String storage_service_url;

    public static final Logger logger = LoggerFactory.getLogger(PaOrderService.class);
    @Autowired
    private IntegralService integralService;
    @Autowired
    PayService payService;
    @Autowired
    Sms7MoorService _7moorService;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;


    @Autowired
    DistributorMapper distributorMapper;
    @Autowired
    private PromotionsOrderService promotionsOrderService;


    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private OfflineIntegrateService offlineIntegrateService;
    @Autowired
    private SmallTicketService smallTicketService;

    /**
     * 检查商品是否可用，如果有一个商品不可用，则整个订单的其它商品也不能购买
     *
     * @param goodsInfoList
     * @return false:不可用，true:可用
     */
    public boolean checkGoods(List<GoodsInfo> goodsInfoList, Integer tradesSource) {
        if (goodsInfoList == null || goodsInfoList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < goodsInfoList.size(); i++) {
            if (!StringUtil.isEmpty(tradesSource) && (tradesSource == 130||tradesSource == 170)) {
                if (goodsInfoList.get(i).getAppGoodsStatus() != 1) {
                    return false;
                }
            }else {
                if (goodsInfoList.get(i).getGoodsStatus() != 1) {
                    return false;
                }
            }
            //app下单不判断
            if (!StringUtil.isEmpty(tradesSource) && tradesSource != 130&& tradesSource != 170) {
                if (goodsInfoList.get(i).getWxPurchaseWay() != 110) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * 设置送货上门订单和门店自提订单的入参必选参数
     *
     * @param homeDeliveryAndStoresInvite
     * @return
     */
    public String setHomeDeliveryAndStoresInviteReqParams(HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite) {

        if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getSiteId()) || homeDeliveryAndStoresInvite.getSiteId() == 0) {
            return "siteId";
        }
        if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getOrderGoods()) || homeDeliveryAndStoresInvite.getOrderGoods().size() <= 0) {
            return "orderGoods";
        }

        if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getOrderType())) {
            return "orderType";
        }
        if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getMobile())) {
            return "mobile";
        }
        if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getTradesSource()) || homeDeliveryAndStoresInvite.getTradesSource() == 0) {
            return "tradesSource";
        }

        if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getFilePdf())) {
            return "filePdf";
        }
        if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getOrderNo())) {
            return "orderNo";
        }
        if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getSecondToken())) {
            return "secondToken";
        }
        if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getDiagnosticResults())) {
            return "diagnosticResults";
        }
        if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getPrescriptionNo())) {
            return "prescriptionNo";
        }

        if ("1".equals(homeDeliveryAndStoresInvite.getOrderType())) {
            if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getReceiverProvince())) {
                return "receiverProvince";
            }
        }else {
            if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getSelfTakenStore())) {
                return "storesId";
            }
        }
        //判断参数是否
        if (!StringUtil.isEmpty(homeDeliveryAndStoresInvite.getFlag()) && homeDeliveryAndStoresInvite.getFlag().equals(1)) {
            if (null == homeDeliveryAndStoresInvite.getSchedulePersonId()) return "schedulePersonId";
            if (null == homeDeliveryAndStoresInvite.getDiagnoseStatus()) return "diagnoseStatus";
            if (org.apache.commons.lang.StringUtils.isBlank(homeDeliveryAndStoresInvite.getDiseaseInfo()))
                return "diseaseInfo";
            if (null == homeDeliveryAndStoresInvite.getUseDetailId() || null == homeDeliveryAndStoresInvite.getSiteId()) {
                return "siteId";
            }
        }
        return "";
    }
    /**
     * 创建送货上门订单和门店自提订单
     *
     * @param homeDeliveryAndStoresInvite
     * @return
     */
    @Transactional
    public OrderResponse createOrders(HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite) {
        logger.info("======createOrders开始创建订单信息:{},", homeDeliveryAndStoresInvite);
        OrderResponse response = new OrderResponse();
        response.setSiteId(String.valueOf(homeDeliveryAndStoresInvite.getSiteId()));
        //校验必选参数是否为空
        String checkParams = setHomeDeliveryAndStoresInviteReqParams(homeDeliveryAndStoresInvite);
        if (StringUtil.isNotEmpty(checkParams)) {
            response.setCode(CommonConstant.TRADES_RESP_CODE_MISSINF_PARAMS);
            response.setMessage(String.format("必选参数：[%s] 为空", checkParams));
            return response;
        }
        logger.info("======createOrders校验必选参数结束:{},", homeDeliveryAndStoresInvite);
        int userId = 0;
        int memberId=0;
        String userName = "";
        logger.info("======createOrders查询下单用户信息:SiteId{}，手机号{},", homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getMobile());
        //查询用户是否已存在
        BMember bMember = ordersMapper.getMemberById(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getMobile());
        if (bMember != null) {
            userId = bMember.getBuyerId();
            userName = bMember.getName();
            memberId=bMember.getMemberId();
        } else {
            //查询51jk中心库是否有匿名用户，没有的话则创建一个,有的话，则更新注册商户
            YBMember ybzfUser = ordersMapper.getYBMemberByMobile(homeDeliveryAndStoresInvite.getMobile());
            if (ybzfUser == null) {
                //创建匿名用户
                ybzfUser = new YBMember();
                ybzfUser.setMobile(homeDeliveryAndStoresInvite.getMobile());
                ybzfUser.setPasswd(orderService.SHA1(orderService.generatePWD()));//默认密码
                ybzfUser.setIsActivated(0);
                ybzfUser.setReginSource(9999);//注册来源
                ybzfUser.setBUsersarr(String.valueOf(homeDeliveryAndStoresInvite.getSiteId()));
                ybzfUser.setRegisterStores(Integer.parseInt(homeDeliveryAndStoresInvite.getTradesStore()));
                ybzfUser.setName("");
                ordersMapper.addYBMember(ybzfUser);
            } else {
                //更新注册商户
                String userSiteArr = ybzfUser.getBUsersarr();
                if (StringUtil.isNotEmpty(userSiteArr)) {
                    ybzfUser.setBUsersarr(userSiteArr + "," + homeDeliveryAndStoresInvite.getSiteId());
                } else {
                    ybzfUser.setBUsersarr(String.valueOf(homeDeliveryAndStoresInvite.getSiteId()));
                }
                ordersMapper.updateYBMember(ybzfUser);
            }
            userId = ybzfUser.getMemberId();
            userName = ybzfUser.getName();
            logger.info("======createOrders查询下单用户信息end:userId{}，userName{},", userId, userName);
            //根据会员ID查询商家会员用户
            BMember bShopMember = ordersMapper.getMemberByBuyerId(homeDeliveryAndStoresInvite.getSiteId(), ybzfUser.getMemberId());
            if (bShopMember == null) {
                logger.info("不执行代码----------------------");
            }
            memberId=bShopMember.getMemberId();
        }
        logger.info("======createOrders根据会员ID查询商家会员用户:userId{}，userName{},", userId, userName);
        if (userId > 0) {
            //通过的商品ids查询商品信息
            List<GoodsInfo> goodsInfos = distributeOrderServiceOld.getGoodsInfos(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getOrderGoods(), null);
            //检查商品是否可以购买： 如果是积分兑换则不检查
            if (homeDeliveryAndStoresInvite.getExchange() == null || !homeDeliveryAndStoresInvite.getExchange()) {
                if (!checkGoods(goodsInfos, homeDeliveryAndStoresInvite.getTradesSource())) {
                    response.setCode(CommonConstant.TRADES_RESP_CODE_GOODS_NOT_NORMAL);
                    response.setMessage("商品不可以购买");
                    return response;
                }
            }
            logger.info("======createOrders检查商品可以购买:goodsInfos{},", goodsInfos);

            /* -- 验证赠品是否有库存 zw start -- */
            if (homeDeliveryAndStoresInvite.getGiftGoods() != null && homeDeliveryAndStoresInvite.getGiftGoods().size() > 0) {
                response = promotionsOrderService.verifyGiftGoodsStock(homeDeliveryAndStoresInvite.getGiftGoods(),
                    homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getPromActivityIdArr(),
                    homeDeliveryAndStoresInvite.getUserCouponId()
                );
                if (response != null && response.getCode() != null) {
                    return response;
                }
            }
            /* -- 验证赠品是否有库存 zw end -- */

            //订单交易表
            Trades trades = new Trades();
            TradesExt tradesExt = new TradesExt();
            //查询商家信息
            Merchant merchant = distributeOrderMapper.getMerchant(homeDeliveryAndStoresInvite.getSiteId());
            //查询商家账号信息
            YBAccount ybAccount = ordersMapper.getYBAccountById(homeDeliveryAndStoresInvite.getSiteId(), 1);
            //查询用户信息
            BMember bShopMember = ordersMapper.getMemberByBuyerId(homeDeliveryAndStoresInvite.getSiteId(), userId);
            logger.info("======createOrders查询用户信息:bShopMember{},", bShopMember);

            //校验积分是否大于0，并且跟用户本身的积分比较，如果用户使用积分值大于用户拥有的积分值，则提示积分不可用
            if (!StringUtil.isEmpty(homeDeliveryAndStoresInvite.getIntegralUse()) && homeDeliveryAndStoresInvite.getIntegralUse() > 0 && homeDeliveryAndStoresInvite.getIntegralUse() > bShopMember.getIntegrate().intValue()) {
                response.setCode(CommonConstant.TRADES_RESP_CODE_INTEGRAL_TOO);
                response.setMessage("积分不能大于用户现有的积分值");
                return response;
            }


            //获取自动分单信息，价格信息及运费信息
            BeforeCreateOrderReq beforeCreateOrderReq = new BeforeCreateOrderReq();
            beforeCreateOrderReq.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
            beforeCreateOrderReq.setBuyerId(userId);
            beforeCreateOrderReq.setAddrCode(homeDeliveryAndStoresInvite.getReceiverProvinceCode());
            beforeCreateOrderReq.setMobile(homeDeliveryAndStoresInvite.getMobile());
            if(!StringUtil.isEmpty(homeDeliveryAndStoresInvite.getTradesStore())){
                beforeCreateOrderReq.setStoresId(Integer.parseInt(homeDeliveryAndStoresInvite.getTradesStore()));
            }
            beforeCreateOrderReq.setOrderGoods(homeDeliveryAndStoresInvite.getOrderGoods());
            beforeCreateOrderReq.setCouponId(homeDeliveryAndStoresInvite.getUserCouponId());
            beforeCreateOrderReq.setIntegralUse(StringUtil.isEmpty(homeDeliveryAndStoresInvite.getIntegralUse()) ? 0 : homeDeliveryAndStoresInvite.getIntegralUse());
            beforeCreateOrderReq.setOrderType(homeDeliveryAndStoresInvite.getOrderType());
            beforeCreateOrderReq.setExchange(homeDeliveryAndStoresInvite.getExchange());
            String province = homeDeliveryAndStoresInvite.getReceiverProvince();
                    //ordersMapper.getAreaId(homeDeliveryAndStoresInvite.getReceiverProvinceCode());
            String city = homeDeliveryAndStoresInvite.getReceiverCity();
                    //ordersMapper.getAreaId(homeDeliveryAndStoresInvite.getReceiverCityCode());
            String receiverCountry = homeDeliveryAndStoresInvite.getReceiverCountry();
                    //ordersMapper.getAreaId(homeDeliveryAndStoresInvite.getReceiverCountryCode());
            beforeCreateOrderReq.setAddress((StringUtil.isEmpty(city) ? "" : city) + (StringUtil.isEmpty(receiverCountry) ? "" : receiverCountry) + homeDeliveryAndStoresInvite.getReceiverAddress());
            beforeCreateOrderReq.setReceiverCityCode(homeDeliveryAndStoresInvite.getReceiverCityCode());
            beforeCreateOrderReq.setPromActivityIdArr(homeDeliveryAndStoresInvite.getPromActivityIdArr()); //活动ids  zw
            beforeCreateOrderReq.setUserId((StringUtil.isEmpty(homeDeliveryAndStoresInvite.getUserId()) ? memberId : homeDeliveryAndStoresInvite.getUserId()));
            beforeCreateOrderReq.setErpStoreId(homeDeliveryAndStoresInvite.getErpStoreId());
            beforeCreateOrderReq.setErpAreaCode(homeDeliveryAndStoresInvite.getErpAreaCode());
            if (null != homeDeliveryAndStoresInvite.getGroupPurchase()) {
                beforeCreateOrderReq.setGroupPurchase(homeDeliveryAndStoresInvite.getGroupPurchase());
            }
            beforeCreateOrderReq.setTradesSource(homeDeliveryAndStoresInvite.getTradesSource());
            beforeCreateOrderReq.setGiftGoods(homeDeliveryAndStoresInvite.getGiftGoods());
            beforeCreateOrderReq.setConcessionResult(homeDeliveryAndStoresInvite.getConcessionResult());

            logger.info("======createOrders开始分单:beforeCreateOrderReq{},", beforeCreateOrderReq);
            //调用分单

            DistributeResponse distributeResponse = null;
            try {
                distributeResponse = distributeOrderService.beforeOrder(beforeCreateOrderReq, "1");
            } catch (Exception e) {
                logger.error("预下单出现异常, {}", e);
                response.setCode(CommonConstant.TRADES_RESP_CODE_CONCESSION_ERROR);
                response.setMessage("优惠预下单计算异常");
                return response;
            }

            tradesExt.setIntegralUsed(homeDeliveryAndStoresInvite.getIntegralUse());

            logger.info("======createOrders分单分单结束:distributeResponse{},", distributeResponse.toString());
            //订单金额控制不能为负数
            if (distributeResponse.getOrderRealPrice() < 0) {
                response.setCode(CommonConstant.TRADES_RESP_CODE_INTEGRAL_TOO);
                response.setMessage("订单金额控制不能为负数");
                return response;
            }
            long tradesId = orderService.getTradesId(homeDeliveryAndStoresInvite.getSiteId());
            trades.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
            trades.setTradesId(tradesId);
            trades.setSellerId(merchant.getMerchantId());
            trades.setBuyerId(bShopMember.getBuyerId());
            trades.setSellerName(merchant.getMerchantName());
            trades.setBuyerNick(bShopMember.getName());
            trades.setReceiverPhone(homeDeliveryAndStoresInvite.getReceiverPhone());
            trades.setFreightCommission(distributeResponse.getFreightCommission());

            if (homeDeliveryAndStoresInvite.getTradesSource() != 130) {
                logger.info("邀请码：" + homeDeliveryAndStoresInvite.getClerkInvitationCode());
                if (!StringUtil.isEmpty(homeDeliveryAndStoresInvite.getClerkInvitationCode())) {
                    StoreAdminExt storeAdminExt = storeAdminExtMapper.selectByIvCode(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getClerkInvitationCode());
                    //查询到的店员不为空时订单才加入促销店员id
                    if (storeAdminExt != null) {
                        trades.setStoreUserId(storeAdminExt.getStoreadmin_id());
                    }else {
                        try{
                            trades.setClerkInvitationCode(homeDeliveryAndStoresInvite.getClerkInvitationCode());
                        }catch (Exception e){
                            logger.info("======createOrders Exception{},", e);
                        }
                    }
                }
            } else {
                trades.setStoreUserId(homeDeliveryAndStoresInvite.getStoreUserId());
            }
            logger.info("======createOrders生成订单id:tradesId{},", tradesId);
            //门店自提
            if ("2".equals(homeDeliveryAndStoresInvite.getOrderType())) {
                logger.info("======createOrders门店自提{},", homeDeliveryAndStoresInvite.getOrderType());
                trades.setRecevierMobile(homeDeliveryAndStoresInvite.getMobile());
                trades.setRecevierName(StringUtils.isEmpty(homeDeliveryAndStoresInvite.getReceiverName()) ? userName : homeDeliveryAndStoresInvite.getReceiverName());
                //String barcode = tradesService.generateBarcode(trades.getSiteId());
                //trades.setSelfTakenCode(barcode);
                trades.setReceiverAddress("");
                if (StringUtil.isEmpty(trades.getRecevierName())) {
                    trades.setRecevierName("--");
                }
            } else {//送货上门
                logger.info("======createOrders送货上门{},", homeDeliveryAndStoresInvite.getOrderType());
                province = StringUtil.isEmpty(province) ? "" : province;
                city = StringUtil.isEmpty(city) ? "" : city;
                receiverCountry = StringUtil.isEmpty(receiverCountry) ? "" : receiverCountry;
                if (province.equals(city)) {
                    province = "";
                }
                trades.setRecevierMobile(homeDeliveryAndStoresInvite.getReceiverMobile());
                trades.setRecevierName(homeDeliveryAndStoresInvite.getReceiverName());
                String address = homeDeliveryAndStoresInvite.getReceiverAddress();
                if ((StringUtil.isEmpty(province) || homeDeliveryAndStoresInvite.getReceiverAddress().indexOf(province) == -1) && (StringUtil.isEmpty(city) || homeDeliveryAndStoresInvite.getReceiverAddress().indexOf(city) == -1)) {
                    address = province + city + receiverCountry + homeDeliveryAndStoresInvite.getReceiverAddress();
                }
                trades.setReceiverAddress(address.trim());

                Coordinate coordinate = mapService.geoCoordinate(address);
                trades.setLat(coordinate.getLat());
                trades.setLng(coordinate.getLng());
            }
            logger.info("======createOrders生成订单id:tradesId{},", tradesId);
            trades.setReceiverCity(city);
            //trades.setReceiverCity(homeDeliveryAndStoresInvite.getReceiverCityCode());
            //trades.setReceiverAddress(province + city + receiverCountry + homeDeliveryAndStoresInvite.getReceiverAddress());
            trades.setReceiverZip(homeDeliveryAndStoresInvite.getReceiverZip());
            if (!StringUtil.isEmpty(ybAccount)) {
                trades.setSellerPayNo(ybAccount.getAccount());
            } else {
                trades.setSellerPayNo("zhifubao@ybzf.com");
            }
            trades.setSellerPhone(merchant.getServicePhone());
            trades.setSellerMobile(merchant.getServiceMobile());
            trades.setSellerName(merchant.getMerchantName());
            trades.setTradesStatus(CommonConstant.WAIT_PAYMENT_BUYERS);//所有订单的初始状态都是：等侍买家付款 未支付
            trades.setTotalFee(distributeResponse.getOrderOriginalPrice());//订单原始金额
            trades.setPostFee(distributeResponse.getOrderFreight());//直购订单和门店自提订单的运费都为0
            trades.setPostageDiscount(distributeResponse.getPostageDiscount());//运费优惠
            Integer o2OFreight=!StringUtil.isEmpty(distributeResponse.getIsO2O()) && distributeResponse.getIsO2O().equals(0) ? 0 : null;
            //蜂鸟没有同步门店信息，门店不支持蜂鸟配送
            if(!StringUtil.isEmpty(distributeResponse.getStore())&&!StringUtil.isEmpty(o2OFreight)){
                Integer eleStatus=storesMapper.queryEleStatus(homeDeliveryAndStoresInvite.getSiteId().toString(), distributeResponse.getStore().getId()+"");
                if(eleStatus==null||eleStatus==0){
                    o2OFreight=null;
                }
            }
            trades.setO2OFreight(o2OFreight);

            if (CommonConstant.ORDER_TYPE_HOME_DELIVERY.equals(homeDeliveryAndStoresInvite.getOrderType())) {
                trades.setPostStyle(CommonConstant.POST_STYLE_DOOR);//送货上门订单
            } else if (CommonConstant.ORDER_TYPE_STORE_TAKE.equals(homeDeliveryAndStoresInvite.getOrderType())) {
                trades.setPostStyle(CommonConstant.POST_STYLE_EXTRACT);//门店自提订单
            }
            //设置订单分配的门店
            //送货上门订单
            if (CommonConstant.ORDER_TYPE_HOME_DELIVERY.equals(homeDeliveryAndStoresInvite.getOrderType())) {
                if (!StringUtil.isEmpty(distributeResponse.getStore())) {
                    Integer order_assign_type = merchantExtMapper.selectByMerchantId(homeDeliveryAndStoresInvite.getSiteId()).getOrder_assign_type();
                    if (order_assign_type == 110) { //全天手动分单模式
                        trades.setAssignedStores(0);//默认总部
                    } else {//全天自动分单模式
                        // if (!StringUtil.isEmpty(distributeResponse.getIsO2O()) && distributeResponse.getIsO2O().equals(0)) {//走02O才有最优门店
                        if (!StringUtil.isEmpty(distributeResponse.getIsO2O())) {//走02O才有最优门店
                            trades.setAssignedStores(distributeResponse.getStore().getId());//指定送货门店
                        } else {
                            trades.setAssignedStores(distributeOrderServiceOld.getQYStore(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite));//先查询总仓库,默认总部
                        }
                    }
                    trades.setCreateOrderAssignedStores(distributeResponse.getStore().getId());

                } else {
                    trades.setAssignedStores(distributeOrderServiceOld.getQYStore(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite));//先查询总仓库,默认总部
                }
            } else if (CommonConstant.ORDER_TYPE_STORE_TAKE.equals(homeDeliveryAndStoresInvite.getOrderType())) {
                //门店自提订单
                trades.setSelfTakenStore(homeDeliveryAndStoresInvite.getSelfTakenStore());
                trades.setAssignedStores(homeDeliveryAndStoresInvite.getSelfTakenStore());//指定送货门店
                //最优门店
                String bestStoreId = stringRedisTemplate.opsForValue().get(homeDeliveryAndStoresInvite.getSiteId() + homeDeliveryAndStoresInvite.getMobile() + "_BestStore");
                if (!StringUtil.isEmpty(bestStoreId)) {
                    trades.setCreateOrderAssignedStores(Integer.parseInt(bestStoreId));
                }
            }
            trades.setTradesSource(homeDeliveryAndStoresInvite.getTradesSource());//订单来源平台
            trades.setTradesInvoice(homeDeliveryAndStoresInvite.getTradesInvoice());//发票信息
            //发票抬头
            if (homeDeliveryAndStoresInvite.getTradesInvoice() != null && homeDeliveryAndStoresInvite.getTradesInvoice() == 1) {
                trades.setInvoiceTitle(homeDeliveryAndStoresInvite.getInvoiceTitle());
            }
            trades.setTradesStore(StringUtil.isEmpty(distributeResponse.getStore())?0:distributeResponse.getStore().getId());
            trades.setStockupStatus(CommonConstant.STOCKUP_WAIT_READY);//送货上门订单和门店自提订单初始创建时默认为：未备货
            trades.setIsPayment(CommonConstant.IS_PAYMENT_ZERO);//默认为0：未付款
            trades.setAccountCheckingStatus(0);//默认为0：待处理
            boolean prescriptionFlag = false;
            //判断是否为处方药订单
            for (GoodsInfo goodsInfo : goodsInfos) {
                if (goodsInfo.getDrugCategory() == 150) {
                    prescriptionFlag = true;
                    break;
                }
            }
            if (prescriptionFlag) {
                trades.setPrescriptionOrders(CommonConstant.TRADES_PRESCRIPTION_TRUE);
            } else {
                trades.setPrescriptionOrders(CommonConstant.TRADES_PRESCRIPTION_FALSE);
            }
            //订单的实际支付金额
            trades.setRealPay(distributeResponse.getOrderRealPrice());
            logger.info("======createOrders订单的实际支付金额{},", distributeResponse.getOrderRealPrice());
            //交易佣金
            trades.setTradesSplit(orderService.getTradesCommission(homeDeliveryAndStoresInvite.getSiteId(), distributeResponse.getOrderRealPrice(), CommonConstant.TRADESCOMMISSION_ORDER_TYPE_DIRECT_PURCHASE));
            logger.info("======createOrders交易佣金{},", trades);
            //四舍五入到分，单位是分，这里取百分之一的手续费
            //int platSplit=new BigDecimal(distributeResponse.getOrderRealPrice()*0.01).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            //支付平台收取的支付佣金,支付完成之后记录
            trades.setPlatSplit(0);//支付平台代收手续费
            trades.setBuyerMessage(homeDeliveryAndStoresInvite.getBuyerMessage());
            trades.setSettlementType(0);
            trades.setDistributorId(0);

            logger.info("创建订单信息：" + trades.toString());
            if (!StringUtil.isEmpty(homeDeliveryAndStoresInvite.getFlag()) && homeDeliveryAndStoresInvite.getFlag().equals(1)) {
                //该订单为预约医生服务类订单
                trades.setServceTpye(100);
            }


            if (null != homeDeliveryAndStoresInvite.getGroupPurchase()) {
                //该订单为拼团活动类型订单
                trades.setServceType(50);
            }
            //---------------------------------积分兑换---------------------------------------------------
            if (homeDeliveryAndStoresInvite.getExchange() != null && homeDeliveryAndStoresInvite.getExchange()) {
                // 使用积分兑换 需要兑换的积分小于可兑换值
                int needIntegral = Optional.ofNullable(distributeResponse.getNeedIntegral()).orElse(0);
                int memberIntegral = Optional.ofNullable(bShopMember.getIntegrate()).orElse(BigInteger.ZERO).intValue();
                int offlineScore = 0;
                if (merchantMapper.getMerchant(String.valueOf(trades.getSiteId())).getOffintegral_use() == 1) {
                    Map<String, Object> map = offlineIntegrateService.getOffTotalScore(trades.getSiteId(), bShopMember.getMobile());
                    if (Integer.valueOf(map.get("code").toString()) == 0) {
                        try {
                            ArrayList res = JacksonUtils.getInstance().convertValue(map.get("info"), ArrayList.class);
                            Object data = res.get(0);

                            System.out.println(res);
                            System.out.println(data);

                            Map score = JacksonUtils.json2map(JacksonUtils.obj2json(data));

                            String str = score.get("gold_score").toString();//浮点变量a转换为字符串str
                            int idx = str.lastIndexOf(".");//查找小数点的位置
                            String strNum = str.substring(0, idx);//截取从字符串开始到小数点位置的字符串，就是整数

                            offlineScore = Integer.valueOf(strNum);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                if (needIntegral > memberIntegral + offlineScore) {
                    response.setCode("0007");
                    response.setMessage("用户剩余积分不足 无法兑换");

                    return response;
                } else {
                    // 减去用户积分
                    List<Integer> goodIds = homeDeliveryAndStoresInvite.getOrderGoods().stream().map(OrderGoods::getGoodsId).collect(toList());

                    int offlineScoreUse = 0;
                    if (needIntegral > memberIntegral && needIntegral <= memberIntegral + offlineScore) {
                        //商品需要的积分大于用户线上积分，小于线上和线下积分的总和，
                        offlineScoreUse = needIntegral - memberIntegral;
                    }
                    //获取积分商品
                    Map<String, Object> goodsInfo = integralGoodsMapper.queryIntegralGoodsByGoodsId(bMember.getSiteId(), goodIds.get(0));
                    if (StringUtil.isEmpty(goodsInfo)) {
                        response.setCode(CommonConstant.TRADES_RESP_CODE_GOODS_NOT_NORMAL);
                        response.setMessage("该商品不可兑换");
                        return response;
                    }
                    //积分商品是否设置每人兑换限制
                    if (Integer.parseInt(goodsInfo.get("limit_each").toString()) > 0) {
                        int convertibility = integralGoodsMapper.getConvertibility(bMember.getBuyerId(), bMember.getSiteId(), Integer.parseInt(goodsInfo.get("goods_id").toString()));
                        if (convertibility >= Integer.parseInt(goodsInfo.get("limit_each").toString())) {
                            response.setCode(CommonConstant.TRADES_RESP_CODE_INTEGRAL_LIMIT_EACH);
                            response.setMessage("个人兑换已达上限，无法继续兑换");
                            return response;
                        }
                    }
                    if (orderService.memberUseIntegral(bShopMember, goodsInfo, needIntegral, tradesId, offlineScoreUse) == 0) {
                        response.setCode("0008");
                        response.setMessage("积分抵扣失败");

                        return response;
                    } else {
                        //trades.setPayStyle(CommonConstant.TRADES_PAY_TYPE_INTEGRAL);
                        tradesExt.setIntegralUsed(needIntegral);
                    }
                }
            }
            //---------------------------------积分兑换---------------------------------------------------
            //查看该商家是否是服务商商户
            String fw=balanceService.boolIsProvider(homeDeliveryAndStoresInvite.getSiteId());
            if("YES".equals(fw)){
                trades.setIsServiceOrder(1);
                response.setIsServiceOrder(1);
            }
            //创建送货上门订单和门店自提订单
            int flag = ordersMapper.addDirectOrderTrades(trades);
            logger.info("======createOrders创建送货上门订单和门店自提订单{},", flag);
            //订单创建成功
            if (flag == 1) {
                List<OrderGoods> orderGoods = homeDeliveryAndStoresInvite.getOrderGoods();
                //------------------------为分销商品和普通分别设置实际金额---------------yeah--------------------------------------------------------
                distributeOrderServiceOld.setGoodsPrice(orderGoods, homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getMobile());
                //------------------------为分销商品和普通分别设置实际金额---------------yeah--------------------------------------------------------
                //记录子订单信息
                orderService.addOrders(orderGoods, tradesId, homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getTradesStore(), distributeResponse.getGoodsInfoInfos(),trades.getServceType());

                // 创建拼团数据信息
                if (null != homeDeliveryAndStoresInvite.getGroupPurchase()) {
                    orderService.addGroupPurchase(orderGoods, tradesId, homeDeliveryAndStoresInvite);
                    homeDeliveryAndStoresInvite.setPromActivityIdArr(homeDeliveryAndStoresInvite.getGroupPurchase().getProActivityId().toString());
                }

                //计算积分抵扣,用户下单成功之后，扣除用户该次使用的积分
                if (!StringUtil.isEmpty(homeDeliveryAndStoresInvite.getIntegralUse()) && homeDeliveryAndStoresInvite.getIntegralUse() > 0) {
                    /*int integral = bShopMember.getIntegrate().intValue() - homeDeliveryAndStoresInvite.getIntegralUse();
                    BMember bMemberIntegrate = new BMember();
                    bMemberIntegrate.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
                    bMemberIntegrate.setBuyerId(userId);
                    bMemberIntegrate.setIntegrate(BigInteger.valueOf(integral));
                    ordersMapper.updateBShopMember(bMemberIntegrate);*/
                    //扣积分，调用积分接口
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("siteId", homeDeliveryAndStoresInvite.getSiteId());
                    params.put("orderAmount", distributeResponse.getOrderOriginalPrice());
                    params.put("buyerId", bShopMember.getBuyerId());
                    params.put("useNum", homeDeliveryAndStoresInvite.getIntegralUse());
                    params.put("integralDesc", "积分抵现金：" + tradesId);
                    //扣积分
                    JSONObject json = integralService.integralDiff(params);
                    logger.info("--------扣积分:" + json);
                }

                //创建订单交易扩展信息
                tradesExt.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
                tradesExt.setTradesId(tradesId);
                tradesExt.setIntegralPreAward(0);//欲送积分
                tradesExt.setIntegralFinalAward(0);//实送积分
                //判断用户是否首单
                if (orderService.checkUserFirstOrder(homeDeliveryAndStoresInvite.getSiteId(), userId)) {
                    tradesExt.setIsFirstOrder(CommonConstant.USER_TRADES_IS_FIRST_ORDER_YES);
                } else {
                    tradesExt.setIsFirstOrder(CommonConstant.USER_TRADES_IS_FIRST_ORDER_NO);
                }
                tradesExt.setIntegralPrice(distributeResponse.getIntegralDeductionPrice());//积分抵扣的订单金额(单位：分)
                tradesExt.setReduceReductionAmount(0);//满减减少的金额
                tradesExt.setBjDiscountAmount(0);//第二件半价活动优惠金额
                tradesExt.setUserCouponId(homeDeliveryAndStoresInvite.getUserCouponId());//使用的优惠券ID
                tradesExt.setUserCouponAmount(distributeResponse.getCouponDeductionPrice());//优惠券抵扣金额
                tradesExt.setDistance(distributeResponse.getMinDistance());
                ordersMapper.addTradesExt(tradesExt);
                logger.info("======createOrders创建订单交易扩展信息{},", tradesExt);
                //有分销优惠则创建分销优惠记录
                if(Objects.nonNull(distributeResponse.getDistributeDiscountPrice()) && distributeResponse.getDistributeDiscountPrice() > 0 && trades.getSiteId() != null && Objects.nonNull(tradesId)){
                    Map<String,Object> discountParam=new HashMap<>();
                    discountParam.put("siteId",trades.getSiteId());
                    discountParam.put("tradesId",tradesId);
                    discountParam.put("tradesDiscount",distributeResponse.getDistributeDiscountPrice());
                    ordersMapper.addDistributorDiscount(discountParam);

                }
                //修改会员信息
                //bShopMember.setOrderFee(bShopMember.getOrderFee() + distributeResponse.getOrderRealPrice());
                //bShopMember.setOrderNum(bShopMember.getOrderNum() + 1);
                //ordersMapper.updateOrderMember(bShopMember);

                if (!StringUtil.isEmpty(homeDeliveryAndStoresInvite.getTradesSource()) &&!StringUtil.isEmpty(homeDeliveryAndStoresInvite.getTradesStore()) && homeDeliveryAndStoresInvite.getTradesSource().equals(130)&&!trades.getTradesStore().equals(trades.getAssignedStores())) {
                    //APP下单默认加调店日志
                    tradesMapper.insertTradesAssignLog(trades.getSiteId(), trades.getTradesId(),
                            trades.getTradesStore(),trades.getPostStyle(), trades.getAssignedStores(),trades.getPostStyle(),
                             homeDeliveryAndStoresInvite.getStoreUserId().toString());
                }
            } else {
                response.setCode(CommonConstant.TRADES_RESP_CODE_FAILED);
                response.setMessage("订单创建失败");
                return response;
            }
            if (!StringUtil.isEmpty(homeDeliveryAndStoresInvite.getFlag()) && homeDeliveryAndStoresInvite.getFlag().equals(1)) {
                ServceOrder servceOrder = new ServceOrder();
                servceOrder.setSchedulePersonId(homeDeliveryAndStoresInvite.getSchedulePersonId());
                servceOrder.setDiagnoseStatus(homeDeliveryAndStoresInvite.getDiagnoseStatus());
                servceOrder.setDiseaseInfo(homeDeliveryAndStoresInvite.getDiseaseInfo());
                servceOrder.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
                servceOrder.setUseDetailId(homeDeliveryAndStoresInvite.getUseDetailId());
                servceOrder.setServeStatus(0);
                servceOrder.setCreateTime(new Date());
                servceOrder.setUpdateTime(servceOrder.getCreateTime());
                servceOrder.setGoodsId(homeDeliveryAndStoresInvite.getOrderGoods().get(0).getGoodsId());
                servceOrder.setStoreId(Integer.valueOf(homeDeliveryAndStoresInvite.getTradesStore()));
                servceOrder.setTradesId(tradesId + "");
                servceOrder.setBookingNo(homeDeliveryAndStoresInvite.getSiteId() + "" + System.currentTimeMillis() / 1000);
                servceOrder.setAmount(homeDeliveryAndStoresInvite.getAccountSource());
                servceOrder.setUseCount(homeDeliveryAndStoresInvite.getAccountSource() - homeDeliveryAndStoresInvite.getUseCount() + 1);
                //向预约表中插入一条数据
                Boolean foo = servceOrderService.insert(servceOrder);
                //修改b_servce_use_detail表中的预定人数
                ServceUseDetail servceUseDetail = new ServceUseDetail();
                servceUseDetail.setUseCount(homeDeliveryAndStoresInvite.getUseCount() - 1);
                servceUseDetail.setMineClassesId(homeDeliveryAndStoresInvite.getMineClassesId());
                servceUseDetail.setTemplateNo(homeDeliveryAndStoresInvite.getTemplateNo());
                servceUseDetailMapper.updateByTemplateNoAndMineClassesId(servceUseDetail);
            }
            response.setCode(CommonConstant.TRADES_RESP_CODE_SUCCESS);
            response.setMessage("订单创建成功");
            response.setTradesId(String.valueOf(tradesId));
            response.setSiteId(String.valueOf(homeDeliveryAndStoresInvite.getSiteId()));

            Map<String,Object> paInfo=new HashMap<>();
            paInfo.put("siteId",trades.getSiteId());
            paInfo.put("tradesId",tradesId);
            paInfo.put("orderNo",homeDeliveryAndStoresInvite.getOrderNo());
            paInfo.put("file",homeDeliveryAndStoresInvite.getFilePdf());
            paInfo.put("secondToken",homeDeliveryAndStoresInvite.getSecondToken());
            paInfo.put("diagnosticResults",homeDeliveryAndStoresInvite.getDiagnosticResults());
            paInfo.put("prescriptionNo",homeDeliveryAndStoresInvite.getPrescriptionNo());
            ordersMapper.addpaInfo(paInfo);

            /* todo 超级优惠上线后删除
            // 修改优惠券使用状态
            if (!StringUtil.isEmpty(beforeCreateOrderReq.getCouponId()) && beforeCreateOrderReq.getCouponId() != 0) {
                CouponDetail couponDetail = couponDetailMapper.getCouponDetailByCouponId(homeDeliveryAndStoresInvite.getSiteId(), beforeCreateOrderReq.getCouponId());
                if (null != couponDetail) {
                    couponRuleMapper.updateUseAmountByRuleId(homeDeliveryAndStoresInvite.getSiteId(), couponDetail.getRuleId());
                    couponDetailMapper.updateStatusById(homeDeliveryAndStoresInvite.getSiteId(), beforeCreateOrderReq.getCouponId(), String.valueOf(tradesId));
                    couponInformErpService.ifContainCrashCouponThenSendQueueMessage(couponDetail);
                }
                logger.info("======createOrders修改优惠券状态{},", couponDetail);
            }*/

           /* if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getFlag())) {
                //分单电话提醒门店（门店自提）
                if (!StringUtils.isEmpty(distributeResponse.getStore()) && !StringUtils.isEmpty(distributeResponse.getStore().getTel()) && CommonConstant.ORDER_TYPE_HOME_DELIVERY.equals(homeDeliveryAndStoresInvite.getOrderType())) {
                *//*new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }).start();*//*
                    Map<String, Object> str = _7moorService.webcall(distributeResponse.getStore().getTel().replace("-", ""));
                    logger.info("分单电话提醒门店反馈：" + str);
                }
            }*/

            //修改首单时间
            labelService.saveFirstOrderTime(homeDeliveryAndStoresInvite.getSiteId(), userId);

            //零元订单直接支付成功
            if (trades.getRealPay().equals(0)) {
                try {
                    boolean pay = payService.noMoneyPay(trades);
                    //tradesService.sendPaySuccessMsg(trades);
                    logger.info("零元订单支付：" + pay);
                    erpToolsService.erpOrdersService(trades.getSiteId(), tradesId);
                    //------ 生成零元订单 电子小票 ------start
                    logger.info("生成零元订单 电子小票开始------：");
                    smallTicketService.callBack(bShopMember, tradesId);
                    //------ 生成零元订单 电子小票 ------end
                } catch (BusinessLogicException e) {
                    e.printStackTrace();
                }
                response.setCode(CommonConstant.TRADES_RESP_CODE_SUCCESS_NOPAY);
            }

            /*保存优惠活动数据 zw start*/
            if (!StringUtil.isBlank(homeDeliveryAndStoresInvite.getPromActivityIdArr()) || homeDeliveryAndStoresInvite.getUserCouponId() != null) {
                // fixme 不仅仅是赠品需要保存，优惠金额也需要保存
                try {
                    promotionsOrderService.saveConcessionResultToTable(homeDeliveryAndStoresInvite.getSiteId(),
                        tradesId,
                        homeDeliveryAndStoresInvite,
                        distributeResponse);

                } catch (Exception e) {
                    logger.error("异常发生,{}", e);
                }
            }
            /*保存优惠活动数据 end*/
            return response;
        } else {
            response.setCode(CommonConstant.TRADES_RESP_CODE_FAILED);
            response.setMessage("订单创建失败");
            return response;
        }
    }
}
