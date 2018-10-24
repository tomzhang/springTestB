package com.jk51.modules.integral.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.CityHasStores;
import com.jk51.model.map.Coordinate;
import com.jk51.model.order.BestStoreQueryParams;
import com.jk51.model.order.GoodsInfo;
import com.jk51.model.order.SortedStore;
import com.jk51.model.order.Store;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.integral.mapper.IntegralGoodsMapper;
import com.jk51.modules.integral.model.IntegralGoodsDetail;
import com.jk51.modules.map.service.MapService;
import com.jk51.modules.order.service.DistributeOrderService;
import com.jk51.modules.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Created by guosheng on 2017/6/2.
 */
@Service
public class IntegralGoodsService {

    public static final Logger logger = LoggerFactory.getLogger(IntegralGoodsService.class);


    @Autowired
    private IntegralGoodsMapper integralGoodsMapper;

    @Autowired
    private StoresMapper storesMapper;

    @Autowired
    GoodsMapper goodsMapper;

    @Autowired
    private MapService mapService;

    @Autowired
    private DistributeOrderService distributeOrderService;

    @Autowired
    private OrderService ordersService;

    public List queryIntegralGoods(Map parameterMap) {
        List<Map<String, Object>> list = integralGoodsMapper.queryIntegralGoods(parameterMap);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();

        list.forEach(item -> {
            if (item.get("start_time") != null && !item.get("start_time").toString() .equals("0000-00-00 00:00") ) {
//                LocalDateTime startTime = LocalDateTime.now();;
//                try {
                LocalDateTime startTime = LocalDateTime.parse(item.get("start_time").toString(), formatter);
//                }catch (Exception e) {
////                    LocalDateTime.parse(startTime.format(formatter), formatter);
                LocalDateTime minTime = startTime.minusDays(1);

                if (now.isBefore(minTime)) {
                    item.put("status_text", "未开始");  //24小时之前
                } else if (now.isAfter(minTime) && now.isBefore(startTime)) {
                    item.put("status_text", "即将开启兑换");
                } else if ( now.isAfter(startTime) && item.get("end_time") .toString().equals("0000-00-00 00:00")) {
                    item.put("status_text", "进行中");
                } else if(!item.get("end_time") .toString().equals("0000-00-00 00:00")){
                    LocalDateTime endTime = LocalDateTime.parse(item.get("end_time").toString(), formatter);
                    LocalDateTime maxTime = endTime.minusDays(1);

                    if (now.isAfter(startTime) && now.isBefore(maxTime)) {
                        item.put("status_text", "进行中");
                    } else if (now.isAfter(maxTime) && now.isBefore(endTime)) {
                        Object seconds = Duration.between(now,endTime).getSeconds();
                        int s = Integer.valueOf(seconds.toString());
                        int balance = (int)Math.floor(s/3600);
                        if(balance > 0){
                            item.put("status_text", "仅剩"+balance+"小时");
                        }else{
                            item.put("status_text", "仅剩不足1小时");
                        }
//                        item.put("status_text", "即将截止");
                    } else {
                        item.put("status_text", "已截止");
                    }
                }

            }
            if (item.get("store_ids") != null) {
                String[] storeIds = (item.get("store_ids").toString()).split(",");

                List<Integer> storeIdsList = Arrays.asList(storeIds).parallelStream().mapToInt(storeId -> {
                    try {
                        return Integer.parseInt(storeId);
                    } catch (NumberFormatException e) {
                        return -1;
                    }
                }).collect(() -> new ArrayList<Integer>(), ArrayList::add, ArrayList::addAll);

                List<String> storeNames = storesMapper.getStoreNamesByStoreIds(Integer.parseInt(item.get("site_id").toString()), storeIdsList);

                item.put("store_names", storeNames);



            }

        });


        return list;
    }

    public String deleteIntegralGoods(Integer id) {
        int result = integralGoodsMapper.deleteIntegralGoods(id);
        if (result != 1) {
            return "500";
        } else {
            return "200";
        }

    }

    public String updateIntegralGoods(Map parameterMap) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(parameterMap.get("starttime").toString(),formatter);

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)){
            parameterMap.put("status", 0);
        } else if(parameterMap.get("endtime").toString().equals("0000-00-00 00:00")) {

            parameterMap.put("status", 10);
//            if (now.isBefore(startTime)) {
//                parameterMap.put("status", 0);
//            } else if (now.isAfter(startTime) && parameterMap.get("endtime").toString().equals("0000-00-00 00:00:00")) {
//                parameterMap.put("status", 10);
//            }
        } else {
                LocalDateTime endTime = LocalDateTime.parse(parameterMap.get("endtime").toString(),formatter);

                if (now.isAfter(startTime) && now.isBefore(endTime)) {
                    parameterMap.put("status", 10);
                } else if (now.isAfter(endTime)) {
                    parameterMap.put("status", 20);
                }
        }


        int result = integralGoodsMapper.updateIntegralGoods(parameterMap);
        if (result != 1) {
            return "500";
        } else {
            return "200";
        }

    }

    public IntegralGoodsDetail getIntegralGoods(Integer siteId, Integer goodsId) {

        IntegralGoodsDetail integralGoods = integralGoodsMapper.getIntegralGoodsByGoodsId(siteId, goodsId);

        Date startTime = integralGoods.getStartTime();
        Date endTime = integralGoods.getEndTime();
        Integer startTimeInterval = integralGoods.getStartTimeInterval();
        Integer endTimeInterval = integralGoods.getEndTimeInterval();

        DateFormat dateFormat = new SimpleDateFormat("M月d日H点m分");

        if (startTimeInterval != null && startTimeInterval > 0) {
            integralGoods.setTimeMessage(dateFormat.format(startTime) + "即将开始");
            integralGoods.setOpenStatus(0);
        }
        if (endTimeInterval != null && endTimeInterval > 0 && (startTimeInterval == null || startTimeInterval <= 0)) {
            integralGoods.setTimeMessage(dateFormat.format(endTime) + "截止兑换");
            integralGoods.setOpenStatus(1);
        }
        if (endTimeInterval != null && endTimeInterval <= 0) {
            integralGoods.setTimeMessage("截止兑换");
            integralGoods.setOpenStatus(3);
        }
        if (endTimeInterval == null && (startTimeInterval == null || startTimeInterval <= 0)) {
            integralGoods.setTimeMessage("不限时");
            integralGoods.setOpenStatus(2);
        }
        return integralGoods;
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
        List<Store> storeListbyToselfAndBycityId = new ArrayList<>();
        List<Store> storageStores = new ArrayList<>();
        List<Store> storeList = new ArrayList<>();


//        Map parameterMap=null;
//        parameterMap.put("siteId",siteId);

//        String ss=params.getGoodsIds().split("@")[0];
        Map<String, Object> result=integralGoodsMapper.queryIntegralGoodsByGoodsId(siteId,Integer.valueOf(params.getGoodsIds().split("@")[0]));

        List<Store> stores;
        if(result.get("store_ids").toString().equals("")){
            stores =integralGoodsMapper.getStores(siteId);
        }else{
            String [] ids=result.get("store_ids").toString().split(",");
            stores = integralGoodsMapper.getStoresBySiteId(siteId,ids);
        }


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
            storageStores = distributeOrderService.getBestStorageStore(siteId, stores, goodsInfoInfos);
        }

        if (!StringUtil.isEmpty(storageStores)) {
            stores = storageStores;
        }
        for (Store store : stores) {
            if (store.getServiceSupport().indexOf("160") > -1) {
                storeListbyToselfAndBycityId.add(store);
            }
        }
        if (CollectionUtils.isEmpty(storeListbyToselfAndBycityId)) {
            response.put("total_stores", storeListbyToselfAndBycityId.size());
            response.put("store", null);
            return response;
        }
        if (params.getCityId() != null && params.getCityId() != 0) {
            //根据前端要求需要返回不含城市条件的有效门店数
//            List<Store> _stores = ordersMapper.getStoresBySiteId(siteId, null);
            List<Store> _stores;
            if(result.get("store_ids").toString().equals("")){
                _stores =integralGoodsMapper.getStores(siteId);
            }else{
                String [] ids=result.get("store_ids").toString().split(",");
                _stores = integralGoodsMapper.getStoresBySiteId(siteId,ids);
            }
            if (!CollectionUtils.isEmpty(goodsInfoInfos)) {
                //如果没有给定商品列表，不走库存
                storageStores = distributeOrderService.getBestStorageStore(siteId, _stores, goodsInfoInfos);
            }
            if (!StringUtil.isEmpty(storageStores)) {
                _stores = storageStores;
            }
            for (Store store : _stores) {
                if (store.getServiceSupport().indexOf("160") > -1) {
                    storeList.add(store);
                }
            }
            response.put("total_stores", storeList.size());
        } else {
            response.put("total_stores", storeListbyToselfAndBycityId.size());
        }
/**
 * 最优门店
 */
        //如果用户经纬度为空，不获取最优门店
        if (params.getLng() == null || params.getLat() == null) {
            return response;
        }

        Set<SortedStore> sorted = queryStoreDistance(params, storeListbyToselfAndBycityId);
        if (StringUtil.isEmpty(params.getGoodsIds())) {
            //如果没有给定商品列表，则直接返回最近的一个门店
            response.put("store", sorted.iterator().next().getStore());
            return response;
        }
        if (StringUtil.isEmpty(params.getGoodsIds())) {
            //如果没有给定商品列表，则直接返回最近的一个门店
            return ordersService.getStoreNewy(params,sorted,response,null);
        }


        List<Store> hasStoresList  = ordersService.getBestStorageStore(siteId, sorted, goodsNos.substring(0, goodsNos.length() - 1), goodsInfoInfos);
        Coordinate coordinate= new Coordinate(params.getLng() ,params.getLat());
        Map<Integer, Store> storeDistanceMap = distributeOrderService.getDistributeMap(hasStoresList, coordinate);
        Integer minDistance=null;
        if (!StringUtil.isEmpty(storeDistanceMap)) {
            //找出距离最近的门店(单位：米)
            Set<Integer> set = storeDistanceMap.keySet();
            Object[] obj = set.toArray();
            Arrays.sort(obj);
            minDistance = (int) obj[0];
        }
        Map<String, Object> re=ordersService.getStoreNewy(params,null,response,hasStoresList);
        if(!re.containsKey("store")&&!StringUtil.isEmpty(minDistance)&&!StringUtil.isEmpty(storeDistanceMap)){
            response.put("store",  storeDistanceMap.get(minDistance));
        }
        return re;
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

    public List<CityHasStores> GroupStoresByCityAndServiceSupport(Integer siteId, String servicesupport,String goodId) {

        Map<String, Object> result=integralGoodsMapper.queryIntegralGoodsByGoodsId(siteId,Integer.valueOf(goodId.split("@")[0]));


        if(result.get("store_ids").toString().equals("")){
            return integralGoodsMapper.GroupStoresByCity(siteId, servicesupport);
        }else{
            String [] ids=result.get("store_ids").toString().split(",");
            return integralGoodsMapper.GroupStoresByCityAndServiceSupport(siteId, servicesupport,ids);
        }
//        String [] ids=result.get("store_ids").toString().split(",");
//        return integralGoodsMapper.GroupStoresByCityAndServiceSupport(siteId, servicesupport,ids);
    }

    public List<Store> selectStoresByCityAndServiceSupport(Integer siteId, Integer cityId, String serviceSupport,String goodId) {
        Map<String, Object> result=integralGoodsMapper.queryIntegralGoodsByGoodsId(siteId,Integer.valueOf(goodId.split("@")[0]));

        if(result.get("store_ids").toString().equals("")){
            return integralGoodsMapper.selectStoresByCity(siteId, cityId, serviceSupport);
        }else{
            String [] ids=result.get("store_ids").toString().split(",");
            return integralGoodsMapper.selectStoresByCityAndServiceSupport(siteId, cityId, serviceSupport,ids);
        }
//        String [] ids=result.get("store_ids").toString().split(",");
//        return integralGoodsMapper.selectStoresByCityAndServiceSupport(siteId, cityId, serviceSupport,ids);
    }
}


