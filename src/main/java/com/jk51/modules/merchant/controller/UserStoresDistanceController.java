package com.jk51.modules.merchant.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.map.Coordinate;
import com.jk51.model.order.SBMember;
import com.jk51.model.order.Store;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.map.service.MapService;
import com.jk51.modules.merchant.service.UserStoresDistanceService;
import com.jk51.modules.order.service.DistributeOrderService;
import com.jk51.modules.store.service.BMemberService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 用户最近门店表
 */
@Controller
@RequestMapping("/label")
public class UserStoresDistanceController {
    public static final Logger logger = LoggerFactory.getLogger(UserStoresDistanceController.class);
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

    @Async
    public Future<Map<String,Object>> insertUserStoresDistanceAsnc(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        //用户
        //没有获取到坐标就返回空
        if(StringUtil.isEmpty("Longitude")){
            Map<String,Object> map = new HashedMap();
            map.put("msg","添加失败");
            return new AsyncResult<>(map);
        }
        //经度 Longitude	地理位置经度
        double lng= Double.valueOf( param.get("Longitude")+"");
        //纬度Latitude	地理位置纬度
        double lat=Double.valueOf( param.get("Latitude")+"");
        int siteId=Integer.parseInt(param.get("siteId")+"");
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
        return new AsyncResult<>(map);
    }
    /**
     * 添加：会员--门店 距离记录
     * @param request
     * @return
     */
    @RequestMapping(value="/insertUserStoresDistance")
    @ResponseBody
    public String insertUserStoresDistance(HttpServletRequest request) {
        Future<Map<String,Object>> test1 = insertUserStoresDistanceAsnc(request);
        String reStr= null;
        try {
            reStr = test1.get().toString();
        } catch (InterruptedException e) {
            logger.debug("insertUserStoresDistance 异常"+e);
        } catch (ExecutionException e) {
            logger.debug("insertUserStoresDistance ExecutionException 异常"+e);
        }
        return reStr;
    }

    /**
     * 获取所有门店所属区域
     * @param request
     * @return
     */
    @RequestMapping(value="/getStoreArea")
    @ResponseBody
    public Map<String,Object> getStoreArea(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = userStoresDistanceService.getStoreArea(param);
        return map;
    }

    /**
     * 根据用户查距离
     * @param request
     * @return
     */
    @RequestMapping(value="/getDistanceByMember")
    @ResponseBody
    public Integer getDistanceByMember(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        return userStoresDistanceService.getDistanceByMember(param);
    }


    /**
     * 根据用户ALL
     * @param request
     * @return
     */
    @RequestMapping(value="/getbMemberAll")
    @ResponseBody
    public Integer getbMemberAll(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Integer siteId=Integer.parseInt(param.get("siteId")+"");
        return bMemberService.getbMemberAll(siteId);
    }
    /**
     * 根据用户查用户信息距离门店
     * @param request
     * @return
     */
    @RequestMapping(value="/getDistanceByMemberMap")
    @ResponseBody
    public Map getDistanceByMemberMap(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
//        Integer siteId=Integer.parseInt(param.get("siteId")+""); fixme:这行代码无意义,可能会引起NumberFormatException
       // Map result = merchantService.getMerchantBySiteId(Integer.valueOf(siteId));
        //if(result.containsKey("has_erp_price")&&"1".equals(result.get("has_erp_price").toString())){
            Map<String,Object> paramap=userStoresDistanceService.getDistanceByMemberMap(param);
            if(!StringUtil.isEmpty(paramap) &&paramap.containsKey("user_address")){
                Coordinate coordinate = mapService.geoCoordinate(paramap.get("user_address").toString());
                paramap.put("areaCode",coordinate.getAdcode());
            }
            return paramap;
       // }
       // return null;

    }
}
