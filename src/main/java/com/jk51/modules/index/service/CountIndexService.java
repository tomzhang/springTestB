package com.jk51.modules.index.service;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.*;
import com.jk51.model.packageEntity.FirstWeightName;
import com.jk51.model.packageEntity.StoreAdminIndex;
import com.jk51.model.packageEntity.TargetIndexValue;
import com.jk51.model.packageEntity.TargetName;
import com.jk51.modules.im.mapper.ImRecodeMapper;
import com.jk51.modules.im.mapper.RaceAnswerRecodeMapper;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.index.mapper.TargetRecodeMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gaojie on 2017/2/15.
 */
@Service
public class CountIndexService {

    Logger logger = LoggerFactory.getLogger(CountIndexService.class);
    @Autowired
    private TargetService targetService;
    @Autowired
    private StoreAdminMapper storeAdminMapper;
    @Autowired
    private CountServiceTotalIndexService countServiceTotalIndexService;
    @Autowired
    private CountTitleIndexService countTitleIndexService;
    @Autowired
    private CountSatisfactionIndexService countSatisfactionIndexService;
    @Autowired
    private CountHistoryAnswerSpeedIndexService countHistoryAnswerSpeedIndexService;
    @Autowired
    private TargetRecodeMapper targetRecodeMapper;
    @Autowired
    private CountBusyIndexService countBusyIndexService;
    @Autowired
    private CountStoreAttributesIndexService countStoreAttributesIndexService;
    @Autowired
    private CountCloseIndexService countCloseIndexService;
    @Autowired
    private IndexInitService indexInitService;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private FirstWeightService firstWeightService;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private RaceAnswerRecodeMapper raceAnswerRecodeMapper;
    @Autowired
    private ImRecodeMapper imRecodeMapper;

    /**
     *计算指标分
     *
     * */
    public void countIndex(){

        List<StoreAdminIndex> storeAdminIndexList = storeAdminMapper.getAllStoreAdminIndexList();
        if(storeAdminIndexList==null || storeAdminIndexList.isEmpty()){
            return;
        }

        //获取所有商家状态为未删除且启用的旗舰店
        List<Map<String, Integer>> storeFlagshipStoreList = storesMapper.getStoresIsQJDList();
        //所有商家旗舰店map key：site_id + store_id
        Map<String, Integer> storeFlagshipStoreMap = countStoreAttributesIndexService.listMapToMap(storeFlagshipStoreList);

        //设置店员所属门店订单量指标分数
        List<Map<String, Integer>> storeOrderQuantityList = tradesMapper.getStoreOrderQuantityList();
        //获取各门店店员数量
        List<Map<String, Integer>> storeClerkQuantityList = storeAdminMapper.getStoreClerkQuantityList();
        //获取各门店所处排行
        Map<String, Map<String, Integer>> storeOrderQuantityLevelMap = countStoreAttributesIndexService.getStoreAttributesQuantityIndex(storeOrderQuantityList);
        Map<String, Map<String, Integer>> storeClerkQuantityLevelMap = countStoreAttributesIndexService.getStoreAttributesQuantityIndex(storeClerkQuantityList);

        //查询所有商家的一级权重
        Map<Integer,List<FirstWeight>> allFirstWeight = firstWeightService.getAllFirstWeight();
        if(StringUtil.isEmpty(allFirstWeight)){
            logger.error("商家一级权重未设置");
            return;
        }

        //查询所有商家的指标参数
        Map<Integer,List<Target>> allTarget = targetService.getAllTarget();
        if(StringUtil.isEmpty(allTarget)){
            logger.error("商家指标参数未设置");
            return;
        }

        //查询一天时间里，店员的抢答记录
        Map<String,List<RaceAnswerRecode>> raceAnswerRecodeMap = getRaceAnswerRecodeMap();

        //查询每个店员时间最大的指标记录数据
        Map<Integer,TargetRecode> targetRecodeMap = getTargetRecodeMap();

        //查询店员30天的服务人数
        Map<String,Integer> serviceTotal = getServiceTotal();

        //以店员的算法计算指标分数
        for(StoreAdminIndex index :storeAdminIndexList){

            //获取指标参数
            /*if(StringUtil.isEmpty(allTarget.get(index.getSite_id()))){
                logger.error("商家_"+index.getSite_id()+"_指标参数未设置");
                return;
            }*/
            index.setTargetList(allTarget.get(100002));

            //获取一级权重
           /* if(StringUtil.isEmpty(allFirstWeight.get(index.getSite_id()))){
                logger.error("商家_"+index.getSite_id()+"_一级权重未设置");
                return;
            }*/
            index.setFirstWeightList(allFirstWeight.get(100002));

            //初始化各指标值
            indexInitService.initIndex(index,targetRecodeMap);

            //满意度指标计算----------暂时没有评价的功能，没有数据查（先返回初始值）
            countSatisfactionIndexService.countSatisfactionIndexOne(index);
            //历史抢答速度指标
            countHistoryAnswerSpeedIndexService.countHistoryAnswerSpeedIndexServiceOne(index,raceAnswerRecodeMap);

            //历史回复速度指标
            countBusyIndexService.CountBusyIndexOnde(index,raceAnswerRecodeMap);


            countStoreAttributesIndexService.setStoreAttributesFlagshipstoreIndex(index, storeFlagshipStoreMap);


            //以店铺的算法计算指标分数
            countStoreAttributesIndexService.setStoreAttributesQuantityIndex(index, storeOrderQuantityLevelMap, TargetName.STORE_ATTRIBUTES_ORDERQUANTITY_INDEX);
            countStoreAttributesIndexService.setStoreAttributesQuantityIndex(index, storeClerkQuantityLevelMap, TargetName.STORE_ATTRIBUTES_CLERK_QUANTITY_INDEX);

        }


        //计算店员职称指标分
        countTitleIndexService.countTitleIndexOne(storeAdminIndexList);

        //以店铺的算法计算指标分数
        countServiceTotalIndexService.countServiceTotalIndex(storeAdminIndexList,serviceTotal);


        //保存亲密度指标(親密度存儲在b_close_index_recode中)
        countCloseIndexService.countCloseIndex(storeAdminIndexList);

        //参数权重计算各指标总分,//获取indexJson字符串
        setIndexAndIndexJson(storeAdminIndexList);

        //保存IndexJson/countIndex
        saveIndexJsonAndCountIndex(storeAdminIndexList);


    }


    //查询前30天店员的服务人数
    public Map<String,Integer> getServiceTotal() {

        Map<String,Integer> result = new HashMap<String,Integer>();
        Date now = new Date();
        Date beforeDate = DateUtils.getBeforeOrAfterDate(now,-30);
        List<Map<String,String>> list = imRecodeMapper.findIMRecodeByTiemScope2(now,beforeDate);
        if(StringUtil.isEmpty(result)){
            return result;
        }
        for(Map<String,String> map:list){
            result.put(map.get("sender"),Integer.valueOf(map.get("times")));
        }

        return result;
    }

    //查询一天时间里，店员的抢答记录
    public Map<String,List<RaceAnswerRecode>> getRaceAnswerRecodeMap() {

        Map<String,List<RaceAnswerRecode>> raceAnswerRecodeMap = new HashMap<String,List<RaceAnswerRecode>>();
        Date now = new Date();
        Date beforeTime = DateUtils.getBeforeOrAfterDate(now,-1);
        List<RaceAnswerRecode> raceAnswerRecodeList =  raceAnswerRecodeMapper.getRaceAnswerRecodeBytime(now,beforeTime);

        if(StringUtil.isEmpty(raceAnswerRecodeList)){
            return raceAnswerRecodeMap;
        }

        for(RaceAnswerRecode rr:raceAnswerRecodeList){

            List<RaceAnswerRecode> list = raceAnswerRecodeMap.get(rr.getReceiver());
            if(StringUtil.isEmpty(list)){
                List<RaceAnswerRecode> rlist = new ArrayList<RaceAnswerRecode>();
                rlist.add(rr);
                raceAnswerRecodeMap.put(rr.getReceiver(),rlist);

            }else{
                list.add(rr);
                raceAnswerRecodeMap.put(rr.getReceiver(),list);
            }
        }

        return raceAnswerRecodeMap;
    }


    //保存IndexJson/countIndex
    @CacheEvict(value="allIndex",allEntries = true)
    private void saveIndexJsonAndCountIndex(List<StoreAdminIndex> storeAdminIndexList){

        storeAdminMapper.batchUpdateCountIndex(storeAdminIndexList);
        targetRecodeMapper.batchInsertSelective(storeAdminIndexList);
    }

    //参数权重计算各指标总分
    private void countIndex(StoreAdminIndex sai){


        double historyAnswerSpeedIndex = sai.getHistoryAnswerSpeedIndex();
        double busyIndex = sai.getBusyIndex();
        double titleIndex = sai.getTitleIndex();
        double satisfactionIndex = sai.getSatisfactionIndex();
        double serviceTotalIndex = sai.getServiceTotalIndex();
        double flagshipstoreIndex = sai.getStoreAttributesFlagshipstoreIndex();
        double orderquantityIndex = sai.getStoreAttributesOrderquantityIndex();
        double clerkQuantityIndex = sai.getStoreAttributesClerkQuantityIndex();

        double historyAnswerSpeed_weight_value = getSecondWeigthValue(sai.getTargetList(),TargetName.HISTORY_ANSWER_SPEED_INDEX);
        double busy_weight_value = getSecondWeigthValue(sai.getTargetList(),TargetName.BUSY_INDEX);
        double title_index_weight_value = getSecondWeigthValue(sai.getTargetList(),TargetName.TITLE_INDEX);
        double satisfaction_weight_value = getSecondWeigthValue(sai.getTargetList(),TargetName.SATISFACTION_INDEX);
        double service_total_weight_value = getSecondWeigthValue(sai.getTargetList(),TargetName.SERVICE_TOTAL_INDEX);
        double flag_shipstore_weight_value = getSecondWeigthValue(sai.getTargetList(),TargetName.STORE_ATTRIBUTES_FLAGSHIPSTORE_INDEX);
        double order_quanityt_weight_value = getSecondWeigthValue(sai.getTargetList(),TargetName.STORE_ATTRIBUTES_ORDERQUANTITY_INDEX);
        double clerk_quantity_weight_value = getSecondWeigthValue(sai.getTargetList(),TargetName.STORE_ATTRIBUTES_CLERK_QUANTITY_INDEX);

        double pesponse_speed_weight_value = getFirstWeightValue(sai.getFirstWeightList(), FirstWeightName.RESPONSE_SPEED);
        double professional_service_weight_value = getFirstWeightValue(sai.getFirstWeightList(), FirstWeightName.PROFESSIONAL_AND_SERVICE);
        double service_continuity_weight_value = getFirstWeightValue(sai.getFirstWeightList(), FirstWeightName.SERVICE_CONTINUITY);

        double pesponse_speed_value =  (historyAnswerSpeedIndex * historyAnswerSpeed_weight_value + busyIndex * busy_weight_value) * pesponse_speed_weight_value;
        double professional_service_value =  (titleIndex * title_index_weight_value + satisfactionIndex * satisfaction_weight_value + serviceTotalIndex * service_total_weight_value) * professional_service_weight_value;
        double service_continuity_value =  (flagshipstoreIndex * flag_shipstore_weight_value + orderquantityIndex * order_quanityt_weight_value + clerkQuantityIndex * clerk_quantity_weight_value ) * service_continuity_weight_value;
        sai.setCountIndex(pesponse_speed_value+professional_service_value+service_continuity_value);
    }

    //获取一级权重值，如果没有设置返回 0
    public double getFirstWeightValue(List<FirstWeight> firstWeightList,String weightName){

        double weightValue = 0;
        if(firstWeightList==null && firstWeightList.isEmpty()){
            return weightValue;
        }

        for(FirstWeight fw:firstWeightList){
            if(fw.getWeight_name().equals(weightName)){
                weightValue = fw.getWeight_value();
            }
        }

        return weightValue;
    }

    //获取二级权重值，如果没有设置返回 0
    public double getSecondWeigthValue(List<Target> targetList,String targetName){
        double secondWeigthValue = 0;

        if(getTarget(targetList,targetName)==null){
            return secondWeigthValue;
        }

        return getTarget(targetList,targetName).getSecond_weigth_value();
    }

    //获取indexJson字符串
    private void getIndexJson(StoreAdminIndex sai){

        TargetIndexValue value = new TargetIndexValue();
        value.setHistoryAnswerSpeedIndex(sai.getHistoryAnswerSpeedIndex());
        value.setBusyIndex(sai.getBusyIndex());
        value.setIntersectionIndex(sai.getIntersectionIndex());
        value.setOrderNumIndex(sai.getOrderNumIndex());
        value.setSatisfactionIndex(sai.getSatisfactionIndex());
        value.setServiceTotalIndex(sai.getServiceTotalIndex());
        value.setStoreAttributesClerkQuantityIndex(sai.getStoreAttributesClerkQuantityIndex());
        value.setStoreAttributesFlagshipstoreIndex(sai.getStoreAttributesFlagshipstoreIndex());
        value.setTitleIndex(sai.getTitleIndex());
        value.setStoreAttributesOrderquantityIndex(sai.getStoreAttributesOrderquantityIndex());

        String str = "";
        try {
            str =  JacksonUtils.obj2json(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sai.setIndexJson(str);

    }


    /**
     *根据指标名称获取指标参数
     * */
    public Target getTarget(List<Target> targetList,String targetName){

        Target target = null;
        if(targetList!=null&&!targetList.isEmpty()){
            for(Target tg:targetList){
                if(tg.getTarget_name().equals(targetName)){
                    target = tg;
                }
            }
        }
        return target;
    }


    /**
     *根据店员ID和商家ID查询指标记录表中时间最大的一笔记录
     * */
    public TargetRecode getTargetRecode(StoreAdminIndex index) {

        TargetRecode targetRecode = null;
        List<TargetRecode> targetRecodeList = targetRecodeMapper.getTargetRecodeByStoreadminIdAndSiteId(index.getStoreadmin_id(),index.getSite_id());
        if(targetRecodeList!=null && !targetRecodeList.isEmpty()){
            targetRecode = targetRecodeList.get(0);
        }
        return targetRecode;
    }

    /**
     * 指标区间Stirng转Int
     *@return Map   key(max,min)
     * */
    public Map<String,Double> scoreParameterSectionMaxAndMin(String score_parameter_section) {

        Map<String,Double> result = new HashMap<String,Double>();
        if(StringUtil.isNumber(score_parameter_section)){
            return result;
        }
        Pattern pattern = Pattern.compile("^(\\d+)-(\\d+)$");
        Matcher matcher = pattern.matcher(score_parameter_section);
        if(matcher.matches()){
            String minStr = matcher.group(1);
            String maxStr = matcher.group(2);
            result.put("min", Double.parseDouble(minStr));
            result.put("max", Double.parseDouble(maxStr));
        }else{
            return result;
        }

        return result;


    }

    //查询每个店员时间最大的指标记录数据
    public Map<Integer,TargetRecode> getTargetRecodeMap() {
        Map<Integer,TargetRecode> targetRecodeMap = new HashMap<Integer,TargetRecode>();

        //查询每个店员时间最大的一笔指标记录
        List<TargetRecode> recodeList = targetRecodeMapper.getTargetRecodeforMaxCreatTime();
        if(StringUtil.isEmpty(recodeList)){
            return targetRecodeMap;
        }

        for(TargetRecode tr:recodeList){
            targetRecodeMap.put(tr.getTarget_record_id(),tr);
        }

        return targetRecodeMap;
    }

    //参数权重计算各指标总分,//获取indexJson字符串
    public void setIndexAndIndexJson(List<StoreAdminIndex> indexAndIndexJson) {

        if(StringUtil.isEmpty(indexAndIndexJson)){
            return;
        }

        for(StoreAdminIndex index:indexAndIndexJson){
            countIndex(index);
            getIndexJson(index);

        }
    }
}
