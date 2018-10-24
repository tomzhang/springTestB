package com.jk51.modules.order.service;

import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.account.models.AccountCommissionRate;
import com.jk51.model.distribute.Distributor;
import com.jk51.model.grouppurchase.GroupPurchase;
import com.jk51.model.map.Coordinate;
import com.jk51.model.order.*;
import com.jk51.model.order.response.DistributeResponse;
import com.jk51.model.order.response.OrderResponse;
import com.jk51.model.order.response.UpdateOrderPayStyleReq;
import com.jk51.model.registration.models.ServceOrder;
import com.jk51.model.registration.models.ServceUseDetail;
import com.jk51.modules.account.mapper.AccountCommissionRateMapper;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.distribution.mapper.DistributorMapper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.grouppurchase.mapper.GroupPurChaseMapper;
import com.jk51.modules.grouppurchase.service.GroupPurChaseService;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.integral.mapper.IntegralGoodsMapper;
import com.jk51.modules.integral.mapper.IntegralMapper;
import com.jk51.modules.integral.service.IntegralService;
import com.jk51.modules.map.service.MapService;
import com.jk51.modules.merchant.mapper.MerchantExtMapper;
import com.jk51.modules.merchant.service.LabelService;
import com.jk51.modules.offline.service.ErpToolsService;
import com.jk51.modules.offline.service.OfflineIntegrateService;
import com.jk51.modules.order.mapper.DistributeOrderMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.pay.service.PayService;
import com.jk51.modules.persistence.mapper.UserStoresDistanceMapper;
import com.jk51.modules.promotions.service.PromotionsOrderService;
import com.jk51.modules.registration.mapper.ServceUseDetailMapper;
import com.jk51.modules.registration.service.ServceOrderService;
import com.jk51.modules.smallTicket.service.SmallTicketService;
import com.jk51.modules.sms.service.Sms7MoorService;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.modules.treat.mapper.MerchantMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:订单下单service
 * 作者: baixiongfei
 * 创建日期: 2017/2/15
 * 修改记录:
 */
@Service
public class OrderService {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private UserStoresDistanceMapper userStoresDistanceMapper;

    @Autowired
    private MemberMapper memberMapper;
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
    private GroupPurChaseService groupPurChaseService;

    @Autowired
    private GroupPurChaseMapper groupPurChaseMapper;


    @Autowired
    private DistributeOrderService distributeOrderService;

    @Autowired
    private MapService mapService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private IntegralMapper mapper;
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

    public static final Logger logger = LoggerFactory.getLogger(OrderService.class);
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
        String userName = "";
        logger.info("======createOrders查询下单用户信息:SiteId{}，手机号{},", homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getMobile());
        //查询用户是否已存在
        BMember bMember = ordersMapper.getMemberById(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getMobile());
        if (bMember != null) {
            userId = bMember.getBuyerId();
            userName = bMember.getName();
        } else {
            //查询51jk中心库是否有匿名用户，没有的话则创建一个,有的话，则更新注册商户
            YBMember ybzfUser = ordersMapper.getYBMemberByMobile(homeDeliveryAndStoresInvite.getMobile());
            if (ybzfUser == null) {
                //创建匿名用户
                ybzfUser = new YBMember();
                ybzfUser.setMobile(homeDeliveryAndStoresInvite.getMobile());
                ybzfUser.setPasswd(SHA1(generatePWD()));//默认密码
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
        }
        logger.info("======createOrders根据会员ID查询商家会员用户:userId{}，userName{},", userId, userName);
        if (userId > 0) {
            //通过的商品ids查询商品信息
            List<GoodsInfo> goodsInfos = distributeOrderService.getGoodsInfos(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getOrderGoods(), null);
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
            beforeCreateOrderReq.setStoresId(Integer.parseInt(homeDeliveryAndStoresInvite.getTradesStore()));
            beforeCreateOrderReq.setOrderGoods(homeDeliveryAndStoresInvite.getOrderGoods());
            beforeCreateOrderReq.setCouponId(homeDeliveryAndStoresInvite.getUserCouponId());
            beforeCreateOrderReq.setIntegralUse(StringUtil.isEmpty(homeDeliveryAndStoresInvite.getIntegralUse()) ? 0 : homeDeliveryAndStoresInvite.getIntegralUse());
            beforeCreateOrderReq.setOrderType(homeDeliveryAndStoresInvite.getOrderType());
            beforeCreateOrderReq.setExchange(homeDeliveryAndStoresInvite.getExchange());
            String province = ordersMapper.getAreaId(homeDeliveryAndStoresInvite.getReceiverProvinceCode());
            String city = ordersMapper.getAreaId(homeDeliveryAndStoresInvite.getReceiverCityCode());
            String receiverCountry = ordersMapper.getAreaId(homeDeliveryAndStoresInvite.getReceiverCountryCode());
            beforeCreateOrderReq.setAddress((StringUtil.isEmpty(city) ? "" : city) + (StringUtil.isEmpty(receiverCountry) ? "" : receiverCountry) + homeDeliveryAndStoresInvite.getReceiverAddress());
            beforeCreateOrderReq.setReceiverCityCode(homeDeliveryAndStoresInvite.getReceiverCityCode());
            beforeCreateOrderReq.setPromActivityIdArr(homeDeliveryAndStoresInvite.getPromActivityIdArr()); //活动ids  zw
            beforeCreateOrderReq.setUserId(homeDeliveryAndStoresInvite.getUserId());
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
            long tradesId = getTradesId(homeDeliveryAndStoresInvite.getSiteId());
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
                            trades.setAssignedStores(distributeOrderService.getQYStore(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite));//先查询总仓库,默认总部
                        }
                    }
                    trades.setCreateOrderAssignedStores(distributeResponse.getStore().getId());

                } else {
                    trades.setAssignedStores(distributeOrderService.getQYStore(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite));//先查询总仓库,默认总部
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
            trades.setTradesStore(Integer.parseInt(homeDeliveryAndStoresInvite.getTradesStore()));
            trades.setStockupStatus(CommonConstant.STOCKUP_WAIT_READY);//送货上门订单和门店自提订单初始创建时默认为：未备货
            trades.setIsPayment(CommonConstant.IS_PAYMENT_ZERO);//默认为0：未付款
            trades.setAccountCheckingStatus(0);//默认为0：待处理
            boolean prescriptionFlag = false;
            //判断是否为处方药订单
            for (GoodsInfo goodsInfo : goodsInfos) {
                if (goodsInfo.getDrugCategory() == 130) {
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
            trades.setTradesSplit(getTradesCommission(homeDeliveryAndStoresInvite.getSiteId(), distributeResponse.getOrderRealPrice(), CommonConstant.TRADESCOMMISSION_ORDER_TYPE_DIRECT_PURCHASE));
            logger.info("======createOrders交易佣金{},", trades);
            //四舍五入到分，单位是分，这里取百分之一的手续费
            //int platSplit=new BigDecimal(distributeResponse.getOrderRealPrice()*0.01).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            //支付平台收取的支付佣金,支付完成之后记录
            trades.setPlatSplit(0);//支付平台代收手续费
            trades.setBuyerMessage(homeDeliveryAndStoresInvite.getBuyerMessage());

            //--------------------------------设置分销商id------------------------------------------------
            try {
                logger.info("======fenxiao 订单信息:{}, {}", trades, JacksonUtils.obj2json(distributeResponse));
                logger.info("======fenxiao 订单信息推荐人id{}", homeDeliveryAndStoresInvite.getDistributorId());
                logger.info("======fenxiao 订单信息推荐商品总价{}", distributeResponse.getDistributePrice());
            } catch (Exception e) {
            }

            AccountCommissionRate accountCommissionRate = accountCommissionRateMapper.getCommissionRatById(homeDeliveryAndStoresInvite.getSiteId());

            if (null != homeDeliveryAndStoresInvite.getDistributorId()) {//存在分销商推荐人
                trades.setSettlementType(1);
                //分销佣金
                if (null != accountCommissionRate) {
                    double distributor_commission = accountCommissionRate.getDistributor_rate() / 100 * distributeResponse.getOrderRealPrice();
                    BigDecimal bg = new BigDecimal(distributor_commission).setScale(0, RoundingMode.HALF_UP);
                    trades.setTradesSplit(Integer.parseInt(bg.toString()));
                }
                Distributor distributor = this.distributorMapper.selectByUid(homeDeliveryAndStoresInvite.getDistributorId(), bMember.getSiteId());
                if (null != distributor) {
                    trades.setDistributorId(distributor.getId());
                }
                //买家不是分销商，并通过招募书分享链接购买商品
                if (null == distributor && homeDeliveryAndStoresInvite.getDistributorId() == 0) {
                    trades.setDistributorId(Integer.MAX_VALUE - 1);
                }

            } else if (distributeResponse.getDistributePrice() > 0) {
                trades.setSettlementType(1);
                //分销佣金
                if (null != accountCommissionRate) {
                    double distributor_commission = accountCommissionRate.getDistributor_rate() / 100 * distributeResponse.getOrderRealPrice();
                    BigDecimal bg = new BigDecimal(distributor_commission).setScale(0, RoundingMode.HALF_UP);
                    trades.setTradesSplit(bg.intValue());
                }
                if (!StringUtil.isEmpty(bMember)) {
                    //不存在分销商推荐人，存在分销商品
                    Distributor distributor = this.distributorMapper.selectByUid(trades.getBuyerId(), bMember.getSiteId());
                    logger.info("======fenxiao 订单信息分销商id:{}", distributor);
                    if (null != distributor) {//存在分销商品 买家自己已经是分销商
                        trades.setDistributorId(distributor.getId());
                    } else {
                        //存在分销商品 买家自己还不是分销商
                        trades.setDistributorId(Integer.MAX_VALUE);
                    }
                }
            } else {
                trades.setSettlementType(0);
                trades.setDistributorId(0);
            }
            //--------------------------------设置分销商id------------------------------------------------

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
                    if (memberUseIntegral(bShopMember, goodsInfo, needIntegral, tradesId, offlineScoreUse) == 0) {
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
                distributeOrderService.setGoodsPrice(orderGoods, homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getMobile());
                //------------------------为分销商品和普通分别设置实际金额---------------yeah--------------------------------------------------------
                //记录子订单信息
                addOrders(orderGoods, tradesId, homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getTradesStore(), distributeResponse.getGoodsInfoInfos(),trades.getServceType());

                // 创建拼团数据信息
                if (null != homeDeliveryAndStoresInvite.getGroupPurchase()) {
                    addGroupPurchase(orderGoods, tradesId, homeDeliveryAndStoresInvite);
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
                if (checkUserFirstOrder(homeDeliveryAndStoresInvite.getSiteId(), userId)) {
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


    //验证是否满足兑换资格
    @Transactional
    public OrderResponse convertIntegral(HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite) {
        OrderResponse response = new OrderResponse();
        Map<String, Object> memberMap = new HashMap<String, Object>();
        BMember bMember = ordersMapper.getMemberById(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getMobile());
        if (StringUtil.isEmpty(bMember)) {
            response.setCode(CommonConstant.TRADES_RESP_CODE_PHONE_FAILED);
            response.setMessage("该用户还不是会员");
            return response;
        }
        //积分商品兑换为单个兑换
        List<Integer> goodIds = homeDeliveryAndStoresInvite.getOrderGoods().stream().map(OrderGoods::getGoodsId).collect(toList());
        //获取积分商品
        Map<String, Object> goodsInfos = integralGoodsMapper.queryIntegralGoodsByGoodsId(bMember.getSiteId(), goodIds.get(0));
        //总部商品
        GoodsInfo goods = distributeOrderMapper.getGoodsInfo(bMember.getSiteId(), goodIds.get(0));
        if (StringUtil.isEmpty(goodsInfos) || StringUtil.isEmpty(goods)) {
            response.setCode(CommonConstant.TRADES_RESP_CODE_GOODS_NOT_NORMAL);
            response.setMessage("商品状态不正常，不可兑换");
            return response;
        }
        //用户当前积分
        BigInteger memberIntegral = bMember.getIntegrate();
        //当前商品兑换所需积分
        BigInteger goodIntergral = BigInteger.valueOf(Long.valueOf(goodsInfos.get("intrgral_exchanges").toString()));
        if (goodIntergral.compareTo(memberIntegral) > 0) {
            response.setCode(CommonConstant.TRADES_RESP_CODE_INTEGRAL_TOO);
            response.setMessage("商品积分超过用户现有的积分值");
            return response;
        }
        if (Integer.parseInt(goodsInfos.get("num").toString()) >= Integer.parseInt(goodsInfos.get("limit_count").toString()) && Integer.parseInt(goodsInfos.get("limit_count").toString()) > 0) {
            response.setCode(CommonConstant.TRADES_RESP_CODE_INTEGRAL_LIMIT);
            response.setMessage("积分商品已达上限，无法兑换");
            return response;
        }
        //订单交易表
        Trades trades = new Trades();
        //查询商家信息
        Merchant merchant = distributeOrderMapper.getMerchant(homeDeliveryAndStoresInvite.getSiteId());
        //查询商家账号信息
        YBAccount ybAccount = ordersMapper.getYBAccountById(homeDeliveryAndStoresInvite.getSiteId(), 1);
        //获取自动分单信息，价格信息及运费信息
        BeforeCreateOrderReq beforeCreateOrderReq = new BeforeCreateOrderReq();
        beforeCreateOrderReq.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
        beforeCreateOrderReq.setBuyerId(bMember.getBuyerId());
        beforeCreateOrderReq.setAddrCode(homeDeliveryAndStoresInvite.getReceiverProvinceCode());
        beforeCreateOrderReq.setMobile(homeDeliveryAndStoresInvite.getMobile());
        beforeCreateOrderReq.setStoresId(Integer.parseInt(homeDeliveryAndStoresInvite.getTradesStore()));
        beforeCreateOrderReq.setOrderGoods(homeDeliveryAndStoresInvite.getOrderGoods());
        beforeCreateOrderReq.setCouponId(homeDeliveryAndStoresInvite.getUserCouponId());
        beforeCreateOrderReq.setIntegralUse(homeDeliveryAndStoresInvite.getIntegralUse());
        beforeCreateOrderReq.setOrderType(homeDeliveryAndStoresInvite.getOrderType());
        String province = ordersMapper.getAreaId(homeDeliveryAndStoresInvite.getReceiverProvinceCode());
        String city = ordersMapper.getAreaId(homeDeliveryAndStoresInvite.getReceiverCityCode());
        String receiverCountry = ordersMapper.getAreaId(homeDeliveryAndStoresInvite.getReceiverCountryCode());
        beforeCreateOrderReq.setAddress(province + city + receiverCountry + homeDeliveryAndStoresInvite.getReceiverAddress());
        beforeCreateOrderReq.setReceiverCityCode(homeDeliveryAndStoresInvite.getReceiverCityCode());

        //调用分单
        DistributeResponse distributeResponse = distributeOrderService.beforeOrder(beforeCreateOrderReq, "1");

        //订单金额控制不能为负数
        if (distributeResponse.getOrderRealPrice() < 0) {
            response.setCode(CommonConstant.TRADES_RESP_CODE_INTEGRAL_TOO);
            response.setMessage("订单金额控制不能为负数");
            return response;
        }
        long tradesId = getTradesId(homeDeliveryAndStoresInvite.getSiteId());
        trades.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
        trades.setTradesId(tradesId);
        trades.setSellerId(merchant.getMerchantId());
        trades.setBuyerId(bMember.getBuyerId());
        trades.setSellerName(merchant.getMerchantName());
        trades.setBuyerNick(bMember.getName());
        trades.setReceiverPhone(homeDeliveryAndStoresInvite.getReceiverPhone());
        if (homeDeliveryAndStoresInvite.getTradesSource() != 130) {
            logger.info("邀请码：" + homeDeliveryAndStoresInvite.getClerkInvitationCode());
            if (!StringUtil.isEmpty(homeDeliveryAndStoresInvite.getClerkInvitationCode())) {
                StoreAdminExt storeAdminExt = storeAdminExtMapper.selectByIvCode(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getClerkInvitationCode());
                trades.setStoreUserId(storeAdminExt.getStoreadmin_id());
            }
        } else {
            trades.setStoreUserId(Integer.parseInt(homeDeliveryAndStoresInvite.getClerkInvitationCode()));
        }
        //门店自提
        if ("2".equals(homeDeliveryAndStoresInvite.getOrderType())) {
            trades.setRecevierMobile(homeDeliveryAndStoresInvite.getMobile());
            trades.setRecevierName(StringUtils.isEmpty(homeDeliveryAndStoresInvite.getReceiverName()) ? bMember.getName() : homeDeliveryAndStoresInvite.getReceiverName());
            //String barcode = tradesService.generateBarcode(trades.getSiteId());
            //trades.setSelfTakenCode(barcode);
            trades.setReceiverAddress("");
            if (StringUtil.isEmpty(trades.getRecevierName())) {
                trades.setRecevierName("--");
            }
        } else {//送货上门

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
        trades.setReceiverCity(city);
        trades.setReceiverZip(homeDeliveryAndStoresInvite.getReceiverZip());
        if (!StringUtil.isEmpty(ybAccount)) {
            trades.setSellerPayNo(ybAccount.getAccount());
        } else {
            trades.setSellerPayNo("zhifubao@ybzf.com");
        }
        trades.setSellerPhone(merchant.getServicePhone());
        trades.setSellerMobile(merchant.getServiceMobile());
        trades.setSellerName(merchant.getMerchantName());
        trades.setTradesStatus(CommonConstant.WAIT_SELLER_SHIPPED);//兑换状态：交易成功
        trades.setTotalFee(distributeResponse.getOrderOriginalPrice());//订单原始金额
        trades.setPostFee(distributeResponse.getOrderFreight());//直购订单和门店自提订单的运费都为0
        trades.setPostageDiscount(distributeResponse.getPostageDiscount());//运费优惠

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
                    trades.setAssignedStores(distributeResponse.getStore().getId());//指定送货门店
                }
                trades.setCreateOrderAssignedStores(distributeResponse.getStore().getId());

            } else {
                trades.setAssignedStores(0);//默认总部
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
        trades.setTradesStore(Integer.parseInt(homeDeliveryAndStoresInvite.getTradesStore()));
        trades.setStockupStatus(CommonConstant.STOCKUP_WAIT_READY);//送货上门订单和门店自提订单初始创建时默认为：未备货
        trades.setIsPayment(CommonConstant.IS_PAYMENT_ONE);//默认为1：积分兑换
        trades.setAccountCheckingStatus(0);//默认为0：待处理
        trades.setPayStyle(CommonConstant.TRADES_PAY_TYPE_INTEGRAL);
        boolean prescriptionFlag = false;
        //判断是否为处方药订单
        if (goods.getDrugCategory() == 130) {
            prescriptionFlag = true;
        }
        if (prescriptionFlag) {
            trades.setPrescriptionOrders(CommonConstant.TRADES_PRESCRIPTION_TRUE);
        } else {
            trades.setPrescriptionOrders(CommonConstant.TRADES_PRESCRIPTION_FALSE);
        }
        //订单的实际支付金额
        trades.setRealPay(0);
        //交易佣金
        trades.setTradesSplit(getTradesCommission(homeDeliveryAndStoresInvite.getSiteId(), distributeResponse.getOrderRealPrice(), CommonConstant.TRADESCOMMISSION_ORDER_TYPE_DIRECT_PURCHASE));
        //支付平台收取的支付佣金,支付完成之后记录
        trades.setPlatSplit(0);
        trades.setBuyerMessage(homeDeliveryAndStoresInvite.getBuyerMessage());

        trades.setDistributorId(0);
        logger.info("创建订单信息：" + trades.toString());
        if (!StringUtil.isEmpty(homeDeliveryAndStoresInvite.getFlag()) && homeDeliveryAndStoresInvite.getFlag().equals(1)) {
            //该订单为预约医生服务类订单
            trades.setServceTpye(100);
        }
        //查看该商家是否是服务商商户
        String fw=balanceService.boolIsProvider(homeDeliveryAndStoresInvite.getSiteId());
        if("YES".equals(fw)){
            trades.setIsServiceOrder(1);
            response.setIsServiceOrder(1);
        }
        //创建送货上门订单和门店自提订单
        int flag = ordersMapper.addDirectOrderTrades(trades);
        if (flag == 1) {
            List<OrderGoods> orderGoods = homeDeliveryAndStoresInvite.getOrderGoods();
            this.distributeOrderService.setGoodsPrice(orderGoods, homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getMobile());
            //记录子订单信息
            addIntegralOrders(orderGoods, tradesId, homeDeliveryAndStoresInvite.getSiteId());
            //预约服务
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

            //创建订单交易扩展信息
            TradesExt tradesExt = new TradesExt();
            tradesExt.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
            tradesExt.setTradesId(tradesId);
            tradesExt.setIntegralUsed(Integer.parseInt(goodIntergral.toString()));//用掉的积分
            tradesExt.setIntegralPreAward(0);//欲送积分
            tradesExt.setIntegralFinalAward(0);//实送积分
            //判断用户是否首单
            if (checkUserFirstOrder(homeDeliveryAndStoresInvite.getSiteId(), bMember.getBuyerId())) {
                tradesExt.setIsFirstOrder(CommonConstant.USER_TRADES_IS_FIRST_ORDER_YES);
            } else {
                tradesExt.setIsFirstOrder(CommonConstant.USER_TRADES_IS_FIRST_ORDER_NO);
            }
            tradesExt.setIntegralPrice(distributeResponse.getIntegralDeductionPrice());//积分抵扣的订单金额(单位：分)
            tradesExt.setReduceReductionAmount(0);//满减减少的金额
            tradesExt.setBjDiscountAmount(0);//第二件半价活动优惠金额
            tradesExt.setUserCouponId(homeDeliveryAndStoresInvite.getUserCouponId());//使用的优惠券ID
            tradesExt.setUserCouponAmount(distributeResponse.getCouponDeductionPrice());//优惠券抵扣金额
            ordersMapper.addTradesExt(tradesExt);

            Map<String, Object> param = new HashMap<String, Object>();
            param.put("siteId", bMember.getSiteId());
            param.put("buyerId", bMember.getBuyerId());
            param.put("buyerNick", bMember.getName());
            param.put("integralDesc", "积分兑换");
            param.put("integralAdd", 0);
            param.put("integralDiff", goodsInfos.get("intrgral_exchanges").toString());//bi1.subtract(bi2)
            param.put("integralOverplus", bMember.getIntegrate().subtract(BigInteger.valueOf(Long.valueOf(goodsInfos.get("intrgral_exchanges").toString()))));
            param.put("mark", "积分兑换,订单号:" + tradesId);
            param.put("totalGetIntegrate", 0);//获得积分
            param.put("totalConsumeIntegrate", bMember.getTotalConsumeIntegrate().add(goodIntergral));//使用积分
            int integrate = bMember.getIntegrate().intValue() - NumberUtils.toInt(String.valueOf(goodsInfos.get("intrgral_exchanges")));
            param.put("integrate", integrate < 0 ? 0 : integrate);
            param.put("id", goodsInfos.get("id"));
            param.put("num", goodsInfos.get("num"));
            param.put("type", CommonConstant.TYPE_ORDER_CONVENT);
            int log = mapper.logAdd(param);
            int change = mapper.updateMemberData(param);
            int info = mapper.updateMemberInfoData(param);
            int changeNum = integralGoodsMapper.updateIntegralGoodsNum(param);
            if (log == 1 && change == 1 && changeNum == 1 && info == 1) {
                response.setCode(CommonConstant.TRADES_RESP_CODE_SUCCESS);
                response.setMessage("订单创建成功");
                response.setTradesId(String.valueOf(tradesId));
                response.setSiteId(String.valueOf(homeDeliveryAndStoresInvite.getSiteId()));

            }
            return response;
        } else {
            response.setCode(CommonConstant.TRADES_RESP_CODE_FAILED);
            response.setMessage("订单创建失败");
            return response;
        }

    }

//    /**
//     * 创建服务类订单
//     *
//     * @param
//     * @return
//     */
//    public OrderResponse createDoctorServceOrders(HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite){
//        OrderResponse response = new OrderResponse();
//        response.setSiteId(String.valueOf(homeDeliveryAndStoresInvite.getSiteId()));
//        //校验必选参数是否为空
//        String checkParams = setHomeDeliveryAndStoresInviteReqParams(homeDeliveryAndStoresInvite);
//        if (StringUtil.isNotEmpty(checkParams)) {
//            response.setCode(CommonConstant.TRADES_RESP_CODE_MISSINF_PARAMS);
//            response.setMessage(String.format("必选参数：[%s] 为空",checkParams));
//            return response;
//        }
//        int userId = 0;
//        String userName = "";
//        //查询用户是否已存在
//        BMember bMember = ordersMapper.getMemberById(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getMobile());
//        if (bMember != null) {
//            userId = bMember.getBuyerId();
//            userName = bMember.getName();
//        } else {
//            //查询51jk中心库是否有匿名用户，没有的话则创建一个,有的话，则更新注册商户
//            YBMember ybzfUser = ordersMapper.getYBMemberByMobile(homeDeliveryAndStoresInvite.getMobile());
//            if (ybzfUser == null) {
//                //创建匿名用户
//                ybzfUser = new YBMember();
//                ybzfUser.setMobile(homeDeliveryAndStoresInvite.getMobile());
//                ybzfUser.setPasswd(SHA1(generatePWD()));//默认密码
//                ybzfUser.setIsActivated(0);
//                ybzfUser.setReginSource(9999);//注册来源
//                ybzfUser.setBUsersarr(String.valueOf(homeDeliveryAndStoresInvite.getSiteId()));
//                ybzfUser.setRegisterStores(Integer.parseInt(homeDeliveryAndStoresInvite.getTradesStore()));
//                ybzfUser.setName("");
//                ordersMapper.addYBMember(ybzfUser);
//            } else {
//                //更新注册商户
//                String userSiteArr = ybzfUser.getBUsersarr();
//                if (StringUtil.isNotEmpty(userSiteArr)) {
//                    ybzfUser.setBUsersarr(userSiteArr + "," + homeDeliveryAndStoresInvite.getSiteId());
//                } else {
//                    ybzfUser.setBUsersarr(String.valueOf(homeDeliveryAndStoresInvite.getSiteId()));
//                }
//                ordersMapper.updateYBMember(ybzfUser);
//            }
//            userId = ybzfUser.getMemberId();
//            userName = ybzfUser.getName();
//            //根据会员ID查询商家会员用户
//            BMember bShopMember = ordersMapper.getMemberByBuyerId(homeDeliveryAndStoresInvite.getSiteId(), ybzfUser.getMemberId());
//            if (bShopMember == null) {
//                bShopMember = new BMember();
//                bShopMember.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
//                bShopMember.setBuyerId(ybzfUser.getMemberId());
//                bShopMember.setMobile(homeDeliveryAndStoresInvite.getMobile());
//                bShopMember.setPasswd(SHA1(generatePWD()));
//                bShopMember.setIsActivated(0);
//                bShopMember.setMemSource(9999);
//                //bShopMember.setSex(3);
//                bShopMember.setRegisterStores(Integer.parseInt(homeDeliveryAndStoresInvite.getTradesStore()));
//                bShopMember.setIntegrate(BigInteger.ZERO);//积分处理这块还没做好，正常来说是需要注册赠送积分的
//                ordersMapper.addBShopMember(bShopMember);
//            }
//        }
//        if (userId > 0) {
//            //通过的商品ids查询商品信息
//            List<GoodsInfo> goodsInfos = distributeOrderService.getGoodsInfos(homeDeliveryAndStoresInvite.getSiteId(), homeDeliveryAndStoresInvite.getOrderGoods());
//            //检查商品是否可以购买
//            if (!checkGoods(goodsInfos)) {
//                response.setCode(CommonConstant.TRADES_RESP_CODE_GOODS_NOT_NORMAL);
//                response.setMessage("商品不可以购买");
//                return response;
//            }
//
//
//            //订单交易表
//            Trades trades = new Trades();
//            //查询商家信息
//            Merchant merchant = distributeOrderMapper.getMerchant(homeDeliveryAndStoresInvite.getSiteId());
//            //查询商家账号信息
//            YBAccount ybAccount = ordersMapper.getYBAccountById(homeDeliveryAndStoresInvite.getSiteId(), 1);
//            //查询用户信息
//            BMember bShopMember = ordersMapper.getMemberByBuyerId(homeDeliveryAndStoresInvite.getSiteId(), userId);
//
//            //校验积分是否大于0，并且跟用户本身的积分比较，如果用户使用积分值大于用户拥有的积分值，则提示积分不可用
//            if (homeDeliveryAndStoresInvite.getIntegralUse() > 0 && homeDeliveryAndStoresInvite.getIntegralUse() > bShopMember.getIntegrate().intValue()) {
//                response.setCode(CommonConstant.TRADES_RESP_CODE_INTEGRAL_TOO);
//                response.setMessage("积分不能大于用户现有的积分值");
//                return response;
//            }
//
//
//            //获取自动分单信息，价格信息及运费信息
//            BeforeCreateOrderReq beforeCreateOrderReq = new BeforeCreateOrderReq();
//            beforeCreateOrderReq.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
//            beforeCreateOrderReq.setBuyerId(userId);
//            beforeCreateOrderReq.setAddrCode(homeDeliveryAndStoresInvite.getReceiverProvinceCode());
//            beforeCreateOrderReq.setMobile(homeDeliveryAndStoresInvite.getMobile());
//            beforeCreateOrderReq.setStoresId(Integer.parseInt(homeDeliveryAndStoresInvite.getTradesStore()));
//            beforeCreateOrderReq.setOrderGoods(homeDeliveryAndStoresInvite.getOrderGoods());
//            beforeCreateOrderReq.setCouponId(homeDeliveryAndStoresInvite.getUserCouponId());
//            beforeCreateOrderReq.setIntegralUse(homeDeliveryAndStoresInvite.getIntegralUse());
//            beforeCreateOrderReq.setOrderType(homeDeliveryAndStoresInvite.getOrderType());
//            String province=ordersMapper.getAreaId(homeDeliveryAndStoresInvite.getReceiverProvinceCode());
//            String city=ordersMapper.getAreaId(homeDeliveryAndStoresInvite.getReceiverCityCode());
//            beforeCreateOrderReq.setAddress(city+homeDeliveryAndStoresInvite.getReceiverAddress());
//
//            //调用分单
//            DistributeResponse distributeResponse = distributeOrderService.beforeOrder(beforeCreateOrderReq,"1");
//
//            //订单金额控制不能为负数
//            if (distributeResponse.getOrderRealPrice() < 0 ) {
//                response.setCode(CommonConstant.TRADES_RESP_CODE_INTEGRAL_TOO);
//                response.setMessage("订单金额控制不能为负数");
//                return response;
//            }
//            Long tradesId = getTradesId(homeDeliveryAndStoresInvite.getSiteId());
//            trades.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
//            trades.setTradesId(tradesId);
//            trades.setSellerId(merchant.getMerchantId());
//            trades.setBuyerId(bShopMember.getBuyerId());
//            trades.setSellerName(merchant.getMerchantName());
//            trades.setBuyerNick(bShopMember.getName());
//            trades.setReceiverPhone(homeDeliveryAndStoresInvite.getReceiverPhone());
//            if(homeDeliveryAndStoresInvite.getTradesSource()!=130){
//                logger.info("邀请码："+homeDeliveryAndStoresInvite.getClerkInvitationCode());
//                if(!StringUtil.isEmpty(homeDeliveryAndStoresInvite.getClerkInvitationCode())){
//                    StoreAdminExt storeAdminExt=storeAdminExtMapper.selectByIvCode(homeDeliveryAndStoresInvite.getSiteId(),homeDeliveryAndStoresInvite.getClerkInvitationCode());
//                    trades.setStoreUserId(storeAdminExt.getStoreadmin_id());
//                }
//            }else{
//                trades.setStoreUserId(Integer.parseInt(homeDeliveryAndStoresInvite.getClerkInvitationCode()));
//            }
//            //门店自提
//            if("2".equals(homeDeliveryAndStoresInvite.getOrderType())){
//                trades.setRecevierMobile(homeDeliveryAndStoresInvite.getMobile());
//                trades.setRecevierName(StringUtils.isEmpty(homeDeliveryAndStoresInvite.getReceiverName())? userName : homeDeliveryAndStoresInvite.getReceiverName());
//                trades.setReceiverAddress("");
//            }else{//送货上门
//                trades.setRecevierMobile(homeDeliveryAndStoresInvite.getReceiverMobile());
//                trades.setRecevierName(homeDeliveryAndStoresInvite.getReceiverName());
//                trades.setReceiverAddress(homeDeliveryAndStoresInvite.getReceiverAddress());
//            }
//            trades.setReceiverCity(city);
//            //trades.setReceiverCity(homeDeliveryAndStoresInvite.getReceiverCityCode());
//
//            trades.setReceiverZip(homeDeliveryAndStoresInvite.getReceiverZip());
//            trades.setSellerPayNo(ybAccount.getAccount());
//            trades.setSellerPhone(merchant.getServicePhone());
//            trades.setSellerMobile(merchant.getServiceMobile());
//            trades.setSellerName(merchant.getMerchantName());
//            trades.setTradesStatus(CommonConstant.WAIT_PAYMENT_BUYERS);//所有订单的初始状态都是：等侍买家付款 未支付
//            trades.setTotalFee(distributeResponse.getOrderOriginalPrice());//订单原始金额
//            trades.setPostFee(distributeResponse.getOrderFreight());//直购订单和门店自提订单的运费都为0
//
//
//            if (CommonConstant.ORDER_TYPE_HOME_DELIVERY.equals(homeDeliveryAndStoresInvite.getOrderType())) {
//                trades.setPostStyle(CommonConstant.POST_STYLE_DOOR);//送货上门订单
//            } else if (CommonConstant.ORDER_TYPE_STORE_TAKE.equals(homeDeliveryAndStoresInvite.getOrderType())) {
//                trades.setPostStyle(CommonConstant.POST_STYLE_EXTRACT);//门店自提订单
//            }else if(CommonConstant.ORDER_TYPE_STORE_TAKE.equals(homeDeliveryAndStoresInvite.getOrderType())){
//
//            }
//            //设置订单分配的门店
//            //送货上门订单
//            if (CommonConstant.ORDER_TYPE_HOME_DELIVERY.equals(homeDeliveryAndStoresInvite.getOrderType())) {
//                if(!StringUtil.isEmpty(distributeResponse.getStore())){
//                    trades.setCreateOrderAssignedStores(distributeResponse.getStore().getId());
//                    trades.setAssignedStores(distributeResponse.getStore().getId());//指定送货门店
//                }else {
//                    trades.setAssignedStores(0);//默认总部
//                }
//            } else if (CommonConstant.ORDER_TYPE_STORE_TAKE.equals(homeDeliveryAndStoresInvite.getOrderType())) {
//                //门店自提订单
//                trades.setSelfTakenStore(homeDeliveryAndStoresInvite.getSelfTakenStore());
//                trades.setAssignedStores(homeDeliveryAndStoresInvite.getSelfTakenStore());//指定送货门店
//            }
//            trades.setTradesSource(homeDeliveryAndStoresInvite.getTradesSource());//订单来源平台
//            trades.setTradesInvoice(homeDeliveryAndStoresInvite.getTradesInvoice());//发票信息
//            //发票抬头
//            if (homeDeliveryAndStoresInvite.getTradesInvoice() != null && homeDeliveryAndStoresInvite.getTradesInvoice() == 1) {
//                trades.setInvoiceTitle(homeDeliveryAndStoresInvite.getInvoiceTitle());
//            }
//            trades.setTradesStore(Integer.parseInt(homeDeliveryAndStoresInvite.getTradesStore()));
//            trades.setStockupStatus(CommonConstant.STOCKUP_WAIT_READY);//送货上门订单和门店自提订单初始创建时默认为：未备货
//            trades.setIsPayment(CommonConstant.IS_PAYMENT_ZERO);//默认为0：未付款
//            trades.setAccountCheckingStatus(0);//默认为0：待处理
//            boolean prescriptionFlag = false;
//            //判断是否为处方药订单
//            for (GoodsInfo goodsInfo : goodsInfos) {
//                if (goodsInfo.getDrugCategory() == 130) {
//                    prescriptionFlag = true;
//                    break;
//                }
//            }
//            if (prescriptionFlag) {
//                trades.setPrescriptionOrders(CommonConstant.TRADES_PRESCRIPTION_TRUE);
//            } else {
//                trades.setPrescriptionOrders(CommonConstant.TRADES_PRESCRIPTION_FALSE);
//            }
//            //订单的实际支付金额
//            trades.setRealPay(distributeResponse.getOrderRealPrice());
//            //交易佣金
//            trades.setTradesSplit(getTradesCommission(homeDeliveryAndStoresInvite.getSiteId(), distributeResponse.getOrderRealPrice(), CommonConstant.TRADESCOMMISSION_ORDER_TYPE_DIRECT_PURCHASE));
//            //支付平台收取的支付佣金,支付完成之后记录
//            trades.setPlatSplit(0);
//            trades.setBuyerMessage(homeDeliveryAndStoresInvite.getBuyerMessage());
//            logger.info("创建订单信息：" + trades.toString());
//            if(1 == homeDeliveryAndStoresInvite.getFlag()){
//                //该订单为预约医生服务类订单
//                trades.setServceTpye(100);
//            }
//
//            //创建送货上门订单和门店自提订单
//            int flag = ordersMapper.addDirectOrderTrades(trades);
//
//            //订单创建成功
//            if (flag == 1) {
//                List<OrderGoods> orderGoods = homeDeliveryAndStoresInvite.getOrderGoods();
//                //记录子订单信息
//                addOrders(orderGoods, tradesId, homeDeliveryAndStoresInvite.getSiteId());
//                //计算积分抵扣,用户下单成功之后，扣除用户该次使用的积分
//                if (homeDeliveryAndStoresInvite.getIntegralUse() > 0) {
//                    /*int integral = bShopMember.getIntegrate().intValue() - homeDeliveryAndStoresInvite.getIntegralUse();
//                    BMember bMemberIntegrate = new BMember();
//                    bMemberIntegrate.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
//                    bMemberIntegrate.setBuyerId(userId);
//                    bMemberIntegrate.setIntegrate(BigInteger.valueOf(integral));
//                    ordersMapper.updateBShopMember(bMemberIntegrate);*/
//                    //扣积分，调用积分接口
//                    Map<String,Object> params = new HashMap<String,Object>();
//                    params.put("siteId",homeDeliveryAndStoresInvite.getSiteId());
//                    params.put("orderAmount",distributeResponse.getOrderOriginalPrice());
//                    params.put("buyerId",bShopMember.getBuyerId());
//                    params.put("useNum",homeDeliveryAndStoresInvite.getIntegralUse());
//                    params.put("integralDesc","积分抵现金："+tradesId);
//                    //扣积分
//                    JSONObject json= integralService.integralDiff(params);
//                    logger.info("--------扣积分:"+json);
//                }
//
//                //创建订单交易扩展信息
//                TradesExt tradesExt = new TradesExt();
//                tradesExt.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
//                tradesExt.setTradesId(tradesId);
//                tradesExt.setIntegralUsed(homeDeliveryAndStoresInvite.getIntegralUse());
//                tradesExt.setIntegralPreAward(0);//欲送积分
//                tradesExt.setIntegralFinalAward(0);//实送积分
//                //判断用户是否首单
//                if (checkUserFirstOrder(homeDeliveryAndStoresInvite.getSiteId(), userId)) {
//                    tradesExt.setIsFirstOrder(CommonConstant.USER_TRADES_IS_FIRST_ORDER_YES);
//                } else {
//                    tradesExt.setIsFirstOrder(CommonConstant.USER_TRADES_IS_FIRST_ORDER_NO);
//                }
//                tradesExt.setIntegralPrice(distributeResponse.getIntegralDeductionPrice());//积分抵扣的订单金额(单位：分)
//                tradesExt.setReduceReductionAmount(0);//满减减少的金额
//                tradesExt.setBjDiscountAmount(0);//第二件半价活动优惠金额
//                tradesExt.setUserCouponId(homeDeliveryAndStoresInvite.getUserCouponId());//使用的优惠券ID
//                tradesExt.setUserCouponAmount(distributeResponse.getCouponDeductionPrice());//优惠券抵扣金额
//                ordersMapper.addTradesExt(tradesExt);
//                //修改会员信息
//                //bShopMember.setOrderFee(bShopMember.getOrderFee() + distributeResponse.getOrderRealPrice());
//                //bShopMember.setOrderNum(bShopMember.getOrderNum() + 1);
//                //ordersMapper.updateOrderMember(bShopMember);
//            } else {
//                response.setCode(CommonConstant.TRADES_RESP_CODE_FAILED);
//                response.setMessage("订单创建失败");
//                return response;
//            }
//
//            ServceOrder servceOrder = new ServceOrder();
//            servceOrder.setSchedulePersonId(homeDeliveryAndStoresInvite.getSchedulePersonId());
//            servceOrder.setDiagnoseStatus(homeDeliveryAndStoresInvite.getDiagnoseStatus());
//            servceOrder.setDiseaseInfo(homeDeliveryAndStoresInvite.getDiseaseInfo());
//            servceOrder.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
//            servceOrder.setUseDetailId(homeDeliveryAndStoresInvite.getUseDetailId());
//            servceOrder.setServeStatus(0);
//            servceOrder.setCreateTime(new Date());
//            servceOrder.setUpdateTime(servceOrder.getCreateTime());
//            servceOrder.setGoodsId(homeDeliveryAndStoresInvite.getOrderGoods().get(0).getGoodsId());
//            servceOrder.setStoreId(Integer.valueOf(homeDeliveryAndStoresInvite.getTradesStore()));
//            servceOrder.setTradesId(tradesId+"");
//            servceOrder.setBookingNo(homeDeliveryAndStoresInvite.getSiteId()+""+System.currentTimeMillis()/1000);
//            servceOrder.setAmount(homeDeliveryAndStoresInvite.getAccountSource());
//            servceOrder.setUseCount(homeDeliveryAndStoresInvite.getAccountSource() - homeDeliveryAndStoresInvite.getUseCount()+1);
//            //向预约表中插入一条数据
//            Boolean foo = servceOrderService.insert(servceOrder);
//            //修改b_servce_use_detail表中的预定人数
//            ServceUseDetail servceUseDetail = new ServceUseDetail();
//            servceUseDetail.setUseCount(homeDeliveryAndStoresInvite.getUseCount()-1);
//            servceUseDetail.setMineClassesId(homeDeliveryAndStoresInvite.getMineClassesId());
//            servceUseDetail.setTemplateNo(homeDeliveryAndStoresInvite.getTemplateNo());
//            servceUseDetailMapper.updateByTemplateNoAndMineClassesId(servceUseDetail);
//
//            if(foo){
//                response.setCode(CommonConstant.TRADES_RESP_CODE_SUCCESS);
//                response.setMessage("订单创建成功");
//                response.setTradesId(String.valueOf(tradesId));
//                response.setSiteId(String.valueOf(homeDeliveryAndStoresInvite.getSiteId()));
//            }
//
//            //修改优惠券状态
//            if (!StringUtil.isEmpty(beforeCreateOrderReq.getCouponId()) && beforeCreateOrderReq.getCouponId() != 0) {
//                couponDetailMapper.updateStatusById(homeDeliveryAndStoresInvite.getSiteId(), beforeCreateOrderReq.getCouponId(), String.valueOf(tradesId));
//            }
//
//            if(1 != homeDeliveryAndStoresInvite.getFlag()){
//                //分单电话提醒门店（门店自提）
//                if (!StringUtils.isEmpty(distributeResponse.getStore()) && !StringUtils.isEmpty(distributeResponse.getStore().getTel()) && CommonConstant.ORDER_TYPE_HOME_DELIVERY.equals(homeDeliveryAndStoresInvite.getOrderType())) {
//                /*new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }).start();*/
//                    Map<String, Object> str = _7moorService.webcall(distributeResponse.getStore().getTel().replace("-", ""));
//                    String ordertype = trades.getPostStyle() == CommonConstant.POST_STYLE_DOOR ? "送货上门" : (CommonConstant.POST_STYLE_EXTRACT == trades.getPostStyle() ? "自提" : "");
//                    String header = "温馨";
//                    String word = "【51健康】" + header + "提示，您有新的" + ordertype + "订单，订单号：" + trades.getTradesId() + "，请尽快处理。";
//                    String[] phones = distributeResponse.getStore().getRemindMobile().split(",");
//                    for (int i = 0; i < phones.length; ++i) {
//                        String result = ztSmsService.SendMessage(word, phones[i]);
//                        if (result.equals("-1")) {
//                            ypSmsService.SendOrderTemplate(1402543, phones[i], header, ordertype, trades.getTradesId().toString());
//                        }
//                    }
//                    logger.info("分单电话提醒门店反馈：" + str);
//                }
//            }
//
//            //零元订单直接支付成功
//            if( trades.getRealPay().equals(0)){
//                try {
//                    boolean pay= payService.noMoneyPay(trades);
//                    logger.info("零元订单支付："+pay);
//                } catch (BusinessLogicException e) {
//                    e.printStackTrace();
//                }
//                response.setCode(CommonConstant.TRADES_RESP_CODE_SUCCESS_NOPAY);
//            }
//            return response;
//        } else {
//            response.setCode(CommonConstant.TRADES_RESP_CODE_FAILED);
//            response.setMessage("订单创建失败");
//            return response;
//        }
//    }


    /**
     * 创建直购订单(药房直购)
     *
     * @param storeDirect
     * @return
     */
    @Transactional
    public OrderResponse createStoreDirectOrders(StoreDirect storeDirect) {
        OrderResponse response = new OrderResponse();
        //校验必选参数是否为空
        String checkParams = setStoreDirectReqParams(storeDirect);
        if (StringUtil.isNotEmpty(checkParams)) {
            response.setCode(CommonConstant.TRADES_RESP_CODE_MISSINF_PARAMS);
            response.setMessage(String.format("必选参数：[%s] 为空", checkParams));
            return response;
        }
        //校验用户电话号码格式
        if (!StringUtil.isMobileNO(storeDirect.getMobile())) {//checkMobile(storeDirect.getMobile())
            response.setCode(CommonConstant.TRADES_RESP_CODE_PHONE_FAILED);
            response.setMessage("用户手机号码格式错误");
            return response;
        }
        String userName = "";
        String userAddr = "";
        //用户唯一ID
        int userId = 0;
        int newuserId = 0;
        //查询用户是否已注册
        BMember bmember = ordersMapper.getMemberById(storeDirect.getSiteId(), storeDirect.getMobile());
        //用户已注册
        if (bmember != null) {
            BMemberInfo bMemberInfo = ordersMapper.getMemberInfoById(storeDirect.getSiteId(), bmember.getBuyerId());
            userId = bmember.getBuyerId();
            userName = bmember.getName();
            newuserId = bmember.getMemberId();
            if (bMemberInfo != null) {
                userAddr = bMemberInfo.getAddress();
            }
        } else {//用户没注册过
            //查询匿名用户是否存在
            YBMember ybMember = ordersMapper.getYBMemberByMobile(storeDirect.getMobile());
            int memberId = 0;
            if (StringUtil.isEmpty(ybMember)) {
                //创建匿名用户
                YBMember ybzfUser = new YBMember();
                ybzfUser.setMobile(storeDirect.getMobile());
                ybzfUser.setPasswd(SHA1(generatePWD()));//默认密码
                ybzfUser.setIsActivated(0);
                ybzfUser.setReginSource(9999);//注册来源
                ybzfUser.setBUsersarr(String.valueOf(storeDirect.getSiteId()));
                ybzfUser.setRegisterStores(Integer.parseInt(storeDirect.getSotreId()));
                ybzfUser.setName("");
                ordersMapper.addYBMember(ybzfUser);
                memberId = ordersMapper.getYBMemberByMobile(storeDirect.getMobile()).getMemberId();
            } else {
                //更新注册商户
                String userSiteArr = ybMember.getBUsersarr();
                if (StringUtil.isNotEmpty(userSiteArr)) {
                    ybMember.setBUsersarr(userSiteArr + "," + storeDirect.getSiteId());
                } else {
                    ybMember.setBUsersarr(String.valueOf(storeDirect.getSiteId()));
                }
                ordersMapper.updateYBMember(ybMember);
                memberId = ybMember.getMemberId();
                userName = ybMember.getName();
            }
            //创建商家会员信息
            if (memberId > 0) {
                //根据匿名用户，查询商家会员
                BMember bShopMember = ordersMapper.getMemberByBuyerId(storeDirect.getSiteId(), memberId);
                if (bShopMember != null) {
                    userId = memberId;
                }
            }
        }
        if (userId > 0) {
            //查询购物车里面的商品信息
            List<GoodsInfo> goodsInfos = distributeOrderService.getGoodsInfos(storeDirect.getSiteId(), storeDirect.getOrderGoods(), storeDirect.getSotreId());
            //检查商品是否可以购买
            if (!checkGoods(goodsInfos, storeDirect.getTradesSource())) {
                response.setCode(CommonConstant.TRADES_RESP_CODE_GOODS_NOT_NORMAL);
                response.setMessage("商品不可以购买");
                return response;
            }

            //订单交易表
            Trades trades = new Trades();
            //查询商家信息
            Merchant merchant = distributeOrderMapper.getMerchant(storeDirect.getSiteId());
            //查询商家账号信息
            YBAccount ybAccount = ordersMapper.getYBAccountById(storeDirect.getSiteId(), 2);
            //查询用户信息
            BMember bShopMember = ordersMapper.getMemberByBuyerId(storeDirect.getSiteId(), userId);

            //获取自动分单信息，价格信息及运费信息
            BeforeCreateOrderReq beforeCreateOrderReq = new BeforeCreateOrderReq();
            beforeCreateOrderReq.setSiteId(storeDirect.getSiteId());
            beforeCreateOrderReq.setUserId(newuserId);
            beforeCreateOrderReq.setBuyerId(userId);
            beforeCreateOrderReq.setMobile(storeDirect.getMobile());
            beforeCreateOrderReq.setStoresId(Integer.parseInt(storeDirect.getSotreId()));
            beforeCreateOrderReq.setOrderGoods(storeDirect.getOrderGoods());
            if (StringUtil.isNotEmpty(storeDirect.getUserCouponId()) && !"0".equals(storeDirect.getUserCouponId())) {
                beforeCreateOrderReq.setCouponId(Integer.parseInt(storeDirect.getUserCouponId()));
            }
            if (storeDirect.getIntegral() != 0) {
                beforeCreateOrderReq.setIntegralUse(storeDirect.getIntegral());
            }
            beforeCreateOrderReq.setOrderType("3");
            beforeCreateOrderReq.setTradesSource(storeDirect.getTradesSource());
            beforeCreateOrderReq.setGiftGoods(storeDirect.getGiftGoods());
            beforeCreateOrderReq.setConcessionResult(storeDirect.getConcessionResult());
            //beforeCreateOrderReq.setTradesSource(140);
            //调用分单(计算价格，不分单)
            DistributeResponse distributeResponse = distributeOrderService.beforeOrder(beforeCreateOrderReq, "1");

            long tradesId = getTradesId(storeDirect.getSiteId());
            trades.setSiteId(storeDirect.getSiteId());
            trades.setTradesId(tradesId);
            trades.setSellerId(merchant.getMerchantId());
            trades.setBuyerId(bShopMember.getBuyerId());
            trades.setSellerName(merchant.getMerchantName());
            trades.setBuyerNick(bShopMember.getName());
            trades.setReceiverPhone(storeDirect.getReceiverPhone());
            trades.setRecevierMobile(storeDirect.getMobile());
            if (StringUtil.isEmpty(storeDirect.getStoreUserId())) {
                trades.setStoreUserId(0);
            } else {
                trades.setStoreUserId(Integer.parseInt(storeDirect.getStoreUserId()));
            }
            trades.setRecevierName(StringUtils.isEmpty(storeDirect.getReceiverName()) ? (StringUtils.isEmpty(userName) ? "--" : userName) : storeDirect.getReceiverName());
            if(StringUtil.isEmpty(userAddr)){
                userAddr="--";
            }
            trades.setReceiverAddress(storeDirect.getReceiverAddress() == null ? userAddr : storeDirect.getReceiverAddress());
            trades.setReceiverZip(storeDirect.getReceiverZip());
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
            trades.setPostFee(0);//直购订单和门店自提订单的运费都为0

            trades.setPostStyle(CommonConstant.POST_STYLE_DIRECT_PURCHASE);//直购订单
            if (!StringUtil.isEmpty(storeDirect.getTradesSource())) {
                trades.setTradesSource(storeDirect.getTradesSource());//app下单
            } else {
                trades.setTradesSource(CommonConstant.TRADES_SOURCE_DIRECT);//店员帮用户下单
            }
            trades.setTradesInvoice(0);//直购订单不开发票
            trades.setAssignedStores(Integer.parseInt(storeDirect.getSotreId()));
            trades.setTradesStore(Integer.parseInt(storeDirect.getSotreId()));
            trades.setStockupStatus(CommonConstant.STOCKUP_REDAY);//直购订单默认已备货
            trades.setIsPayment(CommonConstant.IS_PAYMENT_ZERO);//默认为0：未付款
            trades.setAccountCheckingStatus(0);//默认为0：待处理
            //trades.setPayStyle(CommonConstant.TRADES_PAY_TYPE_CASH);//默认为现金支付
            trades.setPostageDiscount(distributeResponse.getPostageDiscount());//运费优惠
            boolean prescriptionFlag = false;
            //判断是否为处方药订单
            for (GoodsInfo goodsInfo : goodsInfos) {
                if (goodsInfo.getDrugCategory() == 130) {
                    prescriptionFlag = true;
                    break;
                }
            }
            if (prescriptionFlag) {
                trades.setPrescriptionOrders(CommonConstant.TRADES_PRESCRIPTION_TRUE);
            } else {
                trades.setPrescriptionOrders(CommonConstant.TRADES_PRESCRIPTION_FALSE);
            }
            //订单实际支付支付金额
            trades.setRealPay(distributeResponse.getOrderRealPrice());
            //交易佣金
            trades.setTradesSplit(getTradesCommission(storeDirect.getSiteId(), distributeResponse.getOrderRealPrice(), CommonConstant.TRADESCOMMISSION_ORDER_TYPE_DIRECT_PURCHASE));
            //支付平台收取的支付佣金,支付完成之后记录
            trades.setPlatSplit(0);
            trades.setSettlementType(0);
            logger.info("创建订单信息：" + trades.toString());

            //--------------------------------设置分销商id------------------------------------------------
            if (null != storeDirect.getDistributorId()) {//存在分销商推荐人
                trades.setDistributorId(this.distributorMapper.selectByUid(storeDirect.getDistributorId(), bmember.getSiteId()).getId());
            } else if (0 != distributeResponse.getDistributePrice()) {//不存在分销商推荐人，存在分销商品
                Distributor distributor = this.distributorMapper.selectByUid(trades.getBuyerId(), bmember.getSiteId());
                if (null != distributor) {//存在分销商品 买家自己已经是分销商
                    trades.setDistributorId(distributor.getId());
                } else {//存在分销商品 买家自己还不是分销商
                    trades.setDistributorId(Integer.MAX_VALUE);
                }
            } else {
                trades.setDistributorId(0);
            }
            //--------------------------------设置分销商id------------------------------------------------
            //查看该商家是否是服务商商户
            String fw=balanceService.boolIsProvider(storeDirect.getSiteId());
            if("YES".equals(fw)){
                trades.setIsServiceOrder(1);
                response.setIsServiceOrder(1);
            }
            //创建直购订单
            int flag = ordersMapper.addDirectOrderTrades(trades);
            //订单创建成功
            if (flag == 1) {
                List<OrderGoods> orderGoods = storeDirect.getOrderGoods();
                //------------------------为分销商品和普通分别设置实际金额---------------yeah--------------------------------------------------------
                this.distributeOrderService.setGoodsPrice(orderGoods, storeDirect.getSiteId(), storeDirect.getMobile());
                //------------------------为分销商品和普通分别设置实际金额---------------yeah--------------------------------------------------------
                //记录子订单信息
                addOrders(orderGoods, tradesId, storeDirect.getSiteId(), storeDirect.getSotreId(), distributeResponse.getGoodsInfoInfos(),trades.getServceType());
                //计算积分抵扣,用户下单成功之后，扣除用户该次使用的积分
                if (storeDirect.getIntegral() > 0) {
                    /*int integral = bShopMember.getIntegrate().intValue() - storeDirect.getIntegral();
                    BMember bMemberIntegrate = new BMember();
                    bMemberIntegrate.setSiteId(storeDirect.getSiteId());
                    bMemberIntegrate.setBuyerId(userId);
                    bMemberIntegrate.setIntegrate(BigInteger.valueOf(integral));
                    ordersMapper.updateBShopMember(bMemberIntegrate);*/
                    //扣积分，调用积分接口
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("siteId", storeDirect.getSiteId());
                    params.put("orderAmount", distributeResponse.getOrderOriginalPrice());
                    params.put("buyerId", bShopMember.getBuyerId());
                    params.put("useNum", storeDirect.getIntegral());
                    params.put("integralDesc", "积分抵现金：" + tradesId);
                    //扣积分
                    JSONObject json = integralService.integralDiff(params);
                    logger.info("--------扣积分:" + json);
                }
                //创建订单交易扩展信息
                TradesExt tradesExt = new TradesExt();
                tradesExt.setSiteId(storeDirect.getSiteId());
                tradesExt.setTradesId(tradesId);
                tradesExt.setIntegralUsed(storeDirect.getIntegral());
                tradesExt.setIntegralPreAward(0);//欲送积分
                tradesExt.setIntegralFinalAward(0);//实送积分
                //判断用户是否首单
                if (checkUserFirstOrder(storeDirect.getSiteId(), userId)) {
                    tradesExt.setIsFirstOrder(CommonConstant.USER_TRADES_IS_FIRST_ORDER_YES);
                } else {
                    tradesExt.setIsFirstOrder(CommonConstant.USER_TRADES_IS_FIRST_ORDER_NO);
                }
                tradesExt.setIntegralPrice(distributeResponse.getIntegralDeductionPrice());//积分抵扣的订单金额(单位：分)
                tradesExt.setReduceReductionAmount(0);//满减减少的金额
                tradesExt.setBjDiscountAmount(0);//第二件半价活动优惠金额
                if (StringUtil.isNotEmpty(storeDirect.getUserCouponId())) {
                    tradesExt.setUserCouponId(Integer.parseInt(storeDirect.getUserCouponId()));//使用的优惠券ID
                }
                tradesExt.setUserCouponAmount(distributeResponse.getCouponDeductionPrice());//优惠券抵扣金额
                ordersMapper.addTradesExt(tradesExt);

                //修改会员信息
                //bShopMember.setOrderFee(bShopMember.getOrderFee() + distributeResponse.getOrderRealPrice());
                //bShopMember.setOrderNum(bShopMember.getOrderNum() + 1);
                //ordersMapper.updateOrderMember(bShopMember);

                response.setCode(CommonConstant.TRADES_RESP_CODE_SUCCESS);
                response.setMessage("订单创建成功");
                response.setTradesId(String.valueOf(tradesId));
                response.setSiteId(String.valueOf(storeDirect.getSiteId()));

                //修改优惠券状态
                if (!StringUtil.isEmpty(beforeCreateOrderReq.getCouponId()) && beforeCreateOrderReq.getCouponId() != 0) {
                    couponDetailMapper.updateStatusById(storeDirect.getSiteId(), beforeCreateOrderReq.getCouponId(), String.valueOf(tradesId));
                }

                //零元订单直接支付成功
                if (trades.getRealPay().equals(0)) {
                    try {
                        boolean pay = payService.noMoneyPay(trades);
                        if (pay){
                            //------直购订单增加生成电子小票记录------start
                            smallTicketService.callBack(bShopMember, tradesId);
                            //------直购订单增加生成电子小票记录------end
                        }
                        logger.info("零元订单支付：" + pay);
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                    }
                    response.setCode(CommonConstant.TRADES_RESP_CODE_SUCCESS_NOPAY);
                }
                if (StringUtil.isNotBlank(storeDirect.getPromActivityIdArr())
                    || StringUtil.isNotBlank(storeDirect.getUserCouponId())
                    || !"0".equals(storeDirect.getUserCouponId())) {

                    try {
                        promotionsOrderService.saveConcessionResultToTable(storeDirect.getSiteId(),
                            tradesId,
                            storeDirect,
                            distributeResponse);

                    } catch (Exception e) {
                        logger.error("异常发生,{}", e);
                    }
                }

                return response;
            } else {
                response.setCode(CommonConstant.TRADES_RESP_CODE_FAILED);
                response.setMessage("订单创建失败");
                return response;
            }
        } else {
            response.setCode(CommonConstant.TRADES_RESP_CODE_FAILED);
            response.setMessage("订单创建失败");
            return response;
        }
    }

    /**
     * 门店直购订单线下确认收货，保存收款信息
     *
     * @param tradesId                订单ID
     * @param cashPaymentPay          现金收款金额，单位：分
     * @param medicalInsuranceCardPay 医保卡收款金额，单位：分
     * @param lineBreaksPay           线下优惠金额，单位：分
     * @param cashReceiptNote         现金收款备注
     * @return 返回更新的行数
     */
    public int updateTradesExt(String tradesId, Integer cashPaymentPay, Integer medicalInsuranceCardPay, Integer lineBreaksPay, String cashReceiptNote) {
        return ordersMapper.updateTradesExt(tradesId, cashPaymentPay, medicalInsuranceCardPay, lineBreaksPay, cashReceiptNote);
    }

    /**
     * 创建订单详情信息
     * 这里不修改价格 分销价格、ERP价格等全部在外面处理
     *
     * @param orderGoods
     * @param tradesId
     * @param siteId
     */
    public void addOrders(List<OrderGoods> orderGoods, long tradesId, Integer siteId, String storeId, List<GoodsInfo> goodsInfoInfos,Integer servceType) {
        Orders orders = null;
        //计算商品原始总价格
//        MerchantExtTreat merchantExt = merchantExtTreatMapper.selectByMerchantId(siteId);
        List<Integer> goodsIds = orderGoods.stream().map(OrderGoods::getGoodsId).collect(toList());
        List<GoodsInfo> goodsInfos = distributeOrderMapper.selectGoodsByGoodsIds(siteId, goodsIds);
        HashMap<Integer, GoodsInfo> goodsInfoMap = goodsInfos.stream()
            .collect(HashMap::new, (m, e) -> m.put(e.getGoodsId(), e), (m1, m2) -> m1.putAll(m2));
        HashMap<Integer, GoodsInfo> goodsInfoInfosMap = goodsInfoInfos.stream().collect(HashMap::new, (m, e) -> m.put(e.getGoodsId(), e), (m1, m2) -> m1.putAll(m2));
//        logger.info("goodsInfoInfos============" + Arrays.toString(goodsInfoInfos.toArray()));

        //记录子订单信息
        for (int i = 0; i < orderGoods.size(); i++) {
            GoodsInfo goodsInfo = goodsInfoMap.get(orderGoods.get(i).getGoodsId());
            if (goodsInfo == null) {
                continue;
            } else {

//                if (!StringUtil.isEmpty(storeId)) {
//                    YbStoresGoodsPrice ybStoresGoodsPrice = this.goodsMapper.queryGoodStorePrice(orderGoods.get(i).getGoodsId(), siteId, Integer.parseInt(storeId));
//                    if (null != ybStoresGoodsPrice && 0 != ybStoresGoodsPrice.getDiscountPrice()) {
//                        goodsInfo.setDiscountPrice(ybStoresGoodsPrice.getDiscountPrice());
//                    }
//                } else {
//                    if (merchantExt.getHas_erp_price().equals(1)) {
//                        goodsInfo.setDiscountPrice(goodsInfo.getErpPrice());
//                        //goodsInfo.setDiscountPrice(0);
//                    }
//                }

                orders = new Orders();
                orders.setSiteId(siteId);
                orders.setOrderId(getTradesId(siteId));
                orders.setGoodsId(goodsInfo.getGoodsId());
                orders.setGoodsTitle(goodsInfo.getGoodsTitle());
                orders.setGoodsPrice(orderGoods.get(i).getGoodsPrice()==0?goodsInfo.getShopPrice():orderGoods.get(i).getGoodsPrice());

                GoodsInfo goodsFinalInfo = goodsInfoInfosMap.get(goodsInfo.getGoodsId());//计算订单价格时的商品信息
                if (goodsFinalInfo != null)
                    orders.setGoodsFinalPrice(goodsFinalInfo.getDiscountPrice() == 0 ? goodsFinalInfo.getShopPrice() : goodsFinalInfo.getDiscountPrice());

                if(!StringUtil.isEmpty(servceType)&&50==servceType){
                    orders.setGoodsPrice(orders.getGoodsFinalPrice());
                }
                orders.setGoodsNum(orderGoods.get(i).getGoodsNum());
                orders.setGoodsGifts(0);//是否赠品，需要根据优惠券来判断
                orders.setApprovalNumber(goodsInfo.getApprovalNumber());
                orders.setSpecifCation(goodsInfo.getSpecifCation());
                orders.setGoodsCategory(goodsInfo.getDrugCategory());
                orders.setTradesId(tradesId);
                orders.setGoodsImgurl("");
                orders.setOrdersStatus(CommonConstant.WAIT_PAYMENT_BUYERS);
                orders.setGoodsCode(goodsInfo.getGoodsCode());
                orders.setYbGoodsId(goodsInfo.getYbGoodsId());
                orders.setGoodsBatchNo("");
                ordersMapper.addOrders(orders);
            }
        }
    }

    /**
     *
     */
    public void addGroupPurchase(List<OrderGoods> orderGoods, long tradesId, HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite) {
        GroupPurchase paramGrpupPurchase = homeDeliveryAndStoresInvite.getGroupPurchase();

        GroupPurchase addGroupPurchase = new GroupPurchase();
        addGroupPurchase.setSiteId(homeDeliveryAndStoresInvite.getSiteId());
        addGroupPurchase.setProActivityId(paramGrpupPurchase.getProActivityId());
        addGroupPurchase.setGoodsId(paramGrpupPurchase.getGoodsId());
        addGroupPurchase.setTradesId(tradesId + "");
        addGroupPurchase.setStatus(0);
        addGroupPurchase.setMemberId(homeDeliveryAndStoresInvite.getUserId());
        addGroupPurchase.setCreateTime(LocalDateTime.now());
        addGroupPurchase.setUpdateTime(LocalDateTime.now());
        addGroupPurchase.setGroupbeginTime(LocalDateTime.now());
        if (null != paramGrpupPurchase.getId())
            addGroupPurchase.setParentId(paramGrpupPurchase.getId());

        groupPurChaseMapper.create(addGroupPurchase);

    }


    /**
     * 创建积分兑换订单详情信息
     *
     * @param orderGoods
     * @param tradesId
     * @param siteId
     */
    public void addIntegralOrders(List<OrderGoods> orderGoods, long tradesId, Integer siteId) {
        Orders orders = null;
        //记录子订单信息
        for (int i = 0; i < orderGoods.size(); i++) {
            GoodsInfo goodsInfo = distributeOrderMapper.getGoodsInfo(siteId, orderGoods.get(i).getGoodsId());
            if (goodsInfo == null) {
                continue;
            } else {
                orders = new Orders();
                orders.setSiteId(siteId);
                orders.setOrderId(getTradesId(siteId));
                orders.setGoodsId(goodsInfo.getGoodsId());
                orders.setGoodsTitle(goodsInfo.getGoodsTitle());
                orders.setGoodsPrice(goodsInfo.getDiscountPrice() == 0 ? goodsInfo.getShopPrice() : goodsInfo.getDiscountPrice());
                orders.setGoodsNum(orderGoods.get(i).getGoodsNum());
                orders.setGoodsGifts(0);//是否赠品，需要根据优惠券来判断
                orders.setApprovalNumber(goodsInfo.getApprovalNumber());
                orders.setSpecifCation(goodsInfo.getSpecifCation());
                orders.setGoodsCategory(goodsInfo.getDrugCategory());
                orders.setTradesId(tradesId);
                orders.setGoodsImgurl("");
                orders.setOrdersStatus(CommonConstant.WAIT_SELLER_SHIPPED);
                orders.setGoodsCode(goodsInfo.getGoodsCode());
                orders.setYbGoodsId(goodsInfo.getYbGoodsId());
                orders.setGoodsBatchNo("");
                ordersMapper.addOrders(orders);
            }
        }
    }


    /**
     * 查询用户是否首单
     *
     * @param siteId
     * @param buyerId
     * @return true:是首单；false：不是首单
     */
    public boolean checkUserFirstOrder(Integer siteId, Integer buyerId) {
        int orderCount = tradesMapper.queryUserCouponFirstOrder(siteId, buyerId);
        //如果用户有已付款的订单，则不是首单
        if (orderCount >= 1) {
            return false;
        }
        return true;
    }

    /**
     * 生成订单交易号，siteId + 时间
     *
     * @param siteId
     * @return
     */
    public long getTradesId(int siteId) {
        String tradesId = String.valueOf(siteId) + String.valueOf(System.currentTimeMillis());
        return Long.parseLong(tradesId);
    }

    /**
     * 获取八位随机字符串，包含数字和字符串
     *
     * @return
     */
    public String generatePWD() {
        String[] uuids = UUID.randomUUID().toString().split("-");
        return uuids[0];
    }

    /**
     * SHA1 安全加密算法
     *
     * @param str
     * @return 长度为40的加密后字符串
     */
    public static String SHA1(String str) {
        if (StringUtil.isEmpty(str)) return null;
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes("UTF-8"));
            byte[] md = digest.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte bt = md[i];
                buf[k++] = hexDigits[bt >>> 4 & 0xf];
                buf[k++] = hexDigits[bt & 0xf];
            }
            return new String(buf);
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA1 加密算法错误：", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("SHA1 加密算法错误：", e);
        }
        return null;
    }

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
            if (!StringUtil.isEmpty(tradesSource) && tradesSource == 130) {
                if (goodsInfoList.get(i).getAppGoodsStatus() != 1) {
                    return false;
                }
            }else {
                if (goodsInfoList.get(i).getGoodsStatus() != 1) {
                    return false;
                }
            }
            //app下单不判断
            if (!StringUtil.isEmpty(tradesSource) && tradesSource != 130) {
                if (goodsInfoList.get(i).getWxPurchaseWay() != 110) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 计算每笔订单的交易佣金，下单时就计算好，每笔订单的最低交易佣金为0.01元(20170727产品定义去掉最低交易佣金为0.01元)
     *
     * @param siteId         商家ID
     * @param orderRealPrice 订单的实际支付金额,单位：分
     * @param orderType      订单类型, 0:直购(送货上门、门店自提、药房直购)；1：分销(三级分销订单)
     * @return 交易佣金，单位：分
     */
    public int getTradesCommission(int siteId, int orderRealPrice, String orderType) {
        //查询商家的交易佣金比例
        AccountCommissionRate acr = accountCommissionRateMapper.getCommissionRatById(siteId);
        //交易佣金比例
        double commissionRate = 0.00;
        if (acr != null) {
            //直购订单
            if (CommonConstant.TRADESCOMMISSION_ORDER_TYPE_DIRECT_PURCHASE.equals(orderType)) {
                commissionRate = acr.getDirect_purchase_rate();
            } else {//分销订单
                commissionRate = acr.getDistributor_rate();
            }
        } else {//如果为空，则直购订单默认为0.01，分销订单默认为0.03
            //直购订单
            if (CommonConstant.TRADESCOMMISSION_ORDER_TYPE_DIRECT_PURCHASE.equals(orderType)) {
                commissionRate = 1;
            } else {//分销订单
                commissionRate = 3;
            }
        }
        //交易佣金(单位：分)=订单实际支付金额*交易佣金比例
        double tradesCommission = (double) orderRealPrice * (commissionRate / 100);//数据库数值为整数，结算需求，佣金按百分比算，所以*0.01
        //根据小数点后一位四舍五入
        BigDecimal bd = new BigDecimal(tradesCommission);
        //四舍五入后返回交易佣金(单位：分)
        int tradesCommissionInt = bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        //如果交易佣金小于1分钱，则兜底为1分钱
        /*if (tradesCommissionInt < 1) {
            tradesCommissionInt = 1;
        }*/
        return tradesCommissionInt;
    }


    /**
     * 设置门店直购订单入参必选参数
     *
     * @param storeDirect
     * @return
     */
    public String setStoreDirectReqParams(StoreDirect storeDirect) {
        if (StringUtil.isEmpty(storeDirect.getOrderGoods()) || storeDirect.getOrderGoods().size() <= 0) {
            return "orderGoods";
        }
        if (StringUtil.isEmpty(storeDirect.getMobile())) {
            return "mobile";
        }
        if (StringUtil.isEmpty(storeDirect.getSiteId()) || storeDirect.getSiteId() == 0) {
            return "siteId";
        }
        if (StringUtil.isEmpty(storeDirect.getSotreId())) {
            return "sotreId";
        }
        return "";
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

        if ("1".equals(homeDeliveryAndStoresInvite.getOrderType())) {
            if (StringUtil.isEmpty(homeDeliveryAndStoresInvite.getReceiverProvinceCode())) {
                return "receiverProvinceCode";
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
     * 校验手机号码格式(只匹配中国大陆的手机号码)
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     *
     * @return true:正常，false：不正常
     */
    public static boolean checkMobile(String mobile) {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(mobile);
        return matcher.matches();
    }


    /**
     * 获取最优门店
     *
     * @param params 请求参数
     * @return ["total_stores":该商户所有有效门店数,"store":最优门店对象]
     * @throws BusinessLogicException 处理异常
     */
    public Map<String, Object> getBestStore(BestStoreQueryParams params) throws BusinessLogicException {
        Map<String, Object> response = new HashMap<String, Object>();
        checkRequiredParams(params);
        Integer siteId = params.getSiteId();
        String goodsNos = "";
        List<GoodsInfo> goodsInfoInfos = new ArrayList<>();
        if (!StringUtil.isEmpty(params.getGoodsIds())) {
            //如果没有给定商品列表，不走库存
            for (String goodsIdarr : params.getGoodsIds().split(",")) {
                String goodsId = goodsIdarr.split("@")[0];
                String num = goodsIdarr.split("@")[1];
                GoodsInfo info = new GoodsInfo();
                info.setGoodsId(Integer.parseInt(goodsId));
                info.setControlNum(Integer.parseInt(num));
                String goodsCode = goodsMapper.getGoodsCodeID(siteId, Integer.parseInt(goodsId));
                info.setGoodsCode(goodsCode);
                goodsInfoInfos.add(info);
                goodsNos += goodsId + ",";
            }
        }
        //所有有效门店
        List<Store> _stores = ordersMapper.getStoresBySiteId(siteId, null);

        //获取拼团id，根据活动删选门店
        List<String> storesList = null;
        if (!StringUtil.isEmpty(params.getProActivityId())) {
            storesList = groupPurChaseService.getStoresByProActivityId(params.getSiteId(), Integer.parseInt(params.getProActivityId())).get();
            if (!StringUtil.isEmpty(storesList)) {
                String storesStr = org.apache.commons.lang.StringUtils.join(storesList.toArray(), ",");
                logger.info("storesStr:{}", storesStr);
                _stores.stream()
                    .filter(store -> ("," + storesStr + ",").indexOf("," + store.getId() + ",") > -1)
                    .collect(Collectors.toList());
            }
        }


        //所有有效门店（有自提权限门店）
        List<Store> _storesqx = new ArrayList<>();
        for (Store store : _stores) {
            if (store.getServiceSupport().indexOf("160") > -1) {
                _storesqx.add(store);
            }
        }
        //所有有效门店（有自提权限门店+有库存门店）
        List<Store> hasStoresList = getBestStorageStore(siteId, queryStoreDistance(params, _storesqx), goodsNos.substring(0, goodsNos.length() - 1), goodsInfoInfos);

        if (StringUtil.isEmpty(hasStoresList)) {
            response.put("total_stores", 0);
            response.put("store", null);
            return response;
        }
        response.put("total_stores", hasStoresList.size());
        //如果用户经纬度为空，取注册，服务，门店
        Coordinate coordinate = new Coordinate(params.getLng(), params.getLat());
        if (params.getLng() == 0 || params.getLat() == 0 || params.getLng() == null || params.getLat() == null || !mapService.reGeoOne(coordinate)) {
            return getStoreNewy(params, null, response, hasStoresList);
        }

        String yStores = "";
        //最近一个小时的注册门店
        if (!StringUtil.isEmpty(params.getUserId())) {
            yStores = memberMapper.findMobileByIdByTime(params.getSiteId(), params.getMobile());
            if (!StringUtil.isEmpty(yStores) && !StringUtil.isEmpty(hasStoresList)) {
                int yStoresid = Integer.parseInt(yStores);
                for (Store store : hasStoresList) {
                    logger.info(store.getId() + "=-------" + store.getName() + ":store.getId()--------------------yStoresid:" + yStoresid);
                    if (store.getId() == yStoresid) {
                        response.put("store", store);
                        return response;
                    }
                }
            }
        }
        Map<Integer, Store> storeDistanceMap = distributeOrderService.getDistributeMap(hasStoresList, coordinate);
        Integer minDistance = null;
        if (!StringUtil.isEmpty(storeDistanceMap)) {
            //找出距离最近的门店(单位：米)
            Set<Integer> set = storeDistanceMap.keySet();
            Object[] obj = set.toArray();
            Arrays.sort(obj);
            minDistance = (int) obj[0];
            response.put("store", storeDistanceMap.get(minDistance));
        }
        return response;

    }

    public Map<String, Object> getStoreNewy(BestStoreQueryParams params, Set<SortedStore> sorted, Map<String, Object> response, List<Store> hasStoresList) {
        String yStores = "";
        //最近一个小时的注册门店
        if (!StringUtil.isEmpty(params.getUserId())) {
            yStores = memberMapper.findMobileByIdByTime(params.getSiteId(), params.getMobile());
            //地址最近门店
            if (StringUtil.isEmpty(yStores)) {
                Map<String, Object> yStoresParams = new HashedMap();
                yStoresParams.put("siteId", params.getSiteId());
                yStoresParams.put("memberId", params.getUserId());

                Map map = userStoresDistanceMapper.getDistanceByMemberMap(yStoresParams);
                if (!StringUtil.isEmpty(map) && map.containsKey("stores_id")) {
                    yStores = map.get("stores_id") + "";
                }

            }

            //客户最近下的自提订单门店
            if (StringUtil.isEmpty(yStores)) {
                yStores = tradesMapper.selectTradesIdbyPostStyle(params.getSiteId(), params.getBuyerId(), 160);
            }
            //客户最近下的直购订单门店
            if (StringUtil.isEmpty(yStores)) {
                yStores = tradesMapper.selectTradesIdbyPostStyle(params.getSiteId(), params.getBuyerId(), 170);
            }
            //客户最近下的送货上门订单门店
            if (StringUtil.isEmpty(yStores)) {
                yStores = tradesMapper.selectTradesIdbyPostStyle(params.getSiteId(), params.getBuyerId(), 150);
            }
            if (!StringUtil.isEmpty(yStores) && !StringUtil.isEmpty(sorted)) {
                int yStoresid = Integer.parseInt(yStores);
                while (sorted.iterator().hasNext()) {//遍历
                    SortedStore store = sorted.iterator().next();
                    if (store.getStore().getId() == yStoresid) {
                        response.put("store", store.getStore());
                        return response;
                    }
                }
                /*Stores store=storesMapper.getStore(Integer.parseInt(yStores),params.getSiteId());
                if(!StringUtil.isEmpty(store)){
                    response.put("store", store);
                    return response;
                }*/
                response.put("store", sorted.iterator().next().getStore());
            }
            if (!StringUtil.isEmpty(yStores) && !StringUtil.isEmpty(hasStoresList)) {
                int yStoresid = Integer.parseInt(yStores);
                for (Store store : hasStoresList) {
                    logger.info(store.getId() + "=-------" + store.getName() + ":store.getId()--------------------yStoresid:" + yStoresid);
                    if (store.getId() == yStoresid) {
                        response.put("store", store);
                        return response;
                    }
                }
            }
        }

        return response;
    }

    /**
     * 获取门店距离
     *
     * @param params 请求参数
     * @param stores 待获取门店列表
     * @return
     */
    private Set<SortedStore> queryStoreDistance(BestStoreQueryParams params, List<Store> stores) {
        Set<SortedStore> sorted = new TreeSet<>();
        Coordinate from = new Coordinate(params.getLng(), params.getLat());
        long start = Instant.now().toEpochMilli();
        //实际测试parallelStream().forEach并不比for快，所以放弃
        for (Store store : stores) {
            if (StringUtils.isEmpty(store.getGaodeLat()) || StringUtils.isEmpty(store.getGaodeLng())) {
                logger.warn("商户号[{}]门店ID[{}]经纬度信息不完整,忽略该门店.", params.getSiteId(), store.getId());
                continue;
            }
            Coordinate to = new Coordinate(Double.valueOf(store.getGaodeLng()), Double.valueOf(store.getGaodeLat()));
            //调用地图接口获取门店与用户当前位置的距离
            String distance = mapService.bdDistance(from, to);
            if (StringUtils.isEmpty(distance)) {
                logger.warn("获取商户号[{}]门店[{}]坐标与用户[{}]坐标直接距离为空.", params.getSiteId(), to, from);
                continue;
            }
            SortedStore sortedStore = new SortedStore();
            sortedStore.setDistance(Double.valueOf(distance));
            sortedStore.setStore(store);
            sorted.add(sortedStore);
        }
        logger.debug("调用地图api查询所有门店距离完成，总耗时:[{}]", (Instant.now().toEpochMilli() - start));
        return sorted;
    }


    /**
     * 通过erp获取由近及远门店是否有符合条件的库存，如果符合终止查询
     *
     * @param siteId  商户号
     * @param stores  门店列表(经过距离排序的门店列表)
     * @param goodsno 商品编码，多个用逗号隔开
     * @return 符合条件的门店对象
     */
   /* public Store getBestStorageStore(Integer siteId, Set<SortedStore> stores, String goodsno) {
        //九州才走库存
        if (!siteId .equals(100166)) {
            return stores.iterator().next().getStore();
        }
        stores.forEach(s -> logger.info(s.getStore().getName() + "-------------" + s.getDistance()));
        logger.info("1111111111111113333333del" + stores.toString());
        Iterator<SortedStore> iterator = stores.iterator();
        while (iterator.hasNext()) {
            SortedStore store = iterator.next();
            //Map<String, Object> params = new HashMap<>();
            try {
                //params.put("siteId", siteId);
                //params.put("goodsno", goodsno);
                //params.put("uid", store.getStore().getStoresNumber());
                logger.error("storage_service_url路径[{}]",storage_service_url+"?siteId="+siteId+"&goodsno="+goodsno+"&uid="+store.getStore().getStoresNumber());
                //调用ERP接口获取商品库存
                CloseableHttpResponse httpResponse = HttpClientManager.httpGetRequest(storage_service_url+"?siteId="+siteId+"&goodsno="+goodsno+"&uid="+store.getStore().getStoresNumber());
                        //OkHttpUtil.postJson(storage_service_url, params);
                //CloseableHttpResponse httpResponse = null;
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                //int statusCode = 404;
                if (statusCode != 200) {
                    logger.error("请求ERP商品库存查询服务异常,状态码[{}]", statusCode);
                    continue;
                }
                String responseText = HttpClientManager.getResponseString(httpResponse, "utf-8");
                StorageResponse response = JSONObject.parseObject(responseText, StorageResponse.class);
                if (!response.SUCCESS_CODE.equals(response.getCode())) {
                    logger.info(response.getMsg());
                    continue;
                }
                List<StorageResponse.StorageInfo> storageInfoList = response.getInfo();
                boolean exists = false;
                for (StorageResponse.StorageInfo storageInfo : storageInfoList) {
                    if ("0".equals(storageInfo.getSTATE()) && !"0.0".equals(storageInfo.getKcqty())) {
                        exists = true;
                    }
                }
                //如果所有商品都有库存则符合条件，直接返回.
                if (exists) {
                    return store.getStore();
                }
            } catch (IOException e) {
                logger.error("调用erp查询商品库存失败,商户号[{}],门店id[{}]，商品编码[{}]", siteId, store.getStore().getStoresNumber(),
                        goodsno);
                continue;
            }
            continue;
        }
        *//*if (siteId == 100166) {
            return null;
        } else {
            return stores.iterator().next().getStore();
        }*//*
        return stores.iterator().next().getStore();
    }*/

    /**
     * 通过erp获取由近及远门店是否有符合条件的库存，如果符合终止查询
     *
     * @param siteId  商户号
     * @param stores  门店列表(经过距离排序的门店列表)
     * @param goodsno 商品编码，多个用逗号隔开
     * @return 符合条件的门店对象
     */
    public List<Store> getBestStorageStore(Integer siteId, Set<SortedStore> stores, String goodsno, List<GoodsInfo> goodsInfoInfos) {
        List<Store> storesList = new ArrayList<Store>();
        stores.forEach((SortedStore sortedStore) -> {
            storesList.add(sortedStore.getStore());
        });
        List<Store> hasStoresList = distributeOrderService.getBestStorageStore(siteId, storesList, goodsInfoInfos);

        return StringUtil.isEmpty(hasStoresList) ? null : hasStoresList;
    }

    /**
     * 校验必须的请求参数
     *
     * @param params 请求参数
     * @throws BusinessLogicException
     */
    private void checkRequiredParams(BestStoreQueryParams params) throws BusinessLogicException {
        if (params == null) {
            throw new BusinessLogicException("参数不能为空!");
        }
        if (params.getSiteId() == null || params.getSiteId() == 0) {
            throw new BusinessLogicException("SiteId参数不能为空!");
        }

    }

    /**
     * 更新订单的买家支付方式
     *
     * @param req
     * @return
     */
    public boolean updateOrderPayStyle(UpdateOrderPayStyleReq req) throws BusinessLogicException {
        checkRequiredParams(req);
        int flag = ordersMapper.updateOrderPayStyle(req);
        if (flag == 1) {
            return true;
        }
        return false;
    }

    /**
     * 校验必须的请求参数
     *
     * @param req 请求参数
     * @throws BusinessLogicException
     */
    private void checkRequiredParams(UpdateOrderPayStyleReq req) throws BusinessLogicException {
        if (req == null) {
            throw new BusinessLogicException("参数不能为空!");
        }
        if (req.getSiteId() == null || req.getSiteId() == 0) {
            throw new BusinessLogicException("SiteId参数不能为空!");
        }
        if (StringUtil.isEmpty(req.getTradesId())) {
            throw new BusinessLogicException("tradesId参数不能为空!");
        }
        if (StringUtil.isEmpty(req.getPayStyle())) {
            throw new BusinessLogicException("tradesId参数不能为空!");
        }
        if (CommonConstant.TRADES_PAY_TYPE_COLLECT.contains(req.getPayStyle().toLowerCase())) {
            throw new BusinessLogicException(String.format("买家支付方式类型(payStyle)不包括:[%s]", req.getPayStyle().toLowerCase()));
        }
    }

    /**
     * 查询用户付完款是否首单
     *
     * @param siteId
     * @param buyerId
     * @return true:是首单；false：不是首单
     */
    public boolean checkUserFirstOrderByPayment(Integer siteId, Integer buyerId) {
        int orderCount = tradesMapper.queryUserFirstOrder(siteId, buyerId);
        //如果用户有已付款的订单,且订单大于两条,则不是首单
        if (orderCount != 1) {
            return false;
        }
        return true;
    }

    /**
     *
     */
    public int memberUseIntegral(BMember bMember, Map<String, Object> goodsInfos, int needIntegral, long tradesId, int offlineScoreUse) {
        int num = 0;
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("siteId", bMember.getSiteId());
        param.put("buyerId", bMember.getBuyerId());
        if (null != bMember.getName()) {
            param.put("buyerNick", bMember.getName());
        }
        param.put("integralDesc", "积分兑换");
        param.put("integralAdd", 0);
        param.put("integralDiff", goodsInfos.get("intrgral_exchanges").toString());//bi1.subtract(bi2)
        param.put("mark", "积分兑换,订单号:" + tradesId + ",线下积分：" + offlineScoreUse);
        param.put("totalGetIntegrate", 0);//获得积分

        if (offlineScoreUse > 0) {
            //使用了线下积分
            param.put("integralOverplus", 0);  //线上剩余积分为0
            int onlineScore = needIntegral - offlineScoreUse;
            param.put("totalConsumeIntegrate", bMember.getTotalConsumeIntegrate().add(BigInteger.valueOf(onlineScore)));//使用积分
            param.put("integrate", 0);
        } else {
            param.put("totalConsumeIntegrate", bMember.getTotalConsumeIntegrate().add(BigInteger.valueOf(needIntegral)));//使用积分
            param.put("integralOverplus", bMember.getIntegrate().subtract(BigInteger.valueOf(Long.valueOf(goodsInfos.get("intrgral_exchanges").toString()))));
            int integrate = bMember.getIntegrate().intValue() - NumberUtils.toInt(goodsInfos.get("intrgral_exchanges").toString());
            param.put("integrate", integrate < 0 ? 0 : integrate);
        }
        param.put("id", goodsInfos.get("id"));
        param.put("num", goodsInfos.get("num"));
        param.put("type", CommonConstant.TYPE_ORDER_CONVENT);
        int log = mapper.logAdd(param);
        int change = mapper.updateMemberData(param);
        int info = mapper.updateMemberInfoData(param);
        int changeNum = integralGoodsMapper.updateIntegralGoodsNum(param);
        if (log == 1 && change == 1 && changeNum == 1 && info == 1) {
            num = 1;
            //推送积分变动情况
            Map<String, Object> map = new HashMap<>();
            List<Integer> goodsIds = new ArrayList<>();
            goodsIds.add(Integer.parseInt(goodsInfos.get("goods_id").toString()));//商品Id加入数组
            List<String> goodsCode = goodsMapper.getGoodsCode(bMember.getSiteId(), goodsIds);
            if (goodsCode.size() > 0) {
                map.put("goodsNo", goodsCode.get(0));//当前积分兑换商品一次只能兑换一个，如若多个用逗号隔开
            }
            map.put("mobile", bMember.getMobile());
            map.put("tradesId", tradesId);
            map.put("type", 1);
            map.put("sumScore", needIntegral);
            map.put("on_costScore", -Integer.valueOf(param.get("integralDiff").toString()) + offlineScoreUse);//抵扣的线上积分数量
            map.put("off_costScore", -offlineScoreUse);
            map.put("on_integral", param.get("integrate"));
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("create_time", sd.format(new Date()));
            map.put("desc", "兑换积分");
            erpToolsService.integralChange(bMember.getSiteId(), map);
        }
        return num;
    }

    public List<Orders> findOrderByCouponDetail(Integer siteId, Integer memberId, String activityId, int ruleId, String orderId, List<Integer> goods) {
        return ordersMapper.getordersByCouponDetail(siteId, memberId, activityId, ruleId, orderId, goods);
    }
    public String findTradesName(Integer siteId, Long tradesId) {
        String tradesName="51商品";
        String merchantTreat=merchantMapper.getMerchantTitle(String.valueOf(siteId));
        if (StringUtil.isNotEmpty(merchantTreat)) {
            tradesName=merchantTreat+"-消费";
        }
        /*List<Orders> ordersList = ordersMapper.getOrdersListByTradesId(String.valueOf(siteId), tradesId);//获取订单下商品
        if (ordersList != null && ordersList.size() > 0) {
            tradesName=ordersList.get(0).getGoodsTitle();
        }*/
        return tradesName;
    }
}
