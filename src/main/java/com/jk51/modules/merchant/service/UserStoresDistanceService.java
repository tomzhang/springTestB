package com.jk51.modules.merchant.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.map.Coordinate;
import com.jk51.modules.map.service.MapService;
import com.jk51.modules.persistence.mapper.CustomLabelMapper;
import com.jk51.modules.persistence.mapper.UserStoresDistanceMapper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 用户最近门店表
 */
@Service
public class UserStoresDistanceService {

    public static final Logger log = LoggerFactory.getLogger(UserStoresDistanceService.class);
    @Autowired
    private UserStoresDistanceMapper userStoresDistanceMapper;
    @Autowired
    private LabelService labelService;
    @Autowired
    private CustomLabelMapper customLabelMapper;

    /**
     * 添加：会员--门店 距离记录
     * @return
     */
    public Map<String,Object> insertUserStoresDistance(Map<String,Object> params){
        Map<String,Object> map = new HashedMap();
        try{
            //String storeName = userStoresDistanceMapper.getStoreNameByStoreId(params);
            //params.put("storeName",storeName);
            userStoresDistanceMapper.insertUserStoresDistanceLog(params);//往日志表添加数据

            //判断记录表中是否有数据，没有数据添加，有数据对比
            if (0 != Integer.parseInt(String.valueOf(params.get("userId")))){
                Map<String,Object> recordMap = userStoresDistanceMapper.booleanUser(params);//查询标签中是否存在该会员
                if (StringUtil.isEmpty(recordMap)){
                    userStoresDistanceMapper.insertUserStoresDistance(params);
                }else {
                    Integer oldDistance = Integer.parseInt(String.valueOf(recordMap.get("distribution_distance")));
                    Integer newDistance = Integer.parseInt(String.valueOf(params.get("distributionDistance")));
                    if (newDistance < oldDistance){
                        userStoresDistanceMapper.updateUserStoresDistance(params);
                    }
                }
            }
            map.put("msg","添加成功");
            return map;
        }catch (Exception e){
            log.error("添加会员--门店距离失败:{}",e);
            map.put("msg","添加失败");
            return map;
        }
    }
    /**
     * 获取所有门店所属区域
     * @return
     */
    public Map<String,Object> getStoreArea(Map<String,Object> params){
        Map<String,Object> map = new HashedMap();
        List<Map<String,Object>> returnList = new ArrayList<>();
        try{
            List<Map<String,Object>> resultList = userStoresDistanceMapper.getAllStore(params);//获取商户下所有的门店信息
            //根据区域ID，将相同ID的放在一个集合里
            for (Map<String,Object> storeMap : resultList){
                Integer regoinId = Integer.parseInt(String.valueOf(storeMap.get("region_id")));
                Map<String,Object> areaMap = userStoresDistanceMapper.getArea(regoinId);
                String name = String.valueOf(areaMap.get("name"));
                Integer parentId = Integer.parseInt(String.valueOf(areaMap.get("parent_id")));
                String parentName = userStoresDistanceMapper.getParentName(parentId);

                String area = parentName+name;
                List<String> list = labelService.stringToList(String.valueOf(storeMap.get("name")));
                List<Map<String,Object>> listStoreMap = new ArrayList<>();

                for (String id : list){    //根据门店编号查询门店名称
                    Map<String,Object> mapStore = new HashedMap();
                    params.put("storesId",Integer.parseInt(id));
                    String storeName = userStoresDistanceMapper.getStoreNameByStoreId(params);
                    mapStore.put("storesId",Integer.parseInt(id));
                    mapStore.put("storeName",storeName);
                    listStoreMap.add(mapStore);
                }
                Map<String,Object> resultMap = new HashedMap();
                resultMap.put("area",area);
                resultMap.put("regoinId",regoinId);
                resultMap.put("name",listStoreMap);
                returnList.add(resultMap);
            }
            map.put("returnList",returnList);
            map.put("msg","获取所有门店所属区域成功");
            return map;
        }catch (Exception e){
            log.error("获取所有门店所属区域失败:{}",e);
            map.put("msg","获取所有门店所属区域失败");
            return map;
        }
    }

    /**
     *根据用户查距离
     * @param params
     * @return
     */
    public Integer getDistanceByMember(Map<String,Object> params) {
        try {
            String i = userStoresDistanceMapper.getDistanceByMember(params);
            if (!StringUtil.isEmpty(i)){
                return Integer.parseInt(i);
            }else {
                return -1;
            }
        }catch (Exception e){
            return  -1;
        }
    }
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MapService mapService;
    /**
     *根据用户查信息
     * @param params
     * @return
     */
    public Map getDistanceByMemberMap(Map<String,Object> params) {
        //return userStoresDistanceMapper.getDistanceByMemberMap(params);
        String stringRedis=stringRedisTemplate.opsForValue().get("UserStoresDistance_" + params.get("siteId"));
        JSONObject map = (JSONObject) JSON.parse(stringRedis.replaceAll("\\\"","").replaceAll("\\\\","'"));
        List<Map<String, Object>> list=new ArrayList<>();
        String openId=params.get("openId")+"";
        Date createTimeNew=null;
        Map<String,Object> newParam=new HashedMap();
        if(!StringUtil.isEmpty(map)&&!StringUtil.isEmpty(map.get("list"))){
            list= (List<Map<String, Object>>) map.get("list");
            for (Map<String, Object> param:list) {
                if(openId.equals(param.get("FromUserName"))){
                    Long createTimel= (Long) param.get("createTime");
                    Date createTime= new Date(createTimel);
                    if(StringUtil.isEmpty(createTimeNew)){
                        createTimeNew=createTime;
                    }
                    if(createTime.getTime()>=createTimeNew.getTime()){
                        createTimeNew=createTime;
                        newParam=this.UserStoresDistanceConvert(param);
                    }
                }
            }
            }else {
            return userStoresDistanceMapper.getDistanceByMemberMap(params);
        }
        return newParam;
    }

    public  Map<String,Object> UserStoresDistanceConvert(Map<String,Object> param) {
        //经度 Longitude	地理位置经度
        double lng= Double.valueOf( param.get("Longitude")+"");
        //纬度Latitude	地理位置纬度
        double lat=Double.valueOf( param.get("Latitude")+"");
        Coordinate coordinate = mapService.geoConvert(lng+","+lat);
        Map<String,Object> newParam=new HashedMap();
        newParam.put("user_gaode_lng",coordinate.getLng());
        newParam.put("user_gaode_lat",coordinate.getLat());
        newParam.put("user_address",mapService.reGeoAddress(coordinate));
       return newParam;
    }
}
