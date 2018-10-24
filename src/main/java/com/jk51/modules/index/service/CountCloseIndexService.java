package com.jk51.modules.index.service;

import com.jk51.commons.date.DateUtils;
import com.jk51.model.CloseIndexRecode;
import com.jk51.model.Target;
import com.jk51.model.packageEntity.StoreAdminCloseIndex;
import com.jk51.model.packageEntity.StoreAdminIndex;
import com.jk51.model.packageEntity.TargetName;
import com.jk51.modules.im.mapper.ImRecodeMapper;
import com.jk51.modules.index.mapper.CloseIndexRecodeMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-20
 * 修改记录:
 *
 * 与用户的历史数据指标
 */
@Service
public class CountCloseIndexService {

    @Autowired
    private ImRecodeMapper imRecodeMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private CountIndexService countIndexService;
    @Autowired
    private CloseIndexRecodeMapper closeIndexRecodeMapper;
    @Autowired
    private TargetService targetService;


    public void countCloseIndex(List<StoreAdminIndex> storeAdminIndexList){

        //获取所有店员的用户名
        List<String> userNameList = getUserNameList(storeAdminIndexList);

        Date now = new Date();
        Date before = DateUtils.getBeforeOrAfterDate(now,-90);

        //查询90聊天记录，封装成StoreAdminCloseIndex
        List<StoreAdminCloseIndex> iMRecodeList = imRecodeMapper.findStoreAdminCloseIndex(now,before,userNameList);

        //查询90天注册记录，封装成StoreAdminCloseIndex
        List<StoreAdminCloseIndex> registerList = memberMapper.findStoreAdminCloseIndex(now,before);

        //查询90天下单数量，封装成StoreAdminCloseIndex
        List<StoreAdminCloseIndex> tradesList = tradesMapper.findStoreAdminCloseIndex(now,before);

        //统计次数
        Map<StoreAdminCloseIndex,Integer> times = new HashMap<StoreAdminCloseIndex,Integer>();
        times = countTimes(times,iMRecodeList);
        times = countTimes(times,registerList);
        times = countTimes(times,tradesList);


        //获取次数
        List<StoreAdminCloseIndex> storeAdminCloseIndexList = setTimes(times);

        //分商家
        Map<Integer,List<StoreAdminCloseIndex>> siteMap = siteMap(storeAdminCloseIndexList);

        //分商家排序，計分
        sortAndSetIndex(siteMap,storeAdminIndexList,false);


        //下单数量计分
        List<StoreAdminCloseIndex> orderList = setOrderNumIndex(tradesList,storeAdminIndexList);

        //封装成CloseIndexRecode
        List<CloseIndexRecode> recodeList = convertToCloseIndexRecode(storeAdminCloseIndexList);

        //保存交集，下单数量指标
        saveCloseIndex(recodeList);
    }

    /**
     *分商家排序，計分
     * */
     private void sortAndSetIndex(Map<Integer,List<StoreAdminCloseIndex>> siteMap,List<StoreAdminIndex> storeAdminIndexList,boolean isOrderNumIndex){

         if(siteMap==null || siteMap.isEmpty()){
            return;
         }

         for(Map.Entry<Integer,List<StoreAdminCloseIndex>> entry:siteMap.entrySet()){

             int site_id = entry.getKey();
             List<StoreAdminCloseIndex> storeAdminCloseIndexList = entry.getValue();

             //初始化
             initCloseIndex(site_id,storeAdminCloseIndexList);

             //排序
             Collections.sort(storeAdminCloseIndexList,new ComparatorOrderNumTimes());

             //計分
             ladder(storeAdminCloseIndexList,storeAdminIndexList,isOrderNumIndex);
         }
     }

    //初始化店员下单量指标、与用户的历史数据（交集指标）
    private void initCloseIndex(int site_id, List<StoreAdminCloseIndex> storeAdminCloseIndexList){

        List<Target> targetList =  targetService.getTargetBySiteId(100002);
        Target intersectionIndexTarget = countIndexService.getTarget(targetList, TargetName.INTERSECTION_INDEX);
        Target orderNumIndexTarget = countIndexService.getTarget(targetList, TargetName.ORDER_NUM_INDEX);

        if(storeAdminCloseIndexList!=null && !storeAdminCloseIndexList.isEmpty()){
            for(StoreAdminCloseIndex saci:storeAdminCloseIndexList){
                saci.setIndex(intersectionIndexTarget.getInitial_value());
                saci.setOrderNumIndex(orderNumIndexTarget.getInitial_value());
            }
        }


    }

    /**
     * 分商家
     * */
    private Map<Integer,List<StoreAdminCloseIndex>> siteMap(List<StoreAdminCloseIndex> storeAdminCloseIndexList){

        Map<Integer,List<StoreAdminCloseIndex>> siteMap = new HashMap<Integer,List<StoreAdminCloseIndex>>();
        if(storeAdminCloseIndexList!=null && !storeAdminCloseIndexList.isEmpty()){
            for(StoreAdminCloseIndex saci:storeAdminCloseIndexList){
                List<StoreAdminCloseIndex> list = siteMap.get(saci.getSite_id());
                if(list==null){
                    list = new ArrayList<StoreAdminCloseIndex>();
                    list.add(saci);
                    siteMap.put(saci.getSite_id(),list);
                }else{
                    list.add(saci);
                }
            }
        }
       return  siteMap;
    }

    /**
     *封装成CloseIndexRecode
     * */
    private List<CloseIndexRecode> convertToCloseIndexRecode(List<StoreAdminCloseIndex> closeIndexList){

        List<CloseIndexRecode> closeIndexRecodeList = new ArrayList<CloseIndexRecode>();
        for(StoreAdminCloseIndex closeIndex :closeIndexList){
            CloseIndexRecode recode = new CloseIndexRecode();
            recode.setSite_id(closeIndex.getSite_id());
            recode.setStoreadmin_id(closeIndex.getStoreadmin_id());
            recode.setCustomer_user_name(closeIndex.getCustomer_user_name());
            recode.setHistory_index(closeIndex.getIndex());
            recode.setOrder_index(closeIndex.getOrderNumIndex());
            closeIndexRecodeList.add(recode);
        }
        return closeIndexRecodeList;
    }
    /**
     *保存交集，下单数量指标
     * */
    @Transactional
    @CacheEvict(value="closeIndexRecode",allEntries=true)
    private void saveCloseIndex(List<CloseIndexRecode> closeIndexList){
        closeIndexRecodeMapper.batchInsert(closeIndexList);
    }



    /**
     *下单数量计分
     * */
    private List<StoreAdminCloseIndex> setOrderNumIndex( List<StoreAdminCloseIndex> tradesList,List<StoreAdminIndex> storeAdminIndexList){

        Map<StoreAdminCloseIndex,Integer> orderNumTimes = new HashMap<StoreAdminCloseIndex,Integer>();
        orderNumTimes = countTimes(orderNumTimes,tradesList);

        //获取下单次数
        List<StoreAdminCloseIndex> storeAdminCloseIndexList = setTimes(orderNumTimes);

        //分商家
        Map<Integer,List<StoreAdminCloseIndex>> siteMap = siteMap(storeAdminCloseIndexList);

        //分商家排序，計分
        sortAndSetIndex(siteMap,storeAdminIndexList,true);

        return storeAdminCloseIndexList;

    }



    /**
     *分阶梯
     *
     * */
    private void ladder(List<StoreAdminCloseIndex> storeAdminCloseIndexList,List<StoreAdminIndex> storeAdminIndexList,boolean isOrderNumIndex){

        //20%+3
        int size = storeAdminCloseIndexList.size();
        int firstIndex = (int)Math.ceil(size*0.2);
        List<StoreAdminCloseIndex> first = storeAdminCloseIndexList.subList(0,firstIndex);
        if(isOrderNumIndex){
            setOrderNumIndex(first,3,storeAdminIndexList);
        }else{
            setIndex(first,3,storeAdminIndexList);
        }



        //40%+2
        //int secondSize = firstSize-firstIndex;
        int secondIndex = (int)Math.ceil(size*0.4);
        List<StoreAdminCloseIndex> second = storeAdminCloseIndexList.subList(firstIndex,secondIndex);
        if(isOrderNumIndex){
            setOrderNumIndex(second,2,storeAdminIndexList);
        }else{
            setIndex(second,2,storeAdminIndexList);
        }


        //60%+1
        //int thirdSize = secondSize - secondIndex;
        int thirdIndex = (int)Math.ceil(size*0.6);
        List<StoreAdminCloseIndex> third = storeAdminCloseIndexList.subList(secondIndex,thirdIndex);
        if(isOrderNumIndex){
            setOrderNumIndex(third,1,storeAdminIndexList);
        }else{
            setIndex(third,1,storeAdminIndexList);
        }


        //80%+0.5
        //int fourthSize = thirdSize - thirdIndex;
        int fourthIndex = (int)Math.ceil(size*0.8);
        List<StoreAdminCloseIndex> fourth = storeAdminCloseIndexList.subList(thirdIndex,fourthIndex);
        if(isOrderNumIndex){
            setOrderNumIndex(fourth,0.5,storeAdminIndexList);
        }else{
            setIndex(fourth,0.5,storeAdminIndexList);
        }

    }

    private void setOrderNumIndex(List<StoreAdminCloseIndex> indexList,double num,List<StoreAdminIndex> storeAdminIndexList){

        for(StoreAdminCloseIndex index:indexList){
           // int initValue = getInitValue(index,storeAdminIndexList);
           // index.setIndex(initValue);
            index.setOrderNumIndex(index.getIndex()+num);
        }
    }
    //设置指标分
    private void setIndex(List<StoreAdminCloseIndex> indexList,double num,List<StoreAdminIndex> storeAdminIndexList){


        for(StoreAdminCloseIndex index:indexList){
            //int initValue = getInitValue(index,storeAdminIndexList);
            //index.setIndex(initValue);
            index.setIndex(index.getIndex()+num);
        }
    }
/*
    //获取初始化值
    private int getInitValue1(StoreAdminCloseIndex index,List<StoreAdminIndex> storeAdminIndexList){

        int initValue = 0;
        for(StoreAdminIndex storeAdminIndex:storeAdminIndexList){

           //storeAdminIndex.setTargetList(targetService.getTargetBySiteId(100002));
            if(storeAdminIndex.getSite_id()==index.getSite_id()&&storeAdminIndex.getStoreadmin_id()==index.getStoreadmin_id()){
                List<Target> targetList = storeAdminIndex.getTargetList();
                Target target = countIndexService.getTarget(targetList, TargetName.INTERSECTION_INDEX);
                initValue = target.getInitial_value();
                break;
            }
        }
        return initValue;
    }*/

    //获取次数
    private List<StoreAdminCloseIndex> setTimes(Map<StoreAdminCloseIndex,Integer> imTimes){

        List<StoreAdminCloseIndex> storeAdminCloseIndexList = new LinkedList<StoreAdminCloseIndex>();
        if(!imTimes.isEmpty()){
            for(Map.Entry<StoreAdminCloseIndex,Integer> entry  :imTimes.entrySet()){
                StoreAdminCloseIndex index = entry.getKey();
                int times = entry.getValue();
                index.setTimes(times);
                storeAdminCloseIndexList.add(index);
            }
        }

        return storeAdminCloseIndexList;
    }



    //统计次数
    private Map<StoreAdminCloseIndex,Integer> countTimes(Map<StoreAdminCloseIndex,Integer> timesMap,List<StoreAdminCloseIndex> iMRecodeList){


        if(!iMRecodeList.isEmpty()){
            for(StoreAdminCloseIndex index:iMRecodeList){
                if(timesMap.get(index)!=null){
                    timesMap.put(index,timesMap.get(index)+1);
                }else{
                    timesMap.put(index,1);
                }
            }
        }
        return timesMap;

    }


    /**
     *获取店员用户名List
     * */
    private List<String> getUserNameList(List<StoreAdminIndex> storeAdminIndexList){

        List<String> userNameList = new ArrayList<String>();
        for(StoreAdminIndex index:storeAdminIndexList){
            userNameList.add(index.getUser_name());
        }
        return userNameList;
    }

    //实现Comparator用于Collections排序
    private final class ComparatorImTimes implements Comparator<StoreAdminCloseIndex>{

        @Override
        public int compare(StoreAdminCloseIndex o1, StoreAdminCloseIndex o2) {

            int numa = o1.getTimes();
            int numb = o1.getTimes();

            if(numa < numb){
                return 1;
            }else if(numa == numb){
                return 0;
            }else {
                return -1;
            }
        }
    }

    //实现Comparator用于Collections排序
    private final class ComparatorOrderNumTimes implements Comparator<StoreAdminCloseIndex>{

        @Override
        public int compare(StoreAdminCloseIndex o1, StoreAdminCloseIndex o2) {

            int numa = o1.getOrderTimes();
            int numb = o1.getOrderTimes();

            if(numa < numb){
                return 1;
            }else if(numa == numb){
                return 0;
            }else {
                return -1;
            }
        }
    }
}
