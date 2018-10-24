package com.jk51.modules.merchant.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.commons.xml.XmlUtils;
import com.jk51.modules.persistence.mapper.CoordinateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/10/27.
 */
@Service
public class CoordinateService {
    public static final Logger log = LoggerFactory.getLogger(CoordinateService.class);
    @Autowired
    private CoordinateMapper coordinateMapper;

    /**
     * 添加坐标
     *
     * @param params
     * @return
     */
    public Map<String, Object> insertCoordinate(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer i = coordinateMapper.insertCoordinate(params);
            if (i != 1) {
                map.put("msg", "添加坐标失败");
                map.put("status", -1);
                return map;
            } else {
                map.put("msg", "添加坐标成功");
                map.put("status", 0);
                return map;
            }
        } catch (Exception e) {
            log.info("添加坐标失败:{}", e);
            map.put("msg", "添加坐标失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询坐标
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectCoordinate(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            PageHelper.startPage(pageNum,pageSize);//开启分页
            List<Map<String, Object>> coordinateList = coordinateMapper.selectCoordinate(params);
            PageInfo<Map<String, Object>> allLabel = new PageInfo<>(coordinateList);
            map.put("coordinateList", allLabel);
            map.put("msg", "查询坐标成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询坐标失败:{}", e);
            map.put("msg", "查询坐标失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查根据ID询坐标
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectCoordinateById(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Map<String, Object> coordinateMap = coordinateMapper.selectCoordinateById(params);
            map.put("coordinateMap", coordinateMap);
            map.put("msg", "查询坐标成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询坐标失败:{}", e);
            map.put("msg", "查询坐标失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 修改坐标
     *
     * @param params
     * @return
     */
    public Map<String, Object> updateCoordinate(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer i = coordinateMapper.updateCoordinate(params);
            if (i != 1) {
                map.put("msg", "修改坐标失败");
                map.put("status", -1);
                return map;
            } else {
                map.put("msg", "修改坐标成功");
                map.put("status", 0);
                return map;
            }
        } catch (Exception e) {
            log.info("修改坐标失败:{}", e);
            map.put("msg", "修改坐标失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 删除坐标
     *
     * @param params
     * @return
     */
    public Map<String, Object> deleteCoordinate(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer i = coordinateMapper.deleteCoordinate(params);
            if (i != 1) {
                map.put("msg", "删除坐标失败");
                map.put("status", -1);
                return map;
            } else {
                map.put("msg", "删除坐标成功");
                map.put("status", 0);
                return map;
            }
        } catch (Exception e) {
            log.info("删除坐标失败:{}", e);
            map.put("msg", "删除坐标失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询指定范围内的会员数 & 门店数(6公里内)
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectMemberForKilometerAll(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            String time = toTime();//获取当前时间的前30天时间
            params.put("time", time);
            Integer memberCount = coordinateMapper.getMemberCountAll(params);
            Integer storeCount = coordinateMapper.getStoreCountAll(params);

            returnMap.put("memberCountAll", memberCount);
            returnMap.put("storeCountAll", storeCount);
            map.put("returnMap", returnMap);
            map.put("msg", "查询坐标成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询坐标失败:{}", e);
            map.put("msg", "查询坐标失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询指定范围内的门店数(按距离)
     * @param params
     * @return
     */
    public Map<String,Object> selectStoreForKilometer(Map<String, Object> params) {
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> idsMap = new HashMap<>();
        Map<String,Object> countMap = new HashMap<>();
        Map<String,Object> storeMap = new HashMap<>();
        try{
            Map<String,Object> storeIdMap = coordinateMapper.getStoreIdsAll(params);
            String strId1 = String.valueOf(storeIdMap.get("storeids1"));
            countMap.put("one",stringToInt(strId1));
            idsMap.put("idsOne",strId1);

            String strId2 = String.valueOf(storeIdMap.get("storeids2"));
            countMap.put("two",stringToInt(strId2));
            idsMap.put("idsTwo",strId2);

            String strId3 = String.valueOf(storeIdMap.get("storeids3"));
            countMap.put("three",stringToInt(strId3));
            idsMap.put("idshree",strId3);

            String strId4 = String.valueOf(storeIdMap.get("storeids4"));
            countMap.put("fore",stringToInt(strId4));
            idsMap.put("idsFore",strId4);

            String strId5 = String.valueOf(storeIdMap.get("storeids5"));
            countMap.put("five",stringToInt(strId5));
            idsMap.put("idsFive",strId5);

            String strId6 = String.valueOf(storeIdMap.get("storeids6"));
            countMap.put("six",stringToInt(strId6));
            idsMap.put("idsSix",strId6);

            storeMap.put("idsMap",idsMap);
            storeMap.put("countMap",countMap);

            map.put("storeMap",storeMap);
            map.put("msg","查询门店坐标成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询门店坐标失败:{}",e);
            map.put("msg","查询门店坐标失败");
            map.put("status",-1);
            return map;
        }
    }

    /**
     * 查询指定范围内的会员数(按距离)
     * @param params
     * @return
     */
    public Map<String,Object> selectMemberForKilometer(Map<String, Object> params) {
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> memberMap = new HashMap<>();
        Map<String,Object> memberListMap = new HashMap<>();
        try{
            String time = toTime();//获取当前时间的前30天时间
            params.put("time",time);
            Map<String,Object> userIdMap = coordinateMapper.getUserIdsAll(params);
            String strId1 = String.valueOf(userIdMap.get("useid1"));
            memberMap.put("one",stringToInt(strId1));
            memberListMap.put("memberListOne",strId1);

            String strId2 = String.valueOf(userIdMap.get("useid2"));
            memberMap.put("two",stringToInt(strId2));
            memberListMap.put("memberListTwo",strId2);

            String strId3 = String.valueOf(userIdMap.get("useid3"));
            memberMap.put("three",stringToInt(strId3));
            memberListMap.put("memberListThree",strId3);

            String strId4 = String.valueOf(userIdMap.get("useid4"));
            memberMap.put("fore",stringToInt(strId4));
            memberListMap.put("memberListFore",strId4);

            String strId5 = String.valueOf(userIdMap.get("useid5"));
            memberMap.put("five",stringToInt(strId5));
            memberListMap.put("memberListFive",strId5);

            String strId6 = String.valueOf(userIdMap.get("useid6"));
            memberMap.put("six",stringToInt(strId6));
            memberListMap.put("memberListSix",strId6);

            String strId7 = String.valueOf(userIdMap.get("useid7"));
            memberMap.put("seven",stringToInt(strId7));
            memberListMap.put("memberListSeven",strId7);

            String strId8 = String.valueOf(userIdMap.get("useid8"));
            memberMap.put("eight",stringToInt(strId8));
            memberListMap.put("memberListEight",strId8);

            map.put("memberMap",memberMap);
            map.put("memberIds",memberListMap);
            map.put("msg","查询会员坐标成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询会员坐标失败:{}",e);
            map.put("msg","查询会员坐标失败");
            map.put("status",-1);
            return map;
        }
    }

    //String转Int
    public Integer stringToInt(String str) {
        if (!StringUtil.isEmpty(str)){
            String[] split = str.split(",|，");
            return split.length;
        }else {
            return 0;
        }
    }

    /**
     * List<Map<String, Object>>去重
     * @param oldList
     * @return
     */
    public List<Map<String, Object>> listQvChong(List<Map<String, Object>> oldList) {
        List<Map<String, Object>> newList = new ArrayList<>();
        try {

            for (int i = 0; i < oldList.size(); i++) {
                Map<String, Object> oldMap = oldList.get(i);
                if (newList.size() > 0) {
                    boolean isContain = false;
                    for (int j = 0; j < newList.size(); j++) {
                        Map<String, Object> newMap = newList.get(j);
                        if (newMap.get("userId").equals(oldMap.get("userId"))) {
                            isContain = true;
                            continue;
                        }
                    }
                    if (!isContain) {
                        newList.add(oldMap);
                    }
                } else {
                    newList.add(oldMap);
                }
            }
            return newList;
        } catch (Exception e) {
            log.info("区域去重异常:{}", e);
            return newList;
        }

    }

    /**
     * list转String
     * @param list
     * @param separator
     * @return
     */
    public String listToString(List<String> list, char separator) {
        return org.apache.commons.lang.StringUtils.join(list.toArray(),separator);
    }

    /**
     * 获取当前时间30天之前的时间
     * @return
     */
    public String toTime (){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-30);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
