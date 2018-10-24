package com.jk51.modules.order.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.ParamErrorException;
import com.jk51.exception.UnknownTypeException;
import com.jk51.model.Address;
import com.jk51.model.ChOrderRemind;
import com.jk51.model.concession.ConcessionCalculate;
import com.jk51.model.concession.ConcessionDesc;
import com.jk51.model.concession.GiftMsg;
import com.jk51.model.concession.result.BaseResult;
import com.jk51.model.concession.result.GiftResult;
import com.jk51.model.concession.result.GoodsData;
import com.jk51.model.concession.result.Result;
import com.jk51.model.coupon.requestParams.OrderMessageParams;
import com.jk51.model.coupon.returnParams.UseCouponReturnParams;
import com.jk51.model.distribute.*;
import com.jk51.model.erpprice.BGoodsErp;
import com.jk51.model.grouppurchase.GroupPurchase;
import com.jk51.model.map.Coordinate;
import com.jk51.model.order.*;
import com.jk51.model.order.response.DistributeResponse;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.treat.MerchantExtTreat;
import com.jk51.model.treat.O2OMeta;
import com.jk51.modules.concession.ConcessionCalculateBaseImpl;
import com.jk51.modules.coupon.service.CouponProcessService;
import com.jk51.modules.distribution.mapper.DistributorMapper;
import com.jk51.modules.distribution.mapper.GoodsDistributeMapper;
import com.jk51.modules.distribution.mapper.RecruitMapper;
import com.jk51.modules.distribution.mapper.RewardTemplateMapper;
import com.jk51.modules.distribution.service.RewardTemplateService;
import com.jk51.modules.erpprice.service.ErpPriceService;
import com.jk51.modules.grouppurchase.request.GroupPurchaseForBeforeOrder;
import com.jk51.modules.grouppurchase.service.GroupPurChaseService;
import com.jk51.modules.im.mapper.ChOrderRemindMapper;
import com.jk51.modules.im.service.GeTuiPush;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.integral.service.IntegralService;
import com.jk51.modules.logistics.service.AddressService;
import com.jk51.modules.map.service.MapService;
import com.jk51.modules.merchant.service.MerchantExtService;
import com.jk51.modules.offline.mapper.BStoresStorageMapper;
import com.jk51.modules.offline.service.ErpMerchantSettingService;
import com.jk51.modules.offline.service.StorageService;
import com.jk51.modules.order.mapper.DistributeOrderMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.promotions.service.PromotionsActivityService;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.modules.trades.service.TradesDeliveryService;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import com.jk51.modules.treat.service.DeliveryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jk51.commons.base.Preconditions.checkNotZero;
import static com.jk51.modules.concession.ConcessionCalculateBaseImpl.Param;
import static com.jk51.modules.concession.constants.ConcessionConstant.TYPE_COUPON_DETAIL;
import static com.jk51.modules.concession.constants.ConcessionConstant.TYPE_PROMOTIONS_ACTIVITY;
import static java.util.stream.Collectors.groupingBy;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:分单service
 * 作者: baixiongfei
 * 创建日期: 2017/2/16
 * 修改记录:
 */
@Service
public class DistributeOrderService {
    public static final Logger logger = LoggerFactory.getLogger(DistributeOrderService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private IntegralService integralService;
    @Autowired
    private MapService mapService;
    @Autowired
    private CouponProcessService couponProcessService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private MerchantExtService merchantExtService;
    @Autowired
    private MerchantExtService merchantExtservice;
    @Autowired
    private StorageService storageService;
    @Autowired
    private RewardTemplateService rewardTemplateService;
    @Autowired
    private ErpPriceService erpPriceService;
    @Autowired
    private GroupPurChaseService groupPurChaseService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private PromotionsActivityService promotionsActivityService;
    @Autowired
    private GeTuiPush geTuiPush;


    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private DistributeOrderMapper distributeOrderMapper;
    @Autowired
    private ChOrderRemindMapper chOrderRemindMapper;
    @Autowired
    private StoreAdminMapper storeAdminMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private GoodsDistributeMapper goodsDistributeMapper;
    @Autowired
    private RewardTemplateMapper rewardTemplateMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private DistributorMapper distributorMapper;
    @Autowired
    private RecruitMapper recruitMapper;
    @Autowired
    private BStoresStorageMapper bStoresStorageMapper;
    @Autowired
    private MerchantExtTreatMapper merchantExtTreatMapper;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private TradesDeliveryService tradesDeliveryService;
    @Autowired
    private ErpMerchantSettingService erpMerchantSettingService;
    @Autowired
    private StoresService storesService;

    //private String storage_service_url;
    //@Value("${erp.storage_service_url}")


    /**
     * 查找送货上门订单的门店信息(包括计算运费价格)
     *
     * @param stores
     * @param distribute
     * @param orderTotalPrice 订单金额减去积分抵扣之后的金额
     * @param userFlag        使用场景，1：下单时使用，2、预下单时使用
     * @return
     */
    public DistributeResponse selectHomeDeliveryOrderStore(List<Store> stores, Distribute distribute, int orderTotalPrice, List<GoodsInfo> goodsInfoInfos, String userFlag) {
        DistributeResponse response = new DistributeResponse();
        //运费,单位：分
        int freight = 0;
        //先把收货人地址调用高德地图API转换成坐标地址
        Coordinate coordinate = mapService.geoCoordinate(String.valueOf(distribute.getUserDeliveryAddr()));

        logger.info("用户经纬度:{}，Addr：{}", coordinate, distribute.getUserDeliveryAddr());

        int minDistance = 0;
        long startMillis, endMillis;
        startMillis = System.currentTimeMillis();
        //然后再调用高德地图API，计算两个坐标的距离，根据距离最近的来选择门店
        //Store store= selectOrderStore(stores,distribute);
        startMillis = System.currentTimeMillis();
        //然后再调用高德地图API，计算两个坐标的距离，根据距离最近的来选择门店
        Map<Integer, Store> storeDistanceMap = getDistributeMap(stores, coordinate);
        if (!StringUtil.isEmpty(stores)) {
            endMillis = System.currentTimeMillis();
            logger.info("foreach 处理分单计算最近门店耗时: {}, 门店数:{}", endMillis - startMillis, stores.size());
            //找出距离最近的门店(单位：米)
            Set<Integer> set = storeDistanceMap.keySet();
            if (!StringUtil.isEmpty(set)) {
                Object[] obj = set.toArray();
                Arrays.sort(obj);
                for (Object object:obj){
                    Integer distance = (int) object;
                    if(distance<5000){
                        Store store = storeDistanceMap.get(distance);
                        Store haskcstore=getBestStorageStoreOne(distribute.getSiteId(),  store,  goodsInfoInfos);
                        if(!StringUtil.isEmpty(haskcstore)){
                            minDistance = distance;
                            break;
                        }
                    }
                }
                //minDistance = (int) obj[0];
            }
        }
        /**
         *
         * 根据距离来判断是否在商家的O2O配送范围内，并且根据不同的O2O距离来计算运费
         * 如果不在O2O的配送范围内(包括第三方配送服务，如蜂鸟配送)，则使用快递发货，计算快递的费用
         * 计算运费
         * 先判断商家是否开启了O2O配送设置
         * 如果开启了，则根据距离，匹配运费规则，并且如果在一定金额内商家包邮，则运费为零
         * 如果开启了，并且距离超过商家设置的O2O配送的最大距离，则走快递的方式计算运费
         * 如果没有开启O2O配送设置，则走快递的方式计算运费
         */
        //查询是否开启O2O配送设置
        Meta meta = distributeOrderMapper.getMeta(distribute.getSiteId(), CommonConstant.SITE_O2O_CARRIAGE_CONFIG);

        //开启
        if (meta != null && meta.getMetaStatus() == 1) {
            logger.info("开启了o2o info:{}", meta);
            try {
                List<O2ORule> o2ORule = jsonO2ORulelist(meta.getMetaVal());
                //JacksonUtils.json2list(meta.getMetaVal(),O2ORule.class);
                //先判断是否已经超过商家设置的O2O运费的最大距离
                //超过，走快递
                //MerchantExtTreat merchantExt = merchantExtTreatMapper.selectByMerchantId(distribute.getSiteId());
                //Meta metadel = distributeOrderMapper.getMeta(distribute.getSiteId(), CommonConstant.SITE_O2O_CARRIAGE_CONFIG_FEFAULT);
                O2OMeta o2OMeta = new O2OMeta();
                o2OMeta.setSiteId(distribute.getSiteId());
                o2OMeta.setOrderTime(new Date());
                String metadel = deliveryService.getDeliveryConf(o2OMeta);
                if (!StringUtil.isEmpty(metadel)) {
                    List<O2ORule> o2ORuledel = jsonO2ORulelist(metadel);
                    //merchantExt.getlogistics_flag_mode() == 0 &&
                    if (!checkMaxDistance(o2ORuledel, minDistance) && minDistance != 0) {
                        //走第三方配送
                        freight = calculateO2OFreight(o2ORuledel, minDistance, (double) orderTotalPrice);
                        int freightCommission = freight;
                        logger.info(freightCommission + "----开启了o2o-------freightCommission");
                        if (minDistance <= 4000) {
                            response.setIsO2O(0);
                            response.setFreightCommission(freightCommission);
                        } else {
                            //查找门店信息
                            Store store = storeDistanceMap.get(minDistance);
                            //计算美团配送价格
                            int siPrice = tradesDeliveryService.calcMtPrice(store.getCity(), 4);
                            double md = ((double) minDistance) / 1000;
                            int mtPrice = tradesDeliveryService.calcMtPrice(store.getCity(), md);
                            logger.info("mtPrice:{}----siPrice:{}开启了o2o-------minDistance:{}", mtPrice, siPrice, minDistance);

                            //51在此区间的补贴小于0-4公里的最大补贴
                            if (mtPrice - freightCommission > siPrice) {
                                //response.setIsO2O(1);
                                freight = getHomeDeliveryOrderExpress(distribute, goodsInfoInfos);
                            } else {
                                response.setIsO2O(0);
                                response.setFreightCommission(freightCommission);
                            }
                        }
                    } else {
                        //商家自己配送
                        if (checkMaxDistance(o2ORule, minDistance) || minDistance == 0) {
                            freight = getHomeDeliveryOrderExpress(distribute, goodsInfoInfos);
                        } else {
                            freight = calculateO2OFreight(o2ORule, minDistance, (double) orderTotalPrice);
                            response.setIsO2O(1);
                        }
                    }
                } else {
                    //商家自己配送
                    if (checkMaxDistance(o2ORule, minDistance) || minDistance == 0) {
                        freight = getHomeDeliveryOrderExpress(distribute, goodsInfoInfos);
                    } else {
                        freight = calculateO2OFreight(o2ORule, minDistance, (double) orderTotalPrice);
                        response.setIsO2O(1);
                    }
                }

            } catch (Exception e) {
                logger.error("JSON解析错误：{}", e);
            }
        } else {//直接走快递的方式
            freight = getHomeDeliveryOrderExpress(distribute, goodsInfoInfos);
        }

        response.setOrderFreight(freight);

        //下单
        if (userFlag.equals("1")) {
            //找到门店编号
            //String storeNumber = storeDistanceMap.get(minDistance);
            //查找门店信息
            Store store = storeDistanceMap.get(minDistance);
            //distributeOrderMapper.getStoresById(distribute.getSiteId(), storeNumber);
            logger.info("分单门店编号: {}", store);
            response.setStore(store);
        } else if (userFlag.equals("2")) {//预下单
            //找到门店编号
            // String storeNumber = storeDistanceMap.get(minDistance);
            //查找门店信息
            Store store = storeDistanceMap.get(minDistance);
            //distributeOrderMapper.getStoresById(distribute.getSiteId(), storeNumber);
            if (StringUtil.isEmpty(response.getIsO2O())) {
                if (StringUtil.isEmpty(store)) {
                    store = new Store();
                }
                String arrivalTime = deliveryService.queryDeliveryArrivalTime(distribute.getSiteId());
                store.setDeliveryTime(arrivalTime);
            }
            logger.info("分单门店编号: {}", store);
            response.setStore(store);
        }
        response.setMinDistance(minDistance);
        return response;
    }

    /**
     * 计算快递运费
     *
     * @param distribute
     * @param goodsInfoInfos
     * @return
     */
    public int getHomeDeliveryOrderExpress(Distribute distribute, List<GoodsInfo> goodsInfoInfos) {

        int expressFreight = 0;
        //查询商家快递运费规则
        DeliveryMethod deliveryMethod = distributeOrderMapper.getExpressRuledu(distribute.getSiteId());
        //distributeOrderMapper.getExpressRule(distribute.getSiteId(),distribute.getDeliveryMethod());
        //如果快递规则被禁用，则快递费用为0
        if (null != deliveryMethod && deliveryMethod.getIsActivation() == 1) {

            //获取商品总重量
            int goodsTotalWeight = calculateGoodsTotalWeight(goodsInfoInfos, distribute.getOrderGoods());
            //商家指定区域
            String appointArea = deliveryMethod.getAppointArea();
            /**
             * 以下默认地区，都不包括商家指定地区
             */
            //默认地区首重值
            int defFirstWeight = deliveryMethod.getFirstWeight();
            //默认地区首重运费
            int defFirstWeightFreight = deliveryMethod.getDefFirstprice();
            //默认地区超重值
            int defOverWeight = deliveryMethod.getAddWeight();
            //默认地区超重运费
            int defOverWeightFreight = deliveryMethod.getDefAddprice();
            //如果指定地区为空，则全部按照默认地区的运费来计算，
            if (StringUtil.isEmpty(appointArea)) {
                //如果商品总重量小于默认地区首重值，则运费为默认地区首重运费
                if (goodsTotalWeight < defFirstWeight) {
                    expressFreight = defFirstWeightFreight;
                } else {
                    //否则按照 快递费用=(向上取整(订单重量-首重重量)/超重重量) * 超重金额 + 首重金额 的公式来计算费用
                    /**
                     * 比如：订单总重量为：550g，首重重量为：100g，续重重量为：100g，续重金额为：2元，首重金额为：1元，
                     * 则订单运费为：550 - 100 = 450 -> 450/100=4.5=5 -> 5 * 2 = 10 -> 10 + 1 = 11元
                     */
                    //超重比率
                    double overWeighRate = (double) (goodsTotalWeight - defFirstWeight) / (double) defOverWeight;
                    //超重部分的运费
                    int overWeightFreight = (int) Math.ceil(overWeighRate) * defOverWeightFreight;
                    //总运费
                    expressFreight = overWeightFreight + defFirstWeightFreight;
                }
            } else {
                //校验用户的收货地址是否在商家设置的指定区域内
                //指定地区首重列表
                String[] appointFirstWeights = deliveryMethod.getAppointFirstweight().split(";");
                //指定地区超重列表
                String[] appointAddWeights = deliveryMethod.getAppointAddweight().split(";");
                //指定地区首重价格列表
                String[] appointFirstPrices = deliveryMethod.getAppointFirstprice().split(";");
                //指定地区超重价格列表
                String[] appointAddPrices = deliveryMethod.getAppointAddprice().split(";");
                //指定地区代码列表
                String[] appointAreas = appointArea.split(";");
                String province = ordersMapper.getParentId(distribute.getUserDeliveryProvinceCode() + "");
                if (appointFirstWeights.length == appointAddWeights.length && appointAddWeights.length == appointFirstPrices.length
                    && appointFirstPrices.length == appointAddPrices.length) {
                    //循环区域列表代码，如果用户的收货地址在指定地区内，则按照指定地区的运费规则来计算
                    for (int i = 0; i < appointAreas.length; i++) {
                        if (appointAreas[i].contains(String.valueOf(distribute.getUserDeliveryProvinceCode()))
                            || appointAreas[i].contains(String.valueOf(distribute.getUserDeliveryCityCode())) || province.equals(appointAreas[i])) {
                            //商品总重量小于指定地区首重，则价格就为指定地区首重运费
                            if (goodsTotalWeight < Integer.parseInt(appointFirstWeights[i])) {
                                //总运费
                                expressFreight = Integer.parseInt(appointFirstPrices[i]);
                            } else {
                                //指定地区首重1000
                                int appointFirstWeight = Integer.parseInt(appointFirstWeights[i]);
                                //指定地区超重500
                                int appointAddWeight = Integer.parseInt(appointAddWeights[i]);
                                //指定地区首重运费6
                                int appointFirstPrice = Integer.parseInt(appointFirstPrices[i]);
                                //指定地区超重运费1
                                int appointAddPrice = Integer.parseInt(appointAddPrices[i]);
                                //超重比率（1300-500）/500
                                double overWeighRate = (double) (goodsTotalWeight - appointFirstWeight) / (double) appointAddWeight;
                                //指定地区超重部分的运费（1300-500）/500*1
                                int overWeightFreight = (int) Math.ceil(overWeighRate) * appointAddPrice;
                                //总运费
                                expressFreight = overWeightFreight + appointFirstPrice;
                            }
                            break;
                        }
                    }
                    //默认运费（指定地区除外）
                    if (expressFreight == 0) {
                        //如果商品总重量小于默认地区首重值，则运费为默认地区首重运费
                        if (goodsTotalWeight < defFirstWeight) {
                            expressFreight = defFirstWeightFreight;
                        } else {
                            //否则按照 快递费用=(向上取整(订单重量-首重重量)/超重重量) * 超重金额 + 首重金额 的公式来计算费用
                            /**
                             * 比如：订单总重量为：550g，首重重量为：100g，续重重量为：100g，续重金额为：2元，首重金额为：1元，
                             * 则订单运费为：550 - 100 = 450 -> 450/100=4.5=5 -> 5 * 2 = 10 -> 10 + 1 = 11元
                             */
                            //超重比率
                            double overWeighRate = (double) (goodsTotalWeight - defFirstWeight) / (double) defOverWeight;
                            //超重部分的运费
                            int overWeightFreight = (int) Math.ceil(overWeighRate) * defOverWeightFreight;
                            //总运费
                            expressFreight = overWeightFreight + defFirstWeightFreight;
                        }
                    }
                }

            }
        }
        return expressFreight;
    }

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
        List<GoodsInfo> goodsInfoInfos = getGoodsInfos(req.getSiteId(), req.getOrderGoods(), storeId, req.getErpStoreId(), req.getErpAreaCode());

        /* todo 稳定后删除
        // 如果是拼团活动的数据 需要对商品信息进行重新计算 算出拼团价格
        // remark 拼团活动计算入口
        GroupPurchaseResponseForBeforeQuery groupPurchaseResponseForBeforeQuery = null;
        if (null != req.getGroupPurchase()) {
            groupPurchaseResponseForBeforeQuery = groupPurChaseService.getDataForGroupPurchaseResponseForBeforeQuery(req.getGroupPurchase(), req);
            if ((groupPurchaseResponseForBeforeQuery.getResultStatus() != null && groupPurchaseResponseForBeforeQuery.getResultStatus() == -1) ||
                (groupPurchaseResponseForBeforeQuery.getGetGroupProActivityStatus() != null && groupPurchaseResponseForBeforeQuery.getGetGroupProActivityStatus() == -1) ||
                (groupPurchaseResponseForBeforeQuery.getGroupPurchasestatus() != null && groupPurchaseResponseForBeforeQuery.getGroupPurchasestatus() == -1)
                ) {
                throw new GroupPurchaseException("拼团活动预下单状态异常。。。");
            } else {
                goodsInfoInfos = groupPurchaseResponseForBeforeQuery.getListGoodsInfo();
            }

        }*/

        //List<GoodsInfo> goodsInfoInfos = getGoodsInfos(req.getSiteId(), req.getOrderGoods(), req.getStoresId() + "");
        logger.info("======beforeOrder门店信息buyer_id:buyer_id{},间隔：{}", req.getStoresId(), System.currentTimeMillis() - startMillis);
        //获取有商品的门店
        List<String> storesStr = null;
        if (!StringUtil.isEmpty(req.getGroupPurchase()) && !StringUtil.isEmpty(req.getGroupPurchase().getProActivityId())) {
            storesStr = groupPurChaseService.getStoresByProActivityId(req.getSiteId(), req.getGroupPurchase().getProActivityId()).get();
        }
        List<Store> stores = new ArrayList<>();
        if (!StringUtil.isEmpty(req.getTradesSource()) && req.getTradesSource().equals(130)&&(StringUtil.isEmpty(req.getOrderType())||"3".equals(req.getOrderType())) ) {
            Store stoero=ordersMapper.getStore(req.getStoresId(), req.getSiteId());
            if(!StringUtil.isEmpty(stoero)){
                stores.add(stoero);
            }
        }else {
            stores = getStore(req.getSiteId(), goodsInfoInfos, req.getReceiverCityCode(), storesStr);
        }

        logger.info("======beforeOrder获取有商品的门店stores:{},间隔：{}", stores, System.currentTimeMillis() - startMillis);
        //计算价格之前获取最近门店
        Distribute distributezui = new Distribute();
        distributezui.setUserDeliveryAddr(req.getAddress());
        /*if(!StringUtil.isEmpty(req.getTradesSource())&&req.getTradesSource().equals(130)){
            Store storezui= selectOrderStore(stores,distributezui);
            response.setStore(storezui);
        }*/
        getGoodsTotalPrice(req, goodsInfoInfos, req.getOrderGoods(), response, merchantExt.getHas_erp_price());
        int goodsTotalPrice = getGoodsTotalPrice(req, goodsInfoInfos, req.getOrderGoods(), response, merchantExt.getHas_erp_price(), req.getTradesSource());
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
            if (StringUtil.isNotEmpty(req.getAddrCode())) {
                distribute.setUserDeliveryProvinceCode(Integer.parseInt(req.getAddrCode()));
                distribute.setUserDeliveryAddr(req.getAddress());
                //用户运费信息
                response = selectHomeDeliveryOrderStore(stores, distribute, goodsTotalPrice, goodsInfoInfos, useFlag);
                freight = response.getOrderFreight();
            } else {
                //查询用户的默认收货地址
                Address address = addressService.findDefault(req.getBuyerId(), req.getSiteId());
                if (null != address) {
                    distribute.setUserDeliveryProvinceCode(address.getProvinceCode());
                    //用户运费信息
                    response = selectHomeDeliveryOrderStore(stores, distribute, goodsTotalPrice, goodsInfoInfos, useFlag);
                    freight = response.getOrderFreight();
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

            freight = 0;
        }
        logger.info("======beforeOrder计算运费freight:{},", freight);
        response.setDistributePrice(distributePrice);
        //查询用户的剩余积分
        response.setIntegral(integralMax(req.getSiteId(), goodsTotalPrice, String.valueOf(req.getBuyerId())));
        //订单原始金额
        response.setOrderOriginalPrice(goodsTotalPrice);
        logger.info("======beforeOrder订单原始金额goodsTotalPrice:{},间隔：{}", goodsTotalPrice, System.currentTimeMillis() - startMillis);
        int integralDeductionPrice = 0;
        response.setIntegralDeductionPrice(integralDeductionPrice);
        response.setNeedIntegral(0);
//        int integralDeductionPrice = calculateIntegralDiscount(req.getSiteId(), goodsTotalPrice, String.valueOf(req.getBuyerId()), req.getIntegralUse());

//        response.setIntegralDeductionPrice(integralDeductionPrice);
//        logger.info("======beforeOrder积分抵扣金额integralDeductionPrice:{},间隔：{}",integralDeductionPrice,System.currentTimeMillis()-startMillis);        //订单总金额=订单原始金额 + 运费 - 积分抵扣金额  用于判断优惠券的抵扣金额
        // 商品总价+运费-积分优惠
        int coupondisPrice = (goodsTotalPrice + freight) - response.getIntegralDeductionPrice();
        // 优惠券活动抵扣金额，由优惠券模块提供接口查询
        int concessionDeductionPrice = 0;
        /* todo 稳定后删除
       if (!StringUtil.isEmpty(req.getCouponId()) && req.getCouponId() != 0 && null == req.getGroupPurchase()) {
            // remark 优惠券计算入口
            UseCouponReturnParams couponReturnParams = couponDiscountPrice(req, freight, coupondisPrice, Integer.parseInt(req.getAddrCode() == null ? "0" : req.getAddrCode()), goodsInfoInfos);
            if (couponReturnParams != null && null != couponReturnParams.getCouponMoney()) {
                couponDeductionPrice = couponReturnParams.getCouponMoney();
            }

            if (couponReturnParams != null && null != couponReturnParams.getGiftRuleMsg()) {
                response.setGiftRuleMsg(couponReturnParams.getGiftRuleMsg());
            }

        }
        //优惠券抵扣金额
        logger.info("======beforeOrder优惠券抵扣金额couponDeductionPrice:{},", couponDeductionPrice);
        response.setCouponDeductionPrice(couponDeductionPrice);
        */
        //优惠活动抵扣金额，由优惠活动模块提供接口查询
        /*int couponActivityPrice = 0;*/
        Integer postageDiscount = 0;
        // 分销不参与优惠券和活动
        if (!req.isDistributeGoods()) {
            if ("1".equals(useFlag)) {
                Param param = buildParamForConcession(goodsInfoInfos, req, freight, response);

                Optional<List<Integer>> idsList = StringUtil.convertToIdsList(req.getPromActivityIdArr());
                Optional<List<PromotionsActivity>> optional;
                if (idsList.isPresent())
                    optional = promotionsActivityService.getPromotionsActivitiesReleasedAndSorted(param.getSiteId(), idsList.get());
                else
                    optional = promotionsActivityService.getPromotionsActivitiesReleasedAndSorted(param.getSiteId());

                List<PromotionsActivity> promotionsActivities = optional.orElseGet(ArrayList::new);

                // 计算
                ConcessionCalculate concessionCalculate = new ConcessionCalculateBaseImpl(param, promotionsActivities);
                Optional<? extends BaseResult> concessionResult = concessionCalculate.calculateInAllRule();
                if (concessionResult.isPresent()) {
                    Result result = (Result) concessionResult.get();
                    postageDiscount = result.getPostageDiscount();
                }
                // 输出结果
                buildResultForResponse(req, response, concessionResult);
                concessionDeductionPrice = response.getConcessionDeductionPrice();

                if (req.getConcessionResult() != null) {
                    if (!checkCalculateIsSame(req.getConcessionResult(), response.getConcessionResult())) {
                        throw new RuntimeException("预下单和下单计算结果不同");
                    }
                }

                if (req.getGiftGoods() != null && req.getGiftGoods().size() != 0) {
                    if (!checkGiftsPickedByCustomer(req.getGiftGoods(), response.getConcessionResult())) {
                        throw new RuntimeException("顾客选取的赠品不符合赠品列表");
                    }
                }
/*
            // 优惠券活动抵扣金额
            concessionDeductionPrice = orderDeductionService.deductionMoneyByActivityIds(
                req.getPromActivityIdArr(), req, freight, coupondisPrice,
                Integer.parseInt(req.getAddrCode() == null ? "0" : req.getAddrCode()), goodsInfoInfos);
*/
            } else if ("2".equals(useFlag)) {

            /* todo 稳定后删除
            ProRuleMessageParam proRuleMessageParam = new ProRuleMessageParam(req, freight, couponDeductionPrice);
            // remark 活动（除团购外计算入口）
            Map<String, Object> resultMap = promotionsRuleService.proRuleUsableForMax(proRuleMessageParam);
            response.setIsHaveGiftProRuleActivity((null != resultMap && null != resultMap.get("isHaveGiftProRuleActivity")) ? resultMap.get("isHaveGiftProRuleActivity").toString() : null);
            response.setGiftpromotionsRuleId((null != resultMap && null != resultMap.get("promotionsRuleId")) ? Integer.parseInt(resultMap.get("promotionsRuleId").toString()) : null);
            response.setGiftpromotionsActivityId((null != resultMap && null != resultMap.get("promotionsActivityId")) ? Integer.parseInt(resultMap.get("promotionsActivityId").toString()) : null);
            response.setProRuleList((null != resultMap && null != resultMap.get("proRuleList")) ? (List<UsePromotionsParams>) resultMap.get("proRuleList") : null);
            response.setGiftRuleMsg((null != resultMap && null != resultMap.get("giftRuleMsg")) ? (Map<String, Object>) resultMap.get("giftRuleMsg") : null);
            couponActivityPrice = (null != resultMap && null != resultMap.get("proRuleDeductionPrice")) ? Integer.parseInt(resultMap.get("proRuleDeductionPrice").toString()) : 0;
            couponActivityPrice = (goodsTotalPrice + freight - integralDeductionPrice - couponDeductionPrice - distributeDiscountPrice - couponActivityPrice) < 0 ? (goodsTotalPrice + freight - integralDeductionPrice - couponDeductionPrice - distributeDiscountPrice) : couponActivityPrice;
            response.setProRuleDeductionPrice(couponActivityPrice);*/

                // 入参
                Param param = buildParamForConcession(goodsInfoInfos, req, freight, response);
                Optional<List<PromotionsActivity>> optional = promotionsActivityService.getPromotionsActivitiesReleasedAndSorted(param.getSiteId());
                List<PromotionsActivity> promotionsActivities = optional.orElseGet(ArrayList::new);

                // 计算
                ConcessionCalculate concessionCalculate = new ConcessionCalculateBaseImpl(param, promotionsActivities);
                Optional<? extends BaseResult> concessionResult = concessionCalculate.calculateInAllRule();
                if (concessionResult.isPresent()) {
                    Result result = (Result) concessionResult.get();
                    postageDiscount = result.getPostageDiscount();
                }
                // 输出结果
                buildResultForResponse(req, response, concessionResult);
                concessionDeductionPrice = response.getConcessionDeductionPrice();
            }
        }
        response.setPostageDiscount(postageDiscount);
        //是否是分销订单
        response.setDistributeGoods(req.isDistributeGoods());
        //优惠活动抵扣金额
        logger.info("======beforeOrder优惠活动抵扣金额couponActivityPrice:{},", concessionDeductionPrice + postageDiscount);
        response.setProRuleDeductionPrice(response.getProRuleDeductionPrice() + postageDiscount);
        //前端显示，邮费也减掉
        response.setConcessionDeductionPrice(concessionDeductionPrice);

        //订单实际价格=商品总价 + 运费 - 积分抵扣金额 - 优惠券和活动抵扣金额 - 分销商优惠
//        int orderRealPrice = (goodsTotalPrice + freight - integralDeductionPrice - concessionDeductionPrice - distributeDiscountPrice);
        //订单实际价格=商品总价 - （积分抵扣金额 + 优惠券和活动抵扣金额 + 分销商优惠） + freight
        int orderRealPrice = goodsTotalPrice - integralDeductionPrice - concessionDeductionPrice - distributeDiscountPrice;
        //优惠至低于0元，但不能影响到运费计算
        if (orderRealPrice < 0)
            orderRealPrice = 0;
        //尝试 减掉运费
        freight -= postageDiscount;
        //运费不能低于0元
        if (freight < 0)
            freight = 0;
        orderRealPrice += freight;
        //积分抵扣金额
        if (req.getExchange() != null && req.getExchange()) {
            // 兑换商品需要的积分值
            int neddIntegral = calcIntegral(req);
            response.setNeedIntegral(neddIntegral);
            // 使用积分抵扣 实付款只有运费
            orderRealPrice = freight;
        }

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
            String distributeTip = this.getDistributeTip(req, distributePrice);
            response.setDistributeTip(distributeTip);
            response.setDistributeDiscountPrice(distributeDiscountPrice);
        }
        //-------------------------------------------end-------------yeah--------------
        logger.info("======beforeOrder分单结束response:{},间隔：{}", response.toString(), System.currentTimeMillis() - startMillis);
        response.setGoodsInfoInfos(goodsInfoInfos);
        //获取商家设置支持订单类型
        response.setSettingDisMap(querySettingDis(req.getSiteId() + "", req.getTradesSource(), response.getOrderRealPrice()));
        return response;
    }


    public String getDistributeInfo(BeforeCreateOrderReq req) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
//        Distributor distributor = this.checkWhetherDistributor(req.getMobile(), req.getSiteId());//判断是否是分销商
        List<OrderGoods> distributeGoods = req.getOrderGoods().stream().filter(orderGood -> null != this.goodsDistributeMapper.selectByGoodsIdAndSiteId(req.getSiteId(), orderGood.getGoodsId())).collect(Collectors.toList());
        setGoodsPrice(distributeGoods, req.getSiteId(), req.getMobile());
        result.put("goods", distributeGoods);//分销商品信息

        //获取折扣信息
        Recruit recruit = null;
        List<Recruit> recruits = this.recruitMapper.getRecruitListByOwner(String.valueOf(req.getSiteId()));
        if (!StringUtil.isEmpty(recruits) && recruits.size() > 0) {
            recruit = recruits.get(0);
            Map<String, Object> rule = JacksonUtils.json2map(recruit.getRule());

            QueryTemplate queryTemplate = new QueryTemplate();
            queryTemplate.setOwner(req.getSiteId());
            List<Map<String, Object>> rewardTemp = this.rewardTemplateService.queryDiscountMax(queryTemplate);//锅胜接口
            Map<String, Object> discount = rewardTemp.get(0);
            //----------------------------前台无法解析，转换成前台需要的格式-------------begin--------------------------
            List<Object> rulelist = new ArrayList<>();
            for (int i = 1; i < 6; i++) {
                int level = Double.valueOf(Objects.toString(rule.get("level" + i), "0")).intValue();
                if (level > 0) {
                    Map<Object, Object> map = new HashMap<>();
                    map.put("level", "T" + i);
                    map.put("money", level);
                    map.put("discount", Double.parseDouble(discount.get("level" + i).toString()) == 100 ? 0 : (Double.parseDouble(discount.get("level" + i).toString()) / 10));
                    rulelist.add(map);
                }
            }
            //----------------------------前台无法解析，转换成前台需要的格式---------------end------------------------
            result.put("rule", rulelist);//招募规则详情
        }

        return JacksonUtils.mapToJson(result);
    }

    public void setGoodsPrice(List<OrderGoods> distributeGoods, Integer siteId, String mobile) {
        Distributor distributor = this.checkWhetherDistributor(mobile, siteId);//判断是否是分销商

        distributeGoods.stream().forEach(orderGood -> {
//            Goods good = this.goodsMapper.getBySiteIdAndGoodsId(orderGood.getGoodsId(), siteId);
            GoodsDistribute goodsDistribute = this.goodsDistributeMapper.selectByGoodsIdAndSiteId(siteId, orderGood.getGoodsId());
            if (null != goodsDistribute && null != distributor && distributor.getStatus() == 1) {
                RewardTemplate rewardTemplate = this.rewardTemplateMapper.selectByTemplateId(goodsDistribute.getDistributionTemplate());
                if (null != rewardTemplate && StringUtil.isNotBlank(rewardTemplate.getDiscount()) && rewardTemplate.getIsUsed() == 1) {
                    //{"level1":"90","level2":"80","level3":"70","level4":"60","level5":"50"}
                    Map discounts = null;
                    try {
                        discounts = JacksonUtils.json2map(rewardTemplate.getDiscount());
                    } catch (Exception e) {
                        logger.error("分佣模板的推荐优惠比例解析失败", e);
                    }
                    //折扣率
                    Double discountRate = 1.0;
                    if (discounts.containsKey("level" + distributor.getLevel())) {
                        discountRate = Double.parseDouble(discounts.get("level" + distributor.getLevel()).toString()) * 0.01;//获得折扣率
                    }
                    orderGood.setGoodsPrice(new BigDecimal(orderGood.getGoodsPrice() * discountRate).setScale(0, RoundingMode.HALF_UP).intValue());
                }
            }
        });
    }


    /* *//**
     * 门店助手和门店后台下直购单之前的查询
     * @param req
     * @return
     *//*
    public ReturnDto beforeOrderDirects(BeforeCreateOrderDirectReq req){
        ReturnDto dto = new ReturnDto();
        BeforeOrderResponse response = new BeforeOrderResponse();
        try {
            //订单实际支付金额
            int orderRealPrice = 0;

            //查询购物车里面的商品信息
            List<GoodsInfo> goodsInfoInfos = getGoodsInfos(req.getSiteId(),req.getOrderGoods());

            //计算商品价格
            int goodsTotalPrice = getGoodsTotalPrice(goodsInfoInfos,req.getOrderGoods());

            //直购订单暂时没有优惠券
            //response.setCouponCount(0);
            //优惠券抵扣金额
            response.setCouponDeductionPrice(0);

            //查询用户现有可使用积分
            int userIntegral = integralMax(req.getSiteId(),goodsTotalPrice,req.getMobile());
            response.setIntegral(userIntegral);
            //积分抵扣金额，需调用积分模块的查询接口
            int integralDeductionPrice = calculateIntegralDiscount(req.getSiteId(),goodsTotalPrice,req.getMobile(),Integer.parseInt(req.getIntegralUse()));
            response.setIntegralDeductionPrice(integralDeductionPrice);
            //药房直购订单的运费统一为0
            response.setOrderFreight(0);
            //订单原始价格
            response.setOrderOriginalPrice(goodsTotalPrice);
            //订单实际支付金额=订单原始总金额 - 积分抵扣金额
            //订单实际支付金额,本次查询不涉及到优惠券抵扣金额，并且没有运费
            //用户在页面选择使用优惠券时，由优惠券提供接口来变更订单的实际支付金额
            response.setOrderRealPrice(goodsTotalPrice - integralDeductionPrice);
            dto.setCode("0000");
            dto.setMessage("success");
            dto.setValue(JacksonUtils.obj2json(response));
        } catch (Exception e){
            logger.error("查询结算信息错误：",e);
            dto.setCode("9999");
            dto.setMessage("failed");
        }
        return dto;
    }*/


    /**
     * 计算订单的积分抵扣金额
     *
     * @param siteId      商户ID
     * @param orderAmount 订单金额
     * @param buyerId     用户唯一ID
     * @param useNum      用户该笔订单使用了多少积分
     * @return
     */
    public int calculateIntegralDiscount(Integer siteId, Integer orderAmount, String buyerId, Integer useNum) {
        int integralDiscountPrice = 0;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("siteId", siteId);
        params.put("orderAmount", orderAmount);
        params.put("buyerId", buyerId);
        params.put("useNum", useNum);
        JSONObject jsonObject = integralService.integralCalcMoney(params);
        String status = jsonObject.getString("status");
        if ("success".equals(status)) {
            integralDiscountPrice = Integer.parseInt(jsonObject.getString("useMoney"));
        }
        return integralDiscountPrice;
    }


    /**
     * 查询订单最多可使用的积分
     *
     * @param siteId      商户ID
     * @param orderAmount 订单金额
     * @param memberId    用户唯一ID
     * @return
     */
    public int integralMax(Integer siteId, Integer orderAmount, String memberId) {
        int userIntegral = 0;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("siteId", siteId);
        params.put("orderAmount", orderAmount);
        //params.put("memberId",memberId);
        params.put("buyerId", memberId);
        JSONObject jsonObject = integralService.integralMaxDiff(params);
        String status = jsonObject.getString("status");
        if ("success".equals(status)) {
            userIntegral = Integer.parseInt(jsonObject.getString("maxDiff"));
        }
        return userIntegral;
    }

    /**
     * 计算优惠券的抵扣金额,以及优惠后的订单金额
     *
     * @param req
     * @param postFee        邮费
     * @param orderFee       实付金额 商品金额-积分+邮费
     * @param areaId         地区省份编码(code)
     * @param goodsInfoInfos 购物车里面的商品信息
     * @return
     */
    public UseCouponReturnParams couponDiscountPrice(BeforeCreateOrderReq req, int postFee, int orderFee, int areaId, List<GoodsInfo> goodsInfoInfos) {

        OrderMessageParams orderMessageParams = new OrderMessageParams();
        orderMessageParams.setSiteId(req.getSiteId());
        orderMessageParams.setStoreId(req.getStoresId());
        orderMessageParams.setUserId(req.getBuyerId());
        orderMessageParams.setAreaId(areaId);
        orderMessageParams.setCouponId(req.getCouponId());
        orderMessageParams.setPostFee(postFee);
        orderMessageParams.setOrderFee(orderFee);
        List<OrderGoods> orderGoodsList = req.getOrderGoods();
        List<Map<String, Integer>> goodsList = new ArrayList<Map<String, Integer>>();
        Map<String, Integer> goodsMap = null;
        for (int i = 0; i < goodsInfoInfos.size(); i++) {
            for (int j = 0; j < orderGoodsList.size(); j++) {
                GoodsInfo goodsInfo = goodsInfoInfos.get(i);
                OrderGoods orderGoods = orderGoodsList.get(j);
                if (goodsInfo.getGoodsId() == orderGoods.getGoodsId()) {
                    goodsMap = new HashMap<String, Integer>();
                    goodsMap.put("goodsId", orderGoods.getGoodsId());
                    goodsMap.put("num", orderGoods.getGoodsNum());
                    goodsMap.put("goodsPrice", goodsInfo.getShopPrice());
                    goodsList.add(goodsMap);
                }
            }
        }
        orderMessageParams.setGoodsInfo(goodsList);
        ReturnDto dto = couponProcessService.accountCoupon(orderMessageParams);
        UseCouponReturnParams returnParams = (UseCouponReturnParams) dto.getValue();
        return returnParams;
    }

    /**
     * 判断是否存在分销商品
     *
     * @param req
     * @return
     */
    public Boolean checkDistributeGoodsExists(BeforeCreateOrderReq req) {
        List<GoodsDistribute> distributeGoods = new ArrayList();
        req.getOrderGoods().stream().forEach(orderGood -> distributeGoods.add(this.goodsDistributeMapper.selectByGoodsIdAndSiteId(req.getSiteId(), orderGood.getGoodsId())));
        return distributeGoods.stream().anyMatch(goodsDistribute -> 0 != goodsDistribute.getYbGoodsId());
    }

    /**
     * 计算商品总价格
     * 商品个数 * 商品单价(如果有折扣价，就用商品的折扣价)
     *
     * @param goodsInfoInfos
     * @param orderGoods
     * @return
     */
    public int getGoodsTotalPrice(BeforeCreateOrderReq req, List<GoodsInfo> goodsInfoInfos, List<OrderGoods> orderGoods, DistributeResponse response, Integer has_erp_price) {
        logger.info("goodsInfoInfos:{},orderGoods:{},has_erp_price:{}", goodsInfoInfos, orderGoods, has_erp_price);
        int goodsTotalPrice = 0;//分销商品折扣之后的商品总额
        int distributePrice = 0;//分销商品总额
        int distributeDiscountPrice = 0;//分销商品折扣的金额
        Distributor distributor = this.checkWhetherDistributor(req.getMobile(), req.getSiteId());//判断是否是分销商
        //

        for (GoodsInfo goodsInfo : goodsInfoInfos) {
            logger.info("DiscountPrice:{},ErpPrice:{}", goodsInfo.getDiscountPrice(), goodsInfo.getErpPrice());
            if (1 == has_erp_price && goodsInfo.getErpPrice() != -1 && goodsInfo.getErpPrice() != 0) {
                goodsInfo.setDiscountPrice(goodsInfo.getErpPrice());
                goodsInfo.setShopPrice(goodsInfo.getErpPrice());
            }
            /*if (!StringUtil.isEmpty(req.getTradesSource())&&req.getTradesSource()==130&&!StringUtil.isEmpty(req.getStoresId())) {
                Map<String, Integer> priceMap = distributeOrderMapper.getGoodsInfoByPrice(req.getSiteId(), req.getStoresId()+"", goodsInfo.getGoodsId());
                if (!StringUtil.isEmpty(priceMap)) {
                    goodsInfo.setShopPrice(new Integer(String.valueOf(priceMap.get("goods_price"))));
                    goodsInfo.setDiscountPrice(new Integer(priceMap.get("discount_price").intValue()));
                }
            }*/
            //APP下单不走分销
            if (!StringUtil.isEmpty(req.getTradesSource()) && req.getTradesSource() == 130) {
                break;
            }
            for (OrderGoods orderGoodss : orderGoods) {
                if (goodsInfo.getGoodsId() == orderGoodss.getGoodsId()) {
                    //-----------------before--------------yeah--------------------------
                    GoodsDistribute goodsDistribute = this.goodsDistributeMapper.selectByGoodsIdAndSiteId(req.getSiteId(), goodsInfo.getGoodsId());//判断是否是分销商品
                    RewardTemplate rewardTemplate = new RewardTemplate();//获取对应的奖励模板
                    if (null != goodsDistribute) {
                        rewardTemplate = this.rewardTemplateMapper.selectByTemplateId(goodsDistribute.getDistributionTemplate());
                    }
                    logger.info("========fenxiao 分销商品信息:{}, 商品信息:{}", goodsDistribute, goodsInfo);
                    if (null == distributor || distributor.getStatus() != 1) {//不是分销商或分销商状态不是激活状态
                        if (null == goodsDistribute || 0 == goodsDistribute.getDistributionTemplate() || null == rewardTemplate) { //分销商品设置为非分销商品时 将yb_goods_id设置为0
                            //不是分销商 不是分销商品直接计算
                            goodsTotalPrice += orderGoodss.getGoodsNum() * (goodsInfo.getDiscountPrice() == 0 ? goodsInfo.getShopPrice() : goodsInfo.getDiscountPrice());
                        } else {
                            //不是分销商 是分销商品
                            goodsTotalPrice += orderGoodss.getGoodsNum() * (goodsInfo.getDiscountPrice() == 0 ? goodsInfo.getShopPrice() : goodsInfo.getDiscountPrice());
                            //分销商品总金额（不计算分销商折扣金额）
                            distributePrice += orderGoodss.getGoodsNum() * (goodsInfo.getDiscountPrice() == 0 ? new BigDecimal(goodsInfo.getShopPrice()).setScale(0, RoundingMode.HALF_UP).intValue() : new BigDecimal(goodsInfo.getDiscountPrice()).setScale(0, RoundingMode.HALF_UP).intValue());
                            req.setDistributeGoods(true);
                        }
                    } else {
                        //是分销商 判断是否是分销商品
//                        if (null == goodsDistribute || 0 == goodsDistribute.getYbGoodsId()) { //分销商品设置为非分销商品时 将yb_goods_id设置为0
                        if (null == goodsDistribute || goodsDistribute.getDistributionTemplate() <= 0 || null == rewardTemplate) { //分销商品设置为非分销商品时 将yb_goods_id设置为0
                            System.out.println("不是分销商品直接计算");
                            //不是分销商品直接计算
                            goodsTotalPrice += orderGoodss.getGoodsNum() * (goodsInfo.getDiscountPrice() == 0 ? goodsInfo.getShopPrice() : goodsInfo.getDiscountPrice());
                        } else {
                            System.out.println("分销商品计算");
                            //是分销商且是分销商品
                            if (null != rewardTemplate && StringUtil.isNotBlank(rewardTemplate.getDiscount())) {
                                //{"level1":"90","level2":"80","level3":"70","level4":"60","level5":"50"}
                                req.setDistributeGoods(true);
                                Map<String, Object> discounts = new TreeMap<>();
                                try {
                                    discounts = JacksonUtils.json2map(rewardTemplate.getDiscount());
                                    //过滤大于当前等级的消费折扣
                                    Iterator<String> iter = discounts.keySet().iterator();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        if (Integer.valueOf(key.substring(5)) > distributor.getLevel()) {
                                            iter.remove();
                                            discounts.remove(key);
                                        }
                                    }
                                    double max = 100.0;
                                    System.out.println(discounts.values().stream().filter(x -> Integer.valueOf(x.toString()) > 0).count());
                                    long totalLevel = discounts.values().stream().filter(x -> Integer.valueOf(x.toString()) > 0).count();
                                    Stream maxLevel = discounts.values().stream().filter(x -> Integer.valueOf(x.toString()) > 0);
                                    List tmpList = (List) maxLevel.collect(Collectors.toList());
                                    if (tmpList.size() > 0) {
                                        max = discounts.values().stream().filter(x -> Integer.valueOf(x.toString()) > 0).mapToDouble(x -> Integer.valueOf(x.toString())).summaryStatistics().getMin();
                                    }

                                    //折扣率
                                    Double discountRate = 1.0; //初始化折扣为1：无折扣
                                    DecimalFormat format = new DecimalFormat(".00");
                                    if (discounts.containsKey("level" + distributor.getLevel()) && Integer.valueOf(discounts.get("level" + distributor.getLevel()).toString()) > 0) {
                                        discountRate = Double.parseDouble(discounts.get("level" + distributor.getLevel()).toString()) * 0.01;//获得折扣率
                                        String rate = format.format(discountRate);
                                        discountRate = Double.parseDouble(rate);

                                    } else if ((int) totalLevel < distributor.getLevel()) {
                                        discountRate = max * 0.01;//获得折扣率
                                    }
                                    //折扣金额
                                    goodsTotalPrice += orderGoodss.getGoodsNum() * (goodsInfo.getDiscountPrice() == 0 ? new BigDecimal(goodsInfo.getShopPrice() * discountRate).setScale(0, RoundingMode.HALF_UP).intValue() : new BigDecimal(goodsInfo.getDiscountPrice() * discountRate).setScale(0, RoundingMode.HALF_UP).intValue());
                                    //分销商品总金额（不计算分销商折扣金额）
                                    distributePrice += orderGoodss.getGoodsNum() * (goodsInfo.getDiscountPrice() == 0 ? new BigDecimal(goodsInfo.getShopPrice()).setScale(0, RoundingMode.HALF_UP).intValue() : new BigDecimal(goodsInfo.getDiscountPrice()).setScale(0, RoundingMode.HALF_UP).intValue());
                                    //分销商品折扣的金额
//                                    distributeDiscountPrice += orderGoodss.getGoodsNum() * ((int) goodsInfo.getDiscountPrice() == 0 ? new BigDecimal(goodsInfo.getShopPrice() * (1 - discountRate)).setScale(0, RoundingMode.HALF_UP).intValue() : new BigDecimal(goodsInfo.getDiscountPrice() * (1 - discountRate)).setScale(0, RoundingMode.HALF_UP).intValue());

                                    distributeDiscountPrice += orderGoodss.getGoodsNum() * ((int) goodsInfo.getDiscountPrice() == 0 ? goodsInfo.getShopPrice() * (1 - discountRate) : goodsInfo.getDiscountPrice() * (1 - discountRate));
                                    distributeDiscountPrice = new BigDecimal(distributeDiscountPrice).setScale(0, RoundingMode.HALF_UP).intValue();

                                } catch (Exception e) {
                                    logger.error("分佣模板的推荐优惠比例解析失败", e);
                                }
                            }
                        }
                    }
                    //-----------------end----------------yeah------------------------
                }
            }
        }
        response.setDistributeDiscountPrice(distributeDiscountPrice);
        logger.info("========fenxiao 分销商品总价:{}", distributePrice);
        response.setDistributePrice(distributePrice);

        return goodsTotalPrice;
    }

    public int getGoodsTotalPrice(BeforeCreateOrderReq req, List<GoodsInfo> goodsInfoInfos, List<OrderGoods> orderGoods, DistributeResponse response, Integer has_erp_price, Integer tradesSource) {
        logger.info("goodsInfoInfos:{},orderGoods:{},has_erp_price:{}", goodsInfoInfos, orderGoods, has_erp_price);
        int goodsTotalPrice = 0;
        for (GoodsInfo goodsInfo : goodsInfoInfos) {
            logger.info("DiscountPrice:{},ErpPrice:{}", goodsInfo.getDiscountPrice(), goodsInfo.getErpPrice());
            if (1 == has_erp_price && goodsInfo.getErpPrice() != -1 && goodsInfo.getErpPrice() != 0) {
                goodsInfo.setDiscountPrice(goodsInfo.getErpPrice());
                goodsInfo.setShopPrice(goodsInfo.getErpPrice());
            }
            /*if (!StringUtil.isEmpty(tradesSource)&&tradesSource==130&&!StringUtil.isEmpty(req.getStoresId())) {
                Map<String, Integer> priceMap = distributeOrderMapper.getGoodsInfoByPrice(req.getSiteId(), req.getStoresId()+"", goodsInfo.getGoodsId());
                if (!StringUtil.isEmpty(priceMap)) {
                    goodsInfo.setShopPrice(new Integer(String.valueOf(priceMap.get("goods_price"))));
                    goodsInfo.setDiscountPrice(new Integer(priceMap.get("discount_price").intValue()));
                }
            }*/
            for (OrderGoods orderGoodss : orderGoods) {
                if (goodsInfo.getGoodsId() == orderGoodss.getGoodsId()) {
                    //goodsTotalPrice += orderGoodss.getGoodsNum() * (orderGoodss.getGoodsPrice()== 0?goodsInfo.getShopPrice():orderGoodss.getGoodsPrice());
                    goodsTotalPrice += orderGoodss.getGoodsNum() * (goodsInfo.getDiscountPrice() == 0 ? goodsInfo.getShopPrice() : goodsInfo.getDiscountPrice());
                }
            }
        }
        return goodsTotalPrice;
    }

    /**
     * 获取用于预下单提示语---提示用户还差多少钱可以达到分销商的下一个等级
     *
     * @param req
     * @param distributeMoney 购买的分销商品的总价格
     * @return 示例  result{
     * status: 0--招募书不存在 1--已经达到最高等级 2--存在比当前等级高的等级，正常提醒 示例："还差 money 钱就可以成为 level 等级的分销商"
     * [level: 5]   差多少钱可以达到的等级(仅status=2时存在)
     * [money: 12464]   差的钱(仅status=2时存在)
     * }
     */
    public String getDistributeTip(BeforeCreateOrderReq req, Integer distributeMoney) {
        Map result = new HashMap();
        Map rules = new TreeMap();//招募规则d_recruit
        Distributor distributor = this.checkWhetherDistributor(req.getMobile(), req.getSiteId());//判断当前用户是否是分销商
        List<Recruit> recruitList = this.recruitMapper.getRecruitListByOwner(String.valueOf(req.getSiteId()));
        if (CollectionUtils.isNotEmpty(recruitList)) {
            Recruit recruit = recruitList.get(0);
            if (StringUtil.isNotBlank(recruit.getRule())) {
                try {
                    rules = JacksonUtils.json2map(recruit.getRule());
                } catch (Exception e) {
                    logger.error("解析招募书规则失败", e);
                    return null;
                }
            }
        } else {
            result.put("status", 0);
            return JacksonUtils.mapToJson(result);
        }

        logger.info("==========fenxiao:{}, rules:{}", distributor, rules);
        if (null != distributor) {
            //已经是分销商
            Byte currentLevel = distributor.getLevel();//现在的等级
            Integer currentLevelPlus = (Integer) rules.get("level" + (currentLevel + 1));
            if (null == currentLevelPlus || 0 == currentLevelPlus) {
                //判断规则中比现有等级再高一级的等级是否存在
                // 不存在则已经达到最高级别
                result.put("status", 1);
                return JacksonUtils.mapToJson(result);
            } else {
                result.put("status", 2);
                final int MAX_LEVEL = 5;
                int setLevel = 0;
                int maxLevelMoney = rules.values().stream().mapToInt(x -> Double.valueOf(x.toString()).intValue()).summaryStatistics().getMax();
                System.out.println("maxLevelMoney");
                System.out.println(maxLevelMoney);
                currentLevel = Optional.ofNullable(currentLevel).orElse((byte) 1);
                for (int i = currentLevel + 1; i <= MAX_LEVEL; i++) {
                    final String key = "level" + i;
                    if (rules.containsKey(key)) {
                        //int value = Integer.parseInt(String.valueOf(Optional.ofNullable(rules.get(key)).orElse("0")));
                        int value = 0;
                        if (!StringUtil.isEmpty(rules.get(key))) {
                            value = new BigDecimal(rules.get(key) + "").intValue();
                        }
                        if (value > 0 && value > distributeMoney) {
                            result.put("level", i);
                            result.put("money", value - distributeMoney);
                            break;
                        } else if (value == maxLevelMoney) {
                            result.put("level", i);
                        }
                    }
                }
            }
        } else {
            result.put("status", 2);
            //还没有成为分销商
            this.recursiveCall(result, rules, 1, distributeMoney);
        }

        return JacksonUtils.mapToJson(result);
    }

    /**
     * 递归方法设置非分销商用户的预下单提示语,提示当前用户还需购买多少金额的分销商品即可成相应等级的分销商
     *
     * @param result          返回给前台的结果
     * @param rules           招募规则
     * @param a               当前规则等级
     * @param distributeMoney 购买的分销商品总金额
     */
    public void recursiveCall(Map result, Map rules, int a, Integer distributeMoney) {
        Integer moneyNeed = (Integer) rules.get("level" + a);
        Integer moneyNeedPlus = (Integer) rules.get("level" + (a + 1));
        if (moneyNeed != 0) {
            if (null != moneyNeedPlus && 0 != moneyNeedPlus) {
                //上一个等级存在
                if (moneyNeedPlus > distributeMoney && distributeMoney >= moneyNeed) {
                    //购买的分销商品的总额已经大于此等级需要的金额
                    result.put("level", a + 1);
                    result.put("money", moneyNeedPlus - distributeMoney);
                    return;
                } else if (moneyNeedPlus <= distributeMoney) {
                    //购买的分销商品的总额已经大于上一个等级需要的金额
                    this.recursiveCall(result, rules, a + 1, distributeMoney);
                } else {
                    //购买的分销商品的总额小于此等级需要的金额
                    if (a - 1 == 0) {
                        //此等级已经是最低等级
                        result.put("level", 1);
                        result.put("money", moneyNeed - distributeMoney);
                        return;
                    } else {
                        this.recursiveCall(result, rules, a - 1, distributeMoney);
                    }
                }

            } else {//上一个等级不存在 或者 未设置
                if (distributeMoney >= moneyNeed) {
                    //购买的分销商品的总额已经大于此等级需要的金额
                    result.put("status", 2);
                    result.put("level", a);
                    result.put("money", -1);
                    return;
                } else {
                    //购买的分销商品的总额小于此等级需要的金额
                    if (a - 1 == 0) {
                        //此等级已经是最低等级
                        result.put("level", 1);
                        result.put("money", moneyNeed - distributeMoney);
                        return;
                    } else {
                        this.recursiveCall(result, rules, a - 1, distributeMoney);
                    }
                }
            }
        } else {
            this.recursiveCall(result, rules, a - 1, distributeMoney);
        }
    }

    /**
     * 判断是否是分销商
     *
     * @param mobile,siteId
     * @return
     */
    public Distributor checkWhetherDistributor(String mobile, Integer siteId) {
        Member member = this.memberMapper.selectByMobileAndSiteId(mobile, siteId);
        if (StringUtil.isEmpty(member) || StringUtil.isEmpty(member.getBuyerId())) {
            return null;
        }
        Distributor distributor = this.distributorMapper.selectByUid(member.getBuyerId(), siteId);//b_member的buyer_id对应yb_member的member_id
        if (null == distributor) {
            return null;
        }
        return distributor;
    }

    /**
     * 获取有商品的门店信息
     *
     * @param site_id
     * @return
     */
    public List<Store> getStore(int site_id, List<GoodsInfo> goodsInfoInfos, String cityId, List<String> storesA) {

        //获取连锁品牌下所有可用的门店信息(包括 150=送货上门和160=门店自提的门店)
        logger.info("可以分单的门店：site_id" + site_id);
        String strmerchant = "";
        try {
            Map strmerchantMap = merchantExtService.selectassign(site_id);
            strmerchant = strmerchantMap.get("storeIds") + "";
            logger.info("可以分单的门店：" + strmerchant);

            //预先缓存商户的门店
            getStoreCype(site_id, goodsInfoInfos, strmerchant);
        } catch (Exception e) {
            logger.debug("分单权限异常" + e);
        }


        /*if ("9999".equals(strmerchant)) {
            return null;
        }*/
        List<Store> storesold = ordersMapper.getStoresBySiteId(site_id, StringUtil.isEmpty(cityId) ? null : Integer.parseInt(cityId));
        //distributeOrderMapper.getStores(site_id);
        List<Store> stores = new ArrayList<Store>();
        if ("".equals(strmerchant)) {
            //stores = storesold;
        } else {
            for (Store str : storesold) {
                if (("," + strmerchant + ",").indexOf("," + str.getId() + ",") > -1) {
                    stores.add(str);
                }
            }
        }
        //对接ERP查询库存，目前就100166这一家对接了ERP
//        if (site_id == 100166 || site_id == 100190||site_id==100180) {

        //因为查询ERP库存，需要goods_code(商品编码)，而不是goods_id(商品ID),所以需要将商品ID转换成商品编码
        //Map<String, String> goodsCodeMap = getGoodsCode(goodsInfoInfos);

        //把商品ID和门店ID传入给ERP，得到该商品在该门店是否有库存,商品ID和门店ID都可以出入多个以英文逗号","隔开
        /**
         * 对接ERP这块后续补上，现在默认都有库存
         */
        //String goodsno = goodsCodeMap.get("goodsno"); goodsno.substring(0, goodsno.length() - 1)
        List<Store> storeList = stores;
                //getBestStorageStore(site_id, stores, goodsInfoInfos);
        //getGoodsStockFromERP(store_ids, goods_ids);
        //根据活动设置有效门店
        if (!StringUtil.isEmpty(storesA)) {
            String storesStr = org.apache.commons.lang.StringUtils.join(storesA.toArray(), ",");
            logger.info("storesStr:{}", storesStr);
            storeList.stream()
                .filter(store -> ("," + storesStr + ",").indexOf("," + store.getId() + ",") > -1)
                .collect(Collectors.toList());
        }
        return storeList;
//        } else {//否则返回全部可用门店信息
//            return stores;
//        }
//        return getStoresList(site_id, stores, goodsInfoInfos);
    }

    /**
     * 获取有商品的门店信息缓存
     *
     * @param site_id
     * @return
     */
    @Async
    public void getStoreCype(int site_id, List<GoodsInfo> goodsInfoInfos, String strmerchant) {
        logger.info("可以分单的门店：site_id" + site_id);

        List<Store> storesold = ordersMapper.getStoresBySiteId(site_id, null);
        //distributeOrderMapper.getStores(site_id);
        List<Store> stores = new ArrayList<Store>();
        if ("".equals(strmerchant)) {
            stores = storesold;
        } else {
            for (Store str : storesold) {
                if (("," + strmerchant + ",").indexOf("," + str.getId() + ",") > -1) {
                    stores.add(str);
                }
            }
        }

        if (stores.size() > 5) {
            List<List<Store>> listAll = getSplitList(stores, 5);
            final ExecutorService exec = Executors.newFixedThreadPool(5);
            for (List<Store> liststo : listAll) {
                Callable call = new Callable() {
                    public String call() throws Exception {
                        getStoreByERP(site_id, goodsInfoInfos, liststo);
                        return "";
                    }
                };
                Future task = exec.submit(call);
            }
            //关闭线程池
            exec.shutdown();
        }


    }

    /**
     * @param list 要拆分的集合
     * @param size 指定的大小
     * @return
     */
    public static List<List<Store>> getSplitList(List<Store> list, int size) {
        List<List<Store>> returnList = new ArrayList<List<Store>>();
        int listSize = list.size();
        int num = listSize % size == 0 ? listSize / size : (listSize / size + 1);
        int start = 0;
        int end = 0;
        for (int i = 1; i <= num; i++) {
            start = (i - 1) * size;
            end = i * size > listSize ? listSize : i * size;
            System.out.println(start + ":" + end);
            returnList.add(list.subList(start, end));
        }
        return returnList;
    }

    public List<Store> getStoreByERP(int site_id, List<GoodsInfo> goodsInfoInfos, List<Store> stores) {


        List<Store> storeList = getBestStorageStore(site_id, stores, goodsInfoInfos);
        //getGoodsStockFromERP(store_ids, goods_ids);
        //需要对ERP返回的数据进行组装处理

        return storeList;
//        } else {//否则返回全部可用门店信息
//            return stores;
//        }
//        return getStoresList(site_id, stores, goodsInfoInfos);
    }

    /**
     * 通过erp获取门店是否有符合条件的库存
     *
     * @param siteId 商户号
     * @param stores 门店列表(经过距离排序的门店列表)
     * @return 符合条件的门店对象
     */
    public List<Store> getBestStorageStoreFromERP(Integer siteId, List<Store> stores, List<GoodsInfo> goodsInfoInfos) {
        List<Store> storesList = new ArrayList<Store>();
        boolean isException = true;
        for (Store store : stores) {
            try {
                boolean exists = false;
                Map<String, Integer> goodsCodeMap = new HashMap<String, Integer>();
                StringBuffer goodsno = new StringBuffer("");
                int goodsInfos = 0;
                for (GoodsInfo goodsInfo : goodsInfoInfos) {
                    String kcqty = stringRedisTemplate.opsForValue().get(siteId + "_" + store.getStoresNumber() + "_" + goodsInfo.getGoodsCode() + "_Kcqty");
                    logger.info("redis kcqty:{},门店编码:{},商品编号:{}", kcqty, store.getStoresNumber(), goodsInfo.getGoodsCode());
                    if (StringUtil.isEmpty(kcqty)) {
                        // || goodsInfo.getControlNum() > Double.parseDouble(kcqty)
                        goodsCodeMap.put(String.valueOf(goodsInfo.getGoodsCode()), goodsInfo.getControlNum());
                        goodsno.append(goodsInfo.getGoodsCode() + ",");
                        goodsInfos++;
                    } else {
                        if (Double.parseDouble(kcqty) >= goodsInfo.getControlNum()) {
                            exists = true;
                        } else {
                            exists = false;
                            continue;
                        }
                    }
                }
                if (!StringUtil.isEmpty(goodsno)) {
                    try {
                        //调用ERP接口获取商品库存
                        logger.info("storage_service_url路径[{}]", "siteId=" + siteId + "&goodsno=" + goodsno + "&uid=" + store.getStoresNumber());
                        Map<String, Object> responseText = storageService.getStorageBysiteId(String.valueOf(siteId), goodsno.toString(), store.getStoresNumber());
                        logger.info("ERP商品库存查询结果[{}]", responseText);
                        if (responseText.containsKey("info") && !StringUtil.isEmpty(responseText.get("info")) && !"[]".equals(responseText.get("info"))) {
                            List<Map<String, Object>> storageInfoList = (List<Map<String, Object>>) responseText.get("info");
                            //response.getInfo();
                            if (!StringUtil.isEmpty(storageInfoList) && storageInfoList.size() == goodsInfos) {
                                for (Map<String, Object> storageInfo : storageInfoList) {
                                    logger.info("库存比较-----------------商品编号：{},购买数量：{},门店编码:{},库存数量：{}", storageInfo.get("GOODSNO"), goodsCodeMap.get(storageInfo.get("GOODSNO")), store.getStoresNumber(), Double.parseDouble(storageInfo.get("kcqty") + ""));
                                    if (!StringUtil.isEmpty(storageInfo.get("kcqty")) && !"0.0".equals(storageInfo.get("kcqty"))) {
                                        if (Double.parseDouble(storageInfo.get("kcqty") + "") > 0 && Double.parseDouble(storageInfo.get("kcqty") + "") < 10) {
                                            stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("GOODSNO") + "_Kcqty", storageInfo.get("kcqty") + "", 1, TimeUnit.HOURS);
                                            logger.info("ERP商品库存数量[{}]1", storageInfo.get("kcqty"));
                                        } else if (Double.parseDouble(storageInfo.get("kcqty") + "") >= 10 && Double.parseDouble(storageInfo.get("kcqty") + "") < 50) {
                                            stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("GOODSNO") + "_Kcqty", storageInfo.get("kcqty") + "", 3, TimeUnit.HOURS);
                                            logger.info("ERP商品库存数量[{}]2", storageInfo.get("kcqty"));
                                        } else if (Double.parseDouble(storageInfo.get("kcqty") + "") >= 50) {
                                            stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("GOODSNO") + "_Kcqty", storageInfo.get("kcqty") + "", 1, TimeUnit.DAYS);
                                            logger.info("ERP商品库存数量[{}]3", storageInfo.get("kcqty"));
                                        }
                                        if (goodsCodeMap.get(storageInfo.get("GOODSNO")) > Double.parseDouble(storageInfo.get("kcqty") + "")) {
                                            exists = false;
                                            break;
                                        } else {
                                            exists = true;
                                            continue;
                                        }
                                    } else {
                                        exists = false;
                                        stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("GOODSNO") + "_Kcqty", "0", 1, TimeUnit.HOURS);
                                        break;
                                    }
                                }
                            } else {
                                exists = false;
                                String[] arr = goodsno.toString().split(",");
                                for (String str : arr) {
                                    stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + str + "_Kcqty", "0", 1, TimeUnit.HOURS);
                                }

                            }
                        } else {
                            exists = false;
                        }

                        if (StringUtil.isEmpty(responseText)) {
                            isException = false;
                        }
                    } catch (Exception e) {
                        exists = false;
                        logger.error("调用erp查询商品库存失败,商户号[{}],门店id[{}]，商品编码[{}],异常原因:[{}]", siteId, store.getStoresNumber(),
                            goodsno, e.getMessage());
                        isException = false;
                        continue;
//                                return stores;
                    }

                }
                //如果所有商品都有库存则符合条件，直接返回.
                if (exists) {
                    storesList.add(store);
                } else {
                    continue;
                }
            } catch (Exception e) {
                logger.error("调用erp查询商品库存失败,商户号[{}],门店id[{}],异常信息：[{}]", siteId, store.getStoresNumber(),
                    e);
                logger.error("异常Exception{}", e);
                isException = false;
                continue;
            }
        }
        logger.error("异常isException{}", isException);
        if (!StringUtil.isEmpty(storesList) && storesList.size() > 0) {
            return storesList;
        }
        //调用erp接口异常，就返回所有门店
//            if (!isException) {
//                return stores;
//            }
        return new ArrayList<Store>();
    }
    /**
     * 通过erp获取门店是否有符合条件的库存（单个门店）
     *
     * @param siteId 商户号
     * @param store 门店
     * @return 符合条件的门店对象
     */
    public Store getBestStorageStoreFromERPOne(Integer siteId, Store store, List<GoodsInfo> goodsInfoInfos) {
        Store storeOne = null;
        boolean isException = true;
        try {
            boolean exists = false;
            Map<String, Integer> goodsCodeMap = new HashMap<String, Integer>();
            StringBuffer goodsno = new StringBuffer("");
            int goodsInfos = 0;
            for (GoodsInfo goodsInfo : goodsInfoInfos) {
                String kcqty = stringRedisTemplate.opsForValue().get(siteId + "_" + store.getStoresNumber() + "_" + goodsInfo.getGoodsCode() + "_Kcqty");
                logger.info("redis kcqty:{},门店编码:{},商品编号:{}", kcqty, store.getStoresNumber(), goodsInfo.getGoodsCode());
                if (StringUtil.isEmpty(kcqty)) {
                    // || goodsInfo.getControlNum() > Double.parseDouble(kcqty)
                    goodsCodeMap.put(String.valueOf(goodsInfo.getGoodsCode()), goodsInfo.getControlNum());
                    goodsno.append(goodsInfo.getGoodsCode() + ",");
                    goodsInfos++;
                } else {
                    if (Double.parseDouble(kcqty) >= goodsInfo.getControlNum()) {
                        exists = true;
                    } else {
                        exists = false;
                        continue;
                    }
                }
            }
            if (!StringUtil.isEmpty(goodsno)) {
                try {
                    //调用ERP接口获取商品库存
                    logger.info("storage_service_url路径[{}]", "siteId=" + siteId + "&goodsno=" + goodsno + "&uid=" + store.getStoresNumber());
                    Map<String, Object> responseText = storageService.getStorageBysiteId(String.valueOf(siteId), goodsno.toString(), store.getStoresNumber());
                    logger.info("ERP商品库存查询结果[{}]", responseText);
                    if (responseText.containsKey("info") && !StringUtil.isEmpty(responseText.get("info")) && !"[]".equals(responseText.get("info"))) {
                        List<Map<String, Object>> storageInfoList = (List<Map<String, Object>>) responseText.get("info");
                        //response.getInfo();
                        if (!StringUtil.isEmpty(storageInfoList) && storageInfoList.size() == goodsInfos) {
                            for (Map<String, Object> storageInfo : storageInfoList) {
                                logger.info("库存比较-----------------商品编号：{},购买数量：{},门店编码:{},库存数量：{}", storageInfo.get("GOODSNO"), goodsCodeMap.get(storageInfo.get("GOODSNO")), store.getStoresNumber(), Double.parseDouble(storageInfo.get("kcqty") + ""));
                                if (!StringUtil.isEmpty(storageInfo.get("kcqty")) && !"0.0".equals(storageInfo.get("kcqty"))) {
                                    if (Double.parseDouble(storageInfo.get("kcqty") + "") > 0 && Double.parseDouble(storageInfo.get("kcqty") + "") < 10) {
                                        stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("GOODSNO") + "_Kcqty", storageInfo.get("kcqty") + "", 1, TimeUnit.HOURS);
                                        logger.info("ERP商品库存数量[{}]1", storageInfo.get("kcqty"));
                                    } else if (Double.parseDouble(storageInfo.get("kcqty") + "") >= 10 && Double.parseDouble(storageInfo.get("kcqty") + "") < 50) {
                                        stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("GOODSNO") + "_Kcqty", storageInfo.get("kcqty") + "", 3, TimeUnit.HOURS);
                                        logger.info("ERP商品库存数量[{}]2", storageInfo.get("kcqty"));
                                    } else if (Double.parseDouble(storageInfo.get("kcqty") + "") >= 50) {
                                        stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("GOODSNO") + "_Kcqty", storageInfo.get("kcqty") + "", 1, TimeUnit.DAYS);
                                        logger.info("ERP商品库存数量[{}]3", storageInfo.get("kcqty"));
                                    }
                                    if (goodsCodeMap.get(storageInfo.get("GOODSNO")) > Double.parseDouble(storageInfo.get("kcqty") + "")) {
                                        exists = false;
                                        break;
                                    } else {
                                        exists = true;
                                        continue;
                                    }
                                } else {
                                    exists = false;
                                    stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("GOODSNO") + "_Kcqty", "0", 1, TimeUnit.HOURS);
                                    break;
                                }
                            }
                        } else {
                            exists = false;
                            String[] arr = goodsno.toString().split(",");
                            for (String str : arr) {
                                stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + str + "_Kcqty", "0", 1, TimeUnit.HOURS);
                            }

                        }
                    } else {
                        exists = false;
                    }

                    if (StringUtil.isEmpty(responseText)) {
                        isException = false;
                    }
                } catch (Exception e) {
                    exists = false;
                    logger.error("调用erp查询商品库存失败,商户号[{}],门店id[{}]，商品编码[{}],异常原因:[{}]", siteId, store.getStoresNumber(),
                            goodsno, e.getMessage());
                    isException = false;
                }

            }
            //如果所有商品都有库存则符合条件，直接返回.
            if (exists) {
                storeOne=store;
            }
        } catch (Exception e) {
            logger.error("调用erp查询商品库存失败,商户号[{}],门店id[{}],异常信息：[{}]", siteId, store.getStoresNumber(),
                    e);
            logger.error("异常Exception{}", e);
            isException = false;
        }
        logger.error("异常isException{}", isException);
        if (!StringUtil.isEmpty(storeOne) ) {
            return storeOne;
        }
        return null;
    }
    /**
     * 通过erp获取门店是否有符合条件的库存(redis)
     *
     * @param siteId 商户号
     * @param stores 门店列表(经过距离排序的门店列表)
     * @return 符合条件的门店对象
     */
    public List<Store> getBestStorageStore(Integer siteId, List<Store> stores, List<GoodsInfo> goodsInfoInfos) {
        if (merchantExtTreatMapper.selectByMerchantId(siteId).getTrades_storage() == 1) {
            int count = bStoresStorageMapper.judgeCountBySiteId(siteId, 1);
            int status = merchantExtTreatMapper.selectByMerchantId(siteId).getHas_storage();
            logger.info("通过erp获取门店是否有符合条件的库存siteId:{},库存通道状态:{},导入的库存数量:{}。", siteId, status, count);
            if (count > 0 && status == 1) {
                return getStorageListByInPut(siteId, stores, goodsInfoInfos);
            } else {
                List<Store> storeList = storageService.getStoresHasStorageList(siteId, stores, goodsInfoInfos);
                        //getBestStorageStoreFromERP(siteId, stores, goodsInfoInfos);
                if (CollectionUtils.isEmpty(storeList)) {//没有门店可供分配
                    erpMerchantSettingService.insertFaultStatics(siteId, "[getBestStorageStore]", 2, 200, "分单到总部", "未找到商品对应的有库存的门店", "", 0);
                }
                return storeList;
            }
        } else {
            return stores;
        }

    }
    /**
     * 通过erp获取门店是否有符合条件的库存(redis)（单个门店）
     *
     * @param siteId 商户号
     * @param store 门店
     * @return 符合条件的门店对象
     */
    public Store getBestStorageStoreOne(Integer siteId, Store store, List<GoodsInfo> goodsInfoInfos) {
        if (merchantExtTreatMapper.selectByMerchantId(siteId).getTrades_storage() == 1) {
            int count = bStoresStorageMapper.judgeCountBySiteId(siteId, 1);
            int status = merchantExtTreatMapper.selectByMerchantId(siteId).getHas_storage();
            logger.info("通过erp获取门店是否有符合条件的库存siteId:{},库存通道状态:{},导入的库存数量:{}。", siteId, status, count);
            if (count > 0 && status == 1) {
                return getStorageListByInPutOne(siteId, store, goodsInfoInfos);
            } else {
                Store stores = getBestStorageStoreFromERPOne(siteId, store, goodsInfoInfos);
                if (StringUtil.isEmpty(stores)) {//没有门店可供分配
                    erpMerchantSettingService.insertFaultStatics(siteId, "[getBestStorageStore]", 2, 200, "分单到总部", "未找到商品对应的有库存的门店", "", 0);
                }
                return stores;
            }
        } else {
            return store;
        }

    }

    /**
     * 获取商品编码
     *
     * @param goodsInfos
     * @return
     */
    public Map<String, String> getGoodsCode(List<GoodsInfo> goodsInfos) {
        Map<String, String> goodsCodeMap = new HashMap<String, String>();
        StringBuffer goodsno = new StringBuffer("");
        for (GoodsInfo goodsInfo : goodsInfos) {
            goodsCodeMap.put(String.valueOf(goodsInfo.getGoodsId()), goodsInfo.getGoodsCode());
            goodsno.append(goodsInfo.getGoodsCode() + ",");
        }
        goodsCodeMap.put("goodsno", goodsno.toString());
        return goodsCodeMap;
    }

    /**
     * 获取商品总重量
     * 商品重量*商品个数
     *
     * @param goodsInfos
     * @param orderGoodss
     * @return
     */

    public int calculateGoodsTotalWeight(List<GoodsInfo> goodsInfos, List<OrderGoods> orderGoodss) {
        int goodsTotalWeight = 0;
        for (GoodsInfo goodsInfo : goodsInfos) {
            for (OrderGoods orderGoods : orderGoodss) {
                if (goodsInfo.getGoodsId() == orderGoods.getGoodsId()) {
                    goodsTotalWeight += (orderGoods.getGoodsNum() * goodsInfo.getGoodsWeight());
                }
            }
        }
        return goodsTotalWeight;
    }

    /**
     * 查询购物车里面的商品信息
     *
     * @param site_id    商户ID
     * @param orderGoods 购买商品
     * @param sotreId    送货上门门店ID
     * @return
     */
    public List<GoodsInfo> getGoodsInfos(Integer site_id, List<OrderGoods> orderGoods, String sotreId) {
        return getGoodsInfos(site_id, orderGoods, sotreId, null, null);
    }

    /**
     * 查询购物车里面的商品信息
     *
     * @param site_id    商户ID
     * @param orderGoods 购买商品
     * @param sotreId    送货上门门店ID
     * @param erpStoreId ERP价格门店ID
     * @return
     */
    public List<GoodsInfo> getGoodsInfos(Integer site_id, List<OrderGoods> orderGoods, String sotreId, Integer erpStoreId, Integer erpAreaCode) {
        List<Integer> goodIds = orderGoods.stream().map(OrderGoods::getGoodsId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodIds)) {
            List<GoodsInfo> goodsInfoList = distributeOrderMapper.getGoodsInfoByGoodIds(site_id, goodIds);
            Future<Map<Integer, BGoodsErp>> erpPriceFuture = null;
            if (erpStoreId != null && erpStoreId != 0) {
                erpPriceFuture = selectERPPriceAsync(site_id, goodIds, Integer.valueOf(erpStoreId), erpAreaCode);
            }
            List<GoodsInfo> goodsInfoListnew = new ArrayList<GoodsInfo>();
            for (GoodsInfo goodsInfo : goodsInfoList) {
                goodsInfo.setErpPrice(0);
                if (!StringUtil.isEmpty(sotreId)) {
                    Map<String, Integer> priceMap = distributeOrderMapper.getGoodsInfoByPrice(site_id, sotreId, goodsInfo.getGoodsId());
                    if (!StringUtil.isEmpty(priceMap) && !StringUtil.isEmpty(priceMap.get("goods_price")) && new Integer(String.valueOf(priceMap.get("goods_price"))) != 0) {
                        //goodsInfo.setShopPrice(new Integer(String.valueOf(priceMap.get("goods_price"))));
                        goodsInfo.setShopPrice(new Integer(String.valueOf(priceMap.get("goods_price"))));
                        goodsInfo.setErpPrice(-1);
                    }
                    if (!StringUtil.isEmpty(priceMap) && !StringUtil.isEmpty(priceMap.get("discount_price")) && priceMap.get("discount_price") != 0) {
                        //goodsInfo.setShopPrice(new Integer(String.valueOf(priceMap.get("goods_price"))));
                        goodsInfo.setShopPrice(new Integer(String.valueOf(priceMap.get("discount_price"))));
                        goodsInfo.setErpPrice(-1);
                    }

                }
                if (Objects.nonNull(erpStoreId)) {
                    // ERP价格优先级高于门店自主定价
                    try {
                        Map<Integer, BGoodsErp> erpPriceMap = erpPriceFuture.get(1, TimeUnit.SECONDS);
                        int goodsId = goodsInfo.getGoodsId();
                        if (erpPriceMap.containsKey(goodsId)) {
                            goodsInfo.setShopPrice(erpPriceMap.get(goodsId).getPrice());
                        }
                    } catch (Exception e) {
                        logger.error("获取ERP价格失败", e);
                    }
                }

                for (OrderGoods good : orderGoods) {
                    if (good.getGoodsId() == goodsInfo.getGoodsId()) {
                        goodsInfo.setControlNum(good.getGoodsNum());
                    }
                }
                goodsInfoListnew.add(goodsInfo);
            }
            return goodsInfoListnew;
        }

        return Collections.emptyList();
    }

    public Future<Map<Integer, BGoodsErp>> selectERPPriceAsync(final Integer siteId, final List<Integer> goodsIds, final Integer erpStoreId, final Integer erpAreaCode) {
        return CompletableFuture.supplyAsync(() -> {
            return erpPriceService.selectERPPrice(siteId, goodsIds, erpStoreId, erpAreaCode);
        });
    }


    /**
     * 区分购物车中分销商品列表信息和普通商品列表信息
     *
     * @param siteId
     * @param goodsInfoInfos
     * @return {
     * distributeGoodsInfo:[
     * goodsInfo[1] goodsInfo[2] goodsInfo[3]......
     * ]
     * normalGoodsInfo:[
     * goodsInfo[1] goodsInfo[2] goodsInfo[3]......
     * ]
     * }
     * @author yeah
     */
    public Map distinctGoods(int siteId, List<GoodsInfo> goodsInfoInfos) {
        Map map = new HashedMap();
        List<Integer> distributeGoodIds = new ArrayList<>();
        List<Integer> normalGoodIds = new ArrayList<>();
        for (GoodsInfo goodsInfo : goodsInfoInfos
            ) {
            GoodsDistribute goodsDistribute = this.goodsDistributeMapper.selectByGoodsIdAndSiteId(siteId, goodsInfo.getGoodsId());
            if (null != goodsDistribute) {
                distributeGoodIds.add(goodsInfo.getGoodsId());
            } else {
                normalGoodIds.add(goodsInfo.getGoodsId());
            }
        }
        if (CollectionUtils.isNotEmpty(distributeGoodIds)) {
            map.put("distributeGoodsInfo", distributeOrderMapper.getGoodsInfoByGoodIds(siteId, distributeGoodIds));
        }
        if (CollectionUtils.isNotEmpty(normalGoodIds)) {
            map.put("normalGoodsInfo", distributeOrderMapper.getGoodsInfoByGoodIds(siteId, normalGoodIds));
        }
        return map;
    }


    /**
     * 计算商家O2O运费，依次匹配商家设置的O2O运费规则，并且判断订单金额是否在免运费的金额范围内，如果在则运费为0
     * 否则返回匹配到的O2O运费
     * 如果
     *
     * @param o2ORules
     * @param minDistance     最短距离，单位:米
     * @param orderTotalPrice
     * @return
     */
    public int calculateO2OFreight(List<O2ORule> o2ORules, int minDistance, double orderTotalPrice) {

        for (O2ORule o2ORule : o2ORules) {
            int distance = (int) Double.parseDouble(o2ORule.getDistance()) * 1000;
            String[] priceScope = null;
            if (!StringUtil.isEmpty(o2ORule.getFree_scope())) {
                priceScope = o2ORule.getFree_scope().split("-");
            }
            double minPrice = 0.00;
            double maxPrice = 0.00;
            if (priceScope != null && priceScope.length == 2) {
                minPrice = Double.parseDouble(priceScope[0]);
                maxPrice = Double.parseDouble(priceScope[1]);
            }
            //距离在XX公里以内
            if (minDistance <= distance) {
                //如果不设置金额区间满减，则直接返回运费
                if (minPrice == 0 || maxPrice == 0) {
                    return (int) Double.parseDouble(o2ORule.getFix_price());
                } else {
                    //订单金额在免运费的金额范围内
                    if (orderTotalPrice <= maxPrice && orderTotalPrice >= minPrice) {
                        //免运费
                        return 0;
                    } else {//否则返回运费
                        return (int) Double.parseDouble(o2ORule.getFix_price());
                    }
                }
            }
        }
        //都没匹配到，则免运费
        return 0;
    }
    /**
     * 计算商家O2O运费，依次匹配商家设置的O2O运费规则，并且判断订单金额是否在免运费的金额范围内，如果在则运费为0
     * 否则返回匹配到的O2O运费(默认)【超过4-6公里加运费】
     * 如果
     *
     * @param o2ORules
     * @param minDistance     最短距离，单位:米
     * @param orderTotalPrice
     * @return
     */
    /*public Map calculateO2OFreightNew(List<O2ORule> o2ORules, int minDistance, double orderTotalPrice) {
        Map map=new HashMap();
        for (O2ORule o2ORule : o2ORules) {
            if("0".equals(o2ORule.getIndex())){
                int distance = (int) Double.parseDouble(o2ORule.getDistance()) * 1000;
                String[] priceScope = null;
                if (!StringUtil.isEmpty(o2ORule.getFree_scope())) {
                    priceScope = o2ORule.getFree_scope().split("-");
                }
                double minPrice = 0.00;
                double maxPrice = 0.00;
                if (priceScope != null && priceScope.length == 2) {
                    minPrice = Double.parseDouble(priceScope[0]);
                    maxPrice = Double.parseDouble(priceScope[1]);
                }
                //距离在XX公里以内
                if (minDistance <= distance) {
                    //如果不设置金额区间满减，则直接返回运费
                    if (minPrice == 0 || maxPrice == 0) {
                        map.put("freight",(int)Double.parseDouble(o2ORule.getFix_price()));
                    } else {
                        //订单金额在免运费的金额范围内
                        if (orderTotalPrice <= maxPrice && orderTotalPrice >= minPrice) {
                            //免运费
                            map.put("freight",0);
                        } else {//否则返回运费
                            map.put("freight",(int) Double.parseDouble(o2ORule.getFix_price()));

                        }
                    }
                }
            }
            if("1".equals(o2ORule.getIndex())){
                int freight=(int) Double.parseDouble(o2ORule.getFix_price())+(int)map.get("freight");
                map.put("freight",freight);
                map.put("freightCommission",(int) Double.parseDouble(o2ORule.getFix_price()));
            }
        }
        //都没匹配到，则免运费
        map.put("freight",0);
        return map;
    }*/

    /**
     * 判断最近距离是否超过商家设置的O2O最大距离
     *
     * @param o2ORules
     * @param minDistance 距离，单位：米
     * @return TRUE：超过；FALSE：不超过
     */
    public boolean checkMaxDistance(List<O2ORule> o2ORules, int minDistance) {
        for (O2ORule o2ORule : o2ORules) {
            if (minDistance < (int) (Double.parseDouble(o2ORule.getDistance()) * 1000)) {
                return false;
            }
        }
        return true;
    }

    public List<O2ORule> jsonO2ORulelist(String jsonArrayStr) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<O2ORule> list = objectMapper.readValue(jsonArrayStr, new TypeReference<List<O2ORule>>() {
        });
       /* List<O2ORule> result=new ArrayList<O2ORule>();
        for(Map<String,Object> map:list){
            O2ORule o2ORule=new O2ORule();
            o2ORule.setDistance(map.get("distance").toString());
            o2ORule.setFix_price(map.get("fix_price").toString());
            o2ORule.setFree_scope(map.get("free_scope").toString());
        }*/
        return list;
    }

    /**
     * 对应 tradesService.paySuccessCallback()
     * /*@Async
     * public void paySuccessCallback2(Trades trades) throws Exception {
     * //            schedulerTask.getBudgetDate(trades.getTradesId());
     * tradesService.sendPaySuccessMsg(trades);
     * JSONObject jsonObject = integralService.integralAddForBuy(new HashMap() {{
     * put("siteId", trades.getSiteId());
     * put("buyerId", trades.getBuyerId());
     * put("orderAmount", trades.getRealPay());
     * }});
     * tradesService.saveIntegral(trades, jsonObject);
     * <p>
     * //修改会员信息
     * BMember bShopMember = ordersMapper.getMemberByBuyerId(trades.getSiteId(), trades.getBuyerId());
     * bShopMember.setOrderFee(bShopMember.getOrderFee() + trades.getRealPay());
     * bShopMember.setOrderNum(bShopMember.getOrderNum() + 1);
     * ordersMapper.updateOrderMember(bShopMember);
     * <p>
     * storeOrderRemind(trades.getTradesId());//已付款订单且指定门店 进行订单提醒，微信端下单
     *//* 直购订单不送优惠券本期处理 zw*//*
        try {
            if (trades.getPostStyle().intValue() != CommonConstant.POST_STYLE_DIRECT_PURCHASE) {
                couponSendService.sendCouponByPay(String.valueOf(trades.getTradesId()));
            }
        } catch (Exception e) {
            logger.error("赠送优惠券失败，错误：" + e);
            e.printStackTrace();
        }
    }*/

    //买家付款成功后，要推送 新订单提醒 到门店助手APP 该订单对应门店下所有店员 ；
    // 若该已付款订单，并没有被指定某个门店，在商家后台指定门店时 再推送
    // {"site_id":"1001","order_id":"1000011448018893293","assign_store_id":"49","source_store_id":"120","self_take_store_id":"49","self_take_code":"1127205324","status":"120"}
    @Async
    public void storeOrderRemind(Long tradesId) {
        try {
            Trades trades = tradesMapper.getTradesByTradesId(tradesId);
            if (trades != null && trades.getAssignedStores() != null && trades.getTradesSource().intValue() == 120) {//微信端下单
                List<String> storeAdminIds = storeAdminMapper.getStoreAdminIdsByStore(trades.getSiteId(), trades.getAssignedStores());
                if (storeAdminIds != null && storeAdminIds.size() != 0) {
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("tradesId", trades.getTradesId());
                    messageMap.put("siteId", trades.getSiteId());
                    messageMap.put("storeId", trades.getAssignedStores());
                    String messageStr = JSON.toJSONString(messageMap);
                    geTuiPush.noticeOtherAppOrderRemind(storeAdminIds, messageStr, "orderRemind");//个推的clientId根据设备信息生成，短期内某一设备标识一个clientId,不能区分店员，过设置storeId让app区分

                    Map<String, Object> extraMap = new HashMap<>();
                    extraMap.put("site_id", trades.getSiteId());
                    extraMap.put("order_id", trades.getTradesId());
                    extraMap.put("assign_store_id", trades.getAssignedStores());
                    extraMap.put("source_store_id", trades.getTradesStore());
                    extraMap.put("self_take_store_id", trades.getSelfTakenStore());
                    extraMap.put("self_take_code", trades.getSelfTakenCode());
                    extraMap.put("status", trades.getTradesStatus());
                    String extraStr = JSON.toJSONString(extraMap);

                    List<ChOrderRemind> chOrderReminds = chOrderRemindMapper.getChOrderRemindByOrderId(trades.getTradesId());
                    if (chOrderReminds != null && chOrderReminds.size() != 0) {//商家指派门店
                        chOrderRemindMapper.updateByOrderId(trades.getTradesId(), trades.getPostStyle(), extraStr);
                    } else {
                        ChOrderRemind chOrderRemind = new ChOrderRemind();
                        chOrderRemind.setOrderId(String.valueOf(trades.getTradesId()));
                        chOrderRemind.setPostStyle(trades.getPostStyle());
                        chOrderRemind.setExtra(extraStr);
                        chOrderRemindMapper.insertSelective(chOrderRemind);
                    }
                }
            }

            //网站后台 订单提醒 :: 指派的门店,对应 b_stores.id，当assigned_stores为0时就是说明是总部发的货, 默认 null
            //这里 原来网站后台 1分钟轮询一次
            if (trades != null) {
                if (trades.getAssignedStores() != null && trades.getAssignedStores() != 0) {
                    stringRedisTemplate.opsForValue().set("orderRemind_Store_" + trades.getSiteId() + trades.getAssignedStores(), String.valueOf(trades.getTradesId()));
                    if (System.currentTimeMillis() - trades.getPayTime().getTime() < 60000) {//若付款超过一分钟，且已指定门店，不对商家提醒订单
                        stringRedisTemplate.opsForValue().set("orderRemind_Site_" + trades.getSiteId(), String.valueOf(trades.getTradesId()));
                    }
                } else {
                    stringRedisTemplate.opsForValue().set("orderRemind_Site_" + trades.getSiteId(), String.valueOf(trades.getTradesId()));
                }
                logger.info("设置商家订单提醒记录：orderRemind_Site_" + trades.getSiteId() + "：" + stringRedisTemplate.opsForValue().get("orderRemind_Site_" + trades.getSiteId()));
                logger.info("设置门店订单提醒记录：orderRemind_Store_" + trades.getSiteId() + trades.getAssignedStores() + "：" + stringRedisTemplate.opsForValue().get("orderRemind_Store_" + trades.getSiteId() + trades.getAssignedStores()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("订单[{}]，订单提醒记录失败{}", tradesId, e);
        }
    }

    /**
     * 区域总仓
     *
     * @param siteId
     * @return
     */
    public Integer getQYStore(Integer siteId, HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite) {
        String storesStr = merchantExtservice.selectidsBywarehouseFromMeta(siteId);
        String province = ordersMapper.getAreaId(homeDeliveryAndStoresInvite.getReceiverProvinceCode());
        List<Store> storeList = new ArrayList<Store>();
        if (!StringUtil.isEmpty(storesStr)) {
            for (String id : storesStr.split(",")) {
                Store store = ordersMapper.getStore(Integer.parseInt(id), siteId);
                if (!StringUtil.isEmpty(province) && !StringUtil.isEmpty(store) && province.equals(store.getProvince()) && store.getServiceSupport().indexOf("150") > -1) {

                    storeList.add(store);
                    //return store.getId();
                }

            }
        }
        //先把收货人地址调用高德地图API转换成坐标地址
        Coordinate coordinate = mapService.geoCoordinate(String.valueOf(homeDeliveryAndStoresInvite.getReceiverAddress()));
        if (!StringUtil.isEmpty(coordinate) && coordinate.getLat() != 0 && coordinate.getLng() != 0) {
            Map<Integer, Store> storeDistanceMap = getDistributeMap(storeList, coordinate);
            if (!StringUtil.isEmpty(storeDistanceMap)) {
                Set<Integer> set = storeDistanceMap.keySet();
                Object[] obj = set.toArray();
                Arrays.sort(obj);
                int minDistance = (int) obj[0];
                return storeDistanceMap.get(minDistance).getId();
            }
        }
        //设置临时处理门店
        String storesStrLin = storesService.selectidsBywarehouseFromMetaLin(siteId);
        if(!StringUtil.isEmpty(storesStrLin)){
            String[] storesLin=storesStrLin.split(",");
            int num=(int)(Math.random()*(storesLin.length));
            return Integer.parseInt(storesLin[num]);
        }
        return 0;
    }

    /*public Map getDistributeMap(List<Store> stores, Coordinate coordinate) {
        Map<Integer, Store> storeDistanceMap = new ConcurrentHashMap<>();
        if (!StringUtil.isEmpty(stores)) {
            //然后再调用高德地图API，计算两个坐标的距离，根据距离最近的来选择门店
            for (Store store : stores) {
                logger.info("********************store:{} lng:{}, lat:{}:", store.getName(), store.getGaodeLng(), store.getGaodeLat());
                if (StringUtils.isEmpty(store.getGaodeLng()) || StringUtils.isEmpty(store.getGaodeLat())) {
                    //忽略门店经纬度为空的门店
                    continue;
                }
                //用户到门店的距离,单位：米
                int distance = Integer.parseInt(mapService.geoDistance(coordinate, new Coordinate(Double.parseDouble(store.getGaodeLng()), Double.parseDouble(store.getGaodeLat()))));
                //调用高德地图计算查询过多，不支持查询，本地计算
                if (distance == 0) {
                    distance = Distribute.getDistance(Double.parseDouble(store.getGaodeLng()), Double.parseDouble(store.getGaodeLat()), coordinate.getLng(), coordinate.getLat());
                }
                //理论上不会有两个门店的距离一模一样
                storeDistanceMap.put(distance, store);
                logger.info("********************store:{} distance:{}:", store.getName(), distance);
            }
        }
        //return storeDistanceMap;
        return mapService.geoDistances(coordinate,stores);
    }*/
    //优惠路径规划（批量调用接口）
    public Map getDistributeMap(List<Store> stores, Coordinate coordinate) {
        return mapService.geoDistances(coordinate, stores);
    }

    public int calcIntegral(final BeforeCreateOrderReq req) {
        return integralService.calcIntegral(req.getSiteId(), req.getOrderGoods());
    }

    /**
     * 通过excel导入后的分单
     *
     * @return
     */
    public List<Store> getStorageListByInPut(Integer siteId, List<Store> stores, List<GoodsInfo> goodsInfoInfos) {
        List<Store> storesList = new ArrayList<Store>();
        for (Store store : stores) {
            try {
                boolean exists = false;
                Map<String, Integer> goodsCodeMap = new HashMap<String, Integer>();
                List<String> goodsnoList = new ArrayList<>();
                StringBuffer goodsno = new StringBuffer("");
                int goodsInfos = 0;
                for (GoodsInfo goodsInfo : goodsInfoInfos) {
                    String kcqty = stringRedisTemplate.opsForValue().get(siteId + "_" + store.getStoresNumber() + "_" + goodsInfo.getGoodsCode() + "_kcqty");
                    logger.info("redis kcqty:{},门店编码:{},商品编号:{}", kcqty, store.getStoresNumber(), goodsInfo.getGoodsCode());
                    if (StringUtil.isEmpty(kcqty)) {
                        /*|| goodsInfo.getControlNum() > Double.parseDouble(kcqty)*/
                        goodsCodeMap.put(String.valueOf(goodsInfo.getGoodsCode()), goodsInfo.getControlNum());
                        goodsnoList.add(goodsInfo.getGoodsCode());
                        goodsno.append(goodsInfo.getGoodsCode() + ",");
                        goodsInfos++;
                    } else {
                        if (Double.parseDouble(kcqty) >= goodsInfo.getControlNum()) {
                            exists = true;
                        }
                    }
                }
                if (!StringUtil.isEmpty(goodsnoList)) {
                    try {
                        //查询导入的库存接口获取商品库存
                        logger.info("storage_service_url路径[{}]", "siteId=" + siteId + "&goodsno=" + goodsno + "&uid=" + store.getStoresNumber());
                        Map<String, Object> storageMap = new HashMap<>();
                        storageMap.put("siteId", siteId);
                        storageMap.put("uid", store.getStoresNumber());
                        storageMap.put("gCode", goodsnoList);
                        List<Map<String, Object>> storageInfoList = bStoresStorageMapper.selectStorageByOrder(storageMap);
                        if (!StringUtil.isEmpty(storageInfoList)) {
                            for (Map<String, Object> storageInfo : storageInfoList) {
                                logger.info("库存管理-------------购物数量：{}，库存数量：{}", goodsCodeMap.get(storageInfo.get("goods_code")), Double.parseDouble(storageInfo.get("in_stock") + ""));
                                if (!StringUtil.isEmpty(storageInfo.get("in_stock")) && !"0.0".equals(storageInfo.get("in_stock"))) {
                                    if (Double.parseDouble(storageInfo.get("in_stock") + "") > 0 && Double.parseDouble(storageInfo.get("in_stock") + "") < 10) {
                                        stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("goods_code") + "_kcqty", storageInfo.get("in_stock") + "", 1, TimeUnit.HOURS);
                                        logger.info("ERP商品库存数量[{}]1", storageInfo.get("in_stock"));
                                    } else if (Double.parseDouble(storageInfo.get("in_stock") + "") >= 10 && Double.parseDouble(storageInfo.get("in_stock") + "") < 50) {
                                        stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("goods_code") + "_kcqty", storageInfo.get("in_stock") + "", 3, TimeUnit.HOURS);
                                        logger.info("ERP商品库存数量[{}]2", storageInfo.get("in_stock"));
                                    } else if (Double.parseDouble(storageInfo.get("in_stock") + "") >= 50) {
                                        stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("goods_code") + "_kcqty", storageInfo.get("in_stock") + "", 1, TimeUnit.DAYS);
                                        logger.info("ERP商品库存数量[{}]3", storageInfo.get("in_stock"));
                                    }
                                    if (goodsCodeMap.get(storageInfo.get("goods_code")) <= Double.parseDouble(storageInfo.get("in_stock") + "")) {
                                        exists = true;
                                    }
                                } else {
                                    exists = false;
                                    stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("goods_code") + "_kcqty", "0", 1, TimeUnit.HOURS);
                                    break;
                                }
                            }
                        } else {
                            exists = false;
                            String[] arr = goodsno.toString().split(",");
                            for (String str : arr) {
                                stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + str + "_kcqty", "0", 1, TimeUnit.HOURS);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("调用erp查询商品库存失败,商户号[{}],门店id[{}]，商品编码[{}],异常原因:[{}]", siteId, store.getStoresNumber(),
                            goodsno, e.getMessage());
                        continue;
                    }
                }
                //如果所有商品都有库存则符合条件，直接返回.
                if (exists) {
                    storesList.add(store);
                } else {
                    continue;
                }
            } catch (Exception e) {
                logger.error("异常Exception{}", e);

            }
        }
        if (!StringUtil.isEmpty(storesList) && storesList.size() > 0) {
            return storesList;
        }
        return new ArrayList<Store>();
    }
    /**
     * 通过excel导入后的分单(单个门店)
     *
     * @return
     */
    public Store getStorageListByInPutOne(Integer siteId, Store store, List<GoodsInfo> goodsInfoInfos) {
        try {
            boolean exists = false;
            Map<String, Integer> goodsCodeMap = new HashMap<String, Integer>();
            List<String> goodsnoList = new ArrayList<>();
            StringBuffer goodsno = new StringBuffer("");
            int goodsInfos = 0;
            for (GoodsInfo goodsInfo : goodsInfoInfos) {
                String kcqty = stringRedisTemplate.opsForValue().get(siteId + "_" + store.getStoresNumber() + "_" + goodsInfo.getGoodsCode() + "_kcqty");
                logger.info("redis kcqty:{},门店编码:{},商品编号:{}", kcqty, store.getStoresNumber(), goodsInfo.getGoodsCode());
                if (StringUtil.isEmpty(kcqty)) {
                        /*|| goodsInfo.getControlNum() > Double.parseDouble(kcqty)*/
                    goodsCodeMap.put(String.valueOf(goodsInfo.getGoodsCode()), goodsInfo.getControlNum());
                    goodsnoList.add(goodsInfo.getGoodsCode());
                    goodsno.append(goodsInfo.getGoodsCode() + ",");
                    goodsInfos++;
                } else {
                    if (Double.parseDouble(kcqty) >= goodsInfo.getControlNum()) {
                        exists = true;
                    }
                }
            }
            if (!StringUtil.isEmpty(goodsnoList)) {
                try {
                    //查询导入的库存接口获取商品库存
                    logger.info("storage_service_url路径[{}]", "siteId=" + siteId + "&goodsno=" + goodsno + "&uid=" + store.getStoresNumber());
                    Map<String, Object> storageMap = new HashMap<>();
                    storageMap.put("siteId", siteId);
                    storageMap.put("uid", store.getStoresNumber());
                    storageMap.put("gCode", goodsnoList);
                    List<Map<String, Object>> storageInfoList = bStoresStorageMapper.selectStorageByOrder(storageMap);
                    if (!StringUtil.isEmpty(storageInfoList)) {
                        for (Map<String, Object> storageInfo : storageInfoList) {
                            logger.info("库存管理-------------购物数量：{}，库存数量：{}", goodsCodeMap.get(storageInfo.get("goods_code")), Double.parseDouble(storageInfo.get("in_stock") + ""));
                            if (!StringUtil.isEmpty(storageInfo.get("in_stock")) && !"0.0".equals(storageInfo.get("in_stock"))) {
                                if (Double.parseDouble(storageInfo.get("in_stock") + "") > 0 && Double.parseDouble(storageInfo.get("in_stock") + "") < 10) {
                                    stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("goods_code") + "_kcqty", storageInfo.get("in_stock") + "", 1, TimeUnit.HOURS);
                                    logger.info("ERP商品库存数量[{}]1", storageInfo.get("in_stock"));
                                } else if (Double.parseDouble(storageInfo.get("in_stock") + "") >= 10 && Double.parseDouble(storageInfo.get("in_stock") + "") < 50) {
                                    stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("goods_code") + "_kcqty", storageInfo.get("in_stock") + "", 3, TimeUnit.HOURS);
                                    logger.info("ERP商品库存数量[{}]2", storageInfo.get("in_stock"));
                                } else if (Double.parseDouble(storageInfo.get("in_stock") + "") >= 50) {
                                    stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("goods_code") + "_kcqty", storageInfo.get("in_stock") + "", 1, TimeUnit.DAYS);
                                    logger.info("ERP商品库存数量[{}]3", storageInfo.get("in_stock"));
                                }
                                if (goodsCodeMap.get(storageInfo.get("goods_code")) <= Double.parseDouble(storageInfo.get("in_stock") + "")) {
                                    exists = true;
                                }
                            } else {
                                exists = false;
                                stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("goods_code") + "_kcqty", "0", 1, TimeUnit.HOURS);
                                break;
                            }
                        }
                    } else {
                        exists = false;
                        String[] arr = goodsno.toString().split(",");
                        for (String str : arr) {
                            stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + str + "_kcqty", "0", 1, TimeUnit.HOURS);
                        }
                    }
                } catch (Exception e) {
                    logger.error("调用erp查询商品库存失败,商户号[{}],门店id[{}]，商品编码[{}],异常原因:[{}]", siteId, store.getStoresNumber(),
                            goodsno, e.getMessage());

                }
            }
            //如果所有商品都有库存则符合条件，直接返回.
            if (exists) {
                return store;
            }
        } catch (Exception e) {
            logger.error("异常Exception{}", e);

        }
        return null;
    }
    /**
     * 查找送货上门订单的门店(只获取门店)最近
     *
     * @param stores
     * @param distribute
     * @return
     */
    public Store selectOrderStore(List<Store> stores, Distribute distribute) {
        DistributeResponse response = new DistributeResponse();
        //运费,单位：分
        int freight = 0;
        //先把收货人地址调用高德地图API转换成坐标地址
        Coordinate coordinate = mapService.geoCoordinate(String.valueOf(distribute.getUserDeliveryAddr()));

        logger.info("用户经纬度:{}，Addr：{}", coordinate, distribute.getUserDeliveryAddr());

        int minDistance = 0;
        long startMillis, endMillis;
        startMillis = System.currentTimeMillis();
        //然后再调用高德地图API，计算两个坐标的距离，根据距离最近的来选择门店
        Map<Integer, Store> storeDistanceMap = getDistributeMap(stores, coordinate);
        if (!StringUtil.isEmpty(storeDistanceMap)) {
            endMillis = System.currentTimeMillis();
            logger.info("foreach 处理分单计算最近门店耗时: {}, 门店数:{}", endMillis - startMillis, stores.size());
            //找出距离最近的门店(单位：米)
            Set<Integer> set = storeDistanceMap.keySet();
            Object[] obj = set.toArray();
            Arrays.sort(obj);
            minDistance = (int) obj[0];
        }

        return storeDistanceMap.get(minDistance);
    }

    /**
     * 根据坐标获取最近的门店信息
     *
     * @param siteId 商家id
     * @param lng    经度 Longitude	地理位置经度
     * @param lat    纬度Latitude	地理位置纬度
     * @return
     */
    public Store getStoreByDistribute(int siteId, double lng, double lat) {
        List<Store> stores = storesMapper.selectAllStoreByStatus(siteId, 1);
        Coordinate coordinate = new Coordinate(lng, lat);
        int minDistance = 0;
        if (!StringUtil.isEmpty(stores)) {
            //然后再调用高德地图API，计算两个坐标的距离，根据距离最近的来选择门店
            Map<Integer, Store> storeDistanceMap = getDistributeMap(stores, coordinate);
            if (!StringUtil.isEmpty(storeDistanceMap)) {
                //找出距离最近的门店(单位：米)
                Set<Integer> set = storeDistanceMap.keySet();
                Object[] obj = set.toArray();
                Arrays.sort(obj);
                minDistance = (int) obj[0];
                return storeDistanceMap.get(minDistance);
            }
        }
        return null;
    }

    private Param buildParamForConcession(List<GoodsInfo> goodsInfoInfos, BeforeCreateOrderReq req, int freight, DistributeResponse response) {
        Param param = new Param();

        // 站点
        param.setSiteId(checkNotZero(req.getSiteId()));

        // 会员id
        param.setMemberId(checkNotZero(req.getUserId()));

        // 配送信息
        setDeliveryMessage(param, req.getOrderType(), req, response);
        param.setFreight(freight);
        param.setReceiverCityCode(req.getReceiverCityCode());

        // 下单渠道
        setApplyChannel(req, param);

        // 商品信息
        List<GoodsData> goodsDataList = goodsInfoInfos.stream()
            .map(goodsInfo -> {
                GoodsData goodsData = new GoodsData();
                goodsData.setGoodsId(checkNotZero(goodsInfo.getGoodsId()));
                goodsData.setShopPrice(checkNotZero(goodsInfo.getShopPrice()));
                goodsData.setNum(checkNotZero(goodsInfo.getControlNum()));

                return goodsData;
            }).collect(Collectors.toList());
        param.setGoodsDataList(goodsDataList);

        // 优惠券信息
        setCouponToParam(req, param);

        // 拼团活动预下单参数获取
        setGroupPurchaseParam(req, param);

        // servletContext
        param.setServletContext(Preconditions.checkNotNull(servletContext));

        return param;
    }

    private void setApplyChannel(BeforeCreateOrderReq req, Param param) {
        Integer tradesSource = req.getTradesSource();
        switch (tradesSource) {
            case 110:
                param.setApplyChannel("102");
                break;

            case 120:
                param.setApplyChannel("103");
                break;

            case 130:
                param.setApplyChannel("101");
                break;

            case 140:
                param.setApplyChannel("105");
                break;

            case 160:
                param.setApplyChannel("104");
                break;

            default:
                throw new UnknownTypeException();
        }
    }

    /**
     * 获取拼团数据用于计算
     *
     * @param req
     * @param param
     */
    private void setGroupPurchaseParam(BeforeCreateOrderReq req, Param param) {
        GroupPurchase groupPurchase = req.getGroupPurchase();
        if (groupPurchase != null) {
            if (groupPurchase.getGoodsId() != null
                && groupPurchase.getProActivityId() != null
                && groupPurchase.getParentId() == null) {
                GroupPurchaseForBeforeOrder groupPurchaseForBeforeOrder = new GroupPurchaseForBeforeOrder();
                groupPurchaseForBeforeOrder.setGoodsId(groupPurchase.getGoodsId());
                groupPurchaseForBeforeOrder.setPromotionsActivityId(groupPurchase.getProActivityId());

                if (groupPurchase.getId() != null)
                    groupPurchaseForBeforeOrder.setGroupPurchaseParentId(groupPurchase.getId());

                param.setGroupPurchaseForBeforeOrder(groupPurchaseForBeforeOrder);
            } else {
                throw new ParamErrorException();
            }
        }
    }

    private void setCouponToParam(BeforeCreateOrderReq req, Param param) {
        if (req.getCouponId() == null) {
            param.setHasUseCoupon(false);
        } else {
            param.setHasUseCoupon(true);
            param.setCouponDetailId(req.getCouponId());
        }
    }

    /**
     * @param param     1：送货上门订单，2：门店自提订单和药房直购订单
     * @param orderType
     * @param req
     * @param response
     */
    private void setDeliveryMessage(Param param, String orderType, BeforeCreateOrderReq req, DistributeResponse response) {
        if ("1".equals(orderType)) {
            param.setOrderType("200");
            if (response.getStore() == null)
                param.setStoreId(0);
            else
                param.setStoreId(response.getStore().getId());

        } else if ("2".equals(orderType)) {
            param.setOrderType("100");
            param.setStoreId(req.getStoresId());

        } else if ("3".equals(orderType)) {
            param.setOrderType("300");
            param.setStoreId(req.getStoresId());
        } else {
            param.setOrderType("100");
            param.setStoreId(0);
        }
    }

    private void buildResultForResponse(BeforeCreateOrderReq req, DistributeResponse response, Optional<? extends BaseResult> concessionResult) {
        if (concessionResult.isPresent()) {
            Result result = (Result) concessionResult.get();

            // 计算了优惠券或者是活动
            if (result.isUseCoupon() || result.isUsePromotions()) {
                response.setEfficientPromotionsActivityId(result.getEfficientPromotionsActivityId());
                response.setMoneyResultForPromotionsList(result.getMoneyResultForPromotionsList());

                if (result.isUseCoupon() && !result.getCouponConcessionRemark().equals(3))
                    setCouponResultToResponse(req, response, result);

                if (result.isUsePromotions())
                    setPromotionsResultToResponse(req, response, result);

                // 设置赠品列表（包括赠品券和赠品活动）
                response.setGiftResultList(result.getGiftResults());
                if (response.isUseCoupon()
                    || response.getProRuleDeductionPrice() != 0
                    || (response.getGiftResultList() != null && response.getGiftResultList().size() != 0)) {

                    response.setConcessionDeductionPrice(response.getCouponDeductionPrice() + response.getProRuleDeductionPrice());
                    response.setConcessionResult(result);
                }
            }
        }

        Result result = response.getConcessionResult();
        if (result == null) {
            Result temp = new Result();
            temp.setEmpty(true);
            response.setConcessionResult(temp);
        }
    }

    private void clearSelectedCouponGift(DistributeResponse response) {

    }

    /**
     * 判断预下单和下单的计算结果是否相同
     *
     * @param reqResult
     * @param respResult
     * @return
     */
    private boolean checkCalculateIsSame(Result reqResult, Result respResult) {
        if (reqResult.isEmpty()) {
            return respResult.isEmpty();
        }

        return respResult.equals(reqResult);
    }


    /**
     * 检查顾客获取赠品是否符合下单核算
     *
     * @param giftsPickedByCustomer
     * @param concessionResult
     * @return
     */
    private boolean checkGiftsPickedByCustomer(List<GiftResult> giftsPickedByCustomer, Result concessionResult) {
        for (GiftResult giftResultByCustomer : giftsPickedByCustomer) {
            ConcessionDesc concessionDesc = giftResultByCustomer.getConcessionDesc();
            Optional<GiftResult> optional = concessionResult.getGiftResults().stream()
                .filter(gr -> gr.getConcessionDesc().equals(concessionDesc))
                .findFirst();

            if (!optional.isPresent())
                return false;

            GiftResult giftResultByCalculate = optional.get();
            Map<Integer, List<GiftMsg>> map = giftResultByCalculate.getGiftList().stream()
                .collect(groupingBy(GiftMsg::getGoodsId));

            for (GiftMsg giftMsg : giftResultByCustomer.getGiftList()) {
                Integer goodsId = giftMsg.getGoodsId();
                GiftMsg giftRule = map.get(goodsId).get(0);
                // 顾客所选的单类赠品不能超过单类数量限制 如果出NP问题，先考虑是否是代码错误，在考虑处理问题
                if (giftMsg.getSendNum() == 0 || giftMsg.getSendNum() > giftRule.getSendNum()) {
                    return false;
                }
            }

            // 顾客所选的每个列表的赠品不能超出总限制
            Integer totalNumByCustomer = giftResultByCustomer.getGiftList().stream()
                .map(GiftMsg::getSendNum)
                .reduce(0, Integer::sum);

            if (totalNumByCustomer == 0 || totalNumByCustomer > giftResultByCalculate.getMaxSendNum())
                return false;
        }

        return true;
    }


    private void setCouponResultToResponse(BeforeCreateOrderReq req, DistributeResponse response, Result result) {
        switch (result.getCouponConcessionRemark()) {
            case 1:
                response.setUseCoupon(true);
                response.setCouponDeductionPrice(result.getCouponDiscount());
                break;

            case 2:
                response.setUseCoupon(true);

                // 把客户端选择的赠品数据做判断，符合该回合预下单的数据结果的，重新返回赠品数据
                List<GiftResult> giftGoods = req.getGiftGoods();
                if (giftGoods != null) {
                    GiftResult giftResult = giftGoods.stream()
                        .filter(gr -> TYPE_COUPON_DETAIL.equals(gr.getConcessionDesc().getConcessionType()))
                        .findFirst()
                        .orElse(null);

                    if (giftResult != null && req.getCouponId().equals(giftResult.getConcessionDesc().getCouponDetailId())) {
                        response.getGiftResultList().add(giftResult);
                    }
                }

                break;

            default:
                throw new RuntimeException();
        }
    }

    private void setPromotionsResultToResponse(BeforeCreateOrderReq req, DistributeResponse response, Result result) {
        response.setProRuleDeductionPrice(result.getPromotionsDiscount());

        // 把客户端选择的赠品数据做判断，符合该回合预下单的数据结果的，重新返回赠品数据
        List<GiftResult> giftGoods = req.getGiftGoods();
        if (giftGoods != null) {
            giftGoods.stream()
                .filter(gr -> TYPE_PROMOTIONS_ACTIVITY.equals(gr.getConcessionDesc().getConcessionType()))
                .forEach(gr -> {
                    Integer promotionsActivityId = gr.getConcessionDesc().getPromotionsActivityId();

                    List<String> efficientPromotionsActivityIds = new ArrayList<>();
                    if (org.apache.commons.lang.StringUtils.isNotBlank(result.getEfficientPromotionsActivityId())) {
                        String[] split = result.getEfficientPromotionsActivityId().split(",");
                        efficientPromotionsActivityIds.addAll(Arrays.asList(split));
                    }

                    if (promotionsActivityId != null && efficientPromotionsActivityIds.contains(promotionsActivityId.toString())) {
                        response.getSelectedGiftResultList().add(gr);
                    }
                });
        }
    }

    /**
     * 本地计算距离
     *
     * @param stores
     * @param coordinate
     * @return
     */
    public Map getDistributeMapNotGD(List<Store> stores, Coordinate coordinate) {
        Map<Integer, Store> storeDistanceMap = new ConcurrentHashMap<>();
        if (!StringUtil.isEmpty(stores)) {
            //然后再调用高德地图API，计算两个坐标的距离，根据距离最近的来选择门店
            for (Store store : stores) {
                //logger.info("********************store:{} lng:{}, lat:{}:", store.getName(), store.getGaodeLng(), store.getGaodeLat());
                if (StringUtils.isEmpty(store.getGaodeLng()) || StringUtils.isEmpty(store.getGaodeLat())) {
                    //忽略门店经纬度为空的门店
                    continue;
                }
                //本地计算
                int distance = Distribute.getDistance(Double.parseDouble(store.getGaodeLng()), Double.parseDouble(store.getGaodeLat()), coordinate.getLng(), coordinate.getLat());
                //理论上不会有两个门店的距离一模一样
                storeDistanceMap.put(distance, store);
                //logger.info("********************store:{} distance:{}:", store.getName(), distance);
            }
        }
        return storeDistanceMap;
    }

    /**
     * 获取商家设置支持订单类型
     * 110（门店自提），120（送货上门），130（门店直购）,210（即时配送）,220（普通快递配送），100（其他），200（其他）
     * 手机商城 m_shop
     * 门店助手 store_ass
     * 门店后台 store
     * pc商城 pc_shop
     * 服务状态 ser minimum_rice（元起送）close（关闭，勾选为true）
     * 配送时效 dis
     *
     * @param site_id
     * @param tradesSource
     * @return
     */
    public Map querySettingDis(String site_id, int tradesSource, int rderRealPrice) {
        Map<String, Object> remap = new ConcurrentHashMap<>();
        String settingDis = merchantExtTreatMapper.querySettingDis(site_id);
        logger.info("获取商家设置支持订单类型settingDis:{}", settingDis);
        if (StringUtil.isEmpty(settingDis)) {
            settingDis = "{\"m_shop\":\"\",\"store_ass\":\"130\",\"store\":\"130\",\"pc_shop\":\"120\",\"dis\":\"\",\"ser\":{\"minimum_rice\":\"\"}}\n";
        }
        try {
            String type = "m_shop";
            if (tradesSource == 130) {
                type = "store_ass";
            } else if (tradesSource == 140) {
                type = "store";
            } else if (tradesSource == 110) {
                type = "pc_shop";
            }

            Map<String, Object> map = JacksonUtils.json2mapDeeply(settingDis);

            Object ser = map.get("ser");
            Object orderType = map.get(type);
            remap.put("suportOrderType", orderType);
            if (!StringUtil.isEmpty(ser) && !StringUtil.isEmpty(orderType) && orderType.toString().indexOf("120") != -1) {
                Map<String, Object> serMap = (Map<String, Object>) ser;
                remap.put("close", false);
                Integer all_rice = Integer.parseInt(serMap.get("minimum_rice").toString());
                remap.put("allow_price", all_rice);
                if (rderRealPrice >= all_rice || all_rice == 0 || rderRealPrice == 0) {
                    remap.put("close", true);
                } else {
                    remap.put("minimum_rice", all_rice - rderRealPrice);
                }

            }
        } catch (Exception e) {
            logger.info("获取商家设置支持订单类型Exception:{}", e);
        }
        return remap;
    }

    /**
     * 通过erp获取门店是否有符合条件的库存
     *
     * @param siteId 商户号
     * @param stores 门店列表(经过距离排序的门店列表)
     * @return 符合条件的门店对象
     */
    public List<Store> getBestStorageStoreFromERP1(Integer siteId, List<Store> stores, List<GoodsInfo> goodsInfoInfos) {
        boolean isExection = false;
        List<Store> storesList = new ArrayList<Store>();
        for (Store store : stores) {
            boolean exists = true;
            Map<String, Integer> goodsCodeMap = new HashMap<String, Integer>();
            StringBuffer goodsno = new StringBuffer("");
            int goodsInfos = 0;
            for (GoodsInfo goodsInfo : goodsInfoInfos) {
                String kcqty = stringRedisTemplate.opsForValue().get(siteId + "_" + store.getStoresNumber() + "_" + goodsInfo.getGoodsCode() + "_Kcqty");
                logger.info("redis:siteId:{}, kcqty:{},门店编码:{},商品编号:{}", siteId, kcqty, store.getStoresNumber(), goodsInfo.getGoodsCode());
                if (StringUtil.isEmpty(kcqty)) {
                    goodsCodeMap.put(String.valueOf(goodsInfo.getGoodsCode()), goodsInfo.getControlNum());//方便进行比较
                    goodsno.append(goodsInfo.getGoodsCode() + ",");//放入erp进行查询
                    goodsInfos++;
                } else {
                    if (Double.parseDouble(kcqty) < goodsInfo.getControlNum()) {
                        exists = false;//数量不满足需求
                        break;
                    }
                }
            }
            if (goodsInfos == 0) {
                if (exists) {
                    storesList.add(store);//该门店数量全部满足需求
                }
                continue;
            } else {
                try { //调用ERP接口获取商品库存
                    logger.info("storage_service_url路径[{}]", "siteId=" + siteId + "&goodsno=" + goodsno + "&uid=" + store.getStoresNumber());
                    Map<String, Object> responseText = storageService.getStorageBysiteId(String.valueOf(siteId), goodsno.toString(), store.getStoresNumber());
                    logger.info("ERP商户:{},商品库存查询结果{}", siteId, responseText);
                    List<Map<String, Object>> storageInfoList = (List<Map<String, Object>>) responseText.get("info");
                    if (!StringUtil.isEmpty(storageInfoList) && storageInfoList.size() == goodsInfos) {
                        for (Map<String, Object> storageInfo : storageInfoList) {
                            logger.info("库存比较-----------------商品编号：{},购买数量：{},门店编码:{},库存数量：{}", storageInfo.get("GOODSNO"), goodsCodeMap.get(storageInfo.get("GOODSNO")), store.getStoresNumber(), Double.parseDouble(storageInfo.get("kcqty") + ""));
                            if (!StringUtil.isEmpty(storageInfo.get("kcqty")) && !"0.0".equals(storageInfo.get("kcqty"))) {
                                if (Double.parseDouble(storageInfo.get("kcqty") + "") > 0 && Double.parseDouble(storageInfo.get("kcqty") + "") < 10) {
                                    stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("GOODSNO") + "_Kcqty", storageInfo.get("kcqty") + "", 1, TimeUnit.HOURS);
                                    logger.info("ERP商品库存数量[{}]1", storageInfo.get("kcqty"));
                                } else if (Double.parseDouble(storageInfo.get("kcqty") + "") >= 10 && Double.parseDouble(storageInfo.get("kcqty") + "") < 50) {
                                    stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("GOODSNO") + "_Kcqty", storageInfo.get("kcqty") + "", 3, TimeUnit.HOURS);
                                    logger.info("ERP商品库存数量[{}]2", storageInfo.get("kcqty"));
                                } else if (Double.parseDouble(storageInfo.get("kcqty") + "") >= 50) {
                                    stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("GOODSNO") + "_Kcqty", storageInfo.get("kcqty") + "", 1, TimeUnit.DAYS);
                                    logger.info("ERP商品库存数量[{}]3", storageInfo.get("kcqty"));
                                }
                                if (goodsCodeMap.get(storageInfo.get("GOODSNO")) > Double.parseDouble(storageInfo.get("kcqty") + "")) {
                                    exists = false;
                                    break;
                                }
                            } else {
                                exists = false;
                                stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + storageInfo.get("GOODSNO") + "_Kcqty", "0", 1, TimeUnit.HOURS);
                                break;
                            }
                        }
                    } else {//缓存中没有库存的商品在erp接口中也没有库存数据
                        exists = false;
                        String[] arr = goodsno.toString().split(",");
                        for (String str : arr) {
                            stringRedisTemplate.opsForValue().set(siteId + "_" + store.getStoresNumber() + "_" + str + "_Kcqty", "0", 1, TimeUnit.HOURS);
                        }
                    }
                } catch (Exception e) {
                    exists = false;
                    logger.error("调用erp查询商品库存失败,商户号[{}],门店id[{}]，商品编码[{}],问题：{}", siteId, store.getStoresNumber(),
                        goodsno, e.getMessage());
                }
                if (exists) { //如果所有商品都有库存则符合条件，直接返回.
                    storesList.add(store);
                }
            }
        }
        return storesList;
    }
}
