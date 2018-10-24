package com.jk51.modules.map.service;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.map.MapUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.map.Coordinate;
import com.jk51.model.map.GeoJson;
import com.jk51.model.map.Pois;
import com.jk51.model.order.Store;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by chen on 2017/2/16.
 */
@Service
public class MapService {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(MapService.class);

    @Value("${mapConfig.GEO_KEY}")
    private String GEO_KEY;
    @Value("${mapConfig.BD_KEY}")
    private String BD_KEY;
    @Value("${mapConfig.geoDistance}")
    private String geoDistance;
    @Value("${mapConfig.geoCoordinate}")
    private String geoCoordinate;
//    @Value("${bdDistance}")
//    private String bdDistance;
    @Value("${mapConfig.bdCoordinate}")
    private String bdCoordinate;

    @Value("${mapConfig.regeo}")
    private String reGeoUrl;
    @Value("${mapConfig.regeoA}")
    private String regeoAUrl;



    private static final String GEO_SEARCH_URL = "http://restapi.amap.com/v3/place/text?offset=10&extensions=all&key=%s&keywords=%s";//高德地图搜索接口
    private static final String GEO_CONVERT_URL = "http://restapi.amap.com/v3/assistant/coordinate/convert?locations=%s&coordsys=gps&output=JSON&key=%s";//高德地图坐标转换接口
    private static final String GEO_REGEO_URL = "http://restapi.amap.com/v3/geocode/regeo?output=json&location=%s&extensions=base&batch=true&key=%s";//高德地图逆编码
    private static final String GEO_batch_URL = "http://restapi.amap.com/v3/batch?key=%s";//批量请求接口



    /**
     *  计算距离 起点计算到终点的距离
     * @param end  终点
     * @param from 起点
     * @return
     */
    public String geoDistance(Coordinate from, Coordinate end){

        String path = String.format(geoDistance,from.toString(),end.toString(),GEO_KEY);
        String jsonStr=null;
        try {
            jsonStr =  MapUtils.sentURL(path);
            return (String) distance(jsonStr).get("0");
        } catch (Exception e) {
            logger.error("未请求到信息，检查请求链接是否可用，检查key是否可用"+jsonStr+e);
        }
        return "0";
    }
    /**
     *  批量请求接口
     * @param stores  门店集合
     * @return
     */
    public Map<Integer, Store> geoDistances(Coordinate from,List<Store> stores){

        String path = String.format(GEO_batch_URL,GEO_KEY);
        String jsonp=null;
        Map<Integer, Store> reMap=new HashedMap();
        Map<String,Store > mapS=new HashedMap();
        try {
            List< Map<String,String >> storesnew=new ArrayList< Map<String,String >>();
            Map<String,Object > mapobj=new HashedMap();
            int i=0;
            for (int j = 0; j < stores.size(); j++) {
                Store store=stores.get(j);
                Map<String,String > mapurl=new HashedMap();
                mapurl.put("url","/v3/direction/walking?origin="+from.toString()+"&destination="+store.getGaodeLng()+","+store.getGaodeLat()+"&key="+GEO_KEY);
                mapS.put(store.getGaodeLng()+","+store.getGaodeLat(),store);
                storesnew.add(mapurl);
                if(storesnew.size()==20||j==stores.size()-1){
                    mapobj.put("ops",storesnew);
                    String json=OkHttpUtil.postJson(path,JacksonUtils.mapToJson(mapobj));
                    //logger.info("批量结果：{}",json);
                    List<LinkedHashMap<String, Object>> list=JacksonUtils.json2listMap(json);;
                    for (Map m:list) {
                        //logger.info("x结果：{}",m);
                        if("200".equals(m.get("status")+"")){
                            Map mapbody= (Map) m.get("body");
                            Map route= (Map) mapbody.get("route");
                            if(!StringUtil.isEmpty(route)){
                                List paths= (ArrayList<Map>) route.get("paths");
                                Map pathmap= (Map) paths.get(0);
                                int distance=Integer.parseInt(pathmap.get("distance")+"");
                                String location=route.get("destination")+"";
                                reMap.put(distance,mapS.get(location));
                            }
                        }

                    }
                    storesnew=new ArrayList<>();
                    i++;
                }
            }

            return reMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("未请求到信息，检查请求链接是否可用，检查key是否可用"+e);
        }
        return reMap;
    }

    /**
     * 高德地理编码
     * @param address
     * @return
     */
    public Coordinate  geoCoordinate(String address){
        String path = String.format(geoCoordinate,address.replaceAll(" ",""),GEO_KEY);
        String json="";
        try {
            json = MapUtils.sentURL(path);
            JSONObject trueAddress = json2JsonObj(json,"geocodes",0);
            String level= trueAddress.getString("level");
            if("市".equals(level)||"区县".equals(level)||"省".equals(level)){
                return new Coordinate();
            }
            String  location = trueAddress.getString("location");
            String  adcode = trueAddress.getString("adcode");
            String[] coo = location.split(",");
            Coordinate coordinate= new Coordinate(Double.valueOf(coo[0]),Double.valueOf(coo[1]));
            coordinate.setAdcode(adcode);
            return  coordinate;
        } catch (Exception e) {
            logger.error("未请求到信息 ,检查key是否可用"+json+e);
        }
        return new Coordinate();
    }


    /**
     * 高德逆地理编码  判断 province:true/false ,city:true/false
     * @param coordinate1
     * @param coordinate2
     * @return
     */
    public JSONObject reGeo(Coordinate coordinate1, Coordinate coordinate2){
        //拼接请求地址
        String path = String.format(reGeoUrl,coordinate1.toString(),coordinate2.toString(),GEO_KEY);
        //返回结果  json类型的字符串
        String json = "";

        JSONObject jsonObject = new JSONObject();

        try {
            //发送请求
            json = MapUtils.sentURL(path);
            Map<String,String> code = reGeo(json);
            if(code.get("citycode1").equals(code.get("citycode2"))){
                jsonObject.put("province","true");
                if(code.get("adcode1").equals(code.get("adcode2"))){
                    jsonObject.put("city","true");
                }else{
                    jsonObject.put("city","false");
                }
            }else {
                jsonObject.put("province","false");
                jsonObject.put("city","false");
            }

        } catch (IOException e) {
            logger.error("为正确获取到信息"+e);
            jsonObject.put("message",json);
            return jsonObject;
        }
        return jsonObject;
    }

    /**
     * 百度地理编码
     * @param address
     * @return
     */
    public Coordinate bdCoordinate(String address) {
        String path = String.format(bdCoordinate,address,BD_KEY);
        String json = null;
        try {

            json = MapUtils.sentURL(path);

            //获取json对象
            JSONObject obj = JSONObject.parseObject(json).getJSONObject("result").getJSONObject("location");

            //提取数据
            String lng = obj.getString("lng");
            String lat = obj.getString("lat");

            //组装对象，返回
            return new Coordinate(Double.valueOf(lng),Double.valueOf(lat));

        } catch (Exception e) {
            logger.error("为获取到对象，key不可用或者链接失效"+json+e);
        }
        return new Coordinate();
    }

    /**
     * 百度计算直线距离
     * @param from
     * @param end
     * @return
     */
    public String bdDistance(Coordinate from,Coordinate end){
        double distance = MapUtils.getDistance(from.getLng(),from.getLat(),end.getLng(),end.getLat());
        return String.valueOf(distance);
    }


    /**
     * 将得到的json字符串转换成JSONObject对象
     * @param json
     * @return
     */
    private JSONObject json2JsonObj(String json,String key,int index) throws NullPointerException{

        JSONObject obj = JSONObject.parseObject(json);
        JSONArray geocodes = obj.getJSONArray(key);
            if(geocodes.size()==1) {
                return geocodes.getJSONObject(index);
            }
        return null;
    }

    /**
     * 获取json中的步行距离信息
     * @param jsonStr
     * @return
     */
    private Map<String,Object> distance(String jsonStr) throws NullPointerException{
        String distance="";

        JSONObject obj = JSONObject.parseObject(jsonStr);
        JSONObject route = obj.getJSONObject("route");
        JSONArray truePath = route.getJSONArray("paths");
        Map<String,Object> result = new HashMap<>();

        for (int i = 0;i<truePath.size();i++){
            JSONObject paths = truePath.getJSONObject(i);
            distance = paths.getString("distance");
            result.put(""+i,distance);
        }
        return result;
    }

    /**
     * 解析逆地理结果，获取citycode  和 adcode
     * @param jsonStr
     * @return
     */
    private Map<String,String> reGeo(String jsonStr){

        JSONObject obj = JSONObject.parseObject(jsonStr);
        JSONArray array = obj.getJSONArray("regeocodes");

        Map<String,String> code = new HashMap<>();

        for(int i = 0;i<array.size();i++){
            JSONObject obj1 = array.getJSONObject(i);
            JSONObject obj2 = obj1.getJSONObject("addressComponent");
            int j = i+1;
            code.put("citycode"+j,obj2.getString("citycode"));
            code.put("adcode"+j,obj2.getString("adcode"));
        }
        return code;
    }

    public Map<String,Object> moreDistance(Map<String, Object> param) {

        List<Coordinate> coordinates = (List<Coordinate>) param.get("storesCoo");
        Coordinate memberCoo = (Coordinate) param.get("memberCoo");

        StringBuilder sb = new StringBuilder();

        for (Coordinate c:coordinates) {
            sb.append(c.toString()+"|");
        }
        String str = sb.toString();
        String str1 = str.substring(0,str.length()-1);

        String path = String.format(geoDistance,str1,memberCoo.toString(),GEO_KEY);

        try {
            String result = MapUtils.sentURL(path);
            return distance(result);

        } catch (IOException e) {
            logger.error("获取距离失败"+e);
        }
        return null;

    }

    public GeoJson geoSearch(String keywords) {
        String url = String.format(GEO_SEARCH_URL,GEO_KEY,keywords);
        String jsonStr=null;
        try {
            jsonStr =  MapUtils.sentURL(url);
            Map map = JacksonUtils.json2map(jsonStr);
            List<Map<String,Object>> lst = (List<Map<String,Object>>) map.get("pois");
            if(lst.size()>0){
                for (int i=0; i<lst.size(); i++) {
                    Map<String,Object> map2 = lst.get(i);
                    if(StringUtils.isNullOrEmpty(map2.get("address").toString()) || map2.get("address").toString().lastIndexOf("]")>0){
                        lst.remove(i);
                        i--;
                    }
                }
                map.put("pois",lst);
                jsonStr = JacksonUtils.obj2json(map);
                GeoJson json = JacksonUtils.json2pojo(jsonStr, GeoJson.class);
                return json;
            }else{
                Coordinate coordinate = geoCoordinate(keywords);
                if(coordinate != null && coordinate.getLat()>0 && coordinate.getLng()>0){
                    GeoJson geoJson = new GeoJson();
                    List<Pois> poisLst = new ArrayList<Pois>();
                    Pois pois = new Pois();

                    pois.setAddress(keywords);
                    pois.setName(keywords);

                    poisLst.add(pois);

                    geoJson.setPois(poisLst);

                    return geoJson;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("高德地图搜索异常："+e);
        }
        return null;
    }
    /**
     * 高德逆地理编码 获取地址
     * @param coordinate
     * @return
     */
    public String  reGeoAddress(Coordinate coordinate){
        //拼接请求地址
        String path = String.format(regeoAUrl,coordinate.toString(),GEO_KEY);
        //返回结果  json类型的字符串
        String json = "";
        String address="";
        Map<String, Object> map=new HashedMap();

        try {
            //发送请求
            json = MapUtils.sentURL(path);
            logger.info("高德地图 userInfo address:{}",json);
            map=JacksonUtils.json2map(json);
            if("1".equals(map.get("status"))){
                List<Object> list=( List<Object> ) map.get("regeocodes");
                if(!StringUtil.isEmpty(list)&&list.size()!=0){
                    Map<String, Object> mapregeocodes= (Map<String, Object>) list.get(0);
                    return mapregeocodes.get("formatted_address").toString();
                }
            }else {
                return map.get("info").toString();
            }

        } catch (Exception e) {
            logger.error("为正确获取到信息"+e);
            return address;
        }
        return address;
    }
    /**
     * 高德坐标转换gps
     * @param txlocation
     * @return
     */
    public Coordinate  geoConvert(String txlocation){
        String path = String.format(GEO_CONVERT_URL,txlocation,GEO_KEY);
        String json="";
        try {
            json = MapUtils.sentURL(path);
            JSONObject trueAddress = JSONObject.parseObject(json);
            String  location = trueAddress.getString("locations");
            String[] coo = location.split(",");
            return  new Coordinate(Double.valueOf(coo[0]),Double.valueOf(coo[1]));
        } catch (Exception e) {
            logger.error("未请求到信息 ,检查key是否可用"+json+e);
        }
        return new Coordinate();
    }

    /**
     * 高德逆地理编码  判断 province:true/false ,city:true/false
     * @param coordinate
     * @return
     */
    public boolean reGeoOne(Coordinate coordinate){
        //拼接请求地址
        String path = String.format(GEO_REGEO_URL,coordinate.toString(),GEO_KEY);
        //返回结果  json类型的字符串
        String json = "";

        JSONObject jsonObject = new JSONObject();

        try {
            //发送请求
            json = MapUtils.sentURL(path);
            JSONObject obj = JSONObject.parseObject(json);
            if(obj.get("status").equals("1")){
                return true;
            }else {
                return false;
            }

        } catch (IOException e) {
            logger.error("为正确获取到信息"+e);
            jsonObject.put("message",json);
            return false;
        }
    }
}
