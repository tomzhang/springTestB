package com.jk51.modules.merchant.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.merchant.MemberLabel;
import com.jk51.modules.merchant.mapper.StaticsRecordMapper;
import com.jk51.modules.persistence.mapper.*;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:51后台会员标签
 * 作者:
 * 创建日期: 2017-06-09
 * 修改记录:
 */
@Service
public class LabelService {

    public static final Logger log = LoggerFactory.getLogger(LabelService.class);
    @Autowired
    private MemberLabelMapper memberLabelMapper;

    @Autowired
    private SBStoresMapper sbStoresMapper;
    @Autowired
    private CustomLabelMapper customLabelMapper;
    @Autowired
    private StaticsRecordMapper staticsRecordMapper;
    @Autowired
    private RelationLabelMapper relationLabelMapper;
    @Autowired
    private LabelSecondService labelSecondService;

    /**
     * 添加标签
     * @return
     */
    @Transactional
    public Map<String,Object> insertLabel(MemberLabel memberLabel){
        try {
            log.info("****SERVICE-memberLabel****:{}",JacksonUtils.obj2json(memberLabel).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String,Object> map = new HashedMap();
        Integer siteId = memberLabel.getSiteId();
        getSiteId(siteId);  //判断siteId是否有值
        try{
            Map<String,Object> param = JacksonUtils.json2map(JacksonUtils.obj2json(memberLabel));
            //获取标签下的所有会员ID并存入memberLabel
            Map<String,Object> params = getMap(param);
            List<Integer> labelList = getLabelCountInsert(params,siteId);
            Map<String,String> ids = new HashMap<>();
            String str1 = "";
            for (Integer i : labelList){
                str1 += i +",";
            }
            ids.put("userIds",str1);
            String str = JacksonUtils.mapToJson(ids);
            memberLabel.setScene(str);
            //执行添加
            Integer i = memberLabelMapper.insertLabel(memberLabel);
            if (1 == i){
                map.put("code",200);
                map.put("msg","success");
                return map;
            }
            map.put("code",500);
            map.put("msg","标签插入失败");
            return map;
        }catch (Exception e){
            log.error("会员标签插入失败:{}",e);
        }
        return map;
    }
    //添加标签，获取map
    public Map<String,Object> getMap(Map<String,Object> params){
        Map<String, Object> labelsMap = new HashedMap();
        Map<String, Object> map = new HashedMap();
       // String s = params.get("labelGroup").toString();
        String labelGroup = params.get("labelGroup").toString();
        JSONObject obj = JSONObject.parseObject(labelGroup);

        JSONArray sex0 = obj.getJSONArray("sex");
        String sex = list2String(sex0);
        JSONArray area0 = obj.getJSONArray("area");
        String area = list2String(area0);
        JSONArray age0 = obj.getJSONArray("age");
        String age = list2String(age0);

        JSONArray customLabel0 = obj.getJSONArray("customLabel");
        String customLabel = list2String(customLabel0);

        JSONArray slowLabel0 = obj.getJSONArray("slowLabel");
        String slowLabel = list2String(slowLabel0);

        JSONArray distanceLabel0 = obj.getJSONArray("distanceLabel");
        String distanceLabel = list2String(distanceLabel0);

        JSONArray storesLabel0 = obj.getJSONArray("storesLabel");
        String storesLabel = list2String(storesLabel0);
        obj.put("sex",sex);
        obj.put("area",area);
        obj.put("age",age);
        obj.put("customLabel",customLabel);
        obj.put("slowLabel",slowLabel);
        obj.put("distanceLabel",distanceLabel);
        obj.put("storesLabel",storesLabel);

            try {
                labelsMap = JacksonUtils.json2map(JacksonUtils.obj2json(obj));
            } catch (Exception e) {
                log.info("添加标签解析失败", e);
                return null;
            }
        return labelsMap;
    }
    public String list2String(JSONArray labelList){
        String str = "";
        for (Object i : labelList){
            str += i +",";
        }
        return str;
    }

    /**
     * 查询全部标签
     * @return
     */
    public Map<String,Object> getLabelAll(Integer siteId,Integer crowdSort){
        Map<String,Object> map = new HashedMap();
        getSiteId(siteId);
        try{
            List<MemberLabel> memberList = memberLabelMapper.getLabelAll(siteId,crowdSort);
            if (!StringUtil.isEmpty(memberList) && 0 != memberList.size()){
                map.put("memberList",memberList);
                map.put("code",200);
                map.put("msg","success");
                return map;
            }
            map.put("code",500);
            map.put("msg","查询失败");
            return map;
        }catch (Exception e){
            log.error("会员标签查询失败:{}",e);
        }
        return map;
    }
    /**
     * 根据ID查询单个标签
     * @return
     */
    public Map<String, Object> getLabelById(Integer siteId,Integer id,Integer crowdSort){
        Map<String, Object> map = new HashedMap();
        getSiteId(siteId);
        try{
            List<MemberLabel> memberList = memberLabelMapper.getLabelById(siteId,id,crowdSort);
            if (!StringUtil.isEmpty(memberList)){
                map.put("code",200);
                map.put("msg","success");
                map.put("memberList",memberList);
                return map;
            }
            map.put("code",500);
            map.put("msg","查询失败");
            return map;
        }catch (Exception e){
            log.error("会员标签查询失败:{}",e);
            return null;
        }
    }
    /**
     * 按标签名称模糊查询
     * @return
     */
    public Map<String,Object> getLabelByName(Integer siteId,String crowdName,Integer crowdSort){
        Map<String,Object> map = new HashedMap();
        getSiteId(siteId);
        try{
            List<MemberLabel> memberList = memberLabelMapper.getLabelByName(siteId,crowdName,crowdSort);
            if (!StringUtil.isEmpty(memberList)  && 0 != memberList.size()){
                map.put("memberList",memberList);
                map.put("code",200);
                map.put("msg","success");
                return map;
            }
            map.put("code",500);
            map.put("msg","查询失败");
            return map;
        }catch (Exception e){
            log.error("会员标签查询失败:{}",e);
        }
        return map;
    }
    /**
     * 修改标签
     * @return
     */
    @Transactional
    public Map<String,Object> updateLabel(MemberLabel memberLabel){
        Map<String,Object> map = new HashedMap();
        Integer siteId = memberLabel.getSiteId();
        getSiteId(siteId);
        try{
            String strLabel = JacksonUtils.obj2json(memberLabel);
            Map<String, Object> labelMap = JacksonUtils.json2map(strLabel);
            //获取标签下的所有会员ID并存入memberLabel
            Map<String,Object> params = getMap(labelMap);
            List<Integer> labelList = getLabelCountInsert(params,siteId);
            Map<String,String> ids = new HashMap<>();
            String str1 = "";
            for (Integer i : labelList){
                str1 += i +",";
            }
            ids.put("userIds",str1);
            String str = JacksonUtils.mapToJson(ids);
            memberLabel.setScene(str);
            Integer i = memberLabelMapper.updateLabel(memberLabel);
            if(1 == i) {
                map.put("code", 200);
                map.put("msg", "修改成功");
                return map;
            }else {
                map.put("code",500);
                map.put("msg","修改失败");
            }
        }catch (Exception e){
            log.error("会员标签修改失败:{}",e);
            map.put("code",500);
            map.put("msg","修改失败");
        }
        return map;
    }
    /**
     * 删除标签
     * @return
     */
    public Map<String,Object> deleteLabel(Integer siteId,Integer id,Integer crowdSort){
        Map<String,Object> map = new HashedMap();
        getSiteId(siteId);

        try{
            memberLabelMapper.deleteLabel(siteId,id,crowdSort);
            map.put("code",200);
            map.put("msg","删除成功");
            return map;
        }catch (Exception e){
            log.error("会员标签删除失败:{}",e);
        }
        map.put("code",500);
        map.put("msg","删除失败");
        return map;

    }

    /**
     * 获取标签人群数量
     * @return
     */
    public Map<String,Object> getLabelCount(Map<String,Object> labels) {
        Integer siteId = Integer.parseInt(String.valueOf(labels.get("siteId")));
        Map<String, Object> map = new HashedMap();
        if (!StringUtil.isEmpty(labels.get("labelsMap"))){
            Map<String, Object> labelsMap = new HashedMap();
            try {
                //labelsMap = (Map<String, Object>)labels.get("labelsMap");
                String s = labels.get("labelsMap").toString();
                labelsMap = JacksonUtils.json2map(s);
                for (String lab : labelsMap.keySet()) {
                    if (null != labelsMap && 0 != labelsMap.size() && !StringUtil.isEmpty(labelsMap.get(lab))) {
                        //年龄
                        if (!StringUtil.isEmpty(labelsMap.get("age"))) {
                            String[] ages = String.valueOf(labelsMap.get("age")).split(",");
                            Integer labelCount = 0;
                            Map<String, Object> params = getLabelCountOther(labelsMap, siteId);
                            for (String aget : ages) {
                                if ("全部年龄".equals(aget)){
                                    //Map<String, Object> params = getLabelCountOther(labelsMap, siteId);
                                    String ageMinG = formatTime(200);
                                    params.put("ageMinG",ageMinG);
                                    labelCount += memberLabelMapper.getLabelCount(params);
                                    map.put("labelCount", labelCount);
                                    map.put("code", 200);
                                    map.put("msg", "success");
                                    return map;
                                }
                                if ("不足18岁（少年）".equals(aget)) {
                                    String ageMinA = formatTime(18);
                                    params.put("ageMinA", ageMinA);
                                }
                                if ("18-24岁（青少年）".equals(aget)) {
                                    String ageMinB = formatTime(24);
                                    String ageMaxB = formatTime(18);
                                    params.put("ageMaxB", ageMaxB);
                                    params.put("ageMinB", ageMinB);
                                }
                                if ("25-34岁（青年）".equals(aget)) {
                                    String ageMinC = formatTime(34);
                                    String ageMaxC = formatTime(24);
                                    params.put("ageMaxC", ageMaxC);
                                    params.put("ageMinC", ageMinC);
                                }
                                if ("35-49岁（中年）".equals(aget)) {
                                    String ageMinD = formatTime(49);
                                    String ageMaxD = formatTime(34);
                                    params.put("ageMaxD", ageMaxD);
                                    params.put("ageMinD", ageMinD);
                                }
                                if ("50-59岁（中老年）".equals(aget)) {
                                    String ageMinE = formatTime(60);
                                    String ageMaxE = formatTime(49);
                                    params.put("ageMaxE", ageMaxE);
                                    params.put("ageMinE", ageMinE);
                                }
                                if ("60岁以上（老年）".equals(aget)) {
                                    String ageMaxF = formatTime(60);
                                    String ageMinF = formatTime(200);
                                    params.put("ageMaxF", ageMaxF);
                                    params.put("ageMinF", ageMinF);
                                }
                            }
                            labelCount += memberLabelMapper.getLabelCount(params);
                            map.put("labelCount", labelCount);
                            map.put("code", 200);
                            map.put("msg", "success");
                            return map;
                        } else {
                            Map<String, Object> params = getLabelCountOther(labelsMap, siteId);
                            if (null != params && 1 < params.size()){
                                Integer labelCount = memberLabelMapper.getLabelCount(params);
                                map.put("labelCount", labelCount);
                                map.put("code", 200);
                                map.put("msg", "success");
                                return map;
                            }else {
                                map.put("labelCount", 0);
                                map.put("code", 200);
                                map.put("msg", "success");
                            }

                        }
                    } else {
                        map.put("labelCount", 0);
                        map.put("code", 200);
                        map.put("msg", "success");
                    }
                }
                return map;
            } catch (Exception e) {
                log.error("标签查询失败",e);
                map.put("labelCount",0);
                map.put("code",500);
                map.put("msg","标签查询失败");
                return map;
            }
        }else {
            map.put("labelCount",0);
            map.put("code",200);
            map.put("msg","success");
            return map;
        }
    }

    public Map<String,Object> getLabelCountOther(Map<String,Object> labelsMap,Integer siteId){
        Map<String,Object> params = new HashedMap();
        params.put("siteId",siteId);
        try{
        //性别
        if (!StringUtil.isEmpty(labelsMap.get("sex"))){
            //String sext = String.valueOf(labelsMap.get("sex"));
            String[] sexs = String.valueOf(labelsMap.get("sex")).split(",");
            for(String sext : sexs){
                if ("男".equals(sext)){
                    params.put("sexCount0",1);
                }
                if ("女".equals(sext)){
                    params.put("sexCount1",0);
                }
            }
        }
        //地区
        if (!StringUtil.isEmpty(labelsMap.get("area"))){
            String[] areas = String.valueOf(labelsMap.get("area")).split(",");
            List<String> areaList = new ArrayList<>();
            for (String area : areas){
                if ("所有区域".equals(area)){
                    Map<String, Object> map = getAreaLabelBySiteId(siteId);
                    List<Map<Object, Object>>  result = (List<Map<Object,Object>>) map.get("result");
                    for (Map<Object, Object> res : result){
                        List<Map<Object, Object>> countries = (List)res.get("countries");
                        for (Map<Object, Object> coun : countries){
                            areaList.add(coun.get("country").toString());
                            params.put("area",areaList);
                        }
                    }
                }else {
                    areaList.add(area);
                    params.put("area",areaList);
                }
            }
        }
        //交易成功金额
        if (!StringUtil.isEmpty(labelsMap.get("successMoney"))){
            String str = String.valueOf(labelsMap.get("successMoney"));
            String strPay = str.substring(0,4);
            if ("大于等于".equals(strPay)){
                String s = str.substring(4,str.length());
                if (true == replaceSpecStr(s)){
                    params.put("payMax",Integer.parseInt(s));
                }
            }else if("小于等于".equals(strPay)){
                String s = str.substring(4,str.length());
                if (true == replaceSpecStr(s)){
                    params.put("payMin",Integer.parseInt(s));
                }
            }else {
                String s = str.substring(2,str.length());
                String[] sPay = s.split("—");
                if(2 == sPay.length){
                    if (true == replaceSpecStr(sPay[0]) && true == replaceSpecStr(sPay[1])){
                        params.put("payMax",Integer.parseInt(sPay[0]));
                        params.put("payMin",Integer.parseInt(sPay[1]));
                    }
                }
            }
        }
        //交易成功次数
        if (!StringUtil.isEmpty(labelsMap.get("successCount"))){
            String str = String.valueOf(labelsMap.get("successCount"));
            String strCount = str.substring(0,4);
            if ("大于等于".equals(strCount)){
                String s = str.substring(4,str.length());
                if (true == replaceSpecStr(s)){
                    params.put("countMax",Integer.parseInt(s));
                }
            }else if("小于等于".equals(strCount)){
                String s = str.substring(4,str.length());
                if (true == replaceSpecStr(s)){
                    params.put("countMin",Integer.parseInt(s));
                }
            }else{
                String s = str.substring(2,str.length());
                String[] sCount = s.split("—");
                if (2 == sCount.length){
                    if (true == replaceSpecStr(sCount[0]) && true == replaceSpecStr(sCount[1])){
                        params.put("countMax",Integer.parseInt(sCount[0]));
                        params.put("countMin",Integer.parseInt(sCount[1]));
                    }
                }
            }
        }
        //选定日内有购买
        if (!StringUtil.isEmpty(labelsMap.get("datePay"))){
            String str = String.valueOf(labelsMap.get("datePay"));
            String s = str.substring(1,str.length()-1);
            Integer time = Integer.parseInt(s);
            String datePay = formatDayTime(time);
            params.put("datePay",datePay);
        }
        //客单价
        if (!StringUtil.isEmpty(labelsMap.get("ticket"))){
            String str = String.valueOf(labelsMap.get("ticket"));
            String averagePriceCount = str.substring(0,4);
            if ("大于等于".equals(averagePriceCount)){
                String s = str.substring(4,str.length());
                if (true == replaceSpecStr(s)){
                    params.put("apCountMax",Integer.parseInt(s));
                }
            }else if("小于等于".equals(averagePriceCount)){
                String s = str.substring(4,str.length());
                if (true == replaceSpecStr(s)){
                    params.put("apCountMin",Integer.parseInt(s));
                }
            }else{
                String s = str.substring(2,str.length());
                String[] apCount = s.split("—");
                if (2 == apCount.length){
                    if (true == replaceSpecStr(apCount[0]) && true == replaceSpecStr(apCount[1])){
                        params.put("apCountMax",Integer.parseInt(apCount[0]));
                        params.put("apCountMin",Integer.parseInt(apCount[1]));
                    }
                }
            }
        }

            //自定义标签
            if (!StringUtil.isEmpty(labelsMap.get("customLabel"))){
                String customLabels = String.valueOf(labelsMap.get("customLabel"));
                List<String> list = stringToList(customLabels);
                params.put("customList",list);
            }
            //慢病标签
            if (!StringUtil.isEmpty(labelsMap.get("slowLabel"))){
                String slowLabels = String.valueOf(labelsMap.get("slowLabel"));
                List<String> list = stringToList(slowLabels);
                params.put("slowList",list);
            }

            //距离标签
            if (!StringUtil.isEmpty(labelsMap.get("distanceLabel"))){
                String slowLabels = String.valueOf(labelsMap.get("distanceLabel"));
                List<String> distanceList = stringToList(slowLabels);
                if (distanceList.size() > 0){
                    for (String distanceString : distanceList){
                        if (distanceString.equals("距离0~300米之间")){
                            params.put("diatanceMinA",0);
                            params.put("diatanceMaxA",300);
                        }
                        if (distanceString.equals("距离300~500米之间")){
                            params.put("diatanceMinB",300);
                            params.put("diatanceMaxB",500);
                        }
                        if (distanceString.equals("距离500~1000米之间")){
                            params.put("diatanceMinC",500);
                            params.put("diatanceMaxC",1000);
                        }
                        if (distanceString.equals("距离1公里~2公里之间")){
                            params.put("diatanceMinD",1000);
                            params.put("diatanceMaxD",2000);
                        }
                        if (distanceString.equals("距离2公里~3公里之间")){
                            params.put("diatanceMinE",2000);
                            params.put("diatanceMaxE",3000);
                        }
                        if (distanceString.equals("距离3公里~4公里之间")){
                            params.put("diatanceMinF",3000);
                            params.put("diatanceMaxF",4000);
                        }
                        if (distanceString.equals("距离4公里~5公里之间")){
                            params.put("diatanceMinG",4000);
                            params.put("diatanceMaxG",5000);
                        }
                        if (distanceString.equals("距离5公里~6公里之间")){
                            params.put("diatanceMinH",5000);
                            params.put("diatanceMaxH",6000);
                        }
                        if (distanceString.equals("距离6公里以外")){
                            params.put("diatanceMinI",6000);
                        }
                    }
                }
            }
            //门店标签
            if (!StringUtil.isEmpty(labelsMap.get("storesLabel"))){
                String storesLabels = String.valueOf(labelsMap.get("storesLabel"));
                List<String> list = stringToList(storesLabels);
                params.put("storesList",list);
            }
        return params;
        }catch (Exception e){
            log.info("用户在帮我们找bug:{}",e);
            return null;
        }
    }
    //查询人群名称是否有重复
    public Map<String,Object> getBooleanByName(Integer siteId, Integer crowdSort, String crowdName) {
        Map<String, Object> map = new HashedMap();
        getSiteId(siteId);
        try{
            List<MemberLabel> memberList = memberLabelMapper.getBooleanByName(siteId,crowdSort,crowdName);
            if (!StringUtil.isEmpty(memberList) || memberList.size() > 0){
                map.put("code",500);
                map.put("msg","标签人群名称已经存在，请重新输入");
                return map;
            }else {
                map.put("code",200);
                map.put("msg","success");
                return map;
            }
        }catch (Exception e){
            log.error("查询失败:{}",e);
            return null;
        }
    }
    //判断siteId是否为空
    public Map<String,Object> getSiteId(Integer siteId){
        Map<String,Object> map = new HashedMap();
        if (StringUtil.isEmpty(siteId)){
            map.put("code",500);
            map.put("msg","siteId接收失败");
            return map;
        }
        return null;
    }
    //时间格式转换(年份)
    public String formatTime(Integer age){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age+1);
        Date d = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(d);
        return date;
    }
    //时间格式转换(日期)
    public String formatDayTime(Integer day){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -day+1);
        Date d = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(d);
        //Timestamp ts = Timestamp.valueOf(date);
        return date;
    }

    public Map<String, Object> getAreaLabelBySiteId(Integer siteId) {
        //根据siteId 将省市取出
        Map<String,Object> rs=new HashMap<String,Object>();
        try {
            List<Map<Object, Object>> result = sbStoresMapper.getCityLabelBySiteId(siteId);
            //遍历省市，再根据市的ID，取出市包含的区，并合并到结果中
            for (Map<Object, Object> item : result) {
                List<Map<Object, Object>> country = null;
                Integer city_id = (Integer) item.get("city_id");
                if (city_id != null) {
                    country = sbStoresMapper.getAreaLabelByCityId(city_id,siteId);
                } else {

                }
                item.put("countries", country);
            }
            rs.put("result", result);
            rs.put("code", 0);
            return rs;
        }catch (Exception e){
            rs.put("code",-1);
            rs.put("result",null);
            return rs;
        }

    }
    //正则判断输入的字符是否为数字
    public static boolean  replaceSpecStr(String orgStr){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(orgStr);
        if( !isNum.matches() ){
            log.info("---getLabelCount---用户在玩我们的功能,没按照输入规则输入数字:{}",orgStr);
            return false;
        }
        return true;
    }
    /**
     * 添加标签，查询标签人群的member_id
     * @return
     */
    public List<Integer> getLabelCountInsert(Map<String,Object> labelsMap,Integer siteId) {
        if (!StringUtil.isEmpty(labelsMap)){
            try {
                for (String lab : labelsMap.keySet()) {
                    if (null != labelsMap && 0 != labelsMap.size() && !StringUtil.isEmpty(labelsMap.get(lab))) {
                        //年龄
                        if (!StringUtil.isEmpty(labelsMap.get("age"))) {
                            String[] ages = String.valueOf(labelsMap.get("age")).split(",");
                            List<Integer> labelCount = new ArrayList<>();
                            Map<String, Object> params = getLabelCountOther(labelsMap, siteId);
                            for (String aget : ages) {
                                if ("全部年龄".equals(aget)){
                                    //Map<String, Object> params = getLabelCountOther(labelsMap, siteId);
                                    String ageMinG = formatTime(200);
                                    params.put("ageMinG",ageMinG);
                                    labelCount.addAll(memberLabelMapper.getLabelCountInsert(params));
                                    return labelCount;
                                }
                                if ("不足18岁（少年）".equals(aget)) {
                                    String ageMinA = formatTime(18);
                                    params.put("ageMinA", ageMinA);
                                }
                                if ("18-24岁（青少年）".equals(aget)) {
                                    String ageMinB = formatTime(24);
                                    String ageMaxB = formatTime(18);
                                    params.put("ageMaxB", ageMaxB);
                                    params.put("ageMinB", ageMinB);
                                }
                                if ("25-34岁（青年）".equals(aget)) {
                                    String ageMinC = formatTime(34);
                                    String ageMaxC = formatTime(24);
                                    params.put("ageMaxC", ageMaxC);
                                    params.put("ageMinC", ageMinC);
                                }
                                if ("35-49岁（中年）".equals(aget)) {
                                    String ageMinD = formatTime(49);
                                    String ageMaxD = formatTime(34);
                                    params.put("ageMaxD", ageMaxD);
                                    params.put("ageMinD", ageMinD);
                                }
                                if ("50-59岁（中老年）".equals(aget)) {
                                    String ageMinE = formatTime(60);
                                    String ageMaxE = formatTime(49);
                                    params.put("ageMaxE", ageMaxE);
                                    params.put("ageMinE", ageMinE);
                                }
                                if ("60岁以上（老年）".equals(aget)) {
                                    String ageMaxF = formatTime(60);
                                    String ageMinF = formatTime(200);
                                    params.put("ageMaxF", ageMaxF);
                                    params.put("ageMinF", ageMinF);
                                }
                            }
                            labelCount.addAll( memberLabelMapper.getLabelCountInsert(params));
                            return labelCount;
                        } else {
                            Map<String, Object> params = getLabelCountOther(labelsMap, siteId);
                            if (null != params && 1 < params.size()){
                                List<Integer> labelCount = memberLabelMapper.getLabelCountInsert(params);
                                return labelCount;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("标签member_id查询失败",e);
                return null;
            }
        }
        return null;
    }

    /**
     * 添加自定义标签
     * @param customLabel
     * @return
     */
    public Map<String,Object> insertCustomLabel(Map<String,Object> customLabel){
        Map<String,Object> map = new HashMap<>();
        try{
            //获取登录者的身份
            String creater = getLoginData(customLabel);
            customLabel.put("creater",creater);
            Integer i = customLabelMapper.insertCustomLabel(customLabel);
            if (1 == i){
                map.put("msg","自定义标签添加成功");
                map.put("status",0);
                return map;
            }else {
                map.put("msg","自定义标签添加失败");
                map.put("status",-1);
                return map;
            }
        }catch (Exception e){
            log.info("自定义标签添加失败:{}",e);
            map.put("msg","自定义标签添加失败");
            map.put("status",-1);
            return map;
        }
    }

    /**
     * 查询商户下所有自定义标签
     * @param mapCustom
     * @return
     */
    public Map<String,Object> selectCustomAll(Map<String,Object> mapCustom){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(String.valueOf(mapCustom.get("siteId")));
            List<Map<String,Object>> customLabelListAll = customLabelMapper.selectCustomAll(mapCustom);


            if (!StringUtil.isEmpty(customLabelListAll) && 0 < customLabelListAll.size()){
                for (Map<String,Object> customMap : customLabelListAll){
                    if (!StringUtil.isEmpty(customMap.get("name"))){
                        String labelName = String.valueOf(customMap.get("name"));
                        Integer count = relationLabelMapper.getMemberCount(siteId,labelName);
                        customMap.put("count",count);
                    }
                }
            }
            customLabelListAll = labelSecondService.getYingyongList(customLabelListAll,mapCustom,"custom");

            map.put("msg","自定义标签查询成功");
            map.put("status",0);
            map.put("customLabelListAll",customLabelListAll);
            return map;
        }catch (Exception e){
            log.info("自定义标签查询异常:{}",e);
            map.put("msg","自定义标签查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     * 根据ID查询单个自定义标签
     * @param mapCustom
     * @return
     */
    public Map<String,Object> selectCustomById(Map<String,Object> mapCustom){
        Map<String,Object> map = new HashMap<>();
        try{
            Map<String,Object> customLabel = customLabelMapper.selectCustomById(mapCustom);
            map.put("msg","根据ID查询单个自定义标签成功");
            map.put("status",0);
            map.put("customLabelById",customLabel);
            return map;
        }catch (Exception e){
            log.info("根据ID查询单个自定义标签异常:{}",e);
            map.put("msg","根据ID查询单个自定义标签异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     * 修改自定义标签
     * @param customLabel
     * 如果标签名称修改，同时修改标签所在的自定义人群中的标签名称
     * @return
     */
    @Transactional
    public Map<String,Object> updateCustom(Map<String,Object> customLabel){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(customLabel.get("siteId").toString());
            //获取登录者的身份
            String creater = getLoginData(customLabel);
            customLabel.put("modifier",creater);

            //先获取标签名称，用于对比
            Map<String, Object> customMap = customLabelMapper.selectCrowdSortmById(customLabel);
            String customName = customMap.get("label_name").toString();

            Integer type = 0;//用来标记自定义标签名称是否发生修改:0没有发生修改，1发生修改
            String labelName = "";//修改后的名称

            //如果名称修改了，修改会员的标签集合里的标签名称,修改关系表中的标签名称
            if (!StringUtil.isEmpty(customLabel.get("labelName")) && !customName.equals(customLabel.get("labelName").toString())){
                type = 1;
                labelName = customLabel.get("labelName").toString(); //修改后的名称
                Integer i = relationLabelMapper.updateLabelName(siteId,labelName,customName);
                Integer j = customLabelMapper.updateCustom(customLabel);
            }else {
                Integer j = customLabelMapper.updateCustom(customLabel);
            }

            //如果名称修改了，修改会员的标签集合里的标签名称
            if (type == 1){
                labelSecondService.updateName(labelName,customName,"custom",customLabel);
            }

            map.put("msg","自定义标签修改成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("自定义标签修改失败:{}",e);
            map.put("msg","自定义标签修改失败");
            map.put("status",-1);
            return map;
        }
    }

    /**
     * 删除自定义标签
     * @param customLabel
     * @return
     */
    @Transactional
    public Map<String,Object> deleteCustom(Map<String,Object> customLabel){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(customLabel.get("siteId").toString());
            Integer id = Integer.parseInt(customLabel.get("id").toString());
            String customName =  customLabelMapper.getNameById(siteId,id);
            //String customName = customLabel.get("customName").toString();
            //修改自定义标签删除状态
            Integer i = customLabelMapper.deleteCustom(customLabel);
//            Map<String,Object> customStrMap = customLabelMapper.selectCrowdSortmById(customLabel);

            //修改关系表中记录的状态
            Integer p = relationLabelMapper.deleteCustemLabelName(siteId,customName);

            //修改自定义人群删除状态
            labelSecondService.updateMemberLabeltoDelete(customLabel,customName,"custom");


            map.put("msg","删除自定义标签成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("删除自定义标签异常:{}",e);
            map.put("msg","删除自定义标签异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     * 判断数据库表中是否存在这个自定义标签名称
     * @param customLabel
     * @return
     */
    public Map<String,Object> booleanCustom(Map<String,Object> customLabel){
        Map<String,Object> map = new HashMap<>();
        try{
            List<Map<String,Object>> list = customLabelMapper.booleanCustom(customLabel);
            if (0 < list.size()){
                for (Map<String,Object> nameMap : list){
                    if (nameMap.containsKey("id") && !StringUtil.isEmpty(nameMap.get("id"))){
                        Integer oldId = Integer.parseInt(nameMap.get("id").toString());
                        Integer newId = Integer.parseInt(customLabel.get("id").toString());
                        if (oldId == newId){
                            map.put("msg","标签名称可以使用");
                            map.put("status",0);
                            return map;
                        }else {
                            map.put("msg","标签名称已存在");
                            map.put("status",-2);
                            return map;
                        }
                    }
                }
                map.put("msg","标签名称已存在");
                map.put("status",-2);
                return map;
            }else {
                map.put("msg","标签名称可以使用");
                map.put("status",0);
                return map;
            }
        }catch (Exception e){
            log.info("判断自定义标签是否存在—异常:{}",e);
            map.put("msg","判断自定义标签是否存在—异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     * 查看自定义标签中的所有会员
     * @param customLabel
     * site_id
     * id 自定义标签ID
     * @return
     */
    public Map<String,Object> getMemberAllByCustom(Map<String,Object> customLabel){
        Map<String,Object> map = new HashMap<>();
        Integer siteId = Integer.parseInt(customLabel.get("siteId").toString());
        Integer type = 1;
        try{
            Map<String,Object> customStrMap = customLabelMapper.selectCrowdSortmById(customLabel);
            if (!StringUtil.isEmpty(customStrMap.get("label_name"))){
                String labelName = String.valueOf(customStrMap.get("label_name"));
                List<Integer> idsList = relationLabelMapper.getmemberIds(siteId,labelName,type);
                List<Map<String,Object>> memberMap = customLabelMapper.selectMemberByMemberId(siteId,idsList);
                Collections.reverse(memberMap);
                map.put("msg","查看自定义标签中的所有会员成功");
                map.put("status",0);
                map.put("memberMap",memberMap);
                return map;
            }else {
                map.put("msg","该标签中改没有添加会员");
                map.put("status",0);
                map.put("memberMap",null);
                return map;
            }

            //先获取会员ID集合
            /*Map<String,Object> customStrMap = customLabelMapper.selectCrowdSortmById(customLabel);
            if (!StringUtil.isEmpty(customStrMap.get("member_ids"))){
                Map<String,Object> cusMap = JacksonUtils.json2map(customStrMap.get("member_ids").toString());
                String ids = cusMap.get("userIds").toString();
                List<String> list = stringToList(ids);
                List<Map<String,Object>> memberMap = customLabelMapper.selectMemberByMemberId(siteId,list);
                map.put("msg","查看自定义标签中的所有会员成功");
                map.put("status",0);
                map.put("memberMap",memberMap);
                return map;
            }else {
                map.put("msg","该标签中改没有添加会员");
                map.put("status",0);
                map.put("memberMap",null);
                return map;
            }*/
        }catch (Exception e){
            log.info("查看自定义标签中的所有会员异常:{}",e);
            map.put("msg","查看自定义标签中的所有会员异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     * 根据输入字符进行模糊查询
     * @param customLabel
     * @return
     */
    public Map<String,Object> getDimByCustom(Map<String,Object> customLabel){
        Map<String,Object> map = new HashMap<>();
        try{
            String labelname = String.valueOf(customLabel.get("labelName"));
            String lname = labelname.replaceAll(" ","");
            customLabel.put("labelname",lname);
            List<Map<String,Object>> customLabelMap = customLabelMapper.getDimByCustom(customLabel);
            map.put("msg","查询成功");
            map.put("status",0);
            map.put("customLabelMap",customLabelMap);
            return map;
        }catch (Exception e){
            log.info("自定义标签模糊查询异常:{}",e);
            map.put("msg","自定义标签模糊查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *会员修改——查询回显
     * @param customLabel
     * @return
     */
    public Map<String,Object> memberLabelUpdateEcho(Map<String,Object> customLabel){
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> returnMap = new HashMap<>();
        Integer siteId = Integer.parseInt(customLabel.get("siteId").toString());
        try{
            //当前所属人群分组
            List<String> listCrowd = new ArrayList<>();
            String memberID =  customLabel.get("memberId").toString();
            List<Map<String,Object>> crowdList = customLabelMapper.getCrowdAll(siteId);
            if (!StringUtil.isEmpty(crowdList)){
                for (Map<String,Object> crowdMap : crowdList){
                    if (!crowdMap.containsKey("scene") && !StringUtil.isEmpty(crowdMap.get("scene"))){
                        Map<String,Object> labelMap = JacksonUtils.json2map(crowdMap.get("scene").toString());
                        String userIds = labelMap.get("userIds").toString();
                        List<String> stringList = stringToList(userIds);
                        if (stringList.contains(memberID)){
                            listCrowd.add(crowdMap.get("name").toString());
                        }
                    }
                }
                map.put("listCrowd",listCrowd);
            }else {
                map.put("listCrowd",null);
            }

            //基础标签
            Map<String,Object> basicsMap = new HashMap<>();
            Map<String,Object> memberMap = customLabelMapper.getBasicsMap(customLabel);
            String date = memberMap.get("birthday").toString();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//24小时制
            Long time = simpleDateFormat.parse(date).getTime();
            Long time1 = System.currentTimeMillis();
            Long time2 = time1 - time;
            String time3 = Long.toString(time2);
            Double time4 = Double.parseDouble(time3);
            String age = Double.toString(time4/1000/60/60/24/365);
            age = age.substring(0,age.indexOf("."));
            if (200 < Integer.parseInt(age)){
                basicsMap.put("age","保密");//年龄
            }else{
                basicsMap.put("age",age);//年龄
            }

            if (StringUtil.isEmpty(memberMap.get("sex"))){
                basicsMap.put("sex","保密");//性别
            }else {
                Integer i = Integer.parseInt(String.valueOf(memberMap.get("sex")));
                if (0 == i){
                    basicsMap.put("sex","女");
                }else if(1 == i){
                    basicsMap.put("sex","男");
                }else {
                    basicsMap.put("sex","保密");
                }
            }
            String area = "";
            if (memberMap.containsKey("address")){
                String address = String.valueOf(memberMap.get("address"));
                if (!StringUtil.isEmpty(address) && address.lastIndexOf("区") != -1){
                    area = address.substring(0,address.lastIndexOf("区"));
                }else {
                    area = getAddress(memberMap);
                }
            }else {
                area = getAddress(memberMap);
            }
            basicsMap.put("area",area);
            map.put("basicsMap",basicsMap);


            //交易标签
            Map<String,Object> tradsMap = customLabelMapper.getTrandsCount(customLabel);
            Map<String,Object> timeMap = customLabelMapper.getTime(customLabel);
            String str = timeMap.get("time").toString();
            String timestr;
            if ("0".equals(str)){
                timestr = "无购买记录";
            }else {
                timestr = str.substring(0,str.indexOf(" "));
            }
            tradsMap.put("lastTime",timestr);
            map.put("tradsMap",tradsMap);

            //自定义标签
            Integer buyerId = customLabelMapper.getBuyerIdByMember(siteId, memberID);
            List<Map<String,Object>> customLabelList = relationLabelMapper.getRelationLabelForCustom(siteId,buyerId);
            if (0 < customLabelList.size()){
                listSort(customLabelList);
                map.put("listCustom",customLabelList);
            }else {
                map.put("listCustom",null);
            }


            //慢病标签
            List<Map<String,Object>> slowLabelList = relationLabelMapper.getRelationLabelForSlow(siteId,buyerId);
            if (0 < slowLabelList.size()){
                listSort(slowLabelList);
                map.put("listCustomSlow",slowLabelList);
            }else {
                map.put("listCustomSlow",null);
            }

            returnMap.put("memberMap",map);
            returnMap.put("msg","会员修改——查询回显成功");
            returnMap.put("status",0);
            return returnMap;
        }catch (Exception e){
            log.info("会员修改——查询回显异常:{}",e);
            returnMap.put("msg","会员修改——查询回显异常");
            returnMap.put("status",-1);
            return returnMap;
        }
    }

    //获取基础信息，区域信息
    public String getAddress(Map<String,Object> memberMap){
        try{
            String area;
            String city = "";
            String country = "";
            String province = "";
            if (!StringUtil.isEmpty(memberMap.get("province"))){
                province = memberMap.get("province").toString();
            }
            if (!StringUtil.isEmpty(memberMap.get("city"))){
                city = memberMap.get("city").toString();
            }
            if (!StringUtil.isEmpty(memberMap.get("country"))){
                country = memberMap.get("country").toString();
            }
            area = province+city+country;
            if (!StringUtil.isEmpty(area)){
                area = province+" "+city+" "+country;
            }else {
                area = "未知";
            }
            return area;
        }catch (Exception e){
            log.info("查询会员区域失败:{}",e);
            return "未知";
        }

    }

    /**
     *会员修改——修改标签
     * @param customLabel
     * @return
     */
    @Transactional
    public Map<String,Object> memberLabelUpdate(Map<String,Object> customLabel){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(customLabel.get("siteId").toString());
            Integer memberID =  Integer.parseInt(customLabel.get("memberId").toString());
            Integer buyerId =  customLabelMapper.getBuyerIdByMember(siteId,customLabel.get("memberId").toString());
            //Integer buyerId =  Integer.parseInt(customLabel.get("buyerId").toString());
            List<String> stringList = new ArrayList<>();
            /*List<String> newIdsList = new ArrayList<>();
            //查询商户下所有的自定义标签，
            List<Map<String, Object>> customLabelAll = customLabelMapper.getCustomLabelAll(siteId);*/
            // 将含有该会员ID的标签找到，

           /* String stringNames = "";
            if (!StringUtil.isEmpty(customLabelAll) && 0 < customLabelAll.size()){
                //获取所有标签的ID集合和标签名称集合
                for (Map<String, Object> customMap : customLabelAll){
                    if (customMap.containsKey("ids") && !StringUtil.isEmpty(customMap.get("ids"))){
                        List<String> memberIdsList = stringToList(customMap.get("ids").toString());
                        //获取单个标签的会员ID集合
                        String stringIds = "";
                        if (memberIdsList.contains("memberID")){
                            for (String memberId : memberIdsList){
                                if (memberID != Integer.parseInt(memberId)){
                                    stringIds += memberId + ",";
                                }
                            }
                            //根据site_id和标签名称修改单个标签的会员ID集合
                            String name = customMap.get("name").toString();
                            Integer i = customLabelMapper.updateCustomByName(siteId,name,stringIds);
                            //遍历改商户下所有的自定义人群
                            List<Map<String, Object>> crowdAll = customLabelMapper.getCrowdAll(siteId);
                            if (!StringUtil.isEmpty(crowdAll) && 0 < crowdAll.size()){
                                for (Map<String, Object> crowdMap : crowdAll){
                                    //修改自定义人群和
                                }
                            }
                        }
                    }
                }
            }*/

            // 去掉该会员ID
            //重新将会员ID集合放回去，同时修改该会员下的自定义标签组，自定义标签的会员ID集合，自定义人群的自定义标签，自定义人群的所有会员ID集合

            //查询该会员现有的标签
            String labelstr = customLabelMapper.getLabels(siteId,buyerId);
            if (!StringUtil.isEmpty(labelstr)){
                stringList = stringToList(labelstr);
                //根据标签名称，将自定义标签中的该会员ID删掉
                for (String labelName : stringList){
                    //根据标签名称查询ids
                    String ids = customLabelMapper.getIDSByName(siteId,labelName);
                    if (!StringUtil.isEmpty(ids)){
                        Map<String, Object> mapIds = JacksonUtils.json2map(ids);
                        String userIds = mapIds.get("userIds").toString();
                        List<String> idsList = stringToList(userIds);
                        String idstr = "";
                        for (String id : idsList){
                            if (!StringUtil.isEmpty(id) && memberID != Integer.parseInt(id)){
                                idstr += id + ",";
                            }
                        }
                        Map<String,Object> idsMap = new HashMap<>();
                        idsMap.put("userIds",idstr);
                        String str1 = JSON.toJSONString(idsMap);
                        Integer i = customLabelMapper.updateCustomByName(siteId,labelName,str1);
                    }
                }
            }
            //新添加标签
            //将新的慢病标签添加到会员中
            if (customLabel.containsKey("customSlow")){
                String customSlow = String.valueOf(customLabel.get("customSlow"));
                Integer q = customLabelMapper.updateTagLabels(siteId,buyerId,customSlow);
            }

            //将新的自定义标签集合修改到会员表中
            String nameList;
            if (customLabel.containsKey("custom")){
                nameList = customLabel.get("custom").toString();//-----------------------------------------------在给会员存储自定义标签的时候以 ， 号隔开存储，键名为 nameList
                Integer q = customLabelMapper.updateLabels(siteId,buyerId,nameList);
            }else {
                nameList = "";
                Integer q = customLabelMapper.updateLabels(siteId,buyerId,nameList);
            }

            //将该会员ID修改添加到每个标签中
            if (!StringUtil.isEmpty(nameList)){
                List<String> listName = stringToList(nameList);
                for (String lname : listName){
                    String ids = customLabelMapper.getIDSByName(siteId,lname);
                    String idstr;
                    if (!StringUtil.isEmpty(ids)){
                        Map<String, Object> mapIds = JacksonUtils.json2map(ids);
                        idstr = mapIds.get("userIds").toString();
                    }else {
                        idstr = "";
                    }
                    idstr += memberID + ",";
                    Map<String,Object> newMap = new HashMap<>();
                    newMap.put("userIds",idstr);
                    idstr = JSON.toJSONString(newMap);
                    Integer i = customLabelMapper.updateCustomByName(siteId,lname,idstr);
                }
            }
            map.put("msg","会员修改成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("会员修改异常:{}",e);
            map.put("msg","会员修改异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *会员列表--门店后台
     * @param customLabel
     * @return
     */
    @Transactional
    public Map<String,Object> getAllMember(Map<String,Object> customLabel){
        Map<String,Object> map = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(String.valueOf(customLabel.get("siteId")));
            Integer storeId = Integer.parseInt(String.valueOf(customLabel.get("storeId")));
            if (customLabel.containsKey("registerCode")){
                String registerCode = customLabel.get("registerCode").toString();
                String newRegisterCode = storeId + "_" + registerCode;
                customLabel.put("registerCode",newRegisterCode);
            }
            if (customLabel.containsKey("orderCode")){
                String orderCode = customLabel.get("orderCode").toString();
                String newOrderCode = storeId + "_" + orderCode;
                customLabel.put("orderCode",newOrderCode);
            }
            int startRow=Integer.parseInt(StringUtils.isEmpty(customLabel.get("startRow"))?"0":customLabel.get("startRow").toString()) ;
            int pageSize=Integer.parseInt(StringUtils.isEmpty(customLabel.get("pageSize"))?"10":customLabel.get("pageSize").toString()) ;
            PageHelper.startPage(startRow, pageSize);//开启分页
            customLabel.put("siteId",siteId);
            customLabel.put("storeId",storeId);
            //根据标签名称查询：如果存在自定义标签中，按自定义标签查询，不存在按慢病标签查询
            if (customLabel.containsKey("labelName")){
                if (!StringUtil.isEmpty(customLabel.get("labelName"))){
                    Map<String,Object> cMap = new HashMap<>();
                    cMap.put("siteId",siteId);
                    List<String> customLabelBySiteId = customLabelMapper.getCustomLabelBySiteId(cMap);
                    String name = customLabel.get("labelName").toString();
                    if (customLabelBySiteId.contains(name)){
                        customLabel.put("customName",name);
                    }else {
                        customLabel.put("tagName",name);
                    }
                }
            }
            List<Map<String,Object>> allMemberList = customLabelMapper.getAllMember(customLabel);

            map.put("memberPage", new PageInfo<>(allMemberList));
            map.put("allMemberList",allMemberList);
            map.put("msg","查询所有会员成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询所有会员异常:{}",e);
            map.put("msg","查询所有会员异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *APP--根据会员ID获取会员的慢病标签和自定义标签
     * @param customLabel
     * siteId
     * buyerId
     * storeadminId
     * @return
     */
    @Transactional
    public Map<String,Object> getLabelSlowAndCustom(Map<String,Object> customLabel){
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> labelMap = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(String.valueOf(customLabel.get("siteId")));
            Integer buyerId = Integer.parseInt(String.valueOf(customLabel.get("buyerId")));
            Integer storeadminId = Integer.parseInt(String.valueOf(customLabel.get("storeadminId")));

            //自定义标签
            List<Map<String, Object>> relationLabelForCustomList = relationLabelMapper.getRelationLabelForCustom(siteId, buyerId);
//            List<String> customList = getNameList(relationLabelForCustomList,storeadminId);//使用店员ID过滤
            List<String> customList = getNameList2(relationLabelForCustomList);

            //慢病标签
            List<Map<String, Object>> relationLabelForSlowList = relationLabelMapper.getRelationLabelForSlow(siteId, buyerId);
//            List<String> slowList = getNameList(relationLabelForSlowList,storeadminId);//使用店员ID过滤
            List<String> slowList = getNameList2(relationLabelForSlowList);

            customList.addAll(slowList);    //将集合合并起来
            map.put("allMemberList",customList);
            map.put("msg","获取会员的慢病标签和自定义标签成功");
            map.put("status",0);
            return map;

        }catch (Exception e){
            log.info("查询所有会员异常:{}",e);
            map.put("msg","获取会员的慢病标签和自定义标签异常");
            map.put("status",-1);
            return map;
        }
    }

    public List<String> getNameList2(List<Map<String,Object>> relationLabelForSlowList){
        List<String> slowList = new ArrayList<>();
        if (0 < relationLabelForSlowList.size()){
            for (Map<String, Object> relationLabelForSlowMap :relationLabelForSlowList ){
                if (!StringUtil.isEmpty(relationLabelForSlowMap.get("labelName")) ){
                    slowList.add(String.valueOf(relationLabelForSlowMap.get("labelName")));
                }
            }
        }
        return slowList;
    }

    //获取自定义标签和慢病标签的名称集合
    public List<String> getNameList(List<Map<String,Object>> relationLabelForSlowList,Integer storeadminId){
        List<String> slowList = new ArrayList<>();
        if (0 < relationLabelForSlowList.size()){
            for (Map<String, Object> relationLabelForSlowMap :relationLabelForSlowList ){
                if (!StringUtil.isEmpty(relationLabelForSlowMap) && !StringUtil.isEmpty(relationLabelForSlowMap.get("storeadminIds"))){
                    String storeadminIds = relationLabelForSlowMap.get("storeadminIds").toString();
                    if (!StringUtil.isEmpty(storeadminIds)){
                        List<String> storeadminList = stringToList(storeadminIds);
                        if (storeadminList.contains(storeadminId.toString())){
                            slowList.add(String.valueOf(relationLabelForSlowMap.get("labelName")));
                        }
                    }
                }
            }
        }
        return slowList;
    }

    //获取自定义标签和慢病标签的名称集合
    public List<String> getNameList(List<Map<String,Object>> relationLabelForSlowList){

        List<String> slowList = new ArrayList<>();
        if (!StringUtil.isEmpty(relationLabelForSlowList)){
            for (Map<String, Object> relationLabelForSlowMap :relationLabelForSlowList ){
                if (!StringUtil.isEmpty(relationLabelForSlowMap)&&!StringUtil.isEmpty(relationLabelForSlowMap.get("labelName"))){

                    slowList.add(String.valueOf(relationLabelForSlowMap.get("labelName")));

                }
            }
        }
        return slowList;
    }

    /**
     *APP--根据site_id查询改商户下所有自定义标签
     * @param customLabel
     * @return
     */
    @Transactional
    public Map<String,Object> getCustomLabelBySiteId(Map<String,Object> customLabel){
        Map<String,Object> map = new HashMap<>();
        try{
            List<String> customList = customLabelMapper.getCustomLabelBySiteId(customLabel);
            Collections.reverse(customList);
            map.put("allMemberList",customList);
            map.put("msg","查询改商户下所有自定义标签成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询所有会员异常:{}",e);
            map.put("msg","查询改商户下所有自定义标签异常");
            map.put("status",-1);
            return map;
        }
    }
    /**
     *APP--修改用户标签
     * @param customLabel
     * @return
     */
    /*@Transactional
    public Map<String,Object> updateLabelByMemberId(Map<String,Object> customLabel){
        Map<String,Object> map = new HashMap<>();
        try{
            //修改会员的慢病标签
            Integer i = customLabelMapper.updateSlowLabel(customLabel);
            //修改会员的自定义标签
            Map<String,Object> lMap = memberLabelUpdate(customLabel);
            return lMap;
        }catch (Exception e){
            log.info("修改会员标签异常:{}",e);
            map.put("msg","修改会员标签异常");
            map.put("status",-1);
            return map;
        }
    }*/


    /**
     *APP--店员修改会员标签
     * @param customLabel
     * siteId
     * memberId
     * custom
     * tag
     * storeadminId
     * labelType  1：自定义标签，0：慢病标签
     * xCount=1
     * @return
     */
    @Transactional
    public Map<String,Object> updateLabelByMemberId(Map<String,Object> customLabel){
        Map<String, Object> map = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(customLabel.get("siteId")));
            Integer xCount = Integer.parseInt(String.valueOf(customLabel.get("xCount")));
            Integer storeadminId = Integer.parseInt(String.valueOf(customLabel.get("storeadminId")));
            Integer buyerId = Integer.parseInt(String.valueOf(customLabel.get("buyerId")));
            List<String> oldCustomList = relationLabelMapper.getStoreadminToMember(siteId,buyerId,storeadminId,1);
            List<String> oldSlowList = relationLabelMapper.getStoreadminToMember(siteId,buyerId,storeadminId,0);

            //处理自定义标签的添加
            List<String> newCustomList = new ArrayList<>();
            if (!StringUtil.isEmpty(customLabel.get("custom"))){
                String custom = String.valueOf(customLabel.get("custom"));
                newCustomList = stringToList(custom);
            }
            Integer i = disposeLabel(oldCustomList,newCustomList,siteId,buyerId,xCount,storeadminId,1);


            //处理慢病标签的添加
            List<String> newslowList = new ArrayList<>();
            if (!StringUtil.isEmpty(customLabel.get("tag"))){
                String slow = String.valueOf(customLabel.get("tag"));
                newslowList = stringToList(slow);
            }
            Integer j = disposeLabel(oldSlowList,newslowList,siteId,buyerId,xCount,storeadminId,0);


            if (i == 0 && i == j){
                map.put("msg", "修改成功");
                map.put("status",0);
                return map;
            }else {
                map.put("msg", "修改异常");
                map.put("status",-1);
                return map;
            }
        }catch (Exception e){
            log.info("APP修改标签失败:{}",e);
            map.put("msg", "修改异常");
            map.put("status",-1);
            return map;
        }
    }

    //处理自定义标签和慢病标签
    @Transactional
    public Integer disposeLabel(List<String> oldCustomList,List<String> newCustomList,Integer siteId,Integer buyerId,Integer xCount,Integer storeadminId,Integer labelType){
        try{
            if (0 < oldCustomList.size()){  //对比新标签组和老标签组的差别，多的增加，少的删除
                List<String> deleteList = new ArrayList<>();
                List<String> insertList = new ArrayList<>();
                for (String deleteName : oldCustomList){
                    if (!newCustomList.contains(deleteName)){
                        deleteList.add(deleteName);
                    }
                }
                for (String insertName : newCustomList){
                    if (!oldCustomList.contains(insertName)){
                        insertList.add(insertName);
                    }
                }
                //遍历删除标签
                if (0 < deleteList.size()){
                    for (String labelName : deleteList){
                        Map<String,Object> storeadminIdsAndCount = relationLabelMapper.getStoreadminIdsAndByLbelName(siteId,buyerId,labelName);
                        String storeadminIds = String.valueOf(storeadminIdsAndCount.get("storeadminIds"));
                        Integer storeadminCount = Integer.parseInt(String.valueOf(storeadminIdsAndCount.get("storeadminCount")));

                        Integer count = 0;
                        if (0 < storeadminCount){
                            count = storeadminCount - xCount;
                        }
                        List<String> newStoreadminList = new ArrayList<>();
                        List<String> storeadminIdList = stringToList(storeadminIds);
                        for (String newId : storeadminIdList){
                            if (storeadminId != Integer.parseInt(newId)){
                                newStoreadminList.add(newId);
                            }
                        }
                        if (0 < newStoreadminList.size()){
                            String str = "";
                            for (String adminId : newStoreadminList){
                                str += adminId + ",";
                            }
                            Integer i = relationLabelMapper.deleteCountAndStoreadminId(siteId,buyerId,labelName,str,count);
                        }else {
                            Integer i = relationLabelMapper.deleteCountAndStoreadminId(siteId,buyerId,labelName,"",count);
                        }

                    }
                }

                //遍历添加标签
                if (0 < insertList.size()){
                    List<String> nameList = relationLabelMapper.getLabelNameByBuyerId(siteId,buyerId,labelType);
                    for (String labelName : insertList){
                        if (!StringUtil.isEmpty(labelName.replace(" ","")) && nameList.contains(labelName)){   //包含修改
                            Map<String,Object> storeadminIdsAndCount = relationLabelMapper.getStoreadminIdsAndByLbelName(siteId,buyerId,labelName);
                            String storeadminIds = String.valueOf(storeadminIdsAndCount.get("storeadminIds"));
                            Integer storeadminCount = Integer.parseInt(String.valueOf(storeadminIdsAndCount.get("storeadminCount")));

                            Integer count = storeadminCount + xCount;
                            /*List<String> oldStoreadminIdsList = stringToList(storeadminIds);
                            oldStoreadminIdsList.add(storeadminId.toString());*/
                            String newStoreadminIdsList = storeadminIds + storeadminId + ",";
                            Integer i = relationLabelMapper.deleteCountAndStoreadminId(siteId,buyerId,labelName,newStoreadminIdsList,count);
                        }else { //不包含添加
                            Integer i = relationLabelMapper.insertRelationLabel(siteId,buyerId,labelName,storeadminId+",",xCount,labelType);
                        }
                    }
                }
            }else { //如果该店员还没有给该会员添加任何标签，则直接添加
                for (String labelName : newCustomList){
                    if (!StringUtil.isEmpty(labelName.replace(" ",""))){
                        //获取该会员下所有的标签名称
                        List<String> nameList = relationLabelMapper.getLabelNameByBuyerId(siteId,buyerId,labelType);
                        if (!nameList.contains(labelName)){
                            Integer i = relationLabelMapper.insertRelationLabel(siteId,buyerId,labelName,storeadminId+",",xCount,labelType);
                        }else {
                            Map<String,Object> idsMap = relationLabelMapper.getIdsByLabelName(siteId,buyerId,labelType,labelName);
                            String ids = String.valueOf(idsMap.get("storeadminIds"));
                            Integer yCount = Integer.parseInt(String.valueOf(idsMap.get("storeadminCount")));
                            Integer count = yCount + xCount;
                            String newIds = ids + storeadminId + ",";
                            Integer i = relationLabelMapper.updateCountAndIdsByLabelName(siteId,buyerId,labelName,newIds,count,labelType);
                        }
                    }
                }
            }
            return 0;
        }catch (Exception e){
            log.info("处理自定义标签和慢病标签异常:{}",e);
            return -1;
        }
    }

    /**
     *门店后台查询给该会员贴标签的所有店员
     * @param customLabel
     * siteId
     * id   关系表ID
     * @return
     */
    @Transactional
    public Map<String,Object> selectStoreadminForStore(Map<String,Object> customLabel) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(customLabel.get("siteId")));
            Integer id = Integer.parseInt(String.valueOf(customLabel.get("id")));
            String storeadminIds = relationLabelMapper.getStoreadminIdsById(siteId,id);
            List<Map<String,Object>> list = new ArrayList<>();
            if (!StringUtil.isEmpty(storeadminIds)){
                List<String> idList = stringToList(storeadminIds);
                for (String storeadminId : idList){
                    Map<String,Object> storeadminMap = new HashMap<>();
                    if(storeadminId.equals("0")){
                        storeadminMap.put("name","商户总部 管理员");
                        storeadminMap.put("storeadminId","0");
                    }else {
                        //如果是门店管理员添加的标签，显示XX门店管理员
                        Integer i = relationLabelMapper.getType(siteId,storeadminId);
                        if (i == 1){
                            String storeName = relationLabelMapper.getStoreName(siteId,storeadminId);
                            storeadminMap.put("name",storeName + " 管理员");
                            storeadminMap.put("storeadminId",storeadminId);
                        }else if(i == 2){
                            storeadminMap = relationLabelMapper.getStoreadminNameAndMobile(siteId,Integer.parseInt(storeadminId));
                        }
                    }
                    list.add(storeadminMap);
                }
            }
            map.put("storeadminList", list);
            map.put("msg", "查询成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("门店后台标签查询失败:{}",e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *门店后台删除给该会员贴标签的店员
     * @param customLabel
     * siteId
     * id  标签ID
     * storeadminIds
     * @return
     */
    @Transactional
    public Map<String,Object> updateStoreadminForStore(Map<String,Object> customLabel) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(customLabel.get("siteId")));
            Integer storeadminId = Integer.parseInt(String.valueOf(customLabel.get("storeadminIds")));
            Integer id = Integer.parseInt(String.valueOf(customLabel.get("id")));

            Map<String,Object> storeadminIdsMap = relationLabelMapper.getStoreadminIdsByIdAndCount(siteId,id);
            String storeadminIds = String.valueOf(storeadminIdsMap.get("storeadminIds"));
            List<String> oldstoreadminList = stringToList(storeadminIds);
            String storeadminIdsStr = "";
            for (String adminId : oldstoreadminList){
                if (storeadminId != Integer.parseInt(adminId)){
                    storeadminIdsStr += adminId + ",";
                }
            }

            Integer oldCount = Integer.parseInt(String.valueOf(storeadminIdsMap.get("storeadminCount")));
            Integer newCount = oldCount - 1;

            Integer i = relationLabelMapper.updateCountAndIds(siteId,id,storeadminIdsStr,newCount);
            map.put("msg", "删除成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("门店后台修改标签失败:{}",e);
            map.put("msg", "删除异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *根据标签名称查询会员的open_id
     * @param customLabel
     * siteId
     * labelName
     * @return
     */
    @Transactional
    public Map<String,Object> getOpenIdByLabelName(Map<String,Object> customLabel) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(customLabel.get("siteId")));
            List<Integer> buyerIds = relationLabelMapper.getBuyerIdByLabelName(customLabel);
            List<String> openids = relationLabelMapper.getOpernIdByBuyerId(siteId,buyerIds);

            map.put("openids", openids);
            map.put("msg", "查询成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}",e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *根据标签名称查询会员的memberIDs
     * @param customLabel
     * siteId
     * labelName
     * @return
     */
    @Transactional
    public Map<String,Object> getMemberIdByLabelName(Map<String,Object> customLabel) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(customLabel.get("siteId")));
            List<Integer> buyerIds = relationLabelMapper.getBuyerIdByLabelName(customLabel);
            List<Integer> memberIds = relationLabelMapper.getMemberIdBybuyerId(siteId,buyerIds);

            map.put("memberIds", memberIds);
            map.put("msg", "查询成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}",e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *商户后台批量添加会员标签
     * @param relationList
     * siteId
     * labelName
     * relationList 表格
     * @return
     */
    public Map<String,Object> getMemberIdByLabelName(List<Map<String,Object>> relationList,Integer siteId,String labelName,Integer xCount) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String,Object>> returnList = new ArrayList<>();
        try {
            Integer successCount = 0;
            Integer defeatedCount = 0;
            if(0 < relationList.size()){
                for (Map<String,Object> relationMap : relationList){
                    String mobile = String.valueOf(relationMap.get("mobile"));
                    Integer buyerId = relationLabelMapper.getBuyerIdByMobile(siteId,mobile);
                    if (!StringUtil.isEmpty(buyerId) || 0 != buyerId){  //如果能匹配到会员，
                        //判断这个会员是否存在标签中，存在，修改，不存在，就添加
                        Integer countBuyerId = relationLabelMapper.booleanBuyerId(siteId,buyerId,labelName);
                        if (countBuyerId == 0){
                            Integer i = relationLabelMapper.insertRelationLabel(siteId,buyerId,labelName,"",xCount,1);
                        }else {
                            Map<String,Object> idsAndCount = relationLabelMapper.getStoreadminIdsAndByLbelName(siteId,buyerId,labelName);
                            Integer relationCount = Integer.parseInt(String.valueOf(idsAndCount.get("storeadminCount")));
                            String ids = String.valueOf(idsAndCount.get("storeadminIds"));
                            List<String> idsList = stringToList(ids);
                            if (relationCount < idsList.size() + xCount){  //当店员数量 >= 店员的数量 + xCount 时，说明商户后台已经添加/修改过了，就不再修改数量
                                Integer count = idsList.size() + xCount;
//                                Integer i = relationLabelMapper.updateStoreadminCount(siteId,buyerId,labelName,count);
                            }
                        }
                        successCount++;
                    }else {
                        returnList.add(relationMap);//如果匹配不到会员，就打回去
                        defeatedCount++;
                    }
                }
            }
            map.put("successCount", successCount);
            map.put("defeatedCount", defeatedCount);
            map.put("returnList", returnList);
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}",e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *查询商户下所有的人群名称
     * @param params
     * @return
     */
    public Map<String,Object> getCrowdNameAll(Map<String,Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<String> nameList = memberLabelMapper.getCrowdNameAll(siteId);
            map.put("nameList", nameList);
            map.put("msg", "查询成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}",e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *根据人群名称查询人群下所有的OpenId
     * @param params
     * @return
     */
    public Map<String,Object> getCrowdOpenIdAll(Map<String,Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            String labelName = String.valueOf(params.get("labelName"));
            String storeadminIds = memberLabelMapper.getCrowdOpenIdAll(siteId,labelName);
            Map<String, Object> storeadminMap = JacksonUtils.json2map(storeadminIds);
            String ids = String.valueOf(storeadminMap.get("userIds"));
            List<String> idsList = stringToList(ids);
            //根据会员ID查询openId
            List<Map<String,Object>> openIds = memberLabelMapper.getOpenIdByMemberId(siteId,idsList);
            map.put("openIds", openIds);
            map.put("msg", "查询成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}",e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *查询商户下所有的标签名称
     * @param params
     * @return
     */
    public Map<String,Object> getCustomNameAll(Map<String,Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<String> nameList = relationLabelMapper.getCustomNameAll(siteId);
            map.put("nameList", nameList);
            map.put("msg", "查询成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}",e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *根据标签名称查询改标签下会员的openId
     * @param params
     * @return
     */
    public Map<String,Object> getCustomOpenIdAll(Map<String,Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            String labelName = String.valueOf(params.get("labelName"));
            List<Integer> idsList = relationLabelMapper.getCustomOpenIdAll(siteId,labelName);
            List<Map<String,Object>> openIds = relationLabelMapper.getOpenIdByBuyerId(siteId,idsList);
            map.put("openIds", openIds);
            map.put("msg", "查询成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}",e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *获取商户下所有会员的openId
     * @param params
     * @return
     */
    public Map<String,Object> getAllMemberOpenId(Map<String,Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<Map<String,Object>> openIds =  relationLabelMapper.getAllMemberOpenId(siteId);
            map.put("openIds", openIds);
            map.put("msg", "查询成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}",e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *批量导入
     * @param params
     * @return
     */
    @Transactional
    public Map<String,Object> addInsertRelation(Map<String,Object> params) {
        Map<String,Object> map = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            Integer xCount = Integer.parseInt(String.valueOf(params.get("xCount")));
            String mobile = String.valueOf(params.get("mobile"));
            Integer buyerId = relationLabelMapper.getBuyerIdByMobile(siteId,mobile);
            String labelName = String.valueOf(params.get("labelName"));

            //判断这个会员是否存在标签中，存在，修改，不存在，就添加
            Integer countBuyerId = relationLabelMapper.booleanBuyerId(siteId,buyerId,labelName);
            if (countBuyerId == 0){
                Integer i = relationLabelMapper.insertRelationLabel(siteId,buyerId,labelName,"0,",xCount,1);
                map.put("status",i);
                return  map;
            }else {
                Map<String,Object> idsAndCount = relationLabelMapper.getStoreadminIdsAndByLbelName(siteId,buyerId,labelName);
                Integer relationCount = Integer.parseInt(String.valueOf(idsAndCount.get("storeadminCount")));
                String ids = String.valueOf(idsAndCount.get("storeadminIds"));
                List<String> idsList = stringToList(ids);
                Integer i = 1;
                Integer j = 0;
                if (!idsList.contains("0")){  //0作为商户总部添加标签的标志，存在0，则说明商户后台已经添加过，不存在0，说明商户后台还没有添加过
                    Integer count = idsList.size() + xCount;
                    ids += "0,";
                    i = relationLabelMapper.updateStoreadminCount(siteId,buyerId,labelName,count,ids);
                }else {
                    j++;
                }
                map.put("status",i);
                map.put("repetition",j);
                return  map;
            }

        }catch (Exception e){
            map.put("status",-1);
            return  map;
        }
    }







    //获取登录者的信息
    public String getLoginData(Map<String,Object> customLabel){
        try{
            //获取登录者的身份
            Integer siteId = Integer.parseInt(String.valueOf(customLabel.get("siteId")));
            String creater;
            if (!StringUtil.isEmpty(customLabel.get("username"))){//如果是店员创建，获取店员姓名
                String username = String.valueOf(customLabel.get("username"));
                if ("admin".equals(username)){
                    creater = "总部";
                }else {
                    Map<String,Object> userMap = customLabelMapper.getClickName(siteId,username);
                    if (!userMap.containsKey("name") || StringUtil.isEmpty(userMap.get("name"))){
                        return "总部";
                    }
                    String user = userMap.get("name").toString();
                    String mobile = userMap.get("mobile").toString();
                    creater = user+"（"+ mobile +"）";
                }
            }else {//如果是商户创建，登录名为总部
                creater = "总部";
            }
            return creater;
        }catch (Exception e){
            log.info("查询异常:{}",e);
            return "总部";
        }

    }

    //String转List
    public List<String> stringToList(String str){
        List<String> list = new ArrayList<>();
        if (!StringUtil.isEmpty(str)){
            if (str.indexOf(",") != -1 || str.indexOf("，") != -1){
                String[] strings = str.split(",|，");
                for (int i = 0;i < strings.length;i++){
                    if (!StringUtil.isEmpty(strings[i])){
                        list.add(strings[i]);
                    }
                }
                return list;
            }else
                list.add(str);
            return list;
        }else {
            return list;
        }

    }

    //List<Map>排序
    public void listSort(List<Map<String, Object>> list) throws Exception {
        Collections.sort(list, new Comparator<Map<String, Object>>() {

            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer updateDate1 = Integer.parseInt(o1.get("storeadminCount").toString());
                Integer updateDate2 = Integer.parseInt(o2.get("storeadminCount").toString());
                return updateDate2.compareTo(updateDate1);
            }
        });
    }

    /**
     * 获取会员的慢性病标签和自定义标签
     */
    public Map<String,Object> getLable(int siteId,int buyerId,int storeadminId){


        Map<String,Object> labelMap = new HashMap<>();


        //自定义标签
        List<Map<String, Object>> relationLabelForCustomList = relationLabelMapper.getRelationLabelForCustom(siteId, buyerId);
        List<String> customList = getNameList(relationLabelForCustomList);

        //慢病标签
        List<Map<String, Object>> relationLabelForSlowList = relationLabelMapper.getRelationLabelForSlow(siteId, buyerId);
        List<String> slowList = getNameList(relationLabelForSlowList);


        //慢性病标签
        labelMap.put("slowLabels",slowList);

        //自定义标签
        labelMap.put("customLabels",customList);


        return labelMap;

    }


    private List<String> stringToList2(String str){

        if(StringUtil.isEmpty(str)){
            return new ArrayList<>();
        }

        return Arrays.asList(str.split(",|，"));
    }


    /**
     *初始化修改会员的首单时间
     * @return
     */
    public Map<String,Object> getFirstOrder(){
        Map<String,Object> map = new HashMap<>();
        try{
            List<Integer> list = staticsRecordMapper.getSiteId();//获取所有的商户
            for (Integer siteId : list){
                List<Integer> buyerIdList = customLabelMapper.getBuyerIdBySiteId(siteId);
                if (!StringUtil.isEmpty(buyerIdList) && 0 < buyerIdList.size()){
                    for (Integer buyerId : buyerIdList){
                        //获取每个会员的首单时间
                        String date = customLabelMapper.getFirstOrder(siteId,buyerId);
                        //修改b_member表会员的首单时间
                        Integer i = customLabelMapper.updateFirstOrderToMember(siteId,buyerId,date);
                    }
                }
            }
            map.put("status","OK");
            return map;
        }catch (Exception e){
            log.info("初始化修改会员的首单时间异常:{}",e);
            map.put("msg", "初始化修改会员的首单时间异常");
            map.put("status",-1);
            return map;
        }
    }
    /**
     *用户下单时，判断是不是首单，如果是，存储时间到b_member表中
     * @param siteId
     * @param buyerId
     * @return
     */
    @Transactional
    public Map<String,Object> saveFirstOrderTime(Integer siteId,Integer buyerId) {
        Map<String, Object> map = new HashMap<>();
        try {
            //判断该会员是否存在首单时间
            String time = customLabelMapper.selectOrderFirst(siteId, buyerId);
            if (StringUtil.isEmpty(time)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String orderTime = sdf.format(date);
                Integer i = customLabelMapper.updateFirstOrderToMember(siteId, buyerId, orderTime);
            }
            map.put("msg", "首单时间存储成功");
            map.put("status",0);
            return map;
        } catch (Exception e) {
            log.info("首单时间存储异常:{}", e);
            map.put("msg", "首单时间存储异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *初始化：会员慢病标签
     * @return
     */
    @Transactional
    public Map<String, Object> initMemberSlow() {
        Map<String, Object> map = new HashMap<>();
        try {
            List<Integer> list = staticsRecordMapper.getSiteId();//获取所有的商户
           /* List<Integer> list = new ArrayList<>();
            list.add(100190);*/
            for (Integer siteId : list){
                List<Map<String,Object>> memberData = relationLabelMapper.getMemberData(siteId);
                if (0 < memberData.size()){
                    for (Map<String,Object> customLabel : memberData){
                        Integer buyerId = Integer.parseInt(String.valueOf(customLabel.get("buyerId")));
                        String tag = String.valueOf(customLabel.get("tag"));
                        List<String> tagList = stringToList(tag);
                        if (0 < tagList.size()){
                            for (String labelName : tagList){
                                Integer i = relationLabelMapper.insertRelationLabel(siteId,buyerId,labelName,"",1,0);
                            }
                        }
                    }
                }
            }
            map.put("status","ok");
            return map;
        } catch (Exception e) {
            log.info("初始化：会员慢病标签异常:{}", e);
            map.put("status","error");
            return map;
        }
    }

    /**
     *获取该店员给指定会员添加的标签
     * @param params
     * @return
     */
    public Map<String,Object> getLabelByStoreAsminIdAndMemberId(Map<String,Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取会员的buyerId
            Integer buyerId = relationLabelMapper.getBuyerId(params);
            params.put("buyerId",buyerId);
            List<String> nameList = relationLabelMapper.getLabelCustomByStoreAsminIdAndMemberId(params);
            map.put("nameList", nameList);
            map.put("msg", "查询成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}",e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *获取该店员给指定会员添加的自定义标签
     * @param params
     * @return
     */
    public Map<String,Object> getAllLabelByCustom(Map<String,Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            List<Map<String,Object>> customAll = relationLabelMapper.getAllLabelByCustom(params);
            map.put("customAll", customAll);
            map.put("msg", "查询成功");
            map.put("status",0);
            return map;
        }catch (Exception e){
            log.info("查询异常:{}",e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     *修改指定会员的标签
     * @param params
     * @return
     */
    public Map<String,Object> updateMemberLabelByStore(Map<String,Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取会员的buyerId
            Integer buyerId = relationLabelMapper.getBuyerId(params);
            params.put("buyerId",buyerId);
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            Integer storeadminId = Integer.parseInt(String.valueOf(params.get("storeadminId")));
            if (!StringUtil.isEmpty(params.get("custom")) && String.valueOf(params.get("custom")).equals("     ")){
                params.put("custom","");
            }
            if (!params.containsKey("custom")){
                List<String> oldCustomList = relationLabelMapper.getStoreadminToMember(siteId,buyerId,storeadminId,1);
                params.put("custom",getString(oldCustomList));
            }else if (StringUtil.isEmpty(params.get("slow"))){
                List<String> oldSlowList = relationLabelMapper.getStoreadminToMember(siteId,buyerId,storeadminId,0);
                params.put("tag",getString(oldSlowList));
            }

            return updateLabelByMemberId(params);
        }catch (Exception e){
            log.info("查询异常:{}",e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    //list转String
    public String getString(List<String> list){
        if (list.size() > 0){
            if (list.size() == 1){
                return list.get(0);
            }else {
                String string = "";
                for (String str : list){
                    string += str + ",";
                }
                return string;
            }
        }else {
            return "";
        }
    }

    /**
     * 初始化：给商户后台贴的标签打上标记“0”
     * @return
     */
    public Map<String,Object> initTag(){
        Map<String, Object> map = new HashMap<>();
        try {
            //查询出人数与店员ID不匹配的记录
            List<Map<String,Object>> cuoJiluList = relationLabelMapper.getCuoJiluList();
            for (Map<String,Object> cuoMap : cuoJiluList){
                Integer count = Integer.parseInt(String.valueOf(cuoMap.get("count")));
                Integer id = Integer.parseInt(String.valueOf(cuoMap.get("id")));
                String strList = String.valueOf(cuoMap.get("storeadminIds"));
                List<String> storeAdminIdList = stringToList(strList);
                if (!storeAdminIdList.contains("0") && storeAdminIdList.size() < count){
                    //当店员集合中不包含商户后台的标记 并且 店员集合元素的个数小于总数时，修改店员集合
                    String str = strList + "0,";
                    relationLabelMapper.updateCuoJilu(id,str);
                }
            }
            map.put("msg", "初始化顺利完成");
            return map;
        }catch (Exception e){
            log.info("初始化异常:{}",e);
            map.put("msg", "初始化异常");
            return map;
        }
    }

    /**
     * 初始化：将慢病标签重新初始化到标签库
     */
    public Map<String,Object> initSlowLabel(){
        Map<String, Object> map = new HashMap<>();
        try {
            //获取所有含有慢病标签的会员
            List<Map<String,Object>> memberMapList = relationLabelMapper.getMemberMapList();
            for (Map<String,Object> memberMap : memberMapList){
                Integer siteId = Integer.parseInt(String.valueOf(memberMap.get("siteId")));
                Integer buyerId = Integer.parseInt(String.valueOf(memberMap.get("memberId")));
                String tag = String.valueOf(memberMap.get("tag"));
                List<String> tagList = stringToList(tag);

                //根据buyerId查询标签库中该会员的慢病标签
                List<String> labelList = relationLabelMapper.getlabelList(siteId,buyerId);
                for (String label : tagList){
                    if (labelList.contains(label)){
                        //店员ID集合+“0”，数量+1
                        relationLabelMapper.updateSlowCountAndIds(siteId,buyerId,label);
                    }else {
                        //直接添加
                        relationLabelMapper.insertSlowCountAndIds(siteId,buyerId,label);
                    }
                }
            }
            map.put("msg", "初始化顺利完成");
            return map;
        }catch (Exception e){
            log.info("初始化异常:{}",e);
            map.put("msg", "初始化异常");
            return map;
        }
    }

}
