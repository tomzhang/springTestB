package com.jk51.modules.appInterface.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.SVipMember;
import com.jk51.model.coupon.tags.TagsGoodsPromotions;
import com.jk51.model.coupon.tags.TagsParam;
import com.jk51.model.erpprice.BGoodsErp;
import com.jk51.model.goods.YbStoresGoodsPrice;
import com.jk51.model.treat.MerchantExtTreat;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import com.jk51.modules.coupon.tags.PromotionsTimeTagsFilter;
import com.jk51.modules.appInterface.util.OfflineQRCodeParam;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.erpprice.service.ErpPriceService;
import com.jk51.modules.es.entity.AppGoodsInfo;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.member.mapper.VipMemberMapper;
import com.jk51.modules.pandian.service.InventoriesManager;
import com.jk51.modules.persistence.mapper.BTradesMapper;
import com.jk51.modules.task.mapper.BTaskExecuteMapper;
import com.jk51.modules.task.service.TaskService;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.StockupMapper;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yanglile
 * 创建日期: 2017-06-15
 * 修改记录:
 */
@Service
public class Orders2Service {
    private static final Logger logger = LoggerFactory.getLogger(Orders2Service.class);
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private StoreAdminExtMapper storeAdminextMapper;
    @Autowired
    private BTradesMapper bTradesMapper;
    @Autowired
    private BMemberMapper bMemberMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    private StockupMapper stockupMapper;
    @Autowired
    private VipMemberMapper vipMemberMapper;
    @Autowired
    AppMemberService memberService;
    @Autowired
    TaskService taskService;
    @Autowired
    private BTaskExecuteMapper bTaskExecuteMapper;
    @Autowired
    MerchantExtTreatMapper merchantExtTreatMapper;
    @Autowired
    private ErpPriceService erpPriceService;
    @Autowired
    private InventoriesManager inventoriesManager;
    @Autowired
    private ServletContext servletContext;


    /**
     * APP工作首页 统计
     * @param params
     * @return
     */
    public Map<String,Object> workerIndex(Map<String, Object> params) throws Exception{
        Map<String,Object> result = new HashMap<>();

        /*StoreAdminExt storeAdminex = storeAdminextMapper.selectByPrimaryKey(Integer.parseInt((String) params.get("storeAdminExtId")), Integer.parseInt((String) params.get("siteId")));
        if(StringUtil.isEmpty(storeAdminex)|| StringUtil.isEmpty(storeAdminex.getClerk_invitation_code())){
            throw new RuntimeException("店员信息错误！");
        }
        String invitation_code = storeAdminex.getClerk_invitation_code().replaceAll("\\d+\\_","");*/

        /*//店员 今日注册数
        Integer registe_num = statisticsService.getRegisteNum(invitation_code, DateUtils.getToday(), Integer.parseInt((String) params.get("siteId")));
        //店员 今日接单数   --------已付款订单
        Integer todaySaleNum = bTradesMapper.todayTradesNumByIsPay((String) params.get("siteId"), (String) params.get("storeId"), (String) params.get("storeAdminId"));
        //店员 今日下单金额
        Integer todaySaleCount = bTradesMapper.todayTradesRealPayByIsPay((String) params.get("siteId"), (String) params.get("storeId"), (String) params.get("storeAdminId"));*/

        //店员 收款   ------------门店中所有待收款直购订单数量
        Integer isPayingNum = bTradesMapper.getTradesNumByStoreIsPaying((String) params.get("siteId"), (String) params.get("storeId"), (String) params.get("storeAdminId"));
        //店员 备货发货  ----------门店汇总所有待备货，已备货，待提货，待发货的订单数量
        Integer processingNum = bTradesMapper.getTradesNumByStoreWaitProcess((String) params.get("siteId"), (String) params.get("storeId"));

        //盘点计划数量
        Integer pandianPlanNum = inventoriesManager.getPandianPlanNum((String)params.get("siteId"),(String)params.get("storeId"),(String)params.get("storeAdminId"));

        //查询优惠券数量
        List<Map<String, Object>> customerCoupons = memberService.getCustomerCoupon(Integer.parseInt(params.get("siteId")+""),Integer.parseInt(params.get("storeAdminExtId")+""),
            params.get("storeAdminId")+"");

        Integer canPD = getClerkCanPD(Integer.parseInt(params.get("siteId")+""), Integer.parseInt(params.get("storeId")+""), Integer.parseInt(params.get("storeAdminId")+""));

        result.put("registerNum", 0);
        result.put("todaySaleNum", 0);
        result.put("todaySaleCount", 0);
        result.put("isPayingNum", StringUtil.isEmpty(isPayingNum)?0:isPayingNum);
        result.put("processingNum", StringUtil.isEmpty(processingNum)?0:processingNum );
        result.put("couponNum", StringUtil.isEmpty(customerCoupons)?0:customerCoupons.size() );
        result.put("taskNum", gettaskNum(params.get("siteId"),params.get("storeAdminId"),params.get("storeId")) );
        result.put("pandianPlanNum", pandianPlanNum );
        result.put("canPD", canPD);
        result.put("status","OK");
        return result;
    }

    //根据店员查询任务数量
    private int gettaskNum(Object siteId,Object storeAdminId,Object storeId){

        Map<String,Object> param = new HashMap<String,Object>();
        param.put("joinId",storeAdminId);
        param.put("siteId",siteId);
        param.put("storeId",storeId);

        List<Integer> planIds = taskService.queryPlanIdsByJoin(param);
        if(StringUtil.isEmpty(planIds)){
            return 0;
        }

        param.put("planIds",planIds);
        List<Map<String,Object>> list = bTaskExecuteMapper.selectTaskListByJoinId(param);

        return StringUtil.isEmpty(list)?0:list.size();
    }

    public Map<String,Object> selectGoodsByPhone(String siteId, String storeId, String mobile, String storeAdminId) throws Exception{
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> memberMap = bMemberMapper.selectMemberMapByPhoneNum(siteId, mobile);
        if (memberMap!=null && memberMap.size()>0){
            List<Map<String,Object>> items = goodsMapper.selectGoodsByBuyerId(siteId, String.valueOf(memberMap.get("buyer_id")));

            MerchantExtTreat merchantExt = merchantExtTreatMapper.selectByMerchantId( Integer.parseInt(siteId) );
            items.stream().forEach(m -> {
                //未开启erp价格对接
                /*if(!StringUtil.isEmpty(merchantExt.getHas_erp_price())&&merchantExt.getHas_erp_price().equals(1)&&!StringUtil.isEmpty(m.get("erpPrice"))&&Integer.parseInt(m.get("erpPrice")+"")!=0 ){
                    m.put("shopPrice",m.get("erpPrice"));
                }*/
                YbStoresGoodsPrice ybStoresGoodsPrice = this.goodsMapper.queryGoodStorePrice((Integer) m.get("goodsId"), Integer.parseInt(siteId), Integer.parseInt(storeId));



                if (null!=ybStoresGoodsPrice && 0!=ybStoresGoodsPrice.getDiscountPrice()){
                    //m.put("shopPrice",ybStoresGoodsPrice.getDiscountPrice());
                    m.put("shopPrice",ybStoresGoodsPrice.getDiscountPrice());
                }
                m.put("shoppingRecord", 1);
                /*if (1==merchantExt.getHas_erp_price()) {
                    List<Integer> goodsIds=new ArrayList<Integer>();
                    goodsIds.add((Integer)m.get("goodsId"));
                    Map<Integer, BGoodsErp> bGoodsErpMap = erpPriceService.selectERPPrice(Integer.parseInt(siteId), goodsIds,Integer.parseInt(storeId));
                    if (Objects.nonNull(bGoodsErpMap.get(m.get("goodsId")))) {
                        m.put("shopPrice", bGoodsErpMap.get(m.get("goodsId")).getPrice());
                    }
                }*/

                //根据分类查询关联分类
                String userCateid = String.valueOf(m.get("userCateid"));
                if (userCateid.length() > 8) {
                    Map<String,Object> map = goodsMapper.queryRelevanceClassify(Integer.valueOf(siteId), userCateid);
                    if (Objects.nonNull(map)) {
                        m.put("relevanceClassify", map.get("relevanceClassify"));
                        m.put("relevanceReson", map.get("relevanceReson"));
                    }
                }


            });
            //添加需求，判断是否有任务计划（即考核）-------start
            //是否有任务考核
            List<String> taskplansGoodsIds = goodsMapper.getKaoHeBySiteIdAndStoreIdOrStoreAdminId(Integer.valueOf(siteId), Integer.valueOf(storeId), storeAdminId);
            List<String> splitList = new ArrayList<>();
            boolean isAll = false;
            if(CollectionUtils.isNotEmpty(taskplansGoodsIds)){
                Long all=taskplansGoodsIds.stream().filter(s -> Objects.equals("all",s)).count();
                if(all > 0L){
                    isAll =true;
                }else{
                    splitList =Arrays.asList(StringUtil.join(taskplansGoodsIds,",").split(","));
                }
            }
            if(isAll){
                items.stream().forEach(item -> {
                    item.put("haveTaskPlan", 1);
                });
            }else{
                for (Map item : items) {
                    if(splitList.contains(item.get("goodsId").toString())){
                        item.put("haveTaskPlan", 1);
                    }else {
                        item.put("haveTaskPlan", 0);
                    }
                }
            }
            //添加需求，判断是否有任务计划（即考核）-------end

            //获取商品参加的活动 tags ------start
            String goodsIds = items.stream()
                .map(gs -> {
                    return gs.get("goodsId").toString();
                }).collect(Collectors.joining(","));
            PromotionsTimeTagsFilter promotionsTagsFilter = new PromotionsTimeTagsFilter(servletContext, new TagsParam(Integer.parseInt(siteId), Integer.parseInt(memberMap.get("member_id").toString()), goodsIds, 3));
            promotionsTagsFilter.collection();
            try {
                promotionsTagsFilter.sorted().resolve();
            } catch (Exception e) {
                logger.error("处理标签异常:{}",e);
            }
            List<TagsGoodsPromotions> tags = promotionsTagsFilter.getTags();
            items.forEach(gs->{
                tags.forEach(tg->{
                    if(gs.get("goodsId").toString().equals(tg.getGoodsId()))
                        gs.put("tag", tg.getTags());
                });
            });

            //获取商品参加的活动 tags ------end


            result.put("items", items);
            result.put("totalItems",items.size());
        }
        result.put("status","OK");
        return result;
    }

    /**
     * 最近服务顾客：
     *      半个月内咨询我的会员（门店范围内）；
     *      半个月内我注册的会员；
     *      半个月内我协助下单的会员；
     *      -------半个月内我编辑过信息的会员。------没有-----
     *      以上时间区限可在后端不发布版本的情况下调整。建议30人一页。
     * @throws Exception
     */
    public Map<String,Object> serveCustomersCount(Map<String, Object> params) throws Exception{
        Map<String,Object> result = new HashMap<>();
        long total = vipMemberMapper.serveCustomersCount(params);
        result.put("serveTotal", total);
        result.put("status","OK");
        return result;
    }

    public Map<String,Object> selectStockupId(Map<String,Object> params) throws Exception{
        Map<String,Object> result = new HashMap<>();
        List<Map<String, Object>> tradesStockups = stockupMapper.getTradesStockupInfoList(params);
        result.put("status","OK");
        result.put("stockupId", (tradesStockups!=null&&tradesStockups.size()>0)?tradesStockups.get(0).get("stockup_id"):null);
        return result;
    }

    public List<SVipMember> serveCustomers(Map<String, Object> params) {
        return vipMemberMapper.serveCustomers(params);
    }

    public Map<String,Object> getStoreAdminExt(Map<String, Object> params) {
        return storeAdminextMapper.getStoreAdminExt(params);
    }

    public long myDeliveryOrderNum(Map<String, Object> params) {
        return bTradesMapper.myDeliveryOrderNum(params);
    }

    public Integer getClerkCanPD(Integer siteId, Integer storeId, Integer storeAdminId) {
        Integer canPD = null;
        try {
            canPD = merchantExtTreatMapper.selectCheckStore(siteId, storeId);
        } catch (Exception e) {
            logger.error("获取失败" + e);
        }
        if(canPD == null){
            canPD = 0;
        }
        return canPD;
    }

    //获取预约单中商品信息
    @SuppressWarnings("all")
    public Map<String,Object> getPreGoodsByNumber(String siteId, String storeId, String preNumber) throws Exception{
        Map<String,Object> result = new HashMap<>();
        //查询预约单及商品信息
        Map<String,Object> preGoods = goodsMapper.getGoodsByPreNumber(siteId, preNumber);
        //根据手机号查询会员信息
        Map<String,Object> memberMap = bMemberMapper.selectMemberMapByPhoneNum(siteId, String.valueOf(preGoods.get("prebookPhone")));
        //如果有会员信息, 根据这些信息查询多价格
        if (memberMap!=null && memberMap.size()>0){
            //查询会员购买的商品信息
//            List<Map<String,Object>> items = goodsMapper.selectGoodsByBuyerId(siteId, String.valueOf(memberMap.get("buyer_id")));

            //商户信息
            MerchantExtTreat merchantExt = merchantExtTreatMapper.selectByMerchantId( Integer.parseInt(siteId) );

//            items.stream().forEach(m -> {
                //未开启erp价格对接
                /*if(!StringUtil.isEmpty(merchantExt.getHas_erp_price())&&merchantExt.getHas_erp_price().equals(1)&&!StringUtil.isEmpty(m.get("erpPrice"))&&Integer.parseInt(m.get("erpPrice")+"")!=0 ){
                    m.put("shopPrice",m.get("erpPrice"));
                }*/

                //有值就将原本的价格替换掉
                YbStoresGoodsPrice ybStoresGoodsPrice = this.goodsMapper.queryGoodStorePrice((Integer) preGoods.get("goodsId"), Integer.parseInt(siteId), Integer.parseInt(storeId));
                if (null!=ybStoresGoodsPrice && 0!=ybStoresGoodsPrice.getDiscountPrice()){
                    //m.put("shopPrice",ybStoresGoodsPrice.getDiscountPrice());
                    preGoods.put("shopPrice",ybStoresGoodsPrice.getDiscountPrice());
                }
                //有erp价格就
                if (1==merchantExt.getHas_erp_price()) {
                    List<Integer> goodsIds=new ArrayList<Integer>();
                    goodsIds.add((Integer)preGoods.get("goodsId"));
                    Map<Integer, BGoodsErp> bGoodsErpMap = erpPriceService.selectERPPrice(Integer.parseInt(siteId), goodsIds,Integer.parseInt(storeId));
                    if (Objects.nonNull(bGoodsErpMap.get(preGoods.get("goodsId")))) {
                        preGoods.put("shopPrice", bGoodsErpMap.get(preGoods.get("goodsId")).getPrice());
                    }
                }

//            });

        }
        result.put("items", preGoods);
//        result.put("totalItems",1);
        result.put("status","OK");
        return result;
    }

    public Result offlineQRCode(Integer siteId, Integer storeId, Integer storeAdminId, String tradesId) {
        Result result = null;
        if (StringUtils.isBlank(tradesId)) {
            result = Result.fail("非法参数");
        } else {
            try {
                OfflineQRCodeParam offlineQRCodeParam = bTradesMapper.getOfflineQRCodeParam(tradesId);
                if (offlineQRCodeParam == null) {
                    result = Result.fail("查无订单");
                } else if (StringUtils.isBlank(offlineQRCodeParam.getWxShopDomain())) {
                    result = Result.fail("查无微信域名");
                } else {
                    if (storeId == null) storeId = offlineQRCodeParam.getStoreId();
                    if (storeAdminId == null) storeAdminId = offlineQRCodeParam.getStoreAdminId();
                    String domain = offlineQRCodeParam.getWxShopDomain().split(",")[0].trim();
                    if (!Pattern.compile("(http:|https:){1}", Pattern.CASE_INSENSITIVE).matcher(domain).lookingAt()) domain = "http://" + domain;
//                    String url = domain + "/order/orderDiscount?trades_store=" + storeId + "&store_user_id=" + storeAdminId + "&tradesId=" + tradesId;
                    String url = domain + "/new/calculateloads?tradesId=" + tradesId;
                    result = Result.success(url);
                }
            } catch (Exception e) {
                result = Result.fail("查询异常");
            }
        }
        return result;
    }

}
