package com.jk51.modules.esn.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.map.Coordinate;
import com.jk51.model.order.SBMember;
import com.jk51.model.order.Store;
import com.jk51.modules.esn.mapper.GoodsEsMapper;
import com.jk51.modules.esn.service.GoodsEsService;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.map.service.MapService;
import com.jk51.modules.merchant.service.UserStoresDistanceService;
import com.jk51.modules.order.service.DistributeOrderService;
import com.jk51.modules.store.service.BMemberService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * 定时更新索引库中的销量字段
 *
 * @auhter zy
 * @create 2017-09-07 11:46
 */
@Component
public class EsSearchSchedule {

    private static final Logger logger = LoggerFactory.getLogger(EsSearchSchedule.class);
    @Autowired
    private GoodsEsMapper goodsEsMapper;

    @Autowired
    private GoodsEsService goodsEsService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserStoresDistanceService userStoresDistanceService;
    @Autowired
    private DistributeOrderService distributeOrderService;

    @Autowired
    private StoresService storesService;
    @Autowired
    private BMemberService bMemberService;

    @Autowired
    private MapService mapService;
    //半夜12点
    public void autoUpdateESIndex() {
        //查询所有的商家id
       /* List<String> merchantIds = goodsEsMapper.queryMerchantIds();
        merchantIds.parallelStream().unordered().forEach(s -> {
            this.autoUpdateESIndexOne(s);
        });*/
        /*for(String s : merchantIds) {

            try {
                goodsEsService.batchUpdateGoods("b_shop_"+s, "0");
            }catch (Exception e){
                goodsEsService.updateSuggestByBrandId("b_shop_"+s);
                logger.error("HomeController.batchGoods error , error Message:",e);
            }
        }*/
        this.autoUpdateESIndexAll();
        logger.info("所有商家更新ES索引库完成!");
    }
    @Async
    public void autoUpdateESIndexAll() {
        //查询所有的商家id
        List<String> merchantIds = goodsEsMapper.queryMerchantIds();
       // merchantIds.parallelStream().unordered().forEach(s -> {
        merchantIds.stream().forEach(s -> {
            this.autoUpdateESIndexOne(s);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
        });
    }

    public void autoUpdateESIndexOne(String merchantId) {
        try {
            goodsEsService.batchUpdateGoods("b_shop_"+merchantId, "0");
        }catch (Exception e){
            goodsEsService.updateSuggestByBrandId("b_shop_"+merchantId);
            logger.debug("HomeController.batchGoods error , error Message:",e);
        }
    }
    public void insertUserStoresDistanceList() {
        List<String> merchantIds = goodsEsMapper.queryMerchantIds();
        merchantIds.parallelStream().unordered().forEach(s -> {
            String stringRedis=stringRedisTemplate.opsForValue().get("UserStoresDistance_" + s);
            stringRedisTemplate.opsForValue().set("UserStoresDistance_" + s , "");
            try {
                if(!StringUtil.isEmpty(stringRedis)){
                    //logger.info("insertUserStoresDistanceAsnc 历史数据 stringRedis {}",stringRedis.replaceAll("\\\"","").replaceAll("\\\\","'"));
                    //Map map=JacksonUtils.json3map(stringRedis.replaceAll("\\\"","").replaceAll("\\\\","'"));
                    JSONObject map = (JSONObject) JSON.parse(stringRedis.replaceAll("\\\"","").replaceAll("\\\\","'"));
                    List<Map<String, Object>> list=new ArrayList<>();
                    if(!StringUtil.isEmpty(map.get("list"))){
                        list= (List<Map<String, Object>>) map.get("list");
                        //logger.info("insertUserStoresDistanceAsnc 商户id：{},redis 历史数据数量：{}",s,list.size());
                        for (Map<String, Object> param:list) {
                            this.insertUserStoresDistance(param);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
    public void insertUserStoresDistance(Map<String,Object> param) {
        //用户
        logger.info("insertUserStoresDistanceAsnc参数：{}",param.toString());
        //没有获取到坐标就返回空
        if(StringUtil.isEmpty("Longitude")){
            return;
        }
        //经度 Longitude	地理位置经度
        double lng= Double.valueOf( param.get("Longitude")+"");
        //纬度Latitude	地理位置纬度
        double lat=Double.valueOf( param.get("Latitude")+"");
        int siteId=Integer.parseInt(param.get("siteId")+"");
        Long createTimel= (Long) param.get("createTime");
        Date createTime= new Date(createTimel);
        String openId= (String) param.get("FromUserName");
        //Coordinate newlocation=mapService.geoConvert(param.get("Longitude")+","+param.get("Latitude"));
        Coordinate coordinate = mapService.geoConvert(lng+","+lat);
        //new Coordinate( lng, lat);
        List<Store> stores =  storesService.selectStoreIdsBysiteIdand(siteId);
        SBMember member= null ;
        if("2".equals(param.get("pvType"))){
            member= bMemberService.getMembersBByAliUserID(openId,siteId);
        }else {
            member= bMemberService.getMembersBByOpenId(openId,siteId);
        }
        int userId=StringUtil.isEmpty(member)?0:member.getMember_id();
        //所有的门店和用户之间的距离
        Map<Integer, Store> storeDistanceMap = distributeOrderService.getDistributeMapNotGD(stores, coordinate);
        //最近的门店
        Store storeJ=null;
        int minDistance =0;
        if (!StringUtil.isEmpty(storeDistanceMap)) {
            Set<Integer> set = storeDistanceMap.keySet();
            Object[] obj = set.toArray();
            Arrays.sort(obj);
            minDistance = (int) obj[0];
            storeJ= storeDistanceMap.get(minDistance);
        }
        Map<String,Object> newParam=new HashedMap();
        newParam.put("siteId",siteId);
        newParam.put("createTime",createTime);
        newParam.put("updateTime",createTime);
        if(!StringUtil.isEmpty(storeJ)){
            newParam.put("storesId",storeJ.getId());
            newParam.put("storesGaodeLng",storeJ.getGaodeLng());
            newParam.put("storesGaodeLat",storeJ.getGaodeLat());
            newParam.put("storesAddress",storeJ.getAddress());
            newParam.put("storeName",storeJ.getName());
        }

        newParam.put("userId",userId);
        //newParam.put("userGaodeLng",param.get("Longitude"));
        //newParam.put("userGaodeLat",param.get("Latitude"));
        newParam.put("userGaodeLng",coordinate.getLng());
        newParam.put("userGaodeLat",coordinate.getLat());
        newParam.put("userAddress",mapService.reGeoAddress(coordinate));
        newParam.put("distributionDistance",minDistance);
        newParam.put("openId",openId);
        newParam.put("pvType",param.get("pvType"));

        logger.info("newParam-----------{}",newParam);
        Map<String,Object> map = userStoresDistanceService.insertUserStoresDistance(newParam);
        logger.info("保存成功返回结果{}",param.toString());
    }
}
