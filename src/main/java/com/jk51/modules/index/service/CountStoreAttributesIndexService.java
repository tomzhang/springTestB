package com.jk51.modules.index.service;

import com.jk51.model.StoreAdmin;
import com.jk51.model.Stores;
import com.jk51.model.Target;
import com.jk51.model.packageEntity.StoreAdminIndex;
import com.jk51.model.packageEntity.TargetName;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yanglile
 * 创建日期: 2017-02-20
 * 修改记录:
 */
@Service
public class CountStoreAttributesIndexService {

    private static final Integer STORE_ATTRIBUTES_FLAGSHIPSTORE = 10;//门店属性为旗舰店10分，否则0分

    @Autowired
    private CountIndexService countIndexService;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private StoreAdminMapper storeAdminMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private TargetService targetService;


    /**
     * 设置店员所属门店指标分数
     * @param index
     */
    public void countStoreAttributesFlagshipstoreIndexOne(StoreAdminIndex index) {
        /*Target target = countIndexService.getTarget(index.getTargetList(), TargetName.STORE_ATTRIBUTES_FLAGSHIPSTORE_INDEX);
        int titleIndexNum = target.getInitial_value();*/

        if(index != null){
            index.setStoreAttributesFlagshipstoreIndex(getStoreAttributeScore(index.getStoreadmin_id(), index.getStore_id(), index.getSite_id()));
        }
    }

    /**
     * 设置店员所属门店订单量指标分数
     * @param storeAdminIndexList
     */
    public void countStoreAttributesOrderquantityIndex(List<StoreAdminIndex> storeAdminIndexList) {
        List<Map<String, Integer>> storeOrderQuantityList = tradesMapper.getStoreOrderQuantityList();

        //将门店按商家分类
        Map<Integer, List<Map<String, Integer>>> siteMap = getSiteStoreMap(storeOrderQuantityList);

        for (Map.Entry<Integer, List<Map<String, Integer>>> entry : siteMap.entrySet()) {
            List<Map<String, Integer>> list = entry.getValue();
            //按订单量进行排序
            //Collections.sort(list, new CampareServiceTotalNum());

            //20%+3
            int firstSize = list.size();
            int firstIndex = (int)Math.ceil(firstSize*0.2);
            List<Map<String, Integer>> first = list.subList(0,firstIndex);
            Map<String, Integer> firstMap = listMapToMap(first);
            //40%+2
            int secondIndex = (int)Math.ceil(firstSize*0.4);
            List<Map<String, Integer>> second = list.subList(firstIndex,secondIndex);
            Map<String, Integer> secondMap = listMapToMap(second);
            //60%+1
            int thirdIndex = (int)Math.ceil(firstSize*0.6);
            List<Map<String, Integer>> third = list.subList(secondIndex,thirdIndex);
            Map<String, Integer> thirdMap = listMapToMap(third);
            //80%+0.5
            int fourthIndex = (int)Math.ceil(firstSize*0.8);
            List<Map<String, Integer>> fourth = list.subList(thirdIndex,fourthIndex);
            Map<String, Integer> fourthMap = listMapToMap(fourth);

            for(StoreAdminIndex smi : storeAdminIndexList){

                //获取指标参数
                smi.setTargetList(targetService.getTargetBySiteId(100002));
                Target target = countIndexService.getTarget(smi.getTargetList(), TargetName.STORE_ATTRIBUTES_ORDERQUANTITY_INDEX);
                int initialValue = target.getInitial_value();
                if(firstMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesOrderquantityIndex(initialValue + 3);
                }else if(secondMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesOrderquantityIndex(initialValue + 2);
                }else if(thirdMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesOrderquantityIndex(initialValue + 1);
                }else if(fourthMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesOrderquantityIndex(initialValue + 0.5);
                }else{
                    smi.setStoreAttributesOrderquantityIndex(initialValue);
                }
            }
        }
    }


    /**
     * 设置店员所属门店店员数量指标分数
     * @param storeAdminIndexList
     */
    public void countStoreAttributesClerkquantityIndex(List<StoreAdminIndex> storeAdminIndexList) {
        List<Map<String, Integer>> storeClerkQuantityList = storeAdminMapper.getStoreClerkQuantityList();

        //将门店按商家分类
        Map<Integer, List<Map<String, Integer>>> siteMap = getSiteStoreMap(storeClerkQuantityList);

        for (Map.Entry<Integer, List<Map<String, Integer>>> entry : siteMap.entrySet()) {
            List<Map<String, Integer>> list = entry.getValue();

            //20%+3
            int firstSize = list.size();
            int firstIndex = (int)Math.ceil(firstSize*0.2);
            List<Map<String, Integer>> first = list.subList(0,firstIndex);
            Map<String, Integer> firstMap = listMapToMap(first);
            //40%+2
            int secondIndex = (int)Math.ceil(firstSize*0.4);
            List<Map<String, Integer>> second = list.subList(firstIndex,secondIndex);
            Map<String, Integer> secondMap = listMapToMap(second);
            //60%+1
            int thirdIndex = (int)Math.ceil(firstSize*0.6);
            List<Map<String, Integer>> third = list.subList(secondIndex,thirdIndex);
            Map<String, Integer> thirdMap = listMapToMap(third);
            //80%+0.5
            int fourthIndex = (int)Math.ceil(firstSize*0.8);
            List<Map<String, Integer>> fourth = list.subList(thirdIndex,fourthIndex);
            Map<String, Integer> fourthMap = listMapToMap(fourth);

            for(StoreAdminIndex smi : storeAdminIndexList){
                Target target = countIndexService.getTarget(smi.getTargetList(), TargetName.STORE_ATTRIBUTES_CLERK_QUANTITY_INDEX);
                int initialValue = target.getInitial_value();
                if(firstMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesClerkQuantityIndex(initialValue + 3);
                }else if(secondMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesClerkQuantityIndex(initialValue + 2);
                }else if(thirdMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesClerkQuantityIndex(initialValue + 1);
                }else if(fourthMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesClerkQuantityIndex(initialValue + 0.5);
                }else{
                    smi.setStoreAttributesClerkQuantityIndex(initialValue);
                }
            }
        }
    }







    /**
     * 根据店员门店属性是否为 旗舰店 设置指标分
     * @param index
     * @param storeFlagshipStoreMap
     */
    public void setStoreAttributesFlagshipstoreIndex(StoreAdminIndex index, Map<String, Integer> storeFlagshipStoreMap) {
        if(storeFlagshipStoreMap.containsKey(String.valueOf(index.getSite_id()) + String.valueOf(index.getStore_id()))){
            index.setStoreAttributesFlagshipstoreIndex(STORE_ATTRIBUTES_FLAGSHIPSTORE);
        }else{
            index.setStoreAttributesFlagshipstoreIndex(0);
        }
    }

    /**
     * 获取各门店所处排行
     * @param storeQuantityList 按quantity有序的list
     * @return
     */
    public Map<String, Map<String, Integer>> getStoreAttributesQuantityIndex(List<Map<String, Integer>> storeQuantityList){
        Map<String, Map<String, Integer>> result = new HashMap<String, Map<String, Integer>>();

        //将门店按商家分类
        Map<Integer, List<Map<String, Integer>>> siteMap = getSiteStoreMap(storeQuantityList);

        Map<String, Integer> firstMap = new HashMap<String, Integer>();//存放的 各商家的各门店 都是前20%
        Map<String, Integer> secondMap = new HashMap<String, Integer>();//存放的 各商家的各门店 都是前40%
        Map<String, Integer> thirdMap = new HashMap<String, Integer>();//存放的 各商家的各门店 都是前60%
        Map<String, Integer> fourthMap = new HashMap<String, Integer>();//存放的 各商家的各门店 都是前80%
        for (Map.Entry<Integer, List<Map<String, Integer>>> entry : siteMap.entrySet()) {

            List<Map<String, Integer>> list = entry.getValue();

            //20%+3
            int firstSize = list.size();
            int firstIndex = (int)Math.ceil(firstSize*0.2);
            List<Map<String, Integer>> first = list.subList(0,firstIndex);
            Map<String, Integer> map1 = listMapToMap(first);
            firstMap.putAll(map1);

            //40%+2
            int secondIndex = (int)Math.ceil(firstSize*0.4);
            List<Map<String, Integer>> second = list.subList(firstIndex,secondIndex);
            Map<String, Integer> map2 = listMapToMap(second);
            secondMap.putAll(map2);

            //60%+1
            int thirdIndex = (int)Math.ceil(firstSize*0.6);
            List<Map<String, Integer>> third = list.subList(secondIndex,thirdIndex);
            Map<String, Integer> map3 = listMapToMap(third);
            thirdMap.putAll(map3);

            //80%+0.5
            int fourthIndex = (int)Math.ceil(firstSize*0.8);
            List<Map<String, Integer>> fourth = list.subList(thirdIndex,fourthIndex);
            Map<String, Integer> map4 = listMapToMap(fourth);
            fourthMap.putAll(map4);
        }

        result.put("firstMap", firstMap);
        result.put("secondMap", secondMap);
        result.put("thirdMap", thirdMap);
        result.put("fourthMap", fourthMap);
        return result;
    }

    /**
     * 根据店员门店所处商家的排行等级为店员设置指标分
     * @param smi
     * @param storeQuantityLevelMap
     */
    public void setStoreAttributesQuantityIndex(StoreAdminIndex smi, Map<String, Map<String, Integer>> storeQuantityLevelMap, String targetName){
        if(smi!=null && storeQuantityLevelMap!=null){
            Map<String, Integer> firstMap = storeQuantityLevelMap.get("firstMap");
            Map<String, Integer> secondMap = storeQuantityLevelMap.get("secondMap");
            Map<String, Integer> thirdMap = storeQuantityLevelMap.get("thirdMap");
            Map<String, Integer> fourthMap = storeQuantityLevelMap.get("fourthMap");

            /*Target target = countIndexService.getTarget(smi.getTargetList(), targetName);
            int initialValue = target.getInitial_value();*/

            if(TargetName.STORE_ATTRIBUTES_ORDERQUANTITY_INDEX.equals(targetName)){
                if(firstMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesOrderquantityIndex(smi.getStoreAttributesOrderquantityIndex() + 3);
                }else if(secondMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesOrderquantityIndex(smi.getStoreAttributesOrderquantityIndex() + 2);
                }else if(thirdMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesOrderquantityIndex(smi.getStoreAttributesOrderquantityIndex() + 1);
                }else if(fourthMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesOrderquantityIndex(smi.getStoreAttributesOrderquantityIndex() + 0.5);
                }else{
                    //smi.setStoreAttributesOrderquantityIndex(initialValue);
                }
            }else if(TargetName.STORE_ATTRIBUTES_CLERK_QUANTITY_INDEX.equals(targetName)){
                if(firstMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesClerkQuantityIndex(smi.getStoreAttributesClerkQuantityIndex() + 3);
                }else if(secondMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesClerkQuantityIndex(smi.getStoreAttributesClerkQuantityIndex() + 2);
                }else if(thirdMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesClerkQuantityIndex(smi.getStoreAttributesClerkQuantityIndex() + 1);
                }else if(fourthMap.containsKey(String.valueOf(smi.getSite_id()) + String.valueOf(smi.getStore_id()))){
                    smi.setStoreAttributesClerkQuantityIndex(smi.getStoreAttributesClerkQuantityIndex() + 0.5);
                }else{
                    //smi.setStoreAttributesClerkQuantityIndex(initialValue);
                }
            }
        }
    }







    /**
     * 获取店员所属门店指标分数
     * 门店属性-旗舰店:按是否为旗舰店计算分值，是为10，不是为0；is_qjd 旗舰店(1 是 0 否)
     * @param storeAdminId 门店员工ID
     * @param storeId 门店ID
     * @param siteId 商家ID
     * @return
     */
    public int getStoreAttributeScore(int storeAdminId, int storeId, int siteId){
        //可以先根据员工ID和商家ID查出员工，在根据员工信息得到门店ID
        Stores stores = storesMapper.getStore(storeId, siteId);
        if(stores!=null && stores.getIs_qjd()==1){
            return STORE_ATTRIBUTES_FLAGSHIPSTORE;
        }else{
            return 0;
        }
    }

    /**
     * 根据发送者ID和商家ID判断发送者类型：true:顾客  false:店员
     * @param sender
     * @return
     */
    public Boolean isCustomer(String sender, String siteId) {
        //查询员工表
        List<StoreAdmin> storeAdminList = storeAdminMapper.getStoreAdminList(sender, siteId);
        if(CollectionUtils.isEmpty(storeAdminList)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 将门店按商家分类
     */
    private Map<Integer, List<Map<String, Integer>>> getSiteStoreMap(List<Map<String, Integer>> storeList){
        Map<Integer, List<Map<String, Integer>>> siteStoreMap = new HashMap<Integer, List<Map<String, Integer>>>();
        if(storeList!=null && !storeList.isEmpty()){
            for(Map<String, Integer> sai : storeList){
                if(siteStoreMap.get(sai.get("site_id"))!=null && !siteStoreMap.get(sai.get("site_id")).isEmpty()){
                    List<Map<String, Integer>> sdiList = siteStoreMap.get(sai.get("site_id"));
                    sdiList.add(sai);
                    siteStoreMap.put(sai.get("site_id"), sdiList);
                }else{
                    List<Map<String, Integer>> sdiList = new ArrayList<Map<String, Integer>>();
                    sdiList.add(sai);
                    siteStoreMap.put(sai.get("site_id"), sdiList);
                }
            }
        }
        return siteStoreMap;
    }

    /**
     * map key:site_id + store_id
     * @param listMap
     * @return
     */
    public Map<String, Integer> listMapToMap(List<Map<String, Integer>> listMap){
        Map<String, Integer> resultMap = new HashMap<String, Integer>();
        for(Map<String, Integer> lm : listMap){
            resultMap.put(String.valueOf(lm.get("site_id")) + String.valueOf(lm.get("store_id")), lm.get("quantity"));
        }
        return resultMap;
    }
}
